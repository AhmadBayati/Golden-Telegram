package com.hanista.mobogram.tgnet;

import com.hanista.mobogram.tgnet.TLRPC.TL_error;

public interface RequestDelegate {
    void run(TLObject tLObject, TL_error tL_error);
}
