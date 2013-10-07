package org.wikicleta.activities;

import org.wikicleta.R;
import org.wikicleta.activities.common.ModifyingOnMapBaseActivity;
import org.wikicleta.common.AppBase;
import android.os.Bundle;

public class ShareActivity extends ModifyingOnMapBaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		shouldAnimateWithCustomTransition = true;
		super.onCreate(savedInstanceState, R.layout.activity_share);
		setTheme(R.style.Theme_wikicleta);
		
		AppBase.currentActivity = this;
		loadActionButtons();
	}
	
	@Override
	protected void onRestart() {
		super.onRestart();
		this.buildAndShowMenu();
	}
	
	
	
}
