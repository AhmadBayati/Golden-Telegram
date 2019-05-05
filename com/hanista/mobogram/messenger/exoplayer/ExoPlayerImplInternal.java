package com.hanista.mobogram.messenger.exoplayer;

import android.os.Handler;
import android.os.Handler.Callback;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.util.Pair;
import com.hanista.mobogram.messenger.exoplayer.ExoPlayer.ExoPlayerComponent;
import com.hanista.mobogram.messenger.exoplayer.extractor.ts.PtsTimestampAdjuster;
import com.hanista.mobogram.messenger.exoplayer.util.Assertions;
import com.hanista.mobogram.messenger.exoplayer.util.PriorityHandlerThread;
import com.hanista.mobogram.messenger.exoplayer.util.TraceUtil;
import com.hanista.mobogram.messenger.exoplayer.util.Util;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

final class ExoPlayerImplInternal implements Callback {
    private static final int IDLE_INTERVAL_MS = 1000;
    private static final int MSG_CUSTOM = 9;
    private static final int MSG_DO_SOME_WORK = 7;
    public static final int MSG_ERROR = 4;
    private static final int MSG_INCREMENTAL_PREPARE = 2;
    private static final int MSG_PREPARE = 1;
    public static final int MSG_PREPARED = 1;
    private static final int MSG_RELEASE = 5;
    private static final int MSG_SEEK_TO = 6;
    private static final int MSG_SET_PLAY_WHEN_READY = 3;
    public static final int MSG_SET_PLAY_WHEN_READY_ACK = 3;
    private static final int MSG_SET_RENDERER_SELECTED_TRACK = 8;
    public static final int MSG_STATE_CHANGED = 2;
    private static final int MSG_STOP = 4;
    private static final int PREPARE_INTERVAL_MS = 10;
    private static final int RENDERING_INTERVAL_MS = 10;
    private static final String TAG = "ExoPlayerImplInternal";
    private volatile long bufferedPositionUs;
    private int customMessagesProcessed;
    private int customMessagesSent;
    private volatile long durationUs;
    private long elapsedRealtimeUs;
    private final List<TrackRenderer> enabledRenderers;
    private final Handler eventHandler;
    private final Handler handler;
    private final HandlerThread internalPlaybackThread;
    private long lastSeekPositionMs;
    private final long minBufferUs;
    private final long minRebufferUs;
    private final AtomicInteger pendingSeekCount;
    private boolean playWhenReady;
    private volatile long positionUs;
    private boolean rebuffering;
    private boolean released;
    private MediaClock rendererMediaClock;
    private TrackRenderer rendererMediaClockSource;
    private TrackRenderer[] renderers;
    private final int[] selectedTrackIndices;
    private final StandaloneMediaClock standaloneMediaClock;
    private int state;
    private final MediaFormat[][] trackFormats;

    public ExoPlayerImplInternal(Handler handler, boolean z, int[] iArr, int i, int i2) {
        this.customMessagesSent = 0;
        this.customMessagesProcessed = 0;
        this.eventHandler = handler;
        this.playWhenReady = z;
        this.minBufferUs = ((long) i) * 1000;
        this.minRebufferUs = ((long) i2) * 1000;
        this.selectedTrackIndices = Arrays.copyOf(iArr, iArr.length);
        this.state = MSG_PREPARED;
        this.durationUs = -1;
        this.bufferedPositionUs = -1;
        this.standaloneMediaClock = new StandaloneMediaClock();
        this.pendingSeekCount = new AtomicInteger();
        this.enabledRenderers = new ArrayList(iArr.length);
        this.trackFormats = new MediaFormat[iArr.length][];
        this.internalPlaybackThread = new PriorityHandlerThread("ExoPlayerImplInternal:Handler", -16);
        this.internalPlaybackThread.start();
        this.handler = new Handler(this.internalPlaybackThread.getLooper(), this);
    }

