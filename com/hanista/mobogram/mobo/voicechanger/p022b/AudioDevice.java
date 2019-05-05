package com.hanista.mobogram.mobo.voicechanger.p022b;

import android.content.Context;
import com.hanista.mobogram.mobo.voicechanger.Preferences;
import com.hanista.mobogram.mobo.voicechanger.Utils;

/* renamed from: com.hanista.mobogram.mobo.voicechanger.b.a */
public abstract class AudioDevice {
    protected final Context f2560a;
    private int f2561b;

    public AudioDevice(Context context) {
        this(context, new Preferences(context).m2551b());
    }

    public AudioDevice(Context context, int i) {
        this.f2560a = context;
        this.f2561b = i;
        new Utils(context).m2590a("Current sample rate is %s Hz.", Integer.valueOf(i));
    }

    public Context m2528a() {
        return this.f2560a;
    }

    protected void m2529a(int i) {
        this.f2561b = i;
    }

    public int m2530b() {
        return this.f2561b;
    }

    public void m2531c() {
    }
}
