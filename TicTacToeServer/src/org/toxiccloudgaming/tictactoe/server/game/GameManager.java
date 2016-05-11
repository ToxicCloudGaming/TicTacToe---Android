package org.toxiccloudgaming.tictactoe.server.game;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.toxiccloudgaming.tictactoe.server.ServerLog;
import org.toxiccloudgaming.tictactoe.server.action.ActionManager;
import org.toxiccloudgaming.tictactoe.server.database.TicTacToeQuery;
import org.toxiccloudgaming.tictactoe.server.player.Player;

public class GameManager {
	private ConcurrentHashMap<Integer, Game> games; //Game ID, Game
	private Game waitingGame;
	private ConcurrentHashMap<String, Player> players; //Username, Player
	private ActionManager manager;
	private ServerLog logger;
	
	private int nextGameID;
	
	public GameManager(ServerLog logger) {
		this.games = new ConcurrentHashMap<>();
		this.waitingGame = null;
		this.nextGameID = 1;
		this.players = new ConcurrentHashMap<>();
		this.manager = new ActionManager(this, logger);
		this.logger = logger;
	}
	
	public ActionManager getActionManager() {
		return this.manager;
	}
	
	public boolean playerOnline(String user) {
		return this.players.containsKey(user);
	}
	
	public boolean sessionOnline(String sessionID) {
		for(Map.Entry<String, Player> entry : this.players.entrySet()) {
			if(entry.getValue().getSessionID().equals(sessionID)) {
				return true;
			}
		}
		return false;
	}
	
	public Player getPlayer(String user) {
		return this.players.get(user);
	}
	
	public void sessionEnded(String sessionID) {
		for(Map.Entry<String, Player> entry : this.players.entrySet()) {
			Player player = entry.getValue();
			if(player.getSessionID().equals(sessionID)) {
				String user = player.getUsername();
				this.logger.log("Player " + user + " was kicked for being idle!", ServerLog.LOG_INFO);
				this.playerLeft(player);
			}
		}
	}
	
	public void playerLeft(Player player) {
		if(player.inGame()) {
			int gameID = player.getGameID();
			if(this.gameExists(gameID)) {
				Game game = this.getGame(gameID);
				if(game.playerInGame(player) && game.playerLeftGame(player)) {
					this.destroyGame(game);
				}
			}
		}
		TicTacToeQuery.savePlayer(player);
		this.players.remove(player.getUsername());
	}
	
	public void playerJoined(String user, Player player) {
		this.players.put(user, player);
	}
	
	public void updatePlayerSession(String user, String sessionID) {
		this.players.get(user).changeSessionID(sessionID);
	}
	
	public HashMap<String, Player> getPlayerList() {
		return new HashMap<String, Player>(this.players);
	}
	
	public Game getGame(int gameID) {
		if(this.waitingGame != null && this.waitingGame.getGameID() == gameID) {
			return waitingGame;
		}
		return this.games.get(gameID);
	}
	
	public boolean gameExists(int gameID) {
		if(this.waitingGame != null && this.waitingGame.getGameID() == gameID) {
			return true;
		}
		return this.games.containsKey(gameID);
	}
	
	public boolean playerHasGame(Player player) {
		if(player.inGame()) {
			int gameID = player.getGameID();
			if(this.gameExists(gameID) && this.getGame(gameID).playerInGame(player)) {
				return true;
			} else {
				player.setInGame(false);
				player.setGameID(0);
				return false;
			}
		} else {
			return false;
		}
	}
	
	public void addGame(Game game) {
		this.games.put(game.getGameID(), game);
	}
	
	public void destroyGame(Game game) {
		int gameID = game.getGameID();
		if(this.gameExists(gameID)) {
			String victoryText = " no winner.";
			if(game.getWinner() != null) {
				victoryText = game.getWinner().getUsername() + " won!";
			}
			this.logger.log("Game #" + gameID + " ended:" + victoryText, ServerLog.LOG_INFO);
			if(this.waitingGame != null && this.waitingGame.getGameID() == gameID) {
				this.waitingGame = null;
			} else {
				this.games.remove(gameID);
			}
		}
	}
	
	public int getNewGameID() {
		int gameID = this.nextGameID;
		this.nextGameID++;
		return gameID;
	}
	
	public Game leaveGame(Player player) {
		Game game = this.getGame(player.getGameID());
		if(game.playerLeftGame(player)) {
			this.destroyGame(game);
		}
		
		return game;
	}
	
	public Game createGame(Player player) {
		Game game = null;
		if(this.waitingGame != null) {
			game = this.waitingGame;
			this.waitingGame = null;
			game.playerJoinedGame(player);
			this.addGame(game);

			if(game.getPlayer1() == null) this.logger.log("PLAYER 1 NULL IN GAME", ServerLog.LOG_WARNING);
			if(game.getPlayer2() == null) this.logger.log("PLAYER 2 NULL IN GAME", ServerLog.LOG_WARNING);
			
			this.logger.log("Game #" + game.getGameID() + " started: Player1: " 
					+ game.getPlayer1().getUsername() + " Player2: " + game.getPlayer2().getUsername(), ServerLog.LOG_INFO);
		} else {
			game = new Game(getNewGameID(), player);
			this.waitingGame = game;
		}
		player.setGameID(game.getGameID());
		player.setInGame(true);
		
		return game;
	}
	
	public void clearAllGames() {
		int games = 0;
		this.nextGameID = 1;
		
		if(this.waitingGame != null) {
			this.logger.log("Cleared waiting game.", ServerLog.LOG_INFO);
			Player player1 = this.waitingGame.getPlayer1();
			if(player1 != null) {
				player1.setGameID(0);
				player1.setInGame(false);
			} else {
				this.logger.log("Waiting game had no player 1...", ServerLog.LOG_WARNING);
			}
			this.waitingGame = null;
			games++;
		}
		
		for(Map.Entry<Integer, Game> entry : this.games.entrySet()) {
			Game game = entry.getValue();
			games++;
			Player player1 = this.waitingGame.getPlayer1();
			Player player2 = this.waitingGame.getPlayer2();
			if(player1 != null) {
				player1.setGameID(0);
				player1.setInGame(false);
			}
			if(player2 != null) {
				player2.setGameID(0);
				player2.setInGame(false);
			}
			this.logger.log("Cleared a running game.", ServerLog.LOG_INFO);
			this.games.remove(game.getGameID());
		}
		
		this.logger.log("Cleared " + games + " games.", ServerLog.LOG_INFO);
	}
}
