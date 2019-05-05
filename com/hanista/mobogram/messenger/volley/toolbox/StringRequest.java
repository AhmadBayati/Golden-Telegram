package com.hanista.mobogram.messenger.volley.toolbox;

import com.hanista.mobogram.messenger.volley.NetworkResponse;
import com.hanista.mobogram.messenger.volley.Request;
import com.hanista.mobogram.messenger.volley.Response;
import com.hanista.mobogram.messenger.volley.Response.ErrorListener;
import com.hanista.mobogram.messenger.volley.Response.Listener;
import java.io.UnsupportedEncodingException;

public class StringRequest extends Request<String> {
    private final Listener<String> mListener;

    public StringRequest(int i, String str, Listener<String> listener, ErrorListener errorListener) {
        super(i, str, errorListener);
        this.mListener = listener;
    }

    public StringRequest(String str, Listener<String> listener, ErrorListener errorListener) {
        this(0, str, listener, errorListener);
    }

    protected void deliverResponse(String str) {
        this.mListener.onResponse(str);
    }

    protected Response<String> parseNetworkResponse(NetworkResponse networkResponse) {
        Object str;
        try {
            str = new String(networkResponse.data, HttpHeaderParser.parseCharset(networkResponse.headers));
        } catch (UnsupportedEncodingException e) {
            str = new String(networkResponse.data);
        }
        return Response.success(str, HttpHeaderParser.parseCacheHeaders(networkResponse));
    }
}
