package com.googlecode.mp4parser.boxes.mp4.objectdescriptors;

import android.support.v4.view.MotionEventCompat;
import com.coremedia.iso.Hex;
import com.coremedia.iso.IsoTypeWriter;
import com.googlecode.mp4parser.authoring.tracks.h265.NalUnitTypes;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.exoplayer.upstream.UdpDataSource;
import com.hanista.mobogram.messenger.volley.Request.Method;
import com.hanista.mobogram.tgnet.TLRPC;
import com.hanista.mobogram.ui.Components.VideoPlayer;
import com.hanista.mobogram.util.shamsicalendar.ShamsiCalendar;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Descriptor(objectTypeIndication = 64, tags = {5})
public class AudioSpecificConfig extends BaseDescriptor {
    public static Map<Integer, String> audioObjectTypeMap;
    public static Map<Integer, Integer> samplingFrequencyIndexMap;
    public boolean aacScalefactorDataResilienceFlag;
    public boolean aacSectionDataResilienceFlag;
    public boolean aacSpectralDataResilienceFlag;
    public int audioObjectType;
    public int channelConfiguration;
    byte[] configBytes;
    public int coreCoderDelay;
    public int dependsOnCoreCoder;
    public int directMapping;
    public ELDSpecificConfig eldSpecificConfig;
    public int epConfig;
    public int erHvxcExtensionFlag;
    public int extensionAudioObjectType;
    public int extensionChannelConfiguration;
    public int extensionFlag;
    public int extensionFlag3;
    public int extensionSamplingFrequency;
    public int extensionSamplingFrequencyIndex;
    public int fillBits;
    public int frameLengthFlag;
    public boolean gaSpecificConfig;
    public int hilnContMode;
    public int hilnEnhaLayer;
    public int hilnEnhaQuantMode;
    public int hilnFrameLength;
    public int hilnMaxNumLine;
    public int hilnQuantMode;
    public int hilnSampleRateCode;
    public int hvxcRateMode;
    public int hvxcVarMode;
    public int isBaseLayer;
    public int layerNr;
    public int layer_length;
    public int numOfSubFrame;
    public int paraExtensionFlag;
    public int paraMode;
    public boolean parametricSpecificConfig;
    public boolean psPresentFlag;
    public int sacPayloadEmbedding;
    public int samplingFrequency;
    public int samplingFrequencyIndex;
    public boolean sbrPresentFlag;
    public int syncExtensionType;
    public int var_ScalableFlag;

    public class ELDSpecificConfig {
        private static final int ELDEXT_TERM = 0;
        public boolean aacScalefactorDataResilienceFlag;
        public boolean aacSectionDataResilienceFlag;
        public boolean aacSpectralDataResilienceFlag;
        public boolean frameLengthFlag;
        public boolean ldSbrCrcFlag;
        public boolean ldSbrPresentFlag;
        public boolean ldSbrSamplingRate;

        public ELDSpecificConfig(int i, BitReaderBuffer bitReaderBuffer) {
            this.frameLengthFlag = bitReaderBuffer.readBool();
            this.aacSectionDataResilienceFlag = bitReaderBuffer.readBool();
            this.aacScalefactorDataResilienceFlag = bitReaderBuffer.readBool();
            this.aacSpectralDataResilienceFlag = bitReaderBuffer.readBool();
            this.ldSbrPresentFlag = bitReaderBuffer.readBool();
            if (this.ldSbrPresentFlag) {
                this.ldSbrSamplingRate = bitReaderBuffer.readBool();
                this.ldSbrCrcFlag = bitReaderBuffer.readBool();
                ld_sbr_header(i, bitReaderBuffer);
            }
            while (bitReaderBuffer.readBits(4) != 0) {
                int readBits;
                int readBits2 = bitReaderBuffer.readBits(4);
                if (readBits2 == 15) {
                    readBits = bitReaderBuffer.readBits(8);
                    int i2 = readBits;
                    readBits = readBits2 + readBits;
                    readBits2 = i2;
                } else {
                    readBits = readBits2;
                    readBits2 = 0;
                }
                if (readBits2 == 255) {
                    readBits += bitReaderBuffer.readBits(16);
                }
                for (readBits2 = 0; readBits2 < readBits; readBits2++) {
                    bitReaderBuffer.readBits(8);
                }
            }
        }

