package com.mp4parser.iso14496.part15;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.hanista.mobogram.messenger.exoplayer.extractor.ts.PsExtractor;
import com.hanista.mobogram.tgnet.TLRPC;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

public class HevcDecoderConfigurationRecord {
    List<Array> arrays;
    int avgFrameRate;
    int bitDepthChromaMinus8;
    int bitDepthLumaMinus8;
    int chromaFormat;
    int configurationVersion;
    int constantFrameRate;
    boolean frame_only_constraint_flag;
    long general_constraint_indicator_flags;
    int general_level_idc;
    long general_profile_compatibility_flags;
    int general_profile_idc;
    int general_profile_space;
    boolean general_tier_flag;
    boolean interlaced_source_flag;
    int lengthSizeMinusOne;
    int min_spatial_segmentation_idc;
    boolean non_packed_constraint_flag;
    int numTemporalLayers;
    int parallelismType;
    boolean progressive_source_flag;
    int reserved1;
    int reserved2;
    int reserved3;
    int reserved4;
    int reserved5;
    boolean temporalIdNested;

    public static class Array {
        public boolean array_completeness;
        public List<byte[]> nalUnits;
        public int nal_unit_type;
        public boolean reserved;

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            Array array = (Array) obj;
            if (this.array_completeness != array.array_completeness || this.nal_unit_type != array.nal_unit_type || this.reserved != array.reserved) {
                return false;
            }
            ListIterator listIterator = this.nalUnits.listIterator();
            ListIterator listIterator2 = array.nalUnits.listIterator();
            while (listIterator.hasNext() && listIterator2.hasNext()) {
                byte[] bArr = (byte[]) listIterator.next();
                byte[] bArr2 = (byte[]) listIterator2.next();
                if (bArr == null) {
                    if (bArr2 != null) {
                        return false;
                    }
                } else if (!Arrays.equals(bArr, bArr2)) {
                    return false;
                }
            }
            boolean z = (listIterator.hasNext() || listIterator2.hasNext()) ? false : true;
            return z;
        }

        public int hashCode() {
            int i = 1;
            int i2 = 0;
            int i3 = (this.array_completeness ? 1 : 0) * 31;
            if (!this.reserved) {
                i = 0;
            }
            i3 = (((i3 + i) * 31) + this.nal_unit_type) * 31;
            if (this.nalUnits != null) {
                i2 = this.nalUnits.hashCode();
            }
            return i3 + i2;
        }

