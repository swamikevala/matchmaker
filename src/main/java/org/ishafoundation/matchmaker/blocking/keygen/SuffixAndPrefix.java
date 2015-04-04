package org.ishafoundation.matchmaker.blocking.keygen;

import java.util.Set;
import java.util.HashSet;

import org.vertx.java.core.json.JsonObject;

public class SuffixAndPrefix implements KeyGenerator {

	private final JsonObject params;

	public SuffixAndPrefix(JsonObject params) {
		this.params = params;
	}

	@Override
	public Set<String> generateKeys(String value) {
		int minKeyLength = params.getInteger("min-length");
		Set<String> keys = new HashSet();
		keys.add(value);
		int N = value.length() - minKeyLength + 1;
		if ( value.length() > minKeyLength ) {
			for ( int x = 1; x < N; x = x+1 ) {
				keys.add(value.substring(x));
				keys.add(value.substring(0, value.length() - x));
			}
		}
		return keys;
	}
}