package org.wikicleta.activities;

import org.wikicleta.R;
import org.wikicleta.common.AppBase;
import org.wikicleta.models.User;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class LandingActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AppBase.currentActivity = this;
		
		if(User.isSignedIn()) {
			Intent intent = new Intent(AppBase.currentActivity, RoutesActivity.class);
			AppBase.currentActivity.startActivity(intent);
			finish();
			return;
		}
		
		this.setContentView(R.layout.activity_landing); 
		
		findViewById(R.id.join).setOnClickListener(
			new View.OnClickListener() {
				@Override
			public void onClick(View view) {
				Intent intent = new Intent(AppBase.currentActivity, LoginActivity.class);
				AppBase.currentActivity.startActivity(intent);
			}
		});
	}
}
