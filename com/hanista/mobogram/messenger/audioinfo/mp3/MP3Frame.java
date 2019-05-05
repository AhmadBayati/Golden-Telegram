package com.hanista.mobogram.messenger.audioinfo.mp3;

import com.hanista.mobogram.messenger.exoplayer.upstream.UdpDataSource;
import com.hanista.mobogram.messenger.exoplayer.util.NalUnitUtil;
import com.hanista.mobogram.tgnet.TLRPC;

public class MP3Frame {
    private final byte[] bytes;
    private final Header header;

    static final class CRC16 {
        private short crc;

        CRC16() {
            this.crc = (short) -1;
        }

        public short getValue() {
            return this.crc;
        }

        public void reset() {
            this.crc = (short) -1;
        }

        public void update(byte b) {
            update(b, 8);
        }

        public void update(int i, int i2) {
            int i3 = 1 << (i2 - 1);
            do {
                if ((((i & i3) == 0 ? 1 : 0) ^ ((this.crc & TLRPC.MESSAGE_FLAG_EDITED) == 0 ? 1 : 0)) != 0) {
                    this.crc = (short) (this.crc << 1);
                    this.crc = (short) (this.crc ^ 32773);
                } else {
                    this.crc = (short) (this.crc << 1);
                }
                i3 >>>= 1;
            } while (i3 != 0);
        }
    }

    public static class Header {
        private static final int[][] BITRATES;
        private static final int[][] BITRATES_COLUMN;
        private static final int[][] FREQUENCIES;
        private static final int MPEG_BITRATE_FREE = 0;
        private static final int MPEG_BITRATE_RESERVED = 15;
        public static final int MPEG_CHANNEL_MODE_MONO = 3;
        private static final int MPEG_FRQUENCY_RESERVED = 3;
        public static final int MPEG_LAYER_1 = 3;
        public static final int MPEG_LAYER_2 = 2;
        public static final int MPEG_LAYER_3 = 1;
        private static final int MPEG_LAYER_RESERVED = 0;
        public static final int MPEG_PROTECTION_CRC = 0;
        public static final int MPEG_VERSION_1 = 3;
        public static final int MPEG_VERSION_2 = 2;
        public static final int MPEG_VERSION_2_5 = 0;
        private static final int MPEG_VERSION_RESERVED = 1;
        private static final int[][] SIDE_INFO_SIZES;
        private static final int[][] SIZE_COEFFICIENTS;
        private static final int[] SLOT_SIZES;
        private final int bitrate;
        private final int channelMode;
        private final int frequency;
        private final int layer;
        private final int padding;
        private final int protection;
        private final int version;

        static {
            FREQUENCIES = new int[][]{new int[]{11025, -1, 22050, 44100}, new int[]{12000, -1, 24000, 48000}, new int[]{UdpDataSource.DEAFULT_SOCKET_TIMEOUT_MILLIS, -1, 16000, 32000}, new int[]{-1, -1, -1, -1}};
            BITRATES = new int[][]{new int[]{MPEG_VERSION_2_5, MPEG_VERSION_2_5, MPEG_VERSION_2_5, MPEG_VERSION_2_5, MPEG_VERSION_2_5}, new int[]{32000, 32000, 32000, 32000, UdpDataSource.DEAFULT_SOCKET_TIMEOUT_MILLIS}, new int[]{64000, 48000, 40000, 48000, 16000}, new int[]{96000, 56000, 48000, 56000, 24000}, new int[]{128000, 64000, 56000, 64000, 32000}, new int[]{160000, 80000, 64000, 80000, 40000}, new int[]{192000, 96000, 80000, 96000, 48000}, new int[]{224000, 112000, 96000, 112000, 56000}, new int[]{256000, 128000, 112000, 128000, 64000}, new int[]{288000, 160000, 128000, 144000, 80000}, new int[]{320000, 192000, 160000, 160000, 96000}, new int[]{352000, 224000, 192000, 176000, 112000}, new int[]{384000, 256000, 224000, 192000, 128000}, new int[]{416000, 320000, 256000, 224000, 144000}, new int[]{448000, 384000, 320000, 256000, 160000}, new int[]{-1, -1, -1, -1, -1}};
            BITRATES_COLUMN = new int[][]{new int[]{-1, 4, 4, MPEG_VERSION_1}, new int[]{-1, -1, -1, -1}, new int[]{-1, 4, 4, MPEG_VERSION_1}, new int[]{-1, MPEG_VERSION_2, MPEG_VERSION_RESERVED, MPEG_VERSION_2_5}};
            SIZE_COEFFICIENTS = new int[][]{new int[]{-1, 72, 144, 12}, new int[]{-1, -1, -1, -1}, new int[]{-1, 72, 144, 12}, new int[]{-1, 144, 144, 12}};
            SLOT_SIZES = new int[]{-1, MPEG_VERSION_RESERVED, MPEG_VERSION_RESERVED, 4};
            SIDE_INFO_SIZES = new int[][]{new int[]{17, -1, 17, 32}, new int[]{17, -1, 17, 32}, new int[]{17, -1, 17, 32}, new int[]{9, -1, 9, 17}};
        }

