package com.hanista.mobogram.ui;

import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.FileLog;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.MessagesController;
import com.hanista.mobogram.messenger.NotificationCenter;
import com.hanista.mobogram.messenger.NotificationCenter.NotificationCenterDelegate;
import com.hanista.mobogram.messenger.UserConfig;
import com.hanista.mobogram.mobo.p008i.FontUtil;
import com.hanista.mobogram.mobo.p020s.AdvanceTheme;
import com.hanista.mobogram.mobo.p020s.ThemeUtil;
import com.hanista.mobogram.tgnet.ConnectionsManager;
import com.hanista.mobogram.tgnet.RequestDelegate;
import com.hanista.mobogram.tgnet.TLObject;
import com.hanista.mobogram.tgnet.TLRPC.TL_account_authorizations;
import com.hanista.mobogram.tgnet.TLRPC.TL_account_getAuthorizations;
import com.hanista.mobogram.tgnet.TLRPC.TL_account_resetAuthorization;
import com.hanista.mobogram.tgnet.TLRPC.TL_auth_resetAuthorizations;
import com.hanista.mobogram.tgnet.TLRPC.TL_authorization;
import com.hanista.mobogram.tgnet.TLRPC.TL_boolTrue;
import com.hanista.mobogram.tgnet.TLRPC.TL_error;
import com.hanista.mobogram.ui.ActionBar.ActionBar;
import com.hanista.mobogram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import com.hanista.mobogram.ui.ActionBar.BaseFragment;
import com.hanista.mobogram.ui.ActionBar.Theme;
import com.hanista.mobogram.ui.Adapters.BaseFragmentAdapter;
import com.hanista.mobogram.ui.Cells.HeaderCell;
import com.hanista.mobogram.ui.Cells.SessionCell;
import com.hanista.mobogram.ui.Cells.TextInfoPrivacyCell;
import com.hanista.mobogram.ui.Cells.TextSettingsCell;
import java.util.ArrayList;
import java.util.Iterator;

public class SessionsActivity extends BaseFragment implements NotificationCenterDelegate {
    private TL_authorization currentSession;
    private int currentSessionRow;
    private int currentSessionSectionRow;
    private LinearLayout emptyLayout;
    private ListAdapter listAdapter;
    private boolean loading;
    private int noOtherSessionsRow;
    private int otherSessionsEndRow;
    private int otherSessionsSectionRow;
    private int otherSessionsStartRow;
    private int otherSessionsTerminateDetail;
    private int rowCount;
    private ArrayList<TL_authorization> sessions;
    private int terminateAllSessionsDetailRow;
    private int terminateAllSessionsRow;

    /* renamed from: com.hanista.mobogram.ui.SessionsActivity.1 */
    class C18661 extends ActionBarMenuOnItemClick {
        C18661() {
        }

