package com.hanista.mobogram.messenger.exoplayer.util;

import android.util.Pair;
import com.hanista.mobogram.messenger.exoplayer.upstream.UdpDataSource;
import com.hanista.mobogram.tgnet.TLRPC;
import java.util.ArrayList;
import java.util.List;

public final class CodecSpecificDataUtil {
    private static final int AUDIO_OBJECT_TYPE_AAC_LC = 2;
    private static final int AUDIO_OBJECT_TYPE_ER_BSAC = 22;
    private static final int AUDIO_OBJECT_TYPE_PS = 29;
    private static final int AUDIO_OBJECT_TYPE_SBR = 5;
    private static final int AUDIO_SPECIFIC_CONFIG_CHANNEL_CONFIGURATION_INVALID = -1;
    private static final int[] AUDIO_SPECIFIC_CONFIG_CHANNEL_COUNT_TABLE;
    private static final int AUDIO_SPECIFIC_CONFIG_FREQUENCY_INDEX_ARBITRARY = 15;
    private static final int[] AUDIO_SPECIFIC_CONFIG_SAMPLING_RATE_TABLE;
    private static final byte[] NAL_START_CODE;

    static {
        NAL_START_CODE = new byte[]{(byte) 0, (byte) 0, (byte) 0, (byte) 1};
        AUDIO_SPECIFIC_CONFIG_SAMPLING_RATE_TABLE = new int[]{96000, 88200, 64000, 48000, 44100, 32000, 24000, 22050, 16000, 12000, 11025, UdpDataSource.DEAFULT_SOCKET_TIMEOUT_MILLIS, 7350};
        AUDIO_SPECIFIC_CONFIG_CHANNEL_COUNT_TABLE = new int[]{0, 1, AUDIO_OBJECT_TYPE_AAC_LC, 3, 4, AUDIO_OBJECT_TYPE_SBR, 6, 8, AUDIO_SPECIFIC_CONFIG_CHANNEL_CONFIGURATION_INVALID, AUDIO_SPECIFIC_CONFIG_CHANNEL_CONFIGURATION_INVALID, AUDIO_SPECIFIC_CONFIG_CHANNEL_CONFIGURATION_INVALID, 7, 8, AUDIO_SPECIFIC_CONFIG_CHANNEL_CONFIGURATION_INVALID, 8, AUDIO_SPECIFIC_CONFIG_CHANNEL_CONFIGURATION_INVALID};
    }

    private CodecSpecificDataUtil() {
    }

    public static byte[] buildAacAudioSpecificConfig(int i, int i2) {
        int i3;
        int i4 = AUDIO_SPECIFIC_CONFIG_CHANNEL_CONFIGURATION_INVALID;
        int i5 = AUDIO_SPECIFIC_CONFIG_CHANNEL_CONFIGURATION_INVALID;
        for (i3 = 0; i3 < AUDIO_SPECIFIC_CONFIG_SAMPLING_RATE_TABLE.length; i3++) {
            if (i == AUDIO_SPECIFIC_CONFIG_SAMPLING_RATE_TABLE[i3]) {
                i5 = i3;
            }
        }
        for (i3 = 0; i3 < AUDIO_SPECIFIC_CONFIG_CHANNEL_COUNT_TABLE.length; i3++) {
            if (i2 == AUDIO_SPECIFIC_CONFIG_CHANNEL_COUNT_TABLE[i3]) {
                i4 = i3;
            }
        }
        byte[] bArr = new byte[AUDIO_OBJECT_TYPE_AAC_LC];
        bArr[0] = (byte) ((i5 >> 1) | 16);
        bArr[1] = (byte) (((i5 & 1) << 7) | (i4 << 3));
        return bArr;
    }

    public static byte[] buildAacAudioSpecificConfig(int i, int i2, int i3) {
        byte[] bArr = new byte[AUDIO_OBJECT_TYPE_AAC_LC];
        bArr[0] = (byte) (((i << 3) & 248) | ((i2 >> 1) & 7));
        bArr[1] = (byte) (((i2 << 7) & TLRPC.USER_FLAG_UNUSED) | ((i3 << 3) & 120));
        return bArr;
    }

    public static byte[] buildNalUnit(byte[] bArr, int i, int i2) {
        Object obj = new byte[(NAL_START_CODE.length + i2)];
        System.arraycopy(NAL_START_CODE, 0, obj, 0, NAL_START_CODE.length);
        System.arraycopy(bArr, i, obj, NAL_START_CODE.length, i2);
        return obj;
    }

