package org.ishafoundation.matchmaker.api;

import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.vertx.java.core.json.JsonObject;

public class SQLDataSourceAPI extends SQLConnectionAPI implements DataSourceAPI {

	private Map<String, Table> tableMap;
	private Map<Field, String> fieldMap;
	private Connection con;
	
	public SQLDataSourceAPI(JsonObject params) {
		super(params);
		tableMap = new HashMap<String, Table>();
		for ( Map table : (List<Map>) params.getArray("tables").toList() ) {
			JsonObject jTable = new JsonObject(table);
			String alias = jTable.getString("alias");
			tableMap.put(alias, new Table(jTable.getString("name"), jTable.getString("id-field")));
		//Can also check that tables (and id field) exists, and that id index is defined
		}
		
		fieldMap = new HashMap<Field, String>();
		for ( String attribute : params.getObject("mappings").getFieldNames() ) {
			for ( Map fields : (List<Map>) params.getObject("mappings").getArray(attribute).toList() ) {
				JsonObject jFields = new JsonObject(fields);
				String tableAlias = jFields.getString("table");
				Table table = tableMap.get(tableAlias); //Handle exception if not defined
				for ( String field : (List<String>) jFields.getArray("fields").toList() ) {
					fieldMap.put(new Field(table, field), attribute);
				}
			}
		}
	}
	
	public Set<String> getValuesById(String attribute, String id, String dcId) {
		Set<String> values = new HashSet();
			con = openConnection(dcId);
			for ( Map.Entry<Field, String> f : fieldMap.entrySet() ) {
				if ( f.getValue().equals(attribute) ) {
					Field field = f.getKey();
					Table table = field.getTable();
					String sql = "select " + field.getName() + " from " + table.getName() + " where " + table.getIdField() + "=\"" + id + "\"";
					try ( PreparedStatement ps = con.prepareStatement(sql); 
						ResultSet rs = ps.executeQuery(); ) {
						while (rs.next()) {
						//Should do better check for dodgy values
							if ( rs.getString(field.getName()) != null && rs.getString(field.getName()) != "" ) {
								values.add(rs.getString(field.getName()));
							}
						}
					} catch (SQLException e) {
						e.printStackTrace();
					} 
				}
			}
			closeConnection(con, false);
		return values;
	}
	
	public Set<String> getAllIds(String dcId) {
		Set<String> ids = new HashSet();
		con = openConnection(dcId);
		for ( Map.Entry<String, Table> t : tableMap.entrySet() ) {
			Table table = t.getValue();
			String sql = "select " + table.getIdField() + " from " + table.getName() + " order by " + table.getIdField() + " ASC";
			try ( PreparedStatement ps = con.prepareStatement(sql); 
				ResultSet rs = ps.executeQuery(); ) {
				while (rs.next()) {
					//Doing check for dodgy id values
					if ( rs.getString(table.getIdField()) != null && rs.getString(table.getIdField()).matches("[\\w\\d\\-]+") ) {
						ids.add(rs.getString(table.getIdField()));
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		closeConnection(con, false);
		return ids;
	}
	
	private class Table {
		private String name;
		private String idField;
		
		private Table(String name, String idField) {
			this.name = name;
			this.idField = idField;
		}
		
		private String getName() {
			return name;
		}
		
		private String getIdField() {
			return idField;
		}
	}
	
	private class Field {
		private Table table;
		private String name;
		
		private Field(Table table, String name) {
			this.table = table;
			this.name = name;
		}
		
		private String getName() {
			return name;
		}
		
		private Table getTable() {
			return table;
		}
	}

}

  

