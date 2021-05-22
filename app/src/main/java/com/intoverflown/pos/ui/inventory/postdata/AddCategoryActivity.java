package com.intoverflown.pos.ui.inventory.postdata;

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
import com.intoverflown.pos.databinding.ActivityAddCategoryBinding;
import com.intoverflown.pos.ui.inventory.InventoryActivityMain;
import com.intoverflown.pos.utils.Constants;

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
            Intent i = new Intent(AddCategoryActivity.this, InventoryActivityMain.class);
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

        if (name.isEmpty() && remarks.isEmpty()) {
            Toast.makeText(this, "Enter all fields!!", Toast.LENGTH_SHORT).show();
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

            JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(com.android.volley.Request.Method.POST,
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
                    Toast.makeText(this, "Created Successfully!", Toast.LENGTH_SHORT).show();
                    proceedToCreateProduct();
                } catch (Exception e) {
                    Toast.makeText(this, "Error occurred\nCheck logs!", Toast.LENGTH_LONG).show();
                    Log.i("new branch", Log.getStackTraceString(e));
                }
            }, error -> {
                progressDialog.dismiss();
                Log.e("error", error.toString());
                Toast.makeText(AddCategoryActivity.this, "Failed to create category!", Toast.LENGTH_SHORT).show();
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

    private void proceedToCreateProduct() {
        Intent j = new Intent(AddCategoryActivity.this, AddProductActivity.class);
        j.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(j);
        finish();
    }
}