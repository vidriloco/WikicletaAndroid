package org.wikicleta.activities.access;

import java.util.LinkedHashMap;
import java.util.Map;
import org.json.simple.JSONArray;
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

public class RegistrationActivity extends AccessActivity {
	
	private String mEmail;
	private String mPassword;
	private String mPasswordConfirmation;
	private String mName;
	private String mUsername;
	private String mStickerNumber;
	
	private EditText mEmailView;
	private EditText mUsernameView;
	private EditText mNameView;
	private EditText mPasswordView;
	private EditText mPasswordConfirmationView;
	private EditText mStickerNumberView;
	
	protected AlertDialog alertDialog;
	private UserRegistrationTask mRegAuthTask = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTheme(R.style.Theme_wikicleta);

		AppBase.currentActivity = this;
		
		setContentView(R.layout.activity_registration);
		
		SlidingMenuBuilder.loadOnLeft(this);
		
		TextView registrationHint = (TextView) this.findViewById(R.id.registration_prologue);
		registrationHint.setTypeface(AppBase.getTypefaceLight());
    	
		TextView stickerHint = (TextView) this.findViewById(R.id.sticker_prologue);
		stickerHint.setTypeface(AppBase.getTypefaceLight());
		
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
		mStickerNumberView = (EditText) findViewById(R.id.sticker_number);
		mStickerNumberView.setTypeface(AppBase.getTypefaceLight());
		
		Button accept = (Button) findViewById(R.id.registration_accept_button);
		accept.setTypeface(AppBase.getTypefaceStrong());
		accept.setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						attemptSignup();
					}
		});
        
        this.alertDialog = DialogBuilder.buildLoadingDialogWithMessage(this, R.string.registering_user).create();
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
		mStickerNumberView.setError(null);
		
		// Populate registration fields
		mEmail = mEmailView.getText().toString();
		mUsername = mUsernameView.getText().toString();
		mPassword = mPasswordView.getText().toString();
		mName = mNameView.getText().toString();
		mPasswordConfirmation = mPasswordConfirmationView.getText().toString();
		mStickerNumber = mStickerNumberView.getText().toString();

		if(FieldValidators.isFieldEmpty(mStickerNumber)) {
			mStickerNumberView.setError(getString(R.string.error_field_required));
			return;
		}
		
		if(FieldValidators.isFieldEmpty(mName)) {
			mNameView.setError(getString(R.string.error_field_required));
			return;
		}
		
		if(FieldValidators.isFieldEmpty(mUsername)) {
			mUsernameView.setError(getString(R.string.error_field_required));
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
			// TODO: attempt authentication against a network service.

			Map<String, String> parameters = new LinkedHashMap<String, String>();
			parameters.put("username", mUsername);
			parameters.put("email", mEmail);
			parameters.put("password", mPassword);
			parameters.put("password_confirmation", mPassword);
			parameters.put("full_name", mName);
			parameters.put("invitation_code", mStickerNumber);
			
			Map<String, Map<String, String>> user = new LinkedHashMap<String, Map<String, String>>();
			user.put("registration", parameters);

			String result = NetworkOperations.postJSONExpectingStringTo("/api/users", JSONValue.toJSONString(user));
			if(result==null)
				return false;
			
			responseObject = (JSONObject) JSONValue.parse(result);
			if(responseObject.containsKey("errors")) {
				return false;
			} else {
				User.storeWithParams(responseObject, (String) responseObject.get("auth_token"));
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
					} else if(errorsObject.containsKey("invitation_code")) {
						array = (JSONArray) errorsObject.get("invitation_code");
						mStickerNumberView.setError((String) array.get(0));
						mStickerNumberView.requestFocus();
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
}
