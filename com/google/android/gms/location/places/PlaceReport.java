package com.google.android.gms.location.places;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.support.v4.os.EnvironmentCompat;
import com.google.android.gms.common.internal.ReflectedParcelable;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.zzab;
import com.google.android.gms.common.internal.zzab.zza;
import com.google.android.gms.common.internal.zzac;
import com.hanista.mobogram.ui.Components.VideoPlayer;

public class PlaceReport extends AbstractSafeParcelable implements ReflectedParcelable {
    public static final Creator<PlaceReport> CREATOR;
    private final String f10I;
    private final String aiY;
    private final String mTag;
    final int mVersionCode;

    static {
        CREATOR = new zzi();
    }

    PlaceReport(int i, String str, String str2, String str3) {
        this.mVersionCode = i;
        this.aiY = str;
        this.mTag = str2;
        this.f10I = str3;
    }

    public static PlaceReport create(String str, String str2) {
        return zzj(str, str2, EnvironmentCompat.MEDIA_UNKNOWN);
    }

    public static PlaceReport zzj(String str, String str2, String str3) {
        zzac.zzy(str);
        zzac.zzhz(str2);
        zzac.zzhz(str3);
        zzac.zzb(zzla(str3), (Object) "Invalid source");
        return new PlaceReport(1, str, str2, str3);
    }

    private static boolean zzla(String str) {
        boolean z = true;
        switch (str.hashCode()) {
            case -1436706272:
                if (str.equals("inferredGeofencing")) {
                    z = true;
                    break;
                }
                break;
            case -1194968642:
                if (str.equals("userReported")) {
                    z = true;
                    break;
                }
                break;
            case -284840886:
                if (str.equals(EnvironmentCompat.MEDIA_UNKNOWN)) {
                    z = false;
                    break;
                }
                break;
            case -262743844:
                if (str.equals("inferredReverseGeocoding")) {
                    z = true;
                    break;
                }
                break;
            case 1164924125:
                if (str.equals("inferredSnappedToRoad")) {
                    z = true;
                    break;
                }
                break;
            case 1287171955:
                if (str.equals("inferredRadioSignals")) {
                    z = true;
                    break;
                }
                break;
        }
        switch (z) {
            case VideoPlayer.TRACK_DEFAULT /*0*/:
            case VideoPlayer.TYPE_AUDIO /*1*/:
            case VideoPlayer.STATE_PREPARING /*2*/:
            case VideoPlayer.STATE_BUFFERING /*3*/:
            case VideoPlayer.STATE_READY /*4*/:
            case VideoPlayer.STATE_ENDED /*5*/:
                return true;
            default:
                return false;
        }
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof PlaceReport)) {
            return false;
        }
        PlaceReport placeReport = (PlaceReport) obj;
        return zzab.equal(this.aiY, placeReport.aiY) && zzab.equal(this.mTag, placeReport.mTag) && zzab.equal(this.f10I, placeReport.f10I);
    }

    public String getPlaceId() {
        return this.aiY;
    }

    public String getSource() {
        return this.f10I;
    }

    public String getTag() {
        return this.mTag;
    }

    public int hashCode() {
        return zzab.hashCode(this.aiY, this.mTag, this.f10I);
    }

    public String toString() {
        zza zzx = zzab.zzx(this);
        zzx.zzg("placeId", this.aiY);
        zzx.zzg("tag", this.mTag);
        if (!EnvironmentCompat.MEDIA_UNKNOWN.equals(this.f10I)) {
            zzx.zzg("source", this.f10I);
        }
        return zzx.toString();
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzi.zza(this, parcel, i);
    }
}
