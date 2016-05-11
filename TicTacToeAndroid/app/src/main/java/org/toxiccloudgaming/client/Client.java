package org.toxiccloudgaming.client;

import org.toxiccloudgaming.client.http.HttpGet;
import org.toxiccloudgaming.client.http.HttpResponse;

import java.net.HttpURLConnection;
import java.util.Stack;

public class Client extends ClientProxy implements Runnable {

    public final static long DEFAULT_DELAY = 1000;
    public final static int DEFAULT_PING_LIMIT = 5;

    private boolean connected;
    private Stack<Integer> ping;
    private int ping_average;
    private int ping_limit;

    private long delay;
    boolean running;

    public Client() {
        this.connected = false;
        this.setDelay(DEFAULT_DELAY);
        this.ping = new Stack<>();
        this.ping_average = 0;
        this.ping_limit = DEFAULT_PING_LIMIT;
        this.start();
    }

    public boolean connected() {
        return this.connected;
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }

    public long getDelay() {
        return this.delay;
    }

    @Override
    public void run() {
        while(running) {
            try {
                Thread.sleep(this.delay);

                this.tick();
            } catch (InterruptedException e) {
                this.sleepException(e);
            }

            this.connected = (this.testConnection());
        }
    }

    protected void tick() {}

    protected boolean testConnection() {
        HttpResponse response = null;
        try {
            response = (new HttpGet(this.getTargetURL())).send();
        } catch (Exception e) {
            this.connectException(e);
        }

        if(response != null && response.getResponseCode() == HttpURLConnection.HTTP_OK) {
            this.ping(response.getResponseTime());
            return true;
        } else {
            this.clearPings();
            return false;
        }
    }

    public void clearPings() {
        this.ping.empty();
    }

    public void ping(int responseTime) {
        this.ping.push(responseTime);
        if (this.ping.size() >= this.ping_limit){
            int ping_sum = 0;
            int pings = 0;
            while(!this.ping.isEmpty()) {
                ping_sum += this.ping.pop();
                pings++;
            }

            this.ping_average = (int)Math.round(ping_sum / pings);
        }
    }

    public int getPing() {
        return this.ping_average;
    }

    protected void connectException(Exception e) {}

    protected void sleepException(Exception e) {}

    public void stop() {
        this.running = false;
    }

    public void start() {
        this.running = true;
    }
}
