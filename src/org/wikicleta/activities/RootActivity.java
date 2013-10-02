package org.wikicleta.activities;

import org.wikicleta.R;
import org.wikicleta.common.AppBase;
import org.wikicleta.models.User;
import org.wikicleta.routing.Others;
import org.wikicleta.routing.Others.ImageUpdater;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class RootActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		AppBase.currentActivity = this;
		this.setContentView(R.layout.activity_root); 
		
		TextView creatorName = (TextView) findViewById(R.id.user_name);
                
        creatorName.setText(User.username());
        creatorName.setTypeface(AppBase.getTypefaceStrong());
        
        ((TextView) findViewById(R.id.activity_button_text)).setTypeface(AppBase.getTypefaceLight());
        ((TextView) findViewById(R.id.achievements_button_text)).setTypeface(AppBase.getTypefaceLight());
        ((TextView) findViewById(R.id.friends_button_text)).setTypeface(AppBase.getTypefaceLight());

        ((TextView) findViewById(R.id.settings_in_root_text)).setTypeface(AppBase.getTypefaceStrong());
        ((TextView) findViewById(R.id.messages_in_root_text)).setTypeface(AppBase.getTypefaceStrong());

        ImageView ownerPic = (ImageView) findViewById(R.id.user_pic);
        ImageUpdater updater = Others.getImageFetcher();
        updater.setImageAndImageProcessor(ownerPic, Others.ImageProcessor.ROUND_FOR_USER_PROFILE);
        updater.execute("https://si0.twimg.com/profile_images/378800000149808077/701cc6552a0c352a53b75a5aa4781c54.jpeg");
        
        ((TextView) findViewById(R.id.discover_container_text)).setTypeface(AppBase.getTypefaceStrong());
        ((TextView) findViewById(R.id.share_container_text)).setTypeface(AppBase.getTypefaceStrong());
        ((TextView) findViewById(R.id.challenges_container_text)).setTypeface(AppBase.getTypefaceStrong());

        ((TextView) findViewById(R.id.local_events_container_text)).setTypeface(AppBase.getTypefaceStrong());
        ((TextView) findViewById(R.id.routes_tracer_container_text)).setTypeface(AppBase.getTypefaceStrong());

        LinearLayout discoverContainer = (LinearLayout) findViewById(R.id.discover_container);
        discoverContainer.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				AppBase.launchActivity(DiscoverActivity.class);
			}
        	
        });
	}
}
