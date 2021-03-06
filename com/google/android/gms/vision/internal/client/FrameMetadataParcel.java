package com.google.android.gms.vision.internal.client;

import android.os.Parcel;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.vision.Frame;

public class FrameMetadataParcel extends AbstractSafeParcelable {
    public static final zzb CREATOR;
    public long aLt;
    public int height;
    public int id;
    public int rotation;
    final int versionCode;
    public int width;

    static {
        CREATOR = new zzb();
    }

    public FrameMetadataParcel() {
        this.versionCode = 1;
    }

    public FrameMetadataParcel(int i, int i2, int i3, int i4, long j, int i5) {
        this.versionCode = i;
        this.width = i2;
        this.height = i3;
        this.id = i4;
        this.aLt = j;
        this.rotation = i5;
    }

    public static FrameMetadataParcel zzc(Frame frame) {
        FrameMetadataParcel frameMetadataParcel = new FrameMetadataParcel();
        frameMetadataParcel.width = frame.getMetadata().getWidth();
        frameMetadataParcel.height = frame.getMetadata().getHeight();
        frameMetadataParcel.rotation = frame.getMetadata().getRotation();
        frameMetadataParcel.id = frame.getMetadata().getId();
        frameMetadataParcel.aLt = frame.getMetadata().getTimestampMillis();
        return frameMetadataParcel;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzb com_google_android_gms_vision_internal_client_zzb = CREATOR;
        zzb.zza(this, parcel, i);
    }
}