    private void doSomeWork() {
        TraceUtil.beginSection("doSomeWork");
        long elapsedRealtime = SystemClock.elapsedRealtime();
        long j = this.durationUs != -1 ? this.durationUs : PtsTimestampAdjuster.DO_NOT_OFFSET;
        updatePositionUs();
        Object obj = MSG_PREPARED;
        Object obj2 = MSG_PREPARED;
        long j2 = j;
        for (int i = 0; i < this.enabledRenderers.size(); i += MSG_PREPARED) {
            TrackRenderer trackRenderer = (TrackRenderer) this.enabledRenderers.get(i);
            trackRenderer.doSomeWork(this.positionUs, this.elapsedRealtimeUs);
            obj2 = (obj2 == null || !trackRenderer.isEnded()) ? null : MSG_PREPARED;
            boolean rendererReadyOrEnded = rendererReadyOrEnded(trackRenderer);
            if (!rendererReadyOrEnded) {
                trackRenderer.maybeThrowError();
            }
            obj = (obj == null || !rendererReadyOrEnded) ? null : MSG_PREPARED;
            if (j2 != -1) {
                long durationUs = trackRenderer.getDurationUs();
                long bufferedPositionUs = trackRenderer.getBufferedPositionUs();
                if (bufferedPositionUs == -1) {
                    j2 = -1;
                } else if (bufferedPositionUs != -3 && (durationUs == -1 || durationUs == -2 || bufferedPositionUs < durationUs)) {
                    j2 = Math.min(j2, bufferedPositionUs);
                }
            }
        }
        this.bufferedPositionUs = j2;
        if (obj2 != null && (this.durationUs == -1 || this.durationUs <= this.positionUs)) {
            setState(MSG_RELEASE);
            stopRenderers();
        } else if (this.state == MSG_SET_PLAY_WHEN_READY_ACK && obj != null) {
            setState(MSG_STOP);
            if (this.playWhenReady) {
                startRenderers();
            }
        } else if (this.state == MSG_STOP && obj == null) {
            this.rebuffering = this.playWhenReady;
            setState(MSG_SET_PLAY_WHEN_READY_ACK);
            stopRenderers();
        }
        this.handler.removeMessages(MSG_DO_SOME_WORK);
        if ((this.playWhenReady && this.state == MSG_STOP) || this.state == MSG_SET_PLAY_WHEN_READY_ACK) {
            scheduleNextOperation(MSG_DO_SOME_WORK, elapsedRealtime, 10);
        } else if (!this.enabledRenderers.isEmpty()) {
            scheduleNextOperation(MSG_DO_SOME_WORK, elapsedRealtime, 1000);
        }
        TraceUtil.endSection();
    }

    private void enableRenderer(TrackRenderer trackRenderer, int i, boolean z) {
        trackRenderer.enable(i, this.positionUs, z);
        this.enabledRenderers.add(trackRenderer);
        MediaClock mediaClock = trackRenderer.getMediaClock();
        if (mediaClock != null) {
            Assertions.checkState(this.rendererMediaClock == null);
            this.rendererMediaClock = mediaClock;
            this.rendererMediaClockSource = trackRenderer;
        }
    }

    private void ensureDisabled(TrackRenderer trackRenderer) {
        ensureStopped(trackRenderer);
        if (trackRenderer.getState() == MSG_STATE_CHANGED) {
            trackRenderer.disable();
            if (trackRenderer == this.rendererMediaClockSource) {
                this.rendererMediaClock = null;
                this.rendererMediaClockSource = null;
            }
        }
    }

    private void ensureStopped(TrackRenderer trackRenderer) {
        if (trackRenderer.getState() == MSG_SET_PLAY_WHEN_READY_ACK) {
            trackRenderer.stop();
        }
    }

