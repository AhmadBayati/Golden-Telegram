package com.hanista.mobogram.messenger.exoplayer.extractor.ts;

import android.util.SparseArray;
import com.hanista.mobogram.messenger.exoplayer.MediaFormat;
import com.hanista.mobogram.messenger.exoplayer.extractor.TrackOutput;
import com.hanista.mobogram.messenger.exoplayer.util.MimeTypes;
import com.hanista.mobogram.messenger.exoplayer.util.NalUnitUtil;
import com.hanista.mobogram.messenger.exoplayer.util.NalUnitUtil.PpsData;
import com.hanista.mobogram.messenger.exoplayer.util.NalUnitUtil.SpsData;
import com.hanista.mobogram.messenger.exoplayer.util.ParsableBitArray;
import com.hanista.mobogram.messenger.exoplayer.util.ParsableByteArray;
import com.hanista.mobogram.tgnet.TLRPC;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

final class H264Reader extends ElementaryStreamReader {
    private static final int NAL_UNIT_TYPE_PPS = 8;
    private static final int NAL_UNIT_TYPE_SEI = 6;
    private static final int NAL_UNIT_TYPE_SPS = 7;
    private boolean hasOutputFormat;
    private long pesTimeUs;
    private final NalUnitTargetBuffer pps;
    private final boolean[] prefixFlags;
    private final SampleReader sampleReader;
    private final NalUnitTargetBuffer sei;
    private final SeiReader seiReader;
    private final ParsableByteArray seiWrapper;
    private final NalUnitTargetBuffer sps;
    private long totalBytesWritten;

    private static final class SampleReader {
        private static final int DEFAULT_BUFFER_SIZE = 128;
        private static final int NAL_UNIT_TYPE_AUD = 9;
        private static final int NAL_UNIT_TYPE_IDR = 5;
        private static final int NAL_UNIT_TYPE_NON_IDR = 1;
        private static final int NAL_UNIT_TYPE_PARTITION_A = 2;
        private final boolean allowNonIdrKeyframes;
        private byte[] buffer;
        private int bufferLength;
        private final boolean detectAccessUnits;
        private boolean isFilling;
        private long nalUnitStartPosition;
        private long nalUnitTimeUs;
        private int nalUnitType;
        private final TrackOutput output;
        private final SparseArray<PpsData> pps;
        private SliceHeaderData previousSliceHeader;
        private boolean readingSample;
        private boolean sampleIsKeyframe;
        private long samplePosition;
        private long sampleTimeUs;
        private final ParsableBitArray scratch;
        private SliceHeaderData sliceHeader;
        private final SparseArray<SpsData> sps;

        private static final class SliceHeaderData {
            private static final int SLICE_TYPE_ALL_I = 7;
            private static final int SLICE_TYPE_I = 2;
            private boolean bottomFieldFlag;
            private boolean bottomFieldFlagPresent;
            private int deltaPicOrderCnt0;
            private int deltaPicOrderCnt1;
            private int deltaPicOrderCntBottom;
            private boolean fieldPicFlag;
            private int frameNum;
            private boolean hasSliceType;
            private boolean idrPicFlag;
            private int idrPicId;
            private boolean isComplete;
            private int nalRefIdc;
            private int picOrderCntLsb;
            private int picParameterSetId;
            private int sliceType;
            private SpsData spsData;

            private SliceHeaderData() {
            }

