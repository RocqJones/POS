package com.extrainch.pos.ui.inventory;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.extrainch.pos.MainActivity;
import com.extrainch.pos.R;
import com.extrainch.pos.adapters.AdapterInventory;
import com.extrainch.pos.databinding.ActivityInventoryMainBinding;
import com.extrainch.pos.patterns.MySingleton;
import com.extrainch.pos.repository.DataInventoryMgt;
import com.extrainch.pos.ui.category.add.AddCategoryActivity;
import com.extrainch.pos.ui.products.add.AddProductActivity;
import com.extrainch.pos.ui.profile.addmerchant.AddMerchantActivity;
import com.extrainch.pos.utils.Constants;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InventoryActivity extends AppCompatActivity {

    private ActivityInventoryMainBinding binding;

    String token;
    String productId;
    String merchantId;
    String categoryId;
    String uid;

    public SharedPreferences preferences;
    public SharedPreferences.Editor editor;
    public String SHARED_PREF_NAME = "pos_pref";
    public String KEY_TOKEN = "Token";
    public String KEY_ID = "Id";
    public String MERCHANT_ID = "merchantId";
    public String CATEGORY_ARR = "categoryArr";
    public String PRODUCT_ARR = "productArr";

    final Calendar myCalender = Calendar.getInstance();
    String m, d;

    List<DataInventoryMgt> inventoryMgtData;
    private AdapterInventory adapterInventory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityInventoryMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // set up rv
        inventoryMgtData = new ArrayList<>();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(InventoryActivity.this);
        binding.inventoryRv.setLayoutManager(linearLayoutManager);

        binding.backBtn.setOnClickListener(v -> {
            Intent j = new Intent(InventoryActivity.this, MainActivity.class);
            j.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(j);
        });

        preferences = InventoryActivity.this.getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        token = preferences.getString(KEY_TOKEN, "Token");
        uid = preferences.getString(KEY_ID, "Id");

        if (preferences.contains("merchantId")) {
            merchantId = preferences.getString(MERCHANT_ID, "merchantId");
        } else {
            Intent mD = new Intent(InventoryActivity.this, AddMerchantActivity.class);
            startActivity(mD);
        }

        binding.fab.setOnClickListener(v -> createInventory());

        String url = Constants.BASE_URL + "Inventory/?MerchantId=" + merchantId;
        getInventoryDetails(url);
    }

    private void createInventory() {
        String url = Constants.BASE_URL + "Inventory/Create";
        final Dialog dialog = new Dialog(InventoryActivity.this);
        dialog.setContentView(R.layout.activity_add_inventory);
        dialog.setCancelable(false);

        Spinner product = dialog.findViewById(R.id.product);
        Spinner category = dialog.findViewById(R.id.category);
        Spinner unitOfMeasure = dialog.findViewById(R.id.uitOfMeasure);
        EditText unitPrice = dialog.findViewById(R.id.unitPrice);
        EditText reorderLevel = dialog.findViewById(R.id.reorderLevel);
        EditText purchaseDate = dialog.findViewById(R.id.purchaseDate);
        EditText expiryDate = dialog.findViewById(R.id.expiryDate);
        Button saveBtn = dialog.findViewById(R.id.saveBtn);

        // set spinners
        if (preferences.contains("productArr")) {
            String supplierArr = preferences.getString(PRODUCT_ARR, "productArr");

            String temp_str_p = supplierArr.replace("[", "").replace("]", "");
            String[] supplierT = temp_str_p.split(",");
            ArrayAdapter<String> adapterP = new ArrayAdapter<>(this,
                    android.R.layout.simple_dropdown_item_1line, supplierT);
            product.setAdapter(adapterP);

            String sp = product.getSelectedItem().toString().trim();
            String[] s = sp.split(":");
            if (s[0].isEmpty()) {
                Toast.makeText(this, "To create order you must maintain an associate product!", Toast.LENGTH_SHORT).show();
                Intent p = new Intent(this, AddProductActivity.class);
                startActivity(p);
            } else {
                productId = s[0];
            }
        } else {
            Toast.makeText(this, "To create order you must maintain an associate product!", Toast.LENGTH_SHORT).show();
            Intent cT = new Intent(InventoryActivity.this, AddCategoryActivity.class);
            startActivity(cT);
        }

        if (preferences.contains("categoryArr")) {
            // categoryId = preferences.getString(CATEGORY_ID, "categoryId");
            String categoryArr = preferences.getString(CATEGORY_ARR, "categoryArr");

            String temp_str = categoryArr.replace("[", "").replace("]", "");
            String[] categoryT = temp_str.split(",");
            ArrayAdapter<String> adapter = new ArrayAdapter<>(InventoryActivity.this,
                    android.R.layout.simple_dropdown_item_1line, categoryT);
            category.setAdapter(adapter);

            String ct = category.getSelectedItem().toString().trim();
            String [] c = ct.split(":");
            if (c[0].isEmpty()) {
                Toast.makeText(this, "To create product you must maintain category", Toast.LENGTH_SHORT).show();
                Intent cT = new Intent(InventoryActivity.this, AddCategoryActivity.class);
                startActivity(cT);
            } else {
                categoryId = c[0];
            }
        } else {
            Toast.makeText(this, "To create product you must maintain category", Toast.LENGTH_SHORT).show();
            Intent cT = new Intent(InventoryActivity.this, AddCategoryActivity.class);
            startActivity(cT);
        }

        String[] unitsOfMeasure = {"Select...", "kgs", "gms", "Lt" , "ml"};
        ArrayAdapter<String> adapterUnit = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, unitsOfMeasure);
        unitOfMeasure.setAdapter(adapterUnit);

        // date
        purchaseDate.setOnClickListener(v -> {
            int mYear = myCalender.get(Calendar.YEAR);
            int mMonth = myCalender.get(Calendar.MONTH);
            int mDay = myCalender.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog datePickerDialog = new DatePickerDialog(InventoryActivity.this,
                    (view, year, month, dayOfMonth) -> {
                        myCalender.set(year, month, dayOfMonth);
                        if(month < 10){
                            m ="0" + (month + 1);
                        } else {
                            m = ""+ (month + 1);
                        }
                        if (dayOfMonth<10){
                            d ="0"+ dayOfMonth;
                        } else {
                            d = "" + dayOfMonth;
                        }
                        String fnD = year + "-" + m + "-" + d;
                        purchaseDate.setText(fnD);
                    },mYear, mMonth, mDay);
            datePickerDialog.show();
        });

        expiryDate.setOnClickListener(v -> {
            int mYear = myCalender.get(Calendar.YEAR);
            int mMonth = myCalender.get(Calendar.MONTH);
            int mDay = myCalender.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog datePickerDialog = new DatePickerDialog(InventoryActivity.this,
                    (view, year, month, dayOfMonth) -> {
                        myCalender.set(year, month, dayOfMonth);
                        if(month < 10){
                            m ="0" + (month + 1);
                        } else {
                            m = ""+ (month + 1);
                        }
                        if (dayOfMonth<10){
                            d ="0"+ dayOfMonth;
                        } else {
                            d = "" + dayOfMonth;
                        }
                        String fnD = year + "-" + m + "-" + d;
                        expiryDate.setText(fnD);
                    },mYear, mMonth, mDay);
            datePickerDialog.show();
        });

        saveBtn.setOnClickListener(v -> {
            String unitM = unitOfMeasure.getSelectedItem().toString().trim();
            String unitP = unitPrice.getText().toString().trim();
            String reOrder = reorderLevel.getText().toString().trim();
            String purchaseD = purchaseDate.getText().toString().trim();
            String expiryD = expiryDate.getText().toString().trim();

            if (unitM.isEmpty() && unitP.isEmpty() && reOrder.isEmpty() && purchaseD.isEmpty()
                    && expiryD.isEmpty()) {
                Toast.makeText(InventoryActivity.this, "Fill all items", Toast.LENGTH_SHORT).show();
            } else {
                ProgressDialog progressDialog = new ProgressDialog(InventoryActivity.this);
                progressDialog.setMessage("Creating new inventory...");
                progressDialog.show();

                JSONArray jsonArray = new JSONArray();
                JSONObject jsonObject = new JSONObject();

                try {
                    jsonObject.put("merchantId", merchantId);
                    jsonObject.put("inventoryTypeId", "Type-A");
                    jsonObject.put("productId", Integer.valueOf(productId));
                    jsonObject.put("categoryId", Integer.valueOf(categoryId));
                    jsonObject.put("unitOfMeasure", unitM);
                    jsonObject.put("unitPrice", Double.valueOf(unitP));
                    jsonObject.put("reOrderLevel", Integer.valueOf(reOrder));
                    jsonObject.put("expiryDate", expiryD);
                    jsonObject.put("purhaseDate", purchaseD);
                    jsonObject.put("createdById", uid);

                    jsonArray = new JSONArray("["+jsonObject.toString()+"]");

                    Log.d("postInventory", jsonArray.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.POST, url,
                        jsonArray, response -> {
                    try {
                        Log.d("response order", response.toString());
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject jsonObj = response.getJSONObject(i);
                            Log.d("order id", jsonObj.optString("description"));

                            if (jsonObj.optString("description").equals("Inventory added successfully")) {
                                progressDialog.dismiss();
                                dialog.dismiss();
                                String mg = "Inventory created successfully!";
                                successDialog(mg);
                            } else {
                                progressDialog.dismiss();
                                dialog.dismiss();
                                String w = "Inventory created successfully!";
                                warnDialog(w);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        progressDialog.dismiss();
                        dialog.dismiss();
                        String err = "Error occurred while sending request. Check logs!";
                        warnDialog(err);
                    }
                }, error -> {
                    error.printStackTrace();
                    progressDialog.dismiss();
                    dialog.dismiss();
                    String failed = "Failed because the server was unreachable, check your internet connection!";
                    warnDialog(failed);
                }){
                    @Override
                    public Map<String, String> getHeaders() {
                        Map<String, String> params = new HashMap<>();
                        params.put("Content-Type", "application/json");
                        params.put("Authorization", "Bearer " + token);
                        return params;
                    }

                    @Override
                    public String getBodyContentType() {
                        return "application/json";
                    }
                };
                jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(60000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                MySingleton.getInstance(this).addToRequestQueue(jsonArrayRequest);
            }
        });

        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation_1;
        dialog.show();
        dialog.setCanceledOnTouchOutside(true);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        String url = Constants.BASE_URL + "Inventory/?MerchantId=" + merchantId;
        getInventoryDetails(url);
    }

    private void getInventoryDetails(String url) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    Log.d("responseIn", response.toString());
                    try {
                        JSONArray jsonArray1 = response.getJSONArray("inventoy");
                        for (int i = 0; i < jsonArray1.length(); i++) {
                            JSONObject jsonObject = (JSONObject) jsonArray1.get(i);

                            DataInventoryMgt dataInventoryMgt = new DataInventoryMgt();
                            dataInventoryMgt.setId(jsonObject.optString("id"));
                            dataInventoryMgt.setProduct(jsonObject.optString("product"));
                            dataInventoryMgt.setMerchantId(jsonObject.getString("merchantId"));
                            dataInventoryMgt.setProductId(jsonObject.optString("productId"));
                            dataInventoryMgt.setCategoryId(jsonObject.optString("categoryId"));
                            dataInventoryMgt.setUnitOfMeasure(jsonObject.optString("unitOfMeasure"));
                            dataInventoryMgt.setUnitPrice(jsonObject.optString("unitPrice"));
                            dataInventoryMgt.setStockQuantity(jsonObject.optString("stockQuantity"));
                            dataInventoryMgt.setReOrderLevel(jsonObject.optString("reOrderLevel"));
                            dataInventoryMgt.setPurhaseDate(jsonObject.optString("purhaseDate"));
                            dataInventoryMgt.setExpiryDate(jsonObject.optString("expiryDate"));
                            dataInventoryMgt.setCreatedById(jsonObject.optString("createdById"));

                            inventoryMgtData.add(dataInventoryMgt);
                        }

                        adapterInventory = new AdapterInventory(inventoryMgtData, InventoryActivity.this);
                        binding.inventoryRv.setAdapter(adapterInventory);

                        if (adapterInventory.getItemCount() == 0) {
                            binding.inventoryRv.setVisibility(View.GONE);
                            binding.noData.setVisibility(View.VISIBLE);
                        } else {
                            binding.inventoryRv.setVisibility(View.VISIBLE);
                            binding.noData.setVisibility(View.GONE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        String err = "Error occurred while sending request\nCheck logs!";
                        warnDialog(err);
                    }

                }, error -> {
            error.printStackTrace();
            String failed = "Failed because the server was unreachable, check your internet connection!";
            warnDialog(failed);
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getInstance(InventoryActivity.this).addToRequestQueue(jsonObjectRequest);
    }

    public void modifyInventory(String iId, String mId, String pId, String cId, String iUm, String iUp,
                                String iRl, String iEd, String iPd, Context mContext, String createdBy, String tokenI) {
        String modUrl = Constants.BASE_URL + "Inventory/Modify";
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = new JSONObject();

        ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage("Modifying inventory...");
        progressDialog.show();

        try {
            jsonObject.put("Id", Integer.valueOf(iId));
            jsonObject.put("merchantId", mId);
            jsonObject.put("inventoryTypeId", "Type-A");
            jsonObject.put("productId", Integer.valueOf(pId));
            jsonObject.put("categoryId", Integer.valueOf(cId));
            jsonObject.put("unitOfMeasure", iUm);
            jsonObject.put("unitPrice", Double.valueOf(iUp));
            jsonObject.put("reOrderLevel", Double.valueOf(iRl));
            jsonObject.put("expiryDate", iEd);
            jsonObject.put("purhaseDate", iPd);
            jsonObject.put("createdById",createdBy);

            jsonArray = new JSONArray("["+jsonObject.toString()+"]");
            Log.d("postMod", jsonArray.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.POST,
                modUrl, jsonArray, response -> {
            try {
                Log.d("resMod", response.toString());
                progressDialog.dismiss();
                for(int i=0; i < response.length(); i++) {
                    JSONObject jsonObj = response.getJSONObject(i);
                    String msg = jsonObj.optString("description");
                    Toast.makeText(InventoryActivity.this, msg, Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                progressDialog.dismiss();
                e.printStackTrace();
            }
        }, error -> {
            progressDialog.dismiss();
            Log.e("error", error.toString());
        }){
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json");
                params.put("Authorization", "Bearer " + tokenI);
                return params;
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };
        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        MySingleton.getInstance(InventoryActivity.this).addToRequestQueue(jsonArrayRequest);
    }

    private void successDialog(String successM) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_success);
        dialog.setCancelable(false);

        TextView successMessage = dialog.findViewById(R.id.successMessage);
        Button okBtn = dialog.findViewById(R.id.okBtn);

        successMessage.setText(successM);

        okBtn.setOnClickListener(v -> dialog.dismiss());

        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation_1;
        dialog.show();
        dialog.setCanceledOnTouchOutside(true);
    }

    private void warnDialog(String message) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_warning);
        dialog.setCancelable(false);

        TextView warnMessage = dialog.findViewById(R.id.warnMessage);
        Button okBtn = dialog.findViewById(R.id.okBtn);

        warnMessage.setText(message);

        okBtn.setOnClickListener(v -> dialog.dismiss());

        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation_1;
        dialog.show();
        dialog.setCanceledOnTouchOutside(true);
    }
}
