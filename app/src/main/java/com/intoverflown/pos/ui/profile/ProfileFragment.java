package com.intoverflown.pos.ui.profile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.intoverflown.pos.databinding.FragmentProfileBinding;
import com.intoverflown.pos.ui.profile.addmerchant.AddMerchantActivity;

import static android.content.Context.MODE_PRIVATE;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    public SharedPreferences preferences;
    public String SHARED_PREF_NAME = "pos_pref";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);

        preferences = this.getContext().getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        setDataToProfile();

        binding.profileAddMerchant.setOnClickListener(v -> {
            Intent i = new Intent(ProfileFragment.this.getContext(), AddMerchantActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        });

        return binding.getRoot();
    }

    private void setDataToProfile() {
        String fullName = preferences.getString("FirstName", "FirstName")
                + " " +preferences.getString("LastName", "LastName");
        String userEmail = preferences.getString("Username", "Username");

        binding.profileName.setText(fullName);
        binding.profileEmail.setText(userEmail);
    }
}
