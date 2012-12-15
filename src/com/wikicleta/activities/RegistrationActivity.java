package com.wikicleta.activities;

import java.util.LinkedHashMap;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.mobility.wikicleta.R;

import com.wikicleta.common.AppBase;
import com.wikicleta.common.FieldValidators;
import com.wikicleta.common.NetworkOperations;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class RegistrationActivity extends LoadingWithMessageActivity {
	
	private String mEmail;
	private String mPassword;
	private String mPasswordConfirmation;
	private String mName;
	
	private EditText mEmailView;
	private EditText mNameView;
	private EditText mPasswordView;
	private EditText mPasswordConfirmationView;
	
	private UserRegistrationTask mRegAuthTask = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AppBase.currentActivity = this;
		
		setContentView(R.layout.activity_registration);
		
		// Set up the login form
		mNameView = (EditText) findViewById(R.id.name);
		mEmailView = (EditText) findViewById(R.id.email);
		mPasswordView = (EditText) findViewById(R.id.password);
		mPasswordConfirmationView = (EditText) findViewById(R.id.password_confirmation);
		
		previousContainerView = findViewById(R.id.registration_form);
		messageContainerView = findViewById(R.id.signup_status);
		viewMessage = (TextView) findViewById(R.id.signup_status_message);
		
		findViewById(R.id.registration_back_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(AppBase.currentActivity, LoginActivity.class);
						AppBase.currentActivity.startActivity(intent);
					}
		});
		
		findViewById(R.id.registration_accept_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
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
		
		// Populate registration fields
		mEmail = mEmailView.getText().toString();
		mPassword = mPasswordView.getText().toString();
		mName = mNameView.getText().toString();
		mPasswordConfirmation = mPasswordConfirmationView.getText().toString();
		
		if(FieldValidators.isFieldEmpty(mName)) {
			mNameView.setError(getString(R.string.error_field_required));
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
		
		viewMessage.setText(R.string.login_progress_signing_up);
		showProgress(true);
		
		mRegAuthTask = new UserRegistrationTask();
		mRegAuthTask.execute((Void) null);
	}
	
	/**
	 * Represents an asynchronous login/registration task used to authenticate
	 * the user.
	 */
	public class UserRegistrationTask extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO: attempt authentication against a network service.

			try {
				Map<String, String> parameters = new LinkedHashMap<String, String>();
				parameters.put("email", mEmail);
				parameters.put("password", mPassword);
				parameters.put("name", mName);
				
				Map<String, Map<String, String>> superParams = new LinkedHashMap<String, Map<String, String>>();
				superParams.put("registration", parameters);
				
				String result = NetworkOperations.postTo("/api/registrations", superParams);
				Log.i("WWWWW", result);

				Object obj = JSONValue.parse(result);
				JSONObject object =(JSONObject) obj;
			} catch (Exception e) {
				return false;
			}

			return true;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			mRegAuthTask = null;
			showProgress(false);

			if (success) {
				finish();
			} else {
				mPasswordView
						.setError(getString(R.string.error_incorrect_password));
				mPasswordView.requestFocus();
			}
		}

		@Override
		protected void onCancelled() {
			mRegAuthTask = null;
			showProgress(false);
		}
	}
}
