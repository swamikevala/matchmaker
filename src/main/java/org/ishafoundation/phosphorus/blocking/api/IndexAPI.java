package org.ishafoundation.phosphorus.blocking.api;

import java.util.Set;

import org.ishafoundation.phosphorus.blocking.IndexKey;

public interface IndexAPI {
	
	public int getBlockSize(int blockId);
	
	public int getBlockId(IndexKey key);
	
	public Set<IndexKey> getAdjacentKeys(IndexKey key, int windowSize);  //ws=0 means get the matching key itself if it exists, ws=1 means one on either side
	
	public void add(IndexKey key, String id);
	
	public int getNewBlockId();
	
	public int getBlockCountByKey(IndexKey key);
	
}

