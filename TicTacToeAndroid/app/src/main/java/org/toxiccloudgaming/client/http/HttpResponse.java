package org.toxiccloudgaming.client.http;

public class HttpResponse {

    private String result;
    private int responseTime;
    private int responseCode;

    HttpResponse(String result, int responseCode, int responseTime) {
        this.result = result;
        this.responseTime = responseTime;
        this.responseCode = responseCode;
    }

    public String toString() {
        return this.result;
    }

    public int getResponseTime() {
        return this.responseTime;
    }

    public int getResponseCode() {
        return this.responseCode;
    }
}
