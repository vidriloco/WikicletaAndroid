package org.wikicleta.models;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import org.json.simple.JSONValue;

public class RankedComment {

	public String associatedModelKind;
	public long associatedModelId;
	public String comment;
	public boolean positive;
	
	public RankedComment(String associatedModelKind, long associatedModelId, String comment, boolean positive) {
		this.associatedModelId = associatedModelId;
		this.associatedModelKind = associatedModelKind;
		this.comment = comment;
		this.positive = positive;
	}
	
	public HashMap<String, Object> toHashMap() {
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("ranked_comment_object_type", this.associatedModelKind);
		params.put("ranked_comment_object_id", this.associatedModelId);
		params.put("content", this.comment);
		params.put("positive", this.positive);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		params.put("created_at", sdf.format(new Date()));
		params.put("updated_at", sdf.format(new Date()));
		HashMap<String, Object> cover = new HashMap<String, Object>();
		cover.put("ranked_comment", params);
		
		return cover;
	}
	
	public String toJSON(HashMap<String, Object> object) {
		HashMap<String, Object> tipEnvelope = toHashMap();
		tipEnvelope.put("extras", object);
		return JSONValue.toJSONString(tipEnvelope);
	}
}
