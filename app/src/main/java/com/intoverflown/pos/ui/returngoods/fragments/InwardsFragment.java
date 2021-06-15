package com.intoverflown.pos.ui.returngoods.fragments;

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
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.intoverflown.pos.databinding.FragmentInwardsBinding;
import com.intoverflown.pos.patterns.MySingleton;
import com.intoverflown.pos.ui.returngoods.adapter.AdapterReturnInwards;
import com.intoverflown.pos.ui.returngoods.data.ReturnRemoteData;
import com.intoverflown.pos.ui.returngoods.postreturns.AddInwardsActivity;
import com.intoverflown.pos.utils.Constants;

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
    public String KEY_TOKEN = "Token";

    String token;

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

        String url = Constants.BASE_URL + "ReturnInward";
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
            } catch (Exception e){
                e.printStackTrace();
            }
        }, error -> {
            error.printStackTrace();
            Log.d("error", "check string url and network conn");
            Toast.makeText(getContext(), "Failed, Check network", Toast.LENGTH_SHORT).show();
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
}