package com.hanista.mobogram.messenger.exoplayer.upstream;

import com.hanista.mobogram.tgnet.ConnectionsManager;
import java.io.IOException;
import java.util.PriorityQueue;

public final class NetworkLock {
    public static final int DOWNLOAD_PRIORITY = 10;
    public static final int STREAMING_PRIORITY = 0;
    public static final NetworkLock instance;
    private int highestPriority;
    private final Object lock;
    private final PriorityQueue<Integer> queue;

    public static class PriorityTooLowException extends IOException {
        public PriorityTooLowException(int i, int i2) {
            super("Priority too low [priority=" + i + ", highest=" + i2 + "]");
        }
    }

    static {
        instance = new NetworkLock();
    }

    private NetworkLock() {
        this.lock = new Object();
        this.queue = new PriorityQueue();
        this.highestPriority = ConnectionsManager.DEFAULT_DATACENTER_ID;
    }

    public void add(int i) {
        synchronized (this.lock) {
            this.queue.add(Integer.valueOf(i));
            this.highestPriority = Math.min(this.highestPriority, i);
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void proceed(int r3) {
        /*
        r2 = this;
        r1 = r2.lock;
        monitor-enter(r1);
    L_0x0003:
        r0 = r2.highestPriority;	 Catch:{ all -> 0x000d }
        if (r0 >= r3) goto L_0x0010;
    L_0x0007:
        r0 = r2.lock;	 Catch:{ all -> 0x000d }
        r0.wait();	 Catch:{ all -> 0x000d }
        goto L_0x0003;
    L_0x000d:
        r0 = move-exception;
        monitor-exit(r1);	 Catch:{ all -> 0x000d }
        throw r0;
    L_0x0010:
        monitor-exit(r1);	 Catch:{ all -> 0x000d }
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.hanista.mobogram.messenger.exoplayer.upstream.NetworkLock.proceed(int):void");
    }

    public boolean proceedNonBlocking(int i) {
        boolean z;
        synchronized (this.lock) {
            z = this.highestPriority >= i;
        }
        return z;
    }

    public void proceedOrThrow(int i) {
        synchronized (this.lock) {
            if (this.highestPriority < i) {
                throw new PriorityTooLowException(i, this.highestPriority);
            }
        }
    }

    public void remove(int i) {
        synchronized (this.lock) {
            this.queue.remove(Integer.valueOf(i));
            this.highestPriority = this.queue.isEmpty() ? ConnectionsManager.DEFAULT_DATACENTER_ID : ((Integer) this.queue.peek()).intValue();
            this.lock.notifyAll();
        }
    }
}
