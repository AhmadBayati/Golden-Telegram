package com.googlecode.mp4parser.authoring.builder;

import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class ByteBufferHelper {
    public static List<ByteBuffer> mergeAdjacentBuffers(List<ByteBuffer> list) {
        List arrayList = new ArrayList(list.size());
        for (ByteBuffer byteBuffer : list) {
            int size = arrayList.size() - 1;
            if (size >= 0 && byteBuffer.hasArray() && ((ByteBuffer) arrayList.get(size)).hasArray() && byteBuffer.array() == ((ByteBuffer) arrayList.get(size)).array()) {
                if (((ByteBuffer) arrayList.get(size)).limit() + ((ByteBuffer) arrayList.get(size)).arrayOffset() == byteBuffer.arrayOffset()) {
                    ByteBuffer byteBuffer2 = (ByteBuffer) arrayList.remove(size);
                    arrayList.add(ByteBuffer.wrap(byteBuffer.array(), byteBuffer2.arrayOffset(), byteBuffer.limit() + byteBuffer2.limit()).slice());
                }
            }
            if (size >= 0 && (byteBuffer instanceof MappedByteBuffer) && (arrayList.get(size) instanceof MappedByteBuffer) && ((ByteBuffer) arrayList.get(size)).limit() == ((ByteBuffer) arrayList.get(size)).capacity() - byteBuffer.capacity()) {
                byteBuffer2 = (ByteBuffer) arrayList.get(size);
                byteBuffer2.limit(byteBuffer.limit() + byteBuffer2.limit());
            } else {
                byteBuffer.reset();
                arrayList.add(byteBuffer);
            }
        }
        return arrayList;
    }
}
