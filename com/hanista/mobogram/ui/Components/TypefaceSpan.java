package com.hanista.mobogram.ui.Components;

import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.style.MetricAffectingSpan;
import com.hanista.mobogram.tgnet.TLRPC;

public class TypefaceSpan extends MetricAffectingSpan {
    private int color;
    private Typeface mTypeface;
    private int textSize;

    public TypefaceSpan(Typeface typeface) {
        this.mTypeface = typeface;
    }

    public TypefaceSpan(Typeface typeface, int i) {
        this.mTypeface = typeface;
        this.textSize = i;
    }

    public TypefaceSpan(Typeface typeface, int i, int i2) {
        this.mTypeface = typeface;
        this.textSize = i;
        this.color = i2;
    }

    public void updateDrawState(TextPaint textPaint) {
        if (this.mTypeface != null) {
            textPaint.setTypeface(this.mTypeface);
        }
        if (this.textSize != 0) {
            textPaint.setTextSize((float) this.textSize);
        }
        if (this.color != 0) {
            textPaint.setColor(this.color);
        }
        textPaint.setFlags(textPaint.getFlags() | TLRPC.USER_FLAG_UNUSED);
    }

    public void updateMeasureState(TextPaint textPaint) {
        if (this.mTypeface != null) {
            textPaint.setTypeface(this.mTypeface);
        }
        if (this.textSize != 0) {
            textPaint.setTextSize((float) this.textSize);
        }
        textPaint.setFlags(textPaint.getFlags() | TLRPC.USER_FLAG_UNUSED);
    }
}
