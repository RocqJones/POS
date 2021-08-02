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
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.extrainch.pos.MainActivity;
import com.extrainch.pos.R;
import com.extrainch.pos.databinding.ActivityOrdersBinding;
import com.extrainch.pos.patterns.MySingleton;
import com.extrainch.pos.ui.customers.addcustomers.AddCustomerActivity;
import com.extrainch.pos.adapters.AdapterOrder;
import com.extrainch.pos.repository.OrderRemoteData;
import com.extrainch.pos.ui.orders.postorder.AddOrderActivity;
import com.extrainch.pos.ui.products.ProductActivity;
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

                JSONArray jsonArray1 = response.getJSONArray("orderdetail");
                for (int i = 0; i < jsonArray1.length(); i++) {
                    JSONObject jsonObject = (JSONObject) jsonArray1.get(i);
                    unitCost = jsonObject.optString("unitCost");
                    discount = jsonObject.optString("discountAmount");
                }

                JSONArray jsonArrayCustomers = response.getJSONArray("customers");
                for (int j = 0; j < jsonArrayCustomers.length(); j++) {
                    JSONObject jsonObject = (JSONObject) jsonArrayCustomers.get(j);
                    firstName = jsonObject.optString("firstName");
                    otherName = jsonObject.optString("otherNames");
                    phone = jsonObject.optString("phone");
                    address = jsonObject.optString("address");
                }

                JSONArray jsonArrayOrders = response.getJSONArray("orders");
                for (int k = 0; k < jsonArrayOrders.length(); k++) {
                    JSONObject jsonObject = (JSONObject) jsonArrayOrders.get(k);
                    OrderRemoteData orderRemoteData = new OrderRemoteData();
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

                    orderRemoteData.setFirstName(firstName);
                    orderRemoteData.setOtherNames(otherName);
                    orderRemoteData.setAddress(address);
                    orderRemoteData.setPhone(phone);
                    orderRemoteData.setUnitCost(unitCost);
                    orderRemoteData.setDiscountAmount(discount);
                    orderRemoteData.setTotalOrderAmt(jsonObject.optString("totalOrderAmt"));

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

    public void modifyProduct(String pId, String cId, String sId, String mId, String pNm, String pQt,
                              String pRl, String pUm, String pRm, String pSt, String createdBy,
                              String tokenS) {
        String modUrl = Constants.BASE_URL + "Product/Modify";
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("Id", Integer.valueOf(pId));
            jsonObject.put("name", pNm);
            jsonObject.put("merchantId", pNm);
            jsonObject.put("orderNo", pRm);
            jsonObject.put("orderDate", pUm);
            jsonObject.put("dateRequired", pSt);
            jsonObject.put("totalOrderAmt", Integer.valueOf(pRl));
            jsonObject.put("orderStatusId", cId);
            jsonObject.put("shippingDate", pQt);
            jsonObject.put("shippingAddress", sId);
            jsonObject.put("discountAmount", Integer.valueOf(mId));
            jsonObject.put("quantity", sId);
            jsonObject.put("unitCost", sId);
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

                for(int i=0; i < response.length(); i++) {
                    JSONObject jsonObj = response.getJSONObject(i);
                    String msg = jsonObj.optString("description");
                    Toast.makeText(OrdersActivity.this, msg, Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, error -> {
            Log.e("error", error.toString());
            String failed = "Failed because the server was unreachable, check your internet connection!";
            //warnDialog(failed);
        }){
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json");
                params.put("Authorization", "Bearer " + tokenS);
                return params;
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };
        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        MySingleton.getInstance(OrdersActivity.this).addToRequestQueue(jsonArrayRequest);
    }
}