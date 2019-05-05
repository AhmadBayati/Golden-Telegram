package com.hanista.mobogram.messenger.exoplayer.text.ttml;

import com.hanista.mobogram.messenger.exoplayer.text.Cue;
import com.hanista.mobogram.messenger.exoplayer.text.Subtitle;
import com.hanista.mobogram.messenger.exoplayer.util.Util;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public final class TtmlSubtitle implements Subtitle {
    private final long[] eventTimesUs;
    private final Map<String, TtmlStyle> globalStyles;
    private final Map<String, TtmlRegion> regionMap;
    private final TtmlNode root;

    public TtmlSubtitle(TtmlNode ttmlNode, Map<String, TtmlStyle> map, Map<String, TtmlRegion> map2) {
        this.root = ttmlNode;
        this.regionMap = map2;
        this.globalStyles = map != null ? Collections.unmodifiableMap(map) : Collections.emptyMap();
        this.eventTimesUs = ttmlNode.getEventTimesUs();
    }

    public List<Cue> getCues(long j) {
        return this.root.getCues(j, this.globalStyles, this.regionMap);
    }

    public long getEventTime(int i) {
        return this.eventTimesUs[i];
    }

    public int getEventTimeCount() {
        return this.eventTimesUs.length;
    }

    Map<String, TtmlStyle> getGlobalStyles() {
        return this.globalStyles;
    }

    public long getLastEventTime() {
        return this.eventTimesUs.length == 0 ? -1 : this.eventTimesUs[this.eventTimesUs.length - 1];
    }

    public int getNextEventTimeIndex(long j) {
        int binarySearchCeil = Util.binarySearchCeil(this.eventTimesUs, j, false, false);
        return binarySearchCeil < this.eventTimesUs.length ? binarySearchCeil : -1;
    }

    TtmlNode getRoot() {
        return this.root;
    }
}
