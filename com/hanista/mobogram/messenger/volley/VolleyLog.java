package com.hanista.mobogram.messenger.volley;

import android.os.SystemClock;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class VolleyLog {
    public static boolean DEBUG;
    public static String TAG;

    static class MarkerLog {
        public static final boolean ENABLED;
        private static final long MIN_DURATION_FOR_LOGGING_MS = 0;
        private boolean mFinished;
        private final List<Marker> mMarkers;

        private static class Marker {
            public final String name;
            public final long thread;
            public final long time;

            public Marker(String str, long j, long j2) {
                this.name = str;
                this.thread = j;
                this.time = j2;
            }
        }

        static {
            ENABLED = VolleyLog.DEBUG;
        }

        MarkerLog() {
            this.mMarkers = new ArrayList();
            this.mFinished = false;
        }

        private long getTotalDuration() {
            if (this.mMarkers.size() == 0) {
                return 0;
            }
            return ((Marker) this.mMarkers.get(this.mMarkers.size() - 1)).time - ((Marker) this.mMarkers.get(0)).time;
        }

        public synchronized void add(String str, long j) {
            if (this.mFinished) {
                throw new IllegalStateException("Marker added to finished log");
            }
            this.mMarkers.add(new Marker(str, j, SystemClock.elapsedRealtime()));
        }

        protected void finalize() {
            if (!this.mFinished) {
                finish("Request on the loose");
                VolleyLog.m200e("Marker log finalized without finish() - uncaught exit point for request", new Object[0]);
            }
        }

        public synchronized void finish(String str) {
            this.mFinished = true;
            if (getTotalDuration() > 0) {
                long j = ((Marker) this.mMarkers.get(0)).time;
                VolleyLog.m199d("(%-4d ms) %s", Long.valueOf(r2), str);
                long j2 = j;
                for (Marker marker : this.mMarkers) {
                    VolleyLog.m199d("(+%-4d) [%2d] %s", Long.valueOf(marker.time - j2), Long.valueOf(marker.thread), marker.name);
                    j2 = marker.time;
                }
            }
        }
    }

    static {
        TAG = "Volley";
        DEBUG = Log.isLoggable(TAG, 2);
    }

    private static String buildMessage(String str, Object... objArr) {
        String str2;
        if (objArr != null) {
            str = String.format(Locale.US, str, objArr);
        }
        StackTraceElement[] stackTrace = new Throwable().fillInStackTrace().getStackTrace();
        String str3 = "<unknown>";
        for (int i = 2; i < stackTrace.length; i++) {
            if (!stackTrace[i].getClass().equals(VolleyLog.class)) {
                str3 = stackTrace[i].getClassName();
                str3 = str3.substring(str3.lastIndexOf(46) + 1);
                str2 = str3.substring(str3.lastIndexOf(36) + 1) + "." + stackTrace[i].getMethodName();
                break;
            }
        }
        str2 = str3;
        return String.format(Locale.US, "[%d] %s: %s", new Object[]{Long.valueOf(Thread.currentThread().getId()), str2, str});
    }

    public static void m199d(String str, Object... objArr) {
        Log.d(TAG, buildMessage(str, objArr));
    }

    public static void m200e(String str, Object... objArr) {
        Log.e(TAG, buildMessage(str, objArr));
    }

    public static void m201e(Throwable th, String str, Object... objArr) {
        Log.e(TAG, buildMessage(str, objArr), th);
    }

    public static void setTag(String str) {
        m199d("Changing log tag to %s", str);
        TAG = str;
        DEBUG = Log.isLoggable(TAG, 2);
    }

    public static void m202v(String str, Object... objArr) {
        if (DEBUG) {
            Log.v(TAG, buildMessage(str, objArr));
        }
    }

    public static void wtf(String str, Object... objArr) {
        Log.wtf(TAG, buildMessage(str, objArr));
    }

    public static void wtf(Throwable th, String str, Object... objArr) {
        Log.wtf(TAG, buildMessage(str, objArr), th);
    }
}
