package org.wikicleta.fragments.about_sections;

import org.wikicleta.R;
import org.wikicleta.common.AppBase;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MadeInMexicoFragment extends Fragment {

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	View view = (View) inflater.inflate(R.layout.fragment_made_in_mexico, null);
    	((TextView) view.findViewById(R.id.made_in_text)).setTypeface(AppBase.getTypefaceLight());
    	((TextView) view.findViewById(R.id.season_text)).setTypeface(AppBase.getTypefaceStrong());
    	((TextView) view.findViewById(R.id.app_version_text)).setTypeface(AppBase.getTypefaceLight());

    	
    	return view;
	}
}
