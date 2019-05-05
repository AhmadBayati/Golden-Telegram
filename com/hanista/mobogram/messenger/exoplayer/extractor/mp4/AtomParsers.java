package com.hanista.mobogram.messenger.exoplayer.extractor.mp4;

import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.util.Pair;
import com.googlecode.mp4parser.authoring.tracks.h265.NalUnitTypes;
import com.hanista.mobogram.messenger.exoplayer.C0700C;
import com.hanista.mobogram.messenger.exoplayer.MediaFormat;
import com.hanista.mobogram.messenger.exoplayer.ParserException;
import com.hanista.mobogram.messenger.exoplayer.extractor.GaplessInfo;
import com.hanista.mobogram.messenger.exoplayer.extractor.mp4.FixedSampleSizeRechunker.Results;
import com.hanista.mobogram.messenger.exoplayer.extractor.ts.PsExtractor;
import com.hanista.mobogram.messenger.exoplayer.util.Ac3Util;
import com.hanista.mobogram.messenger.exoplayer.util.Assertions;
import com.hanista.mobogram.messenger.exoplayer.util.CodecSpecificDataUtil;
import com.hanista.mobogram.messenger.exoplayer.util.MimeTypes;
import com.hanista.mobogram.messenger.exoplayer.util.NalUnitUtil;
import com.hanista.mobogram.messenger.exoplayer.util.ParsableBitArray;
import com.hanista.mobogram.messenger.exoplayer.util.ParsableByteArray;
import com.hanista.mobogram.messenger.exoplayer.util.Util;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;
import com.hanista.mobogram.tgnet.TLRPC;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

final class AtomParsers {

    private static final class AvcCData {
        public final List<byte[]> initializationData;
        public final int nalUnitLengthFieldLength;
        public final float pixelWidthAspectRatio;

        public AvcCData(List<byte[]> list, int i, float f) {
            this.initializationData = list;
            this.nalUnitLengthFieldLength = i;
            this.pixelWidthAspectRatio = f;
        }
    }

    private static final class ChunkIterator {
        private final ParsableByteArray chunkOffsets;
        private final boolean chunkOffsetsAreLongs;
        public int index;
        public final int length;
        private int nextSamplesPerChunkChangeIndex;
        public int numSamples;
        public long offset;
        private int remainingSamplesPerChunkChanges;
        private final ParsableByteArray stsc;

        public ChunkIterator(ParsableByteArray parsableByteArray, ParsableByteArray parsableByteArray2, boolean z) {
            boolean z2 = true;
            this.stsc = parsableByteArray;
            this.chunkOffsets = parsableByteArray2;
            this.chunkOffsetsAreLongs = z;
            parsableByteArray2.setPosition(12);
            this.length = parsableByteArray2.readUnsignedIntToInt();
            parsableByteArray.setPosition(12);
            this.remainingSamplesPerChunkChanges = parsableByteArray.readUnsignedIntToInt();
            if (parsableByteArray.readInt() != 1) {
                z2 = false;
            }
            Assertions.checkState(z2, "first_chunk must be 1");
            this.index = -1;
        }

        public boolean moveNext() {
            int i = this.index + 1;
            this.index = i;
            if (i == this.length) {
                return false;
            }
            this.offset = this.chunkOffsetsAreLongs ? this.chunkOffsets.readUnsignedLongToLong() : this.chunkOffsets.readUnsignedInt();
            if (this.index == this.nextSamplesPerChunkChangeIndex) {
                this.numSamples = this.stsc.readUnsignedIntToInt();
                this.stsc.skipBytes(4);
                i = this.remainingSamplesPerChunkChanges - 1;
                this.remainingSamplesPerChunkChanges = i;
                this.nextSamplesPerChunkChangeIndex = i > 0 ? this.stsc.readUnsignedIntToInt() - 1 : -1;
            }
            return true;
        }
    }

    private interface SampleSizeBox {
        int getSampleCount();

        boolean isFixedSampleSize();

        int readNextSampleSize();
    }

    private static final class StsdData {
        public MediaFormat mediaFormat;
        public int nalUnitLengthFieldLength;
        public final TrackEncryptionBox[] trackEncryptionBoxes;

        public StsdData(int i) {
            this.trackEncryptionBoxes = new TrackEncryptionBox[i];
            this.nalUnitLengthFieldLength = -1;
        }
    }

    static final class StszSampleSizeBox implements SampleSizeBox {
        private final ParsableByteArray data;
        private final int fixedSampleSize;
        private final int sampleCount;

        public StszSampleSizeBox(LeafAtom leafAtom) {
            this.data = leafAtom.data;
            this.data.setPosition(12);
            this.fixedSampleSize = this.data.readUnsignedIntToInt();
            this.sampleCount = this.data.readUnsignedIntToInt();
        }

        public int getSampleCount() {
            return this.sampleCount;
        }

        public boolean isFixedSampleSize() {
            return this.fixedSampleSize != 0;
        }

        public int readNextSampleSize() {
            return this.fixedSampleSize == 0 ? this.data.readUnsignedIntToInt() : this.fixedSampleSize;
        }
    }

    static final class Stz2SampleSizeBox implements SampleSizeBox {
        private int currentByte;
        private final ParsableByteArray data;
        private final int fieldSize;
        private final int sampleCount;
        private int sampleIndex;

        public Stz2SampleSizeBox(LeafAtom leafAtom) {
            this.data = leafAtom.data;
            this.data.setPosition(12);
            this.fieldSize = this.data.readUnsignedIntToInt() & NalUnitUtil.EXTENDED_SAR;
            this.sampleCount = this.data.readUnsignedIntToInt();
        }

        public int getSampleCount() {
            return this.sampleCount;
        }

        public boolean isFixedSampleSize() {
            return false;
        }

        public int readNextSampleSize() {
            if (this.fieldSize == 8) {
                return this.data.readUnsignedByte();
            }
            if (this.fieldSize == 16) {
                return this.data.readUnsignedShort();
            }
            int i = this.sampleIndex;
            this.sampleIndex = i + 1;
            if (i % 2 != 0) {
                return this.currentByte & 15;
            }
            this.currentByte = this.data.readUnsignedByte();
            return (this.currentByte & PsExtractor.VIDEO_STREAM_MASK) >> 4;
        }
    }

