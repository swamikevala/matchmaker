package org.ishafoundation.phosphorus.protocol;

import java.util.Collection;
import java.util.List;
import java.util.Arrays;
import java.util.HashMap;

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

public class HTTPQuery extends Verticle {
	
	private JsonObject config;
	private EventBus eb;
	private HttpServer server;
	
	public void start() {
		
		config = container.config();
		eb = vertx.eventBus();
		server = vertx.createHttpServer();
		
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
						final String input = body.getString(0, body.length());
						//Need to do lots of validation here
						final JsonObject jInput = new JsonObject(input);
						final String mode = jInput.getString("mode");
						if ( mode.equals("query") ) {
							eb.send("match", jInput, new Handler<Message<JsonObject>>() {
								public void handle(Message<JsonObject> matchIdsMsg) { 
									request.response().end(matchIdsMsg.body().toString());
								}
							});
						} else if ( mode.equals("index") ) {
							eb.send("index", jInput, new Handler<Message<JsonObject>>() {
								public void handle(Message<JsonObject> indexMsg) {
									request.response().end(indexMsg.body().toString());
								}
							});
						}
					}
				});
			}
		}).listen(config.getInteger("port"), config.getString("url"));
	}
	
}