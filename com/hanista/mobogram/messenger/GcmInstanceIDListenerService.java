package com.hanista.mobogram.messenger;

import android.content.Intent;
import com.google.android.gms.iid.InstanceIDListenerService;

public class GcmInstanceIDListenerService extends InstanceIDListenerService {

    /* renamed from: com.hanista.mobogram.messenger.GcmInstanceIDListenerService.1 */
    class C04121 implements Runnable {
        C04121() {
        }

        public void run() {
            ApplicationLoader.postInitApplication();
            GcmInstanceIDListenerService.this.startService(new Intent(ApplicationLoader.applicationContext, GcmRegistrationIntentService.class));
        }
    }

    public void onTokenRefresh() {
        AndroidUtilities.runOnUIThread(new C04121());
    }
}