    private static final class TkhdData {
        private final long duration;
        private final int id;
        private final int rotationDegrees;

        public TkhdData(int i, long j, int i2) {
            this.id = i;
            this.duration = j;
            this.rotationDegrees = i2;
        }
    }

    private AtomParsers() {
    }

    private static int findEsdsPosition(ParsableByteArray parsableByteArray, int i, int i2) {
        int position = parsableByteArray.getPosition();
        while (position - i < i2) {
            parsableByteArray.setPosition(position);
            int readInt = parsableByteArray.readInt();
            Assertions.checkArgument(readInt > 0, "childAtomSize should be positive");
            if (parsableByteArray.readInt() == Atom.TYPE_esds) {
                return position;
            }
            position += readInt;
        }
        return -1;
    }

    private static void parseAudioSampleEntry(ParsableByteArray parsableByteArray, int i, int i2, int i3, int i4, long j, String str, boolean z, StsdData stsdData, int i5) {
        int readUnsignedShort;
        int i6;
        int readUnsignedShort2;
        parsableByteArray.setPosition(i2 + 8);
        if (z) {
            parsableByteArray.skipBytes(8);
            readUnsignedShort = parsableByteArray.readUnsignedShort();
            parsableByteArray.skipBytes(6);
            i6 = readUnsignedShort;
        } else {
            parsableByteArray.skipBytes(16);
            i6 = 0;
        }
        if (i6 == 0 || i6 == 1) {
            readUnsignedShort2 = parsableByteArray.readUnsignedShort();
            parsableByteArray.skipBytes(6);
            readUnsignedShort = parsableByteArray.readUnsignedFixedPoint1616();
            if (i6 == 1) {
                parsableByteArray.skipBytes(16);
            }
        } else if (i6 == 2) {
            parsableByteArray.skipBytes(16);
            readUnsignedShort = (int) Math.round(parsableByteArray.readDouble());
            readUnsignedShort2 = parsableByteArray.readUnsignedIntToInt();
            parsableByteArray.skipBytes(20);
        } else {
            return;
        }
        int position = parsableByteArray.getPosition();
        if (i == Atom.TYPE_enca) {
            i = parseSampleEntryEncryptionData(parsableByteArray, i2, i3, stsdData, i5);
            parsableByteArray.setPosition(position);
        }
        String str2 = null;
        if (i == Atom.TYPE_ac_3) {
            str2 = MimeTypes.AUDIO_AC3;
        } else if (i == Atom.TYPE_ec_3) {
            str2 = MimeTypes.AUDIO_E_AC3;
        } else if (i == Atom.TYPE_dtsc) {
            str2 = MimeTypes.AUDIO_DTS;
        } else if (i == Atom.TYPE_dtsh || i == Atom.TYPE_dtsl) {
            str2 = MimeTypes.AUDIO_DTS_HD;
        } else if (i == Atom.TYPE_dtse) {
            str2 = MimeTypes.AUDIO_DTS_EXPRESS;
        } else if (i == Atom.TYPE_samr) {
            str2 = MimeTypes.AUDIO_AMR_NB;
        } else if (i == Atom.TYPE_sawb) {
            str2 = MimeTypes.AUDIO_AMR_WB;
        } else if (i == Atom.TYPE_lpcm || i == Atom.TYPE_sowt) {
            str2 = MimeTypes.AUDIO_RAW;
        }
        Object obj = null;
        int i7 = readUnsignedShort;
        int i8 = readUnsignedShort2;
        String str3 = str2;
        while (position - i2 < i3) {
            parsableByteArray.setPosition(position);
            int readInt = parsableByteArray.readInt();
            Assertions.checkArgument(readInt > 0, "childAtomSize should be positive");
            readUnsignedShort = parsableByteArray.readInt();
            if (readUnsignedShort == Atom.TYPE_esds || (z && readUnsignedShort == Atom.TYPE_wave)) {
                Object obj2;
                readUnsignedShort = readUnsignedShort == Atom.TYPE_esds ? position : findEsdsPosition(parsableByteArray, position, readInt);
                if (readUnsignedShort != -1) {
                    Pair parseEsdsFromParent = parseEsdsFromParent(parsableByteArray, readUnsignedShort);
                    str3 = (String) parseEsdsFromParent.first;
                    obj2 = (byte[]) parseEsdsFromParent.second;
                    if (MimeTypes.AUDIO_AAC.equals(str3)) {
                        Pair parseAacAudioSpecificConfig = CodecSpecificDataUtil.parseAacAudioSpecificConfig(obj2);
                        i7 = ((Integer) parseAacAudioSpecificConfig.first).intValue();
                        i8 = ((Integer) parseAacAudioSpecificConfig.second).intValue();
                    }
                } else {
                    obj2 = obj;
                }
                obj = obj2;
            } else if (readUnsignedShort == Atom.TYPE_dac3) {
                parsableByteArray.setPosition(position + 8);
                stsdData.mediaFormat = Ac3Util.parseAc3AnnexFFormat(parsableByteArray, Integer.toString(i4), j, str);
            } else if (readUnsignedShort == Atom.TYPE_dec3) {
                parsableByteArray.setPosition(position + 8);
                stsdData.mediaFormat = Ac3Util.parseEAc3AnnexFFormat(parsableByteArray, Integer.toString(i4), j, str);
            } else if (readUnsignedShort == Atom.TYPE_ddts) {
                stsdData.mediaFormat = MediaFormat.createAudioFormat(Integer.toString(i4), str3, -1, -1, j, i8, i7, null, str);
            }
            position += readInt;
        }
        if (stsdData.mediaFormat == null && str3 != null) {
            stsdData.mediaFormat = MediaFormat.createAudioFormat(Integer.toString(i4), str3, -1, -1, j, i8, i7, obj == null ? null : Collections.singletonList(obj), str, MimeTypes.AUDIO_RAW.equals(str3) ? 2 : -1);
        }
    }

