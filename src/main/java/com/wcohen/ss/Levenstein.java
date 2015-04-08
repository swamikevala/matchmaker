package com.wcohen.ss;

import org.ishafoundation.matchmaker.Comparator;
import org.vertx.java.core.json.JsonObject;

/**
 * Levenstein string distance. Levenstein distance is basically
 * NeedlemanWunsch with unit costs for all operations.
 */

public class Levenstein extends NeedlemanWunsch implements Comparator
{
	private final JsonObject params;

	public Levenstein(JsonObject params) {
		super(CharMatchScore.DIST_01, 1.0 );
		this.params = params;
	}
	public String toString() { return "[Levenstein]"; }

}
