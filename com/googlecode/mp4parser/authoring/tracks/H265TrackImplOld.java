package com.googlecode.mp4parser.authoring.tracks;

import com.coremedia.iso.IsoTypeReader;
import com.googlecode.mp4parser.DataSource;
import com.googlecode.mp4parser.FileDataSourceImpl;
import com.googlecode.mp4parser.authoring.Sample;
import com.googlecode.mp4parser.authoring.SampleImpl;
import com.googlecode.mp4parser.h264.read.CAVLCReader;
import com.googlecode.mp4parser.util.ByteBufferByteChannel;
import com.hanista.mobogram.tgnet.TLRPC;
import com.mp4parser.iso14496.part15.HevcDecoderConfigurationRecord;
import com.mp4parser.iso14496.part15.HevcDecoderConfigurationRecord.Array;
import java.io.EOFException;
import java.io.PrintStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

public class H265TrackImplOld {
    public static final int AUD_NUT = 35;
    private static final int BLA_N_LP = 18;
    private static final int BLA_W_LP = 16;
    private static final int BLA_W_RADL = 17;
    private static final long BUFFER = 1048576;
    private static final int CRA_NUT = 21;
    private static final int IDR_N_LP = 20;
    private static final int IDR_W_RADL = 19;
    public static final int PPS_NUT = 34;
    public static final int PREFIX_SEI_NUT = 39;
    private static final int RADL_N = 6;
    private static final int RADL_R = 7;
    private static final int RASL_N = 8;
    private static final int RASL_R = 9;
    public static final int RSV_NVCL41 = 41;
    public static final int RSV_NVCL42 = 42;
    public static final int RSV_NVCL43 = 43;
    public static final int RSV_NVCL44 = 44;
    public static final int SPS_NUT = 33;
    private static final int STSA_N = 4;
    private static final int STSA_R = 5;
    private static final int TRAIL_N = 0;
    private static final int TRAIL_R = 1;
    private static final int TSA_N = 2;
    private static final int TSA_R = 3;
    public static final int UNSPEC48 = 48;
    public static final int UNSPEC49 = 49;
    public static final int UNSPEC50 = 50;
    public static final int UNSPEC51 = 51;
    public static final int UNSPEC52 = 52;
    public static final int UNSPEC53 = 53;
    public static final int UNSPEC54 = 54;
    public static final int UNSPEC55 = 55;
    public static final int VPS_NUT = 32;
    LinkedHashMap<Long, ByteBuffer> pictureParamterSets;
    List<Sample> samples;
    LinkedHashMap<Long, ByteBuffer> sequenceParamterSets;
    List<Long> syncSamples;
    LinkedHashMap<Long, ByteBuffer> videoParamterSets;

    class LookAhead {
        ByteBuffer buffer;
        long bufferStartPos;
        DataSource dataSource;
        int inBufferPos;
        long start;

        LookAhead(DataSource dataSource) {
            this.bufferStartPos = 0;
            this.inBufferPos = H265TrackImplOld.TRAIL_N;
            this.dataSource = dataSource;
            fillBuffer();
        }

        void discardByte() {
            this.inBufferPos += H265TrackImplOld.TRAIL_R;
        }

        void discardNext3AndMarkStart() {
            this.inBufferPos += H265TrackImplOld.TSA_R;
            this.start = this.bufferStartPos + ((long) this.inBufferPos);
        }

        public void fillBuffer() {
            this.buffer = this.dataSource.map(this.bufferStartPos, Math.min(this.dataSource.size() - this.bufferStartPos, H265TrackImplOld.BUFFER));
        }

        public ByteBuffer getNal() {
            if (this.start >= this.bufferStartPos) {
                this.buffer.position((int) (this.start - this.bufferStartPos));
                Buffer slice = this.buffer.slice();
                slice.limit((int) (((long) this.inBufferPos) - (this.start - this.bufferStartPos)));
                return (ByteBuffer) slice;
            }
            throw new RuntimeException("damn! NAL exceeds buffer");
        }

