package org.ishafoundation.phosphorus.blocking.keygen;

import java.util.Set;
import java.util.HashSet;

import org.vertx.java.core.json.JsonObject;

public class RemovePartKG implements KeyGenerator {

	private final JsonObject params;

	public RemovePartKG(JsonObject params) {
		this.params = params;
	}
	
	@Override
	public Set<String> generateKeys(String value) {
		int maxPartLength = params.getInteger("max-length");
		Set<String> keys = new HashSet();
		keys.add(value);
		int N = value.length();
		//if ( value.length() > minKeyLength ) {
			for ( int x = 0; x < N; x++ ) {
				keys.add(value.substring(0, x).concat(value.substring(x+1)));
			}
			//keys.add(value.substring(0, N-1));
		//}
		return keys;
	}
}