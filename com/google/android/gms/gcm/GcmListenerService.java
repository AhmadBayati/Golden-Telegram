package com.google.android.gms.gcm;

import android.annotation.TargetApi;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import com.hanista.mobogram.ui.Components.VideoPlayer;
import java.util.Iterator;

public abstract class GcmListenerService extends Service {
    private int aeC;
    private int aeD;
    private final Object zzakd;

    /* renamed from: com.google.android.gms.gcm.GcmListenerService.1 */
    class C01181 implements Runnable {
        final /* synthetic */ GcmListenerService aeE;
        final /* synthetic */ Intent val$intent;

        C01181(GcmListenerService gcmListenerService, Intent intent) {
            this.aeE = gcmListenerService;
            this.val$intent = intent;
        }

        public void run() {
            this.aeE.zzm(this.val$intent);
        }
    }

    /* renamed from: com.google.android.gms.gcm.GcmListenerService.2 */
    class C01192 extends AsyncTask<Void, Void, Void> {
        final /* synthetic */ GcmListenerService aeE;
        final /* synthetic */ Intent val$intent;

        C01192(GcmListenerService gcmListenerService, Intent intent) {
            this.aeE = gcmListenerService;
            this.val$intent = intent;
        }

        protected Void doInBackground(Void... voidArr) {
            this.aeE.zzm(this.val$intent);
            return null;
        }
    }

    public GcmListenerService() {
        this.zzakd = new Object();
        this.aeD = 0;
    }

    static void zzac(Bundle bundle) {
        Iterator it = bundle.keySet().iterator();
        while (it.hasNext()) {
            String str = (String) it.next();
            if (str != null && str.startsWith("google.c.")) {
                it.remove();
            }
        }
    }

    private void zzbnx() {
        synchronized (this.zzakd) {
            this.aeD--;
            if (this.aeD == 0) {
                zztk(this.aeC);
            }
        }
    }

    @TargetApi(11)
    private void zzl(Intent intent) {
        if (VERSION.SDK_INT >= 11) {
            AsyncTask.THREAD_POOL_EXECUTOR.execute(new C01181(this, intent));
        } else {
            new C01192(this, intent).execute(new Void[0]);
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void zzm(android.content.Intent r5) {
        /*
        r4 = this;
        r1 = r5.getAction();	 Catch:{ all -> 0x0040 }
        r0 = -1;
        r2 = r1.hashCode();	 Catch:{ all -> 0x0040 }
        switch(r2) {
            case 366519424: goto L_0x0031;
            default: goto L_0x000c;
        };	 Catch:{ all -> 0x0040 }
    L_0x000c:
        switch(r0) {
            case 0: goto L_0x003c;
            default: goto L_0x000f;
        };	 Catch:{ all -> 0x0040 }
    L_0x000f:
        r1 = "GcmListenerService";
        r2 = "Unknown intent action: ";
        r0 = r5.getAction();	 Catch:{ all -> 0x0040 }
        r0 = java.lang.String.valueOf(r0);	 Catch:{ all -> 0x0040 }
        r3 = r0.length();	 Catch:{ all -> 0x0040 }
        if (r3 == 0) goto L_0x0045;
    L_0x0023:
        r0 = r2.concat(r0);	 Catch:{ all -> 0x0040 }
    L_0x0027:
        android.util.Log.d(r1, r0);	 Catch:{ all -> 0x0040 }
    L_0x002a:
        r4.zzbnx();	 Catch:{ all -> 0x0040 }
        android.support.v4.content.WakefulBroadcastReceiver.completeWakefulIntent(r5);
        return;
    L_0x0031:
        r2 = "com.google.android.c2dm.intent.RECEIVE";
        r1 = r1.equals(r2);	 Catch:{ all -> 0x0040 }
        if (r1 == 0) goto L_0x000c;
    L_0x003a:
        r0 = 0;
        goto L_0x000c;
    L_0x003c:
        r4.zzn(r5);	 Catch:{ all -> 0x0040 }
        goto L_0x002a;
    L_0x0040:
        r0 = move-exception;
        android.support.v4.content.WakefulBroadcastReceiver.completeWakefulIntent(r5);
        throw r0;
    L_0x0045:
        r0 = new java.lang.String;	 Catch:{ all -> 0x0040 }
        r0.<init>(r2);	 Catch:{ all -> 0x0040 }
        goto L_0x0027;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.gcm.GcmListenerService.zzm(android.content.Intent):void");
    }

    private void zzn(Intent intent) {
        String stringExtra = intent.getStringExtra("message_type");
        if (stringExtra == null) {
            stringExtra = GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE;
        }
        Object obj = -1;
        switch (stringExtra.hashCode()) {
            case -2062414158:
                if (stringExtra.equals(GoogleCloudMessaging.MESSAGE_TYPE_DELETED)) {
                    obj = 1;
                    break;
                }
                break;
            case 102161:
                if (stringExtra.equals(GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE)) {
                    obj = null;
                    break;
                }
                break;
            case 814694033:
                if (stringExtra.equals(GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR)) {
                    obj = 3;
                    break;
                }
                break;
            case 814800675:
                if (stringExtra.equals(GoogleCloudMessaging.MESSAGE_TYPE_SEND_EVENT)) {
                    obj = 2;
                    break;
                }
                break;
        }
        switch (obj) {
            case VideoPlayer.TRACK_DEFAULT /*0*/:
                zzo(intent);
            case VideoPlayer.TYPE_AUDIO /*1*/:
                onDeletedMessages();
            case VideoPlayer.STATE_PREPARING /*2*/:
                onMessageSent(intent.getStringExtra("google.message_id"));
            case VideoPlayer.STATE_BUFFERING /*3*/:
                onSendError(zzp(intent), intent.getStringExtra("error"));
            default:
                String str = "GcmListenerService";
                String str2 = "Received message with unknown type: ";
                stringExtra = String.valueOf(stringExtra);
                Log.w(str, stringExtra.length() != 0 ? str2.concat(stringExtra) : new String(str2));
        }
    }

    private void zzo(Intent intent) {
        Bundle extras = intent.getExtras();
        extras.remove("message_type");
        extras.remove("android.support.content.wakelockid");
        if (zza.zzad(extras)) {
            if (zza.zzdd(this)) {
                zza.zzae(extras);
            } else {
                zza.zzdc(this).zzaf(extras);
                return;
            }
        }
        String string = extras.getString("from");
        extras.remove("from");
        zzac(extras);
        onMessageReceived(string, extras);
    }

    private String zzp(Intent intent) {
        String stringExtra = intent.getStringExtra("google.message_id");
        return stringExtra == null ? intent.getStringExtra("message_id") : stringExtra;
    }

    public final IBinder onBind(Intent intent) {
        return null;
    }

    public void onDeletedMessages() {
    }

    public void onMessageReceived(String str, Bundle bundle) {
    }

    public void onMessageSent(String str) {
    }

    public void onSendError(String str, String str2) {
    }

    public final int onStartCommand(Intent intent, int i, int i2) {
        synchronized (this.zzakd) {
            this.aeC = i2;
            this.aeD++;
        }
        if (intent == null) {
            zzbnx();
            return 2;
        }
        zzl(intent);
        return 3;
    }

    boolean zztk(int i) {
        return stopSelfResult(i);
    }
}
