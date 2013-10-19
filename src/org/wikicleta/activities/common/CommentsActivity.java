package org.wikicleta.activities.common;

import org.interfaces.MarkerInterface;
import org.interfaces.RemoteFetchingDutyListener;
import org.wikicleta.R;
import org.wikicleta.common.AppBase;
import org.wikicleta.common.Constants;
import org.wikicleta.common.FieldValidators;
import org.wikicleta.common.Toasts;
import org.wikicleta.helpers.DialogBuilder;
import org.wikicleta.models.RankedComment;
import org.wikicleta.models.Parking;
import org.wikicleta.models.Route;
import org.wikicleta.models.Tip;
import org.wikicleta.models.Workshop;
import org.wikicleta.routing.RankedComments;

import com.nineoldandroids.animation.ObjectAnimator;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class CommentsActivity extends Activity implements RemoteFetchingDutyListener {

    public static MarkerInterface selectedPoint;
    String modelKind;

    EditText commentTextField;
    
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
			}
			
		});
		
		this.findViewById(R.id.dislike_button).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				v.setVisibility(View.GONE);

				ObjectAnimator.ofFloat(findViewById(R.id.like_button), "alpha", 0.4f, 1, 1).setDuration(1000).start();
				findViewById(R.id.like_button).setVisibility(View.VISIBLE);
			}
			
		});
		
        ((TextView) findViewById(R.id.text_comment_title)).setTypeface(AppBase.getTypefaceStrong());
        ((TextView) findViewById(R.id.no_comments_text)).setTypeface(AppBase.getTypefaceStrong());

        
        ImageView returnIcon = (ImageView) this.findViewById(R.id.return_button);
    	returnIcon.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
    		
    	});
    	
    	ImageView likeButton = (ImageView) this.findViewById(R.id.save_button);
    	likeButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				attemptCommit();
			}
    		
    	});
    	
    	ImageView returnButton = (ImageView) this.findViewById(R.id.return_button);
    	returnButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(commentTextField.getText().toString().isEmpty())
					finish();
				else
					notifyAboutPotentialLossOfComment();
			}
    		
    	});

		commentTextField = (EditText) this.findViewById(R.id.comment_input_field);
    	
	}

	private void notifyAboutPotentialLossOfComment() {
		AlertDialog.Builder builder = DialogBuilder.buildAlertWithTitleAndMessage(this, R.string.notification, R.string.comment_not_saved);
		
		builder.setNegativeButton(getResources().getString(R.string.confirm_no), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int id) {
				dialog.dismiss();
			}
		}).setPositiveButton(getResources().getString(R.string.confirm_yes), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int id) {
				finish();
			}
		});
		final AlertDialog alert = builder.create();
		
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
	
	private void attemptCommit() {
		String comment = commentTextField.getText().toString();
		
		boolean positive = this.findViewById(R.id.like_button).getVisibility() == View.VISIBLE;
		
		if(FieldValidators.isFieldEmpty(comment)) {
			commentTextField.setError(getResources().getString(R.string.comments_input_empty));
			return;
		}
		
		if(FieldValidators.isFieldLongerThan(comment, Constants.CHARACTERS_LENGTH_MAX)) {
			commentTextField.setError(getResources().getString(R.string.comments_input_length_max_error));
			return;
		}
		
		if(FieldValidators.isFieldShorterThan(comment, Constants.CHARACTERS_LENGTH_MIN_FOR_COMMENTS)) {
			commentTextField.setError(getResources().getString(R.string.comments_input_length_min_error));
			return;
		}
				
    	RankedComments comments = new RankedComments();
    	RankedComments.Post commentsPoster = comments.new Post(this);
    	//commentsPoster.execute(new RankedComment("Parking", 39, comment, positive));
    	commentsPoster.execute(new RankedComment(modelKind, selectedPoint.getRemoteId(), comment, positive));
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
			modelKind = "Tip";
		} else if(selectedPoint instanceof Workshop) {
			Workshop workshop = (Workshop) selectedPoint;
			title.setText(workshop.name);
			details.setText(workshop.details);
			icon.setImageResource(workshop.getDrawable());
			modelKind = "Workshop";
		} else if(selectedPoint instanceof Route) {
			Route route = (Route) selectedPoint;
			title.setText(route.name);
			details.setText(route.details);
			icon.setImageResource(route.getDrawable());
			modelKind = "Route";
		} else if(selectedPoint instanceof Parking) {
			Parking parking = (Parking) selectedPoint;
			String type = getResources().getString(
	        		getResources().getIdentifier(
	        				"parkings.kinds.".concat(parking.kindString()), "string", getPackageName()));
			title.setText(type);
			details.setText(parking.details);
			icon.setImageResource(parking.getDrawable());
			modelKind = "Parking";
		}
	}

	private void reFetchComments() {
		
	}
	
	@Override
	public void onFinished(Object duty) {	
		this.reFetchComments();
		Toasts.showToastWithMessage(this, R.string.comment_saved_successfully, R.drawable.success_icon);
		commentTextField.setText("");
	}

	@Override
	public void onFailed() {	
		Toasts.showToastWithMessage(this, R.string.comment_failed_to_save, R.drawable.failure_icon);
	}
}