            private boolean isFirstVclNalUnitOfPicture(SliceHeaderData sliceHeaderData) {
                if (this.isComplete) {
                    if (!sliceHeaderData.isComplete || this.frameNum != sliceHeaderData.frameNum || this.picParameterSetId != sliceHeaderData.picParameterSetId || this.fieldPicFlag != sliceHeaderData.fieldPicFlag) {
                        return true;
                    }
                    if (this.bottomFieldFlagPresent && sliceHeaderData.bottomFieldFlagPresent && this.bottomFieldFlag != sliceHeaderData.bottomFieldFlag) {
                        return true;
                    }
                    if (this.nalRefIdc != sliceHeaderData.nalRefIdc && (this.nalRefIdc == 0 || sliceHeaderData.nalRefIdc == 0)) {
                        return true;
                    }
                    if (this.spsData.picOrderCountType == 0 && sliceHeaderData.spsData.picOrderCountType == 0 && (this.picOrderCntLsb != sliceHeaderData.picOrderCntLsb || this.deltaPicOrderCntBottom != sliceHeaderData.deltaPicOrderCntBottom)) {
                        return true;
                    }
                    if ((this.spsData.picOrderCountType == SampleReader.NAL_UNIT_TYPE_NON_IDR && sliceHeaderData.spsData.picOrderCountType == SampleReader.NAL_UNIT_TYPE_NON_IDR && (this.deltaPicOrderCnt0 != sliceHeaderData.deltaPicOrderCnt0 || this.deltaPicOrderCnt1 != sliceHeaderData.deltaPicOrderCnt1)) || this.idrPicFlag != sliceHeaderData.idrPicFlag) {
                        return true;
                    }
                    if (this.idrPicFlag && sliceHeaderData.idrPicFlag && this.idrPicId != sliceHeaderData.idrPicId) {
                        return true;
                    }
                }
                return false;
            }

            public void clear() {
                this.hasSliceType = false;
                this.isComplete = false;
            }

            public boolean isISlice() {
                return this.hasSliceType && (this.sliceType == SLICE_TYPE_ALL_I || this.sliceType == SLICE_TYPE_I);
            }

            public void setAll(SpsData spsData, int i, int i2, int i3, int i4, boolean z, boolean z2, boolean z3, boolean z4, int i5, int i6, int i7, int i8, int i9) {
                this.spsData = spsData;
                this.nalRefIdc = i;
                this.sliceType = i2;
                this.frameNum = i3;
                this.picParameterSetId = i4;
                this.fieldPicFlag = z;
                this.bottomFieldFlagPresent = z2;
                this.bottomFieldFlag = z3;
                this.idrPicFlag = z4;
                this.idrPicId = i5;
                this.picOrderCntLsb = i6;
                this.deltaPicOrderCntBottom = i7;
                this.deltaPicOrderCnt0 = i8;
                this.deltaPicOrderCnt1 = i9;
                this.isComplete = true;
                this.hasSliceType = true;
            }

            public void setSliceType(int i) {
                this.sliceType = i;
                this.hasSliceType = true;
            }
        }

        public SampleReader(TrackOutput trackOutput, boolean z, boolean z2) {
            this.output = trackOutput;
            this.allowNonIdrKeyframes = z;
            this.detectAccessUnits = z2;
            this.sps = new SparseArray();
            this.pps = new SparseArray();
            this.previousSliceHeader = new SliceHeaderData();
            this.sliceHeader = new SliceHeaderData();
            this.scratch = new ParsableBitArray();
            this.buffer = new byte[DEFAULT_BUFFER_SIZE];
            reset();
        }

        private void outputSample(int i) {
            this.output.sampleMetadata(this.sampleTimeUs, this.sampleIsKeyframe ? NAL_UNIT_TYPE_NON_IDR : 0, (int) (this.nalUnitStartPosition - this.samplePosition), i, null);
        }

