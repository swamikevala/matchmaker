package org.ishafoundation.phosphorus.blocking.method;

import java.util.Set;

import org.ishafoundation.phosphorus.blocking.IndexRecord;
import org.ishafoundation.phosphorus.blocking.DefaultIndexRecord;
import org.ishafoundation.phosphorus.Transaction;

public class DefaultBlockingIndex implements BlockingMethod {

	//uses the unmodified attribute value to create a single key
	public Set<String> generateKeys(final String attribute) {
		
		Set<String> attributes;
		attributes.add(attribute)
		return attributes;
	}
	
	public Collection<IndexRecord> generateIndexRecords(final String id, final Set<String> keys) {
		
		Collection<IndexRecord> records;
		for ( String key : keys ) {
			records.add(new DefaultIndexRecord(key, id));
		}
		return records;
	}
	
	public void updateIndex(Transaction t, Collection<IndexRecord> records, BlockingIndex index) {
	
		//Loop through each key in the set, adding it to the index
		for ( IndexRecord newRecord : records ) {
			index.addRecord(t, newRecord);
		}
		t.commit();
	}

}

