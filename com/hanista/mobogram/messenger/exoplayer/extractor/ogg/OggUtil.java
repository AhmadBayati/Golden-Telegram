package com.hanista.mobogram.messenger.exoplayer.extractor.ogg;

import com.hanista.mobogram.messenger.exoplayer.ParserException;
import com.hanista.mobogram.messenger.exoplayer.extractor.ExtractorInput;
import com.hanista.mobogram.messenger.exoplayer.util.NalUnitUtil;
import com.hanista.mobogram.messenger.exoplayer.util.ParsableByteArray;
import com.hanista.mobogram.messenger.exoplayer.util.Util;
import com.hanista.mobogram.tgnet.TLRPC;
import java.io.EOFException;

final class OggUtil {
    public static final int PAGE_HEADER_SIZE = 27;
    private static final int TYPE_OGGS;

    public static class PacketInfoHolder {
        public int segmentCount;
        public int size;
    }

    public static final class PageHeader {
        public int bodySize;
        public long granulePosition;
        public int headerSize;
        public final int[] laces;
        public long pageChecksum;
        public int pageSegmentCount;
        public long pageSequenceNumber;
        public int revision;
        public long streamSerialNumber;
        public int type;

        public PageHeader() {
            this.laces = new int[NalUnitUtil.EXTENDED_SAR];
        }

        public void reset() {
            this.revision = 0;
            this.type = 0;
            this.granulePosition = 0;
            this.streamSerialNumber = 0;
            this.pageSequenceNumber = 0;
            this.pageChecksum = 0;
            this.pageSegmentCount = 0;
            this.headerSize = 0;
            this.bodySize = 0;
        }
    }

    static {
        TYPE_OGGS = Util.getIntegerCodeForString("OggS");
    }

    OggUtil() {
    }

    public static void calculatePacketSize(PageHeader pageHeader, int i, PacketInfoHolder packetInfoHolder) {
        packetInfoHolder.segmentCount = 0;
        packetInfoHolder.size = 0;
        while (packetInfoHolder.segmentCount + i < pageHeader.pageSegmentCount) {
            int[] iArr = pageHeader.laces;
            int i2 = packetInfoHolder.segmentCount;
            packetInfoHolder.segmentCount = i2 + 1;
            int i3 = iArr[i2 + i];
            packetInfoHolder.size += i3;
            if (i3 != NalUnitUtil.EXTENDED_SAR) {
                return;
            }
        }
    }

    public static boolean populatePageHeader(ExtractorInput extractorInput, PageHeader pageHeader, ParsableByteArray parsableByteArray, boolean z) {
        int i = 0;
        parsableByteArray.reset();
        pageHeader.reset();
        int i2 = (extractorInput.getLength() == -1 || extractorInput.getLength() - extractorInput.getPeekPosition() >= 27) ? true : 0;
        if (i2 == 0 || !extractorInput.peekFully(parsableByteArray.data, 0, PAGE_HEADER_SIZE, true)) {
            if (z) {
                return false;
            }
            throw new EOFException();
        } else if (parsableByteArray.readUnsignedInt() == ((long) TYPE_OGGS)) {
            pageHeader.revision = parsableByteArray.readUnsignedByte();
            if (pageHeader.revision == 0) {
                pageHeader.type = parsableByteArray.readUnsignedByte();
                pageHeader.granulePosition = parsableByteArray.readLittleEndianLong();
                pageHeader.streamSerialNumber = parsableByteArray.readLittleEndianUnsignedInt();
                pageHeader.pageSequenceNumber = parsableByteArray.readLittleEndianUnsignedInt();
                pageHeader.pageChecksum = parsableByteArray.readLittleEndianUnsignedInt();
                pageHeader.pageSegmentCount = parsableByteArray.readUnsignedByte();
                parsableByteArray.reset();
                pageHeader.headerSize = pageHeader.pageSegmentCount + PAGE_HEADER_SIZE;
                extractorInput.peekFully(parsableByteArray.data, 0, pageHeader.pageSegmentCount);
                while (i < pageHeader.pageSegmentCount) {
                    pageHeader.laces[i] = parsableByteArray.readUnsignedByte();
                    pageHeader.bodySize += pageHeader.laces[i];
                    i++;
                }
                return true;
            } else if (z) {
                return false;
            } else {
                throw new ParserException("unsupported bit stream revision");
            }
        } else if (z) {
            return false;
        } else {
            throw new ParserException("expected OggS capture pattern at begin of page");
        }
    }

    public static int readBits(byte b, int i, int i2) {
        return (b >> i2) & (NalUnitUtil.EXTENDED_SAR >>> (8 - i));
    }

    public static void skipToNextPage(ExtractorInput extractorInput) {
        byte[] bArr = new byte[TLRPC.MESSAGE_FLAG_HAS_BOT_ID];
        int length = bArr.length;
        while (true) {
            if (extractorInput.getLength() != -1 && extractorInput.getPosition() + ((long) r0) > extractorInput.getLength()) {
                length = (int) (extractorInput.getLength() - extractorInput.getPosition());
                if (length < 4) {
                    break;
                }
            }
            extractorInput.peekFully(bArr, 0, length, false);
            int i = 0;
            while (i < length - 3) {
                if (bArr[i] == 79 && bArr[i + 1] == (byte) 103 && bArr[i + 2] == (byte) 103 && bArr[i + 3] == 83) {
                    extractorInput.skipFully(i);
                    return;
                }
                i++;
            }
            extractorInput.skipFully(length - 3);
        }
        throw new EOFException();
    }
}
