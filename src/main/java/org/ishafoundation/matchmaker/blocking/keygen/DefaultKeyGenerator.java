package org.ishafoundation.matchmaker.blocking.keygen;

import java.util.Set;
import java.util.HashSet;


public class DefaultKeyGenerator implements KeyGenerator {

	public Set<String> generateKeys(String value) {
		Set<String> keys = new HashSet();
		keys.add(value);
		return keys;
	}
}