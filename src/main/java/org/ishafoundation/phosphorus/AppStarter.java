package org.ishafoundation.phosphorus;

import org.vertx.java.platform.Verticle;
import org.vertx.java.core.json.JsonObject;

import org.ishafoundation.phosphorus.Utils;

public class AppStarter extends Verticle {

	public void start() {
	
		JsonObject config = container.config();
		JsonObject queryConfig = Utils.getConfigObject(config, "query");
		String queryFQCN = Utils.getConfigClassFQCN(config, queryConfig.getString("class"));
		container.deployVerticle(queryFQCN, queryConfig.getObject("params")); 
		
		String base = config.getString("base");
		String mmFQCN = base.concat(".MatchManager");
		container.deployWorkerVerticle(mmFQCN, config);
	}

}