package com.hanista.mobogram.ui;

import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.ApplicationLoader;
import com.hanista.mobogram.messenger.FileLog;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.MessagesController;
import com.hanista.mobogram.messenger.MessagesStorage;
import com.hanista.mobogram.messenger.NotificationCenter;
import com.hanista.mobogram.messenger.UserConfig;
import com.hanista.mobogram.mobo.p008i.FontUtil;
import com.hanista.mobogram.mobo.p020s.AdvanceTheme;
import com.hanista.mobogram.mobo.p020s.ThemeUtil;
import com.hanista.mobogram.tgnet.ConnectionsManager;
import com.hanista.mobogram.tgnet.RequestDelegate;
import com.hanista.mobogram.tgnet.TLObject;
import com.hanista.mobogram.tgnet.TLRPC.TL_account_checkUsername;
import com.hanista.mobogram.tgnet.TLRPC.TL_account_updateUsername;
import com.hanista.mobogram.tgnet.TLRPC.TL_boolTrue;
import com.hanista.mobogram.tgnet.TLRPC.TL_error;
import com.hanista.mobogram.tgnet.TLRPC.User;
import com.hanista.mobogram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import com.hanista.mobogram.ui.ActionBar.ActionBarMenu;
import com.hanista.mobogram.ui.ActionBar.BaseFragment;
import com.hanista.mobogram.ui.ActionBar.Theme;
import com.hanista.mobogram.ui.Components.LayoutHelper;
import com.hanista.mobogram.ui.Components.VideoPlayer;
import java.util.ArrayList;

public class ChangeUsernameActivity extends BaseFragment {
    private static final int done_button = 1;
    private int checkReqId;
    private Runnable checkRunnable;
    private TextView checkTextView;
    private View doneButton;
    private EditText firstNameField;
    private String lastCheckName;
    private boolean lastNameAvailable;

    /* renamed from: com.hanista.mobogram.ui.ChangeUsernameActivity.1 */
    class C11601 extends ActionBarMenuOnItemClick {
        C11601() {
        }

