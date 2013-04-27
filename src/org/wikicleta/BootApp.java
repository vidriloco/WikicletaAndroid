package org.wikicleta;

import android.annotation.SuppressLint;
import android.app.Application;

import com.activeandroid.ActiveAndroid;

public class BootApp extends Application {

	@SuppressLint("NewApi")
	@Override
	public void onCreate() {
		super.onCreate();
		//this.getDatabasePath("Wikicleta.db").delete();
		ActiveAndroid.initialize(this);
	}
}
