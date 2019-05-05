package com.googlecode.mp4parser.authoring.samples;

import com.coremedia.iso.boxes.Container;
import com.coremedia.iso.boxes.MovieBox;
import com.coremedia.iso.boxes.SampleSizeBox;
import com.coremedia.iso.boxes.SampleToChunkBox.Entry;
import com.coremedia.iso.boxes.TrackBox;
import com.googlecode.mp4parser.authoring.Sample;
import com.googlecode.mp4parser.util.CastUtils;
import com.hanista.mobogram.messenger.exoplayer.extractor.ts.PtsTimestampAdjuster;
import com.hanista.mobogram.tgnet.ConnectionsManager;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

public class DefaultMp4SampleList extends AbstractList<Sample> {
    private static final long MAX_MAP_SIZE = 268435456;
    ByteBuffer[][] cache;
    int[] chunkNumsStartSampleNum;
    long[] chunkOffsets;
    long[] chunkSizes;
    int lastChunk;
    long[][] sampleOffsetsWithinChunks;
    SampleSizeBox ssb;
    Container topLevel;
    TrackBox trackBox;

    /* renamed from: com.googlecode.mp4parser.authoring.samples.DefaultMp4SampleList.1 */
    class C03211 implements Sample {
        private final /* synthetic */ ByteBuffer val$finalCorrectPartOfChunk;
        private final /* synthetic */ long val$finalOffsetWithInChunk;
        private final /* synthetic */ long val$sampleSize;

        C03211(long j, ByteBuffer byteBuffer, long j2) {
            this.val$sampleSize = j;
            this.val$finalCorrectPartOfChunk = byteBuffer;
            this.val$finalOffsetWithInChunk = j2;
        }

        public ByteBuffer asByteBuffer() {
            return (ByteBuffer) ((ByteBuffer) this.val$finalCorrectPartOfChunk.position(CastUtils.l2i(this.val$finalOffsetWithInChunk))).slice().limit(CastUtils.l2i(this.val$sampleSize));
        }

        public long getSize() {
            return this.val$sampleSize;
        }

        public String toString() {
            return "DefaultMp4Sample(size:" + this.val$sampleSize + ")";
        }

        public void writeTo(WritableByteChannel writableByteChannel) {
            writableByteChannel.write(asByteBuffer());
        }
    }

    public DefaultMp4SampleList(long j, Container container) {
        this.trackBox = null;
        this.cache = null;
        this.lastChunk = 0;
        this.topLevel = container;
        for (TrackBox trackBox : ((MovieBox) container.getBoxes(MovieBox.class).get(0)).getBoxes(TrackBox.class)) {
            if (trackBox.getTrackHeaderBox().getTrackId() == j) {
                this.trackBox = trackBox;
            }
        }
        if (this.trackBox == null) {
            throw new RuntimeException("This MP4 does not contain track " + j);
        }
        int i;
        long j2;
        int i2;
        this.chunkOffsets = this.trackBox.getSampleTableBox().getChunkOffsetBox().getChunkOffsets();
        this.chunkSizes = new long[this.chunkOffsets.length];
        this.cache = new ByteBuffer[this.chunkOffsets.length][];
        this.sampleOffsetsWithinChunks = new long[this.chunkOffsets.length][];
        this.ssb = this.trackBox.getSampleTableBox().getSampleSizeBox();
        List entries = this.trackBox.getSampleTableBox().getSampleToChunkBox().getEntries();
        Entry[] entryArr = (Entry[]) entries.toArray(new Entry[entries.size()]);
        Entry entry = entryArr[0];
        long firstChunk = entry.getFirstChunk();
        int l2i = CastUtils.l2i(entry.getSamplesPerChunk());
        int size = size();
        int i3 = 0;
        int i4 = 0;
        int i5 = 1;
        int i6 = 1;
        while (true) {
            int i7;
            i = i4 + 1;
            if (((long) i) != firstChunk) {
                int i8 = l2i;
                j2 = firstChunk;
                i7 = i3;
                i2 = i5;
                i3 = i8;
            } else if (entryArr.length > i5) {
                i4 = i5 + 1;
                Entry entry2 = entryArr[i5];
                i3 = CastUtils.l2i(entry2.getSamplesPerChunk());
                long firstChunk2 = entry2.getFirstChunk();
                i7 = l2i;
                i2 = i4;
                j2 = firstChunk2;
            } else {
                i3 = -1;
                i7 = l2i;
                i2 = i5;
                j2 = Long.MAX_VALUE;
            }
            this.sampleOffsetsWithinChunks[i - 1] = new long[i7];
            i5 = i6 + i7;
            if (i5 > size) {
                break;
            }
            i6 = i5;
            i5 = i2;
            firstChunk2 = j2;
            i4 = i;
            l2i = i3;
            i3 = i7;
            firstChunk = firstChunk2;
        }
        this.chunkNumsStartSampleNum = new int[(i + 1)];
        entry = entryArr[0];
        i5 = 0;
        i4 = 0;
        firstChunk = entry.getFirstChunk();
        l2i = CastUtils.l2i(entry.getSamplesPerChunk());
        i3 = 1;
        i = 1;
        while (true) {
            i6 = i5 + 1;
            this.chunkNumsStartSampleNum[i5] = i3;
            if (((long) i6) != firstChunk) {
                i5 = i;
            } else if (entryArr.length > i) {
                i5 = i + 1;
                entry2 = entryArr[i];
                i4 = CastUtils.l2i(entry2.getSamplesPerChunk());
                firstChunk = entry2.getFirstChunk();
                i8 = i4;
                i4 = l2i;
                l2i = i8;
            } else {
                firstChunk = PtsTimestampAdjuster.DO_NOT_OFFSET;
                i5 = i;
                i8 = l2i;
                l2i = -1;
                i4 = i8;
            }
            i3 += i4;
            if (i3 > size) {
                break;
            }
            i = i5;
            i5 = i6;
        }
        this.chunkNumsStartSampleNum[i6] = ConnectionsManager.DEFAULT_DATACENTER_ID;
        i3 = 0;
        j2 = 0;
        for (int i9 = 1; ((long) i9) <= this.ssb.getSampleCount(); i9++) {
            while (i9 == this.chunkNumsStartSampleNum[i3]) {
                i3++;
                j2 = 0;
            }
            long[] jArr = this.chunkSizes;
            i2 = i3 - 1;
            jArr[i2] = jArr[i2] + this.ssb.getSampleSizeAtIndex(i9 - 1);
            this.sampleOffsetsWithinChunks[i3 - 1][i9 - this.chunkNumsStartSampleNum[i3 - 1]] = j2;
            j2 += this.ssb.getSampleSizeAtIndex(i9 - 1);
        }
    }

