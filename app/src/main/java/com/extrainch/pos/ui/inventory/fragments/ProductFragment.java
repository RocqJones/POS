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

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.extrainch.pos.R;
import com.extrainch.pos.databinding.FragmentProductBinding;
import com.extrainch.pos.patterns.MySingleton;
import com.extrainch.pos.ui.inventory.adapters.AdapterProducts;
import com.extrainch.pos.ui.inventory.postdata.AddProductActivity;
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

public class ProductFragment extends Fragment {

    private FragmentProductBinding binding;

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
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentProductBinding.inflate(inflater, container, false);

        binding.fab.setOnClickListener(v -> {
            Intent i = new Intent(ProductFragment.this.getContext(), AddProductActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        });

        // prepare root rv layout manager
        productData = new ArrayList<>();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getContext());
        binding.productRecycler.setLayoutManager(linearLayoutManager);

        if (productData.isEmpty()) {
            binding.productRecycler.setVisibility(View.GONE);
            binding.noData.setVisibility(View.VISIBLE);
        } else {
            binding.productRecycler.setVisibility(View.VISIBLE);
            binding.noData.setVisibility(View.GONE);
        }

        preferences = this.getContext().getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        token = preferences.getString(KEY_TOKEN, "Token");
//        if (preferences.contains("createProductId")) {
//            productId = preferences.getString(PRODUCT_ID, "createProductId");
//        } else {
//            Intent pD = new Intent(ProductFragment.this.getContext(), AddProductActivity.class);
//            startActivity(pD);
//        }
//        if (preferences.contains("categoryId")) {
//            categoryId = preferences.getString(CATEGORY_ID, "categoryId");
//        } else {
//            Intent cG = new Intent(ProductFragment.this.getContext(), AddCategoryActivity.class);
//            startActivity(cG);
//        }
//        if (preferences.contains("supplierId")) {
//            supplierId = preferences.getString(SUPPLIER_ID, "supplierId");
//        } else {
//            Intent sP = new Intent(ProductFragment.this.getContext(), AddSupplierActivity.class);
//            startActivity(sP);
//        }
        if (preferences.contains("merchantId")) {
            merchantId = preferences.getString(MERCHANT_ID, "merchantId");
        } else {
            Intent mD = new Intent(ProductFragment.this.getContext(), AddMerchantActivity.class);
            startActivity(mD);
        }

        String url = Constants.BASE_URL + "Product?";
        getProductName(url);

        return binding.getRoot();
    }

    private void getProductName(String url) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                url + "MerchantId=" + merchantId,
                null, response -> {
            Log.d("response products", response.toString());
            try {

                ArrayList<String> products = new ArrayList<String>();

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
                    inventoryRemoteData.setUnits(jsonObject3.optString("unitOfMeasure"));
                    inventoryRemoteData.setStatusId(jsonObject3.optString("statusID"));
                    inventoryRemoteData.setReOrderLevel(jsonObject3.optString("reOrderLevel"));
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

                adapterProducts = new AdapterProducts(productData, this.getContext());
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
        MySingleton.getInstance(this.getContext()).addToRequestQueue(jsonObjectRequest);
    }

    private void warnDialog(String message) {
        final Dialog dialog = new Dialog(ProductFragment.this.getContext());
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