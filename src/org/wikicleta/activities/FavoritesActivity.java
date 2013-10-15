package org.wikicleta.activities;

import java.util.ArrayList;
import java.util.HashMap;
import org.interfaces.CollectionFetchedListener;
import org.interfaces.FavoriteFragmentInterface;
import org.wikicleta.R;
import org.wikicleta.adapters.TabsPagerAdapter;
import org.wikicleta.common.AppBase;
import org.wikicleta.models.LightPOI;
import org.wikicleta.routing.Favorites;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class FavoritesActivity extends SherlockFragmentActivity implements ActionBar.TabListener, CollectionFetchedListener {

	private ViewPager viewPager;
	private TabsPagerAdapter mAdapter;
	private ActionBar actionBar;
	
	public HashMap<String, ArrayList<LightPOI>> favorites;
	
	// Tab titles
	private ArrayList<String> tabs;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_favorites);
	
		tabs = new ArrayList<String>();
		tabs.add(this.getResources().getString(R.string.favorites_menu_routes));
		tabs.add(this.getResources().getString(R.string.favorites_menu_parkings));
		tabs.add(this.getResources().getString(R.string.favorites_menu_tips));
		tabs.add(this.getResources().getString(R.string.favorites_menu_workshops));
		
		// Initilization
		viewPager = (ViewPager) findViewById(R.id.pager);
		actionBar = getSupportActionBar();
		mAdapter = new TabsPagerAdapter(getSupportFragmentManager());
	
		viewPager.setAdapter(mAdapter);
		actionBar.setHomeButtonEnabled(false);
		
		View actionBarView = this.getLayoutInflater().inflate(R.layout.navbar_layout, null);
		TextView navbarTitle = ((TextView) actionBarView.findViewById(R.id.navbar_title));
		navbarTitle.setTypeface(AppBase.getTypefaceStrong());
		navbarTitle.setText(R.string.favorites_in_root);
		
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setDisplayShowTitleEnabled(false);

		actionBar.setCustomView(actionBarView);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);       
	
		// Adding Tabs
		for (String tabName : tabs) {
			Tab tab = actionBar.newTab();
			View tabView = this.getLayoutInflater().inflate(R.layout.custom_tab_view, null);
			TextView tabTitle = (TextView) tabView.findViewById(R.id.tab_title);
			
			if(tabName.length()>11)
				tabName=tabName.substring(0, 10).concat(" ...");
			
			tabTitle.setText(tabName);
			tabTitle.setTypeface(AppBase.getTypefaceStrong());
			tab.setCustomView(tabView);
			actionBar.addTab(tab.setTabListener(this));
		}
	
		/**
		 * on swiping the viewpager make respective tab selected
		 * */
		viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
		
		    @Override
		    public void onPageSelected(int position) {
		        actionBar.setSelectedNavigationItem(position);
		    }
		
		    @Override
		    public void onPageScrolled(int arg0, float arg1, int arg2) {
		    }
		
		    @Override
		    public void onPageScrollStateChanged(int arg0) {
		    }
		});
		
		ImageView returnIcon = (ImageView) this.findViewById(R.id.return_button);
		returnIcon.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				AppBase.launchActivity(RootActivity.class);
			}
			
		});
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		this.favorites = null;
	}
	
	public void fetchUserFavoritedRoutes() {
		if(this.favorites == null || this.favorites.isEmpty()) {
			Favorites.List markedInvestigator = new Favorites().new List(this);
			markedInvestigator.execute();
		} else {
			this.onFinishedFetchingCollection(favorites);
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		overridePendingTransition(R.anim.right_to_left, R.anim.fade_to_black);
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		viewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
	}

	@Override
	public void onFinishedFetchingCollection(HashMap<String, ArrayList<LightPOI>> collection) {
		this.favorites = collection;
		FavoriteFragmentInterface fragment = (FavoriteFragmentInterface) mAdapter.getItem(viewPager.getCurrentItem());
		fragment.notifyUINeedsUpdate();
	}

	@Override
	public void onFailedFetchingCollection() {
		
	}

}
