package org.wikicleta.helpers;

import org.wikicleta.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class DialogBuilder {

	public static AlertDialog buildLoadingDialogWithMessage(Activity activity, String message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        
        View view = inflater.inflate(R.layout.loading_dialog, null);
        TextView loadingViewMsj = (TextView) view.findViewById(R.id.loading_message);
        loadingViewMsj.setText(message);
        builder.setView(view);
        
        return builder.create();
	}
}
