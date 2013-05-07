package org.wikicleta.layers.workshops;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.json.simple.JSONObject;
import org.wikicleta.R;
import org.wikicleta.helpers.GeoHelpers;
import org.wikicleta.layers.common.BaseOverlayItem;
import org.wikicleta.models.Workshop;
import android.annotation.SuppressLint;
import android.content.Context;

import com.google.android.maps.GeoPoint;

public class WorkshopOverlayItem extends BaseOverlayItem {
	public Workshop associatedWorkshop;
	
	public WorkshopOverlayItem(Context ctx, Workshop workshop, GeoPoint point) {
		super(ctx, point, "", "");
		this.associatedWorkshop = workshop;
		this.setMarkerWithDrawable(R.drawable.workshop_icon);
	}

	@SuppressLint("SimpleDateFormat")
	public static WorkshopOverlayItem buildFrom(Context ctx, JSONObject object) throws IOException {
		long remoteId = (Long) object.get("id");
		String name = (String) object.get("name");
		String details = (String) object.get("details");
		
	    DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date creationDate = null;
		Date updateDate = null;
		try {
			creationDate = df.parse((String)  object.get("str_created_at"));
			updateDate = df.parse((String)  object.get("str_updated_at"));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		long createdAt = creationDate.getTime();
		long updatedAt = updateDate.getTime();
		long likesCountTmp = (Long) object.get("likes_count");
		int likesCount = (int) likesCountTmp;
		
		JSONObject owner = (JSONObject) object.get("owner");
		long userId = (Long) owner.get("id");
		String username = (String) owner.get("username");
				
		boolean isStore = (Boolean) object.get("store");
		boolean anyoneCanEdit = (Boolean) object.get("others_can_edit_it");

		long phone = (Long) object.get("phone");
		long cellPhone = (Long) object.get("cell_phone");
		String webPage = (String) object.get("webpage");
		String twitter = (String) object.get("twitter");
		String horary = (String) object.get("horary");
		GeoPoint point = GeoHelpers.buildGeoPointFromLatLon((Double) object.get("lat"), (Double) object.get("lon"));	
		Workshop workshop = new Workshop(remoteId, name, details, point, userId, likesCount, isStore, 
				anyoneCanEdit, createdAt, updatedAt, username, (int) phone, (int) cellPhone, webPage, twitter, horary);
		if(owner.containsKey("pic"))
			workshop.userPicURL = (String) owner.get("pic");
		
		return new WorkshopOverlayItem(ctx, workshop, point);
	}
}
