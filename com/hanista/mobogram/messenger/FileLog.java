package com.hanista.mobogram.messenger;

import android.util.Log;
import com.hanista.mobogram.messenger.time.FastDateFormat;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Locale;

public class FileLog {
    private static volatile FileLog Instance;
    private File currentFile;
    private FastDateFormat dateFormat;
    private DispatchQueue logQueue;
    private File networkFile;
    private OutputStreamWriter streamWriter;

    /* renamed from: com.hanista.mobogram.messenger.FileLog.1 */
    static class C04041 implements Runnable {
        final /* synthetic */ Throwable val$exception;
        final /* synthetic */ String val$message;
        final /* synthetic */ String val$tag;

        C04041(String str, String str2, Throwable th) {
            this.val$tag = str;
            this.val$message = str2;
            this.val$exception = th;
        }

        public void run() {
            try {
                FileLog.getInstance().streamWriter.write(FileLog.getInstance().dateFormat.format(System.currentTimeMillis()) + " E/" + this.val$tag + "\ufe55 " + this.val$message + "\n");
                FileLog.getInstance().streamWriter.write(this.val$exception.toString());
                FileLog.getInstance().streamWriter.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.FileLog.2 */
    static class C04052 implements Runnable {
        final /* synthetic */ String val$message;
        final /* synthetic */ String val$tag;

        C04052(String str, String str2) {
            this.val$tag = str;
            this.val$message = str2;
        }

        public void run() {
            try {
                FileLog.getInstance().streamWriter.write(FileLog.getInstance().dateFormat.format(System.currentTimeMillis()) + " E/" + this.val$tag + "\ufe55 " + this.val$message + "\n");
                FileLog.getInstance().streamWriter.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.FileLog.3 */
    static class C04063 implements Runnable {
        final /* synthetic */ Throwable val$e;
        final /* synthetic */ String val$tag;

        C04063(String str, Throwable th) {
            this.val$tag = str;
            this.val$e = th;
        }

        public void run() {
            try {
                FileLog.getInstance().streamWriter.write(FileLog.getInstance().dateFormat.format(System.currentTimeMillis()) + " E/" + this.val$tag + "\ufe55 " + this.val$e + "\n");
                StackTraceElement[] stackTrace = this.val$e.getStackTrace();
                for (Object obj : stackTrace) {
                    FileLog.getInstance().streamWriter.write(FileLog.getInstance().dateFormat.format(System.currentTimeMillis()) + " E/" + this.val$tag + "\ufe55 " + obj + "\n");
                }
                FileLog.getInstance().streamWriter.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.FileLog.4 */
    static class C04074 implements Runnable {
        final /* synthetic */ String val$message;
        final /* synthetic */ String val$tag;

        C04074(String str, String str2) {
            this.val$tag = str;
            this.val$message = str2;
        }

        public void run() {
            try {
                FileLog.getInstance().streamWriter.write(FileLog.getInstance().dateFormat.format(System.currentTimeMillis()) + " D/" + this.val$tag + "\ufe55 " + this.val$message + "\n");
                FileLog.getInstance().streamWriter.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.FileLog.5 */
    static class C04085 implements Runnable {
        final /* synthetic */ String val$message;
        final /* synthetic */ String val$tag;

        C04085(String str, String str2) {
            this.val$tag = str;
            this.val$message = str2;
        }

        public void run() {
            try {
                FileLog.getInstance().streamWriter.write(FileLog.getInstance().dateFormat.format(System.currentTimeMillis()) + " W/" + this.val$tag + ": " + this.val$message + "\n");
                FileLog.getInstance().streamWriter.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    static {
        Instance = null;
    }

    public FileLog() {
        this.streamWriter = null;
        this.dateFormat = null;
        this.logQueue = null;
        this.currentFile = null;
        this.networkFile = null;
        if (BuildVars.DEBUG_VERSION) {
            this.dateFormat = FastDateFormat.getInstance("dd_MM_yyyy_HH_mm_ss", Locale.US);
            try {
                File externalFilesDir = ApplicationLoader.applicationContext.getExternalFilesDir(null);
                if (externalFilesDir != null) {
                    File file = new File(externalFilesDir.getAbsolutePath() + "/logs");
                    file.mkdirs();
                    this.currentFile = new File(file, this.dateFormat.format(System.currentTimeMillis()) + ".txt");
                    try {
                        this.logQueue = new DispatchQueue("logQueue");
                        this.currentFile.createNewFile();
                        this.streamWriter = new OutputStreamWriter(new FileOutputStream(this.currentFile));
                        this.streamWriter.write("-----start log " + this.dateFormat.format(System.currentTimeMillis()) + "-----\n");
                        this.streamWriter.flush();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }

    public static void cleanupLogs() {
        File externalFilesDir = ApplicationLoader.applicationContext.getExternalFilesDir(null);
        if (externalFilesDir != null) {
            File[] listFiles = new File(externalFilesDir.getAbsolutePath() + "/logs").listFiles();
            if (listFiles != null) {
                for (File file : listFiles) {
                    if ((getInstance().currentFile == null || !file.getAbsolutePath().equals(getInstance().currentFile.getAbsolutePath())) && (getInstance().networkFile == null || !file.getAbsolutePath().equals(getInstance().networkFile.getAbsolutePath()))) {
                        file.delete();
                    }
                }
            }
        }
    }

    public static void m15d(String str, String str2) {
        if (BuildVars.DEBUG_VERSION) {
            Log.d(str, str2);
            if (getInstance().streamWriter != null) {
                getInstance().logQueue.postRunnable(new C04074(str, str2));
            }
        }
    }

    public static void m16e(String str, String str2) {
        if (BuildVars.DEBUG_VERSION) {
            Log.e(str, str2);
            if (getInstance().streamWriter != null) {
                getInstance().logQueue.postRunnable(new C04052(str, str2));
            }
        }
    }

    public static void m17e(String str, String str2, Throwable th) {
        if (BuildVars.DEBUG_VERSION) {
            Log.e(str, str2, th);
            if (getInstance().streamWriter != null) {
                getInstance().logQueue.postRunnable(new C04041(str, str2, th));
            }
        }
    }

    public static void m18e(String str, Throwable th) {
        if (BuildVars.DEBUG_VERSION) {
            th.printStackTrace();
            if (getInstance().streamWriter != null) {
                getInstance().logQueue.postRunnable(new C04063(str, th));
            } else {
                th.printStackTrace();
            }
        }
    }

    public static FileLog getInstance() {
        FileLog fileLog = Instance;
        if (fileLog == null) {
            synchronized (FileLog.class) {
                fileLog = Instance;
                if (fileLog == null) {
                    fileLog = new FileLog();
                    Instance = fileLog;
                }
            }
        }
        return fileLog;
    }

    public static String getNetworkLogPath() {
        if (!BuildVars.DEBUG_VERSION) {
            return TtmlNode.ANONYMOUS_REGION_ID;
        }
        try {
            File externalFilesDir = ApplicationLoader.applicationContext.getExternalFilesDir(null);
            if (externalFilesDir == null) {
                return TtmlNode.ANONYMOUS_REGION_ID;
            }
            File file = new File(externalFilesDir.getAbsolutePath() + "/logs");
            file.mkdirs();
            getInstance().networkFile = new File(file, getInstance().dateFormat.format(System.currentTimeMillis()) + "_net.txt");
            return getInstance().networkFile.getAbsolutePath();
        } catch (Throwable th) {
            th.printStackTrace();
            return TtmlNode.ANONYMOUS_REGION_ID;
        }
    }

    public static void m19w(String str, String str2) {
        if (BuildVars.DEBUG_VERSION) {
            Log.w(str, str2);
            if (getInstance().streamWriter != null) {
                getInstance().logQueue.postRunnable(new C04085(str, str2));
            }
        }
    }
}
