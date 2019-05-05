package com.hanista.mobogram.mobo.p019r;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build.VERSION;
import android.text.TextUtils.TruncateAt;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.exoplayer.C0700C;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;
import com.hanista.mobogram.mobo.p008i.FontUtil;
import com.hanista.mobogram.mobo.p020s.AdvanceTheme;
import com.hanista.mobogram.mobo.p020s.ThemeUtil;
import com.hanista.mobogram.ui.ActionBar.Theme;
import com.hanista.mobogram.ui.Components.LayoutHelper;

/* renamed from: com.hanista.mobogram.mobo.r.a */
public class TabCell extends FrameLayout {
    private static Paint f2416h;
    private TextView f2417a;
    private TextView f2418b;
    private ImageView f2419c;
    private boolean f2420d;
    private ImageView f2421e;
    private TabData f2422f;
    private Rect f2423g;

    public TabCell(Context context) {
        int i = 3;
        super(context);
        this.f2423g = new Rect();
        if (f2416h == null) {
            f2416h = new Paint();
            f2416h.setColor(-2500135);
        }
        this.f2417a = new TextView(context);
        this.f2417a.setTypeface(FontUtil.m1176a().m1161d());
        this.f2417a.setTextColor(Theme.STICKERS_SHEET_TITLE_TEXT_COLOR);
        this.f2417a.setTextSize(1, 16.0f);
        this.f2417a.setLines(1);
        this.f2417a.setMaxLines(1);
        this.f2417a.setSingleLine(true);
        this.f2417a.setEllipsize(TruncateAt.END);
        this.f2417a.setGravity(LocaleController.isRTL ? 5 : 3);
        addView(this.f2417a, LayoutHelper.createFrame(-2, -2.0f, LocaleController.isRTL ? 5 : 3, LocaleController.isRTL ? 40.0f : 71.0f, 22.0f, LocaleController.isRTL ? 71.0f : 40.0f, 0.0f));
        this.f2418b = new TextView(context);
        this.f2418b.setTextColor(-7697782);
        this.f2418b.setTextSize(1, 13.0f);
        this.f2418b.setLines(1);
        this.f2418b.setMaxLines(1);
        this.f2418b.setSingleLine(true);
        this.f2418b.setVisibility(8);
        this.f2418b.setGravity(LocaleController.isRTL ? 5 : 3);
        addView(this.f2418b, LayoutHelper.createFrame(-2, -2.0f, LocaleController.isRTL ? 5 : 3, LocaleController.isRTL ? 40.0f : 71.0f, 35.0f, LocaleController.isRTL ? 71.0f : 40.0f, 0.0f));
        this.f2419c = new ImageView(context);
        addView(this.f2419c, LayoutHelper.createFrame(40, 40.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 12.0f, 8.0f, LocaleController.isRTL ? 12.0f : 0.0f, 0.0f));
        this.f2421e = new ImageView(context);
        this.f2421e.setFocusable(false);
        this.f2421e.setBackgroundDrawable(Theme.createBarSelectorDrawable(Theme.ACTION_BAR_CHANNEL_INTRO_SELECTOR_COLOR));
        this.f2421e.setImageResource(C0338R.drawable.doc_actions_b);
        this.f2421e.setScaleType(ScaleType.CENTER);
        this.f2421e.setVisibility(8);
        View view = this.f2421e;
        if (!LocaleController.isRTL) {
            i = 5;
        }
        addView(view, LayoutHelper.createFrame(40, 40, i | 48));
    }

    private void m2223a() {
        if (ThemeUtil.m2490b()) {
            setBackgroundColor(AdvanceTheme.f2497h);
            int i = AdvanceTheme.f2498i;
            int i2 = AdvanceTheme.f2494e;
            int i3 = AdvanceTheme.f2495f;
            this.f2417a.setTextColor(i2);
            this.f2418b.setTextColor(i3);
            f2416h.setColor(i);
        }
    }

    public void m2224a(TabData tabData, boolean z) {
        this.f2420d = z;
        this.f2422f = tabData;
        this.f2417a.setText(this.f2422f.m2232d());
        if (this.f2422f.m2231c()) {
            this.f2417a.setAlpha(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            this.f2418b.setAlpha(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            this.f2419c.setAlpha(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        } else {
            this.f2417a.setAlpha(0.5f);
            this.f2418b.setAlpha(0.5f);
            this.f2419c.setAlpha(0.5f);
        }
        this.f2419c.setImageResource(this.f2422f.m2237g());
    }

    public TabData getTabData() {
        return this.f2422f;
    }

    protected void onDraw(Canvas canvas) {
        if (this.f2420d) {
            canvas.drawLine(0.0f, (float) (getHeight() - 1), (float) (getWidth() - getPaddingRight()), (float) (getHeight() - 1), f2416h);
        }
    }

    protected void onMeasure(int i, int i2) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(i), C0700C.ENCODING_PCM_32BIT), MeasureSpec.makeMeasureSpec((this.f2420d ? 1 : 0) + AndroidUtilities.dp(64.0f), C0700C.ENCODING_PCM_32BIT));
        m2223a();
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (VERSION.SDK_INT >= 21 && getBackground() != null) {
            this.f2421e.getHitRect(this.f2423g);
            if (this.f2423g.contains((int) motionEvent.getX(), (int) motionEvent.getY())) {
                return true;
            }
            if (motionEvent.getAction() == 0 || motionEvent.getAction() == 2) {
                getBackground().setHotspot(motionEvent.getX(), motionEvent.getY());
            }
        }
        return super.onTouchEvent(motionEvent);
    }

    public void setOnOptionsClick(OnClickListener onClickListener) {
        this.f2421e.setOnClickListener(onClickListener);
    }
}