    private void incrementalPrepareInternal() {
        int i;
        long elapsedRealtime = SystemClock.elapsedRealtime();
        Object obj = MSG_PREPARED;
        for (i = 0; i < this.renderers.length; i += MSG_PREPARED) {
            TrackRenderer trackRenderer = this.renderers[i];
            if (trackRenderer.getState() == 0 && trackRenderer.prepare(this.positionUs) == 0) {
                trackRenderer.maybeThrowError();
                obj = null;
            }
        }
        if (obj == null) {
            scheduleNextOperation(MSG_STATE_CHANGED, elapsedRealtime, 10);
            return;
        }
        long j = 0;
        Object obj2 = MSG_PREPARED;
        obj = MSG_PREPARED;
        for (i = 0; i < this.renderers.length; i += MSG_PREPARED) {
            int i2;
            TrackRenderer trackRenderer2 = this.renderers[i];
            int trackCount = trackRenderer2.getTrackCount();
            MediaFormat[] mediaFormatArr = new MediaFormat[trackCount];
            for (i2 = 0; i2 < trackCount; i2 += MSG_PREPARED) {
                mediaFormatArr[i2] = trackRenderer2.getFormat(i2);
            }
            this.trackFormats[i] = mediaFormatArr;
            if (trackCount > 0) {
                if (j != -1) {
                    long durationUs = trackRenderer2.getDurationUs();
                    if (durationUs == -1) {
                        j = -1;
                    } else if (durationUs != -2) {
                        j = Math.max(j, durationUs);
                    }
                }
                i2 = this.selectedTrackIndices[i];
                if (i2 >= 0 && i2 < mediaFormatArr.length) {
                    enableRenderer(trackRenderer2, i2, false);
                    obj2 = (obj2 == null || !trackRenderer2.isEnded()) ? null : MSG_PREPARED;
                    obj = (obj == null || !rendererReadyOrEnded(trackRenderer2)) ? null : MSG_PREPARED;
                }
            }
        }
        this.durationUs = j;
        if (obj2 == null || (j != -1 && j > this.positionUs)) {
            this.state = obj != null ? MSG_STOP : MSG_SET_PLAY_WHEN_READY_ACK;
        } else {
            this.state = MSG_RELEASE;
        }
        this.eventHandler.obtainMessage(MSG_PREPARED, this.state, 0, this.trackFormats).sendToTarget();
        if (this.playWhenReady && this.state == MSG_STOP) {
            startRenderers();
        }
        this.handler.sendEmptyMessage(MSG_DO_SOME_WORK);
    }

    private void prepareInternal(TrackRenderer[] trackRendererArr) {
        resetInternal();
        this.renderers = trackRendererArr;
        Arrays.fill(this.trackFormats, null);
        setState(MSG_STATE_CHANGED);
        incrementalPrepareInternal();
    }

    private void release(TrackRenderer trackRenderer) {
        try {
            trackRenderer.release();
        } catch (Throwable e) {
            Log.e(TAG, "Release failed.", e);
        } catch (Throwable e2) {
            Log.e(TAG, "Release failed.", e2);
        }
    }

    private void releaseInternal() {
        resetInternal();
        setState(MSG_PREPARED);
        synchronized (this) {
            this.released = true;
            notifyAll();
        }
    }

    private boolean rendererReadyOrEnded(TrackRenderer trackRenderer) {
        boolean z = false;
        if (trackRenderer.isEnded()) {
            return true;
        }
        if (!trackRenderer.isReady()) {
            return false;
        }
        if (this.state == MSG_STOP) {
            return true;
        }
        long durationUs = trackRenderer.getDurationUs();
        long bufferedPositionUs = trackRenderer.getBufferedPositionUs();
        long j = this.rebuffering ? this.minRebufferUs : this.minBufferUs;
        if (j <= 0 || bufferedPositionUs == -1 || bufferedPositionUs == -3 || bufferedPositionUs >= j + this.positionUs || !(durationUs == -1 || durationUs == -2 || bufferedPositionUs < durationUs)) {
            z = true;
        }
        return z;
    }

