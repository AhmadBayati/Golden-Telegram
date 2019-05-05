package com.hanista.mobogram.ui.Components;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.text.TextPaint;
import android.view.MotionEvent;
import android.view.View;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;
import com.hanista.mobogram.messenger.volley.Request.Method;
import com.hanista.mobogram.ui.Components.PhotoFilterView.CurvesToolValue;
import com.hanista.mobogram.ui.Components.PhotoFilterView.CurvesValue;
import java.util.Locale;

public class PhotoFilterCurvesControl extends View {
    private static final int CurvesSegmentBlacks = 1;
    private static final int CurvesSegmentHighlights = 4;
    private static final int CurvesSegmentMidtones = 3;
    private static final int CurvesSegmentNone = 0;
    private static final int CurvesSegmentShadows = 2;
    private static final int CurvesSegmentWhites = 5;
    private static final int GestureStateBegan = 1;
    private static final int GestureStateCancelled = 4;
    private static final int GestureStateChanged = 2;
    private static final int GestureStateEnded = 3;
    private static final int GestureStateFailed = 5;
    private int activeSegment;
    private Rect actualArea;
    private boolean checkForMoving;
    private CurvesToolValue curveValue;
    private PhotoFilterCurvesControlDelegate delegate;
    private boolean isMoving;
    private float lastX;
    private float lastY;
    private Paint paint;
    private Paint paintCurve;
    private Paint paintDash;
    private Path path;
    private TextPaint textPaint;

    public interface PhotoFilterCurvesControlDelegate {
        void valueChanged();
    }

