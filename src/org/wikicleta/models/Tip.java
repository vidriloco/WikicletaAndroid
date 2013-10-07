package org.wikicleta.models;

import java.io.IOException;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import org.interfaces.MarkerInterface;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.wikicleta.R;
import android.annotation.SuppressLint;
import com.activeandroid.ActiveAndroid;
import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

@SuppressLint("SimpleDateFormat")
@Table(name = "Tips")
public class Tip extends Model implements Serializable, DraftModel, MarkerInterface {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Column(name = "RemoteId")
	public long remoteId;
	
	@Column(name = "Category")
	public int category;
	
	@Column(name = "Content")
	public String content;
	
	@Column(name = "Latitude")
	public double latitude;
	
	@Column(name = "Longitude")
	public double longitude;

	@Column(name = "CreatedAt")
	public long createdAt;
	@Column(name = "UpdatedAt")
	public long updatedAt;
	
	@Column(name = "UserId")
	public long userId;
	
	public int likesCount;
	public String username;
	public String userPicURL;
	protected Marker marker;

	@SuppressLint("UseSparseArrays")
	protected static HashMap<Integer, String> categories = new HashMap<Integer, String>();
	
	public Tip(String content, int category, double lat, double lon, long userId) {
		this();
		this.content = content;
		this.category = category;
		this.latitude = lat;
		this.longitude = lon;
		this.userId = userId;
		this.userPicURL = new String();
	}
	
	public Tip(long remoteId, String content, int category, LatLng geopoint, long userId, int likes, long millisC, long millisU, String username) {
		this(content, category, geopoint.latitude, geopoint.longitude, userId);
		this.remoteId = remoteId;
		this.likesCount = likes;
		this.createdAt = millisC;
		this.updatedAt = millisU;
		this.username = username;
	}
	
	public Tip() {
		this.createdAt = Calendar.getInstance().getTimeInMillis();
		this.updatedAt = Calendar.getInstance().getTimeInMillis();
		this.remoteId = 0;
	}
	
	public HashMap<String, Object> toHashMap() {
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("category", this.category);
		params.put("content", this.content);
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		params.put("created_at", sdf.format(new Date(this.createdAt)));
		params.put("updated_at", sdf.format(new Date()));
		
		//params.put("user_id", this.userId);
		HashMap<String, Float> coordinates = new HashMap<String, Float>();
		coordinates.put("lat", (float) latitude);
		coordinates.put("lon", (float) longitude);
		HashMap<String, Object> cover = new HashMap<String, Object>();
		cover.put("tip", params);
		cover.put("coordinates", coordinates);
		return cover;
	}
	
	public String toJSON(HashMap<String, Object> object) {
		HashMap<String, Object> tipEnvelope = toHashMap();
		tipEnvelope.put("extras", object);
		return JSONValue.toJSONString(tipEnvelope);
	}
	
	public String toJSON() {
		return JSONValue.toJSONString(toHashMap());
	}
	
	public String categoryString() {
		return Tip.getCategories().get(this.category);
	}
	
	public boolean hasPic() {
		return !this.userPicURL.isEmpty();
	}
	
	public boolean isOwnedByCurrentUser() {
		return User.id() == this.userId;
	}
	
	public boolean existsOnRemoteServer() {
		return this.remoteId != 0;
	}
	
	protected static void commitLocally(Tip tip) {
		ActiveAndroid.beginTransaction();

		tip.save();
		
		ActiveAndroid.setTransactionSuccessful();
		ActiveAndroid.endTransaction();
	}
	
	public static HashMap<Integer, String> getCategories() {
		if(categories.isEmpty()) {
			categories.put(1, "danger");
			categories.put(2, "alert");
			categories.put(3, "sightseeing");
		}
		return categories;
	}
	
	public static String[] getCategoriesValues() {
		String[] categoriesStrs = new String[getCategories().size()];
		for(int i=1; i< getCategories().size() ; i++) {
			categoriesStrs[i-1] = getCategories().get(i);
		}
		return categoriesStrs;
	}

	@Override
	public String getContent() {
		return this.content;
	}

	@Override
	public String getCategoryName() {
		return "tips.categories.".concat(this.categoryString());
	}

	@Override
	public boolean requiresCategoryTranslation() {
		return true;
	}
	
	@Override
	public Date getDate() {
		return new Date(this.createdAt);
	}
	
	@SuppressLint("SimpleDateFormat")
	public static Tip buildFrom(JSONObject object) throws IOException {
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
				
		LatLng point = new LatLng((Double) object.get("lat"), (Double) object.get("lon"));
		Tip tip = new Tip(id, content, category, point, userId, likesCount, createdAt, updatedAt, name);
		if(owner.containsKey("pic"))
			tip.userPicURL = (String) owner.get("pic");
		
		return tip;
	}

	@Override
	public LatLng getLatLng() {
		return new LatLng(this.latitude, this.longitude);
	}

	@Override
	public int getDrawable() {
		if(categoryString().equalsIgnoreCase("danger"))
			return R.drawable.tip_danger_icon;
		else if(categoryString().equalsIgnoreCase("alert"))
			return R.drawable.tip_alert_icon;
		else if(categoryString().equalsIgnoreCase("sightseeing"))
			return R.drawable.tip_sightseeing_icon;
		else
			return 0;
	}

	@Override
	public Marker getAssociatedMarker() {
		return marker;
	}
	
	@Override
	public void setMarker(Marker marker) {
		this.marker = marker;
	}

}