    private static AvcCData parseAvcCFromParent(ParsableByteArray parsableByteArray, int i) {
        parsableByteArray.setPosition((i + 8) + 4);
        int readUnsignedByte = (parsableByteArray.readUnsignedByte() & 3) + 1;
        if (readUnsignedByte == 3) {
            throw new IllegalStateException();
        }
        int i2;
        List arrayList = new ArrayList();
        float f = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
        int readUnsignedByte2 = parsableByteArray.readUnsignedByte() & 31;
        for (i2 = 0; i2 < readUnsignedByte2; i2++) {
            arrayList.add(NalUnitUtil.parseChildNalUnit(parsableByteArray));
        }
        int readUnsignedByte3 = parsableByteArray.readUnsignedByte();
        for (i2 = 0; i2 < readUnsignedByte3; i2++) {
            arrayList.add(NalUnitUtil.parseChildNalUnit(parsableByteArray));
        }
        if (readUnsignedByte2 > 0) {
            ParsableBitArray parsableBitArray = new ParsableBitArray((byte[]) arrayList.get(0));
            parsableBitArray.setPosition((readUnsignedByte + 1) * 8);
            f = NalUnitUtil.parseSpsNalUnit(parsableBitArray).pixelWidthAspectRatio;
        }
        return new AvcCData(arrayList, readUnsignedByte, f);
    }

    private static Pair<long[], long[]> parseEdts(ContainerAtom containerAtom) {
        if (containerAtom != null) {
            LeafAtom leafAtomOfType = containerAtom.getLeafAtomOfType(Atom.TYPE_elst);
            if (leafAtomOfType != null) {
                ParsableByteArray parsableByteArray = leafAtomOfType.data;
                parsableByteArray.setPosition(8);
                int parseFullAtomVersion = Atom.parseFullAtomVersion(parsableByteArray.readInt());
                int readUnsignedIntToInt = parsableByteArray.readUnsignedIntToInt();
                Object obj = new long[readUnsignedIntToInt];
                Object obj2 = new long[readUnsignedIntToInt];
                for (int i = 0; i < readUnsignedIntToInt; i++) {
                    obj[i] = parseFullAtomVersion == 1 ? parsableByteArray.readUnsignedLongToLong() : parsableByteArray.readUnsignedInt();
                    obj2[i] = parseFullAtomVersion == 1 ? parsableByteArray.readLong() : (long) parsableByteArray.readInt();
                    if (parsableByteArray.readShort() != (short) 1) {
                        throw new IllegalArgumentException("Unsupported media rate.");
                    }
                    parsableByteArray.skipBytes(2);
                }
                return Pair.create(obj, obj2);
            }
        }
        return Pair.create(null, null);
    }

    private static Pair<String, byte[]> parseEsdsFromParent(ParsableByteArray parsableByteArray, int i) {
        Object obj = null;
        parsableByteArray.setPosition((i + 8) + 4);
        parsableByteArray.skipBytes(1);
        parseExpandableClassSize(parsableByteArray);
        parsableByteArray.skipBytes(2);
        int readUnsignedByte = parsableByteArray.readUnsignedByte();
        if ((readUnsignedByte & TLRPC.USER_FLAG_UNUSED) != 0) {
            parsableByteArray.skipBytes(2);
        }
        if ((readUnsignedByte & 64) != 0) {
            parsableByteArray.skipBytes(parsableByteArray.readUnsignedShort());
        }
        if ((readUnsignedByte & 32) != 0) {
            parsableByteArray.skipBytes(2);
        }
        parsableByteArray.skipBytes(1);
        parseExpandableClassSize(parsableByteArray);
        switch (parsableByteArray.readUnsignedByte()) {
            case TLRPC.USER_FLAG_PHOTO /*32*/:
                obj = MimeTypes.VIDEO_MP4V;
                break;
            case NalUnitTypes.NAL_TYPE_SPS_NUT /*33*/:
                obj = MimeTypes.VIDEO_H264;
                break;
            case NalUnitTypes.NAL_TYPE_AUD_NUT /*35*/:
                obj = MimeTypes.VIDEO_H265;
                break;
            case TLRPC.USER_FLAG_STATUS /*64*/:
            case 102:
            case 103:
            case 104:
                obj = MimeTypes.AUDIO_AAC;
                break;
            case 107:
                return Pair.create(MimeTypes.AUDIO_MPEG, null);
            case 165:
                obj = MimeTypes.AUDIO_AC3;
                break;
            case 166:
                obj = MimeTypes.AUDIO_E_AC3;
                break;
            case 169:
            case 172:
                return Pair.create(MimeTypes.AUDIO_DTS, null);
            case 170:
            case 171:
                return Pair.create(MimeTypes.AUDIO_DTS_HD, null);
        }
        parsableByteArray.skipBytes(12);
        parsableByteArray.skipBytes(1);
        readUnsignedByte = parseExpandableClassSize(parsableByteArray);
        Object obj2 = new byte[readUnsignedByte];
        parsableByteArray.readBytes(obj2, 0, readUnsignedByte);
        return Pair.create(obj, obj2);
    }

    private static int parseExpandableClassSize(ParsableByteArray parsableByteArray) {
        int readUnsignedByte = parsableByteArray.readUnsignedByte();
        int i = readUnsignedByte & 127;
        while ((readUnsignedByte & TLRPC.USER_FLAG_UNUSED) == TLRPC.USER_FLAG_UNUSED) {
            readUnsignedByte = parsableByteArray.readUnsignedByte();
            i = (i << 7) | (readUnsignedByte & 127);
        }
        return i;
    }

    private static int parseHdlr(ParsableByteArray parsableByteArray) {
        parsableByteArray.setPosition(16);
        return parsableByteArray.readInt();
    }

