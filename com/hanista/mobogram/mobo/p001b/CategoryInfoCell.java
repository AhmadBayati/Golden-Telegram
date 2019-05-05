package com.hanista.mobogram.mobo.p001b;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.view.MotionEvent;
import android.view.View.MeasureSpec;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.Emoji;
import com.hanista.mobogram.messenger.FileLog;
import com.hanista.mobogram.messenger.ImageReceiver;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;
import com.hanista.mobogram.mobo.p008i.FontUtil;
import com.hanista.mobogram.mobo.p020s.AdvanceTheme;
import com.hanista.mobogram.mobo.p020s.ThemeUtil;
import com.hanista.mobogram.ui.ActionBar.Theme;
import com.hanista.mobogram.ui.Cells.BaseCell;
import com.hanista.mobogram.ui.Components.AvatarDrawable;

/* renamed from: com.hanista.mobogram.mobo.b.e */
public class CategoryInfoCell extends BaseCell {
    private static TextPaint f189b;
    private static TextPaint f190c;
    private static TextPaint f191d;
    private static TextPaint f192e;
    private static TextPaint f193f;
    private static Drawable f194g;
    private static Drawable f195h;
    private static Paint f196i;
    private static Paint f197j;
    private int f198A;
    private int f199B;
    private int f200C;
    private StaticLayout f201D;
    private int f202E;
    private Category f203F;
    public boolean f204a;
    private int f205k;
    private boolean f206l;
    private ImageReceiver f207m;
    private AvatarDrawable f208n;
    private CharSequence f209o;
    private int f210p;
    private StaticLayout f211q;
    private int f212r;
    private int f213s;
    private int f214t;
    private StaticLayout f215u;
    private int f216v;
    private int f217w;
    private StaticLayout f218x;
    private boolean f219y;
    private boolean f220z;

    public CategoryInfoCell(Context context) {
        super(context);
        this.f209o = null;
        this.f204a = true;
        this.f213s = AndroidUtilities.dp(13.0f);
        this.f216v = AndroidUtilities.dp(40.0f);
        this.f198A = AndroidUtilities.dp(39.0f);
        this.f202E = AndroidUtilities.dp(10.0f);
        if (f189b == null) {
            f189b = new TextPaint(1);
            f189b.setTextSize((float) AndroidUtilities.dp(17.0f));
            f189b.setColor(Theme.STICKERS_SHEET_TITLE_TEXT_COLOR);
            f189b.setTypeface(FontUtil.m1176a().m1160c());
            f190c = new TextPaint(1);
            f190c.setTextSize((float) AndroidUtilities.dp(16.0f));
            f190c.setTypeface(FontUtil.m1176a().m1161d());
            f190c.setColor(Theme.DIALOGS_MESSAGE_TEXT_COLOR);
            f190c.linkColor = Theme.DIALOGS_MESSAGE_TEXT_COLOR;
            f196i = new Paint();
            f196i.setColor(-2302756);
            f197j = new Paint();
            f197j.setColor(251658240);
            f191d = new TextPaint(1);
            f191d.setTypeface(FontUtil.m1176a().m1161d());
            f191d.setTextSize((float) AndroidUtilities.dp(16.0f));
            f191d.setColor(ThemeUtil.m2485a().m2289c());
            f193f = new TextPaint(1);
            f193f.setTextSize((float) AndroidUtilities.dp(13.0f));
            f193f.setColor(-1);
            f193f.setTypeface(FontUtil.m1176a().m1160c());
            f192e = new TextPaint(1);
            f192e.setTextSize((float) AndroidUtilities.dp(13.0f));
            f192e.setColor(-1);
            f192e.setTypeface(FontUtil.m1176a().m1160c());
            f194g = getResources().getDrawable(C0338R.drawable.dialogs_badge);
            f195h = getResources().getDrawable(C0338R.drawable.dialogs_badge2);
        }
        setBackgroundResource(ThemeUtil.m2485a().m2293g());
        this.f207m = new ImageReceiver(this);
        this.f207m.setRoundRadius(AndroidUtilities.dp(26.0f));
        this.f208n = new AvatarDrawable();
    }