        public void onItemClick(int i) {
            if (i == -1) {
                SessionsActivity.this.finishFragment();
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.SessionsActivity.2 */
    class C18672 implements OnTouchListener {
        C18672() {
        }

        public boolean onTouch(View view, MotionEvent motionEvent) {
            return true;
        }
    }

    /* renamed from: com.hanista.mobogram.ui.SessionsActivity.3 */
    class C18743 implements OnItemClickListener {

        /* renamed from: com.hanista.mobogram.ui.SessionsActivity.3.1 */
        class C18701 implements OnClickListener {

            /* renamed from: com.hanista.mobogram.ui.SessionsActivity.3.1.1 */
            class C18691 implements RequestDelegate {

                /* renamed from: com.hanista.mobogram.ui.SessionsActivity.3.1.1.1 */
                class C18681 implements Runnable {
                    final /* synthetic */ TL_error val$error;
                    final /* synthetic */ TLObject val$response;

                    C18681(TL_error tL_error, TLObject tLObject) {
                        this.val$error = tL_error;
                        this.val$response = tLObject;
                    }

                    public void run() {
                        if (SessionsActivity.this.getParentActivity() != null) {
                            if (this.val$error == null && (this.val$response instanceof TL_boolTrue)) {
                                Toast.makeText(SessionsActivity.this.getParentActivity(), LocaleController.getString("TerminateAllSessions", C0338R.string.TerminateAllSessions), 0).show();
                            } else {
                                Toast.makeText(SessionsActivity.this.getParentActivity(), LocaleController.getString("UnknownError", C0338R.string.UnknownError), 0).show();
                            }
                            SessionsActivity.this.finishFragment();
                        }
                    }
                }

                C18691() {
                }

                public void run(TLObject tLObject, TL_error tL_error) {
                    AndroidUtilities.runOnUIThread(new C18681(tL_error, tLObject));
                    UserConfig.registeredForPush = false;
                    UserConfig.saveConfig(false);
                    MessagesController.getInstance().registerForPush(UserConfig.pushString);
                    ConnectionsManager.getInstance().setUserId(UserConfig.getClientUserId());
                }
            }

            C18701() {
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                ConnectionsManager.getInstance().sendRequest(new TL_auth_resetAuthorizations(), new C18691());
            }
        }

        /* renamed from: com.hanista.mobogram.ui.SessionsActivity.3.2 */
        class C18732 implements OnClickListener {
            final /* synthetic */ int val$i;

            /* renamed from: com.hanista.mobogram.ui.SessionsActivity.3.2.1 */
            class C18721 implements RequestDelegate {
                final /* synthetic */ TL_authorization val$authorization;
                final /* synthetic */ ProgressDialog val$progressDialog;

                /* renamed from: com.hanista.mobogram.ui.SessionsActivity.3.2.1.1 */
                class C18711 implements Runnable {
                    final /* synthetic */ TL_error val$error;

                    C18711(TL_error tL_error) {
                        this.val$error = tL_error;
                    }

                    public void run() {
                        try {
                            C18721.this.val$progressDialog.dismiss();
                        } catch (Throwable e) {
                            FileLog.m18e("tmessages", e);
                        }
                        if (this.val$error == null) {
                            SessionsActivity.this.sessions.remove(C18721.this.val$authorization);
                            SessionsActivity.this.updateRows();
                            if (SessionsActivity.this.listAdapter != null) {
                                SessionsActivity.this.listAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                }

                C18721(ProgressDialog progressDialog, TL_authorization tL_authorization) {
                    this.val$progressDialog = progressDialog;
                    this.val$authorization = tL_authorization;
                }

                public void run(TLObject tLObject, TL_error tL_error) {
                    AndroidUtilities.runOnUIThread(new C18711(tL_error));
                }
            }

            C18732(int i) {
                this.val$i = i;
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                ProgressDialog progressDialog = new ProgressDialog(SessionsActivity.this.getParentActivity());
                progressDialog.setMessage(LocaleController.getString("Loading", C0338R.string.Loading));
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.setCancelable(false);
                progressDialog.show();
                TL_authorization tL_authorization = (TL_authorization) SessionsActivity.this.sessions.get(this.val$i - SessionsActivity.this.otherSessionsStartRow);
                TLObject tL_account_resetAuthorization = new TL_account_resetAuthorization();
                tL_account_resetAuthorization.hash = tL_authorization.hash;
                ConnectionsManager.getInstance().sendRequest(tL_account_resetAuthorization, new C18721(progressDialog, tL_authorization));
            }
        }

        C18743() {
        }

        public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
            Builder builder;
            if (i == SessionsActivity.this.terminateAllSessionsRow) {
                if (SessionsActivity.this.getParentActivity() != null) {
                    builder = new Builder(SessionsActivity.this.getParentActivity());
                    builder.setMessage(LocaleController.getString("AreYouSureSessions", C0338R.string.AreYouSureSessions));
                    builder.setTitle(LocaleController.getString("AppName", C0338R.string.AppName));
                    builder.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), new C18701());
                    builder.setNegativeButton(LocaleController.getString("Cancel", C0338R.string.Cancel), null);
                    SessionsActivity.this.showDialog(builder.create());
                }
            } else if (i >= SessionsActivity.this.otherSessionsStartRow && i < SessionsActivity.this.otherSessionsEndRow) {
                builder = new Builder(SessionsActivity.this.getParentActivity());
                builder.setMessage(LocaleController.getString("TerminateSessionQuestion", C0338R.string.TerminateSessionQuestion));
                builder.setTitle(LocaleController.getString("AppName", C0338R.string.AppName));
                builder.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), new C18732(i));
                builder.setNegativeButton(LocaleController.getString("Cancel", C0338R.string.Cancel), null);
                SessionsActivity.this.showDialog(builder.create());
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.SessionsActivity.4 */
    class C18764 implements RequestDelegate {

        /* renamed from: com.hanista.mobogram.ui.SessionsActivity.4.1 */
        class C18751 implements Runnable {
            final /* synthetic */ TL_error val$error;
            final /* synthetic */ TLObject val$response;

            C18751(TL_error tL_error, TLObject tLObject) {
                this.val$error = tL_error;
                this.val$response = tLObject;
            }

            public void run() {
                SessionsActivity.this.loading = false;
                if (this.val$error == null) {
                    SessionsActivity.this.sessions.clear();
                    Iterator it = ((TL_account_authorizations) this.val$response).authorizations.iterator();
                    while (it.hasNext()) {
                        TL_authorization tL_authorization = (TL_authorization) it.next();
                        if ((tL_authorization.flags & 1) != 0) {
                            SessionsActivity.this.currentSession = tL_authorization;
                        } else {
                            SessionsActivity.this.sessions.add(tL_authorization);
                        }
                    }
                    SessionsActivity.this.updateRows();
                }
                if (SessionsActivity.this.listAdapter != null) {
                    SessionsActivity.this.listAdapter.notifyDataSetChanged();
                }
            }
        }

        C18764() {
        }

        public void run(TLObject tLObject, TL_error tL_error) {
            AndroidUtilities.runOnUIThread(new C18751(tL_error, tLObject));
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
            return SessionsActivity.this.loading ? 0 : SessionsActivity.this.rowCount;
        }

        public Object getItem(int i) {
            return null;
        }

        public long getItemId(int i) {
            return (long) i;
        }

        public int getItemViewType(int i) {
            return i == SessionsActivity.this.terminateAllSessionsRow ? 0 : (i == SessionsActivity.this.terminateAllSessionsDetailRow || i == SessionsActivity.this.otherSessionsTerminateDetail) ? 1 : (i == SessionsActivity.this.currentSessionSectionRow || i == SessionsActivity.this.otherSessionsSectionRow) ? 2 : i == SessionsActivity.this.noOtherSessionsRow ? 3 : (i == SessionsActivity.this.currentSessionRow || (i >= SessionsActivity.this.otherSessionsStartRow && i < SessionsActivity.this.otherSessionsEndRow)) ? 4 : 0;
        }

        public View getView(int i, View view, ViewGroup viewGroup) {
            boolean z = true;
            boolean z2 = false;
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
                if (i != SessionsActivity.this.terminateAllSessionsRow) {
                    return textSettingsCell;
                }
                textSettingsCell2.setTextColor(-2404015);
                textSettingsCell2.setText(LocaleController.getString("TerminateAllSessions", C0338R.string.TerminateAllSessions), false);
                return textSettingsCell;
            } else if (itemViewType == 1) {
                textSettingsCell = view == null ? new TextInfoPrivacyCell(this.mContext) : view;
                if (i == SessionsActivity.this.terminateAllSessionsDetailRow) {
                    ((TextInfoPrivacyCell) textSettingsCell).setText(LocaleController.getString("ClearOtherSessionsHelp", C0338R.string.ClearOtherSessionsHelp));
                    textSettingsCell.setBackgroundResource(C0338R.drawable.greydivider);
                    return textSettingsCell;
                } else if (i != SessionsActivity.this.otherSessionsTerminateDetail) {
                    return textSettingsCell;
                } else {
                    ((TextInfoPrivacyCell) textSettingsCell).setText(LocaleController.getString("TerminateSessionInfo", C0338R.string.TerminateSessionInfo));
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
                if (i == SessionsActivity.this.currentSessionSectionRow) {
                    ((HeaderCell) textSettingsCell).setText(LocaleController.getString("CurrentSession", C0338R.string.CurrentSession));
                    return textSettingsCell;
                } else if (i != SessionsActivity.this.otherSessionsSectionRow) {
                    return textSettingsCell;
                } else {
                    ((HeaderCell) textSettingsCell).setText(LocaleController.getString("OtherSessions", C0338R.string.OtherSessions));
                    return textSettingsCell;
                }
            } else if (itemViewType == 3) {
                LayoutParams layoutParams = SessionsActivity.this.emptyLayout.getLayoutParams();
                if (layoutParams != null) {
                    layoutParams.height = Math.max(AndroidUtilities.dp(220.0f), ((AndroidUtilities.displaySize.y - ActionBar.getCurrentActionBarHeight()) - AndroidUtilities.dp(128.0f)) - (VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0));
                    SessionsActivity.this.emptyLayout.setLayoutParams(layoutParams);
                }
                return SessionsActivity.this.emptyLayout;
            } else if (itemViewType != 4) {
                return view;
            } else {
                if (view == null) {
                    textSettingsCell = new SessionCell(this.mContext);
                    textSettingsCell.setBackgroundColor(-1);
                } else {
                    textSettingsCell = view;
                }
                SessionCell sessionCell;
                TL_authorization access$700;
                if (i == SessionsActivity.this.currentSessionRow) {
                    sessionCell = (SessionCell) textSettingsCell;
                    access$700 = SessionsActivity.this.currentSession;
                    if (!SessionsActivity.this.sessions.isEmpty()) {
                        z2 = true;
                    }
                    sessionCell.setSession(access$700, z2);
                    return textSettingsCell;
                }
                sessionCell = (SessionCell) textSettingsCell;
                access$700 = (TL_authorization) SessionsActivity.this.sessions.get(i - SessionsActivity.this.otherSessionsStartRow);
                if (i == SessionsActivity.this.otherSessionsEndRow - 1) {
                    z = false;
                }
                sessionCell.setSession(access$700, z);
                return textSettingsCell;
            }
        }

        public int getViewTypeCount() {
            return 5;
        }

        public boolean hasStableIds() {
            return false;
        }

        public boolean isEmpty() {
            return SessionsActivity.this.loading;
        }

        public boolean isEnabled(int i) {
            return i == SessionsActivity.this.terminateAllSessionsRow || (i >= SessionsActivity.this.otherSessionsStartRow && i < SessionsActivity.this.otherSessionsEndRow);
        }
    }

    public SessionsActivity() {
        this.sessions = new ArrayList();
        this.currentSession = null;
    }

    private void loadSessions(boolean z) {
        if (!this.loading) {
            if (!z) {
                this.loading = true;
            }
            ConnectionsManager.getInstance().bindRequestToGuid(ConnectionsManager.getInstance().sendRequest(new TL_account_getAuthorizations(), new C18764()), this.classGuid);
        }
    }

    private void updateRows() {
        this.rowCount = 0;
        if (this.currentSession != null) {
            int i = this.rowCount;
            this.rowCount = i + 1;
            this.currentSessionSectionRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.currentSessionRow = i;
        } else {
            this.currentSessionRow = -1;
            this.currentSessionSectionRow = -1;
        }
        if (this.sessions.isEmpty()) {
            if (this.currentSession != null) {
                i = this.rowCount;
                this.rowCount = i + 1;
                this.noOtherSessionsRow = i;
            } else {
                this.noOtherSessionsRow = -1;
            }
            this.terminateAllSessionsRow = -1;
            this.terminateAllSessionsDetailRow = -1;
            this.otherSessionsSectionRow = -1;
            this.otherSessionsStartRow = -1;
            this.otherSessionsEndRow = -1;
            this.otherSessionsTerminateDetail = -1;
            return;
        }
        this.noOtherSessionsRow = -1;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.terminateAllSessionsRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.terminateAllSessionsDetailRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.otherSessionsSectionRow = i;
        this.otherSessionsStartRow = this.otherSessionsSectionRow + 1;
        this.otherSessionsEndRow = this.otherSessionsStartRow + this.sessions.size();
        this.rowCount += this.sessions.size();
        i = this.rowCount;
        this.rowCount = i + 1;
        this.otherSessionsTerminateDetail = i;
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(C0338R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("SessionsTitle", C0338R.string.SessionsTitle));
        this.actionBar.setActionBarMenuOnItemClick(new C18661());
        this.listAdapter = new ListAdapter(context);
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        frameLayout.setBackgroundColor(Theme.ACTION_BAR_MODE_SELECTOR_COLOR);
        int i = AdvanceTheme.f2497h;
        int i2 = AdvanceTheme.f2495f;
        this.emptyLayout = new LinearLayout(context);
        this.emptyLayout = new LinearLayout(context);
        this.emptyLayout.setOrientation(1);
        this.emptyLayout.setGravity(17);
        this.emptyLayout.setBackgroundResource(C0338R.drawable.greydivider_bottom);
        this.emptyLayout.setLayoutParams(new AbsListView.LayoutParams(-1, AndroidUtilities.displaySize.y - ActionBar.getCurrentActionBarHeight()));
        View imageView = new ImageView(context);
        if (ThemeUtil.m2490b()) {
            if (i != -1) {
                this.emptyLayout.setBackgroundColor(i);
            }
            if (i2 != -7697782) {
                Drawable drawable = getParentActivity().getResources().getDrawable(C0338R.drawable.devices);
                drawable.setColorFilter(i2, Mode.SRC_IN);
                imageView.setImageDrawable(drawable);
            }
        }
        imageView.setImageResource(C0338R.drawable.devices);
        this.emptyLayout.addView(imageView);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) imageView.getLayoutParams();
        layoutParams.width = -2;
        layoutParams.height = -2;
        imageView.setLayoutParams(layoutParams);
        imageView = new TextView(context);
        imageView.setTextColor(-7697782);
        if (ThemeUtil.m2490b()) {
            imageView.setTextColor(i2);
        }
        imageView.setGravity(17);
        imageView.setTextSize(1, 17.0f);
        imageView.setTypeface(FontUtil.m1176a().m1160c());
        imageView.setText(LocaleController.getString("NoOtherSessions", C0338R.string.NoOtherSessions));
        this.emptyLayout.addView(imageView);
        layoutParams = (LinearLayout.LayoutParams) imageView.getLayoutParams();
        layoutParams.topMargin = AndroidUtilities.dp(16.0f);
        layoutParams.width = -2;
        layoutParams.height = -2;
        layoutParams.gravity = 17;
        imageView.setLayoutParams(layoutParams);
        imageView = new TextView(context);
        imageView.setTextColor(-7697782);
        if (ThemeUtil.m2490b()) {
            imageView.setTextColor(i2);
        }
        imageView.setGravity(17);
        imageView.setTextSize(1, 17.0f);
        imageView.setPadding(AndroidUtilities.dp(20.0f), 0, AndroidUtilities.dp(20.0f), 0);
        imageView.setText(LocaleController.getString("NoOtherSessionsInfo", C0338R.string.NoOtherSessionsInfo));
        this.emptyLayout.addView(imageView);
        layoutParams = (LinearLayout.LayoutParams) imageView.getLayoutParams();
        layoutParams.topMargin = AndroidUtilities.dp(14.0f);
        layoutParams.width = -2;
        layoutParams.height = -2;
        layoutParams.gravity = 17;
        imageView.setLayoutParams(layoutParams);
        View frameLayout2 = new FrameLayout(context);
        frameLayout.addView(frameLayout2);
        FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) frameLayout2.getLayoutParams();
        layoutParams2.width = -1;
        layoutParams2.height = -1;
        frameLayout2.setLayoutParams(layoutParams2);
        frameLayout2.setOnTouchListener(new C18672());
        frameLayout2.addView(new ProgressBar(context));
        layoutParams2 = (FrameLayout.LayoutParams) frameLayout2.getLayoutParams();
        layoutParams2.width = -2;
        layoutParams2.height = -2;
        layoutParams2.gravity = 17;
        frameLayout2.setLayoutParams(layoutParams2);
        View listView = new ListView(context);
        if (ThemeUtil.m2490b()) {
            listView.setBackgroundColor(i);
        }
        listView.setDivider(null);
        listView.setDividerHeight(0);
        listView.setVerticalScrollBarEnabled(false);
        listView.setDrawSelectorOnTop(true);
        listView.setEmptyView(frameLayout2);
        frameLayout.addView(listView);
        FrameLayout.LayoutParams layoutParams3 = (FrameLayout.LayoutParams) listView.getLayoutParams();
        layoutParams3.width = -1;
        layoutParams3.height = -1;
        layoutParams3.gravity = 48;
        listView.setLayoutParams(layoutParams3);
        listView.setAdapter(this.listAdapter);
        listView.setOnItemClickListener(new C18743());
        return this.fragmentView;
    }

    public void didReceivedNotification(int i, Object... objArr) {
        if (i == NotificationCenter.newSessionReceived) {
            loadSessions(true);
        }
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        updateRows();
        loadSessions(false);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.newSessionReceived);
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.newSessionReceived);
    }

    public void onResume() {
        super.onResume();
        if (this.listAdapter != null) {
            this.listAdapter.notifyDataSetChanged();
        }
        initThemeActionBar();
    }
}
