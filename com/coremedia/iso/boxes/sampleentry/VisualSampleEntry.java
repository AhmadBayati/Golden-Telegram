package com.coremedia.iso.boxes.sampleentry;

import android.support.v4.internal.view.SupportMenu;
import com.coremedia.iso.BoxParser;
import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.coremedia.iso.Utf8;
import com.coremedia.iso.boxes.Container;
import com.googlecode.mp4parser.DataSource;
import com.googlecode.mp4parser.util.CastUtils;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;

public final class VisualSampleEntry extends AbstractSampleEntry implements Container {
    static final /* synthetic */ boolean $assertionsDisabled;
    public static final String TYPE1 = "mp4v";
    public static final String TYPE2 = "s263";
    public static final String TYPE3 = "avc1";
    public static final String TYPE4 = "avc3";
    public static final String TYPE5 = "drmi";
    public static final String TYPE6 = "hvc1";
    public static final String TYPE7 = "hev1";
    public static final String TYPE_ENCRYPTED = "encv";
    private String compressorname;
    private int depth;
    private int frameCount;
    private int height;
    private double horizresolution;
    private long[] predefined;
    private double vertresolution;
    private int width;

    /* renamed from: com.coremedia.iso.boxes.sampleentry.VisualSampleEntry.1 */
    class C00821 implements DataSource {
        private final /* synthetic */ DataSource val$dataSource;
        private final /* synthetic */ long val$endPosition;

        C00821(long j, DataSource dataSource) {
            this.val$endPosition = j;
            this.val$dataSource = dataSource;
        }

        public void close() {
            this.val$dataSource.close();
        }

        public ByteBuffer map(long j, long j2) {
            return this.val$dataSource.map(j, j2);
        }

        public long position() {
            return this.val$dataSource.position();
        }

        public void position(long j) {
            this.val$dataSource.position(j);
        }

        public int read(ByteBuffer byteBuffer) {
            if (this.val$endPosition == this.val$dataSource.position()) {
                return -1;
            }
            if (((long) byteBuffer.remaining()) <= this.val$endPosition - this.val$dataSource.position()) {
                return this.val$dataSource.read(byteBuffer);
            }
            ByteBuffer allocate = ByteBuffer.allocate(CastUtils.l2i(this.val$endPosition - this.val$dataSource.position()));
            this.val$dataSource.read(allocate);
            byteBuffer.put((ByteBuffer) allocate.rewind());
            return allocate.capacity();
        }

        public long size() {
            return this.val$endPosition;
        }

        public long transferTo(long j, long j2, WritableByteChannel writableByteChannel) {
            return this.val$dataSource.transferTo(j, j2, writableByteChannel);
        }
    }

    static {
        $assertionsDisabled = !VisualSampleEntry.class.desiredAssertionStatus() ? true : $assertionsDisabled;
    }

    public VisualSampleEntry() {
        super(TYPE3);
        this.horizresolution = 72.0d;
        this.vertresolution = 72.0d;
        this.frameCount = 1;
        this.compressorname = TtmlNode.ANONYMOUS_REGION_ID;
        this.depth = 24;
        this.predefined = new long[3];
    }

    public VisualSampleEntry(String str) {
        super(str);
        this.horizresolution = 72.0d;
        this.vertresolution = 72.0d;
        this.frameCount = 1;
        this.compressorname = TtmlNode.ANONYMOUS_REGION_ID;
        this.depth = 24;
        this.predefined = new long[3];
    }

    public void getBox(WritableByteChannel writableByteChannel) {
        writableByteChannel.write(getHeader());
        ByteBuffer allocate = ByteBuffer.allocate(78);
        allocate.position(6);
        IsoTypeWriter.writeUInt16(allocate, this.dataReferenceIndex);
        IsoTypeWriter.writeUInt16(allocate, 0);
        IsoTypeWriter.writeUInt16(allocate, 0);
        IsoTypeWriter.writeUInt32(allocate, this.predefined[0]);
        IsoTypeWriter.writeUInt32(allocate, this.predefined[1]);
        IsoTypeWriter.writeUInt32(allocate, this.predefined[2]);
        IsoTypeWriter.writeUInt16(allocate, getWidth());
        IsoTypeWriter.writeUInt16(allocate, getHeight());
        IsoTypeWriter.writeFixedPoint1616(allocate, getHorizresolution());
        IsoTypeWriter.writeFixedPoint1616(allocate, getVertresolution());
        IsoTypeWriter.writeUInt32(allocate, 0);
        IsoTypeWriter.writeUInt16(allocate, getFrameCount());
        IsoTypeWriter.writeUInt8(allocate, Utf8.utf8StringLengthInBytes(getCompressorname()));
        allocate.put(Utf8.convert(getCompressorname()));
        int utf8StringLengthInBytes = Utf8.utf8StringLengthInBytes(getCompressorname());
        while (utf8StringLengthInBytes < 31) {
            utf8StringLengthInBytes++;
            allocate.put((byte) 0);
        }
        IsoTypeWriter.writeUInt16(allocate, getDepth());
        IsoTypeWriter.writeUInt16(allocate, SupportMenu.USER_MASK);
        writableByteChannel.write((ByteBuffer) allocate.rewind());
        writeContainer(writableByteChannel);
    }

