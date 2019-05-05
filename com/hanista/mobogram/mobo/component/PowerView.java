package com.hanista.mobogram.mobo.component;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff.Mode;
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
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.MediaController;
import com.hanista.mobogram.messenger.MessageObject;
import com.hanista.mobogram.messenger.NotificationCenter;
import com.hanista.mobogram.messenger.NotificationCenter.NotificationCenterDelegate;
import com.hanista.mobogram.mobo.MoboConstants;
import com.hanista.mobogram.mobo.MoboUtils;
import com.hanista.mobogram.mobo.p008i.FontUtil;
import com.hanista.mobogram.mobo.p020s.AdvanceTheme;
import com.hanista.mobogram.mobo.p020s.ThemeUtil;
import com.hanista.mobogram.ui.ActionBar.BaseFragment;
import com.hanista.mobogram.ui.ActionBar.Theme;
import com.hanista.mobogram.ui.ChatActivity;
import com.hanista.mobogram.ui.Components.LayoutHelper;
import com.hanista.mobogram.ui.DialogsActivity;

/* renamed from: com.hanista.mobogram.mobo.component.e */
public class PowerView extends FrameLayout implements NotificationCenterDelegate {
    private ImageView f430a;
    private TextView f431b;
    private MessageObject f432c;
    private AnimatorSet f433d;
    private float f434e;
    private BaseFragment f435f;
    private float f436g;
    private boolean f437h;
    private int f438i;

    /* renamed from: com.hanista.mobogram.mobo.component.e.1 */
    class PowerView implements OnClickListener {
        final /* synthetic */ PowerView f427a;

        PowerView(PowerView powerView) {
            this.f427a = powerView;
        }

