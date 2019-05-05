package com.hanista.mobogram.mobo.voicechanger.dsp.processors;

import android.content.Context;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;
import com.hanista.mobogram.mobo.voicechanger.Preferences;
import com.hanista.mobogram.mobo.voicechanger.dsp.Math;

/* renamed from: com.hanista.mobogram.mobo.voicechanger.dsp.processors.a */
public final class AmplifyProcessor {
    private final float f2618a;

    public AmplifyProcessor(int i) {
        this.f2618a = Math.pow(10.0f, ((float) i) / 20.0f);
    }

    public AmplifyProcessor(Context context) {
        this(new Preferences(context).m2548a());
    }

    public void m2579a(short[] sArr) {
        if (this.f2618a != DefaultRetryPolicy.DEFAULT_BACKOFF_MULT) {
            for (int i = 0; i < sArr.length; i++) {
                float f = ((float) sArr[i]) * this.f2618a;
                if (f > 32767.0f) {
                    sArr[i] = Short.MAX_VALUE;
                } else if (f < -32768.0f) {
                    sArr[i] = Short.MIN_VALUE;
                } else {
                    sArr[i] = (short) ((int) f);
                }
            }
        }
    }
}
