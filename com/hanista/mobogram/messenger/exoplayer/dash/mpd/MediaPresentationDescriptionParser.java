package com.hanista.mobogram.messenger.exoplayer.dash.mpd;

import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.util.Pair;
import com.google.android.gms.vision.face.Face;
import com.hanista.mobogram.messenger.exoplayer.ParserException;
import com.hanista.mobogram.messenger.exoplayer.chunk.Format;
import com.hanista.mobogram.messenger.exoplayer.dash.mpd.SegmentBase.SegmentList;
import com.hanista.mobogram.messenger.exoplayer.dash.mpd.SegmentBase.SegmentTemplate;
import com.hanista.mobogram.messenger.exoplayer.dash.mpd.SegmentBase.SegmentTimelineElement;
import com.hanista.mobogram.messenger.exoplayer.dash.mpd.SegmentBase.SingleSegmentBase;
import com.hanista.mobogram.messenger.exoplayer.drm.DrmInitData.SchemeInitData;
import com.hanista.mobogram.messenger.exoplayer.extractor.mp4.PsshAtomUtil;
import com.hanista.mobogram.messenger.exoplayer.upstream.UriLoadable.Parser;
import com.hanista.mobogram.messenger.exoplayer.util.Assertions;
import com.hanista.mobogram.messenger.exoplayer.util.MimeTypes;
import com.hanista.mobogram.messenger.exoplayer.util.ParserUtil;
import com.hanista.mobogram.messenger.exoplayer.util.UriUtil;
import com.hanista.mobogram.messenger.exoplayer.util.Util;
import com.mp4parser.iso14496.part30.WebVTTSampleEntry;
import com.mp4parser.iso14496.part30.XMLSubtitleSampleEntry;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.aspectj.lang.JoinPoint;
import org.xml.sax.helpers.DefaultHandler;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

public class MediaPresentationDescriptionParser extends DefaultHandler implements Parser<MediaPresentationDescription> {
    private static final Pattern FRAME_RATE_PATTERN;
    private static final String TAG = "MediaPresentationDescriptionParser";
    private final String contentId;
    private final XmlPullParserFactory xmlParserFactory;

    protected static final class ContentProtectionsBuilder implements Comparator<ContentProtection> {
        private ArrayList<ContentProtection> adaptationSetProtections;
        private ArrayList<ContentProtection> currentRepresentationProtections;
        private ArrayList<ContentProtection> representationProtections;
        private boolean representationProtectionsSet;

        protected ContentProtectionsBuilder() {
        }

        private void maybeAddContentProtection(List<ContentProtection> list, ContentProtection contentProtection) {
            if (!list.contains(contentProtection)) {
                for (int i = 0; i < list.size(); i++) {
                    Assertions.checkState(!((ContentProtection) list.get(i)).schemeUriId.equals(contentProtection.schemeUriId));
                }
                list.add(contentProtection);
            }
        }

        public void addAdaptationSetProtection(ContentProtection contentProtection) {
            if (this.adaptationSetProtections == null) {
                this.adaptationSetProtections = new ArrayList();
            }
            maybeAddContentProtection(this.adaptationSetProtections, contentProtection);
        }

        public void addRepresentationProtection(ContentProtection contentProtection) {
            if (this.currentRepresentationProtections == null) {
                this.currentRepresentationProtections = new ArrayList();
            }
            maybeAddContentProtection(this.currentRepresentationProtections, contentProtection);
        }

        public ArrayList<ContentProtection> build() {
            if (this.adaptationSetProtections == null) {
                return this.representationProtections;
            }
            if (this.representationProtections == null) {
                return this.adaptationSetProtections;
            }
            for (int i = 0; i < this.representationProtections.size(); i++) {
                maybeAddContentProtection(this.adaptationSetProtections, (ContentProtection) this.representationProtections.get(i));
            }
            return this.adaptationSetProtections;
        }

        public int compare(ContentProtection contentProtection, ContentProtection contentProtection2) {
            return contentProtection.schemeUriId.compareTo(contentProtection2.schemeUriId);
        }

        public void endRepresentation() {
            boolean z = true;
            if (!this.representationProtectionsSet) {
                if (this.currentRepresentationProtections != null) {
                    Collections.sort(this.currentRepresentationProtections, this);
                }
                this.representationProtections = this.currentRepresentationProtections;
                this.representationProtectionsSet = true;
            } else if (this.currentRepresentationProtections == null) {
                if (this.representationProtections != null) {
                    z = false;
                }
                Assertions.checkState(z);
            } else {
                Collections.sort(this.currentRepresentationProtections, this);
                Assertions.checkState(this.currentRepresentationProtections.equals(this.representationProtections));
            }
            this.currentRepresentationProtections = null;
        }
    }

    static {
        FRAME_RATE_PATTERN = Pattern.compile("(\\d+)(?:/(\\d+))?");
    }

    public MediaPresentationDescriptionParser() {
        this(null);
    }

