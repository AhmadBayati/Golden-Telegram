package com.google.android.gms.maps.model;

import android.os.Parcel;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;

public final class Tile extends AbstractSafeParcelable {
    public static final zzo CREATOR;
    public final byte[] data;
    public final int height;
    private final int mVersionCode;
    public final int width;

    static {
        CREATOR = new zzo();
    }

    Tile(int i, int i2, int i3, byte[] bArr) {
        this.mVersionCode = i;
        this.width = i2;
        this.height = i3;
        this.data = bArr;
    }

    public Tile(int i, int i2, byte[] bArr) {
        this(1, i, i2, bArr);
    }

    int getVersionCode() {
        return this.mVersionCode;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzo.zza(this, parcel, i);
    }
}
