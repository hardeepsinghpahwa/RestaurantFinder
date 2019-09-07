package com.example.restaurantfinder;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.restaurantfinder.ViewPagerFrags.NonVeg;
import com.example.restaurantfinder.ViewPagerFrags.Veg;


public class ViewPagerAdapter extends FragmentPagerAdapter {

    int tabs;


    public ViewPagerAdapter(FragmentManager fm, int tabs) {
        super(fm);
        this.tabs=tabs;
    }

    @Override
    public Fragment getItem(int i) {

        switch (i) {
            case 0:
                Veg veg = new Veg();
                return veg;

            case 1:
                NonVeg nonVeg=new NonVeg();
                return nonVeg;


            default:
                return null;

        }
    }

    @Override
    public int getCount() {
        return tabs;
    }
}
