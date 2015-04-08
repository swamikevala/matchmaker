package com.wcohen.ss;

import org.ishafoundation.matchmaker.Comparator;


/**
 * Jaro distance metric, as extended by Winkler.  
 */
public class JaroWinkler extends WinklerRescorer implements Comparator
{

	public JaroWinkler() { 
		super(new Jaro()); 
	}
	
}
