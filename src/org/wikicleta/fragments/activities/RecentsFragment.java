package org.wikicleta.fragments.activities;

import java.util.ArrayList;

import org.interfaces.FragmentNotificationsInterface;
import org.wikicleta.R;
import org.wikicleta.activities.ActivitiesActivity;
import org.wikicleta.activities.DiscoverActivity;
import org.wikicleta.adapters.LightPOIsListAdapter;
import org.wikicleta.common.AppBase;
import org.wikicleta.models.LightPOI;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class RecentsFragment extends Fragment implements FragmentNotificationsInterface {

	AnimatorSet set;
	
	protected ActivitiesActivity getParentActivity() {
		return (ActivitiesActivity) this.getActivity();
	}
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recents, container, false);
        this.drawLoadingView(rootView);
        
        return rootView;
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
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		triggerFetch();
	}
	
	protected void switchActiveViewTo(int displayId) {
		this.getView().findViewById(R.id.loading_view).setVisibility(View.GONE);
		this.getView().findViewById(R.id.light_pois_list).setVisibility(View.GONE);
		this.getView().findViewById(R.id.empty_list).setVisibility(View.GONE);
		this.getView().findViewById(displayId).setVisibility(View.VISIBLE);
		this.set.cancel();
	}
		
    protected void toggleViews(ArrayList<LightPOI> objects) {
		if(objects == null || objects.isEmpty()) {
			switchActiveViewTo(R.id.empty_list);
			((ImageView) this.getView().findViewById(R.id.empty_light_list_icon)).setImageResource(R.drawable.activity_icon_big);
			((TextView) this.getView().findViewById(R.id.empty_light_list_text)).setTypeface(AppBase.getTypefaceStrong());
		} else {
			switchActiveViewTo(R.id.light_pois_list);
			this.loadListViewFor(objects);
		}
	}
    
    protected void loadListViewFor(ArrayList<LightPOI> objects) {
        final ListView listview = (ListView) this.getView().findViewById(R.id.light_pois_list);
        final LightPOIsListAdapter listAdapter = new LightPOIsListAdapter(this.getActivity(), objects, true);
	    listview.setAdapter(listAdapter);
	    listview.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
	    listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				DiscoverActivity.selectedPoi = listAdapter.getItem(position);
				AppBase.launchActivity(DiscoverActivity.class);
			}
	    	
	    });
    }

	@Override
	public void triggerFetch() {
		if(this.getParentActivity() != null)
			this.getParentActivity().fetchUserActivities();
	}
	
	protected boolean UIIsReady() {
		return getParentActivity() != null && this.getView() != null;
	}
	
	@Override
	public void notifyIsNowVisible() {			
		triggerFetch();
	}

	@Override
	public void notifyDataFetched() {
		if(UIIsReady())
			toggleViews(getParentActivity().activities);
	}
}
