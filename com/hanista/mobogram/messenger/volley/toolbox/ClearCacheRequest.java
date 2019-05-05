package com.hanista.mobogram.messenger.volley.toolbox;

import android.os.Handler;
import android.os.Looper;
import com.hanista.mobogram.messenger.volley.Cache;
import com.hanista.mobogram.messenger.volley.NetworkResponse;
import com.hanista.mobogram.messenger.volley.Request;
import com.hanista.mobogram.messenger.volley.Request.Priority;
import com.hanista.mobogram.messenger.volley.Response;

public class ClearCacheRequest extends Request<Object> {
    private final Cache mCache;
    private final Runnable mCallback;

    public ClearCacheRequest(Cache cache, Runnable runnable) {
        super(0, null, null);
        this.mCache = cache;
        this.mCallback = runnable;
    }

    protected void deliverResponse(Object obj) {
    }

    public Priority getPriority() {
        return Priority.IMMEDIATE;
    }

    public boolean isCanceled() {
        this.mCache.clear();
        if (this.mCallback != null) {
            new Handler(Looper.getMainLooper()).postAtFrontOfQueue(this.mCallback);
        }
        return true;
    }

    protected Response<Object> parseNetworkResponse(NetworkResponse networkResponse) {
        return null;
    }
}
