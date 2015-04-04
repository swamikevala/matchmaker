package org.ishafoundation.matchmaker;

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

import org.ishafoundation.matchmaker.api.DataSourceAPI;
import org.ishafoundation.matchmaker.api.IndexAPI;
import org.ishafoundation.matchmaker.blocking.BlockingIndex;
import org.ishafoundation.matchmaker.blocking.method.BlockingMethod;
import org.ishafoundation.matchmaker.blocking.method.DefaultBlockingMethod;
import org.ishafoundation.matchmaker.blocking.keygen.KeyGenerator;
import org.ishafoundation.matchmaker.blocking.basekeygen.BaseKeyGenerator;
import org.ishafoundation.matchmaker.comparators.Comparator;
import org.ishafoundation.matchmaker.matching.Rule;
import org.ishafoundation.matchmaker.matching.AttributeRule;
import org.ishafoundation.matchmaker.cache.ValueCache;
import org.ishafoundation.matchmaker.cache.IdAttributeCache;
import org.ishafoundation.matchmaker.cache.ScoreCache;
import org.ishafoundation.matchmaker.Utils;
import org.ishafoundation.matchmaker.ConfigLoader;

public class MatchManager extends Verticle {

	private JsonObject config;
	private ConfigLoader configLoader;
	private Set<String> blockingAttributes;
	private Map<String, BlockingIndex> biMap;
	private List<Rule> rules;
	private IndexAPI indexAPI;
	private DataSourceAPI dataAPI;
	private Map<ValueCache, Set<String>> candidateCache;
	private Map<IdAttributeCache, Set<String>> dataCache;
	private Map<ScoreCache, Double> matchingCache;
	private EventBus eb;

	public void start() {
	
		config = container.config();
		configLoader = new ConfigLoader(container);
		rules = configLoader.loadRules();
		biMap = new HashMap<String, BlockingIndex>();
		//put this stuff in configloader class
		dataAPI = (DataSourceAPI) Utils.getInstance(config, "data-source-api"); 
		indexAPI = (IndexAPI) Utils.getInstance(config, "index-api");
		
		eb = vertx.eventBus();
		eb.registerHandler("match", matchHandler);
		eb.registerHandler("index", indexHandler);
		
		String icId = indexAPI.openSustainedConnection();
		String dcId = dataAPI.openSustainedConnection();
		
		blockingAttributes = new HashSet<String>();
		for ( Rule rule : rules ) {
			blockingAttributes.addAll(getBlockingAttributes(config, rule));
		}
		for ( String bAtt : blockingAttributes ) {
			biMap.put(bAtt, new BlockingIndex(config, bAtt));
			if ( !indexAPI.indexExists(bAtt, icId) ) {
				System.out.println("Creating " + bAtt + " index");
				indexAPI.createIndex(bAtt, icId);
				populateIndex(indexAPI, bAtt, icId, dcId);
			}
		}
		indexAPI.closeSustainedConnection(icId);
		dataAPI.closeSustainedConnection(dcId);
		candidateCache = new HashMap<ValueCache, Set<String>>();
		dataCache = new HashMap<IdAttributeCache, Set<String>>();
		matchingCache = new HashMap<ScoreCache, Double>();
	}
		
	private Set<String> getBlockingAttributes(JsonObject config, Rule rule) {
		Set<String> bAtts = new HashSet();
		for ( AttributeRule attRule : rule.getAttributeRules() ) {
			if ( attRule.getBlocking() ) {
				bAtts.add(attRule.getAttribute());
			}
		}
		return bAtts;
	}
		
	private void populateIndex(IndexAPI indexAPI, String attribute, String icId, String dcId) {
		Set<String> allIds = dataAPI.getAllIds(dcId);
		int count = 0;
		for ( String id : allIds ) {
			JsonObject jo = new JsonObject();
			jo.putString("id", id);
			jo.putString("index-connection-id", icId);
			jo.putString("data-connection-id", dcId);
			updateIndex(indexAPI, jo, attribute);
			count++;
			if ( count % 1000 == 0 ) { 
				System.out.println(count + " records added");
			}
		}
		System.out.println("Total " + count + " records added");
	}
		
	private void updateIndex(IndexAPI indexAPI, JsonObject jo, String att) {
		String id = jo.getString("id");
		String icId = (jo.containsField("index-connection-id")) ? jo.getString("index-connection-id") : "";
		String dcId = (jo.containsField("data-connection-id")) ? jo.getString("data-connection-id") : "";
		BlockingIndex bi = biMap.get(att);
		Set<String> values = dataAPI.getValuesById(att, id, dcId);
		for ( String value : values ) {
			if ( !(value.equals("") || value == null) ) {
				String baseKey = bi.getBaseKeyGenerator().generateBaseKey(value);
				Set<String> keys = bi.getKeyGenerator().generateKeys(baseKey);
				for ( String key : keys ) {
					if ( !(key.equals("") || key == null) ) {
						bi.getBlockingMethod().updateIndex(indexAPI, att, key, baseKey, id, icId);
					}
				}
			}
		}
	}
		
