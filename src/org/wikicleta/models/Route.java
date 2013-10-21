package org.wikicleta.models;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.interfaces.MarkerInterface;
import org.interfaces.RemoteModelInterface;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.wikicleta.R;
import android.location.Location;
import com.activeandroid.ActiveAndroid;
import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

@Table(name = "Routes")
public class Route extends Model implements MarkerInterface, Serializable, RemoteModelInterface {

	private static final long serialVersionUID = 1L;

	@Column(name = "RemoteId")
	public long remoteId;
	
	@Column(name = "Name")
	public String name;
	
	@Column(name = "Details")
	public String details;
	
	@Column(name = "Kilometers")
	public float kilometers;

	@Column(name = "IsPublic")
	public boolean isPublic;
	
	@Column(name = "CheckIns")
	public int checkIns;
	
	@Column(name = "Ranking")
	public int ranking;

	@Column(name = "IsDraft")
	public boolean isDraft;
	
	@Column(name = "CreatedAt")
	public long createdAt;
	
	@Column(name = "UserId")
	public long userId;
	
	@Column(name = "UpdatedAt")
	public long updatedAt;
	
	public LatLng originCoordinate;
	public LatLng endCoordinate;
	
	public String username;
	public String userPicURL;
	
	public int likesCount = 0;
	public int dislikesCount = 0;
	public int comfortIndex = 0;
	public int speedIndex = 0;
	public int safetyIndex = 0;
	
	public double [][] path;
	
	public ArrayList<Instant> temporalInstants;
	protected ArrayList<RoutePerformance> temporalRoutePerformances;
	public ArrayList<RoutePerformance> persistedRoutePerformances;

	protected Marker associatedMarker;
	
	protected RoutePerformance performanceTmp;
	
	public ArrayList<Instant> instants() {
		if(temporalInstants == null)
			return getMany(Instant.class, "Route");
		return temporalInstants;
	}
	
	public ArrayList<RoutePerformance> performances() {
		if(temporalRoutePerformances == null)
			return getMany(RoutePerformance.class, "RoutePerformance");
		return temporalRoutePerformances;
	}
	
	/*
	 * Default Route constructor for json retrieved routes data
	 */
	public Route(long id, String name, int likesCount, int dislikesCount, int speed, int safety, int comfort, String details, float kilometers, long createdAt, long updatedAt, Long userId, String username) {
		this.name = name;
		this.details = details;
		this.kilometers = kilometers;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
		this.remoteId = id;
		this.userId = userId;
		this.username = username;
		this.likesCount = likesCount;
		this.dislikesCount = dislikesCount;
		this.userPicURL = new String();
		this.persistedRoutePerformances = new ArrayList<RoutePerformance>();
		this.speedIndex = speed;
		this.safetyIndex = safety;
		this.comfortIndex = comfort;
	}

	public Route(String name, String details, long elapsedTime, float averageSpeed, float kilometers, long createdAt, ArrayList<Instant> coordinateVector, Long userId, boolean shouldAdd) {
		this(elapsedTime, averageSpeed, kilometers, createdAt, shouldAdd);
		this.name = name;
		this.details = details;
		this.temporalInstants = coordinateVector;
		this.userId = userId;
	}
	
	public Route(long elapsedTime, float averageSpeed, float kilometers, long createdAt, boolean shouldAdd) {
		performanceTmp = new RoutePerformance(elapsedTime, averageSpeed);
		if(shouldAdd)
			this.temporalRoutePerformances.add(performanceTmp);
		this.kilometers = kilometers;
		this.createdAt = createdAt;
		this.updatedAt = createdAt;
	}
	
	public boolean existsOnRemoteServer() {
		return this.remoteId != 0;
	}
	
	public HashMap<String, Object> toPutHashMap() {
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("name", this.name);
		params.put("details", this.details);
		params.put("is_public", this.isPublic);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		params.put("updated_at", sdf.format(new Date(this.updatedAt)));
		HashMap<String, Object> cover = new HashMap<String, Object>();
		cover.put("route", params);
		return cover;
	}
	
	public HashMap<String, Object> toHashMap() {
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("name", this.name);
		params.put("details", this.details);
		params.put("is_public", this.isPublic);

		params.put("kilometers", this.kilometers);
		
		HashMap<String, Object> performance = new HashMap<String, Object>();
		performance.put("elapsed_time", performanceTmp.elapsedTime);
		performance.put("average_speed", performanceTmp.averageSpeed);
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		performance.put("created_at", sdf.format(new Date(this.createdAt)));
		performance.put("updated_at", sdf.format(new Date(this.createdAt)));
		
		params.put("route_performance", performance);
		
		params.put("created_at", sdf.format(new Date(this.createdAt)));
		params.put("updated_at", sdf.format(new Date(this.updatedAt)));
		
		ArrayList<HashMap<String, Object>> instantsList = new ArrayList<HashMap<String, Object>>();
		for(Instant instant : this.instants()) {
			instantsList.add(instant.toHashMap());
		}
		params.put("instants", instantsList);
		
		HashMap<String, Object> cover = new HashMap<String, Object>();
		cover.put("route", params);
		return cover;
	}
	