    public MediaPresentationDescriptionParser(String str) {
        this.contentId = str;
        try {
            this.xmlParserFactory = XmlPullParserFactory.newInstance();
        } catch (Throwable e) {
            throw new RuntimeException("Couldn't create XmlPullParserFactory instance", e);
        }
    }

    private static int checkContentTypeConsistency(int i, int i2) {
        if (i == -1) {
            return i2;
        }
        if (i2 == -1) {
            return i;
        }
        Assertions.checkState(i == i2);
        return i;
    }

    private static String checkLanguageConsistency(String str, String str2) {
        if (str == null) {
            return str2;
        }
        if (str2 == null) {
            return str;
        }
        Assertions.checkState(str.equals(str2));
        return str;
    }

    protected static String parseBaseUrl(XmlPullParser xmlPullParser, String str) {
        xmlPullParser.next();
        return UriUtil.resolve(str, xmlPullParser.getText());
    }

    protected static long parseDateTime(XmlPullParser xmlPullParser, String str, long j) {
        String attributeValue = xmlPullParser.getAttributeValue(null, str);
        return attributeValue == null ? j : Util.parseXsDateTime(attributeValue);
    }

    protected static long parseDuration(XmlPullParser xmlPullParser, String str, long j) {
        String attributeValue = xmlPullParser.getAttributeValue(null, str);
        return attributeValue == null ? j : Util.parseXsDuration(attributeValue);
    }

    protected static float parseFrameRate(XmlPullParser xmlPullParser, float f) {
        CharSequence attributeValue = xmlPullParser.getAttributeValue(null, "frameRate");
        if (attributeValue == null) {
            return f;
        }
        Matcher matcher = FRAME_RATE_PATTERN.matcher(attributeValue);
        if (!matcher.matches()) {
            return f;
        }
        int parseInt = Integer.parseInt(matcher.group(1));
        Object group = matcher.group(2);
        return !TextUtils.isEmpty(group) ? ((float) parseInt) / ((float) Integer.parseInt(group)) : (float) parseInt;
    }

    protected static int parseInt(XmlPullParser xmlPullParser, String str) {
        return parseInt(xmlPullParser, str, -1);
    }

    protected static int parseInt(XmlPullParser xmlPullParser, String str, int i) {
        String attributeValue = xmlPullParser.getAttributeValue(null, str);
        return attributeValue == null ? i : Integer.parseInt(attributeValue);
    }

    protected static long parseLong(XmlPullParser xmlPullParser, String str) {
        return parseLong(xmlPullParser, str, -1);
    }

    protected static long parseLong(XmlPullParser xmlPullParser, String str, long j) {
        String attributeValue = xmlPullParser.getAttributeValue(null, str);
        return attributeValue == null ? j : Long.parseLong(attributeValue);
    }

    protected static String parseString(XmlPullParser xmlPullParser, String str, String str2) {
        String attributeValue = xmlPullParser.getAttributeValue(null, str);
        return attributeValue == null ? str2 : attributeValue;
    }

    protected AdaptationSet buildAdaptationSet(int i, int i2, List<Representation> list, List<ContentProtection> list2) {
        return new AdaptationSet(i, i2, list, list2);
    }

    protected ContentProtection buildContentProtection(String str, UUID uuid, SchemeInitData schemeInitData) {
        return new ContentProtection(str, uuid, schemeInitData);
    }

    protected Format buildFormat(String str, String str2, int i, int i2, float f, int i3, int i4, int i5, String str3, String str4) {
        return new Format(str, str2, i, i2, f, i3, i4, i5, str3, str4);
    }

    protected MediaPresentationDescription buildMediaPresentationDescription(long j, long j2, long j3, boolean z, long j4, long j5, UtcTimingElement utcTimingElement, String str, List<Period> list) {
        return new MediaPresentationDescription(j, j2, j3, z, j4, j5, utcTimingElement, str, list);
    }

    protected Period buildPeriod(String str, long j, List<AdaptationSet> list) {
        return new Period(str, j, list);
    }

    protected RangedUri buildRangedUri(String str, String str2, long j, long j2) {
        return new RangedUri(str, str2, j, j2);
    }

    protected Representation buildRepresentation(String str, int i, Format format, SegmentBase segmentBase) {
        return Representation.newInstance(str, (long) i, format, segmentBase);
    }

    protected SegmentList buildSegmentList(RangedUri rangedUri, long j, long j2, int i, long j3, List<SegmentTimelineElement> list, List<RangedUri> list2) {
        return new SegmentList(rangedUri, j, j2, i, j3, list, list2);
    }

    protected SegmentTemplate buildSegmentTemplate(RangedUri rangedUri, long j, long j2, int i, long j3, List<SegmentTimelineElement> list, UrlTemplate urlTemplate, UrlTemplate urlTemplate2, String str) {
        return new SegmentTemplate(rangedUri, j, j2, i, j3, list, urlTemplate, urlTemplate2, str);
    }

