package org.ishafoundation.matchmaker.blocking.keygen;

import java.util.Set;
import java.util.HashSet;

import com.google.gson.JsonObject;

public class SuffixAndPrefix implements KeyGenerator {

	private int minKeyLength;

	public SuffixAndPrefix(JsonObject params) {
		minKeyLength = params.get("min-length").getAsInt();
	}

	@Override
	public Set<String> generateKeys(String value) {
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