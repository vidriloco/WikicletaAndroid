package org.wikicleta.models;

import java.io.IOException;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.wikicleta.R;
import org.wikicleta.interfaces.ListedModelInterface;
import org.wikicleta.interfaces.MarkerInterface;
import org.wikicleta.interfaces.RemoteModelInterface;

import android.annotation.SuppressLint;
import com.activeandroid.ActiveAndroid;
import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

@SuppressLint("SimpleDateFormat")
@Table(name = "Parkings")
public class Parking extends Model implements Serializable, ListedModelInterface, MarkerInterface, RemoteModelInterface  {

	private static final long serialVersionUID = 1L;

	@Column(name = "RemoteId")
	public long remoteId;
	
	@Column(name = "Details")
	public String details;
	
	@Column(name = "Kind")
	public int kind;
	
	@Column(name = "Latitude")
	public double latitude;
	
	@Column(name = "Longitude")
	public double longitude;

	@Column(name = "CreatedAt")
	public long createdAt;
	@Column(name = "UpdatedAt")
	public long updatedAt;
	
	@Column(name = "HasRoof")
	public boolean hasRoof;
	
	@Column(name = "UserId")
	public long userId;
	
	public int likesCount;
	public int dislikesCount;
	
	public String username;
	public String userPicURL;
	protected Marker marker;

	@SuppressLint("UseSparseArrays")
	protected static HashMap<Integer, String> kinds = new HashMap<Integer, String>();
	
	public Parking() {
		this.createdAt = Calendar.getInstance().getTimeInMillis();
		this.updatedAt = Calendar.getInstance().getTimeInMillis();
		this.remoteId = 0;
		this.userPicURL = new String();
	}
	
	public Parking(long remoteId, String details, int kind, LatLng point, long userId, int likesCount, int dislikesCount, 
			boolean hasRoof, long createdAt, long updatedAt, String name) {
		this();
		this.remoteId = remoteId;
		this.details = details;
		this.kind = kind;
		this.latitude = point.latitude;
		this.longitude = point.longitude;
		this.hasRoof = hasRoof;
		this.userId = userId;
		this.likesCount = likesCount;
		this.dislikesCount = dislikesCount;
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
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		params.put("created_at", sdf.format(new Date(this.createdAt)));
		params.put("updated_at", sdf.format(new Date()));
		
		//params.put("user_id", this.userId);
		HashMap<String, Float> coordinates = new HashMap<String, Float>();
		coordinates.put("lat", (float) latitude);
		coordinates.put("lon", (float) longitude);
		HashMap<String, Object> cover = new HashMap<String, Object>();
		cover.put("parking", params);
		cover.put("coordinates", coordinates);

		return cover;
	}
	
	public boolean hasPic() {
		return !this.userPicURL.isEmpty();
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
	
	public static Parking buildFrom(JSONObject object) throws IOException {
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
			e.printStackTrace();
		}
		long createdAt = creationDate.getTime();
		long updatedAt = updateDate.getTime();
		long likesCountTmp = (Long) object.get("likes_count");
		int likesCount = (int) likesCountTmp;
		
		long dislikesCountTmp = (Long) object.get("dislikes_count");
		int dislikesCount = (int) dislikesCountTmp;
		
		JSONObject owner = (JSONObject) object.get("owner");
		long userId = (Long) owner.get("id");
		String name = (String) owner.get("username");
				
		boolean hasRoof = (Boolean) object.get("has_roof");

		LatLng point = new LatLng((Double) object.get("lat"), (Double) object.get("lon"));	
		Parking parking = new Parking(remoteId, details, kind, point, userId, likesCount, dislikesCount, hasRoof, createdAt, updatedAt, name);
		if(owner.containsKey("pic")) {
			parking.userPicURL = (String) owner.get("pic");
		}
		return parking;
	}
	
	@Override
	public int getDrawable() {
		if(this.kindString() == "government_provided") {
			return R.drawable.parking_government_provided_icon;
		} else if(this.kindString() == "urban_mobiliary") {
			return R.drawable.parking_urban_mobiliary_icon;
		} else if(this.kindString() == "venue_provided") {
			return R.drawable.parking_venue_provided_icon;
		}
		return 0;
	}

	@Override
	public LatLng getLatLng() {
		return new LatLng(this.latitude, this.longitude);
	}

	@Override
	public Marker getAssociatedMarker() {
		return marker;
	}
	
	@Override
	public void setMarker(Marker marker) {
		this.marker = marker;
	}
	
	@Override
	public long getRemoteId() {
		return remoteId;
	}

	@Override
	public String getKind() {
		return "Parking";
	}

	@Override
	public String getDetails() {
		return this.details;
	}

	@Override
	public Date getDate() {
		return new Date(this.createdAt);
	}
	
	@Override
	public int getTitle() {
		return R.string.parking;
	}

	@Override
	public String getSubtitle() {
		return "parkings.kinds.".concat(this.kindString());
	}

	@Override
	public String getName() {
		return null;
	}

}
