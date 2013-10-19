package org.wikicleta.activities;

import java.util.ArrayList;
import org.interfaces.FragmentNotificationsInterface;
import org.wikicleta.R;
import org.wikicleta.activities.common.TabbedActivity;
import org.wikicleta.fragments.activities.DraftsFragment;
import org.wikicleta.fragments.activities.RecentsFragment;
import org.wikicleta.models.LightPOI;
import org.wikicleta.routing.Ownerships;

import android.os.Bundle;
import android.support.v4.app.Fragment;

public class ActivitiesActivity extends TabbedActivity {
	
	public ArrayList<LightPOI> activities;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.string.activities_in_root, R.layout.activity_activities);
	}
	
	public void buildTabs() {
		tabs = new ArrayList<String>();
		tabs.add(this.getResources().getString(R.string.activities_list));
		tabs.add(this.getResources().getString(R.string.activities_drafts));
	}
	
	protected ArrayList<Fragment> listedFragments() {
		ArrayList<Fragment> fragments = new ArrayList<Fragment>();
        fragments.add(new RecentsFragment());
        fragments.add(new DraftsFragment());
        return fragments;
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		this.activities = null;
	}
	
	public void fetchUserActivities() {
		if(this.activities == null || this.activities.isEmpty()) {
			Ownerships.List markedInvestigator = new Ownerships().new List(this);
			markedInvestigator.execute();
		} else {
			this.onFinished(activities);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onFinished(Object collection) {
		this.activities = (ArrayList<LightPOI>) collection;
		FragmentNotificationsInterface fragment = (FragmentNotificationsInterface) mAdapter.getItem(viewPager.getCurrentItem());
		fragment.notifyUINeedsUpdate();
	}

	@Override
	public void onFailed() {
		
	}

}