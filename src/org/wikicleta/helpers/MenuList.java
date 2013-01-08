package org.wikicleta.helpers;

import java.util.ArrayList;

import org.wikicleta.R;
import org.wikicleta.activities.RoutesActivity;
import org.wikicleta.activities.UserProfileActivity;
import org.wikicleta.adapters.MenuEntry;
import org.wikicleta.adapters.MenuEntryAdapter;

import com.slidingmenu.lib.SlidingMenu;
import com.slidingmenu.lib.app.SlidingMapActivity;

import android.app.Activity;
import android.widget.ListView;

public class MenuList {
	
	public static SlidingMenu prepareMenuElementsForActivity(Activity activity) {
        MenuEntryAdapter adapter = new MenuEntryAdapter(activity);
        for(MenuEntry me : MenuList.menuEntries()) {
			adapter.add(me);
		}
        
		// customize the SlidingMenu
        SlidingMenu menu = new SlidingMenu(activity);
        menu.setMode(SlidingMenu.LEFT);
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
        menu.setFadeDegree(0.35f);
        menu.attachToActivity(activity, SlidingMenu.SLIDING_CONTENT);
        menu.setMenu(R.layout.sliding_menu);
		menu.setShadowWidthRes(R.dimen.shadow_width);
		menu.setShadowDrawable(R.drawable.shadow);
		menu.setBehindOffsetRes(R.dimen.actionbar_home_width);
		
        //ListView mainList = (ListView) menu.getMenu().findViewById(R.id.menu_list_view);
        //mainList.setAdapter(adapter);

		return menu;
	}
	
	public static void prepareMenuElementsForActivity(final SlidingMapActivity activity) {
	    
		//activity.setBehindContentView(R.layout.menu_list);
        MenuEntryAdapter adapter = new MenuEntryAdapter(activity);
        for(MenuEntry me : MenuList.menuEntries()) {
			adapter.add(me);
		}
        //ListView mainList = (ListView) activity.findViewById(R.id.menu_list_view);

        //mainList.setAdapter(adapter);

		// customize the SlidingMenu
        activity.getSlidingMenu().setBehindScrollScale(0.5f);
        activity.getSlidingMenu().setShadowWidthRes(R.dimen.shadow_width);
        activity.getSlidingMenu().setShadowDrawable(R.drawable.shadow);
        //activity.getSlidingMenu().setBehindOffsetRes(R.dimen.actionbar_home_width);
	}
	
	public static ArrayList<MenuEntry> menuEntries() {
		ArrayList<MenuEntry> menuEntries = new ArrayList<MenuEntry>();
		menuEntries.add(new MenuEntry(R.string.profile_activity_menu, R.drawable.profile_menu, UserProfileActivity.class));
		menuEntries.add(new MenuEntry(R.string.routes_activity_menu, R.drawable.routes_menu, RoutesActivity.class));
		menuEntries.add(new MenuEntry(R.string.highlights_activity_menu, R.drawable.highlights_menu, null));
		menuEntries.add(new MenuEntry(R.string.about_activity_menu, R.drawable.about_menu, null));
		return menuEntries;
	}


}

