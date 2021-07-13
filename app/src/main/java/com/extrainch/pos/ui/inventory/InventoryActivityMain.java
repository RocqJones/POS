package com.extrainch.pos.ui.inventory;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.extrainch.pos.MainActivity;
import com.extrainch.pos.databinding.ActivityInventoryMainBinding;
import com.extrainch.pos.ui.inventory.main.SectionsPagerAdapter;

public class InventoryActivityMain extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityInventoryMainBinding binding = ActivityInventoryMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        /* Start - ViewPager */
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this,
                getSupportFragmentManager());

        ViewPager viewPager = binding.viewPager;

        viewPager.setAdapter(sectionsPagerAdapter);

        TabLayout tabs = binding.tabs;

        tabs.setupWithViewPager(viewPager);
        /* End - ViewPager */

        binding.titleTabbedFragment.setOnClickListener(v -> {
            Intent j = new Intent(InventoryActivityMain.this, MainActivity.class);
            j.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(j);
        });
    }
}
