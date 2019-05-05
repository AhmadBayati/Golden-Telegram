package com.hanista.mobogram.ui;

import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.support.v4.os.EnvironmentCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ListView;
import com.google.android.gms.vision.face.Face;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.ApplicationLoader;
import com.hanista.mobogram.messenger.ContactsController;
import com.hanista.mobogram.messenger.FileLog;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.MessagesController;
import com.hanista.mobogram.messenger.NotificationCenter;
import com.hanista.mobogram.messenger.NotificationCenter.NotificationCenterDelegate;
import com.hanista.mobogram.messenger.UserConfig;
import com.hanista.mobogram.mobo.lock.LockActivity;
import com.hanista.mobogram.mobo.p003d.Glow;
import com.hanista.mobogram.mobo.p020s.AdvanceTheme;
import com.hanista.mobogram.mobo.p020s.ThemeUtil;
import com.hanista.mobogram.tgnet.ConnectionsManager;
import com.hanista.mobogram.tgnet.RequestDelegate;
import com.hanista.mobogram.tgnet.TLObject;
import com.hanista.mobogram.tgnet.TLRPC.PrivacyRule;
import com.hanista.mobogram.tgnet.TLRPC.TL_accountDaysTTL;
import com.hanista.mobogram.tgnet.TLRPC.TL_account_setAccountTTL;
import com.hanista.mobogram.tgnet.TLRPC.TL_boolTrue;
import com.hanista.mobogram.tgnet.TLRPC.TL_error;
import com.hanista.mobogram.tgnet.TLRPC.TL_privacyValueAllowAll;
import com.hanista.mobogram.tgnet.TLRPC.TL_privacyValueAllowUsers;
import com.hanista.mobogram.tgnet.TLRPC.TL_privacyValueDisallowAll;
import com.hanista.mobogram.tgnet.TLRPC.TL_privacyValueDisallowUsers;
import com.hanista.mobogram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import com.hanista.mobogram.ui.ActionBar.BaseFragment;
import com.hanista.mobogram.ui.ActionBar.Theme;
import com.hanista.mobogram.ui.Adapters.BaseFragmentAdapter;
import com.hanista.mobogram.ui.Cells.HeaderCell;
import com.hanista.mobogram.ui.Cells.TextCheckCell;
import com.hanista.mobogram.ui.Cells.TextInfoPrivacyCell;
import com.hanista.mobogram.ui.Cells.TextSettingsCell;
import com.hanista.mobogram.ui.Components.LayoutHelper;
import java.util.ArrayList;

public class PrivacySettingsActivity extends BaseFragment implements NotificationCenterDelegate {
    private int blockedRow;
    private int deleteAccountDetailRow;
    private int deleteAccountRow;
    private int deleteAccountSectionRow;
    private int groupsDetailRow;
    private int groupsRow;
    private int lastSeenRow;
    private ListAdapter listAdapter;
    private int passcodeRow;
    private int passwordRow;
    private int privacySectionRow;
    private int rowCount;
    private int secretDetailRow;
    private int secretSectionRow;
    private int secretWebpageRow;
    private int securitySectionRow;
    private int sessionsDetailRow;
    private int sessionsRow;

    /* renamed from: com.hanista.mobogram.ui.PrivacySettingsActivity.1 */
    class C18131 extends ActionBarMenuOnItemClick {
        C18131() {
        }