    private void resetInternal() {
        int i = 0;
        this.handler.removeMessages(MSG_DO_SOME_WORK);
        this.handler.removeMessages(MSG_STATE_CHANGED);
        this.rebuffering = false;
        this.standaloneMediaClock.stop();
        if (this.renderers != null) {
            while (i < this.renderers.length) {
                TrackRenderer trackRenderer = this.renderers[i];
                stopAndDisable(trackRenderer);
                release(trackRenderer);
                i += MSG_PREPARED;
            }
            this.renderers = null;
            this.rendererMediaClock = null;
            this.rendererMediaClockSource = null;
            this.enabledRenderers.clear();
        }
    }

    private void scheduleNextOperation(int i, long j, long j2) {
        long elapsedRealtime = (j + j2) - SystemClock.elapsedRealtime();
        if (elapsedRealtime <= 0) {
            this.handler.sendEmptyMessage(i);
        } else {
            this.handler.sendEmptyMessageDelayed(i, elapsedRealtime);
        }
    }

    private void seekToInternal(long j) {
        try {
            if (j != this.positionUs / 1000) {
                this.rebuffering = false;
                this.positionUs = j * 1000;
                this.standaloneMediaClock.stop();
                this.standaloneMediaClock.setPositionUs(this.positionUs);
                if (this.state == MSG_PREPARED || this.state == MSG_STATE_CHANGED) {
                    this.pendingSeekCount.decrementAndGet();
                    return;
                }
                for (int i = 0; i < this.enabledRenderers.size(); i += MSG_PREPARED) {
                    TrackRenderer trackRenderer = (TrackRenderer) this.enabledRenderers.get(i);
                    ensureStopped(trackRenderer);
                    trackRenderer.seekTo(this.positionUs);
                }
                setState(MSG_SET_PLAY_WHEN_READY_ACK);
                this.handler.sendEmptyMessage(MSG_DO_SOME_WORK);
                this.pendingSeekCount.decrementAndGet();
            }
        } finally {
            this.pendingSeekCount.decrementAndGet();
        }
    }

    private <T> void sendMessageInternal(int i, Object obj) {
        try {
            Pair pair = (Pair) obj;
            ((ExoPlayerComponent) pair.first).handleMessage(i, pair.second);
            if (!(this.state == MSG_PREPARED || this.state == MSG_STATE_CHANGED)) {
                this.handler.sendEmptyMessage(MSG_DO_SOME_WORK);
            }
            synchronized (this) {
                this.customMessagesProcessed += MSG_PREPARED;
                notifyAll();
            }
        } catch (Throwable th) {
            synchronized (this) {
            }
            this.customMessagesProcessed += MSG_PREPARED;
            notifyAll();
        }
    }

    private void setPlayWhenReadyInternal(boolean z) {
        try {
            this.rebuffering = false;
            this.playWhenReady = z;
            if (!z) {
                stopRenderers();
                updatePositionUs();
            } else if (this.state == MSG_STOP) {
                startRenderers();
                this.handler.sendEmptyMessage(MSG_DO_SOME_WORK);
            } else if (this.state == MSG_SET_PLAY_WHEN_READY_ACK) {
                this.handler.sendEmptyMessage(MSG_DO_SOME_WORK);
            }
            this.eventHandler.obtainMessage(MSG_SET_PLAY_WHEN_READY_ACK).sendToTarget();
        } catch (Throwable th) {
            this.eventHandler.obtainMessage(MSG_SET_PLAY_WHEN_READY_ACK).sendToTarget();
        }
    }

