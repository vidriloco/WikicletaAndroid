package org.wikicleta.adapters;

import java.util.List;
import org.wikicleta.R;
import org.wikicleta.common.AppBase;
import org.wikicleta.models.LightPOI;
import org.wikicleta.models.helpers.ListedModelExtractor;

import com.ocpsoft.pretty.time.PrettyTime;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class LightPOIsListAdapter extends ArrayAdapter<LightPOI> {

	
	protected final Context context;
	protected final List<LightPOI> objects;
	protected LayoutInflater inflater;
	protected boolean showKind;
  	  
	public LightPOIsListAdapter(Context context, List<LightPOI> objects, boolean showKind) {
		super(context, R.layout.item_light_on_list, objects);
		this.context = context;
		this.objects = objects;
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.showKind = showKind;
		ListedModelExtractor.context = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
	    LightPOI lightPOI = objects.get(position);
	    
		View rowView = inflater.inflate(R.layout.item_light_on_list, parent, false);
	    TextView lightTitleText = (TextView) rowView.findViewById(R.id.light_poi_title);
	    lightTitleText.setTypeface(AppBase.getTypefaceLight());
	    lightTitleText.setText(ListedModelExtractor.extractModelTitle(lightPOI));
	    
	    TextView lightSubTitleText = (TextView) rowView.findViewById(R.id.light_poi_subtitle);
	    lightSubTitleText.setTypeface(AppBase.getTypefaceStrong());
	    lightSubTitleText.setText(ListedModelExtractor.extractModelSubtitle(lightPOI));

	    TextView lightDescriptionText = (TextView) rowView.findViewById(R.id.light_poi_details);
	    lightDescriptionText.setTypeface(AppBase.getTypefaceLight());
	    lightDescriptionText.setText(lightPOI.description);

	    TextView lastLightUpdatedText = (TextView) rowView.findViewById(R.id.light_poi_date);
	    lastLightUpdatedText.setTypeface(AppBase.getTypefaceLight());

	    PrettyTime ptime = new PrettyTime();
	    lastLightUpdatedText.setText(this.context.getResources().getString(R.string.updated_on).concat(" ").concat(ptime.format(lightPOI.updatedAt)));
	    
	   	((ImageView) rowView.findViewById(R.id.ligth_poi_icon)).setImageResource(lightPOI.getDrawable());
	    
	    return rowView;
	}
	
	@Override
	public boolean isEnabled(int position) {
	    return false;
	}
}