        public void onItemClick(int i) {
            if (i == -1) {
                PrivacySettingsActivity.this.finishFragment();
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.PrivacySettingsActivity.2 */
    class C18172 implements OnItemClickListener {

        /* renamed from: com.hanista.mobogram.ui.PrivacySettingsActivity.2.1 */
        class C18161 implements OnClickListener {

            /* renamed from: com.hanista.mobogram.ui.PrivacySettingsActivity.2.1.1 */
            class C18151 implements RequestDelegate {
                final /* synthetic */ ProgressDialog val$progressDialog;
                final /* synthetic */ TL_account_setAccountTTL val$req;

                /* renamed from: com.hanista.mobogram.ui.PrivacySettingsActivity.2.1.1.1 */
                class C18141 implements Runnable {
                    final /* synthetic */ TLObject val$response;

                    C18141(TLObject tLObject) {
                        this.val$response = tLObject;
                    }

                    public void run() {
                        try {
                            C18151.this.val$progressDialog.dismiss();
                        } catch (Throwable e) {
                            FileLog.m18e("tmessages", e);
                        }
                        if (this.val$response instanceof TL_boolTrue) {
                            ContactsController.getInstance().setDeleteAccountTTL(C18151.this.val$req.ttl.days);
                            PrivacySettingsActivity.this.listAdapter.notifyDataSetChanged();
                        }
                    }
                }

                C18151(ProgressDialog progressDialog, TL_account_setAccountTTL tL_account_setAccountTTL) {
                    this.val$progressDialog = progressDialog;
                    this.val$req = tL_account_setAccountTTL;
                }

                public void run(TLObject tLObject, TL_error tL_error) {
                    AndroidUtilities.runOnUIThread(new C18141(tLObject));
                }
            }

            C18161() {
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                int i2 = i == 0 ? 30 : i == 1 ? 90 : i == 2 ? 182 : i == 3 ? 365 : 0;
                ProgressDialog progressDialog = new ProgressDialog(PrivacySettingsActivity.this.getParentActivity());
                progressDialog.setMessage(LocaleController.getString("Loading", C0338R.string.Loading));
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.setCancelable(false);
                progressDialog.show();
                TLObject tL_account_setAccountTTL = new TL_account_setAccountTTL();
                tL_account_setAccountTTL.ttl = new TL_accountDaysTTL();
                tL_account_setAccountTTL.ttl.days = i2;
                ConnectionsManager.getInstance().sendRequest(tL_account_setAccountTTL, new C18151(progressDialog, tL_account_setAccountTTL));
            }
        }

        C18172() {
        }

        public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
            boolean z = true;
            if (i == PrivacySettingsActivity.this.blockedRow) {
                PrivacySettingsActivity.this.presentFragment(new BlockedUsersActivity());
            } else if (i == PrivacySettingsActivity.this.sessionsRow) {
                PrivacySettingsActivity.this.presentFragment(new SessionsActivity());
            } else if (i == PrivacySettingsActivity.this.deleteAccountRow) {
                if (PrivacySettingsActivity.this.getParentActivity() != null) {
                    Builder builder = new Builder(PrivacySettingsActivity.this.getParentActivity());
                    builder.setTitle(LocaleController.getString("DeleteAccountTitle", C0338R.string.DeleteAccountTitle));
                    builder.setItems(new CharSequence[]{LocaleController.formatPluralString("Months", 1), LocaleController.formatPluralString("Months", 3), LocaleController.formatPluralString("Months", 6), LocaleController.formatPluralString("Years", 1)}, new C18161());
                    builder.setNegativeButton(LocaleController.getString("Cancel", C0338R.string.Cancel), null);
                    PrivacySettingsActivity.this.showDialog(builder.create());
                }
            } else if (i == PrivacySettingsActivity.this.lastSeenRow) {
                PrivacySettingsActivity.this.presentFragment(new PrivacyControlActivity(false));
            } else if (i == PrivacySettingsActivity.this.groupsRow) {
                PrivacySettingsActivity.this.presentFragment(new PrivacyControlActivity(true));
            } else if (i == PrivacySettingsActivity.this.passwordRow) {
                PrivacySettingsActivity.this.presentFragment(new TwoStepVerificationActivity(0));
            } else if (i == PrivacySettingsActivity.this.passcodeRow) {
                if (UserConfig.passcodeHash.length() <= 0) {
                    PrivacySettingsActivity.this.presentFragment(new PasscodeActivity(0));
                } else if (UserConfig.passcodeType == 2) {
                    PrivacySettingsActivity.this.presentFragment(new LockActivity(2));
                } else {
                    PrivacySettingsActivity.this.presentFragment(new PasscodeActivity(2));
                }
            } else if (i == PrivacySettingsActivity.this.secretWebpageRow) {
                if (MessagesController.getInstance().secretWebpagePreview == 1) {
                    MessagesController.getInstance().secretWebpagePreview = 0;
                } else {
                    MessagesController.getInstance().secretWebpagePreview = 1;
                }
                ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit().putInt("secretWebpage2", MessagesController.getInstance().secretWebpagePreview).commit();
                if (view instanceof TextCheckCell) {
                    TextCheckCell textCheckCell = (TextCheckCell) view;
                    if (MessagesController.getInstance().secretWebpagePreview != 1) {
                        z = false;
                    }
                    textCheckCell.setChecked(z);
                }
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
            return PrivacySettingsActivity.this.rowCount;
        }

        public Object getItem(int i) {
            return null;
        }

        public long getItemId(int i) {
            return (long) i;
        }

        public int getItemViewType(int i) {
            return (i == PrivacySettingsActivity.this.lastSeenRow || i == PrivacySettingsActivity.this.blockedRow || i == PrivacySettingsActivity.this.deleteAccountRow || i == PrivacySettingsActivity.this.sessionsRow || i == PrivacySettingsActivity.this.passwordRow || i == PrivacySettingsActivity.this.passcodeRow || i == PrivacySettingsActivity.this.groupsRow) ? 0 : (i == PrivacySettingsActivity.this.deleteAccountDetailRow || i == PrivacySettingsActivity.this.groupsDetailRow || i == PrivacySettingsActivity.this.sessionsDetailRow || i == PrivacySettingsActivity.this.secretDetailRow) ? 1 : (i == PrivacySettingsActivity.this.securitySectionRow || i == PrivacySettingsActivity.this.deleteAccountSectionRow || i == PrivacySettingsActivity.this.privacySectionRow || i == PrivacySettingsActivity.this.secretSectionRow) ? 2 : i == PrivacySettingsActivity.this.secretWebpageRow ? 3 : 0;
        }

        public View getView(int i, View view, ViewGroup viewGroup) {
            int itemViewType = getItemViewType(i);
            View textSettingsCell;
            if (itemViewType == 0) {
                if (view == null) {
                    textSettingsCell = new TextSettingsCell(this.mContext);
                    textSettingsCell.setBackgroundColor(-1);
                } else {
                    textSettingsCell = view;
                }
                TextSettingsCell textSettingsCell2 = (TextSettingsCell) textSettingsCell;
                if (i == PrivacySettingsActivity.this.blockedRow) {
                    textSettingsCell2.setText(LocaleController.getString("BlockedUsers", C0338R.string.BlockedUsers), true);
                    return textSettingsCell;
                } else if (i == PrivacySettingsActivity.this.sessionsRow) {
                    textSettingsCell2.setText(LocaleController.getString("SessionsTitle", C0338R.string.SessionsTitle), false);
                    return textSettingsCell;
                } else if (i == PrivacySettingsActivity.this.passwordRow) {
                    textSettingsCell2.setText(LocaleController.getString("TwoStepVerification", C0338R.string.TwoStepVerification), true);
                    return textSettingsCell;
                } else if (i == PrivacySettingsActivity.this.passcodeRow) {
                    textSettingsCell2.setText(LocaleController.getString("PasscodeOrPatternLock", C0338R.string.PasscodeOrPatternLock), true);
                    return textSettingsCell;
                } else if (i == PrivacySettingsActivity.this.lastSeenRow) {
                    textSettingsCell2.setTextAndValue(LocaleController.getString("PrivacyLastSeen", C0338R.string.PrivacyLastSeen), ContactsController.getInstance().getLoadingLastSeenInfo() ? LocaleController.getString("Loading", C0338R.string.Loading) : PrivacySettingsActivity.this.formatRulesString(false), true);
                    return textSettingsCell;
                } else if (i == PrivacySettingsActivity.this.groupsRow) {
                    textSettingsCell2.setTextAndValue(LocaleController.getString("GroupsAndChannels", C0338R.string.GroupsAndChannels), ContactsController.getInstance().getLoadingGroupInfo() ? LocaleController.getString("Loading", C0338R.string.Loading) : PrivacySettingsActivity.this.formatRulesString(true), false);
                    return textSettingsCell;
                } else if (i != PrivacySettingsActivity.this.deleteAccountRow) {
                    return textSettingsCell;
                } else {
                    String string;
                    if (ContactsController.getInstance().getLoadingDeleteInfo()) {
                        string = LocaleController.getString("Loading", C0338R.string.Loading);
                    } else {
                        int deleteAccountTTL = ContactsController.getInstance().getDeleteAccountTTL();
                        string = deleteAccountTTL <= 182 ? LocaleController.formatPluralString("Months", deleteAccountTTL / 30) : deleteAccountTTL == 365 ? LocaleController.formatPluralString("Years", deleteAccountTTL / 365) : LocaleController.formatPluralString("Days", deleteAccountTTL);
                    }
                    textSettingsCell2.setTextAndValue(LocaleController.getString("DeleteAccountIfAwayFor", C0338R.string.DeleteAccountIfAwayFor), string, false);
                    return textSettingsCell;
                }
            } else if (itemViewType == 1) {
                textSettingsCell = view == null ? new TextInfoPrivacyCell(this.mContext) : view;
                if (i == PrivacySettingsActivity.this.deleteAccountDetailRow) {
                    ((TextInfoPrivacyCell) textSettingsCell).setText(LocaleController.getString("DeleteAccountHelp", C0338R.string.DeleteAccountHelp));
                    textSettingsCell.setBackgroundResource(PrivacySettingsActivity.this.secretSectionRow == -1 ? C0338R.drawable.greydivider_bottom : C0338R.drawable.greydivider);
                    return textSettingsCell;
                } else if (i == PrivacySettingsActivity.this.groupsDetailRow) {
                    ((TextInfoPrivacyCell) textSettingsCell).setText(LocaleController.getString("GroupsAndChannelsHelp", C0338R.string.GroupsAndChannelsHelp));
                    textSettingsCell.setBackgroundResource(C0338R.drawable.greydivider);
                    return textSettingsCell;
                } else if (i == PrivacySettingsActivity.this.sessionsDetailRow) {
                    ((TextInfoPrivacyCell) textSettingsCell).setText(LocaleController.getString("SessionsInfo", C0338R.string.SessionsInfo));
                    textSettingsCell.setBackgroundResource(C0338R.drawable.greydivider);
                    return textSettingsCell;
                } else if (i != PrivacySettingsActivity.this.secretDetailRow) {
                    return textSettingsCell;
                } else {
                    ((TextInfoPrivacyCell) textSettingsCell).setText(TtmlNode.ANONYMOUS_REGION_ID);
                    textSettingsCell.setBackgroundResource(C0338R.drawable.greydivider_bottom);
                    return textSettingsCell;
                }
            } else if (itemViewType == 2) {
                if (view == null) {
                    textSettingsCell = new HeaderCell(this.mContext);
                    textSettingsCell.setBackgroundColor(-1);
                } else {
                    textSettingsCell = view;
                }
                if (i == PrivacySettingsActivity.this.privacySectionRow) {
                    ((HeaderCell) textSettingsCell).setText(LocaleController.getString("PrivacyTitle", C0338R.string.PrivacyTitle));
                    return textSettingsCell;
                } else if (i == PrivacySettingsActivity.this.securitySectionRow) {
                    ((HeaderCell) textSettingsCell).setText(LocaleController.getString("SecurityTitle", C0338R.string.SecurityTitle));
                    return textSettingsCell;
                } else if (i == PrivacySettingsActivity.this.deleteAccountSectionRow) {
                    ((HeaderCell) textSettingsCell).setText(LocaleController.getString("DeleteAccountTitle", C0338R.string.DeleteAccountTitle));
                    return textSettingsCell;
                } else if (i != PrivacySettingsActivity.this.secretSectionRow) {
                    return textSettingsCell;
                } else {
                    ((HeaderCell) textSettingsCell).setText(LocaleController.getString("SecretChat", C0338R.string.SecretChat));
                    return textSettingsCell;
                }
            } else if (itemViewType != 3) {
                return view;
            } else {
                if (view == null) {
                    textSettingsCell = new TextCheckCell(this.mContext);
                    textSettingsCell.setBackgroundColor(-1);
                } else {
                    textSettingsCell = view;
                }
                TextCheckCell textCheckCell = (TextCheckCell) textSettingsCell;
                if (i != PrivacySettingsActivity.this.secretWebpageRow) {
                    return textSettingsCell;
                }
                textCheckCell.setTextAndCheck(LocaleController.getString("SecretWebPage", C0338R.string.SecretWebPage), MessagesController.getInstance().secretWebpagePreview == 1, true);
                return textSettingsCell;
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
            return i == PrivacySettingsActivity.this.passcodeRow || i == PrivacySettingsActivity.this.passwordRow || i == PrivacySettingsActivity.this.blockedRow || i == PrivacySettingsActivity.this.sessionsRow || i == PrivacySettingsActivity.this.secretWebpageRow || ((i == PrivacySettingsActivity.this.groupsRow && !ContactsController.getInstance().getLoadingGroupInfo()) || ((i == PrivacySettingsActivity.this.lastSeenRow && !ContactsController.getInstance().getLoadingLastSeenInfo()) || (i == PrivacySettingsActivity.this.deleteAccountRow && !ContactsController.getInstance().getLoadingDeleteInfo())));
        }
    }

    private String formatRulesString(boolean z) {
        ArrayList privacyRules = ContactsController.getInstance().getPrivacyRules(z);
        if (privacyRules.size() == 0) {
            return LocaleController.getString("LastSeenNobody", C0338R.string.LastSeenNobody);
        }
        int i = 0;
        int i2 = 0;
        int i3 = -1;
        for (int i4 = 0; i4 < privacyRules.size(); i4++) {
            PrivacyRule privacyRule = (PrivacyRule) privacyRules.get(i4);
            if (privacyRule instanceof TL_privacyValueAllowUsers) {
                i2 += privacyRule.users.size();
            } else if (privacyRule instanceof TL_privacyValueDisallowUsers) {
                i += privacyRule.users.size();
            } else {
                i3 = privacyRule instanceof TL_privacyValueAllowAll ? 0 : privacyRule instanceof TL_privacyValueDisallowAll ? 1 : 2;
            }
        }
        if (i3 == 0 || (i3 == -1 && i > 0)) {
            if (i == 0) {
                return LocaleController.getString("LastSeenEverybody", C0338R.string.LastSeenEverybody);
            }
            return LocaleController.formatString("LastSeenEverybodyMinus", C0338R.string.LastSeenEverybodyMinus, Integer.valueOf(i));
        } else if (i3 == 2 || (i3 == -1 && i > 0 && i2 > 0)) {
            if (i2 == 0 && i == 0) {
                return LocaleController.getString("LastSeenContacts", C0338R.string.LastSeenContacts);
            }
            if (i2 != 0 && i != 0) {
                return LocaleController.formatString("LastSeenContactsMinusPlus", C0338R.string.LastSeenContactsMinusPlus, Integer.valueOf(i), Integer.valueOf(i2));
            } else if (i != 0) {
                return LocaleController.formatString("LastSeenContactsMinus", C0338R.string.LastSeenContactsMinus, Integer.valueOf(i));
            } else {
                return LocaleController.formatString("LastSeenContactsPlus", C0338R.string.LastSeenContactsPlus, Integer.valueOf(i2));
            }
        } else if (i3 != 1 && i2 <= 0) {
            return EnvironmentCompat.MEDIA_UNKNOWN;
        } else {
            if (i2 == 0) {
                return LocaleController.getString("LastSeenNobody", C0338R.string.LastSeenNobody);
            }
            return LocaleController.formatString("LastSeenNobodyPlus", C0338R.string.LastSeenNobodyPlus, Integer.valueOf(i2));
        }
    }

    private void initThemeListView(Context context) {
        if (ThemeUtil.m2490b()) {
            int i = AdvanceTheme.f2500k;
            int i2 = AdvanceTheme.f2497h;
            AbsListView listView = new ListView(context);
            Glow.m521a(listView, i);
            listView.setBackgroundColor(i2);
        }
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(C0338R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("PrivacySettings", C0338R.string.PrivacySettings));
        this.actionBar.setActionBarMenuOnItemClick(new C18131());
        this.listAdapter = new ListAdapter(context);
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        frameLayout.setBackgroundColor(Theme.ACTION_BAR_MODE_SELECTOR_COLOR);
        View listView = new ListView(context);
        initThemeListView(context);
        listView.setDivider(null);
        listView.setDividerHeight(0);
        listView.setVerticalScrollBarEnabled(false);
        listView.setDrawSelectorOnTop(true);
        frameLayout.addView(listView, LayoutHelper.createFrame(-1, Face.UNCOMPUTED_PROBABILITY));
        listView.setAdapter(this.listAdapter);
        listView.setOnItemClickListener(new C18172());
        initThemeActionBar();
        return this.fragmentView;
    }

    public void didReceivedNotification(int i, Object... objArr) {
        if (i == NotificationCenter.privacyRulesUpdated && this.listAdapter != null) {
            this.listAdapter.notifyDataSetChanged();
        }
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        ContactsController.getInstance().loadPrivacySettings();
        this.rowCount = 0;
        int i = this.rowCount;
        this.rowCount = i + 1;
        this.privacySectionRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.blockedRow = i;
        if (UserConfig.isRobot) {
            this.lastSeenRow = -1;
        } else {
            i = this.rowCount;
            this.rowCount = i + 1;
            this.lastSeenRow = i;
        }
        i = this.rowCount;
        this.rowCount = i + 1;
        this.groupsRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.groupsDetailRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.securitySectionRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.passcodeRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.passwordRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.sessionsRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.sessionsDetailRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.deleteAccountSectionRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.deleteAccountRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.deleteAccountDetailRow = i;
        if (MessagesController.getInstance().secretWebpagePreview != 1) {
            i = this.rowCount;
            this.rowCount = i + 1;
            this.secretSectionRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.secretWebpageRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.secretDetailRow = i;
        } else {
            this.secretSectionRow = -1;
            this.secretWebpageRow = -1;
            this.secretDetailRow = -1;
        }
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.privacyRulesUpdated);
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.privacyRulesUpdated);
    }

    public void onResume() {
        super.onResume();
        if (this.listAdapter != null) {
            this.listAdapter.notifyDataSetChanged();
        }
    }
}