    public Sample get(int i) {
        if (((long) i) >= this.ssb.getSampleCount()) {
            throw new IndexOutOfBoundsException();
        }
        int i2;
        int chunkForSample = getChunkForSample(i);
        int i3 = this.chunkNumsStartSampleNum[chunkForSample] - 1;
        long j = this.chunkOffsets[CastUtils.l2i((long) chunkForSample)];
        int i4 = i - i3;
        long[] jArr = this.sampleOffsetsWithinChunks[CastUtils.l2i((long) chunkForSample)];
        long j2 = jArr[i4];
        ByteBuffer[] byteBufferArr = this.cache[CastUtils.l2i((long) chunkForSample)];
        if (byteBufferArr == null) {
            List arrayList = new ArrayList();
            long j3 = 0;
            i2 = 0;
            while (i2 < jArr.length) {
                try {
                    if ((jArr[i2] + this.ssb.getSampleSizeAtIndex(i2 + i3)) - j3 > MAX_MAP_SIZE) {
                        arrayList.add(this.topLevel.getByteBuffer(j + j3, jArr[i2] - j3));
                        j3 = jArr[i2];
                    }
                    i2++;
                } catch (IOException e) {
                    throw new IndexOutOfBoundsException(e.getMessage());
                }
            }
            arrayList.add(this.topLevel.getByteBuffer(j + j3, ((-j3) + jArr[jArr.length - 1]) + this.ssb.getSampleSizeAtIndex((i3 + jArr.length) - 1)));
            byteBufferArr = (ByteBuffer[]) arrayList.toArray(new ByteBuffer[arrayList.size()]);
            this.cache[CastUtils.l2i((long) chunkForSample)] = byteBufferArr;
        }
        for (ByteBuffer byteBuffer : r2) {
            if (j2 < ((long) byteBuffer.limit())) {
                break;
            }
            j2 -= (long) byteBuffer.limit();
        }
        ByteBuffer byteBuffer2 = null;
        return new C03211(this.ssb.getSampleSizeAtIndex(i), byteBuffer2, j2);
    }

    synchronized int getChunkForSample(int i) {
        int i2;
        i2 = i + 1;
        if (i2 >= this.chunkNumsStartSampleNum[this.lastChunk] && i2 < this.chunkNumsStartSampleNum[this.lastChunk + 1]) {
            i2 = this.lastChunk;
        } else if (i2 < this.chunkNumsStartSampleNum[this.lastChunk]) {
            this.lastChunk = 0;
            while (this.chunkNumsStartSampleNum[this.lastChunk + 1] <= i2) {
                this.lastChunk++;
            }
            i2 = this.lastChunk;
        } else {
            this.lastChunk++;
            while (this.chunkNumsStartSampleNum[this.lastChunk + 1] <= i2) {
                this.lastChunk++;
            }
            i2 = this.lastChunk;
        }
        return i2;
    }

    public int size() {
        return CastUtils.l2i(this.trackBox.getSampleTableBox().getSampleSizeBox().getSampleCount());
    }
}
