package com.wcohen.ss;

import org.ishafoundation.matchmaker.comparators.Comparator;
import org.vertx.java.core.json.JsonObject;

/**
 * Jaro distance metric, as extended by Winkler.  
 */
public class JaroWinkler extends WinklerRescorer implements Comparator
{
	private final JsonObject params;
	
	public JaroWinkler(JsonObject params) { 
		super(new Jaro(params)); 
		this.params = params;
	}
	
}