        boolean nextThreeEquals000or001orEof() {
            if (this.buffer.limit() - this.inBufferPos >= H265TrackImplOld.TSA_R) {
                return this.buffer.get(this.inBufferPos) == null && this.buffer.get(this.inBufferPos + H265TrackImplOld.TRAIL_R) == null && (this.buffer.get(this.inBufferPos + H265TrackImplOld.TSA_N) == null || this.buffer.get(this.inBufferPos + H265TrackImplOld.TSA_N) == (byte) 1);
            } else {
                if ((this.bufferStartPos + ((long) this.inBufferPos)) + 3 > this.dataSource.size()) {
                    return this.bufferStartPos + ((long) this.inBufferPos) == this.dataSource.size();
                } else {
                    this.bufferStartPos = this.start;
                    this.inBufferPos = H265TrackImplOld.TRAIL_N;
                    fillBuffer();
                    return nextThreeEquals000or001orEof();
                }
            }
        }

        boolean nextThreeEquals001() {
            if (this.buffer.limit() - this.inBufferPos >= H265TrackImplOld.TSA_R) {
                return this.buffer.get(this.inBufferPos) == null && this.buffer.get(this.inBufferPos + H265TrackImplOld.TRAIL_R) == null && this.buffer.get(this.inBufferPos + H265TrackImplOld.TSA_N) == (byte) 1;
            } else {
                if (this.bufferStartPos + ((long) this.inBufferPos) == this.dataSource.size()) {
                    throw new EOFException();
                }
                throw new RuntimeException("buffer repositioning require");
            }
        }
    }

    public static class NalUnitHeader {
        int forbiddenZeroFlag;
        int nalUnitType;
        int nuhLayerId;
        int nuhTemporalIdPlusOne;
    }

    public enum PARSE_STATE {
        AUD_SEI_SLICE,
        SEI_SLICE,
        SLICE_OES_EOB
    }

    public H265TrackImplOld(DataSource dataSource) {
        this.videoParamterSets = new LinkedHashMap();
        this.sequenceParamterSets = new LinkedHashMap();
        this.pictureParamterSets = new LinkedHashMap();
        this.syncSamples = new ArrayList();
        this.samples = new ArrayList();
        LookAhead lookAhead = new LookAhead(dataSource);
        long j = 1;
        List<ByteBuffer> arrayList = new ArrayList();
        int i = TRAIL_N;
        while (true) {
            ByteBuffer findNextNal = findNextNal(lookAhead);
            if (findNextNal == null) {
                System.err.println(TtmlNode.ANONYMOUS_REGION_ID);
                HevcDecoderConfigurationRecord hevcDecoderConfigurationRecord = new HevcDecoderConfigurationRecord();
                hevcDecoderConfigurationRecord.setArrays(getArrays());
                hevcDecoderConfigurationRecord.setAvgFrameRate(TRAIL_N);
                return;
            }
            NalUnitHeader nalUnitHeader = getNalUnitHeader(findNextNal);
            switch (nalUnitHeader.nalUnitType) {
                case VPS_NUT /*32*/:
                    this.videoParamterSets.put(Long.valueOf(j), findNextNal);
                    break;
                case SPS_NUT /*33*/:
                    this.sequenceParamterSets.put(Long.valueOf(j), findNextNal);
                    break;
                case PPS_NUT /*34*/:
                    this.pictureParamterSets.put(Long.valueOf(j), findNextNal);
                    break;
            }
            int i2 = nalUnitHeader.nalUnitType < VPS_NUT ? nalUnitHeader.nalUnitType : i;
            if (isFirstOfAU(nalUnitHeader.nalUnitType, findNextNal, arrayList) && !arrayList.isEmpty()) {
                System.err.println("##########################");
                for (ByteBuffer byteBuffer : arrayList) {
                    NalUnitHeader nalUnitHeader2 = getNalUnitHeader(byteBuffer);
                    PrintStream printStream = System.err;
                    Object[] objArr = new Object[STSA_N];
                    objArr[TRAIL_N] = Integer.valueOf(nalUnitHeader2.nalUnitType);
                    objArr[TRAIL_R] = Integer.valueOf(nalUnitHeader2.nuhLayerId);
                    objArr[TSA_N] = Integer.valueOf(nalUnitHeader2.nuhTemporalIdPlusOne);
                    objArr[TSA_R] = Integer.valueOf(byteBuffer.limit());
                    printStream.println(String.format("type: %3d - layer: %3d - tempId: %3d - size: %3d", objArr));
                }
                System.err.println("                          ##########################");
                this.samples.add(createSample(arrayList));
                arrayList.clear();
                j++;
            }
            arrayList.add(findNextNal);
            if (i2 >= BLA_W_LP && i2 <= CRA_NUT) {
                this.syncSamples.add(Long.valueOf(j));
            }
            i = i2;
        }
    }

