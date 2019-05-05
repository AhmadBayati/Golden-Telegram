package com.hanista.mobogram.messenger.audioinfo.m4a;

import com.hanista.mobogram.messenger.audioinfo.util.PositionInputStream;
import java.io.InputStream;

public final class MP4Input extends MP4Box<PositionInputStream> {
    public MP4Input(InputStream inputStream) {
        super(new PositionInputStream(inputStream), null, TtmlNode.ANONYMOUS_REGION_ID);
    }

    public MP4Atom nextChildUpTo(String str) {
        MP4Atom nextChild;
        do {
            nextChild = nextChild();
        } while (!nextChild.getType().matches(str));
        return nextChild;
    }

    public String toString() {
        return "mp4[pos=" + getPosition() + "]";
    }
}
