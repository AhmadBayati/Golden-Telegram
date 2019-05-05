package com.google.android.gms.common.internal;

import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;

public final class zzm implements Callback {
    private final zza Cs;
    private final ArrayList<ConnectionCallbacks> Ct;
    final ArrayList<ConnectionCallbacks> Cu;
    private final ArrayList<OnConnectionFailedListener> Cv;
    private volatile boolean Cw;
    private final AtomicInteger Cx;
    private boolean Cy;
    private final Handler mHandler;
    private final Object zzakd;

    public interface zza {
        boolean isConnected();

        Bundle zzaoe();
    }

    public zzm(Looper looper, zza com_google_android_gms_common_internal_zzm_zza) {
        this.Ct = new ArrayList();
        this.Cu = new ArrayList();
        this.Cv = new ArrayList();
        this.Cw = false;
        this.Cx = new AtomicInteger(0);
        this.Cy = false;
        this.zzakd = new Object();
        this.Cs = com_google_android_gms_common_internal_zzm_zza;
        this.mHandler = new Handler(looper, this);
    }

    public boolean handleMessage(Message message) {
        if (message.what == 1) {
            ConnectionCallbacks connectionCallbacks = (ConnectionCallbacks) message.obj;
            synchronized (this.zzakd) {
                if (this.Cw && this.Cs.isConnected() && this.Ct.contains(connectionCallbacks)) {
                    connectionCallbacks.onConnected(this.Cs.zzaoe());
                }
            }
            return true;
        }
        Log.wtf("GmsClientEvents", "Don't know how to handle message: " + message.what, new Exception());
        return false;
    }

    public boolean isConnectionCallbacksRegistered(ConnectionCallbacks connectionCallbacks) {
        boolean contains;
        zzac.zzy(connectionCallbacks);
        synchronized (this.zzakd) {
            contains = this.Ct.contains(connectionCallbacks);
        }
        return contains;
    }

    public boolean isConnectionFailedListenerRegistered(OnConnectionFailedListener onConnectionFailedListener) {
        boolean contains;
        zzac.zzy(onConnectionFailedListener);
        synchronized (this.zzakd) {
            contains = this.Cv.contains(onConnectionFailedListener);
        }
        return contains;
    }

    public void registerConnectionCallbacks(ConnectionCallbacks connectionCallbacks) {
        zzac.zzy(connectionCallbacks);
        synchronized (this.zzakd) {
            if (this.Ct.contains(connectionCallbacks)) {
                String valueOf = String.valueOf(connectionCallbacks);
                Log.w("GmsClientEvents", new StringBuilder(String.valueOf(valueOf).length() + 62).append("registerConnectionCallbacks(): listener ").append(valueOf).append(" is already registered").toString());
            } else {
                this.Ct.add(connectionCallbacks);
            }
        }
        if (this.Cs.isConnected()) {
            this.mHandler.sendMessage(this.mHandler.obtainMessage(1, connectionCallbacks));
        }
    }

    public void registerConnectionFailedListener(OnConnectionFailedListener onConnectionFailedListener) {
        zzac.zzy(onConnectionFailedListener);
        synchronized (this.zzakd) {
            if (this.Cv.contains(onConnectionFailedListener)) {
                String valueOf = String.valueOf(onConnectionFailedListener);
                Log.w("GmsClientEvents", new StringBuilder(String.valueOf(valueOf).length() + 67).append("registerConnectionFailedListener(): listener ").append(valueOf).append(" is already registered").toString());
            } else {
                this.Cv.add(onConnectionFailedListener);
            }
        }
    }

    public void unregisterConnectionCallbacks(ConnectionCallbacks connectionCallbacks) {
        zzac.zzy(connectionCallbacks);
        synchronized (this.zzakd) {
            if (!this.Ct.remove(connectionCallbacks)) {
                String valueOf = String.valueOf(connectionCallbacks);
                Log.w("GmsClientEvents", new StringBuilder(String.valueOf(valueOf).length() + 52).append("unregisterConnectionCallbacks(): listener ").append(valueOf).append(" not found").toString());
            } else if (this.Cy) {
                this.Cu.add(connectionCallbacks);
            }
        }
    }

