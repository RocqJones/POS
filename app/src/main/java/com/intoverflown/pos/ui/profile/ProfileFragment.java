package com.intoverflown.pos.ui.profile;

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
import com.android.volley.toolbox.JsonObjectRequest;
import com.intoverflown.pos.databinding.FragmentProfileBinding;
import com.intoverflown.pos.patterns.MySingleton;
import com.intoverflown.pos.ui.login.LoginActivity;
import com.intoverflown.pos.ui.profile.addmerchant.AddMerchantActivity;
import com.intoverflown.pos.utils.Constants;

import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    public SharedPreferences preferences;
    public String SHARED_PREF_NAME = "pos_pref";
    public SharedPreferences.Editor editor;
    public String MERCHANT_ID = "merchantId";

    String token;
    Integer merchantId;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);

        preferences = this.getContext().getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        String url = Constants.BASE_URL + "Merchant/?merchantId=";
        getMerchantDetails(url);

        setDataToProfile();

        binding.profileAddMerchant.setOnClickListener(v -> {
            Intent i = new Intent(ProfileFragment.this.getContext(), AddMerchantActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        });

        binding.logout.setOnClickListener(v -> {
            SharedPreferences.Editor editor = preferences.edit();
            editor.remove("Id");
            editor.remove("Token");
            editor.remove("Username");
            editor.remove("FirstName");
            editor.remove("LastName");
            editor.remove("Role");
            editor.apply();

            Intent g = new Intent(this.getContext(), LoginActivity.class);
            startActivity(g);
            Toast.makeText(this.getContext(), "Log out successfully!", Toast.LENGTH_SHORT).show();
            getActivity().finish();
        });

        return binding.getRoot();
    }

    private void getMerchantDetails(String url) {
        if (preferences.contains("merchantId")) {
            merchantId = Integer.valueOf(preferences.getString(MERCHANT_ID, "merchantId"));
        } else {
            Intent mer = new Intent(ProfileFragment.this.getContext(), AddMerchantActivity.class);
            mer.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(mer);
        }
        token = preferences.getString("Token", "Token");

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                url + merchantId, null, response -> {
            try {
                Log.d("response", response.toString());
                Log.d("get merchantName", response.getString("merchantName"));
                Log.d("get countryId", response.getString("countryId"));

                editor = preferences.edit();
                editor.putString("merchantName", response.getString("merchantName"));
                editor.putString("countryId", response.getString("countryId"));
                editor.apply();

            } catch (Exception e) {
                Log.i("profile", Log.getStackTraceString(e));
            }
        } , error -> {
            Log.e("error", error.toString());
            Toast.makeText(ProfileFragment.this.getContext(), "loading failed!", Toast.LENGTH_SHORT).show();
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json");
                params.put("Authorization", "Bearer " + token);
                return params;
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

    private void setDataToProfile() {
        String fullName = preferences.getString("FirstName", "FirstName")
                + " " +preferences.getString("LastName", "LastName");
        String userEmail = preferences.getString("Username", "Username");

        binding.profileName.setText(fullName);
        binding.profileEmail.setText(userEmail);

        if (preferences.contains("merchantName") && preferences.contains("countryId")) {
            binding.profileMerchantName.setText(preferences.getString("merchantName", "merchantName"));
            binding.profileLocation.setText(preferences.getString("countryId", "countryId"));
        } else {
            Intent pM = new Intent(ProfileFragment.this.getContext(), AddMerchantActivity.class);
            startActivity(pM);
        }
    }
}