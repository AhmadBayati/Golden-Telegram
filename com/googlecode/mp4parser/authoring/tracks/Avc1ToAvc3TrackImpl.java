package com.googlecode.mp4parser.authoring.tracks;

import com.coremedia.iso.IsoFile;
import com.coremedia.iso.IsoTypeWriterVariable;
import com.coremedia.iso.boxes.SampleDescriptionBox;
import com.coremedia.iso.boxes.sampleentry.VisualSampleEntry;
import com.googlecode.mp4parser.MemoryDataSourceImpl;
import com.googlecode.mp4parser.authoring.Sample;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.WrappingTrack;
import com.googlecode.mp4parser.util.CastUtils;
import com.googlecode.mp4parser.util.Path;
import com.mp4parser.iso14496.part15.AvcConfigurationBox;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.util.AbstractList;
import java.util.Arrays;
import java.util.List;

public class Avc1ToAvc3TrackImpl extends WrappingTrack {
    AvcConfigurationBox avcC;
    List<Sample> samples;
    SampleDescriptionBox stsd;

    private class ReplaceSyncSamplesList extends AbstractList<Sample> {
        List<Sample> parentSamples;

        /* renamed from: com.googlecode.mp4parser.authoring.tracks.Avc1ToAvc3TrackImpl.ReplaceSyncSamplesList.1 */
        class C03251 implements Sample {
            private final /* synthetic */ ByteBuffer val$buf;
            private final /* synthetic */ int val$len;
            private final /* synthetic */ Sample val$orignalSample;

            C03251(ByteBuffer byteBuffer, int i, Sample sample) {
                this.val$buf = byteBuffer;
                this.val$len = i;
                this.val$orignalSample = sample;
            }

            public ByteBuffer asByteBuffer() {
                int i = 0;
                for (byte[] bArr : Avc1ToAvc3TrackImpl.this.avcC.getSequenceParameterSets()) {
                    i = (bArr.length + this.val$len) + i;
                }
                for (byte[] bArr2 : Avc1ToAvc3TrackImpl.this.avcC.getSequenceParameterSetExts()) {
                    i += bArr2.length + this.val$len;
                }
                for (byte[] bArr22 : Avc1ToAvc3TrackImpl.this.avcC.getPictureParameterSets()) {
                    i += bArr22.length + this.val$len;
                }
                ByteBuffer allocate = ByteBuffer.allocate(CastUtils.l2i(this.val$orignalSample.getSize()) + i);
                for (byte[] bArr222 : Avc1ToAvc3TrackImpl.this.avcC.getSequenceParameterSets()) {
                    IsoTypeWriterVariable.write((long) bArr222.length, allocate, this.val$len);
                    allocate.put(bArr222);
                }
                for (byte[] bArr2222 : Avc1ToAvc3TrackImpl.this.avcC.getSequenceParameterSetExts()) {
                    IsoTypeWriterVariable.write((long) bArr2222.length, allocate, this.val$len);
                    allocate.put(bArr2222);
                }
                for (byte[] bArr22222 : Avc1ToAvc3TrackImpl.this.avcC.getPictureParameterSets()) {
                    IsoTypeWriterVariable.write((long) bArr22222.length, allocate, this.val$len);
                    allocate.put(bArr22222);
                }
                allocate.put(this.val$orignalSample.asByteBuffer());
                return (ByteBuffer) allocate.rewind();
            }

            public long getSize() {
                int i = 0;
                for (byte[] bArr : Avc1ToAvc3TrackImpl.this.avcC.getSequenceParameterSets()) {
                    i = (bArr.length + this.val$len) + i;
                }
                for (byte[] bArr2 : Avc1ToAvc3TrackImpl.this.avcC.getSequenceParameterSetExts()) {
                    i += bArr2.length + this.val$len;
                }
                for (byte[] bArr22 : Avc1ToAvc3TrackImpl.this.avcC.getPictureParameterSets()) {
                    i += bArr22.length + this.val$len;
                }
                return ((long) i) + this.val$orignalSample.getSize();
            }

            public void writeTo(WritableByteChannel writableByteChannel) {
                for (byte[] bArr : Avc1ToAvc3TrackImpl.this.avcC.getSequenceParameterSets()) {
                    IsoTypeWriterVariable.write((long) bArr.length, (ByteBuffer) this.val$buf.rewind(), this.val$len);
                    writableByteChannel.write((ByteBuffer) this.val$buf.rewind());
                    writableByteChannel.write(ByteBuffer.wrap(bArr));
                }
                for (byte[] bArr2 : Avc1ToAvc3TrackImpl.this.avcC.getSequenceParameterSetExts()) {
                    IsoTypeWriterVariable.write((long) bArr2.length, (ByteBuffer) this.val$buf.rewind(), this.val$len);
                    writableByteChannel.write((ByteBuffer) this.val$buf.rewind());
                    writableByteChannel.write(ByteBuffer.wrap(bArr2));
                }
                for (byte[] bArr22 : Avc1ToAvc3TrackImpl.this.avcC.getPictureParameterSets()) {
                    IsoTypeWriterVariable.write((long) bArr22.length, (ByteBuffer) this.val$buf.rewind(), this.val$len);
                    writableByteChannel.write((ByteBuffer) this.val$buf.rewind());
                    writableByteChannel.write(ByteBuffer.wrap(bArr22));
                }
                this.val$orignalSample.writeTo(writableByteChannel);
            }
        }

        public ReplaceSyncSamplesList(List<Sample> list) {
            this.parentSamples = list;
        }

        public Sample get(int i) {
            if (Arrays.binarySearch(Avc1ToAvc3TrackImpl.this.getSyncSamples(), (long) (i + 1)) < 0) {
                return (Sample) this.parentSamples.get(i);
            }
            int lengthSizeMinusOne = Avc1ToAvc3TrackImpl.this.avcC.getLengthSizeMinusOne() + 1;
            return new C03251(ByteBuffer.allocate(lengthSizeMinusOne), lengthSizeMinusOne, (Sample) this.parentSamples.get(i));
        }

        public int size() {
            return this.parentSamples.size();
        }
    }

    public Avc1ToAvc3TrackImpl(Track track) {
        super(track);
        if (VisualSampleEntry.TYPE3.equals(track.getSampleDescriptionBox().getSampleEntry().getType())) {
            OutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            track.getSampleDescriptionBox().getBox(Channels.newChannel(byteArrayOutputStream));
            this.stsd = (SampleDescriptionBox) Path.getPath(new IsoFile(new MemoryDataSourceImpl(byteArrayOutputStream.toByteArray())), SampleDescriptionBox.TYPE);
            ((VisualSampleEntry) this.stsd.getSampleEntry()).setType(VisualSampleEntry.TYPE4);
            this.avcC = (AvcConfigurationBox) Path.getPath(this.stsd, "avc./avcC");
            this.samples = new ReplaceSyncSamplesList(track.getSamples());
            return;
        }
        throw new RuntimeException("Only avc1 tracks can be converted to avc3 tracks");
    }

    public SampleDescriptionBox getSampleDescriptionBox() {
        return this.stsd;
    }

    public List<Sample> getSamples() {
        return this.samples;
    }
}
