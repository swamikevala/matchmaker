package org.ishafoundation.matchmaker;

import org.vertx.java.platform.Verticle;
import org.vertx.java.core.json.JsonObject;

public class AppStarter extends Verticle {

	public void start() {
	
		JsonObject config = container.config();
		container.deployVerticle("org.ishafoundation.matchmaker.protocol.HTTPQuery"); 
		container.deployWorkerVerticle("org.ishafoundation.matchmaker.MatchManager", config);
	}

}