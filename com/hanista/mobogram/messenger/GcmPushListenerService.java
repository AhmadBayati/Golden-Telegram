package com.hanista.mobogram.messenger;

import android.os.Bundle;
import com.google.android.gms.gcm.GcmListenerService;
import com.hanista.mobogram.tgnet.ConnectionsManager;
import org.json.JSONObject;

public class GcmPushListenerService extends GcmListenerService {
    public static final int NOTIFICATION_ID = 1;

    /* renamed from: com.hanista.mobogram.messenger.GcmPushListenerService.1 */
    class C04131 implements Runnable {
        final /* synthetic */ Bundle val$bundle;

        C04131(Bundle bundle) {
            this.val$bundle = bundle;
        }

        public void run() {
            ApplicationLoader.postInitApplication();
            try {
                if ("DC_UPDATE".equals(this.val$bundle.getString("loc_key"))) {
                    JSONObject jSONObject = new JSONObject(this.val$bundle.getString("custom"));
                    int i = jSONObject.getInt("dc");
                    String[] split = jSONObject.getString("addr").split(":");
                    if (split.length == 2) {
                        ConnectionsManager.getInstance().applyDatacenterAddress(i, split[0], Integer.parseInt(split[GcmPushListenerService.NOTIFICATION_ID]));
                    } else {
                        return;
                    }
                }
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
            ConnectionsManager.onInternalPushReceived();
            ConnectionsManager.getInstance().resumeNetworkMaybe();
        }
    }

    public void onMessageReceived(String str, Bundle bundle) {
        FileLog.m15d("tmessages", "GCM received bundle: " + bundle + " from: " + str);
        AndroidUtilities.runOnUIThread(new C04131(bundle));
    }
}
