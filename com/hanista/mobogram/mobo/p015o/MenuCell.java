package com.hanista.mobogram.mobo.p015o;

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
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.exoplayer.C0700C;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;
import com.hanista.mobogram.mobo.p008i.FontUtil;
import com.hanista.mobogram.mobo.p020s.AdvanceTheme;
import com.hanista.mobogram.mobo.p020s.ThemeUtil;
import com.hanista.mobogram.ui.ActionBar.Theme;
import com.hanista.mobogram.ui.Components.LayoutHelper;

/* renamed from: com.hanista.mobogram.mobo.o.a */
public class MenuCell extends FrameLayout {
    private static Paint f1980h;
    private TextView f1981a;
    private TextView f1982b;
    private ImageView f1983c;
    private boolean f1984d;
    private ImageView f1985e;
    private MenuData f1986f;
    private Rect f1987g;

    public MenuCell(Context context) {
        int i = 3;
        super(context);
        this.f1987g = new Rect();
        if (f1980h == null) {
            f1980h = new Paint();
            f1980h.setColor(-2500135);
        }
        this.f1981a = new TextView(context);
        this.f1981a.setTypeface(FontUtil.m1176a().m1161d());
        this.f1981a.setTextColor(Theme.STICKERS_SHEET_TITLE_TEXT_COLOR);
        this.f1981a.setTextSize(1, 16.0f);
        this.f1981a.setLines(1);
        this.f1981a.setMaxLines(1);
        this.f1981a.setSingleLine(true);
        this.f1981a.setEllipsize(TruncateAt.END);
        this.f1981a.setGravity(LocaleController.isRTL ? 5 : 3);
        addView(this.f1981a, LayoutHelper.createFrame(-2, -2.0f, LocaleController.isRTL ? 5 : 3, LocaleController.isRTL ? 40.0f : 71.0f, 22.0f, LocaleController.isRTL ? 71.0f : 40.0f, 0.0f));
        this.f1982b = new TextView(context);
        this.f1982b.setTextColor(-7697782);
        this.f1982b.setTextSize(1, 13.0f);
        this.f1982b.setLines(1);
        this.f1982b.setMaxLines(1);
        this.f1982b.setSingleLine(true);
        this.f1982b.setVisibility(8);
        this.f1982b.setGravity(LocaleController.isRTL ? 5 : 3);
        addView(this.f1982b, LayoutHelper.createFrame(-2, -2.0f, LocaleController.isRTL ? 5 : 3, LocaleController.isRTL ? 40.0f : 71.0f, 35.0f, LocaleController.isRTL ? 71.0f : 40.0f, 0.0f));
        this.f1983c = new ImageView(context);
        addView(this.f1983c, LayoutHelper.createFrame(40, 40.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 12.0f, 8.0f, LocaleController.isRTL ? 12.0f : 0.0f, 0.0f));
        this.f1985e = new ImageView(context);
        this.f1985e.setFocusable(false);
        this.f1985e.setBackgroundDrawable(Theme.createBarSelectorDrawable(Theme.ACTION_BAR_CHANNEL_INTRO_SELECTOR_COLOR));
        this.f1985e.setImageResource(C0338R.drawable.doc_actions_b);
        this.f1985e.setScaleType(ScaleType.CENTER);
        this.f1985e.setVisibility(8);
        View view = this.f1985e;
        if (!LocaleController.isRTL) {
            i = 5;
        }
        addView(view, LayoutHelper.createFrame(40, 40, i | 48));
    }

    private void m1968a() {
        if (ThemeUtil.m2490b()) {
            setBackgroundColor(AdvanceTheme.f2497h);
            int i = AdvanceTheme.f2498i;
            int i2 = AdvanceTheme.f2494e;
            int i3 = AdvanceTheme.f2495f;
            this.f1981a.setTextColor(i2);
            this.f1982b.setTextColor(i3);
            f1980h.setColor(i);
        }
    }

    public void m1969a(MenuData menuData, boolean z) {
        this.f1984d = z;
        this.f1986f = menuData;
        this.f1981a.setText(this.f1986f.m1976e());
        if (this.f1986f.m1975d()) {
            this.f1981a.setAlpha(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            this.f1982b.setAlpha(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            this.f1983c.setAlpha(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        } else {
            this.f1981a.setAlpha(0.5f);
            this.f1982b.setAlpha(0.5f);
            this.f1983c.setAlpha(0.5f);
        }
        if (this.f1986f.m1974c() != 0) {
            int c = ThemeUtil.m2490b() ? AdvanceTheme.f2483T : ThemeUtil.m2485a().m2289c();
            Drawable drawable = getContext().getResources().getDrawable(this.f1986f.m1974c());
            drawable.setColorFilter(c, Mode.SRC_IN);
            this.f1983c.setImageDrawable(drawable);
            return;
        }
        this.f1983c.setImageDrawable(null);
    }

    public MenuData getMenuData() {
        return this.f1986f;
    }

    protected void onDraw(Canvas canvas) {
        if (this.f1984d) {
            canvas.drawLine(0.0f, (float) (getHeight() - 1), (float) (getWidth() - getPaddingRight()), (float) (getHeight() - 1), f1980h);
        }
    }

    protected void onMeasure(int i, int i2) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(i), C0700C.ENCODING_PCM_32BIT), MeasureSpec.makeMeasureSpec((this.f1984d ? 1 : 0) + AndroidUtilities.dp(64.0f), C0700C.ENCODING_PCM_32BIT));
        m1968a();
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (VERSION.SDK_INT >= 21 && getBackground() != null) {
            this.f1985e.getHitRect(this.f1987g);
            if (this.f1987g.contains((int) motionEvent.getX(), (int) motionEvent.getY())) {
                return true;
            }
            if (motionEvent.getAction() == 0 || motionEvent.getAction() == 2) {
                getBackground().setHotspot(motionEvent.getX(), motionEvent.getY());
            }
        }
        return super.onTouchEvent(motionEvent);
    }

    public void setOnOptionsClick(OnClickListener onClickListener) {
        this.f1985e.setOnClickListener(onClickListener);
    }
}
