package org.ishafoundation.phosphorus.blocking.method;

import java.util.Set;

import org.ishafoundation.phosphorus.blocking.IndexKey;
import org.ishafoundation.phosphorus.blocking.api.IndexAPI;

public interface BlockingMethod {

	public Set<String> queryIndex(IndexAPI api, Set<IndexKey> keys);

	public void updateIndex(IndexAPI api, String id, Set<IndexKey> keys);
	
}

