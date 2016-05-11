package org.toxiccloudgaming.tictactoe.client;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.toxiccloudgaming.client.http.json.JSONAction;

import java.util.HashMap;

public abstract class Action implements ActionsList {

    public static JSONObject signIn(final GameClient client, String username, String password, String sessionID) {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put(PARAMS_USER, username);
        params.put(PARAMS_PASS, password);
        if(sessionID != null) {
            params.put(PARAMS_LAST_SESSION, sessionID);
        }

        String response = client.sendAsyncJSON(createJSON(ACTION_SIGN_IN, params));
        return JSONAction.createJSONObj(response);
    }

    public static JSONObject signOut(final GameClient client, String username, String sessionID) {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put(PARAMS_USER, username);
        params.put(PARAMS_LAST_SESSION, sessionID);

        String response = client.sendAsyncJSON(createJSON(ACTION_SIGN_OUT, params));
        return JSONAction.createJSONObj(response);
    }

    public static JSONObject update(final GameClient client, String username, String sessionID) {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put(ActionsList.PARAMS_USER, username);
        params.put(ActionsList.PARAMS_LAST_SESSION, sessionID);

        String response = client.sendAsyncJSON(createJSON(ACTION_UPDATE, params));
        return JSONAction.createJSONObj(response);
    }

    public static JSONObject findGame(final GameClient client, String username, String sessionID) {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put(ActionsList.PARAMS_USER, username);
        params.put(ActionsList.PARAMS_LAST_SESSION, sessionID);

        String response = client.sendAsyncJSON(createJSON(ACTION_FIND_GAME, params));
        return JSONAction.createJSONObj(response);
    }

    public static JSONObject leaveGame(final GameClient client, String username, String sessionID) {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put(ActionsList.PARAMS_USER, username);
        params.put(ActionsList.PARAMS_LAST_SESSION, sessionID);

        String response = client.sendAsyncJSON(createJSON(ACTION_LEAVE_GAME, params));
        return JSONAction.createJSONObj(response);
    }

    public static JSONObject sendMove(final GameClient client, String username, String sessionID, int tileID) {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put(ActionsList.PARAMS_USER, username);
        params.put(ActionsList.PARAMS_LAST_SESSION, sessionID);
        params.put(ActionsList.PARAMS_MOVE, tileID);

        String response = client.sendAsyncJSON(createJSON(ACTION_SEND_MOVE, params));
        return JSONAction.createJSONObj(response);
    }

    public static String createJSON(String action, HashMap<String, Object> params) {
        String json = null;

        try {
            json = JSONAction.createJSONString(action, params);
        } catch(Exception e) {
            Log.e("BDH", "Exception: ", e);
        }

        return json;
    }

    public static boolean validResponse(JSONObject json, String response) throws JSONException {
        if(json != null) {
            if(json.getString(PARAMS_RESPONSE).equals(response)) {
                return true;
            }
        }
        return false;
    }
}
