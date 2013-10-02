package org.wikicleta.helpers;

import org.wikicleta.R;
import org.wikicleta.activities.NowActivity;
import org.wikicleta.activities.DiscoverActivity;
import org.wikicleta.activities.UserProfileActivity;
import org.wikicleta.activities.access.LoginActivity;
import org.wikicleta.activities.trips.TripsListActivity;
import org.wikicleta.common.AppBase;
import org.wikicleta.models.User;

import static com.nineoldandroids.view.ViewPropertyAnimator.animate;
import com.slidingmenu.lib.SlidingMenu;
import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class SlidingMenuBuilder {
	
	public static SlidingMenu loadOnRight(Activity activity, String title) {
		
		// customize the SlidingMenu
        final SlidingMenu menu = new SlidingMenu(activity);
        menu.setMode(SlidingMenu.RIGHT);
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
        menu.setFadeDegree(0.35f);
        menu.attachToActivity(activity, SlidingMenu.SLIDING_CONTENT);
        menu.setMenu(R.layout.right_sliding_menu);  
		menu.setShadowWidthRes(R.dimen.shadow_width);
		menu.setShadowDrawable(R.drawable.shadow_right);
		
		menu.setBehindOffsetRes(R.dimen.right_menu_visible_area_width);
		
        return menu;
	}

	
	public static SlidingMenu loadOnLeft(Activity activity, String title) {

		// customize the SlidingMenu
        final SlidingMenu menu = new SlidingMenu(activity);
        menu.setMode(SlidingMenu.LEFT);
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
        menu.setFadeDegree(0.35f);
        menu.attachToActivity(activity, SlidingMenu.SLIDING_CONTENT);
        menu.setMenu(R.layout.left_sliding_menu);        
        
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
				AppBase.launchActivity(DiscoverActivity.class);
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
		menu.setShadowDrawable(R.drawable.shadow_left);
		menu.setBehindOffsetRes(R.dimen.left_menu_visible_area_width);
        
        return menu;
	}
	
	public static SlidingMenu loadOnLeft(Activity activity) {
		return loadOnLeft(activity, null);
	}

	public static SlidingMenu loadOnRight(Activity activity) {
		return loadOnRight(activity, null);
	}

}

