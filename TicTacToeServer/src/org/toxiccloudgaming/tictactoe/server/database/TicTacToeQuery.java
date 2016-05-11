package org.toxiccloudgaming.tictactoe.server.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.toxiccloudgaming.database.TCGDatabase;
import org.toxiccloudgaming.tictactoe.server.ServerLog;
import org.toxiccloudgaming.tictactoe.server.player.Player;

public class TicTacToeQuery {

	public static boolean signIn(String user, String pass, ServerLog logger) {
		String query = "SELECT * FROM User WHERE"
				+ " (username = ?) AND (password = ?);";
		
		Connection conn = TCGDatabase.getConnection();
		try {
			PreparedStatement statement = conn.prepareStatement(query);
			statement.setString(1, user);
			statement.setString(2, pass);
			ResultSet result = statement.executeQuery();
			
			boolean correctLogin = false;
			if(result.next()) correctLogin = true;
			
			result.close();
			statement.close();
			conn.close();
			
			if(correctLogin) return true;
		} catch(SQLException e) {
			if(logger != null) {
				logger.logException(e);
			} else {
				e.printStackTrace();
			}
		}
		return false;
	}
	
	public static boolean signIn(String user, String pass) {
		return signIn(user, pass, null);
	}
	
	public static boolean isNew(Connection conn, int userID) throws SQLException {
		String query = "SELECT * FROM TicTacToe WHERE"
				+ " (userID = ?);";
		
		PreparedStatement statement = conn.prepareStatement(query);
		statement.setInt(1, userID);
		ResultSet result = statement.executeQuery();
		
		boolean newUser = true;
		if(result.next()) newUser = false;
		
		result.close();
		statement.close();
		
		if(newUser) {
			return true;
		}
		return false;
	}
	
	public static void newUser(Connection conn, int userID) throws SQLException {
		String query = "INSERT INTO TicTacToe (userID, gameWins, gameLosses)"
				+ " VALUES (?, 0, 0);";
		
		PreparedStatement statement = conn.prepareStatement(query);
		statement.setInt(1, userID);
		statement.executeUpdate();
		statement.close();
	}
	
	public static Player updatePlayer(String user, ServerLog logger) {
		Player player = null;
		
		String query = "SELECT * FROM TicTacToe WHERE"
				+ " (userID = ?);";
		
		Connection conn = TCGDatabase.getConnection();
		
		try {
			int userID = getUserID(conn, user);
			if(isNew(conn, userID)) {
				newUser(conn, userID);
				System.out.println("Created new profile for " + user + "!");
			}
			
			PreparedStatement statement = conn.prepareStatement(query);
			statement.setInt(1, userID);
			ResultSet result = statement.executeQuery();
			
			if(result.next()) {
				int wins = result.getInt("gameWins");
				int losses = result.getInt("gameLosses");
				
				player = new Player(user, wins, losses);
			}
			
			result.close();
			statement.close();
			conn.close();
		} catch(SQLException e) {
			if(logger != null) {
				logger.logException(e);
			} else {
				e.printStackTrace();
			}
		}
		
		return player;
	}
	
	public static Player updatePlayer(String user) {
		return updatePlayer(user, null);
	}
	
	public static void savePlayer(Player player, ServerLog logger) {
		String query = "UPDATE TicTacToe SET gameWins = ?, gameLosses = ? WHERE"
				+ " (userID = ?);";
		
		Connection conn = TCGDatabase.getConnection();
		try {
			PreparedStatement statement = conn.prepareStatement(query);
			statement.setInt(1, player.getWins());
			statement.setInt(2, player.getLosses());
			statement.setInt(3, getUserID(conn, player.getUsername()));
			statement.executeUpdate();
			
			statement.close();
			conn.close();
		} catch(SQLException e) {
			if(logger != null) {
				logger.logException(e);
			} else {
				e.printStackTrace();
			}
		}
	}
	
	public static void savePlayer(Player player) {
		savePlayer(player, null);
	}
	
	public static int getUserID(Connection conn, String user) throws SQLException {
		String query = "SELECT userID FROM User WHERE"
				+ " (username = ?);";
		
		int userID = 0;
		
		PreparedStatement statement = conn.prepareStatement(query);
		statement.setString(1, user);
		ResultSet result = statement.executeQuery();
		
		if(result.next()) userID = result.getInt("userID");
		
		result.close();
		statement.close();
		
		return userID;
	}
}
