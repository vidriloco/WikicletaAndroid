package org.wikicleta.activities;

import org.wikicleta.R;
import org.wikicleta.activities.common.ModifyingOnMapBaseActivity;
import org.wikicleta.analytics.AnalyticsBase;
import org.wikicleta.common.AppBase;
import android.os.Bundle;

public class ShareActivity extends ModifyingOnMapBaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		shouldAnimateWithCustomTransition = true;
		super.onCreate(savedInstanceState, R.layout.activity_share);
		
		AppBase.currentActivity = this;
		loadActionButtons();
		AnalyticsBase.reportLoggedInEvent("On Share Activity", getApplicationContext());

	}
	
	@Override
	protected void onRestart() {
		super.onRestart();
		this.buildAndShowMenu();
	}
	
	
	
}
