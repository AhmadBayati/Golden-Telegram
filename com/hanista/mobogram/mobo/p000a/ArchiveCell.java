package com.hanista.mobogram.mobo.p000a;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
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
import com.hanista.mobogram.messenger.ApplicationLoader;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.exoplayer.C0700C;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;
import com.hanista.mobogram.mobo.p008i.FontUtil;
import com.hanista.mobogram.mobo.p020s.AdvanceTheme;
import com.hanista.mobogram.mobo.p020s.ThemeUtil;
import com.hanista.mobogram.ui.ActionBar.Theme;
import com.hanista.mobogram.ui.Components.LayoutHelper;

/* renamed from: com.hanista.mobogram.mobo.a.c */
public class ArchiveCell extends FrameLayout {
    private static Paint f111h;
    private TextView f112a;
    private TextView f113b;
    private ImageView f114c;
    private boolean f115d;
    private ImageView f116e;
    private Archive f117f;
    private Rect f118g;

    public ArchiveCell(Context context) {
        int i = 3;
        super(context);
        this.f118g = new Rect();
        if (f111h == null) {
            f111h = new Paint();
            f111h.setColor(-2500135);
        }
        this.f112a = new TextView(context);
        this.f112a.setTypeface(FontUtil.m1176a().m1161d());
        this.f112a.setTextColor(Theme.STICKERS_SHEET_TITLE_TEXT_COLOR);
        this.f112a.setTextSize(1, 16.0f);
        this.f112a.setLines(1);
        this.f112a.setMaxLines(1);
        this.f112a.setSingleLine(true);
        this.f112a.setEllipsize(TruncateAt.END);
        this.f112a.setGravity(LocaleController.isRTL ? 5 : 3);
        addView(this.f112a, LayoutHelper.createFrame(-2, -2.0f, LocaleController.isRTL ? 5 : 3, LocaleController.isRTL ? 40.0f : 71.0f, 10.0f, LocaleController.isRTL ? 71.0f : 40.0f, 0.0f));
        this.f113b = new TextView(context);
        this.f113b.setTextColor(-7697782);
        this.f113b.setTextSize(1, 13.0f);
        this.f113b.setLines(1);
        this.f113b.setMaxLines(1);
        this.f113b.setSingleLine(true);
        this.f113b.setGravity(LocaleController.isRTL ? 5 : 3);
        addView(this.f113b, LayoutHelper.createFrame(-2, -2.0f, LocaleController.isRTL ? 5 : 3, LocaleController.isRTL ? 40.0f : 71.0f, 35.0f, LocaleController.isRTL ? 71.0f : 40.0f, 0.0f));
        this.f114c = new ImageView(context);
        addView(this.f114c, LayoutHelper.createFrame(40, 40.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 12.0f, 8.0f, LocaleController.isRTL ? 12.0f : 0.0f, 0.0f));
        this.f116e = new ImageView(context);
        this.f116e.setFocusable(false);
        this.f116e.setBackgroundDrawable(Theme.createBarSelectorDrawable(Theme.ACTION_BAR_CHANNEL_INTRO_SELECTOR_COLOR));
        this.f116e.setImageResource(C0338R.drawable.doc_actions_b);
        this.f116e.setScaleType(ScaleType.CENTER);
        View view = this.f116e;
        if (!LocaleController.isRTL) {
            i = 5;
        }
        addView(view, LayoutHelper.createFrame(40, 40, i | 48));
    }

    private void m232a() {
        if (ThemeUtil.m2490b()) {
            setBackgroundColor(AdvanceTheme.f2497h);
            int i = AdvanceTheme.f2498i;
            int i2 = AdvanceTheme.f2494e;
            int i3 = AdvanceTheme.f2495f;
            this.f112a.setTextColor(i2);
            this.f113b.setTextColor(i3);
            f111h.setColor(i);
            this.f114c.setBackgroundColor(AdvanceTheme.f2500k);
            Drawable drawable = ApplicationLoader.applicationContext.getResources().getDrawable(C0338R.drawable.ic_ab_archive);
            drawable.setColorFilter(AdvanceTheme.f2492c, Mode.SRC_IN);
            this.f114c.setImageDrawable(drawable);
        }
    }

    public void m233a(Archive archive, boolean z) {
        this.f115d = z;
        this.f117f = archive;
        this.f112a.setText(this.f117f.m208b());
        if (archive.m204a().longValue() == 0) {
            this.f113b.setText(LocaleController.getString("AllMessages", C0338R.string.AllMessages));
        } else if (archive.m204a().longValue() == -1) {
            this.f113b.setText(LocaleController.getString("NotCategorizedMessages", C0338R.string.NotCategorizedMessages));
        } else {
            this.f113b.setText(LocaleController.formatPluralString("messages", this.f117f.m210d().size()));
        }
        this.f112a.setAlpha(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        this.f113b.setAlpha(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        this.f114c.setAlpha(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        this.f114c.setPadding(AndroidUtilities.dp(5.0f), AndroidUtilities.dp(5.0f), AndroidUtilities.dp(5.0f), AndroidUtilities.dp(5.0f));
        this.f114c.setImageResource(C0338R.drawable.ic_ab_archive);
        this.f114c.setBackgroundColor(ThemeUtil.m2485a().m2289c());
        if (archive.m212f()) {
            this.f116e.setVisibility(8);
        } else {
            this.f116e.setVisibility(0);
        }
    }

    public Archive getArchive() {
        return this.f117f;
    }

    protected void onDraw(Canvas canvas) {
        if (this.f115d) {
            canvas.drawLine(0.0f, (float) (getHeight() - 1), (float) (getWidth() - getPaddingRight()), (float) (getHeight() - 1), f111h);
        }
    }

    protected void onMeasure(int i, int i2) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(i), C0700C.ENCODING_PCM_32BIT), MeasureSpec.makeMeasureSpec((this.f115d ? 1 : 0) + AndroidUtilities.dp(64.0f), C0700C.ENCODING_PCM_32BIT));
        m232a();
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (VERSION.SDK_INT >= 21 && getBackground() != null) {
            this.f116e.getHitRect(this.f118g);
            if (this.f118g.contains((int) motionEvent.getX(), (int) motionEvent.getY())) {
                return true;
            }
            if (motionEvent.getAction() == 0 || motionEvent.getAction() == 2) {
                getBackground().setHotspot(motionEvent.getX(), motionEvent.getY());
            }
        }
        return super.onTouchEvent(motionEvent);
    }

    public void setOnOptionsClick(OnClickListener onClickListener) {
        this.f116e.setOnClickListener(onClickListener);
    }
}
