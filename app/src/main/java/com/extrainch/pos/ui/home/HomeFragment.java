package com.extrainch.pos.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.extrainch.pos.databinding.FragmentHomeBinding;
import com.extrainch.pos.ui.inventory.InventoryActivityMain;
import com.extrainch.pos.ui.merchantbranch.MerchantBranchActivity;
import com.extrainch.pos.ui.orders.OrdersActivity;
import com.extrainch.pos.ui.returngoods.InwardsOutwardsActivity;

public class HomeFragment  extends Fragment {

    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        navigationFromHomeUI();

        return binding.getRoot();
    }

    private void navigationFromHomeUI() {
        binding.homeInventory.setOnClickListener(v -> {
            Intent i = new Intent(HomeFragment.this.getContext(), InventoryActivityMain.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        });

//        binding.homeSalesExp.setOnClickListener(v -> {
//            Intent j = new Intent(HomeFragment.this.getContext(), SalesAndExpenses.class);
//            j.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            startActivity(j);
//        });

        binding.homeMerchants.setOnClickListener(v -> {
            Intent k = new Intent(HomeFragment.this.getContext(), MerchantBranchActivity.class);
            k.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(k);
        });

        binding.homeOrders.setOnClickListener(v -> {
            Intent l = new Intent(HomeFragment.this.getContext(), OrdersActivity.class);
            l.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(l);
        });

        binding.homeReturnInOut.setOnClickListener(v -> {
            Intent m = new Intent(HomeFragment.this.getContext(), InwardsOutwardsActivity.class);
            m.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(m);
        });
    }
}