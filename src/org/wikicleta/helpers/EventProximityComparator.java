package org.wikicleta.helpers;

import java.util.Comparator;

import org.interfaces.EventInterface;

public class EventProximityComparator implements Comparator<EventInterface> {
	@Override
    public int compare(EventInterface o1, EventInterface o2) {
		return Integer.valueOf(o1.daysAway()).compareTo(o2.daysAway());
    }
}
