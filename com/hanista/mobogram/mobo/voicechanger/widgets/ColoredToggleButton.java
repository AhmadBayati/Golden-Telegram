package com.hanista.mobogram.mobo.voicechanger.widgets;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.StateListDrawable;
import android.util.AttributeSet;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ToggleButton;
import com.hanista.mobogram.messenger.exoplayer.util.NalUnitUtil;

public final class ColoredToggleButton extends ToggleButton implements OnCheckedChangeListener {
    private static final int f2645a;
    private static final int f2646b;

    static {
        f2645a = Color.rgb(NalUnitUtil.EXTENDED_SAR, 25, 0);
        f2646b = Color.rgb(0, 184, 44);
    }

    public ColoredToggleButton(Context context) {
        super(context);
        m2592a();
    }

    public ColoredToggleButton(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        m2592a();
    }

    public ColoredToggleButton(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        m2592a();
    }

    private void m2592a() {
        setOnCheckedChangeListener(this);
        onCheckedChanged(this, isChecked());
    }

    public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
        LayerDrawable layerDrawable = (LayerDrawable) ((StateListDrawable) getBackground()).getCurrent();
        Drawable drawable = layerDrawable.getDrawable(1);
        if (z) {
            layerDrawable.setColorFilter(f2645a, Mode.SRC_ATOP);
            drawable.setColorFilter(-1, Mode.SRC_IN);
            return;
        }
        layerDrawable.setColorFilter(f2646b, Mode.SRC_ATOP);
        drawable.setColorFilter(-1, Mode.SRC_IN);
    }
}
