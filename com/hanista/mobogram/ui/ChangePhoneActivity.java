package com.hanista.mobogram.ui;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.view.PointerIconCompat;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputFilter.LengthFilter;
import android.text.TextUtils.TruncateAt;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.vision.face.Face;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.PhoneFormat.PhoneFormat;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.AnimatorListenerAdapterProxy;
import com.hanista.mobogram.messenger.ApplicationLoader;
import com.hanista.mobogram.messenger.BuildVars;
import com.hanista.mobogram.messenger.FileLog;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.MessagesController;
import com.hanista.mobogram.messenger.MessagesStorage;
import com.hanista.mobogram.messenger.NotificationCenter;
import com.hanista.mobogram.messenger.NotificationCenter.NotificationCenterDelegate;
import com.hanista.mobogram.messenger.UserConfig;
import com.hanista.mobogram.messenger.exoplayer.DefaultLoadControl;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;
import com.hanista.mobogram.mobo.p008i.FontUtil;
import com.hanista.mobogram.mobo.p020s.AdvanceTheme;
import com.hanista.mobogram.mobo.p020s.ThemeUtil;
import com.hanista.mobogram.tgnet.ConnectionsManager;
import com.hanista.mobogram.tgnet.RequestDelegate;
import com.hanista.mobogram.tgnet.TLObject;
import com.hanista.mobogram.tgnet.TLRPC.TL_account_changePhone;
import com.hanista.mobogram.tgnet.TLRPC.TL_account_sendChangePhoneCode;
import com.hanista.mobogram.tgnet.TLRPC.TL_auth_cancelCode;
import com.hanista.mobogram.tgnet.TLRPC.TL_auth_codeTypeCall;
import com.hanista.mobogram.tgnet.TLRPC.TL_auth_codeTypeFlashCall;
import com.hanista.mobogram.tgnet.TLRPC.TL_auth_codeTypeSms;
import com.hanista.mobogram.tgnet.TLRPC.TL_auth_resendCode;
import com.hanista.mobogram.tgnet.TLRPC.TL_auth_sentCode;
import com.hanista.mobogram.tgnet.TLRPC.TL_auth_sentCodeTypeApp;
import com.hanista.mobogram.tgnet.TLRPC.TL_auth_sentCodeTypeCall;
import com.hanista.mobogram.tgnet.TLRPC.TL_auth_sentCodeTypeFlashCall;
import com.hanista.mobogram.tgnet.TLRPC.TL_auth_sentCodeTypeSms;
import com.hanista.mobogram.tgnet.TLRPC.TL_error;
import com.hanista.mobogram.tgnet.TLRPC.User;
import com.hanista.mobogram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import com.hanista.mobogram.ui.ActionBar.ActionBarMenu;
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
import java.util.Timer;
import java.util.TimerTask;

public class ChangePhoneActivity extends BaseFragment {
    private static final int done_button = 1;
    private boolean checkPermissions;
    private int currentViewNum;
    private View doneButton;
    private Dialog permissionsDialog;
    private ArrayList<String> permissionsItems;
    private ProgressDialog progressDialog;
    private SlideView[] views;

    /* renamed from: com.hanista.mobogram.ui.ChangePhoneActivity.1 */
    class C11291 extends ActionBarMenuOnItemClick {
        C11291() {
        }

