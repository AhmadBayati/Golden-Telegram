package com.googlecode.mp4parser.authoring.tracks;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class CleanInputStream extends FilterInputStream {
    int prev;
    int prevprev;

    public CleanInputStream(InputStream inputStream) {
        super(inputStream);
        this.prevprev = -1;
        this.prev = -1;
    }

    public boolean markSupported() {
        return false;
    }

    public int read() {
        int read = super.read();
        if (read == 3 && this.prevprev == 0 && this.prev == 0) {
            this.prevprev = -1;
            this.prev = -1;
            read = super.read();
        }
        this.prevprev = this.prev;
        this.prev = read;
        return read;
    }

    public int read(byte[] bArr, int i, int i2) {
        if (bArr == null) {
            throw new NullPointerException();
        } else if (i < 0 || i2 < 0 || i2 > bArr.length - i) {
            throw new IndexOutOfBoundsException();
        } else if (i2 == 0) {
            return 0;
        } else {
            int read = read();
            if (read == -1) {
                return -1;
            }
            bArr[i] = (byte) read;
            read = 1;
            while (read < i2) {
                try {
                    int read2 = read();
                    if (read2 == -1) {
                        break;
                    }
                    bArr[i + read] = (byte) read2;
                    read++;
                } catch (IOException e) {
                }
            }
            return read;
        }
    }
}
