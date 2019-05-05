package com.hanista.mobogram.messenger.exoplayer.util;

import android.os.HandlerThread;
import android.os.Process;

public final class PriorityHandlerThread extends HandlerThread {
    private final int priority;

    public PriorityHandlerThread(String str, int i) {
        super(str);
        this.priority = i;
    }

    public void run() {
        Process.setThreadPriority(this.priority);
        super.run();
    }
}
