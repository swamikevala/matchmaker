package org.ishafoundation.matchmaker.api;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.UUID;
import java.util.Map;
import java.util.HashMap;

import com.google.gson.JsonObject;

public abstract class SQLConnectionAPI implements ConnectionAPI {

	private String jdbc_driver;
	private String db_url;
	private String user;
	private String password;
	
	protected Map<String, Connection> sustainedConnections;
	
	protected SQLConnectionAPI(JsonObject params) {
		JsonObject con = params.get("connection").getAsJsonObject();
		this.jdbc_driver = con.get("driver").getAsString();
		this.db_url = con.get("dburl").getAsString();
		this.user = con.get("user").getAsString();
		this.password = con.get("password").getAsString();
		this.sustainedConnections = new HashMap<String, Connection>();
		try {
			Class.forName(jdbc_driver);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	protected Connection getNewConnection() {
		Connection con = null;
		try {
			con = DriverManager.getConnection(db_url, user, password);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return con;
	}
	
	protected Connection openConnection(String cId) {
		Connection con;
		if ( cId != null && cId != "" ) {
			con = getSustainedConnection(cId);
		} else {
			con = getNewConnection();
		}
		return con;
	}
	
	protected void closeConnection(Connection con, boolean closeSustained) {
		boolean sustained = sustainedConnections.containsValue(con);
		if ( con != null ) {
			if ( !sustained || (closeSustained && sustained) ) {
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public String openSustainedConnection() {
		String cId = UUID.randomUUID().toString();
		Connection con = getNewConnection();
		sustainedConnections.put(cId, con);
		return cId;
	}
	
	public boolean closeSustainedConnection(String cId) {
		if ( cId != null && sustainedConnections.containsKey(cId) ) {
			Connection con = sustainedConnections.get(cId);
			closeConnection(con, true);
			return true;
		} else {
			return false;
		}
	}
	
	protected Connection getSustainedConnection(String cId) {
		Connection con = null;
		if ( cId != null && sustainedConnections.containsKey(cId) ) {
			con = sustainedConnections.get(cId);
		} 
		return con;
	}

}

