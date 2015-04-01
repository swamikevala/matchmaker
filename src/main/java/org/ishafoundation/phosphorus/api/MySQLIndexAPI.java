package org.ishafoundation.phosphorus.api;

import java.util.Set;
import java.util.HashSet;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.DatabaseMetaData;
import java.util.UUID;

import org.vertx.java.core.json.JsonObject;

public class MySQLIndexAPI extends MySQLConnectionAPI implements IndexAPI {

	private String prefix;
	private Connection connect;
	private PreparedStatement preparedStatement;
	private ResultSet resultSet;
	private ResultSet resultSet2;
	
	private final JsonObject params;

	public MySQLIndexAPI(JsonObject params) {
		super(params);
		this.params = params;
		this.prefix = params.getString("prefix");
	}
	
	//delete if exists
	public void createIndex(String indexName, String connId) {
		connect = (connId != null && connId != "") ? getSustainedConnection(connId) : getNewConnection();
		try {
			preparedStatement = connect.prepareStatement("CREATE TABLE `" + prefix.concat(indexName) + "` (`key` VARCHAR(200) NOT NULL, `basekeylength` INT NOT NULL, `block_id` VARCHAR(200) NOT NULL, `id` VARCHAR(200) NOT NULL, INDEX `id` (`id` ASC), INDEX `key` (`key` ASC))");
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				preparedStatement.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		closeConnection(connect, false);
	}
	
	private boolean columnExists(DatabaseMetaData dmd, String table, String column, String type) {
		boolean colExists = false;
		try {
			ResultSet resultSet = dmd.getColumns(null, null, table, column);
			if ( resultSet.next() ) {
				if ( resultSet.getString("TYPE_NAME").equals(type) )
				colExists = true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				resultSet.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return colExists;
	}
					
	public boolean indexExists(String indexName, String connId) {
		connect = (connId != null && connId != "") ? getSustainedConnection(connId) : getNewConnection();
		boolean exists = false;
		try {
			DatabaseMetaData dmd = connect.getMetaData();
			resultSet = dmd.getTables(null, null, prefix.concat(indexName), null);
			if ( resultSet.next() ) {
				exists = true;
				//Check it has the necessary columns defined
				if ( ! columnExists(dmd, prefix.concat(indexName), "key", "VARCHAR") ) {
					System.out.println("`key` column does not exist on table `" + prefix.concat(indexName +"`"));
					exists = false;
				}
				if ( ! columnExists(dmd, prefix.concat(indexName), "basekeylength", "INT") ) {
					System.out.println("`basekeylength` column does not exist on table `" + prefix.concat(indexName +"`"));
					exists = false;
				}
				if ( ! columnExists(dmd, prefix.concat(indexName), "block_id", "VARCHAR") ) {
					System.out.println("`block_id` column does not exist on table `" + prefix.concat(indexName +"`"));
					exists = false;
				}
				if ( ! columnExists(dmd, prefix.concat(indexName), "id", "VARCHAR") ) {
					System.out.println("`id` column does not exist on table `" + prefix.concat(indexName +"`"));
					exists = false;
				}
				//Check it has the necessary indexes defined
				resultSet2 = dmd.getIndexInfo(null, null, prefix.concat(indexName), false, false);
				boolean hasIdIndex = false;
				boolean hasKeyIndex = false;
				while (resultSet2.next()) {
					if ( resultSet2.getString("COLUMN_NAME").equals("id") ) {
						hasIdIndex = true;
					}
					if ( resultSet2.getString("COLUMN_NAME").equals("key") ) {
						hasKeyIndex = true;
					}
				}
				resultSet2.close();
				if ( ! hasIdIndex ) {
					System.out.println("No index for `id` column is defined on table `" + prefix.concat(indexName +"`"));
					exists = false;
				}
				if ( ! hasKeyIndex ) {
					System.out.println("No index for `key` column is defined on table `" + prefix.concat(indexName +"`"));
					exists = false;
				}
			} else {
				System.out.println("Table `" + prefix.concat(indexName +"` does not exist"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				resultSet.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		closeConnection(connect, false);
		return exists;
	}
	
	public void add(String indexName, String key, int baseKeyLength, String blockId, String id, String connId) {
		connect = (connId != null && connId != "") ? getSustainedConnection(connId) : getNewConnection();
		try {
			preparedStatement = connect.prepareStatement("insert into " + prefix.concat(indexName) + " values (? , ? , ?, ?)");
			preparedStatement.setString(1, key);
			preparedStatement.setInt(2, baseKeyLength);
			preparedStatement.setString(3, blockId);
			preparedStatement.setString(4, id);
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				preparedStatement.close();
				
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		closeConnection(connect, false);
	}
	
	public Set<String> getIds(String indexName, String key, String connId) {
		Set<String> ids = new HashSet();
		connect = (connId != null && connId != "") ? getSustainedConnection(connId) : getNewConnection();
		try {
			preparedStatement = connect.prepareStatement("select id from " + prefix.concat(indexName) + " where `key`=?");
			preparedStatement.setString(1, key);
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
	
	public int getMaxBaseKeyLength(String indexName, String key, String connId) {
		int bkl = 0;
		connect = (connId != null && connId != "") ? getSustainedConnection(connId) : getNewConnection();
		try {
			preparedStatement = connect.prepareStatement("select basekeylength from " + prefix.concat(indexName)+ " where `key`=? order by basekeylength desc limit 1");
			preparedStatement.setString(1, key);
			resultSet = preparedStatement.executeQuery();
			resultSet.next();
			bkl = resultSet.getInt("basekeylength");
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
		return bkl;
	}
	
	public int getBaseKeyLength(String indexName, String id, String key, String connId) {
		int bkl = 0;
		connect = (connId != null && connId != "") ? getSustainedConnection(connId) : getNewConnection();
		try {
			preparedStatement = connect.prepareStatement("select basekeylength from " + prefix.concat(indexName)+ " where `id`=? and `key`=?");
			preparedStatement.setString(1, id);
			preparedStatement.setString(2, key);
			resultSet = preparedStatement.executeQuery();
			resultSet.next();
			bkl = resultSet.getInt("basekeylength");
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
		return bkl;
	}
	
	public String getMaxBaseKeyId(String indexName, String key, String connId) {
		String mbkid = "";
		connect = (connId != null && connId != "") ? getSustainedConnection(connId) : getNewConnection();
		try {
			preparedStatement = connect.prepareStatement("select id from " + prefix.concat(indexName) + " where `key`=? order by basekeylength desc limit 1");
			preparedStatement.setString(1, key);
			resultSet = preparedStatement.executeQuery();
			resultSet.next();
			mbkid = resultSet.getString("id");
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
		return mbkid;
	}
	
	public void delete(String indexName, String id, String key, String connId) {
		int bkl = 0;
		connect = (connId != null && connId != "") ? getSustainedConnection(connId) : getNewConnection();
		try {
			preparedStatement = connect.prepareStatement("delete from " + prefix.concat(indexName) + " where id=? and `key`=?");
			preparedStatement.setString(1, id);
			preparedStatement.setString(2, key);
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				preparedStatement.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		closeConnection(connect, false);
	}

	
	public int getKeyCount(String indexName, String key, String connId) {
		int count = 0;
		connect = (connId != null && connId != "") ? getSustainedConnection(connId) : getNewConnection();
		try {
			preparedStatement = connect.prepareStatement("select count(*) from " + prefix.concat(indexName) + " where `key`=?");
			preparedStatement.setString(1, key);
			resultSet = preparedStatement.executeQuery();
			resultSet.next();
			count = resultSet.getInt("count(*)");
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
		return count;
	}
	
	public int getBlockCount(String indexName, String id, String connId) {
		int count = 0;
		connect = (connId != null && connId != "") ? getSustainedConnection(connId) : getNewConnection();
		try {
			preparedStatement = connect.prepareStatement("select count(*) from " + prefix.concat(indexName) + " where id=?");
			preparedStatement.setString(1, id);
			resultSet = preparedStatement.executeQuery();
			resultSet.next();
			count = resultSet.getInt("count(*)");
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
		return count;
	}
}

