package org.ishafoundation.phosphorus;

import java.util.Collection;
import java.util.List;
import java.util.Arrays;
import java.util.HashMap;
import java.lang.Thread;

import org.vertx.java.platform.Verticle;
import org.vertx.java.core.Handler;
import org.vertx.java.core.VoidHandler;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.AsyncResultHandler;

public class BatchIndex extends Verticle {
	
	public void start()  {
		try {
			Thread.sleep(5000);
		} catch (Exception e) {
			e.printStackTrace();
		}
		final EventBus eb = vertx.eventBus();
		for (int x=1; x<600000; x++) {
			System.out.println(x);
			eb.send("index", Integer.toString(x), new Handler<Message<Boolean>>() {
				public void handle(Message<Boolean> successMsg) {
					System.out.println(successMsg.body());}
			});
		}
	}
	
}