        public void appendToNalUnit(byte[] bArr, int i, int i2) {
            if (this.isFilling) {
                int i3 = i2 - i;
                if (this.buffer.length < this.bufferLength + i3) {
                    this.buffer = Arrays.copyOf(this.buffer, (this.bufferLength + i3) * NAL_UNIT_TYPE_PARTITION_A);
                }
                System.arraycopy(bArr, i, this.buffer, this.bufferLength, i3);
                this.bufferLength = i3 + this.bufferLength;
                this.scratch.reset(this.buffer, this.bufferLength);
                if (this.scratch.bitsLeft() >= H264Reader.NAL_UNIT_TYPE_PPS) {
                    this.scratch.skipBits(NAL_UNIT_TYPE_NON_IDR);
                    int readBits = this.scratch.readBits(NAL_UNIT_TYPE_PARTITION_A);
                    this.scratch.skipBits(NAL_UNIT_TYPE_IDR);
                    if (this.scratch.canReadExpGolombCodedNum()) {
                        this.scratch.readUnsignedExpGolombCodedInt();
                        if (this.scratch.canReadExpGolombCodedNum()) {
                            int readUnsignedExpGolombCodedInt = this.scratch.readUnsignedExpGolombCodedInt();
                            if (!this.detectAccessUnits) {
                                this.isFilling = false;
                                this.sliceHeader.setSliceType(readUnsignedExpGolombCodedInt);
                            } else if (this.scratch.canReadExpGolombCodedNum()) {
                                int readUnsignedExpGolombCodedInt2 = this.scratch.readUnsignedExpGolombCodedInt();
                                if (this.pps.indexOfKey(readUnsignedExpGolombCodedInt2) < 0) {
                                    this.isFilling = false;
                                    return;
                                }
                                PpsData ppsData = (PpsData) this.pps.get(readUnsignedExpGolombCodedInt2);
                                SpsData spsData = (SpsData) this.sps.get(ppsData.seqParameterSetId);
                                if (spsData.separateColorPlaneFlag) {
                                    if (this.scratch.bitsLeft() >= NAL_UNIT_TYPE_PARTITION_A) {
                                        this.scratch.skipBits(NAL_UNIT_TYPE_PARTITION_A);
                                    } else {
                                        return;
                                    }
                                }
                                if (this.scratch.bitsLeft() >= spsData.frameNumLength) {
                                    boolean z = false;
                                    boolean z2 = false;
                                    boolean z3 = false;
                                    int readBits2 = this.scratch.readBits(spsData.frameNumLength);
                                    if (!spsData.frameMbsOnlyFlag) {
                                        if (this.scratch.bitsLeft() >= NAL_UNIT_TYPE_NON_IDR) {
                                            z = this.scratch.readBit();
                                            if (z) {
                                                if (this.scratch.bitsLeft() >= NAL_UNIT_TYPE_NON_IDR) {
                                                    z3 = this.scratch.readBit();
                                                    z2 = true;
                                                } else {
                                                    return;
                                                }
                                            }
                                        }
                                        return;
                                    }
                                    boolean z4 = this.nalUnitType == NAL_UNIT_TYPE_IDR;
                                    int i4 = 0;
                                    if (z4) {
                                        if (this.scratch.canReadExpGolombCodedNum()) {
                                            i4 = this.scratch.readUnsignedExpGolombCodedInt();
                                        } else {
                                            return;
                                        }
                                    }
                                    int i5 = 0;
                                    int i6 = 0;
                                    int i7 = 0;
                                    int i8 = 0;
                                    int i9;
                                    if (spsData.picOrderCountType == 0) {
                                        i5 = this.scratch.bitsLeft();
                                        i9 = spsData.picOrderCntLsbLength;
                                        if (i5 >= r0) {
                                            i5 = this.scratch.readBits(spsData.picOrderCntLsbLength);
                                            if (ppsData.bottomFieldPicOrderInFramePresentFlag && !z) {
                                                if (this.scratch.canReadExpGolombCodedNum()) {
                                                    i6 = this.scratch.readSignedExpGolombCodedInt();
                                                } else {
                                                    return;
                                                }
                                            }
                                        }
                                        return;
                                    }
                                    i9 = spsData.picOrderCountType;
                                    if (r0 == NAL_UNIT_TYPE_NON_IDR && !spsData.deltaPicOrderAlwaysZeroFlag) {
                                        if (this.scratch.canReadExpGolombCodedNum()) {
                                            i7 = this.scratch.readSignedExpGolombCodedInt();
                                            if (ppsData.bottomFieldPicOrderInFramePresentFlag && !z) {
                                                if (this.scratch.canReadExpGolombCodedNum()) {
                                                    i8 = this.scratch.readSignedExpGolombCodedInt();
                                                } else {
                                                    return;
                                                }
                                            }
                                        }
                                        return;
                                    }
                                    this.sliceHeader.setAll(spsData, readBits, readUnsignedExpGolombCodedInt, readBits2, readUnsignedExpGolombCodedInt2, z, z2, z3, z4, i4, i5, i6, i7, i8);
                                    this.isFilling = false;
                                }
                            }
                        }
                    }
                }
            }
        }

