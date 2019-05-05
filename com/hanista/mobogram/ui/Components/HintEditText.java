package com.hanista.mobogram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.widget.EditText;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;
import com.hanista.mobogram.mobo.p008i.FontUtil;
import com.hanista.mobogram.ui.ActionBar.Theme;

public class HintEditText extends EditText {
    private String hintText;
    private float numberSize;
    private Paint paint;
    private Rect rect;
    private float spaceSize;
    private float textOffset;

    public HintEditText(Context context) {
        super(context);
        this.paint = new Paint();
        this.rect = new Rect();
        this.paint.setColor(Theme.SHARE_SHEET_EDIT_PLACEHOLDER_TEXT_COLOR);
        setTypeface(FontUtil.m1176a().m1161d());
    }

    public String getHintText() {
        return this.hintText;
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.hintText != null && length() < this.hintText.length()) {
            int measuredHeight = getMeasuredHeight() / 2;
            float f = this.textOffset;
            for (int length = length(); length < this.hintText.length(); length++) {
                if (this.hintText.charAt(length) == ' ') {
                    f += this.spaceSize;
                } else {
                    this.rect.set(((int) f) + AndroidUtilities.dp(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT), measuredHeight, ((int) (this.numberSize + f)) - AndroidUtilities.dp(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT), AndroidUtilities.dp(2.0f) + measuredHeight);
                    canvas.drawRect(this.rect, this.paint);
                    f += this.numberSize;
                }
            }
        }
    }

    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        onTextChange();
    }

    public void onTextChange() {
        this.textOffset = length() > 0 ? getPaint().measureText(getText(), 0, length()) : 0.0f;
        this.spaceSize = getPaint().measureText(" ");
        this.numberSize = getPaint().measureText("1");
        invalidate();
    }

    public void setHintText(String str) {
        this.hintText = str;
        onTextChange();
        setText(getText());
    }
}
