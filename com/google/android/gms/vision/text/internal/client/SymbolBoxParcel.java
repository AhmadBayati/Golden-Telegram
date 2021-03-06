package com.google.android.gms.vision.text.internal.client;

import android.os.Parcel;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;

public class SymbolBoxParcel extends AbstractSafeParcelable {
    public static final zzf CREATOR;
    public final int versionCode;

    static {
        CREATOR = new zzf();
    }

    public SymbolBoxParcel(int i) {
        this.versionCode = i;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzf.zza(this, parcel, i);
    }
}
