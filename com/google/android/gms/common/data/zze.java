package com.google.android.gms.common.data;

import android.database.CursorWindow;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.support.v4.view.PointerIconCompat;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.hanista.mobogram.ui.Components.VideoPlayer;

public class zze implements Creator<DataHolder> {
    static void zza(DataHolder dataHolder, Parcel parcel, int i) {
        int zzcr = zzb.zzcr(parcel);
        zzb.zza(parcel, 1, dataHolder.zzatf(), false);
        zzb.zza(parcel, 2, dataHolder.zzatg(), i, false);
        zzb.zzc(parcel, 3, dataHolder.getStatusCode());
        zzb.zza(parcel, 4, dataHolder.zzasz(), false);
        zzb.zzc(parcel, PointerIconCompat.TYPE_DEFAULT, dataHolder.getVersionCode());
        zzb.zzaj(parcel, zzcr);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzcg(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzge(i);
    }

    public DataHolder zzcg(Parcel parcel) {
        int i = 0;
        Bundle bundle = null;
        int zzcq = zza.zzcq(parcel);
        CursorWindow[] cursorWindowArr = null;
        String[] strArr = null;
        int i2 = 0;
        while (parcel.dataPosition() < zzcq) {
            int zzcp = zza.zzcp(parcel);
            switch (zza.zzgv(zzcp)) {
                case VideoPlayer.TYPE_AUDIO /*1*/:
                    strArr = zza.zzac(parcel, zzcp);
                    break;
                case VideoPlayer.STATE_PREPARING /*2*/:
                    cursorWindowArr = (CursorWindow[]) zza.zzb(parcel, zzcp, CursorWindow.CREATOR);
                    break;
                case VideoPlayer.STATE_BUFFERING /*3*/:
                    i = zza.zzg(parcel, zzcp);
                    break;
                case VideoPlayer.STATE_READY /*4*/:
                    bundle = zza.zzs(parcel, zzcp);
                    break;
                case PointerIconCompat.TYPE_DEFAULT /*1000*/:
                    i2 = zza.zzg(parcel, zzcp);
                    break;
                default:
                    zza.zzb(parcel, zzcp);
                    break;
            }
        }
        if (parcel.dataPosition() != zzcq) {
            throw new zza.zza("Overread allowed size end=" + zzcq, parcel);
        }
        DataHolder dataHolder = new DataHolder(i2, strArr, cursorWindowArr, i, bundle);
        dataHolder.zzate();
        return dataHolder;
    }

    public DataHolder[] zzge(int i) {
        return new DataHolder[i];
    }
}
