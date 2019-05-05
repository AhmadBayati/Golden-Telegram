package com.coremedia.iso.boxes;

import com.googlecode.mp4parser.AbstractBox;
import com.googlecode.mp4parser.RequiresParseDetailAspect;
import java.nio.ByteBuffer;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.JoinPoint.StaticPart;
import org.aspectj.runtime.reflect.Factory;

public class FreeSpaceBox extends AbstractBox {
    public static final String TYPE = "skip";
    private static final /* synthetic */ StaticPart ajc$tjp_0 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_1 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_2 = null;
    byte[] data;

    static {
        ajc$preClinit();
    }

    public FreeSpaceBox() {
        super(TYPE);
    }

    private static /* synthetic */ void ajc$preClinit() {
        Factory factory = new Factory("FreeSpaceBox.java", FreeSpaceBox.class);
        ajc$tjp_0 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, factory.makeMethodSig("1", "setData", "com.coremedia.iso.boxes.FreeSpaceBox", "[B", "data", TtmlNode.ANONYMOUS_REGION_ID, "void"), 42);
        ajc$tjp_1 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, factory.makeMethodSig("1", "getData", "com.coremedia.iso.boxes.FreeSpaceBox", TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, "[B"), 46);
        ajc$tjp_2 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, factory.makeMethodSig("1", "toString", "com.coremedia.iso.boxes.FreeSpaceBox", TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, "java.lang.String"), 61);
    }

    public void _parseDetails(ByteBuffer byteBuffer) {
        this.data = new byte[byteBuffer.remaining()];
        byteBuffer.get(this.data);
    }

    protected void getContent(ByteBuffer byteBuffer) {
        byteBuffer.put(this.data);
    }

    protected long getContentSize() {
        return (long) this.data.length;
    }

    public byte[] getData() {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_1, this, this));
        return this.data;
    }

    public void setData(byte[] bArr) {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_0, (Object) this, (Object) this, (Object) bArr));
        this.data = bArr;
    }

    public String toString() {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_2, this, this));
        return "FreeSpaceBox[size=" + this.data.length + ";type=" + getType() + "]";
    }
}
