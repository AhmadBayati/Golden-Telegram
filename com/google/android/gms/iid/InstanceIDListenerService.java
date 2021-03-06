package com.google.android.gms.iid;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

public class InstanceIDListenerService extends Service {
    static String ACTION;
    private static String aeK;
    private static String agb;
    private static String agc;
    MessengerCompat afZ;
    BroadcastReceiver aga;
    int agd;
    int age;

    /* renamed from: com.google.android.gms.iid.InstanceIDListenerService.1 */
    class C01241 extends Handler {
        final /* synthetic */ InstanceIDListenerService agf;

        C01241(InstanceIDListenerService instanceIDListenerService, Looper looper) {
            this.agf = instanceIDListenerService;
            super(looper);
        }

        public void handleMessage(Message message) {
            this.agf.zza(message, MessengerCompat.zzc(message));
        }
    }

    /* renamed from: com.google.android.gms.iid.InstanceIDListenerService.2 */
    class C01252 extends BroadcastReceiver {
        final /* synthetic */ InstanceIDListenerService agf;

        C01252(InstanceIDListenerService instanceIDListenerService) {
            this.agf = instanceIDListenerService;
        }

        public void onReceive(Context context, Intent intent) {
            if (Log.isLoggable("InstanceID", 3)) {
                intent.getStringExtra("registration_id");
                String valueOf = String.valueOf(intent.getExtras());
                Log.d("InstanceID", new StringBuilder(String.valueOf(valueOf).length() + 46).append("Received GSF callback using dynamic receiver: ").append(valueOf).toString());
            }
            this.agf.zzn(intent);
            this.agf.stop();
        }
    }

    static {
        ACTION = "action";
        agb = "google.com/iid";
        agc = "CMD";
        aeK = "gcm.googleapis.com/refresh";
    }

    public InstanceIDListenerService() {
        this.afZ = new MessengerCompat(new C01241(this, Looper.getMainLooper()));
        this.aga = new C01252(this);
    }

    static void zza(Context context, zzd com_google_android_gms_iid_zzd) {
        com_google_android_gms_iid_zzd.zzbow();
        Intent intent = new Intent("com.google.android.gms.iid.InstanceID");
        intent.putExtra(agc, "RST");
        intent.setPackage(context.getPackageName());
        context.startService(intent);
    }

    private void zza(Message message, int i) {
        zzc.zzdj(this);
        getPackageManager();
        if (i == zzc.agl || i == zzc.agk) {
            zzn((Intent) message.obj);
            return;
        }
        int i2 = zzc.agk;
        Log.w("InstanceID", "Message from unexpected caller " + i + " mine=" + i2 + " appid=" + zzc.agl);
    }

    static void zzdi(Context context) {
        Intent intent = new Intent("com.google.android.gms.iid.InstanceID");
        intent.setPackage(context.getPackageName());
        intent.putExtra(agc, "SYNC");
        context.startService(intent);
    }

    public IBinder onBind(Intent intent) {
        return (intent == null || !"com.google.android.gms.iid.InstanceID".equals(intent.getAction())) ? null : this.afZ.getBinder();
    }

    public void onCreate() {
        IntentFilter intentFilter = new IntentFilter("com.google.android.c2dm.intent.REGISTRATION");
        intentFilter.addCategory(getPackageName());
        registerReceiver(this.aga, intentFilter, "com.google.android.c2dm.permission.RECEIVE", null);
    }

    public void onDestroy() {
        unregisterReceiver(this.aga);
    }

    public int onStartCommand(Intent intent, int i, int i2) {
        zztt(i2);
        if (intent == null) {
            stop();
            return 2;
        }
        try {
            if ("com.google.android.gms.iid.InstanceID".equals(intent.getAction())) {
                if (VERSION.SDK_INT <= 18) {
                    Intent intent2 = (Intent) intent.getParcelableExtra("GSF");
                    if (intent2 != null) {
                        startService(intent2);
                        return 1;
                    }
                }
                zzn(intent);
            }
            stop();
            if (intent.getStringExtra("from") != null) {
                WakefulBroadcastReceiver.completeWakefulIntent(intent);
            }
            return 2;
        } finally {
            stop();
        }
    }

    public void onTokenRefresh() {
    }

    void stop() {
        synchronized (this) {
            this.agd--;
            if (this.agd == 0) {
                stopSelf(this.age);
            }
            if (Log.isLoggable("InstanceID", 3)) {
                int i = this.agd;
                Log.d("InstanceID", "Stop " + i + " " + this.age);
            }
        }
    }

    public void zzcb(boolean z) {
        onTokenRefresh();
    }

    public void zzn(Intent intent) {
        InstanceID instance;
        String stringExtra = intent.getStringExtra("subtype");
        if (stringExtra == null) {
            instance = InstanceID.getInstance(this);
        } else {
            Bundle bundle = new Bundle();
            bundle.putString("subtype", stringExtra);
            instance = InstanceID.zza(this, bundle);
        }
        String stringExtra2 = intent.getStringExtra(agc);
        if (intent.getStringExtra("error") == null && intent.getStringExtra("registration_id") == null) {
            if (Log.isLoggable("InstanceID", 3)) {
                String valueOf = String.valueOf(intent.getExtras());
                Log.d("InstanceID", new StringBuilder(((String.valueOf(stringExtra).length() + 18) + String.valueOf(stringExtra2).length()) + String.valueOf(valueOf).length()).append("Service command ").append(stringExtra).append(" ").append(stringExtra2).append(" ").append(valueOf).toString());
            }
            if (intent.getStringExtra("unregistered") != null) {
                zzd zzbor = instance.zzbor();
                if (stringExtra == null) {
                    stringExtra = TtmlNode.ANONYMOUS_REGION_ID;
                }
                zzbor.zzku(stringExtra);
                instance.zzbos().zzv(intent);
                return;
            } else if (aeK.equals(intent.getStringExtra("from"))) {
                instance.zzbor().zzku(stringExtra);
                zzcb(false);
                return;
            } else if ("RST".equals(stringExtra2)) {
                instance.zzboq();
                zzcb(true);
                return;
            } else if ("RST_FULL".equals(stringExtra2)) {
                if (!instance.zzbor().isEmpty()) {
                    instance.zzbor().zzbow();
                    zzcb(true);
                    return;
                }
                return;
            } else if ("SYNC".equals(stringExtra2)) {
                instance.zzbor().zzku(stringExtra);
                zzcb(false);
                return;
            } else if (!"PING".equals(stringExtra2)) {
                return;
            } else {
                return;
            }
        }
        if (Log.isLoggable("InstanceID", 3)) {
            stringExtra2 = "InstanceID";
            String str = "Register result in service ";
            stringExtra = String.valueOf(stringExtra);
            Log.d(stringExtra2, stringExtra.length() != 0 ? str.concat(stringExtra) : new String(str));
        }
        instance.zzbos().zzv(intent);
    }

    void zztt(int i) {
        synchronized (this) {
            this.agd++;
            if (i > this.age) {
                this.age = i;
            }
        }
    }
}
