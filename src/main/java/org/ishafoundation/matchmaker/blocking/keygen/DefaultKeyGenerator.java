package org.ishafoundation.matchmaker.blocking.keygen;

import java.util.Set;
import java.util.HashSet;

import org.vertx.java.core.json.JsonObject;

public class DefaultKeyGenerator implements KeyGenerator {

	private final JsonObject params;

	public DefaultKeyGenerator(JsonObject params) {
		this.params = params;
	}

	public Set<String> generateKeys(String value) {
		Set<String> keys = new HashSet();
		keys.add(value);
		return keys;
	}
}