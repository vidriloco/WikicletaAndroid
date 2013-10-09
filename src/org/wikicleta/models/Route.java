package org.wikicleta.models;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import org.json.simple.JSONValue;
import org.wikicleta.helpers.GeoHelpers;
import android.location.Location;
import android.util.Log;
import com.activeandroid.ActiveAndroid;
import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

@Table(name = "Routes")
public class Route extends Model {
	
	@Column(name = "RemoteId")
	public long remoteId;
	
	@Column(name = "Name")
	public String name;
	
	@Column(name = "Details")
	public String details;
	
	@Column(name = "ElapsedTime")
	public long elapsedTime;
	
	@Column(name = "AverageSpeed")
	public float averageSpeed;
	
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
	
	public int comfortIndex;
	public int speedIndex;
	public int safetyIndex;
	
	public ArrayList<Instant> temporalInstants;
	
	public ArrayList<Instant> instants() {
		if(temporalInstants == null)
			return getMany(Instant.class, "Route");
		return temporalInstants;
	}

	public Route(String name, String details, long elapsedTime, float averageSpeed, float kilometers, long createdAt, ArrayList<Instant> coordinateVector, Long userId) {
		this(elapsedTime, averageSpeed, kilometers, createdAt);
		this.name = name;
		this.details = details;
		this.temporalInstants = coordinateVector;
		this.userId = userId;
	}
	
	public Route(long elapsedTime, float averageSpeed, float kilometers, long createdAt) {
		this.elapsedTime = elapsedTime;
		this.averageSpeed = averageSpeed;
		this.kilometers = kilometers;
		this.createdAt = createdAt;
		this.updatedAt = createdAt;
	}
	
	public boolean existsOnRemoteServer() {
		return this.remoteId != 0;
	}
	
