package com.hanista.mobogram.mobo.persianmaterialdatetimepicker.date;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.widget.TextView;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.exoplayer.util.NalUnitUtil;
import com.hanista.mobogram.mobo.persianmaterialdatetimepicker.p017a.LanguageUtils;

public class TextViewWithCircularIndicator extends TextView {
    Paint f2129a;
    private final int f2130b;
    private final int f2131c;
    private final String f2132d;
    private boolean f2133e;

    public TextViewWithCircularIndicator(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.f2129a = new Paint();
        Resources resources = context.getResources();
        this.f2131c = resources.getColor(C0338R.color.mdtp_accent_color);
        this.f2130b = resources.getDimensionPixelOffset(C0338R.dimen.mdtp_month_select_circle_radius);
        this.f2132d = context.getResources().getString(C0338R.string.mdtp_item_is_selected);
        m2074a();
    }

    private void m2074a() {
        this.f2129a.setFakeBoldText(true);
        this.f2129a.setAntiAlias(true);
        this.f2129a.setColor(this.f2131c);
        this.f2129a.setTextAlign(Align.CENTER);
        this.f2129a.setStyle(Style.FILL);
        this.f2129a.setAlpha(NalUnitUtil.EXTENDED_SAR);
    }

    public void m2075a(boolean z) {
        this.f2133e = z;
    }

    public CharSequence getContentDescription() {
        CharSequence a = LanguageUtils.m2043a(getText().toString());
        if (!this.f2133e) {
            return a;
        }
        return String.format(this.f2132d, new Object[]{a});
    }

    public void onDraw(@NonNull Canvas canvas) {
        if (this.f2133e) {
            int width = getWidth();
            int height = getHeight();
            canvas.drawCircle((float) (width / 2), (float) (height / 2), (float) (Math.min(width, height) / 2), this.f2129a);
        }
        setSelected(this.f2133e);
        super.onDraw(canvas);
    }
}
