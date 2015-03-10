package org.ishafoundation.phosphorus.blockingindex;

import java.util.Collection;
import java.util.Set;

import org.ishafoundation.phosphorus.Utils;
import org.ishafoundation.phosphorus.blockingindex.BlockingIndexDefinition;
import org.ishafoundation.phosphorus.blockingindex.BlockingIndex;
import org.ishafoundation.phosphorus.blockingindex.IndexRecord;
import org.ishafoundation.phosphorus.blockingindex.IndexRecordImpl;
import org.ishafoundation.phosphorus.Transaction;

public class SuffixArrayModified implements BlockingIndexDefinition{

	public static final int MIN_SUFFIX_LENGTH = 5;
	public static final int MAX_BLOCK_SIZE = 100;

	public Set<String> generateKeys(final Map<String, String> attributeMap) {
	
		String baseKey;
		String name = attributeMap.get("name");
		//Could replace with Unicode friendly regexp: \p{L}\p{M}*+.
		baseKey = Utils.indianNameNormalize(name.replaceAll("[^a-zA-Z ]", "").toLowerCase());
		baseKey = Utils.sortWords(name).replace(" ", "");
		
		Set<String> suffixList;
		suffixList.add(baseKey);
		int N = baseKey.length() - MIN_SUFFIX_LENGTH + 1;
		if ( baseKey.length() > MIN_SUFFIX_LENGTH ) {
			for ( int x = 1; x < N; x = x+1 ) {
				suffixList.add(baseKey.substring(x));
				suffixList.add(baseKey.substring(0, baseKey.length() - x));
			}
		return suffixList;
	}
	
	public Collection<IndexRecord> generateRecords(final String id, final Set<String> keys) {
		
		Collection<IndexRecord> records;
		int baseKeyLength = getMaxKeyLength(keys);
		for ( String key : keys ) {
			records.add(new IndexRecordImpl(key, id, baseKeyLength));
		}
		return records;
	}
	
	
	private int getMaxKeyLength(final Set<String> keys) {
		int max = 0;
		for ( String key : keys ) {
			max = ( key.length() > max ) ? key.length() : max;
		}
		return max;
	}
	
	
	public void updateIndex(Transaction t, Collection<IndexRecord> records, BlockingIndex index) {
	
		//Loop through each key in the set, adding it to the index
		for ( IndexRecord newRecord : records ) {
			//get the key value
			String key = newRecord.getKey();
			//Check current number of existing index records with each key
			Collection<IndexRecord> existingRecords = index.getRecordsByKey(key);
			if ( existingRecords.size() < MAX_BLOCK_SIZE ) { //if strictly > then warning (should not be that many records with same key)
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

