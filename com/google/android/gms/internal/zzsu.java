package com.google.android.gms.internal;

import android.content.Context;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.common.zzc;
import com.google.android.gms.dynamic.zzd;
import com.google.android.gms.dynamic.zze;
import java.lang.reflect.Field;

public final class zzsu {
    public static final zzb OA;
    public static final zzb OB;
    public static final zzb OC;
    private static zzsv Ow;
    private static final zza Ox;
    public static final zzb Oy;
    public static final zzb Oz;
    private final Context OD;

    /* renamed from: com.google.android.gms.internal.zzsu.1 */
    class C02121 implements zza {
        C02121() {
        }

        public int zzaa(Context context, String str) {
            return zzsu.zzaa(context, str);
        }

        public int zzc(Context context, String str, boolean z) {
            return zzsu.zzc(context, str, z);
        }
    }

    public interface zzb {

        public interface zza {
            int zzaa(Context context, String str);

            int zzc(Context context, String str, boolean z);
        }

        public static class zzb {
            public int OF;
            public int OG;
            public int OH;

            public zzb() {
                this.OF = 0;
                this.OG = 0;
                this.OH = 0;
            }
        }

        zzb zza(Context context, String str, zza com_google_android_gms_internal_zzsu_zzb_zza);
    }

    /* renamed from: com.google.android.gms.internal.zzsu.2 */
    class C02132 implements zzb {
        C02132() {
        }

        public zzb zza(Context context, String str, zza com_google_android_gms_internal_zzsu_zzb_zza) {
            zzb com_google_android_gms_internal_zzsu_zzb_zzb = new zzb();
            com_google_android_gms_internal_zzsu_zzb_zzb.OG = com_google_android_gms_internal_zzsu_zzb_zza.zzc(context, str, true);
            if (com_google_android_gms_internal_zzsu_zzb_zzb.OG != 0) {
                com_google_android_gms_internal_zzsu_zzb_zzb.OH = 1;
            } else {
                com_google_android_gms_internal_zzsu_zzb_zzb.OF = com_google_android_gms_internal_zzsu_zzb_zza.zzaa(context, str);
                if (com_google_android_gms_internal_zzsu_zzb_zzb.OF != 0) {
                    com_google_android_gms_internal_zzsu_zzb_zzb.OH = -1;
                }
            }
            return com_google_android_gms_internal_zzsu_zzb_zzb;
        }
    }

    /* renamed from: com.google.android.gms.internal.zzsu.3 */
    class C02143 implements zzb {
        C02143() {
        }

        public zzb zza(Context context, String str, zza com_google_android_gms_internal_zzsu_zzb_zza) {
            zzb com_google_android_gms_internal_zzsu_zzb_zzb = new zzb();
            com_google_android_gms_internal_zzsu_zzb_zzb.OF = com_google_android_gms_internal_zzsu_zzb_zza.zzaa(context, str);
            if (com_google_android_gms_internal_zzsu_zzb_zzb.OF != 0) {
                com_google_android_gms_internal_zzsu_zzb_zzb.OH = -1;
            } else {
                com_google_android_gms_internal_zzsu_zzb_zzb.OG = com_google_android_gms_internal_zzsu_zzb_zza.zzc(context, str, true);
                if (com_google_android_gms_internal_zzsu_zzb_zzb.OG != 0) {
                    com_google_android_gms_internal_zzsu_zzb_zzb.OH = 1;
                }
            }
            return com_google_android_gms_internal_zzsu_zzb_zzb;
        }
    }

    /* renamed from: com.google.android.gms.internal.zzsu.4 */
    class C02154 implements zzb {
        C02154() {
        }

