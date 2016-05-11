package org.toxiccloudgaming.manager;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;

import org.toxiccloudgaming.client.Client;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.util.Set;

public abstract class ActivityManager implements Prefs {

    private Context context;
    private Client client;
    private Thread thread;

    private SharedPreferences prefs;
    private SharedPreferences.Editor prefsEditor;

    public ActivityManager(Context context) {
        this.context = context;
        this.prefs = ((Activity)context).getPreferences(Activity.MODE_PRIVATE);
        this.prefsEditor = this.prefs.edit();

        this.preInit();
        this.initNetwork();
        this.initUI();
        this.postInit();
    }

    protected abstract void preInit();

    private void initNetwork() {
        CookieManager cookieManager = new CookieManager();
        CookieHandler.setDefault(cookieManager);

        if(this.client == null) this.client = new Client();
        this.thread = new Thread(this.client);
        this.thread.start();
    }

    protected abstract void initUI();

    protected void postInit() {}

    protected Client getClient() {
        return this.client;
    }

    protected void setClient(Client client) {
        this.client = client;
    }

    public Context getContext() {
        return this.context;
    }

    public Activity getActivityFromContext() {
        return (Activity)this.context;
    }

    public void start() {
        this.client.start();
    }

    public void stop() {
        this.client.stop();
    }

    public boolean createOptions(Menu menu) {
        return true;
    }

    public boolean prepareOptions(Menu menu) {
        return true;
    }

    public Object getPref(String key, Object def, int type) {
        switch(type) {
            case Prefs.TYPE_STRING:
                return this.prefs.getString(key, (String)def);
            case Prefs.TYPE_BOOLEAN:
                return this.prefs.getBoolean(key, (boolean)def);
            case Prefs.TYPE_INT:
                return this.prefs.getInt(key, (int)def);
            case Prefs.TYPE_FLOAT:
                return this.prefs.getFloat(key, (float)def);
            case Prefs.TYPE_LONG:
                return this.prefs.getLong(key, (long)def);
            case Prefs.TYPE_STRING_SET:
                return this.prefs.getStringSet(key, (Set<String>)def);
            default:
                return null;
        }
    }

    public void addPref(String key, Object value, int type) {
        this.removePref(key);

        switch(type) {
            case Prefs.TYPE_STRING:
                this.prefsEditor.putString(key, (String)value);
                break;
            case Prefs.TYPE_BOOLEAN:
                this.prefsEditor.putBoolean(key, (boolean)value);
                break;
            case Prefs.TYPE_INT:
                this.prefsEditor.putInt(key, (int)value);
                break;
            case Prefs.TYPE_FLOAT:
                this.prefsEditor.putFloat(key, (float)value);
                break;
            case Prefs.TYPE_LONG:
                this.prefsEditor.putLong(key, (long)value);
                break;
            case Prefs.TYPE_STRING_SET:
                this.prefsEditor.putStringSet(key, (Set<String>)value);
                break;
        }
    }

    public void removePref(String key) {
        if(this.prefs.contains(key)) {
            this.prefsEditor.remove(key);
        }
    }

    public void applyPrefs() {
        if(this.prefsEditor == null) {
            Log.d("BDH", "NULL EDITOR!");
        }
        this.prefsEditor.apply();
    }
}
