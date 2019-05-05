package com.hanista.mobogram.ui.Adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.ContactsController;
import com.hanista.mobogram.messenger.FileLog;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.MessagesController;
import com.hanista.mobogram.messenger.UserConfig;
import com.hanista.mobogram.messenger.Utilities;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;
import com.hanista.mobogram.tgnet.TLObject;
import com.hanista.mobogram.tgnet.TLRPC.Chat;
import com.hanista.mobogram.tgnet.TLRPC.TL_contact;
import com.hanista.mobogram.tgnet.TLRPC.User;
import com.hanista.mobogram.ui.Cells.GreySectionCell;
import com.hanista.mobogram.ui.Cells.ProfileSearchCell;
import com.hanista.mobogram.ui.Cells.UserCell;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class SearchAdapter extends BaseSearchAdapter {
    private boolean allowBots;
    private boolean allowChats;
    private boolean allowUsernameSearch;
    private HashMap<Integer, ?> checkedMap;
    private HashMap<Integer, User> ignoreUsers;
    private Context mContext;
    private boolean onlyMutual;
    private ArrayList<User> searchResult;
    private ArrayList<CharSequence> searchResultNames;
    private Timer searchTimer;
    private boolean useUserCell;

    /* renamed from: com.hanista.mobogram.ui.Adapters.SearchAdapter.1 */
    class C10381 extends TimerTask {
        final /* synthetic */ String val$query;

        C10381(String str) {
            this.val$query = str;
        }

        public void run() {
            try {
                SearchAdapter.this.searchTimer.cancel();
                SearchAdapter.this.searchTimer = null;
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
            SearchAdapter.this.processSearch(this.val$query);
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Adapters.SearchAdapter.2 */
    class C10402 implements Runnable {
        final /* synthetic */ String val$query;

        /* renamed from: com.hanista.mobogram.ui.Adapters.SearchAdapter.2.1 */
        class C10391 implements Runnable {
            final /* synthetic */ ArrayList val$contactsCopy;

            C10391(ArrayList arrayList) {
                this.val$contactsCopy = arrayList;
            }

            public void run() {
                String toLowerCase = C10402.this.val$query.trim().toLowerCase();
                if (toLowerCase.length() == 0) {
                    SearchAdapter.this.updateSearchResults(new ArrayList(), new ArrayList());
                    return;
                }
                String translitString = LocaleController.getInstance().getTranslitString(toLowerCase);
                String str = (toLowerCase.equals(translitString) || translitString.length() == 0) ? null : translitString;
                String[] strArr = new String[((str != null ? 1 : 0) + 1)];
                strArr[0] = toLowerCase;
                if (str != null) {
                    strArr[1] = str;
                }
                ArrayList arrayList = new ArrayList();
                ArrayList arrayList2 = new ArrayList();
                for (int i = 0; i < this.val$contactsCopy.size(); i++) {
                    User user = MessagesController.getInstance().getUser(Integer.valueOf(((TL_contact) this.val$contactsCopy.get(i)).user_id));
                    if (user.id != UserConfig.getClientUserId() && (!SearchAdapter.this.onlyMutual || user.mutual_contact)) {
                        String toLowerCase2 = ContactsController.formatName(user.first_name, user.last_name).toLowerCase();
                        translitString = LocaleController.getInstance().getTranslitString(toLowerCase2);
                        if (toLowerCase2.equals(translitString)) {
                            translitString = null;
                        }
                        int length = strArr.length;
                        Object obj = null;
                        int i2 = 0;
                        while (i2 < length) {
                            String str2 = strArr[i2];
                            if (toLowerCase2.startsWith(str2) || toLowerCase2.contains(" " + str2) || (r0 != null && (r0.startsWith(str2) || r0.contains(" " + str2)))) {
                                obj = 1;
                            } else if (user.username != null && user.username.startsWith(str2)) {
                                obj = 2;
                            }
                            if (r2 != null) {
                                if (r2 == 1) {
                                    arrayList2.add(AndroidUtilities.generateSearchName(user.first_name, user.last_name, str2));
                                } else {
                                    arrayList2.add(AndroidUtilities.generateSearchName("@" + user.username, null, "@" + str2));
                                }
                                arrayList.add(user);
                            } else {
                                i2++;
                            }
                        }
                    }
                }
                SearchAdapter.this.updateSearchResults(arrayList, arrayList2);
            }
        }

        C10402(String str) {
            this.val$query = str;
        }

        public void run() {
            if (SearchAdapter.this.allowUsernameSearch) {
                SearchAdapter.this.queryServerSearch(this.val$query, SearchAdapter.this.allowChats, SearchAdapter.this.allowBots);
            }
            ArrayList arrayList = new ArrayList();
            arrayList.addAll(ContactsController.getInstance().contacts);
            Utilities.searchQueue.postRunnable(new C10391(arrayList));
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Adapters.SearchAdapter.3 */
    class C10413 implements Runnable {
        final /* synthetic */ ArrayList val$names;
        final /* synthetic */ ArrayList val$users;

        C10413(ArrayList arrayList, ArrayList arrayList2) {
            this.val$users = arrayList;
            this.val$names = arrayList2;
        }

        public void run() {
            SearchAdapter.this.searchResult = this.val$users;
            SearchAdapter.this.searchResultNames = this.val$names;
            SearchAdapter.this.notifyDataSetChanged();
        }
    }

    public SearchAdapter(Context context, HashMap<Integer, User> hashMap, boolean z, boolean z2, boolean z3, boolean z4) {
        this.searchResult = new ArrayList();
        this.searchResultNames = new ArrayList();
        this.mContext = context;
        this.ignoreUsers = hashMap;
        this.onlyMutual = z2;
        this.allowUsernameSearch = z;
        this.allowChats = z3;
        this.allowBots = z4;
    }

    private void processSearch(String str) {
        AndroidUtilities.runOnUIThread(new C10402(str));
    }

    private void updateSearchResults(ArrayList<User> arrayList, ArrayList<CharSequence> arrayList2) {
        AndroidUtilities.runOnUIThread(new C10413(arrayList, arrayList2));
    }

    public boolean areAllItemsEnabled() {
        return false;
    }

    public int getCount() {
        int size = this.searchResult.size();
        int size2 = this.globalSearch.size();
        return size2 != 0 ? size + (size2 + 1) : size;
    }

    public TLObject getItem(int i) {
        int size = this.searchResult.size();
        return (i < 0 || i >= size) ? (i <= size || i > this.globalSearch.size() + size) ? null : (TLObject) this.globalSearch.get((i - size) - 1) : (TLObject) this.searchResult.get(i);
    }

    public long getItemId(int i) {
        return (long) i;
    }

    public int getItemViewType(int i) {
        return i == this.searchResult.size() ? 1 : 0;
    }

    public View getView(int i, View view, ViewGroup viewGroup) {
        String str;
        View userCell;
        if (i != this.searchResult.size()) {
            View view2;
            if (view != null) {
                view2 = view;
            } else if (this.useUserCell) {
                userCell = new UserCell(this.mContext, 1, 1, false);
                if (this.checkedMap != null) {
                    ((UserCell) userCell).setChecked(false, false);
                    view2 = userCell;
                } else {
                    view2 = userCell;
                }
            } else {
                view2 = new ProfileSearchCell(this.mContext);
            }
            TLObject item = getItem(i);
            if (item == null) {
                return view2;
            }
            int i2;
            CharSequence charSequence;
            if (item instanceof User) {
                str = ((User) item).username;
                i2 = ((User) item).id;
            } else if (item instanceof Chat) {
                str = ((Chat) item).username;
                i2 = ((Chat) item).id;
            } else {
                i2 = 0;
                str = null;
            }
            CharSequence charSequence2 = null;
            if (i < this.searchResult.size()) {
                CharSequence charSequence3 = (CharSequence) this.searchResultNames.get(i);
                if (charSequence3 == null || str == null || str.length() <= 0 || !charSequence3.toString().startsWith("@" + str)) {
                    charSequence = charSequence3;
                } else {
                    charSequence = null;
                    charSequence2 = charSequence3;
                }
            } else if (i <= this.searchResult.size() || str == null) {
                charSequence = null;
            } else {
                String str2 = this.lastFoundUsername;
                if (str2.startsWith("@")) {
                    str2 = str2.substring(1);
                }
                try {
                    charSequence2 = AndroidUtilities.replaceTags(String.format("<c#ff4d83b3>@%s</c>%s", new Object[]{str.substring(0, str2.length()), str.substring(str2.length())}));
                    charSequence = null;
                } catch (Throwable e) {
                    FileLog.m18e("tmessages", e);
                    charSequence = null;
                    Object obj = str;
                }
            }
            if (this.useUserCell) {
                ((UserCell) view2).setData(item, charSequence, charSequence2, 0);
                if (this.checkedMap == null) {
                    return view2;
                }
                ((UserCell) view2).setChecked(this.checkedMap.containsKey(Integer.valueOf(i2)), false);
                return view2;
            }
            ((ProfileSearchCell) view2).setData(item, null, charSequence, charSequence2, false);
            ProfileSearchCell profileSearchCell = (ProfileSearchCell) view2;
            boolean z = (i == getCount() + -1 || i == this.searchResult.size() - 1) ? false : true;
            profileSearchCell.useSeparator = z;
            if (this.ignoreUsers == null) {
                return view2;
            }
            if (this.ignoreUsers.containsKey(Integer.valueOf(i2))) {
                ((ProfileSearchCell) view2).drawAlpha = 0.5f;
                return view2;
            }
            ((ProfileSearchCell) view2).drawAlpha = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
            return view2;
        } else if (view != null) {
            return view;
        } else {
            userCell = new GreySectionCell(this.mContext);
            ((GreySectionCell) userCell).setText(LocaleController.getString("GlobalSearch", C0338R.string.GlobalSearch));
            return userCell;
        }
    }

    public int getViewTypeCount() {
        return 2;
    }

    public boolean hasStableIds() {
        return true;
    }

    public boolean isEmpty() {
        return this.searchResult.isEmpty() && this.globalSearch.isEmpty();
    }

    public boolean isEnabled(int i) {
        return i != this.searchResult.size();
    }

    public boolean isGlobalSearch(int i) {
        int size = this.searchResult.size();
        return (i < 0 || i >= size) && i > size && i <= size + this.globalSearch.size();
    }

    public void searchDialogs(String str) {
        try {
            if (this.searchTimer != null) {
                this.searchTimer.cancel();
            }
        } catch (Throwable e) {
            FileLog.m18e("tmessages", e);
        }
        if (str == null) {
            this.searchResult.clear();
            this.searchResultNames.clear();
            if (this.allowUsernameSearch) {
                queryServerSearch(null, this.allowChats, this.allowBots);
            }
            notifyDataSetChanged();
            return;
        }
        this.searchTimer = new Timer();
        this.searchTimer.schedule(new C10381(str), 200, 300);
    }

    public void setCheckedMap(HashMap<Integer, ?> hashMap) {
        this.checkedMap = hashMap;
    }

    public void setUseUserCell(boolean z) {
        this.useUserCell = z;
    }
}
