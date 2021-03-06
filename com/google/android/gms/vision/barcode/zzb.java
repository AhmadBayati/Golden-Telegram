package com.google.android.gms.vision.barcode;

import android.graphics.Point;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.vision.barcode.Barcode.CalendarEvent;
import com.google.android.gms.vision.barcode.Barcode.ContactInfo;
import com.google.android.gms.vision.barcode.Barcode.DriverLicense;
import com.google.android.gms.vision.barcode.Barcode.Email;
import com.google.android.gms.vision.barcode.Barcode.GeoPoint;
import com.google.android.gms.vision.barcode.Barcode.Phone;
import com.google.android.gms.vision.barcode.Barcode.Sms;
import com.google.android.gms.vision.barcode.Barcode.UrlBookmark;
import com.google.android.gms.vision.barcode.Barcode.WiFi;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.exoplayer.upstream.NetworkLock;
import com.hanista.mobogram.messenger.volley.Request.Method;
import com.hanista.mobogram.tgnet.TLRPC;
import com.hanista.mobogram.ui.Components.VideoPlayer;
import com.hanista.mobogram.util.shamsicalendar.ShamsiCalendar;

public class zzb implements Creator<Barcode> {
    static void zza(Barcode barcode, Parcel parcel, int i) {
        int zzcr = com.google.android.gms.common.internal.safeparcel.zzb.zzcr(parcel);
        com.google.android.gms.common.internal.safeparcel.zzb.zzc(parcel, 1, barcode.versionCode);
        com.google.android.gms.common.internal.safeparcel.zzb.zzc(parcel, 2, barcode.format);
        com.google.android.gms.common.internal.safeparcel.zzb.zza(parcel, 3, barcode.rawValue, false);
        com.google.android.gms.common.internal.safeparcel.zzb.zza(parcel, 4, barcode.displayValue, false);
        com.google.android.gms.common.internal.safeparcel.zzb.zzc(parcel, 5, barcode.valueFormat);
        com.google.android.gms.common.internal.safeparcel.zzb.zza(parcel, 6, barcode.cornerPoints, i, false);
        com.google.android.gms.common.internal.safeparcel.zzb.zza(parcel, 7, barcode.email, i, false);
        com.google.android.gms.common.internal.safeparcel.zzb.zza(parcel, 8, barcode.phone, i, false);
        com.google.android.gms.common.internal.safeparcel.zzb.zza(parcel, 9, barcode.sms, i, false);
        com.google.android.gms.common.internal.safeparcel.zzb.zza(parcel, 10, barcode.wifi, i, false);
        com.google.android.gms.common.internal.safeparcel.zzb.zza(parcel, 11, barcode.url, i, false);
        com.google.android.gms.common.internal.safeparcel.zzb.zza(parcel, 12, barcode.geoPoint, i, false);
        com.google.android.gms.common.internal.safeparcel.zzb.zza(parcel, 13, barcode.calendarEvent, i, false);
        com.google.android.gms.common.internal.safeparcel.zzb.zza(parcel, 14, barcode.contactInfo, i, false);
        com.google.android.gms.common.internal.safeparcel.zzb.zza(parcel, 15, barcode.driverLicense, i, false);
        com.google.android.gms.common.internal.safeparcel.zzb.zzaj(parcel, zzcr);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzsp(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzabe(i);
    }

    public Barcode[] zzabe(int i) {
        return new Barcode[i];
    }

    public Barcode zzsp(Parcel parcel) {
        int zzcq = zza.zzcq(parcel);
        int i = 0;
        int i2 = 0;
        String str = null;
        String str2 = null;
        int i3 = 0;
        Point[] pointArr = null;
        Email email = null;
        Phone phone = null;
        Sms sms = null;
        WiFi wiFi = null;
        UrlBookmark urlBookmark = null;
        GeoPoint geoPoint = null;
        CalendarEvent calendarEvent = null;
        ContactInfo contactInfo = null;
        DriverLicense driverLicense = null;
        while (parcel.dataPosition() < zzcq) {
            int zzcp = zza.zzcp(parcel);
            switch (zza.zzgv(zzcp)) {
                case VideoPlayer.TYPE_AUDIO /*1*/:
                    i = zza.zzg(parcel, zzcp);
                    break;
                case VideoPlayer.STATE_PREPARING /*2*/:
                    i2 = zza.zzg(parcel, zzcp);
                    break;
                case VideoPlayer.STATE_BUFFERING /*3*/:
                    str = zza.zzq(parcel, zzcp);
                    break;
                case VideoPlayer.STATE_READY /*4*/:
                    str2 = zza.zzq(parcel, zzcp);
                    break;
                case VideoPlayer.STATE_ENDED /*5*/:
                    i3 = zza.zzg(parcel, zzcp);
                    break;
                case Method.TRACE /*6*/:
                    pointArr = (Point[]) zza.zzb(parcel, zzcp, Point.CREATOR);
                    break;
                case Method.PATCH /*7*/:
                    email = (Email) zza.zza(parcel, zzcp, Email.CREATOR);
                    break;
                case TLRPC.USER_FLAG_USERNAME /*8*/:
                    phone = (Phone) zza.zza(parcel, zzcp, Phone.CREATOR);
                    break;
                case C0338R.styleable.PromptView_iconTint /*9*/:
                    sms = (Sms) zza.zza(parcel, zzcp, Sms.CREATOR);
                    break;
                case NetworkLock.DOWNLOAD_PRIORITY /*10*/:
                    wiFi = (WiFi) zza.zza(parcel, zzcp, WiFi.CREATOR);
                    break;
                case C0338R.styleable.PromptView_maxTextWidth /*11*/:
                    urlBookmark = (UrlBookmark) zza.zza(parcel, zzcp, UrlBookmark.CREATOR);
                    break;
                case Atom.FULL_HEADER_SIZE /*12*/:
                    geoPoint = (GeoPoint) zza.zza(parcel, zzcp, GeoPoint.CREATOR);
                    break;
                case ShamsiCalendar.CURRENT_CENTURY /*13*/:
                    calendarEvent = (CalendarEvent) zza.zza(parcel, zzcp, CalendarEvent.CREATOR);
                    break;
                case C0338R.styleable.PromptView_primaryTextFontFamily /*14*/:
                    contactInfo = (ContactInfo) zza.zza(parcel, zzcp, ContactInfo.CREATOR);
                    break;
                case C0338R.styleable.PromptView_primaryTextSize /*15*/:
                    driverLicense = (DriverLicense) zza.zza(parcel, zzcp, DriverLicense.CREATOR);
                    break;
                default:
                    zza.zzb(parcel, zzcp);
                    break;
            }
        }
        if (parcel.dataPosition() == zzcq) {
            return new Barcode(i, i2, str, str2, i3, pointArr, email, phone, sms, wiFi, urlBookmark, geoPoint, calendarEvent, contactInfo, driverLicense);
        }
        throw new zza.zza("Overread allowed size end=" + zzcq, parcel);
    }
}
