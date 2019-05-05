package com.googlecode.mp4parser.boxes.microsoft;

import com.googlecode.mp4parser.AbstractBox;
import com.googlecode.mp4parser.RequiresParseDetailAspect;
import com.hanista.mobogram.messenger.support.widget.helper.ItemTouchHelper.Callback;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Date;
import java.util.Iterator;
import java.util.Vector;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.JoinPoint.StaticPart;
import org.aspectj.runtime.internal.Conversions;
import org.aspectj.runtime.reflect.Factory;

public class XtraBox extends AbstractBox {
    private static final long FILETIME_EPOCH_DIFF = 11644473600000L;
    private static final long FILETIME_ONE_MILLISECOND = 10000;
    public static final int MP4_XTRA_BT_FILETIME = 21;
    public static final int MP4_XTRA_BT_GUID = 72;
    public static final int MP4_XTRA_BT_INT64 = 19;
    public static final int MP4_XTRA_BT_UNICODE = 8;
    public static final String TYPE = "Xtra";
    private static final /* synthetic */ StaticPart ajc$tjp_0 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_1 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_10 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_2 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_3 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_4 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_5 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_6 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_7 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_8 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_9 = null;
    ByteBuffer data;
    private boolean successfulParse;
    Vector<XtraTag> tags;

    private static class XtraTag {
        private int inputSize;
        private String tagName;
        private Vector<XtraValue> values;

        private XtraTag() {
            this.values = new Vector();
        }

        private XtraTag(String str) {
            this();
            this.tagName = str;
        }

        private void getContent(ByteBuffer byteBuffer) {
            byteBuffer.putInt(getContentSize());
            byteBuffer.putInt(this.tagName.length());
            XtraBox.writeAsciiString(byteBuffer, this.tagName);
            byteBuffer.putInt(this.values.size());
            for (int i = 0; i < this.values.size(); i++) {
                ((XtraValue) this.values.elementAt(i)).getContent(byteBuffer);
            }
        }

        private int getContentSize() {
            int length = this.tagName.length() + 12;
            for (int i = 0; i < this.values.size(); i++) {
                length += ((XtraValue) this.values.elementAt(i)).getContentSize();
            }
            return length;
        }

        private void parse(ByteBuffer byteBuffer) {
            this.inputSize = byteBuffer.getInt();
            this.tagName = XtraBox.readAsciiString(byteBuffer, byteBuffer.getInt());
            int i = byteBuffer.getInt();
            for (int i2 = 0; i2 < i; i2++) {
                XtraValue xtraValue = new XtraValue();
                xtraValue.parse(byteBuffer);
                this.values.addElement(xtraValue);
            }
            if (this.inputSize != getContentSize()) {
                throw new RuntimeException("Improperly handled Xtra tag: Sizes don't match ( " + this.inputSize + "/" + getContentSize() + ") on " + this.tagName);
            }
        }

        public String toString() {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append(this.tagName);
            stringBuffer.append(" [");
            stringBuffer.append(this.inputSize);
            stringBuffer.append("/");
            stringBuffer.append(this.values.size());
            stringBuffer.append("]:\n");
            for (int i = 0; i < this.values.size(); i++) {
                stringBuffer.append("  ");
                stringBuffer.append(((XtraValue) this.values.elementAt(i)).toString());
                stringBuffer.append("\n");
            }
            return stringBuffer.toString();
        }
    }

    private static class XtraValue {
        public Date fileTimeValue;
        public long longValue;
        public byte[] nonParsedValue;
        public String stringValue;
        public int type;

        private XtraValue() {
        }

        private XtraValue(long j) {
            this.type = XtraBox.MP4_XTRA_BT_INT64;
            this.longValue = j;
        }

        private XtraValue(String str) {
            this.type = XtraBox.MP4_XTRA_BT_UNICODE;
            this.stringValue = str;
        }

        private XtraValue(Date date) {
            this.type = XtraBox.MP4_XTRA_BT_FILETIME;
            this.fileTimeValue = date;
        }

