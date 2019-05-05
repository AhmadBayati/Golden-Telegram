package com.hanista.mobogram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.os.Vibrator;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat.AuthenticationCallback;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat.AuthenticationResult;
import android.support.v4.os.CancellationSignal;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.ActionMode;
import android.view.ActionMode.Callback;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.AnimatorListenerAdapterProxy;
import com.hanista.mobogram.messenger.ApplicationLoader;
import com.hanista.mobogram.messenger.FileLog;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.NotificationCenter;
import com.hanista.mobogram.messenger.UserConfig;
import com.hanista.mobogram.messenger.exoplayer.upstream.NetworkLock;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;
import com.hanista.mobogram.messenger.volley.Request.Method;
import com.hanista.mobogram.mobo.lock.PatternFrame;
import com.hanista.mobogram.mobo.p008i.FontUtil;
import com.hanista.mobogram.mobo.p020s.AdvanceTheme;
import com.hanista.mobogram.mobo.p020s.ThemeUtil;
import com.hanista.mobogram.tgnet.ConnectionsManager;
import com.hanista.mobogram.tgnet.TLRPC;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;

public class PasscodeView extends FrameLayout {
    private static final int id_fingerprint_imageview = 1001;
    private static final int id_fingerprint_textview = 1000;
    private Drawable backgroundDrawable;
    private FrameLayout backgroundFrameLayout;
    private CancellationSignal cancellationSignal;
    private ImageView checkImage;
    private PasscodeViewDelegate delegate;
    private ImageView eraseView;
    private AlertDialog fingerprintDialog;
    private ImageView fingerprintImageView;
    private TextView fingerprintStatusTextView;
    private ImageView imageView;
    private int keyboardHeight;
    private ArrayList<TextView> lettersTextViews;
    private ArrayList<FrameLayout> numberFrameLayouts;
    private ArrayList<TextView> numberTextViews;
    private FrameLayout numbersFrameLayout;
    private TextView passcodeTextView;
    private EditText passwordEditText;
    private AnimatingTextView passwordEditText2;
    private FrameLayout passwordFrameLayout;
    private String pattern;
    private PatternFrame patternFrameLayout;
    private Rect rect;
    private boolean selfCancelled;

    /* renamed from: com.hanista.mobogram.ui.Components.PasscodeView.1 */
    class C13881 implements OnEditorActionListener {
        C13881() {
        }

        public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
            if (i != 6) {
                return false;
            }
            PasscodeView.this.processDone(false);
            return true;
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Components.PasscodeView.2 */
    class C13892 implements TextWatcher {
        C13892() {
        }

