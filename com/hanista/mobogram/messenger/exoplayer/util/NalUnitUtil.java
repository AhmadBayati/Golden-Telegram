package com.hanista.mobogram.messenger.exoplayer.util;

import android.util.Log;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;
import com.hanista.mobogram.tgnet.TLRPC;
import java.nio.ByteBuffer;
import java.util.Arrays;

public final class NalUnitUtil {
    public static final float[] ASPECT_RATIO_IDC_VALUES;
    public static final int EXTENDED_SAR = 255;
    public static final byte[] NAL_START_CODE;
    private static final int NAL_UNIT_TYPE_SPS = 7;
    private static final String TAG = "NalUnitUtil";
    private static int[] scratchEscapePositions;
    private static final Object scratchEscapePositionsLock;

    public static final class PpsData {
        public final boolean bottomFieldPicOrderInFramePresentFlag;
        public final int picParameterSetId;
        public final int seqParameterSetId;

        public PpsData(int i, int i2, boolean z) {
            this.picParameterSetId = i;
            this.seqParameterSetId = i2;
            this.bottomFieldPicOrderInFramePresentFlag = z;
        }
    }

    public static final class SpsData {
        public final boolean deltaPicOrderAlwaysZeroFlag;
        public final boolean frameMbsOnlyFlag;
        public final int frameNumLength;
        public final int height;
        public final int picOrderCntLsbLength;
        public final int picOrderCountType;
        public final float pixelWidthAspectRatio;
        public final boolean separateColorPlaneFlag;
        public final int seqParameterSetId;
        public final int width;

        public SpsData(int i, int i2, int i3, float f, boolean z, boolean z2, int i4, int i5, int i6, boolean z3) {
            this.seqParameterSetId = i;
            this.width = i2;
            this.height = i3;
            this.pixelWidthAspectRatio = f;
            this.separateColorPlaneFlag = z;
            this.frameMbsOnlyFlag = z2;
            this.frameNumLength = i4;
            this.picOrderCountType = i5;
            this.picOrderCntLsbLength = i6;
            this.deltaPicOrderAlwaysZeroFlag = z3;
        }
    }

    static {
        NAL_START_CODE = new byte[]{(byte) 0, (byte) 0, (byte) 0, (byte) 1};
        ASPECT_RATIO_IDC_VALUES = new float[]{DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, 1.0909091f, 0.90909094f, 1.4545455f, 1.2121212f, 2.1818182f, 1.8181819f, 2.909091f, 2.4242425f, 1.6363636f, 1.3636364f, 1.939394f, 1.6161616f, 1.3333334f, 1.5f, 2.0f};
        scratchEscapePositionsLock = new Object();
        scratchEscapePositions = new int[10];
    }

    private NalUnitUtil() {
    }

    public static void clearPrefixFlags(boolean[] zArr) {
        zArr[0] = false;
        zArr[1] = false;
        zArr[2] = false;
    }

    public static void discardToSps(ByteBuffer byteBuffer) {
        int position = byteBuffer.position();
        int i = 0;
        int i2 = 0;
        while (i + 1 < position) {
            int i3 = byteBuffer.get(i) & EXTENDED_SAR;
            if (i2 == 3) {
                if (i3 == 1 && (byteBuffer.get(i + 1) & 31) == NAL_UNIT_TYPE_SPS) {
                    ByteBuffer duplicate = byteBuffer.duplicate();
                    duplicate.position(i - 3);
                    duplicate.limit(position);
                    byteBuffer.position(0);
                    byteBuffer.put(duplicate);
                    return;
                }
            } else if (i3 == 0) {
                i2++;
            }
            if (i3 != 0) {
                i2 = 0;
            }
            i++;
        }
        byteBuffer.clear();
    }

    public static int findNalUnit(byte[] bArr, int i, int i2, boolean[] zArr) {
        boolean z = true;
        int i3 = i2 - i;
        Assertions.checkState(i3 >= 0);
        if (i3 == 0) {
            return i2;
        }
        if (zArr != null) {
            if (zArr[0]) {
                clearPrefixFlags(zArr);
                return i - 3;
            } else if (i3 > 1 && zArr[1] && bArr[i] == (byte) 1) {
                clearPrefixFlags(zArr);
                return i - 2;
            } else if (i3 > 2 && zArr[2] && bArr[i] == null && bArr[i + 1] == (byte) 1) {
                clearPrefixFlags(zArr);
                return i - 1;
            }
        }
        int i4 = i2 - 1;
        int i5 = i + 2;
        while (i5 < i4) {
            if ((bArr[i5] & 254) == 0) {
                if (bArr[i5 - 2] == null && bArr[i5 - 1] == null && bArr[i5] == (byte) 1) {
                    if (zArr != null) {
                        clearPrefixFlags(zArr);
                    }
                    return i5 - 2;
                }
                i5 -= 2;
            }
            i5 += 3;
        }
        if (zArr == null) {
            return i2;
        }
        boolean z2 = i3 > 2 ? bArr[i2 + -3] == null && bArr[i2 - 2] == null && bArr[i2 - 1] == (byte) 1 : i3 == 2 ? zArr[2] && bArr[i2 - 2] == null && bArr[i2 - 1] == (byte) 1 : zArr[1] && bArr[i2 - 1] == (byte) 1;
        zArr[0] = z2;
        z2 = i3 > 1 ? bArr[i2 + -2] == null && bArr[i2 - 1] == null : zArr[2] && bArr[i2 - 1] == null;
        zArr[1] = z2;
        if (bArr[i2 - 1] != null) {
            z = false;
        }
        zArr[2] = z;
        return i2;
    }