        public void endNalUnit(long j, int i) {
            int i2 = 0;
            if (this.nalUnitType == NAL_UNIT_TYPE_AUD || (this.detectAccessUnits && this.sliceHeader.isFirstVclNalUnitOfPicture(this.previousSliceHeader))) {
                if (this.readingSample) {
                    outputSample(((int) (j - this.nalUnitStartPosition)) + i);
                }
                this.samplePosition = this.nalUnitStartPosition;
                this.sampleTimeUs = this.nalUnitTimeUs;
                this.sampleIsKeyframe = false;
                this.readingSample = true;
            }
            boolean z = this.sampleIsKeyframe;
            if (this.nalUnitType == NAL_UNIT_TYPE_IDR || (this.allowNonIdrKeyframes && this.nalUnitType == NAL_UNIT_TYPE_NON_IDR && this.sliceHeader.isISlice())) {
                i2 = NAL_UNIT_TYPE_NON_IDR;
            }
            this.sampleIsKeyframe = i2 | z;
        }

        public boolean needsSpsPps() {
            return this.detectAccessUnits;
        }

        public void putPps(PpsData ppsData) {
            this.pps.append(ppsData.picParameterSetId, ppsData);
        }

        public void putSps(SpsData spsData) {
            this.sps.append(spsData.seqParameterSetId, spsData);
        }

        public void reset() {
            this.isFilling = false;
            this.readingSample = false;
            this.sliceHeader.clear();
        }

        public void startNalUnit(long j, int i, long j2) {
            this.nalUnitType = i;
            this.nalUnitTimeUs = j2;
            this.nalUnitStartPosition = j;
            if (!(this.allowNonIdrKeyframes && this.nalUnitType == NAL_UNIT_TYPE_NON_IDR)) {
                if (!this.detectAccessUnits) {
                    return;
                }
                if (!(this.nalUnitType == NAL_UNIT_TYPE_IDR || this.nalUnitType == NAL_UNIT_TYPE_NON_IDR || this.nalUnitType == NAL_UNIT_TYPE_PARTITION_A)) {
                    return;
                }
            }
            SliceHeaderData sliceHeaderData = this.previousSliceHeader;
            this.previousSliceHeader = this.sliceHeader;
            this.sliceHeader = sliceHeaderData;
            this.sliceHeader.clear();
            this.bufferLength = 0;
            this.isFilling = true;
        }
    }

    public H264Reader(TrackOutput trackOutput, SeiReader seiReader, boolean z, boolean z2) {
        super(trackOutput);
        this.seiReader = seiReader;
        this.prefixFlags = new boolean[3];
        this.sampleReader = new SampleReader(trackOutput, z, z2);
        this.sps = new NalUnitTargetBuffer(NAL_UNIT_TYPE_SPS, TLRPC.USER_FLAG_UNUSED);
        this.pps = new NalUnitTargetBuffer(NAL_UNIT_TYPE_PPS, TLRPC.USER_FLAG_UNUSED);
        this.sei = new NalUnitTargetBuffer(NAL_UNIT_TYPE_SEI, TLRPC.USER_FLAG_UNUSED);
        this.seiWrapper = new ParsableByteArray();
    }

