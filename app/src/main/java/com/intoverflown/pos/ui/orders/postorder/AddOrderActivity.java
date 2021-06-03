package com.intoverflown.pos.ui.orders.postorder;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.intoverflown.pos.databinding.ActivityAddOrderBinding;
import com.intoverflown.pos.patterns.MySingleton;
import com.intoverflown.pos.ui.orders.OrdersActivity;
import com.intoverflown.pos.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
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

    String uid;
    String token;
    Integer merchantId;
    Integer supplierId;
    Integer customerId;
    Integer productId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddOrderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Date picker
        DatePickerDialog.OnDateSetListener date = (view, year, month, dayOfMonth) -> {
            myCalender.set(Calendar.YEAR, year);
            myCalender.set(Calendar.MONTH, month);
            myCalender.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel1();
        };

        DatePickerDialog.OnDateSetListener date2 = (view, year, month, dayOfMonth) -> {
            myCalender.set(Calendar.YEAR, year);
            myCalender.set(Calendar.MONTH, month);
            myCalender.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel2();
        };

        binding.orderDate.setOnClickListener(v -> datePickerDialog(date));
        binding.requiredDate.setOnClickListener(v -> datePickerDialog(date2));

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
        merchantId = Integer.valueOf(preferences.getString(MERCHANT_ID, "merchantId"));
        supplierId = Integer.valueOf(preferences.getString(SUPPLIER_ID, "supplierId"));
        customerId = Integer.valueOf(preferences.getString(CUSTOMER_ID, "customerId"));
//        productId = Integer.valueOf(preferences.getString(PRODUCT_ID, "createProductId"));
        productId = 10;
        binding.orderCreateBtn.setOnClickListener(v -> {
            String url = Constants.BASE_URL + "Order/Create";
            postOrder(url);
        });
    }

    private void datePickerDialog(DatePickerDialog.OnDateSetListener date) {
        new DatePickerDialog(this, date, myCalender.get(Calendar.YEAR),
                myCalender.get(Calendar.MONTH), myCalender.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void updateLabel1() {
        String format = "yyyy-mm-dd";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, Locale.US);
        binding.orderDate.setText(simpleDateFormat.format(myCalender.getTime()));
    }

    private void updateLabel2() {
        String format = "yyyy-mm-dd";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, Locale.US);
        binding.requiredDate.setText(simpleDateFormat.format(myCalender.getTime()));
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
        String paymentTypeId = "Cash";
        String shippingDate = "23-07-21";
        String shippingAddress = "KE";
        int orderId = getOrderId(500);
        int totalOrderAmt = 33;

        // I will check not empty here later
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
                Toast.makeText(this, "Created Successfully!", Toast.LENGTH_SHORT).show();
                proceedToViewOrder();
            } catch (Exception e) {
                e.printStackTrace();
            }
            Toast.makeText(this, "Created Successfully!", Toast.LENGTH_SHORT).show();
        }, error -> {
           error.printStackTrace();
            progressDialog.dismiss();
            Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show();
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
}