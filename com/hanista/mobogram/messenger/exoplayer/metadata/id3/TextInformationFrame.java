package com.hanista.mobogram.messenger.exoplayer.metadata.id3;

public final class TextInformationFrame extends Id3Frame {
    public final String description;

    public TextInformationFrame(String str, String str2) {
        super(str);
        this.description = str2;
    }
}
