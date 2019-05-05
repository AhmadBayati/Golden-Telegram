package com.hanista.mobogram.messenger.exoplayer.extractor.ts;

import com.hanista.mobogram.messenger.exoplayer.C0700C;
import com.hanista.mobogram.messenger.exoplayer.MediaFormat;
import com.hanista.mobogram.messenger.exoplayer.extractor.TrackOutput;
import com.hanista.mobogram.messenger.exoplayer.util.MpegAudioHeader;
import com.hanista.mobogram.messenger.exoplayer.util.NalUnitUtil;
import com.hanista.mobogram.messenger.exoplayer.util.ParsableByteArray;
import com.hanista.mobogram.messenger.support.widget.RecyclerView.ItemAnimator;

final class MpegAudioReader extends ElementaryStreamReader {
    private static final int HEADER_SIZE = 4;
    private static final int STATE_FINDING_HEADER = 0;
    private static final int STATE_READING_FRAME = 2;
    private static final int STATE_READING_HEADER = 1;
    private int frameBytesRead;
    private long frameDurationUs;
    private int frameSize;
    private boolean hasOutputFormat;
    private final MpegAudioHeader header;
    private final ParsableByteArray headerScratch;
    private boolean lastByteWasFF;
    private int state;
    private long timeUs;

    public MpegAudioReader(TrackOutput trackOutput) {
        super(trackOutput);
        this.state = STATE_FINDING_HEADER;
        this.headerScratch = new ParsableByteArray((int) HEADER_SIZE);
        this.headerScratch.data[STATE_FINDING_HEADER] = (byte) -1;
        this.header = new MpegAudioHeader();
    }

    private void findHeader(ParsableByteArray parsableByteArray) {
        byte[] bArr = parsableByteArray.data;
        int position = parsableByteArray.getPosition();
        int limit = parsableByteArray.limit();
        int i = position;
        while (i < limit) {
            boolean z = (bArr[i] & NalUnitUtil.EXTENDED_SAR) == NalUnitUtil.EXTENDED_SAR;
            if (this.lastByteWasFF && (bArr[i] & PsExtractor.VIDEO_STREAM) == PsExtractor.VIDEO_STREAM) {
                int i2 = STATE_READING_HEADER;
            } else {
                boolean z2 = false;
            }
            this.lastByteWasFF = z;
            if (i2 != 0) {
                parsableByteArray.setPosition(i + STATE_READING_HEADER);
                this.lastByteWasFF = false;
                this.headerScratch.data[STATE_READING_HEADER] = bArr[i];
                this.frameBytesRead = STATE_READING_FRAME;
                this.state = STATE_READING_HEADER;
                return;
            }
            i += STATE_READING_HEADER;
        }
        parsableByteArray.setPosition(limit);
    }

    private void readFrameRemainder(ParsableByteArray parsableByteArray) {
        int min = Math.min(parsableByteArray.bytesLeft(), this.frameSize - this.frameBytesRead);
        this.output.sampleData(parsableByteArray, min);
        this.frameBytesRead = min + this.frameBytesRead;
        if (this.frameBytesRead >= this.frameSize) {
            this.output.sampleMetadata(this.timeUs, STATE_READING_HEADER, this.frameSize, STATE_FINDING_HEADER, null);
            this.timeUs += this.frameDurationUs;
            this.frameBytesRead = STATE_FINDING_HEADER;
            this.state = STATE_FINDING_HEADER;
        }
    }

    private void readHeaderRemainder(ParsableByteArray parsableByteArray) {
        int min = Math.min(parsableByteArray.bytesLeft(), 4 - this.frameBytesRead);
        parsableByteArray.readBytes(this.headerScratch.data, this.frameBytesRead, min);
        this.frameBytesRead = min + this.frameBytesRead;
        if (this.frameBytesRead >= HEADER_SIZE) {
            this.headerScratch.setPosition(STATE_FINDING_HEADER);
            if (MpegAudioHeader.populateHeader(this.headerScratch.readInt(), this.header)) {
                this.frameSize = this.header.frameSize;
                if (!this.hasOutputFormat) {
                    this.frameDurationUs = (C0700C.MICROS_PER_SECOND * ((long) this.header.samplesPerFrame)) / ((long) this.header.sampleRate);
                    this.output.format(MediaFormat.createAudioFormat(null, this.header.mimeType, -1, ItemAnimator.FLAG_APPEARED_IN_PRE_LAYOUT, -1, this.header.channels, this.header.sampleRate, null, null));
                    this.hasOutputFormat = true;
                }
                this.headerScratch.setPosition(STATE_FINDING_HEADER);
                this.output.sampleData(this.headerScratch, HEADER_SIZE);
                this.state = STATE_READING_FRAME;
                return;
            }
            this.frameBytesRead = STATE_FINDING_HEADER;
            this.state = STATE_READING_HEADER;
        }
    }

    public void consume(ParsableByteArray parsableByteArray) {
        while (parsableByteArray.bytesLeft() > 0) {
            switch (this.state) {
                case STATE_FINDING_HEADER /*0*/:
                    findHeader(parsableByteArray);
                    break;
                case STATE_READING_HEADER /*1*/:
                    readHeaderRemainder(parsableByteArray);
                    break;
                case STATE_READING_FRAME /*2*/:
                    readFrameRemainder(parsableByteArray);
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
        this.state = STATE_FINDING_HEADER;
        this.frameBytesRead = STATE_FINDING_HEADER;
        this.lastByteWasFF = false;
    }
}
