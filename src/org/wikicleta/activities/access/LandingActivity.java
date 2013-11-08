package org.wikicleta.activities.access;

import java.util.LinkedHashMap;
import java.util.Map;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.wikicleta.R;
import org.wikicleta.activities.RootActivity;
import org.wikicleta.analytics.AnalyticsBase;
import org.wikicleta.common.AppBase;
import org.wikicleta.common.FieldValidators;
import org.wikicleta.common.NetworkOperations;
import org.wikicleta.helpers.DialogBuilder;
import org.wikicleta.models.User;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

public class LandingActivity extends AccessActivity {
		
	UserLoginTask mAuthTask;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setTheme(R.style.Theme_Sherlock_Light_NoActionBar);
		AppBase.currentActivity = this;
		this.setContentView(R.layout.activity_landing); 
		AnalyticsBase.reportUnloggedEvent("On LandingActivity", this.getApplicationContext());

		bootApplicationMainView();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		AnalyticsBase.flushInstance();
	}
	
	protected void bootApplicationMainView() {
		int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		if(status != ConnectionResult.SUCCESS) {
			AnalyticsBase.reportUnloggedEvent("Google Play NOT FOUND", this.getApplicationContext());
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
			
			alertDialogBuilder
					.setTitle("Google Play Services")
					.setMessage(this.getString(R.string.no_google_play_services))
					.setCancelable(true)
					.setPositiveButton(this.getString(R.string.actions_install), new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,int id) {
							AnalyticsBase.reportUnloggedEvent("Attempt to install Google Play", LandingActivity.this.getApplicationContext());
							dialog.dismiss();
							// Try the new HTTP method (I assume that is the official way now given that google uses it).
							try
							{
								Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=com.google.android.gms"));
								intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
								intent.setPackage("com.android.vending");
								startActivity(intent);
								finish();
							}
							catch (ActivityNotFoundException e)
							{
								try
								{
									Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.google.android.gms"));
									intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
									intent.setPackage("com.android.vending");
									startActivity(intent);
									finish();
								}
								catch (ActivityNotFoundException f)
								{
									Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=com.google.android.gms"));
									intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
									startActivity(intent);
									finish();
								}
							}
						}
					})
					.setNegativeButton(this.getString(R.string.no) ,new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,int id) {
							AnalyticsBase.reportUnloggedEvent("Will not install Google Play", LandingActivity.this.getApplicationContext());

							dialog.cancel();
							finish();
						}
					})
					.create()
					.show();
        } else {
        	
        	if(User.isRegisteredLocally()) {
    			AppBase.launchActivity(RootActivity.class);
    			finish();
    		} else {
    			AnalyticsBase.reportUnloggedEvent("GooglePlay FOUND", this.getApplicationContext());
    			
    			AnimatorSet set = new AnimatorSet();
    	    	set.playTogether(
    	    	    ObjectAnimator.ofFloat(findViewById(R.id.logo), "scaleX", 1, 1.2f),
    	    	    ObjectAnimator.ofFloat(findViewById(R.id.logo), "scaleY", 1, 1.2f),
    	    	    ObjectAnimator.ofFloat(findViewById(R.id.logo), "alpha", 0, 1, 1)
    	    	);
    			set.setDuration(1500).start();

    			TextView loginText = (TextView) this.findViewById(R.id.login_text);
    			TextView joinText = (TextView) this.findViewById(R.id.join_text);

    			loginText.setTypeface(AppBase.getTypefaceStrong());		
    			joinText.setTypeface(AppBase.getTypefaceStrong());
    			
    			loginText.setOnClickListener(new OnClickListener() {

    				@Override
    				public void onClick(View arg0) {
    					AnalyticsBase.reportUnloggedEvent("Clicked on Login Button", getApplicationContext());
    					displaySignInForm();				
    				}
    				
    			});
    			
    			joinText.setOnClickListener(new OnClickListener() {

    				@Override
    				public void onClick(View arg0) {
    					AnalyticsBase.reportUnloggedEvent("Clicked on Join Button", getApplicationContext());
    					sendToRegistrationActivity();				
    				}
    				
    			});
    		}
        }
	}
	
	protected void displaySignInForm() {
		final Dialog toggleBuilder = new Dialog(this);
		toggleBuilder.requestWindowFeature(Window.FEATURE_NO_TITLE);

        LayoutInflater inflater = this.getLayoutInflater();
        final View view = inflater.inflate(R.layout.dialog_login, null);
        ((TextView) view.findViewById(R.id.login_text)).setTypeface(AppBase.getTypefaceStrong());
        
        ((EditText) view.findViewById(R.id.login_username)).setTypeface(AppBase.getTypefaceStrong());
        ((EditText) view.findViewById(R.id.login_password)).setTypeface(AppBase.getTypefaceStrong());
        toggleBuilder.setContentView(view);
        toggleBuilder.show();
        
        ((TextView) view.findViewById(R.id.button_return)).setTypeface(AppBase.getTypefaceStrong());
        ((TextView) view.findViewById(R.id.button_login)).setTypeface(AppBase.getTypefaceStrong());

        view.findViewById(R.id.return_button_container).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				toggleBuilder.dismiss();
			}
        	
        });
        
        view.findViewById(R.id.login_button_container).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				EditText username = (EditText) view.findViewById(R.id.login_username);
				AnalyticsBase.reportUnloggedEvent("Attempted To Login", getApplicationContext(), "username", username.toString());				
				attemptLogin(username, (EditText) view.findViewById(R.id.login_password));
			}
        	
        });
        
        toggleBuilder.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialog) {
				AnalyticsBase.reportUnloggedEvent("Dismissed Login View", getApplicationContext());				
			}
        	
        });
	}
	
	protected void sendToRegistrationActivity() {
		AppBase.launchActivity(RegistrationActivity.class);
	}
	
	
	/**
	 * Attempts to sign in or register the account specified by the login form.
	 * If there are form errors (invalid email, missing fields, etc.), the
	 * errors are presented and no actual login attempt is made.
	 */
	public void attemptLogin(EditText mLoginView, EditText mPasswordView) {
		if (mAuthTask != null) {
			return;
		}

		// Reset errors.
		mLoginView.setError(null);
		mPasswordView.setError(null);

		// Store values at the time of the login attempt.
		String mLogin = mLoginView.getText().toString();
		String mPassword = mPasswordView.getText().toString();

		// Check for a valid email address.
		if (FieldValidators.isFieldEmpty(mLogin)) {
			mLoginView.setError(getString(R.string.error_field_required));
			return;
		}
		
		// Check for a valid password.
		if (FieldValidators.isFieldEmpty(mPassword)) {
			mPasswordView.setError(getString(R.string.error_field_required));
			return;
		} else if (FieldValidators.isFieldShorterThan(mPassword, 4)) {
			mPasswordView.setError(getString(R.string.error_invalid_password));
			return;
		}

		mAuthTask = new UserLoginTask(mLogin, mPassword, mLoginView, mPasswordView);
		mAuthTask.execute();
	}

	/**
	 * Represents an asynchronous login/registration task used to authenticate
	 * the user.
	 */
	public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {
		JSONObject responseObject;
		private Dialog progressDialog;
		
		protected String mLogin;
		protected String mPassword;
		protected EditText loginView;
		protected EditText passwordView;
		
		public UserLoginTask(String login, String password, EditText mLoginView, EditText mPasswordView){
			this.mLogin = login;
			this.mPassword = password;
			this.loginView = mLoginView;
			this.passwordView = mPasswordView;
		}
		
		@SuppressWarnings("unchecked")
		@Override
		protected Boolean doInBackground(Void... params) {			
			Map<String, String> parameters = new LinkedHashMap<String, String>();
			parameters.put("login", mLogin);
			parameters.put("password", mPassword);
			
			Map<String, Map<String, String>> superParams = new LinkedHashMap<String, Map<String, String>>();
			superParams.put("session", parameters);

			String result = NetworkOperations.postJSONExpectingStringTo("/api/users/sign_in", JSONValue.toJSONString(superParams));
			if(result==null)
				return false;
			
			responseObject = (JSONObject) JSONValue.parse(result);	
				
			if(responseObject.containsKey("success") && !((Boolean) responseObject.get("success"))) {
				return false;
			} else {
				User.storeWithParams(responseObject, (String) responseObject.get("auth_token"));
				AnalyticsBase.registerUser(User.id(), getApplicationContext());
				return true;
			}
		}

		@Override
		protected void onPreExecute() {
		    super.onPreExecute();
		    progressDialog = DialogBuilder.buildLoadingDialogWithMessage(LandingActivity.this, R.string.signing_in);
		    progressDialog.show();
		}
		
		@Override
		protected void onPostExecute(final Boolean success) {
			super.onPostExecute(success);
			mAuthTask = null;
			progressDialog.dismiss();
			
			if (success) {
				if(User.isRegisteredLocally()) {
					AppBase.launchActivity(RootActivity.class);
					showGreetMessage();
					finish();
				}
			} else {
				if(responseObject==null) {
					DialogBuilder.displayAlertWithTitleAndMessage(AppBase.currentActivity, R.string.neutral, R.string.connectivity_problems);
				} else {
					passwordView.setError((String) responseObject.get("message"));
					loginView.setError((String) responseObject.get("message"));

					loginView.requestFocus();
				}
			}
			
		}

		@Override
		protected void onCancelled() {
			mAuthTask = null;
		}
	}
}
