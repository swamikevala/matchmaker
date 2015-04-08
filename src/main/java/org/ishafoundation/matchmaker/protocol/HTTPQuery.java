package org.ishafoundation.matchmaker.protocol;

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
import org.vertx.java.core.json.DecodeException;
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
						String input = body.getString(0, body.length());
						JsonObject jInput = null;
						try {
							jInput = new JsonObject(input);
						} catch (DecodeException e) {
							request.response().end("error: invalid json request" );
						}
						if ( jInput != null ) {
							String mode = jInput.getString("mode");
							if ( mode.equals("query") ) {
								eb.send("query", jInput, new Handler<Message<JsonArray>>() {
									public void handle(Message<JsonArray> resultsMsg) { 
										request.response().end(resultsMsg.body().toString());
									}
								});
							} else if ( mode.equals("update") ) {
								eb.send("update", jInput, new Handler<Message<JsonObject>>() {
									public void handle(Message<JsonObject> indexMsg) {
										request.response().end(indexMsg.body().toString());
									}
								});
							}
						}
					}
				});
			}
		}).listen(8080, "localhost"); //.listen(config.getInteger("port"), config.getString("url"));
	}
	
}