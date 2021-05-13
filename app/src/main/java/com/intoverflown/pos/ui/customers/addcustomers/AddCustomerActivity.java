package com.intoverflown.pos.ui.customers.addcustomers;

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
import com.android.volley.toolbox.JsonArrayRequest;
import com.intoverflown.pos.MainActivity;
import com.intoverflown.pos.databinding.ActivityAddCustomerBinding;
import com.intoverflown.pos.patterns.MySingleton;
import com.intoverflown.pos.ui.inventory.addproduct.AddProductActivity;
import com.intoverflown.pos.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AddCustomerActivity extends AppCompatActivity {

    String uid;
    String token;
    Integer merchantId;

    private ActivityAddCustomerBinding binding;

    public SharedPreferences preferences;
    public SharedPreferences.Editor editor;
    public String SHARED_PREF_NAME = "pos_pref";
    public String KEY_ID = "Id";
    public String KEY_TOKEN = "Token";
    public String MERCHANT_ID = "merchantId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddCustomerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.backBtn.setOnClickListener(v -> {
            Intent i = new Intent(this, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        });

        String[] clientsTypes = {"Individual", "Business"};

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, clientsTypes);
        binding.customerClientType.setAdapter(adapter);

        preferences = this.getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);

        uid = preferences.getString(KEY_ID, "Id");
        token = preferences.getString(KEY_TOKEN, "Token");
        merchantId = Integer.valueOf(preferences.getString(MERCHANT_ID, "merchantId"));

        binding.customerCreateBtn.setOnClickListener(v -> {
            String url = Constants.BASE_URL + "Customer/Create";
            createCustomer(url);
        });
    }

    private void createCustomer(String url) {
        JSONArray array = new JSONArray();
        JSONObject jsonObjects = new JSONObject();

        String fName = binding.customerFirstName.getText().toString().trim();
        String oName = binding.customerOtherName.getText().toString().trim();
        String address = binding.customerAddress.getText().toString().trim();
        String phone = binding.customerPhone.getText().toString().trim();
        String email = binding.customerEmail.getText().toString().trim();
        String cType = binding.customerClientType.getText().toString().trim();

        Log.d("client type", cType);

        if (fName.isEmpty() && oName.isEmpty() && address.isEmpty() && phone.isEmpty()
                && email.isEmpty() && cType.isEmpty()) {
            Toast.makeText(this, "Enter all fields!", Toast.LENGTH_SHORT).show();
        } else {
            ProgressDialog progressDialog = new ProgressDialog(AddCustomerActivity.this);
            progressDialog.setMessage("Creating customer...");
            progressDialog.show();

            try {
                jsonObjects.put("MerchantId", merchantId);
                jsonObjects.put("FirstName", fName);
                jsonObjects.put("OtherNames", oName);
                jsonObjects.put("Address", address);
                jsonObjects.put("Phone", phone);
                jsonObjects.put("Email", email);
                jsonObjects.put("ClientTypeId", cType);
                jsonObjects.put("CreatedById", uid);

                array = new JSONArray("["+jsonObjects.toString()+"]");
                Log.d("Post", array.toString());
            } catch (final JSONException e) {
                e.printStackTrace();
            }

            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.POST,
                    url, array, response -> {
                try {
                    Log.d("response", response.toString());

                    for(int i=0; i < response.length(); i++) {
                        JSONObject jsonObj = response.getJSONObject(i);
                        Log.d("uid addCustomer", jsonObj.optString("uniqueId"));

                        // store id
                        editor = preferences.edit();
                        editor.putString("customerId", jsonObj.optString("uniqueId"));
                        editor.apply();
                    }

                    progressDialog.dismiss();
                    Toast.makeText(this, "Created Successfully!", Toast.LENGTH_SHORT).show();
                    proceedToHome();
                } catch (Exception e) {
                    Toast.makeText(this, "Error occurred\nCheck logs!", Toast.LENGTH_LONG).show();
                    Log.i("new customer", Log.getStackTraceString(e));
                }
            },error -> {
                progressDialog.dismiss();
                Log.e("error", error.toString());
                Toast.makeText(AddCustomerActivity.this, "Failed to create customer!", Toast.LENGTH_SHORT).show();
            }){
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
    }

    private void proceedToHome() {
        Intent j = new Intent(this, MainActivity.class);
        j.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(j);
    }
}