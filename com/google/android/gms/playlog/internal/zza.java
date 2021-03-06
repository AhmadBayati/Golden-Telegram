package com.google.android.gms.playlog.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.exoplayer.upstream.NetworkLock;
import com.hanista.mobogram.messenger.volley.Request.Method;
import com.hanista.mobogram.tgnet.TLRPC;
import com.hanista.mobogram.ui.Components.VideoPlayer;

public class zza implements Creator<PlayLoggerContext> {
    static void zza(PlayLoggerContext playLoggerContext, Parcel parcel, int i) {
        int zzcr = zzb.zzcr(parcel);
        zzb.zzc(parcel, 1, playLoggerContext.versionCode);
        zzb.zza(parcel, 2, playLoggerContext.packageName, false);
        zzb.zzc(parcel, 3, playLoggerContext.axu);
        zzb.zzc(parcel, 4, playLoggerContext.axv);
        zzb.zza(parcel, 5, playLoggerContext.axw, false);
        zzb.zza(parcel, 6, playLoggerContext.axx, false);
        zzb.zza(parcel, 7, playLoggerContext.axy);
        zzb.zza(parcel, 8, playLoggerContext.axz, false);
        zzb.zza(parcel, 9, playLoggerContext.axA);
        zzb.zzc(parcel, 10, playLoggerContext.axB);
        zzb.zzaj(parcel, zzcr);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzrs(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzzk(i);
    }

    public PlayLoggerContext zzrs(Parcel parcel) {
        String str = null;
        int i = 0;
        int zzcq = com.google.android.gms.common.internal.safeparcel.zza.zzcq(parcel);
        boolean z = true;
        boolean z2 = false;
        String str2 = null;
        String str3 = null;
        int i2 = 0;
        int i3 = 0;
        String str4 = null;
        int i4 = 0;
        while (parcel.dataPosition() < zzcq) {
            int zzcp = com.google.android.gms.common.internal.safeparcel.zza.zzcp(parcel);
            switch (com.google.android.gms.common.internal.safeparcel.zza.zzgv(zzcp)) {
                case VideoPlayer.TYPE_AUDIO /*1*/:
                    i4 = com.google.android.gms.common.internal.safeparcel.zza.zzg(parcel, zzcp);
                    break;
                case VideoPlayer.STATE_PREPARING /*2*/:
                    str4 = com.google.android.gms.common.internal.safeparcel.zza.zzq(parcel, zzcp);
                    break;
                case VideoPlayer.STATE_BUFFERING /*3*/:
                    i3 = com.google.android.gms.common.internal.safeparcel.zza.zzg(parcel, zzcp);
                    break;
                case VideoPlayer.STATE_READY /*4*/:
                    i2 = com.google.android.gms.common.internal.safeparcel.zza.zzg(parcel, zzcp);
                    break;
                case VideoPlayer.STATE_ENDED /*5*/:
                    str3 = com.google.android.gms.common.internal.safeparcel.zza.zzq(parcel, zzcp);
                    break;
                case Method.TRACE /*6*/:
                    str2 = com.google.android.gms.common.internal.safeparcel.zza.zzq(parcel, zzcp);
                    break;
                case Method.PATCH /*7*/:
                    z = com.google.android.gms.common.internal.safeparcel.zza.zzc(parcel, zzcp);
                    break;
                case TLRPC.USER_FLAG_USERNAME /*8*/:
                    str = com.google.android.gms.common.internal.safeparcel.zza.zzq(parcel, zzcp);
                    break;
                case C0338R.styleable.PromptView_iconTint /*9*/:
                    z2 = com.google.android.gms.common.internal.safeparcel.zza.zzc(parcel, zzcp);
                    break;
                case NetworkLock.DOWNLOAD_PRIORITY /*10*/:
                    i = com.google.android.gms.common.internal.safeparcel.zza.zzg(parcel, zzcp);
                    break;
                default:
                    com.google.android.gms.common.internal.safeparcel.zza.zzb(parcel, zzcp);
                    break;
            }
        }
        if (parcel.dataPosition() == zzcq) {
            return new PlayLoggerContext(i4, str4, i3, i2, str3, str2, z, str, z2, i);
        }
        throw new com.google.android.gms.common.internal.safeparcel.zza.zza("Overread allowed size end=" + zzcq, parcel);
    }

    public PlayLoggerContext[] zzzk(int i) {
        return new PlayLoggerContext[i];
    }
}
