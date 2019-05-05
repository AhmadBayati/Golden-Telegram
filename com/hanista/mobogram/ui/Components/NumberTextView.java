package com.hanista.mobogram.ui.Components;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.view.View;
import com.google.android.gms.vision.face.Face;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.AnimatorListenerAdapterProxy;
import com.hanista.mobogram.messenger.exoplayer.util.NalUnitUtil;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;
import java.util.ArrayList;
import java.util.Locale;

public class NumberTextView extends View {
    private ObjectAnimator animator;
    private int currentNumber;
    private ArrayList<StaticLayout> letters;
    private ArrayList<StaticLayout> oldLetters;
    private float progress;
    private TextPaint textPaint;

    /* renamed from: com.hanista.mobogram.ui.Components.NumberTextView.1 */
    class C13551 extends AnimatorListenerAdapterProxy {
        C13551() {
        }

        public void onAnimationEnd(Animator animator) {
            NumberTextView.this.animator = null;
            NumberTextView.this.oldLetters.clear();
        }
    }

    public NumberTextView(Context context) {
        super(context);
        this.letters = new ArrayList();
        this.oldLetters = new ArrayList();
        this.textPaint = new TextPaint(1);
        this.progress = 0.0f;
        this.currentNumber = 1;
    }

    public float getProgress() {
        return this.progress;
    }

    protected void onDraw(Canvas canvas) {
        if (!this.letters.isEmpty()) {
            float height = (float) ((StaticLayout) this.letters.get(0)).getHeight();
            canvas.save();
            canvas.translate((float) getPaddingLeft(), (((float) getMeasuredHeight()) - height) / 2.0f);
            int max = Math.max(this.letters.size(), this.oldLetters.size());
            int i = 0;
            while (i < max) {
                canvas.save();
                StaticLayout staticLayout = i < this.oldLetters.size() ? (StaticLayout) this.oldLetters.get(i) : null;
                StaticLayout staticLayout2 = i < this.letters.size() ? (StaticLayout) this.letters.get(i) : null;
                if (this.progress > 0.0f) {
                    if (staticLayout != null) {
                        this.textPaint.setAlpha((int) (this.progress * 255.0f));
                        canvas.save();
                        canvas.translate(0.0f, (this.progress - DefaultRetryPolicy.DEFAULT_BACKOFF_MULT) * height);
                        staticLayout.draw(canvas);
                        canvas.restore();
                        if (staticLayout2 != null) {
                            this.textPaint.setAlpha((int) ((DefaultRetryPolicy.DEFAULT_BACKOFF_MULT - this.progress) * 255.0f));
                            canvas.translate(0.0f, this.progress * height);
                        }
                    } else {
                        this.textPaint.setAlpha(NalUnitUtil.EXTENDED_SAR);
                    }
                } else if (this.progress < 0.0f) {
                    if (staticLayout != null) {
                        this.textPaint.setAlpha((int) ((-this.progress) * 255.0f));
                        canvas.save();
                        canvas.translate(0.0f, (this.progress + DefaultRetryPolicy.DEFAULT_BACKOFF_MULT) * height);
                        staticLayout.draw(canvas);
                        canvas.restore();
                    }
                    if (staticLayout2 != null) {
                        if (i == max - 1 || staticLayout != null) {
                            this.textPaint.setAlpha((int) ((this.progress + DefaultRetryPolicy.DEFAULT_BACKOFF_MULT) * 255.0f));
                            canvas.translate(0.0f, this.progress * height);
                        } else {
                            this.textPaint.setAlpha(NalUnitUtil.EXTENDED_SAR);
                        }
                    }
                } else if (staticLayout2 != null) {
                    this.textPaint.setAlpha(NalUnitUtil.EXTENDED_SAR);
                }
                if (staticLayout2 != null) {
                    staticLayout2.draw(canvas);
                }
                canvas.restore();
                canvas.translate(staticLayout2 != null ? staticLayout2.getLineWidth(0) : staticLayout.getLineWidth(0) + ((float) AndroidUtilities.dp(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)), 0.0f);
                i++;
            }
            canvas.restore();
        }
    }

    public void setNumber(int i, boolean z) {
        if (this.currentNumber != i || !z) {
            if (this.animator != null) {
                this.animator.cancel();
                this.animator = null;
            }
            this.oldLetters.clear();
            this.oldLetters.addAll(this.letters);
            this.letters.clear();
            String format = String.format(Locale.US, "%d", new Object[]{Integer.valueOf(this.currentNumber)});
            String format2 = String.format(Locale.US, "%d", new Object[]{Integer.valueOf(i)});
            Object obj = i > this.currentNumber ? 1 : null;
            this.currentNumber = i;
            this.progress = 0.0f;
            int i2 = 0;
            while (i2 < format2.length()) {
                CharSequence substring = format2.substring(i2, i2 + 1);
                String substring2 = (this.oldLetters.isEmpty() || i2 >= format.length()) ? null : format.substring(i2, i2 + 1);
                if (substring2 == null || !substring2.equals(substring)) {
                    this.letters.add(new StaticLayout(substring, this.textPaint, (int) Math.ceil((double) this.textPaint.measureText(substring)), Alignment.ALIGN_NORMAL, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, 0.0f, false));
                } else {
                    this.letters.add(this.oldLetters.get(i2));
                    this.oldLetters.set(i2, null);
                }
                i2++;
            }
            if (z && !this.oldLetters.isEmpty()) {
                String str = NotificationCompatApi24.CATEGORY_PROGRESS;
                float[] fArr = new float[2];
                fArr[0] = obj != null ? Face.UNCOMPUTED_PROBABILITY : DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
                fArr[1] = 0.0f;
                this.animator = ObjectAnimator.ofFloat(this, str, fArr);
                this.animator.setDuration(150);
                this.animator.addListener(new C13551());
                this.animator.start();
            }
            invalidate();
        }
    }

    public void setProgress(float f) {
        if (this.progress != f) {
            this.progress = f;
            invalidate();
        }
    }

    public void setTextColor(int i) {
        this.textPaint.setColor(i);
        invalidate();
    }

    public void setTextSize(int i) {
        this.textPaint.setTextSize((float) AndroidUtilities.dp((float) i));
        this.oldLetters.clear();
        this.letters.clear();
        setNumber(this.currentNumber, false);
    }

    public void setTypeface(Typeface typeface) {
        this.textPaint.setTypeface(typeface);
        this.oldLetters.clear();
        this.letters.clear();
        setNumber(this.currentNumber, false);
    }
}
