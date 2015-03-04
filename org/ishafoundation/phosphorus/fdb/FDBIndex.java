package org.ishafoundation.phosphorus.fdb;

import java.lang.Object;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentMap;

import org.vertx.java.platform.Verticle;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;
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
import org.ishafoundation.phosphorus.Index;

public class FDBIndex extends Verticle implements Index {

	FDBDatabase fdbDatabase;
	Database db;
	String indexName = "idx_1";
	DirectorySubspace index;
	
	public void start() {

		ConcurrentMap<String, FDBDatabase> shared = vertx.sharedData().getMap("shared-map");
		fdbDatabase = shared.get("FDB");
		db = fdbDatabase.getInstance();
		final EventBus eb = vertx.eventBus();
		//For multiple indexes, can get index names from config and create array
		index = DirectoryLayer.getDefault().createOrOpen(db, PathUtil.from("blocking_idx", indexName)).get();
		eb.registerHandler("index.query", indexQueryHandler);
		
	}

	Handler<Message> indexQueryHandler = new Handler<Message>() {
		public void handle(Message msg) {	
			List<String> candidateIds = getIdsByKey(msg.body().toString());
			JsonArray jsonCandidateIds = new JsonArray(candidateIds);
			msg.reply(jsonCandidateIds);
		}
	};
	
	public List<String> getIdsByKey(final String key) {
		return db.run(new Function<Transaction,List<String>>() {
			@Override
			public List<String> apply(Transaction tr) {
				ArrayList<String> ids = new ArrayList<String>(); 
				for(KeyValue kv : tr.getRange(index.range(Tuple.from(key)))){
					ids.add(Tuple.fromBytes(kv.getKey()).getString(2));
				}
				return ids;
			}
		});
	}
	
	
}

