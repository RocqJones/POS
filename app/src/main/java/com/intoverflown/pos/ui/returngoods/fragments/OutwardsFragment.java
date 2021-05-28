package com.intoverflown.pos.ui.returngoods.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.intoverflown.pos.databinding.FragmentOutwardsBinding;

public class OutwardsFragment extends Fragment {

    private FragmentOutwardsBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentOutwardsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
}
