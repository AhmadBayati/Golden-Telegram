package com.hanista.mobogram.ui;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.annotation.SuppressLint;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.view.PointerIconCompat;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputFilter.LengthFilter;
import android.text.TextUtils.TruncateAt;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import com.coremedia.iso.boxes.TrackReferenceTypeBox;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.vision.face.Face;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.PhoneFormat.PhoneFormat;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.ApplicationLoader;
import com.hanista.mobogram.messenger.BuildVars;
import com.hanista.mobogram.messenger.ContactsController;
import com.hanista.mobogram.messenger.FileLog;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.MessagesController;
import com.hanista.mobogram.messenger.MessagesStorage;
import com.hanista.mobogram.messenger.NotificationCenter;
import com.hanista.mobogram.messenger.NotificationCenter.NotificationCenterDelegate;
import com.hanista.mobogram.messenger.UserConfig;
import com.hanista.mobogram.messenger.Utilities;
import com.hanista.mobogram.messenger.exoplayer.C0700C;
import com.hanista.mobogram.messenger.exoplayer.DefaultLoadControl;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;
import com.hanista.mobogram.mobo.p008i.FontUtil;
import com.hanista.mobogram.mobo.p020s.AdvanceTheme;
import com.hanista.mobogram.mobo.p020s.ThemeUtil;
import com.hanista.mobogram.tgnet.ConnectionsManager;
import com.hanista.mobogram.tgnet.RequestDelegate;
import com.hanista.mobogram.tgnet.TLObject;
import com.hanista.mobogram.tgnet.TLRPC.TL_account_deleteAccount;
import com.hanista.mobogram.tgnet.TLRPC.TL_account_getPassword;
import com.hanista.mobogram.tgnet.TLRPC.TL_account_password;
import com.hanista.mobogram.tgnet.TLRPC.TL_auth_authorization;
import com.hanista.mobogram.tgnet.TLRPC.TL_auth_cancelCode;
import com.hanista.mobogram.tgnet.TLRPC.TL_auth_checkPassword;
import com.hanista.mobogram.tgnet.TLRPC.TL_auth_codeTypeCall;
import com.hanista.mobogram.tgnet.TLRPC.TL_auth_codeTypeFlashCall;
import com.hanista.mobogram.tgnet.TLRPC.TL_auth_codeTypeSms;
import com.hanista.mobogram.tgnet.TLRPC.TL_auth_importBotAuthorization;
import com.hanista.mobogram.tgnet.TLRPC.TL_auth_passwordRecovery;
import com.hanista.mobogram.tgnet.TLRPC.TL_auth_recoverPassword;
import com.hanista.mobogram.tgnet.TLRPC.TL_auth_requestPasswordRecovery;
import com.hanista.mobogram.tgnet.TLRPC.TL_auth_resendCode;
import com.hanista.mobogram.tgnet.TLRPC.TL_auth_sendCode;
import com.hanista.mobogram.tgnet.TLRPC.TL_auth_sentCode;
import com.hanista.mobogram.tgnet.TLRPC.TL_auth_sentCodeTypeApp;
import com.hanista.mobogram.tgnet.TLRPC.TL_auth_sentCodeTypeCall;
import com.hanista.mobogram.tgnet.TLRPC.TL_auth_sentCodeTypeFlashCall;
import com.hanista.mobogram.tgnet.TLRPC.TL_auth_sentCodeTypeSms;
import com.hanista.mobogram.tgnet.TLRPC.TL_auth_signIn;
import com.hanista.mobogram.tgnet.TLRPC.TL_auth_signUp;
import com.hanista.mobogram.tgnet.TLRPC.TL_error;
import com.hanista.mobogram.ui.ActionBar.ActionBar;
import com.hanista.mobogram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import com.hanista.mobogram.ui.ActionBar.BaseFragment;
import com.hanista.mobogram.ui.ActionBar.Theme;
import com.hanista.mobogram.ui.Components.HintEditText;
import com.hanista.mobogram.ui.Components.LayoutHelper;
import com.hanista.mobogram.ui.Components.SlideView;
import com.hanista.mobogram.ui.CountrySelectActivity.CountrySelectActivityDelegate;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;

public class LoginActivity extends BaseFragment {
    private static final int done_button = 1;
    private boolean checkPermissions;
    private int currentViewNum;
    private View doneButton;
    private boolean loginAsBot;
    private Dialog permissionsDialog;
    private ArrayList<String> permissionsItems;
    private ProgressDialog progressDialog;
    private SlideView[] views;

    /* renamed from: com.hanista.mobogram.ui.LoginActivity.1 */
    class C16281 extends ActionBarMenuOnItemClick {
        C16281() {
        }

