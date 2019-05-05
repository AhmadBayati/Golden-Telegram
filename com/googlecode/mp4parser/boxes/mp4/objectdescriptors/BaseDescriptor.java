package com.googlecode.mp4parser.boxes.mp4.objectdescriptors;

import com.coremedia.iso.IsoTypeReader;
import java.nio.ByteBuffer;

@Descriptor(tags = {0})
public abstract class BaseDescriptor {
    static final /* synthetic */ boolean $assertionsDisabled;
    int sizeBytes;
    int sizeOfInstance;
    int tag;

    static {
        $assertionsDisabled = !BaseDescriptor.class.desiredAssertionStatus();
    }

    public int getSize() {
        return (this.sizeOfInstance + 1) + this.sizeBytes;
    }

    public int getSizeBytes() {
        return this.sizeBytes;
    }

    public int getSizeOfInstance() {
        return this.sizeOfInstance;
    }

    public int getTag() {
        return this.tag;
    }

    public final void parse(int i, ByteBuffer byteBuffer) {
        this.tag = i;
        int readUInt8 = IsoTypeReader.readUInt8(byteBuffer);
        this.sizeOfInstance = readUInt8 & 127;
        int i2 = 1;
        while ((readUInt8 >>> 7) == 1) {
            readUInt8 = IsoTypeReader.readUInt8(byteBuffer);
            i2++;
            this.sizeOfInstance = (this.sizeOfInstance << 7) | (readUInt8 & 127);
        }
        this.sizeBytes = i2;
        ByteBuffer slice = byteBuffer.slice();
        slice.limit(this.sizeOfInstance);
        parseDetail(slice);
        if ($assertionsDisabled || slice.remaining() == 0) {
            byteBuffer.position(byteBuffer.position() + this.sizeOfInstance);
            return;
        }
        throw new AssertionError(new StringBuilder(String.valueOf(getClass().getSimpleName())).append(" has not been fully parsed").toString());
    }

    public abstract void parseDetail(ByteBuffer byteBuffer);

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("BaseDescriptor");
        stringBuilder.append("{tag=").append(this.tag);
        stringBuilder.append(", sizeOfInstance=").append(this.sizeOfInstance);
        stringBuilder.append('}');
        return stringBuilder.toString();
    }
}
