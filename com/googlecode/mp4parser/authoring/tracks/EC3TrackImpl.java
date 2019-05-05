package com.googlecode.mp4parser.authoring.tracks;

import android.support.v4.view.PointerIconCompat;
import com.coremedia.iso.boxes.Box;
import com.coremedia.iso.boxes.CompositionTimeToSample;
import com.coremedia.iso.boxes.SampleDependencyTypeBox;
import com.coremedia.iso.boxes.SampleDescriptionBox;
import com.coremedia.iso.boxes.SubSampleInformationBox;
import com.coremedia.iso.boxes.sampleentry.AudioSampleEntry;
import com.googlecode.mp4parser.DataSource;
import com.googlecode.mp4parser.authoring.AbstractTrack;
import com.googlecode.mp4parser.authoring.Sample;
import com.googlecode.mp4parser.authoring.TrackMetaData;
import com.googlecode.mp4parser.boxes.EC3SpecificBox;
import com.googlecode.mp4parser.boxes.EC3SpecificBox.Entry;
import com.googlecode.mp4parser.boxes.mp4.objectdescriptors.BitReaderBuffer;
import com.googlecode.mp4parser.util.CastUtils;
import com.hanista.mobogram.messenger.exoplayer.util.NalUnitUtil;
import com.hanista.mobogram.messenger.support.widget.helper.ItemTouchHelper.Callback;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;
import com.hanista.mobogram.tgnet.TLRPC;
import com.hanista.mobogram.ui.Components.VideoPlayer;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class EC3TrackImpl extends AbstractTrack {
    private static final long MAX_FRAMES_PER_MMAP = 20;
    private List<BitStreamInfo> bitStreamInfos;
    private int bitrate;
    private final DataSource dataSource;
    private long[] decodingTimes;
    private int frameSize;
    SampleDescriptionBox sampleDescriptionBox;
    private List<Sample> samples;
    TrackMetaData trackMetaData;

    /* renamed from: com.googlecode.mp4parser.authoring.tracks.EC3TrackImpl.1 */
    class C03281 implements Sample {
        private final /* synthetic */ int val$start;

        C03281(int i) {
            this.val$start = i;
        }

        public ByteBuffer asByteBuffer() {
            try {
                return EC3TrackImpl.this.dataSource.map((long) this.val$start, (long) EC3TrackImpl.this.frameSize);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }

        public long getSize() {
            return (long) EC3TrackImpl.this.frameSize;
        }

        public void writeTo(WritableByteChannel writableByteChannel) {
            EC3TrackImpl.this.dataSource.transferTo((long) this.val$start, (long) EC3TrackImpl.this.frameSize, writableByteChannel);
        }
    }

    public static class BitStreamInfo extends Entry {
        public int bitrate;
        public int chanmap;
        public int frameSize;
        public int samplerate;
        public int strmtyp;
        public int substreamid;

        public String toString() {
            return "BitStreamInfo{frameSize=" + this.frameSize + ", substreamid=" + this.substreamid + ", bitrate=" + this.bitrate + ", samplerate=" + this.samplerate + ", strmtyp=" + this.strmtyp + ", chanmap=" + this.chanmap + '}';
        }
    }

    public EC3TrackImpl(DataSource dataSource) {
        int i;
        super(dataSource.toString());
        this.trackMetaData = new TrackMetaData();
        this.bitStreamInfos = new LinkedList();
        this.dataSource = dataSource;
        for (int i2 = 0; i2 == 0; i2 = i) {
            BitStreamInfo readVariables = readVariables();
            if (readVariables == null) {
                throw new IOException();
            }
            i = i2;
            for (BitStreamInfo bitStreamInfo : this.bitStreamInfos) {
                if (readVariables.strmtyp != 1 && bitStreamInfo.substreamid == readVariables.substreamid) {
                    i = 1;
                }
            }
            if (i == 0) {
                this.bitStreamInfos.add(readVariables);
            }
        }
        if (this.bitStreamInfos.size() == 0) {
            throw new IOException();
        }
        i = ((BitStreamInfo) this.bitStreamInfos.get(0)).samplerate;
        this.sampleDescriptionBox = new SampleDescriptionBox();
        Box audioSampleEntry = new AudioSampleEntry(AudioSampleEntry.TYPE9);
        audioSampleEntry.setChannelCount(2);
        audioSampleEntry.setSampleRate((long) i);
        audioSampleEntry.setDataReferenceIndex(1);
        audioSampleEntry.setSampleSize(16);
        Box eC3SpecificBox = new EC3SpecificBox();
        int[] iArr = new int[this.bitStreamInfos.size()];
        int[] iArr2 = new int[this.bitStreamInfos.size()];
        for (BitStreamInfo bitStreamInfo2 : this.bitStreamInfos) {
            if (bitStreamInfo2.strmtyp == 1) {
                int i3 = bitStreamInfo2.substreamid;
                iArr[i3] = iArr[i3] + 1;
                iArr2[bitStreamInfo2.substreamid] = ((bitStreamInfo2.chanmap >> 5) & NalUnitUtil.EXTENDED_SAR) | ((bitStreamInfo2.chanmap >> 6) & TLRPC.USER_FLAG_UNUSED2);
            }
        }
        for (BitStreamInfo bitStreamInfo22 : this.bitStreamInfos) {
            if (bitStreamInfo22.strmtyp != 1) {
                Entry entry = new Entry();
                entry.fscod = bitStreamInfo22.fscod;
                entry.bsid = bitStreamInfo22.bsid;
                entry.bsmod = bitStreamInfo22.bsmod;
                entry.acmod = bitStreamInfo22.acmod;
                entry.lfeon = bitStreamInfo22.lfeon;
                entry.reserved = 0;
                entry.num_dep_sub = iArr[bitStreamInfo22.substreamid];
                entry.chan_loc = iArr2[bitStreamInfo22.substreamid];
                entry.reserved2 = 0;
                eC3SpecificBox.addEntry(entry);
            }
            this.bitrate += bitStreamInfo22.bitrate;
            this.frameSize = bitStreamInfo22.frameSize + this.frameSize;
        }
        eC3SpecificBox.setDataRate(this.bitrate / PointerIconCompat.TYPE_DEFAULT);
        audioSampleEntry.addBox(eC3SpecificBox);
        this.sampleDescriptionBox.addBox(audioSampleEntry);
        this.trackMetaData.setCreationTime(new Date());
        this.trackMetaData.setModificationTime(new Date());
        this.trackMetaData.setTimescale((long) i);
        this.trackMetaData.setVolume(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        dataSource.position(0);
        this.samples = readSamples();
        this.decodingTimes = new long[this.samples.size()];
        Arrays.fill(this.decodingTimes, 1536);
    }

    private List<Sample> readSamples() {
        int l2i = CastUtils.l2i((this.dataSource.size() - this.dataSource.position()) / ((long) this.frameSize));
        List<Sample> arrayList = new ArrayList(l2i);
        for (int i = 0; i < l2i; i++) {
            arrayList.add(new C03281(this.frameSize * i));
        }
        return arrayList;
    }

    private BitStreamInfo readVariables() {
        long position = this.dataSource.position();
        ByteBuffer allocate = ByteBuffer.allocate(Callback.DEFAULT_DRAG_ANIMATION_DURATION);
        this.dataSource.read(allocate);
        allocate.rewind();
        BitReaderBuffer bitReaderBuffer = new BitReaderBuffer(allocate);
        if (bitReaderBuffer.readBits(16) != 2935) {
            return null;
        }
        int i;
        int readBits;
        BitStreamInfo bitStreamInfo = new BitStreamInfo();
        bitStreamInfo.strmtyp = bitReaderBuffer.readBits(2);
        bitStreamInfo.substreamid = bitReaderBuffer.readBits(3);
        bitStreamInfo.frameSize = (bitReaderBuffer.readBits(11) + 1) * 2;
        bitStreamInfo.fscod = bitReaderBuffer.readBits(2);
        if (bitStreamInfo.fscod == 3) {
            i = 3;
            readBits = bitReaderBuffer.readBits(2);
        } else {
            i = bitReaderBuffer.readBits(2);
            readBits = -1;
        }
        int i2 = 0;
        switch (i) {
            case VideoPlayer.TRACK_DEFAULT /*0*/:
                i2 = 1;
                break;
            case VideoPlayer.TYPE_AUDIO /*1*/:
                i2 = 2;
                break;
            case VideoPlayer.STATE_PREPARING /*2*/:
                i2 = 3;
                break;
            case VideoPlayer.STATE_BUFFERING /*3*/:
                i2 = 6;
                break;
        }
        bitStreamInfo.frameSize *= 6 / i2;
        bitStreamInfo.acmod = bitReaderBuffer.readBits(3);
        bitStreamInfo.lfeon = bitReaderBuffer.readBits(1);
        bitStreamInfo.bsid = bitReaderBuffer.readBits(5);
        bitReaderBuffer.readBits(5);
        if (1 == bitReaderBuffer.readBits(1)) {
            bitReaderBuffer.readBits(8);
        }
        if (bitStreamInfo.acmod == 0) {
            bitReaderBuffer.readBits(5);
            if (1 == bitReaderBuffer.readBits(1)) {
                bitReaderBuffer.readBits(8);
            }
        }
        if (1 == bitStreamInfo.strmtyp && 1 == bitReaderBuffer.readBits(1)) {
            bitStreamInfo.chanmap = bitReaderBuffer.readBits(16);
        }
        if (1 == bitReaderBuffer.readBits(1)) {
            if (bitStreamInfo.acmod > 2) {
                bitReaderBuffer.readBits(2);
            }
            if (1 == (bitStreamInfo.acmod & 1) && bitStreamInfo.acmod > 2) {
                bitReaderBuffer.readBits(3);
                bitReaderBuffer.readBits(3);
            }
            if ((bitStreamInfo.acmod & 4) > 0) {
                bitReaderBuffer.readBits(3);
                bitReaderBuffer.readBits(3);
            }
            if (1 == bitStreamInfo.lfeon && 1 == bitReaderBuffer.readBits(1)) {
                bitReaderBuffer.readBits(5);
            }
            if (bitStreamInfo.strmtyp == 0) {
                if (1 == bitReaderBuffer.readBits(1)) {
                    bitReaderBuffer.readBits(6);
                }
                if (bitStreamInfo.acmod == 0 && 1 == bitReaderBuffer.readBits(1)) {
                    bitReaderBuffer.readBits(6);
                }
                if (1 == bitReaderBuffer.readBits(1)) {
                    bitReaderBuffer.readBits(6);
                }
                int readBits2 = bitReaderBuffer.readBits(2);
                if (1 == readBits2) {
                    bitReaderBuffer.readBits(5);
                } else if (2 == readBits2) {
                    bitReaderBuffer.readBits(12);
                } else if (3 == readBits2) {
                    int readBits3 = bitReaderBuffer.readBits(5);
                    if (1 == bitReaderBuffer.readBits(1)) {
                        bitReaderBuffer.readBits(5);
                        if (1 == bitReaderBuffer.readBits(1)) {
                            bitReaderBuffer.readBits(4);
                        }
                        if (1 == bitReaderBuffer.readBits(1)) {
                            bitReaderBuffer.readBits(4);
                        }
                        if (1 == bitReaderBuffer.readBits(1)) {
                            bitReaderBuffer.readBits(4);
                        }
                        if (1 == bitReaderBuffer.readBits(1)) {
                            bitReaderBuffer.readBits(4);
                        }
                        if (1 == bitReaderBuffer.readBits(1)) {
                            bitReaderBuffer.readBits(4);
                        }
                        if (1 == bitReaderBuffer.readBits(1)) {
                            bitReaderBuffer.readBits(4);
                        }
                        if (1 == bitReaderBuffer.readBits(1)) {
                            bitReaderBuffer.readBits(4);
                        }
                        if (1 == bitReaderBuffer.readBits(1)) {
                            if (1 == bitReaderBuffer.readBits(1)) {
                                bitReaderBuffer.readBits(4);
                            }
                            if (1 == bitReaderBuffer.readBits(1)) {
                                bitReaderBuffer.readBits(4);
                            }
                        }
                    }
                    if (1 == bitReaderBuffer.readBits(1)) {
                        bitReaderBuffer.readBits(5);
                        if (1 == bitReaderBuffer.readBits(1)) {
                            bitReaderBuffer.readBits(7);
                            if (1 == bitReaderBuffer.readBits(1)) {
                                bitReaderBuffer.readBits(8);
                            }
                        }
                    }
                    for (readBits2 = 0; readBits2 < readBits3 + 2; readBits2++) {
                        bitReaderBuffer.readBits(8);
                    }
                    bitReaderBuffer.byteSync();
                }
                if (bitStreamInfo.acmod < 2) {
                    if (1 == bitReaderBuffer.readBits(1)) {
                        bitReaderBuffer.readBits(14);
                    }
                    if (bitStreamInfo.acmod == 0 && 1 == bitReaderBuffer.readBits(1)) {
                        bitReaderBuffer.readBits(14);
                    }
                    if (1 == bitReaderBuffer.readBits(1)) {
                        if (i == 0) {
                            bitReaderBuffer.readBits(5);
                        } else {
                            for (int i3 = 0; i3 < i2; i3++) {
                                if (1 == bitReaderBuffer.readBits(1)) {
                                    bitReaderBuffer.readBits(5);
                                }
                            }
                        }
                    }
                }
            }
        }
        if (1 == bitReaderBuffer.readBits(1)) {
            bitStreamInfo.bsmod = bitReaderBuffer.readBits(3);
        }
        switch (bitStreamInfo.fscod) {
            case VideoPlayer.TRACK_DEFAULT /*0*/:
                bitStreamInfo.samplerate = 48000;
                break;
            case VideoPlayer.TYPE_AUDIO /*1*/:
                bitStreamInfo.samplerate = 44100;
                break;
            case VideoPlayer.STATE_PREPARING /*2*/:
                bitStreamInfo.samplerate = 32000;
                break;
            case VideoPlayer.STATE_BUFFERING /*3*/:
                switch (readBits) {
                    case VideoPlayer.TRACK_DEFAULT /*0*/:
                        bitStreamInfo.samplerate = 24000;
                        break;
                    case VideoPlayer.TYPE_AUDIO /*1*/:
                        bitStreamInfo.samplerate = 22050;
                        break;
                    case VideoPlayer.STATE_PREPARING /*2*/:
                        bitStreamInfo.samplerate = 16000;
                        break;
                    case VideoPlayer.STATE_BUFFERING /*3*/:
                        bitStreamInfo.samplerate = 0;
                        break;
                    default:
                        break;
                }
        }
        if (bitStreamInfo.samplerate == 0) {
            return null;
        }
        bitStreamInfo.bitrate = (int) (((((double) bitStreamInfo.samplerate) / 1536.0d) * ((double) bitStreamInfo.frameSize)) * 8.0d);
        this.dataSource.position(((long) bitStreamInfo.frameSize) + position);
        return bitStreamInfo;
    }

    public void close() {
        this.dataSource.close();
    }

    public List<CompositionTimeToSample.Entry> getCompositionTimeEntries() {
        return null;
    }

    public String getHandler() {
        return "soun";
    }

    public List<SampleDependencyTypeBox.Entry> getSampleDependencies() {
        return null;
    }

    public SampleDescriptionBox getSampleDescriptionBox() {
        return this.sampleDescriptionBox;
    }

    public long[] getSampleDurations() {
        return this.decodingTimes;
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
        return this.trackMetaData;
    }

    public String toString() {
        return "EC3TrackImpl{bitrate=" + this.bitrate + ", bitStreamInfos=" + this.bitStreamInfos + '}';
    }
}
