package org.toxiccloudgaming.tictactoe.server.player;

public class Player {

	private String username;
	private String sessionID;
	private int wins;
	private int losses;
	private boolean inGame;
	private int gameID;
	
	public Player(String username, int wins, int losses) {
		this.username = username;
		this.wins = wins;
		this.losses = losses;
		this.sessionID = "";
		this.inGame = false;
		this.gameID = 0;
	}
	
	public int getWins() {
		return this.wins;
	}
	
	public int getLosses() {
		return this.losses;
	}
	
	public void incrWins() {
		this.wins++;
	}
	
	public void incrLosses() {
		this.losses++;
	}
	
	public String getUsername() {
		return this.username;
	}
	
	public void changeSessionID(String sessionID) {
		this.sessionID = sessionID;
	}
	
	public String getSessionID() {
		return this.sessionID;
	}
	
	public boolean inGame() {
		return this.inGame;
	}
	
	public void setInGame(boolean inGame) {
		this.inGame = inGame;
	}
	
	public int getGameID() {
		return this.gameID;
	}
	
	public void setGameID(int gameID) {
		this.gameID = gameID;
	}
}
