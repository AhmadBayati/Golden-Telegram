package com.mp4parser.iso14496.part15;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.googlecode.mp4parser.boxes.mp4.samplegrouping.GroupEntry;
import com.hanista.mobogram.messenger.exoplayer.extractor.ts.PsExtractor;
import java.nio.ByteBuffer;

public class TemporalLayerSampleGroup extends GroupEntry {
    public static final String TYPE = "tscl";
    int temporalLayerId;
    int tlAvgBitRate;
    int tlAvgFrameRate;
    int tlConstantFrameRate;
    int tlMaxBitRate;
    long tlconstraint_indicator_flags;
    int tllevel_idc;
    long tlprofile_compatibility_flags;
    int tlprofile_idc;
    int tlprofile_space;
    boolean tltier_flag;

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        TemporalLayerSampleGroup temporalLayerSampleGroup = (TemporalLayerSampleGroup) obj;
        return this.temporalLayerId != temporalLayerSampleGroup.temporalLayerId ? false : this.tlAvgBitRate != temporalLayerSampleGroup.tlAvgBitRate ? false : this.tlAvgFrameRate != temporalLayerSampleGroup.tlAvgFrameRate ? false : this.tlConstantFrameRate != temporalLayerSampleGroup.tlConstantFrameRate ? false : this.tlMaxBitRate != temporalLayerSampleGroup.tlMaxBitRate ? false : this.tlconstraint_indicator_flags != temporalLayerSampleGroup.tlconstraint_indicator_flags ? false : this.tllevel_idc != temporalLayerSampleGroup.tllevel_idc ? false : this.tlprofile_compatibility_flags != temporalLayerSampleGroup.tlprofile_compatibility_flags ? false : this.tlprofile_idc != temporalLayerSampleGroup.tlprofile_idc ? false : this.tlprofile_space != temporalLayerSampleGroup.tlprofile_space ? false : this.tltier_flag == temporalLayerSampleGroup.tltier_flag;
    }

    public ByteBuffer get() {
        ByteBuffer allocate = ByteBuffer.allocate(20);
        IsoTypeWriter.writeUInt8(allocate, this.temporalLayerId);
        IsoTypeWriter.writeUInt8(allocate, ((this.tltier_flag ? 32 : 0) + (this.tlprofile_space << 6)) + this.tlprofile_idc);
        IsoTypeWriter.writeUInt32(allocate, this.tlprofile_compatibility_flags);
        IsoTypeWriter.writeUInt48(allocate, this.tlconstraint_indicator_flags);
        IsoTypeWriter.writeUInt8(allocate, this.tllevel_idc);
        IsoTypeWriter.writeUInt16(allocate, this.tlMaxBitRate);
        IsoTypeWriter.writeUInt16(allocate, this.tlAvgBitRate);
        IsoTypeWriter.writeUInt8(allocate, this.tlConstantFrameRate);
        IsoTypeWriter.writeUInt16(allocate, this.tlAvgFrameRate);
        return (ByteBuffer) allocate.rewind();
    }

    public int getTemporalLayerId() {
        return this.temporalLayerId;
    }

    public int getTlAvgBitRate() {
        return this.tlAvgBitRate;
    }

    public int getTlAvgFrameRate() {
        return this.tlAvgFrameRate;
    }

    public int getTlConstantFrameRate() {
        return this.tlConstantFrameRate;
    }

    public int getTlMaxBitRate() {
        return this.tlMaxBitRate;
    }

    public long getTlconstraint_indicator_flags() {
        return this.tlconstraint_indicator_flags;
    }

    public int getTllevel_idc() {
        return this.tllevel_idc;
    }

    public long getTlprofile_compatibility_flags() {
        return this.tlprofile_compatibility_flags;
    }

    public int getTlprofile_idc() {
        return this.tlprofile_idc;
    }

    public int getTlprofile_space() {
        return this.tlprofile_space;
    }

    public String getType() {
        return TYPE;
    }

    public int hashCode() {
        return (((((((((((((((((this.tltier_flag ? 1 : 0) + (((this.temporalLayerId * 31) + this.tlprofile_space) * 31)) * 31) + this.tlprofile_idc) * 31) + ((int) (this.tlprofile_compatibility_flags ^ (this.tlprofile_compatibility_flags >>> 32)))) * 31) + ((int) (this.tlconstraint_indicator_flags ^ (this.tlconstraint_indicator_flags >>> 32)))) * 31) + this.tllevel_idc) * 31) + this.tlMaxBitRate) * 31) + this.tlAvgBitRate) * 31) + this.tlConstantFrameRate) * 31) + this.tlAvgFrameRate;
    }

    public boolean isTltier_flag() {
        return this.tltier_flag;
    }

    public void parse(ByteBuffer byteBuffer) {
        this.temporalLayerId = IsoTypeReader.readUInt8(byteBuffer);
        int readUInt8 = IsoTypeReader.readUInt8(byteBuffer);
        this.tlprofile_space = (readUInt8 & PsExtractor.AUDIO_STREAM) >> 6;
        this.tltier_flag = (readUInt8 & 32) > 0;
        this.tlprofile_idc = readUInt8 & 31;
        this.tlprofile_compatibility_flags = IsoTypeReader.readUInt32(byteBuffer);
        this.tlconstraint_indicator_flags = IsoTypeReader.readUInt48(byteBuffer);
        this.tllevel_idc = IsoTypeReader.readUInt8(byteBuffer);
        this.tlMaxBitRate = IsoTypeReader.readUInt16(byteBuffer);
        this.tlAvgBitRate = IsoTypeReader.readUInt16(byteBuffer);
        this.tlConstantFrameRate = IsoTypeReader.readUInt8(byteBuffer);
        this.tlAvgFrameRate = IsoTypeReader.readUInt16(byteBuffer);
    }

    public void setTemporalLayerId(int i) {
        this.temporalLayerId = i;
    }

    public void setTlAvgBitRate(int i) {
        this.tlAvgBitRate = i;
    }

    public void setTlAvgFrameRate(int i) {
        this.tlAvgFrameRate = i;
    }

    public void setTlConstantFrameRate(int i) {
        this.tlConstantFrameRate = i;
    }

    public void setTlMaxBitRate(int i) {
        this.tlMaxBitRate = i;
    }

    public void setTlconstraint_indicator_flags(long j) {
        this.tlconstraint_indicator_flags = j;
    }

    public void setTllevel_idc(int i) {
        this.tllevel_idc = i;
    }

    public void setTlprofile_compatibility_flags(long j) {
        this.tlprofile_compatibility_flags = j;
    }

    public void setTlprofile_idc(int i) {
        this.tlprofile_idc = i;
    }

    public void setTlprofile_space(int i) {
        this.tlprofile_space = i;
    }

    public void setTltier_flag(boolean z) {
        this.tltier_flag = z;
    }

    public int size() {
        return 20;
    }

    public String toString() {
        return "TemporalLayerSampleGroup{temporalLayerId=" + this.temporalLayerId + ", tlprofile_space=" + this.tlprofile_space + ", tltier_flag=" + this.tltier_flag + ", tlprofile_idc=" + this.tlprofile_idc + ", tlprofile_compatibility_flags=" + this.tlprofile_compatibility_flags + ", tlconstraint_indicator_flags=" + this.tlconstraint_indicator_flags + ", tllevel_idc=" + this.tllevel_idc + ", tlMaxBitRate=" + this.tlMaxBitRate + ", tlAvgBitRate=" + this.tlAvgBitRate + ", tlConstantFrameRate=" + this.tlConstantFrameRate + ", tlAvgFrameRate=" + this.tlAvgFrameRate + '}';
    }
}
