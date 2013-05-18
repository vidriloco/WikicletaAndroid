package org.wikicleta.models;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import org.json.simple.JSONValue;
import android.annotation.SuppressLint;
import com.activeandroid.ActiveAndroid;
import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.android.maps.GeoPoint;

@SuppressLint("SimpleDateFormat")
@Table(name = "Tips")
public class Tip extends Model implements Serializable, DraftModel {
	
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
	public int latitude;
	
	@Column(name = "Longitude")
	public int longitude;

	@Column(name = "CreatedAt")
	public long createdAt;
	@Column(name = "UpdatedAt")
	public long updatedAt;
	
	@Column(name = "UserId")
	public long userId;
	
	public int likesCount;
	public String username;
	public String userPicURL;
	
	@SuppressLint("UseSparseArrays")
	protected static HashMap<Integer, String> categories = new HashMap<Integer, String>();
	
	public Tip(String content, int category, int lat, int lon, long userId) {
		this();
		this.content = content;
		this.category = category;
		this.latitude = lat;
		this.longitude = lon;
		this.userId = userId;
	}
	
	public Tip(long remoteId, String content, int category, GeoPoint geopoint, long userId, int likes, long millisC, long millisU, String username) {
		this(content, category, geopoint.getLatitudeE6(), geopoint.getLongitudeE6(), userId);
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
		coordinates.put("lat", (float) (latitude/1E6));
		coordinates.put("lon", (float) (longitude/1E6));
		params.put("coordinates", coordinates);
		HashMap<String, Object> cover = new HashMap<String, Object>();
		cover.put("tip", params);
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
		return this.userPicURL != null;
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
}
