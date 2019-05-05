package com.google.android.gms.maps.internal;

import com.hanista.mobogram.ui.Components.VideoPlayer;

public final class zza {
    public static Boolean zza(byte b) {
        switch (b) {
            case VideoPlayer.TRACK_DEFAULT /*0*/:
                return Boolean.FALSE;
            case VideoPlayer.TYPE_AUDIO /*1*/:
                return Boolean.TRUE;
            default:
                return null;
        }
    }

    public static byte zze(Boolean bool) {
        return bool != null ? bool.booleanValue() ? (byte) 1 : (byte) 0 : (byte) -1;
    }
}
