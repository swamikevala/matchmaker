package org.ishafoundation.phosphorus.blockingindex;

import org.ishafoundation.phosphorus.Transaction;

public interface BlockingIndexDefinition {

	public String getName();
	
	public Set<String> generateKeys(final Map<String, String> attributes);
	
	public Collection<IndexRecord> generateRecords(final String id, final Set<String> keys);

	public void updateIndex(Transaction t, final Collection<IndexRecord> records, final BlockingIndex index);
	
}

//Make a blockingindex abstract base class where we can define the IndexRecordDefinition object

//For index update mode
	//bid.updateIndex(t, id, keys, fdbIndex);
