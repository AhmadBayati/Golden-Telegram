package com.mp4parser.iso14496.part15;

import com.googlecode.mp4parser.boxes.mp4.samplegrouping.GroupEntry;
import java.nio.ByteBuffer;

public class StepwiseTemporalLayerEntry extends GroupEntry {
    public static final String TYPE = "stsa";

    public boolean equals(Object obj) {
        return this == obj ? true : obj != null && getClass() == obj.getClass();
    }

    public ByteBuffer get() {
        return ByteBuffer.allocate(0);
    }

    public String getType() {
        return TYPE;
    }

    public int hashCode() {
        return 37;
    }

    public void parse(ByteBuffer byteBuffer) {
    }
}
