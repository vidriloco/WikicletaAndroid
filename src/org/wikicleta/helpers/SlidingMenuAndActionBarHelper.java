package org.wikicleta.helpers;

import org.wikicleta.R;
import org.wikicleta.activities.HighlightsActivity;
import org.wikicleta.activities.PlacesActivity;
import org.wikicleta.activities.RoutesActivity;
import org.wikicleta.activities.UserProfileActivity;
import org.wikicleta.common.AppBase;
import static com.nineoldandroids.view.ViewPropertyAnimator.animate;
import com.markupartist.android.widget.ActionBar;
import com.markupartist.android.widget.ActionBar.Action;
import com.slidingmenu.lib.SlidingMenu;
import android.app.Activity;
import android.graphics.Typeface;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SlidingMenuAndActionBarHelper {
	static Typeface font;
	
	public static void load(Activity activity) {
		if(font == null)
			font = Typeface.createFromAsset(activity.getAssets(), "GothamRnd-Bold.ttf");  

		// customize the SlidingMenu
        final SlidingMenu menu = new SlidingMenu(activity);
        menu.setMode(SlidingMenu.LEFT);
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
        menu.setFadeDegree(0.35f);
        menu.attachToActivity(activity, SlidingMenu.SLIDING_CONTENT);
        menu.setMenu(R.layout.sliding_menu);        
        
        TextView menuTitle = (TextView) menu.getMenu().findViewById(R.id.menu_layout_title);
        menuTitle.setTypeface(font); 
        
        TextView profileTitle = (TextView) menu.getMenu().findViewById(R.id.profile_title);
        profileTitle.setTypeface(font);
        
        menu.getMenu().findViewById(R.id.profile_menu_group).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				animate(v).alpha(0.9f).setDuration(100);
				AppBase.launchActivity(UserProfileActivity.class);
			}
        	
        });

        TextView highlightTitle = (TextView) menu.getMenu().findViewById(R.id.highlight_title);
        highlightTitle.setTypeface(font); 
        menu.getMenu().findViewById(R.id.highlight_menu_group).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				animate(v).alpha(0.9f).setDuration(100);
				AppBase.launchActivity(HighlightsActivity.class);
			}
        	
        });
        
        TextView placesTitle = (TextView) menu.getMenu().findViewById(R.id.places_title);
        placesTitle.setTypeface(font); 
        menu.getMenu().findViewById(R.id.places_menu_group).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				animate(v).alpha(0.9f).setDuration(100);
				AppBase.launchActivity(PlacesActivity.class);
			}
        	
        });
        
        TextView routesTitle = (TextView) menu.getMenu().findViewById(R.id.routes_title);
        routesTitle.setTypeface(font); 
        menu.getMenu().findViewById(R.id.routes_menu_group).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				animate(v).alpha(0.9f).setDuration(100);
				AppBase.launchActivity(RoutesActivity.class);
			}
        	
        });
        
		menu.setShadowWidthRes(R.dimen.shadow_width);
		menu.setShadowDrawable(R.drawable.shadow);
		menu.setBehindOffsetRes(R.dimen.actionbar_home_width);
		
		ActionBar actionBar = (ActionBar) activity.findViewById(R.id.actionbar);
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
	}
	



}

