package org.toxiccloudgaming.client.http;

import java.net.URL;
import java.util.HashMap;

public class HttpGet extends HttpAction {

    public HttpGet(String targetURL, HashMap<String, String> params) {
        super(targetURL, params);
    }

    public HttpGet(String targetURL) {
        this(targetURL, null);
    }

    @Override
    protected URL getURL() throws Exception {
        if(this.params != null) {
            return new URL(this.targetURL + "?" + this.getParamString(this.params));
        }
        return new URL(this.targetURL);
    }

    @Override
    protected String getRequestMethod() {
        return HTTP_METHOD_GET;
    }

    @Override
    protected boolean doInput() {
        return true;
    }

    @Override
    protected boolean doOutput() {
        return false;
    }
}
