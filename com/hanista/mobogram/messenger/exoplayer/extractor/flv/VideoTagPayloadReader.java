package com.hanista.mobogram.messenger.exoplayer.extractor.flv;

import com.hanista.mobogram.messenger.exoplayer.MediaFormat;
import com.hanista.mobogram.messenger.exoplayer.extractor.TrackOutput;
import com.hanista.mobogram.messenger.exoplayer.extractor.flv.TagPayloadReader.UnsupportedFormatException;
import com.hanista.mobogram.messenger.exoplayer.util.Assertions;
import com.hanista.mobogram.messenger.exoplayer.util.MimeTypes;
import com.hanista.mobogram.messenger.exoplayer.util.NalUnitUtil;
import com.hanista.mobogram.messenger.exoplayer.util.NalUnitUtil.SpsData;
import com.hanista.mobogram.messenger.exoplayer.util.ParsableBitArray;
import com.hanista.mobogram.messenger.exoplayer.util.ParsableByteArray;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;
import java.util.ArrayList;
import java.util.List;

final class VideoTagPayloadReader extends TagPayloadReader {
    private static final int AVC_PACKET_TYPE_AVC_NALU = 1;
    private static final int AVC_PACKET_TYPE_SEQUENCE_HEADER = 0;
    private static final int VIDEO_CODEC_AVC = 7;
    private static final int VIDEO_FRAME_KEYFRAME = 1;
    private static final int VIDEO_FRAME_VIDEO_INFO = 5;
    private int frameType;
    private boolean hasOutputFormat;
    private final ParsableByteArray nalLength;
    private final ParsableByteArray nalStartCode;
    private int nalUnitLengthFieldLength;

    private static final class AvcSequenceHeaderData {
        public final int height;
        public final List<byte[]> initializationData;
        public final int nalUnitLengthFieldLength;
        public final float pixelWidthAspectRatio;
        public final int width;

        public AvcSequenceHeaderData(List<byte[]> list, int i, int i2, int i3, float f) {
            this.initializationData = list;
            this.nalUnitLengthFieldLength = i;
            this.pixelWidthAspectRatio = f;
            this.width = i2;
            this.height = i3;
        }
    }

    public VideoTagPayloadReader(TrackOutput trackOutput) {
        super(trackOutput);
        this.nalStartCode = new ParsableByteArray(NalUnitUtil.NAL_START_CODE);
        this.nalLength = new ParsableByteArray(4);
    }

    private AvcSequenceHeaderData parseAvcCodecPrivate(ParsableByteArray parsableByteArray) {
        int i;
        int i2;
        int i3 = -1;
        parsableByteArray.setPosition(4);
        int readUnsignedByte = (parsableByteArray.readUnsignedByte() & 3) + VIDEO_FRAME_KEYFRAME;
        Assertions.checkState(readUnsignedByte != 3);
        List arrayList = new ArrayList();
        int readUnsignedByte2 = parsableByteArray.readUnsignedByte() & 31;
        for (i = AVC_PACKET_TYPE_SEQUENCE_HEADER; i < readUnsignedByte2; i += VIDEO_FRAME_KEYFRAME) {
            arrayList.add(NalUnitUtil.parseChildNalUnit(parsableByteArray));
        }
        int readUnsignedByte3 = parsableByteArray.readUnsignedByte();
        for (i = AVC_PACKET_TYPE_SEQUENCE_HEADER; i < readUnsignedByte3; i += VIDEO_FRAME_KEYFRAME) {
            arrayList.add(NalUnitUtil.parseChildNalUnit(parsableByteArray));
        }
        float f = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
        if (readUnsignedByte2 > 0) {
            ParsableBitArray parsableBitArray = new ParsableBitArray((byte[]) arrayList.get(AVC_PACKET_TYPE_SEQUENCE_HEADER));
            parsableBitArray.setPosition((readUnsignedByte + VIDEO_FRAME_KEYFRAME) * 8);
            SpsData parseSpsNalUnit = NalUnitUtil.parseSpsNalUnit(parsableBitArray);
            i2 = parseSpsNalUnit.width;
            i3 = parseSpsNalUnit.height;
            f = parseSpsNalUnit.pixelWidthAspectRatio;
        } else {
            i2 = -1;
        }
        return new AvcSequenceHeaderData(arrayList, readUnsignedByte, i2, i3, f);
    }

    protected boolean parseHeader(ParsableByteArray parsableByteArray) {
        int readUnsignedByte = parsableByteArray.readUnsignedByte();
        int i = (readUnsignedByte >> 4) & 15;
        readUnsignedByte &= 15;
        if (readUnsignedByte != VIDEO_CODEC_AVC) {
            throw new UnsupportedFormatException("Video format not supported: " + readUnsignedByte);
        }
        this.frameType = i;
        return i != VIDEO_FRAME_VIDEO_INFO;
    }

    protected void parsePayload(ParsableByteArray parsableByteArray, long j) {
        int readUnsignedByte = parsableByteArray.readUnsignedByte();
        long readUnsignedInt24 = j + (((long) parsableByteArray.readUnsignedInt24()) * 1000);
        if (readUnsignedByte == 0 && !this.hasOutputFormat) {
            ParsableByteArray parsableByteArray2 = new ParsableByteArray(new byte[parsableByteArray.bytesLeft()]);
            parsableByteArray.readBytes(parsableByteArray2.data, AVC_PACKET_TYPE_SEQUENCE_HEADER, parsableByteArray.bytesLeft());
            AvcSequenceHeaderData parseAvcCodecPrivate = parseAvcCodecPrivate(parsableByteArray2);
            this.nalUnitLengthFieldLength = parseAvcCodecPrivate.nalUnitLengthFieldLength;
            this.output.format(MediaFormat.createVideoFormat(null, MimeTypes.VIDEO_H264, -1, -1, getDurationUs(), parseAvcCodecPrivate.width, parseAvcCodecPrivate.height, parseAvcCodecPrivate.initializationData, -1, parseAvcCodecPrivate.pixelWidthAspectRatio));
            this.hasOutputFormat = true;
        } else if (readUnsignedByte == VIDEO_FRAME_KEYFRAME) {
            byte[] bArr = this.nalLength.data;
            bArr[AVC_PACKET_TYPE_SEQUENCE_HEADER] = (byte) 0;
            bArr[VIDEO_FRAME_KEYFRAME] = (byte) 0;
            bArr[2] = (byte) 0;
            readUnsignedByte = 4 - this.nalUnitLengthFieldLength;
            int i = AVC_PACKET_TYPE_SEQUENCE_HEADER;
            while (parsableByteArray.bytesLeft() > 0) {
                parsableByteArray.readBytes(this.nalLength.data, readUnsignedByte, this.nalUnitLengthFieldLength);
                this.nalLength.setPosition(AVC_PACKET_TYPE_SEQUENCE_HEADER);
                int readUnsignedIntToInt = this.nalLength.readUnsignedIntToInt();
                this.nalStartCode.setPosition(AVC_PACKET_TYPE_SEQUENCE_HEADER);
                this.output.sampleData(this.nalStartCode, 4);
                int i2 = i + 4;
                this.output.sampleData(parsableByteArray, readUnsignedIntToInt);
                i = i2 + readUnsignedIntToInt;
            }
            this.output.sampleMetadata(readUnsignedInt24, this.frameType == VIDEO_FRAME_KEYFRAME ? VIDEO_FRAME_KEYFRAME : AVC_PACKET_TYPE_SEQUENCE_HEADER, i, AVC_PACKET_TYPE_SEQUENCE_HEADER, null);
        }
    }

    public void seek() {
    }
}
