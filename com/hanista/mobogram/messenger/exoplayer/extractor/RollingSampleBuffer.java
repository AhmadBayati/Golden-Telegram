package com.hanista.mobogram.messenger.exoplayer.extractor;

import com.hanista.mobogram.messenger.exoplayer.SampleHolder;
import com.hanista.mobogram.messenger.exoplayer.upstream.Allocation;
import com.hanista.mobogram.messenger.exoplayer.upstream.Allocator;
import com.hanista.mobogram.messenger.exoplayer.upstream.DataSource;
import com.hanista.mobogram.messenger.exoplayer.util.Assertions;
import com.hanista.mobogram.messenger.exoplayer.util.ParsableByteArray;
import com.hanista.mobogram.tgnet.TLRPC;
import java.io.EOFException;
import java.nio.ByteBuffer;
import java.util.concurrent.LinkedBlockingDeque;

final class RollingSampleBuffer {
    private static final int INITIAL_SCRATCH_SIZE = 32;
    private final int allocationLength;
    private final Allocator allocator;
    private final LinkedBlockingDeque<Allocation> dataQueue;
    private final SampleExtrasHolder extrasHolder;
    private final InfoQueue infoQueue;
    private Allocation lastAllocation;
    private int lastAllocationOffset;
    private final ParsableByteArray scratch;
    private long totalBytesDropped;
    private long totalBytesWritten;

    private static final class InfoQueue {
        private static final int SAMPLE_CAPACITY_INCREMENT = 1000;
        private int absoluteReadIndex;
        private int capacity;
        private byte[][] encryptionKeys;
        private int[] flags;
        private long[] offsets;
        private int queueSize;
        private int relativeReadIndex;
        private int relativeWriteIndex;
        private int[] sizes;
        private long[] timesUs;

        public InfoQueue() {
            this.capacity = SAMPLE_CAPACITY_INCREMENT;
            this.offsets = new long[this.capacity];
            this.timesUs = new long[this.capacity];
            this.flags = new int[this.capacity];
            this.sizes = new int[this.capacity];
            this.encryptionKeys = new byte[this.capacity][];
        }

        public void clear() {
            this.absoluteReadIndex = 0;
            this.relativeReadIndex = 0;
            this.relativeWriteIndex = 0;
            this.queueSize = 0;
        }

        public synchronized void commitSample(long j, int i, long j2, int i2, byte[] bArr) {
            this.timesUs[this.relativeWriteIndex] = j;
            this.offsets[this.relativeWriteIndex] = j2;
            this.sizes[this.relativeWriteIndex] = i2;
            this.flags[this.relativeWriteIndex] = i;
            this.encryptionKeys[this.relativeWriteIndex] = bArr;
            this.queueSize++;
            if (this.queueSize == this.capacity) {
                int i3 = this.capacity + SAMPLE_CAPACITY_INCREMENT;
                Object obj = new long[i3];
                Object obj2 = new long[i3];
                Object obj3 = new int[i3];
                Object obj4 = new int[i3];
                Object obj5 = new byte[i3][];
                int i4 = this.capacity - this.relativeReadIndex;
                System.arraycopy(this.offsets, this.relativeReadIndex, obj, 0, i4);
                System.arraycopy(this.timesUs, this.relativeReadIndex, obj2, 0, i4);
                System.arraycopy(this.flags, this.relativeReadIndex, obj3, 0, i4);
                System.arraycopy(this.sizes, this.relativeReadIndex, obj4, 0, i4);
                System.arraycopy(this.encryptionKeys, this.relativeReadIndex, obj5, 0, i4);
                int i5 = this.relativeReadIndex;
                System.arraycopy(this.offsets, 0, obj, i4, i5);
                System.arraycopy(this.timesUs, 0, obj2, i4, i5);
                System.arraycopy(this.flags, 0, obj3, i4, i5);
                System.arraycopy(this.sizes, 0, obj4, i4, i5);
                System.arraycopy(this.encryptionKeys, 0, obj5, i4, i5);
                this.offsets = obj;
                this.timesUs = obj2;
                this.flags = obj3;
                this.sizes = obj4;
                this.encryptionKeys = obj5;
                this.relativeReadIndex = 0;
                this.relativeWriteIndex = this.capacity;
                this.queueSize = this.capacity;
                this.capacity = i3;
            } else {
                this.relativeWriteIndex++;
                if (this.relativeWriteIndex == this.capacity) {
                    this.relativeWriteIndex = 0;
                }
            }
        }

