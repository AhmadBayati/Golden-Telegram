package com.hanista.mobogram.messenger.exoplayer.extractor.webm;

import android.support.v4.view.PointerIconCompat;
import android.util.Pair;
import android.util.SparseArray;
import com.hanista.mobogram.messenger.exoplayer.C0700C;
import com.hanista.mobogram.messenger.exoplayer.ParserException;
import com.hanista.mobogram.messenger.exoplayer.drm.DrmInitData.SchemeInitData;
import com.hanista.mobogram.messenger.exoplayer.drm.DrmInitData.Universal;
import com.hanista.mobogram.messenger.exoplayer.extractor.ChunkIndex;
import com.hanista.mobogram.messenger.exoplayer.extractor.Extractor;
import com.hanista.mobogram.messenger.exoplayer.extractor.ExtractorInput;
import com.hanista.mobogram.messenger.exoplayer.extractor.ExtractorOutput;
import com.hanista.mobogram.messenger.exoplayer.extractor.PositionHolder;
import com.hanista.mobogram.messenger.exoplayer.extractor.SeekMap;
import com.hanista.mobogram.messenger.exoplayer.extractor.TrackOutput;
import com.hanista.mobogram.messenger.exoplayer.upstream.UdpDataSource;
import com.hanista.mobogram.messenger.exoplayer.util.LongArray;
import com.hanista.mobogram.messenger.exoplayer.util.MimeTypes;
import com.hanista.mobogram.messenger.exoplayer.util.NalUnitUtil;
import com.hanista.mobogram.messenger.exoplayer.util.ParsableByteArray;
import com.hanista.mobogram.messenger.exoplayer.util.Util;
import com.hanista.mobogram.tgnet.TLRPC;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public final class WebmExtractor implements Extractor {
    private static final int BLOCK_STATE_DATA = 2;
    private static final int BLOCK_STATE_HEADER = 1;
    private static final int BLOCK_STATE_START = 0;
    private static final String CODEC_ID_AAC = "A_AAC";
    private static final String CODEC_ID_AC3 = "A_AC3";
    private static final String CODEC_ID_ACM = "A_MS/ACM";
    private static final String CODEC_ID_DTS = "A_DTS";
    private static final String CODEC_ID_DTS_EXPRESS = "A_DTS/EXPRESS";
    private static final String CODEC_ID_DTS_LOSSLESS = "A_DTS/LOSSLESS";
    private static final String CODEC_ID_E_AC3 = "A_EAC3";
    private static final String CODEC_ID_FLAC = "A_FLAC";
    private static final String CODEC_ID_FOURCC = "V_MS/VFW/FOURCC";
    private static final String CODEC_ID_H264 = "V_MPEG4/ISO/AVC";
    private static final String CODEC_ID_H265 = "V_MPEGH/ISO/HEVC";
    private static final String CODEC_ID_MP3 = "A_MPEG/L3";
    private static final String CODEC_ID_MPEG2 = "V_MPEG2";
    private static final String CODEC_ID_MPEG4_AP = "V_MPEG4/ISO/AP";
    private static final String CODEC_ID_MPEG4_ASP = "V_MPEG4/ISO/ASP";
    private static final String CODEC_ID_MPEG4_SP = "V_MPEG4/ISO/SP";
    private static final String CODEC_ID_OPUS = "A_OPUS";
    private static final String CODEC_ID_PCM_INT_LIT = "A_PCM/INT/LIT";
    private static final String CODEC_ID_PGS = "S_HDMV/PGS";
    private static final String CODEC_ID_SUBRIP = "S_TEXT/UTF8";
    private static final String CODEC_ID_TRUEHD = "A_TRUEHD";
    private static final String CODEC_ID_VOBSUB = "S_VOBSUB";
    private static final String CODEC_ID_VORBIS = "A_VORBIS";
    private static final String CODEC_ID_VP8 = "V_VP8";
    private static final String CODEC_ID_VP9 = "V_VP9";
    private static final String DOC_TYPE_MATROSKA = "matroska";
    private static final String DOC_TYPE_WEBM = "webm";
    private static final int ENCRYPTION_IV_SIZE = 8;
    private static final int FOURCC_COMPRESSION_VC1 = 826496599;
    private static final int ID_AUDIO = 225;
    private static final int ID_AUDIO_BIT_DEPTH = 25188;
    private static final int ID_BLOCK = 161;
    private static final int ID_BLOCK_DURATION = 155;
    private static final int ID_BLOCK_GROUP = 160;
    private static final int ID_CHANNELS = 159;
    private static final int ID_CLUSTER = 524531317;
    private static final int ID_CODEC_DELAY = 22186;
    private static final int ID_CODEC_ID = 134;
    private static final int ID_CODEC_PRIVATE = 25506;
    private static final int ID_CONTENT_COMPRESSION = 20532;
    private static final int ID_CONTENT_COMPRESSION_ALGORITHM = 16980;
    private static final int ID_CONTENT_COMPRESSION_SETTINGS = 16981;
    private static final int ID_CONTENT_ENCODING = 25152;
    private static final int ID_CONTENT_ENCODINGS = 28032;
    private static final int ID_CONTENT_ENCODING_ORDER = 20529;
    private static final int ID_CONTENT_ENCODING_SCOPE = 20530;
    private static final int ID_CONTENT_ENCRYPTION = 20533;
    private static final int ID_CONTENT_ENCRYPTION_AES_SETTINGS = 18407;
    private static final int ID_CONTENT_ENCRYPTION_AES_SETTINGS_CIPHER_MODE = 18408;
    private static final int ID_CONTENT_ENCRYPTION_ALGORITHM = 18401;
    private static final int ID_CONTENT_ENCRYPTION_KEY_ID = 18402;
    private static final int ID_CUES = 475249515;
    private static final int ID_CUE_CLUSTER_POSITION = 241;
    private static final int ID_CUE_POINT = 187;
    private static final int ID_CUE_TIME = 179;
    private static final int ID_CUE_TRACK_POSITIONS = 183;
    private static final int ID_DEFAULT_DURATION = 2352003;
    private static final int ID_DISPLAY_HEIGHT = 21690;
    private static final int ID_DISPLAY_UNIT = 21682;
    private static final int ID_DISPLAY_WIDTH = 21680;
    private static final int ID_DOC_TYPE = 17026;
    private static final int ID_DOC_TYPE_READ_VERSION = 17029;
    private static final int ID_DURATION = 17545;
    private static final int ID_EBML = 440786851;
    private static final int ID_EBML_READ_VERSION = 17143;
    private static final int ID_INFO = 357149030;
    private static final int ID_LANGUAGE = 2274716;
    private static final int ID_PIXEL_HEIGHT = 186;
    private static final int ID_PIXEL_WIDTH = 176;
    private static final int ID_REFERENCE_BLOCK = 251;
    private static final int ID_SAMPLING_FREQUENCY = 181;
    private static final int ID_SEEK = 19899;
    private static final int ID_SEEK_HEAD = 290298740;
    private static final int ID_SEEK_ID = 21419;
    private static final int ID_SEEK_POSITION = 21420;
    private static final int ID_SEEK_PRE_ROLL = 22203;
    private static final int ID_SEGMENT = 408125543;
    private static final int ID_SEGMENT_INFO = 357149030;
    private static final int ID_SIMPLE_BLOCK = 163;
    private static final int ID_TIMECODE_SCALE = 2807729;
    private static final int ID_TIME_CODE = 231;
    private static final int ID_TRACKS = 374648427;
    private static final int ID_TRACK_ENTRY = 174;
    private static final int ID_TRACK_NUMBER = 215;
    private static final int ID_TRACK_TYPE = 131;
    private static final int ID_VIDEO = 224;
    private static final int LACING_EBML = 3;
    private static final int LACING_FIXED_SIZE = 2;
    private static final int LACING_NONE = 0;
    private static final int LACING_XIPH = 1;
    private static final int MP3_MAX_INPUT_SIZE = 4096;
    private static final int OPUS_MAX_INPUT_SIZE = 5760;
    private static final byte[] SUBRIP_PREFIX;
    private static final int SUBRIP_PREFIX_END_TIMECODE_OFFSET = 19;
    private static final byte[] SUBRIP_TIMECODE_EMPTY;
    private static final int SUBRIP_TIMECODE_LENGTH = 12;
    private static final int TRACK_TYPE_AUDIO = 2;
    private static final int UNKNOWN = -1;
    private static final int VORBIS_MAX_INPUT_SIZE = 8192;
    private static final int WAVE_FORMAT_EXTENSIBLE = 65534;
    private static final int WAVE_FORMAT_PCM = 1;
    private static final int WAVE_FORMAT_SIZE = 18;
    private static final UUID WAVE_SUBFORMAT_PCM;
    private long blockDurationUs;
    private int blockFlags;
    private int blockLacingSampleCount;
    private int blockLacingSampleIndex;
    private int[] blockLacingSampleSizes;
    private int blockState;
    private long blockTimeUs;
    private int blockTrackNumber;
    private int blockTrackNumberLength;
    private long clusterTimecodeUs;
    private LongArray cueClusterPositions;
    private LongArray cueTimesUs;
    private long cuesContentPosition;
    private Track currentTrack;
    private long durationTimecode;
    private long durationUs;
    private ExtractorOutput extractorOutput;
    private final ParsableByteArray nalLength;
    private final ParsableByteArray nalStartCode;
    private final EbmlReader reader;
    private int sampleBytesRead;
    private int sampleBytesWritten;
    private int sampleCurrentNalBytesRemaining;
    private boolean sampleEncodingHandled;
    private boolean sampleRead;
    private boolean sampleSeenReferenceBlock;
    private final ParsableByteArray sampleStrippedBytes;
    private final ParsableByteArray scratch;
    private int seekEntryId;
    private final ParsableByteArray seekEntryIdBytes;
    private long seekEntryPosition;
    private boolean seekForCues;
    private long seekPositionAfterBuildingCues;
    private boolean seenClusterPositionForCurrentCuePoint;
    private long segmentContentPosition;
    private long segmentContentSize;
    private boolean sentDrmInitData;
    private boolean sentSeekMap;
    private final ParsableByteArray subripSample;
    private long timecodeScale;
    private final SparseArray<Track> tracks;
    private final VarintReader varintReader;
    private final ParsableByteArray vorbisNumPageSamples;

    private final class InnerEbmlReaderOutput implements EbmlReaderOutput {
        private InnerEbmlReaderOutput() {
        }

        public void binaryElement(int i, int i2, ExtractorInput extractorInput) {
            WebmExtractor.this.binaryElement(i, i2, extractorInput);
        }

        public void endMasterElement(int i) {
            WebmExtractor.this.endMasterElement(i);
        }

        public void floatElement(int i, double d) {
            WebmExtractor.this.floatElement(i, d);
        }

        public int getElementType(int i) {
            return WebmExtractor.this.getElementType(i);
        }

        public void integerElement(int i, long j) {
            WebmExtractor.this.integerElement(i, j);
        }

        public boolean isLevel1Element(int i) {
            return WebmExtractor.this.isLevel1Element(i);
        }

        public void startMasterElement(int i, long j, long j2) {
            WebmExtractor.this.startMasterElement(i, j, j2);
        }

        public void stringElement(int i, String str) {
            WebmExtractor.this.stringElement(i, str);
        }
    }

    private static final class Track {
        private static final int DISPLAY_UNIT_PIXELS = 0;
        public int audioBitDepth;
        public int channelCount;
        public long codecDelayNs;
        public String codecId;
        public byte[] codecPrivate;
        public int defaultSampleDurationNs;
        public int displayHeight;
        public int displayUnit;
        public int displayWidth;
        public byte[] encryptionKeyId;
        public boolean hasContentEncryption;
        public int height;
        private String language;
        public int nalUnitLengthFieldLength;
        public int number;
        public TrackOutput output;
        public int sampleRate;
        public byte[] sampleStrippedBytes;
        public long seekPreRollNs;
        public int type;
        public int width;

        private Track() {
            this.width = WebmExtractor.UNKNOWN;
            this.height = WebmExtractor.UNKNOWN;
            this.displayWidth = WebmExtractor.UNKNOWN;
            this.displayHeight = WebmExtractor.UNKNOWN;
            this.displayUnit = WebmExtractor.LACING_NONE;
            this.channelCount = WebmExtractor.WAVE_FORMAT_PCM;
            this.audioBitDepth = WebmExtractor.UNKNOWN;
            this.sampleRate = UdpDataSource.DEAFULT_SOCKET_TIMEOUT_MILLIS;
            this.codecDelayNs = 0;
            this.seekPreRollNs = 0;
            this.language = "eng";
        }

        private static Pair<List<byte[]>, Integer> parseAvcCodecPrivate(ParsableByteArray parsableByteArray) {
            int i = WebmExtractor.LACING_NONE;
            try {
                parsableByteArray.setPosition(4);
                int readUnsignedByte = (parsableByteArray.readUnsignedByte() & WebmExtractor.LACING_EBML) + WebmExtractor.WAVE_FORMAT_PCM;
                if (readUnsignedByte == WebmExtractor.LACING_EBML) {
                    throw new ParserException();
                }
                int i2;
                List arrayList = new ArrayList();
                int readUnsignedByte2 = parsableByteArray.readUnsignedByte() & 31;
                for (i2 = WebmExtractor.LACING_NONE; i2 < readUnsignedByte2; i2 += WebmExtractor.WAVE_FORMAT_PCM) {
                    arrayList.add(NalUnitUtil.parseChildNalUnit(parsableByteArray));
                }
                i2 = parsableByteArray.readUnsignedByte();
                while (i < i2) {
                    arrayList.add(NalUnitUtil.parseChildNalUnit(parsableByteArray));
                    i += WebmExtractor.WAVE_FORMAT_PCM;
                }
                return Pair.create(arrayList, Integer.valueOf(readUnsignedByte));
            } catch (ArrayIndexOutOfBoundsException e) {
                throw new ParserException("Error parsing AVC codec private");
            }
        }

        private static List<byte[]> parseFourCcVc1Private(ParsableByteArray parsableByteArray) {
            try {
                parsableByteArray.skipBytes(16);
                long readLittleEndianUnsignedInt = parsableByteArray.readLittleEndianUnsignedInt();
                if (readLittleEndianUnsignedInt != 826496599) {
                    throw new ParserException("Unsupported FourCC compression type: " + readLittleEndianUnsignedInt);
                }
                int position = parsableByteArray.getPosition() + 20;
                byte[] bArr = parsableByteArray.data;
                while (position < bArr.length - 4) {
                    if (bArr[position] == null && bArr[position + WebmExtractor.WAVE_FORMAT_PCM] == null && bArr[position + WebmExtractor.TRACK_TYPE_AUDIO] == WebmExtractor.WAVE_FORMAT_PCM && bArr[position + WebmExtractor.LACING_EBML] == 15) {
                        return Collections.singletonList(Arrays.copyOfRange(bArr, position, bArr.length));
                    }
                    position += WebmExtractor.WAVE_FORMAT_PCM;
                }
                throw new ParserException("Failed to find FourCC VC1 initialization data");
            } catch (ArrayIndexOutOfBoundsException e) {
                throw new ParserException("Error parsing FourCC VC1 codec private");
            }
        }

        private static Pair<List<byte[]>, Integer> parseHevcCodecPrivate(ParsableByteArray parsableByteArray) {
            try {
                int readUnsignedShort;
                int i;
                int i2;
                parsableByteArray.setPosition(21);
                int readUnsignedByte = parsableByteArray.readUnsignedByte() & WebmExtractor.LACING_EBML;
                int readUnsignedByte2 = parsableByteArray.readUnsignedByte();
                int position = parsableByteArray.getPosition();
                int i3 = WebmExtractor.LACING_NONE;
                int i4 = WebmExtractor.LACING_NONE;
                while (i3 < readUnsignedByte2) {
                    parsableByteArray.skipBytes(WebmExtractor.WAVE_FORMAT_PCM);
                    readUnsignedShort = parsableByteArray.readUnsignedShort();
                    i = i4;
                    for (i2 = WebmExtractor.LACING_NONE; i2 < readUnsignedShort; i2 += WebmExtractor.WAVE_FORMAT_PCM) {
                        i4 = parsableByteArray.readUnsignedShort();
                        i += i4 + 4;
                        parsableByteArray.skipBytes(i4);
                    }
                    i3 += WebmExtractor.WAVE_FORMAT_PCM;
                    i4 = i;
                }
                parsableByteArray.setPosition(position);
                Object obj = new byte[i4];
                i3 = WebmExtractor.LACING_NONE;
                i2 = WebmExtractor.LACING_NONE;
                while (i3 < readUnsignedByte2) {
                    parsableByteArray.skipBytes(WebmExtractor.WAVE_FORMAT_PCM);
                    readUnsignedShort = parsableByteArray.readUnsignedShort();
                    i = i2;
                    for (i2 = WebmExtractor.LACING_NONE; i2 < readUnsignedShort; i2 += WebmExtractor.WAVE_FORMAT_PCM) {
                        int readUnsignedShort2 = parsableByteArray.readUnsignedShort();
                        System.arraycopy(NalUnitUtil.NAL_START_CODE, WebmExtractor.LACING_NONE, obj, i, NalUnitUtil.NAL_START_CODE.length);
                        i += NalUnitUtil.NAL_START_CODE.length;
                        System.arraycopy(parsableByteArray.data, parsableByteArray.getPosition(), obj, i, readUnsignedShort2);
                        i += readUnsignedShort2;
                        parsableByteArray.skipBytes(readUnsignedShort2);
                    }
                    i3 += WebmExtractor.WAVE_FORMAT_PCM;
                    i2 = i;
                }
                return Pair.create(i4 == 0 ? null : Collections.singletonList(obj), Integer.valueOf(readUnsignedByte + WebmExtractor.WAVE_FORMAT_PCM));
            } catch (ArrayIndexOutOfBoundsException e) {
                throw new ParserException("Error parsing HEVC codec private");
            }
        }

        private static boolean parseMsAcmCodecPrivate(ParsableByteArray parsableByteArray) {
            try {
                int readLittleEndianUnsignedShort = parsableByteArray.readLittleEndianUnsignedShort();
                if (readLittleEndianUnsignedShort == WebmExtractor.WAVE_FORMAT_PCM) {
                    return true;
                }
                if (readLittleEndianUnsignedShort != WebmExtractor.WAVE_FORMAT_EXTENSIBLE) {
                    return false;
                }
                parsableByteArray.setPosition(24);
                return parsableByteArray.readLong() == WebmExtractor.WAVE_SUBFORMAT_PCM.getMostSignificantBits() && parsableByteArray.readLong() == WebmExtractor.WAVE_SUBFORMAT_PCM.getLeastSignificantBits();
            } catch (ArrayIndexOutOfBoundsException e) {
                throw new ParserException("Error parsing MS/ACM codec private");
            }
        }

        private static List<byte[]> parseVorbisCodecPrivate(byte[] bArr) {
            int i = WebmExtractor.LACING_NONE;
            try {
                if (bArr[WebmExtractor.LACING_NONE] != WebmExtractor.TRACK_TYPE_AUDIO) {
                    throw new ParserException("Error parsing vorbis codec private");
                }
                int i2 = WebmExtractor.LACING_NONE;
                int i3 = WebmExtractor.WAVE_FORMAT_PCM;
                while (bArr[i3] == (byte) -1) {
                    i3 += WebmExtractor.WAVE_FORMAT_PCM;
                    i2 += NalUnitUtil.EXTENDED_SAR;
                }
                int i4 = i3 + WebmExtractor.WAVE_FORMAT_PCM;
                i2 += bArr[i3];
                while (bArr[i4] == (byte) -1) {
                    i += NalUnitUtil.EXTENDED_SAR;
                    i4 += WebmExtractor.WAVE_FORMAT_PCM;
                }
                i3 = i4 + WebmExtractor.WAVE_FORMAT_PCM;
                i += bArr[i4];
                if (bArr[i3] != (byte) 1) {
                    throw new ParserException("Error parsing vorbis codec private");
                }
                Object obj = new byte[i2];
                System.arraycopy(bArr, i3, obj, WebmExtractor.LACING_NONE, i2);
                i2 += i3;
                if (bArr[i2] != WebmExtractor.LACING_EBML) {
                    throw new ParserException("Error parsing vorbis codec private");
                }
                i += i2;
                if (bArr[i] != 5) {
                    throw new ParserException("Error parsing vorbis codec private");
                }
                Object obj2 = new byte[(bArr.length - i)];
                System.arraycopy(bArr, i, obj2, WebmExtractor.LACING_NONE, bArr.length - i);
                List<byte[]> arrayList = new ArrayList(WebmExtractor.TRACK_TYPE_AUDIO);
                arrayList.add(obj);
                arrayList.add(obj2);
                return arrayList;
            } catch (ArrayIndexOutOfBoundsException e) {
                throw new ParserException("Error parsing vorbis codec private");
            }
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void initializeOutput(com.hanista.mobogram.messenger.exoplayer.extractor.ExtractorOutput r12, int r13, long r14) {
            /*
            r11 = this;
            r0 = 0;
            r4 = 3;
            r5 = 8;
            r2 = -1;
            r1 = r11.codecId;
            r3 = r1.hashCode();
            switch(r3) {
                case -2095576542: goto L_0x0052;
                case -2095575984: goto L_0x003c;
                case -1985379776: goto L_0x010c;
                case -1784763192: goto L_0x00cb;
                case -1730367663: goto L_0x007e;
                case -1482641357: goto L_0x00a4;
                case -1373388978: goto L_0x0073;
                case -538363189: goto L_0x0047;
                case -538363109: goto L_0x005d;
                case -425012669: goto L_0x0133;
                case -356037306: goto L_0x00f2;
                case 62923557: goto L_0x0097;
                case 62923603: goto L_0x00b1;
                case 62927045: goto L_0x00d8;
                case 82338133: goto L_0x001b;
                case 82338134: goto L_0x0026;
                case 99146302: goto L_0x0140;
                case 542569478: goto L_0x00e5;
                case 725957860: goto L_0x0119;
                case 855502857: goto L_0x0068;
                case 1422270023: goto L_0x0126;
                case 1809237540: goto L_0x0031;
                case 1950749482: goto L_0x00be;
                case 1950789798: goto L_0x00ff;
                case 1951062397: goto L_0x008a;
                default: goto L_0x000e;
            };
        L_0x000e:
            r1 = r2;
        L_0x000f:
            switch(r1) {
                case 0: goto L_0x014d;
                case 1: goto L_0x0176;
                case 2: goto L_0x017d;
                case 3: goto L_0x0184;
                case 4: goto L_0x0184;
                case 5: goto L_0x0184;
                case 6: goto L_0x0196;
                case 7: goto L_0x01b7;
                case 8: goto L_0x01d9;
                case 9: goto L_0x01ec;
                case 10: goto L_0x01fb;
                case 11: goto L_0x0240;
                case 12: goto L_0x024e;
                case 13: goto L_0x0257;
                case 14: goto L_0x025f;
                case 15: goto L_0x0267;
                case 16: goto L_0x026f;
                case 17: goto L_0x026f;
                case 18: goto L_0x0277;
                case 19: goto L_0x027f;
                case 20: goto L_0x028d;
                case 21: goto L_0x02ca;
                case 22: goto L_0x02f1;
                case 23: goto L_0x02f9;
                case 24: goto L_0x0307;
                default: goto L_0x0012;
            };
        L_0x0012:
            r0 = new com.hanista.mobogram.messenger.exoplayer.ParserException;
            r1 = "Unrecognized codec identifier.";
            r0.<init>(r1);
            throw r0;
        L_0x001b:
            r3 = "V_VP8";
            r1 = r1.equals(r3);
            if (r1 == 0) goto L_0x000e;
        L_0x0024:
            r1 = 0;
            goto L_0x000f;
        L_0x0026:
            r3 = "V_VP9";
            r1 = r1.equals(r3);
            if (r1 == 0) goto L_0x000e;
        L_0x002f:
            r1 = 1;
            goto L_0x000f;
        L_0x0031:
            r3 = "V_MPEG2";
            r1 = r1.equals(r3);
            if (r1 == 0) goto L_0x000e;
        L_0x003a:
            r1 = 2;
            goto L_0x000f;
        L_0x003c:
            r3 = "V_MPEG4/ISO/SP";
            r1 = r1.equals(r3);
            if (r1 == 0) goto L_0x000e;
        L_0x0045:
            r1 = r4;
            goto L_0x000f;
        L_0x0047:
            r3 = "V_MPEG4/ISO/ASP";
            r1 = r1.equals(r3);
            if (r1 == 0) goto L_0x000e;
        L_0x0050:
            r1 = 4;
            goto L_0x000f;
        L_0x0052:
            r3 = "V_MPEG4/ISO/AP";
            r1 = r1.equals(r3);
            if (r1 == 0) goto L_0x000e;
        L_0x005b:
            r1 = 5;
            goto L_0x000f;
        L_0x005d:
            r3 = "V_MPEG4/ISO/AVC";
            r1 = r1.equals(r3);
            if (r1 == 0) goto L_0x000e;
        L_0x0066:
            r1 = 6;
            goto L_0x000f;
        L_0x0068:
            r3 = "V_MPEGH/ISO/HEVC";
            r1 = r1.equals(r3);
            if (r1 == 0) goto L_0x000e;
        L_0x0071:
            r1 = 7;
            goto L_0x000f;
        L_0x0073:
            r3 = "V_MS/VFW/FOURCC";
            r1 = r1.equals(r3);
            if (r1 == 0) goto L_0x000e;
        L_0x007c:
            r1 = r5;
            goto L_0x000f;
        L_0x007e:
            r3 = "A_VORBIS";
            r1 = r1.equals(r3);
            if (r1 == 0) goto L_0x000e;
        L_0x0087:
            r1 = 9;
            goto L_0x000f;
        L_0x008a:
            r3 = "A_OPUS";
            r1 = r1.equals(r3);
            if (r1 == 0) goto L_0x000e;
        L_0x0093:
            r1 = 10;
            goto L_0x000f;
        L_0x0097:
            r3 = "A_AAC";
            r1 = r1.equals(r3);
            if (r1 == 0) goto L_0x000e;
        L_0x00a0:
            r1 = 11;
            goto L_0x000f;
        L_0x00a4:
            r3 = "A_MPEG/L3";
            r1 = r1.equals(r3);
            if (r1 == 0) goto L_0x000e;
        L_0x00ad:
            r1 = 12;
            goto L_0x000f;
        L_0x00b1:
            r3 = "A_AC3";
            r1 = r1.equals(r3);
            if (r1 == 0) goto L_0x000e;
        L_0x00ba:
            r1 = 13;
            goto L_0x000f;
        L_0x00be:
            r3 = "A_EAC3";
            r1 = r1.equals(r3);
            if (r1 == 0) goto L_0x000e;
        L_0x00c7:
            r1 = 14;
            goto L_0x000f;
        L_0x00cb:
            r3 = "A_TRUEHD";
            r1 = r1.equals(r3);
            if (r1 == 0) goto L_0x000e;
        L_0x00d4:
            r1 = 15;
            goto L_0x000f;
        L_0x00d8:
            r3 = "A_DTS";
            r1 = r1.equals(r3);
            if (r1 == 0) goto L_0x000e;
        L_0x00e1:
            r1 = 16;
            goto L_0x000f;
        L_0x00e5:
            r3 = "A_DTS/EXPRESS";
            r1 = r1.equals(r3);
            if (r1 == 0) goto L_0x000e;
        L_0x00ee:
            r1 = 17;
            goto L_0x000f;
        L_0x00f2:
            r3 = "A_DTS/LOSSLESS";
            r1 = r1.equals(r3);
            if (r1 == 0) goto L_0x000e;
        L_0x00fb:
            r1 = 18;
            goto L_0x000f;
        L_0x00ff:
            r3 = "A_FLAC";
            r1 = r1.equals(r3);
            if (r1 == 0) goto L_0x000e;
        L_0x0108:
            r1 = 19;
            goto L_0x000f;
        L_0x010c:
            r3 = "A_MS/ACM";
            r1 = r1.equals(r3);
            if (r1 == 0) goto L_0x000e;
        L_0x0115:
            r1 = 20;
            goto L_0x000f;
        L_0x0119:
            r3 = "A_PCM/INT/LIT";
            r1 = r1.equals(r3);
            if (r1 == 0) goto L_0x000e;
        L_0x0122:
            r1 = 21;
            goto L_0x000f;
        L_0x0126:
            r3 = "S_TEXT/UTF8";
            r1 = r1.equals(r3);
            if (r1 == 0) goto L_0x000e;
        L_0x012f:
            r1 = 22;
            goto L_0x000f;
        L_0x0133:
            r3 = "S_VOBSUB";
            r1 = r1.equals(r3);
            if (r1 == 0) goto L_0x000e;
        L_0x013c:
            r1 = 23;
            goto L_0x000f;
        L_0x0140:
            r3 = "S_HDMV/PGS";
            r1 = r1.equals(r3);
            if (r1 == 0) goto L_0x000e;
        L_0x0149:
            r1 = 24;
            goto L_0x000f;
        L_0x014d:
            r1 = "video/x-vnd.on2.vp8";
            r8 = r0;
            r10 = r2;
            r3 = r2;
        L_0x0153:
            r0 = com.hanista.mobogram.messenger.exoplayer.util.MimeTypes.isAudio(r1);
            if (r0 == 0) goto L_0x030f;
        L_0x0159:
            r0 = java.lang.Integer.toString(r13);
            r6 = r11.channelCount;
            r7 = r11.sampleRate;
            r9 = r11.language;
            r4 = r14;
            r0 = com.hanista.mobogram.messenger.exoplayer.MediaFormat.createAudioFormat(r0, r1, r2, r3, r4, r6, r7, r8, r9, r10);
        L_0x0168:
            r1 = r11.number;
            r1 = r12.track(r1);
            r11.output = r1;
            r1 = r11.output;
            r1.format(r0);
            return;
        L_0x0176:
            r1 = "video/x-vnd.on2.vp9";
            r8 = r0;
            r10 = r2;
            r3 = r2;
            goto L_0x0153;
        L_0x017d:
            r1 = "video/mpeg2";
            r8 = r0;
            r10 = r2;
            r3 = r2;
            goto L_0x0153;
        L_0x0184:
            r1 = "video/mp4v-es";
            r3 = r11.codecPrivate;
            if (r3 != 0) goto L_0x018f;
        L_0x018b:
            r8 = r0;
            r10 = r2;
            r3 = r2;
            goto L_0x0153;
        L_0x018f:
            r0 = r11.codecPrivate;
            r0 = java.util.Collections.singletonList(r0);
            goto L_0x018b;
        L_0x0196:
            r3 = "video/avc";
            r0 = new com.hanista.mobogram.messenger.exoplayer.util.ParsableByteArray;
            r1 = r11.codecPrivate;
            r0.<init>(r1);
            r1 = parseAvcCodecPrivate(r0);
            r0 = r1.first;
            r0 = (java.util.List) r0;
            r1 = r1.second;
            r1 = (java.lang.Integer) r1;
            r1 = r1.intValue();
            r11.nalUnitLengthFieldLength = r1;
            r8 = r0;
            r10 = r2;
            r1 = r3;
            r3 = r2;
            goto L_0x0153;
        L_0x01b7:
            r3 = "video/hevc";
            r0 = new com.hanista.mobogram.messenger.exoplayer.util.ParsableByteArray;
            r1 = r11.codecPrivate;
            r0.<init>(r1);
            r1 = parseHevcCodecPrivate(r0);
            r0 = r1.first;
            r0 = (java.util.List) r0;
            r1 = r1.second;
            r1 = (java.lang.Integer) r1;
            r1 = r1.intValue();
            r11.nalUnitLengthFieldLength = r1;
            r8 = r0;
            r10 = r2;
            r1 = r3;
            r3 = r2;
            goto L_0x0153;
        L_0x01d9:
            r1 = "video/wvc1";
            r0 = new com.hanista.mobogram.messenger.exoplayer.util.ParsableByteArray;
            r3 = r11.codecPrivate;
            r0.<init>(r3);
            r0 = parseFourCcVc1Private(r0);
            r8 = r0;
            r10 = r2;
            r3 = r2;
            goto L_0x0153;
        L_0x01ec:
            r1 = "audio/vorbis";
            r3 = 8192; // 0x2000 float:1.14794E-41 double:4.0474E-320;
            r0 = r11.codecPrivate;
            r0 = parseVorbisCodecPrivate(r0);
            r8 = r0;
            r10 = r2;
            goto L_0x0153;
        L_0x01fb:
            r1 = "audio/opus";
            r3 = 5760; // 0x1680 float:8.071E-42 double:2.846E-320;
            r0 = new java.util.ArrayList;
            r0.<init>(r4);
            r4 = r11.codecPrivate;
            r0.add(r4);
            r4 = java.nio.ByteBuffer.allocate(r5);
            r6 = java.nio.ByteOrder.nativeOrder();
            r4 = r4.order(r6);
            r6 = r11.codecDelayNs;
            r4 = r4.putLong(r6);
            r4 = r4.array();
            r0.add(r4);
            r4 = java.nio.ByteBuffer.allocate(r5);
            r5 = java.nio.ByteOrder.nativeOrder();
            r4 = r4.order(r5);
            r6 = r11.seekPreRollNs;
            r4 = r4.putLong(r6);
            r4 = r4.array();
            r0.add(r4);
            r8 = r0;
            r10 = r2;
            goto L_0x0153;
        L_0x0240:
            r1 = "audio/mp4a-latm";
            r0 = r11.codecPrivate;
            r0 = java.util.Collections.singletonList(r0);
            r8 = r0;
            r10 = r2;
            r3 = r2;
            goto L_0x0153;
        L_0x024e:
            r1 = "audio/mpeg";
            r3 = 4096; // 0x1000 float:5.74E-42 double:2.0237E-320;
            r8 = r0;
            r10 = r2;
            goto L_0x0153;
        L_0x0257:
            r1 = "audio/ac3";
            r8 = r0;
            r10 = r2;
            r3 = r2;
            goto L_0x0153;
        L_0x025f:
            r1 = "audio/eac3";
            r8 = r0;
            r10 = r2;
            r3 = r2;
            goto L_0x0153;
        L_0x0267:
            r1 = "audio/true-hd";
            r8 = r0;
            r10 = r2;
            r3 = r2;
            goto L_0x0153;
        L_0x026f:
            r1 = "audio/vnd.dts";
            r8 = r0;
            r10 = r2;
            r3 = r2;
            goto L_0x0153;
        L_0x0277:
            r1 = "audio/vnd.dts.hd";
            r8 = r0;
            r10 = r2;
            r3 = r2;
            goto L_0x0153;
        L_0x027f:
            r1 = "audio/x-flac";
            r0 = r11.codecPrivate;
            r0 = java.util.Collections.singletonList(r0);
            r8 = r0;
            r10 = r2;
            r3 = r2;
            goto L_0x0153;
        L_0x028d:
            r1 = "audio/raw";
            r3 = new com.hanista.mobogram.messenger.exoplayer.util.ParsableByteArray;
            r4 = r11.codecPrivate;
            r3.<init>(r4);
            r3 = parseMsAcmCodecPrivate(r3);
            if (r3 != 0) goto L_0x02a6;
        L_0x029d:
            r0 = new com.hanista.mobogram.messenger.exoplayer.ParserException;
            r1 = "Non-PCM MS/ACM is unsupported";
            r0.<init>(r1);
            throw r0;
        L_0x02a6:
            r3 = r11.audioBitDepth;
            r10 = com.hanista.mobogram.messenger.exoplayer.util.Util.getPcmEncoding(r3);
            if (r10 != 0) goto L_0x0396;
        L_0x02ae:
            r0 = new com.hanista.mobogram.messenger.exoplayer.ParserException;
            r1 = new java.lang.StringBuilder;
            r1.<init>();
            r2 = "Unsupported PCM bit depth: ";
            r1 = r1.append(r2);
            r2 = r11.audioBitDepth;
            r1 = r1.append(r2);
            r1 = r1.toString();
            r0.<init>(r1);
            throw r0;
        L_0x02ca:
            r1 = "audio/raw";
            r3 = r11.audioBitDepth;
            r10 = com.hanista.mobogram.messenger.exoplayer.util.Util.getPcmEncoding(r3);
            if (r10 != 0) goto L_0x0396;
        L_0x02d5:
            r0 = new com.hanista.mobogram.messenger.exoplayer.ParserException;
            r1 = new java.lang.StringBuilder;
            r1.<init>();
            r2 = "Unsupported PCM bit depth: ";
            r1 = r1.append(r2);
            r2 = r11.audioBitDepth;
            r1 = r1.append(r2);
            r1 = r1.toString();
            r0.<init>(r1);
            throw r0;
        L_0x02f1:
            r1 = "application/x-subrip";
            r8 = r0;
            r10 = r2;
            r3 = r2;
            goto L_0x0153;
        L_0x02f9:
            r1 = "application/vobsub";
            r0 = r11.codecPrivate;
            r0 = java.util.Collections.singletonList(r0);
            r8 = r0;
            r10 = r2;
            r3 = r2;
            goto L_0x0153;
        L_0x0307:
            r1 = "application/pgs";
            r8 = r0;
            r10 = r2;
            r3 = r2;
            goto L_0x0153;
        L_0x030f:
            r0 = com.hanista.mobogram.messenger.exoplayer.util.MimeTypes.isVideo(r1);
            if (r0 == 0) goto L_0x0357;
        L_0x0315:
            r0 = r11.displayUnit;
            if (r0 != 0) goto L_0x0329;
        L_0x0319:
            r0 = r11.displayWidth;
            if (r0 != r2) goto L_0x0351;
        L_0x031d:
            r0 = r11.width;
        L_0x031f:
            r11.displayWidth = r0;
            r0 = r11.displayHeight;
            if (r0 != r2) goto L_0x0354;
        L_0x0325:
            r0 = r11.height;
        L_0x0327:
            r11.displayHeight = r0;
        L_0x0329:
            r10 = -1082130432; // 0xffffffffbf800000 float:-1.0 double:NaN;
            r0 = r11.displayWidth;
            if (r0 == r2) goto L_0x0341;
        L_0x032f:
            r0 = r11.displayHeight;
            if (r0 == r2) goto L_0x0341;
        L_0x0333:
            r0 = r11.height;
            r4 = r11.displayWidth;
            r0 = r0 * r4;
            r0 = (float) r0;
            r4 = r11.width;
            r5 = r11.displayHeight;
            r4 = r4 * r5;
            r4 = (float) r4;
            r10 = r0 / r4;
        L_0x0341:
            r0 = java.lang.Integer.toString(r13);
            r6 = r11.width;
            r7 = r11.height;
            r4 = r14;
            r9 = r2;
            r0 = com.hanista.mobogram.messenger.exoplayer.MediaFormat.createVideoFormat(r0, r1, r2, r3, r4, r6, r7, r8, r9, r10);
            goto L_0x0168;
        L_0x0351:
            r0 = r11.displayWidth;
            goto L_0x031f;
        L_0x0354:
            r0 = r11.displayHeight;
            goto L_0x0327;
        L_0x0357:
            r0 = "application/x-subrip";
            r0 = r0.equals(r1);
            if (r0 == 0) goto L_0x036d;
        L_0x0360:
            r0 = java.lang.Integer.toString(r13);
            r5 = r11.language;
            r3 = r14;
            r0 = com.hanista.mobogram.messenger.exoplayer.MediaFormat.createTextFormat(r0, r1, r2, r3, r5);
            goto L_0x0168;
        L_0x036d:
            r0 = "application/vobsub";
            r0 = r0.equals(r1);
            if (r0 != 0) goto L_0x037f;
        L_0x0376:
            r0 = "application/pgs";
            r0 = r0.equals(r1);
            if (r0 == 0) goto L_0x038d;
        L_0x037f:
            r0 = java.lang.Integer.toString(r13);
            r6 = r11.language;
            r3 = r14;
            r5 = r8;
            r0 = com.hanista.mobogram.messenger.exoplayer.MediaFormat.createImageFormat(r0, r1, r2, r3, r5, r6);
            goto L_0x0168;
        L_0x038d:
            r0 = new com.hanista.mobogram.messenger.exoplayer.ParserException;
            r1 = "Unexpected MIME type.";
            r0.<init>(r1);
            throw r0;
        L_0x0396:
            r8 = r0;
            r3 = r2;
            goto L_0x0153;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.hanista.mobogram.messenger.exoplayer.extractor.webm.WebmExtractor.Track.initializeOutput(com.hanista.mobogram.messenger.exoplayer.extractor.ExtractorOutput, int, long):void");
        }
    }

    static {
        SUBRIP_PREFIX = new byte[]{(byte) 49, (byte) 10, (byte) 48, (byte) 48, (byte) 58, (byte) 48, (byte) 48, (byte) 58, (byte) 48, (byte) 48, ClosedCaptionCtrl.ERASE_DISPLAYED_MEMORY, (byte) 48, (byte) 48, (byte) 48, ClosedCaptionCtrl.RESUME_CAPTION_LOADING, ClosedCaptionCtrl.CARRIAGE_RETURN, ClosedCaptionCtrl.CARRIAGE_RETURN, (byte) 62, ClosedCaptionCtrl.RESUME_CAPTION_LOADING, (byte) 48, (byte) 48, (byte) 58, (byte) 48, (byte) 48, (byte) 58, (byte) 48, (byte) 48, ClosedCaptionCtrl.ERASE_DISPLAYED_MEMORY, (byte) 48, (byte) 48, (byte) 48, (byte) 10};
        SUBRIP_TIMECODE_EMPTY = new byte[]{ClosedCaptionCtrl.RESUME_CAPTION_LOADING, ClosedCaptionCtrl.RESUME_CAPTION_LOADING, ClosedCaptionCtrl.RESUME_CAPTION_LOADING, ClosedCaptionCtrl.RESUME_CAPTION_LOADING, ClosedCaptionCtrl.RESUME_CAPTION_LOADING, ClosedCaptionCtrl.RESUME_CAPTION_LOADING, ClosedCaptionCtrl.RESUME_CAPTION_LOADING, ClosedCaptionCtrl.RESUME_CAPTION_LOADING, ClosedCaptionCtrl.RESUME_CAPTION_LOADING, ClosedCaptionCtrl.RESUME_CAPTION_LOADING, ClosedCaptionCtrl.RESUME_CAPTION_LOADING, ClosedCaptionCtrl.RESUME_CAPTION_LOADING};
        WAVE_SUBFORMAT_PCM = new UUID(72057594037932032L, -9223371306706625679L);
    }

    public WebmExtractor() {
        this(new DefaultEbmlReader());
    }

    WebmExtractor(EbmlReader ebmlReader) {
        this.segmentContentPosition = -1;
        this.segmentContentSize = -1;
        this.timecodeScale = -1;
        this.durationTimecode = -1;
        this.durationUs = -1;
        this.cuesContentPosition = -1;
        this.seekPositionAfterBuildingCues = -1;
        this.clusterTimecodeUs = -1;
        this.reader = ebmlReader;
        this.reader.init(new InnerEbmlReaderOutput());
        this.varintReader = new VarintReader();
        this.tracks = new SparseArray();
        this.scratch = new ParsableByteArray(4);
        this.vorbisNumPageSamples = new ParsableByteArray(ByteBuffer.allocate(4).putInt(UNKNOWN).array());
        this.seekEntryIdBytes = new ParsableByteArray(4);
        this.nalStartCode = new ParsableByteArray(NalUnitUtil.NAL_START_CODE);
        this.nalLength = new ParsableByteArray(4);
        this.sampleStrippedBytes = new ParsableByteArray();
        this.subripSample = new ParsableByteArray();
    }

    private SeekMap buildSeekMap() {
        int i = LACING_NONE;
        if (this.segmentContentPosition == -1 || this.durationUs == -1 || this.cueTimesUs == null || this.cueTimesUs.size() == 0 || this.cueClusterPositions == null || this.cueClusterPositions.size() != this.cueTimesUs.size()) {
            this.cueTimesUs = null;
            this.cueClusterPositions = null;
            return SeekMap.UNSEEKABLE;
        }
        int size = this.cueTimesUs.size();
        int[] iArr = new int[size];
        long[] jArr = new long[size];
        long[] jArr2 = new long[size];
        long[] jArr3 = new long[size];
        for (int i2 = LACING_NONE; i2 < size; i2 += WAVE_FORMAT_PCM) {
            jArr3[i2] = this.cueTimesUs.get(i2);
            jArr[i2] = this.segmentContentPosition + this.cueClusterPositions.get(i2);
        }
        while (i < size + UNKNOWN) {
            iArr[i] = (int) (jArr[i + WAVE_FORMAT_PCM] - jArr[i]);
            jArr2[i] = jArr3[i + WAVE_FORMAT_PCM] - jArr3[i];
            i += WAVE_FORMAT_PCM;
        }
        iArr[size + UNKNOWN] = (int) ((this.segmentContentPosition + this.segmentContentSize) - jArr[size + UNKNOWN]);
        jArr2[size + UNKNOWN] = this.durationUs - jArr3[size + UNKNOWN];
        this.cueTimesUs = null;
        this.cueClusterPositions = null;
        return new ChunkIndex(iArr, jArr, jArr2, jArr3);
    }

    private void commitSampleToOutput(Track track, long j) {
        if (CODEC_ID_SUBRIP.equals(track.codecId)) {
            writeSubripSample(track);
        }
        track.output.sampleMetadata(j, this.blockFlags, this.sampleBytesWritten, LACING_NONE, track.encryptionKeyId);
        this.sampleRead = true;
        resetSample();
    }

    private static int[] ensureArrayCapacity(int[] iArr, int i) {
        return iArr == null ? new int[i] : iArr.length < i ? new int[Math.max(iArr.length * TRACK_TYPE_AUDIO, i)] : iArr;
    }

    private static boolean isCodecSupported(String str) {
        return CODEC_ID_VP8.equals(str) || CODEC_ID_VP9.equals(str) || CODEC_ID_MPEG2.equals(str) || CODEC_ID_MPEG4_SP.equals(str) || CODEC_ID_MPEG4_ASP.equals(str) || CODEC_ID_MPEG4_AP.equals(str) || CODEC_ID_H264.equals(str) || CODEC_ID_H265.equals(str) || CODEC_ID_FOURCC.equals(str) || CODEC_ID_OPUS.equals(str) || CODEC_ID_VORBIS.equals(str) || CODEC_ID_AAC.equals(str) || CODEC_ID_MP3.equals(str) || CODEC_ID_AC3.equals(str) || CODEC_ID_E_AC3.equals(str) || CODEC_ID_TRUEHD.equals(str) || CODEC_ID_DTS.equals(str) || CODEC_ID_DTS_EXPRESS.equals(str) || CODEC_ID_DTS_LOSSLESS.equals(str) || CODEC_ID_FLAC.equals(str) || CODEC_ID_ACM.equals(str) || CODEC_ID_PCM_INT_LIT.equals(str) || CODEC_ID_SUBRIP.equals(str) || CODEC_ID_VOBSUB.equals(str) || CODEC_ID_PGS.equals(str);
    }

    private boolean maybeSeekForCues(PositionHolder positionHolder, long j) {
        if (this.seekForCues) {
            this.seekPositionAfterBuildingCues = j;
            positionHolder.position = this.cuesContentPosition;
            this.seekForCues = false;
            return true;
        } else if (!this.sentSeekMap || this.seekPositionAfterBuildingCues == -1) {
            return false;
        } else {
            positionHolder.position = this.seekPositionAfterBuildingCues;
            this.seekPositionAfterBuildingCues = -1;
            return true;
        }
    }

    private void readScratch(ExtractorInput extractorInput, int i) {
        if (this.scratch.limit() < i) {
            if (this.scratch.capacity() < i) {
                this.scratch.reset(Arrays.copyOf(this.scratch.data, Math.max(this.scratch.data.length * TRACK_TYPE_AUDIO, i)), this.scratch.limit());
            }
            extractorInput.readFully(this.scratch.data, this.scratch.limit(), i - this.scratch.limit());
            this.scratch.setLimit(i);
        }
    }

    private int readToOutput(ExtractorInput extractorInput, TrackOutput trackOutput, int i) {
        int bytesLeft = this.sampleStrippedBytes.bytesLeft();
        if (bytesLeft > 0) {
            bytesLeft = Math.min(i, bytesLeft);
            trackOutput.sampleData(this.sampleStrippedBytes, bytesLeft);
        } else {
            bytesLeft = trackOutput.sampleData(extractorInput, i, false);
        }
        this.sampleBytesRead += bytesLeft;
        this.sampleBytesWritten += bytesLeft;
        return bytesLeft;
    }

    private void readToTarget(ExtractorInput extractorInput, byte[] bArr, int i, int i2) {
        int min = Math.min(i2, this.sampleStrippedBytes.bytesLeft());
        extractorInput.readFully(bArr, i + min, i2 - min);
        if (min > 0) {
            this.sampleStrippedBytes.readBytes(bArr, i, min);
        }
        this.sampleBytesRead += i2;
    }

    private void resetSample() {
        this.sampleBytesRead = LACING_NONE;
        this.sampleBytesWritten = LACING_NONE;
        this.sampleCurrentNalBytesRemaining = LACING_NONE;
        this.sampleEncodingHandled = false;
        this.sampleStrippedBytes.reset();
    }

    private long scaleTimecodeToUs(long j) {
        if (this.timecodeScale == -1) {
            throw new ParserException("Can't scale timecode prior to timecodeScale being set.");
        }
        return Util.scaleLargeTimestamp(j, this.timecodeScale, 1000);
    }

    private static void setSubripSampleEndTimecode(byte[] bArr, long j) {
        Object obj;
        if (j == -1) {
            obj = SUBRIP_TIMECODE_EMPTY;
        } else {
            long j2 = j - (((long) ((int) (j / 3600000000L))) * 3600000000L);
            j2 -= (long) (60000000 * ((int) (j2 / 60000000)));
            int i = (int) ((j2 - ((long) (1000000 * ((int) (j2 / C0700C.MICROS_PER_SECOND))))) / 1000);
            obj = String.format(Locale.US, "%02d:%02d:%02d,%03d", new Object[]{Integer.valueOf((int) (j / 3600000000L)), Integer.valueOf(r1), Integer.valueOf(r4), Integer.valueOf(i)}).getBytes();
        }
        System.arraycopy(obj, LACING_NONE, bArr, SUBRIP_PREFIX_END_TIMECODE_OFFSET, SUBRIP_TIMECODE_LENGTH);
    }

    private void writeSampleData(ExtractorInput extractorInput, Track track, int i) {
        if (CODEC_ID_SUBRIP.equals(track.codecId)) {
            int length = SUBRIP_PREFIX.length + i;
            if (this.subripSample.capacity() < length) {
                this.subripSample.data = Arrays.copyOf(SUBRIP_PREFIX, length + i);
            }
            extractorInput.readFully(this.subripSample.data, SUBRIP_PREFIX.length, i);
            this.subripSample.setPosition(LACING_NONE);
            this.subripSample.setLimit(length);
            return;
        }
        TrackOutput trackOutput = track.output;
        if (!this.sampleEncodingHandled) {
            if (track.hasContentEncryption) {
                this.blockFlags &= -3;
                extractorInput.readFully(this.scratch.data, LACING_NONE, WAVE_FORMAT_PCM);
                this.sampleBytesRead += WAVE_FORMAT_PCM;
                if ((this.scratch.data[LACING_NONE] & TLRPC.USER_FLAG_UNUSED) == TLRPC.USER_FLAG_UNUSED) {
                    throw new ParserException("Extension bit is set in signal byte");
                } else if ((this.scratch.data[LACING_NONE] & WAVE_FORMAT_PCM) == WAVE_FORMAT_PCM) {
                    this.scratch.data[LACING_NONE] = (byte) 8;
                    this.scratch.setPosition(LACING_NONE);
                    trackOutput.sampleData(this.scratch, WAVE_FORMAT_PCM);
                    this.sampleBytesWritten += WAVE_FORMAT_PCM;
                    this.blockFlags |= TRACK_TYPE_AUDIO;
                }
            } else if (track.sampleStrippedBytes != null) {
                this.sampleStrippedBytes.reset(track.sampleStrippedBytes, track.sampleStrippedBytes.length);
            }
            this.sampleEncodingHandled = true;
        }
        int limit = this.sampleStrippedBytes.limit() + i;
        if (CODEC_ID_H264.equals(track.codecId) || CODEC_ID_H265.equals(track.codecId)) {
            byte[] bArr = this.nalLength.data;
            bArr[LACING_NONE] = (byte) 0;
            bArr[WAVE_FORMAT_PCM] = (byte) 0;
            bArr[TRACK_TYPE_AUDIO] = (byte) 0;
            int i2 = track.nalUnitLengthFieldLength;
            int i3 = 4 - track.nalUnitLengthFieldLength;
            while (this.sampleBytesRead < limit) {
                if (this.sampleCurrentNalBytesRemaining == 0) {
                    readToTarget(extractorInput, bArr, i3, i2);
                    this.nalLength.setPosition(LACING_NONE);
                    this.sampleCurrentNalBytesRemaining = this.nalLength.readUnsignedIntToInt();
                    this.nalStartCode.setPosition(LACING_NONE);
                    trackOutput.sampleData(this.nalStartCode, 4);
                    this.sampleBytesWritten += 4;
                } else {
                    this.sampleCurrentNalBytesRemaining -= readToOutput(extractorInput, trackOutput, this.sampleCurrentNalBytesRemaining);
                }
            }
        } else {
            while (this.sampleBytesRead < limit) {
                readToOutput(extractorInput, trackOutput, limit - this.sampleBytesRead);
            }
        }
        if (CODEC_ID_VORBIS.equals(track.codecId)) {
            this.vorbisNumPageSamples.setPosition(LACING_NONE);
            trackOutput.sampleData(this.vorbisNumPageSamples, 4);
            this.sampleBytesWritten += 4;
        }
    }

    private void writeSubripSample(Track track) {
        setSubripSampleEndTimecode(this.subripSample.data, this.blockDurationUs);
        track.output.sampleData(this.subripSample, this.subripSample.limit());
        this.sampleBytesWritten += this.subripSample.limit();
    }

    void binaryElement(int i, int i2, ExtractorInput extractorInput) {
        switch (i) {
            case ID_BLOCK /*161*/:
            case ID_SIMPLE_BLOCK /*163*/:
                if (this.blockState == 0) {
                    this.blockTrackNumber = (int) this.varintReader.readUnsignedVarint(extractorInput, false, true, ENCRYPTION_IV_SIZE);
                    this.blockTrackNumberLength = this.varintReader.getLastLength();
                    this.blockDurationUs = -1;
                    this.blockState = WAVE_FORMAT_PCM;
                    this.scratch.reset();
                }
                Track track = (Track) this.tracks.get(this.blockTrackNumber);
                if (track == null) {
                    extractorInput.skipFully(i2 - this.blockTrackNumberLength);
                    this.blockState = LACING_NONE;
                    return;
                }
                if (this.blockState == WAVE_FORMAT_PCM) {
                    readScratch(extractorInput, LACING_EBML);
                    int i3 = (this.scratch.data[TRACK_TYPE_AUDIO] & 6) >> WAVE_FORMAT_PCM;
                    if (i3 == 0) {
                        this.blockLacingSampleCount = WAVE_FORMAT_PCM;
                        this.blockLacingSampleSizes = ensureArrayCapacity(this.blockLacingSampleSizes, WAVE_FORMAT_PCM);
                        this.blockLacingSampleSizes[LACING_NONE] = (i2 - this.blockTrackNumberLength) - 3;
                    } else if (i != ID_SIMPLE_BLOCK) {
                        throw new ParserException("Lacing only supported in SimpleBlocks.");
                    } else {
                        readScratch(extractorInput, 4);
                        this.blockLacingSampleCount = (this.scratch.data[LACING_EBML] & NalUnitUtil.EXTENDED_SAR) + WAVE_FORMAT_PCM;
                        this.blockLacingSampleSizes = ensureArrayCapacity(this.blockLacingSampleSizes, this.blockLacingSampleCount);
                        if (i3 == TRACK_TYPE_AUDIO) {
                            Arrays.fill(this.blockLacingSampleSizes, LACING_NONE, this.blockLacingSampleCount, ((i2 - this.blockTrackNumberLength) - 4) / this.blockLacingSampleCount);
                        } else if (i3 == WAVE_FORMAT_PCM) {
                            r5 = LACING_NONE;
                            r4 = 4;
                            for (i3 = LACING_NONE; i3 < this.blockLacingSampleCount + UNKNOWN; i3 += WAVE_FORMAT_PCM) {
                                this.blockLacingSampleSizes[i3] = LACING_NONE;
                                do {
                                    r4 += WAVE_FORMAT_PCM;
                                    readScratch(extractorInput, r4);
                                    r6 = this.scratch.data[r4 + UNKNOWN] & NalUnitUtil.EXTENDED_SAR;
                                    r7 = this.blockLacingSampleSizes;
                                    r7[i3] = r7[i3] + r6;
                                } while (r6 == NalUnitUtil.EXTENDED_SAR);
                                r5 += this.blockLacingSampleSizes[i3];
                            }
                            this.blockLacingSampleSizes[this.blockLacingSampleCount + UNKNOWN] = ((i2 - this.blockTrackNumberLength) - r4) - r5;
                        } else if (i3 == LACING_EBML) {
                            r5 = LACING_NONE;
                            r4 = 4;
                            i3 = LACING_NONE;
                            while (i3 < this.blockLacingSampleCount + UNKNOWN) {
                                this.blockLacingSampleSizes[i3] = LACING_NONE;
                                r4 += WAVE_FORMAT_PCM;
                                readScratch(extractorInput, r4);
                                if (this.scratch.data[r4 + UNKNOWN] == null) {
                                    throw new ParserException("No valid varint length mask found");
                                }
                                long j = 0;
                                int i4 = LACING_NONE;
                                while (i4 < ENCRYPTION_IV_SIZE) {
                                    int i5 = WAVE_FORMAT_PCM << (7 - i4);
                                    if ((this.scratch.data[r4 + UNKNOWN] & i5) != 0) {
                                        int i6 = r4 + UNKNOWN;
                                        r4 += i4;
                                        readScratch(extractorInput, r4);
                                        j = (long) ((this.scratch.data[i6] & NalUnitUtil.EXTENDED_SAR) & (i5 ^ UNKNOWN));
                                        for (i5 = i6 + WAVE_FORMAT_PCM; i5 < r4; i5 += WAVE_FORMAT_PCM) {
                                            j = ((long) (this.scratch.data[i5] & NalUnitUtil.EXTENDED_SAR)) | (j << ENCRYPTION_IV_SIZE);
                                        }
                                        if (i3 > 0) {
                                            j -= (1 << ((i4 * 7) + 6)) - 1;
                                        }
                                        if (j >= -2147483648L || j > 2147483647L) {
                                            throw new ParserException("EBML lacing sample size out of range.");
                                        }
                                        r6 = (int) j;
                                        r7 = this.blockLacingSampleSizes;
                                        if (i3 != 0) {
                                            r6 += this.blockLacingSampleSizes[i3 + UNKNOWN];
                                        }
                                        r7[i3] = r6;
                                        r5 += this.blockLacingSampleSizes[i3];
                                        i3 += WAVE_FORMAT_PCM;
                                    } else {
                                        i4 += WAVE_FORMAT_PCM;
                                    }
                                }
                                if (j >= -2147483648L) {
                                    break;
                                }
                                throw new ParserException("EBML lacing sample size out of range.");
                            }
                            this.blockLacingSampleSizes[this.blockLacingSampleCount + UNKNOWN] = ((i2 - this.blockTrackNumberLength) - r4) - r5;
                        } else {
                            throw new ParserException("Unexpected lacing value: " + i3);
                        }
                    }
                    this.blockTimeUs = this.clusterTimecodeUs + scaleTimecodeToUs((long) ((this.scratch.data[LACING_NONE] << ENCRYPTION_IV_SIZE) | (this.scratch.data[WAVE_FORMAT_PCM] & NalUnitUtil.EXTENDED_SAR)));
                    Object obj = (this.scratch.data[TRACK_TYPE_AUDIO] & ENCRYPTION_IV_SIZE) == ENCRYPTION_IV_SIZE ? WAVE_FORMAT_PCM : null;
                    Object obj2 = (track.type == TRACK_TYPE_AUDIO || (i == ID_SIMPLE_BLOCK && (this.scratch.data[TRACK_TYPE_AUDIO] & TLRPC.USER_FLAG_UNUSED) == TLRPC.USER_FLAG_UNUSED)) ? WAVE_FORMAT_PCM : null;
                    this.blockFlags = (obj != null ? C0700C.SAMPLE_FLAG_DECODE_ONLY : LACING_NONE) | (obj2 != null ? WAVE_FORMAT_PCM : LACING_NONE);
                    this.blockState = TRACK_TYPE_AUDIO;
                    this.blockLacingSampleIndex = LACING_NONE;
                }
                if (i == ID_SIMPLE_BLOCK) {
                    while (this.blockLacingSampleIndex < this.blockLacingSampleCount) {
                        writeSampleData(extractorInput, track, this.blockLacingSampleSizes[this.blockLacingSampleIndex]);
                        commitSampleToOutput(track, this.blockTimeUs + ((long) ((this.blockLacingSampleIndex * track.defaultSampleDurationNs) / PointerIconCompat.TYPE_DEFAULT)));
                        this.blockLacingSampleIndex += WAVE_FORMAT_PCM;
                    }
                    this.blockState = LACING_NONE;
                    return;
                }
                writeSampleData(extractorInput, track, this.blockLacingSampleSizes[LACING_NONE]);
            case ID_CONTENT_COMPRESSION_SETTINGS /*16981*/:
                this.currentTrack.sampleStrippedBytes = new byte[i2];
                extractorInput.readFully(this.currentTrack.sampleStrippedBytes, LACING_NONE, i2);
            case ID_CONTENT_ENCRYPTION_KEY_ID /*18402*/:
                this.currentTrack.encryptionKeyId = new byte[i2];
                extractorInput.readFully(this.currentTrack.encryptionKeyId, LACING_NONE, i2);
            case ID_SEEK_ID /*21419*/:
                Arrays.fill(this.seekEntryIdBytes.data, (byte) 0);
                extractorInput.readFully(this.seekEntryIdBytes.data, 4 - i2, i2);
                this.seekEntryIdBytes.setPosition(LACING_NONE);
                this.seekEntryId = (int) this.seekEntryIdBytes.readUnsignedInt();
            case ID_CODEC_PRIVATE /*25506*/:
                this.currentTrack.codecPrivate = new byte[i2];
                extractorInput.readFully(this.currentTrack.codecPrivate, LACING_NONE, i2);
            default:
                throw new ParserException("Unexpected id: " + i);
        }
    }

    void endMasterElement(int i) {
        switch (i) {
            case ID_BLOCK_GROUP /*160*/:
                if (this.blockState == TRACK_TYPE_AUDIO) {
                    if (!this.sampleSeenReferenceBlock) {
                        this.blockFlags |= WAVE_FORMAT_PCM;
                    }
                    commitSampleToOutput((Track) this.tracks.get(this.blockTrackNumber), this.blockTimeUs);
                    this.blockState = LACING_NONE;
                }
            case ID_TRACK_ENTRY /*174*/:
                if (this.tracks.get(this.currentTrack.number) == null && isCodecSupported(this.currentTrack.codecId)) {
                    this.currentTrack.initializeOutput(this.extractorOutput, this.currentTrack.number, this.durationUs);
                    this.tracks.put(this.currentTrack.number, this.currentTrack);
                }
                this.currentTrack = null;
            case ID_SEEK /*19899*/:
                if (this.seekEntryId == UNKNOWN || this.seekEntryPosition == -1) {
                    throw new ParserException("Mandatory element SeekID or SeekPosition not found");
                } else if (this.seekEntryId == ID_CUES) {
                    this.cuesContentPosition = this.seekEntryPosition;
                }
            case ID_CONTENT_ENCODING /*25152*/:
                if (!this.currentTrack.hasContentEncryption) {
                    return;
                }
                if (this.currentTrack.encryptionKeyId == null) {
                    throw new ParserException("Encrypted Track found but ContentEncKeyID was not found");
                } else if (!this.sentDrmInitData) {
                    this.extractorOutput.drmInitData(new Universal(new SchemeInitData(MimeTypes.VIDEO_WEBM, this.currentTrack.encryptionKeyId)));
                    this.sentDrmInitData = true;
                }
            case ID_CONTENT_ENCODINGS /*28032*/:
                if (this.currentTrack.hasContentEncryption && this.currentTrack.sampleStrippedBytes != null) {
                    throw new ParserException("Combining encryption and compression is not supported");
                }
            case ID_SEGMENT_INFO /*357149030*/:
                if (this.timecodeScale == -1) {
                    this.timecodeScale = C0700C.MICROS_PER_SECOND;
                }
                if (this.durationTimecode != -1) {
                    this.durationUs = scaleTimecodeToUs(this.durationTimecode);
                }
            case ID_TRACKS /*374648427*/:
                if (this.tracks.size() == 0) {
                    throw new ParserException("No valid tracks were found");
                }
                this.extractorOutput.endTracks();
            case ID_CUES /*475249515*/:
                if (!this.sentSeekMap) {
                    this.extractorOutput.seekMap(buildSeekMap());
                    this.sentSeekMap = true;
                }
            default:
        }
    }

    void floatElement(int i, double d) {
        switch (i) {
            case ID_SAMPLING_FREQUENCY /*181*/:
                this.currentTrack.sampleRate = (int) d;
            case ID_DURATION /*17545*/:
                this.durationTimecode = (long) d;
            default:
        }
    }

    int getElementType(int i) {
        switch (i) {
            case ID_TRACK_TYPE /*131*/:
            case ID_BLOCK_DURATION /*155*/:
            case ID_CHANNELS /*159*/:
            case ID_PIXEL_WIDTH /*176*/:
            case ID_CUE_TIME /*179*/:
            case ID_PIXEL_HEIGHT /*186*/:
            case ID_TRACK_NUMBER /*215*/:
            case ID_TIME_CODE /*231*/:
            case ID_CUE_CLUSTER_POSITION /*241*/:
            case ID_REFERENCE_BLOCK /*251*/:
            case ID_CONTENT_COMPRESSION_ALGORITHM /*16980*/:
            case ID_DOC_TYPE_READ_VERSION /*17029*/:
            case ID_EBML_READ_VERSION /*17143*/:
            case ID_CONTENT_ENCRYPTION_ALGORITHM /*18401*/:
            case ID_CONTENT_ENCRYPTION_AES_SETTINGS_CIPHER_MODE /*18408*/:
            case ID_CONTENT_ENCODING_ORDER /*20529*/:
            case ID_CONTENT_ENCODING_SCOPE /*20530*/:
            case ID_SEEK_POSITION /*21420*/:
            case ID_DISPLAY_WIDTH /*21680*/:
            case ID_DISPLAY_UNIT /*21682*/:
            case ID_DISPLAY_HEIGHT /*21690*/:
            case ID_CODEC_DELAY /*22186*/:
            case ID_SEEK_PRE_ROLL /*22203*/:
            case ID_AUDIO_BIT_DEPTH /*25188*/:
            case ID_DEFAULT_DURATION /*2352003*/:
            case ID_TIMECODE_SCALE /*2807729*/:
                return TRACK_TYPE_AUDIO;
            case ID_CODEC_ID /*134*/:
            case ID_DOC_TYPE /*17026*/:
            case ID_LANGUAGE /*2274716*/:
                return LACING_EBML;
            case ID_BLOCK_GROUP /*160*/:
            case ID_TRACK_ENTRY /*174*/:
            case ID_CUE_TRACK_POSITIONS /*183*/:
            case ID_CUE_POINT /*187*/:
            case ID_VIDEO /*224*/:
            case ID_AUDIO /*225*/:
            case ID_CONTENT_ENCRYPTION_AES_SETTINGS /*18407*/:
            case ID_SEEK /*19899*/:
            case ID_CONTENT_COMPRESSION /*20532*/:
            case ID_CONTENT_ENCRYPTION /*20533*/:
            case ID_CONTENT_ENCODING /*25152*/:
            case ID_CONTENT_ENCODINGS /*28032*/:
            case ID_SEEK_HEAD /*290298740*/:
            case ID_SEGMENT_INFO /*357149030*/:
            case ID_TRACKS /*374648427*/:
            case ID_SEGMENT /*408125543*/:
            case ID_EBML /*440786851*/:
            case ID_CUES /*475249515*/:
            case ID_CLUSTER /*524531317*/:
                return WAVE_FORMAT_PCM;
            case ID_BLOCK /*161*/:
            case ID_SIMPLE_BLOCK /*163*/:
            case ID_CONTENT_COMPRESSION_SETTINGS /*16981*/:
            case ID_CONTENT_ENCRYPTION_KEY_ID /*18402*/:
            case ID_SEEK_ID /*21419*/:
            case ID_CODEC_PRIVATE /*25506*/:
                return 4;
            case ID_SAMPLING_FREQUENCY /*181*/:
            case ID_DURATION /*17545*/:
                return 5;
            default:
                return LACING_NONE;
        }
    }

    public void init(ExtractorOutput extractorOutput) {
        this.extractorOutput = extractorOutput;
    }

    void integerElement(int i, long j) {
        switch (i) {
            case ID_TRACK_TYPE /*131*/:
                this.currentTrack.type = (int) j;
            case ID_BLOCK_DURATION /*155*/:
                this.blockDurationUs = scaleTimecodeToUs(j);
            case ID_CHANNELS /*159*/:
                this.currentTrack.channelCount = (int) j;
            case ID_PIXEL_WIDTH /*176*/:
                this.currentTrack.width = (int) j;
            case ID_CUE_TIME /*179*/:
                this.cueTimesUs.add(scaleTimecodeToUs(j));
            case ID_PIXEL_HEIGHT /*186*/:
                this.currentTrack.height = (int) j;
            case ID_TRACK_NUMBER /*215*/:
                this.currentTrack.number = (int) j;
            case ID_TIME_CODE /*231*/:
                this.clusterTimecodeUs = scaleTimecodeToUs(j);
            case ID_CUE_CLUSTER_POSITION /*241*/:
                if (!this.seenClusterPositionForCurrentCuePoint) {
                    this.cueClusterPositions.add(j);
                    this.seenClusterPositionForCurrentCuePoint = true;
                }
            case ID_REFERENCE_BLOCK /*251*/:
                this.sampleSeenReferenceBlock = true;
            case ID_CONTENT_COMPRESSION_ALGORITHM /*16980*/:
                if (j != 3) {
                    throw new ParserException("ContentCompAlgo " + j + " not supported");
                }
            case ID_DOC_TYPE_READ_VERSION /*17029*/:
                if (j < 1 || j > 2) {
                    throw new ParserException("DocTypeReadVersion " + j + " not supported");
                }
            case ID_EBML_READ_VERSION /*17143*/:
                if (j != 1) {
                    throw new ParserException("EBMLReadVersion " + j + " not supported");
                }
            case ID_CONTENT_ENCRYPTION_ALGORITHM /*18401*/:
                if (j != 5) {
                    throw new ParserException("ContentEncAlgo " + j + " not supported");
                }
            case ID_CONTENT_ENCRYPTION_AES_SETTINGS_CIPHER_MODE /*18408*/:
                if (j != 1) {
                    throw new ParserException("AESSettingsCipherMode " + j + " not supported");
                }
            case ID_CONTENT_ENCODING_ORDER /*20529*/:
                if (j != 0) {
                    throw new ParserException("ContentEncodingOrder " + j + " not supported");
                }
            case ID_CONTENT_ENCODING_SCOPE /*20530*/:
                if (j != 1) {
                    throw new ParserException("ContentEncodingScope " + j + " not supported");
                }
            case ID_SEEK_POSITION /*21420*/:
                this.seekEntryPosition = this.segmentContentPosition + j;
            case ID_DISPLAY_WIDTH /*21680*/:
                this.currentTrack.displayWidth = (int) j;
            case ID_DISPLAY_UNIT /*21682*/:
                this.currentTrack.displayUnit = (int) j;
            case ID_DISPLAY_HEIGHT /*21690*/:
                this.currentTrack.displayHeight = (int) j;
            case ID_CODEC_DELAY /*22186*/:
                this.currentTrack.codecDelayNs = j;
            case ID_SEEK_PRE_ROLL /*22203*/:
                this.currentTrack.seekPreRollNs = j;
            case ID_AUDIO_BIT_DEPTH /*25188*/:
                this.currentTrack.audioBitDepth = (int) j;
            case ID_DEFAULT_DURATION /*2352003*/:
                this.currentTrack.defaultSampleDurationNs = (int) j;
            case ID_TIMECODE_SCALE /*2807729*/:
                this.timecodeScale = j;
            default:
        }
    }

    boolean isLevel1Element(int i) {
        return i == ID_SEGMENT_INFO || i == ID_CLUSTER || i == ID_CUES || i == ID_TRACKS;
    }

    public int read(ExtractorInput extractorInput, PositionHolder positionHolder) {
        this.sampleRead = false;
        boolean z = true;
        while (z && !this.sampleRead) {
            z = this.reader.read(extractorInput);
            if (z && maybeSeekForCues(positionHolder, extractorInput.getPosition())) {
                return WAVE_FORMAT_PCM;
            }
        }
        return !z ? UNKNOWN : LACING_NONE;
    }

    public void release() {
    }

    public void seek() {
        this.clusterTimecodeUs = -1;
        this.blockState = LACING_NONE;
        this.reader.reset();
        this.varintReader.reset();
        resetSample();
    }

    public boolean sniff(ExtractorInput extractorInput) {
        return new Sniffer().sniff(extractorInput);
    }

    void startMasterElement(int i, long j, long j2) {
        switch (i) {
            case ID_BLOCK_GROUP /*160*/:
                this.sampleSeenReferenceBlock = false;
            case ID_TRACK_ENTRY /*174*/:
                this.currentTrack = new Track();
            case ID_CUE_POINT /*187*/:
                this.seenClusterPositionForCurrentCuePoint = false;
            case ID_SEEK /*19899*/:
                this.seekEntryId = UNKNOWN;
                this.seekEntryPosition = -1;
            case ID_CONTENT_ENCRYPTION /*20533*/:
                this.currentTrack.hasContentEncryption = true;
            case ID_SEGMENT /*408125543*/:
                if (this.segmentContentPosition == -1 || this.segmentContentPosition == j) {
                    this.segmentContentPosition = j;
                    this.segmentContentSize = j2;
                    return;
                }
                throw new ParserException("Multiple Segment elements not supported");
            case ID_CUES /*475249515*/:
                this.cueTimesUs = new LongArray();
                this.cueClusterPositions = new LongArray();
            case ID_CLUSTER /*524531317*/:
                if (!this.sentSeekMap) {
                    if (this.cuesContentPosition != -1) {
                        this.seekForCues = true;
                        return;
                    }
                    this.extractorOutput.seekMap(SeekMap.UNSEEKABLE);
                    this.sentSeekMap = true;
                }
            default:
        }
    }

    void stringElement(int i, String str) {
        switch (i) {
            case ID_CODEC_ID /*134*/:
                this.currentTrack.codecId = str;
            case ID_DOC_TYPE /*17026*/:
                if (!DOC_TYPE_WEBM.equals(str) && !DOC_TYPE_MATROSKA.equals(str)) {
                    throw new ParserException("DocType " + str + " not supported");
                }
            case ID_LANGUAGE /*2274716*/:
                this.currentTrack.language = str;
            default:
        }
    }
}