    private ByteBuffer findNextNal(LookAhead lookAhead) {
        while (!lookAhead.nextThreeEquals001()) {
            try {
                lookAhead.discardByte();
            } catch (EOFException e) {
                return null;
            }
        }
        lookAhead.discardNext3AndMarkStart();
        while (!lookAhead.nextThreeEquals000or001orEof()) {
            lookAhead.discardByte();
        }
        return lookAhead.getNal();
    }

    private List<Array> getArrays() {
        Array array = new Array();
        array.array_completeness = true;
        array.nal_unit_type = VPS_NUT;
        array.nalUnits = new ArrayList();
        for (ByteBuffer byteBuffer : this.videoParamterSets.values()) {
            Object obj = new byte[byteBuffer.limit()];
            byteBuffer.position(TRAIL_N);
            byteBuffer.get(obj);
            array.nalUnits.add(obj);
        }
        Array array2 = new Array();
        array2.array_completeness = true;
        array2.nal_unit_type = SPS_NUT;
        array2.nalUnits = new ArrayList();
        for (ByteBuffer byteBuffer2 : this.sequenceParamterSets.values()) {
            Object obj2 = new byte[byteBuffer2.limit()];
            byteBuffer2.position(TRAIL_N);
            byteBuffer2.get(obj2);
            array2.nalUnits.add(obj2);
        }
        Array array3 = new Array();
        array3.array_completeness = true;
        array3.nal_unit_type = SPS_NUT;
        array3.nalUnits = new ArrayList();
        for (ByteBuffer byteBuffer22 : this.pictureParamterSets.values()) {
            Object obj3 = new byte[byteBuffer22.limit()];
            byteBuffer22.position(TRAIL_N);
            byteBuffer22.get(obj3);
            array3.nalUnits.add(obj3);
        }
        Array[] arrayArr = new Array[TSA_R];
        arrayArr[TRAIL_N] = array;
        arrayArr[TRAIL_R] = array2;
        arrayArr[TSA_N] = array3;
        return Arrays.asList(arrayArr);
    }

    private void hrd_parameters(boolean z, int i, CAVLCReader cAVLCReader) {
        boolean readBool;
        boolean readBool2;
        boolean readBool3;
        int i2 = TRAIL_N;
        if (z) {
            readBool = cAVLCReader.readBool("nal_hrd_parameters_present_flag");
            readBool2 = cAVLCReader.readBool("vcl_hrd_parameters_present_flag");
            if (readBool || readBool2) {
                readBool3 = cAVLCReader.readBool("sub_pic_hrd_params_present_flag");
                if (readBool3) {
                    cAVLCReader.readU(RASL_N, "tick_divisor_minus2");
                    cAVLCReader.readU(STSA_R, "du_cpb_removal_delay_increment_length_minus1");
                    cAVLCReader.readBool("sub_pic_cpb_params_in_pic_timing_sei_flag");
                    cAVLCReader.readU(STSA_R, "dpb_output_delay_du_length_minus1");
                }
                cAVLCReader.readU(STSA_N, "bit_rate_scale");
                cAVLCReader.readU(STSA_N, "cpb_size_scale");
                if (readBool3) {
                    cAVLCReader.readU(STSA_N, "cpb_size_du_scale");
                }
                cAVLCReader.readU(STSA_R, "initial_cpb_removal_delay_length_minus1");
                cAVLCReader.readU(STSA_R, "au_cpb_removal_delay_length_minus1");
                cAVLCReader.readU(STSA_R, "dpb_output_delay_length_minus1");
            } else {
                readBool3 = false;
            }
        } else {
            readBool3 = false;
            readBool2 = false;
            readBool = false;
        }
        boolean[] zArr = new boolean[i];
        boolean[] zArr2 = new boolean[i];
        boolean[] zArr3 = new boolean[i];
        int[] iArr = new int[i];
        int[] iArr2 = new int[i];
        while (i2 <= i) {
            zArr[i2] = cAVLCReader.readBool("fixed_pic_rate_general_flag[" + i2 + "]");
            if (!zArr[i2]) {
                zArr2[i2] = cAVLCReader.readBool("fixed_pic_rate_within_cvs_flag[" + i2 + "]");
            }
            if (zArr2[i2]) {
                iArr2[i2] = cAVLCReader.readUE("elemental_duration_in_tc_minus1[" + i2 + "]");
            } else {
                zArr3[i2] = cAVLCReader.readBool("low_delay_hrd_flag[" + i2 + "]");
            }
            if (!zArr3[i2]) {
                iArr[i2] = cAVLCReader.readUE("cpb_cnt_minus1[" + i2 + "]");
            }
            if (readBool) {
                sub_layer_hrd_parameters(i2, iArr[i2], readBool3, cAVLCReader);
            }
            if (readBool2) {
                sub_layer_hrd_parameters(i2, iArr[i2], readBool3, cAVLCReader);
            }
            i2 += TRAIL_R;
        }
    }

