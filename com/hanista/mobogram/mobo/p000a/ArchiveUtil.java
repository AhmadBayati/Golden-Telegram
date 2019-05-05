package com.hanista.mobogram.mobo.p000a;

import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.MessageObject;
import com.hanista.mobogram.messenger.MessagesController;
import com.hanista.mobogram.messenger.UserConfig;
import com.hanista.mobogram.mobo.p004e.DataBaseAccess;
import com.hanista.mobogram.tgnet.ConnectionsManager;
import com.hanista.mobogram.tgnet.TLRPC;
import com.hanista.mobogram.tgnet.TLRPC.User;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/* renamed from: com.hanista.mobogram.mobo.a.f */
public class ArchiveUtil {
    public static HashSet<Integer> f143a;
    public static HashMap<Long, HashSet<Integer>> f144b;

    static {
        f143a = new HashSet();
        f144b = new HashMap();
    }

    public static long m255a(ArchiveMessageInfo archiveMessageInfo) {
        long longValue = new DataBaseAccess().m838a(archiveMessageInfo).longValue();
        ArchiveUtil.m269c();
        return longValue;
    }

    public static long m256a(Long l, int i, long j) {
        long longValue = new DataBaseAccess().m838a(new ArchiveMessageInfo(null, null, Integer.valueOf(i), Long.valueOf(j), l)).longValue();
        ArchiveUtil.m269c();
        ArchiveUtil.m267b();
        return longValue;
    }

    public static ArchiveMessageInfo m257a(int i) {
        return new DataBaseAccess().m868d(i);
    }

    public static Long m258a(Archive archive) {
        Long a = new DataBaseAccess().m837a(archive);
        ArchiveUtil.m269c();
        return a;
    }

    public static List<Archive> m259a(boolean z) {
        List<Archive> arrayList = new ArrayList();
        if (z) {
            arrayList.add(new Archive(Long.valueOf(0), LocaleController.getString("All", C0338R.string.All), Integer.valueOf(TLRPC.MESSAGE_FLAG_MEGAGROUP)));
        }
        arrayList.addAll(new DataBaseAccess().m915v());
        if (z) {
            arrayList.add(new Archive(Long.valueOf(-1), LocaleController.getString("NotCategorized", C0338R.string.NotCategorized), Integer.valueOf(ConnectionsManager.DEFAULT_DATACENTER_ID)));
        }
        return arrayList;
    }

    public static void m260a() {
        new DataBaseAccess().m917x();
        ArchiveUtil.m269c();
    }

    public static void m261a(Long l) {
        DataBaseAccess dataBaseAccess = new DataBaseAccess();
        dataBaseAccess.m866c(l.longValue());
        dataBaseAccess.m907p(l);
        ArchiveUtil.m269c();
    }

    public static void m262a(Long l, int i) {
        DataBaseAccess dataBaseAccess = new DataBaseAccess();
        ArchiveMessageInfo a = dataBaseAccess.m833a(l.longValue(), i);
        if (a != null) {
            ArrayList arrayList = new ArrayList();
            arrayList.add(a.m237b());
            MessagesController.getInstance().deleteMessages(arrayList, null, null, 0);
        }
        dataBaseAccess.m854a(l, Integer.valueOf(i));
        ArchiveUtil.m269c();
    }

    public static void m263a(List<Integer> list) {
        new DataBaseAccess().m855a((List) list);
        ArchiveUtil.m269c();
    }

    public static boolean m264a(MessageObject messageObject) {
        if (messageObject == null) {
            return false;
        }
        HashSet hashSet = (HashSet) f144b.get(Long.valueOf(messageObject.getDialogId()));
        return hashSet != null && hashSet.contains(Integer.valueOf(messageObject.getId()));
    }

    public static boolean m265a(User user) {
        return user != null && user.id == UserConfig.getClientUserId();
    }

    public static long m266b(Long l, int i) {
        long longValue = new DataBaseAccess().m838a(new ArchiveMessageInfo(null, Integer.valueOf(i), null, null, l)).longValue();
        ArchiveUtil.m269c();
        return longValue;
    }

    public static void m267b() {
    }

    public static void m268b(int i) {
        DataBaseAccess dataBaseAccess = new DataBaseAccess();
        ArchiveMessageInfo d = dataBaseAccess.m868d(i);
        if (d != null) {
            d.m236a(Long.valueOf(-1));
            dataBaseAccess.m838a(d);
        }
        ArchiveUtil.m269c();
    }

    public static void m269c() {
        DataBaseAccess dataBaseAccess = new DataBaseAccess();
        f143a = dataBaseAccess.m918y();
        f144b.clear();
        List z = dataBaseAccess.m919z();
        for (int i = 0; i < z.size(); i++) {
            ArchiveMessageInfo archiveMessageInfo = (ArchiveMessageInfo) z.get(i);
            HashSet hashSet = (HashSet) f144b.get(archiveMessageInfo.m239d());
            if (hashSet == null) {
                hashSet = new HashSet();
                f144b.put(archiveMessageInfo.m239d(), hashSet);
            }
            hashSet.add(archiveMessageInfo.m238c());
        }
    }

    public static void m270c(Long l, int i) {
        DataBaseAccess dataBaseAccess = new DataBaseAccess();
        ArchiveMessageInfo q = dataBaseAccess.m908q(l);
        if (q != null) {
            q.m235a(Integer.valueOf(i));
            dataBaseAccess.m838a(q);
        }
        ArchiveUtil.m269c();
        ArchiveUtil.m267b();
    }
}
