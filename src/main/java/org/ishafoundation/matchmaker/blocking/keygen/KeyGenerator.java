package org.ishafoundation.matchmaker.blocking.keygen;

import java.util.Set;

public interface KeyGenerator {

	public Set<String> generateKeys(String value);

}