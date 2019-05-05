package com.hanista.mobogram.messenger.exoplayer.extractor.ts;

import android.util.Log;
import android.util.Pair;
import com.hanista.mobogram.messenger.exoplayer.MediaFormat;
import com.hanista.mobogram.messenger.exoplayer.extractor.TrackOutput;
import com.hanista.mobogram.messenger.exoplayer.util.CodecSpecificDataUtil;
import com.hanista.mobogram.messenger.exoplayer.util.MimeTypes;
import com.hanista.mobogram.messenger.exoplayer.util.NalUnitUtil;
import com.hanista.mobogram.messenger.exoplayer.util.ParsableBitArray;
import com.hanista.mobogram.messenger.exoplayer.util.ParsableByteArray;
import java.util.Arrays;
import java.util.Collections;

final class AdtsReader extends ElementaryStreamReader {
    private static final int CRC_SIZE = 2;
    private static final int HEADER_SIZE = 5;
    private static final int ID3_HEADER_SIZE = 10;
    private static final byte[] ID3_IDENTIFIER;
    private static final int ID3_SIZE_OFFSET = 6;
    private static final int MATCH_STATE_FF = 512;
    private static final int MATCH_STATE_I = 768;
    private static final int MATCH_STATE_ID = 1024;
    private static final int MATCH_STATE_START = 256;
    private static final int MATCH_STATE_VALUE_SHIFT = 8;
    private static final int STATE_FINDING_SAMPLE = 0;
    private static final int STATE_READING_ADTS_HEADER = 2;
    private static final int STATE_READING_ID3_HEADER = 1;
    private static final int STATE_READING_SAMPLE = 3;
    private static final String TAG = "AdtsReader";
    private final ParsableBitArray adtsScratch;
    private int bytesRead;
    private TrackOutput currentOutput;
    private long currentSampleDuration;
    private boolean hasCrc;
    private boolean hasOutputFormat;
    private final ParsableByteArray id3HeaderBuffer;
    private final TrackOutput id3Output;
    private int matchState;
    private long sampleDurationUs;
    private int sampleSize;
    private int state;
    private long timeUs;

    static {
        ID3_IDENTIFIER = new byte[]{(byte) 73, (byte) 68, (byte) 51};
    }

    public AdtsReader(TrackOutput trackOutput, TrackOutput trackOutput2) {
        super(trackOutput);
        this.id3Output = trackOutput2;
        trackOutput2.format(MediaFormat.createId3Format());
        this.adtsScratch = new ParsableBitArray(new byte[7]);
        this.id3HeaderBuffer = new ParsableByteArray(Arrays.copyOf(ID3_IDENTIFIER, ID3_HEADER_SIZE));
        setFindingSampleState();
    }

    private boolean continueRead(ParsableByteArray parsableByteArray, byte[] bArr, int i) {
        int min = Math.min(parsableByteArray.bytesLeft(), i - this.bytesRead);
        parsableByteArray.readBytes(bArr, this.bytesRead, min);
        this.bytesRead = min + this.bytesRead;
        return this.bytesRead == i;
    }

    private void findNextSample(ParsableByteArray parsableByteArray) {
        byte[] bArr = parsableByteArray.data;
        int position = parsableByteArray.getPosition();
        int limit = parsableByteArray.limit();
        while (position < limit) {
            int i = position + STATE_READING_ID3_HEADER;
            position = bArr[position] & NalUnitUtil.EXTENDED_SAR;
            if (this.matchState != MATCH_STATE_FF || position < PsExtractor.VIDEO_STREAM_MASK || position == NalUnitUtil.EXTENDED_SAR) {
                switch (position | this.matchState) {
                    case 329:
                        this.matchState = MATCH_STATE_I;
                        position = i;
                        break;
                    case 511:
                        this.matchState = MATCH_STATE_FF;
                        position = i;
                        break;
                    case 836:
                        this.matchState = MATCH_STATE_ID;
                        position = i;
                        break;
                    case 1075:
                        setReadingId3HeaderState();
                        parsableByteArray.setPosition(i);
                        return;
                    default:
                        if (this.matchState == MATCH_STATE_START) {
                            position = i;
                            break;
                        }
                        this.matchState = MATCH_STATE_START;
                        position = i - 1;
                        break;
                }
            }
            this.hasCrc = (position & STATE_READING_ID3_HEADER) == 0;
            setReadingAdtsHeaderState();
            parsableByteArray.setPosition(i);
            return;
        }
        parsableByteArray.setPosition(position);
    }

