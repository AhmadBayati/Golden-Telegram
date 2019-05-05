package com.mp4parser.iso14496.part30;

import com.coremedia.iso.Utf8;
import com.coremedia.iso.boxes.SampleDescriptionBox;
import com.googlecode.mp4parser.DataSource;
import com.googlecode.mp4parser.authoring.AbstractTrack;
import com.googlecode.mp4parser.authoring.Sample;
import com.googlecode.mp4parser.authoring.TrackMetaData;
import com.googlecode.mp4parser.util.CastUtils;
import java.util.ArrayList;
import java.util.List;

public class WebVTTTrack extends AbstractTrack {
    WebVTTSampleEntry sampleEntry;
    List<Sample> samples;
    String[] subs;

    public WebVTTTrack(DataSource dataSource) {
        super(dataSource.toString());
        this.samples = new ArrayList();
        this.sampleEntry = new WebVTTSampleEntry();
        this.sampleEntry.addBox(new WebVTTConfigurationBox());
        this.sampleEntry.addBox(new WebVTTSourceLabelBox());
        byte[] bArr = new byte[CastUtils.l2i(dataSource.size())];
        dataSource.map(0, (long) CastUtils.l2i(dataSource.size())).get(bArr);
        this.subs = Utf8.convert(bArr).split("\\r?\\n");
        Object obj = TtmlNode.ANONYMOUS_REGION_ID;
        int i = 0;
        while (i < this.subs.length) {
            obj = new StringBuilder(String.valueOf(obj)).append(this.subs[i]).append("\n").toString();
            if (this.subs[i + 1].isEmpty() && this.subs[i + 2].isEmpty()) {
                break;
            }
            i++;
        }
        while (i < this.subs.length && this.subs[i].isEmpty()) {
            i++;
        }
    }

    public void close() {
    }

    public String getHandler() {
        return null;
    }

    public SampleDescriptionBox getSampleDescriptionBox() {
        return null;
    }

    public long[] getSampleDurations() {
        return new long[0];
    }

    public List<Sample> getSamples() {
        return null;
    }

    public TrackMetaData getTrackMetaData() {
        return null;
    }
}
