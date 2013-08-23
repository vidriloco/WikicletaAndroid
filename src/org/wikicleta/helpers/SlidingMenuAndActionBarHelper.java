package org.wikicleta.helpers;

import org.wikicleta.R;
import org.wikicleta.activities.NowActivity;
import org.wikicleta.activities.MainMapActivity;
import org.wikicleta.activities.UserProfileActivity;
import org.wikicleta.activities.access.LoginActivity;
import org.wikicleta.activities.trips.TripsListActivity;
import org.wikicleta.common.AppBase;
import org.wikicleta.models.User;

import static com.nineoldandroids.view.ViewPropertyAnimator.animate;
import com.markupartist.android.widget.ActionBar;
import com.markupartist.android.widget.ActionBar.Action;
import com.slidingmenu.lib.SlidingMenu;
import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class SlidingMenuAndActionBarHelper {
	
	public static SlidingMenu loadWithActionBarTitle(Activity activity, String title) {

		// customize the SlidingMenu
        final SlidingMenu menu = new SlidingMenu(activity);
        menu.setMode(SlidingMenu.LEFT);
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
        menu.setFadeDegree(0.35f);
        menu.attachToActivity(activity, SlidingMenu.SLIDING_CONTENT);
        menu.setMenu(R.layout.sliding_menu);        
        
        TextView profileTitle = (TextView) menu.getMenu().findViewById(R.id.profile_title);
        profileTitle.setTypeface(AppBase.getTypefaceStrong());
        
        if(!User.isRegisteredLocally()) {
        	profileTitle.setText(activity.getResources().getString(R.string.no_profile_section));
        	//ImageView profileIcon = (ImageView) menu.getMenu().findViewById(R.id.profile_img);
        	//profileIcon.setImageDrawable(activity.getResources().getDrawable(id))
        }
        
        menu.getMenu().findViewById(R.id.profile_menu_group).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(User.isRegisteredLocally())
					AppBase.launchActivity(UserProfileActivity.class);
				else
					AppBase.launchActivity(LoginActivity.class);
			}
        	
        });

        TextView mapTitle = (TextView) menu.getMenu().findViewById(R.id.map_title);
        mapTitle.setTypeface(AppBase.getTypefaceStrong()); 
        menu.getMenu().findViewById(R.id.map_menu_group).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				animate(v).alpha(0.9f).setDuration(100);
				AppBase.launchActivity(MainMapActivity.class);
			}
        	
        });
        
        TextView nowTitle = (TextView) menu.getMenu().findViewById(R.id.now_title);
        nowTitle.setTypeface(AppBase.getTypefaceStrong()); 
        menu.getMenu().findViewById(R.id.now_menu_group).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				animate(v).alpha(0.9f).setDuration(100);
				AppBase.launchActivity(NowActivity.class);
			}
        	
        });
        
        /*TextView marketTitle = (TextView) menu.getMenu().findViewById(R.id.market_title);
        marketTitle.setTypeface(AppBase.getTypefaceStrong()); 
        menu.getMenu().findViewById(R.id.market_menu_group).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				animate(v).alpha(0.9f).setDuration(100);
				AppBase.launchActivity(MarketActivity.class);
			}
        	
        });*/
        
        TextView cyclingTitle = (TextView) menu.getMenu().findViewById(R.id.citybiking_title);
        cyclingTitle.setTypeface(AppBase.getTypefaceStrong()); 
        menu.getMenu().findViewById(R.id.citybiking_menu_group).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				AppBase.launchActivity(TripsListActivity.class);
			}
        	
        });
        
		menu.setShadowWidthRes(R.dimen.shadow_width);
		menu.setShadowDrawable(R.drawable.shadow);
		menu.setBehindOffsetRes(R.dimen.actionbar_home_width);
		
		ActionBar actionBar = (ActionBar) activity.findViewById(R.id.actionbar);
		
		if(title != null)
			actionBar.setTitle(title);
        actionBar.setHomeAction(new Action() {

			@Override
			public int getDrawable() {
				return R.drawable.list_menu;
			}

			@Override
			public void performAction(View view) {
				menu.toggle();
			}
        	
        });
        
        setDefaultFontForActionBar(activity);
        return menu;
	}
	
	public static SlidingMenu load(Activity activity) {
		return loadWithActionBarTitle(activity, null);
	}
	
	public static void setDefaultFontForActionBar(Activity activity) {
		ActionBar actionBar = (ActionBar) activity.findViewById(R.id.actionbar);
    	
    	TextView actionBarTitle = (TextView) actionBar.findViewById(R.id.actionbar_title);
    	actionBarTitle.setTypeface(AppBase.getTypefaceStrong());
    	actionBarTitle.setTextSize(18);
	}
	
	public static void setDefaultFontForActionBarWithTitle(Activity activity, int title) {
		getActionBarFor(activity).setTitle(title);
	}
	
	public static void setDefaultFontForActionBarWithTitle(Activity activity, String title) {
		getActionBarFor(activity).setTitle(title);
	}

	protected static ActionBar getActionBarFor(Activity activity) {
		ActionBar actionBar = (ActionBar) activity.findViewById(R.id.actionbar);
    	
    	TextView actionBarTitle = (TextView) actionBar.findViewById(R.id.actionbar_title);
    	actionBarTitle.setTypeface(AppBase.getTypefaceStrong());
    	actionBarTitle.setTextSize(18);
    	return actionBar;
	}
}

