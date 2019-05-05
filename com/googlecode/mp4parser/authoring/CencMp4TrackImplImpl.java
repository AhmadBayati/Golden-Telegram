package com.googlecode.mp4parser.authoring;

import com.coremedia.iso.IsoFile;
import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.boxes.Box;
import com.coremedia.iso.boxes.ChunkOffsetBox;
import com.coremedia.iso.boxes.Container;
import com.coremedia.iso.boxes.MovieBox;
import com.coremedia.iso.boxes.SchemeTypeBox;
import com.coremedia.iso.boxes.TrackBox;
import com.coremedia.iso.boxes.fragment.MovieExtendsBox;
import com.coremedia.iso.boxes.fragment.MovieFragmentBox;
import com.coremedia.iso.boxes.fragment.TrackFragmentBox;
import com.coremedia.iso.boxes.fragment.TrackRunBox;
import com.googlecode.mp4parser.AbstractContainerBox;
import com.googlecode.mp4parser.authoring.tracks.CencEncryptedTrack;
import com.googlecode.mp4parser.util.Path;
import com.mp4parser.iso14496.part12.SampleAuxiliaryInformationOffsetsBox;
import com.mp4parser.iso14496.part12.SampleAuxiliaryInformationSizesBox;
import com.mp4parser.iso23001.part7.CencSampleAuxiliaryDataFormat;
import com.mp4parser.iso23001.part7.CencSampleAuxiliaryDataFormat.Pair;
import com.mp4parser.iso23001.part7.TrackEncryptionBox;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CencMp4TrackImplImpl extends Mp4TrackImpl implements CencEncryptedTrack {
    static final /* synthetic */ boolean $assertionsDisabled;
    private UUID defaultKeyId;
    private List<CencSampleAuxiliaryDataFormat> sampleEncryptionEntries;

    private class FindSaioSaizPair {
        static final /* synthetic */ boolean $assertionsDisabled;
        private Container container;
        private SampleAuxiliaryInformationOffsetsBox saio;
        private SampleAuxiliaryInformationSizesBox saiz;

        static {
            $assertionsDisabled = !CencMp4TrackImplImpl.class.desiredAssertionStatus();
        }

        public FindSaioSaizPair(Container container) {
            this.container = container;
        }

        public SampleAuxiliaryInformationOffsetsBox getSaio() {
            return this.saio;
        }

        public SampleAuxiliaryInformationSizesBox getSaiz() {
            return this.saiz;
        }

        public FindSaioSaizPair invoke() {
            List boxes = this.container.getBoxes(SampleAuxiliaryInformationSizesBox.class);
            List boxes2 = this.container.getBoxes(SampleAuxiliaryInformationOffsetsBox.class);
            if ($assertionsDisabled || boxes.size() == boxes2.size()) {
                this.saiz = null;
                this.saio = null;
                int i = 0;
                while (i < boxes.size()) {
                    if ((this.saiz == null && ((SampleAuxiliaryInformationSizesBox) boxes.get(i)).getAuxInfoType() == null) || "cenc".equals(((SampleAuxiliaryInformationSizesBox) boxes.get(i)).getAuxInfoType())) {
                        this.saiz = (SampleAuxiliaryInformationSizesBox) boxes.get(i);
                    } else if (this.saiz != null && this.saiz.getAuxInfoType() == null && "cenc".equals(((SampleAuxiliaryInformationSizesBox) boxes.get(i)).getAuxInfoType())) {
                        this.saiz = (SampleAuxiliaryInformationSizesBox) boxes.get(i);
                    } else {
                        throw new RuntimeException("Are there two cenc labeled saiz?");
                    }
                    if ((this.saio == null && ((SampleAuxiliaryInformationOffsetsBox) boxes2.get(i)).getAuxInfoType() == null) || "cenc".equals(((SampleAuxiliaryInformationOffsetsBox) boxes2.get(i)).getAuxInfoType())) {
                        this.saio = (SampleAuxiliaryInformationOffsetsBox) boxes2.get(i);
                    } else if (this.saio != null && this.saio.getAuxInfoType() == null && "cenc".equals(((SampleAuxiliaryInformationOffsetsBox) boxes2.get(i)).getAuxInfoType())) {
                        this.saio = (SampleAuxiliaryInformationOffsetsBox) boxes2.get(i);
                    } else {
                        throw new RuntimeException("Are there two cenc labeled saio?");
                    }
                    i++;
                }
                return this;
            }
            throw new AssertionError();
        }
    }

    static {
        $assertionsDisabled = !CencMp4TrackImplImpl.class.desiredAssertionStatus();
    }

    public CencMp4TrackImplImpl(String str, TrackBox trackBox, IsoFile... isoFileArr) {
        super(str, trackBox, isoFileArr);
        SchemeTypeBox schemeTypeBox = (SchemeTypeBox) Path.getPath((AbstractContainerBox) trackBox, "mdia[0]/minf[0]/stbl[0]/stsd[0]/enc.[0]/sinf[0]/schm[0]");
        if ($assertionsDisabled || (schemeTypeBox != null && (schemeTypeBox.getSchemeType().equals("cenc") || schemeTypeBox.getSchemeType().equals("cbc1")))) {
            this.sampleEncryptionEntries = new ArrayList();
            long trackId = trackBox.getTrackHeaderBox().getTrackId();
            long baseDataOffset;
            SampleAuxiliaryInformationOffsetsBox saio;
            int i;
            if (trackBox.getParent().getBoxes(MovieExtendsBox.class).size() > 0) {
                for (MovieFragmentBox movieFragmentBox : ((Box) trackBox.getParent()).getParent().getBoxes(MovieFragmentBox.class)) {
                    for (TrackFragmentBox trackFragmentBox : movieFragmentBox.getBoxes(TrackFragmentBox.class)) {
                        if (trackFragmentBox.getTrackFragmentHeaderBox().getTrackId() == trackId) {
                            Container parent;
                            TrackEncryptionBox trackEncryptionBox = (TrackEncryptionBox) Path.getPath((AbstractContainerBox) trackBox, "mdia[0]/minf[0]/stbl[0]/stsd[0]/enc.[0]/sinf[0]/schi[0]/tenc[0]");
                            this.defaultKeyId = trackEncryptionBox.getDefault_KID();
                            if (trackFragmentBox.getTrackFragmentHeaderBox().hasBaseDataOffset()) {
                                parent = ((Box) trackBox.getParent()).getParent();
                                baseDataOffset = trackFragmentBox.getTrackFragmentHeaderBox().getBaseDataOffset();
                            } else {
                                baseDataOffset = 0;
                                Object obj = movieFragmentBox;
                            }
                            FindSaioSaizPair invoke = new FindSaioSaizPair(trackFragmentBox).invoke();
                            saio = invoke.getSaio();
                            SampleAuxiliaryInformationSizesBox saiz = invoke.getSaiz();
                            if ($assertionsDisabled || saio != null) {
                                long[] offsets = saio.getOffsets();
                                if (!$assertionsDisabled && offsets.length != trackFragmentBox.getBoxes(TrackRunBox.class).size()) {
                                    throw new AssertionError();
                                } else if ($assertionsDisabled || saiz != null) {
                                    List boxes = trackFragmentBox.getBoxes(TrackRunBox.class);
                                    int i2 = 0;
                                    for (int i3 = 0; i3 < offsets.length; i3++) {
                                        int size = ((TrackRunBox) boxes.get(i3)).getEntries().size();
                                        long j = offsets[i3];
                                        long j2 = 0;
                                        for (i = i2; i < i2 + size; i++) {
                                            j2 += (long) saiz.getSize(i);
                                        }
                                        ByteBuffer byteBuffer = parent.getByteBuffer(j + baseDataOffset, j2);
                                        for (i = i2; i < i2 + size; i++) {
                                            short size2 = saiz.getSize(i);
                                            long j3 = (long) size2;
                                            this.sampleEncryptionEntries.add(parseCencAuxDataFormat(trackEncryptionBox.getDefaultIvSize(), byteBuffer, j3));
                                        }
                                        i2 += size;
                                    }
                                } else {
                                    throw new AssertionError();
                                }
                            }
                            throw new AssertionError();
                        }
                    }
                }
                return;
            }
            TrackEncryptionBox trackEncryptionBox2 = (TrackEncryptionBox) Path.getPath((AbstractContainerBox) trackBox, "mdia[0]/minf[0]/stbl[0]/stsd[0]/enc.[0]/sinf[0]/schi[0]/tenc[0]");
            this.defaultKeyId = trackEncryptionBox2.getDefault_KID();
            ChunkOffsetBox chunkOffsetBox = (ChunkOffsetBox) Path.getPath((AbstractContainerBox) trackBox, "mdia[0]/minf[0]/stbl[0]/stco[0]");
            if (chunkOffsetBox == null) {
                chunkOffsetBox = (ChunkOffsetBox) Path.getPath((AbstractContainerBox) trackBox, "mdia[0]/minf[0]/stbl[0]/co64[0]");
            }
            long[] blowup = trackBox.getSampleTableBox().getSampleToChunkBox().blowup(chunkOffsetBox.getChunkOffsets().length);
            FindSaioSaizPair invoke2 = new FindSaioSaizPair((Container) Path.getPath((AbstractContainerBox) trackBox, "mdia[0]/minf[0]/stbl[0]")).invoke();
            saio = invoke2.saio;
            SampleAuxiliaryInformationSizesBox access$1 = invoke2.saiz;
            Container parent2 = ((MovieBox) trackBox.getParent()).getParent();
            int i4;
            if (saio.getOffsets().length == 1) {
                baseDataOffset = saio.getOffsets()[0];
                i4 = 0;
                if (access$1.getDefaultSampleInfoSize() > 0) {
                    i4 = 0 + (access$1.getSampleCount() * access$1.getDefaultSampleInfoSize());
                } else {
                    for (i = 0; i < access$1.getSampleCount(); i++) {
                        i4 += access$1.getSampleInfoSizes()[i];
                    }
                }
                ByteBuffer byteBuffer2 = parent2.getByteBuffer(baseDataOffset, (long) i4);
                for (i = 0; i < access$1.getSampleCount(); i++) {
                    this.sampleEncryptionEntries.add(parseCencAuxDataFormat(trackEncryptionBox2.getDefaultIvSize(), byteBuffer2, (long) access$1.getSize(i)));
                }
                return;
            } else if (saio.getOffsets().length == blowup.length) {
                i4 = 0;
                for (i = 0; i < blowup.length; i++) {
                    int i5;
                    trackId = saio.getOffsets()[i];
                    baseDataOffset = 0;
                    if (access$1.getDefaultSampleInfoSize() <= 0) {
                        i5 = 0;
                        while (true) {
                            if (((long) i5) >= blowup[i]) {
                                break;
                            }
                            baseDataOffset += (long) access$1.getSize(i4 + i5);
                            i5++;
                        }
                    } else {
                        baseDataOffset = 0 + (((long) access$1.getSampleCount()) * blowup[i]);
                    }
                    ByteBuffer byteBuffer3 = parent2.getByteBuffer(trackId, baseDataOffset);
                    for (i5 = 0; ((long) i5) < blowup[i]; i5++) {
                        trackId = (long) access$1.getSize(i4 + i5);
                        this.sampleEncryptionEntries.add(parseCencAuxDataFormat(trackEncryptionBox2.getDefaultIvSize(), byteBuffer3, trackId));
                    }
                    i4 = (int) (((long) i4) + blowup[i]);
                }
                return;
            } else {
                throw new RuntimeException("Number of saio offsets must be either 1 or number of chunks");
            }
        }
        throw new AssertionError("Track must be CENC (cenc or cbc1) encrypted");
    }

    private CencSampleAuxiliaryDataFormat parseCencAuxDataFormat(int i, ByteBuffer byteBuffer, long j) {
        CencSampleAuxiliaryDataFormat cencSampleAuxiliaryDataFormat = new CencSampleAuxiliaryDataFormat();
        if (j > 0) {
            cencSampleAuxiliaryDataFormat.iv = new byte[i];
            byteBuffer.get(cencSampleAuxiliaryDataFormat.iv);
            if (j > ((long) i)) {
                cencSampleAuxiliaryDataFormat.pairs = new Pair[IsoTypeReader.readUInt16(byteBuffer)];
                for (int i2 = 0; i2 < cencSampleAuxiliaryDataFormat.pairs.length; i2++) {
                    cencSampleAuxiliaryDataFormat.pairs[i2] = cencSampleAuxiliaryDataFormat.createPair(IsoTypeReader.readUInt16(byteBuffer), IsoTypeReader.readUInt32(byteBuffer));
                }
            }
        }
        return cencSampleAuxiliaryDataFormat;
    }

    public UUID getDefaultKeyId() {
        return this.defaultKeyId;
    }

    public String getName() {
        return "enc(" + super.getName() + ")";
    }

    public List<CencSampleAuxiliaryDataFormat> getSampleEncryptionEntries() {
        return this.sampleEncryptionEntries;
    }

    public boolean hasSubSampleEncryption() {
        return false;
    }

    public String toString() {
        return "CencMp4TrackImpl{handler='" + getHandler() + '\'' + '}';
    }
}
