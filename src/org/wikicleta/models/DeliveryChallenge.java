package org.wikicleta.models;

import org.wikicleta.R;
import org.wikicleta.interfaces.MarkerInterface;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

public class DeliveryChallenge extends Model implements MarkerInterface {

	public enum DeliveryType {FOOD, PACKAGE};
	
	@Column(name = "Type")
	public DeliveryType type;
	
	@Column(name = "Details")
	public String details;
	
	@Column(name = "Value")
	public float value;
	
	@Column(name = "LatitudePickup")
	public double latitudePickup;
	
	@Column(name = "LongitudePickup")
	public double longitudePickup;

	@Column(name = "LatitudeDropOff")
	public double latitudeDropOff;
	
	@Column(name = "LongitudeDropOff")
	public double longitudeDropOff;
	
	@Column(name = "CreatedAt")
	public long createdAt;
	
	@Column(name = "Consideraciones")
	public String consideraciones;
	
	protected Marker marker;
	
	public DeliveryChallenge() {
		
	}
	
	public DeliveryChallenge(DeliveryType type,  long createdAt, String details, double latPick, double lonPick, double latDrop, double lonDrop, String consideraciones) {
		this.type = type;
		this.details = details;
		this.latitudeDropOff = latDrop;
		this.longitudeDropOff = lonDrop;
		this.latitudePickup = latPick;
		this.longitudePickup = lonPick;
		this.consideraciones = consideraciones;
	}
	
	@Override
	public LatLng getLatLng() {
		return new LatLng(latitudePickup, longitudePickup);
	}

	@Override
	public int getDrawable() {
		if(type == DeliveryType.FOOD)
			return R.drawable.delivery_food_marker;
		else if(type == DeliveryType.PACKAGE)
			return R.drawable.delivery_package_marker;
		else
			return 0;
	}

	@Override
	public Marker getAssociatedMarker() {
		return marker;
	}

	@Override
	public void setMarker(Marker marker) {
		this.marker = marker;
	}
	
	public int getDeliveryStringResource() {
		if(type == DeliveryType.FOOD)
			return R.string.delivery_food;
		else 
			return R.string.delivery_package;
	}

}
