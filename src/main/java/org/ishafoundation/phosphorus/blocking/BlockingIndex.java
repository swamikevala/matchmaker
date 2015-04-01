package org.ishafoundation.phosphorus.blocking;

import java.util.ArrayList;
import java.util.Set;

import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.json.JsonArray;

import org.ishafoundation.phosphorus.blocking.method.BlockingMethod;
import org.ishafoundation.phosphorus.blocking.keygen.KeyGenerator;
import org.ishafoundation.phosphorus.blocking.basekeygen.BaseKeyGenerator;
import org.ishafoundation.phosphorus.api.IndexAPI;
import org.ishafoundation.phosphorus.Utils;

public class BlockingIndex {
	
	private BaseKeyGenerator bkg;
	private KeyGenerator kg;
	private BlockingMethod method;
	
	public BlockingIndex(JsonObject config, String attribute) {
	
		String basePath = "attributes.".concat(attribute).concat(".blocking.");
		bkg = (BaseKeyGenerator) Utils.getInstance(config, basePath.concat("basekeygen"));
		kg = (KeyGenerator) Utils.getInstance(config, basePath.concat("keygen"));
		method = (BlockingMethod) Utils.getInstance(config, basePath.concat("method"));
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


