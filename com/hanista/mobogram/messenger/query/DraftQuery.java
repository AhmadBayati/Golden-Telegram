package com.hanista.mobogram.messenger.query;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;
import com.hanista.mobogram.SQLite.SQLiteCursor;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.ApplicationLoader;
import com.hanista.mobogram.messenger.ChatObject;
import com.hanista.mobogram.messenger.FileLog;
import com.hanista.mobogram.messenger.MessagesController;
import com.hanista.mobogram.messenger.MessagesStorage;
import com.hanista.mobogram.messenger.NotificationCenter;
import com.hanista.mobogram.messenger.UserConfig;
import com.hanista.mobogram.messenger.Utilities;
import com.hanista.mobogram.tgnet.AbstractSerializedData;
import com.hanista.mobogram.tgnet.ConnectionsManager;
import com.hanista.mobogram.tgnet.RequestDelegate;
import com.hanista.mobogram.tgnet.SerializedData;
import com.hanista.mobogram.tgnet.TLObject;
import com.hanista.mobogram.tgnet.TLRPC.Chat;
import com.hanista.mobogram.tgnet.TLRPC.DraftMessage;
import com.hanista.mobogram.tgnet.TLRPC.Message;
import com.hanista.mobogram.tgnet.TLRPC.MessageEntity;
import com.hanista.mobogram.tgnet.TLRPC.TL_channels_getMessages;
import com.hanista.mobogram.tgnet.TLRPC.TL_draftMessage;
import com.hanista.mobogram.tgnet.TLRPC.TL_draftMessageEmpty;
import com.hanista.mobogram.tgnet.TLRPC.TL_error;
import com.hanista.mobogram.tgnet.TLRPC.TL_messages_getAllDrafts;
import com.hanista.mobogram.tgnet.TLRPC.TL_messages_getMessages;
import com.hanista.mobogram.tgnet.TLRPC.TL_messages_saveDraft;
import com.hanista.mobogram.tgnet.TLRPC.Updates;
import com.hanista.mobogram.tgnet.TLRPC.User;
import com.hanista.mobogram.tgnet.TLRPC.messages_Messages;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map.Entry;

public class DraftQuery {
    private static HashMap<Long, Message> draftMessages;
    private static HashMap<Long, DraftMessage> drafts;
    private static boolean inTransaction;
    private static boolean loadingDrafts;
    private static SharedPreferences preferences;

    /* renamed from: com.hanista.mobogram.messenger.query.DraftQuery.1 */
    static class C07681 implements RequestDelegate {

        /* renamed from: com.hanista.mobogram.messenger.query.DraftQuery.1.1 */
        class C07671 implements Runnable {
            C07671() {
            }

            public void run() {
                UserConfig.draftsLoaded = true;
                DraftQuery.loadingDrafts = false;
                UserConfig.saveConfig(false);
            }
        }

        C07681() {
        }

