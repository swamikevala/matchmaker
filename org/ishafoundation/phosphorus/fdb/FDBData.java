package org.ishafoundation.phosphorus.fdb;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentMap;

import org.vertx.java.platform.Verticle;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonArray;

import com.foundationdb.Database;
import com.foundationdb.Transaction;
import com.foundationdb.TransactionContext;
import com.foundationdb.KeyValue;
import com.foundationdb.tuple.Tuple;
import com.foundationdb.directory.DirectoryLayer;
import com.foundationdb.directory.DirectorySubspace;
import com.foundationdb.directory.PathUtil;
import com.foundationdb.async.Function;

import org.ishafoundation.phosphorus.fdb.FDBDatabase;
import org.ishafoundation.phosphorus.Data;

public class FDBData extends Verticle implements Data {
	
	FDBDatabase fdbDatabase;
	Database db;
	String dataName = "contact";
	DirectorySubspace ds;
	
	public void start() {
	
		ConcurrentMap<String, FDBDatabase> shared = vertx.sharedData().getMap("shared-map");
		fdbDatabase = shared.get("FDB");
		db = fdbDatabase.getInstance();
		
		final EventBus eb = vertx.eventBus();
		//Can get data directory details from config
		ds = DirectoryLayer.getDefault().createOrOpen(db, PathUtil.from("data", dataName)).get();
		eb.registerHandler("data.query", dsQueryHandler);
		
	}

	Handler<Message<JsonArray>> dsQueryHandler = new Handler<Message<JsonArray>>() {
		public void handle(Message<JsonArray> msg) {
			//Loop thru all candidate ids and perform matching
			//for(String candidateId : msg.body()) {
				//List<String> matchIds = getIdsByKey(msg.body().toString());
				//JsonArray jsonCandidateIds = new JsonArray(candidateIds);
				//msg.reply(jsonCandidateIds);
			//}
		}
	};
	
	
	
	
}

