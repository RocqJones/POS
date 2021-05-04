package com.intoverflown.pos.ui.inventory.main;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.intoverflown.pos.R;
import com.intoverflown.pos.ui.inventory.fragments.CategoryFragment;
import com.intoverflown.pos.ui.inventory.fragments.ProductFragment;
import com.intoverflown.pos.ui.inventory.fragments.SupplierFragment;

import org.jetbrains.annotations.NotNull;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.inventory_tab_1,
            R.string.inventory_tab_2, R.string.inventory_tab_3};
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
                fragment = new CategoryFragment();
                break;

            case 1:
                fragment = new SupplierFragment();
                break;

            case 2:
                fragment = new ProductFragment();
        }

        return fragment;
        // return PlaceholderFragment.newInstance(position + 1);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_TITLES[position]);
    }

    @Override
    public int getCount() {
        // Show 3 total pages.
        return 3;
    }
}