    public String getCompressorname() {
        return this.compressorname;
    }

    public int getDepth() {
        return this.depth;
    }

    public int getFrameCount() {
        return this.frameCount;
    }

    public int getHeight() {
        return this.height;
    }

    public double getHorizresolution() {
        return this.horizresolution;
    }

    public long getSize() {
        long containerSize = getContainerSize();
        long j = containerSize + 78;
        int i = (this.largeBox || (containerSize + 78) + 8 >= 4294967296L) ? 16 : 8;
        return ((long) i) + j;
    }

    public double getVertresolution() {
        return this.vertresolution;
    }

    public int getWidth() {
        return this.width;
    }

    public void parse(DataSource dataSource, ByteBuffer byteBuffer, long j, BoxParser boxParser) {
        long position = dataSource.position() + j;
        ByteBuffer allocate = ByteBuffer.allocate(78);
        dataSource.read(allocate);
        allocate.position(6);
        this.dataReferenceIndex = IsoTypeReader.readUInt16(allocate);
        long readUInt16 = (long) IsoTypeReader.readUInt16(allocate);
        if ($assertionsDisabled || 0 == readUInt16) {
            readUInt16 = (long) IsoTypeReader.readUInt16(allocate);
            if ($assertionsDisabled || 0 == readUInt16) {
                this.predefined[0] = IsoTypeReader.readUInt32(allocate);
                this.predefined[1] = IsoTypeReader.readUInt32(allocate);
                this.predefined[2] = IsoTypeReader.readUInt32(allocate);
                this.width = IsoTypeReader.readUInt16(allocate);
                this.height = IsoTypeReader.readUInt16(allocate);
                this.horizresolution = IsoTypeReader.readFixedPoint1616(allocate);
                this.vertresolution = IsoTypeReader.readFixedPoint1616(allocate);
                readUInt16 = IsoTypeReader.readUInt32(allocate);
                if ($assertionsDisabled || 0 == readUInt16) {
                    this.frameCount = IsoTypeReader.readUInt16(allocate);
                    int readUInt8 = IsoTypeReader.readUInt8(allocate);
                    if (readUInt8 > 31) {
                        readUInt8 = 31;
                    }
                    byte[] bArr = new byte[readUInt8];
                    allocate.get(bArr);
                    this.compressorname = Utf8.convert(bArr);
                    if (readUInt8 < 31) {
                        allocate.get(new byte[(31 - readUInt8)]);
                    }
                    this.depth = IsoTypeReader.readUInt16(allocate);
                    long readUInt162 = (long) IsoTypeReader.readUInt16(allocate);
                    if ($assertionsDisabled || 65535 == readUInt162) {
                        initContainer(new C00821(position, dataSource), j - 78, boxParser);
                        return;
                    }
                    throw new AssertionError();
                }
                throw new AssertionError("reserved byte not 0");
            }
            throw new AssertionError("reserved byte not 0");
        }
        throw new AssertionError("reserved byte not 0");
    }

    public void setCompressorname(String str) {
        this.compressorname = str;
    }

    public void setDepth(int i) {
        this.depth = i;
    }

    public void setFrameCount(int i) {
        this.frameCount = i;
    }

    public void setHeight(int i) {
        this.height = i;
    }

    public void setHorizresolution(double d) {
        this.horizresolution = d;
    }

    public void setType(String str) {
        this.type = str;
    }

    public void setVertresolution(double d) {
        this.vertresolution = d;
    }

    public void setWidth(int i) {
        this.width = i;
    }
}
