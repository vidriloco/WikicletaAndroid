package org.wikicleta.views;

import org.wikicleta.R;
import org.wikicleta.common.AppBase;
import org.wikicleta.models.TripPoi;

import android.app.Activity;
import android.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class TripPoiViews {
	public static void buildViewForTripPoi(final Activity activity, TripPoi item) {
		Log.e("WIKICLETA", "Prueba");
    	AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        final View view = inflater.inflate(R.layout.poi_details, null);
        
        ImageView categoryImage = (ImageView) view.findViewById(R.id.trip_poi_category_icon);
        categoryImage.setImageResource(item.getDrawable());

        TextView category = (TextView) view.findViewById(R.id.trip_poi_category_title);
        category.setTypeface(AppBase.getTypefaceStrong());
        
        String type = activity.getResources().getString(
        		activity.getResources().getIdentifier(
        				"trip_pois.categories.".concat(item.categoryString()), "string", activity.getPackageName()));
        
        category.setText(type);
        
        TextView contents = (TextView) view.findViewById(R.id.trip_poi_contents);
        contents.setTypeface(AppBase.getTypefaceStrong());
        contents.setText(item.details());
        
        TextView model = (TextView) view.findViewById(R.id.model_named);
        model.setTypeface(AppBase.getTypefaceStrong());
        if(item.name != null)
        	model.setText(item.name);
        builder.setView(view);

        final AlertDialog tripDialog = builder.create();
        view.findViewById(R.id.dialog_close).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				tripDialog.dismiss();
			}
        	
        });
        
        
        tripDialog.show();
    }
}
