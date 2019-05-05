package com.hanista.mobogram.messenger.volley;

public class ServerError extends VolleyError {
    public ServerError(NetworkResponse networkResponse) {
        super(networkResponse);
    }
}
