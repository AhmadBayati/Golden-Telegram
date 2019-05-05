package com.hanista.mobogram.messenger.exoplayer.metadata;

public interface MetadataParser<T> {
    boolean canParse(String str);

    T parse(byte[] bArr, int i);
}
