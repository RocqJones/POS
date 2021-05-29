package com.intoverflown.pos.ui.registerbranch;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.intoverflown.pos.MainActivity;
import com.intoverflown.pos.databinding.ActivityNewBranchBinding;
import com.intoverflown.pos.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class NewBranchActivity extends AppCompatActivity {

    private ActivityNewBranchBinding binding;

    public SharedPreferences preferences;
    public SharedPreferences.Editor editor;
    public String SHARED_PREF_NAME = "pos_pref";
    public String KEY_ID = "Id";
    public String KEY_TOKEN = "Token";
    public String MERCHANT_ID = "merchantId";

    String uid;
    String token;
    Integer merchantId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityNewBranchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.upBtn.setOnClickListener(v -> {
            Intent i = new Intent(NewBranchActivity.this, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        });

        preferences = this.getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);

        uid = preferences.getString(KEY_ID, "Id");
        token = preferences.getString(KEY_TOKEN, "Token");
        merchantId = Integer.valueOf(preferences.getString(MERCHANT_ID, "merchantId"));

        Log.d("uid new branch", uid);
        Log.d("token new branch", token);

        binding.regMerchantBtn.setOnClickListener(v -> {
            String url = Constants.BASE_URL + "MerchantBranch/Create";
            postNewMerchant(url);
        });
    }

    private void postNewMerchant(String url) {
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        JSONArray array = new JSONArray();
        JSONObject jsonObjects = new JSONObject();

        String region = binding.regMerchantRegion.getText().toString().trim();
        String address = binding.regMerchantAddress.getText().toString().trim();
        String branchName = binding.regMerchantMerchantBranch.getText().toString().trim();
        String contactPerson = binding.regMerchantMerchantName.getText().toString().trim();
        String phone = binding.regMerchantPhone.getText().toString().trim();

        if (region.isEmpty() && address.isEmpty() &&
                branchName.isEmpty() && contactPerson.isEmpty() && phone.isEmpty()) {
            Toast.makeText(this, "Enter all fields!!", Toast.LENGTH_SHORT).show();
        } else {
            ProgressDialog progressDialog = new ProgressDialog(NewBranchActivity.this);
            progressDialog.setMessage("Creating new branch merchant!");
            progressDialog.show();

            try {
                jsonObjects.put("MerchantId", merchantId);
                jsonObjects.put("RegionId", region);
                jsonObjects.put("BranchName", branchName);
                jsonObjects.put("Address", address);
                jsonObjects.put("Phone", phone);
                jsonObjects.put("ContactPerson", contactPerson);
                jsonObjects.put("CreatedById", uid);

                array = new JSONArray("["+jsonObjects.toString()+"]");
                Log.d("Post", array.toString());
            } catch (final JSONException e) {
                e.printStackTrace();
            }

            JsonArrayRequest  jsonObjectRequest = new JsonArrayRequest(com.android.volley.Request.Method.POST,
                    url, array, response -> {
                try {
                    Log.d("connection", response.toString());
                    progressDialog.dismiss();

                    Log.d("response", response.toString());

                    for(int i=0; i < response.length(); i++) {
                        JSONObject jsonObj = response.getJSONObject(i);
                        Log.d("uid merchant branch", jsonObj.optString("uniqueId"));

                        // store id
                        editor = preferences.edit();
                        editor.putString("merchantBranchId", jsonObj.optString("uniqueId"));
                        editor.apply();
                    }

                    Toast.makeText(this, "Created Successfully!", Toast.LENGTH_SHORT).show();

                    intentBackHome();
                } catch (Exception e) {
                    Toast.makeText(this, "Error occurred\nCheck logs!", Toast.LENGTH_LONG).show();
                    Log.i("new branch", Log.getStackTraceString(e));
                }
            }, error -> {
                progressDialog.dismiss();
                Log.e("error", error.toString());
                Toast.makeText(NewBranchActivity.this, "Failed to create branch!", Toast.LENGTH_SHORT).show();
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

    private void intentBackHome() {
        Intent j = new Intent(NewBranchActivity.this, MainActivity.class);
        j.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(j);
        finish();
    }
}
