package com.hanista.mobogram.messenger;

import android.app.IntentService;
import android.content.Intent;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

public class GcmRegistrationIntentService extends IntentService {

    /* renamed from: com.hanista.mobogram.messenger.GcmRegistrationIntentService.1 */
    class C04141 implements Runnable {
        final /* synthetic */ String val$token;

        C04141(String str) {
            this.val$token = str;
        }

        public void run() {
            ApplicationLoader.postInitApplication();
            GcmRegistrationIntentService.this.sendRegistrationToServer(this.val$token);
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.GcmRegistrationIntentService.2 */
    class C04152 implements Runnable {
        final /* synthetic */ int val$failCount;

        C04152(int i) {
            this.val$failCount = i;
        }

        public void run() {
            try {
                Intent intent = new Intent(ApplicationLoader.applicationContext, GcmRegistrationIntentService.class);
                intent.putExtra("failCount", this.val$failCount + 1);
                GcmRegistrationIntentService.this.startService(intent);
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.GcmRegistrationIntentService.3 */
    class C04173 implements Runnable {
        final /* synthetic */ String val$token;

        /* renamed from: com.hanista.mobogram.messenger.GcmRegistrationIntentService.3.1 */
        class C04161 implements Runnable {
            C04161() {
            }

            public void run() {
                MessagesController.getInstance().registerForPush(C04173.this.val$token);
            }
        }

        C04173(String str) {
            this.val$token = str;
        }

        public void run() {
            UserConfig.pushString = this.val$token;
            UserConfig.registeredForPush = false;
            UserConfig.saveConfig(false);
            if (UserConfig.getClientUserId() != 0) {
                AndroidUtilities.runOnUIThread(new C04161());
            }
        }
    }

    public GcmRegistrationIntentService() {
        super("GcmRegistrationIntentService");
    }

    private void sendRegistrationToServer(String str) {
        Utilities.stageQueue.postRunnable(new C04173(str));
    }

    protected void onHandleIntent(Intent intent) {
        try {
            String token = InstanceID.getInstance(this).getToken(BuildVars.GCM_SENDER_ID, GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            FileLog.m15d("tmessages", "GCM Registration Token: " + token);
            AndroidUtilities.runOnUIThread(new C04141(token));
        } catch (Throwable e) {
            FileLog.m18e("tmessages", e);
            int intExtra = intent.getIntExtra("failCount", 0);
            if (intExtra < 60) {
                AndroidUtilities.runOnUIThread(new C04152(intExtra), intExtra < 20 ? 10000 : 1800000);
            }
        }
    }
}
