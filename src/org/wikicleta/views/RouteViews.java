package org.wikicleta.views;

import java.util.Date;

import org.wikicleta.R;
import org.wikicleta.common.AppBase;
import org.wikicleta.helpers.DialogBuilder;
import org.wikicleta.models.Route;
import org.wikicleta.models.User;
import org.wikicleta.routing.Others;
import org.wikicleta.routing.Others.ImageUpdater;

import com.ocpsoft.pretty.time.PrettyTime;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class RouteViews {

	public static void buildViewForRoute(final Activity activity, final Route route) {
    	final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_route_details);

        LinearLayout modifyContainer = (LinearLayout) dialog.findViewById(R.id.modify_button_container);
        LinearLayout destroyContainer = (LinearLayout) dialog.findViewById(R.id.delete_button_container);

        TextView title = (TextView) dialog.findViewById(R.id.route_name_text);
        title.setText(route.name);
        title.setTypeface(AppBase.getTypefaceStrong());
        
        TextView modelNamed = (TextView) dialog.findViewById(R.id.model_named);
        modelNamed.setTypeface(AppBase.getTypefaceStrong());
        
        TextView content = (TextView) dialog.findViewById(R.id.route_details_text);
        content.setText(route.details);
        content.setTypeface(AppBase.getTypefaceLight());
        
        TextView creationLegend = (TextView) dialog.findViewById(R.id.creation_date_text);
        
        PrettyTime ptime = new PrettyTime();
        creationLegend.setText(activity.getResources().getString(R.string.updated_on).concat(" ").concat(ptime.format(new Date(route.updatedAt))));
        creationLegend.setTypeface(AppBase.getTypefaceLight());
        
        TextView creatorName = (TextView) dialog.findViewById(R.id.contributor_text);
        
        String username = route.userId == User.id() ? activity.getResources().getString(R.string.you) : route.username;
        
        creatorName.setText(activity.getResources().getString(R.string.created_by).concat(" ").concat(username));
        creatorName.setTypeface(AppBase.getTypefaceStrong());
        
        if(route.hasPic()) {
            ImageView ownerPic = (ImageView) dialog.findViewById(R.id.contributor_pic);
            
            ImageUpdater updater = Others.getImageFetcher();
            updater.setImageAndImageProcessor(ownerPic, Others.ImageProcessor.ROUND_FOR_MINI_USER_PROFILE);
            updater.execute(route.userPicURL);
        }
        
        // Common actions for POIs
        TextView positiveRankingLegend = (TextView) dialog.findViewById(R.id.positive_button_ranks_text);
        positiveRankingLegend.setText("100");
        positiveRankingLegend.setTypeface(AppBase.getTypefaceStrong());

        TextView negativeRankingLegend = (TextView) dialog.findViewById(R.id.negative_button_ranks_text);
        negativeRankingLegend.setText("30");
        negativeRankingLegend.setTypeface(AppBase.getTypefaceStrong());
        
        TextView moreInfoText = (TextView) dialog.findViewById(R.id.route_more_info_text);
        moreInfoText.setTypeface(AppBase.getTypefaceStrong());
        
        dialog.findViewById(R.id.route_more_info_container_button).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
			}
        	
        });
        
        dialog.findViewById(R.id.dialog_close).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
        	
        });
        
        if(route.isOwnedByCurrentUser()) {
        	TextView modifyButton = (TextView) dialog.findViewById(R.id.button_modify);
            modifyButton.setTypeface(AppBase.getTypefaceStrong());
            
            modifyContainer.setOnClickListener(new OnClickListener() {

    			@Override
    			public void onClick(View v) {
    				dialog.dismiss();
    				Bundle bundle = new Bundle();
    				bundle.putSerializable("route", route);
    				//AppBase.launchActivityWithBundle(ModifyingActivity.class, bundle);
    			}
            	
            });
            
            TextView destroyButton = (TextView) dialog.findViewById(R.id.button_delete);
            destroyButton.setTypeface(AppBase.getTypefaceStrong());
            
            destroyContainer.setOnClickListener(new OnClickListener() {

    			@Override
    			public void onClick(View v) {
    				AlertDialog.Builder builder = DialogBuilder.buildAlertWithTitleAndMessage(activity, R.string.question, R.string.tips_question_delete);
    				
    				final AlertDialog alert = builder.setNegativeButton(R.string.confirm_no, new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialogLocal, int which) {
							dialogLocal.dismiss();
							dialog.show();
						}

    					
    				}).setPositiveButton(R.string.confirm_yes, new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							//Routes.Delete routesDelete = new Routes().new Delete();
							//routesDelete.activity = (DiscoverActivity) activity;
							//routesDelete.execute(route);
							dialog.dismiss();
						}

    					
    				}).create();
    				
    				alert.setOnShowListener(new DialogInterface.OnShowListener() {
    	    		    @Override
    	    		    public void onShow(DialogInterface dialog) {
    	    		        Button btnPositive = alert.getButton(Dialog.BUTTON_POSITIVE);
    	    		        btnPositive.setTextSize(13);
    	    		        btnPositive.setTypeface(AppBase.getTypefaceStrong());
    	    		        
    	    		        Button btnNegative = alert.getButton(Dialog.BUTTON_NEGATIVE);
    	    		        btnNegative.setTextSize(13);
    	    		        btnNegative.setTypeface(AppBase.getTypefaceStrong());
    	    		    }
    	    		});
    				
    				alert.show();
    				dialog.hide();
    			}
            });
        } else {
        	dialog.findViewById(R.id.action_buttons_container).setVisibility(View.GONE);
        }
        
        dialog.show();
	}
}
