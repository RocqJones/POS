package com.intoverflown.pos.ui.inventory.fragments;

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
import com.android.volley.toolbox.StringRequest;
import com.intoverflown.pos.databinding.ProductCategoryFragmentBinding;
import com.intoverflown.pos.patterns.MySingleton;
import com.intoverflown.pos.ui.inventory.addcategory.CategoryActivity;
import com.intoverflown.pos.utils.Constants;

import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class CategoryFragment extends Fragment {

    ProductCategoryFragmentBinding binding;

    String token;
    public SharedPreferences preferences;
    public String SHARED_PREF_NAME = "pos_pref";
    public String KEY_TOKEN = "Token";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = ProductCategoryFragmentBinding.inflate(inflater, container, false);

        binding.fab.setOnClickListener(v -> intentToAddCategory());

        preferences = this.getContext().getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        token = preferences.getString(KEY_TOKEN, "Token");
        Log.d("category token", token);

        String url = Constants.BASE_URL + "ProductCategory";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, response -> {
            Log.d("response", response);
            Toast.makeText(getContext(), response, Toast.LENGTH_SHORT).show();
        }, error -> {
            error.printStackTrace();
            Toast.makeText(getContext(), "Get failed", Toast.LENGTH_SHORT).show();
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

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getInstance(this.getContext()).addToRequestQueue(stringRequest);

        return binding.getRoot();
    }

    private void intentToAddCategory() {
        Intent j = new Intent(CategoryFragment.this.getContext(), CategoryActivity.class);
        j.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(j);
    }
}