package com.hanista.mobogram.messenger.exoplayer.text.eia608;

final class ClosedCaptionList implements Comparable<ClosedCaptionList> {
    public final ClosedCaption[] captions;
    public final boolean decodeOnly;
    public final long timeUs;

    public ClosedCaptionList(long j, boolean z, ClosedCaption[] closedCaptionArr) {
        this.timeUs = j;
        this.decodeOnly = z;
        this.captions = closedCaptionArr;
    }

    public int compareTo(ClosedCaptionList closedCaptionList) {
        long j = this.timeUs - closedCaptionList.timeUs;
        return j == 0 ? 0 : j > 0 ? 1 : -1;
    }
}
