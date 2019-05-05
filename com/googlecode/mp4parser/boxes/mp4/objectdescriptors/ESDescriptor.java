package com.googlecode.mp4parser.boxes.mp4.objectdescriptors;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Descriptor(tags = {3})
public class ESDescriptor extends BaseDescriptor {
    private static Logger log;
    int URLFlag;
    int URLLength;
    String URLString;
    DecoderConfigDescriptor decoderConfigDescriptor;
    int dependsOnEsId;
    int esId;
    int oCREsId;
    int oCRstreamFlag;
    List<BaseDescriptor> otherDescriptors;
    int remoteODFlag;
    SLConfigDescriptor slConfigDescriptor;
    int streamDependenceFlag;
    int streamPriority;

    static {
        log = Logger.getLogger(ESDescriptor.class.getName());
    }

    public ESDescriptor() {
        this.URLLength = 0;
        this.otherDescriptors = new ArrayList();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        ESDescriptor eSDescriptor = (ESDescriptor) obj;
        if (this.URLFlag != eSDescriptor.URLFlag) {
            return false;
        }
        if (this.URLLength != eSDescriptor.URLLength) {
            return false;
        }
        if (this.dependsOnEsId != eSDescriptor.dependsOnEsId) {
            return false;
        }
        if (this.esId != eSDescriptor.esId) {
            return false;
        }
        if (this.oCREsId != eSDescriptor.oCREsId) {
            return false;
        }
        if (this.oCRstreamFlag != eSDescriptor.oCRstreamFlag) {
            return false;
        }
        if (this.remoteODFlag != eSDescriptor.remoteODFlag) {
            return false;
        }
        if (this.streamDependenceFlag != eSDescriptor.streamDependenceFlag) {
            return false;
        }
        if (this.streamPriority != eSDescriptor.streamPriority) {
            return false;
        }
        if (this.URLString == null ? eSDescriptor.URLString != null : !this.URLString.equals(eSDescriptor.URLString)) {
            return false;
        }
        if (this.decoderConfigDescriptor == null ? eSDescriptor.decoderConfigDescriptor != null : !this.decoderConfigDescriptor.equals(eSDescriptor.decoderConfigDescriptor)) {
            return false;
        }
        if (this.otherDescriptors == null ? eSDescriptor.otherDescriptors != null : !this.otherDescriptors.equals(eSDescriptor.otherDescriptors)) {
            return false;
        }
        if (this.slConfigDescriptor != null) {
            if (this.slConfigDescriptor.equals(eSDescriptor.slConfigDescriptor)) {
                return true;
            }
        } else if (eSDescriptor.slConfigDescriptor == null) {
            return true;
        }
        return false;
    }

    public DecoderConfigDescriptor getDecoderConfigDescriptor() {
        return this.decoderConfigDescriptor;
    }

    public int getDependsOnEsId() {
        return this.dependsOnEsId;
    }

    public int getEsId() {
        return this.esId;
    }

    public List<BaseDescriptor> getOtherDescriptors() {
        return this.otherDescriptors;
    }

    public int getRemoteODFlag() {
        return this.remoteODFlag;
    }

    public SLConfigDescriptor getSlConfigDescriptor() {
        return this.slConfigDescriptor;
    }

    public int getStreamDependenceFlag() {
        return this.streamDependenceFlag;
    }

    public int getStreamPriority() {
        return this.streamPriority;
    }

    public int getURLFlag() {
        return this.URLFlag;
    }

    public int getURLLength() {
        return this.URLLength;
    }

    public String getURLString() {
        return this.URLString;
    }

    public int getoCREsId() {
        return this.oCREsId;
    }

    public int getoCRstreamFlag() {
        return this.oCRstreamFlag;
    }

