package org.ishafoundation.matchmaker.matching;

import org.ishafoundation.matchmaker.comparators.Comparator;

public class AttributeRule {

	private String attribute;
	private boolean blocking;
	private Comparator comparator;
	private double threshold;

	public AttributeRule(String attribute, boolean blocking, Comparator comparator, double threshold) {
		this.attribute = attribute;
		this.blocking = blocking;
		this.comparator = comparator;
		this.threshold = threshold;
	}
		
	public String getAttribute() {
		return attribute;
	}
	
	public boolean getBlocking() {
		return blocking;
	}
	
	public Comparator getComparator() {
		return comparator;
	}
	
	public double getThreshold() {
		return threshold;
	}
}

