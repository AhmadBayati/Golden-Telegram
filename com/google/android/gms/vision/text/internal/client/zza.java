package com.google.android.gms.vision.text.internal.client;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.hanista.mobogram.messenger.volley.Request.Method;
import com.hanista.mobogram.ui.Components.VideoPlayer;

public class zza implements Creator<BoundingBoxParcel> {
    static void zza(BoundingBoxParcel boundingBoxParcel, Parcel parcel, int i) {
        int zzcr = zzb.zzcr(parcel);
        zzb.zzc(parcel, 1, boundingBoxParcel.versionCode);
        zzb.zzc(parcel, 2, boundingBoxParcel.left);
        zzb.zzc(parcel, 3, boundingBoxParcel.top);
        zzb.zzc(parcel, 4, boundingBoxParcel.width);
        zzb.zzc(parcel, 5, boundingBoxParcel.height);
        zzb.zza(parcel, 6, boundingBoxParcel.aLE);
        zzb.zzaj(parcel, zzcr);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zztg(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzabx(i);
    }

    public BoundingBoxParcel[] zzabx(int i) {
        return new BoundingBoxParcel[i];
    }

    public BoundingBoxParcel zztg(Parcel parcel) {
        int i = 0;
        int zzcq = com.google.android.gms.common.internal.safeparcel.zza.zzcq(parcel);
        float f = 0.0f;
        int i2 = 0;
        int i3 = 0;
        int i4 = 0;
        int i5 = 0;
        while (parcel.dataPosition() < zzcq) {
            int zzcp = com.google.android.gms.common.internal.safeparcel.zza.zzcp(parcel);
            switch (com.google.android.gms.common.internal.safeparcel.zza.zzgv(zzcp)) {
                case VideoPlayer.TYPE_AUDIO /*1*/:
                    i5 = com.google.android.gms.common.internal.safeparcel.zza.zzg(parcel, zzcp);
                    break;
                case VideoPlayer.STATE_PREPARING /*2*/:
                    i4 = com.google.android.gms.common.internal.safeparcel.zza.zzg(parcel, zzcp);
                    break;
                case VideoPlayer.STATE_BUFFERING /*3*/:
                    i3 = com.google.android.gms.common.internal.safeparcel.zza.zzg(parcel, zzcp);
                    break;
                case VideoPlayer.STATE_READY /*4*/:
                    i2 = com.google.android.gms.common.internal.safeparcel.zza.zzg(parcel, zzcp);
                    break;
                case VideoPlayer.STATE_ENDED /*5*/:
                    i = com.google.android.gms.common.internal.safeparcel.zza.zzg(parcel, zzcp);
                    break;
                case Method.TRACE /*6*/:
                    f = com.google.android.gms.common.internal.safeparcel.zza.zzl(parcel, zzcp);
                    break;
                default:
                    com.google.android.gms.common.internal.safeparcel.zza.zzb(parcel, zzcp);
                    break;
            }
        }
        if (parcel.dataPosition() == zzcq) {
            return new BoundingBoxParcel(i5, i4, i3, i2, i, f);
        }
        throw new com.google.android.gms.common.internal.safeparcel.zza.zza("Overread allowed size end=" + zzcq, parcel);
    }
}
