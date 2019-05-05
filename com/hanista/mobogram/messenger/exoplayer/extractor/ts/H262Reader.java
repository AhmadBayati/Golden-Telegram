package com.hanista.mobogram.messenger.exoplayer.extractor.ts;

import android.util.Pair;
import com.hanista.mobogram.messenger.exoplayer.MediaFormat;
import com.hanista.mobogram.messenger.exoplayer.extractor.TrackOutput;
import com.hanista.mobogram.messenger.exoplayer.util.MimeTypes;
import com.hanista.mobogram.messenger.exoplayer.util.NalUnitUtil;
import com.hanista.mobogram.messenger.exoplayer.util.ParsableByteArray;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;
import com.hanista.mobogram.tgnet.TLRPC;
import com.hanista.mobogram.ui.Components.VideoPlayer;
import java.util.Arrays;
import java.util.Collections;

final class H262Reader extends ElementaryStreamReader {
    private static final double[] FRAME_RATE_VALUES;
    private static final int START_EXTENSION = 181;
    private static final int START_GROUP = 184;
    private static final int START_PICTURE = 0;
    private static final int START_SEQUENCE_HEADER = 179;
    private final CsdBuffer csdBuffer;
    private boolean foundFirstFrameInGroup;
    private long frameDurationUs;
    private long framePosition;
    private long frameTimeUs;
    private boolean hasOutputFormat;
    private boolean isKeyframe;
    private boolean pesPtsUsAvailable;
    private long pesTimeUs;
    private final boolean[] prefixFlags;
    private long totalBytesWritten;

    private static final class CsdBuffer {
        public byte[] data;
        private boolean isFilling;
        public int length;
        public int sequenceExtensionPosition;

        public CsdBuffer(int i) {
            this.data = new byte[i];
        }

        public void onData(byte[] bArr, int i, int i2) {
            if (this.isFilling) {
                int i3 = i2 - i;
                if (this.data.length < this.length + i3) {
                    this.data = Arrays.copyOf(this.data, (this.length + i3) * 2);
                }
                System.arraycopy(bArr, i, this.data, this.length, i3);
                this.length = i3 + this.length;
            }
        }

        public boolean onStartCode(int i, int i2) {
            if (this.isFilling) {
                if (this.sequenceExtensionPosition == 0 && i == H262Reader.START_EXTENSION) {
                    this.sequenceExtensionPosition = this.length;
                } else {
                    this.length -= i2;
                    this.isFilling = false;
                    return true;
                }
            } else if (i == H262Reader.START_SEQUENCE_HEADER) {
                this.isFilling = true;
            }
            return false;
        }

        public void reset() {
            this.isFilling = false;
            this.length = H262Reader.START_PICTURE;
            this.sequenceExtensionPosition = H262Reader.START_PICTURE;
        }
    }

    static {
        FRAME_RATE_VALUES = new double[]{23.976023976023978d, 24.0d, 25.0d, 29.97002997002997d, 30.0d, 50.0d, 59.94005994005994d, 60.0d};
    }

    public H262Reader(TrackOutput trackOutput) {
        super(trackOutput);
        this.prefixFlags = new boolean[4];
        this.csdBuffer = new CsdBuffer(TLRPC.USER_FLAG_UNUSED);
    }