        public long discardUpstreamSamples(int i) {
            int writeIndex = getWriteIndex() - i;
            boolean z = writeIndex >= 0 && writeIndex <= this.queueSize;
            Assertions.checkArgument(z);
            if (writeIndex != 0) {
                this.queueSize -= writeIndex;
                this.relativeWriteIndex = ((this.relativeWriteIndex + this.capacity) - writeIndex) % this.capacity;
                return this.offsets[this.relativeWriteIndex];
            } else if (this.absoluteReadIndex == 0) {
                return 0;
            } else {
                int i2 = (this.relativeWriteIndex == 0 ? this.capacity : this.relativeWriteIndex) - 1;
                return ((long) this.sizes[i2]) + this.offsets[i2];
            }
        }

        public int getReadIndex() {
            return this.absoluteReadIndex;
        }

        public int getWriteIndex() {
            return this.absoluteReadIndex + this.queueSize;
        }

        public synchronized long moveToNextSample() {
            long j;
            this.queueSize--;
            int i = this.relativeReadIndex;
            this.relativeReadIndex = i + 1;
            this.absoluteReadIndex++;
            if (this.relativeReadIndex == this.capacity) {
                this.relativeReadIndex = 0;
            }
            if (this.queueSize > 0) {
                j = this.offsets[this.relativeReadIndex];
            } else {
                j = this.offsets[i] + ((long) this.sizes[i]);
            }
            return j;
        }

        public synchronized boolean peekSample(SampleHolder sampleHolder, SampleExtrasHolder sampleExtrasHolder) {
            boolean z;
            if (this.queueSize == 0) {
                z = false;
            } else {
                sampleHolder.timeUs = this.timesUs[this.relativeReadIndex];
                sampleHolder.size = this.sizes[this.relativeReadIndex];
                sampleHolder.flags = this.flags[this.relativeReadIndex];
                sampleExtrasHolder.offset = this.offsets[this.relativeReadIndex];
                sampleExtrasHolder.encryptionKeyId = this.encryptionKeys[this.relativeReadIndex];
                z = true;
            }
            return z;
        }

        public synchronized long skipToKeyframeBefore(long j) {
            long j2 = -1;
            synchronized (this) {
                if (this.queueSize != 0 && j >= this.timesUs[this.relativeReadIndex]) {
                    if (j <= this.timesUs[(this.relativeWriteIndex == 0 ? this.capacity : this.relativeWriteIndex) - 1]) {
                        int i = 0;
                        int i2 = this.relativeReadIndex;
                        int i3 = -1;
                        while (i2 != this.relativeWriteIndex && this.timesUs[i2] <= j) {
                            if ((this.flags[i2] & 1) != 0) {
                                i3 = i;
                            }
                            i2 = (i2 + 1) % this.capacity;
                            i++;
                        }
                        if (i3 != -1) {
                            this.queueSize -= i3;
                            this.relativeReadIndex = (this.relativeReadIndex + i3) % this.capacity;
                            this.absoluteReadIndex += i3;
                            j2 = this.offsets[this.relativeReadIndex];
                        }
                    }
                }
            }
            return j2;
        }
    }

    private static final class SampleExtrasHolder {
        public byte[] encryptionKeyId;
        public long offset;

        private SampleExtrasHolder() {
        }
    }

    public RollingSampleBuffer(Allocator allocator) {
        this.allocator = allocator;
        this.allocationLength = allocator.getIndividualAllocationLength();
        this.infoQueue = new InfoQueue();
        this.dataQueue = new LinkedBlockingDeque();
        this.extrasHolder = new SampleExtrasHolder();
        this.scratch = new ParsableByteArray((int) INITIAL_SCRATCH_SIZE);
        this.lastAllocationOffset = this.allocationLength;
    }

