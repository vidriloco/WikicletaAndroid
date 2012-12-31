package com.wikicleta.activities;

import org.mobility.wikicleta.R;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.TransitionDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.os.Build;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.android.maps.MapActivity;
import com.wikicleta.common.AppBase;
import com.wikicleta.common.Constants;
import com.wikicleta.models.Route;
import com.wikicleta.views.PinchableMapView;
import com.wikicleta.views.RouteOverlay;

public class RouteDetailsActivity extends MapActivity implements LocationListener {
	private PinchableMapView mapView;
	private LinearLayout bottomToolBarView;
	private LinearLayout topToolBarView;
	
	protected RouteOverlay routeOverlay;
	public static Route currentRoute;
	public AlertDialog.Builder alertDialog;
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        AppBase.currentActivity = this;

        this.setContentView(R.layout.activity_route_details);
        
        TextView routeNameView = (TextView) findViewById(R.id.route_name);
        
        if(currentRoute != null)
        	routeNameView.setText(currentRoute.name);
        
        final ImageView closeMoreIcon = (ImageView) findViewById(R.id.close_button);
        closeMoreIcon.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				TransitionDrawable transition = (TransitionDrawable) closeMoreIcon.getBackground();
				transition.startTransition(200);
				transition.setCrossFadeEnabled(true);
				
				Intent intentActivity = new Intent(AppBase.currentActivity, RoutesActivity.class);
				AppBase.currentActivity.startActivity(intentActivity);
			}
	    	
	    });
               
        this.mapView = (PinchableMapView) findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(false);
        
        drawRoutePath();
        drawControls();
        
        this.mapView.getController().setCenter(currentRoute.instants().get(0).geoPoint());
	}
	
	@SuppressLint("NewApi")
	public void drawControls() {
		if(currentRoute.isDraft()) {
	        topToolBarView = (LinearLayout) findViewById(R.id.top_panel_route_status);
	        bottomToolBarView = (LinearLayout) findViewById(R.id.bottom_panel_route_status_actions);
	        
	        // Buttons preparations
	        findViewById(R.id.route_save_button).setOnClickListener(new OnClickListener() {

				public void onClick(View arg0) {

				}
		    	
		    });
	        
	     // Buttons preparations
	    	alertDialog = new AlertDialog.Builder(this);

	        findViewById(R.id.route_discard_button).setOnClickListener(new OnClickListener() {

				public void onClick(View arg0) {
					alertDialog.setTitle("Pregunta");
					alertDialog.setMessage("ÀDeseas descartar esta ruta?");
					alertDialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							currentRoute.delete();

							Intent intentActivity = new Intent(AppBase.currentActivity, RoutesActivity.class);
							AppBase.currentActivity.startActivity(intentActivity);
						}
					});
					alertDialog.setNegativeButton("No", null);
					alertDialog.setNeutralButton(null, null);
					alertDialog.show();
				}
		    	
		    });
	        
		} else {
	        topToolBarView = (LinearLayout) findViewById(R.id.top_panel_extra_route_info);
	        bottomToolBarView = (LinearLayout) findViewById(R.id.bottom_panel_route_actions);
	        
	        // Buttons preparations
	        final ImageView layersMenuButton = (ImageView) findViewById(R.id.route_layers_button);
	        layersMenuButton.setOnClickListener(new OnClickListener() {

				public void onClick(View arg0) {
					openContextMenu(layersMenuButton);
				}
		    	
		    });
	        registerForContextMenu(layersMenuButton);
		}
		
        topToolBarView.setVisibility(View.VISIBLE);
        topToolBarView.animate().alpha((float) 0.8);
        bottomToolBarView.setVisibility(View.VISIBLE);
        bottomToolBarView.animate().alpha((float) 0.8).translationYBy(-Constants.DY_TRANSLATION);
	}

	@Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.route_details_layers_menu , menu);
    }
	
	@Override
    public boolean onContextItemSelected(MenuItem item) {

        switch(item.getItemId()){
            case R.id.route_layers_basic_info_item:
            	this.topToolBarView.setVisibility(View.VISIBLE);
                break;
            case R.id.route_layers_highlights_item:
                break;
            case R.id.route_layers_none_item:
            	this.topToolBarView.setVisibility(View.GONE);
            	break;
            case R.id.route_layers_places_item:
            	break;
        }
        
        return true;
    }
	
	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}
	
	protected void drawRoutePath() {
		routeOverlay = new RouteOverlay(currentRoute.instants());
		mapView.getOverlays().add(routeOverlay);
		
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
	
	
}
