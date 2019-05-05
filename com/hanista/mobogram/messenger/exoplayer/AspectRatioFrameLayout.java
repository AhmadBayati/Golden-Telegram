package com.hanista.mobogram.messenger.exoplayer;

import android.content.Context;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.TextureView;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;

public final class AspectRatioFrameLayout extends FrameLayout {
    private static final float MAX_ASPECT_RATIO_DEFORMATION_FRACTION = 0.01f;
    private Matrix matrix;
    private int rotation;
    private float videoAspectRatio;

    public AspectRatioFrameLayout(Context context) {
        super(context);
        this.matrix = new Matrix();
    }

    public AspectRatioFrameLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.matrix = new Matrix();
    }

    protected void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        if (this.videoAspectRatio != 0.0f) {
            int measuredWidth = getMeasuredWidth();
            int measuredHeight = getMeasuredHeight();
            float f = (this.videoAspectRatio / (((float) measuredWidth) / ((float) measuredHeight))) - DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
            if (Math.abs(f) > MAX_ASPECT_RATIO_DEFORMATION_FRACTION) {
                if (f > 0.0f) {
                    measuredHeight = (int) (((float) measuredWidth) / this.videoAspectRatio);
                } else {
                    measuredWidth = (int) (((float) measuredHeight) * this.videoAspectRatio);
                }
                super.onMeasure(MeasureSpec.makeMeasureSpec(measuredWidth, C0700C.ENCODING_PCM_32BIT), MeasureSpec.makeMeasureSpec(measuredHeight, C0700C.ENCODING_PCM_32BIT));
                int childCount = getChildCount();
                for (measuredWidth = 0; measuredWidth < childCount; measuredWidth++) {
                    View childAt = getChildAt(measuredWidth);
                    if (childAt instanceof TextureView) {
                        this.matrix.reset();
                        measuredWidth = getWidth() / 2;
                        childCount = getHeight() / 2;
                        this.matrix.postRotate((float) this.rotation, (float) measuredWidth, (float) childCount);
                        if (this.rotation == 90 || this.rotation == 270) {
                            float height = ((float) getHeight()) / ((float) getWidth());
                            this.matrix.postScale(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT / height, height, (float) measuredWidth, (float) childCount);
                        }
                        ((TextureView) childAt).setTransform(this.matrix);
                        return;
                    }
                }
            }
        }
    }

    public void setAspectRatio(float f, int i) {
        if (this.videoAspectRatio != f || this.rotation != i) {
            this.videoAspectRatio = f;
            this.rotation = i;
            requestLayout();
        }
    }
}
