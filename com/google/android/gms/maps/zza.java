package com.google.android.gms.maps;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLngBounds;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.exoplayer.upstream.NetworkLock;
import com.hanista.mobogram.messenger.volley.Request.Method;
import com.hanista.mobogram.tgnet.TLRPC;
import com.hanista.mobogram.ui.Components.VideoPlayer;

public class zza implements Creator<GoogleMapOptions> {
    static void zza(GoogleMapOptions googleMapOptions, Parcel parcel, int i) {
        int zzcr = zzb.zzcr(parcel);
        zzb.zzc(parcel, 1, googleMapOptions.getVersionCode());
        zzb.zza(parcel, 2, googleMapOptions.zzbrj());
        zzb.zza(parcel, 3, googleMapOptions.zzbrk());
        zzb.zzc(parcel, 4, googleMapOptions.getMapType());
        zzb.zza(parcel, 5, googleMapOptions.getCamera(), i, false);
        zzb.zza(parcel, 6, googleMapOptions.zzbrl());
        zzb.zza(parcel, 7, googleMapOptions.zzbrm());
        zzb.zza(parcel, 8, googleMapOptions.zzbrn());
        zzb.zza(parcel, 9, googleMapOptions.zzbro());
        zzb.zza(parcel, 10, googleMapOptions.zzbrp());
        zzb.zza(parcel, 11, googleMapOptions.zzbrq());
        zzb.zza(parcel, 12, googleMapOptions.zzbrr());
        zzb.zza(parcel, 14, googleMapOptions.zzbrs());
        zzb.zza(parcel, 15, googleMapOptions.zzbrt());
        zzb.zza(parcel, 16, googleMapOptions.getMinZoomPreference(), false);
        zzb.zza(parcel, 17, googleMapOptions.getMaxZoomPreference(), false);
        zzb.zza(parcel, 18, googleMapOptions.getLatLngBoundsForCameraTarget(), i, false);
        zzb.zzaj(parcel, zzcr);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzol(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzvq(i);
    }

    public GoogleMapOptions zzol(Parcel parcel) {
        int zzcq = com.google.android.gms.common.internal.safeparcel.zza.zzcq(parcel);
        int i = 0;
        byte b = (byte) -1;
        byte b2 = (byte) -1;
        int i2 = 0;
        CameraPosition cameraPosition = null;
        byte b3 = (byte) -1;
        byte b4 = (byte) -1;
        byte b5 = (byte) -1;
        byte b6 = (byte) -1;
        byte b7 = (byte) -1;
        byte b8 = (byte) -1;
        byte b9 = (byte) -1;
        byte b10 = (byte) -1;
        byte b11 = (byte) -1;
        Float f = null;
        Float f2 = null;
        LatLngBounds latLngBounds = null;
        while (parcel.dataPosition() < zzcq) {
            int zzcp = com.google.android.gms.common.internal.safeparcel.zza.zzcp(parcel);
            switch (com.google.android.gms.common.internal.safeparcel.zza.zzgv(zzcp)) {
                case VideoPlayer.TYPE_AUDIO /*1*/:
                    i = com.google.android.gms.common.internal.safeparcel.zza.zzg(parcel, zzcp);
                    break;
                case VideoPlayer.STATE_PREPARING /*2*/:
                    b = com.google.android.gms.common.internal.safeparcel.zza.zze(parcel, zzcp);
                    break;
                case VideoPlayer.STATE_BUFFERING /*3*/:
                    b2 = com.google.android.gms.common.internal.safeparcel.zza.zze(parcel, zzcp);
                    break;
                case VideoPlayer.STATE_READY /*4*/:
                    i2 = com.google.android.gms.common.internal.safeparcel.zza.zzg(parcel, zzcp);
                    break;
                case VideoPlayer.STATE_ENDED /*5*/:
                    cameraPosition = (CameraPosition) com.google.android.gms.common.internal.safeparcel.zza.zza(parcel, zzcp, CameraPosition.CREATOR);
                    break;
                case Method.TRACE /*6*/:
                    b3 = com.google.android.gms.common.internal.safeparcel.zza.zze(parcel, zzcp);
                    break;
                case Method.PATCH /*7*/:
                    b4 = com.google.android.gms.common.internal.safeparcel.zza.zze(parcel, zzcp);
                    break;
                case TLRPC.USER_FLAG_USERNAME /*8*/:
                    b5 = com.google.android.gms.common.internal.safeparcel.zza.zze(parcel, zzcp);
                    break;
                case C0338R.styleable.PromptView_iconTint /*9*/:
                    b6 = com.google.android.gms.common.internal.safeparcel.zza.zze(parcel, zzcp);
                    break;
                case NetworkLock.DOWNLOAD_PRIORITY /*10*/:
                    b7 = com.google.android.gms.common.internal.safeparcel.zza.zze(parcel, zzcp);
                    break;
                case C0338R.styleable.PromptView_maxTextWidth /*11*/:
                    b8 = com.google.android.gms.common.internal.safeparcel.zza.zze(parcel, zzcp);
                    break;
                case Atom.FULL_HEADER_SIZE /*12*/:
                    b9 = com.google.android.gms.common.internal.safeparcel.zza.zze(parcel, zzcp);
                    break;
                case C0338R.styleable.PromptView_primaryTextFontFamily /*14*/:
                    b10 = com.google.android.gms.common.internal.safeparcel.zza.zze(parcel, zzcp);
                    break;
                case C0338R.styleable.PromptView_primaryTextSize /*15*/:
                    b11 = com.google.android.gms.common.internal.safeparcel.zza.zze(parcel, zzcp);
                    break;
                case TLRPC.USER_FLAG_PHONE /*16*/:
                    f = com.google.android.gms.common.internal.safeparcel.zza.zzm(parcel, zzcp);
                    break;
                case C0338R.styleable.PromptView_primaryTextTypeface /*17*/:
                    f2 = com.google.android.gms.common.internal.safeparcel.zza.zzm(parcel, zzcp);
                    break;
                case C0338R.styleable.PromptView_secondaryText /*18*/:
                    latLngBounds = (LatLngBounds) com.google.android.gms.common.internal.safeparcel.zza.zza(parcel, zzcp, LatLngBounds.CREATOR);
                    break;
                default:
                    com.google.android.gms.common.internal.safeparcel.zza.zzb(parcel, zzcp);
                    break;
            }
        }
        if (parcel.dataPosition() == zzcq) {
            return new GoogleMapOptions(i, b, b2, i2, cameraPosition, b3, b4, b5, b6, b7, b8, b9, b10, b11, f, f2, latLngBounds);
        }
        throw new com.google.android.gms.common.internal.safeparcel.zza.zza("Overread allowed size end=" + zzcq, parcel);
    }

    public GoogleMapOptions[] zzvq(int i) {
        return new GoogleMapOptions[i];
    }
}
