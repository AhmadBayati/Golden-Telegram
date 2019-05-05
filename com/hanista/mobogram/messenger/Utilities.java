package com.hanista.mobogram.messenger;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory.Options;
import com.hanista.mobogram.messenger.exoplayer.util.NalUnitUtil;
import com.hanista.mobogram.tgnet.TLRPC;
import java.io.File;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utilities {
    public static volatile DispatchQueue globalQueue;
    protected static final char[] hexArray;
    public static Pattern pattern;
    public static volatile DispatchQueue phoneBookQueue;
    public static SecureRandom random;
    public static volatile DispatchQueue searchQueue;
    public static volatile DispatchQueue stageQueue;

    static {
        pattern = Pattern.compile("[\\-0-9]+");
        random = new SecureRandom();
        stageQueue = new DispatchQueue("stageQueue");
        globalQueue = new DispatchQueue("globalQueue");
        searchQueue = new DispatchQueue("searchQueue");
        phoneBookQueue = new DispatchQueue("photoBookQueue");
        hexArray = "0123456789ABCDEF".toCharArray();
        try {
            FileInputStream fileInputStream = new FileInputStream(new File("/dev/urandom"));
            byte[] bArr = new byte[TLRPC.MESSAGE_FLAG_HAS_VIEWS];
            fileInputStream.read(bArr);
            fileInputStream.close();
            random.setSeed(bArr);
        } catch (Throwable e) {
            FileLog.m18e("tmessages", e);
        }
    }

    public static String MD5(String str) {
        String str2 = null;
        if (str != null) {
            try {
                byte[] digest = MessageDigest.getInstance("MD5").digest(str.getBytes());
                StringBuilder stringBuilder = new StringBuilder();
                for (byte b : digest) {
                    stringBuilder.append(Integer.toHexString((b & NalUnitUtil.EXTENDED_SAR) | TLRPC.USER_FLAG_UNUSED2).substring(1, 3));
                }
                str2 = stringBuilder.toString();
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
        }
        return str2;
    }

    private static native void aesIgeEncryption(ByteBuffer byteBuffer, byte[] bArr, byte[] bArr2, boolean z, int i, int i2);

    public static void aesIgeEncryption(ByteBuffer byteBuffer, byte[] bArr, byte[] bArr2, boolean z, boolean z2, int i, int i2) {
        aesIgeEncryption(byteBuffer, bArr, z2 ? bArr2 : (byte[]) bArr2.clone(), z, i, i2);
    }

    public static boolean arraysEquals(byte[] bArr, int i, byte[] bArr2, int i2) {
        if (bArr == null || bArr2 == null || i < 0 || i2 < 0 || bArr.length - i != bArr2.length - i2 || bArr.length - i < 0 || bArr2.length - i2 < 0) {
            return false;
        }
        boolean z = true;
        for (int i3 = i; i3 < bArr.length; i3++) {
            if (bArr[i3 + i] != bArr2[i3 + i2]) {
                z = false;
            }
        }
        return z;
    }

    public static native void blurBitmap(Object obj, int i, int i2, int i3, int i4, int i5);

    public static String bytesToHex(byte[] bArr) {
        if (bArr == null) {
            return TtmlNode.ANONYMOUS_REGION_ID;
        }
        char[] cArr = new char[(bArr.length * 2)];
        for (int i = 0; i < bArr.length; i++) {
            int i2 = bArr[i] & NalUnitUtil.EXTENDED_SAR;
            cArr[i * 2] = hexArray[i2 >>> 4];
            cArr[(i * 2) + 1] = hexArray[i2 & 15];
        }
        return new String(cArr);
    }

    public static long bytesToLong(byte[] bArr) {
        return (((((((((long) bArr[7]) << 56) + ((((long) bArr[6]) & 255) << 48)) + ((((long) bArr[5]) & 255) << 40)) + ((((long) bArr[4]) & 255) << 32)) + ((((long) bArr[3]) & 255) << 24)) + ((((long) bArr[2]) & 255) << 16)) + ((((long) bArr[1]) & 255) << 8)) + (((long) bArr[0]) & 255);
    }

    public static native void calcCDT(ByteBuffer byteBuffer, int i, int i2, ByteBuffer byteBuffer2);

    public static byte[] computeSHA1(ByteBuffer byteBuffer) {
        return computeSHA1(byteBuffer, 0, byteBuffer.limit());
    }

    public static byte[] computeSHA1(ByteBuffer byteBuffer, int i, int i2) {
        byte[] digest;
        int position = byteBuffer.position();
        int limit = byteBuffer.limit();
        try {
            MessageDigest instance = MessageDigest.getInstance("SHA-1");
            byteBuffer.position(i);
            byteBuffer.limit(i2);
            instance.update(byteBuffer);
            digest = instance.digest();
            return digest;
        } catch (Exception e) {
            digest = e;
            FileLog.m18e("tmessages", (Throwable) digest);
            return new byte[20];
        } finally {
            byteBuffer.limit(limit);
            byteBuffer.position(position);
        }
    }

    public static byte[] computeSHA1(byte[] bArr) {
        return computeSHA1(bArr, 0, bArr.length);
    }

    public static byte[] computeSHA1(byte[] bArr, int i, int i2) {
        try {
            MessageDigest instance = MessageDigest.getInstance("SHA-1");
            instance.update(bArr, i, i2);
            return instance.digest();
        } catch (Throwable e) {
            FileLog.m18e("tmessages", e);
            return new byte[20];
        }
    }

    public static byte[] computeSHA256(byte[] bArr, int i, int i2) {
        try {
            MessageDigest instance = MessageDigest.getInstance("SHA-256");
            instance.update(bArr, i, i2);
            return instance.digest();
        } catch (Throwable e) {
            FileLog.m18e("tmessages", e);
            return null;
        }
    }

    public static native int convertVideoFrame(ByteBuffer byteBuffer, ByteBuffer byteBuffer2, int i, int i2, int i3, int i4, int i5);

    public static byte[] hexToBytes(String str) {
        if (str == null) {
            return null;
        }
        int length = str.length();
        byte[] bArr = new byte[(length / 2)];
        for (int i = 0; i < length; i += 2) {
            bArr[i / 2] = (byte) ((Character.digit(str.charAt(i), 16) << 4) + Character.digit(str.charAt(i + 1), 16));
        }
        return bArr;
    }

    public static boolean isGoodGaAndGb(BigInteger bigInteger, BigInteger bigInteger2) {
        return bigInteger.compareTo(BigInteger.valueOf(1)) == 1 && bigInteger.compareTo(bigInteger2.subtract(BigInteger.valueOf(1))) == -1;
    }

    public static boolean isGoodPrime(byte[] bArr, int i) {
        boolean z = true;
        if (i < 2 || i > 7 || bArr.length != TLRPC.USER_FLAG_UNUSED2 || bArr[0] >= null) {
            return false;
        }
        BigInteger bigInteger = new BigInteger(1, bArr);
        if (i == 2) {
            if (bigInteger.mod(BigInteger.valueOf(8)).intValue() != 7) {
                return false;
            }
        } else if (i == 3) {
            if (bigInteger.mod(BigInteger.valueOf(3)).intValue() != 2) {
                return false;
            }
        } else if (i == 5) {
            r3 = bigInteger.mod(BigInteger.valueOf(5)).intValue();
            if (!(r3 == 1 || r3 == 4)) {
                return false;
            }
        } else if (i == 6) {
            r3 = bigInteger.mod(BigInteger.valueOf(24)).intValue();
            if (!(r3 == 19 || r3 == 23)) {
                return false;
            }
        } else if (i == 7) {
            r3 = bigInteger.mod(BigInteger.valueOf(7)).intValue();
            if (!(r3 == 3 || r3 == 5 || r3 == 6)) {
                return false;
            }
        }
        if (bytesToHex(bArr).equals("C71CAEB9C6B1C9048E6C522F70F13F73980D40238E3E21C14934D037563D930F48198A0AA7C14058229493D22530F4DBFA336F6E0AC925139543AED44CCE7C3720FD51F69458705AC68CD4FE6B6B13ABDC9746512969328454F18FAF8C595F642477FE96BB2A941D5BCD1D4AC8CC49880708FA9B378E3C4F3A9060BEE67CF9A4A4A695811051907E162753B56B0F6B410DBA74D8A84B2A14B3144E0EF1284754FD17ED950D5965B4B9DD46582DB1178D169C6BC465B0D6FF9CA3928FEF5B9AE4E418FC15E83EBEA0F87FA9FF5EED70050DED2849F47BF959D956850CE929851F0D8115F635B105EE2E4E15D04B2454BF6F4FADF034B10403119CD8E3B92FCC5B")) {
            return true;
        }
        BigInteger divide = bigInteger.subtract(BigInteger.valueOf(1)).divide(BigInteger.valueOf(2));
        if (!(bigInteger.isProbablePrime(30) && divide.isProbablePrime(30))) {
            z = false;
        }
        return z;
    }

    public static native void loadBitmap(String str, Bitmap bitmap, int i, int i2, int i3, int i4);

    public static native boolean loadWebpImage(Bitmap bitmap, ByteBuffer byteBuffer, int i, Options options, boolean z);

    public static Integer parseInt(String str) {
        if (str == null) {
            return Integer.valueOf(0);
        }
        Integer valueOf = Integer.valueOf(0);
        try {
            Matcher matcher = pattern.matcher(str);
            return matcher.find() ? Integer.valueOf(Integer.parseInt(matcher.group(0))) : valueOf;
        } catch (Throwable e) {
            FileLog.m18e("tmessages", e);
            return valueOf;
        }
    }

    public static String parseIntToString(String str) {
        Matcher matcher = pattern.matcher(str);
        return matcher.find() ? matcher.group(0) : null;
    }

    public static Long parseLong(String str) {
        if (str == null) {
            return Long.valueOf(0);
        }
        Long valueOf = Long.valueOf(0);
        try {
            Matcher matcher = pattern.matcher(str);
            return matcher.find() ? Long.valueOf(Long.parseLong(matcher.group(0))) : valueOf;
        } catch (Throwable e) {
            FileLog.m18e("tmessages", e);
            return valueOf;
        }
    }

    public static native int pinBitmap(Bitmap bitmap);

    public static native String readlink(String str);

    public static native void unpinBitmap(Bitmap bitmap);
}
