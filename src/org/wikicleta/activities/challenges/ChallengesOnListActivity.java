package org.wikicleta.activities.challenges;

import org.wikicleta.R;
import org.wikicleta.activities.RootActivity;
import org.wikicleta.analytics.AnalyticsBase;
import org.wikicleta.common.AppBase;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class ChallengesOnListActivity extends SherlockFragmentActivity {
	
	protected ActionBar actionBar;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AppBase.currentActivity = this;
		this.setContentView(R.layout.activity_challenges_list);
		
		AnalyticsBase.reportLoggedInEvent("On ChallengesOnListActivity", getApplicationContext());

		ImageView returnIcon = (ImageView) this.findViewById(R.id.return_button);
		returnIcon.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				AppBase.launchActivity(RootActivity.class);
			}
			
		});
		
		ImageView mapIcon = (ImageView) this.findViewById(R.id.map_button);
		mapIcon.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				AppBase.launchActivity(ChallengesOnMapActivity.class);
			}
			
		});
		
		View actionBarView = this.getLayoutInflater().inflate(R.layout.navbar_layout, null);
		TextView navbarTitle = ((TextView) actionBarView.findViewById(R.id.navbar_title));
		navbarTitle.setTypeface(AppBase.getTypefaceStrong());
		navbarTitle.setText(R.string.challenges_list_title);
		
		actionBar = getSupportActionBar();
		actionBar.setHomeButtonEnabled(false);
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setDisplayShowTitleEnabled(false);

		actionBar.setCustomView(actionBarView);
	}
}
