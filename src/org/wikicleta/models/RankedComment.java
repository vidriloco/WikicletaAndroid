package org.wikicleta.models;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import org.interfaces.RemoteModelInterface;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class RankedComment implements RemoteModelInterface {

	public String associatedModelKind;
	public long associatedModelId;
	public String comment;
	public boolean positive;
	public Date createdAt;
	
	public long remoteId;
	
	public long userId;
	public String username;
	public String userPicURL;
	
	public RankedComment(String associatedModelKind, long associatedModelId, String comment, boolean positive) {
		this.associatedModelId = associatedModelId;
		this.associatedModelKind = associatedModelKind;
		this.comment = comment;
		this.positive = positive;
	}
	
	public RankedComment(long remoteId, String content, boolean positive, Date creationDate,
			long userId, String username) {
		this.comment = content;
		this.positive = positive;
		this.createdAt = creationDate;
		this.userId = userId;
		this.username = username;
		this.remoteId = remoteId;
	}

	public RankedComment(long remoteId, String content, boolean positive, Date creationDate,
			long userId, String username, String userPicURL) {
		this(remoteId, content, positive, creationDate, userId, username);
		this.userPicURL = userPicURL;
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

	public static RankedComment buildFrom(JSONObject json) {
		long remoteId = (Long) json.get("id");
		String content = (String) json.get("content");
		boolean positive = (Boolean) json.get("positive");
		
		JSONObject owner = (JSONObject) json.get("owner");
		long userId = (Long) owner.get("id");
		String userName = (String) owner.get("username");
	    
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date creationDate = null;
		try {
			creationDate = df.parse((String)  json.get("str_created_at"));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		if(owner.containsKey("pic"))
			return new RankedComment(remoteId, content, positive, creationDate, userId, userName, (String) owner.get("pic"));
		else
			return new RankedComment(remoteId, content, positive, creationDate, userId, userName);
	}
	
	public boolean hasPic() {
		return !this.userPicURL.isEmpty();
	}
	
	public boolean isOwnedByCurrentUser() {
		return User.id() == this.userId;
	}

	@Override
	public long getRemoteId() {
		return remoteId;
	}

	@Override
	public String getKind() {
		return "RankedComment";
	}
}