    private static Pair<List<byte[]>, Integer> parseHvcCFromParent(ParsableByteArray parsableByteArray, int i) {
        int i2;
        parsableByteArray.setPosition((i + 8) + 21);
        int readUnsignedByte = parsableByteArray.readUnsignedByte() & 3;
        int readUnsignedByte2 = parsableByteArray.readUnsignedByte();
        int position = parsableByteArray.getPosition();
        int i3 = 0;
        int i4 = 0;
        while (i3 < readUnsignedByte2) {
            parsableByteArray.skipBytes(1);
            int readUnsignedShort = parsableByteArray.readUnsignedShort();
            int i5 = i4;
            for (i2 = 0; i2 < readUnsignedShort; i2++) {
                i4 = parsableByteArray.readUnsignedShort();
                i5 += i4 + 4;
                parsableByteArray.skipBytes(i4);
            }
            i3++;
            i4 = i5;
        }
        parsableByteArray.setPosition(position);
        Object obj = new byte[i4];
        i3 = 0;
        i2 = 0;
        while (i3 < readUnsignedByte2) {
            parsableByteArray.skipBytes(1);
            readUnsignedShort = parsableByteArray.readUnsignedShort();
            i5 = i2;
            for (i2 = 0; i2 < readUnsignedShort; i2++) {
                int readUnsignedShort2 = parsableByteArray.readUnsignedShort();
                System.arraycopy(NalUnitUtil.NAL_START_CODE, 0, obj, i5, NalUnitUtil.NAL_START_CODE.length);
                i5 += NalUnitUtil.NAL_START_CODE.length;
                System.arraycopy(parsableByteArray.data, parsableByteArray.getPosition(), obj, i5, readUnsignedShort2);
                i5 += readUnsignedShort2;
                parsableByteArray.skipBytes(readUnsignedShort2);
            }
            i3++;
            i2 = i5;
        }
        return Pair.create(i4 == 0 ? null : Collections.singletonList(obj), Integer.valueOf(readUnsignedByte + 1));
    }

    private static GaplessInfo parseIlst(ParsableByteArray parsableByteArray) {
        while (parsableByteArray.bytesLeft() > 0) {
            int position = parsableByteArray.getPosition() + parsableByteArray.readInt();
            if (parsableByteArray.readInt() == Atom.TYPE_DASHES) {
                String str = null;
                String str2 = null;
                Object obj = null;
                while (parsableByteArray.getPosition() < position) {
                    int readInt = parsableByteArray.readInt() - 12;
                    int readInt2 = parsableByteArray.readInt();
                    parsableByteArray.skipBytes(4);
                    if (readInt2 == Atom.TYPE_mean) {
                        obj = parsableByteArray.readString(readInt);
                    } else if (readInt2 == Atom.TYPE_name) {
                        str2 = parsableByteArray.readString(readInt);
                    } else if (readInt2 == Atom.TYPE_data) {
                        parsableByteArray.skipBytes(4);
                        str = parsableByteArray.readString(readInt - 4);
                    } else {
                        parsableByteArray.skipBytes(readInt);
                    }
                }
                if (!(str2 == null || str == null || !"com.apple.iTunes".equals(r2))) {
                    return GaplessInfo.createFromComment(str2, str);
                }
            }
            parsableByteArray.setPosition(position);
        }
        return null;
    }

    private static Pair<Long, String> parseMdhd(ParsableByteArray parsableByteArray) {
        int i = 8;
        parsableByteArray.setPosition(8);
        int parseFullAtomVersion = Atom.parseFullAtomVersion(parsableByteArray.readInt());
        parsableByteArray.skipBytes(parseFullAtomVersion == 0 ? 8 : 16);
        long readUnsignedInt = parsableByteArray.readUnsignedInt();
        if (parseFullAtomVersion == 0) {
            i = 4;
        }
        parsableByteArray.skipBytes(i);
        int readUnsignedShort = parsableByteArray.readUnsignedShort();
        return Pair.create(Long.valueOf(readUnsignedInt), TtmlNode.ANONYMOUS_REGION_ID + ((char) (((readUnsignedShort >> 10) & 31) + 96)) + ((char) (((readUnsignedShort >> 5) & 31) + 96)) + ((char) ((readUnsignedShort & 31) + 96)));
    }

    private static GaplessInfo parseMetaAtom(ParsableByteArray parsableByteArray) {
        parsableByteArray.skipBytes(12);
        ParsableByteArray parsableByteArray2 = new ParsableByteArray();
        while (parsableByteArray.bytesLeft() >= 8) {
            int readInt = parsableByteArray.readInt() - 8;
            if (parsableByteArray.readInt() == Atom.TYPE_ilst) {
                parsableByteArray2.reset(parsableByteArray.data, parsableByteArray.getPosition() + readInt);
                parsableByteArray2.setPosition(parsableByteArray.getPosition());
                GaplessInfo parseIlst = parseIlst(parsableByteArray2);
                if (parseIlst != null) {
                    return parseIlst;
                }
            }
            parsableByteArray.skipBytes(readInt);
        }
        return null;
    }

    private static long parseMvhd(ParsableByteArray parsableByteArray) {
        int i = 8;
        parsableByteArray.setPosition(8);
        if (Atom.parseFullAtomVersion(parsableByteArray.readInt()) != 0) {
            i = 16;
        }
        parsableByteArray.skipBytes(i);
        return parsableByteArray.readUnsignedInt();
    }

    private static float parsePaspFromParent(ParsableByteArray parsableByteArray, int i) {
        parsableByteArray.setPosition(i + 8);
        return ((float) parsableByteArray.readUnsignedIntToInt()) / ((float) parsableByteArray.readUnsignedIntToInt());
    }

    private static int parseSampleEntryEncryptionData(ParsableByteArray parsableByteArray, int i, int i2, StsdData stsdData, int i3) {
        boolean z = true;
        int position = parsableByteArray.getPosition();
        while (position - i < i2) {
            parsableByteArray.setPosition(position);
            int readInt = parsableByteArray.readInt();
            Assertions.checkArgument(readInt > 0, "childAtomSize should be positive");
            if (parsableByteArray.readInt() == Atom.TYPE_sinf) {
                Pair parseSinfFromParent = parseSinfFromParent(parsableByteArray, position, readInt);
                Integer num = (Integer) parseSinfFromParent.first;
                if (num == null) {
                    z = false;
                }
                Assertions.checkArgument(z, "frma atom is mandatory");
                stsdData.trackEncryptionBoxes[i3] = (TrackEncryptionBox) parseSinfFromParent.second;
                return num.intValue();
            }
            position += readInt;
        }
        return 0;
    }

