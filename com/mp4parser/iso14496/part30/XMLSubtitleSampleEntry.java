package com.mp4parser.iso14496.part30;

import com.coremedia.iso.BoxParser;
import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.coremedia.iso.boxes.sampleentry.AbstractSampleEntry;
import com.googlecode.mp4parser.DataSource;
import com.hanista.mobogram.tgnet.TLRPC;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;

public class XMLSubtitleSampleEntry extends AbstractSampleEntry {
    public static final String TYPE = "stpp";
    private String auxiliaryMimeTypes;
    private String namespace;
    private String schemaLocation;

    public XMLSubtitleSampleEntry() {
        super(TYPE);
        this.namespace = TtmlNode.ANONYMOUS_REGION_ID;
        this.schemaLocation = TtmlNode.ANONYMOUS_REGION_ID;
        this.auxiliaryMimeTypes = TtmlNode.ANONYMOUS_REGION_ID;
    }

    public String getAuxiliaryMimeTypes() {
        return this.auxiliaryMimeTypes;
    }

    public void getBox(WritableByteChannel writableByteChannel) {
        writableByteChannel.write(getHeader());
        ByteBuffer allocate = ByteBuffer.allocate((((this.namespace.length() + 8) + this.schemaLocation.length()) + this.auxiliaryMimeTypes.length()) + 3);
        allocate.position(6);
        IsoTypeWriter.writeUInt16(allocate, this.dataReferenceIndex);
        IsoTypeWriter.writeZeroTermUtf8String(allocate, this.namespace);
        IsoTypeWriter.writeZeroTermUtf8String(allocate, this.schemaLocation);
        IsoTypeWriter.writeZeroTermUtf8String(allocate, this.auxiliaryMimeTypes);
        writableByteChannel.write((ByteBuffer) allocate.rewind());
        writeContainer(writableByteChannel);
    }

    public String getNamespace() {
        return this.namespace;
    }

    public String getSchemaLocation() {
        return this.schemaLocation;
    }

    public long getSize() {
        long containerSize = getContainerSize();
        long length = (long) ((((this.namespace.length() + 8) + this.schemaLocation.length()) + this.auxiliaryMimeTypes.length()) + 3);
        long j = containerSize + length;
        int i = (this.largeBox || (containerSize + length) + 8 >= 4294967296L) ? 16 : 8;
        return ((long) i) + j;
    }

    public void parse(DataSource dataSource, ByteBuffer byteBuffer, long j, BoxParser boxParser) {
        ByteBuffer allocate = ByteBuffer.allocate(8);
        dataSource.read((ByteBuffer) allocate.rewind());
        allocate.position(6);
        this.dataReferenceIndex = IsoTypeReader.readUInt16(allocate);
        long position = dataSource.position();
        allocate = ByteBuffer.allocate(TLRPC.MESSAGE_FLAG_HAS_VIEWS);
        dataSource.read((ByteBuffer) allocate.rewind());
        this.namespace = IsoTypeReader.readString((ByteBuffer) allocate.rewind());
        dataSource.position((((long) this.namespace.length()) + position) + 1);
        dataSource.read((ByteBuffer) allocate.rewind());
        this.schemaLocation = IsoTypeReader.readString((ByteBuffer) allocate.rewind());
        dataSource.position(((((long) this.namespace.length()) + position) + ((long) this.schemaLocation.length())) + 2);
        dataSource.read((ByteBuffer) allocate.rewind());
        this.auxiliaryMimeTypes = IsoTypeReader.readString((ByteBuffer) allocate.rewind());
        dataSource.position((((((long) this.namespace.length()) + position) + ((long) this.schemaLocation.length())) + ((long) this.auxiliaryMimeTypes.length())) + 3);
        initContainer(dataSource, j - ((long) ((((byteBuffer.remaining() + this.namespace.length()) + this.schemaLocation.length()) + this.auxiliaryMimeTypes.length()) + 3)), boxParser);
    }

    public void setAuxiliaryMimeTypes(String str) {
        this.auxiliaryMimeTypes = str;
    }

    public void setNamespace(String str) {
        this.namespace = str;
    }

    public void setSchemaLocation(String str) {
        this.schemaLocation = str;
    }
}