    private static Pair<MediaFormat, Long> parseCsdBuffer(CsdBuffer csdBuffer) {
        Object copyOf = Arrays.copyOf(csdBuffer.data, csdBuffer.length);
        int i = copyOf[5] & NalUnitUtil.EXTENDED_SAR;
        int i2 = ((copyOf[4] & NalUnitUtil.EXTENDED_SAR) << 4) | (i >> 4);
        int i3 = ((i & 15) << 8) | (copyOf[6] & NalUnitUtil.EXTENDED_SAR);
        float f = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
        switch ((copyOf[7] & PsExtractor.VIDEO_STREAM_MASK) >> 4) {
            case VideoPlayer.STATE_PREPARING /*2*/:
                f = ((float) (i3 * 4)) / ((float) (i2 * 3));
                break;
            case VideoPlayer.STATE_BUFFERING /*3*/:
                f = ((float) (i3 * 16)) / ((float) (i2 * 9));
                break;
            case VideoPlayer.STATE_READY /*4*/:
                f = ((float) (i3 * 121)) / ((float) (i2 * 100));
                break;
        }
        MediaFormat createVideoFormat = MediaFormat.createVideoFormat(null, MimeTypes.VIDEO_MPEG2, -1, -1, -1, i2, i3, Collections.singletonList(copyOf), -1, f);
        long j = 0;
        int i4 = (copyOf[7] & 15) - 1;
        if (i4 >= 0 && i4 < FRAME_RATE_VALUES.length) {
            double d = FRAME_RATE_VALUES[i4];
            i4 = csdBuffer.sequenceExtensionPosition;
            int i5 = (copyOf[i4 + 9] & 96) >> 5;
            i4 = copyOf[i4 + 9] & 31;
            if (i5 != i4) {
                d *= (((double) i5) + 1.0d) / ((double) (i4 + 1));
            }
            j = (long) (1000000.0d / d);
        }
        return Pair.create(createVideoFormat, Long.valueOf(j));
    }

    public void consume(ParsableByteArray parsableByteArray) {
        if (parsableByteArray.bytesLeft() > 0) {
            int position = parsableByteArray.getPosition();
            int limit = parsableByteArray.limit();
            byte[] bArr = parsableByteArray.data;
            this.totalBytesWritten += (long) parsableByteArray.bytesLeft();
            this.output.sampleData(parsableByteArray, parsableByteArray.bytesLeft());
            int i = position;
            while (true) {
                int findNalUnit = NalUnitUtil.findNalUnit(bArr, position, limit, this.prefixFlags);
                if (findNalUnit == limit) {
                    break;
                }
                int i2 = parsableByteArray.data[findNalUnit + 3] & NalUnitUtil.EXTENDED_SAR;
                if (!this.hasOutputFormat) {
                    position = findNalUnit - i;
                    if (position > 0) {
                        this.csdBuffer.onData(bArr, i, findNalUnit);
                    }
                    if (this.csdBuffer.onStartCode(i2, position < 0 ? -position : START_PICTURE)) {
                        Pair parseCsdBuffer = parseCsdBuffer(this.csdBuffer);
                        this.output.format((MediaFormat) parseCsdBuffer.first);
                        this.frameDurationUs = ((Long) parseCsdBuffer.second).longValue();
                        this.hasOutputFormat = true;
                    }
                }
                if (this.hasOutputFormat && (i2 == START_GROUP || i2 == 0)) {
                    int i3 = limit - findNalUnit;
                    if (this.foundFirstFrameInGroup) {
                        this.output.sampleMetadata(this.frameTimeUs, this.isKeyframe ? 1 : START_PICTURE, ((int) (this.totalBytesWritten - this.framePosition)) - i3, i3, null);
                        this.isKeyframe = false;
                    }
                    if (i2 == START_GROUP) {
                        this.foundFirstFrameInGroup = false;
                        this.isKeyframe = true;
                    } else {
                        this.frameTimeUs = this.pesPtsUsAvailable ? this.pesTimeUs : this.frameTimeUs + this.frameDurationUs;
                        this.framePosition = this.totalBytesWritten - ((long) i3);
                        this.pesPtsUsAvailable = false;
                        this.foundFirstFrameInGroup = true;
                    }
                }
                position = findNalUnit + 3;
                i = findNalUnit;
            }
            if (!this.hasOutputFormat) {
                this.csdBuffer.onData(bArr, i, limit);
            }
        }
    }

    public void packetFinished() {
    }

    public void packetStarted(long j, boolean z) {
        this.pesPtsUsAvailable = j != -1;
        if (this.pesPtsUsAvailable) {
            this.pesTimeUs = j;
        }
    }

    public void seek() {
        NalUnitUtil.clearPrefixFlags(this.prefixFlags);
        this.csdBuffer.reset();
        this.pesPtsUsAvailable = false;
        this.foundFirstFrameInGroup = false;
        this.totalBytesWritten = 0;
    }
}
