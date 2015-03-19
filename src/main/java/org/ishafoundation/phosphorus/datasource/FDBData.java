package org.ishafoundation.phosphorus.fdb;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentMap;

import org.vertx.java.platform.Verticle;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;

import com.foundationdb.Database;
import com.foundationdb.Transaction;
import com.foundationdb.TransactionContext;
import com.foundationdb.KeyValue;
import com.foundationdb.tuple.Tuple;
import com.foundationdb.directory.DirectoryLayer;
import com.foundationdb.directory.DirectorySubspace;
import com.foundationdb.directory.PathUtil;
import com.foundationdb.async.Function;

import uk.ac.shef.wit.simmetrics.similaritymetrics.Levenshtein;

import org.ishafoundation.phosphorus.fdb.FDBDatabase;
import org.ishafoundation.phosphorus.Data;

public class FDBData extends Verticle implements Data {
	
	FDBDatabase fdbDatabase;
	Database db;
	String dataName = "contact";
	DirectorySubspace data;

	public void start() {
		ConcurrentMap<String, FDBDatabase> shared = vertx.sharedData().getMap("shared-map");
		fdbDatabase = shared.get("FDB");
		db = fdbDatabase.getInstance();
		
		final EventBus eb = vertx.eventBus();
		//Can get data directory details from config
		data = DirectoryLayer.getDefault().createOrOpen(db, PathUtil.from("data", dataName)).get();
		eb.registerHandler("data.query", dataQueryHandler);
		
	}
	
	Handler<Message<JsonObject>> dataQueryHandler = new Handler<Message<JsonObject>>() {
		public void handle(Message<JsonObject> msg) {
			JsonArray jsonMatchIds = new JsonArray();
			List<String> candidateIds = msg.body().getArray("candidateIds").toList();
			for(String candidateId : candidateIds) {
				if (isMatch(candidateId, msg.body().getString("name"), 0.7)) { //Need to pass threshold in via config
					jsonMatchIds.addString(candidateId);
				}
			}
			msg.reply(jsonMatchIds);
		}
	};
	
	boolean isMatch(String candidateId, String name, Double threshold) {
		Levenshtein levSim = new Levenshtein();
		String candidateName = getNameById(candidateId);
		boolean match = false;
		if (levSim.getSimilarity(name, candidateName) > threshold ) {
			match = true;
		}
		return match;
	}
	
	public String getNameById(final String id) {
		return db.run(new Function<Transaction,String>() {
			@Override
			public String apply(Transaction tr) {
				byte[] nameBytes = tr.get(data.pack(Tuple.from(id, "name"))).get();
				String name = new String(nameBytes);
				return name;
			}
		});
	}
	
}

