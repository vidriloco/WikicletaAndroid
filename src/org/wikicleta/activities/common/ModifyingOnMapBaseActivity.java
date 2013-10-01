package org.wikicleta.activities.common;

import org.wikicleta.R;
import com.actionbarsherlock.app.ActionBar;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

public class ModifyingOnMapBaseActivity extends LocationAwareMapWithControlsActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.activity_select_poi_on_map);
    	assignToggleActionsForAutomapCenter();
    	
    	getSherlock().getActionBar().setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_SHOW_HOME); 
		getSherlock().getActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#90072F6B")));
	}
	
	protected void presentSaveForm() {

	}

}
