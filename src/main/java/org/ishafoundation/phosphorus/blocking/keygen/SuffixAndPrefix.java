package org.ishafoundation.phosphorus.blocking.keygen;

import java.util.Set;

import org.ishafoundation.phosphorus.blocking.IndexKey;
import org.ishafoundation.phosphorus.blocking.BaseKey;

public class SuffixAndPrefix implements KeyGenerator {

	private final JsonObject params;

	public SuffixAndPrefix(JsonObject params) {
		this.params = params;
	}

	public Set<IndexKey> generateKeys(BaseKey baseKey) {
		int minKeyLength = params.getInteger("min-length");
		String baseValue = baseKey.getValue();
		Set<IndexKey> keyList;
		keyList.add(baseValue);
		int N = baseValue.length() - minKeyLength + 1;
		if ( baseValue.length() > minKeyLength ) {
			for ( int x = 1; x < N; x = x+1 ) {
				keyList.add(new IndexKey(baseValue.substring(x), baseKey), 0);
				keyList.add(new IndexKey(baseValue.substring(0, baseKey.getlength() - x), baseKey), 0);
			}
		return keyList;
	}
}