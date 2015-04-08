package org.ishafoundation.matchmaker.blocking.basekeygen;

import com.google.gson.JsonObject;

public class DefaultBaseKeyGenerator extends AbstractBaseKeyGenerator implements BaseKeyGenerator {

	public DefaultBaseKeyGenerator(JsonObject params) {
		super(params);
	}
	//Need to put this logic in abstract class
	public String generateBaseKey(String value) {
	
		String newValue = applyTransformations(value);
		newValue.replaceAll(" ", "");
		return newValue;
	}

}