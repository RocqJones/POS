package com.extrainch.pos.ui.orders.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.extrainch.pos.databinding.FragmentQuoteBinding;

public class QuoteFragment extends Fragment {

    private FragmentQuoteBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentQuoteBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
}