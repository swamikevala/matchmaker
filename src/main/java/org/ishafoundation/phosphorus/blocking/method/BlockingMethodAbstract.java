package org.ishafoundation.phosphorus.blocking.method;

import java.util.Set;

import org.vertx.java.core.json.JsonObject;

import org.ishafoundation.phosphorus.blocking.IndexRecord;
import org.ishafoundation.phosphorus.blocking.BaseKey;
import org.ishafoundation.phosphorus.blocking.DefaultIndexRecord;

public class BlockingMethodAbstract implements BlockingMethod {

	private JsonObject config;

	public BlockingMethodAbstract( config ) {
		
		this.config = config;
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

