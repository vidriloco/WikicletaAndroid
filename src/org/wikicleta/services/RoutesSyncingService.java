package org.wikicleta.services;

import org.wikicleta.R;
import org.wikicleta.activities.ActivityFeedsActivity;
import org.wikicleta.async.RouteUploader;
import org.wikicleta.common.Constants;
import org.wikicleta.helpers.NotificationBuilder;
import org.wikicleta.models.Route;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;

public class RoutesSyncingService extends Service {
	
	public static final int UPLOAD_ROUTE = 1;
	public static final int NOTIFY_ABOUT_STALLED = 2;
	
	protected RouteUploader routeUploader;
	protected NotificationBuilder notification;

	// Used to receive messages from the Activity
	final Messenger inMessenger = new Messenger(new IncomingHandler());
	// Use to send message to the Activity
	private Messenger outMessenger;
	
	
	@Override
	public IBinder onBind(Intent intent) {
		Bundle extras = intent.getExtras();
	    // Get messager from the Activity
	    if (extras != null) {
	      outMessenger = (Messenger) extras.get("MESSENGER");
	    }
	    // Return our messenger to the Activity to get commands
	    return inMessenger.getBinder();
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		this.routeUploader = new RouteUploader();
        this.notification = new NotificationBuilder(this);
	}
	 
	@Override
	public void onDestroy() {
		super.onDestroy();
		notification.clearNotification(Constants.ROUTES_SYNCING_NOTIFICATIONS_ID);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		return Service.START_STICKY;
	}
	
	protected void uploadRoute() {
		this.routeUploader.uploadRoute(Intercommunicator.fetchRouteAndClear());
		notification.addNotification(Constants.ROUTES_SYNCING_NOTIFICATIONS_ID, 
				getString(R.string.app_name), getString(R.string.route_being_sent), null);
	}
	
	protected void notifyAboutStalledRoutes() {
		if(queuedRoutesCount() > 0) {			
			String countString = ActivityFeedsActivity.queuedRoutesCount() == 1 ? 
					this.getString(R.string.route_drafts_notification_total_one) : 
						this.getString(R.string.route_drafts_notification_total_many, ActivityFeedsActivity.queuedRoutesCount()); 
			
			notification.addNotification(Constants.DRAFT_ROUTES_NOTIFICATION_ID, 
					getString(R.string.app_name), countString, ActivityFeedsActivity.class);
		}		
	}
	
	protected int queuedRoutesCount() {
		return Route.queued().size();
	}
	
	
	@SuppressLint("HandlerLeak")
	class IncomingHandler extends Handler {
	    @Override
	    public void handleMessage(Message msg) {
	      Bundle data = msg.getData();
	      
	      switch(data.getInt("action")){
	      	case UPLOAD_ROUTE:
	      		uploadRoute();
	           break;
	      	case NOTIFY_ABOUT_STALLED:
	    		notifyAboutStalledRoutes();
	           break;
	      
	      }
	      
	      /*
	      Message backMsg = Message.obtain();
	      backMsg.arg1 = result;
	      Bundle bundle = new Bundle();
	      bundle.putString(RESULTPATH, outputPath);
	      backMsg.setData(bundle);
	      try {
	        outMessenger.send(backMsg);
	      } catch (android.os.RemoteException e1) {
	        Log.w(getClass().getName(), "Exception sending message", e1);
	      }*/
	    }
	 }
}
