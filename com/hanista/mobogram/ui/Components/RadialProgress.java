package com.hanista.mobogram.ui.Components;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.exoplayer.util.NalUnitUtil;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;
import com.hanista.mobogram.mobo.p008i.FontUtil;
import com.hanista.mobogram.mobo.p020s.AdvanceTheme;
import com.hanista.mobogram.mobo.p020s.ThemeUtil;
import com.hanista.mobogram.ui.ActionBar.Theme;

public class RadialProgress {
    private static DecelerateInterpolator decelerateInterpolator;
    private static Paint progressPaint;
    private boolean alphaForPrevious;
    private float animatedAlphaValue;
    private float animatedProgressValue;
    private float animationProgressStart;
    private RectF cicleRect;
    private Drawable currentDrawable;
    private float currentProgress;
    private long currentProgressTime;
    private boolean currentWithRound;
    private long docSize;
    private int docType;
    private boolean hideCurrentDrawable;
    private long lastUpdateTime;
    private View parent;
    private Drawable previousDrawable;
    private boolean previousWithRound;
    private int progressColor;
    private RectF progressRect;
    private TextPaint progressTextPaint;
    private float radOffset;
    private boolean showSize;

    public RadialProgress(View view) {
        this.lastUpdateTime = 0;
        this.radOffset = 0.0f;
        this.currentProgress = 0.0f;
        this.animationProgressStart = 0.0f;
        this.currentProgressTime = 0;
        this.animatedProgressValue = 0.0f;
        this.progressRect = new RectF();
        this.cicleRect = new RectF();
        this.animatedAlphaValue = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
        this.progressColor = -1;
        this.alphaForPrevious = true;
        if (decelerateInterpolator == null) {
            decelerateInterpolator = new DecelerateInterpolator();
            progressPaint = new Paint(1);
            progressPaint.setStyle(Style.STROKE);
            progressPaint.setStrokeCap(Cap.ROUND);
            progressPaint.setStrokeWidth((float) AndroidUtilities.dp(3.0f));
        }
        this.progressTextPaint = new TextPaint(1);
        this.progressTextPaint.setTypeface(FontUtil.m1176a().m1161d());
        this.progressTextPaint.setTextSize((float) AndroidUtilities.dp(10.0f));
        this.parent = view;
    }

    private void invalidateParent() {
        int dp = AndroidUtilities.dp(20.0f);
        this.parent.invalidate(((int) this.progressRect.left) - dp, ((int) this.progressRect.top) - dp, ((int) this.progressRect.right) + (dp * 2), (dp * 2) + ((int) this.progressRect.bottom));
    }

    private void updateAnimation(boolean z) {
        long currentTimeMillis = System.currentTimeMillis();
        long j = currentTimeMillis - this.lastUpdateTime;
        this.lastUpdateTime = currentTimeMillis;
        if (z) {
            if (this.animatedProgressValue != DefaultRetryPolicy.DEFAULT_BACKOFF_MULT) {
                this.radOffset += ((float) (360 * j)) / 3000.0f;
                float f = this.currentProgress - this.animationProgressStart;
                if (f > 0.0f) {
                    this.currentProgressTime += j;
                    if (this.currentProgressTime >= 300) {
                        this.animatedProgressValue = this.currentProgress;
                        this.animationProgressStart = this.currentProgress;
                        this.currentProgressTime = 0;
                    } else {
                        this.animatedProgressValue = (f * decelerateInterpolator.getInterpolation(((float) this.currentProgressTime) / BitmapDescriptorFactory.HUE_MAGENTA)) + this.animationProgressStart;
                    }
                }
                invalidateParent();
            }
            if (this.animatedProgressValue >= DefaultRetryPolicy.DEFAULT_BACKOFF_MULT && this.previousDrawable != null) {
                this.animatedAlphaValue -= ((float) j) / 200.0f;
                if (this.animatedAlphaValue <= 0.0f) {
                    this.animatedAlphaValue = 0.0f;
                    this.previousDrawable = null;
                }
                invalidateParent();
            }
        } else if (this.previousDrawable != null) {
            this.animatedAlphaValue -= ((float) j) / 200.0f;
            if (this.animatedAlphaValue <= 0.0f) {
                this.animatedAlphaValue = 0.0f;
                this.previousDrawable = null;
            }
            invalidateParent();
        }
    }