    public int hashCode() {
        int i = 0;
        int hashCode = ((this.slConfigDescriptor != null ? this.slConfigDescriptor.hashCode() : 0) + (((this.decoderConfigDescriptor != null ? this.decoderConfigDescriptor.hashCode() : 0) + (((((((((this.URLString != null ? this.URLString.hashCode() : 0) + (((((((((((this.esId * 31) + this.streamDependenceFlag) * 31) + this.URLFlag) * 31) + this.oCRstreamFlag) * 31) + this.streamPriority) * 31) + this.URLLength) * 31)) * 31) + this.remoteODFlag) * 31) + this.dependsOnEsId) * 31) + this.oCREsId) * 31)) * 31)) * 31;
        if (this.otherDescriptors != null) {
            i = this.otherDescriptors.hashCode();
        }
        return hashCode + i;
    }

    public void parseDetail(ByteBuffer byteBuffer) {
        BaseDescriptor createFrom;
        long position;
        int i = 0;
        this.esId = IsoTypeReader.readUInt16(byteBuffer);
        int readUInt8 = IsoTypeReader.readUInt8(byteBuffer);
        this.streamDependenceFlag = readUInt8 >>> 7;
        this.URLFlag = (readUInt8 >>> 6) & 1;
        this.oCRstreamFlag = (readUInt8 >>> 5) & 1;
        this.streamPriority = readUInt8 & 31;
        if (this.streamDependenceFlag == 1) {
            this.dependsOnEsId = IsoTypeReader.readUInt16(byteBuffer);
        }
        if (this.URLFlag == 1) {
            this.URLLength = IsoTypeReader.readUInt8(byteBuffer);
            this.URLString = IsoTypeReader.readString(byteBuffer, this.URLLength);
        }
        if (this.oCRstreamFlag == 1) {
            this.oCREsId = IsoTypeReader.readUInt16(byteBuffer);
        }
        readUInt8 = (this.URLFlag == 1 ? this.URLLength + 1 : 0) + ((((getSizeBytes() + 1) + 2) + 1) + (this.streamDependenceFlag == 1 ? 2 : 0));
        if (this.oCRstreamFlag == 1) {
            i = 2;
        }
        int i2 = readUInt8 + i;
        int position2 = byteBuffer.position();
        if (getSize() > i2 + 2) {
            createFrom = ObjectDescriptorFactory.createFrom(-1, byteBuffer);
            position = (long) (byteBuffer.position() - position2);
            log.finer(createFrom + " - ESDescriptor1 read: " + position + ", size: " + (createFrom != null ? Integer.valueOf(createFrom.getSize()) : null));
            if (createFrom != null) {
                i = createFrom.getSize();
                byteBuffer.position(position2 + i);
                i += i2;
            } else {
                i = (int) (((long) i2) + position);
            }
            if (createFrom instanceof DecoderConfigDescriptor) {
                this.decoderConfigDescriptor = (DecoderConfigDescriptor) createFrom;
            }
        } else {
            i = i2;
        }
        position2 = byteBuffer.position();
        if (getSize() > i + 2) {
            createFrom = ObjectDescriptorFactory.createFrom(-1, byteBuffer);
            position = (long) (byteBuffer.position() - position2);
            log.finer(createFrom + " - ESDescriptor2 read: " + position + ", size: " + (createFrom != null ? Integer.valueOf(createFrom.getSize()) : null));
            if (createFrom != null) {
                i2 = createFrom.getSize();
                byteBuffer.position(position2 + i2);
                i += i2;
            } else {
                i = (int) (((long) i) + position);
            }
            if (createFrom instanceof SLConfigDescriptor) {
                this.slConfigDescriptor = (SLConfigDescriptor) createFrom;
            }
        } else {
            log.warning("SLConfigDescriptor is missing!");
        }
        while (getSize() - i > 2) {
            i2 = byteBuffer.position();
            BaseDescriptor createFrom2 = ObjectDescriptorFactory.createFrom(-1, byteBuffer);
            position = (long) (byteBuffer.position() - i2);
            log.finer(createFrom2 + " - ESDescriptor3 read: " + position + ", size: " + (createFrom2 != null ? Integer.valueOf(createFrom2.getSize()) : null));
            if (createFrom2 != null) {
                readUInt8 = createFrom2.getSize();
                byteBuffer.position(i2 + readUInt8);
                i += readUInt8;
            } else {
                i = (int) (position + ((long) i));
            }
            this.otherDescriptors.add(createFrom2);
        }
    }

