package com.hanista.mobogram.messenger.exoplayer;

public final class CodecCounters {
    public int codecInitCount;
    public int codecReleaseCount;
    public int droppedOutputBufferCount;
    public int inputBufferCount;
    public int maxConsecutiveDroppedOutputBufferCount;
    public int outputBuffersChangedCount;
    public int outputFormatChangedCount;
    public int renderedOutputBufferCount;
    public int skippedOutputBufferCount;

    public synchronized void ensureUpdated() {
    }

    public String getDebugString() {
        ensureUpdated();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("cic:").append(this.codecInitCount);
        stringBuilder.append(" crc:").append(this.codecReleaseCount);
        stringBuilder.append(" ibc:").append(this.inputBufferCount);
        stringBuilder.append(" ofc:").append(this.outputFormatChangedCount);
        stringBuilder.append(" obc:").append(this.outputBuffersChangedCount);
        stringBuilder.append(" ren:").append(this.renderedOutputBufferCount);
        stringBuilder.append(" sob:").append(this.skippedOutputBufferCount);
        stringBuilder.append(" dob:").append(this.droppedOutputBufferCount);
        stringBuilder.append(" mcdob:").append(this.maxConsecutiveDroppedOutputBufferCount);
        return stringBuilder.toString();
    }
}
