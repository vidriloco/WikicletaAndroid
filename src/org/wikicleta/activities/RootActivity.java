package org.wikicleta.activities;

import java.util.HashMap;

import org.json.simple.JSONObject;
import org.wikicleta.R;
import org.wikicleta.activities.routes.NewRouteActivity;
import org.wikicleta.common.AppBase;
import org.wikicleta.models.User;
import org.wikicleta.routing.Others;
import org.wikicleta.routing.Others.ImageUpdater;
import org.wikicleta.routing.Users;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class RootActivity extends Activity {
	TextView creatorName;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		AppBase.currentActivity = this;
		this.setContentView(R.layout.activity_root); 
		
		creatorName = (TextView) findViewById(R.id.user_name);
                
        creatorName.setText(User.username());
        creatorName.setTypeface(AppBase.getTypefaceStrong());
        
        ((TextView) findViewById(R.id.activity_button_text)).setTypeface(AppBase.getTypefaceLight());
        ((TextView) findViewById(R.id.achievements_button_text)).setTypeface(AppBase.getTypefaceLight());
        ((TextView) findViewById(R.id.favorites_button_text)).setTypeface(AppBase.getTypefaceLight());

        //((TextView) findViewById(R.id.settings_in_root_text)).setTypeface(AppBase.getTypefaceStrong());
        //((TextView) findViewById(R.id.messages_in_root_text)).setTypeface(AppBase.getTypefaceStrong());
        
        ((TextView) findViewById(R.id.discover_container_text)).setTypeface(AppBase.getTypefaceStrong());
        ((TextView) findViewById(R.id.share_container_text)).setTypeface(AppBase.getTypefaceStrong());
        ((TextView) findViewById(R.id.challenges_container_text)).setTypeface(AppBase.getTypefaceStrong());

        ((TextView) findViewById(R.id.local_events_container_text)).setTypeface(AppBase.getTypefaceStrong());
        ((TextView) findViewById(R.id.routes_tracer_container_text)).setTypeface(AppBase.getTypefaceStrong());
        
        
        
        findViewById(R.id.achievements_container).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				AppBase.launchActivity(AchievementsActivity.class);
			}
        	
        });
        
        findViewById(R.id.activity_container).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				AppBase.launchActivity(ActivitiesActivity.class);
			}
        	
        });
        
        findViewById(R.id.favorites_container).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				AppBase.launchActivity(FavoritesActivity.class);
			}
        	
        });
        
        LinearLayout discoverContainer = (LinearLayout) findViewById(R.id.discover_container);
        discoverContainer.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				AppBase.launchActivity(DiscoverActivity.class);
			}
        	
        });
        
        LinearLayout shareContainer = (LinearLayout) findViewById(R.id.share_container);
        shareContainer.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				AppBase.launchActivity(ShareActivity.class);
			}
        	
        });
        
        LinearLayout routesTracerContainer = (LinearLayout) findViewById(R.id.routes_tracer_container);
        routesTracerContainer.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				AppBase.launchActivity(NewRouteActivity.class);
			}
        	
        });
        
        LinearLayout eventsContainer = (LinearLayout) findViewById(R.id.local_events_container);
        eventsContainer.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				AppBase.launchActivity(EventsActivity.class);
			}
        	
        });
        
    	Users users = new Users();
    	Users.Get usersFetcher = users.new Get(this);
    	usersFetcher.execute();
	}
	
	public void displayUserDetails(JSONObject object) {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("username", (String) object.get("username"));

		User.storeWithParams(map, User.token());
        creatorName.setText(User.username());

		String URL = (String) object.get("user_pic");
		
        ImageView ownerPic = (ImageView) findViewById(R.id.user_pic);
        ImageUpdater updater = Others.getImageFetcher();
        updater.setImageAndImageProcessor(ownerPic, Others.ImageProcessor.ROUND_FOR_USER_PROFILE);
        updater.execute(URL);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		overridePendingTransition(R.anim.left_to_right, R.anim.fade_to_black);

	}
}
