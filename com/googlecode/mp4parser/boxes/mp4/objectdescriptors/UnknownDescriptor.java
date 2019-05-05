package com.googlecode.mp4parser.boxes.mp4.objectdescriptors;

import java.nio.ByteBuffer;
import java.util.logging.Logger;

public class UnknownDescriptor extends BaseDescriptor {
    private static Logger log;
    private ByteBuffer data;

    static {
        log = Logger.getLogger(UnknownDescriptor.class.getName());
    }

    public void parseDetail(ByteBuffer byteBuffer) {
        this.data = (ByteBuffer) byteBuffer.slice().limit(getSizeOfInstance());
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("UnknownDescriptor");
        stringBuilder.append("{tag=").append(this.tag);
        stringBuilder.append(", sizeOfInstance=").append(this.sizeOfInstance);
        stringBuilder.append(", data=").append(this.data);
        stringBuilder.append('}');
        return stringBuilder.toString();
    }
}
