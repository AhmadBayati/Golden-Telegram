package com.google.android.gms.common.stats;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.exoplayer.upstream.NetworkLock;
import com.hanista.mobogram.messenger.volley.Request.Method;
import com.hanista.mobogram.tgnet.TLRPC;
import com.hanista.mobogram.ui.Components.VideoPlayer;
import com.hanista.mobogram.util.shamsicalendar.ShamsiCalendar;

public class zza implements Creator<ConnectionEvent> {
    static void zza(ConnectionEvent connectionEvent, Parcel parcel, int i) {
        int zzcr = zzb.zzcr(parcel);
        zzb.zzc(parcel, 1, connectionEvent.mVersionCode);
        zzb.zza(parcel, 2, connectionEvent.getTimeMillis());
        zzb.zza(parcel, 4, connectionEvent.zzawk(), false);
        zzb.zza(parcel, 5, connectionEvent.zzawl(), false);
        zzb.zza(parcel, 6, connectionEvent.zzawm(), false);
        zzb.zza(parcel, 7, connectionEvent.zzawn(), false);
        zzb.zza(parcel, 8, connectionEvent.zzawo(), false);
        zzb.zza(parcel, 10, connectionEvent.zzaws());
        zzb.zza(parcel, 11, connectionEvent.zzawr());
        zzb.zzc(parcel, 12, connectionEvent.getEventType());
        zzb.zza(parcel, 13, connectionEvent.zzawp(), false);
        zzb.zzaj(parcel, zzcr);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzdb(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzhg(i);
    }

    public ConnectionEvent zzdb(Parcel parcel) {
        int zzcq = com.google.android.gms.common.internal.safeparcel.zza.zzcq(parcel);
        int i = 0;
        long j = 0;
        int i2 = 0;
        String str = null;
        String str2 = null;
        String str3 = null;
        String str4 = null;
        String str5 = null;
        String str6 = null;
        long j2 = 0;
        long j3 = 0;
        while (parcel.dataPosition() < zzcq) {
            int zzcp = com.google.android.gms.common.internal.safeparcel.zza.zzcp(parcel);
            switch (com.google.android.gms.common.internal.safeparcel.zza.zzgv(zzcp)) {
                case VideoPlayer.TYPE_AUDIO /*1*/:
                    i = com.google.android.gms.common.internal.safeparcel.zza.zzg(parcel, zzcp);
                    break;
                case VideoPlayer.STATE_PREPARING /*2*/:
                    j = com.google.android.gms.common.internal.safeparcel.zza.zzi(parcel, zzcp);
                    break;
                case VideoPlayer.STATE_READY /*4*/:
                    str = com.google.android.gms.common.internal.safeparcel.zza.zzq(parcel, zzcp);
                    break;
                case VideoPlayer.STATE_ENDED /*5*/:
                    str2 = com.google.android.gms.common.internal.safeparcel.zza.zzq(parcel, zzcp);
                    break;
                case Method.TRACE /*6*/:
                    str3 = com.google.android.gms.common.internal.safeparcel.zza.zzq(parcel, zzcp);
                    break;
                case Method.PATCH /*7*/:
                    str4 = com.google.android.gms.common.internal.safeparcel.zza.zzq(parcel, zzcp);
                    break;
                case TLRPC.USER_FLAG_USERNAME /*8*/:
                    str5 = com.google.android.gms.common.internal.safeparcel.zza.zzq(parcel, zzcp);
                    break;
                case NetworkLock.DOWNLOAD_PRIORITY /*10*/:
                    j2 = com.google.android.gms.common.internal.safeparcel.zza.zzi(parcel, zzcp);
                    break;
                case C0338R.styleable.PromptView_maxTextWidth /*11*/:
                    j3 = com.google.android.gms.common.internal.safeparcel.zza.zzi(parcel, zzcp);
                    break;
                case Atom.FULL_HEADER_SIZE /*12*/:
                    i2 = com.google.android.gms.common.internal.safeparcel.zza.zzg(parcel, zzcp);
                    break;
                case ShamsiCalendar.CURRENT_CENTURY /*13*/:
                    str6 = com.google.android.gms.common.internal.safeparcel.zza.zzq(parcel, zzcp);
                    break;
                default:
                    com.google.android.gms.common.internal.safeparcel.zza.zzb(parcel, zzcp);
                    break;
            }
        }
        if (parcel.dataPosition() == zzcq) {
            return new ConnectionEvent(i, j, i2, str, str2, str3, str4, str5, str6, j2, j3);
        }
        throw new com.google.android.gms.common.internal.safeparcel.zza.zza("Overread allowed size end=" + zzcq, parcel);
    }

    public ConnectionEvent[] zzhg(int i) {
        return new ConnectionEvent[i];
    }
}