    private void m321c() {
        if (ThemeUtil.m2490b()) {
            int i = AdvanceTheme.f2491b;
            int c = AdvanceTheme.m2286c(AdvanceTheme.f2489Z, Theme.STICKERS_SHEET_TITLE_TEXT_COLOR);
            f189b.setTextSize((float) AndroidUtilities.dp((float) AdvanceTheme.ao));
            f189b.setColor(c);
            f190c.setTextSize((float) AndroidUtilities.dp((float) AdvanceTheme.ar));
            f190c.setColor(AdvanceTheme.m2286c(AdvanceTheme.ah, Theme.DIALOGS_MESSAGE_TEXT_COLOR));
            f191d.setTextSize((float) AndroidUtilities.dp((float) AdvanceTheme.ar));
            f191d.setColor(AdvanceTheme.m2286c(AdvanceTheme.ah, i));
            f193f.setTextSize((float) AndroidUtilities.dp((float) AdvanceTheme.aa));
            f193f.setColor(AdvanceTheme.ab);
            f192e.setTextSize((float) AndroidUtilities.dp((float) AdvanceTheme.aa));
            f192e.setColor(AdvanceTheme.ab);
            f194g.setColorFilter(AdvanceTheme.m2286c(AdvanceTheme.ac, i), Mode.SRC_IN);
            f195h.setColorFilter(AdvanceTheme.ai, Mode.SRC_IN);
            f196i.setColor(AdvanceTheme.as);
        }
    }

