package com.coremedia.iso.boxes.mdat;

import com.coremedia.iso.BoxParser;
import com.coremedia.iso.boxes.Box;
import com.coremedia.iso.boxes.Container;
import com.googlecode.mp4parser.DataSource;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.util.logging.Logger;

public final class MediaDataBox implements Box {
    private static Logger LOG = null;
    public static final String TYPE = "mdat";
    private DataSource dataSource;
    boolean largeBox;
    private long offset;
    Container parent;
    private long size;

    static {
        LOG = Logger.getLogger(MediaDataBox.class.getName());
    }

    public MediaDataBox() {
        this.largeBox = false;
    }

    private static void transfer(DataSource dataSource, long j, long j2, WritableByteChannel writableByteChannel) {
        long j3 = 0;
        while (j3 < j2) {
            j3 = dataSource.transferTo(j + j3, Math.min(67076096, j2 - j3), writableByteChannel) + j3;
        }
    }

    public void getBox(WritableByteChannel writableByteChannel) {
        transfer(this.dataSource, this.offset, this.size, writableByteChannel);
    }

    public long getOffset() {
        return this.offset;
    }

    public Container getParent() {
        return this.parent;
    }

    public long getSize() {
        return this.size;
    }

    public String getType() {
        return TYPE;
    }

    public void parse(DataSource dataSource, ByteBuffer byteBuffer, long j, BoxParser boxParser) {
        this.offset = dataSource.position() - ((long) byteBuffer.remaining());
        this.dataSource = dataSource;
        this.size = ((long) byteBuffer.remaining()) + j;
        dataSource.position(dataSource.position() + j);
    }

    public void setParent(Container container) {
        this.parent = container;
    }

    public String toString() {
        return "MediaDataBox{size=" + this.size + '}';
    }
}
