package com.wcohen.ss.api;

/**
 * An iterator over DistanceInstance objects.
 */

public interface DistanceInstanceIterator extends java.util.Iterator<DistanceInstance> 
{
	public boolean hasNext();
	public DistanceInstance next();
	public DistanceInstance nextDistanceInstance();
}
