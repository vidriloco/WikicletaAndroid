package org.wikicleta.layers.components;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.simple.JSONObject;
import org.wikicleta.R;
import org.wikicleta.helpers.GeoHelpers;
import org.wikicleta.models.Tip;
import android.annotation.SuppressLint;
import android.content.Context;

import com.google.android.maps.GeoPoint;

public class TipOverlayItem extends BaseOverlayItem {

	public Tip associatedTip;
	
	public TipOverlayItem(Context ctx, Tip tip, GeoPoint point) {
		super(ctx, point, "", "");
		this.associatedTip = tip;
		this.setIconAccordingToTipCategory();
	}
	
	public void setIconAccordingToTipCategory() {
		this.setMarkerWithDrawable(getDrawable(associatedTip));
	}
	
	public static int getDrawable(Tip tip) {
		if(tip.categoryString() == "danger") {
			return R.drawable.tip_danger_icon;
		} else if(tip.categoryString() == "alert") {
			return R.drawable.tip_alert_icon;
		} else if(tip.categoryString() == "sightseeing") {
			return R.drawable.tip_sightseeing_icon;
		}
		return 0;
	}

	@SuppressLint("SimpleDateFormat")
	public static TipOverlayItem buildFrom(Context ctx, JSONObject object) throws IOException {
		long id = (Long) object.get("id");
		long categoryTmp = (Long) object.get("category");
		int category = (int) categoryTmp;
		String content = (String) object.get("content");
		
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
				
		GeoPoint point = GeoHelpers.buildGeoPointFromLatLon((Double) object.get("lat"), (Double) object.get("lon"));	
		Tip tip = new Tip(id, content, category, point, userId, likesCount, createdAt, updatedAt, name);
		if(owner.containsKey("pic"))
			tip.userPicURL = (String) owner.get("pic");
		
		return new TipOverlayItem(ctx, tip, point);
	}
}
