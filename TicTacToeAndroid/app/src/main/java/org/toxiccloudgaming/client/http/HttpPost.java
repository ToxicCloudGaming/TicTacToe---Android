package org.toxiccloudgaming.client.http;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

public class HttpPost extends HttpAction {

    private String postData;

    public HttpPost(String targetURL, HashMap<String, String> params) {
        super(targetURL, params);
    }

    public HttpPost(String targetURL) {
        this(targetURL, null);
    }

    @Override
    protected URL getURL() throws Exception {
        return new URL(this.targetURL);
    }

    @Override
    protected String getRequestMethod() {
        return HTTP_METHOD_POST;
    }

    @Override
    protected boolean doInput() {
        return true;
    }

    @Override
    protected boolean doOutput() {
        return true;
    }

    @Override
    protected HttpURLConnection postInit(HttpURLConnection conn) {

        if(this.params != null) {
            try {
                this.setPostData(this.getParamString(this.params));
            } catch(Exception e) {
                this.postDataException(e);
            }
        } else {
            this.postData = null;
        }

        return super.postInit(conn);
    }

    protected void postDataException(Exception e) {}

    protected void setPostData(String postData) {
        this.postData = postData;
    }

    @Override
    protected void write(HttpURLConnection conn) throws Exception {
        OutputStream os = conn.getOutputStream();
        OutputStreamWriter out = new OutputStreamWriter(os, "UTF-8");

        out.write(this.postData);
        out.flush();
        out.close();
    }
}
