package com.hanista.mobogram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build.VERSION;
import android.view.MotionEvent;
import android.view.View.MeasureSpec;
import android.widget.TextView;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.mobo.p008i.FontUtil;
import com.hanista.mobogram.ui.ActionBar.Theme;

public class HashtagSearchCell extends TextView {
    private static Paint paint;
    private boolean needDivider;

    public HashtagSearchCell(Context context) {
        super(context);
        setTypeface(FontUtil.m1176a().m1161d());
        setGravity(16);
        setPadding(AndroidUtilities.dp(16.0f), 0, AndroidUtilities.dp(16.0f), 0);
        setTextSize(1, 17.0f);
        setTextColor(Theme.MSG_TEXT_COLOR);
        if (paint == null) {
            paint = new Paint();
            paint.setColor(-2302756);
        }
        setBackgroundResource(C0338R.drawable.list_selector);
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.needDivider) {
            canvas.drawLine(0.0f, (float) (getHeight() - 1), (float) getWidth(), (float) (getHeight() - 1), paint);
        }
    }

    protected void onMeasure(int i, int i2) {
        setMeasuredDimension(MeasureSpec.getSize(i), AndroidUtilities.dp(48.0f) + 1);
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (VERSION.SDK_INT >= 21 && getBackground() != null && (motionEvent.getAction() == 0 || motionEvent.getAction() == 2)) {
            getBackground().setHotspot(motionEvent.getX(), motionEvent.getY());
        }
        return super.onTouchEvent(motionEvent);
    }

    public void setNeedDivider(boolean z) {
        this.needDivider = z;
    }
}
