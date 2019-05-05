package com.googlecode.mp4parser.authoring.tracks;

import com.coremedia.iso.boxes.Box;
import com.coremedia.iso.boxes.CompositionTimeToSample;
import com.coremedia.iso.boxes.CompositionTimeToSample.Entry;
import com.coremedia.iso.boxes.SampleDependencyTypeBox;
import com.coremedia.iso.boxes.SampleDescriptionBox;
import com.coremedia.iso.boxes.SubSampleInformationBox;
import com.coremedia.iso.boxes.sampleentry.AudioSampleEntry;
import com.coremedia.iso.boxes.sampleentry.SampleEntry;
import com.coremedia.iso.boxes.sampleentry.VisualSampleEntry;
import com.googlecode.mp4parser.authoring.AbstractTrack;
import com.googlecode.mp4parser.authoring.Sample;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.TrackMetaData;
import com.googlecode.mp4parser.boxes.mp4.AbstractDescriptorBox;
import com.googlecode.mp4parser.boxes.mp4.ESDescriptorBox;
import com.googlecode.mp4parser.boxes.mp4.objectdescriptors.BaseDescriptor;
import com.googlecode.mp4parser.boxes.mp4.objectdescriptors.DecoderConfigDescriptor;
import com.googlecode.mp4parser.boxes.mp4.objectdescriptors.ESDescriptor;
import com.googlecode.mp4parser.util.Logger;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.Channels;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class AppendTrack extends AbstractTrack {
    private static Logger LOG;
    SampleDescriptionBox stsd;
    Track[] tracks;

    static {
        LOG = Logger.getLogger(AppendTrack.class);
    }

    public AppendTrack(Track... trackArr) {
        super(appendTracknames(trackArr));
        this.tracks = trackArr;
        for (Track track : trackArr) {
            if (this.stsd == null) {
                this.stsd = new SampleDescriptionBox();
                this.stsd.addBox((Box) track.getSampleDescriptionBox().getBoxes(SampleEntry.class).get(0));
            } else {
                this.stsd = mergeStsds(this.stsd, track.getSampleDescriptionBox());
            }
        }
    }

    public static String appendTracknames(Track... trackArr) {
        String str = TtmlNode.ANONYMOUS_REGION_ID;
        String str2 = str;
        for (Track name : trackArr) {
            str2 = new StringBuilder(String.valueOf(str2)).append(name.getName()).append(" + ").toString();
        }
        return str2.substring(0, str2.length() - 3);
    }

    private AudioSampleEntry mergeAudioSampleEntries(AudioSampleEntry audioSampleEntry, AudioSampleEntry audioSampleEntry2) {
        AudioSampleEntry audioSampleEntry3 = new AudioSampleEntry(audioSampleEntry2.getType());
        if (audioSampleEntry.getBytesPerFrame() == audioSampleEntry2.getBytesPerFrame()) {
            audioSampleEntry3.setBytesPerFrame(audioSampleEntry.getBytesPerFrame());
            if (audioSampleEntry.getBytesPerPacket() != audioSampleEntry2.getBytesPerPacket()) {
                return null;
            }
            audioSampleEntry3.setBytesPerPacket(audioSampleEntry.getBytesPerPacket());
            if (audioSampleEntry.getBytesPerSample() == audioSampleEntry2.getBytesPerSample()) {
                audioSampleEntry3.setBytesPerSample(audioSampleEntry.getBytesPerSample());
                if (audioSampleEntry.getChannelCount() != audioSampleEntry2.getChannelCount()) {
                    return null;
                }
                audioSampleEntry3.setChannelCount(audioSampleEntry.getChannelCount());
                if (audioSampleEntry.getPacketSize() == audioSampleEntry2.getPacketSize()) {
                    audioSampleEntry3.setPacketSize(audioSampleEntry.getPacketSize());
                    if (audioSampleEntry.getCompressionId() != audioSampleEntry2.getCompressionId()) {
                        return null;
                    }
                    audioSampleEntry3.setCompressionId(audioSampleEntry.getCompressionId());
                    if (audioSampleEntry.getSampleRate() != audioSampleEntry2.getSampleRate()) {
                        return null;
                    }
                    audioSampleEntry3.setSampleRate(audioSampleEntry.getSampleRate());
                    if (audioSampleEntry.getSampleSize() != audioSampleEntry2.getSampleSize()) {
                        return null;
                    }
                    audioSampleEntry3.setSampleSize(audioSampleEntry.getSampleSize());
                    if (audioSampleEntry.getSamplesPerPacket() != audioSampleEntry2.getSamplesPerPacket()) {
                        return null;
                    }
                    audioSampleEntry3.setSamplesPerPacket(audioSampleEntry.getSamplesPerPacket());
                    if (audioSampleEntry.getSoundVersion() != audioSampleEntry2.getSoundVersion()) {
                        return null;
                    }
                    audioSampleEntry3.setSoundVersion(audioSampleEntry.getSoundVersion());
                    if (!Arrays.equals(audioSampleEntry.getSoundVersion2Data(), audioSampleEntry2.getSoundVersion2Data())) {
                        return null;
                    }
                    audioSampleEntry3.setSoundVersion2Data(audioSampleEntry.getSoundVersion2Data());
                    if (audioSampleEntry.getBoxes().size() == audioSampleEntry2.getBoxes().size()) {
                        Iterator it = audioSampleEntry2.getBoxes().iterator();
                        for (Box box : audioSampleEntry.getBoxes()) {
                            Box box2 = (Box) it.next();
                            OutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                            OutputStream byteArrayOutputStream2 = new ByteArrayOutputStream();
                            try {
                                box.getBox(Channels.newChannel(byteArrayOutputStream));
                                box2.getBox(Channels.newChannel(byteArrayOutputStream2));
                                if (Arrays.equals(byteArrayOutputStream.toByteArray(), byteArrayOutputStream2.toByteArray())) {
                                    audioSampleEntry3.addBox(box);
                                } else if (ESDescriptorBox.TYPE.equals(box.getType()) && ESDescriptorBox.TYPE.equals(box2.getType())) {
                                    ESDescriptorBox eSDescriptorBox = (ESDescriptorBox) box;
                                    eSDescriptorBox.setDescriptor(mergeDescriptors(eSDescriptorBox.getEsDescriptor(), ((ESDescriptorBox) box2).getEsDescriptor()));
                                    audioSampleEntry3.addBox(box);
                                }
                            } catch (IOException e) {
                                LOG.logWarn(e.getMessage());
                                return null;
                            }
                        }
                    }
                    return audioSampleEntry3;
                }
                LOG.logError("ChannelCount differ");
                return null;
            }
            LOG.logError("BytesPerSample differ");
            return null;
        }
        LOG.logError("BytesPerFrame differ");
        return null;
    }

    private ESDescriptor mergeDescriptors(BaseDescriptor baseDescriptor, BaseDescriptor baseDescriptor2) {
        if ((baseDescriptor instanceof ESDescriptor) && (baseDescriptor2 instanceof ESDescriptor)) {
            ESDescriptor eSDescriptor = (ESDescriptor) baseDescriptor;
            ESDescriptor eSDescriptor2 = (ESDescriptor) baseDescriptor2;
            if (eSDescriptor.getURLFlag() != eSDescriptor2.getURLFlag()) {
                return null;
            }
            eSDescriptor.getURLLength();
            eSDescriptor2.getURLLength();
            if (eSDescriptor.getDependsOnEsId() != eSDescriptor2.getDependsOnEsId()) {
                return null;
            }
            if (eSDescriptor.getEsId() != eSDescriptor2.getEsId()) {
                return null;
            }
            if (eSDescriptor.getoCREsId() != eSDescriptor2.getoCREsId()) {
                return null;
            }
            if (eSDescriptor.getoCRstreamFlag() != eSDescriptor2.getoCRstreamFlag()) {
                return null;
            }
            if (eSDescriptor.getRemoteODFlag() != eSDescriptor2.getRemoteODFlag()) {
                return null;
            }
            if (eSDescriptor.getStreamDependenceFlag() != eSDescriptor2.getStreamDependenceFlag()) {
                return null;
            }
            eSDescriptor.getStreamPriority();
            eSDescriptor2.getStreamPriority();
            if (eSDescriptor.getURLString() != null) {
                eSDescriptor.getURLString().equals(eSDescriptor2.getURLString());
            } else {
                eSDescriptor2.getURLString();
            }
            if (eSDescriptor.getDecoderConfigDescriptor() == null ? eSDescriptor2.getDecoderConfigDescriptor() != null : !eSDescriptor.getDecoderConfigDescriptor().equals(eSDescriptor2.getDecoderConfigDescriptor())) {
                DecoderConfigDescriptor decoderConfigDescriptor = eSDescriptor.getDecoderConfigDescriptor();
                DecoderConfigDescriptor decoderConfigDescriptor2 = eSDescriptor2.getDecoderConfigDescriptor();
                if (decoderConfigDescriptor.getAudioSpecificInfo() != null && decoderConfigDescriptor2.getAudioSpecificInfo() != null && !decoderConfigDescriptor.getAudioSpecificInfo().equals(decoderConfigDescriptor2.getAudioSpecificInfo())) {
                    return null;
                }
                if (decoderConfigDescriptor.getAvgBitRate() != decoderConfigDescriptor2.getAvgBitRate()) {
                    decoderConfigDescriptor.setAvgBitRate((decoderConfigDescriptor.getAvgBitRate() + decoderConfigDescriptor2.getAvgBitRate()) / 2);
                }
                decoderConfigDescriptor.getBufferSizeDB();
                decoderConfigDescriptor2.getBufferSizeDB();
                if (decoderConfigDescriptor.getDecoderSpecificInfo() == null ? decoderConfigDescriptor2.getDecoderSpecificInfo() != null : !decoderConfigDescriptor.getDecoderSpecificInfo().equals(decoderConfigDescriptor2.getDecoderSpecificInfo())) {
                    return null;
                }
                if (decoderConfigDescriptor.getMaxBitRate() != decoderConfigDescriptor2.getMaxBitRate()) {
                    decoderConfigDescriptor.setMaxBitRate(Math.max(decoderConfigDescriptor.getMaxBitRate(), decoderConfigDescriptor2.getMaxBitRate()));
                }
                if (!decoderConfigDescriptor.getProfileLevelIndicationDescriptors().equals(decoderConfigDescriptor2.getProfileLevelIndicationDescriptors())) {
                    return null;
                }
                if (decoderConfigDescriptor.getObjectTypeIndication() != decoderConfigDescriptor2.getObjectTypeIndication()) {
                    return null;
                }
                if (decoderConfigDescriptor.getStreamType() != decoderConfigDescriptor2.getStreamType()) {
                    return null;
                }
                if (decoderConfigDescriptor.getUpStream() != decoderConfigDescriptor2.getUpStream()) {
                    return null;
                }
            }
            if (eSDescriptor.getOtherDescriptors() == null ? eSDescriptor2.getOtherDescriptors() != null : !eSDescriptor.getOtherDescriptors().equals(eSDescriptor2.getOtherDescriptors())) {
                return null;
            }
            if (eSDescriptor.getSlConfigDescriptor() != null) {
                if (eSDescriptor.getSlConfigDescriptor().equals(eSDescriptor2.getSlConfigDescriptor())) {
                    return eSDescriptor;
                }
            } else if (eSDescriptor2.getSlConfigDescriptor() == null) {
                return eSDescriptor;
            }
            return null;
        }
        LOG.logError("I can only merge ESDescriptors");
        return null;
    }

    private SampleEntry mergeSampleEntry(SampleEntry sampleEntry, SampleEntry sampleEntry2) {
        return !sampleEntry.getType().equals(sampleEntry2.getType()) ? null : ((sampleEntry instanceof VisualSampleEntry) && (sampleEntry2 instanceof VisualSampleEntry)) ? mergeVisualSampleEntry((VisualSampleEntry) sampleEntry, (VisualSampleEntry) sampleEntry2) : ((sampleEntry instanceof AudioSampleEntry) && (sampleEntry2 instanceof AudioSampleEntry)) ? mergeAudioSampleEntries((AudioSampleEntry) sampleEntry, (AudioSampleEntry) sampleEntry2) : null;
    }

    private SampleDescriptionBox mergeStsds(SampleDescriptionBox sampleDescriptionBox, SampleDescriptionBox sampleDescriptionBox2) {
        OutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        OutputStream byteArrayOutputStream2 = new ByteArrayOutputStream();
        try {
            sampleDescriptionBox.getBox(Channels.newChannel(byteArrayOutputStream));
            sampleDescriptionBox2.getBox(Channels.newChannel(byteArrayOutputStream2));
            if (Arrays.equals(byteArrayOutputStream2.toByteArray(), byteArrayOutputStream.toByteArray())) {
                return sampleDescriptionBox;
            }
            SampleEntry mergeSampleEntry = mergeSampleEntry((SampleEntry) sampleDescriptionBox.getBoxes(SampleEntry.class).get(0), (SampleEntry) sampleDescriptionBox2.getBoxes(SampleEntry.class).get(0));
            if (mergeSampleEntry != null) {
                sampleDescriptionBox.setBoxes(Collections.singletonList(mergeSampleEntry));
                return sampleDescriptionBox;
            }
            throw new IOException("Cannot merge " + sampleDescriptionBox.getBoxes(SampleEntry.class).get(0) + " and " + sampleDescriptionBox2.getBoxes(SampleEntry.class).get(0));
        } catch (IOException e) {
            LOG.logError(e.getMessage());
            return null;
        }
    }

    private VisualSampleEntry mergeVisualSampleEntry(VisualSampleEntry visualSampleEntry, VisualSampleEntry visualSampleEntry2) {
        VisualSampleEntry visualSampleEntry3 = new VisualSampleEntry();
        if (visualSampleEntry.getHorizresolution() == visualSampleEntry2.getHorizresolution()) {
            visualSampleEntry3.setHorizresolution(visualSampleEntry.getHorizresolution());
            visualSampleEntry3.setCompressorname(visualSampleEntry.getCompressorname());
            if (visualSampleEntry.getDepth() == visualSampleEntry2.getDepth()) {
                visualSampleEntry3.setDepth(visualSampleEntry.getDepth());
                if (visualSampleEntry.getFrameCount() == visualSampleEntry2.getFrameCount()) {
                    visualSampleEntry3.setFrameCount(visualSampleEntry.getFrameCount());
                    if (visualSampleEntry.getHeight() == visualSampleEntry2.getHeight()) {
                        visualSampleEntry3.setHeight(visualSampleEntry.getHeight());
                        if (visualSampleEntry.getWidth() == visualSampleEntry2.getWidth()) {
                            visualSampleEntry3.setWidth(visualSampleEntry.getWidth());
                            if (visualSampleEntry.getVertresolution() == visualSampleEntry2.getVertresolution()) {
                                visualSampleEntry3.setVertresolution(visualSampleEntry.getVertresolution());
                                if (visualSampleEntry.getHorizresolution() == visualSampleEntry2.getHorizresolution()) {
                                    visualSampleEntry3.setHorizresolution(visualSampleEntry.getHorizresolution());
                                    if (visualSampleEntry.getBoxes().size() == visualSampleEntry2.getBoxes().size()) {
                                        Iterator it = visualSampleEntry2.getBoxes().iterator();
                                        for (Box box : visualSampleEntry.getBoxes()) {
                                            Box box2 = (Box) it.next();
                                            OutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                                            OutputStream byteArrayOutputStream2 = new ByteArrayOutputStream();
                                            try {
                                                box.getBox(Channels.newChannel(byteArrayOutputStream));
                                                box2.getBox(Channels.newChannel(byteArrayOutputStream2));
                                                if (Arrays.equals(byteArrayOutputStream.toByteArray(), byteArrayOutputStream2.toByteArray())) {
                                                    visualSampleEntry3.addBox(box);
                                                } else if ((box instanceof AbstractDescriptorBox) && (box2 instanceof AbstractDescriptorBox)) {
                                                    ((AbstractDescriptorBox) box).setDescriptor(mergeDescriptors(((AbstractDescriptorBox) box).getDescriptor(), ((AbstractDescriptorBox) box2).getDescriptor()));
                                                    visualSampleEntry3.addBox(box);
                                                }
                                            } catch (IOException e) {
                                                LOG.logWarn(e.getMessage());
                                                return null;
                                            }
                                        }
                                    }
                                    return visualSampleEntry3;
                                }
                                LOG.logError("horizontal resolution differs");
                                return null;
                            }
                            LOG.logError("vert resolution differs");
                            return null;
                        }
                        LOG.logError("width differs");
                        return null;
                    }
                    LOG.logError("height differs");
                    return null;
                }
                LOG.logError("frame count differs");
                return null;
            }
            LOG.logError("Depth differs");
            return null;
        }
        LOG.logError("Horizontal Resolution differs");
        return null;
    }

    public void close() {
        for (Track close : this.tracks) {
            close.close();
        }
    }

    public List<Entry> getCompositionTimeEntries() {
        if (this.tracks[0].getCompositionTimeEntries() == null || this.tracks[0].getCompositionTimeEntries().isEmpty()) {
            return null;
        }
        List<int[]> linkedList = new LinkedList();
        for (Track compositionTimeEntries : this.tracks) {
            linkedList.add(CompositionTimeToSample.blowupCompositionTimes(compositionTimeEntries.getCompositionTimeEntries()));
        }
        LinkedList linkedList2 = new LinkedList();
        for (int[] iArr : linkedList) {
            for (int i : (int[]) r5.next()) {
                if (linkedList2.isEmpty() || ((Entry) linkedList2.getLast()).getOffset() != i) {
                    linkedList2.add(new Entry(1, i));
                } else {
                    Entry entry = (Entry) linkedList2.getLast();
                    entry.setCount(entry.getCount() + 1);
                }
            }
        }
        return linkedList2;
    }

    public String getHandler() {
        return this.tracks[0].getHandler();
    }

    public List<SampleDependencyTypeBox.Entry> getSampleDependencies() {
        int i = 0;
        if (this.tracks[0].getSampleDependencies() == null || this.tracks[0].getSampleDependencies().isEmpty()) {
            return null;
        }
        List<SampleDependencyTypeBox.Entry> linkedList = new LinkedList();
        Track[] trackArr = this.tracks;
        int length = trackArr.length;
        while (i < length) {
            linkedList.addAll(trackArr[i].getSampleDependencies());
            i++;
        }
        return linkedList;
    }

    public SampleDescriptionBox getSampleDescriptionBox() {
        return this.stsd;
    }

    public synchronized long[] getSampleDurations() {
        long[] jArr;
        int i = 0;
        for (Track sampleDurations : this.tracks) {
            i += sampleDurations.getSampleDurations().length;
        }
        jArr = new long[i];
        i = 0;
        for (Track sampleDurations2 : this.tracks) {
            long[] sampleDurations3 = sampleDurations2.getSampleDurations();
            int length = sampleDurations3.length;
            int i2 = 0;
            while (i2 < length) {
                int i3 = i + 1;
                jArr[i] = sampleDurations3[i2];
                i2++;
                i = i3;
            }
        }
        return jArr;
    }

    public List<Sample> getSamples() {
        List arrayList = new ArrayList();
        for (Track samples : this.tracks) {
            arrayList.addAll(samples.getSamples());
        }
        return arrayList;
    }

    public SubSampleInformationBox getSubsampleInformationBox() {
        return this.tracks[0].getSubsampleInformationBox();
    }

    public long[] getSyncSamples() {
        if (this.tracks[0].getSyncSamples() == null || this.tracks[0].getSyncSamples().length <= 0) {
            return null;
        }
        int i = 0;
        for (Track syncSamples : this.tracks) {
            i += syncSamples.getSyncSamples().length;
        }
        long[] jArr = new long[i];
        long j = 0;
        int i2 = 0;
        for (Track track : this.tracks) {
            long[] syncSamples2 = track.getSyncSamples();
            int length = syncSamples2.length;
            i = 0;
            while (i < length) {
                int i3 = i2 + 1;
                jArr[i2] = syncSamples2[i] + j;
                i++;
                i2 = i3;
            }
            j += (long) track.getSamples().size();
        }
        return jArr;
    }

    public TrackMetaData getTrackMetaData() {
        return this.tracks[0].getTrackMetaData();
    }
}
