package org.ishafoundation.phosphorus;

import org.vertx.java.platform.Verticle;

public class AppStarter extends Verticle {

	public void start() {
	
		JsonObject appConfig = container.config();

		//JsonObject verticle1Config = appConfig.getObject("verticle1_conf");
		
		// Start the verticles that make up the app
		
		//DB specific class for LOW LEVEL db operations (FDB, MySQL. MongoDB etc...)
		//Not specific to either index or data related queries 
		
		final String queryClass = "org.ishafoundation.phosphorus.query.HTTPQuery";
		final String managerClass = "org.ishafoundation.phosphorus.MatchManager";
		final String databaseClass = "org.ishafoundation.phosphorus.database.FDBDatabaseAPI";
		//final String processingClass = "org.ishafoundation.phosphorus.Process";
		
		container.deployVerticle(queryClass); 
		container.deployVerticle(managerClass);
		container.deployWorkerVerticle(databaseClass); 
		//container.deployWorkerVerticle(processingClass); 
		
	}

}