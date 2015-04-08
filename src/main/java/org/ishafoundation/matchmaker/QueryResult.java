package org.ishafoundation.matchmaker;

import java.util.Map;

import org.vertx.java.core.json.JsonObject;

public class QueryResult {

	private String id;
	private Map<String, Double> scores;

	public QueryResult(String id, Map<String, Double> scores) {
		this.id = id;
		this.scores = scores;
	}
	
	public String getId() {
		return id;
	}
	
	public Map<String, Double> getScores() {
		return scores;
	}
}

