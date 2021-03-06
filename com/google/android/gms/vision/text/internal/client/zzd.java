package com.google.android.gms.vision.text.internal.client;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.exoplayer.upstream.NetworkLock;
import com.hanista.mobogram.messenger.volley.Request.Method;
import com.hanista.mobogram.tgnet.TLRPC;
import com.hanista.mobogram.ui.Components.VideoPlayer;

public class zzd implements Creator<LineBoxParcel> {
    static void zza(LineBoxParcel lineBoxParcel, Parcel parcel, int i) {
        int zzcr = zzb.zzcr(parcel);
        zzb.zzc(parcel, 1, lineBoxParcel.versionCode);
        zzb.zza(parcel, 2, lineBoxParcel.aLF, i, false);
        zzb.zza(parcel, 3, lineBoxParcel.aLG, i, false);
        zzb.zza(parcel, 4, lineBoxParcel.aLH, i, false);
        zzb.zza(parcel, 5, lineBoxParcel.aLI, i, false);
        zzb.zza(parcel, 6, lineBoxParcel.aLJ, false);
        zzb.zza(parcel, 7, lineBoxParcel.aLK);
        zzb.zza(parcel, 8, lineBoxParcel.aLz, false);
        zzb.zzc(parcel, 9, lineBoxParcel.aLL);
        zzb.zza(parcel, 10, lineBoxParcel.aLM);
        zzb.zzc(parcel, 11, lineBoxParcel.aLN);
        zzb.zzc(parcel, 12, lineBoxParcel.aLO);
        zzb.zzaj(parcel, zzcr);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzth(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzaby(i);
    }

    public LineBoxParcel[] zzaby(int i) {
        return new LineBoxParcel[i];
    }

    public LineBoxParcel zzth(Parcel parcel) {
        int zzcq = zza.zzcq(parcel);
        int i = 0;
        WordBoxParcel[] wordBoxParcelArr = null;
        BoundingBoxParcel boundingBoxParcel = null;
        BoundingBoxParcel boundingBoxParcel2 = null;
        BoundingBoxParcel boundingBoxParcel3 = null;
        String str = null;
        float f = 0.0f;
        String str2 = null;
        int i2 = 0;
        boolean z = false;
        int i3 = 0;
        int i4 = 0;
        while (parcel.dataPosition() < zzcq) {
            int zzcp = zza.zzcp(parcel);
            switch (zza.zzgv(zzcp)) {
                case VideoPlayer.TYPE_AUDIO /*1*/:
                    i = zza.zzg(parcel, zzcp);
                    break;
                case VideoPlayer.STATE_PREPARING /*2*/:
                    wordBoxParcelArr = (WordBoxParcel[]) zza.zzb(parcel, zzcp, WordBoxParcel.CREATOR);
                    break;
                case VideoPlayer.STATE_BUFFERING /*3*/:
                    boundingBoxParcel = (BoundingBoxParcel) zza.zza(parcel, zzcp, BoundingBoxParcel.CREATOR);
                    break;
                case VideoPlayer.STATE_READY /*4*/:
                    boundingBoxParcel2 = (BoundingBoxParcel) zza.zza(parcel, zzcp, BoundingBoxParcel.CREATOR);
                    break;
                case VideoPlayer.STATE_ENDED /*5*/:
                    boundingBoxParcel3 = (BoundingBoxParcel) zza.zza(parcel, zzcp, BoundingBoxParcel.CREATOR);
                    break;
                case Method.TRACE /*6*/:
                    str = zza.zzq(parcel, zzcp);
                    break;
                case Method.PATCH /*7*/:
                    f = zza.zzl(parcel, zzcp);
                    break;
                case TLRPC.USER_FLAG_USERNAME /*8*/:
                    str2 = zza.zzq(parcel, zzcp);
                    break;
                case C0338R.styleable.PromptView_iconTint /*9*/:
                    i2 = zza.zzg(parcel, zzcp);
                    break;
                case NetworkLock.DOWNLOAD_PRIORITY /*10*/:
                    z = zza.zzc(parcel, zzcp);
                    break;
                case C0338R.styleable.PromptView_maxTextWidth /*11*/:
                    i3 = zza.zzg(parcel, zzcp);
                    break;
                case Atom.FULL_HEADER_SIZE /*12*/:
                    i4 = zza.zzg(parcel, zzcp);
                    break;
                default:
                    zza.zzb(parcel, zzcp);
                    break;
            }
        }
        if (parcel.dataPosition() == zzcq) {
            return new LineBoxParcel(i, wordBoxParcelArr, boundingBoxParcel, boundingBoxParcel2, boundingBoxParcel3, str, f, str2, i2, z, i3, i4);
        }
        throw new zza.zza("Overread allowed size end=" + zzcq, parcel);
    }
}
