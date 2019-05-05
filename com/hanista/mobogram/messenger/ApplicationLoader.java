package com.hanista.mobogram.messenger;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Handler;
import android.os.PowerManager;
import android.util.Base64;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.hanista.mobogram.mobo.MoboConstants;
import com.hanista.mobogram.mobo.p000a.ArchiveUtil;
import com.hanista.mobogram.mobo.p004e.MobogramDBHelper;
import com.hanista.mobogram.mobo.p012l.HiddenConfig;
import com.hanista.mobogram.mobo.p016p.SpecificContactBiz;
import com.hanista.mobogram.mobo.p020s.AdvanceTheme;
import com.hanista.mobogram.mobo.p020s.ThemeUtil;
import com.hanista.mobogram.mobo.voicechanger.Utils;
import com.hanista.mobogram.tgnet.ConnectionsManager;
import com.hanista.mobogram.tgnet.SerializedData;
import com.hanista.mobogram.tgnet.TLRPC;
import com.hanista.mobogram.ui.Components.ForegroundDetector;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.lang.Thread.UncaughtExceptionHandler;

public class ApplicationLoader extends Application {
    public static volatile Context applicationContext;
    public static volatile Handler applicationHandler;
    private static volatile boolean applicationInited;
    private static Drawable cachedWallpaper;
    private static boolean isCustomTheme;
    public static volatile boolean isScreenOn;
    public static volatile boolean mainInterfacePaused;
    private static MobogramDBHelper openHelper;
    private static int selectedColor;
    private static int serviceMessageColor;
    private static int serviceSelectedMessageColor;
    private static final Object sync;

    /* renamed from: com.hanista.mobogram.messenger.ApplicationLoader.1 */
    static class C03451 implements Runnable {

        /* renamed from: com.hanista.mobogram.messenger.ApplicationLoader.1.1 */
        class C03441 implements Runnable {
            C03441() {
            }

            public void run() {
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.didSetNewWallpapper, new Object[0]);
            }
        }

        C03451() {
        }

