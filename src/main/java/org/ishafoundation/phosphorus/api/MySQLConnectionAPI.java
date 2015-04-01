package org.ishafoundation.phosphorus.api;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.UUID;
import java.util.Map;
import java.util.HashMap;

import org.vertx.java.core.json.JsonObject;

public abstract class MySQLConnectionAPI implements ConnectionAPI {

	private String jdbc_driver;
	private String db_url;
	private String user;
	private String password;
	
	protected Map<String, Connection> sustainedConnections;
	
	protected MySQLConnectionAPI(JsonObject params) {
		this.jdbc_driver = params.getString("driver");
		this.db_url = params.getString("dburl");
		this.user = params.getString("user");
		this.password = params.getString("password");
		this.sustainedConnections = new HashMap<String, Connection>();
		try {
			Class.forName(jdbc_driver);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	protected Connection getNewConnection() {
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(db_url, user, password);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return conn;
	}
	
	protected void closeConnection(Connection conn, boolean closeSustained) {
		boolean sustained = sustainedConnections.containsValue(conn);
		if ( conn != null ) {
			if ( !sustained || (closeSustained && sustained) ) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public String openSustainedConnection() {
		String connId = UUID.randomUUID().toString();
		Connection conn = getNewConnection();
		sustainedConnections.put(connId, conn);
		return connId;
	}
	
	public boolean closeSustainedConnection(String connId) {
		if ( connId != null && sustainedConnections.containsKey(connId) ) {
			Connection conn = sustainedConnections.get(connId);
			closeConnection(conn, true);
			return true;
		} else {
			return false;
		}
	}
	
	protected Connection getSustainedConnection(String connId) {
		Connection conn = null;
		if ( connId != null && sustainedConnections.containsKey(connId) ) {
			conn = sustainedConnections.get(connId);
		} 
		return conn;
	}

}

