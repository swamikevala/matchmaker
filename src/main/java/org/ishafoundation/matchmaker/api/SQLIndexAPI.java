package org.ishafoundation.matchmaker.api;

import java.util.Set;
import java.util.HashSet;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.DatabaseMetaData;
import java.util.UUID;

import com.google.gson.JsonObject;

public class SQLIndexAPI extends SQLConnectionAPI implements IndexAPI {

	private String prefix;
	private Connection con;

	public SQLIndexAPI(JsonObject params) {
		super(params);
		prefix = params.get("prefix").getAsString();
	}
	
	//delete if exists
	@Override
	public void createIndex(String attribute, String icId) {
		con = openConnection(icId);
		String sql = "CREATE TABLE `" + prefix.concat(attribute) + "` (`key` VARCHAR(200) NOT NULL, `basekeylength` INT NOT NULL, `block_id` VARCHAR(200) NOT NULL, `id` VARCHAR(200) NOT NULL, INDEX `id` (`id` ASC), INDEX `key` (`key` ASC))";
		try ( PreparedStatement ps = con.prepareStatement(sql); ) {
			ps.executeUpdate(); 
		} catch (SQLException e) {
			e.printStackTrace();
		}
		closeConnection(con, false);
	}
	
	private boolean columnExists(DatabaseMetaData dmd, String table, String column, String type) {
		boolean colExists = false;
		try ( ResultSet rs = dmd.getColumns(null, null, table, column); ) {
			if ( rs.next() ) {
				if ( rs.getString("TYPE_NAME").equals(type) ) {
					colExists = true;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} 
		return colExists;
	}
		
	@Override	
	public boolean indexExists(String attribute, String icId) {
		con = openConnection(icId);
		DatabaseMetaData dmd = null;
		boolean exists = false;
		try {
			dmd = con.getMetaData();
		} catch (SQLException e) {
			e.printStackTrace();
		} 
		
		try ( ResultSet rs = dmd.getTables(null, null, prefix.concat(attribute), null);
			ResultSet rs2 = dmd.getIndexInfo(null, null, prefix.concat(attribute), false, false); ) {
			
			if ( rs.next() ) {
				exists = true;
				//Check it has the necessary columns defined
				if ( !columnExists(dmd, prefix.concat(attribute), "key", "VARCHAR") ) {
					System.out.println("`key` column does not exist on table `" + prefix.concat(attribute +"`"));
					exists = false;
				}
				if ( !columnExists(dmd, prefix.concat(attribute), "basekeylength", "INT") ) {
					System.out.println("`basekeylength` column does not exist on table `" + prefix.concat(attribute +"`"));
					exists = false;
				}
				if ( !columnExists(dmd, prefix.concat(attribute), "block_id", "VARCHAR") ) {
					System.out.println("`block_id` column does not exist on table `" + prefix.concat(attribute +"`"));
					exists = false;
				}
				if ( !columnExists(dmd, prefix.concat(attribute), "id", "VARCHAR") ) {
					System.out.println("`id` column does not exist on table `" + prefix.concat(attribute +"`"));
					exists = false;
				}
				//Check it has the necessary indexes defined
				boolean hasIdIndex = false;
				boolean hasKeyIndex = false;
				while (rs2.next()) {
					if ( rs2.getString("COLUMN_NAME").equals("id") ) {
						hasIdIndex = true;
					}
					if ( rs2.getString("COLUMN_NAME").equals("key") ) {
						hasKeyIndex = true;
					}
				}
				if ( !hasIdIndex ) {
					System.out.println("No index for `id` column is defined on table `" + prefix.concat(attribute +"`"));
					exists = false;
				}
				if ( !hasKeyIndex ) {
					System.out.println("No index for `key` column is defined on table `" + prefix.concat(attribute +"`"));
					exists = false;
				}
			} else {
				System.out.println("Table `" + prefix.concat(attribute +"` does not exist"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} 
		closeConnection(con, false);
		return exists;
	}
	
	@Override
	public void add(String attribute, String key, int baseKeyLength, String blockId, String id, String icId) {
		con = openConnection(icId);
		String sql = "insert into " + prefix.concat(attribute) + " values (\"" + key + "\", " + baseKeyLength + ", \"" + blockId + "\", \"" + id + "\")";
		try ( PreparedStatement ps = con.prepareStatement(sql); ) {
			ps.executeUpdate(); 
		} catch (SQLException e) {
			e.printStackTrace();
		}
		closeConnection(con, false);
	}
	
	@Override
	public Set<String> getIds(String attribute, String key, String icId) {
		Set<String> ids = new HashSet();
		con = openConnection(icId);
		String sql = "select id from " + prefix.concat(attribute) + " where `key`=\"" + key + "\"";
		try ( PreparedStatement ps = con.prepareStatement(sql);
			ResultSet rs = ps.executeQuery(); ) {
			while (rs.next()) {
				ids.add(rs.getString("id"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} 
		closeConnection(con, false);
		return ids;
	}
	
	@Override
	public int getBaseKeyLength(String attribute, String id, String key, String icId) {
		int bkl = 0;
		con = openConnection(icId);
		String sql = "select basekeylength from " + prefix.concat(attribute) + " where `id`=\"" + id + "\" and `key`=\"" + key + "\"";
		try ( PreparedStatement ps = con.prepareStatement(sql);
			ResultSet rs = ps.executeQuery(); ) {
			rs.next();
			bkl = rs.getInt("basekeylength");
		} catch (SQLException e) {
			e.printStackTrace();
		} 
		closeConnection(con, false);
		return bkl;
	}
	
	@Override
	public String getMaxBaseKeyId(String attribute, String key, String icId) {
		String mbkid = "";
		con = openConnection(icId);
		String sql = "select id from " + prefix.concat(attribute) + " where `key`=\"" + key + "\" order by basekeylength desc limit 1";
		try ( PreparedStatement ps = con.prepareStatement(sql);
			ResultSet rs = ps.executeQuery(); ) {
			rs.next();
			mbkid = rs.getString("id");
		} catch (SQLException e) {
			e.printStackTrace();
		} 
		closeConnection(con, false);
		return mbkid;
	}
	
	@Override
	public void delete(String attribute, String id, String key, String icId) {
		int bkl = 0;
		con = openConnection(icId);
		String sql = "delete from " + prefix.concat(attribute) + " where id=\"" + id + "\" and `key`=\"" + key + "\"";
		try ( PreparedStatement ps = con.prepareStatement(sql); ) {
			ps.executeUpdate(); 
		} catch (SQLException e) {
			e.printStackTrace();
		} 
		closeConnection(con, false);
	}

	@Override
	public int getKeyCount(String attribute, String key, String icId) {
		int count = 0;
		con = openConnection(icId);
		String sql = "select count(*) from " + prefix.concat(attribute) + " where `key`=\"" + key + "\"";
		try ( PreparedStatement ps = con.prepareStatement(sql); 
			ResultSet rs = ps.executeQuery(); ) {
			rs.next();
			count = rs.getInt("count(*)");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		closeConnection(con, false);
		return count;
	}
	
	//@Override //not used by BoundedBlockingMethod class
	public int getBlockCount(String attribute, String id, String icId) {
		int count = 0;
		con = openConnection(icId);
		String sql = "select count(*) from " + prefix.concat(attribute) + " where id=\"" + id + "\"";
		try ( PreparedStatement ps = con.prepareStatement(sql); 
			ResultSet rs = ps.executeQuery(); ) {
			rs.next();
			count = rs.getInt("count(*)");
		} catch (SQLException e) {
			e.printStackTrace();
		} 
		closeConnection(con, false);
		return count;
	}
}