        public void ld_sbr_header(int i, BitReaderBuffer bitReaderBuffer) {
            int i2;
            int i3 = 0;
            switch (i) {
                case VideoPlayer.TYPE_AUDIO /*1*/:
                case VideoPlayer.STATE_PREPARING /*2*/:
                    i2 = 1;
                    break;
                case VideoPlayer.STATE_BUFFERING /*3*/:
                    i2 = 2;
                    break;
                case VideoPlayer.STATE_READY /*4*/:
                case VideoPlayer.STATE_ENDED /*5*/:
                case Method.TRACE /*6*/:
                    i2 = 3;
                    break;
                case Method.PATCH /*7*/:
                    i2 = 4;
                    break;
                default:
                    i2 = 0;
                    break;
            }
            while (i3 < i2) {
                sbr_header com_googlecode_mp4parser_boxes_mp4_objectdescriptors_AudioSpecificConfig_sbr_header = new sbr_header(bitReaderBuffer);
                i3++;
            }
        }
    }

    public class sbr_header {
        public boolean bs_alter_scale;
        public boolean bs_amp_res;
        public int bs_freq_scale;
        public boolean bs_header_extra_1;
        public boolean bs_header_extra_2;
        public boolean bs_interpol_freq;
        public int bs_limiter_bands;
        public int bs_limiter_gains;
        public int bs_noise_bands;
        public int bs_reserved;
        public boolean bs_smoothing_mode;
        public int bs_start_freq;
        public int bs_stop_freq;
        public int bs_xover_band;

        public sbr_header(BitReaderBuffer bitReaderBuffer) {
            this.bs_amp_res = bitReaderBuffer.readBool();
            this.bs_start_freq = bitReaderBuffer.readBits(4);
            this.bs_stop_freq = bitReaderBuffer.readBits(4);
            this.bs_xover_band = bitReaderBuffer.readBits(3);
            this.bs_reserved = bitReaderBuffer.readBits(2);
            this.bs_header_extra_1 = bitReaderBuffer.readBool();
            this.bs_header_extra_2 = bitReaderBuffer.readBool();
            if (this.bs_header_extra_1) {
                this.bs_freq_scale = bitReaderBuffer.readBits(2);
                this.bs_alter_scale = bitReaderBuffer.readBool();
                this.bs_noise_bands = bitReaderBuffer.readBits(2);
            }
            if (this.bs_header_extra_2) {
                this.bs_limiter_bands = bitReaderBuffer.readBits(2);
                this.bs_limiter_gains = bitReaderBuffer.readBits(2);
                this.bs_interpol_freq = bitReaderBuffer.readBool();
            }
            this.bs_smoothing_mode = bitReaderBuffer.readBool();
        }
    }

