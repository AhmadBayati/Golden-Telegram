package com.hanista.mobogram.mobo.persianmaterialdatetimepicker.p017a;

/* renamed from: com.hanista.mobogram.mobo.persianmaterialdatetimepicker.a.c */
public class PersianCalendarUtils {
    public static long m2058a(double d, double d2) {
        return (long) (d - (Math.floor(d / d2) * d2));
    }

    public static long m2059a(long j) {
        long a = j - PersianCalendarUtils.m2060a(475, 0, 1);
        long a2 = PersianCalendarUtils.m2058a((double) a, 1029983.0d);
        a = ((((long) Math.floor(((double) a) / 1029983.0d)) * 2820) + 474) + (a2 != 1029982 ? (long) Math.floor(((((double) a2) * 2816.0d) + 1031337.0d) / 1028522.0d) : 2820);
        a2 = (1 + j) - PersianCalendarUtils.m2060a(a, 0, 1);
        int ceil = (int) (a2 > 186 ? Math.ceil(((double) (a2 - 6)) / 30.0d) - 1.0d : Math.ceil(((double) a2) / 31.0d) - 1.0d);
        return ((long) ((int) (j - (PersianCalendarUtils.m2060a(a, ceil, 1) - 1)))) | ((a << 16) | ((long) (ceil << 8)));
    }

    public static long m2060a(long j, int i, int i2) {
        return (((long) (i < 7 ? i * 31 : (i * 30) + 6)) + ((1029983 * ((long) Math.floor(((double) (j - 474)) / 2820.0d))) + (((365 * ((PersianCalendarUtils.m2058a((double) (j - 474), 2820.0d) + 474) - 1)) + ((long) Math.floor(((double) ((682 * (PersianCalendarUtils.m2058a((double) (j - 474), 2820.0d) + 474)) - 110)) / 2816.0d))) + 1948320))) + ((long) i2);
    }

    public static boolean m2061a(int i) {
        return PersianCalendarUtils.m2058a((38.0d + ((double) (PersianCalendarUtils.m2058a((double) (((long) i) - 474), 2820.0d) + 474))) * 682.0d, 2816.0d) < 682;
    }
}
