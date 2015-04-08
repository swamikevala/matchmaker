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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.commons.jcs.JCS;
import org.apache.commons.jcs.access.CacheAccess;
import org.apache.commons.jcs.access.exception.CacheException;

import org.ishafoundation.matchmaker.api.DataSourceAPI;
import org.ishafoundation.matchmaker.api.IndexAPI;
import org.ishafoundation.matchmaker.blocking.BlockingIndex;
import org.ishafoundation.matchmaker.blocking.method.BlockingMethod;
import org.ishafoundation.matchmaker.blocking.keygen.KeyGenerator;
import org.ishafoundation.matchmaker.blocking.basekeygen.BaseKeyGenerator;
import org.ishafoundation.matchmaker.Comparator;
import org.ishafoundation.matchmaker.Metric;
import org.ishafoundation.matchmaker.scoring.ScoringMethod;
import org.ishafoundation.matchmaker.QueryResult;
import org.ishafoundation.matchmaker.cache.ValueCache;
import org.ishafoundation.matchmaker.cache.IdAttributeCache;
import org.ishafoundation.matchmaker.cache.ScoreCache;
import org.ishafoundation.matchmaker.ConfigLoader;

public class MatchManager extends Verticle {

	private ConfigLoader configLoader;
	private Config config;
	private Set<String> blockingAttributes;
	private Map<String, Metric> metricMap;
	private ScoringMethod scoringMethod;
	private Map<String, BlockingIndex> biMap;
	private IndexAPI indexAPI;
	private DataSourceAPI dataAPI;
	private Map<ValueCache, Set<String>> candidateCache;
	private Map<IdAttributeCache, Set<String>> dataCache;
	private Map<ScoreCache, Double> matchingCache;
	private EventBus eb;

