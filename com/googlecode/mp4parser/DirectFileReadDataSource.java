package com.googlecode.mp4parser;

import com.googlecode.mp4parser.util.CastUtils;
import java.io.File;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;

public class DirectFileReadDataSource implements DataSource {
    private static final int TRANSFER_SIZE = 8192;
    private String filename;
    private RandomAccessFile raf;

    public DirectFileReadDataSource(File file) {
        this.raf = new RandomAccessFile(file, "r");
        this.filename = file.getName();
    }

    public void close() {
        this.raf.close();
    }

    public ByteBuffer map(long j, long j2) {
        this.raf.seek(j);
        byte[] bArr = new byte[CastUtils.l2i(j2)];
        this.raf.readFully(bArr);
        return ByteBuffer.wrap(bArr);
    }

    public long position() {
        return this.raf.getFilePointer();
    }

    public void position(long j) {
        this.raf.seek(j);
    }

    public int read(ByteBuffer byteBuffer) {
        int remaining = byteBuffer.remaining();
        byte[] bArr = new byte[TRANSFER_SIZE];
        int i = 0;
        int i2 = 0;
        while (i2 < remaining) {
            i = this.raf.read(bArr, 0, Math.min(remaining - i2, TRANSFER_SIZE));
            if (i < 0) {
                break;
            }
            i2 += i;
            byteBuffer.put(bArr, 0, i);
        }
        return (i >= 0 || i2 != 0) ? i2 : -1;
    }

    public int readAllInOnce(ByteBuffer byteBuffer) {
        byte[] bArr = new byte[byteBuffer.remaining()];
        int read = this.raf.read(bArr);
        byteBuffer.put(bArr, 0, read);
        return read;
    }

    public long size() {
        return this.raf.length();
    }

    public String toString() {
        return this.filename;
    }

    public long transferTo(long j, long j2, WritableByteChannel writableByteChannel) {
        return (long) writableByteChannel.write(map(j, j2));
    }
}
