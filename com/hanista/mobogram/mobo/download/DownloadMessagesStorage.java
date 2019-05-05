package com.hanista.mobogram.mobo.download;

import android.text.TextUtils;
import com.hanista.mobogram.SQLite.SQLiteCursor;
import com.hanista.mobogram.SQLite.SQLiteDatabase;
import com.hanista.mobogram.SQLite.SQLitePreparedStatement;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.ApplicationLoader;
import com.hanista.mobogram.messenger.DispatchQueue;
import com.hanista.mobogram.messenger.FileLoader;
import com.hanista.mobogram.messenger.FileLog;
import com.hanista.mobogram.messenger.MessageObject;
import com.hanista.mobogram.messenger.MessagesController;
import com.hanista.mobogram.messenger.NotificationsController;
import com.hanista.mobogram.messenger.Utilities;
import com.hanista.mobogram.messenger.query.BotQuery;
import com.hanista.mobogram.messenger.query.MessagesQuery;
import com.hanista.mobogram.messenger.query.SharedMediaQuery;
import com.hanista.mobogram.tgnet.AbstractSerializedData;
import com.hanista.mobogram.tgnet.ConnectionsManager;
import com.hanista.mobogram.tgnet.NativeByteBuffer;
import com.hanista.mobogram.tgnet.TLRPC;
import com.hanista.mobogram.tgnet.TLRPC.BotInfo;
import com.hanista.mobogram.tgnet.TLRPC.ChannelParticipant;
import com.hanista.mobogram.tgnet.TLRPC.Chat;
import com.hanista.mobogram.tgnet.TLRPC.ChatFull;
import com.hanista.mobogram.tgnet.TLRPC.ChatParticipant;
import com.hanista.mobogram.tgnet.TLRPC.ChatParticipants;
import com.hanista.mobogram.tgnet.TLRPC.EncryptedChat;
import com.hanista.mobogram.tgnet.TLRPC.Message;
import com.hanista.mobogram.tgnet.TLRPC.PhotoSize;
import com.hanista.mobogram.tgnet.TLRPC.TL_channelFull;
import com.hanista.mobogram.tgnet.TLRPC.TL_chat;
import com.hanista.mobogram.tgnet.TLRPC.TL_chatChannelParticipant;
import com.hanista.mobogram.tgnet.TLRPC.TL_chatFull;
import com.hanista.mobogram.tgnet.TLRPC.TL_chatInviteEmpty;
import com.hanista.mobogram.tgnet.TLRPC.TL_chatParticipants;
import com.hanista.mobogram.tgnet.TLRPC.TL_chatPhotoEmpty;
import com.hanista.mobogram.tgnet.TLRPC.TL_dialog;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageMediaDocument;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageMediaPhoto;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageMediaUnsupported;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageMediaUnsupported_old;
import com.hanista.mobogram.tgnet.TLRPC.TL_messages_messages;
import com.hanista.mobogram.tgnet.TLRPC.TL_peerNotifySettingsEmpty;
import com.hanista.mobogram.tgnet.TLRPC.TL_photoEmpty;
import com.hanista.mobogram.tgnet.TLRPC.TL_userStatusLastMonth;
import com.hanista.mobogram.tgnet.TLRPC.TL_userStatusLastWeek;
import com.hanista.mobogram.tgnet.TLRPC.TL_userStatusRecently;
import com.hanista.mobogram.tgnet.TLRPC.User;
import com.hanista.mobogram.tgnet.TLRPC.messages_Dialogs;
import com.hanista.mobogram.tgnet.TLRPC.messages_Messages;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.concurrent.Semaphore;

/* renamed from: com.hanista.mobogram.mobo.download.c */
public class DownloadMessagesStorage {
    public static int f838a;
    public static int f839b;
    public static int f840c;
    public static int f841d;
    public static int f842e;
    public static byte[] f843f;
    public static int f844g;
    private static volatile DownloadMessagesStorage f845k;
    private DispatchQueue f846h;
    private SQLiteDatabase f847i;
    private File f848j;

    /* renamed from: com.hanista.mobogram.mobo.download.c.1 */
    class DownloadMessagesStorage implements Runnable {
        final /* synthetic */ int f789a;
        final /* synthetic */ DownloadMessagesStorage f790b;

        /* renamed from: com.hanista.mobogram.mobo.download.c.1.1 */
        class DownloadMessagesStorage implements Runnable {
            final /* synthetic */ DownloadMessagesStorage f788a;

            DownloadMessagesStorage(DownloadMessagesStorage downloadMessagesStorage) {
                this.f788a = downloadMessagesStorage;
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
                    this.f788a.f790b.f847i.beginTransaction();
                    SQLitePreparedStatement executeFast = this.f788a.f790b.f847i.executeFast("REPLACE INTO dialog_settings VALUES(?, ?)");
                    Iterator it = arrayList.iterator();
                    while (it.hasNext()) {
                        Integer num = (Integer) it.next();
                        executeFast.requery();
                        executeFast.bindLong(1, (long) num.intValue());
                        executeFast.bindInteger(2, 1);
                        executeFast.step();
                    }
                    executeFast.dispose();
                    this.f788a.f790b.f847i.commitTransaction();
                } catch (Throwable e2) {
                    FileLog.m18e("tmessages", e2);
                }
            }
        }

        DownloadMessagesStorage(DownloadMessagesStorage downloadMessagesStorage, int i) {
            this.f790b = downloadMessagesStorage;
            this.f789a = i;
        }

