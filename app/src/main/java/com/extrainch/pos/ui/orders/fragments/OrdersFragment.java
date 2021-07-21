package com.extrainch.pos.ui.orders.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.extrainch.pos.R;
import com.extrainch.pos.databinding.FragmentOrdersBinding;
import com.extrainch.pos.patterns.MySingleton;
import com.extrainch.pos.ui.customers.CustomersFragment;
import com.extrainch.pos.ui.customers.addcustomers.AddCustomerActivity;
import com.extrainch.pos.ui.orders.adapter.AdapterOrder;
import com.extrainch.pos.ui.orders.data.OrderRemoteData;
import com.extrainch.pos.ui.orders.postorder.AddOrderActivity;
import com.extrainch.pos.ui.profile.addmerchant.AddMerchantActivity;
import com.extrainch.pos.utils.Constants;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class OrdersFragment extends Fragment {

    private FragmentOrdersBinding binding;

    String token;
    public SharedPreferences preferences;
    public String SHARED_PREF_NAME = "pos_pref";
    public String KEY_TOKEN = "Token";

    public String MERCHANT_ID = "merchantId";
    public String CUSTOMER_ID = "customerId";
    public String ORDER_ID= "orderId";

    int merchantId;
    int customerId;

    List<OrderRemoteData> orderData;
    private AdapterOrder adapterOrder;

    String unitCost; String discount; String firstName; String otherName; String phone; String address;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentOrdersBinding.inflate(inflater, container, false);

        binding.fab.setOnClickListener(v -> {
            Intent i = new Intent(OrdersFragment.this.getContext(), AddOrderActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        });

        orderData = new ArrayList<>();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getContext());
        binding.ordersRecyclerView.setLayoutManager(linearLayoutManager);

        preferences = this.getContext().getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        token = preferences.getString(KEY_TOKEN, "Token");
        if (preferences.contains("merchantId")) {
            merchantId = Integer.parseInt(preferences.getString(MERCHANT_ID, "merchantId"));
        } else {
            Intent mI = new Intent(OrdersFragment.this.getContext(), AddMerchantActivity.class);
            startActivity(mI);
        }
        if (preferences.contains("customerId")) {
            customerId = Integer.parseInt(preferences.getString(CUSTOMER_ID, "customerId"));
        } else {
            Intent tC = new Intent(OrdersFragment.this.getContext(), AddCustomerActivity.class);
            tC.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(tC);
        }

        String url = Constants.BASE_URL + "Order/?Id=0&CustomerId=";
        getAllOrders(url);

        return binding.getRoot();
    }

    private void getAllOrders(String url) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                url + customerId + "&MerchantId=" + merchantId, null, response -> {
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

                adapterOrder = new AdapterOrder(orderData, this.getContext());
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
                Map<String, String> headers = new HashMap<String, String>();
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
        MySingleton.getInstance(this.getContext()).addToRequestQueue(jsonObjectRequest);
    }

    private void successDialog(String successM) {
        final Dialog dialog = new Dialog(OrdersFragment.this.getContext());
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
        final Dialog dialog = new Dialog(OrdersFragment.this.getContext());
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