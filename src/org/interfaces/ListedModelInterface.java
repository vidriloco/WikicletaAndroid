package org.interfaces;

import java.util.Date;

public interface ListedModelInterface {

	public int getDrawable();
	// Normally this corresponds to the resource ID for looking up on the translations table
	public int getTitle();
	// This corresponds to the sub-category of the model ready to get it's resource ID generated
	public String getSubtitle();
	
	public String getDetails();
	
	public Date getDate();
	// This should have more precedence than the getTitle method in case it is not null
	public String getName();
	
	public String getKind();
	
	public Long getId();
}
