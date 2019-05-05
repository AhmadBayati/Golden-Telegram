package com.hanista.mobogram.mobo.p013m;

import android.text.TextUtils;
import com.hanista.mobogram.SQLite.SQLiteCursor;
import com.hanista.mobogram.SQLite.SQLiteDatabase;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.FileLog;
import com.hanista.mobogram.messenger.ImageLoader;
import com.hanista.mobogram.messenger.MessageObject;
import com.hanista.mobogram.messenger.MessagesController;
import com.hanista.mobogram.messenger.MessagesStorage;
import com.hanista.mobogram.messenger.NotificationCenter;
import com.hanista.mobogram.tgnet.AbstractSerializedData;
import com.hanista.mobogram.tgnet.TLRPC.Message;
import com.hanista.mobogram.tgnet.TLRPC.MessageEntity;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageEntityEmail;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageEntityTextUrl;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageEntityUrl;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageMediaDocument;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageMediaPhoto;
import com.hanista.mobogram.tgnet.TLRPC.TL_messages_messages;
import com.hanista.mobogram.tgnet.TLRPC.User;
import com.hanista.mobogram.tgnet.TLRPC.messages_Messages;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

/* renamed from: com.hanista.mobogram.mobo.m.b */
public class AllSharedMediaQuery {

    /* renamed from: com.hanista.mobogram.mobo.m.b.1 */
    static class AllSharedMediaQuery implements Runnable {
        final /* synthetic */ messages_Messages f1706a;
        final /* synthetic */ boolean f1707b;
        final /* synthetic */ ArrayList f1708c;
        final /* synthetic */ int f1709d;
        final /* synthetic */ int f1710e;
        final /* synthetic */ boolean f1711f;

        AllSharedMediaQuery(messages_Messages com_hanista_mobogram_tgnet_TLRPC_messages_Messages, boolean z, ArrayList arrayList, int i, int i2, boolean z2) {
            this.f1706a = com_hanista_mobogram_tgnet_TLRPC_messages_Messages;
            this.f1707b = z;
            this.f1708c = arrayList;
            this.f1709d = i;
            this.f1710e = i2;
            this.f1711f = z2;
        }

        public void run() {
            int i = this.f1706a.count;
            MessagesController.getInstance().putUsers(this.f1706a.users, this.f1707b);
            MessagesController.getInstance().putChats(this.f1706a.chats, this.f1707b);
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.mediaDidLoaded, Long.valueOf(0), Integer.valueOf(i), this.f1708c, Integer.valueOf(this.f1709d), Integer.valueOf(this.f1710e), Boolean.valueOf(this.f1711f));
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.m.b.2 */
    static class AllSharedMediaQuery implements Runnable {
        final /* synthetic */ int f1712a;
        final /* synthetic */ int f1713b;
        final /* synthetic */ int f1714c;
        final /* synthetic */ int f1715d;
        final /* synthetic */ int f1716e;

        AllSharedMediaQuery(int i, int i2, int i3, int i4, int i5) {
            this.f1712a = i;
            this.f1713b = i2;
            this.f1714c = i3;
            this.f1715d = i4;
            this.f1716e = i5;
        }

