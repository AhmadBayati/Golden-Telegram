package com.hanista.mobogram.messenger;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import java.util.concurrent.CountDownLatch;

public class DispatchQueue extends Thread {
    private volatile Handler handler;
    private CountDownLatch syncLatch;

    public DispatchQueue(String str) {
        this.handler = null;
        this.syncLatch = new CountDownLatch(1);
        setName(str);
        start();
    }

    private void sendMessage(Message message, int i) {
        try {
            this.syncLatch.await();
            if (i <= 0) {
                this.handler.sendMessage(message);
            } else {
                this.handler.sendMessageDelayed(message, (long) i);
            }
        } catch (Throwable e) {
            FileLog.m18e("tmessages", e);
        }
    }

    public void cancelRunnable(Runnable runnable) {
        try {
            this.syncLatch.await();
            this.handler.removeCallbacks(runnable);
        } catch (Throwable e) {
            FileLog.m18e("tmessages", e);
        }
    }

    public void cleanupQueue() {
        try {
            this.syncLatch.await();
            this.handler.removeCallbacksAndMessages(null);
        } catch (Throwable e) {
            FileLog.m18e("tmessages", e);
        }
    }

    public void postRunnable(Runnable runnable) {
        postRunnable(runnable, 0);
    }

    public void postRunnable(Runnable runnable, long j) {
        try {
            this.syncLatch.await();
            if (j <= 0) {
                this.handler.post(runnable);
            } else {
                this.handler.postDelayed(runnable, j);
            }
        } catch (Throwable e) {
            FileLog.m18e("tmessages", e);
        }
    }

    public void run() {
        Looper.prepare();
        this.handler = new Handler();
        this.syncLatch.countDown();
        Looper.loop();
    }
}
