package org.wikicleta.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table(name = "RoutePerformances")
public class RoutePerformance extends Model {
	
	@Column(name = "ElapsedTime")
	public long elapsedTime;
	
	@Column(name = "AverageSpeed")
	public float averageSpeed;
	
	public RoutePerformance(long elapsedTime, float averageSpeed) {
		this.elapsedTime = elapsedTime;
		this.averageSpeed = averageSpeed;
	}
	
}