    private void dropDownstreamTo(long j) {
        int i = ((int) (j - this.totalBytesDropped)) / this.allocationLength;
        for (int i2 = 0; i2 < i; i2++) {
            this.allocator.release((Allocation) this.dataQueue.remove());
            this.totalBytesDropped += (long) this.allocationLength;
        }
    }

    private void dropUpstreamFrom(long j) {
        int i = (int) (j - this.totalBytesDropped);
        int i2 = i % this.allocationLength;
        i = (this.dataQueue.size() - (i / this.allocationLength)) - 1;
        int i3 = i2 == 0 ? i + 1 : i;
        for (int i4 = 0; i4 < i3; i4++) {
            this.allocator.release((Allocation) this.dataQueue.removeLast());
        }
        this.lastAllocation = (Allocation) this.dataQueue.peekLast();
        this.lastAllocationOffset = i2 == 0 ? this.allocationLength : i2;
    }

    private static void ensureCapacity(ParsableByteArray parsableByteArray, int i) {
        if (parsableByteArray.limit() < i) {
            parsableByteArray.reset(new byte[i], i);
        }
    }

    private int prepareForAppend(int i) {
        if (this.lastAllocationOffset == this.allocationLength) {
            this.lastAllocationOffset = 0;
            this.lastAllocation = this.allocator.allocate();
            this.dataQueue.add(this.lastAllocation);
        }
        return Math.min(i, this.allocationLength - this.lastAllocationOffset);
    }

    private void readData(long j, ByteBuffer byteBuffer, int i) {
        while (i > 0) {
            dropDownstreamTo(j);
            int i2 = (int) (j - this.totalBytesDropped);
            int min = Math.min(i, this.allocationLength - i2);
            Allocation allocation = (Allocation) this.dataQueue.peek();
            byteBuffer.put(allocation.data, allocation.translateOffset(i2), min);
            j += (long) min;
            i -= min;
        }
    }

    private void readData(long j, byte[] bArr, int i) {
        int i2 = 0;
        while (i2 < i) {
            dropDownstreamTo(j);
            int i3 = (int) (j - this.totalBytesDropped);
            int min = Math.min(i - i2, this.allocationLength - i3);
            Allocation allocation = (Allocation) this.dataQueue.peek();
            System.arraycopy(allocation.data, allocation.translateOffset(i3), bArr, i2, min);
            j += (long) min;
            i2 += min;
        }
    }

    private void readEncryptionData(SampleHolder sampleHolder, SampleExtrasHolder sampleExtrasHolder) {
        long j;
        int i = 0;
        long j2 = sampleExtrasHolder.offset;
        readData(j2, this.scratch.data, 1);
        long j3 = 1 + j2;
        byte b = this.scratch.data[0];
        int i2 = (b & TLRPC.USER_FLAG_UNUSED) != 0 ? 1 : 0;
        int i3 = b & 127;
        if (sampleHolder.cryptoInfo.iv == null) {
            sampleHolder.cryptoInfo.iv = new byte[16];
        }
        readData(j3, sampleHolder.cryptoInfo.iv, i3);
        j3 += (long) i3;
        if (i2 != 0) {
            readData(j3, this.scratch.data, 2);
            j3 += 2;
            this.scratch.setPosition(0);
            i3 = this.scratch.readUnsignedShort();
            j = j3;
        } else {
            i3 = 1;
            j = j3;
        }
        int[] iArr = sampleHolder.cryptoInfo.numBytesOfClearData;
        if (iArr == null || iArr.length < i3) {
            iArr = new int[i3];
        }
        int[] iArr2 = sampleHolder.cryptoInfo.numBytesOfEncryptedData;
        if (iArr2 == null || iArr2.length < i3) {
            iArr2 = new int[i3];
        }
        if (i2 != 0) {
            i2 = i3 * 6;
            ensureCapacity(this.scratch, i2);
            readData(j, this.scratch.data, i2);
            j += (long) i2;
            this.scratch.setPosition(0);
            while (i < i3) {
                iArr[i] = this.scratch.readUnsignedShort();
                iArr2[i] = this.scratch.readUnsignedIntToInt();
                i++;
            }
        } else {
            iArr[0] = 0;
            iArr2[0] = sampleHolder.size - ((int) (j - sampleExtrasHolder.offset));
        }
        sampleHolder.cryptoInfo.set(i3, iArr, iArr2, sampleExtrasHolder.encryptionKeyId, sampleHolder.cryptoInfo.iv, 1);
        i2 = (int) (j - sampleExtrasHolder.offset);
        sampleExtrasHolder.offset += (long) i2;
        sampleHolder.size -= i2;
    }

