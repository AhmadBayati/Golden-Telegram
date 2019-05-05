package com.hanista.mobogram.messenger.exoplayer.util.extensions;

public interface Decoder<I, O, E extends Exception> {
    I dequeueInputBuffer();

    O dequeueOutputBuffer();

    void flush();

    void queueInputBuffer(I i);

    void release();
}
