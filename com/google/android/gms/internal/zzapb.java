package com.google.android.gms.internal;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

public final class zzapb {
    private final Map<Type, zzaod<?>> bkY;

    /* renamed from: com.google.android.gms.internal.zzapb.1 */
    class C01431 implements zzapg<T> {
        final /* synthetic */ zzapb blA;
        final /* synthetic */ zzaod bly;
        final /* synthetic */ Type blz;

        C01431(zzapb com_google_android_gms_internal_zzapb, zzaod com_google_android_gms_internal_zzaod, Type type) {
            this.blA = com_google_android_gms_internal_zzapb;
            this.bly = com_google_android_gms_internal_zzaod;
            this.blz = type;
        }

        public T bg() {
            return this.bly.zza(this.blz);
        }
    }

    /* renamed from: com.google.android.gms.internal.zzapb.2 */
    class C01442 implements zzapg<T> {
        final /* synthetic */ zzapb blA;

        C01442(zzapb com_google_android_gms_internal_zzapb) {
            this.blA = com_google_android_gms_internal_zzapb;
        }

        public T bg() {
            return new LinkedHashMap();
        }
    }

    /* renamed from: com.google.android.gms.internal.zzapb.3 */
    class C01453 implements zzapg<T> {
        final /* synthetic */ zzapb blA;

        C01453(zzapb com_google_android_gms_internal_zzapb) {
            this.blA = com_google_android_gms_internal_zzapb;
        }

        public T bg() {
            return new zzapf();
        }
    }

    /* renamed from: com.google.android.gms.internal.zzapb.4 */
    class C01464 implements zzapg<T> {
        final /* synthetic */ zzapb blA;
        private final zzapj blB;
        final /* synthetic */ Class blC;
        final /* synthetic */ Type blz;

        C01464(zzapb com_google_android_gms_internal_zzapb, Class cls, Type type) {
            this.blA = com_google_android_gms_internal_zzapb;
            this.blC = cls;
            this.blz = type;
            this.blB = zzapj.bl();
        }

        public T bg() {
            try {
                return this.blB.zzf(this.blC);
            } catch (Throwable e) {
                String valueOf = String.valueOf(this.blz);
                throw new RuntimeException(new StringBuilder(String.valueOf(valueOf).length() + 116).append("Unable to invoke no-args constructor for ").append(valueOf).append(". ").append("Register an InstanceCreator with Gson for this type may fix this problem.").toString(), e);
            }
        }
    }

    /* renamed from: com.google.android.gms.internal.zzapb.5 */
    class C01475 implements zzapg<T> {
        final /* synthetic */ zzapb blA;
        final /* synthetic */ zzaod blD;
        final /* synthetic */ Type blz;

        C01475(zzapb com_google_android_gms_internal_zzapb, zzaod com_google_android_gms_internal_zzaod, Type type) {
            this.blA = com_google_android_gms_internal_zzapb;
            this.blD = com_google_android_gms_internal_zzaod;
            this.blz = type;
        }

        public T bg() {
            return this.blD.zza(this.blz);
        }
    }

    /* renamed from: com.google.android.gms.internal.zzapb.6 */
    class C01486 implements zzapg<T> {
        final /* synthetic */ zzapb blA;
        final /* synthetic */ Constructor blE;

        C01486(zzapb com_google_android_gms_internal_zzapb, Constructor constructor) {
            this.blA = com_google_android_gms_internal_zzapb;
            this.blE = constructor;
        }

        public T bg() {
            String valueOf;
            try {
                return this.blE.newInstance(null);
            } catch (Throwable e) {
                valueOf = String.valueOf(this.blE);
                throw new RuntimeException(new StringBuilder(String.valueOf(valueOf).length() + 30).append("Failed to invoke ").append(valueOf).append(" with no args").toString(), e);
            } catch (InvocationTargetException e2) {
                valueOf = String.valueOf(this.blE);
                throw new RuntimeException(new StringBuilder(String.valueOf(valueOf).length() + 30).append("Failed to invoke ").append(valueOf).append(" with no args").toString(), e2.getTargetException());
            } catch (IllegalAccessException e3) {
                throw new AssertionError(e3);
            }
        }
    }

    /* renamed from: com.google.android.gms.internal.zzapb.7 */
    class C01497 implements zzapg<T> {
        final /* synthetic */ zzapb blA;

