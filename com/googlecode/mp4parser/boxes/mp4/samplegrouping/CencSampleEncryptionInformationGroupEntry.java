package com.googlecode.mp4parser.boxes.mp4.samplegrouping;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.googlecode.mp4parser.util.UUIDConverter;
import java.nio.ByteBuffer;
import java.util.UUID;

public class CencSampleEncryptionInformationGroupEntry extends GroupEntry {
    public static final String TYPE = "seig";
    private boolean isEncrypted;
    private byte ivSize;
    private UUID kid;

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        CencSampleEncryptionInformationGroupEntry cencSampleEncryptionInformationGroupEntry = (CencSampleEncryptionInformationGroupEntry) obj;
        if (this.isEncrypted != cencSampleEncryptionInformationGroupEntry.isEncrypted) {
            return false;
        }
        if (this.ivSize != cencSampleEncryptionInformationGroupEntry.ivSize) {
            return false;
        }
        if (this.kid != null) {
            if (this.kid.equals(cencSampleEncryptionInformationGroupEntry.kid)) {
                return true;
            }
        } else if (cencSampleEncryptionInformationGroupEntry.kid == null) {
            return true;
        }
        return false;
    }

    public ByteBuffer get() {
        ByteBuffer allocate = ByteBuffer.allocate(20);
        IsoTypeWriter.writeUInt24(allocate, this.isEncrypted ? 1 : 0);
        if (this.isEncrypted) {
            IsoTypeWriter.writeUInt8(allocate, this.ivSize);
            allocate.put(UUIDConverter.convert(this.kid));
        } else {
            allocate.put(new byte[17]);
        }
        allocate.rewind();
        return allocate;
    }

    public byte getIvSize() {
        return this.ivSize;
    }

    public UUID getKid() {
        return this.kid;
    }

    public String getType() {
        return TYPE;
    }

    public int hashCode() {
        return (this.kid != null ? this.kid.hashCode() : 0) + ((((this.isEncrypted ? 7 : 19) * 31) + this.ivSize) * 31);
    }

    public boolean isEncrypted() {
        return this.isEncrypted;
    }

    public void parse(ByteBuffer byteBuffer) {
        boolean z = true;
        if (IsoTypeReader.readUInt24(byteBuffer) != 1) {
            z = false;
        }
        this.isEncrypted = z;
        this.ivSize = (byte) IsoTypeReader.readUInt8(byteBuffer);
        byte[] bArr = new byte[16];
        byteBuffer.get(bArr);
        this.kid = UUIDConverter.convert(bArr);
    }

    public void setEncrypted(boolean z) {
        this.isEncrypted = z;
    }

    public void setIvSize(int i) {
        this.ivSize = (byte) i;
    }

    public void setKid(UUID uuid) {
        this.kid = uuid;
    }

    public String toString() {
        return "CencSampleEncryptionInformationGroupEntry{isEncrypted=" + this.isEncrypted + ", ivSize=" + this.ivSize + ", kid=" + this.kid + '}';
    }
}