    private void setRendererSelectedTrackInternal(int i, int i2) {
        boolean z = true;
        if (this.selectedTrackIndices[i] != i2) {
            this.selectedTrackIndices[i] = i2;
            if (this.state != MSG_PREPARED && this.state != MSG_STATE_CHANGED) {
                TrackRenderer trackRenderer = this.renderers[i];
                int state = trackRenderer.getState();
                if (state != 0 && state != -1 && trackRenderer.getTrackCount() != 0) {
                    boolean z2 = state == MSG_STATE_CHANGED || state == MSG_SET_PLAY_WHEN_READY_ACK;
                    boolean z3 = i2 >= 0 && i2 < this.trackFormats[i].length;
                    if (z2) {
                        if (!z3 && trackRenderer == this.rendererMediaClockSource) {
                            this.standaloneMediaClock.setPositionUs(this.rendererMediaClock.getPositionUs());
                        }
                        ensureDisabled(trackRenderer);
                        this.enabledRenderers.remove(trackRenderer);
                    }
                    if (z3) {
                        z3 = this.playWhenReady && this.state == MSG_STOP;
                        if (z2 || !z3) {
                            z = false;
                        }
                        enableRenderer(trackRenderer, i2, z);
                        if (z3) {
                            trackRenderer.start();
                        }
                        this.handler.sendEmptyMessage(MSG_DO_SOME_WORK);
                    }
                }
            }
        }
    }

    private void setState(int i) {
        if (this.state != i) {
            this.state = i;
            this.eventHandler.obtainMessage(MSG_STATE_CHANGED, i, 0).sendToTarget();
        }
    }

    private void startRenderers() {
        this.rebuffering = false;
        this.standaloneMediaClock.start();
        for (int i = 0; i < this.enabledRenderers.size(); i += MSG_PREPARED) {
            ((TrackRenderer) this.enabledRenderers.get(i)).start();
        }
    }

    private void stopAndDisable(TrackRenderer trackRenderer) {
        try {
            ensureDisabled(trackRenderer);
        } catch (Throwable e) {
            Log.e(TAG, "Stop failed.", e);
        } catch (Throwable e2) {
            Log.e(TAG, "Stop failed.", e2);
        }
    }

    private void stopInternal() {
        resetInternal();
        setState(MSG_PREPARED);
    }

    private void stopRenderers() {
        this.standaloneMediaClock.stop();
        for (int i = 0; i < this.enabledRenderers.size(); i += MSG_PREPARED) {
            ensureStopped((TrackRenderer) this.enabledRenderers.get(i));
        }
    }

    private void updatePositionUs() {
        if (this.rendererMediaClock == null || !this.enabledRenderers.contains(this.rendererMediaClockSource) || this.rendererMediaClockSource.isEnded()) {
            this.positionUs = this.standaloneMediaClock.getPositionUs();
        } else {
            this.positionUs = this.rendererMediaClock.getPositionUs();
            this.standaloneMediaClock.setPositionUs(this.positionUs);
        }
        this.elapsedRealtimeUs = SystemClock.elapsedRealtime() * 1000;
    }

