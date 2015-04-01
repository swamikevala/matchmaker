package org.ishafoundation.phosphorus;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Map;
import java.util.HashSet;
import java.util.HashMap;
import java.util.UUID;

import org.vertx.java.platform.Verticle;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.json.JsonArray;

import org.ishafoundation.phosphorus.api.DataSourceAPI;
import org.ishafoundation.phosphorus.api.MySQLDataSourceAPI;
import org.ishafoundation.phosphorus.api.IndexAPI;
import org.ishafoundation.phosphorus.blocking.BlockingIndex;
import org.ishafoundation.phosphorus.blocking.method.BlockingMethod;
import org.ishafoundation.phosphorus.blocking.method.DefaultBlockingMethod;
import org.ishafoundation.phosphorus.blocking.keygen.KeyGenerator;
import org.ishafoundation.phosphorus.blocking.basekeygen.BaseKeyGenerator;
import org.ishafoundation.phosphorus.comparators.Comparator;
import org.ishafoundation.phosphorus.cache.ValueCache;
import org.ishafoundation.phosphorus.Utils;

public class MatchManager extends Verticle {

	private JsonObject config;
	private Set<String> attributes;
	private Map<String, BlockingIndex> biMap;
	private JsonArray rules;
	private IndexAPI indexAPI;
	private DataSourceAPI dataAPI;
	private Map<ValueCache, Set<String>> candidateCache;
	private EventBus eb;

	public void start() {
	
		config = container.config();
		attributes = config.getObject("attributes").getFieldNames();
		biMap = new HashMap<String, BlockingIndex>();
		rules = config.getArray("rules");
		dataAPI = (DataSourceAPI) Utils.getInstance(config, "data-source-api"); 
		indexAPI = (IndexAPI) Utils.getInstance(config, "index-api");
		eb = vertx.eventBus();
		eb.registerHandler("match", matchHandler);
		eb.registerHandler("index", indexHandler);
		String indexConnId = indexAPI.openSustainedConnection();
		String dataConnId = dataAPI.openSustainedConnection();
		for ( String att : attributes ) {
			biMap.put(att, new BlockingIndex(config, att));
			if ( ! indexAPI.indexExists(att, indexConnId) ) {
				System.out.println("Creating `" + att + "` index");
				indexAPI.createIndex(att, indexConnId);
				populateIndex(att, indexConnId, dataConnId);
			}
		}
		indexAPI.closeSustainedConnection(indexConnId);
		dataAPI.closeSustainedConnection(dataConnId);
		candidateCache = new HashMap<ValueCache, Set<String>>();
	}
		
	private void populateIndex(String attribute, String iConnId, String dConnId) {
		Set<String> allIds = dataAPI.getAllIds(dConnId);
		int count = 0;
		for ( String id : allIds ) {
			JsonObject jo = new JsonObject();
			jo.putString("id", id);
			jo.putString("index-connection-id", iConnId);
			jo.putString("data-connection-id", dConnId);
			updateIndex(jo, attribute);
			count++;
			if ( count % 1000 == 0 ) { 
				System.out.println(count + " records added");
			}
		}
	}
		
	private void updateIndex(JsonObject jo, String att) {
		String id = jo.getString("id");
		String iConnId = (jo.containsField("index-connection-id")) ? jo.getString("index-connection-id") : "";
		String dConnId = (jo.containsField("data-connection-id")) ? jo.getString("data-connection-id") : "";
		BlockingIndex bi = biMap.get(att);
		Set<String> values = dataAPI.getValuesById(att, id, dConnId);
		for ( String value : values ) {
			if ( !(value.equals("") || value == null) ) {
				String baseKey = bi.getBaseKeyGenerator().generateBaseKey(value);
				Set<String> keys = bi.getKeyGenerator().generateKeys(baseKey);
				for ( String key : keys ) {
					if ( !(key.equals("") || key == null) ) {
						bi.getBlockingMethod().updateIndex(indexAPI, att, key, baseKey, id, iConnId);
					}
				}
			}
		}
	}
		
	Handler<Message<JsonObject>> indexHandler = new Handler<Message<JsonObject>>() {
		public void handle(Message<JsonObject> msg) {
			for ( String att : attributes ) {
				updateIndex(msg.body(), att);
			}
			JsonObject replyObj = new JsonObject();
			//need to return identifier for each record (when run in batch) - so we know when all records are processed
			//otherwise we don't know when to terminate the connection (due to asyncyness)
			//should we return both connIds to the user? Or map them to a single uuid? (useful for batch querying / indexing)
			//replyObj.putString("index-connection-id", connId); 
			//replyObj.putString("cache-id", cacheId);
			msg.reply(replyObj);
		}
	};
		
