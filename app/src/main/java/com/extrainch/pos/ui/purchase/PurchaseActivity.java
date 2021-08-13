package com.extrainch.pos.ui.purchase;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
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
import com.extrainch.pos.adapters.AdapterPurchaseOrder;
import com.extrainch.pos.databinding.ActivityPurchaseBinding;
import com.extrainch.pos.patterns.MySingleton;
import com.extrainch.pos.repository.PurchaseRemoteData;
import com.extrainch.pos.ui.profile.addmerchant.AddMerchantActivity;
import com.extrainch.pos.ui.purchase.add.AddPurchaseOrderActivity;
import com.extrainch.pos.utils.Constants;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PurchaseActivity extends AppCompatActivity {

    private ActivityPurchaseBinding binding;

    public SharedPreferences preferences;
    public SharedPreferences.Editor editor;
    public String SHARED_PREF_NAME = "pos_pref";
    public String KEY_TOKEN = "Token";
    public String KEY_ID = "Id";
    public String MERCHANT_ID = "merchantId";

    String token;
    String merchantId;
    String uid;

    List<PurchaseRemoteData> purchaseRemoteDataList;
    private AdapterPurchaseOrder adapterPurchaseOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPurchaseBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.backBtn.setOnClickListener(v -> {
            Intent mA = new Intent(PurchaseActivity.this, MainActivity.class);
            mA.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(mA);
        });

        purchaseRemoteDataList = new ArrayList<>();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(PurchaseActivity.this);
        binding.quoteRecyclerView.setLayoutManager(linearLayoutManager);

        binding.fab.setOnClickListener(v -> {
            Intent i = new Intent(this, AddPurchaseOrderActivity.class);
            startActivity(i);
        });

        preferences = PurchaseActivity.this.getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        token = preferences.getString(KEY_TOKEN, "Token");
        uid = preferences.getString(KEY_ID, "Id");

        if (preferences.contains("merchantId")) {
            merchantId = preferences.getString(MERCHANT_ID, "merchantId");
        } else {
            final Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.dialog_warning);
            dialog.setCancelable(false);

            TextView warnMessage = dialog.findViewById(R.id.warnMessage);
            Button okBtn = dialog.findViewById(R.id.okBtn);

            String message = "To view current purchases you must maintain a merchant";
            warnMessage.setText(message);

            okBtn.setOnClickListener(v -> {
                dialog.dismiss();
                Intent mD = new Intent(PurchaseActivity.this, AddMerchantActivity.class);
                startActivity(mD);
            });

            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation_1;
            dialog.show();
            dialog.setCanceledOnTouchOutside(true);
        }

        String url = Constants.BASE_URL + "Purchase/?MerchantId=" + merchantId;
        getPurchaseDetails(url);
    }

    private void getPurchaseDetails(String url) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    Log.d("responseIn", response.toString());
                    try {
                        PurchaseRemoteData purchaseRemoteData = new PurchaseRemoteData();

                        JSONArray jsonArray = response.getJSONArray("purchase");
                        for (int k = 0; k < jsonArray.length(); k++) {
                            JSONObject jsonObjectX = (JSONObject) jsonArray.get(k);
                            purchaseRemoteData.setMerchantId(jsonObjectX.optString("merchantId"));
                            purchaseRemoteData.setCreatedById(jsonObjectX.optString("createdById"));
                        }

                        JSONArray jsonArray1 = response.getJSONArray("purchasedetail");
                        for (int i = 0; i < jsonArray1.length(); i++) {
                            JSONObject jsonObject = (JSONObject) jsonArray1.get(i);

                            purchaseRemoteData.setId(jsonObject.optString("id"));
                            purchaseRemoteData.setPurchaseId(jsonObject.optString("purchaseId"));
                            purchaseRemoteData.setSupplierId(jsonObject.getString("supplierId"));
                            purchaseRemoteData.setProductId(jsonObject.optString("productId"));
                            purchaseRemoteData.setQuantity(jsonObject.optString("quantity"));
                            purchaseRemoteData.setUnitCost(jsonObject.optString("unitCost"));
                            purchaseRemoteData.setTotalCost(jsonObject.optString("totalCost"));
                            purchaseRemoteData.setSupplier(jsonObject.optString("supplier"));
                            purchaseRemoteData.setProduct(jsonObject.optString("product"));
                            purchaseRemoteData.setPurchase(jsonObject.optString("purchase"));

                            purchaseRemoteDataList.add(purchaseRemoteData);
                        }

                        adapterPurchaseOrder = new AdapterPurchaseOrder(purchaseRemoteDataList, PurchaseActivity.this);
                        binding.quoteRecyclerView.setAdapter(adapterPurchaseOrder);

                        if (adapterPurchaseOrder.getItemCount() == 0) {
                            binding.quoteRecyclerView.setVisibility(View.GONE);
                            binding.noData.setVisibility(View.VISIBLE);
                        } else {
                            binding.quoteRecyclerView.setVisibility(View.VISIBLE);
                            binding.noData.setVisibility(View.GONE);
                        }
                    } catch (Exception e) {
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
                Map<String, String> headers = new HashMap<>();
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
        MySingleton.getInstance(PurchaseActivity.this).addToRequestQueue(jsonObjectRequest);
    }

