package com.intoverflown.pos.ui.returngoods.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.intoverflown.pos.databinding.FragmentOutwardsBinding;
import com.intoverflown.pos.ui.returngoods.postreturns.AddOutwardsActivity;

public class OutwardsFragment extends Fragment {

    private FragmentOutwardsBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentOutwardsBinding.inflate(inflater, container, false);

        binding.fab.setOnClickListener(v -> {
            Intent i = new Intent(this.getContext(), AddOutwardsActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        });
        return binding.getRoot();
    }
}
