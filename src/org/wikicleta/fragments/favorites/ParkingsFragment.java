package org.wikicleta.fragments.favorites;

import org.wikicleta.R;
import org.wikicleta.analytics.AnalyticsBase;
import org.wikicleta.common.AppBase;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ParkingsFragment extends BaseFavoriteFragment {
	
	static String modelNamed = "Parking";

	protected String getModelName() {
		return modelNamed;
	}
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_parkings, container, false);
        
		AnalyticsBase.reportLoggedInEvent("Favorites Activity: On Parkings", AppBase.currentActivity);

        this.drawLoadingView(view);
        return view;
    }
	
}
