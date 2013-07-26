package org.wikicleta.layers.trips;

import java.util.ArrayList;

import org.wikicleta.R;
import org.wikicleta.common.AppBase;
import org.wikicleta.models.TripPoi;
import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class TripPoisOverlay  {
 /*   private ArrayList<OverlayItem> overlayItems = new ArrayList<OverlayItem>();
	
    public TripPoisOverlay(Drawable marker) {
        super(boundCenterBottom(marker));
        this.populate();
    }

    public void addItem(OverlayItem item) {
    	overlayItems.add(item);
        populate();
    }
 
    @Override
    protected OverlayItem createItem(int i) {
        return overlayItems.get(i);
    }
 
    @Override
    public int size() {
        return overlayItems.size();
    }
 
    @Override
    protected boolean onTap(int i) {
        OverlayItem item = overlayItems.get(i);
        this.buildViewForOverlayItem((TripPoiOverlayItem) item);
        return true;
    }
    
    public void buildViewForOverlayItem(TripPoiOverlayItem item) {
    	final TripPoi tripPoi = item.associatedTripPoi;
    	Activity activity = AppBase.currentActivity;
    	
    	AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        final View view = inflater.inflate(R.layout.poi_details, null);
        
        ImageView categoryImage = (ImageView) view.findViewById(R.id.trip_poi_category_icon);
        categoryImage.setImageResource(item.getDrawable());

        TextView category = (TextView) view.findViewById(R.id.trip_poi_category_title);
        category.setTypeface(AppBase.getTypefaceStrong());
        
        String type = activity.getResources().getString(
        		activity.getResources().getIdentifier(
        				"trip_pois.categories.".concat(tripPoi.categoryString()), "string", activity.getPackageName()));
        
        category.setText(type);
        
        TextView contents = (TextView) view.findViewById(R.id.trip_poi_contents);
        contents.setTypeface(AppBase.getTypefaceStrong());
        contents.setText(tripPoi.details());
        
        TextView model = (TextView) view.findViewById(R.id.model_named);
        model.setTypeface(AppBase.getTypefaceStrong());
        if(tripPoi.name != null)
        	model.setText(tripPoi.name);
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
    
    public void clear() {
    	this.overlayItems.clear();
    }
    */
}
