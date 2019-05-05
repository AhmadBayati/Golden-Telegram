package com.hanista.mobogram.messenger.exoplayer.extractor.ts;

import android.util.Log;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import com.hanista.mobogram.messenger.exoplayer.extractor.DummyTrackOutput;
import com.hanista.mobogram.messenger.exoplayer.extractor.Extractor;
import com.hanista.mobogram.messenger.exoplayer.extractor.ExtractorInput;
import com.hanista.mobogram.messenger.exoplayer.extractor.ExtractorOutput;
import com.hanista.mobogram.messenger.exoplayer.extractor.PositionHolder;
import com.hanista.mobogram.messenger.exoplayer.extractor.SeekMap;
import com.hanista.mobogram.messenger.exoplayer.util.NalUnitUtil;
import com.hanista.mobogram.messenger.exoplayer.util.ParsableBitArray;
import com.hanista.mobogram.messenger.exoplayer.util.ParsableByteArray;
import com.hanista.mobogram.messenger.exoplayer.util.Util;

public final class TsExtractor implements Extractor {
    private static final long AC3_FORMAT_IDENTIFIER;
    private static final long E_AC3_FORMAT_IDENTIFIER;
    private static final long HEVC_FORMAT_IDENTIFIER;
    private static final String TAG = "TsExtractor";
    private static final int TS_PACKET_SIZE = 188;
    private static final int TS_PAT_PID = 0;
    private static final int TS_STREAM_TYPE_AAC = 15;
    private static final int TS_STREAM_TYPE_AC3 = 129;
    private static final int TS_STREAM_TYPE_DTS = 138;
    private static final int TS_STREAM_TYPE_EIA608 = 256;
    private static final int TS_STREAM_TYPE_E_AC3 = 135;
    private static final int TS_STREAM_TYPE_H262 = 2;
    private static final int TS_STREAM_TYPE_H264 = 27;
    private static final int TS_STREAM_TYPE_H265 = 36;
    private static final int TS_STREAM_TYPE_HDMV_DTS = 130;
    private static final int TS_STREAM_TYPE_ID3 = 21;
    private static final int TS_STREAM_TYPE_MPA = 3;
    private static final int TS_STREAM_TYPE_MPA_LSF = 4;
    private static final int TS_SYNC_BYTE = 71;
    public static final int WORKAROUND_ALLOW_NON_IDR_KEYFRAMES = 1;
    public static final int WORKAROUND_DETECT_ACCESS_UNITS = 8;
    public static final int WORKAROUND_IGNORE_AAC_STREAM = 2;
    public static final int WORKAROUND_IGNORE_H264_STREAM = 4;
    Id3Reader id3Reader;
    private ExtractorOutput output;
    private final PtsTimestampAdjuster ptsTimestampAdjuster;
    final SparseBooleanArray streamTypes;
    private final ParsableByteArray tsPacketBuffer;
    final SparseArray<TsPayloadReader> tsPayloadReaders;
    private final ParsableBitArray tsScratch;
    private final int workaroundFlags;

    private static abstract class TsPayloadReader {
        private TsPayloadReader() {
        }

        public abstract void consume(ParsableByteArray parsableByteArray, boolean z, ExtractorOutput extractorOutput);

        public abstract void seek();
    }

    private class PatReader extends TsPayloadReader {
        private final ParsableBitArray patScratch;

        public PatReader() {
            super();
            this.patScratch = new ParsableBitArray(new byte[TsExtractor.WORKAROUND_IGNORE_H264_STREAM]);
        }

        public void consume(ParsableByteArray parsableByteArray, boolean z, ExtractorOutput extractorOutput) {
            if (z) {
                parsableByteArray.skipBytes(parsableByteArray.readUnsignedByte());
            }
            parsableByteArray.readBytes(this.patScratch, (int) TsExtractor.TS_STREAM_TYPE_MPA);
            this.patScratch.skipBits(12);
            int readBits = this.patScratch.readBits(12);
            parsableByteArray.skipBytes(5);
            int i = (readBits - 9) / TsExtractor.WORKAROUND_IGNORE_H264_STREAM;
            for (readBits = TsExtractor.TS_PAT_PID; readBits < i; readBits += TsExtractor.WORKAROUND_ALLOW_NON_IDR_KEYFRAMES) {
                parsableByteArray.readBytes(this.patScratch, (int) TsExtractor.WORKAROUND_IGNORE_H264_STREAM);
                int readBits2 = this.patScratch.readBits(16);
                this.patScratch.skipBits(TsExtractor.TS_STREAM_TYPE_MPA);
                if (readBits2 == 0) {
                    this.patScratch.skipBits(13);
                } else {
                    TsExtractor.this.tsPayloadReaders.put(this.patScratch.readBits(13), new PmtReader());
                }
            }
        }