    public synchronized void blockingSendMessage(ExoPlayerComponent exoPlayerComponent, int i, Object obj) {
        if (this.released) {
            Log.w(TAG, "Sent message(" + i + ") after release. Message ignored.");
        } else {
            int i2 = this.customMessagesSent;
            this.customMessagesSent = i2 + MSG_PREPARED;
            this.handler.obtainMessage(MSG_CUSTOM, i, 0, Pair.create(exoPlayerComponent, obj)).sendToTarget();
            while (this.customMessagesProcessed <= i2) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    public long getBufferedPosition() {
        return this.bufferedPositionUs == -1 ? -1 : this.bufferedPositionUs / 1000;
    }

    public long getCurrentPosition() {
        return this.pendingSeekCount.get() > 0 ? this.lastSeekPositionMs : this.positionUs / 1000;
    }

    public long getDuration() {
        return this.durationUs == -1 ? -1 : this.durationUs / 1000;
    }

    public Looper getPlaybackLooper() {
        return this.internalPlaybackThread.getLooper();
    }

    public boolean handleMessage(Message message) {
        boolean z = false;
        try {
            switch (message.what) {
                case MSG_PREPARED /*1*/:
                    prepareInternal((TrackRenderer[]) message.obj);
                    return true;
                case MSG_STATE_CHANGED /*2*/:
                    incrementalPrepareInternal();
                    return true;
                case MSG_SET_PLAY_WHEN_READY_ACK /*3*/:
                    if (message.arg1 != 0) {
                        z = true;
                    }
                    setPlayWhenReadyInternal(z);
                    return true;
                case MSG_STOP /*4*/:
                    stopInternal();
                    return true;
                case MSG_RELEASE /*5*/:
                    releaseInternal();
                    return true;
                case MSG_SEEK_TO /*6*/:
                    seekToInternal(Util.getLong(message.arg1, message.arg2));
                    return true;
                case MSG_DO_SOME_WORK /*7*/:
                    doSomeWork();
                    return true;
                case MSG_SET_RENDERER_SELECTED_TRACK /*8*/:
                    setRendererSelectedTrackInternal(message.arg1, message.arg2);
                    return true;
                case MSG_CUSTOM /*9*/:
                    sendMessageInternal(message.arg1, message.obj);
                    return true;
                default:
                    return false;
            }
        } catch (Throwable e) {
            Log.e(TAG, "Internal track renderer error.", e);
            this.eventHandler.obtainMessage(MSG_STOP, e).sendToTarget();
            stopInternal();
            return true;
        } catch (Throwable e2) {
            Log.e(TAG, "Internal runtime error.", e2);
            this.eventHandler.obtainMessage(MSG_STOP, new ExoPlaybackException(e2, true)).sendToTarget();
            stopInternal();
            return true;
        }
    }

    public void prepare(TrackRenderer... trackRendererArr) {
        this.handler.obtainMessage(MSG_PREPARED, trackRendererArr).sendToTarget();
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized void release() {
        /*
        r2 = this;
        monitor-enter(r2);
        r0 = r2.released;	 Catch:{ all -> 0x001e }
        if (r0 == 0) goto L_0x0007;
    L_0x0005:
        monitor-exit(r2);
        return;
    L_0x0007:
        r0 = r2.handler;	 Catch:{ all -> 0x001e }
        r1 = 5;
        r0.sendEmptyMessage(r1);	 Catch:{ all -> 0x001e }
    L_0x000d:
        r0 = r2.released;	 Catch:{ all -> 0x001e }
        if (r0 != 0) goto L_0x0021;
    L_0x0011:
        r2.wait();	 Catch:{ InterruptedException -> 0x0015 }
        goto L_0x000d;
    L_0x0015:
        r0 = move-exception;
        r0 = java.lang.Thread.currentThread();	 Catch:{ all -> 0x001e }
        r0.interrupt();	 Catch:{ all -> 0x001e }
        goto L_0x000d;
    L_0x001e:
        r0 = move-exception;
        monitor-exit(r2);
        throw r0;
    L_0x0021:
        r0 = r2.internalPlaybackThread;	 Catch:{ all -> 0x001e }
        r0.quit();	 Catch:{ all -> 0x001e }
        goto L_0x0005;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.hanista.mobogram.messenger.exoplayer.ExoPlayerImplInternal.release():void");
    }

    public void seekTo(long j) {
        this.lastSeekPositionMs = j;
        this.pendingSeekCount.incrementAndGet();
        this.handler.obtainMessage(MSG_SEEK_TO, Util.getTopInt(j), Util.getBottomInt(j)).sendToTarget();
    }

    public void sendMessage(ExoPlayerComponent exoPlayerComponent, int i, Object obj) {
        this.customMessagesSent += MSG_PREPARED;
        this.handler.obtainMessage(MSG_CUSTOM, i, 0, Pair.create(exoPlayerComponent, obj)).sendToTarget();
    }

    public void setPlayWhenReady(boolean z) {
        this.handler.obtainMessage(MSG_SET_PLAY_WHEN_READY_ACK, z ? MSG_PREPARED : 0, 0).sendToTarget();
    }

    public void setRendererSelectedTrack(int i, int i2) {
        this.handler.obtainMessage(MSG_SET_RENDERER_SELECTED_TRACK, i, i2).sendToTarget();
    }

    public void stop() {
        this.handler.sendEmptyMessage(MSG_STOP);
    }
}
