package org.toxiccloudgaming.tictactoe.client;

import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.toxiccloudgaming.manager.Prefs;
import org.toxiccloudgaming.tictactoe.GameManager;

public class ResponseManager implements ActionsList {

    private GameManager manager;
    private GameClient client;

    private boolean debounceUpdate;

    public ResponseManager(GameManager manager, GameClient client) {
        this.manager = manager;
        this.client = client;
        this.debounceUpdate = false;
    }

    public void setUpdateDebounce(boolean debounceUpdate) {
        this.debounceUpdate = debounceUpdate;
    }

    public void handleResponse(JSONObject json, String username, String sessionID) {
        if(json != null) {
            try {
                String responseType = json.getString(PARAMS_RESPONSE);
                if(!responseType.equals(RESPONSE_INVALID_ACTION)) {
                    if(json.has(PARAMS_NEW_SESSION)) {
                        String newSessionID = json.getString(PARAMS_NEW_SESSION);
                        this.client.setSessionID(newSessionID);
                        this.manager.addPref(GameManager.PREFS_LAST_SESSION, newSessionID, Prefs.TYPE_STRING);
                    }
                    switch(responseType) {
                        case RESPONSE_SIGN_IN:
                            if(json.getBoolean(PARAMS_SUCCESS)) {
                                this.prompt("Successfully signed in!");
                                this.signIn(username, this.client.getSessionID());
                            } else {
                                this.prompt(json.getString(PARAMS_ERROR_MESSAGE));
                            }
                            break;
                        case RESPONSE_SIGN_OUT:
                            this.debounceUpdate = true;
                            this.signOut();
                            this.manager.flushGame();
                            this.manager.clearBoard();
                            if(json.getBoolean(PARAMS_SUCCESS)) {
                                this.prompt("Successfully signed out!");
                            } else {
                                this.prompt(json.getString(PARAMS_ERROR_MESSAGE));
                            }
                            break;
                        case RESPONSE_FIND_GAME:
                            if(json.getBoolean(PARAMS_ONLINE)) {
                                this.debounceUpdate = true;
                                JSONObject game = json.getJSONObject(PARAMS_GAME);
                                if(json.getBoolean(PARAMS_GAME_IN_PROGRESS)) {
                                    this.prompt("Reloaded game session!");
                                    this.manager.syncGame(game);
                                } else {
                                    this.manager.newGame(game, false);
                                }
                            } else {
                                this.signOut();
                                this.prompt("You were kicked from server!");
                            }
                            break;
                        case RESPONSE_LEAVE_GAME:
                            if(json.getBoolean(PARAMS_ONLINE)) {
                                if(json.has(PARAMS_GAME)) {
                                    JSONObject game = json.getJSONObject(PARAMS_GAME);
                                    this.manager.endGame(game);
                                    this.manager.clearBoard();
                                } else {
                                    this.prompt("You weren't in a game!");
                                    this.manager.flushGame();
                                    this.manager.clearBoard();
                                }
                            } else {
                                this.signOut();
                                this.prompt("You were kicked from server!");
                            }
                            break;
                        case RESPONSE_UPDATE:
                            if(this.debounceUpdate) {
                                this.debounceUpdate = false;
                            } else {
                                if(json.getBoolean(PARAMS_ONLINE)) {
                                    if(json.getBoolean(PARAMS_GAME_IN_PROGRESS)) {
                                        JSONObject game = json.getJSONObject(PARAMS_GAME);
                                        this.manager.syncGame(game);
                                    } else {
                                        if(json.has(PARAMS_GAME)) {
                                            if(this.manager.inGame()) {
                                                JSONObject game = json.getJSONObject(PARAMS_GAME);
                                                this.manager.endGame(game);
                                            }
                                        } else {
                                            if(this.manager.inGame()) {
                                                this.prompt("Game no longer available!");
                                                this.manager.flushGame();
                                                this.manager.clearBoard();
                                            }
                                        }
                                    }
                                } else {
                                    this.signOut();
                                    this.prompt("You were kicked from server!");
                                }
                            }
                            break;
                        case RESPONSE_SEND_MOVE:
                            if(json.getBoolean(PARAMS_ONLINE)) {
                                if(json.getBoolean(PARAMS_GAME_IN_PROGRESS)) {
                                    JSONObject game = json.getJSONObject(PARAMS_GAME);
                                    if(!json.getBoolean(PARAMS_SUCCESS)) {
                                        this.prompt("Move was unsuccessful...");
                                        this.manager.setGameStatus("Move invalid! Try again.");
                                    }
                                    this.manager.syncGame(game);
                                } else {
                                    if(json.has(PARAMS_GAME)) {
                                        JSONObject game = json.getJSONObject(PARAMS_GAME);
                                        this.manager.endGame(game);
                                    } else if(this.manager.inGame()) {
                                        this.prompt("Game no longer available!");
                                        this.manager.flushGame();
                                        this.manager.clearBoard();
                                    }
                                }
                            } else {
                                this.signOut();
                                this.prompt("You were kicked from server!");
                            }
                            break;
                        default:
                            break;
                    }
                } else {
                    prompt("Request was not valid.");
                }
            } catch(JSONException e) {
                Log.e("BDH", "Exception: ", e);
            }
        } else {
            prompt("No response...");
        }
    }

    public void signOut() {
        this.client.setSignedIn(false);
        this.client.setUsername("");
        this.client.setSessionID(null);

        this.manager.flushGame();
        this.manager.setGameStatus("You have signed out.");
        this.manager.setUsernameText(null);
        this.manager.removePref(GameManager.PREFS_USER);
        this.manager.removePref(GameManager.PREFS_LAST_SESSION);
        this.manager.applyPrefs();
    }

    public void signIn(String username, String sessionID) {
        this.client.setSignedIn(true);
        this.client.setUsername(username);
        this.client.setSessionID(sessionID);

        this.manager.setUsernameText(username);
        this.manager.addPref(GameManager.PREFS_USER, username, Prefs.TYPE_STRING);
        this.manager.addPref(GameManager.PREFS_LAST_SESSION, sessionID, Prefs.TYPE_STRING);
        this.manager.applyPrefs();
    }

    public void prompt(String message) {
        Toast.makeText(this.manager.getContext(), message, Toast.LENGTH_SHORT).show();
    }
}
