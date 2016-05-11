package org.toxiccloudgaming.tictactoe.server.game;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.toxiccloudgaming.tictactoe.server.player.Player;

public class Game {
	private static final char PLAYER_X = 'X';
	private static final char PLAYER_O = 'O';
	private static final char EMPTY = '_';
	
	private static final int PLAYER_1 = 1;
	private static final int PLAYER_2 = 2;
	
	private int gameID;
	private Player player1, player2;
	private Player currentPlayer;
	boolean gameStarted;
	
	private int[] winGrid;
	
	private Player winner;
	private boolean gameOver;
	private boolean playerSurrendered;
	private boolean gameTie;
	
	private char[][] gameGrid;
	
	public Game(int gameID, Player player1) {
		this.gameID = gameID;
		this.player1 = player1;
		this.player2 = null;
		this.winGrid = null;
		
		this.currentPlayer = player1;
		this.gameStarted = false;
		this.gameOver = false;
		this.playerSurrendered = false;
		this.winner = null;
		this.gameTie = false;
		
		this.emptyGrid();
	}
	
	public int getGameID() {
		return this.gameID;
	}
	
	public void emptyGrid() {
		char[][] gameGrid = new char[3][3];
		
		for(int y = 0; y < 3; y++) {
			for(int x = 0; x < 3; x++) {
				gameGrid[y][x] = EMPTY;
			}
		}
		
		this.gameGrid = gameGrid;
	}
	
	public void setWinner(Player winner) {
		this.winner = winner;
	}
	
	public Player getWinner() {
		return this.winner;
	}
	
	public void endGame() {
		this.gameOver = true;
	}
	
	public boolean isGameOver() {
		return this.gameOver;
	}
	
	public void startGame() {
		this.gameStarted = true;
	}
	
	public boolean isGameStarted() {
		return this.gameStarted;
	}
	
	public Player getPlayer1() {
		return this.player1;
	}
	
	public Player getPlayer2() {
		return this.player2;
	}
	
	public boolean playerInGame(Player player) {
		if(this.player1 != null && this.player1.getUsername().equals(player.getUsername())) return true;
		if(this.player2 != null && this.player2.getUsername().equals(player.getUsername())) return true;
		return false;
	}
	
	public void playerJoinedGame(Player player) {
		if(!this.gameStarted) {
			this.player2 = player;
			this.gameStarted = true;
		}
	}
	
	public boolean playerLeftGame(Player player) {
		if(this.playerInGame(player)) {
			player.setInGame(false);
			player.setGameID(0);
			
			boolean player1Left = (this.player1 == null);
			boolean player2Left = (this.player2 == null);
			int playerLeft = PLAYER_1;
			if(!player2Left && this.player2.getUsername().equals(player.getUsername())) {
				playerLeft = PLAYER_2;
			}
			
			if(!this.gameOver && this.gameStarted){
				this.endGame();
				this.playerSurrendered = true;
				if(playerLeft == PLAYER_1) {
					this.setWinner(this.player2);
					this.player2.incrWins();
					this.player1.incrLosses();
				} else {
					this.setWinner(this.player1);
					this.player1.incrWins();
					this.player2.incrLosses();
				}
			} else {
				this.endGame();
			}
			
			if(playerLeft == PLAYER_1) {
				this.player1 = null;
			} else {
				this.player2 = null;
			}
			
			if(this.player1 == null && this.player2 == null) {
				return true;
			}
		}
		return false;
	}
	
	public void setWinGrid(int tile1, int tile2, int tile3) {
		this.winGrid = new int[3];
		this.winGrid[0] = tile1;
		this.winGrid[1] = tile2;
		this.winGrid[2] = tile3;
	}
	