    public ByteBuffer serialize() {
        ByteBuffer allocate = ByteBuffer.allocate(serializedSize());
        IsoTypeWriter.writeUInt8(allocate, 3);
        IsoTypeWriter.writeUInt8(allocate, serializedSize() - 2);
        IsoTypeWriter.writeUInt16(allocate, this.esId);
        IsoTypeWriter.writeUInt8(allocate, (((this.streamDependenceFlag << 7) | (this.URLFlag << 6)) | (this.oCRstreamFlag << 5)) | (this.streamPriority & 31));
        if (this.streamDependenceFlag > 0) {
            IsoTypeWriter.writeUInt16(allocate, this.dependsOnEsId);
        }
        if (this.URLFlag > 0) {
            IsoTypeWriter.writeUInt8(allocate, this.URLLength);
            IsoTypeWriter.writeUtf8String(allocate, this.URLString);
        }
        if (this.oCRstreamFlag > 0) {
            IsoTypeWriter.writeUInt16(allocate, this.oCREsId);
        }
        ByteBuffer serialize = this.decoderConfigDescriptor.serialize();
        ByteBuffer serialize2 = this.slConfigDescriptor.serialize();
        allocate.put(serialize.array());
        allocate.put(serialize2.array());
        return allocate;
    }

    public int serializedSize() {
        int i = 5;
        if (this.streamDependenceFlag > 0) {
            i = 7;
        }
        if (this.URLFlag > 0) {
            i += this.URLLength + 1;
        }
        if (this.oCRstreamFlag > 0) {
            i += 2;
        }
        return (i + this.decoderConfigDescriptor.serializedSize()) + this.slConfigDescriptor.serializedSize();
    }

    public void setDecoderConfigDescriptor(DecoderConfigDescriptor decoderConfigDescriptor) {
        this.decoderConfigDescriptor = decoderConfigDescriptor;
    }

    public void setDependsOnEsId(int i) {
        this.dependsOnEsId = i;
    }

    public void setEsId(int i) {
        this.esId = i;
    }

    public void setRemoteODFlag(int i) {
        this.remoteODFlag = i;
    }

    public void setSlConfigDescriptor(SLConfigDescriptor sLConfigDescriptor) {
        this.slConfigDescriptor = sLConfigDescriptor;
    }

    public void setStreamDependenceFlag(int i) {
        this.streamDependenceFlag = i;
    }

    public void setStreamPriority(int i) {
        this.streamPriority = i;
    }

    public void setURLFlag(int i) {
        this.URLFlag = i;
    }

    public void setURLLength(int i) {
        this.URLLength = i;
    }

    public void setURLString(String str) {
        this.URLString = str;
    }

    public void setoCREsId(int i) {
        this.oCREsId = i;
    }

    public void setoCRstreamFlag(int i) {
        this.oCRstreamFlag = i;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("ESDescriptor");
        stringBuilder.append("{esId=").append(this.esId);
        stringBuilder.append(", streamDependenceFlag=").append(this.streamDependenceFlag);
        stringBuilder.append(", URLFlag=").append(this.URLFlag);
        stringBuilder.append(", oCRstreamFlag=").append(this.oCRstreamFlag);
        stringBuilder.append(", streamPriority=").append(this.streamPriority);
        stringBuilder.append(", URLLength=").append(this.URLLength);
        stringBuilder.append(", URLString='").append(this.URLString).append('\'');
        stringBuilder.append(", remoteODFlag=").append(this.remoteODFlag);
        stringBuilder.append(", dependsOnEsId=").append(this.dependsOnEsId);
        stringBuilder.append(", oCREsId=").append(this.oCREsId);
        stringBuilder.append(", decoderConfigDescriptor=").append(this.decoderConfigDescriptor);
        stringBuilder.append(", slConfigDescriptor=").append(this.slConfigDescriptor);
        stringBuilder.append('}');
        return stringBuilder.toString();
    }
}