    public int appendData(ExtractorInput extractorInput, int i, boolean z) {
        int read = extractorInput.read(this.lastAllocation.data, this.lastAllocation.translateOffset(this.lastAllocationOffset), prepareForAppend(i));
        if (read != -1) {
            this.lastAllocationOffset += read;
            this.totalBytesWritten += (long) read;
            return read;
        } else if (z) {
            return -1;
        } else {
            throw new EOFException();
        }
    }

    public int appendData(DataSource dataSource, int i, boolean z) {
        int read = dataSource.read(this.lastAllocation.data, this.lastAllocation.translateOffset(this.lastAllocationOffset), prepareForAppend(i));
        if (read != -1) {
            this.lastAllocationOffset += read;
            this.totalBytesWritten += (long) read;
            return read;
        } else if (z) {
            return -1;
        } else {
            throw new EOFException();
        }
    }

    public void appendData(ParsableByteArray parsableByteArray, int i) {
        while (i > 0) {
            int prepareForAppend = prepareForAppend(i);
            parsableByteArray.readBytes(this.lastAllocation.data, this.lastAllocation.translateOffset(this.lastAllocationOffset), prepareForAppend);
            this.lastAllocationOffset += prepareForAppend;
            this.totalBytesWritten += (long) prepareForAppend;
            i -= prepareForAppend;
        }
    }

    public void clear() {
        this.infoQueue.clear();
        this.allocator.release((Allocation[]) this.dataQueue.toArray(new Allocation[this.dataQueue.size()]));
        this.dataQueue.clear();
        this.totalBytesDropped = 0;
        this.totalBytesWritten = 0;
        this.lastAllocation = null;
        this.lastAllocationOffset = this.allocationLength;
    }

    public void commitSample(long j, int i, long j2, int i2, byte[] bArr) {
        this.infoQueue.commitSample(j, i, j2, i2, bArr);
    }

    public void discardUpstreamSamples(int i) {
        this.totalBytesWritten = this.infoQueue.discardUpstreamSamples(i);
        dropUpstreamFrom(this.totalBytesWritten);
    }

    public int getReadIndex() {
        return this.infoQueue.getReadIndex();
    }

    public int getWriteIndex() {
        return this.infoQueue.getWriteIndex();
    }

    public long getWritePosition() {
        return this.totalBytesWritten;
    }

    public boolean peekSample(SampleHolder sampleHolder) {
        return this.infoQueue.peekSample(sampleHolder, this.extrasHolder);
    }

    public boolean readSample(SampleHolder sampleHolder) {
        if (!this.infoQueue.peekSample(sampleHolder, this.extrasHolder)) {
            return false;
        }
        if (sampleHolder.isEncrypted()) {
            readEncryptionData(sampleHolder, this.extrasHolder);
        }
        sampleHolder.ensureSpaceForWrite(sampleHolder.size);
        readData(this.extrasHolder.offset, sampleHolder.data, sampleHolder.size);
        dropDownstreamTo(this.infoQueue.moveToNextSample());
        return true;
    }

    public void skipSample() {
        dropDownstreamTo(this.infoQueue.moveToNextSample());
    }

    public boolean skipToKeyframeBefore(long j) {
        long skipToKeyframeBefore = this.infoQueue.skipToKeyframeBefore(j);
        if (skipToKeyframeBefore == -1) {
            return false;
        }
        dropDownstreamTo(skipToKeyframeBefore);
        return true;
    }
}