        public void run() {
            int i = 0;
            synchronized (ApplicationLoader.sync) {
                try {
                    SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
                    int i2 = sharedPreferences.getInt("selectedBackground", 1000001);
                    i = sharedPreferences.getInt("selectedColor", 0);
                    ApplicationLoader.serviceMessageColor = sharedPreferences.getInt("serviceMessageColor", 0);
                    ApplicationLoader.serviceSelectedMessageColor = sharedPreferences.getInt("serviceSelectedMessageColor", 0);
                    if (i == 0) {
                        if (i2 == 1000001) {
                            ApplicationLoader.cachedWallpaper = ApplicationLoader.applicationContext.getResources().getDrawable(ThemeUtil.m2485a().m2295i());
                            ApplicationLoader.isCustomTheme = false;
                        } else {
                            File file = new File(ApplicationLoader.getFilesDirFixed(), "wallpaper.jpg");
                            if (file.exists()) {
                                ApplicationLoader.cachedWallpaper = Drawable.createFromPath(file.getAbsolutePath());
                                ApplicationLoader.isCustomTheme = true;
                            } else {
                                ApplicationLoader.cachedWallpaper = ApplicationLoader.applicationContext.getResources().getDrawable(ThemeUtil.m2485a().m2295i());
                                ApplicationLoader.isCustomTheme = false;
                            }
                        }
                    }
                } catch (Throwable th) {
                }
                if (ApplicationLoader.cachedWallpaper == null) {
                    if (i == 0) {
                        i = -2693905;
                    }
                    ApplicationLoader.cachedWallpaper = new ColorDrawable(i);
                }
                if (ApplicationLoader.serviceMessageColor == 0) {
                    ApplicationLoader.calcBackgroundColor();
                }
                AndroidUtilities.runOnUIThread(new C03441());
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.ApplicationLoader.2 */
    class C03462 implements Runnable {
        C03462() {
        }

        public void run() {
            if (ApplicationLoader.this.checkPlayServices()) {
                if (UserConfig.pushString == null || UserConfig.pushString.length() == 0) {
                    FileLog.m15d("tmessages", "GCM Registration not found.");
                } else {
                    FileLog.m15d("tmessages", "GCM regId = " + UserConfig.pushString);
                }
                ApplicationLoader.this.startService(new Intent(ApplicationLoader.applicationContext, GcmRegistrationIntentService.class));
                return;
            }
            FileLog.m15d("tmessages", "No valid Google Play Services APK found.");
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.ApplicationLoader.3 */
    class C03473 implements UncaughtExceptionHandler {
        final /* synthetic */ UncaughtExceptionHandler val$defaultUncaughtExceptionHandler;

        C03473(UncaughtExceptionHandler uncaughtExceptionHandler) {
            this.val$defaultUncaughtExceptionHandler = uncaughtExceptionHandler;
        }

        public void uncaughtException(Thread thread, Throwable th) {
            try {
                PrintWriter printWriter = new PrintWriter(new File(AndroidUtilities.getCacheDir().getParentFile(), "error_log.txt"));
                th.printStackTrace(printWriter);
                printWriter.flush();
                printWriter.close();
                if (this.val$defaultUncaughtExceptionHandler != null) {
                    this.val$defaultUncaughtExceptionHandler.uncaughtException(thread, th);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    static {
        sync = new Object();
        applicationInited = false;
        isScreenOn = false;
        mainInterfacePaused = true;
    }

    private static void calcBackgroundColor() {
        int[] calcDrawableColor = AndroidUtilities.calcDrawableColor(cachedWallpaper);
        serviceMessageColor = calcDrawableColor[0];
        serviceSelectedMessageColor = calcDrawableColor[1];
        applicationContext.getSharedPreferences("mainconfig", 0).edit().putInt("serviceMessageColor", serviceMessageColor).putInt("serviceSelectedMessageColor", serviceSelectedMessageColor).commit();
    }

    private void catchErrors() {
        Thread.setDefaultUncaughtExceptionHandler(new C03473(Thread.getDefaultUncaughtExceptionHandler()));
    }

    private boolean checkPlayServices() {
        try {
            return GooglePlayServicesUtil.isGooglePlayServicesAvailable(this) == 0;
        } catch (Throwable e) {
            FileLog.m18e("tmessages", e);
            return true;
        }
    }

    private MobogramDBHelper constructSQLiteOpenHelper() {
        return new MobogramDBHelper(getApplicationContext());
    }

    private static void convertConfig() {
        SharedPreferences sharedPreferences = applicationContext.getSharedPreferences("dataconfig", 0);
        if (sharedPreferences.contains("currentDatacenterId")) {
            byte[] decode;
            SerializedData serializedData = new SerializedData((int) TLRPC.MESSAGE_FLAG_EDITED);
            serializedData.writeInt32(2);
            serializedData.writeBool(sharedPreferences.getInt("datacenterSetId", 0) != 0);
            serializedData.writeBool(true);
            serializedData.writeInt32(sharedPreferences.getInt("currentDatacenterId", 0));
            serializedData.writeInt32(sharedPreferences.getInt("timeDifference", 0));
            serializedData.writeInt32(sharedPreferences.getInt("lastDcUpdateTime", 0));
            serializedData.writeInt64(sharedPreferences.getLong("pushSessionId", 0));
            serializedData.writeBool(false);
            serializedData.writeInt32(0);
            try {
                String string = sharedPreferences.getString("datacenters", null);
                if (string != null) {
                    decode = Base64.decode(string, 0);
                    if (decode != null) {
                        SerializedData serializedData2 = new SerializedData(decode);
                        serializedData.writeInt32(serializedData2.readInt32(false));
                        serializedData.writeBytes(decode, 4, decode.length - 4);
                        serializedData2.cleanup();
                    }
                }
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
            try {
                RandomAccessFile randomAccessFile = new RandomAccessFile(new File(getFilesDirFixed(), "tgnet.dat"), "rws");
                decode = serializedData.toByteArray();
                randomAccessFile.writeInt(Integer.reverseBytes(decode.length));
                randomAccessFile.write(decode);
                randomAccessFile.close();
            } catch (Throwable e2) {
                FileLog.m18e("tmessages", e2);
            }
            serializedData.cleanup();
            sharedPreferences.edit().clear().commit();
        }
    }

    public static Drawable getCachedWallpaper() {
        Drawable drawable;
        synchronized (sync) {
            drawable = cachedWallpaper;
        }
        return drawable;
    }

    public static File getFilesDirFixed() {
        for (int i = 0; i < 10; i++) {
            File filesDir = applicationContext.getFilesDir();
            if (filesDir != null) {
                return filesDir;
            }
        }
        try {
            filesDir = new File(applicationContext.getApplicationInfo().dataDir, "files");
            filesDir.mkdirs();
            return filesDir;
        } catch (Throwable e) {
            FileLog.m18e("tmessages", e);
            return new File("/data/data/com.hanista.mobogram/files");
        }
    }

    public static MobogramDBHelper getOpenHelper() {
        return openHelper;
    }

    public static int getSelectedColor() {
        return selectedColor;
    }

    public static int getServiceMessageColor() {
        return serviceMessageColor;
    }

    public static int getServiceSelectedMessageColor() {
        return serviceSelectedMessageColor;
    }

    private void initPlayServices() {
        AndroidUtilities.runOnUIThread(new C03462(), 1000);
    }

    public static boolean isCustomTheme() {
        return isCustomTheme;
    }

    public static void loadWallpaper() {
        if (cachedWallpaper == null) {
            Utilities.searchQueue.postRunnable(new C03451());
        }
    }

    public static void postInitApplication() {
        if (!applicationInited) {
            String localeStringIso639;
            String str;
            String str2;
            String str3;
            applicationInited = true;
            convertConfig();
            try {
                LocaleController.getInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                IntentFilter intentFilter = new IntentFilter("android.intent.action.SCREEN_ON");
                intentFilter.addAction("android.intent.action.SCREEN_OFF");
                applicationContext.registerReceiver(new ScreenReceiver(), intentFilter);
            } catch (Exception e2) {
                e2.printStackTrace();
            }
            try {
                isScreenOn = ((PowerManager) applicationContext.getSystemService("power")).isScreenOn();
                FileLog.m16e("tmessages", "screen state = " + isScreenOn);
            } catch (Throwable e3) {
                FileLog.m18e("tmessages", e3);
            }
            UserConfig.loadConfig();
            HiddenConfig.m1401d();
            String file = getFilesDirFixed().toString();
            try {
                localeStringIso639 = LocaleController.getLocaleStringIso639();
                str = Build.MANUFACTURER + Build.MODEL;
                PackageInfo packageInfo = applicationContext.getPackageManager().getPackageInfo(applicationContext.getPackageName(), 0);
                str2 = packageInfo.versionName + " (" + packageInfo.versionCode + ")";
                str3 = "SDK " + VERSION.SDK_INT;
            } catch (Exception e4) {
                localeStringIso639 = "en";
                str = "Android unknown";
                str2 = "App version unknown";
                str3 = "SDK " + VERSION.SDK_INT;
            }
            String str4 = localeStringIso639.trim().length() == 0 ? "en" : localeStringIso639;
            String str5 = str.trim().length() == 0 ? "Android unknown" : str;
            String str6 = str2.trim().length() == 0 ? "App version unknown" : str2;
            String str7 = str3.trim().length() == 0 ? "SDK Unknown" : str3;
            boolean z = applicationContext.getSharedPreferences("Notifications", 0).getBoolean("pushConnection", true);
            MessagesController.getInstance();
            ConnectionsManager.getInstance().init(BuildVars.BUILD_VERSION, 57, BuildVars.APP_ID, str5, str7, str6, str4, file, FileLog.getNetworkLogPath(), UserConfig.getClientUserId(), z);
            if (UserConfig.getCurrentUser() != null) {
                MessagesController.getInstance().putUser(UserConfig.getCurrentUser(), true);
                ConnectionsManager.getInstance().applyCountryPortNumber(UserConfig.getCurrentUser().phone);
                MessagesController.getInstance().getBlockedUsers(true);
                SendMessagesHelper.getInstance().checkUnsentMessages();
            }
            ((ApplicationLoader) applicationContext).initPlayServices();
            FileLog.m16e("tmessages", "app initied");
            ContactsController.getInstance().checkAppAccount();
            MediaController.m71a();
        }
    }

    public static void reloadWallpaper() {
        cachedWallpaper = null;
        serviceMessageColor = 0;
        applicationContext.getSharedPreferences("mainconfig", 0).edit().remove("serviceMessageColor").commit();
        loadWallpaper();
    }

    public static void startPushService() {
        if (applicationContext.getSharedPreferences("Notifications", 0).getBoolean("pushService", true)) {
            applicationContext.startService(new Intent(applicationContext, NotificationsService.class));
        } else {
            stopPushService();
        }
    }

    public static void stopPushService() {
        applicationContext.stopService(new Intent(applicationContext, NotificationsService.class));
        ((AlarmManager) applicationContext.getSystemService(NotificationCompatApi24.CATEGORY_ALARM)).cancel(PendingIntent.getService(applicationContext, 0, new Intent(applicationContext, NotificationsService.class), 0));
    }

    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        try {
            LocaleController.getInstance().onDeviceConfigurationChange(configuration);
            AndroidUtilities.checkDisplaySize(applicationContext, configuration);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onCreate() {
        super.onCreate();
        openHelper = constructSQLiteOpenHelper();
        setDefaultLocale();
        applicationContext = getApplicationContext();
        NativeLoader.initNativeLibs(applicationContext);
        Utils.m2588a();
        boolean z = VERSION.SDK_INT == 14 || VERSION.SDK_INT == 15;
        ConnectionsManager.native_setJava(z);
        ForegroundDetector foregroundDetector = new ForegroundDetector(this);
        applicationHandler = new Handler(applicationContext.getMainLooper());
        startPushService();
        MoboConstants.m1379a();
        AdvanceTheme.m2279a();
        SpecificContactBiz.m2017a();
        if (MoboConstants.aF) {
            ArchiveUtil.m269c();
        }
        catchErrors();
    }

    public void setDefaultLocale() {
        SharedPreferences sharedPreferences = getSharedPreferences("mainconfig", 0);
        if (!sharedPreferences.getBoolean("defaultLangSet", false)) {
            String string = sharedPreferences.getString("language", null);
            Editor edit = sharedPreferences.edit();
            if (string == null) {
                edit.putString("language", "fa");
            }
            edit.putBoolean("defaultLangSet", true);
            edit.commit();
        }
    }
}