    static {
        samplingFrequencyIndexMap = new HashMap();
        audioObjectTypeMap = new HashMap();
        samplingFrequencyIndexMap.put(Integer.valueOf(0), Integer.valueOf(96000));
        samplingFrequencyIndexMap.put(Integer.valueOf(1), Integer.valueOf(88200));
        samplingFrequencyIndexMap.put(Integer.valueOf(2), Integer.valueOf(64000));
        samplingFrequencyIndexMap.put(Integer.valueOf(3), Integer.valueOf(48000));
        samplingFrequencyIndexMap.put(Integer.valueOf(4), Integer.valueOf(44100));
        samplingFrequencyIndexMap.put(Integer.valueOf(5), Integer.valueOf(32000));
        samplingFrequencyIndexMap.put(Integer.valueOf(6), Integer.valueOf(24000));
        samplingFrequencyIndexMap.put(Integer.valueOf(7), Integer.valueOf(22050));
        samplingFrequencyIndexMap.put(Integer.valueOf(8), Integer.valueOf(16000));
        samplingFrequencyIndexMap.put(Integer.valueOf(9), Integer.valueOf(12000));
        samplingFrequencyIndexMap.put(Integer.valueOf(10), Integer.valueOf(11025));
        samplingFrequencyIndexMap.put(Integer.valueOf(11), Integer.valueOf(UdpDataSource.DEAFULT_SOCKET_TIMEOUT_MILLIS));
        audioObjectTypeMap.put(Integer.valueOf(1), "AAC main");
        audioObjectTypeMap.put(Integer.valueOf(2), "AAC LC");
        audioObjectTypeMap.put(Integer.valueOf(3), "AAC SSR");
        audioObjectTypeMap.put(Integer.valueOf(4), "AAC LTP");
        audioObjectTypeMap.put(Integer.valueOf(5), "SBR");
        audioObjectTypeMap.put(Integer.valueOf(6), "AAC Scalable");
        audioObjectTypeMap.put(Integer.valueOf(7), "TwinVQ");
        audioObjectTypeMap.put(Integer.valueOf(8), "CELP");
        audioObjectTypeMap.put(Integer.valueOf(9), "HVXC");
        audioObjectTypeMap.put(Integer.valueOf(10), "(reserved)");
        audioObjectTypeMap.put(Integer.valueOf(11), "(reserved)");
        audioObjectTypeMap.put(Integer.valueOf(12), "TTSI");
        audioObjectTypeMap.put(Integer.valueOf(13), "Main synthetic");
        audioObjectTypeMap.put(Integer.valueOf(14), "Wavetable synthesis");
        audioObjectTypeMap.put(Integer.valueOf(15), "General MIDI");
        audioObjectTypeMap.put(Integer.valueOf(16), "Algorithmic Synthesis and Audio FX");
        audioObjectTypeMap.put(Integer.valueOf(17), "ER AAC LC");
        audioObjectTypeMap.put(Integer.valueOf(18), "(reserved)");
        audioObjectTypeMap.put(Integer.valueOf(19), "ER AAC LTP");
        audioObjectTypeMap.put(Integer.valueOf(20), "ER AAC Scalable");
        audioObjectTypeMap.put(Integer.valueOf(21), "ER TwinVQ");
        audioObjectTypeMap.put(Integer.valueOf(22), "ER BSAC");
        audioObjectTypeMap.put(Integer.valueOf(23), "ER AAC LD");
        audioObjectTypeMap.put(Integer.valueOf(24), "ER CELP");
        audioObjectTypeMap.put(Integer.valueOf(25), "ER HVXC");
        audioObjectTypeMap.put(Integer.valueOf(26), "ER HILN");
        audioObjectTypeMap.put(Integer.valueOf(27), "ER Parametric");
        audioObjectTypeMap.put(Integer.valueOf(28), "SSC");
        audioObjectTypeMap.put(Integer.valueOf(29), "PS");
        audioObjectTypeMap.put(Integer.valueOf(30), "MPEG Surround");
        audioObjectTypeMap.put(Integer.valueOf(31), "(escape)");
        audioObjectTypeMap.put(Integer.valueOf(32), "Layer-1");
        audioObjectTypeMap.put(Integer.valueOf(33), "Layer-2");
        audioObjectTypeMap.put(Integer.valueOf(34), "Layer-3");
        audioObjectTypeMap.put(Integer.valueOf(35), "DST");
        audioObjectTypeMap.put(Integer.valueOf(36), "ALS");
        audioObjectTypeMap.put(Integer.valueOf(37), "SLS");
        audioObjectTypeMap.put(Integer.valueOf(38), "SLS non-core");
        audioObjectTypeMap.put(Integer.valueOf(39), "ER AAC ELD");
        audioObjectTypeMap.put(Integer.valueOf(40), "SMR Simple");
        audioObjectTypeMap.put(Integer.valueOf(41), "SMR Main");
    }

    private int gaSpecificConfigSize() {
        return 0;
    }

    private int getAudioObjectType(BitReaderBuffer bitReaderBuffer) {
        int readBits = bitReaderBuffer.readBits(5);
        return readBits == 31 ? bitReaderBuffer.readBits(6) + 32 : readBits;
    }

    private void parseErHvxcConfig(int i, int i2, int i3, BitReaderBuffer bitReaderBuffer) {
        this.hvxcVarMode = bitReaderBuffer.readBits(1);
        this.hvxcRateMode = bitReaderBuffer.readBits(2);
        this.erHvxcExtensionFlag = bitReaderBuffer.readBits(1);
        if (this.erHvxcExtensionFlag == 1) {
            this.var_ScalableFlag = bitReaderBuffer.readBits(1);
        }
    }

    private void parseGaSpecificConfig(int i, int i2, int i3, BitReaderBuffer bitReaderBuffer) {
        this.frameLengthFlag = bitReaderBuffer.readBits(1);
        this.dependsOnCoreCoder = bitReaderBuffer.readBits(1);
        if (this.dependsOnCoreCoder == 1) {
            this.coreCoderDelay = bitReaderBuffer.readBits(14);
        }
        this.extensionFlag = bitReaderBuffer.readBits(1);
        if (i2 == 0) {
            throw new UnsupportedOperationException("can't parse program_config_element yet");
        }
        if (i3 == 6 || i3 == 20) {
            this.layerNr = bitReaderBuffer.readBits(3);
        }
        if (this.extensionFlag == 1) {
            if (i3 == 22) {
                this.numOfSubFrame = bitReaderBuffer.readBits(5);
                this.layer_length = bitReaderBuffer.readBits(11);
            }
            if (i3 == 17 || i3 == 19 || i3 == 20 || i3 == 23) {
                this.aacSectionDataResilienceFlag = bitReaderBuffer.readBool();
                this.aacScalefactorDataResilienceFlag = bitReaderBuffer.readBool();
                this.aacSpectralDataResilienceFlag = bitReaderBuffer.readBool();
            }
            this.extensionFlag3 = bitReaderBuffer.readBits(1);
        }
        this.gaSpecificConfig = true;
    }

