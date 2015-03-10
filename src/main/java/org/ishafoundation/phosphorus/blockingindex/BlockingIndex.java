package org.ishafoundation.phosphorus.blockingindex;

import java.util.Collection;

import org.ishafoundation.phosphorus.blockingindex.IndexRecord;
import org.ishafoundation.phosphorus.Transaction;

public interface BlockingIndex {
	
	public Collection<IndexRecord> getRecordsByKey(final String key);
	
	public Collection<IndexRecord> getRecordsById(final String id);
	
	public void addRecord(Transaction t, final IndexRecord record);
	
	public void deleteRecord(Transaction t, final String key, final String id);
	
}

