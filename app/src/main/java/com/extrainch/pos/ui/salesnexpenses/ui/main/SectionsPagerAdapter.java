package com.extrainch.pos.ui.salesnexpenses.ui.main;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.extrainch.pos.R;
import com.extrainch.pos.ui.salesnexpenses.fragments.ExpensesFragment;
import com.extrainch.pos.ui.salesnexpenses.fragments.SalesFragment;

import org.jetbrains.annotations.NotNull;

public class SectionsPagerAdapter extends FragmentPagerAdapter {

    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.tab_text_1,
            R.string.tab_text_2};
    private final Context mContext;

    public SectionsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @NotNull
    @Override
    public Fragment getItem(int position) {
        // call fragments here
        Fragment fragment = null;

        switch (position) {
            case 0:
                fragment = new SalesFragment();
                break;

            case 1:
                fragment = new ExpensesFragment();
        }

        return fragment;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_TITLES[position]);
    }

    @Override
    public int getCount() {
        // total pages.
        return 2;
    }
}
