package com.google.android.gms.common.api;

import android.app.PendingIntent;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.support.v4.view.PointerIconCompat;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.hanista.mobogram.ui.Components.VideoPlayer;

public class zzh implements Creator<Status> {
    static void zza(Status status, Parcel parcel, int i) {
        int zzcr = zzb.zzcr(parcel);
        zzb.zzc(parcel, 1, status.getStatusCode());
        zzb.zza(parcel, 2, status.getStatusMessage(), false);
        zzb.zza(parcel, 3, status.zzaqh(), i, false);
        zzb.zzc(parcel, PointerIconCompat.TYPE_DEFAULT, status.getVersionCode());
        zzb.zzaj(parcel, zzcr);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzce(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzfp(i);
    }

    public Status zzce(Parcel parcel) {
        PendingIntent pendingIntent = null;
        int i = 0;
        int zzcq = zza.zzcq(parcel);
        String str = null;
        int i2 = 0;
        while (parcel.dataPosition() < zzcq) {
            int zzcp = zza.zzcp(parcel);
            switch (zza.zzgv(zzcp)) {
                case VideoPlayer.TYPE_AUDIO /*1*/:
                    i = zza.zzg(parcel, zzcp);
                    break;
                case VideoPlayer.STATE_PREPARING /*2*/:
                    str = zza.zzq(parcel, zzcp);
                    break;
                case VideoPlayer.STATE_BUFFERING /*3*/:
                    pendingIntent = (PendingIntent) zza.zza(parcel, zzcp, PendingIntent.CREATOR);
                    break;
                case PointerIconCompat.TYPE_DEFAULT /*1000*/:
                    i2 = zza.zzg(parcel, zzcp);
                    break;
                default:
                    zza.zzb(parcel, zzcp);
                    break;
            }
        }
        if (parcel.dataPosition() == zzcq) {
            return new Status(i2, i, str, pendingIntent);
        }
        throw new zza.zza("Overread allowed size end=" + zzcq, parcel);
    }

    public Status[] zzfp(int i) {
        return new Status[i];
    }
}
