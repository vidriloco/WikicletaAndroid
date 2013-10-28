package org.wikicleta.views;

import java.util.Date;
import org.wikicleta.R;
import org.wikicleta.activities.CommentsActivity;
import org.wikicleta.activities.DiscoverActivity;
import org.wikicleta.activities.parkings.ModifyingActivity;
import org.wikicleta.analytics.AnalyticsBase;
import org.wikicleta.common.AppBase;
import org.wikicleta.helpers.DialogBuilder;
import org.wikicleta.models.Parking;
import org.wikicleta.models.User;
import org.wikicleta.routing.Others;
import org.wikicleta.routing.Others.ImageUpdater;
import org.wikicleta.routing.Parkings;
import com.ocpsoft.pretty.time.PrettyTime;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ParkingViews extends BaseViews {
	
	public static void buildViewForParking(final Activity activity, final Parking parking) {
    	final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.setContentView(R.layout.dialog_parking_details);
        String type = activity.getResources().getString(
        		activity.getResources().getIdentifier(
        				"parkings.kinds.".concat(parking.kindString()), "string", activity.getPackageName()));
        
        TextView modelNamed = (TextView) dialog.findViewById(R.id.model_named);
        modelNamed.setTypeface(AppBase.getTypefaceStrong());

        TextView title = (TextView) dialog.findViewById(R.id.parking_kind_title);
        title.setText(type);
        title.setTypeface(AppBase.getTypefaceStrong());
        
        TextView details = (TextView) dialog.findViewById(R.id.parking_details);
        details.setText(parking.details);
        details.setTypeface(AppBase.getTypefaceLight());
        
        TextView creationLegend = (TextView) dialog.findViewById(R.id.creation_date_text);
        
        PrettyTime ptime = new PrettyTime();
        creationLegend.setText(activity.getResources().getString(R.string.updated_on).concat(" ").concat(ptime.format(new Date(parking.updatedAt))));
        creationLegend.setTypeface(AppBase.getTypefaceLight());
        
        LinearLayout modifyContainer = (LinearLayout) dialog.findViewById(R.id.modify_button_container);
        LinearLayout destroyContainer = (LinearLayout) dialog.findViewById(R.id.delete_button_container);
        
        TextView creatorName = (TextView) dialog.findViewById(R.id.contributor_text);
        
        String username = parking.userId == User.id() ? activity.getResources().getString(R.string.you) : parking.username;
        
        creatorName.setText(activity.getResources().getString(R.string.created_by).concat(" ").concat(username));
        creatorName.setTypeface(AppBase.getTypefaceStrong());
        
        if(parking.hasPic()) {
            ImageView ownerPic = (ImageView) dialog.findViewById(R.id.contributor_pic);
            ImageUpdater updater = Others.getImageFetcher();
            updater.setImageAndImageProcessor(ownerPic, Others.ImageProcessor.ROUND_FOR_MINI_USER_PROFILE);
            updater.execute(parking.userPicURL);
        }

		AnalyticsBase.reportLoggedInEvent("On Discover Activity: parking view", activity, "parking-id", String.valueOf(parking.remoteId));

        ImageView iconImage = (ImageView) dialog.findViewById(R.id.parking_kind_icon);
        iconImage.setImageDrawable(activity.getResources().getDrawable(parking.getDrawable()));
        
        dialog.findViewById(R.id.dialog_close).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
        	
        });
        
        // Common actions for POIs
        TextView positiveRankingLegend = (TextView) dialog.findViewById(R.id.positive_button_ranks_text);
        positiveRankingLegend.setText(String.valueOf(parking.likesCount));
        positiveRankingLegend.setTypeface(AppBase.getTypefaceStrong());

        TextView negativeRankingLegend = (TextView) dialog.findViewById(R.id.negative_button_ranks_text);
        negativeRankingLegend.setText(String.valueOf(parking.dislikesCount));
        negativeRankingLegend.setTypeface(AppBase.getTypefaceStrong());
        
        dialog.findViewById(R.id.positive_rankings_container).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Bundle bundle = new Bundle();
				bundle.putBoolean("like", true);
				CommentsActivity.selectedPoint = parking;
				AppBase.launchActivityWithBundle(CommentsActivity.class, bundle);
			}
        	
        });
        
        dialog.findViewById(R.id.negative_rankings_container).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Bundle bundle = new Bundle();
				bundle.putBoolean("false", true);
				CommentsActivity.selectedPoint = parking;
				AppBase.launchActivityWithBundle(CommentsActivity.class, bundle);
			}
        	
        });
        
        buildViewForFavoritedResource(dialog, parking.remoteId, "Parking");
        
        if(parking.isOwnedByCurrentUser()) {
        	TextView modifyButton = (TextView) dialog.findViewById(R.id.button_modify);
            modifyButton.setTypeface(AppBase.getTypefaceStrong());
            
            modifyContainer.setOnClickListener(new OnClickListener() {

    			@Override
    			public void onClick(View v) {
    				dialog.dismiss();
    				ModifyingActivity.parking = parking;
    				AppBase.launchActivity(ModifyingActivity.class);
    			}
            	
            });
        }
        
        if(parking.isOwnedByCurrentUser()) {
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
							Parkings.Delete parkingDelete = new Parkings().new Delete();
							parkingDelete.activity = (DiscoverActivity) activity;
							parkingDelete.execute(parking);
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
        } 
        
        if(!parking.isOwnedByCurrentUser())
        	destroyContainer.setVisibility(View.GONE);
        
        if(!parking.isOwnedByCurrentUser())
        	dialog.findViewById(R.id.action_buttons_container).setVisibility(View.GONE);
        
        dialog.show();
    }
}
