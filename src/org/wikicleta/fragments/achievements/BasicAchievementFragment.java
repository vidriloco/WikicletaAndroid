package org.wikicleta.fragments.achievements;

import org.wikicleta.R;
import org.wikicleta.common.AppBase;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class BasicAchievementFragment extends Fragment {

	String kind;
	
	public BasicAchievementFragment() {
		this.kind = "Climber";
	}
	
	public BasicAchievementFragment(String kind) {
		this.kind = kind;
	}
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	View view = (View) inflater.inflate(R.layout.fragment_achievement_basic, null);
    	((TextView) view.findViewById(R.id.achievement_title)).setTypeface(AppBase.getTypefaceStrong());
    	((TextView) view.findViewById(R.id.achievement_scoring_value)).setTypeface(AppBase.getTypefaceStrong());
    	((TextView) view.findViewById(R.id.achievement_scoring_text)).setTypeface(AppBase.getTypefaceLight());
    	((TextView) view.findViewById(R.id.challenges_events_now)).setTypeface(AppBase.getTypefaceLight());
    	((TextView) view.findViewById(R.id.challenges_events_discover)).setTypeface(AppBase.getTypefaceStrong());

    	
    	String type = getActivity().getResources().getString(
    			getActivity().getResources().getIdentifier(
        				"challenges.names.".concat(kind.toLowerCase()), "string", getActivity().getPackageName()));
    	
    	((TextView) view.findViewById(R.id.achievement_title)).setText(type);

    	
    	if(kind.equalsIgnoreCase("Climber"))
    		((ImageView) view.findViewById(R.id.achievement_icon)).setImageResource(R.drawable.mountain_conqueror_icon);
    	else if(kind.equalsIgnoreCase("Messenger"))
    		((ImageView) view.findViewById(R.id.achievement_icon)).setImageResource(R.drawable.messenger_icon);
    	else if(kind.equalsIgnoreCase("High"))
    		((ImageView) view.findViewById(R.id.achievement_icon)).setImageResource(R.drawable.climber_icon);
    	else if(kind.equalsIgnoreCase("Plain"))
    		((ImageView) view.findViewById(R.id.achievement_icon)).setImageResource(R.drawable.plain_icon);
    	else if(kind.equalsIgnoreCase("Multimodal"))
    		((ImageView) view.findViewById(R.id.achievement_icon)).setImageResource(R.drawable.multimodal_icon);
    	else if(kind.equalsIgnoreCase("Nocturne"))
    		((ImageView) view.findViewById(R.id.achievement_icon)).setImageResource(R.drawable.nocturne_icon);
    	
        return view;
    }
    
}
