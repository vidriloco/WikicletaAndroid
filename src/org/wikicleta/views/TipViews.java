package org.wikicleta.views;

import java.util.Date;

import org.wikicleta.R;
import org.wikicleta.activities.MainMapActivity;
import org.wikicleta.activities.tips.ModifyingActivity;
import org.wikicleta.common.AppBase;
import org.wikicleta.helpers.DialogBuilder;
import org.wikicleta.models.Tip;
import org.wikicleta.models.User;
import org.wikicleta.routing.Others;
import org.wikicleta.routing.Others.ImageUpdater;
import org.wikicleta.routing.Tips;

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

public class TipViews {
	public static void buildViewForTip(final Activity activity, final Tip tip) {
    	final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.setContentView(R.layout.dialog_tip_details);
        
        String type = activity.getResources().getString(
        		activity.getResources().getIdentifier(
        				"tips.categories.".concat(tip.categoryString()), "string", activity.getPackageName()));
        
        TextView title = (TextView) dialog.findViewById(R.id.tip_category_title);
        title.setText(type);
        title.setTypeface(AppBase.getTypefaceStrong());
        
        TextView modelNamed = (TextView) dialog.findViewById(R.id.model_named);
        modelNamed.setTypeface(AppBase.getTypefaceStrong());
        
        TextView content = (TextView) dialog.findViewById(R.id.tip_contents);
        content.setText(tip.content);
        content.setTypeface(AppBase.getTypefaceLight());
        
        TextView creationLegend = (TextView) dialog.findViewById(R.id.creation_date_text);
        
        PrettyTime ptime = new PrettyTime();
        creationLegend.setText(activity.getResources().getString(R.string.updated_on).concat(" ").concat(ptime.format(new Date(tip.updatedAt))));
        creationLegend.setTypeface(AppBase.getTypefaceLight());
        
        LinearLayout modifyContainer = (LinearLayout) dialog.findViewById(R.id.modify_button_container);
        LinearLayout destroyContainer = (LinearLayout) dialog.findViewById(R.id.delete_button_container);
        
        TextView creatorName = (TextView) dialog.findViewById(R.id.contributor_text);
        
        String username = tip.userId == User.id() ? activity.getResources().getString(R.string.you) : tip.username;
        
        creatorName.setText(activity.getResources().getString(R.string.created_by).concat(" ").concat(username));
        creatorName.setTypeface(AppBase.getTypefaceStrong());
        
        if(tip.hasPic()) {
            ImageView ownerPic = (ImageView) dialog.findViewById(R.id.contributor_pic);
            
            ImageUpdater updater = Others.getImageFetcher();
            updater.setImageAndImageProcessor(ownerPic, Others.ImageProcessor.ROUND_FOR_MINI_USER_PROFILE);
            updater.execute(tip.userPicURL);
        }

        ImageView iconImage = (ImageView) dialog.findViewById(R.id.tip_category_icon);
        iconImage.setImageDrawable(activity.getResources().getDrawable(tip.getDrawable()));
        
        // Common actions for POIs
        TextView positiveRankingLegend = (TextView) dialog.findViewById(R.id.positive_button_ranks_text);
        positiveRankingLegend.setText("100");
        positiveRankingLegend.setTypeface(AppBase.getTypefaceStrong());

        TextView negativeRankingLegend = (TextView) dialog.findViewById(R.id.negative_button_ranks_text);
        negativeRankingLegend.setText("30");
        negativeRankingLegend.setTypeface(AppBase.getTypefaceStrong());
        
        dialog.findViewById(R.id.dialog_close).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
        	
        });
        
        if(tip.isOwnedByCurrentUser()) {
        	TextView modifyButton = (TextView) dialog.findViewById(R.id.button_modify);
            modifyButton.setTypeface(AppBase.getTypefaceStrong());
            
            modifyContainer.setOnClickListener(new OnClickListener() {

    			@Override
    			public void onClick(View v) {
    				dialog.dismiss();
    				Bundle bundle = new Bundle();
    				bundle.putSerializable("tip", tip);
    				AppBase.launchActivityWithBundle(ModifyingActivity.class, bundle);
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
							Tips.Delete tipsDelete = new Tips().new Delete();
							tipsDelete.activity = (MainMapActivity) activity;
							tipsDelete.execute(tip);
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
