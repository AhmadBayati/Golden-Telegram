package com.hanista.mobogram.messenger.volley.toolbox;

import com.hanista.mobogram.messenger.volley.Request;
import com.hanista.mobogram.messenger.volley.Response.ErrorListener;
import com.hanista.mobogram.messenger.volley.Response.Listener;
import com.hanista.mobogram.messenger.volley.VolleyError;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class RequestFuture<T> implements ErrorListener, Listener<T>, Future<T> {
    private VolleyError mException;
    private Request<?> mRequest;
    private T mResult;
    private boolean mResultReceived;

    private RequestFuture() {
        this.mResultReceived = false;
    }

    private synchronized T doGet(Long l) {
        T t;
        if (this.mException != null) {
            throw new ExecutionException(this.mException);
        } else if (this.mResultReceived) {
            t = this.mResult;
        } else {
            if (l == null) {
                wait(0);
            } else if (l.longValue() > 0) {
                wait(l.longValue());
            }
            if (this.mException != null) {
                throw new ExecutionException(this.mException);
            } else if (this.mResultReceived) {
                t = this.mResult;
            } else {
                throw new TimeoutException();
            }
        }
        return t;
    }

    public static <E> RequestFuture<E> newFuture() {
        return new RequestFuture();
    }

    public synchronized boolean cancel(boolean z) {
        boolean z2 = false;
        synchronized (this) {
            if (this.mRequest != null) {
                if (!isDone()) {
                    this.mRequest.cancel();
                    z2 = true;
                }
            }
        }
        return z2;
    }

    public T get() {
        try {
            return doGet(null);
        } catch (TimeoutException e) {
            throw new AssertionError(e);
        }
    }

    public T get(long j, TimeUnit timeUnit) {
        return doGet(Long.valueOf(TimeUnit.MILLISECONDS.convert(j, timeUnit)));
    }

    public boolean isCancelled() {
        return this.mRequest == null ? false : this.mRequest.isCanceled();
    }

    public synchronized boolean isDone() {
        boolean z;
        z = this.mResultReceived || this.mException != null || isCancelled();
        return z;
    }

    public synchronized void onErrorResponse(VolleyError volleyError) {
        this.mException = volleyError;
        notifyAll();
    }

    public synchronized void onResponse(T t) {
        this.mResultReceived = true;
        this.mResult = t;
        notifyAll();
    }

    public void setRequest(Request<?> request) {
        this.mRequest = request;
    }
}
