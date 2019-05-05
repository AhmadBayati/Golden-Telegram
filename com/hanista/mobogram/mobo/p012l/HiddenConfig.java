package com.hanista.mobogram.mobo.p012l;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Base64;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.ApplicationLoader;
import com.hanista.mobogram.messenger.FileLog;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.MessagesController;
import com.hanista.mobogram.messenger.Utilities;
import com.hanista.mobogram.messenger.exoplayer.C0700C;
import com.hanista.mobogram.mobo.p004e.DataBaseAccess;
import com.hanista.mobogram.tgnet.TLRPC.TL_dialog;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* renamed from: com.hanista.mobogram.mobo.l.b */
public class HiddenConfig {
    public static List<Long> f1398a;
    public static String f1399b;
    public static byte[] f1400c;
    public static int f1401d;
    public static boolean f1402e;
    private static final Object f1403f;

    static {
        f1403f = new Object();
        f1399b = TtmlNode.ANONYMOUS_REGION_ID;
        f1400c = new byte[0];
        f1401d = 0;
    }

    public static String m1392a(Long l) {
        return HiddenConfig.m1399b(l) ? LocaleController.getString("RemoveFromHiddens", C0338R.string.RemoveFromHiddens) : LocaleController.getString("AddToHiddens", C0338R.string.AddToHiddens);
    }

    public static ArrayList<TL_dialog> m1393a(ArrayList<TL_dialog> arrayList, boolean z) {
        ArrayList<TL_dialog> arrayList2 = new ArrayList();
        Iterator it;
        if (z) {
            it = arrayList.iterator();
            while (it.hasNext()) {
                TL_dialog tL_dialog = (TL_dialog) it.next();
                if (MessagesController.getInstance().dialogsHiddenOnly.contains(tL_dialog)) {
                    arrayList2.add(tL_dialog);
                }
            }
        } else {
            arrayList2.addAll(arrayList);
            it = MessagesController.getInstance().dialogsHiddenOnly.iterator();
            while (it.hasNext()) {
                arrayList2.remove((TL_dialog) it.next());
            }
        }
        return arrayList2;
    }

    public static void m1394a() {
        synchronized (f1403f) {
            try {
                Editor edit = ApplicationLoader.applicationContext.getSharedPreferences("moboconfig", 0).edit();
                edit.putString("passcodeHash1", f1399b);
                edit.putString("passcodeSalt", f1400c.length > 0 ? Base64.encodeToString(f1400c, 0) : TtmlNode.ANONYMOUS_REGION_ID);
                edit.putInt("passcodeType", f1401d);
                edit.commit();
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
        }
    }

    public static void m1395a(TL_dialog tL_dialog) {
        new DataBaseAccess().m844a(new Hidden(Long.valueOf(tL_dialog.id)));
        MessagesController.getInstance().dialogsHiddenOnly.add(tL_dialog);
        HiddenConfig.m1403f();
    }

    public static boolean m1396a(String str) {
        boolean z = false;
        Object bytes;
        Object obj;
        if (f1400c.length == 0) {
            z = Utilities.MD5(str).equals(f1399b);
            if (z) {
                try {
                    f1400c = new byte[16];
                    Utilities.random.nextBytes(f1400c);
                    bytes = str.getBytes(C0700C.UTF8_NAME);
                    obj = new byte[(bytes.length + 32)];
                    System.arraycopy(f1400c, 0, obj, 0, 16);
                    System.arraycopy(bytes, 0, obj, 16, bytes.length);
                    System.arraycopy(f1400c, 0, obj, bytes.length + 16, 16);
                    f1399b = Utilities.bytesToHex(Utilities.computeSHA256(obj, 0, obj.length));
                    HiddenConfig.m1394a();
                } catch (Throwable e) {
                    FileLog.m18e("tmessages", e);
                }
            }
        } else {
            try {
                bytes = str.getBytes(C0700C.UTF8_NAME);
                obj = new byte[(bytes.length + 32)];
                System.arraycopy(f1400c, 0, obj, 0, 16);
                System.arraycopy(bytes, 0, obj, 16, bytes.length);
                System.arraycopy(f1400c, 0, obj, bytes.length + 16, 16);
                z = f1399b.equals(Utilities.bytesToHex(Utilities.computeSHA256(obj, 0, obj.length)));
            } catch (Throwable e2) {
                FileLog.m18e("tmessages", e2);
            }
        }
        return z;
    }

    public static void m1397b(TL_dialog tL_dialog) {
        new DataBaseAccess().m867c(Long.valueOf(tL_dialog.id));
        MessagesController.getInstance().dialogsHiddenOnly.remove(tL_dialog);
        HiddenConfig.m1403f();
    }

    public static boolean m1398b() {
        return f1399b.length() > 0;
    }

    public static boolean m1399b(Long l) {
        if (f1398a == null) {
            HiddenConfig.m1403f();
        }
        return (l == null || !HiddenConfig.m1398b()) ? false : f1398a.contains(l);
    }

    public static void m1400c() {
        new DataBaseAccess().m879f();
        MessagesController.getInstance().dialogsHiddenOnly.clear();
        HiddenConfig.m1403f();
    }

    public static void m1401d() {
        synchronized (f1403f) {
            SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("moboconfig", 0);
            f1399b = sharedPreferences.getString("passcodeHash1", TtmlNode.ANONYMOUS_REGION_ID);
            f1401d = sharedPreferences.getInt("passcodeType", 0);
            String string = sharedPreferences.getString("passcodeSalt", TtmlNode.ANONYMOUS_REGION_ID);
            if (string.length() > 0) {
                f1400c = Base64.decode(string, 0);
            } else {
                f1400c = new byte[0];
            }
        }
    }

    public static void m1402e() {
        f1399b = TtmlNode.ANONYMOUS_REGION_ID;
        f1400c = new byte[0];
        f1401d = 0;
        HiddenConfig.m1394a();
    }

    private static void m1403f() {
        List<Hidden> e = new DataBaseAccess().m874e();
        f1398a = new ArrayList();
        for (Hidden a : e) {
            f1398a.add(a.m1391a());
        }
    }
}
