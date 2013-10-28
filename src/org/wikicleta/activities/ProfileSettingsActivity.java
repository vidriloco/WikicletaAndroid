package org.wikicleta.activities;

import java.util.HashMap;
import org.wikicleta.R;
import org.wikicleta.activities.common.ImageSelectionActivity;
import org.wikicleta.analytics.AnalyticsBase;
import org.wikicleta.common.AppBase;
import org.wikicleta.common.Constants;
import org.wikicleta.common.FieldValidators;
import org.wikicleta.common.Toasts;
import org.wikicleta.helpers.Graphics;
import org.wikicleta.interfaces.RemoteFetchingDutyListener;
import org.wikicleta.models.User;
import org.wikicleta.routing.Users;
import com.actionbarsherlock.app.ActionBar;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class ProfileSettingsActivity extends ImageSelectionActivity implements RemoteFetchingDutyListener {
	protected ActionBar actionBar;
	
	EditText usernameField;
	EditText bioField;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.activity_profile);
		AppBase.currentActivity = this;
		
		usernameField = (EditText) this.findViewById(R.id.profile_username);
		bioField = (EditText) this.findViewById(R.id.profile_bio);

		usernameField.setTypeface(AppBase.getTypefaceStrong());
		usernameField.setText(User.username());
		
		bioField.setTypeface(AppBase.getTypefaceStrong());
		bioField.setText(User.bio());	
		ImageView returnIcon = (ImageView) this.findViewById(R.id.return_button);
    	returnIcon.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				overridePendingTransition(0, 0);
				AppBase.launchActivity(RootActivity.class);
				finish();
			}
    		
    	});
    	
		Bitmap picImg = loadUserPic();
		if(picImg != null)
			pic.setImageBitmap(Graphics.getRoundedImageAtSize(picImg, 230, 115));
    	
    	View actionBarView = this.getLayoutInflater().inflate(R.layout.navbar_layout, null);
		TextView navbarTitle = ((TextView) actionBarView.findViewById(R.id.navbar_title));
		navbarTitle.setTypeface(AppBase.getTypefaceStrong());
		navbarTitle.setText(R.string.your_profile_settings);
		
		actionBar = getSupportActionBar();
		actionBar.setHomeButtonEnabled(false);
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setCustomView(actionBarView);
		
		AnalyticsBase.reportLoggedInEvent("On Profile Activity", getApplicationContext());

		
		this.findViewById(R.id.save_profile).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				AnalyticsBase.reportLoggedInEvent("Attempted to update profile", getApplicationContext());

				updateProfile();
			}
			
		});
	}
	
	@SuppressWarnings("unchecked")
	protected void updateProfile() {
		String pictureEncoded = new String();
		if(bitmap != null)
			pictureEncoded = Graphics.generateEncodedStringForImage(bitmap);
		
		if(!FieldValidators.isFieldAValidUsername(usernameField.getText().toString())) {
			usernameField.setError(getResources().getString(R.string.user_username_restriction));
			return;
		}
		
		if(FieldValidators.isFieldLongerThan(bioField.getText().toString(), Constants.CHARACTERS_LENGTH_MAX_FOR_USER_BIO)) {
			bioField.setError(getResources().getString(R.string.user_bio_length_restriction));
			return;
		}
		
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("image_pic", pictureEncoded);
		params.put("username", usernameField.getText().toString());
		params.put("bio", bioField.getText().toString());

		HashMap<String, Object> cover = new HashMap<String, Object>();
		cover.put("user", params);
		
    	Users users = new Users();
    	Users.Post fetcher = users.new Post(this);
    	fetcher.execute(cover);
	}
	
	@Override
	public void onSuccess(Object duty) {
		Toasts.showToastWithMessage(this, R.string.user_profile_updated_successfully, R.drawable.success_icon);
		finish();
	}

	@Override
	public void onFailed(Object message) {
		Toasts.showToastWithMessage(this, R.string.user_profile_could_not_update, R.drawable.failure_icon);
	}

	@Override
	public void onFailed() {		
	}
	
	protected Bitmap loadUserPic() {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPreferredConfig = Bitmap.Config.ARGB_8888;
		return BitmapFactory.decodeFile(Constants.USER_PIC_DIR, options);
	}
}
