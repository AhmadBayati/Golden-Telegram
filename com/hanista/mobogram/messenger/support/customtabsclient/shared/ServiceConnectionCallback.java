package com.hanista.mobogram.messenger.support.customtabsclient.shared;

import com.hanista.mobogram.messenger.support.customtabs.CustomTabsClient;

public interface ServiceConnectionCallback {
    void onServiceConnected(CustomTabsClient customTabsClient);

    void onServiceDisconnected();
}
