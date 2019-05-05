package com.google.android.gms.vision.face.internal.client;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.vision.face.Face;
import com.hanista.mobogram.messenger.volley.Request.Method;
import com.hanista.mobogram.ui.Components.VideoPlayer;

public class zzc implements Creator<FaceSettingsParcel> {
    static void zza(FaceSettingsParcel faceSettingsParcel, Parcel parcel, int i) {
        int zzcr = zzb.zzcr(parcel);
        zzb.zzc(parcel, 1, faceSettingsParcel.versionCode);
        zzb.zzc(parcel, 2, faceSettingsParcel.mode);
        zzb.zzc(parcel, 3, faceSettingsParcel.aLm);
        zzb.zzc(parcel, 4, faceSettingsParcel.aLn);
        zzb.zza(parcel, 5, faceSettingsParcel.aLo);
        zzb.zza(parcel, 6, faceSettingsParcel.aLp);
        zzb.zza(parcel, 7, faceSettingsParcel.aLq);
        zzb.zzaj(parcel, zzcr);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zztd(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzabt(i);
    }

    public FaceSettingsParcel[] zzabt(int i) {
        return new FaceSettingsParcel[i];
    }

    public FaceSettingsParcel zztd(Parcel parcel) {
        boolean z = false;
        int zzcq = zza.zzcq(parcel);
        float f = Face.UNCOMPUTED_PROBABILITY;
        boolean z2 = false;
        int i = 0;
        int i2 = 0;
        int i3 = 0;
        int i4 = 0;
        while (parcel.dataPosition() < zzcq) {
            int zzcp = zza.zzcp(parcel);
            switch (zza.zzgv(zzcp)) {
                case VideoPlayer.TYPE_AUDIO /*1*/:
                    i4 = zza.zzg(parcel, zzcp);
                    break;
                case VideoPlayer.STATE_PREPARING /*2*/:
                    i3 = zza.zzg(parcel, zzcp);
                    break;
                case VideoPlayer.STATE_BUFFERING /*3*/:
                    i2 = zza.zzg(parcel, zzcp);
                    break;
                case VideoPlayer.STATE_READY /*4*/:
                    i = zza.zzg(parcel, zzcp);
                    break;
                case VideoPlayer.STATE_ENDED /*5*/:
                    z2 = zza.zzc(parcel, zzcp);
                    break;
                case Method.TRACE /*6*/:
                    z = zza.zzc(parcel, zzcp);
                    break;
                case Method.PATCH /*7*/:
                    f = zza.zzl(parcel, zzcp);
                    break;
                default:
                    zza.zzb(parcel, zzcp);
                    break;
            }
        }
        if (parcel.dataPosition() == zzcq) {
            return new FaceSettingsParcel(i4, i3, i2, i, z2, z, f);
        }
        throw new zza.zza("Overread allowed size end=" + zzcq, parcel);
    }
}
