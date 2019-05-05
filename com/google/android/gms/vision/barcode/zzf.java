package com.google.android.gms.vision.barcode;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.vision.barcode.Barcode.DriverLicense;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.exoplayer.upstream.NetworkLock;
import com.hanista.mobogram.messenger.volley.Request.Method;
import com.hanista.mobogram.tgnet.TLRPC;
import com.hanista.mobogram.ui.Components.VideoPlayer;
import com.hanista.mobogram.util.shamsicalendar.ShamsiCalendar;

public class zzf implements Creator<DriverLicense> {
    static void zza(DriverLicense driverLicense, Parcel parcel, int i) {
        int zzcr = zzb.zzcr(parcel);
        zzb.zzc(parcel, 1, driverLicense.versionCode);
        zzb.zza(parcel, 2, driverLicense.documentType, false);
        zzb.zza(parcel, 3, driverLicense.firstName, false);
        zzb.zza(parcel, 4, driverLicense.middleName, false);
        zzb.zza(parcel, 5, driverLicense.lastName, false);
        zzb.zza(parcel, 6, driverLicense.gender, false);
        zzb.zza(parcel, 7, driverLicense.addressStreet, false);
        zzb.zza(parcel, 8, driverLicense.addressCity, false);
        zzb.zza(parcel, 9, driverLicense.addressState, false);
        zzb.zza(parcel, 10, driverLicense.addressZip, false);
        zzb.zza(parcel, 11, driverLicense.licenseNumber, false);
        zzb.zza(parcel, 12, driverLicense.issueDate, false);
        zzb.zza(parcel, 13, driverLicense.expiryDate, false);
        zzb.zza(parcel, 14, driverLicense.birthDate, false);
        zzb.zza(parcel, 15, driverLicense.issuingCountry, false);
        zzb.zzaj(parcel, zzcr);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzst(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzabi(i);
    }

    public DriverLicense[] zzabi(int i) {
        return new DriverLicense[i];
    }

    public DriverLicense zzst(Parcel parcel) {
        int zzcq = zza.zzcq(parcel);
        int i = 0;
        String str = null;
        String str2 = null;
        String str3 = null;
        String str4 = null;
        String str5 = null;
        String str6 = null;
        String str7 = null;
        String str8 = null;
        String str9 = null;
        String str10 = null;
        String str11 = null;
        String str12 = null;
        String str13 = null;
        String str14 = null;
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
                    str2 = zza.zzq(parcel, zzcp);
                    break;
                case VideoPlayer.STATE_READY /*4*/:
                    str3 = zza.zzq(parcel, zzcp);
                    break;
                case VideoPlayer.STATE_ENDED /*5*/:
                    str4 = zza.zzq(parcel, zzcp);
                    break;
                case Method.TRACE /*6*/:
                    str5 = zza.zzq(parcel, zzcp);
                    break;
                case Method.PATCH /*7*/:
                    str6 = zza.zzq(parcel, zzcp);
                    break;
                case TLRPC.USER_FLAG_USERNAME /*8*/:
                    str7 = zza.zzq(parcel, zzcp);
                    break;
                case C0338R.styleable.PromptView_iconTint /*9*/:
                    str8 = zza.zzq(parcel, zzcp);
                    break;
                case NetworkLock.DOWNLOAD_PRIORITY /*10*/:
                    str9 = zza.zzq(parcel, zzcp);
                    break;
                case C0338R.styleable.PromptView_maxTextWidth /*11*/:
                    str10 = zza.zzq(parcel, zzcp);
                    break;
                case Atom.FULL_HEADER_SIZE /*12*/:
                    str11 = zza.zzq(parcel, zzcp);
                    break;
                case ShamsiCalendar.CURRENT_CENTURY /*13*/:
                    str12 = zza.zzq(parcel, zzcp);
                    break;
                case C0338R.styleable.PromptView_primaryTextFontFamily /*14*/:
                    str13 = zza.zzq(parcel, zzcp);
                    break;
                case C0338R.styleable.PromptView_primaryTextSize /*15*/:
                    str14 = zza.zzq(parcel, zzcp);
                    break;
                default:
                    zza.zzb(parcel, zzcp);
                    break;
            }
        }
        if (parcel.dataPosition() == zzcq) {
            return new DriverLicense(i, str, str2, str3, str4, str5, str6, str7, str8, str9, str10, str11, str12, str13, str14);
        }
        throw new zza.zza("Overread allowed size end=" + zzcq, parcel);
    }
}
