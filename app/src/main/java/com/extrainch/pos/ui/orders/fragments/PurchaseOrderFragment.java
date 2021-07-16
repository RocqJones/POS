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
import com.extrainch.pos.ui.orders.postorder.AddPurchaseOrderActivity;

public class PurchaseOrderFragment extends Fragment {

    private FragmentPurchaseOrderBinding binding;

    String token;
    public SharedPreferences preferences;
    public String SHARED_PREF_NAME = "pos_pref";
    public String KEY_TOKEN = "Token";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPurchaseOrderBinding.inflate(inflater, container, false);

        binding.fab.setOnClickListener(v -> {
            Intent i = new Intent(this.getContext(), AddPurchaseOrderActivity.class);
            startActivity(i);
        });

        return binding.getRoot();
    }
}