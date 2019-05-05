package com.hanista.mobogram.messenger.volley;

public interface Network {
    NetworkResponse performRequest(Request<?> request);
}
