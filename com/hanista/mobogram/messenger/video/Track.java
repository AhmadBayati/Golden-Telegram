package com.hanista.mobogram.messenger.video;

import android.annotation.TargetApi;
import android.media.MediaCodec.BufferInfo;
import android.media.MediaFormat;
import com.coremedia.iso.boxes.AbstractMediaHeaderBox;
import com.coremedia.iso.boxes.Box;
import com.coremedia.iso.boxes.SampleDescriptionBox;
import com.coremedia.iso.boxes.SoundMediaHeaderBox;
import com.coremedia.iso.boxes.VideoMediaHeaderBox;
import com.coremedia.iso.boxes.sampleentry.AudioSampleEntry;
import com.coremedia.iso.boxes.sampleentry.VisualSampleEntry;
import com.googlecode.mp4parser.boxes.mp4.ESDescriptorBox;
import com.googlecode.mp4parser.boxes.mp4.objectdescriptors.AudioSpecificConfig;
import com.googlecode.mp4parser.boxes.mp4.objectdescriptors.DecoderConfigDescriptor;
import com.googlecode.mp4parser.boxes.mp4.objectdescriptors.ESDescriptor;
import com.googlecode.mp4parser.boxes.mp4.objectdescriptors.SLConfigDescriptor;
import com.hanista.mobogram.messenger.exoplayer.C0700C;
import com.hanista.mobogram.messenger.exoplayer.upstream.UdpDataSource;
import com.hanista.mobogram.messenger.exoplayer.util.MimeTypes;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;
import com.mp4parser.iso14496.part15.AvcConfigurationBox;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@TargetApi(16)
public class Track {
    private static Map<Integer, Integer> samplingFrequencyIndexMap;
    private Date creationTime;
    private long duration;
    private boolean first;
    private String handler;
    private AbstractMediaHeaderBox headerBox;
    private int height;
    private boolean isAudio;
    private long lastPresentationTimeUs;
    private SampleDescriptionBox sampleDescriptionBox;
    private ArrayList<Long> sampleDurations;
    private ArrayList<Sample> samples;
    private LinkedList<Integer> syncSamples;
    private int timeScale;
    private long trackId;
    private float volume;
    private int width;

    static {
        samplingFrequencyIndexMap = new HashMap();
        samplingFrequencyIndexMap.put(Integer.valueOf(96000), Integer.valueOf(0));
        samplingFrequencyIndexMap.put(Integer.valueOf(88200), Integer.valueOf(1));
        samplingFrequencyIndexMap.put(Integer.valueOf(64000), Integer.valueOf(2));
        samplingFrequencyIndexMap.put(Integer.valueOf(48000), Integer.valueOf(3));
        samplingFrequencyIndexMap.put(Integer.valueOf(44100), Integer.valueOf(4));
        samplingFrequencyIndexMap.put(Integer.valueOf(32000), Integer.valueOf(5));
        samplingFrequencyIndexMap.put(Integer.valueOf(24000), Integer.valueOf(6));
        samplingFrequencyIndexMap.put(Integer.valueOf(22050), Integer.valueOf(7));
        samplingFrequencyIndexMap.put(Integer.valueOf(16000), Integer.valueOf(8));
        samplingFrequencyIndexMap.put(Integer.valueOf(12000), Integer.valueOf(9));
        samplingFrequencyIndexMap.put(Integer.valueOf(11025), Integer.valueOf(10));
        samplingFrequencyIndexMap.put(Integer.valueOf(UdpDataSource.DEAFULT_SOCKET_TIMEOUT_MILLIS), Integer.valueOf(11));
    }

