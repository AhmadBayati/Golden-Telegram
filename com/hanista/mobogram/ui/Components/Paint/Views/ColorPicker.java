package com.hanista.mobogram.ui.Components.Paint.Views;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.exoplayer.util.NalUnitUtil;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;
import com.hanista.mobogram.ui.ActionBar.Theme;
import com.hanista.mobogram.ui.Components.LayoutHelper;
import com.hanista.mobogram.ui.Components.Paint.Swatch;

public class ColorPicker extends FrameLayout {
    private static final int[] COLORS;
    private static final float[] LOCATIONS;
    private Paint backgroundPaint;
    private boolean changingWeight;
    private ColorPickerDelegate delegate;
    private boolean dragging;
    private float draggingFactor;
    private Paint gradientPaint;
    private boolean interacting;
    private OvershootInterpolator interpolator;
    private float location;
    private RectF rectF;
    private ImageView settingsButton;
    private Drawable shadowDrawable;
    private Paint swatchPaint;
    private Paint swatchStrokePaint;
    private boolean wasChangingWeight;
    private float weight;

    /* renamed from: com.hanista.mobogram.ui.Components.Paint.Views.ColorPicker.1 */
    class C13851 implements OnClickListener {
        C13851() {
        }

        public void onClick(View view) {
            if (ColorPicker.this.delegate != null) {
                ColorPicker.this.delegate.onSettingsPressed();
            }
        }
    }

    public interface ColorPickerDelegate {
        void onBeganColorPicking();

        void onColorValueChanged();

        void onFinishedColorPicking();

        void onSettingsPressed();
    }

    static {
        COLORS = new int[]{-1431751, -2409774, -13610525, -11942419, -8337308, -205211, -223667, Theme.MSG_TEXT_COLOR, -1};
        LOCATIONS = new float[]{0.0f, 0.14f, 0.24f, 0.39f, 0.49f, 0.62f, 0.73f, 0.85f, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT};
    }

