package org.wikicleta.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import com.activeandroid.query.Select;

public class Draft {
	
	public DraftModel associatedModel;
	
	public Draft(DraftModel model) {
		this.associatedModel = model;
	}
	
	public Date getDate() {
		return this.associatedModel.getDate();
	}
	
	public String getCategory() {
		return this.associatedModel.getCategoryName();
	}
	
	public String getDetails() {
		return this.associatedModel.getContent();
	}
	
	public static LinkedList<Draft> fetchDrafts() {
		ArrayList<Tip> tips = new Select().from(Tip.class).where("RemoteId = ?", 0).execute();
		ArrayList<Parking> parkings = new Select().from(Parking.class).where("RemoteId = ?", 0).execute();
		ArrayList<Workshop> workshops = new Select().from(Workshop.class).where("RemoteId = ?", 0).execute();
		
		LinkedList<Draft> drafts = new LinkedList<Draft>();
		
		for(Tip tip : tips) {
			drafts.add(new Draft(tip));
		}
		for(Parking parking : parkings) {
			drafts.add(new Draft(parking));
		}
		
		for(Workshop workshop : workshops) {
			drafts.add(new Draft(workshop));
		}
		
		return drafts;
	}
}
