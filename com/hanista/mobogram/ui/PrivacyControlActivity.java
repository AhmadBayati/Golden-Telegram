package com.hanista.mobogram.ui;

import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.ApplicationLoader;
import com.hanista.mobogram.messenger.ContactsController;
import com.hanista.mobogram.messenger.FileLog;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.MessagesController;
import com.hanista.mobogram.messenger.NotificationCenter;
import com.hanista.mobogram.messenger.NotificationCenter.NotificationCenterDelegate;
import com.hanista.mobogram.mobo.p020s.AdvanceTheme;
import com.hanista.mobogram.mobo.p020s.ThemeUtil;
import com.hanista.mobogram.tgnet.ConnectionsManager;
import com.hanista.mobogram.tgnet.RequestDelegate;
import com.hanista.mobogram.tgnet.TLObject;
import com.hanista.mobogram.tgnet.TLRPC.InputUser;
import com.hanista.mobogram.tgnet.TLRPC.PrivacyRule;
import com.hanista.mobogram.tgnet.TLRPC.TL_account_privacyRules;
import com.hanista.mobogram.tgnet.TLRPC.TL_account_setPrivacy;
import com.hanista.mobogram.tgnet.TLRPC.TL_error;
import com.hanista.mobogram.tgnet.TLRPC.TL_inputPrivacyKeyChatInvite;
import com.hanista.mobogram.tgnet.TLRPC.TL_inputPrivacyKeyStatusTimestamp;
import com.hanista.mobogram.tgnet.TLRPC.TL_inputPrivacyValueAllowAll;
import com.hanista.mobogram.tgnet.TLRPC.TL_inputPrivacyValueAllowContacts;
import com.hanista.mobogram.tgnet.TLRPC.TL_inputPrivacyValueAllowUsers;
import com.hanista.mobogram.tgnet.TLRPC.TL_inputPrivacyValueDisallowAll;
import com.hanista.mobogram.tgnet.TLRPC.TL_inputPrivacyValueDisallowUsers;
import com.hanista.mobogram.tgnet.TLRPC.TL_privacyValueAllowAll;
import com.hanista.mobogram.tgnet.TLRPC.TL_privacyValueAllowUsers;
import com.hanista.mobogram.tgnet.TLRPC.TL_privacyValueDisallowAll;
import com.hanista.mobogram.tgnet.TLRPC.TL_privacyValueDisallowUsers;
import com.hanista.mobogram.tgnet.TLRPC.User;
import com.hanista.mobogram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import com.hanista.mobogram.ui.ActionBar.BaseFragment;
import com.hanista.mobogram.ui.ActionBar.Theme;
import com.hanista.mobogram.ui.Adapters.BaseFragmentAdapter;
import com.hanista.mobogram.ui.Cells.HeaderCell;
import com.hanista.mobogram.ui.Cells.RadioCell;
import com.hanista.mobogram.ui.Cells.TextInfoPrivacyCell;
import com.hanista.mobogram.ui.Cells.TextSettingsCell;
import com.hanista.mobogram.ui.GroupCreateActivity.GroupCreateActivityDelegate;
import com.hanista.mobogram.ui.PrivacyUsersActivity.PrivacyActivityDelegate;
import java.util.ArrayList;

public class PrivacyControlActivity extends BaseFragment implements NotificationCenterDelegate {
    private static final int done_button = 1;
    private int alwaysShareRow;
    private int bgColor;
    private ArrayList<Integer> currentMinus;
    private ArrayList<Integer> currentPlus;
    private int currentType;
    private int detailRow;
    private View doneButton;
    private boolean enableAnimation;
    private int everybodyRow;
    private boolean isGroup;
    private int lastCheckedType;
    private ListAdapter listAdapter;
    private int myContactsRow;
    private int neverShareRow;
    private int nobodyRow;
    private int rowCount;
    private int sectionRow;
    private int shareDetailRow;
    private int shareSectionRow;

    /* renamed from: com.hanista.mobogram.ui.PrivacyControlActivity.1 */
    class C18071 extends ActionBarMenuOnItemClick {

        /* renamed from: com.hanista.mobogram.ui.PrivacyControlActivity.1.1 */
        class C18061 implements OnClickListener {
            final /* synthetic */ SharedPreferences val$preferences;

