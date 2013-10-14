package org.wikicleta.activities.routes;

import org.wikicleta.R;
import org.wikicleta.activities.DiscoverActivity;
import org.wikicleta.activities.common.LocationAwareMapWithMarkersActivity;
import org.wikicleta.adapters.PerformancesListAdapter;
import org.wikicleta.common.AppBase;
import org.wikicleta.common.FieldValidators;
import org.wikicleta.helpers.NotificationBuilder;
import org.wikicleta.models.Route;
import org.wikicleta.routing.Routes;
import org.wikicleta.services.routes.RouteTrackingService;
import org.wikicleta.services.routes.ServiceConstructor;
import org.wikicleta.views.RouteViews;
import org.wikicleta.layers.RouteOverlay;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class RouteDetailsActivity extends LocationAwareMapWithMarkersActivity implements RoutesConnectorInterface {
	
	protected NotificationBuilder notification;
	protected RouteOverlay routesOverlay;
	public static Route currentRoute;
	public AlertDialog.Builder alertDialog;
	protected AlertDialog toggleLayersMenu;
	
	//Service
	protected RouteTrackingService theService;
	ServiceConstructor serviceInitializator;
	private ImageView returnIcon;
	private ImageView moreInfoIcon;
	private ImageView performancesIcon;
	
	private Dialog performancesDialog;

	public void onCreate(Bundle savedInstanceState) {
		attemptCenterOnLocationAtStart = false;
		super.onCreate(savedInstanceState,R.layout.activity_route_details);
		setTheme(R.style.Theme_wikicleta);
		
        AppBase.currentActivity = this;
        
        if(currentRoute == null)
        	AppBase.launchActivity(DiscoverActivity.class);
        else {
        	map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentRoute.originCoordinate, 15));
            
            returnIcon = (ImageView) this.findViewById(R.id.return_button);
        	returnIcon.setOnClickListener(new OnClickListener() {

    			@Override
    			public void onClick(View v) {
    				overridePendingTransition(0, 0);
    				finish();
    			}
        		
        	});
        	
        	moreInfoIcon = (ImageView) this.findViewById(R.id.more_info_button);
        	moreInfoIcon.setOnClickListener(new OnClickListener() {

    			@Override
    			public void onClick(View v) {
    				RouteViews.buildViewDetailsExtra(RouteDetailsActivity.this, currentRoute);
    			}
        		
        	});
        	
        	performancesIcon = (ImageView) this.findViewById(R.id.performances_button);
        	performancesIcon.setOnClickListener(new OnClickListener() {

    			@Override
    			public void onClick(View v) {
    				performancesDialog = RouteViews.buildPerformancesView(RouteDetailsActivity.this, currentRoute);
    			}
        		
        	});
        	
        	
        	fetchAndDrawRoute();
        	
        	loadMarkers();
        }
	}
	
	private void loadMarkers() {
		map.addMarker(new MarkerOptions()
        .position(currentRoute.originCoordinate)
        .icon(BitmapDescriptorFactory.fromResource(R.drawable.start_flag_marker)));
		
		map.addMarker(new MarkerOptions()
        .position(currentRoute.endCoordinate)
        .icon(BitmapDescriptorFactory.fromResource(R.drawable.finish_flag_marker)));
	}
	
	private void fetchAndDrawRoute() {
		if(currentRoute.hasNoPathLoaded()) {
			Routes routes = new Routes();
	    	Routes.Show routesFetcher = routes.new Show(this);
	    	routesFetcher.execute(currentRoute);
		}
	}

	public void attemptUpdate(Dialog dialog) {
		EditText nameView = (EditText) dialog.findViewById(R.id.route_name);
		EditText detailsView = (EditText) dialog.findViewById(R.id.route_details);
		CheckBox routeIsPrivate = (CheckBox) dialog.findViewById(R.id.route_is_private);
		
		String routeName = nameView.getText().toString();
		String routeDetails = detailsView.getText().toString();

		if(FieldValidators.isFieldEmpty(routeName)) {
			nameView.setError(getResources().getString(R.string.tips_input_empty));
			return;
		}
		
		if(FieldValidators.isFieldEmpty(routeDetails)) {
			detailsView.setError(getResources().getString(R.string.tips_input_empty));
			return;
		}
		
		currentRoute.updateAttributes(routeName, routeDetails, !routeIsPrivate.isChecked());
		
		Routes.Put updater = new Routes().new Put();
		updater.activity = this;
		updater.execute(currentRoute);
	}
	
	@Override
	public void routeDetailsFinishedLoading(boolean status) {
		if(status) {
			PolylineOptions options = new PolylineOptions();
			for(int i=0; i < currentRoute.path.length ; i++) {
				options.add(new LatLng(currentRoute.path[i][1], currentRoute.path[i][0]));
			}
			
			options.color(R.color.wikicleta_blue);
			this.map.addPolyline(options);
		} else {
			routeDetailsDidNotLoad(status);
		}


	}

	@Override
	public void routeDetailsDidNotLoad(boolean status) {
		overridePendingTransition(0, 0);
		finish();
	}

	@Override
	public void routePerformancesFinishedLoading(boolean status) {
		performancesDialog.setContentView(R.layout.dialog_performances_list);
		TextView dialogTitle = (TextView) performancesDialog.findViewById(R.id.dialog_title);
        dialogTitle.setTypeface(AppBase.getTypefaceStrong());
        
        ((TextView) performancesDialog.findViewById(R.id.username_title_text)).setTypeface(AppBase.getTypefaceStrong());
        ((TextView) performancesDialog.findViewById(R.id.date_title_text)).setTypeface(AppBase.getTypefaceStrong());
        ((TextView) performancesDialog.findViewById(R.id.time_title_text)).setTypeface(AppBase.getTypefaceStrong());
        ((TextView) performancesDialog.findViewById(R.id.velocity_title_text)).setTypeface(AppBase.getTypefaceStrong());

        
        final ListView listview = (ListView) performancesDialog.findViewById(R.id.list_events);
        PerformancesListAdapter listAdapter = new PerformancesListAdapter(this, currentRoute.persistedRoutePerformances);
	    listview.setAdapter(listAdapter);
	    
		((TextView) performancesDialog.findViewById(R.id.challenge_or_check_in_text)).setTypeface(AppBase.getTypefaceStrong());

	}

	@Override
	public void routePerformancesDidNotLoad(boolean status) {
		performancesDialog.dismiss();
		// Add toast with failure legend
	}

}
