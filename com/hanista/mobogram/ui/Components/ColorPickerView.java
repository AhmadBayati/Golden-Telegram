package com.hanista.mobogram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.internal.view.SupportMenu;
import android.support.v4.view.InputDeviceCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.exoplayer.C0700C;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;
import com.hanista.mobogram.tgnet.TLRPC;
import com.hanista.mobogram.ui.ActionBar.Theme;

public class ColorPickerView extends View {
    private static final int[] COLORS;
    private static final String STATE_ANGLE = "angle";
    private static final String STATE_OLD_COLOR = "color";
    private static final String STATE_PARENT = "parent";
    private static final String STATE_SHOW_OLD_COLOR = "showColor";
    private float mAngle;
    private Paint mCenterHaloPaint;
    private int mCenterNewColor;
    private Paint mCenterNewPaint;
    private int mCenterOldColor;
    private Paint mCenterOldPaint;
    private RectF mCenterRectangle;
    private int mColorCenterHaloRadius;
    private int mColorCenterRadius;
    private int mColorPointerHaloRadius;
    private int mColorPointerRadius;
    private Paint mColorWheelPaint;
    private int mColorWheelRadius;
    private RectF mColorWheelRectangle;
    private int mColorWheelThickness;
    private float[] mHSV;
    private Paint mPointerColor;
    private Paint mPointerHaloPaint;
    private int mPreferredColorCenterHaloRadius;
    private int mPreferredColorCenterRadius;
    private int mPreferredColorWheelRadius;
    private boolean mShowCenterOldColor;
    private float mSlopX;
    private float mSlopY;
    private float mTranslationOffset;
    private boolean mUserIsMovingPointer;
    private int oldChangedListenerColor;
    private int oldSelectedListenerColor;
    private OnColorChangedListener onColorChangedListener;
    private OnColorSelectedListener onColorSelectedListener;

    public interface OnColorChangedListener {
        void onColorChanged(int i);
    }

    public interface OnColorSelectedListener {
        void onColorSelected(int i);
    }

    static {
        COLORS = new int[]{SupportMenu.CATEGORY_MASK, -65281, -16776961, -16711681, -16711936, -1, InputDeviceCompat.SOURCE_ANY, SupportMenu.CATEGORY_MASK};
    }

    public ColorPickerView(Context context) {
        super(context);
        this.mColorWheelRectangle = new RectF();
        this.mCenterRectangle = new RectF();
        this.mUserIsMovingPointer = false;
        this.mHSV = new float[3];
        init(null, 0);
    }