    private static int findNalStartCode(byte[] bArr, int i) {
        int length = bArr.length - NAL_START_CODE.length;
        for (int i2 = i; i2 <= length; i2++) {
            if (isNalStartCode(bArr, i2)) {
                return i2;
            }
        }
        return AUDIO_SPECIFIC_CONFIG_CHANNEL_CONFIGURATION_INVALID;
    }

    private static boolean isNalStartCode(byte[] bArr, int i) {
        if (bArr.length - i <= NAL_START_CODE.length) {
            return false;
        }
        for (int i2 = 0; i2 < NAL_START_CODE.length; i2++) {
            if (bArr[i + i2] != NAL_START_CODE[i2]) {
                return false;
            }
        }
        return true;
    }

    public static Pair<Integer, Integer> parseAacAudioSpecificConfig(byte[] bArr) {
        int readBits;
        boolean z = true;
        ParsableBitArray parsableBitArray = new ParsableBitArray(bArr);
        int readBits2 = parsableBitArray.readBits(AUDIO_OBJECT_TYPE_SBR);
        int readBits3 = parsableBitArray.readBits(4);
        if (readBits3 == AUDIO_SPECIFIC_CONFIG_FREQUENCY_INDEX_ARBITRARY) {
            readBits = parsableBitArray.readBits(24);
        } else {
            Assertions.checkArgument(readBits3 < 13);
            readBits = AUDIO_SPECIFIC_CONFIG_SAMPLING_RATE_TABLE[readBits3];
        }
        readBits3 = parsableBitArray.readBits(4);
        if (readBits2 == AUDIO_OBJECT_TYPE_SBR || readBits2 == AUDIO_OBJECT_TYPE_PS) {
            readBits2 = parsableBitArray.readBits(4);
            if (readBits2 == AUDIO_SPECIFIC_CONFIG_FREQUENCY_INDEX_ARBITRARY) {
                readBits = parsableBitArray.readBits(24);
            } else {
                Assertions.checkArgument(readBits2 < 13);
                readBits = AUDIO_SPECIFIC_CONFIG_SAMPLING_RATE_TABLE[readBits2];
            }
            if (parsableBitArray.readBits(AUDIO_OBJECT_TYPE_SBR) == AUDIO_OBJECT_TYPE_ER_BSAC) {
                readBits3 = readBits;
                readBits = parsableBitArray.readBits(4);
                readBits = AUDIO_SPECIFIC_CONFIG_CHANNEL_COUNT_TABLE[readBits];
                if (readBits == AUDIO_SPECIFIC_CONFIG_CHANNEL_CONFIGURATION_INVALID) {
                    z = false;
                }
                Assertions.checkArgument(z);
                return Pair.create(Integer.valueOf(readBits3), Integer.valueOf(readBits));
            }
        }
        int i = readBits3;
        readBits3 = readBits;
        readBits = i;
        readBits = AUDIO_SPECIFIC_CONFIG_CHANNEL_COUNT_TABLE[readBits];
        if (readBits == AUDIO_SPECIFIC_CONFIG_CHANNEL_CONFIGURATION_INVALID) {
            z = false;
        }
        Assertions.checkArgument(z);
        return Pair.create(Integer.valueOf(readBits3), Integer.valueOf(readBits));
    }

    public static byte[][] splitNalUnits(byte[] bArr) {
        if (!isNalStartCode(bArr, 0)) {
            return (byte[][]) null;
        }
        List arrayList = new ArrayList();
        int i = 0;
        do {
            arrayList.add(Integer.valueOf(i));
            i = findNalStartCode(bArr, i + NAL_START_CODE.length);
        } while (i != AUDIO_SPECIFIC_CONFIG_CHANNEL_CONFIGURATION_INVALID);
        byte[][] bArr2 = new byte[arrayList.size()][];
        int i2 = 0;
        while (i2 < arrayList.size()) {
            int intValue = ((Integer) arrayList.get(i2)).intValue();
            Object obj = new byte[((i2 < arrayList.size() + AUDIO_SPECIFIC_CONFIG_CHANNEL_CONFIGURATION_INVALID ? ((Integer) arrayList.get(i2 + 1)).intValue() : bArr.length) - intValue)];
            System.arraycopy(bArr, intValue, obj, 0, obj.length);
            bArr2[i2] = obj;
            i2++;
        }
        return bArr2;
    }
}
