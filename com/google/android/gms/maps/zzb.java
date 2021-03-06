package com.google.android.gms.maps;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.StreetViewPanoramaCamera;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.exoplayer.upstream.NetworkLock;
import com.hanista.mobogram.messenger.volley.Request.Method;
import com.hanista.mobogram.tgnet.TLRPC;
import com.hanista.mobogram.ui.Components.VideoPlayer;

public class zzb implements Creator<StreetViewPanoramaOptions> {
    static void zza(StreetViewPanoramaOptions streetViewPanoramaOptions, Parcel parcel, int i) {
        int zzcr = com.google.android.gms.common.internal.safeparcel.zzb.zzcr(parcel);
        com.google.android.gms.common.internal.safeparcel.zzb.zzc(parcel, 1, streetViewPanoramaOptions.getVersionCode());
        com.google.android.gms.common.internal.safeparcel.zzb.zza(parcel, 2, streetViewPanoramaOptions.getStreetViewPanoramaCamera(), i, false);
        com.google.android.gms.common.internal.safeparcel.zzb.zza(parcel, 3, streetViewPanoramaOptions.getPanoramaId(), false);
        com.google.android.gms.common.internal.safeparcel.zzb.zza(parcel, 4, streetViewPanoramaOptions.getPosition(), i, false);
        com.google.android.gms.common.internal.safeparcel.zzb.zza(parcel, 5, streetViewPanoramaOptions.getRadius(), false);
        com.google.android.gms.common.internal.safeparcel.zzb.zza(parcel, 6, streetViewPanoramaOptions.zzbry());
        com.google.android.gms.common.internal.safeparcel.zzb.zza(parcel, 7, streetViewPanoramaOptions.zzbro());
        com.google.android.gms.common.internal.safeparcel.zzb.zza(parcel, 8, streetViewPanoramaOptions.zzbrz());
        com.google.android.gms.common.internal.safeparcel.zzb.zza(parcel, 9, streetViewPanoramaOptions.zzbsa());
        com.google.android.gms.common.internal.safeparcel.zzb.zza(parcel, 10, streetViewPanoramaOptions.zzbrk());
        com.google.android.gms.common.internal.safeparcel.zzb.zzaj(parcel, zzcr);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzom(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzvr(i);
    }

    public StreetViewPanoramaOptions zzom(Parcel parcel) {
        Integer num = null;
        byte b = (byte) 0;
        int zzcq = zza.zzcq(parcel);
        byte b2 = (byte) 0;
        byte b3 = (byte) 0;
        byte b4 = (byte) 0;
        byte b5 = (byte) 0;
        LatLng latLng = null;
        String str = null;
        StreetViewPanoramaCamera streetViewPanoramaCamera = null;
        int i = 0;
        while (parcel.dataPosition() < zzcq) {
            int zzcp = zza.zzcp(parcel);
            switch (zza.zzgv(zzcp)) {
                case VideoPlayer.TYPE_AUDIO /*1*/:
                    i = zza.zzg(parcel, zzcp);
                    break;
                case VideoPlayer.STATE_PREPARING /*2*/:
                    streetViewPanoramaCamera = (StreetViewPanoramaCamera) zza.zza(parcel, zzcp, StreetViewPanoramaCamera.CREATOR);
                    break;
                case VideoPlayer.STATE_BUFFERING /*3*/:
                    str = zza.zzq(parcel, zzcp);
                    break;
                case VideoPlayer.STATE_READY /*4*/:
                    latLng = (LatLng) zza.zza(parcel, zzcp, LatLng.CREATOR);
                    break;
                case VideoPlayer.STATE_ENDED /*5*/:
                    num = zza.zzh(parcel, zzcp);
                    break;
                case Method.TRACE /*6*/:
                    b5 = zza.zze(parcel, zzcp);
                    break;
                case Method.PATCH /*7*/:
                    b4 = zza.zze(parcel, zzcp);
                    break;
                case TLRPC.USER_FLAG_USERNAME /*8*/:
                    b3 = zza.zze(parcel, zzcp);
                    break;
                case C0338R.styleable.PromptView_iconTint /*9*/:
                    b2 = zza.zze(parcel, zzcp);
                    break;
                case NetworkLock.DOWNLOAD_PRIORITY /*10*/:
                    b = zza.zze(parcel, zzcp);
                    break;
                default:
                    zza.zzb(parcel, zzcp);
                    break;
            }
        }
        if (parcel.dataPosition() == zzcq) {
            return new StreetViewPanoramaOptions(i, streetViewPanoramaCamera, str, latLng, num, b5, b4, b3, b2, b);
        }
        throw new zza.zza("Overread allowed size end=" + zzcq, parcel);
    }

    public StreetViewPanoramaOptions[] zzvr(int i) {
        return new StreetViewPanoramaOptions[i];
    }
}