//    private void successDialog(String successM) {
//        final Dialog dialog = new Dialog(this);
//        dialog.setContentView(R.layout.dialog_success);
//        dialog.setCancelable(false);
//
//        TextView successMessage = dialog.findViewById(R.id.successMessage);
//        Button okBtn = dialog.findViewById(R.id.okBtn);
//
//        successMessage.setText(successM);
//
//        okBtn.setOnClickListener(v -> dialog.dismiss());
//
//        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation_1;
//        dialog.show();
//        dialog.setCanceledOnTouchOutside(true);
//    }

    private void warnDialog(String message) {
        final Dialog dialog = new Dialog(this);
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

    public void modifyPurchase(String holderId, String mId, String sId, String createdBy, String purchaseId,
                               String prodId, Context mContext, String rqDate, String qTy, String unitP, String totalC,
                               String payTy, String pStatus1, String tokenPc) {
        String modUrl = Constants.BASE_URL + "Purchase/Modify";
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = new JSONObject();

        JSONObject jsonObject1 = new JSONObject();
        JSONObject jsonObject2 = new JSONObject();

        ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage("Modifying purchase...");
        progressDialog.show();

        try {
            jsonObject1.put("Id", Integer.valueOf(holderId));
            jsonObject1.put("merchantId", Integer.valueOf(mId));
            jsonObject1.put("SupplierId", Integer.valueOf(sId));
            jsonObject1.put("RequestDate", rqDate);
            jsonObject1.put("paymentTypeId", payTy);
            jsonObject1.put("PurchaseStatusId", pStatus1);
            jsonObject1.put("createdById", createdBy);

            jsonObject2.put("PurchaseId", purchaseId);
            jsonObject2.put("SupplierId", Integer.valueOf(sId));
            jsonObject2.put("productId", Integer.valueOf(prodId));
            jsonObject2.put("quantity",Integer.valueOf(qTy));
            jsonObject2.put("unitCost", Double.valueOf(unitP));
            jsonObject2.put("TotalCost", Double.valueOf(totalC));

            JSONArray jsonArray1 = new JSONArray("["+jsonObject2+"]");
            jsonObject.put("order", jsonObject1);
            jsonObject.put("details", jsonArray1);

            jsonArray = new JSONArray("["+jsonObject.toString()+"]");
            Log.d("postMod", jsonArray.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.POST,
                modUrl, jsonArray, response -> {
            try {
                Log.d("resMod", response.toString());
                progressDialog.dismiss();
                for(int i=0; i < response.length(); i++) {
                    JSONObject jsonObj = response.getJSONObject(i);
                    String msg = jsonObj.optString("description");
                    Toast.makeText(PurchaseActivity.this, msg, Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                progressDialog.dismiss();
                e.printStackTrace();
            }
        }, error -> {
            progressDialog.dismiss();
            Log.e("error", error.toString());
        }){
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer " + tokenPc);
                return headers;
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };
        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        MySingleton.getInstance(PurchaseActivity.this).addToRequestQueue(jsonArrayRequest);
    }
}