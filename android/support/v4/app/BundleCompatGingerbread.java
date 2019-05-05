package android.support.v4.app;

import java.lang.reflect.Method;

class BundleCompatGingerbread {
    private static final String TAG = "BundleCompatGingerbread";
    private static Method sGetIBinderMethod;
    private static boolean sGetIBinderMethodFetched;
    private static Method sPutIBinderMethod;
    private static boolean sPutIBinderMethodFetched;

    BundleCompatGingerbread() {
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static android.os.IBinder getBinder(android.os.Bundle r7, java.lang.String r8) {
        /*
        r1 = 0;
        r6 = 1;
        r0 = sGetIBinderMethodFetched;
        if (r0 != 0) goto L_0x0021;
    L_0x0006:
        r0 = android.os.Bundle.class;
        r2 = "getIBinder";
        r3 = 1;
        r3 = new java.lang.Class[r3];	 Catch:{ NoSuchMethodException -> 0x0034 }
        r4 = 0;
        r5 = java.lang.String.class;
        r3[r4] = r5;	 Catch:{ NoSuchMethodException -> 0x0034 }
        r0 = r0.getMethod(r2, r3);	 Catch:{ NoSuchMethodException -> 0x0034 }
        sGetIBinderMethod = r0;	 Catch:{ NoSuchMethodException -> 0x0034 }
        r0 = sGetIBinderMethod;	 Catch:{ NoSuchMethodException -> 0x0034 }
        r2 = 1;
        r0.setAccessible(r2);	 Catch:{ NoSuchMethodException -> 0x0034 }
    L_0x001f:
        sGetIBinderMethodFetched = r6;
    L_0x0021:
        r0 = sGetIBinderMethod;
        if (r0 == 0) goto L_0x004b;
    L_0x0025:
        r0 = sGetIBinderMethod;	 Catch:{ InvocationTargetException -> 0x004f, IllegalAccessException -> 0x003f, IllegalArgumentException -> 0x004d }
        r2 = 1;
        r2 = new java.lang.Object[r2];	 Catch:{ InvocationTargetException -> 0x004f, IllegalAccessException -> 0x003f, IllegalArgumentException -> 0x004d }
        r3 = 0;
        r2[r3] = r8;	 Catch:{ InvocationTargetException -> 0x004f, IllegalAccessException -> 0x003f, IllegalArgumentException -> 0x004d }
        r0 = r0.invoke(r7, r2);	 Catch:{ InvocationTargetException -> 0x004f, IllegalAccessException -> 0x003f, IllegalArgumentException -> 0x004d }
        r0 = (android.os.IBinder) r0;	 Catch:{ InvocationTargetException -> 0x004f, IllegalAccessException -> 0x003f, IllegalArgumentException -> 0x004d }
    L_0x0033:
        return r0;
    L_0x0034:
        r0 = move-exception;
        r2 = "BundleCompatGingerbread";
        r3 = "Failed to retrieve getIBinder method";
        android.util.Log.i(r2, r3, r0);
        goto L_0x001f;
    L_0x003f:
        r0 = move-exception;
    L_0x0040:
        r2 = "BundleCompatGingerbread";
        r3 = "Failed to invoke getIBinder via reflection";
        android.util.Log.i(r2, r3, r0);
        sGetIBinderMethod = r1;
    L_0x004b:
        r0 = r1;
        goto L_0x0033;
    L_0x004d:
        r0 = move-exception;
        goto L_0x0040;
    L_0x004f:
        r0 = move-exception;
        goto L_0x0040;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v4.app.BundleCompatGingerbread.getBinder(android.os.Bundle, java.lang.String):android.os.IBinder");
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void putBinder(android.os.Bundle r6, java.lang.String r7, android.os.IBinder r8) {
        /*
        r5 = 1;
        r0 = sPutIBinderMethodFetched;
        if (r0 != 0) goto L_0x0025;
    L_0x0005:
        r0 = android.os.Bundle.class;
        r1 = "putIBinder";
        r2 = 2;
        r2 = new java.lang.Class[r2];	 Catch:{ NoSuchMethodException -> 0x0038 }
        r3 = 0;
        r4 = java.lang.String.class;
        r2[r3] = r4;	 Catch:{ NoSuchMethodException -> 0x0038 }
        r3 = 1;
        r4 = android.os.IBinder.class;
        r2[r3] = r4;	 Catch:{ NoSuchMethodException -> 0x0038 }
        r0 = r0.getMethod(r1, r2);	 Catch:{ NoSuchMethodException -> 0x0038 }
        sPutIBinderMethod = r0;	 Catch:{ NoSuchMethodException -> 0x0038 }
        r0 = sPutIBinderMethod;	 Catch:{ NoSuchMethodException -> 0x0038 }
        r1 = 1;
        r0.setAccessible(r1);	 Catch:{ NoSuchMethodException -> 0x0038 }
    L_0x0023:
        sPutIBinderMethodFetched = r5;
    L_0x0025:
        r0 = sPutIBinderMethod;
        if (r0 == 0) goto L_0x0037;
    L_0x0029:
        r0 = sPutIBinderMethod;	 Catch:{ InvocationTargetException -> 0x0053, IllegalAccessException -> 0x0043, IllegalArgumentException -> 0x0051 }
        r1 = 2;
        r1 = new java.lang.Object[r1];	 Catch:{ InvocationTargetException -> 0x0053, IllegalAccessException -> 0x0043, IllegalArgumentException -> 0x0051 }
        r2 = 0;
        r1[r2] = r7;	 Catch:{ InvocationTargetException -> 0x0053, IllegalAccessException -> 0x0043, IllegalArgumentException -> 0x0051 }
        r2 = 1;
        r1[r2] = r8;	 Catch:{ InvocationTargetException -> 0x0053, IllegalAccessException -> 0x0043, IllegalArgumentException -> 0x0051 }
        r0.invoke(r6, r1);	 Catch:{ InvocationTargetException -> 0x0053, IllegalAccessException -> 0x0043, IllegalArgumentException -> 0x0051 }
    L_0x0037:
        return;
    L_0x0038:
        r0 = move-exception;
        r1 = "BundleCompatGingerbread";
        r2 = "Failed to retrieve putIBinder method";
        android.util.Log.i(r1, r2, r0);
        goto L_0x0023;
    L_0x0043:
        r0 = move-exception;
    L_0x0044:
        r1 = "BundleCompatGingerbread";
        r2 = "Failed to invoke putIBinder via reflection";
        android.util.Log.i(r1, r2, r0);
        r0 = 0;
        sPutIBinderMethod = r0;
        goto L_0x0037;
    L_0x0051:
        r0 = move-exception;
        goto L_0x0044;
    L_0x0053:
        r0 = move-exception;
        goto L_0x0044;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v4.app.BundleCompatGingerbread.putBinder(android.os.Bundle, java.lang.String, android.os.IBinder):void");
    }
}
