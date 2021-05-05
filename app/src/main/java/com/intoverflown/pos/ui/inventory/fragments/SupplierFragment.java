package com.intoverflown.pos.ui.inventory.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.intoverflown.pos.databinding.SupplierFragmentBinding;
import com.intoverflown.pos.ui.inventory.addsupplier.AddSupplierActivity;

public class SupplierFragment extends Fragment {

    SupplierFragmentBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = SupplierFragmentBinding.inflate(inflater, container, false);
        binding.fab.setOnClickListener(v -> {
            Intent i = new Intent(SupplierFragment.this.getContext(), AddSupplierActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        });

        return binding.getRoot();
    }
}