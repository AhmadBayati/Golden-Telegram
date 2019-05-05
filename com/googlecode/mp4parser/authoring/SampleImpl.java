package com.googlecode.mp4parser.authoring;

import com.coremedia.iso.boxes.Container;
import com.googlecode.mp4parser.util.CastUtils;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;

public class SampleImpl implements Sample {
    private ByteBuffer[] data;
    private final long offset;
    private final Container parent;
    private final long size;

    public SampleImpl(long j, long j2, Container container) {
        this.offset = j;
        this.size = j2;
        this.data = null;
        this.parent = container;
    }

    public SampleImpl(long j, long j2, ByteBuffer byteBuffer) {
        this.offset = j;
        this.size = j2;
        this.data = new ByteBuffer[]{byteBuffer};
        this.parent = null;
    }

    public SampleImpl(ByteBuffer byteBuffer) {
        this.offset = -1;
        this.size = (long) byteBuffer.limit();
        this.data = new ByteBuffer[]{byteBuffer};
        this.parent = null;
    }

    public SampleImpl(ByteBuffer[] byteBufferArr) {
        int i = 0;
        this.offset = -1;
        int i2 = 0;
        while (i < byteBufferArr.length) {
            i2 += byteBufferArr[i].remaining();
            i++;
        }
        this.size = (long) i2;
        this.data = byteBufferArr;
        this.parent = null;
    }

    public ByteBuffer asByteBuffer() {
        ensureData();
        ByteBuffer wrap = ByteBuffer.wrap(new byte[CastUtils.l2i(this.size)]);
        for (ByteBuffer duplicate : this.data) {
            wrap.put(duplicate.duplicate());
        }
        wrap.rewind();
        return wrap;
    }

    protected void ensureData() {
        if (this.data == null) {
            if (this.parent == null) {
                throw new RuntimeException("Missing parent container, can't read sample " + this);
            }
            try {
                this.data = new ByteBuffer[]{this.parent.getByteBuffer(this.offset, this.size)};
            } catch (Throwable e) {
                throw new RuntimeException("couldn't read sample " + this, e);
            }
        }
    }

    public long getSize() {
        return this.size;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("SampleImpl");
        stringBuilder.append("{offset=").append(this.offset);
        stringBuilder.append("{size=").append(this.size);
        stringBuilder.append('}');
        return stringBuilder.toString();
    }

    public void writeTo(WritableByteChannel writableByteChannel) {
        ensureData();
        for (ByteBuffer duplicate : this.data) {
            writableByteChannel.write(duplicate.duplicate());
        }
    }
}
