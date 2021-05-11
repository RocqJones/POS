package com.intoverflown.pos.ui.splash;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.intoverflown.pos.MainActivity;
import com.intoverflown.pos.R;
import com.intoverflown.pos.databinding.ActivityStartSplashBinding;
import com.intoverflown.pos.ui.login.LoginActivity;

public class StartSplashActivity extends AppCompatActivity {

    private ActivityStartSplashBinding binding;
    private static final String SHARED_PREF_NAME = "pos_pref";
    private final String KEY_USERNAME = "Username";
    private final String KEY_TOKEN = "Token";
    public SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStartSplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // hide the status bar and make the splash screen as a full screen activity
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Glide.with(this).asGif().load(R.drawable.loadunscreen).error(R.drawable.error_img).into(binding.loader);

        preferences = this.getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        String username = preferences.getString(KEY_USERNAME, "Username");
        String token = preferences.getString(KEY_TOKEN, "Token");

        // set delay time in milliseconds
        new Handler().postDelayed(() -> {
            Log.d("username in pref", username);
            Log.d("token in pref", token);

            if (username != null && token != null) {
                Intent i = new Intent(this, MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                finish();
            } else {
                Intent j = new Intent(this, LoginActivity.class);
                j.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(j);
                finish();
            }
        }, 3000);
    }
}