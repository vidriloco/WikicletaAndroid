package org.wikicleta.fragments.favorites;

import org.wikicleta.R;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class TipsFragment extends BaseFavoriteFragment {

	static String modelNamed = "Tip";

	protected String getModelName() {
		return modelNamed;
	}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	View view = inflater.inflate(R.layout.fragment_tips, container, false);
        this.drawLoadingView(view);
        return view;
    }

}
