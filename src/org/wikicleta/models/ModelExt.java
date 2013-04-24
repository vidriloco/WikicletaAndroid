package org.wikicleta.models;

import org.apache.commons.lang3.StringEscapeUtils;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;

public class ModelExt extends Model {

	@Column(name = "Json")
	public String jsonRepresentation;
	
	@Column(name = "CreatedAt")
	public long createdAt;
	
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
	
}
