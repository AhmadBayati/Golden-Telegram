package com.hanista.mobogram.messenger.exoplayer.dash.mpd;

import com.hanista.mobogram.messenger.exoplayer.dash.DashSegmentIndex;

final class DashSingleSegmentIndex implements DashSegmentIndex {
    private final RangedUri uri;

    public DashSingleSegmentIndex(RangedUri rangedUri) {
        this.uri = rangedUri;
    }

    public long getDurationUs(int i, long j) {
        return j;
    }

    public int getFirstSegmentNum() {
        return 0;
    }

    public int getLastSegmentNum(long j) {
        return 0;
    }

    public int getSegmentNum(long j, long j2) {
        return 0;
    }

    public RangedUri getSegmentUrl(int i) {
        return this.uri;
    }

    public long getTimeUs(int i) {
        return 0;
    }

    public boolean isExplicit() {
        return true;
    }
}
