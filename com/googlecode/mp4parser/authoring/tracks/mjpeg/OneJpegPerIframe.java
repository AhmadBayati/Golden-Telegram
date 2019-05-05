package com.googlecode.mp4parser.authoring.tracks.mjpeg;

import com.coremedia.iso.Hex;
import com.coremedia.iso.boxes.Box;
import com.coremedia.iso.boxes.CompositionTimeToSample;
import com.coremedia.iso.boxes.SampleDescriptionBox;
import com.coremedia.iso.boxes.sampleentry.VisualSampleEntry;
import com.googlecode.mp4parser.authoring.AbstractTrack;
import com.googlecode.mp4parser.authoring.Edit;
import com.googlecode.mp4parser.authoring.Sample;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.TrackMetaData;
import com.googlecode.mp4parser.boxes.mp4.ESDescriptorBox;
import com.googlecode.mp4parser.boxes.mp4.objectdescriptors.ESDescriptor;
import com.googlecode.mp4parser.boxes.mp4.objectdescriptors.ObjectDescriptorFactory;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel.MapMode;
import java.nio.channels.WritableByteChannel;
import java.util.AbstractList;
import java.util.Arrays;
import java.util.List;
import javax.imageio.ImageIO;

public class OneJpegPerIframe extends AbstractTrack {
    File[] jpegs;
    long[] sampleDurations;
    SampleDescriptionBox stsd;
    long[] syncSamples;
    TrackMetaData trackMetaData;

    /* renamed from: com.googlecode.mp4parser.authoring.tracks.mjpeg.OneJpegPerIframe.1 */
    class C03331 extends AbstractList<Sample> {

        /* renamed from: com.googlecode.mp4parser.authoring.tracks.mjpeg.OneJpegPerIframe.1.1 */
        class C03321 implements Sample {
            ByteBuffer sample;
            private final /* synthetic */ int val$index;

            C03321(int i) {
                this.val$index = i;
                this.sample = null;
            }

            public ByteBuffer asByteBuffer() {
                if (this.sample == null) {
                    try {
                        RandomAccessFile randomAccessFile = new RandomAccessFile(OneJpegPerIframe.this.jpegs[this.val$index], "r");
                        this.sample = randomAccessFile.getChannel().map(MapMode.READ_ONLY, 0, randomAccessFile.length());
                    } catch (Throwable e) {
                        throw new RuntimeException(e);
                    }
                }
                return this.sample;
            }

            public long getSize() {
                return OneJpegPerIframe.this.jpegs[this.val$index].length();
            }

            public void writeTo(WritableByteChannel writableByteChannel) {
                RandomAccessFile randomAccessFile = new RandomAccessFile(OneJpegPerIframe.this.jpegs[this.val$index], "r");
                randomAccessFile.getChannel().transferTo(0, randomAccessFile.length(), writableByteChannel);
                randomAccessFile.close();
            }
        }

        C03331() {
        }

        public Sample get(int i) {
            return new C03321(i);
        }

        public int size() {
            return OneJpegPerIframe.this.jpegs.length;
        }
    }

