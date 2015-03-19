package org.ishafoundation.phosphorus.blocking;

public class BaseKey {

	private String value;
	private JsonObject config;

	public BaseKey(String value) {
		this(value, null);
	}
	
	public BaseKey(String value, JsonObject config) {
		this.value = value;
		if ( config.getBoolean("sort-tokens") ) {
			sortTokens();
		}
		this.value.replaceAll(" ", "");
	}
	
	public String getValue() {
		return value;
	}
	
	public int getLength() {
		return value.length();
	}
	
	private void sortTokens() {
		StringBuilder builder = new StringBuilder();
		String [] words = value.split("[\\s]");
		Arrays.sort(words);
		for(String w : words) {
			builder.append(w);
		}
		this.value = builder.toString();
	}
	
}

