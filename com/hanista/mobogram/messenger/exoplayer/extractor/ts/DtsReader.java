package com.hanista.mobogram.messenger.exoplayer.extractor.ts;

import com.hanista.mobogram.messenger.exoplayer.C0700C;
import com.hanista.mobogram.messenger.exoplayer.MediaFormat;
import com.hanista.mobogram.messenger.exoplayer.extractor.TrackOutput;
import com.hanista.mobogram.messenger.exoplayer.util.DtsUtil;
import com.hanista.mobogram.messenger.exoplayer.util.ParsableByteArray;

final class DtsReader extends ElementaryStreamReader {
    private static final int HEADER_SIZE = 15;
    private static final int STATE_FINDING_SYNC = 0;
    private static final int STATE_READING_HEADER = 1;
    private static final int STATE_READING_SAMPLE = 2;
    private static final int SYNC_VALUE = 2147385345;
    private static final int SYNC_VALUE_SIZE = 4;
    private int bytesRead;
    private final ParsableByteArray headerScratchBytes;
    private MediaFormat mediaFormat;
    private long sampleDurationUs;
    private int sampleSize;
    private int state;
    private int syncBytes;
    private long timeUs;

    public DtsReader(TrackOutput trackOutput) {
        super(trackOutput);
        this.headerScratchBytes = new ParsableByteArray(new byte[HEADER_SIZE]);
        this.headerScratchBytes.data[STATE_FINDING_SYNC] = Byte.MAX_VALUE;
        this.headerScratchBytes.data[STATE_READING_HEADER] = (byte) -2;
        this.headerScratchBytes.data[STATE_READING_SAMPLE] = Byte.MIN_VALUE;
        this.headerScratchBytes.data[3] = (byte) 1;
        this.state = STATE_FINDING_SYNC;
    }

    private boolean continueRead(ParsableByteArray parsableByteArray, byte[] bArr, int i) {
        int min = Math.min(parsableByteArray.bytesLeft(), i - this.bytesRead);
        parsableByteArray.readBytes(bArr, this.bytesRead, min);
        this.bytesRead = min + this.bytesRead;
        return this.bytesRead == i;
    }

    private void parseHeader() {
        byte[] bArr = this.headerScratchBytes.data;
        if (this.mediaFormat == null) {
            this.mediaFormat = DtsUtil.parseDtsFormat(bArr, null, -1, null);
            this.output.format(this.mediaFormat);
        }
        this.sampleSize = DtsUtil.getDtsFrameSize(bArr);
        this.sampleDurationUs = (long) ((int) ((((long) DtsUtil.parseDtsAudioSampleCount(bArr)) * C0700C.MICROS_PER_SECOND) / ((long) this.mediaFormat.sampleRate)));
    }

    private boolean skipToNextSync(ParsableByteArray parsableByteArray) {
        while (parsableByteArray.bytesLeft() > 0) {
            this.syncBytes <<= 8;
            this.syncBytes |= parsableByteArray.readUnsignedByte();
            if (this.syncBytes == SYNC_VALUE) {
                this.syncBytes = STATE_FINDING_SYNC;
                return true;
            }
        }
        return false;
    }

    public void consume(ParsableByteArray parsableByteArray) {
        while (parsableByteArray.bytesLeft() > 0) {
            switch (this.state) {
                case STATE_FINDING_SYNC /*0*/:
                    if (!skipToNextSync(parsableByteArray)) {
                        break;
                    }
                    this.bytesRead = SYNC_VALUE_SIZE;
                    this.state = STATE_READING_HEADER;
                    break;
                case STATE_READING_HEADER /*1*/:
                    if (!continueRead(parsableByteArray, this.headerScratchBytes.data, HEADER_SIZE)) {
                        break;
                    }
                    parseHeader();
                    this.headerScratchBytes.setPosition(STATE_FINDING_SYNC);
                    this.output.sampleData(this.headerScratchBytes, HEADER_SIZE);
                    this.state = STATE_READING_SAMPLE;
                    break;
                case STATE_READING_SAMPLE /*2*/:
                    int min = Math.min(parsableByteArray.bytesLeft(), this.sampleSize - this.bytesRead);
                    this.output.sampleData(parsableByteArray, min);
                    this.bytesRead = min + this.bytesRead;
                    if (this.bytesRead != this.sampleSize) {
                        break;
                    }
                    this.output.sampleMetadata(this.timeUs, STATE_READING_HEADER, this.sampleSize, STATE_FINDING_SYNC, null);
                    this.timeUs += this.sampleDurationUs;
                    this.state = STATE_FINDING_SYNC;
                    break;
                default:
                    break;
            }
        }
    }

    public void packetFinished() {
    }

    public void packetStarted(long j, boolean z) {
        this.timeUs = j;
    }

    public void seek() {
        this.state = STATE_FINDING_SYNC;
        this.bytesRead = STATE_FINDING_SYNC;
        this.syncBytes = STATE_FINDING_SYNC;
    }
}
