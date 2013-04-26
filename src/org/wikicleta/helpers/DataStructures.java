package org.wikicleta.helpers;

import java.util.HashMap;
import java.util.Map.Entry;

public class DataStructures {
	public static <V, K> HashMap<V, K> invert(HashMap<K, V> map) {

	    HashMap<V, K> inv = new HashMap<V, K>();

	    for (Entry<K, V> entry : map.entrySet())
	        inv.put(entry.getValue(), entry.getKey());

	    return inv;
	}
}