        public void onItemClick(int i) {
            if (i == -1) {
                ChangeUsernameActivity.this.finishFragment();
            } else if (i == ChangeUsernameActivity.done_button) {
                ChangeUsernameActivity.this.saveName();
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ChangeUsernameActivity.2 */
    class C11612 implements OnTouchListener {
        C11612() {
        }

        public boolean onTouch(View view, MotionEvent motionEvent) {
            return true;
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ChangeUsernameActivity.3 */
    class C11623 implements OnEditorActionListener {
        C11623() {
        }

        public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
            if (i != 6 || ChangeUsernameActivity.this.doneButton == null) {
                return false;
            }
            ChangeUsernameActivity.this.doneButton.performClick();
            return true;
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ChangeUsernameActivity.4 */
    class C11634 implements TextWatcher {
        C11634() {
        }

        public void afterTextChanged(Editable editable) {
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            ChangeUsernameActivity.this.checkUserName(ChangeUsernameActivity.this.firstNameField.getText().toString(), false);
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ChangeUsernameActivity.5 */
    class C11665 implements Runnable {
        final /* synthetic */ String val$name;

        /* renamed from: com.hanista.mobogram.ui.ChangeUsernameActivity.5.1 */
        class C11651 implements RequestDelegate {

            /* renamed from: com.hanista.mobogram.ui.ChangeUsernameActivity.5.1.1 */
            class C11641 implements Runnable {
                final /* synthetic */ TL_error val$error;
                final /* synthetic */ TLObject val$response;

                C11641(TL_error tL_error, TLObject tLObject) {
                    this.val$error = tL_error;
                    this.val$response = tLObject;
                }

                public void run() {
                    ChangeUsernameActivity.this.checkReqId = 0;
                    if (ChangeUsernameActivity.this.lastCheckName != null && ChangeUsernameActivity.this.lastCheckName.equals(C11665.this.val$name)) {
                        if (this.val$error == null && (this.val$response instanceof TL_boolTrue)) {
                            TextView access$600 = ChangeUsernameActivity.this.checkTextView;
                            Object[] objArr = new Object[ChangeUsernameActivity.done_button];
                            objArr[0] = C11665.this.val$name;
                            access$600.setText(LocaleController.formatString("UsernameAvailable", C0338R.string.UsernameAvailable, objArr));
                            ChangeUsernameActivity.this.checkTextView.setTextColor(-14248148);
                            ChangeUsernameActivity.this.lastNameAvailable = true;
                            return;
                        }
                        ChangeUsernameActivity.this.checkTextView.setText(LocaleController.getString("UsernameInUse", C0338R.string.UsernameInUse));
                        ChangeUsernameActivity.this.checkTextView.setTextColor(-3198928);
                        ChangeUsernameActivity.this.lastNameAvailable = false;
                    }
                }
            }

            C11651() {
            }

            public void run(TLObject tLObject, TL_error tL_error) {
                AndroidUtilities.runOnUIThread(new C11641(tL_error, tLObject));
            }
        }

        C11665(String str) {
            this.val$name = str;
        }

        public void run() {
            TLObject tL_account_checkUsername = new TL_account_checkUsername();
            tL_account_checkUsername.username = this.val$name;
            ChangeUsernameActivity.this.checkReqId = ConnectionsManager.getInstance().sendRequest(tL_account_checkUsername, new C11651(), 2);
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ChangeUsernameActivity.6 */
    class C11696 implements RequestDelegate {
        final /* synthetic */ ProgressDialog val$progressDialog;

        /* renamed from: com.hanista.mobogram.ui.ChangeUsernameActivity.6.1 */
        class C11671 implements Runnable {
            final /* synthetic */ User val$user;

            C11671(User user) {
                this.val$user = user;
            }

            public void run() {
                try {
                    C11696.this.val$progressDialog.dismiss();
                } catch (Throwable e) {
                    FileLog.m18e("tmessages", e);
                }
                ArrayList arrayList = new ArrayList();
                arrayList.add(this.val$user);
                MessagesController.getInstance().putUsers(arrayList, false);
                MessagesStorage.getInstance().putUsersAndChats(arrayList, null, false, true);
                UserConfig.saveConfig(true);
                ChangeUsernameActivity.this.finishFragment();
            }
        }

        /* renamed from: com.hanista.mobogram.ui.ChangeUsernameActivity.6.2 */
        class C11682 implements Runnable {
            final /* synthetic */ TL_error val$error;

            C11682(TL_error tL_error) {
                this.val$error = tL_error;
            }

            public void run() {
                try {
                    C11696.this.val$progressDialog.dismiss();
                } catch (Throwable e) {
                    FileLog.m18e("tmessages", e);
                }
                ChangeUsernameActivity.this.showErrorAlert(this.val$error.text);
            }
        }

        C11696(ProgressDialog progressDialog) {
            this.val$progressDialog = progressDialog;
        }

        public void run(TLObject tLObject, TL_error tL_error) {
            if (tL_error == null) {
                AndroidUtilities.runOnUIThread(new C11671((User) tLObject));
            } else {
                AndroidUtilities.runOnUIThread(new C11682(tL_error));
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ChangeUsernameActivity.7 */
    class C11707 implements OnClickListener {
        final /* synthetic */ int val$reqId;

        C11707(int i) {
            this.val$reqId = i;
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            ConnectionsManager.getInstance().cancelRequest(this.val$reqId, true);
            try {
                dialogInterface.dismiss();
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
        }
    }

    public ChangeUsernameActivity() {
        this.checkReqId = 0;
        this.lastCheckName = null;
        this.checkRunnable = null;
        this.lastNameAvailable = false;
    }

    private boolean checkUserName(String str, boolean z) {
        if (str == null || str.length() <= 0) {
            this.checkTextView.setVisibility(8);
        } else {
            this.checkTextView.setVisibility(0);
        }
        if (z && str.length() == 0) {
            return true;
        }
        if (this.checkRunnable != null) {
            AndroidUtilities.cancelRunOnUIThread(this.checkRunnable);
            this.checkRunnable = null;
            this.lastCheckName = null;
            if (this.checkReqId != 0) {
                ConnectionsManager.getInstance().cancelRequest(this.checkReqId, true);
            }
        }
        this.lastNameAvailable = false;
        if (str != null) {
            if (str.startsWith("_") || str.endsWith("_")) {
                this.checkTextView.setText(LocaleController.getString("UsernameInvalid", C0338R.string.UsernameInvalid));
                this.checkTextView.setTextColor(-3198928);
                return false;
            }
            int i = 0;
            while (i < str.length()) {
                char charAt = str.charAt(i);
                if (i != 0 || charAt < '0' || charAt > '9') {
                    if ((charAt >= '0' && charAt <= '9') || ((charAt >= 'a' && charAt <= 'z') || ((charAt >= 'A' && charAt <= 'Z') || charAt == '_'))) {
                        i += done_button;
                    } else if (z) {
                        showErrorAlert(LocaleController.getString("UsernameInvalid", C0338R.string.UsernameInvalid));
                        return false;
                    } else {
                        this.checkTextView.setText(LocaleController.getString("UsernameInvalid", C0338R.string.UsernameInvalid));
                        this.checkTextView.setTextColor(-3198928);
                        return false;
                    }
                } else if (z) {
                    showErrorAlert(LocaleController.getString("UsernameInvalidStartNumber", C0338R.string.UsernameInvalidStartNumber));
                    return false;
                } else {
                    this.checkTextView.setText(LocaleController.getString("UsernameInvalidStartNumber", C0338R.string.UsernameInvalidStartNumber));
                    this.checkTextView.setTextColor(-3198928);
                    return false;
                }
            }
        }
        if (str == null || str.length() < 5) {
            if (z) {
                showErrorAlert(LocaleController.getString("UsernameInvalidShort", C0338R.string.UsernameInvalidShort));
                return false;
            }
            this.checkTextView.setText(LocaleController.getString("UsernameInvalidShort", C0338R.string.UsernameInvalidShort));
            this.checkTextView.setTextColor(-3198928);
            return false;
        } else if (str.length() <= 32) {
            if (!z) {
                Object obj = UserConfig.getCurrentUser().username;
                if (obj == null) {
                    obj = TtmlNode.ANONYMOUS_REGION_ID;
                }
                if (str.equals(obj)) {
                    TextView textView = this.checkTextView;
                    Object[] objArr = new Object[done_button];
                    objArr[0] = str;
                    textView.setText(LocaleController.formatString("UsernameAvailable", C0338R.string.UsernameAvailable, objArr));
                    this.checkTextView.setTextColor(-14248148);
                    return true;
                }
                this.checkTextView.setText(LocaleController.getString("UsernameChecking", C0338R.string.UsernameChecking));
                this.checkTextView.setTextColor(-9605774);
                this.lastCheckName = str;
                this.checkRunnable = new C11665(str);
                AndroidUtilities.runOnUIThread(this.checkRunnable, 300);
            }
            return true;
        } else if (z) {
            showErrorAlert(LocaleController.getString("UsernameInvalidLong", C0338R.string.UsernameInvalidLong));
            return false;
        } else {
            this.checkTextView.setText(LocaleController.getString("UsernameInvalidLong", C0338R.string.UsernameInvalidLong));
            this.checkTextView.setTextColor(-3198928);
            return false;
        }
    }

    private void saveName() {
        if (checkUserName(this.firstNameField.getText().toString(), true)) {
            User currentUser = UserConfig.getCurrentUser();
            if (getParentActivity() != null && currentUser != null) {
                String str = currentUser.username;
                if (str == null) {
                    str = TtmlNode.ANONYMOUS_REGION_ID;
                }
                String obj = this.firstNameField.getText().toString();
                if (str.equals(obj)) {
                    finishFragment();
                    return;
                }
                ProgressDialog progressDialog = new ProgressDialog(getParentActivity());
                progressDialog.setMessage(LocaleController.getString("Loading", C0338R.string.Loading));
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.setCancelable(false);
                TLObject tL_account_updateUsername = new TL_account_updateUsername();
                tL_account_updateUsername.username = obj;
                NotificationCenter instance = NotificationCenter.getInstance();
                int i = NotificationCenter.updateInterfaces;
                Object[] objArr = new Object[done_button];
                objArr[0] = Integer.valueOf(done_button);
                instance.postNotificationName(i, objArr);
                int sendRequest = ConnectionsManager.getInstance().sendRequest(tL_account_updateUsername, new C11696(progressDialog), 2);
                ConnectionsManager.getInstance().bindRequestToGuid(sendRequest, this.classGuid);
                progressDialog.setButton(-2, LocaleController.getString("Cancel", C0338R.string.Cancel), new C11707(sendRequest));
                progressDialog.show();
            }
        }
    }

    private void showErrorAlert(String str) {
        if (getParentActivity() != null) {
            Builder builder = new Builder(getParentActivity());
            builder.setTitle(LocaleController.getString("AppName", C0338R.string.AppName));
            Object obj = -1;
            switch (str.hashCode()) {
                case -141887186:
                    if (str.equals("USERNAMES_UNAVAILABLE")) {
                        obj = 2;
                        break;
                    }
                    break;
                case 288843630:
                    if (str.equals("USERNAME_INVALID")) {
                        obj = null;
                        break;
                    }
                    break;
                case 533175271:
                    if (str.equals("USERNAME_OCCUPIED")) {
                        obj = done_button;
                        break;
                    }
                    break;
            }
            switch (obj) {
                case VideoPlayer.TRACK_DEFAULT /*0*/:
                    builder.setMessage(LocaleController.getString("UsernameInvalid", C0338R.string.UsernameInvalid));
                    break;
                case done_button /*1*/:
                    builder.setMessage(LocaleController.getString("UsernameInUse", C0338R.string.UsernameInUse));
                    break;
                case VideoPlayer.STATE_PREPARING /*2*/:
                    builder.setMessage(LocaleController.getString("FeatureUnavailable", C0338R.string.FeatureUnavailable));
                    break;
                default:
                    builder.setMessage(LocaleController.getString("ErrorOccurred", C0338R.string.ErrorOccurred));
                    break;
            }
            builder.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), null);
            showDialog(builder.create());
        }
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(C0338R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("Username", C0338R.string.Username));
        this.actionBar.setActionBarMenuOnItemClick(new C11601());
        ActionBarMenu createMenu = this.actionBar.createMenu();
        if (ThemeUtil.m2490b()) {
            Drawable drawable = getParentActivity().getResources().getDrawable(C0338R.drawable.ic_done);
            drawable.setColorFilter(AdvanceTheme.f2492c, Mode.SRC_IN);
            this.doneButton = createMenu.addItemWithWidth((int) done_button, drawable, AndroidUtilities.dp(56.0f));
        } else {
            this.doneButton = createMenu.addItemWithWidth((int) done_button, (int) C0338R.drawable.ic_done, AndroidUtilities.dp(56.0f));
        }
        User user = MessagesController.getInstance().getUser(Integer.valueOf(UserConfig.getClientUserId()));
        User currentUser = user == null ? UserConfig.getCurrentUser() : user;
        this.fragmentView = new LinearLayout(context);
        ((LinearLayout) this.fragmentView).setOrientation(done_button);
        this.fragmentView.setOnTouchListener(new C11612());
        this.firstNameField = new EditText(context);
        this.firstNameField.setTextSize(done_button, 18.0f);
        this.firstNameField.setHintTextColor(Theme.SHARE_SHEET_EDIT_PLACEHOLDER_TEXT_COLOR);
        this.firstNameField.setTextColor(Theme.STICKERS_SHEET_TITLE_TEXT_COLOR);
        if (ThemeUtil.m2490b()) {
            this.firstNameField.getBackground().setColorFilter(AdvanceTheme.f2491b, Mode.SRC_IN);
        }
        this.firstNameField.setMaxLines(done_button);
        this.firstNameField.setLines(done_button);
        this.firstNameField.setPadding(0, 0, 0, 0);
        this.firstNameField.setSingleLine(true);
        this.firstNameField.setGravity(LocaleController.isRTL ? 5 : 3);
        this.firstNameField.setInputType(180224);
        this.firstNameField.setImeOptions(6);
        this.firstNameField.setHint(LocaleController.getString("UsernamePlaceholder", C0338R.string.UsernamePlaceholder));
        AndroidUtilities.clearCursorDrawable(this.firstNameField);
        this.firstNameField.setOnEditorActionListener(new C11623());
        ((LinearLayout) this.fragmentView).addView(this.firstNameField, LayoutHelper.createLinear(-1, 36, 24.0f, 24.0f, 24.0f, 0.0f));
        if (!(currentUser == null || currentUser.username == null || currentUser.username.length() <= 0)) {
            this.firstNameField.setText(currentUser.username);
            this.firstNameField.setSelection(this.firstNameField.length());
        }
        this.checkTextView = new TextView(context);
        this.checkTextView.setTypeface(FontUtil.m1176a().m1161d());
        this.checkTextView.setTextSize(done_button, 15.0f);
        this.checkTextView.setGravity(LocaleController.isRTL ? 5 : 3);
        ((LinearLayout) this.fragmentView).addView(this.checkTextView, LayoutHelper.createLinear(-2, -2, LocaleController.isRTL ? 5 : 3, 24, 12, 24, 0));
        View textView = new TextView(context);
        textView.setTextSize(done_button, 15.0f);
        textView.setTextColor(-9605774);
        textView.setGravity(LocaleController.isRTL ? 5 : 3);
        textView.setText(AndroidUtilities.replaceTags(LocaleController.getString("UsernameHelp", C0338R.string.UsernameHelp)));
        ((LinearLayout) this.fragmentView).addView(textView, LayoutHelper.createLinear(-2, -2, LocaleController.isRTL ? 5 : 3, 24, 10, 24, 0));
        this.firstNameField.addTextChangedListener(new C11634());
        this.checkTextView.setVisibility(8);
        return this.fragmentView;
    }

    public void onResume() {
        super.onResume();
        if (!ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).getBoolean("view_animations", true)) {
            this.firstNameField.requestFocus();
            AndroidUtilities.showKeyboard(this.firstNameField);
        }
        initThemeActionBar();
    }

    public void onTransitionAnimationEnd(boolean z, boolean z2) {
        if (z) {
            this.firstNameField.requestFocus();
            AndroidUtilities.showKeyboard(this.firstNameField);
        }
    }
}
