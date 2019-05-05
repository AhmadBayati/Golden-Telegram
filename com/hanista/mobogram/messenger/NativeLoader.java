package com.hanista.mobogram.messenger;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import java.io.File;

public class NativeLoader {
    private static final String LIB_NAME = "tmessages.24";
    private static final String LIB_SO_NAME = "libtmessages.24.so";
    private static final int LIB_VERSION = 24;
    private static final String LOCALE_LIB_SO_NAME = "libtmessages.24loc.so";
    private static volatile boolean nativeLoaded;
    private String crashPath;

    static {
        nativeLoaded = false;
    }

    public NativeLoader() {
        this.crashPath = TtmlNode.ANONYMOUS_REGION_ID;
    }

    private static File getNativeLibraryDir(Context context) {
        File file;
        if (context != null) {
            try {
                file = new File((String) ApplicationInfo.class.getField("nativeLibraryDir").get(context.getApplicationInfo()));
            } catch (Throwable th) {
                th.printStackTrace();
            }
            if (file == null) {
                file = new File(context.getApplicationInfo().dataDir, "lib");
            }
            return file.isDirectory() ? file : null;
        }
        file = null;
        if (file == null) {
            file = new File(context.getApplicationInfo().dataDir, "lib");
        }
        if (file.isDirectory()) {
        }
    }

