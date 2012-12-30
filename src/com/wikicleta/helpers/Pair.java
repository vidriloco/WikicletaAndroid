package com.wikicleta.helpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.wikicleta.models.Instant;
import com.wikicleta.models.Route;

public class Pair<F, S> implements JSONStringMerger {
    public F first; 
    public S second; 

    public Pair(F first, S second) {
        this.first = first;
        this.second = second;
    }

    public void setFirst(F first) {
        this.first = first;
    }

    public void setSecond(S second) {
        this.second = second;
    }

    @SuppressWarnings("unchecked")
	public Map<String, Object> generateParams() {
    	Map<String, Object> params = null;
    	if(this.first.getClass() == Route.class && this.second.getClass() == ArrayList.class) {
    		Route route = (Route) this.first;
    		params = route.toHashMap();
    		
    		ArrayList<HashMap<String, Object>> instants = new ArrayList<HashMap<String, Object>>();
    		for(Instant instant : (ArrayList<Instant>) this.second) {
    			instants.add(instant.toHashMap());
    		}
    		
    		params.put("coordinates", instants);
    	}
    	
    	
    	return params;
    }

	@Override
	public void mergeJSONString(String jsonString) {
		if(this.first.getClass() == Route.class)
			((Route) this.first).setJsonRepresentation(jsonString);
	}

	@Override
	public void clearJSONString() {
		if(this.first.getClass() == Route.class)
			((Route) this.first).jsonRepresentation = "";
	}
}

