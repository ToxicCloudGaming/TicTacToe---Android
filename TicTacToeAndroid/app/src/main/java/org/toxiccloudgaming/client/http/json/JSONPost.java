package org.toxiccloudgaming.client.http.json;

import org.toxiccloudgaming.client.http.HttpPost;

import java.net.HttpURLConnection;

public class JSONPost extends HttpPost {

    public JSONPost(String targetURL, String jsonData) {
        super(targetURL);
        this.setPostData(jsonData);
    }

    @Override
    protected HttpURLConnection postInit(HttpURLConnection conn) {
        return conn;
    }
}
