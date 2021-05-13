package com.intoverflown.pos.ui.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.intoverflown.pos.MainActivity;
import com.intoverflown.pos.databinding.ActivityLoginBinding;
import com.intoverflown.pos.ui.resetpassword.ResetPwdActivity;
import com.intoverflown.pos.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    public SharedPreferences preferences;
    public SharedPreferences.Editor editor;
    private static final String SHARED_PREF_NAME = "pos_pref";
    public String KEY_ID = "Id";
    public String KEY_FIRSTNAME = "FirstName";
    public String KEY_LASTNAME = "LastName";
    public String KEY_USERNAME = "Username";
    public String KEY_ROLE = "Role";
    public String KEY_TOKEN = "Token";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.loginForgotPwd.setOnClickListener(v -> {
            Intent i = new Intent(LoginActivity.this, ResetPwdActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        });

        /** User is logged in */

        /** Create pref file dumb here */
        preferences = this.getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);

        /** Call login method */
        binding.loginBtn.setOnClickListener(v -> {
            String url = Constants.BASE_URL + "Connect/Login";
            loginUser(url);
        });
    }

    private void loginUser(String url) {
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        JSONObject jsonObject = new JSONObject();

        String username = binding.loginUserId.getText().toString().trim();
        String password = binding.loginPassword.getText().toString().trim();

        if (username.isEmpty() && password.isEmpty()) {
            Toast.makeText(this, "Enter all fields!!", Toast.LENGTH_SHORT).show();
        } else {
            ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
            progressDialog.setMessage("Logging in\nPlease wait...!");
            progressDialog.show();

            try {
                jsonObject.put("username", username);
                jsonObject.put("password", password);

                Log.d("login response" + "SAMPLE", jsonObject.toString());

            } catch (final JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(com.android.volley.Request.Method.POST,
                    url, jsonObject, response -> {
                        try {
                            Log.d("connection", response.toString());
                            progressDialog.dismiss();

                            Log.d("uid before pref", response.getString("Id"));
                            Log.d("token before pref", response.getString("Token"));

                            // write responses to shared prefs
                            editor = preferences.edit();
                            editor.putString(KEY_ID, response.getString("Id"));
                            editor.putString(KEY_FIRSTNAME, response.getString("FirstName"));
                            editor.putString(KEY_LASTNAME, response.getString("LastName"));
                            editor.putString(KEY_USERNAME, response.getString("Username"));
                            editor.putString(KEY_ROLE, response.getString("Role"));
                            editor.putString(KEY_TOKEN, response.getString("Token"));
                            editor.apply();

                            // access prefs
                            Log.d("uid inside pref", preferences.getString(KEY_ID, "Id"));
                            Log.d("token inside pref", preferences.getString(KEY_TOKEN, "Token"));

                            // Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_LONG).show();

                            Toast.makeText(this, "Login Successful!", Toast.LENGTH_SHORT).show();

                            intentHome();

                        } catch (Exception e) {
                            Toast.makeText(this, Log.getStackTraceString(e) + "", Toast.LENGTH_LONG).show();
                            Log.i("login", Log.getStackTraceString(e));
                        }
                    }, error -> {
                        Log.e("error", error.toString());
                        Toast.makeText(LoginActivity.this, "Login failed!", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("Content-Type", "application/json");
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

    private void intentHome() {
        Intent h = new Intent(LoginActivity.this, MainActivity.class);
        h.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(h);
        finish();
    }
}