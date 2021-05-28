package com.intoverflown.pos.ui.goodreturn;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.intoverflown.pos.MainActivity;
import com.intoverflown.pos.databinding.ActivityInwardsOutwardsBinding;
import com.intoverflown.pos.ui.goodreturn.ui.main.SectionsPagerAdapter;

public class InwardsOutwardsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityInwardsOutwardsBinding binding = ActivityInwardsOutwardsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        /* Start - ViewPager */
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = binding.viewPager;
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = binding.tabs;
        tabs.setupWithViewPager(viewPager);
        /* End - ViewPager */

        binding.title.setOnClickListener(v -> {
            Intent i = new Intent(InwardsOutwardsActivity.this, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        });
    }
}