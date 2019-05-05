package com.hanista.mobogram.messenger.exoplayer.drm;

import android.annotation.TargetApi;
import android.media.MediaCrypto;
import com.hanista.mobogram.messenger.exoplayer.util.Assertions;

@TargetApi(16)
public final class FrameworkMediaCrypto implements ExoMediaCrypto {
    private final MediaCrypto mediaCrypto;

    FrameworkMediaCrypto(MediaCrypto mediaCrypto) {
        this.mediaCrypto = (MediaCrypto) Assertions.checkNotNull(mediaCrypto);
    }

    public MediaCrypto getWrappedMediaCrypto() {
        return this.mediaCrypto;
    }

    public boolean requiresSecureDecoderComponent(String str) {
        return this.mediaCrypto.requiresSecureDecoderComponent(str);
    }
}