        public void run() {
            boolean z = false;
            messages_Messages tL_messages_messages = new TL_messages_messages();
            try {
                SQLiteCursor queryFinalized;
                Iterable arrayList = new ArrayList();
                Iterable arrayList2 = new ArrayList();
                int i = this.f1712a + 1;
                SQLiteDatabase database = MessagesStorage.getInstance().getDatabase();
                long j = (long) this.f1713b;
                if (this.f1714c == 5) {
                    queryFinalized = database.queryFinalized(String.format(Locale.US, "SELECT data, mid, uid FROM messages WHERE date < %d ORDER BY date DESC, mid DESC LIMIT %d", new Object[]{Long.valueOf(j), Integer.valueOf(i)}), new Object[0]);
                } else {
                    queryFinalized = database.queryFinalized(String.format(Locale.US, "SELECT data, mid, uid FROM media_v2 WHERE date < %d AND type = %d ORDER BY date DESC, mid DESC LIMIT %d", new Object[]{Long.valueOf(j), Integer.valueOf(this.f1714c), Integer.valueOf(i)}), new Object[0]);
                }
                while (queryFinalized.next()) {
                    AbstractSerializedData byteBufferValue = queryFinalized.byteBufferValue(0);
                    if (byteBufferValue != null) {
                        Message TLdeserialize = Message.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                        byteBufferValue.reuse();
                        TLdeserialize.id = queryFinalized.intValue(1);
                        TLdeserialize.dialog_id = queryFinalized.longValue(2);
                        if (TLdeserialize.dialog_id == 0) {
                            TLdeserialize.random_id = queryFinalized.longValue(2);
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
                if (tL_messages_messages.messages.size() > this.f1712a) {
                    tL_messages_messages.messages.remove(tL_messages_messages.messages.size() - 1);
                } else {
                    z = true;
                }
                AllSharedMediaQuery.m1690b(tL_messages_messages, this.f1715d, this.f1712a, this.f1713b, this.f1714c, true, this.f1716e, z);
            } catch (Throwable e) {
                tL_messages_messages.messages.clear();
                tL_messages_messages.chats.clear();
                tL_messages_messages.users.clear();
                FileLog.m18e("tmessages", e);
                AllSharedMediaQuery.m1690b(tL_messages_messages, this.f1715d, this.f1712a, this.f1713b, this.f1714c, true, this.f1716e, false);
            } catch (Throwable e2) {
                Throwable th = e2;
                AllSharedMediaQuery.m1690b(tL_messages_messages, this.f1715d, this.f1712a, this.f1713b, this.f1714c, true, this.f1716e, false);
            }
        }
    }

    public static int m1686a(Message message) {
        if (message == null) {
            return -1;
        }
        if ((message.media instanceof TL_messageMediaPhoto) || MessageObject.isVideoMessage(message)) {
            return 0;
        }
        if (MessageObject.isVoiceMessage(message)) {
            return 2;
        }
        if (message.media instanceof TL_messageMediaDocument) {
            return MessageObject.isStickerMessage(message) ? -1 : MessageObject.isMusicMessage(message) ? 4 : 1;
        } else {
            if (!message.entities.isEmpty()) {
                for (int i = 0; i < message.entities.size(); i++) {
                    MessageEntity messageEntity = (MessageEntity) message.entities.get(i);
                    if ((messageEntity instanceof TL_messageEntityUrl) || (messageEntity instanceof TL_messageEntityTextUrl) || (messageEntity instanceof TL_messageEntityEmail)) {
                        return 3;
                    }
                }
            }
            return -1;
        }
    }

    private static void m1687a(int i, int i2, int i3, int i4, int i5) {
        MessagesStorage.getInstance().getStorageQueue().postRunnable(new AllSharedMediaQuery(i2, i3, i4, i, i5));
    }

    public static void m1688a(int i, int i2, int i3, int i4, boolean z, int i5) {
        if (z) {
            AllSharedMediaQuery.m1687a(i, i2, i3, i4, i5);
        } else {
            AllSharedMediaQuery.m1690b(new TL_messages_messages(), i, i2, i3, i4, false, i5, true);
        }
    }

    private static void m1690b(messages_Messages com_hanista_mobogram_tgnet_TLRPC_messages_Messages, int i, int i2, int i3, int i4, boolean z, int i5, boolean z2) {
        int i6 = 0;
        if (z && com_hanista_mobogram_tgnet_TLRPC_messages_Messages.messages.isEmpty()) {
            AllSharedMediaQuery.m1688a(i, i2, i3, i4, false, i5);
            return;
        }
        if (!z) {
            ImageLoader.saveMessagesThumbs(com_hanista_mobogram_tgnet_TLRPC_messages_Messages.messages);
            MessagesStorage.getInstance().putUsersAndChats(com_hanista_mobogram_tgnet_TLRPC_messages_Messages.users, com_hanista_mobogram_tgnet_TLRPC_messages_Messages.chats, true, true);
        }
        AbstractMap hashMap = new HashMap();
        for (int i7 = 0; i7 < com_hanista_mobogram_tgnet_TLRPC_messages_Messages.users.size(); i7++) {
            User user = (User) com_hanista_mobogram_tgnet_TLRPC_messages_Messages.users.get(i7);
            hashMap.put(Integer.valueOf(user.id), user);
        }
        ArrayList arrayList = new ArrayList();
        while (i6 < com_hanista_mobogram_tgnet_TLRPC_messages_Messages.messages.size()) {
            arrayList.add(new MessageObject((Message) com_hanista_mobogram_tgnet_TLRPC_messages_Messages.messages.get(i6), hashMap, true));
            i6++;
        }
        AndroidUtilities.runOnUIThread(new AllSharedMediaQuery(com_hanista_mobogram_tgnet_TLRPC_messages_Messages, z, arrayList, i5, i4, z2));
    }
}