    private static int findNextUnescapeIndex(byte[] bArr, int i, int i2) {
        int i3 = i;
        while (i3 < i2 - 2) {
            if (bArr[i3] == null && bArr[i3 + 1] == null && bArr[i3 + 2] == 3) {
                return i3;
            }
            i3++;
        }
        return i2;
    }

    public static int getH265NalUnitType(byte[] bArr, int i) {
        return (bArr[i + 3] & 126) >> 1;
    }

    public static int getNalUnitType(byte[] bArr, int i) {
        return bArr[i + 3] & 31;
    }

    public static byte[] parseChildNalUnit(ParsableByteArray parsableByteArray) {
        int readUnsignedShort = parsableByteArray.readUnsignedShort();
        int position = parsableByteArray.getPosition();
        parsableByteArray.skipBytes(readUnsignedShort);
        return CodecSpecificDataUtil.buildNalUnit(parsableByteArray.data, position, readUnsignedShort);
    }

    public static PpsData parsePpsNalUnit(ParsableBitArray parsableBitArray) {
        int readUnsignedExpGolombCodedInt = parsableBitArray.readUnsignedExpGolombCodedInt();
        int readUnsignedExpGolombCodedInt2 = parsableBitArray.readUnsignedExpGolombCodedInt();
        parsableBitArray.skipBits(1);
        return new PpsData(readUnsignedExpGolombCodedInt, readUnsignedExpGolombCodedInt2, parsableBitArray.readBit());
    }

