package org.wikicleta.helpers;

import org.wikicleta.R;
import com.slidingmenu.lib.SlidingMenu;
import android.app.Activity;

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

	public static SlidingMenu loadOnRight(Activity activity) {
		return loadOnRight(activity, null);
	}

}

