package com.hanista.mobogram.messenger.exoplayer.extractor.mp4;

import com.hanista.mobogram.messenger.exoplayer.extractor.ExtractorInput;
import com.hanista.mobogram.messenger.exoplayer.util.ParsableByteArray;

final class TrackFragment {
    public long auxiliaryDataPosition;
    public long dataPosition;
    public boolean definesEncryptionData;
    public DefaultSampleValues header;
    public int length;
    public long nextFragmentDecodeTime;
    public int[] sampleCompositionTimeOffsetTable;
    public long[] sampleDecodingTimeTable;
    public ParsableByteArray sampleEncryptionData;
    public int sampleEncryptionDataLength;
    public boolean sampleEncryptionDataNeedsFill;
    public boolean[] sampleHasSubsampleEncryptionTable;
    public boolean[] sampleIsSyncFrameTable;
    public int[] sampleSizeTable;
    public TrackEncryptionBox trackEncryptionBox;

    TrackFragment() {
    }

    public void fillEncryptionData(ExtractorInput extractorInput) {
        extractorInput.readFully(this.sampleEncryptionData.data, 0, this.sampleEncryptionDataLength);
        this.sampleEncryptionData.setPosition(0);
        this.sampleEncryptionDataNeedsFill = false;
    }

    public void fillEncryptionData(ParsableByteArray parsableByteArray) {
        parsableByteArray.readBytes(this.sampleEncryptionData.data, 0, this.sampleEncryptionDataLength);
        this.sampleEncryptionData.setPosition(0);
        this.sampleEncryptionDataNeedsFill = false;
    }

    public long getSamplePresentationTime(int i) {
        return this.sampleDecodingTimeTable[i] + ((long) this.sampleCompositionTimeOffsetTable[i]);
    }

    public void initEncryptionData(int i) {
        if (this.sampleEncryptionData == null || this.sampleEncryptionData.limit() < i) {
            this.sampleEncryptionData = new ParsableByteArray(i);
        }
        this.sampleEncryptionDataLength = i;
        this.definesEncryptionData = true;
        this.sampleEncryptionDataNeedsFill = true;
    }

    public void initTables(int i) {
        this.length = i;
        if (this.sampleSizeTable == null || this.sampleSizeTable.length < this.length) {
            int i2 = (i * 125) / 100;
            this.sampleSizeTable = new int[i2];
            this.sampleCompositionTimeOffsetTable = new int[i2];
            this.sampleDecodingTimeTable = new long[i2];
            this.sampleIsSyncFrameTable = new boolean[i2];
            this.sampleHasSubsampleEncryptionTable = new boolean[i2];
        }
    }

    public void reset() {
        this.length = 0;
        this.nextFragmentDecodeTime = 0;
        this.definesEncryptionData = false;
        this.sampleEncryptionDataNeedsFill = false;
        this.trackEncryptionBox = null;
    }
}
