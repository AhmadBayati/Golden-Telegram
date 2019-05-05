package com.hanista.mobogram.messenger.exoplayer.extractor;

import com.hanista.mobogram.messenger.MessagesController;
import com.hanista.mobogram.messenger.exoplayer.upstream.DataSource;
import com.hanista.mobogram.messenger.support.widget.RecyclerView.ItemAnimator;
import java.io.EOFException;
import java.util.Arrays;

public final class DefaultExtractorInput implements ExtractorInput {
    private static final byte[] SCRATCH_SPACE;
    private final DataSource dataSource;
    private byte[] peekBuffer;
    private int peekBufferLength;
    private int peekBufferPosition;
    private long position;
    private final long streamLength;

    static {
        SCRATCH_SPACE = new byte[ItemAnimator.FLAG_APPEARED_IN_PRE_LAYOUT];
    }

    public DefaultExtractorInput(DataSource dataSource, long j, long j2) {
        this.dataSource = dataSource;
        this.position = j;
        this.streamLength = j2;
        this.peekBuffer = new byte[MessagesController.UPDATE_MASK_CHANNEL];
    }

    private void commitBytesRead(int i) {
        if (i != -1) {
            this.position += (long) i;
        }
    }

    private void ensureSpaceForPeek(int i) {
        int i2 = this.peekBufferPosition + i;
        if (i2 > this.peekBuffer.length) {
            this.peekBuffer = Arrays.copyOf(this.peekBuffer, Math.max(this.peekBuffer.length * 2, i2));
        }
    }

    private int readFromDataSource(byte[] bArr, int i, int i2, int i3, boolean z) {
        if (Thread.interrupted()) {
            throw new InterruptedException();
        }
        int read = this.dataSource.read(bArr, i + i3, i2 - i3);
        if (read != -1) {
            return i3 + read;
        }
        if (i3 == 0 && z) {
            return -1;
        }
        throw new EOFException();
    }

    private int readFromPeekBuffer(byte[] bArr, int i, int i2) {
        if (this.peekBufferLength == 0) {
            return 0;
        }
        int min = Math.min(this.peekBufferLength, i2);
        System.arraycopy(this.peekBuffer, 0, bArr, i, min);
        updatePeekBuffer(min);
        return min;
    }

    private int skipFromPeekBuffer(int i) {
        int min = Math.min(this.peekBufferLength, i);
        updatePeekBuffer(min);
        return min;
    }

    private void updatePeekBuffer(int i) {
        this.peekBufferLength -= i;
        this.peekBufferPosition = 0;
        System.arraycopy(this.peekBuffer, i, this.peekBuffer, 0, this.peekBufferLength);
    }

    public void advancePeekPosition(int i) {
        advancePeekPosition(i, false);
    }

    public boolean advancePeekPosition(int i, boolean z) {
        ensureSpaceForPeek(i);
        int min = Math.min(this.peekBufferLength - this.peekBufferPosition, i);
        this.peekBufferLength += i - min;
        while (min < i) {
            min = readFromDataSource(this.peekBuffer, this.peekBufferPosition, i, min, z);
            if (min == -1) {
                return false;
            }
        }
        this.peekBufferPosition += i;
        return true;
    }

    public long getLength() {
        return this.streamLength;
    }

    public long getPeekPosition() {
        return this.position + ((long) this.peekBufferPosition);
    }

    public long getPosition() {
        return this.position;
    }

    public void peekFully(byte[] bArr, int i, int i2) {
        peekFully(bArr, i, i2, false);
    }

    public boolean peekFully(byte[] bArr, int i, int i2, boolean z) {
        if (!advancePeekPosition(i2, z)) {
            return false;
        }
        System.arraycopy(this.peekBuffer, this.peekBufferPosition - i2, bArr, i, i2);
        return true;
    }

    public int read(byte[] bArr, int i, int i2) {
        int readFromPeekBuffer = readFromPeekBuffer(bArr, i, i2);
        if (readFromPeekBuffer == 0) {
            readFromPeekBuffer = readFromDataSource(bArr, i, i2, 0, true);
        }
        commitBytesRead(readFromPeekBuffer);
        return readFromPeekBuffer;
    }

    public void readFully(byte[] bArr, int i, int i2) {
        readFully(bArr, i, i2, false);
    }

    public boolean readFully(byte[] bArr, int i, int i2, boolean z) {
        int readFromPeekBuffer = readFromPeekBuffer(bArr, i, i2);
        while (readFromPeekBuffer < i2 && readFromPeekBuffer != -1) {
            readFromPeekBuffer = readFromDataSource(bArr, i, i2, readFromPeekBuffer, z);
        }
        commitBytesRead(readFromPeekBuffer);
        return readFromPeekBuffer != -1;
    }

    public void resetPeekPosition() {
        this.peekBufferPosition = 0;
    }

    public int skip(int i) {
        int skipFromPeekBuffer = skipFromPeekBuffer(i);
        if (skipFromPeekBuffer == 0) {
            skipFromPeekBuffer = readFromDataSource(SCRATCH_SPACE, 0, Math.min(i, SCRATCH_SPACE.length), 0, true);
        }
        commitBytesRead(skipFromPeekBuffer);
        return skipFromPeekBuffer;
    }

    public void skipFully(int i) {
        skipFully(i, false);
    }

    public boolean skipFully(int i, boolean z) {
        int skipFromPeekBuffer = skipFromPeekBuffer(i);
        while (skipFromPeekBuffer < i && skipFromPeekBuffer != -1) {
            skipFromPeekBuffer = readFromDataSource(SCRATCH_SPACE, -skipFromPeekBuffer, Math.min(i, SCRATCH_SPACE.length + skipFromPeekBuffer), skipFromPeekBuffer, z);
        }
        commitBytesRead(skipFromPeekBuffer);
        return skipFromPeekBuffer != -1;
    }
}
