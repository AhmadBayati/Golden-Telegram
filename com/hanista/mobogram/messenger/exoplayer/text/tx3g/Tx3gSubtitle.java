package com.hanista.mobogram.messenger.exoplayer.text.tx3g;

import com.hanista.mobogram.messenger.exoplayer.text.Cue;
import com.hanista.mobogram.messenger.exoplayer.text.Subtitle;
import com.hanista.mobogram.messenger.exoplayer.util.Assertions;
import java.util.Collections;
import java.util.List;

final class Tx3gSubtitle implements Subtitle {
    public static final Tx3gSubtitle EMPTY;
    private final List<Cue> cues;

    static {
        EMPTY = new Tx3gSubtitle();
    }

    private Tx3gSubtitle() {
        this.cues = Collections.emptyList();
    }

    public Tx3gSubtitle(Cue cue) {
        this.cues = Collections.singletonList(cue);
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
