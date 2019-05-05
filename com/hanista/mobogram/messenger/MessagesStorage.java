package com.hanista.mobogram.messenger;

import android.text.TextUtils;
import android.util.SparseArray;
import android.util.SparseIntArray;
import com.hanista.mobogram.PhoneFormat.PhoneFormat;
import com.hanista.mobogram.SQLite.SQLiteCursor;
import com.hanista.mobogram.SQLite.SQLiteDatabase;
import com.hanista.mobogram.SQLite.SQLitePreparedStatement;
import com.hanista.mobogram.messenger.ContactsController.Contact;
import com.hanista.mobogram.messenger.MediaController.SearchImage;
import com.hanista.mobogram.messenger.query.BotQuery;
import com.hanista.mobogram.messenger.query.MessagesQuery;
import com.hanista.mobogram.messenger.query.SharedMediaQuery;
import com.hanista.mobogram.messenger.support.widget.helper.ItemTouchHelper.Callback;
import com.hanista.mobogram.mobo.download.DownloadMessagesStorage;
import com.hanista.mobogram.tgnet.AbstractSerializedData;
import com.hanista.mobogram.tgnet.ConnectionsManager;
import com.hanista.mobogram.tgnet.NativeByteBuffer;
import com.hanista.mobogram.tgnet.TLObject;
import com.hanista.mobogram.tgnet.TLRPC;
import com.hanista.mobogram.tgnet.TLRPC.BotInfo;
import com.hanista.mobogram.tgnet.TLRPC.ChannelParticipant;
import com.hanista.mobogram.tgnet.TLRPC.Chat;
import com.hanista.mobogram.tgnet.TLRPC.ChatFull;
import com.hanista.mobogram.tgnet.TLRPC.ChatParticipant;
import com.hanista.mobogram.tgnet.TLRPC.ChatParticipants;
import com.hanista.mobogram.tgnet.TLRPC.Document;
import com.hanista.mobogram.tgnet.TLRPC.EncryptedChat;
import com.hanista.mobogram.tgnet.TLRPC.InputMedia;
import com.hanista.mobogram.tgnet.TLRPC.InputPeer;
import com.hanista.mobogram.tgnet.TLRPC.Message;
import com.hanista.mobogram.tgnet.TLRPC.MessageEntity;
import com.hanista.mobogram.tgnet.TLRPC.MessageMedia;
import com.hanista.mobogram.tgnet.TLRPC.Photo;
import com.hanista.mobogram.tgnet.TLRPC.PhotoSize;
import com.hanista.mobogram.tgnet.TLRPC.TL_channelFull;
import com.hanista.mobogram.tgnet.TLRPC.TL_chatChannelParticipant;
import com.hanista.mobogram.tgnet.TLRPC.TL_chatFull;
import com.hanista.mobogram.tgnet.TLRPC.TL_chatInviteEmpty;
import com.hanista.mobogram.tgnet.TLRPC.TL_chatParticipant;
import com.hanista.mobogram.tgnet.TLRPC.TL_chatParticipantAdmin;
import com.hanista.mobogram.tgnet.TLRPC.TL_chatParticipants;
import com.hanista.mobogram.tgnet.TLRPC.TL_contact;
import com.hanista.mobogram.tgnet.TLRPC.TL_dialog;
import com.hanista.mobogram.tgnet.TLRPC.TL_inputMediaGame;
import com.hanista.mobogram.tgnet.TLRPC.TL_inputMessageEntityMentionName;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageActionGameScore;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageActionPinMessage;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageEntityMentionName;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageMediaDocument;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageMediaPhoto;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageMediaUnsupported;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageMediaUnsupported_old;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageMediaWebPage;
import com.hanista.mobogram.tgnet.TLRPC.TL_message_secret;
import com.hanista.mobogram.tgnet.TLRPC.TL_messages_dialogs;
import com.hanista.mobogram.tgnet.TLRPC.TL_messages_messages;
import com.hanista.mobogram.tgnet.TLRPC.TL_peerChannel;
import com.hanista.mobogram.tgnet.TLRPC.TL_peerNotifySettings;
import com.hanista.mobogram.tgnet.TLRPC.TL_peerNotifySettingsEmpty;
import com.hanista.mobogram.tgnet.TLRPC.TL_photoEmpty;
import com.hanista.mobogram.tgnet.TLRPC.TL_replyInlineMarkup;
import com.hanista.mobogram.tgnet.TLRPC.TL_updates_channelDifferenceTooLong;
import com.hanista.mobogram.tgnet.TLRPC.TL_userStatusLastMonth;
import com.hanista.mobogram.tgnet.TLRPC.TL_userStatusLastWeek;
import com.hanista.mobogram.tgnet.TLRPC.TL_userStatusRecently;
import com.hanista.mobogram.tgnet.TLRPC.TL_webPagePending;
import com.hanista.mobogram.tgnet.TLRPC.User;
import com.hanista.mobogram.tgnet.TLRPC.WallPaper;
import com.hanista.mobogram.tgnet.TLRPC.WebPage;
import com.hanista.mobogram.tgnet.TLRPC.messages_Dialogs;
import com.hanista.mobogram.tgnet.TLRPC.messages_Messages;
import com.hanista.mobogram.tgnet.TLRPC.photos_Photos;
import com.hanista.mobogram.ui.Components.VideoPlayer;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicLong;

public class MessagesStorage {
    private static volatile MessagesStorage Instance;
    public static int lastDateValue;
    public static int lastPtsValue;
    public static int lastQtsValue;
    public static int lastSecretVersion;
    public static int lastSeqValue;
    public static int secretG;
    public static byte[] secretPBytes;
    private File cacheFile;
    private SQLiteDatabase database;
    private int lastSavedDate;
    private int lastSavedPts;
    private int lastSavedQts;
    private int lastSavedSeq;
    private AtomicLong lastTaskId;
    private DispatchQueue storageQueue;

    /* renamed from: com.hanista.mobogram.messenger.MessagesStorage.11 */
    class AnonymousClass11 implements Runnable {
        final /* synthetic */ ArrayList val$wallPapers;

        AnonymousClass11(ArrayList arrayList) {
            this.val$wallPapers = arrayList;
        }

        public void run() {
            try {
                MessagesStorage.this.database.executeFast("DELETE FROM wallpapers WHERE 1").stepThis().dispose();
                MessagesStorage.this.database.beginTransaction();
                SQLitePreparedStatement executeFast = MessagesStorage.this.database.executeFast("REPLACE INTO wallpapers VALUES(?, ?)");
                Iterator it = this.val$wallPapers.iterator();
                int i = 0;
                while (it.hasNext()) {
                    WallPaper wallPaper = (WallPaper) it.next();
                    executeFast.requery();
                    NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(wallPaper.getObjectSize());
                    wallPaper.serializeToStream(nativeByteBuffer);
                    executeFast.bindInteger(1, i);
                    executeFast.bindByteBuffer(2, nativeByteBuffer);
                    executeFast.step();
                    int i2 = i + 1;
                    nativeByteBuffer.reuse();
                    i = i2;
                }
                executeFast.dispose();
                MessagesStorage.this.database.commitTransaction();
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MessagesStorage.12 */
    class AnonymousClass12 implements Runnable {
        final /* synthetic */ int val$type;

        /* renamed from: com.hanista.mobogram.messenger.MessagesStorage.12.1 */
        class C05701 implements Runnable {
            final /* synthetic */ ArrayList val$arrayList;

            C05701(ArrayList arrayList) {
                this.val$arrayList = arrayList;
            }

            public void run() {
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.recentImagesDidLoaded, Integer.valueOf(AnonymousClass12.this.val$type), this.val$arrayList);
            }
        }

        AnonymousClass12(int i) {
            this.val$type = i;
        }

        public void run() {
            try {
                SQLiteCursor queryFinalized = MessagesStorage.this.database.queryFinalized("SELECT id, image_url, thumb_url, local_url, width, height, size, date, document FROM web_recent_v3 WHERE type = " + this.val$type + " ORDER BY date DESC", new Object[0]);
                ArrayList arrayList = new ArrayList();
                while (queryFinalized.next()) {
                    SearchImage searchImage = new SearchImage();
                    searchImage.id = queryFinalized.stringValue(0);
                    searchImage.imageUrl = queryFinalized.stringValue(1);
                    searchImage.thumbUrl = queryFinalized.stringValue(2);
                    searchImage.localUrl = queryFinalized.stringValue(3);
                    searchImage.width = queryFinalized.intValue(4);
                    searchImage.height = queryFinalized.intValue(5);
                    searchImage.size = queryFinalized.intValue(6);
                    searchImage.date = queryFinalized.intValue(7);
                    if (!queryFinalized.isNull(8)) {
                        AbstractSerializedData byteBufferValue = queryFinalized.byteBufferValue(8);
                        if (byteBufferValue != null) {
                            searchImage.document = Document.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                            byteBufferValue.reuse();
                        }
                    }
                    searchImage.type = this.val$type;
                    arrayList.add(searchImage);
                }
                queryFinalized.dispose();
                AndroidUtilities.runOnUIThread(new C05701(arrayList));
            } catch (Throwable th) {
                FileLog.m18e("tmessages", th);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MessagesStorage.13 */
    class AnonymousClass13 implements Runnable {
        final /* synthetic */ Document val$document;
        final /* synthetic */ String val$imageUrl;
        final /* synthetic */ String val$localUrl;

        AnonymousClass13(Document document, String str, String str2) {
            this.val$document = document;
            this.val$imageUrl = str;
            this.val$localUrl = str2;
        }

        public void run() {
            try {
                SQLitePreparedStatement executeFast;
                if (this.val$document != null) {
                    executeFast = MessagesStorage.this.database.executeFast("UPDATE web_recent_v3 SET document = ? WHERE image_url = ?");
                    executeFast.requery();
                    NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(this.val$document.getObjectSize());
                    this.val$document.serializeToStream(nativeByteBuffer);
                    executeFast.bindByteBuffer(1, nativeByteBuffer);
                    executeFast.bindString(2, this.val$imageUrl);
                    executeFast.step();
                    executeFast.dispose();
                    nativeByteBuffer.reuse();
                    return;
                }
                executeFast = MessagesStorage.this.database.executeFast("UPDATE web_recent_v3 SET local_url = ? WHERE image_url = ?");
                executeFast.requery();
                executeFast.bindString(1, this.val$localUrl);
                executeFast.bindString(2, this.val$imageUrl);
                executeFast.step();
                executeFast.dispose();
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MessagesStorage.14 */
    class AnonymousClass14 implements Runnable {
        final /* synthetic */ int val$type;

        AnonymousClass14(int i) {
            this.val$type = i;
        }

        public void run() {
            try {
                MessagesStorage.this.database.executeFast("DELETE FROM web_recent_v3 WHERE type = " + this.val$type).stepThis().dispose();
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MessagesStorage.15 */
    class AnonymousClass15 implements Runnable {
        final /* synthetic */ ArrayList val$arrayList;

        AnonymousClass15(ArrayList arrayList) {
            this.val$arrayList = arrayList;
        }

        public void run() {
            int i = Callback.DEFAULT_DRAG_ANIMATION_DURATION;
            try {
                MessagesStorage.this.database.beginTransaction();
                SQLitePreparedStatement executeFast = MessagesStorage.this.database.executeFast("REPLACE INTO web_recent_v3 VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                int i2 = 0;
                while (i2 < this.val$arrayList.size() && i2 != Callback.DEFAULT_DRAG_ANIMATION_DURATION) {
                    NativeByteBuffer nativeByteBuffer;
                    SearchImage searchImage = (SearchImage) this.val$arrayList.get(i2);
                    executeFast.requery();
                    executeFast.bindString(1, searchImage.id);
                    executeFast.bindInteger(2, searchImage.type);
                    executeFast.bindString(3, searchImage.imageUrl != null ? searchImage.imageUrl : TtmlNode.ANONYMOUS_REGION_ID);
                    executeFast.bindString(4, searchImage.thumbUrl != null ? searchImage.thumbUrl : TtmlNode.ANONYMOUS_REGION_ID);
                    executeFast.bindString(5, searchImage.localUrl != null ? searchImage.localUrl : TtmlNode.ANONYMOUS_REGION_ID);
                    executeFast.bindInteger(6, searchImage.width);
                    executeFast.bindInteger(7, searchImage.height);
                    executeFast.bindInteger(8, searchImage.size);
                    executeFast.bindInteger(9, searchImage.date);
                    if (searchImage.document != null) {
                        NativeByteBuffer nativeByteBuffer2 = new NativeByteBuffer(searchImage.document.getObjectSize());
                        searchImage.document.serializeToStream(nativeByteBuffer2);
                        executeFast.bindByteBuffer(10, nativeByteBuffer2);
                        nativeByteBuffer = nativeByteBuffer2;
                    } else {
                        executeFast.bindNull(10);
                        nativeByteBuffer = null;
                    }
                    executeFast.step();
                    if (nativeByteBuffer != null) {
                        nativeByteBuffer.reuse();
                    }
                    i2++;
                }
                executeFast.dispose();
                MessagesStorage.this.database.commitTransaction();
                if (this.val$arrayList.size() >= Callback.DEFAULT_DRAG_ANIMATION_DURATION) {
                    MessagesStorage.this.database.beginTransaction();
                    while (i < this.val$arrayList.size()) {
                        MessagesStorage.this.database.executeFast("DELETE FROM web_recent_v3 WHERE id = '" + ((SearchImage) this.val$arrayList.get(i)).id + "'").stepThis().dispose();
                        i++;
                    }
                    MessagesStorage.this.database.commitTransaction();
                }
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MessagesStorage.18 */
    class AnonymousClass18 implements Runnable {
        final /* synthetic */ int val$id;

        AnonymousClass18(int i) {
            this.val$id = i;
        }

        public void run() {
            try {
                MessagesStorage.this.database.executeFast("DELETE FROM blocked_users WHERE uid = " + this.val$id).stepThis().dispose();
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MessagesStorage.19 */
    class AnonymousClass19 implements Runnable {
        final /* synthetic */ ArrayList val$ids;
        final /* synthetic */ boolean val$replace;

        AnonymousClass19(boolean z, ArrayList arrayList) {
            this.val$replace = z;
            this.val$ids = arrayList;
        }

        public void run() {
            try {
                if (this.val$replace) {
                    MessagesStorage.this.database.executeFast("DELETE FROM blocked_users WHERE 1").stepThis().dispose();
                }
                MessagesStorage.this.database.beginTransaction();
                SQLitePreparedStatement executeFast = MessagesStorage.this.database.executeFast("REPLACE INTO blocked_users VALUES(?)");
                Iterator it = this.val$ids.iterator();
                while (it.hasNext()) {
                    Integer num = (Integer) it.next();
                    executeFast.requery();
                    executeFast.bindInteger(1, num.intValue());
                    executeFast.step();
                }
                executeFast.dispose();
                MessagesStorage.this.database.commitTransaction();
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MessagesStorage.1 */
    class C05721 implements Runnable {
        final /* synthetic */ int val$currentVersion;

        /* renamed from: com.hanista.mobogram.messenger.MessagesStorage.1.1 */
        class C05681 implements Runnable {
            C05681() {
            }

            public void run() {
                ArrayList arrayList = new ArrayList();
                for (Entry entry : ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).getAll().entrySet()) {
                    String str = (String) entry.getKey();
                    if (str.startsWith("notify2_") && ((Integer) entry.getValue()).intValue() == 2) {
                        try {
                            arrayList.add(Integer.valueOf(Integer.parseInt(str.replace("notify2_", TtmlNode.ANONYMOUS_REGION_ID))));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                try {
                    MessagesStorage.this.database.beginTransaction();
                    SQLitePreparedStatement executeFast = MessagesStorage.this.database.executeFast("REPLACE INTO dialog_settings VALUES(?, ?)");
                    Iterator it = arrayList.iterator();
                    while (it.hasNext()) {
                        Integer num = (Integer) it.next();
                        executeFast.requery();
                        executeFast.bindLong(1, (long) num.intValue());
                        executeFast.bindInteger(2, 1);
                        executeFast.step();
                    }
                    executeFast.dispose();
                    MessagesStorage.this.database.commitTransaction();
                } catch (Throwable e2) {
                    FileLog.m18e("tmessages", e2);
                }
            }
        }

        C05721(int i) {
            this.val$currentVersion = i;
        }

        public void run() {
            try {
                int i = this.val$currentVersion;
                if (i < 4) {
                    MessagesStorage.this.database.executeFast("CREATE TABLE IF NOT EXISTS user_photos(uid INTEGER, id INTEGER, data BLOB, PRIMARY KEY (uid, id))").stepThis().dispose();
                    MessagesStorage.this.database.executeFast("DROP INDEX IF EXISTS read_state_out_idx_messages;").stepThis().dispose();
                    MessagesStorage.this.database.executeFast("DROP INDEX IF EXISTS ttl_idx_messages;").stepThis().dispose();
                    MessagesStorage.this.database.executeFast("DROP INDEX IF EXISTS date_idx_messages;").stepThis().dispose();
                    MessagesStorage.this.database.executeFast("CREATE INDEX IF NOT EXISTS mid_out_idx_messages ON messages(mid, out);").stepThis().dispose();
                    MessagesStorage.this.database.executeFast("CREATE INDEX IF NOT EXISTS task_idx_messages ON messages(uid, out, read_state, ttl, date, send_state);").stepThis().dispose();
                    MessagesStorage.this.database.executeFast("CREATE INDEX IF NOT EXISTS uid_date_mid_idx_messages ON messages(uid, date, mid);").stepThis().dispose();
                    MessagesStorage.this.database.executeFast("CREATE TABLE IF NOT EXISTS user_contacts_v6(uid INTEGER PRIMARY KEY, fname TEXT, sname TEXT)").stepThis().dispose();
                    MessagesStorage.this.database.executeFast("CREATE TABLE IF NOT EXISTS user_phones_v6(uid INTEGER, phone TEXT, sphone TEXT, deleted INTEGER, PRIMARY KEY (uid, phone))").stepThis().dispose();
                    MessagesStorage.this.database.executeFast("CREATE INDEX IF NOT EXISTS sphone_deleted_idx_user_phones ON user_phones_v6(sphone, deleted);").stepThis().dispose();
                    MessagesStorage.this.database.executeFast("CREATE INDEX IF NOT EXISTS mid_idx_randoms ON randoms(mid);").stepThis().dispose();
                    MessagesStorage.this.database.executeFast("CREATE TABLE IF NOT EXISTS sent_files_v2(uid TEXT, type INTEGER, data BLOB, PRIMARY KEY (uid, type))").stepThis().dispose();
                    MessagesStorage.this.database.executeFast("CREATE TABLE IF NOT EXISTS blocked_users(uid INTEGER PRIMARY KEY)").stepThis().dispose();
                    MessagesStorage.this.database.executeFast("CREATE TABLE IF NOT EXISTS download_queue(uid INTEGER, type INTEGER, date INTEGER, data BLOB, PRIMARY KEY (uid, type));").stepThis().dispose();
                    MessagesStorage.this.database.executeFast("CREATE INDEX IF NOT EXISTS type_date_idx_download_queue ON download_queue(type, date);").stepThis().dispose();
                    MessagesStorage.this.database.executeFast("CREATE TABLE IF NOT EXISTS dialog_settings(did INTEGER PRIMARY KEY, flags INTEGER);").stepThis().dispose();
                    MessagesStorage.this.database.executeFast("CREATE INDEX IF NOT EXISTS send_state_idx_messages ON messages(mid, send_state, date) WHERE mid < 0 AND send_state = 1;").stepThis().dispose();
                    MessagesStorage.this.database.executeFast("CREATE INDEX IF NOT EXISTS unread_count_idx_dialogs ON dialogs(unread_count);").stepThis().dispose();
                    MessagesStorage.this.database.executeFast("UPDATE messages SET send_state = 2 WHERE mid < 0 AND send_state = 1").stepThis().dispose();
                    MessagesStorage.this.storageQueue.postRunnable(new C05681());
                    MessagesStorage.this.database.executeFast("PRAGMA user_version = 4").stepThis().dispose();
                    i = 4;
                }
                if (i == 4) {
                    MessagesStorage.this.database.executeFast("CREATE TABLE IF NOT EXISTS enc_tasks_v2(mid INTEGER PRIMARY KEY, date INTEGER)").stepThis().dispose();
                    MessagesStorage.this.database.executeFast("CREATE INDEX IF NOT EXISTS date_idx_enc_tasks_v2 ON enc_tasks_v2(date);").stepThis().dispose();
                    MessagesStorage.this.database.beginTransaction();
                    SQLiteCursor queryFinalized = MessagesStorage.this.database.queryFinalized("SELECT date, data FROM enc_tasks WHERE 1", new Object[0]);
                    SQLitePreparedStatement executeFast = MessagesStorage.this.database.executeFast("REPLACE INTO enc_tasks_v2 VALUES(?, ?)");
                    if (queryFinalized.next()) {
                        int intValue = queryFinalized.intValue(0);
                        NativeByteBuffer byteBufferValue = queryFinalized.byteBufferValue(1);
                        if (byteBufferValue != null) {
                            int limit = byteBufferValue.limit();
                            for (i = 0; i < limit / 4; i++) {
                                executeFast.requery();
                                executeFast.bindInteger(1, byteBufferValue.readInt32(false));
                                executeFast.bindInteger(2, intValue);
                                executeFast.step();
                            }
                            byteBufferValue.reuse();
                        }
                    }
                    executeFast.dispose();
                    queryFinalized.dispose();
                    MessagesStorage.this.database.commitTransaction();
                    MessagesStorage.this.database.executeFast("DROP INDEX IF EXISTS date_idx_enc_tasks;").stepThis().dispose();
                    MessagesStorage.this.database.executeFast("DROP TABLE IF EXISTS enc_tasks;").stepThis().dispose();
                    MessagesStorage.this.database.executeFast("ALTER TABLE messages ADD COLUMN media INTEGER default 0").stepThis().dispose();
                    MessagesStorage.this.database.executeFast("PRAGMA user_version = 6").stepThis().dispose();
                    i = 6;
                }
                if (i == 6) {
                    MessagesStorage.this.database.executeFast("CREATE TABLE IF NOT EXISTS messages_seq(mid INTEGER PRIMARY KEY, seq_in INTEGER, seq_out INTEGER);").stepThis().dispose();
                    MessagesStorage.this.database.executeFast("CREATE INDEX IF NOT EXISTS seq_idx_messages_seq ON messages_seq(seq_in, seq_out);").stepThis().dispose();
                    MessagesStorage.this.database.executeFast("ALTER TABLE enc_chats ADD COLUMN layer INTEGER default 0").stepThis().dispose();
                    MessagesStorage.this.database.executeFast("ALTER TABLE enc_chats ADD COLUMN seq_in INTEGER default 0").stepThis().dispose();
                    MessagesStorage.this.database.executeFast("ALTER TABLE enc_chats ADD COLUMN seq_out INTEGER default 0").stepThis().dispose();
                    MessagesStorage.this.database.executeFast("PRAGMA user_version = 7").stepThis().dispose();
                    i = 7;
                }
                if (i == 7 || i == 8 || i == 9) {
                    MessagesStorage.this.database.executeFast("ALTER TABLE enc_chats ADD COLUMN use_count INTEGER default 0").stepThis().dispose();
                    MessagesStorage.this.database.executeFast("ALTER TABLE enc_chats ADD COLUMN exchange_id INTEGER default 0").stepThis().dispose();
                    MessagesStorage.this.database.executeFast("ALTER TABLE enc_chats ADD COLUMN key_date INTEGER default 0").stepThis().dispose();
                    MessagesStorage.this.database.executeFast("ALTER TABLE enc_chats ADD COLUMN fprint INTEGER default 0").stepThis().dispose();
                    MessagesStorage.this.database.executeFast("ALTER TABLE enc_chats ADD COLUMN fauthkey BLOB default NULL").stepThis().dispose();
                    MessagesStorage.this.database.executeFast("ALTER TABLE enc_chats ADD COLUMN khash BLOB default NULL").stepThis().dispose();
                    MessagesStorage.this.database.executeFast("PRAGMA user_version = 10").stepThis().dispose();
                    i = 10;
                }
                if (i == 10) {
                    MessagesStorage.this.database.executeFast("CREATE TABLE IF NOT EXISTS web_recent_v3(id TEXT, type INTEGER, image_url TEXT, thumb_url TEXT, local_url TEXT, width INTEGER, height INTEGER, size INTEGER, date INTEGER, PRIMARY KEY (id, type));").stepThis().dispose();
                    MessagesStorage.this.database.executeFast("PRAGMA user_version = 11").stepThis().dispose();
                    i = 11;
                }
                if (i == 11) {
                    i = 12;
                }
                if (i == 12) {
                    MessagesStorage.this.database.executeFast("DROP INDEX IF EXISTS uid_mid_idx_media;").stepThis().dispose();
                    MessagesStorage.this.database.executeFast("DROP INDEX IF EXISTS mid_idx_media;").stepThis().dispose();
                    MessagesStorage.this.database.executeFast("DROP INDEX IF EXISTS uid_date_mid_idx_media;").stepThis().dispose();
                    MessagesStorage.this.database.executeFast("DROP TABLE IF EXISTS media;").stepThis().dispose();
                    MessagesStorage.this.database.executeFast("DROP TABLE IF EXISTS media_counts;").stepThis().dispose();
                    MessagesStorage.this.database.executeFast("CREATE TABLE IF NOT EXISTS media_v2(mid INTEGER PRIMARY KEY, uid INTEGER, date INTEGER, type INTEGER, data BLOB)").stepThis().dispose();
                    MessagesStorage.this.database.executeFast("CREATE TABLE IF NOT EXISTS media_counts_v2(uid INTEGER, type INTEGER, count INTEGER, PRIMARY KEY(uid, type))").stepThis().dispose();
                    MessagesStorage.this.database.executeFast("CREATE INDEX IF NOT EXISTS uid_mid_type_date_idx_media ON media_v2(uid, mid, type, date);").stepThis().dispose();
                    MessagesStorage.this.database.executeFast("CREATE TABLE IF NOT EXISTS keyvalue(id TEXT PRIMARY KEY, value TEXT)").stepThis().dispose();
                    MessagesStorage.this.database.executeFast("PRAGMA user_version = 13").stepThis().dispose();
                    i = 13;
                }
                if (i == 13) {
                    MessagesStorage.this.database.executeFast("ALTER TABLE messages ADD COLUMN replydata BLOB default NULL").stepThis().dispose();
                    MessagesStorage.this.database.executeFast("PRAGMA user_version = 14").stepThis().dispose();
                    i = 14;
                }
                if (i == 14) {
                    MessagesStorage.this.database.executeFast("CREATE TABLE IF NOT EXISTS hashtag_recent_v2(id TEXT PRIMARY KEY, date INTEGER);").stepThis().dispose();
                    MessagesStorage.this.database.executeFast("PRAGMA user_version = 15").stepThis().dispose();
                    i = 15;
                }
                if (i == 15) {
                    MessagesStorage.this.database.executeFast("CREATE TABLE IF NOT EXISTS webpage_pending(id INTEGER, mid INTEGER, PRIMARY KEY (id, mid));").stepThis().dispose();
                    MessagesStorage.this.database.executeFast("PRAGMA user_version = 16").stepThis().dispose();
                    i = 16;
                }
                if (i == 16) {
                    MessagesStorage.this.database.executeFast("ALTER TABLE dialogs ADD COLUMN inbox_max INTEGER default 0").stepThis().dispose();
                    MessagesStorage.this.database.executeFast("ALTER TABLE dialogs ADD COLUMN outbox_max INTEGER default 0").stepThis().dispose();
                    MessagesStorage.this.database.executeFast("PRAGMA user_version = 17").stepThis().dispose();
                    i = 17;
                }
                if (i == 17) {
                    MessagesStorage.this.database.executeFast("CREATE TABLE bot_info(uid INTEGER PRIMARY KEY, info BLOB)").stepThis().dispose();
                    MessagesStorage.this.database.executeFast("PRAGMA user_version = 18").stepThis().dispose();
                    i = 18;
                }
                if (i == 18) {
                    MessagesStorage.this.database.executeFast("DROP TABLE IF EXISTS stickers;").stepThis().dispose();
                    MessagesStorage.this.database.executeFast("CREATE TABLE IF NOT EXISTS stickers_v2(id INTEGER PRIMARY KEY, data BLOB, date INTEGER, hash TEXT);").stepThis().dispose();
                    MessagesStorage.this.database.executeFast("PRAGMA user_version = 19").stepThis().dispose();
                    i = 19;
                }
                if (i == 19) {
                    MessagesStorage.this.database.executeFast("CREATE TABLE IF NOT EXISTS bot_keyboard(uid INTEGER PRIMARY KEY, mid INTEGER, info BLOB)").stepThis().dispose();
                    MessagesStorage.this.database.executeFast("CREATE INDEX IF NOT EXISTS bot_keyboard_idx_mid ON bot_keyboard(mid);").stepThis().dispose();
                    MessagesStorage.this.database.executeFast("PRAGMA user_version = 20").stepThis().dispose();
                    i = 20;
                }
                if (i == 20) {
                    MessagesStorage.this.database.executeFast("CREATE TABLE search_recent(did INTEGER PRIMARY KEY, date INTEGER);").stepThis().dispose();
                    MessagesStorage.this.database.executeFast("PRAGMA user_version = 21").stepThis().dispose();
                    i = 21;
                }
                if (i == 21) {
                    MessagesStorage.this.database.executeFast("CREATE TABLE IF NOT EXISTS chat_settings_v2(uid INTEGER PRIMARY KEY, info BLOB)").stepThis().dispose();
                    SQLiteCursor queryFinalized2 = MessagesStorage.this.database.queryFinalized("SELECT uid, participants FROM chat_settings WHERE uid < 0", new Object[0]);
                    SQLitePreparedStatement executeFast2 = MessagesStorage.this.database.executeFast("REPLACE INTO chat_settings_v2 VALUES(?, ?)");
                    while (queryFinalized2.next()) {
                        int intValue2 = queryFinalized2.intValue(0);
                        AbstractSerializedData byteBufferValue2 = queryFinalized2.byteBufferValue(1);
                        if (byteBufferValue2 != null) {
                            ChatParticipants TLdeserialize = ChatParticipants.TLdeserialize(byteBufferValue2, byteBufferValue2.readInt32(false), false);
                            byteBufferValue2.reuse();
                            if (TLdeserialize != null) {
                                TL_chatFull tL_chatFull = new TL_chatFull();
                                tL_chatFull.id = intValue2;
                                tL_chatFull.chat_photo = new TL_photoEmpty();
                                tL_chatFull.notify_settings = new TL_peerNotifySettingsEmpty();
                                tL_chatFull.exported_invite = new TL_chatInviteEmpty();
                                tL_chatFull.participants = TLdeserialize;
                                NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(tL_chatFull.getObjectSize());
                                tL_chatFull.serializeToStream(nativeByteBuffer);
                                executeFast2.requery();
                                executeFast2.bindInteger(1, intValue2);
                                executeFast2.bindByteBuffer(2, nativeByteBuffer);
                                executeFast2.step();
                                nativeByteBuffer.reuse();
                            }
                        }
                    }
                    executeFast2.dispose();
                    queryFinalized2.dispose();
                    MessagesStorage.this.database.executeFast("DROP TABLE IF EXISTS chat_settings;").stepThis().dispose();
                    MessagesStorage.this.database.executeFast("ALTER TABLE dialogs ADD COLUMN last_mid_i INTEGER default 0").stepThis().dispose();
                    MessagesStorage.this.database.executeFast("ALTER TABLE dialogs ADD COLUMN unread_count_i INTEGER default 0").stepThis().dispose();
                    MessagesStorage.this.database.executeFast("ALTER TABLE dialogs ADD COLUMN pts INTEGER default 0").stepThis().dispose();
                    MessagesStorage.this.database.executeFast("ALTER TABLE dialogs ADD COLUMN date_i INTEGER default 0").stepThis().dispose();
                    MessagesStorage.this.database.executeFast("CREATE INDEX IF NOT EXISTS last_mid_i_idx_dialogs ON dialogs(last_mid_i);").stepThis().dispose();
                    MessagesStorage.this.database.executeFast("CREATE INDEX IF NOT EXISTS unread_count_i_idx_dialogs ON dialogs(unread_count_i);").stepThis().dispose();
                    MessagesStorage.this.database.executeFast("ALTER TABLE messages ADD COLUMN imp INTEGER default 0").stepThis().dispose();
                    MessagesStorage.this.database.executeFast("CREATE TABLE IF NOT EXISTS messages_holes(uid INTEGER, start INTEGER, end INTEGER, PRIMARY KEY(uid, start));").stepThis().dispose();
                    MessagesStorage.this.database.executeFast("CREATE INDEX IF NOT EXISTS uid_end_messages_holes ON messages_holes(uid, end);").stepThis().dispose();
                    MessagesStorage.this.database.executeFast("PRAGMA user_version = 22").stepThis().dispose();
                    i = 22;
                }
                if (i == 22) {
                    MessagesStorage.this.database.executeFast("CREATE TABLE IF NOT EXISTS media_holes_v2(uid INTEGER, type INTEGER, start INTEGER, end INTEGER, PRIMARY KEY(uid, type, start));").stepThis().dispose();
                    MessagesStorage.this.database.executeFast("CREATE INDEX IF NOT EXISTS uid_end_media_holes_v2 ON media_holes_v2(uid, type, end);").stepThis().dispose();
                    MessagesStorage.this.database.executeFast("PRAGMA user_version = 23").stepThis().dispose();
                    i = 23;
                }
                if (i == 24) {
                    MessagesStorage.this.database.executeFast("DELETE FROM media_holes_v2 WHERE uid != 0 AND type >= 0 AND start IN (0, 1)").stepThis().dispose();
                    MessagesStorage.this.database.executeFast("PRAGMA user_version = 25").stepThis().dispose();
                    i = 25;
                }
                if (i == 25 || i == 26) {
                    MessagesStorage.this.database.executeFast("CREATE TABLE IF NOT EXISTS channel_users_v2(did INTEGER, uid INTEGER, date INTEGER, data BLOB, PRIMARY KEY(did, uid))").stepThis().dispose();
                    MessagesStorage.this.database.executeFast("PRAGMA user_version = 27").stepThis().dispose();
                    i = 27;
                }
                if (i == 27) {
                    MessagesStorage.this.database.executeFast("ALTER TABLE web_recent_v3 ADD COLUMN document BLOB default NULL").stepThis().dispose();
                    MessagesStorage.this.database.executeFast("PRAGMA user_version = 28").stepThis().dispose();
                    i = 28;
                }
                if (i == 28) {
                    MessagesStorage.this.database.executeFast("PRAGMA user_version = 29").stepThis().dispose();
                    i = 29;
                }
                if (i == 29) {
                    MessagesStorage.this.database.executeFast("DELETE FROM sent_files_v2 WHERE 1").stepThis().dispose();
                    MessagesStorage.this.database.executeFast("DELETE FROM download_queue WHERE 1").stepThis().dispose();
                    MessagesStorage.this.database.executeFast("PRAGMA user_version = 30").stepThis().dispose();
                    i = 30;
                }
                if (i == 30) {
                    MessagesStorage.this.database.executeFast("ALTER TABLE chat_settings_v2 ADD COLUMN pinned INTEGER default 0").stepThis().dispose();
                    MessagesStorage.this.database.executeFast("CREATE INDEX IF NOT EXISTS chat_settings_pinned_idx ON chat_settings_v2(uid, pinned) WHERE pinned != 0;").stepThis().dispose();
                    MessagesStorage.this.database.executeFast("CREATE TABLE IF NOT EXISTS chat_pinned(uid INTEGER PRIMARY KEY, pinned INTEGER, data BLOB)").stepThis().dispose();
                    MessagesStorage.this.database.executeFast("CREATE INDEX IF NOT EXISTS chat_pinned_mid_idx ON chat_pinned(uid, pinned) WHERE pinned != 0;").stepThis().dispose();
                    MessagesStorage.this.database.executeFast("CREATE TABLE IF NOT EXISTS users_data(uid INTEGER PRIMARY KEY, about TEXT)").stepThis().dispose();
                    MessagesStorage.this.database.executeFast("PRAGMA user_version = 31").stepThis().dispose();
                    i = 31;
                }
                if (i == 31) {
                    MessagesStorage.this.database.executeFast("DROP TABLE IF EXISTS bot_recent;").stepThis().dispose();
                    MessagesStorage.this.database.executeFast("CREATE TABLE IF NOT EXISTS chat_hints(did INTEGER, type INTEGER, rating REAL, date INTEGER, PRIMARY KEY(did, type))").stepThis().dispose();
                    MessagesStorage.this.database.executeFast("CREATE INDEX IF NOT EXISTS chat_hints_rating_idx ON chat_hints(rating);").stepThis().dispose();
                    MessagesStorage.this.database.executeFast("PRAGMA user_version = 32").stepThis().dispose();
                    i = 32;
                }
                if (i == 32) {
                    MessagesStorage.this.database.executeFast("DROP INDEX IF EXISTS uid_mid_idx_imp_messages;").stepThis().dispose();
                    MessagesStorage.this.database.executeFast("DROP INDEX IF EXISTS uid_date_mid_imp_idx_messages;").stepThis().dispose();
                    MessagesStorage.this.database.executeFast("PRAGMA user_version = 33").stepThis().dispose();
                    i = 33;
                }
                if (i == 33) {
                    MessagesStorage.this.database.executeFast("CREATE TABLE IF NOT EXISTS pending_tasks(id INTEGER PRIMARY KEY, data BLOB);").stepThis().dispose();
                    MessagesStorage.this.database.executeFast("PRAGMA user_version = 34").stepThis().dispose();
                    i = 34;
                }
                if (i == 34) {
                    MessagesStorage.this.database.executeFast("CREATE TABLE IF NOT EXISTS stickers_featured(id INTEGER PRIMARY KEY, data BLOB, unread BLOB, date INTEGER, hash TEXT);").stepThis().dispose();
                    MessagesStorage.this.database.executeFast("PRAGMA user_version = 35").stepThis().dispose();
                    i = 35;
                }
                if (i == 35) {
                    MessagesStorage.this.database.executeFast("CREATE TABLE IF NOT EXISTS requested_holes(uid INTEGER, seq_out_start INTEGER, seq_out_end INTEGER, PRIMARY KEY (uid, seq_out_start, seq_out_end));").stepThis().dispose();
                    MessagesStorage.this.database.executeFast("PRAGMA user_version = 36").stepThis().dispose();
                    i = 36;
                }
                if (i == 36) {
                    MessagesStorage.this.database.executeFast("ALTER TABLE enc_chats ADD COLUMN in_seq_no INTEGER default 0").stepThis().dispose();
                    MessagesStorage.this.database.executeFast("PRAGMA user_version = 37").stepThis().dispose();
                }
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MessagesStorage.20 */
    class AnonymousClass20 implements Runnable {
        final /* synthetic */ int val$channelId;
        final /* synthetic */ int val$uid;

        /* renamed from: com.hanista.mobogram.messenger.MessagesStorage.20.1 */
        class C05741 implements Runnable {
            final /* synthetic */ ArrayList val$mids;

            C05741(ArrayList arrayList) {
                this.val$mids = arrayList;
            }

            public void run() {
                MessagesController.getInstance().markChannelDialogMessageAsDeleted(this.val$mids, AnonymousClass20.this.val$channelId);
            }
        }

        /* renamed from: com.hanista.mobogram.messenger.MessagesStorage.20.2 */
        class C05752 implements Runnable {
            final /* synthetic */ ArrayList val$mids;

            C05752(ArrayList arrayList) {
                this.val$mids = arrayList;
            }

            public void run() {
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.messagesDeleted, this.val$mids, Integer.valueOf(AnonymousClass20.this.val$channelId));
            }
        }

        AnonymousClass20(int i, int i2) {
            this.val$channelId = i;
            this.val$uid = i2;
        }

        public void run() {
            try {
                long j = (long) (-this.val$channelId);
                ArrayList arrayList = new ArrayList();
                SQLiteCursor queryFinalized = MessagesStorage.this.database.queryFinalized("SELECT data FROM messages WHERE uid = " + j, new Object[0]);
                ArrayList arrayList2 = new ArrayList();
                while (queryFinalized.next()) {
                    AbstractSerializedData byteBufferValue = queryFinalized.byteBufferValue(0);
                    if (byteBufferValue != null) {
                        Message TLdeserialize = Message.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                        byteBufferValue.reuse();
                        if (!(TLdeserialize == null || TLdeserialize.from_id != this.val$uid || TLdeserialize.id == 1)) {
                            arrayList.add(Integer.valueOf(TLdeserialize.id));
                            File pathToAttach;
                            if (TLdeserialize.media instanceof TL_messageMediaPhoto) {
                                Iterator it = TLdeserialize.media.photo.sizes.iterator();
                                while (it.hasNext()) {
                                    pathToAttach = FileLoader.getPathToAttach((PhotoSize) it.next());
                                    if (pathToAttach != null && pathToAttach.toString().length() > 0) {
                                        arrayList2.add(pathToAttach);
                                    }
                                }
                            } else {
                                try {
                                    if (TLdeserialize.media instanceof TL_messageMediaDocument) {
                                        pathToAttach = FileLoader.getPathToAttach(TLdeserialize.media.document);
                                        if (pathToAttach != null && pathToAttach.toString().length() > 0) {
                                            arrayList2.add(pathToAttach);
                                        }
                                        pathToAttach = FileLoader.getPathToAttach(TLdeserialize.media.document.thumb);
                                        if (pathToAttach != null && pathToAttach.toString().length() > 0) {
                                            arrayList2.add(pathToAttach);
                                        }
                                    }
                                } catch (Throwable e) {
                                    FileLog.m18e("tmessages", e);
                                }
                            }
                        }
                    }
                }
                queryFinalized.dispose();
                AndroidUtilities.runOnUIThread(new C05741(arrayList));
                MessagesStorage.this.markMessagesAsDeletedInternal(arrayList, this.val$channelId);
                MessagesStorage.this.updateDialogsWithDeletedMessagesInternal(arrayList, this.val$channelId);
                FileLoader.getInstance().deleteFiles(arrayList2, 0);
                if (!arrayList.isEmpty()) {
                    AndroidUtilities.runOnUIThread(new C05752(arrayList));
                }
            } catch (Throwable e2) {
                FileLog.m18e("tmessages", e2);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MessagesStorage.21 */
    class AnonymousClass21 implements Runnable {
        final /* synthetic */ long val$did;
        final /* synthetic */ int val$messagesOnly;

        /* renamed from: com.hanista.mobogram.messenger.MessagesStorage.21.1 */
        class C05761 implements Runnable {
            C05761() {
            }

            public void run() {
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.needReloadRecentDialogsSearch, new Object[0]);
            }
        }

        AnonymousClass21(int i, long j) {
            this.val$messagesOnly = i;
            this.val$did = j;
        }

        public void run() {
            try {
                SQLiteCursor queryFinalized;
                int intValue;
                if (this.val$messagesOnly == 3) {
                    queryFinalized = MessagesStorage.this.database.queryFinalized("SELECT last_mid FROM dialogs WHERE did = " + this.val$did, new Object[0]);
                    intValue = queryFinalized.next() ? queryFinalized.intValue(0) : -1;
                    queryFinalized.dispose();
                    if (intValue != 0) {
                        return;
                    }
                }
                if (((int) this.val$did) == 0 || this.val$messagesOnly == 2) {
                    queryFinalized = MessagesStorage.this.database.queryFinalized("SELECT data FROM messages WHERE uid = " + this.val$did, new Object[0]);
                    ArrayList arrayList = new ArrayList();
                    while (queryFinalized.next()) {
                        AbstractSerializedData byteBufferValue = queryFinalized.byteBufferValue(0);
                        if (byteBufferValue != null) {
                            Message TLdeserialize = Message.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                            byteBufferValue.reuse();
                            if (!(TLdeserialize == null || TLdeserialize.media == null)) {
                                File pathToAttach;
                                if (TLdeserialize.media instanceof TL_messageMediaPhoto) {
                                    Iterator it = TLdeserialize.media.photo.sizes.iterator();
                                    while (it.hasNext()) {
                                        pathToAttach = FileLoader.getPathToAttach((PhotoSize) it.next());
                                        if (pathToAttach != null && pathToAttach.toString().length() > 0) {
                                            arrayList.add(pathToAttach);
                                        }
                                    }
                                } else {
                                    try {
                                        if (TLdeserialize.media instanceof TL_messageMediaDocument) {
                                            pathToAttach = FileLoader.getPathToAttach(TLdeserialize.media.document);
                                            if (pathToAttach != null && pathToAttach.toString().length() > 0) {
                                                arrayList.add(pathToAttach);
                                            }
                                            pathToAttach = FileLoader.getPathToAttach(TLdeserialize.media.document.thumb);
                                            if (pathToAttach != null && pathToAttach.toString().length() > 0) {
                                                arrayList.add(pathToAttach);
                                            }
                                        }
                                    } catch (Throwable e) {
                                        FileLog.m18e("tmessages", e);
                                    }
                                }
                            }
                        }
                    }
                    queryFinalized.dispose();
                    FileLoader.getInstance().deleteFiles(arrayList, this.val$messagesOnly);
                }
                if (this.val$messagesOnly == 0 || this.val$messagesOnly == 3) {
                    MessagesStorage.this.database.executeFast("DELETE FROM dialogs WHERE did = " + this.val$did).stepThis().dispose();
                    MessagesStorage.this.database.executeFast("DELETE FROM chat_settings_v2 WHERE uid = " + this.val$did).stepThis().dispose();
                    MessagesStorage.this.database.executeFast("DELETE FROM chat_pinned WHERE uid = " + this.val$did).stepThis().dispose();
                    MessagesStorage.this.database.executeFast("DELETE FROM channel_users_v2 WHERE did = " + this.val$did).stepThis().dispose();
                    MessagesStorage.this.database.executeFast("DELETE FROM search_recent WHERE did = " + this.val$did).stepThis().dispose();
                    intValue = (int) this.val$did;
                    int i = (int) (this.val$did >> 32);
                    if (intValue == 0) {
                        MessagesStorage.this.database.executeFast("DELETE FROM enc_chats WHERE uid = " + i).stepThis().dispose();
                    } else if (i == 1) {
                        MessagesStorage.this.database.executeFast("DELETE FROM chats WHERE uid = " + intValue).stepThis().dispose();
                    } else if (intValue < 0) {
                    }
                } else if (this.val$messagesOnly == 2) {
                    SQLiteCursor queryFinalized2 = MessagesStorage.this.database.queryFinalized("SELECT last_mid_i, last_mid FROM dialogs WHERE did = " + this.val$did, new Object[0]);
                    if (queryFinalized2.next()) {
                        long longValue = queryFinalized2.longValue(0);
                        long longValue2 = queryFinalized2.longValue(1);
                        SQLiteCursor queryFinalized3 = MessagesStorage.this.database.queryFinalized("SELECT data FROM messages WHERE uid = " + this.val$did + " AND mid IN (" + longValue + "," + longValue2 + ")", new Object[0]);
                        intValue = -1;
                        while (queryFinalized3.next()) {
                            try {
                                AbstractSerializedData byteBufferValue2 = queryFinalized3.byteBufferValue(0);
                                if (byteBufferValue2 != null) {
                                    Message TLdeserialize2 = Message.TLdeserialize(byteBufferValue2, byteBufferValue2.readInt32(false), false);
                                    byteBufferValue2.reuse();
                                    if (TLdeserialize2 != null) {
                                        intValue = TLdeserialize2.id;
                                    }
                                }
                            } catch (Throwable e2) {
                                FileLog.m18e("tmessages", e2);
                            }
                        }
                        queryFinalized3.dispose();
                        MessagesStorage.this.database.executeFast("DELETE FROM messages WHERE uid = " + this.val$did + " AND mid != " + longValue + " AND mid != " + longValue2).stepThis().dispose();
                        MessagesStorage.this.database.executeFast("DELETE FROM messages_holes WHERE uid = " + this.val$did).stepThis().dispose();
                        MessagesStorage.this.database.executeFast("DELETE FROM bot_keyboard WHERE uid = " + this.val$did).stepThis().dispose();
                        MessagesStorage.this.database.executeFast("DELETE FROM media_counts_v2 WHERE uid = " + this.val$did).stepThis().dispose();
                        MessagesStorage.this.database.executeFast("DELETE FROM media_v2 WHERE uid = " + this.val$did).stepThis().dispose();
                        MessagesStorage.this.database.executeFast("DELETE FROM media_holes_v2 WHERE uid = " + this.val$did).stepThis().dispose();
                        BotQuery.clearBotKeyboard(this.val$did, null);
                        SQLitePreparedStatement executeFast = MessagesStorage.this.database.executeFast("REPLACE INTO messages_holes VALUES(?, ?, ?)");
                        SQLitePreparedStatement executeFast2 = MessagesStorage.this.database.executeFast("REPLACE INTO media_holes_v2 VALUES(?, ?, ?, ?)");
                        if (intValue != -1) {
                            MessagesStorage.createFirstHoles(this.val$did, executeFast, executeFast2, intValue);
                        }
                        executeFast.dispose();
                        executeFast2.dispose();
                    }
                    queryFinalized2.dispose();
                    return;
                }
                MessagesStorage.this.database.executeFast("UPDATE dialogs SET unread_count = 0, unread_count_i = 0 WHERE did = " + this.val$did).stepThis().dispose();
                MessagesStorage.this.database.executeFast("DELETE FROM messages WHERE uid = " + this.val$did).stepThis().dispose();
                MessagesStorage.this.database.executeFast("DELETE FROM bot_keyboard WHERE uid = " + this.val$did).stepThis().dispose();
                MessagesStorage.this.database.executeFast("DELETE FROM media_counts_v2 WHERE uid = " + this.val$did).stepThis().dispose();
                MessagesStorage.this.database.executeFast("DELETE FROM media_v2 WHERE uid = " + this.val$did).stepThis().dispose();
                MessagesStorage.this.database.executeFast("DELETE FROM messages_holes WHERE uid = " + this.val$did).stepThis().dispose();
                MessagesStorage.this.database.executeFast("DELETE FROM media_holes_v2 WHERE uid = " + this.val$did).stepThis().dispose();
                BotQuery.clearBotKeyboard(this.val$did, null);
                AndroidUtilities.runOnUIThread(new C05761());
            } catch (Throwable e3) {
                FileLog.m18e("tmessages", e3);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MessagesStorage.22 */
    class AnonymousClass22 implements Runnable {
        final /* synthetic */ int val$classGuid;
        final /* synthetic */ int val$count;
        final /* synthetic */ int val$did;
        final /* synthetic */ long val$max_id;
        final /* synthetic */ int val$offset;

        /* renamed from: com.hanista.mobogram.messenger.MessagesStorage.22.1 */
        class C05771 implements Runnable {
            final /* synthetic */ photos_Photos val$res;

            C05771(photos_Photos com_hanista_mobogram_tgnet_TLRPC_photos_Photos) {
                this.val$res = com_hanista_mobogram_tgnet_TLRPC_photos_Photos;
            }

            public void run() {
                MessagesController.getInstance().processLoadedUserPhotos(this.val$res, AnonymousClass22.this.val$did, AnonymousClass22.this.val$offset, AnonymousClass22.this.val$count, AnonymousClass22.this.val$max_id, true, AnonymousClass22.this.val$classGuid);
            }
        }

        AnonymousClass22(long j, int i, int i2, int i3, int i4) {
            this.val$max_id = j;
            this.val$did = i;
            this.val$count = i2;
            this.val$offset = i3;
            this.val$classGuid = i4;
        }

        public void run() {
            try {
                SQLiteCursor queryFinalized;
                if (this.val$max_id != 0) {
                    queryFinalized = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT data FROM user_photos WHERE uid = %d AND id < %d ORDER BY id DESC LIMIT %d", new Object[]{Integer.valueOf(this.val$did), Long.valueOf(this.val$max_id), Integer.valueOf(this.val$count)}), new Object[0]);
                } else {
                    queryFinalized = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT data FROM user_photos WHERE uid = %d ORDER BY id DESC LIMIT %d,%d", new Object[]{Integer.valueOf(this.val$did), Integer.valueOf(this.val$offset), Integer.valueOf(this.val$count)}), new Object[0]);
                }
                photos_Photos com_hanista_mobogram_tgnet_TLRPC_photos_Photos = new photos_Photos();
                while (queryFinalized.next()) {
                    AbstractSerializedData byteBufferValue = queryFinalized.byteBufferValue(0);
                    if (byteBufferValue != null) {
                        Photo TLdeserialize = Photo.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                        byteBufferValue.reuse();
                        com_hanista_mobogram_tgnet_TLRPC_photos_Photos.photos.add(TLdeserialize);
                    }
                }
                queryFinalized.dispose();
                Utilities.stageQueue.postRunnable(new C05771(com_hanista_mobogram_tgnet_TLRPC_photos_Photos));
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MessagesStorage.23 */
    class AnonymousClass23 implements Runnable {
        final /* synthetic */ int val$uid;

        AnonymousClass23(int i) {
            this.val$uid = i;
        }

        public void run() {
            try {
                MessagesStorage.this.database.executeFast("DELETE FROM user_photos WHERE uid = " + this.val$uid).stepThis().dispose();
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MessagesStorage.24 */
    class AnonymousClass24 implements Runnable {
        final /* synthetic */ long val$pid;
        final /* synthetic */ int val$uid;

        AnonymousClass24(int i, long j) {
            this.val$uid = i;
            this.val$pid = j;
        }

        public void run() {
            try {
                MessagesStorage.this.database.executeFast("DELETE FROM user_photos WHERE uid = " + this.val$uid + " AND id = " + this.val$pid).stepThis().dispose();
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MessagesStorage.25 */
    class AnonymousClass25 implements Runnable {
        final /* synthetic */ int val$did;
        final /* synthetic */ photos_Photos val$photos;

        AnonymousClass25(photos_Photos com_hanista_mobogram_tgnet_TLRPC_photos_Photos, int i) {
            this.val$photos = com_hanista_mobogram_tgnet_TLRPC_photos_Photos;
            this.val$did = i;
        }

        public void run() {
            try {
                SQLitePreparedStatement executeFast = MessagesStorage.this.database.executeFast("REPLACE INTO user_photos VALUES(?, ?, ?)");
                Iterator it = this.val$photos.photos.iterator();
                while (it.hasNext()) {
                    Photo photo = (Photo) it.next();
                    if (!(photo instanceof TL_photoEmpty)) {
                        executeFast.requery();
                        NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(photo.getObjectSize());
                        photo.serializeToStream(nativeByteBuffer);
                        executeFast.bindInteger(1, this.val$did);
                        executeFast.bindLong(2, photo.id);
                        executeFast.bindByteBuffer(3, nativeByteBuffer);
                        executeFast.step();
                        nativeByteBuffer.reuse();
                    }
                }
                executeFast.dispose();
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MessagesStorage.26 */
    class AnonymousClass26 implements Runnable {
        final /* synthetic */ ArrayList val$oldTask;

        AnonymousClass26(ArrayList arrayList) {
            this.val$oldTask = arrayList;
        }

        public void run() {
            int i = 0;
            try {
                if (this.val$oldTask != null) {
                    String join = TextUtils.join(",", this.val$oldTask);
                    MessagesStorage.this.database.executeFast(String.format(Locale.US, "DELETE FROM enc_tasks_v2 WHERE mid IN(%s)", new Object[]{join})).stepThis().dispose();
                }
                ArrayList arrayList = null;
                SQLiteCursor queryFinalized = MessagesStorage.this.database.queryFinalized("SELECT mid, date FROM enc_tasks_v2 WHERE date = (SELECT min(date) FROM enc_tasks_v2)", new Object[0]);
                while (queryFinalized.next()) {
                    Integer valueOf = Integer.valueOf(queryFinalized.intValue(0));
                    i = queryFinalized.intValue(1);
                    if (arrayList == null) {
                        arrayList = new ArrayList();
                    }
                    arrayList.add(valueOf);
                }
                queryFinalized.dispose();
                MessagesController.getInstance().processLoadedDeleteTask(i, arrayList);
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MessagesStorage.27 */
    class AnonymousClass27 implements Runnable {
        final /* synthetic */ int val$chat_id;
        final /* synthetic */ int val$isOut;
        final /* synthetic */ ArrayList val$random_ids;
        final /* synthetic */ int val$readTime;
        final /* synthetic */ int val$time;

        /* renamed from: com.hanista.mobogram.messenger.MessagesStorage.27.1 */
        class C05781 implements Runnable {
            final /* synthetic */ ArrayList val$midsArray;

            C05781(ArrayList arrayList) {
                this.val$midsArray = arrayList;
            }

            public void run() {
                MessagesStorage.getInstance().markMessagesContentAsRead(this.val$midsArray);
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.messagesReadContent, this.val$midsArray);
            }
        }

        AnonymousClass27(ArrayList arrayList, int i, int i2, int i3, int i4) {
            this.val$random_ids = arrayList;
            this.val$chat_id = i;
            this.val$isOut = i2;
            this.val$time = i3;
            this.val$readTime = i4;
        }

        public void run() {
            try {
                int i;
                SQLiteCursor queryFinalized;
                int intValue;
                int i2;
                ArrayList arrayList;
                SparseArray sparseArray = new SparseArray();
                ArrayList arrayList2 = new ArrayList();
                StringBuilder stringBuilder = new StringBuilder();
                if (this.val$random_ids == null) {
                    i = ConnectionsManager.DEFAULT_DATACENTER_ID;
                    queryFinalized = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT mid, ttl FROM messages WHERE uid = %d AND out = %d AND read_state != 0 AND ttl > 0 AND date <= %d AND send_state = 0 AND media != 1", new Object[]{Long.valueOf(((long) this.val$chat_id) << 32), Integer.valueOf(this.val$isOut), Integer.valueOf(this.val$time)}), new Object[0]);
                } else {
                    String join = TextUtils.join(",", this.val$random_ids);
                    i = ConnectionsManager.DEFAULT_DATACENTER_ID;
                    queryFinalized = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT m.mid, m.ttl FROM messages as m INNER JOIN randoms as r ON m.mid = r.mid WHERE r.random_id IN (%s)", new Object[]{join}), new Object[0]);
                }
                while (queryFinalized.next()) {
                    intValue = queryFinalized.intValue(1);
                    int intValue2 = queryFinalized.intValue(0);
                    if (this.val$random_ids != null) {
                        arrayList2.add(Long.valueOf((long) intValue2));
                    }
                    if (intValue > 0) {
                        i2 = (this.val$time > this.val$readTime ? this.val$time : this.val$readTime) + intValue;
                        intValue = Math.min(i, i2);
                        arrayList = (ArrayList) sparseArray.get(i2);
                        if (arrayList == null) {
                            arrayList = new ArrayList();
                            sparseArray.put(i2, arrayList);
                        }
                        if (stringBuilder.length() != 0) {
                            stringBuilder.append(",");
                        }
                        stringBuilder.append(intValue2);
                        arrayList.add(Integer.valueOf(intValue2));
                        i = intValue;
                    }
                }
                queryFinalized.dispose();
                if (this.val$random_ids != null) {
                    AndroidUtilities.runOnUIThread(new C05781(arrayList2));
                }
                if (sparseArray.size() != 0) {
                    MessagesStorage.this.database.beginTransaction();
                    SQLitePreparedStatement executeFast = MessagesStorage.this.database.executeFast("REPLACE INTO enc_tasks_v2 VALUES(?, ?)");
                    for (int i3 = 0; i3 < sparseArray.size(); i3++) {
                        i2 = sparseArray.keyAt(i3);
                        arrayList = (ArrayList) sparseArray.get(i2);
                        for (intValue = 0; intValue < arrayList.size(); intValue++) {
                            executeFast.requery();
                            executeFast.bindInteger(1, ((Integer) arrayList.get(intValue)).intValue());
                            executeFast.bindInteger(2, i2);
                            executeFast.step();
                        }
                    }
                    executeFast.dispose();
                    MessagesStorage.this.database.commitTransaction();
                    MessagesStorage.this.database.executeFast(String.format(Locale.US, "UPDATE messages SET ttl = 0 WHERE mid IN(%s)", new Object[]{stringBuilder.toString()})).stepThis().dispose();
                    MessagesController.getInstance().didAddedNewTask(i, sparseArray);
                }
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MessagesStorage.28 */
    class AnonymousClass28 implements Runnable {
        final /* synthetic */ SparseArray val$inbox;
        final /* synthetic */ SparseArray val$outbox;

        AnonymousClass28(SparseArray sparseArray, SparseArray sparseArray2) {
            this.val$inbox = sparseArray;
            this.val$outbox = sparseArray2;
        }

        public void run() {
            MessagesStorage.this.updateDialogsWithReadMessagesInternal(null, this.val$inbox, this.val$outbox);
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MessagesStorage.29 */
    class AnonymousClass29 implements Runnable {
        final /* synthetic */ ChatParticipants val$participants;

        /* renamed from: com.hanista.mobogram.messenger.MessagesStorage.29.1 */
        class C05791 implements Runnable {
            final /* synthetic */ ChatFull val$finalInfo;

            C05791(ChatFull chatFull) {
                this.val$finalInfo = chatFull;
            }

            public void run() {
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.chatInfoDidLoaded, this.val$finalInfo, Integer.valueOf(0), Boolean.valueOf(false), null);
            }
        }

        AnonymousClass29(ChatParticipants chatParticipants) {
            this.val$participants = chatParticipants;
        }

        public void run() {
            try {
                SQLiteCursor queryFinalized = MessagesStorage.this.database.queryFinalized("SELECT info, pinned FROM chat_settings_v2 WHERE uid = " + this.val$participants.chat_id, new Object[0]);
                ChatFull chatFull = null;
                ArrayList arrayList = new ArrayList();
                if (queryFinalized.next()) {
                    AbstractSerializedData byteBufferValue = queryFinalized.byteBufferValue(0);
                    if (byteBufferValue != null) {
                        chatFull = ChatFull.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                        byteBufferValue.reuse();
                        chatFull.pinned_msg_id = queryFinalized.intValue(1);
                    }
                }
                queryFinalized.dispose();
                if (chatFull instanceof TL_chatFull) {
                    chatFull.participants = this.val$participants;
                    AndroidUtilities.runOnUIThread(new C05791(chatFull));
                    SQLitePreparedStatement executeFast = MessagesStorage.this.database.executeFast("REPLACE INTO chat_settings_v2 VALUES(?, ?, ?)");
                    NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(chatFull.getObjectSize());
                    chatFull.serializeToStream(nativeByteBuffer);
                    executeFast.bindInteger(1, chatFull.id);
                    executeFast.bindByteBuffer(2, nativeByteBuffer);
                    executeFast.bindInteger(3, chatFull.pinned_msg_id);
                    executeFast.step();
                    executeFast.dispose();
                    nativeByteBuffer.reuse();
                }
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MessagesStorage.2 */
    class C05802 implements Runnable {
        final /* synthetic */ boolean val$isLogin;

        /* renamed from: com.hanista.mobogram.messenger.MessagesStorage.2.1 */
        class C05731 implements Runnable {
            C05731() {
            }

            public void run() {
                MessagesController.getInstance().getDifference();
            }
        }

        C05802(boolean z) {
            this.val$isLogin = z;
        }

        public void run() {
            MessagesStorage.lastDateValue = 0;
            MessagesStorage.lastSeqValue = 0;
            MessagesStorage.lastPtsValue = 0;
            MessagesStorage.lastQtsValue = 0;
            MessagesStorage.lastSecretVersion = 0;
            MessagesStorage.this.lastSavedSeq = 0;
            MessagesStorage.this.lastSavedPts = 0;
            MessagesStorage.this.lastSavedDate = 0;
            MessagesStorage.this.lastSavedQts = 0;
            MessagesStorage.secretPBytes = null;
            MessagesStorage.secretG = 0;
            if (MessagesStorage.this.database != null) {
                MessagesStorage.this.database.close();
                MessagesStorage.this.database = null;
            }
            if (MessagesStorage.this.cacheFile != null) {
                MessagesStorage.this.cacheFile.delete();
                MessagesStorage.this.cacheFile = null;
            }
            MessagesStorage.this.openDatabase();
            if (this.val$isLogin) {
                Utilities.stageQueue.postRunnable(new C05731());
            }
            DownloadMessagesStorage.m783a().m817a(this.val$isLogin);
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MessagesStorage.30 */
    class AnonymousClass30 implements Runnable {
        final /* synthetic */ int val$channel_id;
        final /* synthetic */ ArrayList val$participants;

        AnonymousClass30(int i, ArrayList arrayList) {
            this.val$channel_id = i;
            this.val$participants = arrayList;
        }

        public void run() {
            try {
                long j = (long) (-this.val$channel_id);
                MessagesStorage.this.database.executeFast("DELETE FROM channel_users_v2 WHERE did = " + j).stepThis().dispose();
                MessagesStorage.this.database.beginTransaction();
                SQLitePreparedStatement executeFast = MessagesStorage.this.database.executeFast("REPLACE INTO channel_users_v2 VALUES(?, ?, ?, ?)");
                int currentTimeMillis = (int) (System.currentTimeMillis() / 1000);
                for (int i = 0; i < this.val$participants.size(); i++) {
                    ChannelParticipant channelParticipant = (ChannelParticipant) this.val$participants.get(i);
                    executeFast.requery();
                    executeFast.bindLong(1, j);
                    executeFast.bindInteger(2, channelParticipant.user_id);
                    executeFast.bindInteger(3, currentTimeMillis);
                    NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(channelParticipant.getObjectSize());
                    channelParticipant.serializeToStream(nativeByteBuffer);
                    executeFast.bindByteBuffer(4, nativeByteBuffer);
                    nativeByteBuffer.reuse();
                    executeFast.step();
                    currentTimeMillis--;
                }
                executeFast.dispose();
                MessagesStorage.this.database.commitTransaction();
                MessagesStorage.this.loadChatInfo(this.val$channel_id, null, false, true);
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MessagesStorage.31 */
    class AnonymousClass31 implements Runnable {
        final /* synthetic */ boolean val$ifExist;
        final /* synthetic */ ChatFull val$info;

        AnonymousClass31(boolean z, ChatFull chatFull) {
            this.val$ifExist = z;
            this.val$info = chatFull;
        }

        public void run() {
            try {
                SQLiteCursor queryFinalized;
                if (this.val$ifExist) {
                    queryFinalized = MessagesStorage.this.database.queryFinalized("SELECT uid FROM chat_settings_v2 WHERE uid = " + this.val$info.id, new Object[0]);
                    boolean next = queryFinalized.next();
                    queryFinalized.dispose();
                    if (!next) {
                        return;
                    }
                }
                SQLitePreparedStatement executeFast = MessagesStorage.this.database.executeFast("REPLACE INTO chat_settings_v2 VALUES(?, ?, ?)");
                NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(this.val$info.getObjectSize());
                this.val$info.serializeToStream(nativeByteBuffer);
                executeFast.bindInteger(1, this.val$info.id);
                executeFast.bindByteBuffer(2, nativeByteBuffer);
                executeFast.bindInteger(3, this.val$info.pinned_msg_id);
                executeFast.step();
                executeFast.dispose();
                nativeByteBuffer.reuse();
                if (this.val$info instanceof TL_channelFull) {
                    queryFinalized = MessagesStorage.this.database.queryFinalized("SELECT date, pts, last_mid, inbox_max, outbox_max FROM dialogs WHERE did = " + (-this.val$info.id), new Object[0]);
                    if (queryFinalized.next()) {
                        int intValue = queryFinalized.intValue(3);
                        if (intValue <= this.val$info.read_inbox_max_id) {
                            intValue = this.val$info.read_inbox_max_id - intValue;
                            if (intValue < this.val$info.unread_count) {
                                this.val$info.unread_count = intValue;
                            }
                            intValue = queryFinalized.intValue(0);
                            int intValue2 = queryFinalized.intValue(1);
                            long longValue = queryFinalized.longValue(2);
                            int intValue3 = queryFinalized.intValue(4);
                            SQLitePreparedStatement executeFast2 = MessagesStorage.this.database.executeFast("REPLACE INTO dialogs VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                            executeFast2.bindLong(1, (long) (-this.val$info.id));
                            executeFast2.bindInteger(2, intValue);
                            executeFast2.bindInteger(3, this.val$info.unread_count);
                            executeFast2.bindLong(4, longValue);
                            executeFast2.bindInteger(5, this.val$info.read_inbox_max_id);
                            executeFast2.bindInteger(6, Math.max(intValue3, this.val$info.read_outbox_max_id));
                            executeFast2.bindLong(7, 0);
                            executeFast2.bindInteger(8, 0);
                            executeFast2.bindInteger(9, intValue2);
                            executeFast2.bindInteger(10, 0);
                            executeFast2.step();
                            executeFast2.dispose();
                        }
                    }
                    queryFinalized.dispose();
                }
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MessagesStorage.32 */
    class AnonymousClass32 implements Runnable {
        final /* synthetic */ int val$channelId;
        final /* synthetic */ int val$messageId;

        /* renamed from: com.hanista.mobogram.messenger.MessagesStorage.32.1 */
        class C05811 implements Runnable {
            final /* synthetic */ ChatFull val$finalInfo;

            C05811(ChatFull chatFull) {
                this.val$finalInfo = chatFull;
            }

            public void run() {
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.chatInfoDidLoaded, this.val$finalInfo, Integer.valueOf(0), Boolean.valueOf(false), null);
            }
        }

        AnonymousClass32(int i, int i2) {
            this.val$channelId = i;
            this.val$messageId = i2;
        }

        public void run() {
            try {
                SQLiteCursor queryFinalized = MessagesStorage.this.database.queryFinalized("SELECT info, pinned FROM chat_settings_v2 WHERE uid = " + this.val$channelId, new Object[0]);
                ChatFull chatFull = null;
                ArrayList arrayList = new ArrayList();
                if (queryFinalized.next()) {
                    AbstractSerializedData byteBufferValue = queryFinalized.byteBufferValue(0);
                    if (byteBufferValue != null) {
                        chatFull = ChatFull.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                        byteBufferValue.reuse();
                        chatFull.pinned_msg_id = queryFinalized.intValue(1);
                    }
                }
                queryFinalized.dispose();
                if (chatFull instanceof TL_channelFull) {
                    chatFull.pinned_msg_id = this.val$messageId;
                    chatFull.flags |= 32;
                    AndroidUtilities.runOnUIThread(new C05811(chatFull));
                    SQLitePreparedStatement executeFast = MessagesStorage.this.database.executeFast("REPLACE INTO chat_settings_v2 VALUES(?, ?, ?)");
                    NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(chatFull.getObjectSize());
                    chatFull.serializeToStream(nativeByteBuffer);
                    executeFast.bindInteger(1, this.val$channelId);
                    executeFast.bindByteBuffer(2, nativeByteBuffer);
                    executeFast.bindInteger(3, chatFull.pinned_msg_id);
                    executeFast.step();
                    executeFast.dispose();
                    nativeByteBuffer.reuse();
                }
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MessagesStorage.33 */
    class AnonymousClass33 implements Runnable {
        final /* synthetic */ int val$chat_id;
        final /* synthetic */ int val$invited_id;
        final /* synthetic */ int val$user_id;
        final /* synthetic */ int val$version;
        final /* synthetic */ int val$what;

        /* renamed from: com.hanista.mobogram.messenger.MessagesStorage.33.1 */
        class C05821 implements Runnable {
            final /* synthetic */ ChatFull val$finalInfo;

            C05821(ChatFull chatFull) {
                this.val$finalInfo = chatFull;
            }

            public void run() {
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.chatInfoDidLoaded, this.val$finalInfo, Integer.valueOf(0), Boolean.valueOf(false), null);
            }
        }

        AnonymousClass33(int i, int i2, int i3, int i4, int i5) {
            this.val$chat_id = i;
            this.val$what = i2;
            this.val$user_id = i3;
            this.val$invited_id = i4;
            this.val$version = i5;
        }

        public void run() {
            try {
                ChatFull chatFull;
                int i;
                Iterator it;
                TL_chatParticipant tL_chatParticipant;
                int i2;
                ChatParticipant chatParticipant;
                ChatParticipant tL_chatParticipantAdmin;
                Object obj;
                SQLitePreparedStatement executeFast;
                NativeByteBuffer nativeByteBuffer;
                SQLiteCursor queryFinalized = MessagesStorage.this.database.queryFinalized("SELECT info, pinned FROM chat_settings_v2 WHERE uid = " + this.val$chat_id, new Object[0]);
                ArrayList arrayList = new ArrayList();
                if (queryFinalized.next()) {
                    AbstractSerializedData byteBufferValue = queryFinalized.byteBufferValue(0);
                    if (byteBufferValue != null) {
                        ChatFull TLdeserialize = ChatFull.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                        byteBufferValue.reuse();
                        TLdeserialize.pinned_msg_id = queryFinalized.intValue(1);
                        chatFull = TLdeserialize;
                        queryFinalized.dispose();
                        if (chatFull instanceof TL_chatFull) {
                            if (this.val$what == 1) {
                                for (i = 0; i < chatFull.participants.participants.size(); i++) {
                                    if (((ChatParticipant) chatFull.participants.participants.get(i)).user_id == this.val$user_id) {
                                        chatFull.participants.participants.remove(i);
                                        break;
                                    }
                                }
                            } else if (this.val$what == 0) {
                                it = chatFull.participants.participants.iterator();
                                while (it.hasNext()) {
                                    if (((ChatParticipant) it.next()).user_id == this.val$user_id) {
                                        return;
                                    }
                                }
                                tL_chatParticipant = new TL_chatParticipant();
                                tL_chatParticipant.user_id = this.val$user_id;
                                tL_chatParticipant.inviter_id = this.val$invited_id;
                                tL_chatParticipant.date = ConnectionsManager.getInstance().getCurrentTime();
                                chatFull.participants.participants.add(tL_chatParticipant);
                            } else if (this.val$what == 2) {
                                i2 = 0;
                                while (i2 < chatFull.participants.participants.size()) {
                                    chatParticipant = (ChatParticipant) chatFull.participants.participants.get(i2);
                                    if (chatParticipant.user_id != this.val$user_id) {
                                        if (this.val$invited_id != 1) {
                                            tL_chatParticipantAdmin = new TL_chatParticipantAdmin();
                                            tL_chatParticipantAdmin.user_id = chatParticipant.user_id;
                                            tL_chatParticipantAdmin.date = chatParticipant.date;
                                            tL_chatParticipantAdmin.inviter_id = chatParticipant.inviter_id;
                                            obj = tL_chatParticipantAdmin;
                                        } else {
                                            tL_chatParticipantAdmin = new TL_chatParticipant();
                                            tL_chatParticipantAdmin.user_id = chatParticipant.user_id;
                                            tL_chatParticipantAdmin.date = chatParticipant.date;
                                            tL_chatParticipantAdmin.inviter_id = chatParticipant.inviter_id;
                                            chatParticipant = tL_chatParticipantAdmin;
                                        }
                                        chatFull.participants.participants.set(i2, obj);
                                    } else {
                                        i2++;
                                    }
                                }
                            }
                            chatFull.participants.version = this.val$version;
                            AndroidUtilities.runOnUIThread(new C05821(chatFull));
                            executeFast = MessagesStorage.this.database.executeFast("REPLACE INTO chat_settings_v2 VALUES(?, ?, ?)");
                            nativeByteBuffer = new NativeByteBuffer(chatFull.getObjectSize());
                            chatFull.serializeToStream(nativeByteBuffer);
                            executeFast.bindInteger(1, this.val$chat_id);
                            executeFast.bindByteBuffer(2, nativeByteBuffer);
                            executeFast.bindInteger(3, chatFull.pinned_msg_id);
                            executeFast.step();
                            executeFast.dispose();
                            nativeByteBuffer.reuse();
                        }
                    }
                }
                chatFull = null;
                queryFinalized.dispose();
                if (chatFull instanceof TL_chatFull) {
                    if (this.val$what == 1) {
                        for (i = 0; i < chatFull.participants.participants.size(); i++) {
                            if (((ChatParticipant) chatFull.participants.participants.get(i)).user_id == this.val$user_id) {
                                chatFull.participants.participants.remove(i);
                                break;
                            }
                        }
                    } else if (this.val$what == 0) {
                        it = chatFull.participants.participants.iterator();
                        while (it.hasNext()) {
                            if (((ChatParticipant) it.next()).user_id == this.val$user_id) {
                                return;
                            }
                        }
                        tL_chatParticipant = new TL_chatParticipant();
                        tL_chatParticipant.user_id = this.val$user_id;
                        tL_chatParticipant.inviter_id = this.val$invited_id;
                        tL_chatParticipant.date = ConnectionsManager.getInstance().getCurrentTime();
                        chatFull.participants.participants.add(tL_chatParticipant);
                    } else if (this.val$what == 2) {
                        i2 = 0;
                        while (i2 < chatFull.participants.participants.size()) {
                            chatParticipant = (ChatParticipant) chatFull.participants.participants.get(i2);
                            if (chatParticipant.user_id != this.val$user_id) {
                                i2++;
                            } else {
                                if (this.val$invited_id != 1) {
                                    tL_chatParticipantAdmin = new TL_chatParticipant();
                                    tL_chatParticipantAdmin.user_id = chatParticipant.user_id;
                                    tL_chatParticipantAdmin.date = chatParticipant.date;
                                    tL_chatParticipantAdmin.inviter_id = chatParticipant.inviter_id;
                                    chatParticipant = tL_chatParticipantAdmin;
                                } else {
                                    tL_chatParticipantAdmin = new TL_chatParticipantAdmin();
                                    tL_chatParticipantAdmin.user_id = chatParticipant.user_id;
                                    tL_chatParticipantAdmin.date = chatParticipant.date;
                                    tL_chatParticipantAdmin.inviter_id = chatParticipant.inviter_id;
                                    obj = tL_chatParticipantAdmin;
                                }
                                chatFull.participants.participants.set(i2, obj);
                            }
                        }
                    }
                    chatFull.participants.version = this.val$version;
                    AndroidUtilities.runOnUIThread(new C05821(chatFull));
                    executeFast = MessagesStorage.this.database.executeFast("REPLACE INTO chat_settings_v2 VALUES(?, ?, ?)");
                    nativeByteBuffer = new NativeByteBuffer(chatFull.getObjectSize());
                    chatFull.serializeToStream(nativeByteBuffer);
                    executeFast.bindInteger(1, this.val$chat_id);
                    executeFast.bindByteBuffer(2, nativeByteBuffer);
                    executeFast.bindInteger(3, chatFull.pinned_msg_id);
                    executeFast.step();
                    executeFast.dispose();
                    nativeByteBuffer.reuse();
                }
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MessagesStorage.34 */
    class AnonymousClass34 implements Runnable {
        final /* synthetic */ int val$chat_id;
        final /* synthetic */ boolean[] val$result;
        final /* synthetic */ Semaphore val$semaphore;

        AnonymousClass34(int i, boolean[] zArr, Semaphore semaphore) {
            this.val$chat_id = i;
            this.val$result = zArr;
            this.val$semaphore = semaphore;
        }

        public void run() {
            boolean z = false;
            try {
                SQLiteCursor queryFinalized = MessagesStorage.this.database.queryFinalized("SELECT info FROM chat_settings_v2 WHERE uid = " + this.val$chat_id, new Object[0]);
                ChatFull chatFull = null;
                ArrayList arrayList = new ArrayList();
                if (queryFinalized.next()) {
                    AbstractSerializedData byteBufferValue = queryFinalized.byteBufferValue(0);
                    if (byteBufferValue != null) {
                        chatFull = ChatFull.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                        byteBufferValue.reuse();
                    }
                }
                queryFinalized.dispose();
                boolean[] zArr = this.val$result;
                if ((chatFull instanceof TL_channelFull) && chatFull.migrated_from_chat_id != 0) {
                    z = true;
                }
                zArr[0] = z;
                if (this.val$semaphore != null) {
                    this.val$semaphore.release();
                }
                if (this.val$semaphore != null) {
                    this.val$semaphore.release();
                }
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
                if (this.val$semaphore != null) {
                    this.val$semaphore.release();
                }
            } catch (Throwable th) {
                if (this.val$semaphore != null) {
                    this.val$semaphore.release();
                }
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MessagesStorage.35 */
    class AnonymousClass35 implements Runnable {
        final /* synthetic */ boolean val$byChannelUsers;
        final /* synthetic */ int val$chat_id;
        final /* synthetic */ boolean val$force;
        final /* synthetic */ Semaphore val$semaphore;

        AnonymousClass35(int i, Semaphore semaphore, boolean z, boolean z2) {
            this.val$chat_id = i;
            this.val$semaphore = semaphore;
            this.val$force = z;
            this.val$byChannelUsers = z2;
        }

        public void run() {
            Throwable e;
            Throwable th;
            MessageObject messageObject = null;
            int i = 0;
            ArrayList arrayList = new ArrayList();
            ChatFull TLdeserialize;
            try {
                AbstractSerializedData byteBufferValue;
                StringBuilder stringBuilder;
                ChatParticipant chatParticipant;
                SQLiteCursor queryFinalized;
                User TLdeserialize2;
                User user;
                AbstractSerializedData byteBufferValue2;
                ChannelParticipant TLdeserialize3;
                TL_chatChannelParticipant tL_chatChannelParticipant;
                BotInfo botInfo;
                SQLiteCursor queryFinalized2 = MessagesStorage.this.database.queryFinalized("SELECT info, pinned FROM chat_settings_v2 WHERE uid = " + this.val$chat_id, new Object[0]);
                if (queryFinalized2.next()) {
                    byteBufferValue = queryFinalized2.byteBufferValue(0);
                    if (byteBufferValue != null) {
                        TLdeserialize = ChatFull.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                        try {
                            byteBufferValue.reuse();
                            TLdeserialize.pinned_msg_id = queryFinalized2.intValue(1);
                            queryFinalized2.dispose();
                            if (TLdeserialize instanceof TL_chatFull) {
                                stringBuilder = new StringBuilder();
                                while (i < TLdeserialize.participants.participants.size()) {
                                    chatParticipant = (ChatParticipant) TLdeserialize.participants.participants.get(i);
                                    if (stringBuilder.length() != 0) {
                                        stringBuilder.append(",");
                                    }
                                    stringBuilder.append(chatParticipant.user_id);
                                    i++;
                                }
                                if (stringBuilder.length() != 0) {
                                    MessagesStorage.this.getUsersInternal(stringBuilder.toString(), arrayList);
                                }
                            } else if (TLdeserialize instanceof TL_channelFull) {
                                queryFinalized = MessagesStorage.this.database.queryFinalized("SELECT us.data, us.status, cu.data, cu.date FROM channel_users_v2 as cu LEFT JOIN users as us ON us.uid = cu.uid WHERE cu.did = " + (-this.val$chat_id) + " ORDER BY cu.date DESC", new Object[0]);
                                TLdeserialize.participants = new TL_chatParticipants();
                                while (queryFinalized.next()) {
                                    try {
                                        byteBufferValue = queryFinalized.byteBufferValue(0);
                                        if (byteBufferValue == null) {
                                            TLdeserialize2 = User.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                                            byteBufferValue.reuse();
                                            user = TLdeserialize2;
                                        } else {
                                            user = null;
                                        }
                                        byteBufferValue2 = queryFinalized.byteBufferValue(2);
                                        if (byteBufferValue2 == null) {
                                            TLdeserialize3 = ChannelParticipant.TLdeserialize(byteBufferValue2, byteBufferValue2.readInt32(false), false);
                                            byteBufferValue2.reuse();
                                        } else {
                                            TLdeserialize3 = null;
                                        }
                                        if (!(user == null || TLdeserialize3 == null)) {
                                            if (user.status != null) {
                                                user.status.expires = queryFinalized.intValue(1);
                                            }
                                            arrayList.add(user);
                                            TLdeserialize3.date = queryFinalized.intValue(3);
                                            tL_chatChannelParticipant = new TL_chatChannelParticipant();
                                            tL_chatChannelParticipant.user_id = TLdeserialize3.user_id;
                                            tL_chatChannelParticipant.date = TLdeserialize3.date;
                                            tL_chatChannelParticipant.inviter_id = TLdeserialize3.inviter_id;
                                            tL_chatChannelParticipant.channelParticipant = TLdeserialize3;
                                            TLdeserialize.participants.participants.add(tL_chatChannelParticipant);
                                        }
                                    } catch (Throwable e2) {
                                        FileLog.m18e("tmessages", e2);
                                    }
                                }
                                queryFinalized.dispose();
                                stringBuilder = new StringBuilder();
                                while (i < TLdeserialize.bot_info.size()) {
                                    botInfo = (BotInfo) TLdeserialize.bot_info.get(i);
                                    if (stringBuilder.length() != 0) {
                                        stringBuilder.append(",");
                                    }
                                    stringBuilder.append(botInfo.user_id);
                                    i++;
                                }
                                if (stringBuilder.length() != 0) {
                                    MessagesStorage.this.getUsersInternal(stringBuilder.toString(), arrayList);
                                }
                            }
                            if (this.val$semaphore != null) {
                                this.val$semaphore.release();
                            }
                            if ((TLdeserialize instanceof TL_channelFull) && TLdeserialize.pinned_msg_id != 0) {
                                messageObject = MessagesQuery.loadPinnedMessage(this.val$chat_id, TLdeserialize.pinned_msg_id, false);
                            }
                            MessagesController.getInstance().processChatInfo(this.val$chat_id, TLdeserialize, arrayList, true, this.val$force, this.val$byChannelUsers, messageObject);
                            if (this.val$semaphore != null) {
                                this.val$semaphore.release();
                            }
                        } catch (Exception e3) {
                            e2 = e3;
                        }
                    }
                }
                TLdeserialize = null;
                queryFinalized2.dispose();
                if (TLdeserialize instanceof TL_chatFull) {
                    stringBuilder = new StringBuilder();
                    while (i < TLdeserialize.participants.participants.size()) {
                        chatParticipant = (ChatParticipant) TLdeserialize.participants.participants.get(i);
                        if (stringBuilder.length() != 0) {
                            stringBuilder.append(",");
                        }
                        stringBuilder.append(chatParticipant.user_id);
                        i++;
                    }
                    if (stringBuilder.length() != 0) {
                        MessagesStorage.this.getUsersInternal(stringBuilder.toString(), arrayList);
                    }
                } else if (TLdeserialize instanceof TL_channelFull) {
                    queryFinalized = MessagesStorage.this.database.queryFinalized("SELECT us.data, us.status, cu.data, cu.date FROM channel_users_v2 as cu LEFT JOIN users as us ON us.uid = cu.uid WHERE cu.did = " + (-this.val$chat_id) + " ORDER BY cu.date DESC", new Object[0]);
                    TLdeserialize.participants = new TL_chatParticipants();
                    while (queryFinalized.next()) {
                        byteBufferValue = queryFinalized.byteBufferValue(0);
                        if (byteBufferValue == null) {
                            user = null;
                        } else {
                            TLdeserialize2 = User.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                            byteBufferValue.reuse();
                            user = TLdeserialize2;
                        }
                        byteBufferValue2 = queryFinalized.byteBufferValue(2);
                        if (byteBufferValue2 == null) {
                            TLdeserialize3 = null;
                        } else {
                            TLdeserialize3 = ChannelParticipant.TLdeserialize(byteBufferValue2, byteBufferValue2.readInt32(false), false);
                            byteBufferValue2.reuse();
                        }
                        if (user.status != null) {
                            user.status.expires = queryFinalized.intValue(1);
                        }
                        arrayList.add(user);
                        TLdeserialize3.date = queryFinalized.intValue(3);
                        tL_chatChannelParticipant = new TL_chatChannelParticipant();
                        tL_chatChannelParticipant.user_id = TLdeserialize3.user_id;
                        tL_chatChannelParticipant.date = TLdeserialize3.date;
                        tL_chatChannelParticipant.inviter_id = TLdeserialize3.inviter_id;
                        tL_chatChannelParticipant.channelParticipant = TLdeserialize3;
                        TLdeserialize.participants.participants.add(tL_chatChannelParticipant);
                    }
                    queryFinalized.dispose();
                    stringBuilder = new StringBuilder();
                    while (i < TLdeserialize.bot_info.size()) {
                        botInfo = (BotInfo) TLdeserialize.bot_info.get(i);
                        if (stringBuilder.length() != 0) {
                            stringBuilder.append(",");
                        }
                        stringBuilder.append(botInfo.user_id);
                        i++;
                    }
                    if (stringBuilder.length() != 0) {
                        MessagesStorage.this.getUsersInternal(stringBuilder.toString(), arrayList);
                    }
                }
                if (this.val$semaphore != null) {
                    this.val$semaphore.release();
                }
                messageObject = MessagesQuery.loadPinnedMessage(this.val$chat_id, TLdeserialize.pinned_msg_id, false);
                MessagesController.getInstance().processChatInfo(this.val$chat_id, TLdeserialize, arrayList, true, this.val$force, this.val$byChannelUsers, messageObject);
                if (this.val$semaphore != null) {
                    this.val$semaphore.release();
                }
            } catch (Exception e4) {
                e2 = e4;
                TLdeserialize = null;
                try {
                    FileLog.m18e("tmessages", e2);
                    MessagesController.getInstance().processChatInfo(this.val$chat_id, TLdeserialize, arrayList, true, this.val$force, this.val$byChannelUsers, null);
                    if (this.val$semaphore != null) {
                        this.val$semaphore.release();
                    }
                } catch (Throwable e22) {
                    th = e22;
                    MessagesController.getInstance().processChatInfo(this.val$chat_id, TLdeserialize, arrayList, true, this.val$force, this.val$byChannelUsers, null);
                    if (this.val$semaphore != null) {
                        this.val$semaphore.release();
                    }
                    throw th;
                }
            } catch (Throwable e222) {
                th = e222;
                TLdeserialize = null;
                MessagesController.getInstance().processChatInfo(this.val$chat_id, TLdeserialize, arrayList, true, this.val$force, this.val$byChannelUsers, null);
                if (this.val$semaphore != null) {
                    this.val$semaphore.release();
                }
                throw th;
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MessagesStorage.36 */
    class AnonymousClass36 implements Runnable {
        final /* synthetic */ long val$dialog_id;
        final /* synthetic */ int val$max_date;
        final /* synthetic */ long val$max_id;

        AnonymousClass36(long j, long j2, int i) {
            this.val$dialog_id = j;
            this.val$max_id = j2;
            this.val$max_date = i;
        }

        public void run() {
            int i = 0;
            try {
                SQLitePreparedStatement executeFast;
                MessagesStorage.this.database.beginTransaction();
                if (((int) this.val$dialog_id) != 0) {
                    executeFast = MessagesStorage.this.database.executeFast("UPDATE messages SET read_state = read_state | 1 WHERE uid = ? AND mid <= ? AND read_state IN(0,2) AND out = 0");
                    executeFast.requery();
                    executeFast.bindLong(1, this.val$dialog_id);
                    executeFast.bindLong(2, this.val$max_id);
                    executeFast.step();
                    executeFast.dispose();
                } else {
                    executeFast = MessagesStorage.this.database.executeFast("UPDATE messages SET read_state = read_state | 1 WHERE uid = ? AND date <= ? AND read_state IN(0,2) AND out = 0");
                    executeFast.requery();
                    executeFast.bindLong(1, this.val$dialog_id);
                    executeFast.bindInteger(2, this.val$max_date);
                    executeFast.step();
                    executeFast.dispose();
                }
                SQLiteCursor queryFinalized = MessagesStorage.this.database.queryFinalized("SELECT inbox_max FROM dialogs WHERE did = " + this.val$dialog_id, new Object[0]);
                if (queryFinalized.next()) {
                    i = queryFinalized.intValue(0);
                }
                queryFinalized.dispose();
                i = Math.max(i, (int) this.val$max_id);
                executeFast = MessagesStorage.this.database.executeFast("UPDATE dialogs SET unread_count = 0, unread_count_i = 0, inbox_max = ? WHERE did = ?");
                executeFast.requery();
                executeFast.bindInteger(1, i);
                executeFast.bindLong(2, this.val$dialog_id);
                executeFast.step();
                executeFast.dispose();
                MessagesStorage.this.database.commitTransaction();
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MessagesStorage.37 */
    class AnonymousClass37 implements Runnable {
        final /* synthetic */ ArrayList val$contactsCopy;
        final /* synthetic */ boolean val$deleteAll;

        AnonymousClass37(boolean z, ArrayList arrayList) {
            this.val$deleteAll = z;
            this.val$contactsCopy = arrayList;
        }

        public void run() {
            try {
                if (this.val$deleteAll) {
                    MessagesStorage.this.database.executeFast("DELETE FROM contacts WHERE 1").stepThis().dispose();
                }
                MessagesStorage.this.database.beginTransaction();
                SQLitePreparedStatement executeFast = MessagesStorage.this.database.executeFast("REPLACE INTO contacts VALUES(?, ?)");
                for (int i = 0; i < this.val$contactsCopy.size(); i++) {
                    TL_contact tL_contact = (TL_contact) this.val$contactsCopy.get(i);
                    executeFast.requery();
                    executeFast.bindInteger(1, tL_contact.user_id);
                    executeFast.bindInteger(2, tL_contact.mutual ? 1 : 0);
                    executeFast.step();
                }
                executeFast.dispose();
                MessagesStorage.this.database.commitTransaction();
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MessagesStorage.38 */
    class AnonymousClass38 implements Runnable {
        final /* synthetic */ ArrayList val$uids;

        AnonymousClass38(ArrayList arrayList) {
            this.val$uids = arrayList;
        }

        public void run() {
            try {
                MessagesStorage.this.database.executeFast("DELETE FROM contacts WHERE uid IN(" + TextUtils.join(",", this.val$uids) + ")").stepThis().dispose();
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MessagesStorage.39 */
    class AnonymousClass39 implements Runnable {
        final /* synthetic */ String val$adds;
        final /* synthetic */ String val$deletes;

        AnonymousClass39(String str, String str2) {
            this.val$adds = str;
            this.val$deletes = str2;
        }

        public void run() {
            try {
                if (this.val$adds.length() != 0) {
                    MessagesStorage.this.database.executeFast(String.format(Locale.US, "UPDATE user_phones_v6 SET deleted = 0 WHERE sphone IN(%s)", new Object[]{this.val$adds})).stepThis().dispose();
                }
                if (this.val$deletes.length() != 0) {
                    MessagesStorage.this.database.executeFast(String.format(Locale.US, "UPDATE user_phones_v6 SET deleted = 1 WHERE sphone IN(%s)", new Object[]{this.val$deletes})).stepThis().dispose();
                }
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MessagesStorage.3 */
    class C05833 implements Runnable {
        final /* synthetic */ int val$lsv;
        final /* synthetic */ byte[] val$pbytes;
        final /* synthetic */ int val$sg;

        C05833(int i, int i2, byte[] bArr) {
            this.val$lsv = i;
            this.val$sg = i2;
            this.val$pbytes = bArr;
        }

        public void run() {
            int i = 1;
            try {
                SQLitePreparedStatement executeFast = MessagesStorage.this.database.executeFast("UPDATE params SET lsv = ?, sg = ?, pbytes = ? WHERE id = 1");
                executeFast.bindInteger(1, this.val$lsv);
                executeFast.bindInteger(2, this.val$sg);
                if (this.val$pbytes != null) {
                    i = this.val$pbytes.length;
                }
                NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(i);
                if (this.val$pbytes != null) {
                    nativeByteBuffer.writeBytes(this.val$pbytes);
                }
                executeFast.bindByteBuffer(3, nativeByteBuffer);
                executeFast.step();
                executeFast.dispose();
                nativeByteBuffer.reuse();
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MessagesStorage.40 */
    class AnonymousClass40 implements Runnable {
        final /* synthetic */ HashMap val$contactHashMap;

        AnonymousClass40(HashMap hashMap) {
            this.val$contactHashMap = hashMap;
        }

        public void run() {
            try {
                MessagesStorage.this.database.beginTransaction();
                SQLitePreparedStatement executeFast = MessagesStorage.this.database.executeFast("REPLACE INTO user_contacts_v6 VALUES(?, ?, ?)");
                SQLitePreparedStatement executeFast2 = MessagesStorage.this.database.executeFast("REPLACE INTO user_phones_v6 VALUES(?, ?, ?, ?)");
                for (Entry value : this.val$contactHashMap.entrySet()) {
                    Contact contact = (Contact) value.getValue();
                    if (!(contact.phones.isEmpty() || contact.shortPhones.isEmpty())) {
                        executeFast.requery();
                        executeFast.bindInteger(1, contact.id);
                        executeFast.bindString(2, contact.first_name);
                        executeFast.bindString(3, contact.last_name);
                        executeFast.step();
                        for (int i = 0; i < contact.phones.size(); i++) {
                            executeFast2.requery();
                            executeFast2.bindInteger(1, contact.id);
                            executeFast2.bindString(2, (String) contact.phones.get(i));
                            executeFast2.bindString(3, (String) contact.shortPhones.get(i));
                            executeFast2.bindInteger(4, ((Integer) contact.phoneDeleted.get(i)).intValue());
                            executeFast2.step();
                        }
                    }
                }
                executeFast.dispose();
                executeFast2.dispose();
                MessagesStorage.this.database.commitTransaction();
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MessagesStorage.43 */
    class AnonymousClass43 implements Runnable {
        final /* synthetic */ int val$count;

        AnonymousClass43(int i) {
            this.val$count = i;
        }

        public void run() {
            try {
                HashMap hashMap = new HashMap();
                ArrayList arrayList = new ArrayList();
                ArrayList arrayList2 = new ArrayList();
                ArrayList arrayList3 = new ArrayList();
                ArrayList arrayList4 = new ArrayList();
                Iterable arrayList5 = new ArrayList();
                ArrayList arrayList6 = new ArrayList();
                ArrayList arrayList7 = new ArrayList();
                Iterable arrayList8 = new ArrayList();
                SQLiteCursor queryFinalized = MessagesStorage.this.database.queryFinalized("SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.uid, s.seq_in, s.seq_out, m.ttl FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid LEFT JOIN messages_seq as s ON m.mid = s.mid WHERE m.mid < 0 AND m.send_state = 1 ORDER BY m.mid DESC LIMIT " + this.val$count, new Object[0]);
                while (queryFinalized.next()) {
                    AbstractSerializedData byteBufferValue = queryFinalized.byteBufferValue(1);
                    if (byteBufferValue != null) {
                        Message TLdeserialize = Message.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                        byteBufferValue.reuse();
                        if (hashMap.containsKey(Integer.valueOf(TLdeserialize.id))) {
                            continue;
                        } else {
                            MessageObject.setUnreadFlags(TLdeserialize, queryFinalized.intValue(0));
                            TLdeserialize.id = queryFinalized.intValue(3);
                            TLdeserialize.date = queryFinalized.intValue(4);
                            if (!queryFinalized.isNull(5)) {
                                TLdeserialize.random_id = queryFinalized.longValue(5);
                            }
                            TLdeserialize.dialog_id = queryFinalized.longValue(6);
                            TLdeserialize.seq_in = queryFinalized.intValue(7);
                            TLdeserialize.seq_out = queryFinalized.intValue(8);
                            TLdeserialize.ttl = queryFinalized.intValue(9);
                            arrayList.add(TLdeserialize);
                            hashMap.put(Integer.valueOf(TLdeserialize.id), TLdeserialize);
                            int i = (int) TLdeserialize.dialog_id;
                            int i2 = (int) (TLdeserialize.dialog_id >> 32);
                            if (i != 0) {
                                if (i2 == 1) {
                                    if (!arrayList7.contains(Integer.valueOf(i))) {
                                        arrayList7.add(Integer.valueOf(i));
                                    }
                                } else if (i < 0) {
                                    if (!arrayList6.contains(Integer.valueOf(-i))) {
                                        arrayList6.add(Integer.valueOf(-i));
                                    }
                                } else if (!arrayList5.contains(Integer.valueOf(i))) {
                                    arrayList5.add(Integer.valueOf(i));
                                }
                            } else if (!arrayList8.contains(Integer.valueOf(i2))) {
                                arrayList8.add(Integer.valueOf(i2));
                            }
                            MessagesStorage.addUsersAndChatsFromMessage(TLdeserialize, arrayList5, arrayList6);
                            TLdeserialize.send_state = queryFinalized.intValue(2);
                            if (!(TLdeserialize.to_id.channel_id != 0 || MessageObject.isUnread(TLdeserialize) || i == 0) || TLdeserialize.id > 0) {
                                TLdeserialize.send_state = 0;
                            }
                            if (i == 0 && !queryFinalized.isNull(5)) {
                                TLdeserialize.random_id = queryFinalized.longValue(5);
                            }
                        }
                    }
                }
                queryFinalized.dispose();
                if (!arrayList8.isEmpty()) {
                    MessagesStorage.this.getEncryptedChatsInternal(TextUtils.join(",", arrayList8), arrayList4, arrayList5);
                }
                if (!arrayList5.isEmpty()) {
                    MessagesStorage.this.getUsersInternal(TextUtils.join(",", arrayList5), arrayList2);
                }
                if (!(arrayList6.isEmpty() && arrayList7.isEmpty())) {
                    int i3;
                    Integer num;
                    StringBuilder stringBuilder = new StringBuilder();
                    for (i3 = 0; i3 < arrayList6.size(); i3++) {
                        num = (Integer) arrayList6.get(i3);
                        if (stringBuilder.length() != 0) {
                            stringBuilder.append(",");
                        }
                        stringBuilder.append(num);
                    }
                    for (i3 = 0; i3 < arrayList7.size(); i3++) {
                        num = (Integer) arrayList7.get(i3);
                        if (stringBuilder.length() != 0) {
                            stringBuilder.append(",");
                        }
                        stringBuilder.append(-num.intValue());
                    }
                    MessagesStorage.this.getChatsInternal(stringBuilder.toString(), arrayList3);
                }
                SendMessagesHelper.getInstance().processUnsentMessages(arrayList, arrayList2, arrayList3, arrayList4);
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MessagesStorage.44 */
    class AnonymousClass44 implements Runnable {
        final /* synthetic */ long val$dialog_id;
        final /* synthetic */ int val$mid;
        final /* synthetic */ boolean[] val$result;
        final /* synthetic */ Semaphore val$semaphore;

        AnonymousClass44(long j, int i, boolean[] zArr, Semaphore semaphore) {
            this.val$dialog_id = j;
            this.val$mid = i;
            this.val$result = zArr;
            this.val$semaphore = semaphore;
        }

        public void run() {
            SQLiteCursor sQLiteCursor = null;
            try {
                sQLiteCursor = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT mid FROM messages WHERE uid = %d AND mid = %d", new Object[]{Long.valueOf(this.val$dialog_id), Integer.valueOf(this.val$mid)}), new Object[0]);
                if (sQLiteCursor.next()) {
                    this.val$result[0] = true;
                }
                if (sQLiteCursor != null) {
                    sQLiteCursor.dispose();
                }
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
                if (sQLiteCursor != null) {
                    sQLiteCursor.dispose();
                }
            } catch (Throwable th) {
                if (sQLiteCursor != null) {
                    sQLiteCursor.dispose();
                }
            }
            this.val$semaphore.release();
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MessagesStorage.45 */
    class AnonymousClass45 implements Runnable {
        final /* synthetic */ int val$classGuid;
        final /* synthetic */ int val$count;
        final /* synthetic */ long val$dialog_id;
        final /* synthetic */ boolean val$isChannel;
        final /* synthetic */ int val$loadIndex;
        final /* synthetic */ int val$load_type;
        final /* synthetic */ int val$max_id;
        final /* synthetic */ int val$minDate;

        /* renamed from: com.hanista.mobogram.messenger.MessagesStorage.45.1 */
        class C05841 implements Comparator<Message> {
            C05841() {
            }

            public int compare(Message message, Message message2) {
                if (message.id <= 0 || message2.id <= 0) {
                    if (message.id >= 0 || message2.id >= 0) {
                        if (message.date > message2.date) {
                            return -1;
                        }
                        if (message.date < message2.date) {
                            return 1;
                        }
                    } else if (message.id < message2.id) {
                        return -1;
                    } else {
                        if (message.id > message2.id) {
                            return 1;
                        }
                    }
                } else if (message.id > message2.id) {
                    return -1;
                } else {
                    if (message.id < message2.id) {
                        return 1;
                    }
                }
                return 0;
            }
        }

        AnonymousClass45(int i, int i2, boolean z, long j, int i3, int i4, int i5, int i6) {
            this.val$count = i;
            this.val$max_id = i2;
            this.val$isChannel = z;
            this.val$dialog_id = j;
            this.val$load_type = i3;
            this.val$minDate = i4;
            this.val$classGuid = i5;
            this.val$loadIndex = i6;
        }

        public void run() {
            int i;
            Throwable e;
            Throwable th;
            messages_Messages tL_messages_messages = new TL_messages_messages();
            int i2 = 0;
            int i3 = this.val$count;
            int i4 = 0;
            int i5 = 0;
            boolean z = false;
            int i6 = 0;
            long j = (long) this.val$max_id;
            int i7 = this.val$max_id;
            int i8 = this.val$isChannel ? -((int) this.val$dialog_id) : 0;
            if (!(j == 0 || i8 == 0)) {
                j |= ((long) i8) << 32;
            }
            boolean z2 = false;
            int i9 = this.val$dialog_id == 777000 ? 4 : 1;
            int i10;
            try {
                int i11;
                SQLiteCursor queryFinalized;
                SQLiteDatabase access$000;
                SQLiteCursor sQLiteCursor;
                Object[] objArr;
                AbstractSerializedData byteBufferValue;
                ArrayList arrayList;
                ArrayList arrayList2 = new ArrayList();
                ArrayList arrayList3 = new ArrayList();
                ArrayList arrayList4 = new ArrayList();
                HashMap hashMap = new HashMap();
                HashMap hashMap2 = new HashMap();
                int i12 = (int) this.val$dialog_id;
                Object[] objArr2;
                if (i12 != 0) {
                    SQLiteDatabase access$0002;
                    SQLiteCursor queryFinalized2;
                    long j2;
                    SQLitePreparedStatement executeFast;
                    Object obj;
                    long j3;
                    long j4;
                    Long[] lArr;
                    Long[] lArr2;
                    if (!(this.val$load_type == 1 || this.val$load_type == 3 || this.val$minDate != 0)) {
                        if (this.val$load_type == 2) {
                            long j5;
                            SQLiteCursor queryFinalized3 = MessagesStorage.this.database.queryFinalized("SELECT inbox_max, unread_count, date FROM dialogs WHERE did = " + this.val$dialog_id, new Object[0]);
                            if (queryFinalized3.next()) {
                                i4 = queryFinalized3.intValue(0);
                                j5 = (long) i4;
                                i2 = queryFinalized3.intValue(1);
                                i6 = queryFinalized3.intValue(2);
                                z = true;
                                if (j5 == 0 || i8 == 0) {
                                    i11 = i4;
                                    i = i4;
                                } else {
                                    j5 |= ((long) i8) << 32;
                                    i11 = i4;
                                    i = i4;
                                }
                            } else {
                                j5 = j;
                                i11 = i7;
                                i = i4;
                            }
                            try {
                                queryFinalized3.dispose();
                                SQLiteDatabase access$0003;
                                Long[] lArr3;
                                if (!z) {
                                    access$0003 = MessagesStorage.this.database;
                                    lArr3 = new Object[1];
                                    lArr3[0] = Long.valueOf(this.val$dialog_id);
                                    queryFinalized3 = access$0003.queryFinalized(String.format(Locale.US, "SELECT min(mid), max(date) FROM messages WHERE uid = %d AND out = 0 AND read_state IN(0,2) AND mid > 0", lArr3), new Object[0]);
                                    if (queryFinalized3.next()) {
                                        i4 = queryFinalized3.intValue(0);
                                        i6 = queryFinalized3.intValue(1);
                                    } else {
                                        i4 = i;
                                    }
                                    queryFinalized3.dispose();
                                    int i13;
                                    if (i4 != 0) {
                                        access$0002 = MessagesStorage.this.database;
                                        lArr3 = new Object[2];
                                        lArr3[0] = Long.valueOf(this.val$dialog_id);
                                        lArr3[1] = Integer.valueOf(i4);
                                        queryFinalized2 = access$0002.queryFinalized(String.format(Locale.US, "SELECT COUNT(*) FROM messages WHERE uid = %d AND mid >= %d AND out = 0 AND read_state IN(0,2)", lArr3), new Object[0]);
                                        if (queryFinalized2.next()) {
                                            i2 = queryFinalized2.intValue(0);
                                        }
                                        queryFinalized2.dispose();
                                        i13 = i11;
                                        j = j5;
                                        i7 = i13;
                                    } else {
                                        i13 = i11;
                                        j = j5;
                                        i7 = i13;
                                    }
                                } else if (i11 == 0) {
                                    i4 = 0;
                                    SQLiteDatabase access$0004 = MessagesStorage.this.database;
                                    Locale locale = Locale.US;
                                    Long[] lArr4 = new Object[1];
                                    lArr4[0] = Long.valueOf(this.val$dialog_id);
                                    queryFinalized3 = access$0004.queryFinalized(String.format(locale, "SELECT COUNT(*) FROM messages WHERE uid = %d AND mid > 0 AND out = 0 AND read_state IN(0,2)", lArr4), new Object[0]);
                                    if (queryFinalized3.next()) {
                                        i4 = queryFinalized3.intValue(0);
                                    }
                                    queryFinalized3.dispose();
                                    if (i4 == i2) {
                                        access$0003 = MessagesStorage.this.database;
                                        lArr3 = new Object[1];
                                        lArr3[0] = Long.valueOf(this.val$dialog_id);
                                        r10 = access$0003.queryFinalized(String.format(Locale.US, "SELECT min(mid) FROM messages WHERE uid = %d AND out = 0 AND read_state IN(0,2) AND mid > 0", lArr3), new Object[0]);
                                        if (r10.next()) {
                                            i11 = r10.intValue(0);
                                            j5 = (long) i11;
                                            if (j5 == 0 || i8 == 0) {
                                                i = i11;
                                            } else {
                                                j5 |= ((long) i8) << 32;
                                                i = i11;
                                            }
                                        }
                                        r10.dispose();
                                    }
                                    i4 = i;
                                    i7 = i11;
                                    j = j5;
                                } else {
                                    access$0003 = MessagesStorage.this.database;
                                    lArr3 = new Object[3];
                                    lArr3[0] = Long.valueOf(this.val$dialog_id);
                                    lArr3[1] = Integer.valueOf(i11);
                                    lArr3[2] = Integer.valueOf(i11);
                                    queryFinalized3 = access$0003.queryFinalized(String.format(Locale.US, "SELECT start, end FROM messages_holes WHERE uid = %d AND start < %d AND end > %d", lArr3), new Object[0]);
                                    Object obj2 = !queryFinalized3.next() ? 1 : null;
                                    queryFinalized3.dispose();
                                    if (obj2 != null) {
                                        access$0003 = MessagesStorage.this.database;
                                        lArr3 = new Object[2];
                                        lArr3[0] = Long.valueOf(this.val$dialog_id);
                                        lArr3[1] = Integer.valueOf(i11);
                                        r10 = access$0003.queryFinalized(String.format(Locale.US, "SELECT min(mid) FROM messages WHERE uid = %d AND out = 0 AND read_state IN(0,2) AND mid > %d", lArr3), new Object[0]);
                                        if (r10.next()) {
                                            i11 = r10.intValue(0);
                                            j5 = (long) i11;
                                            if (!(j5 == 0 || i8 == 0)) {
                                                j5 |= ((long) i8) << 32;
                                            }
                                        }
                                        r10.dispose();
                                    }
                                    i4 = i;
                                    i7 = i11;
                                    j = j5;
                                }
                            } catch (Exception e2) {
                                e = e2;
                                i4 = i;
                                i10 = i3;
                                try {
                                    tL_messages_messages.messages.clear();
                                    tL_messages_messages.chats.clear();
                                    tL_messages_messages.users.clear();
                                    FileLog.m18e("tmessages", e);
                                    MessagesController.getInstance().processLoadedMessages(tL_messages_messages, this.val$dialog_id, i10, this.val$max_id, true, this.val$classGuid, i4, i5, i2, i6, this.val$load_type, this.val$isChannel, z2, this.val$loadIndex, z);
                                } catch (Throwable e3) {
                                    th = e3;
                                    MessagesController.getInstance().processLoadedMessages(tL_messages_messages, this.val$dialog_id, i10, this.val$max_id, true, this.val$classGuid, i4, i5, i2, i6, this.val$load_type, this.val$isChannel, z2, this.val$loadIndex, z);
                                    throw th;
                                }
                            } catch (Throwable e32) {
                                th = e32;
                                i4 = i;
                                i10 = i3;
                                MessagesController.getInstance().processLoadedMessages(tL_messages_messages, this.val$dialog_id, i10, this.val$max_id, true, this.val$classGuid, i4, i5, i2, i6, this.val$load_type, this.val$isChannel, z2, this.val$loadIndex, z);
                                throw th;
                            }
                        }
                        if (i3 > i2 || i2 < i9) {
                            i3 = Math.max(i3, i2 + 10);
                            if (i2 < i9) {
                                i2 = 0;
                                i4 = 0;
                                i5 = 0;
                                z = false;
                                j2 = 0;
                                i9 = 0;
                                i10 = i3;
                                i11 = i7;
                                access$0002 = MessagesStorage.this.database;
                                objArr2 = new Object[1];
                                objArr2[0] = Long.valueOf(this.val$dialog_id);
                                queryFinalized2 = access$0002.queryFinalized(String.format(Locale.US, "SELECT start FROM messages_holes WHERE uid = %d AND start IN (0, 1)", objArr2), new Object[0]);
                                if (queryFinalized2.next()) {
                                    z2 = queryFinalized2.intValue(0) == 1;
                                    queryFinalized2.dispose();
                                } else {
                                    queryFinalized2.dispose();
                                    access$0002 = MessagesStorage.this.database;
                                    objArr2 = new Object[1];
                                    objArr2[0] = Long.valueOf(this.val$dialog_id);
                                    queryFinalized2 = access$0002.queryFinalized(String.format(Locale.US, "SELECT min(mid) FROM messages WHERE uid = %d AND mid > 0", objArr2), new Object[0]);
                                    if (queryFinalized2.next()) {
                                        i7 = queryFinalized2.intValue(0);
                                        if (i7 != 0) {
                                            executeFast = MessagesStorage.this.database.executeFast("REPLACE INTO messages_holes VALUES(?, ?, ?)");
                                            executeFast.requery();
                                            executeFast.bindLong(1, this.val$dialog_id);
                                            executeFast.bindInteger(2, 0);
                                            executeFast.bindInteger(3, i7);
                                            executeFast.step();
                                            executeFast.dispose();
                                        }
                                    }
                                    queryFinalized2.dispose();
                                }
                                if (this.val$load_type != 3 || (r18 && this.val$load_type == 2)) {
                                    queryFinalized = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT max(mid) FROM messages WHERE uid = %d AND mid > 0", new Object[]{Long.valueOf(this.val$dialog_id)}), new Object[0]);
                                    if (queryFinalized.next()) {
                                        i5 = queryFinalized.intValue(0);
                                    }
                                    queryFinalized.dispose();
                                    obj = i11 != 0 ? 1 : null;
                                    if (obj != null) {
                                        access$0002 = MessagesStorage.this.database;
                                        objArr2 = new Object[3];
                                        objArr2[0] = Long.valueOf(this.val$dialog_id);
                                        objArr2[1] = Integer.valueOf(i11);
                                        objArr2[2] = Integer.valueOf(i11);
                                        queryFinalized2 = access$0002.queryFinalized(String.format(Locale.US, "SELECT start FROM messages_holes WHERE uid = %d AND start < %d AND end > %d", objArr2), new Object[0]);
                                        if (queryFinalized2.next()) {
                                            obj = null;
                                        }
                                        queryFinalized2.dispose();
                                    }
                                    if (obj != null) {
                                        j3 = 0;
                                        j4 = 1;
                                        access$000 = MessagesStorage.this.database;
                                        lArr = new Object[2];
                                        lArr[0] = Long.valueOf(this.val$dialog_id);
                                        lArr[1] = Integer.valueOf(i11);
                                        queryFinalized = access$000.queryFinalized(String.format(Locale.US, "SELECT start FROM messages_holes WHERE uid = %d AND start >= %d ORDER BY start ASC LIMIT 1", lArr), new Object[0]);
                                        if (queryFinalized.next()) {
                                            j3 = (long) queryFinalized.intValue(0);
                                            if (i8 != 0) {
                                                j3 |= ((long) i8) << 32;
                                            }
                                        }
                                        queryFinalized.dispose();
                                        access$000 = MessagesStorage.this.database;
                                        lArr = new Object[2];
                                        lArr[0] = Long.valueOf(this.val$dialog_id);
                                        lArr[1] = Integer.valueOf(i11);
                                        queryFinalized = access$000.queryFinalized(String.format(Locale.US, "SELECT end FROM messages_holes WHERE uid = %d AND end <= %d ORDER BY end DESC LIMIT 1", lArr), new Object[0]);
                                        if (queryFinalized.next()) {
                                            j4 = (long) queryFinalized.intValue(0);
                                            if (i8 != 0) {
                                                j4 |= ((long) i8) << 32;
                                            }
                                        }
                                        queryFinalized.dispose();
                                        if (j3 == 0 || j4 != 1) {
                                            if (j3 == 0) {
                                                j3 = 1000000000;
                                                if (i8 != 0) {
                                                    j3 = 1000000000 | (((long) i8) << 32);
                                                }
                                            }
                                            access$000 = MessagesStorage.this.database;
                                            lArr2 = new Object[8];
                                            lArr2[0] = Long.valueOf(this.val$dialog_id);
                                            lArr2[1] = Long.valueOf(j2);
                                            lArr2[2] = Long.valueOf(j4);
                                            lArr2[3] = Integer.valueOf(i10 / 2);
                                            lArr2[4] = Long.valueOf(this.val$dialog_id);
                                            lArr2[5] = Long.valueOf(j2);
                                            lArr2[6] = Long.valueOf(j3);
                                            lArr2[7] = Integer.valueOf(i10 / 2);
                                            queryFinalized = access$000.queryFinalized(String.format(Locale.US, "SELECT * FROM (SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media, m.ttl FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND m.mid <= %d AND m.mid >= %d ORDER BY m.date DESC, m.mid DESC LIMIT %d) UNION SELECT * FROM (SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media, m.ttl FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND m.mid > %d AND m.mid <= %d ORDER BY m.date ASC, m.mid ASC LIMIT %d)", lArr2), new Object[0]);
                                        } else {
                                            queryFinalized = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT * FROM (SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media, m.ttl FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND m.mid <= %d ORDER BY m.date DESC, m.mid DESC LIMIT %d) UNION SELECT * FROM (SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media, m.ttl FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND m.mid > %d ORDER BY m.date ASC, m.mid ASC LIMIT %d)", new Object[]{Long.valueOf(this.val$dialog_id), Long.valueOf(j2), Integer.valueOf(i10 / 2), Long.valueOf(this.val$dialog_id), Long.valueOf(j2), Integer.valueOf(i10 / 2)}), new Object[0]);
                                        }
                                    } else {
                                        queryFinalized = null;
                                    }
                                    sQLiteCursor = queryFinalized;
                                    i3 = i11;
                                } else if (this.val$load_type == 1) {
                                    j3 = 0;
                                    access$000 = MessagesStorage.this.database;
                                    objArr = new Object[2];
                                    objArr[0] = Long.valueOf(this.val$dialog_id);
                                    objArr[1] = Integer.valueOf(this.val$max_id);
                                    queryFinalized = access$000.queryFinalized(String.format(Locale.US, "SELECT start, end FROM messages_holes WHERE uid = %d AND start >= %d AND start != 1 AND end != 1 ORDER BY start ASC LIMIT 1", objArr), new Object[0]);
                                    if (queryFinalized.next()) {
                                        j3 = (long) queryFinalized.intValue(0);
                                        if (i8 != 0) {
                                            j3 |= ((long) i8) << 32;
                                        }
                                    }
                                    queryFinalized.dispose();
                                    if (j3 != 0) {
                                        access$000 = MessagesStorage.this.database;
                                        objArr = new Object[5];
                                        objArr[0] = Long.valueOf(this.val$dialog_id);
                                        objArr[1] = Integer.valueOf(this.val$minDate);
                                        objArr[2] = Long.valueOf(j2);
                                        objArr[3] = Long.valueOf(j3);
                                        objArr[4] = Integer.valueOf(i10);
                                        queryFinalized = access$000.queryFinalized(String.format(Locale.US, "SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media, m.ttl FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND m.date >= %d AND m.mid > %d AND m.mid <= %d ORDER BY m.date ASC, m.mid ASC LIMIT %d", objArr), new Object[0]);
                                    } else {
                                        queryFinalized = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media, m.ttl FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND m.date >= %d AND m.mid > %d ORDER BY m.date ASC, m.mid ASC LIMIT %d", new Object[]{Long.valueOf(this.val$dialog_id), Integer.valueOf(this.val$minDate), Long.valueOf(j2), Integer.valueOf(i10)}), new Object[0]);
                                    }
                                    sQLiteCursor = queryFinalized;
                                    i3 = i11;
                                } else if (this.val$minDate == 0) {
                                    access$0002 = MessagesStorage.this.database;
                                    objArr2 = new Object[1];
                                    objArr2[0] = Long.valueOf(this.val$dialog_id);
                                    queryFinalized2 = access$0002.queryFinalized(String.format(Locale.US, "SELECT max(mid) FROM messages WHERE uid = %d AND mid > 0", objArr2), new Object[0]);
                                    if (queryFinalized2.next()) {
                                        i5 = queryFinalized2.intValue(0);
                                    }
                                    queryFinalized2.dispose();
                                    j3 = 0;
                                    access$0002 = MessagesStorage.this.database;
                                    Object[] objArr3 = new Object[1];
                                    objArr3[0] = Long.valueOf(this.val$dialog_id);
                                    queryFinalized2 = access$0002.queryFinalized(String.format(Locale.US, "SELECT max(end) FROM messages_holes WHERE uid = %d", objArr3), new Object[0]);
                                    if (queryFinalized2.next()) {
                                        j3 = (long) queryFinalized2.intValue(0);
                                        if (i8 != 0) {
                                            j3 |= ((long) i8) << 32;
                                        }
                                    }
                                    queryFinalized2.dispose();
                                    if (j3 != 0) {
                                        access$0002 = MessagesStorage.this.database;
                                        objArr3 = new Object[4];
                                        objArr3[0] = Long.valueOf(this.val$dialog_id);
                                        objArr3[1] = Long.valueOf(j3);
                                        objArr3[2] = Integer.valueOf(i9);
                                        objArr3[3] = Integer.valueOf(i10);
                                        queryFinalized = access$0002.queryFinalized(String.format(Locale.US, "SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media, m.ttl FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND (m.mid >= %d OR m.mid < 0) ORDER BY m.date DESC, m.mid DESC LIMIT %d,%d", objArr3), new Object[0]);
                                    } else {
                                        access$0002 = MessagesStorage.this.database;
                                        objArr2 = new Object[3];
                                        objArr2[0] = Long.valueOf(this.val$dialog_id);
                                        objArr2[1] = Integer.valueOf(i9);
                                        objArr2[2] = Integer.valueOf(i10);
                                        queryFinalized = access$0002.queryFinalized(String.format(Locale.US, "SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media, m.ttl FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d ORDER BY m.date DESC, m.mid DESC LIMIT %d,%d", objArr2), new Object[0]);
                                    }
                                    sQLiteCursor = queryFinalized;
                                    i3 = i11;
                                } else if (j2 != 0) {
                                    j3 = 0;
                                    access$000 = MessagesStorage.this.database;
                                    objArr = new Object[2];
                                    objArr[0] = Long.valueOf(this.val$dialog_id);
                                    objArr[1] = Integer.valueOf(this.val$max_id);
                                    queryFinalized = access$000.queryFinalized(String.format(Locale.US, "SELECT end FROM messages_holes WHERE uid = %d AND end <= %d ORDER BY end DESC LIMIT 1", objArr), new Object[0]);
                                    if (queryFinalized.next()) {
                                        j3 = (long) queryFinalized.intValue(0);
                                        if (i8 != 0) {
                                            j3 |= ((long) i8) << 32;
                                        }
                                    }
                                    queryFinalized.dispose();
                                    if (j3 != 0) {
                                        access$000 = MessagesStorage.this.database;
                                        objArr = new Object[5];
                                        objArr[0] = Long.valueOf(this.val$dialog_id);
                                        objArr[1] = Integer.valueOf(this.val$minDate);
                                        objArr[2] = Long.valueOf(j2);
                                        objArr[3] = Long.valueOf(j3);
                                        objArr[4] = Integer.valueOf(i10);
                                        queryFinalized = access$000.queryFinalized(String.format(Locale.US, "SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media, m.ttl FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND m.date <= %d AND m.mid < %d AND (m.mid >= %d OR m.mid < 0) ORDER BY m.date DESC, m.mid DESC LIMIT %d", objArr), new Object[0]);
                                    } else {
                                        queryFinalized = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media, m.ttl FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND m.date <= %d AND m.mid < %d ORDER BY m.date DESC, m.mid DESC LIMIT %d", new Object[]{Long.valueOf(this.val$dialog_id), Integer.valueOf(this.val$minDate), Long.valueOf(j2), Integer.valueOf(i10)}), new Object[0]);
                                    }
                                    sQLiteCursor = queryFinalized;
                                    i3 = i11;
                                } else {
                                    access$0002 = MessagesStorage.this.database;
                                    objArr2 = new Object[4];
                                    objArr2[0] = Long.valueOf(this.val$dialog_id);
                                    objArr2[1] = Integer.valueOf(this.val$minDate);
                                    objArr2[2] = Integer.valueOf(i9);
                                    objArr2[3] = Integer.valueOf(i10);
                                    sQLiteCursor = access$0002.queryFinalized(String.format(Locale.US, "SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media, m.ttl FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND m.date <= %d ORDER BY m.date DESC, m.mid DESC LIMIT %d,%d", objArr2), new Object[0]);
                                    i3 = i11;
                                }
                            }
                        } else {
                            j2 = j;
                            i9 = i2 - i3;
                            i10 = i3 + 10;
                            i11 = i7;
                            access$0002 = MessagesStorage.this.database;
                            objArr2 = new Object[1];
                            objArr2[0] = Long.valueOf(this.val$dialog_id);
                            queryFinalized2 = access$0002.queryFinalized(String.format(Locale.US, "SELECT start FROM messages_holes WHERE uid = %d AND start IN (0, 1)", objArr2), new Object[0]);
                            if (queryFinalized2.next()) {
                                queryFinalized2.dispose();
                                access$0002 = MessagesStorage.this.database;
                                objArr2 = new Object[1];
                                objArr2[0] = Long.valueOf(this.val$dialog_id);
                                queryFinalized2 = access$0002.queryFinalized(String.format(Locale.US, "SELECT min(mid) FROM messages WHERE uid = %d AND mid > 0", objArr2), new Object[0]);
                                if (queryFinalized2.next()) {
                                    i7 = queryFinalized2.intValue(0);
                                    if (i7 != 0) {
                                        executeFast = MessagesStorage.this.database.executeFast("REPLACE INTO messages_holes VALUES(?, ?, ?)");
                                        executeFast.requery();
                                        executeFast.bindLong(1, this.val$dialog_id);
                                        executeFast.bindInteger(2, 0);
                                        executeFast.bindInteger(3, i7);
                                        executeFast.step();
                                        executeFast.dispose();
                                    }
                                }
                                queryFinalized2.dispose();
                            } else {
                                if (queryFinalized2.intValue(0) == 1) {
                                }
                                queryFinalized2.dispose();
                            }
                            if (this.val$load_type != 3) {
                            }
                            queryFinalized = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT max(mid) FROM messages WHERE uid = %d AND mid > 0", new Object[]{Long.valueOf(this.val$dialog_id)}), new Object[0]);
                            if (queryFinalized.next()) {
                                i5 = queryFinalized.intValue(0);
                            }
                            queryFinalized.dispose();
                            if (i11 != 0) {
                            }
                            if (obj != null) {
                                access$0002 = MessagesStorage.this.database;
                                objArr2 = new Object[3];
                                objArr2[0] = Long.valueOf(this.val$dialog_id);
                                objArr2[1] = Integer.valueOf(i11);
                                objArr2[2] = Integer.valueOf(i11);
                                queryFinalized2 = access$0002.queryFinalized(String.format(Locale.US, "SELECT start FROM messages_holes WHERE uid = %d AND start < %d AND end > %d", objArr2), new Object[0]);
                                if (queryFinalized2.next()) {
                                    obj = null;
                                }
                                queryFinalized2.dispose();
                            }
                            if (obj != null) {
                                queryFinalized = null;
                            } else {
                                j3 = 0;
                                j4 = 1;
                                access$000 = MessagesStorage.this.database;
                                lArr = new Object[2];
                                lArr[0] = Long.valueOf(this.val$dialog_id);
                                lArr[1] = Integer.valueOf(i11);
                                queryFinalized = access$000.queryFinalized(String.format(Locale.US, "SELECT start FROM messages_holes WHERE uid = %d AND start >= %d ORDER BY start ASC LIMIT 1", lArr), new Object[0]);
                                if (queryFinalized.next()) {
                                    j3 = (long) queryFinalized.intValue(0);
                                    if (i8 != 0) {
                                        j3 |= ((long) i8) << 32;
                                    }
                                }
                                queryFinalized.dispose();
                                access$000 = MessagesStorage.this.database;
                                lArr = new Object[2];
                                lArr[0] = Long.valueOf(this.val$dialog_id);
                                lArr[1] = Integer.valueOf(i11);
                                queryFinalized = access$000.queryFinalized(String.format(Locale.US, "SELECT end FROM messages_holes WHERE uid = %d AND end <= %d ORDER BY end DESC LIMIT 1", lArr), new Object[0]);
                                if (queryFinalized.next()) {
                                    j4 = (long) queryFinalized.intValue(0);
                                    if (i8 != 0) {
                                        j4 |= ((long) i8) << 32;
                                    }
                                }
                                queryFinalized.dispose();
                                if (j3 == 0) {
                                }
                                if (j3 == 0) {
                                    j3 = 1000000000;
                                    if (i8 != 0) {
                                        j3 = 1000000000 | (((long) i8) << 32);
                                    }
                                }
                                access$000 = MessagesStorage.this.database;
                                lArr2 = new Object[8];
                                lArr2[0] = Long.valueOf(this.val$dialog_id);
                                lArr2[1] = Long.valueOf(j2);
                                lArr2[2] = Long.valueOf(j4);
                                lArr2[3] = Integer.valueOf(i10 / 2);
                                lArr2[4] = Long.valueOf(this.val$dialog_id);
                                lArr2[5] = Long.valueOf(j2);
                                lArr2[6] = Long.valueOf(j3);
                                lArr2[7] = Integer.valueOf(i10 / 2);
                                queryFinalized = access$000.queryFinalized(String.format(Locale.US, "SELECT * FROM (SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media, m.ttl FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND m.mid <= %d AND m.mid >= %d ORDER BY m.date DESC, m.mid DESC LIMIT %d) UNION SELECT * FROM (SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media, m.ttl FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND m.mid > %d AND m.mid <= %d ORDER BY m.date ASC, m.mid ASC LIMIT %d)", lArr2), new Object[0]);
                            }
                            sQLiteCursor = queryFinalized;
                            i3 = i11;
                        }
                    }
                    j2 = j;
                    i9 = 0;
                    i10 = i3;
                    i11 = i7;
                    try {
                        access$0002 = MessagesStorage.this.database;
                        objArr2 = new Object[1];
                        objArr2[0] = Long.valueOf(this.val$dialog_id);
                        queryFinalized2 = access$0002.queryFinalized(String.format(Locale.US, "SELECT start FROM messages_holes WHERE uid = %d AND start IN (0, 1)", objArr2), new Object[0]);
                        if (queryFinalized2.next()) {
                            if (queryFinalized2.intValue(0) == 1) {
                            }
                            queryFinalized2.dispose();
                        } else {
                            queryFinalized2.dispose();
                            access$0002 = MessagesStorage.this.database;
                            objArr2 = new Object[1];
                            objArr2[0] = Long.valueOf(this.val$dialog_id);
                            queryFinalized2 = access$0002.queryFinalized(String.format(Locale.US, "SELECT min(mid) FROM messages WHERE uid = %d AND mid > 0", objArr2), new Object[0]);
                            if (queryFinalized2.next()) {
                                i7 = queryFinalized2.intValue(0);
                                if (i7 != 0) {
                                    executeFast = MessagesStorage.this.database.executeFast("REPLACE INTO messages_holes VALUES(?, ?, ?)");
                                    executeFast.requery();
                                    executeFast.bindLong(1, this.val$dialog_id);
                                    executeFast.bindInteger(2, 0);
                                    executeFast.bindInteger(3, i7);
                                    executeFast.step();
                                    executeFast.dispose();
                                }
                            }
                            queryFinalized2.dispose();
                        }
                        if (this.val$load_type != 3) {
                        }
                        queryFinalized = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT max(mid) FROM messages WHERE uid = %d AND mid > 0", new Object[]{Long.valueOf(this.val$dialog_id)}), new Object[0]);
                        if (queryFinalized.next()) {
                            i5 = queryFinalized.intValue(0);
                        }
                        queryFinalized.dispose();
                        if (i11 != 0) {
                        }
                        if (obj != null) {
                            access$0002 = MessagesStorage.this.database;
                            objArr2 = new Object[3];
                            objArr2[0] = Long.valueOf(this.val$dialog_id);
                            objArr2[1] = Integer.valueOf(i11);
                            objArr2[2] = Integer.valueOf(i11);
                            queryFinalized2 = access$0002.queryFinalized(String.format(Locale.US, "SELECT start FROM messages_holes WHERE uid = %d AND start < %d AND end > %d", objArr2), new Object[0]);
                            if (queryFinalized2.next()) {
                                obj = null;
                            }
                            queryFinalized2.dispose();
                        }
                        if (obj != null) {
                            j3 = 0;
                            j4 = 1;
                            access$000 = MessagesStorage.this.database;
                            lArr = new Object[2];
                            lArr[0] = Long.valueOf(this.val$dialog_id);
                            lArr[1] = Integer.valueOf(i11);
                            queryFinalized = access$000.queryFinalized(String.format(Locale.US, "SELECT start FROM messages_holes WHERE uid = %d AND start >= %d ORDER BY start ASC LIMIT 1", lArr), new Object[0]);
                            if (queryFinalized.next()) {
                                j3 = (long) queryFinalized.intValue(0);
                                if (i8 != 0) {
                                    j3 |= ((long) i8) << 32;
                                }
                            }
                            queryFinalized.dispose();
                            access$000 = MessagesStorage.this.database;
                            lArr = new Object[2];
                            lArr[0] = Long.valueOf(this.val$dialog_id);
                            lArr[1] = Integer.valueOf(i11);
                            queryFinalized = access$000.queryFinalized(String.format(Locale.US, "SELECT end FROM messages_holes WHERE uid = %d AND end <= %d ORDER BY end DESC LIMIT 1", lArr), new Object[0]);
                            if (queryFinalized.next()) {
                                j4 = (long) queryFinalized.intValue(0);
                                if (i8 != 0) {
                                    j4 |= ((long) i8) << 32;
                                }
                            }
                            queryFinalized.dispose();
                            if (j3 == 0) {
                            }
                            if (j3 == 0) {
                                j3 = 1000000000;
                                if (i8 != 0) {
                                    j3 = 1000000000 | (((long) i8) << 32);
                                }
                            }
                            access$000 = MessagesStorage.this.database;
                            lArr2 = new Object[8];
                            lArr2[0] = Long.valueOf(this.val$dialog_id);
                            lArr2[1] = Long.valueOf(j2);
                            lArr2[2] = Long.valueOf(j4);
                            lArr2[3] = Integer.valueOf(i10 / 2);
                            lArr2[4] = Long.valueOf(this.val$dialog_id);
                            lArr2[5] = Long.valueOf(j2);
                            lArr2[6] = Long.valueOf(j3);
                            lArr2[7] = Integer.valueOf(i10 / 2);
                            queryFinalized = access$000.queryFinalized(String.format(Locale.US, "SELECT * FROM (SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media, m.ttl FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND m.mid <= %d AND m.mid >= %d ORDER BY m.date DESC, m.mid DESC LIMIT %d) UNION SELECT * FROM (SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media, m.ttl FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND m.mid > %d AND m.mid <= %d ORDER BY m.date ASC, m.mid ASC LIMIT %d)", lArr2), new Object[0]);
                        } else {
                            queryFinalized = null;
                        }
                        sQLiteCursor = queryFinalized;
                        i3 = i11;
                    } catch (Exception e4) {
                        e32 = e4;
                    }
                } else {
                    z2 = true;
                    Object[] objArr4;
                    if (this.val$load_type == 1) {
                        objArr4 = new Object[]{Long.valueOf(this.val$dialog_id), Integer.valueOf(this.val$max_id), Integer.valueOf(i3)};
                        i10 = i3;
                        i3 = i7;
                        sQLiteCursor = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media, m.ttl FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND m.mid < %d ORDER BY m.mid DESC LIMIT %d", objArr4), new Object[0]);
                    } else if (this.val$minDate == 0) {
                        SQLiteDatabase access$0005;
                        if (this.val$load_type == 2) {
                            access$0005 = MessagesStorage.this.database;
                            objArr = new Object[1];
                            objArr[0] = Long.valueOf(this.val$dialog_id);
                            SQLiteCursor queryFinalized4 = access$0005.queryFinalized(String.format(Locale.US, "SELECT min(mid) FROM messages WHERE uid = %d AND mid < 0", objArr), new Object[0]);
                            if (queryFinalized4.next()) {
                                i5 = queryFinalized4.intValue(0);
                            }
                            queryFinalized4.dispose();
                            access$0005 = MessagesStorage.this.database;
                            objArr = new Object[1];
                            objArr[0] = Long.valueOf(this.val$dialog_id);
                            queryFinalized4 = access$0005.queryFinalized(String.format(Locale.US, "SELECT max(mid), max(date) FROM messages WHERE uid = %d AND out = 0 AND read_state IN(0,2) AND mid < 0", objArr), new Object[0]);
                            if (queryFinalized4.next()) {
                                i4 = queryFinalized4.intValue(0);
                                i6 = queryFinalized4.intValue(1);
                            }
                            queryFinalized4.dispose();
                            if (i4 != 0) {
                                access$0005 = MessagesStorage.this.database;
                                objArr = new Object[2];
                                objArr[0] = Long.valueOf(this.val$dialog_id);
                                objArr[1] = Integer.valueOf(i4);
                                queryFinalized4 = access$0005.queryFinalized(String.format(Locale.US, "SELECT COUNT(*) FROM messages WHERE uid = %d AND mid <= %d AND out = 0 AND read_state IN(0,2)", objArr), new Object[0]);
                                if (queryFinalized4.next()) {
                                    i2 = queryFinalized4.intValue(0);
                                }
                                queryFinalized4.dispose();
                            }
                        }
                        if (i3 > i2 || i2 < i9) {
                            i10 = Math.max(i3, i2 + 10);
                            if (i2 < i9) {
                                i2 = 0;
                                i4 = 0;
                                i5 = 0;
                                i9 = 0;
                            } else {
                                i9 = 0;
                            }
                        } else {
                            i9 = i2 - i3;
                            i10 = i3 + 10;
                        }
                        access$0005 = MessagesStorage.this.database;
                        objArr2 = new Object[3];
                        objArr2[0] = Long.valueOf(this.val$dialog_id);
                        objArr2[1] = Integer.valueOf(i9);
                        objArr2[2] = Integer.valueOf(i10);
                        i3 = i7;
                        sQLiteCursor = access$0005.queryFinalized(String.format(Locale.US, "SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media, m.ttl FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d ORDER BY m.mid ASC LIMIT %d,%d", objArr2), new Object[0]);
                    } else if (this.val$max_id != 0) {
                        objArr4 = new Object[]{Long.valueOf(this.val$dialog_id), Integer.valueOf(this.val$max_id), Integer.valueOf(i3)};
                        i10 = i3;
                        i3 = i7;
                        sQLiteCursor = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media, m.ttl FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND m.mid > %d ORDER BY m.mid ASC LIMIT %d", objArr4), new Object[0]);
                    } else {
                        access$000 = MessagesStorage.this.database;
                        objArr4 = new Object[4];
                        objArr4[0] = Long.valueOf(this.val$dialog_id);
                        objArr4[1] = Integer.valueOf(this.val$minDate);
                        objArr4[2] = Integer.valueOf(0);
                        objArr4[3] = Integer.valueOf(i3);
                        i10 = i3;
                        i3 = i7;
                        sQLiteCursor = access$000.queryFinalized(String.format(Locale.US, "SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media, m.ttl FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND m.date <= %d ORDER BY m.mid ASC LIMIT %d,%d", objArr4), new Object[0]);
                    }
                }
                if (sQLiteCursor != null) {
                    while (sQLiteCursor.next()) {
                        byteBufferValue = sQLiteCursor.byteBufferValue(1);
                        if (byteBufferValue != null) {
                            Message TLdeserialize = Message.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                            byteBufferValue.reuse();
                            MessageObject.setUnreadFlags(TLdeserialize, sQLiteCursor.intValue(0));
                            TLdeserialize.id = sQLiteCursor.intValue(3);
                            TLdeserialize.date = sQLiteCursor.intValue(4);
                            TLdeserialize.dialog_id = this.val$dialog_id;
                            if ((TLdeserialize.flags & TLRPC.MESSAGE_FLAG_HAS_VIEWS) != 0) {
                                TLdeserialize.views = sQLiteCursor.intValue(7);
                            }
                            if (i12 != 0) {
                                TLdeserialize.ttl = sQLiteCursor.intValue(8);
                            }
                            tL_messages_messages.messages.add(TLdeserialize);
                            MessagesStorage.addUsersAndChatsFromMessage(TLdeserialize, arrayList2, arrayList3);
                            if (!(TLdeserialize.reply_to_msg_id == 0 && TLdeserialize.reply_to_random_id == 0)) {
                                if (!sQLiteCursor.isNull(6)) {
                                    byteBufferValue = sQLiteCursor.byteBufferValue(6);
                                    if (byteBufferValue != null) {
                                        TLdeserialize.replyMessage = Message.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                                        byteBufferValue.reuse();
                                        if (TLdeserialize.replyMessage != null) {
                                            MessagesStorage.addUsersAndChatsFromMessage(TLdeserialize.replyMessage, arrayList2, arrayList3);
                                        }
                                    }
                                }
                                if (TLdeserialize.replyMessage == null) {
                                    if (TLdeserialize.reply_to_msg_id != 0) {
                                        j = (long) TLdeserialize.reply_to_msg_id;
                                        if (TLdeserialize.to_id.channel_id != 0) {
                                            j |= ((long) TLdeserialize.to_id.channel_id) << 32;
                                        }
                                        if (!arrayList4.contains(Long.valueOf(j))) {
                                            arrayList4.add(Long.valueOf(j));
                                        }
                                        arrayList = (ArrayList) hashMap.get(Integer.valueOf(TLdeserialize.reply_to_msg_id));
                                        if (arrayList == null) {
                                            arrayList = new ArrayList();
                                            hashMap.put(Integer.valueOf(TLdeserialize.reply_to_msg_id), arrayList);
                                        }
                                        arrayList.add(TLdeserialize);
                                    } else {
                                        if (!arrayList4.contains(Long.valueOf(TLdeserialize.reply_to_random_id))) {
                                            arrayList4.add(Long.valueOf(TLdeserialize.reply_to_random_id));
                                        }
                                        arrayList = (ArrayList) hashMap2.get(Long.valueOf(TLdeserialize.reply_to_random_id));
                                        if (arrayList == null) {
                                            arrayList = new ArrayList();
                                            hashMap2.put(Long.valueOf(TLdeserialize.reply_to_random_id), arrayList);
                                        }
                                        arrayList.add(TLdeserialize);
                                    }
                                }
                            }
                            TLdeserialize.send_state = sQLiteCursor.intValue(2);
                            if (TLdeserialize.id > 0 && TLdeserialize.send_state != 0) {
                                TLdeserialize.send_state = 0;
                            }
                            if (i12 == 0 && !sQLiteCursor.isNull(5)) {
                                TLdeserialize.random_id = sQLiteCursor.longValue(5);
                            }
                            if (!(((int) this.val$dialog_id) != 0 || TLdeserialize.media == null || TLdeserialize.media.photo == null)) {
                                try {
                                    access$000 = MessagesStorage.this.database;
                                    objArr = new Object[1];
                                    objArr[0] = Integer.valueOf(TLdeserialize.id);
                                    queryFinalized = access$000.queryFinalized(String.format(Locale.US, "SELECT date FROM enc_tasks_v2 WHERE mid = %d", objArr), new Object[0]);
                                    if (queryFinalized.next()) {
                                        TLdeserialize.destroyTime = queryFinalized.intValue(0);
                                    }
                                    queryFinalized.dispose();
                                } catch (Throwable e322) {
                                    FileLog.m18e("tmessages", e322);
                                }
                            }
                        }
                    }
                    sQLiteCursor.dispose();
                }
                Collections.sort(tL_messages_messages.messages, new C05841());
                if ((this.val$load_type == 3 || (this.val$load_type == 2 && z)) && !tL_messages_messages.messages.isEmpty()) {
                    i11 = ((Message) tL_messages_messages.messages.get(tL_messages_messages.messages.size() - 1)).id;
                    i9 = ((Message) tL_messages_messages.messages.get(0)).id;
                    if (i11 > i3 || i9 < i3) {
                        arrayList4.clear();
                        arrayList2.clear();
                        arrayList3.clear();
                        tL_messages_messages.messages.clear();
                    }
                }
                if (this.val$load_type == 3 && tL_messages_messages.messages.size() == 1) {
                    tL_messages_messages.messages.clear();
                }
                if (!arrayList4.isEmpty()) {
                    sQLiteCursor = !hashMap.isEmpty() ? MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT data, mid, date FROM messages WHERE mid IN(%s)", new Object[]{TextUtils.join(",", arrayList4)}), new Object[0]) : MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT m.data, m.mid, m.date, r.random_id FROM randoms as r INNER JOIN messages as m ON r.mid = m.mid WHERE r.random_id IN(%s)", new Object[]{TextUtils.join(",", arrayList4)}), new Object[0]);
                    while (sQLiteCursor.next()) {
                        byteBufferValue = sQLiteCursor.byteBufferValue(0);
                        if (byteBufferValue != null) {
                            Message TLdeserialize2 = Message.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                            byteBufferValue.reuse();
                            TLdeserialize2.id = sQLiteCursor.intValue(1);
                            TLdeserialize2.date = sQLiteCursor.intValue(2);
                            TLdeserialize2.dialog_id = this.val$dialog_id;
                            MessagesStorage.addUsersAndChatsFromMessage(TLdeserialize2, arrayList2, arrayList3);
                            if (hashMap.isEmpty()) {
                                arrayList = (ArrayList) hashMap2.remove(Long.valueOf(sQLiteCursor.longValue(3)));
                                if (arrayList != null) {
                                    for (i = 0; i < arrayList.size(); i++) {
                                        Message message = (Message) arrayList.get(i);
                                        message.replyMessage = TLdeserialize2;
                                        message.reply_to_msg_id = TLdeserialize2.id;
                                    }
                                }
                            } else {
                                arrayList = (ArrayList) hashMap.get(Integer.valueOf(TLdeserialize2.id));
                                if (arrayList != null) {
                                    for (i = 0; i < arrayList.size(); i++) {
                                        ((Message) arrayList.get(i)).replyMessage = TLdeserialize2;
                                    }
                                }
                            }
                        }
                    }
                    sQLiteCursor.dispose();
                    if (!hashMap2.isEmpty()) {
                        for (Entry value : hashMap2.entrySet()) {
                            arrayList = (ArrayList) value.getValue();
                            for (i = 0; i < arrayList.size(); i++) {
                                ((Message) arrayList.get(i)).reply_to_random_id = 0;
                            }
                        }
                    }
                }
                if (!arrayList2.isEmpty()) {
                    MessagesStorage.this.getUsersInternal(TextUtils.join(",", arrayList2), tL_messages_messages.users);
                }
                if (!arrayList3.isEmpty()) {
                    MessagesStorage.this.getChatsInternal(TextUtils.join(",", arrayList3), tL_messages_messages.chats);
                }
                MessagesController.getInstance().processLoadedMessages(tL_messages_messages, this.val$dialog_id, i10, this.val$max_id, true, this.val$classGuid, i4, i5, i2, i6, this.val$load_type, this.val$isChannel, z2, this.val$loadIndex, z);
            } catch (Exception e5) {
                e322 = e5;
                i10 = i3;
                tL_messages_messages.messages.clear();
                tL_messages_messages.chats.clear();
                tL_messages_messages.users.clear();
                FileLog.m18e("tmessages", e322);
                MessagesController.getInstance().processLoadedMessages(tL_messages_messages, this.val$dialog_id, i10, this.val$max_id, true, this.val$classGuid, i4, i5, i2, i6, this.val$load_type, this.val$isChannel, z2, this.val$loadIndex, z);
            } catch (Throwable e3222) {
                th = e3222;
                i10 = i3;
                MessagesController.getInstance().processLoadedMessages(tL_messages_messages, this.val$dialog_id, i10, this.val$max_id, true, this.val$classGuid, i4, i5, i2, i6, this.val$load_type, this.val$isChannel, z2, this.val$loadIndex, z);
                throw th;
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MessagesStorage.48 */
    class AnonymousClass48 implements Runnable {
        final /* synthetic */ String val$path;
        final /* synthetic */ ArrayList val$result;
        final /* synthetic */ Semaphore val$semaphore;
        final /* synthetic */ int val$type;

        AnonymousClass48(String str, int i, ArrayList arrayList, Semaphore semaphore) {
            this.val$path = str;
            this.val$type = i;
            this.val$result = arrayList;
            this.val$semaphore = semaphore;
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void run() {
            /*
            r6 = this;
            r0 = r6.val$path;	 Catch:{ Exception -> 0x006b }
            r0 = com.hanista.mobogram.messenger.Utilities.MD5(r0);	 Catch:{ Exception -> 0x006b }
            if (r0 == 0) goto L_0x0057;
        L_0x0008:
            r1 = com.hanista.mobogram.messenger.MessagesStorage.this;	 Catch:{ Exception -> 0x006b }
            r1 = r1.database;	 Catch:{ Exception -> 0x006b }
            r2 = java.util.Locale.US;	 Catch:{ Exception -> 0x006b }
            r3 = "SELECT data FROM sent_files_v2 WHERE uid = '%s' AND type = %d";
            r4 = 2;
            r4 = new java.lang.Object[r4];	 Catch:{ Exception -> 0x006b }
            r5 = 0;
            r4[r5] = r0;	 Catch:{ Exception -> 0x006b }
            r0 = 1;
            r5 = r6.val$type;	 Catch:{ Exception -> 0x006b }
            r5 = java.lang.Integer.valueOf(r5);	 Catch:{ Exception -> 0x006b }
            r4[r0] = r5;	 Catch:{ Exception -> 0x006b }
            r0 = java.lang.String.format(r2, r3, r4);	 Catch:{ Exception -> 0x006b }
            r2 = 0;
            r2 = new java.lang.Object[r2];	 Catch:{ Exception -> 0x006b }
            r1 = r1.queryFinalized(r0, r2);	 Catch:{ Exception -> 0x006b }
            r0 = r1.next();	 Catch:{ Exception -> 0x006b }
            if (r0 == 0) goto L_0x0054;
        L_0x0033:
            r0 = 0;
            r2 = r1.byteBufferValue(r0);	 Catch:{ Exception -> 0x006b }
            if (r2 == 0) goto L_0x0054;
        L_0x003a:
            r0 = 0;
            r0 = r2.readInt32(r0);	 Catch:{ Exception -> 0x006b }
            r3 = 0;
            r0 = com.hanista.mobogram.tgnet.TLRPC.MessageMedia.TLdeserialize(r2, r0, r3);	 Catch:{ Exception -> 0x006b }
            r2.reuse();	 Catch:{ Exception -> 0x006b }
            r2 = r0 instanceof com.hanista.mobogram.tgnet.TLRPC.TL_messageMediaDocument;	 Catch:{ Exception -> 0x006b }
            if (r2 == 0) goto L_0x005d;
        L_0x004b:
            r2 = r6.val$result;	 Catch:{ Exception -> 0x006b }
            r0 = (com.hanista.mobogram.tgnet.TLRPC.TL_messageMediaDocument) r0;	 Catch:{ Exception -> 0x006b }
            r0 = r0.document;	 Catch:{ Exception -> 0x006b }
            r2.add(r0);	 Catch:{ Exception -> 0x006b }
        L_0x0054:
            r1.dispose();	 Catch:{ Exception -> 0x006b }
        L_0x0057:
            r0 = r6.val$semaphore;
            r0.release();
        L_0x005c:
            return;
        L_0x005d:
            r2 = r0 instanceof com.hanista.mobogram.tgnet.TLRPC.TL_messageMediaPhoto;	 Catch:{ Exception -> 0x006b }
            if (r2 == 0) goto L_0x0054;
        L_0x0061:
            r2 = r6.val$result;	 Catch:{ Exception -> 0x006b }
            r0 = (com.hanista.mobogram.tgnet.TLRPC.TL_messageMediaPhoto) r0;	 Catch:{ Exception -> 0x006b }
            r0 = r0.photo;	 Catch:{ Exception -> 0x006b }
            r2.add(r0);	 Catch:{ Exception -> 0x006b }
            goto L_0x0054;
        L_0x006b:
            r0 = move-exception;
            r1 = "tmessages";
            com.hanista.mobogram.messenger.FileLog.m18e(r1, r0);	 Catch:{ all -> 0x0078 }
            r0 = r6.val$semaphore;
            r0.release();
            goto L_0x005c;
        L_0x0078:
            r0 = move-exception;
            r1 = r6.val$semaphore;
            r1.release();
            throw r0;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.hanista.mobogram.messenger.MessagesStorage.48.run():void");
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MessagesStorage.49 */
    class AnonymousClass49 implements Runnable {
        final /* synthetic */ TLObject val$file;
        final /* synthetic */ String val$path;
        final /* synthetic */ int val$type;

        AnonymousClass49(String str, TLObject tLObject, int i) {
            this.val$path = str;
            this.val$file = tLObject;
            this.val$type = i;
        }

        public void run() {
            SQLitePreparedStatement sQLitePreparedStatement = null;
            try {
                String MD5 = Utilities.MD5(this.val$path);
                if (MD5 != null) {
                    MessageMedia messageMedia;
                    MessageMedia tL_messageMediaPhoto;
                    if (this.val$file instanceof Photo) {
                        tL_messageMediaPhoto = new TL_messageMediaPhoto();
                        tL_messageMediaPhoto.caption = TtmlNode.ANONYMOUS_REGION_ID;
                        tL_messageMediaPhoto.photo = (Photo) this.val$file;
                        messageMedia = tL_messageMediaPhoto;
                    } else if (this.val$file instanceof Document) {
                        tL_messageMediaPhoto = new TL_messageMediaDocument();
                        tL_messageMediaPhoto.caption = TtmlNode.ANONYMOUS_REGION_ID;
                        tL_messageMediaPhoto.document = (Document) this.val$file;
                        messageMedia = tL_messageMediaPhoto;
                    } else {
                        Object obj = sQLitePreparedStatement;
                    }
                    if (messageMedia != null) {
                        sQLitePreparedStatement = MessagesStorage.this.database.executeFast("REPLACE INTO sent_files_v2 VALUES(?, ?, ?)");
                        sQLitePreparedStatement.requery();
                        NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(messageMedia.getObjectSize());
                        messageMedia.serializeToStream(nativeByteBuffer);
                        sQLitePreparedStatement.bindString(1, MD5);
                        sQLitePreparedStatement.bindInteger(2, this.val$type);
                        sQLitePreparedStatement.bindByteBuffer(3, nativeByteBuffer);
                        sQLitePreparedStatement.step();
                        nativeByteBuffer.reuse();
                    } else if (sQLitePreparedStatement != null) {
                        sQLitePreparedStatement.dispose();
                        return;
                    } else {
                        return;
                    }
                }
                if (sQLitePreparedStatement != null) {
                    sQLitePreparedStatement.dispose();
                }
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
                if (sQLitePreparedStatement != null) {
                    sQLitePreparedStatement.dispose();
                }
            } catch (Throwable th) {
                if (sQLitePreparedStatement != null) {
                    sQLitePreparedStatement.dispose();
                }
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MessagesStorage.4 */
    class C05854 implements Runnable {
        final /* synthetic */ NativeByteBuffer val$data;
        final /* synthetic */ long val$id;

        C05854(long j, NativeByteBuffer nativeByteBuffer) {
            this.val$id = j;
            this.val$data = nativeByteBuffer;
        }

        public void run() {
            try {
                SQLitePreparedStatement executeFast = MessagesStorage.this.database.executeFast("REPLACE INTO pending_tasks VALUES(?, ?)");
                executeFast.bindLong(1, this.val$id);
                executeFast.bindByteBuffer(2, this.val$data);
                executeFast.step();
                executeFast.dispose();
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            } finally {
                this.val$data.reuse();
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MessagesStorage.50 */
    class AnonymousClass50 implements Runnable {
        final /* synthetic */ EncryptedChat val$chat;

        AnonymousClass50(EncryptedChat encryptedChat) {
            this.val$chat = encryptedChat;
        }

        public void run() {
            SQLitePreparedStatement sQLitePreparedStatement = null;
            try {
                sQLitePreparedStatement = MessagesStorage.this.database.executeFast("UPDATE enc_chats SET seq_in = ?, seq_out = ?, use_count = ?, in_seq_no = ? WHERE uid = ?");
                sQLitePreparedStatement.bindInteger(1, this.val$chat.seq_in);
                sQLitePreparedStatement.bindInteger(2, this.val$chat.seq_out);
                sQLitePreparedStatement.bindInteger(3, (this.val$chat.key_use_count_in << 16) | this.val$chat.key_use_count_out);
                sQLitePreparedStatement.bindInteger(4, this.val$chat.in_seq_no);
                sQLitePreparedStatement.bindInteger(5, this.val$chat.id);
                sQLitePreparedStatement.step();
                if (sQLitePreparedStatement != null) {
                    sQLitePreparedStatement.dispose();
                }
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
                if (sQLitePreparedStatement != null) {
                    sQLitePreparedStatement.dispose();
                }
            } catch (Throwable th) {
                if (sQLitePreparedStatement != null) {
                    sQLitePreparedStatement.dispose();
                }
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MessagesStorage.51 */
    class AnonymousClass51 implements Runnable {
        final /* synthetic */ EncryptedChat val$chat;

        AnonymousClass51(EncryptedChat encryptedChat) {
            this.val$chat = encryptedChat;
        }

        public void run() {
            SQLitePreparedStatement sQLitePreparedStatement = null;
            try {
                sQLitePreparedStatement = MessagesStorage.this.database.executeFast("UPDATE enc_chats SET ttl = ? WHERE uid = ?");
                sQLitePreparedStatement.bindInteger(1, this.val$chat.ttl);
                sQLitePreparedStatement.bindInteger(2, this.val$chat.id);
                sQLitePreparedStatement.step();
                if (sQLitePreparedStatement != null) {
                    sQLitePreparedStatement.dispose();
                }
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
                if (sQLitePreparedStatement != null) {
                    sQLitePreparedStatement.dispose();
                }
            } catch (Throwable th) {
                if (sQLitePreparedStatement != null) {
                    sQLitePreparedStatement.dispose();
                }
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MessagesStorage.52 */
    class AnonymousClass52 implements Runnable {
        final /* synthetic */ EncryptedChat val$chat;

        AnonymousClass52(EncryptedChat encryptedChat) {
            this.val$chat = encryptedChat;
        }

        public void run() {
            SQLitePreparedStatement sQLitePreparedStatement = null;
            try {
                sQLitePreparedStatement = MessagesStorage.this.database.executeFast("UPDATE enc_chats SET layer = ? WHERE uid = ?");
                sQLitePreparedStatement.bindInteger(1, this.val$chat.layer);
                sQLitePreparedStatement.bindInteger(2, this.val$chat.id);
                sQLitePreparedStatement.step();
                if (sQLitePreparedStatement != null) {
                    sQLitePreparedStatement.dispose();
                }
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
                if (sQLitePreparedStatement != null) {
                    sQLitePreparedStatement.dispose();
                }
            } catch (Throwable th) {
                if (sQLitePreparedStatement != null) {
                    sQLitePreparedStatement.dispose();
                }
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MessagesStorage.53 */
    class AnonymousClass53 implements Runnable {
        final /* synthetic */ EncryptedChat val$chat;

        AnonymousClass53(EncryptedChat encryptedChat) {
            this.val$chat = encryptedChat;
        }

        public void run() {
            int i = 1;
            SQLitePreparedStatement sQLitePreparedStatement = null;
            try {
                if ((this.val$chat.key_hash == null || this.val$chat.key_hash.length < 16) && this.val$chat.auth_key != null) {
                    this.val$chat.key_hash = AndroidUtilities.calcAuthKeyHash(this.val$chat.auth_key);
                }
                sQLitePreparedStatement = MessagesStorage.this.database.executeFast("UPDATE enc_chats SET data = ?, g = ?, authkey = ?, ttl = ?, layer = ?, seq_in = ?, seq_out = ?, use_count = ?, exchange_id = ?, key_date = ?, fprint = ?, fauthkey = ?, khash = ?, in_seq_no = ? WHERE uid = ?");
                NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(this.val$chat.getObjectSize());
                NativeByteBuffer nativeByteBuffer2 = new NativeByteBuffer(this.val$chat.a_or_b != null ? this.val$chat.a_or_b.length : 1);
                NativeByteBuffer nativeByteBuffer3 = new NativeByteBuffer(this.val$chat.auth_key != null ? this.val$chat.auth_key.length : 1);
                NativeByteBuffer nativeByteBuffer4 = new NativeByteBuffer(this.val$chat.future_auth_key != null ? this.val$chat.future_auth_key.length : 1);
                if (this.val$chat.key_hash != null) {
                    i = this.val$chat.key_hash.length;
                }
                NativeByteBuffer nativeByteBuffer5 = new NativeByteBuffer(i);
                this.val$chat.serializeToStream(nativeByteBuffer);
                sQLitePreparedStatement.bindByteBuffer(1, nativeByteBuffer);
                if (this.val$chat.a_or_b != null) {
                    nativeByteBuffer2.writeBytes(this.val$chat.a_or_b);
                }
                if (this.val$chat.auth_key != null) {
                    nativeByteBuffer3.writeBytes(this.val$chat.auth_key);
                }
                if (this.val$chat.future_auth_key != null) {
                    nativeByteBuffer4.writeBytes(this.val$chat.future_auth_key);
                }
                if (this.val$chat.key_hash != null) {
                    nativeByteBuffer5.writeBytes(this.val$chat.key_hash);
                }
                sQLitePreparedStatement.bindByteBuffer(2, nativeByteBuffer2);
                sQLitePreparedStatement.bindByteBuffer(3, nativeByteBuffer3);
                sQLitePreparedStatement.bindInteger(4, this.val$chat.ttl);
                sQLitePreparedStatement.bindInteger(5, this.val$chat.layer);
                sQLitePreparedStatement.bindInteger(6, this.val$chat.seq_in);
                sQLitePreparedStatement.bindInteger(7, this.val$chat.seq_out);
                sQLitePreparedStatement.bindInteger(8, (this.val$chat.key_use_count_in << 16) | this.val$chat.key_use_count_out);
                sQLitePreparedStatement.bindLong(9, this.val$chat.exchange_id);
                sQLitePreparedStatement.bindInteger(10, this.val$chat.key_create_date);
                sQLitePreparedStatement.bindLong(11, this.val$chat.future_key_fingerprint);
                sQLitePreparedStatement.bindByteBuffer(12, nativeByteBuffer4);
                sQLitePreparedStatement.bindByteBuffer(13, nativeByteBuffer5);
                sQLitePreparedStatement.bindInteger(14, this.val$chat.in_seq_no);
                sQLitePreparedStatement.bindInteger(15, this.val$chat.id);
                sQLitePreparedStatement.step();
                nativeByteBuffer.reuse();
                nativeByteBuffer2.reuse();
                nativeByteBuffer3.reuse();
                nativeByteBuffer4.reuse();
                nativeByteBuffer5.reuse();
                if (sQLitePreparedStatement != null) {
                    sQLitePreparedStatement.dispose();
                }
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
                if (sQLitePreparedStatement != null) {
                    sQLitePreparedStatement.dispose();
                }
            } catch (Throwable th) {
                if (sQLitePreparedStatement != null) {
                    sQLitePreparedStatement.dispose();
                }
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MessagesStorage.54 */
    class AnonymousClass54 implements Runnable {
        final /* synthetic */ long val$did;
        final /* synthetic */ boolean[] val$result;
        final /* synthetic */ Semaphore val$semaphore;

        AnonymousClass54(long j, boolean[] zArr, Semaphore semaphore) {
            this.val$did = j;
            this.val$result = zArr;
            this.val$semaphore = semaphore;
        }

        public void run() {
            try {
                SQLiteCursor queryFinalized = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT mid FROM messages WHERE uid = %d LIMIT 1", new Object[]{Long.valueOf(this.val$did)}), new Object[0]);
                this.val$result[0] = queryFinalized.next();
                queryFinalized.dispose();
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            } finally {
                this.val$semaphore.release();
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MessagesStorage.55 */
    class AnonymousClass55 implements Runnable {
        final /* synthetic */ int val$date;
        final /* synthetic */ boolean[] val$result;
        final /* synthetic */ Semaphore val$semaphore;

        AnonymousClass55(int i, boolean[] zArr, Semaphore semaphore) {
            this.val$date = i;
            this.val$result = zArr;
            this.val$semaphore = semaphore;
        }

        public void run() {
            try {
                SQLiteCursor queryFinalized = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT mid FROM messages WHERE uid = 777000 AND date = %d AND mid < 0 LIMIT 1", new Object[]{Integer.valueOf(this.val$date)}), new Object[0]);
                this.val$result[0] = queryFinalized.next();
                queryFinalized.dispose();
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            } finally {
                this.val$semaphore.release();
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MessagesStorage.56 */
    class AnonymousClass56 implements Runnable {
        final /* synthetic */ int val$chat_id;
        final /* synthetic */ ArrayList val$result;
        final /* synthetic */ Semaphore val$semaphore;

        AnonymousClass56(int i, ArrayList arrayList, Semaphore semaphore) {
            this.val$chat_id = i;
            this.val$result = arrayList;
            this.val$semaphore = semaphore;
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void run() {
            /*
            r5 = this;
            r0 = new java.util.ArrayList;	 Catch:{ Exception -> 0x0062 }
            r0.<init>();	 Catch:{ Exception -> 0x0062 }
            r1 = new java.util.ArrayList;	 Catch:{ Exception -> 0x0062 }
            r1.<init>();	 Catch:{ Exception -> 0x0062 }
            r2 = com.hanista.mobogram.messenger.MessagesStorage.this;	 Catch:{ Exception -> 0x0062 }
            r3 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0062 }
            r3.<init>();	 Catch:{ Exception -> 0x0062 }
            r4 = "";
            r3 = r3.append(r4);	 Catch:{ Exception -> 0x0062 }
            r4 = r5.val$chat_id;	 Catch:{ Exception -> 0x0062 }
            r3 = r3.append(r4);	 Catch:{ Exception -> 0x0062 }
            r3 = r3.toString();	 Catch:{ Exception -> 0x0062 }
            r2.getEncryptedChatsInternal(r3, r1, r0);	 Catch:{ Exception -> 0x0062 }
            r2 = r1.isEmpty();	 Catch:{ Exception -> 0x0062 }
            if (r2 != 0) goto L_0x005c;
        L_0x002b:
            r2 = r0.isEmpty();	 Catch:{ Exception -> 0x0062 }
            if (r2 != 0) goto L_0x005c;
        L_0x0031:
            r2 = new java.util.ArrayList;	 Catch:{ Exception -> 0x0062 }
            r2.<init>();	 Catch:{ Exception -> 0x0062 }
            r3 = com.hanista.mobogram.messenger.MessagesStorage.this;	 Catch:{ Exception -> 0x0062 }
            r4 = ",";
            r0 = android.text.TextUtils.join(r4, r0);	 Catch:{ Exception -> 0x0062 }
            r3.getUsersInternal(r0, r2);	 Catch:{ Exception -> 0x0062 }
            r0 = r2.isEmpty();	 Catch:{ Exception -> 0x0062 }
            if (r0 != 0) goto L_0x005c;
        L_0x0048:
            r0 = r5.val$result;	 Catch:{ Exception -> 0x0062 }
            r3 = 0;
            r1 = r1.get(r3);	 Catch:{ Exception -> 0x0062 }
            r0.add(r1);	 Catch:{ Exception -> 0x0062 }
            r0 = r5.val$result;	 Catch:{ Exception -> 0x0062 }
            r1 = 0;
            r1 = r2.get(r1);	 Catch:{ Exception -> 0x0062 }
            r0.add(r1);	 Catch:{ Exception -> 0x0062 }
        L_0x005c:
            r0 = r5.val$semaphore;
            r0.release();
        L_0x0061:
            return;
        L_0x0062:
            r0 = move-exception;
            r1 = "tmessages";
            com.hanista.mobogram.messenger.FileLog.m18e(r1, r0);	 Catch:{ all -> 0x006f }
            r0 = r5.val$semaphore;
            r0.release();
            goto L_0x0061;
        L_0x006f:
            r0 = move-exception;
            r1 = r5.val$semaphore;
            r1.release();
            throw r0;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.hanista.mobogram.messenger.MessagesStorage.56.run():void");
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MessagesStorage.57 */
    class AnonymousClass57 implements Runnable {
        final /* synthetic */ EncryptedChat val$chat;
        final /* synthetic */ TL_dialog val$dialog;
        final /* synthetic */ User val$user;

        AnonymousClass57(EncryptedChat encryptedChat, User user, TL_dialog tL_dialog) {
            this.val$chat = encryptedChat;
            this.val$user = user;
            this.val$dialog = tL_dialog;
        }

        public void run() {
            int i = 1;
            try {
                if ((this.val$chat.key_hash == null || this.val$chat.key_hash.length < 16) && this.val$chat.auth_key != null) {
                    this.val$chat.key_hash = AndroidUtilities.calcAuthKeyHash(this.val$chat.auth_key);
                }
                SQLitePreparedStatement executeFast = MessagesStorage.this.database.executeFast("REPLACE INTO enc_chats VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(this.val$chat.getObjectSize());
                NativeByteBuffer nativeByteBuffer2 = new NativeByteBuffer(this.val$chat.a_or_b != null ? this.val$chat.a_or_b.length : 1);
                NativeByteBuffer nativeByteBuffer3 = new NativeByteBuffer(this.val$chat.auth_key != null ? this.val$chat.auth_key.length : 1);
                NativeByteBuffer nativeByteBuffer4 = new NativeByteBuffer(this.val$chat.future_auth_key != null ? this.val$chat.future_auth_key.length : 1);
                if (this.val$chat.key_hash != null) {
                    i = this.val$chat.key_hash.length;
                }
                NativeByteBuffer nativeByteBuffer5 = new NativeByteBuffer(i);
                this.val$chat.serializeToStream(nativeByteBuffer);
                executeFast.bindInteger(1, this.val$chat.id);
                executeFast.bindInteger(2, this.val$user.id);
                executeFast.bindString(3, MessagesStorage.this.formatUserSearchName(this.val$user));
                executeFast.bindByteBuffer(4, nativeByteBuffer);
                if (this.val$chat.a_or_b != null) {
                    nativeByteBuffer2.writeBytes(this.val$chat.a_or_b);
                }
                if (this.val$chat.auth_key != null) {
                    nativeByteBuffer3.writeBytes(this.val$chat.auth_key);
                }
                if (this.val$chat.future_auth_key != null) {
                    nativeByteBuffer4.writeBytes(this.val$chat.future_auth_key);
                }
                if (this.val$chat.key_hash != null) {
                    nativeByteBuffer5.writeBytes(this.val$chat.key_hash);
                }
                executeFast.bindByteBuffer(5, nativeByteBuffer2);
                executeFast.bindByteBuffer(6, nativeByteBuffer3);
                executeFast.bindInteger(7, this.val$chat.ttl);
                executeFast.bindInteger(8, this.val$chat.layer);
                executeFast.bindInteger(9, this.val$chat.seq_in);
                executeFast.bindInteger(10, this.val$chat.seq_out);
                executeFast.bindInteger(11, (this.val$chat.key_use_count_in << 16) | this.val$chat.key_use_count_out);
                executeFast.bindLong(12, this.val$chat.exchange_id);
                executeFast.bindInteger(13, this.val$chat.key_create_date);
                executeFast.bindLong(14, this.val$chat.future_key_fingerprint);
                executeFast.bindByteBuffer(15, nativeByteBuffer4);
                executeFast.bindByteBuffer(16, nativeByteBuffer5);
                executeFast.bindInteger(17, this.val$chat.in_seq_no);
                executeFast.step();
                executeFast.dispose();
                nativeByteBuffer.reuse();
                nativeByteBuffer2.reuse();
                nativeByteBuffer3.reuse();
                nativeByteBuffer4.reuse();
                nativeByteBuffer5.reuse();
                if (this.val$dialog != null) {
                    SQLitePreparedStatement executeFast2 = MessagesStorage.this.database.executeFast("REPLACE INTO dialogs VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                    executeFast2.bindLong(1, this.val$dialog.id);
                    executeFast2.bindInteger(2, this.val$dialog.last_message_date);
                    executeFast2.bindInteger(3, this.val$dialog.unread_count);
                    executeFast2.bindInteger(4, this.val$dialog.top_message);
                    executeFast2.bindInteger(5, this.val$dialog.read_inbox_max_id);
                    executeFast2.bindInteger(6, this.val$dialog.read_outbox_max_id);
                    executeFast2.bindInteger(7, 0);
                    executeFast2.bindInteger(8, 0);
                    executeFast2.bindInteger(9, this.val$dialog.pts);
                    executeFast2.bindInteger(10, 0);
                    executeFast2.step();
                    executeFast2.dispose();
                }
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MessagesStorage.58 */
    class AnonymousClass58 implements Runnable {
        final /* synthetic */ ArrayList val$chats;
        final /* synthetic */ ArrayList val$users;
        final /* synthetic */ boolean val$withTransaction;

        AnonymousClass58(ArrayList arrayList, ArrayList arrayList2, boolean z) {
            this.val$users = arrayList;
            this.val$chats = arrayList2;
            this.val$withTransaction = z;
        }

        public void run() {
            MessagesStorage.this.putUsersAndChatsInternal(this.val$users, this.val$chats, this.val$withTransaction);
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MessagesStorage.59 */
    class AnonymousClass59 implements Runnable {
        final /* synthetic */ long val$id;
        final /* synthetic */ boolean val$move;
        final /* synthetic */ int val$type;

        AnonymousClass59(boolean z, int i, long j) {
            this.val$move = z;
            this.val$type = i;
            this.val$id = j;
        }

        public void run() {
            try {
                if (this.val$move) {
                    SQLiteCursor queryFinalized = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT min(date) FROM download_queue WHERE type = %d", new Object[]{Integer.valueOf(this.val$type)}), new Object[0]);
                    int intValue = queryFinalized.next() ? queryFinalized.intValue(0) : -1;
                    queryFinalized.dispose();
                    if (intValue != -1) {
                        MessagesStorage.this.database.executeFast(String.format(Locale.US, "UPDATE download_queue SET date = %d WHERE uid = %d AND type = %d", new Object[]{Integer.valueOf(intValue - 1), Long.valueOf(this.val$id), Integer.valueOf(this.val$type)})).stepThis().dispose();
                        return;
                    }
                    return;
                }
                MessagesStorage.this.database.executeFast(String.format(Locale.US, "DELETE FROM download_queue WHERE uid = %d AND type = %d", new Object[]{Long.valueOf(this.val$id), Integer.valueOf(this.val$type)})).stepThis().dispose();
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MessagesStorage.5 */
    class C05865 implements Runnable {
        final /* synthetic */ long val$id;

        C05865(long j) {
            this.val$id = j;
        }

        public void run() {
            try {
                MessagesStorage.this.database.executeFast("DELETE FROM pending_tasks WHERE id = " + this.val$id).stepThis().dispose();
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MessagesStorage.60 */
    class AnonymousClass60 implements Runnable {
        final /* synthetic */ int val$type;

        AnonymousClass60(int i) {
            this.val$type = i;
        }

        public void run() {
            try {
                if (this.val$type == 0) {
                    MessagesStorage.this.database.executeFast("DELETE FROM download_queue WHERE 1").stepThis().dispose();
                    return;
                }
                MessagesStorage.this.database.executeFast(String.format(Locale.US, "DELETE FROM download_queue WHERE type = %d", new Object[]{Integer.valueOf(this.val$type)})).stepThis().dispose();
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MessagesStorage.61 */
    class AnonymousClass61 implements Runnable {
        final /* synthetic */ int val$type;

        /* renamed from: com.hanista.mobogram.messenger.MessagesStorage.61.1 */
        class C05901 implements Runnable {
            final /* synthetic */ ArrayList val$objects;

            C05901(ArrayList arrayList) {
                this.val$objects = arrayList;
            }

            public void run() {
                MediaController.m71a().m145a(AnonymousClass61.this.val$type, this.val$objects);
            }
        }

        AnonymousClass61(int i) {
            this.val$type = i;
        }

        public void run() {
            try {
                ArrayList arrayList = new ArrayList();
                SQLiteCursor queryFinalized = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT uid, type, data FROM download_queue WHERE type = %d ORDER BY date DESC LIMIT 3", new Object[]{Integer.valueOf(this.val$type)}), new Object[0]);
                while (queryFinalized.next()) {
                    DownloadObject downloadObject = new DownloadObject();
                    downloadObject.type = queryFinalized.intValue(1);
                    downloadObject.id = queryFinalized.longValue(0);
                    AbstractSerializedData byteBufferValue = queryFinalized.byteBufferValue(2);
                    if (byteBufferValue != null) {
                        MessageMedia TLdeserialize = MessageMedia.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                        byteBufferValue.reuse();
                        if (TLdeserialize.document != null) {
                            downloadObject.object = TLdeserialize.document;
                        } else if (TLdeserialize.photo != null) {
                            downloadObject.object = FileLoader.getClosestPhotoSizeWithSize(TLdeserialize.photo.sizes, AndroidUtilities.getPhotoSize());
                        }
                    }
                    arrayList.add(downloadObject);
                }
                queryFinalized.dispose();
                AndroidUtilities.runOnUIThread(new C05901(arrayList));
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MessagesStorage.62 */
    class AnonymousClass62 implements Runnable {
        final /* synthetic */ HashMap val$webPages;

        /* renamed from: com.hanista.mobogram.messenger.MessagesStorage.62.1 */
        class C05911 implements Runnable {
            final /* synthetic */ ArrayList val$messages;

            C05911(ArrayList arrayList) {
                this.val$messages = arrayList;
            }

            public void run() {
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.didReceivedWebpages, this.val$messages);
            }
        }

        AnonymousClass62(HashMap hashMap) {
            this.val$webPages = hashMap;
        }

        public void run() {
            try {
                String join = TextUtils.join(",", this.val$webPages.keySet());
                SQLiteCursor queryFinalized = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT mid FROM webpage_pending WHERE id IN (%s)", new Object[]{join}), new Object[0]);
                Iterable arrayList = new ArrayList();
                while (queryFinalized.next()) {
                    arrayList.add(Long.valueOf(queryFinalized.longValue(0)));
                }
                queryFinalized.dispose();
                if (!arrayList.isEmpty()) {
                    ArrayList arrayList2 = new ArrayList();
                    SQLiteCursor queryFinalized2 = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT mid, data FROM messages WHERE mid IN (%s)", new Object[]{TextUtils.join(",", arrayList)}), new Object[0]);
                    while (queryFinalized2.next()) {
                        int intValue = queryFinalized2.intValue(0);
                        AbstractSerializedData byteBufferValue = queryFinalized2.byteBufferValue(1);
                        if (byteBufferValue != null) {
                            Message TLdeserialize = Message.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                            byteBufferValue.reuse();
                            if (TLdeserialize.media instanceof TL_messageMediaWebPage) {
                                TLdeserialize.id = intValue;
                                TLdeserialize.media.webpage = (WebPage) this.val$webPages.get(Long.valueOf(TLdeserialize.media.webpage.id));
                                arrayList2.add(TLdeserialize);
                            }
                        }
                    }
                    queryFinalized2.dispose();
                    MessagesStorage.this.database.executeFast(String.format(Locale.US, "DELETE FROM webpage_pending WHERE id IN (%s)", new Object[]{join})).stepThis().dispose();
                    if (!arrayList2.isEmpty()) {
                        MessagesStorage.this.database.beginTransaction();
                        SQLitePreparedStatement executeFast = MessagesStorage.this.database.executeFast("UPDATE messages SET data = ? WHERE mid = ?");
                        SQLitePreparedStatement executeFast2 = MessagesStorage.this.database.executeFast("UPDATE media_v2 SET data = ? WHERE mid = ?");
                        for (int i = 0; i < arrayList2.size(); i++) {
                            Message message = (Message) arrayList2.get(i);
                            NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(message.getObjectSize());
                            message.serializeToStream(nativeByteBuffer);
                            long j = (long) message.id;
                            long j2 = message.to_id.channel_id != 0 ? (((long) message.to_id.channel_id) << 32) | j : j;
                            executeFast.requery();
                            executeFast.bindByteBuffer(1, nativeByteBuffer);
                            executeFast.bindLong(2, j2);
                            executeFast.step();
                            executeFast2.requery();
                            executeFast2.bindByteBuffer(1, nativeByteBuffer);
                            executeFast2.bindLong(2, j2);
                            executeFast2.step();
                            nativeByteBuffer.reuse();
                        }
                        executeFast.dispose();
                        executeFast2.dispose();
                        MessagesStorage.this.database.commitTransaction();
                        AndroidUtilities.runOnUIThread(new C05911(arrayList2));
                    }
                }
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MessagesStorage.63 */
    class AnonymousClass63 implements Runnable {
        final /* synthetic */ int val$channel_id;
        final /* synthetic */ TL_updates_channelDifferenceTooLong val$difference;
        final /* synthetic */ int val$newDialogType;

        /* renamed from: com.hanista.mobogram.messenger.MessagesStorage.63.1 */
        class C05921 implements Runnable {
            final /* synthetic */ long val$did;

            C05921(long j) {
                this.val$did = j;
            }

            public void run() {
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.removeAllMessagesFromDialog, Long.valueOf(this.val$did), Boolean.valueOf(true));
            }
        }

        AnonymousClass63(int i, int i2, TL_updates_channelDifferenceTooLong tL_updates_channelDifferenceTooLong) {
            this.val$channel_id = i;
            this.val$newDialogType = i2;
            this.val$difference = tL_updates_channelDifferenceTooLong;
        }

        public void run() {
            Object obj = null;
            try {
                long j = (long) (-this.val$channel_id);
                if (this.val$newDialogType != 0) {
                    SQLiteCursor queryFinalized = MessagesStorage.this.database.queryFinalized("SELECT pts FROM dialogs WHERE did = " + j, new Object[0]);
                    if (!queryFinalized.next()) {
                        obj = 1;
                    }
                    queryFinalized.dispose();
                }
                MessagesStorage.this.database.executeFast("DELETE FROM messages WHERE uid = " + j).stepThis().dispose();
                MessagesStorage.this.database.executeFast("DELETE FROM bot_keyboard WHERE uid = " + j).stepThis().dispose();
                MessagesStorage.this.database.executeFast("DELETE FROM media_counts_v2 WHERE uid = " + j).stepThis().dispose();
                MessagesStorage.this.database.executeFast("DELETE FROM media_v2 WHERE uid = " + j).stepThis().dispose();
                MessagesStorage.this.database.executeFast("DELETE FROM messages_holes WHERE uid = " + j).stepThis().dispose();
                MessagesStorage.this.database.executeFast("DELETE FROM media_holes_v2 WHERE uid = " + j).stepThis().dispose();
                BotQuery.clearBotKeyboard(j, null);
                messages_Dialogs tL_messages_dialogs = new TL_messages_dialogs();
                tL_messages_dialogs.chats.addAll(this.val$difference.chats);
                tL_messages_dialogs.users.addAll(this.val$difference.users);
                tL_messages_dialogs.messages.addAll(this.val$difference.messages);
                TL_dialog tL_dialog = new TL_dialog();
                tL_dialog.id = j;
                tL_dialog.flags = 1;
                tL_dialog.peer = new TL_peerChannel();
                tL_dialog.peer.channel_id = this.val$channel_id;
                tL_dialog.top_message = this.val$difference.top_message;
                tL_dialog.read_inbox_max_id = this.val$difference.read_inbox_max_id;
                tL_dialog.read_outbox_max_id = this.val$difference.read_outbox_max_id;
                tL_dialog.unread_count = this.val$difference.unread_count;
                tL_dialog.notify_settings = null;
                tL_dialog.pts = this.val$difference.pts;
                tL_messages_dialogs.dialogs.add(tL_dialog);
                MessagesStorage.this.putDialogsInternal(tL_messages_dialogs);
                MessagesStorage.getInstance().updateDialogsWithDeletedMessages(new ArrayList(), false, this.val$channel_id);
                AndroidUtilities.runOnUIThread(new C05921(j));
                if (obj == null) {
                    return;
                }
                if (this.val$newDialogType == 1) {
                    MessagesController.getInstance().checkChannelInviter(this.val$channel_id);
                } else {
                    MessagesController.getInstance().generateJoinMessage(this.val$channel_id, false);
                }
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MessagesStorage.64 */
    class AnonymousClass64 implements Runnable {
        final /* synthetic */ SparseArray val$channelViews;
        final /* synthetic */ boolean val$isChannel;

        AnonymousClass64(SparseArray sparseArray, boolean z) {
            this.val$channelViews = sparseArray;
            this.val$isChannel = z;
        }

        public void run() {
            try {
                MessagesStorage.this.database.beginTransaction();
                SQLitePreparedStatement executeFast = MessagesStorage.this.database.executeFast("UPDATE messages SET media = max((SELECT media FROM messages WHERE mid = ?), ?) WHERE mid = ?");
                for (int i = 0; i < this.val$channelViews.size(); i++) {
                    int keyAt = this.val$channelViews.keyAt(i);
                    SparseIntArray sparseIntArray = (SparseIntArray) this.val$channelViews.get(keyAt);
                    for (int i2 = 0; i2 < sparseIntArray.size(); i2++) {
                        int i3 = sparseIntArray.get(sparseIntArray.keyAt(i2));
                        long keyAt2 = (long) sparseIntArray.keyAt(i2);
                        if (this.val$isChannel) {
                            keyAt2 |= ((long) (-keyAt)) << 32;
                        }
                        executeFast.requery();
                        executeFast.bindLong(1, keyAt2);
                        executeFast.bindInteger(2, i3);
                        executeFast.bindLong(3, keyAt2);
                        executeFast.step();
                    }
                }
                executeFast.dispose();
                MessagesStorage.this.database.commitTransaction();
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MessagesStorage.65 */
    class AnonymousClass65 implements Runnable {
        final /* synthetic */ int val$downloadMediaMaskFinal;

        AnonymousClass65(int i) {
            this.val$downloadMediaMaskFinal = i;
        }

        public void run() {
            MediaController.m71a().m163b(this.val$downloadMediaMaskFinal);
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MessagesStorage.66 */
    class AnonymousClass66 implements Runnable {
        final /* synthetic */ int val$downloadMediaMaskFinal;

        AnonymousClass66(int i) {
            this.val$downloadMediaMaskFinal = i;
        }

        public void run() {
            MediaController.m71a().m176f(this.val$downloadMediaMaskFinal);
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MessagesStorage.67 */
    class AnonymousClass67 implements Runnable {
        final /* synthetic */ boolean val$doNotUpdateDialogDate;
        final /* synthetic */ int val$downloadMask;
        final /* synthetic */ boolean val$ifNoLastMessage;
        final /* synthetic */ ArrayList val$messages;
        final /* synthetic */ boolean val$withTransaction;

        AnonymousClass67(ArrayList arrayList, boolean z, boolean z2, int i, boolean z3) {
            this.val$messages = arrayList;
            this.val$withTransaction = z;
            this.val$doNotUpdateDialogDate = z2;
            this.val$downloadMask = i;
            this.val$ifNoLastMessage = z3;
        }

        public void run() {
            MessagesStorage.this.putMessagesInternal(this.val$messages, this.val$withTransaction, this.val$doNotUpdateDialogDate, this.val$downloadMask, this.val$ifNoLastMessage);
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MessagesStorage.68 */
    class AnonymousClass68 implements Runnable {
        final /* synthetic */ Message val$message;

        AnonymousClass68(Message message) {
            this.val$message = message;
        }

        public void run() {
            try {
                long j = (long) this.val$message.id;
                if (this.val$message.to_id.channel_id != 0) {
                    j |= ((long) this.val$message.to_id.channel_id) << 32;
                }
                MessagesStorage.this.database.executeFast("UPDATE messages SET send_state = 2 WHERE mid = " + j).stepThis().dispose();
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MessagesStorage.69 */
    class AnonymousClass69 implements Runnable {
        final /* synthetic */ int val$mid;
        final /* synthetic */ int val$seq_in;
        final /* synthetic */ int val$seq_out;

        AnonymousClass69(int i, int i2, int i3) {
            this.val$mid = i;
            this.val$seq_in = i2;
            this.val$seq_out = i3;
        }

        public void run() {
            try {
                SQLitePreparedStatement executeFast = MessagesStorage.this.database.executeFast("REPLACE INTO messages_seq VALUES(?, ?, ?)");
                executeFast.requery();
                executeFast.bindInteger(1, this.val$mid);
                executeFast.bindInteger(2, this.val$seq_in);
                executeFast.bindInteger(3, this.val$seq_out);
                executeFast.step();
                executeFast.dispose();
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MessagesStorage.6 */
    class C05936 implements Runnable {

        /* renamed from: com.hanista.mobogram.messenger.MessagesStorage.6.1 */
        class C05871 implements Runnable {
            final /* synthetic */ Chat val$chat;
            final /* synthetic */ long val$taskId;

            C05871(Chat chat, long j) {
                this.val$chat = chat;
                this.val$taskId = j;
            }

            public void run() {
                MessagesController.getInstance().loadUnknownChannel(this.val$chat, this.val$taskId);
            }
        }

        /* renamed from: com.hanista.mobogram.messenger.MessagesStorage.6.2 */
        class C05882 implements Runnable {
            final /* synthetic */ int val$channelId;
            final /* synthetic */ int val$newDialogType;
            final /* synthetic */ long val$taskId;

            C05882(int i, int i2, long j) {
                this.val$channelId = i;
                this.val$newDialogType = i2;
                this.val$taskId = j;
            }

            public void run() {
                MessagesController.getInstance().getChannelDifference(this.val$channelId, this.val$newDialogType, this.val$taskId);
            }
        }

        /* renamed from: com.hanista.mobogram.messenger.MessagesStorage.6.3 */
        class C05893 implements Runnable {
            final /* synthetic */ TL_dialog val$dialog;
            final /* synthetic */ InputPeer val$peer;
            final /* synthetic */ long val$taskId;

            C05893(TL_dialog tL_dialog, InputPeer inputPeer, long j) {
                this.val$dialog = tL_dialog;
                this.val$peer = inputPeer;
                this.val$taskId = j;
            }

            public void run() {
                MessagesController.getInstance().checkLastDialogMessage(this.val$dialog, this.val$peer, this.val$taskId);
            }
        }

        C05936() {
        }

        public void run() {
            try {
                SQLiteCursor queryFinalized = MessagesStorage.this.database.queryFinalized("SELECT id, data FROM pending_tasks WHERE 1", new Object[0]);
                while (queryFinalized.next()) {
                    long longValue = queryFinalized.longValue(0);
                    AbstractSerializedData byteBufferValue = queryFinalized.byteBufferValue(1);
                    if (byteBufferValue != null) {
                        switch (byteBufferValue.readInt32(false)) {
                            case VideoPlayer.TRACK_DEFAULT /*0*/:
                                Chat TLdeserialize = Chat.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                                if (TLdeserialize != null) {
                                    Utilities.stageQueue.postRunnable(new C05871(TLdeserialize, longValue));
                                    break;
                                }
                                break;
                            case VideoPlayer.TYPE_AUDIO /*1*/:
                                Utilities.stageQueue.postRunnable(new C05882(byteBufferValue.readInt32(false), byteBufferValue.readInt32(false), longValue));
                                break;
                            case VideoPlayer.STATE_PREPARING /*2*/:
                                TL_dialog tL_dialog = new TL_dialog();
                                tL_dialog.id = byteBufferValue.readInt64(false);
                                tL_dialog.top_message = byteBufferValue.readInt32(false);
                                tL_dialog.read_inbox_max_id = byteBufferValue.readInt32(false);
                                tL_dialog.read_outbox_max_id = byteBufferValue.readInt32(false);
                                tL_dialog.unread_count = byteBufferValue.readInt32(false);
                                tL_dialog.last_message_date = byteBufferValue.readInt32(false);
                                tL_dialog.pts = byteBufferValue.readInt32(false);
                                tL_dialog.flags = byteBufferValue.readInt32(false);
                                AndroidUtilities.runOnUIThread(new C05893(tL_dialog, InputPeer.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false), longValue));
                                break;
                            case VideoPlayer.STATE_BUFFERING /*3*/:
                                long readInt64 = byteBufferValue.readInt64(false);
                                SendMessagesHelper.getInstance().sendGame(InputPeer.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false), (TL_inputMediaGame) InputMedia.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false), readInt64, longValue);
                                break;
                        }
                        byteBufferValue.reuse();
                    }
                }
                queryFinalized.dispose();
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MessagesStorage.70 */
    class AnonymousClass70 implements Runnable {
        final /* synthetic */ Integer val$_oldId;
        final /* synthetic */ int val$channelId;
        final /* synthetic */ int val$date;
        final /* synthetic */ int val$newId;
        final /* synthetic */ long val$random_id;

        AnonymousClass70(long j, Integer num, int i, int i2, int i3) {
            this.val$random_id = j;
            this.val$_oldId = num;
            this.val$newId = i;
            this.val$date = i2;
            this.val$channelId = i3;
        }

        public void run() {
            MessagesStorage.this.updateMessageStateAndIdInternal(this.val$random_id, this.val$_oldId, this.val$newId, this.val$date, this.val$channelId);
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MessagesStorage.71 */
    class AnonymousClass71 implements Runnable {
        final /* synthetic */ boolean val$onlyStatus;
        final /* synthetic */ ArrayList val$users;
        final /* synthetic */ boolean val$withTransaction;

        AnonymousClass71(ArrayList arrayList, boolean z, boolean z2) {
            this.val$users = arrayList;
            this.val$onlyStatus = z;
            this.val$withTransaction = z2;
        }

        public void run() {
            MessagesStorage.this.updateUsersInternal(this.val$users, this.val$onlyStatus, this.val$withTransaction);
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MessagesStorage.72 */
    class AnonymousClass72 implements Runnable {
        final /* synthetic */ ArrayList val$mids;

        AnonymousClass72(ArrayList arrayList) {
            this.val$mids = arrayList;
        }

        public void run() {
            try {
                MessagesStorage.this.database.executeFast(String.format(Locale.US, "UPDATE messages SET read_state = read_state | 2 WHERE mid IN (%s)", new Object[]{TextUtils.join(",", this.val$mids)})).stepThis().dispose();
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MessagesStorage.73 */
    class AnonymousClass73 implements Runnable {
        final /* synthetic */ HashMap val$encryptedMessages;
        final /* synthetic */ SparseArray val$inbox;
        final /* synthetic */ SparseArray val$outbox;

        AnonymousClass73(SparseArray sparseArray, SparseArray sparseArray2, HashMap hashMap) {
            this.val$inbox = sparseArray;
            this.val$outbox = sparseArray2;
            this.val$encryptedMessages = hashMap;
        }

        public void run() {
            MessagesStorage.this.markMessagesAsReadInternal(this.val$inbox, this.val$outbox, this.val$encryptedMessages);
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MessagesStorage.74 */
    class AnonymousClass74 implements Runnable {
        final /* synthetic */ ArrayList val$messages;

        /* renamed from: com.hanista.mobogram.messenger.MessagesStorage.74.1 */
        class C05941 implements Runnable {
            final /* synthetic */ ArrayList val$mids;

            C05941(ArrayList arrayList) {
                this.val$mids = arrayList;
            }

            public void run() {
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.messagesDeleted, this.val$mids, Integer.valueOf(0));
            }
        }

        AnonymousClass74(ArrayList arrayList) {
            this.val$messages = arrayList;
        }

        public void run() {
            try {
                String join = TextUtils.join(",", this.val$messages);
                SQLiteCursor queryFinalized = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT mid FROM randoms WHERE random_id IN(%s)", new Object[]{join}), new Object[0]);
                ArrayList arrayList = new ArrayList();
                while (queryFinalized.next()) {
                    arrayList.add(Integer.valueOf(queryFinalized.intValue(0)));
                }
                queryFinalized.dispose();
                if (!arrayList.isEmpty()) {
                    AndroidUtilities.runOnUIThread(new C05941(arrayList));
                    MessagesStorage.getInstance().updateDialogsWithReadMessagesInternal(arrayList, null, null);
                    MessagesStorage.getInstance().markMessagesAsDeletedInternal(arrayList, 0);
                    MessagesStorage.getInstance().updateDialogsWithDeletedMessagesInternal(arrayList, 0);
                }
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MessagesStorage.75 */
    class AnonymousClass75 implements Runnable {
        final /* synthetic */ int val$channelId;
        final /* synthetic */ ArrayList val$messages;

        AnonymousClass75(ArrayList arrayList, int i) {
            this.val$messages = arrayList;
            this.val$channelId = i;
        }

        public void run() {
            MessagesStorage.this.updateDialogsWithDeletedMessagesInternal(this.val$messages, this.val$channelId);
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MessagesStorage.76 */
    class AnonymousClass76 implements Runnable {
        final /* synthetic */ int val$channelId;
        final /* synthetic */ boolean val$deleteFiles;
        final /* synthetic */ ArrayList val$messages;

        AnonymousClass76(ArrayList arrayList, int i, boolean z) {
            this.val$messages = arrayList;
            this.val$channelId = i;
            this.val$deleteFiles = z;
        }

        public void run() {
            MessagesStorage.this.markMessagesAsDeletedInternal(this.val$messages, this.val$channelId, this.val$deleteFiles);
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MessagesStorage.77 */
    class AnonymousClass77 implements Runnable {
        final /* synthetic */ boolean val$createDialog;
        final /* synthetic */ long val$dialog_id;
        final /* synthetic */ int val$load_type;
        final /* synthetic */ int val$max_id;
        final /* synthetic */ messages_Messages val$messages;

        AnonymousClass77(messages_Messages com_hanista_mobogram_tgnet_TLRPC_messages_Messages, int i, long j, int i2, boolean z) {
            this.val$messages = com_hanista_mobogram_tgnet_TLRPC_messages_Messages;
            this.val$load_type = i;
            this.val$dialog_id = j;
            this.val$max_id = i2;
            this.val$createDialog = z;
        }

        public void run() {
            try {
                if (!this.val$messages.messages.isEmpty()) {
                    MessagesStorage.this.database.beginTransaction();
                    int i;
                    if (this.val$load_type == 0) {
                        i = ((Message) this.val$messages.messages.get(this.val$messages.messages.size() - 1)).id;
                        MessagesStorage.this.closeHolesInTable("messages_holes", this.val$dialog_id, i, this.val$max_id);
                        MessagesStorage.this.closeHolesInMedia(this.val$dialog_id, i, this.val$max_id, -1);
                    } else if (this.val$load_type == 1) {
                        r7 = ((Message) this.val$messages.messages.get(0)).id;
                        MessagesStorage.this.closeHolesInTable("messages_holes", this.val$dialog_id, this.val$max_id, r7);
                        MessagesStorage.this.closeHolesInMedia(this.val$dialog_id, this.val$max_id, r7, -1);
                    } else if (this.val$load_type == 3 || this.val$load_type == 2) {
                        r7 = this.val$max_id == 0 ? ConnectionsManager.DEFAULT_DATACENTER_ID : ((Message) this.val$messages.messages.get(0)).id;
                        i = ((Message) this.val$messages.messages.get(this.val$messages.messages.size() - 1)).id;
                        MessagesStorage.this.closeHolesInTable("messages_holes", this.val$dialog_id, i, r7);
                        MessagesStorage.this.closeHolesInMedia(this.val$dialog_id, i, r7, -1);
                    }
                    int size = this.val$messages.messages.size();
                    SQLitePreparedStatement executeFast = MessagesStorage.this.database.executeFast("REPLACE INTO messages VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, NULL, ?)");
                    SQLitePreparedStatement executeFast2 = MessagesStorage.this.database.executeFast("REPLACE INTO media_v2 VALUES(?, ?, ?, ?, ?)");
                    SQLitePreparedStatement sQLitePreparedStatement = null;
                    Message message = null;
                    int i2 = 0;
                    int i3 = 0;
                    while (i3 < size) {
                        SQLitePreparedStatement sQLitePreparedStatement2;
                        Message message2 = (Message) this.val$messages.messages.get(i3);
                        long j = (long) message2.id;
                        int i4 = i2 == 0 ? message2.to_id.channel_id : i2;
                        if (message2.to_id.channel_id != 0) {
                            j |= ((long) i4) << 32;
                        }
                        if (this.val$load_type == -2) {
                            SQLiteCursor queryFinalized = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT mid, data, ttl FROM messages WHERE mid = %d", new Object[]{Long.valueOf(j)}), new Object[0]);
                            boolean next = queryFinalized.next();
                            if (next) {
                                AbstractSerializedData byteBufferValue = queryFinalized.byteBufferValue(1);
                                if (byteBufferValue != null) {
                                    Message TLdeserialize = Message.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                                    byteBufferValue.reuse();
                                    if (TLdeserialize != null) {
                                        message2.attachPath = TLdeserialize.attachPath;
                                        message2.ttl = queryFinalized.intValue(2);
                                    }
                                }
                            }
                            queryFinalized.dispose();
                            if (!next) {
                                message2 = message;
                                sQLitePreparedStatement2 = sQLitePreparedStatement;
                                i3++;
                                i2 = i4;
                                sQLitePreparedStatement = sQLitePreparedStatement2;
                                message = message2;
                            }
                        }
                        if (i3 == 0 && this.val$createDialog) {
                            SQLitePreparedStatement executeFast3 = MessagesStorage.this.database.executeFast("REPLACE INTO dialogs VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                            executeFast3.bindLong(1, this.val$dialog_id);
                            executeFast3.bindInteger(2, message2.date);
                            executeFast3.bindInteger(3, 0);
                            executeFast3.bindLong(4, j);
                            executeFast3.bindInteger(5, message2.id);
                            executeFast3.bindInteger(6, 0);
                            executeFast3.bindLong(7, j);
                            executeFast3.bindInteger(8, message2.ttl);
                            executeFast3.bindInteger(9, this.val$messages.pts);
                            executeFast3.bindInteger(10, message2.date);
                            executeFast3.step();
                            executeFast3.dispose();
                        }
                        MessagesStorage.this.fixUnsupportedMedia(message2);
                        executeFast.requery();
                        NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(message2.getObjectSize());
                        message2.serializeToStream(nativeByteBuffer);
                        executeFast.bindLong(1, j);
                        executeFast.bindLong(2, this.val$dialog_id);
                        executeFast.bindInteger(3, MessageObject.getUnreadFlags(message2));
                        executeFast.bindInteger(4, message2.send_state);
                        executeFast.bindInteger(5, message2.date);
                        executeFast.bindByteBuffer(6, nativeByteBuffer);
                        executeFast.bindInteger(7, MessageObject.isOut(message2) ? 1 : 0);
                        executeFast.bindInteger(8, 0);
                        if ((message2.flags & TLRPC.MESSAGE_FLAG_HAS_VIEWS) != 0) {
                            executeFast.bindInteger(9, message2.views);
                        } else {
                            executeFast.bindInteger(9, 0);
                        }
                        executeFast.bindInteger(10, 0);
                        executeFast.step();
                        if (SharedMediaQuery.canAddMessageToMedia(message2)) {
                            executeFast2.requery();
                            executeFast2.bindLong(1, j);
                            executeFast2.bindLong(2, this.val$dialog_id);
                            executeFast2.bindInteger(3, message2.date);
                            executeFast2.bindInteger(4, SharedMediaQuery.getMediaType(message2));
                            executeFast2.bindByteBuffer(5, nativeByteBuffer);
                            executeFast2.step();
                        }
                        nativeByteBuffer.reuse();
                        if ((message2.media instanceof TL_messageMediaWebPage) && (message2.media.webpage instanceof TL_webPagePending)) {
                            if (sQLitePreparedStatement == null) {
                                sQLitePreparedStatement = MessagesStorage.this.database.executeFast("REPLACE INTO webpage_pending VALUES(?, ?)");
                            }
                            sQLitePreparedStatement.requery();
                            sQLitePreparedStatement.bindLong(1, message2.media.webpage.id);
                            sQLitePreparedStatement.bindLong(2, j);
                            sQLitePreparedStatement.step();
                        }
                        if (this.val$load_type == 0 && MessagesStorage.this.isValidKeyboardToSave(message2) && (message == null || message.id < message2.id)) {
                            sQLitePreparedStatement2 = sQLitePreparedStatement;
                            i3++;
                            i2 = i4;
                            sQLitePreparedStatement = sQLitePreparedStatement2;
                            message = message2;
                        } else {
                            message2 = message;
                            sQLitePreparedStatement2 = sQLitePreparedStatement;
                            i3++;
                            i2 = i4;
                            sQLitePreparedStatement = sQLitePreparedStatement2;
                            message = message2;
                        }
                    }
                    executeFast.dispose();
                    executeFast2.dispose();
                    if (sQLitePreparedStatement != null) {
                        sQLitePreparedStatement.dispose();
                    }
                    if (message != null) {
                        BotQuery.putBotKeyboard(this.val$dialog_id, message);
                    }
                    MessagesStorage.this.putUsersInternal(this.val$messages.users);
                    MessagesStorage.this.putChatsInternal(this.val$messages.chats);
                    MessagesStorage.this.database.commitTransaction();
                    if (this.val$createDialog) {
                        MessagesStorage.getInstance().updateDialogsWithDeletedMessages(new ArrayList(), false, i2);
                    }
                } else if (this.val$load_type == 0) {
                    MessagesStorage.this.doneHolesInTable("messages_holes", this.val$dialog_id, this.val$max_id);
                    MessagesStorage.this.doneHolesInMedia(this.val$dialog_id, this.val$max_id, -1);
                }
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MessagesStorage.78 */
    class AnonymousClass78 implements Runnable {
        final /* synthetic */ int val$count;
        final /* synthetic */ int val$offset;

        AnonymousClass78(int i, int i2) {
            this.val$offset = i;
            this.val$count = i2;
        }

        public void run() {
            messages_Dialogs com_hanista_mobogram_tgnet_TLRPC_messages_Dialogs = new messages_Dialogs();
            ArrayList arrayList = new ArrayList();
            Iterable arrayList2 = new ArrayList();
            arrayList2.add(Integer.valueOf(UserConfig.getClientUserId()));
            Iterable arrayList3 = new ArrayList();
            Iterable arrayList4 = new ArrayList();
            Iterable arrayList5 = new ArrayList();
            HashMap hashMap = new HashMap();
            SQLiteCursor queryFinalized = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT d.did, d.last_mid, d.unread_count, d.date, m.data, m.read_state, m.mid, m.send_state, s.flags, m.date, d.pts, d.inbox_max, d.outbox_max, m.replydata FROM dialogs as d LEFT JOIN messages as m ON d.last_mid = m.mid LEFT JOIN dialog_settings as s ON d.did = s.did ORDER BY d.date DESC LIMIT %d,%d", new Object[]{Integer.valueOf(this.val$offset), Integer.valueOf(this.val$count)}), new Object[0]);
            while (queryFinalized.next()) {
                TL_dialog tL_dialog = new TL_dialog();
                tL_dialog.id = queryFinalized.longValue(0);
                tL_dialog.top_message = queryFinalized.intValue(1);
                tL_dialog.unread_count = queryFinalized.intValue(2);
                tL_dialog.last_message_date = queryFinalized.intValue(3);
                tL_dialog.pts = queryFinalized.intValue(10);
                int i = (tL_dialog.pts == 0 || ((int) tL_dialog.id) > 0) ? 0 : 1;
                tL_dialog.flags = i;
                tL_dialog.read_inbox_max_id = queryFinalized.intValue(11);
                tL_dialog.read_outbox_max_id = queryFinalized.intValue(12);
                long longValue = queryFinalized.longValue(8);
                i = (int) longValue;
                tL_dialog.notify_settings = new TL_peerNotifySettings();
                if ((i & 1) != 0) {
                    tL_dialog.notify_settings.mute_until = (int) (longValue >> 32);
                    if (tL_dialog.notify_settings.mute_until == 0) {
                        tL_dialog.notify_settings.mute_until = ConnectionsManager.DEFAULT_DATACENTER_ID;
                    }
                }
                com_hanista_mobogram_tgnet_TLRPC_messages_Dialogs.dialogs.add(tL_dialog);
                AbstractSerializedData byteBufferValue = queryFinalized.byteBufferValue(4);
                if (byteBufferValue != null) {
                    Message TLdeserialize = Message.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                    byteBufferValue.reuse();
                    if (TLdeserialize != null) {
                        MessageObject.setUnreadFlags(TLdeserialize, queryFinalized.intValue(5));
                        TLdeserialize.id = queryFinalized.intValue(6);
                        i = queryFinalized.intValue(9);
                        if (i != 0) {
                            tL_dialog.last_message_date = i;
                        }
                        TLdeserialize.send_state = queryFinalized.intValue(7);
                        TLdeserialize.dialog_id = tL_dialog.id;
                        com_hanista_mobogram_tgnet_TLRPC_messages_Dialogs.messages.add(TLdeserialize);
                        MessagesStorage.addUsersAndChatsFromMessage(TLdeserialize, arrayList2, arrayList3);
                        try {
                            if (TLdeserialize.reply_to_msg_id != 0 && ((TLdeserialize.action instanceof TL_messageActionPinMessage) || (TLdeserialize.action instanceof TL_messageActionGameScore))) {
                                if (!queryFinalized.isNull(13)) {
                                    byteBufferValue = queryFinalized.byteBufferValue(13);
                                    if (byteBufferValue != null) {
                                        TLdeserialize.replyMessage = Message.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                                        byteBufferValue.reuse();
                                        if (TLdeserialize.replyMessage != null) {
                                            MessagesStorage.addUsersAndChatsFromMessage(TLdeserialize.replyMessage, arrayList2, arrayList3);
                                        }
                                    }
                                }
                                if (TLdeserialize.replyMessage == null) {
                                    longValue = (long) TLdeserialize.reply_to_msg_id;
                                    if (TLdeserialize.to_id.channel_id != 0) {
                                        longValue |= ((long) TLdeserialize.to_id.channel_id) << 32;
                                    }
                                    if (!arrayList5.contains(Long.valueOf(longValue))) {
                                        arrayList5.add(Long.valueOf(longValue));
                                    }
                                    hashMap.put(Long.valueOf(tL_dialog.id), TLdeserialize);
                                }
                            }
                        } catch (Throwable e) {
                            try {
                                FileLog.m18e("tmessages", e);
                            } catch (Throwable e2) {
                                com_hanista_mobogram_tgnet_TLRPC_messages_Dialogs.dialogs.clear();
                                com_hanista_mobogram_tgnet_TLRPC_messages_Dialogs.users.clear();
                                com_hanista_mobogram_tgnet_TLRPC_messages_Dialogs.chats.clear();
                                arrayList.clear();
                                FileLog.m18e("tmessages", e2);
                                MessagesController.getInstance().processLoadedDialogs(com_hanista_mobogram_tgnet_TLRPC_messages_Dialogs, arrayList, 0, 100, 1, true, false);
                                return;
                            }
                        }
                    }
                }
                i = (int) tL_dialog.id;
                int i2 = (int) (tL_dialog.id >> 32);
                if (i != 0) {
                    if (i2 == 1) {
                        if (!arrayList3.contains(Integer.valueOf(i))) {
                            arrayList3.add(Integer.valueOf(i));
                        }
                    } else if (i > 0) {
                        if (!arrayList2.contains(Integer.valueOf(i))) {
                            arrayList2.add(Integer.valueOf(i));
                        }
                    } else if (!arrayList3.contains(Integer.valueOf(-i))) {
                        arrayList3.add(Integer.valueOf(-i));
                    }
                } else if (!arrayList4.contains(Integer.valueOf(i2))) {
                    arrayList4.add(Integer.valueOf(i2));
                }
            }
            queryFinalized.dispose();
            if (!arrayList5.isEmpty()) {
                SQLiteCursor queryFinalized2 = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT data, mid, date, uid FROM messages WHERE mid IN(%s)", new Object[]{TextUtils.join(",", arrayList5)}), new Object[0]);
                while (queryFinalized2.next()) {
                    byteBufferValue = queryFinalized2.byteBufferValue(0);
                    if (byteBufferValue != null) {
                        Message TLdeserialize2 = Message.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                        byteBufferValue.reuse();
                        TLdeserialize2.id = queryFinalized2.intValue(1);
                        TLdeserialize2.date = queryFinalized2.intValue(2);
                        TLdeserialize2.dialog_id = queryFinalized2.longValue(3);
                        MessagesStorage.addUsersAndChatsFromMessage(TLdeserialize2, arrayList2, arrayList3);
                        Message message = (Message) hashMap.get(Long.valueOf(TLdeserialize2.dialog_id));
                        if (message != null) {
                            message.replyMessage = TLdeserialize2;
                            TLdeserialize2.dialog_id = message.dialog_id;
                        }
                    }
                }
                queryFinalized2.dispose();
            }
            if (!arrayList4.isEmpty()) {
                MessagesStorage.this.getEncryptedChatsInternal(TextUtils.join(",", arrayList4), arrayList, arrayList2);
            }
            if (!arrayList3.isEmpty()) {
                MessagesStorage.this.getChatsInternal(TextUtils.join(",", arrayList3), com_hanista_mobogram_tgnet_TLRPC_messages_Dialogs.chats);
            }
            if (!arrayList2.isEmpty()) {
                MessagesStorage.this.getUsersInternal(TextUtils.join(",", arrayList2), com_hanista_mobogram_tgnet_TLRPC_messages_Dialogs.users);
            }
            MessagesController.getInstance().processLoadedDialogs(com_hanista_mobogram_tgnet_TLRPC_messages_Dialogs, arrayList, this.val$offset, this.val$count, 1, false, false);
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MessagesStorage.79 */
    class AnonymousClass79 implements Runnable {
        final /* synthetic */ messages_Dialogs val$dialogs;

        AnonymousClass79(messages_Dialogs com_hanista_mobogram_tgnet_TLRPC_messages_Dialogs) {
            this.val$dialogs = com_hanista_mobogram_tgnet_TLRPC_messages_Dialogs;
        }

        public void run() {
            MessagesStorage.this.putDialogsInternal(this.val$dialogs);
            MessagesStorage.this.loadUnreadMessages();
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MessagesStorage.7 */
    class C05957 implements Runnable {
        final /* synthetic */ int val$channelId;
        final /* synthetic */ int val$pts;

        C05957(int i, int i2) {
            this.val$pts = i;
            this.val$channelId = i2;
        }

        public void run() {
            try {
                SQLitePreparedStatement executeFast = MessagesStorage.this.database.executeFast("UPDATE dialogs SET pts = ? WHERE did = ?");
                executeFast.bindInteger(1, this.val$pts);
                executeFast.bindInteger(2, -this.val$channelId);
                executeFast.step();
                executeFast.dispose();
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MessagesStorage.80 */
    class AnonymousClass80 implements Runnable {
        final /* synthetic */ long val$dialog_id;
        final /* synthetic */ Integer[] val$max;
        final /* synthetic */ boolean val$outbox;
        final /* synthetic */ Semaphore val$semaphore;

        AnonymousClass80(boolean z, long j, Integer[] numArr, Semaphore semaphore) {
            this.val$outbox = z;
            this.val$dialog_id = j;
            this.val$max = numArr;
            this.val$semaphore = semaphore;
        }

        public void run() {
            SQLiteCursor sQLiteCursor = null;
            try {
                sQLiteCursor = this.val$outbox ? MessagesStorage.this.database.queryFinalized("SELECT outbox_max FROM dialogs WHERE did = " + this.val$dialog_id, new Object[0]) : MessagesStorage.this.database.queryFinalized("SELECT inbox_max FROM dialogs WHERE did = " + this.val$dialog_id, new Object[0]);
                if (sQLiteCursor.next()) {
                    this.val$max[0] = Integer.valueOf(sQLiteCursor.intValue(0));
                }
                if (sQLiteCursor != null) {
                    sQLiteCursor.dispose();
                }
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
                if (sQLiteCursor != null) {
                    sQLiteCursor.dispose();
                }
            } catch (Throwable th) {
                if (sQLiteCursor != null) {
                    sQLiteCursor.dispose();
                }
            }
            this.val$semaphore.release();
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MessagesStorage.81 */
    class AnonymousClass81 implements Runnable {
        final /* synthetic */ int val$channelId;
        final /* synthetic */ Integer[] val$pts;
        final /* synthetic */ Semaphore val$semaphore;

        AnonymousClass81(int i, Integer[] numArr, Semaphore semaphore) {
            this.val$channelId = i;
            this.val$pts = numArr;
            this.val$semaphore = semaphore;
        }

        public void run() {
            SQLiteCursor sQLiteCursor = null;
            try {
                sQLiteCursor = MessagesStorage.this.database.queryFinalized("SELECT pts FROM dialogs WHERE did = " + (-this.val$channelId), new Object[0]);
                if (sQLiteCursor.next()) {
                    this.val$pts[0] = Integer.valueOf(sQLiteCursor.intValue(0));
                }
                if (sQLiteCursor != null) {
                    sQLiteCursor.dispose();
                }
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
                if (sQLiteCursor != null) {
                    sQLiteCursor.dispose();
                }
            } catch (Throwable th) {
                if (sQLiteCursor != null) {
                    sQLiteCursor.dispose();
                }
            }
            try {
                if (this.val$semaphore != null) {
                    this.val$semaphore.release();
                }
            } catch (Throwable e2) {
                FileLog.m18e("tmessages", e2);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MessagesStorage.82 */
    class AnonymousClass82 implements Runnable {
        final /* synthetic */ Semaphore val$semaphore;
        final /* synthetic */ User[] val$user;
        final /* synthetic */ int val$user_id;

        AnonymousClass82(User[] userArr, int i, Semaphore semaphore) {
            this.val$user = userArr;
            this.val$user_id = i;
            this.val$semaphore = semaphore;
        }

        public void run() {
            this.val$user[0] = MessagesStorage.this.getUser(this.val$user_id);
            this.val$semaphore.release();
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MessagesStorage.83 */
    class AnonymousClass83 implements Runnable {
        final /* synthetic */ Chat[] val$chat;
        final /* synthetic */ Semaphore val$semaphore;
        final /* synthetic */ int val$user_id;

        AnonymousClass83(Chat[] chatArr, int i, Semaphore semaphore) {
            this.val$chat = chatArr;
            this.val$user_id = i;
            this.val$semaphore = semaphore;
        }

        public void run() {
            this.val$chat[0] = MessagesStorage.this.getChat(this.val$user_id);
            this.val$semaphore.release();
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MessagesStorage.8 */
    class C05968 implements Runnable {
        final /* synthetic */ int val$date;
        final /* synthetic */ int val$pts;
        final /* synthetic */ int val$qts;
        final /* synthetic */ int val$seq;

        C05968(int i, int i2, int i3, int i4) {
            this.val$seq = i;
            this.val$pts = i2;
            this.val$date = i3;
            this.val$qts = i4;
        }

        public void run() {
            try {
                if (MessagesStorage.this.lastSavedSeq != this.val$seq || MessagesStorage.this.lastSavedPts != this.val$pts || MessagesStorage.this.lastSavedDate != this.val$date || MessagesStorage.lastQtsValue != this.val$qts) {
                    SQLitePreparedStatement executeFast = MessagesStorage.this.database.executeFast("UPDATE params SET seq = ?, pts = ?, date = ?, qts = ? WHERE id = 1");
                    executeFast.bindInteger(1, this.val$seq);
                    executeFast.bindInteger(2, this.val$pts);
                    executeFast.bindInteger(3, this.val$date);
                    executeFast.bindInteger(4, this.val$qts);
                    executeFast.step();
                    executeFast.dispose();
                    MessagesStorage.this.lastSavedSeq = this.val$seq;
                    MessagesStorage.this.lastSavedPts = this.val$pts;
                    MessagesStorage.this.lastSavedDate = this.val$date;
                    MessagesStorage.this.lastSavedQts = this.val$qts;
                }
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MessagesStorage.9 */
    class C05979 implements Runnable {
        final /* synthetic */ long val$did;
        final /* synthetic */ long val$flags;

        C05979(long j, long j2) {
            this.val$did = j;
            this.val$flags = j2;
        }

        public void run() {
            try {
                MessagesStorage.this.database.executeFast(String.format(Locale.US, "REPLACE INTO dialog_settings VALUES(%d, %d)", new Object[]{Long.valueOf(this.val$did), Long.valueOf(this.val$flags)})).stepThis().dispose();
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
        }
    }

    private class Hole {
        public int end;
        public int start;
        public int type;

        public Hole(int i, int i2) {
            this.start = i;
            this.end = i2;
        }

        public Hole(int i, int i2, int i3) {
            this.type = i;
            this.start = i2;
            this.end = i3;
        }
    }

    static {
        lastDateValue = 0;
        lastPtsValue = 0;
        lastQtsValue = 0;
        lastSeqValue = 0;
        lastSecretVersion = 0;
        secretPBytes = null;
        secretG = 0;
        Instance = null;
    }

    public MessagesStorage() {
        this.storageQueue = new DispatchQueue("storageQueue");
        this.lastTaskId = new AtomicLong(System.currentTimeMillis());
        this.lastSavedSeq = 0;
        this.lastSavedPts = 0;
        this.lastSavedDate = 0;
        this.lastSavedQts = 0;
        this.storageQueue.setPriority(10);
        openDatabase();
    }

    public static void addUsersAndChatsFromMessage(Message message, ArrayList<Integer> arrayList, ArrayList<Integer> arrayList2) {
        int i = 0;
        if (message.from_id != 0) {
            if (message.from_id > 0) {
                if (!arrayList.contains(Integer.valueOf(message.from_id))) {
                    arrayList.add(Integer.valueOf(message.from_id));
                }
            } else if (!arrayList2.contains(Integer.valueOf(-message.from_id))) {
                arrayList2.add(Integer.valueOf(-message.from_id));
            }
        }
        if (!(message.via_bot_id == 0 || arrayList.contains(Integer.valueOf(message.via_bot_id)))) {
            arrayList.add(Integer.valueOf(message.via_bot_id));
        }
        if (message.action != null) {
            if (!(message.action.user_id == 0 || arrayList.contains(Integer.valueOf(message.action.user_id)))) {
                arrayList.add(Integer.valueOf(message.action.user_id));
            }
            if (!(message.action.channel_id == 0 || arrayList2.contains(Integer.valueOf(message.action.channel_id)))) {
                arrayList2.add(Integer.valueOf(message.action.channel_id));
            }
            if (!(message.action.chat_id == 0 || arrayList2.contains(Integer.valueOf(message.action.chat_id)))) {
                arrayList2.add(Integer.valueOf(message.action.chat_id));
            }
            if (!message.action.users.isEmpty()) {
                for (int i2 = 0; i2 < message.action.users.size(); i2++) {
                    Integer num = (Integer) message.action.users.get(i2);
                    if (!arrayList.contains(num)) {
                        arrayList.add(num);
                    }
                }
            }
        }
        if (!message.entities.isEmpty()) {
            while (i < message.entities.size()) {
                MessageEntity messageEntity = (MessageEntity) message.entities.get(i);
                if (messageEntity instanceof TL_messageEntityMentionName) {
                    arrayList.add(Integer.valueOf(((TL_messageEntityMentionName) messageEntity).user_id));
                } else if (messageEntity instanceof TL_inputMessageEntityMentionName) {
                    arrayList.add(Integer.valueOf(((TL_inputMessageEntityMentionName) messageEntity).user_id.user_id));
                }
                i++;
            }
        }
        if (!(message.media == null || message.media.user_id == 0 || arrayList.contains(Integer.valueOf(message.media.user_id)))) {
            arrayList.add(Integer.valueOf(message.media.user_id));
        }
        if (message.fwd_from != null) {
            if (!(message.fwd_from.from_id == 0 || arrayList.contains(Integer.valueOf(message.fwd_from.from_id)))) {
                arrayList.add(Integer.valueOf(message.fwd_from.from_id));
            }
            if (!(message.fwd_from.channel_id == 0 || arrayList2.contains(Integer.valueOf(message.fwd_from.channel_id)))) {
                arrayList2.add(Integer.valueOf(message.fwd_from.channel_id));
            }
        }
        if (message.ttl < 0 && !arrayList2.contains(Integer.valueOf(-message.ttl))) {
            arrayList2.add(Integer.valueOf(-message.ttl));
        }
    }

    private void closeHolesInTable(String str, long j, int i, int i2) {
        SQLiteCursor queryFinalized = this.database.queryFinalized(String.format(Locale.US, "SELECT start, end FROM " + str + " WHERE uid = %d AND ((end >= %d AND end <= %d) OR (start >= %d AND start <= %d) OR (start >= %d AND end <= %d) OR (start <= %d AND end >= %d))", new Object[]{Long.valueOf(j), Integer.valueOf(i), Integer.valueOf(i2), Integer.valueOf(i), Integer.valueOf(i2), Integer.valueOf(i), Integer.valueOf(i2), Integer.valueOf(i), Integer.valueOf(i2)}), new Object[0]);
        ArrayList arrayList = null;
        while (queryFinalized.next()) {
            ArrayList arrayList2 = arrayList == null ? new ArrayList() : arrayList;
            int intValue = queryFinalized.intValue(0);
            int intValue2 = queryFinalized.intValue(1);
            if (intValue == intValue2 && intValue == 1) {
                arrayList = arrayList2;
            } else {
                arrayList2.add(new Hole(intValue, intValue2));
                arrayList = arrayList2;
            }
        }
        queryFinalized.dispose();
        if (arrayList != null) {
            for (int i3 = 0; i3 < arrayList.size(); i3++) {
                Hole hole = (Hole) arrayList.get(i3);
                if (i2 >= hole.end - 1 && i <= hole.start + 1) {
                    this.database.executeFast(String.format(Locale.US, "DELETE FROM " + str + " WHERE uid = %d AND start = %d AND end = %d", new Object[]{Long.valueOf(j), Integer.valueOf(hole.start), Integer.valueOf(hole.end)})).stepThis().dispose();
                } else if (i2 < hole.end - 1) {
                    try {
                        if (i > hole.start + 1) {
                            this.database.executeFast(String.format(Locale.US, "DELETE FROM " + str + " WHERE uid = %d AND start = %d AND end = %d", new Object[]{Long.valueOf(j), Integer.valueOf(hole.start), Integer.valueOf(hole.end)})).stepThis().dispose();
                            SQLitePreparedStatement executeFast = this.database.executeFast("REPLACE INTO " + str + " VALUES(?, ?, ?)");
                            executeFast.requery();
                            executeFast.bindLong(1, j);
                            executeFast.bindInteger(2, hole.start);
                            executeFast.bindInteger(3, i);
                            executeFast.step();
                            executeFast.requery();
                            executeFast.bindLong(1, j);
                            executeFast.bindInteger(2, i2);
                            executeFast.bindInteger(3, hole.end);
                            executeFast.step();
                            executeFast.dispose();
                        } else if (hole.start != i2) {
                            try {
                                this.database.executeFast(String.format(Locale.US, "UPDATE " + str + " SET start = %d WHERE uid = %d AND start = %d AND end = %d", new Object[]{Integer.valueOf(i2), Long.valueOf(j), Integer.valueOf(hole.start), Integer.valueOf(hole.end)})).stepThis().dispose();
                            } catch (Throwable e) {
                                FileLog.m18e("tmessages", e);
                            }
                        } else {
                            continue;
                        }
                    } catch (Throwable e2) {
                        FileLog.m18e("tmessages", e2);
                        return;
                    }
                } else if (hole.end != i) {
                    try {
                        this.database.executeFast(String.format(Locale.US, "UPDATE " + str + " SET end = %d WHERE uid = %d AND start = %d AND end = %d", new Object[]{Integer.valueOf(i), Long.valueOf(j), Integer.valueOf(hole.start), Integer.valueOf(hole.end)})).stepThis().dispose();
                    } catch (Throwable e22) {
                        FileLog.m18e("tmessages", e22);
                    }
                } else {
                    continue;
                }
            }
        }
    }

    public static void createFirstHoles(long j, SQLitePreparedStatement sQLitePreparedStatement, SQLitePreparedStatement sQLitePreparedStatement2, int i) {
        sQLitePreparedStatement.requery();
        sQLitePreparedStatement.bindLong(1, j);
        sQLitePreparedStatement.bindInteger(2, i == 1 ? 1 : 0);
        sQLitePreparedStatement.bindInteger(3, i);
        sQLitePreparedStatement.step();
        for (int i2 = 0; i2 < 5; i2++) {
            sQLitePreparedStatement2.requery();
            sQLitePreparedStatement2.bindLong(1, j);
            sQLitePreparedStatement2.bindInteger(2, i2);
            sQLitePreparedStatement2.bindInteger(3, i == 1 ? 1 : 0);
            sQLitePreparedStatement2.bindInteger(4, i);
            sQLitePreparedStatement2.step();
        }
    }

    private void deleteFiles(String str) {
        try {
            SQLiteCursor queryFinalized = this.database.queryFinalized(String.format(Locale.US, "SELECT uid, data, read_state FROM messages WHERE mid IN(%s)", new Object[]{str}), new Object[0]);
            ArrayList arrayList = new ArrayList();
            while (queryFinalized.next()) {
                AbstractSerializedData byteBufferValue = queryFinalized.byteBufferValue(1);
                if (byteBufferValue != null) {
                    Message TLdeserialize = Message.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                    byteBufferValue.reuse();
                    if (TLdeserialize == null) {
                        continue;
                    } else if (TLdeserialize.media instanceof TL_messageMediaPhoto) {
                        Iterator it = TLdeserialize.media.photo.sizes.iterator();
                        while (it.hasNext()) {
                            r0 = FileLoader.getPathToAttach((PhotoSize) it.next());
                            if (r0 != null && r0.toString().length() > 0) {
                                arrayList.add(r0);
                            }
                        }
                    } else {
                        try {
                            if (TLdeserialize.media instanceof TL_messageMediaDocument) {
                                r0 = FileLoader.getPathToAttach(TLdeserialize.media.document);
                                if (r0 != null && r0.toString().length() > 0) {
                                    arrayList.add(r0);
                                }
                                r0 = FileLoader.getPathToAttach(TLdeserialize.media.document.thumb);
                                if (r0 != null && r0.toString().length() > 0) {
                                    arrayList.add(r0);
                                }
                            }
                        } catch (Throwable e) {
                            FileLog.m18e("tmessages", e);
                        }
                    }
                }
            }
            queryFinalized.dispose();
            FileLoader.getInstance().deleteFiles(arrayList, 0);
        } catch (Throwable e2) {
            FileLog.m18e("tmessages", e2);
        }
    }

    private void doneHolesInTable(String str, long j, int i) {
        if (i == 0) {
            this.database.executeFast(String.format(Locale.US, "DELETE FROM " + str + " WHERE uid = %d", new Object[]{Long.valueOf(j)})).stepThis().dispose();
        } else {
            this.database.executeFast(String.format(Locale.US, "DELETE FROM " + str + " WHERE uid = %d AND start = 0", new Object[]{Long.valueOf(j)})).stepThis().dispose();
        }
        SQLitePreparedStatement executeFast = this.database.executeFast("REPLACE INTO " + str + " VALUES(?, ?, ?)");
        executeFast.requery();
        executeFast.bindLong(1, j);
        executeFast.bindInteger(2, 1);
        executeFast.bindInteger(3, 1);
        executeFast.step();
        executeFast.dispose();
    }

    private void fixUnsupportedMedia(Message message) {
        if (message != null) {
            if (message.media instanceof TL_messageMediaUnsupported_old) {
                if (message.media.bytes.length == 0) {
                    message.media.bytes = new byte[1];
                    message.media.bytes[0] = (byte) 57;
                }
            } else if (message.media instanceof TL_messageMediaUnsupported) {
                message.media = new TL_messageMediaUnsupported_old();
                message.media.bytes = new byte[1];
                message.media.bytes[0] = (byte) 57;
                message.flags |= TLRPC.USER_FLAG_UNUSED3;
            }
        }
    }

    private String formatUserSearchName(User user) {
        StringBuilder stringBuilder = new StringBuilder(TtmlNode.ANONYMOUS_REGION_ID);
        if (user.first_name != null && user.first_name.length() > 0) {
            stringBuilder.append(user.first_name);
        }
        if (user.last_name != null && user.last_name.length() > 0) {
            if (stringBuilder.length() > 0) {
                stringBuilder.append(" ");
            }
            stringBuilder.append(user.last_name);
        }
        stringBuilder.append(";;;");
        if (user.username != null && user.username.length() > 0) {
            stringBuilder.append(user.username);
        }
        return stringBuilder.toString().toLowerCase();
    }

    public static MessagesStorage getInstance() {
        MessagesStorage messagesStorage = Instance;
        if (messagesStorage == null) {
            synchronized (MessagesStorage.class) {
                messagesStorage = Instance;
                if (messagesStorage == null) {
                    messagesStorage = new MessagesStorage();
                    Instance = messagesStorage;
                }
            }
        }
        return messagesStorage;
    }

    private int getMessageMediaType(Message message) {
        return ((message instanceof TL_message_secret) && (((message.media instanceof TL_messageMediaPhoto) && message.ttl > 0 && message.ttl <= 60) || MessageObject.isVoiceMessage(message) || MessageObject.isVideoMessage(message))) ? 1 : ((message.media instanceof TL_messageMediaPhoto) || MessageObject.isVideoMessage(message)) ? 0 : -1;
    }

    private boolean isValidKeyboardToSave(Message message) {
        return (message.reply_markup == null || (message.reply_markup instanceof TL_replyInlineMarkup) || (message.reply_markup.selective && !message.mentioned)) ? false : true;
    }

    private void loadPendingTasks() {
        this.storageQueue.postRunnable(new C05936());
    }

    private void markMessagesAsDeletedInternal(ArrayList<Integer> arrayList, int i) {
        markMessagesAsDeletedInternal(arrayList, i, false);
    }

    private void markMessagesAsDeletedInternal(ArrayList<Integer> arrayList, int i, boolean z) {
        String stringBuilder;
        int i2 = 0;
        if (i != 0) {
            try {
                StringBuilder stringBuilder2 = new StringBuilder(arrayList.size());
                for (int i3 = 0; i3 < arrayList.size(); i3++) {
                    long intValue = ((long) ((Integer) arrayList.get(i3)).intValue()) | (((long) i) << 32);
                    if (stringBuilder2.length() > 0) {
                        stringBuilder2.append(',');
                    }
                    stringBuilder2.append(intValue);
                }
                stringBuilder = stringBuilder2.toString();
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
                return;
            }
        }
        stringBuilder = TextUtils.join(",", arrayList);
        SQLiteCursor queryFinalized = this.database.queryFinalized(String.format(Locale.US, "SELECT uid, data, read_state FROM messages WHERE mid IN(%s)", new Object[]{stringBuilder}), new Object[0]);
        ArrayList arrayList2 = new ArrayList();
        while (queryFinalized.next()) {
            long longValue = queryFinalized.longValue(0);
            if (i != 0 && queryFinalized.intValue(2) == 0) {
                i2++;
            }
            if (((int) longValue) == 0) {
                AbstractSerializedData byteBufferValue = queryFinalized.byteBufferValue(1);
                if (byteBufferValue != null) {
                    Message TLdeserialize = Message.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                    byteBufferValue.reuse();
                    if (TLdeserialize == null) {
                        continue;
                    } else if (TLdeserialize.media instanceof TL_messageMediaPhoto) {
                        Iterator it = TLdeserialize.media.photo.sizes.iterator();
                        while (it.hasNext()) {
                            r0 = FileLoader.getPathToAttach((PhotoSize) it.next());
                            if (r0 != null && r0.toString().length() > 0) {
                                arrayList2.add(r0);
                            }
                        }
                    } else {
                        try {
                            if (TLdeserialize.media instanceof TL_messageMediaDocument) {
                                r0 = FileLoader.getPathToAttach(TLdeserialize.media.document);
                                if (r0 != null && r0.toString().length() > 0) {
                                    arrayList2.add(r0);
                                }
                                r0 = FileLoader.getPathToAttach(TLdeserialize.media.document.thumb);
                                if (r0 != null && r0.toString().length() > 0) {
                                    arrayList2.add(r0);
                                }
                            }
                        } catch (Throwable e2) {
                            FileLog.m18e("tmessages", e2);
                        }
                    }
                } else {
                    continue;
                }
            }
        }
        queryFinalized.dispose();
        FileLoader.getInstance().deleteFiles(arrayList2, 0);
        if (z) {
            deleteFiles(stringBuilder);
        }
        if (!(i == 0 || i2 == 0)) {
            intValue = (long) (-i);
            SQLitePreparedStatement executeFast = this.database.executeFast("UPDATE dialogs SET unread_count = ((SELECT unread_count FROM dialogs WHERE did = ?) - ?) WHERE did = ?");
            executeFast.requery();
            executeFast.bindLong(1, intValue);
            executeFast.bindInteger(2, i2);
            executeFast.bindLong(3, intValue);
            executeFast.step();
            executeFast.dispose();
        }
        this.database.executeFast(String.format(Locale.US, "DELETE FROM messages WHERE mid IN(%s)", new Object[]{stringBuilder})).stepThis().dispose();
        this.database.executeFast(String.format(Locale.US, "DELETE FROM bot_keyboard WHERE mid IN(%s)", new Object[]{stringBuilder})).stepThis().dispose();
        this.database.executeFast(String.format(Locale.US, "DELETE FROM messages_seq WHERE mid IN(%s)", new Object[]{stringBuilder})).stepThis().dispose();
        this.database.executeFast(String.format(Locale.US, "DELETE FROM media_v2 WHERE mid IN(%s)", new Object[]{stringBuilder})).stepThis().dispose();
        this.database.executeFast("DELETE FROM media_counts_v2 WHERE 1").stepThis().dispose();
        BotQuery.clearBotKeyboard(0, arrayList);
    }

    private void markMessagesAsReadInternal(SparseArray<Long> sparseArray, SparseArray<Long> sparseArray2, HashMap<Integer, Integer> hashMap) {
        int i;
        int i2 = 0;
        if (sparseArray != null) {
            i = 0;
            while (i < sparseArray.size()) {
                try {
                    long longValue = ((Long) sparseArray.get(sparseArray.keyAt(i))).longValue();
                    this.database.executeFast(String.format(Locale.US, "UPDATE messages SET read_state = read_state | 1 WHERE uid = %d AND mid > 0 AND mid <= %d AND read_state IN(0,2) AND out = 0", new Object[]{Integer.valueOf(r3), Long.valueOf(longValue)})).stepThis().dispose();
                    i++;
                } catch (Throwable e) {
                    FileLog.m18e("tmessages", e);
                    return;
                }
            }
        }
        if (sparseArray2 != null) {
            while (i2 < sparseArray2.size()) {
                longValue = ((Long) sparseArray2.get(sparseArray2.keyAt(i2))).longValue();
                this.database.executeFast(String.format(Locale.US, "UPDATE messages SET read_state = read_state | 1 WHERE uid = %d AND mid > 0 AND mid <= %d AND read_state IN(0,2) AND out = 1", new Object[]{Integer.valueOf(i), Long.valueOf(longValue)})).stepThis().dispose();
                i2++;
            }
        }
        if (hashMap != null && !hashMap.isEmpty()) {
            for (Entry entry : hashMap.entrySet()) {
                longValue = ((long) ((Integer) entry.getKey()).intValue()) << 32;
                int intValue = ((Integer) entry.getValue()).intValue();
                SQLitePreparedStatement executeFast = this.database.executeFast("UPDATE messages SET read_state = read_state | 1 WHERE uid = ? AND date <= ? AND read_state IN(0,2) AND out = 1");
                executeFast.requery();
                executeFast.bindLong(1, longValue);
                executeFast.bindInteger(2, intValue);
                executeFast.step();
                executeFast.dispose();
            }
        }
    }

    private void putChatsInternal(ArrayList<Chat> arrayList) {
        if (arrayList != null && !arrayList.isEmpty()) {
            SQLitePreparedStatement executeFast = this.database.executeFast("REPLACE INTO chats VALUES(?, ?, ?)");
            for (int i = 0; i < arrayList.size(); i++) {
                Chat chat = (Chat) arrayList.get(i);
                if (chat.min) {
                    SQLiteCursor queryFinalized = this.database.queryFinalized(String.format(Locale.US, "SELECT data FROM chats WHERE uid = %d", new Object[]{Integer.valueOf(chat.id)}), new Object[0]);
                    if (queryFinalized.next()) {
                        try {
                            AbstractSerializedData byteBufferValue = queryFinalized.byteBufferValue(0);
                            if (byteBufferValue != null) {
                                Chat TLdeserialize = Chat.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                                byteBufferValue.reuse();
                                if (TLdeserialize != null) {
                                    TLdeserialize.title = chat.title;
                                    TLdeserialize.photo = chat.photo;
                                    TLdeserialize.broadcast = chat.broadcast;
                                    TLdeserialize.verified = chat.verified;
                                    TLdeserialize.megagroup = chat.megagroup;
                                    TLdeserialize.democracy = chat.democracy;
                                    if (chat.username != null) {
                                        TLdeserialize.username = chat.username;
                                        TLdeserialize.flags |= 64;
                                    } else {
                                        TLdeserialize.username = null;
                                        TLdeserialize.flags &= -65;
                                    }
                                    chat = TLdeserialize;
                                }
                            }
                        } catch (Throwable e) {
                            FileLog.m18e("tmessages", e);
                        }
                    }
                    queryFinalized.dispose();
                }
                executeFast.requery();
                NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(chat.getObjectSize());
                chat.serializeToStream(nativeByteBuffer);
                executeFast.bindInteger(1, chat.id);
                if (chat.title != null) {
                    executeFast.bindString(2, chat.title.toLowerCase());
                } else {
                    executeFast.bindString(2, TtmlNode.ANONYMOUS_REGION_ID);
                }
                executeFast.bindByteBuffer(3, nativeByteBuffer);
                executeFast.step();
                nativeByteBuffer.reuse();
            }
            executeFast.dispose();
        }
    }

    private void putDialogsInternal(messages_Dialogs com_hanista_mobogram_tgnet_TLRPC_messages_Dialogs) {
        try {
            int i;
            this.database.beginTransaction();
            HashMap hashMap = new HashMap();
            for (i = 0; i < com_hanista_mobogram_tgnet_TLRPC_messages_Dialogs.messages.size(); i++) {
                Message message = (Message) com_hanista_mobogram_tgnet_TLRPC_messages_Dialogs.messages.get(i);
                hashMap.put(Long.valueOf(message.dialog_id), message);
            }
            if (!com_hanista_mobogram_tgnet_TLRPC_messages_Dialogs.dialogs.isEmpty()) {
                SQLitePreparedStatement executeFast = this.database.executeFast("REPLACE INTO messages VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, NULL, ?)");
                SQLitePreparedStatement executeFast2 = this.database.executeFast("REPLACE INTO dialogs VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                SQLitePreparedStatement executeFast3 = this.database.executeFast("REPLACE INTO media_v2 VALUES(?, ?, ?, ?, ?)");
                SQLitePreparedStatement executeFast4 = this.database.executeFast("REPLACE INTO dialog_settings VALUES(?, ?)");
                SQLitePreparedStatement executeFast5 = this.database.executeFast("REPLACE INTO messages_holes VALUES(?, ?, ?)");
                SQLitePreparedStatement executeFast6 = this.database.executeFast("REPLACE INTO media_holes_v2 VALUES(?, ?, ?, ?)");
                for (int i2 = 0; i2 < com_hanista_mobogram_tgnet_TLRPC_messages_Dialogs.dialogs.size(); i2++) {
                    TL_dialog tL_dialog = (TL_dialog) com_hanista_mobogram_tgnet_TLRPC_messages_Dialogs.dialogs.get(i2);
                    if (tL_dialog.id == 0) {
                        if (tL_dialog.peer.user_id != 0) {
                            tL_dialog.id = (long) tL_dialog.peer.user_id;
                        } else if (tL_dialog.peer.chat_id != 0) {
                            tL_dialog.id = (long) (-tL_dialog.peer.chat_id);
                        } else {
                            tL_dialog.id = (long) (-tL_dialog.peer.channel_id);
                        }
                    }
                    int i3 = 0;
                    Message message2 = (Message) hashMap.get(Long.valueOf(tL_dialog.id));
                    if (message2 != null) {
                        i3 = Math.max(message2.date, 0);
                        if (isValidKeyboardToSave(message2)) {
                            BotQuery.putBotKeyboard(tL_dialog.id, message2);
                        }
                        fixUnsupportedMedia(message2);
                        AbstractSerializedData nativeByteBuffer = new NativeByteBuffer(message2.getObjectSize());
                        message2.serializeToStream(nativeByteBuffer);
                        long j = (long) message2.id;
                        if (message2.to_id.channel_id != 0) {
                            j |= ((long) message2.to_id.channel_id) << 32;
                        }
                        executeFast.requery();
                        executeFast.bindLong(1, j);
                        executeFast.bindLong(2, tL_dialog.id);
                        executeFast.bindInteger(3, MessageObject.getUnreadFlags(message2));
                        executeFast.bindInteger(4, message2.send_state);
                        executeFast.bindInteger(5, message2.date);
                        executeFast.bindByteBuffer(6, (NativeByteBuffer) nativeByteBuffer);
                        executeFast.bindInteger(7, MessageObject.isOut(message2) ? 1 : 0);
                        executeFast.bindInteger(8, 0);
                        executeFast.bindInteger(9, (message2.flags & TLRPC.MESSAGE_FLAG_HAS_VIEWS) != 0 ? message2.views : 0);
                        executeFast.bindInteger(10, 0);
                        executeFast.step();
                        if (SharedMediaQuery.canAddMessageToMedia(message2)) {
                            executeFast3.requery();
                            executeFast3.bindLong(1, j);
                            executeFast3.bindLong(2, tL_dialog.id);
                            executeFast3.bindInteger(3, message2.date);
                            executeFast3.bindInteger(4, SharedMediaQuery.getMediaType(message2));
                            executeFast3.bindByteBuffer(5, (NativeByteBuffer) nativeByteBuffer);
                            executeFast3.step();
                        }
                        nativeByteBuffer.reuse();
                        createFirstHoles(tL_dialog.id, executeFast5, executeFast6, message2.id);
                    }
                    i = i3;
                    long j2 = (long) tL_dialog.top_message;
                    if (tL_dialog.peer.channel_id != 0) {
                        j2 |= ((long) tL_dialog.peer.channel_id) << 32;
                    }
                    executeFast2.requery();
                    executeFast2.bindLong(1, tL_dialog.id);
                    executeFast2.bindInteger(2, i);
                    executeFast2.bindInteger(3, tL_dialog.unread_count);
                    executeFast2.bindLong(4, j2);
                    executeFast2.bindInteger(5, tL_dialog.read_inbox_max_id);
                    executeFast2.bindInteger(6, tL_dialog.read_outbox_max_id);
                    executeFast2.bindLong(7, 0);
                    executeFast2.bindInteger(8, 0);
                    executeFast2.bindInteger(9, tL_dialog.pts);
                    executeFast2.bindInteger(10, 0);
                    executeFast2.step();
                    if (tL_dialog.notify_settings != null) {
                        executeFast4.requery();
                        executeFast4.bindLong(1, tL_dialog.id);
                        executeFast4.bindInteger(2, tL_dialog.notify_settings.mute_until != 0 ? 1 : 0);
                        executeFast4.step();
                    }
                }
                executeFast.dispose();
                executeFast2.dispose();
                executeFast3.dispose();
                executeFast4.dispose();
                executeFast5.dispose();
                executeFast6.dispose();
            }
            putUsersInternal(com_hanista_mobogram_tgnet_TLRPC_messages_Dialogs.users);
            putChatsInternal(com_hanista_mobogram_tgnet_TLRPC_messages_Dialogs.chats);
            this.database.commitTransaction();
        } catch (Throwable e) {
            FileLog.m18e("tmessages", e);
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void putMessagesInternal(java.util.ArrayList<com.hanista.mobogram.tgnet.TLRPC.Message> r32, boolean r33, boolean r34, int r35, boolean r36) {
        /*
        r31 = this;
        if (r36 == 0) goto L_0x0073;
    L_0x0002:
        r4 = 0;
        r0 = r32;
        r4 = r0.get(r4);	 Catch:{ Exception -> 0x0062 }
        r4 = (com.hanista.mobogram.tgnet.TLRPC.Message) r4;	 Catch:{ Exception -> 0x0062 }
        r6 = r4.dialog_id;	 Catch:{ Exception -> 0x0062 }
        r8 = 0;
        r5 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1));
        if (r5 != 0) goto L_0x0020;
    L_0x0013:
        r5 = r4.to_id;	 Catch:{ Exception -> 0x0062 }
        r5 = r5.user_id;	 Catch:{ Exception -> 0x0062 }
        if (r5 == 0) goto L_0x0053;
    L_0x0019:
        r5 = r4.to_id;	 Catch:{ Exception -> 0x0062 }
        r5 = r5.user_id;	 Catch:{ Exception -> 0x0062 }
        r6 = (long) r5;	 Catch:{ Exception -> 0x0062 }
        r4.dialog_id = r6;	 Catch:{ Exception -> 0x0062 }
    L_0x0020:
        r5 = -1;
        r0 = r31;
        r6 = r0.database;	 Catch:{ Exception -> 0x0062 }
        r7 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0062 }
        r7.<init>();	 Catch:{ Exception -> 0x0062 }
        r8 = "SELECT last_mid FROM dialogs WHERE did = ";
        r7 = r7.append(r8);	 Catch:{ Exception -> 0x0062 }
        r8 = r4.dialog_id;	 Catch:{ Exception -> 0x0062 }
        r4 = r7.append(r8);	 Catch:{ Exception -> 0x0062 }
        r4 = r4.toString();	 Catch:{ Exception -> 0x0062 }
        r7 = 0;
        r7 = new java.lang.Object[r7];	 Catch:{ Exception -> 0x0062 }
        r6 = r6.queryFinalized(r4, r7);	 Catch:{ Exception -> 0x0062 }
        r4 = r6.next();	 Catch:{ Exception -> 0x0062 }
        if (r4 == 0) goto L_0x08ef;
    L_0x0048:
        r4 = 0;
        r4 = r6.intValue(r4);	 Catch:{ Exception -> 0x0062 }
    L_0x004d:
        r6.dispose();	 Catch:{ Exception -> 0x0062 }
        if (r4 == 0) goto L_0x0073;
    L_0x0052:
        return;
    L_0x0053:
        r5 = r4.to_id;	 Catch:{ Exception -> 0x0062 }
        r5 = r5.chat_id;	 Catch:{ Exception -> 0x0062 }
        if (r5 == 0) goto L_0x006a;
    L_0x0059:
        r5 = r4.to_id;	 Catch:{ Exception -> 0x0062 }
        r5 = r5.chat_id;	 Catch:{ Exception -> 0x0062 }
        r5 = -r5;
        r6 = (long) r5;	 Catch:{ Exception -> 0x0062 }
        r4.dialog_id = r6;	 Catch:{ Exception -> 0x0062 }
        goto L_0x0020;
    L_0x0062:
        r4 = move-exception;
        r5 = "tmessages";
        com.hanista.mobogram.messenger.FileLog.m18e(r5, r4);
        goto L_0x0052;
    L_0x006a:
        r5 = r4.to_id;	 Catch:{ Exception -> 0x0062 }
        r5 = r5.channel_id;	 Catch:{ Exception -> 0x0062 }
        r5 = -r5;
        r6 = (long) r5;	 Catch:{ Exception -> 0x0062 }
        r4.dialog_id = r6;	 Catch:{ Exception -> 0x0062 }
        goto L_0x0020;
    L_0x0073:
        if (r33 == 0) goto L_0x007c;
    L_0x0075:
        r0 = r31;
        r4 = r0.database;	 Catch:{ Exception -> 0x0062 }
        r4.beginTransaction();	 Catch:{ Exception -> 0x0062 }
    L_0x007c:
        r18 = new java.util.HashMap;	 Catch:{ Exception -> 0x0062 }
        r18.<init>();	 Catch:{ Exception -> 0x0062 }
        r19 = new java.util.HashMap;	 Catch:{ Exception -> 0x0062 }
        r19.<init>();	 Catch:{ Exception -> 0x0062 }
        r6 = 0;
        r14 = new java.util.HashMap;	 Catch:{ Exception -> 0x0062 }
        r14.<init>();	 Catch:{ Exception -> 0x0062 }
        r9 = 0;
        r8 = 0;
        r5 = 0;
        r15 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0062 }
        r15.<init>();	 Catch:{ Exception -> 0x0062 }
        r16 = new java.util.HashMap;	 Catch:{ Exception -> 0x0062 }
        r16.<init>();	 Catch:{ Exception -> 0x0062 }
        r20 = new java.util.HashMap;	 Catch:{ Exception -> 0x0062 }
        r20.<init>();	 Catch:{ Exception -> 0x0062 }
        r0 = r31;
        r4 = r0.database;	 Catch:{ Exception -> 0x0062 }
        r7 = "REPLACE INTO messages VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, NULL, ?)";
        r21 = r4.executeFast(r7);	 Catch:{ Exception -> 0x0062 }
        r7 = 0;
        r0 = r31;
        r4 = r0.database;	 Catch:{ Exception -> 0x0062 }
        r10 = "REPLACE INTO randoms VALUES(?, ?)";
        r22 = r4.executeFast(r10);	 Catch:{ Exception -> 0x0062 }
        r0 = r31;
        r4 = r0.database;	 Catch:{ Exception -> 0x0062 }
        r10 = "REPLACE INTO download_queue VALUES(?, ?, ?, ?)";
        r23 = r4.executeFast(r10);	 Catch:{ Exception -> 0x0062 }
        r0 = r31;
        r4 = r0.database;	 Catch:{ Exception -> 0x0062 }
        r10 = "REPLACE INTO webpage_pending VALUES(?, ?)";
        r24 = r4.executeFast(r10);	 Catch:{ Exception -> 0x0062 }
        r4 = 0;
        r10 = r4;
        r11 = r5;
        r12 = r8;
        r13 = r9;
    L_0x00d0:
        r4 = r32.size();	 Catch:{ Exception -> 0x0062 }
        if (r10 >= r4) goto L_0x0259;
    L_0x00d6:
        r0 = r32;
        r4 = r0.get(r10);	 Catch:{ Exception -> 0x0062 }
        r0 = r4;
        r0 = (com.hanista.mobogram.tgnet.TLRPC.Message) r0;	 Catch:{ Exception -> 0x0062 }
        r5 = r0;
        r4 = r5.id;	 Catch:{ Exception -> 0x0062 }
        r8 = (long) r4;	 Catch:{ Exception -> 0x0062 }
        r0 = r5.dialog_id;	 Catch:{ Exception -> 0x0062 }
        r26 = r0;
        r28 = 0;
        r4 = (r26 > r28 ? 1 : (r26 == r28 ? 0 : -1));
        if (r4 != 0) goto L_0x00fe;
    L_0x00ed:
        r4 = r5.to_id;	 Catch:{ Exception -> 0x0062 }
        r4 = r4.user_id;	 Catch:{ Exception -> 0x0062 }
        if (r4 == 0) goto L_0x0230;
    L_0x00f3:
        r4 = r5.to_id;	 Catch:{ Exception -> 0x0062 }
        r4 = r4.user_id;	 Catch:{ Exception -> 0x0062 }
        r0 = (long) r4;	 Catch:{ Exception -> 0x0062 }
        r26 = r0;
        r0 = r26;
        r5.dialog_id = r0;	 Catch:{ Exception -> 0x0062 }
    L_0x00fe:
        r4 = r5.to_id;	 Catch:{ Exception -> 0x0062 }
        r4 = r4.channel_id;	 Catch:{ Exception -> 0x0062 }
        if (r4 == 0) goto L_0x0111;
    L_0x0104:
        r4 = r5.to_id;	 Catch:{ Exception -> 0x0062 }
        r4 = r4.channel_id;	 Catch:{ Exception -> 0x0062 }
        r0 = (long) r4;	 Catch:{ Exception -> 0x0062 }
        r26 = r0;
        r4 = 32;
        r26 = r26 << r4;
        r8 = r8 | r26;
    L_0x0111:
        r4 = com.hanista.mobogram.messenger.MessageObject.isUnread(r5);	 Catch:{ Exception -> 0x0062 }
        if (r4 == 0) goto L_0x01ba;
    L_0x0117:
        r4 = com.hanista.mobogram.messenger.MessageObject.isOut(r5);	 Catch:{ Exception -> 0x0062 }
        if (r4 != 0) goto L_0x01ba;
    L_0x011d:
        r0 = r5.dialog_id;	 Catch:{ Exception -> 0x0062 }
        r26 = r0;
        r4 = java.lang.Long.valueOf(r26);	 Catch:{ Exception -> 0x0062 }
        r0 = r16;
        r4 = r0.get(r4);	 Catch:{ Exception -> 0x0062 }
        r4 = (java.lang.Integer) r4;	 Catch:{ Exception -> 0x0062 }
        if (r4 != 0) goto L_0x0186;
    L_0x012f:
        r0 = r31;
        r4 = r0.database;	 Catch:{ Exception -> 0x0062 }
        r17 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0062 }
        r17.<init>();	 Catch:{ Exception -> 0x0062 }
        r25 = "SELECT inbox_max FROM dialogs WHERE did = ";
        r0 = r17;
        r1 = r25;
        r17 = r0.append(r1);	 Catch:{ Exception -> 0x0062 }
        r0 = r5.dialog_id;	 Catch:{ Exception -> 0x0062 }
        r26 = r0;
        r0 = r17;
        r1 = r26;
        r17 = r0.append(r1);	 Catch:{ Exception -> 0x0062 }
        r17 = r17.toString();	 Catch:{ Exception -> 0x0062 }
        r25 = 0;
        r0 = r25;
        r0 = new java.lang.Object[r0];	 Catch:{ Exception -> 0x0062 }
        r25 = r0;
        r0 = r17;
        r1 = r25;
        r17 = r4.queryFinalized(r0, r1);	 Catch:{ Exception -> 0x0062 }
        r4 = r17.next();	 Catch:{ Exception -> 0x0062 }
        if (r4 == 0) goto L_0x0252;
    L_0x0169:
        r4 = 0;
        r0 = r17;
        r4 = r0.intValue(r4);	 Catch:{ Exception -> 0x0062 }
        r4 = java.lang.Integer.valueOf(r4);	 Catch:{ Exception -> 0x0062 }
    L_0x0174:
        r17.dispose();	 Catch:{ Exception -> 0x0062 }
        r0 = r5.dialog_id;	 Catch:{ Exception -> 0x0062 }
        r26 = r0;
        r17 = java.lang.Long.valueOf(r26);	 Catch:{ Exception -> 0x0062 }
        r0 = r16;
        r1 = r17;
        r0.put(r1, r4);	 Catch:{ Exception -> 0x0062 }
    L_0x0186:
        r0 = r5.id;	 Catch:{ Exception -> 0x0062 }
        r17 = r0;
        if (r17 < 0) goto L_0x0198;
    L_0x018c:
        r4 = r4.intValue();	 Catch:{ Exception -> 0x0062 }
        r0 = r5.id;	 Catch:{ Exception -> 0x0062 }
        r17 = r0;
        r0 = r17;
        if (r4 >= r0) goto L_0x01ba;
    L_0x0198:
        r4 = r15.length();	 Catch:{ Exception -> 0x0062 }
        if (r4 <= 0) goto L_0x01a4;
    L_0x019e:
        r4 = ",";
        r15.append(r4);	 Catch:{ Exception -> 0x0062 }
    L_0x01a4:
        r15.append(r8);	 Catch:{ Exception -> 0x0062 }
        r4 = java.lang.Long.valueOf(r8);	 Catch:{ Exception -> 0x0062 }
        r0 = r5.dialog_id;	 Catch:{ Exception -> 0x0062 }
        r26 = r0;
        r17 = java.lang.Long.valueOf(r26);	 Catch:{ Exception -> 0x0062 }
        r0 = r20;
        r1 = r17;
        r0.put(r4, r1);	 Catch:{ Exception -> 0x0062 }
    L_0x01ba:
        r4 = com.hanista.mobogram.messenger.query.SharedMediaQuery.canAddMessageToMedia(r5);	 Catch:{ Exception -> 0x0062 }
        if (r4 == 0) goto L_0x0200;
    L_0x01c0:
        if (r12 != 0) goto L_0x01d1;
    L_0x01c2:
        r12 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0062 }
        r12.<init>();	 Catch:{ Exception -> 0x0062 }
        r13 = new java.util.HashMap;	 Catch:{ Exception -> 0x0062 }
        r13.<init>();	 Catch:{ Exception -> 0x0062 }
        r11 = new java.util.HashMap;	 Catch:{ Exception -> 0x0062 }
        r11.<init>();	 Catch:{ Exception -> 0x0062 }
    L_0x01d1:
        r4 = r12.length();	 Catch:{ Exception -> 0x0062 }
        if (r4 <= 0) goto L_0x01dd;
    L_0x01d7:
        r4 = ",";
        r12.append(r4);	 Catch:{ Exception -> 0x0062 }
    L_0x01dd:
        r12.append(r8);	 Catch:{ Exception -> 0x0062 }
        r4 = java.lang.Long.valueOf(r8);	 Catch:{ Exception -> 0x0062 }
        r0 = r5.dialog_id;	 Catch:{ Exception -> 0x0062 }
        r26 = r0;
        r17 = java.lang.Long.valueOf(r26);	 Catch:{ Exception -> 0x0062 }
        r0 = r17;
        r13.put(r4, r0);	 Catch:{ Exception -> 0x0062 }
        r4 = java.lang.Long.valueOf(r8);	 Catch:{ Exception -> 0x0062 }
        r8 = com.hanista.mobogram.messenger.query.SharedMediaQuery.getMediaType(r5);	 Catch:{ Exception -> 0x0062 }
        r8 = java.lang.Integer.valueOf(r8);	 Catch:{ Exception -> 0x0062 }
        r11.put(r4, r8);	 Catch:{ Exception -> 0x0062 }
    L_0x0200:
        r8 = r11;
        r9 = r12;
        r11 = r13;
        r0 = r31;
        r4 = r0.isValidKeyboardToSave(r5);	 Catch:{ Exception -> 0x0062 }
        if (r4 == 0) goto L_0x0228;
    L_0x020b:
        r12 = r5.dialog_id;	 Catch:{ Exception -> 0x0062 }
        r4 = java.lang.Long.valueOf(r12);	 Catch:{ Exception -> 0x0062 }
        r4 = r14.get(r4);	 Catch:{ Exception -> 0x0062 }
        r4 = (com.hanista.mobogram.tgnet.TLRPC.Message) r4;	 Catch:{ Exception -> 0x0062 }
        if (r4 == 0) goto L_0x021f;
    L_0x0219:
        r4 = r4.id;	 Catch:{ Exception -> 0x0062 }
        r12 = r5.id;	 Catch:{ Exception -> 0x0062 }
        if (r4 >= r12) goto L_0x0228;
    L_0x021f:
        r12 = r5.dialog_id;	 Catch:{ Exception -> 0x0062 }
        r4 = java.lang.Long.valueOf(r12);	 Catch:{ Exception -> 0x0062 }
        r14.put(r4, r5);	 Catch:{ Exception -> 0x0062 }
    L_0x0228:
        r4 = r10 + 1;
        r10 = r4;
        r12 = r9;
        r13 = r11;
        r11 = r8;
        goto L_0x00d0;
    L_0x0230:
        r4 = r5.to_id;	 Catch:{ Exception -> 0x0062 }
        r4 = r4.chat_id;	 Catch:{ Exception -> 0x0062 }
        if (r4 == 0) goto L_0x0244;
    L_0x0236:
        r4 = r5.to_id;	 Catch:{ Exception -> 0x0062 }
        r4 = r4.chat_id;	 Catch:{ Exception -> 0x0062 }
        r4 = -r4;
        r0 = (long) r4;	 Catch:{ Exception -> 0x0062 }
        r26 = r0;
        r0 = r26;
        r5.dialog_id = r0;	 Catch:{ Exception -> 0x0062 }
        goto L_0x00fe;
    L_0x0244:
        r4 = r5.to_id;	 Catch:{ Exception -> 0x0062 }
        r4 = r4.channel_id;	 Catch:{ Exception -> 0x0062 }
        r4 = -r4;
        r0 = (long) r4;	 Catch:{ Exception -> 0x0062 }
        r26 = r0;
        r0 = r26;
        r5.dialog_id = r0;	 Catch:{ Exception -> 0x0062 }
        goto L_0x00fe;
    L_0x0252:
        r4 = 0;
        r4 = java.lang.Integer.valueOf(r4);	 Catch:{ Exception -> 0x0062 }
        goto L_0x0174;
    L_0x0259:
        r4 = r14.entrySet();	 Catch:{ Exception -> 0x0062 }
        r8 = r4.iterator();	 Catch:{ Exception -> 0x0062 }
    L_0x0261:
        r4 = r8.hasNext();	 Catch:{ Exception -> 0x0062 }
        if (r4 == 0) goto L_0x0283;
    L_0x0267:
        r4 = r8.next();	 Catch:{ Exception -> 0x0062 }
        r4 = (java.util.Map.Entry) r4;	 Catch:{ Exception -> 0x0062 }
        r5 = r4.getKey();	 Catch:{ Exception -> 0x0062 }
        r5 = (java.lang.Long) r5;	 Catch:{ Exception -> 0x0062 }
        r16 = r5.longValue();	 Catch:{ Exception -> 0x0062 }
        r4 = r4.getValue();	 Catch:{ Exception -> 0x0062 }
        r4 = (com.hanista.mobogram.tgnet.TLRPC.Message) r4;	 Catch:{ Exception -> 0x0062 }
        r0 = r16;
        com.hanista.mobogram.messenger.query.BotQuery.putBotKeyboard(r0, r4);	 Catch:{ Exception -> 0x0062 }
        goto L_0x0261;
    L_0x0283:
        if (r12 == 0) goto L_0x08eb;
    L_0x0285:
        r0 = r31;
        r4 = r0.database;	 Catch:{ Exception -> 0x0062 }
        r5 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0062 }
        r5.<init>();	 Catch:{ Exception -> 0x0062 }
        r6 = "SELECT mid FROM media_v2 WHERE mid IN(";
        r5 = r5.append(r6);	 Catch:{ Exception -> 0x0062 }
        r6 = r12.toString();	 Catch:{ Exception -> 0x0062 }
        r5 = r5.append(r6);	 Catch:{ Exception -> 0x0062 }
        r6 = ")";
        r5 = r5.append(r6);	 Catch:{ Exception -> 0x0062 }
        r5 = r5.toString();	 Catch:{ Exception -> 0x0062 }
        r6 = 0;
        r6 = new java.lang.Object[r6];	 Catch:{ Exception -> 0x0062 }
        r4 = r4.queryFinalized(r5, r6);	 Catch:{ Exception -> 0x0062 }
    L_0x02af:
        r5 = r4.next();	 Catch:{ Exception -> 0x0062 }
        if (r5 == 0) goto L_0x02c2;
    L_0x02b5:
        r5 = 0;
        r8 = r4.longValue(r5);	 Catch:{ Exception -> 0x0062 }
        r5 = java.lang.Long.valueOf(r8);	 Catch:{ Exception -> 0x0062 }
        r13.remove(r5);	 Catch:{ Exception -> 0x0062 }
        goto L_0x02af;
    L_0x02c2:
        r4.dispose();	 Catch:{ Exception -> 0x0062 }
        r8 = new java.util.HashMap;	 Catch:{ Exception -> 0x0062 }
        r8.<init>();	 Catch:{ Exception -> 0x0062 }
        r4 = r13.entrySet();	 Catch:{ Exception -> 0x0062 }
        r10 = r4.iterator();	 Catch:{ Exception -> 0x0062 }
    L_0x02d2:
        r4 = r10.hasNext();	 Catch:{ Exception -> 0x0062 }
        if (r4 == 0) goto L_0x0323;
    L_0x02d8:
        r4 = r10.next();	 Catch:{ Exception -> 0x0062 }
        r4 = (java.util.Map.Entry) r4;	 Catch:{ Exception -> 0x0062 }
        r5 = r4.getKey();	 Catch:{ Exception -> 0x0062 }
        r5 = r11.get(r5);	 Catch:{ Exception -> 0x0062 }
        r5 = (java.lang.Integer) r5;	 Catch:{ Exception -> 0x0062 }
        r6 = r8.get(r5);	 Catch:{ Exception -> 0x0062 }
        r6 = (java.util.HashMap) r6;	 Catch:{ Exception -> 0x0062 }
        if (r6 != 0) goto L_0x0318;
    L_0x02f0:
        r9 = new java.util.HashMap;	 Catch:{ Exception -> 0x0062 }
        r9.<init>();	 Catch:{ Exception -> 0x0062 }
        r6 = 0;
        r6 = java.lang.Integer.valueOf(r6);	 Catch:{ Exception -> 0x0062 }
        r8.put(r5, r9);	 Catch:{ Exception -> 0x0062 }
        r5 = r6;
        r6 = r9;
    L_0x02ff:
        if (r5 != 0) goto L_0x0306;
    L_0x0301:
        r5 = 0;
        r5 = java.lang.Integer.valueOf(r5);	 Catch:{ Exception -> 0x0062 }
    L_0x0306:
        r5 = r5.intValue();	 Catch:{ Exception -> 0x0062 }
        r5 = r5 + 1;
        r5 = java.lang.Integer.valueOf(r5);	 Catch:{ Exception -> 0x0062 }
        r4 = r4.getValue();	 Catch:{ Exception -> 0x0062 }
        r6.put(r4, r5);	 Catch:{ Exception -> 0x0062 }
        goto L_0x02d2;
    L_0x0318:
        r5 = r4.getValue();	 Catch:{ Exception -> 0x0062 }
        r5 = r6.get(r5);	 Catch:{ Exception -> 0x0062 }
        r5 = (java.lang.Integer) r5;	 Catch:{ Exception -> 0x0062 }
        goto L_0x02ff;
    L_0x0323:
        r17 = r8;
    L_0x0325:
        r4 = r15.length();	 Catch:{ Exception -> 0x0062 }
        if (r4 <= 0) goto L_0x03a0;
    L_0x032b:
        r0 = r31;
        r4 = r0.database;	 Catch:{ Exception -> 0x0062 }
        r5 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0062 }
        r5.<init>();	 Catch:{ Exception -> 0x0062 }
        r6 = "SELECT mid FROM messages WHERE mid IN(";
        r5 = r5.append(r6);	 Catch:{ Exception -> 0x0062 }
        r6 = r15.toString();	 Catch:{ Exception -> 0x0062 }
        r5 = r5.append(r6);	 Catch:{ Exception -> 0x0062 }
        r6 = ")";
        r5 = r5.append(r6);	 Catch:{ Exception -> 0x0062 }
        r5 = r5.toString();	 Catch:{ Exception -> 0x0062 }
        r6 = 0;
        r6 = new java.lang.Object[r6];	 Catch:{ Exception -> 0x0062 }
        r4 = r4.queryFinalized(r5, r6);	 Catch:{ Exception -> 0x0062 }
    L_0x0355:
        r5 = r4.next();	 Catch:{ Exception -> 0x0062 }
        if (r5 == 0) goto L_0x036a;
    L_0x035b:
        r5 = 0;
        r8 = r4.longValue(r5);	 Catch:{ Exception -> 0x0062 }
        r5 = java.lang.Long.valueOf(r8);	 Catch:{ Exception -> 0x0062 }
        r0 = r20;
        r0.remove(r5);	 Catch:{ Exception -> 0x0062 }
        goto L_0x0355;
    L_0x036a:
        r4.dispose();	 Catch:{ Exception -> 0x0062 }
        r4 = r20.values();	 Catch:{ Exception -> 0x0062 }
        r6 = r4.iterator();	 Catch:{ Exception -> 0x0062 }
    L_0x0375:
        r4 = r6.hasNext();	 Catch:{ Exception -> 0x0062 }
        if (r4 == 0) goto L_0x03a0;
    L_0x037b:
        r4 = r6.next();	 Catch:{ Exception -> 0x0062 }
        r4 = (java.lang.Long) r4;	 Catch:{ Exception -> 0x0062 }
        r0 = r19;
        r5 = r0.get(r4);	 Catch:{ Exception -> 0x0062 }
        r5 = (java.lang.Integer) r5;	 Catch:{ Exception -> 0x0062 }
        if (r5 != 0) goto L_0x0390;
    L_0x038b:
        r5 = 0;
        r5 = java.lang.Integer.valueOf(r5);	 Catch:{ Exception -> 0x0062 }
    L_0x0390:
        r5 = r5.intValue();	 Catch:{ Exception -> 0x0062 }
        r5 = r5 + 1;
        r5 = java.lang.Integer.valueOf(r5);	 Catch:{ Exception -> 0x0062 }
        r0 = r19;
        r0.put(r4, r5);	 Catch:{ Exception -> 0x0062 }
        goto L_0x0375;
    L_0x03a0:
        r10 = 0;
        r9 = 0;
        r4 = 0;
        r11 = r4;
        r8 = r7;
    L_0x03a5:
        r4 = r32.size();	 Catch:{ Exception -> 0x0062 }
        if (r11 >= r4) goto L_0x0677;
    L_0x03ab:
        r0 = r32;
        r4 = r0.get(r11);	 Catch:{ Exception -> 0x0062 }
        r4 = (com.hanista.mobogram.tgnet.TLRPC.Message) r4;	 Catch:{ Exception -> 0x0062 }
        r0 = r31;
        r0.fixUnsupportedMedia(r4);	 Catch:{ Exception -> 0x0062 }
        r21.requery();	 Catch:{ Exception -> 0x0062 }
        r5 = r4.id;	 Catch:{ Exception -> 0x0062 }
        r6 = (long) r5;	 Catch:{ Exception -> 0x0062 }
        r5 = r4.local_id;	 Catch:{ Exception -> 0x0062 }
        if (r5 == 0) goto L_0x03c5;
    L_0x03c2:
        r5 = r4.local_id;	 Catch:{ Exception -> 0x0062 }
        r6 = (long) r5;	 Catch:{ Exception -> 0x0062 }
    L_0x03c5:
        r5 = r4.to_id;	 Catch:{ Exception -> 0x0062 }
        r5 = r5.channel_id;	 Catch:{ Exception -> 0x0062 }
        if (r5 == 0) goto L_0x03d4;
    L_0x03cb:
        r5 = r4.to_id;	 Catch:{ Exception -> 0x0062 }
        r5 = r5.channel_id;	 Catch:{ Exception -> 0x0062 }
        r12 = (long) r5;	 Catch:{ Exception -> 0x0062 }
        r5 = 32;
        r12 = r12 << r5;
        r6 = r6 | r12;
    L_0x03d4:
        r13 = new com.hanista.mobogram.tgnet.NativeByteBuffer;	 Catch:{ Exception -> 0x0062 }
        r5 = r4.getObjectSize();	 Catch:{ Exception -> 0x0062 }
        r13.<init>(r5);	 Catch:{ Exception -> 0x0062 }
        r4.serializeToStream(r13);	 Catch:{ Exception -> 0x0062 }
        r5 = 1;
        r12 = r4.action;	 Catch:{ Exception -> 0x0062 }
        if (r12 == 0) goto L_0x03fc;
    L_0x03e5:
        r12 = r4.action;	 Catch:{ Exception -> 0x0062 }
        r12 = r12 instanceof com.hanista.mobogram.tgnet.TLRPC.TL_messageEncryptedAction;	 Catch:{ Exception -> 0x0062 }
        if (r12 == 0) goto L_0x03fc;
    L_0x03eb:
        r12 = r4.action;	 Catch:{ Exception -> 0x0062 }
        r12 = r12.encryptedAction;	 Catch:{ Exception -> 0x0062 }
        r12 = r12 instanceof com.hanista.mobogram.tgnet.TLRPC.TL_decryptedMessageActionSetMessageTTL;	 Catch:{ Exception -> 0x0062 }
        if (r12 != 0) goto L_0x03fc;
    L_0x03f3:
        r12 = r4.action;	 Catch:{ Exception -> 0x0062 }
        r12 = r12.encryptedAction;	 Catch:{ Exception -> 0x0062 }
        r12 = r12 instanceof com.hanista.mobogram.tgnet.TLRPC.TL_decryptedMessageActionScreenshotMessages;	 Catch:{ Exception -> 0x0062 }
        if (r12 != 0) goto L_0x03fc;
    L_0x03fb:
        r5 = 0;
    L_0x03fc:
        if (r5 == 0) goto L_0x043b;
    L_0x03fe:
        r14 = r4.dialog_id;	 Catch:{ Exception -> 0x0062 }
        r5 = java.lang.Long.valueOf(r14);	 Catch:{ Exception -> 0x0062 }
        r0 = r18;
        r5 = r0.get(r5);	 Catch:{ Exception -> 0x0062 }
        r5 = (com.hanista.mobogram.tgnet.TLRPC.Message) r5;	 Catch:{ Exception -> 0x0062 }
        if (r5 == 0) goto L_0x0430;
    L_0x040e:
        r12 = r4.date;	 Catch:{ Exception -> 0x0062 }
        r14 = r5.date;	 Catch:{ Exception -> 0x0062 }
        if (r12 > r14) goto L_0x0430;
    L_0x0414:
        r12 = r4.id;	 Catch:{ Exception -> 0x0062 }
        if (r12 <= 0) goto L_0x0422;
    L_0x0418:
        r12 = r5.id;	 Catch:{ Exception -> 0x0062 }
        if (r12 <= 0) goto L_0x0422;
    L_0x041c:
        r12 = r4.id;	 Catch:{ Exception -> 0x0062 }
        r14 = r5.id;	 Catch:{ Exception -> 0x0062 }
        if (r12 > r14) goto L_0x0430;
    L_0x0422:
        r12 = r4.id;	 Catch:{ Exception -> 0x0062 }
        if (r12 >= 0) goto L_0x043b;
    L_0x0426:
        r12 = r5.id;	 Catch:{ Exception -> 0x0062 }
        if (r12 >= 0) goto L_0x043b;
    L_0x042a:
        r12 = r4.id;	 Catch:{ Exception -> 0x0062 }
        r5 = r5.id;	 Catch:{ Exception -> 0x0062 }
        if (r12 >= r5) goto L_0x043b;
    L_0x0430:
        r14 = r4.dialog_id;	 Catch:{ Exception -> 0x0062 }
        r5 = java.lang.Long.valueOf(r14);	 Catch:{ Exception -> 0x0062 }
        r0 = r18;
        r0.put(r5, r4);	 Catch:{ Exception -> 0x0062 }
    L_0x043b:
        r5 = 1;
        r0 = r21;
        r0.bindLong(r5, r6);	 Catch:{ Exception -> 0x0062 }
        r5 = 2;
        r14 = r4.dialog_id;	 Catch:{ Exception -> 0x0062 }
        r0 = r21;
        r0.bindLong(r5, r14);	 Catch:{ Exception -> 0x0062 }
        r5 = 3;
        r12 = com.hanista.mobogram.messenger.MessageObject.getUnreadFlags(r4);	 Catch:{ Exception -> 0x0062 }
        r0 = r21;
        r0.bindInteger(r5, r12);	 Catch:{ Exception -> 0x0062 }
        r5 = 4;
        r12 = r4.send_state;	 Catch:{ Exception -> 0x0062 }
        r0 = r21;
        r0.bindInteger(r5, r12);	 Catch:{ Exception -> 0x0062 }
        r5 = 5;
        r12 = r4.date;	 Catch:{ Exception -> 0x0062 }
        r0 = r21;
        r0.bindInteger(r5, r12);	 Catch:{ Exception -> 0x0062 }
        r5 = 6;
        r0 = r21;
        r0.bindByteBuffer(r5, r13);	 Catch:{ Exception -> 0x0062 }
        r12 = 7;
        r5 = com.hanista.mobogram.messenger.MessageObject.isOut(r4);	 Catch:{ Exception -> 0x0062 }
        if (r5 == 0) goto L_0x05d0;
    L_0x0470:
        r5 = 1;
    L_0x0471:
        r0 = r21;
        r0.bindInteger(r12, r5);	 Catch:{ Exception -> 0x0062 }
        r5 = 8;
        r12 = r4.ttl;	 Catch:{ Exception -> 0x0062 }
        r0 = r21;
        r0.bindInteger(r5, r12);	 Catch:{ Exception -> 0x0062 }
        r5 = r4.flags;	 Catch:{ Exception -> 0x0062 }
        r5 = r5 & 1024;
        if (r5 == 0) goto L_0x05d3;
    L_0x0485:
        r5 = 9;
        r12 = r4.views;	 Catch:{ Exception -> 0x0062 }
        r0 = r21;
        r0.bindInteger(r5, r12);	 Catch:{ Exception -> 0x0062 }
    L_0x048e:
        r5 = 10;
        r12 = 0;
        r0 = r21;
        r0.bindInteger(r5, r12);	 Catch:{ Exception -> 0x0062 }
        r21.step();	 Catch:{ Exception -> 0x0062 }
        r14 = r4.random_id;	 Catch:{ Exception -> 0x0062 }
        r26 = 0;
        r5 = (r14 > r26 ? 1 : (r14 == r26 ? 0 : -1));
        if (r5 == 0) goto L_0x04b5;
    L_0x04a1:
        r22.requery();	 Catch:{ Exception -> 0x0062 }
        r5 = 1;
        r14 = r4.random_id;	 Catch:{ Exception -> 0x0062 }
        r0 = r22;
        r0.bindLong(r5, r14);	 Catch:{ Exception -> 0x0062 }
        r5 = 2;
        r0 = r22;
        r0.bindLong(r5, r6);	 Catch:{ Exception -> 0x0062 }
        r22.step();	 Catch:{ Exception -> 0x0062 }
    L_0x04b5:
        r5 = com.hanista.mobogram.messenger.query.SharedMediaQuery.canAddMessageToMedia(r4);	 Catch:{ Exception -> 0x0062 }
        if (r5 == 0) goto L_0x08dd;
    L_0x04bb:
        if (r8 != 0) goto L_0x08da;
    L_0x04bd:
        r0 = r31;
        r5 = r0.database;	 Catch:{ Exception -> 0x0062 }
        r8 = "REPLACE INTO media_v2 VALUES(?, ?, ?, ?, ?)";
        r12 = r5.executeFast(r8);	 Catch:{ Exception -> 0x0062 }
    L_0x04c8:
        r12.requery();	 Catch:{ Exception -> 0x0062 }
        r5 = 1;
        r12.bindLong(r5, r6);	 Catch:{ Exception -> 0x0062 }
        r5 = 2;
        r14 = r4.dialog_id;	 Catch:{ Exception -> 0x0062 }
        r12.bindLong(r5, r14);	 Catch:{ Exception -> 0x0062 }
        r5 = 3;
        r8 = r4.date;	 Catch:{ Exception -> 0x0062 }
        r12.bindInteger(r5, r8);	 Catch:{ Exception -> 0x0062 }
        r5 = 4;
        r8 = com.hanista.mobogram.messenger.query.SharedMediaQuery.getMediaType(r4);	 Catch:{ Exception -> 0x0062 }
        r12.bindInteger(r5, r8);	 Catch:{ Exception -> 0x0062 }
        r5 = 5;
        r12.bindByteBuffer(r5, r13);	 Catch:{ Exception -> 0x0062 }
        r12.step();	 Catch:{ Exception -> 0x0062 }
    L_0x04ea:
        r5 = r4.media;	 Catch:{ Exception -> 0x0062 }
        r5 = r5 instanceof com.hanista.mobogram.tgnet.TLRPC.TL_messageMediaWebPage;	 Catch:{ Exception -> 0x0062 }
        if (r5 == 0) goto L_0x0510;
    L_0x04f0:
        r5 = r4.media;	 Catch:{ Exception -> 0x0062 }
        r5 = r5.webpage;	 Catch:{ Exception -> 0x0062 }
        r5 = r5 instanceof com.hanista.mobogram.tgnet.TLRPC.TL_webPagePending;	 Catch:{ Exception -> 0x0062 }
        if (r5 == 0) goto L_0x0510;
    L_0x04f8:
        r24.requery();	 Catch:{ Exception -> 0x0062 }
        r5 = 1;
        r8 = r4.media;	 Catch:{ Exception -> 0x0062 }
        r8 = r8.webpage;	 Catch:{ Exception -> 0x0062 }
        r14 = r8.id;	 Catch:{ Exception -> 0x0062 }
        r0 = r24;
        r0.bindLong(r5, r14);	 Catch:{ Exception -> 0x0062 }
        r5 = 2;
        r0 = r24;
        r0.bindLong(r5, r6);	 Catch:{ Exception -> 0x0062 }
        r24.step();	 Catch:{ Exception -> 0x0062 }
    L_0x0510:
        r13.reuse();	 Catch:{ Exception -> 0x0062 }
        r5 = 0;
        r6 = r4.dialog_id;	 Catch:{ Exception -> 0x0062 }
        r6 = java.lang.Long.valueOf(r6);	 Catch:{ Exception -> 0x0062 }
        r6 = com.hanista.mobogram.mobo.p007h.FavoriteUtil.m1142b(r6);	 Catch:{ Exception -> 0x0062 }
        if (r6 == 0) goto L_0x0525;
    L_0x0520:
        r5 = com.hanista.mobogram.mobo.MoboConstants.f1346m;	 Catch:{ Exception -> 0x0062 }
        r35 = r35 | r5;
        r5 = 1;
    L_0x0525:
        r6 = r4.dialog_id;	 Catch:{ Exception -> 0x0062 }
        r6 = com.hanista.mobogram.mobo.p005f.DialogSettingsUtil.m997a(r6);	 Catch:{ Exception -> 0x0062 }
        if (r6 == 0) goto L_0x08e8;
    L_0x052d:
        r7 = r6.m980c();	 Catch:{ Exception -> 0x0062 }
        if (r7 == 0) goto L_0x08e8;
    L_0x0533:
        r35 = r6.m980c();	 Catch:{ Exception -> 0x0062 }
        r5 = 1;
        r13 = r5;
    L_0x0539:
        r5 = r4.to_id;	 Catch:{ Exception -> 0x0062 }
        r5 = r5.channel_id;	 Catch:{ Exception -> 0x0062 }
        if (r5 == 0) goto L_0x0543;
    L_0x053f:
        r5 = r4.post;	 Catch:{ Exception -> 0x0062 }
        if (r5 == 0) goto L_0x08e0;
    L_0x0543:
        r5 = r4.date;	 Catch:{ Exception -> 0x0062 }
        r6 = com.hanista.mobogram.tgnet.ConnectionsManager.getInstance();	 Catch:{ Exception -> 0x0062 }
        r6 = r6.getCurrentTime();	 Catch:{ Exception -> 0x0062 }
        r6 = r6 + -3600;
        if (r5 < r6) goto L_0x08e0;
    L_0x0551:
        if (r35 == 0) goto L_0x08e0;
    L_0x0553:
        r5 = r4.media;	 Catch:{ Exception -> 0x0062 }
        r5 = r5 instanceof com.hanista.mobogram.tgnet.TLRPC.TL_messageMediaPhoto;	 Catch:{ Exception -> 0x0062 }
        if (r5 != 0) goto L_0x055f;
    L_0x0559:
        r5 = r4.media;	 Catch:{ Exception -> 0x0062 }
        r5 = r5 instanceof com.hanista.mobogram.tgnet.TLRPC.TL_messageMediaDocument;	 Catch:{ Exception -> 0x0062 }
        if (r5 == 0) goto L_0x08e0;
    L_0x055f:
        r8 = 0;
        r6 = 0;
        r5 = 0;
        r14 = com.hanista.mobogram.messenger.MessageObject.isVoiceMessage(r4);	 Catch:{ Exception -> 0x0062 }
        if (r14 == 0) goto L_0x05e2;
    L_0x0569:
        r14 = r35 & 2;
        if (r14 == 0) goto L_0x08e4;
    L_0x056d:
        r14 = r4.media;	 Catch:{ Exception -> 0x0062 }
        r14 = r14.document;	 Catch:{ Exception -> 0x0062 }
        r14 = r14.size;	 Catch:{ Exception -> 0x0062 }
        r15 = 5242880; // 0x500000 float:7.34684E-39 double:2.590327E-317;
        if (r14 >= r15) goto L_0x08e4;
    L_0x0577:
        r5 = r4.media;	 Catch:{ Exception -> 0x0062 }
        r5 = r5.document;	 Catch:{ Exception -> 0x0062 }
        r6 = r5.id;	 Catch:{ Exception -> 0x0062 }
        r8 = 2;
        r5 = new com.hanista.mobogram.tgnet.TLRPC$TL_messageMediaDocument;	 Catch:{ Exception -> 0x0062 }
        r5.<init>();	 Catch:{ Exception -> 0x0062 }
        r14 = "";
        r5.caption = r14;	 Catch:{ Exception -> 0x0062 }
        r14 = r4.media;	 Catch:{ Exception -> 0x0062 }
        r14 = r14.document;	 Catch:{ Exception -> 0x0062 }
        r5.document = r14;	 Catch:{ Exception -> 0x0062 }
        r14 = r6;
        r7 = r5;
    L_0x0590:
        if (r7 == 0) goto L_0x08e0;
    L_0x0592:
        if (r13 == 0) goto L_0x0672;
    L_0x0594:
        r5 = r9 | r8;
        r6 = r10;
    L_0x0597:
        r23.requery();	 Catch:{ Exception -> 0x0062 }
        r9 = new com.hanista.mobogram.tgnet.NativeByteBuffer;	 Catch:{ Exception -> 0x0062 }
        r10 = r7.getObjectSize();	 Catch:{ Exception -> 0x0062 }
        r9.<init>(r10);	 Catch:{ Exception -> 0x0062 }
        r7.serializeToStream(r9);	 Catch:{ Exception -> 0x0062 }
        r7 = 1;
        r0 = r23;
        r0.bindLong(r7, r14);	 Catch:{ Exception -> 0x0062 }
        r7 = 2;
        r0 = r23;
        r0.bindInteger(r7, r8);	 Catch:{ Exception -> 0x0062 }
        r7 = 3;
        r4 = r4.date;	 Catch:{ Exception -> 0x0062 }
        r0 = r23;
        r0.bindInteger(r7, r4);	 Catch:{ Exception -> 0x0062 }
        r4 = 4;
        r0 = r23;
        r0.bindByteBuffer(r4, r9);	 Catch:{ Exception -> 0x0062 }
        r23.step();	 Catch:{ Exception -> 0x0062 }
        r9.reuse();	 Catch:{ Exception -> 0x0062 }
        r4 = r5;
        r5 = r6;
    L_0x05c8:
        r6 = r11 + 1;
        r11 = r6;
        r9 = r4;
        r10 = r5;
        r8 = r12;
        goto L_0x03a5;
    L_0x05d0:
        r5 = 0;
        goto L_0x0471;
    L_0x05d3:
        r5 = 9;
        r0 = r31;
        r12 = r0.getMessageMediaType(r4);	 Catch:{ Exception -> 0x0062 }
        r0 = r21;
        r0.bindInteger(r5, r12);	 Catch:{ Exception -> 0x0062 }
        goto L_0x048e;
    L_0x05e2:
        r14 = r4.media;	 Catch:{ Exception -> 0x0062 }
        r14 = r14 instanceof com.hanista.mobogram.tgnet.TLRPC.TL_messageMediaPhoto;	 Catch:{ Exception -> 0x0062 }
        if (r14 == 0) goto L_0x0617;
    L_0x05e8:
        r14 = r35 & 1;
        if (r14 == 0) goto L_0x08e4;
    L_0x05ec:
        r14 = r4.media;	 Catch:{ Exception -> 0x0062 }
        r14 = r14.photo;	 Catch:{ Exception -> 0x0062 }
        r14 = r14.sizes;	 Catch:{ Exception -> 0x0062 }
        r15 = com.hanista.mobogram.messenger.AndroidUtilities.getPhotoSize();	 Catch:{ Exception -> 0x0062 }
        r14 = com.hanista.mobogram.messenger.FileLoader.getClosestPhotoSizeWithSize(r14, r15);	 Catch:{ Exception -> 0x0062 }
        if (r14 == 0) goto L_0x0613;
    L_0x05fc:
        r5 = r4.media;	 Catch:{ Exception -> 0x0062 }
        r5 = r5.photo;	 Catch:{ Exception -> 0x0062 }
        r6 = r5.id;	 Catch:{ Exception -> 0x0062 }
        r8 = 1;
        r5 = new com.hanista.mobogram.tgnet.TLRPC$TL_messageMediaPhoto;	 Catch:{ Exception -> 0x0062 }
        r5.<init>();	 Catch:{ Exception -> 0x0062 }
        r14 = "";
        r5.caption = r14;	 Catch:{ Exception -> 0x0062 }
        r14 = r4.media;	 Catch:{ Exception -> 0x0062 }
        r14 = r14.photo;	 Catch:{ Exception -> 0x0062 }
        r5.photo = r14;	 Catch:{ Exception -> 0x0062 }
    L_0x0613:
        r14 = r6;
        r7 = r5;
        goto L_0x0590;
    L_0x0617:
        r14 = com.hanista.mobogram.messenger.MessageObject.isVideoMessage(r4);	 Catch:{ Exception -> 0x0062 }
        if (r14 == 0) goto L_0x063c;
    L_0x061d:
        r14 = r35 & 4;
        if (r14 == 0) goto L_0x08e4;
    L_0x0621:
        r5 = r4.media;	 Catch:{ Exception -> 0x0062 }
        r5 = r5.document;	 Catch:{ Exception -> 0x0062 }
        r6 = r5.id;	 Catch:{ Exception -> 0x0062 }
        r8 = 4;
        r5 = new com.hanista.mobogram.tgnet.TLRPC$TL_messageMediaDocument;	 Catch:{ Exception -> 0x0062 }
        r5.<init>();	 Catch:{ Exception -> 0x0062 }
        r14 = "";
        r5.caption = r14;	 Catch:{ Exception -> 0x0062 }
        r14 = r4.media;	 Catch:{ Exception -> 0x0062 }
        r14 = r14.document;	 Catch:{ Exception -> 0x0062 }
        r5.document = r14;	 Catch:{ Exception -> 0x0062 }
        r14 = r6;
        r7 = r5;
        goto L_0x0590;
    L_0x063c:
        r14 = r4.media;	 Catch:{ Exception -> 0x0062 }
        r14 = r14 instanceof com.hanista.mobogram.tgnet.TLRPC.TL_messageMediaDocument;	 Catch:{ Exception -> 0x0062 }
        if (r14 == 0) goto L_0x08e4;
    L_0x0642:
        r14 = com.hanista.mobogram.messenger.MessageObject.isMusicMessage(r4);	 Catch:{ Exception -> 0x0062 }
        if (r14 != 0) goto L_0x08e4;
    L_0x0648:
        r14 = r4.media;	 Catch:{ Exception -> 0x0062 }
        r14 = r14.document;	 Catch:{ Exception -> 0x0062 }
        r14 = com.hanista.mobogram.messenger.MessageObject.isGifDocument(r14);	 Catch:{ Exception -> 0x0062 }
        if (r14 != 0) goto L_0x08e4;
    L_0x0652:
        r14 = r35 & 8;
        if (r14 == 0) goto L_0x08e4;
    L_0x0656:
        r5 = r4.media;	 Catch:{ Exception -> 0x0062 }
        r5 = r5.document;	 Catch:{ Exception -> 0x0062 }
        r6 = r5.id;	 Catch:{ Exception -> 0x0062 }
        r8 = 8;
        r5 = new com.hanista.mobogram.tgnet.TLRPC$TL_messageMediaDocument;	 Catch:{ Exception -> 0x0062 }
        r5.<init>();	 Catch:{ Exception -> 0x0062 }
        r14 = "";
        r5.caption = r14;	 Catch:{ Exception -> 0x0062 }
        r14 = r4.media;	 Catch:{ Exception -> 0x0062 }
        r14 = r14.document;	 Catch:{ Exception -> 0x0062 }
        r5.document = r14;	 Catch:{ Exception -> 0x0062 }
        r14 = r6;
        r7 = r5;
        goto L_0x0590;
    L_0x0672:
        r6 = r10 | r8;
        r5 = r9;
        goto L_0x0597;
    L_0x0677:
        r21.dispose();	 Catch:{ Exception -> 0x0062 }
        if (r8 == 0) goto L_0x067f;
    L_0x067c:
        r8.dispose();	 Catch:{ Exception -> 0x0062 }
    L_0x067f:
        r22.dispose();	 Catch:{ Exception -> 0x0062 }
        r23.dispose();	 Catch:{ Exception -> 0x0062 }
        r24.dispose();	 Catch:{ Exception -> 0x0062 }
        r0 = r31;
        r4 = r0.database;	 Catch:{ Exception -> 0x0062 }
        r5 = "REPLACE INTO dialogs VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        r20 = r4.executeFast(r5);	 Catch:{ Exception -> 0x0062 }
        r4 = new java.util.HashMap;	 Catch:{ Exception -> 0x0062 }
        r4.<init>();	 Catch:{ Exception -> 0x0062 }
        r0 = r18;
        r4.putAll(r0);	 Catch:{ Exception -> 0x0062 }
        r4 = r4.entrySet();	 Catch:{ Exception -> 0x0062 }
        r21 = r4.iterator();	 Catch:{ Exception -> 0x0062 }
    L_0x06a5:
        r4 = r21.hasNext();	 Catch:{ Exception -> 0x0062 }
        if (r4 == 0) goto L_0x07f7;
    L_0x06ab:
        r4 = r21.next();	 Catch:{ Exception -> 0x0062 }
        r4 = (java.util.Map.Entry) r4;	 Catch:{ Exception -> 0x0062 }
        r4 = r4.getKey();	 Catch:{ Exception -> 0x0062 }
        r4 = (java.lang.Long) r4;	 Catch:{ Exception -> 0x0062 }
        r6 = r4.longValue();	 Catch:{ Exception -> 0x0062 }
        r12 = 0;
        r5 = (r6 > r12 ? 1 : (r6 == r12 ? 0 : -1));
        if (r5 == 0) goto L_0x06a5;
    L_0x06c1:
        r0 = r18;
        r5 = r0.get(r4);	 Catch:{ Exception -> 0x0062 }
        r5 = (com.hanista.mobogram.tgnet.TLRPC.Message) r5;	 Catch:{ Exception -> 0x0062 }
        r6 = 0;
        if (r5 == 0) goto L_0x08d6;
    L_0x06cc:
        r6 = r5.to_id;	 Catch:{ Exception -> 0x0062 }
        r6 = r6.channel_id;	 Catch:{ Exception -> 0x0062 }
        r16 = r6;
    L_0x06d2:
        r0 = r31;
        r6 = r0.database;	 Catch:{ Exception -> 0x0062 }
        r7 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0062 }
        r7.<init>();	 Catch:{ Exception -> 0x0062 }
        r8 = "SELECT date, unread_count, pts, last_mid, inbox_max, outbox_max FROM dialogs WHERE did = ";
        r7 = r7.append(r8);	 Catch:{ Exception -> 0x0062 }
        r7 = r7.append(r4);	 Catch:{ Exception -> 0x0062 }
        r7 = r7.toString();	 Catch:{ Exception -> 0x0062 }
        r8 = 0;
        r8 = new java.lang.Object[r8];	 Catch:{ Exception -> 0x0062 }
        r22 = r6.queryFinalized(r7, r8);	 Catch:{ Exception -> 0x0062 }
        r13 = 0;
        r12 = 0;
        r11 = 0;
        if (r16 == 0) goto L_0x07c3;
    L_0x06f6:
        r8 = 1;
    L_0x06f7:
        r7 = 0;
        r6 = 0;
        r14 = r22.next();	 Catch:{ Exception -> 0x0062 }
        if (r14 == 0) goto L_0x07c6;
    L_0x06ff:
        r6 = 0;
        r0 = r22;
        r13 = r0.intValue(r6);	 Catch:{ Exception -> 0x0062 }
        r6 = 1;
        r0 = r22;
        r11 = r0.intValue(r6);	 Catch:{ Exception -> 0x0062 }
        r6 = 2;
        r0 = r22;
        r8 = r0.intValue(r6);	 Catch:{ Exception -> 0x0062 }
        r6 = 3;
        r0 = r22;
        r12 = r0.intValue(r6);	 Catch:{ Exception -> 0x0062 }
        r6 = 4;
        r0 = r22;
        r7 = r0.intValue(r6);	 Catch:{ Exception -> 0x0062 }
        r6 = 5;
        r0 = r22;
        r6 = r0.intValue(r6);	 Catch:{ Exception -> 0x0062 }
        r14 = r11;
        r15 = r13;
        r13 = r8;
        r11 = r6;
        r30 = r12;
        r12 = r7;
        r7 = r30;
    L_0x0732:
        r22.dispose();	 Catch:{ Exception -> 0x0062 }
        r0 = r19;
        r6 = r0.get(r4);	 Catch:{ Exception -> 0x0062 }
        r6 = (java.lang.Integer) r6;	 Catch:{ Exception -> 0x0062 }
        if (r6 != 0) goto L_0x07dc;
    L_0x073f:
        r6 = 0;
        r6 = java.lang.Integer.valueOf(r6);	 Catch:{ Exception -> 0x0062 }
        r8 = r6;
    L_0x0745:
        if (r5 == 0) goto L_0x07ed;
    L_0x0747:
        r6 = r5.id;	 Catch:{ Exception -> 0x0062 }
        r6 = (long) r6;	 Catch:{ Exception -> 0x0062 }
    L_0x074a:
        if (r5 == 0) goto L_0x0755;
    L_0x074c:
        r0 = r5.local_id;	 Catch:{ Exception -> 0x0062 }
        r22 = r0;
        if (r22 == 0) goto L_0x0755;
    L_0x0752:
        r6 = r5.local_id;	 Catch:{ Exception -> 0x0062 }
        r6 = (long) r6;	 Catch:{ Exception -> 0x0062 }
    L_0x0755:
        if (r16 == 0) goto L_0x0762;
    L_0x0757:
        r0 = r16;
        r0 = (long) r0;	 Catch:{ Exception -> 0x0062 }
        r22 = r0;
        r16 = 32;
        r22 = r22 << r16;
        r6 = r6 | r22;
    L_0x0762:
        r20.requery();	 Catch:{ Exception -> 0x0062 }
        r16 = 1;
        r22 = r4.longValue();	 Catch:{ Exception -> 0x0062 }
        r0 = r20;
        r1 = r16;
        r2 = r22;
        r0.bindLong(r1, r2);	 Catch:{ Exception -> 0x0062 }
        if (r5 == 0) goto L_0x07f0;
    L_0x0776:
        if (r34 == 0) goto L_0x077a;
    L_0x0778:
        if (r15 != 0) goto L_0x07f0;
    L_0x077a:
        r4 = 2;
        r5 = r5.date;	 Catch:{ Exception -> 0x0062 }
        r0 = r20;
        r0.bindInteger(r4, r5);	 Catch:{ Exception -> 0x0062 }
    L_0x0782:
        r4 = 3;
        r5 = r8.intValue();	 Catch:{ Exception -> 0x0062 }
        r5 = r5 + r14;
        r0 = r20;
        r0.bindInteger(r4, r5);	 Catch:{ Exception -> 0x0062 }
        r4 = 4;
        r0 = r20;
        r0.bindLong(r4, r6);	 Catch:{ Exception -> 0x0062 }
        r4 = 5;
        r0 = r20;
        r0.bindInteger(r4, r12);	 Catch:{ Exception -> 0x0062 }
        r4 = 6;
        r0 = r20;
        r0.bindInteger(r4, r11);	 Catch:{ Exception -> 0x0062 }
        r4 = 7;
        r6 = 0;
        r0 = r20;
        r0.bindLong(r4, r6);	 Catch:{ Exception -> 0x0062 }
        r4 = 8;
        r5 = 0;
        r0 = r20;
        r0.bindInteger(r4, r5);	 Catch:{ Exception -> 0x0062 }
        r4 = 9;
        r0 = r20;
        r0.bindInteger(r4, r13);	 Catch:{ Exception -> 0x0062 }
        r4 = 10;
        r5 = 0;
        r0 = r20;
        r0.bindInteger(r4, r5);	 Catch:{ Exception -> 0x0062 }
        r20.step();	 Catch:{ Exception -> 0x0062 }
        goto L_0x06a5;
    L_0x07c3:
        r8 = 0;
        goto L_0x06f7;
    L_0x07c6:
        if (r16 == 0) goto L_0x07d1;
    L_0x07c8:
        r14 = com.hanista.mobogram.messenger.MessagesController.getInstance();	 Catch:{ Exception -> 0x0062 }
        r0 = r16;
        r14.checkChannelInviter(r0);	 Catch:{ Exception -> 0x0062 }
    L_0x07d1:
        r14 = r11;
        r15 = r13;
        r13 = r8;
        r11 = r6;
        r30 = r12;
        r12 = r7;
        r7 = r30;
        goto L_0x0732;
    L_0x07dc:
        r8 = r6.intValue();	 Catch:{ Exception -> 0x0062 }
        r8 = r8 + r14;
        r8 = java.lang.Integer.valueOf(r8);	 Catch:{ Exception -> 0x0062 }
        r0 = r19;
        r0.put(r4, r8);	 Catch:{ Exception -> 0x0062 }
        r8 = r6;
        goto L_0x0745;
    L_0x07ed:
        r6 = (long) r7;	 Catch:{ Exception -> 0x0062 }
        goto L_0x074a;
    L_0x07f0:
        r4 = 2;
        r0 = r20;
        r0.bindInteger(r4, r15);	 Catch:{ Exception -> 0x0062 }
        goto L_0x0782;
    L_0x07f7:
        r20.dispose();	 Catch:{ Exception -> 0x0062 }
        if (r17 == 0) goto L_0x08aa;
    L_0x07fc:
        r0 = r31;
        r4 = r0.database;	 Catch:{ Exception -> 0x0062 }
        r5 = "REPLACE INTO media_counts_v2 VALUES(?, ?, ?)";
        r7 = r4.executeFast(r5);	 Catch:{ Exception -> 0x0062 }
        r4 = r17.entrySet();	 Catch:{ Exception -> 0x0062 }
        r8 = r4.iterator();	 Catch:{ Exception -> 0x0062 }
    L_0x080f:
        r4 = r8.hasNext();	 Catch:{ Exception -> 0x0062 }
        if (r4 == 0) goto L_0x08a7;
    L_0x0815:
        r4 = r8.next();	 Catch:{ Exception -> 0x0062 }
        r4 = (java.util.Map.Entry) r4;	 Catch:{ Exception -> 0x0062 }
        r5 = r4.getKey();	 Catch:{ Exception -> 0x0062 }
        r5 = (java.lang.Integer) r5;	 Catch:{ Exception -> 0x0062 }
        r4 = r4.getValue();	 Catch:{ Exception -> 0x0062 }
        r4 = (java.util.HashMap) r4;	 Catch:{ Exception -> 0x0062 }
        r4 = r4.entrySet();	 Catch:{ Exception -> 0x0062 }
        r11 = r4.iterator();	 Catch:{ Exception -> 0x0062 }
    L_0x082f:
        r4 = r11.hasNext();	 Catch:{ Exception -> 0x0062 }
        if (r4 == 0) goto L_0x080f;
    L_0x0835:
        r4 = r11.next();	 Catch:{ Exception -> 0x0062 }
        r4 = (java.util.Map.Entry) r4;	 Catch:{ Exception -> 0x0062 }
        r6 = r4.getKey();	 Catch:{ Exception -> 0x0062 }
        r6 = (java.lang.Long) r6;	 Catch:{ Exception -> 0x0062 }
        r12 = r6.longValue();	 Catch:{ Exception -> 0x0062 }
        r6 = (int) r12;	 Catch:{ Exception -> 0x0062 }
        r6 = -1;
        r0 = r31;
        r14 = r0.database;	 Catch:{ Exception -> 0x0062 }
        r15 = java.util.Locale.US;	 Catch:{ Exception -> 0x0062 }
        r16 = "SELECT count FROM media_counts_v2 WHERE uid = %d AND type = %d LIMIT 1";
        r17 = 2;
        r0 = r17;
        r0 = new java.lang.Object[r0];	 Catch:{ Exception -> 0x0062 }
        r17 = r0;
        r18 = 0;
        r20 = java.lang.Long.valueOf(r12);	 Catch:{ Exception -> 0x0062 }
        r17[r18] = r20;	 Catch:{ Exception -> 0x0062 }
        r18 = 1;
        r17[r18] = r5;	 Catch:{ Exception -> 0x0062 }
        r15 = java.lang.String.format(r15, r16, r17);	 Catch:{ Exception -> 0x0062 }
        r16 = 0;
        r0 = r16;
        r0 = new java.lang.Object[r0];	 Catch:{ Exception -> 0x0062 }
        r16 = r0;
        r14 = r14.queryFinalized(r15, r16);	 Catch:{ Exception -> 0x0062 }
        r15 = r14.next();	 Catch:{ Exception -> 0x0062 }
        if (r15 == 0) goto L_0x087f;
    L_0x087a:
        r6 = 0;
        r6 = r14.intValue(r6);	 Catch:{ Exception -> 0x0062 }
    L_0x087f:
        r14.dispose();	 Catch:{ Exception -> 0x0062 }
        r14 = -1;
        if (r6 == r14) goto L_0x082f;
    L_0x0885:
        r7.requery();	 Catch:{ Exception -> 0x0062 }
        r4 = r4.getValue();	 Catch:{ Exception -> 0x0062 }
        r4 = (java.lang.Integer) r4;	 Catch:{ Exception -> 0x0062 }
        r4 = r4.intValue();	 Catch:{ Exception -> 0x0062 }
        r4 = r4 + r6;
        r6 = 1;
        r7.bindLong(r6, r12);	 Catch:{ Exception -> 0x0062 }
        r6 = 2;
        r12 = r5.intValue();	 Catch:{ Exception -> 0x0062 }
        r7.bindInteger(r6, r12);	 Catch:{ Exception -> 0x0062 }
        r6 = 3;
        r7.bindInteger(r6, r4);	 Catch:{ Exception -> 0x0062 }
        r7.step();	 Catch:{ Exception -> 0x0062 }
        goto L_0x082f;
    L_0x08a7:
        r7.dispose();	 Catch:{ Exception -> 0x0062 }
    L_0x08aa:
        if (r33 == 0) goto L_0x08b3;
    L_0x08ac:
        r0 = r31;
        r4 = r0.database;	 Catch:{ Exception -> 0x0062 }
        r4.commitTransaction();	 Catch:{ Exception -> 0x0062 }
    L_0x08b3:
        r4 = com.hanista.mobogram.messenger.MessagesController.getInstance();	 Catch:{ Exception -> 0x0062 }
        r0 = r19;
        r4.processDialogsUpdateRead(r0);	 Catch:{ Exception -> 0x0062 }
        if (r10 == 0) goto L_0x08c8;
    L_0x08be:
        r4 = new com.hanista.mobogram.messenger.MessagesStorage$65;	 Catch:{ Exception -> 0x0062 }
        r0 = r31;
        r4.<init>(r10);	 Catch:{ Exception -> 0x0062 }
        com.hanista.mobogram.messenger.AndroidUtilities.runOnUIThread(r4);	 Catch:{ Exception -> 0x0062 }
    L_0x08c8:
        if (r9 == 0) goto L_0x0052;
    L_0x08ca:
        r4 = new com.hanista.mobogram.messenger.MessagesStorage$66;	 Catch:{ Exception -> 0x0062 }
        r0 = r31;
        r4.<init>(r9);	 Catch:{ Exception -> 0x0062 }
        com.hanista.mobogram.messenger.AndroidUtilities.runOnUIThread(r4);	 Catch:{ Exception -> 0x0062 }
        goto L_0x0052;
    L_0x08d6:
        r16 = r6;
        goto L_0x06d2;
    L_0x08da:
        r12 = r8;
        goto L_0x04c8;
    L_0x08dd:
        r12 = r8;
        goto L_0x04ea;
    L_0x08e0:
        r4 = r9;
        r5 = r10;
        goto L_0x05c8;
    L_0x08e4:
        r14 = r6;
        r7 = r5;
        goto L_0x0590;
    L_0x08e8:
        r13 = r5;
        goto L_0x0539;
    L_0x08eb:
        r17 = r6;
        goto L_0x0325;
    L_0x08ef:
        r4 = r5;
        goto L_0x004d;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.hanista.mobogram.messenger.MessagesStorage.putMessagesInternal(java.util.ArrayList, boolean, boolean, int, boolean):void");
    }

    private void putUsersAndChatsInternal(ArrayList<User> arrayList, ArrayList<Chat> arrayList2, boolean z) {
        if (z) {
            try {
                this.database.beginTransaction();
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
                return;
            }
        }
        putUsersInternal(arrayList);
        putChatsInternal(arrayList2);
        if (z) {
            this.database.commitTransaction();
        }
    }

    private void putUsersInternal(ArrayList<User> arrayList) {
        if (arrayList != null && !arrayList.isEmpty()) {
            SQLitePreparedStatement executeFast = this.database.executeFast("REPLACE INTO users VALUES(?, ?, ?, ?)");
            for (int i = 0; i < arrayList.size(); i++) {
                User user = (User) arrayList.get(i);
                if (user.min) {
                    SQLiteCursor queryFinalized = this.database.queryFinalized(String.format(Locale.US, "SELECT data FROM users WHERE uid = %d", new Object[]{Integer.valueOf(user.id)}), new Object[0]);
                    if (queryFinalized.next()) {
                        try {
                            AbstractSerializedData byteBufferValue = queryFinalized.byteBufferValue(0);
                            if (byteBufferValue != null) {
                                User TLdeserialize = User.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                                byteBufferValue.reuse();
                                if (TLdeserialize != null) {
                                    if (user.username != null) {
                                        TLdeserialize.username = user.username;
                                        TLdeserialize.flags |= 8;
                                    } else {
                                        TLdeserialize.username = null;
                                        TLdeserialize.flags &= -9;
                                    }
                                    if (user.photo != null) {
                                        TLdeserialize.photo = user.photo;
                                        TLdeserialize.flags |= 32;
                                    } else {
                                        TLdeserialize.photo = null;
                                        TLdeserialize.flags &= -33;
                                    }
                                    user = TLdeserialize;
                                }
                            }
                        } catch (Throwable e) {
                            FileLog.m18e("tmessages", e);
                        }
                    }
                    queryFinalized.dispose();
                }
                executeFast.requery();
                NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(user.getObjectSize());
                user.serializeToStream(nativeByteBuffer);
                executeFast.bindInteger(1, user.id);
                executeFast.bindString(2, formatUserSearchName(user));
                if (user.status != null) {
                    if (user.status instanceof TL_userStatusRecently) {
                        user.status.expires = -100;
                    } else if (user.status instanceof TL_userStatusLastWeek) {
                        user.status.expires = -101;
                    } else if (user.status instanceof TL_userStatusLastMonth) {
                        user.status.expires = -102;
                    }
                    executeFast.bindInteger(3, user.status.expires);
                } else {
                    executeFast.bindInteger(3, 0);
                }
                executeFast.bindByteBuffer(4, nativeByteBuffer);
                executeFast.step();
                nativeByteBuffer.reuse();
            }
            executeFast.dispose();
        }
    }

    private void updateDialogsWithDeletedMessagesInternal(ArrayList<Integer> arrayList, int i) {
        if (Thread.currentThread().getId() != this.storageQueue.getId()) {
            throw new RuntimeException("wrong db thread");
        }
        try {
            String str;
            if (arrayList.isEmpty()) {
                str = TtmlNode.ANONYMOUS_REGION_ID + (-i);
            } else {
                SQLitePreparedStatement executeFast;
                Object arrayList2 = new ArrayList();
                if (i != 0) {
                    arrayList2.add(Long.valueOf((long) (-i)));
                    executeFast = this.database.executeFast("UPDATE dialogs SET last_mid = (SELECT mid FROM messages WHERE uid = ? AND date = (SELECT MAX(date) FROM messages WHERE uid = ? )) WHERE did = ?");
                } else {
                    str = TextUtils.join(",", arrayList);
                    SQLiteCursor queryFinalized = this.database.queryFinalized(String.format(Locale.US, "SELECT did FROM dialogs WHERE last_mid IN(%s)", new Object[]{str}), new Object[0]);
                    while (queryFinalized.next()) {
                        arrayList2.add(Long.valueOf(queryFinalized.longValue(0)));
                    }
                    queryFinalized.dispose();
                    executeFast = this.database.executeFast("UPDATE dialogs SET unread_count = 0, unread_count_i = 0, last_mid = (SELECT mid FROM messages WHERE uid = ? AND date = (SELECT MAX(date) FROM messages WHERE uid = ? AND date != 0)) WHERE did = ?");
                }
                this.database.beginTransaction();
                for (int i2 = 0; i2 < arrayList2.size(); i2++) {
                    long longValue = ((Long) arrayList2.get(i2)).longValue();
                    executeFast.requery();
                    executeFast.bindLong(1, longValue);
                    executeFast.bindLong(2, longValue);
                    executeFast.bindLong(3, longValue);
                    executeFast.step();
                }
                executeFast.dispose();
                this.database.commitTransaction();
                str = TextUtils.join(",", arrayList2);
            }
            messages_Dialogs com_hanista_mobogram_tgnet_TLRPC_messages_Dialogs = new messages_Dialogs();
            ArrayList arrayList3 = new ArrayList();
            Iterable arrayList4 = new ArrayList();
            Iterable arrayList5 = new ArrayList();
            Iterable arrayList6 = new ArrayList();
            SQLiteCursor queryFinalized2 = this.database.queryFinalized(String.format(Locale.US, "SELECT d.did, d.last_mid, d.unread_count, d.date, m.data, m.read_state, m.mid, m.send_state, m.date, d.pts, d.inbox_max, d.outbox_max FROM dialogs as d LEFT JOIN messages as m ON d.last_mid = m.mid WHERE d.did IN(%s)", new Object[]{str}), new Object[0]);
            while (queryFinalized2.next()) {
                int intValue;
                TL_dialog tL_dialog = new TL_dialog();
                tL_dialog.id = queryFinalized2.longValue(0);
                tL_dialog.top_message = queryFinalized2.intValue(1);
                tL_dialog.read_inbox_max_id = queryFinalized2.intValue(10);
                tL_dialog.read_outbox_max_id = queryFinalized2.intValue(11);
                tL_dialog.unread_count = queryFinalized2.intValue(2);
                tL_dialog.last_message_date = queryFinalized2.intValue(3);
                tL_dialog.pts = queryFinalized2.intValue(9);
                tL_dialog.flags = i == 0 ? 0 : 1;
                com_hanista_mobogram_tgnet_TLRPC_messages_Dialogs.dialogs.add(tL_dialog);
                AbstractSerializedData byteBufferValue = queryFinalized2.byteBufferValue(4);
                if (byteBufferValue != null) {
                    Message TLdeserialize = Message.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                    byteBufferValue.reuse();
                    MessageObject.setUnreadFlags(TLdeserialize, queryFinalized2.intValue(5));
                    TLdeserialize.id = queryFinalized2.intValue(6);
                    TLdeserialize.send_state = queryFinalized2.intValue(7);
                    intValue = queryFinalized2.intValue(8);
                    if (intValue != 0) {
                        tL_dialog.last_message_date = intValue;
                    }
                    TLdeserialize.dialog_id = tL_dialog.id;
                    com_hanista_mobogram_tgnet_TLRPC_messages_Dialogs.messages.add(TLdeserialize);
                    addUsersAndChatsFromMessage(TLdeserialize, arrayList4, arrayList5);
                }
                intValue = (int) tL_dialog.id;
                int i3 = (int) (tL_dialog.id >> 32);
                if (intValue != 0) {
                    if (i3 == 1) {
                        if (!arrayList5.contains(Integer.valueOf(intValue))) {
                            arrayList5.add(Integer.valueOf(intValue));
                        }
                    } else if (intValue > 0) {
                        if (!arrayList4.contains(Integer.valueOf(intValue))) {
                            arrayList4.add(Integer.valueOf(intValue));
                        }
                    } else if (!arrayList5.contains(Integer.valueOf(-intValue))) {
                        arrayList5.add(Integer.valueOf(-intValue));
                    }
                } else if (!arrayList6.contains(Integer.valueOf(i3))) {
                    arrayList6.add(Integer.valueOf(i3));
                }
            }
            queryFinalized2.dispose();
            if (!arrayList6.isEmpty()) {
                getEncryptedChatsInternal(TextUtils.join(",", arrayList6), arrayList3, arrayList4);
            }
            if (!arrayList5.isEmpty()) {
                getChatsInternal(TextUtils.join(",", arrayList5), com_hanista_mobogram_tgnet_TLRPC_messages_Dialogs.chats);
            }
            if (!arrayList4.isEmpty()) {
                getUsersInternal(TextUtils.join(",", arrayList4), com_hanista_mobogram_tgnet_TLRPC_messages_Dialogs.users);
            }
            if (!com_hanista_mobogram_tgnet_TLRPC_messages_Dialogs.dialogs.isEmpty() || !arrayList3.isEmpty()) {
                MessagesController.getInstance().processDialogsUpdate(com_hanista_mobogram_tgnet_TLRPC_messages_Dialogs, arrayList3);
            }
        } catch (Throwable e) {
            FileLog.m18e("tmessages", e);
        }
    }

    private void updateDialogsWithReadMessagesInternal(ArrayList<Integer> arrayList, SparseArray<Long> sparseArray, SparseArray<Long> sparseArray2) {
        int i = 0;
        try {
            HashMap hashMap = new HashMap();
            long longValue;
            if (arrayList == null || arrayList.isEmpty()) {
                int i2;
                SQLitePreparedStatement executeFast;
                if (!(sparseArray == null || sparseArray.size() == 0)) {
                    for (i2 = 0; i2 < sparseArray.size(); i2++) {
                        int keyAt = sparseArray.keyAt(i2);
                        long longValue2 = ((Long) sparseArray.get(keyAt)).longValue();
                        SQLiteCursor queryFinalized = this.database.queryFinalized(String.format(Locale.US, "SELECT COUNT(mid) FROM messages WHERE uid = %d AND mid > %d AND read_state IN(0,2) AND out = 0", new Object[]{Integer.valueOf(keyAt), Long.valueOf(longValue2)}), new Object[0]);
                        if (queryFinalized.next()) {
                            hashMap.put(Long.valueOf((long) keyAt), Integer.valueOf(queryFinalized.intValue(0)));
                        }
                        queryFinalized.dispose();
                        executeFast = this.database.executeFast("UPDATE dialogs SET inbox_max = max((SELECT inbox_max FROM dialogs WHERE did = ?), ?) WHERE did = ?");
                        executeFast.requery();
                        executeFast.bindLong(1, (long) keyAt);
                        executeFast.bindInteger(2, (int) longValue2);
                        executeFast.bindLong(3, (long) keyAt);
                        executeFast.step();
                        executeFast.dispose();
                    }
                }
                if (!(sparseArray2 == null || sparseArray2.size() == 0)) {
                    while (i < sparseArray2.size()) {
                        i2 = sparseArray2.keyAt(i);
                        longValue = ((Long) sparseArray2.get(i2)).longValue();
                        executeFast = this.database.executeFast("UPDATE dialogs SET outbox_max = max((SELECT outbox_max FROM dialogs WHERE did = ?), ?) WHERE did = ?");
                        executeFast.requery();
                        executeFast.bindLong(1, (long) i2);
                        executeFast.bindInteger(2, (int) longValue);
                        executeFast.bindLong(3, (long) i2);
                        executeFast.step();
                        executeFast.dispose();
                        i++;
                    }
                }
            } else {
                String join = TextUtils.join(",", arrayList);
                SQLiteCursor queryFinalized2 = this.database.queryFinalized(String.format(Locale.US, "SELECT uid, read_state, out FROM messages WHERE mid IN(%s)", new Object[]{join}), new Object[0]);
                while (queryFinalized2.next()) {
                    if (queryFinalized2.intValue(2) == 0 && queryFinalized2.intValue(1) == 0) {
                        longValue = queryFinalized2.longValue(0);
                        Integer num = (Integer) hashMap.get(Long.valueOf(longValue));
                        if (num == null) {
                            hashMap.put(Long.valueOf(longValue), Integer.valueOf(1));
                        } else {
                            hashMap.put(Long.valueOf(longValue), Integer.valueOf(num.intValue() + 1));
                        }
                    }
                }
                queryFinalized2.dispose();
            }
            if (!hashMap.isEmpty()) {
                this.database.beginTransaction();
                SQLitePreparedStatement executeFast2 = this.database.executeFast("UPDATE dialogs SET unread_count = ? WHERE did = ?");
                for (Entry entry : hashMap.entrySet()) {
                    executeFast2.requery();
                    executeFast2.bindInteger(1, ((Integer) entry.getValue()).intValue());
                    executeFast2.bindLong(2, ((Long) entry.getKey()).longValue());
                    executeFast2.step();
                }
                executeFast2.dispose();
                this.database.commitTransaction();
            }
            if (!hashMap.isEmpty()) {
                MessagesController.getInstance().processDialogsUpdateRead(hashMap);
            }
        } catch (Throwable e) {
            FileLog.m18e("tmessages", e);
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private long[] updateMessageStateAndIdInternal(long r18, java.lang.Integer r20, int r21, int r22, int r23) {
        /*
        r17 = this;
        r2 = 0;
        r0 = r21;
        r4 = (long) r0;
        if (r20 != 0) goto L_0x0058;
    L_0x0006:
        r0 = r17;
        r3 = r0.database;	 Catch:{ Exception -> 0x003d, all -> 0x004e }
        r6 = java.util.Locale.US;	 Catch:{ Exception -> 0x003d, all -> 0x004e }
        r7 = "SELECT mid FROM randoms WHERE random_id = %d LIMIT 1";
        r8 = 1;
        r8 = new java.lang.Object[r8];	 Catch:{ Exception -> 0x003d, all -> 0x004e }
        r9 = 0;
        r10 = java.lang.Long.valueOf(r18);	 Catch:{ Exception -> 0x003d, all -> 0x004e }
        r8[r9] = r10;	 Catch:{ Exception -> 0x003d, all -> 0x004e }
        r6 = java.lang.String.format(r6, r7, r8);	 Catch:{ Exception -> 0x003d, all -> 0x004e }
        r7 = 0;
        r7 = new java.lang.Object[r7];	 Catch:{ Exception -> 0x003d, all -> 0x004e }
        r3 = r3.queryFinalized(r6, r7);	 Catch:{ Exception -> 0x003d, all -> 0x004e }
        r2 = r3.next();	 Catch:{ Exception -> 0x022a }
        if (r2 == 0) goto L_0x0033;
    L_0x002a:
        r2 = 0;
        r2 = r3.intValue(r2);	 Catch:{ Exception -> 0x022a }
        r20 = java.lang.Integer.valueOf(r2);	 Catch:{ Exception -> 0x022a }
    L_0x0033:
        if (r3 == 0) goto L_0x0230;
    L_0x0035:
        r3.dispose();
        r2 = r3;
    L_0x0039:
        if (r20 != 0) goto L_0x0058;
    L_0x003b:
        r2 = 0;
    L_0x003c:
        return r2;
    L_0x003d:
        r3 = move-exception;
        r15 = r3;
        r3 = r2;
        r2 = r15;
    L_0x0041:
        r6 = "tmessages";
        com.hanista.mobogram.messenger.FileLog.m18e(r6, r2);	 Catch:{ all -> 0x0227 }
        if (r3 == 0) goto L_0x0230;
    L_0x0049:
        r3.dispose();
        r2 = r3;
        goto L_0x0039;
    L_0x004e:
        r3 = move-exception;
        r15 = r3;
        r3 = r2;
        r2 = r15;
    L_0x0052:
        if (r3 == 0) goto L_0x0057;
    L_0x0054:
        r3.dispose();
    L_0x0057:
        throw r2;
    L_0x0058:
        r3 = r20.intValue();
        r6 = (long) r3;
        if (r23 == 0) goto L_0x006d;
    L_0x005f:
        r0 = r23;
        r8 = (long) r0;
        r3 = 32;
        r8 = r8 << r3;
        r6 = r6 | r8;
        r0 = r23;
        r8 = (long) r0;
        r3 = 32;
        r8 = r8 << r3;
        r4 = r4 | r8;
    L_0x006d:
        r8 = 0;
        r0 = r17;
        r3 = r0.database;	 Catch:{ Exception -> 0x00a5, all -> 0x00b5 }
        r10 = java.util.Locale.US;	 Catch:{ Exception -> 0x00a5, all -> 0x00b5 }
        r11 = "SELECT uid FROM messages WHERE mid = %d LIMIT 1";
        r12 = 1;
        r12 = new java.lang.Object[r12];	 Catch:{ Exception -> 0x00a5, all -> 0x00b5 }
        r13 = 0;
        r14 = java.lang.Long.valueOf(r6);	 Catch:{ Exception -> 0x00a5, all -> 0x00b5 }
        r12[r13] = r14;	 Catch:{ Exception -> 0x00a5, all -> 0x00b5 }
        r10 = java.lang.String.format(r10, r11, r12);	 Catch:{ Exception -> 0x00a5, all -> 0x00b5 }
        r11 = 0;
        r11 = new java.lang.Object[r11];	 Catch:{ Exception -> 0x00a5, all -> 0x00b5 }
        r3 = r3.queryFinalized(r10, r11);	 Catch:{ Exception -> 0x00a5, all -> 0x00b5 }
        r2 = r3.next();	 Catch:{ Exception -> 0x0224 }
        if (r2 == 0) goto L_0x0098;
    L_0x0093:
        r2 = 0;
        r8 = r3.longValue(r2);	 Catch:{ Exception -> 0x0224 }
    L_0x0098:
        if (r3 == 0) goto L_0x009d;
    L_0x009a:
        r3.dispose();
    L_0x009d:
        r2 = 0;
        r2 = (r8 > r2 ? 1 : (r8 == r2 ? 0 : -1));
        if (r2 != 0) goto L_0x00bf;
    L_0x00a3:
        r2 = 0;
        goto L_0x003c;
    L_0x00a5:
        r3 = move-exception;
        r15 = r3;
        r3 = r2;
        r2 = r15;
    L_0x00a9:
        r10 = "tmessages";
        com.hanista.mobogram.messenger.FileLog.m18e(r10, r2);	 Catch:{ all -> 0x0221 }
        if (r3 == 0) goto L_0x009d;
    L_0x00b1:
        r3.dispose();
        goto L_0x009d;
    L_0x00b5:
        r3 = move-exception;
        r15 = r3;
        r3 = r2;
        r2 = r15;
    L_0x00b9:
        if (r3 == 0) goto L_0x00be;
    L_0x00bb:
        r3.dispose();
    L_0x00be:
        throw r2;
    L_0x00bf:
        r2 = (r6 > r4 ? 1 : (r6 == r4 ? 0 : -1));
        if (r2 != 0) goto L_0x0105;
    L_0x00c3:
        if (r22 == 0) goto L_0x0105;
    L_0x00c5:
        r3 = 0;
        r0 = r17;
        r2 = r0.database;	 Catch:{ Exception -> 0x00f1 }
        r6 = "UPDATE messages SET send_state = 0, date = ? WHERE mid = ?";
        r3 = r2.executeFast(r6);	 Catch:{ Exception -> 0x00f1 }
        r2 = 1;
        r0 = r22;
        r3.bindInteger(r2, r0);	 Catch:{ Exception -> 0x00f1 }
        r2 = 2;
        r3.bindLong(r2, r4);	 Catch:{ Exception -> 0x00f1 }
        r3.step();	 Catch:{ Exception -> 0x00f1 }
        if (r3 == 0) goto L_0x00e3;
    L_0x00e0:
        r3.dispose();
    L_0x00e3:
        r2 = 2;
        r2 = new long[r2];
        r3 = 0;
        r2[r3] = r8;
        r3 = 1;
        r0 = r21;
        r4 = (long) r0;
        r2[r3] = r4;
        goto L_0x003c;
    L_0x00f1:
        r2 = move-exception;
        r4 = "tmessages";
        com.hanista.mobogram.messenger.FileLog.m18e(r4, r2);	 Catch:{ all -> 0x00fe }
        if (r3 == 0) goto L_0x00e3;
    L_0x00fa:
        r3.dispose();
        goto L_0x00e3;
    L_0x00fe:
        r2 = move-exception;
        if (r3 == 0) goto L_0x0104;
    L_0x0101:
        r3.dispose();
    L_0x0104:
        throw r2;
    L_0x0105:
        r2 = 0;
        r0 = r17;
        r3 = r0.database;	 Catch:{ Exception -> 0x016a, all -> 0x021c }
        r10 = "UPDATE messages SET mid = ?, send_state = 0 WHERE mid = ?";
        r2 = r3.executeFast(r10);	 Catch:{ Exception -> 0x016a, all -> 0x021c }
        r3 = 1;
        r2.bindLong(r3, r4);	 Catch:{ Exception -> 0x016a }
        r3 = 2;
        r2.bindLong(r3, r6);	 Catch:{ Exception -> 0x016a }
        r2.step();	 Catch:{ Exception -> 0x016a }
        if (r2 == 0) goto L_0x0122;
    L_0x011e:
        r2.dispose();
        r2 = 0;
    L_0x0122:
        r0 = r17;
        r3 = r0.database;	 Catch:{ Exception -> 0x01c9 }
        r10 = "UPDATE media_v2 SET mid = ? WHERE mid = ?";
        r2 = r3.executeFast(r10);	 Catch:{ Exception -> 0x01c9 }
        r3 = 1;
        r2.bindLong(r3, r4);	 Catch:{ Exception -> 0x01c9 }
        r3 = 2;
        r2.bindLong(r3, r6);	 Catch:{ Exception -> 0x01c9 }
        r2.step();	 Catch:{ Exception -> 0x01c9 }
        if (r2 == 0) goto L_0x022d;
    L_0x013a:
        r2.dispose();
        r2 = 0;
        r3 = r2;
    L_0x013f:
        r0 = r17;
        r2 = r0.database;	 Catch:{ Exception -> 0x0207 }
        r10 = "UPDATE dialogs SET last_mid = ? WHERE last_mid = ?";
        r3 = r2.executeFast(r10);	 Catch:{ Exception -> 0x0207 }
        r2 = 1;
        r3.bindLong(r2, r4);	 Catch:{ Exception -> 0x0207 }
        r2 = 2;
        r3.bindLong(r2, r6);	 Catch:{ Exception -> 0x0207 }
        r3.step();	 Catch:{ Exception -> 0x0207 }
        if (r3 == 0) goto L_0x015a;
    L_0x0157:
        r3.dispose();
    L_0x015a:
        r2 = 2;
        r2 = new long[r2];
        r3 = 0;
        r2[r3] = r8;
        r3 = 1;
        r4 = r20.intValue();
        r4 = (long) r4;
        r2[r3] = r4;
        goto L_0x003c;
    L_0x016a:
        r3 = move-exception;
        r0 = r17;
        r3 = r0.database;	 Catch:{ Exception -> 0x01b7 }
        r10 = java.util.Locale.US;	 Catch:{ Exception -> 0x01b7 }
        r11 = "DELETE FROM messages WHERE mid = %d";
        r12 = 1;
        r12 = new java.lang.Object[r12];	 Catch:{ Exception -> 0x01b7 }
        r13 = 0;
        r14 = java.lang.Long.valueOf(r6);	 Catch:{ Exception -> 0x01b7 }
        r12[r13] = r14;	 Catch:{ Exception -> 0x01b7 }
        r10 = java.lang.String.format(r10, r11, r12);	 Catch:{ Exception -> 0x01b7 }
        r3 = r3.executeFast(r10);	 Catch:{ Exception -> 0x01b7 }
        r3 = r3.stepThis();	 Catch:{ Exception -> 0x01b7 }
        r3.dispose();	 Catch:{ Exception -> 0x01b7 }
        r0 = r17;
        r3 = r0.database;	 Catch:{ Exception -> 0x01b7 }
        r10 = java.util.Locale.US;	 Catch:{ Exception -> 0x01b7 }
        r11 = "DELETE FROM messages_seq WHERE mid = %d";
        r12 = 1;
        r12 = new java.lang.Object[r12];	 Catch:{ Exception -> 0x01b7 }
        r13 = 0;
        r14 = java.lang.Long.valueOf(r6);	 Catch:{ Exception -> 0x01b7 }
        r12[r13] = r14;	 Catch:{ Exception -> 0x01b7 }
        r10 = java.lang.String.format(r10, r11, r12);	 Catch:{ Exception -> 0x01b7 }
        r3 = r3.executeFast(r10);	 Catch:{ Exception -> 0x01b7 }
        r3 = r3.stepThis();	 Catch:{ Exception -> 0x01b7 }
        r3.dispose();	 Catch:{ Exception -> 0x01b7 }
    L_0x01af:
        if (r2 == 0) goto L_0x0122;
    L_0x01b1:
        r2.dispose();
        r2 = 0;
        goto L_0x0122;
    L_0x01b7:
        r3 = move-exception;
        r10 = "tmessages";
        com.hanista.mobogram.messenger.FileLog.m18e(r10, r3);	 Catch:{ all -> 0x01bf }
        goto L_0x01af;
    L_0x01bf:
        r3 = move-exception;
        r15 = r3;
        r3 = r2;
        r2 = r15;
    L_0x01c3:
        if (r3 == 0) goto L_0x01c8;
    L_0x01c5:
        r3.dispose();
    L_0x01c8:
        throw r2;
    L_0x01c9:
        r3 = move-exception;
        r0 = r17;
        r3 = r0.database;	 Catch:{ Exception -> 0x01f5 }
        r10 = java.util.Locale.US;	 Catch:{ Exception -> 0x01f5 }
        r11 = "DELETE FROM media_v2 WHERE mid = %d";
        r12 = 1;
        r12 = new java.lang.Object[r12];	 Catch:{ Exception -> 0x01f5 }
        r13 = 0;
        r14 = java.lang.Long.valueOf(r6);	 Catch:{ Exception -> 0x01f5 }
        r12[r13] = r14;	 Catch:{ Exception -> 0x01f5 }
        r10 = java.lang.String.format(r10, r11, r12);	 Catch:{ Exception -> 0x01f5 }
        r3 = r3.executeFast(r10);	 Catch:{ Exception -> 0x01f5 }
        r3 = r3.stepThis();	 Catch:{ Exception -> 0x01f5 }
        r3.dispose();	 Catch:{ Exception -> 0x01f5 }
    L_0x01ec:
        if (r2 == 0) goto L_0x022d;
    L_0x01ee:
        r2.dispose();
        r2 = 0;
        r3 = r2;
        goto L_0x013f;
    L_0x01f5:
        r3 = move-exception;
        r10 = "tmessages";
        com.hanista.mobogram.messenger.FileLog.m18e(r10, r3);	 Catch:{ all -> 0x01fd }
        goto L_0x01ec;
    L_0x01fd:
        r3 = move-exception;
        r15 = r3;
        r3 = r2;
        r2 = r15;
        if (r3 == 0) goto L_0x0206;
    L_0x0203:
        r3.dispose();
    L_0x0206:
        throw r2;
    L_0x0207:
        r2 = move-exception;
        r4 = "tmessages";
        com.hanista.mobogram.messenger.FileLog.m18e(r4, r2);	 Catch:{ all -> 0x0215 }
        if (r3 == 0) goto L_0x015a;
    L_0x0210:
        r3.dispose();
        goto L_0x015a;
    L_0x0215:
        r2 = move-exception;
        if (r3 == 0) goto L_0x021b;
    L_0x0218:
        r3.dispose();
    L_0x021b:
        throw r2;
    L_0x021c:
        r3 = move-exception;
        r15 = r3;
        r3 = r2;
        r2 = r15;
        goto L_0x01c3;
    L_0x0221:
        r2 = move-exception;
        goto L_0x00b9;
    L_0x0224:
        r2 = move-exception;
        goto L_0x00a9;
    L_0x0227:
        r2 = move-exception;
        goto L_0x0052;
    L_0x022a:
        r2 = move-exception;
        goto L_0x0041;
    L_0x022d:
        r3 = r2;
        goto L_0x013f;
    L_0x0230:
        r2 = r3;
        goto L_0x0039;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.hanista.mobogram.messenger.MessagesStorage.updateMessageStateAndIdInternal(long, java.lang.Integer, int, int, int):long[]");
    }

    private void updateUsersInternal(ArrayList<User> arrayList, boolean z, boolean z2) {
        if (Thread.currentThread().getId() != this.storageQueue.getId()) {
            throw new RuntimeException("wrong db thread");
        } else if (z) {
            if (z2) {
                try {
                    this.database.beginTransaction();
                } catch (Throwable e) {
                    FileLog.m18e("tmessages", e);
                    return;
                }
            }
            SQLitePreparedStatement executeFast = this.database.executeFast("UPDATE users SET status = ? WHERE uid = ?");
            Iterator it = arrayList.iterator();
            while (it.hasNext()) {
                r0 = (User) it.next();
                executeFast.requery();
                if (r0.status != null) {
                    executeFast.bindInteger(1, r0.status.expires);
                } else {
                    executeFast.bindInteger(1, 0);
                }
                executeFast.bindInteger(2, r0.id);
                executeFast.step();
            }
            executeFast.dispose();
            if (z2) {
                this.database.commitTransaction();
            }
        } else {
            StringBuilder stringBuilder = new StringBuilder();
            HashMap hashMap = new HashMap();
            Iterator it2 = arrayList.iterator();
            while (it2.hasNext()) {
                r0 = (User) it2.next();
                if (stringBuilder.length() != 0) {
                    stringBuilder.append(",");
                }
                stringBuilder.append(r0.id);
                hashMap.put(Integer.valueOf(r0.id), r0);
            }
            ArrayList arrayList2 = new ArrayList();
            getUsersInternal(stringBuilder.toString(), arrayList2);
            Iterator it3 = arrayList2.iterator();
            while (it3.hasNext()) {
                r0 = (User) it3.next();
                User user = (User) hashMap.get(Integer.valueOf(r0.id));
                if (user != null) {
                    if (user.first_name != null && user.last_name != null) {
                        if (!UserObject.isContact(r0)) {
                            r0.first_name = user.first_name;
                            r0.last_name = user.last_name;
                        }
                        r0.username = user.username;
                    } else if (user.photo != null) {
                        r0.photo = user.photo;
                    } else if (user.phone != null) {
                        r0.phone = user.phone;
                    }
                }
            }
            if (!arrayList2.isEmpty()) {
                if (z2) {
                    this.database.beginTransaction();
                }
                putUsersInternal(arrayList2);
                if (z2) {
                    this.database.commitTransaction();
                }
            }
        }
    }

    public void addRecentLocalFile(String str, String str2, Document document) {
        if (str != null && str.length() != 0) {
            if ((str2 != null && str2.length() != 0) || document != null) {
                this.storageQueue.postRunnable(new AnonymousClass13(document, str, str2));
            }
        }
    }

    public void applyPhoneBookUpdates(String str, String str2) {
        if (str.length() != 0 || str2.length() != 0) {
            this.storageQueue.postRunnable(new AnonymousClass39(str, str2));
        }
    }

    public boolean checkMessageId(long j, int i) {
        boolean[] zArr = new boolean[1];
        Semaphore semaphore = new Semaphore(0);
        this.storageQueue.postRunnable(new AnonymousClass44(j, i, zArr, semaphore));
        try {
            semaphore.acquire();
        } catch (Throwable e) {
            FileLog.m18e("tmessages", e);
        }
        return zArr[0];
    }

    public void cleanup(boolean z) {
        this.storageQueue.cleanupQueue();
        this.storageQueue.postRunnable(new C05802(z));
    }

    public void clearDownloadQueue(int i) {
        this.storageQueue.postRunnable(new AnonymousClass60(i));
    }

    public void clearUserPhoto(int i, long j) {
        this.storageQueue.postRunnable(new AnonymousClass24(i, j));
    }

    public void clearUserPhotos(int i) {
        this.storageQueue.postRunnable(new AnonymousClass23(i));
    }

    public void clearWebRecent(int i) {
        this.storageQueue.postRunnable(new AnonymousClass14(i));
    }

    public void closeHolesInMedia(long j, int i, int i2, int i3) {
        SQLiteCursor queryFinalized;
        if (i3 < 0) {
            queryFinalized = this.database.queryFinalized(String.format(Locale.US, "SELECT type, start, end FROM media_holes_v2 WHERE uid = %d AND type >= 0 AND ((end >= %d AND end <= %d) OR (start >= %d AND start <= %d) OR (start >= %d AND end <= %d) OR (start <= %d AND end >= %d))", new Object[]{Long.valueOf(j), Integer.valueOf(i), Integer.valueOf(i2), Integer.valueOf(i), Integer.valueOf(i2), Integer.valueOf(i), Integer.valueOf(i2), Integer.valueOf(i), Integer.valueOf(i2)}), new Object[0]);
        } else {
            queryFinalized = this.database.queryFinalized(String.format(Locale.US, "SELECT type, start, end FROM media_holes_v2 WHERE uid = %d AND type = %d AND ((end >= %d AND end <= %d) OR (start >= %d AND start <= %d) OR (start >= %d AND end <= %d) OR (start <= %d AND end >= %d))", new Object[]{Long.valueOf(j), Integer.valueOf(i3), Integer.valueOf(i), Integer.valueOf(i2), Integer.valueOf(i), Integer.valueOf(i2), Integer.valueOf(i), Integer.valueOf(i2), Integer.valueOf(i), Integer.valueOf(i2)}), new Object[0]);
        }
        ArrayList arrayList = null;
        while (queryFinalized.next()) {
            if (arrayList == null) {
                arrayList = new ArrayList();
            }
            int intValue = queryFinalized.intValue(0);
            int intValue2 = queryFinalized.intValue(1);
            int intValue3 = queryFinalized.intValue(2);
            if (intValue2 != intValue3 || intValue2 != 1) {
                arrayList.add(new Hole(intValue, intValue2, intValue3));
            }
        }
        queryFinalized.dispose();
        if (arrayList != null) {
            for (int i4 = 0; i4 < arrayList.size(); i4++) {
                Hole hole = (Hole) arrayList.get(i4);
                if (i2 >= hole.end - 1 && i <= hole.start + 1) {
                    this.database.executeFast(String.format(Locale.US, "DELETE FROM media_holes_v2 WHERE uid = %d AND type = %d AND start = %d AND end = %d", new Object[]{Long.valueOf(j), Integer.valueOf(hole.type), Integer.valueOf(hole.start), Integer.valueOf(hole.end)})).stepThis().dispose();
                } else if (i2 >= hole.end - 1) {
                    if (hole.end != i) {
                        try {
                            this.database.executeFast(String.format(Locale.US, "UPDATE media_holes_v2 SET end = %d WHERE uid = %d AND type = %d AND start = %d AND end = %d", new Object[]{Integer.valueOf(i), Long.valueOf(j), Integer.valueOf(hole.type), Integer.valueOf(hole.start), Integer.valueOf(hole.end)})).stepThis().dispose();
                        } catch (Throwable e) {
                            try {
                                FileLog.m18e("tmessages", e);
                            } catch (Throwable e2) {
                                FileLog.m18e("tmessages", e2);
                                return;
                            }
                        }
                    }
                    continue;
                } else if (i > hole.start + 1) {
                    this.database.executeFast(String.format(Locale.US, "DELETE FROM media_holes_v2 WHERE uid = %d AND type = %d AND start = %d AND end = %d", new Object[]{Long.valueOf(j), Integer.valueOf(hole.type), Integer.valueOf(hole.start), Integer.valueOf(hole.end)})).stepThis().dispose();
                    SQLitePreparedStatement executeFast = this.database.executeFast("REPLACE INTO media_holes_v2 VALUES(?, ?, ?, ?)");
                    executeFast.requery();
                    executeFast.bindLong(1, j);
                    executeFast.bindInteger(2, hole.type);
                    executeFast.bindInteger(3, hole.start);
                    executeFast.bindInteger(4, i);
                    executeFast.step();
                    executeFast.requery();
                    executeFast.bindLong(1, j);
                    executeFast.bindInteger(2, hole.type);
                    executeFast.bindInteger(3, i2);
                    executeFast.bindInteger(4, hole.end);
                    executeFast.step();
                    executeFast.dispose();
                } else if (hole.start != i2) {
                    try {
                        this.database.executeFast(String.format(Locale.US, "UPDATE media_holes_v2 SET start = %d WHERE uid = %d AND type = %d AND start = %d AND end = %d", new Object[]{Integer.valueOf(i2), Long.valueOf(j), Integer.valueOf(hole.type), Integer.valueOf(hole.start), Integer.valueOf(hole.end)})).stepThis().dispose();
                    } catch (Throwable e22) {
                        FileLog.m18e("tmessages", e22);
                    }
                } else {
                    continue;
                }
            }
        }
    }

    public void commitTransaction(boolean z) {
        if (z) {
            this.storageQueue.postRunnable(new Runnable() {
                public void run() {
                    try {
                        MessagesStorage.this.database.commitTransaction();
                    } catch (Throwable e) {
                        FileLog.m18e("tmessages", e);
                    }
                }
            });
            return;
        }
        try {
            this.database.commitTransaction();
        } catch (Throwable e) {
            FileLog.m18e("tmessages", e);
        }
    }

    public long createPendingTask(NativeByteBuffer nativeByteBuffer) {
        if (nativeByteBuffer == null) {
            return 0;
        }
        long andAdd = this.lastTaskId.getAndAdd(1);
        this.storageQueue.postRunnable(new C05854(andAdd, nativeByteBuffer));
        return andAdd;
    }

    public void createTaskForSecretChat(int i, int i2, int i3, int i4, ArrayList<Long> arrayList) {
        this.storageQueue.postRunnable(new AnonymousClass27(arrayList, i, i4, i2, i3));
    }

    public void deleteBlockedUser(int i) {
        this.storageQueue.postRunnable(new AnonymousClass18(i));
    }

    public void deleteContacts(ArrayList<Integer> arrayList) {
        if (arrayList != null && !arrayList.isEmpty()) {
            this.storageQueue.postRunnable(new AnonymousClass38(arrayList));
        }
    }

    public void deleteDialog(long j, int i) {
        this.storageQueue.postRunnable(new AnonymousClass21(i, j));
    }

    public void deleteUserChannelHistory(int i, int i2) {
        this.storageQueue.postRunnable(new AnonymousClass20(i, i2));
    }

    public void doneHolesInMedia(long j, int i, int i2) {
        int i3 = 0;
        if (i2 == -1) {
            if (i == 0) {
                this.database.executeFast(String.format(Locale.US, "DELETE FROM media_holes_v2 WHERE uid = %d", new Object[]{Long.valueOf(j)})).stepThis().dispose();
            } else {
                this.database.executeFast(String.format(Locale.US, "DELETE FROM media_holes_v2 WHERE uid = %d AND start = 0", new Object[]{Long.valueOf(j)})).stepThis().dispose();
            }
            SQLitePreparedStatement executeFast = this.database.executeFast("REPLACE INTO media_holes_v2 VALUES(?, ?, ?, ?)");
            while (i3 < 5) {
                executeFast.requery();
                executeFast.bindLong(1, j);
                executeFast.bindInteger(2, i3);
                executeFast.bindInteger(3, 1);
                executeFast.bindInteger(4, 1);
                executeFast.step();
                i3++;
            }
            executeFast.dispose();
            return;
        }
        if (i == 0) {
            this.database.executeFast(String.format(Locale.US, "DELETE FROM media_holes_v2 WHERE uid = %d AND type = %d", new Object[]{Long.valueOf(j), Integer.valueOf(i2)})).stepThis().dispose();
        } else {
            this.database.executeFast(String.format(Locale.US, "DELETE FROM media_holes_v2 WHERE uid = %d AND type = %d AND start = 0", new Object[]{Long.valueOf(j), Integer.valueOf(i2)})).stepThis().dispose();
        }
        SQLitePreparedStatement executeFast2 = this.database.executeFast("REPLACE INTO media_holes_v2 VALUES(?, ?, ?, ?)");
        executeFast2.requery();
        executeFast2.bindLong(1, j);
        executeFast2.bindInteger(2, i2);
        executeFast2.bindInteger(3, 1);
        executeFast2.bindInteger(4, 1);
        executeFast2.step();
        executeFast2.dispose();
    }

    public void getBlockedUsers() {
        this.storageQueue.postRunnable(new Runnable() {
            public void run() {
                try {
                    ArrayList arrayList = new ArrayList();
                    ArrayList arrayList2 = new ArrayList();
                    SQLiteCursor queryFinalized = MessagesStorage.this.database.queryFinalized("SELECT * FROM blocked_users WHERE 1", new Object[0]);
                    StringBuilder stringBuilder = new StringBuilder();
                    while (queryFinalized.next()) {
                        int intValue = queryFinalized.intValue(0);
                        arrayList.add(Integer.valueOf(intValue));
                        if (stringBuilder.length() != 0) {
                            stringBuilder.append(",");
                        }
                        stringBuilder.append(intValue);
                    }
                    queryFinalized.dispose();
                    if (stringBuilder.length() != 0) {
                        MessagesStorage.this.getUsersInternal(stringBuilder.toString(), arrayList2);
                    }
                    MessagesController.getInstance().processLoadedBlockedUsers(arrayList, arrayList2, true);
                } catch (Throwable e) {
                    FileLog.m18e("tmessages", e);
                }
            }
        });
    }

    public void getCachedPhoneBook() {
        this.storageQueue.postRunnable(new Runnable() {
            public void run() {
                HashMap hashMap = new HashMap();
                try {
                    SQLiteCursor queryFinalized = MessagesStorage.this.database.queryFinalized("SELECT us.uid, us.fname, us.sname, up.phone, up.sphone, up.deleted FROM user_contacts_v6 as us LEFT JOIN user_phones_v6 as up ON us.uid = up.uid WHERE 1", new Object[0]);
                    while (queryFinalized.next()) {
                        Contact contact;
                        int intValue = queryFinalized.intValue(0);
                        Contact contact2 = (Contact) hashMap.get(Integer.valueOf(intValue));
                        if (contact2 == null) {
                            contact2 = new Contact();
                            contact2.first_name = queryFinalized.stringValue(1);
                            contact2.last_name = queryFinalized.stringValue(2);
                            contact2.id = intValue;
                            hashMap.put(Integer.valueOf(intValue), contact2);
                            contact = contact2;
                        } else {
                            contact = contact2;
                        }
                        String stringValue = queryFinalized.stringValue(3);
                        if (stringValue != null) {
                            contact.phones.add(stringValue);
                            Object stringValue2 = queryFinalized.stringValue(4);
                            if (stringValue2 != null) {
                                if (stringValue2.length() == 8 && stringValue.length() != 8) {
                                    stringValue2 = PhoneFormat.stripExceptNumbers(stringValue);
                                }
                                contact.shortPhones.add(stringValue2);
                                contact.phoneDeleted.add(Integer.valueOf(queryFinalized.intValue(5)));
                                contact.phoneTypes.add(TtmlNode.ANONYMOUS_REGION_ID);
                            }
                        }
                    }
                    queryFinalized.dispose();
                } catch (Throwable e) {
                    hashMap.clear();
                    FileLog.m18e("tmessages", e);
                }
                ContactsController.getInstance().performSyncPhoneBook(hashMap, true, true, false, false);
            }
        });
    }

    public int getChannelPtsSync(int i) {
        Semaphore semaphore = new Semaphore(0);
        Integer[] numArr = new Integer[]{Integer.valueOf(0)};
        getInstance().getStorageQueue().postRunnable(new AnonymousClass81(i, numArr, semaphore));
        try {
            semaphore.acquire();
        } catch (Throwable e) {
            FileLog.m18e("tmessages", e);
        }
        return numArr[0].intValue();
    }

    public Chat getChat(int i) {
        try {
            ArrayList arrayList = new ArrayList();
            getChatsInternal(TtmlNode.ANONYMOUS_REGION_ID + i, arrayList);
            if (!arrayList.isEmpty()) {
                return (Chat) arrayList.get(0);
            }
        } catch (Throwable e) {
            FileLog.m18e("tmessages", e);
        }
        return null;
    }

    public Chat getChatSync(int i) {
        Semaphore semaphore = new Semaphore(0);
        Chat[] chatArr = new Chat[1];
        getInstance().getStorageQueue().postRunnable(new AnonymousClass83(chatArr, i, semaphore));
        try {
            semaphore.acquire();
        } catch (Throwable e) {
            FileLog.m18e("tmessages", e);
        }
        return chatArr[0];
    }

    public void getChatsInternal(String str, ArrayList<Chat> arrayList) {
        if (str != null && str.length() != 0 && arrayList != null) {
            SQLiteCursor queryFinalized = this.database.queryFinalized(String.format(Locale.US, "SELECT data FROM chats WHERE uid IN(%s)", new Object[]{str}), new Object[0]);
            while (queryFinalized.next()) {
                try {
                    AbstractSerializedData byteBufferValue = queryFinalized.byteBufferValue(0);
                    if (byteBufferValue != null) {
                        Chat TLdeserialize = Chat.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                        byteBufferValue.reuse();
                        if (TLdeserialize != null) {
                            arrayList.add(TLdeserialize);
                        }
                    }
                } catch (Throwable e) {
                    FileLog.m18e("tmessages", e);
                }
            }
            queryFinalized.dispose();
        }
    }

    public void getContacts() {
        this.storageQueue.postRunnable(new Runnable() {
            public void run() {
                ArrayList arrayList = new ArrayList();
                ArrayList arrayList2 = new ArrayList();
                try {
                    SQLiteCursor queryFinalized = MessagesStorage.this.database.queryFinalized("SELECT * FROM contacts WHERE 1", new Object[0]);
                    StringBuilder stringBuilder = new StringBuilder();
                    while (queryFinalized.next()) {
                        int intValue = queryFinalized.intValue(0);
                        TL_contact tL_contact = new TL_contact();
                        tL_contact.user_id = intValue;
                        tL_contact.mutual = queryFinalized.intValue(1) == 1;
                        if (stringBuilder.length() != 0) {
                            stringBuilder.append(",");
                        }
                        arrayList.add(tL_contact);
                        stringBuilder.append(tL_contact.user_id);
                    }
                    queryFinalized.dispose();
                    if (stringBuilder.length() != 0) {
                        MessagesStorage.this.getUsersInternal(stringBuilder.toString(), arrayList2);
                    }
                } catch (Throwable e) {
                    arrayList.clear();
                    arrayList2.clear();
                    FileLog.m18e("tmessages", e);
                }
                ContactsController.getInstance().processLoadedContacts(arrayList, arrayList2, 1);
            }
        });
    }

    public SQLiteDatabase getDatabase() {
        return this.database;
    }

    public void getDialogPhotos(int i, int i2, int i3, long j, int i4) {
        this.storageQueue.postRunnable(new AnonymousClass22(j, i, i3, i2, i4));
    }

    public int getDialogReadMax(boolean z, long j) {
        Semaphore semaphore = new Semaphore(0);
        Integer[] numArr = new Integer[]{Integer.valueOf(0)};
        getInstance().getStorageQueue().postRunnable(new AnonymousClass80(z, j, numArr, semaphore));
        try {
            semaphore.acquire();
        } catch (Throwable e) {
            FileLog.m18e("tmessages", e);
        }
        return numArr[0].intValue();
    }

    public void getDialogs(int i, int i2) {
        this.storageQueue.postRunnable(new AnonymousClass78(i, i2));
    }

    public void getDownloadQueue(int i) {
        this.storageQueue.postRunnable(new AnonymousClass61(i));
    }

    public EncryptedChat getEncryptedChat(int i) {
        try {
            ArrayList arrayList = new ArrayList();
            getEncryptedChatsInternal(TtmlNode.ANONYMOUS_REGION_ID + i, arrayList, null);
            if (!arrayList.isEmpty()) {
                return (EncryptedChat) arrayList.get(0);
            }
        } catch (Throwable e) {
            FileLog.m18e("tmessages", e);
        }
        return null;
    }

    public void getEncryptedChat(int i, Semaphore semaphore, ArrayList<TLObject> arrayList) {
        if (semaphore != null && arrayList != null) {
            this.storageQueue.postRunnable(new AnonymousClass56(i, arrayList, semaphore));
        }
    }

    public void getEncryptedChatsInternal(String str, ArrayList<EncryptedChat> arrayList, ArrayList<Integer> arrayList2) {
        if (str != null && str.length() != 0 && arrayList != null) {
            SQLiteCursor queryFinalized = this.database.queryFinalized(String.format(Locale.US, "SELECT data, user, g, authkey, ttl, layer, seq_in, seq_out, use_count, exchange_id, key_date, fprint, fauthkey, khash, in_seq_no FROM enc_chats WHERE uid IN(%s)", new Object[]{str}), new Object[0]);
            while (queryFinalized.next()) {
                try {
                    AbstractSerializedData byteBufferValue = queryFinalized.byteBufferValue(0);
                    if (byteBufferValue != null) {
                        EncryptedChat TLdeserialize = EncryptedChat.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                        byteBufferValue.reuse();
                        if (TLdeserialize != null) {
                            TLdeserialize.user_id = queryFinalized.intValue(1);
                            if (!(arrayList2 == null || arrayList2.contains(Integer.valueOf(TLdeserialize.user_id)))) {
                                arrayList2.add(Integer.valueOf(TLdeserialize.user_id));
                            }
                            TLdeserialize.a_or_b = queryFinalized.byteArrayValue(2);
                            TLdeserialize.auth_key = queryFinalized.byteArrayValue(3);
                            TLdeserialize.ttl = queryFinalized.intValue(4);
                            TLdeserialize.layer = queryFinalized.intValue(5);
                            TLdeserialize.seq_in = queryFinalized.intValue(6);
                            TLdeserialize.seq_out = queryFinalized.intValue(7);
                            int intValue = queryFinalized.intValue(8);
                            TLdeserialize.key_use_count_in = (short) (intValue >> 16);
                            TLdeserialize.key_use_count_out = (short) intValue;
                            TLdeserialize.exchange_id = queryFinalized.longValue(9);
                            TLdeserialize.key_create_date = queryFinalized.intValue(10);
                            TLdeserialize.future_key_fingerprint = queryFinalized.longValue(11);
                            TLdeserialize.future_auth_key = queryFinalized.byteArrayValue(12);
                            TLdeserialize.key_hash = queryFinalized.byteArrayValue(13);
                            TLdeserialize.in_seq_no = queryFinalized.intValue(14);
                            arrayList.add(TLdeserialize);
                        }
                    }
                } catch (Throwable e) {
                    FileLog.m18e("tmessages", e);
                }
            }
            queryFinalized.dispose();
        }
    }

    public void getMessages(long j, int i, int i2, int i3, int i4, int i5, boolean z, int i6) {
        this.storageQueue.postRunnable(new AnonymousClass45(i, i2, z, j, i5, i3, i4, i6));
    }

    public void getNewTask(ArrayList<Integer> arrayList) {
        this.storageQueue.postRunnable(new AnonymousClass26(arrayList));
    }

    public TLObject getSentFile(String str, int i) {
        if (str == null) {
            return null;
        }
        Semaphore semaphore = new Semaphore(0);
        ArrayList arrayList = new ArrayList();
        this.storageQueue.postRunnable(new AnonymousClass48(str, i, arrayList, semaphore));
        try {
            semaphore.acquire();
        } catch (Throwable e) {
            FileLog.m18e("tmessages", e);
        }
        return !arrayList.isEmpty() ? (TLObject) arrayList.get(0) : null;
    }

    public DispatchQueue getStorageQueue() {
        return this.storageQueue;
    }

    public void getUnsentMessages(int i) {
        this.storageQueue.postRunnable(new AnonymousClass43(i));
    }

    public User getUser(int i) {
        try {
            ArrayList arrayList = new ArrayList();
            getUsersInternal(TtmlNode.ANONYMOUS_REGION_ID + i, arrayList);
            if (!arrayList.isEmpty()) {
                return (User) arrayList.get(0);
            }
        } catch (Throwable e) {
            FileLog.m18e("tmessages", e);
        }
        return null;
    }

    public User getUserSync(int i) {
        Semaphore semaphore = new Semaphore(0);
        User[] userArr = new User[1];
        getInstance().getStorageQueue().postRunnable(new AnonymousClass82(userArr, i, semaphore));
        try {
            semaphore.acquire();
        } catch (Throwable e) {
            FileLog.m18e("tmessages", e);
        }
        return userArr[0];
    }

    public ArrayList<User> getUsers(ArrayList<Integer> arrayList) {
        ArrayList<User> arrayList2 = new ArrayList();
        try {
            getUsersInternal(TextUtils.join(",", arrayList), arrayList2);
        } catch (Throwable e) {
            arrayList2.clear();
            FileLog.m18e("tmessages", e);
        }
        return arrayList2;
    }

    public void getUsersInternal(String str, ArrayList<User> arrayList) {
        if (str != null && str.length() != 0 && arrayList != null) {
            SQLiteCursor queryFinalized = this.database.queryFinalized(String.format(Locale.US, "SELECT data, status FROM users WHERE uid IN(%s)", new Object[]{str}), new Object[0]);
            while (queryFinalized.next()) {
                try {
                    AbstractSerializedData byteBufferValue = queryFinalized.byteBufferValue(0);
                    if (byteBufferValue != null) {
                        User TLdeserialize = User.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                        byteBufferValue.reuse();
                        if (TLdeserialize != null) {
                            if (TLdeserialize.status != null) {
                                TLdeserialize.status.expires = queryFinalized.intValue(1);
                            }
                            arrayList.add(TLdeserialize);
                        }
                    }
                } catch (Throwable e) {
                    FileLog.m18e("tmessages", e);
                }
            }
            queryFinalized.dispose();
        }
    }

    public void getWallpapers() {
        this.storageQueue.postRunnable(new Runnable() {

            /* renamed from: com.hanista.mobogram.messenger.MessagesStorage.16.1 */
            class C05711 implements Runnable {
                final /* synthetic */ ArrayList val$wallPapers;

                C05711(ArrayList arrayList) {
                    this.val$wallPapers = arrayList;
                }

                public void run() {
                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.wallpapersDidLoaded, this.val$wallPapers);
                }
            }

            public void run() {
                try {
                    SQLiteCursor queryFinalized = MessagesStorage.this.database.queryFinalized("SELECT data FROM wallpapers WHERE 1", new Object[0]);
                    ArrayList arrayList = new ArrayList();
                    while (queryFinalized.next()) {
                        AbstractSerializedData byteBufferValue = queryFinalized.byteBufferValue(0);
                        if (byteBufferValue != null) {
                            WallPaper TLdeserialize = WallPaper.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                            byteBufferValue.reuse();
                            arrayList.add(TLdeserialize);
                        }
                    }
                    queryFinalized.dispose();
                    AndroidUtilities.runOnUIThread(new C05711(arrayList));
                } catch (Throwable e) {
                    FileLog.m18e("tmessages", e);
                }
            }
        });
    }

    public boolean hasAuthMessage(int i) {
        Semaphore semaphore = new Semaphore(0);
        boolean[] zArr = new boolean[1];
        this.storageQueue.postRunnable(new AnonymousClass55(i, zArr, semaphore));
        try {
            semaphore.acquire();
        } catch (Throwable e) {
            FileLog.m18e("tmessages", e);
        }
        return zArr[0];
    }

    public boolean isDialogHasMessages(long j) {
        Semaphore semaphore = new Semaphore(0);
        boolean[] zArr = new boolean[1];
        this.storageQueue.postRunnable(new AnonymousClass54(j, zArr, semaphore));
        try {
            semaphore.acquire();
        } catch (Throwable e) {
            FileLog.m18e("tmessages", e);
        }
        return zArr[0];
    }

    public boolean isMigratedChat(int i) {
        Semaphore semaphore = new Semaphore(0);
        boolean[] zArr = new boolean[1];
        this.storageQueue.postRunnable(new AnonymousClass34(i, zArr, semaphore));
        try {
            semaphore.acquire();
        } catch (Throwable e) {
            FileLog.m18e("tmessages", e);
        }
        return zArr[0];
    }

    public void loadChatInfo(int i, Semaphore semaphore, boolean z, boolean z2) {
        this.storageQueue.postRunnable(new AnonymousClass35(i, semaphore, z, z2));
    }

    public void loadUnreadMessages() {
        this.storageQueue.postRunnable(new Runnable() {

            /* renamed from: com.hanista.mobogram.messenger.MessagesStorage.10.1 */
            class C05691 implements Runnable {
                final /* synthetic */ ArrayList val$chats;
                final /* synthetic */ ArrayList val$encryptedChats;
                final /* synthetic */ ArrayList val$messages;
                final /* synthetic */ HashMap val$pushDialogs;
                final /* synthetic */ ArrayList val$users;

                C05691(HashMap hashMap, ArrayList arrayList, ArrayList arrayList2, ArrayList arrayList3, ArrayList arrayList4) {
                    this.val$pushDialogs = hashMap;
                    this.val$messages = arrayList;
                    this.val$users = arrayList2;
                    this.val$chats = arrayList3;
                    this.val$encryptedChats = arrayList4;
                }

                public void run() {
                    NotificationsController.getInstance().processLoadedUnreadMessages(this.val$pushDialogs, this.val$messages, this.val$users, this.val$chats, this.val$encryptedChats);
                }
            }

            public void run() {
                try {
                    Iterable arrayList = new ArrayList();
                    Iterable arrayList2 = new ArrayList();
                    Iterable arrayList3 = new ArrayList();
                    HashMap hashMap = new HashMap();
                    SQLiteCursor queryFinalized = MessagesStorage.this.database.queryFinalized("SELECT d.did, d.unread_count, s.flags FROM dialogs as d LEFT JOIN dialog_settings as s ON d.did = s.did WHERE d.unread_count != 0", new Object[0]);
                    StringBuilder stringBuilder = new StringBuilder();
                    while (queryFinalized.next()) {
                        if (queryFinalized.isNull(2) || queryFinalized.intValue(2) != 1) {
                            long longValue = queryFinalized.longValue(0);
                            hashMap.put(Long.valueOf(longValue), Integer.valueOf(queryFinalized.intValue(1)));
                            if (stringBuilder.length() != 0) {
                                stringBuilder.append(",");
                            }
                            stringBuilder.append(longValue);
                            int i = (int) longValue;
                            int i2 = (int) (longValue >> 32);
                            if (i != 0) {
                                if (i < 0) {
                                    if (!arrayList2.contains(Integer.valueOf(-i))) {
                                        arrayList2.add(Integer.valueOf(-i));
                                    }
                                } else if (!arrayList.contains(Integer.valueOf(i))) {
                                    arrayList.add(Integer.valueOf(i));
                                }
                            } else if (!arrayList3.contains(Integer.valueOf(i2))) {
                                arrayList3.add(Integer.valueOf(i2));
                            }
                        }
                    }
                    queryFinalized.dispose();
                    Iterable arrayList4 = new ArrayList();
                    HashMap hashMap2 = new HashMap();
                    Object arrayList5 = new ArrayList();
                    ArrayList arrayList6 = new ArrayList();
                    ArrayList arrayList7 = new ArrayList();
                    ArrayList arrayList8 = new ArrayList();
                    if (stringBuilder.length() > 0) {
                        AbstractSerializedData byteBufferValue;
                        Message TLdeserialize;
                        int i3;
                        ArrayList arrayList9;
                        int i4;
                        SQLiteCursor queryFinalized2 = MessagesStorage.this.database.queryFinalized("SELECT read_state, data, send_state, mid, date, uid, replydata FROM messages WHERE uid IN (" + stringBuilder.toString() + ") AND out = 0 AND read_state IN(0,2) ORDER BY date DESC LIMIT 50", new Object[0]);
                        while (queryFinalized2.next()) {
                            byteBufferValue = queryFinalized2.byteBufferValue(1);
                            if (byteBufferValue != null) {
                                TLdeserialize = Message.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                                byteBufferValue.reuse();
                                MessageObject.setUnreadFlags(TLdeserialize, queryFinalized2.intValue(0));
                                TLdeserialize.id = queryFinalized2.intValue(3);
                                TLdeserialize.date = queryFinalized2.intValue(4);
                                TLdeserialize.dialog_id = queryFinalized2.longValue(5);
                                arrayList5.add(TLdeserialize);
                                i3 = (int) TLdeserialize.dialog_id;
                                MessagesStorage.addUsersAndChatsFromMessage(TLdeserialize, arrayList, arrayList2);
                                TLdeserialize.send_state = queryFinalized2.intValue(2);
                                if (!(TLdeserialize.to_id.channel_id != 0 || MessageObject.isUnread(TLdeserialize) || i3 == 0) || TLdeserialize.id > 0) {
                                    TLdeserialize.send_state = 0;
                                }
                                if (i3 == 0 && !queryFinalized2.isNull(5)) {
                                    TLdeserialize.random_id = queryFinalized2.longValue(5);
                                }
                                try {
                                    if (TLdeserialize.reply_to_msg_id != 0 && ((TLdeserialize.action instanceof TL_messageActionPinMessage) || (TLdeserialize.action instanceof TL_messageActionGameScore))) {
                                        if (!queryFinalized2.isNull(6)) {
                                            byteBufferValue = queryFinalized2.byteBufferValue(6);
                                            if (byteBufferValue != null) {
                                                TLdeserialize.replyMessage = Message.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                                                byteBufferValue.reuse();
                                                if (TLdeserialize.replyMessage != null) {
                                                    MessagesStorage.addUsersAndChatsFromMessage(TLdeserialize.replyMessage, arrayList, arrayList2);
                                                }
                                            }
                                        }
                                        if (TLdeserialize.replyMessage == null) {
                                            long j = (long) TLdeserialize.reply_to_msg_id;
                                            if (TLdeserialize.to_id.channel_id != 0) {
                                                j |= ((long) TLdeserialize.to_id.channel_id) << 32;
                                            }
                                            if (!arrayList4.contains(Long.valueOf(j))) {
                                                arrayList4.add(Long.valueOf(j));
                                            }
                                            arrayList9 = (ArrayList) hashMap2.get(Integer.valueOf(TLdeserialize.reply_to_msg_id));
                                            if (arrayList9 == null) {
                                                arrayList9 = new ArrayList();
                                                hashMap2.put(Integer.valueOf(TLdeserialize.reply_to_msg_id), arrayList9);
                                            }
                                            arrayList9.add(TLdeserialize);
                                        }
                                    }
                                } catch (Throwable e) {
                                    FileLog.m18e("tmessages", e);
                                }
                            }
                        }
                        queryFinalized2.dispose();
                        if (!arrayList4.isEmpty()) {
                            SQLiteDatabase access$000 = MessagesStorage.this.database;
                            Object[] objArr = new Object[1];
                            objArr[0] = TextUtils.join(",", arrayList4);
                            queryFinalized2 = access$000.queryFinalized(String.format(Locale.US, "SELECT data, mid, date, uid FROM messages WHERE mid IN(%s)", objArr), new Object[0]);
                            while (queryFinalized2.next()) {
                                byteBufferValue = queryFinalized2.byteBufferValue(0);
                                if (byteBufferValue != null) {
                                    TLdeserialize = Message.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                                    byteBufferValue.reuse();
                                    TLdeserialize.id = queryFinalized2.intValue(1);
                                    TLdeserialize.date = queryFinalized2.intValue(2);
                                    TLdeserialize.dialog_id = queryFinalized2.longValue(3);
                                    MessagesStorage.addUsersAndChatsFromMessage(TLdeserialize, arrayList, arrayList2);
                                    arrayList9 = (ArrayList) hashMap2.get(Integer.valueOf(TLdeserialize.id));
                                    if (arrayList9 != null) {
                                        for (i4 = 0; i4 < arrayList9.size(); i4++) {
                                            ((Message) arrayList9.get(i4)).replyMessage = TLdeserialize;
                                        }
                                    }
                                }
                            }
                            queryFinalized2.dispose();
                        }
                        if (!arrayList3.isEmpty()) {
                            MessagesStorage.this.getEncryptedChatsInternal(TextUtils.join(",", arrayList3), arrayList8, arrayList);
                        }
                        if (!arrayList.isEmpty()) {
                            MessagesStorage.this.getUsersInternal(TextUtils.join(",", arrayList), arrayList6);
                        }
                        if (!arrayList2.isEmpty()) {
                            MessagesStorage.this.getChatsInternal(TextUtils.join(",", arrayList2), arrayList7);
                            int i5 = 0;
                            while (i5 < arrayList7.size()) {
                                Chat chat = (Chat) arrayList7.get(i5);
                                if (chat == null || (!chat.left && chat.migrated_to == null)) {
                                    i3 = i5;
                                } else {
                                    MessagesStorage.this.database.executeFast("UPDATE dialogs SET unread_count = 0, unread_count_i = 0 WHERE did = " + ((long) (-chat.id))).stepThis().dispose();
                                    MessagesStorage.this.database.executeFast(String.format(Locale.US, "UPDATE messages SET read_state = 3 WHERE uid = %d AND mid > 0 AND read_state IN(0,2) AND out = 0", new Object[]{Long.valueOf(r10)})).stepThis().dispose();
                                    arrayList7.remove(i5);
                                    int i6 = i5 - 1;
                                    hashMap.remove(Long.valueOf((long) (-chat.id)));
                                    i4 = 0;
                                    while (i4 < arrayList5.size()) {
                                        if (((Message) arrayList5.get(i4)).dialog_id == ((long) (-chat.id))) {
                                            arrayList5.remove(i4);
                                            i5 = i4 - 1;
                                        } else {
                                            i5 = i4;
                                        }
                                        i4 = i5 + 1;
                                    }
                                    i3 = i6;
                                }
                                i5 = i3 + 1;
                            }
                        }
                    }
                    Collections.reverse(arrayList5);
                    AndroidUtilities.runOnUIThread(new C05691(hashMap, arrayList5, arrayList6, arrayList7, arrayList8));
                } catch (Throwable e2) {
                    FileLog.m18e("tmessages", e2);
                }
            }
        });
    }

    public void loadWebRecent(int i) {
        this.storageQueue.postRunnable(new AnonymousClass12(i));
    }

    public void markMessageAsSendError(Message message) {
        this.storageQueue.postRunnable(new AnonymousClass68(message));
    }

    public void markMessagesAsDeleted(ArrayList<Integer> arrayList, boolean z, int i, boolean z2) {
        if (!arrayList.isEmpty()) {
            if (z) {
                this.storageQueue.postRunnable(new AnonymousClass76(arrayList, i, z2));
            } else {
                markMessagesAsDeletedInternal(arrayList, i, z2);
            }
        }
    }

    public void markMessagesAsDeletedByRandoms(ArrayList<Long> arrayList) {
        if (!arrayList.isEmpty()) {
            this.storageQueue.postRunnable(new AnonymousClass74(arrayList));
        }
    }

    public void markMessagesAsRead(SparseArray<Long> sparseArray, SparseArray<Long> sparseArray2, HashMap<Integer, Integer> hashMap, boolean z) {
        if (z) {
            this.storageQueue.postRunnable(new AnonymousClass73(sparseArray, sparseArray2, hashMap));
        } else {
            markMessagesAsReadInternal(sparseArray, sparseArray2, hashMap);
        }
    }

    public void markMessagesContentAsRead(ArrayList<Long> arrayList) {
        if (arrayList != null && !arrayList.isEmpty()) {
            this.storageQueue.postRunnable(new AnonymousClass72(arrayList));
        }
    }

    public void openDatabase() {
        Object obj = null;
        this.cacheFile = new File(ApplicationLoader.getFilesDirFixed(), "cache4.db");
        if (!this.cacheFile.exists()) {
            obj = 1;
        }
        try {
            this.database = new SQLiteDatabase(this.cacheFile.getPath());
            this.database.executeFast("PRAGMA secure_delete = ON").stepThis().dispose();
            this.database.executeFast("PRAGMA temp_store = 1").stepThis().dispose();
            if (obj != null) {
                this.database.executeFast("CREATE TABLE messages_holes(uid INTEGER, start INTEGER, end INTEGER, PRIMARY KEY(uid, start));").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS uid_end_messages_holes ON messages_holes(uid, end);").stepThis().dispose();
                this.database.executeFast("CREATE TABLE media_holes_v2(uid INTEGER, type INTEGER, start INTEGER, end INTEGER, PRIMARY KEY(uid, type, start));").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS uid_end_media_holes_v2 ON media_holes_v2(uid, type, end);").stepThis().dispose();
                this.database.executeFast("CREATE TABLE messages(mid INTEGER PRIMARY KEY, uid INTEGER, read_state INTEGER, send_state INTEGER, date INTEGER, data BLOB, out INTEGER, ttl INTEGER, media INTEGER, replydata BLOB, imp INTEGER)").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS uid_mid_idx_messages ON messages(uid, mid);").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS uid_date_mid_idx_messages ON messages(uid, date, mid);").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS mid_out_idx_messages ON messages(mid, out);").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS task_idx_messages ON messages(uid, out, read_state, ttl, date, send_state);").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS send_state_idx_messages ON messages(mid, send_state, date) WHERE mid < 0 AND send_state = 1;").stepThis().dispose();
                this.database.executeFast("CREATE TABLE download_queue(uid INTEGER, type INTEGER, date INTEGER, data BLOB, PRIMARY KEY (uid, type));").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS type_date_idx_download_queue ON download_queue(type, date);").stepThis().dispose();
                this.database.executeFast("CREATE TABLE user_phones_v6(uid INTEGER, phone TEXT, sphone TEXT, deleted INTEGER, PRIMARY KEY (uid, phone))").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS sphone_deleted_idx_user_phones ON user_phones_v6(sphone, deleted);").stepThis().dispose();
                this.database.executeFast("CREATE TABLE dialogs(did INTEGER PRIMARY KEY, date INTEGER, unread_count INTEGER, last_mid INTEGER, inbox_max INTEGER, outbox_max INTEGER, last_mid_i INTEGER, unread_count_i INTEGER, pts INTEGER, date_i INTEGER)").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS date_idx_dialogs ON dialogs(date);").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS last_mid_idx_dialogs ON dialogs(last_mid);").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS unread_count_idx_dialogs ON dialogs(unread_count);").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS last_mid_i_idx_dialogs ON dialogs(last_mid_i);").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS unread_count_i_idx_dialogs ON dialogs(unread_count_i);").stepThis().dispose();
                this.database.executeFast("CREATE TABLE randoms(random_id INTEGER, mid INTEGER, PRIMARY KEY (random_id, mid))").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS mid_idx_randoms ON randoms(mid);").stepThis().dispose();
                this.database.executeFast("CREATE TABLE enc_tasks_v2(mid INTEGER PRIMARY KEY, date INTEGER)").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS date_idx_enc_tasks_v2 ON enc_tasks_v2(date);").stepThis().dispose();
                this.database.executeFast("CREATE TABLE messages_seq(mid INTEGER PRIMARY KEY, seq_in INTEGER, seq_out INTEGER);").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS seq_idx_messages_seq ON messages_seq(seq_in, seq_out);").stepThis().dispose();
                this.database.executeFast("CREATE TABLE params(id INTEGER PRIMARY KEY, seq INTEGER, pts INTEGER, date INTEGER, qts INTEGER, lsv INTEGER, sg INTEGER, pbytes BLOB)").stepThis().dispose();
                this.database.executeFast("INSERT INTO params VALUES(1, 0, 0, 0, 0, 0, 0, NULL)").stepThis().dispose();
                this.database.executeFast("CREATE TABLE media_v2(mid INTEGER PRIMARY KEY, uid INTEGER, date INTEGER, type INTEGER, data BLOB)").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS uid_mid_type_date_idx_media ON media_v2(uid, mid, type, date);").stepThis().dispose();
                this.database.executeFast("CREATE TABLE bot_keyboard(uid INTEGER PRIMARY KEY, mid INTEGER, info BLOB)").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS bot_keyboard_idx_mid ON bot_keyboard(mid);").stepThis().dispose();
                this.database.executeFast("CREATE TABLE chat_settings_v2(uid INTEGER PRIMARY KEY, info BLOB, pinned INTEGER)").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS chat_settings_pinned_idx ON chat_settings_v2(uid, pinned) WHERE pinned != 0;").stepThis().dispose();
                this.database.executeFast("CREATE TABLE chat_pinned(uid INTEGER PRIMARY KEY, pinned INTEGER, data BLOB)").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS chat_pinned_mid_idx ON chat_pinned(uid, pinned) WHERE pinned != 0;").stepThis().dispose();
                this.database.executeFast("CREATE TABLE chat_hints(did INTEGER, type INTEGER, rating REAL, date INTEGER, PRIMARY KEY(did, type))").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS chat_hints_rating_idx ON chat_hints(rating);").stepThis().dispose();
                this.database.executeFast("CREATE TABLE users_data(uid INTEGER PRIMARY KEY, about TEXT)").stepThis().dispose();
                this.database.executeFast("CREATE TABLE users(uid INTEGER PRIMARY KEY, name TEXT, status INTEGER, data BLOB)").stepThis().dispose();
                this.database.executeFast("CREATE TABLE chats(uid INTEGER PRIMARY KEY, name TEXT, data BLOB)").stepThis().dispose();
                this.database.executeFast("CREATE TABLE enc_chats(uid INTEGER PRIMARY KEY, user INTEGER, name TEXT, data BLOB, g BLOB, authkey BLOB, ttl INTEGER, layer INTEGER, seq_in INTEGER, seq_out INTEGER, use_count INTEGER, exchange_id INTEGER, key_date INTEGER, fprint INTEGER, fauthkey BLOB, khash BLOB, in_seq_no INTEGER)").stepThis().dispose();
                this.database.executeFast("CREATE TABLE channel_users_v2(did INTEGER, uid INTEGER, date INTEGER, data BLOB, PRIMARY KEY(did, uid))").stepThis().dispose();
                this.database.executeFast("CREATE TABLE contacts(uid INTEGER PRIMARY KEY, mutual INTEGER)").stepThis().dispose();
                this.database.executeFast("CREATE TABLE wallpapers(uid INTEGER PRIMARY KEY, data BLOB)").stepThis().dispose();
                this.database.executeFast("CREATE TABLE user_photos(uid INTEGER, id INTEGER, data BLOB, PRIMARY KEY (uid, id))").stepThis().dispose();
                this.database.executeFast("CREATE TABLE blocked_users(uid INTEGER PRIMARY KEY)").stepThis().dispose();
                this.database.executeFast("CREATE TABLE dialog_settings(did INTEGER PRIMARY KEY, flags INTEGER);").stepThis().dispose();
                this.database.executeFast("CREATE TABLE web_recent_v3(id TEXT, type INTEGER, image_url TEXT, thumb_url TEXT, local_url TEXT, width INTEGER, height INTEGER, size INTEGER, date INTEGER, document BLOB, PRIMARY KEY (id, type));").stepThis().dispose();
                this.database.executeFast("CREATE TABLE stickers_v2(id INTEGER PRIMARY KEY, data BLOB, date INTEGER, hash TEXT);").stepThis().dispose();
                this.database.executeFast("CREATE TABLE stickers_featured(id INTEGER PRIMARY KEY, data BLOB, unread BLOB, date INTEGER, hash TEXT);").stepThis().dispose();
                this.database.executeFast("CREATE TABLE hashtag_recent_v2(id TEXT PRIMARY KEY, date INTEGER);").stepThis().dispose();
                this.database.executeFast("CREATE TABLE webpage_pending(id INTEGER, mid INTEGER, PRIMARY KEY (id, mid));").stepThis().dispose();
                this.database.executeFast("CREATE TABLE user_contacts_v6(uid INTEGER PRIMARY KEY, fname TEXT, sname TEXT)").stepThis().dispose();
                this.database.executeFast("CREATE TABLE sent_files_v2(uid TEXT, type INTEGER, data BLOB, PRIMARY KEY (uid, type))").stepThis().dispose();
                this.database.executeFast("CREATE TABLE search_recent(did INTEGER PRIMARY KEY, date INTEGER);").stepThis().dispose();
                this.database.executeFast("CREATE TABLE media_counts_v2(uid INTEGER, type INTEGER, count INTEGER, PRIMARY KEY(uid, type))").stepThis().dispose();
                this.database.executeFast("CREATE TABLE keyvalue(id TEXT PRIMARY KEY, value TEXT)").stepThis().dispose();
                this.database.executeFast("CREATE TABLE bot_info(uid INTEGER PRIMARY KEY, info BLOB)").stepThis().dispose();
                this.database.executeFast("CREATE TABLE pending_tasks(id INTEGER PRIMARY KEY, data BLOB);").stepThis().dispose();
                this.database.executeFast("CREATE TABLE requested_holes(uid INTEGER, seq_out_start INTEGER, seq_out_end INTEGER, PRIMARY KEY (uid, seq_out_start, seq_out_end));").stepThis().dispose();
                this.database.executeFast("PRAGMA user_version = 37").stepThis().dispose();
            } else {
                try {
                    SQLiteCursor queryFinalized = this.database.queryFinalized("SELECT seq, pts, date, qts, lsv, sg, pbytes FROM params WHERE id = 1", new Object[0]);
                    if (queryFinalized.next()) {
                        lastSeqValue = queryFinalized.intValue(0);
                        lastPtsValue = queryFinalized.intValue(1);
                        lastDateValue = queryFinalized.intValue(2);
                        lastQtsValue = queryFinalized.intValue(3);
                        lastSecretVersion = queryFinalized.intValue(4);
                        secretG = queryFinalized.intValue(5);
                        if (queryFinalized.isNull(6)) {
                            secretPBytes = null;
                        } else {
                            secretPBytes = queryFinalized.byteArrayValue(6);
                            if (secretPBytes != null && secretPBytes.length == 1) {
                                secretPBytes = null;
                            }
                        }
                    }
                    queryFinalized.dispose();
                } catch (Throwable e) {
                    FileLog.m18e("tmessages", e);
                    try {
                        this.database.executeFast("CREATE TABLE IF NOT EXISTS params(id INTEGER PRIMARY KEY, seq INTEGER, pts INTEGER, date INTEGER, qts INTEGER, lsv INTEGER, sg INTEGER, pbytes BLOB)").stepThis().dispose();
                        this.database.executeFast("INSERT INTO params VALUES(1, 0, 0, 0, 0, 0, 0, NULL)").stepThis().dispose();
                    } catch (Throwable e2) {
                        FileLog.m18e("tmessages", e2);
                    }
                }
                int intValue = this.database.executeInt("PRAGMA user_version", new Object[0]).intValue();
                if (intValue < 37) {
                    updateDbToLastVersion(intValue);
                }
            }
        } catch (Throwable e22) {
            FileLog.m18e("tmessages", e22);
        }
        loadUnreadMessages();
        loadPendingTasks();
    }

    public void overwriteChannel(int i, TL_updates_channelDifferenceTooLong tL_updates_channelDifferenceTooLong, int i2) {
        this.storageQueue.postRunnable(new AnonymousClass63(i, i2, tL_updates_channelDifferenceTooLong));
    }

    public void processPendingRead(long j, long j2, int i) {
        this.storageQueue.postRunnable(new AnonymousClass36(j, j2, i));
    }

    public void putBlockedUsers(ArrayList<Integer> arrayList, boolean z) {
        if (arrayList != null && !arrayList.isEmpty()) {
            this.storageQueue.postRunnable(new AnonymousClass19(z, arrayList));
        }
    }

    public void putCachedPhoneBook(HashMap<Integer, Contact> hashMap) {
        this.storageQueue.postRunnable(new AnonymousClass40(hashMap));
    }

    public void putChannelViews(SparseArray<SparseIntArray> sparseArray, boolean z) {
        this.storageQueue.postRunnable(new AnonymousClass64(sparseArray, z));
    }

    public void putContacts(ArrayList<TL_contact> arrayList, boolean z) {
        if (!arrayList.isEmpty()) {
            this.storageQueue.postRunnable(new AnonymousClass37(z, new ArrayList(arrayList)));
        }
    }

    public void putDialogPhotos(int i, photos_Photos com_hanista_mobogram_tgnet_TLRPC_photos_Photos) {
        if (com_hanista_mobogram_tgnet_TLRPC_photos_Photos != null && !com_hanista_mobogram_tgnet_TLRPC_photos_Photos.photos.isEmpty()) {
            this.storageQueue.postRunnable(new AnonymousClass25(com_hanista_mobogram_tgnet_TLRPC_photos_Photos, i));
        }
    }

    public void putDialogs(messages_Dialogs com_hanista_mobogram_tgnet_TLRPC_messages_Dialogs) {
        if (!com_hanista_mobogram_tgnet_TLRPC_messages_Dialogs.dialogs.isEmpty()) {
            this.storageQueue.postRunnable(new AnonymousClass79(com_hanista_mobogram_tgnet_TLRPC_messages_Dialogs));
        }
    }

    public void putEncryptedChat(EncryptedChat encryptedChat, User user, TL_dialog tL_dialog) {
        if (encryptedChat != null) {
            this.storageQueue.postRunnable(new AnonymousClass57(encryptedChat, user, tL_dialog));
        }
    }

    public void putMessages(messages_Messages com_hanista_mobogram_tgnet_TLRPC_messages_Messages, long j, int i, int i2, boolean z) {
        this.storageQueue.postRunnable(new AnonymousClass77(com_hanista_mobogram_tgnet_TLRPC_messages_Messages, i, j, i2, z));
    }

    public void putMessages(ArrayList<Message> arrayList, boolean z, boolean z2, boolean z3, int i) {
        putMessages(arrayList, z, z2, z3, i, false);
    }

    public void putMessages(ArrayList<Message> arrayList, boolean z, boolean z2, boolean z3, int i, boolean z4) {
        if (arrayList.size() != 0) {
            if (z2) {
                this.storageQueue.postRunnable(new AnonymousClass67(arrayList, z, z3, i, z4));
            } else {
                putMessagesInternal(arrayList, z, z3, i, z4);
            }
        }
    }

    public void putSentFile(String str, TLObject tLObject, int i) {
        if (str != null && tLObject != null) {
            this.storageQueue.postRunnable(new AnonymousClass49(str, tLObject, i));
        }
    }

    public void putUsersAndChats(ArrayList<User> arrayList, ArrayList<Chat> arrayList2, boolean z, boolean z2) {
        if (arrayList != null && arrayList.isEmpty() && arrayList2 != null && arrayList2.isEmpty()) {
            return;
        }
        if (z2) {
            this.storageQueue.postRunnable(new AnonymousClass58(arrayList, arrayList2, z));
        } else {
            putUsersAndChatsInternal(arrayList, arrayList2, z);
        }
    }

    public void putWallpapers(ArrayList<WallPaper> arrayList) {
        this.storageQueue.postRunnable(new AnonymousClass11(arrayList));
    }

    public void putWebPages(HashMap<Long, WebPage> hashMap) {
        if (hashMap != null && !hashMap.isEmpty()) {
            this.storageQueue.postRunnable(new AnonymousClass62(hashMap));
        }
    }

    public void putWebRecent(ArrayList<SearchImage> arrayList) {
        this.storageQueue.postRunnable(new AnonymousClass15(arrayList));
    }

    public void removeFromDownloadQueue(long j, int i, boolean z) {
        this.storageQueue.postRunnable(new AnonymousClass59(z, i, j));
    }

    public void removePendingTask(long j) {
        this.storageQueue.postRunnable(new C05865(j));
    }

    public void saveChannelPts(int i, int i2) {
        this.storageQueue.postRunnable(new C05957(i2, i));
    }

    public void saveDiffParams(int i, int i2, int i3, int i4) {
        this.storageQueue.postRunnable(new C05968(i, i2, i3, i4));
    }

    public void saveSecretParams(int i, int i2, byte[] bArr) {
        this.storageQueue.postRunnable(new C05833(i, i2, bArr));
    }

    public void setDialogFlags(long j, long j2) {
        this.storageQueue.postRunnable(new C05979(j, j2));
    }

    public void setMessageSeq(int i, int i2, int i3) {
        this.storageQueue.postRunnable(new AnonymousClass69(i, i2, i3));
    }

    public void startTransaction(boolean z) {
        if (z) {
            this.storageQueue.postRunnable(new Runnable() {
                public void run() {
                    try {
                        MessagesStorage.this.database.beginTransaction();
                    } catch (Throwable e) {
                        FileLog.m18e("tmessages", e);
                    }
                }
            });
            return;
        }
        try {
            this.database.beginTransaction();
        } catch (Throwable e) {
            FileLog.m18e("tmessages", e);
        }
    }

    public void updateChannelPinnedMessage(int i, int i2) {
        this.storageQueue.postRunnable(new AnonymousClass32(i, i2));
    }

    public void updateChannelUsers(int i, ArrayList<ChannelParticipant> arrayList) {
        this.storageQueue.postRunnable(new AnonymousClass30(i, arrayList));
    }

    public void updateChatInfo(int i, int i2, int i3, int i4, int i5) {
        this.storageQueue.postRunnable(new AnonymousClass33(i, i3, i2, i4, i5));
    }

    public void updateChatInfo(ChatFull chatFull, boolean z) {
        this.storageQueue.postRunnable(new AnonymousClass31(z, chatFull));
    }

    public void updateChatParticipants(ChatParticipants chatParticipants) {
        if (chatParticipants != null) {
            this.storageQueue.postRunnable(new AnonymousClass29(chatParticipants));
        }
    }

    public void updateDbToLastVersion(int i) {
        this.storageQueue.postRunnable(new C05721(i));
    }

    public void updateDialogsWithDeletedMessages(ArrayList<Integer> arrayList, boolean z, int i) {
        if (!arrayList.isEmpty() || i != 0) {
            if (z) {
                this.storageQueue.postRunnable(new AnonymousClass75(arrayList, i));
            } else {
                updateDialogsWithDeletedMessagesInternal(arrayList, i);
            }
        }
    }

    public void updateDialogsWithReadMessages(SparseArray<Long> sparseArray, SparseArray<Long> sparseArray2, boolean z) {
        if (sparseArray.size() != 0) {
            if (z) {
                this.storageQueue.postRunnable(new AnonymousClass28(sparseArray, sparseArray2));
            } else {
                updateDialogsWithReadMessagesInternal(null, sparseArray, sparseArray2);
            }
        }
    }

    public void updateEncryptedChat(EncryptedChat encryptedChat) {
        if (encryptedChat != null) {
            this.storageQueue.postRunnable(new AnonymousClass53(encryptedChat));
        }
    }

    public void updateEncryptedChatLayer(EncryptedChat encryptedChat) {
        if (encryptedChat != null) {
            this.storageQueue.postRunnable(new AnonymousClass52(encryptedChat));
        }
    }

    public void updateEncryptedChatSeq(EncryptedChat encryptedChat) {
        if (encryptedChat != null) {
            this.storageQueue.postRunnable(new AnonymousClass50(encryptedChat));
        }
    }

    public void updateEncryptedChatTTL(EncryptedChat encryptedChat) {
        if (encryptedChat != null) {
            this.storageQueue.postRunnable(new AnonymousClass51(encryptedChat));
        }
    }

    public long[] updateMessageStateAndId(long j, Integer num, int i, int i2, boolean z, int i3) {
        if (!z) {
            return updateMessageStateAndIdInternal(j, num, i, i2, i3);
        }
        this.storageQueue.postRunnable(new AnonymousClass70(j, num, i, i2, i3));
        return null;
    }

    public void updateUsers(ArrayList<User> arrayList, boolean z, boolean z2, boolean z3) {
        if (!arrayList.isEmpty()) {
            if (z3) {
                this.storageQueue.postRunnable(new AnonymousClass71(arrayList, z, z2));
            } else {
                updateUsersInternal(arrayList, z, z2);
            }
        }
    }
}
