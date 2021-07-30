package com.extrainch.pos.ui.purchase;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.extrainch.pos.MainActivity;
import com.extrainch.pos.databinding.ActivityPurchaseBinding;
import com.extrainch.pos.repository.PurchaseRemoteData;
import com.extrainch.pos.ui.purchase.add.AddPurchaseOrderActivity;
import com.extrainch.pos.ui.supplier.SupplierActivity;

import java.util.ArrayList;
import java.util.List;

public class PurchaseActivity extends AppCompatActivity {

    private ActivityPurchaseBinding binding;

    String token;
    public SharedPreferences preferences;
    public String SHARED_PREF_NAME = "pos_pref";
    public String KEY_TOKEN = "Token";

    List<PurchaseRemoteData> purchaseRemoteData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPurchaseBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.backBtn.setOnClickListener(v -> {
            Intent mA = new Intent(PurchaseActivity.this, MainActivity.class);
            mA.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(mA);
        });

        purchaseRemoteData = new ArrayList<>();

        binding.fab.setOnClickListener(v -> {
            Intent i = new Intent(this, AddPurchaseOrderActivity.class);
            startActivity(i);
        });

        // temp
        if (purchaseRemoteData.isEmpty()) {
            binding.quoteRecyclerView.setVisibility(View.GONE);
            binding.noData.setVisibility(View.VISIBLE);
        } else {
            binding.quoteRecyclerView.setVisibility(View.VISIBLE);
            binding.noData.setVisibility(View.GONE);
        }
    }
}