    private void parseAdtsHeader() {
        int i = STATE_READING_ADTS_HEADER;
        this.adtsScratch.setPosition(STATE_FINDING_SAMPLE);
        if (this.hasOutputFormat) {
            this.adtsScratch.skipBits(ID3_HEADER_SIZE);
        } else {
            int readBits = this.adtsScratch.readBits(STATE_READING_ADTS_HEADER) + STATE_READING_ID3_HEADER;
            if (readBits != STATE_READING_ADTS_HEADER) {
                Log.w(TAG, "Detected audio object type: " + readBits + ", but assuming AAC LC.");
            } else {
                i = readBits;
            }
            readBits = this.adtsScratch.readBits(4);
            this.adtsScratch.skipBits(STATE_READING_ID3_HEADER);
            Object buildAacAudioSpecificConfig = CodecSpecificDataUtil.buildAacAudioSpecificConfig(i, readBits, this.adtsScratch.readBits(STATE_READING_SAMPLE));
            Pair parseAacAudioSpecificConfig = CodecSpecificDataUtil.parseAacAudioSpecificConfig(buildAacAudioSpecificConfig);
            MediaFormat createAudioFormat = MediaFormat.createAudioFormat(null, MimeTypes.AUDIO_AAC, -1, -1, -1, ((Integer) parseAacAudioSpecificConfig.second).intValue(), ((Integer) parseAacAudioSpecificConfig.first).intValue(), Collections.singletonList(buildAacAudioSpecificConfig), null);
            this.sampleDurationUs = 1024000000 / ((long) createAudioFormat.sampleRate);
            this.output.format(createAudioFormat);
            this.hasOutputFormat = true;
        }
        this.adtsScratch.skipBits(4);
        int readBits2 = (this.adtsScratch.readBits(13) - 2) - 5;
        if (this.hasCrc) {
            readBits2 -= 2;
        }
        setReadingSampleState(this.output, this.sampleDurationUs, STATE_FINDING_SAMPLE, readBits2);
    }

    private void parseId3Header() {
        this.id3Output.sampleData(this.id3HeaderBuffer, ID3_HEADER_SIZE);
        this.id3HeaderBuffer.setPosition(ID3_SIZE_OFFSET);
        setReadingSampleState(this.id3Output, 0, ID3_HEADER_SIZE, this.id3HeaderBuffer.readSynchSafeInt() + ID3_HEADER_SIZE);
    }

    private void readSample(ParsableByteArray parsableByteArray) {
        int min = Math.min(parsableByteArray.bytesLeft(), this.sampleSize - this.bytesRead);
        this.currentOutput.sampleData(parsableByteArray, min);
        this.bytesRead = min + this.bytesRead;
        if (this.bytesRead == this.sampleSize) {
            this.currentOutput.sampleMetadata(this.timeUs, STATE_READING_ID3_HEADER, this.sampleSize, STATE_FINDING_SAMPLE, null);
            this.timeUs += this.currentSampleDuration;
            setFindingSampleState();
        }
    }

    private void setFindingSampleState() {
        this.state = STATE_FINDING_SAMPLE;
        this.bytesRead = STATE_FINDING_SAMPLE;
        this.matchState = MATCH_STATE_START;
    }

    private void setReadingAdtsHeaderState() {
        this.state = STATE_READING_ADTS_HEADER;
        this.bytesRead = STATE_FINDING_SAMPLE;
    }

    private void setReadingId3HeaderState() {
        this.state = STATE_READING_ID3_HEADER;
        this.bytesRead = ID3_IDENTIFIER.length;
        this.sampleSize = STATE_FINDING_SAMPLE;
        this.id3HeaderBuffer.setPosition(STATE_FINDING_SAMPLE);
    }

    private void setReadingSampleState(TrackOutput trackOutput, long j, int i, int i2) {
        this.state = STATE_READING_SAMPLE;
        this.bytesRead = i;
        this.currentOutput = trackOutput;
        this.currentSampleDuration = j;
        this.sampleSize = i2;
    }

    public void consume(ParsableByteArray parsableByteArray) {
        while (parsableByteArray.bytesLeft() > 0) {
            switch (this.state) {
                case STATE_FINDING_SAMPLE /*0*/:
                    findNextSample(parsableByteArray);
                    break;
                case STATE_READING_ID3_HEADER /*1*/:
                    if (!continueRead(parsableByteArray, this.id3HeaderBuffer.data, ID3_HEADER_SIZE)) {
                        break;
                    }
                    parseId3Header();
                    break;
                case STATE_READING_ADTS_HEADER /*2*/:
                    if (!continueRead(parsableByteArray, this.adtsScratch.data, this.hasCrc ? 7 : HEADER_SIZE)) {
                        break;
                    }
                    parseAdtsHeader();
                    break;
                case STATE_READING_SAMPLE /*3*/:
                    readSample(parsableByteArray);
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
        setFindingSampleState();
    }
}
