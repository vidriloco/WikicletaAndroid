package org.wikicleta.helpers;

import org.wikicleta.R;
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

	public void addNotification(int id, String title, String message, Intent notificationIntent, Ticker ticker) {
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
            PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent,   
                    PendingIntent.FLAG_UPDATE_CURRENT);  
            builder.setContentIntent(contentIntent); 
    	} 
      
    	// Only if needed, initialize the notification manager
    	initializeNotificationManager();
        // Add as notification  
        mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);  
        mNotificationManager.notify(id, builder.build());   
    }
	
	protected void initializeNotificationManager() {
		if(mNotificationManager == null)
			mNotificationManager = (NotificationManager) this.context.getSystemService(Context.NOTIFICATION_SERVICE);
	}
}