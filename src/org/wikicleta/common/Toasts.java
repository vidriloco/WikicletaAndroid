package org.wikicleta.common;

import org.wikicleta.R;

import android.app.Activity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class Toasts {
	public static void showToastWithMessage(Activity activity, int message, int icon) {
		LayoutInflater inflater = activity.getLayoutInflater();
		View layout = inflater.inflate(R.layout.message,
		                               (ViewGroup) activity.findViewById(R.id.toast_layout_root));
		
		ImageView iconPlaceholder = (ImageView) layout.findViewById(R.id.message_icon);
		iconPlaceholder.setImageDrawable(activity.getResources().getDrawable(icon));
		
		TextView text = (TextView) layout.findViewById(R.id.message_text);
		text.setTypeface(AppBase.getTypefaceLight());
		text.setText(message);
		Toast toast = new Toast(activity.getApplicationContext());
		toast.setDuration(Toast.LENGTH_LONG);
		toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL, 0, 0);
		toast.setView(layout);
		toast.show();
	}
	
	public static void showToastWithMessage(Activity activity, String message, int icon) {
		LayoutInflater inflater = activity.getLayoutInflater();
		View layout = inflater.inflate(R.layout.message,
		                               (ViewGroup) activity.findViewById(R.id.toast_layout_root));
		
		ImageView iconPlaceholder = (ImageView) layout.findViewById(R.id.message_icon);
		iconPlaceholder.setImageDrawable(activity.getResources().getDrawable(icon));
		
		TextView text = (TextView) layout.findViewById(R.id.message_text);
		text.setTypeface(AppBase.getTypefaceLight());
		text.setText(message);
		Toast toast = new Toast(activity.getApplicationContext());
		toast.setDuration(Toast.LENGTH_LONG);
		toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL, 0, 0);
		toast.setView(layout);
		toast.show();
	}
}