        public void seek() {
        }
    }

    private static final class PesReader extends TsPayloadReader {
        private static final int HEADER_SIZE = 9;
        private static final int MAX_HEADER_EXTENSION_SIZE = 10;
        private static final int PES_SCRATCH_SIZE = 10;
        private static final int STATE_FINDING_HEADER = 0;
        private static final int STATE_READING_BODY = 3;
        private static final int STATE_READING_HEADER = 1;
        private static final int STATE_READING_HEADER_EXTENSION = 2;
        private int bytesRead;
        private boolean dataAlignmentIndicator;
        private boolean dtsFlag;
        private int extendedHeaderLength;
        private int payloadSize;
        private final ElementaryStreamReader pesPayloadReader;
        private final ParsableBitArray pesScratch;
        private boolean ptsFlag;
        private final PtsTimestampAdjuster ptsTimestampAdjuster;
        private boolean seenFirstDts;
        private int state;
        private long timeUs;

        public PesReader(ElementaryStreamReader elementaryStreamReader, PtsTimestampAdjuster ptsTimestampAdjuster) {
            super();
            this.pesPayloadReader = elementaryStreamReader;
            this.ptsTimestampAdjuster = ptsTimestampAdjuster;
            this.pesScratch = new ParsableBitArray(new byte[PES_SCRATCH_SIZE]);
            this.state = STATE_FINDING_HEADER;
        }

        private boolean continueRead(ParsableByteArray parsableByteArray, byte[] bArr, int i) {
            int min = Math.min(parsableByteArray.bytesLeft(), i - this.bytesRead);
            if (min <= 0) {
                return true;
            }
            if (bArr == null) {
                parsableByteArray.skipBytes(min);
            } else {
                parsableByteArray.readBytes(bArr, this.bytesRead, min);
            }
            this.bytesRead = min + this.bytesRead;
            return this.bytesRead == i;
        }

        private boolean parseHeader() {
            this.pesScratch.setPosition(STATE_FINDING_HEADER);
            int readBits = this.pesScratch.readBits(24);
            if (readBits != STATE_READING_HEADER) {
                Log.w(TsExtractor.TAG, "Unexpected start code prefix: " + readBits);
                this.payloadSize = -1;
                return false;
            }
            this.pesScratch.skipBits(TsExtractor.WORKAROUND_DETECT_ACCESS_UNITS);
            int readBits2 = this.pesScratch.readBits(16);
            this.pesScratch.skipBits(5);
            this.dataAlignmentIndicator = this.pesScratch.readBit();
            this.pesScratch.skipBits(STATE_READING_HEADER_EXTENSION);
            this.ptsFlag = this.pesScratch.readBit();
            this.dtsFlag = this.pesScratch.readBit();
            this.pesScratch.skipBits(6);
            this.extendedHeaderLength = this.pesScratch.readBits(TsExtractor.WORKAROUND_DETECT_ACCESS_UNITS);
            if (readBits2 == 0) {
                this.payloadSize = -1;
            } else {
                this.payloadSize = ((readBits2 + 6) - 9) - this.extendedHeaderLength;
            }
            return true;
        }

