package org.ishafoundation.matchmaker.blocking.method;

import java.util.Collection;
import java.util.Set;
import java.util.HashSet;

import org.vertx.java.core.json.JsonObject;

import org.ishafoundation.matchmaker.blocking.method.BlockingMethodAbstract;
import org.ishafoundation.matchmaker.blocking.IndexKey;
import org.ishafoundation.matchmaker.blocking.BlockLink;
import org.ishafoundation.matchmaker.api.FDBIndexAPI;

public class RobustMultiBlockBounded extends BlockingMethodAbstract implements BlockingMethod {

	private final JsonObject params;
	
	public RobustMultiBlockBounded(JsonObject params) {
		this.params = params;
	}
	
	private int getMaxKeyLength(final Set<String> keys) {
		int max = 0;
		for ( String key : keys ) {
			max = ( key.length() > max ) ? key.length() : max;
		}
		return max;
	}
	
	private void linkBlocks(IndexKey key) {
		StringDistance sd = new StringDistance("Levenshtein"); //take from config
		TreeSet<IndexKey> orderedKeys = new TreeSet<IndexKey>(new Comparator<IndexKey>() {
			@Override
			private int compare(IndexKey k1, IndexKey k2) {
				double k1Score = sd.similarity(key.getValue(), k1.getValue());
				double k2Score = sd.similarity(key.getValue(), k2.getValue());
				return k2Score.compareTo(k1Score);
			}
		});
		Set<IndexKey> adjKeys = indexAPI.getAdjacentKeys(key, 1);
		for ( IndexKey aKey : adjKeys ) {
			if ( sd.similarity(key.getValue(), aKey.getValue()) >= 0.75 ) { //take from config
				orderedKeys.add(aKey);
			}
		}
		TreeSet<Integer> blockIds;
		for ( IndexKey k : orderedKeys ) {
			blockIds.add(k.getBlockId);
		}
	}

	public Set<String> queryIndex(IndexAPI indexAPI, Set<IndexKey> keys) {
	
		Set<String> candidateIds = new HashSet();
		for ( IndexKey key : keys ) {
			
		
		}
	
	}
	
	//Later may need to make id an array (for pks based on multiple id fields)
	public void updateIndex(IndexAPI indexAPI, String id, Set<IndexKey> keys) {
	
		int maxBlockSize = params.getInteger("max-block-size");
		//Loop through each key in the set, adding it to the index
		boolean idAdded = false;
		for ( IndexKey key : keys ) {
			int bkLen = key.getBaseKey().getLength()
			//Get count of records in the same blocks (i.e. with *similar* keys)
			linkBlocks(key);
			if ( key.getBlockLinks().isEmpty() || indexAPI.getBlockCountByKey(key) == 0 ) {
				//Found no existing blocks, so make a new one
				key.addBlockLink(new BlockLink(indexAPI.getNewBlockId(), true);
				indexAPI.add(key, id);
			} else {
				for ( BlockLink blockLink : key.getBlockLinks() ) {
					int blockSize = indexAPI.getBlockSize(blockLink.getBlockId());
					if ( blockSize < maxBlockSize ) {
						indexAPI.add(key, id);
						keyAdded = true;
					} else {
						if ( bkLen 
					
				
		
		
		
		
			//get the key value
			String key = newRecord.getKey();
			//Check current number of existing index records with each key
	
	Collection<IndexRecord> existingRecords = index.getRecordsByKey(key);
			if ( existingRecords.size() < maxBlockSize ) { //if strictly > then warning (should not be that many records with same key)
				index.addRecord(t, newRecord);
			} else {
				//already max number of records with this key. 
				//Need to remove one. Check for records having a greater baseKey length
				IndexRecord mbkRecord = getMaxBaseKeyRecord(existingRecords);
				if ( mbkRecord.getBaseKeyLength() > newRecord.getBaseKeyLength() ) {
					index.deleteRecord(t, mbkRecord.getKey(), mbkRecord.getId());
					index.addRecord(t, newRecord);
				}
			}
		}
		t.commit();
	}
	
	private IndexRecord getMaxBaseKeyRecord(Collection<IndexRecord> records) {
		IndexRecord mbkRec = new IndexRecord();
		int max = 0;
		for ( IndexRecord rec : records ) {
			mbkRec = (rec.getBaseKeyLength() > max) ? rec : mbkRec;
		}
		return mbkRec;
	}
	
}