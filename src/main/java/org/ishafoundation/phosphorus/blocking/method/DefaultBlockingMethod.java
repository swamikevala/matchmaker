package org.ishafoundation.phosphorus.blocking.method;

import java.util.Set;

import org.vertx.java.core.json.JsonObject;

import org.ishafoundation.phosphorus.api.IndexAPI;

public class DefaultBlockingMethod implements BlockingMethod {

	private final JsonObject params;
	
	public DefaultBlockingMethod(JsonObject params) {
		this.params = params;
	}

	public Set<String> queryIndex(IndexAPI api, String attribute, String key, String connId) {
		return api.getIds(attribute, key, connId);
	}
	
	public void updateIndex(IndexAPI api, String attribute, String key, String baseKey, String id, String connId) {
		api.add(attribute, key, baseKey.length(), key, id, connId);
	}

}

