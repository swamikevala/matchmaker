package org.ishafoundation.phosphorus;

import java.util.concurrent.ConcurrentMap;
import java.util.List;
import java.util.Arrays;

import org.vertx.java.platform.Verticle;
import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.Handler;
import org.vertx.java.core.VoidHandler;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.AsyncResultHandler;

import org.ishafoundation.phosphorus.fdb.FDBDatabase;

public class QueryServer extends Verticle {

	final String databaseClass = "org.ishafoundation.phosphorus.fdb.FDBDatabase";
	final String indexClass = "org.ishafoundation.phosphorus.fdb.FDBIndex"; //can pass these class names in via config file
	final String dataClass = "org.ishafoundation.phosphorus.fdb.FDBData";
	
	public void start() {
		FDBDatabase database = new FDBDatabase();
		
		ConcurrentMap<String, FDBDatabase> shared = vertx.sharedData().getMap("shared-map");
		shared.put("FDB", database);
		
		container.deployWorkerVerticle(indexClass); //Handles index reads / writes
		container.deployWorkerVerticle(dataClass);  //Handles data reads
		
		final EventBus eb = vertx.eventBus();
		final HttpServer server = vertx.createHttpServer();
		
		server.requestHandler(new Handler<HttpServerRequest>() {
			public void handle(final HttpServerRequest request) {
			
				final Buffer body = new Buffer(0);
				
				request.dataHandler(new Handler<Buffer>() {
					public void handle(Buffer buffer) {
						body.appendBuffer(buffer);
					}
				});
				request.endHandler(new VoidHandler() {
					public void handle() {
						final String name = body.getString(0, body.length());
						String key = makeKey(name);
						eb.send("index.query", key, new Handler<Message<JsonArray>>() {
							public void handle(Message<JsonArray> candidateIdsMsg) {
								JsonObject nameAndCIds = new JsonObject().putString("name", name);
								nameAndCIds.putArray("candidateIds", candidateIdsMsg.body());
								eb.send("data.query", nameAndCIds, new Handler<Message<JsonArray>>() {
									public void handle(Message<JsonArray> matchIdsMsg) { 
										request.response().end(matchIdsMsg.body().toString());
									}
								});
							}
						});
					}
				});
			}
		}).listen(8080, "localhost");
	}
	
	String antiVowel(String s) {
		return s.replaceAll("[AEIOUHYaeiouhy\\s+]", "");
	}

	String sortAlpha(String s) {
		char[] letters = s.toCharArray(); 
		Arrays.sort(letters); 
		return new String(letters); 
	}
    
	String makeKey(String s) {
		return sortAlpha(antiVowel(s));
	}
	
}