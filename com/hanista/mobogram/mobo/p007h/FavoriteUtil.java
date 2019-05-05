package com.hanista.mobogram.mobo.p007h;

import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.mobo.p004e.DataBaseAccess;
import java.util.ArrayList;
import java.util.List;

/* renamed from: com.hanista.mobogram.mobo.h.b */
public class FavoriteUtil {
    public static List<Long> f1102a;

    public static String m1140a(Long l) {
        return FavoriteUtil.m1142b(l) ? LocaleController.getString("RemoveFromFavorites", C0338R.string.RemoveFromFavorites) : LocaleController.getString("AddToFavorites", C0338R.string.AddToFavorites);
    }

    private static void m1141a() {
        List<Favorite> d = new DataBaseAccess().m870d();
        f1102a = new ArrayList();
        for (Favorite a : d) {
            f1102a.add(a.m1139a());
        }
    }

    public static boolean m1142b(Long l) {
        if (f1102a == null) {
            FavoriteUtil.m1141a();
        }
        return l == null ? false : f1102a.contains(l);
    }

    public static void m1143c(Long l) {
        new DataBaseAccess().m843a(new Favorite(l));
        FavoriteUtil.m1141a();
    }

    public static void m1144d(Long l) {
        new DataBaseAccess().m861b(l);
        FavoriteUtil.m1141a();
    }
}
