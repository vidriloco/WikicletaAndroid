package org.wikicleta.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.apache.commons.lang3.StringEscapeUtils;
import org.wikicleta.helpers.DouglasPeuckerLineSimplifier;
import org.wikicleta.helpers.GeoHelpers;
import android.location.Location;
import android.util.Log;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.google.android.gms.maps.model.LatLng;
import com.vividsolutions.jts.geom.Coordinate;

@Table(name = "Routes")
public class Route extends Model {
	
	@Column(name = "Name")
	public String name;
	
	@Column(name = "Tags")
	public String tags;
	
	@Column(name = "ElapsedTime")
	public long elapsedTime;
	
	@Column(name = "AverageSpeed")
	public float averageSpeed;
	
	@Column(name = "Kilometers")
	public float kilometers;

	@Column(name = "isPublic")
	public boolean isPublic;
	
	@Column(name = "Owner")
	public int owner;
	
	@Column(name = "CheckIns")
	public int checkIns;
	
	@Column(name = "Ranking")
	public int ranking;

	@Column(name = "Json")
	public String jsonRepresentation;
	
	@Column(name = "CreatedAt")
	public long createdAt;
	
	public boolean isBlocked;
	public ArrayList<Instant> temporalInstants;
	
	public ArrayList<Instant> instants() {
		if(temporalInstants == null)
			return getMany(Instant.class, "Route");
		return temporalInstants;
	}
	
	public Route() {
		
	}
	
	public Route(String name, String tags, long elapsedTime, float averageSpeed, float kilometers, long createdAt) {
		this(elapsedTime, averageSpeed, kilometers, createdAt);
		this.name = name;
		this.tags = tags;
	}
	
	public Route(long elapsedTime, float averageSpeed, float kilometers, long createdAt) {
		this.elapsedTime = elapsedTime;
		this.averageSpeed = averageSpeed;
		this.kilometers = kilometers;
		this.createdAt = createdAt;
	}
	
	public HashMap<String, Object> toHashMap() {
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("name", this.name);
		params.put("tags", this.tags);
		params.put("isPublic", this.isPublic);
		params.put("userId", this.owner);
		params.put("elapsedTime", this.elapsedTime);
		params.put("averageSpeed", this.averageSpeed);
		params.put("kilometers", this.kilometers);
		params.put("createdAt", this.createdAt);
		
		return params;
	}
	
	public void setJsonRepresentation(String representation) {
		jsonRepresentation = StringEscapeUtils.escapeJava(representation);
	}
	
	public String getJsonRepresentation(String representation) {
		return StringEscapeUtils.unescapeJava(jsonRepresentation);
	}
	
	public boolean isDraft() {
		if(this.jsonRepresentation == null)
			return false;
		return !(this.jsonRepresentation.length() == 0);
	}
	
	public static Route findById(Long id) {
		if(id == null)
			return null;
		return (Route) new Select().from(Route.class).where("Id = ?", id).execute().get(0);
	}
	
	public static ArrayList<Route> queued() {
		return new Select().from(Route.class).where("Json IS NOT NULL").execute();
	}
	
