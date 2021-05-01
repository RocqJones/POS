package com.intoverflown.pos.ui.registerbranch;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.intoverflown.pos.databinding.ActivityNewBranchBinding;

public class NewBranchActivity extends AppCompatActivity {

    private ActivityNewBranchBinding binding;

    public SharedPreferences preferences;
    public String SHARED_PREF_NAME = "pos_pref";
    public String KEY_ID = "Id";
    public String KEY_TOKEN = "Token";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityNewBranchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferences = this.getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);

        String uid = preferences.getString(KEY_ID, "Id");
        String token = preferences.getString(KEY_TOKEN, "Token");

        Log.d("uid new branch", uid);
        Log.d("token new branch", token);
    }
}
