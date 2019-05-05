package com.hanista.mobogram.messenger.exoplayer.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION;
import android.support.v4.view.MotionEventCompat;
import android.text.TextUtils;
import com.googlecode.mp4parser.authoring.tracks.h265.NalUnitTypes;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.exoplayer.C0700C;
import com.hanista.mobogram.messenger.exoplayer.ExoPlayerLibraryInfo;
import com.hanista.mobogram.messenger.exoplayer.upstream.DataSource;
import com.hanista.mobogram.messenger.exoplayer.upstream.DataSpec;
import com.hanista.mobogram.messenger.support.widget.RecyclerView.ItemAnimator;
import com.hanista.mobogram.tgnet.TLRPC;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Util {
    public static final String DEVICE;
    private static final Pattern ESCAPED_CHARACTER_PATTERN;
    public static final String MANUFACTURER;
    private static final long MAX_BYTES_TO_DRAIN = 2048;
    public static final String MODEL;
    public static final int SDK_INT;
    public static final int TYPE_DASH = 0;
    public static final int TYPE_HLS = 2;
    public static final int TYPE_OTHER = 3;
    public static final int TYPE_SS = 1;
    private static final Pattern XS_DATE_TIME_PATTERN;
    private static final Pattern XS_DURATION_PATTERN;

    /* renamed from: com.hanista.mobogram.messenger.exoplayer.util.Util.1 */
    static class C07581 implements ThreadFactory {
        final /* synthetic */ String val$threadName;

        C07581(String str) {
            this.val$threadName = str;
        }

        public Thread newThread(Runnable runnable) {
            return new Thread(runnable, this.val$threadName);
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.exoplayer.util.Util.2 */
    static class C07592 implements ThreadFactory {
        final /* synthetic */ String val$threadName;

        C07592(String str) {
            this.val$threadName = str;
        }

        public Thread newThread(Runnable runnable) {
            return new Thread(runnable, this.val$threadName);
        }
    }

    static {
        int i = (VERSION.SDK_INT == 23 && VERSION.CODENAME.charAt(TYPE_DASH) == 'N') ? 24 : VERSION.SDK_INT;
        SDK_INT = i;
        DEVICE = Build.DEVICE;
        MANUFACTURER = Build.MANUFACTURER;
        MODEL = Build.MODEL;
        XS_DATE_TIME_PATTERN = Pattern.compile("(\\d\\d\\d\\d)\\-(\\d\\d)\\-(\\d\\d)[Tt](\\d\\d):(\\d\\d):(\\d\\d)(\\.(\\d+))?([Zz]|((\\+|\\-)(\\d\\d):(\\d\\d)))?");
        XS_DURATION_PATTERN = Pattern.compile("^(-)?P(([0-9]*)Y)?(([0-9]*)M)?(([0-9]*)D)?(T(([0-9]*)H)?(([0-9]*)M)?(([0-9.]*)S)?)?$");
        ESCAPED_CHARACTER_PATTERN = Pattern.compile("%([A-Fa-f0-9]{2})");
    }

    private Util() {
    }

    public static boolean areEqual(Object obj, Object obj2) {
        return obj == null ? obj2 == null : obj.equals(obj2);
    }

    public static <T> int binarySearchCeil(List<? extends Comparable<? super T>> list, T t, boolean z, boolean z2) {
        int binarySearch = Collections.binarySearch(list, t);
        if (binarySearch < 0) {
            binarySearch ^= -1;
        } else if (!z) {
            binarySearch += TYPE_SS;
        }
        return z2 ? Math.min(list.size() - 1, binarySearch) : binarySearch;
    }

    public static int binarySearchCeil(long[] jArr, long j, boolean z, boolean z2) {
        int binarySearch = Arrays.binarySearch(jArr, j);
        if (binarySearch < 0) {
            binarySearch ^= -1;
        } else if (!z) {
            binarySearch += TYPE_SS;
        }
        return z2 ? Math.min(jArr.length - 1, binarySearch) : binarySearch;
    }

    public static <T> int binarySearchFloor(List<? extends Comparable<? super T>> list, T t, boolean z, boolean z2) {
        int binarySearch = Collections.binarySearch(list, t);
        if (binarySearch < 0) {
            binarySearch = -(binarySearch + TYPE_HLS);
        } else if (!z) {
            binarySearch--;
        }
        return z2 ? Math.max(TYPE_DASH, binarySearch) : binarySearch;
    }

    public static int binarySearchFloor(long[] jArr, long j, boolean z, boolean z2) {
        int binarySearch = Arrays.binarySearch(jArr, j);
        if (binarySearch < 0) {
            binarySearch = -(binarySearch + TYPE_HLS);
        } else if (!z) {
            binarySearch--;
        }
        return z2 ? Math.max(TYPE_DASH, binarySearch) : binarySearch;
    }

    public static int ceilDivide(int i, int i2) {
        return ((i + i2) - 1) / i2;
    }

    public static long ceilDivide(long j, long j2) {
        return ((j + j2) - 1) / j2;
    }

    public static void closeQuietly(DataSource dataSource) {
        try {
            dataSource.close();
        } catch (IOException e) {
        }
    }

    public static void closeQuietly(OutputStream outputStream) {
        try {
            outputStream.close();
        } catch (IOException e) {
        }
    }

    public static boolean contains(Object[] objArr, Object obj) {
        for (int i = TYPE_DASH; i < objArr.length; i += TYPE_SS) {
            if (areEqual(objArr[i], obj)) {
                return true;
            }
        }
        return false;
    }

    public static String escapeFileName(String str) {
        int i;
        int i2 = TYPE_DASH;
        int length = str.length();
        int i3 = TYPE_DASH;
        for (i = TYPE_DASH; i < length; i += TYPE_SS) {
            if (shouldEscapeCharacter(str.charAt(i))) {
                i3 += TYPE_SS;
            }
        }
        if (i3 == 0) {
            return str;
        }
        StringBuilder stringBuilder = new StringBuilder((i3 * TYPE_HLS) + length);
        while (i3 > 0) {
            i = i2 + TYPE_SS;
            char charAt = str.charAt(i2);
            if (shouldEscapeCharacter(charAt)) {
                stringBuilder.append('%').append(Integer.toHexString(charAt));
                i3--;
            } else {
                stringBuilder.append(charAt);
            }
            i2 = i;
        }
        if (i2 < length) {
            stringBuilder.append(str, i2, length);
        }
        return stringBuilder.toString();
    }

    public static byte[] executePost(String str, byte[] bArr, Map<String, String> map) {
        OutputStream outputStream;
        Throwable th;
        HttpURLConnection httpURLConnection = null;
        try {
            HttpURLConnection httpURLConnection2 = (HttpURLConnection) new URL(str).openConnection();
            InputStream inputStream;
            try {
                httpURLConnection2.setRequestMethod("POST");
                httpURLConnection2.setDoOutput(bArr != null);
                httpURLConnection2.setDoInput(true);
                if (map != null) {
                    for (Entry entry : map.entrySet()) {
                        httpURLConnection2.setRequestProperty((String) entry.getKey(), (String) entry.getValue());
                    }
                }
                if (bArr != null) {
                    outputStream = httpURLConnection2.getOutputStream();
                    outputStream.write(bArr);
                    outputStream.close();
                }
                inputStream = httpURLConnection2.getInputStream();
                byte[] toByteArray = toByteArray(inputStream);
                inputStream.close();
                if (httpURLConnection2 != null) {
                    httpURLConnection2.disconnect();
                }
                return toByteArray;
            } catch (Throwable th2) {
                Throwable th3 = th2;
                httpURLConnection = httpURLConnection2;
                th = th3;
                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
                throw th;
            }
        } catch (Throwable th4) {
            th = th4;
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
            throw th;
        }
    }

    public static int[] firstIntegersArray(int i) {
        int[] iArr = new int[i];
        for (int i2 = TYPE_DASH; i2 < i; i2 += TYPE_SS) {
            iArr[i2] = i2;
        }
        return iArr;
    }

    public static int getBottomInt(long j) {
        return (int) j;
    }

    public static byte[] getBytesFromHexString(String str) {
        byte[] bArr = new byte[(str.length() / TYPE_HLS)];
        for (int i = TYPE_DASH; i < bArr.length; i += TYPE_SS) {
            int i2 = i * TYPE_HLS;
            bArr[i] = (byte) (Character.digit(str.charAt(i2 + TYPE_SS), 16) + (Character.digit(str.charAt(i2), 16) << 4));
        }
        return bArr;
    }

    public static <T> String getCommaDelimitedSimpleClassNames(T[] tArr) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = TYPE_DASH; i < tArr.length; i += TYPE_SS) {
            stringBuilder.append(tArr[i].getClass().getSimpleName());
            if (i < tArr.length - 1) {
                stringBuilder.append(", ");
            }
        }
        return stringBuilder.toString();
    }

    public static String getHexStringFromBytes(byte[] bArr, int i, int i2) {
        StringBuilder stringBuilder = new StringBuilder(i2 - i);
        while (i < i2) {
            Object[] objArr = new Object[TYPE_SS];
            objArr[TYPE_DASH] = Byte.valueOf(bArr[i]);
            stringBuilder.append(String.format(Locale.US, "%02X", objArr));
            i += TYPE_SS;
        }
        return stringBuilder.toString();
    }

    public static int getIntegerCodeForString(String str) {
        int i = TYPE_DASH;
        int length = str.length();
        Assertions.checkArgument(length <= 4);
        int i2 = TYPE_DASH;
        while (i < length) {
            i2 = (i2 << 8) | str.charAt(i);
            i += TYPE_SS;
        }
        return i2;
    }

    public static long getLong(int i, int i2) {
        return (((long) i) << 32) | (((long) i2) & 4294967295L);
    }

    public static int getPcmEncoding(int i) {
        switch (i) {
            case TLRPC.USER_FLAG_USERNAME /*8*/:
                return TYPE_OTHER;
            case TLRPC.USER_FLAG_PHONE /*16*/:
                return TYPE_HLS;
            case C0338R.styleable.PromptView_target /*24*/:
                return TLRPC.MESSAGE_FLAG_MEGAGROUP;
            case TLRPC.USER_FLAG_PHOTO /*32*/:
                return C0700C.ENCODING_PCM_32BIT;
            default:
                return TYPE_DASH;
        }
    }

    public static DataSpec getRemainderDataSpec(DataSpec dataSpec, int i) {
        long j = -1;
        if (i == 0) {
            return dataSpec;
        }
        if (dataSpec.length != -1) {
            j = dataSpec.length - ((long) i);
        }
        return new DataSpec(dataSpec.uri, dataSpec.position + ((long) i), j, dataSpec.key, dataSpec.flags);
    }

    public static int getTopInt(long j) {
        return (int) (j >>> 32);
    }

    public static String getUserAgent(Context context, String str) {
        String str2;
        try {
            str2 = context.getPackageManager().getPackageInfo(context.getPackageName(), TYPE_DASH).versionName;
        } catch (NameNotFoundException e) {
            str2 = "?";
        }
        return str + "/" + str2 + " (Linux;Android " + VERSION.RELEASE + ") ExoPlayerLib/" + ExoPlayerLibraryInfo.VERSION;
    }

    public static int inferContentType(String str) {
        return str == null ? TYPE_OTHER : str.endsWith(".mpd") ? TYPE_DASH : str.endsWith(".ism") ? TYPE_SS : str.endsWith(".m3u8") ? TYPE_HLS : TYPE_OTHER;
    }

    @SuppressLint({"InlinedApi"})
    public static boolean isAndroidTv(Context context) {
        return context.getPackageManager().hasSystemFeature("android.software.leanback");
    }

    public static boolean isLocalFileUri(Uri uri) {
        Object scheme = uri.getScheme();
        return TextUtils.isEmpty(scheme) || scheme.equals("file");
    }

    public static void maybeTerminateInputStream(HttpURLConnection httpURLConnection, long j) {
        if (SDK_INT == 19 || SDK_INT == 20) {
            try {
                InputStream inputStream = httpURLConnection.getInputStream();
                if (j == -1) {
                    if (inputStream.read() == -1) {
                        return;
                    }
                } else if (j <= MAX_BYTES_TO_DRAIN) {
                    return;
                }
                String name = inputStream.getClass().getName();
                if (name.equals("com.android.okhttp.internal.http.HttpTransport$ChunkedInputStream") || name.equals("com.android.okhttp.internal.http.HttpTransport$FixedLengthInputStream")) {
                    Method declaredMethod = inputStream.getClass().getSuperclass().getDeclaredMethod("unexpectedEndOfInput", new Class[TYPE_DASH]);
                    declaredMethod.setAccessible(true);
                    declaredMethod.invoke(inputStream, new Object[TYPE_DASH]);
                }
            } catch (IOException e) {
            } catch (Exception e2) {
            }
        }
    }

    public static ExecutorService newSingleThreadExecutor(String str) {
        return Executors.newSingleThreadExecutor(new C07581(str));
    }

    public static ScheduledExecutorService newSingleThreadScheduledExecutor(String str) {
        return Executors.newSingleThreadScheduledExecutor(new C07592(str));
    }

    public static long parseXsDateTime(String str) {
        Matcher matcher = XS_DATE_TIME_PATTERN.matcher(str);
        if (matcher.matches()) {
            int i;
            if (matcher.group(9) == null) {
                i = TYPE_DASH;
            } else if (matcher.group(9).equalsIgnoreCase("Z")) {
                i = TYPE_DASH;
            } else {
                int parseInt = (Integer.parseInt(matcher.group(12)) * 60) + Integer.parseInt(matcher.group(13));
                i = matcher.group(11).equals("-") ? parseInt * -1 : parseInt;
            }
            Calendar gregorianCalendar = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
            gregorianCalendar.clear();
            gregorianCalendar.set(Integer.parseInt(matcher.group(TYPE_SS)), Integer.parseInt(matcher.group(TYPE_HLS)) - 1, Integer.parseInt(matcher.group(TYPE_OTHER)), Integer.parseInt(matcher.group(4)), Integer.parseInt(matcher.group(5)), Integer.parseInt(matcher.group(6)));
            if (!TextUtils.isEmpty(matcher.group(8))) {
                gregorianCalendar.set(14, new BigDecimal("0." + matcher.group(8)).movePointRight(TYPE_OTHER).intValue());
            }
            long timeInMillis = gregorianCalendar.getTimeInMillis();
            return i != 0 ? timeInMillis - ((long) (60000 * i)) : timeInMillis;
        } else {
            throw new ParseException("Invalid date/time format: " + str, TYPE_DASH);
        }
    }

    public static long parseXsDuration(String str) {
        int i = TYPE_SS;
        double d = 0.0d;
        Matcher matcher = XS_DURATION_PATTERN.matcher(str);
        if (!matcher.matches()) {
            return (long) ((Double.parseDouble(str) * 3600.0d) * 1000.0d);
        }
        if (TextUtils.isEmpty(matcher.group(TYPE_SS))) {
            i = TYPE_DASH;
        }
        String group = matcher.group(TYPE_OTHER);
        double parseDouble = group != null ? Double.parseDouble(group) * 3.1556908E7d : 0.0d;
        String group2 = matcher.group(5);
        double parseDouble2 = (group2 != null ? Double.parseDouble(group2) * 2629739.0d : 0.0d) + parseDouble;
        group = matcher.group(7);
        parseDouble2 += group != null ? Double.parseDouble(group) * 86400.0d : 0.0d;
        group = matcher.group(10);
        parseDouble2 += group != null ? Double.parseDouble(group) * 3600.0d : 0.0d;
        group = matcher.group(12);
        parseDouble = (group != null ? Double.parseDouble(group) * 60.0d : 0.0d) + parseDouble2;
        String group3 = matcher.group(14);
        if (group3 != null) {
            d = Double.parseDouble(group3);
        }
        long j = (long) ((parseDouble + d) * 1000.0d);
        return i != 0 ? -j : j;
    }

    public static long scaleLargeTimestamp(long j, long j2, long j3) {
        return (j3 < j2 || j3 % j2 != 0) ? (j3 >= j2 || j2 % j3 != 0) ? (long) ((((double) j2) / ((double) j3)) * ((double) j)) : (j2 / j3) * j : j / (j3 / j2);
    }

    public static long[] scaleLargeTimestamps(List<Long> list, long j, long j2) {
        long[] jArr = new long[list.size()];
        long j3;
        int i;
        if (j2 >= j && j2 % j == 0) {
            j3 = j2 / j;
            for (i = TYPE_DASH; i < jArr.length; i += TYPE_SS) {
                jArr[i] = ((Long) list.get(i)).longValue() / j3;
            }
        } else if (j2 >= j || j % j2 != 0) {
            double d = ((double) j) / ((double) j2);
            for (i = TYPE_DASH; i < jArr.length; i += TYPE_SS) {
                jArr[i] = (long) (((double) ((Long) list.get(i)).longValue()) * d);
            }
        } else {
            j3 = j / j2;
            for (i = TYPE_DASH; i < jArr.length; i += TYPE_SS) {
                jArr[i] = ((Long) list.get(i)).longValue() * j3;
            }
        }
        return jArr;
    }

    public static void scaleLargeTimestampsInPlace(long[] jArr, long j, long j2) {
        int i = TYPE_DASH;
        long j3;
        if (j2 >= j && j2 % j == 0) {
            j3 = j2 / j;
            while (i < jArr.length) {
                jArr[i] = jArr[i] / j3;
                i += TYPE_SS;
            }
        } else if (j2 >= j || j % j2 != 0) {
            double d = ((double) j) / ((double) j2);
            while (i < jArr.length) {
                jArr[i] = (long) (((double) jArr[i]) * d);
                i += TYPE_SS;
            }
        } else {
            j3 = j / j2;
            while (i < jArr.length) {
                jArr[i] = jArr[i] * j3;
                i += TYPE_SS;
            }
        }
    }

    private static boolean shouldEscapeCharacter(char c) {
        switch (c) {
            case NalUnitTypes.NAL_TYPE_PPS_NUT /*34*/:
            case NalUnitTypes.NAL_TYPE_EOB_NUT /*37*/:
            case NalUnitTypes.NAL_TYPE_RSV_NVCL42 /*42*/:
            case MotionEventCompat.AXIS_GENERIC_16 /*47*/:
            case ':':
            case '<':
            case '>':
            case '?':
            case '\\':
            case '|':
                return true;
            default:
                return false;
        }
    }

    public static int[] toArray(List<Integer> list) {
        if (list == null) {
            return null;
        }
        int size = list.size();
        int[] iArr = new int[size];
        for (int i = TYPE_DASH; i < size; i += TYPE_SS) {
            iArr[i] = ((Integer) list.get(i)).intValue();
        }
        return iArr;
    }

    public static byte[] toByteArray(InputStream inputStream) {
        byte[] bArr = new byte[ItemAnimator.FLAG_APPEARED_IN_PRE_LAYOUT];
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        while (true) {
            int read = inputStream.read(bArr);
            if (read == -1) {
                return byteArrayOutputStream.toByteArray();
            }
            byteArrayOutputStream.write(bArr, TYPE_DASH, read);
        }
    }

    public static String toLowerInvariant(String str) {
        return str == null ? null : str.toLowerCase(Locale.US);
    }

    public static String unescapeFileName(String str) {
        int i;
        int length = str.length();
        int i2 = TYPE_DASH;
        for (i = TYPE_DASH; i < length; i += TYPE_SS) {
            if (str.charAt(i) == '%') {
                i2 += TYPE_SS;
            }
        }
        if (i2 == 0) {
            return str;
        }
        i = length - (i2 * TYPE_HLS);
        StringBuilder stringBuilder = new StringBuilder(i);
        Matcher matcher = ESCAPED_CHARACTER_PATTERN.matcher(str);
        i2 = TYPE_DASH;
        for (int i3 = i2; i3 > 0 && matcher.find(); i3--) {
            stringBuilder.append(str, i2, matcher.start()).append((char) Integer.parseInt(matcher.group(TYPE_SS), 16));
            i2 = matcher.end();
        }
        if (i2 < length) {
            stringBuilder.append(str, i2, length);
        }
        return stringBuilder.length() != i ? null : stringBuilder.toString();
    }
}
