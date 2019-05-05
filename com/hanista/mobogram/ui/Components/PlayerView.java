package com.hanista.mobogram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff.Mode;
import android.text.SpannableStringBuilder;
import android.text.TextUtils.TruncateAt;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.AnimatorListenerAdapterProxy;
import com.hanista.mobogram.messenger.MediaController;
import com.hanista.mobogram.messenger.MessageObject;
import com.hanista.mobogram.messenger.NotificationCenter;
import com.hanista.mobogram.messenger.NotificationCenter.NotificationCenterDelegate;
import com.hanista.mobogram.mobo.MoboConstants;
import com.hanista.mobogram.mobo.p008i.FontUtil;
import com.hanista.mobogram.mobo.p020s.AdvanceTheme;
import com.hanista.mobogram.mobo.p020s.ThemeUtil;
import com.hanista.mobogram.ui.ActionBar.BaseFragment;
import com.hanista.mobogram.ui.ActionBar.Theme;
import com.hanista.mobogram.ui.AudioPlayerActivity;
import com.hanista.mobogram.ui.ChatActivity;
import com.hanista.mobogram.ui.DialogsActivity;

public class PlayerView extends FrameLayout implements NotificationCenterDelegate {
    private AnimatorSet animatorSet;
    private BaseFragment fragment;
    private MessageObject lastMessageObject;
    private ImageView playButton;
    private int titleColor;
    private TextView titleTextView;
    private float topPadding;
    private boolean visible;
    private float yPosition;

    /* renamed from: com.hanista.mobogram.ui.Components.PlayerView.1 */
    class C14401 implements OnClickListener {
        C14401() {
        }

