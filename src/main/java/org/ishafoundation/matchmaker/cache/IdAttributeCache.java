package org.ishafoundation.matchmaker.cache;

public class IdAttributeCache {

	private String cacheId;
	private String id;
	private String attribute;
	private long createdTime;
	private long lastAccessedTime;

	public IdAttributeCache(String cacheId, String id, String attribute) {
		this.cacheId = cacheId;
		this.id = id;
		this.attribute = attribute;
		this.createdTime = System.currentTimeMillis();
		this.lastAccessedTime = createdTime;
	}

	public void setLastAccessedTime() {
		this.lastAccessedTime = System.currentTimeMillis();
	}

	@Override
	public boolean equals(Object otherObj) {
		// check for reference
		if (this == otherObj) {
			return true;
		}
		// checks for equal type
		IdAttributeCache otherIAC;
		if (otherObj != null && otherObj instanceof IdAttributeCache) {
			otherIAC = (IdAttributeCache) otherObj;
		} else {
			return false;
		}
		if ( cacheId.equals(otherIAC.cacheId) && id.equals(otherIAC.id) && attribute.equals(otherIAC.attribute) ) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + cacheId.hashCode();
		result = prime * result + id.hashCode();
		result = prime * result + attribute.hashCode();
		
		return result;
	}
	
}