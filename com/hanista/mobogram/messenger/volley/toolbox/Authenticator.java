package com.hanista.mobogram.messenger.volley.toolbox;

public interface Authenticator {
    String getAuthToken();

    void invalidateAuthToken(String str);
}
