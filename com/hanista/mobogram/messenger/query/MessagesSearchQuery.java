package com.hanista.mobogram.messenger.query;

import android.text.TextUtils;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.MessageObject;
import com.hanista.mobogram.messenger.MessagesController;
import com.hanista.mobogram.messenger.MessagesStorage;
import com.hanista.mobogram.messenger.NotificationCenter;
import com.hanista.mobogram.tgnet.ConnectionsManager;
import com.hanista.mobogram.tgnet.RequestDelegate;
import com.hanista.mobogram.tgnet.TLObject;
import com.hanista.mobogram.tgnet.TLRPC.InputPeer;
import com.hanista.mobogram.tgnet.TLRPC.Message;
import com.hanista.mobogram.tgnet.TLRPC.TL_error;
import com.hanista.mobogram.tgnet.TLRPC.TL_inputMessagesFilterEmpty;
import com.hanista.mobogram.tgnet.TLRPC.TL_messages_messagesSlice;
import com.hanista.mobogram.tgnet.TLRPC.TL_messages_search;
import com.hanista.mobogram.tgnet.TLRPC.messages_Messages;
import java.util.ArrayList;

public class MessagesSearchQuery {
    private static long lastMergeDialogId;
    private static int lastReqId;
    private static int lastReturnedNum;
    private static String lastSearchQuery;
    private static int mergeReqId;
    private static int[] messagesSearchCount;
    private static boolean[] messagesSearchEndReached;
    private static int reqId;
    private static ArrayList<MessageObject> searchResultMessages;

    /* renamed from: com.hanista.mobogram.messenger.query.MessagesSearchQuery.1 */
    static class C07871 implements RequestDelegate {
        final /* synthetic */ long val$dialog_id;
        final /* synthetic */ int val$direction;
        final /* synthetic */ int val$guid;
        final /* synthetic */ long val$mergeDialogId;
        final /* synthetic */ TL_messages_search val$req;

        /* renamed from: com.hanista.mobogram.messenger.query.MessagesSearchQuery.1.1 */
        class C07861 implements Runnable {
            final /* synthetic */ TLObject val$response;

            C07861(TLObject tLObject) {
                this.val$response = tLObject;
            }

            public void run() {
                if (MessagesSearchQuery.lastMergeDialogId == C07871.this.val$mergeDialogId) {
                    MessagesSearchQuery.mergeReqId = 0;
                    if (this.val$response != null) {
                        messages_Messages com_hanista_mobogram_tgnet_TLRPC_messages_Messages = (messages_Messages) this.val$response;
                        MessagesSearchQuery.messagesSearchEndReached[1] = com_hanista_mobogram_tgnet_TLRPC_messages_Messages.messages.isEmpty();
                        MessagesSearchQuery.messagesSearchCount[1] = com_hanista_mobogram_tgnet_TLRPC_messages_Messages instanceof TL_messages_messagesSlice ? com_hanista_mobogram_tgnet_TLRPC_messages_Messages.count : com_hanista_mobogram_tgnet_TLRPC_messages_Messages.messages.size();
                        MessagesSearchQuery.searchMessagesInChat(C07871.this.val$req.f2672q, C07871.this.val$dialog_id, C07871.this.val$mergeDialogId, C07871.this.val$guid, C07871.this.val$direction, true);
                    }
                }
            }
        }

        C07871(long j, TL_messages_search tL_messages_search, long j2, int i, int i2) {
            this.val$mergeDialogId = j;
            this.val$req = tL_messages_search;
            this.val$dialog_id = j2;
            this.val$guid = i;
            this.val$direction = i2;
        }

