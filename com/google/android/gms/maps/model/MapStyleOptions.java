package com.google.android.gms.maps.model;

import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.os.Parcel;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.util.zzo;
import com.hanista.mobogram.messenger.exoplayer.C0700C;
import java.io.IOException;

public final class MapStyleOptions extends AbstractSafeParcelable {
    public static final zzf CREATOR;
    private static final String TAG;
    private String amU;
    private final int mVersionCode;

    static {
        TAG = MapStyleOptions.class.getSimpleName();
        CREATOR = new zzf();
    }

    MapStyleOptions(int i, String str) {
        this.mVersionCode = i;
        this.amU = str;
    }

    public MapStyleOptions(String str) {
        this.mVersionCode = 1;
        this.amU = str;
    }

    public static MapStyleOptions loadRawResourceStyle(Context context, int i) {
        try {
            return new MapStyleOptions(new String(zzo.zzl(context.getResources().openRawResource(i)), C0700C.UTF8_NAME));
        } catch (IOException e) {
            String valueOf = String.valueOf(e);
            throw new NotFoundException(new StringBuilder(String.valueOf(valueOf).length() + 37).append("Failed to read resource ").append(i).append(": ").append(valueOf).toString());
        }
    }

    int getVersionCode() {
        return this.mVersionCode;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzf.zza(this, parcel, i);
    }

    public String zzbsi() {
        return this.amU;
    }
}
