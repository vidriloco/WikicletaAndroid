package org.mobility.wikicleta;

import org.wikicleta.common.AppBase;
import org.wikicleta.common.FieldValidators;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

public class RegistrationActivity extends Activity {
	
	private String mEmail;
	private String mPassword;
	private String mPasswordConfirmation;
	private String mName;
	
	private EditText mEmailView;
	private EditText mNameView;
	private EditText mPasswordView;
	private EditText mPasswordConfirmationView;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AppBase.currentActivity = this;
		
		setContentView(R.layout.activity_registration);
		
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
		// Populate registration fields
		mEmail = mEmailView.getText().toString();
		mPassword = mPasswordView.getText().toString();
		mName = mNameView.getText().toString();
		mPasswordConfirmation = mPasswordConfirmationView.getText().toString();
		
		if (TextUtils.isEmpty(mEmail)) {
			mEmailView.setError(getString(R.string.error_field_required));

		} else if(FieldValidators.isFieldAValidEmail(mEmail)) {
			mEmailView.setError(getString(R.string.error_invalid_email));

		}
	}
}
