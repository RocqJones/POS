package com.intoverflown.pos.ui.customers;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.intoverflown.pos.databinding.FragmentCustomersBinding;
import com.intoverflown.pos.ui.customers.addcustomers.AddCustomerActivity;

public class CustomersFragment extends Fragment {

    private FragmentCustomersBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCustomersBinding.inflate(inflater, container, false);

        binding.fab.setOnClickListener(v -> {
            Intent i = new Intent(this.getContext(), AddCustomerActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        });

        return binding.getRoot();
    }
}