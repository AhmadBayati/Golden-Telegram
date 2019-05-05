package com.hanista.mobogram.messenger.exoplayer.util;

public final class SystemClock implements Clock {
    public long elapsedRealtime() {
        return android.os.SystemClock.elapsedRealtime();
    }
}
