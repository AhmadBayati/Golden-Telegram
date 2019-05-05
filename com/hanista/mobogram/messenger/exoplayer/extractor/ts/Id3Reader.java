package com.hanista.mobogram.messenger.exoplayer.extractor.ts;

import com.hanista.mobogram.messenger.exoplayer.MediaFormat;
import com.hanista.mobogram.messenger.exoplayer.extractor.TrackOutput;
import com.hanista.mobogram.messenger.exoplayer.util.ParsableByteArray;

final class Id3Reader extends ElementaryStreamReader {
    private static final int ID3_HEADER_SIZE = 10;
    private final ParsableByteArray id3Header;
    private int sampleBytesRead;
    private int sampleSize;
    private long sampleTimeUs;
    private boolean writingSample;

    public Id3Reader(TrackOutput trackOutput) {
        super(trackOutput);
        trackOutput.format(MediaFormat.createId3Format());
        this.id3Header = new ParsableByteArray((int) ID3_HEADER_SIZE);
    }

    public void consume(ParsableByteArray parsableByteArray) {
        if (this.writingSample) {
            int bytesLeft = parsableByteArray.bytesLeft();
            if (this.sampleBytesRead < ID3_HEADER_SIZE) {
                int min = Math.min(bytesLeft, 10 - this.sampleBytesRead);
                System.arraycopy(parsableByteArray.data, parsableByteArray.getPosition(), this.id3Header.data, this.sampleBytesRead, min);
                if (min + this.sampleBytesRead == ID3_HEADER_SIZE) {
                    this.id3Header.setPosition(6);
                    this.sampleSize = this.id3Header.readSynchSafeInt() + ID3_HEADER_SIZE;
                }
            }
            bytesLeft = Math.min(bytesLeft, this.sampleSize - this.sampleBytesRead);
            this.output.sampleData(parsableByteArray, bytesLeft);
            this.sampleBytesRead = bytesLeft + this.sampleBytesRead;
        }
    }

    public void packetFinished() {
        if (this.writingSample && this.sampleSize != 0 && this.sampleBytesRead == this.sampleSize) {
            this.output.sampleMetadata(this.sampleTimeUs, 1, this.sampleSize, 0, null);
            this.writingSample = false;
        }
    }

    public void packetStarted(long j, boolean z) {
        if (z) {
            this.writingSample = true;
            this.sampleTimeUs = j;
            this.sampleSize = 0;
            this.sampleBytesRead = 0;
        }
    }

    public void seek() {
        this.writingSample = false;
    }
}
