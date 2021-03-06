package com.googlecode.mp4parser.authoring.tracks;

import com.coremedia.iso.boxes.CompositionTimeToSample.Entry;
import com.coremedia.iso.boxes.SampleDependencyTypeBox;
import com.coremedia.iso.boxes.SampleDescriptionBox;
import com.coremedia.iso.boxes.SubSampleInformationBox;
import com.googlecode.mp4parser.authoring.Edit;
import com.googlecode.mp4parser.authoring.Sample;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.TrackMetaData;
import com.googlecode.mp4parser.boxes.mp4.samplegrouping.GroupEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class ChangeTimeScaleTrack implements Track {
    private static final Logger LOG;
    List<Entry> ctts;
    long[] decodingTimes;
    Track source;
    long timeScale;

    static {
        LOG = Logger.getLogger(ChangeTimeScaleTrack.class.getName());
    }

    public ChangeTimeScaleTrack(Track track, long j, long[] jArr) {
        this.source = track;
        this.timeScale = j;
        double timescale = ((double) j) / ((double) track.getTrackMetaData().getTimescale());
        this.ctts = adjustCtts(track.getCompositionTimeEntries(), timescale);
        this.decodingTimes = adjustTts(track.getSampleDurations(), timescale, jArr, getTimes(track, jArr, j));
    }

    static List<Entry> adjustCtts(List<Entry> list, double d) {
        if (list == null) {
            return null;
        }
        List<Entry> arrayList = new ArrayList(list.size());
        for (Entry entry : list) {
            arrayList.add(new Entry(entry.getCount(), (int) Math.round(((double) entry.getOffset()) * d)));
        }
        return arrayList;
    }

    static long[] adjustTts(long[] jArr, double d, long[] jArr2, long[] jArr3) {
        long j = 0;
        long[] jArr4 = new long[jArr.length];
        for (int i = 1; i <= jArr.length; i++) {
            long round = Math.round(((double) jArr[i - 1]) * d);
            int binarySearch = Arrays.binarySearch(jArr2, (long) (i + 1));
            if (binarySearch >= 0 && jArr3[binarySearch] != j) {
                long j2 = jArr3[binarySearch] - (j + round);
                LOG.finest(String.format("Sample %d %d / %d - correct by %d", new Object[]{Integer.valueOf(i), Long.valueOf(j), Long.valueOf(jArr3[binarySearch]), Long.valueOf(j2)}));
                round += j2;
            }
            j += round;
            jArr4[i - 1] = round;
        }
        return jArr4;
    }

    private static long[] getTimes(Track track, long[] jArr, long j) {
        long[] jArr2 = new long[jArr.length];
        long j2 = 0;
        int i = 0;
        for (int i2 = 1; ((long) i2) <= jArr[jArr.length - 1]; i2++) {
            if (((long) i2) == jArr[i]) {
                int i3 = i + 1;
                jArr2[i] = (j2 * j) / track.getTrackMetaData().getTimescale();
                i = i3;
            }
            j2 += track.getSampleDurations()[i2 - 1];
        }
        return jArr2;
    }

    public void close() {
        this.source.close();
    }

    public List<Entry> getCompositionTimeEntries() {
        return this.ctts;
    }

    public long getDuration() {
        long j = 0;
        for (long j2 : this.decodingTimes) {
            j += j2;
        }
        return j;
    }

    public List<Edit> getEdits() {
        return this.source.getEdits();
    }

    public String getHandler() {
        return this.source.getHandler();
    }

    public String getName() {
        return "timeScale(" + this.source.getName() + ")";
    }

    public List<SampleDependencyTypeBox.Entry> getSampleDependencies() {
        return this.source.getSampleDependencies();
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
        return this.source.getSamples();
    }

    public SubSampleInformationBox getSubsampleInformationBox() {
        return this.source.getSubsampleInformationBox();
    }

    public long[] getSyncSamples() {
        return this.source.getSyncSamples();
    }

    public TrackMetaData getTrackMetaData() {
        TrackMetaData trackMetaData = (TrackMetaData) this.source.getTrackMetaData().clone();
        trackMetaData.setTimescale(this.timeScale);
        return trackMetaData;
    }

    public String toString() {
        return "ChangeTimeScaleTrack{source=" + this.source + '}';
    }
}
