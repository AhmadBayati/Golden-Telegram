package com.coremedia.iso.boxes.fragment;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import java.nio.ByteBuffer;

public class SampleFlags {
    private byte is_leading;
    private byte reserved;
    private int sampleDegradationPriority;
    private byte sampleDependsOn;
    private byte sampleHasRedundancy;
    private byte sampleIsDependedOn;
    private boolean sampleIsDifferenceSample;
    private byte samplePaddingValue;

    public SampleFlags(ByteBuffer byteBuffer) {
        long readUInt32 = IsoTypeReader.readUInt32(byteBuffer);
        this.reserved = (byte) ((int) ((-268435456 & readUInt32) >> 28));
        this.is_leading = (byte) ((int) ((201326592 & readUInt32) >> 26));
        this.sampleDependsOn = (byte) ((int) ((50331648 & readUInt32) >> 24));
        this.sampleIsDependedOn = (byte) ((int) ((12582912 & readUInt32) >> 22));
        this.sampleHasRedundancy = (byte) ((int) ((3145728 & readUInt32) >> 20));
        this.samplePaddingValue = (byte) ((int) ((917504 & readUInt32) >> 17));
        this.sampleIsDifferenceSample = ((65536 & readUInt32) >> 16) > 0;
        this.sampleDegradationPriority = (int) (65535 & readUInt32);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        SampleFlags sampleFlags = (SampleFlags) obj;
        return this.is_leading != sampleFlags.is_leading ? false : this.reserved != sampleFlags.reserved ? false : this.sampleDegradationPriority != sampleFlags.sampleDegradationPriority ? false : this.sampleDependsOn != sampleFlags.sampleDependsOn ? false : this.sampleHasRedundancy != sampleFlags.sampleHasRedundancy ? false : this.sampleIsDependedOn != sampleFlags.sampleIsDependedOn ? false : this.sampleIsDifferenceSample != sampleFlags.sampleIsDifferenceSample ? false : this.samplePaddingValue == sampleFlags.samplePaddingValue;
    }

    public void getContent(ByteBuffer byteBuffer) {
        IsoTypeWriter.writeUInt32(byteBuffer, (((long) ((this.sampleIsDifferenceSample ? 1 : 0) << 16)) | (((long) (this.samplePaddingValue << 17)) | (((((0 | ((long) (this.reserved << 28))) | ((long) (this.is_leading << 26))) | ((long) (this.sampleDependsOn << 24))) | ((long) (this.sampleIsDependedOn << 22))) | ((long) (this.sampleHasRedundancy << 20))))) | ((long) this.sampleDegradationPriority));
    }

    public int getReserved() {
        return this.reserved;
    }

    public int getSampleDegradationPriority() {
        return this.sampleDegradationPriority;
    }

    public int getSampleDependsOn() {
        return this.sampleDependsOn;
    }

    public int getSampleHasRedundancy() {
        return this.sampleHasRedundancy;
    }

    public int getSampleIsDependedOn() {
        return this.sampleIsDependedOn;
    }

    public int getSamplePaddingValue() {
        return this.samplePaddingValue;
    }

    public int hashCode() {
        return (((this.sampleIsDifferenceSample ? 1 : 0) + (((((((((((this.reserved * 31) + this.is_leading) * 31) + this.sampleDependsOn) * 31) + this.sampleIsDependedOn) * 31) + this.sampleHasRedundancy) * 31) + this.samplePaddingValue) * 31)) * 31) + this.sampleDegradationPriority;
    }

    public boolean isSampleIsDifferenceSample() {
        return this.sampleIsDifferenceSample;
    }

    public void setReserved(int i) {
        this.reserved = (byte) i;
    }

    public void setSampleDegradationPriority(int i) {
        this.sampleDegradationPriority = i;
    }

    public void setSampleDependsOn(int i) {
        this.sampleDependsOn = (byte) i;
    }

    public void setSampleHasRedundancy(int i) {
        this.sampleHasRedundancy = (byte) i;
    }

    public void setSampleIsDependedOn(int i) {
        this.sampleIsDependedOn = (byte) i;
    }

    public void setSampleIsDifferenceSample(boolean z) {
        this.sampleIsDifferenceSample = z;
    }

    public void setSamplePaddingValue(int i) {
        this.samplePaddingValue = (byte) i;
    }

    public String toString() {
        return "SampleFlags{reserved=" + this.reserved + ", isLeading=" + this.is_leading + ", depOn=" + this.sampleDependsOn + ", isDepOn=" + this.sampleIsDependedOn + ", hasRedundancy=" + this.sampleHasRedundancy + ", padValue=" + this.samplePaddingValue + ", isDiffSample=" + this.sampleIsDifferenceSample + ", degradPrio=" + this.sampleDegradationPriority + '}';
    }
}
