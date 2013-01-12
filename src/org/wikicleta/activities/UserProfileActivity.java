package org.wikicleta.activities;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import org.wikicleta.R;
import org.wikicleta.adapters.PagerAdapter;
import org.wikicleta.helpers.SlidingMenuAndActionBarHelper;
import org.wikicleta.models.Route;
import org.wikicleta.routes.fragments.ActivityFragment;
import org.wikicleta.routes.fragments.NotificationsFragment;
import org.wikicleta.routes.fragments.ProfileFragment;
import org.wikicleta.routes.services.RoutesService;
import org.wikicleta.routes.services.RoutesServiceListener;
import org.wikicleta.routes.services.ServiceConstructor;
import org.wikicleta.routes.services.ServiceListener;

import android.app.Service;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.TabHost;
import android.widget.TabHost.TabContentFactory;

/**
  * Demonstrates combining a TabHost with a ViewPager to implement a tab UI
  * that switches between tabs and also allows the user to perform horizontal
  * flicks to move between the tabs.
***/
public class UserProfileActivity extends FragmentActivity implements ServiceListener, RoutesServiceListener, TabHost.OnTabChangeListener, ViewPager.OnPageChangeListener {
	
	public enum ViewStatus {BLOCK, UNBLOCK};
	public ViewStatus currentViewStatus;
	
	private TabHost tabHost;
	private ViewPager mViewPager;
	private HashMap<String, TabInfo> mapTabInfo = new HashMap<String, UserProfileActivity.TabInfo>();
	private PagerAdapter pagerAdapter;
	//Service
	public RoutesService theService;
	ServiceConstructor serviceInitializator;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_wikicleta);

        setContentView(R.layout.activity_profile);
        
        this.initialiseTabHost(savedInstanceState);
        if (savedInstanceState != null) {
        	tabHost.setCurrentTabByTag(savedInstanceState.getString("tab")); //set the tab as per the saved state
        }
        // Intialise ViewPager
        this.intialiseViewPager();
        
        SlidingMenuAndActionBarHelper.load(this);
    }
    
    
    /** (non-Javadoc)
     * @see android.support.v4.app.FragmentActivity#onSaveInstanceState(android.os.Bundle)
     */
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("tab", tabHost.getCurrentTabTag()); //save the tab selected
        super.onSaveInstanceState(outState);
    }

    /**
     * Initialise ViewPager
     */
    private void intialiseViewPager() {
		List<Fragment> fragments = new Vector<Fragment>();
		fragments.add(Fragment.instantiate(this, ProfileFragment.class.getName()));
		fragments.add(Fragment.instantiate(this, ActivityFragment.class.getName()));
		fragments.add(Fragment.instantiate(this, NotificationsFragment.class.getName()));
		
		this.pagerAdapter  = new PagerAdapter(super.getSupportFragmentManager(), fragments);
		this.mViewPager = (ViewPager) this.findViewById(R.id.pager);
		this.mViewPager.setAdapter(this.pagerAdapter);
		this.mViewPager.setOnPageChangeListener(this);
    }

	/**
	 * Initialise the Tab Host
	 */
	private void initialiseTabHost(Bundle args) {
		tabHost = (TabHost)findViewById(android.R.id.tabhost);
		tabHost.setup();
        TabInfo tabInfo = null;
        addTab(this, this.tabHost, this.tabHost.newTabSpec("profile").setIndicator(getString(R.string.user_profile_tab)),
        		( tabInfo = new TabInfo("profile", ProfileFragment.class, args)));
        this.mapTabInfo.put(tabInfo.tag, tabInfo);
        addTab(this, this.tabHost, this.tabHost.newTabSpec("activity").setIndicator(getString(R.string.user_activity_tab)),
        		( tabInfo = new TabInfo("activity", ActivityFragment.class, args)));
        this.mapTabInfo.put(tabInfo.tag, tabInfo);
        addTab(this, this.tabHost, this.tabHost.newTabSpec("notifications").setIndicator(getString(R.string.user_notifications_tab)),
        		( tabInfo = new TabInfo("notifications", NotificationsFragment.class, args)));
        this.mapTabInfo.put(tabInfo.tag, tabInfo);
        // Default to first tab
        //this.onTabChanged("Tab1");
        //
        tabHost.setOnTabChangedListener(this);
	}

	/**
	 * Add Tab content to the Tabhost
	 * @param activity
	 * @param tabHost
	 * @param tabSpec
	 * @param clss
	 * @param args
	 */
	private static void addTab(UserProfileActivity activity, TabHost tabHost, TabHost.TabSpec tabSpec, TabInfo tabInfo) {
		// Attach a Tab view factory to the spec
		tabSpec.setContent(activity.new TabFactory(activity));
        tabHost.addTab(tabSpec);
	}

	/** (non-Javadoc)
	 * @see android.widget.TabHost.OnTabChangeListener#onTabChanged(java.lang.String)
	 */
	public void onTabChanged(String tag) {
		//TabInfo newTab = this.mapTabInfo.get(tag);
		int pos = tabHost.getCurrentTab();
		this.mViewPager.setCurrentItem(pos);
    }

	/* (non-Javadoc)
	 * @see android.support.v4.view.ViewPager.OnPageChangeListener#onPageScrolled(int, float, int)
	 */
	@Override
	public void onPageScrolled(int position, float positionOffset,
			int positionOffsetPixels) {

	}

	/* (non-Javadoc)
	 * @see android.support.v4.view.ViewPager.OnPageChangeListener#onPageSelected(int)
	 */
	@Override
	public void onPageSelected(int position) {
		this.tabHost.setCurrentTab(position);
	}

	/* (non-Javadoc)
	 * @see android.support.v4.view.ViewPager.OnPageChangeListener#onPageScrollStateChanged(int)
	 */
	@Override
	public void onPageScrollStateChanged(int state) {
		// TODO Auto-generated method stub

	}
    
	@Override
	public void onStart() {
		super.onStart();
		serviceInitializator = new ServiceConstructor(this);
        serviceInitializator.start(RoutesService.class);
	}
	
	@Override
	public void onStop() {
		super.onStop();
        serviceInitializator.stop();
	}

	@Override
	public void afterServiceConnected(Service service) {
		if(service instanceof RoutesService) {
			this.theService = (RoutesService) service;
			theService.uploadStagedRoutes();
		}
	}

	@Override
	public void routeDidUpload(Route route) {
		// TODO Auto-generated method stub	
	}

	@Override
	public void routeDidNotUpload(Route route) {
		// TODO Auto-generated method stub
		
	}
	

	@Override
	public void shouldBlockView() {
		this.currentViewStatus = ViewStatus.BLOCK;

		runOnUiThread(new Runnable() {
		     public void run() {
		 		ActivityFragment fragment = (ActivityFragment) pagerAdapter.getItem(1);
				fragment.drawView();
		    }
		});
	}
	
	@Override
	public void shouldUnblockView() {
		this.currentViewStatus = ViewStatus.UNBLOCK;
		runOnUiThread(new Runnable() {
		     public void run() {
		 		ActivityFragment fragment = (ActivityFragment) pagerAdapter.getItem(1);
				fragment.drawView();
		    }
		});
	}
	
	
	/**
	 *
	 * @author mwho
	 * Maintains extrinsic info of a tab's construct
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

	    /** (non-Javadoc)
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