package org.wikicleta.activities.common;

import org.wikicleta.R;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.widget.ImageView;

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
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSherlock().getMenuInflater();
		inflater.inflate(R.menu.actionbar_modifying_activity_map, menu);
		return true;
	} 
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
	    case R.id.save_button:
			presentSaveForm();
	      break;
	    case android.R.id.home:
	    	finish();
	    default:
	      break;
	    }

	    return true;
	}
}
