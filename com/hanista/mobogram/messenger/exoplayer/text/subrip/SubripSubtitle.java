package com.hanista.mobogram.messenger.exoplayer.text.subrip;

import com.hanista.mobogram.messenger.exoplayer.text.Cue;
import com.hanista.mobogram.messenger.exoplayer.text.Subtitle;
import com.hanista.mobogram.messenger.exoplayer.util.Assertions;
import com.hanista.mobogram.messenger.exoplayer.util.Util;
import java.util.Collections;
import java.util.List;

final class SubripSubtitle implements Subtitle {
    private final long[] cueTimesUs;
    private final Cue[] cues;

    public SubripSubtitle(Cue[] cueArr, long[] jArr) {
        this.cues = cueArr;
        this.cueTimesUs = jArr;
    }

    public List<Cue> getCues(long j) {
        int binarySearchFloor = Util.binarySearchFloor(this.cueTimesUs, j, true, false);
        return (binarySearchFloor == -1 || this.cues[binarySearchFloor] == null) ? Collections.emptyList() : Collections.singletonList(this.cues[binarySearchFloor]);
    }

    public long getEventTime(int i) {
        boolean z = true;
        Assertions.checkArgument(i >= 0);
        if (i >= this.cueTimesUs.length) {
            z = false;
        }
        Assertions.checkArgument(z);
        return this.cueTimesUs[i];
    }

    public int getEventTimeCount() {
        return this.cueTimesUs.length;
    }

    public long getLastEventTime() {
        return getEventTimeCount() == 0 ? -1 : this.cueTimesUs[this.cueTimesUs.length - 1];
    }

    public int getNextEventTimeIndex(long j) {
        int binarySearchCeil = Util.binarySearchCeil(this.cueTimesUs, j, false, false);
        return binarySearchCeil < this.cueTimesUs.length ? binarySearchCeil : -1;
    }
}
