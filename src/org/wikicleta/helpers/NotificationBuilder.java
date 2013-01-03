package org.wikicleta.helpers;

import org.wikicleta.R;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

public class NotificationBuilder {

    public NotificationManager mNotificationManager;
    private Context context;

    public NotificationBuilder(Context activity) {
    	this.context = activity;
    }

    public void clearNotification(int id) {
    	if(mNotificationManager!=null)
    		mNotificationManager.cancel(id);
    }

	public void addNotification(int id, String title, String message, Class<?> cls) {
		clearNotification(id);
    	NotificationCompat.Builder builder =  
                new NotificationCompat.Builder(context)  
                .setSmallIcon(R.drawable.wikicleta_mini)  
                .setContentTitle(title)  
                .setContentText(message).setTicker(message);  
      
    	if(cls != null) {
    		Intent notificationIntent = new Intent(context, cls);  
            PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent,   
                    PendingIntent.FLAG_UPDATE_CURRENT);  
            builder.setContentIntent(contentIntent); 
    	}
      
        // Add as notification  
        mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);  
        mNotificationManager.notify(id, builder.build());   
    }
}