package com.wikicleta.helpers;

import org.mobility.wikicleta.R;
import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

public class NotificationBuilder {

    public static NotificationManager mNotificationManager;
    private Activity activity;

    public NotificationBuilder(Activity activity) {
    	this.activity = activity;
    }

    public static void clearNotification(int id) {
    	if(mNotificationManager!=null)
    		mNotificationManager.cancel(id);
    }

	public void addNotification(int id, String title, String message, Class<?> cls) {
		clearNotification(id);
    	NotificationCompat.Builder builder =  
                new NotificationCompat.Builder(activity)  
                .setSmallIcon(R.drawable.wikicleta_mini)  
                .setContentTitle(title)  
                .setContentText(message).setTicker(message);  
      
    	if(cls != null) {
    		Intent notificationIntent = new Intent(activity, cls);  
            PendingIntent contentIntent = PendingIntent.getActivity(activity, 0, notificationIntent,   
                    PendingIntent.FLAG_UPDATE_CURRENT);  
            builder.setContentIntent(contentIntent); 
    	}
      
        // Add as notification  
        mNotificationManager = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);  
        mNotificationManager.notify(id, builder.build());   
    }
}