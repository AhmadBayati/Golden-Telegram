package com.coremedia.iso.boxes;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.googlecode.mp4parser.AbstractFullBox;
import com.googlecode.mp4parser.RequiresParseDetailAspect;
import com.googlecode.mp4parser.util.CastUtils;
import java.lang.ref.SoftReference;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.JoinPoint.StaticPart;
import org.aspectj.runtime.reflect.Factory;

public class TimeToSampleBox extends AbstractFullBox {
    static final /* synthetic */ boolean $assertionsDisabled;
    public static final String TYPE = "stts";
    private static final /* synthetic */ StaticPart ajc$tjp_0 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_1 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_2 = null;
    static Map<List<Entry>, SoftReference<long[]>> cache;
    List<Entry> entries;

    public static class Entry {
        long count;
        long delta;

        public Entry(long j, long j2) {
            this.count = j;
            this.delta = j2;
        }

        public long getCount() {
            return this.count;
        }

        public long getDelta() {
            return this.delta;
        }

        public void setCount(long j) {
            this.count = j;
        }

        public void setDelta(long j) {
            this.delta = j;
        }

        public String toString() {
            return "Entry{count=" + this.count + ", delta=" + this.delta + '}';
        }
    }

    static {
        ajc$preClinit();
        $assertionsDisabled = !TimeToSampleBox.class.desiredAssertionStatus() ? true : $assertionsDisabled;
        cache = new WeakHashMap();
    }

    public TimeToSampleBox() {
        super(TYPE);
        this.entries = Collections.emptyList();
    }

