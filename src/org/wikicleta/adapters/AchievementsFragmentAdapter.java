package org.wikicleta.adapters;

import java.util.ArrayList;

import org.wikicleta.R;
import org.wikicleta.fragments.achievements.BasicAchievementFragment;

import com.viewpagerindicator.IconPagerAdapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class AchievementsFragmentAdapter extends FragmentPagerAdapter implements IconPagerAdapter {

    protected ArrayList<BasicAchievementFragment> achievements;

    public AchievementsFragmentAdapter(FragmentManager fm) {
        super(fm);
        achievements = new ArrayList<BasicAchievementFragment>();
        achievements.add(new BasicAchievementFragment("Climber"));
        achievements.add(new BasicAchievementFragment("Messenger"));
        achievements.add(new BasicAchievementFragment("Nocturne"));
        achievements.add(new BasicAchievementFragment("Multimodal"));
        achievements.add(new BasicAchievementFragment("Plain"));
        achievements.add(new BasicAchievementFragment("High"));

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
