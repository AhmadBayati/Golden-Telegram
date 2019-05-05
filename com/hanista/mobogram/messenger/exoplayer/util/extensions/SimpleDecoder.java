package com.hanista.mobogram.messenger.exoplayer.util.extensions;

import com.hanista.mobogram.messenger.exoplayer.util.Assertions;
import java.util.LinkedList;

public abstract class SimpleDecoder<I extends InputBuffer, O extends OutputBuffer, E extends Exception> extends Thread implements Decoder<I, O, E> {
    private int availableInputBufferCount;
    private final I[] availableInputBuffers;
    private int availableOutputBufferCount;
    private final O[] availableOutputBuffers;
    private I dequeuedInputBuffer;
    private E exception;
    private boolean flushed;
    private final Object lock;
    private final LinkedList<I> queuedInputBuffers;
    private final LinkedList<O> queuedOutputBuffers;
    private boolean released;

    public interface EventListener<E> {
        void onDecoderError(E e);
    }

    protected SimpleDecoder(I[] iArr, O[] oArr) {
        int i = 0;
        this.lock = new Object();
        this.queuedInputBuffers = new LinkedList();
        this.queuedOutputBuffers = new LinkedList();
        this.availableInputBuffers = iArr;
        this.availableInputBufferCount = iArr.length;
        for (int i2 = 0; i2 < this.availableInputBufferCount; i2++) {
            this.availableInputBuffers[i2] = createInputBuffer();
        }
        this.availableOutputBuffers = oArr;
        this.availableOutputBufferCount = oArr.length;
        while (i < this.availableOutputBufferCount) {
            this.availableOutputBuffers[i] = createOutputBuffer();
            i++;
        }
    }

