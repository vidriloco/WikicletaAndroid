package org.wikicleta.activities.challenges;

import java.util.Calendar;
import org.wikicleta.R;
import org.wikicleta.activities.RootActivity;
import org.wikicleta.activities.common.LocationAwareMapWithMarkersActivity;
import org.wikicleta.analytics.AnalyticsBase;
import org.wikicleta.common.AppBase;
import org.wikicleta.models.DeliveryChallenge;
import org.wikicleta.models.DeliveryChallenge.DeliveryType;
import org.wikicleta.views.ChallengeViews;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class ChallengesOnMapActivity extends LocationAwareMapWithMarkersActivity {

	protected ImageView returnButtonIcon;
	protected ImageView switchButtonIcon;

	@SuppressLint("UseSparseArrays")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		shouldAnimateWithCustomTransition = true;
		super.onCreate(savedInstanceState, R.layout.activity_challenges);
		AppBase.currentActivity = this;
		
		AnalyticsBase.reportLoggedInEvent("On Challenges Activity", getApplicationContext());
		
		returnButtonIcon = (ImageView) this.findViewById(R.id.return_button);
		returnButtonIcon.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				AnalyticsBase.reportLoggedInEvent("On Challenges Activity: return to back", getApplicationContext());
				AppBase.launchActivity(RootActivity.class);
			}
    		
    	});
    	
    	switchButtonIcon = (ImageView) this.findViewById(R.id.list_switch_button);
    	switchButtonIcon.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				AppBase.launchActivity(ChallengesOnListActivity.class);
			}
    		
    	});
		
    	map.setOnMarkerClickListener(this);
    	loadFakeEvents();
	}

	private void loadFakeEvents() {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.HOUR_OF_DAY, -1);
		
		DeliveryChallenge foodChallengeOne = new DeliveryChallenge(
				DeliveryType.FOOD, 
				calendar.getTime().getTime(), 
				"Dos Woks de WokLand en la Juárez, a dejarse en la Anzures", 
				19.41703638002043,
				-99.17092463928876,
				19.41703638002043,
				-99.17092463928876,
				"Debe estar en menos de 30 minutos");
		
		calendar.add(Calendar.MINUTE, -15);
		DeliveryChallenge foodChallengeTwo = new DeliveryChallenge(
				DeliveryType.FOOD, 
				calendar.getTime().getTime(), 
				"2 pollos rostizados de Don Pollo a La Colonia Obrera", 
				19.41274602802274,
				-99.16543147522802,
				19.41274602802274,
				-99.16543147522802,
				"Ninguna");
		
		calendar.add(Calendar.MINUTE, -20);
		DeliveryChallenge packageChallengeOne = new DeliveryChallenge(
				DeliveryType.PACKAGE, 
				calendar.getTime().getTime(), 
				"Llevar oficios de la colonia roma a la colonia alamos", 
				19.404731671047593,
				-99.16663310487135,
				19.404731671047593,
				-99.16663310487135,
				"No deben arrugarse, firmar al recibir");
		
		calendar.add(Calendar.MINUTE, -50);
		DeliveryChallenge packageChallengeTwo = new DeliveryChallenge(
				DeliveryType.PACKAGE, 
				calendar.getTime().getTime(), 
				"Olvidé el cargador de mi celular, recoger y traerlo", 
				19.42173135243808,
				-99.16440150697001,
				19.42173135243808,
				-99.16440150697001,
				"Ninguna");
		
		Marker marker = map.addMarker(new MarkerOptions()
        .position(foodChallengeOne.getLatLng())
        .icon(BitmapDescriptorFactory.fromResource(foodChallengeOne.getDrawable())));
		markers.put(marker.getPosition(), foodChallengeOne);
		
		marker =map.addMarker(new MarkerOptions()
        .position(foodChallengeTwo.getLatLng())
        .icon(BitmapDescriptorFactory.fromResource(foodChallengeTwo.getDrawable())));
		markers.put(marker.getPosition(), foodChallengeTwo);

		marker =map.addMarker(new MarkerOptions()
        .position(packageChallengeOne.getLatLng())
        .icon(BitmapDescriptorFactory.fromResource(packageChallengeOne.getDrawable())));
		markers.put(marker.getPosition(), packageChallengeOne);

		marker =map.addMarker(new MarkerOptions()
        .position(packageChallengeTwo.getLatLng())
        .icon(BitmapDescriptorFactory.fromResource(packageChallengeTwo.getDrawable())));
		markers.put(marker.getPosition(), packageChallengeTwo);

	}
	
	@Override
	public boolean onMarkerClick(Marker marker) {
		ChallengeViews.buildViewForChallenge(this, (DeliveryChallenge) markers.get(marker.getPosition()));
		return true;
	}
}
