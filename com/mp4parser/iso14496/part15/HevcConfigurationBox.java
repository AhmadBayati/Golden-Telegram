package com.mp4parser.iso14496.part15;

import com.googlecode.mp4parser.AbstractBox;
import com.googlecode.mp4parser.RequiresParseDetailAspect;
import com.hanista.mobogram.tgnet.TLRPC;
import com.mp4parser.iso14496.part15.HevcDecoderConfigurationRecord.Array;
import java.nio.ByteBuffer;
import java.util.List;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.JoinPoint.StaticPart;
import org.aspectj.runtime.reflect.Factory;

public class HevcConfigurationBox extends AbstractBox {
    public static final String TYPE = "hvcC";
    private static final /* synthetic */ StaticPart ajc$tjp_0 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_1 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_10 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_11 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_12 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_13 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_14 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_15 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_16 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_17 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_18 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_19 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_2 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_20 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_21 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_3 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_4 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_5 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_6 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_7 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_8 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_9 = null;
    private HevcDecoderConfigurationRecord hevcDecoderConfigurationRecord;

    static {
        ajc$preClinit();
    }

    public HevcConfigurationBox() {
        super(TYPE);
        this.hevcDecoderConfigurationRecord = new HevcDecoderConfigurationRecord();
    }

    private static /* synthetic */ void ajc$preClinit() {
        Factory factory = new Factory("HevcConfigurationBox.java", HevcConfigurationBox.class);
        ajc$tjp_0 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, factory.makeMethodSig("1", "getHevcDecoderConfigurationRecord", "com.mp4parser.iso14496.part15.HevcConfigurationBox", TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, "com.mp4parser.iso14496.part15.HevcDecoderConfigurationRecord"), 36);
        ajc$tjp_1 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, factory.makeMethodSig("1", "setHevcDecoderConfigurationRecord", "com.mp4parser.iso14496.part15.HevcConfigurationBox", "com.mp4parser.iso14496.part15.HevcDecoderConfigurationRecord", "hevcDecoderConfigurationRecord", TtmlNode.ANONYMOUS_REGION_ID, "void"), 40);
        ajc$tjp_10 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, factory.makeMethodSig("1", "getGeneral_level_idc", "com.mp4parser.iso14496.part15.HevcConfigurationBox", TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, "int"), 88);
        ajc$tjp_11 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, factory.makeMethodSig("1", "getMin_spatial_segmentation_idc", "com.mp4parser.iso14496.part15.HevcConfigurationBox", TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, "int"), 92);
        ajc$tjp_12 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, factory.makeMethodSig("1", "getParallelismType", "com.mp4parser.iso14496.part15.HevcConfigurationBox", TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, "int"), 96);
        ajc$tjp_13 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, factory.makeMethodSig("1", "getChromaFormat", "com.mp4parser.iso14496.part15.HevcConfigurationBox", TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, "int"), 100);
        ajc$tjp_14 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, factory.makeMethodSig("1", "getBitDepthLumaMinus8", "com.mp4parser.iso14496.part15.HevcConfigurationBox", TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, "int"), 104);
        ajc$tjp_15 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, factory.makeMethodSig("1", "getBitDepthChromaMinus8", "com.mp4parser.iso14496.part15.HevcConfigurationBox", TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, "int"), 108);
        ajc$tjp_16 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, factory.makeMethodSig("1", "getAvgFrameRate", "com.mp4parser.iso14496.part15.HevcConfigurationBox", TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, "int"), 112);
        ajc$tjp_17 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, factory.makeMethodSig("1", "getNumTemporalLayers", "com.mp4parser.iso14496.part15.HevcConfigurationBox", TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, "int"), 116);
        ajc$tjp_18 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, factory.makeMethodSig("1", "getLengthSizeMinusOne", "com.mp4parser.iso14496.part15.HevcConfigurationBox", TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, "int"), 120);
        ajc$tjp_19 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, factory.makeMethodSig("1", "isTemporalIdNested", "com.mp4parser.iso14496.part15.HevcConfigurationBox", TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, "boolean"), 124);
        ajc$tjp_2 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, factory.makeMethodSig("1", "equals", "com.mp4parser.iso14496.part15.HevcConfigurationBox", "java.lang.Object", "o", TtmlNode.ANONYMOUS_REGION_ID, "boolean"), 45);
        ajc$tjp_20 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, factory.makeMethodSig("1", "getConstantFrameRate", "com.mp4parser.iso14496.part15.HevcConfigurationBox", TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, "int"), (int) TLRPC.USER_FLAG_UNUSED);
        ajc$tjp_21 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, factory.makeMethodSig("1", "getArrays", "com.mp4parser.iso14496.part15.HevcConfigurationBox", TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, "java.util.List"), 132);
        ajc$tjp_3 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, factory.makeMethodSig("1", "hashCode", "com.mp4parser.iso14496.part15.HevcConfigurationBox", TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, "int"), 58);
        ajc$tjp_4 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, factory.makeMethodSig("1", "getConfigurationVersion", "com.mp4parser.iso14496.part15.HevcConfigurationBox", TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, "int"), 63);
        ajc$tjp_5 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, factory.makeMethodSig("1", "getGeneral_profile_space", "com.mp4parser.iso14496.part15.HevcConfigurationBox", TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, "int"), 67);
        ajc$tjp_6 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, factory.makeMethodSig("1", "isGeneral_tier_flag", "com.mp4parser.iso14496.part15.HevcConfigurationBox", TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, "boolean"), 71);
        ajc$tjp_7 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, factory.makeMethodSig("1", "getGeneral_profile_idc", "com.mp4parser.iso14496.part15.HevcConfigurationBox", TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, "int"), 76);
        ajc$tjp_8 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, factory.makeMethodSig("1", "getGeneral_profile_compatibility_flags", "com.mp4parser.iso14496.part15.HevcConfigurationBox", TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, "long"), 80);
        ajc$tjp_9 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, factory.makeMethodSig("1", "getGeneral_constraint_indicator_flags", "com.mp4parser.iso14496.part15.HevcConfigurationBox", TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, "long"), 84);
    }

    protected void _parseDetails(ByteBuffer byteBuffer) {
        this.hevcDecoderConfigurationRecord.parse(byteBuffer);
    }

    public boolean equals(Object obj) {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_2, (Object) this, (Object) this, obj));
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        HevcConfigurationBox hevcConfigurationBox = (HevcConfigurationBox) obj;
        if (this.hevcDecoderConfigurationRecord != null) {
            if (this.hevcDecoderConfigurationRecord.equals(hevcConfigurationBox.hevcDecoderConfigurationRecord)) {
                return true;
            }
        } else if (hevcConfigurationBox.hevcDecoderConfigurationRecord == null) {
            return true;
        }
        return false;
    }

    public List<Array> getArrays() {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_21, this, this));
        return this.hevcDecoderConfigurationRecord.arrays;
    }

    public int getAvgFrameRate() {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_16, this, this));
        return this.hevcDecoderConfigurationRecord.avgFrameRate;
    }

    public int getBitDepthChromaMinus8() {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_15, this, this));
        return this.hevcDecoderConfigurationRecord.bitDepthChromaMinus8;
    }

    public int getBitDepthLumaMinus8() {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_14, this, this));
        return this.hevcDecoderConfigurationRecord.bitDepthLumaMinus8;
    }

    public int getChromaFormat() {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_13, this, this));
        return this.hevcDecoderConfigurationRecord.chromaFormat;
    }

    public int getConfigurationVersion() {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_4, this, this));
        return this.hevcDecoderConfigurationRecord.configurationVersion;
    }

    public int getConstantFrameRate() {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_20, this, this));
        return this.hevcDecoderConfigurationRecord.constantFrameRate;
    }

    protected void getContent(ByteBuffer byteBuffer) {
        this.hevcDecoderConfigurationRecord.write(byteBuffer);
    }

    protected long getContentSize() {
        return (long) this.hevcDecoderConfigurationRecord.getSize();
    }

    public long getGeneral_constraint_indicator_flags() {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_9, this, this));
        return this.hevcDecoderConfigurationRecord.general_constraint_indicator_flags;
    }

    public int getGeneral_level_idc() {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_10, this, this));
        return this.hevcDecoderConfigurationRecord.general_level_idc;
    }

    public long getGeneral_profile_compatibility_flags() {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_8, this, this));
        return this.hevcDecoderConfigurationRecord.general_profile_compatibility_flags;
    }

    public int getGeneral_profile_idc() {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_7, this, this));
        return this.hevcDecoderConfigurationRecord.general_profile_idc;
    }

    public int getGeneral_profile_space() {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_5, this, this));
        return this.hevcDecoderConfigurationRecord.general_profile_space;
    }

    public HevcDecoderConfigurationRecord getHevcDecoderConfigurationRecord() {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_0, this, this));
        return this.hevcDecoderConfigurationRecord;
    }

    public int getLengthSizeMinusOne() {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_18, this, this));
        return this.hevcDecoderConfigurationRecord.lengthSizeMinusOne;
    }

    public int getMin_spatial_segmentation_idc() {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_11, this, this));
        return this.hevcDecoderConfigurationRecord.min_spatial_segmentation_idc;
    }

    public int getNumTemporalLayers() {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_17, this, this));
        return this.hevcDecoderConfigurationRecord.numTemporalLayers;
    }

    public int getParallelismType() {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_12, this, this));
        return this.hevcDecoderConfigurationRecord.parallelismType;
    }

    public int hashCode() {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_3, this, this));
        return this.hevcDecoderConfigurationRecord != null ? this.hevcDecoderConfigurationRecord.hashCode() : 0;
    }

    public boolean isGeneral_tier_flag() {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_6, this, this));
        return this.hevcDecoderConfigurationRecord.general_tier_flag;
    }

    public boolean isTemporalIdNested() {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_19, this, this));
        return this.hevcDecoderConfigurationRecord.temporalIdNested;
    }

    public void setHevcDecoderConfigurationRecord(HevcDecoderConfigurationRecord hevcDecoderConfigurationRecord) {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_1, (Object) this, (Object) this, (Object) hevcDecoderConfigurationRecord));
        this.hevcDecoderConfigurationRecord = hevcDecoderConfigurationRecord;
    }
}
