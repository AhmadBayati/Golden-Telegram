package com.google.android.gms.internal;

import com.hanista.mobogram.tgnet.TLRPC;
import java.io.ByteArrayOutputStream;

public class zzaa extends ByteArrayOutputStream {
    private final zzu zzbq;

    public zzaa(zzu com_google_android_gms_internal_zzu, int i) {
        this.zzbq = com_google_android_gms_internal_zzu;
        this.buf = this.zzbq.zzb(Math.max(i, TLRPC.USER_FLAG_UNUSED2));
    }

    private void zzd(int i) {
        if (this.count + i > this.buf.length) {
            Object zzb = this.zzbq.zzb((this.count + i) * 2);
            System.arraycopy(this.buf, 0, zzb, 0, this.count);
            this.zzbq.zza(this.buf);
            this.buf = zzb;
        }
    }

    public void close() {
        this.zzbq.zza(this.buf);
        this.buf = null;
        super.close();
    }

    public void finalize() {
        this.zzbq.zza(this.buf);
    }

    public synchronized void write(int i) {
        zzd(1);
        super.write(i);
    }

    public synchronized void write(byte[] bArr, int i, int i2) {
        zzd(i2);
        super.write(bArr, i, i2);
    }
}
