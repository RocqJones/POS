package com.intoverflown.pos.ui.customers;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.intoverflown.pos.databinding.FragmentCustomersBinding;
import com.intoverflown.pos.patterns.MySingleton;
import com.intoverflown.pos.ui.customers.adapter.AdapterCustomer;
import com.intoverflown.pos.ui.customers.addcustomers.AddCustomerActivity;
import com.intoverflown.pos.ui.customers.data.Customer;
import com.intoverflown.pos.ui.profile.addmerchant.AddMerchantActivity;
import com.intoverflown.pos.utils.Constants;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class CustomersFragment extends Fragment {

    private FragmentCustomersBinding binding;

    String token;
    Integer merchantId;

    public SharedPreferences preferences;
    public SharedPreferences.Editor editor;
    public String SHARED_PREF_NAME = "pos_pref";
    public String KEY_TOKEN = "Token";
    public String MERCHANT_ID = "merchantId";

    // adapter data
    List<Customer> customerData;
    private AdapterCustomer adapterCustomer;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCustomersBinding.inflate(inflater, container, false);

        binding.fab.setOnClickListener(v -> {
            Intent i = new Intent(this.getContext(), AddCustomerActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        });

        // prepare adapter
        customerData = new ArrayList<>();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getContext());
        binding.rvCustomer.setLayoutManager(linearLayoutManager);

        preferences = this.getContext().getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        token = preferences.getString(KEY_TOKEN, "Token");
        if (preferences.contains("merchantId")) {
            merchantId = Integer.valueOf(preferences.getString(MERCHANT_ID, "merchantId"));
        } else {
            Intent mC = new Intent(CustomersFragment.this.getContext(), AddMerchantActivity.class);
            startActivity(mC);
        }

        String url = Constants.BASE_URL + "Customer/GetCustomerList?MerchantId=";
        getCustomerList(url);

        return binding.getRoot();
    }

    private void getCustomerList(String url) {
        JsonObjectRequest jsonObjectRequest  = new JsonObjectRequest(Request.Method.GET,
                url + merchantId, null, response -> {
            try {
                Log.d("response customer", response.toString());

                JSONObject jsonObjectM = response.getJSONObject("merchant");
                Log.d("response mName", jsonObjectM.optString("merchantName"));
                String mName = jsonObjectM.optString("merchantName");

                JSONArray jsonArray = response.getJSONArray("customers");

                for(int i=0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                    Log.d("response fname", jsonObject.optString("firstName"));
                    Customer customer = new Customer();
                    customer.setFirstName(jsonObject.optString("firstName"));
                    customer.setOtherNames(jsonObject.optString("otherNames"));
                    customer.setAddress(jsonObject.optString("address"));
                    customer.setPhone(jsonObject.optString("phone"));
                    customer.setEmail(jsonObject.optString("email"));
                    customer.setMerchantName(mName);
                    customerData.add(customer);
                }

                adapterCustomer = new AdapterCustomer(customerData, this.getContext());
                binding.rvCustomer.setAdapter(adapterCustomer);
            } catch (Exception e){
                e.printStackTrace();
            }
        }, Throwable::printStackTrace) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }

            @Override
            public String getBodyContentType()  {
                return "application/json";
            }
        };
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getInstance(this.getContext()).addToRequestQueue(jsonObjectRequest);
    }
}