package org.ishafoundation.matchmaker.blocking.basekeygen;

import org.vertx.java.core.json.JsonObject;

public class IndianNameNormalizeBKG extends AbstractBaseKeyGenerator implements BaseKeyGenerator {

	private final JsonObject params;

	public IndianNameNormalizeBKG(JsonObject params) {
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
		newValue = indianNameNormalize(newValue);
		newValue.replaceAll(" ", "");
		return newValue;
	}

	private String indianNameNormalize(String s) {
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
		s = s.replace("wi", "vi");
		return s;
	}
}