package org.wikicleta.fragments.favorites;

import org.wikicleta.R;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class RoutesFragment extends BaseFavoriteFragment {

	static String modelNamed = "Route";

	protected String getModelName() {
		return modelNamed;
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_routes, container, false);
        this.drawLoadingView(view);
        return view;
    }

}