        public String toString() {
            return "Array{nal_unit_type=" + this.nal_unit_type + ", reserved=" + this.reserved + ", array_completeness=" + this.array_completeness + ", num_nals=" + this.nalUnits.size() + '}';
        }
    }

    public HevcDecoderConfigurationRecord() {
        this.reserved1 = 15;
        this.reserved2 = 63;
        this.reserved3 = 63;
        this.reserved4 = 31;
        this.reserved5 = 31;
        this.arrays = new ArrayList();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        HevcDecoderConfigurationRecord hevcDecoderConfigurationRecord = (HevcDecoderConfigurationRecord) obj;
        if (this.avgFrameRate != hevcDecoderConfigurationRecord.avgFrameRate) {
            return false;
        }
        if (this.bitDepthChromaMinus8 != hevcDecoderConfigurationRecord.bitDepthChromaMinus8) {
            return false;
        }
        if (this.bitDepthLumaMinus8 != hevcDecoderConfigurationRecord.bitDepthLumaMinus8) {
            return false;
        }
        if (this.chromaFormat != hevcDecoderConfigurationRecord.chromaFormat) {
            return false;
        }
        if (this.configurationVersion != hevcDecoderConfigurationRecord.configurationVersion) {
            return false;
        }
        if (this.constantFrameRate != hevcDecoderConfigurationRecord.constantFrameRate) {
            return false;
        }
        if (this.general_constraint_indicator_flags != hevcDecoderConfigurationRecord.general_constraint_indicator_flags) {
            return false;
        }
        if (this.general_level_idc != hevcDecoderConfigurationRecord.general_level_idc) {
            return false;
        }
        if (this.general_profile_compatibility_flags != hevcDecoderConfigurationRecord.general_profile_compatibility_flags) {
            return false;
        }
        if (this.general_profile_idc != hevcDecoderConfigurationRecord.general_profile_idc) {
            return false;
        }
        if (this.general_profile_space != hevcDecoderConfigurationRecord.general_profile_space) {
            return false;
        }
        if (this.general_tier_flag != hevcDecoderConfigurationRecord.general_tier_flag) {
            return false;
        }
        if (this.lengthSizeMinusOne != hevcDecoderConfigurationRecord.lengthSizeMinusOne) {
            return false;
        }
        if (this.min_spatial_segmentation_idc != hevcDecoderConfigurationRecord.min_spatial_segmentation_idc) {
            return false;
        }
        if (this.numTemporalLayers != hevcDecoderConfigurationRecord.numTemporalLayers) {
            return false;
        }
        if (this.parallelismType != hevcDecoderConfigurationRecord.parallelismType) {
            return false;
        }
        if (this.reserved1 != hevcDecoderConfigurationRecord.reserved1) {
            return false;
        }
        if (this.reserved2 != hevcDecoderConfigurationRecord.reserved2) {
            return false;
        }
        if (this.reserved3 != hevcDecoderConfigurationRecord.reserved3) {
            return false;
        }
        if (this.reserved4 != hevcDecoderConfigurationRecord.reserved4) {
            return false;
        }
        if (this.reserved5 != hevcDecoderConfigurationRecord.reserved5) {
            return false;
        }
        if (this.temporalIdNested != hevcDecoderConfigurationRecord.temporalIdNested) {
            return false;
        }
        if (this.arrays != null) {
            if (this.arrays.equals(hevcDecoderConfigurationRecord.arrays)) {
                return true;
            }
        } else if (hevcDecoderConfigurationRecord.arrays == null) {
            return true;
        }
        return false;
    }

    public List<Array> getArrays() {
        return this.arrays;
    }

    public int getAvgFrameRate() {
        return this.avgFrameRate;
    }

    public int getBitDepthChromaMinus8() {
        return this.bitDepthChromaMinus8;
    }

    public int getBitDepthLumaMinus8() {
        return this.bitDepthLumaMinus8;
    }

    public int getChromaFormat() {
        return this.chromaFormat;
    }

    public int getConfigurationVersion() {
        return this.configurationVersion;
    }

    public int getConstantFrameRate() {
        return this.constantFrameRate;
    }

    public long getGeneral_constraint_indicator_flags() {
        return this.general_constraint_indicator_flags;
    }

    public int getGeneral_level_idc() {
        return this.general_level_idc;
    }

    public long getGeneral_profile_compatibility_flags() {
        return this.general_profile_compatibility_flags;
    }

    public int getGeneral_profile_idc() {
        return this.general_profile_idc;
    }

    public int getGeneral_profile_space() {
        return this.general_profile_space;
    }

    public int getLengthSizeMinusOne() {
        return this.lengthSizeMinusOne;
    }

    public int getMin_spatial_segmentation_idc() {
        return this.min_spatial_segmentation_idc;
    }

    public int getNumTemporalLayers() {
        return this.numTemporalLayers;
    }

    public int getParallelismType() {
        return this.parallelismType;
    }

    public int getSize() {
        int i = 23;
        for (Array array : this.arrays) {
            i += 3;
            for (byte[] length : array.nalUnits) {
                i = (i + 2) + length.length;
            }
        }
        return i;
    }

    public int hashCode() {
        int i = 1;
        int i2 = 0;
        int i3 = ((((((((((((((((((((((((((((((((((((this.general_tier_flag ? 1 : 0) + (((this.configurationVersion * 31) + this.general_profile_space) * 31)) * 31) + this.general_profile_idc) * 31) + ((int) (this.general_profile_compatibility_flags ^ (this.general_profile_compatibility_flags >>> 32)))) * 31) + ((int) (this.general_constraint_indicator_flags ^ (this.general_constraint_indicator_flags >>> 32)))) * 31) + this.general_level_idc) * 31) + this.reserved1) * 31) + this.min_spatial_segmentation_idc) * 31) + this.reserved2) * 31) + this.parallelismType) * 31) + this.reserved3) * 31) + this.chromaFormat) * 31) + this.reserved4) * 31) + this.bitDepthLumaMinus8) * 31) + this.reserved5) * 31) + this.bitDepthChromaMinus8) * 31) + this.avgFrameRate) * 31) + this.constantFrameRate) * 31) + this.numTemporalLayers) * 31;
        if (!this.temporalIdNested) {
            i = 0;
        }
        i3 = (((i3 + i) * 31) + this.lengthSizeMinusOne) * 31;
        if (this.arrays != null) {
            i2 = this.arrays.hashCode();
        }
        return i3 + i2;
    }

    public boolean isFrame_only_constraint_flag() {
        return this.frame_only_constraint_flag;
    }

    public boolean isGeneral_tier_flag() {
        return this.general_tier_flag;
    }

    public boolean isInterlaced_source_flag() {
        return this.interlaced_source_flag;
    }

    public boolean isNon_packed_constraint_flag() {
        return this.non_packed_constraint_flag;
    }

    public boolean isProgressive_source_flag() {
        return this.progressive_source_flag;
    }

    public boolean isTemporalIdNested() {
        return this.temporalIdNested;
    }

    public void parse(ByteBuffer byteBuffer) {
        this.configurationVersion = IsoTypeReader.readUInt8(byteBuffer);
        int readUInt8 = IsoTypeReader.readUInt8(byteBuffer);
        this.general_profile_space = (readUInt8 & PsExtractor.AUDIO_STREAM) >> 6;
        this.general_tier_flag = (readUInt8 & 32) > 0;
        this.general_profile_idc = readUInt8 & 31;
        this.general_profile_compatibility_flags = IsoTypeReader.readUInt32(byteBuffer);
        this.general_constraint_indicator_flags = IsoTypeReader.readUInt48(byteBuffer);
        this.frame_only_constraint_flag = ((this.general_constraint_indicator_flags >> 44) & 8) > 0;
        this.non_packed_constraint_flag = ((this.general_constraint_indicator_flags >> 44) & 4) > 0;
        this.interlaced_source_flag = ((this.general_constraint_indicator_flags >> 44) & 2) > 0;
        this.progressive_source_flag = ((this.general_constraint_indicator_flags >> 44) & 1) > 0;
        this.general_constraint_indicator_flags &= 140737488355327L;
        this.general_level_idc = IsoTypeReader.readUInt8(byteBuffer);
        int readUInt16 = IsoTypeReader.readUInt16(byteBuffer);
        this.reserved1 = (61440 & readUInt16) >> 12;
        this.min_spatial_segmentation_idc = readUInt16 & 4095;
        readUInt16 = IsoTypeReader.readUInt8(byteBuffer);
        this.reserved2 = (readUInt16 & 252) >> 2;
        this.parallelismType = readUInt16 & 3;
        readUInt16 = IsoTypeReader.readUInt8(byteBuffer);
        this.reserved3 = (readUInt16 & 252) >> 2;
        this.chromaFormat = readUInt16 & 3;
        readUInt16 = IsoTypeReader.readUInt8(byteBuffer);
        this.reserved4 = (readUInt16 & 248) >> 3;
        this.bitDepthLumaMinus8 = readUInt16 & 7;
        readUInt16 = IsoTypeReader.readUInt8(byteBuffer);
        this.reserved5 = (readUInt16 & 248) >> 3;
        this.bitDepthChromaMinus8 = readUInt16 & 7;
        this.avgFrameRate = IsoTypeReader.readUInt16(byteBuffer);
        readUInt8 = IsoTypeReader.readUInt8(byteBuffer);
        this.constantFrameRate = (readUInt8 & PsExtractor.AUDIO_STREAM) >> 6;
        this.numTemporalLayers = (readUInt8 & 56) >> 3;
        this.temporalIdNested = (readUInt8 & 4) > 0;
        this.lengthSizeMinusOne = readUInt8 & 3;
        int readUInt82 = IsoTypeReader.readUInt8(byteBuffer);
        this.arrays = new ArrayList();
        for (readUInt8 = 0; readUInt8 < readUInt82; readUInt8++) {
            Array array = new Array();
            int readUInt83 = IsoTypeReader.readUInt8(byteBuffer);
            array.array_completeness = (readUInt83 & TLRPC.USER_FLAG_UNUSED) > 0;
            array.reserved = (readUInt83 & 64) > 0;
            array.nal_unit_type = readUInt83 & 63;
            readUInt83 = IsoTypeReader.readUInt16(byteBuffer);
            array.nalUnits = new ArrayList();
            for (readUInt16 = 0; readUInt16 < readUInt83; readUInt16++) {
                Object obj = new byte[IsoTypeReader.readUInt16(byteBuffer)];
                byteBuffer.get(obj);
                array.nalUnits.add(obj);
            }
            this.arrays.add(array);
        }
    }

    public void setArrays(List<Array> list) {
        this.arrays = list;
    }

    public void setAvgFrameRate(int i) {
        this.avgFrameRate = i;
    }

    public void setBitDepthChromaMinus8(int i) {
        this.bitDepthChromaMinus8 = i;
    }

    public void setBitDepthLumaMinus8(int i) {
        this.bitDepthLumaMinus8 = i;
    }

    public void setChromaFormat(int i) {
        this.chromaFormat = i;
    }

    public void setConfigurationVersion(int i) {
        this.configurationVersion = i;
    }

    public void setConstantFrameRate(int i) {
        this.constantFrameRate = i;
    }

    public void setFrame_only_constraint_flag(boolean z) {
        this.frame_only_constraint_flag = z;
    }

    public void setGeneral_constraint_indicator_flags(long j) {
        this.general_constraint_indicator_flags = j;
    }

    public void setGeneral_level_idc(int i) {
        this.general_level_idc = i;
    }

    public void setGeneral_profile_compatibility_flags(long j) {
        this.general_profile_compatibility_flags = j;
    }

    public void setGeneral_profile_idc(int i) {
        this.general_profile_idc = i;
    }

    public void setGeneral_profile_space(int i) {
        this.general_profile_space = i;
    }

    public void setGeneral_tier_flag(boolean z) {
        this.general_tier_flag = z;
    }

    public void setInterlaced_source_flag(boolean z) {
        this.interlaced_source_flag = z;
    }

    public void setLengthSizeMinusOne(int i) {
        this.lengthSizeMinusOne = i;
    }

    public void setMin_spatial_segmentation_idc(int i) {
        this.min_spatial_segmentation_idc = i;
    }

    public void setNon_packed_constraint_flag(boolean z) {
        this.non_packed_constraint_flag = z;
    }

    public void setNumTemporalLayers(int i) {
        this.numTemporalLayers = i;
    }

    public void setParallelismType(int i) {
        this.parallelismType = i;
    }

    public void setProgressive_source_flag(boolean z) {
        this.progressive_source_flag = z;
    }

    public void setTemporalIdNested(boolean z) {
        this.temporalIdNested = z;
    }

    public String toString() {
        return "HEVCDecoderConfigurationRecord{configurationVersion=" + this.configurationVersion + ", general_profile_space=" + this.general_profile_space + ", general_tier_flag=" + this.general_tier_flag + ", general_profile_idc=" + this.general_profile_idc + ", general_profile_compatibility_flags=" + this.general_profile_compatibility_flags + ", general_constraint_indicator_flags=" + this.general_constraint_indicator_flags + ", general_level_idc=" + this.general_level_idc + (this.reserved1 != 15 ? ", reserved1=" + this.reserved1 : TtmlNode.ANONYMOUS_REGION_ID) + ", min_spatial_segmentation_idc=" + this.min_spatial_segmentation_idc + (this.reserved2 != 63 ? ", reserved2=" + this.reserved2 : TtmlNode.ANONYMOUS_REGION_ID) + ", parallelismType=" + this.parallelismType + (this.reserved3 != 63 ? ", reserved3=" + this.reserved3 : TtmlNode.ANONYMOUS_REGION_ID) + ", chromaFormat=" + this.chromaFormat + (this.reserved4 != 31 ? ", reserved4=" + this.reserved4 : TtmlNode.ANONYMOUS_REGION_ID) + ", bitDepthLumaMinus8=" + this.bitDepthLumaMinus8 + (this.reserved5 != 31 ? ", reserved5=" + this.reserved5 : TtmlNode.ANONYMOUS_REGION_ID) + ", bitDepthChromaMinus8=" + this.bitDepthChromaMinus8 + ", avgFrameRate=" + this.avgFrameRate + ", constantFrameRate=" + this.constantFrameRate + ", numTemporalLayers=" + this.numTemporalLayers + ", temporalIdNested=" + this.temporalIdNested + ", lengthSizeMinusOne=" + this.lengthSizeMinusOne + ", arrays=" + this.arrays + '}';
    }

    public void write(ByteBuffer byteBuffer) {
        IsoTypeWriter.writeUInt8(byteBuffer, this.configurationVersion);
        IsoTypeWriter.writeUInt8(byteBuffer, ((this.general_tier_flag ? 32 : 0) + (this.general_profile_space << 6)) + this.general_profile_idc);
        IsoTypeWriter.writeUInt32(byteBuffer, this.general_profile_compatibility_flags);
        long j = this.general_constraint_indicator_flags;
        if (this.frame_only_constraint_flag) {
            j |= 140737488355328L;
        }
        if (this.non_packed_constraint_flag) {
            j |= 70368744177664L;
        }
        if (this.interlaced_source_flag) {
            j |= 35184372088832L;
        }
        if (this.progressive_source_flag) {
            j |= 17592186044416L;
        }
        IsoTypeWriter.writeUInt48(byteBuffer, j);
        IsoTypeWriter.writeUInt8(byteBuffer, this.general_level_idc);
        IsoTypeWriter.writeUInt16(byteBuffer, (this.reserved1 << 12) + this.min_spatial_segmentation_idc);
        IsoTypeWriter.writeUInt8(byteBuffer, (this.reserved2 << 2) + this.parallelismType);
        IsoTypeWriter.writeUInt8(byteBuffer, (this.reserved3 << 2) + this.chromaFormat);
        IsoTypeWriter.writeUInt8(byteBuffer, (this.reserved4 << 3) + this.bitDepthLumaMinus8);
        IsoTypeWriter.writeUInt8(byteBuffer, (this.reserved5 << 3) + this.bitDepthChromaMinus8);
        IsoTypeWriter.writeUInt16(byteBuffer, this.avgFrameRate);
        IsoTypeWriter.writeUInt8(byteBuffer, ((this.temporalIdNested ? 4 : 0) + ((this.numTemporalLayers << 3) + (this.constantFrameRate << 6))) + this.lengthSizeMinusOne);
        IsoTypeWriter.writeUInt8(byteBuffer, this.arrays.size());
        for (Array array : this.arrays) {
            IsoTypeWriter.writeUInt8(byteBuffer, ((array.array_completeness ? TLRPC.USER_FLAG_UNUSED : 0) + (array.reserved ? 64 : 0)) + array.nal_unit_type);
            IsoTypeWriter.writeUInt16(byteBuffer, array.nalUnits.size());
            for (byte[] bArr : array.nalUnits) {
                IsoTypeWriter.writeUInt16(byteBuffer, bArr.length);
                byteBuffer.put(bArr);
            }
        }
    }
}
