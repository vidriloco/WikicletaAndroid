package org.wikicleta.models;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.interfaces.MarkerInterface;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.wikicleta.R;
import org.wikicleta.helpers.GeoHelpers;
import android.location.Location;
import android.util.Log;
import com.activeandroid.ActiveAndroid;
import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

@Table(name = "Routes")
public class Route extends Model implements MarkerInterface, Serializable {

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
	
	public int comfortIndex;
	public int speedIndex;
	public int safetyIndex;
	
	public ArrayList<Instant> temporalInstants;
	public ArrayList<RoutePerformance> temporalRoutePerformances;

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
	public Route(long id, String name, String details, float kilometers, long createdAt, long updatedAt, Long userId, String username) {
		this.name = name;
		this.details = details;
		this.kilometers = kilometers;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
		this.remoteId = id;
		this.userId = userId;
		this.username = username;
		this.userPicURL = new String();
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
	
	public HashMap<String, Object> toHashMap() {
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("name", this.name);
		params.put("details", this.details);
		params.put("is_public", this.isPublic);

		params.put("kilometers", this.kilometers);
		
		HashMap<String, Object> performance = new HashMap<String, Object>();
		performance.put("elapsed_time", performanceTmp.elapsedTime);
		performance.put("average_speed", performanceTmp.averageSpeed);
		params.put("route_performance", performance);
		
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
	
	public static Route buildOne() {
		long date = new Date().getTime();

		ArrayList<Instant> temps = new ArrayList<Instant>();
		temps.add(new Instant(GeoHelpers.buildLocationFromLatLon(19.320745808707706,-99.08743143081665), 5.4f, 10000));
		temps.add(new Instant(GeoHelpers.buildLocationFromLatLon(19.320097832955195,-99.08764600753784), 10.4f, 20000));
		temps.add(new Instant(GeoHelpers.buildLocationFromLatLon(19.319672597471353,-99.08790349960327), 16.4f, 35000));
		temps.add(new Instant(GeoHelpers.buildLocationFromLatLon(19.319267610267627,-99.0880537033081), 6.4f, 50000));
		temps.add(new Instant(GeoHelpers.buildLocationFromLatLon(19.318882871494527,-99.08818244934082), 12.4f, 80000));
		temps.add(new Instant(GeoHelpers.buildLocationFromLatLon(19.318417133873243,-99.08833265304565), 1.4f, 90000));
		temps.add(new Instant(GeoHelpers.buildLocationFromLatLon(19.3179716444719,-99.08841848373413), 10.4f, 98000));
		temps.add(new Instant(GeoHelpers.buildLocationFromLatLon(19.31730340809322,-99.08859014511108), 3.4f, 110000));
		temps.add(new Instant(GeoHelpers.buildLocationFromLatLon(19.31659466955476,-99.08876180648804), 8.4f, 120000));
		
		temps.add(new Instant(GeoHelpers.buildLocationFromLatLon(19.31582517851933,-99.08884763717651), 8.4f, 120000));
		temps.add(new Instant(GeoHelpers.buildLocationFromLatLon(19.315116433571554,-99.08910512924194), 8.4f, 120000));
		temps.add(new Instant(GeoHelpers.buildLocationFromLatLon(19.314549435400906,-99.08923387527466), 8.4f, 120000));
		temps.add(new Instant(GeoHelpers.buildLocationFromLatLon(19.313759684671783,-99.08944845199585), 8.4f, 120000));
		temps.add(new Instant(GeoHelpers.buildLocationFromLatLon(19.310823398501597,-99.09024238586426), 8.4f, 120000));
		temps.add(new Instant(GeoHelpers.buildLocationFromLatLon(19.31011463187057,-99.09047842025757), 8.4f, 120000));
		temps.add(new Instant(GeoHelpers.buildLocationFromLatLon(19.309466613976625,-99.09060716629028), 8.4f, 120000));

		temps.add(new Instant(GeoHelpers.buildLocationFromLatLon(19.309000849529156,-99.090735912323), 8.4f, 120000));
		temps.add(new Instant(GeoHelpers.buildLocationFromLatLon(19.30962861869086,-99.09155130386353), 8.4f, 120000));
		temps.add(new Instant(GeoHelpers.buildLocationFromLatLon(19.310276635942802,-99.09200191497803), 8.4f, 120000));
		temps.add(new Instant(GeoHelpers.buildLocationFromLatLon(19.312666177363965,-99.09393310546875), 8.4f, 120000));
		temps.add(new Instant(GeoHelpers.buildLocationFromLatLon(19.31317243165644,-99.09436225891113), 8.4f, 120000));
		temps.add(new Instant(GeoHelpers.buildLocationFromLatLon(19.31422543556331,-99.09427642822266), 8.4f, 120000));
		temps.add(new Instant(GeoHelpers.buildLocationFromLatLon(19.315136683469863,-99.09408330917358), 8.4f, 120000));
		temps.add(new Instant(GeoHelpers.buildLocationFromLatLon(19.31550118121021,-99.09479141235352), 8.4f, 120000));
		temps.add(new Instant(GeoHelpers.buildLocationFromLatLon(19.31606817607975,-99.0953278541565), 8.4f, 120000));
		temps.add(new Instant(GeoHelpers.buildLocationFromLatLon(19.316938914371544,-99.09610033035278), 8.4f, 120000));
		temps.add(new Instant(GeoHelpers.buildLocationFromLatLon(19.317910895823097,-99.09605741500854), 8.4f, 120000));
		temps.add(new Instant(GeoHelpers.buildLocationFromLatLon(19.318396884381347,-99.09640073776245), 8.4f, 120000));

		temps.add(new Instant(GeoHelpers.buildLocationFromLatLon(19.318396884381347,-99.09693717956543), 8.4f, 120000));
		temps.add(new Instant(GeoHelpers.buildLocationFromLatLon(19.318558880246297,-99.09732341766357), 8.4f, 120000));
		temps.add(new Instant(GeoHelpers.buildLocationFromLatLon(19.318882871494527,-99.09755945205688), 8.4f, 120000));
		temps.add(new Instant(GeoHelpers.buildLocationFromLatLon(19.322345487845705,-99.09691572189331), 8.4f, 120000));
		temps.add(new Instant(GeoHelpers.buildLocationFromLatLon(19.322487230811536,-99.09749507904053), 8.4f, 120000));
		temps.add(new Instant(GeoHelpers.buildLocationFromLatLon(19.320563565786994,-99.09822463989258), 8.4f, 120000));

		temps.add(new Instant(GeoHelpers.buildLocationFromLatLon(19.320482568868112,-99.09886837005615), 8.4f, 120000));
		temps.add(new Instant(GeoHelpers.buildLocationFromLatLon(19.32046231963211,-99.09940481185913), 8.4f, 120000));
		temps.add(new Instant(GeoHelpers.buildLocationFromLatLon(19.32076605790857,-99.09966230392456), 8.4f, 120000));
		temps.add(new Instant(GeoHelpers.buildLocationFromLatLon(19.320907802244392,-99.1002631187439), 8.4f, 120000));
		temps.add(new Instant(GeoHelpers.buildLocationFromLatLon(19.320826805496125,-99.10062789916992), 8.4f, 120000));
		temps.add(new Instant(GeoHelpers.buildLocationFromLatLon(19.322588475711846,-99.10331010818481), 8.4f, 120000));
		temps.add(new Instant(GeoHelpers.buildLocationFromLatLon(19.320907802244392,-99.11187171936035), 8.4f, 120000));
		temps.add(new Instant(GeoHelpers.buildLocationFromLatLon(19.32078630710693,-99.11298751831055), 8.4f, 120000));
		temps.add(new Instant(GeoHelpers.buildLocationFromLatLon(19.32058381501044,-99.1138243675232), 8.4f, 120000));
		temps.add(new Instant(GeoHelpers.buildLocationFromLatLon(19.320300325653836,-99.11474704742432), 8.4f, 120000));
		temps.add(new Instant(GeoHelpers.buildLocationFromLatLon(19.320300325653836,-99.11545515060425), 8.4f, 120000));
		temps.add(new Instant(GeoHelpers.buildLocationFromLatLon(19.320300325653836,-99.11627054214478), 8.4f, 120000));

		temps.add(new Instant(GeoHelpers.buildLocationFromLatLon(19.32011808223635,-99.1169786453247), 8.4f, 120000));
		temps.add(new Instant(GeoHelpers.buildLocationFromLatLon(19.32003708509667,-99.11747217178345), 8.4f, 120000));
		temps.add(new Instant(GeoHelpers.buildLocationFromLatLon(19.319915589311886,-99.11818027496338), 8.4f, 120000));
		temps.add(new Instant(GeoHelpers.buildLocationFromLatLon(19.319875090696883,-99.11863088607788), 8.4f, 120000));
		temps.add(new Instant(GeoHelpers.buildLocationFromLatLon(19.319814342755574,-99.11908149719238), 8.4f, 120000));
		temps.add(new Instant(GeoHelpers.buildLocationFromLatLon(19.319672597471353,-99.11942481994629), 8.4f, 120000));
		temps.add(new Instant(GeoHelpers.buildLocationFromLatLon(19.31965234813501,-99.11989688873291), 8.4f, 120000));
		temps.add(new Instant(GeoHelpers.buildLocationFromLatLon(19.3196928468052,-99.12024021148682), 8.4f, 120000));
		temps.add(new Instant(GeoHelpers.buildLocationFromLatLon(19.319753594791674,-99.1207766532898), 8.4f, 120000));
		temps.add(new Instant(GeoHelpers.buildLocationFromLatLon(19.319915589311886,-99.1212272644043), 8.4f, 120000));
		temps.add(new Instant(GeoHelpers.buildLocationFromLatLon(19.319976337215564,-99.12172079086304), 8.4f, 120000));
		temps.add(new Instant(GeoHelpers.buildLocationFromLatLon(19.320158580791137,-99.12240743637085), 8.4f, 120000));

		temps.add(new Instant(GeoHelpers.buildLocationFromLatLon(19.320300325653836,-99.12290096282959), 8.4f, 120000));
		temps.add(new Instant(GeoHelpers.buildLocationFromLatLon(19.320604064231382,-99.12317991256714), 8.4f, 120000));
		temps.add(new Instant(GeoHelpers.buildLocationFromLatLon(19.320988798952506,-99.12378072738647), 8.4f, 120000));
		temps.add(new Instant(GeoHelpers.buildLocationFromLatLon(19.321353283642285,-99.12440299987793), 8.4f, 120000));
		
		temps.add(new Instant(GeoHelpers.buildLocationFromLatLon(19.321717767519104,-99.12478923797607), 8.4f, 120000));
		temps.add(new Instant(GeoHelpers.buildLocationFromLatLon(19.32141403101154,-99.12498235702515), 8.4f, 120000));
		temps.add(new Instant(GeoHelpers.buildLocationFromLatLon(19.32068506109005,-99.12485361099243), 8.4f, 120000));
		temps.add(new Instant(GeoHelpers.buildLocationFromLatLon(19.320239577870588,-99.12472486495972), 8.4f, 120000));

		temps.add(new Instant(GeoHelpers.buildLocationFromLatLon(19.319733345465355,-99.12463903427124), 8.4f, 120000));
		temps.add(new Instant(GeoHelpers.buildLocationFromLatLon(19.31928785965164,-99.12461757659912), 8.4f, 120000));
		temps.add(new Instant(GeoHelpers.buildLocationFromLatLon(19.318943619782083,-99.124596118927), 8.4f, 120000));
		temps.add(new Instant(GeoHelpers.buildLocationFromLatLon(19.318862622060337,-99.12483215332031), 8.4f, 120000));
		temps.add(new Instant(GeoHelpers.buildLocationFromLatLon(19.319004368047043,-99.12528276443481), 8.4f, 120000));
		temps.add(new Instant(GeoHelpers.buildLocationFromLatLon(19.319085365698527,-99.12543296813965), 8.4f, 120000));
		temps.add(new Instant(GeoHelpers.buildLocationFromLatLon(19.319227111492058,-99.12534713745117), 8.4f, 120000));
		temps.add(new Instant(GeoHelpers.buildLocationFromLatLon(19.319591600110893,-99.12569046020508), 8.4f, 120000));
		temps.add(new Instant(GeoHelpers.buildLocationFromLatLon(19.319227111492058,-99.12534713745117), 8.4f, 120000));
		temps.add(new Instant(GeoHelpers.buildLocationFromLatLon(19.3196928468052,-99.12603378295898), 8.4f, 120000));
		temps.add(new Instant(GeoHelpers.buildLocationFromLatLon(19.319976337215564,-99.12663459777832), 8.4f, 120000));
		temps.add(new Instant(GeoHelpers.buildLocationFromLatLon(19.320158580791137,-99.12714958190918), 8.4f, 120000));
		temps.add(new Instant(GeoHelpers.buildLocationFromLatLon(19.320300325653836,-99.12749290466309), 8.4f, 120000));
		return new Route("Tlahuac - La Marina", "Canal de Chalco, Lomas estrella, la virgen", 4000, 15.4f, 4.6f, date, temps, User.id(), false);
	}
	
	
	public static void build() {
		long date = new Date().getTime();
		for(Route route : Route.queued()) {
			route.delete();
		}
		
		Route route = new Route("Chapultepec - Roma", "Por reforma, Sevilla, centro", 4000, 15.4f, 4.6f, date, null, null, false);
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
		
		route = new Route("S‡n Angel - Roma", "Por Insurgentes, killer", 8000, 13.4f, 7.6f, date, null, null, false);
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
		
		route = new Route("Marina - Colonia Roma", "Por La Viga, Taxque–a, parques, tranquila, puente", 8000, 13.4f, 7.6f, date, null, null, false);
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
				
		Route route = new Route(id, routeName, routeDetails, kilometers, createdAt, updatedAt, userId, name);
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
	
}
