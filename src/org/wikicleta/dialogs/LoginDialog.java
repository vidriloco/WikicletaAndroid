package org.wikicleta.dialogs;

import org.wikicleta.R;
import org.wikicleta.common.AppBase;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class LoginDialog extends Dialog implements android.view.View.OnClickListener {

	LoginDialogListener listener = null;
	private TextView loginText = null;
	private EditText loginUsername = null;
	private EditText loginPassword = null;
	private LinearLayout returnButton = null;
	private LinearLayout loginButton = null;
	
	public LoginDialog(Context context, LoginDialogListener listener) {
		super(context);
		this.listener = listener;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dialog_login);
		loginText = (TextView)findViewById(R.id.login_text);
		loginText.setTypeface(AppBase.getTypefaceStrong());
		loginUsername = (EditText)findViewById(R.id.login_username);
		loginPassword = (EditText)findViewById(R.id.login_password);
		returnButton = (LinearLayout)findViewById(R.id.return_button_container);
		returnButton.setOnClickListener(this);
		loginButton = (LinearLayout)findViewById(R.id.login_button_container);
		loginButton.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id==R.id.return_button_container)
			this.dismiss();
		else if(id==R.id.login_button_container){
			listener.onLoginButtonPressed();
			this.dismiss();
		}
	}
	
	public String getUsername(){
		return loginUsername.getText().toString();
	}
	
	public String getPassword(){
		return loginPassword.getText().toString();
	}
	
	public EditText getUsernameEditText(){
		return loginUsername;
	}
	
	public EditText getPasswordEditText(){
		return loginPassword;
	}

	public interface LoginDialogListener{
		public void onLoginButtonPressed();
	}
	
	@Override
	public void dismiss() {
		listener = null;
		loginText = null;
		loginUsername = null;
		loginPassword = null;
		returnButton = null;
		loginButton = null;
		super.dismiss();
	}
}
