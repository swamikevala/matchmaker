package org.ishafoundation.matchmaker.scoring;

import java.util.Map;
import java.lang.Math;

import com.google.gson.JsonObject;

import org.ishafoundation.matchmaker.QueryResult;

public class GeometricMean implements ScoringMethod {

	private double degree;

	public GeometricMean(JsonObject params) {
		degree = params.get("degree").getAsDouble();
	}
	
	@Override
	public double score(QueryResult queryResult) {
	
		double score = 0.0;
		for ( Map.Entry<String, Double> attribute : queryResult.getScores().entrySet() ) {
			score = score + Math.pow(attribute.getValue(), degree);
		}
		score = Math.pow(score, (1/degree));
		return score;
	}

}
