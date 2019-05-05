package com.google.android.gms.common.stats;

import android.support.v4.util.SimpleArrayMap;
import com.hanista.mobogram.messenger.exoplayer.hls.HlsChunkSource;

public class zze {
    private final long Ev;
    private final int Ew;
    private final SimpleArrayMap<String, Long> Ex;

    public zze() {
        this.Ev = HlsChunkSource.DEFAULT_PLAYLIST_BLACKLIST_MS;
        this.Ew = 10;
        this.Ex = new SimpleArrayMap(10);
    }

    public zze(int i, long j) {
        this.Ev = j;
        this.Ew = i;
        this.Ex = new SimpleArrayMap();
    }

    private void zze(long j, long j2) {
        for (int size = this.Ex.size() - 1; size >= 0; size--) {
            if (j2 - ((Long) this.Ex.valueAt(size)).longValue() > j) {
                this.Ex.removeAt(size);
            }
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.lang.Long zzif(java.lang.String r9) {
        /*
        r8 = this;
        r2 = android.os.SystemClock.elapsedRealtime();
        r0 = r8.Ev;
        monitor-enter(r8);
    L_0x0007:
        r4 = r8.Ex;	 Catch:{ all -> 0x0041 }
        r4 = r4.size();	 Catch:{ all -> 0x0041 }
        r5 = r8.Ew;	 Catch:{ all -> 0x0041 }
        if (r4 < r5) goto L_0x0044;
    L_0x0011:
        r8.zze(r0, r2);	 Catch:{ all -> 0x0041 }
        r4 = 2;
        r0 = r0 / r4;
        r4 = "ConnectionTracker";
        r5 = r8.Ew;	 Catch:{ all -> 0x0041 }
        r6 = new java.lang.StringBuilder;	 Catch:{ all -> 0x0041 }
        r7 = 94;
        r6.<init>(r7);	 Catch:{ all -> 0x0041 }
        r7 = "The max capacity ";
        r6 = r6.append(r7);	 Catch:{ all -> 0x0041 }
        r5 = r6.append(r5);	 Catch:{ all -> 0x0041 }
        r6 = " is not enough. Current durationThreshold is: ";
        r5 = r5.append(r6);	 Catch:{ all -> 0x0041 }
        r5 = r5.append(r0);	 Catch:{ all -> 0x0041 }
        r5 = r5.toString();	 Catch:{ all -> 0x0041 }
        android.util.Log.w(r4, r5);	 Catch:{ all -> 0x0041 }
        goto L_0x0007;
    L_0x0041:
        r0 = move-exception;
        monitor-exit(r8);	 Catch:{ all -> 0x0041 }
        throw r0;
    L_0x0044:
        r0 = r8.Ex;	 Catch:{ all -> 0x0041 }
        r1 = java.lang.Long.valueOf(r2);	 Catch:{ all -> 0x0041 }
        r0 = r0.put(r9, r1);	 Catch:{ all -> 0x0041 }
        r0 = (java.lang.Long) r0;	 Catch:{ all -> 0x0041 }
        monitor-exit(r8);	 Catch:{ all -> 0x0041 }
        return r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.common.stats.zze.zzif(java.lang.String):java.lang.Long");
    }

    public boolean zzig(String str) {
        boolean z;
        synchronized (this) {
            z = this.Ex.remove(str) != null;
        }
        return z;
    }
}