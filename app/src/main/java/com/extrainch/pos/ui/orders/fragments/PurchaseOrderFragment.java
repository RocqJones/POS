package com.extrainch.pos.ui.orders.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.extrainch.pos.databinding.FragmentPurchaseOrderBinding;
import com.extrainch.pos.ui.orders.data.OrderRemoteData;
import com.extrainch.pos.ui.orders.data.PurchaseRemoteData;
import com.extrainch.pos.ui.orders.postorder.AddPurchaseOrderActivity;

import java.util.ArrayList;
import java.util.List;

public class PurchaseOrderFragment extends Fragment {

    private FragmentPurchaseOrderBinding binding;

    String token;
    public SharedPreferences preferences;
    public String SHARED_PREF_NAME = "pos_pref";
    public String KEY_TOKEN = "Token";

    List<PurchaseRemoteData> purchaseRemoteData;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPurchaseOrderBinding.inflate(inflater, container, false);

        purchaseRemoteData = new ArrayList<>();

        binding.fab.setOnClickListener(v -> {
            Intent i = new Intent(this.getContext(), AddPurchaseOrderActivity.class);
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

        return binding.getRoot();
    }
}