        public void onItemClick(int i) {
            if (i == LoginActivity.done_button) {
                LoginActivity.this.views[LoginActivity.this.currentViewNum].onNextPressed();
            } else if (i == -1) {
                LoginActivity.this.onBackPressed();
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.LoginActivity.2 */
    class C16292 implements OnClickListener {
        final /* synthetic */ boolean val$banned;
        final /* synthetic */ String val$phoneNumber;

        C16292(boolean z, String str) {
            this.val$banned = z;
            this.val$phoneNumber = str;
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            try {
                PackageInfo packageInfo = ApplicationLoader.applicationContext.getPackageManager().getPackageInfo(ApplicationLoader.applicationContext.getPackageName(), 0);
                String format = String.format(Locale.US, "%s (%d)", new Object[]{packageInfo.versionName, Integer.valueOf(packageInfo.versionCode)});
                Intent intent = new Intent("android.intent.action.SEND");
                intent.setType("message/rfc822");
                String[] strArr = new String[LoginActivity.done_button];
                strArr[0] = "login@stel.com";
                intent.putExtra("android.intent.extra.EMAIL", strArr);
                if (this.val$banned) {
                    intent.putExtra("android.intent.extra.SUBJECT", "Banned phone number: " + this.val$phoneNumber);
                    intent.putExtra("android.intent.extra.TEXT", "I'm trying to use my mobile phone number: " + this.val$phoneNumber + "\nBut Telegram says it's banned. Please help.\n\nApp version: " + format + "\nOS version: SDK " + VERSION.SDK_INT + "\nDevice Name: " + Build.MANUFACTURER + Build.MODEL + "\nLocale: " + Locale.getDefault());
                } else {
                    intent.putExtra("android.intent.extra.SUBJECT", "Invalid phone number: " + this.val$phoneNumber);
                    intent.putExtra("android.intent.extra.TEXT", "I'm trying to use my mobile phone number: " + this.val$phoneNumber + "\nBut Telegram says it's invalid. Please help.\n\nApp version: " + format + "\nOS version: SDK " + VERSION.SDK_INT + "\nDevice Name: " + Build.MANUFACTURER + Build.MODEL + "\nLocale: " + Locale.getDefault());
                }
                LoginActivity.this.getParentActivity().startActivity(Intent.createChooser(intent, "Send email..."));
            } catch (Exception e) {
                LoginActivity.this.needShowAlert(LocaleController.getString("AppName", C0338R.string.AppName), LocaleController.getString("NoMailInstalled", C0338R.string.NoMailInstalled));
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.LoginActivity.3 */
    class C16303 implements AnimatorListener {
        final /* synthetic */ SlideView val$outView;

        C16303(SlideView slideView) {
            this.val$outView = slideView;
        }

        public void onAnimationCancel(Animator animator) {
        }

        @SuppressLint({"NewApi"})
        public void onAnimationEnd(Animator animator) {
            this.val$outView.setVisibility(8);
            this.val$outView.setX(0.0f);
        }

        public void onAnimationRepeat(Animator animator) {
        }

        public void onAnimationStart(Animator animator) {
        }
    }

    /* renamed from: com.hanista.mobogram.ui.LoginActivity.4 */
    class C16314 implements AnimatorListener {
        final /* synthetic */ SlideView val$newView;

        C16314(SlideView slideView) {
            this.val$newView = slideView;
        }

        public void onAnimationCancel(Animator animator) {
        }

        public void onAnimationEnd(Animator animator) {
        }

        public void onAnimationRepeat(Animator animator) {
        }

        public void onAnimationStart(Animator animator) {
            this.val$newView.setVisibility(0);
        }
    }

    public class LoginActivityPasswordView extends SlideView {
        private EditText codeField;
        private TextView confirmTextView;
        private Bundle currentParams;
        private byte[] current_salt;
        private String email_unconfirmed_pattern;
        private boolean has_recovery;
        private String hint;
        private boolean nextPressed;
        private String phoneCode;
        private String phoneHash;
        private String requestPhone;
        private TextView resetAccountButton;
        private TextView resetAccountText;

        /* renamed from: com.hanista.mobogram.ui.LoginActivity.LoginActivityPasswordView.1 */
        class C16321 implements OnEditorActionListener {
            final /* synthetic */ LoginActivity val$this$0;

            C16321(LoginActivity loginActivity) {
                this.val$this$0 = loginActivity;
            }

            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i != 5) {
                    return false;
                }
                LoginActivityPasswordView.this.onNextPressed();
                return true;
            }
        }

        /* renamed from: com.hanista.mobogram.ui.LoginActivity.LoginActivityPasswordView.2 */
        class C16362 implements View.OnClickListener {
            final /* synthetic */ LoginActivity val$this$0;

            /* renamed from: com.hanista.mobogram.ui.LoginActivity.LoginActivityPasswordView.2.1 */
            class C16351 implements RequestDelegate {

                /* renamed from: com.hanista.mobogram.ui.LoginActivity.LoginActivityPasswordView.2.1.1 */
                class C16341 implements Runnable {
                    final /* synthetic */ TL_error val$error;
                    final /* synthetic */ TLObject val$response;

                    /* renamed from: com.hanista.mobogram.ui.LoginActivity.LoginActivityPasswordView.2.1.1.1 */
                    class C16331 implements OnClickListener {
                        final /* synthetic */ TL_auth_passwordRecovery val$res;

                        C16331(TL_auth_passwordRecovery tL_auth_passwordRecovery) {
                            this.val$res = tL_auth_passwordRecovery;
                        }

                        public void onClick(DialogInterface dialogInterface, int i) {
                            Bundle bundle = new Bundle();
                            bundle.putString("email_unconfirmed_pattern", this.val$res.email_pattern);
                            LoginActivity.this.setPage(7, true, bundle, false);
                        }
                    }

                    C16341(TL_error tL_error, TLObject tLObject) {
                        this.val$error = tL_error;
                        this.val$response = tLObject;
                    }

                    public void run() {
                        LoginActivity.this.needHideProgress();
                        if (this.val$error == null) {
                            TL_auth_passwordRecovery tL_auth_passwordRecovery = (TL_auth_passwordRecovery) this.val$response;
                            Builder builder = new Builder(LoginActivity.this.getParentActivity());
                            Object[] objArr = new Object[LoginActivity.done_button];
                            objArr[0] = tL_auth_passwordRecovery.email_pattern;
                            builder.setMessage(LocaleController.formatString("RestoreEmailSent", C0338R.string.RestoreEmailSent, objArr));
                            builder.setTitle(LocaleController.getString("AppName", C0338R.string.AppName));
                            builder.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), new C16331(tL_auth_passwordRecovery));
                            Dialog showDialog = LoginActivity.this.showDialog(builder.create());
                            if (showDialog != null) {
                                showDialog.setCanceledOnTouchOutside(false);
                                showDialog.setCancelable(false);
                            }
                        } else if (this.val$error.text.startsWith("FLOOD_WAIT")) {
                            int intValue = Utilities.parseInt(this.val$error.text).intValue();
                            String formatPluralString = intValue < 60 ? LocaleController.formatPluralString("Seconds", intValue) : LocaleController.formatPluralString("Minutes", intValue / 60);
                            LoginActivity loginActivity = LoginActivity.this;
                            String string = LocaleController.getString("AppName", C0338R.string.AppName);
                            Object[] objArr2 = new Object[LoginActivity.done_button];
                            objArr2[0] = formatPluralString;
                            loginActivity.needShowAlert(string, LocaleController.formatString("FloodWaitTime", C0338R.string.FloodWaitTime, objArr2));
                        } else {
                            LoginActivity.this.needShowAlert(LocaleController.getString("AppName", C0338R.string.AppName), this.val$error.text);
                        }
                    }
                }

                C16351() {
                }

                public void run(TLObject tLObject, TL_error tL_error) {
                    AndroidUtilities.runOnUIThread(new C16341(tL_error, tLObject));
                }
            }

            C16362(LoginActivity loginActivity) {
                this.val$this$0 = loginActivity;
            }

            public void onClick(View view) {
                if (LoginActivityPasswordView.this.has_recovery) {
                    LoginActivity.this.needShowProgress();
                    ConnectionsManager.getInstance().sendRequest(new TL_auth_requestPasswordRecovery(), new C16351(), 10);
                    return;
                }
                LoginActivityPasswordView.this.resetAccountText.setVisibility(0);
                LoginActivityPasswordView.this.resetAccountButton.setVisibility(0);
                AndroidUtilities.hideKeyboard(LoginActivityPasswordView.this.codeField);
                LoginActivity.this.needShowAlert(LocaleController.getString("RestorePasswordNoEmailTitle", C0338R.string.RestorePasswordNoEmailTitle), LocaleController.getString("RestorePasswordNoEmailText", C0338R.string.RestorePasswordNoEmailText));
            }
        }

        /* renamed from: com.hanista.mobogram.ui.LoginActivity.LoginActivityPasswordView.3 */
        class C16403 implements View.OnClickListener {
            final /* synthetic */ LoginActivity val$this$0;

            /* renamed from: com.hanista.mobogram.ui.LoginActivity.LoginActivityPasswordView.3.1 */
            class C16391 implements OnClickListener {

                /* renamed from: com.hanista.mobogram.ui.LoginActivity.LoginActivityPasswordView.3.1.1 */
                class C16381 implements RequestDelegate {

                    /* renamed from: com.hanista.mobogram.ui.LoginActivity.LoginActivityPasswordView.3.1.1.1 */
                    class C16371 implements Runnable {
                        final /* synthetic */ TL_error val$error;

                        C16371(TL_error tL_error) {
                            this.val$error = tL_error;
                        }

                        public void run() {
                            LoginActivity.this.needHideProgress();
                            Bundle bundle;
                            if (this.val$error == null) {
                                bundle = new Bundle();
                                bundle.putString("phoneFormated", LoginActivityPasswordView.this.requestPhone);
                                bundle.putString("phoneHash", LoginActivityPasswordView.this.phoneHash);
                                bundle.putString("code", LoginActivityPasswordView.this.phoneCode);
                                LoginActivity.this.setPage(5, true, bundle, false);
                            } else if (this.val$error.text.equals("2FA_RECENT_CONFIRM")) {
                                LoginActivity.this.needShowAlert(LocaleController.getString("AppName", C0338R.string.AppName), LocaleController.getString("ResetAccountCancelledAlert", C0338R.string.ResetAccountCancelledAlert));
                            } else if (this.val$error.text.startsWith("2FA_CONFIRM_WAIT_")) {
                                bundle = new Bundle();
                                bundle.putString("phoneFormated", LoginActivityPasswordView.this.requestPhone);
                                bundle.putString("phoneHash", LoginActivityPasswordView.this.phoneHash);
                                bundle.putString("code", LoginActivityPasswordView.this.phoneCode);
                                bundle.putInt("startTime", ConnectionsManager.getInstance().getCurrentTime());
                                bundle.putInt("waitTime", Utilities.parseInt(this.val$error.text.replace("2FA_CONFIRM_WAIT_", TtmlNode.ANONYMOUS_REGION_ID)).intValue());
                                LoginActivity.this.setPage(8, true, bundle, false);
                            } else {
                                LoginActivity.this.needShowAlert(LocaleController.getString("AppName", C0338R.string.AppName), this.val$error.text);
                            }
                        }
                    }

                    C16381() {
                    }

                    public void run(TLObject tLObject, TL_error tL_error) {
                        AndroidUtilities.runOnUIThread(new C16371(tL_error));
                    }
                }

                C16391() {
                }

                public void onClick(DialogInterface dialogInterface, int i) {
                    LoginActivity.this.needShowProgress();
                    TLObject tL_account_deleteAccount = new TL_account_deleteAccount();
                    tL_account_deleteAccount.reason = "Forgot password";
                    ConnectionsManager.getInstance().sendRequest(tL_account_deleteAccount, new C16381(), 10);
                }
            }

            C16403(LoginActivity loginActivity) {
                this.val$this$0 = loginActivity;
            }

            public void onClick(View view) {
                Builder builder = new Builder(LoginActivity.this.getParentActivity());
                builder.setMessage(LocaleController.getString("ResetMyAccountWarningText", C0338R.string.ResetMyAccountWarningText));
                builder.setTitle(LocaleController.getString("ResetMyAccountWarning", C0338R.string.ResetMyAccountWarning));
                builder.setPositiveButton(LocaleController.getString("ResetMyAccountWarningReset", C0338R.string.ResetMyAccountWarningReset), new C16391());
                builder.setNegativeButton(LocaleController.getString("Cancel", C0338R.string.Cancel), null);
                LoginActivity.this.showDialog(builder.create());
            }
        }

        /* renamed from: com.hanista.mobogram.ui.LoginActivity.LoginActivityPasswordView.4 */
        class C16424 implements RequestDelegate {

            /* renamed from: com.hanista.mobogram.ui.LoginActivity.LoginActivityPasswordView.4.1 */
            class C16411 implements Runnable {
                final /* synthetic */ TL_error val$error;
                final /* synthetic */ TLObject val$response;

                C16411(TL_error tL_error, TLObject tLObject) {
                    this.val$error = tL_error;
                    this.val$response = tLObject;
                }

                public void run() {
                    LoginActivity.this.needHideProgress();
                    LoginActivityPasswordView.this.nextPressed = false;
                    if (this.val$error == null) {
                        TL_auth_authorization tL_auth_authorization = (TL_auth_authorization) this.val$response;
                        ConnectionsManager.getInstance().setUserId(tL_auth_authorization.user.id);
                        UserConfig.clearConfig();
                        MessagesController.getInstance().cleanup();
                        UserConfig.setCurrentUser(tL_auth_authorization.user);
                        UserConfig.saveConfig(true);
                        MessagesStorage.getInstance().cleanup(true);
                        ArrayList arrayList = new ArrayList();
                        arrayList.add(tL_auth_authorization.user);
                        MessagesStorage.getInstance().putUsersAndChats(arrayList, null, true, true);
                        MessagesController.getInstance().putUser(tL_auth_authorization.user, false);
                        ContactsController.getInstance().checkAppAccount();
                        MessagesController.getInstance().getBlockedUsers(true);
                        LoginActivity.this.needFinishActivity();
                    } else if (this.val$error.text.equals("PASSWORD_HASH_INVALID")) {
                        LoginActivityPasswordView.this.onPasscodeError(true);
                    } else if (this.val$error.text.startsWith("FLOOD_WAIT")) {
                        int intValue = Utilities.parseInt(this.val$error.text).intValue();
                        String formatPluralString = intValue < 60 ? LocaleController.formatPluralString("Seconds", intValue) : LocaleController.formatPluralString("Minutes", intValue / 60);
                        LoginActivity loginActivity = LoginActivity.this;
                        String string = LocaleController.getString("AppName", C0338R.string.AppName);
                        Object[] objArr = new Object[LoginActivity.done_button];
                        objArr[0] = formatPluralString;
                        loginActivity.needShowAlert(string, LocaleController.formatString("FloodWaitTime", C0338R.string.FloodWaitTime, objArr));
                    } else {
                        LoginActivity.this.needShowAlert(LocaleController.getString("AppName", C0338R.string.AppName), this.val$error.text);
                    }
                }
            }

            C16424() {
            }

            public void run(TLObject tLObject, TL_error tL_error) {
                AndroidUtilities.runOnUIThread(new C16411(tL_error, tLObject));
            }
        }

        public LoginActivityPasswordView(Context context) {
            super(context);
            setOrientation(LoginActivity.done_button);
            this.confirmTextView = new TextView(context);
            this.confirmTextView.setTypeface(FontUtil.m1176a().m1161d());
            this.confirmTextView.setTextColor(Theme.ATTACH_SHEET_TEXT_COLOR);
            this.confirmTextView.setTextSize(LoginActivity.done_button, 14.0f);
            this.confirmTextView.setGravity(LocaleController.isRTL ? 5 : 3);
            this.confirmTextView.setLineSpacing((float) AndroidUtilities.dp(2.0f), DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            this.confirmTextView.setText(LocaleController.getString("LoginPasswordText", C0338R.string.LoginPasswordText));
            addView(this.confirmTextView, LayoutHelper.createLinear(-2, -2, LocaleController.isRTL ? 5 : 3));
            this.codeField = new EditText(context);
            this.codeField.setTextColor(Theme.STICKERS_SHEET_TITLE_TEXT_COLOR);
            AndroidUtilities.clearCursorDrawable(this.codeField);
            this.codeField.setHintTextColor(Theme.SHARE_SHEET_EDIT_PLACEHOLDER_TEXT_COLOR);
            this.codeField.setHint(LocaleController.getString("LoginPassword", C0338R.string.LoginPassword));
            this.codeField.setImeOptions(268435461);
            this.codeField.setTextSize(LoginActivity.done_button, 18.0f);
            this.codeField.setMaxLines(LoginActivity.done_button);
            this.codeField.setPadding(0, 0, 0, 0);
            this.codeField.setInputType(129);
            this.codeField.setTransformationMethod(PasswordTransformationMethod.getInstance());
            this.codeField.setTypeface(Typeface.DEFAULT);
            this.codeField.setGravity(LocaleController.isRTL ? 5 : 3);
            addView(this.codeField, LayoutHelper.createLinear(-1, 36, (int) LoginActivity.done_button, 0, 20, 0, 0));
            this.codeField.setOnEditorActionListener(new C16321(LoginActivity.this));
            View textView = new TextView(context);
            textView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
            textView.setTextColor(Theme.DIALOGS_PRINTING_TEXT_COLOR);
            textView.setText(LocaleController.getString("ForgotPassword", C0338R.string.ForgotPassword));
            textView.setTextSize(LoginActivity.done_button, 14.0f);
            textView.setLineSpacing((float) AndroidUtilities.dp(2.0f), DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            textView.setPadding(0, AndroidUtilities.dp(14.0f), 0, 0);
            addView(textView, LayoutHelper.createLinear(-2, -2, (LocaleController.isRTL ? 5 : 3) | 48));
            textView.setOnClickListener(new C16362(LoginActivity.this));
            this.resetAccountButton = new TextView(context);
            this.resetAccountButton.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
            this.resetAccountButton.setTextColor(-39322);
            this.resetAccountButton.setVisibility(8);
            this.resetAccountButton.setText(LocaleController.getString("ResetMyAccount", C0338R.string.ResetMyAccount));
            this.resetAccountButton.setTypeface(FontUtil.m1176a().m1160c());
            this.resetAccountButton.setTextSize(LoginActivity.done_button, 14.0f);
            this.resetAccountButton.setLineSpacing((float) AndroidUtilities.dp(2.0f), DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            this.resetAccountButton.setPadding(0, AndroidUtilities.dp(14.0f), 0, 0);
            addView(this.resetAccountButton, LayoutHelper.createLinear(-2, -2, (LocaleController.isRTL ? 5 : 3) | 48, 0, 34, 0, 0));
            this.resetAccountButton.setOnClickListener(new C16403(LoginActivity.this));
            this.resetAccountText = new TextView(context);
            this.resetAccountText.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
            this.resetAccountText.setVisibility(8);
            this.resetAccountText.setTextColor(Theme.ATTACH_SHEET_TEXT_COLOR);
            this.resetAccountText.setText(LocaleController.getString("ResetMyAccountText", C0338R.string.ResetMyAccountText));
            this.resetAccountText.setTextSize(LoginActivity.done_button, 14.0f);
            this.resetAccountText.setLineSpacing((float) AndroidUtilities.dp(2.0f), DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            addView(this.resetAccountText, LayoutHelper.createLinear(-2, -2, (LocaleController.isRTL ? 5 : 3) | 48, 0, 7, 0, 14));
        }

        private void onPasscodeError(boolean z) {
            if (LoginActivity.this.getParentActivity() != null) {
                Vibrator vibrator = (Vibrator) LoginActivity.this.getParentActivity().getSystemService("vibrator");
                if (vibrator != null) {
                    vibrator.vibrate(200);
                }
                if (z) {
                    this.codeField.setText(TtmlNode.ANONYMOUS_REGION_ID);
                }
                AndroidUtilities.shakeView(this.confirmTextView, 2.0f, 0);
            }
        }

        public String getHeaderName() {
            return LocaleController.getString("LoginPassword", C0338R.string.LoginPassword);
        }

        public boolean needBackButton() {
            return true;
        }

        public void onBackPressed() {
            this.currentParams = null;
        }

        public void onNextPressed() {
            if (!this.nextPressed) {
                String obj = this.codeField.getText().toString();
                if (obj.length() == 0) {
                    onPasscodeError(false);
                    return;
                }
                this.nextPressed = true;
                Object obj2 = null;
                try {
                    obj2 = obj.getBytes(C0700C.UTF8_NAME);
                } catch (Throwable e) {
                    FileLog.m18e("tmessages", e);
                }
                LoginActivity.this.needShowProgress();
                Object obj3 = new byte[((this.current_salt.length * 2) + obj2.length)];
                System.arraycopy(this.current_salt, 0, obj3, 0, this.current_salt.length);
                System.arraycopy(obj2, 0, obj3, this.current_salt.length, obj2.length);
                System.arraycopy(this.current_salt, 0, obj3, obj3.length - this.current_salt.length, this.current_salt.length);
                TLObject tL_auth_checkPassword = new TL_auth_checkPassword();
                tL_auth_checkPassword.password_hash = Utilities.computeSHA256(obj3, 0, obj3.length);
                ConnectionsManager.getInstance().sendRequest(tL_auth_checkPassword, new C16424(), 10);
            }
        }

        public void onShow() {
            super.onShow();
            if (this.codeField != null) {
                this.codeField.requestFocus();
                this.codeField.setSelection(this.codeField.length());
                AndroidUtilities.showKeyboard(this.codeField);
            }
        }

        public void restoreStateParams(Bundle bundle) {
            this.currentParams = bundle.getBundle("passview_params");
            if (this.currentParams != null) {
                setParams(this.currentParams);
            }
            CharSequence string = bundle.getString("passview_code");
            if (string != null) {
                this.codeField.setText(string);
            }
        }

        public void saveStateParams(Bundle bundle) {
            String obj = this.codeField.getText().toString();
            if (obj.length() != 0) {
                bundle.putString("passview_code", obj);
            }
            if (this.currentParams != null) {
                bundle.putBundle("passview_params", this.currentParams);
            }
        }

        public void setParams(Bundle bundle) {
            boolean z = true;
            if (bundle != null) {
                if (bundle.isEmpty()) {
                    this.resetAccountButton.setVisibility(0);
                    this.resetAccountText.setVisibility(0);
                    AndroidUtilities.hideKeyboard(this.codeField);
                    return;
                }
                this.resetAccountButton.setVisibility(8);
                this.resetAccountText.setVisibility(8);
                this.codeField.setText(TtmlNode.ANONYMOUS_REGION_ID);
                this.currentParams = bundle;
                this.current_salt = Utilities.hexToBytes(this.currentParams.getString("current_salt"));
                this.hint = this.currentParams.getString(TrackReferenceTypeBox.TYPE1);
                if (this.currentParams.getInt("has_recovery") != LoginActivity.done_button) {
                    z = false;
                }
                this.has_recovery = z;
                this.email_unconfirmed_pattern = this.currentParams.getString("email_unconfirmed_pattern");
                this.requestPhone = bundle.getString("phoneFormated");
                this.phoneHash = bundle.getString("phoneHash");
                this.phoneCode = bundle.getString("code");
                if (this.hint == null || this.hint.length() <= 0) {
                    this.codeField.setHint(LocaleController.getString("LoginPassword", C0338R.string.LoginPassword));
                } else {
                    this.codeField.setHint(this.hint);
                }
            }
        }
    }

    public class LoginActivityRecoverView extends SlideView {
        private TextView cancelButton;
        private EditText codeField;
        private TextView confirmTextView;
        private Bundle currentParams;
        private String email_unconfirmed_pattern;
        private boolean nextPressed;
        final /* synthetic */ LoginActivity this$0;

        /* renamed from: com.hanista.mobogram.ui.LoginActivity.LoginActivityRecoverView.1 */
        class C16431 implements OnEditorActionListener {
            final /* synthetic */ LoginActivity val$this$0;

            C16431(LoginActivity loginActivity) {
                this.val$this$0 = loginActivity;
            }

            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i != 5) {
                    return false;
                }
                LoginActivityRecoverView.this.onNextPressed();
                return true;
            }
        }

        /* renamed from: com.hanista.mobogram.ui.LoginActivity.LoginActivityRecoverView.2 */
        class C16452 implements View.OnClickListener {
            final /* synthetic */ LoginActivity val$this$0;

            /* renamed from: com.hanista.mobogram.ui.LoginActivity.LoginActivityRecoverView.2.1 */
            class C16441 implements OnClickListener {
                C16441() {
                }

                public void onClick(DialogInterface dialogInterface, int i) {
                    LoginActivityRecoverView.this.this$0.setPage(6, true, new Bundle(), true);
                }
            }

            C16452(LoginActivity loginActivity) {
                this.val$this$0 = loginActivity;
            }

            public void onClick(View view) {
                Builder builder = new Builder(LoginActivityRecoverView.this.this$0.getParentActivity());
                builder.setMessage(LocaleController.getString("RestoreEmailTroubleText", C0338R.string.RestoreEmailTroubleText));
                builder.setTitle(LocaleController.getString("RestorePasswordNoEmailTitle", C0338R.string.RestorePasswordNoEmailTitle));
                builder.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), new C16441());
                Dialog showDialog = LoginActivityRecoverView.this.this$0.showDialog(builder.create());
                if (showDialog != null) {
                    showDialog.setCanceledOnTouchOutside(false);
                    showDialog.setCancelable(false);
                }
            }
        }

        /* renamed from: com.hanista.mobogram.ui.LoginActivity.LoginActivityRecoverView.3 */
        class C16473 implements RequestDelegate {

            /* renamed from: com.hanista.mobogram.ui.LoginActivity.LoginActivityRecoverView.3.1 */
            class C16461 implements Runnable {
                final /* synthetic */ TL_error val$error;
                final /* synthetic */ TLObject val$response;

                C16461(TL_error tL_error, TLObject tLObject) {
                    this.val$error = tL_error;
                    this.val$response = tLObject;
                }

                public void run() {
                    LoginActivityRecoverView.this.this$0.needHideProgress();
                    LoginActivityRecoverView.this.nextPressed = false;
                    if (this.val$error == null) {
                        TL_auth_authorization tL_auth_authorization = (TL_auth_authorization) this.val$response;
                        ConnectionsManager.getInstance().setUserId(tL_auth_authorization.user.id);
                        UserConfig.clearConfig();
                        MessagesController.getInstance().cleanup();
                        UserConfig.setCurrentUser(tL_auth_authorization.user);
                        UserConfig.saveConfig(true);
                        MessagesStorage.getInstance().cleanup(true);
                        ArrayList arrayList = new ArrayList();
                        arrayList.add(tL_auth_authorization.user);
                        MessagesStorage.getInstance().putUsersAndChats(arrayList, null, true, true);
                        MessagesController.getInstance().putUser(tL_auth_authorization.user, false);
                        ContactsController.getInstance().checkAppAccount();
                        MessagesController.getInstance().getBlockedUsers(true);
                        LoginActivityRecoverView.this.this$0.needFinishActivity();
                    } else if (this.val$error.text.startsWith("CODE_INVALID")) {
                        LoginActivityRecoverView.this.onPasscodeError(true);
                    } else if (this.val$error.text.startsWith("FLOOD_WAIT")) {
                        int intValue = Utilities.parseInt(this.val$error.text).intValue();
                        String formatPluralString = intValue < 60 ? LocaleController.formatPluralString("Seconds", intValue) : LocaleController.formatPluralString("Minutes", intValue / 60);
                        LoginActivity loginActivity = LoginActivityRecoverView.this.this$0;
                        String string = LocaleController.getString("AppName", C0338R.string.AppName);
                        Object[] objArr = new Object[LoginActivity.done_button];
                        objArr[0] = formatPluralString;
                        loginActivity.needShowAlert(string, LocaleController.formatString("FloodWaitTime", C0338R.string.FloodWaitTime, objArr));
                    } else {
                        LoginActivityRecoverView.this.this$0.needShowAlert(LocaleController.getString("AppName", C0338R.string.AppName), this.val$error.text);
                    }
                }
            }

            C16473() {
            }

            public void run(TLObject tLObject, TL_error tL_error) {
                AndroidUtilities.runOnUIThread(new C16461(tL_error, tLObject));
            }
        }

        public LoginActivityRecoverView(LoginActivity loginActivity, Context context) {
            int i = 5;
            this.this$0 = loginActivity;
            super(context);
            setOrientation(LoginActivity.done_button);
            this.confirmTextView = new TextView(context);
            this.confirmTextView.setTextColor(Theme.ATTACH_SHEET_TEXT_COLOR);
            this.confirmTextView.setTextSize(LoginActivity.done_button, 14.0f);
            this.confirmTextView.setGravity(LocaleController.isRTL ? 5 : 3);
            this.confirmTextView.setLineSpacing((float) AndroidUtilities.dp(2.0f), DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            this.confirmTextView.setText(LocaleController.getString("RestoreEmailSentInfo", C0338R.string.RestoreEmailSentInfo));
            addView(this.confirmTextView, LayoutHelper.createLinear(-2, -2, LocaleController.isRTL ? 5 : 3));
            this.codeField = new EditText(context);
            this.codeField.setTextColor(Theme.STICKERS_SHEET_TITLE_TEXT_COLOR);
            AndroidUtilities.clearCursorDrawable(this.codeField);
            this.codeField.setHintTextColor(Theme.SHARE_SHEET_EDIT_PLACEHOLDER_TEXT_COLOR);
            this.codeField.setHint(LocaleController.getString("PasswordCode", C0338R.string.PasswordCode));
            this.codeField.setImeOptions(268435461);
            this.codeField.setTextSize(LoginActivity.done_button, 18.0f);
            this.codeField.setMaxLines(LoginActivity.done_button);
            this.codeField.setPadding(0, 0, 0, 0);
            this.codeField.setInputType(3);
            this.codeField.setTransformationMethod(PasswordTransformationMethod.getInstance());
            this.codeField.setTypeface(Typeface.DEFAULT);
            this.codeField.setGravity(LocaleController.isRTL ? 5 : 3);
            addView(this.codeField, LayoutHelper.createLinear(-1, 36, (int) LoginActivity.done_button, 0, 20, 0, 0));
            this.codeField.setOnEditorActionListener(new C16431(loginActivity));
            this.cancelButton = new TextView(context);
            this.cancelButton.setGravity((LocaleController.isRTL ? 5 : 3) | 80);
            this.cancelButton.setTextColor(Theme.DIALOGS_PRINTING_TEXT_COLOR);
            this.cancelButton.setTextSize(LoginActivity.done_button, 14.0f);
            this.cancelButton.setLineSpacing((float) AndroidUtilities.dp(2.0f), DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            this.cancelButton.setPadding(0, AndroidUtilities.dp(14.0f), 0, 0);
            View view = this.cancelButton;
            if (!LocaleController.isRTL) {
                i = 3;
            }
            addView(view, LayoutHelper.createLinear(-2, -2, i | 80, 0, 0, 0, 14));
            this.cancelButton.setOnClickListener(new C16452(loginActivity));
        }

        private void onPasscodeError(boolean z) {
            if (this.this$0.getParentActivity() != null) {
                Vibrator vibrator = (Vibrator) this.this$0.getParentActivity().getSystemService("vibrator");
                if (vibrator != null) {
                    vibrator.vibrate(200);
                }
                if (z) {
                    this.codeField.setText(TtmlNode.ANONYMOUS_REGION_ID);
                }
                AndroidUtilities.shakeView(this.confirmTextView, 2.0f, 0);
            }
        }

        public String getHeaderName() {
            return LocaleController.getString("LoginPassword", C0338R.string.LoginPassword);
        }

        public boolean needBackButton() {
            return true;
        }

        public void onBackPressed() {
            this.currentParams = null;
        }

        public void onNextPressed() {
            if (!this.nextPressed) {
                if (this.codeField.getText().toString().length() == 0) {
                    onPasscodeError(false);
                    return;
                }
                this.nextPressed = true;
                String obj = this.codeField.getText().toString();
                if (obj.length() == 0) {
                    onPasscodeError(false);
                    return;
                }
                this.this$0.needShowProgress();
                TLObject tL_auth_recoverPassword = new TL_auth_recoverPassword();
                tL_auth_recoverPassword.code = obj;
                ConnectionsManager.getInstance().sendRequest(tL_auth_recoverPassword, new C16473(), 10);
            }
        }

        public void onShow() {
            super.onShow();
            if (this.codeField != null) {
                this.codeField.requestFocus();
                this.codeField.setSelection(this.codeField.length());
            }
        }

        public void restoreStateParams(Bundle bundle) {
            this.currentParams = bundle.getBundle("recoveryview_params");
            if (this.currentParams != null) {
                setParams(this.currentParams);
            }
            CharSequence string = bundle.getString("recoveryview_code");
            if (string != null) {
                this.codeField.setText(string);
            }
        }

        public void saveStateParams(Bundle bundle) {
            String obj = this.codeField.getText().toString();
            if (!(obj == null || obj.length() == 0)) {
                bundle.putString("recoveryview_code", obj);
            }
            if (this.currentParams != null) {
                bundle.putBundle("recoveryview_params", this.currentParams);
            }
        }

        public void setParams(Bundle bundle) {
            if (bundle != null) {
                this.codeField.setText(TtmlNode.ANONYMOUS_REGION_ID);
                this.currentParams = bundle;
                this.email_unconfirmed_pattern = this.currentParams.getString("email_unconfirmed_pattern");
                TextView textView = this.cancelButton;
                Object[] objArr = new Object[LoginActivity.done_button];
                objArr[0] = this.email_unconfirmed_pattern;
                textView.setText(LocaleController.formatString("RestoreEmailTrouble", C0338R.string.RestoreEmailTrouble, objArr));
                AndroidUtilities.showKeyboard(this.codeField);
                this.codeField.requestFocus();
            }
        }
    }

    public class LoginActivityRegisterView extends SlideView {
        private Bundle currentParams;
        private EditText firstNameField;
        private EditText lastNameField;
        private boolean nextPressed;
        private String phoneCode;
        private String phoneHash;
        private String requestPhone;

        /* renamed from: com.hanista.mobogram.ui.LoginActivity.LoginActivityRegisterView.1 */
        class C16481 implements OnEditorActionListener {
            final /* synthetic */ LoginActivity val$this$0;

            C16481(LoginActivity loginActivity) {
                this.val$this$0 = loginActivity;
            }

            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i != 5) {
                    return false;
                }
                LoginActivityRegisterView.this.lastNameField.requestFocus();
                return true;
            }
        }

        /* renamed from: com.hanista.mobogram.ui.LoginActivity.LoginActivityRegisterView.2 */
        class C16502 implements View.OnClickListener {
            final /* synthetic */ LoginActivity val$this$0;

            /* renamed from: com.hanista.mobogram.ui.LoginActivity.LoginActivityRegisterView.2.1 */
            class C16491 implements OnClickListener {
                C16491() {
                }

                public void onClick(DialogInterface dialogInterface, int i) {
                    LoginActivityRegisterView.this.onBackPressed();
                    LoginActivity.this.setPage(0, true, null, true);
                }
            }

            C16502(LoginActivity loginActivity) {
                this.val$this$0 = loginActivity;
            }

            public void onClick(View view) {
                Builder builder = new Builder(LoginActivity.this.getParentActivity());
                builder.setTitle(LocaleController.getString("AppName", C0338R.string.AppName));
                builder.setMessage(LocaleController.getString("AreYouSureRegistration", C0338R.string.AreYouSureRegistration));
                builder.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), new C16491());
                builder.setNegativeButton(LocaleController.getString("Cancel", C0338R.string.Cancel), null);
                LoginActivity.this.showDialog(builder.create());
            }
        }

