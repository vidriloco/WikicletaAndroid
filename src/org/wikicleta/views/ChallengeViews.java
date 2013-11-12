package org.wikicleta.views;

import org.wikicleta.R;
import org.wikicleta.activities.challenges.ChallengesOnMapActivity;
import org.wikicleta.common.AppBase;
import org.wikicleta.models.DeliveryChallenge;

import android.app.Dialog;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

public class ChallengeViews {

	public static int randomWithRange(int min, int max)
	{
	   int range = (max - min) + 1;     
	   return (int)(Math.random() * range) + min;
	}
	
	public static void buildViewForChallenge(ChallengesOnMapActivity activity, final DeliveryChallenge challengeDelivery) {
		final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.setContentView(R.layout.dialog_challenge_details);
        
        ImageView delivery = (ImageView) dialog.findViewById(R.id.delivery_image_icon);
        delivery.setImageResource(challengeDelivery.getDrawable());
                
        TextView prizePoints = (TextView) dialog.findViewById(R.id.prize_on_points_text);
        prizePoints.setTypeface(AppBase.getTypefaceStrong());
        prizePoints.setText(String.valueOf(randomWithRange(5, 20)).concat(" Puntos"));
        
        TextView paymentTextTitle = (TextView) dialog.findViewById(R.id.payment_call_to_action);
        paymentTextTitle.setTypeface(AppBase.getTypefaceLight());  
        
        TextView paymentTextValue = (TextView) dialog.findViewById(R.id.payment_value);
        paymentTextValue.setTypeface(AppBase.getTypefaceStrong());  
        paymentTextValue.setText("$ ".concat(String.valueOf(randomWithRange(40, 100))));

        TextView challengeContents = (TextView) dialog.findViewById(R.id.challenge_contents);
        challengeContents.setTypeface(AppBase.getTypefaceLight());
        challengeContents.setText(challengeDelivery.details);
        
        TextView deliveryTypeText = (TextView) dialog.findViewById(R.id.delivery_type_text);
        deliveryTypeText.setTypeface(AppBase.getTypefaceStrong());
        deliveryTypeText.setText(challengeDelivery.getDeliveryStringResource());
        TextView extraConsiderationsText = (TextView) dialog.findViewById(R.id.extra_considerations_title);
        extraConsiderationsText.setTypeface(AppBase.getTypefaceStrong());
        
        TextView considerationsTitleText = (TextView) dialog.findViewById(R.id.extra_considerations);
        considerationsTitleText.setTypeface(AppBase.getTypefaceLight());
        considerationsTitleText.setText(challengeDelivery.consideraciones);
        
        TextView moreInfoText = (TextView) dialog.findViewById(R.id.more_info_text);
        moreInfoText.setTypeface(AppBase.getTypefaceStrong());
        
        dialog.findViewById(R.id.more_info_button).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
        	
        });
        
        dialog.findViewById(R.id.dialog_close).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
        	
        });
        
        dialog.show();
	}
}
