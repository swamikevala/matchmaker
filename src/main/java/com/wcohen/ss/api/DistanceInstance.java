package com.wcohen.ss.api;

import java.util.*;

/**
 * An 'instance' for a StringDistance, analogous to an 'instance' for
 * a classification learner.  Consists of a pair of StringWrappers,
 * a distance, and some labeling information.
 */

public interface DistanceInstance 
{
	public StringWrapper getA();
	public StringWrapper getB();
	public boolean isCorrect();
	public double getDistance();
	public void setDistance(double distance);

	public static final Comparator<DistanceInstance> INCREASING_DISTANCE = new Comparator<DistanceInstance>() {
			public int compare(DistanceInstance a, DistanceInstance b) {
				if (a.getDistance() > b.getDistance()) return -1;
				else if (a.getDistance() < b.getDistance()) return +1;
				else return 0;
			}
		};
}
