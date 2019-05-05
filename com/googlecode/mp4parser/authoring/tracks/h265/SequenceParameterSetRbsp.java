package com.googlecode.mp4parser.authoring.tracks.h265;

import com.googlecode.mp4parser.h264.read.CAVLCReader;
import com.hanista.mobogram.tgnet.TLRPC;
import java.io.InputStream;
import java.lang.reflect.Array;

public class SequenceParameterSetRbsp {
    public SequenceParameterSetRbsp(InputStream inputStream) {
        int i = 0;
        CAVLCReader cAVLCReader = new CAVLCReader(inputStream);
        int readNBit = (int) cAVLCReader.readNBit(4, "sps_video_parameter_set_id");
        int readNBit2 = (int) cAVLCReader.readNBit(3, "sps_max_sub_layers_minus1");
        cAVLCReader.readBool("sps_temporal_id_nesting_flag");
        profile_tier_level(readNBit2, cAVLCReader);
        cAVLCReader.readUE("sps_seq_parameter_set_id");
        if (cAVLCReader.readUE("chroma_format_idc") == 3) {
            cAVLCReader.read1Bit();
            cAVLCReader.readUE("pic_width_in_luma_samples");
            cAVLCReader.readUE("pic_width_in_luma_samples");
            if (cAVLCReader.readBool("conformance_window_flag")) {
                cAVLCReader.readUE("conf_win_left_offset");
                cAVLCReader.readUE("conf_win_right_offset");
                cAVLCReader.readUE("conf_win_top_offset");
                cAVLCReader.readUE("conf_win_bottom_offset");
            }
        }
        cAVLCReader.readUE("bit_depth_luma_minus8");
        cAVLCReader.readUE("bit_depth_chroma_minus8");
        cAVLCReader.readUE("log2_max_pic_order_cnt_lsb_minus4");
        boolean readBool = cAVLCReader.readBool("sps_sub_layer_ordering_info_present_flag");
        readNBit = (readNBit2 - (readBool ? 0 : readNBit2)) + 1;
        int[] iArr = new int[readNBit];
        int[] iArr2 = new int[readNBit];
        int[] iArr3 = new int[readNBit];
        if (!readBool) {
            i = readNBit2;
        }
        while (i <= readNBit2) {
            iArr[i] = cAVLCReader.readUE("sps_max_dec_pic_buffering_minus1[" + i + "]");
            iArr2[i] = cAVLCReader.readUE("sps_max_num_reorder_pics[" + i + "]");
            iArr3[i] = cAVLCReader.readUE("sps_max_latency_increase_plus1[" + i + "]");
            i++;
        }
        cAVLCReader.readUE("log2_min_luma_coding_block_size_minus3");
        cAVLCReader.readUE("log2_diff_max_min_luma_coding_block_size");
        cAVLCReader.readUE("log2_min_transform_block_size_minus2");
        cAVLCReader.readUE("log2_diff_max_min_transform_block_size");
        cAVLCReader.readUE("max_transform_hierarchy_depth_inter");
        cAVLCReader.readUE("max_transform_hierarchy_depth_intra");
        if (cAVLCReader.readBool("scaling_list_enabled_flag") && cAVLCReader.readBool("sps_scaling_list_data_present_flag")) {
            scaling_list_data(cAVLCReader);
        }
        cAVLCReader.readBool("amp_enabled_flag");
        cAVLCReader.readBool("sample_adaptive_offset_enabled_flag");
        if (cAVLCReader.readBool("pcm_enabled_flag")) {
            readNBit = (int) cAVLCReader.readNBit(4, "pcm_sample_bit_depth_luma_minus1");
            readNBit = (int) cAVLCReader.readNBit(4, "pcm_sample_bit_depth_chroma_minus1");
            cAVLCReader.readUE("log2_min_pcm_luma_coding_block_size_minus3");
        }
    }

