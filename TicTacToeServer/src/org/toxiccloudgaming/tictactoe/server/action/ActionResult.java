package org.toxiccloudgaming.tictactoe.server.action;

import org.json.simple.JSONObject;
import org.toxiccloudgaming.tictactoe.server.ServerLog;
import org.toxiccloudgaming.tictactoe.server.database.TicTacToeQuery;
import org.toxiccloudgaming.tictactoe.server.game.Game;
import org.toxiccloudgaming.tictactoe.server.game.GameManager;
import org.toxiccloudgaming.tictactoe.server.player.Player;

@SuppressWarnings("unchecked")
public class ActionResult implements ActionsList {
	
	private JSONObject jsonResponse;
	private JSONObject jsonRequest;
	private ServerLog logger;
	
	public ActionResult(JSONObject jsonRequest, ServerLog logger) {
		this.jsonResponse = new JSONObject();
		this.jsonRequest = jsonRequest;
		this.logger = logger;
	}
	
	public void setResponseType(String responseType) {
		this.jsonResponse.put(PARAMS_RESPONSE, responseType);
	}
	
	public void signIn(GameManager manager, String sessionID) {
		try {
			String username = (String)this.jsonRequest.get(PARAMS_USER);
			String password = (String)this.jsonRequest.get(PARAMS_PASS);
			String clientSessionID = (String)this.jsonRequest.get(PARAMS_LAST_SESSION);
			
			if(manager.playerOnline(username)) {
				if(this.authorize(manager, sessionID)) {
					this.jsonResponse.put(PARAMS_SUCCESS, true);
				} else {
					this.jsonResponse.put(PARAMS_SUCCESS, false);
					this.jsonResponse.put(PARAMS_ERROR_MESSAGE, "Could not sign in: That username "
							+ "is already in use!");
				}
			} else {
				if(manager.sessionOnline(sessionID) || manager.sessionOnline(clientSessionID)) {
					this.jsonResponse.put(PARAMS_SUCCESS, false);
					this.jsonResponse.put(PARAMS_ERROR_MESSAGE, "Could not sign in: Session is "
							+ "already active under a different username!");
				} else {
					if(TicTacToeQuery.signIn(username, password, this.logger)) {
						this.jsonResponse.put(PARAMS_SUCCESS, true);
						Player player = TicTacToeQuery.updatePlayer(username, this.logger);
						player.changeSessionID(sessionID);
						manager.playerJoined(username, player);
						this.logger.log("Player " + username + " logged in with session "
								+ sessionID + ".", ServerLog.LOG_INFO);
					} else {
						this.jsonResponse.put(PARAMS_SUCCESS, false);
						this.jsonResponse.put(PARAMS_ERROR_MESSAGE, "Could not sign in: That "
								+ "username/password combination does not exist!");
					}
					
					if(clientSessionID == null || !sessionID.equals(clientSessionID)) {
						this.jsonResponse.put(PARAMS_NEW_SESSION, sessionID);
					}
				}
			}
		} catch(NullPointerException e) {
			this.logger.logException(e);
		}
	}
	
	public void signOut(GameManager manager, String sessionID) {
		try {
			String username = (String)this.jsonRequest.get(PARAMS_USER);
			
			if(manager.playerOnline(username)) {
				if(manager.getPlayer(username).getSessionID().equals(sessionID)) {
					Player player = manager.getPlayer(username);
					TicTacToeQuery.savePlayer(player, this.logger);
					manager.playerLeft(player);
					this.jsonResponse.put(PARAMS_SUCCESS, true);
					this.logger.log("Player " + username + " logged out.", ServerLog.LOG_INFO);
				} else {
					this.jsonResponse.put(PARAMS_SUCCESS, false);
					this.jsonResponse.put(PARAMS_ERROR_MESSAGE, "That username is active under "
							+ "a different session!");
				}
			} else {
				this.jsonResponse.put(PARAMS_SUCCESS, false);
				this.jsonResponse.put(PARAMS_ERROR_MESSAGE, "You are already signed out!");
			}
		} catch(NullPointerException e) {
			this.logger.logException(e);
		}
	}
	
