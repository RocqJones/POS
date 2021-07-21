package com.extrainch.pos.ui.returngoods.fragments;

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
import com.extrainch.pos.databinding.FragmentInwardsBinding;
import com.extrainch.pos.patterns.MySingleton;
import com.extrainch.pos.ui.profile.addmerchant.AddMerchantActivity;
import com.extrainch.pos.ui.returngoods.adapter.AdapterReturnInwards;
import com.extrainch.pos.ui.returngoods.data.ReturnRemoteData;
import com.extrainch.pos.ui.returngoods.postreturns.AddInwardsActivity;
import com.extrainch.pos.utils.Constants;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class InwardsFragment extends Fragment {

    private FragmentInwardsBinding binding;

    public SharedPreferences preferences;
    public String SHARED_PREF_NAME = "pos_pref";
    public String MERCHANT_ID = "merchantId";
    public String KEY_TOKEN = "Token";

    String token;
    int merchantId;

    List<ReturnRemoteData> returnInwardsData;
    private AdapterReturnInwards adapterReturnInwards;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentInwardsBinding.inflate(inflater, container, false);

        binding.fab.setOnClickListener(v -> {
            Intent i = new Intent(this.getContext(), AddInwardsActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        });

        returnInwardsData = new ArrayList<>();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getContext());
        binding.returnInwardsRv.setLayoutManager(linearLayoutManager);

        preferences = this.getContext().getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        token = preferences.getString(KEY_TOKEN, "Token");
        if (preferences.contains("merchantId")) {
            merchantId = Integer.parseInt(preferences.getString(MERCHANT_ID, "merchantId"));
        } else {
            Intent mI = new Intent(InwardsFragment.this.getContext(), AddMerchantActivity.class);
            startActivity(mI);
        }

        String url = Constants.BASE_URL + "ReturnInward?MerchantId=" + merchantId;
        getReturnInwards(url);

        return binding.getRoot();
    }

    private void getReturnInwards(String url) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url,
                null, response -> {
            Log.d("response", response.toString());
            try {
                JSONArray jsonArray = response.getJSONArray("returnInward");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                    ReturnRemoteData returnRemoteData = new ReturnRemoteData();
                    returnRemoteData.setReturnedQuantity(jsonObject.optString("returnedQuantity"));
                    returnRemoteData.setOrderedQuantity(jsonObject.optString("orderedQuantity"));

                    String rawOrdD1 = jsonObject.optString("returnDate");
                    String [] d1 = rawOrdD1.split("T");
                    returnRemoteData.setReturnDate(d1[0]);

                    String rawOrdD2 = jsonObject.optString("orderDate");
                    String [] d2 = rawOrdD2.split("T");
                    returnRemoteData.setOrderDate(d2[0]);

                    returnRemoteData.setCustomer(jsonObject.optString("customer"));
                    returnRemoteData.setSupplier(jsonObject.optString("supplier"));
                    returnRemoteData.setReturnReason(jsonObject.optString("returnReason"));
                    returnInwardsData.add(returnRemoteData);
                }

                adapterReturnInwards = new AdapterReturnInwards(returnInwardsData, this.getContext());
                binding.returnInwardsRv.setAdapter(adapterReturnInwards);

                if (adapterReturnInwards.getItemCount() == 0) {
                    binding.returnInwardsRv.setVisibility(View.GONE);
                    binding.noData.setVisibility(View.VISIBLE);
                } else {
                    binding.returnInwardsRv.setVisibility(View.VISIBLE);
                    binding.noData.setVisibility(View.GONE);
                }
            } catch (Exception e){
                e.printStackTrace();
                String err = "Error occurred while sending request\nCheck logs!";
                warnDialog(err);
            }
        }, error -> {
            error.printStackTrace();
            Log.d("error", "check string url and network conn");
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
        final Dialog dialog = new Dialog(InwardsFragment.this.getContext());
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