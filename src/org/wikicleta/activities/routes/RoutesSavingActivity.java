package org.wikicleta.activities.routes;

import org.wikicleta.R;
import org.wikicleta.activities.DiscoverActivity;
import org.wikicleta.activities.UserProfileActivity;
import org.wikicleta.common.AppBase;
import org.wikicleta.fragments.user_profile.DraftsFragment;
import org.wikicleta.helpers.NotificationBuilder;
import org.wikicleta.helpers.SlidingMenuBuilder;
import org.wikicleta.services.routes.RoutesService;
import org.wikicleta.services.routes.ServiceConstructor;
import org.wikicleta.services.routes.ServiceListener;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class RoutesSavingActivity extends Activity implements ServiceListener {
	
	// UI references.
	private EditText nameView;
	private EditText tagsView;
	
	//Notifications
	NotificationBuilder notification;
	
	//Service
	protected RoutesService theService;
	ServiceConstructor serviceInitializator;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTheme(R.style.Theme_wikicleta);
		
        AppBase.currentActivity = this;
        
        this.setContentView(R.layout.dialog_route_save);
        
        this.nameView = (EditText) findViewById(R.id.route_name);
        this.tagsView = (EditText) findViewById(R.id.route_tags);
        
        TextView subtitle = (TextView) findViewById(R.id.subtitle);
        subtitle.setTypeface(AppBase.getTypefaceLight());
        
        this.notification = new NotificationBuilder(this);
        
		/*ActionBar actionBar = (ActionBar) this.findViewById(R.id.actionbar);

        actionBar.addAction(new Action() {

			@Override
			public int getDrawable() {
				return R.drawable.close_icon;
			}

			@Override
			public void performAction(View view) {
				AlertDialog.Builder alertDialog = new AlertDialog.Builder(AppBase.currentActivity);
				alertDialog.setTitle("Descartar ruta");
				alertDialog.setMessage("ÀQuieres descartar esta ruta?");
				alertDialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						theService.routeRecorder.reset();
						AppBase.launchActivity(MainMapActivity.class);
					}
				});
				// If user chooses 'No', then the dialog closes
				alertDialog.setNegativeButton("No", null);
				alertDialog.show();
			}
        	
        });*/
        
        findViewById(R.id.save_route).setOnClickListener(
    			new View.OnClickListener() {
    				@Override
    				public void onClick(View view) {
    					Bundle bundle = new Bundle();
    					bundle.putString("fragment", DraftsFragment.class.getName());
    					theService.addRecordedRouteToUploader(nameView.getText().toString(), tagsView.getText().toString());
    					AppBase.launchActivityWithBundle(UserProfileActivity.class, bundle);
    				}
    	});
        SlidingMenuBuilder.loadOnLeft(this);

	}
	
	@Override
	protected void onStart() {
		super.onStart();
		serviceInitializator = new ServiceConstructor(this);
        serviceInitializator.start(RoutesService.class);
	}
	
	@Override
	protected void onStop() {
		super.onStop();
        serviceInitializator.stop();
	}
	
	@Override
	public void afterServiceConnected(Service service) {
		if(service instanceof RoutesService)
			this.theService = (RoutesService) service;
	}
	
	// TODO: Decide whether it could be desirable that upon automatic return to activity 
	// route is still getting tracked. If so, buttons state should behave accordingly 
	/*@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if (keyCode == KeyEvent.KEYCODE_BACK) {
	    	if(RoutesActivity.currentPath != null && RoutesActivity.currentPath.isPaused())
	    		RoutesActivity.currentPath.resume();
	    }
	    return super.onKeyDown(keyCode, event);
	}*/
	
}