	Handler<Message<JsonObject>> matchHandler = new Handler<Message<JsonObject>>() {
		public void handle(Message<JsonObject> msg) {	
		
			JsonObject qryObj = msg.body().getObject("attributes");
			//Do we need to specify connection ids for match queries?? Maybe for batch matching...
			String connId = (msg.body().containsField("connection-id")) ? msg.body().getString("connection-id") : "";
			String cacheId = "";
			if ( ! msg.body().containsField("cache-id") ) {
				cacheId = UUID.randomUUID().toString();
			} else {
				cacheId = msg.body().getString("cache-id");
			}
			Set<String> candidateIds = getCandidateIds(qryObj, cacheId, connId);
			Set<Integer> relevantRuleIndexes = getRelevantRuleIndexes(rules, qryObj.getFieldNames());
			Map<String, Set<String>> matchIdMap = getMatchIds(qryObj, candidateIds, rules, relevantRuleIndexes, connId);
			JsonObject results = new JsonObject();
			results.putArray("match-ids", new JsonArray(new ArrayList<String>(matchIdMap.get("match"))));
			results.putArray("suspect-ids", new JsonArray(new ArrayList<String>(matchIdMap.get("suspect"))));
			results.putString("cache-id", cacheId);
			msg.reply(results);
		}
	};
		
	private Set<String> getCandidateIds(JsonObject qryObj, String cacheId, String connId) {
		Set<String> allCandidateIds = new HashSet<String>();
		for ( String att : qryObj.getFieldNames() ) {
			BlockingIndex bi = biMap.get(att);
			Set<String> attCIds = new HashSet<String>();
			Set<String> keyCIds = new HashSet<String>();
			//What about if array values are not Strings ?
			for ( Object attValue : qryObj.getArray(att) ) {
				ValueCache vc = new ValueCache(cacheId, att, (String)attValue);
				if ( candidateCache.containsKey(vc) ) {
					attCIds.addAll(candidateCache.get(vc));
					vc.setLastAccessedTime();
				} else {
					String baseKey = bi.getBaseKeyGenerator().generateBaseKey((String)attValue);
					Set<String> keys = bi.getKeyGenerator().generateKeys(baseKey);
					for ( String key : keys ) {
						if ( !key.equals("") ) {
							keyCIds = bi.getBlockingMethod().queryIndex(indexAPI, att, key, connId);
							attCIds.addAll(keyCIds);
						}
					}
					candidateCache.put(new ValueCache(cacheId, att, (String)attValue), attCIds);
				}
			}
			//Returns intersection of candidate Id sets returned from each attribute
			if ( allCandidateIds.isEmpty() ) {
				allCandidateIds.addAll(attCIds);
			} else {
				allCandidateIds.retainAll(attCIds);
			}
		}
		return allCandidateIds;
	}
		
	private Map<String, Set<String>> getMatchIds(JsonObject qryObj, Set<String> candidateIds, JsonArray rules, Set<Integer> ruleIndexes, String connId) {
		Map<String, Set<String>> matchIdsMap = new HashMap<String, Set<String>>();
		Set<String> matchIds = new HashSet<String>();
		Set<String> suspectIds = new HashSet<String>();
		for ( int n=0; n<rules.size(); n++ ) {
			if ( ruleIndexes.contains(n) ) {
				JsonObject rule = rules.get(n);
				String ruleType = rule.getString("type");
				for ( String att : qryObj.getFieldNames() ) {
					JsonObject attRule = rule.getObject("comparators").getObject(att);
					String compConfigPath = "rules[".concat(Integer.toString(n)).concat("].comparators.").concat(att);
					Comparator comparator = (Comparator) Utils.getInstance(config, compConfigPath);
					double threshold = attRule.getNumber("threshold").doubleValue();
					JsonArray queryValues = qryObj.getArray(att);
					for ( int k=0; k<queryValues.size(); k++ ) {
						String queryValue = queryValues.get(k);
						for ( String cId : candidateIds ) {
							Set<String> candidateValues = dataAPI.getValuesById(att, cId, connId); //Can cache these too
							double simScore = 0; 
							double maxSimScore = 0;
							for ( String cValue : candidateValues ) {
								simScore = comparator.score(queryValue, cValue);
								maxSimScore = (simScore > maxSimScore) ? simScore : maxSimScore;
								System.out.println("candidate: " + cValue + ", query: " + queryValue);
								System.out.println(comparator.explainScore(queryValue, cValue));
							}
							if ( maxSimScore >= threshold ) {
								if ( ruleType.equals("match") ){
									matchIds.add(cId);
								} else if ( ruleType.equals("suspect") ) {
									suspectIds.add(cId);
								} else {
									// Invalid rule type
								}
							}
						}
					}
				}
			}
		}
		matchIdsMap.put("match", matchIds);
		matchIdsMap.put("suspect", suspectIds);
		return matchIdsMap;
	}
	
	private Set<Integer> getRelevantRuleIndexes(JsonArray rules, Set<String> queryAttributes) {
		Set<Integer> relRuleIndexes = new HashSet<Integer>();
		for ( int n=0; n<rules.size(); n++ ) {
			Set<String> ruleAtts = ((JsonObject) rules.get(n)).getObject("comparators").getFieldNames();
			if ( ruleAtts.equals(queryAttributes) ) {
				relRuleIndexes.add(n);
			} 
		}
		return relRuleIndexes;
	}
}

