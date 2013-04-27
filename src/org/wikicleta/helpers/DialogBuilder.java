package org.wikicleta.helpers;

import org.wikicleta.R;
import org.wikicleta.common.AppBase;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class DialogBuilder {
	protected static AlertDialog connectivityAlertDialog;

	public static AlertDialog buildLoadingDialogWithMessage(Activity activity, String message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        
        View view = inflater.inflate(R.layout.loading_dialog, null);
        TextView loadingViewMsj = (TextView) view.findViewById(R.id.loading_message);
        loadingViewMsj.setText(message);
        builder.setView(view);
        
        return builder.create();
	}
	
	public static void displayAlertWithTitleAndMessage(Activity ctx, int title, int message) {
		AlertDialog.Builder alertDialogBuilder = DialogBuilder.buildAlertWithTitleAndMessage(ctx, title, message);
			
		alertDialogBuilder.setNeutralButton(ctx.getResources().getString(R.string.neutral), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int id) {
				connectivityAlertDialog.dismiss();
			}
		});

		connectivityAlertDialog = alertDialogBuilder.create();
		connectivityAlertDialog.show();
	}
	
	public static AlertDialog.Builder buildAlertWithTitleAndMessage(Activity ctx, int title, int message) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ctx);
		View alertDialogView = ctx.getLayoutInflater().inflate(R.layout.alert_dialog, null);
		alertDialogBuilder.setView(alertDialogView);
		TextView titleView = (TextView) alertDialogView.findViewById(R.id.dialog_title);
		titleView.setText(ctx.getResources().getString(R.string.notification));
		titleView.setTypeface(AppBase.getTypefaceStrong());

		TextView messageView = (TextView) alertDialogView.findViewById(R.id.dialog_message);
		messageView.setText(ctx.getResources().getString(message));
		messageView.setTypeface(AppBase.getTypefaceLight());
		
		return alertDialogBuilder;
	}
	
	

}
