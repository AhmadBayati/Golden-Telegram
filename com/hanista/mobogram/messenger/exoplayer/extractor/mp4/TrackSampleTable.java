package com.hanista.mobogram.messenger.exoplayer.extractor.mp4;

import com.hanista.mobogram.messenger.exoplayer.util.Assertions;
import com.hanista.mobogram.messenger.exoplayer.util.Util;

final class TrackSampleTable {
    public static final int NO_SAMPLE = -1;
    public final int[] flags;
    public final int maximumSize;
    public final long[] offsets;
    public final int sampleCount;
    public final int[] sizes;
    public final long[] timestampsUs;

    TrackSampleTable(long[] jArr, int[] iArr, int i, long[] jArr2, int[] iArr2) {
        boolean z = true;
        Assertions.checkArgument(iArr.length == jArr2.length);
        Assertions.checkArgument(jArr.length == jArr2.length);
        if (iArr2.length != jArr2.length) {
            z = false;
        }
        Assertions.checkArgument(z);
        this.offsets = jArr;
        this.sizes = iArr;
        this.maximumSize = i;
        this.timestampsUs = jArr2;
        this.flags = iArr2;
        this.sampleCount = jArr.length;
    }

    public int getIndexOfEarlierOrEqualSynchronizationSample(long j) {
        for (int binarySearchFloor = Util.binarySearchFloor(this.timestampsUs, j, true, false); binarySearchFloor >= 0; binarySearchFloor += NO_SAMPLE) {
            if ((this.flags[binarySearchFloor] & 1) != 0) {
                return binarySearchFloor;
            }
        }
        return NO_SAMPLE;
    }

    public int getIndexOfLaterOrEqualSynchronizationSample(long j) {
        for (int binarySearchCeil = Util.binarySearchCeil(this.timestampsUs, j, true, false); binarySearchCeil < this.timestampsUs.length; binarySearchCeil++) {
            if ((this.flags[binarySearchCeil] & 1) != 0) {
                return binarySearchCeil;
            }
        }
        return NO_SAMPLE;
    }
}
