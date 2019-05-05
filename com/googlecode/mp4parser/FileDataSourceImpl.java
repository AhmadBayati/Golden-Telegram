package com.googlecode.mp4parser;

import android.support.v4.os.EnvironmentCompat;
import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.nio.channels.WritableByteChannel;

public class FileDataSourceImpl implements DataSource {
    FileChannel fc;
    String filename;

    public FileDataSourceImpl(File file) {
        this.fc = new FileInputStream(file).getChannel();
        this.filename = file.getName();
    }

    public FileDataSourceImpl(String str) {
        File file = new File(str);
        this.fc = new FileInputStream(file).getChannel();
        this.filename = file.getName();
    }

    public FileDataSourceImpl(FileChannel fileChannel) {
        this.fc = fileChannel;
        this.filename = EnvironmentCompat.MEDIA_UNKNOWN;
    }

    public FileDataSourceImpl(FileChannel fileChannel, String str) {
        this.fc = fileChannel;
        this.filename = str;
    }

    public void close() {
        this.fc.close();
    }

    public ByteBuffer map(long j, long j2) {
        return this.fc.map(MapMode.READ_ONLY, j, j2);
    }

    public long position() {
        return this.fc.position();
    }

    public void position(long j) {
        this.fc.position(j);
    }

    public int read(ByteBuffer byteBuffer) {
        return this.fc.read(byteBuffer);
    }

    public long size() {
        return this.fc.size();
    }

    public String toString() {
        return this.filename;
    }

    public long transferTo(long j, long j2, WritableByteChannel writableByteChannel) {
        return this.fc.transferTo(j, j2, writableByteChannel);
    }
}