        private void getContent(ByteBuffer byteBuffer) {
            try {
                byteBuffer.putInt(getContentSize());
                byteBuffer.putShort((short) this.type);
                byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
                switch (this.type) {
                    case XtraBox.MP4_XTRA_BT_UNICODE /*8*/:
                        XtraBox.writeUtf16String(byteBuffer, this.stringValue);
                        break;
                    case XtraBox.MP4_XTRA_BT_INT64 /*19*/:
                        byteBuffer.putLong(this.longValue);
                        break;
                    case XtraBox.MP4_XTRA_BT_FILETIME /*21*/:
                        byteBuffer.putLong(XtraBox.millisToFiletime(this.fileTimeValue.getTime()));
                        break;
                    default:
                        byteBuffer.put(this.nonParsedValue);
                        break;
                }
                byteBuffer.order(ByteOrder.BIG_ENDIAN);
            } catch (Throwable th) {
                byteBuffer.order(ByteOrder.BIG_ENDIAN);
            }
        }

        private int getContentSize() {
            switch (this.type) {
                case XtraBox.MP4_XTRA_BT_UNICODE /*8*/:
                    return 6 + ((this.stringValue.length() * 2) + 2);
                case XtraBox.MP4_XTRA_BT_INT64 /*19*/:
                case XtraBox.MP4_XTRA_BT_FILETIME /*21*/:
                    return 14;
                default:
                    return 6 + this.nonParsedValue.length;
            }
        }

        private Object getValueAsObject() {
            switch (this.type) {
                case XtraBox.MP4_XTRA_BT_UNICODE /*8*/:
                    return this.stringValue;
                case XtraBox.MP4_XTRA_BT_INT64 /*19*/:
                    return new Long(this.longValue);
                case XtraBox.MP4_XTRA_BT_FILETIME /*21*/:
                    return this.fileTimeValue;
                default:
                    return this.nonParsedValue;
            }
        }

        private void parse(ByteBuffer byteBuffer) {
            int i = byteBuffer.getInt() - 6;
            this.type = byteBuffer.getShort();
            byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
            switch (this.type) {
                case XtraBox.MP4_XTRA_BT_UNICODE /*8*/:
                    this.stringValue = XtraBox.readUtf16String(byteBuffer, i);
                    break;
                case XtraBox.MP4_XTRA_BT_INT64 /*19*/:
                    this.longValue = byteBuffer.getLong();
                    break;
                case XtraBox.MP4_XTRA_BT_FILETIME /*21*/:
                    this.fileTimeValue = new Date(XtraBox.filetimeToMillis(byteBuffer.getLong()));
                    break;
                default:
                    this.nonParsedValue = new byte[i];
                    byteBuffer.get(this.nonParsedValue);
                    break;
            }
            byteBuffer.order(ByteOrder.BIG_ENDIAN);
        }

        public String toString() {
            switch (this.type) {
                case XtraBox.MP4_XTRA_BT_UNICODE /*8*/:
                    return "[string]" + this.stringValue;
                case XtraBox.MP4_XTRA_BT_INT64 /*19*/:
                    return "[long]" + String.valueOf(this.longValue);
                case XtraBox.MP4_XTRA_BT_FILETIME /*21*/:
                    return "[filetime]" + this.fileTimeValue.toString();
                default:
                    return "[GUID](nonParsed)";
            }
        }
    }

    static {
        ajc$preClinit();
    }

    public XtraBox() {
        super(TYPE);
        this.successfulParse = false;
        this.tags = new Vector();
    }

    public XtraBox(String str) {
        super(str);
        this.successfulParse = false;
        this.tags = new Vector();
    }

