package org.wikicleta.views;

import java.util.Date;

import org.wikicleta.R;
import org.wikicleta.activities.MainMapActivity;
import org.wikicleta.activities.parkings.ModifyingActivity;
import org.wikicleta.common.AppBase;
import org.wikicleta.common.NetworkOperations;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ParkingViews {

    public static void buildViewForParking(final Activity activity, final Parking parking) {
    	
    	AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        final View view = inflater.inflate(R.layout.parking_details, null);
        
        String type = activity.getResources().getString(
        		activity.getResources().getIdentifier(
        				"parkings.kinds.".concat(parking.kindString()), "string", activity.getPackageName()));
        
        TextView modelNamed = (TextView) view.findViewById(R.id.model_named);
        modelNamed.setTypeface(AppBase.getTypefaceStrong());

        TextView title = (TextView) view.findViewById(R.id.parking_kind_title);
        title.setText(type);
        title.setTypeface(AppBase.getTypefaceStrong());
        
        TextView details = (TextView) view.findViewById(R.id.parking_details);
        details.setText(parking.details);
        details.setTypeface(AppBase.getTypefaceLight());
        
        TextView creationLegend = (TextView) view.findViewById(R.id.parking_created_date);
        
        PrettyTime ptime = new PrettyTime();
        creationLegend.setText(activity.getResources().getString(R.string.updated_on).concat(" ").concat(ptime.format(new Date(parking.updatedAt))));
        creationLegend.setTypeface(AppBase.getTypefaceLight());
        
        LinearLayout modifyContainer = (LinearLayout) view.findViewById(R.id.modify_button_container);
        LinearLayout destroyContainer = (LinearLayout) view.findViewById(R.id.delete_button_container);
        
        TextView creatorName = (TextView) view.findViewById(R.id.parking_creator);
        
        String username = parking.userId == User.id() ? activity.getResources().getString(R.string.you) : parking.username;
        
        creatorName.setText(activity.getResources().getString(R.string.created_by).concat(" ").concat(username));
        creatorName.setTypeface(AppBase.getTypefaceStrong());
        
        if(parking.hasPic()) {
            ImageView ownerPic = (ImageView) view.findViewById(R.id.parking_creator_pic);
            
            ImageUpdater updater = Others.getImageFetcher();
            updater.setImageAndImageProcessor(ownerPic, Others.ImageProcessor.ROUND_FOR_MINI_USER_PROFILE);
            updater.execute(NetworkOperations.serverHost.concat(parking.userPicURL));
        }

        ImageView iconImage = (ImageView) view.findViewById(R.id.parking_kind_icon);
        iconImage.setImageDrawable(activity.getResources().getDrawable(parking.getDrawable()));
        
        builder.setView(view);
        final AlertDialog parkingDialog = builder.create();
        view.findViewById(R.id.dialog_close).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				parkingDialog.dismiss();
			}
        	
        });
        
        if(parking.isOwnedByCurrentUser() || parking.anyoneCanEdit) {
        	TextView modifyButton = (TextView) view.findViewById(R.id.button_modify);
            modifyButton.setTypeface(AppBase.getTypefaceStrong());
            
            modifyContainer.setOnClickListener(new OnClickListener() {

    			@Override
    			public void onClick(View v) {
    				parkingDialog.dismiss();
    				Bundle bundle = new Bundle();
    				bundle.putSerializable("parking", parking);
    				AppBase.launchActivityWithBundle(ModifyingActivity.class, bundle);
    			}
            	
            });
        }
        
        if(parking.isOwnedByCurrentUser()) {
            TextView destroyButton = (TextView) view.findViewById(R.id.button_delete);
            destroyButton.setTypeface(AppBase.getTypefaceStrong());
            
            destroyContainer.setOnClickListener(new OnClickListener() {

    			@Override
    			public void onClick(View v) {
    				AlertDialog.Builder builder = DialogBuilder.buildAlertWithTitleAndMessage(activity, R.string.question, R.string.tips_question_delete);
    				
    				final AlertDialog alert = builder.setNegativeButton(R.string.confirm_no, new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							parkingDialog.show();
						}

    					
    				}).setPositiveButton(R.string.confirm_yes, new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							Parkings.Delete parkingDelete = new Parkings().new Delete();
							parkingDelete.activity = (MainMapActivity) activity;
							parkingDelete.execute(parking);
							parkingDialog.dismiss();
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
    				parkingDialog.hide();
    			}
            });
        } 
        
        if(!parking.isOwnedByCurrentUser())
        	destroyContainer.setVisibility(View.GONE);
        
        if(!parking.anyoneCanEdit && !parking.isOwnedByCurrentUser())
        	view.findViewById(R.id.action_buttons_container).setVisibility(View.GONE);
        
        parkingDialog.show();
    }
}
