package com.hanista.mobogram.messenger.exoplayer.text.webvtt;

import com.hanista.mobogram.messenger.exoplayer.text.Cue;
import com.hanista.mobogram.messenger.exoplayer.text.Subtitle;
import com.hanista.mobogram.messenger.exoplayer.util.Assertions;
import java.util.Collections;
import java.util.List;

final class Mp4WebvttSubtitle implements Subtitle {
    private final List<Cue> cues;

    public Mp4WebvttSubtitle(List<Cue> list) {
        this.cues = Collections.unmodifiableList(list);
    }

    public List<Cue> getCues(long j) {
        return j >= 0 ? this.cues : Collections.emptyList();
    }

    public long getEventTime(int i) {
        Assertions.checkArgument(i == 0);
        return 0;
    }

    public int getEventTimeCount() {
        return 1;
    }

    public long getLastEventTime() {
        return 0;
    }

    public int getNextEventTimeIndex(long j) {
        return j < 0 ? 0 : -1;
    }
}
