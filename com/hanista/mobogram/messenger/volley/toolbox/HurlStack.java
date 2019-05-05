package com.hanista.mobogram.messenger.volley.toolbox;

import com.hanista.mobogram.messenger.support.widget.helper.ItemTouchHelper.Callback;
import com.hanista.mobogram.messenger.volley.Request;
import com.hanista.mobogram.messenger.volley.Request.Method;
import com.hanista.mobogram.messenger.volley.toolbox.HttpClientStack.HttpPatch;
import com.hanista.mobogram.ui.Components.VideoPlayer;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;

public class HurlStack implements HttpStack {
    private static final String HEADER_CONTENT_TYPE = "Content-Type";
    private final SSLSocketFactory mSslSocketFactory;
    private final UrlRewriter mUrlRewriter;

    public interface UrlRewriter {
        String rewriteUrl(String str);
    }

    public HurlStack() {
        this(null);
    }

    public HurlStack(UrlRewriter urlRewriter) {
        this(urlRewriter, null);
    }

    public HurlStack(UrlRewriter urlRewriter, SSLSocketFactory sSLSocketFactory) {
        this.mUrlRewriter = urlRewriter;
        this.mSslSocketFactory = sSLSocketFactory;
    }

    private static void addBodyIfExists(HttpURLConnection httpURLConnection, Request<?> request) {
        byte[] body = request.getBody();
        if (body != null) {
            httpURLConnection.setDoOutput(true);
            httpURLConnection.addRequestProperty(HEADER_CONTENT_TYPE, request.getBodyContentType());
            DataOutputStream dataOutputStream = new DataOutputStream(httpURLConnection.getOutputStream());
            dataOutputStream.write(body);
            dataOutputStream.close();
        }
    }

    private static HttpEntity entityFromConnection(HttpURLConnection httpURLConnection) {
        InputStream inputStream;
        HttpEntity basicHttpEntity = new BasicHttpEntity();
        try {
            inputStream = httpURLConnection.getInputStream();
        } catch (IOException e) {
            inputStream = httpURLConnection.getErrorStream();
        }
        basicHttpEntity.setContent(inputStream);
        basicHttpEntity.setContentLength((long) httpURLConnection.getContentLength());
        basicHttpEntity.setContentEncoding(httpURLConnection.getContentEncoding());
        basicHttpEntity.setContentType(httpURLConnection.getContentType());
        return basicHttpEntity;
    }

    private static boolean hasResponseBody(int i, int i2) {
        return (i == 4 || ((100 <= i2 && i2 < Callback.DEFAULT_DRAG_ANIMATION_DURATION) || i2 == 204 || i2 == 304)) ? false : true;
    }

    private HttpURLConnection openConnection(URL url, Request<?> request) {
        HttpURLConnection createConnection = createConnection(url);
        int timeoutMs = request.getTimeoutMs();
        createConnection.setConnectTimeout(timeoutMs);
        createConnection.setReadTimeout(timeoutMs);
        createConnection.setUseCaches(false);
        createConnection.setDoInput(true);
        if ("https".equals(url.getProtocol()) && this.mSslSocketFactory != null) {
            ((HttpsURLConnection) createConnection).setSSLSocketFactory(this.mSslSocketFactory);
        }
        return createConnection;
    }

    static void setConnectionParametersForRequest(HttpURLConnection httpURLConnection, Request<?> request) {
        switch (request.getMethod()) {
            case VideoPlayer.TRACK_DISABLED /*-1*/:
                byte[] postBody = request.getPostBody();
                if (postBody != null) {
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.addRequestProperty(HEADER_CONTENT_TYPE, request.getPostBodyContentType());
                    DataOutputStream dataOutputStream = new DataOutputStream(httpURLConnection.getOutputStream());
                    dataOutputStream.write(postBody);
                    dataOutputStream.close();
                }
            case VideoPlayer.TRACK_DEFAULT /*0*/:
                httpURLConnection.setRequestMethod("GET");
            case VideoPlayer.TYPE_AUDIO /*1*/:
                httpURLConnection.setRequestMethod("POST");
                addBodyIfExists(httpURLConnection, request);
            case VideoPlayer.STATE_PREPARING /*2*/:
                httpURLConnection.setRequestMethod("PUT");
                addBodyIfExists(httpURLConnection, request);
            case VideoPlayer.STATE_BUFFERING /*3*/:
                httpURLConnection.setRequestMethod("DELETE");
            case VideoPlayer.STATE_READY /*4*/:
                httpURLConnection.setRequestMethod("HEAD");
            case VideoPlayer.STATE_ENDED /*5*/:
                httpURLConnection.setRequestMethod("OPTIONS");
            case Method.TRACE /*6*/:
                httpURLConnection.setRequestMethod("TRACE");
            case Method.PATCH /*7*/:
                httpURLConnection.setRequestMethod(HttpPatch.METHOD_NAME);
                addBodyIfExists(httpURLConnection, request);
            default:
                throw new IllegalStateException("Unknown method type.");
        }
    }

    protected HttpURLConnection createConnection(URL url) {
        return (HttpURLConnection) url.openConnection();
    }

    public HttpResponse performRequest(Request<?> request, Map<String, String> map) {
        String rewriteUrl;
        String url = request.getUrl();
        HashMap hashMap = new HashMap();
        hashMap.putAll(request.getHeaders());
        hashMap.putAll(map);
        if (this.mUrlRewriter != null) {
            rewriteUrl = this.mUrlRewriter.rewriteUrl(url);
            if (rewriteUrl == null) {
                throw new IOException("URL blocked by rewriter: " + url);
            }
        }
        rewriteUrl = url;
        HttpURLConnection openConnection = openConnection(new URL(rewriteUrl), request);
        for (String rewriteUrl2 : hashMap.keySet()) {
            openConnection.addRequestProperty(rewriteUrl2, (String) hashMap.get(rewriteUrl2));
        }
        setConnectionParametersForRequest(openConnection, request);
        ProtocolVersion protocolVersion = new ProtocolVersion("HTTP", 1, 1);
        if (openConnection.getResponseCode() == -1) {
            throw new IOException("Could not retrieve response code from HttpUrlConnection.");
        }
        StatusLine basicStatusLine = new BasicStatusLine(protocolVersion, openConnection.getResponseCode(), openConnection.getResponseMessage());
        HttpResponse basicHttpResponse = new BasicHttpResponse(basicStatusLine);
        if (hasResponseBody(request.getMethod(), basicStatusLine.getStatusCode())) {
            basicHttpResponse.setEntity(entityFromConnection(openConnection));
        }
        for (Entry entry : openConnection.getHeaderFields().entrySet()) {
            if (entry.getKey() != null) {
                basicHttpResponse.addHeader(new BasicHeader((String) entry.getKey(), (String) ((List) entry.getValue()).get(0)));
            }
        }
        return basicHttpResponse;
    }
}
