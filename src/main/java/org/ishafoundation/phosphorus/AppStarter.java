package org.ishafoundation.phosphorus;

import org.vertx.java.platform.Verticle;

public class AppStarter extends Verticle {

	final String matchManagerFQCN = "org.ishafoundation.phosphorus.MatchManager";

	public void start() {
	
		final JsonObject appConfig = container.config();
		
		final String queryFQCN = appConfig.getObject("query_config").getString("query_class");
		final String databaseFQCN = appConfig.getObject("database_config").getString("database_class");
		//final String processingFQCN = "org.ishafoundation.phosphorus.Process";
		
		container.deployVerticle(queryFQN); 
		container.deployVerticle(matchManagerFQCN, appConfig);

		//container.deployWorkerVerticle(databaseFQCN); 
		//container.deployWorkerVerticle(processingFQCN); 
		
	}

}