    private static TrackEncryptionBox parseSchiFromParent(ParsableByteArray parsableByteArray, int i, int i2) {
        boolean z = true;
        int i3 = i + 8;
        while (i3 - i < i2) {
            parsableByteArray.setPosition(i3);
            int readInt = parsableByteArray.readInt();
            if (parsableByteArray.readInt() == Atom.TYPE_tenc) {
                parsableByteArray.skipBytes(6);
                if (parsableByteArray.readUnsignedByte() != 1) {
                    z = false;
                }
                i3 = parsableByteArray.readUnsignedByte();
                byte[] bArr = new byte[16];
                parsableByteArray.readBytes(bArr, 0, bArr.length);
                return new TrackEncryptionBox(z, i3, bArr);
            }
            i3 += readInt;
        }
        return null;
    }

    private static Pair<Integer, TrackEncryptionBox> parseSinfFromParent(ParsableByteArray parsableByteArray, int i, int i2) {
        Object obj = null;
        int i3 = i + 8;
        Object obj2 = null;
        while (i3 - i < i2) {
            parsableByteArray.setPosition(i3);
            int readInt = parsableByteArray.readInt();
            int readInt2 = parsableByteArray.readInt();
            if (readInt2 == Atom.TYPE_frma) {
                obj = Integer.valueOf(parsableByteArray.readInt());
            } else if (readInt2 == Atom.TYPE_schm) {
                parsableByteArray.skipBytes(4);
                parsableByteArray.readInt();
                parsableByteArray.readInt();
            } else if (readInt2 == Atom.TYPE_schi) {
                obj2 = parseSchiFromParent(parsableByteArray, i3, readInt);
            }
            i3 += readInt;
        }
        return Pair.create(obj, obj2);
    }

