package org.wikicleta.activities;

import java.util.ArrayList;
import org.wikicleta.R;
import org.wikicleta.activities.common.TabbedActivity;
import org.wikicleta.fragments.activities.DraftsFragment;
import org.wikicleta.fragments.activities.RecentsFragment;
import org.wikicleta.interfaces.FragmentNotificationsInterface;
import org.wikicleta.models.LightPOI;
import org.wikicleta.routing.Ownerships;
import android.os.Bundle;
import android.support.v4.app.Fragment;

public class ActivitiesActivity extends TabbedActivity {
	
	public ArrayList<LightPOI> activities;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.string.activities_in_root, R.layout.activity_activities);
	    this.fetchUserActivities();
	}
	
	protected void initializeFragments() {
		super.initializeFragments();
		fragments.add(Fragment.instantiate(this, RecentsFragment.class.getName()));
		fragments.add(Fragment.instantiate(this, DraftsFragment.class.getName()));
	}

	
	public void buildTabs() {
		String [] tmpTabs = { this.getString(R.string.activities_list), this.getString(R.string.activities_drafts) };
		tabs = tmpTabs;
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		this.activities = null;
	}
	
	@Override
	public void onResume() {
	    super.onResume();
	}
	
	public void fetchUserActivities() {
		Ownerships.List markedInvestigator = new Ownerships().new List(this);
		markedInvestigator.execute();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onSuccess(Object collection) {
		this.activities = (ArrayList<LightPOI>) collection;
		for(Fragment fragment : this.fragments) {
			((FragmentNotificationsInterface) fragment).notifyDataFetched();
		}
	}

	@Override
	public void onFailed(Object item) {
		
	}

	@Override
	public void onFailed() {
		for(Fragment fragment : this.fragments)
			((FragmentNotificationsInterface) fragment).notifyDataFailedToLoad();
	}

}