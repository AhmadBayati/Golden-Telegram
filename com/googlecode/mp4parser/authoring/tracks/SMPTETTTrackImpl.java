package com.googlecode.mp4parser.authoring.tracks;

import android.support.v4.internal.view.SupportMenu;
import com.coremedia.iso.Utf8;
import com.coremedia.iso.boxes.SampleDescriptionBox;
import com.coremedia.iso.boxes.SubSampleInformationBox;
import com.coremedia.iso.boxes.SubSampleInformationBox.SubSampleEntry;
import com.coremedia.iso.boxes.SubSampleInformationBox.SubSampleEntry.SubsampleEntry;
import com.googlecode.mp4parser.authoring.AbstractTrack;
import com.googlecode.mp4parser.authoring.Sample;
import com.googlecode.mp4parser.authoring.TrackMetaData;
import com.googlecode.mp4parser.util.Iso639;
import com.mp4parser.iso14496.part30.XMLSubtitleSampleEntry;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class SMPTETTTrackImpl extends AbstractTrack {
    public static final String SMPTE_TT_NAMESPACE = "http://www.smpte-ra.org/schemas/2052-1/2010/smpte-tt";
    XMLSubtitleSampleEntry XMLSubtitleSampleEntry;
    boolean containsImages;
    SampleDescriptionBox sampleDescriptionBox;
    private long[] sampleDurations;
    List<Sample> samples;
    SubSampleInformationBox subSampleInformationBox;
    TrackMetaData trackMetaData;

    /* renamed from: com.googlecode.mp4parser.authoring.tracks.SMPTETTTrackImpl.1 */
    class C03301 implements Sample {
        private final /* synthetic */ String val$finalXml;
        private final /* synthetic */ List val$pix;

        C03301(String str, List list) {
            this.val$finalXml = str;
            this.val$pix = list;
        }

        public ByteBuffer asByteBuffer() {
            OutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            try {
                writeTo(Channels.newChannel(byteArrayOutputStream));
                return ByteBuffer.wrap(byteArrayOutputStream.toByteArray());
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }

        public long getSize() {
            long length = (long) Utf8.convert(this.val$finalXml).length;
            long j = length;
            for (File length2 : this.val$pix) {
                j = length2.length() + j;
            }
            return j;
        }

        public void writeTo(WritableByteChannel writableByteChannel) {
            writableByteChannel.write(ByteBuffer.wrap(Utf8.convert(this.val$finalXml)));
            for (File fileInputStream : this.val$pix) {
                FileInputStream fileInputStream2 = new FileInputStream(fileInputStream);
                byte[] bArr = new byte[8096];
                while (true) {
                    int read = fileInputStream2.read(bArr);
                    if (-1 != read) {
                        writableByteChannel.write(ByteBuffer.wrap(bArr, 0, read));
                    }
                }
            }
        }
    }

    /* renamed from: com.googlecode.mp4parser.authoring.tracks.SMPTETTTrackImpl.2 */
    class C03312 implements Sample {
        private final /* synthetic */ File val$file;

        C03312(File file) {
            this.val$file = file;
        }

        public ByteBuffer asByteBuffer() {
            try {
                return ByteBuffer.wrap(SMPTETTTrackImpl.this.streamToByteArray(new FileInputStream(this.val$file)));
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }

        public long getSize() {
            return this.val$file.length();
        }

        public void writeTo(WritableByteChannel writableByteChannel) {
            Channels.newOutputStream(writableByteChannel).write(SMPTETTTrackImpl.this.streamToByteArray(new FileInputStream(this.val$file)));
        }
    }

    private static class TextTrackNamespaceContext implements NamespaceContext {
        private TextTrackNamespaceContext() {
        }

        public String getNamespaceURI(String str) {
            return str.equals("ttml") ? "http://www.w3.org/ns/ttml" : str.equals("smpte") ? SMPTETTTrackImpl.SMPTE_TT_NAMESPACE : null;
        }

        public String getPrefix(String str) {
            return str.equals("http://www.w3.org/ns/ttml") ? "ttml" : str.equals(SMPTETTTrackImpl.SMPTE_TT_NAMESPACE) ? "smpte" : null;
        }

        public Iterator getPrefixes(String str) {
            return Arrays.asList(new String[]{"ttml", "smpte"}).iterator();
        }
    }

    public SMPTETTTrackImpl(File... fileArr) {
        super(fileArr[0].getName());
        this.trackMetaData = new TrackMetaData();
        this.sampleDescriptionBox = new SampleDescriptionBox();
        this.XMLSubtitleSampleEntry = new XMLSubtitleSampleEntry();
        this.samples = new ArrayList();
        this.subSampleInformationBox = new SubSampleInformationBox();
        this.sampleDurations = new long[fileArr.length];
        DocumentBuilderFactory newInstance = DocumentBuilderFactory.newInstance();
        newInstance.setNamespaceAware(true);
        DocumentBuilder newDocumentBuilder = newInstance.newDocumentBuilder();
        String str = null;
        int i = 0;
        long j = 0;
        while (i < fileArr.length) {
            String str2;
            File file = fileArr[i];
            SubSampleEntry subSampleEntry = new SubSampleEntry();
            this.subSampleInformationBox.getEntries().add(subSampleEntry);
            subSampleEntry.setSampleDelta(1);
            Document parse = newDocumentBuilder.parse(file);
            String language = getLanguage(parse);
            if (str == null) {
                str2 = language;
            } else if (str.equals(language)) {
                str2 = str;
            } else {
                throw new RuntimeException("Within one Track all sample documents need to have the same language");
            }
            XPathFactory newInstance2 = XPathFactory.newInstance();
            NamespaceContext textTrackNamespaceContext = new TextTrackNamespaceContext();
            XPath newXPath = newInstance2.newXPath();
            newXPath.setNamespaceContext(textTrackNamespaceContext);
            long latestTimestamp = latestTimestamp(parse);
            this.sampleDurations[i] = latestTimestamp - j;
            NodeList nodeList = (NodeList) newXPath.compile("/ttml:tt/ttml:body/ttml:div/@smpte:backgroundImage").evaluate(parse, XPathConstants.NODESET);
            HashMap hashMap = new HashMap();
            Collection hashSet = new HashSet();
            for (int i2 = 0; i2 < nodeList.getLength(); i2++) {
                hashSet.add(nodeList.item(i2).getNodeValue());
            }
            Collection<String> arrayList = new ArrayList(hashSet);
            Collections.sort((List) arrayList);
            int i3 = 1;
            for (String language2 : arrayList) {
                int i4 = i3 + 1;
                hashMap.put(language2, "urn:dece:container:subtitleimageindex:" + i3 + language2.substring(language2.lastIndexOf(".")));
                i3 = i4;
            }
            if (arrayList.isEmpty()) {
                this.samples.add(new C03312(file));
            } else {
                language2 = new String(streamToByteArray(new FileInputStream(file)));
                String str3 = language2;
                for (Entry entry : hashMap.entrySet()) {
                    str3 = str3.replace((CharSequence) entry.getKey(), (CharSequence) entry.getValue());
                }
                List arrayList2 = new ArrayList();
                this.samples.add(new C03301(str3, arrayList2));
                SubsampleEntry subsampleEntry = new SubsampleEntry();
                subsampleEntry.setSubsampleSize((long) Utf8.utf8StringLengthInBytes(str3));
                subSampleEntry.getSubsampleEntries().add(subsampleEntry);
                for (String language22 : arrayList) {
                    File file2 = new File(file.getParentFile(), language22);
                    arrayList2.add(file2);
                    subsampleEntry = new SubsampleEntry();
                    subsampleEntry.setSubsampleSize(file2.length());
                    subSampleEntry.getSubsampleEntries().add(subsampleEntry);
                }
            }
            i++;
            str = str2;
            j = latestTimestamp;
        }
        this.trackMetaData.setLanguage(Iso639.convert2to3(str));
        this.XMLSubtitleSampleEntry.setNamespace(SMPTE_TT_NAMESPACE);
        this.XMLSubtitleSampleEntry.setSchemaLocation(SMPTE_TT_NAMESPACE);
        if (this.containsImages) {
            this.XMLSubtitleSampleEntry.setAuxiliaryMimeTypes("image/png");
        } else {
            this.XMLSubtitleSampleEntry.setAuxiliaryMimeTypes(TtmlNode.ANONYMOUS_REGION_ID);
        }
        this.sampleDescriptionBox.addBox(this.XMLSubtitleSampleEntry);
        this.trackMetaData.setTimescale(30000);
        this.trackMetaData.setLayer(SupportMenu.USER_MASK);
    }

    public static long earliestTimestamp(Document document) {
        XPathFactory newInstance = XPathFactory.newInstance();
        NamespaceContext textTrackNamespaceContext = new TextTrackNamespaceContext();
        XPath newXPath = newInstance.newXPath();
        newXPath.setNamespaceContext(textTrackNamespaceContext);
        try {
            NodeList nodeList = (NodeList) newXPath.compile("//*[@begin]").evaluate(document, XPathConstants.NODESET);
            long j = 0;
            for (int i = 0; i < nodeList.getLength(); i++) {
                j = Math.min(toTime(nodeList.item(i).getAttributes().getNamedItem("begin").getNodeValue()), j);
            }
            return j;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static String getLanguage(Document document) {
        return document.getDocumentElement().getAttribute("xml:lang");
    }

    public static long latestTimestamp(Document document) {
        XPathFactory newInstance = XPathFactory.newInstance();
        NamespaceContext textTrackNamespaceContext = new TextTrackNamespaceContext();
        XPath newXPath = newInstance.newXPath();
        newXPath.setNamespaceContext(textTrackNamespaceContext);
        try {
            NodeList nodeList = (NodeList) newXPath.compile("//*[@begin]").evaluate(document, XPathConstants.NODESET);
            int i = 0;
            long j = 0;
            while (i < nodeList.getLength()) {
                long toTime;
                Node item = nodeList.item(i);
                String nodeValue = item.getAttributes().getNamedItem("begin").getNodeValue();
                if (item.getAttributes().getNamedItem("dur") != null) {
                    toTime = toTime(item.getAttributes().getNamedItem("dur").getNodeValue()) + toTime(nodeValue);
                } else if (item.getAttributes().getNamedItem(TtmlNode.END) != null) {
                    toTime = toTime(item.getAttributes().getNamedItem(TtmlNode.END).getNodeValue());
                } else {
                    throw new RuntimeException("neither end nor dur attribute is present");
                }
                i++;
                j = Math.max(toTime, j);
            }
            return j;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private byte[] streamToByteArray(InputStream inputStream) {
        byte[] bArr = new byte[8096];
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        while (true) {
            int read = inputStream.read(bArr);
            if (-1 == read) {
                return byteArrayOutputStream.toByteArray();
            }
            byteArrayOutputStream.write(bArr, 0, read);
        }
    }

    static long toTime(String str) {
        Matcher matcher = Pattern.compile("([0-9][0-9]):([0-9][0-9]):([0-9][0-9])([\\.:][0-9][0-9]?[0-9]?)?").matcher(str);
        if (matcher.matches()) {
            String group = matcher.group(1);
            String group2 = matcher.group(2);
            String group3 = matcher.group(3);
            String group4 = matcher.group(4);
            if (group4 == null) {
                group4 = ".000";
            }
            return (long) ((Double.parseDouble("0" + group4.replace(":", ".")) * 1000.0d) + ((double) ((Long.parseLong(group3) * 1000) + ((((Long.parseLong(group) * 60) * 60) * 1000) + ((Long.parseLong(group2) * 60) * 1000)))));
        }
        throw new RuntimeException("Cannot match " + str + " to time expression");
    }

    public void close() {
    }

    public String getHandler() {
        return "subt";
    }

    public SampleDescriptionBox getSampleDescriptionBox() {
        return this.sampleDescriptionBox;
    }

    public long[] getSampleDurations() {
        long[] jArr = new long[this.sampleDurations.length];
        for (int i = 0; i < jArr.length; i++) {
            jArr[i] = (this.sampleDurations[i] * this.trackMetaData.getTimescale()) / 1000;
        }
        return jArr;
    }

    public List<Sample> getSamples() {
        return this.samples;
    }

    public SubSampleInformationBox getSubsampleInformationBox() {
        return this.subSampleInformationBox;
    }

    public TrackMetaData getTrackMetaData() {
        return this.trackMetaData;
    }
}
