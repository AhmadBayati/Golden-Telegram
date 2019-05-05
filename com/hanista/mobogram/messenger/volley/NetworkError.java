package com.hanista.mobogram.messenger.volley;

public class NetworkError extends VolleyError {
    public NetworkError(NetworkResponse networkResponse) {
        super(networkResponse);
    }

    public NetworkError(Throwable th) {
        super(th);
    }
}
