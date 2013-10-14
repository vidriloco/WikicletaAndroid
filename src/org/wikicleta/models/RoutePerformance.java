package org.wikicleta.models;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table(name = "RoutePerformances")
public class RoutePerformance extends Model {
	
	@Column(name = "ElapsedTime")
	public long elapsedTime;
	
	@Column(name = "AverageSpeed")
	public double averageSpeed;
	
	@Column(name = "CreatedAt")
	public Date createdAt;
	
	public String username;
	public String picURL;
	public ArrayList<Instant> instants;
	
	public RoutePerformance(long elapsedTime, double averageSpeed, String username, String picURL, Date createdAt) {
		this(elapsedTime, averageSpeed);
		this.username = username;
		this.picURL = picURL;
		this.instants = new ArrayList<Instant>();
		this.createdAt = createdAt;
	}

	public RoutePerformance(long elapsedTime, double averageSpeed) {
		this.elapsedTime = elapsedTime;
		this.averageSpeed = averageSpeed;
	}

	public static RoutePerformance buildFrom(JSONObject object) {
		long elapsedTime = (Long) object.get("elapsed_time");
		double speedAverage = Double.parseDouble( (String) object.get("speed_average"));
		
		JSONObject owner = (JSONObject) object.get("owner");
		String username = "";
		String picURL = "";
		if(owner!=null) {
			username = (String) owner.get("username");
			picURL = (String) owner.get("pic");
		}
		
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date creationDate = null;
		try {
			creationDate = df.parse((String)  object.get("str_created_at"));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		JSONArray instantsTopTen = (JSONArray) object.get("top_ten_instants");
		@SuppressWarnings("unchecked")
		Iterator<JSONObject> instantIterator = (Iterator<JSONObject>) instantsTopTen.iterator();
		
		RoutePerformance route = new RoutePerformance(elapsedTime, speedAverage, username, picURL, creationDate);
		while(instantIterator.hasNext()) {
			route.instants.add(Instant.buildFrom(instantIterator.next()));
		}
		
		return route;
	}
	
	public boolean hasPic() {
		if(this.picURL == null)
			return false;
		return !this.picURL.isEmpty();
	}
	
}
