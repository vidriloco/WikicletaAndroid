package org.wikicleta.fragments.favorites;

import org.wikicleta.R;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class RoutesFragment extends BaseFavoriteFragment {

    public RoutesFragment(String modelNamed) {
		super(modelNamed);
	}

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_routes, container, false);
        this.drawLoadingView(rootView);
        return rootView;
    }

}
