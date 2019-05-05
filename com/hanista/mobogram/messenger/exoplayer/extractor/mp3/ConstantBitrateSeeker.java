package com.hanista.mobogram.messenger.exoplayer.extractor.mp3;

import com.hanista.mobogram.messenger.exoplayer.C0700C;

final class ConstantBitrateSeeker implements Seeker {
    private static final int BITS_PER_BYTE = 8;
    private final int bitrate;
    private final long durationUs;
    private final long firstFramePosition;

    public ConstantBitrateSeeker(long j, int i, long j2) {
        long j3 = -1;
        this.firstFramePosition = j;
        this.bitrate = i;
        if (j2 != -1) {
            j3 = getTimeUs(j2);
        }
        this.durationUs = j3;
    }

    public long getDurationUs() {
        return this.durationUs;
    }

    public long getPosition(long j) {
        return this.durationUs == -1 ? 0 : this.firstFramePosition + ((((long) this.bitrate) * j) / 8000000);
    }

    public long getTimeUs(long j) {
        return ((Math.max(0, j - this.firstFramePosition) * C0700C.MICROS_PER_SECOND) * 8) / ((long) this.bitrate);
    }

    public boolean isSeekable() {
        return this.durationUs != -1;
    }
}