    public static SpsData parseSpsNalUnit(ParsableBitArray parsableBitArray) {
        int readUnsignedExpGolombCodedInt;
        int i;
        boolean z;
        int i2;
        float f;
        int readBits = parsableBitArray.readBits(8);
        parsableBitArray.skipBits(16);
        int readUnsignedExpGolombCodedInt2 = parsableBitArray.readUnsignedExpGolombCodedInt();
        boolean z2 = false;
        if (readBits == 100 || readBits == 110 || readBits == 122 || readBits == 244 || readBits == 44 || readBits == 83 || readBits == 86 || readBits == 118 || readBits == TLRPC.USER_FLAG_UNUSED || readBits == 138) {
            readUnsignedExpGolombCodedInt = parsableBitArray.readUnsignedExpGolombCodedInt();
            if (readUnsignedExpGolombCodedInt == 3) {
                z2 = parsableBitArray.readBit();
            }
            parsableBitArray.readUnsignedExpGolombCodedInt();
            parsableBitArray.readUnsignedExpGolombCodedInt();
            parsableBitArray.skipBits(1);
            if (parsableBitArray.readBit()) {
                i = readUnsignedExpGolombCodedInt != 3 ? 8 : 12;
                int i3 = 0;
                while (i3 < i) {
                    if (parsableBitArray.readBit()) {
                        skipScalingList(parsableBitArray, i3 < 6 ? 16 : 64);
                    }
                    i3++;
                }
            }
            z = z2;
            i2 = readUnsignedExpGolombCodedInt;
        } else {
            z = false;
            i2 = 1;
        }
        int readUnsignedExpGolombCodedInt3 = parsableBitArray.readUnsignedExpGolombCodedInt() + 4;
        int readUnsignedExpGolombCodedInt4 = parsableBitArray.readUnsignedExpGolombCodedInt();
        int i4 = 0;
        boolean z3 = false;
        if (readUnsignedExpGolombCodedInt4 != 0) {
            if (readUnsignedExpGolombCodedInt4 == 1) {
                z3 = parsableBitArray.readBit();
                parsableBitArray.readSignedExpGolombCodedInt();
                parsableBitArray.readSignedExpGolombCodedInt();
                long readUnsignedExpGolombCodedInt5 = (long) parsableBitArray.readUnsignedExpGolombCodedInt();
                i = 0;
                while (true) {
                    if (((long) i) >= readUnsignedExpGolombCodedInt5) {
                        break;
                    }
                    parsableBitArray.readUnsignedExpGolombCodedInt();
                    i++;
                }
            }
        } else {
            i4 = parsableBitArray.readUnsignedExpGolombCodedInt() + 4;
        }
        parsableBitArray.readUnsignedExpGolombCodedInt();
        parsableBitArray.skipBits(1);
        readBits = parsableBitArray.readUnsignedExpGolombCodedInt() + 1;
        readUnsignedExpGolombCodedInt = parsableBitArray.readUnsignedExpGolombCodedInt() + 1;
        boolean readBit = parsableBitArray.readBit();
        i = (2 - (readBit ? 1 : 0)) * readUnsignedExpGolombCodedInt;
        if (!readBit) {
            parsableBitArray.skipBits(1);
        }
        parsableBitArray.skipBits(1);
        readUnsignedExpGolombCodedInt = readBits * 16;
        readBits = i * 16;
        if (parsableBitArray.readBit()) {
            int readUnsignedExpGolombCodedInt6 = parsableBitArray.readUnsignedExpGolombCodedInt();
            int readUnsignedExpGolombCodedInt7 = parsableBitArray.readUnsignedExpGolombCodedInt();
            int readUnsignedExpGolombCodedInt8 = parsableBitArray.readUnsignedExpGolombCodedInt();
            int readUnsignedExpGolombCodedInt9 = parsableBitArray.readUnsignedExpGolombCodedInt();
            if (i2 == 0) {
                i = 1;
                i2 = 2 - (readBit ? 1 : 0);
            } else {
                i = i2 == 3 ? 1 : 2;
                i2 = (2 - (readBit ? 1 : 0)) * (i2 == 1 ? 2 : 1);
            }
            i = readUnsignedExpGolombCodedInt - (i * (readUnsignedExpGolombCodedInt6 + readUnsignedExpGolombCodedInt7));
            readBits -= i2 * (readUnsignedExpGolombCodedInt8 + readUnsignedExpGolombCodedInt9);
        } else {
            i = readUnsignedExpGolombCodedInt;
        }
        float f2 = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
        if (parsableBitArray.readBit() && parsableBitArray.readBit()) {
            readUnsignedExpGolombCodedInt = parsableBitArray.readBits(8);
            if (readUnsignedExpGolombCodedInt == EXTENDED_SAR) {
                readUnsignedExpGolombCodedInt = parsableBitArray.readBits(16);
                int readBits2 = parsableBitArray.readBits(16);
                if (!(readUnsignedExpGolombCodedInt == 0 || readBits2 == 0)) {
                    f2 = ((float) readUnsignedExpGolombCodedInt) / ((float) readBits2);
                }
                f = f2;
            } else if (readUnsignedExpGolombCodedInt < ASPECT_RATIO_IDC_VALUES.length) {
                f = ASPECT_RATIO_IDC_VALUES[readUnsignedExpGolombCodedInt];
            } else {
                Log.w(TAG, "Unexpected aspect_ratio_idc value: " + readUnsignedExpGolombCodedInt);
            }
            return new SpsData(readUnsignedExpGolombCodedInt2, i, readBits, f, z, readBit, readUnsignedExpGolombCodedInt3, readUnsignedExpGolombCodedInt4, i4, z3);
        }
        f = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
        return new SpsData(readUnsignedExpGolombCodedInt2, i, readBits, f, z, readBit, readUnsignedExpGolombCodedInt3, readUnsignedExpGolombCodedInt4, i4, z3);
    }

    private static void skipScalingList(ParsableBitArray parsableBitArray, int i) {
        int i2 = 8;
        int i3 = 8;
        for (int i4 = 0; i4 < i; i4++) {
            if (i2 != 0) {
                i2 = ((parsableBitArray.readSignedExpGolombCodedInt() + i3) + TLRPC.USER_FLAG_UNUSED2) % TLRPC.USER_FLAG_UNUSED2;
            }
            if (i2 != 0) {
                i3 = i2;
            }
        }
    }

    public static int unescapeStream(byte[] bArr, int i) {
        int i2;
        int i3 = 0;
        synchronized (scratchEscapePositionsLock) {
            int findNextUnescapeIndex;
            int i4 = 0;
            int i5 = 0;
            while (i5 < i) {
                findNextUnescapeIndex = findNextUnescapeIndex(bArr, i5, i);
                if (findNextUnescapeIndex < i) {
                    if (scratchEscapePositions.length <= i4) {
                        scratchEscapePositions = Arrays.copyOf(scratchEscapePositions, scratchEscapePositions.length * 2);
                    }
                    i5 = i4 + 1;
                    scratchEscapePositions[i4] = findNextUnescapeIndex;
                    i4 = i5;
                    i5 = findNextUnescapeIndex + 3;
                } else {
                    i5 = findNextUnescapeIndex;
                }
            }
            i2 = i - i4;
            i5 = 0;
            findNextUnescapeIndex = 0;
            while (i3 < i4) {
                int i6 = scratchEscapePositions[i3] - findNextUnescapeIndex;
                System.arraycopy(bArr, findNextUnescapeIndex, bArr, i5, i6);
                i5 += i6;
                int i7 = i5 + 1;
                bArr[i5] = (byte) 0;
                i5 = i7 + 1;
                bArr[i7] = (byte) 0;
                findNextUnescapeIndex += i6 + 3;
                i3++;
            }
            System.arraycopy(bArr, findNextUnescapeIndex, bArr, i5, i2 - i5);
        }
        return i2;
    }
}
