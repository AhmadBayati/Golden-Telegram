package com.googlecode.mp4parser.h264.model;

import com.hanista.mobogram.messenger.exoplayer.util.NalUnitUtil;

public class AspectRatio {
    public static final AspectRatio Extended_SAR;
    private int value;

    static {
        Extended_SAR = new AspectRatio(NalUnitUtil.EXTENDED_SAR);
    }

    private AspectRatio(int i) {
        this.value = i;
    }

    public static AspectRatio fromValue(int i) {
        return i == Extended_SAR.value ? Extended_SAR : new AspectRatio(i);
    }

    public int getValue() {
        return this.value;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("AspectRatio{");
        stringBuilder.append("value=").append(this.value);
        stringBuilder.append('}');
        return stringBuilder.toString();
    }
}
