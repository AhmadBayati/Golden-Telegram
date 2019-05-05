package com.hanista.mobogram.messenger.volley.toolbox;

import com.hanista.mobogram.messenger.volley.NetworkResponse;
import com.hanista.mobogram.messenger.volley.Request;
import com.hanista.mobogram.messenger.volley.Response;
import com.hanista.mobogram.messenger.volley.Response.ErrorListener;
import com.hanista.mobogram.messenger.volley.Response.Listener;
import com.hanista.mobogram.messenger.volley.VolleyLog;
import java.io.UnsupportedEncodingException;

public abstract class JsonRequest<T> extends Request<T> {
    protected static final String PROTOCOL_CHARSET = "utf-8";
    private static final String PROTOCOL_CONTENT_TYPE;
    private final Listener<T> mListener;
    private final String mRequestBody;

    static {
        PROTOCOL_CONTENT_TYPE = String.format("application/json; charset=%s", new Object[]{PROTOCOL_CHARSET});
    }

    public JsonRequest(int i, String str, String str2, Listener<T> listener, ErrorListener errorListener) {
        super(i, str, errorListener);
        this.mListener = listener;
        this.mRequestBody = str2;
    }

    public JsonRequest(String str, String str2, Listener<T> listener, ErrorListener errorListener) {
        this(-1, str, str2, listener, errorListener);
    }

    protected void deliverResponse(T t) {
        this.mListener.onResponse(t);
    }

    public byte[] getBody() {
        byte[] bArr = null;
        try {
            if (this.mRequestBody != null) {
                bArr = this.mRequestBody.getBytes(PROTOCOL_CHARSET);
            }
        } catch (UnsupportedEncodingException e) {
            VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", this.mRequestBody, PROTOCOL_CHARSET);
        }
        return bArr;
    }

    public String getBodyContentType() {
        return PROTOCOL_CONTENT_TYPE;
    }

    public byte[] getPostBody() {
        return getBody();
    }

    public String getPostBodyContentType() {
        return getBodyContentType();
    }

    protected abstract Response<T> parseNetworkResponse(NetworkResponse networkResponse);
}