    public static TrackSampleTable parseStbl(Track track, ContainerAtom containerAtom) {
        SampleSizeBox stszSampleSizeBox;
        LeafAtom leafAtomOfType = containerAtom.getLeafAtomOfType(Atom.TYPE_stsz);
        if (leafAtomOfType != null) {
            stszSampleSizeBox = new StszSampleSizeBox(leafAtomOfType);
        } else {
            leafAtomOfType = containerAtom.getLeafAtomOfType(Atom.TYPE_stz2);
            if (leafAtomOfType == null) {
                throw new ParserException("Track has no sample table size information");
            }
            stszSampleSizeBox = new Stz2SampleSizeBox(leafAtomOfType);
        }
        int sampleCount = stszSampleSizeBox.getSampleCount();
        if (sampleCount == 0) {
            return new TrackSampleTable(new long[0], new int[0], 0, new long[0], new int[0]);
        }
        int readUnsignedIntToInt;
        int i;
        int i2;
        long j;
        int i3;
        int i4;
        int i5;
        int i6;
        long[] jArr;
        int i7;
        Object obj;
        Object obj2;
        boolean z = false;
        leafAtomOfType = containerAtom.getLeafAtomOfType(Atom.TYPE_stco);
        if (leafAtomOfType == null) {
            z = true;
            leafAtomOfType = containerAtom.getLeafAtomOfType(Atom.TYPE_co64);
        }
        ParsableByteArray parsableByteArray = leafAtomOfType.data;
        ParsableByteArray parsableByteArray2 = containerAtom.getLeafAtomOfType(Atom.TYPE_stsc).data;
        ParsableByteArray parsableByteArray3 = containerAtom.getLeafAtomOfType(Atom.TYPE_stts).data;
        leafAtomOfType = containerAtom.getLeafAtomOfType(Atom.TYPE_stss);
        ParsableByteArray parsableByteArray4 = leafAtomOfType != null ? leafAtomOfType.data : null;
        LeafAtom leafAtomOfType2 = containerAtom.getLeafAtomOfType(Atom.TYPE_ctts);
        ParsableByteArray parsableByteArray5 = leafAtomOfType2 != null ? leafAtomOfType2.data : null;
        ChunkIterator chunkIterator = new ChunkIterator(parsableByteArray2, parsableByteArray, z);
        parsableByteArray3.setPosition(12);
        int readUnsignedIntToInt2 = parsableByteArray3.readUnsignedIntToInt() - 1;
        int readUnsignedIntToInt3 = parsableByteArray3.readUnsignedIntToInt();
        int readUnsignedIntToInt4 = parsableByteArray3.readUnsignedIntToInt();
        int i8 = 0;
        if (parsableByteArray5 != null) {
            parsableByteArray5.setPosition(12);
            i8 = parsableByteArray5.readUnsignedIntToInt();
        }
        if (parsableByteArray4 != null) {
            parsableByteArray4.setPosition(12);
            readUnsignedIntToInt = parsableByteArray4.readUnsignedIntToInt();
            if (readUnsignedIntToInt > 0) {
                i = readUnsignedIntToInt;
                readUnsignedIntToInt = parsableByteArray4.readUnsignedIntToInt() - 1;
                parsableByteArray = parsableByteArray4;
                i2 = i;
            } else {
                i = readUnsignedIntToInt;
                readUnsignedIntToInt = -1;
                parsableByteArray = null;
                i2 = i;
            }
        } else {
            readUnsignedIntToInt = -1;
            parsableByteArray = parsableByteArray4;
            i2 = 0;
        }
        Object obj3 = (stszSampleSizeBox.isFixedSampleSize() && MimeTypes.AUDIO_RAW.equals(track.mediaFormat.mimeType) && readUnsignedIntToInt2 == 0 && i8 == 0 && i2 == 0) ? 1 : null;
        int i9 = 0;
        if (obj3 == null) {
            Object obj4 = new long[sampleCount];
            Object obj5 = new int[sampleCount];
            long[] jArr2 = new long[sampleCount];
            Object obj6 = new int[sampleCount];
            j = 0;
            i3 = 0;
            long j2 = 0;
            i4 = readUnsignedIntToInt4;
            i5 = readUnsignedIntToInt2;
            readUnsignedIntToInt2 = readUnsignedIntToInt3;
            readUnsignedIntToInt3 = 0;
            int i10 = 0;
            i = i8;
            i8 = readUnsignedIntToInt;
            readUnsignedIntToInt = i;
            i6 = i2;
            i2 = 0;
            while (readUnsignedIntToInt3 < sampleCount) {
                long j3 = j;
                int i11 = i3;
                while (i11 == 0) {
                    Assertions.checkState(chunkIterator.moveNext());
                    j3 = chunkIterator.offset;
                    i11 = chunkIterator.numSamples;
                }
                if (parsableByteArray5 != null) {
                    while (i10 == 0 && readUnsignedIntToInt > 0) {
                        i10 = parsableByteArray5.readUnsignedIntToInt();
                        i2 = parsableByteArray5.readInt();
                        readUnsignedIntToInt--;
                    }
                    i10--;
                }
                obj4[readUnsignedIntToInt3] = j3;
                obj5[readUnsignedIntToInt3] = stszSampleSizeBox.readNextSampleSize();
                if (obj5[readUnsignedIntToInt3] > i9) {
                    i9 = obj5[readUnsignedIntToInt3];
                }
                jArr2[readUnsignedIntToInt3] = ((long) i2) + j2;
                obj6[readUnsignedIntToInt3] = parsableByteArray == null ? 1 : 0;
                if (readUnsignedIntToInt3 == i8) {
                    obj6[readUnsignedIntToInt3] = 1;
                    i3 = i6 - 1;
                    if (i3 > 0) {
                        i8 = parsableByteArray.readUnsignedIntToInt() - 1;
                        i6 = i3;
                    } else {
                        i6 = i3;
                    }
                }
                j2 += (long) i4;
                i3 = readUnsignedIntToInt2 - 1;
                if (i3 != 0 || i5 <= 0) {
                    i = i4;
                    i4 = i3;
                    i3 = i;
                } else {
                    i4 = parsableByteArray3.readUnsignedIntToInt();
                    i3 = parsableByteArray3.readUnsignedIntToInt();
                    i5--;
                }
                long j4 = (long) obj5[readUnsignedIntToInt3];
                readUnsignedIntToInt3++;
                readUnsignedIntToInt2 = i4;
                i4 = i3;
                i3 = i11 - 1;
                j = j3 + r0;
            }
            Assertions.checkArgument(i10 == 0);
            while (readUnsignedIntToInt > 0) {
                Assertions.checkArgument(parsableByteArray5.readUnsignedIntToInt() == 0);
                parsableByteArray5.readInt();
                readUnsignedIntToInt--;
            }
            Assertions.checkArgument(i6 == 0);
            Assertions.checkArgument(readUnsignedIntToInt2 == 0);
            Assertions.checkArgument(i3 == 0);
            Assertions.checkArgument(i5 == 0);
            obj3 = obj6;
            jArr = jArr2;
            i7 = i9;
            obj = obj5;
            obj2 = obj4;
        } else {
            long[] jArr3 = new long[chunkIterator.length];
            int[] iArr = new int[chunkIterator.length];
            while (chunkIterator.moveNext()) {
                jArr3[chunkIterator.index] = chunkIterator.offset;
                iArr[chunkIterator.index] = chunkIterator.numSamples;
            }
            Results rechunk = FixedSampleSizeRechunker.rechunk(stszSampleSizeBox.readNextSampleSize(), jArr3, iArr, (long) readUnsignedIntToInt4);
            obj2 = rechunk.offsets;
            obj = rechunk.sizes;
            i7 = rechunk.maximumSize;
            jArr = rechunk.timestamps;
            obj3 = rechunk.flags;
        }
        if (track.editListDurations == null) {
            Util.scaleLargeTimestampsInPlace(jArr, C0700C.MICROS_PER_SECOND, track.timescale);
            return new TrackSampleTable(obj2, obj, i7, jArr, obj3);
        } else if (track.editListDurations.length == 1 && track.editListDurations[0] == 0) {
            for (r2 = 0; r2 < jArr.length; r2++) {
                jArr[r2] = Util.scaleLargeTimestamp(jArr[r2] - track.editListMediaTimes[0], C0700C.MICROS_PER_SECOND, track.timescale);
            }
            return new TrackSampleTable(obj2, obj, i7, jArr, obj3);
        } else {
            long scaleLargeTimestamp;
            r2 = 0;
            int i12 = 0;
            int i13 = 0;
            i6 = 0;
            while (r2 < track.editListDurations.length) {
                j = track.editListMediaTimes[r2];
                if (j != -1) {
                    scaleLargeTimestamp = Util.scaleLargeTimestamp(track.editListDurations[r2], track.timescale, track.movieTimescale);
                    i5 = Util.binarySearchCeil(jArr, j, true, true);
                    i3 = Util.binarySearchCeil(jArr, scaleLargeTimestamp + j, true, false);
                    i4 = i6 + (i3 - i5);
                    i9 = (i13 != i5 ? 1 : 0) | i12;
                } else {
                    i9 = i12;
                    i3 = i13;
                    i4 = i6;
                }
                r2++;
                i12 = i9;
                i13 = i3;
                i6 = i4;
            }
            readUnsignedIntToInt2 = i12 | (i6 != sampleCount ? 1 : 0);
            Object obj7 = readUnsignedIntToInt2 != 0 ? new long[i6] : obj2;
            Object obj8 = readUnsignedIntToInt2 != 0 ? new int[i6] : obj;
            i4 = readUnsignedIntToInt2 != 0 ? 0 : i7;
            Object obj9 = readUnsignedIntToInt2 != 0 ? new int[i6] : obj3;
            long[] jArr4 = new long[i6];
            r2 = 0;
            i12 = 0;
            j = 0;
            i7 = i4;
            while (r2 < track.editListDurations.length) {
                long j5 = track.editListMediaTimes[r2];
                scaleLargeTimestamp = track.editListDurations[r2];
                if (j5 != -1) {
                    long scaleLargeTimestamp2 = j5 + Util.scaleLargeTimestamp(scaleLargeTimestamp, track.timescale, track.movieTimescale);
                    i4 = Util.binarySearchCeil(jArr, j5, true, true);
                    int binarySearchCeil = Util.binarySearchCeil(jArr, scaleLargeTimestamp2, true, false);
                    if (readUnsignedIntToInt2 != 0) {
                        i5 = binarySearchCeil - i4;
                        System.arraycopy(obj2, i4, obj7, i12, i5);
                        System.arraycopy(obj, i4, obj8, i12, i5);
                        System.arraycopy(obj3, i4, obj9, i12, i5);
                    }
                    i6 = i12;
                    for (int i14 = i4; i14 < binarySearchCeil; i14++) {
                        jArr4[i6] = Util.scaleLargeTimestamp(jArr[i14] - j5, C0700C.MICROS_PER_SECOND, track.timescale) + Util.scaleLargeTimestamp(j, C0700C.MICROS_PER_SECOND, track.movieTimescale);
                        if (readUnsignedIntToInt2 != 0 && obj8[i6] > i7) {
                            i7 = obj[i14];
                        }
                        i6++;
                    }
                    i4 = i7;
                    i7 = i6;
                } else {
                    i4 = i7;
                    i7 = i12;
                }
                r2++;
                i12 = i7;
                j = scaleLargeTimestamp + j;
                i7 = i4;
            }
            i2 = 0;
            for (r2 = 0; r2 < obj9.length && i2 == 0; r2++) {
                i2 |= (obj9[r2] & 1) != 0 ? 1 : 0;
            }
            if (i2 != 0) {
                return new TrackSampleTable(obj7, obj8, i7, jArr4, obj9);
            }
            throw new ParserException("The edited sample sequence does not contain a sync sample.");
        }
    }

