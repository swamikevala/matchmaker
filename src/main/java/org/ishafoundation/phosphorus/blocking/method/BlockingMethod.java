package org.ishafoundation.phosphorus.blocking.method;

import java.util.Set;

import org.ishafoundation.phosphorus.api.IndexAPI;

public interface BlockingMethod {

	public Set<String> queryIndex(IndexAPI api, String attribute, String key, String connId);

	public void updateIndex(IndexAPI api, String attribute, String key, String baseKey, String id, String connId);
	
}


