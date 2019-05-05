package com.hanista.mobogram.messenger.query;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.widget.Toast;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.SQLite.SQLiteCursor;
import com.hanista.mobogram.SQLite.SQLiteDatabase;
import com.hanista.mobogram.SQLite.SQLitePreparedStatement;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.ApplicationLoader;
import com.hanista.mobogram.messenger.FileLog;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.MessagesController;
import com.hanista.mobogram.messenger.MessagesStorage;
import com.hanista.mobogram.messenger.NotificationCenter;
import com.hanista.mobogram.messenger.Utilities;
import com.hanista.mobogram.messenger.support.widget.helper.ItemTouchHelper.Callback;
import com.hanista.mobogram.tgnet.AbstractSerializedData;
import com.hanista.mobogram.tgnet.ConnectionsManager;
import com.hanista.mobogram.tgnet.NativeByteBuffer;
import com.hanista.mobogram.tgnet.RequestDelegate;
import com.hanista.mobogram.tgnet.TLObject;
import com.hanista.mobogram.tgnet.TLRPC.Document;
import com.hanista.mobogram.tgnet.TLRPC.DocumentAttribute;
import com.hanista.mobogram.tgnet.TLRPC.InputStickerSet;
import com.hanista.mobogram.tgnet.TLRPC.StickerSet;
import com.hanista.mobogram.tgnet.TLRPC.StickerSetCovered;
import com.hanista.mobogram.tgnet.TLRPC.TL_documentAttributeSticker;
import com.hanista.mobogram.tgnet.TLRPC.TL_documentEmpty;
import com.hanista.mobogram.tgnet.TLRPC.TL_error;
import com.hanista.mobogram.tgnet.TLRPC.TL_inputDocument;
import com.hanista.mobogram.tgnet.TLRPC.TL_inputStickerSetID;
import com.hanista.mobogram.tgnet.TLRPC.TL_messages_allStickers;
import com.hanista.mobogram.tgnet.TLRPC.TL_messages_featuredStickers;
import com.hanista.mobogram.tgnet.TLRPC.TL_messages_getAllStickers;
import com.hanista.mobogram.tgnet.TLRPC.TL_messages_getFeaturedStickers;
import com.hanista.mobogram.tgnet.TLRPC.TL_messages_getMaskStickers;
import com.hanista.mobogram.tgnet.TLRPC.TL_messages_getRecentStickers;
import com.hanista.mobogram.tgnet.TLRPC.TL_messages_getSavedGifs;
import com.hanista.mobogram.tgnet.TLRPC.TL_messages_getStickerSet;
import com.hanista.mobogram.tgnet.TLRPC.TL_messages_installStickerSet;
import com.hanista.mobogram.tgnet.TLRPC.TL_messages_readFeaturedStickers;
import com.hanista.mobogram.tgnet.TLRPC.TL_messages_recentStickers;
import com.hanista.mobogram.tgnet.TLRPC.TL_messages_saveGif;
import com.hanista.mobogram.tgnet.TLRPC.TL_messages_saveRecentSticker;
import com.hanista.mobogram.tgnet.TLRPC.TL_messages_savedGifs;
import com.hanista.mobogram.tgnet.TLRPC.TL_messages_stickerSet;
import com.hanista.mobogram.tgnet.TLRPC.TL_messages_stickerSetInstallResultArchive;
import com.hanista.mobogram.tgnet.TLRPC.TL_messages_uninstallStickerSet;
import com.hanista.mobogram.tgnet.TLRPC.TL_stickerPack;
import com.hanista.mobogram.ui.ActionBar.BaseFragment;
import com.hanista.mobogram.ui.Components.StickersArchiveAlert;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

public class StickersQuery {
    public static final int TYPE_IMAGE = 0;
    public static final int TYPE_MASK = 1;
    private static HashMap<String, ArrayList<Document>> allStickers;
    private static ArrayList<StickerSetCovered> featuredStickerSets;
    private static HashMap<Long, StickerSetCovered> featuredStickerSetsById;
    private static boolean featuredStickersLoaded;
    private static int[] loadDate;
    private static int loadFeaturedDate;
    private static int loadFeaturedHash;
    private static int[] loadHash;
    private static boolean loadingFeaturedStickers;
    private static boolean loadingRecentGifs;
    private static boolean[] loadingRecentStickers;
    private static boolean[] loadingStickers;
    private static ArrayList<Long> readingStickerSets;
    private static ArrayList<Document> recentGifs;
    private static boolean recentGifsLoaded;
    private static ArrayList<Document>[] recentStickers;
    private static boolean[] recentStickersLoaded;
    private static ArrayList<TL_messages_stickerSet>[] stickerSets;
    private static HashMap<Long, TL_messages_stickerSet> stickerSetsById;
    private static HashMap<String, TL_messages_stickerSet> stickerSetsByName;
    private static HashMap<Long, String> stickersByEmoji;
    private static HashMap<Long, Document> stickersById;
    private static boolean[] stickersLoaded;
    private static ArrayList<Long> unreadStickerSets;

    /* renamed from: com.hanista.mobogram.messenger.query.StickersQuery.10 */
    static class AnonymousClass10 implements Comparator<TL_messages_stickerSet> {
        final /* synthetic */ ArrayList val$order;

        AnonymousClass10(ArrayList arrayList) {
            this.val$order = arrayList;
        }

