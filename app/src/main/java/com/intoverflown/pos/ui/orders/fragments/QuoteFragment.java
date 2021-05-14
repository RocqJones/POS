package com.intoverflown.pos.ui.orders.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.intoverflown.pos.databinding.FragmentQuoteBinding;

public class QuoteFragment extends Fragment {

    private FragmentQuoteBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentQuoteBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
}