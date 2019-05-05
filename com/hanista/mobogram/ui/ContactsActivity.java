package com.hanista.mobogram.ui;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.ApplicationLoader;
import com.hanista.mobogram.messenger.ContactsController;
import com.hanista.mobogram.messenger.ContactsController.Contact;
import com.hanista.mobogram.messenger.FileLog;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.MessagesController;
import com.hanista.mobogram.messenger.MessagesStorage;
import com.hanista.mobogram.messenger.NotificationCenter;
import com.hanista.mobogram.messenger.NotificationCenter.NotificationCenterDelegate;
import com.hanista.mobogram.messenger.SecretChatHelper;
import com.hanista.mobogram.messenger.SendMessagesHelper;
import com.hanista.mobogram.messenger.UserConfig;
import com.hanista.mobogram.messenger.UserObject;
import com.hanista.mobogram.messenger.Utilities;
import com.hanista.mobogram.mobo.OnlineContactsAdapter;
import com.hanista.mobogram.mobo.p003d.Glow;
import com.hanista.mobogram.mobo.p010k.MaterialHelperUtil;
import com.hanista.mobogram.mobo.p016p.SpecificContactActivity;
import com.hanista.mobogram.mobo.p020s.AdvanceTheme;
import com.hanista.mobogram.mobo.p020s.ThemeUtil;
import com.hanista.mobogram.tgnet.TLRPC.EncryptedChat;
import com.hanista.mobogram.tgnet.TLRPC.User;
import com.hanista.mobogram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import com.hanista.mobogram.ui.ActionBar.ActionBarMenu;
import com.hanista.mobogram.ui.ActionBar.ActionBarMenuItem;
import com.hanista.mobogram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener;
import com.hanista.mobogram.ui.ActionBar.BaseFragment;
import com.hanista.mobogram.ui.ActionBar.BottomSheet;
import com.hanista.mobogram.ui.Adapters.BaseSectionsAdapter;
import com.hanista.mobogram.ui.Adapters.ContactsAdapter;
import com.hanista.mobogram.ui.Adapters.SearchAdapter;
import com.hanista.mobogram.ui.Cells.UserCell;
import com.hanista.mobogram.ui.Components.ChipSpan;
import com.hanista.mobogram.ui.Components.LetterSectionsListView;
import com.hanista.mobogram.ui.Components.VideoPlayer;
import com.hanista.mobogram.ui.DialogsActivity.DialogsActivityDelegate;
import com.hanista.mobogram.ui.GroupCreateActivity.GroupCreateActivityDelegate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class ContactsActivity extends BaseFragment implements NotificationCenterDelegate {
    private static final int add_button = 1;
    private static final int delete = 3;
    private static final int done_button = 10;
    private static final int refresh = 2;
    private static final int search_button = 0;
    private static final int select_all_button = 11;
    private boolean allowBots;
    private boolean allowUsernameSearch;
    private int chat_id;
    private boolean createSecretChat;
    private boolean creatingChat;
    private ContactsActivityDelegate delegate;
    private boolean destroyAfterSelect;
    private TextView emptyTextView;
    private HashMap<Integer, User> ignoreUsers;
    private LetterSectionsListView listView;
    private BaseSectionsAdapter listViewAdapter;
    private ContactsActivityMultiSelectDelegate multiSelectDelegate;
    private boolean multiSelectMode;
    private boolean needForwardCount;
    private boolean needPhonebook;
    private boolean onlyOnlines;
    private boolean onlyUsers;
    ProgressDialog progressDialog;
    private boolean returnAsResult;
    private SearchAdapter searchListViewAdapter;
    private boolean searchWas;
    private boolean searching;
    private String selectAlertString;
    private HashMap<Integer, ChipSpan> selectedContacts;

    public interface ContactsActivityDelegate {
        void didSelectContact(User user, String str);
    }

    public interface ContactsActivityMultiSelectDelegate {
        void didSelectContacts(List<Integer> list, String str);
    }

    /* renamed from: com.hanista.mobogram.ui.ContactsActivity.10 */
    class AnonymousClass10 implements OnClickListener {
        final /* synthetic */ ArrayList val$selectedContacts;

        AnonymousClass10(ArrayList arrayList) {
            this.val$selectedContacts = arrayList;
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            ArrayList arrayList = new ArrayList();
            Iterator it = this.val$selectedContacts.iterator();
            while (it.hasNext()) {
                arrayList.add(MessagesController.getInstance().getUser((Integer) it.next()));
            }
            ContactsController.getInstance().deleteContact(arrayList);
            if (ContactsActivity.this.listViewAdapter != null) {
                ContactsActivity.this.listViewAdapter.notifyDataSetChanged();
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ContactsActivity.12 */
    class AnonymousClass12 implements TextWatcher {
        final /* synthetic */ EditText val$editTextFinal;

        AnonymousClass12(EditText editText) {
            this.val$editTextFinal = editText;
        }

        public void afterTextChanged(Editable editable) {
            try {
                String obj = editable.toString();
                if (obj.length() != 0) {
                    int intValue = Utilities.parseInt(obj).intValue();
                    if (intValue < 0) {
                        this.val$editTextFinal.setText("0");
                        this.val$editTextFinal.setSelection(this.val$editTextFinal.length());
                    } else if (intValue > 300) {
                        this.val$editTextFinal.setText("300");
                        this.val$editTextFinal.setSelection(this.val$editTextFinal.length());
                    } else if (!obj.equals(TtmlNode.ANONYMOUS_REGION_ID + intValue)) {
                        this.val$editTextFinal.setText(TtmlNode.ANONYMOUS_REGION_ID + intValue);
                        this.val$editTextFinal.setSelection(this.val$editTextFinal.length());
                    }
                }
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ContactsActivity.13 */
    class AnonymousClass13 implements OnClickListener {
        final /* synthetic */ EditText val$finalEditText;
        final /* synthetic */ List val$userIds;

        AnonymousClass13(List list, EditText editText) {
            this.val$userIds = list;
            this.val$finalEditText = editText;
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            ContactsActivity.this.didSelectMultiResult(this.val$userIds, false, this.val$finalEditText != null ? this.val$finalEditText.getText().toString() : "0");
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ContactsActivity.14 */
    class AnonymousClass14 implements OnClickListener {
        final /* synthetic */ User val$user;
        final /* synthetic */ boolean val$userBlocked;

        /* renamed from: com.hanista.mobogram.ui.ContactsActivity.14.1 */
        class C15101 implements DialogsActivityDelegate {
            C15101() {
            }

            public void didSelectDialog(DialogsActivity dialogsActivity, long j, boolean z) {
                Bundle bundle = new Bundle();
                bundle.putBoolean("scrollToTopOnResume", true);
                int i = (int) j;
                if (i == 0) {
                    bundle.putInt("enc_id", (int) (j >> 32));
                } else if (i > 0) {
                    bundle.putInt("user_id", i);
                } else if (i < 0) {
                    bundle.putInt("chat_id", -i);
                }
                if (MessagesController.checkCanOpenChat(bundle, dialogsActivity)) {
                    ContactsActivity.this.presentFragment(new ChatActivity(bundle), true);
                    SendMessagesHelper.getInstance().sendMessage(AnonymousClass14.this.val$user, j, null, null, null);
                }
            }
        }

        /* renamed from: com.hanista.mobogram.ui.ContactsActivity.14.2 */
        class C15112 implements OnClickListener {
            C15112() {
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                if (AnonymousClass14.this.val$userBlocked) {
                    MessagesController.getInstance().unblockUser(AnonymousClass14.this.val$user.id);
                } else {
                    MessagesController.getInstance().blockUser(AnonymousClass14.this.val$user.id);
                }
            }
        }

        /* renamed from: com.hanista.mobogram.ui.ContactsActivity.14.3 */
        class C15123 implements OnClickListener {
            C15123() {
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                ArrayList arrayList = new ArrayList();
                arrayList.add(AnonymousClass14.this.val$user);
                ContactsController.getInstance().deleteContact(arrayList);
            }
        }

        /* renamed from: com.hanista.mobogram.ui.ContactsActivity.14.4 */
        class C15134 implements DialogsActivityDelegate {
            C15134() {
            }

            public void didSelectDialog(DialogsActivity dialogsActivity, long j, boolean z) {
                Bundle bundle = new Bundle();
                bundle.putBoolean("scrollToTopOnResume", true);
                bundle.putInt("chat_id", -((int) j));
                if (MessagesController.checkCanOpenChat(bundle, dialogsActivity)) {
                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, new Object[ContactsActivity.search_button]);
                    MessagesController.getInstance().addUserToChat(-((int) j), AnonymousClass14.this.val$user, null, ContactsActivity.search_button, null, ContactsActivity.this);
                    ContactsActivity.this.presentFragment(new ChatActivity(bundle), true);
                }
            }
        }

        AnonymousClass14(User user, boolean z) {
            this.val$user = user;
            this.val$userBlocked = z;
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            Bundle bundle;
            BaseFragment dialogsActivity;
            if (i == 0) {
                bundle = new Bundle();
                bundle.putBoolean("onlySelect", true);
                bundle.putInt("dialogsType", ContactsActivity.add_button);
                bundle.putString("selectAlertString", LocaleController.getString("SendContactTo", C0338R.string.SendContactTo));
                bundle.putString("selectAlertStringGroup", LocaleController.getString("SendContactToGroup", C0338R.string.SendContactToGroup));
                dialogsActivity = new DialogsActivity(bundle);
                dialogsActivity.setDelegate(new C15101());
                ContactsActivity.this.presentFragment(dialogsActivity);
            } else if (i == ContactsActivity.add_button) {
                r0 = new Builder(ContactsActivity.this.getParentActivity());
                if (this.val$userBlocked) {
                    r0.setMessage(LocaleController.getString("AreYouSureUnblockContact", C0338R.string.AreYouSureUnblockContact));
                } else {
                    r0.setMessage(LocaleController.getString("AreYouSureBlockContact", C0338R.string.AreYouSureBlockContact));
                }
                r0.setTitle(LocaleController.getString("AppName", C0338R.string.AppName));
                r0.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), new C15112());
                r0.setNegativeButton(LocaleController.getString("Cancel", C0338R.string.Cancel), null);
                ContactsActivity.this.showDialog(r0.create());
            } else if (i == ContactsActivity.refresh) {
                bundle = new Bundle();
                bundle.putInt("user_id", this.val$user.id);
                ContactsActivity.this.presentFragment(new ContactAddActivity(bundle));
            } else if (i == ContactsActivity.delete) {
                r0 = new Builder(ContactsActivity.this.getParentActivity());
                r0.setMessage(LocaleController.getString("AreYouSureDeleteContact", C0338R.string.AreYouSureDeleteContact));
                r0.setTitle(LocaleController.getString("AppName", C0338R.string.AppName));
                r0.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), new C15123());
                r0.setNegativeButton(LocaleController.getString("Cancel", C0338R.string.Cancel), null);
                ContactsActivity.this.showDialog(r0.create());
            } else if (i == 4) {
                bundle = new Bundle();
                bundle.putBoolean("onlySelect", true);
                bundle.putInt("dialogsType", ContactsActivity.refresh);
                Object[] objArr = new Object[ContactsActivity.refresh];
                objArr[ContactsActivity.search_button] = UserObject.getUserName(this.val$user);
                objArr[ContactsActivity.add_button] = "%1$s";
                bundle.putString("addToGroupAlertString", LocaleController.formatString("AddToTheGroupTitle", C0338R.string.AddToTheGroupTitle, objArr));
                dialogsActivity = new DialogsActivity(bundle);
                dialogsActivity.setDelegate(new C15134());
                ContactsActivity.this.presentFragment(dialogsActivity);
            } else if (i == 5) {
                bundle = new Bundle();
                bundle.putInt("user_id", this.val$user.id);
                ContactsActivity.this.presentFragment(new SpecificContactActivity(bundle));
            } else if (i == 6) {
                try {
                    AndroidUtilities.installShortcut((long) this.val$user.id);
                } catch (Throwable e) {
                    FileLog.m18e("tmessages", e);
                }
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ContactsActivity.1 */
    class C15141 extends ActionBarMenuOnItemClick {
        C15141() {
        }

        public void onItemClick(int i) {
            if (i == -1) {
                ContactsActivity.this.finishFragment();
            } else if (i == ContactsActivity.add_button) {
                ContactsActivity.this.presentFragment(new NewContactActivity());
            } else if (i == ContactsActivity.refresh) {
                ContactsActivity.this.reloadOnlineList();
            } else if (i == ContactsActivity.delete) {
                ContactsActivity.this.doDeleteMultipleContacts();
            } else if (i == ContactsActivity.done_button) {
                ContactsActivity.this.didSelectMultiResult(new ArrayList(ContactsActivity.this.selectedContacts.keySet()), true, null);
            } else if (i == ContactsActivity.select_all_button) {
                ContactsActivity.this.selectAll();
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ContactsActivity.2 */
    class C15152 extends ActionBarMenuItemSearchListener {
        C15152() {
        }

        public void onSearchCollapse() {
            ContactsActivity.this.searchListViewAdapter.searchDialogs(null);
            ContactsActivity.this.searching = false;
            ContactsActivity.this.searchWas = false;
            ContactsActivity.this.listView.setAdapter(ContactsActivity.this.listViewAdapter);
            ContactsActivity.this.listViewAdapter.notifyDataSetChanged();
            ContactsActivity.this.listView.setFastScrollAlwaysVisible(true);
            ContactsActivity.this.listView.setFastScrollEnabled(true);
            ContactsActivity.this.listView.setVerticalScrollBarEnabled(false);
            ContactsActivity.this.emptyTextView.setText(LocaleController.getString("NoContacts", C0338R.string.NoContacts));
        }

        public void onSearchExpand() {
            ContactsActivity.this.searching = true;
        }

        public void onTextChanged(EditText editText) {
            if (ContactsActivity.this.searchListViewAdapter != null) {
                String obj = editText.getText().toString();
                if (obj.length() != 0) {
                    ContactsActivity.this.searchWas = true;
                    if (ContactsActivity.this.listView != null) {
                        ContactsActivity.this.listView.setAdapter(ContactsActivity.this.searchListViewAdapter);
                        ContactsActivity.this.searchListViewAdapter.notifyDataSetChanged();
                        ContactsActivity.this.listView.setFastScrollAlwaysVisible(false);
                        ContactsActivity.this.listView.setFastScrollEnabled(false);
                        ContactsActivity.this.listView.setVerticalScrollBarEnabled(true);
                    }
                    if (ContactsActivity.this.emptyTextView != null) {
                        ContactsActivity.this.emptyTextView.setText(LocaleController.getString("NoResult", C0338R.string.NoResult));
                    }
                }
                ContactsActivity.this.searchListViewAdapter.searchDialogs(obj);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ContactsActivity.3 */
    class C15163 implements OnTouchListener {
        C15163() {
        }

        public boolean onTouch(View view, MotionEvent motionEvent) {
            return true;
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ContactsActivity.4 */
    class C15184 implements OnItemClickListener {

        /* renamed from: com.hanista.mobogram.ui.ContactsActivity.4.1 */
        class C15171 implements OnClickListener {
            final /* synthetic */ String val$arg1;

            C15171(String str) {
                this.val$arg1 = str;
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                try {
                    Intent intent = new Intent("android.intent.action.VIEW", Uri.fromParts("sms", this.val$arg1, null));
                    intent.putExtra("sms_body", LocaleController.getString("InviteText", C0338R.string.InviteText));
                    ContactsActivity.this.getParentActivity().startActivityForResult(intent, 500);
                } catch (Throwable e) {
                    FileLog.m18e("tmessages", e);
                }
            }
        }

        C15184() {
        }

        public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
            User user;
            Bundle bundle;
            if (ContactsActivity.this.searching && ContactsActivity.this.searchWas) {
                user = (User) ContactsActivity.this.searchListViewAdapter.getItem(i);
                if (user != null) {
                    if (ContactsActivity.this.searchListViewAdapter.isGlobalSearch(i)) {
                        ArrayList arrayList = new ArrayList();
                        arrayList.add(user);
                        MessagesController.getInstance().putUsers(arrayList, false);
                        MessagesStorage.getInstance().putUsersAndChats(arrayList, null, false, true);
                    }
                    if (ContactsActivity.this.returnAsResult) {
                        if (ContactsActivity.this.ignoreUsers == null || !ContactsActivity.this.ignoreUsers.containsKey(Integer.valueOf(user.id))) {
                            ContactsActivity.this.didSelectResult(user, true, null);
                            return;
                        }
                        return;
                    } else if (!ContactsActivity.this.createSecretChat) {
                        bundle = new Bundle();
                        bundle.putInt("user_id", user.id);
                        if (MessagesController.checkCanOpenChat(bundle, ContactsActivity.this)) {
                            ContactsActivity.this.presentFragment(new ChatActivity(bundle), false);
                            return;
                        }
                        return;
                    } else if (user.id != UserConfig.getClientUserId()) {
                        ContactsActivity.this.creatingChat = true;
                        SecretChatHelper.getInstance().startSecretChat(ContactsActivity.this.getParentActivity(), user);
                        return;
                    } else {
                        return;
                    }
                }
                return;
            }
            int sectionForPosition = ContactsActivity.this.listViewAdapter.getSectionForPosition(i);
            int positionInSectionForPosition = ContactsActivity.this.listViewAdapter.getPositionInSectionForPosition(i);
            if (positionInSectionForPosition >= 0 && sectionForPosition >= 0) {
                if ((ContactsActivity.this.onlyUsers && ContactsActivity.this.chat_id == 0) || sectionForPosition != 0) {
                    Object item = ContactsActivity.this.listViewAdapter.getItem(sectionForPosition, positionInSectionForPosition);
                    if (item instanceof User) {
                        user = (User) item;
                        if (ContactsActivity.this.returnAsResult) {
                            if (ContactsActivity.this.ignoreUsers == null || !ContactsActivity.this.ignoreUsers.containsKey(Integer.valueOf(user.id))) {
                                ContactsActivity.this.didSelectResult(user, true, null);
                            }
                        } else if (ContactsActivity.this.createSecretChat) {
                            ContactsActivity.this.creatingChat = true;
                            SecretChatHelper.getInstance().startSecretChat(ContactsActivity.this.getParentActivity(), user);
                        } else {
                            bundle = new Bundle();
                            bundle.putInt("user_id", user.id);
                            if (MessagesController.checkCanOpenChat(bundle, ContactsActivity.this)) {
                                ContactsActivity.this.presentFragment(new ChatActivity(bundle), false);
                            }
                        }
                    } else if (item instanceof Contact) {
                        Contact contact = (Contact) item;
                        String str = !contact.phones.isEmpty() ? (String) contact.phones.get(ContactsActivity.search_button) : null;
                        if (str != null && ContactsActivity.this.getParentActivity() != null) {
                            Builder builder = new Builder(ContactsActivity.this.getParentActivity());
                            builder.setMessage(LocaleController.getString("InviteUser", C0338R.string.InviteUser));
                            builder.setTitle(LocaleController.getString("AppName", C0338R.string.AppName));
                            builder.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), new C15171(str));
                            builder.setNegativeButton(LocaleController.getString("Cancel", C0338R.string.Cancel), null);
                            ContactsActivity.this.showDialog(builder.create());
                        }
                    }
                } else if (ContactsActivity.this.needPhonebook) {
                    if (positionInSectionForPosition == 0) {
                        try {
                            Intent intent = new Intent("android.intent.action.SEND");
                            intent.setType("text/plain");
                            intent.putExtra("android.intent.extra.TEXT", ContactsController.getInstance().getInviteText());
                            ContactsActivity.this.getParentActivity().startActivityForResult(Intent.createChooser(intent, LocaleController.getString("InviteFriends", C0338R.string.InviteFriends)), 500);
                        } catch (Throwable e) {
                            FileLog.m18e("tmessages", e);
                        }
                    } else if (positionInSectionForPosition == ContactsActivity.add_button) {
                        ContactsActivity.this.presentFragment(new NewContactActivity());
                    }
                } else if (ContactsActivity.this.chat_id != 0) {
                    if (positionInSectionForPosition == 0) {
                        ContactsActivity.this.presentFragment(new GroupInviteActivity(ContactsActivity.this.chat_id));
                    } else if (positionInSectionForPosition == ContactsActivity.add_button) {
                        ContactsActivity.this.presentFragment(new NewContactActivity());
                    }
                } else if (positionInSectionForPosition == 0) {
                    if (MessagesController.isFeatureEnabled("chat_create", ContactsActivity.this)) {
                        ContactsActivity.this.presentFragment(new GroupCreateActivity(), false);
                    }
                } else if (positionInSectionForPosition == ContactsActivity.add_button) {
                    r0 = new Bundle();
                    r0.putBoolean("onlyUsers", true);
                    r0.putBoolean("destroyAfterSelect", true);
                    r0.putBoolean("createSecretChat", true);
                    r0.putBoolean("allowBots", false);
                    ContactsActivity.this.presentFragment(new ContactsActivity(r0), false);
                } else if (positionInSectionForPosition == ContactsActivity.refresh) {
                    if (MessagesController.isFeatureEnabled("broadcast_create", ContactsActivity.this)) {
                        SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", ContactsActivity.search_button);
                        if (sharedPreferences.getBoolean("channel_intro", false)) {
                            r0 = new Bundle();
                            r0.putInt("step", ContactsActivity.search_button);
                            ContactsActivity.this.presentFragment(new ChannelCreateActivity(r0));
                            return;
                        }
                        ContactsActivity.this.presentFragment(new ChannelIntroActivity());
                        sharedPreferences.edit().putBoolean("channel_intro", true).commit();
                    }
                } else if (positionInSectionForPosition == ContactsActivity.delete) {
                    ContactsActivity.this.presentFragment(new NewContactActivity());
                }
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ContactsActivity.5 */
    class C15195 implements OnScrollListener {
        C15195() {
        }

        public void onScroll(AbsListView absListView, int i, int i2, int i3) {
            if (absListView.isFastScrollEnabled()) {
                AndroidUtilities.clearDrawableAnimation(absListView);
            }
        }

        public void onScrollStateChanged(AbsListView absListView, int i) {
            if (i == ContactsActivity.add_button && ContactsActivity.this.searching && ContactsActivity.this.searchWas) {
                AndroidUtilities.hideKeyboard(ContactsActivity.this.getParentActivity().getCurrentFocus());
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ContactsActivity.6 */
    class C15206 implements OnItemLongClickListener {
        C15206() {
        }

        public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long j) {
            ContactsActivity.this.showContextMenu(i);
            return true;
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ContactsActivity.7 */
    class C15217 implements TextWatcher {
        final /* synthetic */ EditText val$editTextFinal;

        C15217(EditText editText) {
            this.val$editTextFinal = editText;
        }

        public void afterTextChanged(Editable editable) {
            try {
                String obj = editable.toString();
                if (obj.length() != 0) {
                    int intValue = Utilities.parseInt(obj).intValue();
                    if (intValue < 0) {
                        this.val$editTextFinal.setText("0");
                        this.val$editTextFinal.setSelection(this.val$editTextFinal.length());
                    } else if (intValue > 300) {
                        this.val$editTextFinal.setText("300");
                        this.val$editTextFinal.setSelection(this.val$editTextFinal.length());
                    } else if (!obj.equals(TtmlNode.ANONYMOUS_REGION_ID + intValue)) {
                        this.val$editTextFinal.setText(TtmlNode.ANONYMOUS_REGION_ID + intValue);
                        this.val$editTextFinal.setSelection(this.val$editTextFinal.length());
                    }
                }
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ContactsActivity.8 */
    class C15228 implements OnClickListener {
        final /* synthetic */ EditText val$finalEditText;
        final /* synthetic */ User val$user;

        C15228(User user, EditText editText) {
            this.val$user = user;
            this.val$finalEditText = editText;
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            ContactsActivity.this.didSelectResult(this.val$user, false, this.val$finalEditText != null ? this.val$finalEditText.getText().toString() : "0");
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ContactsActivity.9 */
    class C15239 implements GroupCreateActivityDelegate {
        C15239() {
        }

        public void didSelectUsers(ArrayList<Integer> arrayList) {
            ContactsActivity.this.showDeleteContactsConfirmation(arrayList);
        }
    }

    public ContactsActivity(Bundle bundle) {
        super(bundle);
        this.creatingChat = false;
        this.allowBots = true;
        this.needForwardCount = true;
        this.selectAlertString = null;
        this.allowUsernameSearch = true;
        this.selectedContacts = new HashMap();
    }

    private void didSelectMultiResult(List<Integer> list, boolean z, String str) {
        if (!z || this.selectAlertString == null) {
            if (this.multiSelectDelegate != null) {
                this.multiSelectDelegate.didSelectContacts(list, str);
                this.multiSelectDelegate = null;
            }
            finishFragment();
        } else if (getParentActivity() != null) {
            User user;
            String str2;
            Object[] objArr;
            CharSequence formatStringSimple;
            EditText editText;
            for (Integer user2 : list) {
                user = MessagesController.getInstance().getUser(user2);
                if (user.bot && user.bot_nochats) {
                    try {
                        Toast.makeText(getParentActivity(), LocaleController.getString("BotCantJoinGroups", C0338R.string.BotCantJoinGroups), search_button).show();
                        return;
                    } catch (Throwable e) {
                        FileLog.m18e("tmessages", e);
                        return;
                    }
                }
            }
            Builder builder = new Builder(getParentActivity());
            builder.setTitle(LocaleController.getString("AppName", C0338R.string.AppName));
            if (list.size() == add_button) {
                user = MessagesController.getInstance().getUser((Integer) list.get(search_button));
                str2 = this.selectAlertString;
                objArr = new Object[add_button];
                objArr[search_button] = UserObject.getUserName(user);
                formatStringSimple = LocaleController.formatStringSimple(str2, objArr);
            } else {
                String str3 = this.selectAlertString;
                Object[] objArr2 = new Object[add_button];
                objArr2[search_button] = list.size() + " " + LocaleController.getString("User", C0338R.string.User);
                formatStringSimple = LocaleController.formatStringSimple(str3, objArr2);
            }
            if (this.needForwardCount) {
                objArr = new Object[refresh];
                objArr[search_button] = formatStringSimple;
                objArr[add_button] = LocaleController.getString("AddToTheGroupForwardCount", C0338R.string.AddToTheGroupForwardCount);
                str2 = String.format("%s\n\n%s", objArr);
                View editText2 = new EditText(getParentActivity());
                editText2.setTextSize(18.0f);
                editText2.setText("50");
                editText2.setGravity(17);
                editText2.setInputType(refresh);
                editText2.setImeOptions(6);
                editText2.addTextChangedListener(new AnonymousClass12(editText2));
                builder.setView(editText2);
                View view = editText2;
                formatStringSimple = str2;
                editText = view;
            } else {
                editText = null;
            }
            builder.setMessage(formatStringSimple);
            builder.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), new AnonymousClass13(list, editText));
            builder.setNegativeButton(LocaleController.getString("Cancel", C0338R.string.Cancel), null);
            showDialog(builder.create());
            if (editText != null) {
                MarginLayoutParams marginLayoutParams = (MarginLayoutParams) editText.getLayoutParams();
                if (marginLayoutParams != null) {
                    if (marginLayoutParams instanceof LayoutParams) {
                        ((LayoutParams) marginLayoutParams).gravity = add_button;
                    }
                    int dp = AndroidUtilities.dp(10.0f);
                    marginLayoutParams.leftMargin = dp;
                    marginLayoutParams.rightMargin = dp;
                    editText.setLayoutParams(marginLayoutParams);
                }
                editText.setSelection(editText.getText().length());
            }
        }
    }

    private void didSelectResult(User user, boolean z, String str) {
        if (this.multiSelectMode) {
            selectUser(user);
        } else if (!z || this.selectAlertString == null) {
            if (this.delegate != null) {
                this.delegate.didSelectContact(user, str);
                this.delegate = null;
            }
            finishFragment();
        } else if (getParentActivity() == null) {
        } else {
            if (user.bot && user.bot_nochats) {
                try {
                    Toast.makeText(getParentActivity(), LocaleController.getString("BotCantJoinGroups", C0338R.string.BotCantJoinGroups), search_button).show();
                    return;
                } catch (Throwable e) {
                    FileLog.m18e("tmessages", e);
                    return;
                }
            }
            EditText editText;
            Builder builder = new Builder(getParentActivity());
            builder.setTitle(LocaleController.getString("AppName", C0338R.string.AppName));
            String str2 = this.selectAlertString;
            Object[] objArr = new Object[add_button];
            objArr[search_button] = UserObject.getUserName(user);
            CharSequence formatStringSimple = LocaleController.formatStringSimple(str2, objArr);
            if (user.bot || !this.needForwardCount) {
                editText = null;
            } else {
                Object[] objArr2 = new Object[refresh];
                objArr2[search_button] = formatStringSimple;
                objArr2[add_button] = LocaleController.getString("AddToTheGroupForwardCount", C0338R.string.AddToTheGroupForwardCount);
                String format = String.format("%s\n\n%s", objArr2);
                View editText2 = new EditText(getParentActivity());
                editText2.setTextSize(18.0f);
                editText2.setText("50");
                editText2.setGravity(17);
                editText2.setInputType(refresh);
                editText2.setImeOptions(6);
                editText2.addTextChangedListener(new C15217(editText2));
                builder.setView(editText2);
                View view = editText2;
                formatStringSimple = format;
                editText = view;
            }
            builder.setMessage(formatStringSimple);
            builder.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), new C15228(user, editText));
            builder.setNegativeButton(LocaleController.getString("Cancel", C0338R.string.Cancel), null);
            showDialog(builder.create());
            if (editText != null) {
                MarginLayoutParams marginLayoutParams = (MarginLayoutParams) editText.getLayoutParams();
                if (marginLayoutParams != null) {
                    if (marginLayoutParams instanceof LayoutParams) {
                        ((LayoutParams) marginLayoutParams).gravity = add_button;
                    }
                    int dp = AndroidUtilities.dp(10.0f);
                    marginLayoutParams.leftMargin = dp;
                    marginLayoutParams.rightMargin = dp;
                    editText.setLayoutParams(marginLayoutParams);
                }
                editText.setSelection(editText.getText().length());
            }
        }
    }

    private void doDeleteMultipleContacts() {
        Bundle bundle = new Bundle();
        bundle.putBoolean("isDelete", true);
        BaseFragment groupCreateActivity = new GroupCreateActivity(bundle);
        groupCreateActivity.setDelegate(new C15239());
        presentFragment(groupCreateActivity);
    }

    private void initTheme() {
        if (ThemeUtil.m2490b()) {
            int i = AdvanceTheme.bc;
            this.actionBar.setBackgroundColor(i);
            int i2 = AdvanceTheme.bd;
            if (i2 > 0) {
                Orientation orientation;
                switch (i2) {
                    case refresh /*2*/:
                        orientation = Orientation.LEFT_RIGHT;
                        break;
                    case delete /*3*/:
                        orientation = Orientation.TL_BR;
                        break;
                    case VideoPlayer.STATE_READY /*4*/:
                        orientation = Orientation.BL_TR;
                        break;
                    default:
                        orientation = Orientation.TOP_BOTTOM;
                        break;
                }
                int i3 = AdvanceTheme.be;
                int[] iArr = new int[refresh];
                iArr[search_button] = i;
                iArr[add_button] = i3;
                this.actionBar.setBackgroundDrawable(new GradientDrawable(orientation, iArr));
            }
            this.actionBar.setTitleColor(AdvanceTheme.bb);
            getParentActivity().getResources().getDrawable(C0338R.drawable.ic_ab_search).setColorFilter(AdvanceTheme.ba, Mode.MULTIPLY);
            getParentActivity().getResources().getDrawable(C0338R.drawable.ic_delete).setColorFilter(AdvanceTheme.ba, Mode.MULTIPLY);
            getParentActivity().getResources().getDrawable(C0338R.drawable.ic_mobo_refresh).setColorFilter(AdvanceTheme.ba, Mode.MULTIPLY);
            getParentActivity().getResources().getDrawable(C0338R.drawable.ic_select_all).setColorFilter(AdvanceTheme.ba, Mode.MULTIPLY);
            getParentActivity().getResources().getDrawable(C0338R.drawable.ic_done).setColorFilter(AdvanceTheme.ba, Mode.MULTIPLY);
            getParentActivity().getResources().getDrawable(C0338R.drawable.add).setColorFilter(AdvanceTheme.ba, Mode.MULTIPLY);
        }
    }

    private void initThemeListView() {
        if (ThemeUtil.m2490b()) {
            Glow.m521a(this.listView, AdvanceTheme.bc);
        }
    }

    private void initThemeSearchItem(ActionBarMenuItem actionBarMenuItem) {
        if (ThemeUtil.m2490b()) {
            actionBarMenuItem.getSearchField().setTextColor(AdvanceTheme.bb);
            Drawable drawable = getParentActivity().getResources().getDrawable(C0338R.drawable.ic_close_white);
            drawable.setColorFilter(AdvanceTheme.ba, Mode.MULTIPLY);
            actionBarMenuItem.getClearButton().setImageDrawable(drawable);
        }
    }

    private void reloadOnlineList() {
        if (this.onlyOnlines) {
            ContactsController.getInstance().initOnlineUsersSectionsDict();
            this.listViewAdapter = new OnlineContactsAdapter(ApplicationLoader.applicationContext, this.onlyUsers, false, this.ignoreUsers, this.chat_id != 0);
            this.listView.setAdapter(this.listViewAdapter);
        }
    }

    private void selectAll() {
        this.progressDialog = new ProgressDialog(getParentActivity());
        this.progressDialog.setMessage(LocaleController.getString("PleaseWait", C0338R.string.PleaseWait));
        this.progressDialog.setCanceledOnTouchOutside(false);
        this.progressDialog.setCancelable(false);
        this.progressDialog.show();
        new Handler().postDelayed(new Runnable() {
            public void run() {
                ContactsActivity.this.selectedContacts.clear();
                int i;
                User user;
                if (ContactsActivity.this.searching && ContactsActivity.this.searchWas) {
                    for (i = ContactsActivity.search_button; i < ContactsActivity.this.searchListViewAdapter.getCount(); i += ContactsActivity.add_button) {
                        user = (User) ContactsActivity.this.searchListViewAdapter.getItem(i);
                        if (user != null) {
                            ContactsActivity.this.selectedContacts.put(Integer.valueOf(user.id), null);
                        }
                    }
                } else {
                    for (i = ContactsActivity.search_button; i < ContactsActivity.this.listViewAdapter.getSectionCount(); i += ContactsActivity.add_button) {
                        for (int i2 = ContactsActivity.search_button; i2 < ContactsActivity.this.listViewAdapter.getCountForSection(i); i2 += ContactsActivity.add_button) {
                            user = (User) ContactsActivity.this.listViewAdapter.getItem(i, i2);
                            if (user != null) {
                                ContactsActivity.this.selectedContacts.put(Integer.valueOf(user.id), null);
                            }
                        }
                    }
                }
                if (ContactsActivity.this.searching || ContactsActivity.this.searchWas) {
                    ContactsActivity.this.searchListViewAdapter.searchDialogs(null);
                    ContactsActivity.this.searching = false;
                    ContactsActivity.this.searchWas = false;
                    ContactsActivity.this.listView.setAdapter(ContactsActivity.this.listViewAdapter);
                    ContactsActivity.this.listViewAdapter.notifyDataSetChanged();
                    if (VERSION.SDK_INT >= ContactsActivity.select_all_button) {
                        ContactsActivity.this.listView.setFastScrollAlwaysVisible(true);
                    }
                    ContactsActivity.this.listView.setFastScrollEnabled(true);
                    ContactsActivity.this.listView.setVerticalScrollBarEnabled(false);
                    ContactsActivity.this.emptyTextView.setText(LocaleController.getString("NoContacts", C0338R.string.NoContacts));
                }
                if (ContactsActivity.this.listViewAdapter != null) {
                    ContactsActivity.this.listViewAdapter.notifyDataSetChanged();
                }
                if (ContactsActivity.this.searchListViewAdapter != null) {
                    ContactsActivity.this.searchListViewAdapter.notifyDataSetChanged();
                }
                ContactsActivity.this.progressDialog.dismiss();
            }
        }, 500);
    }

    private void selectUser(User user) {
        if (this.selectedContacts.containsKey(Integer.valueOf(user.id))) {
            this.selectedContacts.remove(Integer.valueOf(user.id));
        } else {
            this.selectedContacts.put(Integer.valueOf(user.id), null);
        }
        if (this.listViewAdapter != null) {
            this.listViewAdapter.notifyDataSetChanged();
        }
        if (this.searchListViewAdapter != null) {
            this.searchListViewAdapter.notifyDataSetChanged();
        }
    }

    private void showContextMenu(int i) {
        User user;
        if (this.searching && this.searchWas) {
            user = (User) this.searchListViewAdapter.getItem(i);
        } else {
            int sectionForPosition = this.listViewAdapter.getSectionForPosition(i);
            int positionInSectionForPosition = this.listViewAdapter.getPositionInSectionForPosition(i);
            if (positionInSectionForPosition >= 0 && sectionForPosition >= 0) {
                Object item = this.listViewAdapter.getItem(sectionForPosition, positionInSectionForPosition);
                if (item instanceof User) {
                    user = (User) item;
                } else {
                    return;
                }
            }
            return;
        }
        if (user != null) {
            BottomSheet.Builder builder = new BottomSheet.Builder(getParentActivity());
            boolean contains = MessagesController.getInstance().blockedUsers.contains(Integer.valueOf(user.id));
            CharSequence[] charSequenceArr = new CharSequence[7];
            charSequenceArr[search_button] = LocaleController.getString("ShareContact", C0338R.string.ShareContact);
            charSequenceArr[add_button] = !contains ? LocaleController.getString("BlockContact", C0338R.string.BlockContact) : LocaleController.getString("Unblock", C0338R.string.Unblock);
            charSequenceArr[refresh] = LocaleController.getString("EditContact", C0338R.string.EditContact);
            charSequenceArr[delete] = LocaleController.getString("DeleteContact", C0338R.string.DeleteContact);
            charSequenceArr[4] = LocaleController.getString("AddToGroupOrChannel", C0338R.string.AddToGroupOrChannel);
            charSequenceArr[5] = LocaleController.getString("AddToSpecificContacts", C0338R.string.AddToSpecificContacts);
            charSequenceArr[6] = LocaleController.getString("AddShortcut", C0338R.string.AddShortcut);
            builder.setTitle(UserObject.getUserName(user));
            builder.setItems(charSequenceArr, new AnonymousClass14(user, contains));
            showDialog(builder.create());
        }
    }

    private void showDeleteContactsConfirmation(ArrayList<Integer> arrayList) {
        Builder builder = new Builder(getParentActivity());
        builder.setMessage(LocaleController.getString("AreYouSureDeleteContacts", C0338R.string.AreYouSureDeleteContacts));
        builder.setTitle(LocaleController.getString("AppName", C0338R.string.AppName));
        builder.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), new AnonymousClass10(arrayList));
        builder.setNegativeButton(LocaleController.getString("Cancel", C0338R.string.Cancel), null);
        showDialog(builder.create());
    }

    private void showMaterialHelp() {
        View view = null;
        if (!this.onlyOnlines) {
            try {
                View view2;
                if (this.listView != null) {
                    int childCount = this.listView.getChildCount();
                    for (int i = search_button; i < childCount; i += add_button) {
                        View childAt = this.listView.getChildAt(i);
                        if (childAt instanceof UserCell) {
                            view2 = (UserCell) childAt;
                            break;
                        }
                    }
                }
                view2 = null;
                Activity parentActivity = getParentActivity();
                View item = this.actionBar.createMenu().getItem(delete);
                View item2 = this.actionBar.createMenu().getItem(add_button);
                View view3 = view2 != null ? view2.avatarImageView : null;
                if (!(view2 == null || view2.mutualImageView == null || view2.mutualImageView.getVisibility() != 0)) {
                    view = view2.mutualImageView;
                }
                MaterialHelperUtil.m1365a(parentActivity, item, item2, view3, view, view2);
            } catch (Exception e) {
            }
        }
    }

    private void updateVisibleRows(int i) {
        if (this.listView != null) {
            int childCount = this.listView.getChildCount();
            for (int i2 = search_button; i2 < childCount; i2 += add_button) {
                View childAt = this.listView.getChildAt(i2);
                if (childAt instanceof UserCell) {
                    ((UserCell) childAt).update(i);
                }
            }
        }
    }

    public View createView(Context context) {
        int i = add_button;
        this.searching = false;
        this.searchWas = false;
        if (ThemeUtil.m2490b()) {
            Drawable drawable = getParentActivity().getResources().getDrawable(C0338R.drawable.ic_ab_back);
            drawable.setColorFilter(AdvanceTheme.ba, Mode.MULTIPLY);
            this.actionBar.setBackButtonDrawable(drawable);
        } else {
            this.actionBar.setBackButtonImage(C0338R.drawable.ic_ab_back);
        }
        this.actionBar.setAllowOverlayTitle(true);
        if (this.destroyAfterSelect) {
            if (this.returnAsResult) {
                this.actionBar.setTitle(LocaleController.getString("SelectContact", C0338R.string.SelectContact));
            } else if (this.createSecretChat) {
                this.actionBar.setTitle(LocaleController.getString("NewSecretChat", C0338R.string.NewSecretChat));
            } else {
                this.actionBar.setTitle(LocaleController.getString("NewMessageTitle", C0338R.string.NewMessageTitle));
            }
        } else if (this.onlyOnlines) {
            this.actionBar.setTitle(LocaleController.getString("OnlineContacts", C0338R.string.OnlineContacts));
        } else {
            this.actionBar.setTitle(LocaleController.getString("Contacts", C0338R.string.Contacts));
        }
        this.actionBar.setActionBarMenuOnItemClick(new C15141());
        ActionBarMenu createMenu = this.actionBar.createMenu();
        ActionBarMenuItem actionBarMenuItemSearchListener = createMenu.addItem((int) search_button, (int) C0338R.drawable.ic_ab_search).setIsSearchField(true).setActionBarMenuItemSearchListener(new C15152());
        actionBarMenuItemSearchListener.getSearchField().setHint(LocaleController.getString("Search", C0338R.string.Search));
        createMenu.addItem((int) add_button, (int) C0338R.drawable.add);
        initThemeSearchItem(actionBarMenuItemSearchListener);
        if (this.onlyOnlines) {
            createMenu.addItem((int) refresh, (int) C0338R.drawable.ic_mobo_refresh);
        } else if (!this.multiSelectMode) {
            createMenu.addItem((int) delete, (int) C0338R.drawable.ic_delete);
        }
        if (this.multiSelectMode) {
            createMenu.addItemWithWidth((int) select_all_button, (int) C0338R.drawable.ic_select_all, AndroidUtilities.dp(56.0f));
            createMenu.addItemWithWidth((int) done_button, (int) C0338R.drawable.ic_done, AndroidUtilities.dp(56.0f));
        }
        this.searchListViewAdapter = new SearchAdapter(context, this.ignoreUsers, this.allowUsernameSearch, false, false, this.allowBots);
        if (this.multiSelectMode) {
            this.searchListViewAdapter.setUseUserCell(true);
            this.searchListViewAdapter.setCheckedMap(this.selectedContacts);
        }
        if (this.onlyOnlines) {
            ContactsController.getInstance().initOnlineUsersSectionsDict();
            this.listViewAdapter = new OnlineContactsAdapter(context, this.onlyUsers, false, this.ignoreUsers, this.chat_id != 0);
        } else {
            this.listViewAdapter = new ContactsAdapter(context, this.onlyUsers ? add_button : search_button, this.needPhonebook, this.ignoreUsers, this.chat_id != 0);
            if (this.multiSelectMode) {
                ((ContactsAdapter) this.listViewAdapter).setCheckedMap(this.selectedContacts);
            }
        }
        this.fragmentView = new FrameLayout(context);
        View linearLayout = new LinearLayout(context);
        if (ThemeUtil.m2490b()) {
            linearLayout.setBackgroundColor(AdvanceTheme.aU);
        }
        linearLayout.setVisibility(4);
        linearLayout.setOrientation(add_button);
        ((FrameLayout) this.fragmentView).addView(linearLayout);
        LayoutParams layoutParams = (LayoutParams) linearLayout.getLayoutParams();
        layoutParams.width = -1;
        layoutParams.height = -1;
        layoutParams.gravity = 48;
        linearLayout.setLayoutParams(layoutParams);
        linearLayout.setOnTouchListener(new C15163());
        this.emptyTextView = new TextView(context);
        this.emptyTextView.setTextColor(-8355712);
        if (ThemeUtil.m2490b()) {
            this.emptyTextView.setTextColor(AdvanceTheme.aR);
        }
        this.emptyTextView.setTextSize(add_button, 20.0f);
        this.emptyTextView.setGravity(17);
        this.emptyTextView.setText(LocaleController.getString("NoContacts", C0338R.string.NoContacts));
        linearLayout.addView(this.emptyTextView);
        LinearLayout.LayoutParams layoutParams2 = (LinearLayout.LayoutParams) this.emptyTextView.getLayoutParams();
        layoutParams2.width = -1;
        layoutParams2.height = -1;
        layoutParams2.weight = 0.5f;
        this.emptyTextView.setLayoutParams(layoutParams2);
        View frameLayout = new FrameLayout(context);
        linearLayout.addView(frameLayout);
        layoutParams2 = (LinearLayout.LayoutParams) frameLayout.getLayoutParams();
        layoutParams2.width = -1;
        layoutParams2.height = -1;
        layoutParams2.weight = 0.5f;
        frameLayout.setLayoutParams(layoutParams2);
        this.listView = new LetterSectionsListView(context);
        initThemeListView();
        this.listView.setEmptyView(linearLayout);
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setDivider(null);
        this.listView.setDividerHeight(search_button);
        this.listView.setFastScrollEnabled(true);
        this.listView.setScrollBarStyle(33554432);
        this.listView.setAdapter(this.listViewAdapter);
        this.listView.setFastScrollAlwaysVisible(true);
        LetterSectionsListView letterSectionsListView = this.listView;
        if (!LocaleController.isRTL) {
            i = refresh;
        }
        letterSectionsListView.setVerticalScrollbarPosition(i);
        ((FrameLayout) this.fragmentView).addView(this.listView);
        layoutParams = (LayoutParams) this.listView.getLayoutParams();
        layoutParams.width = -1;
        layoutParams.height = -1;
        this.listView.setLayoutParams(layoutParams);
        this.listView.setOnItemClickListener(new C15184());
        this.listView.setOnScrollListener(new C15195());
        this.listView.setOnItemLongClickListener(new C15206());
        return this.fragmentView;
    }

    public void didReceivedNotification(int i, Object... objArr) {
        if (i == NotificationCenter.contactsDidLoaded) {
            if (this.listViewAdapter != null) {
                this.listViewAdapter.notifyDataSetChanged();
                reloadOnlineList();
            }
        } else if (i == NotificationCenter.updateInterfaces) {
            int intValue = ((Integer) objArr[search_button]).intValue();
            if ((intValue & 4) == 0 || this.onlyOnlines) {
            }
            if ((intValue & refresh) != 0 || (intValue & add_button) != 0 || (intValue & 4) != 0) {
                updateVisibleRows(intValue);
            }
        } else if (i == NotificationCenter.encryptedChatCreated) {
            if (this.createSecretChat && this.creatingChat) {
                EncryptedChat encryptedChat = (EncryptedChat) objArr[search_button];
                Bundle bundle = new Bundle();
                bundle.putInt("enc_id", encryptedChat.id);
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, new Object[search_button]);
                presentFragment(new ChatActivity(bundle), true);
            }
        } else if (i == NotificationCenter.closeChats && !this.creatingChat) {
            removeSelfFromStack();
        }
    }

    protected void onBecomeFullyVisible() {
        showMaterialHelp();
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.contactsDidLoaded);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.updateInterfaces);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.encryptedChatCreated);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.closeChats);
        if (this.arguments != null) {
            this.onlyUsers = getArguments().getBoolean("onlyUsers", false);
            this.onlyOnlines = getArguments().getBoolean("onlyOnlines", false);
            this.destroyAfterSelect = this.arguments.getBoolean("destroyAfterSelect", false);
            this.returnAsResult = this.arguments.getBoolean("returnAsResult", false);
            this.createSecretChat = this.arguments.getBoolean("createSecretChat", false);
            this.selectAlertString = this.arguments.getString("selectAlertString");
            this.allowUsernameSearch = this.arguments.getBoolean("allowUsernameSearch", true);
            this.needForwardCount = this.arguments.getBoolean("needForwardCount", true);
            this.allowBots = this.arguments.getBoolean("allowBots", true);
            this.chat_id = this.arguments.getInt("chat_id", search_button);
            this.multiSelectMode = this.arguments.getBoolean("multiSelectMode", false);
        } else {
            this.needPhonebook = true;
        }
        ContactsController.getInstance().checkInviteText();
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.contactsDidLoaded);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.updateInterfaces);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.encryptedChatCreated);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.closeChats);
        this.delegate = null;
    }

    public void onPause() {
        super.onPause();
        if (this.actionBar != null) {
            this.actionBar.closeSearchField();
        }
    }

    public void onResume() {
        super.onResume();
        if (this.listViewAdapter != null) {
            this.listViewAdapter.notifyDataSetChanged();
        }
        initTheme();
    }

    public void setDelegate(ContactsActivityDelegate contactsActivityDelegate) {
        this.delegate = contactsActivityDelegate;
    }

    public void setIgnoreUsers(HashMap<Integer, User> hashMap) {
        this.ignoreUsers = hashMap;
    }

    public void setMultiSelectDelegate(ContactsActivityMultiSelectDelegate contactsActivityMultiSelectDelegate) {
        this.multiSelectDelegate = contactsActivityMultiSelectDelegate;
    }
}
