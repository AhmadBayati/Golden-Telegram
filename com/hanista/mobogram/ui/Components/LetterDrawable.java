package com.hanista.mobogram.ui.Components;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.FileLog;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;
import com.hanista.mobogram.ui.ActionBar.Theme;

public class LetterDrawable extends Drawable {
    private static TextPaint namePaint;
    public static Paint paint;
    private StringBuilder stringBuilder;
    private float textHeight;
    private StaticLayout textLayout;
    private float textLeft;
    private float textWidth;

    static {
        paint = new Paint();
    }

    public LetterDrawable() {
        this.stringBuilder = new StringBuilder(5);
        if (namePaint == null) {
            paint.setColor(Theme.ACTION_BAR_MODE_SELECTOR_COLOR);
            namePaint = new TextPaint(1);
            namePaint.setColor(-1);
        }
        namePaint.setTextSize((float) AndroidUtilities.dp(28.0f));
    }

    public void draw(Canvas canvas) {
        Rect bounds = getBounds();
        if (bounds != null) {
            int width = bounds.width();
            canvas.save();
            canvas.drawRect((float) bounds.left, (float) bounds.top, (float) bounds.right, (float) bounds.bottom, paint);
            if (this.textLayout != null) {
                canvas.translate((((float) bounds.left) + ((((float) width) - this.textWidth) / 2.0f)) - this.textLeft, ((float) bounds.top) + ((((float) width) - this.textHeight) / 2.0f));
                this.textLayout.draw(canvas);
            }
            canvas.restore();
        }
    }

    public int getIntrinsicHeight() {
        return 0;
    }

    public int getIntrinsicWidth() {
        return 0;
    }

    public int getOpacity() {
        return -2;
    }

    public void setAlpha(int i) {
    }

    public void setColorFilter(ColorFilter colorFilter) {
    }

    public void setTitle(String str) {
        this.stringBuilder.setLength(0);
        if (str != null && str.length() > 0) {
            this.stringBuilder.append(str.substring(0, 1));
        }
        if (this.stringBuilder.length() > 0) {
            try {
                this.textLayout = new StaticLayout(this.stringBuilder.toString().toUpperCase(), namePaint, AndroidUtilities.dp(100.0f), Alignment.ALIGN_NORMAL, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, 0.0f, false);
                if (this.textLayout.getLineCount() > 0) {
                    this.textLeft = this.textLayout.getLineLeft(0);
                    this.textWidth = this.textLayout.getLineWidth(0);
                    this.textHeight = (float) this.textLayout.getLineBottom(0);
                    return;
                }
                return;
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
                return;
            }
        }
        this.textLayout = null;
    }
}
