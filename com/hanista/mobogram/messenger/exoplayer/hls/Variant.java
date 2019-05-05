package com.hanista.mobogram.messenger.exoplayer.hls;

import com.hanista.mobogram.messenger.exoplayer.chunk.Format;
import com.hanista.mobogram.messenger.exoplayer.chunk.FormatWrapper;

public final class Variant implements FormatWrapper {
    public final Format format;
    public final String url;

    public Variant(String str, Format format) {
        this.url = str;
        this.format = format;
    }

    public Format getFormat() {
        return this.format;
    }
}