    public void unregisterConnectionFailedListener(OnConnectionFailedListener onConnectionFailedListener) {
        zzac.zzy(onConnectionFailedListener);
        synchronized (this.zzakd) {
            if (!this.Cv.remove(onConnectionFailedListener)) {
                String valueOf = String.valueOf(onConnectionFailedListener);
                Log.w("GmsClientEvents", new StringBuilder(String.valueOf(valueOf).length() + 57).append("unregisterConnectionFailedListener(): listener ").append(valueOf).append(" not found").toString());
            }
        }
    }

    public void zzaut() {
        this.Cw = false;
        this.Cx.incrementAndGet();
    }

    public void zzauu() {
        this.Cw = true;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void zzgo(int r6) {
        /*
        r5 = this;
        r0 = 0;
        r1 = 1;
        r2 = android.os.Looper.myLooper();
        r3 = r5.mHandler;
        r3 = r3.getLooper();
        if (r2 != r3) goto L_0x000f;
    L_0x000e:
        r0 = r1;
    L_0x000f:
        r2 = "onUnintentionalDisconnection must only be called on the Handler thread";
        com.google.android.gms.common.internal.zzac.zza(r0, r2);
        r0 = r5.mHandler;
        r0.removeMessages(r1);
        r1 = r5.zzakd;
        monitor-enter(r1);
        r0 = 1;
        r5.Cy = r0;	 Catch:{ all -> 0x005f }
        r0 = new java.util.ArrayList;	 Catch:{ all -> 0x005f }
        r2 = r5.Ct;	 Catch:{ all -> 0x005f }
        r0.<init>(r2);	 Catch:{ all -> 0x005f }
        r2 = r5.Cx;	 Catch:{ all -> 0x005f }
        r2 = r2.get();	 Catch:{ all -> 0x005f }
        r3 = r0.iterator();	 Catch:{ all -> 0x005f }
    L_0x0031:
        r0 = r3.hasNext();	 Catch:{ all -> 0x005f }
        if (r0 == 0) goto L_0x0049;
    L_0x0037:
        r0 = r3.next();	 Catch:{ all -> 0x005f }
        r0 = (com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks) r0;	 Catch:{ all -> 0x005f }
        r4 = r5.Cw;	 Catch:{ all -> 0x005f }
        if (r4 == 0) goto L_0x0049;
    L_0x0041:
        r4 = r5.Cx;	 Catch:{ all -> 0x005f }
        r4 = r4.get();	 Catch:{ all -> 0x005f }
        if (r4 == r2) goto L_0x0053;
    L_0x0049:
        r0 = r5.Cu;	 Catch:{ all -> 0x005f }
        r0.clear();	 Catch:{ all -> 0x005f }
        r0 = 0;
        r5.Cy = r0;	 Catch:{ all -> 0x005f }
        monitor-exit(r1);	 Catch:{ all -> 0x005f }
        return;
    L_0x0053:
        r4 = r5.Ct;	 Catch:{ all -> 0x005f }
        r4 = r4.contains(r0);	 Catch:{ all -> 0x005f }
        if (r4 == 0) goto L_0x0031;
    L_0x005b:
        r0.onConnectionSuspended(r6);	 Catch:{ all -> 0x005f }
        goto L_0x0031;
    L_0x005f:
        r0 = move-exception;
        monitor-exit(r1);	 Catch:{ all -> 0x005f }
        throw r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.common.internal.zzm.zzgo(int):void");
    }

    public void zzn(ConnectionResult connectionResult) {
        zzac.zza(Looper.myLooper() == this.mHandler.getLooper(), (Object) "onConnectionFailure must only be called on the Handler thread");
        this.mHandler.removeMessages(1);
        synchronized (this.zzakd) {
            ArrayList arrayList = new ArrayList(this.Cv);
            int i = this.Cx.get();
            Iterator it = arrayList.iterator();
            while (it.hasNext()) {
                OnConnectionFailedListener onConnectionFailedListener = (OnConnectionFailedListener) it.next();
                if (!this.Cw || this.Cx.get() != i) {
                    return;
                } else if (this.Cv.contains(onConnectionFailedListener)) {
                    onConnectionFailedListener.onConnectionFailed(connectionResult);
                }
            }
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void zzp(android.os.Bundle r6) {
        /*
        r5 = this;
        r2 = 0;
        r1 = 1;
        r0 = android.os.Looper.myLooper();
        r3 = r5.mHandler;
        r3 = r3.getLooper();
        if (r0 != r3) goto L_0x006f;
    L_0x000e:
        r0 = r1;
    L_0x000f:
        r3 = "onConnectionSuccess must only be called on the Handler thread";
        com.google.android.gms.common.internal.zzac.zza(r0, r3);
        r3 = r5.zzakd;
        monitor-enter(r3);
        r0 = r5.Cy;	 Catch:{ all -> 0x0081 }
        if (r0 != 0) goto L_0x0071;
    L_0x001c:
        r0 = r1;
    L_0x001d:
        com.google.android.gms.common.internal.zzac.zzbr(r0);	 Catch:{ all -> 0x0081 }
        r0 = r5.mHandler;	 Catch:{ all -> 0x0081 }
        r4 = 1;
        r0.removeMessages(r4);	 Catch:{ all -> 0x0081 }
        r0 = 1;
        r5.Cy = r0;	 Catch:{ all -> 0x0081 }
        r0 = r5.Cu;	 Catch:{ all -> 0x0081 }
        r0 = r0.size();	 Catch:{ all -> 0x0081 }
        if (r0 != 0) goto L_0x0073;
    L_0x0031:
        com.google.android.gms.common.internal.zzac.zzbr(r1);	 Catch:{ all -> 0x0081 }
        r0 = new java.util.ArrayList;	 Catch:{ all -> 0x0081 }
        r1 = r5.Ct;	 Catch:{ all -> 0x0081 }
        r0.<init>(r1);	 Catch:{ all -> 0x0081 }
        r1 = r5.Cx;	 Catch:{ all -> 0x0081 }
        r1 = r1.get();	 Catch:{ all -> 0x0081 }
        r2 = r0.iterator();	 Catch:{ all -> 0x0081 }
    L_0x0045:
        r0 = r2.hasNext();	 Catch:{ all -> 0x0081 }
        if (r0 == 0) goto L_0x0065;
    L_0x004b:
        r0 = r2.next();	 Catch:{ all -> 0x0081 }
        r0 = (com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks) r0;	 Catch:{ all -> 0x0081 }
        r4 = r5.Cw;	 Catch:{ all -> 0x0081 }
        if (r4 == 0) goto L_0x0065;
    L_0x0055:
        r4 = r5.Cs;	 Catch:{ all -> 0x0081 }
        r4 = r4.isConnected();	 Catch:{ all -> 0x0081 }
        if (r4 == 0) goto L_0x0065;
    L_0x005d:
        r4 = r5.Cx;	 Catch:{ all -> 0x0081 }
        r4 = r4.get();	 Catch:{ all -> 0x0081 }
        if (r4 == r1) goto L_0x0075;
    L_0x0065:
        r0 = r5.Cu;	 Catch:{ all -> 0x0081 }
        r0.clear();	 Catch:{ all -> 0x0081 }
        r0 = 0;
        r5.Cy = r0;	 Catch:{ all -> 0x0081 }
        monitor-exit(r3);	 Catch:{ all -> 0x0081 }
        return;
    L_0x006f:
        r0 = r2;
        goto L_0x000f;
    L_0x0071:
        r0 = r2;
        goto L_0x001d;
    L_0x0073:
        r1 = r2;
        goto L_0x0031;
    L_0x0075:
        r4 = r5.Cu;	 Catch:{ all -> 0x0081 }
        r4 = r4.contains(r0);	 Catch:{ all -> 0x0081 }
        if (r4 != 0) goto L_0x0045;
    L_0x007d:
        r0.onConnected(r6);	 Catch:{ all -> 0x0081 }
        goto L_0x0045;
    L_0x0081:
        r0 = move-exception;
        monitor-exit(r3);	 Catch:{ all -> 0x0081 }
        throw r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.common.internal.zzm.zzp(android.os.Bundle):void");
    }
}
