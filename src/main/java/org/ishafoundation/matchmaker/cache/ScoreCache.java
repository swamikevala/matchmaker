package org.ishafoundation.matchmaker.cache;

public class ScoreCache {

	private String cacheId;
	private String attribute;
	private String comparatorName;
	private String queryValue;
	private String candidateValue;
	private long createdTime;
	private long lastAccessedTime;

	public ScoreCache(String cacheId, String attribute, String compName, String qValue, String cValue) {
		this.cacheId = cacheId;
		this.attribute = attribute;
		this.comparatorName = compName;
		this.queryValue = qValue;
		this.candidateValue = cValue;
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
		ScoreCache otherSC;
		if (otherObj != null && otherObj instanceof ScoreCache) {
			otherSC = (ScoreCache) otherObj;
		} else {
			return false;
		}
		if ( cacheId.equals(otherSC.cacheId) && 
			attribute.equals(otherSC.attribute) && 
			comparatorName.equals(otherSC.comparatorName) &&
			queryValue.equals(otherSC.queryValue) &&
			candidateValue.equals(otherSC.candidateValue) ) {
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
		result = prime * result + comparatorName.hashCode();
		result = prime * result + queryValue.hashCode();
		result = prime * result + candidateValue.hashCode();
		return result;
	}
	
}