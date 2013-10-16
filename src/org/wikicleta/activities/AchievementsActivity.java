package org.wikicleta.activities;

import org.wikicleta.R;
import org.wikicleta.adapters.AchievementsFragmentAdapter;
import org.wikicleta.adapters.TabsPagerAdapter;
import org.wikicleta.common.AppBase;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.viewpagerindicator.PageIndicator;
import com.viewpagerindicator.UnderlinePageIndicator;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class AchievementsActivity extends SherlockFragmentActivity {

	AchievementsFragmentAdapter mAdapter;
    ViewPager mPager;
    PageIndicator mIndicator;
    protected ActionBar actionBar;
    
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_achievements);
		setTheme(R.style.Theme_wikicleta);
		
		mAdapter = new AchievementsFragmentAdapter(getSupportFragmentManager());

        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(mAdapter);

        mIndicator = (UnderlinePageIndicator) findViewById(R.id.indicator);
        mIndicator.setViewPager(mPager);
		
    	((TextView) findViewById(R.id.challenges_total_value)).setTypeface(AppBase.getTypefaceStrong());
    	((TextView) findViewById(R.id.challenges_total_text)).setTypeface(AppBase.getTypefaceStrong());
    	((TextView) findViewById(R.id.challenges_week_value)).setTypeface(AppBase.getTypefaceStrong());
    	((TextView) findViewById(R.id.challenges_week_text)).setTypeface(AppBase.getTypefaceStrong());

    	
		// Assign icons
        ImageView returnIcon = (ImageView) this.findViewById(R.id.return_button);
		returnIcon = (ImageView) this.findViewById(R.id.return_button);
    	
    	returnIcon.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				AppBase.launchActivity(RootActivity.class);
			}
    		
    	});
    	
    	this.loadActionBarWith(R.string.achievements_in_root);
	}
	
	protected void loadActionBarWith(int identifier) {
		// Initilization
		actionBar = getSupportActionBar();
	
		actionBar.setHomeButtonEnabled(false);
		
		View actionBarView = this.getLayoutInflater().inflate(R.layout.navbar_layout, null);
		TextView navbarTitle = ((TextView) actionBarView.findViewById(R.id.navbar_title));
		navbarTitle.setTypeface(AppBase.getTypefaceStrong());
		navbarTitle.setText(identifier);
		
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setDisplayShowTitleEnabled(false);

		actionBar.setCustomView(actionBarView); 
	}
	
	
	
}
