package com.hanista.mobogram.messenger.exoplayer.extractor.mp3;

import com.hanista.mobogram.messenger.exoplayer.C0700C;
import com.hanista.mobogram.messenger.exoplayer.MediaFormat;
import com.hanista.mobogram.messenger.exoplayer.extractor.Extractor;
import com.hanista.mobogram.messenger.exoplayer.extractor.ExtractorInput;
import com.hanista.mobogram.messenger.exoplayer.extractor.ExtractorOutput;
import com.hanista.mobogram.messenger.exoplayer.extractor.GaplessInfo;
import com.hanista.mobogram.messenger.exoplayer.extractor.PositionHolder;
import com.hanista.mobogram.messenger.exoplayer.extractor.SeekMap;
import com.hanista.mobogram.messenger.exoplayer.extractor.TrackOutput;
import com.hanista.mobogram.messenger.exoplayer.util.MpegAudioHeader;
import com.hanista.mobogram.messenger.exoplayer.util.ParsableByteArray;
import com.hanista.mobogram.messenger.exoplayer.util.Util;
import java.io.EOFException;

public final class Mp3Extractor implements Extractor {
    private static final int HEADER_MASK = -128000;
    private static final int INFO_HEADER;
    private static final int MAX_SNIFF_BYTES = 4096;
    private static final int MAX_SYNC_BYTES = 131072;
    private static final int VBRI_HEADER;
    private static final int XING_HEADER;
    private long basisTimeUs;
    private ExtractorOutput extractorOutput;
    private final long forcedFirstSampleTimestampUs;
    private GaplessInfo gaplessInfo;
    private int sampleBytesRemaining;
    private long samplesRead;
    private final ParsableByteArray scratch;
    private Seeker seeker;
    private final MpegAudioHeader synchronizedHeader;
    private int synchronizedHeaderData;
    private TrackOutput trackOutput;

    interface Seeker extends SeekMap {
        long getDurationUs();

        long getTimeUs(long j);
    }

    static {
        XING_HEADER = Util.getIntegerCodeForString("Xing");
        INFO_HEADER = Util.getIntegerCodeForString("Info");
        VBRI_HEADER = Util.getIntegerCodeForString("VBRI");
    }

    public Mp3Extractor() {
        this(-1);
    }

    public Mp3Extractor(long j) {
        this.forcedFirstSampleTimestampUs = j;
        this.scratch = new ParsableByteArray(4);
        this.synchronizedHeader = new MpegAudioHeader();
        this.basisTimeUs = -1;
    }

    private boolean maybeResynchronize(ExtractorInput extractorInput) {
        extractorInput.resetPeekPosition();
        if (!extractorInput.peekFully(this.scratch.data, INFO_HEADER, 4, true)) {
            return false;
        }
        this.scratch.setPosition(INFO_HEADER);
        int readInt = this.scratch.readInt();
        if ((readInt & HEADER_MASK) != (this.synchronizedHeaderData & HEADER_MASK) || MpegAudioHeader.getFrameSize(readInt) == -1) {
            this.synchronizedHeaderData = INFO_HEADER;
            extractorInput.skipFully(1);
            return synchronizeCatchingEndOfInput(extractorInput);
        }
        MpegAudioHeader.populateHeader(readInt, this.synchronizedHeader);
        return true;
    }

    private int readSample(ExtractorInput extractorInput) {
        if (this.sampleBytesRemaining == 0) {
            if (!maybeResynchronize(extractorInput)) {
                return -1;
            }
            if (this.basisTimeUs == -1) {
                this.basisTimeUs = this.seeker.getTimeUs(extractorInput.getPosition());
                if (this.forcedFirstSampleTimestampUs != -1) {
                    long timeUs = this.seeker.getTimeUs(0);
                    this.basisTimeUs = (this.forcedFirstSampleTimestampUs - timeUs) + this.basisTimeUs;
                }
            }
            this.sampleBytesRemaining = this.synchronizedHeader.frameSize;
        }
        int sampleData = this.trackOutput.sampleData(extractorInput, this.sampleBytesRemaining, true);
        if (sampleData == -1) {
            return -1;
        }
        this.sampleBytesRemaining -= sampleData;
        if (this.sampleBytesRemaining > 0) {
            return INFO_HEADER;
        }
        this.trackOutput.sampleMetadata(((this.samplesRead * C0700C.MICROS_PER_SECOND) / ((long) this.synchronizedHeader.sampleRate)) + this.basisTimeUs, 1, this.synchronizedHeader.frameSize, INFO_HEADER, null);
        this.samplesRead += (long) this.synchronizedHeader.samplesPerFrame;
        this.sampleBytesRemaining = INFO_HEADER;
        return INFO_HEADER;
    }

