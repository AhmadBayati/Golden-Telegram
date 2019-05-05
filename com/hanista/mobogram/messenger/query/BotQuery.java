package com.hanista.mobogram.messenger.query;

import com.hanista.mobogram.SQLite.SQLiteCursor;
import com.hanista.mobogram.SQLite.SQLitePreparedStatement;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.FileLog;
import com.hanista.mobogram.messenger.MessagesStorage;
import com.hanista.mobogram.messenger.NotificationCenter;
import com.hanista.mobogram.tgnet.AbstractSerializedData;
import com.hanista.mobogram.tgnet.NativeByteBuffer;
import com.hanista.mobogram.tgnet.TLRPC.BotInfo;
import com.hanista.mobogram.tgnet.TLRPC.Message;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class BotQuery {
    private static HashMap<Integer, BotInfo> botInfos;
    private static HashMap<Long, Message> botKeyboards;
    private static HashMap<Integer, Long> botKeyboardsByMids;

    /* renamed from: com.hanista.mobogram.messenger.query.BotQuery.1 */
    static class C07601 implements Runnable {
        final /* synthetic */ long val$did;
        final /* synthetic */ ArrayList val$messages;

        C07601(ArrayList arrayList, long j) {
            this.val$messages = arrayList;
            this.val$did = j;
        }

        public void run() {
            if (this.val$messages != null) {
                for (int i = 0; i < this.val$messages.size(); i++) {
                    Long l = (Long) BotQuery.botKeyboardsByMids.get(this.val$messages.get(i));
                    if (l != null) {
                        BotQuery.botKeyboards.remove(l);
                        BotQuery.botKeyboardsByMids.remove(this.val$messages.get(i));
                        NotificationCenter.getInstance().postNotificationName(NotificationCenter.botKeyboardDidLoaded, null, l);
                    }
                }
                return;
            }
            BotQuery.botKeyboards.remove(Long.valueOf(this.val$did));
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.botKeyboardDidLoaded, null, Long.valueOf(this.val$did));
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.query.BotQuery.2 */
    static class C07622 implements Runnable {
        final /* synthetic */ long val$did;

        /* renamed from: com.hanista.mobogram.messenger.query.BotQuery.2.1 */
        class C07611 implements Runnable {
            final /* synthetic */ Message val$botKeyboardFinal;

            C07611(Message message) {
                this.val$botKeyboardFinal = message;
            }

            public void run() {
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.botKeyboardDidLoaded, this.val$botKeyboardFinal, Long.valueOf(C07622.this.val$did));
            }
        }

        C07622(long j) {
            this.val$did = j;
        }

        public void run() {
            Message message = null;
            try {
                SQLiteCursor queryFinalized = MessagesStorage.getInstance().getDatabase().queryFinalized(String.format(Locale.US, "SELECT info FROM bot_keyboard WHERE uid = %d", new Object[]{Long.valueOf(this.val$did)}), new Object[0]);
                if (queryFinalized.next() && !queryFinalized.isNull(0)) {
                    AbstractSerializedData byteBufferValue = queryFinalized.byteBufferValue(0);
                    if (byteBufferValue != null) {
                        message = Message.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                        byteBufferValue.reuse();
                    }
                }
                queryFinalized.dispose();
                if (message != null) {
                    AndroidUtilities.runOnUIThread(new C07611(message));
                }
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.query.BotQuery.3 */
    static class C07643 implements Runnable {
        final /* synthetic */ int val$classGuid;
        final /* synthetic */ int val$uid;

        /* renamed from: com.hanista.mobogram.messenger.query.BotQuery.3.1 */
        class C07631 implements Runnable {
            final /* synthetic */ BotInfo val$botInfoFinal;

            C07631(BotInfo botInfo) {
                this.val$botInfoFinal = botInfo;
            }

            public void run() {
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.botInfoDidLoaded, this.val$botInfoFinal, Integer.valueOf(C07643.this.val$classGuid));
            }
        }

        C07643(int i, int i2) {
            this.val$uid = i;
            this.val$classGuid = i2;
        }

        public void run() {
            BotInfo botInfo = null;
            try {
                SQLiteCursor queryFinalized = MessagesStorage.getInstance().getDatabase().queryFinalized(String.format(Locale.US, "SELECT info FROM bot_info WHERE uid = %d", new Object[]{Integer.valueOf(this.val$uid)}), new Object[0]);
                if (queryFinalized.next() && !queryFinalized.isNull(0)) {
                    AbstractSerializedData byteBufferValue = queryFinalized.byteBufferValue(0);
                    if (byteBufferValue != null) {
                        botInfo = BotInfo.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                        byteBufferValue.reuse();
                    }
                }
                queryFinalized.dispose();
                if (botInfo != null) {
                    AndroidUtilities.runOnUIThread(new C07631(botInfo));
                }
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.query.BotQuery.4 */
    static class C07654 implements Runnable {
        final /* synthetic */ long val$did;
        final /* synthetic */ Message val$message;

        C07654(long j, Message message) {
            this.val$did = j;
            this.val$message = message;
        }

        public void run() {
            Message message = (Message) BotQuery.botKeyboards.put(Long.valueOf(this.val$did), this.val$message);
            if (message != null) {
                BotQuery.botKeyboardsByMids.remove(Integer.valueOf(message.id));
            }
            BotQuery.botKeyboardsByMids.put(Integer.valueOf(this.val$message.id), Long.valueOf(this.val$did));
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.botKeyboardDidLoaded, this.val$message, Long.valueOf(this.val$did));
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.query.BotQuery.5 */
    static class C07665 implements Runnable {
        final /* synthetic */ BotInfo val$botInfo;

        C07665(BotInfo botInfo) {
            this.val$botInfo = botInfo;
        }

        public void run() {
            try {
                SQLitePreparedStatement executeFast = MessagesStorage.getInstance().getDatabase().executeFast("REPLACE INTO bot_info(uid, info) VALUES(?, ?)");
                executeFast.requery();
                NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(this.val$botInfo.getObjectSize());
                this.val$botInfo.serializeToStream(nativeByteBuffer);
                executeFast.bindInteger(1, this.val$botInfo.user_id);
                executeFast.bindByteBuffer(2, nativeByteBuffer);
                executeFast.step();
                nativeByteBuffer.reuse();
                executeFast.dispose();
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
        }
    }

    static {
        botInfos = new HashMap();
        botKeyboards = new HashMap();
        botKeyboardsByMids = new HashMap();
    }

    public static void cleanup() {
        botInfos.clear();
        botKeyboards.clear();
        botKeyboardsByMids.clear();
    }

    public static void clearBotKeyboard(long j, ArrayList<Integer> arrayList) {
        AndroidUtilities.runOnUIThread(new C07601(arrayList, j));
    }

    public static void loadBotInfo(int i, boolean z, int i2) {
        if (!z || ((BotInfo) botInfos.get(Integer.valueOf(i))) == null) {
            MessagesStorage.getInstance().getStorageQueue().postRunnable(new C07643(i, i2));
            return;
        }
        NotificationCenter.getInstance().postNotificationName(NotificationCenter.botInfoDidLoaded, r0, Integer.valueOf(i2));
    }

    public static void loadBotKeyboard(long j) {
        if (((Message) botKeyboards.get(Long.valueOf(j))) != null) {
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.botKeyboardDidLoaded, r0, Long.valueOf(j));
            return;
        }
        MessagesStorage.getInstance().getStorageQueue().postRunnable(new C07622(j));
    }

    public static void putBotInfo(BotInfo botInfo) {
        if (botInfo != null) {
            botInfos.put(Integer.valueOf(botInfo.user_id), botInfo);
            MessagesStorage.getInstance().getStorageQueue().postRunnable(new C07665(botInfo));
        }
    }

    public static void putBotKeyboard(long j, Message message) {
        int i = 0;
        if (message != null) {
            try {
                SQLiteCursor queryFinalized = MessagesStorage.getInstance().getDatabase().queryFinalized(String.format(Locale.US, "SELECT mid FROM bot_keyboard WHERE uid = %d", new Object[]{Long.valueOf(j)}), new Object[0]);
                if (queryFinalized.next()) {
                    i = queryFinalized.intValue(0);
                }
                queryFinalized.dispose();
                if (i < message.id) {
                    SQLitePreparedStatement executeFast = MessagesStorage.getInstance().getDatabase().executeFast("REPLACE INTO bot_keyboard VALUES(?, ?, ?)");
                    executeFast.requery();
                    NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(message.getObjectSize());
                    message.serializeToStream(nativeByteBuffer);
                    executeFast.bindLong(1, j);
                    executeFast.bindInteger(2, message.id);
                    executeFast.bindByteBuffer(3, nativeByteBuffer);
                    executeFast.step();
                    nativeByteBuffer.reuse();
                    executeFast.dispose();
                    AndroidUtilities.runOnUIThread(new C07654(j, message));
                }
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
        }
    }
}