    public Track(int i, MediaFormat mediaFormat, boolean z) {
        this.trackId = 0;
        this.samples = new ArrayList();
        this.duration = 0;
        this.headerBox = null;
        this.sampleDescriptionBox = null;
        this.syncSamples = null;
        this.creationTime = new Date();
        this.volume = 0.0f;
        this.sampleDurations = new ArrayList();
        this.isAudio = false;
        this.lastPresentationTimeUs = 0;
        this.first = true;
        this.trackId = (long) i;
        this.isAudio = z;
        if (this.isAudio) {
            this.sampleDurations.add(Long.valueOf(1024));
            this.duration = 1024;
            this.volume = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
            this.timeScale = mediaFormat.getInteger("sample-rate");
            this.handler = "soun";
            this.headerBox = new SoundMediaHeaderBox();
            this.sampleDescriptionBox = new SampleDescriptionBox();
            Box audioSampleEntry = new AudioSampleEntry(AudioSampleEntry.TYPE3);
            audioSampleEntry.setChannelCount(mediaFormat.getInteger("channel-count"));
            audioSampleEntry.setSampleRate((long) mediaFormat.getInteger("sample-rate"));
            audioSampleEntry.setDataReferenceIndex(1);
            audioSampleEntry.setSampleSize(16);
            Box eSDescriptorBox = new ESDescriptorBox();
            ESDescriptor eSDescriptor = new ESDescriptor();
            eSDescriptor.setEsId(0);
            SLConfigDescriptor sLConfigDescriptor = new SLConfigDescriptor();
            sLConfigDescriptor.setPredefined(2);
            eSDescriptor.setSlConfigDescriptor(sLConfigDescriptor);
            DecoderConfigDescriptor decoderConfigDescriptor = new DecoderConfigDescriptor();
            decoderConfigDescriptor.setObjectTypeIndication(64);
            decoderConfigDescriptor.setStreamType(5);
            decoderConfigDescriptor.setBufferSizeDB(1536);
            decoderConfigDescriptor.setMaxBitRate(96000);
            decoderConfigDescriptor.setAvgBitRate(96000);
            AudioSpecificConfig audioSpecificConfig = new AudioSpecificConfig();
            audioSpecificConfig.setAudioObjectType(2);
            audioSpecificConfig.setSamplingFrequencyIndex(((Integer) samplingFrequencyIndexMap.get(Integer.valueOf((int) audioSampleEntry.getSampleRate()))).intValue());
            audioSpecificConfig.setChannelConfiguration(audioSampleEntry.getChannelCount());
            decoderConfigDescriptor.setAudioSpecificInfo(audioSpecificConfig);
            eSDescriptor.setDecoderConfigDescriptor(decoderConfigDescriptor);
            ByteBuffer serialize = eSDescriptor.serialize();
            eSDescriptorBox.setEsDescriptor(eSDescriptor);
            eSDescriptorBox.setData(serialize);
            audioSampleEntry.addBox(eSDescriptorBox);
            this.sampleDescriptionBox.addBox(audioSampleEntry);
            return;
        }
        this.sampleDurations.add(Long.valueOf(3015));
        this.duration = 3015;
        this.width = mediaFormat.getInteger("width");
        this.height = mediaFormat.getInteger("height");
        this.timeScale = 90000;
        this.syncSamples = new LinkedList();
        this.handler = "vide";
        this.headerBox = new VideoMediaHeaderBox();
        this.sampleDescriptionBox = new SampleDescriptionBox();
        String string = mediaFormat.getString("mime");
        Box visualSampleEntry;
        if (string.equals(MimeTypes.VIDEO_H264)) {
            visualSampleEntry = new VisualSampleEntry(VisualSampleEntry.TYPE3);
            visualSampleEntry.setDataReferenceIndex(1);
            visualSampleEntry.setDepth(24);
            visualSampleEntry.setFrameCount(1);
            visualSampleEntry.setHorizresolution(72.0d);
            visualSampleEntry.setVertresolution(72.0d);
            visualSampleEntry.setWidth(this.width);
            visualSampleEntry.setHeight(this.height);
            audioSampleEntry = new AvcConfigurationBox();
            if (mediaFormat.getByteBuffer("csd-0") != null) {
                List arrayList = new ArrayList();
                ByteBuffer byteBuffer = mediaFormat.getByteBuffer("csd-0");
                byteBuffer.position(4);
                Object obj = new byte[byteBuffer.remaining()];
                byteBuffer.get(obj);
                arrayList.add(obj);
                List arrayList2 = new ArrayList();
                ByteBuffer byteBuffer2 = mediaFormat.getByteBuffer("csd-1");
                byteBuffer2.position(4);
                Object obj2 = new byte[byteBuffer2.remaining()];
                byteBuffer2.get(obj2);
                arrayList2.add(obj2);
                audioSampleEntry.setSequenceParameterSets(arrayList);
                audioSampleEntry.setPictureParameterSets(arrayList2);
            }
            audioSampleEntry.setAvcLevelIndication(13);
            audioSampleEntry.setAvcProfileIndication(100);
            audioSampleEntry.setBitDepthLumaMinus8(-1);
            audioSampleEntry.setBitDepthChromaMinus8(-1);
            audioSampleEntry.setChromaFormat(-1);
            audioSampleEntry.setConfigurationVersion(1);
            audioSampleEntry.setLengthSizeMinusOne(3);
            audioSampleEntry.setProfileCompatibility(0);
            visualSampleEntry.addBox(audioSampleEntry);
            this.sampleDescriptionBox.addBox(visualSampleEntry);
        } else if (string.equals("video/mp4v")) {
            visualSampleEntry = new VisualSampleEntry(VisualSampleEntry.TYPE1);
            visualSampleEntry.setDataReferenceIndex(1);
            visualSampleEntry.setDepth(24);
            visualSampleEntry.setFrameCount(1);
            visualSampleEntry.setHorizresolution(72.0d);
            visualSampleEntry.setVertresolution(72.0d);
            visualSampleEntry.setWidth(this.width);
            visualSampleEntry.setHeight(this.height);
            this.sampleDescriptionBox.addBox(visualSampleEntry);
        }
    }