        /* renamed from: com.hanista.mobogram.ui.LoginActivity.LoginActivityRegisterView.3 */
        class C16523 implements RequestDelegate {

            /* renamed from: com.hanista.mobogram.ui.LoginActivity.LoginActivityRegisterView.3.1 */
            class C16511 implements Runnable {
                final /* synthetic */ TL_error val$error;
                final /* synthetic */ TLObject val$response;

                C16511(TL_error tL_error, TLObject tLObject) {
                    this.val$error = tL_error;
                    this.val$response = tLObject;
                }

                public void run() {
                    LoginActivityRegisterView.this.nextPressed = false;
                    LoginActivity.this.needHideProgress();
                    if (this.val$error == null) {
                        TL_auth_authorization tL_auth_authorization = (TL_auth_authorization) this.val$response;
                        ConnectionsManager.getInstance().setUserId(tL_auth_authorization.user.id);
                        UserConfig.clearConfig();
                        MessagesController.getInstance().cleanup();
                        UserConfig.setCurrentUser(tL_auth_authorization.user);
                        UserConfig.saveConfig(true);
                        MessagesStorage.getInstance().cleanup(true);
                        ArrayList arrayList = new ArrayList();
                        arrayList.add(tL_auth_authorization.user);
                        MessagesStorage.getInstance().putUsersAndChats(arrayList, null, true, true);
                        MessagesController.getInstance().putUser(tL_auth_authorization.user, false);
                        ContactsController.getInstance().checkAppAccount();
                        MessagesController.getInstance().getBlockedUsers(true);
                        LoginActivity.this.needFinishActivity();
                    } else if (this.val$error.text.contains("PHONE_NUMBER_INVALID")) {
                        LoginActivity.this.needShowAlert(LocaleController.getString("AppName", C0338R.string.AppName), LocaleController.getString("InvalidPhoneNumber", C0338R.string.InvalidPhoneNumber));
                    } else if (this.val$error.text.contains("PHONE_CODE_EMPTY") || this.val$error.text.contains("PHONE_CODE_INVALID")) {
                        LoginActivity.this.needShowAlert(LocaleController.getString("AppName", C0338R.string.AppName), LocaleController.getString("InvalidCode", C0338R.string.InvalidCode));
                    } else if (this.val$error.text.contains("PHONE_CODE_EXPIRED")) {
                        LoginActivity.this.needShowAlert(LocaleController.getString("AppName", C0338R.string.AppName), LocaleController.getString("CodeExpired", C0338R.string.CodeExpired));
                    } else if (this.val$error.text.contains("FIRSTNAME_INVALID")) {
                        LoginActivity.this.needShowAlert(LocaleController.getString("AppName", C0338R.string.AppName), LocaleController.getString("InvalidFirstName", C0338R.string.InvalidFirstName));
                    } else if (this.val$error.text.contains("LASTNAME_INVALID")) {
                        LoginActivity.this.needShowAlert(LocaleController.getString("AppName", C0338R.string.AppName), LocaleController.getString("InvalidLastName", C0338R.string.InvalidLastName));
                    } else {
                        LoginActivity.this.needShowAlert(LocaleController.getString("AppName", C0338R.string.AppName), this.val$error.text);
                    }
                }
            }

            C16523() {
            }

            public void run(TLObject tLObject, TL_error tL_error) {
                AndroidUtilities.runOnUIThread(new C16511(tL_error, tLObject));
            }
        }

        public LoginActivityRegisterView(Context context) {
            super(context);
            this.nextPressed = false;
            setOrientation(LoginActivity.done_button);
            View textView = new TextView(context);
            textView.setTypeface(FontUtil.m1176a().m1161d());
            textView.setText(LocaleController.getString("RegisterText", C0338R.string.RegisterText));
            textView.setTextColor(Theme.ATTACH_SHEET_TEXT_COLOR);
            textView.setGravity(LocaleController.isRTL ? 5 : 3);
            textView.setTextSize(LoginActivity.done_button, 14.0f);
            addView(textView, LayoutHelper.createLinear(-2, -2, LocaleController.isRTL ? 5 : 3, 0, 8, 0, 0));
            this.firstNameField = new EditText(context);
            this.firstNameField.setHintTextColor(Theme.SHARE_SHEET_EDIT_PLACEHOLDER_TEXT_COLOR);
            this.firstNameField.setTextColor(Theme.STICKERS_SHEET_TITLE_TEXT_COLOR);
            AndroidUtilities.clearCursorDrawable(this.firstNameField);
            this.firstNameField.setHint(LocaleController.getString("FirstName", C0338R.string.FirstName));
            this.firstNameField.setImeOptions(268435461);
            this.firstNameField.setTextSize(LoginActivity.done_button, 18.0f);
            this.firstNameField.setMaxLines(LoginActivity.done_button);
            this.firstNameField.setInputType(MessagesController.UPDATE_MASK_CHANNEL);
            addView(this.firstNameField, LayoutHelper.createLinear(-1, 36, 0.0f, 26.0f, 0.0f, 0.0f));
            this.firstNameField.setOnEditorActionListener(new C16481(LoginActivity.this));
            this.lastNameField = new EditText(context);
            this.lastNameField.setHint(LocaleController.getString("LastName", C0338R.string.LastName));
            this.lastNameField.setHintTextColor(Theme.SHARE_SHEET_EDIT_PLACEHOLDER_TEXT_COLOR);
            this.lastNameField.setTextColor(Theme.STICKERS_SHEET_TITLE_TEXT_COLOR);
            AndroidUtilities.clearCursorDrawable(this.lastNameField);
            this.lastNameField.setImeOptions(268435461);
            this.lastNameField.setTextSize(LoginActivity.done_button, 18.0f);
            this.lastNameField.setMaxLines(LoginActivity.done_button);
            this.lastNameField.setInputType(MessagesController.UPDATE_MASK_CHANNEL);
            addView(this.lastNameField, LayoutHelper.createLinear(-1, 36, 0.0f, 10.0f, 0.0f, 0.0f));
            textView = new LinearLayout(context);
            textView.setGravity(80);
            addView(textView, LayoutHelper.createLinear(-1, -1));
            View textView2 = new TextView(context);
            textView2.setText(LocaleController.getString("CancelRegistration", C0338R.string.CancelRegistration));
            textView2.setGravity((LocaleController.isRTL ? 5 : 3) | LoginActivity.done_button);
            textView2.setTextColor(Theme.DIALOGS_PRINTING_TEXT_COLOR);
            textView2.setTextSize(LoginActivity.done_button, 14.0f);
            textView2.setLineSpacing((float) AndroidUtilities.dp(2.0f), DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            textView2.setPadding(0, AndroidUtilities.dp(24.0f), 0, 0);
            textView.addView(textView2, LayoutHelper.createLinear(-2, -2, (LocaleController.isRTL ? 5 : 3) | 80, 0, 0, 0, 10));
            textView2.setOnClickListener(new C16502(LoginActivity.this));
        }

        public String getHeaderName() {
            return LocaleController.getString("YourName", C0338R.string.YourName);
        }

        public void onBackPressed() {
            this.currentParams = null;
        }

        public void onNextPressed() {
            if (!this.nextPressed) {
                this.nextPressed = true;
                TLObject tL_auth_signUp = new TL_auth_signUp();
                tL_auth_signUp.phone_code = this.phoneCode;
                tL_auth_signUp.phone_code_hash = this.phoneHash;
                tL_auth_signUp.phone_number = this.requestPhone;
                tL_auth_signUp.first_name = this.firstNameField.getText().toString();
                tL_auth_signUp.last_name = this.lastNameField.getText().toString();
                LoginActivity.this.needShowProgress();
                ConnectionsManager.getInstance().sendRequest(tL_auth_signUp, new C16523(), 10);
            }
        }

        public void onShow() {
            super.onShow();
            if (this.firstNameField != null) {
                this.firstNameField.requestFocus();
                this.firstNameField.setSelection(this.firstNameField.length());
            }
        }

        public void restoreStateParams(Bundle bundle) {
            this.currentParams = bundle.getBundle("registerview_params");
            if (this.currentParams != null) {
                setParams(this.currentParams);
            }
            CharSequence string = bundle.getString("registerview_first");
            if (string != null) {
                this.firstNameField.setText(string);
            }
            string = bundle.getString("registerview_last");
            if (string != null) {
                this.lastNameField.setText(string);
            }
        }

        public void saveStateParams(Bundle bundle) {
            String obj = this.firstNameField.getText().toString();
            if (obj.length() != 0) {
                bundle.putString("registerview_first", obj);
            }
            obj = this.lastNameField.getText().toString();
            if (obj.length() != 0) {
                bundle.putString("registerview_last", obj);
            }
            if (this.currentParams != null) {
                bundle.putBundle("registerview_params", this.currentParams);
            }
        }

        public void setParams(Bundle bundle) {
            if (bundle != null) {
                this.firstNameField.setText(TtmlNode.ANONYMOUS_REGION_ID);
                this.lastNameField.setText(TtmlNode.ANONYMOUS_REGION_ID);
                this.requestPhone = bundle.getString("phoneFormated");
                this.phoneHash = bundle.getString("phoneHash");
                this.phoneCode = bundle.getString("code");
                this.currentParams = bundle;
            }
        }
    }

    public class LoginActivityResetWaitView extends SlideView {
        private TextView confirmTextView;
        private Bundle currentParams;
        private String phoneCode;
        private String phoneHash;
        private String requestPhone;
        private TextView resetAccountButton;
        private TextView resetAccountTime;
        private int startTime;
        final /* synthetic */ LoginActivity this$0;
        private Runnable timeRunnable;
        private int waitTime;

        /* renamed from: com.hanista.mobogram.ui.LoginActivity.LoginActivityResetWaitView.1 */
        class C16561 implements View.OnClickListener {
            final /* synthetic */ LoginActivity val$this$0;

            /* renamed from: com.hanista.mobogram.ui.LoginActivity.LoginActivityResetWaitView.1.1 */
            class C16551 implements OnClickListener {

                /* renamed from: com.hanista.mobogram.ui.LoginActivity.LoginActivityResetWaitView.1.1.1 */
                class C16541 implements RequestDelegate {

                    /* renamed from: com.hanista.mobogram.ui.LoginActivity.LoginActivityResetWaitView.1.1.1.1 */
                    class C16531 implements Runnable {
                        final /* synthetic */ TL_error val$error;

                        C16531(TL_error tL_error) {
                            this.val$error = tL_error;
                        }

                        public void run() {
                            LoginActivityResetWaitView.this.this$0.needHideProgress();
                            if (this.val$error == null) {
                                Bundle bundle = new Bundle();
                                bundle.putString("phoneFormated", LoginActivityResetWaitView.this.requestPhone);
                                bundle.putString("phoneHash", LoginActivityResetWaitView.this.phoneHash);
                                bundle.putString("code", LoginActivityResetWaitView.this.phoneCode);
                                LoginActivityResetWaitView.this.this$0.setPage(5, true, bundle, false);
                            } else if (this.val$error.text.equals("2FA_RECENT_CONFIRM")) {
                                LoginActivityResetWaitView.this.this$0.needShowAlert(LocaleController.getString("AppName", C0338R.string.AppName), LocaleController.getString("ResetAccountCancelledAlert", C0338R.string.ResetAccountCancelledAlert));
                            } else {
                                LoginActivityResetWaitView.this.this$0.needShowAlert(LocaleController.getString("AppName", C0338R.string.AppName), this.val$error.text);
                            }
                        }
                    }

                    C16541() {
                    }

                    public void run(TLObject tLObject, TL_error tL_error) {
                        AndroidUtilities.runOnUIThread(new C16531(tL_error));
                    }
                }

                C16551() {
                }

                public void onClick(DialogInterface dialogInterface, int i) {
                    LoginActivityResetWaitView.this.this$0.needShowProgress();
                    TLObject tL_account_deleteAccount = new TL_account_deleteAccount();
                    tL_account_deleteAccount.reason = "Forgot password";
                    ConnectionsManager.getInstance().sendRequest(tL_account_deleteAccount, new C16541(), 10);
                }
            }

            C16561(LoginActivity loginActivity) {
                this.val$this$0 = loginActivity;
            }

            public void onClick(View view) {
                if (Math.abs(ConnectionsManager.getInstance().getCurrentTime() - LoginActivityResetWaitView.this.startTime) >= LoginActivityResetWaitView.this.waitTime) {
                    Builder builder = new Builder(LoginActivityResetWaitView.this.this$0.getParentActivity());
                    builder.setMessage(LocaleController.getString("ResetMyAccountWarningText", C0338R.string.ResetMyAccountWarningText));
                    builder.setTitle(LocaleController.getString("ResetMyAccountWarning", C0338R.string.ResetMyAccountWarning));
                    builder.setPositiveButton(LocaleController.getString("ResetMyAccountWarningReset", C0338R.string.ResetMyAccountWarningReset), new C16551());
                    builder.setNegativeButton(LocaleController.getString("Cancel", C0338R.string.Cancel), null);
                    LoginActivityResetWaitView.this.this$0.showDialog(builder.create());
                }
            }
        }

        /* renamed from: com.hanista.mobogram.ui.LoginActivity.LoginActivityResetWaitView.2 */
        class C16572 implements Runnable {
            C16572() {
            }

            public void run() {
                if (LoginActivityResetWaitView.this.timeRunnable == this) {
                    LoginActivityResetWaitView.this.updateTimeText();
                    AndroidUtilities.runOnUIThread(LoginActivityResetWaitView.this.timeRunnable, 1000);
                }
            }
        }

        public LoginActivityResetWaitView(LoginActivity loginActivity, Context context) {
            int i = 5;
            this.this$0 = loginActivity;
            super(context);
            setOrientation(LoginActivity.done_button);
            this.confirmTextView = new TextView(context);
            this.confirmTextView.setTextColor(Theme.ATTACH_SHEET_TEXT_COLOR);
            this.confirmTextView.setTextSize(LoginActivity.done_button, 14.0f);
            this.confirmTextView.setGravity(LocaleController.isRTL ? 5 : 3);
            this.confirmTextView.setLineSpacing((float) AndroidUtilities.dp(2.0f), DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            addView(this.confirmTextView, LayoutHelper.createLinear(-2, -2, LocaleController.isRTL ? 5 : 3));
            View textView = new TextView(context);
            textView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
            textView.setTextColor(Theme.ATTACH_SHEET_TEXT_COLOR);
            textView.setText(LocaleController.getString("ResetAccountStatus", C0338R.string.ResetAccountStatus));
            textView.setTextSize(LoginActivity.done_button, 14.0f);
            textView.setLineSpacing((float) AndroidUtilities.dp(2.0f), DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            addView(textView, LayoutHelper.createLinear(-2, -2, (LocaleController.isRTL ? 5 : 3) | 48, 0, 24, 0, 0));
            this.resetAccountTime = new TextView(context);
            this.resetAccountTime.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
            this.resetAccountTime.setTextColor(Theme.ATTACH_SHEET_TEXT_COLOR);
            this.resetAccountTime.setTextSize(LoginActivity.done_button, 14.0f);
            this.resetAccountTime.setLineSpacing((float) AndroidUtilities.dp(2.0f), DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            addView(this.resetAccountTime, LayoutHelper.createLinear(-2, -2, (LocaleController.isRTL ? 5 : 3) | 48, 0, 2, 0, 0));
            this.resetAccountButton = new TextView(context);
            this.resetAccountButton.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
            this.resetAccountButton.setText(LocaleController.getString("ResetAccountButton", C0338R.string.ResetAccountButton));
            this.resetAccountButton.setTypeface(FontUtil.m1176a().m1160c());
            this.resetAccountButton.setTextSize(LoginActivity.done_button, 14.0f);
            this.resetAccountButton.setLineSpacing((float) AndroidUtilities.dp(2.0f), DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            this.resetAccountButton.setPadding(0, AndroidUtilities.dp(14.0f), 0, 0);
            textView = this.resetAccountButton;
            if (!LocaleController.isRTL) {
                i = 3;
            }
            addView(textView, LayoutHelper.createLinear(-2, -2, i | 48, 0, 7, 0, 0));
            this.resetAccountButton.setOnClickListener(new C16561(loginActivity));
        }

        private void updateTimeText() {
            int max = Math.max(0, this.waitTime - (ConnectionsManager.getInstance().getCurrentTime() - this.startTime));
            int i = max / 86400;
            int i2 = (max - (i * 86400)) / 3600;
            int i3 = ((max - (i * 86400)) - (i2 * 3600)) / 60;
            int i4 = max % 60;
            if (i != 0) {
                this.resetAccountTime.setText(AndroidUtilities.replaceTags(LocaleController.formatPluralString("DaysBold", i) + " " + LocaleController.formatPluralString("HoursBold", i2) + " " + LocaleController.formatPluralString("MinutesBold", i3)));
            } else {
                this.resetAccountTime.setText(AndroidUtilities.replaceTags(LocaleController.formatPluralString("HoursBold", i2) + " " + LocaleController.formatPluralString("MinutesBold", i3) + " " + LocaleController.formatPluralString("SecondsBold", i4)));
            }
            if (max > 0) {
                this.resetAccountButton.setTextColor(-2004318072);
            } else {
                this.resetAccountButton.setTextColor(-39322);
            }
        }

        public String getHeaderName() {
            return LocaleController.getString("ResetAccount", C0338R.string.ResetAccount);
        }

        public boolean needBackButton() {
            return true;
        }

        public void onBackPressed() {
            AndroidUtilities.cancelRunOnUIThread(this.timeRunnable);
            this.timeRunnable = null;
            this.currentParams = null;
        }

        public void restoreStateParams(Bundle bundle) {
            this.currentParams = bundle.getBundle("resetview_params");
            if (this.currentParams != null) {
                setParams(this.currentParams);
            }
        }

        public void saveStateParams(Bundle bundle) {
            if (this.currentParams != null) {
                bundle.putBundle("resetview_params", this.currentParams);
            }
        }

        public void setParams(Bundle bundle) {
            if (bundle != null) {
                this.currentParams = bundle;
                this.requestPhone = bundle.getString("phoneFormated");
                this.phoneHash = bundle.getString("phoneHash");
                this.phoneCode = bundle.getString("code");
                this.startTime = bundle.getInt("startTime");
                this.waitTime = bundle.getInt("waitTime");
                TextView textView = this.confirmTextView;
                Object[] objArr = new Object[LoginActivity.done_button];
                objArr[0] = PhoneFormat.getInstance().format("+" + this.requestPhone);
                textView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("ResetAccountInfo", C0338R.string.ResetAccountInfo, objArr)));
                updateTimeText();
                this.timeRunnable = new C16572();
                AndroidUtilities.runOnUIThread(this.timeRunnable, 1000);
            }
        }
    }

    public class LoginActivitySmsView extends SlideView implements NotificationCenterDelegate {
        private EditText codeField;
        private volatile int codeTime;
        private Timer codeTimer;
        private TextView confirmTextView;
        private Bundle currentParams;
        private int currentType;
        private String emailPhone;
        private boolean ignoreOnTextChange;
        private double lastCodeTime;
        private double lastCurrentTime;
        private String lastError;
        private int length;
        private boolean nextPressed;
        private int nextType;
        private int openTime;
        private String pattern;
        private String phone;
        private String phoneHash;
        private TextView problemText;
        private ProgressView progressView;
        private String requestPhone;
        private volatile int time;
        private TextView timeText;
        private Timer timeTimer;
        private int timeout;
        private final Object timerSync;
        private boolean waitingForEvent;

        /* renamed from: com.hanista.mobogram.ui.LoginActivity.LoginActivitySmsView.1 */
        class C16581 implements TextWatcher {
            final /* synthetic */ LoginActivity val$this$0;

            C16581(LoginActivity loginActivity) {
                this.val$this$0 = loginActivity;
            }

            public void afterTextChanged(Editable editable) {
                if (!LoginActivitySmsView.this.ignoreOnTextChange && LoginActivitySmsView.this.length != 0 && LoginActivitySmsView.this.codeField.length() == LoginActivitySmsView.this.length) {
                    LoginActivitySmsView.this.onNextPressed();
                }
            }

            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }
        }

        /* renamed from: com.hanista.mobogram.ui.LoginActivity.LoginActivitySmsView.2 */
        class C16592 implements OnEditorActionListener {
            final /* synthetic */ LoginActivity val$this$0;

            C16592(LoginActivity loginActivity) {
                this.val$this$0 = loginActivity;
            }

            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i != 5) {
                    return false;
                }
                LoginActivitySmsView.this.onNextPressed();
                return true;
            }
        }

