package com.hanista.mobogram.mobo.markers;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.PointF;
import android.support.v4.internal.view.SupportMenu;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;
import com.hanista.mobogram.mobo.markers.ToolButton.PenToolButton;
import com.hanista.mobogram.ui.ActionBar.Theme;
import com.hanista.mobogram.ui.Components.VideoPlayer;

public class PenWidthEditorView extends View {
    private float f1752a;
    private float f1753b;
    private Paint f1754c;
    private Paint f1755d;
    private Paint f1756e;
    private float f1757f;
    private float f1758g;
    private PenToolButton f1759h;
    private boolean f1760i;
    private boolean f1761j;
    private boolean f1762k;

    public PenWidthEditorView(Context context) {
        this(context, null);
    }

    public PenWidthEditorView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public PenWidthEditorView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.f1760i = false;
        this.f1761j = false;
        this.f1762k = false;
        float f = getResources().getDisplayMetrics().density;
        this.f1752a = 16.0f * f;
        this.f1754c = new Paint(1);
        this.f1754c.setColor(-10066330);
        this.f1755d = new Paint();
        this.f1755d.setTextSize(this.f1752a);
        this.f1755d.setColor(SupportMenu.CATEGORY_MASK);
        this.f1755d.setTextAlign(Align.CENTER);
        this.f1756e = new Paint(1);
        this.f1756e.setStyle(Style.STROKE);
        this.f1756e.setColor(SupportMenu.CATEGORY_MASK);
        this.f1756e.setStrokeWidth(2.0f * f);
        this.f1757f = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
        this.f1758g = 20.0f;
        this.f1753b = f * 5.0f;
    }

    PointF getEndPoint() {
        Object obj = getHeight() > getWidth() ? 1 : null;
        float width = (float) ((obj != null ? getWidth() : getHeight()) / 2);
        float height = ((float) (obj != null ? getHeight() - getPaddingBottom() : getWidth() - getPaddingRight())) - 128.0f;
        float f = obj != null ? width : height;
        if (obj == null) {
            height = width;
        }
        return new PointF(f, height);
    }

    PointF getStartPoint() {
        Object obj = getHeight() > getWidth() ? 1 : null;
        float width = (float) ((obj != null ? getWidth() : getHeight()) / 2);
        float paddingTop = ((float) (obj != null ? getPaddingTop() : getPaddingLeft())) + 128.0f;
        float f = obj != null ? width : paddingTop;
        if (obj == null) {
            paddingTop = width;
        }
        return new PointF(f, paddingTop);
    }

    public void onDraw(Canvas canvas) {
        float a;
        super.onDraw(canvas);
        float f = this.f1757f * 0.5f;
        float f2 = this.f1758g * 0.5f;
        Object obj = getHeight() > getWidth() ? 1 : null;
        PointF startPoint = getStartPoint();
        PointF endPoint = getEndPoint();
        float width = (float) ((obj != null ? getWidth() : getHeight()) / 2);
        float f3 = (width - f2) * 0.5f;
        float b = Slate.m1846b(0.125f, 128.0f, f);
        float b2 = Slate.m1846b(0.125f, 128.0f, f2);
        float min = Math.min(8.0f, b) / ((float) (obj != null ? getHeight() : getWidth()));
        for (float f4 = 0.0f; f4 < DefaultRetryPolicy.DEFAULT_BACKOFF_MULT; f4 += min) {
            a = Slate.m1839a(obj != null ? startPoint.y : startPoint.x, obj != null ? endPoint.y : endPoint.x, f4);
            f = (float) (((double) width) + (((double) f3) * Math.sin(((double) (2.0f * f4)) * 3.141592653589793d)));
            float a2 = Slate.m1839a(b, b2, f4);
            f2 = obj != null ? f : a;
            if (obj == null) {
                a = f;
            }
            canvas.drawCircle(f2, a, a2, this.f1754c);
        }
        canvas.drawCircle(endPoint.x, endPoint.y, b2, this.f1754c);
        if (this.f1761j) {
            canvas.drawCircle(startPoint.x, startPoint.y, b, this.f1756e);
            if (obj != null) {
                if (obj == null) {
                    canvas.drawCircle(startPoint.x, startPoint.y, b, this.f1756e);
                }
            } else if (obj == null) {
                canvas.drawCircle(startPoint.x, startPoint.y, b, this.f1756e);
            }
            canvas.drawCircle(startPoint.x, startPoint.y, b, this.f1756e);
        } else if (this.f1762k) {
            if (obj != null) {
                if (obj == null) {
                    canvas.drawCircle(endPoint.x, endPoint.y, b2, this.f1756e);
                }
            } else if (obj == null) {
                canvas.drawCircle(endPoint.x, endPoint.y, b2, this.f1756e);
            }
            canvas.drawCircle(endPoint.x, endPoint.y, b2, this.f1756e);
        }
        String format = String.format(this.f1757f < 3.0f ? "%.1f" : "%.0f", new Object[]{Float.valueOf(this.f1757f)});
        String format2 = String.format(this.f1758g < 3.0f ? "%.1f" : "%.0f", new Object[]{Float.valueOf(this.f1758g)});
        if (b < 2.0f * this.f1752a) {
            a = (this.f1752a * 1.25f) + b;
            this.f1755d.setColor(Theme.MSG_TEXT_COLOR);
        } else {
            a = 0.0f;
            this.f1755d.setColor(-1);
        }
        f = startPoint.x - (obj != null ? 0.0f : a);
        float f5 = startPoint.y;
        if (obj == null) {
            a = 0.0f;
        }
        canvas.drawText(format, f, (f5 - a) + (this.f1752a * 0.3f), this.f1755d);
        if (b2 < 2.0f * this.f1752a) {
            a = (this.f1752a * 1.25f) + b2;
            this.f1755d.setColor(Theme.MSG_TEXT_COLOR);
        } else {
            a = 0.0f;
            this.f1755d.setColor(-1);
        }
        f = (obj != null ? 0.0f : a) + endPoint.x;
        f2 = endPoint.y;
        if (obj == null) {
            a = 0.0f;
        }
        canvas.drawText(format2, f, (f2 + a) + (this.f1752a * 0.3f), this.f1755d);
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        float height;
        float width;
        boolean z = false;
        int action = motionEvent.getAction();
        boolean z2 = getHeight() > getWidth();
        if (z2) {
            height = (float) getHeight();
        } else {
            height = (float) getWidth();
        }
        if (z2) {
            width = (float) getWidth();
        } else {
            width = (float) getHeight();
        }
        switch (action) {
            case VideoPlayer.TRACK_DEFAULT /*0*/:
                this.f1760i = true;
                invalidate();
                break;
            case VideoPlayer.TYPE_AUDIO /*1*/:
                this.f1760i = false;
                this.f1762k = false;
                this.f1761j = false;
                invalidate();
                break;
            case VideoPlayer.STATE_PREPARING /*2*/:
                PointF startPoint = getStartPoint();
                PointF endPoint = getEndPoint();
                float x = motionEvent.getX();
                float y = motionEvent.getY();
                float hypot = (float) Math.hypot((double) (x - startPoint.x), (double) (y - startPoint.y));
                height = (float) Math.hypot((double) (x - endPoint.x), (double) (y - endPoint.y));
                if (!(this.f1761j || this.f1762k)) {
                    this.f1761j = hypot < height;
                    if (!this.f1761j) {
                        z = true;
                    }
                    this.f1762k = z;
                }
                width = (((float) Math.pow((double) (Slate.m1846b(0.125f, 128.0f, (this.f1761j ? hypot : height) - this.f1753b) / 128.0f), 3.0d)) * 0.5f) * 256.0f;
                if (this.f1761j || this.f1762k) {
                    if (this.f1761j) {
                        this.f1757f = Math.min(width * 2.0f, this.f1758g);
                    } else if (this.f1762k) {
                        this.f1758g = Math.max(width * 2.0f, this.f1757f);
                    }
                    this.f1759h.m1805a(this.f1757f, this.f1758g);
                    invalidate();
                    break;
                }
        }
        return true;
    }

    public void setTool(PenToolButton penToolButton) {
        this.f1759h = penToolButton;
        this.f1757f = penToolButton.f1769e;
        this.f1758g = penToolButton.f1770f;
    }
}
