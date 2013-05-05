package org.wikicleta.layers.parkings;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.simple.JSONObject;
import org.wikicleta.R;
import org.wikicleta.helpers.GeoHelpers;
import org.wikicleta.layers.common.BaseOverlayItem;
import org.wikicleta.models.Parking;

import android.annotation.SuppressLint;
import android.content.Context;

import com.google.android.maps.GeoPoint;

public class ParkingOverlayItem extends BaseOverlayItem {
	public Parking associatedParking;
	
	public ParkingOverlayItem(Context ctx, Parking parking, GeoPoint point) {
		super(ctx, point, "", "");
		this.associatedParking = parking;
		this.setIconAccordingToTipCategory();
	}
	
	public void setIconAccordingToTipCategory() {
		this.setMarkerWithDrawable(getDrawable(associatedParking));
	}
	
	public static int getDrawable(Parking parking) {
		if(parking.kindString() == "government_provided") {
			return R.drawable.parking_government_provided_icon;
		} else if(parking.kindString() == "urban_mobiliary") {
			return R.drawable.parking_urban_mobiliary_icon;
		} else if(parking.kindString() == "venue_provided") {
			return R.drawable.parking_venue_provided_icon;
		}
		return 0;
	}

	@SuppressLint("SimpleDateFormat")
	public static ParkingOverlayItem buildFrom(Context ctx, JSONObject object) throws IOException {
		long remoteId = (Long) object.get("id");
		long kindTmp = (Long) object.get("kind");
		int kind = (int) kindTmp;
		String details = (String) object.get("details");
		
	    DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date creationDate = null;
		Date updateDate = null;
		try {
			creationDate = df.parse((String)  object.get("str_created_at"));
			updateDate = df.parse((String)  object.get("str_updated_at"));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		long createdAt = creationDate.getTime();
		long updatedAt = updateDate.getTime();
		long likesCountTmp = (Long) object.get("likes_count");
		int likesCount = (int) likesCountTmp;
		
		JSONObject owner = (JSONObject) object.get("owner");
		long userId = (Long) owner.get("id");
		String name = (String) owner.get("username");
				
		boolean hasRoof = (Boolean) object.get("has_roof");
		boolean anyoneCanEdit = (Boolean) object.get("others_can_edit_it");

		GeoPoint point = GeoHelpers.buildGeoPointFromLatLon((Double) object.get("lat"), (Double) object.get("lon"));	
		Parking parking = new Parking(remoteId, details, kind, point, userId, likesCount, hasRoof, anyoneCanEdit, createdAt, updatedAt, name);
		if(owner.containsKey("pic"))
			parking.userPicURL = (String) owner.get("pic");
		
		return new ParkingOverlayItem(ctx, parking, point);
	}
}
