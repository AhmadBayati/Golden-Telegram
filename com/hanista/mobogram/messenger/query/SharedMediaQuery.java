package com.hanista.mobogram.messenger.query;

import android.text.TextUtils;
import com.hanista.mobogram.SQLite.SQLiteCursor;
import com.hanista.mobogram.SQLite.SQLiteDatabase;
import com.hanista.mobogram.SQLite.SQLitePreparedStatement;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.ChatObject;
import com.hanista.mobogram.messenger.FileLog;
import com.hanista.mobogram.messenger.ImageLoader;
import com.hanista.mobogram.messenger.MessageObject;
import com.hanista.mobogram.messenger.MessagesController;
import com.hanista.mobogram.messenger.MessagesStorage;
import com.hanista.mobogram.messenger.NotificationCenter;
import com.hanista.mobogram.tgnet.AbstractSerializedData;
import com.hanista.mobogram.tgnet.ConnectionsManager;
import com.hanista.mobogram.tgnet.NativeByteBuffer;
import com.hanista.mobogram.tgnet.RequestDelegate;
import com.hanista.mobogram.tgnet.TLObject;
import com.hanista.mobogram.tgnet.TLRPC.Message;
import com.hanista.mobogram.tgnet.TLRPC.MessageEntity;
import com.hanista.mobogram.tgnet.TLRPC.TL_error;
import com.hanista.mobogram.tgnet.TLRPC.TL_inputMessagesFilterDocument;
import com.hanista.mobogram.tgnet.TLRPC.TL_inputMessagesFilterGif;
import com.hanista.mobogram.tgnet.TLRPC.TL_inputMessagesFilterMusic;
import com.hanista.mobogram.tgnet.TLRPC.TL_inputMessagesFilterPhotoVideo;
import com.hanista.mobogram.tgnet.TLRPC.TL_inputMessagesFilterUrl;
import com.hanista.mobogram.tgnet.TLRPC.TL_inputMessagesFilterVoice;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageEntityEmail;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageEntityTextUrl;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageEntityUrl;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageMediaDocument;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageMediaPhoto;
import com.hanista.mobogram.tgnet.TLRPC.TL_message_secret;
import com.hanista.mobogram.tgnet.TLRPC.TL_messages_messages;
import com.hanista.mobogram.tgnet.TLRPC.TL_messages_search;
import com.hanista.mobogram.tgnet.TLRPC.User;
import com.hanista.mobogram.tgnet.TLRPC.messages_Messages;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

public class SharedMediaQuery {
    public static final int MEDIA_AUDIO = 2;
    public static final int MEDIA_FILE = 1;
    public static final int MEDIA_GIF = 5;
    public static final int MEDIA_MUSIC = 4;
    public static final int MEDIA_PHOTOVIDEO = 0;
    public static final int MEDIA_TYPES_COUNT = 5;
    public static final int MEDIA_URL = 3;

    /* renamed from: com.hanista.mobogram.messenger.query.SharedMediaQuery.1 */
    static class C08041 implements RequestDelegate {
        final /* synthetic */ int val$classGuid;
        final /* synthetic */ int val$count;
        final /* synthetic */ boolean val$isChannel;
        final /* synthetic */ int val$max_id;
        final /* synthetic */ int val$offset;
        final /* synthetic */ int val$type;
        final /* synthetic */ long val$uid;

        C08041(int i, long j, int i2, int i3, int i4, int i5, boolean z) {
            this.val$count = i;
            this.val$uid = j;
            this.val$offset = i2;
            this.val$max_id = i3;
            this.val$type = i4;
            this.val$classGuid = i5;
            this.val$isChannel = z;
        }

