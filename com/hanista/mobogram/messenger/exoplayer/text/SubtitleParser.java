package com.hanista.mobogram.messenger.exoplayer.text;

public interface SubtitleParser {
    boolean canParse(String str);

    Subtitle parse(byte[] bArr, int i, int i2);
}
