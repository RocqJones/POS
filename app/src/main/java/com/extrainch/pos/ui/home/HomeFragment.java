package com.extrainch.pos.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.extrainch.pos.databinding.FragmentHomeBinding;
import com.extrainch.pos.ui.category.CategoryActivity;
import com.extrainch.pos.ui.inventory.InventoryActivity;
import com.extrainch.pos.ui.merchantbranch.MerchantBranchActivity;
import com.extrainch.pos.ui.orders.OrdersActivity;
import com.extrainch.pos.ui.products.ProductActivity;
import com.extrainch.pos.ui.purchase.PurchaseActivity;
import com.extrainch.pos.ui.returngoods.InwardsOutwardsActivity;
import com.extrainch.pos.ui.supplier.SupplierActivity;

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
            Intent i = new Intent(HomeFragment.this.getContext(), InventoryActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        });

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

        binding.homeProduct.setOnClickListener(v -> {
            Intent p = new Intent(HomeFragment.this.getContext(), ProductActivity.class);
            p.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(p);
        });

        binding.homeCategory.setOnClickListener(v -> {
            Intent cT = new Intent(HomeFragment.this.getContext(), CategoryActivity.class);
            cT.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(cT);
        });

        binding.homeSupplier.setOnClickListener(v -> {
            Intent sP = new Intent(HomeFragment.this.getContext(), SupplierActivity.class);
            sP.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(sP);
        });

        binding.homePurchase.setOnClickListener(v -> {
            Intent pC = new Intent(HomeFragment.this.getContext(), PurchaseActivity.class);
            pC.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(pC);
        });
    }
}