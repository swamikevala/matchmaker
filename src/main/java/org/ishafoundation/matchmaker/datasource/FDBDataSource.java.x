package org.ishafoundation.matchmaker.datasource;

import org.vertx.java.platform.Verticle;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;

import com.foundationdb.FDB;
import com.foundationdb.Database;

import org.ishafoundation.matchmaker.IndexAPI;
import org.ishafoundation.matchmaker.DataAPI;

public class FDBDataSource extends Verticle implements IndexAPI, DataAPI {

	final int fdbApiVersion = 300;
	static FDB fdb;
	static Database db;
	String blockingIndexName = "idx_1";
	DirectorySubspace blockingIndex;
	
	public void start()
	
		fdb = FDB.selectAPIVersion(fdbApiVersion);
		db = fdb.open();
		final EventBus eb = vertx.eventBus();
		//For multiple indexes, can get index names from config and create array
		blockingIndex = DirectoryLayer.getDefault().createOrOpen(db, PathUtil.from("blocking_idx", blockingIndexName)).get();
		eb.registerHandler("database.index.operations", indexQueryExecutor);
		//eb.registerHandler("database.data.operations", dataQueryExecutor);
	}
	
	Handler<Message<JsonObject>> indexQueryExecutor = new Handler<Message<JsonObject>>() {
		public void handle(Message<JsonObject> msg) {	
		
			String operation = msg.body().getString("operation");
			//Currently only able to pass a single parameter
			//Will need to make this more general 
			String parameter = msg.body().getString("parameter"); 
		
			if ( operation == "getIdsByKey" ) {
				Set<String> ids = getIdsByKey(parameter);
				JsonArray jIds = Utils.stringSetToJsonArray(ids);
				msg.reply(jIds);
			}
		}
	};
	
	public Set<String> getIdsByKey(final String key) {
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
	
	