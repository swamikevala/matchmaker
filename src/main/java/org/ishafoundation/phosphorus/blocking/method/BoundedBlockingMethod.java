package org.ishafoundation.phosphorus.blocking.method;

import java.util.Set;
import java.util.HashSet;

import org.vertx.java.core.json.JsonObject;

import org.ishafoundation.phosphorus.blocking.BlockingIndex;
import org.ishafoundation.phosphorus.api.IndexAPI;
import org.ishafoundation.phosphorus.Utils;

public class BoundedBlockingMethod implements BlockingMethod {

	private final JsonObject params;

	public BoundedBlockingMethod(JsonObject params) {
		this.params = params;
	}

	public Set<String> queryIndex(IndexAPI api, String attribute, String key, String connId) {
		return api.getIds(attribute, key, connId);
	}
	
	public void updateIndex(IndexAPI api, String attribute, String key, String baseKey, String id, String connId) {
		int maxBlockSize = params.getInteger("max-block-size");
		int blockSize = api.getKeyCount(attribute, key, connId);
		if ( key != null && key != "" ) {
			if ( blockSize < maxBlockSize ) {
				api.add(attribute, key, baseKey.length(), key, id, connId);
			} else {
				//if key = basekey then add it and try to delete existing key whose bkl is bigger
				if ( key.equals(baseKey) ) {
					api.add(attribute, key, baseKey.length(), key, id, connId);
					String maxBKId = api.getMaxBaseKeyId(attribute, key, connId);
					if ( api.getBaseKeyLength(attribute, maxBKId, key, connId) > baseKey.length() ) {
						api.delete(attribute, maxBKId, key, connId);
					}
				}
			}
		}
	}

}

			
	
	
					
				
		
		
		
		