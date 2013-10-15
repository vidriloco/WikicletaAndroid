package org.wikicleta.adapters;

import java.util.ArrayList;
import java.util.List;

import org.wikicleta.fragments.favorites.ParkingsFragment;
import org.wikicleta.fragments.favorites.RoutesFragment;
import org.wikicleta.fragments.favorites.TipsFragment;
import org.wikicleta.fragments.favorites.WorkshopsFragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class TabsPagerAdapter extends FragmentPagerAdapter {
	
	private List<Fragment> fragments;

	
    public TabsPagerAdapter(FragmentManager fm) {
        super(fm);
        fragments = new ArrayList<Fragment>();
        fragments.add(new RoutesFragment("Route"));
        fragments.add(new ParkingsFragment("Parking"));
        fragments.add(new TipsFragment("Tip"));
        fragments.add(new WorkshopsFragment("Workshop"));
    }
 
    @Override
    public Fragment getItem(int index) {
    	return fragments.get(index);
    }
 
    @Override
    public int getCount() {
        return 4;
    }
 
}