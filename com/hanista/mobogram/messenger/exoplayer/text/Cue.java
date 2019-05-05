package com.hanista.mobogram.messenger.exoplayer.text;

import android.text.Layout.Alignment;

public class Cue {
    public static final int ANCHOR_TYPE_END = 2;
    public static final int ANCHOR_TYPE_MIDDLE = 1;
    public static final int ANCHOR_TYPE_START = 0;
    public static final float DIMEN_UNSET = Float.MIN_VALUE;
    public static final int LINE_TYPE_FRACTION = 0;
    public static final int LINE_TYPE_NUMBER = 1;
    public static final int TYPE_UNSET = Integer.MIN_VALUE;
    public final float line;
    public final int lineAnchor;
    public final int lineType;
    public final float position;
    public final int positionAnchor;
    public final float size;
    public final CharSequence text;
    public final Alignment textAlignment;

    public Cue() {
        this(null);
    }

    public Cue(CharSequence charSequence) {
        this(charSequence, null, DIMEN_UNSET, TYPE_UNSET, TYPE_UNSET, DIMEN_UNSET, TYPE_UNSET, DIMEN_UNSET);
    }

    public Cue(CharSequence charSequence, Alignment alignment, float f, int i, int i2, float f2, int i3, float f3) {
        this.text = charSequence;
        this.textAlignment = alignment;
        this.line = f;
        this.lineType = i;
        this.lineAnchor = i2;
        this.position = f2;
        this.positionAnchor = i3;
        this.size = f3;
    }
}
