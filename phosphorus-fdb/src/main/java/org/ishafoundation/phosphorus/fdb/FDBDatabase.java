package org.ishafoundation.phosphorus.fdb;

import org.vertx.java.core.shareddata.Shareable;

import com.foundationdb.FDB;
import com.foundationdb.Database;

public class FDBDatabase implements Shareable {

	final int fdbApiVersion = 300;
	static FDB fdb;
	static Database instance;
	
	public FDBDatabase() {
		fdb = FDB.selectAPIVersion(fdbApiVersion);
		this.instance = fdb.open();
	}
	
	public Database getInstance() {
		return this.instance;
	}
}
	
	