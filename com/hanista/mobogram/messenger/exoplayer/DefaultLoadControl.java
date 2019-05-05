package com.hanista.mobogram.messenger.exoplayer;

import android.os.Handler;
import com.hanista.mobogram.messenger.exoplayer.upstream.Allocator;
import com.hanista.mobogram.messenger.exoplayer.upstream.NetworkLock;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class DefaultLoadControl implements LoadControl {
    private static final int ABOVE_HIGH_WATERMARK = 0;
    private static final int BELOW_LOW_WATERMARK = 2;
    private static final int BETWEEN_WATERMARKS = 1;
    public static final float DEFAULT_HIGH_BUFFER_LOAD = 0.8f;
    public static final int DEFAULT_HIGH_WATERMARK_MS = 30000;
    public static final float DEFAULT_LOW_BUFFER_LOAD = 0.2f;
    public static final int DEFAULT_LOW_WATERMARK_MS = 15000;
    private final Allocator allocator;
    private int bufferState;
    private final Handler eventHandler;
    private final EventListener eventListener;
    private boolean fillingBuffers;
    private final float highBufferLoad;
    private final long highWatermarkUs;
    private final HashMap<Object, LoaderState> loaderStates;
    private final List<Object> loaders;
    private final float lowBufferLoad;
    private final long lowWatermarkUs;
    private long maxLoadStartPositionUs;
    private boolean streamingPrioritySet;
    private int targetBufferSize;

    /* renamed from: com.hanista.mobogram.messenger.exoplayer.DefaultLoadControl.1 */
    class C07011 implements Runnable {
        final /* synthetic */ boolean val$loading;

        C07011(boolean z) {
            this.val$loading = z;
        }

        public void run() {
            DefaultLoadControl.this.eventListener.onLoadingChanged(this.val$loading);
        }
    }

    public interface EventListener {
        void onLoadingChanged(boolean z);
    }

    private static class LoaderState {
        public final int bufferSizeContribution;
        public int bufferState;
        public boolean loading;
        public long nextLoadPositionUs;

        public LoaderState(int i) {
            this.bufferSizeContribution = i;
            this.bufferState = DefaultLoadControl.ABOVE_HIGH_WATERMARK;
            this.loading = false;
            this.nextLoadPositionUs = -1;
        }
    }

    public DefaultLoadControl(Allocator allocator) {
        this(allocator, null, null);
    }

    public DefaultLoadControl(Allocator allocator, Handler handler, EventListener eventListener) {
        this(allocator, handler, eventListener, DEFAULT_LOW_WATERMARK_MS, DEFAULT_HIGH_WATERMARK_MS, DEFAULT_LOW_BUFFER_LOAD, DEFAULT_HIGH_BUFFER_LOAD);
    }

    public DefaultLoadControl(Allocator allocator, Handler handler, EventListener eventListener, int i, int i2, float f, float f2) {
        this.allocator = allocator;
        this.eventHandler = handler;
        this.eventListener = eventListener;
        this.loaders = new ArrayList();
        this.loaderStates = new HashMap();
        this.lowWatermarkUs = ((long) i) * 1000;
        this.highWatermarkUs = ((long) i2) * 1000;
        this.lowBufferLoad = f;
        this.highBufferLoad = f2;
    }

    private int getBufferState(int i) {
        float f = ((float) i) / ((float) this.targetBufferSize);
        return f > this.highBufferLoad ? ABOVE_HIGH_WATERMARK : f < this.lowBufferLoad ? BELOW_LOW_WATERMARK : BETWEEN_WATERMARKS;
    }

    private int getLoaderBufferState(long j, long j2) {
        if (j2 == -1) {
            return ABOVE_HIGH_WATERMARK;
        }
        long j3 = j2 - j;
        return j3 <= this.highWatermarkUs ? j3 < this.lowWatermarkUs ? BELOW_LOW_WATERMARK : BETWEEN_WATERMARKS : ABOVE_HIGH_WATERMARK;
    }

    private void notifyLoadingChanged(boolean z) {
        if (this.eventHandler != null && this.eventListener != null) {
            this.eventHandler.post(new C07011(z));
        }
    }

    private void updateControlState() {
        int i = ABOVE_HIGH_WATERMARK;
        int i2 = this.bufferState;
        int i3 = ABOVE_HIGH_WATERMARK;
        int i4 = ABOVE_HIGH_WATERMARK;
        for (int i5 = ABOVE_HIGH_WATERMARK; i5 < this.loaders.size(); i5 += BETWEEN_WATERMARKS) {
            LoaderState loaderState = (LoaderState) this.loaderStates.get(this.loaders.get(i5));
            i4 |= loaderState.loading;
            i3 |= loaderState.nextLoadPositionUs != -1 ? BETWEEN_WATERMARKS : ABOVE_HIGH_WATERMARK;
            i2 = Math.max(i2, loaderState.bufferState);
        }
        boolean z = !this.loaders.isEmpty() && (!(i4 == 0 && i3 == 0) && (i2 == BELOW_LOW_WATERMARK || (i2 == BETWEEN_WATERMARKS && this.fillingBuffers)));
        this.fillingBuffers = z;
        if (this.fillingBuffers && !this.streamingPrioritySet) {
            NetworkLock.instance.add(ABOVE_HIGH_WATERMARK);
            this.streamingPrioritySet = true;
            notifyLoadingChanged(true);
        } else if (!this.fillingBuffers && this.streamingPrioritySet && i4 == 0) {
            NetworkLock.instance.remove(ABOVE_HIGH_WATERMARK);
            this.streamingPrioritySet = false;
            notifyLoadingChanged(false);
        }
        this.maxLoadStartPositionUs = -1;
        if (this.fillingBuffers) {
            while (i < this.loaders.size()) {
                long j = ((LoaderState) this.loaderStates.get(this.loaders.get(i))).nextLoadPositionUs;
                if (j != -1 && (this.maxLoadStartPositionUs == -1 || j < this.maxLoadStartPositionUs)) {
                    this.maxLoadStartPositionUs = j;
                }
                i += BETWEEN_WATERMARKS;
            }
        }
    }

    public Allocator getAllocator() {
        return this.allocator;
    }

    public void register(Object obj, int i) {
        this.loaders.add(obj);
        this.loaderStates.put(obj, new LoaderState(i));
        this.targetBufferSize += i;
    }

    public void trimAllocator() {
        this.allocator.trim(this.targetBufferSize);
    }

    public void unregister(Object obj) {
        this.loaders.remove(obj);
        this.targetBufferSize -= ((LoaderState) this.loaderStates.remove(obj)).bufferSizeContribution;
        updateControlState();
    }

    public boolean update(Object obj, long j, long j2, boolean z) {
        int loaderBufferState = getLoaderBufferState(j, j2);
        LoaderState loaderState = (LoaderState) this.loaderStates.get(obj);
        Object obj2 = (loaderState.bufferState == loaderBufferState && loaderState.nextLoadPositionUs == j2 && loaderState.loading == z) ? ABOVE_HIGH_WATERMARK : BETWEEN_WATERMARKS;
        if (obj2 != null) {
            loaderState.bufferState = loaderBufferState;
            loaderState.nextLoadPositionUs = j2;
            loaderState.loading = z;
        }
        loaderBufferState = this.allocator.getTotalBytesAllocated();
        int bufferState = getBufferState(loaderBufferState);
        Object obj3 = this.bufferState != bufferState ? BETWEEN_WATERMARKS : ABOVE_HIGH_WATERMARK;
        if (obj3 != null) {
            this.bufferState = bufferState;
        }
        if (!(obj2 == null && obj3 == null)) {
            updateControlState();
        }
        return loaderBufferState < this.targetBufferSize && j2 != -1 && j2 <= this.maxLoadStartPositionUs;
    }
}
