package com.hanista.mobogram.mobo.p020s;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Point;
import android.util.Log;
import android.widget.Toast;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.ApplicationLoader;
import com.hanista.mobogram.messenger.FileLoader;
import com.hanista.mobogram.messenger.FileLog;
import com.hanista.mobogram.messenger.ImageLoader;
import com.hanista.mobogram.mobo.MoboConstants;
import com.hanista.mobogram.mobo.MoboUtils;
import com.hanista.mobogram.mobo.p004e.SettingManager;
import com.hanista.mobogram.mobo.p008i.FontUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/* renamed from: com.hanista.mobogram.mobo.s.q */
public class ThemeUtil {
    private static Theme f2520a;

    public static int m2483a(Context context, String str) {
        File file = new File(MoboUtils.m1735r(context), "mobotheme.xml");
        String b = ThemeUtil.m2489b(ThemeUtil.m2484a(new File(str), file, false));
        if (!b.contains("4")) {
            Toast.makeText(context, "ERROR: " + b + "\n" + context.getString(C0338R.string.NoPrefErrorMsg, new Object[]{r1.getAbsolutePath()}), 1).show();
        }
        return Integer.parseInt(b);
    }

    static int m2484a(File file, File file2, boolean z) {
        Exception exception;
        int i = -1;
        int i2;
        try {
            if (!file.exists()) {
                return 0;
            }
            FileInputStream fileInputStream;
            FileOutputStream fileOutputStream;
            if (!file2.exists()) {
                if (z) {
                    i = 1;
                }
                file2.createNewFile();
            }
            int i3 = i;
            try {
                int i4;
                fileInputStream = new FileInputStream(file);
                Object channel = fileInputStream.getChannel();
                fileOutputStream = new FileOutputStream(file2);
                FileChannel channel2 = fileOutputStream.getChannel();
                if (channel2 == null || channel == null) {
                    i4 = i3;
                } else {
                    channel2.transferFrom(channel, 0, channel.size());
                    i4 = 2;
                }
                if (channel != null) {
                    try {
                        channel.close();
                        i = 3;
                    } catch (Exception e) {
                        exception = e;
                        i2 = i4;
                        System.err.println("Error saving preferences: " + exception.getMessage());
                        Log.e(exception.getMessage(), exception.toString());
                        return i2;
                    }
                }
                i = i4;
                if (channel2 != null) {
                    channel2.close();
                    i2 = 4;
                } else {
                    i2 = i;
                }
            } catch (Exception e2) {
                exception = e2;
                i2 = i3;
                System.err.println("Error saving preferences: " + exception.getMessage());
                Log.e(exception.getMessage(), exception.toString());
                return i2;
            }
            try {
                fileInputStream.close();
                fileOutputStream.close();
                return i2;
            } catch (Exception e3) {
                exception = e3;
                System.err.println("Error saving preferences: " + exception.getMessage());
                Log.e(exception.getMessage(), exception.toString());
                return i2;
            }
        } catch (Exception e22) {
            Exception exception2 = e22;
            i2 = i;
            exception = exception2;
            System.err.println("Error saving preferences: " + exception.getMessage());
            Log.e(exception.getMessage(), exception.toString());
            return i2;
        }
    }

    public static Theme m2485a() {
        if (f2520a == null) {
            int a = new SettingManager().m941a("currentTheme");
            for (Theme theme : ThemeUtil.m2494d()) {
                if (theme.m2287a() == a) {
                    f2520a = new ThemeBase(theme);
                    break;
                }
            }
        }
        if (f2520a == null) {
            if (MoboUtils.m1726i(ApplicationLoader.applicationContext)) {
                f2520a = new ThemeBase(new GreenTheme());
            } else {
                f2520a = new ThemeBase(new IndigoTheme());
            }
        }
        return f2520a;
    }

    public static void m2486a(int i) {
        new SettingManager().m942a("currentTheme", i);
        ThemeUtil.m2495e();
    }