	public HashMap<String, Object> toHashMap() {
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("name", this.name);
		params.put("details", this.details);
		params.put("is_public", this.isPublic);
		params.put("user_id", this.userId);
		params.put("elapsed_time", this.elapsedTime);
		params.put("average_speed", this.averageSpeed);
		params.put("kilometers", this.kilometers);
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
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
		HashMap<String, Object> tipEnvelope = toHashMap();
		tipEnvelope.put("extras", object);
		return JSONValue.toJSONString(tipEnvelope);
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
	
	@Override
	public void delete() {
		super.delete();
		
		for(Instant instant : this.instants()) {
			instant.delete();
		}
	}
	
	public static void build() {
		long date = new Date().getTime();
		for(Route route : Route.queued()) {
			route.delete();
		}
		
		Route route = new Route("Chapultepec - Roma", "Por reforma, Sevilla, centro", 4000, 15.4f, 4.6f, date, null, null);
		ArrayList<Instant> temps = new ArrayList<Instant>();
		temps.add(new Instant(GeoHelpers.buildLocationFromLatLon(19.424463, -99.184570), 5.4f, 10000));
		temps.add(new Instant(GeoHelpers.buildLocationFromLatLon(19.423362, -99.175601), 10.4f, 20000));
		temps.add(new Instant(GeoHelpers.buildLocationFromLatLon(19.424819, -99.171653), 16.4f, 35000));
		temps.add(new Instant(GeoHelpers.buildLocationFromLatLon(19.422027, -99.170151), 6.4f, 50000));
		temps.add(new Instant(GeoHelpers.buildLocationFromLatLon(19.420165, -99.169807), 12.4f, 80000));
		temps.add(new Instant(GeoHelpers.buildLocationFromLatLon(19.416927, -99.168992), 1.4f, 90000));
		temps.add(new Instant(GeoHelpers.buildLocationFromLatLon(19.416441, -99.168863), 10.4f, 98000));
		temps.add(new Instant(GeoHelpers.buildLocationFromLatLon(19.418060, -99.161139), 3.4f, 110000));
		temps.add(new Instant(GeoHelpers.buildLocationFromLatLon(19.415713, -99.160538), 8.4f, 120000));
		
		route.temporalInstants = temps;
		Log.e("WIKICLETA", String.valueOf(route.instants().size()));
		route.isDraft = false;
		commitLocally(route);
		
		route = new Route("S‡n Angel - Roma", "Por Insurgentes, killer", 8000, 13.4f, 7.6f, date, null, null);
		temps = new ArrayList<Instant>();
		temps.add(new Instant(GeoHelpers.buildLocationFromLatLon(19.346324, -99.189978), 5.4f, 40030));
		temps.add(new Instant(GeoHelpers.buildLocationFromLatLon(19.347377, -99.187489), 10.4f, 50000));
		temps.add(new Instant(GeoHelpers.buildLocationFromLatLon(19.362439, -99.182596), 16.4f, 70000));
		temps.add(new Instant(GeoHelpers.buildLocationFromLatLon(19.375800, -99.178348), 6.4f, 89000));
		temps.add(new Instant(GeoHelpers.buildLocationFromLatLon(19.378876, -99.177232), 12.4f, 99000));
		temps.add(new Instant(GeoHelpers.buildLocationFromLatLon(19.386123, -99.175043), 1.4f, 120000));
		temps.add(new Instant(GeoHelpers.buildLocationFromLatLon(19.396283, -99.171653), 10.4f, 130000));
		temps.add(new Instant(GeoHelpers.buildLocationFromLatLon(19.403570, -99.169250), 3.4f, 160000));
		temps.add(new Instant(GeoHelpers.buildLocationFromLatLon(19.412839, -99.166331), 8.4f, 180000));
		temps.add(new Instant(GeoHelpers.buildLocationFromLatLon(19.413810, -99.161525), 10.4f, 200000));

		route.temporalInstants = temps;
		Log.e("WIKICLETA", String.valueOf(route.instants().size()));
		route.isDraft = false;
		commitLocally(route);
		
		route = new Route("Marina - Colonia Roma", "Por La Viga, Taxque–a, parques, tranquila, puente", 8000, 13.4f, 7.6f, date, null, null);
		temps = new ArrayList<Instant>();
		temps.add(new Instant(GeoHelpers.buildLocationFromLatLon(19.321895, -99.125004), 5.4f, 40030));
		temps.add(new Instant(GeoHelpers.buildLocationFromLatLon(19.329265, -99.124317), 10.4f, 50000));
		temps.add(new Instant(GeoHelpers.buildLocationFromLatLon(19.337769, -99.122686), 16.4f, 70000));
		temps.add(new Instant(GeoHelpers.buildLocationFromLatLon(19.338023, -99.124231), 6.4f, 89000));
		temps.add(new Instant(GeoHelpers.buildLocationFromLatLon(19.338711, -99.127192), 12.4f, 99000));
		temps.add(new Instant(GeoHelpers.buildLocationFromLatLon(19.340533, -99.133458), 1.4f, 120000));
		temps.add(new Instant(GeoHelpers.buildLocationFromLatLon(19.341222, -99.137535), 10.4f, 1300000));
		temps.add(new Instant(GeoHelpers.buildLocationFromLatLon(19.344137, -99.137321), 3.4f, 1600000));
		temps.add(new Instant(GeoHelpers.buildLocationFromLatLon(19.348268, -99.130411), 8.4f, 1800000));
		temps.add(new Instant(GeoHelpers.buildLocationFromLatLon(19.360293, -99.130025), 10.4f, 2000000));

		temps.add(new Instant(GeoHelpers.buildLocationFromLatLon(19.366285, -99.129896), 25.4f, 2400000));
		temps.add(new Instant(GeoHelpers.buildLocationFromLatLon(19.367257, -99.137750), 12.4f, 2800000));
		temps.add(new Instant(GeoHelpers.buildLocationFromLatLon(19.370374, -99.156847), 9.4f, 3200000));
		temps.add(new Instant(GeoHelpers.buildLocationFromLatLon(19.378876, -99.159164), 20.6f, 3400000));
		temps.add(new Instant(GeoHelpers.buildLocationFromLatLon(19.386163, -99.161224), 2.6f, 3800000));
		temps.add(new Instant(GeoHelpers.buildLocationFromLatLon(19.389645, -99.163413), 9.6f, 4000000));
		temps.add(new Instant(GeoHelpers.buildLocationFromLatLon(19.396486, -99.161739), 23.6f, 4500000));
		temps.add(new Instant(GeoHelpers.buildLocationFromLatLon(19.401384, -99.161096), 10.6f, 4600000));
		temps.add(new Instant(GeoHelpers.buildLocationFromLatLon(19.401586, -99.159851), 5.6f, 4800000));
		temps.add(new Instant(GeoHelpers.buildLocationFromLatLon(19.415105, -99.163499), 2.6f, 5000000));

		route.temporalInstants = temps;
		Log.e("WIKICLETA", String.valueOf(route.instants().size()));
		route.isDraft = false;
		commitLocally(route);
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
	
}
