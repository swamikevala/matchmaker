package org.ishafoundation.phosphorus.query;

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

import org.ishafoundation.phosphorus.Database;

public class HTTPQuery extends Verticle {
	
	public void start() {
		
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
						final String input = body.getString(0, body.length());
						//Need to do lots of validation here
						final JsonObject jsonInput = new JsonObject(input);
						final String mode = jsonInput.getString("mode");
						if ( mode == "match" ) {
							JsonObject attributes = jsonInput.getObject("attributes");
							eb.send("match.manager", attributes, new Handler<Message<JsonArray>>() {
								public void handle(Message<JsonArray> matchIdsMsg) { 
									request.response().end(matchIdsMsg.body().toString());
								}
							});
						} else if ( mode == "index" ) {
							String id = jsonInput.getString("id");
							//To do
						}
					}
				});
			}
		}).listen(8080, "localhost");
	}
	
}