package org.wikicleta.activities.routes;

import java.text.DecimalFormat;
import org.interfaces.RemoteFetchingDutyListener;
import org.wikicleta.R;
import org.wikicleta.activities.DiscoverActivity;
import org.wikicleta.activities.common.LocationAwareMapWithMarkersActivity;
import org.wikicleta.adapters.PerformancesListAdapter;
import org.wikicleta.common.AppBase;
import org.wikicleta.common.FieldValidators;
import org.wikicleta.common.Toasts;
import org.wikicleta.helpers.NotificationBuilder;
import org.wikicleta.models.Route;
import org.wikicleta.models.RouteRanking;
import org.wikicleta.routing.RouteRankings;
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
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class RouteDetailsActivity extends LocationAwareMapWithMarkersActivity implements RoutesConnectorInterface, RemoteFetchingDutyListener {
	
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
	private Dialog rankingDialog;
	
	private RouteRanking lastRouteRanking;
	
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
	}
	
	protected void attemptCommitRouteRanking(int security, int speed, int comfort) {
		RouteRankings rankings = new RouteRankings();
		RouteRankings.Post commentsPoster = rankings.new Post(this);
		lastRouteRanking = new RouteRanking(currentRoute.remoteId, security, speed, comfort);
    	commentsPoster.execute(lastRouteRanking);
	}
	
	public void showRankingRouteView(final Dialog dialog) {
		dialog.hide();
		rankingDialog = new Dialog(this);
		rankingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		rankingDialog.setContentView(R.layout.dialog_route_ranking);
		
		((TextView) rankingDialog.findViewById(R.id.route_security_value_text)).setTypeface(AppBase.getTypefaceStrong());
		((TextView) rankingDialog.findViewById(R.id.route_fast_value_text)).setTypeface(AppBase.getTypefaceStrong());
		((TextView) rankingDialog.findViewById(R.id.route_comfort_value_text)).setTypeface(AppBase.getTypefaceStrong());
		
		((TextView) rankingDialog.findViewById(R.id.ranking_instructions_text)).setTypeface(AppBase.getTypefaceLight());

		((TextView) rankingDialog.findViewById(R.id.route_name_text)).setTypeface(AppBase.getTypefaceStrong());
		((TextView) rankingDialog.findViewById(R.id.route_name_text)).setText(currentRoute.name);
		DecimalFormat format=new DecimalFormat("#.##");

		TextView kilometers = (TextView) rankingDialog.findViewById(R.id.route_kms_text);
		kilometers.setText(format.format(currentRoute.kilometers).concat(" Km/h"));
		kilometers.setTypeface(AppBase.getTypefaceStrong());
		
		rankingDialog.findViewById(R.id.route_security_container).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				changeSelectedRankingView(v);
			}
			
		});
		
		rankingDialog.findViewById(R.id.route_comfort_container).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				changeSelectedRankingView(v);
			}
			
		});
		
		rankingDialog.findViewById(R.id.route_fast_container).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				changeSelectedRankingView(v);
			}
			
		});
		
		((TextView) rankingDialog.findViewById(R.id.button_save_text)).setTypeface(AppBase.getTypefaceStrong());
		rankingDialog.findViewById(R.id.save_button_container).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int speed = getSelectedRankingValueForView(rankingDialog.findViewById(R.id.route_fast_container));
				int comfort = getSelectedRankingValueForView(rankingDialog.findViewById(R.id.route_comfort_container));
				int security = getSelectedRankingValueForView(rankingDialog.findViewById(R.id.route_security_container));
				if(speed > 0  && comfort > 0 && security > 0)
					attemptCommitRouteRanking(security, speed, comfort);
					
			}
			
		});
		
		((TextView) rankingDialog.findViewById(R.id.button_return_text)).setTypeface(AppBase.getTypefaceStrong());
		rankingDialog.findViewById(R.id.return_button_container).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {		
				rankingDialog.dismiss();
				dialog.show();
			}
			
		});
		rankingDialog.show();
		
		RouteRankings rankings = new RouteRankings();
		RouteRankings.Get commentsPoster = rankings.new Get(this);
    	commentsPoster.execute(currentRoute);
	}
	
	protected int getSelectedRankingValueForView(View v) {
		if(v.findViewById(R.id.ranking_value_1).getVisibility() == View.VISIBLE)
			return 1;
		else if(v.findViewById(R.id.ranking_value_2).getVisibility() == View.VISIBLE)
			return 2;
		else if(v.findViewById(R.id.ranking_value_3).getVisibility() == View.VISIBLE)
			return 3;
		else
			return 0;
	}
	
	protected void changeSelectedRankingView(View v) {
		if(v.findViewById(R.id.ranking_value_0).getVisibility() == View.VISIBLE) {
			v.findViewById(R.id.ranking_value_0).setVisibility(View.GONE);
			v.findViewById(R.id.ranking_value_1).setVisibility(View.VISIBLE);
			v.findViewById(R.id.ranking_value_2).setVisibility(View.GONE);
			v.findViewById(R.id.ranking_value_3).setVisibility(View.GONE);
		} else if(v.findViewById(R.id.ranking_value_1).getVisibility() == View.VISIBLE) {
			v.findViewById(R.id.ranking_value_1).setVisibility(View.GONE);
			v.findViewById(R.id.ranking_value_2).setVisibility(View.VISIBLE);
			v.findViewById(R.id.ranking_value_3).setVisibility(View.GONE);
		} else if(v.findViewById(R.id.ranking_value_2).getVisibility() == View.VISIBLE) {
			v.findViewById(R.id.ranking_value_1).setVisibility(View.GONE);
			v.findViewById(R.id.ranking_value_2).setVisibility(View.GONE);
			v.findViewById(R.id.ranking_value_3).setVisibility(View.VISIBLE);
		} else if(v.findViewById(R.id.ranking_value_3).getVisibility() == View.VISIBLE) {
			v.findViewById(R.id.ranking_value_1).setVisibility(View.VISIBLE);
			v.findViewById(R.id.ranking_value_2).setVisibility(View.GONE);
			v.findViewById(R.id.ranking_value_3).setVisibility(View.GONE);
		}	
	}

	@Override
	public void onSuccess(Object duty) {
		if(duty instanceof String) {
			String dutyKind = (String) duty;
			if(dutyKind.equalsIgnoreCase("Post")) {
				Toasts.showToastWithMessage(this, R.string.route_ranking_shared_successfully, R.drawable.success_icon);
				rankingDialog.dismiss();
				currentRoute.updateWith(lastRouteRanking);
				RouteViews.buildViewDetailsExtra(RouteDetailsActivity.this, currentRoute);
			}
		} else if(duty instanceof RouteRanking) {
			RouteViews.setRankedViewsWith(this, rankingDialog, (RouteRanking) duty);
		}
	}

	@Override
	public void onFailed(Object message) {
		if(message instanceof String) {
			String dutyKind = (String) message;
			if(dutyKind.equalsIgnoreCase("Post")) {
				Toasts.showToastWithMessage(this, R.string.route_ranking_failed_to_share, R.drawable.failure_icon);
			}
		}		
	}

	@Override
	public void onFailed() {
		// TODO Auto-generated method stub
		
	}

}