	public void start() {
	
		//try {
			//CacheAccess<String, String> c = JCS.getInstance( "testCache" );
			//setCache( c );
		//} catch ( CacheException e ) {
		//	System.out.println("Problem initializing cache");
		//}

		String configStr = container.config().toString();
		
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(Config.class, new ConfigLoader());
		Gson gson = gsonBuilder.create();
		Config config = gson.fromJson(configStr, Config.class);
		
		metricMap = config.getMetricMap();
		scoringMethod = config.getScoringMethod();
		biMap = config.getBIMap();
		indexAPI = config.getIndexAPI();
		dataAPI = config.getDataSourceAPI();
		
		eb = vertx.eventBus();
		eb.registerHandler("query", queryHandler);
		eb.registerHandler("update", updateHandler);
		
		String icId = indexAPI.openSustainedConnection();
		String dcId = dataAPI.openSustainedConnection();
		
		blockingAttributes = config.getBIMap().keySet();
		for ( String bAtt : blockingAttributes ) {
			if ( !indexAPI.indexExists(bAtt, icId) ) {
				System.out.println("Creating " + bAtt + " index");
				indexAPI.createIndex(bAtt, icId);
				populateIndex(indexAPI, bAtt, icId, dcId);
			}
		}
		indexAPI.closeSustainedConnection(icId);
		dataAPI.closeSustainedConnection(dcId);
		//candidateCache = new HashMap<ValueCache, Set<String>>();
		//dataCache = new HashMap<IdAttributeCache, Set<String>>();
		//matchingCache = new HashMap<ScoreCache, Double>();
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
		
	Handler<Message<JsonObject>> updateHandler = new Handler<Message<JsonObject>>() {
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
		
	Handler<Message<JsonObject>> queryHandler = new Handler<Message<JsonObject>>() {
		public void handle(Message<JsonObject> msg) {	
			
			String icId = indexAPI.openSustainedConnection();
			String dcId = dataAPI.openSustainedConnection();
			
			JsonObject qryObj = msg.body().getObject("attributes");
			
			//Do we need to specify connection ids for match queries?? Maybe for batch matching...
			String conId = (msg.body().containsField("connection-id")) ? msg.body().getString("connection-id") : "";
			String cacheId = "";
			if ( ! msg.body().containsField("cache-id") ) {
				cacheId = UUID.randomUUID().toString();
			} else {
				cacheId = msg.body().getString("cache-id");
			}
			
			Set<String> idSetUnion = new HashSet<String>();
			for ( String att : qryObj.getFieldNames() ) {
				idSetUnion.addAll(getCandidateIdSet(qryObj, att, cacheId, icId));
			}

			List<QueryResult> results = getResults(qryObj, idSetUnion, cacheId, dcId);
			JsonArray jScores = new JsonArray();
			
			for (QueryResult queryResult : results ) {
				JsonObject jScore = new JsonObject();
				jScore.putString("id", queryResult.getId());
				jScore.putNumber("score", (Number) scoringMethod.score(queryResult));
				jScores.add(jScore);
			}
			msg.reply(jScores);
			
			indexAPI.closeSustainedConnection(icId);
			dataAPI.closeSustainedConnection(dcId);
		}
	};
		
	private List<QueryResult> getResults(JsonObject qryObj, Set<String> candidateIds, String cacheId, String dcId) {
		List<QueryResult> results = new ArrayList<QueryResult>();
		Metric metric;
		Comparator comparator;
		double simScore; 
		double maxSimScore;
		List<String> queryValues;
		Set<String> candidateValues;
		for ( String cId : candidateIds ) {
			Map<String, Double> scores = new HashMap<String, Double>();
			for ( String attribute : qryObj.getFieldNames() ) {
				metric = metricMap.get(attribute);
				comparator = metric.getComparator();
				simScore = 0; 
				maxSimScore = 0;
				queryValues = (List<String>) qryObj.getArray(attribute).toList();
				
				//IdAttributeCache iac = new IdAttributeCache(cacheId, cId, attRule.getAttribute());
					//if ( dataCache.containsKey(iac) ) {
						//System.out.println("cached: " + cId);
						//candidateValues = dataCache.get(iac);
					//} else {
						//System.out.println("not-cached: " + cId);
				candidateValues = dataAPI.getValuesById(attribute, cId, dcId);
						//dataCache.put(new IdAttributeCache(cacheId, cId, attRule.getAttribute()), candidateValues);
					//}
				
				for ( String candidateValue : candidateValues ) {
				
					if ( candidateValue != null && !candidateValue.equals("") ) {
						for ( String queryValue : queryValues ) {
							//ScoreCache sc = new ScoreCache(cacheId, attRule.getAttribute(), attRule.getComparator().toString(), queryValue, candidateValue);
							//if ( matchingCache.containsKey(sc) ) {
								//simScore = matchingCache.get(sc);
								//sc.setLastAccessedTime();
							//} else {
								
							simScore = comparator.score(queryValue, candidateValue);
								//matchingCache.put(new ScoreCache(cacheId, attRule.getAttribute(), attRule.getComparator().toString(), queryValue, candidateValue), simScore);
							//}
							maxSimScore = (simScore > maxSimScore) ? simScore : maxSimScore;
						}
					}
				}
				if ( candidateValues.size() > 0 ) {
					scores.put(attribute, maxSimScore);
				}
			}
			for ( Map.Entry<String, Double> score : scores.entrySet() ) {
				Metric m = metricMap.get(score.getKey());
				if ( score.getValue() >= m.getThreshold() ) {
					results.add(new QueryResult(cId, scores));
					break;
				}
			}
		}
		return results;
	}	
		
	private Set<String> getCandidateIdSet(JsonObject qryObj, String att, String cacheId, String icId) {
		Set<String> candidateIdSet = new HashSet<String>();
		BlockingIndex bi = biMap.get(att);
		//What about if array values are not Strings ?
		for ( String attValue : (List<String>) qryObj.getArray(att).toList() ) {
			//ValueCache vc = new ValueCache(cacheId, att, attValue);
			//if ( candidateCache.containsKey(vc) ) {
			//	candidateIdSet.addAll(candidateCache.get(vc));
			//	vc.setLastAccessedTime();
			//} else {
				String baseKey = bi.getBaseKeyGenerator().generateBaseKey(attValue);
				Set<String> keys = bi.getKeyGenerator().generateKeys(baseKey);
				for ( String key : keys ) {
					Set<String> keyCandidateIds = new HashSet<String>();
					if ( !key.equals("") ) {
						keyCandidateIds = bi.getBlockingMethod().queryIndex(indexAPI, att, key, icId);
						candidateIdSet.addAll(keyCandidateIds);
					}
				//}
				//candidateCache.put(new ValueCache(cacheId, att, attValue), candidateIdSet);
			}
		}
		return candidateIdSet;
	}
		
}

	