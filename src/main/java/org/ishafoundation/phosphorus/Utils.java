package org.ishafoundation.phosphorus;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.json.JsonElement;

public class Utils {

	public static JsonObject getConfigObject(JsonObject config, String path) {
		JsonObject jo = config;
		String[] objNames = path.split("\\.");
		Pattern arrayIndexPattern = Pattern.compile("([a-zA-Z_0-9\\-]+)\\[(\\d+)\\]$");
		Matcher matcher; 
		for ( int x = 0; x < objNames.length; x++ ) {
			matcher = arrayIndexPattern.matcher(objNames[x]);
			if ( matcher.find() ) {
				JsonElement je = jo.getElement(matcher.group(1));
				if ( je.isArray() ) {
					jo = je.asArray().get(Integer.parseInt(matcher.group(2)));
				} else {
					//Should be an array!
				}
			} else {
				jo = jo.getObject(objNames[x]);
			}
		}
		return jo;
	}
	
	public static Object getInstance(JsonObject config, String path) {
		JsonObject conf = Utils.getConfigObject(config, path);
		String clName = conf.getString("class");
		JsonObject params = conf.containsField("params") ? conf.getObject("params") : new JsonObject("{}");
		return Utils.instantiateConfigClass(config, clName, params);
	}
	
	private static Object instantiateConfigClass(JsonObject config, String className, JsonObject params) {
		Object obj = new Object();
		String fqcn = getConfigClassFQCN(config, className);
		try {
			Class klass = Class.forName(fqcn);
			Constructor konst = klass.getConstructor(JsonObject.class);
			obj = konst.newInstance(params);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return obj;
	}

	public static String getConfigClassFQCN(JsonObject config, String className) {
		String base = config.getString("base");
		JsonObject classes = config.getObject("classes");
		String fqcn = classes.getString(className).replace("$base", base);
		return fqcn;
	}
	
}