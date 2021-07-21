 package com.extrainch.pos.ui.customers.addcustomers;

 import android.app.Dialog;
 import android.app.ProgressDialog;
 import android.content.Intent;
 import android.content.SharedPreferences;
 import android.os.Bundle;
 import android.util.Log;
 import android.widget.ArrayAdapter;
 import android.widget.Button;
 import android.widget.TextView;

 import androidx.appcompat.app.AppCompatActivity;

 import com.android.volley.DefaultRetryPolicy;
 import com.android.volley.Request;
 import com.android.volley.toolbox.JsonArrayRequest;
 import com.extrainch.pos.MainActivity;
 import com.extrainch.pos.R;
 import com.extrainch.pos.databinding.ActivityAddCustomerBinding;
 import com.extrainch.pos.patterns.MySingleton;
 import com.extrainch.pos.ui.profile.addmerchant.AddMerchantActivity;
 import com.extrainch.pos.utils.Constants;

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
        if (preferences.contains("merchantId")) {
            merchantId = Integer.valueOf(preferences.getString(MERCHANT_ID, "merchantId"));
        } else {
            final Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.dialog_warning);
            dialog.setCancelable(false);

            TextView warnMessage = (TextView) dialog.findViewById(R.id.warnMessage);
            Button okBtn = (Button) dialog.findViewById(R.id.okBtn);

            warnMessage.setText("To create supplier you must maintain merchant!");

            okBtn.setOnClickListener(v -> {
                dialog.dismiss();
                Intent mD = new Intent(this, AddMerchantActivity.class);
                startActivity(mD);
            });

            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation_1;
            dialog.show();
            dialog.setCanceledOnTouchOutside(true);
        }

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
        String cType = binding.customerClientType.getSelectedItem().toString().trim();

        Log.d("client type", cType);

        if (fName.isEmpty() || oName.isEmpty() || address.isEmpty() || phone.isEmpty()
                || email.isEmpty() || cType.isEmpty()) {
            String empError = "Sorry, Please fill all the fields required!";
            warnDialog(empError);
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
                    String mg = "Customer created successfully!";
                    successDialog(mg);
                    proceedToHome();
                } catch (Exception e) {
                    Log.i("new customer", Log.getStackTraceString(e));
                    String err = "Error occurred while sending request\nCheck logs!";
                    warnDialog(err);
                }
            },error -> {
                progressDialog.dismiss();
                Log.e("error", error.toString());
                String failed = "The server was unreachable, check your internet connection!";
                warnDialog(failed);
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

    private void successDialog(String successM) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_success);
        dialog.setCancelable(false);

        TextView successMessage = (TextView) dialog.findViewById(R.id.successMessage);
        Button okBtn = (Button) dialog.findViewById(R.id.okBtn);

        successMessage.setText(successM);

        okBtn.setOnClickListener(v -> dialog.dismiss());

        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation_1;
        dialog.show();
        dialog.setCanceledOnTouchOutside(true);
    }

    private void warnDialog(String message) {
        final Dialog dialog = new Dialog(this);
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