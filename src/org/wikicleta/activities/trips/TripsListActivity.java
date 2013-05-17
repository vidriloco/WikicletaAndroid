package org.wikicleta.activities.trips;

import java.util.ArrayList;
import org.wikicleta.R;
import org.wikicleta.adapters.trips.CitiesListAdapter;
import org.wikicleta.common.AppBase;
import org.wikicleta.helpers.SlidingMenuAndActionBarHelper;
import org.wikicleta.models.City;
import org.wikicleta.routing.CityTrips;
import com.nineoldandroids.animation.ObjectAnimator;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class TripsListActivity extends Activity {

	RelativeLayout cityListSyncContainer;
	protected ObjectAnimator syncAnimator;
	protected CityTrips.Get tripsTask;
	TextView syncingText;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.trips_list_activity);
		AppBase.currentActivity = this;
    	SlidingMenuAndActionBarHelper.loadWithActionBarTitle(this, this.getResources().getString(R.string.trips_list_title));

    	TextView citiesText = (TextView) this.findViewById(R.id.cities_text);
    	citiesText.setTypeface(AppBase.getTypefaceStrong());
    	
    	syncingText = (TextView) this.findViewById(R.id.syncing_text);
    	syncingText.setTypeface(AppBase.getTypefaceLight());
    	    	
    	this.cityListSyncContainer = (RelativeLayout) this.findViewById(R.id.trips_list_sync_container);
    	
    	this.cityListSyncContainer.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(tripsTask == null) {
					tripsTask = new CityTrips().new Get();
					tripsTask.activity = TripsListActivity.this;
					tripsTask.execute((Void) null);
				}
			}
    		
    	});
    	
		syncAnimator = ObjectAnimator.ofFloat(cityListSyncContainer, "alpha", 1, 0.2f, 1);
		syncAnimator.setDuration(2000);
		syncAnimator.setRepeatCount(ObjectAnimator.INFINITE);
		
		CityTrips.Get trips = new CityTrips().new Get();
		trips.activity = this;
		trips.execute((Void) null);
	}
	
	public void populateList(final ArrayList<City> list) {
		final ListView listview = (ListView) findViewById(R.id.cities_list);
        
        CitiesListAdapter adapter = new CitiesListAdapter(this, list);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if(list.get(position).trips.size() == 0)
					return;
				View listOfTrips = view.findViewById(R.id.trips_listed);
				if(listOfTrips.getVisibility() == View.GONE)
					listOfTrips.setVisibility(View.VISIBLE);
				else
					listOfTrips.setVisibility(View.GONE);
			}
        	
        });
	}
	
	public void onSuccessfulRetrievalOfCityTrips(ArrayList<City> trips) {
		tripsTask = null;
		this.populateList(trips);
		
		this.cityListSyncContainer.setVisibility(View.GONE);
		this.findViewById(R.id.trips_list_container).setVisibility(View.VISIBLE);
		syncAnimator.cancel();
	}
	
	public void onUnsuccessfulRetrievalOfCityTrips() {
		tripsTask = null;
		syncAnimator.cancel();
		this.cityListSyncContainer.setVisibility(View.VISIBLE);
		this.findViewById(R.id.trips_list_container).setVisibility(View.GONE);
		syncingText.setText(R.string.trips_city_not_fetched);
	}

	public void onFetchingCitiesStarted() {
		syncingText.setText(R.string.trips_updating_cities);
		syncAnimator.start();
	}
}
