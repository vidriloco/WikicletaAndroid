package org.wikicleta.views;

import org.wikicleta.R;
import org.wikicleta.common.AppBase;
import org.wikicleta.layers.common.LayersConnectorListener;
import org.wikicleta.models.CyclingGroup;
import org.wikicleta.routing.Others;
import org.wikicleta.routing.Others.ImageUpdater;
import android.app.Activity;
import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CyclingGroupViews {

	public static void buildViewForCyclingGroup(LayersConnectorListener listener, final CyclingGroup cyclingGroup) {
    	Activity activity = listener.getActivity();
        
    	final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
    	
        LayoutInflater inflater = activity.getLayoutInflater();
        final View view = inflater.inflate(R.layout.dialog_cycling_group_details, null);
        
        TextView cyclingGroupName = (TextView) view.findViewById(R.id.cycling_group_name_text);
        cyclingGroupName.setTypeface(AppBase.getTypefaceStrong());
        cyclingGroupName.setText(cyclingGroup.name);
        
        TextView cyclingGroupNextRideDate = (TextView) view.findViewById(R.id.cycling_group_next_ride_text);
        cyclingGroupNextRideDate.setTypeface(AppBase.getTypefaceLight());
        cyclingGroupNextRideDate.setText(String.format(activity.getResources().getString(cyclingGroup.daysToRide()), cyclingGroup.daysToEventFromNow));
        
        TextView meetingTimeValueText = (TextView) view.findViewById(R.id.meeting_time_value_text);
        meetingTimeValueText.setTypeface(AppBase.getTypefaceStrong());
        meetingTimeValueText.setText(cyclingGroup.meetingTime);
        
        TextView departingTimeValueText = (TextView) view.findViewById(R.id.departing_time_value_text);
        departingTimeValueText.setTypeface(AppBase.getTypefaceStrong());
        departingTimeValueText.setText(cyclingGroup.departingTime);
        
        TextView cyclingGroupDetailsText = (TextView) view.findViewById(R.id.cycling_group_details_text);
        cyclingGroupDetailsText.setTypeface(AppBase.getTypefaceLight());
        cyclingGroupDetailsText.setText(cyclingGroup.details);

        ((TextView) view.findViewById(R.id.meeting_time_text)).setTypeface(AppBase.getTypefaceLight());
        ((TextView) view.findViewById(R.id.departing_time_text)).setTypeface(AppBase.getTypefaceLight());

        // Hide/show twitter buttons
        if(cyclingGroup.twitterAccount.length() > 0) {
            TextView twitterAccountText = (TextView) view.findViewById(R.id.twitter_account_text);
            twitterAccountText.setTypeface(AppBase.getTypefaceLight());
        	
        	LinearLayout twitterButton = (LinearLayout) view.findViewById(R.id.twitter_container);
        	twitterButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					AppBase.launchTwitterActivityWithUsername(cyclingGroup.twitterAccount);
				}
        		
        	});
        } else {
        	((LinearLayout) view.findViewById(R.id.twitter_container)).setVisibility(View.GONE);
        }
        
        // Hide/show webpage buttons
        if(cyclingGroup.websiteURL.length() > 0) {
        	TextView webpageText = (TextView) view.findViewById(R.id.website_container_text);
        	webpageText.setTypeface(AppBase.getTypefaceLight());
        	
        	LinearLayout webpageButton = (LinearLayout) view.findViewById(R.id.website_container);
        	webpageButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					AppBase.launchBrowserActivityWithURL(cyclingGroup.websiteURL);
				}
        		
        	});

        } else {
        	((LinearLayout) view.findViewById(R.id.website_container)).setVisibility(View.GONE);
        }
        
        // Hide/show facebook buttons
        if(cyclingGroup.facebookURL.length() > 0) {
        	TextView webpageText = (TextView) view.findViewById(R.id.facebook_account_text);
        	webpageText.setTypeface(AppBase.getTypefaceLight());
        	
        	LinearLayout webpageButton = (LinearLayout) view.findViewById(R.id.facebook_container);
        	webpageButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					AppBase.launchBrowserActivityWithURL(cyclingGroup.facebookURL);
				}
        		
        	});

        } else {
        	((LinearLayout) view.findViewById(R.id.facebook_container)).setVisibility(View.GONE);
        }
        
        if(cyclingGroup.hasPic()) {
            ImageView ownerPic = (ImageView) view.findViewById(R.id.pic_image);
            ImageUpdater updater = Others.getImageFetcher();
            updater.setImageAndImageProcessor(ownerPic, Others.ImageProcessor.NONE);
            updater.execute(cyclingGroup.pic);
        }
        
        dialog.setContentView(view);        
        dialog.show();
	}

}
