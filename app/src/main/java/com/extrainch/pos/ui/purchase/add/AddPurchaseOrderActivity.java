package com.extrainch.pos.ui.purchase.add;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.extrainch.pos.R;
import com.extrainch.pos.databinding.ActivityAddPurchaseOrderBinding;
import com.extrainch.pos.patterns.MySingleton;
import com.extrainch.pos.ui.products.add.AddProductActivity;
import com.extrainch.pos.ui.purchase.PurchaseActivity;
import com.extrainch.pos.ui.supplier.add.AddSupplierActivity;
import com.extrainch.pos.ui.orders.OrdersActivity;
import com.extrainch.pos.ui.profile.addmerchant.AddMerchantActivity;
import com.extrainch.pos.utils.Constants;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AddPurchaseOrderActivity extends AppCompatActivity {

    private ActivityAddPurchaseOrderBinding binding;

    public SharedPreferences preferences;
    public SharedPreferences.Editor editor;
    public String SHARED_PREF_NAME = "pos_pref";
    public String KEY_TOKEN = "Token";
    public String KEY_ID = "Id";
    public String MERCHANT_ID = "merchantId";

    public String CATEGORY_ARR = "categoryArr";
    public String PRODUCT_ARR = "productArr";
    public String SUPPLIER_ARR = "supplierArr";
    Integer productId;
    Integer supplierId;
    Integer merchantId;
    String uid;
    String token;

    final Calendar myCalender = Calendar.getInstance();
    String m, d;

    String responseMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddPurchaseOrderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.backBtn.setOnClickListener(v -> {
            Intent i = new Intent(this, PurchaseActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        });

        preferences = this.getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);

        uid = preferences.getString(KEY_ID, "Id");
        token = preferences.getString(KEY_TOKEN, "Token");

        if (preferences.contains("merchantId")) {
            merchantId = Integer.valueOf(preferences.getString(MERCHANT_ID, "merchantId"));
        } else {
            final Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.dialog_warning);
            dialog.setCancelable(false);

            TextView warnMessage = dialog.findViewById(R.id.warnMessage);
            Button okBtn = dialog.findViewById(R.id.okBtn);

            warnMessage.setText("To create purchase order you must maintain a merchant!");

            okBtn.setOnClickListener(v -> {
                dialog.dismiss();
                Intent mC = new Intent(this, AddMerchantActivity.class);
                startActivity(mC);
            });

            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation_1;
            dialog.show();
            dialog.setCanceledOnTouchOutside(true);
        }

        if (preferences.contains("supplierArr")) {
            String supplierArr = preferences.getString(SUPPLIER_ARR, "supplierArr");

            String temp_str = supplierArr.replace("[", "").replace("]", "");
            String[] supplierT = temp_str.split(",");
            ArrayAdapter<String> adapterS = new ArrayAdapter<>(this,
                    android.R.layout.simple_dropdown_item_1line, supplierT);
            binding.selectSupplier.setAdapter(adapterS);

            String sp = binding.selectSupplier.getSelectedItem().toString().trim();
            String [] s = sp.split(":");
            if (s[0].isEmpty()) {
                final Dialog dialog = new Dialog(this);
                dialog.setContentView(R.layout.dialog_warning);
                dialog.setCancelable(false);

                TextView warnMessage = dialog.findViewById(R.id.warnMessage);
                Button okBtn = dialog.findViewById(R.id.okBtn);

                warnMessage.setText("To create purchase order you must maintain a supplier!");

                okBtn.setOnClickListener(v -> {
                    dialog.dismiss();
                    Intent ss = new Intent(this, AddSupplierActivity.class);
                    startActivity(ss);
                });

                dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation_1;
                dialog.show();
                dialog.setCanceledOnTouchOutside(true);
            } else {
                supplierId = Integer.valueOf(s[0]);
            }
        } else {
            final Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.dialog_warning);
            dialog.setCancelable(false);

            TextView warnMessage = dialog.findViewById(R.id.warnMessage);
            Button okBtn = dialog.findViewById(R.id.okBtn);

            warnMessage.setText("To create purchase order you must maintain a supplier!");

            okBtn.setOnClickListener(v -> {
                dialog.dismiss();
                Intent sP = new Intent(this, AddSupplierActivity.class);
                startActivity(sP);
            });

            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation_1;
            dialog.show();
            dialog.setCanceledOnTouchOutside(true);
        }

        binding.requestedDate.setOnClickListener(v -> {
            int mYear = myCalender.get(Calendar.YEAR);
            int mMonth = myCalender.get(Calendar.MONTH);
            int mDay = myCalender.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
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
                binding.requestedDate.setText(fnD);
            },mYear, mMonth, mDay);
            datePickerDialog.show();
        });

        String[] pStatus = {"Select...", "Quote", "Paid", "Unpaid", "Prepaid", "Postpaid"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, pStatus);
        binding.purchaseStatus.setAdapter(adapter);

        String[] payType = {"Select...", "Cash", "Card", "Mobile"};
        ArrayAdapter<String> adapterT = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, payType);
        binding.paymentType.setAdapter(adapterT);

        if (preferences.contains("productArr")) {
            String supplierArr = preferences.getString(PRODUCT_ARR, "productArr");

            String temp_str_p = supplierArr.replace("[", "").replace("]", "");
            String[] supplierT = temp_str_p.split(",");
            ArrayAdapter<String> adapterP = new ArrayAdapter<>(this,
                    android.R.layout.simple_dropdown_item_1line, supplierT);
            binding.selectProduct.setAdapter(adapterP);

            String sp = binding.selectProduct.getSelectedItem().toString().trim();
            String [] s = sp.split(":");
            if (s[0].isEmpty()) {
                final Dialog dialog = new Dialog(this);
                dialog.setContentView(R.layout.dialog_warning);
                dialog.setCancelable(false);

                TextView warnMessage = dialog.findViewById(R.id.warnMessage);
                Button okBtn = dialog.findViewById(R.id.okBtn);

                warnMessage.setText("To create purchase order you must maintain a product!");

                okBtn.setOnClickListener(v -> {
                    dialog.dismiss();
                    Intent ss = new Intent(this, AddProductActivity.class);
                    startActivity(ss);
                });

                dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation_1;
                dialog.show();
                dialog.setCanceledOnTouchOutside(true);
            } else {
                productId = Integer.valueOf(s[0]);
            }
        } else {
            final Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.dialog_warning);
            dialog.setCancelable(false);

            TextView warnMessage = dialog.findViewById(R.id.warnMessage);
            Button okBtn = dialog.findViewById(R.id.okBtn);

            warnMessage.setText("To create purchase order you must maintain a product!");

            okBtn.setOnClickListener(v -> {
                dialog.dismiss();
                Intent p = new Intent(this, AddProductActivity.class);
                startActivity(p);
            });

            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation_1;
            dialog.show();
            dialog.setCanceledOnTouchOutside(true);
        }

        // auto calculate total cost
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!binding.quantity.getText().toString().equals("") &&
                        !binding.unitCost.getText().toString().equals("")) {
                    int quantity = Integer.parseInt(binding.quantity.getText().toString());
                    int unitCost = Integer.parseInt(binding.unitCost.getText().toString());

                    int results = unitCost  * quantity;
                    binding.totalCost.setText(String.valueOf(results));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };

        binding.quantity.addTextChangedListener(textWatcher);
        binding.unitCost.addTextChangedListener(textWatcher);

        binding.createPurchaseOrder.setOnClickListener(v -> {
            String url = Constants.BASE_URL + "Purchase/Create";
            createPurchaseOrder(url);
        });
    }

    private void createPurchaseOrder(String url) {
        //roots
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = new JSONObject();

        String requestedDate = binding.requestedDate.getText().toString().trim();
        String paymentType = binding.paymentType.getSelectedItem().toString().trim();
        String purchaseStatus = binding.purchaseStatus.getSelectedItem().toString().trim();
        int purchaseId = 1;
        String quantityT = binding.quantity.getText().toString().trim();
        String unitCost = binding.unitCost.getText().toString().trim();
        String totalCost = binding.totalCost.getText().toString().trim();

        if (quantityT.isEmpty() || unitCost.isEmpty() || totalCost.isEmpty()) {
            String msg = "Sorry, All fields are required!";
            final Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.dialog_warning);
            dialog.setCancelable(false);

            TextView warnMessage = dialog.findViewById(R.id.warnMessage);
            Button okBtn = dialog.findViewById(R.id.okBtn);

            warnMessage.setText(msg);

            okBtn.setOnClickListener(v -> dialog.dismiss());

            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation_1;
            dialog.show();
            dialog.setCanceledOnTouchOutside(true);
        } else {
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Creating purchase...");
            progressDialog.show();

            try {
                JSONObject sec1 = new JSONObject();
                sec1.put("merchantId", merchantId);
                sec1.put("SupplierId", supplierId);
                sec1.put("RequestDate", requestedDate);
                sec1.put("PaymentStatusId", paymentType);
                sec1.put("PurchaseStatusId", purchaseStatus);
                sec1.put("createdById", uid);

                jsonObject.put("order", sec1);

                JSONObject sec2 = new JSONObject();
                sec2.put("PurchaseId", purchaseId);
                sec2.put("SupplierId", supplierId);
                sec2.put("productId", productId);
                sec2.put("quantity", Integer.valueOf(quantityT));
                sec2.put("unitCost", Integer.valueOf(unitCost));
                sec2.put("TotalCost", Integer.valueOf(totalCost));

                JSONArray jsonArray1 = new JSONArray("["+sec2+"]");
                jsonObject.put("details", jsonArray1);
                jsonArray = new JSONArray("["+jsonObject.toString()+"]");

                Log.d("postPurchase", jsonArray.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }

            JsonArrayRequest jsonArrayRequest  = new JsonArrayRequest(Request.Method.POST, url,
                    jsonArray, response -> {
                try {
                    Log.d("response purchase", response.toString());

                    for (int i = 0; i < response.length(); i++) {
                        JSONObject jsonObj = response.getJSONObject(i);
                        Log.d("id", jsonObj.optString("uniqueId"));
                        responseMessage = jsonObj.getString("description");
                        editor = preferences.edit();
                        editor.putString("purchaseId", jsonObj.optString("uniqueId"));
                        editor.apply();
                    }
                    progressDialog.dismiss();
                    //successDialog(responseMessage);
                    final Dialog dialog = new Dialog(this);
                    dialog.setContentView(R.layout.dialog_success);
                    dialog.setCancelable(false);

                    TextView successMessage = dialog.findViewById(R.id.successMessage);
                    Button okBtn = dialog.findViewById(R.id.okBtn);

                    successMessage.setText(responseMessage);

                    okBtn.setOnClickListener(v -> {
                        Intent iN = new Intent(this, PurchaseActivity.class);
                        startActivity(iN);
                        dialog.dismiss();
                    });

                    dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation_1;
                    dialog.show();
                    dialog.setCanceledOnTouchOutside(true);
                    // to the next screen
                } catch (Exception e) {
                    e.printStackTrace();
                    String err = "Error occurred while sending request\nCheck logs!";
                    warnDialog(err);
                }
            }, error -> {
                error.printStackTrace();
                progressDialog.dismiss();
                //Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show();
                String failed = "The server was unreachable, check your internet connection!";
                warnDialog(failed);
                final Dialog dialog = new Dialog(this);
                dialog.setContentView(R.layout.dialog_warning);
                dialog.setCancelable(false);

                TextView warnMessage = dialog.findViewById(R.id.warnMessage);
                Button okBtn = dialog.findViewById(R.id.okBtn);

                warnMessage.setText("The server was unreachable, check your internet connection!");

                okBtn.setOnClickListener(v -> dialog.dismiss());

                dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation_1;
                dialog.show();
                dialog.setCanceledOnTouchOutside(true);
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
    }

    private void warnDialog(String message) {
        final Dialog dialog = new Dialog(AddPurchaseOrderActivity.this);
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