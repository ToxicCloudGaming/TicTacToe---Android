package org.toxiccloudgaming.client.http;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public abstract class HttpAction implements IHttpAction {

    protected String targetURL;
    protected HashMap<String, String> params;

    protected URL url;

    public HttpAction(String targetURL, HashMap<String, String> params) {
        this.targetURL = targetURL;
        this.params = params;
    }

    public HttpAction(String targetURL) {
        this(targetURL, null);
    }

    public HttpAction() {
        this(null);
    }

    //This sends the actual request. It will return the time it takes to send/receive a response.
    public HttpResponse send() throws Exception {
        long startTime = System.currentTimeMillis();
        HttpURLConnection conn = this.preInit();
        conn = this.postInit(conn);

        String result = null;

        if(this.doOutput()) {
            try {
                this.write(conn);
            } catch(Exception e) {
                this.writeException(e);
            }
        }

        int responseCode = conn.getResponseCode();

        try {
            result = this.read(conn);
        } catch(Exception e) {
            this.readException(e);
        }

        long endTime = System.currentTimeMillis();

        int responseTime = (int)(endTime - startTime);

        return new HttpResponse(result, responseCode, responseTime);
    }

    //Override this to send a body response to the url.
    protected void write(HttpURLConnection conn) throws Exception {}

    //This can be overridden for custom read implementation.
    protected String read(HttpURLConnection conn) throws Exception {
        String result = null;

        if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
            InputStream is = conn.getInputStream();
            InputStreamReader isr = new InputStreamReader(is, "UTF-8");
            BufferedReader in = new BufferedReader(isr);

            String line = null;
            StringBuilder sb = new StringBuilder();
            while((line = in.readLine()) != null) {
                sb.append(line);
            }

            result = sb.toString();

            in.close();
        }

        return result;
    }

    //These will execute if write/read throw an exception.
    protected void writeException(Exception e) {}
    protected void readException(Exception e) {}

    /*
        PRE-INITIALIZATION: Make sure to give values for these functions!
     */
    protected abstract URL getURL() throws Exception;
    protected abstract String getRequestMethod();
    protected abstract boolean doInput();
    protected abstract boolean doOutput();

    /*
        The values for these functions are set by default. Override them if needed.
     */
    protected String getUserAgent() {
        return System.getProperty("http.agent");
    }

    protected int getConnectTimeout() {
        return TIME_OUT_CONNECT;
    }

    protected int getReadTimeout() {
        return TIME_OUT_READ;
    }

    //This CANNOT be overriden! Set the values in get() functions instead.
    private HttpURLConnection preInit() {
        HttpURLConnection conn = null;
        try {
            this.url = this.getURL();
            conn = (HttpURLConnection) this.url.openConnection();
            conn.setRequestMethod(this.getRequestMethod());

            conn.setConnectTimeout(this.getConnectTimeout());
            conn.setReadTimeout(this.getReadTimeout());
            conn.setDoInput(this.doInput());
            conn.setDoOutput(this.doOutput());
            conn.setRequestProperty("User-Agent", this.getUserAgent());
        } catch(Exception e) {
            this.preInitException(e);
        }
        return conn;
    }

    //If any additional initialization is needed, override this method.
    protected HttpURLConnection postInit(HttpURLConnection conn) {
        return conn;
    }

    //Override this for exceptions caught during pre-initialization.
    public void preInitException(Exception e) {}

    //This method will create a string from a parameter list.
    protected String getParamString(HashMap<String, String> params) throws Exception {
        StringBuilder data = new StringBuilder();

        boolean firstParam = true;
        if(params != null) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                if (firstParam) {
                    firstParam = false;
                } else {
                    data.append("&");
                }

                data.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                data.append("=");
                data.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            }
        }

        return data.toString();
    }
}