	public boolean gameEnd() {
		boolean foundWin = false;
		for(int y = 0; y < 3; y++) {
			if(gameGrid[y][0] != EMPTY && gameGrid[y][0] == gameGrid[y][1] && gameGrid[y][0] == gameGrid[y][2]) {
				foundWin = true;
				this.winner = this.parseWinner(gameGrid[y][0]);
				this.setWinGrid((y * 3), (y * 3) + 1, (y * 3) + 2);
				break;
			}
		}
		for(int x = 0; x < 3; x++) {
			if(gameGrid[0][x] != EMPTY && gameGrid[0][x] == gameGrid[1][x] && gameGrid[0][x] == gameGrid[2][x]) {
				foundWin = true;
				this.winner = this.parseWinner(gameGrid[0][x]);
				this.setWinGrid(x, x + 3, x + 6);
				break;
			}
		}
		if(gameGrid[0][0] != EMPTY && gameGrid[0][0] == gameGrid[1][1] && gameGrid[0][0] == gameGrid[2][2]) {
			foundWin = true;
			this.winner = this.parseWinner(gameGrid[0][0]);
			this.setWinGrid(0, 4, 8);
		}
		if(gameGrid[2][0] != EMPTY && gameGrid[2][0] == gameGrid[1][1] && gameGrid[2][0] == gameGrid[0][2]) {
			foundWin = true;
			this.winner = this.parseWinner(gameGrid[2][0]);
			this.setWinGrid(2, 4, 6);
		}
		
		if(foundWin) {
			return true;
		} else {
			boolean boardFull = true;
			for(int y = 0; y < 3; y++) {
				for(int x = 0; x < 3; x++) {
					if(this.gameGrid[y][x] == EMPTY) {
						boardFull = false;
					}
				}
			}
			
			if(boardFull) {
				this.gameTie = true;
				return true;
			}
		}
		return false;
	}
	
	public Player parseWinner(char c) {
		if(c == PLAYER_X) {
			this.player1.incrWins();
			this.player2.incrLosses();
			return this.player1;
		} else {
			this.player1.incrWins();
			this.player2.incrLosses();
			return this.player2;
		}
		
	}
	
	public boolean tryMove(Player player, int tileID) {
		if(this.currentPlayer.getUsername().equals(player.getUsername())) {
			int x = tileID % 3;
            int y = (int)((tileID - x) / 3);
			if(this.gameGrid[y][x] == EMPTY) {
				int playerID = this.parsePlayer(player);
				if(playerID == PLAYER_1) {
					this.gameGrid[y][x] = PLAYER_X;
				} else {
					this.gameGrid[y][x] = PLAYER_O;
				}
				
				if(gameEnd()) {
					this.endGame();
				} else {
					if(playerID == PLAYER_1) {
						this.currentPlayer = this.player2;
					} else {
						this.currentPlayer = this.player1;
					}
				}
			} else {
				return false;
			}
			return true;
		}
		return false;
	}
	
	public int parsePlayer(Player player) {
		if(this.player1 != null && this.player1.getUsername().equals(player.getUsername())) return PLAYER_1;
		return PLAYER_2;
	}
	
	@SuppressWarnings("unchecked")
	public JSONObject toJSONObject() {
		JSONObject json = new JSONObject();
		json.put("gameID", this.gameID);
		
		String player1 = null;
		if(this.player1 != null) player1 = this.player1.getUsername();
		String player2 = null;
		if(this.player2 != null) player2 = this.player2.getUsername();
		String winner = null;
		if(this.winner != null) winner = this.winner.getUsername();
		String currentPlayer = null;
		if(this.currentPlayer != null) currentPlayer = this.currentPlayer.getUsername();
		
		json.put("player1", player1);
		json.put("player2", player2);
		json.put("currentPlayer", currentPlayer);
		json.put("gameStarted",this.gameStarted);
		json.put("winner", winner);
		json.put("gameOver", this.gameOver);
		json.put("playerSurrendered", this.playerSurrendered);
		json.put("gameTie", this.gameTie);
		if(this.winGrid != null) {
			JSONArray array = new JSONArray();
			array.add(this.winGrid[0]);
			array.add(this.winGrid[1]);
			array.add(this.winGrid[2]);
			json.put("winGrid", array);
		}
		
		JSONArray array = new JSONArray();
		for(int y = 0; y < 3; y++) {
			for(int x = 0; x < 3; x++) {
				char gridValue = this.gameGrid[y][x];
				array.add(gridValue);
			}
		}
		
		json.put("gameGrid", array);
		
		return json;
	}
}
