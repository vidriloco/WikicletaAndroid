package org.wikicleta.views;

import java.util.Date;

import org.wikicleta.R;
import org.wikicleta.activities.CommentsActivity;
import org.wikicleta.activities.DiscoverActivity;
import org.wikicleta.activities.workshops.ModifyingActivity;
import org.wikicleta.analytics.AnalyticsBase;
import org.wikicleta.common.AppBase;
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
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ocpsoft.pretty.time.PrettyTime;

public class WorkshopViews extends BaseViews {
	
	protected static String modelNamed = "Workshop";

	public static void buildViewForWorkshop(final Activity activity, final Workshop workshop) {
    	final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.setContentView(R.layout.dialog_workshop_details);
                
        TextView modelNamed = (TextView) dialog.findViewById(R.id.workshop_name);
        modelNamed.setText(workshop.name);
        modelNamed.setTypeface(AppBase.getTypefaceStrong());

        TextView title = (TextView) dialog.findViewById(R.id.workshop_category);
        int workshopCat = workshop.isStore ? R.string.workshop_store : R.string.workshop;
        
        title.setText(activity.getResources().getString(workshopCat));
        title.setTypeface(AppBase.getTypefaceStrong());
        
        TextView details = (TextView) dialog.findViewById(R.id.workshop_details);
        details.setText(workshop.details);
        details.setTypeface(AppBase.getTypefaceLight());
        
        TextView creationLegend = (TextView) dialog.findViewById(R.id.creation_date_text);
        
        PrettyTime ptime = new PrettyTime();
        creationLegend.setText(activity.getResources().getString(R.string.updated_on).concat(" ").concat(ptime.format(new Date(workshop.updatedAt))));
        creationLegend.setTypeface(AppBase.getTypefaceLight());
        
        LinearLayout modifyContainer = (LinearLayout) dialog.findViewById(R.id.modify_button_container);
        LinearLayout destroyContainer = (LinearLayout) dialog.findViewById(R.id.delete_button_container);
        
        TextView creatorName = (TextView) dialog.findViewById(R.id.contributor_text);
        
        String username = workshop.userId == User.id() ? activity.getResources().getString(R.string.you) : workshop.username;
        
        creatorName.setText(activity.getResources().getString(R.string.created_by).concat(" ").concat(username));
        creatorName.setTypeface(AppBase.getTypefaceStrong());
        
        buildViewForFavoritedResource(dialog, workshop.remoteId, "Workshop");

        // Common actions for POIs
        TextView positiveRankingLegend = (TextView) dialog.findViewById(R.id.positive_button_ranks_text);
        positiveRankingLegend.setText(String.valueOf(workshop.likesCount));
        positiveRankingLegend.setTypeface(AppBase.getTypefaceStrong());

        TextView negativeRankingLegend = (TextView) dialog.findViewById(R.id.negative_button_ranks_text);
        negativeRankingLegend.setText(String.valueOf(workshop.dislikesCount));
        negativeRankingLegend.setTypeface(AppBase.getTypefaceStrong());
        
		AnalyticsBase.reportLoggedInEvent("On Discover Activity: workshop view", activity, "workshop-id", String.valueOf(workshop.remoteId));

        dialog.findViewById(R.id.positive_rankings_container).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Bundle bundle = new Bundle();
				bundle.putBoolean("like", true);
				CommentsActivity.selectedPoint = workshop;
				AppBase.launchActivityWithBundle(CommentsActivity.class, bundle);
			}
        	
        });
        
        dialog.findViewById(R.id.negative_rankings_container).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Bundle bundle = new Bundle();
				bundle.putBoolean("false", true);
				CommentsActivity.selectedPoint = workshop;
				AppBase.launchActivityWithBundle(CommentsActivity.class, bundle);
			}
        	
        });
        
        // Hide or fill and show open/closed days information
        if(workshop.horary.length() > 0) {
        	((TextView) dialog.findViewById(R.id.workshop_horary_value)).setTypeface(AppBase.getTypefaceStrong());
        	TextView horaryText = (TextView) dialog.findViewById(R.id.workshop_horary_text);
        	horaryText.setTypeface(AppBase.getTypefaceLight());
        	horaryText.setText(workshop.horary);
        } else 
        	((LinearLayout) dialog.findViewById(R.id.workshop_horary_container)).setVisibility(View.GONE);
        
        // Hide twitter buttons
        if(workshop.twitter.length() > 0) {
        	TextView twitterText = (TextView) dialog.findViewById(R.id.workshop_twitter_text);
        	twitterText.setText(workshop.twitter);
        	twitterText.setTypeface(AppBase.getTypefaceLight());
        	
        	LinearLayout twitterButton = (LinearLayout) dialog.findViewById(R.id.twitter_buttons_container);
        	twitterButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					AnalyticsBase.reportLoggedInEvent("On Discover Activity: workshop view twitter click", activity, "workshop-id",  String.valueOf(workshop.remoteId));

					AppBase.launchTwitterActivityWithUsername(workshop.twitter);
				}
        		
        	});
        } else {
        	((LinearLayout) dialog.findViewById(R.id.twitter_buttons_container)).setVisibility(View.GONE);
        }
        
        // Hide webpage buttons
        if(workshop.webpage.length() > 0) {
        	TextView webpageText = (TextView) dialog.findViewById(R.id.workshop_webpage_text);
        	webpageText.setTypeface(AppBase.getTypefaceLight());
        	
        	LinearLayout webpageButton = (LinearLayout) dialog.findViewById(R.id.webpage_buttons_container);
        	webpageButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					AnalyticsBase.reportLoggedInEvent("On Discover Activity: workshop view webpage click", activity, "workshop-id",  String.valueOf(workshop.remoteId));
					AppBase.launchBrowserActivityWithURL(workshop.webpage);
				}
        		
        	});

        } else {
        	((LinearLayout) dialog.findViewById(R.id.webpage_buttons_container)).setVisibility(View.GONE);
        }
        
        
        
        if(workshop.hasPic()) {
            ImageView ownerPic = (ImageView) dialog.findViewById(R.id.contributor_pic);
            
            ImageUpdater updater = Others.getImageFetcher();
            updater.setImageAndImageProcessor(ownerPic, Others.ImageProcessor.ROUND_FOR_MINI_USER_PROFILE);
            updater.execute(workshop.userPicURL);
        }
        
        dialog.findViewById(R.id.dialog_close).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
        	
        });
        
        if(workshop.isOwnedByCurrentUser()) {
        	TextView modifyButton = (TextView) dialog.findViewById(R.id.button_modify);
            modifyButton.setTypeface(AppBase.getTypefaceStrong());
            
            modifyContainer.setOnClickListener(new OnClickListener() {

    			@Override
    			public void onClick(View v) {
    				dialog.dismiss();
    				Bundle bundle = new Bundle();
    				bundle.putSerializable("workshop", workshop);
    				AppBase.launchActivityWithBundle(ModifyingActivity.class, bundle);
    			}
            	
            });
        }
        
        if(workshop.isOwnedByCurrentUser()) {
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
							Workshops.Delete workshopDelete = new Workshops().new Delete();
							workshopDelete.activity = (DiscoverActivity) activity;
							workshopDelete.execute(workshop);
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
        
        if(!workshop.isOwnedByCurrentUser())
        	destroyContainer.setVisibility(View.GONE);
        
        if(!workshop.isOwnedByCurrentUser())
        	dialog.findViewById(R.id.action_buttons_container).setVisibility(View.GONE);
        
        dialog.show();
        
        if(workshop.cellPhone > 0 || workshop.phone > 0) {
        	TextView cellPhoneText = (TextView) dialog.findViewById(R.id.workshop_cellphone_text);
        	if(workshop.cellPhone == 0)
        		cellPhoneText.setText("---");
        	else {
        		cellPhoneText.setText(String.valueOf(workshop.cellPhone));
        		LinearLayout buttonCellPhone = (LinearLayout) dialog.findViewById(R.id.workshop_cell_phone_container);

        		buttonCellPhone.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						dialog.hide();
						Builder alertBuilder = DialogBuilder.buildAlertWithTitleAndMessage(activity, R.string.question, R.string.call_phone_number_confirmation);
						alertBuilder.setPositiveButton(R.string.confirm_yes, new DialogInterface.OnClickListener(){

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								AnalyticsBase.reportLoggedInEvent("On Discover Activity: workshop view phone attempt", activity, "workshop-id",  String.valueOf(workshop.remoteId));
								AppBase.launchPhoneCallingActivity(String.valueOf(Long.valueOf(workshop.cellPhone)));								
							}
							
						});
						alertBuilder.setNegativeButton(R.string.confirm_no, new DialogInterface.OnClickListener(){

							@Override
							public void onClick(DialogInterface dialogLocal,
									int which) {
								dialogLocal.dismiss();
								dialog.show();
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
        	
        	TextView phoneText = (TextView) dialog.findViewById(R.id.workshop_phone_text);
        	if(workshop.phone == 0)
        		phoneText.setText("---");
        	else {
        		phoneText.setText(String.valueOf(workshop.phone));
        		LinearLayout buttonPhone = (LinearLayout) dialog.findViewById(R.id.workshop_phone_container);
        		buttonPhone.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						dialog.hide();
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
							public void onClick(DialogInterface dialogLocal,
									int which) {
								dialogLocal.dismiss();
								dialog.show();
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
        	((LinearLayout) dialog.findViewById(R.id.contact_buttons_container)).setVisibility(View.GONE);
        }
    }
	
	protected static void loadSingleton() {
		singleton = new WorkshopViews();
	}
}
