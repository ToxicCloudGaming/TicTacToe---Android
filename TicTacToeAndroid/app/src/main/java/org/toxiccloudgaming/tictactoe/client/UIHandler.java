package org.toxiccloudgaming.tictactoe.client;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import org.json.JSONObject;
import org.toxiccloudgaming.client.http.json.JSONAction;
import org.toxiccloudgaming.tictactoe.GameManager;

public class UIHandler extends Handler implements ConnectHandler {

    private GameManager manager;
    boolean connected;

    public UIHandler(GameManager manager) {
        super(Looper.getMainLooper());
        this.manager = manager;
        this.connected = false;
    }

    @Override
    public void handleMessage(Message message) {
        Bundle data = message.getData();

        boolean connected = data.getBoolean(KEY_CAN_CONNECT);
        int ping = data.getInt(KEY_PING);

        if(!this.connected && connected) {
            this.connected = true;
            this.manager.connectionChanged(true);
        } else if(this.connected && !connected) {
            this.connected = false;
            this.manager.connectionChanged(false);
        }

        if(data.containsKey(KEY_UPDATE)) {
            JSONObject json = JSONAction.createJSONObj(data.getString(KEY_UPDATE));
            String username = data.getString(KEY_UPDATE_USERNAME);
            String sessionID = data.getString(KEY_UPDATE_SESSION);

            this.manager.getResponseManager().handleResponse(json, username, sessionID);
        }

        this.manager.updatePing(connected, ping);
    }
}
