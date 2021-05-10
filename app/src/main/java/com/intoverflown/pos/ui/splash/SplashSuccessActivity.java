package com.intoverflown.pos.ui.splash;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.intoverflown.pos.R;
import com.intoverflown.pos.databinding.ActivitySplashSuccessBinding;

public class SplashSuccessActivity extends AppCompatActivity {

    private ActivitySplashSuccessBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashSuccessBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Glide.with(this).asGif().load(R.drawable.check).error(R.drawable.error_img).into(binding.gifCheck);
    }
}
