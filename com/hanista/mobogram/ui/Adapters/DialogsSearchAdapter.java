package com.hanista.mobogram.ui.Adapters;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.SQLite.SQLiteCursor;
import com.hanista.mobogram.SQLite.SQLitePreparedStatement;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.ContactsController;
import com.hanista.mobogram.messenger.FileLog;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.MessageObject;
import com.hanista.mobogram.messenger.MessagesController;
import com.hanista.mobogram.messenger.MessagesStorage;
import com.hanista.mobogram.messenger.query.SearchQuery;
import com.hanista.mobogram.messenger.support.widget.LinearLayoutManager;
import com.hanista.mobogram.messenger.support.widget.RecyclerView.Adapter;
import com.hanista.mobogram.messenger.support.widget.RecyclerView.LayoutManager;
import com.hanista.mobogram.messenger.support.widget.RecyclerView.LayoutParams;
import com.hanista.mobogram.messenger.support.widget.RecyclerView.ViewHolder;
import com.hanista.mobogram.mobo.p012l.HiddenConfig;
import com.hanista.mobogram.mobo.p020s.AdvanceTheme;
import com.hanista.mobogram.mobo.p020s.ThemeUtil;
import com.hanista.mobogram.tgnet.ConnectionsManager;
import com.hanista.mobogram.tgnet.RequestDelegate;
import com.hanista.mobogram.tgnet.TLObject;
import com.hanista.mobogram.tgnet.TLRPC.Chat;
import com.hanista.mobogram.tgnet.TLRPC.EncryptedChat;
import com.hanista.mobogram.tgnet.TLRPC.Message;
import com.hanista.mobogram.tgnet.TLRPC.TL_dialog;
import com.hanista.mobogram.tgnet.TLRPC.TL_error;
import com.hanista.mobogram.tgnet.TLRPC.TL_inputPeerEmpty;
import com.hanista.mobogram.tgnet.TLRPC.TL_messages_searchGlobal;
import com.hanista.mobogram.tgnet.TLRPC.TL_topPeer;
import com.hanista.mobogram.tgnet.TLRPC.User;
import com.hanista.mobogram.tgnet.TLRPC.messages_Messages;
import com.hanista.mobogram.ui.ActionBar.Theme;
import com.hanista.mobogram.ui.Cells.DialogCell;
import com.hanista.mobogram.ui.Cells.GreySectionCell;
import com.hanista.mobogram.ui.Cells.HashtagSearchCell;
import com.hanista.mobogram.ui.Cells.HintDialogCell;
import com.hanista.mobogram.ui.Cells.LoadingCell;
import com.hanista.mobogram.ui.Cells.ProfileSearchCell;
import com.hanista.mobogram.ui.Components.RecyclerListView;
import com.hanista.mobogram.ui.Components.RecyclerListView.OnItemClickListener;
import com.hanista.mobogram.ui.Components.RecyclerListView.OnItemLongClickListener;
import com.hanista.mobogram.ui.Components.VideoPlayer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

public class DialogsSearchAdapter extends BaseSearchAdapterRecycler {
    private DialogsSearchAdapterDelegate delegate;
    private int dialogsType;
    private String lastMessagesSearchString;
    private int lastReqId;
    private int lastSearchId;
    private String lastSearchText;
    private Context mContext;
    private boolean messagesSearchEndReached;
    private int needMessagesSearch;
    private ArrayList<RecentSearchObject> recentSearchObjects;
    private HashMap<Long, RecentSearchObject> recentSearchObjectsById;
    private int reqId;
    private ArrayList<TLObject> searchResult;
    private ArrayList<String> searchResultHashtags;
    private ArrayList<MessageObject> searchResultMessages;
    private ArrayList<CharSequence> searchResultNames;
    private Timer searchTimer;

    /* renamed from: com.hanista.mobogram.ui.Adapters.DialogsSearchAdapter.1 */
    class C10141 implements RequestDelegate {
        final /* synthetic */ int val$currentReqId;
        final /* synthetic */ TL_messages_searchGlobal val$req;

        /* renamed from: com.hanista.mobogram.ui.Adapters.DialogsSearchAdapter.1.1 */
        class C10131 implements Runnable {
            final /* synthetic */ TL_error val$error;
            final /* synthetic */ TLObject val$response;

            C10131(TL_error tL_error, TLObject tLObject) {
                this.val$error = tL_error;
                this.val$response = tLObject;
            }

            public void run() {
                boolean z = true;
                if (C10141.this.val$currentReqId == DialogsSearchAdapter.this.lastReqId && this.val$error == null) {
                    messages_Messages com_hanista_mobogram_tgnet_TLRPC_messages_Messages = (messages_Messages) this.val$response;
                    MessagesStorage.getInstance().putUsersAndChats(com_hanista_mobogram_tgnet_TLRPC_messages_Messages.users, com_hanista_mobogram_tgnet_TLRPC_messages_Messages.chats, true, true);
                    MessagesController.getInstance().putUsers(com_hanista_mobogram_tgnet_TLRPC_messages_Messages.users, false);
                    MessagesController.getInstance().putChats(com_hanista_mobogram_tgnet_TLRPC_messages_Messages.chats, false);
                    if (C10141.this.val$req.offset_id == 0) {
                        DialogsSearchAdapter.this.searchResultMessages.clear();
                    }
                    for (int i = 0; i < com_hanista_mobogram_tgnet_TLRPC_messages_Messages.messages.size(); i++) {
                        Message message = (Message) com_hanista_mobogram_tgnet_TLRPC_messages_Messages.messages.get(i);
                        if (!HiddenConfig.m1399b(Long.valueOf(MessageObject.getDialogId(message))) || HiddenConfig.f1402e) {
                            DialogsSearchAdapter.this.searchResultMessages.add(new MessageObject(message, null, false));
                            long dialogId = MessageObject.getDialogId(message);
                            ConcurrentHashMap concurrentHashMap = message.out ? MessagesController.getInstance().dialogs_read_outbox_max : MessagesController.getInstance().dialogs_read_inbox_max;
                            Integer num = (Integer) concurrentHashMap.get(Long.valueOf(dialogId));
                            if (num == null) {
                                num = Integer.valueOf(MessagesStorage.getInstance().getDialogReadMax(message.out, dialogId));
                                concurrentHashMap.put(Long.valueOf(dialogId), num);
                            }
                            message.unread = num.intValue() < message.id;
                        }
                    }
                    DialogsSearchAdapter dialogsSearchAdapter = DialogsSearchAdapter.this;
                    if (com_hanista_mobogram_tgnet_TLRPC_messages_Messages.messages.size() == 20) {
                        z = false;
                    }
                    dialogsSearchAdapter.messagesSearchEndReached = z;
                    DialogsSearchAdapter.this.notifyDataSetChanged();
                }
                if (DialogsSearchAdapter.this.delegate != null) {
                    DialogsSearchAdapter.this.delegate.searchStateChanged(false);
                }
                DialogsSearchAdapter.this.reqId = 0;
            }
        }

        C10141(int i, TL_messages_searchGlobal tL_messages_searchGlobal) {
            this.val$currentReqId = i;
            this.val$req = tL_messages_searchGlobal;
        }