        /* renamed from: com.hanista.mobogram.ui.LoginActivity.LoginActivitySmsView.3 */
        class C16603 implements View.OnClickListener {
            final /* synthetic */ LoginActivity val$this$0;

            C16603(LoginActivity loginActivity) {
                this.val$this$0 = loginActivity;
            }

            public void onClick(View view) {
                if (!LoginActivitySmsView.this.nextPressed) {
                    if (LoginActivitySmsView.this.nextType == 0 || LoginActivitySmsView.this.nextType == 4) {
                        try {
                            PackageInfo packageInfo = ApplicationLoader.applicationContext.getPackageManager().getPackageInfo(ApplicationLoader.applicationContext.getPackageName(), 0);
                            String format = String.format(Locale.US, "%s (%d)", new Object[]{packageInfo.versionName, Integer.valueOf(packageInfo.versionCode)});
                            Intent intent = new Intent("android.intent.action.SEND");
                            intent.setType("message/rfc822");
                            String[] strArr = new String[LoginActivity.done_button];
                            strArr[0] = "sms@stel.com";
                            intent.putExtra("android.intent.extra.EMAIL", strArr);
                            intent.putExtra("android.intent.extra.SUBJECT", "Android registration/login issue " + format + " " + LoginActivitySmsView.this.emailPhone);
                            intent.putExtra("android.intent.extra.TEXT", "Phone: " + LoginActivitySmsView.this.requestPhone + "\nApp version: " + format + "\nOS version: SDK " + VERSION.SDK_INT + "\nDevice Name: " + Build.MANUFACTURER + Build.MODEL + "\nLocale: " + Locale.getDefault() + "\nError: " + LoginActivitySmsView.this.lastError);
                            LoginActivitySmsView.this.getContext().startActivity(Intent.createChooser(intent, "Send email..."));
                            return;
                        } catch (Exception e) {
                            LoginActivity.this.needShowAlert(LocaleController.getString("AppName", C0338R.string.AppName), LocaleController.getString("NoMailInstalled", C0338R.string.NoMailInstalled));
                            return;
                        }
                    }
                    LoginActivitySmsView.this.resendCode();
                }
            }
        }

        /* renamed from: com.hanista.mobogram.ui.LoginActivity.LoginActivitySmsView.4 */
        class C16624 implements View.OnClickListener {
            final /* synthetic */ LoginActivity val$this$0;

            /* renamed from: com.hanista.mobogram.ui.LoginActivity.LoginActivitySmsView.4.1 */
            class C16611 implements RequestDelegate {
                C16611() {
                }

                public void run(TLObject tLObject, TL_error tL_error) {
                }
            }

            C16624(LoginActivity loginActivity) {
                this.val$this$0 = loginActivity;
            }

            public void onClick(View view) {
                TLObject tL_auth_cancelCode = new TL_auth_cancelCode();
                tL_auth_cancelCode.phone_number = LoginActivitySmsView.this.requestPhone;
                tL_auth_cancelCode.phone_code_hash = LoginActivitySmsView.this.phoneHash;
                ConnectionsManager.getInstance().sendRequest(tL_auth_cancelCode, new C16611(), 10);
                LoginActivitySmsView.this.onBackPressed();
                LoginActivity.this.setPage(0, true, null, true);
            }
        }

        /* renamed from: com.hanista.mobogram.ui.LoginActivity.LoginActivitySmsView.5 */
        class C16645 implements RequestDelegate {
            final /* synthetic */ Bundle val$params;

            /* renamed from: com.hanista.mobogram.ui.LoginActivity.LoginActivitySmsView.5.1 */
            class C16631 implements Runnable {
                final /* synthetic */ TL_error val$error;
                final /* synthetic */ TLObject val$response;

                C16631(TL_error tL_error, TLObject tLObject) {
                    this.val$error = tL_error;
                    this.val$response = tLObject;
                }

                public void run() {
                    LoginActivitySmsView.this.nextPressed = false;
                    if (this.val$error == null) {
                        LoginActivity.this.fillNextCodeParams(C16645.this.val$params, (TL_auth_sentCode) this.val$response);
                    } else if (this.val$error.text != null) {
                        if (this.val$error.text.contains("PHONE_NUMBER_INVALID")) {
                            LoginActivity.this.needShowAlert(LocaleController.getString("AppName", C0338R.string.AppName), LocaleController.getString("InvalidPhoneNumber", C0338R.string.InvalidPhoneNumber));
                        } else if (this.val$error.text.contains("PHONE_CODE_EMPTY") || this.val$error.text.contains("PHONE_CODE_INVALID")) {
                            LoginActivity.this.needShowAlert(LocaleController.getString("AppName", C0338R.string.AppName), LocaleController.getString("InvalidCode", C0338R.string.InvalidCode));
                        } else if (this.val$error.text.contains("PHONE_CODE_EXPIRED")) {
                            LoginActivitySmsView.this.onBackPressed();
                            LoginActivity.this.setPage(0, true, null, true);
                            LoginActivity.this.needShowAlert(LocaleController.getString("AppName", C0338R.string.AppName), LocaleController.getString("CodeExpired", C0338R.string.CodeExpired));
                        } else if (this.val$error.text.startsWith("FLOOD_WAIT")) {
                            LoginActivity.this.needShowAlert(LocaleController.getString("AppName", C0338R.string.AppName), LocaleController.getString("FloodWait", C0338R.string.FloodWait));
                        } else if (this.val$error.code != NotificationManagerCompat.IMPORTANCE_UNSPECIFIED) {
                            LoginActivity.this.needShowAlert(LocaleController.getString("AppName", C0338R.string.AppName), LocaleController.getString("ErrorOccurred", C0338R.string.ErrorOccurred) + "\n" + this.val$error.text);
                        }
                    }
                    LoginActivity.this.needHideProgress();
                }
            }

            C16645(Bundle bundle) {
                this.val$params = bundle;
            }

            public void run(TLObject tLObject, TL_error tL_error) {
                AndroidUtilities.runOnUIThread(new C16631(tL_error, tLObject));
            }
        }

        /* renamed from: com.hanista.mobogram.ui.LoginActivity.LoginActivitySmsView.6 */
        class C16666 extends TimerTask {

            /* renamed from: com.hanista.mobogram.ui.LoginActivity.LoginActivitySmsView.6.1 */
            class C16651 implements Runnable {
                C16651() {
                }

                public void run() {
                    if (LoginActivitySmsView.this.codeTime <= PointerIconCompat.TYPE_DEFAULT) {
                        LoginActivitySmsView.this.problemText.setVisibility(0);
                        LoginActivitySmsView.this.destroyCodeTimer();
                    }
                }
            }

            C16666() {
            }

            public void run() {
                double currentTimeMillis = (double) System.currentTimeMillis();
                LoginActivitySmsView.this.codeTime = (int) (((double) LoginActivitySmsView.this.codeTime) - (currentTimeMillis - LoginActivitySmsView.this.lastCodeTime));
                LoginActivitySmsView.this.lastCodeTime = currentTimeMillis;
                AndroidUtilities.runOnUIThread(new C16651());
            }
        }

        /* renamed from: com.hanista.mobogram.ui.LoginActivity.LoginActivitySmsView.7 */
        class C16707 extends TimerTask {

            /* renamed from: com.hanista.mobogram.ui.LoginActivity.LoginActivitySmsView.7.1 */
            class C16691 implements Runnable {

                /* renamed from: com.hanista.mobogram.ui.LoginActivity.LoginActivitySmsView.7.1.1 */
                class C16681 implements RequestDelegate {

                    /* renamed from: com.hanista.mobogram.ui.LoginActivity.LoginActivitySmsView.7.1.1.1 */
                    class C16671 implements Runnable {
                        final /* synthetic */ TL_error val$error;

                        C16671(TL_error tL_error) {
                            this.val$error = tL_error;
                        }

                        public void run() {
                            LoginActivitySmsView.this.lastError = this.val$error.text;
                        }
                    }

                    C16681() {
                    }

                    public void run(TLObject tLObject, TL_error tL_error) {
                        if (tL_error != null && tL_error.text != null) {
                            AndroidUtilities.runOnUIThread(new C16671(tL_error));
                        }
                    }
                }

                C16691() {
                }

                public void run() {
                    if (LoginActivitySmsView.this.time >= PointerIconCompat.TYPE_DEFAULT) {
                        int access$3700 = (LoginActivitySmsView.this.time / PointerIconCompat.TYPE_DEFAULT) - (((LoginActivitySmsView.this.time / PointerIconCompat.TYPE_DEFAULT) / 60) * 60);
                        if (LoginActivitySmsView.this.nextType == 4 || LoginActivitySmsView.this.nextType == 3) {
                            LoginActivitySmsView.this.timeText.setText(LocaleController.formatString("CallText", C0338R.string.CallText, Integer.valueOf(r0), Integer.valueOf(access$3700)));
                        } else if (LoginActivitySmsView.this.nextType == 2) {
                            LoginActivitySmsView.this.timeText.setText(LocaleController.formatString("SmsText", C0338R.string.SmsText, Integer.valueOf(r0), Integer.valueOf(access$3700)));
                        }
                        if (LoginActivitySmsView.this.progressView != null) {
                            LoginActivitySmsView.this.progressView.setProgress(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT - (((float) LoginActivitySmsView.this.time) / ((float) LoginActivitySmsView.this.timeout)));
                            return;
                        }
                        return;
                    }
                    if (LoginActivitySmsView.this.progressView != null) {
                        LoginActivitySmsView.this.progressView.setProgress(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                    }
                    LoginActivitySmsView.this.destroyTimer();
                    if (LoginActivitySmsView.this.currentType == 3) {
                        AndroidUtilities.setWaitingForCall(false);
                        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.didReceiveCall);
                        LoginActivitySmsView.this.waitingForEvent = false;
                        LoginActivitySmsView.this.destroyCodeTimer();
                        LoginActivitySmsView.this.resendCode();
                    } else if (LoginActivitySmsView.this.currentType != 2) {
                    } else {
                        if (LoginActivitySmsView.this.nextType == 4) {
                            LoginActivitySmsView.this.timeText.setText(LocaleController.getString("Calling", C0338R.string.Calling));
                            LoginActivitySmsView.this.createCodeTimer();
                            TLObject tL_auth_resendCode = new TL_auth_resendCode();
                            tL_auth_resendCode.phone_number = LoginActivitySmsView.this.requestPhone;
                            tL_auth_resendCode.phone_code_hash = LoginActivitySmsView.this.phoneHash;
                            ConnectionsManager.getInstance().sendRequest(tL_auth_resendCode, new C16681(), 10);
                        } else if (LoginActivitySmsView.this.nextType == 3) {
                            AndroidUtilities.setWaitingForSms(false);
                            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.didReceiveSmsCode);
                            LoginActivitySmsView.this.waitingForEvent = false;
                            LoginActivitySmsView.this.destroyCodeTimer();
                            LoginActivitySmsView.this.resendCode();
                        }
                    }
                }
            }

            C16707() {
            }

            public void run() {
                if (LoginActivitySmsView.this.timeTimer != null) {
                    double currentTimeMillis = (double) System.currentTimeMillis();
                    LoginActivitySmsView.this.time = (int) (((double) LoginActivitySmsView.this.time) - (currentTimeMillis - LoginActivitySmsView.this.lastCurrentTime));
                    LoginActivitySmsView.this.lastCurrentTime = currentTimeMillis;
                    AndroidUtilities.runOnUIThread(new C16691());
                }
            }
        }

        /* renamed from: com.hanista.mobogram.ui.LoginActivity.LoginActivitySmsView.8 */
        class C16748 implements RequestDelegate {
            final /* synthetic */ TL_auth_signIn val$req;

            /* renamed from: com.hanista.mobogram.ui.LoginActivity.LoginActivitySmsView.8.1 */
            class C16731 implements Runnable {
                final /* synthetic */ TL_error val$error;
                final /* synthetic */ TLObject val$response;

                /* renamed from: com.hanista.mobogram.ui.LoginActivity.LoginActivitySmsView.8.1.1 */
                class C16721 implements RequestDelegate {

                    /* renamed from: com.hanista.mobogram.ui.LoginActivity.LoginActivitySmsView.8.1.1.1 */
                    class C16711 implements Runnable {
                        final /* synthetic */ TL_error val$error;
                        final /* synthetic */ TLObject val$response;

                        C16711(TL_error tL_error, TLObject tLObject) {
                            this.val$error = tL_error;
                            this.val$response = tLObject;
                        }

                        public void run() {
                            LoginActivity.this.needHideProgress();
                            if (this.val$error == null) {
                                TL_account_password tL_account_password = (TL_account_password) this.val$response;
                                Bundle bundle = new Bundle();
                                bundle.putString("current_salt", Utilities.bytesToHex(tL_account_password.current_salt));
                                bundle.putString(TrackReferenceTypeBox.TYPE1, tL_account_password.hint);
                                bundle.putString("email_unconfirmed_pattern", tL_account_password.email_unconfirmed_pattern);
                                bundle.putString("phoneFormated", LoginActivitySmsView.this.requestPhone);
                                bundle.putString("phoneHash", LoginActivitySmsView.this.phoneHash);
                                bundle.putString("code", C16748.this.val$req.phone_code);
                                bundle.putInt("has_recovery", tL_account_password.has_recovery ? LoginActivity.done_button : 0);
                                LoginActivity.this.setPage(6, true, bundle, false);
                                return;
                            }
                            LoginActivity.this.needShowAlert(LocaleController.getString("AppName", C0338R.string.AppName), this.val$error.text);
                        }
                    }

                    C16721() {
                    }

                    public void run(TLObject tLObject, TL_error tL_error) {
                        AndroidUtilities.runOnUIThread(new C16711(tL_error, tLObject));
                    }
                }

                C16731(TL_error tL_error, TLObject tLObject) {
                    this.val$error = tL_error;
                    this.val$response = tLObject;
                }