        private void parseHeaderExtension() {
            this.pesScratch.setPosition(STATE_FINDING_HEADER);
            this.timeUs = -1;
            if (this.ptsFlag) {
                this.pesScratch.skipBits(TsExtractor.WORKAROUND_IGNORE_H264_STREAM);
                long readBits = ((long) this.pesScratch.readBits(STATE_READING_BODY)) << 30;
                this.pesScratch.skipBits(STATE_READING_HEADER);
                readBits |= (long) (this.pesScratch.readBits(TsExtractor.TS_STREAM_TYPE_AAC) << TsExtractor.TS_STREAM_TYPE_AAC);
                this.pesScratch.skipBits(STATE_READING_HEADER);
                readBits |= (long) this.pesScratch.readBits(TsExtractor.TS_STREAM_TYPE_AAC);
                this.pesScratch.skipBits(STATE_READING_HEADER);
                if (!this.seenFirstDts && this.dtsFlag) {
                    this.pesScratch.skipBits(TsExtractor.WORKAROUND_IGNORE_H264_STREAM);
                    long readBits2 = ((long) this.pesScratch.readBits(STATE_READING_BODY)) << 30;
                    this.pesScratch.skipBits(STATE_READING_HEADER);
                    readBits2 |= (long) (this.pesScratch.readBits(TsExtractor.TS_STREAM_TYPE_AAC) << TsExtractor.TS_STREAM_TYPE_AAC);
                    this.pesScratch.skipBits(STATE_READING_HEADER);
                    readBits2 |= (long) this.pesScratch.readBits(TsExtractor.TS_STREAM_TYPE_AAC);
                    this.pesScratch.skipBits(STATE_READING_HEADER);
                    this.ptsTimestampAdjuster.adjustTimestamp(readBits2);
                    this.seenFirstDts = true;
                }
                this.timeUs = this.ptsTimestampAdjuster.adjustTimestamp(readBits);
            }
        }

        private void setState(int i) {
            this.state = i;
            this.bytesRead = STATE_FINDING_HEADER;
        }

        public void consume(ParsableByteArray parsableByteArray, boolean z, ExtractorOutput extractorOutput) {
            if (z) {
                switch (this.state) {
                    case STATE_READING_HEADER_EXTENSION /*2*/:
                        Log.w(TsExtractor.TAG, "Unexpected start indicator reading extended header");
                        break;
                    case STATE_READING_BODY /*3*/:
                        if (this.payloadSize != -1) {
                            Log.w(TsExtractor.TAG, "Unexpected start indicator: expected " + this.payloadSize + " more bytes");
                        }
                        this.pesPayloadReader.packetFinished();
                        break;
                }
                setState(STATE_READING_HEADER);
            }
            while (parsableByteArray.bytesLeft() > 0) {
                switch (this.state) {
                    case STATE_FINDING_HEADER /*0*/:
                        parsableByteArray.skipBytes(parsableByteArray.bytesLeft());
                        break;
                    case STATE_READING_HEADER /*1*/:
                        if (!continueRead(parsableByteArray, this.pesScratch.data, HEADER_SIZE)) {
                            break;
                        }
                        setState(parseHeader() ? STATE_READING_HEADER_EXTENSION : STATE_FINDING_HEADER);
                        break;
                    case STATE_READING_HEADER_EXTENSION /*2*/:
                        if (continueRead(parsableByteArray, this.pesScratch.data, Math.min(PES_SCRATCH_SIZE, this.extendedHeaderLength)) && continueRead(parsableByteArray, null, this.extendedHeaderLength)) {
                            parseHeaderExtension();
                            this.pesPayloadReader.packetStarted(this.timeUs, this.dataAlignmentIndicator);
                            setState(STATE_READING_BODY);
                            break;
                        }
                    case STATE_READING_BODY /*3*/:
                        int bytesLeft = parsableByteArray.bytesLeft();
                        int i = this.payloadSize == -1 ? STATE_FINDING_HEADER : bytesLeft - this.payloadSize;
                        if (i > 0) {
                            bytesLeft -= i;
                            parsableByteArray.setLimit(parsableByteArray.getPosition() + bytesLeft);
                        }
                        this.pesPayloadReader.consume(parsableByteArray);
                        if (this.payloadSize == -1) {
                            break;
                        }
                        this.payloadSize -= bytesLeft;
                        if (this.payloadSize != 0) {
                            break;
                        }
                        this.pesPayloadReader.packetFinished();
                        setState(STATE_READING_HEADER);
                        break;
                    default:
                        break;
                }
            }
        }

        public void seek() {
            this.state = STATE_FINDING_HEADER;
            this.bytesRead = STATE_FINDING_HEADER;
            this.seenFirstDts = false;
            this.pesPayloadReader.seek();
        }
    }

