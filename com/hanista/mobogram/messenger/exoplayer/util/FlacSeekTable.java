package com.hanista.mobogram.messenger.exoplayer.util;

import com.hanista.mobogram.messenger.exoplayer.C0700C;
import com.hanista.mobogram.messenger.exoplayer.extractor.SeekMap;

public final class FlacSeekTable {
    private static final int METADATA_LENGTH_OFFSET = 1;
    private static final int SEEK_POINT_SIZE = 18;
    private final long[] offsets;
    private final long[] sampleNumbers;

    /* renamed from: com.hanista.mobogram.messenger.exoplayer.util.FlacSeekTable.1 */
    class C07521 implements SeekMap {
        final /* synthetic */ long val$firstFrameOffset;
        final /* synthetic */ long val$sampleRate;

        C07521(long j, long j2) {
            this.val$sampleRate = j;
            this.val$firstFrameOffset = j2;
        }

        public long getPosition(long j) {
            int binarySearchFloor = Util.binarySearchFloor(FlacSeekTable.this.sampleNumbers, (this.val$sampleRate * j) / C0700C.MICROS_PER_SECOND, true, true);
            return FlacSeekTable.this.offsets[binarySearchFloor] + this.val$firstFrameOffset;
        }

        public boolean isSeekable() {
            return true;
        }
    }

    private FlacSeekTable(long[] jArr, long[] jArr2) {
        this.sampleNumbers = jArr;
        this.offsets = jArr2;
    }

    public static FlacSeekTable parseSeekTable(ParsableByteArray parsableByteArray) {
        parsableByteArray.skipBytes(METADATA_LENGTH_OFFSET);
        int readUnsignedInt24 = parsableByteArray.readUnsignedInt24() / SEEK_POINT_SIZE;
        long[] jArr = new long[readUnsignedInt24];
        long[] jArr2 = new long[readUnsignedInt24];
        for (int i = 0; i < readUnsignedInt24; i += METADATA_LENGTH_OFFSET) {
            jArr[i] = parsableByteArray.readLong();
            jArr2[i] = parsableByteArray.readLong();
            parsableByteArray.skipBytes(2);
        }
        return new FlacSeekTable(jArr, jArr2);
    }

    public SeekMap createSeekMap(long j, long j2) {
        return new C07521(j2, j);
    }
}