    public OneJpegPerIframe(String str, File[] fileArr, Track track) {
        super(str);
        this.trackMetaData = new TrackMetaData();
        this.jpegs = fileArr;
        if (track.getSyncSamples().length != fileArr.length) {
            throw new RuntimeException("Number of sync samples doesn't match the number of stills (" + track.getSyncSamples().length + " vs. " + fileArr.length + ")");
        }
        double d;
        BufferedImage read = ImageIO.read(fileArr[0]);
        this.trackMetaData.setWidth((double) read.getWidth());
        this.trackMetaData.setHeight((double) read.getHeight());
        this.trackMetaData.setTimescale(track.getTrackMetaData().getTimescale());
        long[] sampleDurations = track.getSampleDurations();
        long[] syncSamples = track.getSyncSamples();
        int i = 1;
        long j = 0;
        this.sampleDurations = new long[syncSamples.length];
        int i2 = 1;
        while (i2 < sampleDurations.length) {
            if (i < syncSamples.length && ((long) i2) == syncSamples[i]) {
                this.sampleDurations[i - 1] = j;
                j = 0;
                i++;
            }
            j += sampleDurations[i2];
            i2++;
        }
        this.sampleDurations[this.sampleDurations.length - 1] = j;
        this.stsd = new SampleDescriptionBox();
        Object visualSampleEntry = new VisualSampleEntry(VisualSampleEntry.TYPE1);
        this.stsd.addBox(visualSampleEntry);
        Box eSDescriptorBox = new ESDescriptorBox();
        eSDescriptorBox.setData(ByteBuffer.wrap(Hex.decodeHex("038080801B000100048080800D6C11000000000A1CB4000A1CB4068080800102")));
        eSDescriptorBox.setEsDescriptor((ESDescriptor) ObjectDescriptorFactory.createFrom(-1, ByteBuffer.wrap(Hex.decodeHex("038080801B000100048080800D6C11000000000A1CB4000A1CB4068080800102"))));
        visualSampleEntry.addBox(eSDescriptorBox);
        this.syncSamples = new long[fileArr.length];
        for (i2 = 0; i2 < this.syncSamples.length; i2++) {
            this.syncSamples[i2] = (long) (i2 + 1);
        }
        double d2 = 0.0d;
        Object obj = 1;
        visualSampleEntry = 1;
        for (Edit edit : track.getEdits()) {
            if (edit.getMediaTime() == -1 && r2 == null) {
                throw new RuntimeException("Cannot accept edit list for processing (1)");
            } else if (edit.getMediaTime() >= 0 && r1 == null) {
                throw new RuntimeException("Cannot accept edit list for processing (2)");
            } else if (edit.getMediaTime() == -1) {
                d2 += edit.getSegmentDuration();
            } else {
                d2 -= ((double) edit.getMediaTime()) / ((double) edit.getTimeScale());
                obj = null;
                visualSampleEntry = null;
            }
        }
        if (track.getCompositionTimeEntries() == null || track.getCompositionTimeEntries().size() <= 0) {
            d = d2;
        } else {
            j = 0;
            int[] copyOfRange = Arrays.copyOfRange(CompositionTimeToSample.blowupCompositionTimes(track.getCompositionTimeEntries()), 0, 50);
            for (i2 = 0; i2 < copyOfRange.length; i2++) {
                copyOfRange[i2] = (int) (((long) copyOfRange[i2]) + j);
                j += track.getSampleDurations()[i2];
            }
            Arrays.sort(copyOfRange);
            d = d2 + (((double) copyOfRange[0]) / ((double) track.getTrackMetaData().getTimescale()));
        }
        if (d < 0.0d) {
            getEdits().add(new Edit((long) ((-d) * ((double) getTrackMetaData().getTimescale())), getTrackMetaData().getTimescale(), 1.0d, ((double) getDuration()) / ((double) getTrackMetaData().getTimescale())));
        } else if (d > 0.0d) {
            getEdits().add(new Edit(-1, getTrackMetaData().getTimescale(), 1.0d, d));
            getEdits().add(new Edit(0, getTrackMetaData().getTimescale(), 1.0d, ((double) getDuration()) / ((double) getTrackMetaData().getTimescale())));
        }
    }

    public void close() {
    }

    public String getHandler() {
        return "vide";
    }

    public SampleDescriptionBox getSampleDescriptionBox() {
        return this.stsd;
    }

    public long[] getSampleDurations() {
        return this.sampleDurations;
    }

    public List<Sample> getSamples() {
        return new C03331();
    }

    public long[] getSyncSamples() {
        return this.syncSamples;
    }

    public TrackMetaData getTrackMetaData() {
        return this.trackMetaData;
    }
}