    private static /* synthetic */ void ajc$preClinit() {
        Factory factory = new Factory("XtraBox.java", XtraBox.class);
        ajc$tjp_0 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, factory.makeMethodSig("1", "toString", "com.googlecode.mp4parser.boxes.microsoft.XtraBox", TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, "java.lang.String"), 88);
        ajc$tjp_1 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, factory.makeMethodSig("1", "getAllTagNames", "com.googlecode.mp4parser.boxes.microsoft.XtraBox", TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, "[Ljava.lang.String;"), 151);
        ajc$tjp_10 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, factory.makeMethodSig("1", "setTagValue", "com.googlecode.mp4parser.boxes.microsoft.XtraBox", "java.lang.String:long", "name:value", TtmlNode.ANONYMOUS_REGION_ID, "void"), 289);
        ajc$tjp_2 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, factory.makeMethodSig("1", "getFirstStringValue", "com.googlecode.mp4parser.boxes.microsoft.XtraBox", "java.lang.String", "name", TtmlNode.ANONYMOUS_REGION_ID, "java.lang.String"), 166);
        ajc$tjp_3 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, factory.makeMethodSig("1", "getFirstDateValue", "com.googlecode.mp4parser.boxes.microsoft.XtraBox", "java.lang.String", "name", TtmlNode.ANONYMOUS_REGION_ID, "java.util.Date"), 183);
        ajc$tjp_4 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, factory.makeMethodSig("1", "getFirstLongValue", "com.googlecode.mp4parser.boxes.microsoft.XtraBox", "java.lang.String", "name", TtmlNode.ANONYMOUS_REGION_ID, "java.lang.Long"), (int) Callback.DEFAULT_DRAG_ANIMATION_DURATION);
        ajc$tjp_5 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, factory.makeMethodSig("1", "getValues", "com.googlecode.mp4parser.boxes.microsoft.XtraBox", "java.lang.String", "name", TtmlNode.ANONYMOUS_REGION_ID, "[Ljava.lang.Object;"), 216);
        ajc$tjp_6 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, factory.makeMethodSig("1", "removeTag", "com.googlecode.mp4parser.boxes.microsoft.XtraBox", "java.lang.String", "name", TtmlNode.ANONYMOUS_REGION_ID, "void"), 236);
        ajc$tjp_7 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, factory.makeMethodSig("1", "setTagValues", "com.googlecode.mp4parser.boxes.microsoft.XtraBox", "java.lang.String:[Ljava.lang.String;", "name:values", TtmlNode.ANONYMOUS_REGION_ID, "void"), 249);
        ajc$tjp_8 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, factory.makeMethodSig("1", "setTagValue", "com.googlecode.mp4parser.boxes.microsoft.XtraBox", "java.lang.String:java.lang.String", "name:value", TtmlNode.ANONYMOUS_REGION_ID, "void"), 265);
        ajc$tjp_9 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, factory.makeMethodSig("1", "setTagValue", "com.googlecode.mp4parser.boxes.microsoft.XtraBox", "java.lang.String:java.util.Date", "name:date", TtmlNode.ANONYMOUS_REGION_ID, "void"), 276);
    }

    private int detailSize() {
        int i = 0;
        for (int i2 = 0; i2 < this.tags.size(); i2++) {
            i += ((XtraTag) this.tags.elementAt(i2)).getContentSize();
        }
        return i;
    }

    private static long filetimeToMillis(long j) {
        return (j / FILETIME_ONE_MILLISECOND) - FILETIME_EPOCH_DIFF;
    }

    private XtraTag getTagByName(String str) {
        Iterator it = this.tags.iterator();
        while (it.hasNext()) {
            XtraTag xtraTag = (XtraTag) it.next();
            if (xtraTag.tagName.equals(str)) {
                return xtraTag;
            }
        }
        return null;
    }

    private static long millisToFiletime(long j) {
        return (FILETIME_EPOCH_DIFF + j) * FILETIME_ONE_MILLISECOND;
    }

    private static String readAsciiString(ByteBuffer byteBuffer, int i) {
        byte[] bArr = new byte[i];
        byteBuffer.get(bArr);
        try {
            return new String(bArr, "US-ASCII");
        } catch (Throwable e) {
            throw new RuntimeException("Shouldn't happen", e);
        }
    }

    private static String readUtf16String(ByteBuffer byteBuffer, int i) {
        char[] cArr = new char[((i / 2) - 1)];
        for (int i2 = 0; i2 < (i / 2) - 1; i2++) {
            cArr[i2] = byteBuffer.getChar();
        }
        byteBuffer.getChar();
        return new String(cArr);
    }

    private static void writeAsciiString(ByteBuffer byteBuffer, String str) {
        try {
            byteBuffer.put(str.getBytes("US-ASCII"));
        } catch (Throwable e) {
            throw new RuntimeException("Shouldn't happen", e);
        }
    }

    private static void writeUtf16String(ByteBuffer byteBuffer, String str) {
        char[] toCharArray = str.toCharArray();
        for (char putChar : toCharArray) {
            byteBuffer.putChar(putChar);
        }
        byteBuffer.putChar('\u0000');
    }

    public void _parseDetails(ByteBuffer byteBuffer) {
        int remaining = byteBuffer.remaining();
        this.data = byteBuffer.slice();
        this.successfulParse = false;
        try {
            this.tags.clear();
            while (byteBuffer.remaining() > 0) {
                XtraTag xtraTag = new XtraTag();
                xtraTag.parse(byteBuffer);
                this.tags.addElement(xtraTag);
            }
            int detailSize = detailSize();
            if (remaining != detailSize) {
                throw new RuntimeException("Improperly handled Xtra tag: Calculated sizes don't match ( " + remaining + "/" + detailSize + ")");
            }
            this.successfulParse = true;
        } catch (Exception e) {
            this.successfulParse = false;
            System.err.println("Malformed Xtra Tag detected: " + e.toString());
            e.printStackTrace();
            byteBuffer.position(byteBuffer.position() + byteBuffer.remaining());
        } finally {
            byteBuffer.order(ByteOrder.BIG_ENDIAN);
        }
    }

    public String[] getAllTagNames() {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_1, this, this));
        String[] strArr = new String[this.tags.size()];
        for (int i = 0; i < this.tags.size(); i++) {
            strArr[i] = ((XtraTag) this.tags.elementAt(i)).tagName;
        }
        return strArr;
    }

    protected void getContent(ByteBuffer byteBuffer) {
        if (this.successfulParse) {
            for (int i = 0; i < this.tags.size(); i++) {
                ((XtraTag) this.tags.elementAt(i)).getContent(byteBuffer);
            }
            return;
        }
        this.data.rewind();
        byteBuffer.put(this.data);
    }

    protected long getContentSize() {
        return this.successfulParse ? (long) detailSize() : (long) this.data.limit();
    }

    public Date getFirstDateValue(String str) {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_3, (Object) this, (Object) this, (Object) str));
        for (Object obj : getValues(str)) {
            if (obj instanceof Date) {
                return (Date) obj;
            }
        }
        return null;
    }

    public Long getFirstLongValue(String str) {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_4, (Object) this, (Object) this, (Object) str));
        for (Object obj : getValues(str)) {
            if (obj instanceof Long) {
                return (Long) obj;
            }
        }
        return null;
    }

    public String getFirstStringValue(String str) {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_2, (Object) this, (Object) this, (Object) str));
        for (Object obj : getValues(str)) {
            if (obj instanceof String) {
                return (String) obj;
            }
        }
        return null;
    }

    public Object[] getValues(String str) {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_5, (Object) this, (Object) this, (Object) str));
        XtraTag tagByName = getTagByName(str);
        if (tagByName == null) {
            return new Object[0];
        }
        Object[] objArr = new Object[tagByName.values.size()];
        for (int i = 0; i < tagByName.values.size(); i++) {
            objArr[i] = ((XtraValue) tagByName.values.elementAt(i)).getValueAsObject();
        }
        return objArr;
    }

    public void removeTag(String str) {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_6, (Object) this, (Object) this, (Object) str));
        XtraTag tagByName = getTagByName(str);
        if (tagByName != null) {
            this.tags.remove(tagByName);
        }
    }

    public void setTagValue(String str, long j) {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_10, this, this, str, Conversions.longObject(j)));
        removeTag(str);
        XtraTag xtraTag = new XtraTag(null);
        xtraTag.values.addElement(new XtraValue(null));
        this.tags.addElement(xtraTag);
    }

    public void setTagValue(String str, String str2) {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_8, this, this, str, str2));
        setTagValues(str, new String[]{str2});
    }

    public void setTagValue(String str, Date date) {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_9, this, this, str, date));
        removeTag(str);
        XtraTag xtraTag = new XtraTag(null);
        xtraTag.values.addElement(new XtraValue(null));
        this.tags.addElement(xtraTag);
    }

    public void setTagValues(String str, String[] strArr) {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_7, this, this, str, strArr));
        removeTag(str);
        XtraTag xtraTag = new XtraTag(null);
        for (String xtraValue : strArr) {
            xtraTag.values.addElement(new XtraValue(null));
        }
        this.tags.addElement(xtraTag);
    }

    public String toString() {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_0, this, this));
        if (!isParsed()) {
            parseDetails();
        }
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("XtraBox[");
        Iterator it = this.tags.iterator();
        while (it.hasNext()) {
            XtraTag xtraTag = (XtraTag) it.next();
            Iterator it2 = xtraTag.values.iterator();
            while (it2.hasNext()) {
                XtraValue xtraValue = (XtraValue) it2.next();
                stringBuffer.append(xtraTag.tagName);
                stringBuffer.append("=");
                stringBuffer.append(xtraValue.toString());
                stringBuffer.append(";");
            }
        }
        stringBuffer.append("]");
        return stringBuffer.toString();
    }
}