	public void update(GameManager manager, String sessionID) {
		try {
			String username = (String)this.jsonRequest.get(PARAMS_USER);
				
			if(this.online(manager, username, sessionID)) {
				Player player = manager.getPlayer(username);
				if(manager.playerHasGame(player)) {
					Game game = manager.getGame(player.getGameID());
					
					this.jsonResponse.put(PARAMS_GAME, game.toJSONObject());
					if(!game.isGameOver()) {
						this.jsonResponse.put(PARAMS_GAME_IN_PROGRESS, true);
					} else {
						this.jsonResponse.put(PARAMS_GAME_IN_PROGRESS, false);
						if(game.playerLeftGame(player)) {
							manager.destroyGame(game);
						}
					}
				} else {
					this.jsonResponse.put(PARAMS_GAME_IN_PROGRESS, false);
				}
			}
		} catch(NullPointerException e) {
			this.logger.logException(e);
		}
	}
	
	public void findGame(GameManager manager, String sessionID) {
		try {
			String username = (String)this.jsonRequest.get(PARAMS_USER);
			
			if(this.online(manager, username, sessionID)) {
				Player player = manager.getPlayer(username);
				Game game = null;
				if(manager.playerHasGame(player)) {
					game = manager.getGame(player.getGameID());
					this.jsonResponse.put(PARAMS_GAME_IN_PROGRESS, true);
				} else {
					game = manager.createGame(player);
					this.jsonResponse.put(PARAMS_GAME_IN_PROGRESS, false);
				}
				this.jsonResponse.put(PARAMS_GAME, game.toJSONObject());
			}
		} catch(NullPointerException e) {
			this.logger.logException(e);
		}
	}
	
	public void leaveGame(GameManager manager, String sessionID) {
		try {
			String username = (String)this.jsonRequest.get(PARAMS_USER);
			
			if(this.online(manager, username, sessionID)) {
				Player player = manager.getPlayer(username);
				if(manager.playerHasGame(player)) {
					Game game = manager.leaveGame(player);
					
					this.jsonResponse.put(PARAMS_GAME, game.toJSONObject());
				}
			}
		} catch(NullPointerException e) {
			this.logger.logException(e);
		}
	}
	
	public void sendMove(GameManager manager, String sessionID) {
		try {
			String username = (String)this.jsonRequest.get(PARAMS_USER);
			int tileID = ((Long)this.jsonRequest.get(PARAMS_MOVE)).intValue();
			this.logger.log("Player " + username + " clicked tile " + tileID + ".", ServerLog.LOG_INFO);
			
			if(this.online(manager, username, sessionID)) {
				Player player = manager.getPlayer(username);
				if(manager.playerHasGame(player)) {
					Game game = manager.getGame(player.getGameID());
					this.jsonResponse.put(PARAMS_GAME, game.toJSONObject());
					if(!game.isGameOver()) {
						if(game.tryMove(player, tileID)) {
							this.jsonResponse.put(PARAMS_SUCCESS, true);
						} else {
							this.jsonResponse.put(PARAMS_SUCCESS, false);
						}
						this.jsonResponse.put(PARAMS_GAME_IN_PROGRESS, true);
					} else {
						this.jsonResponse.put(PARAMS_GAME_IN_PROGRESS, false);
						if(game.playerLeftGame(player)) {
							manager.destroyGame(game);
						}
					}
				} else {
					this.jsonResponse.put(PARAMS_GAME_IN_PROGRESS, false);
				}
			}
		} catch(NullPointerException e) {
			this.logger.logException(e);
		}
	}
	
	public boolean online(GameManager manager, String username, String sessionID) {
		if(manager.playerOnline(username) && this.authorize(manager, sessionID)) {
			this.jsonResponse.put(PARAMS_ONLINE, true);
			return true;
		} else {
			this.jsonResponse.put(PARAMS_ONLINE, false);
			return false;
		}
	}
	
	public boolean authorize(GameManager manager, String sessionID) {
		String username = (String)this.jsonRequest.get(PARAMS_USER);
		String clientSessionID = (String)this.jsonRequest.get(PARAMS_LAST_SESSION);
		
		try {
			Player player = manager.getPlayer(username);
			String playerSessionID = player.getSessionID();
			if(playerSessionID.equals(sessionID)) {
				if(!sessionID.equals(clientSessionID)) {
					this.jsonResponse.put(PARAMS_NEW_SESSION, sessionID);
				}
				return true;
			} else if(clientSessionID != null && playerSessionID.equals(clientSessionID) ){
				this.jsonResponse.put(PARAMS_NEW_SESSION, sessionID);
				this.logger.log("Player " + username + "'s session changed to " + sessionID + ".", ServerLog.LOG_INFO);
				player.changeSessionID(sessionID);
				return true;
			}
			return false;
		} catch(NullPointerException e) {
			this.logger.logException(e);
		}
		return false;
	}
	
	public JSONObject getJsonResponse() {
		return this.jsonResponse;
	}
}
