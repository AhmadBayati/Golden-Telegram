package com.hanista.mobogram.messenger.exoplayer;

import java.io.IOException;

public class ParserException extends IOException {
    public ParserException(String str) {
        super(str);
    }

    public ParserException(String str, Throwable th) {
        super(str, th);
    }

    public ParserException(Throwable th) {
        super(th);
    }
}
