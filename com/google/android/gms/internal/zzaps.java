package com.google.android.gms.internal;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public final class zzaps implements zzaou {
    private final zzapb bkM;
    private final zzapc bkV;
    private final zzaoa bkX;

    static abstract class zzb {
        final boolean bmN;
        final boolean bmO;
        final String name;

        protected zzb(String str, boolean z, boolean z2) {
            this.name = str;
            this.bmN = z;
            this.bmO = z2;
        }

        abstract void zza(zzapy com_google_android_gms_internal_zzapy, Object obj);

        abstract void zza(zzaqa com_google_android_gms_internal_zzaqa, Object obj);

        abstract boolean zzct(Object obj);
    }

    /* renamed from: com.google.android.gms.internal.zzaps.1 */
    class C01671 extends zzb {
        final zzaot<?> bmG;
        final /* synthetic */ zzaob bmH;
        final /* synthetic */ Field bmI;
        final /* synthetic */ zzapx bmJ;
        final /* synthetic */ boolean bmK;
        final /* synthetic */ zzaps bmL;

        C01671(zzaps com_google_android_gms_internal_zzaps, String str, boolean z, boolean z2, zzaob com_google_android_gms_internal_zzaob, Field field, zzapx com_google_android_gms_internal_zzapx, boolean z3) {
            this.bmL = com_google_android_gms_internal_zzaps;
            this.bmH = com_google_android_gms_internal_zzaob;
            this.bmI = field;
            this.bmJ = com_google_android_gms_internal_zzapx;
            this.bmK = z3;
            super(str, z, z2);
            this.bmG = this.bmL.zza(this.bmH, this.bmI, this.bmJ);
        }

        void zza(zzapy com_google_android_gms_internal_zzapy, Object obj) {
            Object zzb = this.bmG.zzb(com_google_android_gms_internal_zzapy);
            if (zzb != null || !this.bmK) {
                this.bmI.set(obj, zzb);
            }
        }

        void zza(zzaqa com_google_android_gms_internal_zzaqa, Object obj) {
            new zzapv(this.bmH, this.bmG, this.bmJ.bz()).zza(com_google_android_gms_internal_zzaqa, this.bmI.get(obj));
        }

        public boolean zzct(Object obj) {
            return this.bmN && this.bmI.get(obj) != obj;
        }
    }

    public static final class zza<T> extends zzaot<T> {
        private final Map<String, zzb> bmM;
        private final zzapg<T> bmt;

        private zza(zzapg<T> com_google_android_gms_internal_zzapg_T, Map<String, zzb> map) {
            this.bmt = com_google_android_gms_internal_zzapg_T;
            this.bmM = map;
        }

        public void zza(zzaqa com_google_android_gms_internal_zzaqa, T t) {
            if (t == null) {
                com_google_android_gms_internal_zzaqa.bx();
                return;
            }
            com_google_android_gms_internal_zzaqa.bv();
            try {
                for (zzb com_google_android_gms_internal_zzaps_zzb : this.bmM.values()) {
                    if (com_google_android_gms_internal_zzaps_zzb.zzct(t)) {
                        com_google_android_gms_internal_zzaqa.zzus(com_google_android_gms_internal_zzaps_zzb.name);
                        com_google_android_gms_internal_zzaps_zzb.zza(com_google_android_gms_internal_zzaqa, (Object) t);
                    }
                }
                com_google_android_gms_internal_zzaqa.bw();
            } catch (IllegalAccessException e) {
                throw new AssertionError();
            }
        }

        public T zzb(zzapy com_google_android_gms_internal_zzapy) {
            if (com_google_android_gms_internal_zzapy.bn() == zzapz.NULL) {
                com_google_android_gms_internal_zzapy.nextNull();
                return null;
            }
            T bg = this.bmt.bg();
            try {
                com_google_android_gms_internal_zzapy.beginObject();
                while (com_google_android_gms_internal_zzapy.hasNext()) {
                    zzb com_google_android_gms_internal_zzaps_zzb = (zzb) this.bmM.get(com_google_android_gms_internal_zzapy.nextName());
                    if (com_google_android_gms_internal_zzaps_zzb == null || !com_google_android_gms_internal_zzaps_zzb.bmO) {
                        com_google_android_gms_internal_zzapy.skipValue();
                    } else {
                        com_google_android_gms_internal_zzaps_zzb.zza(com_google_android_gms_internal_zzapy, (Object) bg);
                    }
                }
                com_google_android_gms_internal_zzapy.endObject();
                return bg;
            } catch (Throwable e) {
                throw new zzaoq(e);
            } catch (IllegalAccessException e2) {
                throw new AssertionError(e2);
            }
        }
    }

    public zzaps(zzapb com_google_android_gms_internal_zzapb, zzaoa com_google_android_gms_internal_zzaoa, zzapc com_google_android_gms_internal_zzapc) {
        this.bkM = com_google_android_gms_internal_zzapb;
        this.bkX = com_google_android_gms_internal_zzaoa;
        this.bkV = com_google_android_gms_internal_zzapc;
    }

    private zzaot<?> zza(zzaob com_google_android_gms_internal_zzaob, Field field, zzapx<?> com_google_android_gms_internal_zzapx_) {
        zzaov com_google_android_gms_internal_zzaov = (zzaov) field.getAnnotation(zzaov.class);
        if (com_google_android_gms_internal_zzaov != null) {
            zzaot<?> zza = zzapn.zza(this.bkM, com_google_android_gms_internal_zzaob, com_google_android_gms_internal_zzapx_, com_google_android_gms_internal_zzaov);
            if (zza != null) {
                return zza;
            }
        }
        return com_google_android_gms_internal_zzaob.zza((zzapx) com_google_android_gms_internal_zzapx_);
    }

    private zzb zza(zzaob com_google_android_gms_internal_zzaob, Field field, String str, zzapx<?> com_google_android_gms_internal_zzapx_, boolean z, boolean z2) {
        return new C01671(this, str, z, z2, com_google_android_gms_internal_zzaob, field, com_google_android_gms_internal_zzapx_, zzaph.zzk(com_google_android_gms_internal_zzapx_.by()));
    }

    static List<String> zza(zzaoa com_google_android_gms_internal_zzaoa, Field field) {
        zzaow com_google_android_gms_internal_zzaow = (zzaow) field.getAnnotation(zzaow.class);
        List<String> linkedList = new LinkedList();
        if (com_google_android_gms_internal_zzaow == null) {
            linkedList.add(com_google_android_gms_internal_zzaoa.zzc(field));
        } else {
            linkedList.add(com_google_android_gms_internal_zzaow.value());
            for (Object add : com_google_android_gms_internal_zzaow.be()) {
                linkedList.add(add);
            }
        }
        return linkedList;
    }

    private Map<String, zzb> zza(zzaob com_google_android_gms_internal_zzaob, zzapx<?> com_google_android_gms_internal_zzapx_, Class<?> cls) {
        Map<String, zzb> linkedHashMap = new LinkedHashMap();
        if (cls.isInterface()) {
            return linkedHashMap;
        }
        Type bz = com_google_android_gms_internal_zzapx_.bz();
        Class by;
        while (by != Object.class) {
            for (Field field : by.getDeclaredFields()) {
                boolean zza = zza(field, true);
                boolean zza2 = zza(field, false);
                if (zza || zza2) {
                    field.setAccessible(true);
                    Type zza3 = zzapa.zza(r19.bz(), by, field.getGenericType());
                    List zzd = zzd(field);
                    zzb com_google_android_gms_internal_zzaps_zzb = null;
                    int i = 0;
                    while (i < zzd.size()) {
                        String str = (String) zzd.get(i);
                        if (i != 0) {
                            zza = false;
                        }
                        zzb com_google_android_gms_internal_zzaps_zzb2 = (zzb) linkedHashMap.put(str, zza(com_google_android_gms_internal_zzaob, field, str, zzapx.zzl(zza3), zza, zza2));
                        if (com_google_android_gms_internal_zzaps_zzb != null) {
                            com_google_android_gms_internal_zzaps_zzb2 = com_google_android_gms_internal_zzaps_zzb;
                        }
                        i++;
                        com_google_android_gms_internal_zzaps_zzb = com_google_android_gms_internal_zzaps_zzb2;
                    }
                    if (com_google_android_gms_internal_zzaps_zzb != null) {
                        String valueOf = String.valueOf(bz);
                        String str2 = com_google_android_gms_internal_zzaps_zzb.name;
                        throw new IllegalArgumentException(new StringBuilder((String.valueOf(valueOf).length() + 37) + String.valueOf(str2).length()).append(valueOf).append(" declares multiple JSON fields named ").append(str2).toString());
                    }
                }
            }
            zzapx zzl = zzapx.zzl(zzapa.zza(zzl.bz(), by, by.getGenericSuperclass()));
            by = zzl.by();
        }
        return linkedHashMap;
    }

    static boolean zza(Field field, boolean z, zzapc com_google_android_gms_internal_zzapc) {
        return (com_google_android_gms_internal_zzapc.zza(field.getType(), z) || com_google_android_gms_internal_zzapc.zza(field, z)) ? false : true;
    }

    private List<String> zzd(Field field) {
        return zza(this.bkX, field);
    }

    public <T> zzaot<T> zza(zzaob com_google_android_gms_internal_zzaob, zzapx<T> com_google_android_gms_internal_zzapx_T) {
        Class by = com_google_android_gms_internal_zzapx_T.by();
        return !Object.class.isAssignableFrom(by) ? null : new zza(zza(com_google_android_gms_internal_zzaob, (zzapx) com_google_android_gms_internal_zzapx_T, by), null);
    }

    public boolean zza(Field field, boolean z) {
        return zza(field, z, this.bkV);
    }
}
