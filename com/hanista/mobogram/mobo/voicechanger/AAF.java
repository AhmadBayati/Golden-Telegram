package com.hanista.mobogram.mobo.voicechanger;

/* renamed from: com.hanista.mobogram.mobo.voicechanger.a */
public enum AAF {
    FAF,
    DAF,
    UnprocessedFeedback;
    
    private static final AAF[] f2558d;

    static {
        f2558d = AAF.values();
    }

    public static int m2526a() {
        return f2558d.length;
    }

    public static AAF m2527a(int i) {
        return f2558d[i];
    }
}
