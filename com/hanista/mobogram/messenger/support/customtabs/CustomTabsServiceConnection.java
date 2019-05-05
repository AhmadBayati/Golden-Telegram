package com.hanista.mobogram.messenger.support.customtabs;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import com.hanista.mobogram.messenger.support.customtabs.ICustomTabsService.Stub;

public abstract class CustomTabsServiceConnection implements ServiceConnection {

    /* renamed from: com.hanista.mobogram.messenger.support.customtabs.CustomTabsServiceConnection.1 */
    class C08421 extends CustomTabsClient {
        C08421(ICustomTabsService iCustomTabsService, ComponentName componentName) {
            super(iCustomTabsService, componentName);
        }
    }

    public abstract void onCustomTabsServiceConnected(ComponentName componentName, CustomTabsClient customTabsClient);

    public final void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        onCustomTabsServiceConnected(componentName, new C08421(Stub.asInterface(iBinder), componentName));
    }
}
