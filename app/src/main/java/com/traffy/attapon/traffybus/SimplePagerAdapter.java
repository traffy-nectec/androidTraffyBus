package com.traffy.attapon.traffybus;

/**
 * Created by Attapon on 2/5/2559.
 */
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

class SimplePagerAdapter extends FragmentStatePagerAdapter {

    public static final String ARGS_POSITION = "name";
    public static final int NUM_PAGES = 2;
    public static final String[] BOUND = {"O","O"};

    public SimplePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        Fragment fragment = new SimpleFragment();
        Bundle args = new Bundle();
        args.putInt(ARGS_POSITION, position);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public int getCount() {
        return NUM_PAGES;
    }

    @Override
    public CharSequence getPageTitle(int position) {
       // return "" + (BOUND[position]);
        return null;
    }

}