    public void m322a() {
        TextPaint textPaint;
        CharSequence charSequence;
        String str;
        String format;
        String format2;
        int measuredWidth;
        String str2 = TtmlNode.ANONYMOUS_REGION_ID;
        str2 = TtmlNode.ANONYMOUS_REGION_ID;
        TextPaint textPaint2 = f189b;
        TextPaint textPaint3 = f190c;
        if (LocaleController.isRTL) {
            this.f210p = AndroidUtilities.dp(14.0f);
        } else {
            this.f210p = AndroidUtilities.dp((float) AndroidUtilities.leftBaseline);
        }
        if (null != null) {
            this.f209o = null;
            textPaint = f191d;
            charSequence = null;
        } else {
            this.f209o = null;
            str = this.f203F.m284d().size() + " " + (this.f203F.m284d().size() < 2 ? LocaleController.getString("Chat", C0338R.string.Chat) : LocaleController.getString("Chats", C0338R.string.Chats));
            textPaint = f191d;
            Object obj = str;
        }
        if (this.f205k != 0) {
            this.f219y = true;
            format = String.format("%d", new Object[]{Integer.valueOf(this.f205k)});
        } else {
            this.f219y = false;
            format = null;
        }
        if (this.f203F.m285e() != 0) {
            this.f220z = true;
            format2 = String.format("%d", new Object[]{Integer.valueOf(this.f203F.m285e())});
        } else {
            this.f220z = false;
            format2 = null;
        }
        str = this.f203F.m281b();
        int max = Math.max(AndroidUtilities.dp(12.0f), !LocaleController.isRTL ? (getMeasuredWidth() - this.f210p) - AndroidUtilities.dp(14.0f) : (getMeasuredWidth() - this.f210p) - AndroidUtilities.dp((float) AndroidUtilities.leftBaseline));
        try {
            this.f211q = new StaticLayout(TextUtils.ellipsize(str.replace("\n", " "), textPaint2, (float) (max - AndroidUtilities.dp(12.0f)), TruncateAt.END), textPaint2, max, Alignment.ALIGN_NORMAL, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, 0.0f, false);
        } catch (Throwable e) {
            FileLog.m18e("tmessages", e);
        }
        int measuredWidth2 = getMeasuredWidth() - AndroidUtilities.dp((float) (AndroidUtilities.leftBaseline + 16));
        if (LocaleController.isRTL) {
            this.f217w = AndroidUtilities.dp(16.0f);
            measuredWidth = getMeasuredWidth() - AndroidUtilities.dp(AndroidUtilities.isTablet() ? 65.0f : 61.0f);
        } else {
            this.f217w = AndroidUtilities.dp((float) AndroidUtilities.leftBaseline);
            measuredWidth = AndroidUtilities.dp(AndroidUtilities.isTablet() ? 13.0f : 9.0f);
        }
        this.f207m.setImageCoords(measuredWidth, this.f202E, AndroidUtilities.dp(52.0f), AndroidUtilities.dp(52.0f));
        if (format != null) {
            this.f200C = Math.max(AndroidUtilities.dp(12.0f), (int) Math.ceil((double) f192e.measureText(format)));
            this.f201D = new StaticLayout(format, f192e, this.f200C, Alignment.ALIGN_CENTER, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, 0.0f, false);
            int dp = AndroidUtilities.dp(18.0f) + this.f200C;
            measuredWidth = measuredWidth2 - dp;
            if (LocaleController.isRTL) {
                this.f199B = AndroidUtilities.dp(19.0f);
                this.f217w += dp;
            } else {
                this.f199B = (getMeasuredWidth() - this.f200C) - AndroidUtilities.dp(19.0f);
            }
            this.f219y = true;
            measuredWidth2 = measuredWidth;
        } else {
            this.f219y = false;
        }
        if (format2 != null) {
            this.f214t = Math.max(AndroidUtilities.dp(12.0f), (int) Math.ceil((double) f193f.measureText(format2)));
            this.f215u = new StaticLayout(format2, f193f, this.f214t, Alignment.ALIGN_CENTER, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, 0.0f, false);
            if (LocaleController.isRTL) {
                this.f212r = AndroidUtilities.dp(19.0f);
            } else {
                this.f212r = (getMeasuredWidth() - this.f214t) - AndroidUtilities.dp(19.0f);
            }
            this.f220z = true;
        } else {
            this.f220z = false;
        }
        if (charSequence == null) {
            charSequence = TtmlNode.ANONYMOUS_REGION_ID;
        }
        str2 = charSequence.toString();
        if (str2.length() > 150) {
            str2 = str2.substring(0, 150);
        }
        charSequence = Emoji.replaceEmoji(str2.replace("\n", " "), f190c.getFontMetricsInt(), AndroidUtilities.dp(17.0f), false);
        int max2 = Math.max(AndroidUtilities.dp(12.0f), measuredWidth2);
        try {
            this.f218x = new StaticLayout(TextUtils.ellipsize(charSequence, textPaint, (float) (max2 - AndroidUtilities.dp(12.0f)), TruncateAt.END), textPaint, max2, Alignment.ALIGN_NORMAL, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, 0.0f, false);
        } catch (Throwable e2) {
            FileLog.m18e("tmessages", e2);
        }
        double ceil;
        if (LocaleController.isRTL) {
            if (this.f211q != null && this.f211q.getLineCount() > 0) {
                float lineLeft = this.f211q.getLineLeft(0);
                double ceil2 = Math.ceil((double) this.f211q.getLineWidth(0));
                if (lineLeft == 0.0f && ceil2 < ((double) max)) {
                    this.f210p = (int) (((double) this.f210p) + (((double) max) - ceil2));
                }
            }
            if (this.f218x != null && this.f218x.getLineCount() > 0 && this.f218x.getLineLeft(0) == 0.0f) {
                ceil = Math.ceil((double) this.f218x.getLineWidth(0));
                if (ceil < ((double) max2)) {
                    this.f217w = (int) ((((double) max2) - ceil) + ((double) this.f217w));
                    return;
                }
                return;
            }
            return;
        }
        if (this.f211q != null && this.f211q.getLineCount() > 0 && this.f211q.getLineRight(0) == ((float) max)) {
            ceil = Math.ceil((double) this.f211q.getLineWidth(0));
            if (ceil < ((double) max)) {
                this.f210p = (int) (((double) this.f210p) - (((double) max) - ceil));
            }
        }
        if (this.f218x != null && this.f218x.getLineCount() > 0 && this.f218x.getLineRight(0) == ((float) max2)) {
            ceil = Math.ceil((double) this.f218x.getLineWidth(0));
            if (ceil < ((double) max2)) {
                this.f217w = (int) (((double) this.f217w) - (((double) max2) - ceil));
            }
        }
    }