    public PhotoFilterCurvesControl(Context context, CurvesToolValue curvesToolValue) {
        super(context);
        this.activeSegment = CurvesSegmentNone;
        this.checkForMoving = true;
        this.actualArea = new Rect();
        this.paint = new Paint(GestureStateBegan);
        this.paintDash = new Paint(GestureStateBegan);
        this.paintCurve = new Paint(GestureStateBegan);
        this.textPaint = new TextPaint(GestureStateBegan);
        this.path = new Path();
        setWillNotDraw(false);
        this.curveValue = curvesToolValue;
        this.paint.setColor(-1711276033);
        this.paint.setStrokeWidth((float) AndroidUtilities.dp(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        this.paint.setStyle(Style.STROKE);
        this.paintDash.setColor(-1711276033);
        this.paintDash.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
        this.paintDash.setStyle(Style.STROKE);
        this.paintCurve.setColor(-1);
        this.paintCurve.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
        this.paintCurve.setStyle(Style.STROKE);
        this.textPaint.setColor(-4210753);
        this.textPaint.setTextSize((float) AndroidUtilities.dp(13.0f));
    }

    private void handlePan(int i, MotionEvent motionEvent) {
        float x = motionEvent.getX();
        float y = motionEvent.getY();
        switch (i) {
            case GestureStateBegan /*1*/:
                selectSegmentWithPoint(x);
            case GestureStateChanged /*2*/:
                float min = Math.min(2.0f, (this.lastY - y) / 8.0f);
                CurvesValue curvesValue = null;
                switch (this.curveValue.activeType) {
                    case CurvesSegmentNone /*0*/:
                        curvesValue = this.curveValue.luminanceCurve;
                        break;
                    case GestureStateBegan /*1*/:
                        curvesValue = this.curveValue.redCurve;
                        break;
                    case GestureStateChanged /*2*/:
                        curvesValue = this.curveValue.greenCurve;
                        break;
                    case GestureStateEnded /*3*/:
                        curvesValue = this.curveValue.blueCurve;
                        break;
                }
                switch (this.activeSegment) {
                    case GestureStateBegan /*1*/:
                        curvesValue.blacksLevel = Math.max(0.0f, Math.min(100.0f, min + curvesValue.blacksLevel));
                        break;
                    case GestureStateChanged /*2*/:
                        curvesValue.shadowsLevel = Math.max(0.0f, Math.min(100.0f, min + curvesValue.shadowsLevel));
                        break;
                    case GestureStateEnded /*3*/:
                        curvesValue.midtonesLevel = Math.max(0.0f, Math.min(100.0f, min + curvesValue.midtonesLevel));
                        break;
                    case GestureStateCancelled /*4*/:
                        curvesValue.highlightsLevel = Math.max(0.0f, Math.min(100.0f, min + curvesValue.highlightsLevel));
                        break;
                    case GestureStateFailed /*5*/:
                        curvesValue.whitesLevel = Math.max(0.0f, Math.min(100.0f, min + curvesValue.whitesLevel));
                        break;
                }
                invalidate();
                if (this.delegate != null) {
                    this.delegate.valueChanged();
                }
                this.lastX = x;
                this.lastY = y;
            case GestureStateEnded /*3*/:
            case GestureStateCancelled /*4*/:
            case GestureStateFailed /*5*/:
                unselectSegments();
            default:
        }
    }

    private void selectSegmentWithPoint(float f) {
        if (this.activeSegment == 0) {
            this.activeSegment = (int) Math.floor((double) (((f - this.actualArea.f2685x) / (this.actualArea.width / 5.0f)) + DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        }
    }

    private void unselectSegments() {
        if (this.activeSegment != 0) {
            this.activeSegment = CurvesSegmentNone;
        }
    }

    @SuppressLint({"DrawAllocation"})
    protected void onDraw(Canvas canvas) {
        Canvas canvas2;
        float f = this.actualArea.width / 5.0f;
        for (int i = CurvesSegmentNone; i < GestureStateCancelled; i += GestureStateBegan) {
            canvas2 = canvas;
            canvas2.drawLine((((float) i) * f) + (this.actualArea.f2685x + f), this.actualArea.f2686y, (((float) i) * f) + (this.actualArea.f2685x + f), this.actualArea.height + this.actualArea.f2686y, this.paint);
        }
        canvas2 = canvas;
        canvas2.drawLine(this.actualArea.f2685x, this.actualArea.height + this.actualArea.f2686y, this.actualArea.width + this.actualArea.f2685x, this.actualArea.f2686y, this.paintDash);
        CurvesValue curvesValue = null;
        switch (this.curveValue.activeType) {
            case CurvesSegmentNone /*0*/:
                this.paintCurve.setColor(-1);
                curvesValue = this.curveValue.luminanceCurve;
                break;
            case GestureStateBegan /*1*/:
                this.paintCurve.setColor(-1229492);
                curvesValue = this.curveValue.redCurve;
                break;
            case GestureStateChanged /*2*/:
                this.paintCurve.setColor(-15667555);
                curvesValue = this.curveValue.greenCurve;
                break;
            case GestureStateEnded /*3*/:
                this.paintCurve.setColor(-13404165);
                curvesValue = this.curveValue.blueCurve;
                break;
        }
        for (int i2 = CurvesSegmentNone; i2 < GestureStateFailed; i2 += GestureStateBegan) {
            String format;
            Object[] objArr;
            switch (i2) {
                case CurvesSegmentNone /*0*/:
                    objArr = new Object[GestureStateBegan];
                    objArr[CurvesSegmentNone] = Float.valueOf(curvesValue.blacksLevel / 100.0f);
                    format = String.format(Locale.US, "%.2f", objArr);
                    break;
                case GestureStateBegan /*1*/:
                    objArr = new Object[GestureStateBegan];
                    objArr[CurvesSegmentNone] = Float.valueOf(curvesValue.shadowsLevel / 100.0f);
                    format = String.format(Locale.US, "%.2f", objArr);
                    break;
                case GestureStateChanged /*2*/:
                    objArr = new Object[GestureStateBegan];
                    objArr[CurvesSegmentNone] = Float.valueOf(curvesValue.midtonesLevel / 100.0f);
                    format = String.format(Locale.US, "%.2f", objArr);
                    break;
                case GestureStateEnded /*3*/:
                    objArr = new Object[GestureStateBegan];
                    objArr[CurvesSegmentNone] = Float.valueOf(curvesValue.highlightsLevel / 100.0f);
                    format = String.format(Locale.US, "%.2f", objArr);
                    break;
                case GestureStateCancelled /*4*/:
                    objArr = new Object[GestureStateBegan];
                    objArr[CurvesSegmentNone] = Float.valueOf(curvesValue.whitesLevel / 100.0f);
                    format = String.format(Locale.US, "%.2f", objArr);
                    break;
                default:
                    format = TtmlNode.ANONYMOUS_REGION_ID;
                    break;
            }
            canvas.drawText(format, (((f - this.textPaint.measureText(format)) / 2.0f) + this.actualArea.f2685x) + (((float) i2) * f), (this.actualArea.f2686y + this.actualArea.height) - ((float) AndroidUtilities.dp(4.0f)), this.textPaint);
        }
        float[] interpolateCurve = curvesValue.interpolateCurve();
        invalidate();
        this.path.reset();
        for (int i3 = CurvesSegmentNone; i3 < interpolateCurve.length / GestureStateChanged; i3 += GestureStateBegan) {
            if (i3 == 0) {
                this.path.moveTo(this.actualArea.f2685x + (interpolateCurve[i3 * GestureStateChanged] * this.actualArea.width), this.actualArea.f2686y + ((DefaultRetryPolicy.DEFAULT_BACKOFF_MULT - interpolateCurve[(i3 * GestureStateChanged) + GestureStateBegan]) * this.actualArea.height));
            } else {
                this.path.lineTo(this.actualArea.f2685x + (interpolateCurve[i3 * GestureStateChanged] * this.actualArea.width), this.actualArea.f2686y + ((DefaultRetryPolicy.DEFAULT_BACKOFF_MULT - interpolateCurve[(i3 * GestureStateChanged) + GestureStateBegan]) * this.actualArea.height));
            }
        }
        canvas.drawPath(this.path, this.paintCurve);
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        switch (motionEvent.getActionMasked()) {
            case CurvesSegmentNone /*0*/:
            case GestureStateFailed /*5*/:
                if (motionEvent.getPointerCount() != GestureStateBegan) {
                    if (this.isMoving) {
                        handlePan(GestureStateEnded, motionEvent);
                        this.checkForMoving = true;
                        this.isMoving = false;
                        break;
                    }
                } else if (this.checkForMoving && !this.isMoving) {
                    float x = motionEvent.getX();
                    float y = motionEvent.getY();
                    this.lastX = x;
                    this.lastY = y;
                    if (x >= this.actualArea.f2685x && x <= this.actualArea.f2685x + this.actualArea.width && y >= this.actualArea.f2686y && y <= this.actualArea.f2686y + this.actualArea.height) {
                        this.isMoving = true;
                    }
                    this.checkForMoving = false;
                    if (this.isMoving) {
                        handlePan(GestureStateBegan, motionEvent);
                        break;
                    }
                }
                break;
            case GestureStateBegan /*1*/:
            case GestureStateEnded /*3*/:
            case Method.TRACE /*6*/:
                if (this.isMoving) {
                    handlePan(GestureStateEnded, motionEvent);
                    this.isMoving = false;
                }
                this.checkForMoving = true;
                break;
            case GestureStateChanged /*2*/:
                if (this.isMoving) {
                    handlePan(GestureStateChanged, motionEvent);
                    break;
                }
                break;
        }
        return true;
    }

    public void setActualArea(float f, float f2, float f3, float f4) {
        this.actualArea.f2685x = f;
        this.actualArea.f2686y = f2;
        this.actualArea.width = f3;
        this.actualArea.height = f4;
    }

    public void setDelegate(PhotoFilterCurvesControlDelegate photoFilterCurvesControlDelegate) {
        this.delegate = photoFilterCurvesControlDelegate;
    }
}