	Handler<Message<JsonObject>> indexHandler = new Handler<Message<JsonObject>>() {
		public void handle(Message<JsonObject> msg) {
			for ( String att : blockingAttributes ) {
				updateIndex(indexAPI, msg.body(), att);
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
			
			String icId = indexAPI.openSustainedConnection();
			String dcId = dataAPI.openSustainedConnection();
			
			Map<String, String> matchIdMap = new HashMap<String, String>();
			JsonObject qryObj = msg.body().getObject("attributes");
			
			//Do we need to specify connection ids for match queries?? Maybe for batch matching...
			String conId = (msg.body().containsField("connection-id")) ? msg.body().getString("connection-id") : "";
			String cacheId = "";
			if ( ! msg.body().containsField("cache-id") ) {
				cacheId = UUID.randomUUID().toString();
			} else {
				cacheId = msg.body().getString("cache-id");
			}
			List<Rule> relevantRules = getRelevantRules(rules, qryObj);
			Set<String> matches = new HashSet<String>();
			Set<String> suspects = new HashSet<String>();
			
			for ( Rule rule : relevantRules ) {
				Set<String> idSetUnion = new HashSet<String>();
				for ( String bAtt : getBlockingAttributes(config, rule) ) {
					idSetUnion.addAll(getCandidateIdSet(qryObj, bAtt, cacheId, icId));
				}
				Set<String> ruleMatches = getMatches(qryObj, idSetUnion, rule, cacheId, dcId);
				if ( rule.getType().equals("match") ) {
					matches.addAll(ruleMatches);
				} else if ( rule.getType().equals("suspect") ) {
					suspects.addAll(ruleMatches);
				}
			}
			JsonObject results = new JsonObject();
			List<String> matchesList = new ArrayList<String>();
			List<String> suspectsList = new ArrayList<String>();
			matchesList.addAll(matches);
			suspectsList.addAll(suspects);
			results.putArray("match-ids", new JsonArray(matchesList));
			results.putArray("suspect-ids", new JsonArray(suspectsList));
			results.putString("cache-id", cacheId);
			msg.reply(results);
			
			indexAPI.closeSustainedConnection(icId);
			dataAPI.closeSustainedConnection(dcId);
		}
	};
		
	private Set<String> getMatches(JsonObject qryObj, Set<String> candidateIds, Rule rule, String cacheId, String dcId) {
		Set<String> matchIds = new HashSet<String>();
		List<AttributeRule> attRules = rule.getAttributeRules();
		for ( AttributeRule attRule : attRules ) {
			List<String> queryValues = (List<String>) qryObj.getArray(attRule.getAttribute()).toList();
			for ( String queryValue : queryValues ) {
				for ( String cId : candidateIds ) {
					Set<String> candidateValues;
					if ( attRules.indexOf(attRule) == 0 || matchIds.contains(cId) ) {
						//IdAttributeCache iac = new IdAttributeCache(cacheId, cId, attRule.getAttribute());
						//if ( dataCache.containsKey(iac) ) {
							//System.out.println("cached: " + cId);
							//candidateValues = dataCache.get(iac);
						//} else {
							//System.out.println("not-cached: " + cId);
							candidateValues = dataAPI.getValuesById(attRule.getAttribute(), cId, dcId);
							//dataCache.put(new IdAttributeCache(cacheId, cId, attRule.getAttribute()), candidateValues);
						//}
						double simScore = 0; 
						double maxSimScore = 0;
						for ( String candidateValue : candidateValues ) {
							if ( candidateValue != null && candidateValue != "" ) {
								ScoreCache sc = new ScoreCache(cacheId, attRule.getAttribute(), attRule.getComparator().toString(), queryValue, candidateValue);
								if ( matchingCache.containsKey(sc) ) {
									simScore = matchingCache.get(sc);
									sc.setLastAccessedTime();
								} else {
									simScore = attRule.getComparator().score(queryValue, candidateValue);
									matchingCache.put(new ScoreCache(cacheId, attRule.getAttribute(), attRule.getComparator().toString(), queryValue, candidateValue), simScore);
								}
								maxSimScore = (simScore > maxSimScore) ? simScore : maxSimScore;
							}
						}
						if ( maxSimScore >= attRule.getThreshold() ) {
							matchIds.add(cId);
						} else {
							matchIds.remove(cId);
						}	
					}
				}
			}
		}
		return matchIds;
	}	
		
	private Set<String> getCandidateIdSet(JsonObject qryObj, String att, String cacheId, String icId) {
		Set<String> candidateIdSet = new HashSet<String>();
		BlockingIndex bi = biMap.get(att);
		//What about if array values are not Strings ?
		for ( String attValue : (List<String>) qryObj.getArray(att).toList() ) {
			ValueCache vc = new ValueCache(cacheId, att, attValue);
			if ( candidateCache.containsKey(vc) ) {
				candidateIdSet.addAll(candidateCache.get(vc));
				vc.setLastAccessedTime();
			} else {
				String baseKey = bi.getBaseKeyGenerator().generateBaseKey(attValue);
				Set<String> keys = bi.getKeyGenerator().generateKeys(baseKey);
				for ( String key : keys ) {
					Set<String> keyCandidateIds = new HashSet<String>();
					if ( !key.equals("") ) {
						keyCandidateIds = bi.getBlockingMethod().queryIndex(indexAPI, att, key, icId);
						candidateIdSet.addAll(keyCandidateIds);
					}
				}
				candidateCache.put(new ValueCache(cacheId, att, attValue), candidateIdSet);
			}
		}
		return candidateIdSet;
	}
		
	private List<Rule> getRelevantRules(List<Rule> rules, JsonObject qryObj) {
		List<Rule> relRules = new ArrayList<Rule>();
		for ( Rule rule : rules ) {
			if ( rule.getAttributes().equals(qryObj.getFieldNames()) ) {
				relRules.add(rule);
			} 
		}
		return relRules;
	}
}