    private void profile_tier_level(int i, CAVLCReader cAVLCReader) {
        int i2;
        cAVLCReader.readU(2, "general_profile_space");
        cAVLCReader.readBool("general_tier_flag");
        cAVLCReader.readU(5, "general_profile_idc");
        boolean[] zArr = new boolean[32];
        for (i2 = 0; i2 < 32; i2++) {
            zArr[i2] = cAVLCReader.readBool();
        }
        cAVLCReader.readBool("general_progressive_source_flag");
        cAVLCReader.readBool("general_interlaced_source_flag");
        cAVLCReader.readBool("general_non_packed_constraint_flag");
        cAVLCReader.readBool("general_frame_only_constraint_flag");
        cAVLCReader.readNBit(44, "general_reserved_zero_44bits");
        cAVLCReader.readByte();
        boolean[] zArr2 = new boolean[i];
        boolean[] zArr3 = new boolean[i];
        for (i2 = 0; i2 < i; i2++) {
            zArr2[i2] = cAVLCReader.readBool("sub_layer_profile_present_flag[" + i2 + "]");
            zArr3[i2] = cAVLCReader.readBool("sub_layer_level_present_flag[" + i2 + "]");
        }
        if (i > 0) {
            int[] iArr = new int[8];
            for (i2 = i; i2 < 8; i2++) {
                iArr[i2] = cAVLCReader.readU(2, "reserved_zero_2bits[" + i2 + "]");
            }
        }
        int[] iArr2 = new int[i];
        boolean[] zArr4 = new boolean[i];
        int[] iArr3 = new int[i];
        boolean[][] zArr5 = (boolean[][]) Array.newInstance(Boolean.TYPE, new int[]{i, 32});
        boolean[] zArr6 = new boolean[i];
        boolean[] zArr7 = new boolean[i];
        boolean[] zArr8 = new boolean[i];
        boolean[] zArr9 = new boolean[i];
        long[] jArr = new long[i];
        int[] iArr4 = new int[i];
        for (int i3 = 0; i3 < i; i3++) {
            if (zArr2[i3]) {
                iArr2[i3] = cAVLCReader.readU(2, "sub_layer_profile_space[" + i3 + "]");
                String str = "]";
                CAVLCReader cAVLCReader2 = cAVLCReader;
                zArr4[i3] = cAVLCReader2.readBool("sub_layer_tier_flag[" + i3 + r16);
                iArr3[i3] = cAVLCReader.readU(5, "sub_layer_profile_idc[" + i3 + "]");
                for (int i4 = 0; i4 < 32; i4++) {
                    zArr5[i3][i4] = cAVLCReader.readBool("sub_layer_profile_compatibility_flag[" + i3 + "][" + i4 + "]");
                }
                str = "]";
                cAVLCReader2 = cAVLCReader;
                zArr6[i3] = cAVLCReader2.readBool("sub_layer_progressive_source_flag[" + i3 + r16);
                str = "]";
                cAVLCReader2 = cAVLCReader;
                zArr7[i3] = cAVLCReader2.readBool("sub_layer_interlaced_source_flag[" + i3 + r16);
                str = "]";
                cAVLCReader2 = cAVLCReader;
                zArr8[i3] = cAVLCReader2.readBool("sub_layer_non_packed_constraint_flag[" + i3 + r16);
                str = "]";
                cAVLCReader2 = cAVLCReader;
                zArr9[i3] = cAVLCReader2.readBool("sub_layer_frame_only_constraint_flag[" + i3 + r16);
                jArr[i3] = cAVLCReader.readNBit(44);
            }
            if (zArr3[i3]) {
                iArr4[i3] = cAVLCReader.readU(8, "sub_layer_level_idc[" + i3 + "]");
            }
        }
    }

    private void scaling_list_data(CAVLCReader cAVLCReader) {
        boolean[][] zArr = new boolean[4][];
        int[][] iArr = new int[4][];
        int[][] iArr2 = new int[2][];
        int[][][] iArr3 = new int[4][][];
        int i = 0;
        while (i < 4) {
            int i2 = 0;
            while (true) {
                if (i2 >= (i == 3 ? 2 : 6)) {
                    break;
                }
                zArr[i] = new boolean[(i == 3 ? 2 : 6)];
                iArr[i] = new int[(i == 3 ? 2 : 6)];
                iArr3[i] = new int[(i == 3 ? 2 : 6)][];
                zArr[i][i2] = cAVLCReader.readBool();
                if (zArr[i][i2]) {
                    int i3 = 8;
                    int min = Math.min(64, 1 << ((i << 1) + 4));
                    if (i > 1) {
                        iArr2[i - 2][i2] = cAVLCReader.readSE("scaling_list_dc_coef_minus8[" + i + "- 2][" + i2 + "]");
                        i3 = iArr2[i - 2][i2] + 8;
                    }
                    iArr3[i][i2] = new int[min];
                    int i4 = i3;
                    for (i3 = 0; i3 < min; i3++) {
                        i4 = ((i4 + cAVLCReader.readSE("scaling_list_delta_coef ")) + TLRPC.USER_FLAG_UNUSED2) % TLRPC.USER_FLAG_UNUSED2;
                        iArr3[i][i2][i3] = i4;
                    }
                } else {
                    iArr[i][i2] = cAVLCReader.readUE("scaling_list_pred_matrix_id_delta[" + i + "][" + i2 + "]");
                }
                i2++;
            }
            i++;
        }
    }
}
