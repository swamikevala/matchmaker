package org.ishafoundation.matchmaker.api;

import java.util.Set;

import com.foundationdb.FDB;
import com.foundationdb.Database;
import com.foundationdb.directory.DirectorySubspace;
import com.foundationdb.directory.DirectoryLayer;

import org.ishafoundation.matchmaker.blocking.IndexKey;
import org.ishafoundation.matchmaker.blocking.BlockLink;

public class FDBIndexAPI {
	
	private final int fdbApiVersion = 300;
	private final FDB fdb;
	private final Database db;
	private final String indexDirectoryName = "blocking";
	private final DirectoryLayer dl;
	
	public FDBIndexAPI() {
		fdb = FDB.selectAPIVersion(fdbApiVersion);
		db = fdb.open();
		dl = DirectoryLayer.getDefault();
		//open/create indexes for all attributes in config file
		DirectorySubspace blockingIndex;
	}
	
	public int getBlockSize(String indexName, int blockId) {
	
	
	
	}
	
	public int getBlockId(String indexName, IndexKey key);
	
	public Set<SubKey> getAdjacentKeys(IndexKey key, int windowSize);  //ws=0 means get the matching key itself if it exists, ws=1 means one on either side
	
	public void add(String indexName, IndexKey key, String id) {
		
		.createOrOpen(db, PathUtil.from("blocking_idx", blockingIndexName)).get();
		Set<BlockLink> blockLinks = key.getBlockLinks();
		for ( BlockLink bl : blockLinks ) {
			
		}
	
	}
	
	public int getNewBlockId() {
	
	
	}
	
	public int getBlockCountByKey(IndexKey key);
	
}