        public void onItemClick(int i) {
            if (i == ChangePhoneActivity.done_button) {
                ChangePhoneActivity.this.views[ChangePhoneActivity.this.currentViewNum].onNextPressed();
            } else if (i == -1) {
                ChangePhoneActivity.this.finishFragment();
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ChangePhoneActivity.2 */
    class C11302 extends AnimatorListenerAdapterProxy {
        final /* synthetic */ SlideView val$newView;
        final /* synthetic */ SlideView val$outView;

        C11302(SlideView slideView, SlideView slideView2) {
            this.val$newView = slideView;
            this.val$outView = slideView2;
        }

        public void onAnimationEnd(Animator animator) {
            this.val$outView.setVisibility(8);
            this.val$outView.setX(0.0f);
        }

        public void onAnimationStart(Animator animator) {
            this.val$newView.setVisibility(0);
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

        /* renamed from: com.hanista.mobogram.ui.ChangePhoneActivity.LoginActivitySmsView.1 */
        class C11311 implements TextWatcher {
            final /* synthetic */ ChangePhoneActivity val$this$0;

            C11311(ChangePhoneActivity changePhoneActivity) {
                this.val$this$0 = changePhoneActivity;
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

        /* renamed from: com.hanista.mobogram.ui.ChangePhoneActivity.LoginActivitySmsView.2 */
        class C11322 implements OnEditorActionListener {
            final /* synthetic */ ChangePhoneActivity val$this$0;

            C11322(ChangePhoneActivity changePhoneActivity) {
                this.val$this$0 = changePhoneActivity;
            }

            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i != 5) {
                    return false;
                }
                LoginActivitySmsView.this.onNextPressed();
                return true;
            }
        }

        /* renamed from: com.hanista.mobogram.ui.ChangePhoneActivity.LoginActivitySmsView.3 */
        class C11333 implements OnClickListener {
            final /* synthetic */ ChangePhoneActivity val$this$0;

            C11333(ChangePhoneActivity changePhoneActivity) {
                this.val$this$0 = changePhoneActivity;
            }

            public void onClick(View view) {
                if (!LoginActivitySmsView.this.nextPressed) {
                    if (LoginActivitySmsView.this.nextType == 0 || LoginActivitySmsView.this.nextType == 4) {
                        try {
                            PackageInfo packageInfo = ApplicationLoader.applicationContext.getPackageManager().getPackageInfo(ApplicationLoader.applicationContext.getPackageName(), 0);
                            String format = String.format(Locale.US, "%s (%d)", new Object[]{packageInfo.versionName, Integer.valueOf(packageInfo.versionCode)});
                            Intent intent = new Intent("android.intent.action.SEND");
                            intent.setType("message/rfc822");
                            String[] strArr = new String[ChangePhoneActivity.done_button];
                            strArr[0] = "sms@stel.com";
                            intent.putExtra("android.intent.extra.EMAIL", strArr);
                            intent.putExtra("android.intent.extra.SUBJECT", "Android registration/login issue " + format + " " + LoginActivitySmsView.this.emailPhone);
                            intent.putExtra("android.intent.extra.TEXT", "Phone: " + LoginActivitySmsView.this.requestPhone + "\nApp version: " + format + "\nOS version: SDK " + VERSION.SDK_INT + "\nDevice Name: " + Build.MANUFACTURER + Build.MODEL + "\nLocale: " + Locale.getDefault() + "\nError: " + LoginActivitySmsView.this.lastError);
                            LoginActivitySmsView.this.getContext().startActivity(Intent.createChooser(intent, "Send email..."));
                            return;
                        } catch (Exception e) {
                            ChangePhoneActivity.this.needShowAlert(LocaleController.getString("NoMailInstalled", C0338R.string.NoMailInstalled));
                            return;
                        }
                    }
                    LoginActivitySmsView.this.resendCode();
                }
            }
        }

        /* renamed from: com.hanista.mobogram.ui.ChangePhoneActivity.LoginActivitySmsView.4 */
        class C11354 implements OnClickListener {
            final /* synthetic */ ChangePhoneActivity val$this$0;

            /* renamed from: com.hanista.mobogram.ui.ChangePhoneActivity.LoginActivitySmsView.4.1 */
            class C11341 implements RequestDelegate {
                C11341() {
                }

                public void run(TLObject tLObject, TL_error tL_error) {
                }
            }

            C11354(ChangePhoneActivity changePhoneActivity) {
                this.val$this$0 = changePhoneActivity;
            }

            public void onClick(View view) {
                TLObject tL_auth_cancelCode = new TL_auth_cancelCode();
                tL_auth_cancelCode.phone_number = LoginActivitySmsView.this.requestPhone;
                tL_auth_cancelCode.phone_code_hash = LoginActivitySmsView.this.phoneHash;
                ConnectionsManager.getInstance().sendRequest(tL_auth_cancelCode, new C11341(), 2);
                LoginActivitySmsView.this.onBackPressed();
                ChangePhoneActivity.this.setPage(0, true, null, true);
            }
        }

        /* renamed from: com.hanista.mobogram.ui.ChangePhoneActivity.LoginActivitySmsView.5 */
        class C11375 implements RequestDelegate {
            final /* synthetic */ Bundle val$params;

            /* renamed from: com.hanista.mobogram.ui.ChangePhoneActivity.LoginActivitySmsView.5.1 */
            class C11361 implements Runnable {
                final /* synthetic */ TL_error val$error;
                final /* synthetic */ TLObject val$response;

                C11361(TL_error tL_error, TLObject tLObject) {
                    this.val$error = tL_error;
                    this.val$response = tLObject;
                }

                public void run() {
                    LoginActivitySmsView.this.nextPressed = false;
                    if (this.val$error == null) {
                        ChangePhoneActivity.this.fillNextCodeParams(C11375.this.val$params, (TL_auth_sentCode) this.val$response);
                    } else if (this.val$error.text != null) {
                        if (this.val$error.text.contains("PHONE_NUMBER_INVALID")) {
                            ChangePhoneActivity.this.needShowAlert(LocaleController.getString("InvalidPhoneNumber", C0338R.string.InvalidPhoneNumber));
                        } else if (this.val$error.text.contains("PHONE_CODE_EMPTY") || this.val$error.text.contains("PHONE_CODE_INVALID")) {
                            ChangePhoneActivity.this.needShowAlert(LocaleController.getString("InvalidCode", C0338R.string.InvalidCode));
                        } else if (this.val$error.text.contains("PHONE_CODE_EXPIRED")) {
                            LoginActivitySmsView.this.onBackPressed();
                            ChangePhoneActivity.this.setPage(0, true, null, true);
                            ChangePhoneActivity.this.needShowAlert(LocaleController.getString("CodeExpired", C0338R.string.CodeExpired));
                        } else if (this.val$error.text.startsWith("FLOOD_WAIT")) {
                            ChangePhoneActivity.this.needShowAlert(LocaleController.getString("FloodWait", C0338R.string.FloodWait));
                        } else if (this.val$error.code != NotificationManagerCompat.IMPORTANCE_UNSPECIFIED) {
                            ChangePhoneActivity.this.needShowAlert(LocaleController.getString("ErrorOccurred", C0338R.string.ErrorOccurred) + "\n" + this.val$error.text);
                        }
                    }
                    ChangePhoneActivity.this.needHideProgress();
                }
            }

            C11375(Bundle bundle) {
                this.val$params = bundle;
            }

            public void run(TLObject tLObject, TL_error tL_error) {
                AndroidUtilities.runOnUIThread(new C11361(tL_error, tLObject));
            }
        }

        /* renamed from: com.hanista.mobogram.ui.ChangePhoneActivity.LoginActivitySmsView.6 */
        class C11396 extends TimerTask {

            /* renamed from: com.hanista.mobogram.ui.ChangePhoneActivity.LoginActivitySmsView.6.1 */
            class C11381 implements Runnable {
                C11381() {
                }

                public void run() {
                    if (LoginActivitySmsView.this.codeTime <= PointerIconCompat.TYPE_DEFAULT) {
                        LoginActivitySmsView.this.problemText.setVisibility(0);
                        LoginActivitySmsView.this.destroyCodeTimer();
                    }
                }
            }

            C11396() {
            }

            public void run() {
                double currentTimeMillis = (double) System.currentTimeMillis();
                LoginActivitySmsView.this.codeTime = (int) (((double) LoginActivitySmsView.this.codeTime) - (currentTimeMillis - LoginActivitySmsView.this.lastCodeTime));
                LoginActivitySmsView.this.lastCodeTime = currentTimeMillis;
                AndroidUtilities.runOnUIThread(new C11381());
            }
        }

        /* renamed from: com.hanista.mobogram.ui.ChangePhoneActivity.LoginActivitySmsView.7 */
        class C11437 extends TimerTask {

            /* renamed from: com.hanista.mobogram.ui.ChangePhoneActivity.LoginActivitySmsView.7.1 */
            class C11421 implements Runnable {

                /* renamed from: com.hanista.mobogram.ui.ChangePhoneActivity.LoginActivitySmsView.7.1.1 */
                class C11411 implements RequestDelegate {

                    /* renamed from: com.hanista.mobogram.ui.ChangePhoneActivity.LoginActivitySmsView.7.1.1.1 */
                    class C11401 implements Runnable {
                        final /* synthetic */ TL_error val$error;

                        C11401(TL_error tL_error) {
                            this.val$error = tL_error;
                        }

                        public void run() {
                            LoginActivitySmsView.this.lastError = this.val$error.text;
                        }
                    }

                    C11411() {
                    }

                    public void run(TLObject tLObject, TL_error tL_error) {
                        if (tL_error != null && tL_error.text != null) {
                            AndroidUtilities.runOnUIThread(new C11401(tL_error));
                        }
                    }
                }

                C11421() {
                }

                public void run() {
                    if (LoginActivitySmsView.this.time >= PointerIconCompat.TYPE_DEFAULT) {
                        int access$3300 = (LoginActivitySmsView.this.time / PointerIconCompat.TYPE_DEFAULT) - (((LoginActivitySmsView.this.time / PointerIconCompat.TYPE_DEFAULT) / 60) * 60);
                        if (LoginActivitySmsView.this.nextType == 4 || LoginActivitySmsView.this.nextType == 3) {
                            LoginActivitySmsView.this.timeText.setText(LocaleController.formatString("CallText", C0338R.string.CallText, Integer.valueOf(r0), Integer.valueOf(access$3300)));
                        } else if (LoginActivitySmsView.this.nextType == 2) {
                            LoginActivitySmsView.this.timeText.setText(LocaleController.formatString("SmsText", C0338R.string.SmsText, Integer.valueOf(r0), Integer.valueOf(access$3300)));
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
                            ConnectionsManager.getInstance().sendRequest(tL_auth_resendCode, new C11411(), 2);
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

            C11437() {
            }

            public void run() {
                if (LoginActivitySmsView.this.timeTimer != null) {
                    double currentTimeMillis = (double) System.currentTimeMillis();
                    LoginActivitySmsView.this.time = (int) (((double) LoginActivitySmsView.this.time) - (currentTimeMillis - LoginActivitySmsView.this.lastCurrentTime));
                    LoginActivitySmsView.this.lastCurrentTime = currentTimeMillis;
                    AndroidUtilities.runOnUIThread(new C11421());
                }
            }
        }

        /* renamed from: com.hanista.mobogram.ui.ChangePhoneActivity.LoginActivitySmsView.8 */
        class C11458 implements RequestDelegate {

            /* renamed from: com.hanista.mobogram.ui.ChangePhoneActivity.LoginActivitySmsView.8.1 */
            class C11441 implements Runnable {
                final /* synthetic */ TL_error val$error;
                final /* synthetic */ TLObject val$response;

                C11441(TL_error tL_error, TLObject tLObject) {
                    this.val$error = tL_error;
                    this.val$response = tLObject;
                }

                public void run() {
                    ChangePhoneActivity.this.needHideProgress();
                    LoginActivitySmsView.this.nextPressed = false;
                    if (this.val$error == null) {
                        User user = (User) this.val$response;
                        LoginActivitySmsView.this.destroyTimer();
                        LoginActivitySmsView.this.destroyCodeTimer();
                        UserConfig.setCurrentUser(user);
                        UserConfig.saveConfig(true);
                        ArrayList arrayList = new ArrayList();
                        arrayList.add(user);
                        MessagesStorage.getInstance().putUsersAndChats(arrayList, null, true, true);
                        MessagesController.getInstance().putUser(user, false);
                        ChangePhoneActivity.this.finishFragment();
                        NotificationCenter.getInstance().postNotificationName(NotificationCenter.mainUserInfoChanged, new Object[0]);
                        return;
                    }
                    LoginActivitySmsView.this.lastError = this.val$error.text;
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
                        ChangePhoneActivity.this.needShowAlert(LocaleController.getString("InvalidPhoneNumber", C0338R.string.InvalidPhoneNumber));
                    } else if (this.val$error.text.contains("PHONE_CODE_EMPTY") || this.val$error.text.contains("PHONE_CODE_INVALID")) {
                        ChangePhoneActivity.this.needShowAlert(LocaleController.getString("InvalidCode", C0338R.string.InvalidCode));
                    } else if (this.val$error.text.contains("PHONE_CODE_EXPIRED")) {
                        ChangePhoneActivity.this.needShowAlert(LocaleController.getString("CodeExpired", C0338R.string.CodeExpired));
                    } else if (this.val$error.text.startsWith("FLOOD_WAIT")) {
                        ChangePhoneActivity.this.needShowAlert(LocaleController.getString("FloodWait", C0338R.string.FloodWait));
                    } else {
                        ChangePhoneActivity.this.needShowAlert(this.val$error.text);
                    }
                }
            }

            C11458() {
            }

            public void run(TLObject tLObject, TL_error tL_error) {
                AndroidUtilities.runOnUIThread(new C11441(tL_error, tLObject));
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
            View frameLayout;
            View imageView;
            super(context);
            this.timerSync = new Object();
            this.time = 60000;
            this.codeTime = DefaultLoadControl.DEFAULT_LOW_WATERMARK_MS;
            this.lastError = TtmlNode.ANONYMOUS_REGION_ID;
            this.pattern = "*";
            this.currentType = i;
            setOrientation(ChangePhoneActivity.done_button);
            this.confirmTextView = new TextView(context);
            this.confirmTextView.setTextColor(Theme.ATTACH_SHEET_TEXT_COLOR);
            this.confirmTextView.setTextSize(ChangePhoneActivity.done_button, 14.0f);
            this.confirmTextView.setGravity(LocaleController.isRTL ? 5 : 3);
            this.confirmTextView.setLineSpacing((float) AndroidUtilities.dp(2.0f), DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            if (this.currentType == 3) {
                frameLayout = new FrameLayout(context);
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
            this.codeField.setHintTextColor(Theme.SHARE_SHEET_EDIT_PLACEHOLDER_TEXT_COLOR);
            this.codeField.setImeOptions(268435461);
            this.codeField.setTextSize(ChangePhoneActivity.done_button, 18.0f);
            this.codeField.setInputType(3);
            this.codeField.setMaxLines(ChangePhoneActivity.done_button);
            this.codeField.setPadding(0, 0, 0, 0);
            addView(this.codeField, LayoutHelper.createLinear(-1, 36, (int) ChangePhoneActivity.done_button, 0, 20, 0, 0));
            this.codeField.addTextChangedListener(new C11311(ChangePhoneActivity.this));
            this.codeField.setOnEditorActionListener(new C11322(ChangePhoneActivity.this));
            if (this.currentType == 3) {
                this.codeField.setEnabled(false);
                this.codeField.setInputType(0);
                this.codeField.setVisibility(8);
            }
            this.timeText = new TextView(context);
            this.timeText.setTextSize(ChangePhoneActivity.done_button, 14.0f);
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
            this.problemText.setTextSize(ChangePhoneActivity.done_button, 14.0f);
            this.problemText.setTextColor(Theme.DIALOGS_PRINTING_TEXT_COLOR);
            this.problemText.setLineSpacing((float) AndroidUtilities.dp(2.0f), DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            this.problemText.setPadding(0, AndroidUtilities.dp(2.0f), 0, AndroidUtilities.dp(12.0f));
            addView(this.problemText, LayoutHelper.createLinear(-2, -2, LocaleController.isRTL ? 5 : 3, 0, 20, 0, 0));
            this.problemText.setOnClickListener(new C11333(ChangePhoneActivity.this));
            frameLayout = new LinearLayout(context);
            frameLayout.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
            addView(frameLayout, LayoutHelper.createLinear(-1, -1, LocaleController.isRTL ? 5 : 3));
            imageView = new TextView(context);
            imageView.setGravity((LocaleController.isRTL ? 5 : 3) | ChangePhoneActivity.done_button);
            imageView.setTextColor(Theme.DIALOGS_PRINTING_TEXT_COLOR);
            if (ThemeUtil.m2490b()) {
                imageView.setTextColor(AdvanceTheme.f2491b);
            }
            imageView.setTextSize(ChangePhoneActivity.done_button, 14.0f);
            imageView.setLineSpacing((float) AndroidUtilities.dp(2.0f), DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            imageView.setPadding(0, AndroidUtilities.dp(24.0f), 0, 0);
            frameLayout.addView(imageView, LayoutHelper.createLinear(-2, -2, (LocaleController.isRTL ? 5 : 3) | 80, 0, 0, 0, 10));
            imageView.setText(LocaleController.getString("WrongNumber", C0338R.string.WrongNumber));
            imageView.setOnClickListener(new C11354(ChangePhoneActivity.this));
        }

        private void createCodeTimer() {
            if (this.codeTimer == null) {
                this.codeTime = DefaultLoadControl.DEFAULT_LOW_WATERMARK_MS;
                this.codeTimer = new Timer();
                this.lastCodeTime = (double) System.currentTimeMillis();
                this.codeTimer.schedule(new C11396(), 0, 1000);
            }
        }

        private void createTimer() {
            if (this.timeTimer == null) {
                this.timeTimer = new Timer();
                this.timeTimer.schedule(new C11437(), 0, 1000);
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
            ChangePhoneActivity.this.needShowProgress();
            TLObject tL_auth_resendCode = new TL_auth_resendCode();
            tL_auth_resendCode.phone_number = this.requestPhone;
            tL_auth_resendCode.phone_code_hash = this.phoneHash;
            ConnectionsManager.getInstance().sendRequest(tL_auth_resendCode, new C11375(bundle), 2);
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
                TLObject tL_account_changePhone = new TL_account_changePhone();
                tL_account_changePhone.phone_number = this.requestPhone;
                tL_account_changePhone.phone_code = this.codeField.getText().toString();
                tL_account_changePhone.phone_code_hash = this.phoneHash;
                destroyTimer();
                ChangePhoneActivity.this.needShowProgress();
                ConnectionsManager.getInstance().sendRequest(tL_account_changePhone, new C11458(), 2);
            }
        }

        public void onShow() {
            super.onShow();
            if (this.codeField != null) {
                this.codeField.requestFocus();
                this.codeField.setSelection(this.codeField.length());
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
                    InputFilter[] inputFilterArr = new InputFilter[ChangePhoneActivity.done_button];
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
                    if (this.currentType == ChangePhoneActivity.done_button) {
                        charSequence = AndroidUtilities.replaceTags(LocaleController.getString("SentAppCode", C0338R.string.SentAppCode));
                    } else if (this.currentType == 2) {
                        r5 = new Object[ChangePhoneActivity.done_button];
                        r5[0] = format;
                        charSequence = AndroidUtilities.replaceTags(LocaleController.formatString("SentSmsCode", C0338R.string.SentSmsCode, r5));
                    } else if (this.currentType == 3) {
                        r5 = new Object[ChangePhoneActivity.done_button];
                        r5[0] = format;
                        charSequence = AndroidUtilities.replaceTags(LocaleController.formatString("SentCallCode", C0338R.string.SentCallCode, r5));
                    } else if (this.currentType == 4) {
                        r5 = new Object[ChangePhoneActivity.done_button];
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
                    if (this.currentType == ChangePhoneActivity.done_button) {
                        this.problemText.setVisibility(0);
                        this.timeText.setVisibility(8);
                    } else if (this.currentType == 3 && (this.nextType == 4 || this.nextType == 2)) {
                        this.problemText.setVisibility(8);
                        this.timeText.setVisibility(0);
                        if (this.nextType == 4) {
                            this.timeText.setText(LocaleController.formatString("CallText", C0338R.string.CallText, Integer.valueOf(ChangePhoneActivity.done_button), Integer.valueOf(0)));
                        } else if (this.nextType == 2) {
                            this.timeText.setText(LocaleController.formatString("SmsText", C0338R.string.SmsText, Integer.valueOf(ChangePhoneActivity.done_button), Integer.valueOf(0)));
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

        /* renamed from: com.hanista.mobogram.ui.ChangePhoneActivity.PhoneView.1 */
        class C11481 implements OnClickListener {
            final /* synthetic */ ChangePhoneActivity val$this$0;

            /* renamed from: com.hanista.mobogram.ui.ChangePhoneActivity.PhoneView.1.1 */
            class C11471 implements CountrySelectActivityDelegate {

                /* renamed from: com.hanista.mobogram.ui.ChangePhoneActivity.PhoneView.1.1.1 */
                class C11461 implements Runnable {
                    C11461() {
                    }

                    public void run() {
                        AndroidUtilities.showKeyboard(PhoneView.this.phoneField);
                    }
                }

                C11471() {
                }

                public void didSelectCountry(String str) {
                    PhoneView.this.selectCountry(str);
                    AndroidUtilities.runOnUIThread(new C11461(), 300);
                    PhoneView.this.phoneField.requestFocus();
                    PhoneView.this.phoneField.setSelection(PhoneView.this.phoneField.length());
                }
            }

            C11481(ChangePhoneActivity changePhoneActivity) {
                this.val$this$0 = changePhoneActivity;
            }

            public void onClick(View view) {
                BaseFragment countrySelectActivity = new CountrySelectActivity();
                countrySelectActivity.setCountrySelectActivityDelegate(new C11471());
                ChangePhoneActivity.this.presentFragment(countrySelectActivity);
            }
        }

        /* renamed from: com.hanista.mobogram.ui.ChangePhoneActivity.PhoneView.2 */
        class C11492 implements TextWatcher {
            final /* synthetic */ ChangePhoneActivity val$this$0;

            C11492(ChangePhoneActivity changePhoneActivity) {
                this.val$this$0 = changePhoneActivity;
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
                        PhoneView.this.countryState = ChangePhoneActivity.done_button;
                    } else {
                        String str2;
                        Object obj;
                        boolean z;
                        CharSequence charSequence;
                        if (stripExceptNumbers.length() > 4) {
                            String substring;
                            boolean z2;
                            PhoneView.this.ignoreOnTextChange = true;
                            for (int i = 4; i >= ChangePhoneActivity.done_button; i--) {
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
                                str2 = substring.substring(ChangePhoneActivity.done_button, substring.length()) + PhoneView.this.phoneField.getText().toString();
                                EditText access$400 = PhoneView.this.codeField;
                                substring = substring.substring(0, ChangePhoneActivity.done_button);
                                access$400.setText(substring);
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
                                HintEditText access$200 = PhoneView.this.phoneField;
                                if (str2 != null) {
                                    str = str2.replace('X', '\u2013');
                                }
                                access$200.setHintText(str);
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

        /* renamed from: com.hanista.mobogram.ui.ChangePhoneActivity.PhoneView.3 */
        class C11503 implements OnEditorActionListener {
            final /* synthetic */ ChangePhoneActivity val$this$0;

            C11503(ChangePhoneActivity changePhoneActivity) {
                this.val$this$0 = changePhoneActivity;
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

        /* renamed from: com.hanista.mobogram.ui.ChangePhoneActivity.PhoneView.4 */
        class C11514 implements TextWatcher {
            private int actionPosition;
            private int characterAction;
            final /* synthetic */ ChangePhoneActivity val$this$0;

            C11514(ChangePhoneActivity changePhoneActivity) {
                this.val$this$0 = changePhoneActivity;
                this.characterAction = -1;
            }

            public void afterTextChanged(Editable editable) {
                if (!PhoneView.this.ignoreOnPhoneChange) {
                    int selectionStart = PhoneView.this.phoneField.getSelectionStart();
                    String str = "0123456789";
                    String obj = PhoneView.this.phoneField.getText().toString();
                    if (this.characterAction == 3) {
                        obj = obj.substring(0, this.actionPosition) + obj.substring(this.actionPosition + ChangePhoneActivity.done_button, obj.length());
                        selectionStart--;
                    }
                    CharSequence stringBuilder = new StringBuilder(obj.length());
                    for (int i = 0; i < obj.length(); i += ChangePhoneActivity.done_button) {
                        Object substring = obj.substring(i, i + ChangePhoneActivity.done_button);
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
                                    i2 += ChangePhoneActivity.done_button;
                                    if (!(selectionStart != i2 || this.characterAction == 2 || this.characterAction == 3)) {
                                        selectionStart += ChangePhoneActivity.done_button;
                                    }
                                }
                                i2 += ChangePhoneActivity.done_button;
                            } else {
                                stringBuilder.insert(i2, ' ');
                                if (!(selectionStart != i2 + ChangePhoneActivity.done_button || this.characterAction == 2 || this.characterAction == 3)) {
                                    selectionStart += ChangePhoneActivity.done_button;
                                }
                            }
                        }
                    }
                    PhoneView.this.phoneField.setText(stringBuilder);
                    if (selectionStart >= 0) {
                        HintEditText access$200 = PhoneView.this.phoneField;
                        if (selectionStart > PhoneView.this.phoneField.length()) {
                            selectionStart = PhoneView.this.phoneField.length();
                        }
                        access$200.setSelection(selectionStart);
                    }
                    PhoneView.this.phoneField.onTextChange();
                    PhoneView.this.ignoreOnPhoneChange = false;
                }
            }

            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                if (i2 == 0 && i3 == ChangePhoneActivity.done_button) {
                    this.characterAction = ChangePhoneActivity.done_button;
                } else if (i2 != ChangePhoneActivity.done_button || i3 != 0) {
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

        /* renamed from: com.hanista.mobogram.ui.ChangePhoneActivity.PhoneView.5 */
        class C11525 implements OnEditorActionListener {
            final /* synthetic */ ChangePhoneActivity val$this$0;

            C11525(ChangePhoneActivity changePhoneActivity) {
                this.val$this$0 = changePhoneActivity;
            }

            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i != 5) {
                    return false;
                }
                PhoneView.this.onNextPressed();
                return true;
            }
        }

        /* renamed from: com.hanista.mobogram.ui.ChangePhoneActivity.PhoneView.6 */
        class C11536 implements Comparator<String> {
            final /* synthetic */ ChangePhoneActivity val$this$0;

            C11536(ChangePhoneActivity changePhoneActivity) {
                this.val$this$0 = changePhoneActivity;
            }

            public int compare(String str, String str2) {
                return str.compareTo(str2);
            }
        }

        /* renamed from: com.hanista.mobogram.ui.ChangePhoneActivity.PhoneView.7 */
        class C11557 implements RequestDelegate {
            final /* synthetic */ Bundle val$params;

            /* renamed from: com.hanista.mobogram.ui.ChangePhoneActivity.PhoneView.7.1 */
            class C11541 implements Runnable {
                final /* synthetic */ TL_error val$error;
                final /* synthetic */ TLObject val$response;

                C11541(TL_error tL_error, TLObject tLObject) {
                    this.val$error = tL_error;
                    this.val$response = tLObject;
                }

                public void run() {
                    PhoneView.this.nextPressed = false;
                    if (this.val$error == null) {
                        ChangePhoneActivity.this.fillNextCodeParams(C11557.this.val$params, (TL_auth_sentCode) this.val$response);
                    } else if (this.val$error.text != null) {
                        if (this.val$error.text.contains("PHONE_NUMBER_INVALID")) {
                            ChangePhoneActivity.this.needShowAlert(LocaleController.getString("InvalidPhoneNumber", C0338R.string.InvalidPhoneNumber));
                        } else if (this.val$error.text.contains("PHONE_CODE_EMPTY") || this.val$error.text.contains("PHONE_CODE_INVALID")) {
                            ChangePhoneActivity.this.needShowAlert(LocaleController.getString("InvalidCode", C0338R.string.InvalidCode));
                        } else if (this.val$error.text.contains("PHONE_CODE_EXPIRED")) {
                            ChangePhoneActivity.this.needShowAlert(LocaleController.getString("CodeExpired", C0338R.string.CodeExpired));
                        } else if (this.val$error.text.startsWith("FLOOD_WAIT")) {
                            ChangePhoneActivity.this.needShowAlert(LocaleController.getString("FloodWait", C0338R.string.FloodWait));
                        } else if (this.val$error.text.startsWith("PHONE_NUMBER_OCCUPIED")) {
                            ChangePhoneActivity changePhoneActivity = ChangePhoneActivity.this;
                            Object[] objArr = new Object[ChangePhoneActivity.done_button];
                            objArr[0] = C11557.this.val$params.getString("phone");
                            changePhoneActivity.needShowAlert(LocaleController.formatString("ChangePhoneNumberOccupied", C0338R.string.ChangePhoneNumberOccupied, objArr));
                        } else {
                            ChangePhoneActivity.this.needShowAlert(LocaleController.getString("ErrorOccurred", C0338R.string.ErrorOccurred));
                        }
                    }
                    ChangePhoneActivity.this.needHideProgress();
                }
            }

            C11557(Bundle bundle) {
                this.val$params = bundle;
            }

            public void run(TLObject tLObject, TL_error tL_error) {
                AndroidUtilities.runOnUIThread(new C11541(tL_error, tLObject));
            }
        }

        public PhoneView(Context context) {
            Object toUpperCase;
            String str;
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
            setOrientation(ChangePhoneActivity.done_button);
            this.countryButton = new TextView(context);
            this.countryButton.setTypeface(FontUtil.m1176a().m1161d());
            this.countryButton.setTextSize(ChangePhoneActivity.done_button, 18.0f);
            this.countryButton.setPadding(AndroidUtilities.dp(12.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(12.0f), 0);
            this.countryButton.setTextColor(Theme.STICKERS_SHEET_TITLE_TEXT_COLOR);
            this.countryButton.setMaxLines(ChangePhoneActivity.done_button);
            this.countryButton.setSingleLine(true);
            this.countryButton.setEllipsize(TruncateAt.END);
            this.countryButton.setGravity((LocaleController.isRTL ? 5 : 3) | ChangePhoneActivity.done_button);
            this.countryButton.setBackgroundResource(C0338R.drawable.spinner_states);
            addView(this.countryButton, LayoutHelper.createLinear(-1, 36, 0.0f, 0.0f, 0.0f, 14.0f));
            this.countryButton.setOnClickListener(new C11481(ChangePhoneActivity.this));
            View view = new View(context);
            view.setPadding(AndroidUtilities.dp(12.0f), 0, AndroidUtilities.dp(12.0f), 0);
            view.setBackgroundColor(-2368549);
            addView(view, LayoutHelper.createLinear(-1, ChangePhoneActivity.done_button, 4.0f, -17.5f, 4.0f, 0.0f));
            view = new LinearLayout(context);
            view.setOrientation(0);
            addView(view, LayoutHelper.createLinear(-1, -2, 0.0f, 20.0f, 0.0f, 0.0f));
            View textView = new TextView(context);
            textView.setText("+");
            textView.setTextColor(Theme.STICKERS_SHEET_TITLE_TEXT_COLOR);
            textView.setTextSize(ChangePhoneActivity.done_button, 18.0f);
            view.addView(textView, LayoutHelper.createLinear(-2, -2));
            this.codeField = new EditText(context);
            this.codeField.setInputType(3);
            this.codeField.setTextColor(Theme.STICKERS_SHEET_TITLE_TEXT_COLOR);
            AndroidUtilities.clearCursorDrawable(this.codeField);
            this.codeField.setPadding(AndroidUtilities.dp(10.0f), 0, 0, 0);
            this.codeField.setTextSize(ChangePhoneActivity.done_button, 18.0f);
            this.codeField.setMaxLines(ChangePhoneActivity.done_button);
            this.codeField.setGravity(19);
            this.codeField.setImeOptions(268435461);
            InputFilter[] inputFilterArr = new InputFilter[ChangePhoneActivity.done_button];
            inputFilterArr[0] = new LengthFilter(5);
            this.codeField.setFilters(inputFilterArr);
            view.addView(this.codeField, LayoutHelper.createLinear(55, 36, -9.0f, 0.0f, 16.0f, 0.0f));
            this.codeField.addTextChangedListener(new C11492(ChangePhoneActivity.this));
            this.codeField.setOnEditorActionListener(new C11503(ChangePhoneActivity.this));
            this.phoneField = new HintEditText(context);
            this.phoneField.setInputType(3);
            this.phoneField.setTextColor(Theme.STICKERS_SHEET_TITLE_TEXT_COLOR);
            if (ThemeUtil.m2490b()) {
                this.phoneField.getBackground().setColorFilter(AdvanceTheme.f2491b, Mode.SRC_IN);
            }
            this.phoneField.setHintTextColor(Theme.SHARE_SHEET_EDIT_PLACEHOLDER_TEXT_COLOR);
            this.phoneField.setPadding(0, 0, 0, 0);
            AndroidUtilities.clearCursorDrawable(this.phoneField);
            this.phoneField.setTextSize(ChangePhoneActivity.done_button, 18.0f);
            this.phoneField.setMaxLines(ChangePhoneActivity.done_button);
            this.phoneField.setGravity(19);
            this.phoneField.setImeOptions(268435461);
            view.addView(this.phoneField, LayoutHelper.createFrame(-1, 36.0f));
            this.phoneField.addTextChangedListener(new C11514(ChangePhoneActivity.this));
            this.phoneField.setOnEditorActionListener(new C11525(ChangePhoneActivity.this));
            View textView2 = new TextView(context);
            textView2.setText(LocaleController.getString("ChangePhoneHelp", C0338R.string.ChangePhoneHelp));
            textView2.setTextColor(Theme.ATTACH_SHEET_TEXT_COLOR);
            textView2.setTextSize(ChangePhoneActivity.done_button, 14.0f);
            textView2.setGravity(LocaleController.isRTL ? 5 : 3);
            textView2.setLineSpacing((float) AndroidUtilities.dp(2.0f), DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            addView(textView2, LayoutHelper.createLinear(-2, -2, LocaleController.isRTL ? 5 : 3, 0, 28, 0, 10));
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
                    hashMap.put(split[ChangePhoneActivity.done_button], split[2]);
                }
                bufferedReader.close();
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
            Collections.sort(this.countriesArray, new C11536(ChangePhoneActivity.this));
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
                        this.countryState = ChangePhoneActivity.done_button;
                    }
                    if (this.codeField.length() == 0) {
                        AndroidUtilities.showKeyboard(this.phoneField);
                        this.phoneField.requestFocus();
                        this.phoneField.setSelection(this.phoneField.length());
                    }
                    AndroidUtilities.showKeyboard(this.codeField);
                    this.codeField.requestFocus();
                    return;
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
                this.countryState = ChangePhoneActivity.done_button;
            }
            if (this.codeField.length() == 0) {
                AndroidUtilities.showKeyboard(this.codeField);
                this.codeField.requestFocus();
                return;
            }
            AndroidUtilities.showKeyboard(this.phoneField);
            this.phoneField.requestFocus();
            this.phoneField.setSelection(this.phoneField.length());
        }

        public String getHeaderName() {
            return LocaleController.getString("ChangePhoneNewNumber", C0338R.string.ChangePhoneNewNumber);
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
            if (ChangePhoneActivity.this.getParentActivity() != null && !this.nextPressed) {
                boolean z;
                TelephonyManager telephonyManager = (TelephonyManager) ApplicationLoader.applicationContext.getSystemService("phone");
                boolean z2 = (telephonyManager.getSimState() == ChangePhoneActivity.done_button || telephonyManager.getPhoneType() == 0) ? false : true;
                if (VERSION.SDK_INT < 23 || !z2) {
                    z = true;
                } else {
                    z = ChangePhoneActivity.this.getParentActivity().checkSelfPermission("android.permission.READ_PHONE_STATE") == 0;
                    boolean z3 = ChangePhoneActivity.this.getParentActivity().checkSelfPermission("android.permission.RECEIVE_SMS") == 0;
                    if (ChangePhoneActivity.this.checkPermissions) {
                        ChangePhoneActivity.this.permissionsItems.clear();
                        if (!z) {
                            ChangePhoneActivity.this.permissionsItems.add("android.permission.READ_PHONE_STATE");
                        }
                        if (!z3) {
                            ChangePhoneActivity.this.permissionsItems.add("android.permission.RECEIVE_SMS");
                        }
                        if (!ChangePhoneActivity.this.permissionsItems.isEmpty()) {
                            SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
                            if (sharedPreferences.getBoolean("firstlogin", true) || ChangePhoneActivity.this.getParentActivity().shouldShowRequestPermissionRationale("android.permission.READ_PHONE_STATE") || ChangePhoneActivity.this.getParentActivity().shouldShowRequestPermissionRationale("android.permission.RECEIVE_SMS")) {
                                sharedPreferences.edit().putBoolean("firstlogin", false).commit();
                                Builder builder = new Builder(ChangePhoneActivity.this.getParentActivity());
                                builder.setTitle(LocaleController.getString("AppName", C0338R.string.AppName));
                                builder.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), null);
                                if (ChangePhoneActivity.this.permissionsItems.size() == 2) {
                                    builder.setMessage(LocaleController.getString("AllowReadCallAndSms", C0338R.string.AllowReadCallAndSms));
                                } else if (z3) {
                                    builder.setMessage(LocaleController.getString("AllowReadCall", C0338R.string.AllowReadCall));
                                } else {
                                    builder.setMessage(LocaleController.getString("AllowReadSms", C0338R.string.AllowReadSms));
                                }
                                ChangePhoneActivity.this.permissionsDialog = ChangePhoneActivity.this.showDialog(builder.create());
                                return;
                            }
                            ChangePhoneActivity.this.getParentActivity().requestPermissions((String[]) ChangePhoneActivity.this.permissionsItems.toArray(new String[ChangePhoneActivity.this.permissionsItems.size()]), 6);
                            return;
                        }
                    }
                }
                if (this.countryState == ChangePhoneActivity.done_button) {
                    ChangePhoneActivity.this.needShowAlert(LocaleController.getString("ChooseCountry", C0338R.string.ChooseCountry));
                } else if (this.countryState == 2 && !BuildVars.DEBUG_VERSION) {
                    ChangePhoneActivity.this.needShowAlert(LocaleController.getString("WrongCountry", C0338R.string.WrongCountry));
                } else if (this.codeField.length() == 0) {
                    ChangePhoneActivity.this.needShowAlert(LocaleController.getString("InvalidPhoneNumber", C0338R.string.InvalidPhoneNumber));
                } else {
                    TLObject tL_account_sendChangePhoneCode = new TL_account_sendChangePhoneCode();
                    String stripExceptNumbers = PhoneFormat.stripExceptNumbers(TtmlNode.ANONYMOUS_REGION_ID + this.codeField.getText() + this.phoneField.getText());
                    tL_account_sendChangePhoneCode.phone_number = stripExceptNumbers;
                    z2 = z2 && z;
                    tL_account_sendChangePhoneCode.allow_flashcall = z2;
                    if (tL_account_sendChangePhoneCode.allow_flashcall) {
                        try {
                            Object line1Number = telephonyManager.getLine1Number();
                            boolean z4 = (line1Number == null || line1Number.length() == 0 || (!stripExceptNumbers.contains(line1Number) && !line1Number.contains(stripExceptNumbers))) ? false : true;
                            tL_account_sendChangePhoneCode.current_number = z4;
                        } catch (Throwable e) {
                            tL_account_sendChangePhoneCode.allow_flashcall = false;
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
                    ChangePhoneActivity.this.needShowProgress();
                    ConnectionsManager.getInstance().sendRequest(tL_account_sendChangePhoneCode, new C11557(bundle), 2);
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

    public ChangePhoneActivity() {
        this.currentViewNum = 0;
        this.views = new SlideView[5];
        this.permissionsItems = new ArrayList();
        this.checkPermissions = true;
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

    public View createView(Context context) {
        this.actionBar.setTitle(LocaleController.getString("AppName", C0338R.string.AppName));
        this.actionBar.setBackButtonImage(C0338R.drawable.ic_ab_back);
        this.actionBar.setActionBarMenuOnItemClick(new C11291());
        ActionBarMenu createMenu = this.actionBar.createMenu();
        if (ThemeUtil.m2490b()) {
            Drawable drawable = getParentActivity().getResources().getDrawable(C0338R.drawable.ic_done);
            drawable.setColorFilter(AdvanceTheme.f2492c, Mode.SRC_IN);
            this.doneButton = createMenu.addItemWithWidth((int) done_button, drawable, AndroidUtilities.dp(56.0f));
        } else {
            this.doneButton = createMenu.addItemWithWidth((int) done_button, (int) C0338R.drawable.ic_done, AndroidUtilities.dp(56.0f));
        }
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
        int i = 0;
        while (i < this.views.length) {
            this.views[i].setVisibility(i == 0 ? 0 : 8);
            frameLayout.addView(this.views[i], LayoutHelper.createFrame(-1, i == 0 ? -2.0f : Face.UNCOMPUTED_PROBABILITY, 51, AndroidUtilities.isTablet() ? 26.0f : 18.0f, BitmapDescriptorFactory.HUE_ORANGE, AndroidUtilities.isTablet() ? 26.0f : 18.0f, 0.0f));
            i += done_button;
        }
        this.actionBar.setTitle(this.views[0].getHeaderName());
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

    public void needShowAlert(String str) {
        if (str != null && getParentActivity() != null) {
            Builder builder = new Builder(getParentActivity());
            builder.setTitle(LocaleController.getString("AppName", C0338R.string.AppName));
            builder.setMessage(str);
            builder.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), null);
            showDialog(builder.create());
        }
    }

    public void needShowProgress() {
        if (getParentActivity() != null && !getParentActivity().isFinishing() && this.progressDialog == null) {
            this.progressDialog = new ProgressDialog(getParentActivity());
            this.progressDialog.setMessage(LocaleController.getString("Loading", C0338R.string.Loading));
            this.progressDialog.setCanceledOnTouchOutside(false);
            this.progressDialog.setCancelable(false);
            this.progressDialog.show();
        }
    }

    public boolean onBackPressed() {
        int i = 0;
        if (this.currentViewNum == 0) {
            while (i < this.views.length) {
                if (this.views[i] != null) {
                    this.views[i].onDestroyActivity();
                }
                i += done_button;
            }
            return true;
        }
        this.views[this.currentViewNum].onBackPressed();
        setPage(0, true, null, true);
        return false;
    }

    protected void onDialogDismiss(Dialog dialog) {
        if (VERSION.SDK_INT >= 23 && dialog == this.permissionsDialog && !this.permissionsItems.isEmpty()) {
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
        initThemeActionBar();
    }

    public void onTransitionAnimationEnd(boolean z, boolean z2) {
        if (z) {
            this.views[this.currentViewNum].onShow();
        }
    }

    public void setPage(int i, boolean z, Bundle bundle, boolean z2) {
        if (i == 3) {
            this.doneButton.setVisibility(8);
        } else {
            if (i == 0) {
                this.checkPermissions = true;
            }
            this.doneButton.setVisibility(0);
        }
        SlideView slideView = this.views[this.currentViewNum];
        SlideView slideView2 = this.views[i];
        this.currentViewNum = i;
        slideView2.setParams(bundle);
        this.actionBar.setTitle(slideView2.getHeaderName());
        slideView2.onShow();
        slideView2.setX(z2 ? (float) (-AndroidUtilities.displaySize.x) : (float) AndroidUtilities.displaySize.x);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animatorSet.setDuration(300);
        Animator[] animatorArr = new Animator[2];
        String str = "translationX";
        float[] fArr = new float[done_button];
        fArr[0] = z2 ? (float) AndroidUtilities.displaySize.x : (float) (-AndroidUtilities.displaySize.x);
        animatorArr[0] = ObjectAnimator.ofFloat(slideView, str, fArr);
        float[] fArr2 = new float[done_button];
        fArr2[0] = 0.0f;
        animatorArr[done_button] = ObjectAnimator.ofFloat(slideView2, "translationX", fArr2);
        animatorSet.playTogether(animatorArr);
        animatorSet.addListener(new C11302(slideView2, slideView));
        animatorSet.start();
    }
}