            C18061(SharedPreferences sharedPreferences) {
                this.val$preferences = sharedPreferences;
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                PrivacyControlActivity.this.applyCurrentPrivacySettings();
                this.val$preferences.edit().putBoolean("privacyAlertShowed", true).commit();
            }
        }

        C18071() {
        }

        public void onItemClick(int i) {
            if (i == -1) {
                PrivacyControlActivity.this.finishFragment();
            } else if (i == PrivacyControlActivity.done_button && PrivacyControlActivity.this.getParentActivity() != null) {
                if (!(PrivacyControlActivity.this.currentType == 0 || PrivacyControlActivity.this.isGroup)) {
                    SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
                    if (!sharedPreferences.getBoolean("privacyAlertShowed", false)) {
                        Builder builder = new Builder(PrivacyControlActivity.this.getParentActivity());
                        if (PrivacyControlActivity.this.isGroup) {
                            builder.setMessage(LocaleController.getString("WhoCanAddMeInfo", C0338R.string.WhoCanAddMeInfo));
                        } else {
                            builder.setMessage(LocaleController.getString("CustomHelp", C0338R.string.CustomHelp));
                        }
                        builder.setTitle(LocaleController.getString("AppName", C0338R.string.AppName));
                        builder.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), new C18061(sharedPreferences));
                        builder.setNegativeButton(LocaleController.getString("Cancel", C0338R.string.Cancel), null);
                        PrivacyControlActivity.this.showDialog(builder.create());
                        return;
                    }
                }
                PrivacyControlActivity.this.applyCurrentPrivacySettings();
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.PrivacyControlActivity.2 */
    class C18102 implements OnItemClickListener {

        /* renamed from: com.hanista.mobogram.ui.PrivacyControlActivity.2.1 */
        class C18081 implements GroupCreateActivityDelegate {
            final /* synthetic */ int val$i;

            C18081(int i) {
                this.val$i = i;
            }

            public void didSelectUsers(ArrayList<Integer> arrayList) {
                int i;
                if (this.val$i == PrivacyControlActivity.this.neverShareRow) {
                    PrivacyControlActivity.this.currentMinus = arrayList;
                    for (i = 0; i < PrivacyControlActivity.this.currentMinus.size(); i += PrivacyControlActivity.done_button) {
                        PrivacyControlActivity.this.currentPlus.remove(PrivacyControlActivity.this.currentMinus.get(i));
                    }
                } else {
                    PrivacyControlActivity.this.currentPlus = arrayList;
                    for (i = 0; i < PrivacyControlActivity.this.currentPlus.size(); i += PrivacyControlActivity.done_button) {
                        PrivacyControlActivity.this.currentMinus.remove(PrivacyControlActivity.this.currentPlus.get(i));
                    }
                }
                PrivacyControlActivity.this.doneButton.setVisibility(0);
                PrivacyControlActivity.this.lastCheckedType = -1;
                PrivacyControlActivity.this.listAdapter.notifyDataSetChanged();
            }
        }

        /* renamed from: com.hanista.mobogram.ui.PrivacyControlActivity.2.2 */
        class C18092 implements PrivacyActivityDelegate {
            final /* synthetic */ int val$i;

            C18092(int i) {
                this.val$i = i;
            }

            public void didUpdatedUserList(ArrayList<Integer> arrayList, boolean z) {
                int i;
                if (this.val$i == PrivacyControlActivity.this.neverShareRow) {
                    PrivacyControlActivity.this.currentMinus = arrayList;
                    if (z) {
                        for (i = 0; i < PrivacyControlActivity.this.currentMinus.size(); i += PrivacyControlActivity.done_button) {
                            PrivacyControlActivity.this.currentPlus.remove(PrivacyControlActivity.this.currentMinus.get(i));
                        }
                    }
                } else {
                    PrivacyControlActivity.this.currentPlus = arrayList;
                    if (z) {
                        for (i = 0; i < PrivacyControlActivity.this.currentPlus.size(); i += PrivacyControlActivity.done_button) {
                            PrivacyControlActivity.this.currentMinus.remove(PrivacyControlActivity.this.currentPlus.get(i));
                        }
                    }
                }
                PrivacyControlActivity.this.doneButton.setVisibility(0);
                PrivacyControlActivity.this.listAdapter.notifyDataSetChanged();
            }
        }