    public void m323b() {
        m321c();
        Drawable drawable;
        if (this.f203F.m276a() == null) {
            if (ThemeUtil.m2490b()) {
                drawable = getContext().getResources().getDrawable(C0338R.drawable.ic_close_category_lrg);
                drawable.setColorFilter(AdvanceTheme.f2492c, Mode.SRC_IN);
                this.f207m.setImageBitmap(drawable);
            } else {
                this.f207m.setImageBitmap(getResources().getDrawable(C0338R.drawable.ic_close_category_lrg));
            }
        } else if (ThemeUtil.m2490b()) {
            drawable = getContext().getResources().getDrawable(C0338R.drawable.ic_category_lrg);
            drawable.setColorFilter(AdvanceTheme.f2492c, Mode.SRC_IN);
            this.f207m.setImageBitmap(drawable);
        } else {
            this.f207m.setImageBitmap(getResources().getDrawable(C0338R.drawable.ic_category_lrg));
        }
        this.f205k = this.f203F.m286f();
        if (getMeasuredWidth() == 0 && getMeasuredHeight() == 0) {
            requestLayout();
        } else {
            m322a();
        }
        invalidate();
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.f207m.onAttachedToWindow();
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.f207m.onDetachedFromWindow();
    }

    protected void onDraw(Canvas canvas) {
        if (this.f203F != null) {
            if (this.f211q != null) {
                canvas.save();
                canvas.translate((float) this.f210p, (float) AndroidUtilities.dp(13.0f));
                this.f211q.draw(canvas);
                canvas.restore();
            }
            canvas.save();
            if (this.f220z) {
                setDrawableBounds(f195h, this.f212r - AndroidUtilities.dp(5.5f), this.f213s, AndroidUtilities.dp(11.0f) + this.f214t, f195h.getIntrinsicHeight());
                f195h.draw(canvas);
                canvas.save();
                canvas.translate((float) this.f212r, (float) (this.f213s + AndroidUtilities.dp(4.0f)));
                this.f215u.draw(canvas);
                canvas.restore();
            }
            if (this.f218x != null) {
                canvas.save();
                canvas.translate((float) this.f217w, (float) this.f216v);
                this.f218x.draw(canvas);
                canvas.restore();
            }
            if (this.f219y) {
                if (this.f206l) {
                    setDrawableBounds(f195h, this.f199B - AndroidUtilities.dp(5.5f), this.f198A, AndroidUtilities.dp(11.0f) + this.f200C, f194g.getIntrinsicHeight());
                    f195h.draw(canvas);
                } else {
                    setDrawableBounds(f194g, this.f199B - AndroidUtilities.dp(5.5f), this.f198A, AndroidUtilities.dp(11.0f) + this.f200C, f194g.getIntrinsicHeight());
                    f194g.draw(canvas);
                }
                canvas.save();
                canvas.translate((float) this.f199B, (float) (this.f198A + AndroidUtilities.dp(4.0f)));
                this.f201D.draw(canvas);
                canvas.restore();
            }
            if (this.f204a) {
                if (LocaleController.isRTL) {
                    canvas.drawLine(0.0f, (float) (getMeasuredHeight() - 1), (float) (getMeasuredWidth() - AndroidUtilities.dp((float) AndroidUtilities.leftBaseline)), (float) (getMeasuredHeight() - 1), f196i);
                } else {
                    canvas.drawLine((float) AndroidUtilities.dp((float) AndroidUtilities.leftBaseline), (float) (getMeasuredHeight() - 1), (float) getMeasuredWidth(), (float) (getMeasuredHeight() - 1), f196i);
                }
            }
            Paint paint = new Paint();
            paint.setColor(ThemeUtil.m2485a().m2289c());
            if (ThemeUtil.m2490b()) {
                paint.setColor(AdvanceTheme.f2500k);
            }
            int i = this.f203F.m276a() == null ? 30 : 20;
            canvas.drawRoundRect(new RectF((float) this.f207m.getImageX(), (float) this.f207m.getImageY(), (float) (this.f207m.getImageX() + this.f207m.getImageWidth()), (float) (this.f207m.getImageY() + this.f207m.getImageHeight())), (float) i, (float) i, paint);
            this.f207m.draw(canvas);
        }
    }

    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        if (this.f203F == null) {
            super.onLayout(z, i, i2, i3, i4);
        } else if (z) {
            m322a();
        }
    }

    protected void onMeasure(int i, int i2) {
        setMeasuredDimension(MeasureSpec.getSize(i), (this.f204a ? 1 : 0) + AndroidUtilities.dp(72.0f));
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (VERSION.SDK_INT >= 21 && getBackground() != null && (motionEvent.getAction() == 0 || motionEvent.getAction() == 2)) {
            getBackground().setHotspot(motionEvent.getX(), motionEvent.getY());
        }
        return super.onTouchEvent(motionEvent);
    }

    public void setCategory(Category category) {
        this.f203F = category;
        m323b();
    }
}
