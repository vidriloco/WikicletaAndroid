package org.wikicleta.fragments.favorites;

import java.util.ArrayList;
import org.wikicleta.R;
import org.wikicleta.activities.FavoritesActivity;
import org.wikicleta.common.AppBase;
import org.wikicleta.common.fragments.BasePagedFragment;
import org.wikicleta.models.LightPOI;
import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import com.nineoldandroids.animation.AnimatorSet;

public class BaseFavoriteFragment extends BasePagedFragment {

	AnimatorSet set;
	FavoritesActivity activity;
	
	protected FavoritesActivity getAssociatedActivity() {
		return activity;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.activity = (FavoritesActivity) activity;
	}
	
	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
	    super.setUserVisibleHint(isVisibleToUser);
	    if (isVisibleToUser) { 
	    	this.triggerFetch();
	    }
	}
	
	protected String getModelName() {
		return null;
	}
	
	protected void displaySuccessOnFetchView(View view) {
    	ArrayList<LightPOI> objects = getAssociatedActivity().favorites.get(this.getModelName());
		if(objects == null || objects.isEmpty()) {
			switchActiveViewTo(view, R.id.empty_list);
			((ImageView) view.findViewById(R.id.empty_light_list_icon)).setImageResource(R.drawable.favorites_icon_big);
			((TextView) view.findViewById(R.id.empty_light_list_text)).setTypeface(AppBase.getTypefaceStrong());
			((TextView) view.findViewById(R.id.empty_light_list_text)).setText(R.string.favorites_empty_list);

		} else {
			switchActiveViewTo(view, R.id.light_pois_list);
			this.loadListViewFor(view, objects);
		}
	}

	@Override
	public void triggerFetch() {
		if(getAssociatedActivity()!=null)
			getAssociatedActivity().fetchUserFavorites();
	}

	@Override
	public void notifyIsNowVisible() {
		
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
	
}