	public String toJSON(HashMap<String, Object> object) {
		HashMap<String, Object> routeEnvelope = toHashMap();
		routeEnvelope.put("extras", object);
		return JSONValue.toJSONString(routeEnvelope);
	}
	
	public String toJSON() {
		return JSONValue.toJSONString(toHashMap());
	}
	
	public static Route findById(Long id) {
		if(id == null)
			return null;
		return (Route) new Select().from(Route.class).where("Id = ?", id).execute().get(0);
	}
	
	public static ArrayList<Route> queued() {
		return new Select().from(Route.class).where("IsDraft IS TRUE").execute();
	}
	
	public static ArrayList<Route> uploaded() {
		return new Select().from(Route.class).where("IsDraft IS FALSE").execute();
	} 
	
	public Location getStartingLocation() {
		return this.instants().get(0).location();
	}
	
	public Location getEndingLocation() {
		return this.instants().get(instants().size()-1).location();
	}
	
	public String shortName() {
		if(this.name.length() > 25) {
			return this.name.substring(0, 12).concat("...");
		}
		return this.name;
	}
	
	@Override
	public void delete() {
		super.delete();
		
		for(Instant instant : this.instants()) {
			instant.delete();
		}
	}
	
	protected static void commitLocally(Route route) {
		ActiveAndroid.beginTransaction();

		route.save();
		for (Instant instant : route.instants()) {
			instant.route = route;
			instant.save();
		}
		
		ActiveAndroid.setTransactionSuccessful();
		ActiveAndroid.endTransaction();
	}

	public static Route buildFrom(JSONObject object) {
		long id = (Long) object.get("id");
		String routeName = (String) object.get("name");
		String routeDetails = (String) object.get("details");
		float kilometers = Float.parseFloat((String) object.get("kilometers"));
		
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
		
		JSONObject owner = (JSONObject) object.get("owner");
		long userId = (Long) owner.get("id");
		String name = (String) owner.get("username");
				
		long likesCountTmp = (Long) object.get("likes_count");
		int likesCount = (int) likesCountTmp;
		
		long dislikesCountTmp = (Long) object.get("dislikes_count");
		int dislikesCount = (int) dislikesCountTmp;
		
		long speedTmp = (Long) object.get("speed_index");
		int speed = (int) speedTmp;
		
		long safetyTmp = (Long) object.get("safety_index");
		int safety = (int) safetyTmp;
		
		long comfortTmp = (Long) object.get("comfort_index");
		int comfort = (int) comfortTmp;
		
		Route route = new Route(id, routeName, likesCount, dislikesCount, speed, safety, comfort, routeDetails, kilometers, createdAt, updatedAt, userId, name);
		route.originCoordinate = new LatLng((Double) object.get("origin_lat"), (Double) object.get("origin_lon"));
		route.endCoordinate = new LatLng((Double) object.get("end_lat"), (Double) object.get("end_lon"));
		
		if(owner.containsKey("pic"))
			route.userPicURL = (String) owner.get("pic");
		
		return route;
	}

	public boolean isOwnedByCurrentUser() {
		return User.id() == this.userId;
	}
	
	public boolean hasPic() {
		return !this.userPicURL.isEmpty();
	}
	
	@Override
	public LatLng getLatLng() {
		return this.originCoordinate;
	}

	@Override
	public int getDrawable() {
		return R.drawable.start_flag_marker;
	}

	@Override
	public Marker getAssociatedMarker() {
		return associatedMarker;
	}

	@Override
	public void setMarker(Marker marker) {
		associatedMarker = marker;		
	}

	public void updateAttributes(String routeName, String routeDetails, boolean isPublic) {
		this.name = routeName;
		this.details = routeDetails;
		this.isPublic = isPublic;
		this.updatedAt = new Date().getTime();
	}

	public String toJSONForPut(HashMap<String, Object> object) {
		HashMap<String, Object> routeEnvelope = toPutHashMap();
		routeEnvelope.put("extras", object);
		return JSONValue.toJSONString(routeEnvelope);
	}
	
	public boolean hasNoPathLoaded() {
		return (this.path == null);
	}

	@Override
	public long getRemoteId() {
		return remoteId;
	}

	@Override
	public String getKind() {
		return "Route";
	}

	public void updateWith(RouteRanking lastRouteRanking) {
		if(comfortIndex != 0)
			comfortIndex=Math.round((comfortIndex+lastRouteRanking.comfortIndex)/2);
		else
			comfortIndex=lastRouteRanking.comfortIndex;
		
		if(safetyIndex != 0)
			safetyIndex=Math.round((safetyIndex+lastRouteRanking.safetyIndex)/2);
		else
			safetyIndex=lastRouteRanking.safetyIndex;

		if(speedIndex != 0)
			speedIndex=Math.round((speedIndex+lastRouteRanking.speedIndex)/2);
		else
			speedIndex=lastRouteRanking.speedIndex;
	}
}
