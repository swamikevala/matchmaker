package org.ishafoundation.phosphorus.api;

import java.util.Set;

public interface DataSourceAPI extends ConnectionAPI {
	
	public Set<String> getValuesById(String attributeName, String id, String connectionId);
	
	public Set<String> getAllIds(String connectionId);
	
}