    private class PmtReader extends TsPayloadReader {
        private final ParsableBitArray pmtScratch;
        private int sectionBytesRead;
        private final ParsableByteArray sectionData;
        private int sectionLength;

        public PmtReader() {
            super();
            this.pmtScratch = new ParsableBitArray(new byte[5]);
            this.sectionData = new ParsableByteArray();
        }

        private int readPrivateDataStreamType(ParsableByteArray parsableByteArray, int i) {
            int i2 = -1;
            int position = parsableByteArray.getPosition() + i;
            while (parsableByteArray.getPosition() < position) {
                int readUnsignedByte = parsableByteArray.readUnsignedByte();
                int readUnsignedByte2 = parsableByteArray.readUnsignedByte();
                if (readUnsignedByte == 5) {
                    long readUnsignedInt = parsableByteArray.readUnsignedInt();
                    if (readUnsignedInt == TsExtractor.AC3_FORMAT_IDENTIFIER) {
                        i2 = TsExtractor.TS_STREAM_TYPE_AC3;
                    } else if (readUnsignedInt == TsExtractor.E_AC3_FORMAT_IDENTIFIER) {
                        i2 = TsExtractor.TS_STREAM_TYPE_E_AC3;
                    } else if (readUnsignedInt == TsExtractor.HEVC_FORMAT_IDENTIFIER) {
                        i2 = TsExtractor.TS_STREAM_TYPE_H265;
                    }
                    parsableByteArray.setPosition(position);
                    return i2;
                }
                if (readUnsignedByte == 106) {
                    i2 = TsExtractor.TS_STREAM_TYPE_AC3;
                } else if (readUnsignedByte == 122) {
                    i2 = TsExtractor.TS_STREAM_TYPE_E_AC3;
                } else if (readUnsignedByte == 123) {
                    i2 = TsExtractor.TS_STREAM_TYPE_DTS;
                }
                parsableByteArray.skipBytes(readUnsignedByte2);
            }
            parsableByteArray.setPosition(position);
            return i2;
        }

