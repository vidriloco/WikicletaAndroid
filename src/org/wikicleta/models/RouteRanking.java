package org.wikicleta.models;

import java.util.HashMap;

import org.json.simple.JSONValue;

public class RouteRanking {

	public long routeId;
	public int comfortIndex;
	public int speedIndex;
	public int safetyIndex;
	
	public RouteRanking(int safetyIndex, int speedIndex, int comfortIndex) {
		this.comfortIndex = comfortIndex;
		this.speedIndex = speedIndex;
		this.safetyIndex = safetyIndex;
	}
	
	public RouteRanking(long routeId, int safetyIndex, int speedIndex, int comfortIndex) {
		this(safetyIndex, speedIndex, comfortIndex);
		this.routeId = routeId;
	}
	
	public HashMap<String, Object> toHashMap() {
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("route_id", this.routeId);
		params.put("comfort_index", this.comfortIndex);
		params.put("speed_index", this.speedIndex);
		params.put("safety_index", this.safetyIndex);
		
		HashMap<String, Object> cover = new HashMap<String, Object>();
		cover.put("route_ranking", params);
		return cover;
	}
	
	public String toJSON(HashMap<String, Object> object) {
		HashMap<String, Object> tipEnvelope = toHashMap();
		tipEnvelope.put("extras", object);
		return JSONValue.toJSONString(tipEnvelope);
	}
}
