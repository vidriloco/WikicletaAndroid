package org.wikicleta.activities;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import org.interfaces.ImageFetchedListener;
import org.json.simple.JSONObject;
import org.wikicleta.R;
import org.wikicleta.activities.routes.NewRouteActivity;
import org.wikicleta.common.AppBase;
import org.wikicleta.common.Constants;
import org.wikicleta.helpers.Graphics;
import org.wikicleta.models.User;
import org.wikicleta.routing.Others;
import org.wikicleta.routing.Others.ImageUpdater;
import org.wikicleta.routing.Users;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class RootActivity extends Activity implements ImageFetchedListener {
	
	TextView creatorName;
	ImageView ownerPic;
	
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
        
        findViewById(R.id.settings_launcher_icon).setOnClickListener(new OnClickListener() {
			
        	@Override
			public void onClick(View v) {
				AppBase.launchActivity(ProfileSettingsActivity.class);
			}
        	
        });
        
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
        
        ownerPic = (ImageView) findViewById(R.id.user_pic);
	}
	
	public void userDetailsReceived(JSONObject object) {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("username", (String) object.get("username"));
		map.put("bio", (String) object.get("bio"));
		map.put("updated_at", (String) object.get("updated_at"));

		long mostRecentUpdatedOn = User.lastUpdateOn();
		
		User.storeWithParams(map, User.token());
        creatorName.setText(User.username());

        if(mostRecentUpdatedOn < User.lastUpdateOn()) {
    		String URL = (String) object.get("user_pic");
            ImageUpdater updater = Others.getImageFetcher();
            updater.setImageAndImageProcessor(ownerPic, Others.ImageProcessor.ROUND_FOR_USER_PROFILE);
            updater.setListener(this);
            updater.execute(URL);
        }
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		overridePendingTransition(R.anim.left_to_right, R.anim.fade_to_black);
		
		Bitmap pic = loadUserPic();
		if(pic != null)
			ownerPic.setImageBitmap(Graphics.getRoundedImageAtSize(pic, 230, 115));
		
    	Users users = new Users();
    	Users.Get usersFetcher = users.new Get(this);
    	usersFetcher.execute();
	}

	@Override
	public void imageFetchedSucceded(Bitmap bitmap) {
		saveUserPic(bitmap);
	}

	@Override
	public void imageFetchedFailed() {
		// TODO Auto-generated method stub
		
	}
	
	protected void saveUserPic(Bitmap bitmap) {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

		File f = new File(Constants.USER_PIC_DIR);
		try {
			f.createNewFile();
			FileOutputStream fo = new FileOutputStream(f);
			fo.write(bytes.toByteArray());
			fo.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	protected Bitmap loadUserPic() {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPreferredConfig = Bitmap.Config.ARGB_8888;
		return BitmapFactory.decodeFile(Constants.USER_PIC_DIR, options);
	}
}