        public zzb zza(Context context, String str, zza com_google_android_gms_internal_zzsu_zzb_zza) {
            zzb com_google_android_gms_internal_zzsu_zzb_zzb = new zzb();
            com_google_android_gms_internal_zzsu_zzb_zzb.OF = com_google_android_gms_internal_zzsu_zzb_zza.zzaa(context, str);
            com_google_android_gms_internal_zzsu_zzb_zzb.OG = com_google_android_gms_internal_zzsu_zzb_zza.zzc(context, str, true);
            if (com_google_android_gms_internal_zzsu_zzb_zzb.OF == 0 && com_google_android_gms_internal_zzsu_zzb_zzb.OG == 0) {
                com_google_android_gms_internal_zzsu_zzb_zzb.OH = 0;
            } else if (com_google_android_gms_internal_zzsu_zzb_zzb.OF >= com_google_android_gms_internal_zzsu_zzb_zzb.OG) {
                com_google_android_gms_internal_zzsu_zzb_zzb.OH = -1;
            } else {
                com_google_android_gms_internal_zzsu_zzb_zzb.OH = 1;
            }
            return com_google_android_gms_internal_zzsu_zzb_zzb;
        }
    }

    /* renamed from: com.google.android.gms.internal.zzsu.5 */
    class C02165 implements zzb {
        C02165() {
        }

        public zzb zza(Context context, String str, zza com_google_android_gms_internal_zzsu_zzb_zza) {
            zzb com_google_android_gms_internal_zzsu_zzb_zzb = new zzb();
            com_google_android_gms_internal_zzsu_zzb_zzb.OF = com_google_android_gms_internal_zzsu_zzb_zza.zzaa(context, str);
            com_google_android_gms_internal_zzsu_zzb_zzb.OG = com_google_android_gms_internal_zzsu_zzb_zza.zzc(context, str, true);
            if (com_google_android_gms_internal_zzsu_zzb_zzb.OF == 0 && com_google_android_gms_internal_zzsu_zzb_zzb.OG == 0) {
                com_google_android_gms_internal_zzsu_zzb_zzb.OH = 0;
            } else if (com_google_android_gms_internal_zzsu_zzb_zzb.OG >= com_google_android_gms_internal_zzsu_zzb_zzb.OF) {
                com_google_android_gms_internal_zzsu_zzb_zzb.OH = 1;
            } else {
                com_google_android_gms_internal_zzsu_zzb_zzb.OH = -1;
            }
            return com_google_android_gms_internal_zzsu_zzb_zzb;
        }
    }

    /* renamed from: com.google.android.gms.internal.zzsu.6 */
    class C02176 implements zzb {
        C02176() {
        }

        public zzb zza(Context context, String str, zza com_google_android_gms_internal_zzsu_zzb_zza) {
            zzb com_google_android_gms_internal_zzsu_zzb_zzb = new zzb();
            com_google_android_gms_internal_zzsu_zzb_zzb.OF = com_google_android_gms_internal_zzsu_zzb_zza.zzaa(context, str);
            if (com_google_android_gms_internal_zzsu_zzb_zzb.OF != 0) {
                com_google_android_gms_internal_zzsu_zzb_zzb.OG = com_google_android_gms_internal_zzsu_zzb_zza.zzc(context, str, false);
            } else {
                com_google_android_gms_internal_zzsu_zzb_zzb.OG = com_google_android_gms_internal_zzsu_zzb_zza.zzc(context, str, true);
            }
            if (com_google_android_gms_internal_zzsu_zzb_zzb.OF == 0 && com_google_android_gms_internal_zzsu_zzb_zzb.OG == 0) {
                com_google_android_gms_internal_zzsu_zzb_zzb.OH = 0;
            } else if (com_google_android_gms_internal_zzsu_zzb_zzb.OG >= com_google_android_gms_internal_zzsu_zzb_zzb.OF) {
                com_google_android_gms_internal_zzsu_zzb_zzb.OH = 1;
            } else {
                com_google_android_gms_internal_zzsu_zzb_zzb.OH = -1;
            }
            return com_google_android_gms_internal_zzsu_zzb_zzb;
        }
    }