    private void endNalUnit(long j, int i, int i2, long j2) {
        if (!this.hasOutputFormat || this.sampleReader.needsSpsPps()) {
            this.sps.endNalUnit(i2);
            this.pps.endNalUnit(i2);
            if (this.hasOutputFormat) {
                if (this.sps.isCompleted()) {
                    this.sampleReader.putSps(NalUnitUtil.parseSpsNalUnit(unescape(this.sps)));
                    this.sps.reset();
                } else if (this.pps.isCompleted()) {
                    this.sampleReader.putPps(NalUnitUtil.parsePpsNalUnit(unescape(this.pps)));
                    this.pps.reset();
                }
            } else if (this.sps.isCompleted() && this.pps.isCompleted()) {
                List arrayList = new ArrayList();
                arrayList.add(Arrays.copyOf(this.sps.nalData, this.sps.nalLength));
                arrayList.add(Arrays.copyOf(this.pps.nalData, this.pps.nalLength));
                SpsData parseSpsNalUnit = NalUnitUtil.parseSpsNalUnit(unescape(this.sps));
                PpsData parsePpsNalUnit = NalUnitUtil.parsePpsNalUnit(unescape(this.pps));
                TrackOutput trackOutput = this.output;
                r17.format(MediaFormat.createVideoFormat(null, MimeTypes.VIDEO_H264, -1, -1, -1, parseSpsNalUnit.width, parseSpsNalUnit.height, arrayList, -1, parseSpsNalUnit.pixelWidthAspectRatio));
                this.hasOutputFormat = true;
                this.sampleReader.putSps(parseSpsNalUnit);
                this.sampleReader.putPps(parsePpsNalUnit);
                this.sps.reset();
                this.pps.reset();
            }
        }
        if (this.sei.endNalUnit(i2)) {
            this.seiWrapper.reset(this.sei.nalData, NalUnitUtil.unescapeStream(this.sei.nalData, this.sei.nalLength));
            this.seiWrapper.setPosition(4);
            this.seiReader.consume(j2, this.seiWrapper);
        }
        this.sampleReader.endNalUnit(j, i);
    }

    private void nalUnitData(byte[] bArr, int i, int i2) {
        if (!this.hasOutputFormat || this.sampleReader.needsSpsPps()) {
            this.sps.appendToNalUnit(bArr, i, i2);
            this.pps.appendToNalUnit(bArr, i, i2);
        }
        this.sei.appendToNalUnit(bArr, i, i2);
        this.sampleReader.appendToNalUnit(bArr, i, i2);
    }

    private void startNalUnit(long j, int i, long j2) {
        if (!this.hasOutputFormat || this.sampleReader.needsSpsPps()) {
            this.sps.startNalUnit(i);
            this.pps.startNalUnit(i);
        }
        this.sei.startNalUnit(i);
        this.sampleReader.startNalUnit(j, i, j2);
    }

    private static ParsableBitArray unescape(NalUnitTargetBuffer nalUnitTargetBuffer) {
        ParsableBitArray parsableBitArray = new ParsableBitArray(nalUnitTargetBuffer.nalData, NalUnitUtil.unescapeStream(nalUnitTargetBuffer.nalData, nalUnitTargetBuffer.nalLength));
        parsableBitArray.skipBits(32);
        return parsableBitArray;
    }

    public void consume(ParsableByteArray parsableByteArray) {
        if (parsableByteArray.bytesLeft() > 0) {
            int position = parsableByteArray.getPosition();
            int limit = parsableByteArray.limit();
            byte[] bArr = parsableByteArray.data;
            this.totalBytesWritten += (long) parsableByteArray.bytesLeft();
            this.output.sampleData(parsableByteArray, parsableByteArray.bytesLeft());
            while (true) {
                int findNalUnit = NalUnitUtil.findNalUnit(bArr, position, limit, this.prefixFlags);
                if (findNalUnit == limit) {
                    nalUnitData(bArr, position, limit);
                    return;
                }
                int nalUnitType = NalUnitUtil.getNalUnitType(bArr, findNalUnit);
                int i = findNalUnit - position;
                if (i > 0) {
                    nalUnitData(bArr, position, findNalUnit);
                }
                int i2 = limit - findNalUnit;
                long j = this.totalBytesWritten - ((long) i2);
                endNalUnit(j, i2, i < 0 ? -i : 0, this.pesTimeUs);
                startNalUnit(j, nalUnitType, this.pesTimeUs);
                position = findNalUnit + 3;
            }
        }
    }

    public void packetFinished() {
    }

    public void packetStarted(long j, boolean z) {
        this.pesTimeUs = j;
    }

    public void seek() {
        NalUnitUtil.clearPrefixFlags(this.prefixFlags);
        this.sps.reset();
        this.pps.reset();
        this.sei.reset();
        this.sampleReader.reset();
        this.totalBytesWritten = 0;
    }
}
