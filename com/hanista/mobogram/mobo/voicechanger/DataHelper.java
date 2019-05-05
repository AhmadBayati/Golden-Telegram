package com.hanista.mobogram.mobo.voicechanger;

import android.media.AudioRecord;

/* renamed from: com.hanista.mobogram.mobo.voicechanger.b */
public class DataHelper {
    public int f2572a;
    public boolean f2573b;
    private AudioRecord f2574c;

    public DataHelper(AudioRecord audioRecord) {
        this.f2572a = -1;
        this.f2574c = null;
        this.f2574c = audioRecord;
    }

    public int m2545a(short[] sArr, int i, int i2) {
        return i2 == 0 ? 0 : this.f2574c.read(sArr, i, i2);
    }

    public void m2546a(short[] sArr) {
        this.f2572a = 0;
        this.f2573b = true;
        m2547b(sArr);
    }

    public final boolean m2547b(short[] sArr) {
        if (sArr == null || sArr.length == 0) {
            return false;
        }
        int i = 0;
        do {
            int a = m2545a(sArr, i, sArr.length - i);
            this.f2572a = a;
            if (a <= 0) {
                return false;
            }
            i += a;
        } while (i < sArr.length);
        return true;
    }
}
