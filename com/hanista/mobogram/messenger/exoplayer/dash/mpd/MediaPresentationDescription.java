package com.hanista.mobogram.messenger.exoplayer.dash.mpd;

import com.hanista.mobogram.messenger.exoplayer.util.ManifestFetcher.RedirectingManifest;
import java.util.Collections;
import java.util.List;

public class MediaPresentationDescription implements RedirectingManifest {
    public final long availabilityStartTime;
    public final long duration;
    public final boolean dynamic;
    public final String location;
    public final long minBufferTime;
    public final long minUpdatePeriod;
    private final List<Period> periods;
    public final long timeShiftBufferDepth;
    public final UtcTimingElement utcTiming;

    public MediaPresentationDescription(long j, long j2, long j3, boolean z, long j4, long j5, UtcTimingElement utcTimingElement, String str, List<Period> list) {
        List emptyList;
        this.availabilityStartTime = j;
        this.duration = j2;
        this.minBufferTime = j3;
        this.dynamic = z;
        this.minUpdatePeriod = j4;
        this.timeShiftBufferDepth = j5;
        this.utcTiming = utcTimingElement;
        this.location = str;
        if (list == null) {
            emptyList = Collections.emptyList();
        }
        this.periods = emptyList;
    }

    public final String getNextManifestUri() {
        return this.location;
    }

    public final Period getPeriod(int i) {
        return (Period) this.periods.get(i);
    }

    public final int getPeriodCount() {
        return this.periods.size();
    }

    public final long getPeriodDuration(int i) {
        return i == this.periods.size() + -1 ? this.duration == -1 ? -1 : this.duration - ((Period) this.periods.get(i)).startMs : ((Period) this.periods.get(i + 1)).startMs - ((Period) this.periods.get(i)).startMs;
    }
}