    private static StsdData parseStsd(ParsableByteArray parsableByteArray, int i, long j, int i2, String str, boolean z) {
        parsableByteArray.setPosition(12);
        int readInt = parsableByteArray.readInt();
        StsdData stsdData = new StsdData(readInt);
        for (int i3 = 0; i3 < readInt; i3++) {
            int position = parsableByteArray.getPosition();
            int readInt2 = parsableByteArray.readInt();
            Assertions.checkArgument(readInt2 > 0, "childAtomSize should be positive");
            int readInt3 = parsableByteArray.readInt();
            if (readInt3 == Atom.TYPE_avc1 || readInt3 == Atom.TYPE_avc3 || readInt3 == Atom.TYPE_encv || readInt3 == Atom.TYPE_mp4v || readInt3 == Atom.TYPE_hvc1 || readInt3 == Atom.TYPE_hev1 || readInt3 == Atom.TYPE_s263 || readInt3 == Atom.TYPE_vp08 || readInt3 == Atom.TYPE_vp09) {
                parseVideoSampleEntry(parsableByteArray, readInt3, position, readInt2, i, j, i2, stsdData, i3);
            } else if (readInt3 == Atom.TYPE_mp4a || readInt3 == Atom.TYPE_enca || readInt3 == Atom.TYPE_ac_3 || readInt3 == Atom.TYPE_ec_3 || readInt3 == Atom.TYPE_dtsc || readInt3 == Atom.TYPE_dtse || readInt3 == Atom.TYPE_dtsh || readInt3 == Atom.TYPE_dtsl || readInt3 == Atom.TYPE_samr || readInt3 == Atom.TYPE_sawb || readInt3 == Atom.TYPE_lpcm || readInt3 == Atom.TYPE_sowt) {
                parseAudioSampleEntry(parsableByteArray, readInt3, position, readInt2, i, j, str, z, stsdData, i3);
            } else if (readInt3 == Atom.TYPE_TTML) {
                stsdData.mediaFormat = MediaFormat.createTextFormat(Integer.toString(i), MimeTypes.APPLICATION_TTML, -1, j, str);
            } else if (readInt3 == Atom.TYPE_tx3g) {
                stsdData.mediaFormat = MediaFormat.createTextFormat(Integer.toString(i), MimeTypes.APPLICATION_TX3G, -1, j, str);
            } else if (readInt3 == Atom.TYPE_wvtt) {
                stsdData.mediaFormat = MediaFormat.createTextFormat(Integer.toString(i), MimeTypes.APPLICATION_MP4VTT, -1, j, str);
            } else if (readInt3 == Atom.TYPE_stpp) {
                stsdData.mediaFormat = MediaFormat.createTextFormat(Integer.toString(i), MimeTypes.APPLICATION_TTML, -1, j, str, 0);
            }
            parsableByteArray.setPosition(position + readInt2);
        }
        return stsdData;
    }

    private static TkhdData parseTkhd(ParsableByteArray parsableByteArray) {
        long j;
        int i = 8;
        parsableByteArray.setPosition(8);
        int parseFullAtomVersion = Atom.parseFullAtomVersion(parsableByteArray.readInt());
        parsableByteArray.skipBytes(parseFullAtomVersion == 0 ? 8 : 16);
        int readInt = parsableByteArray.readInt();
        parsableByteArray.skipBytes(4);
        Object obj = 1;
        int position = parsableByteArray.getPosition();
        if (parseFullAtomVersion == 0) {
            i = 4;
        }
        for (int i2 = 0; i2 < i; i2++) {
            if (parsableByteArray.data[position + i2] != -1) {
                obj = null;
                break;
            }
        }
        if (obj != null) {
            parsableByteArray.skipBytes(i);
            j = -1;
        } else {
            j = parseFullAtomVersion == 0 ? parsableByteArray.readUnsignedInt() : parsableByteArray.readUnsignedLongToLong();
            if (j == 0) {
                j = -1;
            }
        }
        parsableByteArray.skipBytes(16);
        int readInt2 = parsableByteArray.readInt();
        int readInt3 = parsableByteArray.readInt();
        parsableByteArray.skipBytes(4);
        int readInt4 = parsableByteArray.readInt();
        int readInt5 = parsableByteArray.readInt();
        readInt2 = (readInt2 == 0 && readInt3 == AccessibilityNodeInfoCompat.ACTION_CUT && readInt4 == (-65536) && readInt5 == 0) ? 90 : (readInt2 == 0 && readInt3 == (-65536) && readInt4 == AccessibilityNodeInfoCompat.ACTION_CUT && readInt5 == 0) ? 270 : (readInt2 == (-65536) && readInt3 == 0 && readInt4 == 0 && readInt5 == (-65536)) ? 180 : 0;
        return new TkhdData(readInt, j, readInt2);
    }

