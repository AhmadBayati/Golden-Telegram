package com.google.android.gms.internal;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public final class zzapp extends zzaqa {
    private static final Writer bmx;
    private static final zzaon bmy;
    private zzaoh bmA;
    private final List<zzaoh> bmw;
    private String bmz;

    /* renamed from: com.google.android.gms.internal.zzapp.1 */
    static class C01641 extends Writer {
        C01641() {
        }

        public void close() {
            throw new AssertionError();
        }

        public void flush() {
            throw new AssertionError();
        }

        public void write(char[] cArr, int i, int i2) {
            throw new AssertionError();
        }
    }

    static {
        bmx = new C01641();
        bmy = new zzaon("closed");
    }

    public zzapp() {
        super(bmx);
        this.bmw = new ArrayList();
        this.bmA = zzaoj.bld;
    }

    private zzaoh bs() {
        return (zzaoh) this.bmw.get(this.bmw.size() - 1);
    }

    private void zzd(zzaoh com_google_android_gms_internal_zzaoh) {
        if (this.bmz != null) {
            if (!com_google_android_gms_internal_zzaoh.aV() || bK()) {
                ((zzaok) bs()).zza(this.bmz, com_google_android_gms_internal_zzaoh);
            }
            this.bmz = null;
        } else if (this.bmw.isEmpty()) {
            this.bmA = com_google_android_gms_internal_zzaoh;
        } else {
            zzaoh bs = bs();
            if (bs instanceof zzaoe) {
                ((zzaoe) bs).zzc(com_google_android_gms_internal_zzaoh);
                return;
            }
            throw new IllegalStateException();
        }
    }

    public zzaoh br() {
        if (this.bmw.isEmpty()) {
            return this.bmA;
        }
        String valueOf = String.valueOf(this.bmw);
        throw new IllegalStateException(new StringBuilder(String.valueOf(valueOf).length() + 34).append("Expected one JSON element but was ").append(valueOf).toString());
    }

    public zzaqa bt() {
        zzaoh com_google_android_gms_internal_zzaoe = new zzaoe();
        zzd(com_google_android_gms_internal_zzaoe);
        this.bmw.add(com_google_android_gms_internal_zzaoe);
        return this;
    }

    public zzaqa bu() {
        if (this.bmw.isEmpty() || this.bmz != null) {
            throw new IllegalStateException();
        } else if (bs() instanceof zzaoe) {
            this.bmw.remove(this.bmw.size() - 1);
            return this;
        } else {
            throw new IllegalStateException();
        }
    }

    public zzaqa bv() {
        zzaoh com_google_android_gms_internal_zzaok = new zzaok();
        zzd(com_google_android_gms_internal_zzaok);
        this.bmw.add(com_google_android_gms_internal_zzaok);
        return this;
    }

    public zzaqa bw() {
        if (this.bmw.isEmpty() || this.bmz != null) {
            throw new IllegalStateException();
        } else if (bs() instanceof zzaok) {
            this.bmw.remove(this.bmw.size() - 1);
            return this;
        } else {
            throw new IllegalStateException();
        }
    }

    public zzaqa bx() {
        zzd(zzaoj.bld);
        return this;
    }

    public void close() {
        if (this.bmw.isEmpty()) {
            this.bmw.add(bmy);
            return;
        }
        throw new IOException("Incomplete document");
    }

    public void flush() {
    }

    public zzaqa zza(Number number) {
        if (number == null) {
            return bx();
        }
        if (!isLenient()) {
            double doubleValue = number.doubleValue();
            if (Double.isNaN(doubleValue) || Double.isInfinite(doubleValue)) {
                String valueOf = String.valueOf(number);
                throw new IllegalArgumentException(new StringBuilder(String.valueOf(valueOf).length() + 33).append("JSON forbids NaN and infinities: ").append(valueOf).toString());
            }
        }
        zzd(new zzaon(number));
        return this;
    }

    public zzaqa zzcu(long j) {
        zzd(new zzaon(Long.valueOf(j)));
        return this;
    }

    public zzaqa zzdf(boolean z) {
        zzd(new zzaon(Boolean.valueOf(z)));
        return this;
    }

    public zzaqa zzus(String str) {
        if (this.bmw.isEmpty() || this.bmz != null) {
            throw new IllegalStateException();
        } else if (bs() instanceof zzaok) {
            this.bmz = str;
            return this;
        } else {
            throw new IllegalStateException();
        }
    }

    public zzaqa zzut(String str) {
        if (str == null) {
            return bx();
        }
        zzd(new zzaon(str));
        return this;
    }
}
