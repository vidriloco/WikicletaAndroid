package org.wikicleta.activities;

import java.util.ArrayList;
import org.interfaces.FragmentNotificationsInterface;
import org.wikicleta.R;
import org.wikicleta.activities.common.TabbedActivity;
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
		String [] tmpTabs = { this.getString(R.string.activities_list), this.getString(R.string.activities_drafts) };
		tabs = tmpTabs;
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
		} 
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
		// TODO Auto-generated method stub
	}

}