    protected SegmentTimelineElement buildSegmentTimelineElement(long j, long j2) {
        return new SegmentTimelineElement(j, j2);
    }

    protected SingleSegmentBase buildSingleSegmentBase(RangedUri rangedUri, long j, long j2, String str, long j3, long j4) {
        return new SingleSegmentBase(rangedUri, j, j2, str, j3, j4);
    }

    protected UtcTimingElement buildUtcTimingElement(String str, String str2) {
        return new UtcTimingElement(str, str2);
    }

    protected int getContentType(Representation representation) {
        String str = representation.format.mimeType;
        if (TextUtils.isEmpty(str)) {
            return -1;
        }
        if (MimeTypes.isVideo(str)) {
            return 0;
        }
        if (MimeTypes.isAudio(str)) {
            return 1;
        }
        if (MimeTypes.isText(str) || MimeTypes.APPLICATION_TTML.equals(str)) {
            return 2;
        }
        if (!MimeTypes.APPLICATION_MP4.equals(str)) {
            return -1;
        }
        str = representation.format.codecs;
        return (XMLSubtitleSampleEntry.TYPE.equals(str) || WebVTTSampleEntry.TYPE.equals(str)) ? 2 : -1;
    }

    public MediaPresentationDescription parse(String str, InputStream inputStream) {
        try {
            XmlPullParser newPullParser = this.xmlParserFactory.newPullParser();
            newPullParser.setInput(inputStream, null);
            if (newPullParser.next() == 2 && "MPD".equals(newPullParser.getName())) {
                return parseMediaPresentationDescription(newPullParser, str);
            }
            throw new ParserException("inputStream does not contain a valid media presentation description");
        } catch (Throwable e) {
            throw new ParserException(e);
        } catch (Throwable e2) {
            throw new ParserException(e2);
        }
    }

