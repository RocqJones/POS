package com.extrainch.pos.ui.orders;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.extrainch.pos.MainActivity;
import com.extrainch.pos.R;
import com.extrainch.pos.adapters.AdapterOrder;
import com.extrainch.pos.databinding.ActivityOrdersBinding;
import com.extrainch.pos.patterns.MySingleton;
import com.extrainch.pos.repository.OrderRemoteData;
import com.extrainch.pos.ui.customers.addcustomers.AddCustomerActivity;
import com.extrainch.pos.ui.orders.postorder.AddOrderActivity;
import com.extrainch.pos.ui.profile.addmerchant.AddMerchantActivity;
import com.extrainch.pos.utils.Constants;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrdersActivity extends AppCompatActivity {

    private ActivityOrdersBinding binding;

    String token;
    public SharedPreferences preferences;
    public String SHARED_PREF_NAME = "pos_pref";
    public String KEY_TOKEN = "Token";

    public String MERCHANT_ID = "merchantId";
    public String CUSTOMER_ID = "customerId";

    int merchantId;
    int customerId;

    List<OrderRemoteData> orderData;
    private AdapterOrder adapterOrder;

    String unitCost; String discount; String firstName; String otherName; String phone; String address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrdersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.backBtn.setOnClickListener(v -> {
            Intent mA = new Intent(OrdersActivity.this, MainActivity.class);
            mA.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(mA);
        });

        binding.fab.setOnClickListener(v -> {
            Intent i = new Intent(OrdersActivity.this, AddOrderActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        });

        orderData = new ArrayList<>();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(OrdersActivity.this);
        binding.ordersRecyclerView.setLayoutManager(linearLayoutManager);

        preferences = OrdersActivity.this.getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        token = preferences.getString(KEY_TOKEN, "Token");
        if (preferences.contains("merchantId")) {
            merchantId = Integer.parseInt(preferences.getString(MERCHANT_ID, "merchantId"));
        } else {
            Intent mI = new Intent(OrdersActivity.this, AddMerchantActivity.class);
            startActivity(mI);
        }
        if (preferences.contains("customerId")) {
            customerId = Integer.parseInt(preferences.getString(CUSTOMER_ID, "customerId"));
        } else {
            Intent tC = new Intent(OrdersActivity.this, AddCustomerActivity.class);
            tC.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(tC);
        }

        String url = Constants.BASE_URL +"Order/?Id=0&CustomerId="+ customerId +"&MerchantId="+ merchantId;
        getAllOrders(url);
    }

    private void getAllOrders(String url) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                url, null, response -> {
            try {
                Log.d("response", response.toString());
                OrderRemoteData orderRemoteData = new OrderRemoteData();

                JSONArray jsonArray1 = response.getJSONArray("orderdetail");
                for (int i = 0; i < jsonArray1.length(); i++) {
                    JSONObject jsonObject = (JSONObject) jsonArray1.get(i);
//                    unitCost = jsonObject.optString("unitCost");
//                    discount = jsonObject.optString("discountAmount");
                    orderRemoteData.setUnitCost(jsonObject.optString("unitCost"));
                    orderRemoteData.setDiscountAmount(jsonObject.optString("discountAmount"));
                }

                JSONArray jsonArrayCustomers = response.getJSONArray("customers");
                for (int j = 0; j < jsonArrayCustomers.length(); j++) {
                    JSONObject jsonObject = (JSONObject) jsonArrayCustomers.get(j);
//                    firstName = jsonObject.optString("firstName");
//                    otherName = jsonObject.optString("otherNames");
//                    phone = jsonObject.optString("phone");
//                    address = jsonObject.optString("address");

                    orderRemoteData.setFirstName(jsonObject.optString("firstName"));
                    orderRemoteData.setOtherNames(jsonObject.optString("otherNames"));
                    orderRemoteData.setPhone(jsonObject.optString("phone"));
                    orderRemoteData.setAddress(jsonObject.optString("address"));
                }

                JSONArray jsonArrayOrders = response.getJSONArray("orders");
                for (int k = 0; k < jsonArrayOrders.length(); k++) {
                    JSONObject jsonObject = (JSONObject) jsonArrayOrders.get(k);
                    orderRemoteData.setOrderNo(jsonObject.optString("orderNo"));

                    String rawOrdDate = jsonObject.optString("orderDate");
                    String [] d = rawOrdDate.split("T");
                    orderRemoteData.setOrderDate(d[0]);

                    String rawRq = jsonObject.optString("dateRequired");
                    String [] e = rawRq.split("T");
                    orderRemoteData.setDateRequired(e[0]);

                    String rawShipping = jsonObject.optString("shippingDate");
                    String [] f = rawShipping.split("T");
                    orderRemoteData.setShippingDate(f[0]);

                    orderRemoteData.setTotalOrderAmt(jsonObject.optString("totalOrderAmt"));
                    orderRemoteData.setCreatedBy(jsonObject.optString("createdById"));
                    orderRemoteData.setOrderId(jsonObject.optString("id"));
                    orderRemoteData.setCustomerId(jsonObject.optString("customerId"));

                    orderData.add(orderRemoteData);
                }

                adapterOrder = new AdapterOrder(orderData, OrdersActivity.this);
                binding.ordersRecyclerView.setAdapter(adapterOrder);

                if (adapterOrder.getItemCount() == 0) {
                    binding.ordersRecyclerView.setVisibility(View.GONE);
                    binding.noData.setVisibility(View.VISIBLE);
                } else {
                    binding.ordersRecyclerView.setVisibility(View.VISIBLE);
                    binding.noData.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }, error -> {
            error.printStackTrace();
//            Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();
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
        MySingleton.getInstance(OrdersActivity.this).addToRequestQueue(jsonObjectRequest);
    }

    private void successDialog(String successM) {
        final Dialog dialog = new Dialog(OrdersActivity.this);
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

    public void modifyProduct(String mId, String cId, String oId, String oQt, String oNo,
                              String oDt, String oRt, String oUc, String oDc, String oTd,
                              String oPt, String oSd, String oSa, String oSt, String createdBy,
                              String tokenO) {
        String modUrl = Constants.BASE_URL + "Product/Modify";
        JSONObject jsonObject = new JSONObject();

        JSONObject jsonObject1 = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject2 = new JSONObject();

        try {
            jsonObject1.put("customerId", Integer.valueOf(cId));
            jsonObject1.put("merchantId", Integer.valueOf(mId));
            jsonObject1.put("orderNo", oNo);
            jsonObject1.put("orderDate", oDt);
            jsonObject1.put("dateRequired", oRt);
            jsonObject1.put("totalOrderAmt", Double.valueOf(oTd));
            jsonObject1.put("orderStatusId", oSt);
            jsonObject1.put("paymentTypeId", oPt);
            jsonObject1.put("shippingDate", oSd);
            jsonObject1.put("shippingAddress", oSa);
            jsonObject1.put("createdById",createdBy);

            jsonObject.put("order", jsonObject1);

            jsonObject2.put("orderId", Integer.valueOf(oId));
            jsonObject2.put("productId", 1);
            jsonObject2.put("quantity", Integer.valueOf(oQt));
            jsonObject2.put("unitCost", Double.valueOf(oUc));
            jsonObject2.put("discountAmount", Double.valueOf(oDc));
            jsonObject2.put("totalDue", Double.valueOf(oTd));
            jsonObject2.put("supplierId", 1);

            jsonArray = new JSONArray("["+jsonObject2.toString()+"]");

            jsonObject.put("details", jsonArray);
            Log.d("postMod", jsonObject.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest  = new JsonObjectRequest(Request.Method.POST,
                modUrl, jsonObject, response -> {
            try {
                Log.d("resMod", response.toString());

                for(int i=0; i < response.length(); i++) {
                    JSONObject jsonObj = response.getJSONObject(String.valueOf(i));
                    String msg = jsonObj.optString("description");
                    Toast.makeText(OrdersActivity.this, msg, Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, error -> {
            Log.e("error", error.toString());
            String failed = "Failed because the server was unreachable, check your internet connection!";
//            warnDialog(failed);
        }){
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json");
                params.put("Authorization", "Bearer " + tokenO);
                return params;
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        MySingleton.getInstance(OrdersActivity.this).addToRequestQueue(jsonObjectRequest);
    }

    private void warnDialog(String message) {
        final Dialog dialog = new Dialog(OrdersActivity.this);
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