        public void consume(ParsableByteArray parsableByteArray, boolean z, ExtractorOutput extractorOutput) {
            if (z) {
                parsableByteArray.skipBytes(parsableByteArray.readUnsignedByte());
                parsableByteArray.readBytes(this.pmtScratch, (int) TsExtractor.TS_STREAM_TYPE_MPA);
                this.pmtScratch.skipBits(12);
                this.sectionLength = this.pmtScratch.readBits(12);
                if (this.sectionData.capacity() < this.sectionLength) {
                    this.sectionData.reset(new byte[this.sectionLength], this.sectionLength);
                } else {
                    this.sectionData.reset();
                    this.sectionData.setLimit(this.sectionLength);
                }
            }
            int min = Math.min(parsableByteArray.bytesLeft(), this.sectionLength - this.sectionBytesRead);
            parsableByteArray.readBytes(this.sectionData.data, this.sectionBytesRead, min);
            this.sectionBytesRead = min + this.sectionBytesRead;
            if (this.sectionBytesRead >= this.sectionLength) {
                this.sectionData.skipBytes(7);
                this.sectionData.readBytes(this.pmtScratch, (int) TsExtractor.WORKAROUND_IGNORE_AAC_STREAM);
                this.pmtScratch.skipBits(TsExtractor.WORKAROUND_IGNORE_H264_STREAM);
                min = this.pmtScratch.readBits(12);
                this.sectionData.skipBytes(min);
                if (TsExtractor.this.id3Reader == null) {
                    TsExtractor.this.id3Reader = new Id3Reader(extractorOutput.track(TsExtractor.TS_STREAM_TYPE_ID3));
                }
                int i = ((this.sectionLength - 9) - min) - 4;
                while (i > 0) {
                    this.sectionData.readBytes(this.pmtScratch, 5);
                    min = this.pmtScratch.readBits(TsExtractor.WORKAROUND_DETECT_ACCESS_UNITS);
                    this.pmtScratch.skipBits(TsExtractor.TS_STREAM_TYPE_MPA);
                    int readBits = this.pmtScratch.readBits(13);
                    this.pmtScratch.skipBits(TsExtractor.WORKAROUND_IGNORE_H264_STREAM);
                    int readBits2 = this.pmtScratch.readBits(12);
                    if (min == 6) {
                        min = readPrivateDataStreamType(this.sectionData, readBits2);
                    } else {
                        this.sectionData.skipBytes(readBits2);
                    }
                    int i2 = i - (readBits2 + 5);
                    if (TsExtractor.this.streamTypes.get(min)) {
                        i = i2;
                    } else {
                        ElementaryStreamReader h262Reader;
                        switch (min) {
                            case TsExtractor.WORKAROUND_IGNORE_AAC_STREAM /*2*/:
                                h262Reader = new H262Reader(extractorOutput.track(TsExtractor.WORKAROUND_IGNORE_AAC_STREAM));
                                break;
                            case TsExtractor.TS_STREAM_TYPE_MPA /*3*/:
                                h262Reader = new MpegAudioReader(extractorOutput.track(TsExtractor.TS_STREAM_TYPE_MPA));
                                break;
                            case TsExtractor.WORKAROUND_IGNORE_H264_STREAM /*4*/:
                                h262Reader = new MpegAudioReader(extractorOutput.track(TsExtractor.WORKAROUND_IGNORE_H264_STREAM));
                                break;
                            case TsExtractor.TS_STREAM_TYPE_AAC /*15*/:
                                if ((TsExtractor.this.workaroundFlags & TsExtractor.WORKAROUND_IGNORE_AAC_STREAM) == 0) {
                                    h262Reader = new AdtsReader(extractorOutput.track(TsExtractor.TS_STREAM_TYPE_AAC), new DummyTrackOutput());
                                    break;
                                } else {
                                    h262Reader = null;
                                    break;
                                }
                            case TsExtractor.TS_STREAM_TYPE_ID3 /*21*/:
                                h262Reader = TsExtractor.this.id3Reader;
                                break;
                            case TsExtractor.TS_STREAM_TYPE_H264 /*27*/:
                                if ((TsExtractor.this.workaroundFlags & TsExtractor.WORKAROUND_IGNORE_H264_STREAM) == 0) {
                                    h262Reader = new H264Reader(extractorOutput.track(TsExtractor.TS_STREAM_TYPE_H264), new SeiReader(extractorOutput.track(TsExtractor.TS_STREAM_TYPE_EIA608)), (TsExtractor.this.workaroundFlags & TsExtractor.WORKAROUND_ALLOW_NON_IDR_KEYFRAMES) != 0, (TsExtractor.this.workaroundFlags & TsExtractor.WORKAROUND_DETECT_ACCESS_UNITS) != 0);
                                    break;
                                } else {
                                    h262Reader = null;
                                    break;
                                }
                            case TsExtractor.TS_STREAM_TYPE_H265 /*36*/:
                                h262Reader = new H265Reader(extractorOutput.track(TsExtractor.TS_STREAM_TYPE_H265), new SeiReader(extractorOutput.track(TsExtractor.TS_STREAM_TYPE_EIA608)));
                                break;
                            case TsExtractor.TS_STREAM_TYPE_AC3 /*129*/:
                                h262Reader = new Ac3Reader(extractorOutput.track(TsExtractor.TS_STREAM_TYPE_AC3), false);
                                break;
                            case TsExtractor.TS_STREAM_TYPE_HDMV_DTS /*130*/:
                            case TsExtractor.TS_STREAM_TYPE_DTS /*138*/:
                                h262Reader = new DtsReader(extractorOutput.track(TsExtractor.TS_STREAM_TYPE_DTS));
                                break;
                            case TsExtractor.TS_STREAM_TYPE_E_AC3 /*135*/:
                                h262Reader = new Ac3Reader(extractorOutput.track(TsExtractor.TS_STREAM_TYPE_E_AC3), true);
                                break;
                            default:
                                h262Reader = null;
                                break;
                        }
                        if (h262Reader != null) {
                            TsExtractor.this.streamTypes.put(min, true);
                            TsExtractor.this.tsPayloadReaders.put(readBits, new PesReader(h262Reader, TsExtractor.this.ptsTimestampAdjuster));
                        }
                        i = i2;
                    }
                }
                extractorOutput.endTracks();
            }
        }

