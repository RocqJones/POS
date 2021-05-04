package com.intoverflown.pos.ui.inventory.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.intoverflown.pos.databinding.ProductFragmentBinding;

public class ProductFragment extends Fragment {

    private ProductFragmentBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = ProductFragmentBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }
}