package com.coremedia.iso.boxes;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.googlecode.mp4parser.AbstractFullBox;
import com.googlecode.mp4parser.RequiresParseDetailAspect;
import com.googlecode.mp4parser.util.CastUtils;
import java.nio.ByteBuffer;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.JoinPoint.StaticPart;
import org.aspectj.runtime.reflect.Factory;

public class SyncSampleBox extends AbstractFullBox {
    public static final String TYPE = "stss";
    private static final /* synthetic */ StaticPart ajc$tjp_0 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_1 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_2 = null;
    private long[] sampleNumber;

    static {
        ajc$preClinit();
    }

    public SyncSampleBox() {
        super(TYPE);
    }

    private static /* synthetic */ void ajc$preClinit() {
        Factory factory = new Factory("SyncSampleBox.java", SyncSampleBox.class);
        ajc$tjp_0 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, factory.makeMethodSig("1", "getSampleNumber", "com.coremedia.iso.boxes.SyncSampleBox", TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, "[J"), 46);
        ajc$tjp_1 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, factory.makeMethodSig("1", "toString", "com.coremedia.iso.boxes.SyncSampleBox", TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, "java.lang.String"), 77);
        ajc$tjp_2 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, factory.makeMethodSig("1", "setSampleNumber", "com.coremedia.iso.boxes.SyncSampleBox", "[J", "sampleNumber", TtmlNode.ANONYMOUS_REGION_ID, "void"), 81);
    }

    public void _parseDetails(ByteBuffer byteBuffer) {
        parseVersionAndFlags(byteBuffer);
        int l2i = CastUtils.l2i(IsoTypeReader.readUInt32(byteBuffer));
        this.sampleNumber = new long[l2i];
        for (int i = 0; i < l2i; i++) {
            this.sampleNumber[i] = IsoTypeReader.readUInt32(byteBuffer);
        }
    }

    protected void getContent(ByteBuffer byteBuffer) {
        writeVersionAndFlags(byteBuffer);
        IsoTypeWriter.writeUInt32(byteBuffer, (long) this.sampleNumber.length);
        for (long writeUInt32 : this.sampleNumber) {
            IsoTypeWriter.writeUInt32(byteBuffer, writeUInt32);
        }
    }

    protected long getContentSize() {
        return (long) ((this.sampleNumber.length * 4) + 8);
    }

    public long[] getSampleNumber() {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_0, this, this));
        return this.sampleNumber;
    }

    public void setSampleNumber(long[] jArr) {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_2, (Object) this, (Object) this, (Object) jArr));
        this.sampleNumber = jArr;
    }

    public String toString() {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_1, this, this));
        return "SyncSampleBox[entryCount=" + this.sampleNumber.length + "]";
    }
}
