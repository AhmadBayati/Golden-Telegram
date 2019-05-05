package com.googlecode.mp4parser.authoring;

import com.coremedia.iso.IsoFile;
import com.coremedia.iso.boxes.Box;
import com.coremedia.iso.boxes.CompositionTimeToSample.Entry;
import com.coremedia.iso.boxes.Container;
import com.coremedia.iso.boxes.EditListBox;
import com.coremedia.iso.boxes.MediaHeaderBox;
import com.coremedia.iso.boxes.MovieHeaderBox;
import com.coremedia.iso.boxes.SampleDependencyTypeBox;
import com.coremedia.iso.boxes.SampleDescriptionBox;
import com.coremedia.iso.boxes.SubSampleInformationBox;
import com.coremedia.iso.boxes.SubSampleInformationBox.SubSampleEntry;
import com.coremedia.iso.boxes.TimeToSampleBox;
import com.coremedia.iso.boxes.TrackBox;
import com.coremedia.iso.boxes.TrackHeaderBox;
import com.coremedia.iso.boxes.fragment.MovieExtendsBox;
import com.coremedia.iso.boxes.fragment.MovieFragmentBox;
import com.coremedia.iso.boxes.fragment.SampleFlags;
import com.coremedia.iso.boxes.fragment.TrackExtendsBox;
import com.coremedia.iso.boxes.fragment.TrackFragmentBox;
import com.coremedia.iso.boxes.fragment.TrackFragmentHeaderBox;
import com.coremedia.iso.boxes.fragment.TrackRunBox;
import com.coremedia.iso.boxes.mdat.SampleList;
import com.googlecode.mp4parser.AbstractContainerBox;
import com.googlecode.mp4parser.BasicContainer;
import com.googlecode.mp4parser.boxes.mp4.samplegrouping.GroupEntry;
import com.googlecode.mp4parser.boxes.mp4.samplegrouping.SampleGroupDescriptionBox;
import com.googlecode.mp4parser.boxes.mp4.samplegrouping.SampleToGroupBox;
import com.googlecode.mp4parser.util.CastUtils;
import com.googlecode.mp4parser.util.Path;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Mp4TrackImpl extends AbstractTrack {
    private List<Entry> compositionTimeEntries;
    private long[] decodingTimes;
    IsoFile[] fragments;
    private String handler;
    private List<SampleDependencyTypeBox.Entry> sampleDependencies;
    private SampleDescriptionBox sampleDescriptionBox;
    private List<Sample> samples;
    private SubSampleInformationBox subSampleInformationBox;
    private long[] syncSamples;
    TrackBox trackBox;
    private TrackMetaData trackMetaData;

    public Mp4TrackImpl(String str, TrackBox trackBox, IsoFile... isoFileArr) {
        int i;
        super(str);
        this.syncSamples = new long[0];
        this.trackMetaData = new TrackMetaData();
        this.subSampleInformationBox = null;
        long trackId = trackBox.getTrackHeaderBox().getTrackId();
        this.samples = new SampleList(trackBox, isoFileArr);
        AbstractContainerBox sampleTableBox = trackBox.getMediaBox().getMediaInformationBox().getSampleTableBox();
        this.handler = trackBox.getMediaBox().getHandlerBox().getHandlerType();
        List arrayList = new ArrayList();
        this.compositionTimeEntries = new ArrayList();
        this.sampleDependencies = new ArrayList();
        arrayList.addAll(sampleTableBox.getTimeToSampleBox().getEntries());
        if (sampleTableBox.getCompositionTimeToSample() != null) {
            this.compositionTimeEntries.addAll(sampleTableBox.getCompositionTimeToSample().getEntries());
        }
        if (sampleTableBox.getSampleDependencyTypeBox() != null) {
            this.sampleDependencies.addAll(sampleTableBox.getSampleDependencyTypeBox().getEntries());
        }
        if (sampleTableBox.getSyncSampleBox() != null) {
            this.syncSamples = sampleTableBox.getSyncSampleBox().getSampleNumber();
        }
        this.subSampleInformationBox = (SubSampleInformationBox) Path.getPath(sampleTableBox, SubSampleInformationBox.TYPE);
        List<MovieFragmentBox> arrayList2 = new ArrayList();
        arrayList2.addAll(((Box) trackBox.getParent()).getParent().getBoxes(MovieFragmentBox.class));
        for (IsoFile boxes : isoFileArr) {
            arrayList2.addAll(boxes.getBoxes(MovieFragmentBox.class));
        }
        this.sampleDescriptionBox = sampleTableBox.getSampleDescriptionBox();
        List<MovieExtendsBox> boxes2 = trackBox.getParent().getBoxes(MovieExtendsBox.class);
        if (boxes2.size() > 0) {
            for (MovieExtendsBox boxes3 : boxes2) {
                for (TrackExtendsBox trackExtendsBox : boxes3.getBoxes(TrackExtendsBox.class)) {
                    if (trackExtendsBox.getTrackId() == trackId) {
                        if (Path.getPaths(((Box) trackBox.getParent()).getParent(), "/moof/traf/subs").size() > 0) {
                            this.subSampleInformationBox = new SubSampleInformationBox();
                        }
                        List<Long> linkedList = new LinkedList();
                        long j = 1;
                        for (MovieFragmentBox boxes4 : arrayList2) {
                            long j2 = j;
                            for (AbstractContainerBox sampleTableBox2 : boxes4.getBoxes(TrackFragmentBox.class)) {
                                if (sampleTableBox2.getTrackFragmentHeaderBox().getTrackId() == trackId) {
                                    SubSampleInformationBox subSampleInformationBox = (SubSampleInformationBox) Path.getPath(sampleTableBox2, SubSampleInformationBox.TYPE);
                                    if (subSampleInformationBox != null) {
                                        long j3 = (j2 - ((long) 0)) - 1;
                                        for (SubSampleEntry subSampleEntry : subSampleInformationBox.getEntries()) {
                                            SubSampleEntry subSampleEntry2 = new SubSampleEntry();
                                            subSampleEntry2.getSubsampleEntries().addAll(subSampleEntry.getSubsampleEntries());
                                            if (j3 != 0) {
                                                subSampleEntry2.setSampleDelta(subSampleEntry.getSampleDelta() + j3);
                                                j = 0;
                                            } else {
                                                subSampleEntry2.setSampleDelta(subSampleEntry.getSampleDelta());
                                                j = j3;
                                            }
                                            this.subSampleInformationBox.getEntries().add(subSampleEntry2);
                                            j3 = j;
                                        }
                                    }
                                    for (TrackRunBox trackRunBox : sampleTableBox2.getBoxes(TrackRunBox.class)) {
                                        TrackFragmentHeaderBox trackFragmentHeaderBox = ((TrackFragmentBox) trackRunBox.getParent()).getTrackFragmentHeaderBox();
                                        Object obj = 1;
                                        for (TrackRunBox.Entry entry : trackRunBox.getEntries()) {
                                            if (trackRunBox.isSampleDurationPresent()) {
                                                if (arrayList.size() == 0 || ((TimeToSampleBox.Entry) arrayList.get(arrayList.size() - 1)).getDelta() != entry.getSampleDuration()) {
                                                    arrayList.add(new TimeToSampleBox.Entry(1, entry.getSampleDuration()));
                                                } else {
                                                    TimeToSampleBox.Entry entry2 = (TimeToSampleBox.Entry) arrayList.get(arrayList.size() - 1);
                                                    entry2.setCount(entry2.getCount() + 1);
                                                }
                                            } else if (trackFragmentHeaderBox.hasDefaultSampleDuration()) {
                                                arrayList.add(new TimeToSampleBox.Entry(1, trackFragmentHeaderBox.getDefaultSampleDuration()));
                                            } else {
                                                arrayList.add(new TimeToSampleBox.Entry(1, trackExtendsBox.getDefaultSampleDuration()));
                                            }
                                            if (trackRunBox.isSampleCompositionTimeOffsetPresent()) {
                                                if (this.compositionTimeEntries.size() != 0) {
                                                    if (((long) ((Entry) this.compositionTimeEntries.get(this.compositionTimeEntries.size() - 1)).getOffset()) == entry.getSampleCompositionTimeOffset()) {
                                                        Entry entry3 = (Entry) this.compositionTimeEntries.get(this.compositionTimeEntries.size() - 1);
                                                        entry3.setCount(entry3.getCount() + 1);
                                                    }
                                                }
                                                this.compositionTimeEntries.add(new Entry(1, CastUtils.l2i(entry.getSampleCompositionTimeOffset())));
                                            }
                                            SampleFlags sampleFlags = trackRunBox.isSampleFlagsPresent() ? entry.getSampleFlags() : (obj == null || !trackRunBox.isFirstSampleFlagsPresent()) ? trackFragmentHeaderBox.hasDefaultSampleFlags() ? trackFragmentHeaderBox.getDefaultSampleFlags() : trackExtendsBox.getDefaultSampleFlags() : trackRunBox.getFirstSampleFlags();
                                            if (!(sampleFlags == null || sampleFlags.isSampleIsDifferenceSample())) {
                                                linkedList.add(Long.valueOf(j2));
                                            }
                                            j2++;
                                            obj = null;
                                        }
                                    }
                                }
                            }
                            j = j2;
                        }
                        Object obj2 = this.syncSamples;
                        this.syncSamples = new long[(this.syncSamples.length + linkedList.size())];
                        System.arraycopy(obj2, 0, this.syncSamples, 0, obj2.length);
                        int length = obj2.length;
                        for (Long longValue : linkedList) {
                            i = length + 1;
                            this.syncSamples[length] = longValue.longValue();
                            length = i;
                        }
                    }
                }
            }
            ArrayList arrayList3 = new ArrayList();
            arrayList3 = new ArrayList();
            for (MovieFragmentBox boxes5 : arrayList2) {
                for (Container container : boxes5.getBoxes(TrackFragmentBox.class)) {
                    if (container.getTrackFragmentHeaderBox().getTrackId() == trackId) {
                        this.sampleGroups = getSampleGroups(Path.getPaths(container, SampleGroupDescriptionBox.TYPE), Path.getPaths(container, SampleToGroupBox.TYPE), this.sampleGroups);
                    }
                }
            }
        } else {
            this.sampleGroups = getSampleGroups(sampleTableBox2.getBoxes(SampleGroupDescriptionBox.class), sampleTableBox2.getBoxes(SampleToGroupBox.class), this.sampleGroups);
        }
        this.decodingTimes = TimeToSampleBox.blowupTimeToSamples(arrayList);
        MediaHeaderBox mediaHeaderBox = trackBox.getMediaBox().getMediaHeaderBox();
        TrackHeaderBox trackHeaderBox = trackBox.getTrackHeaderBox();
        this.trackMetaData.setTrackId(trackHeaderBox.getTrackId());
        this.trackMetaData.setCreationTime(mediaHeaderBox.getCreationTime());
        this.trackMetaData.setLanguage(mediaHeaderBox.getLanguage());
        this.trackMetaData.setModificationTime(mediaHeaderBox.getModificationTime());
        this.trackMetaData.setTimescale(mediaHeaderBox.getTimescale());
        this.trackMetaData.setHeight(trackHeaderBox.getHeight());
        this.trackMetaData.setWidth(trackHeaderBox.getWidth());
        this.trackMetaData.setLayer(trackHeaderBox.getLayer());
        this.trackMetaData.setMatrix(trackHeaderBox.getMatrix());
        EditListBox editListBox = (EditListBox) Path.getPath((AbstractContainerBox) trackBox, "edts/elst");
        MovieHeaderBox movieHeaderBox = (MovieHeaderBox) Path.getPath((AbstractContainerBox) trackBox, "../mvhd");
        if (editListBox != null) {
            for (EditListBox.Entry entry4 : editListBox.getEntries()) {
                this.edits.add(new Edit(entry4.getMediaTime(), mediaHeaderBox.getTimescale(), entry4.getMediaRate(), ((double) entry4.getSegmentDuration()) / ((double) movieHeaderBox.getTimescale())));
            }
        }
    }

    private Map<GroupEntry, long[]> getSampleGroups(List<SampleGroupDescriptionBox> list, List<SampleToGroupBox> list2, Map<GroupEntry, long[]> map) {
        for (SampleGroupDescriptionBox sampleGroupDescriptionBox : list) {
            Object obj = null;
            for (SampleToGroupBox sampleToGroupBox : list2) {
                if (sampleToGroupBox.getGroupingType().equals(((GroupEntry) sampleGroupDescriptionBox.getGroupEntries().get(0)).getType())) {
                    int i = 0;
                    for (SampleToGroupBox.Entry entry : sampleToGroupBox.getEntries()) {
                        if (entry.getGroupDescriptionIndex() > 0) {
                            GroupEntry groupEntry = (GroupEntry) sampleGroupDescriptionBox.getGroupEntries().get(entry.getGroupDescriptionIndex() - 1);
                            obj = (long[]) map.get(groupEntry);
                            if (obj == null) {
                                obj = new long[0];
                            }
                            Object obj2 = new long[(CastUtils.l2i(entry.getSampleCount()) + obj.length)];
                            System.arraycopy(obj, 0, obj2, 0, obj.length);
                            for (int i2 = 0; ((long) i2) < entry.getSampleCount(); i2++) {
                                obj2[obj.length + i2] = (long) (i + i2);
                            }
                            map.put(groupEntry, obj2);
                        }
                        i = (int) (((long) i) + entry.getSampleCount());
                    }
                    obj = 1;
                }
            }
            if (obj == null) {
                throw new RuntimeException("Could not find SampleToGroupBox for " + ((GroupEntry) sampleGroupDescriptionBox.getGroupEntries().get(0)).getType() + ".");
            }
        }
        return map;
    }

    public void close() {
        Container parent = this.trackBox.getParent();
        if (parent instanceof BasicContainer) {
            ((BasicContainer) parent).close();
        }
        for (IsoFile close : this.fragments) {
            close.close();
        }
    }

    public List<Entry> getCompositionTimeEntries() {
        return this.compositionTimeEntries;
    }

    public String getHandler() {
        return this.handler;
    }

    public List<SampleDependencyTypeBox.Entry> getSampleDependencies() {
        return this.sampleDependencies;
    }

    public SampleDescriptionBox getSampleDescriptionBox() {
        return this.sampleDescriptionBox;
    }

    public synchronized long[] getSampleDurations() {
        return this.decodingTimes;
    }

    public List<Sample> getSamples() {
        return this.samples;
    }

    public SubSampleInformationBox getSubsampleInformationBox() {
        return this.subSampleInformationBox;
    }

    public long[] getSyncSamples() {
        return this.syncSamples.length == this.samples.size() ? null : this.syncSamples;
    }

    public TrackMetaData getTrackMetaData() {
        return this.trackMetaData;
    }
}
