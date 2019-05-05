package com.hanista.mobogram.messenger;

import com.hanista.mobogram.tgnet.TLRPC.TL_dialog;

public class DialogObject {
    public static boolean isChannel(TL_dialog tL_dialog) {
        return (tL_dialog == null || (tL_dialog.flags & 1) == 0) ? false : true;
    }
}
