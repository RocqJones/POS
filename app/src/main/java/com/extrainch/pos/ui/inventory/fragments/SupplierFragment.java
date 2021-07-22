package com.extrainch.pos.ui.inventory.fragments;

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
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.extrainch.pos.R;
import com.extrainch.pos.databinding.FragmentSupplierBinding;
import com.extrainch.pos.patterns.MySingleton;
import com.extrainch.pos.ui.inventory.adapters.AdapterSupplier;
import com.extrainch.pos.ui.inventory.postdata.AddSupplierActivity;
import com.extrainch.pos.ui.inventory.data.InventoryRemoteData;
import com.extrainch.pos.ui.profile.addmerchant.AddMerchantActivity;
import com.extrainch.pos.utils.Constants;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class SupplierFragment extends Fragment {

    FragmentSupplierBinding binding;

    String token;
    Integer merchantId;

    public SharedPreferences preferences;
    public SharedPreferences.Editor editor;
    public String SHARED_PREF_NAME = "pos_pref";
    public String KEY_TOKEN = "Token";
    public String MERCHANT_ID = "merchantId";

    List<InventoryRemoteData> supplierData;
    private AdapterSupplier adapterSupplier;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSupplierBinding.inflate(inflater, container, false);

        binding.fab.setOnClickListener(v -> {
            Intent i = new Intent(SupplierFragment.this.getContext(), AddSupplierActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        });

        // call layout manager
        supplierData = new ArrayList<>();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getContext());
        binding.recyclerSupplier.setLayoutManager(linearLayoutManager);

        preferences = this.getContext().getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        token = preferences.getString(KEY_TOKEN, "Token");
        if (preferences.contains("merchantId")) {
            merchantId = Integer.valueOf(preferences.getString(MERCHANT_ID, "merchantId"));
        } else {
            Intent mD = new Intent(SupplierFragment.this.getContext(), AddMerchantActivity.class);
            startActivity(mD);
        }

        String url = Constants.BASE_URL + "Supplier/GetSupplierList?MerchantId=";
        getSupplier(url);

        return binding.getRoot();
    }

    private void getSupplier(String url) {
        JsonObjectRequest jsonObjectRequest  = new JsonObjectRequest(Request.Method.GET,
                url + merchantId, null, response -> {
            Log.d("response supplier", response.toString());
            try {
                // get merchant name from second array list
//                JSONArray jsonArray1 = response.getJSONArray("merchant");
//                JSONObject jsonObject1 = (JSONObject) jsonArray1.get(1);
                String mName = preferences.getString("merchantName", "merchantName");

                ArrayList<String> suppliers = new ArrayList<String>();
                // supplier details from first array list
                JSONArray jsonArray = response.getJSONArray("supplier");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                    InventoryRemoteData inventoryRemoteData = new InventoryRemoteData();
                    inventoryRemoteData.setName(jsonObject.optString("name"));
                    inventoryRemoteData.setAddress(jsonObject.optString("address"));
                    inventoryRemoteData.setPhone(jsonObject.optString("phone"));
                    inventoryRemoteData.setEmail(jsonObject.optString("email"));
                    inventoryRemoteData.setRemarks(jsonObject.optString("remarks"));
                    inventoryRemoteData.setMerchantName(mName);
                    supplierData.add(inventoryRemoteData);

                    // write names to arr. The '#' will be used to get sub-string
                    String temp_str = jsonObject.optString("id")+":"+jsonObject.optString("name");
                    suppliers.add(temp_str);
                }
                editor = preferences.edit();
                editor.putString("supplierArr", suppliers.toString());
                editor.apply();

                adapterSupplier = new AdapterSupplier(supplierData, this.getContext());
                binding.recyclerSupplier.setAdapter(adapterSupplier);

                if (adapterSupplier.getItemCount() == 0) {
                    binding.recyclerSupplier.setVisibility(View.GONE);
                    binding.noData.setVisibility(View.VISIBLE);
                } else {
                    binding.recyclerSupplier.setVisibility(View.VISIBLE);
                    binding.noData.setVisibility(View.GONE);
                }
            } catch (Exception e){
                e.printStackTrace();
                String err = "Error occurred while sending request\nCheck logs!";
                warnDialog(err);
            }
        }, error -> {
            error.printStackTrace();
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

    private void warnDialog(String message) {
        final Dialog dialog = new Dialog(SupplierFragment.this.getContext());
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

    public void modifyData(String id, String cId, String tokenM) {
        String modUrl = Constants.BASE_URL + "ProductCategory/Create";
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("Id", Integer.valueOf(id));
            jsonObject.put("merchantId", );
            jsonObject.put("name", );
            jsonObject.put("address", );
            jsonObject.put("phone", );
            jsonObject.put("email", );
            jsonObject.put("remarks", );
            jsonObject.put("createdById", cId);

            jsonArray = new JSONArray("["+jsonObject.toString()+"]");
            Log.d("postCatMod", jsonArray.toString());

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
                    Toast.makeText(SupplierFragment.this.getContext(), msg, Toast.LENGTH_SHORT).show();
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
                params.put("Authorization", "Bearer " + tokenM);
                return params;
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };
        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        MySingleton.getInstance(this.getContext()).addToRequestQueue(jsonArrayRequest);
    }
}