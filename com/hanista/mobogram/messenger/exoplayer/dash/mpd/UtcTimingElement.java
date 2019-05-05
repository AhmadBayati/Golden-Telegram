package com.hanista.mobogram.messenger.exoplayer.dash.mpd;

public final class UtcTimingElement {
    public final String schemeIdUri;
    public final String value;

    public UtcTimingElement(String str, String str2) {
        this.schemeIdUri = str;
        this.value = str2;
    }

    public String toString() {
        return this.schemeIdUri + ", " + this.value;
    }
}
