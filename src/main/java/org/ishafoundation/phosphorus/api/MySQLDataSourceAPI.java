package org.ishafoundation.phosphorus.api;

import java.util.Set;
import java.util.HashSet;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.vertx.java.core.json.JsonObject;

public class MySQLDataSourceAPI extends MySQLConnectionAPI implements DataSourceAPI {

	private String prefix;
	private Connection connect;
	private PreparedStatement preparedStatement;
	private ResultSet resultSet;
	private ResultSet resultSet2;
	
	private final JsonObject params;
	
	public MySQLDataSourceAPI(JsonObject params) {
		super(params);
		this.params = params;
		this.prefix = params.getString("prefix");
	}
	
	public Set<String> getValuesById(String attributeName, String id, String connId) {
		Set<String> values = new HashSet();
		connect = (connId != null && connId != "") ? getSustainedConnection(connId) : getNewConnection();
		try {
			preparedStatement = connect.prepareStatement("select " + attributeName +" from ishadb.addresslist_cleaned where id=?");
			preparedStatement.setString(1, id);
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				if ( resultSet.getString(attributeName) != null ) {
					values.add(resultSet.getString(attributeName));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				preparedStatement.close();
				resultSet.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		closeConnection(connect, false);
		return values;
	}
	
	public Set<String> getAllIds(String connId) {
		Set<String> ids = new HashSet();
		connect = (connId != null && connId != "") ? getSustainedConnection(connId) : getNewConnection();
		try {
			preparedStatement = connect.prepareStatement("select id from ishadb.addresslist_cleaned order by id ASC");
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				ids.add(resultSet.getString("id"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				preparedStatement.close();
				resultSet.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		closeConnection(connect, false);
		return ids;
	}
	
}

  

