package org.ishafoundation.matchmaker.blocking.method;

import java.util.Set;

import org.ishafoundation.matchmaker.api.IndexAPI;

public class DefaultBlockingMethod implements BlockingMethod {

	public Set<String> queryIndex(IndexAPI api, String attribute, String key, String conId) {
		return api.getIds(attribute, key, conId);
	}
	
	public void updateIndex(IndexAPI api, String attribute, String key, String baseKey, String id, String conId) {
		api.add(attribute, key, baseKey.length(), key, id, conId);
	}

}

