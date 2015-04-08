package org.ishafoundation.matchmaker.scoring;

import org.ishafoundation.matchmaker.QueryResult;

public interface ScoringMethod {

	public double score(QueryResult queryResult);

}