package com.hanista.mobogram.messenger.exoplayer.extractor.ts;

import com.hanista.mobogram.messenger.exoplayer.C0700C;

public final class PtsTimestampAdjuster {
    public static final long DO_NOT_OFFSET = Long.MAX_VALUE;
    private static final long MAX_PTS_PLUS_ONE = 8589934592L;
    private final long firstSampleTimestampUs;
    private volatile long lastPts;
    private long timestampOffsetUs;

    public PtsTimestampAdjuster(long j) {
        this.firstSampleTimestampUs = j;
        this.lastPts = Long.MIN_VALUE;
    }

    public static long ptsToUs(long j) {
        return (C0700C.MICROS_PER_SECOND * j) / 90000;
    }

    public static long usToPts(long j) {
        return (90000 * j) / C0700C.MICROS_PER_SECOND;
    }

    public long adjustTimestamp(long j) {
        long j2;
        long j3;
        if (this.lastPts != Long.MIN_VALUE) {
            j2 = (this.lastPts + 4294967296L) / MAX_PTS_PLUS_ONE;
            j3 = ((j2 - 1) * MAX_PTS_PLUS_ONE) + j;
            j2 = (j2 * MAX_PTS_PLUS_ONE) + j;
            if (Math.abs(j3 - this.lastPts) >= Math.abs(j2 - this.lastPts)) {
                j3 = j2;
            }
        } else {
            j3 = j;
        }
        j2 = ptsToUs(j3);
        if (this.firstSampleTimestampUs != DO_NOT_OFFSET && this.lastPts == Long.MIN_VALUE) {
            this.timestampOffsetUs = this.firstSampleTimestampUs - j2;
        }
        this.lastPts = j3;
        return this.timestampOffsetUs + j2;
    }

    public boolean isInitialized() {
        return this.lastPts != Long.MIN_VALUE;
    }

    public void reset() {
        this.lastPts = Long.MIN_VALUE;
    }
}
