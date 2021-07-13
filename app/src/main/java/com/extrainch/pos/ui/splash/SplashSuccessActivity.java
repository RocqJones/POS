package com.extrainch.pos.ui.splash;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.extrainch.pos.R;
import com.extrainch.pos.databinding.ActivitySplashSuccessBinding;

public class SplashSuccessActivity extends AppCompatActivity {

    private ActivitySplashSuccessBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashSuccessBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Glide.with(this).asGif().load(R.drawable.checkedited).error(R.drawable.error_img).into(binding.gifCheck);
    }
}
