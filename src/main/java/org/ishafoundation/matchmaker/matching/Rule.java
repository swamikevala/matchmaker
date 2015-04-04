package org.ishafoundation.matchmaker.matching;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import javax.naming.ConfigurationException;

import org.vertx.java.core.json.JsonObject;

import org.ishafoundation.matchmaker.comparators.Comparator;
import org.ishafoundation.matchmaker.Utils;

public class Rule {

	private String type;
	private Set<String> attributes;
	private List<AttributeRule> attributeRules;
	private int index;
	
	public Rule(JsonObject config, int index) throws ConfigurationException {
		Map rule = ((List<Map>) config.getArray("rules").toList()).get(index);
		JsonObject jRule = new JsonObject(rule); 
		this.type = jRule.getString("type");
		this.attributes = jRule.getObject("comparators").getFieldNames();
		this.index = index;
		attributeRules = new ArrayList<AttributeRule>();
		for ( String attribute : attributes ) { 
			JsonObject jAttRule = jRule.getObject("comparators").getObject(attribute);
			boolean blocking = useForBlocking(config, attribute, jAttRule);
			double threshold = jAttRule.getNumber("threshold").doubleValue();
			Comparator comparator =  instantiateComparator(config, attribute, index);
			attributeRules.add(new AttributeRule(attribute, blocking, comparator, threshold));
		}
	}
	
	private boolean useForBlocking(JsonObject config, String attribute, JsonObject jAttRule) throws ConfigurationException { 
		boolean use = false;
		boolean condition1 = jAttRule.containsField("blocking") && jAttRule.getBoolean("blocking");
		boolean condition2 = attributes.size() == 1;
		boolean condition3 = !jAttRule.containsField("blocking") && config.getObject("blocking").containsField(attribute);
		if ( condition1 || condition2 || condition3 ) {
			if ( !config.getObject("blocking").containsField(attribute) ) {
				throw new ConfigurationException("Blocking configuration not defined for \"" + attribute + "\" attribute");
			} else {
				use = true;
			}
		} 
		return use;
	}
	
	private Comparator instantiateComparator(JsonObject config, String attribute, int index) {
		String cPath = "rules[".concat(Integer.toString(index)).concat("].comparators.").concat(attribute);
		return (Comparator) Utils.getInstance(config, cPath);
	}
	
	public Set<String> getAttributes() {
		return attributes;
	}
	
	public List<AttributeRule> getAttributeRules() {
		return attributeRules;
	}
	
	public String getType() {
		return type;
	}
	
}

