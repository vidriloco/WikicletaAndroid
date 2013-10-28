package org.wikicleta.fragments.activities;

import java.util.LinkedList;
import org.wikicleta.R;
import org.wikicleta.adapters.DraftsListAdapter;
import org.wikicleta.analytics.AnalyticsBase;
import org.wikicleta.common.AppBase;
import org.wikicleta.interfaces.FragmentNotificationsInterface;
import org.wikicleta.interfaces.ListedModelInterface;
import org.wikicleta.models.helpers.Drafts;

import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class DraftsFragment extends Fragment implements FragmentNotificationsInterface {

	AnimatorSet set;
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_drafts, container, false);
        
		AnalyticsBase.reportLoggedInEvent("Activities Activity: Drafts", AppBase.currentActivity);
		
        this.drawLoadingView(rootView);
        return rootView;
    }
    
    protected void buildDraftList(View rootView, LinkedList<ListedModelInterface> drafts) {
    	set.cancel();
    	rootView.findViewById(R.id.loading_view).setVisibility(View.GONE);
    	
    	if(drafts.isEmpty()) {
            ((TextView) rootView.findViewById(R.id.no_drafts_listed_text)).setTypeface(AppBase.getTypefaceStrong());
            rootView.findViewById(R.id.drafts_list_empty).setVisibility(View.VISIBLE);
            rootView.findViewById(R.id.drafts_list).setVisibility(View.GONE);
    	} else {
            rootView.findViewById(R.id.drafts_list_empty).setVisibility(View.GONE);
    		rootView.findViewById(R.id.drafts_list).setVisibility(View.VISIBLE);
    		ListView listview = (ListView) rootView.findViewById(R.id.drafts_list);
            final DraftsListAdapter listAdapter = new DraftsListAdapter(this.getActivity(), drafts, this);
    	    listview.setAdapter(listAdapter);
    	    listview.setChoiceMode(ListView.CHOICE_MODE_NONE);
    	}
    }
    
    public void drawLoadingView(View view) {
		final ImageView spinner = (ImageView) view.findViewById(R.id.spinner_view);
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
	
	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
	    super.setUserVisibleHint(isVisibleToUser);
	    if (isVisibleToUser) { 
	    	this.triggerFetch();
	    }
	}

	@Override
	public void notifyIsNowVisible() {
		
	}

	@Override
	public void triggerFetch() {
		buildDraftList(this.getView(), Drafts.fetchDrafts());		
	}

	@Override
	public void notifyDataFetched() {
		
	}

	@Override
	public void notifyDataFailedToLoad() {
		// TODO Auto-generated method stub
		
	}
}
