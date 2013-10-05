package org.wikicleta.activities;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import org.wikicleta.R;
import org.wikicleta.adapters.PagerAdapter;
import org.wikicleta.fragments.user_profile.DraftsFragment;
import org.wikicleta.fragments.user_profile.ProfileFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TabHost;
import android.widget.TabHost.TabContentFactory;

public class UserProfileActivity extends FragmentActivity implements TabHost.OnTabChangeListener,
		ViewPager.OnPageChangeListener {
	private TabHost tabHost;
	private ViewPager mViewPager;
	private HashMap<String, TabInfo> mapTabInfo = new HashMap<String, UserProfileActivity.TabInfo>();
	private PagerAdapter pagerAdapter;
	
	List<String> fragmentsNames;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTheme(R.style.Theme_wikicleta);

		setContentView(R.layout.activity_profile);

		this.initialiseTabHost(savedInstanceState);
		if (savedInstanceState != null) {
			tabHost.setCurrentTabByTag(savedInstanceState.getString("tab")); 
		}

		fragmentsNames = new Vector<String>();
		fragmentsNames.add(ProfileFragment.class.getName());
		fragmentsNames.add(DraftsFragment.class.getName());
		// Intialise ViewPager
		this.intialiseViewPager();


		String fragment = this.getIntent().getStringExtra("fragment");
		if (fragment != null)
			this.setActiveFragment(fragment);

	}

	protected void onSaveInstanceState(Bundle outState) {
		outState.putString("tab", tabHost.getCurrentTabTag()); // save the tab
																// selected
		super.onSaveInstanceState(outState);
	}

	private void intialiseViewPager() {
		Vector<Fragment> fragments = new Vector<Fragment>();
		for (String klass : fragmentsNames) {
			fragments.add(Fragment.instantiate(this, klass));
		}

		this.pagerAdapter = new PagerAdapter(super.getSupportFragmentManager(),
				fragments);
		this.mViewPager = (ViewPager) this.findViewById(R.id.pager);
		this.mViewPager.setAdapter(this.pagerAdapter);
		this.mViewPager.setOnPageChangeListener(this);
	}

	private void initialiseTabHost(Bundle args) {
		tabHost = (TabHost) findViewById(android.R.id.tabhost);
		tabHost.setup();
		TabInfo tabInfo = null;
		addTab(this,
				this.tabHost.newTabSpec("profile").setIndicator(
						getString(R.string.user_profile_tab)),
				(tabInfo = new TabInfo("profile", ProfileFragment.class, args)));
		this.mapTabInfo.put(tabInfo.tag, tabInfo);
		addTab(this,
				this.tabHost.newTabSpec("activity").setIndicator(
						getString(R.string.user_drafts_tab)),
				(tabInfo = new TabInfo("activity", DraftsFragment.class, args)));
		this.mapTabInfo.put(tabInfo.tag, tabInfo);
		tabHost.setOnTabChangedListener(this);
	}

	public void setActiveFragment(String fragmentName) {
		int pos = this.fragmentsNames.indexOf(fragmentName);
		this.mViewPager.setCurrentItem(pos);
		this.tabHost.setCurrentTab(pos);
	}

	/**
	 * Add Tab content to the Tabhost
	 * 
	 * @param activity
	 * @param tabHost
	 * @param tabSpec
	 * @param clss
	 * @param args
	 */
	private void addTab(UserProfileActivity activity, TabHost.TabSpec tabSpec,
			TabInfo tabInfo) {
		// Attach a Tab view factory to the spec
		tabSpec.setContent(activity.new TabFactory(activity));
		tabHost.addTab(tabSpec);
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see android.widget.TabHost.OnTabChangeListener#onTabChanged(java.lang.String)
	 */
	public void onTabChanged(String tag) {
		// TabInfo newTab = this.mapTabInfo.get(tag);
		int pos = tabHost.getCurrentTab();
		this.mViewPager.setCurrentItem(pos);
		
		/*if(pagerAdapter.getItem(pos) instanceof ActivityFragment) {
			ActivityFragment af = (ActivityFragment) pagerAdapter.getItem(pos);
			if(af != null)
				af.drawView();
		}*/
		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.support.v4.view.ViewPager.OnPageChangeListener#onPageScrolled
	 * (int, float, int)
	 */
	@Override
	public void onPageScrolled(int position, float positionOffset,
			int positionOffsetPixels) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.support.v4.view.ViewPager.OnPageChangeListener#onPageSelected
	 * (int)
	 */
	@Override
	public void onPageSelected(int position) {
		this.tabHost.setCurrentTab(position);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.view.ViewPager.OnPageChangeListener#
	 * onPageScrollStateChanged(int)
	 */
	@Override
	public void onPageScrollStateChanged(int state) {
		// TODO Auto-generated method stub

	}

	/**
	 * 
	 * @author mwho Maintains extrinsic info of a tab's construct
	 */
	private class TabInfo {
		protected String tag;
		protected Class<?> clss;
		protected Bundle args;
		protected Fragment fragment;

		TabInfo(String tag, Class<?> clazz, Bundle args) {
			this.tag = tag;
			this.clss = clazz;
			this.args = args;
		}
	}

	/**
	 * A simple factory that returns dummy views to the Tabhost
	 * 
	 * @author mwho
	 */
	class TabFactory implements TabContentFactory {

		private final Context mContext;

		/**
		 * @param context
		 */
		public TabFactory(Context context) {
			mContext = context;
		}

		/**
		 * (non-Javadoc)
		 * 
		 * @see android.widget.TabHost.TabContentFactory#createTabContent(java.lang.String)
		 */
		public View createTabContent(String tag) {
			View v = new View(mContext);
			
			v.setMinimumWidth(0);
			v.setMinimumHeight(0);
			return v;
		}

	}

}