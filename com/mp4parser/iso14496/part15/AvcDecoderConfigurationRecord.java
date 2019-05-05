package com.mp4parser.iso14496.part15;

import com.coremedia.iso.Hex;
import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.googlecode.mp4parser.authoring.tracks.CleanInputStream;
import com.googlecode.mp4parser.boxes.mp4.objectdescriptors.BitReaderBuffer;
import com.googlecode.mp4parser.boxes.mp4.objectdescriptors.BitWriterBuffer;
import com.googlecode.mp4parser.h264.model.PictureParameterSet;
import com.googlecode.mp4parser.h264.model.SeqParameterSet;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class AvcDecoderConfigurationRecord {
    public int avcLevelIndication;
    public int avcProfileIndication;
    public int bitDepthChromaMinus8;
    public int bitDepthChromaMinus8PaddingBits;
    public int bitDepthLumaMinus8;
    public int bitDepthLumaMinus8PaddingBits;
    public int chromaFormat;
    public int chromaFormatPaddingBits;
    public int configurationVersion;
    public boolean hasExts;
    public int lengthSizeMinusOne;
    public int lengthSizeMinusOnePaddingBits;
    public int numberOfSequenceParameterSetsPaddingBits;
    public List<byte[]> pictureParameterSets;
    public int profileCompatibility;
    public List<byte[]> sequenceParameterSetExts;
    public List<byte[]> sequenceParameterSets;

    public AvcDecoderConfigurationRecord() {
        this.sequenceParameterSets = new ArrayList();
        this.pictureParameterSets = new ArrayList();
        this.hasExts = true;
        this.chromaFormat = 1;
        this.bitDepthLumaMinus8 = 0;
        this.bitDepthChromaMinus8 = 0;
        this.sequenceParameterSetExts = new ArrayList();
        this.lengthSizeMinusOnePaddingBits = 63;
        this.numberOfSequenceParameterSetsPaddingBits = 7;
        this.chromaFormatPaddingBits = 31;
        this.bitDepthLumaMinus8PaddingBits = 31;
        this.bitDepthChromaMinus8PaddingBits = 31;
    }

    public AvcDecoderConfigurationRecord(ByteBuffer byteBuffer) {
        int i;
        int i2 = 0;
        this.sequenceParameterSets = new ArrayList();
        this.pictureParameterSets = new ArrayList();
        this.hasExts = true;
        this.chromaFormat = 1;
        this.bitDepthLumaMinus8 = 0;
        this.bitDepthChromaMinus8 = 0;
        this.sequenceParameterSetExts = new ArrayList();
        this.lengthSizeMinusOnePaddingBits = 63;
        this.numberOfSequenceParameterSetsPaddingBits = 7;
        this.chromaFormatPaddingBits = 31;
        this.bitDepthLumaMinus8PaddingBits = 31;
        this.bitDepthChromaMinus8PaddingBits = 31;
        this.configurationVersion = IsoTypeReader.readUInt8(byteBuffer);
        this.avcProfileIndication = IsoTypeReader.readUInt8(byteBuffer);
        this.profileCompatibility = IsoTypeReader.readUInt8(byteBuffer);
        this.avcLevelIndication = IsoTypeReader.readUInt8(byteBuffer);
        BitReaderBuffer bitReaderBuffer = new BitReaderBuffer(byteBuffer);
        this.lengthSizeMinusOnePaddingBits = bitReaderBuffer.readBits(6);
        this.lengthSizeMinusOne = bitReaderBuffer.readBits(2);
        this.numberOfSequenceParameterSetsPaddingBits = bitReaderBuffer.readBits(3);
        int readBits = bitReaderBuffer.readBits(5);
        for (i = 0; i < readBits; i++) {
            Object obj = new byte[IsoTypeReader.readUInt16(byteBuffer)];
            byteBuffer.get(obj);
            this.sequenceParameterSets.add(obj);
        }
        long readUInt8 = (long) IsoTypeReader.readUInt8(byteBuffer);
        for (i = 0; ((long) i) < readUInt8; i++) {
            Object obj2 = new byte[IsoTypeReader.readUInt16(byteBuffer)];
            byteBuffer.get(obj2);
            this.pictureParameterSets.add(obj2);
        }
        if (byteBuffer.remaining() < 4) {
            this.hasExts = false;
        }
        if (this.hasExts && (this.avcProfileIndication == 100 || this.avcProfileIndication == 110 || this.avcProfileIndication == 122 || this.avcProfileIndication == 144)) {
            bitReaderBuffer = new BitReaderBuffer(byteBuffer);
            this.chromaFormatPaddingBits = bitReaderBuffer.readBits(6);
            this.chromaFormat = bitReaderBuffer.readBits(2);
            this.bitDepthLumaMinus8PaddingBits = bitReaderBuffer.readBits(5);
            this.bitDepthLumaMinus8 = bitReaderBuffer.readBits(3);
            this.bitDepthChromaMinus8PaddingBits = bitReaderBuffer.readBits(5);
            this.bitDepthChromaMinus8 = bitReaderBuffer.readBits(3);
            readUInt8 = (long) IsoTypeReader.readUInt8(byteBuffer);
            while (((long) i2) < readUInt8) {
                Object obj3 = new byte[IsoTypeReader.readUInt16(byteBuffer)];
                byteBuffer.get(obj3);
                this.sequenceParameterSetExts.add(obj3);
                i2++;
            }
            return;
        }
        this.chromaFormat = -1;
        this.bitDepthLumaMinus8 = -1;
        this.bitDepthChromaMinus8 = -1;
    }

    public void getContent(ByteBuffer byteBuffer) {
        IsoTypeWriter.writeUInt8(byteBuffer, this.configurationVersion);
        IsoTypeWriter.writeUInt8(byteBuffer, this.avcProfileIndication);
        IsoTypeWriter.writeUInt8(byteBuffer, this.profileCompatibility);
        IsoTypeWriter.writeUInt8(byteBuffer, this.avcLevelIndication);
        BitWriterBuffer bitWriterBuffer = new BitWriterBuffer(byteBuffer);
        bitWriterBuffer.writeBits(this.lengthSizeMinusOnePaddingBits, 6);
        bitWriterBuffer.writeBits(this.lengthSizeMinusOne, 2);
        bitWriterBuffer.writeBits(this.numberOfSequenceParameterSetsPaddingBits, 3);
        bitWriterBuffer.writeBits(this.pictureParameterSets.size(), 5);
        for (byte[] bArr : this.sequenceParameterSets) {
            IsoTypeWriter.writeUInt16(byteBuffer, bArr.length);
            byteBuffer.put(bArr);
        }
        IsoTypeWriter.writeUInt8(byteBuffer, this.pictureParameterSets.size());
        for (byte[] bArr2 : this.pictureParameterSets) {
            IsoTypeWriter.writeUInt16(byteBuffer, bArr2.length);
            byteBuffer.put(bArr2);
        }
        if (!this.hasExts) {
            return;
        }
        if (this.avcProfileIndication == 100 || this.avcProfileIndication == 110 || this.avcProfileIndication == 122 || this.avcProfileIndication == 144) {
            bitWriterBuffer = new BitWriterBuffer(byteBuffer);
            bitWriterBuffer.writeBits(this.chromaFormatPaddingBits, 6);
            bitWriterBuffer.writeBits(this.chromaFormat, 2);
            bitWriterBuffer.writeBits(this.bitDepthLumaMinus8PaddingBits, 5);
            bitWriterBuffer.writeBits(this.bitDepthLumaMinus8, 3);
            bitWriterBuffer.writeBits(this.bitDepthChromaMinus8PaddingBits, 5);
            bitWriterBuffer.writeBits(this.bitDepthChromaMinus8, 3);
            for (byte[] bArr22 : this.sequenceParameterSetExts) {
                IsoTypeWriter.writeUInt16(byteBuffer, bArr22.length);
                byteBuffer.put(bArr22);
            }
        }
    }

    public long getContentSize() {
        long j = 5 + 1;
        long j2 = j;
        for (byte[] length : this.sequenceParameterSets) {
            j2 = ((long) length.length) + (j2 + 2);
        }
        j = j2 + 1;
        j2 = j;
        for (byte[] length2 : this.pictureParameterSets) {
            j2 = ((long) length2.length) + (j2 + 2);
        }
        if (this.hasExts && (this.avcProfileIndication == 100 || this.avcProfileIndication == 110 || this.avcProfileIndication == 122 || this.avcProfileIndication == 144)) {
            j = 4 + j2;
            j2 = j;
            for (byte[] length22 : this.sequenceParameterSetExts) {
                j2 = ((long) length22.length) + (j2 + 2);
            }
        }
        return j2;
    }

    public String[] getPPS() {
        ArrayList arrayList = new ArrayList();
        for (byte[] bArr : this.pictureParameterSets) {
            String str = "not parsable";
            try {
                arrayList.add(PictureParameterSet.read(new ByteArrayInputStream(bArr, 1, bArr.length - 1)).toString());
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }
        return (String[]) arrayList.toArray(new String[arrayList.size()]);
    }

    public List<String> getPictureParameterSetsAsStrings() {
        List<String> arrayList = new ArrayList(this.pictureParameterSets.size());
        for (byte[] encodeHex : this.pictureParameterSets) {
            arrayList.add(Hex.encodeHex(encodeHex));
        }
        return arrayList;
    }

    public String[] getSPS() {
        ArrayList arrayList = new ArrayList();
        for (byte[] bArr : this.sequenceParameterSets) {
            Object seqParameterSet;
            String str = "not parsable";
            try {
                seqParameterSet = SeqParameterSet.read(new CleanInputStream(new ByteArrayInputStream(bArr, 1, bArr.length - 1))).toString();
            } catch (IOException e) {
                String str2 = str;
            }
            arrayList.add(seqParameterSet);
        }
        return (String[]) arrayList.toArray(new String[arrayList.size()]);
    }

    public List<String> getSequenceParameterSetExtsAsStrings() {
        List<String> arrayList = new ArrayList(this.sequenceParameterSetExts.size());
        for (byte[] encodeHex : this.sequenceParameterSetExts) {
            arrayList.add(Hex.encodeHex(encodeHex));
        }
        return arrayList;
    }

    public List<String> getSequenceParameterSetsAsStrings() {
        List<String> arrayList = new ArrayList(this.sequenceParameterSets.size());
        for (byte[] encodeHex : this.sequenceParameterSets) {
            arrayList.add(Hex.encodeHex(encodeHex));
        }
        return arrayList;
    }

    public String toString() {
        return "AvcDecoderConfigurationRecord{configurationVersion=" + this.configurationVersion + ", avcProfileIndication=" + this.avcProfileIndication + ", profileCompatibility=" + this.profileCompatibility + ", avcLevelIndication=" + this.avcLevelIndication + ", lengthSizeMinusOne=" + this.lengthSizeMinusOne + ", hasExts=" + this.hasExts + ", chromaFormat=" + this.chromaFormat + ", bitDepthLumaMinus8=" + this.bitDepthLumaMinus8 + ", bitDepthChromaMinus8=" + this.bitDepthChromaMinus8 + ", lengthSizeMinusOnePaddingBits=" + this.lengthSizeMinusOnePaddingBits + ", numberOfSequenceParameterSetsPaddingBits=" + this.numberOfSequenceParameterSetsPaddingBits + ", chromaFormatPaddingBits=" + this.chromaFormatPaddingBits + ", bitDepthLumaMinus8PaddingBits=" + this.bitDepthLumaMinus8PaddingBits + ", bitDepthChromaMinus8PaddingBits=" + this.bitDepthChromaMinus8PaddingBits + '}';
    }
}
