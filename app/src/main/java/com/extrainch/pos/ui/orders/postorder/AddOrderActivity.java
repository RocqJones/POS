package com.extrainch.pos.ui.orders.postorder;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.extrainch.pos.R;
import com.extrainch.pos.databinding.ActivityAddOrderBinding;
import com.extrainch.pos.patterns.MySingleton;
import com.extrainch.pos.ui.customers.addcustomers.AddCustomerActivity;
import com.extrainch.pos.ui.inventory.postdata.AddProductActivity;
import com.extrainch.pos.ui.inventory.postdata.AddSupplierActivity;
import com.extrainch.pos.ui.orders.OrdersActivity;
import com.extrainch.pos.ui.profile.addmerchant.AddMerchantActivity;
import com.extrainch.pos.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class AddOrderActivity extends AppCompatActivity {

    private ActivityAddOrderBinding binding;
    final Calendar myCalender = Calendar.getInstance();

    public SharedPreferences preferences;
    public SharedPreferences.Editor editor;
    public String SHARED_PREF_NAME = "pos_pref";
    public String KEY_ID = "Id";
    public String KEY_TOKEN = "Token";
    public String MERCHANT_ID = "merchantId";
    public String SUPPLIER_ID = "supplierId";
    public String CUSTOMER_ID = "customerId";
    public String PRODUCT_ID = "createProductId";

    public String SUPPLIER_ARR = "supplierArr";
    public String PRODUCT_ARR = "productArr";

    String uid;
    String token;
    Integer merchantId;
    String supplierId;
    Integer customerId;
    String productId;

    String m, d;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddOrderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String[] paymentTypes = {"Cash", "Mobile", "Card"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, paymentTypes);
        binding.paymentType.setAdapter(adapter);

        // order date picker
        binding.orderDate.setOnClickListener(v -> {
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
                binding.orderDate.setText(fnD);
            },mYear, mMonth, mDay);
            datePickerDialog.show();
        });

        // required date picker
        binding.requiredDate.setOnClickListener(v -> {
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
                binding.requiredDate.setText(fnD);
            },mYear, mMonth, mDay);
            datePickerDialog.show();
        });

        binding.shippingDate.setOnClickListener(v -> {
            int mYear = myCalender.get(Calendar.YEAR);
            int mMonth = myCalender.get(Calendar.MONTH);
            int mDay = myCalender.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog datePickerDialog = new DatePickerDialog(AddOrderActivity.this, (view, year, month, dayOfMonth) -> {
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
                binding.shippingDate.setText(fnD);
            },mYear, mMonth, mDay);
            datePickerDialog.show();
        });

        // auto calculate due cost
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!binding.orderQuantity.getText().toString().equals("") &&
                        !binding.orderUnitCost.getText().toString().equals("") &&
                        !binding.orderDiscount.getText().toString().equals("")) {
                    int quantity = Integer.parseInt(binding.orderQuantity.getText().toString());
                    double unitCost = Double.parseDouble(binding.orderUnitCost.getText().toString());
                    double discount = Double.parseDouble(binding.orderDiscount.getText().toString());

                    double results = (unitCost - (discount/100 * unitCost)) * quantity;
                    binding.orderTotalDue.setText(String.valueOf(results));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };

        binding.orderQuantity.addTextChangedListener(textWatcher);
        binding.orderUnitCost.addTextChangedListener(textWatcher);
        binding.orderDiscount.addTextChangedListener(textWatcher);

        binding.backBtn.setOnClickListener(v -> {
            Intent j = new Intent(this, OrdersActivity.class);
            j.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(j);
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

            TextView warnMessage = (TextView) dialog.findViewById(R.id.warnMessage);
            Button okBtn = (Button) dialog.findViewById(R.id.okBtn);

            warnMessage.setText("To create supplier you must maintain merchant!");

            okBtn.setOnClickListener(v -> {
                dialog.dismiss();
                Intent mC = new Intent(this, AddMerchantActivity.class);
                startActivity(mC);
            });

            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation_1;
            dialog.show();
            dialog.setCanceledOnTouchOutside(true);
        }

//        if (preferences.contains("supplierId")) {
//            supplierId = Integer.valueOf(preferences.getString(SUPPLIER_ID, "supplierId"));
//        } else {
//            Intent sP = new Intent(this, AddSupplierActivity.class);
//            startActivity(sP);
//        }

        if (preferences.contains("customerId")) {
            customerId = Integer.valueOf(preferences.getString(CUSTOMER_ID, "customerId"));
        } else {
            Intent cM = new Intent(this, AddCustomerActivity.class);
            startActivity(cM);
        }

        if (preferences.contains("supplierId") | preferences.contains("supplierArr")) {
            // supplierId = preferences.getString(SUPPLIER_ID, "supplierId");
            String supplierArr = preferences.getString(SUPPLIER_ARR, "supplierArr");

            String temp_str = supplierArr.replace("[", "").replace("]", "");
            String[] supplierT = temp_str.split(",");
            ArrayAdapter<String> adapterS = new ArrayAdapter<String>(this,
                    android.R.layout.simple_dropdown_item_1line, supplierT);
            binding.orderSupplier.setAdapter(adapterS);

            String sp = binding.orderSupplier.getSelectedItem().toString().trim();
            String [] s = sp.split(":");
            supplierId = s[0];
        } else {
            final Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.dialog_warning);
            dialog.setCancelable(false);

            TextView warnMessage = (TextView) dialog.findViewById(R.id.warnMessage);
            Button okBtn = (Button) dialog.findViewById(R.id.okBtn);

            warnMessage.setText("To create order you must create an associate supplier!");

            okBtn.setOnClickListener(v -> {
                dialog.dismiss();
                Intent sP = new Intent(this, AddSupplierActivity.class);
                startActivity(sP);
            });

            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation_1;
            dialog.show();
            dialog.setCanceledOnTouchOutside(true);
        }

        if (preferences.contains("productArr")) {
            String supplierArr = preferences.getString(PRODUCT_ARR, "productArr");

            String temp_str_p = supplierArr.replace("[", "").replace("]", "");
            String[] supplierT = temp_str_p.split(",");
            ArrayAdapter<String> adapterP = new ArrayAdapter<String>(this,
                    android.R.layout.simple_dropdown_item_1line, supplierT);
            binding.selectProduct.setAdapter(adapterP);

            String sp = binding.selectProduct.getSelectedItem().toString().trim();
            String [] s = sp.split(":");
            productId = s[0];
        } else {
            final Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.dialog_warning);
            dialog.setCancelable(false);

            TextView warnMessage = (TextView) dialog.findViewById(R.id.warnMessage);
            Button okBtn = (Button) dialog.findViewById(R.id.okBtn);

            warnMessage.setText("To create order you must maintain an associate product!");

            okBtn.setOnClickListener(v -> {
                dialog.dismiss();
                Intent p = new Intent(this, AddProductActivity.class);
                startActivity(p);
            });

            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation_1;
            dialog.show();
            dialog.setCanceledOnTouchOutside(true);
        }

