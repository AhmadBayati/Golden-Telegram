package com.google.android.gms.internal;

import android.os.Binder;

public abstract class zzrs<T> {
    private static String READ_PERMISSION;
    private static zza zB;
    private static int zC;
    private static final Object zzaok;
    private T zD;
    protected final String zzbaf;
    protected final T zzbag;

    /* renamed from: com.google.android.gms.internal.zzrs.1 */
    class C02041 extends zzrs<Boolean> {
        C02041(String str, Boolean bool) {
            super(str, bool);
        }

        protected /* synthetic */ Object zzhg(String str) {
            return zzhh(str);
        }

        protected Boolean zzhh(String str) {
            return null.zza(this.zzbaf, (Boolean) this.zzbag);
        }
    }

    /* renamed from: com.google.android.gms.internal.zzrs.2 */
    class C02052 extends zzrs<Long> {
        C02052(String str, Long l) {
            super(str, l);
        }

        protected /* synthetic */ Object zzhg(String str) {
            return zzhi(str);
        }

        protected Long zzhi(String str) {
            return null.getLong(this.zzbaf, (Long) this.zzbag);
        }
    }

    /* renamed from: com.google.android.gms.internal.zzrs.3 */
    class C02063 extends zzrs<Integer> {
        C02063(String str, Integer num) {
            super(str, num);
        }

        protected /* synthetic */ Object zzhg(String str) {
            return zzhj(str);
        }

        protected Integer zzhj(String str) {
            return null.zzb(this.zzbaf, (Integer) this.zzbag);
        }
    }

    /* renamed from: com.google.android.gms.internal.zzrs.4 */
    class C02074 extends zzrs<Float> {
        C02074(String str, Float f) {
            super(str, f);
        }

        protected /* synthetic */ Object zzhg(String str) {
            return zzhk(str);
        }

        protected Float zzhk(String str) {
            return null.zzb(this.zzbaf, (Float) this.zzbag);
        }
    }

    /* renamed from: com.google.android.gms.internal.zzrs.5 */
    class C02085 extends zzrs<String> {
        C02085(String str, String str2) {
            super(str, str2);
        }

        protected /* synthetic */ Object zzhg(String str) {
            return zzhl(str);
        }

        protected String zzhl(String str) {
            return null.getString(this.zzbaf, (String) this.zzbag);
        }
    }

    private interface zza {
        Long getLong(String str, Long l);

        String getString(String str, String str2);

        Boolean zza(String str, Boolean bool);

        Float zzb(String str, Float f);

        Integer zzb(String str, Integer num);
    }

    static {
        zzaok = new Object();
        zB = null;
        zC = 0;
        READ_PERMISSION = "com.google.android.providers.gsf.permission.READ_GSERVICES";
    }

    protected zzrs(String str, T t) {
        this.zD = null;
        this.zzbaf = str;
        this.zzbag = t;
    }

    public static zzrs<Float> zza(String str, Float f) {
        return new C02074(str, f);
    }

    public static zzrs<Integer> zza(String str, Integer num) {
        return new C02063(str, num);
    }

    public static zzrs<Long> zza(String str, Long l) {
        return new C02052(str, l);
    }

    public static zzrs<String> zzab(String str, String str2) {
        return new C02085(str, str2);
    }

    public static zzrs<Boolean> zzm(String str, boolean z) {
        return new C02041(str, Boolean.valueOf(z));
    }

    public final T get() {
        T zzhg;
        long clearCallingIdentity;
        try {
            zzhg = zzhg(this.zzbaf);
        } catch (SecurityException e) {
            clearCallingIdentity = Binder.clearCallingIdentity();
            zzhg = zzhg(this.zzbaf);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
        return zzhg;
    }

    protected abstract T zzhg(String str);
}