    public static Track parseTrak(ContainerAtom containerAtom, LeafAtom leafAtom, long j, boolean z) {
        ContainerAtom containerAtomOfType = containerAtom.getContainerAtomOfType(Atom.TYPE_mdia);
        int parseHdlr = parseHdlr(containerAtomOfType.getLeafAtomOfType(Atom.TYPE_hdlr).data);
        if (parseHdlr != Track.TYPE_soun && parseHdlr != Track.TYPE_vide && parseHdlr != Track.TYPE_text && parseHdlr != Track.TYPE_sbtl && parseHdlr != Track.TYPE_subt) {
            return null;
        }
        TkhdData parseTkhd = parseTkhd(containerAtom.getLeafAtomOfType(Atom.TYPE_tkhd).data);
        long access$000 = j == -1 ? parseTkhd.duration : j;
        long parseMvhd = parseMvhd(leafAtom.data);
        long scaleLargeTimestamp = access$000 == -1 ? -1 : Util.scaleLargeTimestamp(access$000, C0700C.MICROS_PER_SECOND, parseMvhd);
        ContainerAtom containerAtomOfType2 = containerAtomOfType.getContainerAtomOfType(Atom.TYPE_minf).getContainerAtomOfType(Atom.TYPE_stbl);
        Pair parseMdhd = parseMdhd(containerAtomOfType.getLeafAtomOfType(Atom.TYPE_mdhd).data);
        StsdData parseStsd = parseStsd(containerAtomOfType2.getLeafAtomOfType(Atom.TYPE_stsd).data, parseTkhd.id, scaleLargeTimestamp, parseTkhd.rotationDegrees, (String) parseMdhd.second, z);
        Pair parseEdts = parseEdts(containerAtom.getContainerAtomOfType(Atom.TYPE_edts));
        if (parseStsd.mediaFormat == null) {
            return null;
        }
        return new Track(parseTkhd.id, parseHdlr, ((Long) parseMdhd.first).longValue(), parseMvhd, scaleLargeTimestamp, parseStsd.mediaFormat, parseStsd.trackEncryptionBoxes, parseStsd.nalUnitLengthFieldLength, (long[]) parseEdts.first, (long[]) parseEdts.second);
    }

    public static GaplessInfo parseUdta(LeafAtom leafAtom, boolean z) {
        if (z) {
            return null;
        }
        ParsableByteArray parsableByteArray = leafAtom.data;
        parsableByteArray.setPosition(8);
        while (parsableByteArray.bytesLeft() >= 8) {
            int readInt = parsableByteArray.readInt();
            if (parsableByteArray.readInt() == Atom.TYPE_meta) {
                parsableByteArray.setPosition(parsableByteArray.getPosition() - 8);
                parsableByteArray.setLimit(parsableByteArray.getPosition() + readInt);
                return parseMetaAtom(parsableByteArray);
            }
            parsableByteArray.skipBytes(readInt - 8);
        }
        return null;
    }

    private static void parseVideoSampleEntry(ParsableByteArray parsableByteArray, int i, int i2, int i3, int i4, long j, int i5, StsdData stsdData, int i6) {
        parsableByteArray.setPosition(i2 + 8);
        parsableByteArray.skipBytes(24);
        int readUnsignedShort = parsableByteArray.readUnsignedShort();
        int readUnsignedShort2 = parsableByteArray.readUnsignedShort();
        Object obj = null;
        float f = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
        parsableByteArray.skipBytes(50);
        int position = parsableByteArray.getPosition();
        if (i == Atom.TYPE_encv) {
            parseSampleEntryEncryptionData(parsableByteArray, i2, i3, stsdData, i6);
            parsableByteArray.setPosition(position);
        }
        List list = null;
        String str = null;
        int i7 = position;
        while (i7 - i2 < i3) {
            parsableByteArray.setPosition(i7);
            int position2 = parsableByteArray.getPosition();
            int readInt = parsableByteArray.readInt();
            if (readInt == 0 && parsableByteArray.getPosition() - i2 == i3) {
                break;
            }
            Object obj2;
            Assertions.checkArgument(readInt > 0, "childAtomSize should be positive");
            position = parsableByteArray.readInt();
            if (position == Atom.TYPE_avcC) {
                Assertions.checkState(str == null);
                str = MimeTypes.VIDEO_H264;
                AvcCData parseAvcCFromParent = parseAvcCFromParent(parsableByteArray, position2);
                list = parseAvcCFromParent.initializationData;
                stsdData.nalUnitLengthFieldLength = parseAvcCFromParent.nalUnitLengthFieldLength;
                if (obj == null) {
                    f = parseAvcCFromParent.pixelWidthAspectRatio;
                }
                obj2 = obj;
            } else if (position == Atom.TYPE_hvcC) {
                Assertions.checkState(str == null);
                String str2 = MimeTypes.VIDEO_H265;
                Pair parseHvcCFromParent = parseHvcCFromParent(parsableByteArray, position2);
                List list2 = (List) parseHvcCFromParent.first;
                stsdData.nalUnitLengthFieldLength = ((Integer) parseHvcCFromParent.second).intValue();
                list = list2;
                obj2 = obj;
                str = str2;
            } else if (position == Atom.TYPE_d263) {
                Assertions.checkState(str == null);
                str = MimeTypes.VIDEO_H263;
                obj2 = obj;
            } else if (position == Atom.TYPE_esds) {
                Assertions.checkState(str == null);
                Pair parseEsdsFromParent = parseEsdsFromParent(parsableByteArray, position2);
                String str3 = (String) parseEsdsFromParent.first;
                list = Collections.singletonList(parseEsdsFromParent.second);
                str = str3;
                obj2 = obj;
            } else if (position == Atom.TYPE_pasp) {
                f = parsePaspFromParent(parsableByteArray, position2);
                obj2 = 1;
            } else if (position == Atom.TYPE_vpcC) {
                Assertions.checkState(str == null);
                str = i == Atom.TYPE_vp08 ? MimeTypes.VIDEO_VP8 : MimeTypes.VIDEO_VP9;
                obj2 = obj;
            } else {
                obj2 = obj;
            }
            i7 += readInt;
            obj = obj2;
        }
        if (str != null) {
            stsdData.mediaFormat = MediaFormat.createVideoFormat(Integer.toString(i4), str, -1, -1, j, readUnsignedShort, readUnsignedShort2, list, i5, f);
        }
    }
}
