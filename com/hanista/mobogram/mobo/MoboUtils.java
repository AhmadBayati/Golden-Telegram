package com.hanista.mobogram.mobo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Environment;
import android.provider.Settings.Secure;
import android.util.Xml;
import android.view.View;
import com.hanista.mobogram.BuildConfig;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.ApplicationLoader;
import com.hanista.mobogram.messenger.FileLog;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.NotificationCenter;
import com.hanista.mobogram.messenger.exoplayer.C0700C;
import com.hanista.mobogram.tgnet.ConnectionsManager;
import com.hanista.mobogram.ui.LaunchActivity;
import com.hanista.mobogram.util.shamsicalendar.ShamsiCalendar;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import org.xmlpull.v1.XmlPullParser;

/* renamed from: com.hanista.mobogram.mobo.m */
public class MoboUtils {
    public static int m1691a() {
        return new Random(System.currentTimeMillis()).nextInt();
    }

    public static int m1692a(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (NameNotFoundException e) {
            return 1;
        }
    }

    public static ApplicationInfo m1693a(String str, Context context) {
        ApplicationInfo applicationInfo = null;
        if (!(str == null || context == null)) {
            try {
                applicationInfo = context.getPackageManager().getApplicationInfo(str, 0);
            } catch (NameNotFoundException e) {
            }
        }
        return applicationInfo;
    }