                public void run() {
                    LoginActivitySmsView.this.nextPressed = false;
                    if (this.val$error == null) {
                        LoginActivity.this.needHideProgress();
                        TL_auth_authorization tL_auth_authorization = (TL_auth_authorization) this.val$response;
                        ConnectionsManager.getInstance().setUserId(tL_auth_authorization.user.id);
                        LoginActivitySmsView.this.destroyTimer();
                        LoginActivitySmsView.this.destroyCodeTimer();
                        UserConfig.clearConfig();
                        MessagesController.getInstance().cleanup();
                        UserConfig.setCurrentUser(tL_auth_authorization.user);
                        UserConfig.saveConfig(true);
                        MessagesStorage.getInstance().cleanup(true);
                        ArrayList arrayList = new ArrayList();
                        arrayList.add(tL_auth_authorization.user);
                        MessagesStorage.getInstance().putUsersAndChats(arrayList, null, true, true);
                        MessagesController.getInstance().putUser(tL_auth_authorization.user, false);
                        ContactsController.getInstance().checkAppAccount();
                        MessagesController.getInstance().getBlockedUsers(true);
                        LoginActivity.this.needFinishActivity();
                        return;
                    }
                    LoginActivitySmsView.this.lastError = this.val$error.text;
                    if (this.val$error.text.contains("PHONE_NUMBER_UNOCCUPIED")) {
                        LoginActivity.this.needHideProgress();
                        Bundle bundle = new Bundle();
                        bundle.putString("phoneFormated", LoginActivitySmsView.this.requestPhone);
                        bundle.putString("phoneHash", LoginActivitySmsView.this.phoneHash);
                        bundle.putString("code", C16748.this.val$req.phone_code);
                        LoginActivity.this.setPage(5, true, bundle, false);
                        LoginActivitySmsView.this.destroyTimer();
                        LoginActivitySmsView.this.destroyCodeTimer();
                    } else if (this.val$error.text.contains("SESSION_PASSWORD_NEEDED")) {
                        ConnectionsManager.getInstance().sendRequest(new TL_account_getPassword(), new C16721(), 10);
                        LoginActivitySmsView.this.destroyTimer();
                        LoginActivitySmsView.this.destroyCodeTimer();
                    } else {
                        LoginActivity.this.needHideProgress();
                        if ((LoginActivitySmsView.this.currentType == 3 && (LoginActivitySmsView.this.nextType == 4 || LoginActivitySmsView.this.nextType == 2)) || (LoginActivitySmsView.this.currentType == 2 && (LoginActivitySmsView.this.nextType == 4 || LoginActivitySmsView.this.nextType == 3))) {
                            LoginActivitySmsView.this.createTimer();
                        }
                        if (LoginActivitySmsView.this.currentType == 2) {
                            AndroidUtilities.setWaitingForSms(true);
                            NotificationCenter.getInstance().addObserver(LoginActivitySmsView.this, NotificationCenter.didReceiveSmsCode);
                        } else if (LoginActivitySmsView.this.currentType == 3) {
                            AndroidUtilities.setWaitingForCall(true);
                            NotificationCenter.getInstance().addObserver(LoginActivitySmsView.this, NotificationCenter.didReceiveCall);
                        }
                        LoginActivitySmsView.this.waitingForEvent = true;
                        if (LoginActivitySmsView.this.currentType == 3) {
                            return;
                        }
                        if (this.val$error.text.contains("PHONE_NUMBER_INVALID")) {
                            LoginActivity.this.needShowAlert(LocaleController.getString("AppName", C0338R.string.AppName), LocaleController.getString("InvalidPhoneNumber", C0338R.string.InvalidPhoneNumber));
                        } else if (this.val$error.text.contains("PHONE_CODE_EMPTY") || this.val$error.text.contains("PHONE_CODE_INVALID")) {
                            LoginActivity.this.needShowAlert(LocaleController.getString("AppName", C0338R.string.AppName), LocaleController.getString("InvalidCode", C0338R.string.InvalidCode));
                        } else if (this.val$error.text.contains("PHONE_CODE_EXPIRED")) {
                            LoginActivitySmsView.this.onBackPressed();
                            LoginActivity.this.setPage(0, true, null, true);
                            LoginActivity.this.needShowAlert(LocaleController.getString("AppName", C0338R.string.AppName), LocaleController.getString("CodeExpired", C0338R.string.CodeExpired));
                        } else if (this.val$error.text.startsWith("FLOOD_WAIT")) {
                            LoginActivity.this.needShowAlert(LocaleController.getString("AppName", C0338R.string.AppName), LocaleController.getString("FloodWait", C0338R.string.FloodWait));
                        } else {
                            LoginActivity.this.needShowAlert(LocaleController.getString("AppName", C0338R.string.AppName), LocaleController.getString("ErrorOccurred", C0338R.string.ErrorOccurred) + "\n" + this.val$error.text);
                        }
                    }
                }
            }

            C16748(TL_auth_signIn tL_auth_signIn) {
                this.val$req = tL_auth_signIn;
            }

            public void run(TLObject tLObject, TL_error tL_error) {
                AndroidUtilities.runOnUIThread(new C16731(tL_error, tLObject));
            }
        }

        private class ProgressView extends View {
            private Paint paint;
            private Paint paint2;
            private float progress;

            public ProgressView(Context context) {
                super(context);
                this.paint = new Paint();
                this.paint2 = new Paint();
                this.paint.setColor(-1971470);
                this.paint2.setColor(-10313520);
            }

            protected void onDraw(Canvas canvas) {
                int measuredWidth = (int) (((float) getMeasuredWidth()) * this.progress);
                canvas.drawRect(0.0f, 0.0f, (float) measuredWidth, (float) getMeasuredHeight(), this.paint2);
                canvas.drawRect((float) measuredWidth, 0.0f, (float) getMeasuredWidth(), (float) getMeasuredHeight(), this.paint);
            }

            public void setProgress(float f) {
                this.progress = f;
                invalidate();
            }
        }