        public void run(TLObject tLObject, TL_error tL_error) {
            AndroidUtilities.runOnUIThread(new C07861(tLObject));
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.query.MessagesSearchQuery.2 */
    static class C07892 implements RequestDelegate {
        final /* synthetic */ int val$currentReqId;
        final /* synthetic */ long val$dialog_id;
        final /* synthetic */ int val$guid;
        final /* synthetic */ long val$mergeDialogId;
        final /* synthetic */ long val$queryWithDialogFinal;
        final /* synthetic */ TL_messages_search val$req;

        /* renamed from: com.hanista.mobogram.messenger.query.MessagesSearchQuery.2.1 */
        class C07881 implements Runnable {
            final /* synthetic */ TLObject val$response;

            C07881(TLObject tLObject) {
                this.val$response = tLObject;
            }

            public void run() {
                if (C07892.this.val$currentReqId == MessagesSearchQuery.lastReqId) {
                    MessagesSearchQuery.reqId = 0;
                    if (this.val$response != null) {
                        messages_Messages com_hanista_mobogram_tgnet_TLRPC_messages_Messages = (messages_Messages) this.val$response;
                        MessagesStorage.getInstance().putUsersAndChats(com_hanista_mobogram_tgnet_TLRPC_messages_Messages.users, com_hanista_mobogram_tgnet_TLRPC_messages_Messages.chats, true, true);
                        MessagesController.getInstance().putUsers(com_hanista_mobogram_tgnet_TLRPC_messages_Messages.users, false);
                        MessagesController.getInstance().putChats(com_hanista_mobogram_tgnet_TLRPC_messages_Messages.chats, false);
                        if (C07892.this.val$req.max_id == 0 && C07892.this.val$queryWithDialogFinal == C07892.this.val$dialog_id) {
                            MessagesSearchQuery.lastReturnedNum = 0;
                            MessagesSearchQuery.searchResultMessages.clear();
                            MessagesSearchQuery.messagesSearchCount[0] = 0;
                        }
                        int i = 0;
                        boolean z = false;
                        while (i < Math.min(com_hanista_mobogram_tgnet_TLRPC_messages_Messages.messages.size(), 20)) {
                            MessagesSearchQuery.searchResultMessages.add(new MessageObject((Message) com_hanista_mobogram_tgnet_TLRPC_messages_Messages.messages.get(i), null, false));
                            i++;
                            z = true;
                        }
                        MessagesSearchQuery.messagesSearchEndReached[C07892.this.val$queryWithDialogFinal == C07892.this.val$dialog_id ? 0 : 1] = com_hanista_mobogram_tgnet_TLRPC_messages_Messages.messages.size() != 21;
                        MessagesSearchQuery.messagesSearchCount[C07892.this.val$queryWithDialogFinal == C07892.this.val$dialog_id ? 0 : 1] = com_hanista_mobogram_tgnet_TLRPC_messages_Messages instanceof TL_messages_messagesSlice ? com_hanista_mobogram_tgnet_TLRPC_messages_Messages.count : com_hanista_mobogram_tgnet_TLRPC_messages_Messages.messages.size();
                        if (MessagesSearchQuery.searchResultMessages.isEmpty()) {
                            NotificationCenter.getInstance().postNotificationName(NotificationCenter.chatSearchResultsAvailable, Integer.valueOf(C07892.this.val$guid), Integer.valueOf(0), Integer.valueOf(MessagesSearchQuery.getMask()), Long.valueOf(0), Integer.valueOf(0), Integer.valueOf(0));
                        } else if (z) {
                            if (MessagesSearchQuery.lastReturnedNum >= MessagesSearchQuery.searchResultMessages.size()) {
                                MessagesSearchQuery.lastReturnedNum = MessagesSearchQuery.searchResultMessages.size() - 1;
                            }
                            MessageObject messageObject = (MessageObject) MessagesSearchQuery.searchResultMessages.get(MessagesSearchQuery.lastReturnedNum);
                            NotificationCenter.getInstance().postNotificationName(NotificationCenter.chatSearchResultsAvailable, Integer.valueOf(C07892.this.val$guid), Integer.valueOf(messageObject.getId()), Integer.valueOf(MessagesSearchQuery.getMask()), Long.valueOf(messageObject.getDialogId()), Integer.valueOf(MessagesSearchQuery.lastReturnedNum), Integer.valueOf(MessagesSearchQuery.messagesSearchCount[0] + MessagesSearchQuery.messagesSearchCount[1]));
                        }
                        if (C07892.this.val$queryWithDialogFinal == C07892.this.val$dialog_id && MessagesSearchQuery.messagesSearchEndReached[0] && C07892.this.val$mergeDialogId != 0 && !MessagesSearchQuery.messagesSearchEndReached[1]) {
                            MessagesSearchQuery.searchMessagesInChat(MessagesSearchQuery.lastSearchQuery, C07892.this.val$dialog_id, C07892.this.val$mergeDialogId, C07892.this.val$guid, 0, true);
                        }
                    }
                }
            }
        }

        C07892(int i, TL_messages_search tL_messages_search, long j, long j2, int i2, long j3) {
            this.val$currentReqId = i;
            this.val$req = tL_messages_search;
            this.val$queryWithDialogFinal = j;
            this.val$dialog_id = j2;
            this.val$guid = i2;
            this.val$mergeDialogId = j3;
        }

        public void run(TLObject tLObject, TL_error tL_error) {
            AndroidUtilities.runOnUIThread(new C07881(tLObject));
        }
    }

    static {
        messagesSearchCount = new int[]{0, 0};
        messagesSearchEndReached = new boolean[]{false, false};
        searchResultMessages = new ArrayList();
    }

    public static String getLastSearchQuery() {
        return lastSearchQuery;
    }

    private static int getMask() {
        int i = 0;
        if (!(lastReturnedNum >= searchResultMessages.size() - 1 && messagesSearchEndReached[0] && messagesSearchEndReached[1])) {
            i = 1;
        }
        return lastReturnedNum > 0 ? i | 2 : i;
    }

    public static void searchMessagesInChat(String str, long j, long j2, int i, int i2) {
        searchMessagesInChat(str, j, j2, i, i2, false);
    }

    private static void searchMessagesInChat(String str, long j, long j2, int i, int i2, boolean z) {
        long j3;
        int i3;
        TLObject tL_messages_search;
        int i4 = 0;
        Object obj = !z ? 1 : null;
        if (reqId != 0) {
            ConnectionsManager.getInstance().cancelRequest(reqId, true);
            reqId = 0;
        }
        if (mergeReqId != 0) {
            ConnectionsManager.getInstance().cancelRequest(mergeReqId, true);
            mergeReqId = 0;
        }
        if (!TextUtils.isEmpty(str)) {
            if (obj != null) {
                boolean[] zArr = messagesSearchEndReached;
                messagesSearchEndReached[1] = false;
                zArr[0] = false;
                int[] iArr = messagesSearchCount;
                messagesSearchCount[1] = 0;
                iArr[0] = 0;
                searchResultMessages.clear();
            }
            j3 = j;
        } else if (!searchResultMessages.isEmpty()) {
            MessageObject messageObject;
            if (i2 == 1) {
                lastReturnedNum++;
                if (lastReturnedNum < searchResultMessages.size()) {
                    messageObject = (MessageObject) searchResultMessages.get(lastReturnedNum);
                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.chatSearchResultsAvailable, Integer.valueOf(i), Integer.valueOf(messageObject.getId()), Integer.valueOf(getMask()), Long.valueOf(messageObject.getDialogId()), Integer.valueOf(lastReturnedNum), Integer.valueOf(messagesSearchCount[0] + messagesSearchCount[1]));
                    return;
                } else if (messagesSearchEndReached[0] && j2 == 0 && messagesSearchEndReached[1]) {
                    lastReturnedNum--;
                    return;
                } else {
                    long j4;
                    str = lastSearchQuery;
                    messageObject = (MessageObject) searchResultMessages.get(searchResultMessages.size() - 1);
                    if (messageObject.getDialogId() != j || messagesSearchEndReached[0]) {
                        int id = messageObject.getDialogId() == j2 ? messageObject.getId() : 0;
                        messagesSearchEndReached[1] = false;
                        i3 = id;
                        j4 = j2;
                    } else {
                        i3 = messageObject.getId();
                        j4 = j;
                    }
                    long j5 = j4;
                    obj = null;
                    i4 = i3;
                    j3 = j5;
                }
            } else if (i2 == 2) {
                lastReturnedNum--;
                if (lastReturnedNum < 0) {
                    lastReturnedNum = 0;
                    return;
                }
                if (lastReturnedNum >= searchResultMessages.size()) {
                    lastReturnedNum = searchResultMessages.size() - 1;
                }
                messageObject = (MessageObject) searchResultMessages.get(lastReturnedNum);
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.chatSearchResultsAvailable, Integer.valueOf(i), Integer.valueOf(messageObject.getId()), Integer.valueOf(getMask()), Long.valueOf(messageObject.getDialogId()), Integer.valueOf(lastReturnedNum), Integer.valueOf(messagesSearchCount[0] + messagesSearchCount[1]));
                return;
            } else {
                return;
            }
        } else {
            return;
        }
        long j6 = (!messagesSearchEndReached[0] || messagesSearchEndReached[1] || j2 == 0) ? j3 : j2;
        if (j6 == j && r2 != null) {
            if (j2 != 0) {
                InputPeer inputPeer = MessagesController.getInputPeer((int) j2);
                if (inputPeer != null) {
                    tL_messages_search = new TL_messages_search();
                    tL_messages_search.peer = inputPeer;
                    lastMergeDialogId = j2;
                    tL_messages_search.limit = 1;
                    tL_messages_search.f2672q = str;
                    tL_messages_search.filter = new TL_inputMessagesFilterEmpty();
                    mergeReqId = ConnectionsManager.getInstance().sendRequest(tL_messages_search, new C07871(j2, tL_messages_search, j, i, i2), 2);
                    return;
                }
                return;
            }
            lastMergeDialogId = 0;
            messagesSearchEndReached[1] = true;
            messagesSearchCount[1] = 0;
        }
        tL_messages_search = new TL_messages_search();
        tL_messages_search.peer = MessagesController.getInputPeer((int) j6);
        if (tL_messages_search.peer != null) {
            tL_messages_search.limit = 21;
            tL_messages_search.f2672q = str;
            tL_messages_search.max_id = i4;
            tL_messages_search.filter = new TL_inputMessagesFilterEmpty();
            i3 = lastReqId + 1;
            lastReqId = i3;
            lastSearchQuery = str;
            reqId = ConnectionsManager.getInstance().sendRequest(tL_messages_search, new C07892(i3, tL_messages_search, j6, j, i, j2), 2);
        }
    }
}
