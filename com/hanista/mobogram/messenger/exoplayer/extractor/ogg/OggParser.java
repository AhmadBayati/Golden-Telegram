package com.hanista.mobogram.messenger.exoplayer.extractor.ogg;

import com.hanista.mobogram.messenger.exoplayer.ParserException;
import com.hanista.mobogram.messenger.exoplayer.extractor.ExtractorInput;
import com.hanista.mobogram.messenger.exoplayer.extractor.ogg.OggUtil.PacketInfoHolder;
import com.hanista.mobogram.messenger.exoplayer.extractor.ogg.OggUtil.PageHeader;
import com.hanista.mobogram.messenger.exoplayer.util.Assertions;
import com.hanista.mobogram.messenger.exoplayer.util.ParsableByteArray;

final class OggParser {
    public static final int OGG_MAX_SEGMENT_SIZE = 255;
    private int currentSegmentIndex;
    private long elapsedSamples;
    private final ParsableByteArray headerArray;
    private final PacketInfoHolder holder;
    private final PageHeader pageHeader;

    OggParser() {
        this.pageHeader = new PageHeader();
        this.headerArray = new ParsableByteArray(282);
        this.holder = new PacketInfoHolder();
        this.currentSegmentIndex = -1;
    }

    public PageHeader getPageHeader() {
        return this.pageHeader;
    }

    public long readGranuleOfLastPage(ExtractorInput extractorInput) {
        Assertions.checkArgument(extractorInput.getLength() != -1);
        OggUtil.skipToNextPage(extractorInput);
        this.pageHeader.reset();
        while ((this.pageHeader.type & 4) != 4 && extractorInput.getPosition() < extractorInput.getLength()) {
            OggUtil.populatePageHeader(extractorInput, this.pageHeader, this.headerArray, false);
            extractorInput.skipFully(this.pageHeader.headerSize + this.pageHeader.bodySize);
        }
        return this.pageHeader.granulePosition;
    }

    public boolean readPacket(ExtractorInput extractorInput, ParsableByteArray parsableByteArray) {
        boolean z = (extractorInput == null || parsableByteArray == null) ? false : true;
        Assertions.checkState(z);
        for (boolean z2 = false; !z2; z2 = z) {
            int i;
            if (this.currentSegmentIndex < 0) {
                if (!OggUtil.populatePageHeader(extractorInput, this.pageHeader, this.headerArray, true)) {
                    return false;
                }
                int i2 = this.pageHeader.headerSize;
                if ((this.pageHeader.type & 1) == 1 && parsableByteArray.limit() == 0) {
                    OggUtil.calculatePacketSize(this.pageHeader, 0, this.holder);
                    i = this.holder.segmentCount + 0;
                    i2 += this.holder.size;
                } else {
                    i = 0;
                }
                extractorInput.skipFully(i2);
                this.currentSegmentIndex = i;
            }
            OggUtil.calculatePacketSize(this.pageHeader, this.currentSegmentIndex, this.holder);
            i = this.holder.segmentCount + this.currentSegmentIndex;
            if (this.holder.size > 0) {
                extractorInput.readFully(parsableByteArray.data, parsableByteArray.limit(), this.holder.size);
                parsableByteArray.setLimit(parsableByteArray.limit() + this.holder.size);
                z = this.pageHeader.laces[i + -1] != OGG_MAX_SEGMENT_SIZE;
            } else {
                z = z2;
            }
            if (i == this.pageHeader.pageSegmentCount) {
                i = -1;
            }
            this.currentSegmentIndex = i;
        }
        return true;
    }

    public void reset() {
        this.pageHeader.reset();
        this.headerArray.reset();
        this.currentSegmentIndex = -1;
    }

    public long skipToPageOfGranule(ExtractorInput extractorInput, long j) {
        OggUtil.skipToNextPage(extractorInput);
        OggUtil.populatePageHeader(extractorInput, this.pageHeader, this.headerArray, false);
        while (this.pageHeader.granulePosition < j) {
            extractorInput.skipFully(this.pageHeader.headerSize + this.pageHeader.bodySize);
            this.elapsedSamples = this.pageHeader.granulePosition;
            OggUtil.populatePageHeader(extractorInput, this.pageHeader, this.headerArray, false);
        }
        if (this.elapsedSamples == 0) {
            throw new ParserException();
        }
        extractorInput.resetPeekPosition();
        long j2 = this.elapsedSamples;
        this.elapsedSamples = 0;
        this.currentSegmentIndex = -1;
        return j2;
    }
}