        public void run(TLObject tLObject, TL_error tL_error) {
            if (tL_error == null) {
                boolean z;
                messages_Messages com_hanista_mobogram_tgnet_TLRPC_messages_Messages = (messages_Messages) tLObject;
                if (com_hanista_mobogram_tgnet_TLRPC_messages_Messages.messages.size() > this.val$count) {
                    com_hanista_mobogram_tgnet_TLRPC_messages_Messages.messages.remove(com_hanista_mobogram_tgnet_TLRPC_messages_Messages.messages.size() - 1);
                    z = false;
                } else {
                    z = true;
                }
                SharedMediaQuery.processLoadedMedia(com_hanista_mobogram_tgnet_TLRPC_messages_Messages, this.val$uid, this.val$offset, this.val$count, this.val$max_id, this.val$type, false, this.val$classGuid, this.val$isChannel, z);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.query.SharedMediaQuery.2 */
    static class C08062 implements RequestDelegate {
        final /* synthetic */ int val$classGuid;
        final /* synthetic */ int val$type;
        final /* synthetic */ long val$uid;

        /* renamed from: com.hanista.mobogram.messenger.query.SharedMediaQuery.2.1 */
        class C08051 implements Runnable {
            final /* synthetic */ messages_Messages val$res;

            C08051(messages_Messages com_hanista_mobogram_tgnet_TLRPC_messages_Messages) {
                this.val$res = com_hanista_mobogram_tgnet_TLRPC_messages_Messages;
            }

            public void run() {
                MessagesController.getInstance().putUsers(this.val$res.users, false);
                MessagesController.getInstance().putChats(this.val$res.chats, false);
            }
        }

        C08062(long j, int i, int i2) {
            this.val$uid = j;
            this.val$type = i;
            this.val$classGuid = i2;
        }

        public void run(TLObject tLObject, TL_error tL_error) {
            if (tL_error == null) {
                messages_Messages com_hanista_mobogram_tgnet_TLRPC_messages_Messages = (messages_Messages) tLObject;
                MessagesStorage.getInstance().putUsersAndChats(com_hanista_mobogram_tgnet_TLRPC_messages_Messages.users, com_hanista_mobogram_tgnet_TLRPC_messages_Messages.chats, true, true);
                int size = com_hanista_mobogram_tgnet_TLRPC_messages_Messages instanceof TL_messages_messages ? com_hanista_mobogram_tgnet_TLRPC_messages_Messages.messages.size() : com_hanista_mobogram_tgnet_TLRPC_messages_Messages.count;
                AndroidUtilities.runOnUIThread(new C08051(com_hanista_mobogram_tgnet_TLRPC_messages_Messages));
                SharedMediaQuery.processLoadedMediaCount(size, this.val$uid, this.val$type, this.val$classGuid, false);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.query.SharedMediaQuery.3 */
    static class C08073 implements Runnable {
        final /* synthetic */ int val$classGuid;
        final /* synthetic */ boolean val$fromCache;
        final /* synthetic */ ArrayList val$objects;
        final /* synthetic */ messages_Messages val$res;
        final /* synthetic */ boolean val$topReached;
        final /* synthetic */ int val$type;
        final /* synthetic */ long val$uid;

        C08073(messages_Messages com_hanista_mobogram_tgnet_TLRPC_messages_Messages, boolean z, long j, ArrayList arrayList, int i, int i2, boolean z2) {
            this.val$res = com_hanista_mobogram_tgnet_TLRPC_messages_Messages;
            this.val$fromCache = z;
            this.val$uid = j;
            this.val$objects = arrayList;
            this.val$classGuid = i;
            this.val$type = i2;
            this.val$topReached = z2;
        }

        public void run() {
            int i = this.val$res.count;
            MessagesController.getInstance().putUsers(this.val$res.users, this.val$fromCache);
            MessagesController.getInstance().putChats(this.val$res.chats, this.val$fromCache);
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.mediaDidLoaded, Long.valueOf(this.val$uid), Integer.valueOf(i), this.val$objects, Integer.valueOf(this.val$classGuid), Integer.valueOf(this.val$type), Boolean.valueOf(this.val$topReached));
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.query.SharedMediaQuery.4 */
    static class C08084 implements Runnable {
        final /* synthetic */ int val$classGuid;
        final /* synthetic */ int val$count;
        final /* synthetic */ boolean val$fromCache;
        final /* synthetic */ int val$type;
        final /* synthetic */ long val$uid;

        C08084(long j, boolean z, int i, int i2, int i3) {
            this.val$uid = j;
            this.val$fromCache = z;
            this.val$count = i;
            this.val$type = i2;
            this.val$classGuid = i3;
        }

        public void run() {
            int i = SharedMediaQuery.MEDIA_PHOTOVIDEO;
            int i2 = (int) this.val$uid;
            if (this.val$fromCache && this.val$count == -1 && i2 != 0) {
                SharedMediaQuery.getMediaCount(this.val$uid, this.val$type, this.val$classGuid, false);
                return;
            }
            if (!this.val$fromCache) {
                SharedMediaQuery.putMediaCountDatabase(this.val$uid, this.val$type, this.val$count);
            }
            NotificationCenter instance = NotificationCenter.getInstance();
            int i3 = NotificationCenter.mediaCountDidLoaded;
            Object[] objArr = new Object[SharedMediaQuery.MEDIA_MUSIC];
            objArr[SharedMediaQuery.MEDIA_PHOTOVIDEO] = Long.valueOf(this.val$uid);
            if (!(this.val$fromCache && this.val$count == -1)) {
                i = this.val$count;
            }
            objArr[SharedMediaQuery.MEDIA_FILE] = Integer.valueOf(i);
            objArr[SharedMediaQuery.MEDIA_AUDIO] = Boolean.valueOf(this.val$fromCache);
            objArr[SharedMediaQuery.MEDIA_URL] = Integer.valueOf(this.val$type);
            instance.postNotificationName(i3, objArr);
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.query.SharedMediaQuery.5 */
    static class C08095 implements Runnable {
        final /* synthetic */ int val$count;
        final /* synthetic */ int val$type;
        final /* synthetic */ long val$uid;

        C08095(long j, int i, int i2) {
            this.val$uid = j;
            this.val$type = i;
            this.val$count = i2;
        }

        public void run() {
            try {
                SQLitePreparedStatement executeFast = MessagesStorage.getInstance().getDatabase().executeFast("REPLACE INTO media_counts_v2 VALUES(?, ?, ?)");
                executeFast.requery();
                executeFast.bindLong(SharedMediaQuery.MEDIA_FILE, this.val$uid);
                executeFast.bindInteger(SharedMediaQuery.MEDIA_AUDIO, this.val$type);
                executeFast.bindInteger(SharedMediaQuery.MEDIA_URL, this.val$count);
                executeFast.step();
                executeFast.dispose();
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.query.SharedMediaQuery.6 */
    static class C08106 implements Runnable {
        final /* synthetic */ int val$classGuid;
        final /* synthetic */ int val$type;
        final /* synthetic */ long val$uid;

        C08106(long j, int i, int i2) {
            this.val$uid = j;
            this.val$type = i;
            this.val$classGuid = i2;
        }

        public void run() {
            try {
                SQLiteDatabase database = MessagesStorage.getInstance().getDatabase();
                Object[] objArr = new Object[SharedMediaQuery.MEDIA_AUDIO];
                objArr[SharedMediaQuery.MEDIA_PHOTOVIDEO] = Long.valueOf(this.val$uid);
                objArr[SharedMediaQuery.MEDIA_FILE] = Integer.valueOf(this.val$type);
                SQLiteCursor queryFinalized = database.queryFinalized(String.format(Locale.US, "SELECT count FROM media_counts_v2 WHERE uid = %d AND type = %d LIMIT 1", objArr), new Object[SharedMediaQuery.MEDIA_PHOTOVIDEO]);
                int intValue = queryFinalized.next() ? queryFinalized.intValue(SharedMediaQuery.MEDIA_PHOTOVIDEO) : -1;
                queryFinalized.dispose();
                int i = (int) this.val$uid;
                if (intValue == -1 && i == 0) {
                    SQLiteDatabase database2 = MessagesStorage.getInstance().getDatabase();
                    Object[] objArr2 = new Object[SharedMediaQuery.MEDIA_AUDIO];
                    objArr2[SharedMediaQuery.MEDIA_PHOTOVIDEO] = Long.valueOf(this.val$uid);
                    objArr2[SharedMediaQuery.MEDIA_FILE] = Integer.valueOf(this.val$type);
                    queryFinalized = database2.queryFinalized(String.format(Locale.US, "SELECT COUNT(mid) FROM media_v2 WHERE uid = %d AND type = %d LIMIT 1", objArr2), new Object[SharedMediaQuery.MEDIA_PHOTOVIDEO]);
                    if (queryFinalized.next()) {
                        intValue = queryFinalized.intValue(SharedMediaQuery.MEDIA_PHOTOVIDEO);
                    }
                    queryFinalized.dispose();
                    if (intValue != -1) {
                        SharedMediaQuery.putMediaCountDatabase(this.val$uid, this.val$type, intValue);
                    }
                }
                SharedMediaQuery.processLoadedMediaCount(intValue, this.val$uid, this.val$type, this.val$classGuid, true);
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.query.SharedMediaQuery.7 */
    static class C08117 implements Runnable {
        final /* synthetic */ int val$classGuid;
        final /* synthetic */ int val$count;
        final /* synthetic */ boolean val$isChannel;
        final /* synthetic */ int val$max_id;
        final /* synthetic */ int val$offset;
        final /* synthetic */ int val$type;
        final /* synthetic */ long val$uid;

        C08117(int i, long j, int i2, boolean z, int i3, int i4, int i5) {
            this.val$count = i;
            this.val$uid = j;
            this.val$max_id = i2;
            this.val$isChannel = z;
            this.val$type = i3;
            this.val$offset = i4;
            this.val$classGuid = i5;
        }

        public void run() {
            boolean z;
            Throwable e;
            messages_Messages tL_messages_messages = new TL_messages_messages();
            try {
                SQLiteCursor queryFinalized;
                Iterable arrayList = new ArrayList();
                Iterable arrayList2 = new ArrayList();
                int i = this.val$count + SharedMediaQuery.MEDIA_FILE;
                SQLiteDatabase database = MessagesStorage.getInstance().getDatabase();
                z = false;
                Object[] objArr;
                if (((int) this.val$uid) != 0) {
                    int i2 = SharedMediaQuery.MEDIA_PHOTOVIDEO;
                    long j = (long) this.val$max_id;
                    if (this.val$isChannel) {
                        i2 = -((int) this.val$uid);
                    }
                    long j2 = (j == 0 || i2 == 0) ? j : j | (((long) i2) << 32);
                    Object[] objArr2 = new Object[SharedMediaQuery.MEDIA_AUDIO];
                    objArr2[SharedMediaQuery.MEDIA_PHOTOVIDEO] = Long.valueOf(this.val$uid);
                    objArr2[SharedMediaQuery.MEDIA_FILE] = Integer.valueOf(this.val$type);
                    SQLiteCursor queryFinalized2 = database.queryFinalized(String.format(Locale.US, "SELECT start FROM media_holes_v2 WHERE uid = %d AND type = %d AND start IN (0, 1)", objArr2), new Object[SharedMediaQuery.MEDIA_PHOTOVIDEO]);
                    if (queryFinalized2.next()) {
                        z = queryFinalized2.intValue(SharedMediaQuery.MEDIA_PHOTOVIDEO) == SharedMediaQuery.MEDIA_FILE;
                        queryFinalized2.dispose();
                    } else {
                        queryFinalized2.dispose();
                        objArr2 = new Object[SharedMediaQuery.MEDIA_AUDIO];
                        objArr2[SharedMediaQuery.MEDIA_PHOTOVIDEO] = Long.valueOf(this.val$uid);
                        objArr2[SharedMediaQuery.MEDIA_FILE] = Integer.valueOf(this.val$type);
                        queryFinalized2 = database.queryFinalized(String.format(Locale.US, "SELECT min(mid) FROM media_v2 WHERE uid = %d AND type = %d AND mid > 0", objArr2), new Object[SharedMediaQuery.MEDIA_PHOTOVIDEO]);
                        if (queryFinalized2.next()) {
                            int intValue = queryFinalized2.intValue(SharedMediaQuery.MEDIA_PHOTOVIDEO);
                            if (intValue != 0) {
                                SQLitePreparedStatement executeFast = database.executeFast("REPLACE INTO media_holes_v2 VALUES(?, ?, ?, ?)");
                                executeFast.requery();
                                executeFast.bindLong(SharedMediaQuery.MEDIA_FILE, this.val$uid);
                                executeFast.bindInteger(SharedMediaQuery.MEDIA_AUDIO, this.val$type);
                                executeFast.bindInteger(SharedMediaQuery.MEDIA_URL, SharedMediaQuery.MEDIA_PHOTOVIDEO);
                                executeFast.bindInteger(SharedMediaQuery.MEDIA_MUSIC, intValue);
                                executeFast.step();
                                executeFast.dispose();
                            }
                        }
                        queryFinalized2.dispose();
                    }
                    if (j2 != 0) {
                        j = 0;
                        Object[] objArr3 = new Object[SharedMediaQuery.MEDIA_URL];
                        objArr3[SharedMediaQuery.MEDIA_PHOTOVIDEO] = Long.valueOf(this.val$uid);
                        objArr3[SharedMediaQuery.MEDIA_FILE] = Integer.valueOf(this.val$type);
                        objArr3[SharedMediaQuery.MEDIA_AUDIO] = Integer.valueOf(this.val$max_id);
                        SQLiteCursor queryFinalized3 = database.queryFinalized(String.format(Locale.US, "SELECT end FROM media_holes_v2 WHERE uid = %d AND type = %d AND end <= %d ORDER BY end DESC LIMIT 1", objArr3), new Object[SharedMediaQuery.MEDIA_PHOTOVIDEO]);
                        if (queryFinalized3.next()) {
                            j = (long) queryFinalized3.intValue(SharedMediaQuery.MEDIA_PHOTOVIDEO);
                            if (i2 != 0) {
                                j |= ((long) i2) << 32;
                            }
                        }
                        queryFinalized3.dispose();
                        if (j > 1) {
                            Object[] objArr4 = new Object[SharedMediaQuery.MEDIA_TYPES_COUNT];
                            objArr4[SharedMediaQuery.MEDIA_PHOTOVIDEO] = Long.valueOf(this.val$uid);
                            objArr4[SharedMediaQuery.MEDIA_FILE] = Long.valueOf(j2);
                            objArr4[SharedMediaQuery.MEDIA_AUDIO] = Long.valueOf(j);
                            objArr4[SharedMediaQuery.MEDIA_URL] = Integer.valueOf(this.val$type);
                            objArr4[SharedMediaQuery.MEDIA_MUSIC] = Integer.valueOf(i);
                            queryFinalized = database.queryFinalized(String.format(Locale.US, "SELECT data, mid FROM media_v2 WHERE uid = %d AND mid > 0 AND mid < %d AND mid >= %d AND type = %d ORDER BY date DESC, mid DESC LIMIT %d", objArr4), new Object[SharedMediaQuery.MEDIA_PHOTOVIDEO]);
                        } else {
                            objArr = new Object[SharedMediaQuery.MEDIA_MUSIC];
                            objArr[SharedMediaQuery.MEDIA_PHOTOVIDEO] = Long.valueOf(this.val$uid);
                            objArr[SharedMediaQuery.MEDIA_FILE] = Long.valueOf(j2);
                            objArr[SharedMediaQuery.MEDIA_AUDIO] = Integer.valueOf(this.val$type);
                            objArr[SharedMediaQuery.MEDIA_URL] = Integer.valueOf(i);
                            queryFinalized = database.queryFinalized(String.format(Locale.US, "SELECT data, mid FROM media_v2 WHERE uid = %d AND mid > 0 AND mid < %d AND type = %d ORDER BY date DESC, mid DESC LIMIT %d", objArr), new Object[SharedMediaQuery.MEDIA_PHOTOVIDEO]);
                        }
                    } else {
                        j = 0;
                        objArr2 = new Object[SharedMediaQuery.MEDIA_AUDIO];
                        objArr2[SharedMediaQuery.MEDIA_PHOTOVIDEO] = Long.valueOf(this.val$uid);
                        objArr2[SharedMediaQuery.MEDIA_FILE] = Integer.valueOf(this.val$type);
                        SQLiteCursor queryFinalized4 = database.queryFinalized(String.format(Locale.US, "SELECT max(end) FROM media_holes_v2 WHERE uid = %d AND type = %d", objArr2), new Object[SharedMediaQuery.MEDIA_PHOTOVIDEO]);
                        if (queryFinalized4.next()) {
                            j = (long) queryFinalized4.intValue(SharedMediaQuery.MEDIA_PHOTOVIDEO);
                            if (i2 != 0) {
                                j |= ((long) i2) << 32;
                            }
                        }
                        queryFinalized4.dispose();
                        if (j > 1) {
                            Object[] objArr5 = new Object[SharedMediaQuery.MEDIA_TYPES_COUNT];
                            objArr5[SharedMediaQuery.MEDIA_PHOTOVIDEO] = Long.valueOf(this.val$uid);
                            objArr5[SharedMediaQuery.MEDIA_FILE] = Long.valueOf(j);
                            objArr5[SharedMediaQuery.MEDIA_AUDIO] = Integer.valueOf(this.val$type);
                            objArr5[SharedMediaQuery.MEDIA_URL] = Integer.valueOf(this.val$offset);
                            objArr5[SharedMediaQuery.MEDIA_MUSIC] = Integer.valueOf(i);
                            queryFinalized = database.queryFinalized(String.format(Locale.US, "SELECT data, mid FROM media_v2 WHERE uid = %d AND mid >= %d AND type = %d ORDER BY date DESC, mid DESC LIMIT %d,%d", objArr5), new Object[SharedMediaQuery.MEDIA_PHOTOVIDEO]);
                        } else {
                            objArr = new Object[SharedMediaQuery.MEDIA_MUSIC];
                            objArr[SharedMediaQuery.MEDIA_PHOTOVIDEO] = Long.valueOf(this.val$uid);
                            objArr[SharedMediaQuery.MEDIA_FILE] = Integer.valueOf(this.val$type);
                            objArr[SharedMediaQuery.MEDIA_AUDIO] = Integer.valueOf(this.val$offset);
                            objArr[SharedMediaQuery.MEDIA_URL] = Integer.valueOf(i);
                            queryFinalized = database.queryFinalized(String.format(Locale.US, "SELECT data, mid FROM media_v2 WHERE uid = %d AND mid > 0 AND type = %d ORDER BY date DESC, mid DESC LIMIT %d,%d", objArr), new Object[SharedMediaQuery.MEDIA_PHOTOVIDEO]);
                        }
                    }
                } else {
                    z = true;
                    if (this.val$max_id != 0) {
                        objArr = new Object[SharedMediaQuery.MEDIA_MUSIC];
                        objArr[SharedMediaQuery.MEDIA_PHOTOVIDEO] = Long.valueOf(this.val$uid);
                        objArr[SharedMediaQuery.MEDIA_FILE] = Integer.valueOf(this.val$max_id);
                        objArr[SharedMediaQuery.MEDIA_AUDIO] = Integer.valueOf(this.val$type);
                        objArr[SharedMediaQuery.MEDIA_URL] = Integer.valueOf(i);
                        queryFinalized = database.queryFinalized(String.format(Locale.US, "SELECT m.data, m.mid, r.random_id FROM media_v2 as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND m.mid > %d AND type = %d ORDER BY m.mid ASC LIMIT %d", objArr), new Object[SharedMediaQuery.MEDIA_PHOTOVIDEO]);
                    } else {
                        objArr = new Object[SharedMediaQuery.MEDIA_MUSIC];
                        objArr[SharedMediaQuery.MEDIA_PHOTOVIDEO] = Long.valueOf(this.val$uid);
                        objArr[SharedMediaQuery.MEDIA_FILE] = Integer.valueOf(this.val$type);
                        objArr[SharedMediaQuery.MEDIA_AUDIO] = Integer.valueOf(this.val$offset);
                        objArr[SharedMediaQuery.MEDIA_URL] = Integer.valueOf(i);
                        queryFinalized = database.queryFinalized(String.format(Locale.US, "SELECT m.data, m.mid, r.random_id FROM media_v2 as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND type = %d ORDER BY m.mid ASC LIMIT %d,%d", objArr), new Object[SharedMediaQuery.MEDIA_PHOTOVIDEO]);
                    }
                }
                while (queryFinalized.next()) {
                    AbstractSerializedData byteBufferValue = queryFinalized.byteBufferValue(SharedMediaQuery.MEDIA_PHOTOVIDEO);
                    if (byteBufferValue != null) {
                        Message TLdeserialize = Message.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                        byteBufferValue.reuse();
                        TLdeserialize.id = queryFinalized.intValue(SharedMediaQuery.MEDIA_FILE);
                        TLdeserialize.dialog_id = this.val$uid;
                        if (((int) this.val$uid) == 0) {
                            TLdeserialize.random_id = queryFinalized.longValue(SharedMediaQuery.MEDIA_AUDIO);
                        }
                        tL_messages_messages.messages.add(TLdeserialize);
                        if (TLdeserialize.from_id > 0) {
                            if (!arrayList.contains(Integer.valueOf(TLdeserialize.from_id))) {
                                arrayList.add(Integer.valueOf(TLdeserialize.from_id));
                            }
                        } else if (!arrayList2.contains(Integer.valueOf(-TLdeserialize.from_id))) {
                            arrayList2.add(Integer.valueOf(-TLdeserialize.from_id));
                        }
                    }
                }
                queryFinalized.dispose();
                if (!arrayList.isEmpty()) {
                    MessagesStorage.getInstance().getUsersInternal(TextUtils.join(",", arrayList), tL_messages_messages.users);
                }
                if (!arrayList2.isEmpty()) {
                    MessagesStorage.getInstance().getChatsInternal(TextUtils.join(",", arrayList2), tL_messages_messages.chats);
                }
                if (tL_messages_messages.messages.size() > this.val$count) {
                    z = false;
                    try {
                        tL_messages_messages.messages.remove(tL_messages_messages.messages.size() - 1);
                    } catch (Exception e2) {
                        e = e2;
                        try {
                            tL_messages_messages.messages.clear();
                            tL_messages_messages.chats.clear();
                            tL_messages_messages.users.clear();
                            FileLog.m18e("tmessages", e);
                            SharedMediaQuery.processLoadedMedia(tL_messages_messages, this.val$uid, this.val$offset, this.val$count, this.val$max_id, this.val$type, true, this.val$classGuid, this.val$isChannel, z);
                        } catch (Throwable th) {
                            e = th;
                            SharedMediaQuery.processLoadedMedia(tL_messages_messages, this.val$uid, this.val$offset, this.val$count, this.val$max_id, this.val$type, true, this.val$classGuid, this.val$isChannel, z);
                            throw e;
                        }
                    }
                }
                SharedMediaQuery.processLoadedMedia(tL_messages_messages, this.val$uid, this.val$offset, this.val$count, this.val$max_id, this.val$type, true, this.val$classGuid, this.val$isChannel, z);
            } catch (Exception e3) {
                e = e3;
                z = false;
                tL_messages_messages.messages.clear();
                tL_messages_messages.chats.clear();
                tL_messages_messages.users.clear();
                FileLog.m18e("tmessages", e);
                SharedMediaQuery.processLoadedMedia(tL_messages_messages, this.val$uid, this.val$offset, this.val$count, this.val$max_id, this.val$type, true, this.val$classGuid, this.val$isChannel, z);
            } catch (Throwable th2) {
                e = th2;
                z = false;
                SharedMediaQuery.processLoadedMedia(tL_messages_messages, this.val$uid, this.val$offset, this.val$count, this.val$max_id, this.val$type, true, this.val$classGuid, this.val$isChannel, z);
                throw e;
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.query.SharedMediaQuery.8 */
    static class C08128 implements Runnable {
        final /* synthetic */ int val$max_id;
        final /* synthetic */ ArrayList val$messages;
        final /* synthetic */ boolean val$topReached;
        final /* synthetic */ int val$type;
        final /* synthetic */ long val$uid;

        C08128(ArrayList arrayList, boolean z, long j, int i, int i2) {
            this.val$messages = arrayList;
            this.val$topReached = z;
            this.val$uid = j;
            this.val$max_id = i;
            this.val$type = i2;
        }

        public void run() {
            int i = SharedMediaQuery.MEDIA_FILE;
            try {
                if (this.val$messages.isEmpty() || this.val$topReached) {
                    MessagesStorage.getInstance().doneHolesInMedia(this.val$uid, this.val$max_id, this.val$type);
                    if (this.val$messages.isEmpty()) {
                        return;
                    }
                }
                MessagesStorage.getInstance().getDatabase().beginTransaction();
                SQLitePreparedStatement executeFast = MessagesStorage.getInstance().getDatabase().executeFast("REPLACE INTO media_v2 VALUES(?, ?, ?, ?, ?)");
                Iterator it = this.val$messages.iterator();
                while (it.hasNext()) {
                    Message message = (Message) it.next();
                    if (SharedMediaQuery.canAddMessageToMedia(message)) {
                        long j = (long) message.id;
                        if (message.to_id.channel_id != 0) {
                            j |= ((long) message.to_id.channel_id) << 32;
                        }
                        executeFast.requery();
                        NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(message.getObjectSize());
                        message.serializeToStream(nativeByteBuffer);
                        executeFast.bindLong(SharedMediaQuery.MEDIA_FILE, j);
                        executeFast.bindLong(SharedMediaQuery.MEDIA_AUDIO, this.val$uid);
                        executeFast.bindInteger(SharedMediaQuery.MEDIA_URL, message.date);
                        executeFast.bindInteger(SharedMediaQuery.MEDIA_MUSIC, this.val$type);
                        executeFast.bindByteBuffer((int) SharedMediaQuery.MEDIA_TYPES_COUNT, nativeByteBuffer);
                        executeFast.step();
                        nativeByteBuffer.reuse();
                    }
                }
                executeFast.dispose();
                if (!(this.val$topReached && this.val$max_id == 0)) {
                    if (!this.val$topReached) {
                        i = ((Message) this.val$messages.get(this.val$messages.size() - 1)).id;
                    }
                    if (this.val$max_id != 0) {
                        MessagesStorage.getInstance().closeHolesInMedia(this.val$uid, i, this.val$max_id, this.val$type);
                    } else {
                        MessagesStorage.getInstance().closeHolesInMedia(this.val$uid, i, ConnectionsManager.DEFAULT_DATACENTER_ID, this.val$type);
                    }
                }
                MessagesStorage.getInstance().getDatabase().commitTransaction();
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.query.SharedMediaQuery.9 */
    static class C08149 implements Runnable {
        final /* synthetic */ int val$max_id;
        final /* synthetic */ long val$uid;

        /* renamed from: com.hanista.mobogram.messenger.query.SharedMediaQuery.9.1 */
        class C08131 implements Runnable {
            final /* synthetic */ ArrayList val$arrayList;

            C08131(ArrayList arrayList) {
                this.val$arrayList = arrayList;
            }

            public void run() {
                NotificationCenter instance = NotificationCenter.getInstance();
                int i = NotificationCenter.musicDidLoaded;
                Object[] objArr = new Object[SharedMediaQuery.MEDIA_AUDIO];
                objArr[SharedMediaQuery.MEDIA_PHOTOVIDEO] = Long.valueOf(C08149.this.val$uid);
                objArr[SharedMediaQuery.MEDIA_FILE] = this.val$arrayList;
                instance.postNotificationName(i, objArr);
            }
        }

        C08149(long j, int i) {
            this.val$uid = j;
            this.val$max_id = i;
        }

        public void run() {
            ArrayList arrayList = new ArrayList();
            try {
                SQLiteDatabase database = MessagesStorage.getInstance().getDatabase();
                Object[] objArr = new Object[SharedMediaQuery.MEDIA_URL];
                objArr[SharedMediaQuery.MEDIA_PHOTOVIDEO] = Long.valueOf(this.val$uid);
                objArr[SharedMediaQuery.MEDIA_FILE] = Integer.valueOf(this.val$max_id);
                objArr[SharedMediaQuery.MEDIA_AUDIO] = Integer.valueOf(SharedMediaQuery.MEDIA_MUSIC);
                SQLiteCursor queryFinalized = database.queryFinalized(String.format(Locale.US, "SELECT data, mid FROM media_v2 WHERE uid = %d AND mid < %d AND type = %d ORDER BY date DESC, mid DESC LIMIT 1000", objArr), new Object[SharedMediaQuery.MEDIA_PHOTOVIDEO]);
                while (queryFinalized.next()) {
                    AbstractSerializedData byteBufferValue = queryFinalized.byteBufferValue(SharedMediaQuery.MEDIA_PHOTOVIDEO);
                    if (byteBufferValue != null) {
                        Message TLdeserialize = Message.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                        byteBufferValue.reuse();
                        if (MessageObject.isMusicMessage(TLdeserialize)) {
                            TLdeserialize.id = queryFinalized.intValue(SharedMediaQuery.MEDIA_FILE);
                            TLdeserialize.dialog_id = this.val$uid;
                            arrayList.add(SharedMediaQuery.MEDIA_PHOTOVIDEO, new MessageObject(TLdeserialize, null, false));
                        }
                    }
                }
                queryFinalized.dispose();
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
            AndroidUtilities.runOnUIThread(new C08131(arrayList));
        }
    }

    public static boolean canAddMessageToMedia(Message message) {
        if ((message instanceof TL_message_secret) && (message.media instanceof TL_messageMediaPhoto) && message.ttl != 0 && message.ttl <= 60) {
            return false;
        }
        if ((message.media instanceof TL_messageMediaPhoto) || ((message.media instanceof TL_messageMediaDocument) && !MessageObject.isGifDocument(message.media.document))) {
            return true;
        }
        if (message.entities.isEmpty()) {
            return false;
        }
        for (int i = MEDIA_PHOTOVIDEO; i < message.entities.size(); i += MEDIA_FILE) {
            MessageEntity messageEntity = (MessageEntity) message.entities.get(i);
            if ((messageEntity instanceof TL_messageEntityUrl) || (messageEntity instanceof TL_messageEntityTextUrl) || (messageEntity instanceof TL_messageEntityEmail)) {
                return true;
            }
        }
        return false;
    }

    public static void getMediaCount(long j, int i, int i2, boolean z) {
        int i3 = (int) j;
        if (z || i3 == 0) {
            getMediaCountDatabase(j, i, i2);
            return;
        }
        TLObject tL_messages_search = new TL_messages_search();
        tL_messages_search.offset = MEDIA_PHOTOVIDEO;
        tL_messages_search.limit = MEDIA_FILE;
        tL_messages_search.max_id = MEDIA_PHOTOVIDEO;
        if (i == 0) {
            tL_messages_search.filter = new TL_inputMessagesFilterPhotoVideo();
        } else if (i == MEDIA_FILE) {
            tL_messages_search.filter = new TL_inputMessagesFilterDocument();
        } else if (i == MEDIA_AUDIO) {
            tL_messages_search.filter = new TL_inputMessagesFilterVoice();
        } else if (i == MEDIA_URL) {
            tL_messages_search.filter = new TL_inputMessagesFilterUrl();
        } else if (i == MEDIA_MUSIC) {
            tL_messages_search.filter = new TL_inputMessagesFilterMusic();
        }
        tL_messages_search.f2672q = TtmlNode.ANONYMOUS_REGION_ID;
        tL_messages_search.peer = MessagesController.getInputPeer(i3);
        if (tL_messages_search.peer != null) {
            ConnectionsManager.getInstance().bindRequestToGuid(ConnectionsManager.getInstance().sendRequest(tL_messages_search, new C08062(j, i, i2)), i2);
        }
    }

    private static void getMediaCountDatabase(long j, int i, int i2) {
        MessagesStorage.getInstance().getStorageQueue().postRunnable(new C08106(j, i, i2));
    }

    public static int getMediaType(Message message) {
        if (message == null) {
            return -1;
        }
        if (message.media instanceof TL_messageMediaPhoto) {
            return MEDIA_PHOTOVIDEO;
        }
        if (message.media instanceof TL_messageMediaDocument) {
            return MessageObject.isVoiceMessage(message) ? MEDIA_AUDIO : !MessageObject.isVideoMessage(message) ? MessageObject.isStickerMessage(message) ? -1 : MessageObject.isMusicMessage(message) ? MEDIA_MUSIC : MEDIA_FILE : MEDIA_PHOTOVIDEO;
        } else {
            if (!message.entities.isEmpty()) {
                for (int i = MEDIA_PHOTOVIDEO; i < message.entities.size(); i += MEDIA_FILE) {
                    MessageEntity messageEntity = (MessageEntity) message.entities.get(i);
                    if ((messageEntity instanceof TL_messageEntityUrl) || (messageEntity instanceof TL_messageEntityTextUrl) || (messageEntity instanceof TL_messageEntityEmail)) {
                        return MEDIA_URL;
                    }
                }
            }
            return -1;
        }
    }

    public static void loadMedia(long j, int i, int i2, int i3, int i4, boolean z, int i5) {
        boolean z2 = ((int) j) < 0 && ChatObject.isChannel(-((int) j));
        int i6 = (int) j;
        if ((z || i6 == 0) && i4 != MEDIA_TYPES_COUNT) {
            loadMediaDatabase(j, i, i2, i3, i4, i5, z2);
            return;
        }
        TLObject tL_messages_search = new TL_messages_search();
        tL_messages_search.offset = i;
        tL_messages_search.limit = i2 + MEDIA_FILE;
        tL_messages_search.max_id = i3;
        if (i4 == 0) {
            tL_messages_search.filter = new TL_inputMessagesFilterPhotoVideo();
        } else if (i4 == MEDIA_FILE) {
            tL_messages_search.filter = new TL_inputMessagesFilterDocument();
        } else if (i4 == MEDIA_AUDIO) {
            tL_messages_search.filter = new TL_inputMessagesFilterVoice();
        } else if (i4 == MEDIA_URL) {
            tL_messages_search.filter = new TL_inputMessagesFilterUrl();
        } else if (i4 == MEDIA_MUSIC) {
            tL_messages_search.filter = new TL_inputMessagesFilterMusic();
        } else if (i4 == MEDIA_TYPES_COUNT) {
            tL_messages_search.filter = new TL_inputMessagesFilterGif();
        }
        tL_messages_search.f2672q = TtmlNode.ANONYMOUS_REGION_ID;
        tL_messages_search.peer = MessagesController.getInputPeer(i6);
        if (tL_messages_search.peer != null) {
            ConnectionsManager.getInstance().bindRequestToGuid(ConnectionsManager.getInstance().sendRequest(tL_messages_search, new C08041(i2, j, i, i3, i4, i5, z2)), i5);
        }
    }

    private static void loadMediaDatabase(long j, int i, int i2, int i3, int i4, int i5, boolean z) {
        MessagesStorage.getInstance().getStorageQueue().postRunnable(new C08117(i2, j, i3, z, i4, i, i5));
    }

    public static void loadMusic(long j, int i) {
        MessagesStorage.getInstance().getStorageQueue().postRunnable(new C08149(j, i));
    }

    private static void processLoadedMedia(messages_Messages com_hanista_mobogram_tgnet_TLRPC_messages_Messages, long j, int i, int i2, int i3, int i4, boolean z, int i5, boolean z2, boolean z3) {
        int i6 = (int) j;
        if (z && com_hanista_mobogram_tgnet_TLRPC_messages_Messages.messages.isEmpty() && i6 != 0) {
            loadMedia(j, i, i2, i3, i4, false, i5);
            return;
        }
        int i7;
        if (!z) {
            ImageLoader.saveMessagesThumbs(com_hanista_mobogram_tgnet_TLRPC_messages_Messages.messages);
            MessagesStorage.getInstance().putUsersAndChats(com_hanista_mobogram_tgnet_TLRPC_messages_Messages.users, com_hanista_mobogram_tgnet_TLRPC_messages_Messages.chats, true, true);
            putMediaDatabase(j, i4, com_hanista_mobogram_tgnet_TLRPC_messages_Messages.messages, i3, z3);
        }
        AbstractMap hashMap = new HashMap();
        for (i7 = MEDIA_PHOTOVIDEO; i7 < com_hanista_mobogram_tgnet_TLRPC_messages_Messages.users.size(); i7 += MEDIA_FILE) {
            User user = (User) com_hanista_mobogram_tgnet_TLRPC_messages_Messages.users.get(i7);
            hashMap.put(Integer.valueOf(user.id), user);
        }
        ArrayList arrayList = new ArrayList();
        for (i7 = MEDIA_PHOTOVIDEO; i7 < com_hanista_mobogram_tgnet_TLRPC_messages_Messages.messages.size(); i7 += MEDIA_FILE) {
            arrayList.add(new MessageObject((Message) com_hanista_mobogram_tgnet_TLRPC_messages_Messages.messages.get(i7), hashMap, true));
        }
        AndroidUtilities.runOnUIThread(new C08073(com_hanista_mobogram_tgnet_TLRPC_messages_Messages, z, j, arrayList, i5, i4, z3));
    }

    private static void processLoadedMediaCount(int i, long j, int i2, int i3, boolean z) {
        AndroidUtilities.runOnUIThread(new C08084(j, z, i, i2, i3));
    }

    private static void putMediaCountDatabase(long j, int i, int i2) {
        MessagesStorage.getInstance().getStorageQueue().postRunnable(new C08095(j, i, i2));
    }

    private static void putMediaDatabase(long j, int i, ArrayList<Message> arrayList, int i2, boolean z) {
        MessagesStorage.getInstance().getStorageQueue().postRunnable(new C08128(arrayList, z, j, i2, i));
    }
}