    public static App m1694a(Activity activity) {
        try {
            Uri b = MoboUtils.m1706b(activity);
            if (!(b == null || b.getHost() == null || b.getHost().equalsIgnoreCase("com.sec.android.app.launcher") || b.getHost().equalsIgnoreCase(BuildConfig.APPLICATION_ID) || b.getHost().equalsIgnoreCase("com.hanista.mobogram.two") || b.getHost().equalsIgnoreCase("com.hanista.mobogram.three") || b.getHost().equalsIgnoreCase("com.hanista.moboplus") || b.getHost().equalsIgnoreCase("com.lge.launcher2") || b.getHost().equalsIgnoreCase("com.sonyericsson.home") || b.getHost().equalsIgnoreCase("android") || b.getHost().equalsIgnoreCase("com.huawei.android.launcher") || b.getHost().equalsIgnoreCase("com.android.packageinstaller"))) {
                PackageManager packageManager = activity.getPackageManager();
                ApplicationInfo applicationInfo = packageManager.getApplicationInfo(b.getHost(), 0);
                if (applicationInfo != null) {
                    App app = new App();
                    app.m357a(applicationInfo.packageName);
                    app.m358b(packageManager.getApplicationLabel(applicationInfo) + TtmlNode.ANONYMOUS_REGION_ID);
                    app.m356a(packageManager.getApplicationIcon(applicationInfo));
                    return app;
                }
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public static String m1695a(int i, int i2) {
        char[] cArr = new char[i2];
        Arrays.fill(cArr, '0');
        return new DecimalFormat(String.valueOf(cArr)).format((long) i);
    }

    public static String m1696a(String str) {
        return str.substring(str.lastIndexOf(".") + 1);
    }

    public static String m1697a(Date date) {
        return ShamsiCalendar.dateToShamsi(date).toDateString();
    }

    public static String m1698a(byte[] bArr) {
        StringBuilder stringBuilder = new StringBuilder(bArr.length * 2);
        for (int i = 0; i < bArr.length; i++) {
            String toHexString = Integer.toHexString(bArr[i]);
            int length = toHexString.length();
            if (length == 1) {
                toHexString = "0" + toHexString;
            }
            if (length > 2) {
                toHexString = toHexString.substring(length - 2, length);
            }
            stringBuilder.append(toHexString.toUpperCase());
            if (i < bArr.length - 1) {
                stringBuilder.append(':');
            }
        }
        return stringBuilder.toString();
    }

    public static void m1699a(Context context, String str, String str2, File file) {
        MoboUtils.m1700a(context, context.getString(C0338R.string.hanista_mail), str, str2, file);
    }

    public static void m1700a(Context context, String str, String str2, String str3, File file) {
        Intent intent = new Intent("android.intent.action.SEND");
        intent.setType("plain/text");
        intent.putExtra("android.intent.extra.EMAIL", new String[]{str});
        intent.putExtra("android.intent.extra.SUBJECT", str2);
        if (file != null) {
            intent.putExtra("android.intent.extra.STREAM", Uri.fromFile(file));
        }
        intent.putExtra("android.intent.extra.TEXT", str3);
        context.startActivity(Intent.createChooser(intent, context.getString(C0338R.string.send_email)));
    }

    @SuppressLint({"NewApi"})
    public static void m1701a(View view, Drawable drawable) {
        if (view != null) {
            if (VERSION.SDK_INT < 16) {
                view.setBackgroundDrawable(drawable);
            } else {
                view.setBackground(drawable);
            }
        }
    }

    public static boolean m1702a(Context context, String str) {
        try {
            context.getPackageManager().getPackageInfo(str, 1);
            return true;
        } catch (NameNotFoundException e) {
            return false;
        }
    }

    public static boolean m1703a(File file) {
        File file2 = new File(file, ".mobo_tmp");
        try {
            if (file2.createNewFile()) {
                file2.delete();
                return true;
            }
            file2.delete();
            return false;
        } catch (Exception e) {
        }
    }

    public static boolean m1704a(File file, File file2) {
        FileOutputStream fileOutputStream;
        FileChannel channel;
        FileChannel channel2;
        Throwable e;
        FileOutputStream fileOutputStream2;
        FileInputStream fileInputStream;
        FileChannel fileChannel;
        FileChannel fileChannel2 = null;
        FileInputStream fileInputStream2;
        try {
            fileInputStream2 = new FileInputStream(file);
            try {
                fileOutputStream = new FileOutputStream(file2);
                try {
                    channel = fileInputStream2.getChannel();
                    try {
                        channel2 = fileOutputStream.getChannel();
                    } catch (Exception e2) {
                        e = e2;
                        fileOutputStream2 = fileOutputStream;
                        fileInputStream = fileInputStream2;
                        fileChannel2 = channel;
                        channel = null;
                        try {
                            FileLog.m18e("tmessages", e);
                            if (fileChannel2 != null) {
                                try {
                                    fileChannel2.close();
                                } catch (Exception e3) {
                                    return false;
                                }
                            }
                            if (channel != null) {
                                channel.close();
                            }
                            if (fileOutputStream2 != null) {
                                fileOutputStream2.close();
                            }
                            if (fileInputStream == null) {
                                return false;
                            }
                            fileInputStream.close();
                            return false;
                        } catch (Throwable th) {
                            e = th;
                            fileOutputStream = fileOutputStream2;
                            fileInputStream2 = fileInputStream;
                            fileChannel = channel;
                            channel = fileChannel2;
                            fileChannel2 = fileChannel;
                            if (channel != null) {
                                try {
                                    channel.close();
                                } catch (Exception e4) {
                                    throw e;
                                }
                            }
                            if (fileChannel2 != null) {
                                fileChannel2.close();
                            }
                            if (fileOutputStream != null) {
                                fileOutputStream.close();
                            }
                            if (fileInputStream2 != null) {
                                fileInputStream2.close();
                            }
                            throw e;
                        }
                    } catch (Throwable th2) {
                        e = th2;
                        if (channel != null) {
                            channel.close();
                        }
                        if (fileChannel2 != null) {
                            fileChannel2.close();
                        }
                        if (fileOutputStream != null) {
                            fileOutputStream.close();
                        }
                        if (fileInputStream2 != null) {
                            fileInputStream2.close();
                        }
                        throw e;
                    }
                } catch (Exception e5) {
                    e = e5;
                    channel = null;
                    fileOutputStream2 = fileOutputStream;
                    fileInputStream = fileInputStream2;
                    FileLog.m18e("tmessages", e);
                    if (fileChannel2 != null) {
                        fileChannel2.close();
                    }
                    if (channel != null) {
                        channel.close();
                    }
                    if (fileOutputStream2 != null) {
                        fileOutputStream2.close();
                    }
                    if (fileInputStream == null) {
                        return false;
                    }
                    fileInputStream.close();
                    return false;
                } catch (Throwable th3) {
                    e = th3;
                    channel = null;
                    if (channel != null) {
                        channel.close();
                    }
                    if (fileChannel2 != null) {
                        fileChannel2.close();
                    }
                    if (fileOutputStream != null) {
                        fileOutputStream.close();
                    }
                    if (fileInputStream2 != null) {
                        fileInputStream2.close();
                    }
                    throw e;
                }
            } catch (Exception e6) {
                e = e6;
                channel = null;
                fileOutputStream2 = null;
                fileInputStream = fileInputStream2;
                FileLog.m18e("tmessages", e);
                if (fileChannel2 != null) {
                    fileChannel2.close();
                }
                if (channel != null) {
                    channel.close();
                }
                if (fileOutputStream2 != null) {
                    fileOutputStream2.close();
                }
                if (fileInputStream == null) {
                    return false;
                }
                fileInputStream.close();
                return false;
            } catch (Throwable th4) {
                e = th4;
                channel = null;
                fileOutputStream = null;
                if (channel != null) {
                    channel.close();
                }
                if (fileChannel2 != null) {
                    fileChannel2.close();
                }
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
                if (fileInputStream2 != null) {
                    fileInputStream2.close();
                }
                throw e;
            }
            try {
                channel2.transferFrom(channel, 0, channel.size());
                if (channel != null) {
                    try {
                        channel.close();
                    } catch (Exception e7) {
                    }
                }
                if (channel2 != null) {
                    channel2.close();
                }
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
                if (fileInputStream2 != null) {
                    fileInputStream2.close();
                }
                return true;
            } catch (Throwable e8) {
                fileOutputStream2 = fileOutputStream;
                fileInputStream = fileInputStream2;
                fileChannel = channel;
                channel = channel2;
                e = e8;
                fileChannel2 = fileChannel;
                FileLog.m18e("tmessages", e);
                if (fileChannel2 != null) {
                    fileChannel2.close();
                }
                if (channel != null) {
                    channel.close();
                }
                if (fileOutputStream2 != null) {
                    fileOutputStream2.close();
                }
                if (fileInputStream == null) {
                    return false;
                }
                fileInputStream.close();
                return false;
            } catch (Throwable e82) {
                Throwable th5 = e82;
                fileChannel2 = channel2;
                e = th5;
                if (channel != null) {
                    channel.close();
                }
                if (fileChannel2 != null) {
                    fileChannel2.close();
                }
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
                if (fileInputStream2 != null) {
                    fileInputStream2.close();
                }
                throw e;
            }
        } catch (Exception e9) {
            e = e9;
            channel = null;
            fileOutputStream2 = null;
            fileInputStream = null;
            FileLog.m18e("tmessages", e);
            if (fileChannel2 != null) {
                fileChannel2.close();
            }
            if (channel != null) {
                channel.close();
            }
            if (fileOutputStream2 != null) {
                fileOutputStream2.close();
            }
            if (fileInputStream == null) {
                return false;
            }
            fileInputStream.close();
            return false;
        } catch (Throwable th6) {
            e = th6;
            channel = null;
            fileOutputStream = null;
            fileInputStream2 = null;
            if (channel != null) {
                channel.close();
            }
            if (fileChannel2 != null) {
                fileChannel2.close();
            }
            if (fileOutputStream != null) {
                fileOutputStream.close();
            }
            if (fileInputStream2 != null) {
                fileInputStream2.close();
            }
            throw e;
        }
    }

    public static int m1705b(Context context, String str) {
        try {
            return context.getPackageManager().getPackageInfo(str, 1).versionCode;
        } catch (NameNotFoundException e) {
            return 0;
        }
    }

    public static Uri m1706b(Activity activity) {
        Uri referrer;
        if (VERSION.SDK_INT >= 22) {
            referrer = activity.getReferrer();
        } else {
            Intent intent = activity.getIntent();
            referrer = (Uri) intent.getParcelableExtra("android.intent.extra.REFERRER");
            if (referrer != null) {
                return referrer;
            }
            String stringExtra = intent.getStringExtra("android.intent.extra.REFERRER_NAME");
            if (stringExtra != null) {
                return Uri.parse(stringExtra);
            }
            referrer = null;
        }
        if (referrer != null && !referrer.getHost().equalsIgnoreCase(BuildConfig.APPLICATION_ID)) {
            return referrer;
        }
        Intent intent2 = activity.getIntent();
        return (intent2 == null || intent2.getParcelableExtra("com.hanista.mobogram.referrer") == null) ? referrer : (Uri) intent2.getParcelableExtra("com.hanista.mobogram.referrer");
    }

    public static synchronized File m1707b() {
        File file;
        synchronized (MoboUtils.class) {
            file = new File(MoboUtils.m1711c() + File.separator + "Hanista" + File.separator);
            if (!file.exists()) {
                file.mkdir();
            }
        }
        return file;
    }

    public static String m1708b(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (NameNotFoundException e) {
            return TtmlNode.ANONYMOUS_REGION_ID;
        }
    }

    public static HashMap<String, String> m1709b(File file) {
        Throwable e;
        FileInputStream fileInputStream;
        FileInputStream fileInputStream2;
        try {
            HashMap<String, String> hashMap = new HashMap();
            XmlPullParser newPullParser = Xml.newPullParser();
            fileInputStream2 = new FileInputStream(file);
            try {
                newPullParser.setInput(fileInputStream2, C0700C.UTF8_NAME);
                String str = null;
                String str2 = null;
                String str3 = null;
                for (int eventType = newPullParser.getEventType(); eventType != 1; eventType = newPullParser.next()) {
                    if (eventType == 2) {
                        str2 = newPullParser.getName();
                        if (newPullParser.getAttributeCount() > 0) {
                            str3 = newPullParser.getAttributeValue(0);
                        }
                    } else if (eventType == 4) {
                        if (str3 != null) {
                            str = newPullParser.getText();
                            if (str != null) {
                                str = str.trim().replace("\\n", "\n").replace("\\", TtmlNode.ANONYMOUS_REGION_ID);
                            }
                        }
                    } else if (eventType == 3) {
                        str3 = null;
                        str = null;
                        str2 = null;
                    }
                    if (!(str2 == null || !str2.equals("string") || str == null || str3 == null || str.length() == 0 || str3.length() == 0)) {
                        hashMap.put(str3, str);
                        str3 = null;
                        str = null;
                        str2 = null;
                    }
                }
                if (fileInputStream2 != null) {
                    try {
                        fileInputStream2.close();
                    } catch (Throwable e2) {
                        FileLog.m18e("tmessages", e2);
                    }
                }
                return hashMap;
            } catch (Exception e3) {
                e2 = e3;
                fileInputStream = fileInputStream2;
                try {
                    FileLog.m18e("tmessages", e2);
                    if (fileInputStream != null) {
                        try {
                            fileInputStream.close();
                        } catch (Throwable e22) {
                            FileLog.m18e("tmessages", e22);
                        }
                    }
                    return null;
                } catch (Throwable th) {
                    e22 = th;
                    fileInputStream2 = fileInputStream;
                    if (fileInputStream2 != null) {
                        try {
                            fileInputStream2.close();
                        } catch (Throwable e4) {
                            FileLog.m18e("tmessages", e4);
                        }
                    }
                    throw e22;
                }
            } catch (Throwable th2) {
                e22 = th2;
                if (fileInputStream2 != null) {
                    fileInputStream2.close();
                }
                throw e22;
            }
        } catch (Exception e5) {
            e22 = e5;
            fileInputStream = null;
            FileLog.m18e("tmessages", e22);
            if (fileInputStream != null) {
                fileInputStream.close();
            }
            return null;
        } catch (Throwable th3) {
            e22 = th3;
            fileInputStream2 = null;
            if (fileInputStream2 != null) {
                fileInputStream2.close();
            }
            throw e22;
        }
    }

    public static boolean m1710b(String str) {
        return str == null || str.length() == 0;
    }

    public static synchronized File m1711c() {
        File externalStorageDirectory;
        synchronized (MoboUtils.class) {
            externalStorageDirectory = Environment.getExternalStorageDirectory();
            if (!(externalStorageDirectory == null || externalStorageDirectory.exists() || externalStorageDirectory.mkdirs())) {
                externalStorageDirectory = null;
            }
        }
        return externalStorageDirectory;
    }

    public static String m1712c(Context context) {
        return MoboUtils.m1696a(context.getPackageName());
    }

    public static String m1713c(String str) {
        char[] cArr = new char[str.length()];
        for (int i = 0; i < str.length(); i++) {
            char charAt = str.charAt(i);
            if (charAt >= '\u0660' && charAt <= '\u0669') {
                charAt = (char) (charAt - 1584);
            } else if (charAt >= '\u06f0' && charAt <= '\u06f9') {
                charAt = (char) (charAt - 1728);
            }
            cArr[i] = charAt;
        }
        return new String(cArr);
    }

    public static String m1714d(String str) {
        return str == null ? null : MoboUtils.m1717e().equals(str) ? LocaleController.getString("FirstMobogram", C0338R.string.FirstMobogram) : MoboUtils.m1719f().equals(str) ? LocaleController.getString("SecondMobogram", C0338R.string.SecondMobogram) : MoboUtils.m1721g().equals(str) ? LocaleController.getString("ThirdMobogram", C0338R.string.ThirdMobogram) : null;
    }

    public static void m1715d(Context context) {
        Intent intent = new Intent("android.intent.action.EDIT");
        intent.setData(Uri.parse("bazaar://details?id=" + context.getPackageName()));
        context.startActivity(intent);
    }

    public static boolean m1716d() {
        try {
            File file = new File(Environment.getExternalStorageDirectory(), "MoboPlus");
            if (!file.exists()) {
                return false;
            }
            File file2 = new File(file, "emoji");
            return file2.exists() && file2.isDirectory() && file2.list().length == 80;
        } catch (Exception e) {
            return false;
        }
    }

    public static String m1717e() {
        return BuildConfig.APPLICATION_ID;
    }

    public static boolean m1718e(Context context) {
        NetworkInfo activeNetworkInfo = ((ConnectivityManager) context.getSystemService("connectivity")).getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static String m1719f() {
        return "com.hanista.mobogram.two";
    }

    public static String m1720f(Context context) {
        return Secure.getString(context.getContentResolver(), "android_id");
    }

    public static String m1721g() {
        return "com.hanista.mobogram.three";
    }

    public static void m1722g(Context context) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.setData(Uri.parse("http://www.hanista.com"));
        context.startActivity(intent);
    }

    public static void m1723h() {
        Editor edit = ApplicationLoader.applicationContext.getSharedPreferences("moboconfig", 0).edit();
        edit.putBoolean("turn_off", !MoboConstants.aO);
        edit.commit();
        MoboConstants.m1379a();
        if (MoboConstants.aO) {
            MoboUtils.m1725i();
            return;
        }
        ConnectionsManager.getInstance().checkConnection();
        NotificationCenter.getInstance().postNotificationName(NotificationCenter.powerChanged, new Object[0]);
        NotificationCenter.getInstance().postNotificationName(NotificationCenter.mainUserInfoChanged, new Object[0]);
    }

    public static boolean m1724h(Context context) {
        PackageManager packageManager = context.getPackageManager();
        for (String packageInfo : MoboUtils.m1729l(context)) {
            try {
                packageManager.getPackageInfo(packageInfo, 1);
                return true;
            } catch (NameNotFoundException e) {
            }
        }
        return false;
    }

    public static void m1725i() {
        ((AlarmManager) ApplicationLoader.applicationContext.getSystemService(NotificationCompatApi24.CATEGORY_ALARM)).set(1, System.currentTimeMillis() + 1000, PendingIntent.getActivity(ApplicationLoader.applicationContext, 1234567, new Intent(ApplicationLoader.applicationContext, LaunchActivity.class), 268435456));
        System.exit(0);
    }

    public static boolean m1726i(Context context) {
        return context.getPackageName().equals(MoboUtils.m1717e());
    }

    public static boolean m1727j(Context context) {
        return context.getPackageName().equals(MoboUtils.m1719f());
    }

    public static boolean m1728k(Context context) {
        return context.getPackageName().equals(MoboUtils.m1721g());
    }

    public static List<String> m1729l(Context context) {
        List<String> arrayList = new ArrayList();
        if (context.getPackageName().equals(MoboUtils.m1717e())) {
            arrayList.add(MoboUtils.m1719f());
            arrayList.add(MoboUtils.m1721g());
        } else if (context.getPackageName().equals(MoboUtils.m1719f())) {
            arrayList.add(MoboUtils.m1717e());
            arrayList.add(MoboUtils.m1721g());
        } else {
            arrayList.add(MoboUtils.m1717e());
            arrayList.add(MoboUtils.m1719f());
        }
        return arrayList;
    }

    public static List<String> m1730m(Context context) {
        PackageManager packageManager = context.getPackageManager();
        List<String> l = MoboUtils.m1729l(context);
        List<String> arrayList = new ArrayList();
        for (String str : l) {
            try {
                packageManager.getPackageInfo(str, 1);
                arrayList.add(str);
            } catch (NameNotFoundException e) {
            }
        }
        return arrayList;
    }

    public static boolean m1731n(Context context) {
        return MoboUtils.m1702a(context, MoboUtils.m1717e());
    }

    public static boolean m1732o(Context context) {
        return MoboUtils.m1734q(context).equalsIgnoreCase("13:AD:93:96:94:26:2A:DE:5A:23:ED:F3:6E:AA:E3:53:83:DB:2B:EE");
    }

    public static boolean m1733p(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(MoboUtils.m1717e(), 1).versionCode >= 82190;
        } catch (NameNotFoundException e) {
            return false;
        }
    }

    public static String m1734q(Context context) {
        try {
            try {
                try {
                    try {
                        return MoboUtils.m1698a(MessageDigest.getInstance("SHA1").digest(((X509Certificate) CertificateFactory.getInstance("X509").generateCertificate(new ByteArrayInputStream(context.getPackageManager().getPackageInfo(MoboUtils.m1717e(), 64).signatures[0].toByteArray()))).getEncoded()));
                    } catch (NoSuchAlgorithmException e) {
                        return "invalid";
                    } catch (CertificateEncodingException e2) {
                        return "invalid";
                    }
                } catch (CertificateException e3) {
                    return "invalid";
                }
            } catch (CertificateException e4) {
                return "invalid";
            }
        } catch (NameNotFoundException e5) {
            return "invalid";
        }
    }

    public static String m1735r(Context context) {
        String absolutePath = context.getFilesDir().getAbsolutePath();
        File file = new File(absolutePath.substring(0, absolutePath.lastIndexOf(47) + 1) + "shared_prefs/");
        if (!file.exists()) {
            file = new File("/dbdata/databases/" + context.getPackageName() + "/shared_prefs/");
        }
        return file.getAbsolutePath();
    }
}