    public ColorPicker(Context context) {
        super(context);
        this.interpolator = new OvershootInterpolator(1.02f);
        this.gradientPaint = new Paint(1);
        this.backgroundPaint = new Paint(1);
        this.swatchPaint = new Paint(1);
        this.swatchStrokePaint = new Paint(1);
        this.rectF = new RectF();
        this.location = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
        this.weight = 0.27f;
        setWillNotDraw(false);
        this.shadowDrawable = getResources().getDrawable(C0338R.drawable.knob_shadow);
        this.backgroundPaint.setColor(-1);
        this.swatchStrokePaint.setStyle(Style.STROKE);
        this.swatchStrokePaint.setStrokeWidth((float) AndroidUtilities.dp(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        this.settingsButton = new ImageView(context);
        this.settingsButton.setScaleType(ScaleType.CENTER);
        this.settingsButton.setImageResource(C0338R.drawable.photo_paint_brush);
        addView(this.settingsButton, LayoutHelper.createFrame(60, 52.0f));
        this.settingsButton.setOnClickListener(new C13851());
        this.location = context.getSharedPreferences("paint", 0).getFloat("last_color_location", DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        setLocation(this.location);
    }

    private int interpolateColors(int i, int i2, float f) {
        float min = Math.min(Math.max(f, 0.0f), DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        int red = Color.red(i);
        int red2 = Color.red(i2);
        int green = Color.green(i);
        int green2 = Color.green(i2);
        int blue = Color.blue(i);
        return Color.argb(NalUnitUtil.EXTENDED_SAR, Math.min(NalUnitUtil.EXTENDED_SAR, (int) ((((float) (red2 - red)) * min) + ((float) red))), Math.min(NalUnitUtil.EXTENDED_SAR, (int) (((float) green) + (((float) (green2 - green)) * min))), Math.min(NalUnitUtil.EXTENDED_SAR, (int) ((min * ((float) (Color.blue(i2) - blue))) + ((float) blue))));
    }

    private void setDragging(boolean z, boolean z2) {
        if (this.dragging != z) {
            this.dragging = z;
            float f = this.dragging ? DefaultRetryPolicy.DEFAULT_BACKOFF_MULT : 0.0f;
            if (z2) {
                Animator ofFloat = ObjectAnimator.ofFloat(this, "draggingFactor", new float[]{this.draggingFactor, f});
                ofFloat.setInterpolator(this.interpolator);
                int i = 300;
                if (this.wasChangingWeight) {
                    i = (int) (((float) 300) + (this.weight * 75.0f));
                }
                ofFloat.setDuration((long) i);
                ofFloat.start();
                return;
            }
            setDraggingFactor(f);
        }
    }

    private void setDraggingFactor(float f) {
        this.draggingFactor = f;
        invalidate();
    }

    public int colorForLocation(float f) {
        int i = -1;
        if (f <= 0.0f) {
            return COLORS[0];
        }
        if (f >= DefaultRetryPolicy.DEFAULT_BACKOFF_MULT) {
            return COLORS[COLORS.length - 1];
        }
        int i2 = 1;
        while (i2 < LOCATIONS.length) {
            if (LOCATIONS[i2] > f) {
                i = i2 - 1;
                break;
            }
            i2++;
        }
        i2 = -1;
        float f2 = LOCATIONS[i];
        return interpolateColors(COLORS[i], COLORS[i2], (f - f2) / (LOCATIONS[i2] - f2));
    }

    public float getDraggingFactor() {
        return this.draggingFactor;
    }

    public View getSettingsButton() {
        return this.settingsButton;
    }

    public Swatch getSwatch() {
        return new Swatch(colorForLocation(this.location), this.location, this.weight);
    }

    protected void onDraw(Canvas canvas) {
        canvas.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(6.0f), (float) AndroidUtilities.dp(6.0f), this.gradientPaint);
        int centerX = (int) (((this.draggingFactor * ((float) (-AndroidUtilities.dp(70.0f)))) + this.rectF.centerX()) - (this.changingWeight ? this.weight * ((float) AndroidUtilities.dp(190.0f)) : 0.0f));
        int dp = ((int) ((this.rectF.top - ((float) AndroidUtilities.dp(22.0f))) + (this.rectF.height() * this.location))) + AndroidUtilities.dp(22.0f);
        int dp2 = (int) (((float) AndroidUtilities.dp(24.0f)) * ((this.draggingFactor + DefaultRetryPolicy.DEFAULT_BACKOFF_MULT) * 0.5f));
        this.shadowDrawable.setBounds(centerX - dp2, dp - dp2, centerX + dp2, dp2 + dp);
        this.shadowDrawable.draw(canvas);
        float floor = (((float) ((int) Math.floor((double) (((float) AndroidUtilities.dp(4.0f)) + (((float) (AndroidUtilities.dp(19.0f) - AndroidUtilities.dp(4.0f))) * this.weight))))) * (this.draggingFactor + DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)) / 2.0f;
        canvas.drawCircle((float) centerX, (float) dp, ((float) (AndroidUtilities.dp(22.0f) / 2)) * (this.draggingFactor + DefaultRetryPolicy.DEFAULT_BACKOFF_MULT), this.backgroundPaint);
        canvas.drawCircle((float) centerX, (float) dp, floor, this.swatchPaint);
        canvas.drawCircle((float) centerX, (float) dp, floor - ((float) AndroidUtilities.dp(0.5f)), this.swatchStrokePaint);
    }

    @SuppressLint({"DrawAllocation"})
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        int i5 = i3 - i;
        int i6 = i4 - i2;
        int measuredHeight = (getMeasuredHeight() - AndroidUtilities.dp(26.0f)) - AndroidUtilities.dp(64.0f);
        this.gradientPaint.setShader(new LinearGradient(0.0f, (float) AndroidUtilities.dp(26.0f), 0.0f, (float) (AndroidUtilities.dp(26.0f) + measuredHeight), COLORS, LOCATIONS, TileMode.REPEAT));
        int dp = (i5 - AndroidUtilities.dp(26.0f)) - AndroidUtilities.dp(8.0f);
        int dp2 = AndroidUtilities.dp(26.0f);
        this.rectF.set((float) dp, (float) dp2, (float) (dp + AndroidUtilities.dp(8.0f)), (float) (dp2 + measuredHeight));
        this.settingsButton.layout(i5 - this.settingsButton.getMeasuredWidth(), i6 - AndroidUtilities.dp(52.0f), i5, i6);
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (motionEvent.getPointerCount() > 1) {
            return false;
        }
        float x = motionEvent.getX() - this.rectF.left;
        float y = motionEvent.getY() - this.rectF.top;
        if (!this.interacting && x < ((float) (-AndroidUtilities.dp(10.0f)))) {
            return false;
        }
        int actionMasked = motionEvent.getActionMasked();
        if (actionMasked == 3 || actionMasked == 1 || actionMasked == 6) {
            if (this.interacting && this.delegate != null) {
                this.delegate.onFinishedColorPicking();
                getContext().getSharedPreferences("paint", 0).edit().putFloat("last_color_location", this.location).commit();
            }
            this.interacting = false;
            this.wasChangingWeight = this.changingWeight;
            this.changingWeight = false;
            setDragging(false, true);
            return false;
        } else if (actionMasked != 0 && actionMasked != 2) {
            return false;
        } else {
            if (!this.interacting) {
                this.interacting = true;
                if (this.delegate != null) {
                    this.delegate.onBeganColorPicking();
                }
            }
            setLocation(Math.max(0.0f, Math.min(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, y / this.rectF.height())));
            setDragging(true, true);
            if (x < ((float) (-AndroidUtilities.dp(10.0f)))) {
                this.changingWeight = true;
                setWeight(Math.max(0.0f, Math.min(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, ((-x) - ((float) AndroidUtilities.dp(10.0f))) / ((float) AndroidUtilities.dp(190.0f)))));
            }
            if (this.delegate != null) {
                this.delegate.onColorValueChanged();
            }
            return true;
        }
    }

    public void setDelegate(ColorPickerDelegate colorPickerDelegate) {
        this.delegate = colorPickerDelegate;
    }

    public void setLocation(float f) {
        this.location = f;
        int colorForLocation = colorForLocation(f);
        this.swatchPaint.setColor(colorForLocation);
        float[] fArr = new float[3];
        Color.colorToHSV(colorForLocation, fArr);
        if (((double) fArr[0]) >= 0.001d || ((double) fArr[1]) >= 0.001d || fArr[2] <= 0.92f) {
            this.swatchStrokePaint.setColor(colorForLocation);
        } else {
            colorForLocation = (int) ((DefaultRetryPolicy.DEFAULT_BACKOFF_MULT - (((fArr[2] - 0.92f) / 0.08f) * 0.22f)) * 255.0f);
            this.swatchStrokePaint.setColor(Color.rgb(colorForLocation, colorForLocation, colorForLocation));
        }
        invalidate();
    }

    public void setSettingsButtonImage(int i) {
        this.settingsButton.setImageResource(i);
    }

    public void setSwatch(Swatch swatch) {
        setLocation(swatch.colorLocation);
        setWeight(swatch.brushWeight);
    }

    public void setWeight(float f) {
        this.weight = f;
        invalidate();
    }
}
