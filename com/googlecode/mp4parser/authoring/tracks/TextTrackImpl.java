package com.googlecode.mp4parser.authoring.tracks;

import com.coremedia.iso.boxes.Box;
import com.coremedia.iso.boxes.CompositionTimeToSample.Entry;
import com.coremedia.iso.boxes.SampleDependencyTypeBox;
import com.coremedia.iso.boxes.SampleDescriptionBox;
import com.coremedia.iso.boxes.SubSampleInformationBox;
import com.coremedia.iso.boxes.sampleentry.TextSampleEntry;
import com.coremedia.iso.boxes.sampleentry.TextSampleEntry.BoxRecord;
import com.coremedia.iso.boxes.sampleentry.TextSampleEntry.StyleRecord;
import com.googlecode.mp4parser.authoring.AbstractTrack;
import com.googlecode.mp4parser.authoring.Sample;
import com.googlecode.mp4parser.authoring.SampleImpl;
import com.googlecode.mp4parser.authoring.TrackMetaData;
import com.googlecode.mp4parser.boxes.threegpp26245.FontTableBox;
import com.googlecode.mp4parser.boxes.threegpp26245.FontTableBox.FontRecord;
import com.hanista.mobogram.messenger.exoplayer.C0700C;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class TextTrackImpl extends AbstractTrack {
    SampleDescriptionBox sampleDescriptionBox;
    List<Line> subs;
    TrackMetaData trackMetaData;

    public static class Line {
        long from;
        String text;
        long to;

        public Line(long j, long j2, String str) {
            this.from = j;
            this.to = j2;
            this.text = str;
        }

        public long getFrom() {
            return this.from;
        }

        public String getText() {
            return this.text;
        }

        public long getTo() {
            return this.to;
        }
    }

    public TextTrackImpl() {
        super("subtiles");
        this.trackMetaData = new TrackMetaData();
        this.subs = new LinkedList();
        this.sampleDescriptionBox = new SampleDescriptionBox();
        Object textSampleEntry = new TextSampleEntry(TextSampleEntry.TYPE1);
        textSampleEntry.setDataReferenceIndex(1);
        textSampleEntry.setStyleRecord(new StyleRecord());
        textSampleEntry.setBoxRecord(new BoxRecord());
        this.sampleDescriptionBox.addBox(textSampleEntry);
        Box fontTableBox = new FontTableBox();
        fontTableBox.setEntries(Collections.singletonList(new FontRecord(1, "Serif")));
        textSampleEntry.addBox(fontTableBox);
        this.trackMetaData.setCreationTime(new Date());
        this.trackMetaData.setModificationTime(new Date());
        this.trackMetaData.setTimescale(1000);
    }

    public void close() {
    }

    public List<Entry> getCompositionTimeEntries() {
        return null;
    }

    public String getHandler() {
        return "sbtl";
    }

    public List<SampleDependencyTypeBox.Entry> getSampleDependencies() {
        return null;
    }

    public SampleDescriptionBox getSampleDescriptionBox() {
        return this.sampleDescriptionBox;
    }

    public long[] getSampleDurations() {
        List<Long> arrayList = new ArrayList();
        long j = 0;
        for (Line line : this.subs) {
            j = line.from - j;
            if (j > 0) {
                arrayList.add(Long.valueOf(j));
            } else if (j < 0) {
                throw new Error("Subtitle display times may not intersect");
            }
            arrayList.add(Long.valueOf(line.to - line.from));
            j = line.to;
        }
        long[] jArr = new long[arrayList.size()];
        int i = 0;
        for (Long longValue : arrayList) {
            int i2 = i + 1;
            jArr[i] = longValue.longValue();
            i = i2;
        }
        return jArr;
    }

    public List<Sample> getSamples() {
        List<Sample> linkedList = new LinkedList();
        long j = 0;
        for (Line line : this.subs) {
            j = line.from - j;
            if (j > 0) {
                linkedList.add(new SampleImpl(ByteBuffer.wrap(new byte[2])));
            } else if (j < 0) {
                throw new Error("Subtitle display times may not intersect");
            }
            OutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
            try {
                dataOutputStream.writeShort(line.text.getBytes(C0700C.UTF8_NAME).length);
                dataOutputStream.write(line.text.getBytes(C0700C.UTF8_NAME));
                dataOutputStream.close();
                linkedList.add(new SampleImpl(ByteBuffer.wrap(byteArrayOutputStream.toByteArray())));
                j = line.to;
            } catch (IOException e) {
                throw new Error("VM is broken. Does not support UTF-8");
            }
        }
        return linkedList;
    }

    public List<Line> getSubs() {
        return this.subs;
    }

    public SubSampleInformationBox getSubsampleInformationBox() {
        return null;
    }

    public long[] getSyncSamples() {
        return null;
    }

    public TrackMetaData getTrackMetaData() {
        return this.trackMetaData;
    }
}
