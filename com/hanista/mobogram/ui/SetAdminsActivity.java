package com.hanista.mobogram.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import com.google.android.gms.vision.face.Face;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.ContactsController;
import com.hanista.mobogram.messenger.FileLog;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.MessagesController;
import com.hanista.mobogram.messenger.NotificationCenter;
import com.hanista.mobogram.messenger.NotificationCenter.NotificationCenterDelegate;
import com.hanista.mobogram.messenger.UserConfig;
import com.hanista.mobogram.messenger.Utilities;
import com.hanista.mobogram.mobo.p020s.AdvanceTheme;
import com.hanista.mobogram.mobo.p020s.ThemeUtil;
import com.hanista.mobogram.tgnet.TLObject;
import com.hanista.mobogram.tgnet.TLRPC.Chat;
import com.hanista.mobogram.tgnet.TLRPC.ChatFull;
import com.hanista.mobogram.tgnet.TLRPC.ChatParticipant;
import com.hanista.mobogram.tgnet.TLRPC.TL_chatParticipant;
import com.hanista.mobogram.tgnet.TLRPC.TL_chatParticipantAdmin;
import com.hanista.mobogram.tgnet.TLRPC.TL_chatParticipantCreator;
import com.hanista.mobogram.tgnet.TLRPC.User;
import com.hanista.mobogram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import com.hanista.mobogram.ui.ActionBar.ActionBarMenuItem;
import com.hanista.mobogram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener;
import com.hanista.mobogram.ui.ActionBar.BaseFragment;
import com.hanista.mobogram.ui.ActionBar.Theme;
import com.hanista.mobogram.ui.Adapters.BaseFragmentAdapter;
import com.hanista.mobogram.ui.Cells.TextCheckCell;
import com.hanista.mobogram.ui.Cells.TextInfoPrivacyCell;
import com.hanista.mobogram.ui.Cells.UserCell;
import com.hanista.mobogram.ui.Components.EmptyTextProgressView;
import com.hanista.mobogram.ui.Components.LayoutHelper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Timer;
import java.util.TimerTask;

public class SetAdminsActivity extends BaseFragment implements NotificationCenterDelegate {
    private int allAdminsInfoRow;
    private int allAdminsRow;
    private Chat chat;
    private int chat_id;
    private ChatFull info;
    private ListAdapter listAdapter;
    private ListView listView;
    private ArrayList<ChatParticipant> participants;
    private int rowCount;
    private SearchAdapter searchAdapter;
    private EmptyTextProgressView searchEmptyView;
    private ActionBarMenuItem searchItem;
    private boolean searchWas;
    private boolean searching;
    private int usersEndRow;
    private int usersStartRow;

    /* renamed from: com.hanista.mobogram.ui.SetAdminsActivity.1 */
    class C18771 extends ActionBarMenuOnItemClick {
        C18771() {
        }

