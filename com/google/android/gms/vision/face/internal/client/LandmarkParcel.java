package com.google.android.gms.vision.face.internal.client;

import android.os.Parcel;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;

public final class LandmarkParcel extends AbstractSafeParcelable {
    public static final zzf CREATOR;
    public final int type;
    public final int versionCode;
    public final float f11x;
    public final float f12y;

    static {
        CREATOR = new zzf();
    }

    public LandmarkParcel(int i, float f, float f2, int i2) {
        this.versionCode = i;
        this.f11x = f;
        this.f12y = f2;
        this.type = i2;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzf.zza(this, parcel, i);
    }
}
