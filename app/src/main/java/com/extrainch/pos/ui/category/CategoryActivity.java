package com.extrainch.pos.ui.category;

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
import com.extrainch.pos.MainActivity;
import com.extrainch.pos.R;
import com.extrainch.pos.databinding.ActivityCategoryBinding;
import com.extrainch.pos.patterns.MySingleton;
import com.extrainch.pos.repository.InventoryRemoteData;
import com.extrainch.pos.adapters.AdapterCategory;
import com.extrainch.pos.ui.category.add.AddCategoryActivity;
import com.extrainch.pos.ui.profile.addmerchant.AddMerchantActivity;
import com.extrainch.pos.utils.Constants;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CategoryActivity extends AppCompatActivity {

    private ActivityCategoryBinding binding;

    String token;
    public SharedPreferences preferences;
    public SharedPreferences.Editor editor;
    public String SHARED_PREF_NAME = "pos_pref";
    public String KEY_TOKEN = "Token";
    public String KEY_ID = "Id";
    public String MERCHANT_ID = "merchantId";
    String uid;
    String merchantId;

    List<InventoryRemoteData> categoryData;
    private AdapterCategory adapterCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCategoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mA = new Intent(CategoryActivity.this, MainActivity.class);
                mA.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(mA);
            }
        });

        binding.fab.setOnClickListener(v -> intentToAddCategory());

        // prepare data to display on rv
        categoryData = new ArrayList<>();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(CategoryActivity.this);
        binding.categoryRecycler.setLayoutManager(linearLayoutManager);

        preferences = CategoryActivity.this.getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        token = preferences.getString(KEY_TOKEN, "Token");
        uid = preferences.getString(KEY_ID, "Id");
        Log.d("category token", token);

        if (preferences.contains("merchantId")) {
            merchantId = preferences.getString(MERCHANT_ID, "merchantId");
        } else {
            Intent mC = new Intent(CategoryActivity.this, AddMerchantActivity.class);
            startActivity(mC);
        }

        String url = Constants.BASE_URL + "ProductCategory?MerchantId=" + merchantId;
        getCategoryData(url);
    }

    private void getCategoryData(String url) {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url,
                null, response -> {
            try {
                Log.d("response", response.toString());
//            Toast.makeText(getContext(), response, Toast.LENGTH_SHORT).show();
                ArrayList<String> category = new ArrayList<String>();

                for (int i = 0; i < response.length(); i++) {
                    JSONObject jsonObject = (JSONObject) response.get(i);
                    InventoryRemoteData inventoryRemoteData = new InventoryRemoteData();
                    inventoryRemoteData.setItemCatId(jsonObject.optString("id"));
                    inventoryRemoteData.setName(jsonObject.optString("name"));
                    inventoryRemoteData.setRemarks(jsonObject.optString("remarks"));
                    inventoryRemoteData.setCreatedById(jsonObject.getString("createdById"));

                    String rawDate = jsonObject.optString("dateCreated");
                    String [] d = rawDate.split("T");

                    inventoryRemoteData.setDateCreated(d[0]);
                    categoryData.add(inventoryRemoteData);

                    // write names to arr. The ':' will be used to get sub-string
                    String temp_str = jsonObject.optString("id")+":"+jsonObject.optString("name");
                    category.add(temp_str);
                }

                editor = preferences.edit();
                editor.putString("categoryArr", category.toString());
                editor.apply();

                adapterCategory = new AdapterCategory(categoryData, CategoryActivity.this);
                binding.categoryRecycler.setAdapter(adapterCategory);

                if (adapterCategory.getItemCount() == 0) {
                    binding.categoryRecycler.setVisibility(View.GONE);
                    binding.noData.setVisibility(View.VISIBLE);
                } else {
                    binding.categoryRecycler.setVisibility(View.VISIBLE);
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

        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getInstance(CategoryActivity.this).addToRequestQueue(jsonArrayRequest);
    }

    private void intentToAddCategory() {
        Intent j = new Intent(CategoryActivity.this, AddCategoryActivity.class);
        j.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(j);
    }

    private void warnDialog(String message) {
        final Dialog dialog = new Dialog(CategoryActivity.this);
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

    public void modifyData(String id, String txt1, String txt2, String cId, String tokenM) {
        String modUrl = Constants.BASE_URL + "ProductCategory/Modify";
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("Id", Integer.valueOf(id));
            jsonObject.put("Name", txt1);
            jsonObject.put("Remarks", txt2);
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
                    Toast.makeText(CategoryActivity.this, msg, Toast.LENGTH_SHORT).show();
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

        MySingleton.getInstance(CategoryActivity.this).addToRequestQueue(jsonArrayRequest);
    }
}