package org.wikicleta.fragments.about_sections;

import org.wikicleta.R;
import org.wikicleta.common.AppBase;

import com.google.android.gms.common.GooglePlayServicesUtil;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class AttributionsFragment extends Fragment {

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	View view = (View) inflater.inflate(R.layout.fragment_attributions, null);
    	((TextView) view.findViewById(R.id.attribution_message)).setTypeface(AppBase.getTypefaceLight());
    	((TextView) view.findViewById(R.id.attribution_message_title)).setTypeface(AppBase.getTypefaceStrong());
    	((TextView) view.findViewById(R.id.attribution_message)).setText(GooglePlayServicesUtil.getOpenSourceSoftwareLicenseInfo(AppBase.currentActivity));
    	return view;
	}
}