        public void onClick(View view) {
            if (MediaController.m71a().m191s()) {
                MediaController.m71a().m158a(MediaController.m71a().m182j());
            } else {
                MediaController.m71a().m166b(MediaController.m71a().m182j());
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Components.PlayerView.2 */
    class C14412 implements OnClickListener {
        C14412() {
        }

        public void onClick(View view) {
            MediaController.m71a().m155a(true, true);
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Components.PlayerView.3 */
    class C14423 implements OnClickListener {
        C14423() {
        }

        public void onClick(View view) {
            MessageObject j = MediaController.m71a().m182j();
            if (j != null && j.isMusic() && PlayerView.this.fragment != null) {
                PlayerView.this.fragment.presentFragment(new AudioPlayerActivity());
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Components.PlayerView.4 */
    class C14434 extends AnimatorListenerAdapterProxy {
        C14434() {
        }

        public void onAnimationEnd(Animator animator) {
            if (PlayerView.this.animatorSet != null && PlayerView.this.animatorSet.equals(animator)) {
                PlayerView.this.setVisibility(8);
                PlayerView.this.animatorSet = null;
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Components.PlayerView.5 */
    class C14445 extends AnimatorListenerAdapterProxy {
        C14445() {
        }

        public void onAnimationEnd(Animator animator) {
            if (PlayerView.this.animatorSet != null && PlayerView.this.animatorSet.equals(animator)) {
                PlayerView.this.animatorSet = null;
            }
        }
    }

    public PlayerView(Context context, BaseFragment baseFragment) {
        super(context);
        this.titleColor = Theme.MSG_IN_CONTACT_PHONE_TEXT_COLOR;
        this.fragment = baseFragment;
        this.visible = true;
        ((ViewGroup) this.fragment.getFragmentView()).setClipToPadding(false);
        setTag(Integer.valueOf(1));
        View frameLayout = new FrameLayout(context);
        frameLayout.setBackgroundColor(-1);
        addView(frameLayout, LayoutHelper.createFrame(-1, 36.0f, 51, 0.0f, 0.0f, 0.0f, 0.0f));
        View view = new View(context);
        view.setBackgroundResource(C0338R.drawable.header_shadow);
        addView(view, LayoutHelper.createFrame(-1, 3.0f, 51, 0.0f, 36.0f, 0.0f, 0.0f));
        this.playButton = new ImageView(context);
        this.playButton.setScaleType(ScaleType.CENTER);
        addView(this.playButton, LayoutHelper.createFrame(36, 36.0f, 51, 0.0f, 0.0f, 0.0f, 0.0f));
        this.playButton.setOnClickListener(new C14401());
        paintPlayer();
        this.titleTextView = new TextView(context);
        this.titleTextView.setTextColor(Theme.MSG_IN_CONTACT_PHONE_TEXT_COLOR);
        if (ThemeUtil.m2490b()) {
            this.titleTextView.setTextColor(this.titleColor);
        }
        this.titleTextView.setMaxLines(1);
        this.titleTextView.setLines(1);
        this.titleTextView.setSingleLine(true);
        this.titleTextView.setEllipsize(TruncateAt.END);
        this.titleTextView.setTextSize(1, 15.0f);
        this.titleTextView.setGravity(19);
        this.titleTextView.setTypeface(FontUtil.m1176a().m1161d());
        addView(this.titleTextView, LayoutHelper.createFrame(-1, 36.0f, 51, 35.0f, 0.0f, 36.0f, 0.0f));
        View imageView = new ImageView(context);
        imageView.setImageResource(C0338R.drawable.miniplayer_close);
        imageView.setScaleType(ScaleType.CENTER);
        addView(imageView, LayoutHelper.createFrame(36, 36, 53));
        imageView.setOnClickListener(new C14412());
        setOnClickListener(new C14423());
    }

    private void checkPlayer(boolean z) {
        MessageObject j = MediaController.m71a().m182j();
        View fragmentView = this.fragment.getFragmentView();
        if (!(z || fragmentView == null || (fragmentView.getParent() != null && ((View) fragmentView.getParent()).getVisibility() == 0))) {
            z = true;
        }
        if (j == null || j.getId() == 0) {
            this.lastMessageObject = null;
            if (this.visible) {
                this.visible = false;
                if (z) {
                    if (getVisibility() != 8) {
                        setVisibility(8);
                    }
                    setTopPadding(0.0f);
                    return;
                }
                if (this.animatorSet != null) {
                    this.animatorSet.cancel();
                    this.animatorSet = null;
                }
                this.animatorSet = new AnimatorSet();
                AnimatorSet animatorSet = this.animatorSet;
                r2 = new Animator[2];
                r2[0] = ObjectAnimator.ofFloat(this, "translationY", new float[]{(float) (-AndroidUtilities.dp(36.0f))});
                r2[1] = ObjectAnimator.ofFloat(this, "topPadding", new float[]{0.0f});
                animatorSet.playTogether(r2);
                this.animatorSet.setDuration(200);
                this.animatorSet.addListener(new C14434());
                this.animatorSet.start();
                return;
            }
            return;
        }
        if (z && this.topPadding == 0.0f) {
            setTopPadding((float) AndroidUtilities.dp(36.0f));
            setTranslationY(0.0f);
            this.yPosition = 0.0f;
        }
        if (!this.visible) {
            if (!z) {
                if (this.animatorSet != null) {
                    this.animatorSet.cancel();
                    this.animatorSet = null;
                }
                this.animatorSet = new AnimatorSet();
                animatorSet = this.animatorSet;
                r3 = new Animator[2];
                r3[0] = ObjectAnimator.ofFloat(this, "translationY", new float[]{(float) (-AndroidUtilities.dp(36.0f)), 0.0f});
                r3[1] = ObjectAnimator.ofFloat(this, "topPadding", new float[]{(float) AndroidUtilities.dp(36.0f)});
                animatorSet.playTogether(r3);
                this.animatorSet.setDuration(200);
                this.animatorSet.addListener(new C14445());
                this.animatorSet.start();
            }
            this.visible = true;
            setVisibility(0);
        }
        if (MediaController.m71a().m191s()) {
            this.playButton.setImageResource(C0338R.drawable.miniplayer_play);
        } else {
            this.playButton.setImageResource(C0338R.drawable.miniplayer_pause);
        }
        if (this.lastMessageObject != j) {
            CharSequence spannableStringBuilder;
            this.lastMessageObject = j;
            if (this.lastMessageObject.isVoice()) {
                spannableStringBuilder = new SpannableStringBuilder(String.format("%s %s", new Object[]{j.getMusicAuthor(), j.getMusicTitle()}));
                this.titleTextView.setEllipsize(TruncateAt.MIDDLE);
            } else {
                spannableStringBuilder = new SpannableStringBuilder(String.format("%s - %s", new Object[]{j.getMusicAuthor(), j.getMusicTitle()}));
                this.titleTextView.setEllipsize(TruncateAt.END);
            }
            Object typefaceSpan = new TypefaceSpan(FontUtil.m1176a().m1160c());
            if (ThemeUtil.m2490b()) {
                typefaceSpan = new TypefaceSpan(FontUtil.m1176a().m1160c(), 0, this.titleColor);
            }
            spannableStringBuilder.setSpan(typefaceSpan, 0, j.getMusicAuthor().length(), 18);
            this.titleTextView.setText(spannableStringBuilder);
        }
    }

    private void paintPlayer() {
        int i = -1;
        if (ThemeUtil.m2490b()) {
            int i2;
            int i3 = AdvanceTheme.f2491b;
            if (this.fragment instanceof DialogsActivity) {
                i2 = AdvanceTheme.f2508s;
                i3 = AdvanceTheme.f2507r;
                i = AdvanceTheme.f2513x;
            } else if (this.fragment instanceof ChatActivity) {
                i2 = AdvanceTheme.bg;
                i3 = AdvanceTheme.bi;
                i = AdvanceTheme.bh;
            } else {
                i2 = i3;
                i3 = -1;
            }
            setBackgroundColor(0);
            if (getChildAt(0) != null) {
                getChildAt(0).setBackgroundColor(i2);
            }
            if (this.titleColor != i) {
                this.titleColor = i;
                if (this.titleTextView != null) {
                    this.titleTextView.setTextColor(i);
                }
            }
            this.playButton.setColorFilter(i3, Mode.SRC_IN);
            if (getChildAt(4) != null) {
                ((ImageView) getChildAt(4)).setColorFilter(i3, Mode.SRC_IN);
            }
        }
    }

    public void didReceivedNotification(int i, Object... objArr) {
        if (i == NotificationCenter.audioDidStarted || i == NotificationCenter.audioPlayStateChanged || i == NotificationCenter.audioDidReset) {
            checkPlayer(false);
        }
    }

    protected boolean drawChild(Canvas canvas, View view, long j) {
        int save = canvas.save();
        if (this.yPosition < 0.0f) {
            canvas.clipRect(0, (int) (-this.yPosition), view.getMeasuredWidth(), AndroidUtilities.dp(39.0f));
        }
        boolean drawChild = super.drawChild(canvas, view, j);
        canvas.restoreToCount(save);
        paintPlayer();
        return drawChild;
    }

    public float getTopPadding() {
        return this.topPadding;
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.audioDidReset);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.audioPlayStateChanged);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.audioDidStarted);
        checkPlayer(true);
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.topPadding = 0.0f;
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.audioDidReset);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.audioPlayStateChanged);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.audioDidStarted);
    }

    protected void onMeasure(int i, int i2) {
        super.onMeasure(i, AndroidUtilities.dp(39.0f));
    }

    public void setTopPadding(float f) {
        this.topPadding = f;
        if (this.fragment != null) {
            View fragmentView = this.fragment.getFragmentView();
            if (fragmentView != null) {
                fragmentView.setPadding(0, (int) this.topPadding, 0, 0);
                if (MoboConstants.aO) {
                    fragmentView.setPadding(0, ((int) this.topPadding) + AndroidUtilities.dp(36.0f), 0, 0);
                }
            }
        }
    }

    public void setTranslationY(float f) {
        super.setTranslationY(f);
        this.yPosition = f;
        invalidate();
    }
}
