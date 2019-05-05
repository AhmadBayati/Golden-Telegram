package com.hanista.mobogram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View.MeasureSpec;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;
import com.hanista.mobogram.mobo.p020s.AdvanceTheme;
import com.hanista.mobogram.mobo.p020s.ThemeUtil;

public class DividerCell extends BaseCell {
    private static Paint paint;

    public DividerCell(Context context) {
        super(context);
        if (paint == null) {
            paint = new Paint();
            paint.setColor(-2500135);
            paint.setStrokeWidth(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        }
    }

    protected void onDraw(Canvas canvas) {
        canvas.drawLine((float) getPaddingLeft(), (float) AndroidUtilities.dp(8.0f), (float) (getWidth() - getPaddingRight()), (float) AndroidUtilities.dp(8.0f), paint);
    }

    protected void onMeasure(int i, int i2) {
        setMeasuredDimension(MeasureSpec.getSize(i), AndroidUtilities.dp(16.0f) + 1);
        if (ThemeUtil.m2490b()) {
            String obj = getTag() != null ? getTag().toString() : null;
            if (obj != null) {
                paint.setColor(AdvanceTheme.m2278a(obj, -2500135));
                if (obj.contains("00")) {
                    paint.setColor(0);
                }
            }
        }
    }
}