        public void onClick(View view) {
            MoboUtils.m1723h();
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.component.e.2 */
    class PowerView extends AnimatorListenerAdapterProxy {
        final /* synthetic */ PowerView f428a;

        PowerView(PowerView powerView) {
            this.f428a = powerView;
        }

        public void onAnimationEnd(Animator animator) {
            if (this.f428a.f433d != null && this.f428a.f433d.equals(animator)) {
                this.f428a.setVisibility(8);
                this.f428a.f433d = null;
            }
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.component.e.3 */
    class PowerView extends AnimatorListenerAdapterProxy {
        final /* synthetic */ PowerView f429a;

        PowerView(PowerView powerView) {
            this.f429a = powerView;
        }

        public void onAnimationEnd(Animator animator) {
            if (this.f429a.f433d != null && this.f429a.f433d.equals(animator)) {
                this.f429a.f433d = null;
            }
        }
    }

    public PowerView(Context context, BaseFragment baseFragment) {
        super(context);
        this.f438i = Theme.MSG_IN_CONTACT_PHONE_TEXT_COLOR;
        this.f435f = baseFragment;
        this.f437h = true;
        ((ViewGroup) this.f435f.getFragmentView()).setClipToPadding(false);
        setTag(Integer.valueOf(1));
        View frameLayout = new FrameLayout(context);
        frameLayout.setBackgroundColor(-1);
        addView(frameLayout, LayoutHelper.createFrame(-1, 36.0f, 51, 0.0f, 0.0f, 0.0f, 0.0f));
        View view = new View(context);
        view.setBackgroundResource(C0338R.drawable.header_shadow);
        addView(view, LayoutHelper.createFrame(-1, 3.0f, 51, 0.0f, 36.0f, 0.0f, 0.0f));
        this.f430a = new ImageView(context);
        this.f430a.setScaleType(ScaleType.CENTER);
        addView(this.f430a, LayoutHelper.createFrame(42, 42.0f, 51, 0.0f, 0.0f, 0.0f, 0.0f));
        this.f430a.setOnClickListener(new PowerView(this));
        m496a();
        this.f431b = new TextView(context);
        this.f431b.setTextColor(Theme.MSG_IN_CONTACT_PHONE_TEXT_COLOR);
        if (ThemeUtil.m2490b()) {
            this.f431b.setTextColor(this.f438i);
        }
        this.f431b.setMaxLines(1);
        this.f431b.setLines(1);
        this.f431b.setSingleLine(true);
        this.f431b.setEllipsize(TruncateAt.END);
        this.f431b.setTextSize(1, 15.0f);
        this.f431b.setGravity(19);
        this.f431b.setTypeface(FontUtil.m1176a().m1161d());
        addView(this.f431b, LayoutHelper.createFrame(-1, 36.0f, 19, 35.0f, 0.0f, 36.0f, 0.0f));
    }

    private void m496a() {
        int i = -1;
        if (ThemeUtil.m2490b()) {
            int i2;
            int i3 = AdvanceTheme.f2491b;
            if (this.f435f instanceof DialogsActivity) {
                i2 = AdvanceTheme.f2508s;
                i3 = AdvanceTheme.f2507r;
                i = AdvanceTheme.f2513x;
            } else if (this.f435f instanceof ChatActivity) {
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
            if (this.f438i != i) {
                this.f438i = i;
                if (this.f431b != null) {
                    this.f431b.setTextColor(i);
                }
            }
            this.f430a.setColorFilter(i3, Mode.SRC_IN);
            if (getChildAt(4) != null) {
                ((ImageView) getChildAt(4)).setColorFilter(i3, Mode.SRC_IN);
            }
        }
    }

    private void m497a(boolean z) {
        MessageObject j = MediaController.m71a().m182j();
        View fragmentView = this.f435f.getFragmentView();
        if (!(z || fragmentView == null || (fragmentView.getParent() != null && ((View) fragmentView.getParent()).getVisibility() == 0))) {
            z = true;
        }
        boolean z2 = (j == null || j.getId() == 0) ? false : true;
        if (MoboConstants.aO) {
            setTopPadding(z2 ? (float) AndroidUtilities.dp(72.0f) : (float) AndroidUtilities.dp(36.0f));
            setTranslationY(z2 ? (float) (-AndroidUtilities.dp(36.0f)) : 0.0f);
            this.f434e = 0.0f;
            if (!z2) {
                if (this.f433d != null) {
                    this.f433d.cancel();
                    this.f433d = null;
                }
                this.f433d = new AnimatorSet();
                AnimatorSet animatorSet = this.f433d;
                r4 = new Animator[2];
                r4[0] = ObjectAnimator.ofFloat(this, "translationY", new float[]{(float) (-AndroidUtilities.dp(36.0f)), 0.0f});
                r4[1] = ObjectAnimator.ofFloat(this, "topPadding", new float[]{(float) AndroidUtilities.dp(36.0f)});
                animatorSet.playTogether(r4);
                this.f433d.setDuration(200);
                this.f433d.addListener(new PowerView(this));
                this.f433d.start();
            }
            this.f437h = true;
            setVisibility(0);
            this.f430a.setImageResource(C0338R.drawable.ic_power);
            this.f431b.setEllipsize(TruncateAt.END);
            this.f431b.setText(LocaleController.getString("MoboIsOff", C0338R.string.MoboIsOff));
            return;
        }
        this.f432c = null;
        if (this.f437h) {
            this.f437h = false;
            if (z) {
                if (getVisibility() != 8) {
                    setVisibility(8);
                }
                setTopPadding(z2 ? (float) AndroidUtilities.dp(36.0f) : 0.0f);
                return;
            }
            if (this.f433d != null) {
                this.f433d.cancel();
                this.f433d = null;
            }
            this.f433d = new AnimatorSet();
            animatorSet = this.f433d;
            r4 = new Animator[2];
            r4[0] = ObjectAnimator.ofFloat(this, "translationY", new float[]{(float) (-AndroidUtilities.dp(36.0f))});
            r4[1] = ObjectAnimator.ofFloat(this, "topPadding", new float[]{0.0f});
            animatorSet.playTogether(r4);
            this.f433d.setDuration(200);
            this.f433d.addListener(new PowerView(this));
            this.f433d.start();
        }
    }

    public void didReceivedNotification(int i, Object... objArr) {
        if (i == NotificationCenter.audioDidStarted || i == NotificationCenter.audioPlayStateChanged || i == NotificationCenter.audioDidReset || i == NotificationCenter.powerChanged) {
            m497a(false);
        }
    }

    protected boolean drawChild(Canvas canvas, View view, long j) {
        int save = canvas.save();
        if (this.f434e < 0.0f) {
            canvas.clipRect(0, (int) (-this.f434e), view.getMeasuredWidth(), AndroidUtilities.dp(39.0f));
        }
        boolean drawChild = super.drawChild(canvas, view, j);
        canvas.restoreToCount(save);
        m496a();
        return drawChild;
    }

    public float getTopPadding() {
        return this.f436g;
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.audioDidReset);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.audioPlayStateChanged);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.audioDidStarted);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.powerChanged);
        m497a(true);
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.audioDidReset);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.audioPlayStateChanged);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.audioDidStarted);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.powerChanged);
    }

    protected void onMeasure(int i, int i2) {
        super.onMeasure(i, AndroidUtilities.dp(39.0f));
    }

    public void setTopPadding(float f) {
        this.f436g = f;
        if (this.f435f != null) {
            View fragmentView = this.f435f.getFragmentView();
            if (fragmentView != null) {
                fragmentView.setPadding(0, (int) this.f436g, 0, 0);
            }
        }
    }

    public void setTranslationY(float f) {
        super.setTranslationY(f);
        this.f434e = f;
        invalidate();
    }
}
