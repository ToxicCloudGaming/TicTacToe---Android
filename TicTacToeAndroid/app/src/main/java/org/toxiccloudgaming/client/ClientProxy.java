package org.toxiccloudgaming.client;

public abstract class ClientProxy {

    /*
        ClientProxy is a TCG class used for connecting a client to a server.
     */

    protected final static String HTTP_PREFIX = "http://";
    protected final static String HTTPS_PREFIX = "https://";
    protected final static String HOST_DEFAULT = "localhost";

    //Full URL: PREFIX + HOST + ROOT + DIRECTORY
    //e.g. http://127.0.0.1/root/directory
    private boolean isHttps;
    private String httpHost;
    private String httpRoot;
    private String httpDir;

    public ClientProxy(String httpHost, boolean isHttps) {
        this.isHttps = isHttps;
        this.httpHost = httpHost;
        this.httpRoot = "/";
        this.httpDir = "";
    }

    public ClientProxy(boolean isHttps) {
        this(HOST_DEFAULT, isHttps);
    }

    public ClientProxy(String httpHost) {
        this(httpHost, false);
    }

    public ClientProxy() {
        this(HOST_DEFAULT);
    }

    public void setHttps(boolean isHttps) {
        this.isHttps = isHttps;
    }

    public boolean isHttps() {
        return this.isHttps;
    }

    public void setHost(String httpHost) {
        this.httpHost = httpHost;
    }

    public String getHost() {
        return this.httpHost;
    }

    public void clearHost() {
        this.httpHost = HOST_DEFAULT;
    }

    public void setRoot(String httpRoot) {
        this.httpRoot = httpRoot;
    }

    public String getRoot() {
        return this.httpRoot;
    }

    public void clearRoot() {
        this.httpRoot = "/";
    }

    public void setDir(String httpDir) {
        this.httpDir = httpDir;
    }

    public String getDir() {
        return this.httpDir;
    }

    public void clearDir() {
        this.httpDir = "";
    }

    public void clear() {
        this.clearHost();
        this.clearRoot();
        this.clearDir();
    }

    public String getTargetURL() {
        String httpSuffix = this.httpHost + this.httpRoot + this.httpDir;
        if(this.isHttps) return HTTPS_PREFIX + httpSuffix;
        return HTTP_PREFIX + httpSuffix;
    }
}