	public static ArrayList<Route> uploaded() {
		return new Select().from(Route.class).where("Json IS NULL").execute();
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
	
	public void attemptSimplification() {
		Log.e("WIKICLETA", "Will attempt to: " + instants().size());
		
		Coordinate [] newPoints = this.coordinates();
		
    	Coordinate[] pointsSimplified = DouglasPeuckerLineSimplifier.simplify(newPoints, 100);
    	Log.e("WIKICLETA", "Simplified to: " + pointsSimplified.length);
	}
	
	
	protected Coordinate[] coordinates() {
		Coordinate[] coordinates = new Coordinate[instants().size()];
		int idx = 0;
    	for (Instant instant : instants()) {
        	
        	LatLng geoPoint1 = instant.geoPoint();
        	Coordinate coord = new Coordinate();
        	coord.x = geoPoint1.latitude;
        	coord.y = geoPoint1.longitude;
        	
        	coordinates[idx] = coord;
        	idx++;
    	}
    	Log.e("WIKICLETA", "built simplification points");

    	return coordinates;
    }
	
	public static void build() {
		long date = new Date().getTime();
		for(Route route : Route.queued()) {
			route.delete();
		}
		
		Route route = new Route("Chapultepec - Roma", "Por reforma, Sevilla, centro", 4000, 15.4f, 4.6f, date);
		ArrayList<Instant> temps = new ArrayList<Instant>();
		temps.add(new Instant(GeoHelpers.buildGeoPointFromLatLon(19.424463, -99.184570), 5.4f, 10000));
		temps.add(new Instant(GeoHelpers.buildGeoPointFromLatLon(19.423362, -99.175601), 10.4f, 20000));
		temps.add(new Instant(GeoHelpers.buildGeoPointFromLatLon(19.424819, -99.171653), 16.4f, 35000));
		temps.add(new Instant(GeoHelpers.buildGeoPointFromLatLon(19.422027, -99.170151), 6.4f, 50000));
		temps.add(new Instant(GeoHelpers.buildGeoPointFromLatLon(19.420165, -99.169807), 12.4f, 80000));
		temps.add(new Instant(GeoHelpers.buildGeoPointFromLatLon(19.416927, -99.168992), 1.4f, 90000));
		temps.add(new Instant(GeoHelpers.buildGeoPointFromLatLon(19.416441, -99.168863), 10.4f, 98000));
		temps.add(new Instant(GeoHelpers.buildGeoPointFromLatLon(19.418060, -99.161139), 3.4f, 110000));
		temps.add(new Instant(GeoHelpers.buildGeoPointFromLatLon(19.415713, -99.160538), 8.4f, 120000));
		
		route.temporalInstants = temps;
		Log.e("WIKICLETA", String.valueOf(route.instants().size()));
		route.jsonRepresentation = "No es nulo";
		commitLocally(route);
		
		route = new Route("S‡n Angel - Roma", "Por Insurgentes, killer", 8000, 13.4f, 7.6f, date);
		temps = new ArrayList<Instant>();
		temps.add(new Instant(GeoHelpers.buildGeoPointFromLatLon(19.346324, -99.189978), 5.4f, 40030));
		temps.add(new Instant(GeoHelpers.buildGeoPointFromLatLon(19.347377, -99.187489), 10.4f, 50000));
		temps.add(new Instant(GeoHelpers.buildGeoPointFromLatLon(19.362439, -99.182596), 16.4f, 70000));
		temps.add(new Instant(GeoHelpers.buildGeoPointFromLatLon(19.375800, -99.178348), 6.4f, 89000));
		temps.add(new Instant(GeoHelpers.buildGeoPointFromLatLon(19.378876, -99.177232), 12.4f, 99000));
		temps.add(new Instant(GeoHelpers.buildGeoPointFromLatLon(19.386123, -99.175043), 1.4f, 120000));
		temps.add(new Instant(GeoHelpers.buildGeoPointFromLatLon(19.396283, -99.171653), 10.4f, 130000));
		temps.add(new Instant(GeoHelpers.buildGeoPointFromLatLon(19.403570, -99.169250), 3.4f, 160000));
		temps.add(new Instant(GeoHelpers.buildGeoPointFromLatLon(19.412839, -99.166331), 8.4f, 180000));
		temps.add(new Instant(GeoHelpers.buildGeoPointFromLatLon(19.413810, -99.161525), 10.4f, 200000));

		route.temporalInstants = temps;
		Log.e("WIKICLETA", String.valueOf(route.instants().size()));
		route.jsonRepresentation = "No es nulo";
		commitLocally(route);
		
		route = new Route("Marina - Colonia Roma", "Por La Viga, Taxque–a, parques, tranquila, puente", 8000, 13.4f, 7.6f, date);
		temps = new ArrayList<Instant>();
		temps.add(new Instant(GeoHelpers.buildGeoPointFromLatLon(19.321895, -99.125004), 5.4f, 40030));
		temps.add(new Instant(GeoHelpers.buildGeoPointFromLatLon(19.329265, -99.124317), 10.4f, 50000));
		temps.add(new Instant(GeoHelpers.buildGeoPointFromLatLon(19.337769, -99.122686), 16.4f, 70000));
		temps.add(new Instant(GeoHelpers.buildGeoPointFromLatLon(19.338023, -99.124231), 6.4f, 89000));
		temps.add(new Instant(GeoHelpers.buildGeoPointFromLatLon(19.338711, -99.127192), 12.4f, 99000));
		temps.add(new Instant(GeoHelpers.buildGeoPointFromLatLon(19.340533, -99.133458), 1.4f, 120000));
		temps.add(new Instant(GeoHelpers.buildGeoPointFromLatLon(19.341222, -99.137535), 10.4f, 1300000));
		temps.add(new Instant(GeoHelpers.buildGeoPointFromLatLon(19.344137, -99.137321), 3.4f, 1600000));
		temps.add(new Instant(GeoHelpers.buildGeoPointFromLatLon(19.348268, -99.130411), 8.4f, 1800000));
		temps.add(new Instant(GeoHelpers.buildGeoPointFromLatLon(19.360293, -99.130025), 10.4f, 2000000));

		temps.add(new Instant(GeoHelpers.buildGeoPointFromLatLon(19.366285, -99.129896), 25.4f, 2400000));
		temps.add(new Instant(GeoHelpers.buildGeoPointFromLatLon(19.367257, -99.137750), 12.4f, 2800000));
		temps.add(new Instant(GeoHelpers.buildGeoPointFromLatLon(19.370374, -99.156847), 9.4f, 3200000));
		temps.add(new Instant(GeoHelpers.buildGeoPointFromLatLon(19.378876, -99.159164), 20.6f, 3400000));
		temps.add(new Instant(GeoHelpers.buildGeoPointFromLatLon(19.386163, -99.161224), 2.6f, 3800000));
		temps.add(new Instant(GeoHelpers.buildGeoPointFromLatLon(19.389645, -99.163413), 9.6f, 4000000));
		temps.add(new Instant(GeoHelpers.buildGeoPointFromLatLon(19.396486, -99.161739), 23.6f, 4500000));
		temps.add(new Instant(GeoHelpers.buildGeoPointFromLatLon(19.401384, -99.161096), 10.6f, 4600000));
		temps.add(new Instant(GeoHelpers.buildGeoPointFromLatLon(19.401586, -99.159851), 5.6f, 4800000));
		temps.add(new Instant(GeoHelpers.buildGeoPointFromLatLon(19.415105, -99.163499), 2.6f, 5000000));

		route.temporalInstants = temps;
		Log.e("WIKICLETA", String.valueOf(route.instants().size()));
		route.jsonRepresentation = "No es nulo";
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
