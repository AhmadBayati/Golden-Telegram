package com.hanista.mobogram.mobo.markers;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ImageView;

public class LogoView extends ImageView {
    public LogoView(Context context) {
        this(context, null);
    }

    public LogoView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public LogoView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    protected boolean fitSystemWindows(Rect rect) {
        DecorTracker.m1816a().m1817a(rect);
        return true;
    }
}
