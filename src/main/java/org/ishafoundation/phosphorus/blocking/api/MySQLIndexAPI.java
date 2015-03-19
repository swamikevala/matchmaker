package org.ishafoundation.phosphorus.blocking.api;

import java.util.Set;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.ishafoundation.phosphorus.blocking.IndexKey;
import org.ishafoundation.phosphorus.blocking.BlockLink;

public class MySQLIndexAPI {

	private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
	private static final String DB_URL = "jdbc:mysql://localhost/ishadb";
	private static final String USER = "talend";
	private static final String PASS = "talend";
	
	private Connection connect = null;
	private Statement statement = null;
	private PreparedStatement preparedStatement = null;
	private ResultSet resultSet = null;
	
	public MySQLIndexAPI() {
		Class.forName("com.mysql.jdbc.Driver");
		connect = DriverManager.getConnection(DB_URL, USER, PASS);

	preparedStatement = connect.prepareStatement("insert into ishadb.? values (? , ? , ?, ?)");
	preparedStatement.setString(1, "Test");
	preparedStatement.setString(2, "TestEmail");
	preparedStatement.setString(3, "TestWebpage");
	preparedStatement.setDate(4, new java.sql.Date(2009, 12, 11));
      preparedStatement.setString(5, "TestSummary");
      preparedStatement.setString(6, "TestComment");
      preparedStatement.executeUpdate();

      preparedStatement = connect
          .prepareStatement("SELECT myuser, webpage, datum, summery, COMMENTS from feedback.comments");
      resultSet = preparedStatement.executeQuery();
      writeResultSet(resultSet);
	}
	
	public int getBlockSize(String indexName, int blockId) {
	
	
	
	}
	
	public int getBlockId(String indexName, IndexKey key);
	
	public Set<SubKey> getAdjacentKeys(IndexKey key, int windowSize);  //ws=0 means get the matching key itself if it exists, ws=1 means one on either side
	
	public void add(String indexName, IndexKey key, String id) {
		
		.createOrOpen(db, PathUtil.from("blocking_idx", blockingIndexName)).get();
		Set<BlockLink> blockLinks = key.getBlockLinks();
		for ( BlockLink bl : blockLinks ) {
			
		}
	
	}
	
	public int getNewBlockId() {
	
	
	}
	
	public int getBlockCountByKey(IndexKey key);
	
}

