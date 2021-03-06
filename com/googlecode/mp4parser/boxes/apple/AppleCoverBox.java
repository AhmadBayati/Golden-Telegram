package com.googlecode.mp4parser.boxes.apple;

import com.googlecode.mp4parser.RequiresParseDetailAspect;
import java.nio.ByteBuffer;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.JoinPoint.StaticPart;
import org.aspectj.runtime.reflect.Factory;

public class AppleCoverBox extends AppleDataBox {
    private static final int IMAGE_TYPE_JPG = 13;
    private static final int IMAGE_TYPE_PNG = 14;
    private static final /* synthetic */ StaticPart ajc$tjp_0 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_1 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_2 = null;
    private byte[] data;

    static {
        ajc$preClinit();
    }

    public AppleCoverBox() {
        super("covr", 1);
    }

    private static /* synthetic */ void ajc$preClinit() {
        Factory factory = new Factory("AppleCoverBox.java", AppleCoverBox.class);
        ajc$tjp_0 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, factory.makeMethodSig("1", "getCoverData", "com.googlecode.mp4parser.boxes.apple.AppleCoverBox", TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, "[B"), 21);
        ajc$tjp_1 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, factory.makeMethodSig("1", "setJpg", "com.googlecode.mp4parser.boxes.apple.AppleCoverBox", "[B", "data", TtmlNode.ANONYMOUS_REGION_ID, "void"), 25);
        ajc$tjp_2 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, factory.makeMethodSig("1", "setPng", "com.googlecode.mp4parser.boxes.apple.AppleCoverBox", "[B", "data", TtmlNode.ANONYMOUS_REGION_ID, "void"), 29);
    }

    private void setImageData(byte[] bArr, int i) {
        this.data = bArr;
        this.dataType = i;
    }

    public byte[] getCoverData() {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_0, this, this));
        return this.data;
    }

    protected int getDataLength() {
        return this.data.length;
    }

    protected void parseData(ByteBuffer byteBuffer) {
        this.data = new byte[byteBuffer.limit()];
        byteBuffer.get(this.data);
    }

    public void setJpg(byte[] bArr) {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_1, (Object) this, (Object) this, (Object) bArr));
        setImageData(bArr, IMAGE_TYPE_JPG);
    }

    public void setPng(byte[] bArr) {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_2, (Object) this, (Object) this, (Object) bArr));
        setImageData(bArr, IMAGE_TYPE_PNG);
    }

    protected byte[] writeData() {
        return this.data;
    }
}
