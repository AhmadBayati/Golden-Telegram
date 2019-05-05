package com.hanista.mobogram.messenger.support.customtabsclient.shared;

import android.content.ComponentName;
import com.hanista.mobogram.messenger.support.customtabs.CustomTabsClient;
import com.hanista.mobogram.messenger.support.customtabs.CustomTabsServiceConnection;
import java.lang.ref.WeakReference;

public class ServiceConnection extends CustomTabsServiceConnection {
    private WeakReference<ServiceConnectionCallback> mConnectionCallback;

    public ServiceConnection(ServiceConnectionCallback serviceConnectionCallback) {
        this.mConnectionCallback = new WeakReference(serviceConnectionCallback);
    }

    public void onCustomTabsServiceConnected(ComponentName componentName, CustomTabsClient customTabsClient) {
        ServiceConnectionCallback serviceConnectionCallback = (ServiceConnectionCallback) this.mConnectionCallback.get();
        if (serviceConnectionCallback != null) {
            serviceConnectionCallback.onServiceConnected(customTabsClient);
        }
    }

    public void onServiceDisconnected(ComponentName componentName) {
        ServiceConnectionCallback serviceConnectionCallback = (ServiceConnectionCallback) this.mConnectionCallback.get();
        if (serviceConnectionCallback != null) {
            serviceConnectionCallback.onServiceDisconnected();
        }
    }
}