        public Header(int i, int i2, int i3) {
            this.version = (i >> MPEG_VERSION_1) & MPEG_VERSION_1;
            if (this.version == MPEG_VERSION_RESERVED) {
                throw new MP3Exception("Reserved version");
            }
            this.layer = (i >> MPEG_VERSION_RESERVED) & MPEG_VERSION_1;
            if (this.layer == 0) {
                throw new MP3Exception("Reserved layer");
            }
            this.bitrate = (i2 >> 4) & MPEG_BITRATE_RESERVED;
            if (this.bitrate == MPEG_BITRATE_RESERVED) {
                throw new MP3Exception("Reserved bitrate");
            } else if (this.bitrate == 0) {
                throw new MP3Exception("Free bitrate");
            } else {
                this.frequency = (i2 >> MPEG_VERSION_2) & MPEG_VERSION_1;
                if (this.frequency == MPEG_VERSION_1) {
                    throw new MP3Exception("Reserved frequency");
                }
                this.channelMode = (i3 >> 6) & MPEG_VERSION_1;
                this.padding = (i2 >> MPEG_VERSION_RESERVED) & MPEG_VERSION_RESERVED;
                this.protection = i & MPEG_VERSION_RESERVED;
                int i4 = 4;
                if (this.protection == 0) {
                    i4 = 6;
                }
                if (this.layer == MPEG_VERSION_RESERVED) {
                    i4 += getSideInfoSize();
                }
                if (getFrameSize() < i4) {
                    throw new MP3Exception("Frame size must be at least " + i4);
                }
            }
        }

        public int getBitrate() {
            return BITRATES[this.bitrate][BITRATES_COLUMN[this.version][this.layer]];
        }

        public int getChannelMode() {
            return this.channelMode;
        }

        public int getDuration() {
            return (int) getTotalDuration((long) getFrameSize());
        }

        public int getFrameSize() {
            return (((SIZE_COEFFICIENTS[this.version][this.layer] * getBitrate()) / getFrequency()) + this.padding) * SLOT_SIZES[this.layer];
        }

        public int getFrequency() {
            return FREQUENCIES[this.frequency][this.version];
        }

        public int getLayer() {
            return this.layer;
        }

        public int getProtection() {
            return this.protection;
        }

        public int getSampleCount() {
            return this.layer == MPEG_VERSION_1 ? 384 : 1152;
        }

        public int getSideInfoSize() {
            return SIDE_INFO_SIZES[this.channelMode][this.version];
        }

        public long getTotalDuration(long j) {
            long sampleCount = (1000 * (((long) getSampleCount()) * j)) / ((long) (getFrameSize() * getFrequency()));
            return (getVersion() == MPEG_VERSION_1 || getChannelMode() != MPEG_VERSION_1) ? sampleCount : sampleCount / 2;
        }

        public int getVBRIOffset() {
            return 36;
        }

        public int getVersion() {
            return this.version;
        }

        public int getXingOffset() {
            return getSideInfoSize() + 4;
        }

        public boolean isCompatible(Header header) {
            return this.layer == header.layer && this.version == header.version && this.frequency == header.frequency && this.channelMode == header.channelMode;
        }
    }

    MP3Frame(Header header, byte[] bArr) {
        this.header = header;
        this.bytes = bArr;
    }

    public Header getHeader() {
        return this.header;
    }

    public int getNumberOfFrames() {
        int xingOffset;
        if (isXingFrame()) {
            xingOffset = this.header.getXingOffset();
            if ((this.bytes[xingOffset + 7] & 1) != 0) {
                return (this.bytes[xingOffset + 11] & NalUnitUtil.EXTENDED_SAR) | ((((this.bytes[xingOffset + 8] & NalUnitUtil.EXTENDED_SAR) << 24) | ((this.bytes[xingOffset + 9] & NalUnitUtil.EXTENDED_SAR) << 16)) | ((this.bytes[xingOffset + 10] & NalUnitUtil.EXTENDED_SAR) << 8));
            }
        } else if (isVBRIFrame()) {
            xingOffset = this.header.getVBRIOffset();
            return (this.bytes[xingOffset + 17] & NalUnitUtil.EXTENDED_SAR) | ((((this.bytes[xingOffset + 14] & NalUnitUtil.EXTENDED_SAR) << 24) | ((this.bytes[xingOffset + 15] & NalUnitUtil.EXTENDED_SAR) << 16)) | ((this.bytes[xingOffset + 16] & NalUnitUtil.EXTENDED_SAR) << 8));
        }
        return -1;
    }

    public int getSize() {
        return this.bytes.length;
    }

    boolean isChecksumError() {
        if (this.header.getProtection() != 0 || this.header.getLayer() != 1) {
            return false;
        }
        CRC16 crc16 = new CRC16();
        crc16.update(this.bytes[2]);
        crc16.update(this.bytes[3]);
        int sideInfoSize = this.header.getSideInfoSize();
        for (int i = 0; i < sideInfoSize; i++) {
            crc16.update(this.bytes[i + 6]);
        }
        return (((this.bytes[4] & NalUnitUtil.EXTENDED_SAR) << 8) | (this.bytes[5] & NalUnitUtil.EXTENDED_SAR)) != crc16.getValue();
    }

    boolean isVBRIFrame() {
        int vBRIOffset = this.header.getVBRIOffset();
        return this.bytes.length >= vBRIOffset + 26 && this.bytes[vBRIOffset] == 86 && this.bytes[vBRIOffset + 1] == 66 && this.bytes[vBRIOffset + 2] == 82 && this.bytes[vBRIOffset + 3] == 73;
    }

    boolean isXingFrame() {
        int xingOffset = this.header.getXingOffset();
        return (this.bytes.length >= xingOffset + 12 && xingOffset >= 0 && this.bytes.length >= xingOffset + 8) ? (this.bytes[xingOffset] == 88 && this.bytes[xingOffset + 1] == 105 && this.bytes[xingOffset + 2] == (byte) 110 && this.bytes[xingOffset + 3] == 103) ? true : this.bytes[xingOffset] == 73 && this.bytes[xingOffset + 1] == (byte) 110 && this.bytes[xingOffset + 2] == 102 && this.bytes[xingOffset + 3] == 111 : false;
    }
}
