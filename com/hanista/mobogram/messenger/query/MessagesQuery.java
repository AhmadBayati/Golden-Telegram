package com.hanista.mobogram.messenger.query;

import android.text.Spannable;
import android.text.TextUtils;
import com.hanista.mobogram.SQLite.SQLiteCursor;
import com.hanista.mobogram.SQLite.SQLitePreparedStatement;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.FileLog;
import com.hanista.mobogram.messenger.ImageLoader;
import com.hanista.mobogram.messenger.MessageObject;
import com.hanista.mobogram.messenger.MessagesController;
import com.hanista.mobogram.messenger.MessagesStorage;
import com.hanista.mobogram.messenger.NotificationCenter;
import com.hanista.mobogram.messenger.Utilities;
import com.hanista.mobogram.tgnet.AbstractSerializedData;
import com.hanista.mobogram.tgnet.ConnectionsManager;
import com.hanista.mobogram.tgnet.NativeByteBuffer;
import com.hanista.mobogram.tgnet.RequestDelegate;
import com.hanista.mobogram.tgnet.TLObject;
import com.hanista.mobogram.tgnet.TLRPC.Chat;
import com.hanista.mobogram.tgnet.TLRPC.Message;
import com.hanista.mobogram.tgnet.TLRPC.MessageEntity;
import com.hanista.mobogram.tgnet.TLRPC.TL_channels_getMessages;
import com.hanista.mobogram.tgnet.TLRPC.TL_error;
import com.hanista.mobogram.tgnet.TLRPC.TL_inputMessageEntityMentionName;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageActionGameScore;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageActionPinMessage;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageEntityCode;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageEntityPre;
import com.hanista.mobogram.tgnet.TLRPC.TL_messages_getMessages;
import com.hanista.mobogram.tgnet.TLRPC.User;
import com.hanista.mobogram.tgnet.TLRPC.messages_Messages;
import com.hanista.mobogram.ui.Components.URLSpanUserMention;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map.Entry;

public class MessagesQuery {
    private static Comparator<MessageEntity> entityComparator;

    /* renamed from: com.hanista.mobogram.messenger.query.MessagesQuery.1 */
    static class C07741 implements Comparator<MessageEntity> {
        C07741() {
        }

