package com.hanista.mobogram.messenger.exoplayer.text;

import java.util.List;

final class PlayableSubtitle implements Subtitle {
    private final long offsetUs;
    public final long startTimeUs;
    private final Subtitle subtitle;

    public PlayableSubtitle(Subtitle subtitle, boolean z, long j, long j2) {
        this.subtitle = subtitle;
        this.startTimeUs = j;
        if (!z) {
            j = 0;
        }
        this.offsetUs = j + j2;
    }

    public List<Cue> getCues(long j) {
        return this.subtitle.getCues(j - this.offsetUs);
    }

    public long getEventTime(int i) {
        return this.subtitle.getEventTime(i) + this.offsetUs;
    }

    public int getEventTimeCount() {
        return this.subtitle.getEventTimeCount();
    }

    public long getLastEventTime() {
        return this.subtitle.getLastEventTime() + this.offsetUs;
    }

    public int getNextEventTimeIndex(long j) {
        return this.subtitle.getNextEventTimeIndex(j - this.offsetUs);
    }
}
