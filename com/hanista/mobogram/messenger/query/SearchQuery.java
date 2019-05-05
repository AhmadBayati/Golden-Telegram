package com.hanista.mobogram.messenger.query;

import android.support.v4.view.PointerIconCompat;
import android.text.TextUtils;
import com.hanista.mobogram.SQLite.SQLiteCursor;
import com.hanista.mobogram.SQLite.SQLitePreparedStatement;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.FileLog;
import com.hanista.mobogram.messenger.MessagesController;
import com.hanista.mobogram.messenger.MessagesStorage;
import com.hanista.mobogram.messenger.NotificationCenter;
import com.hanista.mobogram.messenger.UserConfig;
import com.hanista.mobogram.mobo.p012l.HiddenConfig;
import com.hanista.mobogram.tgnet.ConnectionsManager;
import com.hanista.mobogram.tgnet.RequestDelegate;
import com.hanista.mobogram.tgnet.TLObject;
import com.hanista.mobogram.tgnet.TLRPC.TL_contacts_getTopPeers;
import com.hanista.mobogram.tgnet.TLRPC.TL_contacts_resetTopPeerRating;
import com.hanista.mobogram.tgnet.TLRPC.TL_contacts_topPeers;
import com.hanista.mobogram.tgnet.TLRPC.TL_error;
import com.hanista.mobogram.tgnet.TLRPC.TL_peerChat;
import com.hanista.mobogram.tgnet.TLRPC.TL_peerUser;
import com.hanista.mobogram.tgnet.TLRPC.TL_topPeer;
import com.hanista.mobogram.tgnet.TLRPC.TL_topPeerCategoryBotsInline;
import com.hanista.mobogram.tgnet.TLRPC.TL_topPeerCategoryCorrespondents;
import com.hanista.mobogram.tgnet.TLRPC.TL_topPeerCategoryPeers;
import com.hanista.mobogram.tgnet.TLRPC.User;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

public class SearchQuery {
    public static ArrayList<TL_topPeer> hints;
    public static ArrayList<TL_topPeer> inlineBots;
    private static HashMap<Integer, Integer> inlineDates;
    public static boolean loaded;
    private static boolean loading;

    /* renamed from: com.hanista.mobogram.messenger.query.SearchQuery.1 */
    static class C07911 implements Runnable {

        /* renamed from: com.hanista.mobogram.messenger.query.SearchQuery.1.1 */
        class C07901 implements Runnable {
            final /* synthetic */ ArrayList val$chats;
            final /* synthetic */ ArrayList val$hintsNew;
            final /* synthetic */ ArrayList val$inlineBotsNew;
            final /* synthetic */ HashMap val$inlineDatesNew;
            final /* synthetic */ ArrayList val$users;

            C07901(ArrayList arrayList, ArrayList arrayList2, ArrayList arrayList3, ArrayList arrayList4, HashMap hashMap) {
                this.val$users = arrayList;
                this.val$chats = arrayList2;
                this.val$hintsNew = arrayList3;
                this.val$inlineBotsNew = arrayList4;
                this.val$inlineDatesNew = hashMap;
            }