        public void afterTextChanged(Editable editable) {
            if (PasscodeView.this.passwordEditText.length() == 4 && UserConfig.passcodeType == 0) {
                PasscodeView.this.processDone(false);
            }
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Components.PasscodeView.3 */
    class C13903 implements Callback {
        C13903() {
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

    /* renamed from: com.hanista.mobogram.ui.Components.PasscodeView.4 */
    class C13914 implements OnClickListener {
        C13914() {
        }

        public void onClick(View view) {
            PasscodeView.this.processDone(false);
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Components.PasscodeView.5 */
    class C13925 implements PatternFrame.PatternFrame {
        C13925() {
        }

        public void didAcceptedPattern(String str) {
            PasscodeView.this.pattern = str;
            PasscodeView.this.processDone(false);
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Components.PasscodeView.6 */
    class C13936 implements OnLongClickListener {
        C13936() {
        }

        public boolean onLongClick(View view) {
            PasscodeView.this.passwordEditText.setText(TtmlNode.ANONYMOUS_REGION_ID);
            PasscodeView.this.passwordEditText2.eraseAllCharacters(true);
            return true;
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Components.PasscodeView.7 */
    class C13947 implements OnClickListener {
        C13947() {
        }

        public void onClick(View view) {
            switch (((Integer) view.getTag()).intValue()) {
                case VideoPlayer.TRACK_DEFAULT /*0*/:
                    PasscodeView.this.passwordEditText2.appendCharacter("0");
                    break;
                case VideoPlayer.TYPE_AUDIO /*1*/:
                    PasscodeView.this.passwordEditText2.appendCharacter("1");
                    break;
                case VideoPlayer.STATE_PREPARING /*2*/:
                    PasscodeView.this.passwordEditText2.appendCharacter("2");
                    break;
                case VideoPlayer.STATE_BUFFERING /*3*/:
                    PasscodeView.this.passwordEditText2.appendCharacter("3");
                    break;
                case VideoPlayer.STATE_READY /*4*/:
                    PasscodeView.this.passwordEditText2.appendCharacter("4");
                    break;
                case VideoPlayer.STATE_ENDED /*5*/:
                    PasscodeView.this.passwordEditText2.appendCharacter("5");
                    break;
                case Method.TRACE /*6*/:
                    PasscodeView.this.passwordEditText2.appendCharacter("6");
                    break;
                case Method.PATCH /*7*/:
                    PasscodeView.this.passwordEditText2.appendCharacter("7");
                    break;
                case TLRPC.USER_FLAG_USERNAME /*8*/:
                    PasscodeView.this.passwordEditText2.appendCharacter("8");
                    break;
                case C0338R.styleable.PromptView_iconTint /*9*/:
                    PasscodeView.this.passwordEditText2.appendCharacter("9");
                    break;
                case NetworkLock.DOWNLOAD_PRIORITY /*10*/:
                    PasscodeView.this.passwordEditText2.eraseLastCharacter();
                    break;
            }
            if (PasscodeView.this.passwordEditText2.lenght() == 4) {
                PasscodeView.this.processDone(false);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Components.PasscodeView.8 */
    class C13958 extends AnimatorListenerAdapterProxy {
        C13958() {
        }

        public void onAnimationEnd(Animator animator) {
            PasscodeView.this.setVisibility(8);
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Components.PasscodeView.9 */
    class C13969 extends AnimatorListenerAdapterProxy {
        final /* synthetic */ int val$num;
        final /* synthetic */ float val$x;

        C13969(int i, float f) {
            this.val$num = i;
            this.val$x = f;
        }

        public void onAnimationEnd(Animator animator) {
            PasscodeView.this.shakeTextView(this.val$num == 5 ? 0.0f : -this.val$x, this.val$num + 1);
        }
    }

    private class AnimatingTextView extends FrameLayout {
        private String DOT;
        private ArrayList<TextView> characterTextViews;
        private AnimatorSet currentAnimation;
        private Runnable dotRunnable;
        private ArrayList<TextView> dotTextViews;
        private StringBuilder stringBuilder;

        /* renamed from: com.hanista.mobogram.ui.Components.PasscodeView.AnimatingTextView.1 */
        class C13981 implements Runnable {
            final /* synthetic */ int val$newPos;

            /* renamed from: com.hanista.mobogram.ui.Components.PasscodeView.AnimatingTextView.1.1 */
            class C13971 extends AnimatorListenerAdapterProxy {
                C13971() {
                }

                public void onAnimationEnd(Animator animator) {
                    if (AnimatingTextView.this.currentAnimation != null && AnimatingTextView.this.currentAnimation.equals(animator)) {
                        AnimatingTextView.this.currentAnimation = null;
                    }
                }
            }

            C13981(int i) {
                this.val$newPos = i;
            }

            public void run() {
                if (AnimatingTextView.this.dotRunnable == this) {
                    Collection arrayList = new ArrayList();
                    TextView textView = (TextView) AnimatingTextView.this.characterTextViews.get(this.val$newPos);
                    arrayList.add(ObjectAnimator.ofFloat(textView, "scaleX", new float[]{0.0f}));
                    arrayList.add(ObjectAnimator.ofFloat(textView, "scaleY", new float[]{0.0f}));
                    arrayList.add(ObjectAnimator.ofFloat(textView, "alpha", new float[]{0.0f}));
                    textView = (TextView) AnimatingTextView.this.dotTextViews.get(this.val$newPos);
                    arrayList.add(ObjectAnimator.ofFloat(textView, "scaleX", new float[]{DefaultRetryPolicy.DEFAULT_BACKOFF_MULT}));
                    arrayList.add(ObjectAnimator.ofFloat(textView, "scaleY", new float[]{DefaultRetryPolicy.DEFAULT_BACKOFF_MULT}));
                    arrayList.add(ObjectAnimator.ofFloat(textView, "alpha", new float[]{DefaultRetryPolicy.DEFAULT_BACKOFF_MULT}));
                    AnimatingTextView.this.currentAnimation = new AnimatorSet();
                    AnimatingTextView.this.currentAnimation.setDuration(150);
                    AnimatingTextView.this.currentAnimation.playTogether(arrayList);
                    AnimatingTextView.this.currentAnimation.addListener(new C13971());
                    AnimatingTextView.this.currentAnimation.start();
                }
            }
        }

        /* renamed from: com.hanista.mobogram.ui.Components.PasscodeView.AnimatingTextView.2 */
        class C13992 extends AnimatorListenerAdapterProxy {
            C13992() {
            }

            public void onAnimationEnd(Animator animator) {
                if (AnimatingTextView.this.currentAnimation != null && AnimatingTextView.this.currentAnimation.equals(animator)) {
                    AnimatingTextView.this.currentAnimation = null;
                }
            }
        }

        /* renamed from: com.hanista.mobogram.ui.Components.PasscodeView.AnimatingTextView.3 */
        class C14003 extends AnimatorListenerAdapterProxy {
            C14003() {
            }

            public void onAnimationEnd(Animator animator) {
                if (AnimatingTextView.this.currentAnimation != null && AnimatingTextView.this.currentAnimation.equals(animator)) {
                    AnimatingTextView.this.currentAnimation = null;
                }
            }
        }

        /* renamed from: com.hanista.mobogram.ui.Components.PasscodeView.AnimatingTextView.4 */
        class C14014 extends AnimatorListenerAdapterProxy {
            C14014() {
            }

            public void onAnimationEnd(Animator animator) {
                if (AnimatingTextView.this.currentAnimation != null && AnimatingTextView.this.currentAnimation.equals(animator)) {
                    AnimatingTextView.this.currentAnimation = null;
                }
            }
        }

        public AnimatingTextView(Context context) {
            super(context);
            this.DOT = "\u2022";
            this.characterTextViews = new ArrayList(4);
            this.dotTextViews = new ArrayList(4);
            this.stringBuilder = new StringBuilder(4);
            for (int i = 0; i < 4; i++) {
                View textView = new TextView(context);
                textView.setTypeface(FontUtil.m1176a().m1161d());
                textView.setTextColor(-1);
                textView.setTextSize(1, 36.0f);
                textView.setGravity(17);
                textView.setAlpha(0.0f);
                textView.setPivotX((float) AndroidUtilities.dp(25.0f));
                textView.setPivotY((float) AndroidUtilities.dp(25.0f));
                addView(textView);
                LayoutParams layoutParams = (LayoutParams) textView.getLayoutParams();
                layoutParams.width = AndroidUtilities.dp(50.0f);
                layoutParams.height = AndroidUtilities.dp(50.0f);
                layoutParams.gravity = 51;
                textView.setLayoutParams(layoutParams);
                this.characterTextViews.add(textView);
                textView = new TextView(context);
                textView.setTextColor(-1);
                textView.setTextSize(1, 36.0f);
                textView.setGravity(17);
                textView.setAlpha(0.0f);
                textView.setText(this.DOT);
                textView.setPivotX((float) AndroidUtilities.dp(25.0f));
                textView.setPivotY((float) AndroidUtilities.dp(25.0f));
                addView(textView);
                layoutParams = (LayoutParams) textView.getLayoutParams();
                layoutParams.width = AndroidUtilities.dp(50.0f);
                layoutParams.height = AndroidUtilities.dp(50.0f);
                layoutParams.gravity = 51;
                textView.setLayoutParams(layoutParams);
                this.dotTextViews.add(textView);
            }
        }

        private void eraseAllCharacters(boolean z) {
            int i = 0;
            if (this.stringBuilder.length() != 0) {
                if (this.dotRunnable != null) {
                    AndroidUtilities.cancelRunOnUIThread(this.dotRunnable);
                    this.dotRunnable = null;
                }
                if (this.currentAnimation != null) {
                    this.currentAnimation.cancel();
                    this.currentAnimation = null;
                }
                this.stringBuilder.delete(0, this.stringBuilder.length());
                if (z) {
                    Collection arrayList = new ArrayList();
                    for (int i2 = 0; i2 < 4; i2++) {
                        TextView textView = (TextView) this.characterTextViews.get(i2);
                        if (textView.getAlpha() != 0.0f) {
                            arrayList.add(ObjectAnimator.ofFloat(textView, "scaleX", new float[]{0.0f}));
                            arrayList.add(ObjectAnimator.ofFloat(textView, "scaleY", new float[]{0.0f}));
                            arrayList.add(ObjectAnimator.ofFloat(textView, "alpha", new float[]{0.0f}));
                        }
                        textView = (TextView) this.dotTextViews.get(i2);
                        if (textView.getAlpha() != 0.0f) {
                            arrayList.add(ObjectAnimator.ofFloat(textView, "scaleX", new float[]{0.0f}));
                            arrayList.add(ObjectAnimator.ofFloat(textView, "scaleY", new float[]{0.0f}));
                            arrayList.add(ObjectAnimator.ofFloat(textView, "alpha", new float[]{0.0f}));
                        }
                    }
                    this.currentAnimation = new AnimatorSet();
                    this.currentAnimation.setDuration(150);
                    this.currentAnimation.playTogether(arrayList);
                    this.currentAnimation.addListener(new C14014());
                    this.currentAnimation.start();
                    return;
                }
                while (i < 4) {
                    ((TextView) this.characterTextViews.get(i)).setAlpha(0.0f);
                    ((TextView) this.dotTextViews.get(i)).setAlpha(0.0f);
                    i++;
                }
            }
        }

        private int getXForTextView(int i) {
            return (((getMeasuredWidth() - (this.stringBuilder.length() * AndroidUtilities.dp(BitmapDescriptorFactory.HUE_ORANGE))) / 2) + (AndroidUtilities.dp(BitmapDescriptorFactory.HUE_ORANGE) * i)) - AndroidUtilities.dp(10.0f);
        }

        public void appendCharacter(String str) {
            if (this.stringBuilder.length() != 4) {
                int i;
                try {
                    performHapticFeedback(3);
                } catch (Throwable e) {
                    FileLog.m18e("tmessages", e);
                }
                Collection arrayList = new ArrayList();
                int length = this.stringBuilder.length();
                this.stringBuilder.append(str);
                TextView textView = (TextView) this.characterTextViews.get(length);
                textView.setText(str);
                textView.setTranslationX((float) getXForTextView(length));
                arrayList.add(ObjectAnimator.ofFloat(textView, "scaleX", new float[]{0.0f, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT}));
                arrayList.add(ObjectAnimator.ofFloat(textView, "scaleY", new float[]{0.0f, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT}));
                arrayList.add(ObjectAnimator.ofFloat(textView, "alpha", new float[]{0.0f, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT}));
                arrayList.add(ObjectAnimator.ofFloat(textView, "translationY", new float[]{(float) AndroidUtilities.dp(20.0f), 0.0f}));
                textView = (TextView) this.dotTextViews.get(length);
                textView.setTranslationX((float) getXForTextView(length));
                textView.setAlpha(0.0f);
                arrayList.add(ObjectAnimator.ofFloat(textView, "scaleX", new float[]{0.0f, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT}));
                arrayList.add(ObjectAnimator.ofFloat(textView, "scaleY", new float[]{0.0f, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT}));
                arrayList.add(ObjectAnimator.ofFloat(textView, "translationY", new float[]{(float) AndroidUtilities.dp(20.0f), 0.0f}));
                for (i = length + 1; i < 4; i++) {
                    textView = (TextView) this.characterTextViews.get(i);
                    if (textView.getAlpha() != 0.0f) {
                        arrayList.add(ObjectAnimator.ofFloat(textView, "scaleX", new float[]{0.0f}));
                        arrayList.add(ObjectAnimator.ofFloat(textView, "scaleY", new float[]{0.0f}));
                        arrayList.add(ObjectAnimator.ofFloat(textView, "alpha", new float[]{0.0f}));
                    }
                    textView = (TextView) this.dotTextViews.get(i);
                    if (textView.getAlpha() != 0.0f) {
                        arrayList.add(ObjectAnimator.ofFloat(textView, "scaleX", new float[]{0.0f}));
                        arrayList.add(ObjectAnimator.ofFloat(textView, "scaleY", new float[]{0.0f}));
                        arrayList.add(ObjectAnimator.ofFloat(textView, "alpha", new float[]{0.0f}));
                    }
                }
                if (this.dotRunnable != null) {
                    AndroidUtilities.cancelRunOnUIThread(this.dotRunnable);
                }
                this.dotRunnable = new C13981(length);
                AndroidUtilities.runOnUIThread(this.dotRunnable, 1500);
                for (i = 0; i < length; i++) {
                    textView = (TextView) this.characterTextViews.get(i);
                    arrayList.add(ObjectAnimator.ofFloat(textView, "translationX", new float[]{(float) getXForTextView(i)}));
                    arrayList.add(ObjectAnimator.ofFloat(textView, "scaleX", new float[]{0.0f}));
                    arrayList.add(ObjectAnimator.ofFloat(textView, "scaleY", new float[]{0.0f}));
                    arrayList.add(ObjectAnimator.ofFloat(textView, "alpha", new float[]{0.0f}));
                    arrayList.add(ObjectAnimator.ofFloat(textView, "translationY", new float[]{0.0f}));
                    textView = (TextView) this.dotTextViews.get(i);
                    arrayList.add(ObjectAnimator.ofFloat(textView, "translationX", new float[]{(float) getXForTextView(i)}));
                    arrayList.add(ObjectAnimator.ofFloat(textView, "scaleX", new float[]{DefaultRetryPolicy.DEFAULT_BACKOFF_MULT}));
                    arrayList.add(ObjectAnimator.ofFloat(textView, "scaleY", new float[]{DefaultRetryPolicy.DEFAULT_BACKOFF_MULT}));
                    arrayList.add(ObjectAnimator.ofFloat(textView, "alpha", new float[]{DefaultRetryPolicy.DEFAULT_BACKOFF_MULT}));
                    arrayList.add(ObjectAnimator.ofFloat(textView, "translationY", new float[]{0.0f}));
                }
                if (this.currentAnimation != null) {
                    this.currentAnimation.cancel();
                }
                this.currentAnimation = new AnimatorSet();
                this.currentAnimation.setDuration(150);
                this.currentAnimation.playTogether(arrayList);
                this.currentAnimation.addListener(new C13992());
                this.currentAnimation.start();
            }
        }

        public void eraseLastCharacter() {
            if (this.stringBuilder.length() != 0) {
                int i;
                try {
                    performHapticFeedback(3);
                } catch (Throwable e) {
                    FileLog.m18e("tmessages", e);
                }
                Collection arrayList = new ArrayList();
                int length = this.stringBuilder.length() - 1;
                if (length != 0) {
                    this.stringBuilder.deleteCharAt(length);
                }
                for (i = length; i < 4; i++) {
                    TextView textView = (TextView) this.characterTextViews.get(i);
                    if (textView.getAlpha() != 0.0f) {
                        arrayList.add(ObjectAnimator.ofFloat(textView, "scaleX", new float[]{0.0f}));
                        arrayList.add(ObjectAnimator.ofFloat(textView, "scaleY", new float[]{0.0f}));
                        arrayList.add(ObjectAnimator.ofFloat(textView, "alpha", new float[]{0.0f}));
                        arrayList.add(ObjectAnimator.ofFloat(textView, "translationY", new float[]{0.0f}));
                        arrayList.add(ObjectAnimator.ofFloat(textView, "translationX", new float[]{(float) getXForTextView(i)}));
                    }
                    textView = (TextView) this.dotTextViews.get(i);
                    if (textView.getAlpha() != 0.0f) {
                        arrayList.add(ObjectAnimator.ofFloat(textView, "scaleX", new float[]{0.0f}));
                        arrayList.add(ObjectAnimator.ofFloat(textView, "scaleY", new float[]{0.0f}));
                        arrayList.add(ObjectAnimator.ofFloat(textView, "alpha", new float[]{0.0f}));
                        arrayList.add(ObjectAnimator.ofFloat(textView, "translationY", new float[]{0.0f}));
                        arrayList.add(ObjectAnimator.ofFloat(textView, "translationX", new float[]{(float) getXForTextView(i)}));
                    }
                }
                if (length == 0) {
                    this.stringBuilder.deleteCharAt(length);
                }
                for (i = 0; i < length; i++) {
                    arrayList.add(ObjectAnimator.ofFloat((TextView) this.characterTextViews.get(i), "translationX", new float[]{(float) getXForTextView(i)}));
                    arrayList.add(ObjectAnimator.ofFloat((TextView) this.dotTextViews.get(i), "translationX", new float[]{(float) getXForTextView(i)}));
                }
                if (this.dotRunnable != null) {
                    AndroidUtilities.cancelRunOnUIThread(this.dotRunnable);
                    this.dotRunnable = null;
                }
                if (this.currentAnimation != null) {
                    this.currentAnimation.cancel();
                }
                this.currentAnimation = new AnimatorSet();
                this.currentAnimation.setDuration(150);
                this.currentAnimation.playTogether(arrayList);
                this.currentAnimation.addListener(new C14003());
                this.currentAnimation.start();
            }
        }

        public String getString() {
            return this.stringBuilder.toString();
        }

        public int lenght() {
            return this.stringBuilder.length();
        }

        protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
            if (this.dotRunnable != null) {
                AndroidUtilities.cancelRunOnUIThread(this.dotRunnable);
                this.dotRunnable = null;
            }
            if (this.currentAnimation != null) {
                this.currentAnimation.cancel();
                this.currentAnimation = null;
            }
            for (int i5 = 0; i5 < 4; i5++) {
                if (i5 < this.stringBuilder.length()) {
                    TextView textView = (TextView) this.characterTextViews.get(i5);
                    textView.setAlpha(0.0f);
                    textView.setScaleX(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                    textView.setScaleY(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                    textView.setTranslationY(0.0f);
                    textView.setTranslationX((float) getXForTextView(i5));
                    textView = (TextView) this.dotTextViews.get(i5);
                    textView.setAlpha(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                    textView.setScaleX(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                    textView.setScaleY(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                    textView.setTranslationY(0.0f);
                    textView.setTranslationX((float) getXForTextView(i5));
                } else {
                    ((TextView) this.characterTextViews.get(i5)).setAlpha(0.0f);
                    ((TextView) this.dotTextViews.get(i5)).setAlpha(0.0f);
                }
            }
            super.onLayout(z, i, i2, i3, i4);
        }
    }

    public interface PasscodeViewDelegate {
        void didAcceptedPassword();
    }

    public PasscodeView(Context context) {
        int i;
        super(context);
        this.keyboardHeight = 0;
        this.rect = new Rect();
        setWillNotDraw(false);
        setVisibility(8);
        this.backgroundFrameLayout = new FrameLayout(context);
        addView(this.backgroundFrameLayout);
        LayoutParams layoutParams = (LayoutParams) this.backgroundFrameLayout.getLayoutParams();
        layoutParams.width = -1;
        layoutParams.height = -1;
        this.backgroundFrameLayout.setLayoutParams(layoutParams);
        this.passwordFrameLayout = new FrameLayout(context);
        addView(this.passwordFrameLayout);
        layoutParams = (LayoutParams) this.passwordFrameLayout.getLayoutParams();
        layoutParams.width = -1;
        layoutParams.height = -1;
        layoutParams.gravity = 51;
        this.passwordFrameLayout.setLayoutParams(layoutParams);
        this.imageView = new ImageView(context);
        this.imageView.setScaleType(ScaleType.FIT_XY);
        this.imageView.setImageResource(C0338R.drawable.passcode_logo);
        this.passwordFrameLayout.addView(this.imageView);
        layoutParams = (LayoutParams) this.imageView.getLayoutParams();
        if (AndroidUtilities.density < DefaultRetryPolicy.DEFAULT_BACKOFF_MULT) {
            layoutParams.width = AndroidUtilities.dp(BitmapDescriptorFactory.HUE_ORANGE);
            layoutParams.height = AndroidUtilities.dp(BitmapDescriptorFactory.HUE_ORANGE);
        } else {
            layoutParams.width = AndroidUtilities.dp(40.0f);
            layoutParams.height = AndroidUtilities.dp(40.0f);
        }
        layoutParams.gravity = 81;
        layoutParams.bottomMargin = AndroidUtilities.dp(100.0f);
        this.imageView.setLayoutParams(layoutParams);
        this.passcodeTextView = new TextView(context);
        this.passcodeTextView.setTypeface(FontUtil.m1176a().m1161d());
        this.passcodeTextView.setTextColor(-1);
        this.passcodeTextView.setTextSize(1, 14.0f);
        this.passcodeTextView.setGravity(1);
        this.passwordFrameLayout.addView(this.passcodeTextView);
        layoutParams = (LayoutParams) this.passcodeTextView.getLayoutParams();
        layoutParams.width = -2;
        layoutParams.height = -2;
        layoutParams.bottomMargin = AndroidUtilities.dp(62.0f);
        layoutParams.gravity = 81;
        this.passcodeTextView.setLayoutParams(layoutParams);
        this.passwordEditText2 = new AnimatingTextView(context);
        this.passwordFrameLayout.addView(this.passwordEditText2);
        layoutParams = (LayoutParams) this.passwordEditText2.getLayoutParams();
        layoutParams.height = -2;
        layoutParams.width = -1;
        layoutParams.leftMargin = AndroidUtilities.dp(70.0f);
        layoutParams.rightMargin = AndroidUtilities.dp(70.0f);
        layoutParams.bottomMargin = AndroidUtilities.dp(6.0f);
        layoutParams.gravity = 81;
        this.passwordEditText2.setLayoutParams(layoutParams);
        this.passwordEditText = new EditText(context);
        this.passwordEditText.setTextSize(1, 36.0f);
        this.passwordEditText.setTextColor(-1);
        this.passwordEditText.setMaxLines(1);
        this.passwordEditText.setLines(1);
        this.passwordEditText.setGravity(1);
        this.passwordEditText.setSingleLine(true);
        this.passwordEditText.setImeOptions(6);
        this.passwordEditText.setTypeface(Typeface.DEFAULT);
        this.passwordEditText.setBackgroundDrawable(null);
        AndroidUtilities.clearCursorDrawable(this.passwordEditText);
        this.passwordFrameLayout.addView(this.passwordEditText);
        layoutParams = (LayoutParams) this.passwordEditText.getLayoutParams();
        layoutParams.height = -2;
        layoutParams.width = -1;
        layoutParams.leftMargin = AndroidUtilities.dp(70.0f);
        layoutParams.rightMargin = AndroidUtilities.dp(70.0f);
        layoutParams.bottomMargin = AndroidUtilities.dp(6.0f);
        layoutParams.gravity = 81;
        this.passwordEditText.setLayoutParams(layoutParams);
        this.passwordEditText.setOnEditorActionListener(new C13881());
        this.passwordEditText.addTextChangedListener(new C13892());
        this.passwordEditText.setCustomSelectionActionModeCallback(new C13903());
        this.checkImage = new ImageView(context);
        this.checkImage.setImageResource(C0338R.drawable.passcode_check);
        this.checkImage.setScaleType(ScaleType.CENTER);
        this.checkImage.setBackgroundResource(C0338R.drawable.bar_selector_lock);
        this.passwordFrameLayout.addView(this.checkImage);
        layoutParams = (LayoutParams) this.checkImage.getLayoutParams();
        layoutParams.width = AndroidUtilities.dp(BitmapDescriptorFactory.HUE_YELLOW);
        layoutParams.height = AndroidUtilities.dp(BitmapDescriptorFactory.HUE_YELLOW);
        layoutParams.bottomMargin = AndroidUtilities.dp(4.0f);
        layoutParams.rightMargin = AndroidUtilities.dp(10.0f);
        layoutParams.gravity = 85;
        this.checkImage.setLayoutParams(layoutParams);
        this.checkImage.setOnClickListener(new C13914());
        View frameLayout = new FrameLayout(context);
        frameLayout.setBackgroundColor(654311423);
        this.passwordFrameLayout.addView(frameLayout);
        layoutParams = (LayoutParams) frameLayout.getLayoutParams();
        layoutParams.width = -1;
        layoutParams.height = AndroidUtilities.dp(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        layoutParams.gravity = 83;
        layoutParams.leftMargin = AndroidUtilities.dp(20.0f);
        layoutParams.rightMargin = AndroidUtilities.dp(20.0f);
        frameLayout.setLayoutParams(layoutParams);
        this.numbersFrameLayout = new FrameLayout(context);
        addView(this.numbersFrameLayout);
        layoutParams = (LayoutParams) this.numbersFrameLayout.getLayoutParams();
        layoutParams.width = -1;
        layoutParams.height = -1;
        layoutParams.gravity = 51;
        this.numbersFrameLayout.setLayoutParams(layoutParams);
        this.patternFrameLayout = new PatternFrame(context);
        addView(this.patternFrameLayout);
        layoutParams = (LayoutParams) this.patternFrameLayout.getLayoutParams();
        layoutParams.width = -1;
        layoutParams.height = -1;
        layoutParams.gravity = 51;
        this.patternFrameLayout.setLayoutParams(layoutParams);
        this.patternFrameLayout.setDelegate(new C13925());
        this.lettersTextViews = new ArrayList(10);
        this.numberTextViews = new ArrayList(10);
        this.numberFrameLayouts = new ArrayList(10);
        for (i = 0; i < 10; i++) {
            View textView = new TextView(context);
            textView.setTextColor(-1);
            textView.setTextSize(1, 36.0f);
            textView.setGravity(17);
            textView.setText(String.format(Locale.US, "%d", new Object[]{Integer.valueOf(i)}));
            this.numbersFrameLayout.addView(textView);
            layoutParams = (LayoutParams) textView.getLayoutParams();
            layoutParams.width = AndroidUtilities.dp(50.0f);
            layoutParams.height = AndroidUtilities.dp(50.0f);
            layoutParams.gravity = 51;
            textView.setLayoutParams(layoutParams);
            this.numberTextViews.add(textView);
            textView = new TextView(context);
            textView.setTextSize(1, 12.0f);
            textView.setTextColor(ConnectionsManager.DEFAULT_DATACENTER_ID);
            textView.setGravity(17);
            this.numbersFrameLayout.addView(textView);
            layoutParams = (LayoutParams) textView.getLayoutParams();
            layoutParams.width = AndroidUtilities.dp(50.0f);
            layoutParams.height = AndroidUtilities.dp(20.0f);
            layoutParams.gravity = 51;
            textView.setLayoutParams(layoutParams);
            switch (i) {
                case VideoPlayer.TRACK_DEFAULT /*0*/:
                    textView.setText("+");
                    break;
                case VideoPlayer.STATE_PREPARING /*2*/:
                    textView.setText("ABC");
                    break;
                case VideoPlayer.STATE_BUFFERING /*3*/:
                    textView.setText("DEF");
                    break;
                case VideoPlayer.STATE_READY /*4*/:
                    textView.setText("GHI");
                    break;
                case VideoPlayer.STATE_ENDED /*5*/:
                    textView.setText("JKL");
                    break;
                case Method.TRACE /*6*/:
                    textView.setText("MNO");
                    break;
                case Method.PATCH /*7*/:
                    textView.setText("PQRS");
                    break;
                case TLRPC.USER_FLAG_USERNAME /*8*/:
                    textView.setText("TUV");
                    break;
                case C0338R.styleable.PromptView_iconTint /*9*/:
                    textView.setText("WXYZ");
                    break;
                default:
                    break;
            }
            this.lettersTextViews.add(textView);
        }
        this.eraseView = new ImageView(context);
        this.eraseView.setScaleType(ScaleType.CENTER);
        this.eraseView.setImageResource(C0338R.drawable.passcode_delete);
        this.numbersFrameLayout.addView(this.eraseView);
        layoutParams = (LayoutParams) this.eraseView.getLayoutParams();
        layoutParams.width = AndroidUtilities.dp(50.0f);
        layoutParams.height = AndroidUtilities.dp(50.0f);
        layoutParams.gravity = 51;
        this.eraseView.setLayoutParams(layoutParams);
        for (int i2 = 0; i2 < 11; i2++) {
            FrameLayout frameLayout2 = new FrameLayout(context);
            frameLayout2.setBackgroundResource(C0338R.drawable.bar_selector_lock);
            frameLayout2.setTag(Integer.valueOf(i2));
            if (i2 == 10) {
                frameLayout2.setOnLongClickListener(new C13936());
            }
            frameLayout2.setOnClickListener(new C13947());
            this.numberFrameLayouts.add(frameLayout2);
        }
        for (i = 10; i >= 0; i--) {
            FrameLayout frameLayout3 = (FrameLayout) this.numberFrameLayouts.get(i);
            this.numbersFrameLayout.addView(frameLayout3);
            LayoutParams layoutParams2 = (LayoutParams) frameLayout3.getLayoutParams();
            layoutParams2.width = AndroidUtilities.dp(100.0f);
            layoutParams2.height = AndroidUtilities.dp(100.0f);
            layoutParams2.gravity = 51;
            frameLayout3.setLayoutParams(layoutParams2);
        }
    }

    private void checkFingerprint() {
        Activity activity = (Activity) getContext();
        if (VERSION.SDK_INT >= 23 && activity != null && UserConfig.useFingerprint && !ApplicationLoader.mainInterfacePaused) {
            try {
                if (this.fingerprintDialog != null && this.fingerprintDialog.isShowing()) {
                    return;
                }
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
            try {
                FingerprintManagerCompat from = FingerprintManagerCompat.from(ApplicationLoader.applicationContext);
                if (from.isHardwareDetected() && from.hasEnrolledFingerprints()) {
                    View relativeLayout = new RelativeLayout(getContext());
                    relativeLayout.setPadding(AndroidUtilities.dp(24.0f), AndroidUtilities.dp(16.0f), AndroidUtilities.dp(24.0f), AndroidUtilities.dp(8.0f));
                    View textView = new TextView(getContext());
                    textView.setTextColor(-7105645);
                    textView.setId(id_fingerprint_textview);
                    textView.setTextAppearance(16974344);
                    textView.setText(LocaleController.getString("FingerprintInfo", C0338R.string.FingerprintInfo));
                    relativeLayout.addView(textView);
                    ViewGroup.LayoutParams createRelative = LayoutHelper.createRelative(-2, -2);
                    createRelative.addRule(10);
                    createRelative.addRule(20);
                    textView.setLayoutParams(createRelative);
                    this.fingerprintImageView = new ImageView(getContext());
                    this.fingerprintImageView.setImageResource(C0338R.drawable.ic_fp_40px);
                    this.fingerprintImageView.setId(id_fingerprint_imageview);
                    relativeLayout.addView(this.fingerprintImageView, LayoutHelper.createRelative(-2.0f, -2.0f, 0, 20, 0, 0, 20, 3, id_fingerprint_textview));
                    this.fingerprintStatusTextView = new TextView(getContext());
                    this.fingerprintStatusTextView.setGravity(16);
                    this.fingerprintStatusTextView.setText(LocaleController.getString("FingerprintHelp", C0338R.string.FingerprintHelp));
                    this.fingerprintStatusTextView.setTextAppearance(16974320);
                    this.fingerprintStatusTextView.setTextColor(1107296256);
                    relativeLayout.addView(this.fingerprintStatusTextView);
                    ViewGroup.LayoutParams createRelative2 = LayoutHelper.createRelative(-2, -2);
                    createRelative2.setMarginStart(AndroidUtilities.dp(16.0f));
                    createRelative2.addRule(8, id_fingerprint_imageview);
                    createRelative2.addRule(6, id_fingerprint_imageview);
                    createRelative2.addRule(17, id_fingerprint_imageview);
                    this.fingerprintStatusTextView.setLayoutParams(createRelative2);
                    Builder builder = new Builder(getContext());
                    builder.setTitle(LocaleController.getString("AppName", C0338R.string.AppName));
                    builder.setView(relativeLayout);
                    builder.setNegativeButton(LocaleController.getString("Cancel", C0338R.string.Cancel), null);
                    builder.setOnDismissListener(new OnDismissListener() {
                        public void onDismiss(DialogInterface dialogInterface) {
                            if (PasscodeView.this.cancellationSignal != null) {
                                PasscodeView.this.selfCancelled = true;
                                PasscodeView.this.cancellationSignal.cancel();
                                PasscodeView.this.cancellationSignal = null;
                            }
                        }
                    });
                    if (this.fingerprintDialog != null) {
                        if (this.fingerprintDialog.isShowing()) {
                            this.fingerprintDialog.dismiss();
                        }
                    }
                    this.fingerprintDialog = builder.show();
                    this.cancellationSignal = new CancellationSignal();
                    this.selfCancelled = false;
                    from.authenticate(null, 0, this.cancellationSignal, new AuthenticationCallback() {
                        public void onAuthenticationError(int i, CharSequence charSequence) {
                            if (!PasscodeView.this.selfCancelled) {
                                PasscodeView.this.showFingerprintError(charSequence);
                            }
                        }

                        public void onAuthenticationFailed() {
                            PasscodeView.this.showFingerprintError(LocaleController.getString("FingerprintNotRecognized", C0338R.string.FingerprintNotRecognized));
                        }

                        public void onAuthenticationHelp(int i, CharSequence charSequence) {
                            PasscodeView.this.showFingerprintError(charSequence);
                        }

                        public void onAuthenticationSucceeded(AuthenticationResult authenticationResult) {
                            try {
                                if (PasscodeView.this.fingerprintDialog.isShowing()) {
                                    PasscodeView.this.fingerprintDialog.dismiss();
                                }
                            } catch (Throwable e) {
                                FileLog.m18e("tmessages", e);
                            }
                            PasscodeView.this.fingerprintDialog = null;
                            PasscodeView.this.processDone(true);
                        }
                    }, null);
                }
            } catch (Throwable e2) {
                FileLog.m18e("tmessages", e2);
            } catch (Throwable th) {
            }
        }
    }

    private void onPasscodeError() {
        Vibrator vibrator = (Vibrator) getContext().getSystemService("vibrator");
        if (vibrator != null) {
            vibrator.vibrate(200);
        }
        shakeTextView(2.0f, 0);
    }

    private void processDone(boolean z) {
        if (!z) {
            String str = TtmlNode.ANONYMOUS_REGION_ID;
            if (UserConfig.passcodeType == 0) {
                str = this.passwordEditText2.getString();
            } else if (UserConfig.passcodeType == 1) {
                str = this.passwordEditText.getText().toString();
            } else if (UserConfig.passcodeType == 2) {
                str = this.pattern;
            }
            if (str.length() == 0) {
                onPasscodeError();
                return;
            } else if (!(UserConfig.checkPasscode(str) || UserConfig.passcodeType == 2)) {
                this.passwordEditText.setText(TtmlNode.ANONYMOUS_REGION_ID);
                this.passwordEditText2.eraseAllCharacters(true);
                onPasscodeError();
                return;
            }
        }
        this.passwordEditText.clearFocus();
        AndroidUtilities.hideKeyboard(this.passwordEditText);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(200);
        r1 = new Animator[2];
        r1[0] = ObjectAnimator.ofFloat(this, "translationY", new float[]{(float) AndroidUtilities.dp(20.0f)});
        r1[1] = ObjectAnimator.ofFloat(this, "alpha", new float[]{(float) AndroidUtilities.dp(0.0f)});
        animatorSet.playTogether(r1);
        animatorSet.addListener(new C13958());
        animatorSet.start();
        UserConfig.appLocked = false;
        UserConfig.saveConfig(false);
        NotificationCenter.getInstance().postNotificationName(NotificationCenter.didSetPasscode, new Object[0]);
        setOnTouchListener(null);
        if (this.delegate != null) {
            this.delegate.didAcceptedPassword();
        }
    }

    private void shakeTextView(float f, int i) {
        if (i != 6) {
            AnimatorSet animatorSet = new AnimatorSet();
            Animator[] animatorArr = new Animator[1];
            animatorArr[0] = ObjectAnimator.ofFloat(this.passcodeTextView, "translationX", new float[]{(float) AndroidUtilities.dp(f)});
            animatorSet.playTogether(animatorArr);
            animatorSet.setDuration(50);
            animatorSet.addListener(new C13969(i, f));
            animatorSet.start();
        }
    }

    private void showFingerprintError(CharSequence charSequence) {
        this.fingerprintImageView.setImageResource(C0338R.drawable.ic_fingerprint_error);
        this.fingerprintStatusTextView.setText(charSequence);
        this.fingerprintStatusTextView.setTextColor(-765666);
        Vibrator vibrator = (Vibrator) getContext().getSystemService("vibrator");
        if (vibrator != null) {
            vibrator.vibrate(200);
        }
        AndroidUtilities.shakeView(this.fingerprintStatusTextView, 2.0f, 0);
    }

    protected void onDraw(Canvas canvas) {
        if (getVisibility() == 0) {
            if (this.backgroundDrawable == null) {
                super.onDraw(canvas);
            } else if (this.backgroundDrawable instanceof ColorDrawable) {
                this.backgroundDrawable.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
                this.backgroundDrawable.draw(canvas);
            } else {
                float measuredWidth = ((float) getMeasuredWidth()) / ((float) this.backgroundDrawable.getIntrinsicWidth());
                float measuredHeight = ((float) (getMeasuredHeight() + this.keyboardHeight)) / ((float) this.backgroundDrawable.getIntrinsicHeight());
                if (measuredWidth >= measuredHeight) {
                    measuredHeight = measuredWidth;
                }
                int ceil = (int) Math.ceil((double) (((float) this.backgroundDrawable.getIntrinsicWidth()) * measuredHeight));
                int ceil2 = (int) Math.ceil((double) (measuredHeight * ((float) this.backgroundDrawable.getIntrinsicHeight())));
                int measuredWidth2 = (getMeasuredWidth() - ceil) / 2;
                int measuredHeight2 = ((getMeasuredHeight() - ceil2) + this.keyboardHeight) / 2;
                this.backgroundDrawable.setBounds(measuredWidth2, measuredHeight2, ceil + measuredWidth2, ceil2 + measuredHeight2);
                this.backgroundDrawable.draw(canvas);
            }
        }
    }

    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        int i5 = 0;
        View rootView = getRootView();
        int height = (rootView.getHeight() - AndroidUtilities.statusBarHeight) - AndroidUtilities.getViewInset(rootView);
        getWindowVisibleDisplayFrame(this.rect);
        this.keyboardHeight = height - (this.rect.bottom - this.rect.top);
        if (UserConfig.passcodeType == 1 && (AndroidUtilities.isTablet() || getContext().getResources().getConfiguration().orientation != 2)) {
            LayoutParams layoutParams = (LayoutParams) this.passwordFrameLayout.getLayoutParams();
            int intValue = ((this.passwordFrameLayout.getTag() != null ? ((Integer) this.passwordFrameLayout.getTag()).intValue() : 0) + layoutParams.height) - (this.keyboardHeight / 2);
            if (VERSION.SDK_INT >= 21) {
                i5 = AndroidUtilities.statusBarHeight;
            }
            layoutParams.topMargin = intValue - i5;
            this.passwordFrameLayout.setLayoutParams(layoutParams);
        }
        super.onLayout(z, i, i2, i3, i4);
    }

    protected void onMeasure(int i, int i2) {
        int dp;
        int dp2;
        int i3;
        LayoutParams layoutParams;
        int size = MeasureSpec.getSize(i);
        int i4 = AndroidUtilities.displaySize.y - (VERSION.SDK_INT >= 21 ? 0 : AndroidUtilities.statusBarHeight);
        if (AndroidUtilities.isTablet() || getContext().getResources().getConfiguration().orientation != 2) {
            if (AndroidUtilities.isTablet()) {
                if (size > AndroidUtilities.dp(498.0f)) {
                    dp = (size - AndroidUtilities.dp(498.0f)) / 2;
                    size = AndroidUtilities.dp(498.0f);
                } else {
                    dp = 0;
                }
                if (i4 > AndroidUtilities.dp(528.0f)) {
                    dp2 = (i4 - AndroidUtilities.dp(528.0f)) / 2;
                    i4 = AndroidUtilities.dp(528.0f);
                    i3 = size;
                    size = dp2;
                    dp2 = dp;
                } else {
                    dp2 = dp;
                    i3 = size;
                    size = 0;
                }
            } else {
                dp2 = 0;
                i3 = size;
                size = 0;
            }
            layoutParams = (LayoutParams) this.passwordFrameLayout.getLayoutParams();
            layoutParams.height = i4 / 3;
            layoutParams.width = i3;
            layoutParams.topMargin = size;
            layoutParams.leftMargin = dp2;
            this.passwordFrameLayout.setTag(Integer.valueOf(size));
            this.passwordFrameLayout.setLayoutParams(layoutParams);
            layoutParams = (LayoutParams) this.numbersFrameLayout.getLayoutParams();
            layoutParams.height = (i4 / 3) * 2;
            layoutParams.leftMargin = dp2;
            layoutParams.topMargin = (i4 - layoutParams.height) + size;
            layoutParams.width = i3;
            this.numbersFrameLayout.setLayoutParams(layoutParams);
        } else {
            layoutParams = (LayoutParams) this.passwordFrameLayout.getLayoutParams();
            layoutParams.width = UserConfig.passcodeType == 0 ? size / 2 : size;
            layoutParams.height = AndroidUtilities.dp(140.0f);
            layoutParams.topMargin = (i4 - AndroidUtilities.dp(140.0f)) / 2;
            this.passwordFrameLayout.setLayoutParams(layoutParams);
            layoutParams = (LayoutParams) this.numbersFrameLayout.getLayoutParams();
            layoutParams.height = i4;
            layoutParams.leftMargin = size / 2;
            layoutParams.topMargin = i4 - layoutParams.height;
            layoutParams.width = size / 2;
            this.numbersFrameLayout.setLayoutParams(layoutParams);
        }
        int dp3 = (layoutParams.width - (AndroidUtilities.dp(50.0f) * 3)) / 4;
        int dp4 = (layoutParams.height - (AndroidUtilities.dp(50.0f) * 4)) / 5;
        i3 = 0;
        while (i3 < 11) {
            LayoutParams layoutParams2;
            dp = i3 == 0 ? 10 : i3 == 10 ? 11 : i3 - 1;
            i4 = dp / 3;
            int i5 = dp % 3;
            if (i3 < 10) {
                TextView textView = (TextView) this.numberTextViews.get(i3);
                TextView textView2 = (TextView) this.lettersTextViews.get(i3);
                LayoutParams layoutParams3 = (LayoutParams) textView.getLayoutParams();
                layoutParams2 = (LayoutParams) textView2.getLayoutParams();
                i4 = (i4 * (AndroidUtilities.dp(50.0f) + dp4)) + dp4;
                layoutParams3.topMargin = i4;
                layoutParams2.topMargin = i4;
                i5 = (i5 * (AndroidUtilities.dp(50.0f) + dp3)) + dp3;
                layoutParams3.leftMargin = i5;
                layoutParams2.leftMargin = i5;
                layoutParams2.topMargin += AndroidUtilities.dp(40.0f);
                textView.setLayoutParams(layoutParams3);
                textView2.setLayoutParams(layoutParams2);
                layoutParams2 = layoutParams3;
                dp2 = i4;
            } else {
                layoutParams = (LayoutParams) this.eraseView.getLayoutParams();
                int dp5 = (((AndroidUtilities.dp(50.0f) + dp4) * i4) + dp4) + AndroidUtilities.dp(8.0f);
                layoutParams.topMargin = dp5;
                layoutParams.leftMargin = ((AndroidUtilities.dp(50.0f) + dp3) * i5) + dp3;
                dp5 -= AndroidUtilities.dp(8.0f);
                this.eraseView.setLayoutParams(layoutParams);
                dp2 = dp5;
                layoutParams2 = layoutParams;
            }
            FrameLayout frameLayout = (FrameLayout) this.numberFrameLayouts.get(i3);
            LayoutParams layoutParams4 = (LayoutParams) frameLayout.getLayoutParams();
            layoutParams4.topMargin = dp2 - AndroidUtilities.dp(17.0f);
            layoutParams4.leftMargin = layoutParams2.leftMargin - AndroidUtilities.dp(25.0f);
            frameLayout.setLayoutParams(layoutParams4);
            i3++;
        }
        super.onMeasure(i, i2);
    }

    public void onPause() {
        if (this.fingerprintDialog != null) {
            try {
                if (this.fingerprintDialog.isShowing()) {
                    this.fingerprintDialog.dismiss();
                }
                this.fingerprintDialog = null;
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
        }
        try {
            if (VERSION.SDK_INT >= 23 && this.cancellationSignal != null) {
                this.cancellationSignal.cancel();
                this.cancellationSignal = null;
            }
        } catch (Throwable e2) {
            FileLog.m18e("tmessages", e2);
        }
    }

    public void onResume() {
        if (UserConfig.passcodeType == 1) {
            if (this.passwordEditText != null) {
                this.passwordEditText.requestFocus();
                AndroidUtilities.showKeyboard(this.passwordEditText);
            }
            AndroidUtilities.runOnUIThread(new Runnable() {
                public void run() {
                    if (PasscodeView.this.passwordEditText != null) {
                        PasscodeView.this.passwordEditText.requestFocus();
                        AndroidUtilities.showKeyboard(PasscodeView.this.passwordEditText);
                    }
                }
            }, 200);
        }
        checkFingerprint();
    }

    public void onShow() {
        Activity activity = (Activity) getContext();
        if (UserConfig.passcodeType == 1) {
            if (this.passwordEditText != null) {
                this.passwordEditText.requestFocus();
                AndroidUtilities.showKeyboard(this.passwordEditText);
            }
        } else if (activity != null) {
            View currentFocus = activity.getCurrentFocus();
            if (currentFocus != null) {
                currentFocus.clearFocus();
                AndroidUtilities.hideKeyboard(((Activity) getContext()).getCurrentFocus());
            }
        }
        checkFingerprint();
        if (getVisibility() != 0) {
            setAlpha(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            setTranslationY(0.0f);
            if (ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).getInt("selectedBackground", 1000001) == 1000001) {
                this.backgroundFrameLayout.setBackgroundColor(ThemeUtil.m2485a().m2289c());
                if (ThemeUtil.m2490b()) {
                    this.backgroundFrameLayout.setBackgroundColor(AdvanceTheme.m2276a(AdvanceTheme.f2491b, 21));
                }
            } else {
                this.backgroundDrawable = ApplicationLoader.getCachedWallpaper();
                if (this.backgroundDrawable != null) {
                    this.backgroundFrameLayout.setBackgroundColor(-1090519040);
                } else {
                    this.backgroundFrameLayout.setBackgroundColor(ThemeUtil.m2485a().m2289c());
                    if (ThemeUtil.m2490b()) {
                        this.backgroundFrameLayout.setBackgroundColor(AdvanceTheme.m2276a(AdvanceTheme.f2491b, 21));
                    }
                }
            }
            this.passcodeTextView.setText(LocaleController.getString("EnterYourPasscode", C0338R.string.EnterYourPasscode));
            if (UserConfig.passcodeType == 0) {
                this.numbersFrameLayout.setVisibility(0);
                this.passwordEditText.setVisibility(8);
                this.passwordEditText2.setVisibility(0);
                this.checkImage.setVisibility(8);
                this.patternFrameLayout.setVisibility(8);
                this.passcodeTextView.setVisibility(0);
                this.imageView.setVisibility(0);
                this.passwordFrameLayout.setVisibility(0);
            } else if (UserConfig.passcodeType == 1) {
                this.passwordEditText.setFilters(new InputFilter[0]);
                this.passwordEditText.setInputType(129);
                this.numbersFrameLayout.setVisibility(8);
                this.passwordEditText.setFocusable(true);
                this.passwordEditText.setFocusableInTouchMode(true);
                this.passwordEditText.setVisibility(0);
                this.passwordEditText2.setVisibility(8);
                this.checkImage.setVisibility(0);
                this.patternFrameLayout.setVisibility(8);
                this.passcodeTextView.setVisibility(0);
                this.imageView.setVisibility(0);
                this.passwordFrameLayout.setVisibility(0);
            } else if (UserConfig.passcodeType == 2) {
                this.patternFrameLayout.setVisibility(0);
                this.patternFrameLayout.removeAllViews();
                this.patternFrameLayout.m1573a();
                this.numbersFrameLayout.setVisibility(8);
                this.checkImage.setVisibility(8);
                this.passwordEditText.setVisibility(8);
                this.passwordEditText2.setVisibility(8);
                this.passcodeTextView.setVisibility(8);
                this.imageView.setVisibility(8);
                this.passwordFrameLayout.setVisibility(8);
            }
            setVisibility(0);
            this.passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
            this.passwordEditText.setText(TtmlNode.ANONYMOUS_REGION_ID);
            this.passwordEditText2.eraseAllCharacters(false);
            setOnTouchListener(new OnTouchListener() {
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    return true;
                }
            });
        }
    }

    public void setDelegate(PasscodeViewDelegate passcodeViewDelegate) {
        this.delegate = passcodeViewDelegate;
    }
}
