package com.googlecode.mp4parser.authoring;

import com.coremedia.iso.boxes.CompositionTimeToSample.Entry;
import com.coremedia.iso.boxes.SampleDependencyTypeBox;
import com.coremedia.iso.boxes.SubSampleInformationBox;
import com.googlecode.mp4parser.boxes.mp4.samplegrouping.GroupEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractTrack implements Track {
    List<Edit> edits;
    String name;
    Map<GroupEntry, long[]> sampleGroups;

    public AbstractTrack(String str) {
        this.edits = new ArrayList();
        this.sampleGroups = new HashMap();
        this.name = str;
    }

    public List<Entry> getCompositionTimeEntries() {
        return null;
    }

    public long getDuration() {
        long j = 0;
        for (long j2 : getSampleDurations()) {
            j += j2;
        }
        return j;
    }

    public List<Edit> getEdits() {
        return this.edits;
    }

    public String getName() {
        return this.name;
    }

    public List<SampleDependencyTypeBox.Entry> getSampleDependencies() {
        return null;
    }

    public Map<GroupEntry, long[]> getSampleGroups() {
        return this.sampleGroups;
    }

    public SubSampleInformationBox getSubsampleInformationBox() {
        return null;
    }

    public long[] getSyncSamples() {
        return null;
    }
}
