package org.ishafoundation.matchmaker.blocking.keygen;

import java.util.Set;
import java.util.HashSet;

import com.google.gson.JsonObject;

public class RemovePartKG implements KeyGenerator {

	private int maxPartLength;

	public RemovePartKG(JsonObject params) {
		maxPartLength = params.get("max-length").getAsInt();
	}
	
	@Override
	public Set<String> generateKeys(String value) {
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