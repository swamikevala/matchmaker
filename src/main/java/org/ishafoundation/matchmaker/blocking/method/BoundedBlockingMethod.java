package org.ishafoundation.matchmaker.blocking.method;

import java.util.Set;
import java.util.HashSet;

import com.google.gson.JsonObject;

import org.ishafoundation.matchmaker.blocking.BlockingIndex;
import org.ishafoundation.matchmaker.api.IndexAPI;

public class BoundedBlockingMethod implements BlockingMethod {

	private int maxBlockSize;

	public BoundedBlockingMethod(JsonObject params) {
		maxBlockSize = params.get("max-block-size").getAsInt(); 
	}

	public Set<String> queryIndex(IndexAPI api, String attribute, String key, String conId) {
		return api.getIds(attribute, key, conId);
	}
	
	public void updateIndex(IndexAPI api, String attribute, String key, String baseKey, String id, String conId) {
		int blockSize = api.getKeyCount(attribute, key, conId);
		if ( key != null && key != "" ) {
			if ( blockSize < maxBlockSize ) {
				api.add(attribute, key, baseKey.length(), key, id, conId);
			} else {
				//if key = basekey then add it and try to delete existing key whose bkl is bigger
				if ( key.equals(baseKey) ) {
					api.add(attribute, key, baseKey.length(), key, id, conId);
					String maxBKId = api.getMaxBaseKeyId(attribute, key, conId);
					if ( api.getBaseKeyLength(attribute, maxBKId, key, conId) > baseKey.length() ) {
						api.delete(attribute, maxBKId, key, conId);
					}
				}
			}
		}
	}

}

			
	
	
					
				
		
		
		
		