        public void run(TLObject tLObject, TL_error tL_error) {
            AndroidUtilities.runOnUIThread(new C10131(tL_error, tLObject));
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Adapters.DialogsSearchAdapter.2 */
    class C10172 implements Runnable {

        /* renamed from: com.hanista.mobogram.ui.Adapters.DialogsSearchAdapter.2.1 */
        class C10151 implements Comparator<RecentSearchObject> {
            C10151() {
            }

            public int compare(RecentSearchObject recentSearchObject, RecentSearchObject recentSearchObject2) {
                return recentSearchObject.date < recentSearchObject2.date ? 1 : recentSearchObject.date > recentSearchObject2.date ? -1 : 0;
            }
        }

        /* renamed from: com.hanista.mobogram.ui.Adapters.DialogsSearchAdapter.2.2 */
        class C10162 implements Runnable {
            final /* synthetic */ ArrayList val$arrayList;
            final /* synthetic */ HashMap val$hashMap;

            C10162(ArrayList arrayList, HashMap hashMap) {
                this.val$arrayList = arrayList;
                this.val$hashMap = hashMap;
            }

            public void run() {
                DialogsSearchAdapter.this.setRecentSearch(this.val$arrayList, this.val$hashMap);
            }
        }

        C10172() {
        }

        public void run() {
            int i = 0;
            try {
                RecentSearchObject recentSearchObject;
                ArrayList arrayList;
                int i2;
                SQLiteCursor queryFinalized = MessagesStorage.getInstance().getDatabase().queryFinalized("SELECT did, date FROM search_recent WHERE 1", new Object[0]);
                Iterable arrayList2 = new ArrayList();
                Iterable arrayList3 = new ArrayList();
                Iterable arrayList4 = new ArrayList();
                ArrayList arrayList5 = new ArrayList();
                Object arrayList6 = new ArrayList();
                HashMap hashMap = new HashMap();
                while (queryFinalized.next()) {
                    long longValue = queryFinalized.longValue(0);
                    if (!HiddenConfig.m1399b(Long.valueOf(longValue))) {
                        int i3 = (int) longValue;
                        int i4 = (int) (longValue >> 32);
                        if (i3 == 0) {
                            if (DialogsSearchAdapter.this.dialogsType == 0 && !arrayList4.contains(Integer.valueOf(i4))) {
                                arrayList4.add(Integer.valueOf(i4));
                                i3 = 1;
                            }
                            i3 = 0;
                        } else if (i4 == 1) {
                            if (DialogsSearchAdapter.this.dialogsType == 0 && !arrayList3.contains(Integer.valueOf(i3))) {
                                arrayList3.add(Integer.valueOf(i3));
                                i3 = 1;
                            }
                            i3 = 0;
                        } else if (i3 > 0) {
                            if (!(DialogsSearchAdapter.this.dialogsType == 2 || arrayList2.contains(Integer.valueOf(i3)))) {
                                arrayList2.add(Integer.valueOf(i3));
                                i3 = 1;
                            }
                            i3 = 0;
                        } else {
                            if (!arrayList3.contains(Integer.valueOf(-i3))) {
                                arrayList3.add(Integer.valueOf(-i3));
                                i3 = 1;
                            }
                            i3 = 0;
                        }
                        if (i3 != 0) {
                            recentSearchObject = new RecentSearchObject();
                            recentSearchObject.did = longValue;
                            recentSearchObject.date = queryFinalized.intValue(1);
                            arrayList6.add(recentSearchObject);
                            hashMap.put(Long.valueOf(recentSearchObject.did), recentSearchObject);
                        }
                    }
                }
                queryFinalized.dispose();
                ArrayList arrayList7 = new ArrayList();
                if (!arrayList4.isEmpty()) {
                    arrayList = new ArrayList();
                    MessagesStorage.getInstance().getEncryptedChatsInternal(TextUtils.join(",", arrayList4), arrayList, arrayList2);
                    for (i2 = 0; i2 < arrayList.size(); i2++) {
                        ((RecentSearchObject) hashMap.get(Long.valueOf(((long) ((EncryptedChat) arrayList.get(i2)).id) << 32))).object = (TLObject) arrayList.get(i2);
                    }
                }
                if (!arrayList3.isEmpty()) {
                    arrayList = new ArrayList();
                    MessagesStorage.getInstance().getChatsInternal(TextUtils.join(",", arrayList3), arrayList);
                    for (i2 = 0; i2 < arrayList.size(); i2++) {
                        Chat chat = (Chat) arrayList.get(i2);
                        long makeBroadcastId = chat.id > 0 ? (long) (-chat.id) : AndroidUtilities.makeBroadcastId(chat.id);
                        if (chat.migrated_to != null) {
                            recentSearchObject = (RecentSearchObject) hashMap.remove(Long.valueOf(makeBroadcastId));
                            if (recentSearchObject != null) {
                                arrayList6.remove(recentSearchObject);
                            }
                        } else {
                            ((RecentSearchObject) hashMap.get(Long.valueOf(makeBroadcastId))).object = chat;
                        }
                    }
                }
                if (!arrayList2.isEmpty()) {
                    MessagesStorage.getInstance().getUsersInternal(TextUtils.join(",", arrayList2), arrayList7);
                    while (i < arrayList7.size()) {
                        User user = (User) arrayList7.get(i);
                        RecentSearchObject recentSearchObject2 = (RecentSearchObject) hashMap.get(Long.valueOf((long) user.id));
                        if (recentSearchObject2 != null) {
                            recentSearchObject2.object = user;
                        }
                        i++;
                    }
                }
                Collections.sort(arrayList6, new C10151());
                AndroidUtilities.runOnUIThread(new C10162(arrayList6, hashMap));
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Adapters.DialogsSearchAdapter.3 */
    class C10183 implements Runnable {
        final /* synthetic */ long val$did;

        C10183(long j) {
            this.val$did = j;
        }

        public void run() {
            try {
                SQLitePreparedStatement executeFast = MessagesStorage.getInstance().getDatabase().executeFast("REPLACE INTO search_recent VALUES(?, ?)");
                executeFast.requery();
                executeFast.bindLong(1, this.val$did);
                executeFast.bindInteger(2, (int) (System.currentTimeMillis() / 1000));
                executeFast.step();
                executeFast.dispose();
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Adapters.DialogsSearchAdapter.4 */
    class C10194 implements Runnable {
        C10194() {
        }

        public void run() {
            try {
                MessagesStorage.getInstance().getDatabase().executeFast("DELETE FROM search_recent WHERE 1").stepThis().dispose();
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Adapters.DialogsSearchAdapter.5 */
    class C10215 implements Runnable {
        final /* synthetic */ String val$query;
        final /* synthetic */ int val$searchId;

        /* renamed from: com.hanista.mobogram.ui.Adapters.DialogsSearchAdapter.5.1 */
        class C10201 implements Comparator<DialogSearchResult> {
            C10201() {
            }

            public int compare(DialogSearchResult dialogSearchResult, DialogSearchResult dialogSearchResult2) {
                return dialogSearchResult.date < dialogSearchResult2.date ? 1 : dialogSearchResult.date > dialogSearchResult2.date ? -1 : 0;
            }
        }

        C10215(String str, int i) {
            this.val$query = str;
            this.val$searchId = i;
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void run() {
            /*
            r19 = this;
            r0 = r19;
            r2 = r0.val$query;	 Catch:{ Exception -> 0x00e1 }
            r2 = r2.trim();	 Catch:{ Exception -> 0x00e1 }
            r4 = r2.toLowerCase();	 Catch:{ Exception -> 0x00e1 }
            r2 = r4.length();	 Catch:{ Exception -> 0x00e1 }
            if (r2 != 0) goto L_0x0039;
        L_0x0012:
            r0 = r19;
            r2 = com.hanista.mobogram.ui.Adapters.DialogsSearchAdapter.this;	 Catch:{ Exception -> 0x00e1 }
            r3 = -1;
            r2.lastSearchId = r3;	 Catch:{ Exception -> 0x00e1 }
            r0 = r19;
            r2 = com.hanista.mobogram.ui.Adapters.DialogsSearchAdapter.this;	 Catch:{ Exception -> 0x00e1 }
            r3 = new java.util.ArrayList;	 Catch:{ Exception -> 0x00e1 }
            r3.<init>();	 Catch:{ Exception -> 0x00e1 }
            r4 = new java.util.ArrayList;	 Catch:{ Exception -> 0x00e1 }
            r4.<init>();	 Catch:{ Exception -> 0x00e1 }
            r5 = new java.util.ArrayList;	 Catch:{ Exception -> 0x00e1 }
            r5.<init>();	 Catch:{ Exception -> 0x00e1 }
            r0 = r19;
            r6 = com.hanista.mobogram.ui.Adapters.DialogsSearchAdapter.this;	 Catch:{ Exception -> 0x00e1 }
            r6 = r6.lastSearchId;	 Catch:{ Exception -> 0x00e1 }
            r2.updateSearchResults(r3, r4, r5, r6);	 Catch:{ Exception -> 0x00e1 }
        L_0x0038:
            return;
        L_0x0039:
            r2 = com.hanista.mobogram.messenger.LocaleController.getInstance();	 Catch:{ Exception -> 0x00e1 }
            r2 = r2.getTranslitString(r4);	 Catch:{ Exception -> 0x00e1 }
            r3 = r4.equals(r2);	 Catch:{ Exception -> 0x00e1 }
            if (r3 != 0) goto L_0x004d;
        L_0x0047:
            r3 = r2.length();	 Catch:{ Exception -> 0x00e1 }
            if (r3 != 0) goto L_0x0700;
        L_0x004d:
            r2 = 0;
            r3 = r2;
        L_0x004f:
            if (r3 == 0) goto L_0x00ea;
        L_0x0051:
            r2 = 1;
        L_0x0052:
            r2 = r2 + 1;
            r8 = new java.lang.String[r2];	 Catch:{ Exception -> 0x00e1 }
            r2 = 0;
            r8[r2] = r4;	 Catch:{ Exception -> 0x00e1 }
            if (r3 == 0) goto L_0x005e;
        L_0x005b:
            r2 = 1;
            r8[r2] = r3;	 Catch:{ Exception -> 0x00e1 }
        L_0x005e:
            r2 = new java.util.ArrayList;	 Catch:{ Exception -> 0x00e1 }
            r2.<init>();	 Catch:{ Exception -> 0x00e1 }
            r9 = new java.util.ArrayList;	 Catch:{ Exception -> 0x00e1 }
            r9.<init>();	 Catch:{ Exception -> 0x00e1 }
            r10 = new java.util.ArrayList;	 Catch:{ Exception -> 0x00e1 }
            r10.<init>();	 Catch:{ Exception -> 0x00e1 }
            r11 = new java.util.ArrayList;	 Catch:{ Exception -> 0x00e1 }
            r11.<init>();	 Catch:{ Exception -> 0x00e1 }
            r3 = 0;
            r12 = new java.util.HashMap;	 Catch:{ Exception -> 0x00e1 }
            r12.<init>();	 Catch:{ Exception -> 0x00e1 }
            r4 = com.hanista.mobogram.messenger.MessagesStorage.getInstance();	 Catch:{ Exception -> 0x00e1 }
            r4 = r4.getDatabase();	 Catch:{ Exception -> 0x00e1 }
            r5 = "SELECT did, date FROM dialogs ORDER BY date DESC LIMIT 400";
            r6 = 0;
            r6 = new java.lang.Object[r6];	 Catch:{ Exception -> 0x00e1 }
            r4 = r4.queryFinalized(r5, r6);	 Catch:{ Exception -> 0x00e1 }
        L_0x008a:
            r5 = r4.next();	 Catch:{ Exception -> 0x00e1 }
            if (r5 == 0) goto L_0x013f;
        L_0x0090:
            r5 = 0;
            r6 = r4.longValue(r5);	 Catch:{ Exception -> 0x00e1 }
            r5 = java.lang.Long.valueOf(r6);	 Catch:{ Exception -> 0x00e1 }
            r5 = com.hanista.mobogram.mobo.p012l.HiddenConfig.m1399b(r5);	 Catch:{ Exception -> 0x00e1 }
            if (r5 == 0) goto L_0x00a3;
        L_0x009f:
            r5 = com.hanista.mobogram.mobo.p012l.HiddenConfig.f1402e;	 Catch:{ Exception -> 0x00e1 }
            if (r5 == 0) goto L_0x008a;
        L_0x00a3:
            r5 = new com.hanista.mobogram.ui.Adapters.DialogsSearchAdapter$DialogSearchResult;	 Catch:{ Exception -> 0x00e1 }
            r0 = r19;
            r13 = com.hanista.mobogram.ui.Adapters.DialogsSearchAdapter.this;	 Catch:{ Exception -> 0x00e1 }
            r14 = 0;
            r5.<init>(r14);	 Catch:{ Exception -> 0x00e1 }
            r13 = 1;
            r13 = r4.intValue(r13);	 Catch:{ Exception -> 0x00e1 }
            r5.date = r13;	 Catch:{ Exception -> 0x00e1 }
            r13 = java.lang.Long.valueOf(r6);	 Catch:{ Exception -> 0x00e1 }
            r12.put(r13, r5);	 Catch:{ Exception -> 0x00e1 }
            r5 = (int) r6;	 Catch:{ Exception -> 0x00e1 }
            r13 = 32;
            r6 = r6 >> r13;
            r6 = (int) r6;	 Catch:{ Exception -> 0x00e1 }
            if (r5 == 0) goto L_0x0122;
        L_0x00c2:
            r7 = 1;
            if (r6 != r7) goto L_0x00ed;
        L_0x00c5:
            r0 = r19;
            r6 = com.hanista.mobogram.ui.Adapters.DialogsSearchAdapter.this;	 Catch:{ Exception -> 0x00e1 }
            r6 = r6.dialogsType;	 Catch:{ Exception -> 0x00e1 }
            if (r6 != 0) goto L_0x008a;
        L_0x00cf:
            r6 = java.lang.Integer.valueOf(r5);	 Catch:{ Exception -> 0x00e1 }
            r6 = r9.contains(r6);	 Catch:{ Exception -> 0x00e1 }
            if (r6 != 0) goto L_0x008a;
        L_0x00d9:
            r5 = java.lang.Integer.valueOf(r5);	 Catch:{ Exception -> 0x00e1 }
            r9.add(r5);	 Catch:{ Exception -> 0x00e1 }
            goto L_0x008a;
        L_0x00e1:
            r2 = move-exception;
            r3 = "tmessages";
            com.hanista.mobogram.messenger.FileLog.m18e(r3, r2);
            goto L_0x0038;
        L_0x00ea:
            r2 = 0;
            goto L_0x0052;
        L_0x00ed:
            if (r5 <= 0) goto L_0x010d;
        L_0x00ef:
            r0 = r19;
            r6 = com.hanista.mobogram.ui.Adapters.DialogsSearchAdapter.this;	 Catch:{ Exception -> 0x00e1 }
            r6 = r6.dialogsType;	 Catch:{ Exception -> 0x00e1 }
            r7 = 2;
            if (r6 == r7) goto L_0x008a;
        L_0x00fa:
            r6 = java.lang.Integer.valueOf(r5);	 Catch:{ Exception -> 0x00e1 }
            r6 = r2.contains(r6);	 Catch:{ Exception -> 0x00e1 }
            if (r6 != 0) goto L_0x008a;
        L_0x0104:
            r5 = java.lang.Integer.valueOf(r5);	 Catch:{ Exception -> 0x00e1 }
            r2.add(r5);	 Catch:{ Exception -> 0x00e1 }
            goto L_0x008a;
        L_0x010d:
            r6 = -r5;
            r6 = java.lang.Integer.valueOf(r6);	 Catch:{ Exception -> 0x00e1 }
            r6 = r9.contains(r6);	 Catch:{ Exception -> 0x00e1 }
            if (r6 != 0) goto L_0x008a;
        L_0x0118:
            r5 = -r5;
            r5 = java.lang.Integer.valueOf(r5);	 Catch:{ Exception -> 0x00e1 }
            r9.add(r5);	 Catch:{ Exception -> 0x00e1 }
            goto L_0x008a;
        L_0x0122:
            r0 = r19;
            r5 = com.hanista.mobogram.ui.Adapters.DialogsSearchAdapter.this;	 Catch:{ Exception -> 0x00e1 }
            r5 = r5.dialogsType;	 Catch:{ Exception -> 0x00e1 }
            if (r5 != 0) goto L_0x008a;
        L_0x012c:
            r5 = java.lang.Integer.valueOf(r6);	 Catch:{ Exception -> 0x00e1 }
            r5 = r10.contains(r5);	 Catch:{ Exception -> 0x00e1 }
            if (r5 != 0) goto L_0x008a;
        L_0x0136:
            r5 = java.lang.Integer.valueOf(r6);	 Catch:{ Exception -> 0x00e1 }
            r10.add(r5);	 Catch:{ Exception -> 0x00e1 }
            goto L_0x008a;
        L_0x013f:
            r4.dispose();	 Catch:{ Exception -> 0x00e1 }
            r4 = r2.isEmpty();	 Catch:{ Exception -> 0x00e1 }
            if (r4 != 0) goto L_0x0283;
        L_0x0148:
            r4 = com.hanista.mobogram.messenger.MessagesStorage.getInstance();	 Catch:{ Exception -> 0x00e1 }
            r4 = r4.getDatabase();	 Catch:{ Exception -> 0x00e1 }
            r5 = java.util.Locale.US;	 Catch:{ Exception -> 0x00e1 }
            r6 = "SELECT data, status, name FROM users WHERE uid IN(%s)";
            r7 = 1;
            r7 = new java.lang.Object[r7];	 Catch:{ Exception -> 0x00e1 }
            r13 = 0;
            r14 = ",";
            r2 = android.text.TextUtils.join(r14, r2);	 Catch:{ Exception -> 0x00e1 }
            r7[r13] = r2;	 Catch:{ Exception -> 0x00e1 }
            r2 = java.lang.String.format(r5, r6, r7);	 Catch:{ Exception -> 0x00e1 }
            r5 = 0;
            r5 = new java.lang.Object[r5];	 Catch:{ Exception -> 0x00e1 }
            r13 = r4.queryFinalized(r2, r5);	 Catch:{ Exception -> 0x00e1 }
        L_0x016d:
            r2 = r13.next();	 Catch:{ Exception -> 0x00e1 }
            if (r2 == 0) goto L_0x0280;
        L_0x0173:
            r2 = 2;
            r14 = r13.stringValue(r2);	 Catch:{ Exception -> 0x00e1 }
            r2 = com.hanista.mobogram.messenger.LocaleController.getInstance();	 Catch:{ Exception -> 0x00e1 }
            r2 = r2.getTranslitString(r14);	 Catch:{ Exception -> 0x00e1 }
            r4 = r14.equals(r2);	 Catch:{ Exception -> 0x00e1 }
            if (r4 == 0) goto L_0x06fd;
        L_0x0186:
            r2 = 0;
            r7 = r2;
        L_0x0188:
            r2 = 0;
            r4 = ";;;";
            r4 = r14.lastIndexOf(r4);	 Catch:{ Exception -> 0x00e1 }
            r5 = -1;
            if (r4 == r5) goto L_0x06fa;
        L_0x0193:
            r2 = r4 + 3;
            r2 = r14.substring(r2);	 Catch:{ Exception -> 0x00e1 }
            r6 = r2;
        L_0x019a:
            r4 = 0;
            r15 = r8.length;	 Catch:{ Exception -> 0x00e1 }
            r2 = 0;
            r5 = r2;
            r2 = r4;
        L_0x019f:
            if (r5 >= r15) goto L_0x06f4;
        L_0x01a1:
            r16 = r8[r5];	 Catch:{ Exception -> 0x00e1 }
            r0 = r16;
            r4 = r14.startsWith(r0);	 Catch:{ Exception -> 0x00e1 }
            if (r4 != 0) goto L_0x01f1;
        L_0x01ab:
            r4 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x00e1 }
            r4.<init>();	 Catch:{ Exception -> 0x00e1 }
            r17 = " ";
            r0 = r17;
            r4 = r4.append(r0);	 Catch:{ Exception -> 0x00e1 }
            r0 = r16;
            r4 = r4.append(r0);	 Catch:{ Exception -> 0x00e1 }
            r4 = r4.toString();	 Catch:{ Exception -> 0x00e1 }
            r4 = r14.contains(r4);	 Catch:{ Exception -> 0x00e1 }
            if (r4 != 0) goto L_0x01f1;
        L_0x01c9:
            if (r7 == 0) goto L_0x0239;
        L_0x01cb:
            r0 = r16;
            r4 = r7.startsWith(r0);	 Catch:{ Exception -> 0x00e1 }
            if (r4 != 0) goto L_0x01f1;
        L_0x01d3:
            r4 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x00e1 }
            r4.<init>();	 Catch:{ Exception -> 0x00e1 }
            r17 = " ";
            r0 = r17;
            r4 = r4.append(r0);	 Catch:{ Exception -> 0x00e1 }
            r0 = r16;
            r4 = r4.append(r0);	 Catch:{ Exception -> 0x00e1 }
            r4 = r4.toString();	 Catch:{ Exception -> 0x00e1 }
            r4 = r7.contains(r4);	 Catch:{ Exception -> 0x00e1 }
            if (r4 == 0) goto L_0x0239;
        L_0x01f1:
            r2 = 1;
            r4 = r2;
        L_0x01f3:
            if (r4 == 0) goto L_0x027a;
        L_0x01f5:
            r2 = 0;
            r2 = r13.byteBufferValue(r2);	 Catch:{ Exception -> 0x00e1 }
            if (r2 == 0) goto L_0x06f4;
        L_0x01fc:
            r5 = 0;
            r5 = r2.readInt32(r5);	 Catch:{ Exception -> 0x00e1 }
            r6 = 0;
            r5 = com.hanista.mobogram.tgnet.TLRPC.User.TLdeserialize(r2, r5, r6);	 Catch:{ Exception -> 0x00e1 }
            r2.reuse();	 Catch:{ Exception -> 0x00e1 }
            r2 = r5.id;	 Catch:{ Exception -> 0x00e1 }
            r6 = (long) r2;	 Catch:{ Exception -> 0x00e1 }
            r2 = java.lang.Long.valueOf(r6);	 Catch:{ Exception -> 0x00e1 }
            r2 = r12.get(r2);	 Catch:{ Exception -> 0x00e1 }
            r2 = (com.hanista.mobogram.ui.Adapters.DialogsSearchAdapter.DialogSearchResult) r2;	 Catch:{ Exception -> 0x00e1 }
            r6 = r5.status;	 Catch:{ Exception -> 0x00e1 }
            if (r6 == 0) goto L_0x0223;
        L_0x021a:
            r6 = r5.status;	 Catch:{ Exception -> 0x00e1 }
            r7 = 1;
            r7 = r13.intValue(r7);	 Catch:{ Exception -> 0x00e1 }
            r6.expires = r7;	 Catch:{ Exception -> 0x00e1 }
        L_0x0223:
            r6 = 1;
            if (r4 != r6) goto L_0x0246;
        L_0x0226:
            r4 = r5.first_name;	 Catch:{ Exception -> 0x00e1 }
            r6 = r5.last_name;	 Catch:{ Exception -> 0x00e1 }
            r0 = r16;
            r4 = com.hanista.mobogram.messenger.AndroidUtilities.generateSearchName(r4, r6, r0);	 Catch:{ Exception -> 0x00e1 }
            r2.name = r4;	 Catch:{ Exception -> 0x00e1 }
        L_0x0232:
            r2.object = r5;	 Catch:{ Exception -> 0x00e1 }
            r2 = r3 + 1;
        L_0x0236:
            r3 = r2;
            goto L_0x016d;
        L_0x0239:
            if (r6 == 0) goto L_0x06f7;
        L_0x023b:
            r0 = r16;
            r4 = r6.startsWith(r0);	 Catch:{ Exception -> 0x00e1 }
            if (r4 == 0) goto L_0x06f7;
        L_0x0243:
            r2 = 2;
            r4 = r2;
            goto L_0x01f3;
        L_0x0246:
            r4 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x00e1 }
            r4.<init>();	 Catch:{ Exception -> 0x00e1 }
            r6 = "@";
            r4 = r4.append(r6);	 Catch:{ Exception -> 0x00e1 }
            r6 = r5.username;	 Catch:{ Exception -> 0x00e1 }
            r4 = r4.append(r6);	 Catch:{ Exception -> 0x00e1 }
            r4 = r4.toString();	 Catch:{ Exception -> 0x00e1 }
            r6 = 0;
            r7 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x00e1 }
            r7.<init>();	 Catch:{ Exception -> 0x00e1 }
            r14 = "@";
            r7 = r7.append(r14);	 Catch:{ Exception -> 0x00e1 }
            r0 = r16;
            r7 = r7.append(r0);	 Catch:{ Exception -> 0x00e1 }
            r7 = r7.toString();	 Catch:{ Exception -> 0x00e1 }
            r4 = com.hanista.mobogram.messenger.AndroidUtilities.generateSearchName(r4, r6, r7);	 Catch:{ Exception -> 0x00e1 }
            r2.name = r4;	 Catch:{ Exception -> 0x00e1 }
            goto L_0x0232;
        L_0x027a:
            r2 = r5 + 1;
            r5 = r2;
            r2 = r4;
            goto L_0x019f;
        L_0x0280:
            r13.dispose();	 Catch:{ Exception -> 0x00e1 }
        L_0x0283:
            r2 = r9.isEmpty();	 Catch:{ Exception -> 0x00e1 }
            if (r2 != 0) goto L_0x0367;
        L_0x0289:
            r2 = com.hanista.mobogram.messenger.MessagesStorage.getInstance();	 Catch:{ Exception -> 0x00e1 }
            r2 = r2.getDatabase();	 Catch:{ Exception -> 0x00e1 }
            r4 = java.util.Locale.US;	 Catch:{ Exception -> 0x00e1 }
            r5 = "SELECT data, name FROM chats WHERE uid IN(%s)";
            r6 = 1;
            r6 = new java.lang.Object[r6];	 Catch:{ Exception -> 0x00e1 }
            r7 = 0;
            r13 = ",";
            r9 = android.text.TextUtils.join(r13, r9);	 Catch:{ Exception -> 0x00e1 }
            r6[r7] = r9;	 Catch:{ Exception -> 0x00e1 }
            r4 = java.lang.String.format(r4, r5, r6);	 Catch:{ Exception -> 0x00e1 }
            r5 = 0;
            r5 = new java.lang.Object[r5];	 Catch:{ Exception -> 0x00e1 }
            r6 = r2.queryFinalized(r4, r5);	 Catch:{ Exception -> 0x00e1 }
        L_0x02ae:
            r2 = r6.next();	 Catch:{ Exception -> 0x00e1 }
            if (r2 == 0) goto L_0x0364;
        L_0x02b4:
            r2 = 1;
            r5 = r6.stringValue(r2);	 Catch:{ Exception -> 0x00e1 }
            r2 = com.hanista.mobogram.messenger.LocaleController.getInstance();	 Catch:{ Exception -> 0x00e1 }
            r2 = r2.getTranslitString(r5);	 Catch:{ Exception -> 0x00e1 }
            r4 = r5.equals(r2);	 Catch:{ Exception -> 0x00e1 }
            if (r4 == 0) goto L_0x06f1;
        L_0x02c7:
            r2 = 0;
            r4 = r2;
        L_0x02c9:
            r7 = r8.length;	 Catch:{ Exception -> 0x00e1 }
            r2 = 0;
        L_0x02cb:
            if (r2 >= r7) goto L_0x02ae;
        L_0x02cd:
            r9 = r8[r2];	 Catch:{ Exception -> 0x00e1 }
            r13 = r5.startsWith(r9);	 Catch:{ Exception -> 0x00e1 }
            if (r13 != 0) goto L_0x0311;
        L_0x02d5:
            r13 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x00e1 }
            r13.<init>();	 Catch:{ Exception -> 0x00e1 }
            r14 = " ";
            r13 = r13.append(r14);	 Catch:{ Exception -> 0x00e1 }
            r13 = r13.append(r9);	 Catch:{ Exception -> 0x00e1 }
            r13 = r13.toString();	 Catch:{ Exception -> 0x00e1 }
            r13 = r5.contains(r13);	 Catch:{ Exception -> 0x00e1 }
            if (r13 != 0) goto L_0x0311;
        L_0x02ef:
            if (r4 == 0) goto L_0x0360;
        L_0x02f1:
            r13 = r4.startsWith(r9);	 Catch:{ Exception -> 0x00e1 }
            if (r13 != 0) goto L_0x0311;
        L_0x02f7:
            r13 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x00e1 }
            r13.<init>();	 Catch:{ Exception -> 0x00e1 }
            r14 = " ";
            r13 = r13.append(r14);	 Catch:{ Exception -> 0x00e1 }
            r13 = r13.append(r9);	 Catch:{ Exception -> 0x00e1 }
            r13 = r13.toString();	 Catch:{ Exception -> 0x00e1 }
            r13 = r4.contains(r13);	 Catch:{ Exception -> 0x00e1 }
            if (r13 == 0) goto L_0x0360;
        L_0x0311:
            r2 = 0;
            r2 = r6.byteBufferValue(r2);	 Catch:{ Exception -> 0x00e1 }
            if (r2 == 0) goto L_0x02ae;
        L_0x0318:
            r4 = 0;
            r4 = r2.readInt32(r4);	 Catch:{ Exception -> 0x00e1 }
            r5 = 0;
            r7 = com.hanista.mobogram.tgnet.TLRPC.Chat.TLdeserialize(r2, r4, r5);	 Catch:{ Exception -> 0x00e1 }
            r2.reuse();	 Catch:{ Exception -> 0x00e1 }
            if (r7 == 0) goto L_0x06ee;
        L_0x0327:
            r2 = r7.deactivated;	 Catch:{ Exception -> 0x00e1 }
            if (r2 != 0) goto L_0x06ee;
        L_0x032b:
            r2 = com.hanista.mobogram.messenger.ChatObject.isChannel(r7);	 Catch:{ Exception -> 0x00e1 }
            if (r2 == 0) goto L_0x0337;
        L_0x0331:
            r2 = com.hanista.mobogram.messenger.ChatObject.isNotInChat(r7);	 Catch:{ Exception -> 0x00e1 }
            if (r2 != 0) goto L_0x06ee;
        L_0x0337:
            r2 = r7.id;	 Catch:{ Exception -> 0x00e1 }
            if (r2 <= 0) goto L_0x0359;
        L_0x033b:
            r2 = r7.id;	 Catch:{ Exception -> 0x00e1 }
            r2 = -r2;
            r4 = (long) r2;	 Catch:{ Exception -> 0x00e1 }
        L_0x033f:
            r2 = java.lang.Long.valueOf(r4);	 Catch:{ Exception -> 0x00e1 }
            r2 = r12.get(r2);	 Catch:{ Exception -> 0x00e1 }
            r2 = (com.hanista.mobogram.ui.Adapters.DialogsSearchAdapter.DialogSearchResult) r2;	 Catch:{ Exception -> 0x00e1 }
            r4 = r7.title;	 Catch:{ Exception -> 0x00e1 }
            r5 = 0;
            r4 = com.hanista.mobogram.messenger.AndroidUtilities.generateSearchName(r4, r5, r9);	 Catch:{ Exception -> 0x00e1 }
            r2.name = r4;	 Catch:{ Exception -> 0x00e1 }
            r2.object = r7;	 Catch:{ Exception -> 0x00e1 }
            r2 = r3 + 1;
        L_0x0356:
            r3 = r2;
            goto L_0x02ae;
        L_0x0359:
            r2 = r7.id;	 Catch:{ Exception -> 0x00e1 }
            r4 = com.hanista.mobogram.messenger.AndroidUtilities.makeBroadcastId(r2);	 Catch:{ Exception -> 0x00e1 }
            goto L_0x033f;
        L_0x0360:
            r2 = r2 + 1;
            goto L_0x02cb;
        L_0x0364:
            r6.dispose();	 Catch:{ Exception -> 0x00e1 }
        L_0x0367:
            r2 = r10.isEmpty();	 Catch:{ Exception -> 0x00e1 }
            if (r2 != 0) goto L_0x0549;
        L_0x036d:
            r2 = com.hanista.mobogram.messenger.MessagesStorage.getInstance();	 Catch:{ Exception -> 0x00e1 }
            r2 = r2.getDatabase();	 Catch:{ Exception -> 0x00e1 }
            r4 = java.util.Locale.US;	 Catch:{ Exception -> 0x00e1 }
            r5 = "SELECT q.data, u.name, q.user, q.g, q.authkey, q.ttl, u.data, u.status, q.layer, q.seq_in, q.seq_out, q.use_count, q.exchange_id, q.key_date, q.fprint, q.fauthkey, q.khash, q.in_seq_no FROM enc_chats as q INNER JOIN users as u ON q.user = u.uid WHERE q.uid IN(%s)";
            r6 = 1;
            r6 = new java.lang.Object[r6];	 Catch:{ Exception -> 0x00e1 }
            r7 = 0;
            r9 = ",";
            r9 = android.text.TextUtils.join(r9, r10);	 Catch:{ Exception -> 0x00e1 }
            r6[r7] = r9;	 Catch:{ Exception -> 0x00e1 }
            r4 = java.lang.String.format(r4, r5, r6);	 Catch:{ Exception -> 0x00e1 }
            r5 = 0;
            r5 = new java.lang.Object[r5];	 Catch:{ Exception -> 0x00e1 }
            r9 = r2.queryFinalized(r4, r5);	 Catch:{ Exception -> 0x00e1 }
        L_0x0392:
            r2 = r9.next();	 Catch:{ Exception -> 0x00e1 }
            if (r2 == 0) goto L_0x0546;
        L_0x0398:
            r2 = 1;
            r10 = r9.stringValue(r2);	 Catch:{ Exception -> 0x00e1 }
            r2 = com.hanista.mobogram.messenger.LocaleController.getInstance();	 Catch:{ Exception -> 0x00e1 }
            r2 = r2.getTranslitString(r10);	 Catch:{ Exception -> 0x00e1 }
            r4 = r10.equals(r2);	 Catch:{ Exception -> 0x00e1 }
            if (r4 == 0) goto L_0x06eb;
        L_0x03ab:
            r2 = 0;
            r7 = r2;
        L_0x03ad:
            r2 = 0;
            r4 = ";;;";
            r4 = r10.lastIndexOf(r4);	 Catch:{ Exception -> 0x00e1 }
            r5 = -1;
            if (r4 == r5) goto L_0x03be;
        L_0x03b8:
            r2 = r4 + 2;
            r2 = r10.substring(r2);	 Catch:{ Exception -> 0x00e1 }
        L_0x03be:
            r6 = 0;
            r4 = 0;
            r5 = r4;
            r4 = r6;
        L_0x03c2:
            r6 = r8.length;	 Catch:{ Exception -> 0x00e1 }
            if (r5 >= r6) goto L_0x06df;
        L_0x03c5:
            r13 = r8[r5];	 Catch:{ Exception -> 0x00e1 }
            r6 = r10.startsWith(r13);	 Catch:{ Exception -> 0x00e1 }
            if (r6 != 0) goto L_0x0409;
        L_0x03cd:
            r6 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x00e1 }
            r6.<init>();	 Catch:{ Exception -> 0x00e1 }
            r14 = " ";
            r6 = r6.append(r14);	 Catch:{ Exception -> 0x00e1 }
            r6 = r6.append(r13);	 Catch:{ Exception -> 0x00e1 }
            r6 = r6.toString();	 Catch:{ Exception -> 0x00e1 }
            r6 = r10.contains(r6);	 Catch:{ Exception -> 0x00e1 }
            if (r6 != 0) goto L_0x0409;
        L_0x03e7:
            if (r7 == 0) goto L_0x0502;
        L_0x03e9:
            r6 = r7.startsWith(r13);	 Catch:{ Exception -> 0x00e1 }
            if (r6 != 0) goto L_0x0409;
        L_0x03ef:
            r6 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x00e1 }
            r6.<init>();	 Catch:{ Exception -> 0x00e1 }
            r14 = " ";
            r6 = r6.append(r14);	 Catch:{ Exception -> 0x00e1 }
            r6 = r6.append(r13);	 Catch:{ Exception -> 0x00e1 }
            r6 = r6.toString();	 Catch:{ Exception -> 0x00e1 }
            r6 = r7.contains(r6);	 Catch:{ Exception -> 0x00e1 }
            if (r6 == 0) goto L_0x0502;
        L_0x0409:
            r4 = 1;
            r6 = r4;
        L_0x040b:
            if (r6 == 0) goto L_0x0540;
        L_0x040d:
            r4 = 0;
            r2 = 0;
            r5 = 0;
            r5 = r9.byteBufferValue(r5);	 Catch:{ Exception -> 0x00e1 }
            if (r5 == 0) goto L_0x06e5;
        L_0x0416:
            r4 = 0;
            r4 = r5.readInt32(r4);	 Catch:{ Exception -> 0x00e1 }
            r7 = 0;
            r4 = com.hanista.mobogram.tgnet.TLRPC.EncryptedChat.TLdeserialize(r5, r4, r7);	 Catch:{ Exception -> 0x00e1 }
            r5.reuse();	 Catch:{ Exception -> 0x00e1 }
            r5 = r4;
        L_0x0424:
            r4 = 6;
            r4 = r9.byteBufferValue(r4);	 Catch:{ Exception -> 0x00e1 }
            if (r4 == 0) goto L_0x06e2;
        L_0x042b:
            r2 = 0;
            r2 = r4.readInt32(r2);	 Catch:{ Exception -> 0x00e1 }
            r7 = 0;
            r2 = com.hanista.mobogram.tgnet.TLRPC.User.TLdeserialize(r4, r2, r7);	 Catch:{ Exception -> 0x00e1 }
            r4.reuse();	 Catch:{ Exception -> 0x00e1 }
            r4 = r2;
        L_0x0439:
            if (r5 == 0) goto L_0x06df;
        L_0x043b:
            if (r4 == 0) goto L_0x06df;
        L_0x043d:
            r2 = r5.id;	 Catch:{ Exception -> 0x00e1 }
            r14 = (long) r2;	 Catch:{ Exception -> 0x00e1 }
            r2 = 32;
            r14 = r14 << r2;
            r2 = java.lang.Long.valueOf(r14);	 Catch:{ Exception -> 0x00e1 }
            r2 = r12.get(r2);	 Catch:{ Exception -> 0x00e1 }
            r2 = (com.hanista.mobogram.ui.Adapters.DialogsSearchAdapter.DialogSearchResult) r2;	 Catch:{ Exception -> 0x00e1 }
            r7 = 2;
            r7 = r9.intValue(r7);	 Catch:{ Exception -> 0x00e1 }
            r5.user_id = r7;	 Catch:{ Exception -> 0x00e1 }
            r7 = 3;
            r7 = r9.byteArrayValue(r7);	 Catch:{ Exception -> 0x00e1 }
            r5.a_or_b = r7;	 Catch:{ Exception -> 0x00e1 }
            r7 = 4;
            r7 = r9.byteArrayValue(r7);	 Catch:{ Exception -> 0x00e1 }
            r5.auth_key = r7;	 Catch:{ Exception -> 0x00e1 }
            r7 = 5;
            r7 = r9.intValue(r7);	 Catch:{ Exception -> 0x00e1 }
            r5.ttl = r7;	 Catch:{ Exception -> 0x00e1 }
            r7 = 8;
            r7 = r9.intValue(r7);	 Catch:{ Exception -> 0x00e1 }
            r5.layer = r7;	 Catch:{ Exception -> 0x00e1 }
            r7 = 9;
            r7 = r9.intValue(r7);	 Catch:{ Exception -> 0x00e1 }
            r5.seq_in = r7;	 Catch:{ Exception -> 0x00e1 }
            r7 = 10;
            r7 = r9.intValue(r7);	 Catch:{ Exception -> 0x00e1 }
            r5.seq_out = r7;	 Catch:{ Exception -> 0x00e1 }
            r7 = 11;
            r7 = r9.intValue(r7);	 Catch:{ Exception -> 0x00e1 }
            r10 = r7 >> 16;
            r10 = (short) r10;	 Catch:{ Exception -> 0x00e1 }
            r5.key_use_count_in = r10;	 Catch:{ Exception -> 0x00e1 }
            r7 = (short) r7;	 Catch:{ Exception -> 0x00e1 }
            r5.key_use_count_out = r7;	 Catch:{ Exception -> 0x00e1 }
            r7 = 12;
            r14 = r9.longValue(r7);	 Catch:{ Exception -> 0x00e1 }
            r5.exchange_id = r14;	 Catch:{ Exception -> 0x00e1 }
            r7 = 13;
            r7 = r9.intValue(r7);	 Catch:{ Exception -> 0x00e1 }
            r5.key_create_date = r7;	 Catch:{ Exception -> 0x00e1 }
            r7 = 14;
            r14 = r9.longValue(r7);	 Catch:{ Exception -> 0x00e1 }
            r5.future_key_fingerprint = r14;	 Catch:{ Exception -> 0x00e1 }
            r7 = 15;
            r7 = r9.byteArrayValue(r7);	 Catch:{ Exception -> 0x00e1 }
            r5.future_auth_key = r7;	 Catch:{ Exception -> 0x00e1 }
            r7 = 16;
            r7 = r9.byteArrayValue(r7);	 Catch:{ Exception -> 0x00e1 }
            r5.key_hash = r7;	 Catch:{ Exception -> 0x00e1 }
            r7 = 17;
            r7 = r9.intValue(r7);	 Catch:{ Exception -> 0x00e1 }
            r5.in_seq_no = r7;	 Catch:{ Exception -> 0x00e1 }
            r7 = r4.status;	 Catch:{ Exception -> 0x00e1 }
            if (r7 == 0) goto L_0x04cc;
        L_0x04c3:
            r7 = r4.status;	 Catch:{ Exception -> 0x00e1 }
            r10 = 7;
            r10 = r9.intValue(r10);	 Catch:{ Exception -> 0x00e1 }
            r7.expires = r10;	 Catch:{ Exception -> 0x00e1 }
        L_0x04cc:
            r7 = 1;
            if (r6 != r7) goto L_0x050e;
        L_0x04cf:
            r6 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x00e1 }
            r6.<init>();	 Catch:{ Exception -> 0x00e1 }
            r7 = "<c#ff00a60e>";
            r6 = r6.append(r7);	 Catch:{ Exception -> 0x00e1 }
            r7 = r4.first_name;	 Catch:{ Exception -> 0x00e1 }
            r10 = r4.last_name;	 Catch:{ Exception -> 0x00e1 }
            r7 = com.hanista.mobogram.messenger.ContactsController.formatName(r7, r10);	 Catch:{ Exception -> 0x00e1 }
            r6 = r6.append(r7);	 Catch:{ Exception -> 0x00e1 }
            r7 = "</c>";
            r6 = r6.append(r7);	 Catch:{ Exception -> 0x00e1 }
            r6 = r6.toString();	 Catch:{ Exception -> 0x00e1 }
            r6 = com.hanista.mobogram.messenger.AndroidUtilities.replaceTags(r6);	 Catch:{ Exception -> 0x00e1 }
            r2.name = r6;	 Catch:{ Exception -> 0x00e1 }
        L_0x04f8:
            r2.object = r5;	 Catch:{ Exception -> 0x00e1 }
            r11.add(r4);	 Catch:{ Exception -> 0x00e1 }
            r2 = r3 + 1;
        L_0x04ff:
            r3 = r2;
            goto L_0x0392;
        L_0x0502:
            if (r2 == 0) goto L_0x06e8;
        L_0x0504:
            r6 = r2.startsWith(r13);	 Catch:{ Exception -> 0x00e1 }
            if (r6 == 0) goto L_0x06e8;
        L_0x050a:
            r4 = 2;
            r6 = r4;
            goto L_0x040b;
        L_0x050e:
            r6 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x00e1 }
            r6.<init>();	 Catch:{ Exception -> 0x00e1 }
            r7 = "@";
            r6 = r6.append(r7);	 Catch:{ Exception -> 0x00e1 }
            r7 = r4.username;	 Catch:{ Exception -> 0x00e1 }
            r6 = r6.append(r7);	 Catch:{ Exception -> 0x00e1 }
            r6 = r6.toString();	 Catch:{ Exception -> 0x00e1 }
            r7 = 0;
            r10 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x00e1 }
            r10.<init>();	 Catch:{ Exception -> 0x00e1 }
            r14 = "@";
            r10 = r10.append(r14);	 Catch:{ Exception -> 0x00e1 }
            r10 = r10.append(r13);	 Catch:{ Exception -> 0x00e1 }
            r10 = r10.toString();	 Catch:{ Exception -> 0x00e1 }
            r6 = com.hanista.mobogram.messenger.AndroidUtilities.generateSearchName(r6, r7, r10);	 Catch:{ Exception -> 0x00e1 }
            r2.name = r6;	 Catch:{ Exception -> 0x00e1 }
            goto L_0x04f8;
        L_0x0540:
            r4 = r5 + 1;
            r5 = r4;
            r4 = r6;
            goto L_0x03c2;
        L_0x0546:
            r9.dispose();	 Catch:{ Exception -> 0x00e1 }
        L_0x0549:
            r4 = new java.util.ArrayList;	 Catch:{ Exception -> 0x00e1 }
            r4.<init>(r3);	 Catch:{ Exception -> 0x00e1 }
            r2 = r12.values();	 Catch:{ Exception -> 0x00e1 }
            r3 = r2.iterator();	 Catch:{ Exception -> 0x00e1 }
        L_0x0556:
            r2 = r3.hasNext();	 Catch:{ Exception -> 0x00e1 }
            if (r2 == 0) goto L_0x056e;
        L_0x055c:
            r2 = r3.next();	 Catch:{ Exception -> 0x00e1 }
            r2 = (com.hanista.mobogram.ui.Adapters.DialogsSearchAdapter.DialogSearchResult) r2;	 Catch:{ Exception -> 0x00e1 }
            r5 = r2.object;	 Catch:{ Exception -> 0x00e1 }
            if (r5 == 0) goto L_0x0556;
        L_0x0566:
            r5 = r2.name;	 Catch:{ Exception -> 0x00e1 }
            if (r5 == 0) goto L_0x0556;
        L_0x056a:
            r4.add(r2);	 Catch:{ Exception -> 0x00e1 }
            goto L_0x0556;
        L_0x056e:
            r2 = new com.hanista.mobogram.ui.Adapters.DialogsSearchAdapter$5$1;	 Catch:{ Exception -> 0x00e1 }
            r0 = r19;
            r2.<init>();	 Catch:{ Exception -> 0x00e1 }
            java.util.Collections.sort(r4, r2);	 Catch:{ Exception -> 0x00e1 }
            r6 = new java.util.ArrayList;	 Catch:{ Exception -> 0x00e1 }
            r6.<init>();	 Catch:{ Exception -> 0x00e1 }
            r7 = new java.util.ArrayList;	 Catch:{ Exception -> 0x00e1 }
            r7.<init>();	 Catch:{ Exception -> 0x00e1 }
            r2 = 0;
            r3 = r2;
        L_0x0584:
            r2 = r4.size();	 Catch:{ Exception -> 0x00e1 }
            if (r3 >= r2) goto L_0x059e;
        L_0x058a:
            r2 = r4.get(r3);	 Catch:{ Exception -> 0x00e1 }
            r2 = (com.hanista.mobogram.ui.Adapters.DialogsSearchAdapter.DialogSearchResult) r2;	 Catch:{ Exception -> 0x00e1 }
            r5 = r2.object;	 Catch:{ Exception -> 0x00e1 }
            r6.add(r5);	 Catch:{ Exception -> 0x00e1 }
            r2 = r2.name;	 Catch:{ Exception -> 0x00e1 }
            r7.add(r2);	 Catch:{ Exception -> 0x00e1 }
            r2 = r3 + 1;
            r3 = r2;
            goto L_0x0584;
        L_0x059e:
            r0 = r19;
            r2 = com.hanista.mobogram.ui.Adapters.DialogsSearchAdapter.this;	 Catch:{ Exception -> 0x00e1 }
            r2 = r2.dialogsType;	 Catch:{ Exception -> 0x00e1 }
            r3 = 2;
            if (r2 == r3) goto L_0x06cc;
        L_0x05a9:
            r2 = com.hanista.mobogram.messenger.MessagesStorage.getInstance();	 Catch:{ Exception -> 0x00e1 }
            r2 = r2.getDatabase();	 Catch:{ Exception -> 0x00e1 }
            r3 = "SELECT u.data, u.status, u.name, u.uid FROM users as u INNER JOIN contacts as c ON u.uid = c.uid";
            r4 = 0;
            r4 = new java.lang.Object[r4];	 Catch:{ Exception -> 0x00e1 }
            r9 = r2.queryFinalized(r3, r4);	 Catch:{ Exception -> 0x00e1 }
        L_0x05bb:
            r2 = r9.next();	 Catch:{ Exception -> 0x00e1 }
            if (r2 == 0) goto L_0x06c9;
        L_0x05c1:
            r2 = 3;
            r2 = r9.intValue(r2);	 Catch:{ Exception -> 0x00e1 }
            r4 = (long) r2;	 Catch:{ Exception -> 0x00e1 }
            r3 = java.lang.Long.valueOf(r4);	 Catch:{ Exception -> 0x00e1 }
            r3 = com.hanista.mobogram.mobo.p012l.HiddenConfig.m1399b(r3);	 Catch:{ Exception -> 0x00e1 }
            if (r3 != 0) goto L_0x05bb;
        L_0x05d1:
            r2 = (long) r2;	 Catch:{ Exception -> 0x00e1 }
            r2 = java.lang.Long.valueOf(r2);	 Catch:{ Exception -> 0x00e1 }
            r2 = r12.containsKey(r2);	 Catch:{ Exception -> 0x00e1 }
            if (r2 != 0) goto L_0x05bb;
        L_0x05dc:
            r2 = 2;
            r10 = r9.stringValue(r2);	 Catch:{ Exception -> 0x00e1 }
            r2 = com.hanista.mobogram.messenger.LocaleController.getInstance();	 Catch:{ Exception -> 0x00e1 }
            r2 = r2.getTranslitString(r10);	 Catch:{ Exception -> 0x00e1 }
            r3 = r10.equals(r2);	 Catch:{ Exception -> 0x00e1 }
            if (r3 == 0) goto L_0x06dc;
        L_0x05ef:
            r2 = 0;
            r5 = r2;
        L_0x05f1:
            r2 = 0;
            r3 = ";;;";
            r3 = r10.lastIndexOf(r3);	 Catch:{ Exception -> 0x00e1 }
            r4 = -1;
            if (r3 == r4) goto L_0x06d9;
        L_0x05fc:
            r2 = r3 + 3;
            r2 = r10.substring(r2);	 Catch:{ Exception -> 0x00e1 }
            r4 = r2;
        L_0x0603:
            r3 = 0;
            r13 = r8.length;	 Catch:{ Exception -> 0x00e1 }
            r2 = 0;
            r18 = r2;
            r2 = r3;
            r3 = r18;
        L_0x060b:
            if (r3 >= r13) goto L_0x05bb;
        L_0x060d:
            r14 = r8[r3];	 Catch:{ Exception -> 0x00e1 }
            r15 = r10.startsWith(r14);	 Catch:{ Exception -> 0x00e1 }
            if (r15 != 0) goto L_0x0651;
        L_0x0615:
            r15 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x00e1 }
            r15.<init>();	 Catch:{ Exception -> 0x00e1 }
            r16 = " ";
            r15 = r15.append(r16);	 Catch:{ Exception -> 0x00e1 }
            r15 = r15.append(r14);	 Catch:{ Exception -> 0x00e1 }
            r15 = r15.toString();	 Catch:{ Exception -> 0x00e1 }
            r15 = r10.contains(r15);	 Catch:{ Exception -> 0x00e1 }
            if (r15 != 0) goto L_0x0651;
        L_0x062f:
            if (r5 == 0) goto L_0x0688;
        L_0x0631:
            r15 = r5.startsWith(r14);	 Catch:{ Exception -> 0x00e1 }
            if (r15 != 0) goto L_0x0651;
        L_0x0637:
            r15 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x00e1 }
            r15.<init>();	 Catch:{ Exception -> 0x00e1 }
            r16 = " ";
            r15 = r15.append(r16);	 Catch:{ Exception -> 0x00e1 }
            r15 = r15.append(r14);	 Catch:{ Exception -> 0x00e1 }
            r15 = r15.toString();	 Catch:{ Exception -> 0x00e1 }
            r15 = r5.contains(r15);	 Catch:{ Exception -> 0x00e1 }
            if (r15 == 0) goto L_0x0688;
        L_0x0651:
            r2 = 1;
        L_0x0652:
            if (r2 == 0) goto L_0x06c5;
        L_0x0654:
            r3 = 0;
            r3 = r9.byteBufferValue(r3);	 Catch:{ Exception -> 0x00e1 }
            if (r3 == 0) goto L_0x05bb;
        L_0x065b:
            r4 = 0;
            r4 = r3.readInt32(r4);	 Catch:{ Exception -> 0x00e1 }
            r5 = 0;
            r4 = com.hanista.mobogram.tgnet.TLRPC.User.TLdeserialize(r3, r4, r5);	 Catch:{ Exception -> 0x00e1 }
            r3.reuse();	 Catch:{ Exception -> 0x00e1 }
            r3 = r4.status;	 Catch:{ Exception -> 0x00e1 }
            if (r3 == 0) goto L_0x0675;
        L_0x066c:
            r3 = r4.status;	 Catch:{ Exception -> 0x00e1 }
            r5 = 1;
            r5 = r9.intValue(r5);	 Catch:{ Exception -> 0x00e1 }
            r3.expires = r5;	 Catch:{ Exception -> 0x00e1 }
        L_0x0675:
            r3 = 1;
            if (r2 != r3) goto L_0x0692;
        L_0x0678:
            r2 = r4.first_name;	 Catch:{ Exception -> 0x00e1 }
            r3 = r4.last_name;	 Catch:{ Exception -> 0x00e1 }
            r2 = com.hanista.mobogram.messenger.AndroidUtilities.generateSearchName(r2, r3, r14);	 Catch:{ Exception -> 0x00e1 }
            r7.add(r2);	 Catch:{ Exception -> 0x00e1 }
        L_0x0683:
            r6.add(r4);	 Catch:{ Exception -> 0x00e1 }
            goto L_0x05bb;
        L_0x0688:
            if (r4 == 0) goto L_0x0652;
        L_0x068a:
            r15 = r4.startsWith(r14);	 Catch:{ Exception -> 0x00e1 }
            if (r15 == 0) goto L_0x0652;
        L_0x0690:
            r2 = 2;
            goto L_0x0652;
        L_0x0692:
            r2 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x00e1 }
            r2.<init>();	 Catch:{ Exception -> 0x00e1 }
            r3 = "@";
            r2 = r2.append(r3);	 Catch:{ Exception -> 0x00e1 }
            r3 = r4.username;	 Catch:{ Exception -> 0x00e1 }
            r2 = r2.append(r3);	 Catch:{ Exception -> 0x00e1 }
            r2 = r2.toString();	 Catch:{ Exception -> 0x00e1 }
            r3 = 0;
            r5 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x00e1 }
            r5.<init>();	 Catch:{ Exception -> 0x00e1 }
            r10 = "@";
            r5 = r5.append(r10);	 Catch:{ Exception -> 0x00e1 }
            r5 = r5.append(r14);	 Catch:{ Exception -> 0x00e1 }
            r5 = r5.toString();	 Catch:{ Exception -> 0x00e1 }
            r2 = com.hanista.mobogram.messenger.AndroidUtilities.generateSearchName(r2, r3, r5);	 Catch:{ Exception -> 0x00e1 }
            r7.add(r2);	 Catch:{ Exception -> 0x00e1 }
            goto L_0x0683;
        L_0x06c5:
            r3 = r3 + 1;
            goto L_0x060b;
        L_0x06c9:
            r9.dispose();	 Catch:{ Exception -> 0x00e1 }
        L_0x06cc:
            r0 = r19;
            r2 = com.hanista.mobogram.ui.Adapters.DialogsSearchAdapter.this;	 Catch:{ Exception -> 0x00e1 }
            r0 = r19;
            r3 = r0.val$searchId;	 Catch:{ Exception -> 0x00e1 }
            r2.updateSearchResults(r6, r7, r11, r3);	 Catch:{ Exception -> 0x00e1 }
            goto L_0x0038;
        L_0x06d9:
            r4 = r2;
            goto L_0x0603;
        L_0x06dc:
            r5 = r2;
            goto L_0x05f1;
        L_0x06df:
            r2 = r3;
            goto L_0x04ff;
        L_0x06e2:
            r4 = r2;
            goto L_0x0439;
        L_0x06e5:
            r5 = r4;
            goto L_0x0424;
        L_0x06e8:
            r6 = r4;
            goto L_0x040b;
        L_0x06eb:
            r7 = r2;
            goto L_0x03ad;
        L_0x06ee:
            r2 = r3;
            goto L_0x0356;
        L_0x06f1:
            r4 = r2;
            goto L_0x02c9;
        L_0x06f4:
            r2 = r3;
            goto L_0x0236;
        L_0x06f7:
            r4 = r2;
            goto L_0x01f3;
        L_0x06fa:
            r6 = r2;
            goto L_0x019a;
        L_0x06fd:
            r7 = r2;
            goto L_0x0188;
        L_0x0700:
            r3 = r2;
            goto L_0x004f;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.hanista.mobogram.ui.Adapters.DialogsSearchAdapter.5.run():void");
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Adapters.DialogsSearchAdapter.6 */
    class C10226 implements Runnable {
        final /* synthetic */ ArrayList val$encUsers;
        final /* synthetic */ ArrayList val$names;
        final /* synthetic */ ArrayList val$result;
        final /* synthetic */ int val$searchId;

        C10226(int i, ArrayList arrayList, ArrayList arrayList2, ArrayList arrayList3) {
            this.val$searchId = i;
            this.val$result = arrayList;
            this.val$encUsers = arrayList2;
            this.val$names = arrayList3;
        }

        public void run() {
            if (this.val$searchId == DialogsSearchAdapter.this.lastSearchId) {
                for (int i = 0; i < this.val$result.size(); i++) {
                    TLObject tLObject = (TLObject) this.val$result.get(i);
                    if (tLObject instanceof User) {
                        MessagesController.getInstance().putUser((User) tLObject, true);
                    } else if (tLObject instanceof Chat) {
                        MessagesController.getInstance().putChat((Chat) tLObject, true);
                    } else if (tLObject instanceof EncryptedChat) {
                        MessagesController.getInstance().putEncryptedChat((EncryptedChat) tLObject, true);
                    }
                }
                MessagesController.getInstance().putUsers(this.val$encUsers, true);
                DialogsSearchAdapter.this.searchResult = DialogsSearchAdapter.this.filterResult(this.val$result);
                DialogsSearchAdapter.this.searchResultNames = this.val$names;
                DialogsSearchAdapter.this.notifyDataSetChanged();
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Adapters.DialogsSearchAdapter.7 */
    class C10247 extends TimerTask {
        final /* synthetic */ String val$query;
        final /* synthetic */ int val$searchId;

        /* renamed from: com.hanista.mobogram.ui.Adapters.DialogsSearchAdapter.7.1 */
        class C10231 implements Runnable {
            C10231() {
            }

            public void run() {
                if (DialogsSearchAdapter.this.needMessagesSearch != 2) {
                    DialogsSearchAdapter.this.queryServerSearch(C10247.this.val$query, true);
                }
                DialogsSearchAdapter.this.searchMessagesInternal(C10247.this.val$query);
            }
        }

        C10247(String str, int i) {
            this.val$query = str;
            this.val$searchId = i;
        }

        public void run() {
            try {
                cancel();
                DialogsSearchAdapter.this.searchTimer.cancel();
                DialogsSearchAdapter.this.searchTimer = null;
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
            DialogsSearchAdapter.this.searchDialogsInternal(this.val$query, this.val$searchId);
            AndroidUtilities.runOnUIThread(new C10231());
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Adapters.DialogsSearchAdapter.8 */
    class C10258 extends RecyclerListView {
        C10258(Context context) {
            super(context);
        }

        public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
            if (!(getParent() == null || getParent().getParent() == null)) {
                getParent().getParent().requestDisallowInterceptTouchEvent(true);
            }
            return super.onInterceptTouchEvent(motionEvent);
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Adapters.DialogsSearchAdapter.9 */
    class C10269 extends LinearLayoutManager {
        C10269(Context context) {
            super(context);
        }

        public boolean supportsPredictiveItemAnimations() {
            return false;
        }
    }

    private class CategoryAdapterRecycler extends Adapter {
        private CategoryAdapterRecycler() {
        }

        public int getItemCount() {
            return SearchQuery.getHints().size();
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            User user;
            int i2;
            CharSequence formatName;
            Chat chat = null;
            HintDialogCell hintDialogCell = (HintDialogCell) viewHolder.itemView;
            TL_topPeer tL_topPeer = (TL_topPeer) SearchQuery.getHints().get(i);
            TL_dialog tL_dialog = new TL_dialog();
            int i3;
            if (tL_topPeer.peer.user_id != 0) {
                i3 = tL_topPeer.peer.user_id;
                user = MessagesController.getInstance().getUser(Integer.valueOf(tL_topPeer.peer.user_id));
                i2 = i3;
            } else if (tL_topPeer.peer.channel_id != 0) {
                i3 = -tL_topPeer.peer.channel_id;
                user = null;
                chat = MessagesController.getInstance().getChat(Integer.valueOf(tL_topPeer.peer.channel_id));
                i2 = i3;
            } else if (tL_topPeer.peer.chat_id != 0) {
                i3 = -tL_topPeer.peer.chat_id;
                user = null;
                chat = MessagesController.getInstance().getChat(Integer.valueOf(tL_topPeer.peer.chat_id));
                i2 = i3;
            } else {
                i2 = 0;
                user = null;
            }
            hintDialogCell.setTag(Integer.valueOf(i2));
            String str = TtmlNode.ANONYMOUS_REGION_ID;
            if (user != null) {
                formatName = ContactsController.formatName(user.first_name, user.last_name);
            } else if (chat != null) {
                formatName = chat.title;
            } else {
                Object obj = str;
            }
            hintDialogCell.setDialog(i2, true, formatName);
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View hintDialogCell = new HintDialogCell(DialogsSearchAdapter.this.mContext);
            hintDialogCell.setLayoutParams(new LayoutParams(AndroidUtilities.dp(80.0f), AndroidUtilities.dp(100.0f)));
            return new Holder(hintDialogCell);
        }

        public void setIndex(int i) {
            notifyDataSetChanged();
        }
    }

    private class DialogSearchResult {
        public int date;
        public CharSequence name;
        public TLObject object;

        private DialogSearchResult() {
        }
    }

    public interface DialogsSearchAdapterDelegate {
        void didPressedOnSubDialog(int i);

        void needRemoveHint(int i);

        void searchStateChanged(boolean z);
    }

    private class Holder extends ViewHolder {
        public Holder(View view) {
            super(view);
        }
    }

    protected static class RecentSearchObject {
        int date;
        long did;
        TLObject object;

        protected RecentSearchObject() {
        }
    }

    public DialogsSearchAdapter(Context context, int i, int i2) {
        this.searchResult = new ArrayList();
        this.searchResultNames = new ArrayList();
        this.searchResultMessages = new ArrayList();
        this.searchResultHashtags = new ArrayList();
        this.reqId = 0;
        this.lastSearchId = 0;
        this.recentSearchObjects = new ArrayList();
        this.recentSearchObjectsById = new HashMap();
        this.mContext = context;
        this.needMessagesSearch = i;
        this.dialogsType = i2;
        loadRecentSearch();
        SearchQuery.loadHints(true);
    }

    private void searchDialogsInternal(String str, int i) {
        if (this.needMessagesSearch != 2) {
            MessagesStorage.getInstance().getStorageQueue().postRunnable(new C10215(str, i));
        }
    }

    private void searchMessagesInternal(String str) {
        if (this.needMessagesSearch == 0) {
            return;
        }
        if ((this.lastMessagesSearchString != null && this.lastMessagesSearchString.length() != 0) || (str != null && str.length() != 0)) {
            if (this.reqId != 0) {
                ConnectionsManager.getInstance().cancelRequest(this.reqId, true);
                this.reqId = 0;
            }
            if (str == null || str.length() == 0) {
                this.searchResultMessages.clear();
                this.lastReqId = 0;
                this.lastMessagesSearchString = null;
                notifyDataSetChanged();
                if (this.delegate != null) {
                    this.delegate.searchStateChanged(false);
                    return;
                }
                return;
            }
            int i;
            TLObject tL_messages_searchGlobal = new TL_messages_searchGlobal();
            tL_messages_searchGlobal.limit = 20;
            tL_messages_searchGlobal.f2674q = str;
            if (this.lastMessagesSearchString == null || !str.equals(this.lastMessagesSearchString) || this.searchResultMessages.isEmpty()) {
                tL_messages_searchGlobal.offset_date = 0;
                tL_messages_searchGlobal.offset_id = 0;
                tL_messages_searchGlobal.offset_peer = new TL_inputPeerEmpty();
            } else {
                MessageObject messageObject = (MessageObject) this.searchResultMessages.get(this.searchResultMessages.size() - 1);
                tL_messages_searchGlobal.offset_id = messageObject.getId();
                tL_messages_searchGlobal.offset_date = messageObject.messageOwner.date;
                i = messageObject.messageOwner.to_id.channel_id != 0 ? -messageObject.messageOwner.to_id.channel_id : messageObject.messageOwner.to_id.chat_id != 0 ? -messageObject.messageOwner.to_id.chat_id : messageObject.messageOwner.to_id.user_id;
                tL_messages_searchGlobal.offset_peer = MessagesController.getInputPeer(i);
            }
            this.lastMessagesSearchString = str;
            i = this.lastReqId + 1;
            this.lastReqId = i;
            if (this.delegate != null) {
                this.delegate.searchStateChanged(true);
            }
            this.reqId = ConnectionsManager.getInstance().sendRequest(tL_messages_searchGlobal, new C10141(i, tL_messages_searchGlobal), 2);
        }
    }

    private void setRecentSearch(ArrayList<RecentSearchObject> arrayList, HashMap<Long, RecentSearchObject> hashMap) {
        this.recentSearchObjects = arrayList;
        this.recentSearchObjectsById = hashMap;
        for (int i = 0; i < this.recentSearchObjects.size(); i++) {
            RecentSearchObject recentSearchObject = (RecentSearchObject) this.recentSearchObjects.get(i);
            if (recentSearchObject.object instanceof User) {
                MessagesController.getInstance().putUser((User) recentSearchObject.object, true);
            } else if (recentSearchObject.object instanceof Chat) {
                MessagesController.getInstance().putChat((Chat) recentSearchObject.object, true);
            } else if (recentSearchObject.object instanceof EncryptedChat) {
                MessagesController.getInstance().putEncryptedChat((EncryptedChat) recentSearchObject.object, true);
            }
        }
        notifyDataSetChanged();
    }

    private void updateSearchResults(ArrayList<TLObject> arrayList, ArrayList<CharSequence> arrayList2, ArrayList<User> arrayList3, int i) {
        AndroidUtilities.runOnUIThread(new C10226(i, arrayList, arrayList3, arrayList2));
    }

    public void clearRecentHashtags() {
        super.clearRecentHashtags();
        this.searchResultHashtags.clear();
        notifyDataSetChanged();
    }

    public void clearRecentSearch() {
        this.recentSearchObjectsById = new HashMap();
        this.recentSearchObjects = new ArrayList();
        notifyDataSetChanged();
        MessagesStorage.getInstance().getStorageQueue().postRunnable(new C10194());
    }

    public ArrayList<TLObject> filterResult(ArrayList<TLObject> arrayList) {
        ArrayList<TLObject> arrayList2 = new ArrayList();
        for (int i = 0; i < arrayList.size(); i++) {
            long j = 0;
            TLObject tLObject = (TLObject) arrayList.get(i);
            if (tLObject instanceof User) {
                j = (long) ((User) tLObject).id;
            } else if (tLObject instanceof Chat) {
                j = (long) (-((Chat) tLObject).id);
            } else if (tLObject instanceof EncryptedChat) {
                j = (long) (-((EncryptedChat) tLObject).id);
            }
            if (!HiddenConfig.m1399b(Long.valueOf(j)) || HiddenConfig.f1402e) {
                arrayList2.add(tLObject);
            }
        }
        return arrayList2;
    }

    public Object getItem(int i) {
        int i2 = 0;
        if (isRecentSearchDisplayed()) {
            if (!SearchQuery.getHints().isEmpty()) {
                i2 = 2;
            }
            if (i <= i2 || (i - 1) - i2 >= this.recentSearchObjects.size()) {
                return null;
            }
            TLObject tLObject = ((RecentSearchObject) this.recentSearchObjects.get((i - 1) - i2)).object;
            if (tLObject instanceof User) {
                User user = MessagesController.getInstance().getUser(Integer.valueOf(((User) tLObject).id));
                return user != null ? user : tLObject;
            } else if (!(tLObject instanceof Chat)) {
                return tLObject;
            } else {
                Chat chat = MessagesController.getInstance().getChat(Integer.valueOf(((Chat) tLObject).id));
                return chat != null ? chat : tLObject;
            }
        } else if (!this.searchResultHashtags.isEmpty()) {
            return i > 0 ? this.searchResultHashtags.get(i - 1) : null;
        } else {
            int size = this.searchResult.size();
            int size2 = this.globalSearch.isEmpty() ? 0 : this.globalSearch.size() + 1;
            if (!this.searchResultMessages.isEmpty()) {
                i2 = this.searchResultMessages.size() + 1;
            }
            return (i < 0 || i >= size) ? (i <= size || i >= size2 + size) ? (i <= size2 + size || i >= i2 + (size2 + size)) ? null : this.searchResultMessages.get(((i - size) - size2) - 1) : this.globalSearch.get((i - size) - 1) : this.searchResult.get(i);
        }
    }

    public int getItemCount() {
        int i = 0;
        int size;
        if (isRecentSearchDisplayed()) {
            size = !this.recentSearchObjects.isEmpty() ? this.recentSearchObjects.size() + 1 : 0;
            if (!SearchQuery.getHints().isEmpty()) {
                i = 2;
            }
            return size + i;
        } else if (!this.searchResultHashtags.isEmpty()) {
            return this.searchResultHashtags.size() + 1;
        } else {
            size = this.searchResult.size();
            int size2 = this.globalSearch.size();
            int size3 = this.searchResultMessages.size();
            if (size2 != 0) {
                size += size2 + 1;
            }
            if (size3 == 0) {
                return size;
            }
            size2 = size3 + 1;
            if (!this.messagesSearchEndReached) {
                i = 1;
            }
            return size + (i + size2);
        }
    }

    public long getItemId(int i) {
        return (long) i;
    }

    public int getItemViewType(int i) {
        int i2 = 2;
        if (isRecentSearchDisplayed()) {
            if (SearchQuery.getHints().isEmpty()) {
                i2 = 0;
            }
            return i <= i2 ? (i == i2 || i % 2 == 0) ? 1 : 5 : 0;
        } else if (!this.searchResultHashtags.isEmpty()) {
            return i != 0 ? 4 : 1;
        } else {
            int size = this.searchResult.size();
            int size2 = this.globalSearch.isEmpty() ? 0 : this.globalSearch.size() + 1;
            int size3 = this.searchResultMessages.isEmpty() ? 0 : this.searchResultMessages.size() + 1;
            return ((i < 0 || i >= size) && (i <= size || i >= size2 + size)) ? (i <= size2 + size || i >= (size2 + size) + size3) ? (size3 == 0 || i != (size2 + size) + size3) ? 1 : 3 : 2 : 0;
        }
    }

    public String getLastSearchString() {
        return this.lastMessagesSearchString;
    }

    public boolean hasRecentRearch() {
        return (this.recentSearchObjects.isEmpty() && SearchQuery.getHints().isEmpty()) ? false : true;
    }

    public boolean isGlobalSearch(int i) {
        return i > this.searchResult.size() && i <= this.globalSearch.size() + this.searchResult.size();
    }

    public boolean isMessagesSearchEndReached() {
        return this.messagesSearchEndReached;
    }

    public boolean isRecentSearchDisplayed() {
        return this.needMessagesSearch != 2 && ((this.lastSearchText == null || this.lastSearchText.length() == 0) && !(this.recentSearchObjects.isEmpty() && SearchQuery.getHints().isEmpty()));
    }

    public void loadMoreSearchMessages() {
        searchMessagesInternal(this.lastMessagesSearchString);
    }

    public void loadRecentSearch() {
        MessagesStorage.getInstance().getStorageQueue().postRunnable(new C10172());
    }

    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        switch (viewHolder.getItemViewType()) {
            case VideoPlayer.TRACK_DEFAULT /*0*/:
                TLObject tLObject;
                TLObject tLObject2;
                String str;
                EncryptedChat encryptedChat;
                boolean z;
                CharSequence charSequence;
                CharSequence charSequence2;
                ProfileSearchCell profileSearchCell = (ProfileSearchCell) viewHolder.itemView;
                String format = String.format("#%08X", new Object[]{Integer.valueOf(AdvanceTheme.au & -1)});
                Object item = getItem(i);
                TLObject tLObject3;
                if (item instanceof User) {
                    tLObject3 = (User) item;
                    tLObject = null;
                    tLObject2 = tLObject3;
                    str = tLObject3.username;
                    encryptedChat = null;
                } else if (item instanceof Chat) {
                    TLObject chat = MessagesController.getInstance().getChat(Integer.valueOf(((Chat) item).id));
                    tLObject3 = chat == null ? (Chat) item : chat;
                    tLObject = tLObject3;
                    tLObject2 = null;
                    str = tLObject3.username;
                    encryptedChat = null;
                } else if (item instanceof EncryptedChat) {
                    EncryptedChat encryptedChat2 = MessagesController.getInstance().getEncryptedChat(Integer.valueOf(((EncryptedChat) item).id));
                    tLObject = null;
                    tLObject2 = MessagesController.getInstance().getUser(Integer.valueOf(encryptedChat2.user_id));
                    encryptedChat = encryptedChat2;
                    str = null;
                } else {
                    str = null;
                    tLObject = null;
                    tLObject2 = null;
                    encryptedChat = null;
                }
                if (isRecentSearchDisplayed()) {
                    z = true;
                    profileSearchCell.useSeparator = i != getItemCount() + -1;
                    charSequence = null;
                    charSequence2 = null;
                } else {
                    int size = this.searchResult.size();
                    boolean z2 = (i == getItemCount() + -1 || i == size - 1 || i == ((this.globalSearch.isEmpty() ? 0 : this.globalSearch.size() + 1) + size) - 1) ? false : true;
                    profileSearchCell.useSeparator = z2;
                    if (i < this.searchResult.size()) {
                        CharSequence charSequence3 = (CharSequence) this.searchResultNames.get(i);
                        if (charSequence3 == null || tLObject2 == null || tLObject2.username == null || tLObject2.username.length() <= 0 || !charSequence3.toString().startsWith("@" + tLObject2.username)) {
                            z = false;
                            charSequence = charSequence3;
                            charSequence2 = null;
                        } else {
                            charSequence = null;
                            z = false;
                            charSequence2 = charSequence3;
                        }
                    } else if (i <= this.searchResult.size() || str == null) {
                        z = false;
                        charSequence = null;
                        charSequence2 = null;
                    } else {
                        String str2 = this.lastFoundUsername;
                        String substring = str2.startsWith("@") ? str2.substring(1) : str2;
                        Object obj;
                        try {
                            SpannableStringBuilder replaceTags = ThemeUtil.m2490b() ? AndroidUtilities.replaceTags(String.format("<c" + format + ">@%s</c>%s", new Object[]{str.substring(0, substring.length()), str.substring(substring.length())})) : AndroidUtilities.replaceTags(String.format("<c#ff4d83b3>@%s</c>%s", new Object[]{str.substring(0, substring.length()), str.substring(substring.length())}));
                            z = false;
                            charSequence = null;
                            obj = replaceTags;
                        } catch (Throwable e) {
                            FileLog.m18e("tmessages", e);
                            z = false;
                            charSequence = null;
                            obj = str;
                        }
                    }
                }
                profileSearchCell.setData(tLObject2 != null ? tLObject2 : tLObject, encryptedChat, charSequence, charSequence2, z);
            case VideoPlayer.TYPE_AUDIO /*1*/:
                GreySectionCell greySectionCell = (GreySectionCell) viewHolder.itemView;
                if (ThemeUtil.m2490b()) {
                    greySectionCell.setBackgroundColor(AdvanceTheme.m2286c(AdvanceTheme.f2515z, -855310));
                    greySectionCell.setTextColor(AdvanceTheme.m2286c(AdvanceTheme.f2489Z, -7697782));
                }
                if (isRecentSearchDisplayed()) {
                    if (i < (!SearchQuery.getHints().isEmpty() ? 2 : 0)) {
                        greySectionCell.setText(LocaleController.getString("ChatHints", C0338R.string.ChatHints).toUpperCase());
                    } else {
                        greySectionCell.setText(LocaleController.getString("Recent", C0338R.string.Recent).toUpperCase());
                    }
                } else if (!this.searchResultHashtags.isEmpty()) {
                    greySectionCell.setText(LocaleController.getString("Hashtags", C0338R.string.Hashtags).toUpperCase());
                } else if (this.globalSearch.isEmpty() || i != this.searchResult.size()) {
                    greySectionCell.setText(LocaleController.getString("SearchMessages", C0338R.string.SearchMessages));
                } else {
                    greySectionCell.setText(LocaleController.getString("GlobalSearch", C0338R.string.GlobalSearch));
                }
            case VideoPlayer.STATE_PREPARING /*2*/:
                DialogCell dialogCell = (DialogCell) viewHolder.itemView;
                if (ThemeUtil.m2490b()) {
                    dialogCell.setBackgroundColor(AdvanceTheme.m2286c(AdvanceTheme.f2515z, -855310));
                }
                dialogCell.useSeparator = i != getItemCount() + -1;
                MessageObject messageObject = (MessageObject) getItem(i);
                dialogCell.setDialog(messageObject.getDialogId(), messageObject, messageObject.messageOwner.date);
            case VideoPlayer.STATE_READY /*4*/:
                HashtagSearchCell hashtagSearchCell = (HashtagSearchCell) viewHolder.itemView;
                if (ThemeUtil.m2490b()) {
                    hashtagSearchCell.setTextColor(AdvanceTheme.m2286c(AdvanceTheme.am, Theme.MSG_TEXT_COLOR));
                }
                hashtagSearchCell.setText((CharSequence) this.searchResultHashtags.get(i - 1));
                hashtagSearchCell.setNeedDivider(i != this.searchResultHashtags.size());
            case VideoPlayer.STATE_ENDED /*5*/:
                ((CategoryAdapterRecycler) ((RecyclerListView) viewHolder.itemView).getAdapter()).setIndex(i / 2);
            default:
        }
    }

    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View profileSearchCell;
        switch (i) {
            case VideoPlayer.TRACK_DEFAULT /*0*/:
                profileSearchCell = new ProfileSearchCell(this.mContext);
                profileSearchCell.setBackgroundResource(C0338R.drawable.list_selector);
                break;
            case VideoPlayer.TYPE_AUDIO /*1*/:
                profileSearchCell = new GreySectionCell(this.mContext);
                break;
            case VideoPlayer.STATE_PREPARING /*2*/:
                profileSearchCell = new DialogCell(this.mContext);
                break;
            case VideoPlayer.STATE_BUFFERING /*3*/:
                profileSearchCell = new LoadingCell(this.mContext);
                break;
            case VideoPlayer.STATE_READY /*4*/:
                profileSearchCell = new HashtagSearchCell(this.mContext);
                break;
            case VideoPlayer.STATE_ENDED /*5*/:
                profileSearchCell = new C10258(this.mContext);
                profileSearchCell.setTag(Integer.valueOf(9));
                profileSearchCell.setItemAnimator(null);
                profileSearchCell.setLayoutAnimation(null);
                LayoutManager c10269 = new C10269(this.mContext);
                c10269.setOrientation(0);
                profileSearchCell.setLayoutManager(c10269);
                profileSearchCell.setAdapter(new CategoryAdapterRecycler());
                profileSearchCell.setOnItemClickListener(new OnItemClickListener() {
                    public void onItemClick(View view, int i) {
                        if (DialogsSearchAdapter.this.delegate != null) {
                            DialogsSearchAdapter.this.delegate.didPressedOnSubDialog(((Integer) view.getTag()).intValue());
                        }
                    }
                });
                profileSearchCell.setOnItemLongClickListener(new OnItemLongClickListener() {
                    public boolean onItemClick(View view, int i) {
                        if (DialogsSearchAdapter.this.delegate != null) {
                            DialogsSearchAdapter.this.delegate.needRemoveHint(((Integer) view.getTag()).intValue());
                        }
                        return true;
                    }
                });
                break;
            default:
                profileSearchCell = null;
                break;
        }
        if (i == 5) {
            profileSearchCell.setLayoutParams(new LayoutParams(-1, AndroidUtilities.dp(100.0f)));
        } else {
            profileSearchCell.setLayoutParams(new LayoutParams(-1, -2));
        }
        return new Holder(profileSearchCell);
    }

    public void putRecentSearch(long j, TLObject tLObject) {
        RecentSearchObject recentSearchObject = (RecentSearchObject) this.recentSearchObjectsById.get(Long.valueOf(j));
        if (recentSearchObject == null) {
            recentSearchObject = new RecentSearchObject();
            this.recentSearchObjectsById.put(Long.valueOf(j), recentSearchObject);
        } else {
            this.recentSearchObjects.remove(recentSearchObject);
        }
        this.recentSearchObjects.add(0, recentSearchObject);
        recentSearchObject.did = j;
        recentSearchObject.object = tLObject;
        recentSearchObject.date = (int) (System.currentTimeMillis() / 1000);
        notifyDataSetChanged();
        MessagesStorage.getInstance().getStorageQueue().postRunnable(new C10183(j));
    }

    public void searchDialogs(String str) {
        if (str == null || this.lastSearchText == null || !str.equals(this.lastSearchText)) {
            this.lastSearchText = str;
            try {
                if (this.searchTimer != null) {
                    this.searchTimer.cancel();
                    this.searchTimer = null;
                }
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
            if (str == null || str.length() == 0) {
                this.hashtagsLoadedFromDb = false;
                this.searchResult.clear();
                this.searchResultNames.clear();
                this.searchResultHashtags.clear();
                if (this.needMessagesSearch != 2) {
                    queryServerSearch(null, true);
                }
                searchMessagesInternal(null);
                notifyDataSetChanged();
            } else if (this.needMessagesSearch != 2 && str.startsWith("#") && str.length() == 1) {
                this.messagesSearchEndReached = true;
                if (this.hashtagsLoadedFromDb) {
                    this.searchResultMessages.clear();
                    this.searchResultHashtags.clear();
                    for (int i = 0; i < this.hashtags.size(); i++) {
                        this.searchResultHashtags.add(((HashtagObject) this.hashtags.get(i)).hashtag);
                    }
                    if (this.delegate != null) {
                        this.delegate.searchStateChanged(false);
                    }
                    notifyDataSetChanged();
                    return;
                }
                loadRecentHashtags();
                if (this.delegate != null) {
                    this.delegate.searchStateChanged(true);
                }
                notifyDataSetChanged();
            } else {
                this.searchResultHashtags.clear();
                notifyDataSetChanged();
                int i2 = this.lastSearchId + 1;
                this.lastSearchId = i2;
                this.searchTimer = new Timer();
                this.searchTimer.schedule(new C10247(str, i2), 200, 300);
            }
        }
    }

    public void setDelegate(DialogsSearchAdapterDelegate dialogsSearchAdapterDelegate) {
        this.delegate = dialogsSearchAdapterDelegate;
    }

    protected void setHashtags(ArrayList<HashtagObject> arrayList, HashMap<String, HashtagObject> hashMap) {
        super.setHashtags(arrayList, hashMap);
        for (int i = 0; i < arrayList.size(); i++) {
            this.searchResultHashtags.add(((HashtagObject) arrayList.get(i)).hashtag);
        }
        if (this.delegate != null) {
            this.delegate.searchStateChanged(false);
        }
        notifyDataSetChanged();
    }
}
