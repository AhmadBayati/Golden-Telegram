package com.hanista.mobogram.ui.Adapters;

import com.hanista.mobogram.SQLite.SQLiteCursor;
import com.hanista.mobogram.SQLite.SQLitePreparedStatement;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.FileLog;
import com.hanista.mobogram.messenger.MessagesStorage;
import com.hanista.mobogram.tgnet.ConnectionsManager;
import com.hanista.mobogram.tgnet.RequestDelegate;
import com.hanista.mobogram.tgnet.TLObject;
import com.hanista.mobogram.tgnet.TLRPC.TL_contacts_found;
import com.hanista.mobogram.tgnet.TLRPC.TL_contacts_search;
import com.hanista.mobogram.tgnet.TLRPC.TL_error;
import com.hanista.mobogram.tgnet.TLRPC.User;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BaseSearchAdapter extends BaseFragmentAdapter {
    protected ArrayList<TLObject> globalSearch;
    protected ArrayList<HashtagObject> hashtags;
    protected HashMap<String, HashtagObject> hashtagsByText;
    protected boolean hashtagsLoadedFromDb;
    protected String lastFoundUsername;
    private int lastReqId;
    private int reqId;

    /* renamed from: com.hanista.mobogram.ui.Adapters.BaseSearchAdapter.1 */
    class C09951 implements RequestDelegate {
        final /* synthetic */ boolean val$allowBots;
        final /* synthetic */ boolean val$allowChats;
        final /* synthetic */ int val$currentReqId;
        final /* synthetic */ String val$query;

        /* renamed from: com.hanista.mobogram.ui.Adapters.BaseSearchAdapter.1.1 */
        class C09941 implements Runnable {
            final /* synthetic */ TL_error val$error;
            final /* synthetic */ TLObject val$response;

            C09941(TL_error tL_error, TLObject tLObject) {
                this.val$error = tL_error;
                this.val$response = tLObject;
            }

            public void run() {
                if (C09951.this.val$currentReqId == BaseSearchAdapter.this.lastReqId && this.val$error == null) {
                    TL_contacts_found tL_contacts_found = (TL_contacts_found) this.val$response;
                    BaseSearchAdapter.this.globalSearch.clear();
                    if (C09951.this.val$allowChats) {
                        for (int i = 0; i < tL_contacts_found.chats.size(); i++) {
                            BaseSearchAdapter.this.globalSearch.add(tL_contacts_found.chats.get(i));
                        }
                    }
                    int i2 = 0;
                    while (i2 < tL_contacts_found.users.size()) {
                        if (C09951.this.val$allowBots || !((User) tL_contacts_found.users.get(i2)).bot) {
                            BaseSearchAdapter.this.globalSearch.add(tL_contacts_found.users.get(i2));
                        }
                        i2++;
                    }
                    BaseSearchAdapter.this.lastFoundUsername = C09951.this.val$query;
                    BaseSearchAdapter.this.notifyDataSetChanged();
                }
                BaseSearchAdapter.this.reqId = 0;
            }
        }

        C09951(int i, boolean z, boolean z2, String str) {
            this.val$currentReqId = i;
            this.val$allowChats = z;
            this.val$allowBots = z2;
            this.val$query = str;
        }

        public void run(TLObject tLObject, TL_error tL_error) {
            AndroidUtilities.runOnUIThread(new C09941(tL_error, tLObject));
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Adapters.BaseSearchAdapter.2 */
    class C09982 implements Runnable {

        /* renamed from: com.hanista.mobogram.ui.Adapters.BaseSearchAdapter.2.1 */
        class C09961 implements Comparator<HashtagObject> {
            C09961() {
            }

            public int compare(HashtagObject hashtagObject, HashtagObject hashtagObject2) {
                return hashtagObject.date < hashtagObject2.date ? 1 : hashtagObject.date > hashtagObject2.date ? -1 : 0;
            }
        }

        /* renamed from: com.hanista.mobogram.ui.Adapters.BaseSearchAdapter.2.2 */
        class C09972 implements Runnable {
            final /* synthetic */ ArrayList val$arrayList;
            final /* synthetic */ HashMap val$hashMap;

            C09972(ArrayList arrayList, HashMap hashMap) {
                this.val$arrayList = arrayList;
                this.val$hashMap = hashMap;
            }

            public void run() {
                BaseSearchAdapter.this.setHashtags(this.val$arrayList, this.val$hashMap);
            }
        }

        C09982() {
        }

        public void run() {
            try {
                SQLiteCursor queryFinalized = MessagesStorage.getInstance().getDatabase().queryFinalized("SELECT id, date FROM hashtag_recent_v2 WHERE 1", new Object[0]);
                Object arrayList = new ArrayList();
                HashMap hashMap = new HashMap();
                while (queryFinalized.next()) {
                    HashtagObject hashtagObject = new HashtagObject();
                    hashtagObject.hashtag = queryFinalized.stringValue(0);
                    hashtagObject.date = queryFinalized.intValue(1);
                    arrayList.add(hashtagObject);
                    hashMap.put(hashtagObject.hashtag, hashtagObject);
                }
                queryFinalized.dispose();
                Collections.sort(arrayList, new C09961());
                AndroidUtilities.runOnUIThread(new C09972(arrayList, hashMap));
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Adapters.BaseSearchAdapter.3 */
    class C09993 implements Runnable {
        final /* synthetic */ ArrayList val$arrayList;

        C09993(ArrayList arrayList) {
            this.val$arrayList = arrayList;
        }

        public void run() {
            int i = 100;
            try {
                MessagesStorage.getInstance().getDatabase().beginTransaction();
                SQLitePreparedStatement executeFast = MessagesStorage.getInstance().getDatabase().executeFast("REPLACE INTO hashtag_recent_v2 VALUES(?, ?)");
                int i2 = 0;
                while (i2 < this.val$arrayList.size() && i2 != 100) {
                    HashtagObject hashtagObject = (HashtagObject) this.val$arrayList.get(i2);
                    executeFast.requery();
                    executeFast.bindString(1, hashtagObject.hashtag);
                    executeFast.bindInteger(2, hashtagObject.date);
                    executeFast.step();
                    i2++;
                }
                executeFast.dispose();
                MessagesStorage.getInstance().getDatabase().commitTransaction();
                if (this.val$arrayList.size() >= 100) {
                    MessagesStorage.getInstance().getDatabase().beginTransaction();
                    while (i < this.val$arrayList.size()) {
                        MessagesStorage.getInstance().getDatabase().executeFast("DELETE FROM hashtag_recent_v2 WHERE id = '" + ((HashtagObject) this.val$arrayList.get(i)).hashtag + "'").stepThis().dispose();
                        i++;
                    }
                    MessagesStorage.getInstance().getDatabase().commitTransaction();
                }
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Adapters.BaseSearchAdapter.4 */
    class C10004 implements Runnable {
        C10004() {
        }

        public void run() {
            try {
                MessagesStorage.getInstance().getDatabase().executeFast("DELETE FROM hashtag_recent_v2 WHERE 1").stepThis().dispose();
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
        }
    }

    protected static class HashtagObject {
        int date;
        String hashtag;

        protected HashtagObject() {
        }
    }

    public BaseSearchAdapter() {
        this.globalSearch = new ArrayList();
        this.reqId = 0;
        this.lastFoundUsername = null;
        this.hashtagsLoadedFromDb = false;
    }

    private void putRecentHashtags(ArrayList<HashtagObject> arrayList) {
        MessagesStorage.getInstance().getStorageQueue().postRunnable(new C09993(arrayList));
    }

    public void addHashtagsFromMessage(String str) {
        if (str != null) {
            Matcher matcher = Pattern.compile("(^|\\s)#[\\w@\\.]+").matcher(str);
            int i = 0;
            while (matcher.find()) {
                i = matcher.start();
                int end = matcher.end();
                if (!(str.charAt(i) == '@' || str.charAt(i) == '#')) {
                    i++;
                }
                String substring = str.substring(i, end);
                if (this.hashtagsByText == null) {
                    this.hashtagsByText = new HashMap();
                    this.hashtags = new ArrayList();
                }
                HashtagObject hashtagObject = (HashtagObject) this.hashtagsByText.get(substring);
                if (hashtagObject == null) {
                    hashtagObject = new HashtagObject();
                    hashtagObject.hashtag = substring;
                    this.hashtagsByText.put(hashtagObject.hashtag, hashtagObject);
                } else {
                    this.hashtags.remove(hashtagObject);
                }
                hashtagObject.date = (int) (System.currentTimeMillis() / 1000);
                this.hashtags.add(0, hashtagObject);
                i = 1;
            }
            if (i != 0) {
                putRecentHashtags(this.hashtags);
            }
        }
    }

    public void clearRecentHashtags() {
        this.hashtags = new ArrayList();
        this.hashtagsByText = new HashMap();
        MessagesStorage.getInstance().getStorageQueue().postRunnable(new C10004());
    }

    public void loadRecentHashtags() {
        MessagesStorage.getInstance().getStorageQueue().postRunnable(new C09982());
    }

    public void queryServerSearch(String str, boolean z, boolean z2) {
        if (this.reqId != 0) {
            ConnectionsManager.getInstance().cancelRequest(this.reqId, true);
            this.reqId = 0;
        }
        if (str == null || str.length() < 5) {
            this.globalSearch.clear();
            this.lastReqId = 0;
            notifyDataSetChanged();
            return;
        }
        TLObject tL_contacts_search = new TL_contacts_search();
        tL_contacts_search.f2665q = str;
        tL_contacts_search.limit = 50;
        int i = this.lastReqId + 1;
        this.lastReqId = i;
        this.reqId = ConnectionsManager.getInstance().sendRequest(tL_contacts_search, new C09951(i, z, z2, str), 2);
    }

    protected void setHashtags(ArrayList<HashtagObject> arrayList, HashMap<String, HashtagObject> hashMap) {
        this.hashtags = arrayList;
        this.hashtagsByText = hashMap;
        this.hashtagsLoadedFromDb = true;
    }
}
