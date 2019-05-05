package com.google.android.gms.maps.model;

import android.os.Parcel;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;

public final class PointOfInterest extends AbstractSafeParcelable {
    public static final zzh CREATOR;
    public final LatLng latLng;
    private final int mVersionCode;
    public final String name;
    public final String placeId;

    static {
        CREATOR = new zzh();
    }

    PointOfInterest(int i, LatLng latLng, String str, String str2) {
        this.mVersionCode = i;
        this.latLng = latLng;
        this.placeId = str;
        this.name = str2;
    }

    public PointOfInterest(LatLng latLng, String str, String str2) {
        this(1, latLng, str, str2);
    }

    int getVersionCode() {
        return this.mVersionCode;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzh.zza(this, parcel, i);
    }
}