    private void parseHilnConfig(int i, int i2, int i3, BitReaderBuffer bitReaderBuffer) {
        this.hilnQuantMode = bitReaderBuffer.readBits(1);
        this.hilnMaxNumLine = bitReaderBuffer.readBits(8);
        this.hilnSampleRateCode = bitReaderBuffer.readBits(4);
        this.hilnFrameLength = bitReaderBuffer.readBits(12);
        this.hilnContMode = bitReaderBuffer.readBits(2);
    }

    private void parseHilnEnexConfig(int i, int i2, int i3, BitReaderBuffer bitReaderBuffer) {
        this.hilnEnhaLayer = bitReaderBuffer.readBits(1);
        if (this.hilnEnhaLayer == 1) {
            this.hilnEnhaQuantMode = bitReaderBuffer.readBits(2);
        }
    }

    private void parseParaConfig(int i, int i2, int i3, BitReaderBuffer bitReaderBuffer) {
        this.paraMode = bitReaderBuffer.readBits(2);
        if (this.paraMode != 1) {
            parseErHvxcConfig(i, i2, i3, bitReaderBuffer);
        }
        if (this.paraMode != 0) {
            parseHilnConfig(i, i2, i3, bitReaderBuffer);
        }
        this.paraExtensionFlag = bitReaderBuffer.readBits(1);
        this.parametricSpecificConfig = true;
    }

    private void parseParametricSpecificConfig(int i, int i2, int i3, BitReaderBuffer bitReaderBuffer) {
        this.isBaseLayer = bitReaderBuffer.readBits(1);
        if (this.isBaseLayer == 1) {
            parseParaConfig(i, i2, i3, bitReaderBuffer);
        } else {
            parseHilnEnexConfig(i, i2, i3, bitReaderBuffer);
        }
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        AudioSpecificConfig audioSpecificConfig = (AudioSpecificConfig) obj;
        return this.aacScalefactorDataResilienceFlag != audioSpecificConfig.aacScalefactorDataResilienceFlag ? false : this.aacSectionDataResilienceFlag != audioSpecificConfig.aacSectionDataResilienceFlag ? false : this.aacSpectralDataResilienceFlag != audioSpecificConfig.aacSpectralDataResilienceFlag ? false : this.audioObjectType != audioSpecificConfig.audioObjectType ? false : this.channelConfiguration != audioSpecificConfig.channelConfiguration ? false : this.coreCoderDelay != audioSpecificConfig.coreCoderDelay ? false : this.dependsOnCoreCoder != audioSpecificConfig.dependsOnCoreCoder ? false : this.directMapping != audioSpecificConfig.directMapping ? false : this.epConfig != audioSpecificConfig.epConfig ? false : this.erHvxcExtensionFlag != audioSpecificConfig.erHvxcExtensionFlag ? false : this.extensionAudioObjectType != audioSpecificConfig.extensionAudioObjectType ? false : this.extensionChannelConfiguration != audioSpecificConfig.extensionChannelConfiguration ? false : this.extensionFlag != audioSpecificConfig.extensionFlag ? false : this.extensionFlag3 != audioSpecificConfig.extensionFlag3 ? false : this.extensionSamplingFrequency != audioSpecificConfig.extensionSamplingFrequency ? false : this.extensionSamplingFrequencyIndex != audioSpecificConfig.extensionSamplingFrequencyIndex ? false : this.fillBits != audioSpecificConfig.fillBits ? false : this.frameLengthFlag != audioSpecificConfig.frameLengthFlag ? false : this.gaSpecificConfig != audioSpecificConfig.gaSpecificConfig ? false : this.hilnContMode != audioSpecificConfig.hilnContMode ? false : this.hilnEnhaLayer != audioSpecificConfig.hilnEnhaLayer ? false : this.hilnEnhaQuantMode != audioSpecificConfig.hilnEnhaQuantMode ? false : this.hilnFrameLength != audioSpecificConfig.hilnFrameLength ? false : this.hilnMaxNumLine != audioSpecificConfig.hilnMaxNumLine ? false : this.hilnQuantMode != audioSpecificConfig.hilnQuantMode ? false : this.hilnSampleRateCode != audioSpecificConfig.hilnSampleRateCode ? false : this.hvxcRateMode != audioSpecificConfig.hvxcRateMode ? false : this.hvxcVarMode != audioSpecificConfig.hvxcVarMode ? false : this.isBaseLayer != audioSpecificConfig.isBaseLayer ? false : this.layerNr != audioSpecificConfig.layerNr ? false : this.layer_length != audioSpecificConfig.layer_length ? false : this.numOfSubFrame != audioSpecificConfig.numOfSubFrame ? false : this.paraExtensionFlag != audioSpecificConfig.paraExtensionFlag ? false : this.paraMode != audioSpecificConfig.paraMode ? false : this.parametricSpecificConfig != audioSpecificConfig.parametricSpecificConfig ? false : this.psPresentFlag != audioSpecificConfig.psPresentFlag ? false : this.sacPayloadEmbedding != audioSpecificConfig.sacPayloadEmbedding ? false : this.samplingFrequency != audioSpecificConfig.samplingFrequency ? false : this.samplingFrequencyIndex != audioSpecificConfig.samplingFrequencyIndex ? false : this.sbrPresentFlag != audioSpecificConfig.sbrPresentFlag ? false : this.syncExtensionType != audioSpecificConfig.syncExtensionType ? false : this.var_ScalableFlag != audioSpecificConfig.var_ScalableFlag ? false : Arrays.equals(this.configBytes, audioSpecificConfig.configBytes);
    }

