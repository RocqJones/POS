package com.extrainch.pos.ui.category.add;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.extrainch.pos.R;
import com.extrainch.pos.databinding.ActivityAddCategoryBinding;
import com.extrainch.pos.ui.category.CategoryActivity;
import com.extrainch.pos.ui.inventory.InventoryActivityMain;
import com.extrainch.pos.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AddCategoryActivity extends AppCompatActivity {

    private ActivityAddCategoryBinding binding;

    String uid;
    String token;
    public SharedPreferences preferences;
    public SharedPreferences.Editor editor;
    public String SHARED_PREF_NAME = "pos_pref";
    public String KEY_ID = "Id";
    public String KEY_TOKEN = "Token";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddCategoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.categoryBackBtn.setOnClickListener(v -> {
            Intent i = new Intent(AddCategoryActivity.this, CategoryActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        });

        /* Get uid and token */
        preferences = this.getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);

        uid = preferences.getString(KEY_ID, "Id");
        token = preferences.getString(KEY_TOKEN, "Token");

        Log.d("uid category", uid);
        Log.d("token category", token);

        binding.categorySaveBtn.setOnClickListener(v -> {
            String url = Constants.BASE_URL + "ProductCategory/Create";
            createCategory(url);
        });
    }

    private void createCategory(String url) {
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        JSONArray array = new JSONArray();
        JSONObject jsonObjects = new JSONObject();

        String name = binding.categoryName.getText().toString().trim();
        String remarks = binding.categoryRemarks.getText().toString().trim();

        if (name.isEmpty() || remarks.isEmpty()) {
            String empError = "Sorry, Please fill all the required fields!";
            warnDialog(empError);
        } else {
            ProgressDialog progressDialog = new ProgressDialog(AddCategoryActivity.this);
            progressDialog.setMessage("Creating new category...");
            progressDialog.show();

            try {
                jsonObjects.put("Name", name);
                jsonObjects.put("Remarks", remarks);
                jsonObjects.put("createdById", uid);

                array = new JSONArray("["+jsonObjects.toString()+"]");
                Log.d("Post", array.toString());

            } catch (final JSONException e) {
                e.printStackTrace();
            }

            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(com.android.volley.Request.Method.POST,
                    url, array, response -> {
                try {
                    Log.d("response", response.toString());

                    for(int i=0; i < response.length(); i++) {
                        JSONObject jsonObj = response.getJSONObject(i);
                        Log.d("uid category", jsonObj.optString("uniqueId"));

                        // store id
                        editor = preferences.edit();
                        editor.putString("categoryId", jsonObj.optString("uniqueId"));
                        editor.apply();
                    }

                    progressDialog.dismiss();
                    String mg = "Category created successfully!";
                    successDialog(mg);
                    proceedToCreateProduct();
                } catch (Exception e) {
//                    Toast.makeText(this, "Error occurred\nCheck logs!", Toast.LENGTH_LONG).show();
                    Log.i("new branch", Log.getStackTraceString(e));
                    String err = "Error occurred while sending request\nCheck logs!";
                    warnDialog(err);
                }
            }, error -> {
                progressDialog.dismiss();
                Log.e("error", error.toString());
                String failed = "Failed because the server was unreachable, check your internet connection!";
                warnDialog(failed);
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

            queue.add(jsonArrayRequest);
        }
    }

    private void proceedToCreateProduct() {
        Intent j = new Intent(AddCategoryActivity.this, InventoryActivityMain.class);
        j.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(j);
        finish();
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