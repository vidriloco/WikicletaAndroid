package org.wikicleta.services;

import android.app.Activity;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

public class ServiceConstructor {

	// Service connectors
	protected Service theService;
    protected boolean serviceBound = false;
    protected Activity activity;
    
    public ServiceConstructor(Activity activity) {
    	this.activity = activity;
    }
    
    public void start(Class<?> serviceClass) {
    	Intent intent = new Intent(activity, serviceClass);
    	activity.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }
    
    public void stop() {
    	if (serviceBound) {
            activity.unbindService(serviceConnection);
            serviceBound = false;
        }
    }
    
	// Decide if we should leave a service implementation on this activity
    protected ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                IBinder binder) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
        	ServiceBinder serviceBinder = (ServiceBinder) binder;
            theService = serviceBinder.getService();
            serviceBinder.setBindingActivity(activity);
            
            serviceBound = true;
            if(activity instanceof ServiceListener) 
            	((ServiceListener) activity).afterServiceConnected(theService);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
        	serviceBound = false;
        }
    };
}
