package com.googlecode.mp4parser.util;

import android.util.Log;

public class AndroidLogger extends Logger {
    private static final String TAG = "isoparser";
    String name;

    public AndroidLogger(String str) {
        this.name = str;
    }

    public void logDebug(String str) {
        Log.d(TAG, this.name + ":" + str);
    }

    public void logError(String str) {
        Log.e(TAG, this.name + ":" + str);
    }

    public void logWarn(String str) {
        Log.w(TAG, this.name + ":" + str);
    }
}
