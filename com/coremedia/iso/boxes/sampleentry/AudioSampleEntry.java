package com.coremedia.iso.boxes.sampleentry;

import com.coremedia.iso.BoxParser;
import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.coremedia.iso.boxes.Box;
import com.coremedia.iso.boxes.Container;
import com.googlecode.mp4parser.DataSource;
import com.googlecode.mp4parser.util.CastUtils;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;

public final class AudioSampleEntry extends AbstractSampleEntry {
    static final /* synthetic */ boolean $assertionsDisabled;
    public static final String TYPE1 = "samr";
    public static final String TYPE10 = "mlpa";
    public static final String TYPE11 = "dtsl";
    public static final String TYPE12 = "dtsh";
    public static final String TYPE13 = "dtse";
    public static final String TYPE2 = "sawb";
    public static final String TYPE3 = "mp4a";
    public static final String TYPE4 = "drms";
    public static final String TYPE5 = "alac";
    public static final String TYPE7 = "owma";
    public static final String TYPE8 = "ac-3";
    public static final String TYPE9 = "ec-3";
    public static final String TYPE_ENCRYPTED = "enca";
    private long bytesPerFrame;
    private long bytesPerPacket;
    private long bytesPerSample;
    private int channelCount;
    private int compressionId;
    private int packetSize;
    private int reserved1;
    private long reserved2;
    private long sampleRate;
    private int sampleSize;
    private long samplesPerPacket;
    private int soundVersion;
    private byte[] soundVersion2Data;

    /* renamed from: com.coremedia.iso.boxes.sampleentry.AudioSampleEntry.1 */
    class C00811 implements Box {
        private final /* synthetic */ ByteBuffer val$owmaSpecifics;
        private final /* synthetic */ long val$remaining;

        C00811(long j, ByteBuffer byteBuffer) {
            this.val$remaining = j;
            this.val$owmaSpecifics = byteBuffer;
        }

        public void getBox(WritableByteChannel writableByteChannel) {
            this.val$owmaSpecifics.rewind();
            writableByteChannel.write(this.val$owmaSpecifics);
        }

        public long getOffset() {
            return 0;
        }

        public Container getParent() {
            return AudioSampleEntry.this;
        }

        public long getSize() {
            return this.val$remaining;
        }

        public String getType() {
            return "----";
        }

        public void parse(DataSource dataSource, ByteBuffer byteBuffer, long j, BoxParser boxParser) {
            throw new RuntimeException("NotImplemented");
        }

        public void setParent(Container container) {
            if (!AudioSampleEntry.$assertionsDisabled && container != AudioSampleEntry.this) {
                throw new AssertionError("you cannot diswown this special box");
            }
        }
    }

    static {
        $assertionsDisabled = !AudioSampleEntry.class.desiredAssertionStatus() ? true : $assertionsDisabled;
    }

    public AudioSampleEntry(String str) {
        super(str);
    }

    public void getBox(WritableByteChannel writableByteChannel) {
        int i = 0;
        writableByteChannel.write(getHeader());
        int i2 = (this.soundVersion == 1 ? 16 : 0) + 28;
        if (this.soundVersion == 2) {
            i = 36;
        }
        ByteBuffer allocate = ByteBuffer.allocate(i2 + i);
        allocate.position(6);
        IsoTypeWriter.writeUInt16(allocate, this.dataReferenceIndex);
        IsoTypeWriter.writeUInt16(allocate, this.soundVersion);
        IsoTypeWriter.writeUInt16(allocate, this.reserved1);
        IsoTypeWriter.writeUInt32(allocate, this.reserved2);
        IsoTypeWriter.writeUInt16(allocate, this.channelCount);
        IsoTypeWriter.writeUInt16(allocate, this.sampleSize);
        IsoTypeWriter.writeUInt16(allocate, this.compressionId);
        IsoTypeWriter.writeUInt16(allocate, this.packetSize);
        if (this.type.equals(TYPE10)) {
            IsoTypeWriter.writeUInt32(allocate, getSampleRate());
        } else {
            IsoTypeWriter.writeUInt32(allocate, getSampleRate() << 16);
        }
        if (this.soundVersion == 1) {
            IsoTypeWriter.writeUInt32(allocate, this.samplesPerPacket);
            IsoTypeWriter.writeUInt32(allocate, this.bytesPerPacket);
            IsoTypeWriter.writeUInt32(allocate, this.bytesPerFrame);
            IsoTypeWriter.writeUInt32(allocate, this.bytesPerSample);
        }
        if (this.soundVersion == 2) {
            IsoTypeWriter.writeUInt32(allocate, this.samplesPerPacket);
            IsoTypeWriter.writeUInt32(allocate, this.bytesPerPacket);
            IsoTypeWriter.writeUInt32(allocate, this.bytesPerFrame);
            IsoTypeWriter.writeUInt32(allocate, this.bytesPerSample);
            allocate.put(this.soundVersion2Data);
        }
        writableByteChannel.write((ByteBuffer) allocate.rewind());
        writeContainer(writableByteChannel);
    }

    public long getBytesPerFrame() {
        return this.bytesPerFrame;
    }

    public long getBytesPerPacket() {
        return this.bytesPerPacket;
    }

    public long getBytesPerSample() {
        return this.bytesPerSample;
    }

    public int getChannelCount() {
        return this.channelCount;
    }

    public int getCompressionId() {
        return this.compressionId;
    }

    public int getPacketSize() {
        return this.packetSize;
    }

    public int getReserved1() {
        return this.reserved1;
    }

