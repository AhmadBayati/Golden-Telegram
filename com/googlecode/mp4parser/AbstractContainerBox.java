package com.googlecode.mp4parser;

import com.coremedia.iso.BoxParser;
import com.coremedia.iso.IsoTypeWriter;
import com.coremedia.iso.boxes.Box;
import com.coremedia.iso.boxes.Container;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;

public class AbstractContainerBox extends BasicContainer implements Box {
    protected boolean largeBox;
    private long offset;
    Container parent;
    protected String type;

    public AbstractContainerBox(String str) {
        this.type = str;
    }

    public void getBox(WritableByteChannel writableByteChannel) {
        writableByteChannel.write(getHeader());
        writeContainer(writableByteChannel);
    }

    protected ByteBuffer getHeader() {
        ByteBuffer wrap;
        byte[] bArr;
        if (this.largeBox || getSize() >= 4294967296L) {
            bArr = new byte[16];
            bArr[3] = (byte) 1;
            bArr[4] = this.type.getBytes()[0];
            bArr[5] = this.type.getBytes()[1];
            bArr[6] = this.type.getBytes()[2];
            bArr[7] = this.type.getBytes()[3];
            wrap = ByteBuffer.wrap(bArr);
            wrap.position(8);
            IsoTypeWriter.writeUInt64(wrap, getSize());
        } else {
            bArr = new byte[8];
            bArr[4] = this.type.getBytes()[0];
            bArr[5] = this.type.getBytes()[1];
            bArr[6] = this.type.getBytes()[2];
            bArr[7] = this.type.getBytes()[3];
            wrap = ByteBuffer.wrap(bArr);
            IsoTypeWriter.writeUInt32(wrap, getSize());
        }
        wrap.rewind();
        return wrap;
    }

    public long getOffset() {
        return this.offset;
    }

    public Container getParent() {
        return this.parent;
    }

    public long getSize() {
        long containerSize = getContainerSize();
        int i = (this.largeBox || 8 + containerSize >= 4294967296L) ? 16 : 8;
        return ((long) i) + containerSize;
    }

    public String getType() {
        return this.type;
    }

    public void initContainer(DataSource dataSource, long j, BoxParser boxParser) {
        this.dataSource = dataSource;
        this.parsePosition = dataSource.position();
        long j2 = this.parsePosition;
        int i = (this.largeBox || 8 + j >= 4294967296L) ? 16 : 8;
        this.startPosition = j2 - ((long) i);
        dataSource.position(dataSource.position() + j);
        this.endPosition = dataSource.position();
        this.boxParser = boxParser;
    }

    public void parse(DataSource dataSource, ByteBuffer byteBuffer, long j, BoxParser boxParser) {
        this.offset = dataSource.position() - ((long) byteBuffer.remaining());
        this.largeBox = byteBuffer.remaining() == 16;
        initContainer(dataSource, j, boxParser);
    }

    public void setParent(Container container) {
        this.parent = container;
    }
}
