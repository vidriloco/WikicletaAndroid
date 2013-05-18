package org.wikicleta.models;

import java.util.Date;

public interface DraftModel {
	public String getContent();
	public String getCategoryName();
	public Date getDate();
	public Long getId();
	public void delete();
	public boolean requiresCategoryTranslation();
}
