package org.ishafoundation.phosphorus;

import java.utils.Arrays;
import java.lang.StringBuilder;
import java.util.Map;

import org.vertx.java.core.json.JsonObject;

public class Utils {

	

	public static String sortTokens(String s) {
	
		StringBuilder builder = new StringBuilder();
		String [] words = s.split("[\\s]");
		Arrays.sort(words);
		
		for(String w : words) {
			builder.append(w);
		}
		return builder.toString();
	}

	public HashMap<String, String> jsonObjectToStringHashMap(jsonObject jo) {

		HashMap<String, String> newMap;
		Map<String, Object> map = jo.toMap();
		for ( Map.Entry<String, Object> entry : map ) {
			try {
				newMap.put(entry.getKey(), (String) entry.getValue());
			} catch(ClassCastException cce){
				// TODO: handle exception
			}
		}
		return newMap;
	}
	
	public JsonArray stringSetToJsonArray(Set<String> set) {
		JsonArray ja = new JsonArray();
		for ( String str : set ) {
			ja.addString(str);
		}
		return ja;
	}
	
	public Set<String> jsonArrayToStringSet(JsonArray ja) {
		Set<String> newSet;
		List list = ja.toList();
		for ( Object item : list ) {
			try {
				ja.addString((String) item);
			} catch(ClassCastException cce){
				// TODO: handle exception
			}
		}
		return newSet;
	}
}