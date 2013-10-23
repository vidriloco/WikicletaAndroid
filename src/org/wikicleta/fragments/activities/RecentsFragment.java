package org.wikicleta.fragments.activities;

import java.util.ArrayList;

import org.interfaces.FragmentNotificationsInterface;
import org.wikicleta.R;
import org.wikicleta.activities.ActivitiesActivity;
import org.wikicleta.adapters.LightPOIsListAdapter;
import org.wikicleta.common.AppBase;
import org.wikicleta.helpers.SimpleAnimatorListener;
import org.wikicleta.models.LightPOI;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class RecentsFragment extends Fragment implements FragmentNotificationsInterface {

	static AnimatorSet set;
	
	enum DataLoaded {SUCCESS, FAILURE};
	static DataLoaded statusOfData = null;
	
	int MAX_ATTEMPTS = 1;
	static int fetchAttempts = 0;
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recents, container, false);
        this.drawLoadingView(view);
        return view;
    }
    
    @Override
    public void onDestroy() {
    	super.onDestroy();
    	statusOfData = null;
    	fetchAttempts = 0;
    }
	
	protected void drawLoadingView(View view) {	
		if(statusOfData == null) {
			if(fetchAttempts < MAX_ATTEMPTS) {
				fetchAttempts++;
				this.triggerFetch();
				this.prepareAnimationAndLaunchIt(view);
			} else {
				fetchAttempts=0;
				displayFailureOnFetchView(view);
			}
		} else {
			if(statusOfData == DataLoaded.FAILURE)
				displayFailureOnFetchView(view);
			else if(statusOfData == DataLoaded.SUCCESS)
				displaySuccessOnFetchView(view);
		}
	}
	
	protected void prepareAnimationAndLaunchIt(final View view) {
		
		final ImageView spinner = (ImageView) view.findViewById(R.id.spinner_view);
		set = new AnimatorSet();

		ObjectAnimator rotator = ObjectAnimator.ofFloat(spinner, "rotation", 0, 360);
		rotator.setDuration(1800);

		ObjectAnimator fader = ObjectAnimator.ofFloat(spinner, "alpha", 1, 0.1f, 1);
		fader.setDuration(1800);

		set.playTogether(
				rotator,
				fader
		);
		
		set.addListener(new SimpleAnimatorListener() {
			@Override
			public void onAnimationEnd(Animator animation) {
				drawLoadingView(view);
			}
		});
		
		set.setDuration(1800);
		set.start();
		
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}
	
	protected void switchActiveViewTo(View view, int displayId) {
		view.findViewById(R.id.loading_view).setVisibility(View.GONE);
		view.findViewById(R.id.light_pois_list).setVisibility(View.GONE);
		view.findViewById(R.id.empty_list).setVisibility(View.GONE);
		view.findViewById(R.id.attempt_reload_view).setVisibility(View.GONE);
		view.findViewById(displayId).setVisibility(View.VISIBLE);
	}
	
    protected void displaySuccessOnFetchView(View view) {
    	ArrayList<LightPOI> objects = getAssociatedActivity().activities;
		if(objects == null || objects.isEmpty()) {
			switchActiveViewTo(view, R.id.empty_list);
			((ImageView) view.findViewById(R.id.empty_light_list_icon)).setImageResource(R.drawable.activity_icon_big);
			((TextView) view.findViewById(R.id.empty_light_list_text)).setTypeface(AppBase.getTypefaceStrong());
			((TextView) view.findViewById(R.id.empty_light_list_text)).setText(R.string.activities_list_empty);

		} else {
			switchActiveViewTo(view, R.id.light_pois_list);
			this.loadListViewFor(objects);
		}
	}
    
	protected void displayFailureOnFetchView(final View view) {
		switchActiveViewTo(view, R.id.attempt_reload_view);
		((TextView) view.findViewById(R.id.attempt_reload_text)).setTypeface(AppBase.getTypefaceStrong());
		view.findViewById(R.id.attempt_reload_view).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				statusOfData = null;
				switchActiveViewTo(view, R.id.loading_view);
				drawLoadingView(view);
			}
			
		});
	}
    
    protected ActivitiesActivity getAssociatedActivity() {
    	return (ActivitiesActivity) AppBase.currentActivity;
    }
    
    protected void loadListViewFor(ArrayList<LightPOI> objects) {
        final ListView listview = (ListView) this.getView().findViewById(R.id.light_pois_list);
        final LightPOIsListAdapter listAdapter = new LightPOIsListAdapter(this.getActivity(), objects, true);
	    listview.setAdapter(listAdapter);
	    listview.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
    }

	@Override
	public void triggerFetch() {
		if(getAssociatedActivity()!=null)
			getAssociatedActivity().fetchUserActivities();
	}
	
	@Override
	public void notifyIsNowVisible() {
	}
	

	@Override
	public void notifyDataFetched() {
		statusOfData = DataLoaded.SUCCESS;
	}

	@Override
	public void notifyDataFailedToLoad() {
		statusOfData = DataLoaded.FAILURE;
	}
	
}
