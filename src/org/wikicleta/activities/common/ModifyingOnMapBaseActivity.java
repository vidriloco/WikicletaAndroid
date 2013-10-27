package org.wikicleta.activities.common;

import org.wikicleta.R;
import org.wikicleta.activities.RootActivity;
import org.wikicleta.common.AppBase;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class ModifyingOnMapBaseActivity extends LocationAwareMapWithControlsActivity {
	protected ImageView returnIcon;
	protected ImageView newIcon;
	protected ImageView saveIcon;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.activity_select_poi_on_map);
    	loadActionButtons();
    	
		saveIcon = (ImageView) this.findViewById(R.id.save_button);
    	saveIcon.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				presentSaveForm();
			}
    		
    	}); 

	}
	
	protected void loadActionButtons() {
		returnIcon = (ImageView) this.findViewById(R.id.return_button);
    	returnIcon.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				AppBase.launchActivity(RootActivity.class);
			}
    		
    	});
    	
		newIcon = (ImageView) this.findViewById(R.id.add_button);
    	newIcon.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				buildAndShowMenu();
			}
    		
    	});
	}
	
	protected void presentSaveForm() {
		
	}
	
	protected void buildAndShowMenu() {
		final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        LayoutInflater inflater = this.getLayoutInflater();
        
        View view = inflater.inflate(R.layout.dialog_add, null);
        view.findViewById(R.id.dialog_close).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
        	
        });
        
        TextView dialogTitle = (TextView) view.findViewById(R.id.dialog_menu_title);
        dialogTitle.setTypeface(AppBase.getTypefaceStrong());
        
        TextView highlightTitle = (TextView) view.findViewById(R.id.tips_title);
        highlightTitle.setTypeface(AppBase.getTypefaceStrong());
        
        TextView bikeparkingTitle = (TextView) view.findViewById(R.id.bike_parking_title);
        bikeparkingTitle.setTypeface(AppBase.getTypefaceStrong());
        
        TextView bikeRepairTitle = (TextView) view.findViewById(R.id.bike_repair_title);
        bikeRepairTitle.setTypeface(AppBase.getTypefaceStrong());
        
        view.findViewById(R.id.tips_dialog_group).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				AppBase.launchActivity(org.wikicleta.activities.tips.ModifyingActivity.class);
			}
        });
        
        view.findViewById(R.id.bike_parking_dialog_group).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				AppBase.launchActivity(org.wikicleta.activities.parkings.ModifyingActivity.class);
			}
        });
        
        view.findViewById(R.id.bike_repair_dialog_group).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				AppBase.launchActivity(org.wikicleta.activities.workshops.ModifyingActivity.class);

			}
        });
        
    	dialog.setContentView(view);
		dialog.show();
		getWindow().setBackgroundDrawableResource(android.R.color.transparent); 
	}

}
