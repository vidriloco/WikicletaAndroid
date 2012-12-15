package com.wikicleta.activities;

import org.mobility.wikicleta.R;

import com.wikicleta.common.AppBase;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class LandingActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AppBase.currentActivity = this;
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
