package com.googlecode.mp4parser.authoring.tracks;

import android.support.v4.internal.view.SupportMenu;
import com.coremedia.iso.boxes.Box;
import com.coremedia.iso.boxes.CompositionTimeToSample.Entry;
import com.coremedia.iso.boxes.SampleDependencyTypeBox;
import com.coremedia.iso.boxes.SampleDescriptionBox;
import com.coremedia.iso.boxes.sampleentry.AudioSampleEntry;
import com.googlecode.mp4parser.DataSource;
import com.googlecode.mp4parser.authoring.AbstractTrack;
import com.googlecode.mp4parser.authoring.Sample;
import com.googlecode.mp4parser.authoring.TrackMetaData;
import com.googlecode.mp4parser.boxes.DTSSpecificBox;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.exoplayer.extractor.ts.PsExtractor;
import com.hanista.mobogram.messenger.exoplayer.upstream.NetworkLock;
import com.hanista.mobogram.messenger.exoplayer.upstream.UdpDataSource;
import com.hanista.mobogram.messenger.volley.Request.Method;
import com.hanista.mobogram.tgnet.TLRPC;
import com.hanista.mobogram.ui.Components.VideoPlayer;
import com.hanista.mobogram.util.shamsicalendar.ShamsiCalendar;
import java.io.EOFException;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DTSTrackImpl extends AbstractTrack {
    private static final int BUFFER = 67108864;
    int bcCoreBitRate;
    int bcCoreChannelMask;
    int bcCoreMaxSampleRate;
    int bitrate;
    int channelCount;
    int channelMask;
    int codecDelayAtMaxFs;
    int coreBitRate;
    int coreChannelMask;
    int coreFramePayloadInBytes;
    int coreMaxSampleRate;
    boolean coreSubStreamPresent;
    private int dataOffset;
    private DataSource dataSource;
    DTSSpecificBox ddts;
    int extAvgBitrate;
    int extFramePayloadInBytes;
    int extPeakBitrate;
    int extSmoothBuffSize;
    boolean extensionSubStreamPresent;
    int frameSize;
    boolean isVBR;
    private String lang;
    int lbrCodingPresent;
    int lsbTrimPercent;
    int maxSampleRate;
    int numExtSubStreams;
    int numFramesTotal;
    int numSamplesOrigAudioAtMaxFs;
    SampleDescriptionBox sampleDescriptionBox;
    private long[] sampleDurations;
    int sampleSize;
    int samplerate;
    private List<Sample> samples;
    int samplesPerFrame;
    int samplesPerFrameAtMaxFs;
    TrackMetaData trackMetaData;
    String type;

    /* renamed from: com.googlecode.mp4parser.authoring.tracks.DTSTrackImpl.1 */
    class C03271 implements Sample {
        private final /* synthetic */ ByteBuffer val$finalSample;

        C03271(ByteBuffer byteBuffer) {
            this.val$finalSample = byteBuffer;
        }

        public ByteBuffer asByteBuffer() {
            return this.val$finalSample;
        }

        public long getSize() {
            return (long) this.val$finalSample.rewind().remaining();
        }

        public void writeTo(WritableByteChannel writableByteChannel) {
            writableByteChannel.write((ByteBuffer) this.val$finalSample.rewind());
        }
    }

    class LookAhead {
        ByteBuffer buffer;
        long bufferStartPos;
        private final int corePresent;
        long dataEnd;
        DataSource dataSource;
        int inBufferPos;
        long start;

        LookAhead(DataSource dataSource, long j, long j2, int i) {
            this.inBufferPos = 0;
            this.dataSource = dataSource;
            this.bufferStartPos = j;
            this.dataEnd = j2 + j;
            this.corePresent = i;
            fillBuffer();
        }

        private void discardByte() {
            this.inBufferPos++;
        }

        private void discardNext4AndMarkStart() {
            this.start = this.bufferStartPos + ((long) this.inBufferPos);
            this.inBufferPos += 4;
        }

        private void discardQWord() {
            this.inBufferPos += 4;
        }

        private void fillBuffer() {
            System.err.println("Fill Buffer");
            this.buffer = this.dataSource.map(this.bufferStartPos, Math.min(this.dataEnd - this.bufferStartPos, 67108864));
        }

        private ByteBuffer getSample() {
            if (this.start >= this.bufferStartPos) {
                this.buffer.position((int) (this.start - this.bufferStartPos));
                Buffer slice = this.buffer.slice();
                slice.limit((int) (((long) this.inBufferPos) - (this.start - this.bufferStartPos)));
                return (ByteBuffer) slice;
            }
            throw new RuntimeException("damn! NAL exceeds buffer");
        }

        private boolean nextFourEquals(byte b, byte b2, byte b3, byte b4) {
            if (this.buffer.limit() - this.inBufferPos >= 4) {
                return this.buffer.get(this.inBufferPos) == b && this.buffer.get(this.inBufferPos + 1) == b2 && this.buffer.get(this.inBufferPos + 2) == b3 && this.buffer.get(this.inBufferPos + 3) == b4;
            } else {
                if ((this.bufferStartPos + ((long) this.inBufferPos)) + 4 < this.dataSource.size()) {
                    return false;
                }
                throw new EOFException();
            }
        }

        private boolean nextFourEquals0x64582025() {
            return nextFourEquals((byte) 100, (byte) 88, ClosedCaptionCtrl.RESUME_CAPTION_LOADING, ClosedCaptionCtrl.ROLL_UP_CAPTIONS_2_ROWS);
        }

        private boolean nextFourEquals0x64582025orEof() {
            return nextFourEqualsOrEof((byte) 100, (byte) 88, ClosedCaptionCtrl.RESUME_CAPTION_LOADING, ClosedCaptionCtrl.ROLL_UP_CAPTIONS_2_ROWS);
        }

        private boolean nextFourEquals0x7FFE8001() {
            return nextFourEquals(Byte.MAX_VALUE, (byte) -2, Byte.MIN_VALUE, (byte) 1);
        }

        private boolean nextFourEquals0x7FFE8001orEof() {
            return nextFourEqualsOrEof(Byte.MAX_VALUE, (byte) -2, Byte.MIN_VALUE, (byte) 1);
        }

        private boolean nextFourEqualsOrEof(byte b, byte b2, byte b3, byte b4) {
            if (this.buffer.limit() - this.inBufferPos >= 4) {
                if ((this.bufferStartPos + ((long) this.inBufferPos)) % 1048576 == 0) {
                    System.err.println((((this.bufferStartPos + ((long) this.inBufferPos)) / 1024) / 1024));
                }
                return this.buffer.get(this.inBufferPos) == b && this.buffer.get(this.inBufferPos + 1) == b2 && this.buffer.get(this.inBufferPos + 2) == b3 && this.buffer.get(this.inBufferPos + 3) == b4;
            } else if ((this.bufferStartPos + ((long) this.inBufferPos)) + 4 > this.dataEnd) {
                return this.bufferStartPos + ((long) this.inBufferPos) == this.dataEnd;
            } else {
                this.bufferStartPos = this.start;
                this.inBufferPos = 0;
                fillBuffer();
                return nextFourEquals0x7FFE8001();
            }
        }

        public ByteBuffer findNextStart() {
            while (true) {
                try {
                    if (this.corePresent == 1) {
                        if (nextFourEquals0x7FFE8001()) {
                            break;
                        }
                        discardByte();
                    } else if (nextFourEquals0x64582025()) {
                        break;
                    } else {
                        discardByte();
                    }
                } catch (EOFException e) {
                    return null;
                }
            }
            discardNext4AndMarkStart();
            while (true) {
                if (this.corePresent == 1) {
                    if (nextFourEquals0x7FFE8001orEof()) {
                        break;
                    }
                    discardQWord();
                } else if (nextFourEquals0x64582025orEof()) {
                    break;
                } else {
                    discardQWord();
                }
            }
            return getSample();
        }
    }

    public DTSTrackImpl(DataSource dataSource) {
        super(dataSource.toString());
        this.trackMetaData = new TrackMetaData();
        this.frameSize = 0;
        this.dataOffset = 0;
        this.ddts = new DTSSpecificBox();
        this.isVBR = false;
        this.coreSubStreamPresent = false;
        this.extensionSubStreamPresent = false;
        this.numExtSubStreams = 0;
        this.coreMaxSampleRate = 0;
        this.coreBitRate = 0;
        this.coreChannelMask = 0;
        this.coreFramePayloadInBytes = 0;
        this.extAvgBitrate = 0;
        this.extPeakBitrate = 0;
        this.extSmoothBuffSize = 0;
        this.extFramePayloadInBytes = 0;
        this.maxSampleRate = 0;
        this.lbrCodingPresent = 0;
        this.numFramesTotal = 0;
        this.samplesPerFrameAtMaxFs = 0;
        this.numSamplesOrigAudioAtMaxFs = 0;
        this.channelMask = 0;
        this.codecDelayAtMaxFs = 0;
        this.bcCoreMaxSampleRate = 0;
        this.bcCoreBitRate = 0;
        this.bcCoreChannelMask = 0;
        this.lsbTrimPercent = 0;
        this.type = "none";
        this.lang = "eng";
        this.dataSource = dataSource;
        parse();
    }

    public DTSTrackImpl(DataSource dataSource, String str) {
        super(dataSource.toString());
        this.trackMetaData = new TrackMetaData();
        this.frameSize = 0;
        this.dataOffset = 0;
        this.ddts = new DTSSpecificBox();
        this.isVBR = false;
        this.coreSubStreamPresent = false;
        this.extensionSubStreamPresent = false;
        this.numExtSubStreams = 0;
        this.coreMaxSampleRate = 0;
        this.coreBitRate = 0;
        this.coreChannelMask = 0;
        this.coreFramePayloadInBytes = 0;
        this.extAvgBitrate = 0;
        this.extPeakBitrate = 0;
        this.extSmoothBuffSize = 0;
        this.extFramePayloadInBytes = 0;
        this.maxSampleRate = 0;
        this.lbrCodingPresent = 0;
        this.numFramesTotal = 0;
        this.samplesPerFrameAtMaxFs = 0;
        this.numSamplesOrigAudioAtMaxFs = 0;
        this.channelMask = 0;
        this.codecDelayAtMaxFs = 0;
        this.bcCoreMaxSampleRate = 0;
        this.bcCoreBitRate = 0;
        this.bcCoreChannelMask = 0;
        this.lsbTrimPercent = 0;
        this.type = "none";
        this.lang = "eng";
        this.lang = str;
        this.dataSource = dataSource;
        parse();
    }

    private List<Sample> generateSamples(DataSource dataSource, int i, long j, int i2) {
        LookAhead lookAhead = new LookAhead(dataSource, (long) i, j, i2);
        List<Sample> arrayList = new ArrayList();
        while (true) {
            ByteBuffer findNextStart = lookAhead.findNextStart();
            if (findNextStart == null) {
                System.err.println("all samples found");
                return arrayList;
            }
            arrayList.add(new C03271(findNextStart));
        }
    }

    private int getBitRate(int i) {
        switch (i) {
            case VideoPlayer.TRACK_DEFAULT /*0*/:
                return 32;
            case VideoPlayer.TYPE_AUDIO /*1*/:
                return 56;
            case VideoPlayer.STATE_PREPARING /*2*/:
                return 64;
            case VideoPlayer.STATE_BUFFERING /*3*/:
                return 96;
            case VideoPlayer.STATE_READY /*4*/:
                return 112;
            case VideoPlayer.STATE_ENDED /*5*/:
                return TLRPC.USER_FLAG_UNUSED;
            case Method.TRACE /*6*/:
                return PsExtractor.AUDIO_STREAM;
            case Method.PATCH /*7*/:
                return PsExtractor.VIDEO_STREAM;
            case TLRPC.USER_FLAG_USERNAME /*8*/:
                return TLRPC.USER_FLAG_UNUSED2;
            case C0338R.styleable.PromptView_iconTint /*9*/:
                return 320;
            case NetworkLock.DOWNLOAD_PRIORITY /*10*/:
                return 384;
            case C0338R.styleable.PromptView_maxTextWidth /*11*/:
                return 448;
            case Atom.FULL_HEADER_SIZE /*12*/:
                return TLRPC.USER_FLAG_UNUSED3;
            case ShamsiCalendar.CURRENT_CENTURY /*13*/:
                return 576;
            case C0338R.styleable.PromptView_primaryTextFontFamily /*14*/:
                return 640;
            case C0338R.styleable.PromptView_primaryTextSize /*15*/:
                return 768;
            case TLRPC.USER_FLAG_PHONE /*16*/:
                return 960;
            case C0338R.styleable.PromptView_primaryTextTypeface /*17*/:
                return TLRPC.MESSAGE_FLAG_HAS_VIEWS;
            case C0338R.styleable.PromptView_secondaryText /*18*/:
                return 1152;
            case C0338R.styleable.PromptView_secondaryTextColour /*19*/:
                return 1280;
            case C0338R.styleable.PromptView_secondaryTextFontFamily /*20*/:
                return 1344;
            case C0338R.styleable.PromptView_secondaryTextSize /*21*/:
                return 1408;
            case C0338R.styleable.PromptView_secondaryTextStyle /*22*/:
                return 1411;
            case C0338R.styleable.PromptView_secondaryTextTypeface /*23*/:
                return 1472;
            case C0338R.styleable.PromptView_target /*24*/:
                return 1536;
            case C0338R.styleable.PromptView_textPadding /*25*/:
                return -1;
            default:
                throw new IOException("Unknown bitrate value");
        }
    }

    private int getSampleRate(int i) {
        switch (i) {
            case VideoPlayer.TYPE_AUDIO /*1*/:
                return UdpDataSource.DEAFULT_SOCKET_TIMEOUT_MILLIS;
            case VideoPlayer.STATE_PREPARING /*2*/:
                return 16000;
            case VideoPlayer.STATE_BUFFERING /*3*/:
                return 32000;
            case Method.TRACE /*6*/:
                return 11025;
            case Method.PATCH /*7*/:
                return 22050;
            case TLRPC.USER_FLAG_USERNAME /*8*/:
                return 44100;
            case C0338R.styleable.PromptView_maxTextWidth /*11*/:
                return 12000;
            case Atom.FULL_HEADER_SIZE /*12*/:
                return 24000;
            case ShamsiCalendar.CURRENT_CENTURY /*13*/:
                return 48000;
            default:
                throw new IOException("Unknown Sample Rate");
        }
    }

    private void parse() {
        if (readVariables()) {
            this.sampleDescriptionBox = new SampleDescriptionBox();
            Box audioSampleEntry = new AudioSampleEntry(this.type);
            audioSampleEntry.setChannelCount(this.channelCount);
            audioSampleEntry.setSampleRate((long) this.samplerate);
            audioSampleEntry.setDataReferenceIndex(1);
            audioSampleEntry.setSampleSize(16);
            audioSampleEntry.addBox(this.ddts);
            this.sampleDescriptionBox.addBox(audioSampleEntry);
            this.trackMetaData.setCreationTime(new Date());
            this.trackMetaData.setModificationTime(new Date());
            this.trackMetaData.setLanguage(this.lang);
            this.trackMetaData.setTimescale((long) this.samplerate);
            return;
        }
        throw new IOException();
    }

    private boolean parseAuprhdr(int i, ByteBuffer byteBuffer) {
        byteBuffer.get();
        short s = byteBuffer.getShort();
        this.maxSampleRate = (byteBuffer.get() << 16) | (byteBuffer.getShort() & SupportMenu.USER_MASK);
        this.numFramesTotal = byteBuffer.getInt();
        this.samplesPerFrameAtMaxFs = byteBuffer.getShort();
        this.numSamplesOrigAudioAtMaxFs = (byteBuffer.get() << 32) | (byteBuffer.getInt() & SupportMenu.USER_MASK);
        this.channelMask = byteBuffer.getShort();
        this.codecDelayAtMaxFs = byteBuffer.getShort();
        int i2 = 21;
        if ((s & 3) == 3) {
            this.bcCoreMaxSampleRate = (byteBuffer.get() << 16) | (byteBuffer.getShort() & SupportMenu.USER_MASK);
            this.bcCoreBitRate = byteBuffer.getShort();
            this.bcCoreChannelMask = byteBuffer.getShort();
            i2 = 28;
        }
        if ((s & 4) > 0) {
            this.lsbTrimPercent = byteBuffer.get();
            i2++;
        }
        if ((s & 8) > 0) {
            this.lbrCodingPresent = 1;
        }
        while (i2 < i) {
            byteBuffer.get();
            i2++;
        }
        return true;
    }

    private boolean parseCoressmd(int i, ByteBuffer byteBuffer) {
        this.coreMaxSampleRate = (byteBuffer.get() << 16) | (byteBuffer.getShort() & SupportMenu.USER_MASK);
        this.coreBitRate = byteBuffer.getShort();
        this.coreChannelMask = byteBuffer.getShort();
        this.coreFramePayloadInBytes = byteBuffer.getInt();
        for (int i2 = 11; i2 < i; i2++) {
            byteBuffer.get();
        }
        return true;
    }

    private void parseDtshdhdr(int i, ByteBuffer byteBuffer) {
        byteBuffer.getInt();
        byteBuffer.get();
        byteBuffer.getInt();
        byteBuffer.get();
        short s = byteBuffer.getShort();
        byteBuffer.get();
        this.numExtSubStreams = byteBuffer.get();
        if ((s & 1) == 1) {
            this.isVBR = true;
        }
        if ((s & 8) == 8) {
            this.coreSubStreamPresent = true;
        }
        if ((s & 16) == 16) {
            this.extensionSubStreamPresent = true;
            this.numExtSubStreams++;
        } else {
            this.numExtSubStreams = 0;
        }
        for (int i2 = 14; i2 < i; i2++) {
            byteBuffer.get();
        }
    }

    private boolean parseExtssmd(int i, ByteBuffer byteBuffer) {
        int i2;
        this.extAvgBitrate = (byteBuffer.get() << 16) | (byteBuffer.getShort() & SupportMenu.USER_MASK);
        if (this.isVBR) {
            this.extPeakBitrate = (byteBuffer.get() << 16) | (byteBuffer.getShort() & SupportMenu.USER_MASK);
            this.extSmoothBuffSize = byteBuffer.getShort();
            i2 = 8;
        } else {
            this.extFramePayloadInBytes = byteBuffer.getInt();
            i2 = 7;
        }
        while (i2 < i) {
            byteBuffer.get();
            i2++;
        }
        return true;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean readVariables() {
        /*
        r24 = this;
        r0 = r24;
        r2 = r0.dataSource;
        r4 = 0;
        r6 = 25000; // 0x61a8 float:3.5032E-41 double:1.23516E-319;
        r19 = r2.map(r4, r6);
        r3 = r19.getInt();
        r2 = r19.getInt();
        r4 = 1146377032; // 0x44545348 float:849.3013 double:5.663855087E-315;
        if (r3 != r4) goto L_0x001e;
    L_0x0019:
        r4 = 1145586770; // 0x44484452 float:801.0675 double:5.659950674E-315;
        if (r2 == r4) goto L_0x0045;
    L_0x001e:
        r2 = new java.io.IOException;
        r3 = "data does not start with 'DTSHDHDR' as required for a DTS-HD file";
        r2.<init>(r3);
        throw r2;
    L_0x0027:
        r4 = r19.getLong();
        r4 = (int) r4;
        r5 = 1146377032; // 0x44545348 float:849.3013 double:5.663855087E-315;
        if (r3 != r5) goto L_0x0089;
    L_0x0031:
        r5 = 1145586770; // 0x44484452 float:801.0675 double:5.659950674E-315;
        if (r2 != r5) goto L_0x0089;
    L_0x0036:
        r0 = r24;
        r1 = r19;
        r0.parseDtshdhdr(r4, r1);
    L_0x003d:
        r3 = r19.getInt();
        r2 = r19.getInt();
    L_0x0045:
        r4 = 1398035021; // 0x5354524d float:9.1191384E11 double:6.907210756E-315;
        if (r3 != r4) goto L_0x004f;
    L_0x004a:
        r4 = 1145132097; // 0x44415441 float:773.31647 double:5.65770429E-315;
        if (r2 == r4) goto L_0x0057;
    L_0x004f:
        r4 = r19.remaining();
        r5 = 100;
        if (r4 > r5) goto L_0x0027;
    L_0x0057:
        r6 = r19.getLong();
        r2 = r19.position();
        r0 = r24;
        r0.dataOffset = r2;
        r16 = -1;
        r5 = 0;
        r3 = 0;
        r8 = -1;
        r2 = -1;
        r15 = 0;
        r14 = 0;
        r13 = 0;
        r12 = 0;
        r11 = 0;
        r10 = 0;
        r9 = 0;
        r4 = 0;
        r17 = r3;
        r18 = r5;
        r5 = r16;
        r16 = r2;
    L_0x0079:
        if (r4 == 0) goto L_0x00d4;
    L_0x007b:
        r2 = -1;
        r0 = r24;
        r3 = r0.samplesPerFrame;
        switch(r3) {
            case 512: goto L_0x0348;
            case 1024: goto L_0x034c;
            case 2048: goto L_0x0350;
            case 4096: goto L_0x0354;
            default: goto L_0x0083;
        };
    L_0x0083:
        r4 = r2;
    L_0x0084:
        r2 = -1;
        if (r4 != r2) goto L_0x0358;
    L_0x0087:
        r2 = 0;
    L_0x0088:
        return r2;
    L_0x0089:
        r5 = 1129271877; // 0x434f5245 float:207.32137 double:5.57934439E-315;
        if (r3 != r5) goto L_0x009f;
    L_0x008e:
        r5 = 1397968196; // 0x53534d44 float:9.075344E11 double:6.906880596E-315;
        if (r2 != r5) goto L_0x009f;
    L_0x0093:
        r0 = r24;
        r1 = r19;
        r2 = r0.parseCoressmd(r4, r1);
        if (r2 != 0) goto L_0x003d;
    L_0x009d:
        r2 = 0;
        goto L_0x0088;
    L_0x009f:
        r5 = 1096110162; // 0x41555052 float:13.332109 double:5.41550375E-315;
        if (r3 != r5) goto L_0x00b5;
    L_0x00a4:
        r5 = 759710802; // 0x2d484452 float:1.1383854E-11 double:3.75347008E-315;
        if (r2 != r5) goto L_0x00b5;
    L_0x00a9:
        r0 = r24;
        r1 = r19;
        r2 = r0.parseAuprhdr(r4, r1);
        if (r2 != 0) goto L_0x003d;
    L_0x00b3:
        r2 = 0;
        goto L_0x0088;
    L_0x00b5:
        r5 = 1163416659; // 0x45585453 float:3461.2703 double:5.74804203E-315;
        if (r3 != r5) goto L_0x00cb;
    L_0x00ba:
        r3 = 1398754628; // 0x535f4d44 float:9.5907401E11 double:6.910766087E-315;
        if (r2 != r3) goto L_0x00cb;
    L_0x00bf:
        r0 = r24;
        r1 = r19;
        r2 = r0.parseExtssmd(r4, r1);
        if (r2 != 0) goto L_0x003d;
    L_0x00c9:
        r2 = 0;
        goto L_0x0088;
    L_0x00cb:
        r2 = 0;
    L_0x00cc:
        if (r2 >= r4) goto L_0x003d;
    L_0x00ce:
        r19.get();
        r2 = r2 + 1;
        goto L_0x00cc;
    L_0x00d4:
        r20 = r19.position();
        r2 = r19.getInt();
        r3 = 2147385345; // 0x7ffe8001 float:NaN double:1.0609493273E-314;
        if (r2 != r3) goto L_0x0239;
    L_0x00e1:
        r2 = 1;
        if (r8 != r2) goto L_0x00e6;
    L_0x00e4:
        r4 = 1;
        goto L_0x0079;
    L_0x00e6:
        r8 = 1;
        r17 = new com.googlecode.mp4parser.boxes.mp4.objectdescriptors.BitReaderBuffer;
        r0 = r17;
        r1 = r19;
        r0.<init>(r1);
        r2 = 1;
        r0 = r17;
        r2 = r0.readBits(r2);
        r3 = 5;
        r0 = r17;
        r3 = r0.readBits(r3);
        r5 = 1;
        r0 = r17;
        r18 = r0.readBits(r5);
        r5 = 1;
        if (r2 != r5) goto L_0x010e;
    L_0x0108:
        r2 = 31;
        if (r3 != r2) goto L_0x010e;
    L_0x010c:
        if (r18 == 0) goto L_0x0111;
    L_0x010e:
        r2 = 0;
        goto L_0x0088;
    L_0x0111:
        r2 = 7;
        r0 = r17;
        r2 = r0.readBits(r2);
        r2 = r2 + 1;
        r2 = r2 * 32;
        r0 = r24;
        r0.samplesPerFrame = r2;
        r2 = 14;
        r0 = r17;
        r21 = r0.readBits(r2);
        r0 = r24;
        r2 = r0.frameSize;
        r3 = r21 + 1;
        r2 = r2 + r3;
        r0 = r24;
        r0.frameSize = r2;
        r2 = 6;
        r0 = r17;
        r5 = r0.readBits(r2);
        r2 = 4;
        r0 = r17;
        r2 = r0.readBits(r2);
        r0 = r24;
        r2 = r0.getSampleRate(r2);
        r0 = r24;
        r0.samplerate = r2;
        r2 = 5;
        r0 = r17;
        r2 = r0.readBits(r2);
        r0 = r24;
        r2 = r0.getBitRate(r2);
        r0 = r24;
        r0.bitrate = r2;
        r2 = 1;
        r0 = r17;
        r2 = r0.readBits(r2);
        if (r2 == 0) goto L_0x0168;
    L_0x0165:
        r2 = 0;
        goto L_0x0088;
    L_0x0168:
        r2 = 1;
        r0 = r17;
        r0.readBits(r2);
        r2 = 1;
        r0 = r17;
        r0.readBits(r2);
        r2 = 1;
        r0 = r17;
        r0.readBits(r2);
        r2 = 1;
        r0 = r17;
        r0.readBits(r2);
        r2 = 3;
        r0 = r17;
        r3 = r0.readBits(r2);
        r2 = 1;
        r0 = r17;
        r2 = r0.readBits(r2);
        r22 = 1;
        r0 = r17;
        r1 = r22;
        r0.readBits(r1);
        r22 = 2;
        r0 = r17;
        r1 = r22;
        r0.readBits(r1);
        r22 = 1;
        r0 = r17;
        r1 = r22;
        r0.readBits(r1);
        r22 = 1;
        r0 = r18;
        r1 = r22;
        if (r0 != r1) goto L_0x01b6;
    L_0x01b1:
        r18 = 16;
        r17.readBits(r18);
    L_0x01b6:
        r18 = 1;
        r17.readBits(r18);
        r18 = 4;
        r18 = r17.readBits(r18);
        r22 = 2;
        r0 = r17;
        r1 = r22;
        r0.readBits(r1);
        r22 = 3;
        r0 = r17;
        r1 = r22;
        r22 = r0.readBits(r1);
        switch(r22) {
            case 0: goto L_0x01da;
            case 1: goto L_0x01da;
            case 2: goto L_0x020d;
            case 3: goto L_0x020d;
            case 4: goto L_0x01d7;
            case 5: goto L_0x0216;
            case 6: goto L_0x0216;
            default: goto L_0x01d7;
        };
    L_0x01d7:
        r2 = 0;
        goto L_0x0088;
    L_0x01da:
        r22 = 16;
        r0 = r22;
        r1 = r24;
        r1.sampleSize = r0;
    L_0x01e2:
        r22 = 1;
        r0 = r17;
        r1 = r22;
        r0.readBits(r1);
        r22 = 1;
        r0 = r17;
        r1 = r22;
        r0.readBits(r1);
        switch(r18) {
            case 6: goto L_0x021f;
            case 7: goto L_0x022d;
            default: goto L_0x01f7;
        };
    L_0x01f7:
        r18 = 4;
        r17.readBits(r18);
    L_0x01fc:
        r17 = r20 + r21;
        r17 = r17 + 1;
        r0 = r19;
        r1 = r17;
        r0.position(r1);
        r17 = r2;
        r18 = r3;
        goto L_0x0079;
    L_0x020d:
        r22 = 20;
        r0 = r22;
        r1 = r24;
        r1.sampleSize = r0;
        goto L_0x01e2;
    L_0x0216:
        r22 = 24;
        r0 = r22;
        r1 = r24;
        r1.sampleSize = r0;
        goto L_0x01e2;
    L_0x021f:
        r18 = 4;
        r17 = r17.readBits(r18);
        r17 = r17 + 16;
        r0 = r17;
        r0 = -r0;
        r17 = r0;
        goto L_0x01fc;
    L_0x022d:
        r18 = 4;
        r17 = r17.readBits(r18);
        r0 = r17;
        r0 = -r0;
        r17 = r0;
        goto L_0x01fc;
    L_0x0239:
        r3 = 1683496997; // 0x64582025 float:1.5947252E22 double:8.31758031E-315;
        if (r2 != r3) goto L_0x032e;
    L_0x023e:
        r2 = -1;
        if (r8 != r2) goto L_0x024a;
    L_0x0241:
        r8 = 0;
        r0 = r24;
        r2 = r0.samplesPerFrameAtMaxFs;
        r0 = r24;
        r0.samplesPerFrame = r2;
    L_0x024a:
        r16 = 1;
        r21 = new com.googlecode.mp4parser.boxes.mp4.objectdescriptors.BitReaderBuffer;
        r0 = r21;
        r1 = r19;
        r0.<init>(r1);
        r2 = 8;
        r0 = r21;
        r0.readBits(r2);
        r2 = 2;
        r0 = r21;
        r0.readBits(r2);
        r2 = 1;
        r0 = r21;
        r22 = r0.readBits(r2);
        r3 = 12;
        r2 = 20;
        if (r22 != 0) goto L_0x0273;
    L_0x026f:
        r3 = 8;
        r2 = 16;
    L_0x0273:
        r0 = r21;
        r3 = r0.readBits(r3);
        r3 = r3 + 1;
        r0 = r21;
        r2 = r0.readBits(r2);
        r21 = r2 + 1;
        r2 = r20 + r3;
        r0 = r19;
        r0.position(r2);
        r2 = r19.getInt();
        r3 = 1515870810; // 0x5a5a5a5a float:1.53652219E16 double:7.48939691E-315;
        if (r2 != r3) goto L_0x02bc;
    L_0x0293:
        r2 = 1;
        if (r15 != r2) goto L_0x0630;
    L_0x0296:
        r2 = 1;
    L_0x0297:
        r3 = 1;
        r4 = r2;
        r2 = r9;
        r9 = r11;
        r11 = r13;
        r13 = r3;
        r3 = r10;
        r10 = r12;
        r12 = r14;
    L_0x02a0:
        if (r4 != 0) goto L_0x02ac;
    L_0x02a2:
        r0 = r24;
        r14 = r0.frameSize;
        r14 = r14 + r21;
        r0 = r24;
        r0.frameSize = r14;
    L_0x02ac:
        r14 = r20 + r21;
        r0 = r19;
        r0.position(r14);
        r14 = r12;
        r15 = r13;
        r12 = r10;
        r13 = r11;
        r11 = r9;
        r10 = r3;
        r9 = r2;
        goto L_0x0079;
    L_0x02bc:
        r3 = 1191201283; // 0x47004a03 float:32842.01 double:5.88531631E-315;
        if (r2 != r3) goto L_0x02ce;
    L_0x02c1:
        r2 = 1;
        if (r14 != r2) goto L_0x02c5;
    L_0x02c4:
        r4 = 1;
    L_0x02c5:
        r2 = 1;
        r3 = r10;
        r10 = r12;
        r12 = r2;
        r2 = r9;
        r9 = r11;
        r11 = r13;
        r13 = r15;
        goto L_0x02a0;
    L_0x02ce:
        r3 = 496366178; // 0x1d95f262 float:3.969059E-21 double:2.452374763E-315;
        if (r2 != r3) goto L_0x02e3;
    L_0x02d3:
        r2 = 1;
        if (r13 != r2) goto L_0x02d7;
    L_0x02d6:
        r4 = 1;
    L_0x02d7:
        r2 = 1;
        r3 = r10;
        r13 = r15;
        r10 = r12;
        r12 = r14;
        r23 = r2;
        r2 = r9;
        r9 = r11;
        r11 = r23;
        goto L_0x02a0;
    L_0x02e3:
        r3 = 1700671838; // 0x655e315e float:6.557975E22 double:8.4024353E-315;
        if (r2 != r3) goto L_0x02f5;
    L_0x02e8:
        r2 = 1;
        if (r12 != r2) goto L_0x02ec;
    L_0x02eb:
        r4 = 1;
    L_0x02ec:
        r2 = 1;
        r3 = r10;
        r12 = r14;
        r10 = r2;
        r2 = r9;
        r9 = r11;
        r11 = r13;
        r13 = r15;
        goto L_0x02a0;
    L_0x02f5:
        r3 = 176167201; // 0xa801921 float:1.2335404E-32 double:8.7038162E-316;
        if (r2 != r3) goto L_0x030a;
    L_0x02fa:
        r2 = 1;
        if (r11 != r2) goto L_0x02fe;
    L_0x02fd:
        r4 = 1;
    L_0x02fe:
        r2 = 1;
        r3 = r10;
        r11 = r13;
        r10 = r12;
        r13 = r15;
        r12 = r14;
        r23 = r9;
        r9 = r2;
        r2 = r23;
        goto L_0x02a0;
    L_0x030a:
        r3 = 1101174087; // 0x41a29547 float:20.32289 double:5.440522865E-315;
        if (r2 != r3) goto L_0x031c;
    L_0x030f:
        r2 = 1;
        if (r10 != r2) goto L_0x0313;
    L_0x0312:
        r4 = 1;
    L_0x0313:
        r2 = 1;
        r3 = r2;
        r10 = r12;
        r2 = r9;
        r12 = r14;
        r9 = r11;
        r11 = r13;
        r13 = r15;
        goto L_0x02a0;
    L_0x031c:
        r3 = 45126241; // 0x2b09261 float:2.5944893E-37 double:2.22953254E-316;
        if (r2 != r3) goto L_0x0627;
    L_0x0321:
        r2 = 1;
        if (r9 != r2) goto L_0x0325;
    L_0x0324:
        r4 = 1;
    L_0x0325:
        r2 = 1;
        r3 = r10;
        r9 = r11;
        r10 = r12;
        r11 = r13;
        r12 = r14;
        r13 = r15;
        goto L_0x02a0;
    L_0x032e:
        r2 = new java.io.IOException;
        r3 = new java.lang.StringBuilder;
        r4 = "No DTS_SYNCWORD_* found at ";
        r3.<init>(r4);
        r4 = r19.position();
        r3 = r3.append(r4);
        r3 = r3.toString();
        r2.<init>(r3);
        throw r2;
    L_0x0348:
        r2 = 0;
        r4 = r2;
        goto L_0x0084;
    L_0x034c:
        r2 = 1;
        r4 = r2;
        goto L_0x0084;
    L_0x0350:
        r2 = 2;
        r4 = r2;
        goto L_0x0084;
    L_0x0354:
        r2 = 3;
        r4 = r2;
        goto L_0x0084;
    L_0x0358:
        r2 = 31;
        switch(r5) {
            case 0: goto L_0x0473;
            case 1: goto L_0x035d;
            case 2: goto L_0x0473;
            case 3: goto L_0x035d;
            case 4: goto L_0x0473;
            case 5: goto L_0x0473;
            case 6: goto L_0x0473;
            case 7: goto L_0x0473;
            case 8: goto L_0x0473;
            case 9: goto L_0x0473;
            default: goto L_0x035d;
        };
    L_0x035d:
        r3 = r2;
    L_0x035e:
        r2 = 0;
        if (r8 != 0) goto L_0x04b3;
    L_0x0361:
        r5 = 1;
        if (r10 != r5) goto L_0x0481;
    L_0x0364:
        if (r9 != 0) goto L_0x0476;
    L_0x0366:
        r2 = 17;
        r5 = "dtsl";
        r0 = r24;
        r0.type = r5;
    L_0x036f:
        r0 = r24;
        r5 = r0.maxSampleRate;
        r0 = r24;
        r0.samplerate = r5;
        r5 = 24;
        r0 = r24;
        r0.sampleSize = r5;
    L_0x037d:
        r0 = r24;
        r5 = r0.ddts;
        r0 = r24;
        r9 = r0.maxSampleRate;
        r10 = (long) r9;
        r5.setDTSSamplingFrequency(r10);
        r0 = r24;
        r5 = r0.isVBR;
        if (r5 == 0) goto L_0x05d8;
    L_0x038f:
        r0 = r24;
        r5 = r0.ddts;
        r0 = r24;
        r9 = r0.coreBitRate;
        r0 = r24;
        r10 = r0.extPeakBitrate;
        r9 = r9 + r10;
        r9 = r9 * 1000;
        r10 = (long) r9;
        r5.setMaxBitRate(r10);
    L_0x03a2:
        r0 = r24;
        r5 = r0.ddts;
        r0 = r24;
        r9 = r0.coreBitRate;
        r0 = r24;
        r10 = r0.extAvgBitrate;
        r9 = r9 + r10;
        r9 = r9 * 1000;
        r10 = (long) r9;
        r5.setAvgBitRate(r10);
        r0 = r24;
        r5 = r0.ddts;
        r0 = r24;
        r9 = r0.sampleSize;
        r5.setPcmSampleDepth(r9);
        r0 = r24;
        r5 = r0.ddts;
        r5.setFrameDuration(r4);
        r0 = r24;
        r4 = r0.ddts;
        r4.setStreamConstruction(r2);
        r0 = r24;
        r2 = r0.coreChannelMask;
        r2 = r2 & 8;
        if (r2 > 0) goto L_0x03de;
    L_0x03d6:
        r0 = r24;
        r2 = r0.coreChannelMask;
        r2 = r2 & 4096;
        if (r2 <= 0) goto L_0x05ed;
    L_0x03de:
        r0 = r24;
        r2 = r0.ddts;
        r4 = 1;
        r2.setCoreLFEPresent(r4);
    L_0x03e6:
        r0 = r24;
        r2 = r0.ddts;
        r2.setCoreLayout(r3);
        r0 = r24;
        r2 = r0.ddts;
        r0 = r24;
        r3 = r0.coreFramePayloadInBytes;
        r2.setCoreSize(r3);
        r0 = r24;
        r2 = r0.ddts;
        r3 = 0;
        r2.setStereoDownmix(r3);
        r0 = r24;
        r2 = r0.ddts;
        r3 = 4;
        r2.setRepresentationType(r3);
        r0 = r24;
        r2 = r0.ddts;
        r0 = r24;
        r3 = r0.channelMask;
        r2.setChannelLayout(r3);
        r0 = r24;
        r2 = r0.coreMaxSampleRate;
        if (r2 <= 0) goto L_0x05f7;
    L_0x0419:
        r0 = r24;
        r2 = r0.extAvgBitrate;
        if (r2 <= 0) goto L_0x05f7;
    L_0x041f:
        r0 = r24;
        r2 = r0.ddts;
        r3 = 1;
        r2.setMultiAssetFlag(r3);
    L_0x0427:
        r0 = r24;
        r2 = r0.ddts;
        r0 = r24;
        r3 = r0.lbrCodingPresent;
        r2.setLBRDurationMod(r3);
        r0 = r24;
        r2 = r0.ddts;
        r3 = 0;
        r2.setReservedBoxPresent(r3);
        r2 = 0;
        r0 = r24;
        r0.channelCount = r2;
        r2 = 0;
    L_0x0440:
        r3 = 16;
        if (r2 < r3) goto L_0x0601;
    L_0x0444:
        r0 = r24;
        r4 = r0.dataSource;
        r0 = r24;
        r5 = r0.dataOffset;
        r3 = r24;
        r2 = r3.generateSamples(r4, r5, r6, r8);
        r0 = r24;
        r0.samples = r2;
        r0 = r24;
        r2 = r0.samples;
        r2 = r2.size();
        r2 = new long[r2];
        r0 = r24;
        r0.sampleDurations = r2;
        r0 = r24;
        r2 = r0.sampleDurations;
        r0 = r24;
        r3 = r0.samplesPerFrame;
        r4 = (long) r3;
        java.util.Arrays.fill(r2, r4);
        r2 = 1;
        goto L_0x0088;
    L_0x0473:
        r3 = r5;
        goto L_0x035e;
    L_0x0476:
        r2 = 21;
        r5 = "dtsh";
        r0 = r24;
        r0.type = r5;
        goto L_0x036f;
    L_0x0481:
        r5 = 1;
        if (r11 != r5) goto L_0x048f;
    L_0x0484:
        r2 = 18;
        r5 = "dtse";
        r0 = r24;
        r0.type = r5;
        goto L_0x036f;
    L_0x048f:
        r5 = 1;
        if (r9 != r5) goto L_0x036f;
    L_0x0492:
        r5 = "dtsh";
        r0 = r24;
        r0.type = r5;
        if (r14 != 0) goto L_0x04a1;
    L_0x049b:
        if (r10 != 0) goto L_0x04a1;
    L_0x049d:
        r2 = 19;
        goto L_0x036f;
    L_0x04a1:
        r5 = 1;
        if (r14 != r5) goto L_0x04aa;
    L_0x04a4:
        if (r10 != 0) goto L_0x04aa;
    L_0x04a6:
        r2 = 20;
        goto L_0x036f;
    L_0x04aa:
        if (r14 != 0) goto L_0x036f;
    L_0x04ac:
        r5 = 1;
        if (r10 != r5) goto L_0x036f;
    L_0x04af:
        r2 = 21;
        goto L_0x036f;
    L_0x04b3:
        r5 = 1;
        r0 = r16;
        if (r0 >= r5) goto L_0x04ef;
    L_0x04b8:
        if (r17 <= 0) goto L_0x04e5;
    L_0x04ba:
        switch(r18) {
            case 0: goto L_0x04c7;
            case 2: goto L_0x04d1;
            case 6: goto L_0x04db;
            default: goto L_0x04bd;
        };
    L_0x04bd:
        r2 = 0;
        r5 = "dtsh";
        r0 = r24;
        r0.type = r5;
        goto L_0x037d;
    L_0x04c7:
        r2 = 2;
        r5 = "dtsc";
        r0 = r24;
        r0.type = r5;
        goto L_0x037d;
    L_0x04d1:
        r2 = 4;
        r5 = "dtsc";
        r0 = r24;
        r0.type = r5;
        goto L_0x037d;
    L_0x04db:
        r2 = 3;
        r5 = "dtsh";
        r0 = r24;
        r0.type = r5;
        goto L_0x037d;
    L_0x04e5:
        r2 = 1;
        r5 = "dtsc";
        r0 = r24;
        r0.type = r5;
        goto L_0x037d;
    L_0x04ef:
        r5 = "dtsh";
        r0 = r24;
        r0.type = r5;
        if (r17 != 0) goto L_0x055e;
    L_0x04f8:
        if (r9 != 0) goto L_0x0508;
    L_0x04fa:
        r5 = 1;
        if (r14 != r5) goto L_0x0508;
    L_0x04fd:
        if (r13 != 0) goto L_0x0508;
    L_0x04ff:
        if (r12 != 0) goto L_0x0508;
    L_0x0501:
        if (r10 != 0) goto L_0x0508;
    L_0x0503:
        if (r11 != 0) goto L_0x0508;
    L_0x0505:
        r2 = 5;
        goto L_0x037d;
    L_0x0508:
        if (r9 != 0) goto L_0x0518;
    L_0x050a:
        if (r14 != 0) goto L_0x0518;
    L_0x050c:
        if (r13 != 0) goto L_0x0518;
    L_0x050e:
        r5 = 1;
        if (r12 != r5) goto L_0x0518;
    L_0x0511:
        if (r10 != 0) goto L_0x0518;
    L_0x0513:
        if (r11 != 0) goto L_0x0518;
    L_0x0515:
        r2 = 6;
        goto L_0x037d;
    L_0x0518:
        if (r9 != 0) goto L_0x052a;
    L_0x051a:
        r5 = 1;
        if (r14 != r5) goto L_0x052a;
    L_0x051d:
        if (r13 != 0) goto L_0x052a;
    L_0x051f:
        r5 = 1;
        if (r12 != r5) goto L_0x052a;
    L_0x0522:
        if (r10 != 0) goto L_0x052a;
    L_0x0524:
        if (r11 != 0) goto L_0x052a;
    L_0x0526:
        r2 = 9;
        goto L_0x037d;
    L_0x052a:
        if (r9 != 0) goto L_0x053b;
    L_0x052c:
        if (r14 != 0) goto L_0x053b;
    L_0x052e:
        r5 = 1;
        if (r13 != r5) goto L_0x053b;
    L_0x0531:
        if (r12 != 0) goto L_0x053b;
    L_0x0533:
        if (r10 != 0) goto L_0x053b;
    L_0x0535:
        if (r11 != 0) goto L_0x053b;
    L_0x0537:
        r2 = 10;
        goto L_0x037d;
    L_0x053b:
        if (r9 != 0) goto L_0x054d;
    L_0x053d:
        r5 = 1;
        if (r14 != r5) goto L_0x054d;
    L_0x0540:
        r5 = 1;
        if (r13 != r5) goto L_0x054d;
    L_0x0543:
        if (r12 != 0) goto L_0x054d;
    L_0x0545:
        if (r10 != 0) goto L_0x054d;
    L_0x0547:
        if (r11 != 0) goto L_0x054d;
    L_0x0549:
        r2 = 13;
        goto L_0x037d;
    L_0x054d:
        if (r9 != 0) goto L_0x037d;
    L_0x054f:
        if (r14 != 0) goto L_0x037d;
    L_0x0551:
        if (r13 != 0) goto L_0x037d;
    L_0x0553:
        if (r12 != 0) goto L_0x037d;
    L_0x0555:
        r5 = 1;
        if (r10 != r5) goto L_0x037d;
    L_0x0558:
        if (r11 != 0) goto L_0x037d;
    L_0x055a:
        r2 = 14;
        goto L_0x037d;
    L_0x055e:
        if (r18 != 0) goto L_0x0570;
    L_0x0560:
        if (r9 != 0) goto L_0x0570;
    L_0x0562:
        if (r14 != 0) goto L_0x0570;
    L_0x0564:
        if (r13 != 0) goto L_0x0570;
    L_0x0566:
        r5 = 1;
        if (r12 != r5) goto L_0x0570;
    L_0x0569:
        if (r10 != 0) goto L_0x0570;
    L_0x056b:
        if (r11 != 0) goto L_0x0570;
    L_0x056d:
        r2 = 7;
        goto L_0x037d;
    L_0x0570:
        r5 = 6;
        r0 = r18;
        if (r0 != r5) goto L_0x0586;
    L_0x0575:
        if (r9 != 0) goto L_0x0586;
    L_0x0577:
        if (r14 != 0) goto L_0x0586;
    L_0x0579:
        if (r13 != 0) goto L_0x0586;
    L_0x057b:
        r5 = 1;
        if (r12 != r5) goto L_0x0586;
    L_0x057e:
        if (r10 != 0) goto L_0x0586;
    L_0x0580:
        if (r11 != 0) goto L_0x0586;
    L_0x0582:
        r2 = 8;
        goto L_0x037d;
    L_0x0586:
        if (r18 != 0) goto L_0x0599;
    L_0x0588:
        if (r9 != 0) goto L_0x0599;
    L_0x058a:
        if (r14 != 0) goto L_0x0599;
    L_0x058c:
        r5 = 1;
        if (r13 != r5) goto L_0x0599;
    L_0x058f:
        if (r12 != 0) goto L_0x0599;
    L_0x0591:
        if (r10 != 0) goto L_0x0599;
    L_0x0593:
        if (r11 != 0) goto L_0x0599;
    L_0x0595:
        r2 = 11;
        goto L_0x037d;
    L_0x0599:
        r5 = 6;
        r0 = r18;
        if (r0 != r5) goto L_0x05af;
    L_0x059e:
        if (r9 != 0) goto L_0x05af;
    L_0x05a0:
        if (r14 != 0) goto L_0x05af;
    L_0x05a2:
        r5 = 1;
        if (r13 != r5) goto L_0x05af;
    L_0x05a5:
        if (r12 != 0) goto L_0x05af;
    L_0x05a7:
        if (r10 != 0) goto L_0x05af;
    L_0x05a9:
        if (r11 != 0) goto L_0x05af;
    L_0x05ab:
        r2 = 12;
        goto L_0x037d;
    L_0x05af:
        if (r18 != 0) goto L_0x05c2;
    L_0x05b1:
        if (r9 != 0) goto L_0x05c2;
    L_0x05b3:
        if (r14 != 0) goto L_0x05c2;
    L_0x05b5:
        if (r13 != 0) goto L_0x05c2;
    L_0x05b7:
        if (r12 != 0) goto L_0x05c2;
    L_0x05b9:
        r5 = 1;
        if (r10 != r5) goto L_0x05c2;
    L_0x05bc:
        if (r11 != 0) goto L_0x05c2;
    L_0x05be:
        r2 = 15;
        goto L_0x037d;
    L_0x05c2:
        r5 = 2;
        r0 = r18;
        if (r0 != r5) goto L_0x037d;
    L_0x05c7:
        if (r9 != 0) goto L_0x037d;
    L_0x05c9:
        if (r14 != 0) goto L_0x037d;
    L_0x05cb:
        if (r13 != 0) goto L_0x037d;
    L_0x05cd:
        if (r12 != 0) goto L_0x037d;
    L_0x05cf:
        r5 = 1;
        if (r10 != r5) goto L_0x037d;
    L_0x05d2:
        if (r11 != 0) goto L_0x037d;
    L_0x05d4:
        r2 = 16;
        goto L_0x037d;
    L_0x05d8:
        r0 = r24;
        r5 = r0.ddts;
        r0 = r24;
        r9 = r0.coreBitRate;
        r0 = r24;
        r10 = r0.extAvgBitrate;
        r9 = r9 + r10;
        r9 = r9 * 1000;
        r10 = (long) r9;
        r5.setMaxBitRate(r10);
        goto L_0x03a2;
    L_0x05ed:
        r0 = r24;
        r2 = r0.ddts;
        r4 = 0;
        r2.setCoreLFEPresent(r4);
        goto L_0x03e6;
    L_0x05f7:
        r0 = r24;
        r2 = r0.ddts;
        r3 = 0;
        r2.setMultiAssetFlag(r3);
        goto L_0x0427;
    L_0x0601:
        r0 = r24;
        r3 = r0.channelMask;
        r3 = r3 >> r2;
        r3 = r3 & 1;
        r4 = 1;
        if (r3 != r4) goto L_0x0618;
    L_0x060b:
        switch(r2) {
            case 0: goto L_0x061c;
            case 1: goto L_0x060e;
            case 2: goto L_0x060e;
            case 3: goto L_0x061c;
            case 4: goto L_0x061c;
            case 5: goto L_0x060e;
            case 6: goto L_0x060e;
            case 7: goto L_0x061c;
            case 8: goto L_0x061c;
            case 9: goto L_0x060e;
            case 10: goto L_0x060e;
            case 11: goto L_0x060e;
            case 12: goto L_0x061c;
            case 13: goto L_0x060e;
            case 14: goto L_0x061c;
            default: goto L_0x060e;
        };
    L_0x060e:
        r0 = r24;
        r3 = r0.channelCount;
        r3 = r3 + 2;
        r0 = r24;
        r0.channelCount = r3;
    L_0x0618:
        r2 = r2 + 1;
        goto L_0x0440;
    L_0x061c:
        r0 = r24;
        r3 = r0.channelCount;
        r3 = r3 + 1;
        r0 = r24;
        r0.channelCount = r3;
        goto L_0x0618;
    L_0x0627:
        r2 = r9;
        r3 = r10;
        r9 = r11;
        r10 = r12;
        r11 = r13;
        r12 = r14;
        r13 = r15;
        goto L_0x02a0;
    L_0x0630:
        r2 = r4;
        goto L_0x0297;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.googlecode.mp4parser.authoring.tracks.DTSTrackImpl.readVariables():boolean");
    }

    public void close() {
        this.dataSource.close();
    }

    public List<Entry> getCompositionTimeEntries() {
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
        return this.sampleDurations;
    }

    public List<Sample> getSamples() {
        return this.samples;
    }

    public long[] getSyncSamples() {
        return null;
    }

    public TrackMetaData getTrackMetaData() {
        return this.trackMetaData;
    }
}
