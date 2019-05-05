package com.hanista.mobogram.messenger.exoplayer.metadata.id3;

public final class TxxxFrame extends Id3Frame {
    public static final String ID = "TXXX";
    public final String description;
    public final String value;

    public TxxxFrame(String str, String str2) {
        super(ID);
        this.description = str;
        this.value = str2;
    }
}
