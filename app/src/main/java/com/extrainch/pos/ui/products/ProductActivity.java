package com.extrainch.pos.ui.products;

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
import com.extrainch.pos.databinding.ActivityProductBinding;
import com.extrainch.pos.patterns.MySingleton;
import com.extrainch.pos.adapters.AdapterProducts;
import com.extrainch.pos.repository.InventoryRemoteData;
import com.extrainch.pos.ui.products.add.AddProductActivity;
import com.extrainch.pos.ui.profile.addmerchant.AddMerchantActivity;
import com.extrainch.pos.utils.Constants;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductActivity extends AppCompatActivity {

    private ActivityProductBinding binding;

    String token;
    String productId;
    String merchantId;
    String categoryId;
    String supplierId;
    public SharedPreferences preferences;
    public String SHARED_PREF_NAME = "pos_pref";
    public String KEY_TOKEN = "Token";
    public String MERCHANT_ID = "merchantId";
    public String CATEGORY_ID = "categoryId";
    public String SUPPLIER_ID = "supplierId";
    public String PRODUCT_ID = "createProductId";

    public SharedPreferences.Editor editor;

    String categoryName;
    String supplierName;

    List<InventoryRemoteData> productData;
    private AdapterProducts adapterProducts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProductBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.backBtn.setOnClickListener(v -> {
            Intent mA = new Intent(ProductActivity.this, MainActivity.class);
            mA.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(mA);
        });

        binding.fab.setOnClickListener(v -> {
            Intent i = new Intent(ProductActivity.this, AddProductActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        });

        // prepare root rv layout manager
        productData = new ArrayList<>();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ProductActivity.this);
        binding.productRecycler.setLayoutManager(linearLayoutManager);

        if (productData.isEmpty()) {
            binding.productRecycler.setVisibility(View.GONE);
            binding.noData.setVisibility(View.VISIBLE);
        } else {
            binding.productRecycler.setVisibility(View.VISIBLE);
            binding.noData.setVisibility(View.GONE);
        }

        preferences = ProductActivity.this.getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        token = preferences.getString(KEY_TOKEN, "Token");
//        if (preferences.contains("createProductId")) {
//            productId = preferences.getString(PRODUCT_ID, "createProductId");
//        } else {
//            Intent pD = new Intent(ProductActivity.this, AddProductActivity.class);
//            startActivity(pD);
//        }
//        if (preferences.contains("categoryId")) {
//            categoryId = preferences.getString(CATEGORY_ID, "categoryId");
//        } else {
//            Intent cG = new Intent(ProductActivity.this, AddCategoryActivity.class);
//            startActivity(cG);
//        }
//        if (preferences.contains("supplierId")) {
//            supplierId = preferences.getString(SUPPLIER_ID, "supplierId");
//        } else {
//            Intent sP = new Intent(ProductActivity.this, AddSupplierActivity.class);
//            startActivity(sP);
//        }
        if (preferences.contains("merchantId")) {
            merchantId = preferences.getString(MERCHANT_ID, "merchantId");
        } else {
            Intent mD = new Intent(ProductActivity.this, AddMerchantActivity.class);
            startActivity(mD);
        }

        String url = Constants.BASE_URL + "Product?";
        getProductName(url);
    }

    private void getProductName(String url) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                url + "MerchantId=" + merchantId,
                null, response -> {
            Log.d("response products", response.toString());
            try {

                ArrayList<String> products = new ArrayList<>();

                JSONArray jsonArray1 = response.getJSONArray("productCategory");
                for (int i = 0; i < jsonArray1.length(); i++) {
                    JSONObject jsonObject1 = (JSONObject) jsonArray1.get(i);
                    categoryName = jsonObject1.optString("name");
                }

                JSONArray jsonArray2 = response.getJSONArray("supplier");
                for (int j = 0; j < jsonArray2.length(); j++) {
                    JSONObject jsonObject2 = (JSONObject) jsonArray2.get(j);
                    supplierName = jsonObject2.optString("name");
                }

                JSONArray jsonArray3 = response.getJSONArray("product");
                for (int k = 0; k < jsonArray3.length(); k++) {
                    JSONObject jsonObject3 = (JSONObject) jsonArray3.get(k);
                    InventoryRemoteData inventoryRemoteData = new InventoryRemoteData();
                    inventoryRemoteData.setName(jsonObject3.optString("name"));
                    inventoryRemoteData.setpId(jsonObject3.optString("id"));
                    inventoryRemoteData.setUnits(jsonObject3.optString("unitOfMeasure"));
                    inventoryRemoteData.setStatusId(jsonObject3.optString("statusID"));
                    inventoryRemoteData.setReOrderLevel(jsonObject3.optString("reOrderLevel"));
                    inventoryRemoteData.setpSupId(jsonObject3.optString("supplierId"));
                    inventoryRemoteData.setpCatId(jsonObject3.optString("categoryId"));
                    inventoryRemoteData.setpMerchId(jsonObject3.optString("merchantId"));
                    inventoryRemoteData.setpQuantity(jsonObject3.optString("quantity"));
                    inventoryRemoteData.setpUnitMeasure(jsonObject3.optString("unitOfMeasure"));
                    inventoryRemoteData.setRemarks(jsonObject3.optString("remarks"));
                    inventoryRemoteData.setCreatedById(jsonObject3.optString("createdById"));
                    inventoryRemoteData.setCategory(categoryName);
                    inventoryRemoteData.setSupplier(supplierName);
                    inventoryRemoteData.setRemarks(jsonObject3.optString("remarks"));
                    productData.add(inventoryRemoteData);

                    String temp_str = jsonObject3.optString("id")+":"+jsonObject3.optString("name");
                    products.add(temp_str);
                }
                editor = preferences.edit();
                editor.putString("productArr", products.toString());
                editor.apply();

                adapterProducts = new AdapterProducts(productData, ProductActivity.this);
                binding.productRecycler.setAdapter(adapterProducts);

                if (adapterProducts.getItemCount() == 0) {
                    binding.productRecycler.setVisibility(View.GONE);
                    binding.noData.setVisibility(View.VISIBLE);
                } else {
                    binding.productRecycler.setVisibility(View.VISIBLE);
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
        MySingleton.getInstance(ProductActivity.this).addToRequestQueue(jsonObjectRequest);
    }

    private void warnDialog(String message) {
        final Dialog dialog = new Dialog(ProductActivity.this);
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
            jsonObject.put("remarks", pRm);
            jsonObject.put("unitOfMeasure", pUm);
            jsonObject.put("statusID", pSt);
            jsonObject.put("reOrderLevel", Integer.valueOf(pRl));
            jsonObject.put("categoryId", cId);
            jsonObject.put("quantity", pQt);
            jsonObject.put("supplierId", sId);
            jsonObject.put("merchantId", Integer.valueOf(mId));
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
                    Toast.makeText(ProductActivity.this, msg, Toast.LENGTH_SHORT).show();
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
                Map<String, String> params = new HashMap<>();
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

        MySingleton.getInstance(ProductActivity.this).addToRequestQueue(jsonArrayRequest);
    }
}