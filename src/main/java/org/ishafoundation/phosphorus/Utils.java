package org.ishafoundation.phosphorus;

import java.utils.Arrays;
import java.lang.StringBuilder;
import java.util.Map;

import org.vertx.java.core.json.JsonObject;

public class Utils {

	public static indianNameNormalize(String s) {
	
		s = s.replace("aa", "a");
		s = s.replace("bb", "b");
		s = s.replace("ee", "i");
		s = s.replace("zh", "l");
		s = s.replace("oo", "u");
		s = s.replace("bh", "b");
		s = s.replace("dh", "d");
		s = s.replace("gh", "g");
		s = s.replace("jh", "j");
		s = s.replace("kh", "k");
		s = s.replace("sh", "s");
		s = s.replace("th", "t");
		s = s.replace("ck", "k");
		s = s.replace("kk", "k");
		s = s.replace("nn", "n");
		s = s.replace("mm", "m");
		s = s.replace("pp", "p");
		s = s.replace("ll", "l");
		s = s.replace("ty", "ti");
		s = s.replace("ot", "od");
		s = s.replace("iya", "ia");
		s = s.replace("ya", "ia");
		s = s.replace("sv", "s");
		s = s.replace("sw", "s");
		s = s.replace("my", "mi");
		return s;
	}

	public static sortWords(String s) {
	
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