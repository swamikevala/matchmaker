package org.ishafoundation.matchmaker;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.lang.reflect.Type;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import javax.naming.ConfigurationException;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import org.ishafoundation.matchmaker.Comparator;
import org.ishafoundation.matchmaker.Metric;
import org.ishafoundation.matchmaker.scoring.ScoringMethod;
import org.ishafoundation.matchmaker.blocking.BlockingIndex;
import org.ishafoundation.matchmaker.blocking.method.BlockingMethod;
import org.ishafoundation.matchmaker.blocking.keygen.KeyGenerator;
import org.ishafoundation.matchmaker.blocking.basekeygen.BaseKeyGenerator;
import org.ishafoundation.matchmaker.Comparator;
import org.ishafoundation.matchmaker.api.IndexAPI;
import org.ishafoundation.matchmaker.api.DataSourceAPI;


public class ConfigLoader implements JsonDeserializer<Config> {

	private static String base;
	private static Map<String, String> fqcns;

	@Override
	public Config deserialize(final JsonElement json, final Type typeOfT, final JsonDeserializationContext context) throws JsonParseException {

		JsonObject jConfig = json.getAsJsonObject(); 
	
		base = jConfig.get("base").getAsString();
	
		fqcns = new HashMap<String, String>();
		final JsonObject jClasses = jConfig.get("classes").getAsJsonObject();
		for ( Map.Entry<String, JsonElement> entry : jClasses.entrySet() ) {
			fqcns.put(entry.getKey(), entry.getValue().getAsString());
		}
		
		final JsonObject jMetrics = jConfig.get("metrics").getAsJsonObject();
		final Map<String, Metric> metricMap = new HashMap<String, Metric>();
		for( Map.Entry<String, JsonElement> jMetric : jMetrics.entrySet() ) {
			Comparator comparator = (Comparator) getInstance(jMetric.getValue().getAsJsonObject().get("comparator-class").getAsJsonObject());
			double threshold = jMetric.getValue().getAsJsonObject().get("threshold").getAsDouble();
			metricMap.put(jMetric.getKey(), new Metric(comparator, threshold));
		}
		
		final ScoringMethod scoringMethod = (ScoringMethod) getInstance(jConfig.get("scoring-method-class").getAsJsonObject());
		
		final JsonObject jBlockingIndexes = jConfig.get("blocking-indexes").getAsJsonObject();
		final Map<String, BlockingIndex> biMap = new HashMap<String, BlockingIndex>();
		for( Map.Entry<String, JsonElement> jBI : jBlockingIndexes.entrySet() ) {
			BaseKeyGenerator baseKeyGen = (BaseKeyGenerator) getInstance(jBI.getValue().getAsJsonObject().get("base-keygen-class").getAsJsonObject());
			KeyGenerator keyGen = (KeyGenerator) getInstance(jBI.getValue().getAsJsonObject().get("keygen-class").getAsJsonObject());
			BlockingMethod method = (BlockingMethod) getInstance(jBI.getValue().getAsJsonObject().get("method-class").getAsJsonObject());
			biMap.put(jBI.getKey(), new BlockingIndex(baseKeyGen, keyGen, method));
		}
		
		final IndexAPI indexAPI = (IndexAPI) getInstance(jConfig.get("index-api-class").getAsJsonObject());;
		final DataSourceAPI dataSourceAPI = (DataSourceAPI) getInstance(jConfig.get("data-source-api-class").getAsJsonObject());
		
		final Config config = new Config();
		config.setMetricMap(metricMap);
		config.setBIMap(biMap);
		config.setScoringMethod(scoringMethod);
		config.setIndexAPI(indexAPI);
		config.setDataSourceAPI(dataSourceAPI);
		return config;
	}
	
	public static Object getInstance(JsonObject json) {
	
		String name = json.get("name").getAsString();
		String fqcn = fqcns.get(name).replace("$base", base);
		JsonObject jParams = null;
		if ( json.has("params") ) {
			jParams = json.get("params").getAsJsonObject();
		}
		return instantiate(fqcn, jParams);
	}
	
	private static Object instantiate(String fqcn, JsonObject params) {
	
		Object obj = new Object();
		try {
			Class klass = Class.forName(fqcn);
			if ( params != null ) {
				obj = klass.getConstructor(JsonObject.class).newInstance(params);
			} else {
				obj = klass.getConstructor().newInstance();
			}

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return obj;
	}

}