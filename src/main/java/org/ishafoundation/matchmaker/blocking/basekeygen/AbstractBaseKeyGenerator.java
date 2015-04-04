package org.ishafoundation.matchmaker.blocking.basekeygen;

import java.util.Arrays;

public class AbstractBaseKeyGenerator {

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