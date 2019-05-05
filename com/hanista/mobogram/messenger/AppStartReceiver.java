package com.hanista.mobogram.messenger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AppStartReceiver extends BroadcastReceiver {

    /* renamed from: com.hanista.mobogram.messenger.AppStartReceiver.1 */
    class C03431 implements Runnable {
        C03431() {
        }

        public void run() {
            ApplicationLoader.startPushService();
        }
    }

    public void onReceive(Context context, Intent intent) {
        AndroidUtilities.runOnUIThread(new C03431());
    }
}
