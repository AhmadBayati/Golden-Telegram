package com.hanista.mobogram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.FileLog;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;
import com.hanista.mobogram.mobo.p008i.FontUtil;

public class TimerDrawable extends Drawable {
    private static Drawable emptyTimerDrawable;
    private static TextPaint timePaint;
    private static Drawable timerDrawable;
    private int time;
    private int timeHeight;
    private StaticLayout timeLayout;
    private float timeWidth;

    public TimerDrawable(Context context) {
        this.timeWidth = 0.0f;
        this.timeHeight = 0;
        this.time = 0;
        if (emptyTimerDrawable == null) {
            emptyTimerDrawable = context.getResources().getDrawable(C0338R.drawable.header_timer);
            timerDrawable = context.getResources().getDrawable(C0338R.drawable.header_timer2);
            timePaint = new TextPaint(1);
            timePaint.setColor(-1);
            timePaint.setTypeface(FontUtil.m1176a().m1160c());
        }
        timePaint.setTextSize((float) AndroidUtilities.dp(11.0f));
    }

    public void draw(Canvas canvas) {
        int intrinsicWidth = timerDrawable.getIntrinsicWidth();
        int intrinsicHeight = timerDrawable.getIntrinsicHeight();
        Drawable drawable = this.time == 0 ? timerDrawable : emptyTimerDrawable;
        int intrinsicWidth2 = (intrinsicWidth - drawable.getIntrinsicWidth()) / 2;
        int intrinsicHeight2 = (intrinsicHeight - drawable.getIntrinsicHeight()) / 2;
        drawable.setBounds(intrinsicWidth2, intrinsicHeight2, drawable.getIntrinsicWidth() + intrinsicWidth2, drawable.getIntrinsicHeight() + intrinsicHeight2);
        drawable.draw(canvas);
        if (this.time != 0 && this.timeLayout != null) {
            int i = 0;
            if (AndroidUtilities.density == 3.0f) {
                i = -1;
            }
            canvas.translate((float) (i + ((int) (((double) (intrinsicWidth / 2)) - Math.ceil((double) (this.timeWidth / 2.0f))))), (float) ((intrinsicHeight - this.timeHeight) / 2));
            this.timeLayout.draw(canvas);
        }
    }

    public int getIntrinsicHeight() {
        return timerDrawable.getIntrinsicHeight();
    }

    public int getIntrinsicWidth() {
        return timerDrawable.getIntrinsicWidth();
    }

    public int getOpacity() {
        return 0;
    }

    public void setAlpha(int i) {
    }

    public void setColorFilter(ColorFilter colorFilter) {
    }

    public void setTime(int i) {
        CharSequence charSequence;
        this.time = i;
        if (this.time >= 1 && this.time < 60) {
            charSequence = TtmlNode.ANONYMOUS_REGION_ID + i;
            if (charSequence.length() < 2) {
                charSequence = charSequence + "s";
            }
        } else if (this.time >= 60 && this.time < 3600) {
            charSequence = TtmlNode.ANONYMOUS_REGION_ID + (i / 60);
            if (charSequence.length() < 2) {
                charSequence = charSequence + "m";
            }
        } else if (this.time >= 3600 && this.time < 86400) {
            charSequence = TtmlNode.ANONYMOUS_REGION_ID + ((i / 60) / 60);
            if (charSequence.length() < 2) {
                charSequence = charSequence + "h";
            }
        } else if (this.time < 86400 || this.time >= 604800) {
            charSequence = TtmlNode.ANONYMOUS_REGION_ID + ((((i / 60) / 60) / 24) / 7);
            if (charSequence.length() < 2) {
                charSequence = charSequence + "w";
            } else if (charSequence.length() > 2) {
                charSequence = "c";
            }
        } else {
            charSequence = TtmlNode.ANONYMOUS_REGION_ID + (((i / 60) / 60) / 24);
            if (charSequence.length() < 2) {
                charSequence = charSequence + "d";
            }
        }
        this.timeWidth = timePaint.measureText(charSequence);
        try {
            this.timeLayout = new StaticLayout(charSequence, timePaint, (int) Math.ceil((double) this.timeWidth), Alignment.ALIGN_NORMAL, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, 0.0f, false);
            this.timeHeight = this.timeLayout.getHeight();
        } catch (Throwable e) {
            this.timeLayout = null;
            FileLog.m18e("tmessages", e);
        }
        invalidateSelf();
    }
}
