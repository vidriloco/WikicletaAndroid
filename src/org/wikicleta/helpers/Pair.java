package org.wikicleta.helpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.wikicleta.models.Instant;
import org.wikicleta.models.Route;


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

