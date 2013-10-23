package org.wikicleta.activities;

import java.util.ArrayList;
import java.util.HashMap;
import org.interfaces.FragmentNotificationsInterface;
import org.wikicleta.R;
import org.wikicleta.activities.common.TabbedActivity;
import org.wikicleta.fragments.favorites.ParkingsFragment;
import org.wikicleta.fragments.favorites.RoutesFragment;
import org.wikicleta.fragments.favorites.TipsFragment;
import org.wikicleta.fragments.favorites.WorkshopsFragment;
import org.wikicleta.models.LightPOI;
import org.wikicleta.routing.Favorites;
import android.os.Bundle;
import android.support.v4.app.Fragment;

public class FavoritesActivity extends TabbedActivity {
	
	public HashMap<String, ArrayList<LightPOI>> favorites;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.string.favorites_in_root, R.layout.activity_favorites);
	}
	
	public void buildTabs() {
		String [] tmpTabs = { getResources().getString(R.string.favorites_menu_routes),
							  getResources().getString(R.string.favorites_menu_parkings),
							  getResources().getString(R.string.favorites_menu_tips),
							  getResources().getString(R.string.favorites_menu_workshops) };
		this.tabs = tmpTabs;
	}
	
	protected void initializeFragments() {
		super.initializeFragments();
        fragments.add(new RoutesFragment("Route"));
        fragments.add(new ParkingsFragment("Parking"));
        fragments.add(new TipsFragment("Tip"));
        fragments.add(new WorkshopsFragment("Workshop"));
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		this.favorites = null;
	}
	
	public void fetchUserFavorites() {
		if(this.favorites == null || this.favorites.isEmpty()) {
			Favorites.List markedInvestigator = new Favorites().new List(this);
			markedInvestigator.execute();
		} else {
			this.onSuccess(favorites);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onSuccess(Object collection) {
		this.favorites = (HashMap<String, ArrayList<LightPOI>>) collection;
		
		for(Fragment fragment : this.fragments) {
			((FragmentNotificationsInterface) fragment).notifyDataFetched();
		}
	}

	@Override
	public void onFailed(Object message) {
		
	}
	
	@Override
	public void onFailed() {
	}

}
