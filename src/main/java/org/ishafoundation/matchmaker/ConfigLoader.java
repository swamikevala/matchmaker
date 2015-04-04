package org.ishafoundation.matchmaker;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import javax.naming.ConfigurationException;

import org.vertx.java.platform.Container;
import org.vertx.java.core.json.JsonObject;

import org.ishafoundation.matchmaker.matching.Rule;

public class ConfigLoader {

	private Container container;
	private JsonObject config;
	
	public ConfigLoader(Container container) {
		this.container = container;
		this.config = container.config();
	}

	public List<Rule> loadRules() {
		List<Rule> ruleObjs = new ArrayList<Rule>();
		List<Map> rules = (List<Map>) (config.getArray("rules").toList());
		for ( Map rule : rules ) {
			try {
				ruleObjs.add(new Rule(config, rules.indexOf(rule)));
			} catch (ConfigurationException e) {
				System.out.println(e.getMessage());
				container.exit();
			}
		}
		return ruleObjs;
	}
	
}