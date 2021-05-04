package com.intoverflown.pos.ui.inventory.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.intoverflown.pos.databinding.ProductCategoryFragmentBinding;
import com.intoverflown.pos.ui.inventory.addproduct.AddProductActivity;
import com.intoverflown.pos.ui.inventory.category.CategoryActivity;

public class CategoryFragment extends Fragment {

    ProductCategoryFragmentBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = ProductCategoryFragmentBinding.inflate(inflater, container, false);

        binding.fab.setOnClickListener(v -> intentToAddCategory());

        binding.categoryAdd.setOnClickListener(v -> intentToAddCategory());

        binding.categoryAddIc.setOnClickListener(v -> intentToAddCategory());

        return binding.getRoot();
    }

    private void intentToAddCategory() {
        Intent j = new Intent(CategoryFragment.this.getContext(), CategoryActivity.class);
        j.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(j);
    }
}