    private static /* synthetic */ void ajc$preClinit() {
        Factory factory = new Factory("TimeToSampleBox.java", TimeToSampleBox.class);
        ajc$tjp_0 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, factory.makeMethodSig("1", "getEntries", "com.coremedia.iso.boxes.TimeToSampleBox", TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, "java.util.List"), 79);
        ajc$tjp_1 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, factory.makeMethodSig("1", "setEntries", "com.coremedia.iso.boxes.TimeToSampleBox", "java.util.List", "entries", TtmlNode.ANONYMOUS_REGION_ID, "void"), 83);
        ajc$tjp_2 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, factory.makeMethodSig("1", "toString", "com.coremedia.iso.boxes.TimeToSampleBox", TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, "java.lang.String"), 87);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static synchronized long[] blowupTimeToSamples(java.util.List<com.coremedia.iso.boxes.TimeToSampleBox.Entry> r12) {
        /*
        r3 = 0;
        r6 = com.coremedia.iso.boxes.TimeToSampleBox.class;
        monitor-enter(r6);
        r0 = cache;	 Catch:{ all -> 0x0036 }
        r0 = r0.get(r12);	 Catch:{ all -> 0x0036 }
        r0 = (java.lang.ref.SoftReference) r0;	 Catch:{ all -> 0x0036 }
        if (r0 == 0) goto L_0x0018;
    L_0x000e:
        r0 = r0.get();	 Catch:{ all -> 0x0036 }
        r0 = (long[]) r0;	 Catch:{ all -> 0x0036 }
        if (r0 == 0) goto L_0x0018;
    L_0x0016:
        monitor-exit(r6);
        return r0;
    L_0x0018:
        r0 = 0;
        r2 = r12.iterator();	 Catch:{ all -> 0x0036 }
        r4 = r0;
    L_0x001f:
        r0 = r2.hasNext();	 Catch:{ all -> 0x0036 }
        if (r0 != 0) goto L_0x0039;
    L_0x0025:
        r0 = $assertionsDisabled;	 Catch:{ all -> 0x0036 }
        if (r0 != 0) goto L_0x0046;
    L_0x0029:
        r0 = 2147483647; // 0x7fffffff float:NaN double:1.060997895E-314;
        r0 = (r4 > r0 ? 1 : (r4 == r0 ? 0 : -1));
        if (r0 <= 0) goto L_0x0046;
    L_0x0030:
        r0 = new java.lang.AssertionError;	 Catch:{ all -> 0x0036 }
        r0.<init>();	 Catch:{ all -> 0x0036 }
        throw r0;	 Catch:{ all -> 0x0036 }
    L_0x0036:
        r0 = move-exception;
        monitor-exit(r6);
        throw r0;
    L_0x0039:
        r0 = r2.next();	 Catch:{ all -> 0x0036 }
        r0 = (com.coremedia.iso.boxes.TimeToSampleBox.Entry) r0;	 Catch:{ all -> 0x0036 }
        r0 = r0.getCount();	 Catch:{ all -> 0x0036 }
        r0 = r0 + r4;
        r4 = r0;
        goto L_0x001f;
    L_0x0046:
        r0 = (int) r4;	 Catch:{ all -> 0x0036 }
        r1 = new long[r0];	 Catch:{ all -> 0x0036 }
        r7 = r12.iterator();	 Catch:{ all -> 0x0036 }
        r2 = r3;
    L_0x004e:
        r0 = r7.hasNext();	 Catch:{ all -> 0x0036 }
        if (r0 != 0) goto L_0x0060;
    L_0x0054:
        r0 = cache;	 Catch:{ all -> 0x0036 }
        r2 = new java.lang.ref.SoftReference;	 Catch:{ all -> 0x0036 }
        r2.<init>(r1);	 Catch:{ all -> 0x0036 }
        r0.put(r12, r2);	 Catch:{ all -> 0x0036 }
        r0 = r1;
        goto L_0x0016;
    L_0x0060:
        r0 = r7.next();	 Catch:{ all -> 0x0036 }
        r0 = (com.coremedia.iso.boxes.TimeToSampleBox.Entry) r0;	 Catch:{ all -> 0x0036 }
        r4 = r2;
        r2 = r3;
    L_0x0068:
        r8 = (long) r2;	 Catch:{ all -> 0x0036 }
        r10 = r0.getCount();	 Catch:{ all -> 0x0036 }
        r5 = (r8 > r10 ? 1 : (r8 == r10 ? 0 : -1));
        if (r5 < 0) goto L_0x0073;
    L_0x0071:
        r2 = r4;
        goto L_0x004e;
    L_0x0073:
        r5 = r4 + 1;
        r8 = r0.getDelta();	 Catch:{ all -> 0x0036 }
        r1[r4] = r8;	 Catch:{ all -> 0x0036 }
        r2 = r2 + 1;
        r4 = r5;
        goto L_0x0068;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.coremedia.iso.boxes.TimeToSampleBox.blowupTimeToSamples(java.util.List):long[]");
    }

    public void _parseDetails(ByteBuffer byteBuffer) {
        parseVersionAndFlags(byteBuffer);
        int l2i = CastUtils.l2i(IsoTypeReader.readUInt32(byteBuffer));
        this.entries = new ArrayList(l2i);
        for (int i = 0; i < l2i; i++) {
            this.entries.add(new Entry(IsoTypeReader.readUInt32(byteBuffer), IsoTypeReader.readUInt32(byteBuffer)));
        }
    }

    protected void getContent(ByteBuffer byteBuffer) {
        writeVersionAndFlags(byteBuffer);
        IsoTypeWriter.writeUInt32(byteBuffer, (long) this.entries.size());
        for (Entry entry : this.entries) {
            IsoTypeWriter.writeUInt32(byteBuffer, entry.getCount());
            IsoTypeWriter.writeUInt32(byteBuffer, entry.getDelta());
        }
    }

    protected long getContentSize() {
        return (long) ((this.entries.size() * 8) + 8);
    }

    public List<Entry> getEntries() {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_0, this, this));
        return this.entries;
    }

    public void setEntries(List<Entry> list) {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_1, (Object) this, (Object) this, (Object) list));
        this.entries = list;
    }

    public String toString() {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_2, this, this));
        return "TimeToSampleBox[entryCount=" + this.entries.size() + "]";
    }
}
