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

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.intoverflown.pos.databinding.FragmentCustomersBinding;
import com.intoverflown.pos.patterns.MySingleton;
import com.intoverflown.pos.ui.customers.addcustomers.AddCustomerActivity;
import com.intoverflown.pos.utils.Constants;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
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

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCustomersBinding.inflate(inflater, container, false);

        binding.fab.setOnClickListener(v -> {
            Intent i = new Intent(this.getContext(), AddCustomerActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        });

        preferences = this.getContext().getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        token = preferences.getString(KEY_TOKEN, "Token");
        merchantId = Integer.valueOf(preferences.getString(MERCHANT_ID, "merchantId"));

        String url = Constants.BASE_URL + "Customer/GetCustomerList?MerchantId=";
        getCustomerList(url);

        return binding.getRoot();
    }

    private void getCustomerList(String url) {
        JsonObjectRequest jsonObjectRequest  = new JsonObjectRequest(Request.Method.GET,
                url + merchantId, null, response -> {
            try {
                Log.d("response customer", response.toString());
                JSONArray jsonArray = response.getJSONArray("customers");

                for(int i=0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                    Log.d("response fname", jsonObject.optString("firstName"));
                }
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