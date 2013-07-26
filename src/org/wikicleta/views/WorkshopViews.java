package org.wikicleta.views;

import java.util.Date;

import org.wikicleta.R;
import org.wikicleta.activities.MainMapActivity;
import org.wikicleta.activities.workshops.ModifyingActivity;
import org.wikicleta.common.AppBase;
import org.wikicleta.common.NetworkOperations;
import org.wikicleta.helpers.DialogBuilder;
import org.wikicleta.models.User;
import org.wikicleta.models.Workshop;
import org.wikicleta.routing.Others;
import org.wikicleta.routing.Workshops;
import org.wikicleta.routing.Others.ImageUpdater;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ocpsoft.pretty.time.PrettyTime;

public class WorkshopViews {
	
	public static void buildViewForWorkshop(final Activity activity, final Workshop workshop) {
    	
    	AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        final View view = inflater.inflate(R.layout.workshop_details, null);
        
        TextView modelNamed = (TextView) view.findViewById(R.id.workshop_name);
        modelNamed.setText(workshop.name);
        modelNamed.setTypeface(AppBase.getTypefaceStrong());

        TextView title = (TextView) view.findViewById(R.id.workshop_category);
        int workshopCat = workshop.isStore ? R.string.workshop_store : R.string.workshop;
        
        title.setText(activity.getResources().getString(workshopCat));
        title.setTypeface(AppBase.getTypefaceStrong());
        
        TextView details = (TextView) view.findViewById(R.id.workshop_details);
        details.setText(workshop.details);
        details.setTypeface(AppBase.getTypefaceLight());
        
        TextView creationLegend = (TextView) view.findViewById(R.id.workshop_created_date);
        
        PrettyTime ptime = new PrettyTime();
        creationLegend.setText(activity.getResources().getString(R.string.updated_on).concat(" ").concat(ptime.format(new Date(workshop.updatedAt))));
        creationLegend.setTypeface(AppBase.getTypefaceLight());
        
        LinearLayout modifyContainer = (LinearLayout) view.findViewById(R.id.modify_button_container);
        LinearLayout destroyContainer = (LinearLayout) view.findViewById(R.id.delete_button_container);
        
        TextView creatorName = (TextView) view.findViewById(R.id.workshop_creator);
        
        String username = workshop.userId == User.id() ? activity.getResources().getString(R.string.you) : workshop.username;
        
        creatorName.setText(activity.getResources().getString(R.string.created_by).concat(" ").concat(username));
        creatorName.setTypeface(AppBase.getTypefaceStrong());
        
        // Hide or fill and show open/closed days information
        if(workshop.horary.length() > 0) {
        	((TextView) view.findViewById(R.id.workshop_horary_value)).setTypeface(AppBase.getTypefaceStrong());
        	TextView horaryText = (TextView) view.findViewById(R.id.workshop_horary_text);
        	horaryText.setTypeface(AppBase.getTypefaceLight());
        	horaryText.setText(workshop.horary);
        } else 
        	((LinearLayout) view.findViewById(R.id.workshop_horary_container)).setVisibility(View.GONE);
        
        // Hide twitter buttons
        if(workshop.twitter.length() > 0) {
        	TextView twitterText = (TextView) view.findViewById(R.id.workshop_twitter_text);
        	twitterText.setText(workshop.twitter);
        	twitterText.setTypeface(AppBase.getTypefaceLight());
        	
        	LinearLayout twitterButton = (LinearLayout) view.findViewById(R.id.twitter_buttons_container);
        	twitterButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					AppBase.launchTwitterActivityWithUsername(workshop.twitter);
				}
        		
        	});
        } else {
        	((LinearLayout) view.findViewById(R.id.twitter_buttons_container)).setVisibility(View.GONE);
        }
        
        // Hide webpage buttons
        if(workshop.webpage.length() > 0) {
        	TextView webpageText = (TextView) view.findViewById(R.id.workshop_webpage_text);
        	webpageText.setText(workshop.webpage);
        	webpageText.setTypeface(AppBase.getTypefaceLight());
        	
        	LinearLayout webpageButton = (LinearLayout) view.findViewById(R.id.webpage_buttons_container);
        	webpageButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					AppBase.launchBrowserActivityWithURL(workshop.webpage);
				}
        		
        	});

        } else {
        	((LinearLayout) view.findViewById(R.id.webpage_buttons_container)).setVisibility(View.GONE);
        }
        
        
        
        if(workshop.hasPic()) {
            ImageView ownerPic = (ImageView) view.findViewById(R.id.workshop_creator_pic);
            
            ImageUpdater updater = Others.getImageFetcher();
            updater.setImageAndImageProcessor(ownerPic, Others.ImageProcessor.ROUND_FOR_MINI_USER_PROFILE);
            updater.execute(NetworkOperations.serverHost.concat(workshop.userPicURL));
        }
        
        builder.setView(view);
        final AlertDialog workshopDialog = builder.create();
        view.findViewById(R.id.dialog_close).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				workshopDialog.dismiss();
			}
        	
        });
        
        if(workshop.isOwnedByCurrentUser() || workshop.anyoneCanEdit) {
        	TextView modifyButton = (TextView) view.findViewById(R.id.button_modify);
            modifyButton.setTypeface(AppBase.getTypefaceStrong());
            
            modifyContainer.setOnClickListener(new OnClickListener() {

    			@Override
    			public void onClick(View v) {
    				workshopDialog.dismiss();
    				Bundle bundle = new Bundle();
    				bundle.putSerializable("workshop", workshop);
    				AppBase.launchActivityWithBundle(ModifyingActivity.class, bundle);
    			}
            	
            });
        }
        
        if(workshop.isOwnedByCurrentUser()) {
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
							workshopDialog.show();
						}

    					
    				}).setPositiveButton(R.string.confirm_yes, new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							Workshops.Delete workshopDelete = new Workshops().new Delete();
							workshopDelete.activity = (MainMapActivity) activity;
							workshopDelete.execute(workshop);
							workshopDialog.dismiss();
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
    				workshopDialog.hide();
    			}
            });
        } 
        
        if(!workshop.isOwnedByCurrentUser())
        	destroyContainer.setVisibility(View.GONE);
        
        if(!workshop.anyoneCanEdit && !workshop.isOwnedByCurrentUser())
        	view.findViewById(R.id.action_buttons_container).setVisibility(View.GONE);
        
        workshopDialog.show();
        
        if(workshop.cellPhone > 0 || workshop.phone > 0) {
        	TextView cellPhoneText = (TextView) view.findViewById(R.id.workshop_cellphone_text);
        	if(workshop.cellPhone == 0)
        		cellPhoneText.setText("---");
        	else {
        		cellPhoneText.setText(String.valueOf(workshop.cellPhone));
        		LinearLayout buttonCellPhone = (LinearLayout) view.findViewById(R.id.workshop_cell_phone_container);

        		buttonCellPhone.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						workshopDialog.hide();
						Builder alertBuilder = DialogBuilder.buildAlertWithTitleAndMessage(activity, R.string.question, R.string.call_phone_number_confirmation);
						alertBuilder.setPositiveButton(R.string.confirm_yes, new DialogInterface.OnClickListener(){

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								AppBase.launchPhoneCallingActivity(String.valueOf(Long.valueOf(workshop.cellPhone)));								
							}
							
						});
						alertBuilder.setNegativeButton(R.string.confirm_no, new DialogInterface.OnClickListener(){

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
								workshopDialog.show();
							}
							
						});
						final AlertDialog alert = alertBuilder.create();
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
					}
        		});
        	}
        	cellPhoneText.setTypeface(AppBase.getTypefaceLight());
        	
        	TextView phoneText = (TextView) view.findViewById(R.id.workshop_phone_text);
        	if(workshop.phone == 0)
        		phoneText.setText("---");
        	else {
        		phoneText.setText(String.valueOf(workshop.phone));
        		LinearLayout buttonPhone = (LinearLayout) view.findViewById(R.id.workshop_phone_container);
        		buttonPhone.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						workshopDialog.hide();
						Builder alertBuilder = DialogBuilder.buildAlertWithTitleAndMessage(activity, R.string.question, R.string.call_phone_number_confirmation);
						alertBuilder.setPositiveButton(R.string.confirm_yes, new DialogInterface.OnClickListener(){

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								AppBase.launchPhoneCallingActivity(String.valueOf(Long.valueOf(workshop.phone)));								
							}
							
						});
						alertBuilder.setNegativeButton(R.string.confirm_no, new DialogInterface.OnClickListener(){

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
								workshopDialog.show();
							}
							
						});
						final AlertDialog alert = alertBuilder.create();
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
					}
        		});
        	}
        	phoneText.setTypeface(AppBase.getTypefaceLight());
        } else {
        	((LinearLayout) view.findViewById(R.id.contact_buttons_container)).setVisibility(View.GONE);
        }
    }
}