        C01497(zzapb com_google_android_gms_internal_zzapb) {
            this.blA = com_google_android_gms_internal_zzapb;
        }

        public T bg() {
            return new TreeSet();
        }
    }

    /* renamed from: com.google.android.gms.internal.zzapb.8 */
    class C01508 implements zzapg<T> {
        final /* synthetic */ zzapb blA;
        final /* synthetic */ Type blz;

        C01508(zzapb com_google_android_gms_internal_zzapb, Type type) {
            this.blA = com_google_android_gms_internal_zzapb;
            this.blz = type;
        }

        public T bg() {
            if (this.blz instanceof ParameterizedType) {
                Type type = ((ParameterizedType) this.blz).getActualTypeArguments()[0];
                if (type instanceof Class) {
                    return EnumSet.noneOf((Class) type);
                }
                String str = "Invalid EnumSet type: ";
                String valueOf = String.valueOf(this.blz.toString());
                throw new zzaoi(valueOf.length() != 0 ? str.concat(valueOf) : new String(str));
            }
            str = "Invalid EnumSet type: ";
            valueOf = String.valueOf(this.blz.toString());
            throw new zzaoi(valueOf.length() != 0 ? str.concat(valueOf) : new String(str));
        }
    }

    /* renamed from: com.google.android.gms.internal.zzapb.9 */
    class C01519 implements zzapg<T> {
        final /* synthetic */ zzapb blA;

        C01519(zzapb com_google_android_gms_internal_zzapb) {
            this.blA = com_google_android_gms_internal_zzapb;
        }

        public T bg() {
            return new LinkedHashSet();
        }
    }

    public zzapb(Map<Type, zzaod<?>> map) {
        this.bkY = map;
    }

    private <T> zzapg<T> zzc(Type type, Class<? super T> cls) {
        return Collection.class.isAssignableFrom(cls) ? SortedSet.class.isAssignableFrom(cls) ? new C01497(this) : EnumSet.class.isAssignableFrom(cls) ? new C01508(this, type) : Set.class.isAssignableFrom(cls) ? new C01519(this) : Queue.class.isAssignableFrom(cls) ? new zzapg<T>() {
            final /* synthetic */ zzapb blA;

            {
                this.blA = r1;
            }

            public T bg() {
                return new LinkedList();
            }
        } : new zzapg<T>() {
            final /* synthetic */ zzapb blA;

            {
                this.blA = r1;
            }

            public T bg() {
                return new ArrayList();
            }
        } : Map.class.isAssignableFrom(cls) ? SortedMap.class.isAssignableFrom(cls) ? new zzapg<T>() {
            final /* synthetic */ zzapb blA;

            {
                this.blA = r1;
            }

            public T bg() {
                return new TreeMap();
            }
        } : (!(type instanceof ParameterizedType) || String.class.isAssignableFrom(zzapx.zzl(((ParameterizedType) type).getActualTypeArguments()[0]).by())) ? new C01453(this) : new C01442(this) : null;
    }

    private <T> zzapg<T> zzd(Type type, Class<? super T> cls) {
        return new C01464(this, cls, type);
    }

    private <T> zzapg<T> zzl(Class<? super T> cls) {
        try {
            Constructor declaredConstructor = cls.getDeclaredConstructor(new Class[0]);
            if (!declaredConstructor.isAccessible()) {
                declaredConstructor.setAccessible(true);
            }
            return new C01486(this, declaredConstructor);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    public String toString() {
        return this.bkY.toString();
    }

    public <T> zzapg<T> zzb(zzapx<T> com_google_android_gms_internal_zzapx_T) {
        Type bz = com_google_android_gms_internal_zzapx_T.bz();
        Class by = com_google_android_gms_internal_zzapx_T.by();
        zzaod com_google_android_gms_internal_zzaod = (zzaod) this.bkY.get(bz);
        if (com_google_android_gms_internal_zzaod != null) {
            return new C01431(this, com_google_android_gms_internal_zzaod, bz);
        }
        com_google_android_gms_internal_zzaod = (zzaod) this.bkY.get(by);
        if (com_google_android_gms_internal_zzaod != null) {
            return new C01475(this, com_google_android_gms_internal_zzaod, bz);
        }
        zzapg<T> zzl = zzl(by);
        if (zzl != null) {
            return zzl;
        }
        zzl = zzc(bz, by);
        return zzl == null ? zzd(bz, by) : zzl;
    }
}