        public int compare(TL_messages_stickerSet tL_messages_stickerSet, TL_messages_stickerSet tL_messages_stickerSet2) {
            int indexOf = this.val$order.indexOf(Long.valueOf(tL_messages_stickerSet.set.id));
            int indexOf2 = this.val$order.indexOf(Long.valueOf(tL_messages_stickerSet2.set.id));
            return indexOf > indexOf2 ? StickersQuery.TYPE_MASK : indexOf < indexOf2 ? -1 : StickersQuery.TYPE_IMAGE;
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.query.StickersQuery.12 */
    static class AnonymousClass12 implements RequestDelegate {
        final /* synthetic */ TL_messages_getFeaturedStickers val$req;

        /* renamed from: com.hanista.mobogram.messenger.query.StickersQuery.12.1 */
        class C08151 implements Runnable {
            final /* synthetic */ TLObject val$response;

            C08151(TLObject tLObject) {
                this.val$response = tLObject;
            }

            public void run() {
                if (this.val$response instanceof TL_messages_featuredStickers) {
                    TL_messages_featuredStickers tL_messages_featuredStickers = (TL_messages_featuredStickers) this.val$response;
                    StickersQuery.processLoadedFeaturedStickers(tL_messages_featuredStickers.sets, tL_messages_featuredStickers.unread, false, (int) (System.currentTimeMillis() / 1000), tL_messages_featuredStickers.hash);
                    return;
                }
                StickersQuery.processLoadedFeaturedStickers(null, null, false, (int) (System.currentTimeMillis() / 1000), AnonymousClass12.this.val$req.hash);
            }
        }

        AnonymousClass12(TL_messages_getFeaturedStickers tL_messages_getFeaturedStickers) {
            this.val$req = tL_messages_getFeaturedStickers;
        }

        public void run(TLObject tLObject, TL_error tL_error) {
            AndroidUtilities.runOnUIThread(new C08151(tLObject));
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.query.StickersQuery.14 */
    static class AnonymousClass14 implements Runnable {
        final /* synthetic */ boolean val$cache;
        final /* synthetic */ int val$date;
        final /* synthetic */ int val$hash;
        final /* synthetic */ ArrayList val$res;
        final /* synthetic */ ArrayList val$unreadStickers;

        /* renamed from: com.hanista.mobogram.messenger.query.StickersQuery.14.1 */
        class C08161 implements Runnable {
            C08161() {
            }

            public void run() {
                if (!(AnonymousClass14.this.val$res == null || AnonymousClass14.this.val$hash == 0)) {
                    StickersQuery.loadFeaturedHash = AnonymousClass14.this.val$hash;
                }
                StickersQuery.loadFeaturesStickers(false, false);
            }
        }

        /* renamed from: com.hanista.mobogram.messenger.query.StickersQuery.14.2 */
        class C08172 implements Runnable {
            final /* synthetic */ HashMap val$stickerSetsByIdNew;
            final /* synthetic */ ArrayList val$stickerSetsNew;

            C08172(HashMap hashMap, ArrayList arrayList) {
                this.val$stickerSetsByIdNew = hashMap;
                this.val$stickerSetsNew = arrayList;
            }

            public void run() {
                StickersQuery.unreadStickerSets = AnonymousClass14.this.val$unreadStickers;
                StickersQuery.featuredStickerSetsById = this.val$stickerSetsByIdNew;
                StickersQuery.featuredStickerSets = this.val$stickerSetsNew;
                StickersQuery.loadFeaturedHash = AnonymousClass14.this.val$hash;
                StickersQuery.loadFeaturedDate = AnonymousClass14.this.val$date;
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.featuredStickersDidLoaded, new Object[StickersQuery.TYPE_IMAGE]);
            }
        }

        /* renamed from: com.hanista.mobogram.messenger.query.StickersQuery.14.3 */
        class C08183 implements Runnable {
            C08183() {
            }

            public void run() {
                StickersQuery.loadFeaturedDate = AnonymousClass14.this.val$date;
            }
        }

        AnonymousClass14(boolean z, ArrayList arrayList, int i, int i2, ArrayList arrayList2) {
            this.val$cache = z;
            this.val$res = arrayList;
            this.val$date = i;
            this.val$hash = i2;
            this.val$unreadStickers = arrayList2;
        }

        public void run() {
            long j = 1000;
            if ((this.val$cache && (this.val$res == null || Math.abs((System.currentTimeMillis() / 1000) - ((long) this.val$date)) >= 3600)) || (!this.val$cache && this.val$res == null && this.val$hash == 0)) {
                Runnable c08161 = new C08161();
                if (this.val$res != null || this.val$cache) {
                    j = 0;
                }
                AndroidUtilities.runOnUIThread(c08161, j);
                if (this.val$res == null) {
                    return;
                }
            }
            if (this.val$res != null) {
                try {
                    ArrayList arrayList = new ArrayList();
                    HashMap hashMap = new HashMap();
                    for (int i = StickersQuery.TYPE_IMAGE; i < this.val$res.size(); i += StickersQuery.TYPE_MASK) {
                        StickerSetCovered stickerSetCovered = (StickerSetCovered) this.val$res.get(i);
                        arrayList.add(stickerSetCovered);
                        hashMap.put(Long.valueOf(stickerSetCovered.set.id), stickerSetCovered);
                    }
                    if (!this.val$cache) {
                        StickersQuery.putFeaturedStickersToCache(arrayList, this.val$unreadStickers, this.val$date, this.val$hash);
                    }
                    AndroidUtilities.runOnUIThread(new C08172(hashMap, arrayList));
                } catch (Throwable th) {
                    FileLog.m18e("tmessages", th);
                }
            } else if (!this.val$cache) {
                AndroidUtilities.runOnUIThread(new C08183());
                StickersQuery.putFeaturedStickersToCache(null, null, this.val$date, StickersQuery.TYPE_IMAGE);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.query.StickersQuery.15 */
    static class AnonymousClass15 implements Runnable {
        final /* synthetic */ int val$date;
        final /* synthetic */ int val$hash;
        final /* synthetic */ ArrayList val$stickersFinal;
        final /* synthetic */ ArrayList val$unreadStickers;

        AnonymousClass15(ArrayList arrayList, ArrayList arrayList2, int i, int i2) {
            this.val$stickersFinal = arrayList;
            this.val$unreadStickers = arrayList2;
            this.val$date = i;
            this.val$hash = i2;
        }

        public void run() {
            int i = StickersQuery.TYPE_IMAGE;
            try {
                if (this.val$stickersFinal != null) {
                    int i2;
                    SQLitePreparedStatement executeFast = MessagesStorage.getInstance().getDatabase().executeFast("REPLACE INTO stickers_featured VALUES(?, ?, ?, ?, ?)");
                    executeFast.requery();
                    int i3 = 4;
                    for (i2 = StickersQuery.TYPE_IMAGE; i2 < this.val$stickersFinal.size(); i2 += StickersQuery.TYPE_MASK) {
                        i3 += ((StickerSetCovered) this.val$stickersFinal.get(i2)).getObjectSize();
                    }
                    NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(i3);
                    NativeByteBuffer nativeByteBuffer2 = new NativeByteBuffer((this.val$unreadStickers.size() * 8) + 4);
                    nativeByteBuffer.writeInt32(this.val$stickersFinal.size());
                    for (i2 = StickersQuery.TYPE_IMAGE; i2 < this.val$stickersFinal.size(); i2 += StickersQuery.TYPE_MASK) {
                        ((StickerSetCovered) this.val$stickersFinal.get(i2)).serializeToStream(nativeByteBuffer);
                    }
                    nativeByteBuffer2.writeInt32(this.val$unreadStickers.size());
                    while (i < this.val$unreadStickers.size()) {
                        nativeByteBuffer2.writeInt64(((Long) this.val$unreadStickers.get(i)).longValue());
                        i += StickersQuery.TYPE_MASK;
                    }
                    executeFast.bindInteger(StickersQuery.TYPE_MASK, StickersQuery.TYPE_MASK);
                    executeFast.bindByteBuffer(2, nativeByteBuffer);
                    executeFast.bindByteBuffer(3, nativeByteBuffer2);
                    executeFast.bindInteger(4, this.val$date);
                    executeFast.bindInteger(5, this.val$hash);
                    executeFast.step();
                    nativeByteBuffer.reuse();
                    nativeByteBuffer2.reuse();
                    executeFast.dispose();
                    return;
                }
                SQLitePreparedStatement executeFast2 = MessagesStorage.getInstance().getDatabase().executeFast("UPDATE stickers_featured SET date = ?");
                executeFast2.requery();
                executeFast2.bindInteger(StickersQuery.TYPE_MASK, this.val$date);
                executeFast2.step();
                executeFast2.dispose();
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.query.StickersQuery.18 */
    static class AnonymousClass18 implements Runnable {
        final /* synthetic */ long val$id;

        AnonymousClass18(long j) {
            this.val$id = j;
        }

        public void run() {
            StickersQuery.unreadStickerSets.remove(Long.valueOf(this.val$id));
            StickersQuery.readingStickerSets.remove(Long.valueOf(this.val$id));
            StickersQuery.loadFeaturedHash = StickersQuery.calcFeaturedStickersHash(StickersQuery.featuredStickerSets);
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.featuredStickersDidLoaded, new Object[StickersQuery.TYPE_IMAGE]);
            StickersQuery.putFeaturedStickersToCache(StickersQuery.featuredStickerSets, StickersQuery.unreadStickerSets, StickersQuery.loadFeaturedDate, StickersQuery.loadFeaturedHash);
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.query.StickersQuery.19 */
    static class AnonymousClass19 implements Runnable {
        final /* synthetic */ int val$type;

        AnonymousClass19(int i) {
            this.val$type = i;
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void run() {
            /*
            r10 = this;
            r2 = 0;
            r8 = 1;
            r0 = 0;
            r1 = com.hanista.mobogram.messenger.MessagesStorage.getInstance();	 Catch:{ Throwable -> 0x006d, all -> 0x007d }
            r1 = r1.getDatabase();	 Catch:{ Throwable -> 0x006d, all -> 0x007d }
            r3 = new java.lang.StringBuilder;	 Catch:{ Throwable -> 0x006d, all -> 0x007d }
            r3.<init>();	 Catch:{ Throwable -> 0x006d, all -> 0x007d }
            r4 = "SELECT data, date, hash FROM stickers_v2 WHERE id = ";
            r3 = r3.append(r4);	 Catch:{ Throwable -> 0x006d, all -> 0x007d }
            r4 = r10.val$type;	 Catch:{ Throwable -> 0x006d, all -> 0x007d }
            r4 = r4 + 1;
            r3 = r3.append(r4);	 Catch:{ Throwable -> 0x006d, all -> 0x007d }
            r3 = r3.toString();	 Catch:{ Throwable -> 0x006d, all -> 0x007d }
            r4 = 0;
            r4 = new java.lang.Object[r4];	 Catch:{ Throwable -> 0x006d, all -> 0x007d }
            r3 = r1.queryFinalized(r3, r4);	 Catch:{ Throwable -> 0x006d, all -> 0x007d }
            r1 = r3.next();	 Catch:{ Throwable -> 0x008a, all -> 0x0085 }
            if (r1 == 0) goto L_0x009e;
        L_0x0030:
            r1 = 0;
            r5 = r3.byteBufferValue(r1);	 Catch:{ Throwable -> 0x008a, all -> 0x0085 }
            if (r5 == 0) goto L_0x009c;
        L_0x0037:
            r4 = new java.util.ArrayList;	 Catch:{ Throwable -> 0x008a, all -> 0x0085 }
            r4.<init>();	 Catch:{ Throwable -> 0x008a, all -> 0x0085 }
            r1 = 0;
            r2 = r5.readInt32(r1);	 Catch:{ Throwable -> 0x008f, all -> 0x0085 }
            r1 = r0;
        L_0x0042:
            if (r1 >= r2) goto L_0x0054;
        L_0x0044:
            r6 = 0;
            r6 = r5.readInt32(r6);	 Catch:{ Throwable -> 0x008f, all -> 0x0085 }
            r7 = 0;
            r6 = com.hanista.mobogram.tgnet.TLRPC.TL_messages_stickerSet.TLdeserialize(r5, r6, r7);	 Catch:{ Throwable -> 0x008f, all -> 0x0085 }
            r4.add(r6);	 Catch:{ Throwable -> 0x008f, all -> 0x0085 }
            r1 = r1 + 1;
            goto L_0x0042;
        L_0x0054:
            r5.reuse();	 Catch:{ Throwable -> 0x008f, all -> 0x0085 }
        L_0x0057:
            r1 = 1;
            r2 = r3.intValue(r1);	 Catch:{ Throwable -> 0x008f, all -> 0x0085 }
            r0 = com.hanista.mobogram.messenger.query.StickersQuery.calcStickersHash(r4);	 Catch:{ Throwable -> 0x0095, all -> 0x0085 }
            r1 = r2;
            r2 = r4;
        L_0x0062:
            if (r3 == 0) goto L_0x0067;
        L_0x0064:
            r3.dispose();
        L_0x0067:
            r3 = r10.val$type;
            com.hanista.mobogram.messenger.query.StickersQuery.processLoadedStickers(r3, r2, r8, r1, r0);
            return;
        L_0x006d:
            r1 = move-exception;
            r3 = r1;
            r4 = r2;
            r1 = r0;
        L_0x0071:
            r5 = "tmessages";
            com.hanista.mobogram.messenger.FileLog.m18e(r5, r3);	 Catch:{ all -> 0x0087 }
            if (r4 == 0) goto L_0x0067;
        L_0x0079:
            r4.dispose();
            goto L_0x0067;
        L_0x007d:
            r0 = move-exception;
            r3 = r2;
        L_0x007f:
            if (r3 == 0) goto L_0x0084;
        L_0x0081:
            r3.dispose();
        L_0x0084:
            throw r0;
        L_0x0085:
            r0 = move-exception;
            goto L_0x007f;
        L_0x0087:
            r0 = move-exception;
            r3 = r4;
            goto L_0x007f;
        L_0x008a:
            r1 = move-exception;
            r4 = r3;
            r3 = r1;
            r1 = r0;
            goto L_0x0071;
        L_0x008f:
            r1 = move-exception;
            r2 = r4;
            r4 = r3;
            r3 = r1;
            r1 = r0;
            goto L_0x0071;
        L_0x0095:
            r1 = move-exception;
            r9 = r1;
            r1 = r2;
            r2 = r4;
            r4 = r3;
            r3 = r9;
            goto L_0x0071;
        L_0x009c:
            r4 = r2;
            goto L_0x0057;
        L_0x009e:
            r1 = r0;
            goto L_0x0062;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.hanista.mobogram.messenger.query.StickersQuery.19.run():void");
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.query.StickersQuery.1 */
    static class C08191 implements Runnable {
        final /* synthetic */ Document val$old;

        C08191(Document document) {
            this.val$old = document;
        }

        public void run() {
            try {
                MessagesStorage.getInstance().getDatabase().executeFast("DELETE FROM web_recent_v3 WHERE id = '" + this.val$old.id + "'").stepThis().dispose();
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.query.StickersQuery.20 */
    static class AnonymousClass20 implements RequestDelegate {
        final /* synthetic */ int val$hash;
        final /* synthetic */ int val$type;

        /* renamed from: com.hanista.mobogram.messenger.query.StickersQuery.20.1 */
        class C08221 implements Runnable {
            final /* synthetic */ TLObject val$response;

            /* renamed from: com.hanista.mobogram.messenger.query.StickersQuery.20.1.1 */
            class C08211 implements RequestDelegate {
                final /* synthetic */ int val$index;
                final /* synthetic */ ArrayList val$newStickerArray;
                final /* synthetic */ HashMap val$newStickerSets;
                final /* synthetic */ TL_messages_allStickers val$res;
                final /* synthetic */ StickerSet val$stickerSet;

                /* renamed from: com.hanista.mobogram.messenger.query.StickersQuery.20.1.1.1 */
                class C08201 implements Runnable {
                    final /* synthetic */ TLObject val$response;

                    C08201(TLObject tLObject) {
                        this.val$response = tLObject;
                    }

                    public void run() {
                        TL_messages_stickerSet tL_messages_stickerSet = (TL_messages_stickerSet) this.val$response;
                        C08211.this.val$newStickerArray.set(C08211.this.val$index, tL_messages_stickerSet);
                        C08211.this.val$newStickerSets.put(Long.valueOf(C08211.this.val$stickerSet.id), tL_messages_stickerSet);
                        if (C08211.this.val$newStickerSets.size() == C08211.this.val$res.sets.size()) {
                            for (int i = StickersQuery.TYPE_IMAGE; i < C08211.this.val$newStickerArray.size(); i += StickersQuery.TYPE_MASK) {
                                if (C08211.this.val$newStickerArray.get(i) == null) {
                                    C08211.this.val$newStickerArray.remove(i);
                                }
                            }
                            StickersQuery.processLoadedStickers(AnonymousClass20.this.val$type, C08211.this.val$newStickerArray, false, (int) (System.currentTimeMillis() / 1000), C08211.this.val$res.hash);
                        }
                    }
                }

                C08211(ArrayList arrayList, int i, HashMap hashMap, StickerSet stickerSet, TL_messages_allStickers tL_messages_allStickers) {
                    this.val$newStickerArray = arrayList;
                    this.val$index = i;
                    this.val$newStickerSets = hashMap;
                    this.val$stickerSet = stickerSet;
                    this.val$res = tL_messages_allStickers;
                }

                public void run(TLObject tLObject, TL_error tL_error) {
                    AndroidUtilities.runOnUIThread(new C08201(tLObject));
                }
            }

            C08221(TLObject tLObject) {
                this.val$response = tLObject;
            }

            public void run() {
                if (this.val$response instanceof TL_messages_allStickers) {
                    TL_messages_allStickers tL_messages_allStickers = (TL_messages_allStickers) this.val$response;
                    ArrayList arrayList = new ArrayList();
                    if (tL_messages_allStickers.sets.isEmpty()) {
                        StickersQuery.processLoadedStickers(AnonymousClass20.this.val$type, arrayList, false, (int) (System.currentTimeMillis() / 1000), tL_messages_allStickers.hash);
                        return;
                    }
                    HashMap hashMap = new HashMap();
                    for (int i = StickersQuery.TYPE_IMAGE; i < tL_messages_allStickers.sets.size(); i += StickersQuery.TYPE_MASK) {
                        StickerSet stickerSet = (StickerSet) tL_messages_allStickers.sets.get(i);
                        TL_messages_stickerSet tL_messages_stickerSet = (TL_messages_stickerSet) StickersQuery.stickerSetsById.get(Long.valueOf(stickerSet.id));
                        if (tL_messages_stickerSet == null || tL_messages_stickerSet.set.hash != stickerSet.hash) {
                            arrayList.add(null);
                            TLObject tL_messages_getStickerSet = new TL_messages_getStickerSet();
                            tL_messages_getStickerSet.stickerset = new TL_inputStickerSetID();
                            tL_messages_getStickerSet.stickerset.id = stickerSet.id;
                            tL_messages_getStickerSet.stickerset.access_hash = stickerSet.access_hash;
                            ConnectionsManager.getInstance().sendRequest(tL_messages_getStickerSet, new C08211(arrayList, i, hashMap, stickerSet, tL_messages_allStickers));
                        } else {
                            tL_messages_stickerSet.set.archived = stickerSet.archived;
                            tL_messages_stickerSet.set.installed = stickerSet.installed;
                            tL_messages_stickerSet.set.official = stickerSet.official;
                            hashMap.put(Long.valueOf(tL_messages_stickerSet.set.id), tL_messages_stickerSet);
                            arrayList.add(tL_messages_stickerSet);
                            if (hashMap.size() == tL_messages_allStickers.sets.size()) {
                                StickersQuery.processLoadedStickers(AnonymousClass20.this.val$type, arrayList, false, (int) (System.currentTimeMillis() / 1000), tL_messages_allStickers.hash);
                            }
                        }
                    }
                    return;
                }
                StickersQuery.processLoadedStickers(AnonymousClass20.this.val$type, null, false, (int) (System.currentTimeMillis() / 1000), AnonymousClass20.this.val$hash);
            }
        }

        AnonymousClass20(int i, int i2) {
            this.val$type = i;
            this.val$hash = i2;
        }

        public void run(TLObject tLObject, TL_error tL_error) {
            AndroidUtilities.runOnUIThread(new C08221(tLObject));
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.query.StickersQuery.21 */
    static class AnonymousClass21 implements Runnable {
        final /* synthetic */ int val$date;
        final /* synthetic */ int val$hash;
        final /* synthetic */ ArrayList val$stickersFinal;
        final /* synthetic */ int val$type;

        AnonymousClass21(ArrayList arrayList, int i, int i2, int i3) {
            this.val$stickersFinal = arrayList;
            this.val$type = i;
            this.val$date = i2;
            this.val$hash = i3;
        }

        public void run() {
            int i = StickersQuery.TYPE_IMAGE;
            try {
                if (this.val$stickersFinal != null) {
                    SQLitePreparedStatement executeFast = MessagesStorage.getInstance().getDatabase().executeFast("REPLACE INTO stickers_v2 VALUES(?, ?, ?, ?)");
                    executeFast.requery();
                    int i2 = 4;
                    for (int i3 = StickersQuery.TYPE_IMAGE; i3 < this.val$stickersFinal.size(); i3 += StickersQuery.TYPE_MASK) {
                        i2 += ((TL_messages_stickerSet) this.val$stickersFinal.get(i3)).getObjectSize();
                    }
                    NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(i2);
                    nativeByteBuffer.writeInt32(this.val$stickersFinal.size());
                    while (i < this.val$stickersFinal.size()) {
                        ((TL_messages_stickerSet) this.val$stickersFinal.get(i)).serializeToStream(nativeByteBuffer);
                        i += StickersQuery.TYPE_MASK;
                    }
                    executeFast.bindInteger(StickersQuery.TYPE_MASK, this.val$type == 0 ? StickersQuery.TYPE_MASK : 2);
                    executeFast.bindByteBuffer(2, nativeByteBuffer);
                    executeFast.bindInteger(3, this.val$date);
                    executeFast.bindInteger(4, this.val$hash);
                    executeFast.step();
                    nativeByteBuffer.reuse();
                    executeFast.dispose();
                    return;
                }
                SQLitePreparedStatement executeFast2 = MessagesStorage.getInstance().getDatabase().executeFast("UPDATE stickers_v2 SET date = ?");
                executeFast2.requery();
                executeFast2.bindInteger(StickersQuery.TYPE_MASK, this.val$date);
                executeFast2.step();
                executeFast2.dispose();
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.query.StickersQuery.22 */
    static class AnonymousClass22 implements Runnable {
        final /* synthetic */ int val$type;

        AnonymousClass22(int i) {
            this.val$type = i;
        }

        public void run() {
            StickersQuery.loadingStickers[this.val$type] = false;
            StickersQuery.stickersLoaded[this.val$type] = true;
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.query.StickersQuery.23 */
    static class AnonymousClass23 implements Runnable {
        final /* synthetic */ boolean val$cache;
        final /* synthetic */ int val$date;
        final /* synthetic */ int val$hash;
        final /* synthetic */ ArrayList val$res;
        final /* synthetic */ int val$type;

        /* renamed from: com.hanista.mobogram.messenger.query.StickersQuery.23.1 */
        class C08231 implements Runnable {
            C08231() {
            }

            public void run() {
                if (!(AnonymousClass23.this.val$res == null || AnonymousClass23.this.val$hash == 0)) {
                    StickersQuery.loadHash[AnonymousClass23.this.val$type] = AnonymousClass23.this.val$hash;
                }
                StickersQuery.loadStickers(AnonymousClass23.this.val$type, false, false);
            }
        }

        /* renamed from: com.hanista.mobogram.messenger.query.StickersQuery.23.2 */
        class C08242 implements Runnable {
            final /* synthetic */ HashMap val$allStickersNew;
            final /* synthetic */ HashMap val$stickerSetsByIdNew;
            final /* synthetic */ HashMap val$stickerSetsByNameNew;
            final /* synthetic */ ArrayList val$stickerSetsNew;
            final /* synthetic */ HashMap val$stickersByEmojiNew;

            C08242(HashMap hashMap, HashMap hashMap2, ArrayList arrayList, HashMap hashMap3, HashMap hashMap4) {
                this.val$stickerSetsByIdNew = hashMap;
                this.val$stickerSetsByNameNew = hashMap2;
                this.val$stickerSetsNew = arrayList;
                this.val$allStickersNew = hashMap3;
                this.val$stickersByEmojiNew = hashMap4;
            }

            public void run() {
                int i;
                for (i = StickersQuery.TYPE_IMAGE; i < StickersQuery.stickerSets[AnonymousClass23.this.val$type].size(); i += StickersQuery.TYPE_MASK) {
                    StickerSet stickerSet = ((TL_messages_stickerSet) StickersQuery.stickerSets[AnonymousClass23.this.val$type].get(i)).set;
                    StickersQuery.stickerSetsById.remove(Long.valueOf(stickerSet.id));
                    StickersQuery.stickerSetsByName.remove(stickerSet.short_name);
                }
                StickersQuery.stickerSetsById.putAll(this.val$stickerSetsByIdNew);
                StickersQuery.stickerSetsByName.putAll(this.val$stickerSetsByNameNew);
                StickersQuery.stickerSets[AnonymousClass23.this.val$type] = this.val$stickerSetsNew;
                StickersQuery.loadHash[AnonymousClass23.this.val$type] = AnonymousClass23.this.val$hash;
                StickersQuery.loadDate[AnonymousClass23.this.val$type] = AnonymousClass23.this.val$date;
                if (AnonymousClass23.this.val$type == 0) {
                    StickersQuery.allStickers = this.val$allStickersNew;
                    StickersQuery.stickersByEmoji = this.val$stickersByEmojiNew;
                }
                NotificationCenter instance = NotificationCenter.getInstance();
                i = NotificationCenter.stickersDidLoaded;
                Object[] objArr = new Object[StickersQuery.TYPE_MASK];
                objArr[StickersQuery.TYPE_IMAGE] = Integer.valueOf(AnonymousClass23.this.val$type);
                instance.postNotificationName(i, objArr);
            }
        }

        /* renamed from: com.hanista.mobogram.messenger.query.StickersQuery.23.3 */
        class C08253 implements Runnable {
            C08253() {
            }

            public void run() {
                StickersQuery.loadDate[AnonymousClass23.this.val$type] = AnonymousClass23.this.val$date;
            }
        }

        AnonymousClass23(boolean z, ArrayList arrayList, int i, int i2, int i3) {
            this.val$cache = z;
            this.val$res = arrayList;
            this.val$date = i;
            this.val$hash = i2;
            this.val$type = i3;
        }

        public void run() {
            long j = 1000;
            if ((this.val$cache && (this.val$res == null || Math.abs((System.currentTimeMillis() / 1000) - ((long) this.val$date)) >= 3600)) || (!this.val$cache && this.val$res == null && this.val$hash == 0)) {
                Runnable c08231 = new C08231();
                if (this.val$res != null || this.val$cache) {
                    j = 0;
                }
                AndroidUtilities.runOnUIThread(c08231, j);
                if (this.val$res == null) {
                    return;
                }
            }
            if (this.val$res != null) {
                try {
                    ArrayList arrayList = new ArrayList();
                    HashMap hashMap = new HashMap();
                    HashMap hashMap2 = new HashMap();
                    HashMap hashMap3 = new HashMap();
                    HashMap hashMap4 = new HashMap();
                    HashMap hashMap5 = new HashMap();
                    for (int i = StickersQuery.TYPE_IMAGE; i < this.val$res.size(); i += StickersQuery.TYPE_MASK) {
                        TL_messages_stickerSet tL_messages_stickerSet = (TL_messages_stickerSet) this.val$res.get(i);
                        if (tL_messages_stickerSet != null) {
                            arrayList.add(tL_messages_stickerSet);
                            hashMap.put(Long.valueOf(tL_messages_stickerSet.set.id), tL_messages_stickerSet);
                            hashMap2.put(tL_messages_stickerSet.set.short_name, tL_messages_stickerSet);
                            for (int i2 = StickersQuery.TYPE_IMAGE; i2 < tL_messages_stickerSet.documents.size(); i2 += StickersQuery.TYPE_MASK) {
                                Document document = (Document) tL_messages_stickerSet.documents.get(i2);
                                if (!(document == null || (document instanceof TL_documentEmpty))) {
                                    hashMap4.put(Long.valueOf(document.id), document);
                                }
                            }
                            if (!tL_messages_stickerSet.set.archived) {
                                for (int i3 = StickersQuery.TYPE_IMAGE; i3 < tL_messages_stickerSet.packs.size(); i3 += StickersQuery.TYPE_MASK) {
                                    TL_stickerPack tL_stickerPack = (TL_stickerPack) tL_messages_stickerSet.packs.get(i3);
                                    if (!(tL_stickerPack == null || tL_stickerPack.emoticon == null)) {
                                        ArrayList arrayList2;
                                        tL_stickerPack.emoticon = tL_stickerPack.emoticon.replace("\ufe0f", TtmlNode.ANONYMOUS_REGION_ID);
                                        ArrayList arrayList3 = (ArrayList) hashMap5.get(tL_stickerPack.emoticon);
                                        if (arrayList3 == null) {
                                            arrayList3 = new ArrayList();
                                            hashMap5.put(tL_stickerPack.emoticon, arrayList3);
                                            arrayList2 = arrayList3;
                                        } else {
                                            arrayList2 = arrayList3;
                                        }
                                        for (int i4 = StickersQuery.TYPE_IMAGE; i4 < tL_stickerPack.documents.size(); i4 += StickersQuery.TYPE_MASK) {
                                            Long l = (Long) tL_stickerPack.documents.get(i4);
                                            if (!hashMap3.containsKey(l)) {
                                                hashMap3.put(l, tL_stickerPack.emoticon);
                                            }
                                            Document document2 = (Document) hashMap4.get(l);
                                            if (document2 != null) {
                                                arrayList2.add(document2);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (!this.val$cache) {
                        StickersQuery.putStickersToCache(this.val$type, arrayList, this.val$date, this.val$hash);
                    }
                    AndroidUtilities.runOnUIThread(new C08242(hashMap, hashMap2, arrayList, hashMap5, hashMap3));
                } catch (Throwable th) {
                    FileLog.m18e("tmessages", th);
                }
            } else if (!this.val$cache) {
                AndroidUtilities.runOnUIThread(new C08253());
                StickersQuery.putStickersToCache(this.val$type, null, this.val$date, StickersQuery.TYPE_IMAGE);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.query.StickersQuery.24 */
    static class AnonymousClass24 implements RequestDelegate {
        final /* synthetic */ BaseFragment val$baseFragment;
        final /* synthetic */ int val$hide;
        final /* synthetic */ boolean val$showSettings;
        final /* synthetic */ int val$type;

        /* renamed from: com.hanista.mobogram.messenger.query.StickersQuery.24.1 */
        class C08261 implements Runnable {
            final /* synthetic */ TLObject val$response;

            C08261(TLObject tLObject) {
                this.val$response = tLObject;
            }

            public void run() {
                if (this.val$response instanceof TL_messages_stickerSetInstallResultArchive) {
                    NotificationCenter instance = NotificationCenter.getInstance();
                    int i = NotificationCenter.needReloadArchivedStickers;
                    Object[] objArr = new Object[StickersQuery.TYPE_MASK];
                    objArr[StickersQuery.TYPE_IMAGE] = Integer.valueOf(AnonymousClass24.this.val$type);
                    instance.postNotificationName(i, objArr);
                    if (AnonymousClass24.this.val$hide != StickersQuery.TYPE_MASK && AnonymousClass24.this.val$baseFragment != null && AnonymousClass24.this.val$baseFragment.getParentActivity() != null) {
                        AnonymousClass24.this.val$baseFragment.showDialog(new StickersArchiveAlert(AnonymousClass24.this.val$baseFragment.getParentActivity(), AnonymousClass24.this.val$showSettings ? AnonymousClass24.this.val$baseFragment : null, ((TL_messages_stickerSetInstallResultArchive) this.val$response).sets).create());
                    }
                }
            }
        }

        /* renamed from: com.hanista.mobogram.messenger.query.StickersQuery.24.2 */
        class C08272 implements Runnable {
            C08272() {
            }

            public void run() {
                StickersQuery.loadStickers(AnonymousClass24.this.val$type, false, false);
            }
        }

        AnonymousClass24(int i, int i2, BaseFragment baseFragment, boolean z) {
            this.val$type = i;
            this.val$hide = i2;
            this.val$baseFragment = baseFragment;
            this.val$showSettings = z;
        }

        public void run(TLObject tLObject, TL_error tL_error) {
            AndroidUtilities.runOnUIThread(new C08261(tLObject));
            AndroidUtilities.runOnUIThread(new C08272(), 1000);
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.query.StickersQuery.25 */
    static class AnonymousClass25 implements RequestDelegate {
        final /* synthetic */ Context val$context;
        final /* synthetic */ StickerSet val$stickerSet;
        final /* synthetic */ int val$type;

        /* renamed from: com.hanista.mobogram.messenger.query.StickersQuery.25.1 */
        class C08281 implements Runnable {
            final /* synthetic */ TL_error val$error;

            C08281(TL_error tL_error) {
                this.val$error = tL_error;
            }

            public void run() {
                try {
                    if (this.val$error == null) {
                        if (AnonymousClass25.this.val$stickerSet.masks) {
                            Toast.makeText(AnonymousClass25.this.val$context, LocaleController.getString("MasksRemoved", C0338R.string.MasksRemoved), StickersQuery.TYPE_IMAGE).show();
                        } else {
                            Toast.makeText(AnonymousClass25.this.val$context, LocaleController.getString("StickersRemoved", C0338R.string.StickersRemoved), StickersQuery.TYPE_IMAGE).show();
                        }
                        StickersQuery.loadStickers(AnonymousClass25.this.val$type, false, true);
                    }
                    Toast.makeText(AnonymousClass25.this.val$context, LocaleController.getString("ErrorOccurred", C0338R.string.ErrorOccurred), StickersQuery.TYPE_IMAGE).show();
                    StickersQuery.loadStickers(AnonymousClass25.this.val$type, false, true);
                } catch (Throwable e) {
                    FileLog.m18e("tmessages", e);
                }
            }
        }

        AnonymousClass25(StickerSet stickerSet, Context context, int i) {
            this.val$stickerSet = stickerSet;
            this.val$context = context;
            this.val$type = i;
        }

        public void run(TLObject tLObject, TL_error tL_error) {
            AndroidUtilities.runOnUIThread(new C08281(tL_error));
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.query.StickersQuery.27 */
    static class AnonymousClass27 implements Runnable {
        final /* synthetic */ Document val$document;

        AnonymousClass27(Document document) {
            this.val$document = document;
        }

        public void run() {
            try {
                MessagesStorage.getInstance().getDatabase().executeFast("DELETE FROM web_recent_v3 WHERE id = '" + this.val$document.id + "'").stepThis().dispose();
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.query.StickersQuery.2 */
    static class C08292 implements RequestDelegate {
        C08292() {
        }

        public void run(TLObject tLObject, TL_error tL_error) {
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.query.StickersQuery.3 */
    static class C08303 implements Runnable {
        final /* synthetic */ Document val$document;

        C08303(Document document) {
            this.val$document = document;
        }

        public void run() {
            try {
                MessagesStorage.getInstance().getDatabase().executeFast("DELETE FROM web_recent_v3 WHERE id = '" + this.val$document.id + "'").stepThis().dispose();
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.query.StickersQuery.4 */
    static class C08314 implements Runnable {
        final /* synthetic */ Document val$old;

        C08314(Document document) {
            this.val$old = document;
        }

        public void run() {
            try {
                MessagesStorage.getInstance().getDatabase().executeFast("DELETE FROM web_recent_v3 WHERE id = '" + this.val$old.id + "'").stepThis().dispose();
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.query.StickersQuery.5 */
    static class C08335 implements Runnable {
        final /* synthetic */ boolean val$gif;
        final /* synthetic */ int val$type;

        /* renamed from: com.hanista.mobogram.messenger.query.StickersQuery.5.1 */
        class C08321 implements Runnable {
            final /* synthetic */ ArrayList val$arrayList;

            C08321(ArrayList arrayList) {
                this.val$arrayList = arrayList;
            }

            public void run() {
                if (C08335.this.val$gif) {
                    StickersQuery.recentGifs = this.val$arrayList;
                    StickersQuery.loadingRecentGifs = false;
                    StickersQuery.recentGifsLoaded = true;
                } else {
                    StickersQuery.recentStickers[C08335.this.val$type] = this.val$arrayList;
                    StickersQuery.loadingRecentStickers[C08335.this.val$type] = false;
                    StickersQuery.recentStickersLoaded[C08335.this.val$type] = true;
                }
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.recentDocumentsDidLoaded, Boolean.valueOf(C08335.this.val$gif), Integer.valueOf(C08335.this.val$type));
                StickersQuery.loadRecents(C08335.this.val$type, C08335.this.val$gif, false);
            }
        }

        C08335(boolean z, int i) {
            this.val$gif = z;
            this.val$type = i;
        }

        public void run() {
            try {
                SQLiteDatabase database = MessagesStorage.getInstance().getDatabase();
                StringBuilder append = new StringBuilder().append("SELECT document FROM web_recent_v3 WHERE type = ");
                int i = this.val$gif ? 2 : this.val$type == 0 ? 3 : 4;
                SQLiteCursor queryFinalized = database.queryFinalized(append.append(i).append(" ORDER BY date DESC").toString(), new Object[StickersQuery.TYPE_IMAGE]);
                ArrayList arrayList = new ArrayList();
                while (queryFinalized.next()) {
                    if (!queryFinalized.isNull(StickersQuery.TYPE_IMAGE)) {
                        AbstractSerializedData byteBufferValue = queryFinalized.byteBufferValue(StickersQuery.TYPE_IMAGE);
                        if (byteBufferValue != null) {
                            Document TLdeserialize = Document.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                            if (TLdeserialize != null) {
                                arrayList.add(TLdeserialize);
                            }
                            byteBufferValue.reuse();
                        }
                    }
                }
                queryFinalized.dispose();
                AndroidUtilities.runOnUIThread(new C08321(arrayList));
            } catch (Throwable th) {
                FileLog.m18e("tmessages", th);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.query.StickersQuery.6 */
    static class C08346 implements RequestDelegate {
        final /* synthetic */ boolean val$gif;
        final /* synthetic */ int val$type;

        C08346(int i, boolean z) {
            this.val$type = i;
            this.val$gif = z;
        }

        public void run(TLObject tLObject, TL_error tL_error) {
            ArrayList arrayList = null;
            if (tLObject instanceof TL_messages_savedGifs) {
                arrayList = ((TL_messages_savedGifs) tLObject).gifs;
            }
            StickersQuery.processLoadedRecentDocuments(this.val$type, arrayList, this.val$gif, StickersQuery.TYPE_IMAGE);
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.query.StickersQuery.7 */
    static class C08357 implements RequestDelegate {
        final /* synthetic */ boolean val$gif;
        final /* synthetic */ int val$type;

        C08357(int i, boolean z) {
            this.val$type = i;
            this.val$gif = z;
        }

        public void run(TLObject tLObject, TL_error tL_error) {
            ArrayList arrayList = null;
            if (tLObject instanceof TL_messages_recentStickers) {
                arrayList = ((TL_messages_recentStickers) tLObject).stickers;
            }
            StickersQuery.processLoadedRecentDocuments(this.val$type, arrayList, this.val$gif, StickersQuery.TYPE_IMAGE);
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.query.StickersQuery.8 */
    static class C08368 implements Runnable {
        final /* synthetic */ int val$date;
        final /* synthetic */ ArrayList val$documents;
        final /* synthetic */ boolean val$gif;
        final /* synthetic */ int val$type;

        C08368(boolean z, ArrayList arrayList, int i, int i2) {
            this.val$gif = z;
            this.val$documents = arrayList;
            this.val$type = i;
            this.val$date = i2;
        }

        public void run() {
            try {
                SQLiteDatabase database = MessagesStorage.getInstance().getDatabase();
                int i = this.val$gif ? MessagesController.getInstance().maxRecentGifsCount : MessagesController.getInstance().maxRecentStickersCount;
                database.beginTransaction();
                SQLitePreparedStatement executeFast = database.executeFast("REPLACE INTO web_recent_v3 VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                int size = this.val$documents.size();
                int i2 = StickersQuery.TYPE_IMAGE;
                while (i2 < size && i2 != i) {
                    Document document = (Document) this.val$documents.get(i2);
                    executeFast.requery();
                    executeFast.bindString(StickersQuery.TYPE_MASK, TtmlNode.ANONYMOUS_REGION_ID + document.id);
                    int i3 = this.val$gif ? 2 : this.val$type == 0 ? 3 : 4;
                    executeFast.bindInteger(2, i3);
                    executeFast.bindString(3, TtmlNode.ANONYMOUS_REGION_ID);
                    executeFast.bindString(4, TtmlNode.ANONYMOUS_REGION_ID);
                    executeFast.bindString(5, TtmlNode.ANONYMOUS_REGION_ID);
                    executeFast.bindInteger(6, StickersQuery.TYPE_IMAGE);
                    executeFast.bindInteger(7, StickersQuery.TYPE_IMAGE);
                    executeFast.bindInteger(8, StickersQuery.TYPE_IMAGE);
                    executeFast.bindInteger(9, this.val$date != 0 ? this.val$date : size - i2);
                    NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(document.getObjectSize());
                    document.serializeToStream(nativeByteBuffer);
                    executeFast.bindByteBuffer(10, nativeByteBuffer);
                    executeFast.step();
                    if (nativeByteBuffer != null) {
                        nativeByteBuffer.reuse();
                    }
                    i2 += StickersQuery.TYPE_MASK;
                }
                executeFast.dispose();
                database.commitTransaction();
                if (this.val$documents.size() >= i) {
                    database.beginTransaction();
                    while (i < this.val$documents.size()) {
                        database.executeFast("DELETE FROM web_recent_v3 WHERE id = '" + ((Document) this.val$documents.get(i)).id + "'").stepThis().dispose();
                        i += StickersQuery.TYPE_MASK;
                    }
                    database.commitTransaction();
                }
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.query.StickersQuery.9 */
    static class C08379 implements Runnable {
        final /* synthetic */ ArrayList val$documents;
        final /* synthetic */ boolean val$gif;
        final /* synthetic */ int val$type;

        C08379(boolean z, int i, ArrayList arrayList) {
            this.val$gif = z;
            this.val$type = i;
            this.val$documents = arrayList;
        }

        public void run() {
            Editor edit = ApplicationLoader.applicationContext.getSharedPreferences("emoji", StickersQuery.TYPE_IMAGE).edit();
            if (this.val$gif) {
                StickersQuery.loadingRecentGifs = false;
                StickersQuery.recentGifsLoaded = true;
                edit.putLong("lastGifLoadTime", System.currentTimeMillis()).commit();
            } else {
                StickersQuery.loadingRecentStickers[this.val$type] = false;
                StickersQuery.recentStickersLoaded[this.val$type] = true;
                edit.putLong("lastStickersLoadTime", System.currentTimeMillis()).commit();
            }
            if (this.val$documents != null) {
                if (this.val$gif) {
                    StickersQuery.recentGifs = this.val$documents;
                } else {
                    StickersQuery.recentStickers[this.val$type] = this.val$documents;
                }
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.recentDocumentsDidLoaded, Boolean.valueOf(this.val$gif), Integer.valueOf(this.val$type));
            }
        }
    }

    static {
        stickerSets = new ArrayList[]{new ArrayList(), new ArrayList()};
        stickerSetsById = new HashMap();
        stickerSetsByName = new HashMap();
        loadingStickers = new boolean[2];
        stickersLoaded = new boolean[2];
        loadHash = new int[2];
        loadDate = new int[2];
        stickersByEmoji = new HashMap();
        allStickers = new HashMap();
        recentStickers = new ArrayList[]{new ArrayList(), new ArrayList()};
        loadingRecentStickers = new boolean[2];
        recentStickersLoaded = new boolean[2];
        recentGifs = new ArrayList();
        featuredStickerSets = new ArrayList();
        featuredStickerSetsById = new HashMap();
        unreadStickerSets = new ArrayList();
        readingStickerSets = new ArrayList();
        stickersById = new HashMap();
    }

    public static void addNewStickerSet(TL_messages_stickerSet tL_messages_stickerSet) {
        if (!stickerSetsById.containsKey(Long.valueOf(tL_messages_stickerSet.set.id)) && !stickerSetsByName.containsKey(tL_messages_stickerSet.set.short_name)) {
            int i;
            int i2 = tL_messages_stickerSet.set.masks ? TYPE_MASK : TYPE_IMAGE;
            stickerSets[i2].add(TYPE_IMAGE, tL_messages_stickerSet);
            stickerSetsById.put(Long.valueOf(tL_messages_stickerSet.set.id), tL_messages_stickerSet);
            stickerSetsByName.put(tL_messages_stickerSet.set.short_name, tL_messages_stickerSet);
            HashMap hashMap = new HashMap();
            for (i = TYPE_IMAGE; i < tL_messages_stickerSet.documents.size(); i += TYPE_MASK) {
                Document document = (Document) tL_messages_stickerSet.documents.get(i);
                hashMap.put(Long.valueOf(document.id), document);
            }
            for (int i3 = TYPE_IMAGE; i3 < tL_messages_stickerSet.packs.size(); i3 += TYPE_MASK) {
                ArrayList arrayList;
                Document document2;
                TL_stickerPack tL_stickerPack = (TL_stickerPack) tL_messages_stickerSet.packs.get(i3);
                tL_stickerPack.emoticon = tL_stickerPack.emoticon.replace("\ufe0f", TtmlNode.ANONYMOUS_REGION_ID);
                ArrayList arrayList2 = (ArrayList) allStickers.get(tL_stickerPack.emoticon);
                if (arrayList2 == null) {
                    arrayList = new ArrayList();
                    allStickers.put(tL_stickerPack.emoticon, arrayList);
                    Iterator it = arrayList.iterator();
                    while (it.hasNext()) {
                        document2 = (Document) it.next();
                        hashMap.put(Long.valueOf(document2.id), document2);
                    }
                } else {
                    arrayList = arrayList2;
                }
                for (int i4 = TYPE_IMAGE; i4 < tL_stickerPack.documents.size(); i4 += TYPE_MASK) {
                    Long l = (Long) tL_stickerPack.documents.get(i4);
                    if (!stickersByEmoji.containsKey(l)) {
                        stickersByEmoji.put(l, tL_stickerPack.emoticon);
                    }
                    document2 = (Document) hashMap.get(l);
                    if (document2 != null) {
                        arrayList.add(document2);
                    }
                }
            }
            loadHash[i2] = calcStickersHash(stickerSets[i2]);
            NotificationCenter instance = NotificationCenter.getInstance();
            i = NotificationCenter.stickersDidLoaded;
            Object[] objArr = new Object[TYPE_MASK];
            objArr[TYPE_IMAGE] = Integer.valueOf(i2);
            instance.postNotificationName(i, objArr);
            loadStickers(i2, false, true);
        }
    }

    public static void addRecentGif(Document document, int i) {
        int i2 = TYPE_IMAGE;
        for (int i3 = TYPE_IMAGE; i3 < recentGifs.size(); i3 += TYPE_MASK) {
            Document document2 = (Document) recentGifs.get(i3);
            if (document2.id == document.id) {
                recentGifs.remove(i3);
                recentGifs.add(TYPE_IMAGE, document2);
                i2 = true;
            }
        }
        if (i2 == 0) {
            recentGifs.add(TYPE_IMAGE, document);
        }
        if (recentGifs.size() > MessagesController.getInstance().maxRecentGifsCount) {
            MessagesStorage.getInstance().getStorageQueue().postRunnable(new C08314((Document) recentGifs.remove(recentGifs.size() - 1)));
        }
        ArrayList arrayList = new ArrayList();
        arrayList.add(document);
        processLoadedRecentDocuments(TYPE_IMAGE, arrayList, true, i);
    }

    public static void addRecentSticker(int i, Document document, int i2) {
        boolean z = false;
        for (int i3 = TYPE_IMAGE; i3 < recentStickers[i].size(); i3 += TYPE_MASK) {
            Document document2 = (Document) recentStickers[i].get(i3);
            if (document2.id == document.id) {
                recentStickers[i].remove(i3);
                recentStickers[i].add(TYPE_IMAGE, document2);
                z = true;
            }
        }
        if (!z) {
            recentStickers[i].add(TYPE_IMAGE, document);
        }
        if (recentStickers[i].size() > MessagesController.getInstance().maxRecentStickersCount) {
            MessagesStorage.getInstance().getStorageQueue().postRunnable(new C08191((Document) recentStickers[i].remove(recentStickers[i].size() - 1)));
        }
        ArrayList arrayList = new ArrayList();
        arrayList.add(document);
        processLoadedRecentDocuments(i, arrayList, false, i2);
    }

    private static int calcDocumentsHash(ArrayList<Document> arrayList) {
        if (arrayList == null) {
            return TYPE_IMAGE;
        }
        long j = 0;
        for (int i = TYPE_IMAGE; i < Math.min(Callback.DEFAULT_DRAG_ANIMATION_DURATION, arrayList.size()); i += TYPE_MASK) {
            Document document = (Document) arrayList.get(i);
            if (document != null) {
                j = (((((((j * 20261) + 2147483648L) + ((long) ((int) (document.id >> 32)))) % 2147483648L) * 20261) + 2147483648L) + ((long) ((int) document.id))) % 2147483648L;
            }
        }
        return (int) j;
    }

    private static int calcFeaturedStickersHash(ArrayList<StickerSetCovered> arrayList) {
        long j = 0;
        for (int i = TYPE_IMAGE; i < arrayList.size(); i += TYPE_MASK) {
            StickerSet stickerSet = ((StickerSetCovered) arrayList.get(i)).set;
            if (!stickerSet.archived) {
                j = (((((((j * 20261) + 2147483648L) + ((long) ((int) (stickerSet.id >> 32)))) % 2147483648L) * 20261) + 2147483648L) + ((long) ((int) stickerSet.id))) % 2147483648L;
                if (unreadStickerSets.contains(Long.valueOf(stickerSet.id))) {
                    j = (((j * 20261) + 2147483648L) + 1) % 2147483648L;
                }
            }
        }
        return (int) j;
    }

    public static void calcNewHash(int i) {
        loadHash[i] = calcStickersHash(stickerSets[i]);
    }

    private static int calcStickersHash(ArrayList<TL_messages_stickerSet> arrayList) {
        long j = 0;
        for (int i = TYPE_IMAGE; i < arrayList.size(); i += TYPE_MASK) {
            StickerSet stickerSet = ((TL_messages_stickerSet) arrayList.get(i)).set;
            if (!stickerSet.archived) {
                j = (((j * 20261) + 2147483648L) + ((long) stickerSet.hash)) % 2147483648L;
            }
        }
        return (int) j;
    }

    public static void checkFeaturedStickers() {
        if (!loadingFeaturedStickers) {
            if (!featuredStickersLoaded || Math.abs((System.currentTimeMillis() / 1000) - ((long) loadFeaturedDate)) >= 3600) {
                loadFeaturesStickers(true, false);
            }
        }
    }

    public static void checkStickers(int i) {
        if (!loadingStickers[i]) {
            if (!stickersLoaded[i] || Math.abs((System.currentTimeMillis() / 1000) - ((long) loadDate[i])) >= 3600) {
                loadStickers(i, true, false);
            }
        }
    }

    public static void cleanup() {
        for (int i = TYPE_IMAGE; i < 2; i += TYPE_MASK) {
            loadHash[i] = TYPE_IMAGE;
            loadDate[i] = TYPE_IMAGE;
            stickerSets[i].clear();
            recentStickers[i].clear();
            loadingStickers[i] = false;
            stickersLoaded[i] = false;
            loadingRecentStickers[i] = false;
            recentStickersLoaded[i] = false;
        }
        loadFeaturedDate = TYPE_IMAGE;
        loadFeaturedHash = TYPE_IMAGE;
        allStickers.clear();
        stickersByEmoji.clear();
        featuredStickerSetsById.clear();
        featuredStickerSets.clear();
        unreadStickerSets.clear();
        recentGifs.clear();
        stickerSetsById.clear();
        stickerSetsByName.clear();
        loadingFeaturedStickers = false;
        featuredStickersLoaded = false;
        loadingRecentGifs = false;
        recentGifsLoaded = false;
    }

    public static HashMap<String, ArrayList<Document>> getAllStickers() {
        return allStickers;
    }

    public static String getEmojiForSticker(long j) {
        String str = (String) stickersByEmoji.get(Long.valueOf(j));
        return str != null ? str : TtmlNode.ANONYMOUS_REGION_ID;
    }

    public static ArrayList<StickerSetCovered> getFeaturedStickerSets() {
        return featuredStickerSets;
    }

    public static ArrayList<Document> getRecentGifs() {
        return new ArrayList(recentGifs);
    }

    public static ArrayList<Document> getRecentStickers(int i) {
        return new ArrayList(recentStickers[i]);
    }

    public static ArrayList<Document> getRecentStickersNoCopy(int i) {
        return recentStickers[i];
    }

    public static Document getStickerById(long j) {
        Document document;
        if (stickersById.isEmpty()) {
            for (Entry value : allStickers.entrySet()) {
                Iterator it = ((ArrayList) value.getValue()).iterator();
                while (it.hasNext()) {
                    document = (Document) it.next();
                    stickersById.put(Long.valueOf(document.id), document);
                }
            }
        }
        document = (Document) stickersById.get(Long.valueOf(j));
        if (document == null) {
            return document;
        }
        TL_messages_stickerSet tL_messages_stickerSet = (TL_messages_stickerSet) stickerSetsById.get(Long.valueOf(getStickerSetId(document)));
        return (tL_messages_stickerSet == null || !tL_messages_stickerSet.set.archived) ? document : null;
    }

    public static TL_messages_stickerSet getStickerSetById(Long l) {
        return (TL_messages_stickerSet) stickerSetsById.get(l);
    }

    public static TL_messages_stickerSet getStickerSetByName(String str) {
        return (TL_messages_stickerSet) stickerSetsByName.get(str);
    }

    public static long getStickerSetId(Document document) {
        for (int i = TYPE_IMAGE; i < document.attributes.size(); i += TYPE_MASK) {
            DocumentAttribute documentAttribute = (DocumentAttribute) document.attributes.get(i);
            if (documentAttribute instanceof TL_documentAttributeSticker) {
                if (documentAttribute.stickerset instanceof TL_inputStickerSetID) {
                    return documentAttribute.stickerset.id;
                }
                return -1;
            }
        }
        return -1;
    }

    public static String getStickerSetName(long j) {
        TL_messages_stickerSet tL_messages_stickerSet = (TL_messages_stickerSet) stickerSetsById.get(Long.valueOf(j));
        return tL_messages_stickerSet != null ? tL_messages_stickerSet.set.short_name : null;
    }

    public static ArrayList<TL_messages_stickerSet> getStickerSets(int i) {
        return stickerSets[i];
    }

    public static ArrayList<Long> getUnreadStickerSets() {
        return unreadStickerSets;
    }

    public static boolean isLoadingStickers(int i) {
        return loadingStickers[i];
    }

    public static boolean isStickerPackInstalled(long j) {
        return stickerSetsById.containsKey(Long.valueOf(j));
    }

    public static boolean isStickerPackInstalled(String str) {
        return stickerSetsByName.containsKey(str);
    }

    public static boolean isStickerPackUnread(long j) {
        return unreadStickerSets.contains(Long.valueOf(j));
    }

    public static void loadFeaturesStickers(boolean z, boolean z2) {
        if (!loadingFeaturedStickers) {
            loadingFeaturedStickers = true;
            if (z) {
                MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable() {
                    /* JADX WARNING: inconsistent code. */
                    /* Code decompiled incorrectly, please refer to instructions dump. */
                    public void run() {
                        /*
                        r12 = this;
                        r2 = 0;
                        r10 = 1;
                        r0 = 0;
                        r5 = new java.util.ArrayList;
                        r5.<init>();
                        r1 = com.hanista.mobogram.messenger.MessagesStorage.getInstance();	 Catch:{ Throwable -> 0x007c, all -> 0x008c }
                        r1 = r1.getDatabase();	 Catch:{ Throwable -> 0x007c, all -> 0x008c }
                        r3 = "SELECT data, unread, date, hash FROM stickers_featured WHERE 1";
                        r4 = 0;
                        r4 = new java.lang.Object[r4];	 Catch:{ Throwable -> 0x007c, all -> 0x008c }
                        r3 = r1.queryFinalized(r3, r4);	 Catch:{ Throwable -> 0x007c, all -> 0x008c }
                        r1 = r3.next();	 Catch:{ Throwable -> 0x0099, all -> 0x0094 }
                        if (r1 == 0) goto L_0x00ad;
                    L_0x0020:
                        r1 = 0;
                        r6 = r3.byteBufferValue(r1);	 Catch:{ Throwable -> 0x0099, all -> 0x0094 }
                        if (r6 == 0) goto L_0x00ab;
                    L_0x0027:
                        r4 = new java.util.ArrayList;	 Catch:{ Throwable -> 0x0099, all -> 0x0094 }
                        r4.<init>();	 Catch:{ Throwable -> 0x0099, all -> 0x0094 }
                        r1 = 0;
                        r2 = r6.readInt32(r1);	 Catch:{ Throwable -> 0x009e, all -> 0x0094 }
                        r1 = r0;
                    L_0x0032:
                        if (r1 >= r2) goto L_0x0044;
                    L_0x0034:
                        r7 = 0;
                        r7 = r6.readInt32(r7);	 Catch:{ Throwable -> 0x009e, all -> 0x0094 }
                        r8 = 0;
                        r7 = com.hanista.mobogram.tgnet.TLRPC.StickerSetCovered.TLdeserialize(r6, r7, r8);	 Catch:{ Throwable -> 0x009e, all -> 0x0094 }
                        r4.add(r7);	 Catch:{ Throwable -> 0x009e, all -> 0x0094 }
                        r1 = r1 + 1;
                        goto L_0x0032;
                    L_0x0044:
                        r6.reuse();	 Catch:{ Throwable -> 0x009e, all -> 0x0094 }
                    L_0x0047:
                        r1 = 1;
                        r2 = r3.byteBufferValue(r1);	 Catch:{ Throwable -> 0x009e, all -> 0x0094 }
                        if (r2 == 0) goto L_0x0068;
                    L_0x004e:
                        r1 = 0;
                        r6 = r2.readInt32(r1);	 Catch:{ Throwable -> 0x009e, all -> 0x0094 }
                        r1 = r0;
                    L_0x0054:
                        if (r1 >= r6) goto L_0x0065;
                    L_0x0056:
                        r7 = 0;
                        r8 = r2.readInt64(r7);	 Catch:{ Throwable -> 0x009e, all -> 0x0094 }
                        r7 = java.lang.Long.valueOf(r8);	 Catch:{ Throwable -> 0x009e, all -> 0x0094 }
                        r5.add(r7);	 Catch:{ Throwable -> 0x009e, all -> 0x0094 }
                        r1 = r1 + 1;
                        goto L_0x0054;
                    L_0x0065:
                        r2.reuse();	 Catch:{ Throwable -> 0x009e, all -> 0x0094 }
                    L_0x0068:
                        r1 = 2;
                        r2 = r3.intValue(r1);	 Catch:{ Throwable -> 0x009e, all -> 0x0094 }
                        r0 = com.hanista.mobogram.messenger.query.StickersQuery.calcFeaturedStickersHash(r4);	 Catch:{ Throwable -> 0x00a4, all -> 0x0094 }
                        r1 = r2;
                        r2 = r4;
                    L_0x0073:
                        if (r3 == 0) goto L_0x0078;
                    L_0x0075:
                        r3.dispose();
                    L_0x0078:
                        com.hanista.mobogram.messenger.query.StickersQuery.processLoadedFeaturedStickers(r2, r5, r10, r1, r0);
                        return;
                    L_0x007c:
                        r1 = move-exception;
                        r3 = r1;
                        r4 = r2;
                        r1 = r0;
                    L_0x0080:
                        r6 = "tmessages";
                        com.hanista.mobogram.messenger.FileLog.m18e(r6, r3);	 Catch:{ all -> 0x0096 }
                        if (r4 == 0) goto L_0x0078;
                    L_0x0088:
                        r4.dispose();
                        goto L_0x0078;
                    L_0x008c:
                        r0 = move-exception;
                        r3 = r2;
                    L_0x008e:
                        if (r3 == 0) goto L_0x0093;
                    L_0x0090:
                        r3.dispose();
                    L_0x0093:
                        throw r0;
                    L_0x0094:
                        r0 = move-exception;
                        goto L_0x008e;
                    L_0x0096:
                        r0 = move-exception;
                        r3 = r4;
                        goto L_0x008e;
                    L_0x0099:
                        r1 = move-exception;
                        r4 = r3;
                        r3 = r1;
                        r1 = r0;
                        goto L_0x0080;
                    L_0x009e:
                        r1 = move-exception;
                        r2 = r4;
                        r4 = r3;
                        r3 = r1;
                        r1 = r0;
                        goto L_0x0080;
                    L_0x00a4:
                        r1 = move-exception;
                        r11 = r1;
                        r1 = r2;
                        r2 = r4;
                        r4 = r3;
                        r3 = r11;
                        goto L_0x0080;
                    L_0x00ab:
                        r4 = r2;
                        goto L_0x0047;
                    L_0x00ad:
                        r1 = r0;
                        goto L_0x0073;
                        */
                        throw new UnsupportedOperationException("Method not decompiled: com.hanista.mobogram.messenger.query.StickersQuery.11.run():void");
                    }
                });
                return;
            }
            TLObject tL_messages_getFeaturedStickers = new TL_messages_getFeaturedStickers();
            tL_messages_getFeaturedStickers.hash = z2 ? TYPE_IMAGE : loadFeaturedHash;
            ConnectionsManager.getInstance().sendRequest(tL_messages_getFeaturedStickers, new AnonymousClass12(tL_messages_getFeaturedStickers));
        }
    }

    public static void loadRecents(int i, boolean z, boolean z2) {
        if (z) {
            if (!loadingRecentGifs) {
                loadingRecentGifs = true;
                if (recentGifsLoaded) {
                    z2 = false;
                }
            } else {
                return;
            }
        } else if (!loadingRecentStickers[i]) {
            loadingRecentStickers[i] = true;
            if (recentStickersLoaded[i]) {
                z2 = false;
            }
        } else {
            return;
        }
        if (z2) {
            MessagesStorage.getInstance().getStorageQueue().postRunnable(new C08335(z, i));
            return;
        }
        SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("emoji", TYPE_IMAGE);
        if (Math.abs(System.currentTimeMillis() - (z ? sharedPreferences.getLong("lastGifLoadTime", 0) : sharedPreferences.getLong("lastStickersLoadTime", 0))) < 3600000) {
            return;
        }
        if (z) {
            TLObject tL_messages_getSavedGifs = new TL_messages_getSavedGifs();
            tL_messages_getSavedGifs.hash = calcDocumentsHash(recentGifs);
            ConnectionsManager.getInstance().sendRequest(tL_messages_getSavedGifs, new C08346(i, z));
            return;
        }
        TLObject tL_messages_getRecentStickers = new TL_messages_getRecentStickers();
        tL_messages_getRecentStickers.hash = calcDocumentsHash(recentStickers[i]);
        tL_messages_getRecentStickers.attached = i == TYPE_MASK;
        ConnectionsManager.getInstance().sendRequest(tL_messages_getRecentStickers, new C08357(i, z));
    }

    public static void loadStickers(int i, boolean z, boolean z2) {
        int i2 = TYPE_IMAGE;
        if (!loadingStickers[i]) {
            loadingStickers[i] = true;
            if (z) {
                MessagesStorage.getInstance().getStorageQueue().postRunnable(new AnonymousClass19(i));
                return;
            }
            TLObject tL_messages_getAllStickers;
            if (i == 0) {
                tL_messages_getAllStickers = new TL_messages_getAllStickers();
                TL_messages_getAllStickers tL_messages_getAllStickers2 = (TL_messages_getAllStickers) tL_messages_getAllStickers;
                if (!z2) {
                    i2 = loadHash[i];
                }
                tL_messages_getAllStickers2.hash = i2;
            } else {
                tL_messages_getAllStickers = new TL_messages_getMaskStickers();
                TL_messages_getMaskStickers tL_messages_getMaskStickers = (TL_messages_getMaskStickers) tL_messages_getAllStickers;
                if (!z2) {
                    i2 = loadHash[i];
                }
                tL_messages_getMaskStickers.hash = i2;
            }
            ConnectionsManager.getInstance().sendRequest(tL_messages_getAllStickers, new AnonymousClass20(i, i2));
        }
    }

    public static void markFaturedStickersAsRead(boolean z) {
        if (!unreadStickerSets.isEmpty()) {
            unreadStickerSets.clear();
            loadFeaturedHash = calcFeaturedStickersHash(featuredStickerSets);
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.featuredStickersDidLoaded, new Object[TYPE_IMAGE]);
            putFeaturedStickersToCache(featuredStickerSets, unreadStickerSets, loadFeaturedDate, loadFeaturedHash);
            if (z) {
                ConnectionsManager.getInstance().sendRequest(new TL_messages_readFeaturedStickers(), new RequestDelegate() {
                    public void run(TLObject tLObject, TL_error tL_error) {
                    }
                });
            }
        }
    }

    public static void markFaturedStickersByIdAsRead(long j) {
        if (unreadStickerSets.contains(Long.valueOf(j)) && !readingStickerSets.contains(Long.valueOf(j))) {
            readingStickerSets.add(Long.valueOf(j));
            TLObject tL_messages_readFeaturedStickers = new TL_messages_readFeaturedStickers();
            tL_messages_readFeaturedStickers.id.add(Long.valueOf(j));
            ConnectionsManager.getInstance().sendRequest(tL_messages_readFeaturedStickers, new RequestDelegate() {
                public void run(TLObject tLObject, TL_error tL_error) {
                }
            });
            AndroidUtilities.runOnUIThread(new AnonymousClass18(j), 1000);
        }
    }

    private static void processLoadedFeaturedStickers(ArrayList<StickerSetCovered> arrayList, ArrayList<Long> arrayList2, boolean z, int i, int i2) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                StickersQuery.loadingFeaturedStickers = false;
                StickersQuery.featuredStickersLoaded = true;
            }
        });
        Utilities.stageQueue.postRunnable(new AnonymousClass14(z, arrayList, i, i2, arrayList2));
    }

    private static void processLoadedRecentDocuments(int i, ArrayList<Document> arrayList, boolean z, int i2) {
        if (arrayList != null) {
            MessagesStorage.getInstance().getStorageQueue().postRunnable(new C08368(z, arrayList, i, i2));
        }
        if (i2 == 0) {
            AndroidUtilities.runOnUIThread(new C08379(z, i, arrayList));
        }
    }

    private static void processLoadedStickers(int i, ArrayList<TL_messages_stickerSet> arrayList, boolean z, int i2, int i3) {
        AndroidUtilities.runOnUIThread(new AnonymousClass22(i));
        Utilities.stageQueue.postRunnable(new AnonymousClass23(z, arrayList, i2, i3, i));
    }

    private static void putFeaturedStickersToCache(ArrayList<StickerSetCovered> arrayList, ArrayList<Long> arrayList2, int i, int i2) {
        MessagesStorage.getInstance().getStorageQueue().postRunnable(new AnonymousClass15(arrayList != null ? new ArrayList(arrayList) : null, arrayList2, i, i2));
    }

    private static void putStickersToCache(int i, ArrayList<TL_messages_stickerSet> arrayList, int i2, int i3) {
        MessagesStorage.getInstance().getStorageQueue().postRunnable(new AnonymousClass21(arrayList != null ? new ArrayList(arrayList) : null, i, i2, i3));
    }

    public static void removeRecentGif(Document document) {
        recentGifs.remove(document);
        TLObject tL_messages_saveGif = new TL_messages_saveGif();
        tL_messages_saveGif.id = new TL_inputDocument();
        tL_messages_saveGif.id.id = document.id;
        tL_messages_saveGif.id.access_hash = document.access_hash;
        tL_messages_saveGif.unsave = true;
        ConnectionsManager.getInstance().sendRequest(tL_messages_saveGif, new C08292());
        MessagesStorage.getInstance().getStorageQueue().postRunnable(new C08303(document));
    }

    public static void removeRecentSticker(Document document) {
        recentStickers[TYPE_IMAGE].remove(document);
        TLObject tL_messages_saveRecentSticker = new TL_messages_saveRecentSticker();
        tL_messages_saveRecentSticker.id = new TL_inputDocument();
        tL_messages_saveRecentSticker.id.id = document.id;
        tL_messages_saveRecentSticker.id.access_hash = document.access_hash;
        tL_messages_saveRecentSticker.unsave = true;
        ConnectionsManager.getInstance().sendRequest(tL_messages_saveRecentSticker, new RequestDelegate() {
            public void run(TLObject tLObject, TL_error tL_error) {
            }
        });
        MessagesStorage.getInstance().getStorageQueue().postRunnable(new AnonymousClass27(document));
    }

    public static void removeStickersSet(Context context, StickerSet stickerSet, int i, BaseFragment baseFragment, boolean z) {
        boolean z2 = true;
        int i2 = stickerSet.masks ? TYPE_MASK : TYPE_IMAGE;
        InputStickerSet tL_inputStickerSetID = new TL_inputStickerSetID();
        tL_inputStickerSetID.access_hash = stickerSet.access_hash;
        tL_inputStickerSetID.id = stickerSet.id;
        TLObject tL_messages_installStickerSet;
        if (i != 0) {
            int i3;
            NotificationCenter instance;
            Object[] objArr;
            stickerSet.archived = i == TYPE_MASK;
            for (i3 = TYPE_IMAGE; i3 < stickerSets[i2].size(); i3 += TYPE_MASK) {
                TL_messages_stickerSet tL_messages_stickerSet = (TL_messages_stickerSet) stickerSets[i2].get(i3);
                if (tL_messages_stickerSet.set.id == stickerSet.id) {
                    stickerSets[i2].remove(i3);
                    if (i == 2) {
                        stickerSets[i2].add(TYPE_IMAGE, tL_messages_stickerSet);
                    } else {
                        stickerSetsById.remove(Long.valueOf(tL_messages_stickerSet.set.id));
                        stickerSetsByName.remove(tL_messages_stickerSet.set.short_name);
                    }
                    loadHash[i2] = calcStickersHash(stickerSets[i2]);
                    putStickersToCache(i2, stickerSets[i2], loadDate[i2], loadHash[i2]);
                    instance = NotificationCenter.getInstance();
                    i3 = NotificationCenter.stickersDidLoaded;
                    objArr = new Object[TYPE_MASK];
                    objArr[TYPE_IMAGE] = Integer.valueOf(i2);
                    instance.postNotificationName(i3, objArr);
                    tL_messages_installStickerSet = new TL_messages_installStickerSet();
                    tL_messages_installStickerSet.stickerset = tL_inputStickerSetID;
                    if (i != TYPE_MASK) {
                        z2 = false;
                    }
                    tL_messages_installStickerSet.archived = z2;
                    ConnectionsManager.getInstance().sendRequest(tL_messages_installStickerSet, new AnonymousClass24(i2, i, baseFragment, z));
                    return;
                }
            }
            loadHash[i2] = calcStickersHash(stickerSets[i2]);
            putStickersToCache(i2, stickerSets[i2], loadDate[i2], loadHash[i2]);
            instance = NotificationCenter.getInstance();
            i3 = NotificationCenter.stickersDidLoaded;
            objArr = new Object[TYPE_MASK];
            objArr[TYPE_IMAGE] = Integer.valueOf(i2);
            instance.postNotificationName(i3, objArr);
            tL_messages_installStickerSet = new TL_messages_installStickerSet();
            tL_messages_installStickerSet.stickerset = tL_inputStickerSetID;
            if (i != TYPE_MASK) {
                z2 = false;
            }
            tL_messages_installStickerSet.archived = z2;
            ConnectionsManager.getInstance().sendRequest(tL_messages_installStickerSet, new AnonymousClass24(i2, i, baseFragment, z));
            return;
        }
        tL_messages_installStickerSet = new TL_messages_uninstallStickerSet();
        tL_messages_installStickerSet.stickerset = tL_inputStickerSetID;
        ConnectionsManager.getInstance().sendRequest(tL_messages_installStickerSet, new AnonymousClass25(stickerSet, context, i2));
    }

    public static void reorderStickers(int i, ArrayList<Long> arrayList) {
        Collections.sort(stickerSets[i], new AnonymousClass10(arrayList));
        loadHash[i] = calcStickersHash(stickerSets[i]);
        NotificationCenter instance = NotificationCenter.getInstance();
        int i2 = NotificationCenter.stickersDidLoaded;
        Object[] objArr = new Object[TYPE_MASK];
        objArr[TYPE_IMAGE] = Integer.valueOf(i);
        instance.postNotificationName(i2, objArr);
        loadStickers(i, false, true);
    }
}