        C18102() {
        }

        public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
            boolean z = true;
            if (i == PrivacyControlActivity.this.nobodyRow || i == PrivacyControlActivity.this.everybodyRow || i == PrivacyControlActivity.this.myContactsRow) {
                int access$000 = PrivacyControlActivity.this.currentType;
                if (i == PrivacyControlActivity.this.nobodyRow) {
                    access$000 = PrivacyControlActivity.done_button;
                } else if (i == PrivacyControlActivity.this.everybodyRow) {
                    access$000 = 0;
                } else if (i == PrivacyControlActivity.this.myContactsRow) {
                    access$000 = 2;
                }
                if (access$000 != PrivacyControlActivity.this.currentType) {
                    PrivacyControlActivity.this.enableAnimation = true;
                    PrivacyControlActivity.this.doneButton.setVisibility(0);
                    PrivacyControlActivity.this.lastCheckedType = PrivacyControlActivity.this.currentType;
                    PrivacyControlActivity.this.currentType = access$000;
                    PrivacyControlActivity.this.updateRows();
                }
            } else if (i == PrivacyControlActivity.this.neverShareRow || i == PrivacyControlActivity.this.alwaysShareRow) {
                ArrayList access$1200 = i == PrivacyControlActivity.this.neverShareRow ? PrivacyControlActivity.this.currentMinus : PrivacyControlActivity.this.currentPlus;
                if (access$1200.isEmpty()) {
                    Bundle bundle = new Bundle();
                    bundle.putBoolean(i == PrivacyControlActivity.this.neverShareRow ? "isNeverShare" : "isAlwaysShare", true);
                    bundle.putBoolean("isGroup", PrivacyControlActivity.this.isGroup);
                    BaseFragment groupCreateActivity = new GroupCreateActivity(bundle);
                    groupCreateActivity.setDelegate(new C18081(i));
                    PrivacyControlActivity.this.presentFragment(groupCreateActivity);
                    return;
                }
                boolean access$100 = PrivacyControlActivity.this.isGroup;
                if (i != PrivacyControlActivity.this.alwaysShareRow) {
                    z = false;
                }
                BaseFragment privacyUsersActivity = new PrivacyUsersActivity(access$1200, access$100, z);
                privacyUsersActivity.setDelegate(new C18092(i));
                PrivacyControlActivity.this.presentFragment(privacyUsersActivity);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.PrivacyControlActivity.3 */
    class C18123 implements RequestDelegate {
        final /* synthetic */ ProgressDialog val$progressDialogFinal;

        /* renamed from: com.hanista.mobogram.ui.PrivacyControlActivity.3.1 */
        class C18111 implements Runnable {
            final /* synthetic */ TL_error val$error;
            final /* synthetic */ TLObject val$response;

            C18111(TL_error tL_error, TLObject tLObject) {
                this.val$error = tL_error;
                this.val$response = tLObject;
            }

            public void run() {
                try {
                    if (C18123.this.val$progressDialogFinal != null) {
                        C18123.this.val$progressDialogFinal.dismiss();
                    }
                } catch (Throwable e) {
                    FileLog.m18e("tmessages", e);
                }
                if (this.val$error == null) {
                    PrivacyControlActivity.this.finishFragment();
                    TL_account_privacyRules tL_account_privacyRules = (TL_account_privacyRules) this.val$response;
                    MessagesController.getInstance().putUsers(tL_account_privacyRules.users, false);
                    ContactsController.getInstance().setPrivacyRules(tL_account_privacyRules.rules, PrivacyControlActivity.this.isGroup);
                    return;
                }
                PrivacyControlActivity.this.showErrorAlert();
            }
        }

        C18123(ProgressDialog progressDialog) {
            this.val$progressDialogFinal = progressDialog;
        }

        public void run(TLObject tLObject, TL_error tL_error) {
            AndroidUtilities.runOnUIThread(new C18111(tL_error, tLObject));
        }
    }

    private static class LinkMovementMethodMy extends LinkMovementMethod {
        private LinkMovementMethodMy() {
        }

        public boolean onTouchEvent(TextView textView, Spannable spannable, MotionEvent motionEvent) {
            try {
                return super.onTouchEvent(textView, spannable, motionEvent);
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
                return false;
            }
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
            return PrivacyControlActivity.this.rowCount;
        }

        public Object getItem(int i) {
            return null;
        }

        public long getItemId(int i) {
            return (long) i;
        }

        public int getItemViewType(int i) {
            return (i == PrivacyControlActivity.this.alwaysShareRow || i == PrivacyControlActivity.this.neverShareRow) ? 0 : (i == PrivacyControlActivity.this.shareDetailRow || i == PrivacyControlActivity.this.detailRow) ? PrivacyControlActivity.done_button : (i == PrivacyControlActivity.this.sectionRow || i == PrivacyControlActivity.this.shareSectionRow) ? 2 : (i == PrivacyControlActivity.this.everybodyRow || i == PrivacyControlActivity.this.myContactsRow || i == PrivacyControlActivity.this.nobodyRow) ? 3 : 0;
        }

        public View getView(int i, View view, ViewGroup viewGroup) {
            boolean z = true;
            int itemViewType = getItemViewType(i);
            View textSettingsCell;
            if (itemViewType == 0) {
                if (view == null) {
                    textSettingsCell = new TextSettingsCell(this.mContext);
                    textSettingsCell.setBackgroundColor(-1);
                    if (ThemeUtil.m2490b()) {
                        textSettingsCell.setBackgroundColor(PrivacyControlActivity.this.bgColor);
                    }
                } else {
                    textSettingsCell = view;
                }
                TextSettingsCell textSettingsCell2 = (TextSettingsCell) textSettingsCell;
                String formatPluralString;
                if (i == PrivacyControlActivity.this.alwaysShareRow) {
                    formatPluralString = PrivacyControlActivity.this.currentPlus.size() != 0 ? LocaleController.formatPluralString("Users", PrivacyControlActivity.this.currentPlus.size()) : LocaleController.getString("EmpryUsersPlaceholder", C0338R.string.EmpryUsersPlaceholder);
                    String string;
                    if (PrivacyControlActivity.this.isGroup) {
                        string = LocaleController.getString("AlwaysAllow", C0338R.string.AlwaysAllow);
                        if (PrivacyControlActivity.this.neverShareRow == -1) {
                            z = false;
                        }
                        textSettingsCell2.setTextAndValue(string, formatPluralString, z);
                        return textSettingsCell;
                    }
                    string = LocaleController.getString("AlwaysShareWith", C0338R.string.AlwaysShareWith);
                    if (PrivacyControlActivity.this.neverShareRow == -1) {
                        z = false;
                    }
                    textSettingsCell2.setTextAndValue(string, formatPluralString, z);
                    return textSettingsCell;
                } else if (i != PrivacyControlActivity.this.neverShareRow) {
                    return textSettingsCell;
                } else {
                    formatPluralString = PrivacyControlActivity.this.currentMinus.size() != 0 ? LocaleController.formatPluralString("Users", PrivacyControlActivity.this.currentMinus.size()) : LocaleController.getString("EmpryUsersPlaceholder", C0338R.string.EmpryUsersPlaceholder);
                    if (PrivacyControlActivity.this.isGroup) {
                        textSettingsCell2.setTextAndValue(LocaleController.getString("NeverAllow", C0338R.string.NeverAllow), formatPluralString, false);
                        return textSettingsCell;
                    }
                    textSettingsCell2.setTextAndValue(LocaleController.getString("NeverShareWith", C0338R.string.NeverShareWith), formatPluralString, false);
                    return textSettingsCell;
                }
            } else if (itemViewType == PrivacyControlActivity.done_button) {
                if (view == null) {
                    textSettingsCell = new TextInfoPrivacyCell(this.mContext);
                    textSettingsCell.setBackgroundColor(-1);
                    if (ThemeUtil.m2490b()) {
                        textSettingsCell.setBackgroundColor(PrivacyControlActivity.this.bgColor);
                    }
                } else {
                    textSettingsCell = view;
                }
                if (i == PrivacyControlActivity.this.detailRow) {
                    if (PrivacyControlActivity.this.isGroup) {
                        ((TextInfoPrivacyCell) textSettingsCell).setText(LocaleController.getString("WhoCanAddMeInfo", C0338R.string.WhoCanAddMeInfo));
                    } else {
                        ((TextInfoPrivacyCell) textSettingsCell).setText(LocaleController.getString("CustomHelp", C0338R.string.CustomHelp));
                    }
                    textSettingsCell.setBackgroundResource(C0338R.drawable.greydivider);
                    return textSettingsCell;
                } else if (i != PrivacyControlActivity.this.shareDetailRow) {
                    return textSettingsCell;
                } else {
                    if (PrivacyControlActivity.this.isGroup) {
                        ((TextInfoPrivacyCell) textSettingsCell).setText(LocaleController.getString("CustomShareInfo", C0338R.string.CustomShareInfo));
                    } else {
                        ((TextInfoPrivacyCell) textSettingsCell).setText(LocaleController.getString("CustomShareSettingsHelp", C0338R.string.CustomShareSettingsHelp));
                    }
                    textSettingsCell.setBackgroundResource(C0338R.drawable.greydivider_bottom);
                    return textSettingsCell;
                }
            } else if (itemViewType == 2) {
                if (view == null) {
                    textSettingsCell = new HeaderCell(this.mContext);
                    textSettingsCell.setBackgroundColor(-1);
                    if (ThemeUtil.m2490b()) {
                        textSettingsCell.setBackgroundColor(PrivacyControlActivity.this.bgColor);
                    }
                } else {
                    textSettingsCell = view;
                }
                if (i == PrivacyControlActivity.this.sectionRow) {
                    if (PrivacyControlActivity.this.isGroup) {
                        ((HeaderCell) textSettingsCell).setText(LocaleController.getString("WhoCanAddMe", C0338R.string.WhoCanAddMe));
                        return textSettingsCell;
                    }
                    ((HeaderCell) textSettingsCell).setText(LocaleController.getString("LastSeenTitle", C0338R.string.LastSeenTitle));
                    return textSettingsCell;
                } else if (i != PrivacyControlActivity.this.shareSectionRow) {
                    return textSettingsCell;
                } else {
                    ((HeaderCell) textSettingsCell).setText(LocaleController.getString("AddExceptions", C0338R.string.AddExceptions));
                    return textSettingsCell;
                }
            } else if (itemViewType != 3) {
                return view;
            } else {
                boolean z2;
                if (view == null) {
                    textSettingsCell = new RadioCell(this.mContext);
                    textSettingsCell.setBackgroundColor(-1);
                    if (ThemeUtil.m2490b()) {
                        textSettingsCell.setBackgroundColor(PrivacyControlActivity.this.bgColor);
                    }
                } else {
                    textSettingsCell = view;
                }
                RadioCell radioCell = (RadioCell) textSettingsCell;
                radioCell.setTag("Pref");
                if (i == PrivacyControlActivity.this.everybodyRow) {
                    radioCell.setText(LocaleController.getString("LastSeenEverybody", C0338R.string.LastSeenEverybody), PrivacyControlActivity.this.lastCheckedType == 0, true);
                    z2 = false;
                } else if (i == PrivacyControlActivity.this.myContactsRow) {
                    radioCell.setText(LocaleController.getString("LastSeenContacts", C0338R.string.LastSeenContacts), PrivacyControlActivity.this.lastCheckedType == 2, PrivacyControlActivity.this.nobodyRow != -1);
                    z2 = true;
                } else if (i == PrivacyControlActivity.this.nobodyRow) {
                    radioCell.setText(LocaleController.getString("LastSeenNobody", C0338R.string.LastSeenNobody), PrivacyControlActivity.this.lastCheckedType == PrivacyControlActivity.done_button, false);
                    z2 = true;
                } else {
                    z2 = false;
                }
                if (PrivacyControlActivity.this.lastCheckedType == z2) {
                    radioCell.setChecked(false, PrivacyControlActivity.this.enableAnimation);
                    return textSettingsCell;
                } else if (PrivacyControlActivity.this.currentType != z2) {
                    return textSettingsCell;
                } else {
                    radioCell.setChecked(true, PrivacyControlActivity.this.enableAnimation);
                    return textSettingsCell;
                }
            }
        }

        public int getViewTypeCount() {
            return 4;
        }

        public boolean hasStableIds() {
            return false;
        }

        public boolean isEmpty() {
            return false;
        }

        public boolean isEnabled(int i) {
            return i == PrivacyControlActivity.this.nobodyRow || i == PrivacyControlActivity.this.everybodyRow || i == PrivacyControlActivity.this.myContactsRow || i == PrivacyControlActivity.this.neverShareRow || i == PrivacyControlActivity.this.alwaysShareRow;
        }
    }

    public PrivacyControlActivity(boolean z) {
        this.currentType = 0;
        this.lastCheckedType = -1;
        this.isGroup = z;
    }

    private void applyCurrentPrivacySettings() {
        int i;
        User user;
        InputUser inputUser;
        TLObject tL_account_setPrivacy = new TL_account_setPrivacy();
        if (this.isGroup) {
            tL_account_setPrivacy.key = new TL_inputPrivacyKeyChatInvite();
        } else {
            tL_account_setPrivacy.key = new TL_inputPrivacyKeyStatusTimestamp();
        }
        if (this.currentType != 0 && this.currentPlus.size() > 0) {
            TL_inputPrivacyValueAllowUsers tL_inputPrivacyValueAllowUsers = new TL_inputPrivacyValueAllowUsers();
            for (i = 0; i < this.currentPlus.size(); i += done_button) {
                user = MessagesController.getInstance().getUser((Integer) this.currentPlus.get(i));
                if (user != null) {
                    inputUser = MessagesController.getInputUser(user);
                    if (inputUser != null) {
                        tL_inputPrivacyValueAllowUsers.users.add(inputUser);
                    }
                }
            }
            tL_account_setPrivacy.rules.add(tL_inputPrivacyValueAllowUsers);
        }
        if (this.currentType != done_button && this.currentMinus.size() > 0) {
            TL_inputPrivacyValueDisallowUsers tL_inputPrivacyValueDisallowUsers = new TL_inputPrivacyValueDisallowUsers();
            for (i = 0; i < this.currentMinus.size(); i += done_button) {
                user = MessagesController.getInstance().getUser((Integer) this.currentMinus.get(i));
                if (user != null) {
                    inputUser = MessagesController.getInputUser(user);
                    if (inputUser != null) {
                        tL_inputPrivacyValueDisallowUsers.users.add(inputUser);
                    }
                }
            }
            tL_account_setPrivacy.rules.add(tL_inputPrivacyValueDisallowUsers);
        }
        if (this.currentType == 0) {
            tL_account_setPrivacy.rules.add(new TL_inputPrivacyValueAllowAll());
        } else if (this.currentType == done_button) {
            tL_account_setPrivacy.rules.add(new TL_inputPrivacyValueDisallowAll());
        } else if (this.currentType == 2) {
            tL_account_setPrivacy.rules.add(new TL_inputPrivacyValueAllowContacts());
        }
        ProgressDialog progressDialog = null;
        if (getParentActivity() != null) {
            progressDialog = new ProgressDialog(getParentActivity());
            progressDialog.setMessage(LocaleController.getString("Loading", C0338R.string.Loading));
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }
        ConnectionsManager.getInstance().sendRequest(tL_account_setPrivacy, new C18123(progressDialog), 2);
    }

    private void checkPrivacy() {
        this.currentPlus = new ArrayList();
        this.currentMinus = new ArrayList();
        ArrayList privacyRules = ContactsController.getInstance().getPrivacyRules(this.isGroup);
        if (privacyRules.size() == 0) {
            this.currentType = done_button;
            return;
        }
        int i = -1;
        for (int i2 = 0; i2 < privacyRules.size(); i2 += done_button) {
            PrivacyRule privacyRule = (PrivacyRule) privacyRules.get(i2);
            if (privacyRule instanceof TL_privacyValueAllowUsers) {
                this.currentPlus.addAll(privacyRule.users);
            } else if (privacyRule instanceof TL_privacyValueDisallowUsers) {
                this.currentMinus.addAll(privacyRule.users);
            } else {
                i = privacyRule instanceof TL_privacyValueAllowAll ? 0 : privacyRule instanceof TL_privacyValueDisallowAll ? done_button : 2;
            }
        }
        if (i == 0 || (i == -1 && this.currentMinus.size() > 0)) {
            this.currentType = 0;
        } else if (i == 2 || (i == -1 && this.currentMinus.size() > 0 && this.currentPlus.size() > 0)) {
            this.currentType = 2;
        } else if (i == done_button || (i == -1 && this.currentPlus.size() > 0)) {
            this.currentType = done_button;
        }
        if (this.doneButton != null) {
            this.doneButton.setVisibility(8);
        }
        updateRows();
    }

    private void showErrorAlert() {
        if (getParentActivity() != null) {
            Builder builder = new Builder(getParentActivity());
            builder.setTitle(LocaleController.getString("AppName", C0338R.string.AppName));
            builder.setMessage(LocaleController.getString("PrivacyFloodControlError", C0338R.string.PrivacyFloodControlError));
            builder.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), null);
            showDialog(builder.create());
        }
    }

