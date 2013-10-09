package org.wikicleta.helpers;

import org.wikicleta.R;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

public class NotificationBuilder {

	public enum Ticker {MESSAGE, TITLE}
    protected static NotificationManager mNotificationManager;
    private Context context;
    
    
    public NotificationBuilder(Context activity) {
    	this.context = activity;
    }

    public void clearNotification(int id) {
    	// Only if needed, initialize the notification manager
    	initializeNotificationManager();
    	mNotificationManager.cancel(id);
    }

	public Notification addNotification(int id, String title, String message, Intent notificationIntent, Ticker ticker, boolean notify) {
		clearNotification(id);
    	NotificationCompat.Builder builder =  
                new NotificationCompat.Builder(context)  
                .setSmallIcon(R.drawable.wikicleta_nav_icon)  
                .setContentTitle(title)  
                .setContentText(message);
    	
    	if(ticker.equals(Ticker.MESSAGE))
    		builder.setTicker(message);
    	else
    		builder.setTicker(title);
      
    	if(notificationIntent != null) {
    		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|
	                 Intent.FLAG_ACTIVITY_SINGLE_TOP);
    		
            PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent,   
                    PendingIntent.FLAG_UPDATE_CURRENT);  
            builder.setContentIntent(contentIntent); 
    	} 
      
        Notification note = builder.build();
        note.flags|=Notification.FLAG_NO_CLEAR;
        
        if(notify) {
        	// Only if needed, initialize the notification manager
        	initializeNotificationManager();  
            mNotificationManager.notify(id, note);
        }
        
        return note;
    }
	
	protected void initializeNotificationManager() {
		if(mNotificationManager == null)
			mNotificationManager = (NotificationManager) this.context.getSystemService(Context.NOTIFICATION_SERVICE);
	}
}