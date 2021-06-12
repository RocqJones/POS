package com.intoverflown.pos.ui.merchantbranch;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.intoverflown.pos.databinding.ActivityMerchantBranchBinding;

public class MerchantBranchActivity extends AppCompatActivity {

    private ActivityMerchantBranchBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMerchantBranchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}