        public void onItemClick(int i) {
            if (i == -1) {
                SetAdminsActivity.this.finishFragment();
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.SetAdminsActivity.2 */
    class C18782 extends ActionBarMenuItemSearchListener {
        C18782() {
        }

        public void onSearchCollapse() {
            SetAdminsActivity.this.searching = false;
            SetAdminsActivity.this.searchWas = false;
            if (SetAdminsActivity.this.listView != null) {
                SetAdminsActivity.this.listView.setEmptyView(null);
                SetAdminsActivity.this.searchEmptyView.setVisibility(8);
                if (SetAdminsActivity.this.listView.getAdapter() != SetAdminsActivity.this.listAdapter) {
                    SetAdminsActivity.this.listView.setAdapter(SetAdminsActivity.this.listAdapter);
                    SetAdminsActivity.this.fragmentView.setBackgroundColor(Theme.ACTION_BAR_MODE_SELECTOR_COLOR);
                }
            }
            if (SetAdminsActivity.this.searchAdapter != null) {
                SetAdminsActivity.this.searchAdapter.search(null);
            }
        }

        public void onSearchExpand() {
            SetAdminsActivity.this.searching = true;
            SetAdminsActivity.this.listView.setEmptyView(SetAdminsActivity.this.searchEmptyView);
        }

        public void onTextChanged(EditText editText) {
            String obj = editText.getText().toString();
            if (obj.length() != 0) {
                SetAdminsActivity.this.searchWas = true;
                if (!(SetAdminsActivity.this.searchAdapter == null || SetAdminsActivity.this.listView.getAdapter() == SetAdminsActivity.this.searchAdapter)) {
                    SetAdminsActivity.this.listView.setAdapter(SetAdminsActivity.this.searchAdapter);
                    SetAdminsActivity.this.fragmentView.setBackgroundColor(-1);
                }
                if (!(SetAdminsActivity.this.searchEmptyView == null || SetAdminsActivity.this.listView.getEmptyView() == SetAdminsActivity.this.searchEmptyView)) {
                    SetAdminsActivity.this.searchEmptyView.showTextView();
                    SetAdminsActivity.this.listView.setEmptyView(SetAdminsActivity.this.searchEmptyView);
                }
            }
            if (SetAdminsActivity.this.searchAdapter != null) {
                SetAdminsActivity.this.searchAdapter.search(obj);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.SetAdminsActivity.3 */
    class C18793 implements OnItemClickListener {
        C18793() {
        }

        public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
            boolean z = true;
            if (SetAdminsActivity.this.listView.getAdapter() == SetAdminsActivity.this.searchAdapter || (i >= SetAdminsActivity.this.usersStartRow && i < SetAdminsActivity.this.usersEndRow)) {
                ChatParticipant item;
                int i2;
                UserCell userCell = (UserCell) view;
                SetAdminsActivity.this.chat = MessagesController.getInstance().getChat(Integer.valueOf(SetAdminsActivity.this.chat_id));
                if (SetAdminsActivity.this.listView.getAdapter() == SetAdminsActivity.this.searchAdapter) {
                    item = SetAdminsActivity.this.searchAdapter.getItem(i);
                    i2 = 0;
                    while (i2 < SetAdminsActivity.this.participants.size()) {
                        if (((ChatParticipant) SetAdminsActivity.this.participants.get(i2)).user_id == item.user_id) {
                            break;
                        }
                        i2++;
                    }
                    i2 = -1;
                } else {
                    i2 = i - SetAdminsActivity.this.usersStartRow;
                    item = (ChatParticipant) SetAdminsActivity.this.participants.get(i2);
                }
                if (i2 != -1 && !(item instanceof TL_chatParticipantCreator)) {
                    ChatParticipant tL_chatParticipantAdmin;
                    if (item instanceof TL_chatParticipant) {
                        tL_chatParticipantAdmin = new TL_chatParticipantAdmin();
                        tL_chatParticipantAdmin.user_id = item.user_id;
                        tL_chatParticipantAdmin.date = item.date;
                        tL_chatParticipantAdmin.inviter_id = item.inviter_id;
                    } else {
                        tL_chatParticipantAdmin = new TL_chatParticipant();
                        tL_chatParticipantAdmin.user_id = item.user_id;
                        tL_chatParticipantAdmin.date = item.date;
                        tL_chatParticipantAdmin.inviter_id = item.inviter_id;
                    }
                    SetAdminsActivity.this.participants.set(i2, tL_chatParticipantAdmin);
                    i2 = SetAdminsActivity.this.info.participants.participants.indexOf(item);
                    if (i2 != -1) {
                        SetAdminsActivity.this.info.participants.participants.set(i2, tL_chatParticipantAdmin);
                    }
                    if (SetAdminsActivity.this.listView.getAdapter() == SetAdminsActivity.this.searchAdapter) {
                        SetAdminsActivity.this.searchAdapter.searchResult.set(i, tL_chatParticipantAdmin);
                    }
                    boolean z2 = ((tL_chatParticipantAdmin instanceof TL_chatParticipant) && (SetAdminsActivity.this.chat == null || SetAdminsActivity.this.chat.admins_enabled)) ? false : true;
                    userCell.setChecked(z2, true);
                    if (SetAdminsActivity.this.chat != null && SetAdminsActivity.this.chat.admins_enabled) {
                        MessagesController instance = MessagesController.getInstance();
                        int access$1100 = SetAdminsActivity.this.chat_id;
                        int i3 = tL_chatParticipantAdmin.user_id;
                        if (tL_chatParticipantAdmin instanceof TL_chatParticipant) {
                            z = false;
                        }
                        instance.toggleUserAdmin(access$1100, i3, z);
                    }
                }
            } else if (i == SetAdminsActivity.this.allAdminsRow) {
                SetAdminsActivity.this.chat = MessagesController.getInstance().getChat(Integer.valueOf(SetAdminsActivity.this.chat_id));
                if (SetAdminsActivity.this.chat != null) {
                    SetAdminsActivity.this.chat.admins_enabled = !SetAdminsActivity.this.chat.admins_enabled;
                    TextCheckCell textCheckCell = (TextCheckCell) view;
                    if (SetAdminsActivity.this.chat.admins_enabled) {
                        z = false;
                    }
                    textCheckCell.setChecked(z);
                    MessagesController.getInstance().toggleAdminMode(SetAdminsActivity.this.chat_id, SetAdminsActivity.this.chat.admins_enabled);
                }
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.SetAdminsActivity.4 */
    class C18804 implements Comparator<ChatParticipant> {
        C18804() {
        }

        public int compare(ChatParticipant chatParticipant, ChatParticipant chatParticipant2) {
            int access$1600 = SetAdminsActivity.this.getChatAdminParticipantType(chatParticipant);
            int access$16002 = SetAdminsActivity.this.getChatAdminParticipantType(chatParticipant2);
            if (access$1600 > access$16002) {
                return 1;
            }
            if (access$1600 < access$16002) {
                return -1;
            }
            if (access$1600 == access$16002) {
                User user = MessagesController.getInstance().getUser(Integer.valueOf(chatParticipant2.user_id));
                User user2 = MessagesController.getInstance().getUser(Integer.valueOf(chatParticipant.user_id));
                access$16002 = (user == null || user.status == null) ? 0 : user.status.expires;
                access$1600 = (user2 == null || user2.status == null) ? 0 : user2.status.expires;
                if (access$16002 > 0 && access$1600 > 0) {
                    return access$16002 <= access$1600 ? access$16002 < access$1600 ? -1 : 0 : 1;
                } else {
                    if (access$16002 < 0 && access$1600 < 0) {
                        return access$16002 <= access$1600 ? access$16002 < access$1600 ? -1 : 0 : 1;
                    } else {
                        if ((access$16002 < 0 && access$1600 > 0) || (access$16002 == 0 && access$1600 != 0)) {
                            return -1;
                        }
                        if (access$1600 < 0 && access$16002 > 0) {
                            return 1;
                        }
                        if (access$1600 == 0 && access$16002 != 0) {
                            return 1;
                        }
                    }
                }
            }
            return 0;
        }
    }

    private class ListAdapter extends BaseFragmentAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean areAllItemsEnabled() {
            return false;
        }

        public int getCount() {
            return SetAdminsActivity.this.rowCount;
        }

        public Object getItem(int i) {
            return null;
        }

        public long getItemId(int i) {
            return (long) i;
        }

        public int getItemViewType(int i) {
            return i == SetAdminsActivity.this.allAdminsRow ? 0 : (i == SetAdminsActivity.this.allAdminsInfoRow || i == SetAdminsActivity.this.usersEndRow) ? 1 : 2;
        }

        public View getView(int i, View view, ViewGroup viewGroup) {
            boolean z = false;
            int itemViewType = getItemViewType(i);
            View textCheckCell;
            if (itemViewType == 0) {
                if (view == null) {
                    textCheckCell = new TextCheckCell(this.mContext);
                    textCheckCell.setBackgroundColor(-1);
                } else {
                    textCheckCell = view;
                }
                TextCheckCell textCheckCell2 = (TextCheckCell) textCheckCell;
                SetAdminsActivity.this.chat = MessagesController.getInstance().getChat(Integer.valueOf(SetAdminsActivity.this.chat_id));
                String string = LocaleController.getString("SetAdminsAll", C0338R.string.SetAdminsAll);
                boolean z2 = (SetAdminsActivity.this.chat == null || SetAdminsActivity.this.chat.admins_enabled) ? false : true;
                textCheckCell2.setTextAndCheck(string, z2, false);
                return textCheckCell;
            } else if (itemViewType == 1) {
                textCheckCell = view == null ? new TextInfoPrivacyCell(this.mContext) : view;
                if (i == SetAdminsActivity.this.allAdminsInfoRow) {
                    if (SetAdminsActivity.this.chat.admins_enabled) {
                        ((TextInfoPrivacyCell) textCheckCell).setText(LocaleController.getString("SetAdminsNotAllInfo", C0338R.string.SetAdminsNotAllInfo));
                    } else {
                        ((TextInfoPrivacyCell) textCheckCell).setText(LocaleController.getString("SetAdminsAllInfo", C0338R.string.SetAdminsAllInfo));
                    }
                    if (SetAdminsActivity.this.usersStartRow != -1) {
                        textCheckCell.setBackgroundResource(C0338R.drawable.greydivider);
                        return textCheckCell;
                    }
                    textCheckCell.setBackgroundResource(C0338R.drawable.greydivider_bottom);
                    return textCheckCell;
                } else if (i != SetAdminsActivity.this.usersEndRow) {
                    return textCheckCell;
                } else {
                    ((TextInfoPrivacyCell) textCheckCell).setText(TtmlNode.ANONYMOUS_REGION_ID);
                    textCheckCell.setBackgroundResource(C0338R.drawable.greydivider_bottom);
                    return textCheckCell;
                }
            } else if (itemViewType != 2) {
                return view;
            } else {
                if (view == null) {
                    textCheckCell = new UserCell(this.mContext, 1, 2, false);
                    textCheckCell.setBackgroundColor(-1);
                } else {
                    textCheckCell = view;
                }
                UserCell userCell = (UserCell) textCheckCell;
                ChatParticipant chatParticipant = (ChatParticipant) SetAdminsActivity.this.participants.get(i - SetAdminsActivity.this.usersStartRow);
                userCell.setData(MessagesController.getInstance().getUser(Integer.valueOf(chatParticipant.user_id)), null, null, 0);
                SetAdminsActivity.this.chat = MessagesController.getInstance().getChat(Integer.valueOf(SetAdminsActivity.this.chat_id));
                boolean z3 = ((chatParticipant instanceof TL_chatParticipant) && (SetAdminsActivity.this.chat == null || SetAdminsActivity.this.chat.admins_enabled)) ? false : true;
                userCell.setChecked(z3, false);
                if (SetAdminsActivity.this.chat == null || !SetAdminsActivity.this.chat.admins_enabled || chatParticipant.user_id == UserConfig.getClientUserId()) {
                    z = true;
                }
                userCell.setCheckDisabled(z);
                if (!ThemeUtil.m2490b()) {
                    return textCheckCell;
                }
                userCell.setBackgroundColor(AdvanceTheme.aA);
                userCell.setTag("Profile");
                return textCheckCell;
            }
        }

        public int getViewTypeCount() {
            return 3;
        }

        public boolean hasStableIds() {
            return false;
        }

        public boolean isEmpty() {
            return false;
        }

        public boolean isEnabled(int i) {
            return i == SetAdminsActivity.this.allAdminsRow ? true : i >= SetAdminsActivity.this.usersStartRow && i < SetAdminsActivity.this.usersEndRow && !(((ChatParticipant) SetAdminsActivity.this.participants.get(i - SetAdminsActivity.this.usersStartRow)) instanceof TL_chatParticipantCreator);
        }
    }

    public class SearchAdapter extends BaseFragmentAdapter {
        private Context mContext;
        private ArrayList<ChatParticipant> searchResult;
        private ArrayList<CharSequence> searchResultNames;
        private Timer searchTimer;

        /* renamed from: com.hanista.mobogram.ui.SetAdminsActivity.SearchAdapter.1 */
        class C18811 extends TimerTask {
            final /* synthetic */ String val$query;

            C18811(String str) {
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

        /* renamed from: com.hanista.mobogram.ui.SetAdminsActivity.SearchAdapter.2 */
        class C18832 implements Runnable {
            final /* synthetic */ String val$query;

            /* renamed from: com.hanista.mobogram.ui.SetAdminsActivity.SearchAdapter.2.1 */
            class C18821 implements Runnable {
                final /* synthetic */ ArrayList val$contactsCopy;

                C18821(ArrayList arrayList) {
                    this.val$contactsCopy = arrayList;
                }

                public void run() {
                    String toLowerCase = C18832.this.val$query.trim().toLowerCase();
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
                        ChatParticipant chatParticipant = (ChatParticipant) this.val$contactsCopy.get(i);
                        User user = MessagesController.getInstance().getUser(Integer.valueOf(chatParticipant.user_id));
                        if (user.id != UserConfig.getClientUserId()) {
                            String toLowerCase2 = ContactsController.formatName(user.first_name, user.last_name).toLowerCase();
                            toLowerCase = LocaleController.getInstance().getTranslitString(toLowerCase2);
                            if (toLowerCase2.equals(toLowerCase)) {
                                toLowerCase = null;
                            }
                            int length = strArr.length;
                            Object obj = null;
                            int i2 = 0;
                            while (i2 < length) {
                                String str2 = strArr[i2];
                                if (toLowerCase2.startsWith(str2) || toLowerCase2.contains(" " + str2) || (r2 != null && (r2.startsWith(str2) || r2.contains(" " + str2)))) {
                                    obj = 1;
                                } else if (user.username != null && user.username.startsWith(str2)) {
                                    obj = 2;
                                }
                                if (r3 != null) {
                                    if (r3 == 1) {
                                        arrayList2.add(AndroidUtilities.generateSearchName(user.first_name, user.last_name, str2));
                                    } else {
                                        arrayList2.add(AndroidUtilities.generateSearchName("@" + user.username, null, "@" + str2));
                                    }
                                    arrayList.add(chatParticipant);
                                } else {
                                    i2++;
                                }
                            }
                        }
                    }
                    SearchAdapter.this.updateSearchResults(arrayList, arrayList2);
                }
            }

            C18832(String str) {
                this.val$query = str;
            }

            public void run() {
                ArrayList arrayList = new ArrayList();
                arrayList.addAll(SetAdminsActivity.this.participants);
                Utilities.searchQueue.postRunnable(new C18821(arrayList));
            }
        }

        /* renamed from: com.hanista.mobogram.ui.SetAdminsActivity.SearchAdapter.3 */
        class C18843 implements Runnable {
            final /* synthetic */ ArrayList val$names;
            final /* synthetic */ ArrayList val$users;

            C18843(ArrayList arrayList, ArrayList arrayList2) {
                this.val$users = arrayList;
                this.val$names = arrayList2;
            }

            public void run() {
                SearchAdapter.this.searchResult = this.val$users;
                SearchAdapter.this.searchResultNames = this.val$names;
                SearchAdapter.this.notifyDataSetChanged();
            }
        }

        public SearchAdapter(Context context) {
            this.searchResult = new ArrayList();
            this.searchResultNames = new ArrayList();
            this.mContext = context;
        }

        private void processSearch(String str) {
            AndroidUtilities.runOnUIThread(new C18832(str));
        }

        private void updateSearchResults(ArrayList<ChatParticipant> arrayList, ArrayList<CharSequence> arrayList2) {
            AndroidUtilities.runOnUIThread(new C18843(arrayList, arrayList2));
        }

        public boolean areAllItemsEnabled() {
            return true;
        }

        public int getCount() {
            return this.searchResult.size();
        }

        public ChatParticipant getItem(int i) {
            return (ChatParticipant) this.searchResult.get(i);
        }

        public long getItemId(int i) {
            return (long) i;
        }

        public int getItemViewType(int i) {
            return 0;
        }

        public View getView(int i, View view, ViewGroup viewGroup) {
            CharSequence charSequence;
            CharSequence charSequence2 = null;
            boolean z = false;
            View userCell = view == null ? new UserCell(this.mContext, 1, 2, false) : view;
            ChatParticipant item = getItem(i);
            TLObject user = MessagesController.getInstance().getUser(Integer.valueOf(item.user_id));
            String str = user.username;
            if (i < this.searchResult.size()) {
                CharSequence charSequence3 = (CharSequence) this.searchResultNames.get(i);
                if (charSequence3 == null || str == null || str.length() <= 0 || !charSequence3.toString().startsWith("@" + str)) {
                    charSequence = null;
                    charSequence2 = charSequence3;
                } else {
                    charSequence = charSequence3;
                }
            } else {
                charSequence = null;
            }
            UserCell userCell2 = (UserCell) userCell;
            userCell2.setData(user, charSequence2, charSequence, 0);
            SetAdminsActivity.this.chat = MessagesController.getInstance().getChat(Integer.valueOf(SetAdminsActivity.this.chat_id));
            boolean z2 = ((item instanceof TL_chatParticipant) && (SetAdminsActivity.this.chat == null || SetAdminsActivity.this.chat.admins_enabled)) ? false : true;
            userCell2.setChecked(z2, false);
            if (SetAdminsActivity.this.chat == null || !SetAdminsActivity.this.chat.admins_enabled || item.user_id == UserConfig.getClientUserId()) {
                z = true;
            }
            userCell2.setCheckDisabled(z);
            return userCell;
        }

        public int getViewTypeCount() {
            return 1;
        }

        public boolean hasStableIds() {
            return true;
        }

        public boolean isEmpty() {
            return this.searchResult.isEmpty();
        }

        public boolean isEnabled(int i) {
            return true;
        }

        public void search(String str) {
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
                notifyDataSetChanged();
                return;
            }
            this.searchTimer = new Timer();
            this.searchTimer.schedule(new C18811(str), 200, 300);
        }
    }

    public SetAdminsActivity(Bundle bundle) {
        super(bundle);
        this.participants = new ArrayList();
        this.chat_id = bundle.getInt("chat_id");
    }

    private int getChatAdminParticipantType(ChatParticipant chatParticipant) {
        return chatParticipant instanceof TL_chatParticipantCreator ? 0 : chatParticipant instanceof TL_chatParticipantAdmin ? 1 : 2;
    }

    private void updateChatParticipants() {
        if (this.info != null && this.participants.size() != this.info.participants.participants.size()) {
            this.participants.clear();
            this.participants.addAll(this.info.participants.participants);
            try {
                Collections.sort(this.participants, new C18804());
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
        }
    }

    private void updateRowsIds() {
        this.rowCount = 0;
        int i = this.rowCount;
        this.rowCount = i + 1;
        this.allAdminsRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.allAdminsInfoRow = i;
        if (this.info != null) {
            this.usersStartRow = this.rowCount;
            this.rowCount += this.participants.size();
            i = this.rowCount;
            this.rowCount = i + 1;
            this.usersEndRow = i;
            if (!(this.searchItem == null || this.searchWas)) {
                this.searchItem.setVisibility(0);
            }
        } else {
            this.usersStartRow = -1;
            this.usersEndRow = -1;
            if (this.searchItem != null) {
                this.searchItem.setVisibility(8);
            }
        }
        if (this.listAdapter != null) {
            this.listAdapter.notifyDataSetChanged();
        }
    }

    public View createView(Context context) {
        this.searching = false;
        this.searchWas = false;
        this.actionBar.setBackButtonImage(C0338R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("SetAdminsTitle", C0338R.string.SetAdminsTitle));
        this.actionBar.setActionBarMenuOnItemClick(new C18771());
        this.searchItem = this.actionBar.createMenu().addItem(0, (int) C0338R.drawable.ic_ab_search).setIsSearchField(true).setActionBarMenuItemSearchListener(new C18782());
        this.searchItem.getSearchField().setHint(LocaleController.getString("Search", C0338R.string.Search));
        this.listAdapter = new ListAdapter(context);
        this.searchAdapter = new SearchAdapter(context);
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        this.fragmentView.setBackgroundColor(Theme.ACTION_BAR_MODE_SELECTOR_COLOR);
        this.listView = new ListView(context);
        this.listView.setDivider(null);
        this.listView.setDividerHeight(0);
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setDrawSelectorOnTop(true);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, Face.UNCOMPUTED_PROBABILITY));
        this.listView.setAdapter(this.listAdapter);
        this.listView.setOnItemClickListener(new C18793());
        this.searchEmptyView = new EmptyTextProgressView(context);
        this.searchEmptyView.setVisibility(8);
        this.searchEmptyView.setShowAtCenter(true);
        this.searchEmptyView.setText(LocaleController.getString("NoResult", C0338R.string.NoResult));
        frameLayout.addView(this.searchEmptyView, LayoutHelper.createFrame(-1, Face.UNCOMPUTED_PROBABILITY));
        this.searchEmptyView.showTextView();
        updateRowsIds();
        return this.fragmentView;
    }

    public void didReceivedNotification(int i, Object... objArr) {
        int i2 = 0;
        if (i == NotificationCenter.chatInfoDidLoaded) {
            ChatFull chatFull = (ChatFull) objArr[0];
            if (chatFull.id == this.chat_id) {
                this.info = chatFull;
                updateChatParticipants();
                updateRowsIds();
            }
        }
        if (i == NotificationCenter.updateInterfaces) {
            int intValue = ((Integer) objArr[0]).intValue();
            if (((intValue & 2) != 0 || (intValue & 1) != 0 || (intValue & 4) != 0) && this.listView != null) {
                int childCount = this.listView.getChildCount();
                while (i2 < childCount) {
                    View childAt = this.listView.getChildAt(i2);
                    if (childAt instanceof UserCell) {
                        ((UserCell) childAt).update(intValue);
                    }
                    i2++;
                }
            }
        }
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.chatInfoDidLoaded);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.updateInterfaces);
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.chatInfoDidLoaded);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.updateInterfaces);
    }

    public void onResume() {
        super.onResume();
        if (this.listAdapter != null) {
            this.listAdapter.notifyDataSetChanged();
        }
    }

    public void setChatInfo(ChatFull chatFull) {
        this.info = chatFull;
        updateChatParticipants();
    }
}
