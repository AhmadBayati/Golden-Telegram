package com.hanista.mobogram.messenger.support.customtabs;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.text.TextUtils;
import com.hanista.mobogram.messenger.support.customtabs.ICustomTabsCallback.Stub;

public class CustomTabsClient {
    private final ICustomTabsService mService;
    private final ComponentName mServiceComponentName;

    /* renamed from: com.hanista.mobogram.messenger.support.customtabs.CustomTabsClient.1 */
    class C08381 extends Stub {
        final /* synthetic */ CustomTabsCallback val$callback;

        C08381(CustomTabsCallback customTabsCallback) {
            this.val$callback = customTabsCallback;
        }

        public void extraCallback(String str, Bundle bundle) {
            if (this.val$callback != null) {
                this.val$callback.extraCallback(str, bundle);
            }
        }

        public void onNavigationEvent(int i, Bundle bundle) {
            if (this.val$callback != null) {
                this.val$callback.onNavigationEvent(i, bundle);
            }
        }
    }

    CustomTabsClient(ICustomTabsService iCustomTabsService, ComponentName componentName) {
        this.mService = iCustomTabsService;
        this.mServiceComponentName = componentName;
    }

    public static boolean bindCustomTabsService(Context context, String str, CustomTabsServiceConnection customTabsServiceConnection) {
        Intent intent = new Intent(CustomTabsService.ACTION_CUSTOM_TABS_CONNECTION);
        if (!TextUtils.isEmpty(str)) {
            intent.setPackage(str);
        }
        return context.bindService(intent, customTabsServiceConnection, 33);
    }

    public Bundle extraCommand(String str, Bundle bundle) {
        try {
            return this.mService.extraCommand(str, bundle);
        } catch (RemoteException e) {
            return null;
        }
    }

    public CustomTabsSession newSession(CustomTabsCallback customTabsCallback) {
        ICustomTabsCallback c08381 = new C08381(customTabsCallback);
        try {
            return !this.mService.newSession(c08381) ? null : new CustomTabsSession(this.mService, c08381, this.mServiceComponentName);
        } catch (RemoteException e) {
            return null;
        }
    }

    public boolean warmup(long j) {
        try {
            return this.mService.warmup(j);
        } catch (RemoteException e) {
            return false;
        }
    }
}