    public void draw(Canvas canvas) {
        if (this.previousDrawable != null) {
            if (this.alphaForPrevious) {
                this.previousDrawable.setAlpha((int) (this.animatedAlphaValue * 255.0f));
            } else {
                this.previousDrawable.setAlpha(NalUnitUtil.EXTENDED_SAR);
            }
            this.previousDrawable.setBounds((int) this.progressRect.left, (int) this.progressRect.top, (int) this.progressRect.right, (int) this.progressRect.bottom);
            this.previousDrawable.draw(canvas);
        }
        if (!(this.hideCurrentDrawable || this.currentDrawable == null)) {
            if (this.previousDrawable != null) {
                this.currentDrawable.setAlpha((int) ((DefaultRetryPolicy.DEFAULT_BACKOFF_MULT - this.animatedAlphaValue) * 255.0f));
            } else {
                this.currentDrawable.setAlpha(NalUnitUtil.EXTENDED_SAR);
            }
            this.currentDrawable.setBounds((int) this.progressRect.left, (int) this.progressRect.top, (int) this.progressRect.right, (int) this.progressRect.bottom);
            this.currentDrawable.draw(canvas);
        }
        if (this.currentWithRound || this.previousWithRound) {
            int dp = AndroidUtilities.dp(4.0f);
            progressPaint.setColor(this.progressColor);
            if (this.previousWithRound) {
                progressPaint.setAlpha((int) (this.animatedAlphaValue * 255.0f));
            } else {
                progressPaint.setAlpha(NalUnitUtil.EXTENDED_SAR);
            }
            this.cicleRect.set(this.progressRect.left + ((float) dp), this.progressRect.top + ((float) dp), this.progressRect.right - ((float) dp), this.progressRect.bottom - ((float) dp));
            canvas.drawArc(this.cicleRect, this.radOffset - 0.049804688f, Math.max(4.0f, 360.0f * this.animatedProgressValue), false, progressPaint);
            updateAnimation(true);
        } else {
            updateAnimation(false);
        }
        if (this.currentDrawable != null && this.progressTextPaint != null) {
            CharSequence charSequence = AndroidUtilities.formatFileSize((long) (((float) this.docSize) * this.currentProgress)) + " | " + Math.round(this.currentProgress * 100.0f) + '%';
            int ceil = (int) Math.ceil((double) this.progressTextPaint.measureText(charSequence));
            if (this.currentProgress == DefaultRetryPolicy.DEFAULT_BACKOFF_MULT || !this.currentWithRound || this.currentProgress == 0.0f || this.docSize == 0) {
                charSequence = TtmlNode.ANONYMOUS_REGION_ID;
                if (this.showSize && this.docSize > 0) {
                    charSequence = AndroidUtilities.formatFileSize(this.docSize);
                    ceil = (int) Math.ceil((double) this.progressTextPaint.measureText(charSequence));
                }
            }
            StaticLayout staticLayout = new StaticLayout(charSequence, this.progressTextPaint, ceil, Alignment.ALIGN_NORMAL, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, 0.0f, false);
            if (ThemeUtil.m2490b()) {
                TextPaint textPaint = this.progressTextPaint;
                dp = (this.progressColor == Theme.MSG_OUT_FILE_PROGRESS_SELECTED_COLOR || this.progressColor == Theme.MSG_OUT_FILE_PROGRESS_COLOR || this.progressColor == Theme.MSG_OUT_AUDIO_PROGRESS_COLOR || this.progressColor == Theme.MSG_OUT_AUDIO_SELECTED_PROGRESS_COLOR) ? AdvanceTheme.bt : AdvanceTheme.bp;
                textPaint.setColor(dp);
            } else {
                this.progressTextPaint.setColor(Theme.MSG_IN_VENUE_INFO_TEXT_COLOR);
            }
            dp = (((int) this.progressRect.left) + ((((int) (this.progressRect.right - this.progressRect.left)) - ceil) / 2)) + AndroidUtilities.dp(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            int dp2 = ((int) this.progressRect.bottom) + AndroidUtilities.dp(6.0f);
            if (!((this.docType != 1 && this.docType != 3 && this.docType != 8) || this.currentProgress == DefaultRetryPolicy.DEFAULT_BACKOFF_MULT || !this.currentWithRound || this.currentProgress == 0.0f || this.docSize == 0)) {
                Theme.timeBackgroundDrawable.setBounds(dp - AndroidUtilities.dp(2.0f), dp2 - AndroidUtilities.dp(2.0f), (dp + ceil) + AndroidUtilities.dp(2.0f), ((int) this.progressRect.bottom) + AndroidUtilities.dp(18.0f));
                Theme.timeBackgroundDrawable.draw(canvas);
                this.progressTextPaint.setColor(-1);
            }
            canvas.save();
            canvas.translate((float) dp, (float) dp2);
            staticLayout.draw(canvas);
            canvas.restore();
        }
    }

    public float getAlpha() {
        return (this.previousDrawable == null && this.currentDrawable == null) ? 0.0f : this.animatedAlphaValue;
    }

    public void setAlphaForPrevious(boolean z) {
        this.alphaForPrevious = z;
    }

    public void setBackground(Drawable drawable, boolean z, boolean z2) {
        this.lastUpdateTime = System.currentTimeMillis();
        if (!z2 || this.currentDrawable == drawable) {
            this.previousDrawable = null;
            this.previousWithRound = false;
        } else {
            this.previousDrawable = this.currentDrawable;
            this.previousWithRound = this.currentWithRound;
            this.animatedAlphaValue = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
            setProgress(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, z2);
        }
        this.currentWithRound = z;
        this.currentDrawable = drawable;
        if (z2) {
            invalidateParent();
        } else {
            this.parent.invalidate();
        }
    }

    public void setHideCurrentDrawable(boolean z) {
        this.hideCurrentDrawable = z;
    }

    public void setProgress(float f, boolean z) {
        if (!(f == DefaultRetryPolicy.DEFAULT_BACKOFF_MULT || this.animatedAlphaValue == 0.0f || this.previousDrawable == null)) {
            this.animatedAlphaValue = 0.0f;
            this.previousDrawable = null;
        }
        if (z) {
            if (this.animatedProgressValue > f) {
                this.animatedProgressValue = f;
            }
            this.animationProgressStart = this.animatedProgressValue;
        } else {
            this.animatedProgressValue = f;
            this.animationProgressStart = f;
        }
        this.currentProgress = f;
        this.currentProgressTime = 0;
        invalidateParent();
    }

    public void setProgressColor(int i) {
        this.progressColor = i;
        if (ThemeUtil.m2490b()) {
            TextPaint textPaint = this.progressTextPaint;
            int i2 = (i == Theme.MSG_OUT_FILE_PROGRESS_SELECTED_COLOR || i == Theme.MSG_OUT_FILE_PROGRESS_COLOR || i == Theme.MSG_OUT_AUDIO_PROGRESS_COLOR || i == Theme.MSG_OUT_AUDIO_SELECTED_PROGRESS_COLOR) ? AdvanceTheme.bt : AdvanceTheme.bp;
            textPaint.setColor(i2);
        }
    }

    public void setProgressRect(int i, int i2, int i3, int i4) {
        this.progressRect.set((float) i, (float) i2, (float) i3, (float) i4);
    }

    public void setShowSize(boolean z) {
        this.showSize = z;
    }

    public void setSizeAndType(long j, int i) {
        this.docSize = j;
        this.docType = i;
    }

    public boolean swapBackground(Drawable drawable) {
        if (this.currentDrawable == drawable) {
            return false;
        }
        this.currentDrawable = drawable;
        return true;
    }
}
