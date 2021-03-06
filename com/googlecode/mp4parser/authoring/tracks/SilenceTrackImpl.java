package com.googlecode.mp4parser.authoring.tracks;

import com.coremedia.iso.boxes.CompositionTimeToSample.Entry;
import com.coremedia.iso.boxes.SampleDependencyTypeBox;
import com.coremedia.iso.boxes.SampleDescriptionBox;
import com.coremedia.iso.boxes.SubSampleInformationBox;
import com.coremedia.iso.boxes.sampleentry.AudioSampleEntry;
import com.googlecode.mp4parser.authoring.Edit;
import com.googlecode.mp4parser.authoring.Sample;
import com.googlecode.mp4parser.authoring.SampleImpl;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.TrackMetaData;
import com.googlecode.mp4parser.boxes.mp4.samplegrouping.GroupEntry;
import com.googlecode.mp4parser.util.CastUtils;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class SilenceTrackImpl implements Track {
    long[] decodingTimes;
    String name;
    List<Sample> samples;
    Track source;

    public SilenceTrackImpl(Track track, long j) {
        this.samples = new LinkedList();
        this.source = track;
        this.name = j + "ms silence";
        if (AudioSampleEntry.TYPE3.equals(track.getSampleDescriptionBox().getSampleEntry().getType())) {
            int l2i = CastUtils.l2i(((getTrackMetaData().getTimescale() * j) / 1000) / 1024);
            this.decodingTimes = new long[l2i];
            Arrays.fill(this.decodingTimes, ((getTrackMetaData().getTimescale() * j) / ((long) l2i)) / 1000);
            while (true) {
                int i = l2i - 1;
                if (l2i > 0) {
                    this.samples.add(new SampleImpl((ByteBuffer) ByteBuffer.wrap(new byte[]{ClosedCaptionCtrl.BACKSPACE, (byte) 16, (byte) 4, (byte) 96, (byte) -116, ClosedCaptionCtrl.MISC_CHAN_2}).rewind()));
                    l2i = i;
                } else {
                    return;
                }
            }
        }
        throw new RuntimeException("Tracks of type " + track.getClass().getSimpleName() + " are not supported");
    }

    public void close() {
    }

    public List<Entry> getCompositionTimeEntries() {
        return null;
    }

    public long getDuration() {
        long j = 0;
        for (long j2 : this.decodingTimes) {
            j += j2;
        }
        return j;
    }

    public List<Edit> getEdits() {
        return null;
    }

    public String getHandler() {
        return this.source.getHandler();
    }

    public String getName() {
        return this.name;
    }

    public List<SampleDependencyTypeBox.Entry> getSampleDependencies() {
        return null;
    }

    public SampleDescriptionBox getSampleDescriptionBox() {
        return this.source.getSampleDescriptionBox();
    }

    public long[] getSampleDurations() {
        return this.decodingTimes;
    }

    public Map<GroupEntry, long[]> getSampleGroups() {
        return this.source.getSampleGroups();
    }

    public List<Sample> getSamples() {
        return this.samples;
    }

    public SubSampleInformationBox getSubsampleInformationBox() {
        return null;
    }

    public long[] getSyncSamples() {
        return null;
    }

    public TrackMetaData getTrackMetaData() {
        return this.source.getTrackMetaData();
    }
}
