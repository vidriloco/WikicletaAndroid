package org.wikicleta.services.routes;

import android.app.Activity;
import android.app.Service;

public interface ServiceBinder {
	public Service getService();
	public void setBindingActivity(Activity activity);
}