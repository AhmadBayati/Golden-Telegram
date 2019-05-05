package com.hanista.mobogram.mobo.voicechanger;

/* renamed from: com.hanista.mobogram.mobo.voicechanger.c */
public enum FrameType {
    Large(2.0d),
    Default(1.0d),
    Medium(0.5d),
    Small(0.25d);
    
    public final double f2580e;

    private FrameType(double d) {
        this.f2580e = d;
    }
}
