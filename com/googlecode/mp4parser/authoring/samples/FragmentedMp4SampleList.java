package com.googlecode.mp4parser.authoring.samples;

import com.coremedia.iso.IsoFile;
import com.coremedia.iso.boxes.Box;
import com.coremedia.iso.boxes.Container;
import com.coremedia.iso.boxes.TrackBox;
import com.coremedia.iso.boxes.fragment.MovieFragmentBox;
import com.coremedia.iso.boxes.fragment.TrackExtendsBox;
import com.coremedia.iso.boxes.fragment.TrackFragmentBox;
import com.coremedia.iso.boxes.fragment.TrackFragmentHeaderBox;
import com.coremedia.iso.boxes.fragment.TrackRunBox;
import com.coremedia.iso.boxes.fragment.TrackRunBox.Entry;
import com.googlecode.mp4parser.authoring.Sample;
import com.googlecode.mp4parser.util.CastUtils;
import com.googlecode.mp4parser.util.Path;
import java.lang.ref.SoftReference;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FragmentedMp4SampleList extends AbstractList<Sample> {
    private List<TrackFragmentBox> allTrafs;
    private int[] firstSamples;
    IsoFile[] fragments;
    private SoftReference<Sample>[] sampleCache;
    private int size_;
    Container topLevel;
    TrackBox trackBox;
    TrackExtendsBox trex;
    private Map<TrackRunBox, SoftReference<ByteBuffer>> trunDataCache;

    /* renamed from: com.googlecode.mp4parser.authoring.samples.FragmentedMp4SampleList.1 */
    class C03221 implements Sample {
        private final /* synthetic */ int val$finalOffset;
        private final /* synthetic */ ByteBuffer val$finalTrunData;
        private final /* synthetic */ long val$sampleSize;

        C03221(long j, ByteBuffer byteBuffer, int i) {
            this.val$sampleSize = j;
            this.val$finalTrunData = byteBuffer;
            this.val$finalOffset = i;
        }

        public ByteBuffer asByteBuffer() {
            return (ByteBuffer) ((ByteBuffer) this.val$finalTrunData.position(this.val$finalOffset)).slice().limit(CastUtils.l2i(this.val$sampleSize));
        }

        public long getSize() {
            return this.val$sampleSize;
        }

        public void writeTo(WritableByteChannel writableByteChannel) {
            writableByteChannel.write(asByteBuffer());
        }
    }

    public FragmentedMp4SampleList(long j, Container container, IsoFile... isoFileArr) {
        this.trackBox = null;
        this.trex = null;
        this.trunDataCache = new HashMap();
        this.size_ = -1;
        this.topLevel = container;
        this.fragments = isoFileArr;
        for (TrackBox trackBox : Path.getPaths(container, "moov[0]/trak")) {
            if (trackBox.getTrackHeaderBox().getTrackId() == j) {
                this.trackBox = trackBox;
            }
        }
        if (this.trackBox == null) {
            throw new RuntimeException("This MP4 does not contain track " + j);
        }
        for (TrackExtendsBox trackExtendsBox : Path.getPaths(container, "moov[0]/mvex[0]/trex")) {
            if (trackExtendsBox.getTrackId() == this.trackBox.getTrackHeaderBox().getTrackId()) {
                this.trex = trackExtendsBox;
            }
        }
        this.sampleCache = (SoftReference[]) Array.newInstance(SoftReference.class, size());
        initAllFragments();
    }

    private int getTrafSize(TrackFragmentBox trackFragmentBox) {
        List boxes = trackFragmentBox.getBoxes();
        int i = 0;
        for (int i2 = 0; i2 < boxes.size(); i2++) {
            Box box = (Box) boxes.get(i2);
            if (box instanceof TrackRunBox) {
                i += CastUtils.l2i(((TrackRunBox) box).getSampleCount());
            }
        }
        return i;
    }

    private List<TrackFragmentBox> initAllFragments() {
        int i = 0;
        if (this.allTrafs != null) {
            return this.allTrafs;
        }
        List<TrackFragmentBox> arrayList = new ArrayList();
        for (MovieFragmentBox boxes : this.topLevel.getBoxes(MovieFragmentBox.class)) {
            for (TrackFragmentBox trackFragmentBox : boxes.getBoxes(TrackFragmentBox.class)) {
                if (trackFragmentBox.getTrackFragmentHeaderBox().getTrackId() == this.trackBox.getTrackHeaderBox().getTrackId()) {
                    arrayList.add(trackFragmentBox);
                }
            }
        }
        if (this.fragments != null) {
            for (IsoFile boxes2 : this.fragments) {
                for (MovieFragmentBox boxes3 : boxes2.getBoxes(MovieFragmentBox.class)) {
                    for (TrackFragmentBox trackFragmentBox2 : boxes3.getBoxes(TrackFragmentBox.class)) {
                        if (trackFragmentBox2.getTrackFragmentHeaderBox().getTrackId() == this.trackBox.getTrackHeaderBox().getTrackId()) {
                            arrayList.add(trackFragmentBox2);
                        }
                    }
                }
            }
        }
        this.allTrafs = arrayList;
        this.firstSamples = new int[this.allTrafs.size()];
        int i2 = 1;
        while (i < this.allTrafs.size()) {
            this.firstSamples[i] = i2;
            i2 += getTrafSize((TrackFragmentBox) this.allTrafs.get(i));
            i++;
        }
        return arrayList;
    }

    public Sample get(int i) {
        Sample sample;
        if (this.sampleCache[i] != null) {
            sample = (Sample) this.sampleCache[i].get();
            if (sample != null) {
                return sample;
            }
        }
        int i2 = i + 1;
        int length = this.firstSamples.length - 1;
        while (i2 - this.firstSamples[length] < 0) {
            length--;
        }
        TrackFragmentBox trackFragmentBox = (TrackFragmentBox) this.allTrafs.get(length);
        int i3 = i2 - this.firstSamples[length];
        Container container = (MovieFragmentBox) trackFragmentBox.getParent();
        int i4 = 0;
        for (Box box : trackFragmentBox.getBoxes()) {
            if (box instanceof TrackRunBox) {
                TrackRunBox trackRunBox = (TrackRunBox) box;
                if (trackRunBox.getEntries().size() < i3 - i4) {
                    i4 = trackRunBox.getEntries().size() + i4;
                } else {
                    long j;
                    List<Entry> entries = trackRunBox.getEntries();
                    TrackFragmentHeaderBox trackFragmentHeaderBox = trackFragmentBox.getTrackFragmentHeaderBox();
                    boolean isSampleSizePresent = trackRunBox.isSampleSizePresent();
                    boolean hasDefaultSampleSize = trackFragmentHeaderBox.hasDefaultSampleSize();
                    if (isSampleSizePresent) {
                        j = 0;
                    } else if (hasDefaultSampleSize) {
                        j = trackFragmentHeaderBox.getDefaultSampleSize();
                    } else if (this.trex == null) {
                        throw new RuntimeException("File doesn't contain trex box but track fragments aren't fully self contained. Cannot determine sample size.");
                    } else {
                        j = this.trex.getDefaultSampleSize();
                    }
                    SoftReference softReference = (SoftReference) this.trunDataCache.get(trackRunBox);
                    ByteBuffer byteBuffer = softReference != null ? (ByteBuffer) softReference.get() : null;
                    if (byteBuffer == null) {
                        long j2 = 0;
                        if (trackFragmentHeaderBox.hasBaseDataOffset()) {
                            j2 = 0 + trackFragmentHeaderBox.getBaseDataOffset();
                            container = container.getParent();
                        }
                        if (trackRunBox.isDataOffsetPresent()) {
                            j2 += (long) trackRunBox.getDataOffset();
                        }
                        int i5 = 0;
                        for (Entry sampleSize : entries) {
                            i5 = isSampleSizePresent ? (int) (((long) i5) + sampleSize.getSampleSize()) : (int) (((long) i5) + j);
                        }
                        try {
                            byteBuffer = container.getByteBuffer(j2, (long) i5);
                            this.trunDataCache.put(trackRunBox, new SoftReference(byteBuffer));
                        } catch (Throwable e) {
                            throw new RuntimeException(e);
                        }
                    }
                    int i6 = 0;
                    length = 0;
                    while (length < i3 - i4) {
                        int sampleSize2 = isSampleSizePresent ? (int) (((long) i6) + ((Entry) entries.get(length)).getSampleSize()) : (int) (((long) i6) + j);
                        length++;
                        i6 = sampleSize2;
                    }
                    sample = new C03221(isSampleSizePresent ? ((Entry) entries.get(i3 - i4)).getSampleSize() : j, byteBuffer, i6);
                    this.sampleCache[i] = new SoftReference(sample);
                    return sample;
                }
            }
        }
        throw new RuntimeException("Couldn't find sample in the traf I was looking");
    }

    public int size() {
        if (this.size_ != -1) {
            return this.size_;
        }
        int i = 0;
        for (MovieFragmentBox boxes : this.topLevel.getBoxes(MovieFragmentBox.class)) {
            for (TrackFragmentBox trackFragmentBox : boxes.getBoxes(TrackFragmentBox.class)) {
                if (trackFragmentBox.getTrackFragmentHeaderBox().getTrackId() == this.trackBox.getTrackHeaderBox().getTrackId()) {
                    i = (int) (((TrackRunBox) trackFragmentBox.getBoxes(TrackRunBox.class).get(0)).getSampleCount() + ((long) i));
                }
            }
        }
        IsoFile[] isoFileArr = this.fragments;
        int length = isoFileArr.length;
        int i2 = 0;
        int i3 = i;
        while (i2 < length) {
            i = i3;
            for (MovieFragmentBox boxes2 : isoFileArr[i2].getBoxes(MovieFragmentBox.class)) {
                for (TrackFragmentBox trackFragmentBox2 : boxes2.getBoxes(TrackFragmentBox.class)) {
                    if (trackFragmentBox2.getTrackFragmentHeaderBox().getTrackId() == this.trackBox.getTrackHeaderBox().getTrackId()) {
                        i = (int) (((TrackRunBox) trackFragmentBox2.getBoxes(TrackRunBox.class).get(0)).getSampleCount() + ((long) i));
                    }
                }
            }
            i2++;
            i3 = i;
        }
        this.size_ = i3;
        return i3;
    }
}
