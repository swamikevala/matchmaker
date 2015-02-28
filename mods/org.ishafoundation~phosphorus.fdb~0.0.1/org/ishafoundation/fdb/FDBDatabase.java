package org.ishafoundation.fdb;

import org.vertx.java.platform.Verticle;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonArray;

import com.foundationdb.FDB;
import com.foundationdb.Database;

public class FDBDatabase extends Verticle {

	private static final FDB fdb;
	private static final Database db;
	
	static {
		fdb = FDB.selectAPIVersion(300);
		db = fdb.open();
	}
	
	public void start() {
		EventBus eb = vertx.eventBus();
		final Index index = new Index(db, "idx_1");
	
		Handler<Message> queryHandler = new Handler<Message>() {
			public void handle(Message msg) {
				String name = msg.body().toString();
				JsonArray jsIds = new JsonArray(index.getIdsByKey(db, name));
				msg.reply(jsIds);
			}
		};
		eb.registerHandler("me.index_query", queryHandler);
	}
	
};

	