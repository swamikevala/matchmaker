package org.ishafoundation.matchmaker.cache;

public class ValueCache {

	private String cacheId;
	private String attribute;
	private String value;
	private long createdTime;
	private long lastAccessedTime;

	public ValueCache(String cacheId, String attribute, String value) {
		this.cacheId = cacheId;
		this.attribute = attribute;
		this.value = value;
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
		ValueCache otherVC;
		if (otherObj != null && otherObj instanceof ValueCache) {
			otherVC = (ValueCache) otherObj;
		} else {
			return false;
		}
		if ( cacheId.equals(otherVC.cacheId) && attribute.equals(otherVC.attribute) && value.equals(otherVC.value) ) {
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
		result = prime * result + attribute.hashCode();
		result = prime * result + value.hashCode();
		return result;
	}
	
}