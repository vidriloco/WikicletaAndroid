package org.wikicleta.activities;

import java.util.LinkedHashMap;
import java.util.Map;

import org.wikicleta.R;
import org.wikicleta.common.AppBase;
import org.wikicleta.common.FieldValidators;
import org.wikicleta.models.User;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public class LoginActivity extends Activity {

	/**
	 * Keep track of the login task to ensure we can cancel it if requested.
	 */
	private UserLoginTask mAuthTask = null;

	// Values for email and password at the time of the login attempt.
	private String mEmail;
	private String mPassword;

	// UI references.
	private EditText mEmailView;
	private EditText mPasswordView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTheme(R.style.Theme_wikicleta);

		setContentView(R.layout.activity_login);
		AppBase.currentActivity = this;
		
		// Set up the login form
		mEmailView = (EditText) findViewById(R.id.email);
		mPasswordView = (EditText) findViewById(R.id.password);
		
		mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView textView, int id,
					KeyEvent keyEvent) {
				if (id == R.id.login || id == EditorInfo.IME_NULL) {
					attemptLogin();
					return true;
				}
				return false;
			}
		});

		findViewById(R.id.sign_in_button).setOnClickListener(
			new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					attemptLogin();
				}
		});
		
		findViewById(R.id.register_button).setOnClickListener(
			new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					AppBase.launchActivity(RegistrationActivity.class);
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
		mEmailView.setError(null);
		mPasswordView.setError(null);

		// Store values at the time of the login attempt.
		mEmail = mEmailView.getText().toString();
		mPassword = mPasswordView.getText().toString();

		// Check for a valid email address.
		if (FieldValidators.isFieldEmpty(mEmail)) {
			mEmailView.setError(getString(R.string.error_field_required));
			return;
		} else if(!FieldValidators.isFieldAValidEmail(mEmail)) {
			mEmailView.setError(getString(R.string.error_invalid_email));
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
		mAuthTask.execute((Void) null);
	}

	/**
	 * Represents an asynchronous login/registration task used to authenticate
	 * the user.
	 */
	public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {
		
		private ProgressDialog progressDialog;
		
		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO: attempt authentication against a network service.
			Log.i("Wikicleta", "Attempting login ... ");
			try {
				Map<String, String> parameters = new LinkedHashMap<String, String>();
				parameters.put("email", mEmail);
				parameters.put("password", mPassword);
				
				Map<String, Map<String, String>> superParams = new LinkedHashMap<String, Map<String, String>>();
				superParams.put("session", parameters);
				Log.i("Wikicleta", "Sending params  ... ");

				//String result = NetworkOperations.postJSONTo("/api/sessions", JSONValue.toJSONString(superParams));
				//JSONObject object =(JSONObject) JSONValue.parse(result);	
								
				//User.storeWithParams(parameters, (String) object.get("token"));
			} catch (Exception e) {
				// In case authentication fails 
				return false;
			}

			return true;
		}

		@Override
		protected void onPreExecute() {
		    super.onPreExecute();
		    progressDialog = ProgressDialog.show(AppBase.currentActivity, "", 
		            "Attempting to log-in", true);
		}
		
		@Override
		protected void onPostExecute(final Boolean success) {
			super.onPostExecute(success);
			mAuthTask = null;
			progressDialog.dismiss();
			
			if (success) {
				if(User.isSignedIn()) {
					Intent intent = new Intent(AppBase.currentActivity, MainMapActivity.class);
					AppBase.currentActivity.startActivity(intent);
					finish();
				} else {
					Log.e("Wikicleta", "Something rare ocurred");
				}
			} else {
				mPasswordView.setError(getString(R.string.error_incorrect_password));
				mPasswordView.requestFocus();
			}
			
		}

		@Override
		protected void onCancelled() {
			mAuthTask = null;
		}
	}
}
