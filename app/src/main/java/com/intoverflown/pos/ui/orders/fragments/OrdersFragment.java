package com.intoverflown.pos.ui.orders.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.intoverflown.pos.databinding.FragmentOrdersBinding;
import com.intoverflown.pos.patterns.MySingleton;
import com.intoverflown.pos.ui.orders.postorder.AddOrderActivity;
import com.intoverflown.pos.utils.Constants;

import java.util.HashMap;
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

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentOrdersBinding.inflate(inflater, container, false);

        binding.fab.setOnClickListener(v -> {
            Intent i = new Intent(OrdersFragment.this.getContext(), AddOrderActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        });

        preferences = this.getContext().getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        token = preferences.getString(KEY_TOKEN, "Token");
        merchantId = Integer.parseInt(preferences.getString(MERCHANT_ID, "merchantId"));
        customerId = Integer.parseInt(preferences.getString(CUSTOMER_ID, "customerId"));

        String url = Constants.BASE_URL + "Order/?Id=0&CustomerId=  2";
        getAllOrders(url);

        return binding.getRoot();
    }

    private void getAllOrders(String url) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                url + customerId + "&MerchantId=" + merchantId, null, response -> {
            Log.d("response", response.toString());

        }, error -> {
            error.printStackTrace();
            Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();
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
}