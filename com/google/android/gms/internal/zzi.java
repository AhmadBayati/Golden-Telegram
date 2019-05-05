package com.google.android.gms.internal;

import com.hanista.mobogram.messenger.support.widget.helper.ItemTouchHelper.Callback;
import java.util.Map;

public class zzi {
    public final byte[] data;
    public final int statusCode;
    public final boolean zzaa;
    public final long zzab;
    public final Map<String, String> zzz;

    public zzi(int i, byte[] bArr, Map<String, String> map, boolean z, long j) {
        this.statusCode = i;
        this.data = bArr;
        this.zzz = map;
        this.zzaa = z;
        this.zzab = j;
    }

    public zzi(byte[] bArr, Map<String, String> map) {
        this(Callback.DEFAULT_DRAG_ANIMATION_DURATION, bArr, map, false, 0);
    }
}
