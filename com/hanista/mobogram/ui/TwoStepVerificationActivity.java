package com.hanista.mobogram.ui;

import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.PorterDuff.Mode;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Vibrator;
import android.text.method.PasswordTransformationMethod;
import android.view.ActionMode;
import android.view.ActionMode.Callback;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.FileLog;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.NotificationCenter;
import com.hanista.mobogram.messenger.NotificationCenter.NotificationCenterDelegate;
import com.hanista.mobogram.messenger.Utilities;
import com.hanista.mobogram.messenger.exoplayer.C0700C;
import com.hanista.mobogram.messenger.exoplayer.hls.HlsChunkSource;
import com.hanista.mobogram.mobo.p008i.FontUtil;
import com.hanista.mobogram.mobo.p020s.AdvanceTheme;
import com.hanista.mobogram.mobo.p020s.ThemeUtil;
import com.hanista.mobogram.tgnet.ConnectionsManager;
import com.hanista.mobogram.tgnet.RequestDelegate;
import com.hanista.mobogram.tgnet.TLObject;
import com.hanista.mobogram.tgnet.TLRPC.TL_account_getPassword;
import com.hanista.mobogram.tgnet.TLRPC.TL_account_getPasswordSettings;
import com.hanista.mobogram.tgnet.TLRPC.TL_account_noPassword;
import com.hanista.mobogram.tgnet.TLRPC.TL_account_password;
import com.hanista.mobogram.tgnet.TLRPC.TL_account_passwordInputSettings;
import com.hanista.mobogram.tgnet.TLRPC.TL_account_updatePasswordSettings;
import com.hanista.mobogram.tgnet.TLRPC.TL_auth_passwordRecovery;
import com.hanista.mobogram.tgnet.TLRPC.TL_auth_recoverPassword;
import com.hanista.mobogram.tgnet.TLRPC.TL_auth_requestPasswordRecovery;
import com.hanista.mobogram.tgnet.TLRPC.TL_boolTrue;
import com.hanista.mobogram.tgnet.TLRPC.TL_error;
import com.hanista.mobogram.tgnet.TLRPC.account_Password;
import com.hanista.mobogram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import com.hanista.mobogram.ui.ActionBar.ActionBarMenu;
import com.hanista.mobogram.ui.ActionBar.ActionBarMenuItem;
import com.hanista.mobogram.ui.ActionBar.BaseFragment;
import com.hanista.mobogram.ui.ActionBar.Theme;
import com.hanista.mobogram.ui.Adapters.BaseFragmentAdapter;
import com.hanista.mobogram.ui.Cells.TextInfoPrivacyCell;
import com.hanista.mobogram.ui.Cells.TextSettingsCell;

public class TwoStepVerificationActivity extends BaseFragment implements NotificationCenterDelegate {
    private static final int done_button = 1;
    private int abortPasswordRow;
    private TextView bottomButton;
    private TextView bottomTextView;
    private int changePasswordRow;
    private int changeRecoveryEmailRow;
    private account_Password currentPassword;
    private byte[] currentPasswordHash;
    private boolean destroyed;
    private ActionBarMenuItem doneItem;
    private String email;
    private boolean emailOnly;
    private String firstPassword;
    private String hint;
    private ListAdapter listAdapter;
    private ListView listView;
    private boolean loading;
    private EditText passwordEditText;
    private int passwordEmailVerifyDetailRow;
    private int passwordEnabledDetailRow;
    private boolean passwordEntered;
    private int passwordSetState;
    private int passwordSetupDetailRow;
    private ProgressDialog progressDialog;
    private FrameLayout progressView;
    private int rowCount;
    private ScrollView scrollView;
    private int setPasswordDetailRow;
    private int setPasswordRow;
    private int setRecoveryEmailRow;
    private int shadowRow;
    private Runnable shortPollRunnable;
    private TextView titleTextView;
    private int turnPasswordOffRow;
    private int type;
    private boolean waitingForEmail;

    /* renamed from: com.hanista.mobogram.ui.TwoStepVerificationActivity.10 */
    class AnonymousClass10 implements RequestDelegate {
        final /* synthetic */ boolean val$clear;
        final /* synthetic */ TL_account_updatePasswordSettings val$req;

        /* renamed from: com.hanista.mobogram.ui.TwoStepVerificationActivity.10.1 */
        class C19261 implements Runnable {
            final /* synthetic */ TL_error val$error;
            final /* synthetic */ TLObject val$response;

            /* renamed from: com.hanista.mobogram.ui.TwoStepVerificationActivity.10.1.1 */
            class C19241 implements OnClickListener {
                C19241() {
                }

                public void onClick(DialogInterface dialogInterface, int i) {
                    NotificationCenter instance = NotificationCenter.getInstance();
                    int i2 = NotificationCenter.didSetTwoStepPassword;
                    Object[] objArr = new Object[TwoStepVerificationActivity.done_button];
                    objArr[0] = AnonymousClass10.this.val$req.new_settings.new_password_hash;
                    instance.postNotificationName(i2, objArr);
                    TwoStepVerificationActivity.this.finishFragment();
                }
            }

            /* renamed from: com.hanista.mobogram.ui.TwoStepVerificationActivity.10.1.2 */
            class C19252 implements OnClickListener {
                C19252() {
                }

                public void onClick(DialogInterface dialogInterface, int i) {
                    NotificationCenter instance = NotificationCenter.getInstance();
                    int i2 = NotificationCenter.didSetTwoStepPassword;
                    Object[] objArr = new Object[TwoStepVerificationActivity.done_button];
                    objArr[0] = AnonymousClass10.this.val$req.new_settings.new_password_hash;
                    instance.postNotificationName(i2, objArr);
                    TwoStepVerificationActivity.this.finishFragment();
                }
            }

            C19261(TL_error tL_error, TLObject tLObject) {
                this.val$error = tL_error;
                this.val$response = tLObject;
            }

