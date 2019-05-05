package com.hanista.mobogram.messenger.volley.toolbox;

import com.hanista.mobogram.messenger.volley.NetworkResponse;
import com.hanista.mobogram.messenger.volley.ParseError;
import com.hanista.mobogram.messenger.volley.Response;
import com.hanista.mobogram.messenger.volley.Response.ErrorListener;
import com.hanista.mobogram.messenger.volley.Response.Listener;
import org.json.JSONObject;

public class JsonObjectRequest extends JsonRequest<JSONObject> {
    public JsonObjectRequest(int i, String str, JSONObject jSONObject, Listener<JSONObject> listener, ErrorListener errorListener) {
        super(i, str, jSONObject == null ? null : jSONObject.toString(), listener, errorListener);
    }

    public JsonObjectRequest(String str, JSONObject jSONObject, Listener<JSONObject> listener, ErrorListener errorListener) {
        this(jSONObject == null ? 0 : 1, str, jSONObject, listener, errorListener);
    }

    protected Response<JSONObject> parseNetworkResponse(NetworkResponse networkResponse) {
        try {
            return Response.success(new JSONObject(new String(networkResponse.data, HttpHeaderParser.parseCharset(networkResponse.headers, "utf-8"))), HttpHeaderParser.parseCacheHeaders(networkResponse));
        } catch (Throwable e) {
            return Response.error(new ParseError(e));
        } catch (Throwable e2) {
            return Response.error(new ParseError(e2));
        }
    }
}
