package com.googlecode.mp4parser.authoring.tracks;

import com.coremedia.iso.boxes.Box;
import com.coremedia.iso.boxes.CompositionTimeToSample.Entry;
import com.coremedia.iso.boxes.SampleDependencyTypeBox;
import com.coremedia.iso.boxes.SampleDescriptionBox;
import com.coremedia.iso.boxes.sampleentry.VisualSampleEntry;
import com.googlecode.mp4parser.DataSource;
import com.googlecode.mp4parser.authoring.Sample;
import com.googlecode.mp4parser.authoring.tracks.AbstractH26XTrack.LookAhead;
import com.googlecode.mp4parser.h264.model.PictureParameterSet;
import com.googlecode.mp4parser.h264.model.SeqParameterSet;
import com.googlecode.mp4parser.h264.read.CAVLCReader;
import com.googlecode.mp4parser.util.RangeStartMap;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.exoplayer.upstream.NetworkLock;
import com.hanista.mobogram.messenger.exoplayer.util.NalUnitUtil;
import com.hanista.mobogram.messenger.volley.Request.Method;
import com.hanista.mobogram.tgnet.TLRPC;
import com.hanista.mobogram.ui.Components.VideoPlayer;
import com.hanista.mobogram.util.shamsicalendar.ShamsiCalendar;
import com.mp4parser.iso14496.part15.AvcConfigurationBox;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class H264TrackImpl extends AbstractH26XTrack {
    private static final Logger LOG;
    PictureParameterSet currentPictureParameterSet;
    SeqParameterSet currentSeqParameterSet;
    private boolean determineFrameRate;
    PictureParameterSet firstPictureParameterSet;
    SeqParameterSet firstSeqParameterSet;
    int frameNrInGop;
    private int frametick;
    private int height;
    private String lang;
    RangeStartMap<Integer, byte[]> pictureParameterRangeMap;
    Map<Integer, PictureParameterSet> ppsIdToPps;
    Map<Integer, byte[]> ppsIdToPpsBytes;
    SampleDescriptionBox sampleDescriptionBox;
    private List<Sample> samples;
    private SEIMessage seiMessage;
    RangeStartMap<Integer, byte[]> seqParameterRangeMap;
    Map<Integer, SeqParameterSet> spsIdToSps;
    Map<Integer, byte[]> spsIdToSpsBytes;
    private long timescale;
    private int width;

    /* renamed from: com.googlecode.mp4parser.authoring.tracks.H264TrackImpl.1FirstVclNalDetector */
    class AnonymousClass1FirstVclNalDetector {
        boolean bottom_field_flag;
        int delta_pic_order_cnt_0;
        int delta_pic_order_cnt_1;
        int delta_pic_order_cnt_bottom;
        boolean field_pic_flag;
        int frame_num;
        boolean idrPicFlag;
        int idr_pic_id;
        int nal_ref_idc;
        int pic_order_cnt_lsb;
        int pic_order_cnt_type;
        int pic_parameter_set_id;

        public AnonymousClass1FirstVclNalDetector(ByteBuffer byteBuffer, int i, int i2) {
            SliceHeader sliceHeader = new SliceHeader(AbstractH26XTrack.cleanBuffer(new ByteBufferBackedInputStream(byteBuffer)), H264TrackImpl.this.spsIdToSps, H264TrackImpl.this.ppsIdToPps, i2 == 5);
            this.frame_num = sliceHeader.frame_num;
            this.pic_parameter_set_id = sliceHeader.pic_parameter_set_id;
            this.field_pic_flag = sliceHeader.field_pic_flag;
            this.bottom_field_flag = sliceHeader.bottom_field_flag;
            this.nal_ref_idc = i;
            this.pic_order_cnt_type = ((SeqParameterSet) H264TrackImpl.this.spsIdToSps.get(Integer.valueOf(((PictureParameterSet) H264TrackImpl.this.ppsIdToPps.get(Integer.valueOf(sliceHeader.pic_parameter_set_id))).seq_parameter_set_id))).pic_order_cnt_type;
            this.delta_pic_order_cnt_bottom = sliceHeader.delta_pic_order_cnt_bottom;
            this.pic_order_cnt_lsb = sliceHeader.pic_order_cnt_lsb;
            this.delta_pic_order_cnt_0 = sliceHeader.delta_pic_order_cnt_0;
            this.delta_pic_order_cnt_1 = sliceHeader.delta_pic_order_cnt_1;
            this.idr_pic_id = sliceHeader.idr_pic_id;
        }

        boolean isFirstInNew(AnonymousClass1FirstVclNalDetector anonymousClass1FirstVclNalDetector) {
            return (anonymousClass1FirstVclNalDetector.frame_num == this.frame_num && anonymousClass1FirstVclNalDetector.pic_parameter_set_id == this.pic_parameter_set_id && anonymousClass1FirstVclNalDetector.field_pic_flag == this.field_pic_flag) ? ((!anonymousClass1FirstVclNalDetector.field_pic_flag || anonymousClass1FirstVclNalDetector.bottom_field_flag == this.bottom_field_flag) && anonymousClass1FirstVclNalDetector.nal_ref_idc == this.nal_ref_idc) ? (anonymousClass1FirstVclNalDetector.pic_order_cnt_type == 0 && this.pic_order_cnt_type == 0 && (anonymousClass1FirstVclNalDetector.pic_order_cnt_lsb != this.pic_order_cnt_lsb || anonymousClass1FirstVclNalDetector.delta_pic_order_cnt_bottom != this.delta_pic_order_cnt_bottom)) ? true : (!(anonymousClass1FirstVclNalDetector.pic_order_cnt_type == 1 && this.pic_order_cnt_type == 1 && (anonymousClass1FirstVclNalDetector.delta_pic_order_cnt_0 != this.delta_pic_order_cnt_0 || anonymousClass1FirstVclNalDetector.delta_pic_order_cnt_1 != this.delta_pic_order_cnt_1)) && anonymousClass1FirstVclNalDetector.idrPicFlag == this.idrPicFlag) ? anonymousClass1FirstVclNalDetector.idrPicFlag && this.idrPicFlag && anonymousClass1FirstVclNalDetector.idr_pic_id != this.idr_pic_id : true : true : true;
        }
    }

    public class ByteBufferBackedInputStream extends InputStream {
        private final ByteBuffer buf;

        public ByteBufferBackedInputStream(ByteBuffer byteBuffer) {
            this.buf = byteBuffer.duplicate();
        }

        public int read() {
            return !this.buf.hasRemaining() ? -1 : this.buf.get() & NalUnitUtil.EXTENDED_SAR;
        }

        public int read(byte[] bArr, int i, int i2) {
            if (!this.buf.hasRemaining()) {
                return -1;
            }
            int min = Math.min(i2, this.buf.remaining());
            this.buf.get(bArr, i, min);
            return min;
        }
    }

    public class SEIMessage {
        boolean clock_timestamp_flag;
        int cnt_dropped_flag;
        int counting_type;
        int cpb_removal_delay;
        int ct_type;
        int discontinuity_flag;
        int dpb_removal_delay;
        int full_timestamp_flag;
        int hours_value;
        int minutes_value;
        int n_frames;
        int nuit_field_based_flag;
        int payloadSize;
        int payloadType;
        int pic_struct;
        boolean removal_delay_flag;
        int seconds_value;
        SeqParameterSet sps;
        int time_offset;
        int time_offset_length;

        public SEIMessage(InputStream inputStream, SeqParameterSet seqParameterSet) {
            this.payloadType = 0;
            this.payloadSize = 0;
            this.sps = seqParameterSet;
            inputStream.read();
            int available = inputStream.available();
            int i = 0;
            while (i < available) {
                this.payloadType = 0;
                this.payloadSize = 0;
                int read = inputStream.read();
                i++;
                while (read == NalUnitUtil.EXTENDED_SAR) {
                    this.payloadType = read + this.payloadType;
                    read = inputStream.read();
                    i++;
                }
                this.payloadType = read + this.payloadType;
                read = inputStream.read();
                i++;
                while (read == NalUnitUtil.EXTENDED_SAR) {
                    this.payloadSize = read + this.payloadSize;
                    read = inputStream.read();
                    i++;
                }
                this.payloadSize = read + this.payloadSize;
                if (available - i < this.payloadSize) {
                    i = available;
                } else if (this.payloadType != 1) {
                    for (read = 0; read < this.payloadSize; read++) {
                        inputStream.read();
                        i++;
                    }
                } else if (seqParameterSet.vuiParams == null || (seqParameterSet.vuiParams.nalHRDParams == null && seqParameterSet.vuiParams.vclHRDParams == null && !seqParameterSet.vuiParams.pic_struct_present_flag)) {
                    for (read = 0; read < this.payloadSize; read++) {
                        inputStream.read();
                        i++;
                    }
                } else {
                    byte[] bArr = new byte[this.payloadSize];
                    inputStream.read(bArr);
                    i += this.payloadSize;
                    CAVLCReader cAVLCReader = new CAVLCReader(new ByteArrayInputStream(bArr));
                    if (seqParameterSet.vuiParams.nalHRDParams == null && seqParameterSet.vuiParams.vclHRDParams == null) {
                        this.removal_delay_flag = false;
                    } else {
                        this.removal_delay_flag = true;
                        this.cpb_removal_delay = cAVLCReader.readU(seqParameterSet.vuiParams.nalHRDParams.cpb_removal_delay_length_minus1 + 1, "SEI: cpb_removal_delay");
                        this.dpb_removal_delay = cAVLCReader.readU(seqParameterSet.vuiParams.nalHRDParams.dpb_output_delay_length_minus1 + 1, "SEI: dpb_removal_delay");
                    }
                    if (seqParameterSet.vuiParams.pic_struct_present_flag) {
                        this.pic_struct = cAVLCReader.readU(4, "SEI: pic_struct");
                        switch (this.pic_struct) {
                            case VideoPlayer.STATE_BUFFERING /*3*/:
                            case VideoPlayer.STATE_READY /*4*/:
                            case Method.PATCH /*7*/:
                                read = 2;
                                break;
                            case VideoPlayer.STATE_ENDED /*5*/:
                            case Method.TRACE /*6*/:
                            case TLRPC.USER_FLAG_USERNAME /*8*/:
                                read = 3;
                                break;
                            default:
                                read = 1;
                                break;
                        }
                        for (int i2 = 0; i2 < read; i2++) {
                            this.clock_timestamp_flag = cAVLCReader.readBool("pic_timing SEI: clock_timestamp_flag[" + i2 + "]");
                            if (this.clock_timestamp_flag) {
                                this.ct_type = cAVLCReader.readU(2, "pic_timing SEI: ct_type");
                                this.nuit_field_based_flag = cAVLCReader.readU(1, "pic_timing SEI: nuit_field_based_flag");
                                this.counting_type = cAVLCReader.readU(5, "pic_timing SEI: counting_type");
                                this.full_timestamp_flag = cAVLCReader.readU(1, "pic_timing SEI: full_timestamp_flag");
                                this.discontinuity_flag = cAVLCReader.readU(1, "pic_timing SEI: discontinuity_flag");
                                this.cnt_dropped_flag = cAVLCReader.readU(1, "pic_timing SEI: cnt_dropped_flag");
                                this.n_frames = cAVLCReader.readU(8, "pic_timing SEI: n_frames");
                                if (this.full_timestamp_flag == 1) {
                                    this.seconds_value = cAVLCReader.readU(6, "pic_timing SEI: seconds_value");
                                    this.minutes_value = cAVLCReader.readU(6, "pic_timing SEI: minutes_value");
                                    this.hours_value = cAVLCReader.readU(5, "pic_timing SEI: hours_value");
                                } else if (cAVLCReader.readBool("pic_timing SEI: seconds_flag")) {
                                    this.seconds_value = cAVLCReader.readU(6, "pic_timing SEI: seconds_value");
                                    if (cAVLCReader.readBool("pic_timing SEI: minutes_flag")) {
                                        this.minutes_value = cAVLCReader.readU(6, "pic_timing SEI: minutes_value");
                                        if (cAVLCReader.readBool("pic_timing SEI: hours_flag")) {
                                            this.hours_value = cAVLCReader.readU(5, "pic_timing SEI: hours_value");
                                        }
                                    }
                                }
                                if (seqParameterSet.vuiParams.nalHRDParams != null) {
                                    this.time_offset_length = seqParameterSet.vuiParams.nalHRDParams.time_offset_length;
                                } else if (seqParameterSet.vuiParams.vclHRDParams != null) {
                                    this.time_offset_length = seqParameterSet.vuiParams.vclHRDParams.time_offset_length;
                                } else {
                                    this.time_offset_length = 24;
                                }
                                this.time_offset = cAVLCReader.readU(24, "pic_timing SEI: time_offset");
                            }
                        }
                    }
                }
                H264TrackImpl.LOG.fine(toString());
            }
        }

        public String toString() {
            Object obj = "SEIMessage{payloadType=" + this.payloadType + ", payloadSize=" + this.payloadSize;
            if (this.payloadType == 1) {
                if (!(this.sps.vuiParams.nalHRDParams == null && this.sps.vuiParams.vclHRDParams == null)) {
                    obj = new StringBuilder(String.valueOf(obj)).append(", cpb_removal_delay=").append(this.cpb_removal_delay).append(", dpb_removal_delay=").append(this.dpb_removal_delay).toString();
                }
                if (this.sps.vuiParams.pic_struct_present_flag) {
                    obj = new StringBuilder(String.valueOf(obj)).append(", pic_struct=").append(this.pic_struct).toString();
                    if (this.clock_timestamp_flag) {
                        obj = new StringBuilder(String.valueOf(obj)).append(", ct_type=").append(this.ct_type).append(", nuit_field_based_flag=").append(this.nuit_field_based_flag).append(", counting_type=").append(this.counting_type).append(", full_timestamp_flag=").append(this.full_timestamp_flag).append(", discontinuity_flag=").append(this.discontinuity_flag).append(", cnt_dropped_flag=").append(this.cnt_dropped_flag).append(", n_frames=").append(this.n_frames).append(", seconds_value=").append(this.seconds_value).append(", minutes_value=").append(this.minutes_value).append(", hours_value=").append(this.hours_value).append(", time_offset_length=").append(this.time_offset_length).append(", time_offset=").append(this.time_offset).toString();
                    }
                }
            }
            return new StringBuilder(String.valueOf(obj)).append('}').toString();
        }
    }

    public static class SliceHeader {
        public boolean bottom_field_flag;
        public int colour_plane_id;
        public int delta_pic_order_cnt_0;
        public int delta_pic_order_cnt_1;
        public int delta_pic_order_cnt_bottom;
        public boolean field_pic_flag;
        public int first_mb_in_slice;
        public int frame_num;
        public int idr_pic_id;
        public int pic_order_cnt_lsb;
        public int pic_parameter_set_id;
        public SliceType slice_type;

        public enum SliceType {
            P,
            B,
            I,
            SP,
            SI
        }

        public SliceHeader(InputStream inputStream, Map<Integer, SeqParameterSet> map, Map<Integer, PictureParameterSet> map2, boolean z) {
            this.field_pic_flag = false;
            this.bottom_field_flag = false;
            try {
                inputStream.read();
                CAVLCReader cAVLCReader = new CAVLCReader(inputStream);
                this.first_mb_in_slice = cAVLCReader.readUE("SliceHeader: first_mb_in_slice");
                switch (cAVLCReader.readUE("SliceHeader: slice_type")) {
                    case VideoPlayer.TRACK_DEFAULT /*0*/:
                    case VideoPlayer.STATE_ENDED /*5*/:
                        this.slice_type = SliceType.P;
                        break;
                    case VideoPlayer.TYPE_AUDIO /*1*/:
                    case Method.TRACE /*6*/:
                        this.slice_type = SliceType.B;
                        break;
                    case VideoPlayer.STATE_PREPARING /*2*/:
                    case Method.PATCH /*7*/:
                        this.slice_type = SliceType.I;
                        break;
                    case VideoPlayer.STATE_BUFFERING /*3*/:
                    case TLRPC.USER_FLAG_USERNAME /*8*/:
                        this.slice_type = SliceType.SP;
                        break;
                    case VideoPlayer.STATE_READY /*4*/:
                    case C0338R.styleable.PromptView_iconTint /*9*/:
                        this.slice_type = SliceType.SI;
                        break;
                }
                this.pic_parameter_set_id = cAVLCReader.readUE("SliceHeader: pic_parameter_set_id");
                PictureParameterSet pictureParameterSet = (PictureParameterSet) map2.get(Integer.valueOf(this.pic_parameter_set_id));
                SeqParameterSet seqParameterSet = (SeqParameterSet) map.get(Integer.valueOf(pictureParameterSet.seq_parameter_set_id));
                if (seqParameterSet.residual_color_transform_flag) {
                    this.colour_plane_id = cAVLCReader.readU(2, "SliceHeader: colour_plane_id");
                }
                this.frame_num = cAVLCReader.readU(seqParameterSet.log2_max_frame_num_minus4 + 4, "SliceHeader: frame_num");
                if (!seqParameterSet.frame_mbs_only_flag) {
                    this.field_pic_flag = cAVLCReader.readBool("SliceHeader: field_pic_flag");
                    if (this.field_pic_flag) {
                        this.bottom_field_flag = cAVLCReader.readBool("SliceHeader: bottom_field_flag");
                    }
                }
                if (z) {
                    this.idr_pic_id = cAVLCReader.readUE("SliceHeader: idr_pic_id");
                }
                if (seqParameterSet.pic_order_cnt_type == 0) {
                    this.pic_order_cnt_lsb = cAVLCReader.readU(seqParameterSet.log2_max_pic_order_cnt_lsb_minus4 + 4, "SliceHeader: pic_order_cnt_lsb");
                    if (pictureParameterSet.bottom_field_pic_order_in_frame_present_flag && !this.field_pic_flag) {
                        this.delta_pic_order_cnt_bottom = cAVLCReader.readSE("SliceHeader: delta_pic_order_cnt_bottom");
                    }
                }
                if (seqParameterSet.pic_order_cnt_type == 1 && !seqParameterSet.delta_pic_order_always_zero_flag) {
                    this.delta_pic_order_cnt_0 = cAVLCReader.readSE("delta_pic_order_cnt_0");
                    if (pictureParameterSet.bottom_field_pic_order_in_frame_present_flag && !this.field_pic_flag) {
                        this.delta_pic_order_cnt_1 = cAVLCReader.readSE("delta_pic_order_cnt_1");
                    }
                }
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }

        public String toString() {
            return "SliceHeader{first_mb_in_slice=" + this.first_mb_in_slice + ", slice_type=" + this.slice_type + ", pic_parameter_set_id=" + this.pic_parameter_set_id + ", colour_plane_id=" + this.colour_plane_id + ", frame_num=" + this.frame_num + ", field_pic_flag=" + this.field_pic_flag + ", bottom_field_flag=" + this.bottom_field_flag + ", idr_pic_id=" + this.idr_pic_id + ", pic_order_cnt_lsb=" + this.pic_order_cnt_lsb + ", delta_pic_order_cnt_bottom=" + this.delta_pic_order_cnt_bottom + '}';
        }
    }

    static {
        LOG = Logger.getLogger(H264TrackImpl.class.getName());
    }

    public H264TrackImpl(DataSource dataSource) {
        this(dataSource, "eng");
    }

    public H264TrackImpl(DataSource dataSource, String str) {
        this(dataSource, str, -1, -1);
    }

    public H264TrackImpl(DataSource dataSource, String str, long j, int i) {
        super(dataSource);
        this.spsIdToSpsBytes = new HashMap();
        this.spsIdToSps = new HashMap();
        this.ppsIdToPpsBytes = new HashMap();
        this.ppsIdToPps = new HashMap();
        this.firstSeqParameterSet = null;
        this.firstPictureParameterSet = null;
        this.currentSeqParameterSet = null;
        this.currentPictureParameterSet = null;
        this.seqParameterRangeMap = new RangeStartMap();
        this.pictureParameterRangeMap = new RangeStartMap();
        this.frameNrInGop = 0;
        this.determineFrameRate = true;
        this.lang = "eng";
        this.lang = str;
        this.timescale = j;
        this.frametick = i;
        if (j > 0 && i > 0) {
            this.determineFrameRate = false;
        }
        parse(new LookAhead(dataSource));
    }

    private void configureFramerate() {
        if (!this.determineFrameRate) {
            return;
        }
        if (this.firstSeqParameterSet.vuiParams != null) {
            this.timescale = (long) (this.firstSeqParameterSet.vuiParams.time_scale >> 1);
            this.frametick = this.firstSeqParameterSet.vuiParams.num_units_in_tick;
            if (this.timescale == 0 || this.frametick == 0) {
                System.err.println("Warning: vuiParams contain invalid values: time_scale: " + this.timescale + " and frame_tick: " + this.frametick + ". Setting frame rate to 25fps");
                this.timescale = 90000;
                this.frametick = 3600;
                return;
            }
            return;
        }
        System.err.println("Warning: Can't determine frame rate. Guessing 25 fps");
        this.timescale = 90000;
        this.frametick = 3600;
    }

    private void createSample(List<ByteBuffer> list) {
        int i = 0;
        int i2 = 22;
        boolean z = false;
        for (ByteBuffer byteBuffer : list) {
            if ((byteBuffer.get(0) & 31) == 5) {
                z = true;
            }
        }
        if (z) {
            i2 = 38;
        }
        if (new SliceHeader(AbstractH26XTrack.cleanBuffer(new ByteBufferBackedInputStream((ByteBuffer) list.get(list.size() - 1))), this.spsIdToSps, this.ppsIdToPps, z).slice_type == SliceType.B) {
            i2 += 4;
        }
        Sample createSampleObject = createSampleObject(list);
        list.clear();
        if (this.seiMessage == null || this.seiMessage.n_frames == 0) {
            this.frameNrInGop = 0;
        }
        if (this.seiMessage != null && this.seiMessage.clock_timestamp_flag) {
            i = this.seiMessage.n_frames - this.frameNrInGop;
        } else if (this.seiMessage != null && this.seiMessage.removal_delay_flag) {
            i = this.seiMessage.dpb_removal_delay / 2;
        }
        this.ctts.add(new Entry(1, i * this.frametick));
        this.sdtp.add(new SampleDependencyTypeBox.Entry(i2));
        this.frameNrInGop++;
        this.samples.add(createSampleObject);
        if (z) {
            this.stss.add(Integer.valueOf(this.samples.size()));
        }
    }

    private void handlePPS(ByteBuffer byteBuffer) {
        InputStream byteBufferBackedInputStream = new ByteBufferBackedInputStream(byteBuffer);
        byteBufferBackedInputStream.read();
        PictureParameterSet read = PictureParameterSet.read(byteBufferBackedInputStream);
        if (this.firstPictureParameterSet == null) {
            this.firstPictureParameterSet = read;
        }
        this.currentPictureParameterSet = read;
        Object toArray = AbstractH26XTrack.toArray((ByteBuffer) byteBuffer.rewind());
        byte[] bArr = (byte[]) this.ppsIdToPpsBytes.get(Integer.valueOf(read.pic_parameter_set_id));
        if (bArr == null || Arrays.equals(bArr, toArray)) {
            if (bArr == null) {
                this.pictureParameterRangeMap.put(Integer.valueOf(this.samples.size()), toArray);
            }
            this.ppsIdToPpsBytes.put(Integer.valueOf(read.pic_parameter_set_id), toArray);
            this.ppsIdToPps.put(Integer.valueOf(read.pic_parameter_set_id), read);
            return;
        }
        throw new RuntimeException("OMG - I got two SPS with same ID but different settings! (AVC3 is the solution)");
    }

    private void handleSPS(ByteBuffer byteBuffer) {
        InputStream cleanBuffer = AbstractH26XTrack.cleanBuffer(new ByteBufferBackedInputStream(byteBuffer));
        cleanBuffer.read();
        SeqParameterSet read = SeqParameterSet.read(cleanBuffer);
        if (this.firstSeqParameterSet == null) {
            this.firstSeqParameterSet = read;
            configureFramerate();
        }
        this.currentSeqParameterSet = read;
        Object toArray = AbstractH26XTrack.toArray((ByteBuffer) byteBuffer.rewind());
        byte[] bArr = (byte[]) this.spsIdToSpsBytes.get(Integer.valueOf(read.seq_parameter_set_id));
        if (bArr == null || Arrays.equals(bArr, toArray)) {
            if (bArr != null) {
                this.seqParameterRangeMap.put(Integer.valueOf(this.samples.size()), toArray);
            }
            this.spsIdToSpsBytes.put(Integer.valueOf(read.seq_parameter_set_id), toArray);
            this.spsIdToSps.put(Integer.valueOf(read.seq_parameter_set_id), read);
            return;
        }
        throw new RuntimeException("OMG - I got two SPS with same ID but different settings!");
    }

    private void parse(LookAhead lookAhead) {
        int i = 0;
        this.samples = new LinkedList();
        if (!readSamples(lookAhead)) {
            throw new IOException();
        } else if (readVariables()) {
            this.sampleDescriptionBox = new SampleDescriptionBox();
            Box visualSampleEntry = new VisualSampleEntry(VisualSampleEntry.TYPE3);
            visualSampleEntry.setDataReferenceIndex(1);
            visualSampleEntry.setDepth(24);
            visualSampleEntry.setFrameCount(1);
            visualSampleEntry.setHorizresolution(72.0d);
            visualSampleEntry.setVertresolution(72.0d);
            visualSampleEntry.setWidth(this.width);
            visualSampleEntry.setHeight(this.height);
            visualSampleEntry.setCompressorname("AVC Coding");
            Box avcConfigurationBox = new AvcConfigurationBox();
            avcConfigurationBox.setSequenceParameterSets(new ArrayList(this.spsIdToSpsBytes.values()));
            avcConfigurationBox.setPictureParameterSets(new ArrayList(this.ppsIdToPpsBytes.values()));
            avcConfigurationBox.setAvcLevelIndication(this.firstSeqParameterSet.level_idc);
            avcConfigurationBox.setAvcProfileIndication(this.firstSeqParameterSet.profile_idc);
            avcConfigurationBox.setBitDepthLumaMinus8(this.firstSeqParameterSet.bit_depth_luma_minus8);
            avcConfigurationBox.setBitDepthChromaMinus8(this.firstSeqParameterSet.bit_depth_chroma_minus8);
            avcConfigurationBox.setChromaFormat(this.firstSeqParameterSet.chroma_format_idc.getId());
            avcConfigurationBox.setConfigurationVersion(1);
            avcConfigurationBox.setLengthSizeMinusOne(3);
            int i2 = (this.firstSeqParameterSet.constraint_set_3_flag ? 16 : 0) + (((this.firstSeqParameterSet.constraint_set_1_flag ? 64 : 0) + (this.firstSeqParameterSet.constraint_set_0_flag ? TLRPC.USER_FLAG_UNUSED : 0)) + (this.firstSeqParameterSet.constraint_set_2_flag ? 32 : 0));
            if (this.firstSeqParameterSet.constraint_set_4_flag) {
                i = 8;
            }
            avcConfigurationBox.setProfileCompatibility((i2 + i) + ((int) (this.firstSeqParameterSet.reserved_zero_2bits & 3)));
            visualSampleEntry.addBox(avcConfigurationBox);
            this.sampleDescriptionBox.addBox(visualSampleEntry);
            this.trackMetaData.setCreationTime(new Date());
            this.trackMetaData.setModificationTime(new Date());
            this.trackMetaData.setLanguage(this.lang);
            this.trackMetaData.setTimescale(this.timescale);
            this.trackMetaData.setWidth((double) this.width);
            this.trackMetaData.setHeight((double) this.height);
        } else {
            throw new IOException();
        }
    }

    private boolean readSamples(LookAhead lookAhead) {
        List arrayList = new ArrayList();
        AnonymousClass1FirstVclNalDetector anonymousClass1FirstVclNalDetector = null;
        while (true) {
            ByteBuffer findNextNal = findNextNal(lookAhead);
            if (findNextNal != null) {
                byte b = findNextNal.get(0);
                int i = (b >> 5) & 3;
                int i2 = b & 31;
                switch (i2) {
                    case VideoPlayer.TYPE_AUDIO /*1*/:
                    case VideoPlayer.STATE_PREPARING /*2*/:
                    case VideoPlayer.STATE_BUFFERING /*3*/:
                    case VideoPlayer.STATE_READY /*4*/:
                    case VideoPlayer.STATE_ENDED /*5*/:
                        AnonymousClass1FirstVclNalDetector anonymousClass1FirstVclNalDetector2 = new AnonymousClass1FirstVclNalDetector(findNextNal, i, i2);
                        if (anonymousClass1FirstVclNalDetector == null) {
                            anonymousClass1FirstVclNalDetector = anonymousClass1FirstVclNalDetector2;
                        } else if (anonymousClass1FirstVclNalDetector.isFirstInNew(anonymousClass1FirstVclNalDetector2)) {
                            createSample(arrayList);
                            anonymousClass1FirstVclNalDetector = anonymousClass1FirstVclNalDetector2;
                        }
                        arrayList.add((ByteBuffer) findNextNal.rewind());
                        continue;
                    case Method.TRACE /*6*/:
                        if (anonymousClass1FirstVclNalDetector != null) {
                            createSample(arrayList);
                            anonymousClass1FirstVclNalDetector = null;
                        }
                        this.seiMessage = new SEIMessage(AbstractH26XTrack.cleanBuffer(new ByteBufferBackedInputStream(findNextNal)), this.currentSeqParameterSet);
                        arrayList.add(findNextNal);
                        continue;
                    case Method.PATCH /*7*/:
                        if (anonymousClass1FirstVclNalDetector != null) {
                            createSample(arrayList);
                            anonymousClass1FirstVclNalDetector = null;
                        }
                        handleSPS((ByteBuffer) findNextNal.rewind());
                        continue;
                    case TLRPC.USER_FLAG_USERNAME /*8*/:
                        if (anonymousClass1FirstVclNalDetector != null) {
                            createSample(arrayList);
                            anonymousClass1FirstVclNalDetector = null;
                        }
                        handlePPS((ByteBuffer) findNextNal.rewind());
                        continue;
                    case C0338R.styleable.PromptView_iconTint /*9*/:
                        if (anonymousClass1FirstVclNalDetector != null) {
                            createSample(arrayList);
                            anonymousClass1FirstVclNalDetector = null;
                        }
                        arrayList.add(findNextNal);
                        continue;
                    case NetworkLock.DOWNLOAD_PRIORITY /*10*/:
                    case C0338R.styleable.PromptView_maxTextWidth /*11*/:
                        break;
                    case ShamsiCalendar.CURRENT_CENTURY /*13*/:
                        throw new RuntimeException("Sequence parameter set extension is not yet handled. Needs TLC.");
                    default:
                        System.err.println("Unknown NAL unit type: " + i2);
                        continue;
                }
            }
            createSample(arrayList);
            this.decodingTimes = new long[this.samples.size()];
            Arrays.fill(this.decodingTimes, (long) this.frametick);
            return true;
        }
    }

    private boolean readVariables() {
        this.width = (this.firstSeqParameterSet.pic_width_in_mbs_minus1 + 1) * 16;
        int i = 2;
        if (this.firstSeqParameterSet.frame_mbs_only_flag) {
            i = 1;
        }
        this.height = ((this.firstSeqParameterSet.pic_height_in_map_units_minus1 + 1) * 16) * i;
        if (this.firstSeqParameterSet.frame_cropping_flag) {
            int i2 = 0;
            if (!this.firstSeqParameterSet.residual_color_transform_flag) {
                i2 = this.firstSeqParameterSet.chroma_format_idc.getId();
            }
            if (i2 != 0) {
                i2 = this.firstSeqParameterSet.chroma_format_idc.getSubWidth();
                i *= this.firstSeqParameterSet.chroma_format_idc.getSubHeight();
            } else {
                i2 = 1;
            }
            this.width -= i2 * (this.firstSeqParameterSet.frame_crop_left_offset + this.firstSeqParameterSet.frame_crop_right_offset);
            this.height -= i * (this.firstSeqParameterSet.frame_crop_top_offset + this.firstSeqParameterSet.frame_crop_bottom_offset);
        }
        return true;
    }

    public String getHandler() {
        return "vide";
    }

    public SampleDescriptionBox getSampleDescriptionBox() {
        return this.sampleDescriptionBox;
    }

    public List<Sample> getSamples() {
        return this.samples;
    }
}
