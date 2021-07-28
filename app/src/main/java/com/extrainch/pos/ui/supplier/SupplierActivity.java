package com.extrainch.pos.ui.supplier;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.extrainch.pos.MainActivity;
import com.extrainch.pos.R;
import com.extrainch.pos.databinding.ActivitySupplierBinding;
import com.extrainch.pos.patterns.MySingleton;
import com.extrainch.pos.repository.InventoryRemoteData;
import com.extrainch.pos.adapters.AdapterSupplier;
import com.extrainch.pos.ui.supplier.add.AddSupplierActivity;
import com.extrainch.pos.ui.profile.addmerchant.AddMerchantActivity;
import com.extrainch.pos.utils.Constants;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SupplierActivity extends AppCompatActivity {

    private ActivitySupplierBinding binding;

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySupplierBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.backBtn.setOnClickListener(v -> {
            Intent mA = new Intent(SupplierActivity.this, MainActivity.class);
            mA.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(mA);
        });

        binding.fab.setOnClickListener(v -> {
            Intent i = new Intent(SupplierActivity.this, AddSupplierActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        });

        // call layout manager
        supplierData = new ArrayList<>();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(SupplierActivity.this);
        binding.recyclerSupplier.setLayoutManager(linearLayoutManager);

        preferences = SupplierActivity.this.getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        token = preferences.getString(KEY_TOKEN, "Token");
        if (preferences.contains("merchantId")) {
            merchantId = Integer.valueOf(preferences.getString(MERCHANT_ID, "merchantId"));
        } else {
            Intent mD = new Intent(SupplierActivity.this, AddMerchantActivity.class);
            startActivity(mD);
        }

        String url = Constants.BASE_URL + "Supplier/GetSupplierList?MerchantId=";
        getSupplier(url);
    }

    private void getSupplier(String url) {
        JsonObjectRequest jsonObjectRequest  = new JsonObjectRequest(Request.Method.GET,
                url + merchantId, null, response -> {
            Log.d("response supplier", response.toString());
            try {
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
                    inventoryRemoteData.setsId(jsonObject.optString("id"));
                    inventoryRemoteData.setMerchantId(jsonObject.optString("merchantId"));
                    inventoryRemoteData.setCreatedById(jsonObject.getString("createdById"));
                    supplierData.add(inventoryRemoteData);

                    // write names to arr. The '#' will be used to get sub-string
                    String temp_str = jsonObject.optString("id")+":"+jsonObject.optString("name");
                    suppliers.add(temp_str);
                }
                editor = preferences.edit();
                editor.putString("supplierArr", suppliers.toString());
                editor.apply();

                adapterSupplier = new AdapterSupplier(supplierData, SupplierActivity.this);
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
        MySingleton.getInstance(SupplierActivity.this).addToRequestQueue(jsonObjectRequest);
    }

    private void warnDialog(String message) {
        final Dialog dialog = new Dialog(SupplierActivity.this);
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

    public void modifyData(String sId, String mId, String gNm, String gAd, String gPh, String gEm,
                           String gRm, String createdBy, String tokenS) {
        String modUrl = Constants.BASE_URL + "Supplier/Modify";
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("Id", Integer.valueOf(sId));
            jsonObject.put("merchantId", Integer.valueOf(mId));
            jsonObject.put("name", gNm);
            jsonObject.put("address", gAd);
            jsonObject.put("phone", gPh);
            jsonObject.put("email", gEm);
            jsonObject.put("remarks", gRm);
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
                    Toast.makeText(SupplierActivity.this, msg, Toast.LENGTH_SHORT).show();
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

        MySingleton.getInstance(SupplierActivity.this).addToRequestQueue(jsonArrayRequest);
    }
}