    private void updateRows() {
        this.rowCount = 0;
        int i = this.rowCount;
        this.rowCount = i + done_button;
        this.sectionRow = i;
        i = this.rowCount;
        this.rowCount = i + done_button;
        this.everybodyRow = i;
        i = this.rowCount;
        this.rowCount = i + done_button;
        this.myContactsRow = i;
        if (this.isGroup) {
            this.nobodyRow = -1;
        } else {
            i = this.rowCount;
            this.rowCount = i + done_button;
            this.nobodyRow = i;
        }
        i = this.rowCount;
        this.rowCount = i + done_button;
        this.detailRow = i;
        i = this.rowCount;
        this.rowCount = i + done_button;
        this.shareSectionRow = i;
        if (this.currentType == done_button || this.currentType == 2) {
            i = this.rowCount;
            this.rowCount = i + done_button;
            this.alwaysShareRow = i;
        } else {
            this.alwaysShareRow = -1;
        }
        if (this.currentType == 0 || this.currentType == 2) {
            i = this.rowCount;
            this.rowCount = i + done_button;
            this.neverShareRow = i;
        } else {
            this.neverShareRow = -1;
        }
        i = this.rowCount;
        this.rowCount = i + done_button;
        this.shareDetailRow = i;
        if (this.listAdapter != null) {
            this.listAdapter.notifyDataSetChanged();
        }
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(C0338R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        if (this.isGroup) {
            this.actionBar.setTitle(LocaleController.getString("GroupsAndChannels", C0338R.string.GroupsAndChannels));
        } else {
            this.actionBar.setTitle(LocaleController.getString("PrivacyLastSeen", C0338R.string.PrivacyLastSeen));
        }
        this.actionBar.setActionBarMenuOnItemClick(new C18071());
        this.doneButton = this.actionBar.createMenu().addItemWithWidth((int) done_button, (int) C0338R.drawable.ic_done, AndroidUtilities.dp(56.0f));
        this.doneButton.setVisibility(8);
        this.listAdapter = new ListAdapter(context);
        this.bgColor = AdvanceTheme.f2497h;
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        frameLayout.setBackgroundColor(Theme.ACTION_BAR_MODE_SELECTOR_COLOR);
        View listView = new ListView(context);
        if (ThemeUtil.m2490b()) {
            listView.setBackgroundColor(this.bgColor);
        }
        listView.setDivider(null);
        listView.setDividerHeight(0);
        listView.setVerticalScrollBarEnabled(false);
        listView.setDrawSelectorOnTop(true);
        frameLayout.addView(listView);
        LayoutParams layoutParams = (LayoutParams) listView.getLayoutParams();
        layoutParams.width = -1;
        layoutParams.height = -1;
        layoutParams.gravity = 48;
        listView.setLayoutParams(layoutParams);
        listView.setAdapter(this.listAdapter);
        listView.setOnItemClickListener(new C18102());
        initThemeActionBar();
        return this.fragmentView;
    }

    public void didReceivedNotification(int i, Object... objArr) {
        if (i == NotificationCenter.privacyRulesUpdated) {
            checkPrivacy();
        }
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        checkPrivacy();
        updateRows();
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.privacyRulesUpdated);
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.privacyRulesUpdated);
    }

    public void onResume() {
        super.onResume();
        this.lastCheckedType = -1;
        this.enableAnimation = false;
    }
}
