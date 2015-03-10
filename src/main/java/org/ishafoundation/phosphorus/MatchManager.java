package org.ishafoundation.phosphorus;

import java.lang.Thread;
import java.lang.Object;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

import org.vertx.java.platform.Verticle;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.json.JsonArray;

import org.ishafoundation.phosphorus.blockingindex.BlockingIndexDefinition;
//import org.ishafoundation.phosphorus.BlockingIndex;
import org.ishafoundation.phosphorus.Utils;
//import org.ishafoundation.phosphorus.DataRecord;

public class MatchManager extends Verticle {

	public void start() {
	
		final EventBus eb = vertx.eventBus();
		eb.registerHandler("match.manager", queryHandler);
	
		BlockingIndexDefinition bid = new SuffixArrayModified();

	}
		
	final JsonObject queryAttributesObj = jsonInput.getObject("attributes");
	public Set<String> getCandidateIds(
		
		
	Handler<Message<JsonObject>> queryHandler = new Handler<Message<JsonObject>>() {
		public void handle(Message<JsonObject> msg) {	
		
			final HashMap<String, String> attributeMap = Utils.jsonObectToStringHashMap(msg.body());
			Set<String> blockingKeys = bid.generateKeys(attributeMap);
			
			Set<String> candidateIds;
			for ( String key : blockingKeys ) {
				eb.send("database.index.operations", key, new Handler<Message<JsonArray>>() {
					public void handle(Message<JsonArray> candidateIdsMsg) { 
						candidateIds.addAll(Utils.jsonArrayToStringSet(candidateIdsMsg.body()));
					}
				});
			}
			
			sleep(2000); //remove this after figuring out how to start after looped async db calls
			JsonArray jCandidateIds = Utils.stringSetToJsonArray(candidateIds);
			msg.reply(jCandidateIds);
			
			//Collection<DataRecord> candidateRecords;
			//for ( String candidateId : candidateIds ) {
			//	eb.send("database.data.operations", candidateId, new Handler<Message<JsonObject>>() {
			//		public void handle(Message<JsonObject> candidateRecordsMsg) { 
			//			candidateRecords.addAll(Utils.jsonObjectToStringSet(candidateRecordsMsg.body()));
			//		}
			//	});
			//}
			
			
		}
	};
	
}

