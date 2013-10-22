package org.wikicleta.fragments.favorites;

import java.util.ArrayList;
import org.interfaces.FragmentNotificationsInterface;
import org.wikicleta.R;
import org.wikicleta.activities.DiscoverActivity;
import org.wikicleta.activities.FavoritesActivity;
import org.wikicleta.adapters.LightPOIsListAdapter;
import org.wikicleta.common.AppBase;
import org.wikicleta.models.LightPOI;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;

public class BaseFavoriteFragment extends Fragment implements FragmentNotificationsInterface {

	AnimatorSet set;
	String modelNamed;
	
	public BaseFavoriteFragment(String modelNamed) {
		this.modelNamed = modelNamed;
	}
	
	protected FavoritesActivity getParentActivity() {
		return (FavoritesActivity) this.getActivity();
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
			((TextView) this.getView().findViewById(R.id.empty_light_list_text)).setTypeface(AppBase.getTypefaceStrong());
		} else {
			switchActiveViewTo(R.id.light_pois_list);
			this.loadListViewFor(objects);
		}
	}
    
    protected void loadListViewFor(ArrayList<LightPOI> objects) {
        final ListView listview = (ListView) this.getView().findViewById(R.id.light_pois_list);
        final LightPOIsListAdapter listAdapter = new LightPOIsListAdapter(this.getActivity(), objects, false);
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
		//this.getParentActivity().fetchUserFavorites();		
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
		//toggleViews(this.getParentActivity().favorites.get(modelNamed));

	}

	@Override
	public void notifyDataFetched() {
		// TODO Auto-generated method stub
		
	}
	
}
