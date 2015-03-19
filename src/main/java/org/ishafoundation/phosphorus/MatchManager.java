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

import org.ishafoundation.phosphorus.datasource.api.MySQLDataSourceAPI;
import org.ishafoundation.phosphorus.blocking.BaseKey;
import org.ishafoundation.phosphorus.blocking.method.BlockingMethod;
import org.ishafoundation.phosphorus.blocking.method.DefaultBlockingMethod;
import org.ishafoundation.phosphorus.blocking.keygen.KeyGenerator;

import org.ishafoundation.phosphorus.Utils;

public class MatchManager extends Verticle {

	private final JsonObject config;
	private Set<String> attributes;
	private HashMap<String, BlockingIndex> biMap;
	private DataSourceAPI dataAPI = new MySQLDataSourceAPI(); 

	public void start() {
	
		config = container.config();
		attributes = config.getObject("attributes").getFieldNames();
		biMap = getBlockingIndexConfig(attributes);
		EventBus eb = vertx.eventBus();
		eb.registerHandler("match", matchHandler);
		eb.registerHandler("index", indexHandler);
	}
		
	Handler<Message<String>> indexHandler = new Handler<Message<String>>() {
		public void handle(Message<String> msg) {	
		
			String id = msg.body();
			for ( att : attributes ) {
				//need to support multiple values for an attribute (e.g. 2 mobile numbers)
				BlockingIndex bi = biMap.get(att);
				JsonObject bkConfig = getConfigObject("attributes".concat(att).concat("blocking.basekey"));
				JsonArray values = dataAPI.getValuesById(att, id);
				for ( String value : values ) {
					bi.method.updateIndex(bi.api, id, bi.keygen.generateKeys(new BaseKey(value, bkConfig)));
				}
			}
		}
	});
		
	Handler<Message<JsonObject>> queryHandler = new Handler<Message<JsonObject>>() {
		public void handle(Message<JsonObject> msg) {	
		
			//final HashMap<String, String> attributeMap = Utils.jsonObectToStringHashMap(msg.body());
			JsonObject qryObj = msg.body();
			Set<String> allCandidateIds = getCandidateIds(qryObj);
			
			
			
			
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
	
	private Set<String> getCandidateIds(JsonObject qryObj) {
		Set<String> allCandidateIds = new HashSet();
		
		for ( String att : qryObj.getFieldNames() ) {
			BlockingIndex bi = biMap.get(att);
			JsonObject bkConfig = getConfigObject("attributes".concat(att).concat("blocking.basekey"));
			JsonArray attValues = new JsonArray();
			attValues.addArray(qryObj.getArray(att));
			
			//What about if array values are not Strings ?
			for ( String attValue : qryAttValues ) {
				BaseKey baseKey = new BaseKey(attValue, bkConfig);
				Set<IndexKey> indexKeys = bi.keygen.generateKeys(baseKey);
				allCandidateIds.addAll(getCandidateIds(qryObj, qryAttribute));
			}
		
		
		

		

			
		Set<String> candidateIds;
		for ( String key : blockingKeys ) {
			eb.send("database.index.operations", key, new Handler<Message<JsonArray>>() {
				public void handle(Message<JsonArray> candidateIdsMsg) { 
					candidateIds.addAll(Utils.jsonArrayToStringSet(candidateIdsMsg.body()));
				}
			});
		}
		return candidateIds;
	}
	
	private class BlockingIndex {
		private KeyGenerator keygen;
		private BlockingMethod method;
		private IndexAPI api;
		
		private BlockingIndex(KeyGenerator keygen, BlockingMethood method, IndexAPI api) {
			this.keygen = keygen;
			this.method = method;
			this.api = api;
		}
	}
	
	private HashMap<String, BlockingIndex> getBlockingIndexConfig(Set<String> attributes) {
		HashMap<String, BlockingIndex> biMap = new HashMap<String, BlockingIndex>();
		for ( att : attributes ) {
			String basePath = "attributes.".concat(att).concat(".blocking.");
			JsonObject kgConf = getConfigObject(basePath.concat("keygen");
			JsonObject mConf = getConfigObject(basePath.concat("method");
			JsonObject apiConf = getConfigObject(basePath.concat("api");
			String kgClName = kgConf.getString("class");
			String mClName = mConf.getString("class");
			String apiClName = apiConf.getString("class");
			JsonObject kgParams = kgConf.getObject("params");
			JsonObject mParams = mConf.getObject("params");
			JsonObject apiParams = apiConf.getObject("params");
			KeyGenerator kg = (KeyGenerator) getInstance(kgClName, kgParams);
			BlockingMethod m = (BlockingMethod) getInstance(mClName, mParams);
			IndexAPI api = (IndexAPI) getInstance(apiClName, apiParams);
			BlockingIndex bi = new BlockingIndex(kg, m, api);
			biMap.put(att, bi);
		}
	}
	
	private Object getInstance(String className, JsonObject params) {
		String base = config.getString("base");
		JsonObject classes = config.getObject("classes");
		String fqcn = classes.getString(className).replace("$base", base);
		Class klass = Class.forname(fqcn);
		Constructor konst = klass.getConstructor(JsonObject);
		Object obj = konst.newInstance(params);
		return obj;
	}
	
	private JsonObject getConfigObject(String path) {
		String[] objNames = path.split("\\.");
		JsonObject jo = config.getObject(objNames[0]);
		for ( int x = 0; x < objNames.length; x = x+1 ) {
			jo = jo.getObject(objNames[x+1]);
		}
	}
}

