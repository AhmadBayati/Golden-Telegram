package com.hanista.mobogram.mobo.markers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import com.google.android.gms.vision.face.Face;

/* renamed from: com.hanista.mobogram.mobo.markers.j */
public class ZoomTouchView extends View {
    private static final float[] f1897i;
    private Slate f1898a;
    private float[] f1899b;
    private float[] f1900c;
    private double f1901d;
    private long f1902e;
    private float[] f1903f;
    private Matrix f1904g;
    private Paint f1905h;

    static {
        f1897i = new float[9];
    }

    public ZoomTouchView(Context context) {
        this(context, null);
    }

    public ZoomTouchView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public ZoomTouchView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.f1899b = new float[2];
        this.f1900c = new float[2];
        this.f1901d = 1.0d;
        this.f1903f = new float[2];
        this.f1904g = new Matrix();
        this.f1905h = new Paint();
        this.f1905h.setTextSize(25.0f);
    }

    public static float m1904a(Matrix matrix) {
        matrix.getValues(f1897i);
        return f1897i[0];
    }

    private void m1905b(MotionEvent motionEvent) {
    }

    double m1906a(MotionEvent motionEvent) {
        if (motionEvent.getPointerCount() < 2) {
            return 0.0d;
        }
        return Math.hypot(((double) motionEvent.getX(1)) - ((double) motionEvent.getX(0)), ((double) motionEvent.getY(1)) - ((double) motionEvent.getY(0)));
    }

    float[] m1907a(MotionEvent motionEvent, float[] fArr) {
        int pointerCount = motionEvent.getPointerCount();
        if (fArr == null) {
            fArr = new float[2];
        }
        fArr[0] = motionEvent.getX(0);
        fArr[1] = motionEvent.getY(0);
        for (int i = 1; i < pointerCount; i++) {
            fArr[0] = fArr[0] + motionEvent.getX(i);
            fArr[1] = fArr[1] + motionEvent.getY(i);
        }
        fArr[0] = fArr[0] / ((float) pointerCount);
        fArr[1] = fArr[1] / ((float) pointerCount);
        return fArr;
    }

    @SuppressLint({"NewApi"})
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isEnabled()) {
            Matrix zoom = this.f1898a.getZoom();
            int zoomPosX = (int) this.f1898a.getZoomPosX();
            int zoomPosY = (int) this.f1898a.getZoomPosY();
            canvas.drawText(String.format("%d%% %+d,%+d", new Object[]{Integer.valueOf((int) (ZoomTouchView.m1904a(zoom) * 100.0f)), Integer.valueOf(zoomPosX), Integer.valueOf(zoomPosY)}), (float) (canvas.getWidth() - 200), (float) (canvas.getHeight() - 20), this.f1905h);
        }
    }

    @SuppressLint({"NewApi"})
    public boolean onTouchEvent(MotionEvent motionEvent) {
        int actionMasked = motionEvent.getActionMasked();
        if (!isEnabled()) {
            return false;
        }
        ViewConfiguration viewConfiguration = ViewConfiguration.get(getContext());
        if (actionMasked == 0 || actionMasked == 5 || actionMasked == 6 || actionMasked == 1) {
            float[] fArr = this.f1899b;
            this.f1899b[1] = Face.UNCOMPUTED_PROBABILITY;
            fArr[0] = Face.UNCOMPUTED_PROBABILITY;
            if (actionMasked == 0) {
                long eventTime = motionEvent.getEventTime();
                if (eventTime - this.f1902e < ((long) ViewConfiguration.getDoubleTapTimeout())) {
                    this.f1902e = 0;
                    m1905b(motionEvent);
                    return true;
                }
                this.f1902e = eventTime;
                return true;
            } else if (actionMasked != 5) {
                return true;
            } else {
                this.f1902e = 0;
                return true;
            }
        } else if (actionMasked != 2) {
            return true;
        } else {
            float a;
            if (this.f1899b[0] < 0.0f) {
                this.f1904g.set(this.f1898a.getZoom());
                this.f1898a.m1864a(this.f1903f);
                this.f1901d = m1906a(motionEvent);
                m1907a(motionEvent, this.f1899b);
                this.f1900c[0] = this.f1899b[0] - this.f1898a.getZoomPosX();
                this.f1900c[1] = this.f1899b[1] - this.f1898a.getZoomPosY();
                this.f1898a.getZoomInv().mapPoints(this.f1900c);
            }
            if (this.f1901d != 0.0d) {
                a = (float) (m1906a(motionEvent) / this.f1901d);
                if (a != 0.0f) {
                    Matrix matrix = new Matrix(this.f1904g);
                    float a2 = ZoomTouchView.m1904a(matrix);
                    a = Math.max(Math.min(a * a2, 20.0f), 0.1f) / a2;
                    matrix.preScale(a, a, this.f1900c[0], this.f1900c[1]);
                    this.f1898a.setZoom(matrix);
                }
            }
            float[] a3 = m1907a(motionEvent, null);
            float f = a3[0] - this.f1899b[0];
            a = a3[1] - this.f1899b[1];
            this.f1898a.m1866b(this.f1903f[0] + f, this.f1903f[1] + a);
            if (Math.hypot((double) f, (double) a) <= ((double) viewConfiguration.getScaledTouchSlop())) {
                return true;
            }
            this.f1902e = 0;
            return true;
        }
    }

    public void setSlate(Slate slate) {
        this.f1898a = slate;
    }
}
