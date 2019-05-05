package com.hanista.mobogram.messenger.exoplayer.upstream;

import com.hanista.mobogram.messenger.exoplayer.util.Assertions;
import java.util.Arrays;

public final class DefaultAllocator implements Allocator {
    private static final int AVAILABLE_EXTRA_CAPACITY = 100;
    private int allocatedCount;
    private Allocation[] availableAllocations;
    private int availableCount;
    private final int individualAllocationSize;
    private final byte[] initialAllocationBlock;

    public DefaultAllocator(int i) {
        this(i, 0);
    }

    public DefaultAllocator(int i, int i2) {
        boolean z = true;
        int i3 = 0;
        Assertions.checkArgument(i > 0);
        if (i2 < 0) {
            z = false;
        }
        Assertions.checkArgument(z);
        this.individualAllocationSize = i;
        this.availableCount = i2;
        this.availableAllocations = new Allocation[(i2 + AVAILABLE_EXTRA_CAPACITY)];
        if (i2 > 0) {
            this.initialAllocationBlock = new byte[(i2 * i)];
            while (i3 < i2) {
                this.availableAllocations[i3] = new Allocation(this.initialAllocationBlock, i3 * i);
                i3++;
            }
            return;
        }
        this.initialAllocationBlock = null;
    }

    public synchronized Allocation allocate() {
        Allocation allocation;
        this.allocatedCount++;
        if (this.availableCount > 0) {
            Allocation[] allocationArr = this.availableAllocations;
            int i = this.availableCount - 1;
            this.availableCount = i;
            allocation = allocationArr[i];
            this.availableAllocations[this.availableCount] = null;
        } else {
            allocation = new Allocation(new byte[this.individualAllocationSize], 0);
        }
        return allocation;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized void blockWhileTotalBytesAllocatedExceeds(int r2) {
        /*
        r1 = this;
        monitor-enter(r1);
    L_0x0001:
        r0 = r1.getTotalBytesAllocated();	 Catch:{ all -> 0x000b }
        if (r0 <= r2) goto L_0x000e;
    L_0x0007:
        r1.wait();	 Catch:{ all -> 0x000b }
        goto L_0x0001;
    L_0x000b:
        r0 = move-exception;
        monitor-exit(r1);
        throw r0;
    L_0x000e:
        monitor-exit(r1);
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.hanista.mobogram.messenger.exoplayer.upstream.DefaultAllocator.blockWhileTotalBytesAllocatedExceeds(int):void");
    }

    public int getIndividualAllocationLength() {
        return this.individualAllocationSize;
    }

    public synchronized int getTotalBytesAllocated() {
        return this.allocatedCount * this.individualAllocationSize;
    }

    public synchronized void release(Allocation allocation) {
        boolean z = allocation.data == this.initialAllocationBlock || allocation.data.length == this.individualAllocationSize;
        Assertions.checkArgument(z);
        this.allocatedCount--;
        if (this.availableCount == this.availableAllocations.length) {
            this.availableAllocations = (Allocation[]) Arrays.copyOf(this.availableAllocations, this.availableAllocations.length * 2);
        }
        Allocation[] allocationArr = this.availableAllocations;
        int i = this.availableCount;
        this.availableCount = i + 1;
        allocationArr[i] = allocation;
        notifyAll();
    }

    public synchronized void release(Allocation[] allocationArr) {
        if (this.availableCount + allocationArr.length >= this.availableAllocations.length) {
            this.availableAllocations = (Allocation[]) Arrays.copyOf(this.availableAllocations, Math.max(this.availableAllocations.length * 2, this.availableCount + allocationArr.length));
        }
        for (Allocation allocation : allocationArr) {
            boolean z = allocation.data == this.initialAllocationBlock || allocation.data.length == this.individualAllocationSize;
            Assertions.checkArgument(z);
            Allocation[] allocationArr2 = this.availableAllocations;
            int i = this.availableCount;
            this.availableCount = i + 1;
            allocationArr2[i] = allocation;
        }
        this.allocatedCount -= allocationArr.length;
        notifyAll();
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized void trim(int r8) {
        /*
        r7 = this;
        r1 = 0;
        monitor-enter(r7);
        r0 = r7.individualAllocationSize;	 Catch:{ all -> 0x005c }
        r0 = com.hanista.mobogram.messenger.exoplayer.util.Util.ceilDivide(r8, r0);	 Catch:{ all -> 0x005c }
        r2 = 0;
        r3 = r7.allocatedCount;	 Catch:{ all -> 0x005c }
        r0 = r0 - r3;
        r3 = java.lang.Math.max(r2, r0);	 Catch:{ all -> 0x005c }
        r0 = r7.availableCount;	 Catch:{ all -> 0x005c }
        if (r3 < r0) goto L_0x0016;
    L_0x0014:
        monitor-exit(r7);
        return;
    L_0x0016:
        r0 = r7.initialAllocationBlock;	 Catch:{ all -> 0x005c }
        if (r0 == 0) goto L_0x005f;
    L_0x001a:
        r0 = r7.availableCount;	 Catch:{ all -> 0x005c }
        r0 = r0 + -1;
    L_0x001e:
        if (r1 > r0) goto L_0x0049;
    L_0x0020:
        r2 = r7.availableAllocations;	 Catch:{ all -> 0x005c }
        r4 = r2[r1];	 Catch:{ all -> 0x005c }
        r2 = r4.data;	 Catch:{ all -> 0x005c }
        r5 = r7.initialAllocationBlock;	 Catch:{ all -> 0x005c }
        if (r2 != r5) goto L_0x002d;
    L_0x002a:
        r1 = r1 + 1;
        goto L_0x001e;
    L_0x002d:
        r2 = r7.availableAllocations;	 Catch:{ all -> 0x005c }
        r5 = r2[r1];	 Catch:{ all -> 0x005c }
        r2 = r5.data;	 Catch:{ all -> 0x005c }
        r6 = r7.initialAllocationBlock;	 Catch:{ all -> 0x005c }
        if (r2 == r6) goto L_0x003a;
    L_0x0037:
        r0 = r0 + -1;
        goto L_0x001e;
    L_0x003a:
        r6 = r7.availableAllocations;	 Catch:{ all -> 0x005c }
        r2 = r1 + 1;
        r6[r1] = r5;	 Catch:{ all -> 0x005c }
        r5 = r7.availableAllocations;	 Catch:{ all -> 0x005c }
        r1 = r0 + -1;
        r5[r0] = r4;	 Catch:{ all -> 0x005c }
        r0 = r1;
        r1 = r2;
        goto L_0x001e;
    L_0x0049:
        r0 = java.lang.Math.max(r3, r1);	 Catch:{ all -> 0x005c }
        r1 = r7.availableCount;	 Catch:{ all -> 0x005c }
        if (r0 >= r1) goto L_0x0014;
    L_0x0051:
        r1 = r7.availableAllocations;	 Catch:{ all -> 0x005c }
        r2 = r7.availableCount;	 Catch:{ all -> 0x005c }
        r3 = 0;
        java.util.Arrays.fill(r1, r0, r2, r3);	 Catch:{ all -> 0x005c }
        r7.availableCount = r0;	 Catch:{ all -> 0x005c }
        goto L_0x0014;
    L_0x005c:
        r0 = move-exception;
        monitor-exit(r7);
        throw r0;
    L_0x005f:
        r0 = r3;
        goto L_0x0051;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.hanista.mobogram.messenger.exoplayer.upstream.DefaultAllocator.trim(int):void");
    }
}
