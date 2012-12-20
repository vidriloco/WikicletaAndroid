package com.wikicleta.drafts;

import org.kroz.activerecord.ActiveRecordBase;

public class RouteDraft extends ActiveRecordBase {
	
	public String name;
	public String tags;
	public long elapsedTime;
	public float averageSpeed;
	public float kilometers;
	
	public String lineString;
	
	
}
