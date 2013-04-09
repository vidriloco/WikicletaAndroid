package org.wikicleta.tips.activities;

import org.wikicleta.R;
import org.wikicleta.common.AppBase;
import org.wikicleta.helpers.SlidingMenuAndActionBarHelper;

import android.app.Activity;
import android.os.Bundle;

public class SaveTipActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTheme(R.style.Theme_wikicleta);
		
        AppBase.currentActivity = this;
        
        this.setContentView(R.layout.tips_activity_saving);
    	SlidingMenuAndActionBarHelper.setDefaultFontForActionBar(this);
    	
    	Bundle extras = this.getIntent().getExtras();
    	
    	if(!extras.containsKey("lat") && !extras.containsKey("lon")) {
    		finish();
    	} else {
    		int lat = (Integer) extras.get("lat");
    		int lon = (Integer) extras.get("lon");
    		
    		
    	}
	}
}