        public void run() {
            try {
                int i = this.f789a;
                if (i < 4) {
                    this.f790b.f847i.executeFast("CREATE TABLE IF NOT EXISTS user_photos(uid INTEGER, id INTEGER, data BLOB, PRIMARY KEY (uid, id))").stepThis().dispose();
                    this.f790b.f847i.executeFast("DROP INDEX IF EXISTS read_state_out_idx_messages;").stepThis().dispose();
                    this.f790b.f847i.executeFast("DROP INDEX IF EXISTS ttl_idx_messages;").stepThis().dispose();
                    this.f790b.f847i.executeFast("DROP INDEX IF EXISTS date_idx_messages;").stepThis().dispose();
                    this.f790b.f847i.executeFast("CREATE INDEX IF NOT EXISTS mid_out_idx_messages ON messages(mid, out);").stepThis().dispose();
                    this.f790b.f847i.executeFast("CREATE INDEX IF NOT EXISTS task_idx_messages ON messages(uid, out, read_state, ttl, date, send_state);").stepThis().dispose();
                    this.f790b.f847i.executeFast("CREATE INDEX IF NOT EXISTS uid_date_mid_idx_messages ON messages(uid, date, mid);").stepThis().dispose();
                    this.f790b.f847i.executeFast("CREATE TABLE IF NOT EXISTS user_contacts_v6(uid INTEGER PRIMARY KEY, fname TEXT, sname TEXT)").stepThis().dispose();
                    this.f790b.f847i.executeFast("CREATE TABLE IF NOT EXISTS user_phones_v6(uid INTEGER, phone TEXT, sphone TEXT, deleted INTEGER, PRIMARY KEY (uid, phone))").stepThis().dispose();
                    this.f790b.f847i.executeFast("CREATE INDEX IF NOT EXISTS sphone_deleted_idx_user_phones ON user_phones_v6(sphone, deleted);").stepThis().dispose();
                    this.f790b.f847i.executeFast("CREATE INDEX IF NOT EXISTS mid_idx_randoms ON randoms(mid);").stepThis().dispose();
                    this.f790b.f847i.executeFast("CREATE TABLE IF NOT EXISTS sent_files_v2(uid TEXT, type INTEGER, data BLOB, PRIMARY KEY (uid, type))").stepThis().dispose();
                    this.f790b.f847i.executeFast("CREATE TABLE IF NOT EXISTS blocked_users(uid INTEGER PRIMARY KEY)").stepThis().dispose();
                    this.f790b.f847i.executeFast("CREATE TABLE IF NOT EXISTS download_queue(uid INTEGER, type INTEGER, date INTEGER, data BLOB, PRIMARY KEY (uid, type));").stepThis().dispose();
                    this.f790b.f847i.executeFast("CREATE INDEX IF NOT EXISTS type_date_idx_download_queue ON download_queue(type, date);").stepThis().dispose();
                    this.f790b.f847i.executeFast("CREATE TABLE IF NOT EXISTS dialog_settings(did INTEGER PRIMARY KEY, flags INTEGER);").stepThis().dispose();
                    this.f790b.f847i.executeFast("CREATE INDEX IF NOT EXISTS send_state_idx_messages ON messages(mid, send_state, date) WHERE mid < 0 AND send_state = 1;").stepThis().dispose();
                    this.f790b.f847i.executeFast("CREATE INDEX IF NOT EXISTS unread_count_idx_dialogs ON dialogs(unread_count);").stepThis().dispose();
                    this.f790b.f847i.executeFast("UPDATE messages SET send_state = 2 WHERE mid < 0 AND send_state = 1").stepThis().dispose();
                    this.f790b.f846h.postRunnable(new DownloadMessagesStorage(this));
                    this.f790b.f847i.executeFast("PRAGMA user_version = 4").stepThis().dispose();
                    i = 4;
                }
                if (i == 4) {
                    this.f790b.f847i.executeFast("CREATE TABLE IF NOT EXISTS enc_tasks_v2(mid INTEGER PRIMARY KEY, date INTEGER)").stepThis().dispose();
                    this.f790b.f847i.executeFast("CREATE INDEX IF NOT EXISTS date_idx_enc_tasks_v2 ON enc_tasks_v2(date);").stepThis().dispose();
                    this.f790b.f847i.beginTransaction();
                    SQLiteCursor queryFinalized = this.f790b.f847i.queryFinalized("SELECT date, data FROM enc_tasks WHERE 1", new Object[0]);
                    SQLitePreparedStatement executeFast = this.f790b.f847i.executeFast("REPLACE INTO enc_tasks_v2 VALUES(?, ?)");
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
                    this.f790b.f847i.commitTransaction();
                    this.f790b.f847i.executeFast("DROP INDEX IF EXISTS date_idx_enc_tasks;").stepThis().dispose();
                    this.f790b.f847i.executeFast("DROP TABLE IF EXISTS enc_tasks;").stepThis().dispose();
                    this.f790b.f847i.executeFast("ALTER TABLE messages ADD COLUMN media INTEGER default 0").stepThis().dispose();
                    this.f790b.f847i.executeFast("PRAGMA user_version = 6").stepThis().dispose();
                    i = 6;
                }
                if (i == 6) {
                    this.f790b.f847i.executeFast("CREATE TABLE IF NOT EXISTS messages_seq(mid INTEGER PRIMARY KEY, seq_in INTEGER, seq_out INTEGER);").stepThis().dispose();
                    this.f790b.f847i.executeFast("CREATE INDEX IF NOT EXISTS seq_idx_messages_seq ON messages_seq(seq_in, seq_out);").stepThis().dispose();
                    this.f790b.f847i.executeFast("ALTER TABLE enc_chats ADD COLUMN layer INTEGER default 0").stepThis().dispose();
                    this.f790b.f847i.executeFast("ALTER TABLE enc_chats ADD COLUMN seq_in INTEGER default 0").stepThis().dispose();
                    this.f790b.f847i.executeFast("ALTER TABLE enc_chats ADD COLUMN seq_out INTEGER default 0").stepThis().dispose();
                    this.f790b.f847i.executeFast("PRAGMA user_version = 7").stepThis().dispose();
                    i = 7;
                }
                if (i == 7 || i == 8 || i == 9) {
                    this.f790b.f847i.executeFast("ALTER TABLE enc_chats ADD COLUMN use_count INTEGER default 0").stepThis().dispose();
                    this.f790b.f847i.executeFast("ALTER TABLE enc_chats ADD COLUMN exchange_id INTEGER default 0").stepThis().dispose();
                    this.f790b.f847i.executeFast("ALTER TABLE enc_chats ADD COLUMN key_date INTEGER default 0").stepThis().dispose();
                    this.f790b.f847i.executeFast("ALTER TABLE enc_chats ADD COLUMN fprint INTEGER default 0").stepThis().dispose();
                    this.f790b.f847i.executeFast("ALTER TABLE enc_chats ADD COLUMN fauthkey BLOB default NULL").stepThis().dispose();
                    this.f790b.f847i.executeFast("ALTER TABLE enc_chats ADD COLUMN khash BLOB default NULL").stepThis().dispose();
                    this.f790b.f847i.executeFast("PRAGMA user_version = 10").stepThis().dispose();
                    i = 10;
                }
                if (i == 10) {
                    this.f790b.f847i.executeFast("CREATE TABLE IF NOT EXISTS web_recent_v3(id TEXT, type INTEGER, image_url TEXT, thumb_url TEXT, local_url TEXT, width INTEGER, height INTEGER, size INTEGER, date INTEGER, PRIMARY KEY (id, type));").stepThis().dispose();
                    this.f790b.f847i.executeFast("PRAGMA user_version = 11").stepThis().dispose();
                    i = 11;
                }
                if (i == 11) {
                    i = 12;
                }
                if (i == 12) {
                    this.f790b.f847i.executeFast("DROP INDEX IF EXISTS uid_mid_idx_media;").stepThis().dispose();
                    this.f790b.f847i.executeFast("DROP INDEX IF EXISTS mid_idx_media;").stepThis().dispose();
                    this.f790b.f847i.executeFast("DROP INDEX IF EXISTS uid_date_mid_idx_media;").stepThis().dispose();
                    this.f790b.f847i.executeFast("DROP TABLE IF EXISTS media;").stepThis().dispose();
                    this.f790b.f847i.executeFast("DROP TABLE IF EXISTS media_counts;").stepThis().dispose();
                    this.f790b.f847i.executeFast("CREATE TABLE IF NOT EXISTS media_v2(mid INTEGER PRIMARY KEY, uid INTEGER, date INTEGER, type INTEGER, data BLOB)").stepThis().dispose();
                    this.f790b.f847i.executeFast("CREATE TABLE IF NOT EXISTS media_counts_v2(uid INTEGER, type INTEGER, count INTEGER, PRIMARY KEY(uid, type))").stepThis().dispose();
                    this.f790b.f847i.executeFast("CREATE INDEX IF NOT EXISTS uid_mid_type_date_idx_media ON media_v2(uid, mid, type, date);").stepThis().dispose();
                    this.f790b.f847i.executeFast("CREATE TABLE IF NOT EXISTS keyvalue(id TEXT PRIMARY KEY, value TEXT)").stepThis().dispose();
                    this.f790b.f847i.executeFast("PRAGMA user_version = 13").stepThis().dispose();
                    i = 13;
                }
                if (i == 13) {
                    this.f790b.f847i.executeFast("ALTER TABLE messages ADD COLUMN replydata BLOB default NULL").stepThis().dispose();
                    this.f790b.f847i.executeFast("PRAGMA user_version = 14").stepThis().dispose();
                    i = 14;
                }
                if (i == 14) {
                    this.f790b.f847i.executeFast("CREATE TABLE IF NOT EXISTS hashtag_recent_v2(id TEXT PRIMARY KEY, date INTEGER);").stepThis().dispose();
                    this.f790b.f847i.executeFast("PRAGMA user_version = 15").stepThis().dispose();
                    i = 15;
                }
                if (i == 15) {
                    this.f790b.f847i.executeFast("CREATE TABLE IF NOT EXISTS webpage_pending(id INTEGER, mid INTEGER, PRIMARY KEY (id, mid));").stepThis().dispose();
                    this.f790b.f847i.executeFast("PRAGMA user_version = 16").stepThis().dispose();
                    i = 16;
                }
                if (i == 16) {
                    this.f790b.f847i.executeFast("ALTER TABLE dialogs ADD COLUMN inbox_max INTEGER default 0").stepThis().dispose();
                    this.f790b.f847i.executeFast("ALTER TABLE dialogs ADD COLUMN outbox_max INTEGER default 0").stepThis().dispose();
                    this.f790b.f847i.executeFast("PRAGMA user_version = 17").stepThis().dispose();
                    i = 17;
                }
                if (i == 17) {
                    this.f790b.f847i.executeFast("CREATE TABLE bot_info(uid INTEGER PRIMARY KEY, info BLOB)").stepThis().dispose();
                    this.f790b.f847i.executeFast("PRAGMA user_version = 18").stepThis().dispose();
                    i = 18;
                }
                if (i == 18) {
                    this.f790b.f847i.executeFast("DROP TABLE IF EXISTS stickers;").stepThis().dispose();
                    this.f790b.f847i.executeFast("CREATE TABLE IF NOT EXISTS stickers_v2(id INTEGER PRIMARY KEY, data BLOB, date INTEGER, hash TEXT);").stepThis().dispose();
                    this.f790b.f847i.executeFast("PRAGMA user_version = 19").stepThis().dispose();
                    i = 19;
                }
                if (i == 19) {
                    this.f790b.f847i.executeFast("CREATE TABLE IF NOT EXISTS bot_keyboard(uid INTEGER PRIMARY KEY, mid INTEGER, info BLOB)").stepThis().dispose();
                    this.f790b.f847i.executeFast("CREATE INDEX IF NOT EXISTS bot_keyboard_idx_mid ON bot_keyboard(mid);").stepThis().dispose();
                    this.f790b.f847i.executeFast("PRAGMA user_version = 20").stepThis().dispose();
                    i = 20;
                }
                if (i == 20) {
                    this.f790b.f847i.executeFast("CREATE TABLE search_recent(did INTEGER PRIMARY KEY, date INTEGER);").stepThis().dispose();
                    this.f790b.f847i.executeFast("PRAGMA user_version = 21").stepThis().dispose();
                    i = 21;
                }
                if (i == 21) {
                    this.f790b.f847i.executeFast("CREATE TABLE IF NOT EXISTS chat_settings_v2(uid INTEGER PRIMARY KEY, info BLOB)").stepThis().dispose();
                    SQLiteCursor queryFinalized2 = this.f790b.f847i.queryFinalized("SELECT uid, participants FROM chat_settings WHERE uid < 0", new Object[0]);
                    SQLitePreparedStatement executeFast2 = this.f790b.f847i.executeFast("REPLACE INTO chat_settings_v2 VALUES(?, ?)");
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
                    this.f790b.f847i.executeFast("DROP TABLE IF EXISTS chat_settings;").stepThis().dispose();
                    this.f790b.f847i.executeFast("ALTER TABLE dialogs ADD COLUMN last_mid_i INTEGER default 0").stepThis().dispose();
                    this.f790b.f847i.executeFast("ALTER TABLE dialogs ADD COLUMN unread_count_i INTEGER default 0").stepThis().dispose();
                    this.f790b.f847i.executeFast("ALTER TABLE dialogs ADD COLUMN pts INTEGER default 0").stepThis().dispose();
                    this.f790b.f847i.executeFast("ALTER TABLE dialogs ADD COLUMN date_i INTEGER default 0").stepThis().dispose();
                    this.f790b.f847i.executeFast("CREATE INDEX IF NOT EXISTS last_mid_i_idx_dialogs ON dialogs(last_mid_i);").stepThis().dispose();
                    this.f790b.f847i.executeFast("CREATE INDEX IF NOT EXISTS unread_count_i_idx_dialogs ON dialogs(unread_count_i);").stepThis().dispose();
                    this.f790b.f847i.executeFast("ALTER TABLE messages ADD COLUMN imp INTEGER default 0").stepThis().dispose();
                    this.f790b.f847i.executeFast("CREATE INDEX IF NOT EXISTS uid_mid_idx_imp_messages ON messages(uid, mid, imp) WHERE imp = 1;").stepThis().dispose();
                    this.f790b.f847i.executeFast("CREATE INDEX IF NOT EXISTS uid_date_mid_imp_idx_messages ON messages(uid, date, mid, imp) WHERE imp = 1;").stepThis().dispose();
                    this.f790b.f847i.executeFast("CREATE TABLE IF NOT EXISTS channel_group(uid INTEGER, start INTEGER, end INTEGER, count INTEGER, PRIMARY KEY(uid, start));").stepThis().dispose();
                    this.f790b.f847i.executeFast("CREATE TABLE IF NOT EXISTS messages_holes(uid INTEGER, start INTEGER, end INTEGER, PRIMARY KEY(uid, start));").stepThis().dispose();
                    this.f790b.f847i.executeFast("CREATE INDEX IF NOT EXISTS uid_end_messages_holes ON messages_holes(uid, end);").stepThis().dispose();
                    this.f790b.f847i.executeFast("CREATE TABLE IF NOT EXISTS messages_imp_holes(uid INTEGER, start INTEGER, end INTEGER, PRIMARY KEY(uid, start));").stepThis().dispose();
                    this.f790b.f847i.executeFast("CREATE INDEX IF NOT EXISTS uid_end_messages_imp_holes ON messages_imp_holes(uid, end);").stepThis().dispose();
                    this.f790b.f847i.executeFast("PRAGMA user_version = 22").stepThis().dispose();
                    i = 22;
                }
                if (i == 22) {
                    this.f790b.f847i.executeFast("CREATE TABLE IF NOT EXISTS media_holes_v2(uid INTEGER, type INTEGER, start INTEGER, end INTEGER, PRIMARY KEY(uid, type, start));").stepThis().dispose();
                    this.f790b.f847i.executeFast("CREATE INDEX IF NOT EXISTS uid_end_media_holes_v2 ON media_holes_v2(uid, type, end);").stepThis().dispose();
                    this.f790b.f847i.executeFast("PRAGMA user_version = 23").stepThis().dispose();
                    i = 23;
                }
                if (i == 24) {
                    this.f790b.f847i.executeFast("DELETE FROM media_holes_v2 WHERE uid != 0 AND type >= 0 AND start IN (0, 1)").stepThis().dispose();
                    this.f790b.f847i.executeFast("PRAGMA user_version = 25").stepThis().dispose();
                    i = 25;
                }
                if (i == 25 || i == 26) {
                    this.f790b.f847i.executeFast("CREATE TABLE IF NOT EXISTS channel_users_v2(did INTEGER, uid INTEGER, date INTEGER, data BLOB, PRIMARY KEY(did, uid))").stepThis().dispose();
                    this.f790b.f847i.executeFast("PRAGMA user_version = 27").stepThis().dispose();
                    i = 27;
                }
                if (i == 27) {
                    this.f790b.f847i.executeFast("ALTER TABLE web_recent_v3 ADD COLUMN document BLOB default NULL").stepThis().dispose();
                    this.f790b.f847i.executeFast("PRAGMA user_version = 28").stepThis().dispose();
                    i = 28;
                }
                if (i == 28) {
                    this.f790b.f847i.executeFast("PRAGMA user_version = 29").stepThis().dispose();
                    i = 29;
                }
                if (i == 29) {
                    this.f790b.f847i.executeFast("DELETE FROM sent_files_v2 WHERE 1").stepThis().dispose();
                    this.f790b.f847i.executeFast("DELETE FROM download_queue WHERE 1").stepThis().dispose();
                    this.f790b.f847i.executeFast("PRAGMA user_version = 30").stepThis().dispose();
                    i = 30;
                }
                if (i == 30) {
                    this.f790b.f847i.executeFast("ALTER TABLE chat_settings_v2 ADD COLUMN pinned INTEGER default 0").stepThis().dispose();
                    this.f790b.f847i.executeFast("CREATE INDEX IF NOT EXISTS chat_settings_pinned_idx ON chat_settings_v2(uid, pinned) WHERE pinned != 0;").stepThis().dispose();
                    this.f790b.f847i.executeFast("CREATE TABLE IF NOT EXISTS chat_pinned(uid INTEGER PRIMARY KEY, pinned INTEGER, data BLOB)").stepThis().dispose();
                    this.f790b.f847i.executeFast("CREATE INDEX IF NOT EXISTS chat_pinned_mid_idx ON chat_pinned(uid, pinned) WHERE pinned != 0;").stepThis().dispose();
                    this.f790b.f847i.executeFast("CREATE TABLE IF NOT EXISTS users_data(uid INTEGER PRIMARY KEY, about TEXT)").stepThis().dispose();
                    this.f790b.f847i.executeFast("PRAGMA user_version = 31").stepThis().dispose();
                    i = 31;
                }
                if (i == 31) {
                    this.f790b.f847i.executeFast("DROP TABLE IF EXISTS bot_recent;").stepThis().dispose();
                    this.f790b.f847i.executeFast("CREATE TABLE IF NOT EXISTS chat_hints(did INTEGER, type INTEGER, rating REAL, date INTEGER, PRIMARY KEY(did, type))").stepThis().dispose();
                    this.f790b.f847i.executeFast("CREATE INDEX IF NOT EXISTS chat_hints_rating_idx ON chat_hints(rating);").stepThis().dispose();
                    this.f790b.f847i.executeFast("PRAGMA user_version = 32").stepThis().dispose();
                }
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.download.c.2 */
    class DownloadMessagesStorage implements Runnable {
        final /* synthetic */ messages_Messages f791a;
        final /* synthetic */ int f792b;
        final /* synthetic */ int f793c;
        final /* synthetic */ long f794d;
        final /* synthetic */ int f795e;
        final /* synthetic */ boolean f796f;
        final /* synthetic */ DownloadMessagesStorage f797g;

        DownloadMessagesStorage(DownloadMessagesStorage downloadMessagesStorage, messages_Messages com_hanista_mobogram_tgnet_TLRPC_messages_Messages, int i, int i2, long j, int i3, boolean z) {
            this.f797g = downloadMessagesStorage;
            this.f791a = com_hanista_mobogram_tgnet_TLRPC_messages_Messages;
            this.f792b = i;
            this.f793c = i2;
            this.f794d = j;
            this.f795e = i3;
            this.f796f = z;
        }

        public void run() {
            try {
                if (!this.f791a.messages.isEmpty()) {
                    int i;
                    this.f797g.f847i.beginTransaction();
                    if (this.f792b == 0) {
                        i = ((Message) this.f791a.messages.get(this.f791a.messages.size() - 1)).id;
                        this.f797g.m797a("messages_holes", this.f794d, i, this.f795e);
                        this.f797g.m809a(this.f794d, i, this.f795e, -1);
                    } else if (this.f792b == 1) {
                        r5 = ((Message) this.f791a.messages.get(0)).id;
                        this.f797g.m797a("messages_holes", this.f794d, this.f795e, r5);
                        this.f797g.m809a(this.f794d, this.f795e, r5, -1);
                    } else if (this.f792b == 3 || this.f792b == 2) {
                        r5 = this.f795e == 0 ? ConnectionsManager.DEFAULT_DATACENTER_ID : ((Message) this.f791a.messages.get(0)).id;
                        i = ((Message) this.f791a.messages.get(this.f791a.messages.size() - 1)).id;
                        this.f797g.m797a("messages_holes", this.f794d, i, r5);
                        this.f797g.m809a(this.f794d, i, r5, -1);
                    }
                    int size = this.f791a.messages.size();
                    SQLitePreparedStatement executeFast = this.f797g.f847i.executeFast("REPLACE INTO messages VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, NULL, ?)");
                    SQLitePreparedStatement executeFast2 = this.f797g.f847i.executeFast("REPLACE INTO media_v2 VALUES(?, ?, ?, ?, ?)");
                    Message message = null;
                    i = 0;
                    int i2 = 0;
                    while (i2 < size) {
                        Message message2 = (Message) this.f791a.messages.get(i2);
                        long j = (long) message2.id;
                        int i3 = i == 0 ? message2.to_id.channel_id : i;
                        long j2 = message2.to_id.channel_id != 0 ? j | (((long) i3) << 32) : j;
                        if (this.f792b == -2) {
                            SQLiteCursor queryFinalized = this.f797g.f847i.queryFinalized(String.format(Locale.US, "SELECT mid FROM messages WHERE mid = %d", new Object[]{Long.valueOf(j2)}), new Object[0]);
                            boolean next = queryFinalized.next();
                            queryFinalized.dispose();
                            if (!next) {
                                message2 = message;
                                i2++;
                                i = i3;
                                message = message2;
                            }
                        }
                        if (i2 == 0 && this.f796f) {
                            SQLitePreparedStatement executeFast3 = this.f797g.f847i.executeFast("REPLACE INTO dialogs VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                            executeFast3.bindLong(1, this.f794d);
                            executeFast3.bindInteger(2, message2.date);
                            executeFast3.bindInteger(3, 0);
                            executeFast3.bindLong(4, j2);
                            executeFast3.bindInteger(5, message2.id);
                            executeFast3.bindInteger(6, 0);
                            executeFast3.bindLong(7, j2);
                            executeFast3.bindInteger(8, this.f792b < 0 ? message2.ttl : 0);
                            executeFast3.bindInteger(9, this.f791a.pts);
                            executeFast3.bindInteger(10, message2.date);
                            executeFast3.step();
                            executeFast3.dispose();
                        }
                        this.f797g.m793a(message2);
                        executeFast.requery();
                        NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(message2.getObjectSize());
                        message2.serializeToStream(nativeByteBuffer);
                        executeFast.bindLong(1, j2);
                        executeFast.bindLong(2, this.f794d);
                        executeFast.bindInteger(3, MessageObject.getUnreadFlags(message2));
                        executeFast.bindInteger(4, message2.send_state);
                        executeFast.bindInteger(5, ConnectionsManager.getInstance().getCurrentTime());
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
                            executeFast2.bindLong(1, j2);
                            executeFast2.bindLong(2, this.f794d);
                            executeFast2.bindInteger(3, message2.date);
                            executeFast2.bindInteger(4, SharedMediaQuery.getMediaType(message2));
                            executeFast2.bindByteBuffer(5, nativeByteBuffer);
                            executeFast2.step();
                        }
                        nativeByteBuffer.reuse();
                        if (this.f792b != 0 || message2.reply_markup == null || ((message2.reply_markup.selective && !message2.mentioned) || (message != null && message.id >= message2.id))) {
                            message2 = message;
                        }
                        i2++;
                        i = i3;
                        message = message2;
                    }
                    executeFast.dispose();
                    executeFast2.dispose();
                    if (message != null) {
                        BotQuery.putBotKeyboard(this.f794d, message);
                    }
                    this.f797g.m798a(this.f791a.users);
                    this.f797g.m804b(this.f791a.chats);
                    this.f797g.f847i.commitTransaction();
                    if (this.f796f) {
                        DownloadMessagesStorage.m783a().m815a(new ArrayList(), false, i);
                    }
                } else if (this.f792b == 0) {
                    if (this.f793c != 2) {
                        this.f797g.m796a("messages_holes", this.f794d, this.f795e);
                        this.f797g.m808a(this.f794d, this.f795e, -1);
                    }
                    if (this.f793c != 0) {
                        this.f797g.m796a("messages_imp_holes", this.f794d, this.f795e);
                    }
                }
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.download.c.3 */
    class DownloadMessagesStorage implements Runnable {
        final /* synthetic */ boolean f799a;
        final /* synthetic */ DownloadMessagesStorage f800b;

        /* renamed from: com.hanista.mobogram.mobo.download.c.3.1 */
        class DownloadMessagesStorage implements Runnable {
            final /* synthetic */ DownloadMessagesStorage f798a;

            DownloadMessagesStorage(DownloadMessagesStorage downloadMessagesStorage) {
                this.f798a = downloadMessagesStorage;
            }

            public void run() {
                MessagesController.getInstance().getDifference();
            }
        }

        DownloadMessagesStorage(DownloadMessagesStorage downloadMessagesStorage, boolean z) {
            this.f800b = downloadMessagesStorage;
            this.f799a = z;
        }

        public void run() {
            DownloadMessagesStorage.f838a = 0;
            DownloadMessagesStorage.f841d = 0;
            DownloadMessagesStorage.f839b = 0;
            DownloadMessagesStorage.f840c = 0;
            DownloadMessagesStorage.f842e = 0;
            DownloadMessagesStorage.f843f = null;
            DownloadMessagesStorage.f844g = 0;
            if (this.f800b.f847i != null) {
                this.f800b.f847i.close();
                this.f800b.f847i = null;
            }
            if (this.f800b.f848j != null) {
                this.f800b.f848j.delete();
                this.f800b.f848j = null;
            }
            this.f800b.m821c();
            if (this.f799a) {
                Utilities.stageQueue.postRunnable(new DownloadMessagesStorage(this));
            }
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.download.c.4 */
    class DownloadMessagesStorage implements Runnable {
        final /* synthetic */ DownloadMessagesStorage f807a;

        /* renamed from: com.hanista.mobogram.mobo.download.c.4.1 */
        class DownloadMessagesStorage implements Runnable {
            final /* synthetic */ HashMap f801a;
            final /* synthetic */ ArrayList f802b;
            final /* synthetic */ ArrayList f803c;
            final /* synthetic */ ArrayList f804d;
            final /* synthetic */ ArrayList f805e;
            final /* synthetic */ DownloadMessagesStorage f806f;

            DownloadMessagesStorage(DownloadMessagesStorage downloadMessagesStorage, HashMap hashMap, ArrayList arrayList, ArrayList arrayList2, ArrayList arrayList3, ArrayList arrayList4) {
                this.f806f = downloadMessagesStorage;
                this.f801a = hashMap;
                this.f802b = arrayList;
                this.f803c = arrayList2;
                this.f804d = arrayList3;
                this.f805e = arrayList4;
            }

            public void run() {
                NotificationsController.getInstance().processLoadedUnreadMessages(this.f801a, this.f802b, this.f803c, this.f804d, this.f805e);
            }
        }

        DownloadMessagesStorage(DownloadMessagesStorage downloadMessagesStorage) {
            this.f807a = downloadMessagesStorage;
        }

        public void run() {
            HashMap hashMap = new HashMap();
            SQLiteCursor queryFinalized = this.f807a.f847i.queryFinalized("SELECT d.did, d.unread_count, s.flags FROM dialogs as d LEFT JOIN dialog_settings as s ON d.did = s.did WHERE d.unread_count != 0", new Object[0]);
            StringBuilder stringBuilder = new StringBuilder();
            while (queryFinalized.next()) {
                if (queryFinalized.isNull(2) || queryFinalized.intValue(2) != 1) {
                    long longValue = queryFinalized.longValue(0);
                    hashMap.put(Long.valueOf(longValue), Integer.valueOf(queryFinalized.intValue(1)));
                    if (stringBuilder.length() != 0) {
                        stringBuilder.append(",");
                    }
                    stringBuilder.append(longValue);
                }
            }
            queryFinalized.dispose();
            Object arrayList = new ArrayList();
            ArrayList arrayList2 = new ArrayList();
            ArrayList arrayList3 = new ArrayList();
            ArrayList arrayList4 = new ArrayList();
            if (stringBuilder.length() > 0) {
                int i;
                ArrayList arrayList5 = new ArrayList();
                ArrayList arrayList6 = new ArrayList();
                Iterable arrayList7 = new ArrayList();
                queryFinalized = this.f807a.f847i.queryFinalized("SELECT read_state, data, send_state, mid, date, uid FROM messages WHERE uid IN (" + stringBuilder.toString() + ") AND out = 0 AND read_state IN(0,2) ORDER BY date DESC LIMIT 50", new Object[0]);
                while (queryFinalized.next()) {
                    AbstractSerializedData byteBufferValue = queryFinalized.byteBufferValue(1);
                    if (byteBufferValue != null) {
                        Message TLdeserialize = Message.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                        byteBufferValue.reuse();
                        MessageObject.setUnreadFlags(TLdeserialize, queryFinalized.intValue(0));
                        TLdeserialize.id = queryFinalized.intValue(3);
                        TLdeserialize.date = queryFinalized.intValue(4);
                        TLdeserialize.dialog_id = queryFinalized.longValue(5);
                        arrayList.add(TLdeserialize);
                        i = (int) TLdeserialize.dialog_id;
                        int i2 = (int) (TLdeserialize.dialog_id >> 32);
                        if (i != 0) {
                            if (i < 0) {
                                if (!arrayList6.contains(Integer.valueOf(-i))) {
                                    arrayList6.add(Integer.valueOf(-i));
                                }
                            } else if (!arrayList5.contains(Integer.valueOf(i))) {
                                arrayList5.add(Integer.valueOf(i));
                            }
                        } else if (!arrayList7.contains(Integer.valueOf(i2))) {
                            arrayList7.add(Integer.valueOf(i2));
                        }
                        DownloadMessagesStorage.m794a(TLdeserialize, arrayList5, arrayList6);
                        TLdeserialize.send_state = queryFinalized.intValue(2);
                        if (!(TLdeserialize.to_id.channel_id != 0 || MessageObject.isUnread(TLdeserialize) || i == 0) || TLdeserialize.id > 0) {
                            TLdeserialize.send_state = 0;
                        }
                        if (i == 0 && !queryFinalized.isNull(5)) {
                            TLdeserialize.random_id = queryFinalized.longValue(5);
                        }
                    }
                }
                queryFinalized.dispose();
                try {
                    ArrayList arrayList8 = new ArrayList();
                    Iterator it = arrayList.iterator();
                    while (it.hasNext()) {
                        Message message = (Message) it.next();
                        if (!arrayList8.contains(Long.valueOf(message.dialog_id))) {
                            arrayList8.add(Long.valueOf(message.dialog_id));
                        }
                    }
                    for (Long longValue2 : hashMap.keySet()) {
                        long longValue3 = longValue2.longValue();
                        if (!(arrayList8.contains(Long.valueOf(longValue3)) || arrayList6.contains(Long.valueOf(longValue3)))) {
                            arrayList6.add(Integer.valueOf(Math.abs((int) longValue3)));
                        }
                    }
                } catch (Exception e) {
                }
                try {
                    if (!arrayList7.isEmpty()) {
                        this.f807a.m813a(TextUtils.join(",", arrayList7), arrayList4, arrayList5);
                    }
                    if (!arrayList5.isEmpty()) {
                        this.f807a.m812a(TextUtils.join(",", arrayList5), arrayList2);
                    }
                    if (!arrayList6.isEmpty()) {
                        this.f807a.m820b(TextUtils.join(",", arrayList6), arrayList3);
                        i = 0;
                        while (i < arrayList3.size()) {
                            int i3;
                            Chat chat = (Chat) arrayList3.get(i);
                            if (chat == null || (!chat.left && chat.migrated_to == null)) {
                                i3 = i;
                            } else {
                                this.f807a.f847i.executeFast("UPDATE dialogs SET unread_count = 0, unread_count_i = 0 WHERE did = " + ((long) (-chat.id))).stepThis().dispose();
                                this.f807a.f847i.executeFast(String.format(Locale.US, "UPDATE messages SET read_state = 3 WHERE uid = %d AND mid > 0 AND read_state IN(0,2) AND out = 0", new Object[]{Long.valueOf(r10)})).stepThis().dispose();
                                arrayList3.remove(i);
                                int i4 = i - 1;
                                hashMap.remove(Long.valueOf((long) (-chat.id)));
                                int i5 = 0;
                                while (i5 < arrayList.size()) {
                                    if (((Message) arrayList.get(i5)).dialog_id == ((long) (-chat.id))) {
                                        arrayList.remove(i5);
                                        i = i5 - 1;
                                    } else {
                                        i = i5;
                                    }
                                    i5 = i + 1;
                                }
                                i3 = i4;
                            }
                            i = i3 + 1;
                        }
                    }
                } catch (Throwable e2) {
                    FileLog.m18e("tmessages", e2);
                    return;
                }
            }
            Collections.reverse(arrayList);
            AndroidUtilities.runOnUIThread(new DownloadMessagesStorage(this, hashMap, arrayList, arrayList2, arrayList3, arrayList4));
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.download.c.5 */
    class DownloadMessagesStorage implements Runnable {
        final /* synthetic */ int f808a;
        final /* synthetic */ Semaphore f809b;
        final /* synthetic */ boolean f810c;
        final /* synthetic */ boolean f811d;
        final /* synthetic */ DownloadMessagesStorage f812e;

        DownloadMessagesStorage(DownloadMessagesStorage downloadMessagesStorage, int i, Semaphore semaphore, boolean z, boolean z2) {
            this.f812e = downloadMessagesStorage;
            this.f808a = i;
            this.f809b = semaphore;
            this.f810c = z;
            this.f811d = z2;
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
                SQLiteCursor queryFinalized2 = this.f812e.f847i.queryFinalized("SELECT info, pinned FROM chat_settings_v2 WHERE uid = " + this.f808a, new Object[0]);
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
                                    this.f812e.m812a(stringBuilder.toString(), arrayList);
                                }
                            } else if (TLdeserialize instanceof TL_channelFull) {
                                queryFinalized = this.f812e.f847i.queryFinalized("SELECT us.data, us.status, cu.data, cu.date FROM channel_users_v2 as cu LEFT JOIN users as us ON us.uid = cu.uid WHERE cu.did = " + (-this.f808a) + " ORDER BY cu.date DESC", new Object[0]);
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
                                    this.f812e.m812a(stringBuilder.toString(), arrayList);
                                }
                            }
                            if (this.f809b != null) {
                                this.f809b.release();
                            }
                            if ((TLdeserialize instanceof TL_channelFull) && TLdeserialize.pinned_msg_id != 0) {
                                messageObject = MessagesQuery.loadPinnedMessage(this.f808a, TLdeserialize.pinned_msg_id, false);
                            }
                            MessagesController.getInstance().processChatInfo(this.f808a, TLdeserialize, arrayList, true, this.f810c, this.f811d, messageObject);
                            if (this.f809b != null) {
                                this.f809b.release();
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
                        this.f812e.m812a(stringBuilder.toString(), arrayList);
                    }
                } else if (TLdeserialize instanceof TL_channelFull) {
                    queryFinalized = this.f812e.f847i.queryFinalized("SELECT us.data, us.status, cu.data, cu.date FROM channel_users_v2 as cu LEFT JOIN users as us ON us.uid = cu.uid WHERE cu.did = " + (-this.f808a) + " ORDER BY cu.date DESC", new Object[0]);
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
                        this.f812e.m812a(stringBuilder.toString(), arrayList);
                    }
                }
                if (this.f809b != null) {
                    this.f809b.release();
                }
                messageObject = MessagesQuery.loadPinnedMessage(this.f808a, TLdeserialize.pinned_msg_id, false);
                MessagesController.getInstance().processChatInfo(this.f808a, TLdeserialize, arrayList, true, this.f810c, this.f811d, messageObject);
                if (this.f809b != null) {
                    this.f809b.release();
                }
            } catch (Exception e4) {
                e2 = e4;
                TLdeserialize = null;
                try {
                    FileLog.m18e("tmessages", e2);
                    MessagesController.getInstance().processChatInfo(this.f808a, TLdeserialize, arrayList, true, this.f810c, this.f811d, null);
                    if (this.f809b != null) {
                        this.f809b.release();
                    }
                } catch (Throwable e22) {
                    th = e22;
                    MessagesController.getInstance().processChatInfo(this.f808a, TLdeserialize, arrayList, true, this.f810c, this.f811d, null);
                    if (this.f809b != null) {
                        this.f809b.release();
                    }
                    throw th;
                }
            } catch (Throwable e222) {
                th = e222;
                TLdeserialize = null;
                MessagesController.getInstance().processChatInfo(this.f808a, TLdeserialize, arrayList, true, this.f810c, this.f811d, null);
                if (this.f809b != null) {
                    this.f809b.release();
                }
                throw th;
            }
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.download.c.6 */
    class DownloadMessagesStorage implements Runnable {
        final /* synthetic */ int f814a;
        final /* synthetic */ int f815b;
        final /* synthetic */ int f816c;
        final /* synthetic */ long f817d;
        final /* synthetic */ int f818e;
        final /* synthetic */ int f819f;
        final /* synthetic */ int f820g;
        final /* synthetic */ int f821h;
        final /* synthetic */ DownloadMessagesStorage f822i;

        /* renamed from: com.hanista.mobogram.mobo.download.c.6.1 */
        class DownloadMessagesStorage implements Comparator<Message> {
            final /* synthetic */ DownloadMessagesStorage f813a;

            DownloadMessagesStorage(DownloadMessagesStorage downloadMessagesStorage) {
                this.f813a = downloadMessagesStorage;
            }

            public int m780a(Message message, Message message2) {
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

            public /* synthetic */ int compare(Object obj, Object obj2) {
                return m780a((Message) obj, (Message) obj2);
            }
        }

        DownloadMessagesStorage(DownloadMessagesStorage downloadMessagesStorage, int i, int i2, int i3, long j, int i4, int i5, int i6, int i7) {
            this.f822i = downloadMessagesStorage;
            this.f814a = i;
            this.f815b = i2;
            this.f816c = i3;
            this.f817d = j;
            this.f818e = i4;
            this.f819f = i5;
            this.f820g = i6;
            this.f821h = i7;
        }

        public void run() {
            int intValue;
            Object obj;
            Throwable e;
            AbstractSerializedData byteBufferValue;
            ArrayList arrayList;
            messages_Messages tL_messages_messages = new TL_messages_messages();
            int i = 0;
            int i2 = this.f814a;
            int i3 = 0;
            int i4 = 0;
            int i5 = 0;
            boolean z = false;
            int i6 = 0;
            long j = (long) this.f815b;
            int i7 = this.f815b;
            int i8 = this.f816c != 0 ? -((int) this.f817d) : 0;
            if (!(j == 0 || i8 == 0)) {
                j |= ((long) i8) << 32;
            }
            boolean z2 = false;
            ArrayList arrayList2 = new ArrayList();
            ArrayList arrayList3 = new ArrayList();
            ArrayList arrayList4 = new ArrayList();
            HashMap hashMap = new HashMap();
            HashMap hashMap2 = new HashMap();
            int i9 = (int) this.f817d;
            int i10;
            int i11;
            Object[] objArr;
            SQLiteCursor queryFinalized;
            SQLiteCursor queryFinalized2;
            SQLiteDatabase a;
            Object[] objArr2;
            if (i9 != 0) {
                long j2;
                SQLiteDatabase a2;
                Locale locale;
                String str;
                SQLitePreparedStatement executeFast;
                long j3;
                long j4;
                Locale locale2;
                String str2;
                Long[] lArr;
                Long[] lArr2;
                String str3 = this.f816c == 2 ? " AND imp = 1 " : TtmlNode.ANONYMOUS_REGION_ID;
                String str4 = this.f816c == 2 ? "messages_imp_holes" : "messages_holes";
                if (!(this.f818e == 1 || this.f818e == 3 || this.f819f != 0)) {
                    String str5;
                    Object[] objArr3;
                    if (this.f818e == 2) {
                        SQLiteCursor queryFinalized3 = this.f822i.f847i.queryFinalized("SELECT inbox_max, unread_count, date FROM dialogs WHERE did = " + this.f817d, new Object[0]);
                        if (queryFinalized3.next()) {
                            i4 = queryFinalized3.intValue(0);
                            j = (long) i4;
                            i = queryFinalized3.intValue(1);
                            i6 = queryFinalized3.intValue(2);
                            z = true;
                            if (j == 0 || i8 == 0) {
                                i7 = i4;
                            } else {
                                j |= ((long) i8) << 32;
                                i7 = i4;
                            }
                        }
                        queryFinalized3.dispose();
                        if (!z) {
                            SQLiteDatabase a3 = this.f822i.f847i;
                            Locale locale3 = Locale.US;
                            str5 = "SELECT min(mid), max(date) FROM messages WHERE uid = %d AND out = 0 AND read_state IN(0,2) AND mid > 0" + str3;
                            objArr3 = new Object[1];
                            objArr3[0] = Long.valueOf(this.f817d);
                            queryFinalized3 = a3.queryFinalized(String.format(locale3, str5, objArr3), new Object[0]);
                            if (queryFinalized3.next()) {
                                i4 = queryFinalized3.intValue(0);
                                i6 = queryFinalized3.intValue(1);
                            }
                            queryFinalized3.dispose();
                            if (i4 != 0) {
                                a3 = this.f822i.f847i;
                                locale3 = Locale.US;
                                str5 = "SELECT COUNT(*) FROM messages WHERE uid = %d AND mid >= %d " + str3 + "AND out = 0 AND read_state IN(0,2)";
                                objArr3 = new Object[2];
                                objArr3[0] = Long.valueOf(this.f817d);
                                objArr3[1] = Integer.valueOf(i4);
                                queryFinalized3 = a3.queryFinalized(String.format(locale3, str5, objArr3), new Object[0]);
                                if (queryFinalized3.next()) {
                                    i = queryFinalized3.intValue(0);
                                }
                                queryFinalized3.dispose();
                            }
                        }
                    }
                    if (i2 > i || i < 4) {
                        i2 = Math.max(i2, i + 10);
                        if (i < 4) {
                            i = 0;
                            i4 = 0;
                            z = false;
                            j2 = 0;
                            i10 = i7;
                            i11 = 0;
                            a2 = this.f822i.f847i;
                            locale = Locale.US;
                            str = "SELECT start FROM " + str4 + " WHERE uid = %d AND start IN (0, 1)";
                            objArr = new Object[1];
                            objArr[0] = Long.valueOf(this.f817d);
                            queryFinalized = a2.queryFinalized(String.format(locale, str, objArr), new Object[0]);
                            if (queryFinalized.next()) {
                                z2 = queryFinalized.intValue(0) == 1;
                                queryFinalized.dispose();
                            } else {
                                queryFinalized.dispose();
                                a2 = this.f822i.f847i;
                                objArr = new Object[1];
                                objArr[0] = Long.valueOf(this.f817d);
                                queryFinalized = a2.queryFinalized(String.format(Locale.US, "SELECT min(mid) FROM messages WHERE uid = %d AND mid > 0", objArr), new Object[0]);
                                if (queryFinalized.next()) {
                                    intValue = queryFinalized.intValue(0);
                                    if (intValue != 0) {
                                        executeFast = this.f822i.f847i.executeFast("REPLACE INTO " + str4 + " VALUES(?, ?, ?)");
                                        executeFast.requery();
                                        executeFast.bindLong(1, this.f817d);
                                        executeFast.bindInteger(2, 0);
                                        executeFast.bindInteger(3, intValue);
                                        executeFast.step();
                                        executeFast.dispose();
                                    }
                                }
                                queryFinalized.dispose();
                            }
                            if (this.f818e != 3 || (r18 && this.f818e == 2)) {
                                queryFinalized2 = this.f822i.f847i.queryFinalized(String.format(Locale.US, "SELECT max(mid) FROM messages WHERE uid = %d AND mid > 0", new Object[]{Long.valueOf(this.f817d)}), new Object[0]);
                                if (queryFinalized2.next()) {
                                    i11 = queryFinalized2.intValue(0);
                                }
                                queryFinalized2.dispose();
                                obj = 1;
                                a2 = this.f822i.f847i;
                                locale = Locale.US;
                                str = "SELECT start FROM " + str4 + " WHERE uid = %d AND start < %d AND end > %d";
                                objArr = new Object[3];
                                objArr[0] = Long.valueOf(this.f817d);
                                objArr[1] = Integer.valueOf(i10);
                                objArr[2] = Integer.valueOf(i10);
                                queryFinalized = a2.queryFinalized(String.format(locale, str, objArr), new Object[0]);
                                if (queryFinalized.next()) {
                                    obj = null;
                                }
                                queryFinalized.dispose();
                                if (obj != null) {
                                    j3 = 0;
                                    j4 = 1;
                                    a = this.f822i.f847i;
                                    locale2 = Locale.US;
                                    str2 = "SELECT start FROM " + str4 + " WHERE uid = %d AND start >= %d ORDER BY start ASC LIMIT 1";
                                    lArr = new Object[2];
                                    lArr[0] = Long.valueOf(this.f817d);
                                    lArr[1] = Integer.valueOf(i10);
                                    queryFinalized2 = a.queryFinalized(String.format(locale2, str2, lArr), new Object[0]);
                                    if (queryFinalized2.next()) {
                                        j3 = (long) queryFinalized2.intValue(0);
                                        if (i8 != 0) {
                                            j3 |= ((long) i8) << 32;
                                        }
                                    }
                                    queryFinalized2.dispose();
                                    a = this.f822i.f847i;
                                    locale2 = Locale.US;
                                    str2 = "SELECT end FROM " + str4 + " WHERE uid = %d AND end <= %d ORDER BY end DESC LIMIT 1";
                                    lArr2 = new Object[2];
                                    lArr2[0] = Long.valueOf(this.f817d);
                                    lArr2[1] = Integer.valueOf(i10);
                                    queryFinalized2 = a.queryFinalized(String.format(locale2, str2, lArr2), new Object[0]);
                                    if (queryFinalized2.next()) {
                                        j4 = (long) queryFinalized2.intValue(0);
                                        if (i8 != 0) {
                                            j4 |= ((long) i8) << 32;
                                        }
                                    }
                                    queryFinalized2.dispose();
                                    if (j3 == 0 || j4 != 1) {
                                        if (j3 == 0) {
                                            j3 = 1000000000;
                                            if (i8 != 0) {
                                                j3 = 1000000000 | (((long) i8) << 32);
                                            }
                                        }
                                        a = this.f822i.f847i;
                                        locale2 = Locale.US;
                                        str2 = "SELECT * FROM (SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND m.mid <= %d AND m.mid >= %d " + str3 + "ORDER BY m.date DESC, m.mid DESC LIMIT %d) UNION SELECT * FROM (SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND m.mid > %d AND m.mid <= %d " + str3 + "ORDER BY m.date ASC, m.mid ASC LIMIT %d)";
                                        lArr2 = new Object[8];
                                        lArr2[0] = Long.valueOf(this.f817d);
                                        lArr2[1] = Long.valueOf(j2);
                                        lArr2[2] = Long.valueOf(j4);
                                        lArr2[3] = Integer.valueOf(i2 / 2);
                                        lArr2[4] = Long.valueOf(this.f817d);
                                        lArr2[5] = Long.valueOf(j2);
                                        lArr2[6] = Long.valueOf(j3);
                                        lArr2[7] = Integer.valueOf(i2 / 2);
                                        queryFinalized2 = a.queryFinalized(String.format(locale2, str2, lArr2), new Object[0]);
                                    } else {
                                        queryFinalized2 = this.f822i.f847i.queryFinalized(String.format(Locale.US, "SELECT * FROM (SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND m.mid <= %d " + str3 + "ORDER BY m.date DESC, m.mid DESC LIMIT %d) UNION SELECT * FROM (SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND m.mid > %d " + str3 + "ORDER BY m.date DESC, m.mid ASC LIMIT %d)", new Object[]{Long.valueOf(this.f817d), Long.valueOf(j2), Integer.valueOf(i2 / 2), Long.valueOf(this.f817d), Long.valueOf(j2), Integer.valueOf(i2 / 2)}), new Object[0]);
                                    }
                                } else {
                                    queryFinalized2 = null;
                                }
                            } else if (this.f818e == 1) {
                                j3 = 0;
                                a = this.f822i.f847i;
                                locale2 = Locale.US;
                                str2 = "SELECT start, end FROM " + str4 + " WHERE uid = %d AND start >= %d AND start != 1 AND end != 1 ORDER BY start ASC LIMIT 1";
                                objArr2 = new Object[2];
                                objArr2[0] = Long.valueOf(this.f817d);
                                objArr2[1] = Integer.valueOf(this.f815b);
                                queryFinalized2 = a.queryFinalized(String.format(locale2, str2, objArr2), new Object[0]);
                                if (queryFinalized2.next()) {
                                    j3 = (long) queryFinalized2.intValue(0);
                                    if (i8 != 0) {
                                        j3 |= ((long) i8) << 32;
                                    }
                                }
                                queryFinalized2.dispose();
                                if (j3 != 0) {
                                    a = this.f822i.f847i;
                                    locale2 = Locale.US;
                                    str2 = "SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND m.date >= %d AND m.mid > %d AND m.mid <= %d " + str3 + "ORDER BY m.date ASC, m.mid ASC LIMIT %d";
                                    objArr2 = new Object[5];
                                    objArr2[0] = Long.valueOf(this.f817d);
                                    objArr2[1] = Integer.valueOf(this.f819f);
                                    objArr2[2] = Long.valueOf(j2);
                                    objArr2[3] = Long.valueOf(j3);
                                    objArr2[4] = Integer.valueOf(i2);
                                    queryFinalized2 = a.queryFinalized(String.format(locale2, str2, objArr2), new Object[0]);
                                } else {
                                    queryFinalized2 = this.f822i.f847i.queryFinalized(String.format(Locale.US, "SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND m.date >= %d AND m.mid > %d " + str3 + "ORDER BY m.date ASC, m.mid ASC LIMIT %d", new Object[]{Long.valueOf(this.f817d), Integer.valueOf(this.f819f), Long.valueOf(j2), Integer.valueOf(i2)}), new Object[0]);
                                }
                            } else if (this.f819f == 0) {
                                a2 = this.f822i.f847i;
                                objArr = new Object[1];
                                objArr[0] = Long.valueOf(this.f817d);
                                queryFinalized = a2.queryFinalized(String.format(Locale.US, "SELECT max(mid) FROM messages WHERE uid = %d AND mid > 0", objArr), new Object[0]);
                                if (queryFinalized.next()) {
                                    i11 = queryFinalized.intValue(0);
                                }
                                queryFinalized.dispose();
                                j3 = 0;
                                a2 = this.f822i.f847i;
                                Locale locale4 = Locale.US;
                                str5 = "SELECT max(end) FROM " + str4 + " WHERE uid = %d";
                                objArr3 = new Object[1];
                                objArr3[0] = Long.valueOf(this.f817d);
                                queryFinalized = a2.queryFinalized(String.format(locale4, str5, objArr3), new Object[0]);
                                if (queryFinalized.next()) {
                                    j3 = (long) queryFinalized.intValue(0);
                                    if (i8 != 0) {
                                        j3 |= ((long) i8) << 32;
                                    }
                                }
                                queryFinalized.dispose();
                                if (j3 != 0) {
                                    a2 = this.f822i.f847i;
                                    locale4 = Locale.US;
                                    str5 = "SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND (m.mid >= %d OR m.mid < 0) " + str3 + "ORDER BY m.date DESC, m.mid DESC LIMIT %d,%d";
                                    objArr3 = new Object[4];
                                    objArr3[0] = Long.valueOf(this.f817d);
                                    objArr3[1] = Long.valueOf(j3);
                                    objArr3[2] = Integer.valueOf(i3);
                                    objArr3[3] = Integer.valueOf(i2);
                                    queryFinalized2 = a2.queryFinalized(String.format(locale4, str5, objArr3), new Object[0]);
                                } else {
                                    a2 = this.f822i.f847i;
                                    locale = Locale.US;
                                    str = "SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d " + str3 + "ORDER BY m.date DESC, m.mid DESC LIMIT %d,%d";
                                    objArr = new Object[3];
                                    objArr[0] = Long.valueOf(this.f817d);
                                    objArr[1] = Integer.valueOf(i3);
                                    objArr[2] = Integer.valueOf(i2);
                                    queryFinalized2 = a2.queryFinalized(String.format(locale, str, objArr), new Object[0]);
                                }
                            } else if (j2 != 0) {
                                j3 = 0;
                                a = this.f822i.f847i;
                                locale2 = Locale.US;
                                str2 = "SELECT end FROM " + str4 + " WHERE uid = %d AND end <= %d ORDER BY end DESC LIMIT 1";
                                objArr2 = new Object[2];
                                objArr2[0] = Long.valueOf(this.f817d);
                                objArr2[1] = Integer.valueOf(this.f815b);
                                queryFinalized2 = a.queryFinalized(String.format(locale2, str2, objArr2), new Object[0]);
                                if (queryFinalized2.next()) {
                                    j3 = (long) queryFinalized2.intValue(0);
                                    if (i8 != 0) {
                                        j3 |= ((long) i8) << 32;
                                    }
                                }
                                queryFinalized2.dispose();
                                if (j3 != 0) {
                                    a = this.f822i.f847i;
                                    locale2 = Locale.US;
                                    str2 = "SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND m.date <= %d AND m.mid < %d AND (m.mid >= %d OR m.mid < 0) " + str3 + "ORDER BY m.date DESC, m.mid DESC LIMIT %d";
                                    objArr2 = new Object[5];
                                    objArr2[0] = Long.valueOf(this.f817d);
                                    objArr2[1] = Integer.valueOf(this.f819f);
                                    objArr2[2] = Long.valueOf(j2);
                                    objArr2[3] = Long.valueOf(j3);
                                    objArr2[4] = Integer.valueOf(i2);
                                    queryFinalized2 = a.queryFinalized(String.format(locale2, str2, objArr2), new Object[0]);
                                } else {
                                    queryFinalized2 = this.f822i.f847i.queryFinalized(String.format(Locale.US, "SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND m.date <= %d AND m.mid < %d " + str3 + "ORDER BY m.date DESC, m.mid DESC LIMIT %d", new Object[]{Long.valueOf(this.f817d), Integer.valueOf(this.f819f), Long.valueOf(j2), Integer.valueOf(i2)}), new Object[0]);
                                }
                            } else {
                                a2 = this.f822i.f847i;
                                locale = Locale.US;
                                str = "SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND m.date <= %d " + str3 + "ORDER BY m.date DESC, m.mid DESC LIMIT %d,%d";
                                objArr = new Object[4];
                                objArr[0] = Long.valueOf(this.f817d);
                                objArr[1] = Integer.valueOf(this.f819f);
                                objArr[2] = Integer.valueOf(i3);
                                objArr[3] = Integer.valueOf(i2);
                                queryFinalized2 = a2.queryFinalized(String.format(locale, str, objArr), new Object[0]);
                            }
                            queryFinalized = queryFinalized2;
                            intValue = i10;
                            i5 = i11;
                        }
                    } else {
                        i3 = i - i2;
                        i2 += 10;
                        j2 = j;
                        i10 = i7;
                        i11 = 0;
                        a2 = this.f822i.f847i;
                        locale = Locale.US;
                        str = "SELECT start FROM " + str4 + " WHERE uid = %d AND start IN (0, 1)";
                        objArr = new Object[1];
                        objArr[0] = Long.valueOf(this.f817d);
                        queryFinalized = a2.queryFinalized(String.format(locale, str, objArr), new Object[0]);
                        if (queryFinalized.next()) {
                            queryFinalized.dispose();
                            a2 = this.f822i.f847i;
                            objArr = new Object[1];
                            objArr[0] = Long.valueOf(this.f817d);
                            queryFinalized = a2.queryFinalized(String.format(Locale.US, "SELECT min(mid) FROM messages WHERE uid = %d AND mid > 0", objArr), new Object[0]);
                            if (queryFinalized.next()) {
                                intValue = queryFinalized.intValue(0);
                                if (intValue != 0) {
                                    executeFast = this.f822i.f847i.executeFast("REPLACE INTO " + str4 + " VALUES(?, ?, ?)");
                                    executeFast.requery();
                                    executeFast.bindLong(1, this.f817d);
                                    executeFast.bindInteger(2, 0);
                                    executeFast.bindInteger(3, intValue);
                                    executeFast.step();
                                    executeFast.dispose();
                                }
                            }
                            queryFinalized.dispose();
                        } else {
                            if (queryFinalized.intValue(0) == 1) {
                            }
                            queryFinalized.dispose();
                        }
                        if (this.f818e != 3) {
                        }
                        queryFinalized2 = this.f822i.f847i.queryFinalized(String.format(Locale.US, "SELECT max(mid) FROM messages WHERE uid = %d AND mid > 0", new Object[]{Long.valueOf(this.f817d)}), new Object[0]);
                        if (queryFinalized2.next()) {
                            i11 = queryFinalized2.intValue(0);
                        }
                        queryFinalized2.dispose();
                        obj = 1;
                        a2 = this.f822i.f847i;
                        locale = Locale.US;
                        str = "SELECT start FROM " + str4 + " WHERE uid = %d AND start < %d AND end > %d";
                        objArr = new Object[3];
                        objArr[0] = Long.valueOf(this.f817d);
                        objArr[1] = Integer.valueOf(i10);
                        objArr[2] = Integer.valueOf(i10);
                        queryFinalized = a2.queryFinalized(String.format(locale, str, objArr), new Object[0]);
                        if (queryFinalized.next()) {
                            obj = null;
                        }
                        queryFinalized.dispose();
                        if (obj != null) {
                            queryFinalized2 = null;
                        } else {
                            j3 = 0;
                            j4 = 1;
                            a = this.f822i.f847i;
                            locale2 = Locale.US;
                            str2 = "SELECT start FROM " + str4 + " WHERE uid = %d AND start >= %d ORDER BY start ASC LIMIT 1";
                            lArr = new Object[2];
                            lArr[0] = Long.valueOf(this.f817d);
                            lArr[1] = Integer.valueOf(i10);
                            queryFinalized2 = a.queryFinalized(String.format(locale2, str2, lArr), new Object[0]);
                            if (queryFinalized2.next()) {
                                j3 = (long) queryFinalized2.intValue(0);
                                if (i8 != 0) {
                                    j3 |= ((long) i8) << 32;
                                }
                            }
                            queryFinalized2.dispose();
                            a = this.f822i.f847i;
                            locale2 = Locale.US;
                            str2 = "SELECT end FROM " + str4 + " WHERE uid = %d AND end <= %d ORDER BY end DESC LIMIT 1";
                            lArr2 = new Object[2];
                            lArr2[0] = Long.valueOf(this.f817d);
                            lArr2[1] = Integer.valueOf(i10);
                            queryFinalized2 = a.queryFinalized(String.format(locale2, str2, lArr2), new Object[0]);
                            if (queryFinalized2.next()) {
                                j4 = (long) queryFinalized2.intValue(0);
                                if (i8 != 0) {
                                    j4 |= ((long) i8) << 32;
                                }
                            }
                            queryFinalized2.dispose();
                            if (j3 == 0) {
                            }
                            if (j3 == 0) {
                                j3 = 1000000000;
                                if (i8 != 0) {
                                    j3 = 1000000000 | (((long) i8) << 32);
                                }
                            }
                            a = this.f822i.f847i;
                            locale2 = Locale.US;
                            str2 = "SELECT * FROM (SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND m.mid <= %d AND m.mid >= %d " + str3 + "ORDER BY m.date DESC, m.mid DESC LIMIT %d) UNION SELECT * FROM (SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND m.mid > %d AND m.mid <= %d " + str3 + "ORDER BY m.date ASC, m.mid ASC LIMIT %d)";
                            lArr2 = new Object[8];
                            lArr2[0] = Long.valueOf(this.f817d);
                            lArr2[1] = Long.valueOf(j2);
                            lArr2[2] = Long.valueOf(j4);
                            lArr2[3] = Integer.valueOf(i2 / 2);
                            lArr2[4] = Long.valueOf(this.f817d);
                            lArr2[5] = Long.valueOf(j2);
                            lArr2[6] = Long.valueOf(j3);
                            lArr2[7] = Integer.valueOf(i2 / 2);
                            queryFinalized2 = a.queryFinalized(String.format(locale2, str2, lArr2), new Object[0]);
                        }
                        queryFinalized = queryFinalized2;
                        intValue = i10;
                        i5 = i11;
                    }
                }
                j2 = j;
                i10 = i7;
                i11 = 0;
                try {
                    a2 = this.f822i.f847i;
                    locale = Locale.US;
                    str = "SELECT start FROM " + str4 + " WHERE uid = %d AND start IN (0, 1)";
                    objArr = new Object[1];
                    objArr[0] = Long.valueOf(this.f817d);
                    queryFinalized = a2.queryFinalized(String.format(locale, str, objArr), new Object[0]);
                    if (queryFinalized.next()) {
                        if (queryFinalized.intValue(0) == 1) {
                        }
                        queryFinalized.dispose();
                    } else {
                        queryFinalized.dispose();
                        a2 = this.f822i.f847i;
                        objArr = new Object[1];
                        objArr[0] = Long.valueOf(this.f817d);
                        queryFinalized = a2.queryFinalized(String.format(Locale.US, "SELECT min(mid) FROM messages WHERE uid = %d AND mid > 0", objArr), new Object[0]);
                        if (queryFinalized.next()) {
                            intValue = queryFinalized.intValue(0);
                            if (intValue != 0) {
                                executeFast = this.f822i.f847i.executeFast("REPLACE INTO " + str4 + " VALUES(?, ?, ?)");
                                executeFast.requery();
                                executeFast.bindLong(1, this.f817d);
                                executeFast.bindInteger(2, 0);
                                executeFast.bindInteger(3, intValue);
                                executeFast.step();
                                executeFast.dispose();
                            }
                        }
                        queryFinalized.dispose();
                    }
                    if (this.f818e != 3) {
                    }
                    queryFinalized2 = this.f822i.f847i.queryFinalized(String.format(Locale.US, "SELECT max(mid) FROM messages WHERE uid = %d AND mid > 0", new Object[]{Long.valueOf(this.f817d)}), new Object[0]);
                    if (queryFinalized2.next()) {
                        i11 = queryFinalized2.intValue(0);
                    }
                    queryFinalized2.dispose();
                    obj = 1;
                    a2 = this.f822i.f847i;
                    locale = Locale.US;
                    str = "SELECT start FROM " + str4 + " WHERE uid = %d AND start < %d AND end > %d";
                    objArr = new Object[3];
                    objArr[0] = Long.valueOf(this.f817d);
                    objArr[1] = Integer.valueOf(i10);
                    objArr[2] = Integer.valueOf(i10);
                    queryFinalized = a2.queryFinalized(String.format(locale, str, objArr), new Object[0]);
                    if (queryFinalized.next()) {
                        obj = null;
                    }
                    queryFinalized.dispose();
                    if (obj != null) {
                        j3 = 0;
                        j4 = 1;
                        a = this.f822i.f847i;
                        locale2 = Locale.US;
                        str2 = "SELECT start FROM " + str4 + " WHERE uid = %d AND start >= %d ORDER BY start ASC LIMIT 1";
                        lArr = new Object[2];
                        lArr[0] = Long.valueOf(this.f817d);
                        lArr[1] = Integer.valueOf(i10);
                        queryFinalized2 = a.queryFinalized(String.format(locale2, str2, lArr), new Object[0]);
                        if (queryFinalized2.next()) {
                            j3 = (long) queryFinalized2.intValue(0);
                            if (i8 != 0) {
                                j3 |= ((long) i8) << 32;
                            }
                        }
                        queryFinalized2.dispose();
                        a = this.f822i.f847i;
                        locale2 = Locale.US;
                        str2 = "SELECT end FROM " + str4 + " WHERE uid = %d AND end <= %d ORDER BY end DESC LIMIT 1";
                        lArr2 = new Object[2];
                        lArr2[0] = Long.valueOf(this.f817d);
                        lArr2[1] = Integer.valueOf(i10);
                        queryFinalized2 = a.queryFinalized(String.format(locale2, str2, lArr2), new Object[0]);
                        if (queryFinalized2.next()) {
                            j4 = (long) queryFinalized2.intValue(0);
                            if (i8 != 0) {
                                j4 |= ((long) i8) << 32;
                            }
                        }
                        queryFinalized2.dispose();
                        if (j3 == 0) {
                        }
                        if (j3 == 0) {
                            j3 = 1000000000;
                            if (i8 != 0) {
                                j3 = 1000000000 | (((long) i8) << 32);
                            }
                        }
                        a = this.f822i.f847i;
                        locale2 = Locale.US;
                        str2 = "SELECT * FROM (SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND m.mid <= %d AND m.mid >= %d " + str3 + "ORDER BY m.date DESC, m.mid DESC LIMIT %d) UNION SELECT * FROM (SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND m.mid > %d AND m.mid <= %d " + str3 + "ORDER BY m.date ASC, m.mid ASC LIMIT %d)";
                        lArr2 = new Object[8];
                        lArr2[0] = Long.valueOf(this.f817d);
                        lArr2[1] = Long.valueOf(j2);
                        lArr2[2] = Long.valueOf(j4);
                        lArr2[3] = Integer.valueOf(i2 / 2);
                        lArr2[4] = Long.valueOf(this.f817d);
                        lArr2[5] = Long.valueOf(j2);
                        lArr2[6] = Long.valueOf(j3);
                        lArr2[7] = Integer.valueOf(i2 / 2);
                        queryFinalized2 = a.queryFinalized(String.format(locale2, str2, lArr2), new Object[0]);
                    } else {
                        queryFinalized2 = null;
                    }
                    queryFinalized = queryFinalized2;
                    intValue = i10;
                    i5 = i11;
                } catch (Exception e2) {
                    e = e2;
                    i5 = i11;
                    try {
                        tL_messages_messages.messages.clear();
                        tL_messages_messages.chats.clear();
                        tL_messages_messages.users.clear();
                        FileLog.m18e("tmessages", e);
                        MessagesController.getInstance().processLoadedMessages(tL_messages_messages, this.f817d, i2, this.f815b, true, this.f820g, i4, i5, i, i6, this.f818e, false, z2, this.f821h, z);
                        return;
                    } catch (Throwable e3) {
                        Throwable th = e3;
                        MessagesController.getInstance().processLoadedMessages(tL_messages_messages, this.f817d, i2, this.f815b, true, this.f820g, i4, i5, i, i6, this.f818e, false, z2, this.f821h, z);
                        throw th;
                    }
                } catch (Throwable e32) {
                    th = e32;
                    i5 = i11;
                    MessagesController.getInstance().processLoadedMessages(tL_messages_messages, this.f817d, i2, this.f815b, true, this.f820g, i4, i5, i, i6, this.f818e, false, z2, this.f821h, z);
                    throw th;
                }
            }
            z2 = true;
            if (this.f818e == 1) {
                intValue = i7;
                queryFinalized = this.f822i.f847i.queryFinalized(String.format(Locale.US, "SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND m.mid < %d ORDER BY m.mid DESC LIMIT %d", new Object[]{Long.valueOf(this.f817d), Integer.valueOf(this.f815b), Integer.valueOf(i2)}), new Object[0]);
            } else if (this.f819f == 0) {
                SQLiteDatabase a4;
                if (this.f818e == 2) {
                    r4 = this.f822i.f847i;
                    r9 = new Object[1];
                    r9[0] = Long.valueOf(this.f817d);
                    SQLiteCursor queryFinalized4 = r4.queryFinalized(String.format(Locale.US, "SELECT min(mid) FROM messages WHERE uid = %d AND mid < 0", r9), new Object[0]);
                    i11 = queryFinalized4.next() ? queryFinalized4.intValue(0) : 0;
                    queryFinalized4.dispose();
                    a4 = this.f822i.f847i;
                    objArr = new Object[1];
                    objArr[0] = Long.valueOf(this.f817d);
                    queryFinalized4 = a4.queryFinalized(String.format(Locale.US, "SELECT max(mid), max(date) FROM messages WHERE uid = %d AND out = 0 AND read_state IN(0,2) AND mid < 0", objArr), new Object[0]);
                    if (queryFinalized4.next()) {
                        i4 = queryFinalized4.intValue(0);
                        i6 = queryFinalized4.intValue(1);
                    }
                    queryFinalized4.dispose();
                    if (i4 != 0) {
                        a4 = this.f822i.f847i;
                        objArr = new Object[2];
                        objArr[0] = Long.valueOf(this.f817d);
                        objArr[1] = Integer.valueOf(i4);
                        queryFinalized4 = a4.queryFinalized(String.format(Locale.US, "SELECT COUNT(*) FROM messages WHERE uid = %d AND mid <= %d AND out = 0 AND read_state IN(0,2)", objArr), new Object[0]);
                        if (queryFinalized4.next()) {
                            i = queryFinalized4.intValue(0);
                        }
                        queryFinalized4.dispose();
                    }
                } else {
                    i11 = 0;
                }
                if (i2 > i || i < 4) {
                    i2 = Math.max(i2, i + 10);
                    if (i < 4) {
                        i = 0;
                        i4 = 0;
                        i11 = 0;
                    }
                } else {
                    i3 = i - i2;
                    i2 += 10;
                }
                a4 = this.f822i.f847i;
                objArr = new Object[3];
                objArr[0] = Long.valueOf(this.f817d);
                objArr[1] = Integer.valueOf(i3);
                objArr[2] = Integer.valueOf(i2);
                queryFinalized2 = a4.queryFinalized(String.format(Locale.US, "SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d ORDER BY m.mid ASC LIMIT %d,%d", objArr), new Object[0]);
                intValue = i7;
                i5 = i11;
                queryFinalized = queryFinalized2;
            } else if (this.f815b != 0) {
                intValue = i7;
                queryFinalized = this.f822i.f847i.queryFinalized(String.format(Locale.US, "SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND m.mid > %d ORDER BY m.mid ASC LIMIT %d", new Object[]{Long.valueOf(this.f817d), Integer.valueOf(this.f815b), Integer.valueOf(i2)}), new Object[0]);
            } else {
                r4 = this.f822i.f847i;
                r9 = new Object[4];
                r9[0] = Long.valueOf(this.f817d);
                r9[1] = Integer.valueOf(this.f819f);
                r9[2] = Integer.valueOf(0);
                r9[3] = Integer.valueOf(i2);
                intValue = i7;
                queryFinalized = r4.queryFinalized(String.format(Locale.US, "SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND m.date <= %d ORDER BY m.mid ASC LIMIT %d,%d", r9), new Object[0]);
            }
            if (queryFinalized != null) {
                while (queryFinalized.next()) {
                    byteBufferValue = queryFinalized.byteBufferValue(1);
                    if (byteBufferValue != null) {
                        Message TLdeserialize = Message.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                        byteBufferValue.reuse();
                        MessageObject.setUnreadFlags(TLdeserialize, queryFinalized.intValue(0));
                        TLdeserialize.id = queryFinalized.intValue(3);
                        TLdeserialize.date = queryFinalized.intValue(4);
                        TLdeserialize.dialog_id = this.f817d;
                        if ((TLdeserialize.flags & TLRPC.MESSAGE_FLAG_HAS_VIEWS) != 0) {
                            TLdeserialize.views = queryFinalized.intValue(7);
                        }
                        tL_messages_messages.messages.add(TLdeserialize);
                        DownloadMessagesStorage.m794a(TLdeserialize, arrayList2, arrayList3);
                        if (!(TLdeserialize.reply_to_msg_id == 0 && TLdeserialize.reply_to_random_id == 0)) {
                            obj = null;
                            if (!queryFinalized.isNull(6)) {
                                AbstractSerializedData byteBufferValue2 = queryFinalized.byteBufferValue(6);
                                if (byteBufferValue2 != null) {
                                    TLdeserialize.replyMessage = Message.TLdeserialize(byteBufferValue2, byteBufferValue2.readInt32(false), false);
                                    byteBufferValue2.reuse();
                                    if (TLdeserialize.replyMessage != null) {
                                        DownloadMessagesStorage.m794a(TLdeserialize.replyMessage, arrayList2, arrayList3);
                                        obj = 1;
                                    }
                                }
                            }
                            if (obj == null) {
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
                                    try {
                                        if (!arrayList4.contains(Long.valueOf(TLdeserialize.reply_to_random_id))) {
                                            arrayList4.add(Long.valueOf(TLdeserialize.reply_to_random_id));
                                        }
                                        arrayList = (ArrayList) hashMap2.get(Long.valueOf(TLdeserialize.reply_to_random_id));
                                        if (arrayList == null) {
                                            arrayList = new ArrayList();
                                            hashMap2.put(Long.valueOf(TLdeserialize.reply_to_random_id), arrayList);
                                        }
                                        arrayList.add(TLdeserialize);
                                    } catch (Exception e4) {
                                        e32 = e4;
                                    }
                                }
                            }
                        }
                        TLdeserialize.send_state = queryFinalized.intValue(2);
                        if (TLdeserialize.id > 0 && TLdeserialize.send_state != 0) {
                            TLdeserialize.send_state = 0;
                        }
                        if (i9 == 0 && !queryFinalized.isNull(5)) {
                            TLdeserialize.random_id = queryFinalized.longValue(5);
                        }
                        if (!(((int) this.f817d) != 0 || TLdeserialize.media == null || TLdeserialize.media.photo == null)) {
                            try {
                                a = this.f822i.f847i;
                                objArr2 = new Object[1];
                                objArr2[0] = Integer.valueOf(TLdeserialize.id);
                                queryFinalized2 = a.queryFinalized(String.format(Locale.US, "SELECT date FROM enc_tasks_v2 WHERE mid = %d", objArr2), new Object[0]);
                                if (queryFinalized2.next()) {
                                    TLdeserialize.destroyTime = queryFinalized2.intValue(0);
                                }
                                queryFinalized2.dispose();
                            } catch (Throwable e322) {
                                FileLog.m18e("tmessages", e322);
                            }
                        }
                    }
                }
                queryFinalized.dispose();
            }
            Collections.sort(tL_messages_messages.messages, new DownloadMessagesStorage(this));
            if ((this.f818e == 3 || (this.f818e == 2 && z)) && !tL_messages_messages.messages.isEmpty()) {
                i11 = ((Message) tL_messages_messages.messages.get(tL_messages_messages.messages.size() - 1)).id;
                i3 = ((Message) tL_messages_messages.messages.get(0)).id;
                if (i11 > intValue || i3 < intValue) {
                    arrayList4.clear();
                    arrayList2.clear();
                    arrayList3.clear();
                }
            }
            if (this.f818e != 3 || tL_messages_messages.messages.size() == 1) {
            }
            if (!arrayList4.isEmpty()) {
                queryFinalized = !hashMap.isEmpty() ? this.f822i.f847i.queryFinalized(String.format(Locale.US, "SELECT data, mid, date FROM messages WHERE mid IN(%s)", new Object[]{TextUtils.join(",", arrayList4)}), new Object[0]) : this.f822i.f847i.queryFinalized(String.format(Locale.US, "SELECT m.data, m.mid, m.date, r.random_id FROM randoms as r INNER JOIN messages as m ON r.mid = m.mid WHERE r.random_id IN(%s)", new Object[]{TextUtils.join(",", arrayList4)}), new Object[0]);
                while (queryFinalized.next()) {
                    byteBufferValue = queryFinalized.byteBufferValue(0);
                    if (byteBufferValue != null) {
                        Message TLdeserialize2 = Message.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                        byteBufferValue.reuse();
                        TLdeserialize2.id = queryFinalized.intValue(1);
                        TLdeserialize2.date = queryFinalized.intValue(2);
                        TLdeserialize2.dialog_id = this.f817d;
                        DownloadMessagesStorage.m794a(TLdeserialize2, arrayList2, arrayList3);
                        if (hashMap.isEmpty()) {
                            arrayList = (ArrayList) hashMap2.remove(Long.valueOf(queryFinalized.longValue(3)));
                            if (arrayList != null) {
                                for (i10 = 0; i10 < arrayList.size(); i10++) {
                                    Message message = (Message) arrayList.get(i10);
                                    message.replyMessage = TLdeserialize2;
                                    message.reply_to_msg_id = TLdeserialize2.id;
                                }
                            }
                        } else {
                            arrayList = (ArrayList) hashMap.get(Integer.valueOf(TLdeserialize2.id));
                            if (arrayList != null) {
                                for (i10 = 0; i10 < arrayList.size(); i10++) {
                                    ((Message) arrayList.get(i10)).replyMessage = TLdeserialize2;
                                }
                            }
                        }
                    }
                }
                queryFinalized.dispose();
                if (!hashMap2.isEmpty()) {
                    for (Entry value : hashMap2.entrySet()) {
                        arrayList = (ArrayList) value.getValue();
                        for (i10 = 0; i10 < arrayList.size(); i10++) {
                            ((Message) arrayList.get(i10)).reply_to_random_id = 0;
                        }
                    }
                }
            }
            if (!arrayList2.isEmpty()) {
                this.f822i.m812a(TextUtils.join(",", arrayList2), tL_messages_messages.users);
            }
            if (!arrayList3.isEmpty()) {
                this.f822i.m820b(TextUtils.join(",", arrayList3), tL_messages_messages.chats);
            }
            MessagesController.getInstance().processLoadedMessages(tL_messages_messages, this.f817d, i2, this.f815b, true, this.f820g, i4, i5, i, i6, this.f818e, false, z2, this.f821h, z);
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.download.c.7 */
    class DownloadMessagesStorage implements Runnable {
        final /* synthetic */ ArrayList f823a;
        final /* synthetic */ ArrayList f824b;
        final /* synthetic */ boolean f825c;
        final /* synthetic */ DownloadMessagesStorage f826d;

        DownloadMessagesStorage(DownloadMessagesStorage downloadMessagesStorage, ArrayList arrayList, ArrayList arrayList2, boolean z) {
            this.f826d = downloadMessagesStorage;
            this.f823a = arrayList;
            this.f824b = arrayList2;
            this.f825c = z;
        }

        public void run() {
            this.f826d.m801a(this.f823a, this.f824b, this.f825c);
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.download.c.8 */
    class DownloadMessagesStorage implements Runnable {
        final /* synthetic */ ArrayList f827a;
        final /* synthetic */ int f828b;
        final /* synthetic */ DownloadMessagesStorage f829c;

        DownloadMessagesStorage(DownloadMessagesStorage downloadMessagesStorage, ArrayList arrayList, int i) {
            this.f829c = downloadMessagesStorage;
            this.f827a = arrayList;
            this.f828b = i;
        }

        public void run() {
            this.f829c.m799a(this.f827a, this.f828b);
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.download.c.9 */
    class DownloadMessagesStorage implements Runnable {
        final /* synthetic */ ArrayList f830a;
        final /* synthetic */ int f831b;
        final /* synthetic */ boolean f832c;
        final /* synthetic */ DownloadMessagesStorage f833d;

        DownloadMessagesStorage(DownloadMessagesStorage downloadMessagesStorage, ArrayList arrayList, int i, boolean z) {
            this.f833d = downloadMessagesStorage;
            this.f830a = arrayList;
            this.f831b = i;
            this.f832c = z;
        }

        public void run() {
            this.f833d.m800a(this.f830a, this.f831b, this.f832c);
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.download.c.a */
    private class DownloadMessagesStorage {
        public int f834a;
        public int f835b;
        public int f836c;
        final /* synthetic */ DownloadMessagesStorage f837d;

        public DownloadMessagesStorage(DownloadMessagesStorage downloadMessagesStorage, int i, int i2) {
            this.f837d = downloadMessagesStorage;
            this.f834a = i;
            this.f835b = i2;
        }

        public DownloadMessagesStorage(DownloadMessagesStorage downloadMessagesStorage, int i, int i2, int i3) {
            this.f837d = downloadMessagesStorage;
            this.f836c = i;
            this.f834a = i2;
            this.f835b = i3;
        }
    }

    static {
        f838a = 0;
        f839b = 0;
        f840c = 0;
        f841d = 0;
        f842e = 0;
        f843f = null;
        f844g = 0;
        f845k = null;
    }

    public DownloadMessagesStorage() {
        this.f846h = new DispatchQueue("downloadStorageQueue");
        this.f846h.setPriority(10);
        m821c();
    }

    public static DownloadMessagesStorage m783a() {
        boolean z = !new File(ApplicationLoader.getFilesDirFixed(), "downloads.db").exists();
        DownloadMessagesStorage downloadMessagesStorage = f845k;
        if (downloadMessagesStorage == null) {
            synchronized (DownloadMessagesStorage.class) {
                downloadMessagesStorage = f845k;
                if (downloadMessagesStorage == null) {
                    downloadMessagesStorage = new DownloadMessagesStorage();
                    f845k = downloadMessagesStorage;
                }
            }
        }
        if (z) {
            TL_chat tL_chat = new TL_chat();
            tL_chat.id = 1;
            tL_chat.title = "downloads";
            tL_chat.photo = new TL_chatPhotoEmpty();
            tL_chat.participants_count = 0;
            tL_chat.date = (int) (System.currentTimeMillis() / 1000);
            tL_chat.version = 1;
            ArrayList arrayList = new ArrayList();
            arrayList.add(tL_chat);
            downloadMessagesStorage.m814a(null, arrayList, true, true);
        }
        return downloadMessagesStorage;
    }

    private String m785a(User user) {
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

    private void m793a(Message message) {
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

    public static void m794a(Message message, ArrayList<Integer> arrayList, ArrayList<Integer> arrayList2) {
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
                for (int i = 0; i < message.action.users.size(); i++) {
                    Integer num = (Integer) message.action.users.get(i);
                    if (!arrayList.contains(num)) {
                        arrayList.add(num);
                    }
                }
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

    private void m795a(String str) {
        try {
            SQLiteCursor queryFinalized = this.f847i.queryFinalized(String.format(Locale.US, "SELECT uid, data, read_state FROM messages WHERE mid IN(%s)", new Object[]{str}), new Object[0]);
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

    private void m796a(String str, long j, int i) {
        if (i == 0) {
            this.f847i.executeFast(String.format(Locale.US, "DELETE FROM " + str + " WHERE uid = %d", new Object[]{Long.valueOf(j)})).stepThis().dispose();
        } else {
            this.f847i.executeFast(String.format(Locale.US, "DELETE FROM " + str + " WHERE uid = %d AND start = 0", new Object[]{Long.valueOf(j)})).stepThis().dispose();
        }
        SQLitePreparedStatement executeFast = this.f847i.executeFast("REPLACE INTO " + str + " VALUES(?, ?, ?)");
        executeFast.requery();
        executeFast.bindLong(1, j);
        executeFast.bindInteger(2, 1);
        executeFast.bindInteger(3, 1);
        executeFast.step();
        executeFast.dispose();
    }

    private void m797a(String str, long j, int i, int i2) {
        SQLiteCursor queryFinalized = this.f847i.queryFinalized(String.format(Locale.US, "SELECT start, end FROM " + str + " WHERE uid = %d AND ((end >= %d AND end <= %d) OR (start >= %d AND start <= %d) OR (start >= %d AND end <= %d) OR (start <= %d AND end >= %d))", new Object[]{Long.valueOf(j), Integer.valueOf(i), Integer.valueOf(i2), Integer.valueOf(i), Integer.valueOf(i2), Integer.valueOf(i), Integer.valueOf(i2), Integer.valueOf(i), Integer.valueOf(i2)}), new Object[0]);
        ArrayList arrayList = null;
        while (queryFinalized.next()) {
            ArrayList arrayList2 = arrayList == null ? new ArrayList() : arrayList;
            int intValue = queryFinalized.intValue(0);
            int intValue2 = queryFinalized.intValue(1);
            if (intValue == intValue2 && intValue == 1) {
                arrayList = arrayList2;
            } else {
                arrayList2.add(new DownloadMessagesStorage(this, intValue, intValue2));
                arrayList = arrayList2;
            }
        }
        queryFinalized.dispose();
        if (arrayList != null) {
            for (int i3 = 0; i3 < arrayList.size(); i3++) {
                DownloadMessagesStorage downloadMessagesStorage = (DownloadMessagesStorage) arrayList.get(i3);
                if (i2 >= downloadMessagesStorage.f835b - 1 && i <= downloadMessagesStorage.f834a + 1) {
                    this.f847i.executeFast(String.format(Locale.US, "DELETE FROM " + str + " WHERE uid = %d AND start = %d AND end = %d", new Object[]{Long.valueOf(j), Integer.valueOf(downloadMessagesStorage.f834a), Integer.valueOf(downloadMessagesStorage.f835b)})).stepThis().dispose();
                } else if (i2 < downloadMessagesStorage.f835b - 1) {
                    try {
                        if (i > downloadMessagesStorage.f834a + 1) {
                            this.f847i.executeFast(String.format(Locale.US, "DELETE FROM " + str + " WHERE uid = %d AND start = %d AND end = %d", new Object[]{Long.valueOf(j), Integer.valueOf(downloadMessagesStorage.f834a), Integer.valueOf(downloadMessagesStorage.f835b)})).stepThis().dispose();
                            SQLitePreparedStatement executeFast = this.f847i.executeFast("REPLACE INTO " + str + " VALUES(?, ?, ?)");
                            executeFast.requery();
                            executeFast.bindLong(1, j);
                            executeFast.bindInteger(2, downloadMessagesStorage.f834a);
                            executeFast.bindInteger(3, i);
                            executeFast.step();
                            executeFast.requery();
                            executeFast.bindLong(1, j);
                            executeFast.bindInteger(2, i2);
                            executeFast.bindInteger(3, downloadMessagesStorage.f835b);
                            executeFast.step();
                            executeFast.dispose();
                        } else if (downloadMessagesStorage.f834a != i2) {
                            try {
                                this.f847i.executeFast(String.format(Locale.US, "UPDATE " + str + " SET start = %d WHERE uid = %d AND start = %d AND end = %d", new Object[]{Integer.valueOf(i2), Long.valueOf(j), Integer.valueOf(downloadMessagesStorage.f834a), Integer.valueOf(downloadMessagesStorage.f835b)})).stepThis().dispose();
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
                } else if (downloadMessagesStorage.f835b != i) {
                    try {
                        this.f847i.executeFast(String.format(Locale.US, "UPDATE " + str + " SET end = %d WHERE uid = %d AND start = %d AND end = %d", new Object[]{Integer.valueOf(i), Long.valueOf(j), Integer.valueOf(downloadMessagesStorage.f834a), Integer.valueOf(downloadMessagesStorage.f835b)})).stepThis().dispose();
                    } catch (Throwable e22) {
                        FileLog.m18e("tmessages", e22);
                    }
                } else {
                    continue;
                }
            }
        }
    }

    private void m798a(ArrayList<User> arrayList) {
        if (arrayList != null && !arrayList.isEmpty()) {
            SQLitePreparedStatement executeFast = this.f847i.executeFast("REPLACE INTO users VALUES(?, ?, ?, ?)");
            for (int i = 0; i < arrayList.size(); i++) {
                User user = (User) arrayList.get(i);
                if (user.min) {
                    SQLiteCursor queryFinalized = this.f847i.queryFinalized(String.format(Locale.US, "SELECT data FROM users WHERE uid = %d", new Object[]{Integer.valueOf(user.id)}), new Object[0]);
                    if (queryFinalized.next()) {
                        try {
                            AbstractSerializedData byteBufferValue = queryFinalized.byteBufferValue(0);
                            if (byteBufferValue != null) {
                                User TLdeserialize = User.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                                byteBufferValue.reuse();
                                if (user != null) {
                                    if (user.first_name != null) {
                                        TLdeserialize.first_name = user.first_name;
                                        TLdeserialize.flags |= 2;
                                    } else {
                                        TLdeserialize.first_name = null;
                                        TLdeserialize.flags &= -3;
                                    }
                                    if (user.last_name != null) {
                                        TLdeserialize.last_name = user.last_name;
                                        TLdeserialize.flags |= 4;
                                    } else {
                                        TLdeserialize.last_name = null;
                                        TLdeserialize.flags &= -5;
                                    }
                                    if (user.photo != null) {
                                        TLdeserialize.photo = user.photo;
                                        TLdeserialize.flags |= 32;
                                    } else {
                                        TLdeserialize.photo = null;
                                        TLdeserialize.flags &= -33;
                                    }
                                }
                                user = TLdeserialize;
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
                executeFast.bindString(2, m785a(user));
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

    private void m799a(ArrayList<Integer> arrayList, int i) {
        if (Thread.currentThread().getId() != this.f846h.getId()) {
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
                    executeFast = this.f847i.executeFast("UPDATE dialogs SET last_mid = (SELECT mid FROM messages WHERE uid = ? AND date = (SELECT MAX(date) FROM messages WHERE uid = ? )) WHERE did = ?");
                } else {
                    str = TextUtils.join(",", arrayList);
                    SQLiteCursor queryFinalized = this.f847i.queryFinalized(String.format(Locale.US, "SELECT did FROM dialogs WHERE last_mid IN(%s)", new Object[]{str}), new Object[0]);
                    while (queryFinalized.next()) {
                        arrayList2.add(Long.valueOf(queryFinalized.longValue(0)));
                    }
                    queryFinalized.dispose();
                    executeFast = this.f847i.executeFast("UPDATE dialogs SET unread_count = 0, unread_count_i = 0, last_mid = (SELECT mid FROM messages WHERE uid = ? AND date = (SELECT MAX(date) FROM messages WHERE uid = ? AND date != 0)) WHERE did = ?");
                }
                this.f847i.beginTransaction();
                for (int i2 = 0; i2 < arrayList2.size(); i2++) {
                    long longValue = ((Long) arrayList2.get(i2)).longValue();
                    executeFast.requery();
                    executeFast.bindLong(1, longValue);
                    executeFast.bindLong(2, longValue);
                    executeFast.bindLong(3, longValue);
                    executeFast.step();
                }
                executeFast.dispose();
                this.f847i.commitTransaction();
                str = TextUtils.join(",", arrayList2);
            }
            messages_Dialogs com_hanista_mobogram_tgnet_TLRPC_messages_Dialogs = new messages_Dialogs();
            ArrayList arrayList3 = new ArrayList();
            ArrayList arrayList4 = new ArrayList();
            ArrayList arrayList5 = new ArrayList();
            Iterable arrayList6 = new ArrayList();
            SQLiteCursor queryFinalized2 = this.f847i.queryFinalized(String.format(Locale.US, "SELECT d.did, d.last_mid, d.unread_count, d.date, m.data, m.read_state, m.mid, m.send_state, m.date, d.last_mid_i, d.unread_count_i, d.pts, d.inbox_max FROM dialogs as d LEFT JOIN messages as m ON d.last_mid = m.mid WHERE d.did IN(%s)", new Object[]{str}), new Object[0]);
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
                AbstractSerializedData byteBufferValue = queryFinalized2.byteBufferValue(1);
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
                    DownloadMessagesStorage.m794a(TLdeserialize, arrayList4, arrayList5);
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
                m813a(TextUtils.join(",", arrayList6), arrayList3, arrayList4);
            }
            if (!arrayList5.isEmpty()) {
                m820b(TextUtils.join(",", arrayList5), com_hanista_mobogram_tgnet_TLRPC_messages_Dialogs.chats);
            }
            if (!arrayList4.isEmpty()) {
                m812a(TextUtils.join(",", arrayList4), com_hanista_mobogram_tgnet_TLRPC_messages_Dialogs.users);
            }
            if (!com_hanista_mobogram_tgnet_TLRPC_messages_Dialogs.dialogs.isEmpty() || !arrayList3.isEmpty()) {
                MessagesController.getInstance().processDialogsUpdate(com_hanista_mobogram_tgnet_TLRPC_messages_Dialogs, arrayList3);
            }
        } catch (Throwable e) {
            FileLog.m18e("tmessages", e);
        }
    }

    private void m800a(ArrayList<Integer> arrayList, int i, boolean z) {
        String stringBuilder;
        if (i != 0) {
            try {
                StringBuilder stringBuilder2 = new StringBuilder(arrayList.size());
                for (int i2 = 0; i2 < arrayList.size(); i2++) {
                    long intValue = ((long) ((Integer) arrayList.get(i2)).intValue()) | (((long) i) << 32);
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
        SQLiteCursor queryFinalized = this.f847i.queryFinalized(String.format(Locale.US, "SELECT uid, data, read_state FROM messages WHERE mid IN(%s)", new Object[]{stringBuilder}), new Object[0]);
        ArrayList arrayList2 = new ArrayList();
        int i3 = 0;
        while (queryFinalized.next()) {
            long longValue = queryFinalized.longValue(0);
            int i4 = (i == 0 || queryFinalized.intValue(2) != 0) ? i3 : i3 + 1;
            if (((int) longValue) != 0) {
                i3 = i4;
            } else {
                AbstractSerializedData byteBufferValue = queryFinalized.byteBufferValue(2);
                if (byteBufferValue != null) {
                    Message TLdeserialize = Message.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                    byteBufferValue.reuse();
                    if (TLdeserialize == null) {
                        i3 = i4;
                    } else if (TLdeserialize.media == null) {
                        i3 = i4;
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
                }
                i3 = i4;
            }
        }
        queryFinalized.dispose();
        FileLoader.getInstance().deleteFiles(arrayList2, 0);
        if (z) {
            m795a(stringBuilder);
        }
        this.f847i.executeFast(String.format(Locale.US, "DELETE FROM messages WHERE mid IN(%s)", new Object[]{stringBuilder})).stepThis().dispose();
        this.f847i.executeFast(String.format(Locale.US, "DELETE FROM bot_keyboard WHERE mid IN(%s)", new Object[]{stringBuilder})).stepThis().dispose();
        this.f847i.executeFast(String.format(Locale.US, "DELETE FROM messages_seq WHERE mid IN(%s)", new Object[]{stringBuilder})).stepThis().dispose();
        this.f847i.executeFast(String.format(Locale.US, "DELETE FROM media_v2 WHERE mid IN(%s)", new Object[]{stringBuilder})).stepThis().dispose();
        this.f847i.executeFast("DELETE FROM media_counts_v2 WHERE 1").stepThis().dispose();
        BotQuery.clearBotKeyboard(0, arrayList);
    }

    private void m801a(ArrayList<User> arrayList, ArrayList<Chat> arrayList2, boolean z) {
        if (z) {
            try {
                this.f847i.beginTransaction();
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
                return;
            }
        }
        m798a((ArrayList) arrayList);
        m804b((ArrayList) arrayList2);
        if (z) {
            this.f847i.commitTransaction();
        }
    }

    private void m804b(ArrayList<Chat> arrayList) {
        if (arrayList != null && !arrayList.isEmpty()) {
            SQLitePreparedStatement executeFast = this.f847i.executeFast("REPLACE INTO chats VALUES(?, ?, ?)");
            for (int i = 0; i < arrayList.size(); i++) {
                Chat chat = (Chat) arrayList.get(i);
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

    public void m806a(int i) {
        this.f846h.postRunnable(new DownloadMessagesStorage(this, i));
    }

    public void m807a(int i, Semaphore semaphore, boolean z, boolean z2) {
        this.f846h.postRunnable(new DownloadMessagesStorage(this, i, semaphore, z, z2));
    }

    public void m808a(long j, int i, int i2) {
        int i3 = 0;
        if (i2 == -1) {
            if (i == 0) {
                this.f847i.executeFast(String.format(Locale.US, "DELETE FROM media_holes_v2 WHERE uid = %d", new Object[]{Long.valueOf(j)})).stepThis().dispose();
            } else {
                this.f847i.executeFast(String.format(Locale.US, "DELETE FROM media_holes_v2 WHERE uid = %d AND start = 0", new Object[]{Long.valueOf(j)})).stepThis().dispose();
            }
            SQLitePreparedStatement executeFast = this.f847i.executeFast("REPLACE INTO media_holes_v2 VALUES(?, ?, ?, ?)");
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
            this.f847i.executeFast(String.format(Locale.US, "DELETE FROM media_holes_v2 WHERE uid = %d AND type = %d", new Object[]{Long.valueOf(j), Integer.valueOf(i2)})).stepThis().dispose();
        } else {
            this.f847i.executeFast(String.format(Locale.US, "DELETE FROM media_holes_v2 WHERE uid = %d AND type = %d AND start = 0", new Object[]{Long.valueOf(j), Integer.valueOf(i2)})).stepThis().dispose();
        }
        SQLitePreparedStatement executeFast2 = this.f847i.executeFast("REPLACE INTO media_holes_v2 VALUES(?, ?, ?, ?)");
        executeFast2.requery();
        executeFast2.bindLong(1, j);
        executeFast2.bindInteger(2, i2);
        executeFast2.bindInteger(3, 1);
        executeFast2.bindInteger(4, 1);
        executeFast2.step();
        executeFast2.dispose();
    }

    public void m809a(long j, int i, int i2, int i3) {
        SQLiteCursor queryFinalized;
        if (i3 < 0) {
            queryFinalized = this.f847i.queryFinalized(String.format(Locale.US, "SELECT type, start, end FROM media_holes_v2 WHERE uid = %d AND type >= 0 AND ((end >= %d AND end <= %d) OR (start >= %d AND start <= %d) OR (start >= %d AND end <= %d) OR (start <= %d AND end >= %d))", new Object[]{Long.valueOf(j), Integer.valueOf(i), Integer.valueOf(i2), Integer.valueOf(i), Integer.valueOf(i2), Integer.valueOf(i), Integer.valueOf(i2), Integer.valueOf(i), Integer.valueOf(i2)}), new Object[0]);
        } else {
            queryFinalized = this.f847i.queryFinalized(String.format(Locale.US, "SELECT type, start, end FROM media_holes_v2 WHERE uid = %d AND type = %d AND ((end >= %d AND end <= %d) OR (start >= %d AND start <= %d) OR (start >= %d AND end <= %d) OR (start <= %d AND end >= %d))", new Object[]{Long.valueOf(j), Integer.valueOf(i3), Integer.valueOf(i), Integer.valueOf(i2), Integer.valueOf(i), Integer.valueOf(i2), Integer.valueOf(i), Integer.valueOf(i2), Integer.valueOf(i), Integer.valueOf(i2)}), new Object[0]);
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
                arrayList.add(new DownloadMessagesStorage(this, intValue, intValue2, intValue3));
            }
        }
        queryFinalized.dispose();
        if (arrayList != null) {
            for (int i4 = 0; i4 < arrayList.size(); i4++) {
                DownloadMessagesStorage downloadMessagesStorage = (DownloadMessagesStorage) arrayList.get(i4);
                if (i2 >= downloadMessagesStorage.f835b - 1 && i <= downloadMessagesStorage.f834a + 1) {
                    this.f847i.executeFast(String.format(Locale.US, "DELETE FROM media_holes_v2 WHERE uid = %d AND type = %d AND start = %d AND end = %d", new Object[]{Long.valueOf(j), Integer.valueOf(downloadMessagesStorage.f836c), Integer.valueOf(downloadMessagesStorage.f834a), Integer.valueOf(downloadMessagesStorage.f835b)})).stepThis().dispose();
                } else if (i2 >= downloadMessagesStorage.f835b - 1) {
                    if (downloadMessagesStorage.f835b != i) {
                        try {
                            this.f847i.executeFast(String.format(Locale.US, "UPDATE media_holes_v2 SET end = %d WHERE uid = %d AND type = %d AND start = %d AND end = %d", new Object[]{Integer.valueOf(i), Long.valueOf(j), Integer.valueOf(downloadMessagesStorage.f836c), Integer.valueOf(downloadMessagesStorage.f834a), Integer.valueOf(downloadMessagesStorage.f835b)})).stepThis().dispose();
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
                } else if (i > downloadMessagesStorage.f834a + 1) {
                    this.f847i.executeFast(String.format(Locale.US, "DELETE FROM media_holes_v2 WHERE uid = %d AND type = %d AND start = %d AND end = %d", new Object[]{Long.valueOf(j), Integer.valueOf(downloadMessagesStorage.f836c), Integer.valueOf(downloadMessagesStorage.f834a), Integer.valueOf(downloadMessagesStorage.f835b)})).stepThis().dispose();
                    SQLitePreparedStatement executeFast = this.f847i.executeFast("REPLACE INTO media_holes_v2 VALUES(?, ?, ?, ?)");
                    executeFast.requery();
                    executeFast.bindLong(1, j);
                    executeFast.bindInteger(2, downloadMessagesStorage.f836c);
                    executeFast.bindInteger(3, downloadMessagesStorage.f834a);
                    executeFast.bindInteger(4, i);
                    executeFast.step();
                    executeFast.requery();
                    executeFast.bindLong(1, j);
                    executeFast.bindInteger(2, downloadMessagesStorage.f836c);
                    executeFast.bindInteger(3, i2);
                    executeFast.bindInteger(4, downloadMessagesStorage.f835b);
                    executeFast.step();
                    executeFast.dispose();
                } else if (downloadMessagesStorage.f834a != i2) {
                    try {
                        this.f847i.executeFast(String.format(Locale.US, "UPDATE media_holes_v2 SET start = %d WHERE uid = %d AND type = %d AND start = %d AND end = %d", new Object[]{Integer.valueOf(i2), Long.valueOf(j), Integer.valueOf(downloadMessagesStorage.f836c), Integer.valueOf(downloadMessagesStorage.f834a), Integer.valueOf(downloadMessagesStorage.f835b)})).stepThis().dispose();
                    } catch (Throwable e22) {
                        FileLog.m18e("tmessages", e22);
                    }
                } else {
                    continue;
                }
            }
        }
    }

    public void m810a(long j, int i, int i2, int i3, int i4, int i5, int i6, int i7) {
        this.f846h.postRunnable(new DownloadMessagesStorage(this, i, i2, i6, j, i5, i3, i4, i7));
    }

    public void m811a(messages_Messages com_hanista_mobogram_tgnet_TLRPC_messages_Messages, long j, int i, int i2, int i3, boolean z) {
        this.f846h.postRunnable(new DownloadMessagesStorage(this, com_hanista_mobogram_tgnet_TLRPC_messages_Messages, i, i3, j, i2, z));
    }

    public void m812a(String str, ArrayList<User> arrayList) {
        if (str != null && str.length() != 0 && arrayList != null) {
            SQLiteCursor queryFinalized = this.f847i.queryFinalized(String.format(Locale.US, "SELECT data, status FROM users WHERE uid IN(%s)", new Object[]{str}), new Object[0]);
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

    public void m813a(String str, ArrayList<EncryptedChat> arrayList, ArrayList<Integer> arrayList2) {
        if (str != null && str.length() != 0 && arrayList != null) {
            SQLiteCursor queryFinalized = this.f847i.queryFinalized(String.format(Locale.US, "SELECT data, user, g, authkey, ttl, layer, seq_in, seq_out, use_count, exchange_id, key_date, fprint, fauthkey, khash FROM enc_chats WHERE uid IN(%s)", new Object[]{str}), new Object[0]);
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

    public void m814a(ArrayList<User> arrayList, ArrayList<Chat> arrayList2, boolean z, boolean z2) {
        if (arrayList != null && arrayList.isEmpty() && arrayList2 != null && arrayList2.isEmpty()) {
            return;
        }
        if (z2) {
            this.f846h.postRunnable(new DownloadMessagesStorage(this, arrayList, arrayList2, z));
        } else {
            m801a((ArrayList) arrayList, (ArrayList) arrayList2, z);
        }
    }

    public void m815a(ArrayList<Integer> arrayList, boolean z, int i) {
        if (!arrayList.isEmpty() || i != 0) {
            if (z) {
                this.f846h.postRunnable(new DownloadMessagesStorage(this, arrayList, i));
            } else {
                m799a((ArrayList) arrayList, i);
            }
        }
    }

    public void m816a(ArrayList<Integer> arrayList, boolean z, int i, boolean z2) {
        if (!arrayList.isEmpty()) {
            if (z) {
                this.f846h.postRunnable(new DownloadMessagesStorage(this, arrayList, i, z2));
            } else {
                m800a((ArrayList) arrayList, i, z2);
            }
        }
    }

    public void m817a(boolean z) {
        this.f846h.cleanupQueue();
        this.f846h.postRunnable(new DownloadMessagesStorage(this, z));
    }

    public DispatchQueue m818b() {
        return this.f846h;
    }

    public Chat m819b(int i) {
        try {
            ArrayList arrayList = new ArrayList();
            m820b(TtmlNode.ANONYMOUS_REGION_ID + i, arrayList);
            if (!arrayList.isEmpty()) {
                return (Chat) arrayList.get(0);
            }
        } catch (Throwable e) {
            FileLog.m18e("tmessages", e);
        }
        return null;
    }

    public void m820b(String str, ArrayList<Chat> arrayList) {
        if (str != null && str.length() != 0 && arrayList != null) {
            SQLiteCursor queryFinalized = this.f847i.queryFinalized(String.format(Locale.US, "SELECT data FROM chats WHERE uid IN(%s)", new Object[]{str}), new Object[0]);
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

    public void m821c() {
        Object obj = null;
        this.f848j = new File(ApplicationLoader.getFilesDirFixed(), "downloads.db");
        if (!this.f848j.exists()) {
            obj = 1;
        }
        try {
            this.f847i = new SQLiteDatabase(this.f848j.getPath());
            this.f847i.executeFast("PRAGMA secure_delete = ON").stepThis().dispose();
            this.f847i.executeFast("PRAGMA temp_store = 1").stepThis().dispose();
            if (obj != null) {
                this.f847i.executeFast("CREATE TABLE channel_group(uid INTEGER, start INTEGER, end INTEGER, count INTEGER, PRIMARY KEY(uid, start));").stepThis().dispose();
                this.f847i.executeFast("CREATE TABLE messages_holes(uid INTEGER, start INTEGER, end INTEGER, PRIMARY KEY(uid, start));").stepThis().dispose();
                this.f847i.executeFast("CREATE INDEX IF NOT EXISTS uid_end_messages_holes ON messages_holes(uid, end);").stepThis().dispose();
                this.f847i.executeFast("CREATE TABLE messages_imp_holes(uid INTEGER, start INTEGER, end INTEGER, PRIMARY KEY(uid, start));").stepThis().dispose();
                this.f847i.executeFast("CREATE INDEX IF NOT EXISTS uid_end_messages_imp_holes ON messages_imp_holes(uid, end);").stepThis().dispose();
                this.f847i.executeFast("CREATE TABLE media_holes_v2(uid INTEGER, type INTEGER, start INTEGER, end INTEGER, PRIMARY KEY(uid, type, start));").stepThis().dispose();
                this.f847i.executeFast("CREATE INDEX IF NOT EXISTS uid_end_media_holes_v2 ON media_holes_v2(uid, type, end);").stepThis().dispose();
                this.f847i.executeFast("CREATE TABLE messages(mid INTEGER PRIMARY KEY, uid INTEGER, read_state INTEGER, send_state INTEGER, date INTEGER, data BLOB, out INTEGER, ttl INTEGER, media INTEGER, replydata BLOB, imp INTEGER)").stepThis().dispose();
                this.f847i.executeFast("CREATE INDEX IF NOT EXISTS uid_mid_idx_messages ON messages(uid, mid);").stepThis().dispose();
                this.f847i.executeFast("CREATE INDEX IF NOT EXISTS uid_date_mid_idx_messages ON messages(uid, date, mid);").stepThis().dispose();
                this.f847i.executeFast("CREATE INDEX IF NOT EXISTS uid_mid_idx_imp_messages ON messages(uid, mid, imp) WHERE imp = 1;").stepThis().dispose();
                this.f847i.executeFast("CREATE INDEX IF NOT EXISTS uid_date_mid_imp_idx_messages ON messages(uid, date, mid, imp) WHERE imp = 1;").stepThis().dispose();
                this.f847i.executeFast("CREATE INDEX IF NOT EXISTS mid_out_idx_messages ON messages(mid, out);").stepThis().dispose();
                this.f847i.executeFast("CREATE INDEX IF NOT EXISTS task_idx_messages ON messages(uid, out, read_state, ttl, date, send_state);").stepThis().dispose();
                this.f847i.executeFast("CREATE INDEX IF NOT EXISTS send_state_idx_messages ON messages(mid, send_state, date) WHERE mid < 0 AND send_state = 1;").stepThis().dispose();
                this.f847i.executeFast("CREATE TABLE download_queue(uid INTEGER, type INTEGER, date INTEGER, data BLOB, PRIMARY KEY (uid, type));").stepThis().dispose();
                this.f847i.executeFast("CREATE INDEX IF NOT EXISTS type_date_idx_download_queue ON download_queue(type, date);").stepThis().dispose();
                this.f847i.executeFast("CREATE TABLE user_phones_v6(uid INTEGER, phone TEXT, sphone TEXT, deleted INTEGER, PRIMARY KEY (uid, phone))").stepThis().dispose();
                this.f847i.executeFast("CREATE INDEX IF NOT EXISTS sphone_deleted_idx_user_phones ON user_phones_v6(sphone, deleted);").stepThis().dispose();
                this.f847i.executeFast("CREATE TABLE dialogs(did INTEGER PRIMARY KEY, date INTEGER, unread_count INTEGER, last_mid INTEGER, inbox_max INTEGER, outbox_max INTEGER, last_mid_i INTEGER, unread_count_i INTEGER, pts INTEGER, date_i INTEGER)").stepThis().dispose();
                this.f847i.executeFast("CREATE INDEX IF NOT EXISTS date_idx_dialogs ON dialogs(date);").stepThis().dispose();
                this.f847i.executeFast("CREATE INDEX IF NOT EXISTS last_mid_idx_dialogs ON dialogs(last_mid);").stepThis().dispose();
                this.f847i.executeFast("CREATE INDEX IF NOT EXISTS unread_count_idx_dialogs ON dialogs(unread_count);").stepThis().dispose();
                this.f847i.executeFast("CREATE INDEX IF NOT EXISTS last_mid_i_idx_dialogs ON dialogs(last_mid_i);").stepThis().dispose();
                this.f847i.executeFast("CREATE INDEX IF NOT EXISTS unread_count_i_idx_dialogs ON dialogs(unread_count_i);").stepThis().dispose();
                this.f847i.executeFast("CREATE TABLE randoms(random_id INTEGER, mid INTEGER, PRIMARY KEY (random_id, mid))").stepThis().dispose();
                this.f847i.executeFast("CREATE INDEX IF NOT EXISTS mid_idx_randoms ON randoms(mid);").stepThis().dispose();
                this.f847i.executeFast("CREATE TABLE enc_tasks_v2(mid INTEGER PRIMARY KEY, date INTEGER)").stepThis().dispose();
                this.f847i.executeFast("CREATE INDEX IF NOT EXISTS date_idx_enc_tasks_v2 ON enc_tasks_v2(date);").stepThis().dispose();
                this.f847i.executeFast("CREATE TABLE messages_seq(mid INTEGER PRIMARY KEY, seq_in INTEGER, seq_out INTEGER);").stepThis().dispose();
                this.f847i.executeFast("CREATE INDEX IF NOT EXISTS seq_idx_messages_seq ON messages_seq(seq_in, seq_out);").stepThis().dispose();
                this.f847i.executeFast("CREATE TABLE params(id INTEGER PRIMARY KEY, seq INTEGER, pts INTEGER, date INTEGER, qts INTEGER, lsv INTEGER, sg INTEGER, pbytes BLOB)").stepThis().dispose();
                this.f847i.executeFast("INSERT INTO params VALUES(1, 0, 0, 0, 0, 0, 0, NULL)").stepThis().dispose();
                this.f847i.executeFast("CREATE TABLE media_v2(mid INTEGER PRIMARY KEY, uid INTEGER, date INTEGER, type INTEGER, data BLOB)").stepThis().dispose();
                this.f847i.executeFast("CREATE INDEX IF NOT EXISTS uid_mid_type_date_idx_media ON media_v2(uid, mid, type, date);").stepThis().dispose();
                this.f847i.executeFast("CREATE TABLE bot_keyboard(uid INTEGER PRIMARY KEY, mid INTEGER, info BLOB)").stepThis().dispose();
                this.f847i.executeFast("CREATE INDEX IF NOT EXISTS bot_keyboard_idx_mid ON bot_keyboard(mid);").stepThis().dispose();
                this.f847i.executeFast("CREATE TABLE users(uid INTEGER PRIMARY KEY, name TEXT, status INTEGER, data BLOB)").stepThis().dispose();
                this.f847i.executeFast("CREATE TABLE chats(uid INTEGER PRIMARY KEY, name TEXT, data BLOB)").stepThis().dispose();
                this.f847i.executeFast("CREATE TABLE enc_chats(uid INTEGER PRIMARY KEY, user INTEGER, name TEXT, data BLOB, g BLOB, authkey BLOB, ttl INTEGER, layer INTEGER, seq_in INTEGER, seq_out INTEGER, use_count INTEGER, exchange_id INTEGER, key_date INTEGER, fprint INTEGER, fauthkey BLOB, khash BLOB)").stepThis().dispose();
                this.f847i.executeFast("CREATE TABLE chat_settings_v2(uid INTEGER PRIMARY KEY, info BLOB)").stepThis().dispose();
                this.f847i.executeFast("CREATE TABLE channel_users_v2(did INTEGER, uid INTEGER, date INTEGER, data BLOB, PRIMARY KEY(did, uid))").stepThis().dispose();
                this.f847i.executeFast("CREATE TABLE contacts(uid INTEGER PRIMARY KEY, mutual INTEGER)").stepThis().dispose();
                this.f847i.executeFast("CREATE TABLE pending_read(uid INTEGER PRIMARY KEY, max_id INTEGER)").stepThis().dispose();
                this.f847i.executeFast("CREATE TABLE wallpapers(uid INTEGER PRIMARY KEY, data BLOB)").stepThis().dispose();
                this.f847i.executeFast("CREATE TABLE user_photos(uid INTEGER, id INTEGER, data BLOB, PRIMARY KEY (uid, id))").stepThis().dispose();
                this.f847i.executeFast("CREATE TABLE blocked_users(uid INTEGER PRIMARY KEY)").stepThis().dispose();
                this.f847i.executeFast("CREATE TABLE dialog_settings(did INTEGER PRIMARY KEY, flags INTEGER);").stepThis().dispose();
                this.f847i.executeFast("CREATE TABLE web_recent_v3(id TEXT, type INTEGER, image_url TEXT, thumb_url TEXT, local_url TEXT, width INTEGER, height INTEGER, size INTEGER, date INTEGER, document BLOB, PRIMARY KEY (id, type));").stepThis().dispose();
                this.f847i.executeFast("CREATE TABLE bot_recent(id INTEGER PRIMARY KEY, date INTEGER);").stepThis().dispose();
                this.f847i.executeFast("CREATE TABLE stickers_v2(id INTEGER PRIMARY KEY, data BLOB, date INTEGER, hash TEXT);").stepThis().dispose();
                this.f847i.executeFast("CREATE TABLE hashtag_recent_v2(id TEXT PRIMARY KEY, date INTEGER);").stepThis().dispose();
                this.f847i.executeFast("CREATE TABLE webpage_pending(id INTEGER, mid INTEGER, PRIMARY KEY (id, mid));").stepThis().dispose();
                this.f847i.executeFast("CREATE TABLE user_contacts_v6(uid INTEGER PRIMARY KEY, fname TEXT, sname TEXT)").stepThis().dispose();
                this.f847i.executeFast("CREATE TABLE sent_files_v2(uid TEXT, type INTEGER, data BLOB, PRIMARY KEY (uid, type))").stepThis().dispose();
                this.f847i.executeFast("CREATE TABLE search_recent(did INTEGER PRIMARY KEY, date INTEGER);").stepThis().dispose();
                this.f847i.executeFast("CREATE TABLE media_counts_v2(uid INTEGER, type INTEGER, count INTEGER, PRIMARY KEY(uid, type))").stepThis().dispose();
                this.f847i.executeFast("CREATE TABLE keyvalue(id TEXT PRIMARY KEY, value TEXT)").stepThis().dispose();
                this.f847i.executeFast("CREATE TABLE bot_info(uid INTEGER PRIMARY KEY, info BLOB)").stepThis().dispose();
                this.f847i.executeFast("PRAGMA user_version = 32").stepThis().dispose();
            } else {
                try {
                    SQLiteCursor queryFinalized = this.f847i.queryFinalized("SELECT seq, pts, date, qts, lsv, sg, pbytes FROM params WHERE id = 1", new Object[0]);
                    if (queryFinalized.next()) {
                        f841d = queryFinalized.intValue(0);
                        f839b = queryFinalized.intValue(1);
                        f838a = queryFinalized.intValue(2);
                        f840c = queryFinalized.intValue(3);
                        f842e = queryFinalized.intValue(4);
                        f844g = queryFinalized.intValue(5);
                        if (queryFinalized.isNull(6)) {
                            f843f = null;
                        } else {
                            f843f = queryFinalized.byteArrayValue(6);
                            if (f843f != null && f843f.length == 1) {
                                f843f = null;
                            }
                        }
                    }
                    queryFinalized.dispose();
                } catch (Throwable e) {
                    FileLog.m18e("tmessages", e);
                    try {
                        this.f847i.executeFast("CREATE TABLE IF NOT EXISTS params(id INTEGER PRIMARY KEY, seq INTEGER, pts INTEGER, date INTEGER, qts INTEGER, lsv INTEGER, sg INTEGER, pbytes BLOB)").stepThis().dispose();
                        this.f847i.executeFast("INSERT INTO params VALUES(1, 0, 0, 0, 0, 0, 0, NULL)").stepThis().dispose();
                    } catch (Throwable e2) {
                        FileLog.m18e("tmessages", e2);
                    }
                }
                int intValue = this.f847i.executeInt("PRAGMA user_version", new Object[0]).intValue();
                if (intValue < 30) {
                    m806a(intValue);
                }
            }
        } catch (Throwable e22) {
            FileLog.m18e("tmessages", e22);
        }
        m822d();
    }

    public void m822d() {
        this.f846h.postRunnable(new DownloadMessagesStorage(this));
    }

    public void m823e() {
        try {
            this.f847i.executeFast("DELETE FROM messages").stepThis().dispose();
            this.f847i.executeFast("DELETE FROM media_v2").stepThis().dispose();
        } catch (Throwable e) {
            FileLog.m18e("tmessages", e);
        }
    }
}
