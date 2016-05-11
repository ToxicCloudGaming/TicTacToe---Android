package org.toxiccloudgaming.database;

import java.sql.Connection;
import java.sql.DriverManager;

public class TCGDatabase {

	public static final String DB_URL = "jdbc:mysql://localhost:3306/tc_db";
	public static final String DB_USER = "tc-mysql";
	public static final String DB_PASS = "(tc-dat@2016)";
	
	public static Connection getConnection() {
		Connection conn = null;
		
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return conn;
	}
}
