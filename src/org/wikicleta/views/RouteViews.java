package org.wikicleta.views;

import java.text.DecimalFormat;
import java.util.Date;

import org.wikicleta.R;
import org.wikicleta.activities.CommentsActivity;
import org.wikicleta.activities.routes.RouteDetailsActivity;
import org.wikicleta.common.AppBase;
import org.wikicleta.helpers.DialogBuilder;
import org.wikicleta.models.Route;
import org.wikicleta.models.RouteRanking;
import org.wikicleta.models.User;
import org.wikicleta.routing.Others;
import org.wikicleta.routing.Others.ImageUpdater;
import org.wikicleta.routing.Routes;

import com.nineoldandroids.animation.ObjectAnimator;
import com.ocpsoft.pretty.time.PrettyTime;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class RouteViews extends BaseViews {

	public static void buildViewDetails(final Activity activity, final Route route) {
    	final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_route_details);

        buildRouteBasicInfoView(activity, dialog, route);
        buildContributorDetailsViewFor(activity, dialog, route);
        buildRouteRankingViewFor(activity, dialog, route);

        TextView moreInfoText = (TextView) dialog.findViewById(R.id.route_more_info_text);
        moreInfoText.setTypeface(AppBase.getTypefaceStrong());
        
        dialog.findViewById(R.id.route_more_info_container_button).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
				RouteDetailsActivity.currentRoute = route;
				AppBase.launchActivity(RouteDetailsActivity.class);
			}
        	
        });
        
        dialog.findViewById(R.id.route_security_container).setBackgroundColor(android.R.color.transparent);
        dialog.findViewById(R.id.route_fast_container).setBackgroundColor(android.R.color.transparent);
        dialog.findViewById(R.id.route_comfort_container).setBackgroundColor(android.R.color.transparent);

        dialog.show();
	}

	public static void buildViewDetailsExtra(final RouteDetailsActivity activity,final Route route) {
		
    	final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_route_extra_details);

        buildRouteBasicInfoView(activity, dialog, route);
        buildDestructiveControlsViewFor(activity, dialog, route);
        buildRouteRankingViewFor(activity, dialog, route);
        buildContributorDetailsViewFor(activity, dialog, route);
        buildRouteRankingControlsViewFor(activity, dialog, route);
        buildViewForFavoritedResource(dialog, route.remoteId, "Route");

        ((TextView) dialog.findViewById(R.id.button_rank_text)).setTypeface(AppBase.getTypefaceStrong());

        dialog.findViewById(R.id.rank_button_container).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				activity.showRankingRouteView(dialog);
			}
        	
        });
        
        dialog.findViewById(R.id.route_security_container).setBackgroundColor(android.R.color.transparent);
        dialog.findViewById(R.id.route_fast_container).setBackgroundColor(android.R.color.transparent);
        dialog.findViewById(R.id.route_comfort_container).setBackgroundColor(android.R.color.transparent);
        
        dialog.show();
	}
	
	public static Dialog buildPerformancesView(final RouteDetailsActivity activity, Route route) {
    	Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_route_performances);
        
        ImageView performancesLoaderView = (ImageView) dialog.findViewById(R.id.loading_performances_icon);

        ((TextView) dialog.findViewById(R.id.waiting_for_performances_text)).setTypeface(AppBase.getTypefaceStrong());
        
        final ObjectAnimator loadingAnimator = ObjectAnimator.ofFloat(performancesLoaderView, "alpha", 1, 0.2f, 1);
    	loadingAnimator.setDuration(3000);
    	loadingAnimator.setRepeatCount(ObjectAnimator.INFINITE);
        loadingAnimator.start();

        Routes.Performances poster = new Routes().new Performances(activity);
		poster.execute(route);
        
        dialog.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialog) {
				loadingAnimator.cancel();
			}
        	
        });
        dialog.show();

        return dialog;
	}

	private static void buildRouteBasicInfoView(Activity activity, Dialog dialog, Route route) {
        TextView title = (TextView) dialog.findViewById(R.id.route_name_text);
        title.setText(route.name);
        title.setTypeface(AppBase.getTypefaceStrong());
        
        TextView content = (TextView) dialog.findViewById(R.id.route_details_text);
        content.setText(route.details);
        content.setTypeface(AppBase.getTypefaceLight());
        
        DecimalFormat format=new DecimalFormat("#.##");
        
        TextView kilometers = (TextView) dialog.findViewById(R.id.route_kms_text);
        kilometers.setText(format.format(route.kilometers).concat(" Km/h"));
        kilometers.setTypeface(AppBase.getTypefaceStrong());

	}
	
	private static void buildContributorDetailsViewFor(Activity activity, Dialog dialog, Route route) {
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
	}
	
	private static void buildRouteRankingViewFor(Activity activity, Dialog dialog, Route route) {

        ((TextView) dialog.findViewById(R.id.route_fast_value_text)).setTypeface(AppBase.getTypefaceLight());
        ((TextView) dialog.findViewById(R.id.route_comfort_value_text)).setTypeface(AppBase.getTypefaceLight());
        ((TextView) dialog.findViewById(R.id.route_security_value_text)).setTypeface(AppBase.getTypefaceLight());
        
        setRankedViewsWith(activity, dialog, new RouteRanking(route.safetyIndex, route.speedIndex, route.comfortIndex));
	}
	
	public static void setRankedViewsWith(Activity activity, Dialog dialog, RouteRanking ranking) {
		int viewForSecurity = activity.getResources().getIdentifier("ranking_value_".concat(String.valueOf(ranking.safetyIndex)), "id", activity.getPackageName());
		setRankingViewSelected(dialog, R.id.route_security_container, viewForSecurity);
		int viewForSpeed = activity.getResources().getIdentifier("ranking_value_".concat(String.valueOf(ranking.speedIndex)), "id", activity.getPackageName());
		setRankingViewSelected(dialog, R.id.route_fast_container, viewForSpeed);
		int viewForComfort = activity.getResources().getIdentifier("ranking_value_".concat(String.valueOf(ranking.comfortIndex)), "id", activity.getPackageName());
		setRankingViewSelected(dialog, R.id.route_comfort_container, viewForComfort);
	}
	
	protected static void setRankingViewSelected(Dialog rankingDialog, int superviewId, int viewWithId) {
		rankingDialog.findViewById(superviewId).findViewById(R.id.ranking_value_0).setVisibility(View.GONE);
		rankingDialog.findViewById(superviewId).findViewById(R.id.ranking_value_1).setVisibility(View.GONE);
		rankingDialog.findViewById(superviewId).findViewById(R.id.ranking_value_2).setVisibility(View.GONE);
		rankingDialog.findViewById(superviewId).findViewById(R.id.ranking_value_3).setVisibility(View.GONE);
		
		rankingDialog.findViewById(superviewId).findViewById(viewWithId).setVisibility(View.VISIBLE);
	}
	
	private static void buildRouteRankingControlsViewFor(Activity activity, Dialog dialog, final Route route) {
        // Common actions for POIs
        TextView positiveRankingLegend = (TextView) dialog.findViewById(R.id.positive_button_ranks_text);
        positiveRankingLegend.setText(String.valueOf(route.likesCount));
        positiveRankingLegend.setTypeface(AppBase.getTypefaceStrong());

        TextView negativeRankingLegend = (TextView) dialog.findViewById(R.id.negative_button_ranks_text);
        negativeRankingLegend.setText(String.valueOf(route.dislikesCount));
        negativeRankingLegend.setTypeface(AppBase.getTypefaceStrong());
        
        dialog.findViewById(R.id.positive_rankings_container).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Bundle bundle = new Bundle();
				bundle.putBoolean("like", true);
				CommentsActivity.selectedPoint = route;
				AppBase.launchActivityWithBundle(CommentsActivity.class, bundle);
			}
        	
        });
        
        dialog.findViewById(R.id.negative_rankings_container).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Bundle bundle = new Bundle();
				bundle.putBoolean("false", true);
				CommentsActivity.selectedPoint = route;
				AppBase.launchActivityWithBundle(CommentsActivity.class, bundle);
			}
        	
        });

	}
	
	public static void buildDestructiveControlsViewFor(final RouteDetailsActivity activity, final Dialog dialog, final Route route) {
        if(route.isOwnedByCurrentUser()) {
            
        	LinearLayout modifyContainer = (LinearLayout) dialog.findViewById(R.id.modify_button_container);
            LinearLayout destroyContainer = (LinearLayout) dialog.findViewById(R.id.delete_button_container);
            
        	TextView modifyButton = (TextView) dialog.findViewById(R.id.button_modify);
            modifyButton.setTypeface(AppBase.getTypefaceStrong());
            
            modifyContainer.setOnClickListener(new OnClickListener() {

    			@Override
    			public void onClick(View v) {
    				dialog.hide();
    				
    				AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
    		        LayoutInflater inflater = activity.getLayoutInflater();
    		        View view = inflater.inflate(R.layout.dialog_route_save, null);
    				alertDialog.setView(view);
    				final AlertDialog modifyingDialog = alertDialog.create();

    				// First, preload the route fields
    				((EditText) view.findViewById(R.id.route_name)).setText(route.name);
    				((EditText) view.findViewById(R.id.route_details)).setText(route.details);
    				((CheckBox) view.findViewById(R.id.route_is_private)).setChecked(!route.isPublic);
    				
    		        Button saveButton = (Button) view.findViewById(R.id.save_route);
    		        saveButton.setTypeface(AppBase.getTypefaceStrong());
    		        
    		        ((TextView) view.findViewById(R.id.dialog_menu_title)).setTypeface(AppBase.getTypefaceStrong());
    		        
    		        ImageView closeImage = (ImageView) view.findViewById(R.id.dialog_close);
    		        closeImage.setOnClickListener(new OnClickListener () {
    		        	
    					@Override
    					public void onClick(View v) {
    						dialog.show();
    						modifyingDialog.dismiss();
    					}
    		        	
    		        });
    		        
    		        saveButton.setOnClickListener(new OnClickListener () {

    					@Override
    					public void onClick(View v) {
    						modifyingDialog.dismiss();
    						activity.attemptUpdate(modifyingDialog);
    					}
    		        	
    		        });
    				
    				modifyingDialog.show();
    			}
            	
            });
            
            TextView destroyButton = (TextView) dialog.findViewById(R.id.button_delete);
            destroyButton.setTypeface(AppBase.getTypefaceStrong());
            
            destroyContainer.setOnClickListener(new OnClickListener() {

    			@Override
    			public void onClick(View v) {
    				AlertDialog.Builder builder = DialogBuilder.buildAlertWithTitleAndMessage(activity, R.string.question, R.string.routes_question_delete);
    				
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
							Routes.Delete routesDelete = new Routes().new Delete();
							routesDelete.execute(route);
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
        
	}
}
