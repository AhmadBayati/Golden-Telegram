package com.hanista.mobogram.messenger.exoplayer;

import android.os.SystemClock;

final class StandaloneMediaClock implements MediaClock {
    private long deltaUs;
    private long positionUs;
    private boolean started;

    StandaloneMediaClock() {
    }

    private long elapsedRealtimeMinus(long j) {
        return (SystemClock.elapsedRealtime() * 1000) - j;
    }

    public long getPositionUs() {
        return this.started ? elapsedRealtimeMinus(this.deltaUs) : this.positionUs;
    }

    public void setPositionUs(long j) {
        this.positionUs = j;
        this.deltaUs = elapsedRealtimeMinus(j);
    }

    public void start() {
        if (!this.started) {
            this.started = true;
            this.deltaUs = elapsedRealtimeMinus(this.positionUs);
        }
    }

    public void stop() {
        if (this.started) {
            this.positionUs = elapsedRealtimeMinus(this.deltaUs);
            this.started = false;
        }
    }
}
