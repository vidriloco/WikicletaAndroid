package org.wikicleta.routes.fragments;

import org.wikicleta.R;
import org.wikicleta.common.AppBase;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

public class ProfileFragment extends Fragment {
	
	protected AlertDialog.Builder alertDialog;
	
	public static ProfileFragment newInstance(int index) {
		ProfileFragment f = new ProfileFragment();

        // Supply index input as an argument.
        Bundle args = new Bundle();
        args.putInt("index", index);
        f.setArguments(args);

        return f;
    }

    public int getShownIndex() {
        return getArguments().getInt("index", 0);
    }
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		alertDialog = new AlertDialog.Builder(this.getActivity());
        // Inflate the layout for this fragment
		View fragment =  inflater.inflate(R.layout.fragment_profile, container, false);
		
		TextView profileUsername = (TextView) fragment.findViewById(R.id.profile_username);
		profileUsername.setTypeface(AppBase.getDefaultTypeface("Bold"));
		
		TextView profileCity = (TextView) fragment.findViewById(R.id.profile_city);
		profileCity.setTypeface(AppBase.getDefaultTypeface("Light"));

		TextView profileGearTitle = (TextView) fragment.findViewById(R.id.profile_road_gear_title);
		profileGearTitle.setTypeface(AppBase.getDefaultTypeface("Bold"));

		TextView profileGearDescription = (TextView) fragment.findViewById(R.id.profile_road_gear_description);
		profileGearDescription.setTypeface(AppBase.getDefaultTypeface("Light"));

		// Activity contents
		TextView profileStats = (TextView) fragment.findViewById(R.id.profile_stats_title);
		profileStats.setTypeface(AppBase.getDefaultTypeface("Bold"));

		TextView profilePlacesTitle = (TextView) fragment.findViewById(R.id.profile_places_total_title);
		profilePlacesTitle.setTypeface(AppBase.getDefaultTypeface("Bold"));

		TextView profileRoutesTitle = (TextView) fragment.findViewById(R.id.profile_routes_total_title);
		profileRoutesTitle.setTypeface(AppBase.getDefaultTypeface("Bold"));

		TextView profileHighlightsTitle = (TextView) fragment.findViewById(R.id.profile_highlights_total_title);
		profileHighlightsTitle.setTypeface(AppBase.getDefaultTypeface("Bold"));
		
		TextView profilePlacesCount = (TextView) fragment.findViewById(R.id.profile_places_total_count);
		profilePlacesCount.setTypeface(AppBase.getDefaultTypeface("Bold"));

		TextView profileRoutesCount = (TextView) fragment.findViewById(R.id.profile_routes_total_count);
		profileRoutesCount.setTypeface(AppBase.getDefaultTypeface("Bold"));

		TextView profileHighlightsCount = (TextView) fragment.findViewById(R.id.profile_highlights_total_count);
		profileHighlightsCount.setTypeface(AppBase.getDefaultTypeface("Bold"));
		
		// Stats contents
		TextView profileDistanceTitle = (TextView) fragment.findViewById(R.id.profile_pedal_distance_title);
		profileDistanceTitle.setTypeface(AppBase.getDefaultTypeface("Bold"));

		TextView profileDistanceCount = (TextView) fragment.findViewById(R.id.profile_pedal_distance_count);
		profileDistanceCount.setTypeface(AppBase.getDefaultTypeface("Bold"));

		TextView profileTimeOnRoadTitle = (TextView) fragment.findViewById(R.id.profile_road_time_title);
		profileTimeOnRoadTitle.setTypeface(AppBase.getDefaultTypeface("Bold"));

		TextView profileTimeOnRoadCount = (TextView) fragment.findViewById(R.id.profile_road_time_count);
		profileTimeOnRoadCount.setTypeface(AppBase.getDefaultTypeface("Bold"));
		
		//Badges contents
		TextView profileBadgesTitle = (TextView) fragment.findViewById(R.id.profile_badges_title);
		profileBadgesTitle.setTypeface(AppBase.getDefaultTypeface("Bold"));
		
		TextView profileBadgeRunner = (TextView) fragment.findViewById(R.id.profile_badge_long_runner);
		profileBadgeRunner.setTypeface(AppBase.getDefaultTypeface("Bold"));
		
		TextView profileBadgeFasterThanACar = (TextView) fragment.findViewById(R.id.profile_badge_faster_than_acar);
		profileBadgeFasterThanACar.setTypeface(AppBase.getDefaultTypeface("Bold"));
		
		TextView profileBadgeBromton = (TextView) fragment.findViewById(R.id.profile_badge_brompton);
		profileBadgeBromton.setTypeface(AppBase.getDefaultTypeface("Bold"));

		TextView profileBadgeSurvivor = (TextView) fragment.findViewById(R.id.profile_badge_survivor);
		profileBadgeSurvivor.setTypeface(AppBase.getDefaultTypeface("Bold"));
		
		TextView profileEraseAccountText = (TextView) fragment.findViewById(R.id.profile_erase_account_text);
		profileEraseAccountText.setTypeface(AppBase.getDefaultTypeface("Bold"));
		
		fragment.findViewById(R.id.profile_erase_acount_button).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				alertDialog.setTitle("Pregunta").
				setMessage("¿Deseas eliminar tu cuenta de este teléfono?").
				setNeutralButton(null, null).
				setNegativeButton("Cancelar", null).
				setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						
					}
				});
				
				alertDialog.show();
			}
			
		});
		
		return fragment;
    }
}
