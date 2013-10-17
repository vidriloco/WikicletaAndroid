package org.wikicleta.activities.common;

import org.interfaces.MarkerInterface;
import org.wikicleta.R;
import org.wikicleta.common.AppBase;
import org.wikicleta.models.Parking;
import org.wikicleta.models.Route;
import org.wikicleta.models.Tip;
import org.wikicleta.models.Workshop;

import com.nineoldandroids.animation.ObjectAnimator;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class CommentsActivity extends Activity {

	public boolean likeSelected;
	public String comment;
    public static MarkerInterface selectedPoint;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTheme(R.style.Theme_Sherlock_Light_NoActionBar);
		this.setContentView(R.layout.activity_comments);

    	if(selectedPoint != null)
    		assignFieldsToView();
    	else
    		finish();
		
		ImageView image = null;
		if(this.getIntent().getExtras().getBoolean("like")) {
			image = (ImageView) this.findViewById(R.id.like_button);
		} else {
			image = (ImageView) this.findViewById(R.id.dislike_button);
		}

		image.setVisibility(View.VISIBLE);
		ObjectAnimator.ofFloat(image, "alpha", 0.4f, 1, 1).setDuration(1000).start();

		
		this.findViewById(R.id.like_button).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				v.setVisibility(View.GONE);

				findViewById(R.id.dislike_button).setVisibility(View.VISIBLE);
				ObjectAnimator.ofFloat(findViewById(R.id.dislike_button), "alpha", 0.4f, 1, 1).setDuration(1000).start();
				likeSelected = false;
			}
			
		});
		
		this.findViewById(R.id.dislike_button).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				v.setVisibility(View.GONE);

				ObjectAnimator.ofFloat(findViewById(R.id.like_button), "alpha", 0.4f, 1, 1).setDuration(1000).start();
				findViewById(R.id.like_button).setVisibility(View.VISIBLE);
				likeSelected = true;
			}
			
		});
		
        ((TextView) findViewById(R.id.text_comment_title)).setTypeface(AppBase.getTypefaceStrong());
        ((TextView) findViewById(R.id.no_comments_text)).setTypeface(AppBase.getTypefaceStrong());

        
        ImageView returnIcon = (ImageView) this.findViewById(R.id.return_button);
		returnIcon = (ImageView) this.findViewById(R.id.return_button);
    	
    	returnIcon.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
    		
    	});
    	
	}

	private void assignFieldsToView() {
		TextView title = (TextView) findViewById(R.id.selected_point_title_text);
		title.setTypeface(AppBase.getTypefaceStrong());
		
		TextView details = (TextView) findViewById(R.id.selected_point_details_text);
		details.setTypeface(AppBase.getTypefaceStrong());

		ImageView icon = (ImageView) findViewById(R.id.selected_point_category_icon);
		icon.setImageResource(0);
		
		if(selectedPoint instanceof Tip) {
			Tip tip = (Tip) selectedPoint;
			String type = getResources().getString(
	        		getResources().getIdentifier(
	        				"tips.categories.".concat(tip.categoryString()), "string", getPackageName()));
			title.setText(type);
			details.setText(tip.content);
			icon.setImageResource(tip.getDrawable());
		} else if(selectedPoint instanceof Workshop) {
			Workshop workshop = (Workshop) selectedPoint;
			title.setText(workshop.name);
			details.setText(workshop.details);
			icon.setImageResource(workshop.getDrawable());
		} else if(selectedPoint instanceof Route) {
			Route route = (Route) selectedPoint;
			title.setText(route.name);
			details.setText(route.details);
			icon.setImageResource(route.getDrawable());
		} else if(selectedPoint instanceof Parking) {
			Parking parking = (Parking) selectedPoint;
			String type = getResources().getString(
	        		getResources().getIdentifier(
	        				"parkings.kinds.".concat(parking.kindString()), "string", getPackageName()));
			title.setText(type);
			details.setText(parking.details);
			icon.setImageResource(parking.getDrawable());
		}
	}
}
