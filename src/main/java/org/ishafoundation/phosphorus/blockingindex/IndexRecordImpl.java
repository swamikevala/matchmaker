package org.ishafoundation.phosphorus.blockingindex;

public class IndexRecordImpl {

	public String key;
	public String id;
	public int baseKeyLength;

	public IndexRecordImpl(String key, String id, int baseKeyLength) {
	
		this.key = key;
		this.id = id;
		this.baseKeyLength = baseKeyLength;
	}

	public String getKey() {
		return this.key;
	}
	
	public String getId() {
		return this.id;
	}
	
	public int getBaseKeyLength() {
		return this.baseKeyLength;
	}
		
}
