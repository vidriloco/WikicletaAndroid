package org.wikicleta.activities.access;

import java.util.LinkedHashMap;
import java.util.Map;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.wikicleta.R;
import org.wikicleta.activities.RootActivity;
import org.wikicleta.activities.common.ImageSelectionActivity;
import org.wikicleta.analytics.AnalyticsBase;
import org.wikicleta.common.AppBase;
import org.wikicleta.common.FieldValidators;
import org.wikicleta.common.NetworkOperations;
import org.wikicleta.helpers.DialogBuilder;
import org.wikicleta.helpers.Graphics;
import org.wikicleta.models.User;
import com.actionbarsherlock.app.ActionBar;
import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class RegistrationActivity extends ImageSelectionActivity {
	
	private String mEmail;
	private String mPassword;
	private String mPasswordConfirmation;
	private String mName;
	private String mUsername;
	private String picEncoded;
	
	private EditText mEmailView;
	private EditText mUsernameView;
	private EditText mNameView;
	private EditText mPasswordView;
	private EditText mPasswordConfirmationView;
	
	protected ActionBar actionBar;
	
	protected Dialog alertDialog;
	private UserRegistrationTask mRegAuthTask = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.activity_registration);
		AppBase.currentActivity = this;
        loadNavBarWithTitle(R.string.registration_title);

		// Set up the login form
		mNameView = (EditText) findViewById(R.id.name);
		mNameView.setTypeface(AppBase.getTypefaceLight());
		mEmailView = (EditText) findViewById(R.id.email);
		mEmailView.setTypeface(AppBase.getTypefaceLight());
		mUsernameView = (EditText) findViewById(R.id.username);
		mUsernameView.setTypeface(AppBase.getTypefaceLight());
		mPasswordView = (EditText) findViewById(R.id.password);
		mPasswordView.setTypeface(AppBase.getTypefaceLight());
		mPasswordConfirmationView = (EditText) findViewById(R.id.password_confirmation);
		mPasswordConfirmationView.setTypeface(AppBase.getTypefaceLight());
		
		AnalyticsBase.reportUnloggedEvent("On Registration Activity", getApplicationContext());

		((TextView) this.findViewById(R.id.pic_instructions)).setTypeface(AppBase.getTypefaceStrong());
        
        alertDialog = DialogBuilder.buildLoadingDialogWithMessage(this, R.string.registering_user);
        
        this.findViewById(R.id.return_button).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				AppBase.launchActivity(LandingActivity.class);
				finish();
    			AnalyticsBase.reportUnloggedEvent("Did not register", RegistrationActivity.this.getApplicationContext());

			}
        	
        });
        
        this.findViewById(R.id.save_profile).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
    			AnalyticsBase.reportUnloggedEvent("Attempted to save profile", RegistrationActivity.this.getApplicationContext());

				attemptSignup();
			}
        	
        });
        
	}
	
	public void attemptSignup() {
		if (mRegAuthTask != null) {
			return;
		}
		
		// Reset errors.
		mEmailView.setError(null);
		mPasswordView.setError(null);
		mPasswordConfirmationView.setError(null);
		mNameView.setError(null);
		mUsernameView.setError(null);
		
		// Populate registration fields
		mEmail = mEmailView.getText().toString();
		mUsername = mUsernameView.getText().toString();
		mPassword = mPasswordView.getText().toString();
		mName = mNameView.getText().toString();
		mPasswordConfirmation = mPasswordConfirmationView.getText().toString();
		
		if(FieldValidators.isFieldEmpty(mName)) {
			mNameView.setError(getString(R.string.error_field_required));
			return;
		}
		
		if(FieldValidators.isFieldEmpty(mUsername)) {
			mUsernameView.setError(getString(R.string.error_field_required));
			return;
		}
		
		if (FieldValidators.isFieldEmpty(mEmail)) {
			mEmailView.setError(getString(R.string.error_field_required));
			return;
		} else if(!FieldValidators.isFieldAValidEmail(mEmail)) {
			mEmailView.setError(getString(R.string.error_invalid_email));
			return;
		}
		
		if (FieldValidators.isFieldEmpty(mPassword)) {
			mPasswordView.setError(getString(R.string.error_field_required));
			return;
		} else if(FieldValidators.isFieldShorterThan(mPassword, 5)) {
			mPasswordView.setError(getString(R.string.error_invalid_password));
			return;
		}
		
		if(FieldValidators.isFieldEmpty(mPasswordConfirmation)) {
			mPasswordConfirmationView.setError(getString(R.string.error_field_required));
			return;
		} else if(!FieldValidators.fieldsMatch(mPassword, mPasswordConfirmation)) {
			mPasswordConfirmationView.setError(getString(R.string.error_not_matching_password_confirmation));
			return;
		}
		
		if(bitmap != null)
			picEncoded = Graphics.generateEncodedStringForImage(bitmap);
		
		mRegAuthTask = new UserRegistrationTask();
		mRegAuthTask.execute((Void) null);
	}
	
	/**
	 * Represents an asynchronous login/registration task used to authenticate
	 * the user.
	 */
	public class UserRegistrationTask extends AsyncTask<Void, Void, Boolean> {
		JSONObject responseObject;
		@SuppressWarnings("unchecked")
		@Override
		protected Boolean doInBackground(Void... params) {

			Map<String, String> parameters = new LinkedHashMap<String, String>();
			parameters.put("username", mUsername);
			parameters.put("email", mEmail);
			parameters.put("password", mPassword);
			parameters.put("password_confirmation", mPassword);
			parameters.put("full_name", mName);
			parameters.put("image_pic", picEncoded);
			
			Map<String, Map<String, String>> user = new LinkedHashMap<String, Map<String, String>>();
			user.put("registration", parameters);

			String result = NetworkOperations.postJSONExpectingStringTo("/api/users", JSONValue.toJSONString(user));
			if(result==null)
				return false;
			Log.e("WIKICLETA", result);
			responseObject = (JSONObject) JSONValue.parse(result);
			if(responseObject!= null && responseObject.containsKey("errors")) {
				return false;
			} else {
				User.storeWithParams(responseObject, (String) responseObject.get("auth_token"));
				
				AnalyticsBase.registerUser(User.id(), getApplicationContext());
    			AnalyticsBase.reportLoggedInEvent("Registered new user", RegistrationActivity.this.getApplicationContext());

				return true;
			}
		}

		@Override
		protected void onPreExecute() {
			alertDialog.show();
		}
		
		@Override
		protected void onPostExecute(final Boolean success) {
			mRegAuthTask = null;
			alertDialog.hide();
			
			if (success) {
				if(User.isRegisteredLocally()) {
					AppBase.launchActivity(RootActivity.class);
					showGreetMessage();
				}
			} else {
				if(responseObject == null) {
					DialogBuilder.displayAlertWithTitleAndMessage(AppBase.currentActivity, R.string.neutral, R.string.connectivity_problems);
				} else {
					JSONObject errorsObject = (JSONObject) responseObject.get("errors");
					JSONArray array;
					if(errorsObject.containsKey("email")) {
						array = (JSONArray) errorsObject.get("email");
						mEmailView.setError((String) array.get(0));
						mEmailView.requestFocus();
					} else if(errorsObject.containsKey("username")) {
						array = (JSONArray) errorsObject.get("username");
						mUsernameView.setError((String) array.get(0));
						mUsernameView.requestFocus();
					} else {
						mPasswordView
								.setError(getString(R.string.error_incorrect_password));
						mPasswordView.requestFocus();
					}
				}
			}
		}

		@Override
		protected void onCancelled() {
			mRegAuthTask = null;
			alertDialog.hide();
		}
	}
	
	protected void loadNavBarWithTitle(int titleResource) {
		View actionBarView = this.getLayoutInflater().inflate(R.layout.navbar_layout, null);
		TextView navbarTitle = ((TextView) actionBarView.findViewById(R.id.navbar_title));
		navbarTitle.setTypeface(AppBase.getTypefaceStrong());
		navbarTitle.setText(titleResource);
		
		actionBar = getSupportActionBar();
		actionBar.setHomeButtonEnabled(false);
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setCustomView(actionBarView);
	}
	
	protected void showGreetMessage() {
		LayoutInflater inflater = this.getLayoutInflater();
		View layout = inflater.inflate(R.layout.message,
		                               (ViewGroup) this.findViewById(R.id.toast_layout_root));
		
		ImageView icon = (ImageView) layout.findViewById(R.id.message_icon);
		icon.setImageDrawable(this.getResources().getDrawable(R.drawable.hand_icon));
		TextView text = (TextView) layout.findViewById(R.id.message_text);
		text.setTypeface(AppBase.getTypefaceLight());
		text.setText(R.string.profile_welcome);
		Toast toast = new Toast(this);
		toast.setDuration(Toast.LENGTH_LONG);
		toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL, 0, 0);
		toast.setView(layout);
		toast.show();
	}
}