            public void run() {
                TwoStepVerificationActivity.this.needHideProgress();
                Builder builder;
                Dialog showDialog;
                if (this.val$error == null && (this.val$response instanceof TL_boolTrue)) {
                    if (AnonymousClass10.this.val$clear) {
                        TwoStepVerificationActivity.this.currentPassword = null;
                        TwoStepVerificationActivity.this.currentPasswordHash = new byte[0];
                        TwoStepVerificationActivity.this.loadPasswordInfo(false);
                        TwoStepVerificationActivity.this.updateRows();
                    } else if (TwoStepVerificationActivity.this.getParentActivity() != null) {
                        builder = new Builder(TwoStepVerificationActivity.this.getParentActivity());
                        builder.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), new C19241());
                        builder.setMessage(LocaleController.getString("YourPasswordSuccessText", C0338R.string.YourPasswordSuccessText));
                        builder.setTitle(LocaleController.getString("YourPasswordSuccess", C0338R.string.YourPasswordSuccess));
                        showDialog = TwoStepVerificationActivity.this.showDialog(builder.create());
                        if (showDialog != null) {
                            showDialog.setCanceledOnTouchOutside(false);
                            showDialog.setCancelable(false);
                        }
                    }
                } else if (this.val$error == null) {
                } else {
                    if (this.val$error.text.equals("EMAIL_UNCONFIRMED")) {
                        builder = new Builder(TwoStepVerificationActivity.this.getParentActivity());
                        builder.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), new C19252());
                        builder.setMessage(LocaleController.getString("YourEmailAlmostThereText", C0338R.string.YourEmailAlmostThereText));
                        builder.setTitle(LocaleController.getString("YourEmailAlmostThere", C0338R.string.YourEmailAlmostThere));
                        showDialog = TwoStepVerificationActivity.this.showDialog(builder.create());
                        if (showDialog != null) {
                            showDialog.setCanceledOnTouchOutside(false);
                            showDialog.setCancelable(false);
                        }
                    } else if (this.val$error.text.equals("EMAIL_INVALID")) {
                        TwoStepVerificationActivity.this.showAlertWithText(LocaleController.getString("AppName", C0338R.string.AppName), LocaleController.getString("PasswordEmailInvalid", C0338R.string.PasswordEmailInvalid));
                    } else if (this.val$error.text.startsWith("FLOOD_WAIT")) {
                        int intValue = Utilities.parseInt(this.val$error.text).intValue();
                        String formatPluralString = intValue < 60 ? LocaleController.formatPluralString("Seconds", intValue) : LocaleController.formatPluralString("Minutes", intValue / 60);
                        TwoStepVerificationActivity twoStepVerificationActivity = TwoStepVerificationActivity.this;
                        String string = LocaleController.getString("AppName", C0338R.string.AppName);
                        Object[] objArr = new Object[TwoStepVerificationActivity.done_button];
                        objArr[0] = formatPluralString;
                        twoStepVerificationActivity.showAlertWithText(string, LocaleController.formatString("FloodWaitTime", C0338R.string.FloodWaitTime, objArr));
                    } else {
                        TwoStepVerificationActivity.this.showAlertWithText(LocaleController.getString("AppName", C0338R.string.AppName), this.val$error.text);
                    }
                }
            }
        }

        AnonymousClass10(boolean z, TL_account_updatePasswordSettings tL_account_updatePasswordSettings) {
            this.val$clear = z;
            this.val$req = tL_account_updatePasswordSettings;
        }

        public void run(TLObject tLObject, TL_error tL_error) {
            AndroidUtilities.runOnUIThread(new C19261(tL_error, tLObject));
        }
    }

    /* renamed from: com.hanista.mobogram.ui.TwoStepVerificationActivity.11 */
    class AnonymousClass11 implements RequestDelegate {
        final /* synthetic */ TL_account_getPasswordSettings val$req;

        /* renamed from: com.hanista.mobogram.ui.TwoStepVerificationActivity.11.1 */
        class C19271 implements Runnable {
            final /* synthetic */ TL_error val$error;

            C19271(TL_error tL_error) {
                this.val$error = tL_error;
            }

            public void run() {
                TwoStepVerificationActivity.this.needHideProgress();
                if (this.val$error == null) {
                    TwoStepVerificationActivity.this.currentPasswordHash = AnonymousClass11.this.val$req.current_password_hash;
                    TwoStepVerificationActivity.this.passwordEntered = true;
                    AndroidUtilities.hideKeyboard(TwoStepVerificationActivity.this.passwordEditText);
                    TwoStepVerificationActivity.this.updateRows();
                } else if (this.val$error.text.equals("PASSWORD_HASH_INVALID")) {
                    TwoStepVerificationActivity.this.onPasscodeError(true);
                } else if (this.val$error.text.startsWith("FLOOD_WAIT")) {
                    int intValue = Utilities.parseInt(this.val$error.text).intValue();
                    String formatPluralString = intValue < 60 ? LocaleController.formatPluralString("Seconds", intValue) : LocaleController.formatPluralString("Minutes", intValue / 60);
                    TwoStepVerificationActivity twoStepVerificationActivity = TwoStepVerificationActivity.this;
                    String string = LocaleController.getString("AppName", C0338R.string.AppName);
                    Object[] objArr = new Object[TwoStepVerificationActivity.done_button];
                    objArr[0] = formatPluralString;
                    twoStepVerificationActivity.showAlertWithText(string, LocaleController.formatString("FloodWaitTime", C0338R.string.FloodWaitTime, objArr));
                } else {
                    TwoStepVerificationActivity.this.showAlertWithText(LocaleController.getString("AppName", C0338R.string.AppName), this.val$error.text);
                }
            }
        }

        AnonymousClass11(TL_account_getPasswordSettings tL_account_getPasswordSettings) {
            this.val$req = tL_account_getPasswordSettings;
        }

        public void run(TLObject tLObject, TL_error tL_error) {
            AndroidUtilities.runOnUIThread(new C19271(tL_error));
        }
    }

    /* renamed from: com.hanista.mobogram.ui.TwoStepVerificationActivity.1 */
    class C19301 extends ActionBarMenuOnItemClick {
        C19301() {
        }

        public void onItemClick(int i) {
            if (i == -1) {
                TwoStepVerificationActivity.this.finishFragment();
            } else if (i == TwoStepVerificationActivity.done_button) {
                TwoStepVerificationActivity.this.processDone();
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.TwoStepVerificationActivity.2 */
    class C19312 implements OnEditorActionListener {
        C19312() {
        }

        public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
            if (i != 5 && i != 6) {
                return false;
            }
            TwoStepVerificationActivity.this.processDone();
            return true;
        }
    }

    /* renamed from: com.hanista.mobogram.ui.TwoStepVerificationActivity.3 */
    class C19323 implements Callback {
        C19323() {
        }

        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
            return false;
        }

        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            return false;
        }

        public void onDestroyActionMode(ActionMode actionMode) {
        }

        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
            return false;
        }
    }

    /* renamed from: com.hanista.mobogram.ui.TwoStepVerificationActivity.4 */
    class C19374 implements View.OnClickListener {

        /* renamed from: com.hanista.mobogram.ui.TwoStepVerificationActivity.4.1 */
        class C19351 implements RequestDelegate {

            /* renamed from: com.hanista.mobogram.ui.TwoStepVerificationActivity.4.1.1 */
            class C19341 implements Runnable {
                final /* synthetic */ TL_error val$error;
                final /* synthetic */ TLObject val$response;

                /* renamed from: com.hanista.mobogram.ui.TwoStepVerificationActivity.4.1.1.1 */
                class C19331 implements OnClickListener {
                    final /* synthetic */ TL_auth_passwordRecovery val$res;

                    C19331(TL_auth_passwordRecovery tL_auth_passwordRecovery) {
                        this.val$res = tL_auth_passwordRecovery;
                    }

                    public void onClick(DialogInterface dialogInterface, int i) {
                        BaseFragment twoStepVerificationActivity = new TwoStepVerificationActivity(TwoStepVerificationActivity.done_button);
                        twoStepVerificationActivity.currentPassword = TwoStepVerificationActivity.this.currentPassword;
                        twoStepVerificationActivity.currentPassword.email_unconfirmed_pattern = this.val$res.email_pattern;
                        twoStepVerificationActivity.passwordSetState = 4;
                        TwoStepVerificationActivity.this.presentFragment(twoStepVerificationActivity);
                    }
                }

                C19341(TL_error tL_error, TLObject tLObject) {
                    this.val$error = tL_error;
                    this.val$response = tLObject;
                }

                public void run() {
                    TwoStepVerificationActivity.this.needHideProgress();
                    if (this.val$error == null) {
                        TL_auth_passwordRecovery tL_auth_passwordRecovery = (TL_auth_passwordRecovery) this.val$response;
                        Builder builder = new Builder(TwoStepVerificationActivity.this.getParentActivity());
                        Object[] objArr = new Object[TwoStepVerificationActivity.done_button];
                        objArr[0] = tL_auth_passwordRecovery.email_pattern;
                        builder.setMessage(LocaleController.formatString("RestoreEmailSent", C0338R.string.RestoreEmailSent, objArr));
                        builder.setTitle(LocaleController.getString("AppName", C0338R.string.AppName));
                        builder.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), new C19331(tL_auth_passwordRecovery));
                        Dialog showDialog = TwoStepVerificationActivity.this.showDialog(builder.create());
                        if (showDialog != null) {
                            showDialog.setCanceledOnTouchOutside(false);
                            showDialog.setCancelable(false);
                        }
                    } else if (this.val$error.text.startsWith("FLOOD_WAIT")) {
                        int intValue = Utilities.parseInt(this.val$error.text).intValue();
                        String formatPluralString = intValue < 60 ? LocaleController.formatPluralString("Seconds", intValue) : LocaleController.formatPluralString("Minutes", intValue / 60);
                        TwoStepVerificationActivity twoStepVerificationActivity = TwoStepVerificationActivity.this;
                        String string = LocaleController.getString("AppName", C0338R.string.AppName);
                        Object[] objArr2 = new Object[TwoStepVerificationActivity.done_button];
                        objArr2[0] = formatPluralString;
                        twoStepVerificationActivity.showAlertWithText(string, LocaleController.formatString("FloodWaitTime", C0338R.string.FloodWaitTime, objArr2));
                    } else {
                        TwoStepVerificationActivity.this.showAlertWithText(LocaleController.getString("AppName", C0338R.string.AppName), this.val$error.text);
                    }
                }
            }

            C19351() {
            }

            public void run(TLObject tLObject, TL_error tL_error) {
                AndroidUtilities.runOnUIThread(new C19341(tL_error, tLObject));
            }
        }

        /* renamed from: com.hanista.mobogram.ui.TwoStepVerificationActivity.4.2 */
        class C19362 implements OnClickListener {
            C19362() {
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                TwoStepVerificationActivity.this.email = TtmlNode.ANONYMOUS_REGION_ID;
                TwoStepVerificationActivity.this.setNewPassword(false);
            }
        }

        C19374() {
        }

        public void onClick(View view) {
            if (TwoStepVerificationActivity.this.type == 0) {
                if (TwoStepVerificationActivity.this.currentPassword.has_recovery) {
                    TwoStepVerificationActivity.this.needShowProgress();
                    ConnectionsManager.getInstance().sendRequest(new TL_auth_requestPasswordRecovery(), new C19351(), 10);
                    return;
                }
                TwoStepVerificationActivity.this.showAlertWithText(LocaleController.getString("RestorePasswordNoEmailTitle", C0338R.string.RestorePasswordNoEmailTitle), LocaleController.getString("RestorePasswordNoEmailText", C0338R.string.RestorePasswordNoEmailText));
            } else if (TwoStepVerificationActivity.this.passwordSetState == 4) {
                TwoStepVerificationActivity.this.showAlertWithText(LocaleController.getString("RestorePasswordNoEmailTitle", C0338R.string.RestorePasswordNoEmailTitle), LocaleController.getString("RestoreEmailTroubleText", C0338R.string.RestoreEmailTroubleText));
            } else {
                Builder builder = new Builder(TwoStepVerificationActivity.this.getParentActivity());
                builder.setMessage(LocaleController.getString("YourEmailSkipWarningText", C0338R.string.YourEmailSkipWarningText));
                builder.setTitle(LocaleController.getString("YourEmailSkipWarning", C0338R.string.YourEmailSkipWarning));
                builder.setPositiveButton(LocaleController.getString("YourEmailSkip", C0338R.string.YourEmailSkip), new C19362());
                builder.setNegativeButton(LocaleController.getString("Cancel", C0338R.string.Cancel), null);
                TwoStepVerificationActivity.this.showDialog(builder.create());
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.TwoStepVerificationActivity.5 */
    class C19385 implements OnTouchListener {
        C19385() {
        }

        public boolean onTouch(View view, MotionEvent motionEvent) {
            return true;
        }
    }

    /* renamed from: com.hanista.mobogram.ui.TwoStepVerificationActivity.6 */
    class C19406 implements OnItemClickListener {

        /* renamed from: com.hanista.mobogram.ui.TwoStepVerificationActivity.6.1 */
        class C19391 implements OnClickListener {
            C19391() {
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                TwoStepVerificationActivity.this.setNewPassword(true);
            }
        }

        C19406() {
        }

        public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
            BaseFragment twoStepVerificationActivity;
            if (i == TwoStepVerificationActivity.this.setPasswordRow || i == TwoStepVerificationActivity.this.changePasswordRow) {
                twoStepVerificationActivity = new TwoStepVerificationActivity(TwoStepVerificationActivity.done_button);
                twoStepVerificationActivity.currentPasswordHash = TwoStepVerificationActivity.this.currentPasswordHash;
                twoStepVerificationActivity.currentPassword = TwoStepVerificationActivity.this.currentPassword;
                TwoStepVerificationActivity.this.presentFragment(twoStepVerificationActivity);
            } else if (i == TwoStepVerificationActivity.this.setRecoveryEmailRow || i == TwoStepVerificationActivity.this.changeRecoveryEmailRow) {
                twoStepVerificationActivity = new TwoStepVerificationActivity(TwoStepVerificationActivity.done_button);
                twoStepVerificationActivity.currentPasswordHash = TwoStepVerificationActivity.this.currentPasswordHash;
                twoStepVerificationActivity.currentPassword = TwoStepVerificationActivity.this.currentPassword;
                twoStepVerificationActivity.emailOnly = true;
                twoStepVerificationActivity.passwordSetState = 3;
                TwoStepVerificationActivity.this.presentFragment(twoStepVerificationActivity);
            } else if (i == TwoStepVerificationActivity.this.turnPasswordOffRow || i == TwoStepVerificationActivity.this.abortPasswordRow) {
                Builder builder = new Builder(TwoStepVerificationActivity.this.getParentActivity());
                builder.setMessage(LocaleController.getString("TurnPasswordOffQuestion", C0338R.string.TurnPasswordOffQuestion));
                builder.setTitle(LocaleController.getString("AppName", C0338R.string.AppName));
                builder.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), new C19391());
                builder.setNegativeButton(LocaleController.getString("Cancel", C0338R.string.Cancel), null);
                TwoStepVerificationActivity.this.showDialog(builder.create());
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.TwoStepVerificationActivity.7 */
    class C19417 implements Runnable {
        C19417() {
        }

        public void run() {
            if (TwoStepVerificationActivity.this.passwordEditText != null) {
                TwoStepVerificationActivity.this.passwordEditText.requestFocus();
                AndroidUtilities.showKeyboard(TwoStepVerificationActivity.this.passwordEditText);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.TwoStepVerificationActivity.8 */
    class C19448 implements RequestDelegate {
        final /* synthetic */ boolean val$silent;

        /* renamed from: com.hanista.mobogram.ui.TwoStepVerificationActivity.8.1 */
        class C19431 implements Runnable {
            final /* synthetic */ TL_error val$error;
            final /* synthetic */ TLObject val$response;

            /* renamed from: com.hanista.mobogram.ui.TwoStepVerificationActivity.8.1.1 */
            class C19421 implements Runnable {
                C19421() {
                }

                public void run() {
                    if (TwoStepVerificationActivity.this.shortPollRunnable != null) {
                        TwoStepVerificationActivity.this.loadPasswordInfo(true);
                        TwoStepVerificationActivity.this.shortPollRunnable = null;
                    }
                }
            }

            C19431(TL_error tL_error, TLObject tLObject) {
                this.val$error = tL_error;
                this.val$response = tLObject;
            }

            public void run() {
                boolean z = true;
                TwoStepVerificationActivity.this.loading = false;
                if (this.val$error == null) {
                    if (!C19448.this.val$silent) {
                        TwoStepVerificationActivity twoStepVerificationActivity = TwoStepVerificationActivity.this;
                        boolean z2 = TwoStepVerificationActivity.this.currentPassword != null || (this.val$response instanceof TL_account_noPassword);
                        twoStepVerificationActivity.passwordEntered = z2;
                    }
                    TwoStepVerificationActivity.this.currentPassword = (account_Password) this.val$response;
                    TwoStepVerificationActivity twoStepVerificationActivity2 = TwoStepVerificationActivity.this;
                    if (TwoStepVerificationActivity.this.currentPassword.email_unconfirmed_pattern.length() <= 0) {
                        z = false;
                    }
                    twoStepVerificationActivity2.waitingForEmail = z;
                    Object obj = new byte[(TwoStepVerificationActivity.this.currentPassword.new_salt.length + 8)];
                    Utilities.random.nextBytes(obj);
                    System.arraycopy(TwoStepVerificationActivity.this.currentPassword.new_salt, 0, obj, 0, TwoStepVerificationActivity.this.currentPassword.new_salt.length);
                    TwoStepVerificationActivity.this.currentPassword.new_salt = obj;
                }
                if (TwoStepVerificationActivity.this.type == 0 && !TwoStepVerificationActivity.this.destroyed && TwoStepVerificationActivity.this.shortPollRunnable == null) {
                    TwoStepVerificationActivity.this.shortPollRunnable = new C19421();
                    AndroidUtilities.runOnUIThread(TwoStepVerificationActivity.this.shortPollRunnable, HlsChunkSource.DEFAULT_MIN_BUFFER_TO_SWITCH_UP_MS);
                }
                TwoStepVerificationActivity.this.updateRows();
            }
        }

        C19448(boolean z) {
            this.val$silent = z;
        }

        public void run(TLObject tLObject, TL_error tL_error) {
            AndroidUtilities.runOnUIThread(new C19431(tL_error, tLObject));
        }
    }

    /* renamed from: com.hanista.mobogram.ui.TwoStepVerificationActivity.9 */
    class C19459 implements Runnable {
        C19459() {
        }

        public void run() {
            if (TwoStepVerificationActivity.this.passwordEditText != null) {
                TwoStepVerificationActivity.this.passwordEditText.requestFocus();
                AndroidUtilities.showKeyboard(TwoStepVerificationActivity.this.passwordEditText);
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
            return (TwoStepVerificationActivity.this.loading || TwoStepVerificationActivity.this.currentPassword == null) ? 0 : TwoStepVerificationActivity.this.rowCount;
        }

        public Object getItem(int i) {
            return null;
        }

        public long getItemId(int i) {
            return (long) i;
        }

        public int getItemViewType(int i) {
            return (i == TwoStepVerificationActivity.this.setPasswordDetailRow || i == TwoStepVerificationActivity.this.shadowRow || i == TwoStepVerificationActivity.this.passwordSetupDetailRow || i == TwoStepVerificationActivity.this.passwordEnabledDetailRow || i == TwoStepVerificationActivity.this.passwordEmailVerifyDetailRow) ? TwoStepVerificationActivity.done_button : 0;
        }

        public View getView(int i, View view, ViewGroup viewGroup) {
            boolean z = true;
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
                textSettingsCell2.setTextColor(Theme.STICKERS_SHEET_TITLE_TEXT_COLOR);
                if (i == TwoStepVerificationActivity.this.changePasswordRow) {
                    textSettingsCell2.setText(LocaleController.getString("ChangePassword", C0338R.string.ChangePassword), true);
                    return textSettingsCell;
                } else if (i == TwoStepVerificationActivity.this.setPasswordRow) {
                    textSettingsCell2.setText(LocaleController.getString("SetAdditionalPassword", C0338R.string.SetAdditionalPassword), true);
                    return textSettingsCell;
                } else if (i == TwoStepVerificationActivity.this.turnPasswordOffRow) {
                    textSettingsCell2.setText(LocaleController.getString("TurnPasswordOff", C0338R.string.TurnPasswordOff), true);
                    return textSettingsCell;
                } else if (i == TwoStepVerificationActivity.this.changeRecoveryEmailRow) {
                    String string = LocaleController.getString("ChangeRecoveryEmail", C0338R.string.ChangeRecoveryEmail);
                    if (TwoStepVerificationActivity.this.abortPasswordRow == -1) {
                        z = false;
                    }
                    textSettingsCell2.setText(string, z);
                    return textSettingsCell;
                } else if (i == TwoStepVerificationActivity.this.setRecoveryEmailRow) {
                    textSettingsCell2.setText(LocaleController.getString("SetRecoveryEmail", C0338R.string.SetRecoveryEmail), false);
                    return textSettingsCell;
                } else if (i != TwoStepVerificationActivity.this.abortPasswordRow) {
                    return textSettingsCell;
                } else {
                    textSettingsCell2.setTextColor(-2995895);
                    textSettingsCell2.setText(LocaleController.getString("AbortPassword", C0338R.string.AbortPassword), false);
                    return textSettingsCell;
                }
            } else if (itemViewType != TwoStepVerificationActivity.done_button) {
                return view;
            } else {
                textSettingsCell = view == null ? new TextInfoPrivacyCell(this.mContext) : view;
                if (i == TwoStepVerificationActivity.this.setPasswordDetailRow) {
                    ((TextInfoPrivacyCell) textSettingsCell).setText(LocaleController.getString("SetAdditionalPasswordInfo", C0338R.string.SetAdditionalPasswordInfo));
                    textSettingsCell.setBackgroundResource(C0338R.drawable.greydivider_bottom);
                    return textSettingsCell;
                } else if (i == TwoStepVerificationActivity.this.shadowRow) {
                    ((TextInfoPrivacyCell) textSettingsCell).setText(TtmlNode.ANONYMOUS_REGION_ID);
                    textSettingsCell.setBackgroundResource(C0338R.drawable.greydivider_bottom);
                    return textSettingsCell;
                } else if (i == TwoStepVerificationActivity.this.passwordSetupDetailRow) {
                    r0 = (TextInfoPrivacyCell) textSettingsCell;
                    r2 = new Object[TwoStepVerificationActivity.done_button];
                    r2[0] = TwoStepVerificationActivity.this.currentPassword.email_unconfirmed_pattern;
                    r0.setText(LocaleController.formatString("EmailPasswordConfirmText", C0338R.string.EmailPasswordConfirmText, r2));
                    textSettingsCell.setBackgroundResource(C0338R.drawable.greydivider_top);
                    return textSettingsCell;
                } else if (i == TwoStepVerificationActivity.this.passwordEnabledDetailRow) {
                    ((TextInfoPrivacyCell) textSettingsCell).setText(LocaleController.getString("EnabledPasswordText", C0338R.string.EnabledPasswordText));
                    textSettingsCell.setBackgroundResource(C0338R.drawable.greydivider_bottom);
                    return textSettingsCell;
                } else if (i != TwoStepVerificationActivity.this.passwordEmailVerifyDetailRow) {
                    return textSettingsCell;
                } else {
                    r0 = (TextInfoPrivacyCell) textSettingsCell;
                    r2 = new Object[TwoStepVerificationActivity.done_button];
                    r2[0] = TwoStepVerificationActivity.this.currentPassword.email_unconfirmed_pattern;
                    r0.setText(LocaleController.formatString("PendingEmailText", C0338R.string.PendingEmailText, r2));
                    textSettingsCell.setBackgroundResource(C0338R.drawable.greydivider_bottom);
                    return textSettingsCell;
                }
            }
        }

        public int getViewTypeCount() {
            return 2;
        }

        public boolean hasStableIds() {
            return false;
        }

        public boolean isEmpty() {
            return TwoStepVerificationActivity.this.loading || TwoStepVerificationActivity.this.currentPassword == null;
        }

        public boolean isEnabled(int i) {
            return (i == TwoStepVerificationActivity.this.setPasswordDetailRow || i == TwoStepVerificationActivity.this.shadowRow || i == TwoStepVerificationActivity.this.passwordSetupDetailRow || i == TwoStepVerificationActivity.this.passwordEmailVerifyDetailRow || i == TwoStepVerificationActivity.this.passwordEnabledDetailRow) ? false : true;
        }
    }

    public TwoStepVerificationActivity(int i) {
        this.passwordEntered = true;
        this.currentPasswordHash = new byte[0];
        this.type = i;
        if (i == 0) {
            loadPasswordInfo(false);
        }
    }

    private boolean isValidEmail(String str) {
        if (str == null || str.length() < 3) {
            return false;
        }
        int lastIndexOf = str.lastIndexOf(46);
        int lastIndexOf2 = str.lastIndexOf(64);
        return lastIndexOf >= 0 && lastIndexOf2 >= 0 && lastIndexOf >= lastIndexOf2;
    }

    private void loadPasswordInfo(boolean z) {
        if (!z) {
            this.loading = true;
        }
        ConnectionsManager.getInstance().sendRequest(new TL_account_getPassword(), new C19448(z), 10);
    }

    private void needHideProgress() {
        if (this.progressDialog != null) {
            try {
                this.progressDialog.dismiss();
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
            this.progressDialog = null;
        }
    }

    private void needShowProgress() {
        if (getParentActivity() != null && !getParentActivity().isFinishing() && this.progressDialog == null) {
            this.progressDialog = new ProgressDialog(getParentActivity());
            this.progressDialog.setMessage(LocaleController.getString("Loading", C0338R.string.Loading));
            this.progressDialog.setCanceledOnTouchOutside(false);
            this.progressDialog.setCancelable(false);
            this.progressDialog.show();
        }
    }

    private void onPasscodeError(boolean z) {
        if (getParentActivity() != null) {
            Vibrator vibrator = (Vibrator) getParentActivity().getSystemService("vibrator");
            if (vibrator != null) {
                vibrator.vibrate(200);
            }
            if (z) {
                this.passwordEditText.setText(TtmlNode.ANONYMOUS_REGION_ID);
            }
            AndroidUtilities.shakeView(this.titleTextView, 2.0f, 0);
        }
    }

    private void processDone() {
        if (this.type == 0) {
            if (!this.passwordEntered) {
                String obj = this.passwordEditText.getText().toString();
                if (obj.length() == 0) {
                    onPasscodeError(false);
                    return;
                }
                Object obj2 = null;
                try {
                    obj2 = obj.getBytes(C0700C.UTF8_NAME);
                } catch (Throwable e) {
                    FileLog.m18e("tmessages", e);
                }
                needShowProgress();
                Object obj3 = new byte[((this.currentPassword.current_salt.length * 2) + obj2.length)];
                System.arraycopy(this.currentPassword.current_salt, 0, obj3, 0, this.currentPassword.current_salt.length);
                System.arraycopy(obj2, 0, obj3, this.currentPassword.current_salt.length, obj2.length);
                System.arraycopy(this.currentPassword.current_salt, 0, obj3, obj3.length - this.currentPassword.current_salt.length, this.currentPassword.current_salt.length);
                TLObject tL_account_getPasswordSettings = new TL_account_getPasswordSettings();
                tL_account_getPasswordSettings.current_password_hash = Utilities.computeSHA256(obj3, 0, obj3.length);
                ConnectionsManager.getInstance().sendRequest(tL_account_getPasswordSettings, new AnonymousClass11(tL_account_getPasswordSettings), 10);
            }
        } else if (this.type != done_button) {
        } else {
            if (this.passwordSetState == 0) {
                if (this.passwordEditText.getText().length() == 0) {
                    onPasscodeError(false);
                    return;
                }
                this.titleTextView.setText(LocaleController.getString("ReEnterYourPasscode", C0338R.string.ReEnterYourPasscode));
                this.firstPassword = this.passwordEditText.getText().toString();
                setPasswordSetState(done_button);
            } else if (this.passwordSetState == done_button) {
                if (this.firstPassword.equals(this.passwordEditText.getText().toString())) {
                    setPasswordSetState(2);
                    return;
                }
                try {
                    Toast.makeText(getParentActivity(), LocaleController.getString("PasswordDoNotMatch", C0338R.string.PasswordDoNotMatch), 0).show();
                } catch (Throwable e2) {
                    FileLog.m18e("tmessages", e2);
                }
                onPasscodeError(true);
            } else if (this.passwordSetState == 2) {
                this.hint = this.passwordEditText.getText().toString();
                if (this.hint.toLowerCase().equals(this.firstPassword.toLowerCase())) {
                    try {
                        Toast.makeText(getParentActivity(), LocaleController.getString("PasswordAsHintError", C0338R.string.PasswordAsHintError), 0).show();
                    } catch (Throwable e22) {
                        FileLog.m18e("tmessages", e22);
                    }
                    onPasscodeError(false);
                } else if (this.currentPassword.has_recovery) {
                    this.email = TtmlNode.ANONYMOUS_REGION_ID;
                    setNewPassword(false);
                } else {
                    setPasswordSetState(3);
                }
            } else if (this.passwordSetState == 3) {
                this.email = this.passwordEditText.getText().toString();
                if (isValidEmail(this.email)) {
                    setNewPassword(false);
                } else {
                    onPasscodeError(false);
                }
            } else if (this.passwordSetState == 4) {
                String obj4 = this.passwordEditText.getText().toString();
                if (obj4.length() == 0) {
                    onPasscodeError(false);
                    return;
                }
                TLObject tL_auth_recoverPassword = new TL_auth_recoverPassword();
                tL_auth_recoverPassword.code = obj4;
                ConnectionsManager.getInstance().sendRequest(tL_auth_recoverPassword, new RequestDelegate() {

                    /* renamed from: com.hanista.mobogram.ui.TwoStepVerificationActivity.12.1 */
                    class C19291 implements Runnable {
                        final /* synthetic */ TL_error val$error;

                        /* renamed from: com.hanista.mobogram.ui.TwoStepVerificationActivity.12.1.1 */
                        class C19281 implements OnClickListener {
                            C19281() {
                            }

                            public void onClick(DialogInterface dialogInterface, int i) {
                                NotificationCenter.getInstance().postNotificationName(NotificationCenter.didSetTwoStepPassword, new Object[0]);
                                TwoStepVerificationActivity.this.finishFragment();
                            }
                        }

                        C19291(TL_error tL_error) {
                            this.val$error = tL_error;
                        }

                        public void run() {
                            if (this.val$error == null) {
                                Builder builder = new Builder(TwoStepVerificationActivity.this.getParentActivity());
                                builder.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), new C19281());
                                builder.setMessage(LocaleController.getString("PasswordReset", C0338R.string.PasswordReset));
                                builder.setTitle(LocaleController.getString("AppName", C0338R.string.AppName));
                                Dialog showDialog = TwoStepVerificationActivity.this.showDialog(builder.create());
                                if (showDialog != null) {
                                    showDialog.setCanceledOnTouchOutside(false);
                                    showDialog.setCancelable(false);
                                }
                            } else if (this.val$error.text.startsWith("CODE_INVALID")) {
                                TwoStepVerificationActivity.this.onPasscodeError(true);
                            } else if (this.val$error.text.startsWith("FLOOD_WAIT")) {
                                int intValue = Utilities.parseInt(this.val$error.text).intValue();
                                String formatPluralString = intValue < 60 ? LocaleController.formatPluralString("Seconds", intValue) : LocaleController.formatPluralString("Minutes", intValue / 60);
                                TwoStepVerificationActivity twoStepVerificationActivity = TwoStepVerificationActivity.this;
                                String string = LocaleController.getString("AppName", C0338R.string.AppName);
                                Object[] objArr = new Object[TwoStepVerificationActivity.done_button];
                                objArr[0] = formatPluralString;
                                twoStepVerificationActivity.showAlertWithText(string, LocaleController.formatString("FloodWaitTime", C0338R.string.FloodWaitTime, objArr));
                            } else {
                                TwoStepVerificationActivity.this.showAlertWithText(LocaleController.getString("AppName", C0338R.string.AppName), this.val$error.text);
                            }
                        }
                    }

                    public void run(TLObject tLObject, TL_error tL_error) {
                        AndroidUtilities.runOnUIThread(new C19291(tL_error));
                    }
                }, 10);
            }
        }
    }

    private void setNewPassword(boolean z) {
        TLObject tL_account_updatePasswordSettings = new TL_account_updatePasswordSettings();
        tL_account_updatePasswordSettings.current_password_hash = this.currentPasswordHash;
        tL_account_updatePasswordSettings.new_settings = new TL_account_passwordInputSettings();
        if (!z) {
            TL_account_passwordInputSettings tL_account_passwordInputSettings;
            if (this.firstPassword != null && this.firstPassword.length() > 0) {
                Object obj = null;
                try {
                    obj = this.firstPassword.getBytes(C0700C.UTF8_NAME);
                } catch (Throwable e) {
                    FileLog.m18e("tmessages", e);
                }
                Object obj2 = this.currentPassword.new_salt;
                Object obj3 = new byte[((obj2.length * 2) + obj.length)];
                System.arraycopy(obj2, 0, obj3, 0, obj2.length);
                System.arraycopy(obj, 0, obj3, obj2.length, obj.length);
                System.arraycopy(obj2, 0, obj3, obj3.length - obj2.length, obj2.length);
                tL_account_passwordInputSettings = tL_account_updatePasswordSettings.new_settings;
                tL_account_passwordInputSettings.flags |= done_button;
                tL_account_updatePasswordSettings.new_settings.hint = this.hint;
                tL_account_updatePasswordSettings.new_settings.new_password_hash = Utilities.computeSHA256(obj3, 0, obj3.length);
                tL_account_updatePasswordSettings.new_settings.new_salt = obj2;
            }
            if (this.email.length() > 0) {
                tL_account_passwordInputSettings = tL_account_updatePasswordSettings.new_settings;
                tL_account_passwordInputSettings.flags |= 2;
                tL_account_updatePasswordSettings.new_settings.email = this.email;
            }
        } else if (this.waitingForEmail && (this.currentPassword instanceof TL_account_noPassword)) {
            tL_account_updatePasswordSettings.new_settings.flags = 2;
            tL_account_updatePasswordSettings.new_settings.email = TtmlNode.ANONYMOUS_REGION_ID;
            tL_account_updatePasswordSettings.current_password_hash = new byte[0];
        } else {
            tL_account_updatePasswordSettings.new_settings.flags = 3;
            tL_account_updatePasswordSettings.new_settings.hint = TtmlNode.ANONYMOUS_REGION_ID;
            tL_account_updatePasswordSettings.new_settings.new_password_hash = new byte[0];
            tL_account_updatePasswordSettings.new_settings.new_salt = new byte[0];
            tL_account_updatePasswordSettings.new_settings.email = TtmlNode.ANONYMOUS_REGION_ID;
        }
        needShowProgress();
        ConnectionsManager.getInstance().sendRequest(tL_account_updatePasswordSettings, new AnonymousClass10(z, tL_account_updatePasswordSettings), 10);
    }

    private void setPasswordSetState(int i) {
        int i2 = 4;
        if (this.passwordEditText != null) {
            this.passwordSetState = i;
            if (this.passwordSetState == 0) {
                this.actionBar.setTitle(LocaleController.getString("YourPassword", C0338R.string.YourPassword));
                if (this.currentPassword instanceof TL_account_noPassword) {
                    this.titleTextView.setText(LocaleController.getString("PleaseEnterFirstPassword", C0338R.string.PleaseEnterFirstPassword));
                } else {
                    this.titleTextView.setText(LocaleController.getString("PleaseEnterPassword", C0338R.string.PleaseEnterPassword));
                }
                this.passwordEditText.setImeOptions(5);
                this.passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                this.bottomTextView.setVisibility(4);
                this.bottomButton.setVisibility(4);
            } else if (this.passwordSetState == done_button) {
                this.actionBar.setTitle(LocaleController.getString("YourPassword", C0338R.string.YourPassword));
                this.titleTextView.setText(LocaleController.getString("PleaseReEnterPassword", C0338R.string.PleaseReEnterPassword));
                this.passwordEditText.setImeOptions(5);
                this.passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                this.bottomTextView.setVisibility(4);
                this.bottomButton.setVisibility(4);
            } else if (this.passwordSetState == 2) {
                this.actionBar.setTitle(LocaleController.getString("PasswordHint", C0338R.string.PasswordHint));
                this.titleTextView.setText(LocaleController.getString("PasswordHintText", C0338R.string.PasswordHintText));
                this.passwordEditText.setImeOptions(5);
                this.passwordEditText.setTransformationMethod(null);
                this.bottomTextView.setVisibility(4);
                this.bottomButton.setVisibility(4);
            } else if (this.passwordSetState == 3) {
                this.actionBar.setTitle(LocaleController.getString("RecoveryEmail", C0338R.string.RecoveryEmail));
                this.titleTextView.setText(LocaleController.getString("YourEmail", C0338R.string.YourEmail));
                this.passwordEditText.setImeOptions(6);
                this.passwordEditText.setTransformationMethod(null);
                this.passwordEditText.setInputType(33);
                this.bottomTextView.setVisibility(0);
                TextView textView = this.bottomButton;
                if (!this.emailOnly) {
                    i2 = 0;
                }
                textView.setVisibility(i2);
            } else if (this.passwordSetState == 4) {
                this.actionBar.setTitle(LocaleController.getString("PasswordRecovery", C0338R.string.PasswordRecovery));
                this.titleTextView.setText(LocaleController.getString("PasswordCode", C0338R.string.PasswordCode));
                this.bottomTextView.setText(LocaleController.getString("RestoreEmailSentInfo", C0338R.string.RestoreEmailSentInfo));
                TextView textView2 = this.bottomButton;
                Object[] objArr = new Object[done_button];
                objArr[0] = this.currentPassword.email_unconfirmed_pattern;
                textView2.setText(LocaleController.formatString("RestoreEmailTrouble", C0338R.string.RestoreEmailTrouble, objArr));
                this.passwordEditText.setImeOptions(6);
                this.passwordEditText.setTransformationMethod(null);
                this.passwordEditText.setInputType(3);
                this.bottomTextView.setVisibility(0);
                this.bottomButton.setVisibility(0);
            }
            this.passwordEditText.setText(TtmlNode.ANONYMOUS_REGION_ID);
        }
    }

    private void showAlertWithText(String str, String str2) {
        Builder builder = new Builder(getParentActivity());
        builder.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), null);
        builder.setTitle(str);
        builder.setMessage(str2);
        showDialog(builder.create());
    }

    private void updateRows() {
        this.rowCount = 0;
        this.setPasswordRow = -1;
        this.setPasswordDetailRow = -1;
        this.changePasswordRow = -1;
        this.turnPasswordOffRow = -1;
        this.setRecoveryEmailRow = -1;
        this.changeRecoveryEmailRow = -1;
        this.abortPasswordRow = -1;
        this.passwordSetupDetailRow = -1;
        this.passwordEnabledDetailRow = -1;
        this.passwordEmailVerifyDetailRow = -1;
        this.shadowRow = -1;
        if (!(this.loading || this.currentPassword == null)) {
            int i;
            if (this.currentPassword instanceof TL_account_noPassword) {
                if (this.waitingForEmail) {
                    i = this.rowCount;
                    this.rowCount = i + done_button;
                    this.passwordSetupDetailRow = i;
                    i = this.rowCount;
                    this.rowCount = i + done_button;
                    this.abortPasswordRow = i;
                    i = this.rowCount;
                    this.rowCount = i + done_button;
                    this.shadowRow = i;
                } else {
                    i = this.rowCount;
                    this.rowCount = i + done_button;
                    this.setPasswordRow = i;
                    i = this.rowCount;
                    this.rowCount = i + done_button;
                    this.setPasswordDetailRow = i;
                }
            } else if (this.currentPassword instanceof TL_account_password) {
                i = this.rowCount;
                this.rowCount = i + done_button;
                this.changePasswordRow = i;
                i = this.rowCount;
                this.rowCount = i + done_button;
                this.turnPasswordOffRow = i;
                if (this.currentPassword.has_recovery) {
                    i = this.rowCount;
                    this.rowCount = i + done_button;
                    this.changeRecoveryEmailRow = i;
                } else {
                    i = this.rowCount;
                    this.rowCount = i + done_button;
                    this.setRecoveryEmailRow = i;
                }
                if (this.waitingForEmail) {
                    i = this.rowCount;
                    this.rowCount = i + done_button;
                    this.passwordEmailVerifyDetailRow = i;
                } else {
                    i = this.rowCount;
                    this.rowCount = i + done_button;
                    this.passwordEnabledDetailRow = i;
                }
            }
        }
        if (this.listAdapter != null) {
            this.listAdapter.notifyDataSetChanged();
        }
        if (this.passwordEntered) {
            if (this.listView != null) {
                this.listView.setVisibility(0);
                this.scrollView.setVisibility(4);
                this.progressView.setVisibility(0);
                this.listView.setEmptyView(this.progressView);
            }
            if (this.passwordEditText != null) {
                this.doneItem.setVisibility(8);
                this.passwordEditText.setVisibility(4);
                this.titleTextView.setVisibility(4);
                this.bottomTextView.setVisibility(4);
                this.bottomButton.setVisibility(4);
                return;
            }
            return;
        }
        if (this.listView != null) {
            this.listView.setEmptyView(null);
            this.listView.setVisibility(4);
            this.scrollView.setVisibility(0);
            this.progressView.setVisibility(4);
        }
        if (this.passwordEditText != null) {
            this.doneItem.setVisibility(0);
            this.passwordEditText.setVisibility(0);
            this.titleTextView.setVisibility(0);
            this.bottomButton.setVisibility(0);
            this.bottomTextView.setVisibility(4);
            this.bottomButton.setText(LocaleController.getString("ForgotPassword", C0338R.string.ForgotPassword));
            if (this.currentPassword.hint == null || this.currentPassword.hint.length() <= 0) {
                this.passwordEditText.setHint(TtmlNode.ANONYMOUS_REGION_ID);
            } else {
                this.passwordEditText.setHint(this.currentPassword.hint);
            }
            AndroidUtilities.runOnUIThread(new C19459(), 200);
        }
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(C0338R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(false);
        this.actionBar.setActionBarMenuOnItemClick(new C19301());
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        frameLayout.setBackgroundColor(Theme.ACTION_BAR_MODE_SELECTOR_COLOR);
        ActionBarMenu createMenu = this.actionBar.createMenu();
        this.doneItem = createMenu.addItemWithWidth((int) done_button, (int) C0338R.drawable.ic_done, AndroidUtilities.dp(56.0f));
        if (ThemeUtil.m2490b()) {
            Drawable drawable = getParentActivity().getResources().getDrawable(C0338R.drawable.ic_done);
            drawable.setColorFilter(AdvanceTheme.f2492c, Mode.MULTIPLY);
            this.doneItem = createMenu.addItemWithWidth((int) done_button, drawable, AndroidUtilities.dp(56.0f));
        } else {
            this.doneItem = createMenu.addItemWithWidth((int) done_button, (int) C0338R.drawable.ic_done, AndroidUtilities.dp(56.0f));
        }
        this.scrollView = new ScrollView(context);
        this.scrollView.setFillViewport(true);
        frameLayout.addView(this.scrollView);
        LayoutParams layoutParams = (LayoutParams) this.scrollView.getLayoutParams();
        layoutParams.width = -1;
        layoutParams.height = -1;
        this.scrollView.setLayoutParams(layoutParams);
        View linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(done_button);
        this.scrollView.addView(linearLayout);
        layoutParams = (LayoutParams) linearLayout.getLayoutParams();
        layoutParams.width = -1;
        layoutParams.height = -2;
        linearLayout.setLayoutParams(layoutParams);
        this.titleTextView = new TextView(context);
        this.titleTextView.setTextColor(Theme.ATTACH_SHEET_TEXT_COLOR);
        this.titleTextView.setTextSize(done_button, 18.0f);
        this.titleTextView.setGravity(done_button);
        this.titleTextView.setTypeface(FontUtil.m1176a().m1161d());
        linearLayout.addView(this.titleTextView);
        LinearLayout.LayoutParams layoutParams2 = (LinearLayout.LayoutParams) this.titleTextView.getLayoutParams();
        layoutParams2.width = -2;
        layoutParams2.height = -2;
        layoutParams2.gravity = done_button;
        layoutParams2.topMargin = AndroidUtilities.dp(38.0f);
        this.titleTextView.setLayoutParams(layoutParams2);
        this.passwordEditText = new EditText(context);
        this.passwordEditText.setTextSize(done_button, 20.0f);
        this.passwordEditText.setTextColor(Theme.MSG_TEXT_COLOR);
        this.passwordEditText.setMaxLines(done_button);
        this.passwordEditText.setLines(done_button);
        this.passwordEditText.setGravity(done_button);
        this.passwordEditText.setSingleLine(true);
        this.passwordEditText.setInputType(129);
        this.passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
        this.passwordEditText.setTypeface(Typeface.DEFAULT);
        AndroidUtilities.clearCursorDrawable(this.passwordEditText);
        linearLayout.addView(this.passwordEditText);
        layoutParams2 = (LinearLayout.LayoutParams) this.passwordEditText.getLayoutParams();
        layoutParams2.topMargin = AndroidUtilities.dp(32.0f);
        layoutParams2.height = AndroidUtilities.dp(36.0f);
        layoutParams2.leftMargin = AndroidUtilities.dp(40.0f);
        layoutParams2.rightMargin = AndroidUtilities.dp(40.0f);
        layoutParams2.gravity = 51;
        layoutParams2.width = -1;
        this.passwordEditText.setLayoutParams(layoutParams2);
        this.passwordEditText.setOnEditorActionListener(new C19312());
        this.passwordEditText.setCustomSelectionActionModeCallback(new C19323());
        this.bottomTextView = new TextView(context);
        this.bottomTextView.setTextColor(Theme.ATTACH_SHEET_TEXT_COLOR);
        this.bottomTextView.setTextSize(done_button, 14.0f);
        this.bottomTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        this.bottomTextView.setText(LocaleController.getString("YourEmailInfo", C0338R.string.YourEmailInfo));
        linearLayout.addView(this.bottomTextView);
        layoutParams2 = (LinearLayout.LayoutParams) this.bottomTextView.getLayoutParams();
        layoutParams2.width = -2;
        layoutParams2.height = -2;
        layoutParams2.gravity = (LocaleController.isRTL ? 5 : 3) | 48;
        layoutParams2.topMargin = AndroidUtilities.dp(BitmapDescriptorFactory.HUE_ORANGE);
        layoutParams2.leftMargin = AndroidUtilities.dp(40.0f);
        layoutParams2.rightMargin = AndroidUtilities.dp(40.0f);
        this.bottomTextView.setLayoutParams(layoutParams2);
        View linearLayout2 = new LinearLayout(context);
        linearLayout2.setGravity(80);
        linearLayout.addView(linearLayout2);
        layoutParams2 = (LinearLayout.LayoutParams) linearLayout2.getLayoutParams();
        layoutParams2.width = -1;
        layoutParams2.height = -1;
        linearLayout2.setLayoutParams(layoutParams2);
        this.bottomButton = new TextView(context);
        this.bottomButton.setTextColor(Theme.DIALOGS_PRINTING_TEXT_COLOR);
        this.bottomButton.setTextSize(done_button, 14.0f);
        this.bottomButton.setGravity((LocaleController.isRTL ? 5 : 3) | 80);
        this.bottomButton.setText(LocaleController.getString("YourEmailSkip", C0338R.string.YourEmailSkip));
        this.bottomButton.setPadding(0, AndroidUtilities.dp(10.0f), 0, 0);
        linearLayout2.addView(this.bottomButton);
        layoutParams2 = (LinearLayout.LayoutParams) this.bottomButton.getLayoutParams();
        layoutParams2.width = -2;
        layoutParams2.height = -2;
        layoutParams2.gravity = (LocaleController.isRTL ? 5 : 3) | 80;
        layoutParams2.bottomMargin = AndroidUtilities.dp(14.0f);
        layoutParams2.leftMargin = AndroidUtilities.dp(40.0f);
        layoutParams2.rightMargin = AndroidUtilities.dp(40.0f);
        this.bottomButton.setLayoutParams(layoutParams2);
        this.bottomButton.setOnClickListener(new C19374());
        if (this.type == 0) {
            this.progressView = new FrameLayout(context);
            frameLayout.addView(this.progressView);
            layoutParams = (LayoutParams) this.progressView.getLayoutParams();
            layoutParams.width = -1;
            layoutParams.height = -1;
            this.progressView.setLayoutParams(layoutParams);
            this.progressView.setOnTouchListener(new C19385());
            this.progressView.addView(new ProgressBar(context));
            layoutParams = (LayoutParams) this.progressView.getLayoutParams();
            layoutParams.width = -2;
            layoutParams.height = -2;
            layoutParams.gravity = 17;
            this.progressView.setLayoutParams(layoutParams);
            this.listView = new ListView(context);
            initThemeBackground(this.listView);
            this.listView.setDivider(null);
            this.listView.setEmptyView(this.progressView);
            this.listView.setDividerHeight(0);
            this.listView.setVerticalScrollBarEnabled(false);
            this.listView.setDrawSelectorOnTop(true);
            frameLayout.addView(this.listView);
            LayoutParams layoutParams3 = (LayoutParams) this.listView.getLayoutParams();
            layoutParams3.width = -1;
            layoutParams3.height = -1;
            layoutParams3.gravity = 48;
            this.listView.setLayoutParams(layoutParams3);
            ListView listView = this.listView;
            android.widget.ListAdapter listAdapter = new ListAdapter(context);
            this.listAdapter = listAdapter;
            listView.setAdapter(listAdapter);
            this.listView.setOnItemClickListener(new C19406());
            updateRows();
            this.actionBar.setTitle(LocaleController.getString("TwoStepVerification", C0338R.string.TwoStepVerification));
            this.titleTextView.setText(LocaleController.getString("PleaseEnterCurrentPassword", C0338R.string.PleaseEnterCurrentPassword));
        } else if (this.type == done_button) {
            setPasswordSetState(this.passwordSetState);
        }
        return this.fragmentView;
    }

    public void didReceivedNotification(int i, Object... objArr) {
        if (i == NotificationCenter.didSetTwoStepPassword) {
            if (!(objArr == null || objArr.length <= 0 || objArr[0] == null)) {
                this.currentPasswordHash = (byte[]) objArr[0];
            }
            loadPasswordInfo(false);
            updateRows();
        }
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        updateRows();
        if (this.type == 0) {
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.didSetTwoStepPassword);
        }
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        if (this.type == 0) {
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.didSetTwoStepPassword);
            if (this.shortPollRunnable != null) {
                AndroidUtilities.cancelRunOnUIThread(this.shortPollRunnable);
                this.shortPollRunnable = null;
            }
            this.destroyed = true;
        }
        if (this.progressDialog != null) {
            try {
                this.progressDialog.dismiss();
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
            this.progressDialog = null;
        }
        AndroidUtilities.removeAdjustResize(getParentActivity(), this.classGuid);
    }

    public void onResume() {
        super.onResume();
        if (this.type == done_button) {
            AndroidUtilities.runOnUIThread(new C19417(), 200);
        }
        AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
        initThemeActionBar();
    }

    public void onTransitionAnimationEnd(boolean z, boolean z2) {
        if (z && this.type == done_button) {
            AndroidUtilities.showKeyboard(this.passwordEditText);
        }
    }
}
