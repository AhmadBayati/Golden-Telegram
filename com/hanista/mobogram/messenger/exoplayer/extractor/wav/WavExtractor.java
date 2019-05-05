package com.hanista.mobogram.messenger.exoplayer.extractor.wav;

import com.hanista.mobogram.messenger.exoplayer.MediaFormat;
import com.hanista.mobogram.messenger.exoplayer.ParserException;
import com.hanista.mobogram.messenger.exoplayer.extractor.Extractor;
import com.hanista.mobogram.messenger.exoplayer.extractor.ExtractorInput;
import com.hanista.mobogram.messenger.exoplayer.extractor.ExtractorOutput;
import com.hanista.mobogram.messenger.exoplayer.extractor.PositionHolder;
import com.hanista.mobogram.messenger.exoplayer.extractor.SeekMap;
import com.hanista.mobogram.messenger.exoplayer.extractor.TrackOutput;
import com.hanista.mobogram.messenger.exoplayer.util.MimeTypes;

public final class WavExtractor implements Extractor, SeekMap {
    private static final int MAX_INPUT_SIZE = 32768;
    private int bytesPerFrame;
    private ExtractorOutput extractorOutput;
    private int pendingBytes;
    private TrackOutput trackOutput;
    private WavHeader wavHeader;

    public long getPosition(long j) {
        return this.wavHeader.getPosition(j);
    }

    public void init(ExtractorOutput extractorOutput) {
        this.extractorOutput = extractorOutput;
        this.trackOutput = extractorOutput.track(0);
        this.wavHeader = null;
        extractorOutput.endTracks();
    }

    public boolean isSeekable() {
        return true;
    }

    public int read(ExtractorInput extractorInput, PositionHolder positionHolder) {
        if (this.wavHeader == null) {
            this.wavHeader = WavHeaderReader.peek(extractorInput);
            if (this.wavHeader == null) {
                throw new ParserException("Error initializing WavHeader. Did you sniff first?");
            }
            this.bytesPerFrame = this.wavHeader.getBytesPerFrame();
        }
        if (!this.wavHeader.hasDataBounds()) {
            WavHeaderReader.skipToData(extractorInput, this.wavHeader);
            this.trackOutput.format(MediaFormat.createAudioFormat(null, MimeTypes.AUDIO_RAW, this.wavHeader.getBitrate(), MAX_INPUT_SIZE, this.wavHeader.getDurationUs(), this.wavHeader.getNumChannels(), this.wavHeader.getSampleRateHz(), null, null, this.wavHeader.getEncoding()));
            this.extractorOutput.seekMap(this);
        }
        int sampleData = this.trackOutput.sampleData(extractorInput, MAX_INPUT_SIZE - this.pendingBytes, true);
        if (sampleData != -1) {
            this.pendingBytes += sampleData;
        }
        int i = (this.pendingBytes / this.bytesPerFrame) * this.bytesPerFrame;
        if (i > 0) {
            long position = extractorInput.getPosition() - ((long) this.pendingBytes);
            this.pendingBytes -= i;
            this.trackOutput.sampleMetadata(this.wavHeader.getTimeUs(position), 1, i, this.pendingBytes, null);
        }
        return sampleData == -1 ? -1 : 0;
    }

    public void release() {
    }

    public void seek() {
        this.pendingBytes = 0;
    }

    public boolean sniff(ExtractorInput extractorInput) {
        return WavHeaderReader.peek(extractorInput) != null;
    }
}
