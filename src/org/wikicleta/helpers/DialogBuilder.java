package org.wikicleta.helpers;

import org.wikicleta.R;
import org.wikicleta.common.AppBase;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

public class DialogBuilder {
	protected static AlertDialog connectivityAlertDialog;

	public static Dialog buildLoadingDialogWithMessage(Activity activity, int id) {
		return buildLoadingDialogWithMessage(activity, activity.getResources().getString(id));
	}
	
	public static Dialog buildLoadingDialogWithMessage(Activity activity, String message) {
		Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        LayoutInflater inflater = activity.getLayoutInflater();
        
        View view = inflater.inflate(R.layout.dialog_loading, null);
        TextView loadingViewMsj = (TextView) view.findViewById(R.id.loading_message);
        loadingViewMsj.setTypeface(AppBase.getTypefaceLight());
        loadingViewMsj.setText(message);
        dialog.setContentView(view);
        
        return dialog;
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
		return buildAlertWithTitleAndMessage(ctx, ctx.getResources().getString(title), ctx.getResources().getString(message));
	}
	
	public static AlertDialog.Builder buildAlertWithTitleAndMessage(Activity ctx, String title, String message) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ctx);
		View alertDialogView = ctx.getLayoutInflater().inflate(R.layout.alert_dialog, null);
		alertDialogBuilder.setView(alertDialogView);
		TextView titleView = (TextView) alertDialogView.findViewById(R.id.dialog_title);
		titleView.setText(title);
		titleView.setTypeface(AppBase.getTypefaceStrong());

		TextView messageView = (TextView) alertDialogView.findViewById(R.id.dialog_message);
		messageView.setText(message);
		messageView.setTypeface(AppBase.getTypefaceLight());
		
		return alertDialogBuilder;
	}
	
	

}