            public void run() {
                MessagesController.getInstance().putUsers(this.val$users, true);
                MessagesController.getInstance().putChats(this.val$chats, true);
                SearchQuery.loading = false;
                SearchQuery.loaded = true;
                SearchQuery.hints = this.val$hintsNew;
                SearchQuery.inlineBots = this.val$inlineBotsNew;
                SearchQuery.inlineDates = this.val$inlineDatesNew;
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.reloadHints, new Object[0]);
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.reloadInlineHints, new Object[0]);
                if (Math.abs(UserConfig.lastHintsSyncTime - ((int) (System.currentTimeMillis() / 1000))) >= 86400) {
                    SearchQuery.loadHints(false);
                }
            }
        }

        C07911() {
        }

        public void run() {
            ArrayList arrayList = new ArrayList();
            ArrayList arrayList2 = new ArrayList();
            HashMap hashMap = new HashMap();
            ArrayList arrayList3 = new ArrayList();
            ArrayList arrayList4 = new ArrayList();
            try {
                Iterable arrayList5 = new ArrayList();
                Iterable arrayList6 = new ArrayList();
                SQLiteCursor queryFinalized = MessagesStorage.getInstance().getDatabase().queryFinalized("SELECT did, type, rating, date FROM chat_hints WHERE 1 ORDER BY rating DESC", new Object[0]);
                while (queryFinalized.next()) {
                    int intValue = queryFinalized.intValue(0);
                    int intValue2 = queryFinalized.intValue(1);
                    TL_topPeer tL_topPeer = new TL_topPeer();
                    tL_topPeer.rating = queryFinalized.doubleValue(2);
                    if (intValue > 0) {
                        tL_topPeer.peer = new TL_peerUser();
                        tL_topPeer.peer.user_id = intValue;
                        arrayList5.add(Integer.valueOf(intValue));
                    } else {
                        tL_topPeer.peer = new TL_peerChat();
                        tL_topPeer.peer.chat_id = -intValue;
                        arrayList6.add(Integer.valueOf(-intValue));
                    }
                    if (intValue2 == 0) {
                        arrayList.add(tL_topPeer);
                    } else if (intValue2 == 1) {
                        arrayList2.add(tL_topPeer);
                        hashMap.put(Integer.valueOf(intValue), Integer.valueOf(queryFinalized.intValue(3)));
                    }
                }
                queryFinalized.dispose();
                if (!arrayList5.isEmpty()) {
                    MessagesStorage.getInstance().getUsersInternal(TextUtils.join(",", arrayList5), arrayList3);
                }
                if (!arrayList6.isEmpty()) {
                    MessagesStorage.getInstance().getChatsInternal(TextUtils.join(",", arrayList6), arrayList4);
                }
                AndroidUtilities.runOnUIThread(new C07901(arrayList3, arrayList4, arrayList, arrayList2, hashMap));
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.query.SearchQuery.2 */
    static class C07952 implements RequestDelegate {

        /* renamed from: com.hanista.mobogram.messenger.query.SearchQuery.2.1 */
        class C07941 implements Runnable {
            final /* synthetic */ TLObject val$response;

            /* renamed from: com.hanista.mobogram.messenger.query.SearchQuery.2.1.1 */
            class C07931 implements Runnable {
                final /* synthetic */ HashMap val$inlineDatesCopy;
                final /* synthetic */ TL_contacts_topPeers val$topPeers;

                /* renamed from: com.hanista.mobogram.messenger.query.SearchQuery.2.1.1.1 */
                class C07921 implements Runnable {
                    C07921() {
                    }

                    public void run() {
                        UserConfig.lastHintsSyncTime = (int) (System.currentTimeMillis() / 1000);
                        UserConfig.saveConfig(false);
                    }
                }

                C07931(TL_contacts_topPeers tL_contacts_topPeers, HashMap hashMap) {
                    this.val$topPeers = tL_contacts_topPeers;
                    this.val$inlineDatesCopy = hashMap;
                }

                public void run() {
                    try {
                        MessagesStorage.getInstance().getDatabase().executeFast("DELETE FROM chat_hints WHERE 1").stepThis().dispose();
                        MessagesStorage.getInstance().getDatabase().beginTransaction();
                        MessagesStorage.getInstance().putUsersAndChats(this.val$topPeers.users, this.val$topPeers.chats, false, false);
                        SQLitePreparedStatement executeFast = MessagesStorage.getInstance().getDatabase().executeFast("REPLACE INTO chat_hints VALUES(?, ?, ?, ?)");
                        for (int i = 0; i < this.val$topPeers.categories.size(); i++) {
                            TL_topPeerCategoryPeers tL_topPeerCategoryPeers = (TL_topPeerCategoryPeers) this.val$topPeers.categories.get(i);
                            int i2 = tL_topPeerCategoryPeers.category instanceof TL_topPeerCategoryBotsInline ? 1 : 0;
                            for (int i3 = 0; i3 < tL_topPeerCategoryPeers.peers.size(); i3++) {
                                TL_topPeer tL_topPeer = (TL_topPeer) tL_topPeerCategoryPeers.peers.get(i3);
                                int i4 = tL_topPeer.peer instanceof TL_peerUser ? tL_topPeer.peer.user_id : tL_topPeer.peer instanceof TL_peerChat ? -tL_topPeer.peer.chat_id : -tL_topPeer.peer.channel_id;
                                Integer num = (Integer) this.val$inlineDatesCopy.get(Integer.valueOf(i4));
                                executeFast.requery();
                                executeFast.bindInteger(1, i4);
                                executeFast.bindInteger(2, i2);
                                executeFast.bindDouble(3, tL_topPeer.rating);
                                executeFast.bindInteger(4, num != null ? num.intValue() : 0);
                                executeFast.step();
                            }
                        }
                        executeFast.dispose();
                        MessagesStorage.getInstance().getDatabase().commitTransaction();
                        AndroidUtilities.runOnUIThread(new C07921());
                    } catch (Throwable e) {
                        FileLog.m18e("tmessages", e);
                    }
                }
            }

            C07941(TLObject tLObject) {
                this.val$response = tLObject;
            }

            public void run() {
                TL_contacts_topPeers tL_contacts_topPeers = (TL_contacts_topPeers) this.val$response;
                MessagesController.getInstance().putUsers(tL_contacts_topPeers.users, false);
                MessagesController.getInstance().putChats(tL_contacts_topPeers.chats, false);
                for (int i = 0; i < tL_contacts_topPeers.categories.size(); i++) {
                    TL_topPeerCategoryPeers tL_topPeerCategoryPeers = (TL_topPeerCategoryPeers) tL_contacts_topPeers.categories.get(i);
                    if (tL_topPeerCategoryPeers.category instanceof TL_topPeerCategoryBotsInline) {
                        SearchQuery.inlineBots = tL_topPeerCategoryPeers.peers;
                    } else {
                        SearchQuery.hints = tL_topPeerCategoryPeers.peers;
                    }
                }
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.reloadHints, new Object[0]);
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.reloadInlineHints, new Object[0]);
                MessagesStorage.getInstance().getStorageQueue().postRunnable(new C07931(tL_contacts_topPeers, new HashMap(SearchQuery.inlineDates)));
            }
        }

        C07952() {
        }

        public void run(TLObject tLObject, TL_error tL_error) {
            if (tLObject instanceof TL_contacts_topPeers) {
                AndroidUtilities.runOnUIThread(new C07941(tLObject));
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.query.SearchQuery.3 */
    static class C07963 implements Comparator<TL_topPeer> {
        C07963() {
        }

        public int compare(TL_topPeer tL_topPeer, TL_topPeer tL_topPeer2) {
            return tL_topPeer.rating > tL_topPeer2.rating ? -1 : tL_topPeer.rating < tL_topPeer2.rating ? 1 : 0;
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.query.SearchQuery.4 */
    static class C07974 implements RequestDelegate {
        C07974() {
        }

        public void run(TLObject tLObject, TL_error tL_error) {
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.query.SearchQuery.5 */
    static class C07985 implements RequestDelegate {
        C07985() {
        }

        public void run(TLObject tLObject, TL_error tL_error) {
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.query.SearchQuery.6 */
    static class C08016 implements Runnable {
        final /* synthetic */ long val$did;
        final /* synthetic */ int val$lower_id;

        /* renamed from: com.hanista.mobogram.messenger.query.SearchQuery.6.1 */
        class C08001 implements Runnable {
            final /* synthetic */ double val$dtFinal;

            /* renamed from: com.hanista.mobogram.messenger.query.SearchQuery.6.1.1 */
            class C07991 implements Comparator<TL_topPeer> {
                C07991() {
                }

                public int compare(TL_topPeer tL_topPeer, TL_topPeer tL_topPeer2) {
                    return tL_topPeer.rating > tL_topPeer2.rating ? -1 : tL_topPeer.rating < tL_topPeer2.rating ? 1 : 0;
                }
            }

            C08001(double d) {
                this.val$dtFinal = d;
            }

            /* JADX WARNING: inconsistent code. */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public void run() {
                /*
                r10 = this;
                r2 = 0;
                r3 = 0;
                r1 = r2;
            L_0x0003:
                r0 = com.hanista.mobogram.messenger.query.SearchQuery.hints;
                r0 = r0.size();
                if (r1 >= r0) goto L_0x00a9;
            L_0x000b:
                r0 = com.hanista.mobogram.messenger.query.SearchQuery.hints;
                r0 = r0.get(r1);
                r0 = (com.hanista.mobogram.tgnet.TLRPC.TL_topPeer) r0;
                r4 = com.hanista.mobogram.messenger.query.SearchQuery.C08016.this;
                r4 = r4.val$lower_id;
                if (r4 >= 0) goto L_0x002f;
            L_0x0019:
                r4 = r0.peer;
                r4 = r4.chat_id;
                r5 = com.hanista.mobogram.messenger.query.SearchQuery.C08016.this;
                r5 = r5.val$lower_id;
                r5 = -r5;
                if (r4 == r5) goto L_0x003f;
            L_0x0024:
                r4 = r0.peer;
                r4 = r4.channel_id;
                r5 = com.hanista.mobogram.messenger.query.SearchQuery.C08016.this;
                r5 = r5.val$lower_id;
                r5 = -r5;
                if (r4 == r5) goto L_0x003f;
            L_0x002f:
                r4 = com.hanista.mobogram.messenger.query.SearchQuery.C08016.this;
                r4 = r4.val$lower_id;
                if (r4 <= 0) goto L_0x0093;
            L_0x0035:
                r4 = r0.peer;
                r4 = r4.user_id;
                r5 = com.hanista.mobogram.messenger.query.SearchQuery.C08016.this;
                r5 = r5.val$lower_id;
                if (r4 != r5) goto L_0x0093;
            L_0x003f:
                if (r0 != 0) goto L_0x0060;
            L_0x0041:
                r0 = new com.hanista.mobogram.tgnet.TLRPC$TL_topPeer;
                r0.<init>();
                r1 = com.hanista.mobogram.messenger.query.SearchQuery.C08016.this;
                r1 = r1.val$lower_id;
                if (r1 <= 0) goto L_0x0098;
            L_0x004c:
                r1 = new com.hanista.mobogram.tgnet.TLRPC$TL_peerUser;
                r1.<init>();
                r0.peer = r1;
                r1 = r0.peer;
                r3 = com.hanista.mobogram.messenger.query.SearchQuery.C08016.this;
                r3 = r3.val$lower_id;
                r1.user_id = r3;
            L_0x005b:
                r1 = com.hanista.mobogram.messenger.query.SearchQuery.hints;
                r1.add(r0);
            L_0x0060:
                r4 = r0.rating;
                r6 = r10.val$dtFinal;
                r1 = com.hanista.mobogram.messenger.MessagesController.getInstance();
                r1 = r1.ratingDecay;
                r8 = (double) r1;
                r6 = r6 / r8;
                r6 = java.lang.Math.exp(r6);
                r4 = r4 + r6;
                r0.rating = r4;
                r1 = com.hanista.mobogram.messenger.query.SearchQuery.hints;
                r3 = new com.hanista.mobogram.messenger.query.SearchQuery$6$1$1;
                r3.<init>();
                java.util.Collections.sort(r1, r3);
                r1 = com.hanista.mobogram.messenger.query.SearchQuery.C08016.this;
                r4 = r1.val$did;
                r1 = (int) r4;
                r4 = r0.rating;
                com.hanista.mobogram.messenger.query.SearchQuery.savePeer(r1, r2, r4);
                r0 = com.hanista.mobogram.messenger.NotificationCenter.getInstance();
                r1 = com.hanista.mobogram.messenger.NotificationCenter.reloadHints;
                r2 = new java.lang.Object[r2];
                r0.postNotificationName(r1, r2);
                return;
            L_0x0093:
                r0 = r1 + 1;
                r1 = r0;
                goto L_0x0003;
            L_0x0098:
                r1 = new com.hanista.mobogram.tgnet.TLRPC$TL_peerChat;
                r1.<init>();
                r0.peer = r1;
                r1 = r0.peer;
                r3 = com.hanista.mobogram.messenger.query.SearchQuery.C08016.this;
                r3 = r3.val$lower_id;
                r3 = -r3;
                r1.chat_id = r3;
                goto L_0x005b;
            L_0x00a9:
                r0 = r3;
                goto L_0x003f;
                */
                throw new UnsupportedOperationException("Method not decompiled: com.hanista.mobogram.messenger.query.SearchQuery.6.1.run():void");
            }
        }

        C08016(long j, int i) {
            this.val$did = j;
            this.val$lower_id = i;
        }

        public void run() {
            int i = 0;
            double d = 0.0d;
            try {
                int intValue;
                SQLiteCursor queryFinalized = MessagesStorage.getInstance().getDatabase().queryFinalized(String.format(Locale.US, "SELECT MAX(mid), MAX(date) FROM messages WHERE uid = %d AND out = 1", new Object[]{Long.valueOf(this.val$did)}), new Object[0]);
                if (queryFinalized.next()) {
                    i = queryFinalized.intValue(0);
                    intValue = queryFinalized.intValue(1);
                } else {
                    intValue = 0;
                }
                queryFinalized.dispose();
                if (i > 0) {
                    SQLiteCursor queryFinalized2 = MessagesStorage.getInstance().getDatabase().queryFinalized(String.format(Locale.US, "SELECT date FROM messages WHERE uid = %d AND mid < %d AND out = 1 ORDER BY date DESC", new Object[]{Long.valueOf(this.val$did), Integer.valueOf(i)}), new Object[0]);
                    if (queryFinalized2.next()) {
                        d = (double) (intValue - queryFinalized2.intValue(0));
                    }
                    queryFinalized2.dispose();
                }
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
            AndroidUtilities.runOnUIThread(new C08001(d));
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.query.SearchQuery.7 */
    static class C08027 implements Runnable {
        final /* synthetic */ int val$did;
        final /* synthetic */ double val$rating;
        final /* synthetic */ int val$type;

        C08027(int i, int i2, double d) {
            this.val$did = i;
            this.val$type = i2;
            this.val$rating = d;
        }

        public void run() {
            try {
                SQLitePreparedStatement executeFast = MessagesStorage.getInstance().getDatabase().executeFast("REPLACE INTO chat_hints VALUES(?, ?, ?, ?)");
                executeFast.requery();
                executeFast.bindInteger(1, this.val$did);
                executeFast.bindInteger(2, this.val$type);
                executeFast.bindDouble(3, this.val$rating);
                executeFast.bindInteger(4, ((int) System.currentTimeMillis()) / PointerIconCompat.TYPE_DEFAULT);
                executeFast.step();
                executeFast.dispose();
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.query.SearchQuery.8 */
    static class C08038 implements Runnable {
        final /* synthetic */ int val$did;
        final /* synthetic */ int val$type;

        C08038(int i, int i2) {
            this.val$did = i;
            this.val$type = i2;
        }

        public void run() {
            try {
                MessagesStorage.getInstance().getDatabase().executeFast(String.format(Locale.US, "DELETE FROM chat_hints WHERE did = %d AND type = %d", new Object[]{Integer.valueOf(this.val$did), Integer.valueOf(this.val$type)})).stepThis().dispose();
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
        }
    }

    static {
        hints = new ArrayList();
        inlineBots = new ArrayList();
        inlineDates = new HashMap();
    }

    public static void cleanup() {
        loading = false;
        loaded = false;
        hints.clear();
        inlineBots.clear();
        inlineDates.clear();
        NotificationCenter.getInstance().postNotificationName(NotificationCenter.reloadHints, new Object[0]);
        NotificationCenter.getInstance().postNotificationName(NotificationCenter.reloadInlineHints, new Object[0]);
    }

    private static void deletePeer(int i, int i2) {
        MessagesStorage.getInstance().getStorageQueue().postRunnable(new C08038(i, i2));
    }

    public static ArrayList<TL_topPeer> getHints() {
        ArrayList<TL_topPeer> arrayList = new ArrayList();
        Iterator it = hints.iterator();
        while (it.hasNext()) {
            TL_topPeer tL_topPeer = (TL_topPeer) it.next();
            if (tL_topPeer.peer != null) {
                int i = 0;
                if (tL_topPeer.peer.user_id != 0) {
                    i = tL_topPeer.peer.user_id;
                } else if (tL_topPeer.peer.channel_id != 0) {
                    i = -tL_topPeer.peer.channel_id;
                } else if (tL_topPeer.peer.chat_id != 0) {
                    i = -tL_topPeer.peer.chat_id;
                }
                if (!HiddenConfig.m1399b(Long.valueOf((long) i))) {
                    arrayList.add(tL_topPeer);
                }
            }
        }
        return arrayList;
    }

    public static void increaseInlineRaiting(int i) {
        TL_topPeer tL_topPeer;
        Integer num = (Integer) inlineDates.get(Integer.valueOf(i));
        int max = num != null ? Math.max(1, ((int) (System.currentTimeMillis() / 1000)) - num.intValue()) : 60;
        for (int i2 = 0; i2 < inlineBots.size(); i2++) {
            tL_topPeer = (TL_topPeer) inlineBots.get(i2);
            if (tL_topPeer.peer.user_id == i) {
                break;
            }
        }
        tL_topPeer = null;
        if (tL_topPeer == null) {
            tL_topPeer = new TL_topPeer();
            tL_topPeer.peer = new TL_peerUser();
            tL_topPeer.peer.user_id = i;
            inlineBots.add(tL_topPeer);
        }
        tL_topPeer.rating += Math.exp((double) (max / MessagesController.getInstance().ratingDecay));
        Collections.sort(inlineBots, new C07963());
        if (inlineBots.size() > 20) {
            inlineBots.remove(inlineBots.size() - 1);
        }
        savePeer(i, 1, tL_topPeer.rating);
        NotificationCenter.getInstance().postNotificationName(NotificationCenter.reloadInlineHints, new Object[0]);
    }

    public static void increasePeerRaiting(long j) {
        int i = (int) j;
        if (i > 0) {
            User user = i > 0 ? MessagesController.getInstance().getUser(Integer.valueOf(i)) : null;
            if (user != null && !user.bot) {
                MessagesStorage.getInstance().getStorageQueue().postRunnable(new C08016(j, i));
            }
        }
    }

    public static void loadHints(boolean z) {
        if (!loading) {
            if (!z) {
                loading = true;
                TLObject tL_contacts_getTopPeers = new TL_contacts_getTopPeers();
                tL_contacts_getTopPeers.hash = 0;
                tL_contacts_getTopPeers.bots_pm = false;
                tL_contacts_getTopPeers.correspondents = true;
                tL_contacts_getTopPeers.groups = false;
                tL_contacts_getTopPeers.channels = false;
                tL_contacts_getTopPeers.bots_inline = true;
                tL_contacts_getTopPeers.offset = 0;
                tL_contacts_getTopPeers.limit = 20;
                ConnectionsManager.getInstance().sendRequest(tL_contacts_getTopPeers, new C07952());
            } else if (!loaded) {
                loading = true;
                MessagesStorage.getInstance().getStorageQueue().postRunnable(new C07911());
                loaded = true;
            }
        }
    }

    public static void removeInline(int i) {
        for (int i2 = 0; i2 < inlineBots.size(); i2++) {
            if (((TL_topPeer) inlineBots.get(i2)).peer.user_id == i) {
                inlineBots.remove(i2);
                TLObject tL_contacts_resetTopPeerRating = new TL_contacts_resetTopPeerRating();
                tL_contacts_resetTopPeerRating.category = new TL_topPeerCategoryBotsInline();
                tL_contacts_resetTopPeerRating.peer = MessagesController.getInputPeer(i);
                ConnectionsManager.getInstance().sendRequest(tL_contacts_resetTopPeerRating, new C07974());
                deletePeer(i, 1);
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.reloadInlineHints, new Object[0]);
                return;
            }
        }
    }

    public static void removePeer(int i) {
        for (int i2 = 0; i2 < hints.size(); i2++) {
            if (((TL_topPeer) hints.get(i2)).peer.user_id == i) {
                hints.remove(i2);
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.reloadHints, new Object[0]);
                TLObject tL_contacts_resetTopPeerRating = new TL_contacts_resetTopPeerRating();
                tL_contacts_resetTopPeerRating.category = new TL_topPeerCategoryCorrespondents();
                tL_contacts_resetTopPeerRating.peer = MessagesController.getInputPeer(i);
                deletePeer(i, 0);
                ConnectionsManager.getInstance().sendRequest(tL_contacts_resetTopPeerRating, new C07985());
                return;
            }
        }
    }

    private static void savePeer(int i, int i2, double d) {
        MessagesStorage.getInstance().getStorageQueue().postRunnable(new C08027(i, i2, d));
    }
}