    public int getAudioObjectType() {
        return this.audioObjectType;
    }

    public int getChannelConfiguration() {
        return this.channelConfiguration;
    }

    public byte[] getConfigBytes() {
        return this.configBytes;
    }

    public int getExtensionAudioObjectType() {
        return this.extensionAudioObjectType;
    }

    public int getSamplingFrequency() {
        return this.samplingFrequencyIndex == 15 ? this.samplingFrequency : ((Integer) samplingFrequencyIndexMap.get(Integer.valueOf(this.samplingFrequencyIndex))).intValue();
    }

    public int hashCode() {
        int i = 1;
        int hashCode = ((((((((((((((((((((((((((((((this.gaSpecificConfig ? 1 : 0) + (((((this.aacSpectralDataResilienceFlag ? 1 : 0) + (((this.aacScalefactorDataResilienceFlag ? 1 : 0) + (((this.aacSectionDataResilienceFlag ? 1 : 0) + (((((((((((((((((((((((((((((((((this.psPresentFlag ? 1 : 0) + (((this.sbrPresentFlag ? 1 : 0) + ((((((((((((this.configBytes != null ? Arrays.hashCode(this.configBytes) : 0) * 31) + this.audioObjectType) * 31) + this.samplingFrequencyIndex) * 31) + this.samplingFrequency) * 31) + this.channelConfiguration) * 31) + this.extensionAudioObjectType) * 31)) * 31)) * 31) + this.extensionSamplingFrequencyIndex) * 31) + this.extensionSamplingFrequency) * 31) + this.extensionChannelConfiguration) * 31) + this.sacPayloadEmbedding) * 31) + this.fillBits) * 31) + this.epConfig) * 31) + this.directMapping) * 31) + this.syncExtensionType) * 31) + this.frameLengthFlag) * 31) + this.dependsOnCoreCoder) * 31) + this.coreCoderDelay) * 31) + this.extensionFlag) * 31) + this.layerNr) * 31) + this.numOfSubFrame) * 31) + this.layer_length) * 31)) * 31)) * 31)) * 31) + this.extensionFlag3) * 31)) * 31) + this.isBaseLayer) * 31) + this.paraMode) * 31) + this.paraExtensionFlag) * 31) + this.hvxcVarMode) * 31) + this.hvxcRateMode) * 31) + this.erHvxcExtensionFlag) * 31) + this.var_ScalableFlag) * 31) + this.hilnQuantMode) * 31) + this.hilnMaxNumLine) * 31) + this.hilnSampleRateCode) * 31) + this.hilnFrameLength) * 31) + this.hilnContMode) * 31) + this.hilnEnhaLayer) * 31) + this.hilnEnhaQuantMode) * 31;
        if (!this.parametricSpecificConfig) {
            i = 0;
        }
        return hashCode + i;
    }

