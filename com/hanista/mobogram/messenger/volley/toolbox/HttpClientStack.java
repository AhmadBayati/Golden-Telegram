package com.hanista.mobogram.messenger.volley.toolbox;

import com.hanista.mobogram.messenger.exoplayer.ExoPlayer.Factory;
import com.hanista.mobogram.messenger.volley.Request;
import com.hanista.mobogram.messenger.volley.Request.Method;
import com.hanista.mobogram.ui.Components.VideoPlayer;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpTrace;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

public class HttpClientStack implements HttpStack {
    private static final String HEADER_CONTENT_TYPE = "Content-Type";
    protected final HttpClient mClient;

    public static final class HttpPatch extends HttpEntityEnclosingRequestBase {
        public static final String METHOD_NAME = "PATCH";

        public HttpPatch(String str) {
            setURI(URI.create(str));
        }

        public HttpPatch(URI uri) {
            setURI(uri);
        }

        public String getMethod() {
            return METHOD_NAME;
        }
    }

    public HttpClientStack(HttpClient httpClient) {
        this.mClient = httpClient;
    }

    private static void addHeaders(HttpUriRequest httpUriRequest, Map<String, String> map) {
        for (String str : map.keySet()) {
            httpUriRequest.setHeader(str, (String) map.get(str));
        }
    }

    static HttpUriRequest createHttpRequest(Request<?> request, Map<String, String> map) {
        HttpUriRequest httpPost;
        switch (request.getMethod()) {
            case VideoPlayer.TRACK_DISABLED /*-1*/:
                byte[] postBody = request.getPostBody();
                if (postBody == null) {
                    return new HttpGet(request.getUrl());
                }
                httpPost = new HttpPost(request.getUrl());
                httpPost.addHeader(HEADER_CONTENT_TYPE, request.getPostBodyContentType());
                httpPost.setEntity(new ByteArrayEntity(postBody));
                return httpPost;
            case VideoPlayer.TRACK_DEFAULT /*0*/:
                return new HttpGet(request.getUrl());
            case VideoPlayer.TYPE_AUDIO /*1*/:
                httpPost = new HttpPost(request.getUrl());
                httpPost.addHeader(HEADER_CONTENT_TYPE, request.getBodyContentType());
                setEntityIfNonEmptyBody(httpPost, request);
                return httpPost;
            case VideoPlayer.STATE_PREPARING /*2*/:
                httpPost = new HttpPut(request.getUrl());
                httpPost.addHeader(HEADER_CONTENT_TYPE, request.getBodyContentType());
                setEntityIfNonEmptyBody(httpPost, request);
                return httpPost;
            case VideoPlayer.STATE_BUFFERING /*3*/:
                return new HttpDelete(request.getUrl());
            case VideoPlayer.STATE_READY /*4*/:
                return new HttpHead(request.getUrl());
            case VideoPlayer.STATE_ENDED /*5*/:
                return new HttpOptions(request.getUrl());
            case Method.TRACE /*6*/:
                return new HttpTrace(request.getUrl());
            case Method.PATCH /*7*/:
                httpPost = new HttpPatch(request.getUrl());
                httpPost.addHeader(HEADER_CONTENT_TYPE, request.getBodyContentType());
                setEntityIfNonEmptyBody(httpPost, request);
                return httpPost;
            default:
                throw new IllegalStateException("Unknown request method.");
        }
    }

    private static List<NameValuePair> getPostParameterPairs(Map<String, String> map) {
        List<NameValuePair> arrayList = new ArrayList(map.size());
        for (String str : map.keySet()) {
            arrayList.add(new BasicNameValuePair(str, (String) map.get(str)));
        }
        return arrayList;
    }

    private static void setEntityIfNonEmptyBody(HttpEntityEnclosingRequestBase httpEntityEnclosingRequestBase, Request<?> request) {
        byte[] body = request.getBody();
        if (body != null) {
            httpEntityEnclosingRequestBase.setEntity(new ByteArrayEntity(body));
        }
    }

    protected void onPrepareRequest(HttpUriRequest httpUriRequest) {
    }

    public HttpResponse performRequest(Request<?> request, Map<String, String> map) {
        HttpUriRequest createHttpRequest = createHttpRequest(request, map);
        addHeaders(createHttpRequest, map);
        addHeaders(createHttpRequest, request.getHeaders());
        onPrepareRequest(createHttpRequest);
        HttpParams params = createHttpRequest.getParams();
        int timeoutMs = request.getTimeoutMs();
        HttpConnectionParams.setConnectionTimeout(params, Factory.DEFAULT_MIN_REBUFFER_MS);
        HttpConnectionParams.setSoTimeout(params, timeoutMs);
        return this.mClient.execute(createHttpRequest);
    }
}
