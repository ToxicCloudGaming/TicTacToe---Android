package org.toxiccloudgaming.tictactoe.client;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import org.toxiccloudgaming.client.Client;
import org.toxiccloudgaming.client.http.HttpResponse;
import org.toxiccloudgaming.client.http.json.JSONPost;
import org.toxiccloudgaming.tictactoe.GameManager;

public class GameClient extends Client implements ConnectHandler {

    private String username;
    private String sessionID;
    private GameManager manager;
    private UIHandler handler;

    public final static String SERVER_DEFAULT_HOST = "www.toxiccloudgaming.org";
    public final static String SERVER_DEFAULT_ROOT = "/tictactoe";
    public final static long GAME_DEFAULT_DELAY = 500;

    private boolean signedIn;
    private boolean waiting;
    private HttpResponse response;
    private boolean updating;

    public GameClient(GameManager manager) {
        this.signedIn = false;
        this.waiting = false;
        this.response = null;
        this.updating = false;
        this.username = "";
        this.sessionID = null;
        this.setHost(SERVER_DEFAULT_HOST);
        this.setRoot(SERVER_DEFAULT_ROOT);
        this.setHttps(false);

        this.setDelay(GAME_DEFAULT_DELAY);
        this.manager = manager;
        this.handler = new UIHandler(this.manager);
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return this.username;
    }

    public void setSessionID(String sessionID) {
        this.sessionID = sessionID;
    }

    public String getSessionID() {
        return this.sessionID;
    }

    public boolean isSignedIn() {
        return this.signedIn;
    }

    public void setSignedIn(boolean signedIn) {
        this.signedIn = signedIn;
        if(!signedIn) {
            this.username = "";
        }
    }

    @Override
    protected void tick() {
        Bundle data = new Bundle();
        data.putBoolean(KEY_CAN_CONNECT, this.connected());
        data.putInt(KEY_PING, this.getPing());
        String update = doUpdate();
        if(update != null) {
            data.putString(KEY_UPDATE, update);
            data.putString(KEY_UPDATE_USERNAME, this.username);
            data.putString(KEY_UPDATE_SESSION, this.sessionID);
        }

        Message message = handler.obtainMessage();
        message.setData(data);
        handler.sendMessage(message);
    }

    private String doUpdate() {
        if(this.connected() && this.signedIn && !this.updating) {
            this.updating = true;

            String json = Action.update(this, this.username, this.sessionID).toString();

            this.updating = false;
            return json;
        }
        return null;
    }

    public synchronized String sendAsyncJSON(final String json) {
        if(!json.contains("update")) {
            Log.d("BDH", "Request: " + json);
        }
        this.waiting = true;

        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... params) {
                JSONPost post = new JSONPost(GameClient.this.getTargetURL(), json);
                HttpResponse response = null;

                try {
                    response = post.send();
                } catch(Exception e) {
                    Log.e("BDH", "Exception: ", e);
                }

                GameClient.this.setJsonResponse(response);
                GameClient.this.setWaiting(false);

                return null;
            }
        }.execute();

        while(waiting) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Log.e("BDH", "Exception: ", e);
            }
        }

        HttpResponse response = this.getJsonResponse();

        if(response != null) {
            if(!json.contains("update")) {
                Log.d("BDH", "Response: " + response.toString());
            }
            return response.toString();
        }
        Log.d("BDH", "Response: NONE");
        return "Could not connect to server.";
    }

    public String sendSyncJSON(final String json) {
        JSONPost post = new JSONPost(this.getTargetURL(), json);
        HttpResponse response = null;

        try {
            response = post.send();
        } catch(Exception e) {
            Log.e("BDH", "Exception: ", e);
        }

        if(response != null) {
            return response.toString();
        } else {
            return "Could not connect to server.";
        }
    }

    public boolean isWaiting() {
        return this.waiting;
    }

    public void setWaiting(boolean waiting) {
        this.waiting = waiting;
    }

    public HttpResponse getJsonResponse() {
        return this.response;
    }

    public void setJsonResponse(HttpResponse response) {
        this.response = response;
    }

    @Override
    public void clearHost() {
        this.setHost(SERVER_DEFAULT_HOST);
    }

    @Override
    public void clearRoot() {
        this.setRoot(SERVER_DEFAULT_ROOT);
    }

    public boolean testConnection() {
        return super.testConnection();
    }
}