    private static native void init(String str, boolean z);

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static synchronized void initNativeLibs(android.content.Context r7) {
        /*
        r2 = com.hanista.mobogram.messenger.NativeLoader.class;
        monitor-enter(r2);
        r0 = nativeLoaded;	 Catch:{ all -> 0x00cb }
        if (r0 == 0) goto L_0x0009;
    L_0x0007:
        monitor-exit(r2);
        return;
    L_0x0009:
        r0 = android.os.Build.CPU_ABI;	 Catch:{ Exception -> 0x011f }
        r1 = "armeabi-v7a";
        r0 = r0.equalsIgnoreCase(r1);	 Catch:{ Exception -> 0x011f }
        if (r0 == 0) goto L_0x00ce;
    L_0x0014:
        r0 = "armeabi-v7a";
    L_0x0017:
        r1 = "os.arch";
        r1 = java.lang.System.getProperty(r1);	 Catch:{ Throwable -> 0x012b }
        if (r1 == 0) goto L_0x0130;
    L_0x0020:
        r3 = "686";
        r1 = r1.contains(r3);	 Catch:{ Throwable -> 0x012b }
        if (r1 == 0) goto L_0x0130;
    L_0x0029:
        r0 = "x86";
        r1 = r0;
    L_0x002d:
        r0 = getNativeLibraryDir(r7);	 Catch:{ Throwable -> 0x012b }
        if (r0 == 0) goto L_0x005b;
    L_0x0033:
        r3 = new java.io.File;	 Catch:{ Throwable -> 0x012b }
        r4 = "libtmessages.24.so";
        r3.<init>(r0, r4);	 Catch:{ Throwable -> 0x012b }
        r0 = r3.exists();	 Catch:{ Throwable -> 0x012b }
        if (r0 == 0) goto L_0x005b;
    L_0x0041:
        r0 = "tmessages";
        r3 = "load normal lib";
        com.hanista.mobogram.messenger.FileLog.m15d(r0, r3);	 Catch:{ Throwable -> 0x012b }
        r0 = "tmessages.24";
        java.lang.System.loadLibrary(r0);	 Catch:{ Error -> 0x0054 }
        r0 = 1;
        nativeLoaded = r0;	 Catch:{ Error -> 0x0054 }
        goto L_0x0007;
    L_0x0054:
        r0 = move-exception;
        r3 = "tmessages";
        com.hanista.mobogram.messenger.FileLog.m18e(r3, r0);	 Catch:{ Throwable -> 0x012b }
    L_0x005b:
        r3 = new java.io.File;	 Catch:{ Throwable -> 0x012b }
        r0 = r7.getFilesDir();	 Catch:{ Throwable -> 0x012b }
        r4 = "lib";
        r3.<init>(r0, r4);	 Catch:{ Throwable -> 0x012b }
        r3.mkdirs();	 Catch:{ Throwable -> 0x012b }
        r4 = new java.io.File;	 Catch:{ Throwable -> 0x012b }
        r0 = "libtmessages.24loc.so";
        r4.<init>(r3, r0);	 Catch:{ Throwable -> 0x012b }
        r0 = r4.exists();	 Catch:{ Throwable -> 0x012b }
        if (r0 == 0) goto L_0x0097;
    L_0x0078:
        r0 = "tmessages";
        r5 = "Load local lib";
        com.hanista.mobogram.messenger.FileLog.m15d(r0, r5);	 Catch:{ Error -> 0x008d }
        r0 = r4.getAbsolutePath();	 Catch:{ Error -> 0x008d }
        java.lang.System.load(r0);	 Catch:{ Error -> 0x008d }
        r0 = 1;
        nativeLoaded = r0;	 Catch:{ Error -> 0x008d }
        goto L_0x0007;
    L_0x008d:
        r0 = move-exception;
        r5 = "tmessages";
        com.hanista.mobogram.messenger.FileLog.m18e(r5, r0);	 Catch:{ Throwable -> 0x012b }
        r4.delete();	 Catch:{ Throwable -> 0x012b }
    L_0x0097:
        r0 = "tmessages";
        r5 = new java.lang.StringBuilder;	 Catch:{ Throwable -> 0x012b }
        r5.<init>();	 Catch:{ Throwable -> 0x012b }
        r6 = "Library not found, arch = ";
        r5 = r5.append(r6);	 Catch:{ Throwable -> 0x012b }
        r5 = r5.append(r1);	 Catch:{ Throwable -> 0x012b }
        r5 = r5.toString();	 Catch:{ Throwable -> 0x012b }
        com.hanista.mobogram.messenger.FileLog.m16e(r0, r5);	 Catch:{ Throwable -> 0x012b }
        r0 = loadFromZip(r7, r3, r4, r1);	 Catch:{ Throwable -> 0x012b }
        if (r0 != 0) goto L_0x0007;
    L_0x00b7:
        r0 = "tmessages.24";
        java.lang.System.loadLibrary(r0);	 Catch:{ Error -> 0x00c2 }
        r0 = 1;
        nativeLoaded = r0;	 Catch:{ Error -> 0x00c2 }
        goto L_0x0007;
    L_0x00c2:
        r0 = move-exception;
        r1 = "tmessages";
        com.hanista.mobogram.messenger.FileLog.m18e(r1, r0);	 Catch:{ all -> 0x00cb }
        goto L_0x0007;
    L_0x00cb:
        r0 = move-exception;
        monitor-exit(r2);
        throw r0;
    L_0x00ce:
        r0 = android.os.Build.CPU_ABI;	 Catch:{ Exception -> 0x011f }
        r1 = "armeabi";
        r0 = r0.equalsIgnoreCase(r1);	 Catch:{ Exception -> 0x011f }
        if (r0 == 0) goto L_0x00de;
    L_0x00d9:
        r0 = "armeabi";
        goto L_0x0017;
    L_0x00de:
        r0 = android.os.Build.CPU_ABI;	 Catch:{ Exception -> 0x011f }
        r1 = "x86";
        r0 = r0.equalsIgnoreCase(r1);	 Catch:{ Exception -> 0x011f }
        if (r0 == 0) goto L_0x00ee;
    L_0x00e9:
        r0 = "x86";
        goto L_0x0017;
    L_0x00ee:
        r0 = android.os.Build.CPU_ABI;	 Catch:{ Exception -> 0x011f }
        r1 = "mips";
        r0 = r0.equalsIgnoreCase(r1);	 Catch:{ Exception -> 0x011f }
        if (r0 == 0) goto L_0x00fe;
    L_0x00f9:
        r0 = "mips";
        goto L_0x0017;
    L_0x00fe:
        r0 = "armeabi";
        r1 = "tmessages";
        r3 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x011f }
        r3.<init>();	 Catch:{ Exception -> 0x011f }
        r4 = "Unsupported arch: ";
        r3 = r3.append(r4);	 Catch:{ Exception -> 0x011f }
        r4 = android.os.Build.CPU_ABI;	 Catch:{ Exception -> 0x011f }
        r3 = r3.append(r4);	 Catch:{ Exception -> 0x011f }
        r3 = r3.toString();	 Catch:{ Exception -> 0x011f }
        com.hanista.mobogram.messenger.FileLog.m16e(r1, r3);	 Catch:{ Exception -> 0x011f }
        goto L_0x0017;
    L_0x011f:
        r0 = move-exception;
        r1 = "tmessages";
        com.hanista.mobogram.messenger.FileLog.m18e(r1, r0);	 Catch:{ Throwable -> 0x012b }
        r0 = "armeabi";
        goto L_0x0017;
    L_0x012b:
        r0 = move-exception;
        r0.printStackTrace();	 Catch:{ all -> 0x00cb }
        goto L_0x00b7;
    L_0x0130:
        r1 = r0;
        goto L_0x002d;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.hanista.mobogram.messenger.NativeLoader.initNativeLibs(android.content.Context):void");
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static boolean loadFromZip(android.content.Context r8, java.io.File r9, java.io.File r10, java.lang.String r11) {
        /*
        r3 = 0;
        r0 = 1;
        r1 = 0;
        r4 = r9.listFiles();	 Catch:{ Exception -> 0x0013 }
        r5 = r4.length;	 Catch:{ Exception -> 0x0013 }
        r2 = r1;
    L_0x0009:
        if (r2 >= r5) goto L_0x001a;
    L_0x000b:
        r6 = r4[r2];	 Catch:{ Exception -> 0x0013 }
        r6.delete();	 Catch:{ Exception -> 0x0013 }
        r2 = r2 + 1;
        goto L_0x0009;
    L_0x0013:
        r2 = move-exception;
        r4 = "tmessages";
        com.hanista.mobogram.messenger.FileLog.m18e(r4, r2);
    L_0x001a:
        r4 = new java.util.zip.ZipFile;	 Catch:{ Exception -> 0x011a, all -> 0x0113 }
        r2 = r8.getApplicationInfo();	 Catch:{ Exception -> 0x011a, all -> 0x0113 }
        r2 = r2.sourceDir;	 Catch:{ Exception -> 0x011a, all -> 0x0113 }
        r4.<init>(r2);	 Catch:{ Exception -> 0x011a, all -> 0x0113 }
        r2 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0075, all -> 0x00df }
        r2.<init>();	 Catch:{ Exception -> 0x0075, all -> 0x00df }
        r5 = "lib/";
        r2 = r2.append(r5);	 Catch:{ Exception -> 0x0075, all -> 0x00df }
        r2 = r2.append(r11);	 Catch:{ Exception -> 0x0075, all -> 0x00df }
        r5 = "/";
        r2 = r2.append(r5);	 Catch:{ Exception -> 0x0075, all -> 0x00df }
        r5 = "libtmessages.24.so";
        r2 = r2.append(r5);	 Catch:{ Exception -> 0x0075, all -> 0x00df }
        r2 = r2.toString();	 Catch:{ Exception -> 0x0075, all -> 0x00df }
        r2 = r4.getEntry(r2);	 Catch:{ Exception -> 0x0075, all -> 0x00df }
        if (r2 != 0) goto L_0x008a;
    L_0x004d:
        r0 = new java.lang.Exception;	 Catch:{ Exception -> 0x0075, all -> 0x00df }
        r2 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0075, all -> 0x00df }
        r2.<init>();	 Catch:{ Exception -> 0x0075, all -> 0x00df }
        r5 = "Unable to find file in apk:lib/";
        r2 = r2.append(r5);	 Catch:{ Exception -> 0x0075, all -> 0x00df }
        r2 = r2.append(r11);	 Catch:{ Exception -> 0x0075, all -> 0x00df }
        r5 = "/";
        r2 = r2.append(r5);	 Catch:{ Exception -> 0x0075, all -> 0x00df }
        r5 = "tmessages.24";
        r2 = r2.append(r5);	 Catch:{ Exception -> 0x0075, all -> 0x00df }
        r2 = r2.toString();	 Catch:{ Exception -> 0x0075, all -> 0x00df }
        r0.<init>(r2);	 Catch:{ Exception -> 0x0075, all -> 0x00df }
        throw r0;	 Catch:{ Exception -> 0x0075, all -> 0x00df }
    L_0x0075:
        r0 = move-exception;
        r2 = r3;
        r3 = r4;
    L_0x0078:
        r4 = "tmessages";
        com.hanista.mobogram.messenger.FileLog.m18e(r4, r0);	 Catch:{ all -> 0x0116 }
        if (r2 == 0) goto L_0x0083;
    L_0x0080:
        r2.close();	 Catch:{ Exception -> 0x00f3 }
    L_0x0083:
        if (r3 == 0) goto L_0x0088;
    L_0x0085:
        r3.close();	 Catch:{ Exception -> 0x00fb }
    L_0x0088:
        r0 = r1;
    L_0x0089:
        return r0;
    L_0x008a:
        r3 = r4.getInputStream(r2);	 Catch:{ Exception -> 0x0075, all -> 0x00df }
        r2 = new java.io.FileOutputStream;	 Catch:{ Exception -> 0x00a5, all -> 0x00df }
        r2.<init>(r10);	 Catch:{ Exception -> 0x00a5, all -> 0x00df }
        r5 = 4096; // 0x1000 float:5.74E-42 double:2.0237E-320;
        r5 = new byte[r5];	 Catch:{ Exception -> 0x00a5, all -> 0x00df }
    L_0x0097:
        r6 = r3.read(r5);	 Catch:{ Exception -> 0x00a5, all -> 0x00df }
        if (r6 <= 0) goto L_0x00a9;
    L_0x009d:
        java.lang.Thread.yield();	 Catch:{ Exception -> 0x00a5, all -> 0x00df }
        r7 = 0;
        r2.write(r5, r7, r6);	 Catch:{ Exception -> 0x00a5, all -> 0x00df }
        goto L_0x0097;
    L_0x00a5:
        r0 = move-exception;
        r2 = r3;
        r3 = r4;
        goto L_0x0078;
    L_0x00a9:
        r2.close();	 Catch:{ Exception -> 0x00a5, all -> 0x00df }
        r2 = 1;
        r5 = 0;
        r10.setReadable(r2, r5);	 Catch:{ Exception -> 0x00a5, all -> 0x00df }
        r2 = 1;
        r5 = 0;
        r10.setExecutable(r2, r5);	 Catch:{ Exception -> 0x00a5, all -> 0x00df }
        r2 = 1;
        r10.setWritable(r2);	 Catch:{ Exception -> 0x00a5, all -> 0x00df }
        r2 = r10.getAbsolutePath();	 Catch:{ Error -> 0x00d7 }
        java.lang.System.load(r2);	 Catch:{ Error -> 0x00d7 }
        r2 = 1;
        nativeLoaded = r2;	 Catch:{ Error -> 0x00d7 }
    L_0x00c4:
        if (r3 == 0) goto L_0x00c9;
    L_0x00c6:
        r3.close();	 Catch:{ Exception -> 0x00eb }
    L_0x00c9:
        if (r4 == 0) goto L_0x0089;
    L_0x00cb:
        r4.close();	 Catch:{ Exception -> 0x00cf }
        goto L_0x0089;
    L_0x00cf:
        r1 = move-exception;
        r2 = "tmessages";
        com.hanista.mobogram.messenger.FileLog.m18e(r2, r1);
        goto L_0x0089;
    L_0x00d7:
        r2 = move-exception;
        r5 = "tmessages";
        com.hanista.mobogram.messenger.FileLog.m18e(r5, r2);	 Catch:{ Exception -> 0x00a5, all -> 0x00df }
        goto L_0x00c4;
    L_0x00df:
        r0 = move-exception;
    L_0x00e0:
        if (r3 == 0) goto L_0x00e5;
    L_0x00e2:
        r3.close();	 Catch:{ Exception -> 0x0103 }
    L_0x00e5:
        if (r4 == 0) goto L_0x00ea;
    L_0x00e7:
        r4.close();	 Catch:{ Exception -> 0x010b }
    L_0x00ea:
        throw r0;
    L_0x00eb:
        r1 = move-exception;
        r2 = "tmessages";
        com.hanista.mobogram.messenger.FileLog.m18e(r2, r1);
        goto L_0x00c9;
    L_0x00f3:
        r0 = move-exception;
        r2 = "tmessages";
        com.hanista.mobogram.messenger.FileLog.m18e(r2, r0);
        goto L_0x0083;
    L_0x00fb:
        r0 = move-exception;
        r2 = "tmessages";
        com.hanista.mobogram.messenger.FileLog.m18e(r2, r0);
        goto L_0x0088;
    L_0x0103:
        r1 = move-exception;
        r2 = "tmessages";
        com.hanista.mobogram.messenger.FileLog.m18e(r2, r1);
        goto L_0x00e5;
    L_0x010b:
        r1 = move-exception;
        r2 = "tmessages";
        com.hanista.mobogram.messenger.FileLog.m18e(r2, r1);
        goto L_0x00ea;
    L_0x0113:
        r0 = move-exception;
        r4 = r3;
        goto L_0x00e0;
    L_0x0116:
        r0 = move-exception;
        r4 = r3;
        r3 = r2;
        goto L_0x00e0;
    L_0x011a:
        r0 = move-exception;
        r2 = r3;
        goto L_0x0078;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.hanista.mobogram.messenger.NativeLoader.loadFromZip(android.content.Context, java.io.File, java.io.File, java.lang.String):boolean");
    }
}
