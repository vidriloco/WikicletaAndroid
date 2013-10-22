package org.wikicleta.adapters;

import java.util.List;
import org.interfaces.ListedModelInterface;
import org.wikicleta.R;
import org.wikicleta.common.AppBase;
import org.wikicleta.fragments.activities.DraftsFragment;
import org.wikicleta.helpers.DialogBuilder;
import org.wikicleta.models.Parking;
import org.wikicleta.models.Route;
import org.wikicleta.models.Tip;
import org.wikicleta.models.Workshop;
import org.wikicleta.models.helpers.ListedModelExtractor;
import org.wikicleta.routing.Routes;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.activeandroid.Model;
import com.ocpsoft.pretty.time.PrettyTime;

public class DraftsListAdapter extends ArrayAdapter<ListedModelInterface> {
	public final Activity context;
	protected final List<ListedModelInterface> objects;
	protected LayoutInflater inflater;
  	protected DraftsFragment fragment;  
	
	public DraftsListAdapter(Activity context, List<ListedModelInterface> objects, DraftsFragment fragment) {
		super(context, R.layout.item_draft_on_list, objects);
		this.context = context;
		this.objects = objects;
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		ListedModelExtractor.setContext(context);
		this.fragment = fragment;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ListedModelInterface draft = objects.get(position);
		View rowView = inflater.inflate(R.layout.item_draft_on_list, parent, false);
		TextView title = (TextView) rowView.findViewById(R.id.draft_title);
		title.setTypeface(AppBase.getTypefaceLight());
		title.setText(ListedModelExtractor.extractModelTitle(draft));
		
		TextView subtitle = (TextView) rowView.findViewById(R.id.draft_subtitle);
		subtitle.setTypeface(AppBase.getTypefaceStrong());
		subtitle.setText(ListedModelExtractor.extractModelSubtitle(draft));

		
		TextView details = (TextView) rowView.findViewById(R.id.draft_details);
		details.setTypeface(AppBase.getTypefaceLight());
		details.setText(draft.getDetails());
		
		((TextView) rowView.findViewById(R.id.button_discard_text)).setTypeface(AppBase.getTypefaceStrong());
		
		if(draft instanceof Route) {
			rowView.findViewById(R.id.review_button_container).setVisibility(View.GONE);
			rowView.findViewById(R.id.upload_button_container).setVisibility(View.VISIBLE);
			((TextView) rowView.findViewById(R.id.button_upload_text)).setTypeface(AppBase.getTypefaceStrong());
		} else {
			((TextView) rowView.findViewById(R.id.button_review_text)).setTypeface(AppBase.getTypefaceStrong());
		}
		
		rowView.findViewById(R.id.upload_button_container).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Routes draftPosters = new Routes();
				Routes.DraftPost poster = draftPosters.new DraftPost(DraftsListAdapter.this);
				poster.execute((Route) draft);
			}
			
		});
		
		rowView.findViewById(R.id.review_button_container).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Bundle bundle = new Bundle();
				bundle.putString("id", String.valueOf(draft.getId()));
				
				if(draft instanceof Parking)
					AppBase.launchActivityWithBundle(org.wikicleta.activities.parkings.ModifyingActivity.class, bundle);
				else if(draft instanceof Workshop)
					AppBase.launchActivityWithBundle(org.wikicleta.activities.workshops.ModifyingActivity.class, bundle);
				else if(draft instanceof Tip)
					AppBase.launchActivityWithBundle(org.wikicleta.activities.tips.ModifyingActivity.class, bundle);
				context.finish();
			}
			
		});
		
		rowView.findViewById(R.id.discard_button_container).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				AlertDialog.Builder builder = DialogBuilder.buildAlertWithTitleAndMessage(context, R.string.question, R.string.discard_draft);
				
				final AlertDialog alert = builder.setNegativeButton(R.string.confirm_no, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialogLocal, int which) {
						dialogLocal.dismiss();
					}

					
				}).setPositiveButton(R.string.confirm_yes, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						removeItem(draft);
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
			}
			
		});
		TextView date = (TextView) rowView.findViewById(R.id.draft_date);
		date.setTypeface(AppBase.getTypefaceLight());
		
		PrettyTime ptime = new PrettyTime();
		date.setText(context.getResources().getString(R.string.drafts_on).concat(" ").concat(ptime.format(draft.getDate())));
		
		((ImageView) rowView.findViewById(R.id.draft_icon)).setImageResource(draft.getDrawable());
		
	    return rowView;
	}
	
	public void removeItem(ListedModelInterface draft) {
		((Model) draft).delete();
		if(getCount() == 1) {
			fragment.triggerFetch();
		} else {
			remove(draft);
			notifyDataSetChanged();
		}
	}
	
	@Override
	public boolean isEnabled(int position) {
	    return false;
	}
}
