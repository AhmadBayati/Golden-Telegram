package com.hanista.mobogram.messenger.exoplayer.hls;

import android.util.SparseArray;
import com.hanista.mobogram.messenger.exoplayer.extractor.ts.PtsTimestampAdjuster;

public final class PtsTimestampAdjusterProvider {
    private final SparseArray<PtsTimestampAdjuster> ptsTimestampAdjusters;

    public PtsTimestampAdjusterProvider() {
        this.ptsTimestampAdjusters = new SparseArray();
    }

    public PtsTimestampAdjuster getAdjuster(boolean z, int i, long j) {
        PtsTimestampAdjuster ptsTimestampAdjuster = (PtsTimestampAdjuster) this.ptsTimestampAdjusters.get(i);
        if (z && ptsTimestampAdjuster == null) {
            ptsTimestampAdjuster = new PtsTimestampAdjuster(j);
            this.ptsTimestampAdjusters.put(i, ptsTimestampAdjuster);
        }
        return !z ? (ptsTimestampAdjuster == null || !ptsTimestampAdjuster.isInitialized()) ? null : ptsTimestampAdjuster : ptsTimestampAdjuster;
    }

    public void reset() {
        this.ptsTimestampAdjusters.clear();
    }
}
