package org.toxiccloudgaming.tictactoe.client;

public interface ActionsList {

    //Parameter Strings for Client to Server
    String ACTION_UPDATE = "update";
    String ACTION_SIGN_IN = "sign_in";
    String ACTION_SIGN_OUT = "sign_out";
    String ACTION_SEND_MOVE = "send_move";
    String ACTION_FIND_GAME = "find_game";
    String ACTION_LEAVE_GAME = "leave_game";

    String PARAMS_ACTION = "action";
    String PARAMS_USER = "user";
    String PARAMS_PASS = "pass";
    String PARAMS_LAST_SESSION = "lastSession";
    String PARAMS_MOVE = "move";

    //Parameter Strings for Server to Client
    String RESPONSE_INVALID_ACTION = "invalid_action";
    String RESPONSE_SIGN_IN = "sign_in";
    String RESPONSE_SIGN_OUT = "sign_out";
    String RESPONSE_SEND_MOVE = "send_move";
    String RESPONSE_UPDATE = "update";
    String RESPONSE_FIND_GAME = "find_game";
    String RESPONSE_LEAVE_GAME = "leave_game";

    String PARAMS_RESPONSE = "response";
    String PARAMS_SUCCESS = "wasSuccessful";
    String PARAMS_ERROR_MESSAGE = "errorMessage";
    String PARAMS_NEW_SESSION = "newSession";
    String PARAMS_ONLINE = "online";
    String PARAMS_GAME_IN_PROGRESS = "gameInProgress";
    String PARAMS_GAME = "game";
}