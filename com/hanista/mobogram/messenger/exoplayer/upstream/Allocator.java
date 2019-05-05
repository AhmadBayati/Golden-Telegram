package com.hanista.mobogram.messenger.exoplayer.upstream;

public interface Allocator {
    Allocation allocate();

    void blockWhileTotalBytesAllocatedExceeds(int i);

    int getIndividualAllocationLength();

    int getTotalBytesAllocated();

    void release(Allocation allocation);

    void release(Allocation[] allocationArr);

    void trim(int i);
}
