package com.hanista.mobogram.messenger.exoplayer;

import android.annotation.TargetApi;
import android.media.MediaCodecInfo.CodecCapabilities;
import com.hanista.mobogram.messenger.exoplayer.util.Util;

@TargetApi(16)
public final class DecoderInfo {
    public final boolean adaptive;
    public final CodecCapabilities capabilities;
    public final String name;

    DecoderInfo(String str, CodecCapabilities codecCapabilities) {
        this.name = str;
        this.capabilities = codecCapabilities;
        this.adaptive = isAdaptive(codecCapabilities);
    }

    private static boolean isAdaptive(CodecCapabilities codecCapabilities) {
        return codecCapabilities != null && Util.SDK_INT >= 19 && isAdaptiveV19(codecCapabilities);
    }

    @TargetApi(19)
    private static boolean isAdaptiveV19(CodecCapabilities codecCapabilities) {
        return codecCapabilities.isFeatureSupported("adaptive-playback");
    }
}
