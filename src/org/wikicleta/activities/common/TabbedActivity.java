package org.wikicleta.activities.common;

import java.util.ArrayList;
import org.interfaces.CollectionFetchedListener;
import org.wikicleta.R;
import org.wikicleta.activities.RootActivity;
import org.wikicleta.adapters.TabsPagerAdapter;
import org.wikicleta.common.AppBase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.app.ActionBar.Tab;

public abstract class TabbedActivity extends SherlockFragmentActivity implements ActionBar.TabListener, CollectionFetchedListener {

	protected ViewPager viewPager;
	protected TabsPagerAdapter mAdapter;
	protected ActionBar actionBar;
		
	// Tab titles
	protected ArrayList<String> tabs;
	
	
	protected void onCreate(Bundle savedInstanceState, int title, int layout) {
		super.onCreate(savedInstanceState);
		setContentView(layout);
		loadActionBarWith(title);
		
		buildTabs();
		loadTabs();
		loadAdditionalControls();
	}
	
	protected void loadAdditionalControls() {
		viewPager.setPageMargin(10);
	    viewPager.setPageMarginDrawable(R.color.wikicleta_blue);
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
	
	protected void buildTabs() {
	}
	
	protected void loadTabs() {
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
	}
	
	protected ArrayList<Fragment> listedFragments() {
		return new ArrayList<Fragment>();
	}
	
	protected void loadActionBarWith(int identifier) {
		// Initilization
		viewPager = (ViewPager) findViewById(R.id.pager);
		actionBar = getSupportActionBar();
		mAdapter = new TabsPagerAdapter(getSupportFragmentManager(), listedFragments());
	
		viewPager.setAdapter(mAdapter);
		actionBar.setHomeButtonEnabled(false);
		
		View actionBarView = this.getLayoutInflater().inflate(R.layout.navbar_layout, null);
		TextView navbarTitle = ((TextView) actionBarView.findViewById(R.id.navbar_title));
		navbarTitle.setTypeface(AppBase.getTypefaceStrong());
		navbarTitle.setText(identifier);
		
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setDisplayShowTitleEnabled(false);

		actionBar.setCustomView(actionBarView);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);  
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

}