    public void parseDetail(ByteBuffer byteBuffer) {
        ByteBuffer slice = byteBuffer.slice();
        slice.limit(this.sizeOfInstance);
        byteBuffer.position(byteBuffer.position() + this.sizeOfInstance);
        this.configBytes = new byte[this.sizeOfInstance];
        slice.get(this.configBytes);
        slice.rewind();
        BitReaderBuffer bitReaderBuffer = new BitReaderBuffer(slice);
        this.audioObjectType = getAudioObjectType(bitReaderBuffer);
        this.samplingFrequencyIndex = bitReaderBuffer.readBits(4);
        if (this.samplingFrequencyIndex == 15) {
            this.samplingFrequency = bitReaderBuffer.readBits(24);
        }
        this.channelConfiguration = bitReaderBuffer.readBits(4);
        if (this.audioObjectType == 5 || this.audioObjectType == 29) {
            this.extensionAudioObjectType = 5;
            this.sbrPresentFlag = true;
            if (this.audioObjectType == 29) {
                this.psPresentFlag = true;
            }
            this.extensionSamplingFrequencyIndex = bitReaderBuffer.readBits(4);
            if (this.extensionSamplingFrequencyIndex == 15) {
                this.extensionSamplingFrequency = bitReaderBuffer.readBits(24);
            }
            this.audioObjectType = getAudioObjectType(bitReaderBuffer);
            if (this.audioObjectType == 22) {
                this.extensionChannelConfiguration = bitReaderBuffer.readBits(4);
            }
        } else {
            this.extensionAudioObjectType = 0;
        }
        switch (this.audioObjectType) {
            case VideoPlayer.TYPE_AUDIO /*1*/:
            case VideoPlayer.STATE_PREPARING /*2*/:
            case VideoPlayer.STATE_BUFFERING /*3*/:
            case VideoPlayer.STATE_READY /*4*/:
            case Method.TRACE /*6*/:
            case Method.PATCH /*7*/:
            case C0338R.styleable.PromptView_primaryTextTypeface /*17*/:
            case C0338R.styleable.PromptView_secondaryTextColour /*19*/:
            case C0338R.styleable.PromptView_secondaryTextFontFamily /*20*/:
            case C0338R.styleable.PromptView_secondaryTextSize /*21*/:
            case C0338R.styleable.PromptView_secondaryTextStyle /*22*/:
            case C0338R.styleable.PromptView_secondaryTextTypeface /*23*/:
                parseGaSpecificConfig(this.samplingFrequencyIndex, this.channelConfiguration, this.audioObjectType, bitReaderBuffer);
                break;
            case TLRPC.USER_FLAG_USERNAME /*8*/:
                throw new UnsupportedOperationException("can't parse CelpSpecificConfig yet");
            case C0338R.styleable.PromptView_iconTint /*9*/:
                throw new UnsupportedOperationException("can't parse HvxcSpecificConfig yet");
            case Atom.FULL_HEADER_SIZE /*12*/:
                throw new UnsupportedOperationException("can't parse TTSSpecificConfig yet");
            case ShamsiCalendar.CURRENT_CENTURY /*13*/:
            case C0338R.styleable.PromptView_primaryTextFontFamily /*14*/:
            case C0338R.styleable.PromptView_primaryTextSize /*15*/:
            case TLRPC.USER_FLAG_PHONE /*16*/:
                throw new UnsupportedOperationException("can't parse StructuredAudioSpecificConfig yet");
            case C0338R.styleable.PromptView_target /*24*/:
                throw new UnsupportedOperationException("can't parse ErrorResilientCelpSpecificConfig yet");
            case C0338R.styleable.PromptView_textPadding /*25*/:
                throw new UnsupportedOperationException("can't parse ErrorResilientHvxcSpecificConfig yet");
            case C0338R.styleable.PromptView_textSeparation /*26*/:
            case OggUtil.PAGE_HEADER_SIZE /*27*/:
                parseParametricSpecificConfig(this.samplingFrequencyIndex, this.channelConfiguration, this.audioObjectType, bitReaderBuffer);
                break;
            case NalUnitTypes.NAL_TYPE_RSV_VCL28 /*28*/:
                throw new UnsupportedOperationException("can't parse SSCSpecificConfig yet");
            case NalUnitTypes.NAL_TYPE_RSV_VCL30 /*30*/:
                this.sacPayloadEmbedding = bitReaderBuffer.readBits(1);
                throw new UnsupportedOperationException("can't parse SpatialSpecificConfig yet");
            case TLRPC.USER_FLAG_PHOTO /*32*/:
            case NalUnitTypes.NAL_TYPE_SPS_NUT /*33*/:
            case NalUnitTypes.NAL_TYPE_PPS_NUT /*34*/:
                throw new UnsupportedOperationException("can't parse MPEG_1_2_SpecificConfig yet");
            case NalUnitTypes.NAL_TYPE_AUD_NUT /*35*/:
                throw new UnsupportedOperationException("can't parse DSTSpecificConfig yet");
            case NalUnitTypes.NAL_TYPE_EOS_NUT /*36*/:
                this.fillBits = bitReaderBuffer.readBits(5);
                throw new UnsupportedOperationException("can't parse ALSSpecificConfig yet");
            case NalUnitTypes.NAL_TYPE_EOB_NUT /*37*/:
            case NalUnitTypes.NAL_TYPE_FD_NUT /*38*/:
                throw new UnsupportedOperationException("can't parse SLSSpecificConfig yet");
            case NalUnitTypes.NAL_TYPE_PREFIX_SEI_NUT /*39*/:
                this.eldSpecificConfig = new ELDSpecificConfig(this.channelConfiguration, bitReaderBuffer);
                break;
            case MotionEventCompat.AXIS_GENERIC_9 /*40*/:
            case NalUnitTypes.NAL_TYPE_RSV_NVCL41 /*41*/:
                throw new UnsupportedOperationException("can't parse SymbolicMusicSpecificConfig yet");
        }
        switch (this.audioObjectType) {
            case C0338R.styleable.PromptView_primaryTextTypeface /*17*/:
            case C0338R.styleable.PromptView_secondaryTextColour /*19*/:
            case C0338R.styleable.PromptView_secondaryTextFontFamily /*20*/:
            case C0338R.styleable.PromptView_secondaryTextSize /*21*/:
            case C0338R.styleable.PromptView_secondaryTextStyle /*22*/:
            case C0338R.styleable.PromptView_secondaryTextTypeface /*23*/:
            case C0338R.styleable.PromptView_target /*24*/:
            case C0338R.styleable.PromptView_textPadding /*25*/:
            case C0338R.styleable.PromptView_textSeparation /*26*/:
            case OggUtil.PAGE_HEADER_SIZE /*27*/:
            case NalUnitTypes.NAL_TYPE_PREFIX_SEI_NUT /*39*/:
                this.epConfig = bitReaderBuffer.readBits(2);
                if (this.epConfig == 2 || this.epConfig == 3) {
                    throw new UnsupportedOperationException("can't parse ErrorProtectionSpecificConfig yet");
                } else if (this.epConfig == 3) {
                    this.directMapping = bitReaderBuffer.readBits(1);
                    if (this.directMapping == 0) {
                        throw new RuntimeException("not implemented");
                    }
                }
                break;
        }
        if (this.extensionAudioObjectType != 5 && bitReaderBuffer.remainingBits() >= 16) {
            this.syncExtensionType = bitReaderBuffer.readBits(11);
            if (this.syncExtensionType == 695) {
                this.extensionAudioObjectType = getAudioObjectType(bitReaderBuffer);
                if (this.extensionAudioObjectType == 5) {
                    this.sbrPresentFlag = bitReaderBuffer.readBool();
                    if (this.sbrPresentFlag) {
                        this.extensionSamplingFrequencyIndex = bitReaderBuffer.readBits(4);
                        if (this.extensionSamplingFrequencyIndex == 15) {
                            this.extensionSamplingFrequency = bitReaderBuffer.readBits(24);
                        }
                        if (bitReaderBuffer.remainingBits() >= 12) {
                            this.syncExtensionType = bitReaderBuffer.readBits(11);
                            if (this.syncExtensionType == 1352) {
                                this.psPresentFlag = bitReaderBuffer.readBool();
                            }
                        }
                    }
                }
                if (this.extensionAudioObjectType == 22) {
                    this.sbrPresentFlag = bitReaderBuffer.readBool();
                    if (this.sbrPresentFlag) {
                        this.extensionSamplingFrequencyIndex = bitReaderBuffer.readBits(4);
                        if (this.extensionSamplingFrequencyIndex == 15) {
                            this.extensionSamplingFrequency = bitReaderBuffer.readBits(24);
                        }
                    }
                    this.extensionChannelConfiguration = bitReaderBuffer.readBits(4);
                }
            }
        }
    }