//        productId = Integer.valueOf(preferences.getString(PRODUCT_ID, "createProductId"));
        //productId = 10;
        binding.orderCreateBtn.setOnClickListener(v -> {
            String url = Constants.BASE_URL + "Order/Create";
            postOrder(url);
        });
    }

    private void postOrder(String url) {
        // root json obj
        JSONArray array = new JSONArray();
        JSONObject jsonObject = new JSONObject();

        String orderDate = binding.orderDate.getText().toString().trim();
        String requiredDate = binding.requiredDate.getText().toString().trim();
        int quantity = Integer.parseInt(binding.orderQuantity.getText().toString().trim());
        double unitPrice = Double.parseDouble(binding.orderUnitCost.getText().toString().trim());
        double discount = Double.parseDouble(binding.orderDiscount.getText().toString().trim());
        double totalDue = Double.parseDouble(binding.orderTotalDue.getText().toString().trim());
        String orderStatusId = "Quote";
        String orderNo = getRandomOrderNo(5);
        String paymentTypeId = binding.paymentType.getSelectedItem().toString().trim();
        String shippingDate = binding.shippingDate.getText().toString().trim();
        String shippingAddress = "KE";
        int orderId = getOrderId(500);
        int totalOrderAmt = 33;

        if (orderDate.isEmpty() || requiredDate.isEmpty() || shippingDate.isEmpty()) {
            String empError = "Sorry, Please fill all the required fields!";
            warnDialog(empError);
        } else {
            ProgressDialog progressDialog = new ProgressDialog(AddOrderActivity.this);
            progressDialog.setMessage("Creating new order...");
            progressDialog.show();

            try {
                JSONObject jsonObject2 = new JSONObject();

                // 1st json obj inside root "order"
                JSONObject jsonObject1 = new JSONObject();
                jsonObject1.put("customerId", customerId);
                jsonObject1.put("merchantId", merchantId);
                jsonObject1.put("orderNo", orderNo);
                jsonObject1.put("orderDate", orderDate);
                jsonObject1.put("dateRequired", requiredDate);
                jsonObject1.put("totalOrderAmt", totalOrderAmt);
                jsonObject1.put("orderStatusId", orderStatusId);
                jsonObject1.put("paymentTypeId", paymentTypeId);
                jsonObject1.put("shippingDate", shippingDate);
                jsonObject1.put("shippingAddress", shippingAddress);
                jsonObject1.put("createdById", uid);

                jsonObject.put("order", jsonObject1);

                jsonObject2.put("orderId", orderId);
                jsonObject2.put("productId", productId);
                jsonObject2.put("quantity", quantity);
                jsonObject2.put("unitCost", unitPrice);
                jsonObject2.put("discountAmount", discount);
                jsonObject2.put("totalDue", totalDue);
                jsonObject2.put("supplierId", supplierId);

                JSONArray jsonArray1 = new JSONArray("["+jsonObject2+"]");
                jsonObject.put("details", jsonArray1);

                array = new JSONArray("["+jsonObject.toString()+"]");
                Log.d("post order", array.toString());
             } catch (final JSONException e) {
                e.printStackTrace();
            }

            JsonArrayRequest jsonArrayRequest  = new JsonArrayRequest(Request.Method.POST, url, array, response -> {
                try {
                    Log.d("response order", response.toString());
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject jsonObj = response.getJSONObject(i);
                        Log.d("order id", jsonObj.optString("uniqueId"));

                        editor = preferences.edit();
                        editor.putString("orderId", jsonObj.optString("uniqueId"));
                        editor.apply();
                    }
                    progressDialog.dismiss();
                    String mg = "Order created successfully!";
                    successDialog(mg);
                    proceedToViewOrder();
                } catch (Exception e) {
                    e.printStackTrace();
                    String err = "Error occurred while sending request\nCheck logs!";
                    warnDialog(err);
                }
            }, error -> {
                error.printStackTrace();
                progressDialog.dismiss();
                String failed = "Failed because the server was unreachable, check your internet connection!";
                warnDialog(failed);
            }){
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
            jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(60000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            MySingleton.getInstance(this).addToRequestQueue(jsonArrayRequest);
        }
    }

    private void proceedToViewOrder() {
        Intent k = new Intent(this, OrdersActivity.class);
        k.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(k);
    }

    static String getRandomOrderNo(int n) {
        // length is bounded by 256 Character
        byte[] array = new byte[256];
        new Random().nextBytes(array);

        String randomString = new String(array, StandardCharsets.UTF_8);

        // Create a StringBuffer to store the result and remove all special chars
        StringBuilder r = new StringBuilder();
        String  AlphaNumericString = randomString.replaceAll("[^A-Za-z0-9]", "");

        // Append first 20 alphanumeric characters from the generated random String into the result
        for (int k = 0; k < AlphaNumericString.length(); k++) {

            if (Character.isLetter(AlphaNumericString.charAt(k))
                    && (n > 0)
                    || Character.isDigit(AlphaNumericString.charAt(k))
                    && (n > 0)) {

                r.append(AlphaNumericString.charAt(k));
                n--;
            }
        }

        return r.toString().toUpperCase();
    }

    static Integer getOrderId(int r) {
        Random rand = new Random();
        return rand.nextInt(r);
    }

    private void successDialog(String successM) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_success);
        dialog.setCancelable(false);

        TextView successMessage = (TextView) dialog.findViewById(R.id.successMessage);
        Button okBtn = (Button) dialog.findViewById(R.id.okBtn);

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

        TextView warnMessage = (TextView) dialog.findViewById(R.id.warnMessage);
        Button okBtn = (Button) dialog.findViewById(R.id.okBtn);

        warnMessage.setText(message);

        okBtn.setOnClickListener(v -> dialog.dismiss());

        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation_1;
        dialog.show();
        dialog.setCanceledOnTouchOutside(true);
    }
}