    public static void m2487a(String str) {
        FileOutputStream fileOutputStream;
        Throwable e;
        FileOutputStream fileOutputStream2 = null;
        String str2 = "wallpaper.jpg";
        if (new File(str).exists()) {
            try {
                Point realScreenSize = AndroidUtilities.getRealScreenSize();
                Bitmap loadBitmap = ImageLoader.loadBitmap(str, null, (float) realScreenSize.x, (float) realScreenSize.y, true);
                fileOutputStream = new FileOutputStream(new File(ApplicationLoader.applicationContext.getFilesDir(), str2));
                try {
                    loadBitmap.compress(CompressFormat.JPEG, 93, fileOutputStream);
                    if (fileOutputStream != null) {
                        try {
                            fileOutputStream.close();
                        } catch (Throwable e2) {
                            FileLog.m18e("tmessages", e2);
                        }
                    }
                } catch (Exception e3) {
                    e2 = e3;
                    try {
                        FileLog.m18e("tmessages", e2);
                        if (fileOutputStream != null) {
                            try {
                                fileOutputStream.close();
                            } catch (Throwable e22) {
                                FileLog.m18e("tmessages", e22);
                            }
                        }
                    } catch (Throwable th) {
                        e22 = th;
                        fileOutputStream2 = fileOutputStream;
                        if (fileOutputStream2 != null) {
                            try {
                                fileOutputStream2.close();
                            } catch (Throwable e4) {
                                FileLog.m18e("tmessages", e4);
                            }
                        }
                        throw e22;
                    }
                }
            } catch (Exception e5) {
                e22 = e5;
                fileOutputStream = null;
                FileLog.m18e("tmessages", e22);
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (Throwable th2) {
                e22 = th2;
                if (fileOutputStream2 != null) {
                    fileOutputStream2.close();
                }
                throw e22;
            }
        }
    }

    public static boolean m2488a(File file) {
        try {
            HashMap b = MoboUtils.m1709b(file);
            String absolutePath = file.getAbsolutePath();
            String str = (String) b.get("mobo_theme_name");
            if (str == null || str.length() <= 0) {
                return false;
            }
            if (str.contains("&") || str.contains("|")) {
                return false;
            }
            if (ThemeUtil.m2483a(ApplicationLoader.applicationContext, absolutePath) != 4) {
                return false;
            }
            ThemeUtil.m2491b(absolutePath.substring(0, absolutePath.lastIndexOf(".")) + "_wallpaper.jpg");
            return true;
        } catch (Throwable e) {
            FileLog.m18e("tmessages", e);
            return false;
        }
    }

    static String m2489b(int i) {
        String str = "-1";
        if (i == 0) {
            str = "0: SOURCE FILE DOESN'T EXIST";
        }
        if (i == 1) {
            str = "1: DESTINATION FILE DOESN'T EXIST";
        }
        if (i == 2) {
            str = "2: NULL SOURCE & DESTINATION FILES";
        }
        if (i == 3) {
            str = "3: NULL SOURCE FILE";
        }
        return i == 4 ? "4" : str;
    }

    public static boolean m2490b() {
        return AdvanceTheme.f2490a;
    }

    public static boolean m2491b(String str) {
        try {
            if (new File(str).exists()) {
                SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
                if (sharedPreferences.getInt("selectedBackground", 1000001) == 1000001) {
                    Editor edit = sharedPreferences.edit();
                    edit.putInt("selectedBackground", 113);
                    edit.putInt("selectedColor", 0);
                    edit.commit();
                }
                ThemeUtil.m2487a(str);
            }
            return true;
        } catch (Throwable e) {
            FileLog.m18e("tmessages", e);
            return false;
        }
    }

    public static void m2492c() {
        f2520a = null;
    }

    public static boolean m2493c(String str) {
        try {
            File file = new File(str);
            if (!file.exists()) {
                return false;
            }
            File file2 = new File(new File(MoboConstants.m1381b(), MoboConstants.f1325R), "Font");
            file2.mkdir();
            if (!FileLoader.renameTo(file, new File(file2, "customfont.ttf"))) {
                return false;
            }
            FontUtil.m1177a(-1);
            return true;
        } catch (Throwable e) {
            FileLog.m18e("tmessages", e);
            return false;
        }
    }

    public static List<Theme> m2494d() {
        List<Theme> arrayList = new ArrayList();
        arrayList.add(new PurpleTheme());
        arrayList.add(new GreenTheme());
        arrayList.add(new BrownTheme());
        arrayList.add(new LightGreenTheme());
        arrayList.add(new BlueGreyTheme());
        arrayList.add(new IndigoTheme());
        arrayList.add(new TelegramTheme());
        arrayList.add(new BlackTheme());
        arrayList.add(new PinkTheme());
        arrayList.add(new CyanTheme());
        arrayList.add(new OrangeTheme());
        arrayList.add(new RedTheme());
        return arrayList;
    }

    public static void m2495e() {
        new File(MoboUtils.m1735r(ApplicationLoader.applicationContext), "mobotheme.xml").delete();
    }
}
