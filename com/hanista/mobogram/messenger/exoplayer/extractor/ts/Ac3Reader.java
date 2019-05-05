package com.hanista.mobogram.messenger.exoplayer.extractor.ts;

import com.hanista.mobogram.messenger.exoplayer.C0700C;
import com.hanista.mobogram.messenger.exoplayer.MediaFormat;
import com.hanista.mobogram.messenger.exoplayer.extractor.TrackOutput;
import com.hanista.mobogram.messenger.exoplayer.util.Ac3Util;
import com.hanista.mobogram.messenger.exoplayer.util.ParsableBitArray;
import com.hanista.mobogram.messenger.exoplayer.util.ParsableByteArray;

final class Ac3Reader extends ElementaryStreamReader {
    private static final int HEADER_SIZE = 8;
    private static final int STATE_FINDING_SYNC = 0;
    private static final int STATE_READING_HEADER = 1;
    private static final int STATE_READING_SAMPLE = 2;
    private int bytesRead;
    private final ParsableBitArray headerScratchBits;
    private final ParsableByteArray headerScratchBytes;
    private final boolean isEac3;
    private boolean lastByteWas0B;
    private MediaFormat mediaFormat;
    private long sampleDurationUs;
    private int sampleSize;
    private int state;
    private long timeUs;

    public Ac3Reader(TrackOutput trackOutput, boolean z) {
        super(trackOutput);
        this.isEac3 = z;
        this.headerScratchBits = new ParsableBitArray(new byte[HEADER_SIZE]);
        this.headerScratchBytes = new ParsableByteArray(this.headerScratchBits.data);
        this.state = STATE_FINDING_SYNC;
    }

    private boolean continueRead(ParsableByteArray parsableByteArray, byte[] bArr, int i) {
        int min = Math.min(parsableByteArray.bytesLeft(), i - this.bytesRead);
        parsableByteArray.readBytes(bArr, this.bytesRead, min);
        this.bytesRead = min + this.bytesRead;
        return this.bytesRead == i;
    }

    private void parseHeader() {
        if (this.mediaFormat == null) {
            this.mediaFormat = this.isEac3 ? Ac3Util.parseEac3SyncframeFormat(this.headerScratchBits, null, -1, null) : Ac3Util.parseAc3SyncframeFormat(this.headerScratchBits, null, -1, null);
            this.output.format(this.mediaFormat);
        }
        this.sampleSize = this.isEac3 ? Ac3Util.parseEAc3SyncframeSize(this.headerScratchBits.data) : Ac3Util.parseAc3SyncframeSize(this.headerScratchBits.data);
        this.sampleDurationUs = (long) ((int) ((((long) (this.isEac3 ? Ac3Util.parseEAc3SyncframeAudioSampleCount(this.headerScratchBits.data) : Ac3Util.getAc3SyncframeAudioSampleCount())) * C0700C.MICROS_PER_SECOND) / ((long) this.mediaFormat.sampleRate)));
    }

    private boolean skipToNextSync(ParsableByteArray parsableByteArray) {
        while (parsableByteArray.bytesLeft() > 0) {
            if (this.lastByteWas0B) {
                int readUnsignedByte = parsableByteArray.readUnsignedByte();
                if (readUnsignedByte == 119) {
                    this.lastByteWas0B = false;
                    return true;
                }
                this.lastByteWas0B = readUnsignedByte == 11;
            } else {
                this.lastByteWas0B = parsableByteArray.readUnsignedByte() == 11;
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
                    this.state = STATE_READING_HEADER;
                    this.headerScratchBytes.data[STATE_FINDING_SYNC] = (byte) 11;
                    this.headerScratchBytes.data[STATE_READING_HEADER] = (byte) 119;
                    this.bytesRead = STATE_READING_SAMPLE;
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
        this.lastByteWas0B = false;
    }
}
