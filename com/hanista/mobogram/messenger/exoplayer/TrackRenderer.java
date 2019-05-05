package com.hanista.mobogram.messenger.exoplayer;

import com.hanista.mobogram.messenger.exoplayer.ExoPlayer.ExoPlayerComponent;
import com.hanista.mobogram.messenger.exoplayer.util.Assertions;

public abstract class TrackRenderer implements ExoPlayerComponent {
    public static final long END_OF_TRACK_US = -3;
    public static final long MATCH_LONGEST_US = -2;
    protected static final int STATE_ENABLED = 2;
    protected static final int STATE_PREPARED = 1;
    protected static final int STATE_RELEASED = -1;
    protected static final int STATE_STARTED = 3;
    protected static final int STATE_UNPREPARED = 0;
    public static final long UNKNOWN_TIME_US = -1;
    private int state;

    final void disable() {
        Assertions.checkState(this.state == STATE_ENABLED);
        this.state = STATE_PREPARED;
        onDisabled();
    }

    protected abstract boolean doPrepare(long j);

    protected abstract void doSomeWork(long j, long j2);

    final void enable(int i, long j, boolean z) {
        boolean z2 = true;
        if (this.state != STATE_PREPARED) {
            z2 = false;
        }
        Assertions.checkState(z2);
        this.state = STATE_ENABLED;
        onEnabled(i, j, z);
    }

    protected abstract long getBufferedPositionUs();

    protected abstract long getDurationUs();

    protected abstract MediaFormat getFormat(int i);

    protected MediaClock getMediaClock() {
        return null;
    }

    protected final int getState() {
        return this.state;
    }

    protected abstract int getTrackCount();

    public void handleMessage(int i, Object obj) {
    }

    protected abstract boolean isEnded();

    protected abstract boolean isReady();

    protected abstract void maybeThrowError();

    protected void onDisabled() {
    }

    protected void onEnabled(int i, long j, boolean z) {
    }

    protected void onReleased() {
    }

    protected void onStarted() {
    }

    protected void onStopped() {
    }

    final int prepare(long j) {
        int i = STATE_PREPARED;
        Assertions.checkState(this.state == 0);
        if (!doPrepare(j)) {
            i = STATE_UNPREPARED;
        }
        this.state = i;
        return this.state;
    }

    final void release() {
        boolean z = (this.state == STATE_ENABLED || this.state == STATE_STARTED || this.state == STATE_RELEASED) ? false : true;
        Assertions.checkState(z);
        this.state = STATE_RELEASED;
        onReleased();
    }

    protected abstract void seekTo(long j);

    final void start() {
        Assertions.checkState(this.state == STATE_ENABLED);
        this.state = STATE_STARTED;
        onStarted();
    }

    final void stop() {
        Assertions.checkState(this.state == STATE_STARTED);
        this.state = STATE_ENABLED;
        onStopped();
    }
}