    public static void main(String[] strArr) {
        H265TrackImplOld h265TrackImplOld = new H265TrackImplOld(new FileDataSourceImpl("c:\\content\\test-UHD-HEVC_01_FMV_Med_track1.hvc"));
    }

    protected Sample createSample(List<ByteBuffer> list) {
        byte[] bArr = new byte[(list.size() * STSA_N)];
        ByteBuffer wrap = ByteBuffer.wrap(bArr);
        for (ByteBuffer remaining : list) {
            wrap.putInt(remaining.remaining());
        }
        ByteBuffer[] byteBufferArr = new ByteBuffer[(list.size() * TSA_N)];
        for (int i = TRAIL_N; i < list.size(); i += TRAIL_R) {
            byteBufferArr[i * TSA_N] = ByteBuffer.wrap(bArr, i * STSA_N, STSA_N);
            byteBufferArr[(i * TSA_N) + TRAIL_R] = (ByteBuffer) list.get(i);
        }
        return new SampleImpl(byteBufferArr);
    }

    public int getFrameRate(ByteBuffer byteBuffer) {
        int i;
        CAVLCReader cAVLCReader = new CAVLCReader(Channels.newInputStream(new ByteBufferByteChannel((ByteBuffer) byteBuffer.position(TRAIL_N))));
        cAVLCReader.readU(STSA_N, "vps_parameter_set_id");
        cAVLCReader.readU(TSA_N, "vps_reserved_three_2bits");
        cAVLCReader.readU(RADL_N, "vps_max_layers_minus1");
        int readU = cAVLCReader.readU(TSA_R, "vps_max_sub_layers_minus1");
        cAVLCReader.readBool("vps_temporal_id_nesting_flag");
        cAVLCReader.readU(BLA_W_LP, "vps_reserved_0xffff_16bits");
        profile_tier_level(readU, cAVLCReader);
        boolean readBool = cAVLCReader.readBool("vps_sub_layer_ordering_info_present_flag");
        int[] iArr = new int[(readBool ? TRAIL_N : readU)];
        int[] iArr2 = new int[(readBool ? TRAIL_N : readU)];
        int[] iArr3 = new int[(readBool ? TRAIL_N : readU)];
        int i2 = readBool ? TRAIL_N : readU;
        while (i2 <= readU) {
            iArr[i2] = cAVLCReader.readUE("vps_max_dec_pic_buffering_minus1[" + i2 + "]");
            iArr2[i2] = cAVLCReader.readUE("vps_max_dec_pic_buffering_minus1[" + i2 + "]");
            iArr3[i2] = cAVLCReader.readUE("vps_max_dec_pic_buffering_minus1[" + i2 + "]");
            i2 += TRAIL_R;
        }
        int readU2 = cAVLCReader.readU(RADL_N, "vps_max_layer_id");
        int readUE = cAVLCReader.readUE("vps_num_layer_sets_minus1");
        boolean[][] zArr = (boolean[][]) java.lang.reflect.Array.newInstance(Boolean.TYPE, new int[]{readUE, readU2});
        for (int i3 = TRAIL_R; i3 <= readUE; i3 += TRAIL_R) {
            for (i = TRAIL_N; i <= readU2; i += TRAIL_R) {
                zArr[i3][i] = cAVLCReader.readBool("layer_id_included_flag[" + i3 + "][" + i + "]");
            }
        }
        if (cAVLCReader.readBool("vps_timing_info_present_flag")) {
            long readU3 = (long) cAVLCReader.readU(VPS_NUT, "vps_num_units_in_tick");
            readU3 = (long) cAVLCReader.readU(VPS_NUT, "vps_time_scale");
            if (cAVLCReader.readBool("vps_poc_proportional_to_timing_flag")) {
                cAVLCReader.readUE("vps_num_ticks_poc_diff_one_minus1");
            }
            i = cAVLCReader.readUE("vps_num_hrd_parameters");
            iArr = new int[i];
            boolean[] zArr2 = new boolean[i];
            for (i2 = TRAIL_N; i2 < i; i2 += TRAIL_R) {
                iArr[i2] = cAVLCReader.readUE("hrd_layer_set_idx[" + i2 + "]");
                if (i2 > 0) {
                    zArr2[i2] = cAVLCReader.readBool("cprms_present_flag[" + i2 + "]");
                } else {
                    zArr2[TRAIL_N] = true;
                }
                hrd_parameters(zArr2[i2], readU, cAVLCReader);
            }
        }
        if (cAVLCReader.readBool("vps_extension_flag")) {
            while (cAVLCReader.moreRBSPData()) {
                cAVLCReader.readBool("vps_extension_data_flag");
            }
        }
        cAVLCReader.readTrailingBits();
        return TRAIL_N;
    }

