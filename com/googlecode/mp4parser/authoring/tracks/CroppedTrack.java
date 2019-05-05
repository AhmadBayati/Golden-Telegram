package com.googlecode.mp4parser.authoring.tracks;

import com.coremedia.iso.boxes.CompositionTimeToSample.Entry;
import com.coremedia.iso.boxes.SampleDependencyTypeBox;
import com.coremedia.iso.boxes.SampleDescriptionBox;
import com.coremedia.iso.boxes.SubSampleInformationBox;
import com.coremedia.iso.boxes.TimeToSampleBox;
import com.googlecode.mp4parser.authoring.AbstractTrack;
import com.googlecode.mp4parser.authoring.Sample;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.TrackMetaData;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

public class CroppedTrack extends AbstractTrack {
    static final /* synthetic */ boolean $assertionsDisabled;
    private int fromSample;
    Track origTrack;
    private int toSample;

    static {
        $assertionsDisabled = !CroppedTrack.class.desiredAssertionStatus();
    }

    public CroppedTrack(Track track, long j, long j2) {
        super("crop(" + track.getName() + ")");
        this.origTrack = track;
        if (!$assertionsDisabled && j > 2147483647L) {
            throw new AssertionError();
        } else if ($assertionsDisabled || j2 <= 2147483647L) {
            this.fromSample = (int) j;
            this.toSample = (int) j2;
        } else {
            throw new AssertionError();
        }
    }

    static List<Entry> getCompositionTimeEntries(List<Entry> list, long j, long j2) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        ListIterator listIterator = list.listIterator();
        ArrayList arrayList = new ArrayList();
        long j3 = 0;
        while (true) {
            Entry entry = (Entry) listIterator.next();
            if (((long) entry.getCount()) + j3 > j) {
                break;
            }
            j3 = ((long) entry.getCount()) + j3;
        }
        if (((long) entry.getCount()) + j3 >= j2) {
            arrayList.add(new Entry((int) (j2 - j), entry.getOffset()));
            return arrayList;
        }
        arrayList.add(new Entry((int) ((((long) entry.getCount()) + j3) - j), entry.getOffset()));
        j3 += (long) entry.getCount();
        while (listIterator.hasNext()) {
            entry = (Entry) listIterator.next();
            if (((long) entry.getCount()) + j3 >= j2) {
                break;
            }
            arrayList.add(entry);
            j3 += (long) entry.getCount();
        }
        arrayList.add(new Entry((int) (j2 - j3), entry.getOffset()));
        return arrayList;
    }

    static List<TimeToSampleBox.Entry> getDecodingTimeEntries(List<TimeToSampleBox.Entry> list, long j, long j2) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        ListIterator listIterator = list.listIterator();
        LinkedList linkedList = new LinkedList();
        long j3 = 0;
        while (true) {
            TimeToSampleBox.Entry entry = (TimeToSampleBox.Entry) listIterator.next();
            if (entry.getCount() + j3 > j) {
                break;
            }
            j3 = entry.getCount() + j3;
        }
        if (entry.getCount() + j3 >= j2) {
            linkedList.add(new TimeToSampleBox.Entry(j2 - j, entry.getDelta()));
            return linkedList;
        }
        linkedList.add(new TimeToSampleBox.Entry((entry.getCount() + j3) - j, entry.getDelta()));
        j3 += entry.getCount();
        while (listIterator.hasNext()) {
            entry = (TimeToSampleBox.Entry) listIterator.next();
            if (entry.getCount() + j3 >= j2) {
                break;
            }
            linkedList.add(entry);
            j3 += entry.getCount();
        }
        linkedList.add(new TimeToSampleBox.Entry(j2 - j3, entry.getDelta()));
        return linkedList;
    }

    public void close() {
        this.origTrack.close();
    }

    public List<Entry> getCompositionTimeEntries() {
        return getCompositionTimeEntries(this.origTrack.getCompositionTimeEntries(), (long) this.fromSample, (long) this.toSample);
    }

    public String getHandler() {
        return this.origTrack.getHandler();
    }

    public List<SampleDependencyTypeBox.Entry> getSampleDependencies() {
        return (this.origTrack.getSampleDependencies() == null || this.origTrack.getSampleDependencies().isEmpty()) ? null : this.origTrack.getSampleDependencies().subList(this.fromSample, this.toSample);
    }

    public SampleDescriptionBox getSampleDescriptionBox() {
        return this.origTrack.getSampleDescriptionBox();
    }

    public synchronized long[] getSampleDurations() {
        Object obj;
        obj = new long[(this.toSample - this.fromSample)];
        System.arraycopy(this.origTrack.getSampleDurations(), this.fromSample, obj, 0, obj.length);
        return obj;
    }

    public List<Sample> getSamples() {
        return this.origTrack.getSamples().subList(this.fromSample, this.toSample);
    }

    public SubSampleInformationBox getSubsampleInformationBox() {
        return this.origTrack.getSubsampleInformationBox();
    }

    public synchronized long[] getSyncSamples() {
        long[] copyOfRange;
        int i = 0;
        synchronized (this) {
            if (this.origTrack.getSyncSamples() != null) {
                long[] syncSamples = this.origTrack.getSyncSamples();
                int length = syncSamples.length;
                int i2 = 0;
                while (i2 < syncSamples.length && syncSamples[i2] < ((long) this.fromSample)) {
                    i2++;
                }
                while (length > 0 && ((long) this.toSample) < syncSamples[length - 1]) {
                    length--;
                }
                copyOfRange = Arrays.copyOfRange(this.origTrack.getSyncSamples(), i2, length);
                while (i < copyOfRange.length) {
                    copyOfRange[i] = copyOfRange[i] - ((long) this.fromSample);
                    i++;
                }
            } else {
                copyOfRange = null;
            }
        }
        return copyOfRange;
    }

    public TrackMetaData getTrackMetaData() {
        return this.origTrack.getTrackMetaData();
    }
}