        public void run(TLObject tLObject, TL_error tL_error) {
            if (tL_error == null) {
                MessagesController.getInstance().processUpdates((Updates) tLObject, false);
                AndroidUtilities.runOnUIThread(new C07671());
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.query.DraftQuery.2 */
    static class C07692 implements RequestDelegate {
        C07692() {
        }

        public void run(TLObject tLObject, TL_error tL_error) {
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.query.DraftQuery.3 */
    static class C07723 implements Runnable {
        final /* synthetic */ int val$channelIdFinal;
        final /* synthetic */ long val$did;
        final /* synthetic */ long val$messageIdFinal;

        /* renamed from: com.hanista.mobogram.messenger.query.DraftQuery.3.1 */
        class C07701 implements RequestDelegate {
            C07701() {
            }

            public void run(TLObject tLObject, TL_error tL_error) {
                if (tL_error == null) {
                    messages_Messages com_hanista_mobogram_tgnet_TLRPC_messages_Messages = (messages_Messages) tLObject;
                    if (!com_hanista_mobogram_tgnet_TLRPC_messages_Messages.messages.isEmpty()) {
                        DraftQuery.saveDraftReplyMessage(C07723.this.val$did, (Message) com_hanista_mobogram_tgnet_TLRPC_messages_Messages.messages.get(0));
                    }
                }
            }
        }

        /* renamed from: com.hanista.mobogram.messenger.query.DraftQuery.3.2 */
        class C07712 implements RequestDelegate {
            C07712() {
            }

            public void run(TLObject tLObject, TL_error tL_error) {
                if (tL_error == null) {
                    messages_Messages com_hanista_mobogram_tgnet_TLRPC_messages_Messages = (messages_Messages) tLObject;
                    if (!com_hanista_mobogram_tgnet_TLRPC_messages_Messages.messages.isEmpty()) {
                        DraftQuery.saveDraftReplyMessage(C07723.this.val$did, (Message) com_hanista_mobogram_tgnet_TLRPC_messages_Messages.messages.get(0));
                    }
                }
            }
        }

        C07723(long j, int i, long j2) {
            this.val$messageIdFinal = j;
            this.val$channelIdFinal = i;
            this.val$did = j2;
        }

        public void run() {
            Message message = null;
            try {
                SQLiteCursor queryFinalized = MessagesStorage.getInstance().getDatabase().queryFinalized(String.format(Locale.US, "SELECT data FROM messages WHERE mid = %d", new Object[]{Long.valueOf(this.val$messageIdFinal)}), new Object[0]);
                if (queryFinalized.next()) {
                    AbstractSerializedData byteBufferValue = queryFinalized.byteBufferValue(0);
                    if (byteBufferValue != null) {
                        message = Message.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                        byteBufferValue.reuse();
                    }
                }
                queryFinalized.dispose();
                if (message != null) {
                    DraftQuery.saveDraftReplyMessage(this.val$did, message);
                } else if (this.val$channelIdFinal != 0) {
                    r0 = new TL_channels_getMessages();
                    r0.channel = MessagesController.getInputChannel(this.val$channelIdFinal);
                    r0.id.add(Integer.valueOf((int) this.val$messageIdFinal));
                    ConnectionsManager.getInstance().sendRequest(r0, new C07701());
                } else {
                    r0 = new TL_messages_getMessages();
                    r0.id.add(Integer.valueOf((int) this.val$messageIdFinal));
                    ConnectionsManager.getInstance().sendRequest(r0, new C07712());
                }
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.query.DraftQuery.4 */
    static class C07734 implements Runnable {
        final /* synthetic */ long val$did;
        final /* synthetic */ Message val$message;

        C07734(long j, Message message) {
            this.val$did = j;
            this.val$message = message;
        }

        public void run() {
            DraftMessage draftMessage = (DraftMessage) DraftQuery.drafts.get(Long.valueOf(this.val$did));
            if (draftMessage != null && draftMessage.reply_to_msg_id == this.val$message.id) {
                DraftQuery.draftMessages.put(Long.valueOf(this.val$did), this.val$message);
                AbstractSerializedData serializedData = new SerializedData(this.val$message.getObjectSize());
                this.val$message.serializeToStream(serializedData);
                DraftQuery.preferences.edit().putString("r_" + this.val$did, Utilities.bytesToHex(serializedData.toByteArray())).commit();
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.newDraftReceived, Long.valueOf(this.val$did));
            }
        }
    }

    static {
        drafts = new HashMap();
        draftMessages = new HashMap();
        preferences = ApplicationLoader.applicationContext.getSharedPreferences("drafts", 0);
        for (Entry entry : preferences.getAll().entrySet()) {
            try {
                String str = (String) entry.getKey();
                long longValue = Utilities.parseLong(str).longValue();
                AbstractSerializedData serializedData = new SerializedData(Utilities.hexToBytes((String) entry.getValue()));
                if (str.startsWith("r_")) {
                    Message TLdeserialize = Message.TLdeserialize(serializedData, serializedData.readInt32(true), true);
                    if (TLdeserialize != null) {
                        draftMessages.put(Long.valueOf(longValue), TLdeserialize);
                    }
                } else {
                    DraftMessage TLdeserialize2 = DraftMessage.TLdeserialize(serializedData, serializedData.readInt32(true), true);
                    if (TLdeserialize2 != null) {
                        drafts.put(Long.valueOf(longValue), TLdeserialize2);
                    }
                }
            } catch (Exception e) {
            }
        }
    }

    public static void beginTransaction() {
        inTransaction = true;
    }

    public static void cleanDraft(long j, boolean z) {
        DraftMessage draftMessage = (DraftMessage) drafts.get(Long.valueOf(j));
        if (draftMessage != null) {
            if (!z) {
                drafts.remove(Long.valueOf(j));
                draftMessages.remove(Long.valueOf(j));
                preferences.edit().remove(TtmlNode.ANONYMOUS_REGION_ID + j).remove("r_" + j).commit();
                MessagesController.getInstance().sortDialogs(null);
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
            } else if (draftMessage.reply_to_msg_id != 0) {
                draftMessage.reply_to_msg_id = 0;
                draftMessage.flags &= -2;
                saveDraft(j, draftMessage.message, draftMessage.entities, null, draftMessage.no_webpage, true);
            }
        }
    }

    public static void cleanup() {
        drafts.clear();
        draftMessages.clear();
        preferences.edit().clear().commit();
    }

    public static void endTransaction() {
        inTransaction = false;
    }

    public static DraftMessage getDraft(long j) {
        return (DraftMessage) drafts.get(Long.valueOf(j));
    }

    public static Message getDraftMessage(long j) {
        return (Message) draftMessages.get(Long.valueOf(j));
    }

    public static void loadDrafts() {
        if (!UserConfig.draftsLoaded && !loadingDrafts) {
            loadingDrafts = true;
            ConnectionsManager.getInstance().sendRequest(new TL_messages_getAllDrafts(), new C07681());
        }
    }

    public static void saveDraft(long j, DraftMessage draftMessage, Message message, boolean z) {
        User user = null;
        Editor edit = preferences.edit();
        if (draftMessage == null || (draftMessage instanceof TL_draftMessageEmpty)) {
            drafts.remove(Long.valueOf(j));
            draftMessages.remove(Long.valueOf(j));
            preferences.edit().remove(TtmlNode.ANONYMOUS_REGION_ID + j).remove("r_" + j).commit();
        } else {
            drafts.put(Long.valueOf(j), draftMessage);
            try {
                AbstractSerializedData serializedData = new SerializedData(draftMessage.getObjectSize());
                draftMessage.serializeToStream(serializedData);
                edit.putString(TtmlNode.ANONYMOUS_REGION_ID + j, Utilities.bytesToHex(serializedData.toByteArray()));
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
        }
        if (message == null) {
            draftMessages.remove(Long.valueOf(j));
            edit.remove("r_" + j);
        } else {
            draftMessages.put(Long.valueOf(j), message);
            serializedData = new SerializedData(message.getObjectSize());
            message.serializeToStream(serializedData);
            edit.putString("r_" + j, Utilities.bytesToHex(serializedData.toByteArray()));
        }
        edit.commit();
        if (z) {
            if (draftMessage.reply_to_msg_id != 0 && message == null) {
                Chat chat;
                int i = (int) j;
                if (i > 0) {
                    user = MessagesController.getInstance().getUser(Integer.valueOf(i));
                    chat = null;
                } else {
                    chat = MessagesController.getInstance().getChat(Integer.valueOf(-i));
                }
                if (!(user == null && chat == null)) {
                    int i2;
                    long j2 = (long) draftMessage.reply_to_msg_id;
                    if (ChatObject.isChannel(chat)) {
                        j2 |= ((long) chat.id) << 32;
                        i2 = chat.id;
                    } else {
                        i2 = 0;
                    }
                    MessagesStorage.getInstance().getStorageQueue().postRunnable(new C07723(j2, i2, j));
                }
            }
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.newDraftReceived, Long.valueOf(j));
        }
    }

    public static void saveDraft(long j, CharSequence charSequence, ArrayList<MessageEntity> arrayList, Message message, boolean z) {
        saveDraft(j, charSequence, arrayList, message, z, false);
    }

    public static void saveDraft(long j, CharSequence charSequence, ArrayList<MessageEntity> arrayList, Message message, boolean z, boolean z2) {
        DraftMessage tL_draftMessageEmpty = (TextUtils.isEmpty(charSequence) && message == null) ? new TL_draftMessageEmpty() : new TL_draftMessage();
        tL_draftMessageEmpty.date = (int) (System.currentTimeMillis() / 1000);
        tL_draftMessageEmpty.message = charSequence == null ? TtmlNode.ANONYMOUS_REGION_ID : charSequence.toString();
        tL_draftMessageEmpty.no_webpage = z;
        if (message != null) {
            tL_draftMessageEmpty.reply_to_msg_id = message.id;
            tL_draftMessageEmpty.flags |= 1;
        }
        if (!(arrayList == null || arrayList.isEmpty())) {
            tL_draftMessageEmpty.entities = arrayList;
            tL_draftMessageEmpty.flags |= 8;
        }
        DraftMessage draftMessage = (DraftMessage) drafts.get(Long.valueOf(j));
        if (!z2) {
            if (draftMessage == null || !draftMessage.message.equals(tL_draftMessageEmpty.message) || draftMessage.reply_to_msg_id != tL_draftMessageEmpty.reply_to_msg_id || draftMessage.no_webpage != tL_draftMessageEmpty.no_webpage) {
                if (draftMessage == null && TextUtils.isEmpty(tL_draftMessageEmpty.message) && tL_draftMessageEmpty.reply_to_msg_id == 0) {
                    return;
                }
            }
            return;
        }
        saveDraft(j, tL_draftMessageEmpty, message, false);
        int i = (int) j;
        if (i != 0) {
            TLObject tL_messages_saveDraft = new TL_messages_saveDraft();
            tL_messages_saveDraft.peer = MessagesController.getInputPeer(i);
            if (tL_messages_saveDraft.peer != null) {
                tL_messages_saveDraft.message = tL_draftMessageEmpty.message;
                tL_messages_saveDraft.no_webpage = tL_draftMessageEmpty.no_webpage;
                tL_messages_saveDraft.reply_to_msg_id = tL_draftMessageEmpty.reply_to_msg_id;
                tL_messages_saveDraft.entities = tL_draftMessageEmpty.entities;
                tL_messages_saveDraft.flags = tL_draftMessageEmpty.flags;
                ConnectionsManager.getInstance().sendRequest(tL_messages_saveDraft, new C07692());
            } else {
                return;
            }
        }
        MessagesController.getInstance().sortDialogs(null);
        NotificationCenter.getInstance().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
    }

    private static void saveDraftReplyMessage(long j, Message message) {
        if (message != null) {
            AndroidUtilities.runOnUIThread(new C07734(j, message));
        }
    }
}
