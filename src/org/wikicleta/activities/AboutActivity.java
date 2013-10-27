package org.wikicleta.activities;

import org.wikicleta.R;
import org.wikicleta.adapters.AboutSectionsFragmentAdapter;
import org.wikicleta.common.AppBase;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.viewpagerindicator.UnderlinePageIndicator;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class AboutActivity extends SherlockFragmentActivity {

	protected ActionBar actionBar;
	protected AboutSectionsFragmentAdapter mAdapter;
	protected ViewPager mPager;
	protected UnderlinePageIndicator mIndicator;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		AppBase.currentActivity = this;
		this.setContentView(R.layout.activity_about); 
		
		mAdapter = new AboutSectionsFragmentAdapter(getSupportFragmentManager());

        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(mAdapter);

        mIndicator = (UnderlinePageIndicator) findViewById(R.id.indicator);
        mIndicator.setViewPager(mPager);
		
		View actionBarView = this.getLayoutInflater().inflate(R.layout.navbar_layout, null);
		TextView navbarTitle = ((TextView) actionBarView.findViewById(R.id.navbar_title));
		navbarTitle.setTypeface(AppBase.getTypefaceStrong());
		navbarTitle.setText(R.string.about_in_root_text);
		
		actionBar = getSupportActionBar();
		actionBar.setHomeButtonEnabled(false);
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setCustomView(actionBarView);
		
		this.findViewById(R.id.return_button).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
			
		});
	}
}
