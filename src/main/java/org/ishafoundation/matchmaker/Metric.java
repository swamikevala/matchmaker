package org.ishafoundation.matchmaker;

import org.ishafoundation.matchmaker.Comparator;

public class Metric {

	private Comparator comparator;
	private double threshold;

	public Metric(Comparator comparator, double threshold) {
		this.comparator = comparator;
		this.threshold = threshold;
	}
	
	public Comparator getComparator() {
		return comparator;
	}
	
	public double getThreshold() {
		return threshold;
	}
}

