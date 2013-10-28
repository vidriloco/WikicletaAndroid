package org.wikicleta.fragments.activities;

import java.util.ArrayList;
import org.wikicleta.R;
import org.wikicleta.activities.ActivitiesActivity;
import org.wikicleta.analytics.AnalyticsBase;
import org.wikicleta.common.AppBase;
import org.wikicleta.common.fragments.BasePagedFragment;
import org.wikicleta.models.LightPOI;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class RecentsFragment extends BasePagedFragment {
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recents, container, false);
        
		AnalyticsBase.reportLoggedInEvent("Activities Activity: Recents", AppBase.currentActivity);
        
        this.drawLoadingView(view);
        return view;
    }
	
    protected ActivitiesActivity getAssociatedActivity() {
    	return (ActivitiesActivity) AppBase.currentActivity;
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
			this.loadListViewFor(view, objects);
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

	@Override
	public void triggerFetch() {
		if(getAssociatedActivity()!=null)
			getAssociatedActivity().fetchUserActivities();
	}
	
	@Override
	public void notifyIsNowVisible() {
	}
	
}
