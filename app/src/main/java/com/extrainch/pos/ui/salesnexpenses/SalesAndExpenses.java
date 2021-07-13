package com.extrainch.pos.ui.salesnexpenses;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.extrainch.pos.MainActivity;
import com.extrainch.pos.databinding.ActivitySalesAndExpensesBinding;
import com.extrainch.pos.ui.salesnexpenses.ui.main.SectionsPagerAdapter;

public class SalesAndExpenses extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivitySalesAndExpensesBinding binding = ActivitySalesAndExpensesBinding.inflate(getLayoutInflater());
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
            Intent j = new Intent(SalesAndExpenses.this, MainActivity.class);
            j.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(j);
        });
    }
}
