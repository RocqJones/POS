package com.intoverflown.pos.ui.inventory.postdata;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.toolbox.JsonArrayRequest;
import com.intoverflown.pos.databinding.ActivityAddProductBinding;
import com.intoverflown.pos.patterns.MySingleton;
import com.intoverflown.pos.ui.inventory.InventoryActivityMain;
import com.intoverflown.pos.ui.profile.addmerchant.AddMerchantActivity;
import com.intoverflown.pos.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddProductActivity extends AppCompatActivity {

    private ActivityAddProductBinding binding;
    String uid;
    String token;
    String merchantId;
    String categoryId;
    String supplierId;
    public SharedPreferences preferences;
    public SharedPreferences.Editor editor;
    public String SHARED_PREF_NAME = "pos_pref";
    public String KEY_ID = "Id";
    public String KEY_TOKEN = "Token";
    public String MERCHANT_ID = "merchantId";
    public String CATEGORY_ARR = "categoryArr";
    public String SUPPLIER_ID = "supplierId";
    public String SUPPLIER_ARR = "supplierArr";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAddProductBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.addBackBtn.setOnClickListener(v -> {
            Intent i = new  Intent(AddProductActivity.this, InventoryActivityMain.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        });

        preferences = this.getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);

        uid = preferences.getString(KEY_ID, "Id");
        token = preferences.getString(KEY_TOKEN, "Token");
        if (preferences.contains("merchantId")) {
            merchantId = preferences.getString(MERCHANT_ID, "merchantId");
        } else {
            Intent mC = new Intent(this, AddMerchantActivity.class);
            startActivity(mC);
        }

        if (preferences.contains("categoryArr")) {
            // categoryId = preferences.getString(CATEGORY_ID, "categoryId");
            String categoryArr = preferences.getString(CATEGORY_ARR, "categoryArr");

            String temp_str = categoryArr.replace("[", "").replace("]", "");
            String[] categoryT = temp_str.split(",");
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(AddProductActivity.this,
                    android.R.layout.simple_dropdown_item_1line, categoryT);
            binding.category.setAdapter(adapter);

            String ct = binding.category.getSelectedItem().toString().trim();
            String [] c = ct.split(":");
            categoryId = c[0];
        } else {
            Intent cT = new Intent(this, AddCategoryActivity.class);
            startActivity(cT);
        }

        if (preferences.contains("supplierId") | preferences.contains("supplierArr")) {
            // supplierId = preferences.getString(SUPPLIER_ID, "supplierId");
            String supplierArr = preferences.getString(SUPPLIER_ARR, "supplierArr");

            String temp_str = supplierArr.replace("[", "").replace("]", "");
            String[] supplierT = temp_str.split(",");
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(AddProductActivity.this,
                    android.R.layout.simple_dropdown_item_1line, supplierT);
            binding.supplier.setAdapter(adapter);

            String sp = binding.supplier.getSelectedItem().toString().trim();
            String [] s = sp.split(":");
            supplierId = s[0];
        } else {
            Intent sP = new Intent(this, AddSupplierActivity.class);
            startActivity(sP);
        }

        binding.addSaveBtn.setOnClickListener(v -> {
            String url = Constants.BASE_URL + "Product/Create";
            postProduct(url);
        });
    }

    private void postProduct(String url) {
        JSONArray array = new JSONArray();
        JSONObject jsonObjects = new JSONObject();

        String productName = binding.addName.getText().toString().trim();
        String unitOfMeasure = binding.addUnitMeasure.getText().toString().trim();
        String purchasePrice = binding.addPrice.getText().toString().trim();
        String quantity = binding.addQty.getText().toString().trim();
        String remarks = binding.addRemarks.getText().toString().trim();
        String reorderLevel = binding.addReOrderLevel.getText().toString().trim();
        String status = "Available";

        if (productName.isEmpty() && unitOfMeasure.isEmpty() && purchasePrice.isEmpty()
                && quantity.isEmpty() && remarks.isEmpty() && reorderLevel.isEmpty()) {
            Toast.makeText(this, "Enter all fields!!", Toast.LENGTH_SHORT).show();
        } else {
            ProgressDialog progressDialog = new ProgressDialog(AddProductActivity.this);
            progressDialog.setMessage("Creating new product...");
            progressDialog.show();

            try {
                jsonObjects.put("Id", uid);
                jsonObjects.put("name", productName);
                jsonObjects.put("remarks", remarks);
                jsonObjects.put("unitOfMeasure", unitOfMeasure);
                jsonObjects.put("statusID", status);
                jsonObjects.put("reOrderLevel", reorderLevel);
                jsonObjects.put("categoryId", categoryId);
                jsonObjects.put("quantity", quantity);
                jsonObjects.put("supplierId", supplierId);
                jsonObjects.put("merchantId", merchantId);
                jsonObjects.put("createdById", uid);

                array = new JSONArray("["+jsonObjects.toString()+"]");
                Log.d("Post", array.toString());
            } catch (final JSONException e) {
                e.printStackTrace();
            }

            JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(com.android.volley.Request.Method.POST,
                    url, array, response -> {
                try {
                    Log.d("response", response.toString());

                    for(int i=0; i < response.length(); i++) {
                        JSONObject jsonObj = response.getJSONObject(i);
                        Log.d("uid addProduct", jsonObj.optString("uniqueId"));

                        // store id
                        editor = preferences.edit();
                        editor.putString("createProductId", jsonObj.optString("uniqueId"));
                        editor.apply();
                    }

                    progressDialog.dismiss();
                    Toast.makeText(this, "Created Successfully!", Toast.LENGTH_SHORT).show();
                    proceedToInventory();
                } catch (Exception e) {
                    Toast.makeText(this, "Error occurred\nCheck logs!", Toast.LENGTH_LONG).show();
                    Log.i("new merchant", Log.getStackTraceString(e));
                }
            }, error -> {
                progressDialog.dismiss();
                Log.e("error", error.toString());
                Toast.makeText(AddProductActivity.this, "Failed to create product!", Toast.LENGTH_SHORT).show();
            }) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("Content-Type", "application/json");
                    params.put("Authorization", "Bearer " + token);
                    return params;
                }

                @Override
                public String getBodyContentType() {
                    return "application/json";
                }
            };
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(60000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
        }
    }

    private void proceedToInventory() {
        Intent j = new Intent(AddProductActivity.this, InventoryActivityMain.class);
        j.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(j);
    }
}
