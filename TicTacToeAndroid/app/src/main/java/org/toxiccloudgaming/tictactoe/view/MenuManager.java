package org.toxiccloudgaming.tictactoe.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONObject;
import org.toxiccloudgaming.manager.Prefs;
import org.toxiccloudgaming.resource.Res;
import org.toxiccloudgaming.tictactoe.GameManager;
import org.toxiccloudgaming.tictactoe.R;
import org.toxiccloudgaming.tictactoe.client.Action;
import org.toxiccloudgaming.tictactoe.client.ActionsList;
import org.toxiccloudgaming.tictactoe.client.GameClient;

public abstract class MenuManager implements ActionsList {

    public static AlertDialog buildDialog(Activity activity, int DialogID) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        View view = activity.getLayoutInflater().inflate(DialogID, null);
        builder.setView(view);

        AlertDialog dialog = builder.create();
        dialog.show();

        return dialog;
    }

    public static void about(Activity activity) {
        AlertDialog.Builder prompt = new AlertDialog.Builder(activity);
        prompt.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {}
        });

        String appName = Res.getString(activity, "app_name");
        String appVersion = Res.getString(activity, "app_version");
        String appDate = Res.getString(activity, "app_version_date");
        String devName = Res.getString(activity, "app_dev_name");
        String siteURL = Res.getString(activity, "app_website_url");

        String title = "Welcome to " + appName + "!";

        String message =
                "Development version " + appVersion + " released " + appDate + ".\n"
                + "Created by " + devName + ".\n\n"
                + "For the full website, please visit " + siteURL + ".";

        prompt.setTitle(title);
        prompt.setMessage(message);
        prompt.create().show();

    }

    public static void settings(final GameClient client, final GameManager manager) {
        final AlertDialog dialog = buildDialog(manager.getActivityFromContext(), R.layout.dialog_settings);

        final Button applyButton = (Button)dialog.findViewById(R.id.settings_apply);
        final Button resetButton = (Button)dialog.findViewById(R.id.settings_reset);
        final Button okButton = (Button)dialog.findViewById(R.id.settings_ok);

        final EditText ipAddress = (EditText)dialog.findViewById(R.id.input_settings_ip);
        ipAddress.setText(client.getHost());

        applyButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String address = ipAddress.getText().toString();

                client.setHost(address);
                manager.addPref(GameManager.PREFS_ADDRESS, address, Prefs.TYPE_STRING);
                manager.applyPrefs();
                manager.getResponseManager().prompt("Server IP changed!");
            }
        });

        resetButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                client.clearHost();
                ipAddress.setText(client.getHost());
                manager.removePref(GameManager.PREFS_ADDRESS);
                manager.applyPrefs();
                manager.getResponseManager().prompt("IP was reset to default value!");

                dialog.dismiss();
            }
        });

        okButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    public static void signIn(final GameClient client, final GameManager manager) {
        final AlertDialog dialog = buildDialog(manager.getActivityFromContext(), R.layout.dialog_sign_in);

        final Button submitButton = (Button)dialog.findViewById(R.id.sign_in_submit);
        final EditText username = (EditText)dialog.findViewById(R.id.input_sign_in_username);
        username.setText(client.getUsername());
        final EditText password = (EditText)dialog.findViewById(R.id.input_sign_in_password);

        submitButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if(!client.connected()) {
                    manager.getResponseManager().prompt("Cannot communicate with server!");
                } else {
                    submitButton.setEnabled(false);
                    submitButton.setClickable(false);
                    String user = username.getText().toString();
                    String pass = password.getText().toString();
                    String sessionID = client.getSessionID();
                    if(sessionID != null) {
                        Log.d("BDH", "SessionID: " + sessionID);
                    }

                    try {
                        JSONObject json = Action.signIn(client, user, pass, sessionID);
                        manager.getResponseManager().handleResponse(json, user, sessionID);
                        dialog.dismiss();
                    } catch (Exception e) {
                        Log.e("BDH", "Exception: ", e);
                    }

                    submitButton.setEnabled(true);
                    submitButton.setClickable(true);
                }
            }
        });
    }

    public static void signOut(final GameClient client, final GameManager manager) {
        if(!client.connected()) {
            manager.getResponseManager().prompt("Cannot communicate with server!");
        } else {

            String user = client.getUsername();
            String sessionID = client.getSessionID();

            try {
                JSONObject json = Action.signOut(client, user, sessionID);
                manager.getResponseManager().handleResponse(json, user, sessionID);
            } catch (Exception e) {
                Log.e("BDH", "Exception: ", e);
            }
        }
    }

    public static void findGame(final GameClient client, final GameManager manager) {
        manager.setSearchingForGame(true);

        if(!client.connected()) {
            manager.getResponseManager().prompt("Cannot communicate with server!");
        } else if(client.connected() && client.isSignedIn()){
            manager.getResponseManager().prompt("Searching for game...");
            String user = client.getUsername();
            String sessionID = client.getSessionID();

            try {
                JSONObject json = Action.findGame(client, user, sessionID);
                manager.getResponseManager().handleResponse(json, user, sessionID);
            } catch(Exception e) {
                Log.e("BDH", "Exception: ", e);
            }
        } else {
            manager.getResponseManager().prompt("You must be signed in first!");
        }
        manager.setSearchingForGame(false);
    }

    public static void leaveGame(final GameClient client, final GameManager manager) {
        if(!client.connected()) {
            manager.getResponseManager().prompt("Cannot communicate with server!");
        } else if(client.connected() && client.isSignedIn()){
            manager.getResponseManager().prompt("Leaving game...");
            String user = client.getUsername();
            String sessionID = client.getSessionID();

            try {
                JSONObject json = Action.leaveGame(client, user, sessionID);
                manager.getResponseManager().handleResponse(json, user, sessionID);
            } catch(Exception e) {
                Log.e("BDH", "Exception: ", e);
            }
        } else {
            manager.getResponseManager().prompt("You must be signed in first!");
        }
    }
}
