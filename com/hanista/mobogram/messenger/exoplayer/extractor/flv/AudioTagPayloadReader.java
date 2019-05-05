package com.hanista.mobogram.messenger.exoplayer.extractor.flv;

import android.util.Pair;
import com.hanista.mobogram.messenger.exoplayer.MediaFormat;
import com.hanista.mobogram.messenger.exoplayer.extractor.TrackOutput;
import com.hanista.mobogram.messenger.exoplayer.extractor.flv.TagPayloadReader.UnsupportedFormatException;
import com.hanista.mobogram.messenger.exoplayer.util.CodecSpecificDataUtil;
import com.hanista.mobogram.messenger.exoplayer.util.MimeTypes;
import com.hanista.mobogram.messenger.exoplayer.util.ParsableByteArray;
import java.util.Collections;

final class AudioTagPayloadReader extends TagPayloadReader {
    private static final int AAC_PACKET_TYPE_AAC_RAW = 1;
    private static final int AAC_PACKET_TYPE_SEQUENCE_HEADER = 0;
    private static final int AUDIO_FORMAT_AAC = 10;
    private static final int[] AUDIO_SAMPLING_RATE_TABLE;
    private boolean hasOutputFormat;
    private boolean hasParsedAudioDataHeader;

    static {
        AUDIO_SAMPLING_RATE_TABLE = new int[]{5500, 11000, 22000, 44000};
    }

    public AudioTagPayloadReader(TrackOutput trackOutput) {
        super(trackOutput);
    }

    protected boolean parseHeader(ParsableByteArray parsableByteArray) {
        if (this.hasParsedAudioDataHeader) {
            parsableByteArray.skipBytes(AAC_PACKET_TYPE_AAC_RAW);
        } else {
            int readUnsignedByte = parsableByteArray.readUnsignedByte();
            int i = (readUnsignedByte >> 4) & 15;
            readUnsignedByte = (readUnsignedByte >> 2) & 3;
            if (readUnsignedByte < 0 || readUnsignedByte >= AUDIO_SAMPLING_RATE_TABLE.length) {
                throw new UnsupportedFormatException("Invalid sample rate index: " + readUnsignedByte);
            } else if (i != AUDIO_FORMAT_AAC) {
                throw new UnsupportedFormatException("Audio format not supported: " + i);
            } else {
                this.hasParsedAudioDataHeader = true;
            }
        }
        return true;
    }

    protected void parsePayload(ParsableByteArray parsableByteArray, long j) {
        int readUnsignedByte = parsableByteArray.readUnsignedByte();
        if (readUnsignedByte == 0 && !this.hasOutputFormat) {
            Object obj = new byte[parsableByteArray.bytesLeft()];
            parsableByteArray.readBytes(obj, AAC_PACKET_TYPE_SEQUENCE_HEADER, obj.length);
            Pair parseAacAudioSpecificConfig = CodecSpecificDataUtil.parseAacAudioSpecificConfig(obj);
            this.output.format(MediaFormat.createAudioFormat(null, MimeTypes.AUDIO_AAC, -1, -1, getDurationUs(), ((Integer) parseAacAudioSpecificConfig.second).intValue(), ((Integer) parseAacAudioSpecificConfig.first).intValue(), Collections.singletonList(obj), null));
            this.hasOutputFormat = true;
        } else if (readUnsignedByte == AAC_PACKET_TYPE_AAC_RAW) {
            int bytesLeft = parsableByteArray.bytesLeft();
            this.output.sampleData(parsableByteArray, bytesLeft);
            this.output.sampleMetadata(j, AAC_PACKET_TYPE_AAC_RAW, bytesLeft, AAC_PACKET_TYPE_SEQUENCE_HEADER, null);
        }
    }

    public void seek() {
    }
}
