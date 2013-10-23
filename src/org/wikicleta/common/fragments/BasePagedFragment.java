package org.wikicleta.common.fragments;

import java.util.ArrayList;

import org.interfaces.FragmentNotificationsInterface;
import org.wikicleta.R;
import org.wikicleta.adapters.LightPOIsListAdapter;
import org.wikicleta.helpers.SimpleAnimatorListener;
import org.wikicleta.models.LightPOI;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;

import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

public abstract class BasePagedFragment extends Fragment implements FragmentNotificationsInterface {

	protected static AnimatorSet set;
	
	protected enum DataLoaded {SUCCESS, FAILURE};
	protected static DataLoaded statusOfData = null;
	
	protected int MAX_ATTEMPTS = 1;
	protected static int fetchAttempts = 0;
	
    @Override
    public void onDestroy() {
    	super.onDestroy();
    	statusOfData = null;
    	fetchAttempts = 0;
    }
    
	protected void switchActiveViewTo(View view, int displayId) {
		view.findViewById(R.id.loading_view).setVisibility(View.GONE);
		view.findViewById(R.id.light_pois_list).setVisibility(View.GONE);
		view.findViewById(R.id.empty_list).setVisibility(View.GONE);
		view.findViewById(R.id.attempt_reload_view).setVisibility(View.GONE);
		view.findViewById(displayId).setVisibility(View.VISIBLE);
	}
	
    protected void loadListViewFor(View view, ArrayList<LightPOI> objects) {
        final ListView listview = (ListView) view.findViewById(R.id.light_pois_list);
        final LightPOIsListAdapter listAdapter = new LightPOIsListAdapter(this.getActivity(), objects, true);
	    listview.setAdapter(listAdapter);
	    listview.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
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
	
    protected abstract void displaySuccessOnFetchView(View view);
	protected abstract void displayFailureOnFetchView(final View view);
	
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
	public void notifyDataFetched() {
		statusOfData = DataLoaded.SUCCESS;
	}

	@Override
	public void notifyDataFailedToLoad() {
		statusOfData = DataLoaded.FAILURE;
	}
	
}
