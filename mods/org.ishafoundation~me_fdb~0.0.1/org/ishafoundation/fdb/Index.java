package org.ishafoundation.fdb;

import java.util.ArrayList;
import java.util.List;

import com.foundationdb.Database;
import com.foundationdb.Transaction;
import com.foundationdb.TransactionContext;
import com.foundationdb.KeyValue;
import com.foundationdb.tuple.Tuple;
import com.foundationdb.directory.DirectoryLayer;
import com.foundationdb.directory.DirectorySubspace;
import com.foundationdb.directory.PathUtil;
import com.foundationdb.async.Function;

public class Index {
	
	private DirectorySubspace index;
	
	public Index(Database db, String indexName) {
		this.index = DirectoryLayer.getDefault().createOrOpen(db, PathUtil.from("blocking_idx", indexName)).get();
	}

	public List<String> getIdsByKey(TransactionContext tcx, final String key) {
		return tcx.run(new Function<Transaction,List<String>>() {
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

