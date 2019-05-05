package com.hanista.mobogram.messenger.exoplayer;

public final class DummyTrackRenderer extends TrackRenderer {
    protected boolean doPrepare(long j) {
        return true;
    }

    protected void doSomeWork(long j, long j2) {
        throw new IllegalStateException();
    }

    protected long getBufferedPositionUs() {
        throw new IllegalStateException();
    }

    protected long getDurationUs() {
        throw new IllegalStateException();
    }

    protected MediaFormat getFormat(int i) {
        throw new IllegalStateException();
    }

    protected int getTrackCount() {
        return 0;
    }

    protected boolean isEnded() {
        throw new IllegalStateException();
    }

    protected boolean isReady() {
        throw new IllegalStateException();
    }

    protected void maybeThrowError() {
        throw new IllegalStateException();
    }

    protected void seekTo(long j) {
        throw new IllegalStateException();
    }
}