    public NalUnitHeader getNalUnitHeader(ByteBuffer byteBuffer) {
        byteBuffer.position(TRAIL_N);
        int readUInt16 = IsoTypeReader.readUInt16(byteBuffer);
        NalUnitHeader nalUnitHeader = new NalUnitHeader();
        nalUnitHeader.forbiddenZeroFlag = (TLRPC.MESSAGE_FLAG_EDITED & readUInt16) >> 15;
        nalUnitHeader.nalUnitType = (readUInt16 & 32256) >> RASL_R;
        nalUnitHeader.nuhLayerId = (readUInt16 & 504) >> TSA_R;
        nalUnitHeader.nuhTemporalIdPlusOne = readUInt16 & RADL_R;
        return nalUnitHeader;
    }

    boolean isFirstOfAU(int i, ByteBuffer byteBuffer, List<ByteBuffer> list) {
        if (list.isEmpty()) {
            return true;
        }
        int i2 = getNalUnitHeader((ByteBuffer) list.get(list.size() + -1)).nalUnitType <= 31 ? TRAIL_R : TRAIL_N;
        switch (i) {
            case VPS_NUT /*32*/:
            case SPS_NUT /*33*/:
            case PPS_NUT /*34*/:
            case AUD_NUT /*35*/:
            case PREFIX_SEI_NUT /*39*/:
            case RSV_NVCL41 /*41*/:
            case RSV_NVCL42 /*42*/:
            case RSV_NVCL43 /*43*/:
            case RSV_NVCL44 /*44*/:
            case UNSPEC48 /*48*/:
            case UNSPEC49 /*49*/:
            case UNSPEC50 /*50*/:
            case UNSPEC51 /*51*/:
            case UNSPEC52 /*52*/:
            case UNSPEC53 /*53*/:
            case UNSPEC54 /*54*/:
            case UNSPEC55 /*55*/:
                if (i2 != 0) {
                    return true;
                }
                break;
        }
        switch (i) {
            case TRAIL_N /*0*/:
            case TRAIL_R /*1*/:
            case TSA_N /*2*/:
            case TSA_R /*3*/:
            case STSA_N /*4*/:
            case STSA_R /*5*/:
            case RADL_N /*6*/:
            case RADL_R /*7*/:
            case RASL_N /*8*/:
            case RASL_R /*9*/:
            case BLA_W_LP /*16*/:
            case BLA_W_RADL /*17*/:
            case BLA_N_LP /*18*/:
            case IDR_W_RADL /*19*/:
            case IDR_N_LP /*20*/:
            case CRA_NUT /*21*/:
                byte[] bArr = new byte[UNSPEC50];
                byteBuffer.position(TRAIL_N);
                byteBuffer.get(bArr);
                byteBuffer.position(TSA_N);
                return i2 != 0 && (IsoTypeReader.readUInt8(byteBuffer) & TLRPC.USER_FLAG_UNUSED) > 0;
            default:
                return false;
        }
    }

