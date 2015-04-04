package org.ishafoundation.matchmaker.blocking;

import java.util.ArrayList;
import java.util.Set;

import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.json.JsonArray;

import org.ishafoundation.matchmaker.blocking.method.BlockingMethod;
import org.ishafoundation.matchmaker.blocking.keygen.KeyGenerator;
import org.ishafoundation.matchmaker.blocking.basekeygen.BaseKeyGenerator;
import org.ishafoundation.matchmaker.api.IndexAPI;
import org.ishafoundation.matchmaker.Utils;

public class BlockingIndex {
	
	private BaseKeyGenerator bkg;
	private KeyGenerator kg;
	private BlockingMethod method;
	
	public BlockingIndex(JsonObject config, String attribute) {
		String basePath = "blocking.".concat(attribute);
		bkg = (BaseKeyGenerator) Utils.getInstance(config, basePath.concat(".base-keygen"));
		kg = (KeyGenerator) Utils.getInstance(config, basePath.concat(".keygen"));
		method = (BlockingMethod) Utils.getInstance(config, basePath.concat(".method"));
	}
	
	public BaseKeyGenerator getBaseKeyGenerator() {
		return bkg;
	}
	
	public KeyGenerator getKeyGenerator() {
		return kg;
	}
	
	public BlockingMethod getBlockingMethod() {
		return method;
	}
	
}


