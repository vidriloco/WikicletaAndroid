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
@Table(name = "Parkings")
public class Parking extends Model implements Serializable, DraftModel {

	private static final long serialVersionUID = 1L;

	@Column(name = "RemoteId")
	public long remoteId;
	
	@Column(name = "Details")
	public String details;
	
	@Column(name = "Kind")
	public int kind;
	
	@Column(name = "Latitude")
	public int latitude;
	
	@Column(name = "Longitude")
	public int longitude;

	@Column(name = "CreatedAt")
	public long createdAt;
	@Column(name = "UpdatedAt")
	public long updatedAt;
	
	@Column(name = "HasRoof")
	public boolean hasRoof;
	
	@Column(name = "AnyoneCanEdit")
	public boolean anyoneCanEdit;
	
	@Column(name = "UserId")
	public long userId;
	
	public int likesCount;
	public String username;
	public String userPicURL;
	
	@SuppressLint("UseSparseArrays")
	protected static HashMap<Integer, String> kinds = new HashMap<Integer, String>();
	
	public Parking() {
		this.createdAt = Calendar.getInstance().getTimeInMillis();
		this.updatedAt = Calendar.getInstance().getTimeInMillis();
		this.remoteId = 0;
	}
	
	public Parking(long remoteId, String details, int kind, GeoPoint point, long userId, int likesCount, 
			boolean hasRoof, boolean anyoneCanEdit, long createdAt, long updatedAt, String name) {
		this();
		this.remoteId = remoteId;
		this.details = details;
		this.kind = kind;
		this.latitude = point.getLatitudeE6();
		this.longitude = point.getLongitudeE6();
		this.hasRoof = hasRoof;
		this.anyoneCanEdit = anyoneCanEdit;
		this.userId = userId;
		this.likesCount = likesCount;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
		this.username = name;
	}
	
	public boolean existsOnRemoteServer() {
		return this.remoteId != 0;
	}
	
	public HashMap<String, Object> toHashMap() {
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("kind", this.kind);
		params.put("details", this.details);
		params.put("has_roof", this.hasRoof);
		params.put("others_can_edit_it", this.anyoneCanEdit);
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		params.put("created_at", sdf.format(new Date(this.createdAt)));
		params.put("updated_at", sdf.format(new Date()));
		
		//params.put("user_id", this.userId);
		HashMap<String, Float> coordinates = new HashMap<String, Float>();
		coordinates.put("lat", (float) (latitude/1E6));
		coordinates.put("lon", (float) (longitude/1E6));
		params.put("coordinates", coordinates);
		HashMap<String, Object> cover = new HashMap<String, Object>();
		cover.put("parking", params);
		return cover;
	}
	
	public boolean hasPic() {
		return this.userPicURL != null;
	}
	
	public boolean isOwnedByCurrentUser() {
		return User.id() == this.userId;
	}
	
	public String toJSON(HashMap<String, Object> object) {
		HashMap<String, Object> tipEnvelope = toHashMap();
		tipEnvelope.put("extras", object);
		return JSONValue.toJSONString(tipEnvelope);
	}
	
	public String toJSON() {
		return JSONValue.toJSONString(toHashMap());
	}
	
	protected static void commitLocally(Parking parking) {
		ActiveAndroid.beginTransaction();

		parking.save();
		
		ActiveAndroid.setTransactionSuccessful();
		ActiveAndroid.endTransaction();
	}
	
	public String kindString() {
		return Parking.getKinds().get(this.kind);
	}
	
	public static HashMap<Integer, String> getKinds() {
		if(kinds.isEmpty()) {
			kinds.put(1, "government_provided");
			kinds.put(2, "urban_mobiliary");
			kinds.put(3, "venue_provided");
		}
		return kinds;
	}
	
	public static String[] getKindsValues() {
		String[] kindsStrs = new String[getKinds().size()];
		for(int i=1; i< getKinds().size() ; i++) {
			kindsStrs[i-1] = getKinds().get(i);
		}
		return kindsStrs;
	}

	@Override
	public String getContent() {
		return this.details;
	}

	@Override
	public String getCategoryName() {
		return "parkings.kinds.".concat(this.kindString());
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
