package org.ishafoundation.matchmaker.blocking.basekeygen;

import java.util.Arrays;

import com.google.gson.JsonObject;

public class AbstractBaseKeyGenerator {

	private String baseKey;
	private JsonObject params;
	private boolean makeLowerCaseFlag;
	private boolean removeCharsFlag;
	private String removeCharsRegexp;
	private boolean sortTokensFlag;
	
	protected AbstractBaseKeyGenerator(JsonObject params) {
	
		makeLowerCaseFlag = ( params.has("lower-case") && params.get("lower-case").getAsBoolean() )? true : false;
		removeCharsFlag = params.has("remove-chars") ? true : false;
		removeCharsRegexp = params.has("remove-chars") ? params.get("remove-chars").getAsString() : "";
		sortTokensFlag = params.has("sort-tokens") && params.get("sort-tokens").getAsBoolean()? true : false;
	}
	
	protected String applyTransformations(String value) {
	
		String newValue = value;
		if ( makeLowerCaseFlag ) {
			newValue = makeLowerCase(value);
		}
		if ( removeCharsFlag ) {
			newValue = removeChars(newValue, removeCharsRegexp);
		}
		if ( sortTokensFlag ) {
			newValue = sortTokens(newValue);
		}
		return newValue;
	}

	protected String sortTokens(String value) {
		StringBuilder builder = new StringBuilder();
		//what if no spaces - null pointer exception!
		String [] words = value.split("[\\s]");
		Arrays.sort(words);
		for(String w : words) {
			builder.append(w);
		}
		return builder.toString();
	}

	protected String removeChars(String value, String regexp) {
		return value.replaceAll(regexp, "");
	}
	
	protected String makeLowerCase(String value) {
		return value.toLowerCase();
	}
}