    public void profile_tier_level(int i, CAVLCReader cAVLCReader) {
        int i2;
        cAVLCReader.readU(TSA_N, "general_profile_space ");
        cAVLCReader.readBool("general_tier_flag");
        cAVLCReader.readU(STSA_R, "general_profile_idc");
        boolean[] zArr = new boolean[VPS_NUT];
        for (i2 = TRAIL_N; i2 < VPS_NUT; i2 += TRAIL_R) {
            zArr[i2] = cAVLCReader.readBool("general_profile_compatibility_flag[" + i2 + "]");
        }
        cAVLCReader.readBool("general_progressive_source_flag");
        cAVLCReader.readBool("general_interlaced_source_flag");
        cAVLCReader.readBool("general_non_packed_constraint_flag");
        cAVLCReader.readBool("general_frame_only_constraint_flag");
        long readU = (long) cAVLCReader.readU(RSV_NVCL44, "general_reserved_zero_44bits");
        cAVLCReader.readU(RASL_N, "general_level_idc");
        boolean[] zArr2 = new boolean[i];
        boolean[] zArr3 = new boolean[i];
        for (i2 = TRAIL_N; i2 < i; i2 += TRAIL_R) {
            zArr2[i2] = cAVLCReader.readBool("sub_layer_profile_present_flag[" + i2 + "]");
            zArr3[i2] = cAVLCReader.readBool("sub_layer_level_present_flag[" + i2 + "]");
        }
        if (i > 0) {
            for (i2 = i; i2 < RASL_N; i2 += TRAIL_R) {
                cAVLCReader.readU(TSA_N, "reserved_zero_2bits");
            }
        }
        int[] iArr = new int[i];
        boolean[] zArr4 = new boolean[i];
        int[] iArr2 = new int[i];
        boolean[][] zArr5 = (boolean[][]) java.lang.reflect.Array.newInstance(Boolean.TYPE, new int[]{i, VPS_NUT});
        boolean[] zArr6 = new boolean[i];
        boolean[] zArr7 = new boolean[i];
        boolean[] zArr8 = new boolean[i];
        boolean[] zArr9 = new boolean[i];
        int[] iArr3 = new int[i];
        for (int i3 = TRAIL_N; i3 < i; i3 += TRAIL_R) {
            if (zArr2[i3]) {
                iArr[i3] = cAVLCReader.readU(TSA_N, "sub_layer_profile_space[" + i3 + "]");
                zArr4[i3] = cAVLCReader.readBool("sub_layer_tier_flag[" + i3 + "]");
                iArr2[i3] = cAVLCReader.readU(STSA_R, "sub_layer_profile_idc[" + i3 + "]");
                for (int i4 = TRAIL_N; i4 < VPS_NUT; i4 += TRAIL_R) {
                    zArr5[i3][i4] = cAVLCReader.readBool("sub_layer_profile_compatibility_flag[" + i3 + "][" + i4 + "]");
                }
                zArr6[i3] = cAVLCReader.readBool("sub_layer_progressive_source_flag[" + i3 + "]");
                zArr7[i3] = cAVLCReader.readBool("sub_layer_interlaced_source_flag[" + i3 + "]");
                zArr8[i3] = cAVLCReader.readBool("sub_layer_non_packed_constraint_flag[" + i3 + "]");
                zArr9[i3] = cAVLCReader.readBool("sub_layer_frame_only_constraint_flag[" + i3 + "]");
                cAVLCReader.readNBit(RSV_NVCL44, "reserved");
            }
            if (zArr3[i3]) {
                iArr3[i3] = cAVLCReader.readU(RASL_N, "sub_layer_level_idc");
            }
        }
    }

    void sub_layer_hrd_parameters(int i, int i2, boolean z, CAVLCReader cAVLCReader) {
        int[] iArr = new int[i2];
        int[] iArr2 = new int[i2];
        int[] iArr3 = new int[i2];
        int[] iArr4 = new int[i2];
        boolean[] zArr = new boolean[i2];
        for (int i3 = TRAIL_N; i3 <= i2; i3 += TRAIL_R) {
            iArr[i3] = cAVLCReader.readUE("bit_rate_value_minus1[" + i3 + "]");
            iArr2[i3] = cAVLCReader.readUE("cpb_size_value_minus1[" + i3 + "]");
            if (z) {
                iArr3[i3] = cAVLCReader.readUE("cpb_size_du_value_minus1[" + i3 + "]");
                iArr4[i3] = cAVLCReader.readUE("bit_rate_du_value_minus1[" + i3 + "]");
            }
            zArr[i3] = cAVLCReader.readBool("cbr_flag[" + i3 + "]");
        }
    }
}
