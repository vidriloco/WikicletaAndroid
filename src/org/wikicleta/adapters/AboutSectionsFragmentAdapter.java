package org.wikicleta.adapters;

import java.util.ArrayList;

import org.wikicleta.R;
import org.wikicleta.fragments.about_sections.GraphicsFragment;
import org.wikicleta.fragments.about_sections.AttributionsFragment;
import org.wikicleta.fragments.about_sections.MadeInMexicoFragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.viewpagerindicator.IconPagerAdapter;

public class AboutSectionsFragmentAdapter extends FragmentPagerAdapter implements IconPagerAdapter {

    protected ArrayList<Fragment> achievements;

    public AboutSectionsFragmentAdapter(FragmentManager fm) {
        super(fm);
        achievements = new ArrayList<Fragment>();
        achievements.add(new MadeInMexicoFragment());
        achievements.add(new GraphicsFragment());
        achievements.add(new AttributionsFragment());
    }

    @Override
    public Fragment getItem(int index) {
    	return achievements.get(index);
    }
 
    @Override
    public int getCount() {
        return this.achievements.size();
    }

    @Override
    public int getIconResId(int index) {
      return R.drawable.circle_pager_icon;
    }

}