package org.ishafoundation.matchmaker.blocking.basekeygen;

import org.vertx.java.core.json.JsonObject;

public class DefaultBaseKeyGenerator extends AbstractBaseKeyGenerator implements BaseKeyGenerator {

	private final JsonObject params;

	public DefaultBaseKeyGenerator(JsonObject params) {
		this.params = params;
	}
	//Need to put this logic in abstract class
	public String generateBaseKey(String value) {
		String newValue = value;
		if ( params.containsField("lower-case") ) {
			newValue = this.makeLowerCase(newValue);
		}
		if ( params.containsField("remove-chars") ) {
			String regexp = params.getString("remove-chars");
			newValue = this.removeChars(newValue, regexp);
		}
		if ( params.containsField("sort-tokens") ) {
			if ( params.getBoolean("sort-tokens") ) {
				newValue = this.sortTokens(newValue);
			}
		}
		newValue.replaceAll(" ", "");
		return newValue;
	}

}