    public void addSample(long j, BufferInfo bufferInfo) {
        long j2 = bufferInfo.presentationTimeUs - this.lastPresentationTimeUs;
        if (j2 >= 0) {
            boolean z = (this.isAudio || (bufferInfo.flags & 1) == 0) ? false : true;
            this.samples.add(new Sample(j, (long) bufferInfo.size));
            if (this.syncSamples != null && z) {
                this.syncSamples.add(Integer.valueOf(this.samples.size()));
            }
            j2 = ((j2 * ((long) this.timeScale)) + 500000) / C0700C.MICROS_PER_SECOND;
            this.lastPresentationTimeUs = bufferInfo.presentationTimeUs;
            if (!this.first) {
                this.sampleDurations.add(this.sampleDurations.size() - 1, Long.valueOf(j2));
                this.duration = j2 + this.duration;
            }
            this.first = false;
        }
    }

    public Date getCreationTime() {
        return this.creationTime;
    }

    public long getDuration() {
        return this.duration;
    }

    public String getHandler() {
        return this.handler;
    }

    public int getHeight() {
        return this.height;
    }

    public AbstractMediaHeaderBox getMediaHeaderBox() {
        return this.headerBox;
    }

    public SampleDescriptionBox getSampleDescriptionBox() {
        return this.sampleDescriptionBox;
    }

    public ArrayList<Long> getSampleDurations() {
        return this.sampleDurations;
    }

    public ArrayList<Sample> getSamples() {
        return this.samples;
    }

    public long[] getSyncSamples() {
        if (this.syncSamples == null || this.syncSamples.isEmpty()) {
            return null;
        }
        long[] jArr = new long[this.syncSamples.size()];
        for (int i = 0; i < this.syncSamples.size(); i++) {
            jArr[i] = (long) ((Integer) this.syncSamples.get(i)).intValue();
        }
        return jArr;
    }

    public int getTimeScale() {
        return this.timeScale;
    }

    public long getTrackId() {
        return this.trackId;
    }

    public float getVolume() {
        return this.volume;
    }

    public int getWidth() {
        return this.width;
    }

    public boolean isAudio() {
        return this.isAudio;
    }
}
