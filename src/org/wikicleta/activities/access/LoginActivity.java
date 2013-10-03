package org.wikicleta.activities.access;

import java.util.LinkedHashMap;
import java.util.Map;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.wikicleta.R;
import org.wikicleta.activities.RootActivity;
import org.wikicleta.common.AppBase;
import org.wikicleta.common.FieldValidators;
import org.wikicleta.common.NetworkOperations;
import org.wikicleta.helpers.DialogBuilder;
import org.wikicleta.helpers.SlidingMenuBuilder;
import org.wikicleta.models.User;

import android.app.AlertDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public class LoginActivity extends AccessActivity {

	/**
	 * Keep track of the login task to ensure we can cancel it if requested.
	 */
	private UserLoginTask mAuthTask = null;

	// Values for email and password at the time of the login attempt.
	private String mLogin;
	private String mPassword;

	// UI references.
	private EditText mLoginView;
	private EditText mPasswordView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTheme(R.style.Theme_wikicleta);

		setContentView(R.layout.activity_login);
		AppBase.currentActivity = this;
		
		SlidingMenuBuilder.loadOnLeft(this);
    	/*ActionBar actionBar = (ActionBar) this.findViewById(R.id.actionbar);

        actionBar.addAction(new Action() {

			@Override
			public int getDrawable() {
				return R.drawable.close_icon;
			}

			@Override
			public void performAction(View view) {
				AppBase.launchActivity(LandingActivity.class);
			}
        	
        });*/
    	
        TextView loginPrologue = (TextView) findViewById(R.id.login_prologue);
        loginPrologue.setTypeface(AppBase.getTypefaceLight());
        
		// Set up the login form
		mLoginView = (EditText) findViewById(R.id.login_input_field);
		mLoginView.setTypeface(AppBase.getTypefaceLight());
		mPasswordView = (EditText) findViewById(R.id.password_input_field);
		mPasswordView.setTypeface(AppBase.getTypefaceLight());

		Button signInButton = (Button) findViewById(R.id.sign_in_button);
		signInButton.setTypeface(AppBase.getTypefaceStrong());
		
		signInButton.setOnClickListener(
			new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					attemptLogin();
				}
		});

	}

	/**
	 * Attempts to sign in or register the account specified by the login form.
	 * If there are form errors (invalid email, missing fields, etc.), the
	 * errors are presented and no actual login attempt is made.
	 */
	public void attemptLogin() {
		if (mAuthTask != null) {
			return;
		}

		// Reset errors.
		mLoginView.setError(null);
		mPasswordView.setError(null);

		// Store values at the time of the login attempt.
		mLogin = mLoginView.getText().toString();
		mPassword = mPasswordView.getText().toString();

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

		// Show a progress spinner, and kick off a background task to
		// perform the user login attempt.
		mAuthTask = new UserLoginTask();
		mAuthTask.execute();
	}

	/**
	 * Represents an asynchronous login/registration task used to authenticate
	 * the user.
	 */
	public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {
		JSONObject responseObject;
		private AlertDialog progressDialog;
		
		@SuppressWarnings("unchecked")
		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO: attempt authentication against a network service.
			
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
				return true;
			}
		}

		@Override
		protected void onPreExecute() {
		    super.onPreExecute();
		    progressDialog = DialogBuilder.buildLoadingDialogWithMessage(LoginActivity.this, R.string.signing_in).create();
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
				}
			} else {
				if(responseObject==null) {
					DialogBuilder.displayAlertWithTitleAndMessage(AppBase.currentActivity, R.string.neutral, R.string.connectivity_problems);
				} else {
					mPasswordView.setError((String) responseObject.get("message"));
					mLoginView.setError((String) responseObject.get("message"));

					mLoginView.requestFocus();
				}
			}
			
		}

		@Override
		protected void onCancelled() {
			mAuthTask = null;
		}
	}
}
