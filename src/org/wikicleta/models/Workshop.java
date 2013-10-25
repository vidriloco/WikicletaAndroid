package org.wikicleta.models;

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
@Table(name = "Workshops")
public class Workshop extends Model implements Serializable, ListedModelInterface, MarkerInterface, RemoteModelInterface {
	
	private static final long serialVersionUID = 1L;
	
	@Column(name = "RemoteId")
	public long remoteId;
	
	@Column(name = "Name")
	public String name;
	
	@Column(name = "Details")
	public String details;
	
	@Column(name = "IsStore")
	public boolean isStore;
	
	@Column(name = "Phone")
	public long phone;
	
	@Column(name = "CellPhone")
	public long cellPhone;
	
	@Column(name = "Webpage")
	public String webpage;
	
	@Column(name = "Twitter")
	public String twitter;
	
	@Column(name = "Horary")
	public String horary;
	
	@Column(name = "UserId")
	public long userId;
	
	@Column(name = "Latitude")
	public double latitude;
	
	@Column(name = "Longitude")
	public double longitude;

	@Column(name = "CreatedAt")
	public long createdAt;
	
	@Column(name = "UpdatedAt")
	public long updatedAt;
	
	public int likesCount;
	public int dislikesCount;

	public String username;
	public String userPicURL;
	protected Marker marker;
	
	public Workshop() {
		this.createdAt = Calendar.getInstance().getTimeInMillis();
		this.updatedAt = Calendar.getInstance().getTimeInMillis();
		this.remoteId = 0;
		this.userPicURL = new String();
	}
	
	public Workshop(long remoteId, String name, String details,
			LatLng point, long userId, int likesCount, int dislikesCount, boolean isStore, 
			long createdAt, long updatedAt,
			String username, long phone, long cellPhone, String webPage,
			String twitter, String horary) {
		this();
		this.remoteId = remoteId;
		this.userId = userId;
		this.name = name;
		this.details = details;
		this.latitude = point.latitude;
		this.longitude = point.longitude;
		this.isStore = isStore;
		this.username = username;
		this.phone = phone;
		this.cellPhone = cellPhone;
		this.webpage = webPage;
		this.twitter = twitter;
		this.horary = horary;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
		this.likesCount = likesCount;
		this.dislikesCount = dislikesCount;
	}

	public boolean existsOnRemoteServer() {
		return this.remoteId != 0;
	}
	
	public HashMap<String, Object> toHashMap() {
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("name", this.name);
		params.put("details", this.details);
		params.put("store", this.isStore);
		params.put("phone", this.phone);
		params.put("cell_phone", this.cellPhone);
		params.put("webpage", this.webpage);
		params.put("twitter", this.twitter);
		params.put("horary", this.horary);
		params.put("store", this.isStore);
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		params.put("created_at", sdf.format(new Date(this.createdAt)));
		params.put("updated_at", sdf.format(new Date()));
		
		//params.put("user_id", this.userId);
		HashMap<String, Float> coordinates = new HashMap<String, Float>();
		coordinates.put("lat", (float) latitude);
		coordinates.put("lon", (float) longitude);
		
		HashMap<String, Object> cover = new HashMap<String, Object>();
		cover.put("workshop", params);
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
	
	protected static void commitLocally(Workshop workshop) {
		ActiveAndroid.beginTransaction();

		workshop.save();
		
		ActiveAndroid.setTransactionSuccessful();
		ActiveAndroid.endTransaction();
	}

	public static Workshop buildFrom(JSONObject object) {
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
		
		long dislikesCountTmp = (Long) object.get("dislikes_count");
		int dislikesCount = (int) dislikesCountTmp;
		
		JSONObject owner = (JSONObject) object.get("owner");
		long userId = (Long) owner.get("id");
		String username = (String) owner.get("username");
				
		boolean isStore = (Boolean) object.get("store");

		Object phoneB = object.get("phone");
		Object cellPhoneB = object.get("cell_phone");
		
		long phone = 0;
		long cellPhone = 0;
		
		if(phoneB != null)
			phone = (Long) phoneB;
		if(cellPhoneB != null)
			cellPhone = (Long) cellPhoneB;
		
		String webPage = (String) object.get("webpage");
		String twitter = (String) object.get("twitter");
		String horary = (String) object.get("horary");
		LatLng point = new LatLng((Double) object.get("lat"), (Double) object.get("lon"));
		Workshop workshop = new Workshop(remoteId, name, details, point, userId, likesCount, dislikesCount, isStore, 
				createdAt, updatedAt, username, phone, cellPhone, webPage, twitter, horary);
		if(owner.containsKey("pic"))
			workshop.userPicURL = (String) owner.get("pic");
		
		return workshop;
	}

	@Override
	public LatLng getLatLng() {
		return new LatLng(this.latitude, this.longitude);
	}

	@Override
	public int getDrawable() {
		return R.drawable.workshop_icon;
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
		return "Workshop";
	}
	
	@Override
	public String getDetails() {
		return this.details;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public Date getDate() {
		return new Date(this.createdAt);
	}

	@Override
	public int getTitle() {
		if(this.isStore) {
			return R.string.workshop_store;
		}
		return R.string.workshop;
	}

	@Override
	public String getSubtitle() {
		return null;
	}

}
