package com.extrainch.pos.ui.printer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.extrainch.pos.databinding.FragmentPrinterBinding;
import com.extrainch.pos.ui.orders.data.PurchaseRemoteData;
import com.extrainch.pos.ui.printer.addpurchasereceipt.AddPurchaseReceiptActivity;

import java.util.ArrayList;
import java.util.List;

public class PrinterFragment extends Fragment {

    private FragmentPrinterBinding binding;

    public SharedPreferences preferences;
    public String SHARED_PREF_NAME = "pos_pref";
    public SharedPreferences.Editor editor;
    public String MERCHANT_ID = "merchantId";

    List<PurchaseRemoteData> purchaseData;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPrinterBinding.inflate(inflater ,container, false);

        binding.fab.setOnClickListener(v -> {
            Intent i = new Intent(PrinterFragment.this.getContext(), AddPurchaseReceiptActivity.class);
            startActivity(i);
        });

        purchaseData = new ArrayList<>();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getContext());
        binding.recyclerView.setLayoutManager(linearLayoutManager);

//        if (linearLayoutManager.getItemCount() == 0) {
//
//        }
        if (purchaseData.isEmpty()) {
            binding.recyclerView.setVisibility(View.GONE);
            binding.noData.setVisibility(View.VISIBLE);
        } else {
            binding.recyclerView.setVisibility(View.VISIBLE);
            binding.noData.setVisibility(View.GONE);
        }

        return binding.getRoot();
    }
}
