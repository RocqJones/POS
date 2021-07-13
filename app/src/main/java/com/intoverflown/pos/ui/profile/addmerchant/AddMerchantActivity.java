package com.intoverflown.pos.ui.profile.addmerchant;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.intoverflown.pos.MainActivity;
import com.intoverflown.pos.databinding.ActivityAddMerchantBinding;
import com.intoverflown.pos.patterns.MySingleton;
import com.intoverflown.pos.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddMerchantActivity extends AppCompatActivity {

    private ActivityAddMerchantBinding binding;
    String uid;
    String token;
    public SharedPreferences preferences;
    public SharedPreferences.Editor editor;
    public String SHARED_PREF_NAME = "pos_pref";
    public String KEY_ID = "Id";
    public String KEY_TOKEN = "Token";

    List<String> mutableArrCountry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddMerchantBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mutableArrCountry = new ArrayList<String>();

        preferences = this.getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);

        uid = preferences.getString(KEY_ID, "Id");
        token = preferences.getString(KEY_TOKEN, "Token");

        // countries
        getCountryList();

        binding.merchantBackBtn.setOnClickListener(v -> {
            Intent i = new Intent(AddMerchantActivity.this, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        });

        String[] merchantTypes = {"Select...", "TypeA", "TypeB"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, merchantTypes);
        binding.merchantType.setAdapter(adapter);

        Log.d("uid category", uid);
        Log.d("token category", token);

        binding.merchantSaveBtn.setOnClickListener(v -> {
            String url = Constants.BASE_URL + "Merchant/Create";
            createMerchant(url);
        });
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

                String[] countries = mutableArrCountry.toArray(new String[mutableArrCountry.size()]);
                Log.d("arrStr", String.valueOf(countries));
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                        android.R.layout.simple_dropdown_item_1line, countries);
                binding.merchantCountries.setAdapter(adapter);

            } catch (Exception e) {
                Log.i("profile", Log.getStackTraceString(e));
            }
        } , error -> {
            Log.e("error", error.toString());
            Toast.makeText(AddMerchantActivity.this, "loading failed!", Toast.LENGTH_SHORT).show();
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
        MySingleton.getInstance(this).addToRequestQueue(jsonArrayRequest);
    }

    private void createMerchant(String url) {
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        JSONArray array = new JSONArray();
        JSONObject jsonObjects = new JSONObject();

        String name = binding.merchantName.getText().toString().trim();
        String country = binding.merchantCountries.getSelectedItem().toString().trim();
        String merchantType = binding.merchantType.getSelectedItem().toString().trim();

        if (name.isEmpty() && country.isEmpty()) {
            Toast.makeText(this, "Enter all fields!!", Toast.LENGTH_SHORT).show();
        } else {
            ProgressDialog progressDialog = new ProgressDialog(AddMerchantActivity.this);
            progressDialog.setMessage("Creating new merchant...");
            progressDialog.show();

            try {
                jsonObjects.put("MerchantName", name);
                jsonObjects.put("MerchantTypeId", merchantType);
                jsonObjects.put("CountryId", country);
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

                    for(int i=0; i < response.length(); i++) {
                        JSONObject jsonObj = response.getJSONObject(i);
                        Log.d("uid merchant", jsonObj.optString("uniqueId"));

                        // store id
                        editor = preferences.edit();
                        editor.putString("merchantId", jsonObj.optString("uniqueId"));
                        editor.apply();
                    }

                    progressDialog.dismiss();
                    Toast.makeText(this, "Created Successfully!", Toast.LENGTH_SHORT).show();
                    proceedToCreateProfile();
                } catch (Exception e) {
                    Toast.makeText(this, "Error occurred\nCheck logs!", Toast.LENGTH_LONG).show();
                    Log.i("new merchant", Log.getStackTraceString(e));
                }
            }, error -> {
                progressDialog.dismiss();
                Log.e("error", error.toString());
                Toast.makeText(AddMerchantActivity.this, "Failed to create merchant!", Toast.LENGTH_SHORT).show();
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

            queue.add(jsonObjectRequest);
        }
    }

    private void proceedToCreateProfile() {
        Intent j = new Intent(AddMerchantActivity.this, MainActivity.class);
        j.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(j);
    }
}