    public ColorPickerView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mColorWheelRectangle = new RectF();
        this.mCenterRectangle = new RectF();
        this.mUserIsMovingPointer = false;
        this.mHSV = new float[3];
        init(attributeSet, 0);
    }

    public ColorPickerView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mColorWheelRectangle = new RectF();
        this.mCenterRectangle = new RectF();
        this.mUserIsMovingPointer = false;
        this.mHSV = new float[3];
        init(attributeSet, i);
    }

    private int ave(int i, int i2, float f) {
        return Math.round(((float) (i2 - i)) * f) + i;
    }

    private int calculateColor(float f) {
        float f2 = (float) (((double) f) / 6.283185307179586d);
        if (f2 < 0.0f) {
            f2 += DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
        }
        if (f2 <= 0.0f) {
            return COLORS[0];
        }
        if (f2 >= DefaultRetryPolicy.DEFAULT_BACKOFF_MULT) {
            return COLORS[COLORS.length - 1];
        }
        f2 *= (float) (COLORS.length - 1);
        int i = (int) f2;
        f2 -= (float) i;
        int i2 = COLORS[i];
        i = COLORS[i + 1];
        return Color.argb(ave(Color.alpha(i2), Color.alpha(i), f2), ave(Color.red(i2), Color.red(i), f2), ave(Color.green(i2), Color.green(i), f2), ave(Color.blue(i2), Color.blue(i), f2));
    }

    private float[] calculatePointerPosition(float f) {
        float cos = (float) (((double) this.mColorWheelRadius) * Math.cos((double) f));
        float sin = (float) (((double) this.mColorWheelRadius) * Math.sin((double) f));
        return new float[]{cos, sin};
    }

    private float colorToAngle(int i) {
        float[] fArr = new float[3];
        Color.colorToHSV(i, fArr);
        return (float) Math.toRadians((double) (-fArr[0]));
    }

    private void init(AttributeSet attributeSet, int i) {
        this.mColorWheelThickness = AndroidUtilities.dp(8.0f);
        this.mColorWheelRadius = AndroidUtilities.dp(124.0f);
        this.mPreferredColorWheelRadius = this.mColorWheelRadius;
        this.mColorCenterRadius = AndroidUtilities.dp(54.0f);
        this.mPreferredColorCenterRadius = this.mColorCenterRadius;
        this.mColorCenterHaloRadius = AndroidUtilities.dp(BitmapDescriptorFactory.HUE_YELLOW);
        this.mPreferredColorCenterHaloRadius = this.mColorCenterHaloRadius;
        this.mColorPointerRadius = AndroidUtilities.dp(14.0f);
        this.mColorPointerHaloRadius = AndroidUtilities.dp(18.0f);
        this.mAngle = -1.5707964f;
        Shader sweepGradient = new SweepGradient(0.0f, 0.0f, COLORS, null);
        this.mColorWheelPaint = new Paint(1);
        this.mColorWheelPaint.setShader(sweepGradient);
        this.mColorWheelPaint.setStyle(Style.STROKE);
        this.mColorWheelPaint.setStrokeWidth((float) this.mColorWheelThickness);
        this.mPointerHaloPaint = new Paint(1);
        this.mPointerHaloPaint.setColor(Theme.MSG_TEXT_COLOR);
        this.mPointerHaloPaint.setAlpha(80);
        this.mPointerColor = new Paint(1);
        this.mPointerColor.setColor(calculateColor(this.mAngle));
        this.mCenterNewPaint = new Paint(1);
        this.mCenterNewPaint.setColor(calculateColor(this.mAngle));
        this.mCenterNewPaint.setStyle(Style.FILL);
        this.mCenterOldPaint = new Paint(1);
        this.mCenterOldPaint.setColor(calculateColor(this.mAngle));
        this.mCenterOldPaint.setStyle(Style.FILL);
        this.mCenterHaloPaint = new Paint(1);
        this.mCenterHaloPaint.setColor(Theme.MSG_TEXT_COLOR);
        this.mCenterHaloPaint.setAlpha(0);
        this.mCenterNewColor = calculateColor(this.mAngle);
        this.mCenterOldColor = calculateColor(this.mAngle);
        this.mShowCenterOldColor = true;
    }

    public int getColor() {
        return this.mCenterNewColor;
    }

    public int getOldCenterColor() {
        return this.mCenterOldColor;
    }

    public boolean getShowOldCenterColor() {
        return this.mShowCenterOldColor;
    }

    protected void onDraw(Canvas canvas) {
        canvas.translate(this.mTranslationOffset, this.mTranslationOffset);
        canvas.drawOval(this.mColorWheelRectangle, this.mColorWheelPaint);
        float[] calculatePointerPosition = calculatePointerPosition(this.mAngle);
        canvas.drawCircle(calculatePointerPosition[0], calculatePointerPosition[1], (float) this.mColorPointerHaloRadius, this.mPointerHaloPaint);
        canvas.drawCircle(calculatePointerPosition[0], calculatePointerPosition[1], (float) this.mColorPointerRadius, this.mPointerColor);
        canvas.drawCircle(0.0f, 0.0f, (float) this.mColorCenterHaloRadius, this.mCenterHaloPaint);
        if (this.mShowCenterOldColor) {
            canvas.drawArc(this.mCenterRectangle, 90.0f, BitmapDescriptorFactory.HUE_CYAN, true, this.mCenterOldPaint);
            canvas.drawArc(this.mCenterRectangle, BitmapDescriptorFactory.HUE_VIOLET, BitmapDescriptorFactory.HUE_CYAN, true, this.mCenterNewPaint);
            return;
        }
        canvas.drawArc(this.mCenterRectangle, 0.0f, 360.0f, true, this.mCenterNewPaint);
    }

    protected void onMeasure(int i, int i2) {
        int i3 = (this.mPreferredColorWheelRadius + this.mColorPointerHaloRadius) * 2;
        int mode = MeasureSpec.getMode(i);
        int size = MeasureSpec.getSize(i);
        int mode2 = MeasureSpec.getMode(i2);
        int size2 = MeasureSpec.getSize(i2);
        if (mode != C0700C.ENCODING_PCM_32BIT) {
            size = mode == TLRPC.MESSAGE_FLAG_MEGAGROUP ? Math.min(i3, size) : i3;
        }
        if (mode2 != C0700C.ENCODING_PCM_32BIT) {
            size2 = mode2 == TLRPC.MESSAGE_FLAG_MEGAGROUP ? Math.min(i3, size2) : i3;
        }
        size2 = Math.min(size, size2);
        setMeasuredDimension(size2, size2);
        this.mTranslationOffset = ((float) size2) * 0.5f;
        this.mColorWheelRadius = ((size2 / 2) - this.mColorWheelThickness) - this.mColorPointerHaloRadius;
        this.mColorWheelRectangle.set((float) (-this.mColorWheelRadius), (float) (-this.mColorWheelRadius), (float) this.mColorWheelRadius, (float) this.mColorWheelRadius);
        this.mColorCenterRadius = (int) (((float) this.mPreferredColorCenterRadius) * (((float) this.mColorWheelRadius) / ((float) this.mPreferredColorWheelRadius)));
        this.mColorCenterHaloRadius = (int) (((float) this.mPreferredColorCenterHaloRadius) * (((float) this.mColorWheelRadius) / ((float) this.mPreferredColorWheelRadius)));
        this.mCenterRectangle.set((float) (-this.mColorCenterRadius), (float) (-this.mColorCenterRadius), (float) this.mColorCenterRadius, (float) this.mColorCenterRadius);
    }

    protected void onRestoreInstanceState(Parcelable parcelable) {
        Bundle bundle = (Bundle) parcelable;
        super.onRestoreInstanceState(bundle.getParcelable(STATE_PARENT));
        this.mAngle = bundle.getFloat(STATE_ANGLE);
        setOldCenterColor(bundle.getInt(STATE_OLD_COLOR));
        this.mShowCenterOldColor = bundle.getBoolean(STATE_SHOW_OLD_COLOR);
        int calculateColor = calculateColor(this.mAngle);
        this.mPointerColor.setColor(calculateColor);
        setNewCenterColor(calculateColor);
    }

    protected Parcelable onSaveInstanceState() {
        Parcelable onSaveInstanceState = super.onSaveInstanceState();
        Parcelable bundle = new Bundle();
        bundle.putParcelable(STATE_PARENT, onSaveInstanceState);
        bundle.putFloat(STATE_ANGLE, this.mAngle);
        bundle.putInt(STATE_OLD_COLOR, this.mCenterOldColor);
        bundle.putBoolean(STATE_SHOW_OLD_COLOR, this.mShowCenterOldColor);
        return bundle;
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        getParent().requestDisallowInterceptTouchEvent(true);
        float x = motionEvent.getX() - this.mTranslationOffset;
        float y = motionEvent.getY() - this.mTranslationOffset;
        switch (motionEvent.getAction()) {
            case VideoPlayer.TRACK_DEFAULT /*0*/:
                float[] calculatePointerPosition = calculatePointerPosition(this.mAngle);
                if (x < calculatePointerPosition[0] - ((float) this.mColorPointerHaloRadius) || x > calculatePointerPosition[0] + ((float) this.mColorPointerHaloRadius) || y < calculatePointerPosition[1] - ((float) this.mColorPointerHaloRadius) || y > calculatePointerPosition[1] + ((float) this.mColorPointerHaloRadius)) {
                    if (x >= ((float) (-this.mColorCenterRadius)) && x <= ((float) this.mColorCenterRadius) && y >= ((float) (-this.mColorCenterRadius)) && y <= ((float) this.mColorCenterRadius) && this.mShowCenterOldColor) {
                        this.mCenterHaloPaint.setAlpha(80);
                        setColor(getOldCenterColor());
                        invalidate();
                        break;
                    }
                    getParent().requestDisallowInterceptTouchEvent(false);
                    return false;
                }
                this.mSlopX = x - calculatePointerPosition[0];
                this.mSlopY = y - calculatePointerPosition[1];
                this.mUserIsMovingPointer = true;
                invalidate();
                break;
                break;
            case VideoPlayer.TYPE_AUDIO /*1*/:
                this.mUserIsMovingPointer = false;
                this.mCenterHaloPaint.setAlpha(0);
                if (!(this.onColorSelectedListener == null || this.mCenterNewColor == this.oldSelectedListenerColor)) {
                    this.onColorSelectedListener.onColorSelected(this.mCenterNewColor);
                    this.oldSelectedListenerColor = this.mCenterNewColor;
                }
                invalidate();
                break;
            case VideoPlayer.STATE_PREPARING /*2*/:
                if (this.mUserIsMovingPointer) {
                    this.mAngle = (float) Math.atan2((double) (y - this.mSlopY), (double) (x - this.mSlopX));
                    this.mPointerColor.setColor(calculateColor(this.mAngle));
                    int calculateColor = calculateColor(this.mAngle);
                    this.mCenterNewColor = calculateColor;
                    setNewCenterColor(calculateColor);
                    invalidate();
                    break;
                }
                getParent().requestDisallowInterceptTouchEvent(false);
                return false;
            case VideoPlayer.STATE_BUFFERING /*3*/:
                if (!(this.onColorSelectedListener == null || this.mCenterNewColor == this.oldSelectedListenerColor)) {
                    this.onColorSelectedListener.onColorSelected(this.mCenterNewColor);
                    this.oldSelectedListenerColor = this.mCenterNewColor;
                    break;
                }
        }
        return true;
    }

    public void setColor(int i) {
        this.mAngle = colorToAngle(i);
        this.mPointerColor.setColor(calculateColor(this.mAngle));
        this.mCenterNewPaint.setColor(calculateColor(this.mAngle));
        invalidate();
    }

    public void setNewCenterColor(int i) {
        this.mCenterNewColor = i;
        this.mCenterNewPaint.setColor(i);
        if (this.mCenterOldColor == 0) {
            this.mCenterOldColor = i;
            this.mCenterOldPaint.setColor(i);
        }
        if (!(this.onColorChangedListener == null || i == this.oldChangedListenerColor)) {
            this.onColorChangedListener.onColorChanged(i);
            this.oldChangedListenerColor = i;
        }
        invalidate();
    }

    public void setOldCenterColor(int i) {
        this.mCenterOldColor = i;
        this.mCenterOldPaint.setColor(i);
        invalidate();
    }

    public void setOnColorChangedListener(OnColorChangedListener onColorChangedListener) {
        this.onColorChangedListener = onColorChangedListener;
    }

    public void setOnColorSelectedListener(OnColorSelectedListener onColorSelectedListener) {
        this.onColorSelectedListener = onColorSelectedListener;
    }

    public void setShowOldCenterColor(boolean z) {
        this.mShowCenterOldColor = z;
        invalidate();
    }
}
