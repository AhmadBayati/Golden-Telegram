package com.google.android.gms.gcm;

import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import com.google.android.gms.common.internal.zzac;
import java.util.List;

public class GcmNetworkManager {
    public static final int RESULT_FAILURE = 2;
    public static final int RESULT_RESCHEDULE = 1;
    public static final int RESULT_SUCCESS = 0;
    private static GcmNetworkManager aeF;
    private Context mContext;
    private final PendingIntent mPendingIntent;

    private GcmNetworkManager(Context context) {
        this.mContext = context;
        this.mPendingIntent = PendingIntent.getBroadcast(this.mContext, 0, new Intent(), 0);
    }

    public static GcmNetworkManager getInstance(Context context) {
        GcmNetworkManager gcmNetworkManager;
        synchronized (GcmNetworkManager.class) {
            if (aeF == null) {
                aeF = new GcmNetworkManager(context.getApplicationContext());
            }
            gcmNetworkManager = aeF;
        }
        return gcmNetworkManager;
    }

    private void zza(ComponentName componentName) {
        zzkj(componentName.getClassName());
        Intent zzbny = zzbny();
        if (zzbny != null) {
            zzbny.putExtra("scheduler_action", "CANCEL_ALL");
            zzbny.putExtra("component", componentName);
            this.mContext.sendBroadcast(zzbny);
        }
    }

    private void zza(String str, ComponentName componentName) {
        zzki(str);
        zzkj(componentName.getClassName());
        Intent zzbny = zzbny();
        if (zzbny != null) {
            zzbny.putExtra("scheduler_action", "CANCEL_TASK");
            zzbny.putExtra("tag", str);
            zzbny.putExtra("component", componentName);
            this.mContext.sendBroadcast(zzbny);
        }
    }

    private Intent zzbny() {
        String zzde = GoogleCloudMessaging.zzde(this.mContext);
        int i = -1;
        if (zzde != null) {
            i = GoogleCloudMessaging.zzdf(this.mContext);
        }
        if (zzde == null || i < GoogleCloudMessaging.aeP) {
            Log.e("GcmNetworkManager", "Google Play Services is not available, dropping GcmNetworkManager request. code=" + i);
            return null;
        }
        Intent intent = new Intent("com.google.android.gms.gcm.ACTION_SCHEDULE");
        intent.setPackage(zzde);
        intent.putExtra("app", this.mPendingIntent);
        return intent;
    }

    static void zzki(String str) {
        if (TextUtils.isEmpty(str)) {
            throw new IllegalArgumentException("Must provide a valid tag.");
        } else if (100 < str.length()) {
            throw new IllegalArgumentException("Tag is larger than max permissible tag length (100)");
        }
    }

    private void zzkj(String str) {
        boolean z = true;
        zzac.zzb((Object) str, (Object) "GcmTaskService must not be null.");
        Intent intent = new Intent(GcmTaskService.SERVICE_ACTION_EXECUTE_TASK);
        intent.setPackage(this.mContext.getPackageName());
        List<ResolveInfo> queryIntentServices = this.mContext.getPackageManager().queryIntentServices(intent, 0);
        boolean z2 = (queryIntentServices == null || queryIntentServices.size() == 0) ? false : true;
        zzac.zzb(z2, (Object) "There is no GcmTaskService component registered within this package. Have you extended GcmTaskService correctly?");
        for (ResolveInfo resolveInfo : queryIntentServices) {
            if (resolveInfo.serviceInfo.name.equals(str)) {
                break;
            }
        }
        z = false;
        zzac.zzb(z, new StringBuilder(String.valueOf(str).length() + 119).append("The GcmTaskService class you provided ").append(str).append(" does not seem to support receiving com.google.android.gms.gcm.ACTION_TASK_READY.").toString());
    }

    public void cancelAllTasks(Class<? extends GcmTaskService> cls) {
        zze(cls);
    }

    public void cancelTask(String str, Class<? extends GcmTaskService> cls) {
        zzb(str, cls);
    }

    public void schedule(Task task) {
        zzkj(task.getServiceName());
        Intent zzbny = zzbny();
        if (zzbny != null) {
            Bundle extras = zzbny.getExtras();
            extras.putString("scheduler_action", "SCHEDULE_TASK");
            task.toBundle(extras);
            zzbny.putExtras(extras);
            this.mContext.sendBroadcast(zzbny);
        }
    }

    public void zzb(String str, Class<? extends Service> cls) {
        zza(str, new ComponentName(this.mContext, cls));
    }

    public void zze(Class<? extends Service> cls) {
        zza(new ComponentName(this.mContext, cls));
    }
}
