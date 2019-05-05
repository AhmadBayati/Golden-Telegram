package com.googlecode.mp4parser.boxes.mp4.samplegrouping;

import com.hanista.mobogram.tgnet.TLRPC;
import java.nio.ByteBuffer;

public class TemporalLevelEntry extends GroupEntry {
    public static final String TYPE = "tele";
    private boolean levelIndependentlyDecodable;
    private short reserved;

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        TemporalLevelEntry temporalLevelEntry = (TemporalLevelEntry) obj;
        return this.levelIndependentlyDecodable != temporalLevelEntry.levelIndependentlyDecodable ? false : this.reserved == temporalLevelEntry.reserved;
    }

    public ByteBuffer get() {
        ByteBuffer allocate = ByteBuffer.allocate(1);
        allocate.put((byte) (this.levelIndependentlyDecodable ? TLRPC.USER_FLAG_UNUSED : 0));
        allocate.rewind();
        return allocate;
    }

    public String getType() {
        return TYPE;
    }

    public int hashCode() {
        return ((this.levelIndependentlyDecodable ? 1 : 0) * 31) + this.reserved;
    }

    public boolean isLevelIndependentlyDecodable() {
        return this.levelIndependentlyDecodable;
    }

    public void parse(ByteBuffer byteBuffer) {
        this.levelIndependentlyDecodable = (byteBuffer.get() & TLRPC.USER_FLAG_UNUSED) == TLRPC.USER_FLAG_UNUSED;
    }

    public void setLevelIndependentlyDecodable(boolean z) {
        this.levelIndependentlyDecodable = z;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("TemporalLevelEntry");
        stringBuilder.append("{levelIndependentlyDecodable=").append(this.levelIndependentlyDecodable);
        stringBuilder.append('}');
        return stringBuilder.toString();
    }
}
