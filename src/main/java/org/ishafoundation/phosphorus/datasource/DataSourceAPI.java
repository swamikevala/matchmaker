package org.ishafoundation.phosphorus.datasource;

import org.vertx.java.core.json.JsonArray;

public interface DataSourceAPI {
	
	public JsonArray getValuesById(final String attributeName, final String id);
	
}

