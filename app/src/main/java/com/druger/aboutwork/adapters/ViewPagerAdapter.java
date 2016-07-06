package com.druger.aboutwork.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.druger.aboutwork.fragments.AccountFragment;
import com.druger.aboutwork.fragments.CompaniesFragment;
import com.druger.aboutwork.fragments.RatingsFragment;

/**
 * Created by druger on 06.07.2016.
 */
public class ViewPagerAdapter extends FragmentPagerAdapter {
    private static final int NUM_ITEMS = 3;

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return CompaniesFragment.newInstance(0);
            case 1:
                return RatingsFragment.newInstance(1);
            case 2:
                return AccountFragment.newInstance(2);
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return NUM_ITEMS;
    }
}
