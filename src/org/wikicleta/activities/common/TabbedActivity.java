package org.wikicleta.activities.common;

import java.util.ArrayList;

import org.interfaces.FragmentNotificationsInterface;
import org.interfaces.RemoteFetchingDutyListener;
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

public abstract class TabbedActivity extends SherlockFragmentActivity implements ActionBar.TabListener, RemoteFetchingDutyListener {

	protected ViewPager viewPager;
	protected TabsPagerAdapter mAdapter;
	protected ActionBar actionBar;
	protected String [] tabs;
	protected ArrayList<Fragment> fragments;
	
	protected int title = -1;
	
	protected void onCreate(Bundle savedInstanceState, int title, int layout) {
		super.onCreate(savedInstanceState);
		AppBase.currentActivity = this;

		setContentView(layout);
		this.title = title;
		
		if(title == -1)
			title = this.getIntent().getIntExtra("title", 0);
		
		
		initialiseNavBarWithIdentifier(title);
		initialiseViewPager();
		loadAdditionalControls();

		buildTabs();
		loadTabs();

		int fragment = this.getIntent().getIntExtra("fragment", 0);
		this.setActiveFragment(fragment);
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putInt("fragment", viewPager.getCurrentItem()); 
		outState.putInt("title", title); 
		super.onSaveInstanceState(outState);
	}
	
	public void setActiveFragment(int pos) {
		this.viewPager.setCurrentItem(pos);
		this.actionBar.setSelectedNavigationItem(pos);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		overridePendingTransition(R.anim.right_to_left, R.anim.fade_to_black);
	}
	
	protected void loadAdditionalControls() {
		ImageView returnIcon = (ImageView) this.findViewById(R.id.return_button);
		returnIcon.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				AppBase.launchActivity(RootActivity.class);
				finish();
			}
			
		});
	}
	
	public void buildTabs() {
		
	}
	
	protected void loadTabs() {
		actionBar.removeAllTabs();
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
	
	protected void initializeFragments() {
		fragments = new ArrayList<Fragment>();
	}
	
	private void initialiseViewPager() {
		initializeFragments();
		mAdapter = new TabsPagerAdapter(getSupportFragmentManager(), fragments);
		viewPager = (ViewPager) this.findViewById(R.id.pager);
		viewPager.setAdapter(mAdapter);
		viewPager.setPageMarginDrawable(R.color.wikicleta_blue);
		viewPager.setPageMargin(10);
		viewPager.setOffscreenPageLimit(1);	    
		viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
		
		    @Override
		    public void onPageSelected(int position) {
		        actionBar.setSelectedNavigationItem(position);
		    }
		
		    @Override
		    public void onPageScrolled(int position, float arg1, int arg2) {
		    }
		
		    @Override
		    public void onPageScrollStateChanged(int arg0) {
		    }
		});
		
	}
	
	private void initialiseNavBarWithIdentifier(int identifier) {
		View actionBarView = this.getLayoutInflater().inflate(R.layout.navbar_layout, null);
		TextView navbarTitle = ((TextView) actionBarView.findViewById(R.id.navbar_title));
		navbarTitle.setTypeface(AppBase.getTypefaceStrong());
		navbarTitle.setText(identifier);
		
		actionBar = getSupportActionBar();
		actionBar.setHomeButtonEnabled(false);
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setDisplayShowTitleEnabled(false);

		actionBar.setCustomView(actionBarView);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);  
	}
	
	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		viewPager.setCurrentItem(tab.getPosition());
		((FragmentNotificationsInterface) mAdapter.getItem(tab.getPosition())).notifyIsNowVisible();
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
	}

}
