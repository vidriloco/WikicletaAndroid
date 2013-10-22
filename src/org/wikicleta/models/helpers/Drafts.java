package org.wikicleta.models.helpers;

import java.util.ArrayList;
import java.util.LinkedList;
import org.interfaces.ListedModelInterface;
import org.wikicleta.models.Parking;
import org.wikicleta.models.Route;
import org.wikicleta.models.Tip;
import org.wikicleta.models.Workshop;

import android.util.Log;

import com.activeandroid.query.Select;

public class Drafts {
	
	public static LinkedList<ListedModelInterface> fetchDrafts() {
		ArrayList<Tip> tips = new Select().from(Tip.class).where("RemoteId = ?", 0).execute();
		ArrayList<Parking> parkings = new Select().from(Parking.class).where("RemoteId = ?", 0).execute();
		ArrayList<Workshop> workshops = new Select().from(Workshop.class).where("RemoteId = ?", 0).execute();
		ArrayList<Route> routes = new Select().from(Route.class).where("RemoteId = ?", 0).execute();
		Log.e("WIKICLETA", "RRR "+String.valueOf(routes.size()));
		LinkedList<ListedModelInterface> drafts = new LinkedList<ListedModelInterface>();
		
		for(Tip tip : tips) {
			drafts.add(tip);
		}
		for(Parking parking : parkings) {
			drafts.add(parking);
		}
		
		for(Workshop workshop : workshops) {
			drafts.add(workshop);
		}
		
		for(Route route : routes) {
			drafts.add(route);
		}
		
		return drafts;
	}
}
