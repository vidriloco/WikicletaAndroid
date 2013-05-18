package org.wikicleta.fragments.user_profile;

import java.util.LinkedList;
import org.wikicleta.R;
import org.wikicleta.activities.UserProfileActivity;
import org.wikicleta.common.AppBase;
import org.wikicleta.helpers.DialogBuilder;
import org.wikicleta.models.Draft;
import org.wikicleta.models.Parking;
import org.wikicleta.models.Tip;
import org.wikicleta.models.Workshop;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class DraftsFragment extends Fragment {
	
	DraftsListAdapter adapter;
	ListView list;
	
	public static ProfileFragment newInstance(int index) {
		ProfileFragment f = new ProfileFragment();

        // Supply index input as an argument.
        Bundle args = new Bundle();
        args.putInt("index", index);
        f.setArguments(args);

        return f;
    }

    public int getShownIndex() {
        return getArguments().getInt("index", 0);
    }
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
	    return inflater.inflate(R.layout.drafts_fragment, container, false);
    } 
	
	public void drawView() {
		final LinkedList<Draft> drafts = Draft.fetchDrafts();
		TextView noDraftsList = (TextView) this.getView().findViewById(R.id.no_drafts_text);
		noDraftsList.setTypeface(AppBase.getTypefaceStrong());
		
		if(drafts.size() > 0) {
			this.getView().findViewById(R.id.no_drafts_list).setVisibility(View.GONE);
			adapter = new DraftsListAdapter(this.getActivity(), drafts);
			list = (ListView) this.getView().findViewById(R.id.drafts_list);
			
			final Activity current = this.getActivity();
			// Long click event for single list row
			list.setOnItemLongClickListener(new OnItemLongClickListener() {

	            @SuppressWarnings("null")
				public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int pos, long id) {
	            	final Draft draft = drafts.get(pos);
	            	
	            	AlertDialog.Builder builder = DialogBuilder.buildAlertWithTitleAndMessage(current, R.string.question, R.string.discard_draft);
	            	builder.setNegativeButton(current.getString(R.string.confirm_no), new OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
	            		
	            	}).setPositiveButton(current.getString(R.string.confirm_yes), new OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							draft.associatedModel.delete();
							dialog.dismiss();
							drawView();
						}
	            		
	            	});
	            	
	            	final AlertDialog confirmDialog = builder.create();
	            	confirmDialog.setOnShowListener(new DialogInterface.OnShowListener() {
    	    		    @Override
    	    		    public void onShow(DialogInterface dialog) {
    	    		        Button btnPositive = confirmDialog.getButton(Dialog.BUTTON_POSITIVE);
    	    		        btnPositive.setTextSize(13);
    	    		        btnPositive.setTypeface(AppBase.getTypefaceStrong());
    	    		        
    	    		        Button btnNegative = confirmDialog.getButton(Dialog.BUTTON_NEGATIVE);
    	    		        btnNegative.setTextSize(13);
    	    		        btnNegative.setTypeface(AppBase.getTypefaceStrong());
    	    		    }
    	    		});
	            	
	            	confirmDialog.show();
	                return true;
	            }
	        }); 
			
	        // Click event for single list row
	        list.setOnItemClickListener(new OnItemClickListener() {
	            public void onItemClick(AdapterView<?> parent, View v, int pos, long id) {
	            	Draft draft = drafts.get(pos);
    				Bundle bundle = new Bundle();
    				if(draft.associatedModel instanceof Parking) {
    					bundle.putSerializable("id", draft.associatedModel.getId());
        				AppBase.launchActivityWithBundle(org.wikicleta.activities.parkings.ModifyingActivity.class, bundle);
    				} else if(draft.associatedModel instanceof Tip) {
    					bundle.putSerializable("id", draft.associatedModel.getId());
        				AppBase.launchActivityWithBundle(org.wikicleta.activities.tips.ModifyingActivity.class, bundle);
	            	} else if(draft.associatedModel instanceof Workshop) {
    					bundle.putSerializable("id", draft.associatedModel.getId());
        				AppBase.launchActivityWithBundle(org.wikicleta.activities.workshops.ModifyingActivity.class, bundle);
	            	}
	            }
	        });	    
	        list.setAdapter(adapter);
		} else {
			this.getView().findViewById(R.id.no_drafts_list).setVisibility(View.VISIBLE);
			this.getView().findViewById(R.id.drafts_list).setVisibility(View.GONE);
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
		this.drawView();
	}
	
	protected UserProfileActivity getUserProfileActivity() {
		return (UserProfileActivity) this.getActivity();
	}
}
