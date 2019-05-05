package com.hanista.mobogram.messenger.volley;

import com.hanista.mobogram.messenger.support.widget.helper.ItemTouchHelper.Callback;
import java.util.Collections;
import java.util.Map;

public class NetworkResponse {
    public final byte[] data;
    public final Map<String, String> headers;
    public final long networkTimeMs;
    public final boolean notModified;
    public final int statusCode;

    public NetworkResponse(int i, byte[] bArr, Map<String, String> map, boolean z) {
        this(i, bArr, map, z, 0);
    }

    public NetworkResponse(int i, byte[] bArr, Map<String, String> map, boolean z, long j) {
        this.statusCode = i;
        this.data = bArr;
        this.headers = map;
        this.notModified = z;
        this.networkTimeMs = j;
    }

    public NetworkResponse(byte[] bArr) {
        this(Callback.DEFAULT_DRAG_ANIMATION_DURATION, bArr, Collections.emptyMap(), false, 0);
    }

    public NetworkResponse(byte[] bArr, Map<String, String> map) {
        this(Callback.DEFAULT_DRAG_ANIMATION_DURATION, bArr, map, false, 0);
    }
}