        public LoginActivitySmsView(Context context, int i) {
            View imageView;
            super(context);
            this.timerSync = new Object();
            this.time = 60000;
            this.codeTime = DefaultLoadControl.DEFAULT_LOW_WATERMARK_MS;
            this.lastError = TtmlNode.ANONYMOUS_REGION_ID;
            this.pattern = "*";
            this.currentType = i;
            setOrientation(LoginActivity.done_button);
            this.confirmTextView = new TextView(context);
            this.confirmTextView.setTypeface(FontUtil.m1176a().m1161d());
            this.confirmTextView.setTextColor(Theme.ATTACH_SHEET_TEXT_COLOR);
            this.confirmTextView.setTextSize(LoginActivity.done_button, 14.0f);
            this.confirmTextView.setGravity(LocaleController.isRTL ? 5 : 3);
            this.confirmTextView.setLineSpacing((float) AndroidUtilities.dp(2.0f), DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            if (this.currentType == 3) {
                View frameLayout = new FrameLayout(context);
                imageView = new ImageView(context);
                imageView.setImageResource(C0338R.drawable.phone_activate);
                if (LocaleController.isRTL) {
                    frameLayout.addView(imageView, LayoutHelper.createFrame(64, 76.0f, 19, 2.0f, 2.0f, 0.0f, 0.0f));
                    frameLayout.addView(this.confirmTextView, LayoutHelper.createFrame(-1, -2.0f, LocaleController.isRTL ? 5 : 3, 82.0f, 0.0f, 0.0f, 0.0f));
                } else {
                    frameLayout.addView(this.confirmTextView, LayoutHelper.createFrame(-1, -2.0f, LocaleController.isRTL ? 5 : 3, 0.0f, 0.0f, 82.0f, 0.0f));
                    frameLayout.addView(imageView, LayoutHelper.createFrame(64, 76.0f, 21, 0.0f, 2.0f, 0.0f, 2.0f));
                }
                addView(frameLayout, LayoutHelper.createLinear(-2, -2, LocaleController.isRTL ? 5 : 3));
            } else {
                addView(this.confirmTextView, LayoutHelper.createLinear(-2, -2, LocaleController.isRTL ? 5 : 3));
            }
            this.codeField = new EditText(context);
            this.codeField.setTextColor(Theme.STICKERS_SHEET_TITLE_TEXT_COLOR);
            this.codeField.setHint(LocaleController.getString("Code", C0338R.string.Code));
            AndroidUtilities.clearCursorDrawable(this.codeField);
            int i2 = AdvanceTheme.f2491b;
            if (ThemeUtil.m2490b()) {
                this.codeField.getBackground().setColorFilter(i2, Mode.SRC_IN);
            }
            this.codeField.setHintTextColor(Theme.SHARE_SHEET_EDIT_PLACEHOLDER_TEXT_COLOR);
            this.codeField.setImeOptions(268435461);
            this.codeField.setTextSize(LoginActivity.done_button, 18.0f);
            this.codeField.setInputType(3);
            this.codeField.setMaxLines(LoginActivity.done_button);
            this.codeField.setPadding(0, 0, 0, 0);
            addView(this.codeField, LayoutHelper.createLinear(-1, 36, (int) LoginActivity.done_button, 0, 20, 0, 0));
            this.codeField.addTextChangedListener(new C16581(LoginActivity.this));
            this.codeField.setOnEditorActionListener(new C16592(LoginActivity.this));
            if (this.currentType == 3) {
                this.codeField.setEnabled(false);
                this.codeField.setInputType(0);
                this.codeField.setVisibility(8);
            }
            this.timeText = new TextView(context);
            this.timeText.setTextSize(LoginActivity.done_button, 14.0f);
            this.timeText.setTextColor(Theme.ATTACH_SHEET_TEXT_COLOR);
            this.timeText.setLineSpacing((float) AndroidUtilities.dp(2.0f), DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            this.timeText.setGravity(LocaleController.isRTL ? 5 : 3);
            addView(this.timeText, LayoutHelper.createLinear(-2, -2, LocaleController.isRTL ? 5 : 3, 0, 30, 0, 0));
            if (this.currentType == 3) {
                this.progressView = new ProgressView(context);
                addView(this.progressView, LayoutHelper.createLinear(-1, 3, 0.0f, 12.0f, 0.0f, 0.0f));
            }
            this.problemText = new TextView(context);
            this.problemText.setText(LocaleController.getString("DidNotGetTheCode", C0338R.string.DidNotGetTheCode));
            this.problemText.setGravity(LocaleController.isRTL ? 5 : 3);
            this.problemText.setTextSize(LoginActivity.done_button, 14.0f);
            this.problemText.setTextColor(Theme.DIALOGS_PRINTING_TEXT_COLOR);
            if (ThemeUtil.m2490b()) {
                this.problemText.setTextColor(i2);
            }
            this.problemText.setLineSpacing((float) AndroidUtilities.dp(2.0f), DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            this.problemText.setPadding(0, AndroidUtilities.dp(2.0f), 0, AndroidUtilities.dp(12.0f));
            addView(this.problemText, LayoutHelper.createLinear(-2, -2, LocaleController.isRTL ? 5 : 3, 0, 20, 0, 0));
            this.problemText.setOnClickListener(new C16603(LoginActivity.this));
            imageView = new LinearLayout(context);
            imageView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
            addView(imageView, LayoutHelper.createLinear(-1, -1, LocaleController.isRTL ? 5 : 3));
            View textView = new TextView(context);
            textView.setGravity((LocaleController.isRTL ? 5 : 3) | LoginActivity.done_button);
            textView.setTextColor(Theme.DIALOGS_PRINTING_TEXT_COLOR);
            if (ThemeUtil.m2490b()) {
                textView.setTextColor(i2);
            }
            textView.setTextSize(LoginActivity.done_button, 14.0f);
            textView.setLineSpacing((float) AndroidUtilities.dp(2.0f), DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            textView.setPadding(0, AndroidUtilities.dp(24.0f), 0, 0);
            imageView.addView(textView, LayoutHelper.createLinear(-2, -2, (LocaleController.isRTL ? 5 : 3) | 80, 0, 0, 0, 10));
            textView.setText(LocaleController.getString("WrongNumber", C0338R.string.WrongNumber));
            textView.setOnClickListener(new C16624(LoginActivity.this));
        }

        private void createCodeTimer() {
            if (this.codeTimer == null) {
                this.codeTime = DefaultLoadControl.DEFAULT_LOW_WATERMARK_MS;
                this.codeTimer = new Timer();
                this.lastCodeTime = (double) System.currentTimeMillis();
                this.codeTimer.schedule(new C16666(), 0, 1000);
            }
        }

        private void createTimer() {
            if (this.timeTimer == null) {
                this.timeTimer = new Timer();
                this.timeTimer.schedule(new C16707(), 0, 1000);
            }
        }

        private void destroyCodeTimer() {
            try {
                synchronized (this.timerSync) {
                    if (this.codeTimer != null) {
                        this.codeTimer.cancel();
                        this.codeTimer = null;
                    }
                }
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
        }

        private void destroyTimer() {
            try {
                synchronized (this.timerSync) {
                    if (this.timeTimer != null) {
                        this.timeTimer.cancel();
                        this.timeTimer = null;
                    }
                }
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
        }

        private void resendCode() {
            Bundle bundle = new Bundle();
            bundle.putString("phone", this.phone);
            bundle.putString("ephone", this.emailPhone);
            bundle.putString("phoneFormated", this.requestPhone);
            this.nextPressed = true;
            LoginActivity.this.needShowProgress();
            TLObject tL_auth_resendCode = new TL_auth_resendCode();
            tL_auth_resendCode.phone_number = this.requestPhone;
            tL_auth_resendCode.phone_code_hash = this.phoneHash;
            ConnectionsManager.getInstance().sendRequest(tL_auth_resendCode, new C16645(bundle), 10);
        }

        public void didReceivedNotification(int i, Object... objArr) {
            if (this.waitingForEvent && this.codeField != null) {
                if (i == NotificationCenter.didReceiveSmsCode) {
                    this.ignoreOnTextChange = true;
                    this.codeField.setText(TtmlNode.ANONYMOUS_REGION_ID + objArr[0]);
                    this.ignoreOnTextChange = false;
                    onNextPressed();
                } else if (i == NotificationCenter.didReceiveCall) {
                    CharSequence charSequence = TtmlNode.ANONYMOUS_REGION_ID + objArr[0];
                    if (this.pattern.equals("*") || charSequence.contains(this.pattern.replace("*", TtmlNode.ANONYMOUS_REGION_ID))) {
                        this.ignoreOnTextChange = true;
                        this.codeField.setText(charSequence);
                        this.ignoreOnTextChange = false;
                        onNextPressed();
                    }
                }
            }
        }

        public String getHeaderName() {
            return LocaleController.getString("YourCode", C0338R.string.YourCode);
        }

        public void onBackPressed() {
            destroyTimer();
            destroyCodeTimer();
            this.currentParams = null;
            if (this.currentType == 2) {
                AndroidUtilities.setWaitingForSms(false);
                NotificationCenter.getInstance().removeObserver(this, NotificationCenter.didReceiveSmsCode);
            } else if (this.currentType == 3) {
                AndroidUtilities.setWaitingForCall(false);
                NotificationCenter.getInstance().removeObserver(this, NotificationCenter.didReceiveCall);
            }
            this.waitingForEvent = false;
        }

        public void onDestroyActivity() {
            super.onDestroyActivity();
            if (this.currentType == 2) {
                AndroidUtilities.setWaitingForSms(false);
                NotificationCenter.getInstance().removeObserver(this, NotificationCenter.didReceiveSmsCode);
            } else if (this.currentType == 3) {
                AndroidUtilities.setWaitingForCall(false);
                NotificationCenter.getInstance().removeObserver(this, NotificationCenter.didReceiveCall);
            }
            this.waitingForEvent = false;
            destroyTimer();
            destroyCodeTimer();
        }

        public void onNextPressed() {
            if (!this.nextPressed) {
                this.nextPressed = true;
                if (this.currentType == 2) {
                    AndroidUtilities.setWaitingForSms(false);
                    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.didReceiveSmsCode);
                } else if (this.currentType == 3) {
                    AndroidUtilities.setWaitingForCall(false);
                    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.didReceiveCall);
                }
                this.waitingForEvent = false;
                TLObject tL_auth_signIn = new TL_auth_signIn();
                tL_auth_signIn.phone_number = this.requestPhone;
                tL_auth_signIn.phone_code = this.codeField.getText().toString();
                tL_auth_signIn.phone_code_hash = this.phoneHash;
                destroyTimer();
                LoginActivity.this.needShowProgress();
                ConnectionsManager.getInstance().sendRequest(tL_auth_signIn, new C16748(tL_auth_signIn), 10);
            }
        }

        public void onShow() {
            super.onShow();
            if (this.codeField != null) {
                this.codeField.requestFocus();
                this.codeField.setSelection(this.codeField.length());
            }
        }

        public void restoreStateParams(Bundle bundle) {
            this.currentParams = bundle.getBundle("smsview_params_" + this.currentType);
            if (this.currentParams != null) {
                setParams(this.currentParams);
            }
            CharSequence string = bundle.getString("smsview_code_" + this.currentType);
            if (string != null) {
                this.codeField.setText(string);
            }
            int i = bundle.getInt("time");
            if (i != 0) {
                this.time = i;
            }
            i = bundle.getInt("open");
            if (i != 0) {
                this.openTime = i;
            }
        }

        public void saveStateParams(Bundle bundle) {
            String obj = this.codeField.getText().toString();
            if (obj.length() != 0) {
                bundle.putString("smsview_code_" + this.currentType, obj);
            }
            if (this.currentParams != null) {
                bundle.putBundle("smsview_params_" + this.currentType, this.currentParams);
            }
            if (this.time != 0) {
                bundle.putInt("time", this.time);
            }
            if (this.openTime != 0) {
                bundle.putInt("open", this.openTime);
            }
        }

        public void setParams(Bundle bundle) {
            int i = 0;
            if (bundle != null) {
                this.codeField.setText(TtmlNode.ANONYMOUS_REGION_ID);
                this.waitingForEvent = true;
                if (this.currentType == 2) {
                    AndroidUtilities.setWaitingForSms(true);
                    NotificationCenter.getInstance().addObserver(this, NotificationCenter.didReceiveSmsCode);
                } else if (this.currentType == 3) {
                    AndroidUtilities.setWaitingForCall(true);
                    NotificationCenter.getInstance().addObserver(this, NotificationCenter.didReceiveCall);
                }
                this.currentParams = bundle;
                this.phone = bundle.getString("phone");
                this.emailPhone = bundle.getString("ephone");
                this.requestPhone = bundle.getString("phoneFormated");
                this.phoneHash = bundle.getString("phoneHash");
                int i2 = bundle.getInt("timeout");
                this.time = i2;
                this.timeout = i2;
                this.openTime = (int) (System.currentTimeMillis() / 1000);
                this.nextType = bundle.getInt("nextType");
                this.pattern = bundle.getString("pattern");
                this.length = bundle.getInt("length");
                if (this.length != 0) {
                    InputFilter[] inputFilterArr = new InputFilter[LoginActivity.done_button];
                    inputFilterArr[0] = new LengthFilter(this.length);
                    this.codeField.setFilters(inputFilterArr);
                } else {
                    this.codeField.setFilters(new InputFilter[0]);
                }
                if (this.progressView != null) {
                    this.progressView.setVisibility(this.nextType != 0 ? 0 : 8);
                }
                if (this.phone != null) {
                    String format = PhoneFormat.getInstance().format(this.phone);
                    CharSequence charSequence = TtmlNode.ANONYMOUS_REGION_ID;
                    if (this.currentType == LoginActivity.done_button) {
                        charSequence = AndroidUtilities.replaceTags(LocaleController.getString("SentAppCode", C0338R.string.SentAppCode));
                    } else if (this.currentType == 2) {
                        r5 = new Object[LoginActivity.done_button];
                        r5[0] = format;
                        charSequence = AndroidUtilities.replaceTags(LocaleController.formatString("SentSmsCode", C0338R.string.SentSmsCode, r5));
                    } else if (this.currentType == 3) {
                        r5 = new Object[LoginActivity.done_button];
                        r5[0] = format;
                        charSequence = AndroidUtilities.replaceTags(LocaleController.formatString("SentCallCode", C0338R.string.SentCallCode, r5));
                    } else if (this.currentType == 4) {
                        r5 = new Object[LoginActivity.done_button];
                        r5[0] = format;
                        charSequence = AndroidUtilities.replaceTags(LocaleController.formatString("SentCallOnly", C0338R.string.SentCallOnly, r5));
                    }
                    this.confirmTextView.setText(charSequence);
                    if (this.currentType != 3) {
                        AndroidUtilities.showKeyboard(this.codeField);
                        this.codeField.requestFocus();
                    } else {
                        AndroidUtilities.hideKeyboard(this.codeField);
                    }
                    destroyTimer();
                    destroyCodeTimer();
                    this.lastCurrentTime = (double) System.currentTimeMillis();
                    if (this.currentType == LoginActivity.done_button) {
                        this.problemText.setVisibility(0);
                        this.timeText.setVisibility(8);
                    } else if (this.currentType == 3 && (this.nextType == 4 || this.nextType == 2)) {
                        this.problemText.setVisibility(8);
                        this.timeText.setVisibility(0);
                        if (this.nextType == 4) {
                            this.timeText.setText(LocaleController.formatString("CallText", C0338R.string.CallText, Integer.valueOf(LoginActivity.done_button), Integer.valueOf(0)));
                        } else if (this.nextType == 2) {
                            this.timeText.setText(LocaleController.formatString("SmsText", C0338R.string.SmsText, Integer.valueOf(LoginActivity.done_button), Integer.valueOf(0)));
                        }
                        createTimer();
                    } else if (this.currentType == 2 && (this.nextType == 4 || this.nextType == 3)) {
                        this.timeText.setVisibility(0);
                        this.timeText.setText(LocaleController.formatString("CallText", C0338R.string.CallText, Integer.valueOf(2), Integer.valueOf(0)));
                        TextView textView = this.problemText;
                        if (this.time >= PointerIconCompat.TYPE_DEFAULT) {
                            i = 8;
                        }
                        textView.setVisibility(i);
                        createTimer();
                    } else {
                        this.timeText.setVisibility(8);
                        this.problemText.setVisibility(8);
                        createCodeTimer();
                    }
                }
            }
        }
    }

    public class PhoneView extends SlideView implements OnItemSelectedListener {
        private EditText codeField;
        private HashMap<String, String> codesMap;
        private ArrayList<String> countriesArray;
        private HashMap<String, String> countriesMap;
        private TextView countryButton;
        private int countryState;
        private boolean ignoreOnPhoneChange;
        private boolean ignoreOnTextChange;
        private boolean ignoreSelection;
        private boolean nextPressed;
        private HintEditText phoneField;
        private HashMap<String, String> phoneFormatMap;

        /* renamed from: com.hanista.mobogram.ui.LoginActivity.PhoneView.1 */
        class C16771 implements View.OnClickListener {
            final /* synthetic */ LoginActivity val$this$0;

            /* renamed from: com.hanista.mobogram.ui.LoginActivity.PhoneView.1.1 */
            class C16761 implements CountrySelectActivityDelegate {

                /* renamed from: com.hanista.mobogram.ui.LoginActivity.PhoneView.1.1.1 */
                class C16751 implements Runnable {
                    C16751() {
                    }

                    public void run() {
                        AndroidUtilities.showKeyboard(PhoneView.this.phoneField);
                    }
                }

                C16761() {
                }

                public void didSelectCountry(String str) {
                    PhoneView.this.selectCountry(str);
                    AndroidUtilities.runOnUIThread(new C16751(), 300);
                    PhoneView.this.phoneField.requestFocus();
                    PhoneView.this.phoneField.setSelection(PhoneView.this.phoneField.length());
                }
            }

            C16771(LoginActivity loginActivity) {
                this.val$this$0 = loginActivity;
            }

            public void onClick(View view) {
                BaseFragment countrySelectActivity = new CountrySelectActivity();
                countrySelectActivity.setCountrySelectActivityDelegate(new C16761());
                LoginActivity.this.presentFragment(countrySelectActivity);
            }
        }

        /* renamed from: com.hanista.mobogram.ui.LoginActivity.PhoneView.2 */
        class C16782 implements TextWatcher {
            final /* synthetic */ LoginActivity val$this$0;

            C16782(LoginActivity loginActivity) {
                this.val$this$0 = loginActivity;
            }

            public void afterTextChanged(Editable editable) {
                String str = null;
                if (!PhoneView.this.ignoreOnTextChange) {
                    PhoneView.this.ignoreOnTextChange = true;
                    String stripExceptNumbers = PhoneFormat.stripExceptNumbers(PhoneView.this.codeField.getText().toString());
                    PhoneView.this.codeField.setText(stripExceptNumbers);
                    if (stripExceptNumbers.length() == 0) {
                        PhoneView.this.countryButton.setText(LocaleController.getString("ChooseCountry", C0338R.string.ChooseCountry));
                        PhoneView.this.phoneField.setHintText(null);
                        PhoneView.this.countryState = LoginActivity.done_button;
                    } else {
                        String str2;
                        Object obj;
                        boolean z;
                        CharSequence charSequence;
                        if (stripExceptNumbers.length() > 4) {
                            String substring;
                            boolean z2;
                            PhoneView.this.ignoreOnTextChange = true;
                            for (int i = 4; i >= LoginActivity.done_button; i--) {
                                substring = stripExceptNumbers.substring(0, i);
                                if (((String) PhoneView.this.codesMap.get(substring)) != null) {
                                    str2 = stripExceptNumbers.substring(i, stripExceptNumbers.length()) + PhoneView.this.phoneField.getText().toString();
                                    PhoneView.this.codeField.setText(substring);
                                    z2 = true;
                                    break;
                                }
                            }
                            str2 = null;
                            substring = stripExceptNumbers;
                            z2 = false;
                            if (!z2) {
                                PhoneView.this.ignoreOnTextChange = true;
                                str2 = substring.substring(LoginActivity.done_button, substring.length()) + PhoneView.this.phoneField.getText().toString();
                                EditText access$600 = PhoneView.this.codeField;
                                substring = substring.substring(0, LoginActivity.done_button);
                                access$600.setText(substring);
                            }
                            obj = substring;
                            z = z2;
                            charSequence = str2;
                        } else {
                            z = false;
                            String str3 = stripExceptNumbers;
                            charSequence = null;
                        }
                        str2 = (String) PhoneView.this.codesMap.get(obj);
                        if (str2 != null) {
                            int indexOf = PhoneView.this.countriesArray.indexOf(str2);
                            if (indexOf != -1) {
                                PhoneView.this.ignoreSelection = true;
                                PhoneView.this.countryButton.setText((CharSequence) PhoneView.this.countriesArray.get(indexOf));
                                str2 = (String) PhoneView.this.phoneFormatMap.get(obj);
                                HintEditText access$400 = PhoneView.this.phoneField;
                                if (str2 != null) {
                                    str = str2.replace('X', '\u2013');
                                }
                                access$400.setHintText(str);
                                PhoneView.this.countryState = 0;
                            } else {
                                PhoneView.this.countryButton.setText(LocaleController.getString("WrongCountry", C0338R.string.WrongCountry));
                                PhoneView.this.phoneField.setHintText(null);
                                PhoneView.this.countryState = 2;
                            }
                        } else {
                            PhoneView.this.countryButton.setText(LocaleController.getString("WrongCountry", C0338R.string.WrongCountry));
                            PhoneView.this.phoneField.setHintText(null);
                            PhoneView.this.countryState = 2;
                        }
                        if (!z) {
                            PhoneView.this.codeField.setSelection(PhoneView.this.codeField.getText().length());
                        }
                        if (charSequence != null) {
                            PhoneView.this.phoneField.requestFocus();
                            PhoneView.this.phoneField.setText(charSequence);
                            PhoneView.this.phoneField.setSelection(PhoneView.this.phoneField.length());
                        }
                    }
                    PhoneView.this.ignoreOnTextChange = false;
                }
            }

            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }
        }

        /* renamed from: com.hanista.mobogram.ui.LoginActivity.PhoneView.3 */
        class C16793 implements OnEditorActionListener {
            final /* synthetic */ LoginActivity val$this$0;

            C16793(LoginActivity loginActivity) {
                this.val$this$0 = loginActivity;
            }

            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i != 5) {
                    return false;
                }
                PhoneView.this.phoneField.requestFocus();
                PhoneView.this.phoneField.setSelection(PhoneView.this.phoneField.length());
                return true;
            }
        }

        /* renamed from: com.hanista.mobogram.ui.LoginActivity.PhoneView.4 */
        class C16804 implements TextWatcher {
            private int actionPosition;
            private int characterAction;
            final /* synthetic */ LoginActivity val$this$0;

            C16804(LoginActivity loginActivity) {
                this.val$this$0 = loginActivity;
                this.characterAction = -1;
            }

            public void afterTextChanged(Editable editable) {
                if (!PhoneView.this.ignoreOnPhoneChange) {
                    int selectionStart = PhoneView.this.phoneField.getSelectionStart();
                    String str = "0123456789";
                    String obj = PhoneView.this.phoneField.getText().toString();
                    if (this.characterAction == 3) {
                        obj = obj.substring(0, this.actionPosition) + obj.substring(this.actionPosition + LoginActivity.done_button, obj.length());
                        selectionStart--;
                    }
                    CharSequence stringBuilder = new StringBuilder(obj.length());
                    for (int i = 0; i < obj.length(); i += LoginActivity.done_button) {
                        Object substring = obj.substring(i, i + LoginActivity.done_button);
                        if (str.contains(substring)) {
                            stringBuilder.append(substring);
                        }
                    }
                    PhoneView.this.ignoreOnPhoneChange = true;
                    String hintText = PhoneView.this.phoneField.getHintText();
                    if (hintText != null) {
                        int i2 = 0;
                        while (i2 < stringBuilder.length()) {
                            if (i2 < hintText.length()) {
                                if (hintText.charAt(i2) == ' ') {
                                    stringBuilder.insert(i2, ' ');
                                    i2 += LoginActivity.done_button;
                                    if (!(selectionStart != i2 || this.characterAction == 2 || this.characterAction == 3)) {
                                        selectionStart += LoginActivity.done_button;
                                    }
                                }
                                i2 += LoginActivity.done_button;
                            } else {
                                stringBuilder.insert(i2, ' ');
                                if (!(selectionStart != i2 + LoginActivity.done_button || this.characterAction == 2 || this.characterAction == 3)) {
                                    selectionStart += LoginActivity.done_button;
                                }
                            }
                        }
                    }
                    PhoneView.this.phoneField.setText(stringBuilder);
                    if (selectionStart >= 0) {
                        HintEditText access$400 = PhoneView.this.phoneField;
                        if (selectionStart > PhoneView.this.phoneField.length()) {
                            selectionStart = PhoneView.this.phoneField.length();
                        }
                        access$400.setSelection(selectionStart);
                    }
                    PhoneView.this.phoneField.onTextChange();
                    PhoneView.this.ignoreOnPhoneChange = false;
                }
            }

            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                if (i2 == 0 && i3 == LoginActivity.done_button) {
                    this.characterAction = LoginActivity.done_button;
                } else if (i2 != LoginActivity.done_button || i3 != 0) {
                    this.characterAction = -1;
                } else if (charSequence.charAt(i) != ' ' || i <= 0) {
                    this.characterAction = 2;
                } else {
                    this.characterAction = 3;
                    this.actionPosition = i - 1;
                }
            }

            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }
        }

        /* renamed from: com.hanista.mobogram.ui.LoginActivity.PhoneView.5 */
        class C16815 implements OnEditorActionListener {
            final /* synthetic */ LoginActivity val$this$0;

            C16815(LoginActivity loginActivity) {
                this.val$this$0 = loginActivity;
            }

            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i != 5) {
                    return false;
                }
                PhoneView.this.onNextPressed();
                return true;
            }
        }

        /* renamed from: com.hanista.mobogram.ui.LoginActivity.PhoneView.6 */
        class C16826 implements Comparator<String> {
            final /* synthetic */ LoginActivity val$this$0;

            C16826(LoginActivity loginActivity) {
                this.val$this$0 = loginActivity;
            }

            public int compare(String str, String str2) {
                return str.compareTo(str2);
            }
        }

        /* renamed from: com.hanista.mobogram.ui.LoginActivity.PhoneView.7 */
        class C16837 implements View.OnClickListener {
            final /* synthetic */ LoginActivity val$this$0;

            C16837(LoginActivity loginActivity) {
                this.val$this$0 = loginActivity;
            }

            public void onClick(View view) {
                LoginActivity.this.setPage(9, true, null, false);
            }
        }

        /* renamed from: com.hanista.mobogram.ui.LoginActivity.PhoneView.8 */
        class C16858 implements RequestDelegate {
            final /* synthetic */ Bundle val$params;
            final /* synthetic */ TL_auth_sendCode val$req;

            /* renamed from: com.hanista.mobogram.ui.LoginActivity.PhoneView.8.1 */
            class C16841 implements Runnable {
                final /* synthetic */ TL_error val$error;
                final /* synthetic */ TLObject val$response;

                C16841(TL_error tL_error, TLObject tLObject) {
                    this.val$error = tL_error;
                    this.val$response = tLObject;
                }

                public void run() {
                    PhoneView.this.nextPressed = false;
                    if (this.val$error == null) {
                        LoginActivity.this.fillNextCodeParams(C16858.this.val$params, (TL_auth_sentCode) this.val$response);
                    } else if (this.val$error.text != null) {
                        if (this.val$error.text.contains("PHONE_NUMBER_INVALID")) {
                            LoginActivity.this.needShowInvalidAlert(C16858.this.val$req.phone_number, false);
                        } else if (this.val$error.text.contains("PHONE_NUMBER_BANNED")) {
                            LoginActivity.this.needShowInvalidAlert(C16858.this.val$req.phone_number, true);
                        } else if (this.val$error.text.contains("PHONE_CODE_EMPTY") || this.val$error.text.contains("PHONE_CODE_INVALID")) {
                            LoginActivity.this.needShowAlert(LocaleController.getString("AppName", C0338R.string.AppName), LocaleController.getString("InvalidCode", C0338R.string.InvalidCode));
                        } else if (this.val$error.text.contains("PHONE_CODE_EXPIRED")) {
                            LoginActivity.this.needShowAlert(LocaleController.getString("AppName", C0338R.string.AppName), LocaleController.getString("CodeExpired", C0338R.string.CodeExpired));
                        } else if (this.val$error.text.startsWith("FLOOD_WAIT")) {
                            LoginActivity.this.needShowAlert(LocaleController.getString("AppName", C0338R.string.AppName), LocaleController.getString("FloodWait", C0338R.string.FloodWait));
                        } else if (this.val$error.code != NotificationManagerCompat.IMPORTANCE_UNSPECIFIED) {
                            LoginActivity.this.needShowAlert(LocaleController.getString("AppName", C0338R.string.AppName), this.val$error.text);
                        }
                    }
                    LoginActivity.this.needHideProgress();
                }
            }

            C16858(Bundle bundle, TL_auth_sendCode tL_auth_sendCode) {
                this.val$params = bundle;
                this.val$req = tL_auth_sendCode;
            }

            public void run(TLObject tLObject, TL_error tL_error) {
                AndroidUtilities.runOnUIThread(new C16841(tL_error, tLObject));
            }
        }

        public PhoneView(Context context) {
            Object toUpperCase;
            String str;
            View textView;
            super(context);
            this.countryState = 0;
            this.countriesArray = new ArrayList();
            this.countriesMap = new HashMap();
            this.codesMap = new HashMap();
            this.phoneFormatMap = new HashMap();
            this.ignoreSelection = false;
            this.ignoreOnTextChange = false;
            this.ignoreOnPhoneChange = false;
            this.nextPressed = false;
            setOrientation(LoginActivity.done_button);
            this.countryButton = new TextView(context);
            this.countryButton.setTypeface(FontUtil.m1176a().m1161d());
            this.countryButton.setTextSize(LoginActivity.done_button, 18.0f);
            this.countryButton.setPadding(AndroidUtilities.dp(12.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(12.0f), 0);
            this.countryButton.setTextColor(Theme.STICKERS_SHEET_TITLE_TEXT_COLOR);
            this.countryButton.setMaxLines(LoginActivity.done_button);
            this.countryButton.setSingleLine(true);
            this.countryButton.setEllipsize(TruncateAt.END);
            this.countryButton.setGravity((LocaleController.isRTL ? 5 : 3) | LoginActivity.done_button);
            this.countryButton.setBackgroundResource(C0338R.drawable.spinner_states);
            addView(this.countryButton, LayoutHelper.createLinear(-1, 36, 0.0f, 0.0f, 0.0f, 14.0f));
            this.countryButton.setOnClickListener(new C16771(LoginActivity.this));
            View view = new View(context);
            view.setPadding(AndroidUtilities.dp(12.0f), 0, AndroidUtilities.dp(12.0f), 0);
            view.setBackgroundColor(-2368549);
            addView(view, LayoutHelper.createLinear(-1, LoginActivity.done_button, 4.0f, -17.5f, 4.0f, 0.0f));
            view = new LinearLayout(context);
            view.setOrientation(0);
            addView(view, LayoutHelper.createLinear(-1, -2, 0.0f, 20.0f, 0.0f, 0.0f));
            View textView2 = new TextView(context);
            textView2.setText("+");
            textView2.setTextColor(Theme.STICKERS_SHEET_TITLE_TEXT_COLOR);
            textView2.setTextSize(LoginActivity.done_button, 18.0f);
            view.addView(textView2, LayoutHelper.createLinear(-2, -2));
            this.codeField = new EditText(context);
            this.codeField.setInputType(3);
            this.codeField.setTextColor(Theme.STICKERS_SHEET_TITLE_TEXT_COLOR);
            AndroidUtilities.clearCursorDrawable(this.codeField);
            this.codeField.setPadding(AndroidUtilities.dp(10.0f), 0, 0, 0);
            this.codeField.setTextSize(LoginActivity.done_button, 18.0f);
            this.codeField.setMaxLines(LoginActivity.done_button);
            this.codeField.setGravity(19);
            this.codeField.setImeOptions(268435461);
            InputFilter[] inputFilterArr = new InputFilter[LoginActivity.done_button];
            inputFilterArr[0] = new LengthFilter(5);
            this.codeField.setFilters(inputFilterArr);
            view.addView(this.codeField, LayoutHelper.createLinear(55, 36, -9.0f, 0.0f, 16.0f, 0.0f));
            this.codeField.addTextChangedListener(new C16782(LoginActivity.this));
            this.codeField.setOnEditorActionListener(new C16793(LoginActivity.this));
            this.phoneField = new HintEditText(context);
            this.phoneField.setInputType(3);
            this.phoneField.setTextColor(Theme.STICKERS_SHEET_TITLE_TEXT_COLOR);
            this.phoneField.setHintTextColor(Theme.SHARE_SHEET_EDIT_PLACEHOLDER_TEXT_COLOR);
            this.phoneField.setPadding(0, 0, 0, 0);
            AndroidUtilities.clearCursorDrawable(this.phoneField);
            if (ThemeUtil.m2490b()) {
                this.phoneField.getBackground().setColorFilter(AdvanceTheme.f2491b, Mode.SRC_IN);
            }
            this.phoneField.setTextSize(LoginActivity.done_button, 18.0f);
            this.phoneField.setMaxLines(LoginActivity.done_button);
            this.phoneField.setGravity(19);
            this.phoneField.setImeOptions(268435461);
            view.addView(this.phoneField, LayoutHelper.createFrame(-1, 36.0f));
            this.phoneField.addTextChangedListener(new C16804(LoginActivity.this));
            this.phoneField.setOnEditorActionListener(new C16815(LoginActivity.this));
            View textView3 = new TextView(context);
            textView3.setText(LocaleController.getString("StartText", C0338R.string.StartText));
            textView3.setTextColor(Theme.ATTACH_SHEET_TEXT_COLOR);
            textView3.setTextSize(LoginActivity.done_button, 14.0f);
            textView3.setGravity(LocaleController.isRTL ? 5 : 3);
            textView3.setLineSpacing((float) AndroidUtilities.dp(2.0f), DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            addView(textView3, LayoutHelper.createLinear(-2, -2, LocaleController.isRTL ? 5 : 3, 0, 28, 0, 10));
            HashMap hashMap = new HashMap();
            try {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(getResources().getAssets().open("countries.txt")));
                while (true) {
                    String readLine = bufferedReader.readLine();
                    if (readLine == null) {
                        break;
                    }
                    String[] split = readLine.split(";");
                    this.countriesArray.add(0, split[2]);
                    this.countriesMap.put(split[2], split[0]);
                    this.codesMap.put(split[0], split[2]);
                    if (split.length > 3) {
                        this.phoneFormatMap.put(split[0], split[3]);
                    }
                    hashMap.put(split[LoginActivity.done_button], split[2]);
                }
                bufferedReader.close();
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
            Collections.sort(this.countriesArray, new C16826(LoginActivity.this));
            try {
                TelephonyManager telephonyManager = (TelephonyManager) ApplicationLoader.applicationContext.getSystemService("phone");
                if (telephonyManager != null) {
                    toUpperCase = telephonyManager.getSimCountryIso().toUpperCase();
                    if (toUpperCase != null) {
                        str = (String) hashMap.get(toUpperCase);
                        if (!(str == null || this.countriesArray.indexOf(str) == -1)) {
                            this.codeField.setText((CharSequence) this.countriesMap.get(str));
                            this.countryState = 0;
                        }
                    }
                    if (this.codeField.length() == 0) {
                        this.countryButton.setText(LocaleController.getString("ChooseCountry", C0338R.string.ChooseCountry));
                        this.phoneField.setHintText(null);
                        this.countryState = LoginActivity.done_button;
                    }
                    if (this.codeField.length() == 0) {
                        this.phoneField.requestFocus();
                        this.phoneField.setSelection(this.phoneField.length());
                    } else {
                        this.codeField.requestFocus();
                    }
                    textView3 = new LinearLayout(context);
                    textView3.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
                    addView(textView3, LayoutHelper.createLinear(-1, -1, LocaleController.isRTL ? 5 : 3));
                    textView = new TextView(context);
                    textView.setGravity((LocaleController.isRTL ? 5 : 3) | LoginActivity.done_button);
                    textView.setTextColor(Theme.DIALOGS_PRINTING_TEXT_COLOR);
                    if (ThemeUtil.m2490b()) {
                        textView.setTextColor(AdvanceTheme.f2491b);
                    }
                    textView.setTextSize(LoginActivity.done_button, 14.0f);
                    textView.setLineSpacing((float) AndroidUtilities.dp(2.0f), DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                    textView.setPadding(0, AndroidUtilities.dp(24.0f), 0, 0);
                    textView3.addView(textView, LayoutHelper.createLinear(-2, -2, (LocaleController.isRTL ? 5 : 3) | 80, 0, 0, 0, 10));
                    textView.setText(LocaleController.getString("LoginWithBotToken", C0338R.string.LoginWithBotToken));
                    textView.setOnClickListener(new C16837(LoginActivity.this));
                }
            } catch (Throwable e2) {
                FileLog.m18e("tmessages", e2);
            }
            toUpperCase = null;
            if (toUpperCase != null) {
                str = (String) hashMap.get(toUpperCase);
                this.codeField.setText((CharSequence) this.countriesMap.get(str));
                this.countryState = 0;
            }
            if (this.codeField.length() == 0) {
                this.countryButton.setText(LocaleController.getString("ChooseCountry", C0338R.string.ChooseCountry));
                this.phoneField.setHintText(null);
                this.countryState = LoginActivity.done_button;
            }
            if (this.codeField.length() == 0) {
                this.codeField.requestFocus();
            } else {
                this.phoneField.requestFocus();
                this.phoneField.setSelection(this.phoneField.length());
            }
            textView3 = new LinearLayout(context);
            if (LocaleController.isRTL) {
            }
            textView3.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
            if (LocaleController.isRTL) {
            }
            addView(textView3, LayoutHelper.createLinear(-1, -1, LocaleController.isRTL ? 5 : 3));
            textView = new TextView(context);
            if (LocaleController.isRTL) {
            }
            textView.setGravity((LocaleController.isRTL ? 5 : 3) | LoginActivity.done_button);
            textView.setTextColor(Theme.DIALOGS_PRINTING_TEXT_COLOR);
            if (ThemeUtil.m2490b()) {
                textView.setTextColor(AdvanceTheme.f2491b);
            }
            textView.setTextSize(LoginActivity.done_button, 14.0f);
            textView.setLineSpacing((float) AndroidUtilities.dp(2.0f), DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            textView.setPadding(0, AndroidUtilities.dp(24.0f), 0, 0);
            if (LocaleController.isRTL) {
            }
            textView3.addView(textView, LayoutHelper.createLinear(-2, -2, (LocaleController.isRTL ? 5 : 3) | 80, 0, 0, 0, 10));
            textView.setText(LocaleController.getString("LoginWithBotToken", C0338R.string.LoginWithBotToken));
            textView.setOnClickListener(new C16837(LoginActivity.this));
        }

        public String getHeaderName() {
            return LocaleController.getString("YourPhone", C0338R.string.YourPhone);
        }

        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
            if (this.ignoreSelection) {
                this.ignoreSelection = false;
                return;
            }
            this.ignoreOnTextChange = true;
            this.codeField.setText((CharSequence) this.countriesMap.get((String) this.countriesArray.get(i)));
            this.ignoreOnTextChange = false;
        }

        public void onNextPressed() {
            if (LoginActivity.this.getParentActivity() != null && !this.nextPressed) {
                boolean z;
                TelephonyManager telephonyManager = (TelephonyManager) ApplicationLoader.applicationContext.getSystemService("phone");
                boolean z2 = (telephonyManager.getSimState() == LoginActivity.done_button || telephonyManager.getPhoneType() == 0) ? false : true;
                if (VERSION.SDK_INT < 23 || !z2) {
                    z = true;
                } else {
                    z = LoginActivity.this.getParentActivity().checkSelfPermission("android.permission.READ_PHONE_STATE") == 0;
                    boolean z3 = LoginActivity.this.getParentActivity().checkSelfPermission("android.permission.RECEIVE_SMS") == 0;
                    if (LoginActivity.this.checkPermissions) {
                        LoginActivity.this.permissionsItems.clear();
                        if (!z) {
                            LoginActivity.this.permissionsItems.add("android.permission.READ_PHONE_STATE");
                        }
                        if (!z3) {
                            LoginActivity.this.permissionsItems.add("android.permission.RECEIVE_SMS");
                        }
                        if (!LoginActivity.this.permissionsItems.isEmpty()) {
                            SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
                            if (sharedPreferences.getBoolean("firstlogin", true) || LoginActivity.this.getParentActivity().shouldShowRequestPermissionRationale("android.permission.READ_PHONE_STATE") || LoginActivity.this.getParentActivity().shouldShowRequestPermissionRationale("android.permission.RECEIVE_SMS")) {
                                sharedPreferences.edit().putBoolean("firstlogin", false).commit();
                                Builder builder = new Builder(LoginActivity.this.getParentActivity());
                                builder.setTitle(LocaleController.getString("AppName", C0338R.string.AppName));
                                builder.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), null);
                                if (LoginActivity.this.permissionsItems.size() == 2) {
                                    builder.setMessage(LocaleController.getString("AllowReadCallAndSms", C0338R.string.AllowReadCallAndSms));
                                } else if (z3) {
                                    builder.setMessage(LocaleController.getString("AllowReadCall", C0338R.string.AllowReadCall));
                                } else {
                                    builder.setMessage(LocaleController.getString("AllowReadSms", C0338R.string.AllowReadSms));
                                }
                                LoginActivity.this.permissionsDialog = LoginActivity.this.showDialog(builder.create());
                                return;
                            }
                            LoginActivity.this.getParentActivity().requestPermissions((String[]) LoginActivity.this.permissionsItems.toArray(new String[LoginActivity.this.permissionsItems.size()]), 6);
                            return;
                        }
                    }
                }
                if (this.countryState == LoginActivity.done_button) {
                    LoginActivity.this.needShowAlert(LocaleController.getString("AppName", C0338R.string.AppName), LocaleController.getString("ChooseCountry", C0338R.string.ChooseCountry));
                } else if (this.countryState == 2 && !BuildVars.DEBUG_VERSION && !this.codeField.getText().toString().equals("999")) {
                    LoginActivity.this.needShowAlert(LocaleController.getString("AppName", C0338R.string.AppName), LocaleController.getString("WrongCountry", C0338R.string.WrongCountry));
                } else if (this.codeField.length() == 0) {
                    LoginActivity.this.needShowAlert(LocaleController.getString("AppName", C0338R.string.AppName), LocaleController.getString("InvalidPhoneNumber", C0338R.string.InvalidPhoneNumber));
                } else {
                    ConnectionsManager.getInstance().cleanup();
                    TLObject tL_auth_sendCode = new TL_auth_sendCode();
                    String stripExceptNumbers = PhoneFormat.stripExceptNumbers(TtmlNode.ANONYMOUS_REGION_ID + this.codeField.getText() + this.phoneField.getText());
                    ConnectionsManager.getInstance().applyCountryPortNumber(stripExceptNumbers);
                    tL_auth_sendCode.api_hash = BuildVars.APP_HASH;
                    tL_auth_sendCode.api_id = BuildVars.APP_ID;
                    tL_auth_sendCode.phone_number = stripExceptNumbers;
                    z2 = z2 && z;
                    tL_auth_sendCode.allow_flashcall = z2;
                    if (tL_auth_sendCode.allow_flashcall) {
                        try {
                            Object line1Number = telephonyManager.getLine1Number();
                            boolean z4 = (line1Number == null || line1Number.length() == 0 || (!stripExceptNumbers.contains(line1Number) && !line1Number.contains(stripExceptNumbers))) ? false : true;
                            tL_auth_sendCode.current_number = z4;
                        } catch (Throwable e) {
                            tL_auth_sendCode.allow_flashcall = false;
                            FileLog.m18e("tmessages", e);
                        }
                    }
                    Bundle bundle = new Bundle();
                    bundle.putString("phone", "+" + this.codeField.getText() + this.phoneField.getText());
                    try {
                        bundle.putString("ephone", "+" + PhoneFormat.stripExceptNumbers(this.codeField.getText().toString()) + " " + PhoneFormat.stripExceptNumbers(this.phoneField.getText().toString()));
                    } catch (Throwable e2) {
                        FileLog.m18e("tmessages", e2);
                        bundle.putString("ephone", "+" + stripExceptNumbers);
                    }
                    bundle.putString("phoneFormated", stripExceptNumbers);
                    this.nextPressed = true;
                    LoginActivity.this.needShowProgress();
                    ConnectionsManager.getInstance().sendRequest(tL_auth_sendCode, new C16858(bundle, tL_auth_sendCode), 27);
                }
            }
        }

        public void onNothingSelected(AdapterView<?> adapterView) {
        }

        public void onShow() {
            super.onShow();
            if (this.phoneField == null) {
                return;
            }
            if (this.codeField.length() != 0) {
                AndroidUtilities.showKeyboard(this.phoneField);
                this.phoneField.requestFocus();
                this.phoneField.setSelection(this.phoneField.length());
                return;
            }
            AndroidUtilities.showKeyboard(this.codeField);
            this.codeField.requestFocus();
        }

        public void restoreStateParams(Bundle bundle) {
            CharSequence string = bundle.getString("phoneview_code");
            if (string != null) {
                this.codeField.setText(string);
            }
            string = bundle.getString("phoneview_phone");
            if (string != null) {
                this.phoneField.setText(string);
            }
        }

        public void saveStateParams(Bundle bundle) {
            String obj = this.codeField.getText().toString();
            if (obj.length() != 0) {
                bundle.putString("phoneview_code", obj);
            }
            obj = this.phoneField.getText().toString();
            if (obj.length() != 0) {
                bundle.putString("phoneview_phone", obj);
            }
        }

        public void selectCountry(String str) {
            if (this.countriesArray.indexOf(str) != -1) {
                this.ignoreOnTextChange = true;
                String str2 = (String) this.countriesMap.get(str);
                this.codeField.setText(str2);
                this.countryButton.setText(str);
                str2 = (String) this.phoneFormatMap.get(str2);
                this.phoneField.setHintText(str2 != null ? str2.replace('X', '\u2013') : null);
                this.countryState = 0;
                this.ignoreOnTextChange = false;
            }
        }
    }

    public class TokenView extends SlideView implements OnItemSelectedListener {
        private boolean ignoreOnTokenChange;
        private boolean ignoreSelection;
        private boolean nextPressed;
        private HintEditText tokenField;

        /* renamed from: com.hanista.mobogram.ui.LoginActivity.TokenView.1 */
        class C16861 implements TextWatcher {
            private int actionPosition;
            private int characterAction;
            final /* synthetic */ LoginActivity val$this$0;

            C16861(LoginActivity loginActivity) {
                this.val$this$0 = loginActivity;
                this.characterAction = -1;
            }

            public void afterTextChanged(Editable editable) {
                if (!TokenView.this.ignoreOnTokenChange) {
                    int selectionStart = TokenView.this.tokenField.getSelectionStart();
                    String str = "0123456789:abcdefghijklmnopqrstuvwxyz-ABCDEFGHIJKLMNOPQRSTUVWXYZ_";
                    String obj = TokenView.this.tokenField.getText().toString();
                    if (this.characterAction == 3) {
                        obj = obj.substring(0, this.actionPosition) + obj.substring(this.actionPosition + LoginActivity.done_button, obj.length());
                        selectionStart--;
                    }
                    CharSequence stringBuilder = new StringBuilder(obj.length());
                    for (int i = 0; i < obj.length(); i += LoginActivity.done_button) {
                        Object substring = obj.substring(i, i + LoginActivity.done_button);
                        if (str.contains(substring)) {
                            stringBuilder.append(substring);
                        }
                    }
                    TokenView.this.ignoreOnTokenChange = true;
                    String hintText = TokenView.this.tokenField.getHintText();
                    if (hintText != null) {
                        int i2 = 0;
                        while (i2 < stringBuilder.length()) {
                            if (i2 < hintText.length()) {
                                if (hintText.charAt(i2) == ' ') {
                                    stringBuilder.insert(i2, ' ');
                                    i2 += LoginActivity.done_button;
                                    if (!(selectionStart != i2 || this.characterAction == 2 || this.characterAction == 3)) {
                                        selectionStart += LoginActivity.done_button;
                                    }
                                }
                                i2 += LoginActivity.done_button;
                            } else {
                                stringBuilder.insert(i2, ' ');
                                if (!(selectionStart != i2 + LoginActivity.done_button || this.characterAction == 2 || this.characterAction == 3)) {
                                    selectionStart += LoginActivity.done_button;
                                }
                            }
                        }
                    }
                    TokenView.this.tokenField.setText(stringBuilder);
                    if (selectionStart >= 0) {
                        HintEditText access$6800 = TokenView.this.tokenField;
                        if (selectionStart > TokenView.this.tokenField.length()) {
                            selectionStart = TokenView.this.tokenField.length();
                        }
                        access$6800.setSelection(selectionStart);
                    }
                    TokenView.this.tokenField.onTextChange();
                    TokenView.this.ignoreOnTokenChange = false;
                }
            }

            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                if (i2 == 0 && i3 == LoginActivity.done_button) {
                    this.characterAction = LoginActivity.done_button;
                } else if (i2 != LoginActivity.done_button || i3 != 0) {
                    this.characterAction = -1;
                } else if (charSequence.charAt(i) != ' ' || i <= 0) {
                    this.characterAction = 2;
                } else {
                    this.characterAction = 3;
                    this.actionPosition = i - 1;
                }
            }

            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }
        }

        /* renamed from: com.hanista.mobogram.ui.LoginActivity.TokenView.2 */
        class C16872 implements OnEditorActionListener {
            final /* synthetic */ LoginActivity val$this$0;

            C16872(LoginActivity loginActivity) {
                this.val$this$0 = loginActivity;
            }

            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i != 5) {
                    return false;
                }
                TokenView.this.onNextPressed();
                return true;
            }
        }

        /* renamed from: com.hanista.mobogram.ui.LoginActivity.TokenView.3 */
        class C16883 implements View.OnClickListener {
            final /* synthetic */ LoginActivity val$this$0;

            C16883(LoginActivity loginActivity) {
                this.val$this$0 = loginActivity;
            }

            public void onClick(View view) {
                LoginActivity.this.setPage(0, true, null, true);
            }
        }

        /* renamed from: com.hanista.mobogram.ui.LoginActivity.TokenView.4 */
        class C16904 implements RequestDelegate {

            /* renamed from: com.hanista.mobogram.ui.LoginActivity.TokenView.4.1 */
            class C16891 implements Runnable {
                final /* synthetic */ TL_error val$error;
                final /* synthetic */ TLObject val$response;

                C16891(TL_error tL_error, TLObject tLObject) {
                    this.val$error = tL_error;
                    this.val$response = tLObject;
                }

                public void run() {
                    TokenView.this.nextPressed = false;
                    LoginActivity.this.needHideProgress();
                    if (this.val$error == null) {
                        TL_auth_authorization tL_auth_authorization = (TL_auth_authorization) this.val$response;
                        ConnectionsManager.getInstance().setUserId(tL_auth_authorization.user.id);
                        UserConfig.clearConfig();
                        MessagesController.getInstance().cleanup();
                        UserConfig.setCurrentUser(tL_auth_authorization.user);
                        UserConfig.isRobot = true;
                        UserConfig.saveConfig(true);
                        MessagesStorage.getInstance().cleanup(true);
                        ArrayList arrayList = new ArrayList();
                        arrayList.add(tL_auth_authorization.user);
                        MessagesStorage.getInstance().putUsersAndChats(arrayList, null, true, true);
                        MessagesController.getInstance().putUser(tL_auth_authorization.user, false);
                        ContactsController.getInstance().checkAppAccount();
                        MessagesController.getInstance().getBlockedUsers(true);
                        LoginActivity.this.needFinishActivity();
                    } else if (this.val$error.text == null) {
                    } else {
                        if (this.val$error.text.contains("ACCESS_TOKEN_INVALID")) {
                            LoginActivity.this.needShowAlert(LocaleController.getString("AppName", C0338R.string.AppName), LocaleController.getString("InvalidAccessToken", C0338R.string.InvalidAccessToken));
                        } else if (this.val$error.text.startsWith("FLOOD_WAIT")) {
                            LoginActivity.this.needShowAlert(LocaleController.getString("AppName", C0338R.string.AppName), LocaleController.getString("FloodWait", C0338R.string.FloodWait));
                        } else if (this.val$error.code != NotificationManagerCompat.IMPORTANCE_UNSPECIFIED) {
                            LoginActivity.this.needShowAlert(LocaleController.getString("AppName", C0338R.string.AppName), LocaleController.getString("ErrorOccurred", C0338R.string.ErrorOccurred) + "\n" + this.val$error.text);
                        }
                    }
                }
            }

            C16904() {
            }

            public void run(TLObject tLObject, TL_error tL_error) {
                AndroidUtilities.runOnUIThread(new C16891(tL_error, tLObject));
            }
        }

        public TokenView(Context context) {
            super(context);
            this.ignoreSelection = false;
            this.ignoreOnTokenChange = false;
            this.nextPressed = false;
            setOrientation(LoginActivity.done_button);
            View view = new View(context);
            view.setPadding(AndroidUtilities.dp(12.0f), 0, AndroidUtilities.dp(12.0f), 0);
            view.setBackgroundColor(-2368549);
            addView(view, LayoutHelper.createLinear(-1, LoginActivity.done_button, 4.0f, -17.5f, 4.0f, 0.0f));
            view = new LinearLayout(context);
            view.setOrientation(0);
            addView(view, LayoutHelper.createLinear(-1, -2, 0.0f, 20.0f, 0.0f, 0.0f));
            View textView = new TextView(context);
            textView.setText(TtmlNode.ANONYMOUS_REGION_ID);
            textView.setTextColor(Theme.STICKERS_SHEET_TITLE_TEXT_COLOR);
            textView.setTextSize(LoginActivity.done_button, 18.0f);
            view.addView(textView, LayoutHelper.createLinear(-2, -2));
            this.tokenField = new HintEditText(context);
            this.tokenField.setInputType(LoginActivity.done_button);
            this.tokenField.setTextColor(Theme.STICKERS_SHEET_TITLE_TEXT_COLOR);
            this.tokenField.setHintTextColor(Theme.SHARE_SHEET_EDIT_PLACEHOLDER_TEXT_COLOR);
            this.tokenField.setPadding(0, 0, 0, 0);
            AndroidUtilities.clearCursorDrawable(this.tokenField);
            this.tokenField.setTextSize(LoginActivity.done_button, 18.0f);
            this.tokenField.setMaxLines(LoginActivity.done_button);
            this.tokenField.setGravity(19);
            this.tokenField.setImeOptions(268435461);
            view.addView(this.tokenField, LayoutHelper.createFrame(-1, 36.0f));
            this.tokenField.addTextChangedListener(new C16861(LoginActivity.this));
            this.tokenField.setOnEditorActionListener(new C16872(LoginActivity.this));
            View textView2 = new TextView(context);
            textView2.setText(LocaleController.getString("StartTextBot", C0338R.string.StartTextBot));
            textView2.setTextColor(Theme.ATTACH_SHEET_TEXT_COLOR);
            textView2.setTextSize(LoginActivity.done_button, 14.0f);
            textView2.setGravity(LocaleController.isRTL ? 5 : 3);
            textView2.setLineSpacing((float) AndroidUtilities.dp(2.0f), DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            addView(textView2, LayoutHelper.createLinear(-2, -2, LocaleController.isRTL ? 5 : 3, 0, 28, 0, 10));
            textView2 = new TextView(context);
            textView2.setText(LocaleController.getString("LoginWithBotTokenLimits", C0338R.string.LoginWithBotTokenLimits));
            textView2.setTextColor(Theme.ATTACH_SHEET_TEXT_COLOR);
            textView2.setTextSize(LoginActivity.done_button, 14.0f);
            textView2.setGravity(LocaleController.isRTL ? 5 : 3);
            textView2.setLineSpacing((float) AndroidUtilities.dp(2.0f), DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            addView(textView2, LayoutHelper.createLinear(-2, -2, LocaleController.isRTL ? 5 : 3, 0, 28, 0, 10));
            textView2 = new LinearLayout(context);
            textView2.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
            addView(textView2, LayoutHelper.createLinear(-1, -1, LocaleController.isRTL ? 5 : 3));
            View textView3 = new TextView(context);
            textView3.setGravity((LocaleController.isRTL ? 5 : 3) | LoginActivity.done_button);
            textView3.setTextColor(Theme.DIALOGS_PRINTING_TEXT_COLOR);
            if (ThemeUtil.m2490b()) {
                textView3.setTextColor(AdvanceTheme.f2491b);
            }
            textView3.setTextSize(LoginActivity.done_button, 14.0f);
            textView3.setLineSpacing((float) AndroidUtilities.dp(2.0f), DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            textView3.setPadding(0, AndroidUtilities.dp(24.0f), 0, 0);
            textView2.addView(textView3, LayoutHelper.createLinear(-2, -2, (LocaleController.isRTL ? 5 : 3) | 80, 0, 0, 0, 10));
            textView3.setText(LocaleController.getString("LoginWithPhoneNumber", C0338R.string.LoginWithPhoneNumber));
            textView3.setOnClickListener(new C16883(LoginActivity.this));
        }

        public String getHeaderName() {
            return LocaleController.getString("YourToken", C0338R.string.YourToken);
        }

        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
            if (this.ignoreSelection) {
                this.ignoreSelection = false;
            }
        }

        public void onNextPressed() {
            if (LoginActivity.this.getParentActivity() != null && !this.nextPressed) {
                this.nextPressed = true;
                ConnectionsManager.getInstance().cleanup();
                TLObject tL_auth_importBotAuthorization = new TL_auth_importBotAuthorization();
                tL_auth_importBotAuthorization.api_hash = BuildVars.APP_HASH;
                tL_auth_importBotAuthorization.api_id = BuildVars.APP_ID;
                tL_auth_importBotAuthorization.bot_auth_token = this.tokenField.getText().toString();
                tL_auth_importBotAuthorization.flags = 0;
                LoginActivity.this.needShowProgress();
                ConnectionsManager.getInstance().sendRequest(tL_auth_importBotAuthorization, new C16904(), 10);
            }
        }

        public void onNothingSelected(AdapterView<?> adapterView) {
        }

        public void onShow() {
            super.onShow();
            if (this.tokenField != null) {
                AndroidUtilities.showKeyboard(this.tokenField);
                this.tokenField.requestFocus();
                this.tokenField.setSelection(this.tokenField.length());
            }
        }

        public void restoreStateParams(Bundle bundle) {
            CharSequence string = bundle.getString("tokenview_token");
            if (string != null) {
                this.tokenField.setText(string);
            }
        }

        public void saveStateParams(Bundle bundle) {
            String obj = this.tokenField.getText().toString();
            if (obj.length() != 0) {
                bundle.putString("tokenview_token", obj);
            }
        }
    }

    public LoginActivity() {
        this.currentViewNum = 0;
        this.views = new SlideView[10];
        this.permissionsItems = new ArrayList();
        this.checkPermissions = true;
    }

    private void clearCurrentState() {
        Editor edit = ApplicationLoader.applicationContext.getSharedPreferences("logininfo2", 0).edit();
        edit.clear();
        edit.commit();
    }

    private void fillNextCodeParams(Bundle bundle, TL_auth_sentCode tL_auth_sentCode) {
        bundle.putString("phoneHash", tL_auth_sentCode.phone_code_hash);
        if (tL_auth_sentCode.next_type instanceof TL_auth_codeTypeCall) {
            bundle.putInt("nextType", 4);
        } else if (tL_auth_sentCode.next_type instanceof TL_auth_codeTypeFlashCall) {
            bundle.putInt("nextType", 3);
        } else if (tL_auth_sentCode.next_type instanceof TL_auth_codeTypeSms) {
            bundle.putInt("nextType", 2);
        }
        if (tL_auth_sentCode.type instanceof TL_auth_sentCodeTypeApp) {
            bundle.putInt("type", done_button);
            bundle.putInt("length", tL_auth_sentCode.type.length);
            setPage(done_button, true, bundle, false);
            return;
        }
        if (tL_auth_sentCode.timeout == 0) {
            tL_auth_sentCode.timeout = 60;
        }
        bundle.putInt("timeout", tL_auth_sentCode.timeout * PointerIconCompat.TYPE_DEFAULT);
        if (tL_auth_sentCode.type instanceof TL_auth_sentCodeTypeCall) {
            bundle.putInt("type", 4);
            bundle.putInt("length", tL_auth_sentCode.type.length);
            setPage(4, true, bundle, false);
        } else if (tL_auth_sentCode.type instanceof TL_auth_sentCodeTypeFlashCall) {
            bundle.putInt("type", 3);
            bundle.putString("pattern", tL_auth_sentCode.type.pattern);
            setPage(3, true, bundle, false);
        } else if (tL_auth_sentCode.type instanceof TL_auth_sentCodeTypeSms) {
            bundle.putInt("type", 2);
            bundle.putInt("length", tL_auth_sentCode.type.length);
            setPage(2, true, bundle, false);
        }
    }

    private Bundle loadCurrentState() {
        try {
            Bundle bundle = new Bundle();
            for (Entry entry : ApplicationLoader.applicationContext.getSharedPreferences("logininfo2", 0).getAll().entrySet()) {
                String str = (String) entry.getKey();
                Object value = entry.getValue();
                String[] split = str.split("_\\|_");
                if (split.length == done_button) {
                    if (value instanceof String) {
                        bundle.putString(str, (String) value);
                    } else if (value instanceof Integer) {
                        bundle.putInt(str, ((Integer) value).intValue());
                    }
                } else if (split.length == 2) {
                    Bundle bundle2 = bundle.getBundle(split[0]);
                    if (bundle2 == null) {
                        bundle2 = new Bundle();
                        bundle.putBundle(split[0], bundle2);
                    }
                    if (value instanceof String) {
                        bundle2.putString(split[done_button], (String) value);
                    } else if (value instanceof Integer) {
                        bundle2.putInt(split[done_button], ((Integer) value).intValue());
                    }
                }
            }
            return bundle;
        } catch (Throwable e) {
            FileLog.m18e("tmessages", e);
            return null;
        }
    }

    private void needFinishActivity() {
        clearCurrentState();
        presentFragment(new DialogsActivity(null), true);
        NotificationCenter.getInstance().postNotificationName(NotificationCenter.mainUserInfoChanged, new Object[0]);
    }

    private void needShowAlert(String str, String str2) {
        if (str2 != null && getParentActivity() != null) {
            Builder builder = new Builder(getParentActivity());
            builder.setTitle(str);
            builder.setMessage(str2);
            builder.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), null);
            showDialog(builder.create());
        }
    }

    private void needShowInvalidAlert(String str, boolean z) {
        if (getParentActivity() != null) {
            Builder builder = new Builder(getParentActivity());
            builder.setTitle(LocaleController.getString("AppName", C0338R.string.AppName));
            if (z) {
                builder.setMessage(LocaleController.getString("BannedPhoneNumber", C0338R.string.BannedPhoneNumber));
            } else {
                builder.setMessage(LocaleController.getString("InvalidPhoneNumber", C0338R.string.InvalidPhoneNumber));
            }
            builder.setNeutralButton(LocaleController.getString("BotHelp", C0338R.string.BotHelp), new C16292(z, str));
            builder.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), null);
            showDialog(builder.create());
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

    private void putBundleToEditor(Bundle bundle, Editor editor, String str) {
        for (String str2 : bundle.keySet()) {
            Object obj = bundle.get(str2);
            if (obj instanceof String) {
                if (str != null) {
                    editor.putString(str + "_|_" + str2, (String) obj);
                } else {
                    editor.putString(str2, (String) obj);
                }
            } else if (obj instanceof Integer) {
                if (str != null) {
                    editor.putInt(str + "_|_" + str2, ((Integer) obj).intValue());
                } else {
                    editor.putInt(str2, ((Integer) obj).intValue());
                }
            } else if (obj instanceof Bundle) {
                putBundleToEditor((Bundle) obj, editor, str2);
            }
        }
    }

    public View createView(Context context) {
        int i;
        this.actionBar.setTitle(LocaleController.getString("AppName", C0338R.string.AppName));
        this.actionBar.setActionBarMenuOnItemClick(new C16281());
        this.doneButton = this.actionBar.createMenu().addItemWithWidth((int) done_button, (int) C0338R.drawable.ic_done, AndroidUtilities.dp(56.0f));
        this.fragmentView = new ScrollView(context);
        ScrollView scrollView = (ScrollView) this.fragmentView;
        scrollView.setFillViewport(true);
        View frameLayout = new FrameLayout(context);
        scrollView.addView(frameLayout, LayoutHelper.createScroll(-1, -2, 51));
        this.views[0] = new PhoneView(context);
        this.views[done_button] = new LoginActivitySmsView(context, done_button);
        this.views[2] = new LoginActivitySmsView(context, 2);
        this.views[3] = new LoginActivitySmsView(context, 3);
        this.views[4] = new LoginActivitySmsView(context, 4);
        this.views[5] = new LoginActivityRegisterView(context);
        this.views[6] = new LoginActivityPasswordView(context);
        this.views[7] = new LoginActivityRecoverView(this, context);
        this.views[8] = new LoginActivityResetWaitView(this, context);
        this.views[9] = new TokenView(context);
        int i2 = 0;
        while (i2 < this.views.length) {
            this.views[i2].setVisibility(i2 == 0 ? 0 : 8);
            frameLayout.addView(this.views[i2], LayoutHelper.createFrame(-1, i2 == 0 ? -2.0f : Face.UNCOMPUTED_PROBABILITY, 51, AndroidUtilities.isTablet() ? 26.0f : 18.0f, BitmapDescriptorFactory.HUE_ORANGE, AndroidUtilities.isTablet() ? 26.0f : 18.0f, 0.0f));
            i2 += done_button;
        }
        Bundle loadCurrentState = loadCurrentState();
        if (loadCurrentState != null) {
            this.currentViewNum = loadCurrentState.getInt("currentViewNum", 0);
            if (this.currentViewNum >= done_button && this.currentViewNum <= 4) {
                i = loadCurrentState.getInt("open");
                if (i != 0 && Math.abs((System.currentTimeMillis() / 1000) - ((long) i)) >= 86400) {
                    this.currentViewNum = 0;
                    loadCurrentState = null;
                    clearCurrentState();
                }
            }
        }
        this.actionBar.setTitle(this.views[this.currentViewNum].getHeaderName());
        i = 0;
        while (i < this.views.length) {
            if (loadCurrentState != null) {
                if (i < done_button || i > 4) {
                    this.views[i].restoreStateParams(loadCurrentState);
                } else if (i == this.currentViewNum) {
                    this.views[i].restoreStateParams(loadCurrentState);
                }
            }
            if (this.currentViewNum == i) {
                this.actionBar.setBackButtonImage(this.views[i].needBackButton() ? C0338R.drawable.ic_ab_back : 0);
                this.views[i].setVisibility(0);
                this.views[i].onShow();
                if (i == 3 || i == 8) {
                    this.doneButton.setVisibility(8);
                }
            } else {
                this.views[i].setVisibility(8);
            }
            i += done_button;
        }
        return this.fragmentView;
    }

    public void needHideProgress() {
        if (this.progressDialog != null) {
            try {
                this.progressDialog.dismiss();
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
            this.progressDialog = null;
        }
    }

    public boolean onBackPressed() {
        int i = 0;
        if (this.currentViewNum == 0 || this.currentViewNum == 9) {
            while (i < this.views.length) {
                if (this.views[i] != null) {
                    this.views[i].onDestroyActivity();
                }
                i += done_button;
            }
            clearCurrentState();
            return true;
        } else if (this.currentViewNum == 6) {
            this.views[this.currentViewNum].onBackPressed();
            setPage(0, true, null, true);
            return false;
        } else if (this.currentViewNum != 7 && this.currentViewNum != 8) {
            return false;
        } else {
            this.views[this.currentViewNum].onBackPressed();
            setPage(6, true, null, true);
            return false;
        }
    }

    protected void onDialogDismiss(Dialog dialog) {
        if (VERSION.SDK_INT >= 23 && dialog == this.permissionsDialog && !this.permissionsItems.isEmpty() && getParentActivity() != null) {
            getParentActivity().requestPermissions((String[]) this.permissionsItems.toArray(new String[this.permissionsItems.size()]), 6);
        }
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        for (int i = 0; i < this.views.length; i += done_button) {
            if (this.views[i] != null) {
                this.views[i].onDestroyActivity();
            }
        }
        if (this.progressDialog != null) {
            try {
                this.progressDialog.dismiss();
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
            this.progressDialog = null;
        }
    }

    public void onPause() {
        super.onPause();
        AndroidUtilities.removeAdjustResize(getParentActivity(), this.classGuid);
    }

    public void onRequestPermissionsResultFragment(int i, String[] strArr, int[] iArr) {
        if (i == 6) {
            this.checkPermissions = false;
            if (this.currentViewNum == 0) {
                this.views[this.currentViewNum].onNextPressed();
            }
        }
    }

    public void onResume() {
        super.onResume();
        AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
        try {
            if (this.currentViewNum >= done_button && this.currentViewNum <= 4 && (this.views[this.currentViewNum] instanceof LoginActivitySmsView)) {
                int access$200 = ((LoginActivitySmsView) this.views[this.currentViewNum]).openTime;
                if (access$200 != 0 && Math.abs((System.currentTimeMillis() / 1000) - ((long) access$200)) >= 86400) {
                    this.views[this.currentViewNum].onBackPressed();
                    setPage(0, false, null, true);
                }
            }
        } catch (Throwable e) {
            FileLog.m18e("tmessages", e);
        }
    }

    public void saveSelfArgs(Bundle bundle) {
        try {
            Bundle bundle2 = new Bundle();
            bundle2.putInt("currentViewNum", this.currentViewNum);
            for (int i = 0; i <= this.currentViewNum; i += done_button) {
                SlideView slideView = this.views[i];
                if (slideView != null) {
                    slideView.saveStateParams(bundle2);
                }
            }
            Editor edit = ApplicationLoader.applicationContext.getSharedPreferences("logininfo2", 0).edit();
            edit.clear();
            putBundleToEditor(bundle2, edit, null);
            edit.commit();
        } catch (Throwable e) {
            FileLog.m18e("tmessages", e);
        }
    }

    public void setPage(int i, boolean z, Bundle bundle, boolean z2) {
        int i2 = C0338R.drawable.ic_ab_back;
        if (i == 3 || i == 8) {
            this.doneButton.setVisibility(8);
        } else {
            if (i == 0) {
                this.checkPermissions = true;
            }
            this.doneButton.setVisibility(0);
        }
        if (z) {
            SlideView slideView = this.views[this.currentViewNum];
            SlideView slideView2 = this.views[i];
            this.currentViewNum = i;
            ActionBar actionBar = this.actionBar;
            if (!slideView2.needBackButton()) {
                i2 = 0;
            }
            actionBar.setBackButtonImage(i2);
            slideView2.setParams(bundle);
            this.actionBar.setTitle(slideView2.getHeaderName());
            slideView2.onShow();
            slideView2.setX(z2 ? (float) (-AndroidUtilities.displaySize.x) : (float) AndroidUtilities.displaySize.x);
            slideView.animate().setInterpolator(new AccelerateDecelerateInterpolator()).setListener(new C16303(slideView)).setDuration(300).translationX(z2 ? (float) AndroidUtilities.displaySize.x : (float) (-AndroidUtilities.displaySize.x)).start();
            slideView2.animate().setInterpolator(new AccelerateDecelerateInterpolator()).setListener(new C16314(slideView2)).setDuration(300).translationX(0.0f).start();
            return;
        }
        ActionBar actionBar2 = this.actionBar;
        if (!this.views[i].needBackButton()) {
            i2 = 0;
        }
        actionBar2.setBackButtonImage(i2);
        this.views[this.currentViewNum].setVisibility(8);
        this.currentViewNum = i;
        this.views[i].setParams(bundle);
        this.views[i].setVisibility(0);
        this.actionBar.setTitle(this.views[i].getHeaderName());
        this.views[i].onShow();
    }
}
