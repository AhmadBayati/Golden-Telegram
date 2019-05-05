package com.google.android.gms.internal;

import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public abstract class zzapj {

    /* renamed from: com.google.android.gms.internal.zzapj.1 */
    static class C01571 extends zzapj {
        final /* synthetic */ Method bml;
        final /* synthetic */ Object bmm;

        C01571(Method method, Object obj) {
            this.bml = method;
            this.bmm = obj;
        }

        public <T> T zzf(Class<T> cls) {
            return this.bml.invoke(this.bmm, new Object[]{cls});
        }
    }

    /* renamed from: com.google.android.gms.internal.zzapj.2 */
    static class C01582 extends zzapj {
        final /* synthetic */ Method bmn;
        final /* synthetic */ int bmo;

        C01582(Method method, int i) {
            this.bmn = method;
            this.bmo = i;
        }

        public <T> T zzf(Class<T> cls) {
            return this.bmn.invoke(null, new Object[]{cls, Integer.valueOf(this.bmo)});
        }
    }

    /* renamed from: com.google.android.gms.internal.zzapj.3 */
    static class C01593 extends zzapj {
        final /* synthetic */ Method bmn;

        C01593(Method method) {
            this.bmn = method;
        }

        public <T> T zzf(Class<T> cls) {
            return this.bmn.invoke(null, new Object[]{cls, Object.class});
        }
    }

    /* renamed from: com.google.android.gms.internal.zzapj.4 */
    static class C01604 extends zzapj {
        C01604() {
        }

        public <T> T zzf(Class<T> cls) {
            String valueOf = String.valueOf(cls);
            throw new UnsupportedOperationException(new StringBuilder(String.valueOf(valueOf).length() + 16).append("Cannot allocate ").append(valueOf).toString());
        }
    }

    public static zzapj bl() {
        try {
            Class cls = Class.forName("sun.misc.Unsafe");
            Field declaredField = cls.getDeclaredField("theUnsafe");
            declaredField.setAccessible(true);
            return new C01571(cls.getMethod("allocateInstance", new Class[]{Class.class}), declaredField.get(null));
        } catch (Exception e) {
            try {
                Method declaredMethod = ObjectStreamClass.class.getDeclaredMethod("getConstructorId", new Class[]{Class.class});
                declaredMethod.setAccessible(true);
                int intValue = ((Integer) declaredMethod.invoke(null, new Object[]{Object.class})).intValue();
                Method declaredMethod2 = ObjectStreamClass.class.getDeclaredMethod("newInstance", new Class[]{Class.class, Integer.TYPE});
                declaredMethod2.setAccessible(true);
                return new C01582(declaredMethod2, intValue);
            } catch (Exception e2) {
                try {
                    Method declaredMethod3 = ObjectInputStream.class.getDeclaredMethod("newInstance", new Class[]{Class.class, Class.class});
                    declaredMethod3.setAccessible(true);
                    return new C01593(declaredMethod3);
                } catch (Exception e3) {
                    return new C01604();
                }
            }
        }
    }

    public abstract <T> T zzf(Class<T> cls);
}