    /* renamed from: com.google.android.gms.internal.zzsu.7 */
    class C02187 implements zza {
        final /* synthetic */ int OE;

        C02187(int i) {
            this.OE = i;
        }

        public int zzaa(Context context, String str) {
            return this.OE;
        }

        public int zzc(Context context, String str, boolean z) {
            return 0;
        }
    }

    public static class zza extends Exception {
        private zza(String str) {
            super(str);
        }

        private zza(String str, Throwable th) {
            super(str, th);
        }
    }

    static {
        Ox = new C02121();
        Oy = new C02132();
        Oz = new C02143();
        OA = new C02154();
        OB = new C02165();
        OC = new C02176();
    }

    private zzsu(Context context) {
        this.OD = (Context) zzac.zzy(context);
    }

    public static zzsu zza(Context context, zzb com_google_android_gms_internal_zzsu_zzb, String str) {
        zzb zza = com_google_android_gms_internal_zzsu_zzb.zza(context, str, Ox);
        Log.i("DynamiteModule", new StringBuilder((String.valueOf(str).length() + 68) + String.valueOf(str).length()).append("Considering local module ").append(str).append(":").append(zza.OF).append(" and remote module ").append(str).append(":").append(zza.OG).toString());
        if (zza.OH == 0 || ((zza.OH == -1 && zza.OF == 0) || (zza.OH == 1 && zza.OG == 0))) {
            throw new zza(null);
        } else if (zza.OH == -1) {
            return zzac(context, str);
        } else {
            if (zza.OH == 1) {
                try {
                    return zza(context, str, zza.OG);
                } catch (Throwable e) {
                    Throwable th = e;
                    String str2 = "DynamiteModule";
                    String str3 = "Failed to load remote module: ";
                    String valueOf = String.valueOf(th.getMessage());
                    Log.w(str2, valueOf.length() != 0 ? str3.concat(valueOf) : new String(str3));
                    if (zza.OF != 0 && com_google_android_gms_internal_zzsu_zzb.zza(context, str, new C02187(zza.OF)).OH == -1) {
                        return zzac(context, str);
                    }
                    throw new zza(th, null);
                }
            }
            throw new zza(null);
        }
    }

    private static zzsu zza(Context context, String str, int i) {
        Log.i("DynamiteModule", new StringBuilder(String.valueOf(str).length() + 51).append("Selected remote version of ").append(str).append(", version >= ").append(i).toString());
        zzsv zzcv = zzcv(context);
        if (zzcv == null) {
            throw new zza(null);
        }
        try {
            zzd zza = zzcv.zza(zze.zzac(context), str, i);
            if (zze.zzae(zza) != null) {
                return new zzsu((Context) zze.zzae(zza));
            }
            throw new zza(null);
        } catch (Throwable e) {
            throw new zza(e, null);
        }
    }

    public static int zzaa(Context context, String str) {
        String valueOf;
        String valueOf2;
        try {
            ClassLoader classLoader = context.getApplicationContext().getClassLoader();
            valueOf = String.valueOf("com.google.android.gms.dynamite.descriptors.");
            valueOf2 = String.valueOf("ModuleDescriptor");
            Class loadClass = classLoader.loadClass(new StringBuilder(((String.valueOf(valueOf).length() + 1) + String.valueOf(str).length()) + String.valueOf(valueOf2).length()).append(valueOf).append(str).append(".").append(valueOf2).toString());
            Field declaredField = loadClass.getDeclaredField("MODULE_ID");
            Field declaredField2 = loadClass.getDeclaredField("MODULE_VERSION");
            if (declaredField.get(null).equals(str)) {
                return declaredField2.getInt(null);
            }
            valueOf = String.valueOf(declaredField.get(null));
            Log.e("DynamiteModule", new StringBuilder((String.valueOf(valueOf).length() + 51) + String.valueOf(str).length()).append("Module descriptor id '").append(valueOf).append("' didn't match expected id '").append(str).append("'").toString());
            return 0;
        } catch (ClassNotFoundException e) {
            Log.w("DynamiteModule", new StringBuilder(String.valueOf(str).length() + 45).append("Local module descriptor class for ").append(str).append(" not found.").toString());
            return 0;
        } catch (Exception e2) {
            valueOf = "DynamiteModule";
            valueOf2 = "Failed to load module descriptor class: ";
            String valueOf3 = String.valueOf(e2.getMessage());
            Log.e(valueOf, valueOf3.length() != 0 ? valueOf2.concat(valueOf3) : new String(valueOf2));
            return 0;
        }
    }

