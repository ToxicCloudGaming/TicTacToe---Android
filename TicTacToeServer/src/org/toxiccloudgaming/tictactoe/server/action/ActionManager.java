package org.toxiccloudgaming.tictactoe.server.action;

import java.util.Map;

import org.json.simple.JSONObject;
import org.toxiccloudgaming.tictactoe.server.ServerLog;
import org.toxiccloudgaming.tictactoe.server.database.TicTacToeQuery;
import org.toxiccloudgaming.tictactoe.server.game.GameManager;
import org.toxiccloudgaming.tictactoe.server.player.Player;

public class ActionManager implements Action {
	
	private GameManager manager;
	private ServerLog logger;
	
	public ActionManager(GameManager manager, ServerLog logger) {
		this.manager = manager;
		this.logger = logger;
	}
	
	public JSONObject act(String sessionID, JSONObject json) {
		String action = (String)json.get(ActionsList.PARAMS_ACTION);	
		ActionResult result = new ActionResult(json, this.logger);
		
		switch(action) {
			case ActionsList.ACTION_UPDATE:
				result.setResponseType(ActionsList.RESPONSE_UPDATE);
				result.update(this.manager, sessionID);
				break;
			case ActionsList.ACTION_SIGN_IN:
				result.setResponseType(ActionsList.RESPONSE_SIGN_IN);
				result.signIn(this.manager, sessionID);
				break;
			case ActionsList.ACTION_SIGN_OUT:
				result.setResponseType(ActionsList.RESPONSE_SIGN_OUT);
				result.signOut(this.manager, sessionID);
				break;
			case ActionsList.ACTION_FIND_GAME:
				result.setResponseType(ActionsList.RESPONSE_FIND_GAME);
				result.findGame(this.manager, sessionID);
				break;
			case ActionsList.ACTION_LEAVE_GAME:
				result.setResponseType(ActionsList.RESPONSE_LEAVE_GAME);
				result.leaveGame(this.manager, sessionID);
				break;
			case ActionsList.ACTION_SEND_MOVE:
				result.setResponseType(ActionsList.RESPONSE_SEND_MOVE);
				result.sendMove(this.manager, sessionID);
				break;
			default:
				result.setResponseType(ActionsList.RESPONSE_INVALID_ACTION);
				break;
		}
		
		return result.getJsonResponse();
	}
}
