package org.wikicleta.models;

import java.io.IOException;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.json.simple.JSONObject;
import org.wikicleta.R;
import org.wikicleta.interfaces.EventInterface;
import org.wikicleta.interfaces.MarkerInterface;
import org.wikicleta.interfaces.RemoteModelInterface;

import android.annotation.SuppressLint;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

public class CyclingGroup extends Model implements Serializable, MarkerInterface, EventInterface, RemoteModelInterface {

	private static final long serialVersionUID = 1L;
	@Column(name = "RemoteId")
	public long remoteId;
	
	@Column(name = "Name")
	public String name;
	
	@Column(name = "MeetingTime")
	public String meetingTime;
	
	@Column(name = "DepartingTime")
	public String departingTime;
	
	@Column(name = "DaysToEventFromNow")
	public int daysToEventFromNow;
	
	@Column(name = "FacebookURL")
	public String facebookURL;
	
	@Column(name = "twitterAccount")
	public String twitterAccount;
	
	@Column(name = "WebsiteURL")
	public String websiteURL;
	
	@Column(name = "Details")
	public String details;
	
	protected LatLng point;
	
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
	
	@Column(name = "Pic")
	public String pic;
	
	public String username;
	public String userPicURL;
	protected Marker marker;

	public CyclingGroup(long id, String name, String meetingTime,
			String departingTime, int daysToEventFromNow, String facebookURL,
			String twitterAccount, String websiteURL, String details,
			LatLng point, long userId, String username, long createdAt,
			long updatedAt, String pic) {
		this.remoteId = id;
		this.name = name;
		this.meetingTime = meetingTime;
		this.departingTime = departingTime;
		this.twitterAccount = twitterAccount;
		this.websiteURL = websiteURL;
		this.facebookURL = facebookURL;
		this.point = point;
		this.userId = userId;
		this.username = username;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
		this.latitude = point.latitude;
		this.longitude = point.longitude;
		this.pic = pic;
		this.details = details;
		this.daysToEventFromNow = daysToEventFromNow;
	}

	public boolean hasPic() {
		if(this.pic == null)
			return false;
		return !this.pic.isEmpty();
	}
	
	@Override
	public LatLng getLatLng() {
		return this.point;
	}

	@Override
	public int getDrawable() {
		return R.drawable.cycling_group_icon;
	}
	
	@SuppressLint("SimpleDateFormat")
	public static CyclingGroup buildFrom(JSONObject object) throws IOException {
		long id = (Long) object.get("id");
		String name = (String) object.get("name");
		String meetingTime = (String) object.get("meeting_time");
		String departingTime = (String) object.get("departing_time");
		String facebookURL = (String) object.get("facebook_url");
		String twitterAccount = (String) object.get("twitter_account");
		String websiteURL = (String) object.get("website_url");
		
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
		long daysToEventFromNowTmp = (Long) object.get("calculated_days_to_event");
		int daysToEventFromNow = (int) daysToEventFromNowTmp;
		
		JSONObject owner = (JSONObject) object.get("owner");
		long userId = (Long) owner.get("id");
		String username = (String) owner.get("username");
		String pic = (String) object.get("pic");
		
		LatLng point = new LatLng((Double) object.get("lat"), (Double) object.get("lon"));
		CyclingGroup cyclingGroup = new CyclingGroup(id, name, meetingTime, departingTime, daysToEventFromNow, facebookURL, twitterAccount, websiteURL, details, point, userId, username, createdAt, updatedAt, pic);
		if(owner.containsKey("pic"))
			cyclingGroup.userPicURL = (String) owner.get("pic");
		
		return cyclingGroup;
	}

	public int daysToRide() {
	    if(daysToEventFromNow==0)
	    	return R.string.event_today;
	    else if(daysToEventFromNow==1000)
	        return R.string.event_unknown;
	    else if(daysToEventFromNow==1)
	        return R.string.event_tomorrow;
	    else 
	        return R.string.event_other;
	}
	
	@Override
	public int daysAway() {
		return this.daysToEventFromNow;
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
		return "CyclingGroup";
	}
}