    private boolean canDecodeBuffer() {
        return !this.queuedInputBuffers.isEmpty() && this.availableOutputBufferCount > 0;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean decode() {
        /*
        r8 = this;
        r7 = 2;
        r2 = 1;
        r1 = 0;
        r3 = r8.lock;
        monitor-enter(r3);
    L_0x0006:
        r0 = r8.released;	 Catch:{ all -> 0x0016 }
        if (r0 != 0) goto L_0x0019;
    L_0x000a:
        r0 = r8.canDecodeBuffer();	 Catch:{ all -> 0x0016 }
        if (r0 != 0) goto L_0x0019;
    L_0x0010:
        r0 = r8.lock;	 Catch:{ all -> 0x0016 }
        r0.wait();	 Catch:{ all -> 0x0016 }
        goto L_0x0006;
    L_0x0016:
        r0 = move-exception;
        monitor-exit(r3);	 Catch:{ all -> 0x0016 }
        throw r0;
    L_0x0019:
        r0 = r8.released;	 Catch:{ all -> 0x0016 }
        if (r0 == 0) goto L_0x0020;
    L_0x001d:
        monitor-exit(r3);	 Catch:{ all -> 0x0016 }
        r0 = r1;
    L_0x001f:
        return r0;
    L_0x0020:
        r0 = r8.queuedInputBuffers;	 Catch:{ all -> 0x0016 }
        r0 = r0.removeFirst();	 Catch:{ all -> 0x0016 }
        r0 = (com.hanista.mobogram.messenger.exoplayer.util.extensions.InputBuffer) r0;	 Catch:{ all -> 0x0016 }
        r4 = r8.availableOutputBuffers;	 Catch:{ all -> 0x0016 }
        r5 = r8.availableOutputBufferCount;	 Catch:{ all -> 0x0016 }
        r5 = r5 + -1;
        r8.availableOutputBufferCount = r5;	 Catch:{ all -> 0x0016 }
        r4 = r4[r5];	 Catch:{ all -> 0x0016 }
        r5 = r8.flushed;	 Catch:{ all -> 0x0016 }
        r6 = 0;
        r8.flushed = r6;	 Catch:{ all -> 0x0016 }
        monitor-exit(r3);	 Catch:{ all -> 0x0016 }
        r4.reset();
        r3 = r0.getFlag(r2);
        if (r3 == 0) goto L_0x0069;
    L_0x0041:
        r4.setFlag(r2);
    L_0x0044:
        r1 = r8.lock;
        monitor-enter(r1);
        r3 = r8.flushed;	 Catch:{ all -> 0x008b }
        if (r3 != 0) goto L_0x0052;
    L_0x004b:
        r3 = 2;
        r3 = r4.getFlag(r3);	 Catch:{ all -> 0x008b }
        if (r3 == 0) goto L_0x0085;
    L_0x0052:
        r3 = r8.availableOutputBuffers;	 Catch:{ all -> 0x008b }
        r5 = r8.availableOutputBufferCount;	 Catch:{ all -> 0x008b }
        r6 = r5 + 1;
        r8.availableOutputBufferCount = r6;	 Catch:{ all -> 0x008b }
        r3[r5] = r4;	 Catch:{ all -> 0x008b }
    L_0x005c:
        r3 = r8.availableInputBuffers;	 Catch:{ all -> 0x008b }
        r4 = r8.availableInputBufferCount;	 Catch:{ all -> 0x008b }
        r5 = r4 + 1;
        r8.availableInputBufferCount = r5;	 Catch:{ all -> 0x008b }
        r3[r4] = r0;	 Catch:{ all -> 0x008b }
        monitor-exit(r1);	 Catch:{ all -> 0x008b }
        r0 = r2;
        goto L_0x001f;
    L_0x0069:
        r3 = r0.getFlag(r7);
        if (r3 == 0) goto L_0x0072;
    L_0x006f:
        r4.setFlag(r7);
    L_0x0072:
        r3 = r8.decode(r0, r4, r5);
        r8.exception = r3;
        r3 = r8.exception;
        if (r3 == 0) goto L_0x0044;
    L_0x007c:
        r2 = r8.lock;
        monitor-enter(r2);
        monitor-exit(r2);	 Catch:{ all -> 0x0082 }
        r0 = r1;
        goto L_0x001f;
    L_0x0082:
        r0 = move-exception;
        monitor-exit(r2);	 Catch:{ all -> 0x0082 }
        throw r0;
    L_0x0085:
        r3 = r8.queuedOutputBuffers;	 Catch:{ all -> 0x008b }
        r3.addLast(r4);	 Catch:{ all -> 0x008b }
        goto L_0x005c;
    L_0x008b:
        r0 = move-exception;
        monitor-exit(r1);	 Catch:{ all -> 0x008b }
        throw r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.hanista.mobogram.messenger.exoplayer.util.extensions.SimpleDecoder.decode():boolean");
    }

    private void maybeNotifyDecodeLoop() {
        if (canDecodeBuffer()) {
            this.lock.notify();
        }
    }

    private void maybeThrowException() {
        if (this.exception != null) {
            throw this.exception;
        }
    }

    protected abstract I createInputBuffer();

    protected abstract O createOutputBuffer();

    protected abstract E decode(I i, O o, boolean z);

    public final I dequeueInputBuffer() {
        I i;
        synchronized (this.lock) {
            maybeThrowException();
            Assertions.checkState(this.dequeuedInputBuffer == null);
            if (this.availableInputBufferCount == 0) {
                i = null;
            } else {
                InputBuffer[] inputBufferArr = this.availableInputBuffers;
                int i2 = this.availableInputBufferCount - 1;
                this.availableInputBufferCount = i2;
                i = inputBufferArr[i2];
                i.reset();
                this.dequeuedInputBuffer = i;
            }
        }
        return i;
    }

    public final O dequeueOutputBuffer() {
        O o;
        synchronized (this.lock) {
            maybeThrowException();
            if (this.queuedOutputBuffers.isEmpty()) {
                o = null;
            } else {
                OutputBuffer outputBuffer = (OutputBuffer) this.queuedOutputBuffers.removeFirst();
            }
        }
        return o;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final void flush() {
        /*
        r4 = this;
        r1 = r4.lock;
        monitor-enter(r1);
        r0 = 1;
        r4.flushed = r0;	 Catch:{ all -> 0x0034 }
        r0 = r4.dequeuedInputBuffer;	 Catch:{ all -> 0x0034 }
        if (r0 == 0) goto L_0x0019;
    L_0x000a:
        r0 = r4.availableInputBuffers;	 Catch:{ all -> 0x0034 }
        r2 = r4.availableInputBufferCount;	 Catch:{ all -> 0x0034 }
        r3 = r2 + 1;
        r4.availableInputBufferCount = r3;	 Catch:{ all -> 0x0034 }
        r3 = r4.dequeuedInputBuffer;	 Catch:{ all -> 0x0034 }
        r0[r2] = r3;	 Catch:{ all -> 0x0034 }
        r0 = 0;
        r4.dequeuedInputBuffer = r0;	 Catch:{ all -> 0x0034 }
    L_0x0019:
        r0 = r4.queuedInputBuffers;	 Catch:{ all -> 0x0034 }
        r0 = r0.isEmpty();	 Catch:{ all -> 0x0034 }
        if (r0 != 0) goto L_0x0037;
    L_0x0021:
        r2 = r4.availableInputBuffers;	 Catch:{ all -> 0x0034 }
        r3 = r4.availableInputBufferCount;	 Catch:{ all -> 0x0034 }
        r0 = r3 + 1;
        r4.availableInputBufferCount = r0;	 Catch:{ all -> 0x0034 }
        r0 = r4.queuedInputBuffers;	 Catch:{ all -> 0x0034 }
        r0 = r0.removeFirst();	 Catch:{ all -> 0x0034 }
        r0 = (com.hanista.mobogram.messenger.exoplayer.util.extensions.InputBuffer) r0;	 Catch:{ all -> 0x0034 }
        r2[r3] = r0;	 Catch:{ all -> 0x0034 }
        goto L_0x0019;
    L_0x0034:
        r0 = move-exception;
        monitor-exit(r1);	 Catch:{ all -> 0x0034 }
        throw r0;
    L_0x0037:
        r0 = r4.queuedOutputBuffers;	 Catch:{ all -> 0x0034 }
        r0 = r0.isEmpty();	 Catch:{ all -> 0x0034 }
        if (r0 != 0) goto L_0x0052;
    L_0x003f:
        r2 = r4.availableOutputBuffers;	 Catch:{ all -> 0x0034 }
        r3 = r4.availableOutputBufferCount;	 Catch:{ all -> 0x0034 }
        r0 = r3 + 1;
        r4.availableOutputBufferCount = r0;	 Catch:{ all -> 0x0034 }
        r0 = r4.queuedOutputBuffers;	 Catch:{ all -> 0x0034 }
        r0 = r0.removeFirst();	 Catch:{ all -> 0x0034 }
        r0 = (com.hanista.mobogram.messenger.exoplayer.util.extensions.OutputBuffer) r0;	 Catch:{ all -> 0x0034 }
        r2[r3] = r0;	 Catch:{ all -> 0x0034 }
        goto L_0x0037;
    L_0x0052:
        monitor-exit(r1);	 Catch:{ all -> 0x0034 }
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.hanista.mobogram.messenger.exoplayer.util.extensions.SimpleDecoder.flush():void");
    }

    public final void queueInputBuffer(I i) {
        synchronized (this.lock) {
            maybeThrowException();
            Assertions.checkArgument(i == this.dequeuedInputBuffer);
            this.queuedInputBuffers.addLast(i);
            maybeNotifyDecodeLoop();
            this.dequeuedInputBuffer = null;
        }
    }

    public void release() {
        synchronized (this.lock) {
            this.released = true;
            this.lock.notify();
        }
        try {
            join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    protected void releaseOutputBuffer(O o) {
        synchronized (this.lock) {
            OutputBuffer[] outputBufferArr = this.availableOutputBuffers;
            int i = this.availableOutputBufferCount;
            this.availableOutputBufferCount = i + 1;
            outputBufferArr[i] = o;
            maybeNotifyDecodeLoop();
        }
    }

    public final void run() {
        do {
            try {
            } catch (Throwable e) {
                throw new IllegalStateException(e);
            }
        } while (decode());
    }

    protected final void setInitialInputBufferSize(int i) {
        int i2 = 0;
        Assertions.checkState(this.availableInputBufferCount == this.availableInputBuffers.length);
        while (i2 < this.availableInputBuffers.length) {
            this.availableInputBuffers[i2].sampleHolder.ensureSpaceForWrite(i);
            i2++;
        }
    }
}
