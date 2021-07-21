package com.extrainch.pos.ui.merchantbranch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.extrainch.pos.MainActivity;
import com.extrainch.pos.databinding.ActivityMerchantBranchBinding;
import com.extrainch.pos.patterns.MySingleton;
import com.extrainch.pos.ui.merchantbranch.adapter.AdapterBranchMerchant;
import com.extrainch.pos.ui.merchantbranch.addmerchantbranch.NewBranchActivity;
import com.extrainch.pos.ui.merchantbranch.data.BranchRemoteData;
import com.extrainch.pos.utils.Constants;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MerchantBranchActivity extends AppCompatActivity {

    private ActivityMerchantBranchBinding binding;

    public String SHARED_PREF_NAME = "pos_pref";
    public String KEY_TOKEN = "Token";
    public SharedPreferences preferences;
    public SharedPreferences.Editor editor;
    String token;

    List<BranchRemoteData> mBranchRemoteData;
    private AdapterBranchMerchant adapterBranchMerchant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMerchantBranchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.backBtn.setOnClickListener(v -> {
            Intent i = new Intent(this, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        });

        binding.actionAddIc.setOnClickListener(v -> {
            Intent j = new Intent(this, NewBranchActivity.class);
            j.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(j);
        });

        mBranchRemoteData = new ArrayList<>();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        binding.rvMerchantBranch.setLayoutManager(linearLayoutManager);

        preferences = this.getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        token = preferences.getString(KEY_TOKEN, "Token");

        String url = Constants.BASE_URL + "MerchantBranch/GetMerchantBranchList";
        getBranchList(url);
    }

    private void getBranchList(String url) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url,
                null, response -> {
            try {
                Log.d("response", response.toString());
                JSONArray jsonArray = response.getJSONArray("branch");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                    BranchRemoteData branchRemoteData = new BranchRemoteData();
                    branchRemoteData.setBranchName(jsonObject.optString("branchName"));
                    branchRemoteData.setAddress(jsonObject.optString("address") + ",");
                    branchRemoteData.setRegionId(jsonObject.optString("regionId"));
                    branchRemoteData.setPhone(jsonObject.optString("phone"));
                    branchRemoteData.setContactPerson(jsonObject.optString("contactPerson"));
                    branchRemoteData.setMerchants(jsonObject.optString("merchants"));
                    mBranchRemoteData.add(branchRemoteData);
                }

                // set adapter constructor params
                adapterBranchMerchant = new AdapterBranchMerchant(mBranchRemoteData, this);
                binding.rvMerchantBranch.setAdapter(adapterBranchMerchant);

                if (adapterBranchMerchant.getItemCount() == 0) {
                    binding.rvMerchantBranch.setVisibility(View.GONE);
                    binding.noData.setVisibility(View.VISIBLE);
                } else {
                    binding.rvMerchantBranch.setVisibility(View.VISIBLE);
                    binding.noData.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, error -> {
            error.printStackTrace();
            Log.d("error", "check string url and network conn");
            Toast.makeText(this, "Failed, Check network", Toast.LENGTH_SHORT).show();
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
        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }
}