        public void seek() {
        }
    }

    static {
        AC3_FORMAT_IDENTIFIER = (long) Util.getIntegerCodeForString("AC-3");
        E_AC3_FORMAT_IDENTIFIER = (long) Util.getIntegerCodeForString("EAC3");
        HEVC_FORMAT_IDENTIFIER = (long) Util.getIntegerCodeForString("HEVC");
    }

    public TsExtractor() {
        this(new PtsTimestampAdjuster(HEVC_FORMAT_IDENTIFIER));
    }

    public TsExtractor(PtsTimestampAdjuster ptsTimestampAdjuster) {
        this(ptsTimestampAdjuster, TS_PAT_PID);
    }

    public TsExtractor(PtsTimestampAdjuster ptsTimestampAdjuster, int i) {
        this.ptsTimestampAdjuster = ptsTimestampAdjuster;
        this.workaroundFlags = i;
        this.tsPacketBuffer = new ParsableByteArray((int) TS_PACKET_SIZE);
        this.tsScratch = new ParsableBitArray(new byte[TS_STREAM_TYPE_MPA]);
        this.tsPayloadReaders = new SparseArray();
        this.tsPayloadReaders.put(TS_PAT_PID, new PatReader());
        this.streamTypes = new SparseBooleanArray();
    }

    public void init(ExtractorOutput extractorOutput) {
        this.output = extractorOutput;
        extractorOutput.seekMap(SeekMap.UNSEEKABLE);
    }

    public int read(ExtractorInput extractorInput, PositionHolder positionHolder) {
        if (!extractorInput.readFully(this.tsPacketBuffer.data, TS_PAT_PID, TS_PACKET_SIZE, true)) {
            return -1;
        }
        this.tsPacketBuffer.setPosition(TS_PAT_PID);
        this.tsPacketBuffer.setLimit(TS_PACKET_SIZE);
        if (this.tsPacketBuffer.readUnsignedByte() != TS_SYNC_BYTE) {
            return TS_PAT_PID;
        }
        this.tsPacketBuffer.readBytes(this.tsScratch, (int) TS_STREAM_TYPE_MPA);
        this.tsScratch.skipBits(WORKAROUND_ALLOW_NON_IDR_KEYFRAMES);
        boolean readBit = this.tsScratch.readBit();
        this.tsScratch.skipBits(WORKAROUND_ALLOW_NON_IDR_KEYFRAMES);
        int readBits = this.tsScratch.readBits(13);
        this.tsScratch.skipBits(WORKAROUND_IGNORE_AAC_STREAM);
        boolean readBit2 = this.tsScratch.readBit();
        boolean readBit3 = this.tsScratch.readBit();
        if (readBit2) {
            this.tsPacketBuffer.skipBytes(this.tsPacketBuffer.readUnsignedByte());
        }
        if (readBit3) {
            TsPayloadReader tsPayloadReader = (TsPayloadReader) this.tsPayloadReaders.get(readBits);
            if (tsPayloadReader != null) {
                tsPayloadReader.consume(this.tsPacketBuffer, readBit, this.output);
            }
        }
        return TS_PAT_PID;
    }

    public void release() {
    }

    public void seek() {
        this.ptsTimestampAdjuster.reset();
        for (int i = TS_PAT_PID; i < this.tsPayloadReaders.size(); i += WORKAROUND_ALLOW_NON_IDR_KEYFRAMES) {
            ((TsPayloadReader) this.tsPayloadReaders.valueAt(i)).seek();
        }
    }

    public boolean sniff(ExtractorInput extractorInput) {
        byte[] bArr = new byte[WORKAROUND_ALLOW_NON_IDR_KEYFRAMES];
        for (int i = TS_PAT_PID; i < 5; i += WORKAROUND_ALLOW_NON_IDR_KEYFRAMES) {
            extractorInput.peekFully(bArr, TS_PAT_PID, WORKAROUND_ALLOW_NON_IDR_KEYFRAMES);
            if ((bArr[TS_PAT_PID] & NalUnitUtil.EXTENDED_SAR) != TS_SYNC_BYTE) {
                return false;
            }
            extractorInput.advancePeekPosition(187);
        }
        return true;
    }
}