    private void setupSeeker(ExtractorInput extractorInput) {
        int i = 21;
        ParsableByteArray parsableByteArray = new ParsableByteArray(this.synchronizedHeader.frameSize);
        extractorInput.peekFully(parsableByteArray.data, INFO_HEADER, this.synchronizedHeader.frameSize);
        long position = extractorInput.getPosition();
        long length = extractorInput.getLength();
        if ((this.synchronizedHeader.version & 1) != 0) {
            if (this.synchronizedHeader.channels != 1) {
                i = 36;
            }
        } else if (this.synchronizedHeader.channels == 1) {
            i = 13;
        }
        parsableByteArray.setPosition(i);
        int readInt = parsableByteArray.readInt();
        if (readInt == XING_HEADER || readInt == INFO_HEADER) {
            this.seeker = XingSeeker.create(this.synchronizedHeader, parsableByteArray, position, length);
            if (this.seeker != null && this.gaplessInfo == null) {
                extractorInput.resetPeekPosition();
                extractorInput.advancePeekPosition(i + 141);
                extractorInput.peekFully(this.scratch.data, INFO_HEADER, 3);
                this.scratch.setPosition(INFO_HEADER);
                this.gaplessInfo = GaplessInfo.createFromXingHeaderValue(this.scratch.readUnsignedInt24());
            }
            extractorInput.skipFully(this.synchronizedHeader.frameSize);
        } else {
            parsableByteArray.setPosition(36);
            if (parsableByteArray.readInt() == VBRI_HEADER) {
                this.seeker = VbriSeeker.create(this.synchronizedHeader, parsableByteArray, position, length);
                extractorInput.skipFully(this.synchronizedHeader.frameSize);
            }
        }
        if (this.seeker == null) {
            extractorInput.resetPeekPosition();
            extractorInput.peekFully(this.scratch.data, INFO_HEADER, 4);
            this.scratch.setPosition(INFO_HEADER);
            MpegAudioHeader.populateHeader(this.scratch.readInt(), this.synchronizedHeader);
            this.seeker = new ConstantBitrateSeeker(extractorInput.getPosition(), this.synchronizedHeader.bitrate, length);
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean synchronize(com.hanista.mobogram.messenger.exoplayer.extractor.ExtractorInput r12, boolean r13) {
        /*
        r11 = this;
        r10 = 4;
        r9 = -128000; // 0xfffffffffffe0c00 float:NaN double:NaN;
        r5 = 1;
        r2 = 0;
        r12.resetPeekPosition();
        r0 = r12.getPosition();
        r6 = 0;
        r0 = (r0 > r6 ? 1 : (r0 == r6 ? 0 : -1));
        if (r0 != 0) goto L_0x009f;
    L_0x0013:
        r0 = com.hanista.mobogram.messenger.exoplayer.extractor.mp3.Id3Util.parseId3(r12);
        r11.gaplessInfo = r0;
        r0 = r12.getPeekPosition();
        r0 = (int) r0;
        if (r13 != 0) goto L_0x0023;
    L_0x0020:
        r12.skipFully(r0);
    L_0x0023:
        r6 = r0;
        r1 = r2;
        r3 = r2;
        r4 = r2;
    L_0x0027:
        if (r13 == 0) goto L_0x002e;
    L_0x0029:
        r0 = 4096; // 0x1000 float:5.74E-42 double:2.0237E-320;
        if (r4 != r0) goto L_0x002e;
    L_0x002d:
        return r2;
    L_0x002e:
        if (r13 != 0) goto L_0x003d;
    L_0x0030:
        r0 = 131072; // 0x20000 float:1.83671E-40 double:6.47582E-319;
        if (r4 != r0) goto L_0x003d;
    L_0x0034:
        r0 = new com.hanista.mobogram.messenger.exoplayer.ParserException;
        r1 = "Searched too many bytes.";
        r0.<init>(r1);
        throw r0;
    L_0x003d:
        r0 = r11.scratch;
        r0 = r0.data;
        r0 = r12.peekFully(r0, r2, r10, r5);
        if (r0 == 0) goto L_0x002d;
    L_0x0047:
        r0 = r11.scratch;
        r0.setPosition(r2);
        r0 = r11.scratch;
        r0 = r0.readInt();
        if (r1 == 0) goto L_0x005a;
    L_0x0054:
        r7 = r0 & r9;
        r8 = r1 & r9;
        if (r7 != r8) goto L_0x0061;
    L_0x005a:
        r7 = com.hanista.mobogram.messenger.exoplayer.util.MpegAudioHeader.getFrameSize(r0);
        r8 = -1;
        if (r7 != r8) goto L_0x007b;
    L_0x0061:
        r0 = r4 + 1;
        if (r13 == 0) goto L_0x0074;
    L_0x0065:
        r12.resetPeekPosition();
        r1 = r6 + r0;
        r12.advancePeekPosition(r1);
        r1 = r2;
        r3 = r0;
        r0 = r2;
    L_0x0070:
        r4 = r3;
        r3 = r1;
        r1 = r0;
        goto L_0x0027;
    L_0x0074:
        r12.skipFully(r5);
        r1 = r2;
        r3 = r0;
        r0 = r2;
        goto L_0x0070;
    L_0x007b:
        r3 = r3 + 1;
        if (r3 != r5) goto L_0x008c;
    L_0x007f:
        r1 = r11.synchronizedHeader;
        com.hanista.mobogram.messenger.exoplayer.util.MpegAudioHeader.populateHeader(r0, r1);
    L_0x0084:
        r1 = r7 + -4;
        r12.advancePeekPosition(r1);
        r1 = r3;
        r3 = r4;
        goto L_0x0070;
    L_0x008c:
        if (r3 != r10) goto L_0x009d;
    L_0x008e:
        if (r13 == 0) goto L_0x0099;
    L_0x0090:
        r0 = r6 + r4;
        r12.skipFully(r0);
    L_0x0095:
        r11.synchronizedHeaderData = r1;
        r2 = r5;
        goto L_0x002d;
    L_0x0099:
        r12.resetPeekPosition();
        goto L_0x0095;
    L_0x009d:
        r0 = r1;
        goto L_0x0084;
    L_0x009f:
        r6 = r2;
        r1 = r2;
        r3 = r2;
        r4 = r2;
        goto L_0x0027;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.hanista.mobogram.messenger.exoplayer.extractor.mp3.Mp3Extractor.synchronize(com.hanista.mobogram.messenger.exoplayer.extractor.ExtractorInput, boolean):boolean");
    }

    private boolean synchronizeCatchingEndOfInput(ExtractorInput extractorInput) {
        boolean z = false;
        try {
            z = synchronize(extractorInput, false);
        } catch (EOFException e) {
        }
        return z;
    }

    public void init(ExtractorOutput extractorOutput) {
        this.extractorOutput = extractorOutput;
        this.trackOutput = extractorOutput.track(INFO_HEADER);
        extractorOutput.endTracks();
    }

    public int read(ExtractorInput extractorInput, PositionHolder positionHolder) {
        if (this.synchronizedHeaderData == 0 && !synchronizeCatchingEndOfInput(extractorInput)) {
            return -1;
        }
        if (this.seeker == null) {
            setupSeeker(extractorInput);
            this.extractorOutput.seekMap(this.seeker);
            MediaFormat createAudioFormat = MediaFormat.createAudioFormat(null, this.synchronizedHeader.mimeType, -1, MAX_SNIFF_BYTES, this.seeker.getDurationUs(), this.synchronizedHeader.channels, this.synchronizedHeader.sampleRate, null, null);
            if (this.gaplessInfo != null) {
                createAudioFormat = createAudioFormat.copyWithGaplessInfo(this.gaplessInfo.encoderDelay, this.gaplessInfo.encoderPadding);
            }
            this.trackOutput.format(createAudioFormat);
        }
        return readSample(extractorInput);
    }

    public void release() {
    }

    public void seek() {
        this.synchronizedHeaderData = INFO_HEADER;
        this.samplesRead = 0;
        this.basisTimeUs = -1;
        this.sampleBytesRemaining = INFO_HEADER;
    }

    public boolean sniff(ExtractorInput extractorInput) {
        return synchronize(extractorInput, true);
    }
}
