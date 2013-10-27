package org.wikicleta.activities;

import java.util.ArrayList;
import org.wikicleta.R;
import org.wikicleta.adapters.CommentsListAdapter;
import org.wikicleta.common.AppBase;
import org.wikicleta.common.Constants;
import org.wikicleta.common.FieldValidators;
import org.wikicleta.common.Toasts;
import org.wikicleta.helpers.DialogBuilder;
import org.wikicleta.interfaces.RemoteFetchingDutyListener;
import org.wikicleta.interfaces.RemoteModelInterface;
import org.wikicleta.models.RankedComment;
import org.wikicleta.models.Parking;
import org.wikicleta.models.Route;
import org.wikicleta.models.Tip;
import org.wikicleta.models.Workshop;
import org.wikicleta.routing.RankedComments;

import com.nineoldandroids.animation.AnimatorSet;
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
import android.widget.ListView;
import android.widget.TextView;

public class CommentsActivity extends Activity implements RemoteFetchingDutyListener {

    public static RemoteModelInterface selectedPoint;
    EditText commentTextField;
	AnimatorSet set;
	
	RankedComment lastComment;
	ArrayList<RankedComment> existentComments;
	
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
		if(android.os.Build.VERSION.SDK_INT >= 11)
			ObjectAnimator.ofFloat(image, "alpha", 0.4f, 1, 1).setDuration(1000).start();
		
		
		this.findViewById(R.id.like_button).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				v.setVisibility(View.GONE);
				if(android.os.Build.VERSION.SDK_INT >= 11)
					ObjectAnimator.ofFloat(v, "alpha", 1, 0.2f, 1).setDuration(1000).start();
				findViewById(R.id.dislike_button).setVisibility(View.VISIBLE);
				
			}
			
		});
		
		this.findViewById(R.id.dislike_button).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				v.setVisibility(View.GONE);
				if(android.os.Build.VERSION.SDK_INT >= 11)
					ObjectAnimator.ofFloat(v, "alpha", 1, 0.2f, 1).setDuration(1000).start();
				findViewById(R.id.like_button).setVisibility(View.VISIBLE);
			}
			
		});
		
        ((TextView) findViewById(R.id.text_comment_title)).setTypeface(AppBase.getTypefaceStrong());
        
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
		drawLoadingView();
		fetchComments();
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
				
		lastComment = new RankedComment(selectedPoint.getKind(), selectedPoint.getRemoteId(), comment, positive);
    	RankedComments comments = new RankedComments();
    	RankedComments.Post commentsPoster = comments.new Post(this);
    	commentsPoster.execute(lastComment);
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

	private void fetchComments() {
    	if(selectedPoint != null) {
    		RankedComments comments = new RankedComments();
        	RankedComments.Get commentsGetter = comments.new Get(this);
        	commentsGetter.execute(selectedPoint);
        } else
    		finish();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void onSuccess(Object duty) {	
		// TODO: Implement no re-fetching of comments for visual adding/removing of new comment
		if(duty instanceof ArrayList)
			loadComments((ArrayList<RankedComment>) duty);
		else if((duty instanceof String) && ((String) duty).equalsIgnoreCase("Delete")) { 
			this.fetchComments();
			Toasts.showToastWithMessage(this, R.string.comment_deleted_successfully, R.drawable.success_icon);
		} else {
			this.fetchComments();
			Toasts.showToastWithMessage(this, R.string.comment_saved_successfully, R.drawable.success_icon);
			commentTextField.setText("");
		}
	}

	private void loadComments(ArrayList<RankedComment> duty) {
        findViewById(R.id.loading_view).setVisibility(View.GONE);
        findViewById(R.id.empty_comments_list).setVisibility(View.GONE);
        findViewById(R.id.comments_list).setVisibility(View.GONE);

		if(duty.isEmpty()) {
	        ((TextView) findViewById(R.id.no_comments_text)).setTypeface(AppBase.getTypefaceStrong());
	        findViewById(R.id.empty_comments_list).setVisibility(View.VISIBLE);
		} else {
			findViewById(R.id.comments_list).setVisibility(View.VISIBLE);
			
			final ListView listview = (ListView) findViewById(R.id.comments_list);
	        final CommentsListAdapter listAdapter = new CommentsListAdapter(this, duty);
		    listview.setAdapter(listAdapter);
		    listview.setChoiceMode(ListView.CHOICE_MODE_NONE);
		}
	}

	@Override
	public void onFailed(Object message) {
		if(message instanceof String) {
			String messageS = (String) message;
			if(messageS.equalsIgnoreCase("Post"))
				Toasts.showToastWithMessage(this, R.string.comment_failed_to_save, R.drawable.failure_icon);
			else if(messageS.equalsIgnoreCase("Delete"))
				Toasts.showToastWithMessage(this, R.string.comment_failed_to_delete, R.drawable.failure_icon);
			else if(messageS.equalsIgnoreCase("Get"))
				showAttemptReloadView();
		}
		
	}
	
	@Override
	public void onFailed() {
		
	}
	
	protected void showAttemptReloadView() {
		((TextView) findViewById(R.id.attempt_reload_text)).setTypeface(AppBase.getTypefaceStrong());
		findViewById(R.id.loading_view).setVisibility(View.GONE);
        findViewById(R.id.empty_comments_list).setVisibility(View.GONE);
        findViewById(R.id.comments_list).setVisibility(View.GONE);
        findViewById(R.id.attempt_reload_view).setVisibility(View.VISIBLE);
        findViewById(R.id.attempt_reload_view).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				findViewById(R.id.attempt_reload_view).setVisibility(View.GONE);
				findViewById(R.id.loading_view).setVisibility(View.VISIBLE);
				drawLoadingView();
				fetchComments();
			}
        	
        });
        
	}
	
	protected void drawLoadingView() {
		final ImageView spinner = (ImageView) this.findViewById(R.id.spinner_view);
		set = new AnimatorSet();

		ObjectAnimator rotator = ObjectAnimator.ofFloat(spinner, "rotation", 0, 360);
		rotator.setRepeatCount(ObjectAnimator.INFINITE);
		
		ObjectAnimator fader = ObjectAnimator.ofFloat(spinner, "alpha", 1, 0.1f, 1);
		fader.setDuration(1800);
		fader.setRepeatCount(ObjectAnimator.INFINITE);
		
		set.playTogether(
				rotator,
				fader
		);
		
		set.setDuration(1800);
		set.start();
	}
}