    protected AdaptationSet parseAdaptationSet(XmlPullParser xmlPullParser, String str, SegmentBase segmentBase) {
        int parseInt = parseInt(xmlPullParser, TtmlNode.ATTR_ID, -1);
        int parseContentType = parseContentType(xmlPullParser);
        String attributeValue = xmlPullParser.getAttributeValue(null, "mimeType");
        String attributeValue2 = xmlPullParser.getAttributeValue(null, "codecs");
        int parseInt2 = parseInt(xmlPullParser, "width", -1);
        int parseInt3 = parseInt(xmlPullParser, "height", -1);
        float parseFrameRate = parseFrameRate(xmlPullParser, Face.UNCOMPUTED_PROBABILITY);
        int i = -1;
        int parseInt4 = parseInt(xmlPullParser, "audioSamplingRate", -1);
        String attributeValue3 = xmlPullParser.getAttributeValue(null, "lang");
        ContentProtectionsBuilder contentProtectionsBuilder = new ContentProtectionsBuilder();
        ArrayList arrayList = new ArrayList();
        Object obj = null;
        SegmentBase segmentBase2 = segmentBase;
        String str2 = str;
        while (true) {
            Object obj2;
            int i2;
            xmlPullParser.next();
            if (ParserUtil.isStartTag(xmlPullParser, "BaseURL")) {
                if (obj == null) {
                    str2 = parseBaseUrl(xmlPullParser, str2);
                    obj2 = 1;
                    i2 = parseContentType;
                }
                obj2 = obj;
                i2 = parseContentType;
            } else {
                if (ParserUtil.isStartTag(xmlPullParser, "ContentProtection")) {
                    ContentProtection parseContentProtection = parseContentProtection(xmlPullParser);
                    if (parseContentProtection != null) {
                        contentProtectionsBuilder.addAdaptationSetProtection(parseContentProtection);
                    }
                    obj2 = obj;
                    i2 = parseContentType;
                } else {
                    if (ParserUtil.isStartTag(xmlPullParser, "ContentComponent")) {
                        attributeValue3 = checkLanguageConsistency(attributeValue3, xmlPullParser.getAttributeValue(null, "lang"));
                        i2 = checkContentTypeConsistency(parseContentType, parseContentType(xmlPullParser));
                        obj2 = obj;
                    } else {
                        if (ParserUtil.isStartTag(xmlPullParser, "Representation")) {
                            Representation parseRepresentation = parseRepresentation(xmlPullParser, str2, attributeValue, attributeValue2, parseInt2, parseInt3, parseFrameRate, i, parseInt4, attributeValue3, segmentBase2, contentProtectionsBuilder);
                            contentProtectionsBuilder.endRepresentation();
                            int checkContentTypeConsistency = checkContentTypeConsistency(parseContentType, getContentType(parseRepresentation));
                            arrayList.add(parseRepresentation);
                            i2 = checkContentTypeConsistency;
                            obj2 = obj;
                        } else {
                            if (ParserUtil.isStartTag(xmlPullParser, "AudioChannelConfiguration")) {
                                i = parseAudioChannelConfiguration(xmlPullParser);
                                obj2 = obj;
                                i2 = parseContentType;
                            } else {
                                if (ParserUtil.isStartTag(xmlPullParser, "SegmentBase")) {
                                    segmentBase2 = parseSegmentBase(xmlPullParser, str2, (SingleSegmentBase) segmentBase2);
                                    obj2 = obj;
                                    i2 = parseContentType;
                                } else {
                                    if (ParserUtil.isStartTag(xmlPullParser, "SegmentList")) {
                                        segmentBase2 = parseSegmentList(xmlPullParser, str2, (SegmentList) segmentBase2);
                                        obj2 = obj;
                                        i2 = parseContentType;
                                    } else {
                                        if (ParserUtil.isStartTag(xmlPullParser, "SegmentTemplate")) {
                                            segmentBase2 = parseSegmentTemplate(xmlPullParser, str2, (SegmentTemplate) segmentBase2);
                                            obj2 = obj;
                                            i2 = parseContentType;
                                        } else {
                                            if (ParserUtil.isStartTag(xmlPullParser)) {
                                                parseAdaptationSetChild(xmlPullParser);
                                            }
                                            obj2 = obj;
                                            i2 = parseContentType;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (ParserUtil.isEndTag(xmlPullParser, "AdaptationSet")) {
                return buildAdaptationSet(parseInt, i2, arrayList, contentProtectionsBuilder.build());
            }
            obj = obj2;
            parseContentType = i2;
        }
    }

    protected void parseAdaptationSetChild(XmlPullParser xmlPullParser) {
    }

    protected int parseAudioChannelConfiguration(XmlPullParser xmlPullParser) {
        int parseInt = "urn:mpeg:dash:23003:3:audio_channel_configuration:2011".equals(parseString(xmlPullParser, "schemeIdUri", null)) ? parseInt(xmlPullParser, "value") : -1;
        do {
            xmlPullParser.next();
        } while (!ParserUtil.isEndTag(xmlPullParser, "AudioChannelConfiguration"));
        return parseInt;
    }

    protected ContentProtection parseContentProtection(XmlPullParser xmlPullParser) {
        String attributeValue = xmlPullParser.getAttributeValue(null, "schemeIdUri");
        int i = 0;
        SchemeInitData schemeInitData = null;
        UUID uuid = null;
        do {
            xmlPullParser.next();
            if (ParserUtil.isStartTag(xmlPullParser, "cenc:pssh") && xmlPullParser.next() == 4) {
                i = 1;
                schemeInitData = new SchemeInitData(MimeTypes.VIDEO_MP4, Base64.decode(xmlPullParser.getText(), 0));
                uuid = PsshAtomUtil.parseUuid(schemeInitData.data);
            }
        } while (!ParserUtil.isEndTag(xmlPullParser, "ContentProtection"));
        if (i == 0 || uuid != null) {
            return buildContentProtection(attributeValue, uuid, schemeInitData);
        }
        Log.w(TAG, "Skipped unsupported ContentProtection element");
        return null;
    }

    protected int parseContentType(XmlPullParser xmlPullParser) {
        CharSequence attributeValue = xmlPullParser.getAttributeValue(null, "contentType");
        return TextUtils.isEmpty(attributeValue) ? -1 : MimeTypes.BASE_TYPE_AUDIO.equals(attributeValue) ? 1 : MimeTypes.BASE_TYPE_VIDEO.equals(attributeValue) ? 0 : MimeTypes.BASE_TYPE_TEXT.equals(attributeValue) ? 2 : -1;
    }

    protected RangedUri parseInitialization(XmlPullParser xmlPullParser, String str) {
        return parseRangedUrl(xmlPullParser, str, "sourceURL", "range");
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    protected com.hanista.mobogram.messenger.exoplayer.dash.mpd.MediaPresentationDescription parseMediaPresentationDescription(org.xmlpull.v1.XmlPullParser r34, java.lang.String r35) {
        /*
        r33 = this;
        r6 = "availabilityStartTime";
        r8 = -1;
        r0 = r34;
        r8 = parseDateTime(r0, r6, r8);
        r6 = "mediaPresentationDuration";
        r10 = -1;
        r0 = r34;
        r26 = parseDuration(r0, r6, r10);
        r6 = "minBufferTime";
        r10 = -1;
        r0 = r34;
        r12 = parseDuration(r0, r6, r10);
        r6 = 0;
        r7 = "type";
        r0 = r34;
        r6 = r0.getAttributeValue(r6, r7);
        if (r6 == 0) goto L_0x00aa;
    L_0x002d:
        r7 = "dynamic";
        r14 = r6.equals(r7);
    L_0x0034:
        if (r14 == 0) goto L_0x00ac;
    L_0x0036:
        r6 = "minimumUpdatePeriod";
        r10 = -1;
        r0 = r34;
        r15 = parseDuration(r0, r6, r10);
    L_0x0041:
        if (r14 == 0) goto L_0x00af;
    L_0x0043:
        r6 = "timeShiftBufferDepth";
        r10 = -1;
        r0 = r34;
        r17 = parseDuration(r0, r6, r10);
    L_0x004e:
        r19 = 0;
        r20 = 0;
        r21 = new java.util.ArrayList;
        r21.<init>();
        if (r14 == 0) goto L_0x00b2;
    L_0x0059:
        r6 = -1;
    L_0x005b:
        r11 = 0;
        r10 = 0;
        r22 = r6;
        r24 = r35;
    L_0x0061:
        r34.next();
        r6 = "BaseURL";
        r0 = r34;
        r6 = com.hanista.mobogram.messenger.exoplayer.util.ParserUtil.isStartTag(r0, r6);
        if (r6 == 0) goto L_0x00b5;
    L_0x006f:
        if (r10 != 0) goto L_0x0146;
    L_0x0071:
        r0 = r34;
        r1 = r24;
        r7 = parseBaseUrl(r0, r1);
        r6 = 1;
        r32 = r11;
        r10 = r22;
        r22 = r7;
        r7 = r32;
    L_0x0082:
        r23 = "MPD";
        r0 = r34;
        r1 = r23;
        r23 = com.hanista.mobogram.messenger.exoplayer.util.ParserUtil.isEndTag(r0, r1);
        if (r23 == 0) goto L_0x016b;
    L_0x008f:
        r6 = -1;
        r6 = (r26 > r6 ? 1 : (r26 == r6 ? 0 : -1));
        if (r6 != 0) goto L_0x0167;
    L_0x0095:
        r6 = -1;
        r6 = (r10 > r6 ? 1 : (r10 == r6 ? 0 : -1));
        if (r6 == 0) goto L_0x0155;
    L_0x009b:
        r6 = r21.isEmpty();
        if (r6 == 0) goto L_0x0160;
    L_0x00a1:
        r6 = new com.hanista.mobogram.messenger.exoplayer.ParserException;
        r7 = "No periods found.";
        r6.<init>(r7);
        throw r6;
    L_0x00aa:
        r14 = 0;
        goto L_0x0034;
    L_0x00ac:
        r15 = -1;
        goto L_0x0041;
    L_0x00af:
        r17 = -1;
        goto L_0x004e;
    L_0x00b2:
        r6 = 0;
        goto L_0x005b;
    L_0x00b5:
        r6 = "UTCTiming";
        r0 = r34;
        r6 = com.hanista.mobogram.messenger.exoplayer.util.ParserUtil.isStartTag(r0, r6);
        if (r6 == 0) goto L_0x00cb;
    L_0x00c0:
        r19 = r33.parseUtcTiming(r34);
        r6 = r10;
        r7 = r11;
        r10 = r22;
        r22 = r24;
        goto L_0x0082;
    L_0x00cb:
        r6 = "Location";
        r0 = r34;
        r6 = com.hanista.mobogram.messenger.exoplayer.util.ParserUtil.isStartTag(r0, r6);
        if (r6 == 0) goto L_0x00e1;
    L_0x00d6:
        r20 = r34.nextText();
        r6 = r10;
        r7 = r11;
        r10 = r22;
        r22 = r24;
        goto L_0x0082;
    L_0x00e1:
        r6 = "Period";
        r0 = r34;
        r6 = com.hanista.mobogram.messenger.exoplayer.util.ParserUtil.isStartTag(r0, r6);
        if (r6 == 0) goto L_0x0146;
    L_0x00ec:
        if (r11 != 0) goto L_0x0146;
    L_0x00ee:
        r0 = r33;
        r1 = r34;
        r2 = r24;
        r3 = r22;
        r7 = r0.parsePeriod(r1, r2, r3);
        r6 = r7.first;
        r6 = (com.hanista.mobogram.messenger.exoplayer.dash.mpd.Period) r6;
        r0 = r6.startMs;
        r28 = r0;
        r30 = -1;
        r25 = (r28 > r30 ? 1 : (r28 == r30 ? 0 : -1));
        if (r25 != 0) goto L_0x0131;
    L_0x0108:
        if (r14 == 0) goto L_0x0113;
    L_0x010a:
        r6 = 1;
        r7 = r6;
        r6 = r10;
        r10 = r22;
        r22 = r24;
        goto L_0x0082;
    L_0x0113:
        r6 = new com.hanista.mobogram.messenger.exoplayer.ParserException;
        r7 = new java.lang.StringBuilder;
        r7.<init>();
        r8 = "Unable to determine start of period ";
        r7 = r7.append(r8);
        r8 = r21.size();
        r7 = r7.append(r8);
        r7 = r7.toString();
        r6.<init>(r7);
        throw r6;
    L_0x0131:
        r7 = r7.second;
        r7 = (java.lang.Long) r7;
        r22 = r7.longValue();
        r28 = -1;
        r7 = (r22 > r28 ? 1 : (r22 == r28 ? 0 : -1));
        if (r7 != 0) goto L_0x014e;
    L_0x013f:
        r22 = -1;
    L_0x0141:
        r0 = r21;
        r0.add(r6);
    L_0x0146:
        r6 = r10;
        r7 = r11;
        r10 = r22;
        r22 = r24;
        goto L_0x0082;
    L_0x014e:
        r0 = r6.startMs;
        r28 = r0;
        r22 = r22 + r28;
        goto L_0x0141;
    L_0x0155:
        if (r14 != 0) goto L_0x0167;
    L_0x0157:
        r6 = new com.hanista.mobogram.messenger.exoplayer.ParserException;
        r7 = "Unable to determine duration of static manifest.";
        r6.<init>(r7);
        throw r6;
    L_0x0160:
        r7 = r33;
        r6 = r7.buildMediaPresentationDescription(r8, r10, r12, r14, r15, r17, r19, r20, r21);
        return r6;
    L_0x0167:
        r10 = r26;
        goto L_0x009b;
    L_0x016b:
        r24 = r22;
        r22 = r10;
        r11 = r7;
        r10 = r6;
        goto L_0x0061;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.hanista.mobogram.messenger.exoplayer.dash.mpd.MediaPresentationDescriptionParser.parseMediaPresentationDescription(org.xmlpull.v1.XmlPullParser, java.lang.String):com.hanista.mobogram.messenger.exoplayer.dash.mpd.MediaPresentationDescription");
    }

    protected Pair<Period, Long> parsePeriod(XmlPullParser xmlPullParser, String str, long j) {
        String attributeValue = xmlPullParser.getAttributeValue(null, TtmlNode.ATTR_ID);
        long parseDuration = parseDuration(xmlPullParser, TtmlNode.START, j);
        long parseDuration2 = parseDuration(xmlPullParser, "duration", -1);
        List arrayList = new ArrayList();
        Object obj = null;
        SegmentBase segmentBase = null;
        String str2 = str;
        do {
            xmlPullParser.next();
            if (ParserUtil.isStartTag(xmlPullParser, "BaseURL")) {
                if (obj == null) {
                    str2 = parseBaseUrl(xmlPullParser, str2);
                    obj = 1;
                }
            } else if (ParserUtil.isStartTag(xmlPullParser, "AdaptationSet")) {
                arrayList.add(parseAdaptationSet(xmlPullParser, str2, segmentBase));
            } else if (ParserUtil.isStartTag(xmlPullParser, "SegmentBase")) {
                segmentBase = parseSegmentBase(xmlPullParser, str2, null);
            } else if (ParserUtil.isStartTag(xmlPullParser, "SegmentList")) {
                segmentBase = parseSegmentList(xmlPullParser, str2, null);
            } else if (ParserUtil.isStartTag(xmlPullParser, "SegmentTemplate")) {
                segmentBase = parseSegmentTemplate(xmlPullParser, str2, null);
            }
        } while (!ParserUtil.isEndTag(xmlPullParser, "Period"));
        return Pair.create(buildPeriod(attributeValue, parseDuration, arrayList), Long.valueOf(parseDuration2));
    }

    protected RangedUri parseRangedUrl(XmlPullParser xmlPullParser, String str, String str2, String str3) {
        String attributeValue = xmlPullParser.getAttributeValue(null, str2);
        long j = 0;
        long j2 = -1;
        String attributeValue2 = xmlPullParser.getAttributeValue(null, str3);
        if (attributeValue2 != null) {
            String[] split = attributeValue2.split("-");
            j = Long.parseLong(split[0]);
            if (split.length == 2) {
                j2 = 1 + (Long.parseLong(split[1]) - j);
            }
        }
        return buildRangedUri(str, attributeValue, j, j2);
    }

    protected Representation parseRepresentation(XmlPullParser xmlPullParser, String str, String str2, String str3, int i, int i2, float f, int i3, int i4, String str4, SegmentBase segmentBase, ContentProtectionsBuilder contentProtectionsBuilder) {
        SegmentBase segmentBase2;
        int i5;
        String attributeValue = xmlPullParser.getAttributeValue(null, TtmlNode.ATTR_ID);
        int parseInt = parseInt(xmlPullParser, "bandwidth");
        String parseString = parseString(xmlPullParser, "mimeType", str2);
        String parseString2 = parseString(xmlPullParser, "codecs", str3);
        int parseInt2 = parseInt(xmlPullParser, "width", i);
        int parseInt3 = parseInt(xmlPullParser, "height", i2);
        float parseFrameRate = parseFrameRate(xmlPullParser, f);
        int parseInt4 = parseInt(xmlPullParser, "audioSamplingRate", i4);
        Object obj = null;
        int i6 = i3;
        SegmentBase segmentBase3 = segmentBase;
        String str5 = str;
        while (true) {
            String parseBaseUrl;
            Object obj2;
            xmlPullParser.next();
            if (ParserUtil.isStartTag(xmlPullParser, "BaseURL")) {
                if (obj == null) {
                    parseBaseUrl = parseBaseUrl(xmlPullParser, str5);
                    segmentBase2 = segmentBase3;
                    obj2 = 1;
                    i5 = i6;
                }
                parseBaseUrl = str5;
                segmentBase2 = segmentBase3;
                obj2 = obj;
                i5 = i6;
            } else if (ParserUtil.isStartTag(xmlPullParser, "AudioChannelConfiguration")) {
                parseBaseUrl = str5;
                segmentBase2 = segmentBase3;
                obj2 = obj;
                i5 = parseAudioChannelConfiguration(xmlPullParser);
            } else if (ParserUtil.isStartTag(xmlPullParser, "SegmentBase")) {
                parseBaseUrl = str5;
                segmentBase2 = parseSegmentBase(xmlPullParser, str5, (SingleSegmentBase) segmentBase3);
                obj2 = obj;
                i5 = i6;
            } else if (ParserUtil.isStartTag(xmlPullParser, "SegmentList")) {
                parseBaseUrl = str5;
                segmentBase2 = parseSegmentList(xmlPullParser, str5, (SegmentList) segmentBase3);
                obj2 = obj;
                i5 = i6;
            } else if (ParserUtil.isStartTag(xmlPullParser, "SegmentTemplate")) {
                parseBaseUrl = str5;
                segmentBase2 = parseSegmentTemplate(xmlPullParser, str5, (SegmentTemplate) segmentBase3);
                obj2 = obj;
                i5 = i6;
            } else {
                if (ParserUtil.isStartTag(xmlPullParser, "ContentProtection")) {
                    ContentProtection parseContentProtection = parseContentProtection(xmlPullParser);
                    if (parseContentProtection != null) {
                        contentProtectionsBuilder.addAdaptationSetProtection(parseContentProtection);
                    }
                }
                parseBaseUrl = str5;
                segmentBase2 = segmentBase3;
                obj2 = obj;
                i5 = i6;
            }
            if (ParserUtil.isEndTag(xmlPullParser, "Representation")) {
                break;
            }
            i6 = i5;
            obj = obj2;
            segmentBase3 = segmentBase2;
            str5 = parseBaseUrl;
        }
        Format buildFormat = buildFormat(attributeValue, parseString, parseInt2, parseInt3, parseFrameRate, i5, parseInt4, parseInt, str4, parseString2);
        attributeValue = this.contentId;
        if (segmentBase2 == null) {
            segmentBase2 = new SingleSegmentBase(parseBaseUrl);
        }
        return buildRepresentation(attributeValue, -1, buildFormat, segmentBase2);
    }

    protected SingleSegmentBase parseSegmentBase(XmlPullParser xmlPullParser, String str, SingleSegmentBase singleSegmentBase) {
        long parseLong;
        long parseLong2 = parseLong(xmlPullParser, "timescale", singleSegmentBase != null ? singleSegmentBase.timescale : 1);
        long parseLong3 = parseLong(xmlPullParser, "presentationTimeOffset", singleSegmentBase != null ? singleSegmentBase.presentationTimeOffset : 0);
        long j = singleSegmentBase != null ? singleSegmentBase.indexStart : 0;
        long j2 = singleSegmentBase != null ? singleSegmentBase.indexLength : -1;
        String attributeValue = xmlPullParser.getAttributeValue(null, "indexRange");
        if (attributeValue != null) {
            String[] split = attributeValue.split("-");
            j = Long.parseLong(split[0]);
            parseLong = (Long.parseLong(split[1]) - j) + 1;
        } else {
            parseLong = j2;
        }
        RangedUri rangedUri = singleSegmentBase != null ? singleSegmentBase.initialization : null;
        while (true) {
            xmlPullParser.next();
            RangedUri parseInitialization = ParserUtil.isStartTag(xmlPullParser, "Initialization") ? parseInitialization(xmlPullParser, str) : rangedUri;
            if (ParserUtil.isEndTag(xmlPullParser, "SegmentBase")) {
                return buildSingleSegmentBase(parseInitialization, parseLong2, parseLong3, str, j, parseLong);
            }
            rangedUri = parseInitialization;
        }
    }

    protected SegmentList parseSegmentList(XmlPullParser xmlPullParser, String str, SegmentList segmentList) {
        List list;
        RangedUri rangedUri;
        List list2;
        RangedUri rangedUri2 = null;
        long parseLong = parseLong(xmlPullParser, "timescale", segmentList != null ? segmentList.timescale : 1);
        long parseLong2 = parseLong(xmlPullParser, "presentationTimeOffset", segmentList != null ? segmentList.presentationTimeOffset : 0);
        long parseLong3 = parseLong(xmlPullParser, "duration", segmentList != null ? segmentList.duration : -1);
        int parseInt = parseInt(xmlPullParser, "startNumber", segmentList != null ? segmentList.startNumber : 1);
        List list3 = null;
        List list4 = null;
        do {
            xmlPullParser.next();
            if (ParserUtil.isStartTag(xmlPullParser, "Initialization")) {
                rangedUri2 = parseInitialization(xmlPullParser, str);
            } else if (ParserUtil.isStartTag(xmlPullParser, "SegmentTimeline")) {
                list4 = parseSegmentTimeline(xmlPullParser);
            } else if (ParserUtil.isStartTag(xmlPullParser, "SegmentURL")) {
                if (list3 == null) {
                    list3 = new ArrayList();
                }
                list3.add(parseSegmentUrl(xmlPullParser, str));
            }
        } while (!ParserUtil.isEndTag(xmlPullParser, "SegmentList"));
        if (segmentList != null) {
            RangedUri rangedUri3 = rangedUri2 != null ? rangedUri2 : segmentList.initialization;
            list = list4 != null ? list4 : segmentList.segmentTimeline;
            if (list3 == null) {
                list3 = segmentList.mediaSegments;
            }
            rangedUri = rangedUri3;
            list2 = list3;
        } else {
            list2 = list3;
            List list5 = list4;
            rangedUri = rangedUri2;
            list = list5;
        }
        return buildSegmentList(rangedUri, parseLong, parseLong2, parseInt, parseLong3, list, list2);
    }

    protected SegmentTemplate parseSegmentTemplate(XmlPullParser xmlPullParser, String str, SegmentTemplate segmentTemplate) {
        List list;
        long parseLong = parseLong(xmlPullParser, "timescale", segmentTemplate != null ? segmentTemplate.timescale : 1);
        long parseLong2 = parseLong(xmlPullParser, "presentationTimeOffset", segmentTemplate != null ? segmentTemplate.presentationTimeOffset : 0);
        long parseLong3 = parseLong(xmlPullParser, "duration", segmentTemplate != null ? segmentTemplate.duration : -1);
        int parseInt = parseInt(xmlPullParser, "startNumber", segmentTemplate != null ? segmentTemplate.startNumber : 1);
        UrlTemplate parseUrlTemplate = parseUrlTemplate(xmlPullParser, "media", segmentTemplate != null ? segmentTemplate.mediaTemplate : null);
        UrlTemplate parseUrlTemplate2 = parseUrlTemplate(xmlPullParser, JoinPoint.INITIALIZATION, segmentTemplate != null ? segmentTemplate.initializationTemplate : null);
        RangedUri rangedUri = null;
        List list2 = null;
        do {
            xmlPullParser.next();
            if (ParserUtil.isStartTag(xmlPullParser, "Initialization")) {
                rangedUri = parseInitialization(xmlPullParser, str);
            } else {
                if (ParserUtil.isStartTag(xmlPullParser, "SegmentTimeline")) {
                    list2 = parseSegmentTimeline(xmlPullParser);
                }
            }
        } while (!ParserUtil.isEndTag(xmlPullParser, "SegmentTemplate"));
        if (segmentTemplate != null) {
            if (rangedUri == null) {
                rangedUri = segmentTemplate.initialization;
            }
            if (list2 == null) {
                list2 = segmentTemplate.segmentTimeline;
            }
            list = list2;
        } else {
            list = list2;
        }
        return buildSegmentTemplate(rangedUri, parseLong, parseLong2, parseInt, parseLong3, list, parseUrlTemplate2, parseUrlTemplate, str);
    }

    protected List<SegmentTimelineElement> parseSegmentTimeline(XmlPullParser xmlPullParser) {
        List<SegmentTimelineElement> arrayList = new ArrayList();
        long j = 0;
        do {
            xmlPullParser.next();
            if (ParserUtil.isStartTag(xmlPullParser, "S")) {
                j = parseLong(xmlPullParser, "t", j);
                long parseLong = parseLong(xmlPullParser, "d");
                int parseInt = parseInt(xmlPullParser, "r", 0) + 1;
                int i = 0;
                while (i < parseInt) {
                    arrayList.add(buildSegmentTimelineElement(j, parseLong));
                    i++;
                    j += parseLong;
                }
            }
        } while (!ParserUtil.isEndTag(xmlPullParser, "SegmentTimeline"));
        return arrayList;
    }

    protected RangedUri parseSegmentUrl(XmlPullParser xmlPullParser, String str) {
        return parseRangedUrl(xmlPullParser, str, "media", "mediaRange");
    }

    protected UrlTemplate parseUrlTemplate(XmlPullParser xmlPullParser, String str, UrlTemplate urlTemplate) {
        String attributeValue = xmlPullParser.getAttributeValue(null, str);
        return attributeValue != null ? UrlTemplate.compile(attributeValue) : urlTemplate;
    }

    protected UtcTimingElement parseUtcTiming(XmlPullParser xmlPullParser) {
        return buildUtcTimingElement(xmlPullParser.getAttributeValue(null, "schemeIdUri"), xmlPullParser.getAttributeValue(null, "value"));
    }
}