    public static int zzab(Context context, String str) {
        return zzc(context, str, false);
    }

    private static zzsu zzac(Context context, String str) {
        String str2 = "DynamiteModule";
        String str3 = "Selected local version of ";
        String valueOf = String.valueOf(str);
        Log.i(str2, valueOf.length() != 0 ? str3.concat(valueOf) : new String(str3));
        return new zzsu(context.getApplicationContext());
    }

    public static int zzc(Context context, String str, boolean z) {
        zzsv zzcv = zzcv(context);
        if (zzcv == null) {
            return 0;
        }
        try {
            return zzcv.zza(zze.zzac(context), str, z);
        } catch (RemoteException e) {
            String str2 = "DynamiteModule";
            String str3 = "Failed to retrieve remote module version: ";
            String valueOf = String.valueOf(e.getMessage());
            Log.w(str2, valueOf.length() != 0 ? str3.concat(valueOf) : new String(str3));
            return 0;
        }
    }

    private static zzsv zzcv(Context context) {
        synchronized (zzsu.class) {
            zzsv com_google_android_gms_internal_zzsv;
            if (Ow != null) {
                com_google_android_gms_internal_zzsv = Ow;
                return com_google_android_gms_internal_zzsv;
            } else if (zzc.zzapd().isGooglePlayServicesAvailable(context) != 0) {
                return null;
            } else {
                try {
                    com_google_android_gms_internal_zzsv = com.google.android.gms.internal.zzsv.zza.zzff((IBinder) context.createPackageContext(GooglePlayServicesUtil.GOOGLE_PLAY_SERVICES_PACKAGE, 3).getClassLoader().loadClass("com.google.android.gms.chimera.container.DynamiteLoaderImpl").newInstance());
                    if (com_google_android_gms_internal_zzsv != null) {
                        Ow = com_google_android_gms_internal_zzsv;
                        return com_google_android_gms_internal_zzsv;
                    }
                } catch (Exception e) {
                    String str = "DynamiteModule";
                    String str2 = "Failed to load IDynamiteLoader from GmsCore: ";
                    String valueOf = String.valueOf(e.getMessage());
                    Log.e(str, valueOf.length() != 0 ? str2.concat(valueOf) : new String(str2));
                }
                return null;
            }
        }
    }

    public Context zzbdy() {
        return this.OD;
    }

    public IBinder zzjd(String str) {
        Throwable e;
        String str2;
        String valueOf;
        try {
            return (IBinder) this.OD.getClassLoader().loadClass(str).newInstance();
        } catch (ClassNotFoundException e2) {
            e = e2;
            str2 = "Failed to instantiate module class: ";
            valueOf = String.valueOf(str);
            throw new zza(e, null);
        } catch (InstantiationException e3) {
            e = e3;
            str2 = "Failed to instantiate module class: ";
            valueOf = String.valueOf(str);
            if (valueOf.length() != 0) {
            }
            throw new zza(e, null);
        } catch (IllegalAccessException e4) {
            e = e4;
            str2 = "Failed to instantiate module class: ";
            valueOf = String.valueOf(str);
            if (valueOf.length() != 0) {
            }
            throw new zza(e, null);
        }
    }
}
