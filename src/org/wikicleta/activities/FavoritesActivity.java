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

	@Override
	public void onSuccess(Object duty) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onFailed(Object message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onFailed() {
		// TODO Auto-generated method stub
		
	}
	
	/*public HashMap<String, ArrayList<LightPOI>> favorites;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.string.favorites_in_root, R.layout.activity_favorites);
	}
	
	protected void buildTabs() {
		tabs = new ArrayList<String>();
		tabs.add(this.getResources().getString(R.string.favorites_menu_routes));
		tabs.add(this.getResources().getString(R.string.favorites_menu_parkings));
		tabs.add(this.getResources().getString(R.string.favorites_menu_tips));
		tabs.add(this.getResources().getString(R.string.favorites_menu_workshops));
	}
	
	protected ArrayList<Fragment> listedFragments() {
		ArrayList<Fragment> fragments = new ArrayList<Fragment>();
        fragments.add(new RoutesFragment("Route"));
        fragments.add(new ParkingsFragment("Parking"));
        fragments.add(new TipsFragment("Tip"));
        fragments.add(new WorkshopsFragment("Workshop"));
        return fragments;
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
		FragmentNotificationsInterface fragment = (FragmentNotificationsInterface) mAdapter.getItem(viewPager.getCurrentItem());
		fragment.notifyUINeedsUpdate();
	}

	@Override
	public void onFailed(Object message) {
		
	}
	
	@Override
	public void onFailed() {
	}
	 */
}