    public ByteBuffer serialize() {
        ByteBuffer allocate = ByteBuffer.allocate(serializedSize());
        IsoTypeWriter.writeUInt8(allocate, 5);
        IsoTypeWriter.writeUInt8(allocate, serializedSize() - 2);
        BitWriterBuffer bitWriterBuffer = new BitWriterBuffer(allocate);
        bitWriterBuffer.writeBits(this.audioObjectType, 5);
        bitWriterBuffer.writeBits(this.samplingFrequencyIndex, 4);
        if (this.samplingFrequencyIndex == 15) {
            throw new UnsupportedOperationException("can't serialize that yet");
        }
        bitWriterBuffer.writeBits(this.channelConfiguration, 4);
        return allocate;
    }

    public int serializedSize() {
        if (this.audioObjectType == 2) {
            return 4 + gaSpecificConfigSize();
        }
        throw new UnsupportedOperationException("can't serialize that yet");
    }

    public void setAudioObjectType(int i) {
        this.audioObjectType = i;
    }

    public void setChannelConfiguration(int i) {
        this.channelConfiguration = i;
    }

    public void setSamplingFrequency(int i) {
        this.samplingFrequency = i;
    }

    public void setSamplingFrequencyIndex(int i) {
        this.samplingFrequencyIndex = i;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("AudioSpecificConfig");
        stringBuilder.append("{configBytes=").append(Hex.encodeHex(this.configBytes));
        stringBuilder.append(", audioObjectType=").append(this.audioObjectType).append(" (").append((String) audioObjectTypeMap.get(Integer.valueOf(this.audioObjectType))).append(")");
        stringBuilder.append(", samplingFrequencyIndex=").append(this.samplingFrequencyIndex).append(" (").append(samplingFrequencyIndexMap.get(Integer.valueOf(this.samplingFrequencyIndex))).append(")");
        stringBuilder.append(", samplingFrequency=").append(this.samplingFrequency);
        stringBuilder.append(", channelConfiguration=").append(this.channelConfiguration);
        if (this.extensionAudioObjectType > 0) {
            stringBuilder.append(", extensionAudioObjectType=").append(this.extensionAudioObjectType).append(" (").append((String) audioObjectTypeMap.get(Integer.valueOf(this.extensionAudioObjectType))).append(")");
            stringBuilder.append(", sbrPresentFlag=").append(this.sbrPresentFlag);
            stringBuilder.append(", psPresentFlag=").append(this.psPresentFlag);
            stringBuilder.append(", extensionSamplingFrequencyIndex=").append(this.extensionSamplingFrequencyIndex).append(" (").append(samplingFrequencyIndexMap.get(Integer.valueOf(this.extensionSamplingFrequencyIndex))).append(")");
            stringBuilder.append(", extensionSamplingFrequency=").append(this.extensionSamplingFrequency);
            stringBuilder.append(", extensionChannelConfiguration=").append(this.extensionChannelConfiguration);
        }
        stringBuilder.append(", syncExtensionType=").append(this.syncExtensionType);
        if (this.gaSpecificConfig) {
            stringBuilder.append(", frameLengthFlag=").append(this.frameLengthFlag);
            stringBuilder.append(", dependsOnCoreCoder=").append(this.dependsOnCoreCoder);
            stringBuilder.append(", coreCoderDelay=").append(this.coreCoderDelay);
            stringBuilder.append(", extensionFlag=").append(this.extensionFlag);
            stringBuilder.append(", layerNr=").append(this.layerNr);
            stringBuilder.append(", numOfSubFrame=").append(this.numOfSubFrame);
            stringBuilder.append(", layer_length=").append(this.layer_length);
            stringBuilder.append(", aacSectionDataResilienceFlag=").append(this.aacSectionDataResilienceFlag);
            stringBuilder.append(", aacScalefactorDataResilienceFlag=").append(this.aacScalefactorDataResilienceFlag);
            stringBuilder.append(", aacSpectralDataResilienceFlag=").append(this.aacSpectralDataResilienceFlag);
            stringBuilder.append(", extensionFlag3=").append(this.extensionFlag3);
        }
        if (this.parametricSpecificConfig) {
            stringBuilder.append(", isBaseLayer=").append(this.isBaseLayer);
            stringBuilder.append(", paraMode=").append(this.paraMode);
            stringBuilder.append(", paraExtensionFlag=").append(this.paraExtensionFlag);
            stringBuilder.append(", hvxcVarMode=").append(this.hvxcVarMode);
            stringBuilder.append(", hvxcRateMode=").append(this.hvxcRateMode);
            stringBuilder.append(", erHvxcExtensionFlag=").append(this.erHvxcExtensionFlag);
            stringBuilder.append(", var_ScalableFlag=").append(this.var_ScalableFlag);
            stringBuilder.append(", hilnQuantMode=").append(this.hilnQuantMode);
            stringBuilder.append(", hilnMaxNumLine=").append(this.hilnMaxNumLine);
            stringBuilder.append(", hilnSampleRateCode=").append(this.hilnSampleRateCode);
            stringBuilder.append(", hilnFrameLength=").append(this.hilnFrameLength);
            stringBuilder.append(", hilnContMode=").append(this.hilnContMode);
            stringBuilder.append(", hilnEnhaLayer=").append(this.hilnEnhaLayer);
            stringBuilder.append(", hilnEnhaQuantMode=").append(this.hilnEnhaQuantMode);
        }
        stringBuilder.append('}');
        return stringBuilder.toString();
    }
}