        public int compare(MessageEntity messageEntity, MessageEntity messageEntity2) {
            return messageEntity.offset > messageEntity2.offset ? 1 : messageEntity.offset < messageEntity2.offset ? -1 : 0;
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.query.MessagesQuery.2 */
    static class C07752 implements Runnable {
        final /* synthetic */ int val$channelId;
        final /* synthetic */ int val$mid;

        C07752(int i, int i2) {
            this.val$channelId = i;
            this.val$mid = i2;
        }

        public void run() {
            MessagesQuery.loadPinnedMessageInternal(this.val$channelId, this.val$mid, false);
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.query.MessagesQuery.3 */
    static class C07763 implements RequestDelegate {
        final /* synthetic */ int val$channelId;

        C07763(int i) {
            this.val$channelId = i;
        }

        public void run(TLObject tLObject, TL_error tL_error) {
            boolean z;
            if (tL_error == null) {
                messages_Messages com_hanista_mobogram_tgnet_TLRPC_messages_Messages = (messages_Messages) tLObject;
                if (!com_hanista_mobogram_tgnet_TLRPC_messages_Messages.messages.isEmpty()) {
                    ImageLoader.saveMessagesThumbs(com_hanista_mobogram_tgnet_TLRPC_messages_Messages.messages);
                    MessagesQuery.broadcastPinnedMessage((Message) com_hanista_mobogram_tgnet_TLRPC_messages_Messages.messages.get(0), com_hanista_mobogram_tgnet_TLRPC_messages_Messages.users, com_hanista_mobogram_tgnet_TLRPC_messages_Messages.chats, false, false);
                    MessagesStorage.getInstance().putUsersAndChats(com_hanista_mobogram_tgnet_TLRPC_messages_Messages.users, com_hanista_mobogram_tgnet_TLRPC_messages_Messages.chats, true, true);
                    MessagesQuery.savePinnedMessage((Message) com_hanista_mobogram_tgnet_TLRPC_messages_Messages.messages.get(0));
                    z = true;
                    if (!z) {
                        MessagesStorage.getInstance().updateChannelPinnedMessage(this.val$channelId, 0);
                    }
                }
            }
            z = false;
            if (!z) {
                MessagesStorage.getInstance().updateChannelPinnedMessage(this.val$channelId, 0);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.query.MessagesQuery.4 */
    static class C07774 implements Runnable {
        final /* synthetic */ Message val$result;

        C07774(Message message) {
            this.val$result = message;
        }

        public void run() {
            try {
                MessagesStorage.getInstance().getDatabase().beginTransaction();
                SQLitePreparedStatement executeFast = MessagesStorage.getInstance().getDatabase().executeFast("REPLACE INTO chat_pinned VALUES(?, ?, ?)");
                NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(this.val$result.getObjectSize());
                this.val$result.serializeToStream(nativeByteBuffer);
                executeFast.requery();
                executeFast.bindInteger(1, this.val$result.to_id.channel_id);
                executeFast.bindInteger(2, this.val$result.id);
                executeFast.bindByteBuffer(3, nativeByteBuffer);
                executeFast.step();
                nativeByteBuffer.reuse();
                executeFast.dispose();
                MessagesStorage.getInstance().getDatabase().commitTransaction();
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.query.MessagesQuery.5 */
    static class C07785 implements Runnable {
        final /* synthetic */ ArrayList val$chats;
        final /* synthetic */ HashMap val$chatsDict;
        final /* synthetic */ boolean val$isCache;
        final /* synthetic */ Message val$result;
        final /* synthetic */ ArrayList val$users;
        final /* synthetic */ HashMap val$usersDict;

        C07785(ArrayList arrayList, boolean z, ArrayList arrayList2, Message message, HashMap hashMap, HashMap hashMap2) {
            this.val$users = arrayList;
            this.val$isCache = z;
            this.val$chats = arrayList2;
            this.val$result = message;
            this.val$usersDict = hashMap;
            this.val$chatsDict = hashMap2;
        }

        public void run() {
            MessagesController.getInstance().putUsers(this.val$users, this.val$isCache);
            MessagesController.getInstance().putChats(this.val$chats, this.val$isCache);
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.didLoadedPinnedMessage, new MessageObject(this.val$result, this.val$usersDict, this.val$chatsDict, false));
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.query.MessagesQuery.6 */
    static class C07806 implements Runnable {
        final /* synthetic */ long val$dialogId;
        final /* synthetic */ HashMap val$replyMessageRandomOwners;
        final /* synthetic */ ArrayList val$replyMessages;

        /* renamed from: com.hanista.mobogram.messenger.query.MessagesQuery.6.1 */
        class C07791 implements Runnable {
            C07791() {
            }

            public void run() {
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.didLoadedReplyMessages, Long.valueOf(C07806.this.val$dialogId));
            }
        }

        C07806(ArrayList arrayList, long j, HashMap hashMap) {
            this.val$replyMessages = arrayList;
            this.val$dialogId = j;
            this.val$replyMessageRandomOwners = hashMap;
        }

        public void run() {
            try {
                ArrayList arrayList;
                int i;
                SQLiteCursor queryFinalized = MessagesStorage.getInstance().getDatabase().queryFinalized(String.format(Locale.US, "SELECT m.data, m.mid, m.date, r.random_id FROM randoms as r INNER JOIN messages as m ON r.mid = m.mid WHERE r.random_id IN(%s)", new Object[]{TextUtils.join(",", this.val$replyMessages)}), new Object[0]);
                while (queryFinalized.next()) {
                    AbstractSerializedData byteBufferValue = queryFinalized.byteBufferValue(0);
                    if (byteBufferValue != null) {
                        Message TLdeserialize = Message.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                        byteBufferValue.reuse();
                        TLdeserialize.id = queryFinalized.intValue(1);
                        TLdeserialize.date = queryFinalized.intValue(2);
                        TLdeserialize.dialog_id = this.val$dialogId;
                        arrayList = (ArrayList) this.val$replyMessageRandomOwners.remove(Long.valueOf(queryFinalized.longValue(3)));
                        if (arrayList != null) {
                            MessageObject messageObject = new MessageObject(TLdeserialize, null, null, false);
                            for (i = 0; i < arrayList.size(); i++) {
                                MessageObject messageObject2 = (MessageObject) arrayList.get(i);
                                messageObject2.replyMessageObject = messageObject;
                                messageObject2.messageOwner.reply_to_msg_id = messageObject.getId();
                            }
                        }
                    }
                }
                queryFinalized.dispose();
                if (!this.val$replyMessageRandomOwners.isEmpty()) {
                    for (Entry value : this.val$replyMessageRandomOwners.entrySet()) {
                        arrayList = (ArrayList) value.getValue();
                        for (i = 0; i < arrayList.size(); i++) {
                            ((MessageObject) arrayList.get(i)).messageOwner.reply_to_random_id = 0;
                        }
                    }
                }
                AndroidUtilities.runOnUIThread(new C07791());
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.query.MessagesQuery.7 */
    static class C07837 implements Runnable {
        final /* synthetic */ int val$channelIdFinal;
        final /* synthetic */ long val$dialogId;
        final /* synthetic */ HashMap val$replyMessageOwners;
        final /* synthetic */ ArrayList val$replyMessages;
        final /* synthetic */ StringBuilder val$stringBuilder;

        /* renamed from: com.hanista.mobogram.messenger.query.MessagesQuery.7.1 */
        class C07811 implements RequestDelegate {
            C07811() {
            }

            public void run(TLObject tLObject, TL_error tL_error) {
                if (tL_error == null) {
                    messages_Messages com_hanista_mobogram_tgnet_TLRPC_messages_Messages = (messages_Messages) tLObject;
                    ImageLoader.saveMessagesThumbs(com_hanista_mobogram_tgnet_TLRPC_messages_Messages.messages);
                    MessagesQuery.broadcastReplyMessages(com_hanista_mobogram_tgnet_TLRPC_messages_Messages.messages, C07837.this.val$replyMessageOwners, com_hanista_mobogram_tgnet_TLRPC_messages_Messages.users, com_hanista_mobogram_tgnet_TLRPC_messages_Messages.chats, C07837.this.val$dialogId, false);
                    MessagesStorage.getInstance().putUsersAndChats(com_hanista_mobogram_tgnet_TLRPC_messages_Messages.users, com_hanista_mobogram_tgnet_TLRPC_messages_Messages.chats, true, true);
                    MessagesQuery.saveReplyMessages(C07837.this.val$replyMessageOwners, com_hanista_mobogram_tgnet_TLRPC_messages_Messages.messages);
                }
            }
        }

        /* renamed from: com.hanista.mobogram.messenger.query.MessagesQuery.7.2 */
        class C07822 implements RequestDelegate {
            C07822() {
            }

            public void run(TLObject tLObject, TL_error tL_error) {
                if (tL_error == null) {
                    messages_Messages com_hanista_mobogram_tgnet_TLRPC_messages_Messages = (messages_Messages) tLObject;
                    ImageLoader.saveMessagesThumbs(com_hanista_mobogram_tgnet_TLRPC_messages_Messages.messages);
                    MessagesQuery.broadcastReplyMessages(com_hanista_mobogram_tgnet_TLRPC_messages_Messages.messages, C07837.this.val$replyMessageOwners, com_hanista_mobogram_tgnet_TLRPC_messages_Messages.users, com_hanista_mobogram_tgnet_TLRPC_messages_Messages.chats, C07837.this.val$dialogId, false);
                    MessagesStorage.getInstance().putUsersAndChats(com_hanista_mobogram_tgnet_TLRPC_messages_Messages.users, com_hanista_mobogram_tgnet_TLRPC_messages_Messages.chats, true, true);
                    MessagesQuery.saveReplyMessages(C07837.this.val$replyMessageOwners, com_hanista_mobogram_tgnet_TLRPC_messages_Messages.messages);
                }
            }
        }

        C07837(StringBuilder stringBuilder, long j, ArrayList arrayList, HashMap hashMap, int i) {
            this.val$stringBuilder = stringBuilder;
            this.val$dialogId = j;
            this.val$replyMessages = arrayList;
            this.val$replyMessageOwners = hashMap;
            this.val$channelIdFinal = i;
        }

        public void run() {
            try {
                ArrayList arrayList = new ArrayList();
                ArrayList arrayList2 = new ArrayList();
                ArrayList arrayList3 = new ArrayList();
                Iterable arrayList4 = new ArrayList();
                Iterable arrayList5 = new ArrayList();
                SQLiteCursor queryFinalized = MessagesStorage.getInstance().getDatabase().queryFinalized(String.format(Locale.US, "SELECT data, mid, date FROM messages WHERE mid IN(%s)", new Object[]{this.val$stringBuilder.toString()}), new Object[0]);
                while (queryFinalized.next()) {
                    AbstractSerializedData byteBufferValue = queryFinalized.byteBufferValue(0);
                    if (byteBufferValue != null) {
                        Message TLdeserialize = Message.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                        byteBufferValue.reuse();
                        TLdeserialize.id = queryFinalized.intValue(1);
                        TLdeserialize.date = queryFinalized.intValue(2);
                        TLdeserialize.dialog_id = this.val$dialogId;
                        MessagesStorage.addUsersAndChatsFromMessage(TLdeserialize, arrayList4, arrayList5);
                        arrayList.add(TLdeserialize);
                        this.val$replyMessages.remove(Integer.valueOf(TLdeserialize.id));
                    }
                }
                queryFinalized.dispose();
                if (!arrayList4.isEmpty()) {
                    MessagesStorage.getInstance().getUsersInternal(TextUtils.join(",", arrayList4), arrayList2);
                }
                if (!arrayList5.isEmpty()) {
                    MessagesStorage.getInstance().getChatsInternal(TextUtils.join(",", arrayList5), arrayList3);
                }
                MessagesQuery.broadcastReplyMessages(arrayList, this.val$replyMessageOwners, arrayList2, arrayList3, this.val$dialogId, true);
                if (!this.val$replyMessages.isEmpty()) {
                    TLObject tL_channels_getMessages;
                    if (this.val$channelIdFinal != 0) {
                        tL_channels_getMessages = new TL_channels_getMessages();
                        tL_channels_getMessages.channel = MessagesController.getInputChannel(this.val$channelIdFinal);
                        tL_channels_getMessages.id = this.val$replyMessages;
                        ConnectionsManager.getInstance().sendRequest(tL_channels_getMessages, new C07811());
                        return;
                    }
                    tL_channels_getMessages = new TL_messages_getMessages();
                    tL_channels_getMessages.id = this.val$replyMessages;
                    ConnectionsManager.getInstance().sendRequest(tL_channels_getMessages, new C07822());
                }
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.query.MessagesQuery.8 */
    static class C07848 implements Runnable {
        final /* synthetic */ HashMap val$replyMessageOwners;
        final /* synthetic */ ArrayList val$result;

        C07848(ArrayList arrayList, HashMap hashMap) {
            this.val$result = arrayList;
            this.val$replyMessageOwners = hashMap;
        }

        public void run() {
            try {
                MessagesStorage.getInstance().getDatabase().beginTransaction();
                SQLitePreparedStatement executeFast = MessagesStorage.getInstance().getDatabase().executeFast("UPDATE messages SET replydata = ? WHERE mid = ?");
                for (int i = 0; i < this.val$result.size(); i++) {
                    Message message = (Message) this.val$result.get(i);
                    ArrayList arrayList = (ArrayList) this.val$replyMessageOwners.get(Integer.valueOf(message.id));
                    if (arrayList != null) {
                        NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(message.getObjectSize());
                        message.serializeToStream(nativeByteBuffer);
                        for (int i2 = 0; i2 < arrayList.size(); i2++) {
                            MessageObject messageObject = (MessageObject) arrayList.get(i2);
                            executeFast.requery();
                            long id = (long) messageObject.getId();
                            if (messageObject.messageOwner.to_id.channel_id != 0) {
                                id |= ((long) messageObject.messageOwner.to_id.channel_id) << 32;
                            }
                            executeFast.bindByteBuffer(1, nativeByteBuffer);
                            executeFast.bindLong(2, id);
                            executeFast.step();
                        }
                        nativeByteBuffer.reuse();
                    }
                }
                executeFast.dispose();
                MessagesStorage.getInstance().getDatabase().commitTransaction();
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.query.MessagesQuery.9 */
    static class C07859 implements Runnable {
        final /* synthetic */ ArrayList val$chats;
        final /* synthetic */ HashMap val$chatsDict;
        final /* synthetic */ long val$dialog_id;
        final /* synthetic */ boolean val$isCache;
        final /* synthetic */ HashMap val$replyMessageOwners;
        final /* synthetic */ ArrayList val$result;
        final /* synthetic */ ArrayList val$users;
        final /* synthetic */ HashMap val$usersDict;

        C07859(ArrayList arrayList, boolean z, ArrayList arrayList2, ArrayList arrayList3, HashMap hashMap, HashMap hashMap2, HashMap hashMap3, long j) {
            this.val$users = arrayList;
            this.val$isCache = z;
            this.val$chats = arrayList2;
            this.val$result = arrayList3;
            this.val$replyMessageOwners = hashMap;
            this.val$usersDict = hashMap2;
            this.val$chatsDict = hashMap3;
            this.val$dialog_id = j;
        }

        public void run() {
            MessagesController.getInstance().putUsers(this.val$users, this.val$isCache);
            MessagesController.getInstance().putChats(this.val$chats, this.val$isCache);
            int i = 0;
            boolean z = false;
            while (i < this.val$result.size()) {
                boolean z2;
                Message message = (Message) this.val$result.get(i);
                ArrayList arrayList = (ArrayList) this.val$replyMessageOwners.get(Integer.valueOf(message.id));
                if (arrayList != null) {
                    MessageObject messageObject = new MessageObject(message, this.val$usersDict, this.val$chatsDict, false);
                    for (int i2 = 0; i2 < arrayList.size(); i2++) {
                        MessageObject messageObject2 = (MessageObject) arrayList.get(i2);
                        messageObject2.replyMessageObject = messageObject;
                        if (messageObject2.messageOwner.action instanceof TL_messageActionPinMessage) {
                            messageObject2.generatePinMessageText(null, null);
                        } else if (messageObject2.messageOwner.action instanceof TL_messageActionGameScore) {
                            messageObject2.generateGameMessageText(null);
                        }
                    }
                    z2 = true;
                } else {
                    z2 = z;
                }
                i++;
                z = z2;
            }
            if (z) {
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.didLoadedReplyMessages, Long.valueOf(this.val$dialog_id));
            }
        }
    }

    static {
        entityComparator = new C07741();
    }

    private static MessageObject broadcastPinnedMessage(Message message, ArrayList<User> arrayList, ArrayList<Chat> arrayList2, boolean z, boolean z2) {
        int i;
        AbstractMap hashMap = new HashMap();
        for (i = 0; i < arrayList.size(); i++) {
            User user = (User) arrayList.get(i);
            hashMap.put(Integer.valueOf(user.id), user);
        }
        AbstractMap hashMap2 = new HashMap();
        for (i = 0; i < arrayList2.size(); i++) {
            Chat chat = (Chat) arrayList2.get(i);
            hashMap2.put(Integer.valueOf(chat.id), chat);
        }
        if (z2) {
            return new MessageObject(message, hashMap, hashMap2, false);
        }
        AndroidUtilities.runOnUIThread(new C07785(arrayList, z, arrayList2, message, hashMap, hashMap2));
        return null;
    }

    private static void broadcastReplyMessages(ArrayList<Message> arrayList, HashMap<Integer, ArrayList<MessageObject>> hashMap, ArrayList<User> arrayList2, ArrayList<Chat> arrayList3, long j, boolean z) {
        int i;
        HashMap hashMap2 = new HashMap();
        for (i = 0; i < arrayList2.size(); i++) {
            User user = (User) arrayList2.get(i);
            hashMap2.put(Integer.valueOf(user.id), user);
        }
        HashMap hashMap3 = new HashMap();
        for (i = 0; i < arrayList3.size(); i++) {
            Chat chat = (Chat) arrayList3.get(i);
            hashMap3.put(Integer.valueOf(chat.id), chat);
        }
        AndroidUtilities.runOnUIThread(new C07859(arrayList2, z, arrayList3, arrayList, hashMap, hashMap2, hashMap3, j));
    }

    public static ArrayList<MessageEntity> getEntities(CharSequence[] charSequenceArr) {
        if (charSequenceArr == null || charSequenceArr[0] == null) {
            return null;
        }
        int i;
        ArrayList<MessageEntity> arrayList = null;
        String str = "`";
        str = "```";
        int i2 = -1;
        Object obj = null;
        int i3 = 0;
        while (true) {
            int indexOf = TextUtils.indexOf(charSequenceArr[0], obj == null ? "`" : "```", i3);
            if (indexOf == -1) {
                break;
            } else if (i2 == -1) {
                r0 = (charSequenceArr[0].length() - indexOf > 2 && charSequenceArr[0].charAt(indexOf + 1) == '`' && charSequenceArr[0].charAt(indexOf + 2) == '`') ? 1 : null;
                i2 = indexOf;
                obj = r0;
                i3 = (r0 != null ? 3 : 1) + indexOf;
            } else {
                ArrayList<MessageEntity> arrayList2 = arrayList == null ? new ArrayList() : arrayList;
                i3 = (obj != null ? 3 : 1) + indexOf;
                while (i3 < charSequenceArr[0].length() && charSequenceArr[0].charAt(i3) == '`') {
                    indexOf++;
                    i3++;
                }
                int i4 = indexOf + (obj != null ? 3 : 1);
                if (obj != null) {
                    CharSequence concat;
                    char charAt = i2 > 0 ? charSequenceArr[0].charAt(i2 - 1) : '\u0000';
                    r0 = (charAt == ' ' || charAt == '\n') ? 1 : null;
                    CharSequence substring = TextUtils.substring(charSequenceArr[0], 0, i2 - (r0 != null ? 1 : 0));
                    String substring2 = TextUtils.substring(charSequenceArr[0], i2 + 3, indexOf);
                    char charAt2 = indexOf + 3 < charSequenceArr[0].length() ? charSequenceArr[0].charAt(indexOf + 3) : '\u0000';
                    CharSequence charSequence = charSequenceArr[0];
                    int i5 = indexOf + 3;
                    i = (charAt2 == ' ' || charAt2 == '\n') ? 1 : 0;
                    charSequence = TextUtils.substring(charSequence, i + i5, charSequenceArr[0].length());
                    if (substring.length() != 0) {
                        obj = r0;
                        concat = TextUtils.concat(new CharSequence[]{substring, "\n"});
                    } else {
                        i = 1;
                        concat = substring;
                    }
                    substring = charSequence.length() != 0 ? TextUtils.concat(new CharSequence[]{"\n", charSequence}) : charSequence;
                    charSequenceArr[0] = TextUtils.concat(new CharSequence[]{concat, substring2, substring});
                    TL_messageEntityPre tL_messageEntityPre = new TL_messageEntityPre();
                    tL_messageEntityPre.offset = (obj != null ? 0 : 1) + i2;
                    tL_messageEntityPre.length = (obj != null ? 0 : 1) + ((indexOf - i2) - 3);
                    tL_messageEntityPre.language = TtmlNode.ANONYMOUS_REGION_ID;
                    arrayList2.add(tL_messageEntityPre);
                    i3 = i4 - 6;
                } else {
                    charSequenceArr[0] = TextUtils.concat(new CharSequence[]{TextUtils.substring(charSequenceArr[0], 0, i2), TextUtils.substring(charSequenceArr[0], i2 + 1, indexOf), TextUtils.substring(charSequenceArr[0], indexOf + 1, charSequenceArr[0].length())});
                    TL_messageEntityCode tL_messageEntityCode = new TL_messageEntityCode();
                    tL_messageEntityCode.offset = i2;
                    tL_messageEntityCode.length = (indexOf - i2) - 1;
                    arrayList2.add(tL_messageEntityCode);
                    i3 = i4 - 2;
                }
                i2 = -1;
                arrayList = arrayList2;
                obj = null;
            }
        }
        if (!(i2 == -1 || obj == null)) {
            charSequenceArr[0] = TextUtils.concat(new CharSequence[]{TextUtils.substring(charSequenceArr[0], 0, i2), TextUtils.substring(charSequenceArr[0], i2 + 2, charSequenceArr[0].length())});
            if (arrayList == null) {
                arrayList = new ArrayList();
            }
            tL_messageEntityCode = new TL_messageEntityCode();
            tL_messageEntityCode.offset = i2;
            tL_messageEntityCode.length = 1;
            arrayList.add(tL_messageEntityCode);
        }
        if (!(charSequenceArr[0] instanceof Spannable)) {
            return arrayList;
        }
        Spannable spannable = (Spannable) charSequenceArr[0];
        URLSpanUserMention[] uRLSpanUserMentionArr = (URLSpanUserMention[]) spannable.getSpans(0, charSequenceArr[0].length(), URLSpanUserMention.class);
        if (uRLSpanUserMentionArr == null || uRLSpanUserMentionArr.length <= 0) {
            return arrayList;
        }
        arrayList = new ArrayList();
        for (i = 0; i < uRLSpanUserMentionArr.length; i++) {
            TL_inputMessageEntityMentionName tL_inputMessageEntityMentionName = new TL_inputMessageEntityMentionName();
            tL_inputMessageEntityMentionName.user_id = MessagesController.getInputUser(Utilities.parseInt(uRLSpanUserMentionArr[i].getURL()).intValue());
            if (tL_inputMessageEntityMentionName.user_id != null) {
                tL_inputMessageEntityMentionName.offset = spannable.getSpanStart(uRLSpanUserMentionArr[i]);
                tL_inputMessageEntityMentionName.length = Math.min(spannable.getSpanEnd(uRLSpanUserMentionArr[i]), charSequenceArr[0].length()) - tL_inputMessageEntityMentionName.offset;
                if (charSequenceArr[0].charAt((tL_inputMessageEntityMentionName.offset + tL_inputMessageEntityMentionName.length) - 1) == ' ') {
                    tL_inputMessageEntityMentionName.length--;
                }
                arrayList.add(tL_inputMessageEntityMentionName);
            }
        }
        return arrayList;
    }

    public static MessageObject loadPinnedMessage(int i, int i2, boolean z) {
        if (!z) {
            return loadPinnedMessageInternal(i, i2, true);
        }
        MessagesStorage.getInstance().getStorageQueue().postRunnable(new C07752(i, i2));
        return null;
    }

    private static MessageObject loadPinnedMessageInternal(int i, int i2, boolean z) {
        long j = ((long) i2) | (((long) i) << 32);
        try {
            AbstractSerializedData byteBufferValue;
            Message TLdeserialize;
            TLObject tL_channels_getMessages;
            ArrayList arrayList = new ArrayList();
            ArrayList arrayList2 = new ArrayList();
            Iterable arrayList3 = new ArrayList();
            Iterable arrayList4 = new ArrayList();
            SQLiteCursor queryFinalized = MessagesStorage.getInstance().getDatabase().queryFinalized(String.format(Locale.US, "SELECT data, mid, date FROM messages WHERE mid = %d", new Object[]{Long.valueOf(j)}), new Object[0]);
            if (queryFinalized.next()) {
                byteBufferValue = queryFinalized.byteBufferValue(0);
                if (byteBufferValue != null) {
                    TLdeserialize = Message.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                    byteBufferValue.reuse();
                    TLdeserialize.id = queryFinalized.intValue(1);
                    TLdeserialize.date = queryFinalized.intValue(2);
                    TLdeserialize.dialog_id = (long) (-i);
                    MessagesStorage.addUsersAndChatsFromMessage(TLdeserialize, arrayList3, arrayList4);
                    queryFinalized.dispose();
                    if (TLdeserialize == null) {
                        queryFinalized = MessagesStorage.getInstance().getDatabase().queryFinalized(String.format(Locale.US, "SELECT data FROM chat_pinned WHERE uid = %d", new Object[]{Integer.valueOf(i)}), new Object[0]);
                        if (queryFinalized.next()) {
                            byteBufferValue = queryFinalized.byteBufferValue(0);
                            if (byteBufferValue != null) {
                                TLdeserialize = Message.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                                byteBufferValue.reuse();
                                if (TLdeserialize.id == i2) {
                                    TLdeserialize = null;
                                } else {
                                    TLdeserialize.dialog_id = (long) (-i);
                                    MessagesStorage.addUsersAndChatsFromMessage(TLdeserialize, arrayList3, arrayList4);
                                }
                            }
                        }
                        queryFinalized.dispose();
                    }
                    if (TLdeserialize == null) {
                        tL_channels_getMessages = new TL_channels_getMessages();
                        tL_channels_getMessages.channel = MessagesController.getInputChannel(i);
                        tL_channels_getMessages.id.add(Integer.valueOf(i2));
                        ConnectionsManager.getInstance().sendRequest(tL_channels_getMessages, new C07763(i));
                        return null;
                    } else if (z) {
                        return broadcastPinnedMessage(TLdeserialize, arrayList, arrayList2, true, z);
                    } else {
                        if (!arrayList3.isEmpty()) {
                            MessagesStorage.getInstance().getUsersInternal(TextUtils.join(",", arrayList3), arrayList);
                        }
                        if (!arrayList4.isEmpty()) {
                            MessagesStorage.getInstance().getChatsInternal(TextUtils.join(",", arrayList4), arrayList2);
                        }
                        broadcastPinnedMessage(TLdeserialize, arrayList, arrayList2, true, false);
                        return null;
                    }
                }
            }
            TLdeserialize = null;
            queryFinalized.dispose();
            if (TLdeserialize == null) {
                queryFinalized = MessagesStorage.getInstance().getDatabase().queryFinalized(String.format(Locale.US, "SELECT data FROM chat_pinned WHERE uid = %d", new Object[]{Integer.valueOf(i)}), new Object[0]);
                if (queryFinalized.next()) {
                    byteBufferValue = queryFinalized.byteBufferValue(0);
                    if (byteBufferValue != null) {
                        TLdeserialize = Message.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                        byteBufferValue.reuse();
                        if (TLdeserialize.id == i2) {
                            TLdeserialize.dialog_id = (long) (-i);
                            MessagesStorage.addUsersAndChatsFromMessage(TLdeserialize, arrayList3, arrayList4);
                        } else {
                            TLdeserialize = null;
                        }
                    }
                }
                queryFinalized.dispose();
            }
            if (TLdeserialize == null) {
                tL_channels_getMessages = new TL_channels_getMessages();
                tL_channels_getMessages.channel = MessagesController.getInputChannel(i);
                tL_channels_getMessages.id.add(Integer.valueOf(i2));
                ConnectionsManager.getInstance().sendRequest(tL_channels_getMessages, new C07763(i));
                return null;
            } else if (z) {
                return broadcastPinnedMessage(TLdeserialize, arrayList, arrayList2, true, z);
            } else {
                if (arrayList3.isEmpty()) {
                    MessagesStorage.getInstance().getUsersInternal(TextUtils.join(",", arrayList3), arrayList);
                }
                if (arrayList4.isEmpty()) {
                    MessagesStorage.getInstance().getChatsInternal(TextUtils.join(",", arrayList4), arrayList2);
                }
                broadcastPinnedMessage(TLdeserialize, arrayList, arrayList2, true, false);
                return null;
            }
        } catch (Throwable e) {
            FileLog.m18e("tmessages", e);
            return null;
        }
    }

    public static void loadReplyMessagesForMessages(ArrayList<MessageObject> arrayList, long j) {
        MessageObject messageObject;
        if (((int) j) == 0) {
            ArrayList arrayList2 = new ArrayList();
            HashMap hashMap = new HashMap();
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < arrayList.size(); i++) {
                messageObject = (MessageObject) arrayList.get(i);
                if (messageObject.isReply() && messageObject.replyMessageObject == null) {
                    Long valueOf = Long.valueOf(messageObject.messageOwner.reply_to_random_id);
                    if (stringBuilder.length() > 0) {
                        stringBuilder.append(',');
                    }
                    stringBuilder.append(valueOf);
                    ArrayList arrayList3 = (ArrayList) hashMap.get(valueOf);
                    if (arrayList3 == null) {
                        arrayList3 = new ArrayList();
                        hashMap.put(valueOf, arrayList3);
                    }
                    arrayList3.add(messageObject);
                    if (!arrayList2.contains(valueOf)) {
                        arrayList2.add(valueOf);
                    }
                }
            }
            if (!arrayList2.isEmpty()) {
                MessagesStorage.getInstance().getStorageQueue().postRunnable(new C07806(arrayList2, j, hashMap));
                return;
            }
            return;
        }
        ArrayList arrayList4 = new ArrayList();
        HashMap hashMap2 = new HashMap();
        StringBuilder stringBuilder2 = new StringBuilder();
        int i2 = 0;
        for (int i3 = 0; i3 < arrayList.size(); i3++) {
            messageObject = (MessageObject) arrayList.get(i3);
            if (messageObject.getId() > 0 && messageObject.isReply() && messageObject.replyMessageObject == null) {
                long j2;
                int i4;
                Integer valueOf2 = Integer.valueOf(messageObject.messageOwner.reply_to_msg_id);
                long intValue = (long) valueOf2.intValue();
                if (messageObject.messageOwner.to_id.channel_id != 0) {
                    j2 = (((long) messageObject.messageOwner.to_id.channel_id) << 32) | intValue;
                    i4 = messageObject.messageOwner.to_id.channel_id;
                } else {
                    long j3 = intValue;
                    i4 = i2;
                    j2 = j3;
                }
                if (stringBuilder2.length() > 0) {
                    stringBuilder2.append(',');
                }
                stringBuilder2.append(j2);
                ArrayList arrayList5 = (ArrayList) hashMap2.get(valueOf2);
                if (arrayList5 == null) {
                    arrayList5 = new ArrayList();
                    hashMap2.put(valueOf2, arrayList5);
                }
                arrayList5.add(messageObject);
                if (!arrayList4.contains(valueOf2)) {
                    arrayList4.add(valueOf2);
                }
                i2 = i4;
            }
        }
        if (!arrayList4.isEmpty()) {
            MessagesStorage.getInstance().getStorageQueue().postRunnable(new C07837(stringBuilder2, j, arrayList4, hashMap2, i2));
        }
    }

    private static void savePinnedMessage(Message message) {
        MessagesStorage.getInstance().getStorageQueue().postRunnable(new C07774(message));
    }

    private static void saveReplyMessages(HashMap<Integer, ArrayList<MessageObject>> hashMap, ArrayList<Message> arrayList) {
        MessagesStorage.getInstance().getStorageQueue().postRunnable(new C07848(arrayList, hashMap));
    }

    public static void sortEntities(ArrayList<MessageEntity> arrayList) {
        Collections.sort(arrayList, entityComparator);
    }
}
