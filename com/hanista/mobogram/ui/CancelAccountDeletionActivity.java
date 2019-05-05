package com.hanista.mobogram.ui;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.view.PointerIconCompat;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputFilter.LengthFilter;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import com.hanista.mobogram.messenger.FileLog;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.NotificationCenter;
import com.hanista.mobogram.messenger.NotificationCenter.NotificationCenterDelegate;
import com.hanista.mobogram.messenger.exoplayer.DefaultLoadControl;
import com.hanista.mobogram.messenger.support.widget.helper.ItemTouchHelper.Callback;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;
import com.hanista.mobogram.tgnet.ConnectionsManager;
import com.hanista.mobogram.tgnet.RequestDelegate;
import com.hanista.mobogram.tgnet.TLObject;
import com.hanista.mobogram.tgnet.TLRPC.TL_account_confirmPhone;
import com.hanista.mobogram.tgnet.TLRPC.TL_account_sendConfirmPhoneCode;
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
import com.hanista.mobogram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import com.hanista.mobogram.ui.ActionBar.BaseFragment;
import com.hanista.mobogram.ui.ActionBar.Theme;
import com.hanista.mobogram.ui.Components.LayoutHelper;
import com.hanista.mobogram.ui.Components.SlideView;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class CancelAccountDeletionActivity extends BaseFragment {
    private static final int done_button = 1;
    private boolean checkPermissions;
    private int currentViewNum;
    private View doneButton;
    private Dialog errorDialog;
    private String hash;
    private Dialog permissionsDialog;
    private ArrayList<String> permissionsItems;
    private String phone;
    private ProgressDialog progressDialog;
    private SlideView[] views;

    /* renamed from: com.hanista.mobogram.ui.CancelAccountDeletionActivity.1 */
    class C10821 extends ActionBarMenuOnItemClick {
        C10821() {
        }

        public void onItemClick(int i) {
            if (i == CancelAccountDeletionActivity.done_button) {
                CancelAccountDeletionActivity.this.views[CancelAccountDeletionActivity.this.currentViewNum].onNextPressed();
            } else if (i == -1) {
                CancelAccountDeletionActivity.this.finishFragment();
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.CancelAccountDeletionActivity.2 */
    class C10832 extends AnimatorListenerAdapterProxy {
        final /* synthetic */ SlideView val$newView;
        final /* synthetic */ SlideView val$outView;

        C10832(SlideView slideView, SlideView slideView2) {
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
        private volatile int time;
        private TextView timeText;
        private Timer timeTimer;
        private int timeout;
        private final Object timerSync;
        private boolean waitingForEvent;

        /* renamed from: com.hanista.mobogram.ui.CancelAccountDeletionActivity.LoginActivitySmsView.1 */
        class C10841 implements TextWatcher {
            final /* synthetic */ CancelAccountDeletionActivity val$this$0;

            C10841(CancelAccountDeletionActivity cancelAccountDeletionActivity) {
                this.val$this$0 = cancelAccountDeletionActivity;
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

        /* renamed from: com.hanista.mobogram.ui.CancelAccountDeletionActivity.LoginActivitySmsView.2 */
        class C10852 implements OnEditorActionListener {
            final /* synthetic */ CancelAccountDeletionActivity val$this$0;

            C10852(CancelAccountDeletionActivity cancelAccountDeletionActivity) {
                this.val$this$0 = cancelAccountDeletionActivity;
            }

            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i != 5) {
                    return false;
                }
                LoginActivitySmsView.this.onNextPressed();
                return true;
            }
        }

        /* renamed from: com.hanista.mobogram.ui.CancelAccountDeletionActivity.LoginActivitySmsView.3 */
        class C10863 implements OnClickListener {
            final /* synthetic */ CancelAccountDeletionActivity val$this$0;

            C10863(CancelAccountDeletionActivity cancelAccountDeletionActivity) {
                this.val$this$0 = cancelAccountDeletionActivity;
            }

            public void onClick(View view) {
                if (!LoginActivitySmsView.this.nextPressed) {
                    if (LoginActivitySmsView.this.nextType == 0 || LoginActivitySmsView.this.nextType == 4) {
                        try {
                            PackageInfo packageInfo = ApplicationLoader.applicationContext.getPackageManager().getPackageInfo(ApplicationLoader.applicationContext.getPackageName(), 0);
                            String format = String.format(Locale.US, "%s (%d)", new Object[]{packageInfo.versionName, Integer.valueOf(packageInfo.versionCode)});
                            Intent intent = new Intent("android.intent.action.SEND");
                            intent.setType("message/rfc822");
                            String[] strArr = new String[CancelAccountDeletionActivity.done_button];
                            strArr[0] = "sms@stel.com";
                            intent.putExtra("android.intent.extra.EMAIL", strArr);
                            intent.putExtra("android.intent.extra.SUBJECT", "Android cancel account deletion issue " + format + " " + LoginActivitySmsView.this.phone);
                            intent.putExtra("android.intent.extra.TEXT", "Phone: " + LoginActivitySmsView.this.phone + "\nApp version: " + format + "\nOS version: SDK " + VERSION.SDK_INT + "\nDevice Name: " + Build.MANUFACTURER + Build.MODEL + "\nLocale: " + Locale.getDefault() + "\nError: " + LoginActivitySmsView.this.lastError);
                            LoginActivitySmsView.this.getContext().startActivity(Intent.createChooser(intent, "Send email..."));
                            return;
                        } catch (Exception e) {
                            CancelAccountDeletionActivity.this.needShowAlert(LocaleController.getString("NoMailInstalled", C0338R.string.NoMailInstalled));
                            return;
                        }
                    }
                    LoginActivitySmsView.this.resendCode();
                }
            }
        }

        /* renamed from: com.hanista.mobogram.ui.CancelAccountDeletionActivity.LoginActivitySmsView.4 */
        class C10884 implements RequestDelegate {
            final /* synthetic */ Bundle val$params;

            /* renamed from: com.hanista.mobogram.ui.CancelAccountDeletionActivity.LoginActivitySmsView.4.1 */
            class C10871 implements Runnable {
                final /* synthetic */ TL_error val$error;
                final /* synthetic */ TLObject val$response;

                C10871(TL_error tL_error, TLObject tLObject) {
                    this.val$error = tL_error;
                    this.val$response = tLObject;
                }

                public void run() {
                    LoginActivitySmsView.this.nextPressed = false;
                    if (this.val$error == null) {
                        CancelAccountDeletionActivity.this.fillNextCodeParams(C10884.this.val$params, (TL_auth_sentCode) this.val$response);
                    } else if (this.val$error.text != null) {
                        if (this.val$error.text.contains("PHONE_CODE_EXPIRED")) {
                            CancelAccountDeletionActivity.this.needShowAlert(LocaleController.getString("CodeExpired", C0338R.string.CodeExpired));
                        } else if (this.val$error.text.startsWith("FLOOD_WAIT")) {
                            CancelAccountDeletionActivity.this.needShowAlert(LocaleController.getString("FloodWait", C0338R.string.FloodWait));
                        } else if (this.val$error.code != NotificationManagerCompat.IMPORTANCE_UNSPECIFIED) {
                            CancelAccountDeletionActivity.this.needShowAlert(LocaleController.getString("ErrorOccurred", C0338R.string.ErrorOccurred) + "\n" + this.val$error.text);
                        }
                    }
                    CancelAccountDeletionActivity.this.needHideProgress();
                }
            }

            C10884(Bundle bundle) {
                this.val$params = bundle;
            }

            public void run(TLObject tLObject, TL_error tL_error) {
                AndroidUtilities.runOnUIThread(new C10871(tL_error, tLObject));
            }
        }

        /* renamed from: com.hanista.mobogram.ui.CancelAccountDeletionActivity.LoginActivitySmsView.5 */
        class C10905 extends TimerTask {

            /* renamed from: com.hanista.mobogram.ui.CancelAccountDeletionActivity.LoginActivitySmsView.5.1 */
            class C10891 implements Runnable {
                C10891() {
                }

                public void run() {
                    if (LoginActivitySmsView.this.codeTime <= PointerIconCompat.TYPE_DEFAULT) {
                        LoginActivitySmsView.this.problemText.setVisibility(0);
                        LoginActivitySmsView.this.destroyCodeTimer();
                    }
                }
            }

            C10905() {
            }

            public void run() {
                double currentTimeMillis = (double) System.currentTimeMillis();
                LoginActivitySmsView.this.codeTime = (int) (((double) LoginActivitySmsView.this.codeTime) - (currentTimeMillis - LoginActivitySmsView.this.lastCodeTime));
                LoginActivitySmsView.this.lastCodeTime = currentTimeMillis;
                AndroidUtilities.runOnUIThread(new C10891());
            }
        }

        /* renamed from: com.hanista.mobogram.ui.CancelAccountDeletionActivity.LoginActivitySmsView.6 */
        class C10946 extends TimerTask {

            /* renamed from: com.hanista.mobogram.ui.CancelAccountDeletionActivity.LoginActivitySmsView.6.1 */
            class C10931 implements Runnable {

                /* renamed from: com.hanista.mobogram.ui.CancelAccountDeletionActivity.LoginActivitySmsView.6.1.1 */
                class C10921 implements RequestDelegate {

                    /* renamed from: com.hanista.mobogram.ui.CancelAccountDeletionActivity.LoginActivitySmsView.6.1.1.1 */
                    class C10911 implements Runnable {
                        final /* synthetic */ TL_error val$error;

                        C10911(TL_error tL_error) {
                            this.val$error = tL_error;
                        }

                        public void run() {
                            LoginActivitySmsView.this.lastError = this.val$error.text;
                        }
                    }

                    C10921() {
                    }

                    public void run(TLObject tLObject, TL_error tL_error) {
                        if (tL_error != null && tL_error.text != null) {
                            AndroidUtilities.runOnUIThread(new C10911(tL_error));
                        }
                    }
                }

                C10931() {
                }

                public void run() {
                    if (LoginActivitySmsView.this.time >= PointerIconCompat.TYPE_DEFAULT) {
                        int access$2100 = (LoginActivitySmsView.this.time / PointerIconCompat.TYPE_DEFAULT) - (((LoginActivitySmsView.this.time / PointerIconCompat.TYPE_DEFAULT) / 60) * 60);
                        if (LoginActivitySmsView.this.nextType == 4 || LoginActivitySmsView.this.nextType == 3) {
                            LoginActivitySmsView.this.timeText.setText(LocaleController.formatString("CallText", C0338R.string.CallText, Integer.valueOf(r0), Integer.valueOf(access$2100)));
                        } else if (LoginActivitySmsView.this.nextType == 2) {
                            LoginActivitySmsView.this.timeText.setText(LocaleController.formatString("SmsText", C0338R.string.SmsText, Integer.valueOf(r0), Integer.valueOf(access$2100)));
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
                            tL_auth_resendCode.phone_number = LoginActivitySmsView.this.phone;
                            tL_auth_resendCode.phone_code_hash = LoginActivitySmsView.this.phoneHash;
                            ConnectionsManager.getInstance().sendRequest(tL_auth_resendCode, new C10921(), 2);
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

            C10946() {
            }

            public void run() {
                if (LoginActivitySmsView.this.timeTimer != null) {
                    double currentTimeMillis = (double) System.currentTimeMillis();
                    LoginActivitySmsView.this.time = (int) (((double) LoginActivitySmsView.this.time) - (currentTimeMillis - LoginActivitySmsView.this.lastCurrentTime));
                    LoginActivitySmsView.this.lastCurrentTime = currentTimeMillis;
                    AndroidUtilities.runOnUIThread(new C10931());
                }
            }
        }

        /* renamed from: com.hanista.mobogram.ui.CancelAccountDeletionActivity.LoginActivitySmsView.7 */
        class C10967 implements RequestDelegate {

            /* renamed from: com.hanista.mobogram.ui.CancelAccountDeletionActivity.LoginActivitySmsView.7.1 */
            class C10951 implements Runnable {
                final /* synthetic */ TL_error val$error;

                C10951(TL_error tL_error) {
                    this.val$error = tL_error;
                }

                public void run() {
                    CancelAccountDeletionActivity.this.needHideProgress();
                    LoginActivitySmsView.this.nextPressed = false;
                    if (this.val$error == null) {
                        CancelAccountDeletionActivity cancelAccountDeletionActivity = CancelAccountDeletionActivity.this;
                        CancelAccountDeletionActivity cancelAccountDeletionActivity2 = CancelAccountDeletionActivity.this;
                        Object[] objArr = new Object[CancelAccountDeletionActivity.done_button];
                        objArr[0] = PhoneFormat.getInstance().format("+" + LoginActivitySmsView.this.phone);
                        cancelAccountDeletionActivity.errorDialog = cancelAccountDeletionActivity2.needShowAlert(LocaleController.formatString("CancelLinkSuccess", C0338R.string.CancelLinkSuccess, objArr));
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
                    if (this.val$error.text.contains("PHONE_CODE_EMPTY") || this.val$error.text.contains("PHONE_CODE_INVALID")) {
                        CancelAccountDeletionActivity.this.needShowAlert(LocaleController.getString("InvalidCode", C0338R.string.InvalidCode));
                    } else if (this.val$error.text.contains("PHONE_CODE_EXPIRED")) {
                        CancelAccountDeletionActivity.this.needShowAlert(LocaleController.getString("CodeExpired", C0338R.string.CodeExpired));
                    } else if (this.val$error.text.startsWith("FLOOD_WAIT")) {
                        CancelAccountDeletionActivity.this.needShowAlert(LocaleController.getString("FloodWait", C0338R.string.FloodWait));
                    } else {
                        CancelAccountDeletionActivity.this.needShowAlert(this.val$error.text);
                    }
                }
            }

            C10967() {
            }

            public void run(TLObject tLObject, TL_error tL_error) {
                AndroidUtilities.runOnUIThread(new C10951(tL_error));
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
            super(context);
            this.timerSync = new Object();
            this.time = 60000;
            this.codeTime = DefaultLoadControl.DEFAULT_LOW_WATERMARK_MS;
            this.lastError = TtmlNode.ANONYMOUS_REGION_ID;
            this.pattern = "*";
            this.currentType = i;
            setOrientation(CancelAccountDeletionActivity.done_button);
            this.confirmTextView = new TextView(context);
            this.confirmTextView.setTextColor(Theme.ATTACH_SHEET_TEXT_COLOR);
            this.confirmTextView.setTextSize(CancelAccountDeletionActivity.done_button, 14.0f);
            this.confirmTextView.setGravity(LocaleController.isRTL ? 5 : 3);
            this.confirmTextView.setLineSpacing((float) AndroidUtilities.dp(2.0f), DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            if (this.currentType == 3) {
                View frameLayout = new FrameLayout(context);
                View imageView = new ImageView(context);
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
            this.codeField.setTextSize(CancelAccountDeletionActivity.done_button, 18.0f);
            this.codeField.setInputType(3);
            this.codeField.setMaxLines(CancelAccountDeletionActivity.done_button);
            this.codeField.setPadding(0, 0, 0, 0);
            addView(this.codeField, LayoutHelper.createLinear(-1, 36, (int) CancelAccountDeletionActivity.done_button, 0, 20, 0, 0));
            this.codeField.addTextChangedListener(new C10841(CancelAccountDeletionActivity.this));
            this.codeField.setOnEditorActionListener(new C10852(CancelAccountDeletionActivity.this));
            if (this.currentType == 3) {
                this.codeField.setEnabled(false);
                this.codeField.setInputType(0);
                this.codeField.setVisibility(8);
            }
            this.timeText = new TextView(context);
            this.timeText.setTextSize(CancelAccountDeletionActivity.done_button, 14.0f);
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
            this.problemText.setTextSize(CancelAccountDeletionActivity.done_button, 14.0f);
            this.problemText.setTextColor(Theme.DIALOGS_PRINTING_TEXT_COLOR);
            this.problemText.setLineSpacing((float) AndroidUtilities.dp(2.0f), DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            this.problemText.setPadding(0, AndroidUtilities.dp(2.0f), 0, AndroidUtilities.dp(12.0f));
            addView(this.problemText, LayoutHelper.createLinear(-2, -2, LocaleController.isRTL ? 5 : 3, 0, 20, 0, 0));
            this.problemText.setOnClickListener(new C10863(CancelAccountDeletionActivity.this));
        }

        private void createCodeTimer() {
            if (this.codeTimer == null) {
                this.codeTime = DefaultLoadControl.DEFAULT_LOW_WATERMARK_MS;
                this.codeTimer = new Timer();
                this.lastCodeTime = (double) System.currentTimeMillis();
                this.codeTimer.schedule(new C10905(), 0, 1000);
            }
        }

        private void createTimer() {
            if (this.timeTimer == null) {
                this.timeTimer = new Timer();
                this.timeTimer.schedule(new C10946(), 0, 1000);
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
            this.nextPressed = true;
            CancelAccountDeletionActivity.this.needShowProgress();
            TLObject tL_auth_resendCode = new TL_auth_resendCode();
            tL_auth_resendCode.phone_number = this.phone;
            tL_auth_resendCode.phone_code_hash = this.phoneHash;
            ConnectionsManager.getInstance().sendRequest(tL_auth_resendCode, new C10884(bundle), 2);
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
            return LocaleController.getString("CancelAccountReset", C0338R.string.CancelAccountReset);
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
                TLObject tL_account_confirmPhone = new TL_account_confirmPhone();
                tL_account_confirmPhone.phone_code = this.codeField.getText().toString();
                tL_account_confirmPhone.phone_code_hash = this.phoneHash;
                destroyTimer();
                CancelAccountDeletionActivity.this.needShowProgress();
                ConnectionsManager.getInstance().sendRequest(tL_account_confirmPhone, new C10967(), 2);
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
                this.phoneHash = bundle.getString("phoneHash");
                int i2 = bundle.getInt("timeout");
                this.time = i2;
                this.timeout = i2;
                this.openTime = (int) (System.currentTimeMillis() / 1000);
                this.nextType = bundle.getInt("nextType");
                this.pattern = bundle.getString("pattern");
                this.length = bundle.getInt("length");
                if (this.length != 0) {
                    InputFilter[] inputFilterArr = new InputFilter[CancelAccountDeletionActivity.done_button];
                    inputFilterArr[0] = new LengthFilter(this.length);
                    this.codeField.setFilters(inputFilterArr);
                } else {
                    this.codeField.setFilters(new InputFilter[0]);
                }
                if (this.progressView != null) {
                    this.progressView.setVisibility(this.nextType != 0 ? 0 : 8);
                }
                if (this.phone != null) {
                    Object[] objArr = new Object[CancelAccountDeletionActivity.done_button];
                    objArr[0] = PhoneFormat.getInstance().format("+" + PhoneFormat.getInstance().format(this.phone));
                    this.confirmTextView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("CancelAccountResetInfo", C0338R.string.CancelAccountResetInfo, objArr)));
                    if (this.currentType != 3) {
                        AndroidUtilities.showKeyboard(this.codeField);
                        this.codeField.requestFocus();
                    } else {
                        AndroidUtilities.hideKeyboard(this.codeField);
                    }
                    destroyTimer();
                    destroyCodeTimer();
                    this.lastCurrentTime = (double) System.currentTimeMillis();
                    if (this.currentType == CancelAccountDeletionActivity.done_button) {
                        this.problemText.setVisibility(0);
                        this.timeText.setVisibility(8);
                    } else if (this.currentType == 3 && (this.nextType == 4 || this.nextType == 2)) {
                        this.problemText.setVisibility(8);
                        this.timeText.setVisibility(0);
                        if (this.nextType == 4) {
                            this.timeText.setText(LocaleController.formatString("CallText", C0338R.string.CallText, Integer.valueOf(CancelAccountDeletionActivity.done_button), Integer.valueOf(0)));
                        } else if (this.nextType == 2) {
                            this.timeText.setText(LocaleController.formatString("SmsText", C0338R.string.SmsText, Integer.valueOf(CancelAccountDeletionActivity.done_button), Integer.valueOf(0)));
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

    public class PhoneView extends SlideView {
        private boolean nextPressed;

        /* renamed from: com.hanista.mobogram.ui.CancelAccountDeletionActivity.PhoneView.1 */
        class C10981 implements RequestDelegate {
            final /* synthetic */ Bundle val$params;

            /* renamed from: com.hanista.mobogram.ui.CancelAccountDeletionActivity.PhoneView.1.1 */
            class C10971 implements Runnable {
                final /* synthetic */ TL_error val$error;
                final /* synthetic */ TLObject val$response;

                C10971(TL_error tL_error, TLObject tLObject) {
                    this.val$error = tL_error;
                    this.val$response = tLObject;
                }

                public void run() {
                    PhoneView.this.nextPressed = false;
                    if (this.val$error == null) {
                        CancelAccountDeletionActivity.this.fillNextCodeParams(C10981.this.val$params, (TL_auth_sentCode) this.val$response);
                    } else if (this.val$error.code == 400) {
                        CancelAccountDeletionActivity.this.errorDialog = CancelAccountDeletionActivity.this.needShowAlert(LocaleController.getString("CancelLinkExpired", C0338R.string.CancelLinkExpired));
                    } else if (this.val$error.text == null) {
                    } else {
                        if (this.val$error.text.startsWith("FLOOD_WAIT")) {
                            CancelAccountDeletionActivity.this.errorDialog = CancelAccountDeletionActivity.this.needShowAlert(LocaleController.getString("FloodWait", C0338R.string.FloodWait));
                        } else {
                            CancelAccountDeletionActivity.this.errorDialog = CancelAccountDeletionActivity.this.needShowAlert(LocaleController.getString("ErrorOccurred", C0338R.string.ErrorOccurred));
                        }
                    }
                }
            }

            C10981(Bundle bundle) {
                this.val$params = bundle;
            }

            public void run(TLObject tLObject, TL_error tL_error) {
                AndroidUtilities.runOnUIThread(new C10971(tL_error, tLObject));
            }
        }

        public PhoneView(Context context) {
            super(context);
            this.nextPressed = false;
            setOrientation(CancelAccountDeletionActivity.done_button);
            View frameLayout = new FrameLayout(context);
            addView(frameLayout, LayoutHelper.createLinear(-1, Callback.DEFAULT_DRAG_ANIMATION_DURATION));
            frameLayout.addView(new ProgressBar(context), LayoutHelper.createFrame(-2, -2, 17));
        }

        public String getHeaderName() {
            return LocaleController.getString("CancelAccountReset", C0338R.string.CancelAccountReset);
        }

        public void onNextPressed() {
            TLObject tL_account_sendConfirmPhoneCode;
            if (CancelAccountDeletionActivity.this.getParentActivity() != null && !this.nextPressed) {
                TelephonyManager telephonyManager = (TelephonyManager) ApplicationLoader.applicationContext.getSystemService("phone");
                boolean z = (telephonyManager.getSimState() == CancelAccountDeletionActivity.done_button || telephonyManager.getPhoneType() == 0) ? false : true;
                if (VERSION.SDK_INT < 23 || z) {
                    tL_account_sendConfirmPhoneCode = new TL_account_sendConfirmPhoneCode();
                    tL_account_sendConfirmPhoneCode.allow_flashcall = false;
                    tL_account_sendConfirmPhoneCode.hash = CancelAccountDeletionActivity.this.hash;
                } else {
                    tL_account_sendConfirmPhoneCode = new TL_account_sendConfirmPhoneCode();
                    tL_account_sendConfirmPhoneCode.allow_flashcall = false;
                    tL_account_sendConfirmPhoneCode.hash = CancelAccountDeletionActivity.this.hash;
                }
                if (tL_account_sendConfirmPhoneCode.allow_flashcall) {
                    try {
                        Object line1Number = telephonyManager.getLine1Number();
                        boolean z2 = (line1Number == null || line1Number.length() == 0 || (!CancelAccountDeletionActivity.this.phone.contains(line1Number) && !line1Number.contains(CancelAccountDeletionActivity.this.phone))) ? false : true;
                        tL_account_sendConfirmPhoneCode.current_number = z2;
                    } catch (Throwable e) {
                        tL_account_sendConfirmPhoneCode.allow_flashcall = false;
                        FileLog.m18e("tmessages", e);
                    }
                }
                Bundle bundle = new Bundle();
                bundle.putString("phone", CancelAccountDeletionActivity.this.phone);
                this.nextPressed = true;
                ConnectionsManager.getInstance().sendRequest(tL_account_sendConfirmPhoneCode, new C10981(bundle), 2);
            }
        }

        public void onShow() {
            super.onShow();
            onNextPressed();
        }
    }

    public CancelAccountDeletionActivity(Bundle bundle) {
        super(bundle);
        this.currentViewNum = 0;
        this.views = new SlideView[5];
        this.permissionsItems = new ArrayList();
        this.checkPermissions = false;
        this.hash = bundle.getString("hash");
        this.phone = bundle.getString("phone");
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
        this.actionBar.setActionBarMenuOnItemClick(new C10821());
        this.doneButton = this.actionBar.createMenu().addItemWithWidth((int) done_button, (int) C0338R.drawable.ic_done, AndroidUtilities.dp(56.0f));
        this.doneButton.setVisibility(8);
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

    public Dialog needShowAlert(String str) {
        if (str == null || getParentActivity() == null) {
            return null;
        }
        Builder builder = new Builder(getParentActivity());
        builder.setTitle(LocaleController.getString("AppName", C0338R.string.AppName));
        builder.setMessage(str);
        builder.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), null);
        Dialog create = builder.create();
        showDialog(create);
        return create;
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
        for (int i = 0; i < this.views.length; i += done_button) {
            if (this.views[i] != null) {
                this.views[i].onDestroyActivity();
            }
        }
        return true;
    }

    protected void onDialogDismiss(Dialog dialog) {
        if (VERSION.SDK_INT >= 23 && dialog == this.permissionsDialog && !this.permissionsItems.isEmpty()) {
            getParentActivity().requestPermissions((String[]) this.permissionsItems.toArray(new String[this.permissionsItems.size()]), 6);
        }
        if (dialog == this.errorDialog) {
            finishFragment();
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
    }

    public void onTransitionAnimationEnd(boolean z, boolean z2) {
        if (z) {
            this.views[this.currentViewNum].onShow();
        }
    }

    public void setPage(int i, boolean z, Bundle bundle, boolean z2) {
        if (i != 3 && i != 0) {
            this.doneButton.setVisibility(0);
        } else if (i == 0) {
            this.doneButton.setVisibility(8);
        } else {
            this.doneButton.setVisibility(8);
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
        animatorSet.addListener(new C10832(slideView2, slideView));
        animatorSet.start();
    }
}