    public long getReserved2() {
        return this.reserved2;
    }

    public long getSampleRate() {
        return this.sampleRate;
    }

    public int getSampleSize() {
        return this.sampleSize;
    }

    public long getSamplesPerPacket() {
        return this.samplesPerPacket;
    }

    public long getSize() {
        int i = 16;
        int i2 = 0;
        int i3 = (this.soundVersion == 1 ? 16 : 0) + 28;
        if (this.soundVersion == 2) {
            i2 = 36;
        }
        long containerSize = ((long) (i3 + i2)) + getContainerSize();
        if (!this.largeBox && 8 + containerSize < 4294967296L) {
            i = 8;
        }
        return ((long) i) + containerSize;
    }

    public int getSoundVersion() {
        return this.soundVersion;
    }

    public byte[] getSoundVersion2Data() {
        return this.soundVersion2Data;
    }

    public void parse(DataSource dataSource, ByteBuffer byteBuffer, long j, BoxParser boxParser) {
        int i = 36;
        int i2 = 16;
        int i3 = 0;
        ByteBuffer allocate = ByteBuffer.allocate(28);
        dataSource.read(allocate);
        allocate.position(6);
        this.dataReferenceIndex = IsoTypeReader.readUInt16(allocate);
        this.soundVersion = IsoTypeReader.readUInt16(allocate);
        this.reserved1 = IsoTypeReader.readUInt16(allocate);
        this.reserved2 = IsoTypeReader.readUInt32(allocate);
        this.channelCount = IsoTypeReader.readUInt16(allocate);
        this.sampleSize = IsoTypeReader.readUInt16(allocate);
        this.compressionId = IsoTypeReader.readUInt16(allocate);
        this.packetSize = IsoTypeReader.readUInt16(allocate);
        this.sampleRate = IsoTypeReader.readUInt32(allocate);
        if (!this.type.equals(TYPE10)) {
            this.sampleRate >>>= 16;
        }
        if (this.soundVersion == 1) {
            allocate = ByteBuffer.allocate(16);
            dataSource.read(allocate);
            allocate.rewind();
            this.samplesPerPacket = IsoTypeReader.readUInt32(allocate);
            this.bytesPerPacket = IsoTypeReader.readUInt32(allocate);
            this.bytesPerFrame = IsoTypeReader.readUInt32(allocate);
            this.bytesPerSample = IsoTypeReader.readUInt32(allocate);
        }
        if (this.soundVersion == 2) {
            allocate = ByteBuffer.allocate(36);
            dataSource.read(allocate);
            allocate.rewind();
            this.samplesPerPacket = IsoTypeReader.readUInt32(allocate);
            this.bytesPerPacket = IsoTypeReader.readUInt32(allocate);
            this.bytesPerFrame = IsoTypeReader.readUInt32(allocate);
            this.bytesPerSample = IsoTypeReader.readUInt32(allocate);
            this.soundVersion2Data = new byte[20];
            allocate.get(this.soundVersion2Data);
        }
        if (TYPE7.equals(this.type)) {
            System.err.println(TYPE7);
            long j2 = j - 28;
            if (this.soundVersion != 1) {
                i2 = 0;
            }
            j2 -= (long) i2;
            if (this.soundVersion == 2) {
                i3 = 36;
            }
            long j3 = j2 - ((long) i3);
            ByteBuffer allocate2 = ByteBuffer.allocate(CastUtils.l2i(j3));
            dataSource.read(allocate2);
            addBox(new C00811(j3, allocate2));
            return;
        }
        j2 = j - 28;
        if (this.soundVersion != 1) {
            i2 = 0;
        }
        j2 -= (long) i2;
        if (this.soundVersion != 2) {
            i = 0;
        }
        initContainer(dataSource, j2 - ((long) i), boxParser);
    }

    public void setBytesPerFrame(long j) {
        this.bytesPerFrame = j;
    }

    public void setBytesPerPacket(long j) {
        this.bytesPerPacket = j;
    }

    public void setBytesPerSample(long j) {
        this.bytesPerSample = j;
    }

    public void setChannelCount(int i) {
        this.channelCount = i;
    }

    public void setCompressionId(int i) {
        this.compressionId = i;
    }

    public void setPacketSize(int i) {
        this.packetSize = i;
    }

    public void setReserved1(int i) {
        this.reserved1 = i;
    }

    public void setReserved2(long j) {
        this.reserved2 = j;
    }

    public void setSampleRate(long j) {
        this.sampleRate = j;
    }

    public void setSampleSize(int i) {
        this.sampleSize = i;
    }

    public void setSamplesPerPacket(long j) {
        this.samplesPerPacket = j;
    }

    public void setSoundVersion(int i) {
        this.soundVersion = i;
    }

    public void setSoundVersion2Data(byte[] bArr) {
        this.soundVersion2Data = bArr;
    }

    public void setType(String str) {
        this.type = str;
    }

    public String toString() {
        return "AudioSampleEntry{bytesPerSample=" + this.bytesPerSample + ", bytesPerFrame=" + this.bytesPerFrame + ", bytesPerPacket=" + this.bytesPerPacket + ", samplesPerPacket=" + this.samplesPerPacket + ", packetSize=" + this.packetSize + ", compressionId=" + this.compressionId + ", soundVersion=" + this.soundVersion + ", sampleRate=" + this.sampleRate + ", sampleSize=" + this.sampleSize + ", channelCount=" + this.channelCount + ", boxes=" + getBoxes() + '}';
    }
}
