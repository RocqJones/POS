package com.extrainch.pos.ui.profile;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.extrainch.pos.R;
import com.extrainch.pos.databinding.FragmentProfileBinding;
import com.extrainch.pos.patterns.MySingleton;
import com.extrainch.pos.ui.login.LoginActivity;
import com.extrainch.pos.ui.profile.addmerchant.AddMerchantActivity;
import com.extrainch.pos.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    String mName, mCountry, mType;

    List<String> mutableArrCountry;

    public String KEY_ID = "Id";
    String uid;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);

        mutableArrCountry = new ArrayList<String>();

        preferences = this.getContext().getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        String url = Constants.BASE_URL + "Merchant/?merchantId=";
        getMerchantDetails(url);

        setDataToProfile();

        // countries
        getCountryList();

        binding.profileAddMerchant.setOnClickListener(v -> {
            Intent i = new Intent(ProfileFragment.this.getContext(), AddMerchantActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        });

        binding.profileEditMerchant.setOnClickListener(v -> {
            dialogEditMerchant(R.style.DialogAnimation_1, "Left - Right Animation!");
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

    private void getCountryList() {
        String countryUrl = Constants.BASE_URL + "Code/GetCountry";
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET,
                countryUrl, null, response -> {
            try {
                Log.d("response", response.toString());

                for (int i = 0; i < response.length(); i++) {
                    JSONObject jsonObject = (JSONObject) response.get(i);
                    String txt = jsonObject.optString("text");
                    mutableArrCountry.add(txt);
                }
                Log.d("arr", mutableArrCountry.toString());

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
        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getInstance(this.getContext()).addToRequestQueue(jsonArrayRequest);
    }

    private void dialogEditMerchant(int dialogAnimation_1, String s) {
        final Dialog dialog = new Dialog(this.getContext());
        dialog.setContentView(R.layout.dialog_edit_merchant);
        dialog.setCancelable(false);

        EditText merchantName = (EditText) dialog.findViewById(R.id.merchantName);
        Spinner merchantCountries = (Spinner) dialog.findViewById(R.id.merchantCountries);
        Spinner merchantType = (Spinner) dialog.findViewById(R.id.merchantType);
        Button merchantSaveBtn = (Button) dialog.findViewById(R.id.merchantSaveBtn);

        String[] countries = mutableArrCountry.toArray(new String[mutableArrCountry.size()]);
        Log.d("arrStr", String.valueOf(countries));
        ArrayAdapter<String> adapterC = new ArrayAdapter<String>(this.getContext(),
                android.R.layout.simple_dropdown_item_1line, countries);
        merchantCountries.setAdapter(adapterC);

        String[] merchantTypes = {"Select...", "TypeA", "TypeB"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getContext(),
                android.R.layout.simple_dropdown_item_1line, merchantTypes);
        merchantType.setAdapter(adapter);

        merchantSaveBtn.setOnClickListener(v -> {
            mName = merchantName.getText().toString().trim();
            mCountry = merchantCountries.getSelectedItem().toString().trim();
            mType = merchantType.getSelectedItem().toString().trim();
            dialog.dismiss();
            String url = Constants.BASE_URL + "Merchant/Modify";
            modifyMerchant(url);
        });

        dialog.getWindow().getAttributes().windowAnimations = dialogAnimation_1;
        dialog.show();
        dialog.setCanceledOnTouchOutside(true);
    }

    private void modifyMerchant(String url) {
        JSONArray array = new JSONArray();
        JSONObject jsonObjects = new JSONObject();

        if (mName.isEmpty() && mCountry.isEmpty() && mType.isEmpty()) {
            Toast.makeText(this.getContext(), "All fields required!", Toast.LENGTH_SHORT).show();
        } else {
            ProgressDialog progressDialog = new ProgressDialog(this.getContext());
            progressDialog.setMessage("Updating merchant...");
            progressDialog.show();

            uid = preferences.getString(KEY_ID, "Id");
            Integer mId = Integer.valueOf(preferences.getString(MERCHANT_ID, "merchantId"));

            try {
                jsonObjects.put("Id", mId);
                jsonObjects.put("MerchantName", mName);
                jsonObjects.put("MerchantTypeId", mType);
                jsonObjects.put("CountryId", mCountry);
                jsonObjects.put("createdById", uid);

                array = new JSONArray("["+jsonObjects.toString()+"]");
                Log.d("Post", array.toString());

            } catch (final JSONException e) {
                e.printStackTrace();
            }

            JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(com.android.volley.Request.Method.POST,
                    url, array, response -> {
                try {
                    Log.d("response", response.toString());
                    progressDialog.dismiss();
                    getMerchantDetails(Constants.BASE_URL + "Merchant/?merchantId=");
                    Toast.makeText(this.getContext(), "Updated Successfully!", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(this.getContext(), "Error occurred\nCheck logs!", Toast.LENGTH_LONG).show();
                    Log.i("new merchant", Log.getStackTraceString(e));
                }
            }, error -> {
                progressDialog.dismiss();
                Log.e("error", error.toString());
                Toast.makeText(this.getContext(), "Failed to create merchant!", Toast.LENGTH_SHORT).show();
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