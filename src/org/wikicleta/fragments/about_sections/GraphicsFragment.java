package org.wikicleta.fragments.about_sections;

import org.wikicleta.R;
import org.wikicleta.common.AppBase;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class GraphicsFragment extends Fragment {

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	View view = (View) inflater.inflate(R.layout.fragment_graphics, null);
    	
    	((TextView) view.findViewById(R.id.attribution_icons_text)).setTypeface(AppBase.getTypefaceStrong());
    	((TextView) view.findViewById(R.id.comes_from_text)).setTypeface(AppBase.getTypefaceStrong());
    	return view;
	}
}
