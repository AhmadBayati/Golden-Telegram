package com.googlecode.mp4parser.boxes.mp4.samplegrouping;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.googlecode.mp4parser.util.CastUtils;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

public class RateShareEntry extends GroupEntry {
    public static final String TYPE = "rash";
    private short discardPriority;
    private List<Entry> entries;
    private int maximumBitrate;
    private int minimumBitrate;
    private short operationPointCut;
    private short targetRateShare;

    public static class Entry {
        int availableBitrate;
        short targetRateShare;

        public Entry(int i, short s) {
            this.availableBitrate = i;
            this.targetRateShare = s;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            Entry entry = (Entry) obj;
            return this.availableBitrate != entry.availableBitrate ? false : this.targetRateShare == entry.targetRateShare;
        }

        public int getAvailableBitrate() {
            return this.availableBitrate;
        }

        public short getTargetRateShare() {
            return this.targetRateShare;
        }

        public int hashCode() {
            return (this.availableBitrate * 31) + this.targetRateShare;
        }

        public void setAvailableBitrate(int i) {
            this.availableBitrate = i;
        }

        public void setTargetRateShare(short s) {
            this.targetRateShare = s;
        }

        public String toString() {
            return "{availableBitrate=" + this.availableBitrate + ", targetRateShare=" + this.targetRateShare + '}';
        }
    }

    public RateShareEntry() {
        this.entries = new LinkedList();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        RateShareEntry rateShareEntry = (RateShareEntry) obj;
        if (this.discardPriority != rateShareEntry.discardPriority) {
            return false;
        }
        if (this.maximumBitrate != rateShareEntry.maximumBitrate) {
            return false;
        }
        if (this.minimumBitrate != rateShareEntry.minimumBitrate) {
            return false;
        }
        if (this.operationPointCut != rateShareEntry.operationPointCut) {
            return false;
        }
        if (this.targetRateShare != rateShareEntry.targetRateShare) {
            return false;
        }
        if (this.entries != null) {
            if (this.entries.equals(rateShareEntry.entries)) {
                return true;
            }
        } else if (rateShareEntry.entries == null) {
            return true;
        }
        return false;
    }

    public ByteBuffer get() {
        ByteBuffer allocate = ByteBuffer.allocate(this.operationPointCut == (short) 1 ? 13 : (this.operationPointCut * 6) + 11);
        allocate.putShort(this.operationPointCut);
        if (this.operationPointCut == (short) 1) {
            allocate.putShort(this.targetRateShare);
        } else {
            for (Entry entry : this.entries) {
                allocate.putInt(entry.getAvailableBitrate());
                allocate.putShort(entry.getTargetRateShare());
            }
        }
        allocate.putInt(this.maximumBitrate);
        allocate.putInt(this.minimumBitrate);
        IsoTypeWriter.writeUInt8(allocate, this.discardPriority);
        allocate.rewind();
        return allocate;
    }

    public short getDiscardPriority() {
        return this.discardPriority;
    }

    public List<Entry> getEntries() {
        return this.entries;
    }

    public int getMaximumBitrate() {
        return this.maximumBitrate;
    }

    public int getMinimumBitrate() {
        return this.minimumBitrate;
    }

    public short getOperationPointCut() {
        return this.operationPointCut;
    }

    public short getTargetRateShare() {
        return this.targetRateShare;
    }

    public String getType() {
        return TYPE;
    }

    public int hashCode() {
        return (((((((this.entries != null ? this.entries.hashCode() : 0) + (((this.operationPointCut * 31) + this.targetRateShare) * 31)) * 31) + this.maximumBitrate) * 31) + this.minimumBitrate) * 31) + this.discardPriority;
    }

    public void parse(ByteBuffer byteBuffer) {
        this.operationPointCut = byteBuffer.getShort();
        if (this.operationPointCut != (short) 1) {
            int i = this.operationPointCut;
            while (true) {
                int i2 = i - 1;
                if (i <= 0) {
                    break;
                }
                this.entries.add(new Entry(CastUtils.l2i(IsoTypeReader.readUInt32(byteBuffer)), byteBuffer.getShort()));
                i = i2;
            }
        } else {
            this.targetRateShare = byteBuffer.getShort();
        }
        this.maximumBitrate = CastUtils.l2i(IsoTypeReader.readUInt32(byteBuffer));
        this.minimumBitrate = CastUtils.l2i(IsoTypeReader.readUInt32(byteBuffer));
        this.discardPriority = (short) IsoTypeReader.readUInt8(byteBuffer);
    }

    public void setDiscardPriority(short s) {
        this.discardPriority = s;
    }

    public void setEntries(List<Entry> list) {
        this.entries = list;
    }

    public void setMaximumBitrate(int i) {
        this.maximumBitrate = i;
    }

    public void setMinimumBitrate(int i) {
        this.minimumBitrate = i;
    }

    public void setOperationPointCut(short s) {
        this.operationPointCut = s;
    }

    public void setTargetRateShare(short s) {
        this.targetRateShare = s;
    }
}
