package com.hanista.mobogram.messenger.exoplayer.text.ttml;

import com.hanista.mobogram.messenger.exoplayer.text.Cue;
import com.hanista.mobogram.tgnet.TLRPC;

final class TtmlRegion {
    public final float line;
    public final int lineType;
    public final float position;
    public final float width;

    public TtmlRegion() {
        this(Cue.DIMEN_UNSET, Cue.DIMEN_UNSET, TLRPC.MESSAGE_FLAG_MEGAGROUP, Cue.DIMEN_UNSET);
    }

    public TtmlRegion(float f, float f2, int i, float f3) {
        this.position = f;
        this.line = f2;
        this.lineType = i;
        this.width = f3;
    }
}
