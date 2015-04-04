package org.ishafoundation.matchmaker.api;

import java.util.Set;

public interface IndexAPI extends ConnectionAPI {
	
	public boolean indexExists(String indexName, String connectionId);
	
	public void createIndex(String indexName, String connectionId);
	
	public void add(String indexName, String key, int baseKeyLength, String blockId, String id, String connectionId);
	
	public int getBaseKeyLength(String indexName, String id, String key, String connId);
	
	public String getMaxBaseKeyId(String indexName, String key, String connectionId);
	
	public int getKeyCount(String indexName, String key, String connectionId);
	
	public Set<String> getIds(String indexName, String key, String connectionId);
	
	public void delete(String indexName, String id, String key, String connectionId); 
	
	//public int getBlockCount(String indexName, String id, String connectionId);
	
	//public int getNewBlockId();
	
	//public int getBlockCountByKey(IndexKey key);
	
	//public int getMaxBaseKeyLength(String indexName, String key);
	
	//public int getBlockSize(int blockId);
	
	//public String getBlockId(IndexKey key);
	
	//public Set<IndexKey> getAdjacentKeys(IndexKey key, int windowSize);  //ws=0 means get the matching key itself if it exists, ws=1 means one on either side
	
}

